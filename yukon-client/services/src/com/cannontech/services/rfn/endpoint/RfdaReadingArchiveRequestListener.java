package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.message.archive.RfdaArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfdaArchiveResponse;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class RfdaReadingArchiveRequestListener extends ArchiveRequestListenerBase<RfdaArchiveRequest> {
    
    private static final String archiveResponseQueueName = "yukon.qr.obj.da.rfn.RfDaArchiveResponse";
    
    private List<Worker> workers;
    private final AtomicInteger archivedReadings = new AtomicInteger();
    
    public class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, RfdaArchiveRequest archiveRequest) {
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
    protected Object getRfnArchiveResponse(RfdaArchiveRequest archiveRequest) {
        RfdaArchiveResponse response = new RfdaArchiveResponse();
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