package com.cannontech.web.cc;

import java.util.Map;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.service.CICurtailmentStrategy;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.web.cc.methods.BaseDetailBean;

public class EventDetailHelper {
    private StrategyFactory strategyFactory;
    private Map<String, BaseDetailBean> eventDetailLookup;
    private Map<String, BaseDetailBean> userEventDetailLookup;

    public EventDetailHelper() {
        super();
    }

    public BaseDetailBean getEventDetailBean(BaseEvent event) {
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(event.getProgram());
        String methodKey = strategy.getMethodKey();
        return eventDetailLookup.get(methodKey);
    }

    public BaseDetailBean getUserEventDetailBean(BaseEvent event) {
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(event.getProgram());
        String methodKey = strategy.getMethodKey();
        return userEventDetailLookup.get(methodKey);
    }

    public Map<String, BaseDetailBean> getEventDetailLookup() {
        return eventDetailLookup;
    }

    public void setEventDetailLookup(Map<String, BaseDetailBean> eventDetailLookup) {
        this.eventDetailLookup = eventDetailLookup;
    }

    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public Map<String, BaseDetailBean> getUserEventDetailLookup() {
        return userEventDetailLookup;
    }

    public void setUserEventDetailLookup(Map<String, BaseDetailBean> userEventDetailLookup) {
        this.userEventDetailLookup = userEventDetailLookup;
    }


}
