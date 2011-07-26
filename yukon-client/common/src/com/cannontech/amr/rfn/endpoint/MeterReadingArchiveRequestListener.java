package com.cannontech.amr.rfn.endpoint;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveProcessingRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveResponse;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveStartupNotification;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.amr.rfn.model.RfnMeterPlusReadingData;
import com.cannontech.amr.rfn.service.RfnMeterLookupService;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.events.loggers.RfnMeteringEventLogService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.TransactionTemplateHelper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.base.Function;
import com.google.common.collect.ComputationException;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.ImmutableSet.Builder;

@ManagedResource
public class MeterReadingArchiveRequestListener {
    
    private static final Logger log = YukonLogManager.getLogger(MeterReadingArchiveRequestListener.class);
    
    private DynamicDataSource dynamicDataSource;
    private RfnMeterReadService rfnMeterReadService;
    private JmsTemplate jmsTemplate;
    private RfnMeterDao rfnMeterDao;
    private RfnMeterLookupService rfnMeterLookupService;
    private DeviceDao deviceDao;
    private DeviceCreationService deviceCreationService;
    private TransactionTemplate transactionTemplate;
    private ConfigurationSource configurationSource;
    private RfnMeteringEventLogService rfnMeteringEventLogService;
    private AsyncDynamicDataSource asyncDynamicDataSource;

    private String meterTemplatePrefix;
    private Map<RfnMeterIdentifier, RfnMeter> existingMeterLookup;
    private Map<String, Boolean> recentlyUncreatableTemplates;
    private ConcurrentHashMultiset<String> unknownTemplatesEncountered = ConcurrentHashMultiset.create();
    private ConcurrentHashMultiset<RfnMeterIdentifier> uncreatableDevices = ConcurrentHashMultiset.create();
    private Set<String> templatesToIgnore;

    private AtomicInteger meterLookupAttempt = new AtomicInteger();
    private AtomicInteger meterLookupCacheMiss = new AtomicInteger();
    private AtomicInteger meterLookupError = new AtomicInteger();
    private AtomicInteger newMeterCreated = new AtomicInteger();
    private AtomicInteger meterFoundOnCreate = new AtomicInteger();
    private AtomicInteger processedArchiveRequest = new AtomicInteger();
    private AtomicInteger archivedReadings = new AtomicInteger();
    
    @PostConstruct
    public void startup() {
        RfnMeterReadingArchiveStartupNotification response = new RfnMeterReadingArchiveStartupNotification();
        jmsTemplate.convertAndSend("yukon.notif.obj.amr.rfn.MeterReadingArchiveStartupNotification", response);
        meterTemplatePrefix = configurationSource.getString("RFN_METER_TEMPLATE_PREFIX", "*RfnTemplate_");
        
        Builder<String> ignoredTemplateBuilder = ImmutableSet.builder();
        String templatesToIgnoreConfigStr = configurationSource.getString("RFN_METER_TEMPLATES_TO_IGNORE", "");
        String[] ignoredTemplates = StringUtils.splitByWholeSeparator(templatesToIgnoreConfigStr, ",");
        for (String template : ignoredTemplates) {
            ignoredTemplateBuilder.add(template.trim());
        }
        templatesToIgnore = ignoredTemplateBuilder.build();
        
        existingMeterLookup = new MapMaker()
            .concurrencyLevel(4)
            .expiration(4, TimeUnit.MINUTES)
            .makeComputingMap(new Function<RfnMeterIdentifier, RfnMeter>() {
                @Override
                public RfnMeter apply(RfnMeterIdentifier meterIdentifier) {
                    meterLookupCacheMiss.incrementAndGet();
                    RfnMeter rfnMeter = rfnMeterLookupService.getMeter(meterIdentifier);
                    return rfnMeter;
                }
            });
        
        recentlyUncreatableTemplates = new MapMaker()
            .concurrencyLevel(10)
            .expiration(5, TimeUnit.SECONDS)
            .makeMap();
        
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
                    // no reason to be too delicate here
                    existingMeterLookup.clear();
                    recentlyUncreatableTemplates.clear();
                }
            }
        });
    }
    
    public void handleArchiveRequestMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof RfnMeterReadingArchiveRequest) {
                    RfnMeterReadingArchiveRequest archiveRequest = (RfnMeterReadingArchiveRequest) object;
                    handleArchiveRequest(archiveRequest);
                    message.acknowledge();
                }
            } catch (JMSException e) {
                log.warn("Unable to extract object from message", e);
            }
        }
    }

    /**
     * Process archive requests from Network Manager. This step only involves determining
     * what if any device is associated with the archive request. If a device is found,
     * the device and the original request are forwarded to the 
     * yukon.rr.obj.amr.rfn.MeterReadingArchiveProcessingRequest queue.
     * If the device is not found, the request is forwarded to the 
     * yukon.rr.obj.amr.rfn.MeterReadingArchiveRequest.create queue.
     * 
     * This may run on multiple threads and on multiple JVMs.
     */
    public void handleArchiveRequest(RfnMeterReadingArchiveRequest archiveRequest) {
        try {
            LogHelper.debug(log, "Received RfnMeterReadingArchiveRequest: %s", archiveRequest);

            RfnMeterIdentifier meterIdentifier = getMeterIdentifier(archiveRequest.getData());

            try {
                // a simple computing map is used as a cache here to handle
                // multiple requests for the same device in a short period of time
                meterLookupAttempt.incrementAndGet();
                RfnMeter rfnMeter = existingMeterLookup.get(meterIdentifier);
                queueForArchiveProcessing(rfnMeter, archiveRequest);
            } catch (ComputationException e) {
                meterLookupError.incrementAndGet();
                LogHelper.debug(log, "unable to find existing meter, send to create queue: %s", e.getCause().getMessage());
                
                jmsTemplate.convertAndSend("yukon.rr.obj.amr.rfn.MeterReadingArchiveRequest.create", archiveRequest);
            }
            

        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("caught exception in handleArchiveRequest", e);
            } else {
                log.warn("caught exception in handleArchiveRequest: " + e.toString());
            }
        }
    }

    private void queueForArchiveProcessing(RfnMeter rfnMeter,
            RfnMeterReadingArchiveRequest archiveRequest) {
        RfnMeterReadingArchiveProcessingRequest processingRequest = new RfnMeterReadingArchiveProcessingRequest();
        processingRequest.setOriginalRequest(archiveRequest);
        processingRequest.setMeter(rfnMeter);
        
        jmsTemplate.convertAndSend("yukon.rr.obj.amr.rfn.MeterReadingArchiveProcessingRequest", processingRequest);
    }
    
    /**
     * Process archive requests that have already been determined to be for a 
     * device that did not exist at the moment it was put on this queue.
     * 
     * This queue should be processed by a single thread in the entire system.
     */
    public void handleArchiveRequest_create(RfnMeterReadingArchiveRequest archiveRequest) {
        LogHelper.debug(log, "Received RfnMeterReadingArchiveRequest on create queue: %s", archiveRequest);
        
        RfnMeterIdentifier meterIdentifier = getMeterIdentifier(archiveRequest.getData());
        
        RfnMeter rfnMeter;
        try {
            try {
                rfnMeter = rfnMeterLookupService.getMeter(meterIdentifier);
                meterFoundOnCreate.incrementAndGet();
                LogHelper.debug(log, "Found matching meter on new meter lookup: %s", rfnMeter);
                queueForArchiveProcessing(rfnMeter, archiveRequest);
                return;
            } catch (NotFoundException e) {
                // fall through
            }
            rfnMeter = createMeter(meterIdentifier);
            newMeterCreated.incrementAndGet();
            LogHelper.debug(log, "Created new meter: %s", rfnMeter);
            queueForArchiveProcessing(rfnMeter, archiveRequest);
            return;

        } catch (IgnoredTemplateException e) {
            LogHelper.debug(log, "Unable to create meter for %s because template is ignored", meterIdentifier);
        } catch (Exception e) {
            LogHelper.debug(log, "Creation failed for %s: %s", meterIdentifier, e);
            if (log.isTraceEnabled()) {
                log.trace("stack dump of creation failure", e);
            }
        }
    }

    /**
     * Processes archive requests once an actual device (PaoIdentifier) has been determined.
     * See RfnMeterReadService for a description of this process.
     * 
     * This may run on multiple threads and on multiple JVMs.
     */
    public void handleArchiveProcessingRequest(RfnMeterReadingArchiveProcessingRequest processingRequest) {
        RfnMeterPlusReadingData meterPlusReadingData = new RfnMeterPlusReadingData(processingRequest.getMeter(), processingRequest.getOriginalRequest().getData());
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
        rfnMeterReadService.processMeterReadingDataMessage(meterPlusReadingData, messagesToSend);

        dynamicDataSource.putValues(messagesToSend);
        archivedReadings.addAndGet(messagesToSend.size());

        sendAcknowledgement(processingRequest.getOriginalRequest());
        processedArchiveRequest.incrementAndGet();
        LogHelper.debug(log, "%d PointDatas generated for RfnMeterReadingArchiveRequest", messagesToSend.size());
    }
    
    private RfnMeter createMeter(final RfnMeterIdentifier meterIdentifier) {
        RfnMeter result = TransactionTemplateHelper.execute(transactionTemplate, new Callable<RfnMeter>() {

            @Override
            public RfnMeter call() {
                String templateName = meterTemplatePrefix + meterIdentifier.getSensorManufacturer() + "_" + meterIdentifier.getSensorModel();
                if (templatesToIgnore.contains(templateName)) {
                    throw new IgnoredTemplateException();
                }
                if (recentlyUncreatableTemplates.containsKey(templateName)) {
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

    private RfnMeterIdentifier getMeterIdentifier(RfnMeterReadingData meterReadingDataNotification) {
        return new RfnMeterIdentifier(meterReadingDataNotification.getSensorSerialNumber(), meterReadingDataNotification.getSensorManufacturer(), meterReadingDataNotification.getSensorModel());
    }
    
    private void sendAcknowledgement(RfnMeterReadingArchiveRequest archiveRequest) {
        RfnMeterReadingArchiveResponse response = new RfnMeterReadingArchiveResponse();
        response.setDataPointId(archiveRequest.getDataPointId());
        response.setReadingType(archiveRequest.getReadingType());
        
        jmsTemplate.convertAndSend("yukon.rr.obj.amr.rfn.MeterReadingArchiveResponse", response);
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Autowired
    public void setRfnMeterReadService(RfnMeterReadService rfnMeterReadService) {
        this.rfnMeterReadService = rfnMeterReadService;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
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
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    @Autowired
    public void setRfnMeteringEventLogService(RfnMeteringEventLogService rfnMeteringEventLogService) {
        this.rfnMeteringEventLogService = rfnMeteringEventLogService;
    }
    
    @Autowired
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }
    
    @Autowired
    public void setRfnMeterLookupService(RfnMeterLookupService rfnMeterLookupService) {
        this.rfnMeterLookupService = rfnMeterLookupService;
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
    public int getMeterLookupCacheMiss() {
        return meterLookupCacheMiss.get();
    }
    
    @ManagedAttribute
    public int getMeterLookupError() {
        return meterLookupError.get();
    }
    
    @ManagedAttribute
    public int getNewMeterCreated() {
        return newMeterCreated.get();
    }
    
    @ManagedAttribute
    public int getMeterFoundOnCreate() {
        return meterFoundOnCreate.get();
    }
    
    @ManagedAttribute
    public int getProcessedArchiveRequest() {
        return processedArchiveRequest.get();
    }
    
    @ManagedAttribute
    public int getArchivedReadings() {
        return archivedReadings.get();
    }
}