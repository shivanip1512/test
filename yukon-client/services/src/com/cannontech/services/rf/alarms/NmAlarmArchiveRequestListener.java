package com.cannontech.services.rf.alarms;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.alarm.AlarmArchiveRequest;
import com.cannontech.common.rfn.message.alarm.AlarmArchiveResponse;
import com.cannontech.common.rfn.message.alarm.AlarmCategory;
import com.cannontech.common.rfn.message.alarm.AlarmData;
import com.cannontech.common.rfn.message.alarm.AlarmState;
import com.cannontech.common.rfn.message.alarm.AlarmType;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.services.rfn.endpoint.ArchiveRequestListenerBase;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class NmAlarmArchiveRequestListener extends ArchiveRequestListenerBase<AlarmArchiveRequest> {

    @Autowired RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired RfnGatewayService rfnGatewayService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;
    private List<Worker> workers;
    private AtomicInteger processedAlarmArchiveRequest = new AtomicInteger();

    public class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super("AlarmArchiveConverter", workerNumber, queueSize);
        }

        @Override
        protected Optional<String> processData(RfnDevice device, AlarmArchiveRequest archiveRequest) {
            AlarmData data = archiveRequest.getAlarmData();
            RfnIdentifier raisedBy = data.getRaisedBy();
            try {
                RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(raisedBy);
                if (rfnDevice.getPaoIdentifier().getPaoType().isRfGateway()) {
                    AlarmCategory category = archiveRequest.getAlarmCategory();
                    AlarmType.of(category, data.getAlarmCodeID()).ifPresent(alarmType -> {
                        double value = data.getAlarmState() == AlarmState.ASSERT ? EventStatus.ACTIVE.getRawState() : EventStatus.CLEARED.getRawState();
                        rfnGatewayService.generatePointData(rfnDevice, alarmType.getAttribute(), value, true, data.getTimeStamp());
                    });
                    sendAcknowledgement(archiveRequest);
                } else {
                    log.warn("NM Alarm Data received for non-gateway device type {}.", rfnDevice.getPaoIdentifier().getPaoType());
                    // TODO should we "ack" this?
                }
            } catch (NotFoundException e) {
                log.error("Gateway not found in cache: {}.", raisedBy); 
            }
            return Optional.empty();  //  Need to rewrite this to get trackingIds for the pointData....skipping this for now.
        }

        @Override
        protected Instant getDataTimestamp(AlarmArchiveRequest request) {
            try {
                return new Instant(request.getAlarmData().getTimeStamp());
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
        jmsTemplate = jmsTemplateFactory.createResponseTemplate(JmsApiDirectory.NM_ALARM);
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
    protected AlarmArchiveResponse getRfnArchiveResponse(AlarmArchiveRequest archiveRequest) {
      AlarmArchiveResponse response = new AlarmArchiveResponse();
      response.setAlarmCategory(archiveRequest.getAlarmCategory());
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
