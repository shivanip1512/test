package com.cannontech.capcontrol.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.message.IvvcAnalysisMessage;
import com.cannontech.capcontrol.service.impl.IvvcAnalysisScenarioProcessor;
import com.cannontech.user.YukonUserContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class IvvcAnalysisMessageService {
    
    private IvvcAnalysisScenarioProcessor ivvcAnalysisScenarioProcessor;
    
    private Cache<Integer, IvvcAnalysisMessage> msgMap = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterWrite(5, TimeUnit.DAYS).build();
    
    public void setIvvcAnalysisMessage(IvvcAnalysisMessage msg) {
        msgMap.put(msg.getSubBusId(), msg);
    }
    
    public String getMessageForSubBusId(int subBusId, YukonUserContext userContext) {
        IvvcAnalysisMessage analysisMessage = msgMap.asMap().get(subBusId);
        if (analysisMessage == null) {
            return null;
        }
        String returnMessage = ivvcAnalysisScenarioProcessor.getMessage(analysisMessage, userContext);
        return returnMessage;
    }
    
    @Autowired
    public void setIvvcAnalysisScenarioProcessor(IvvcAnalysisScenarioProcessor ivvcAnalysisScenarioProcessor) {
        this.ivvcAnalysisScenarioProcessor = ivvcAnalysisScenarioProcessor;
    }
}
