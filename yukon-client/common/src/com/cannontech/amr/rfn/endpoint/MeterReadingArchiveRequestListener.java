package com.cannontech.amr.rfn.endpoint;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveResponse;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveStartupNotification;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;

public class MeterReadingArchiveRequestListener implements MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(MeterReadingArchiveRequestListener.class);
    
    private DynamicDataSource dynamicDataSource;
    private RfnMeterReadService rfnMeterReadService;
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void startup() {
        RfnMeterReadingArchiveStartupNotification response = new RfnMeterReadingArchiveStartupNotification();
        jmsTemplate.convertAndSend("yukon.notif.obj.amr.rfn.MeterReadingArchiveStartupNotification", response);
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof RfnMeterReadingArchiveRequest) {
                    RfnMeterReadingArchiveRequest archiveRequest = (RfnMeterReadingArchiveRequest) object;
                    handleArchiveRequest(archiveRequest);
                    message.acknowledge();
                }
            } catch (JMSException e) {
                log.warn("Unable to extract object from message", e);
            }
        }
    }

    private void handleArchiveRequest(RfnMeterReadingArchiveRequest archiveRequest) {
        try {
            LogHelper.debug(log, "Received RfnMeterReadingArchiveRequest: %s", archiveRequest);
            List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
            
            rfnMeterReadService.processMeterReadingDataMessage(archiveRequest.getData(), archiveRequest.getReadingType(), messagesToSend);
            
            dynamicDataSource.putValues(messagesToSend);
            
            sendAcknowledgement(archiveRequest);
            
            LogHelper.debug(log, "%d PointDatas generated for RfnMeterReadingArchiveRequest", messagesToSend.size());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("caught exception in handleArchiveRequest", e);
            } else {
                log.warn("caught exception in handleArchiveRequest: " + e.toString());
            }
        }
    }
    
    private void sendAcknowledgement(RfnMeterReadingArchiveRequest archiveRequest) {
        RfnMeterReadingArchiveResponse response = new RfnMeterReadingArchiveResponse();
        response.setDataPointId(archiveRequest.getDataPointId());
        response.setReadingType(archiveRequest.getReadingType());
        
        jmsTemplate.convertAndSend("yukon.rr.obj.amr.rfn.MeterReadingArchiveResponse", response);
        
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Autowired
    public void setRfnMeterReadService(RfnMeterReadService rfnMeterReadService) {
        this.rfnMeterReadService = rfnMeterReadService;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
    }
}