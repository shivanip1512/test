package com.cannontech.amr.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.message.event.RfnEventArchiveRequest;
import com.cannontech.amr.rfn.message.event.RfnEventArchiveResponse;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterEventService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class RfnEventArchiveRequestListener extends RfnArchiveRequestListenerBase<RfnEventArchiveRequest> {

    private static final Logger log = YukonLogManager.getLogger(RfnEventArchiveRequestListener.class);
    private static final String archiveResponseQueueName = "yukon.rr.obj.amr.rfn.EventArchiveResponse";

    @Autowired private RfnMeterEventService rfnMeterEventService;

    private List<Worker> workers;
    private AtomicInteger processedEventRequest = new AtomicInteger();

    public class Worker extends WorkerBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }

        @Override
        protected void processPointDatas(RfnMeter meter, RfnEventArchiveRequest eventRequest) {
            List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(3);
            rfnMeterEventService.processEvent(meter, eventRequest.getEvent(), messagesToSend);

            // Save analog value(s) to db
            dynamicDataSource.putValues(messagesToSend);
            processedEventRequest.addAndGet(messagesToSend.size());

            sendAcknowledgement(eventRequest);
            rfnArchiveRequestService.incrementProcessedArchiveRequest();
            LogHelper.debug(log, "%d PointDatas generated for RfnEventArchiveRequest", messagesToSend.size());
        }
    }

    @PostConstruct
    public void init() {
        // setup as many workers as requested
        ImmutableList.Builder<Worker> workerBuilder = ImmutableList.builder();
        int workerCount = rfnArchiveRequestService.getWorkerCount();
        int queueSize = rfnArchiveRequestService.getQueueSize();
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
    protected RfnEventArchiveResponse getRfnArchiveResponse(RfnEventArchiveRequest archiveRequest) {
        RfnEventArchiveResponse response = new RfnEventArchiveResponse();
        response.setDataPointId(archiveRequest.getDataPointId());
        return response;
    }

    @Override
    protected String getRfnArchiveResponseQueueName() {
        return archiveResponseQueueName;
    }

    @Override
    protected List<Worker> getWorkers() {
        return workers;
    }

    @ManagedAttribute
    public int getProcessedEventRequest() {
        return processedEventRequest.get();
    }

}