package com.cannontech.services.rfn.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.archive.RfRelayArchiveRequest;
import com.cannontech.common.rfn.message.archive.RfRelayArchiveResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class RfRelayArchiveRequestListener extends ArchiveRequestListenerBase<RfRelayArchiveRequest> {

    private static final Logger log = YukonLogManager.getLogger(RfRelayArchiveRequestListener.class);
    private static final String archiveResponseQueueName = "yukon.qr.obj.rfn.RfRelayArchiveResponse";

    private List<Worker> workers = new ArrayList<>();

    private class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super("RfRelayArchive", workerNumber, queueSize);
        }

        @Override
        public Optional<String> processData(RfnDevice rfnDevice, RfRelayArchiveRequest archiveRequest) {
            // no data to archive on this queue, just device creation requests
            // that have no other payload
            incrementProcessedArchiveRequest();
            sendAcknowledgement(archiveRequest);
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

    @Override
    protected Object getRfnArchiveResponse(RfRelayArchiveRequest archiveRequest) {
        RfRelayArchiveResponse response = new RfRelayArchiveResponse();
        response.setNodeId(archiveRequest.getNodeId());
        return response;
    }

    @Override
    protected String getRfnArchiveResponseQueueName() {
        return archiveResponseQueueName;
    }

    @ManagedAttribute
    public String getArchivedReadings() {
        return null;
    }
}