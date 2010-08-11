package com.cannontech.amr.crf.endpoint;

import java.io.Serializable;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.crf.message.CrfMeterReadingArchiveRequest;
import com.cannontech.amr.crf.message.CrfMeterReadingArchiveResponse;
import com.cannontech.amr.crf.service.CrfMeterReadService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;

public class MeterReadingArchiveRequestListener implements MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(MeterReadingArchiveRequestListener.class);
    
    private DynamicDataSource dynamicDataSource;
    private CrfMeterReadService crfMeterReadService;
    private JmsTemplate jmsTemplate;
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof CrfMeterReadingArchiveRequest) {
                    CrfMeterReadingArchiveRequest archiveRequest = (CrfMeterReadingArchiveRequest) object;
                    handleArchiveRequest(archiveRequest);
                    message.acknowledge();
                }
            } catch (JMSException e) {
                log.warn("Unable to extract object from message", e);
            }
        }
    }

    private void handleArchiveRequest(CrfMeterReadingArchiveRequest archiveRequest) {
        LogHelper.debug(log, "Received CrfMeterReadingArchiveRequest: %s", archiveRequest);
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
        
        crfMeterReadService.processMeterReadingDataMessage(archiveRequest.getData(), archiveRequest.getReadingType(), messagesToSend);
        
        dynamicDataSource.putValues(messagesToSend);
        
        sendAcknowledgement(archiveRequest);
        
        LogHelper.debug(log, "%d PointDatas generated for CrfMeterReadingArchiveRequest", messagesToSend.size());
    }
    
    private void sendAcknowledgement(CrfMeterReadingArchiveRequest archiveRequest) {
        CrfMeterReadingArchiveResponse response = new CrfMeterReadingArchiveResponse();
        response.setDataPointId(archiveRequest.getDataPointId());
        response.setReadingType(archiveRequest.getReadingType());
        
        jmsTemplate.convertAndSend("yukon.rr.obj.amr.crf.MeterReadingArchiveResponse", response);
        
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Autowired
    public void setCrfMeterReadService(CrfMeterReadService crfMeterReadService) {
        this.crfMeterReadService = crfMeterReadService;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
    }
}