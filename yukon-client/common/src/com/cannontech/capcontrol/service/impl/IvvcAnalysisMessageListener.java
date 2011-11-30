package com.cannontech.capcontrol.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.message.IvvcAnalysisMessage;
import com.cannontech.capcontrol.service.IvvcAnalysisMessageService;
import com.cannontech.capcontrol.service.IvvcAnalysisScenarioType;
import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.Lists;

public class IvvcAnalysisMessageListener implements MessageListener {

    private static final Logger log = YukonLogManager.getLogger(IvvcAnalysisMessageListener.class);

    private IvvcAnalysisMessageService ivvcAnalysisMessageService;

    @Override
    public void onMessage(Message message) {
        log.trace("Received Ivvc Analysis Message");
        if (message instanceof StreamMessage) {
            StreamMessage streamMessage = (StreamMessage) message;
            try {
                IvvcAnalysisMessage lastAnalysisMessage = buildIvvcAnalysisMessage(streamMessage);
                ivvcAnalysisMessageService.setIvvcAnalysisMessage(lastAnalysisMessage);
            } catch (JMSException e) {
                log.warn("Unable to extract IvvcAnalysisMessage from message", e);
            }
        }
    }

    private IvvcAnalysisMessage buildIvvcAnalysisMessage(StreamMessage streamMessage)
            throws JMSException {
        int subBusId = streamMessage.readInt();
        long timeStamp = streamMessage.readLong() * 1000; //convert from seconds to milliseconds
        int scenarioId = streamMessage.readInt();
        int numIntData = streamMessage.readInt();
        int numFloatData = streamMessage.readInt();

        List<Integer> intData = Lists.newArrayListWithExpectedSize(numIntData);
        for (int i = 0; i < numIntData; i++) {
            int value = streamMessage.readInt();
            intData.add(value);
        }

        List<Float> floatData = Lists.newArrayListWithExpectedSize(numFloatData);
        for (int i = 0; i < numFloatData; i++) {
            float value = streamMessage.readFloat();
            floatData.add(value);
        }
        
        IvvcAnalysisScenarioType type = IvvcAnalysisScenarioType.getEnumWithId(scenarioId);
        IvvcAnalysisMessage msg = new IvvcAnalysisMessage(subBusId,
                                                          timeStamp,
                                                          scenarioId,
                                                          type,
                                                          numIntData,
                                                          numFloatData,
                                                          intData,
                                                          floatData);
        return msg;
    }

    @Autowired
    public void setIvvcAnalysisMessageService(IvvcAnalysisMessageService ivvcAnalysisMessageService) {
        this.ivvcAnalysisMessageService = ivvcAnalysisMessageService;
    }
}
