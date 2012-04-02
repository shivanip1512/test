package com.cannontech.amr.rfn.service;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.amr.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.events.loggers.RfnMeteringEventLogService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.TransactionTemplateHelper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableSet;

public class RfnArchiveRequestService {
    private static final Logger log = YukonLogManager.getLogger(RfnArchiveRequestService.class);

    // @Autowired resources
    private ConfigurationSource configurationSource;
    private DeviceCreationService deviceCreationService;
    private DeviceDao deviceDao;
    private RfnMeterDao rfnMeterDao;
    private TransactionTemplate transactionTemplate;
    private RfnMeteringEventLogService rfnMeteringEventLogService;
    private AsyncDynamicDataSource asyncDynamicDataSource;

    private String meterTemplatePrefix;
    private Cache<String, Boolean> recentlyUncreatableTemplates;
    private ConcurrentHashMultiset<String> unknownTemplatesEncountered = ConcurrentHashMultiset.create();
    private ConcurrentHashMultiset<RfnMeterIdentifier> uncreatableDevices = ConcurrentHashMultiset.create();
    private Set<String> templatesToIgnore;
    private int workerCount;
    private int queueSize;
    private AtomicInteger meterLookupAttempt = new AtomicInteger();
    private AtomicInteger newMeterCreated = new AtomicInteger();
    private AtomicInteger processedArchiveRequest = new AtomicInteger();

    @PostConstruct
    public void init() {
        ImmutableSet.Builder<String> ignoredTemplateBuilder = ImmutableSet.builder();
        String templatesToIgnoreConfigStr = configurationSource.getString("RFN_METER_TEMPLATES_TO_IGNORE", "");
        String[] ignoredTemplates = StringUtils.splitByWholeSeparator(templatesToIgnoreConfigStr, ",");
        for (String template : ignoredTemplates) {
            ignoredTemplateBuilder.add(template.trim());
        }
        templatesToIgnore = ignoredTemplateBuilder.build();
        
        recentlyUncreatableTemplates = CacheBuilder.newBuilder().concurrencyLevel(10).expireAfterWrite(5, TimeUnit.SECONDS).build();
    
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
                    // no reason to be too delicate here
                    recentlyUncreatableTemplates.invalidateAll();
                }
            }
        });
        
        workerCount = configurationSource.getInteger("RFN_METER_DATA_WORKER_COUNT", 5);
        queueSize = configurationSource.getInteger("RFN_METER_DATA_WORKER_QUEUE_SIZE", 500);
        
        meterTemplatePrefix = configurationSource.getString("RFN_METER_TEMPLATE_PREFIX", "*RfnTemplate_");
    }
    
    public RfnMeter createMeter(final RfnMeterIdentifier meterIdentifier) {
        RfnMeter result = TransactionTemplateHelper.execute(transactionTemplate, new Callable<RfnMeter>() {

            @Override
            public RfnMeter call() {
                String templateName = meterTemplatePrefix + meterIdentifier.getSensorManufacturer() + "_" + meterIdentifier.getSensorModel();
                if (templatesToIgnore.contains(templateName)) {
                    throw new IgnoredTemplateException();
                }
                
                if (recentlyUncreatableTemplates.asMap().containsKey(templateName)) {
                    // we already tried to create this template within the last few seconds and failed
                    unknownTemplatesEncountered.add(templateName, 1);
                    uncreatableDevices.add(meterIdentifier);
                    throw new BadTemplateDeviceCreationException(templateName);
                }
                try {
                    String deviceName = meterIdentifier.getSensorSerialNumber().trim();
                    YukonDevice newDevice = deviceCreationService.createDeviceByTemplate(templateName, deviceName, true);
                    RfnMeter meter = new RfnMeter(newDevice.getPaoIdentifier(), meterIdentifier);
                    rfnMeterDao.updateMeter(meter);
                    deviceDao.changeMeterNumber(meter, deviceName);
                    rfnMeteringEventLogService.createdNewMeterAutomatically(meter.getPaoIdentifier().getPaoId(), meter.getMeterIdentifier().getCombinedIdentifier(), templateName, deviceName);
                    return meter;
                } catch (BadTemplateDeviceCreationException e) {
                    recentlyUncreatableTemplates.put(templateName, Boolean.TRUE);
                    uncreatableDevices.add(meterIdentifier, 1);
                    int oldCount = unknownTemplatesEncountered.add(templateName, 1);
                    if (oldCount == 0) {
                        // we may log this multiple times if the server is restarted, but this if statement
                        // seems to be a good idea to prevent excess 
                        rfnMeteringEventLogService.receivedDataForUnkownMeterTemplate(templateName);
                        log.warn("Unable to create meter with template for " + meterIdentifier, e);
                    }
                    throw e;
                } catch (DeviceCreationException e) {
                    int oldCount = uncreatableDevices.add(meterIdentifier, 1);
                    if (oldCount == 0) {
                        // we may log this multiple times if the server is restarted, but this if statement
                        // seems to be a good idea to prevent excess 
                        rfnMeteringEventLogService.unableToCreateMeterFromTemplate(templateName, meterIdentifier.getSensorManufacturer(), meterIdentifier.getSensorModel(), meterIdentifier.getSensorSerialNumber());
                        log.warn("Unable to create meter for " + meterIdentifier, e);
                    }
                    throw e;
                }
            }

        });
        return result;
    }
    
    public void incrementProcessedArchiveRequest() {
        processedArchiveRequest.incrementAndGet();
    }
    
    public void incrementMeterLookupAttempt() {
        meterLookupAttempt.incrementAndGet();
    }
    
    public void incrementNewMeterCreated() {
        newMeterCreated.incrementAndGet();
    }
    
    @ManagedAttribute
    public int getWorkerCount() {
        return workerCount;
    }
    
    @ManagedAttribute
    public int getQueueSize() {
        return queueSize;
    }
    
    @ManagedAttribute
    public String getUnkownTempaltes() {
        return unknownTemplatesEncountered.entrySet().toString();
    }
    
    @ManagedAttribute
    public int getMeterLookupAttempt() {
        return meterLookupAttempt.get();
    }
    
    @ManagedAttribute
    public int getNewMeterCreated() {
        return newMeterCreated.get();
    }
    
    @ManagedAttribute
    public int getProcessedArchiveRequest() {
        return processedArchiveRequest.get();
    }
    
    @Autowired
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }
    
    @Autowired
    public void setDeviceCreationService(DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setRfnMeterDao(RfnMeterDao rfnMeterDao) {
        this.rfnMeterDao = rfnMeterDao;
    }
    
    @Autowired
    public void setRfnMeteringEventLogService(RfnMeteringEventLogService rfnMeteringEventLogService) {
        this.rfnMeteringEventLogService = rfnMeteringEventLogService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
