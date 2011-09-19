package com.cannontech.amr.rfn.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
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
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

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
    private Map<String, Boolean> recentlyUncreatableTemplates;
    private ConcurrentHashMultiset<String> unknownTemplatesEncountered = ConcurrentHashMultiset.create();
    private ConcurrentHashMultiset<RfnMeterIdentifier> uncreatableDevices = ConcurrentHashMultiset.create();
    private Set<String> templatesToIgnore;
    private List<Worker> workers;

    private AtomicInteger meterLookupAttempt = new AtomicInteger();
    private AtomicInteger newMeterCreated = new AtomicInteger();
    private AtomicInteger processedArchiveRequest = new AtomicInteger();
    private AtomicInteger archivedReadings = new AtomicInteger();
    
    private class Worker extends Thread {
        private ArrayBlockingQueue<RfnMeterReadingArchiveRequest> inQueue;
        private volatile boolean shutdown = false;
        
        public Worker(int workerNumber, int queueSize) {
            super("RfnProcessor-" + workerNumber);
            inQueue = new ArrayBlockingQueue<RfnMeterReadingArchiveRequest>(queueSize);
        }

        public void queue(RfnMeterReadingArchiveRequest request) throws InterruptedException {
            if (shutdown) {
                throw new IllegalStateException("worker has already shutdown");
            }
            inQueue.put(request);
            if (shutdown) {
                // oops, that was awkward timing
                drainQueue();
            }
        }
        
        private void drainQueue() {
            // this method is called from multiple places, so it can't really do much
            inQueue.clear();
        }

        @Override
        public void run() {
            
            while (true) {
                try {
                    RfnMeterReadingArchiveRequest request = inQueue.take();
                    processInitial(request);
                    log.debug("Proccessed Archive Request on " + this.getName() + ", queue size is: " + inQueue.size());
                } catch (InterruptedException e) {
                    log.warn("received shutdown signal, queue size: " + inQueue.size());
                    break;
                } catch (Exception e) {
                    // consider using more "named" exceptions throughout this code
                    log.warn("Unknown exception while processing request", e);
                }
            }
        }

        private void processInitial(RfnMeterReadingArchiveRequest archiveRequest) {
            RfnMeterIdentifier meterIdentifier = getMeterIdentifier(archiveRequest.getData());
            RfnMeter rfnMeter;
            try {
                meterLookupAttempt.incrementAndGet();
                rfnMeter = rfnMeterLookupService.getMeter(meterIdentifier);
            } catch (NotFoundException e1) {
                // looks like we need to create the meter
                rfnMeter = processCreation(archiveRequest, meterIdentifier);
            }
            
            processPointDatas(rfnMeter, archiveRequest);
        }

        private RfnMeter processCreation(RfnMeterReadingArchiveRequest archiveRequest,
                                     RfnMeterIdentifier meterIdentifier) {
            try {
                RfnMeter rfnMeter = createMeter(meterIdentifier);
                newMeterCreated.incrementAndGet();
                LogHelper.debug(log, "Created new meter: %s", rfnMeter);
                return rfnMeter;
            } catch (IgnoredTemplateException e) {
                throw new RuntimeException("Unable to create meter for " + meterIdentifier + " because template is ignored", e);
            } catch (Exception e) {
                LogHelper.debug(log, "Creation failed for %s: %s", meterIdentifier, e);
                throw new RuntimeException("Creation failed for " + meterIdentifier, e);
            }
        }
        
        public void processPointDatas(RfnMeter rfnMeter, RfnMeterReadingArchiveRequest archiveRequest) {
            RfnMeterPlusReadingData meterPlusReadingData = new RfnMeterPlusReadingData(rfnMeter, archiveRequest.getData());
            List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
            rfnMeterReadService.processMeterReadingDataMessage(meterPlusReadingData, messagesToSend);

            dynamicDataSource.putValues(messagesToSend);
            archivedReadings.addAndGet(messagesToSend.size());

            sendAcknowledgement(archiveRequest);
            processedArchiveRequest.incrementAndGet();
            LogHelper.debug(log, "%d PointDatas generated for RfnMeterReadingArchiveRequest", messagesToSend.size());
        }

        public void shutdown() {
            // shutdown mechanism assumes that 
            shutdown  = true;
            this.interrupt();
            drainQueue();
        }
    }
    
    @PostConstruct
    public void startup() {
        RfnMeterReadingArchiveStartupNotification response = new RfnMeterReadingArchiveStartupNotification();
        // setup as many workers as requested
        int workerCount = configurationSource.getInteger("RFN_METER_DATA_WORKER_COUNT", 5);
        int queueSize = configurationSource.getInteger("RFN_METER_DATA_WORKER_QUEUE_SIZE", 500);
        ImmutableList.Builder<Worker> workerBuilder = ImmutableList.builder();
        for (int i = 0; i < workerCount; ++i) {
            Worker worker = new Worker(i, queueSize);
            workerBuilder.add(worker);
            worker.start();
        }
        workers = workerBuilder.build();
        
        
        jmsTemplate.convertAndSend("yukon.notif.obj.amr.rfn.MeterReadingArchiveStartupNotification", response);
        meterTemplatePrefix = configurationSource.getString("RFN_METER_TEMPLATE_PREFIX", "*RfnTemplate_");
        
        ImmutableSet.Builder<String> ignoredTemplateBuilder = ImmutableSet.builder();
        String templatesToIgnoreConfigStr = configurationSource.getString("RFN_METER_TEMPLATES_TO_IGNORE", "");
        String[] ignoredTemplates = StringUtils.splitByWholeSeparator(templatesToIgnoreConfigStr, ",");
        for (String template : ignoredTemplates) {
            ignoredTemplateBuilder.add(template.trim());
        }
        templatesToIgnore = ignoredTemplateBuilder.build();
        
        recentlyUncreatableTemplates = new MapMaker()
            .concurrencyLevel(10)
            .expiration(5, TimeUnit.SECONDS)
            .makeMap();
        
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {
                    // no reason to be too delicate here
                    recentlyUncreatableTemplates.clear();
                }
            }
        });
    }
    
    @PreDestroy
    public void shutdown() {
        // should handle listener container here as well
        
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }
    
    public void handleArchiveRequest(RfnMeterReadingArchiveRequest archiveRequest) {
        // determine which worker will handle the request by hashing the serial number
        int hashCode = archiveRequest.getData().getSensorSerialNumber().hashCode();
        int workerIndex = hashCode % workers.size();
        Worker worker = workers.get(workerIndex);
        try {
            worker.queue(archiveRequest);
        } catch (InterruptedException e) {
            log.warn("interrupted while queuing archive request", e);
            Thread.currentThread().interrupt();
        }
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
    public int getNewMeterCreated() {
        return newMeterCreated.get();
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