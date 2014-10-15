package com.cannontech.services.rfn.endpoint;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayArchiveRequest;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class GatewayArchiveRequestListener extends ArchiveRequestListenerBase<GatewayArchiveRequest> {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayArchiveRequestListener.class);
    
    private @Autowired RfnGatewayDataCache rfnGatewayDataCache;
    
    private List<Worker> workers;
    
    private class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super("GatewayArchive", workerNumber, queueSize);
        }
        
        @Override
        protected RfnDevice processCreation(GatewayArchiveRequest request, RfnIdentifier identifier) {
            try {
                RfnDevice device = rfnDeviceCreationService.createGateway(request.getName(), request.getRfnIdentifier());
                rfnDeviceCreationService.incrementNewDeviceCreated();
                log.debug("Created new gateway: " + device);
                loadGatewayData(device.getPaoIdentifier());
                incrementProcessedArchiveRequest();
                return device;
            } catch (Exception e) {
                log.warn("Creation failed for gateway: " + request.getRfnIdentifier(), e);
                throw new RuntimeException("Creation failed for " + request.getRfnIdentifier(), e);
            }
        }
        
        private void loadGatewayData(PaoIdentifier paoIdentifier) {
            try {
                //requesting data not in the cache causes it to fetch the data from NM
                rfnGatewayDataCache.get(paoIdentifier);
                log.debug("Successfully cached data for gateway: " + paoIdentifier);
            } catch (Exception e) {
                //Catch any exceptions here - creation still succeeded even if data retrieval failed
                log.warn("Exception updating gateway data cache for new gateway: " + paoIdentifier, e);
            }
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, GatewayArchiveRequest archiveRequest) {
            //no data to archive on this queue, just device creation requests that have no other payload
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
    
}