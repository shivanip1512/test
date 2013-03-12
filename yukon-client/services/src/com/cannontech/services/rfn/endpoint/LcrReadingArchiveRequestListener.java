package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.ParseExiException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveResponse;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveResponse;
import com.cannontech.dr.rfn.service.ExiParsingService;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class LcrReadingArchiveRequestListener extends ArchiveRequestListenerBase<RfnLcrArchiveRequest> {
    
    @Autowired RfnLcrDataMappingService rfnLcrDataMappingService;
    @Autowired ExiParsingService exiParsingService;
    @Autowired PaoDao paoDao;
    @Autowired PointDao pointDao;
    @Autowired EnrollmentDao enrollmentService;
    @Autowired InventoryDao inventoryDao;
    @Autowired YukonEnergyCompanyService yecService;
    @Autowired EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired LmHardwareCommandService commandService;
    @Autowired InventoryBaseDao inventoryBaseDao;
    
    private static final Logger log = YukonLogManager.getLogger(LcrReadingArchiveRequestListener.class);
    private static final String archiveResponseQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveResponse";
    
    private List<Worker> workers;
    private final AtomicInteger archivedReadings = new AtomicInteger();
    
    public class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, RfnLcrArchiveRequest archiveRequest) {
            
            if (archiveRequest instanceof RfnLcrReadingArchiveRequest) {
                RfnLcrReadingArchiveRequest readingArchiveRequest = ((RfnLcrReadingArchiveRequest) archiveRequest);
                SimpleXPathTemplate decodedPayload = null;

                byte[] payload = readingArchiveRequest.getData().getPayload();
                try {
                    decodedPayload = exiParsingService.parseRfLcrReading(payload);
                } catch (ParseExiException e) {
                    log.error("Can't parse incoming RF LCR payload data.  Payload may be corrupt or not schema compliant.", e);
                    throw new RuntimeException("Error parsing RF LCR payload.", e);
                }
                
                /** Handle point data */
                List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
                messagesToSend = rfnLcrDataMappingService.mapPointData(readingArchiveRequest, decodedPayload);
                dynamicDataSource.putValues(messagesToSend);
                archivedReadings.addAndGet(messagesToSend.size());
                LogHelper.debug(log, "%d PointDatas generated for RfnLcrReadingArchiveRequest", messagesToSend.size());
                
                /** Handle addressing data */
                rfnLcrDataMappingService.storeAddressingData(jmsTemplate, decodedPayload, rfnDevice);
    
                incrementProcessedArchiveRequest();
                
            } else {
                
                /** Just an lcr archive request, these happen when devices join the network */
                InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(rfnDevice.getPaoIdentifier().getPaoId());
                int inventoryId = inventory.getInventoryId();
                List<ProgramEnrollment> activeEnrollments = enrollmentService.getActiveEnrollmentsByInventory(inventoryId);
                if (!activeEnrollments.isEmpty()) {
                    /** Send config if auto-config is enabled */
                    YukonEnergyCompany yec = yecService.getEnergyCompanyByInventoryId(inventoryId);
                    boolean autoConfig = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.AUTOMATIC_CONFIGURATION, yec.getEnergyCompanyId());
                    if (autoConfig) {
                        LiteLmHardwareBase lmhb = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
                        LmHardwareCommand lmhc = new LmHardwareCommand();
                        lmhc.setDevice(lmhb);
                        lmhc.setType(LmHardwareCommandType.CONFIG);
                        lmhc.setUser(yec.getEnergyCompanyUser());
                        
                        try {
                            commandService.sendConfigCommand(lmhc);
                            log.debug("Sent config command for RfnLcrArchiveRequest");
                        } catch (CommandCompletionException e) {
                            log.error("Unable to send config command for RfnLcrArchiveRequest", e);
                        }
                    }
                }
            }
            
            sendAcknowledgement(archiveRequest);
        }
    }
    
    @Override
    @PostConstruct
    public void init() {
     // setup as many workers as requested
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
        // should handle listener container here as well
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
            return response;
        } else {
            RfnLcrArchiveResponse response = new RfnLcrArchiveResponse();
            response.setSensorId(archiveRequest.getSensorId());
            return response;
        }
    }

    @Override
    protected String getRfnArchiveResponseQueueName() {
        return archiveResponseQueueName;
    }

    @ManagedAttribute
    public int getArchivedReadings() {
        return archivedReadings.get();
    }
    
}