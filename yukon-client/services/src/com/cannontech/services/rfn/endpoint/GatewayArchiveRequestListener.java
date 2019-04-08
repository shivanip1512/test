package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.impl.NmSyncServiceImpl;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayArchiveRequest;
import com.cannontech.common.rfn.model.RfnDevice;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class GatewayArchiveRequestListener extends ArchiveRequestListenerBase<GatewayArchiveRequest> {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayArchiveRequestListener.class);
    
    @Autowired private GatewayEventLogService gatewayEventLogService;
    @Autowired private NmSyncServiceImpl nmSyncService;

    @Resource(name = "missingGatewayFirstDataTimes") private Map<RfnIdentifier, Instant> missingGatewayFirstDataTimes;
    private List<Worker> workers;
    
    private class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super("GatewayArchive", workerNumber, queueSize);
        }
        
        @Override
        protected RfnDevice processCreation(GatewayArchiveRequest request, RfnIdentifier identifier) {
            if (missingGatewayFirstDataTimes.containsKey(identifier)) {
                missingGatewayFirstDataTimes.remove(identifier);
            }

            try {
                // Create the device in Yukon and send a DB change message
                RfnDevice device = rfnDeviceCreationService.createGateway(identifier.getSensorSerialNumber(),
                    request.getRfnIdentifier());
                rfnDeviceCreationService.incrementNewDeviceCreated();
                log.debug("Created new gateway: " + device);

                gatewayEventLogService.createdGatewayAutomatically(device.getName(),
                    request.getRfnIdentifier().getSensorSerialNumber());
                nmSyncService.syncGatewayName(device);
                return device;
            } catch (Exception e) {
                log.warn("Creation failed for gateway: " + request.getRfnIdentifier(), e);
                throw new RuntimeException("Creation failed for " + request.getRfnIdentifier(), e);
            }
        }
        
        @Override
        public Optional<String> processData(RfnDevice rfnDevice, GatewayArchiveRequest archiveRequest) {
            //no data to archive on this queue, just device creation requests that have no other payload
            incrementProcessedArchiveRequest();
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
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }
    
    @Override
    protected List<Worker> getConverters() {
        return workers;
    }
    
    //Not needed, no response is sent for this message
    @Override
    protected Object getRfnArchiveResponse(GatewayArchiveRequest archiveRequest) {
        return null;
    }
    
    //Not needed, no response is sent for this message
    @Override
    protected String getRfnArchiveResponseQueueName() {
        return null;
    }
    
    @Override
    @ManagedAttribute
    public int getWorkerCount() {
        return 2;
    }
    
    @ManagedAttribute
    public String getArchivedReadings() {
        return null;
    }
}