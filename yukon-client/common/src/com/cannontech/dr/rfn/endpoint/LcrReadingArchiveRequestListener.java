package com.cannontech.dr.rfn.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.rfn.endpoint.RfnArchiveRequestListenerBase;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
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
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand.Builder;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class LcrReadingArchiveRequestListener extends RfnArchiveRequestListenerBase<RfnLcrArchiveRequest> {
    
    @Autowired ResourceLoader loader;
    @Autowired RfnLcrDataMappingService rfnLcrDataMappingService;
    @Autowired ExiParsingService exiParsingService;
    @Autowired PaoDao paoDao;
    @Autowired PointDao pointDao;
    @Autowired EnrollmentDao enrollmentService;
    @Autowired InventoryDao inventoryDao;
    @Autowired EnergyCompanyRolePropertyDao ecRolePropertyDao;
    @Autowired YukonEnergyCompanyService yecService;
    @Autowired LmHardwareCommandService commandService;
    @Autowired InventoryBaseDao inventoryBaseDao;
    
    private static final Logger log = YukonLogManager.getLogger(LcrReadingArchiveRequestListener.class);
    private static final String archiveResponseQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveResponse";
    private static final String informingSchemaLocation = "classpath:com/cannontech/dr/rfn/endpoint/rfnLcrExiMessageSchema.xsd";
    
    private List<Worker> workers;
    private AtomicInteger archivedReadings = new AtomicInteger();
    
    public class Worker extends WorkerBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, RfnLcrArchiveRequest archiveRequest) {
            if (archiveRequest instanceof RfnLcrReadingArchiveRequest) {
                RfnLcrReadingArchiveRequest readingArchiveRequest = ((RfnLcrReadingArchiveRequest) archiveRequest);
                InputStream informingSchema = null;
                SimpleXPathTemplate decodedPayload = null;
                List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);

                byte[] payload = readingArchiveRequest.getData().getPayload();
                Resource informingSchemaResource = loader.getResource(informingSchemaLocation);
                try {
                    informingSchema = informingSchemaResource.getInputStream();
                    decodedPayload = exiParsingService.decodeExiMessage(payload, informingSchema);
                } catch (IOException e1) {
                    log.error("Unable to open informing schema as an input stream: " + informingSchemaLocation, e1);
                    throw new RuntimeException();
                } catch (ParseException e) {
                    log.error("Unable to parse EXI message payload for device: " + rfnDevice.getName(), e);
                    throw new RuntimeException();
                }
                
                messagesToSend = rfnLcrDataMappingService.mapPointData(readingArchiveRequest, decodedPayload);
                
                dynamicDataSource.putValues(messagesToSend);
                archivedReadings.addAndGet(messagesToSend.size());
    
                incrementProcessedArchiveRequest();
                LogHelper.debug(log, "%d PointDatas generated for RfnLcrReadingArchiveRequest", messagesToSend.size());
            } else {
                /** Just an lcr archive request, these happen when devices join the network */
                InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(rfnDevice.getPaoIdentifier().getPaoId());
                int inventoryId = inventory.getInventoryId();
                List<ProgramEnrollment> activeEnrollments = enrollmentService.getActiveEnrollmentsByInventory(inventoryId);
                if (!activeEnrollments.isEmpty()) {
                    /** Send config if auto-config is enabled */
                    YukonEnergyCompany yec = yecService.getEnergyCompanyByInventoryId(inventoryId);
                    boolean autoConfig = ecRolePropertyDao.checkProperty(YukonRoleProperty.AUTOMATIC_CONFIGURATION, yec);
                    if (autoConfig) {
                        LiteLmHardwareBase lmhb = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
                        LmHardwareCommand.Builder b = new Builder(lmhb, LmHardwareCommandType.CONFIG, yec.getEnergyCompanyUser());
                        try {
                            commandService.sendConfigCommand(b.build());
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
    protected List<Worker> getWorkers() {
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