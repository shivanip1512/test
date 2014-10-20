package com.cannontech.services.rfn.endpoint;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.google.common.collect.ImmutableList;

public class GatewayDataResponseListener extends ArchiveRequestListenerBase<GatewayDataResponse> {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDataResponseListener.class);
    
    @Autowired RfnGatewayDataCache rfnGatewayDataCache;
    
    private List<Worker> workers;
    
    private class Worker extends ConverterBase {
        
        public Worker(int workerNumber, int queueSize) {
            super("GatewayDataArchive", workerNumber, queueSize);
        }
        
        @Override
        protected RfnDevice processCreation(GatewayDataResponse message, RfnIdentifier identifier) {
            //Somehow, we got data for a gateway that is not in the database.
            log.warn("Received data for a gateway that is not in the database. Creating " + identifier);
            try {
                RfnDevice device = rfnDeviceCreationService.createGateway(identifier.getSensorSerialNumber(), identifier);
                rfnDeviceCreationService.incrementNewDeviceCreated();
                log.debug("Created new gateway: " + device);
                return device;
            } catch (Exception e) {
                log.warn("Creation failed for " + identifier, e);
                throw new RuntimeException("Creation failed for " + identifier, e);
            }
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, GatewayDataResponse message) {
            try {
                RfnGatewayData data = new RfnGatewayData(message);
                rfnGatewayDataCache.put(rfnDevice.getPaoIdentifier(), data);
            } catch (Exception e) {
                log.warn("Data processing failed for " + rfnDevice, e);
                log.debug("Gateway data: " + message);
                throw new RuntimeException("Data processing failed for " + rfnDevice, e);
            }
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
    protected Object getRfnArchiveResponse(GatewayDataResponse archiveRequest) {
        return null;
    }
    
    //Not needed, no response is sent for this message
    @Override
    protected String getRfnArchiveResponseQueueName() {
        return null;
    }
}
