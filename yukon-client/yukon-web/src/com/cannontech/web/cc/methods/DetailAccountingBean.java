package com.cannontech.web.cc.methods;

import java.util.List;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.service.AccountingStrategy;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.database.data.lite.LiteYukonUser;


public class DetailAccountingBean implements BaseDetailBean {
    private AccountingStrategy strategy;
    private StrategyFactory strategyFactory;
    private AccountingEvent event;
    private LiteYukonUser yukonUser;
    
    public String showDetail(BaseEvent event) {
        setEvent((AccountingEvent) event);
        return "acctDetail";
    }

    public Boolean getShowDeleteButton() {
        return true;
    }
    
    public String deleteEvent() {
        getStrategy().deleteEvent(event,getYukonUser());
        return "programSelect";
    }
    
    public String refresh() {
        return null;
    }

    public AccountingEvent getEvent() {
        return event;
    }
    
    public List<AccountingEventParticipant> getParticipants() {
        List<AccountingEventParticipant> eventNotifs = getStrategy().getParticipants(getEvent());
        return eventNotifs;
    }

    public AccountingStrategy getStrategy() {
        return strategy;
    }
    
    public void setEvent(AccountingEvent event) {
        this.event = event;
        strategy = (AccountingStrategy) strategyFactory.getStrategy(event.getProgram());
    }

    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

}
