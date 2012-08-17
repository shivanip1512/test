package com.cannontech.amr.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveResponse;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterPlusReadingData;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.endpoint.RfnArchiveRequestListenerBase;
import com.cannontech.common.rfn.message.RfnArchiveStartupNotification;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class MeterReadingArchiveRequestListener extends RfnArchiveRequestListenerBase<RfnMeterReadingArchiveRequest> {
    
    private static final Logger log = YukonLogManager.getLogger(MeterReadingArchiveRequestListener.class);
    private static final String archiveResponseQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveResponse";
    
    private List<Worker> workers;
    private AtomicInteger archivedReadings = new AtomicInteger();
    
    public class Worker extends WorkerBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }
        
        @Override
        public void processData(RfnDevice rfnDevice, RfnMeterReadingArchiveRequest archiveRequest) {
            RfnMeterPlusReadingData meterPlusReadingData = new RfnMeterPlusReadingData(new RfnMeter(rfnDevice.getPaoIdentifier(), rfnDevice.getRfnIdentifier()), archiveRequest.getData());
            List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
            rfnMeterReadService.processMeterReadingDataMessage(meterPlusReadingData, messagesToSend);

            dynamicDataSource.putValues(messagesToSend);
            archivedReadings.addAndGet(messagesToSend.size());

            sendAcknowledgement(archiveRequest);
            incrementProcessedArchiveRequest();
            LogHelper.debug(log, "%d PointDatas generated for RfnMeterReadingArchiveRequest", messagesToSend.size());
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
        
        // This message is signifying that we are ready to archive meter reads, events & alarms
        // (we only need to send this message once... so we are doing it for no particular reason in this listener)
        RfnArchiveStartupNotification notif = new RfnArchiveStartupNotification();
        jmsTemplate.convertAndSend("yukon.notif.obj.common.rfn.ArchiveStartupNotification", notif);
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
    protected RfnMeterReadingArchiveResponse getRfnArchiveResponse(RfnMeterReadingArchiveRequest archiveRequest) {
        RfnMeterReadingArchiveResponse response = new RfnMeterReadingArchiveResponse();
        response.setDataPointId(archiveRequest.getDataPointId());
        response.setReadingType(archiveRequest.getReadingType());
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