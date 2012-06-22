package com.cannontech.dr.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.endpoint.RfnArchiveRequestListenerBase;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveResponse;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveResponse;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class LcrReadingArchiveRequestListener extends RfnArchiveRequestListenerBase<RfnLcrArchiveRequest> {
    
    private static final Logger log = YukonLogManager.getLogger(LcrReadingArchiveRequestListener.class);
    private static final String archiveResponseQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveResponse";
    
    private List<Worker> workers;
    private AtomicInteger archivedReadings = new AtomicInteger();
    
    public class Worker extends WorkerBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }
        
        @Override
        public void processPointDatas(RfnDevice rfnDevice, RfnLcrArchiveRequest archiveRequest) {
            if (archiveRequest instanceof RfnLcrReadingArchiveRequest) { // Nothing to do when just an RfnLcrArchiveRequest
                List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
                // TODO Garret's stuff
    
                dynamicDataSource.putValues(messagesToSend);
                archivedReadings.addAndGet(messagesToSend.size());
    
                sendAcknowledgement(archiveRequest);
                incrementProcessedArchiveRequest();
                LogHelper.debug(log, "%d PointDatas generated for RfnLcrReadingArchiveRequest", messagesToSend.size());
            }
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