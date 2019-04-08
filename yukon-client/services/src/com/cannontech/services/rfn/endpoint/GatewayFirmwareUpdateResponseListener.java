package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.dao.RfnGatewayFirmwareUpgradeDao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.google.common.collect.ImmutableList;

/**
 * Listener that handles incoming GatewayFirmwareUpdateResponse messages, which indicate the result of a firmware
 * upgrade operation for a particular gateway.
 * 
 * This listener is wired up in rfnMeteringContext.xml.
 */
public class GatewayFirmwareUpdateResponseListener extends ArchiveRequestListenerBase<RfnIdentifyingMessage> {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayFirmwareUpdateResponseListener.class);
    
    @Autowired private RfnGatewayFirmwareUpgradeDao gatewayFirmwareUpgradeDao;
    
    private List<Worker> workers;
    
    private class Worker extends ConverterBase {
        
        public Worker(int workerNumber, int queueSize) {
            super("GatewayFirmwareUpdate", workerNumber, queueSize);
        }
        
        @Override
        protected RfnDevice processCreation(RfnIdentifyingMessage message, RfnIdentifier identifier) {
            // We got a message for a gateway that is not in the database.
            log.warn("Received firmware update response for a gateway that's not in the database: " + identifier);
            throw new RuntimeException("Creation not attempted for " + identifier);
        }
        
        @Override
        public Optional<String> processData(RfnDevice rfnDevice, RfnIdentifyingMessage message) {
            RfnGatewayFirmwareUpdateResponse response = (RfnGatewayFirmwareUpdateResponse) message;
            gatewayFirmwareUpgradeDao.complete(response.getUpdateId(), 
                                               rfnDevice.getPaoIdentifier(), 
                                               response.getResult());
            return Optional.empty();  //  no point data to track
        }
    }
    
    @Override
    @PostConstruct
    public void init() {
        //Set up as many workers as requested
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
    
    @Override
    @PreDestroy
    protected void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }
    
    @Override
    protected List<? extends ArchiveRequestListenerBase<RfnIdentifyingMessage>.ConverterBase> getConverters() {
        return workers;
    }
    
    //Not needed, no response is sent for this message
    @Override
    protected Object getRfnArchiveResponse(RfnIdentifyingMessage archiveRequest) {
        return null;
    }
    
    //Not needed, no response is sent for this message
    @Override
    protected String getRfnArchiveResponseQueueName() {
        return null;
    }
}
