package com.cannontech.capcontrol.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.StreamMessage;

import com.cannontech.capcontrol.message.IvvcAnalysisMessage;
import com.cannontech.capcontrol.service.IvvcAnalysisScenarioType;
import com.google.common.collect.Lists;

public class IvvcAnalysisHelper {
    public static IvvcAnalysisMessage buildIvvcAnalysisMessage(StreamMessage streamMessage) throws JMSException {
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
                                                          intData,
                                                          floatData);
        return msg;
    }
}
