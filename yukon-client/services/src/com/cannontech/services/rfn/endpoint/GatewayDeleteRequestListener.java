package com.cannontech.services.rfn.endpoint;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.DeviceDao;
import com.google.common.collect.ImmutableList;

public class GatewayDeleteRequestListener extends ArchiveRequestListenerBase<GatewayDeleteRequest> {
    private static final Logger log = YukonLogManager.getLogger(GatewayDeleteRequestListener.class);

    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnGatewayDataCache dataCache;
    @Autowired private GatewayEventLogService gatewayEventLogService;

    private List<Worker> workers;

    private class Worker extends ConverterBase {

        public Worker(int workerNumber, int queueSize) {
            super("GatewayDelete", workerNumber, queueSize);
        }

        @Override
        protected RfnDevice processCreation(GatewayDeleteRequest gatewayDeleteRequest, RfnIdentifier identifier) {
            // We got a message for a gateway that is not in the database.
            log.warn("Received delete request for a gateway that's not in the database: " + identifier);
            throw new RuntimeException("Gateway does not exist in System " + identifier);
        }

        @Override
        public void processData(RfnDevice rfnDevice, GatewayDeleteRequest gatewayDeleteRequest) {
            // Delete from yukon database and cache, and send DB change message
            deviceDao.removeDevice(rfnDevice);
            dataCache.remove(rfnDevice.getPaoIdentifier());
            gatewayEventLogService.deletedGatewayAutomatically(rfnDevice.getName(),
                gatewayDeleteRequest.getRfnIdentifier().getSensorSerialNumber());
        }
    }

    @Override
    @PostConstruct
    public void init() {
        // Set up as many workers as requested
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

    // Not needed, no response is sent for this message
    @Override
    protected Object getRfnArchiveResponse(GatewayDeleteRequest archiveRequest) {
        return null;
    }

    // Not needed, no response is sent for this message
    @Override
    protected String getRfnArchiveResponseQueueName() {
        return null;
    }

    @Override
    @PreDestroy
    protected void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    protected List<? extends ArchiveRequestListenerBase<GatewayDeleteRequest>.ConverterBase> getConverters() {
        return workers;
    }

}
