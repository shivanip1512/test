package com.cannontech.dr.itron.service.impl;

import java.util.List;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.service.ItronDataReadService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ItronDataReadServiceImpl implements ItronDataReadService{
    private static final Logger log = YukonLogManager.getLogger(ItronDataReadServiceImpl.class);
    
    /* 
     * A special Itron group name, used for on-demand reads. Synchronize on this group when using it. If assignment to 
     * the group and use of the group are interleaved, it may result in the wrong device data being updated.
     */
    private static final String onDemandReadGroup = "ITRON_READ_GROUP";
    
    // Autowired
    private ItronDeviceDataParser itronDeviceDataParser;
    private ItronCommunicationService communicationService;
    private PersistedSystemValueDao persistedSystemValueDao;
    
    // Master.cfg settings
    private final int recordsPerRead;
    private final boolean updateLogsBeforeExport;
    
    @Autowired
    public ItronDataReadServiceImpl(ConfigurationSource configurationSource,
            ItronDeviceDataParser itronDeviceDataParser,
            ItronCommunicationService communicationService,
            PersistedSystemValueDao persistedSystemValueDao) {
        
        this.itronDeviceDataParser = itronDeviceDataParser;
        this.communicationService = communicationService;
        this.persistedSystemValueDao = persistedSystemValueDao;
        
        recordsPerRead = configurationSource.getInteger(MasterConfigInteger.ITRON_RECORD_IDS_PER_READ, 5000);
        updateLogsBeforeExport = configurationSource.getBoolean(MasterConfigBoolean.ITRON_UPDATE_DEVICE_LOGS_BEFORE_EXPORT);
    }
    
    @Override
    public void collectData() {
        try {
            if (updateLogsBeforeExport) {
                communicationService.updateDeviceLogsForAllGroups();
            }
            while (true) {
                Range<Long> range = getRecordRange();
                log.debug("Exporting Itron data for range: {} - {}", range.getMin(), range.getMax());
                ZipFile zip = communicationService.exportDeviceLogs(range.getMin(), range.getMax());
                if (zip == null) {
                    log.debug("Data zip file is null. File parsing will be skipped.");
                    break;
                }
                try {
                    itronDeviceDataParser.parseAndSend(zip, true);
                } catch (EmptyImportFileException e) {
                    log.debug(e);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Exception while collecting data", e);
        }
    }
    
    @Override
    public void collectDataForRead(int deviceId) {
        Range<Long> range = getRecordRange();
        List<Integer> deviceIds = List.of(deviceId);
        
        synchronized(onDemandReadGroup) {
            try {
                long onDemandReadGroupId = communicationService.createOrUpdateItronGroup(onDemandReadGroup, deviceIds);
                communicationService.updateDeviceLogsForItronGroup(onDemandReadGroupId);
                ZipFile zip = communicationService.exportDeviceLogsForItronGroup(range.getMin(), null, onDemandReadGroupId);
                itronDeviceDataParser.parseAndSend(zip, false);
            } catch (EmptyImportFileException e) {
                log.info("On-demand Itron read succeeded, but no new data was available.");
            }
        }
    }

    @Override
    public Multimap<PaoIdentifier, PointValueHolder> collectDataForRead(List<Integer> deviceIds) {
        Multimap<PaoIdentifier, PointValueHolder> pointValues = HashMultimap.create();
        Range<Long> range = getRecordRange();
        long startRecordId = range.getMin();
        long endRecordId = range.getMax();
        
        synchronized(onDemandReadGroup) {
            long onDemandReadGroupId = communicationService.createOrUpdateItronGroup(onDemandReadGroup, deviceIds);
            communicationService.updateDeviceLogsForItronGroup(onDemandReadGroupId);
            
            while (true) {
                ZipFile zip = communicationService.exportDeviceLogsForItronGroup(startRecordId, endRecordId, onDemandReadGroupId);
                if (zip == null) {
                    break;
                }
                try {
                    pointValues.putAll(itronDeviceDataParser.parseAndSend(zip, false));
                } catch (EmptyImportFileException e) {
                    log.info(e);
                    break;
                }
                startRecordId = endRecordId +1;
                endRecordId = startRecordId + recordsPerRead;
            }
        }
        
        return pointValues;
    }
    
    /**
     * Returns range of record ids to export
     */
    private Range<Long> getRecordRange() {
        long startRecordId = persistedSystemValueDao.getLongValue(PersistedSystemValueKey.ITRON_DATA_LAST_RECORD_ID) + 1;
        long endRecordId = startRecordId + recordsPerRead;
        return new Range<>(startRecordId, true, endRecordId, true);
    }
}
