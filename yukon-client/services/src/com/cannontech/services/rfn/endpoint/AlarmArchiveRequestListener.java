package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveRequest;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveResponse;
import com.cannontech.amr.rfn.service.RfnMeterEventService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ManagedResource
public class AlarmArchiveRequestListener extends ArchiveRequestListenerBase<RfnAlarmArchiveRequest> {
    private static final Logger log = YukonLogManager.getLogger(AlarmArchiveRequestListener.class);

    @Autowired private RfnMeterEventService rfnMeterEventService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;
    private List<Worker> workers;
    private AtomicInteger processedAlarmArchiveRequest = new AtomicInteger();

    public class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super("AlarmArchiveConverter", workerNumber, queueSize);
        }

        @Override
        protected Optional<String> processData(RfnDevice device, RfnAlarmArchiveRequest archiveRequest) {
            incrementProcessedArchiveRequest();
            Optional<String> trackingIds = Optional.empty();
            
            // Only process events for meters at this time
            if (device.getPaoIdentifier().getPaoType().isMeter() || 
                    device.getPaoIdentifier().getPaoType().isRfRelay()) {
                List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(3);
                rfnMeterEventService.processEvent(device, archiveRequest.getAlarm(), messagesToSend);

                trackingIds = trackValues(messagesToSend);

                // Save analog value(s) to db
                asyncDynamicDataSource.putValues(messagesToSend);
                processedAlarmArchiveRequest.addAndGet(messagesToSend.size());
    
                if (log.isDebugEnabled()) {
                    log.debug("{} PointDatas generated for RfnAlarmArchiveRequest", messagesToSend.size());
                }
            }
            sendAcknowledgement(archiveRequest);
            
            return trackingIds;
        }

        @Override
        protected Instant getDataTimestamp(RfnAlarmArchiveRequest request) {
            try {
                return new Instant(request.getAlarm().getTimeStamp());
            } catch (Exception e) {
                return null;
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
        jmsTemplate = jmsTemplateFactory.createResponseTemplate(JmsApiDirectory.RF_ALARM_ARCHIVE);
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
    protected YukonJmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    @Override
    protected List<Worker> getConverters() {
        return workers;
    }

    @ManagedAttribute
    public int getProcessedAlarmArchiveRequest() {
        return processedAlarmArchiveRequest.get();
    }
}
