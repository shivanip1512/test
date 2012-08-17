package com.cannontech.amr.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveRequest;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveResponse;
import com.cannontech.amr.rfn.service.RfnMeterEventService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.endpoint.RfnArchiveRequestListenerBase;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class RfnAlarmArchiveRequestListener extends RfnArchiveRequestListenerBase<RfnAlarmArchiveRequest> {

    private static final Logger log = YukonLogManager.getLogger(RfnAlarmArchiveRequestListener.class);
    private static final String archiveResponseQueueName = "yukon.qr.obj.amr.rfn.AlarmArchiveResponse";

    private RfnMeterEventService rfnMeterEventService;

    private List<Worker> workers;
    private AtomicInteger processedAlarmArchiveRequest = new AtomicInteger();

    public class Worker extends WorkerBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }

        @Override
        protected void processData(RfnDevice device, RfnAlarmArchiveRequest archiveRequest) {
            /** Only process events for meters at this time */
            if (device.getPaoIdentifier().getPaoType().isMeter()) {
                List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(3);
                rfnMeterEventService.processEvent(device, archiveRequest.getAlarm(), messagesToSend);
    
                // Save analog value(s) to db
                dynamicDataSource.putValues(messagesToSend);
                processedAlarmArchiveRequest.addAndGet(messagesToSend.size());
    
                incrementProcessedArchiveRequest();
                LogHelper.debug(log, "%d PointDatas generated for RfnAlarmArchiveRequest", messagesToSend.size());
            }
            
            sendAcknowledgement(archiveRequest);
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
    protected RfnAlarmArchiveResponse getRfnArchiveResponse(RfnAlarmArchiveRequest archiveRequest) {
        RfnAlarmArchiveResponse response = new RfnAlarmArchiveResponse();
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
    public int getProcessedAlarmArchiveRequest() {
        return processedAlarmArchiveRequest.get();
    }

    @Autowired
    public void setRfnMeterEventService(RfnMeterEventService rfnMeterEventService) {
        this.rfnMeterEventService = rfnMeterEventService;
    }
}