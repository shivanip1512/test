package com.cannontech.dr.itron.service.impl;

import java.util.List;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.service.ItronDataReadService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ItronDataReadServiceImpl implements ItronDataReadService{

    private static final Logger log = YukonLogManager.getLogger(ItronDataReadServiceImpl.class);

    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private ItronDeviceDataParser itronDeviceDataParser;
    @Autowired private ItronCommunicationService itronCommunicationService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    
    public static int maxRows = 1000;
    
    @Override
    public void collectData() {
        try {
            while (true) {
                long startRecordId = getStartId();
                ZipFile zip = itronCommunicationService.exportDeviceLogs(startRecordId, getEndId(startRecordId));
                if (zip == null) {
                    break;
                }
                itronDeviceDataParser.parseAndSend(zip, null);
            }
        } catch (Exception e) {
            log.error("Exception while collecting data", e);
        }
    }
    
    @Override
    public void collectDataForRead(int deviceId) {
        ZipFile zip = itronCommunicationService.exportDeviceLogsForItronGroup(getStartId(), null, Lists.newArrayList(deviceId));
        itronDeviceDataParser.parseAndSend(zip, null);
    }

    @Override
    public  Multimap<PaoIdentifier, PointValueHolder> collectDataForRead(List<Integer> deviceIds) {
        Multimap<PaoIdentifier, PointValueHolder> pointValues = HashMultimap.create();
        while (true) {
            long startRecordId = getStartId();
            ZipFile zip = itronCommunicationService.exportDeviceLogsForItronGroup(startRecordId,
                getEndId(startRecordId), deviceIds);
            if (zip == null) {
                break;
            }
            itronDeviceDataParser.parseAndSend(zip, pointValues);
        }
      
        return pointValues;
    }
    
    private long getEndId(long startRecordId) {
        return startRecordId + maxRows;
    }

    private long getStartId() {
        long startRecordId = persistedSystemValueDao.getLongValue(PersistedSystemValueKey.ITRON_DATA_LAST_RECORD_ID) + 1;
        return startRecordId;
    }
}
