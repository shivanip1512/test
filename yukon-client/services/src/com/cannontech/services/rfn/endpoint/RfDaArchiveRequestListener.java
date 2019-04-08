package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.da.rfn.message.archive.RfDaArchiveRequest;
import com.cannontech.da.rfn.message.archive.RfDaArchiveResponse;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class RfDaArchiveRequestListener extends ArchiveRequestListenerBase<RfDaArchiveRequest> {
    private static final Logger log = YukonLogManager.getLogger(RfDaArchiveRequestListener.class);
    
    @Autowired protected RfDaCreationService rfdaCreationService;
    
    private static final String archiveResponseQueueName = "yukon.qr.obj.da.rfn.RfDaArchiveResponse";
    
    private List<Worker> workers;
    private final AtomicInteger archivedReadings = new AtomicInteger();
    
    public class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super("RfDaArchive", workerNumber, queueSize);
        }
        
        @Override
        protected RfnDevice processCreation(RfDaArchiveRequest archiveRequest, RfnIdentifier identifier) {
            try {
                RfnDevice device = rfdaCreationService.create(identifier);
                rfdaCreationService.incrementNewDeviceCreated();
                log.debug("Created new device: " + device);
                sendAcknowledgement(archiveRequest);
                return device;
            } catch (Exception e) {
                log.warn("Creation failed for " + identifier, e);
                throw new RuntimeException("Creation failed for " + identifier, e);
            }
        }
        
        @Override
        public Optional<String> processData(RfnDevice rfnDevice, RfDaArchiveRequest archiveRequest) {
            //  no data to archive on this queue, just device creation requests that have no other payload
            return Optional.empty();  //  no point data to track
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
    protected Object getRfnArchiveResponse(RfDaArchiveRequest archiveRequest) {
        RfDaArchiveResponse response = new RfDaArchiveResponse();
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
}
