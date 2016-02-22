package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.ParseExiException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveResponse;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveResponse;
import com.cannontech.dr.rfn.service.ExiParsingService;
import com.cannontech.dr.rfn.service.ExiParsingService.Schema;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class LcrReadingArchiveRequestListener extends ArchiveRequestListenerBase<RfnLcrArchiveRequest> {
    
    @Autowired private ConfigurationSource configSource;
    @Autowired private DispatchClientConnection dispatch;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private EnrollmentDao enrollmentService;
    @Autowired private ExiParsingService exiParsingService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareCommandService commandService;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private RfnLcrDataMappingService rfnLcrDataMappingService;
    @Autowired private RfnPerformanceVerificationService rfnPerformanceVerificationService;
    
    private static final Logger log = YukonLogManager.getLogger(LcrReadingArchiveRequestListener.class);
    private static final Logger rfnCommsLog = YukonLogManager.getRfnCommsLogger();
    
    private static final String archiveResponseQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveResponse";
    private List<Worker> workers;
    private final AtomicInteger archivedReadings = new AtomicInteger();
    private final AtomicInteger numPausedQueues = new AtomicInteger();

    public class Worker extends ConverterBase {
        
        public Worker(int workerNumber, int queueSize) {
            super("LcrReadingArchive", workerNumber, queueSize);
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, RfnLcrArchiveRequest request) {
            
            Instant startTime = new Instant();
            
            // Make sure dispatch message handling isn't blocked up.
            boolean pausingEnabled = configSource.getBoolean(MasterConfigBoolean.PAUSE_FOR_DISPATCH_MESSAGE_BACKUP, true);
            if (pausingEnabled && dispatch.isBehind()) {
                
                numPausedQueues.incrementAndGet();
                log.warn("dispatch message handling is behind...sleeping");
                
                int msSlept = 0;
                while (dispatch.isBehind()) {
                    try {
                        Thread.sleep(250);
                        msSlept += 250;
                    } catch (InterruptedException e) {
                        log.warn("Interrupted waiting for dispatch message handling to catch up.");
                    }
                }
                log.warn("slept for " + msSlept + "ms waiting for dispatch message handling to catch up.");
                numPausedQueues.decrementAndGet();
            }
            
            if (request instanceof RfnLcrReadingArchiveRequest) {
                
                RfnLcrReadingArchiveRequest reading = ((RfnLcrReadingArchiveRequest) request);
                SimpleXPathTemplate decodedPayload = null;
                
                byte[] payload = reading.getData().getPayload();
                try {
                    if (log.isTraceEnabled()) {
                        log.trace(rfnDevice + " payload: 0x" + DatatypeConverter.printHexBinary(payload));
                    } else if (log.isDebugEnabled()) {
                        log.debug("decoding: " + rfnDevice);
                    }
                    decodedPayload = exiParsingService.parseRfLcrReading(rfnDevice.getRfnIdentifier(), payload);
                } catch (ParseExiException e) {
                    // Acknowledge the request to prevent NM from sending back that data which can't be parsed.
                    sendAcknowledgement(request);
                    log.error(
                        "Can't parse incoming RF LCR payload data for " + rfnDevice + 
                        ". Payload: 0x" + DatatypeConverter.printHexBinary(payload) + 
                        ". Payload may be corrupt or not schema compliant.");
                    throw new RuntimeException("Error parsing RF LCR payload.", e);
                }
                
                // Handle broadcast verification messages
                Schema schema = exiParsingService.getSchema(payload);
                if (schema.supportsBroadcastVerificationMessages()) {
                    // Verification Messages should be processed even if there is no connection to dispatch.
                    Map<Long, Instant> verificationMsgs =
                        rfnLcrDataMappingService.mapBroadcastVerificationMessages(decodedPayload);
                    Range<Instant> range =
                        rfnLcrDataMappingService.mapBroadcastVerificationUnsuccessRange(decodedPayload, rfnDevice);
                    rfnPerformanceVerificationService.processVerificationMessages(rfnDevice, verificationMsgs, range);
                }
                
                // Discard all the data before 1/1/2001
                if (rfnLcrDataMappingService.isValidTimeOfReading(decodedPayload)) {
                    // Handle point data
                    List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
                    messagesToSend = rfnLcrDataMappingService.mapPointData(reading, decodedPayload);
                    asyncDynamicDataSource.putValues(messagesToSend);
                    archivedReadings.addAndGet(messagesToSend.size());
                    if (log.isDebugEnabled()) {
                        log.debug(messagesToSend.size() + " PointDatas generated for RfnLcrReadingArchiveRequest");
                    }
                    
                    // Handle addressing data
                    rfnLcrDataMappingService.storeAddressingData(jmsTemplate, decodedPayload, rfnDevice);
                }
                incrementProcessedArchiveRequest();
                
            } else {
                // Just an LCR archive request, these happen when devices join the network
                InventoryIdentifier inventory =
                    inventoryDao.getYukonInventoryForDeviceId(rfnDevice.getPaoIdentifier().getPaoId());
                int inventoryId = inventory.getInventoryId();
                List<ProgramEnrollment> activeEnrollments =
                    enrollmentService.getActiveEnrollmentsByInventory(inventoryId);
                
                if (!activeEnrollments.isEmpty()) {
                    // Send config if auto-config is enabled
                    EnergyCompany ec = ecDao.getEnergyCompanyByInventoryId(inventoryId);
                    boolean autoConfig =
                        ecSettingDao.getBoolean(EnergyCompanySettingType.AUTOMATIC_CONFIGURATION, ec.getId());
                    if (autoConfig) {
                        LiteLmHardwareBase lmhb = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
                        LmHardwareCommand lmhc = new LmHardwareCommand();
                        lmhc.setDevice(lmhb);
                        lmhc.setType(LmHardwareCommandType.CONFIG);
                        lmhc.setUser(ec.getUser());
                        
                        try {
                            commandService.sendConfigCommand(lmhc);
                            log.debug("Sent config command for RfnLcrArchiveRequest");
                        } catch (CommandCompletionException e) {
                            log.error("Unable to send config command for RfnLcrArchiveRequest", e);
                        }
                    }
                }
            }
            
            sendAcknowledgement(request);
            if (log.isDebugEnabled()) {
                Duration processingDuration = new Duration(startTime, new Instant());
                log.debug("It took " + processingDuration + " to process a request");
            }
        }
    }
    
    @Override
    @PostConstruct
    public void init() {
        // Setup as many workers as requested
        ImmutableList.Builder<Worker> workerBuilder = ImmutableList.builder();
        int workerCount = getWorkerCount();
        int queueSize = getQueueSize();
        for (int i = 0; i < workerCount; ++i) {
            Worker worker = new Worker(i, queueSize);
            workerBuilder.add(worker);
            worker.start();
        }
        workers = workerBuilder.build();
    }
    
    @PreDestroy
    @Override
    protected void shutdown() {
        // Should handle listener container here as well
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }
    
    @Override
    protected List<Worker> getConverters() {
        return workers;
    }
    
    @Override
    protected Object getRfnArchiveResponse(RfnLcrArchiveRequest archiveRequest) {
        
        if (archiveRequest instanceof RfnLcrReadingArchiveRequest) {
            RfnLcrReadingArchiveRequest readRequest = (RfnLcrReadingArchiveRequest) archiveRequest;
            RfnLcrReadingArchiveResponse response = new RfnLcrReadingArchiveResponse();
            response.setDataPointId(readRequest.getDataPointId());
            response.setType(readRequest.getType());
            return response;
        }
        
        RfnLcrArchiveResponse response = new RfnLcrArchiveResponse();
        response.setSensorId(archiveRequest.getSensorId());
        return response;
    }
    
    @Override
    protected String getRfnArchiveResponseQueueName() {
        return archiveResponseQueueName;
    }
    
    @ManagedAttribute
    public int getArchivedReadings() {
        return archivedReadings.get();
    }
    
    @ManagedAttribute
    public int getNumPausedQueues() {
        return numPausedQueues.get();
    }
    
}