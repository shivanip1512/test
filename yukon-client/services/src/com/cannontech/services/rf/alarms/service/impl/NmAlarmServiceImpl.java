package com.cannontech.services.rf.alarms.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
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
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.services.rf.alarms.service.NmAlarmService;

public class NmAlarmServiceImpl implements NmAlarmService, MessageListener{

    @Autowired RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired InfrastructureWarningsDao infrastructureWarningsDao;
    @Autowired RfnGatewayService rfnGatewayService;
    
    private JmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(NmAlarmServiceImpl.class);

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof AlarmArchiveRequest) {
                    AlarmArchiveResponse response = handle((AlarmArchiveRequest) objMessage.getObject());
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }

    @Override
    public AlarmArchiveResponse handle(AlarmArchiveRequest request) throws JMSException {
        AlarmData data = request.getAlarmData();
        RfnIdentifier raisedBy = data.getRaisedBy();
        RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(raisedBy);
        if (rfnDevice.getPaoIdentifier().getPaoType().isRfGateway()) {
            AlarmCategory category = request.getAlarmCategory();
            AlarmType.of(category, data.getAlarmCodeID()).ifPresent(alarmType -> {
                double value = data.getAlarmState() == AlarmState.ASSERT ? EventStatus.ACTIVE.getRawState() : EventStatus.CLEARED.getRawState();
                rfnGatewayService.generatePointData(rfnDevice, alarmType.getAttribute(), value, true, data.getTimeStamp());
            });
        }
        
        AlarmArchiveResponse response = new AlarmArchiveResponse();
        response.setAlarmCategory(request.getAlarmCategory());
        response.setDataPointId(request.getDataPointId());
        return response;
    }

}
