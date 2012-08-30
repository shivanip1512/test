package com.cannontech.capcontrol.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.message.IvvcAnalysisMessage;
import com.cannontech.capcontrol.service.IvvcAnalysisMessageService;
import com.cannontech.clientutils.YukonLogManager;

public class IvvcAnalysisMessageListener implements MessageListener {

    private static final Logger log = YukonLogManager.getLogger(IvvcAnalysisMessageListener.class);

    private IvvcAnalysisMessageService ivvcAnalysisMessageService;

    @Override
    public void onMessage(Message message) {
        log.trace("Received Ivvc Analysis Message");
        if (message instanceof StreamMessage) {
            StreamMessage streamMessage = (StreamMessage) message;
            try {
                IvvcAnalysisMessage lastAnalysisMessage = IvvcAnalysisHelper.buildIvvcAnalysisMessage(streamMessage);
                ivvcAnalysisMessageService.setIvvcAnalysisMessage(lastAnalysisMessage);
            } catch (JMSException e) {
                log.warn("Unable to extract IvvcAnalysisMessage from message", e);
            }
        }
    }

    @Autowired
    public void setIvvcAnalysisMessageService(IvvcAnalysisMessageService ivvcAnalysisMessageService) {
        this.ivvcAnalysisMessageService = ivvcAnalysisMessageService;
    }
}
