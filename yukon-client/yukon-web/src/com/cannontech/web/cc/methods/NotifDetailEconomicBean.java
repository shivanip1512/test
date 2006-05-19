package com.cannontech.web.cc.methods;

import java.util.List;

import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.service.BaseEconomicStrategy;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.web.cc.CommercialCurtailmentBean;

public class NotifDetailEconomicBean {
    private ProgramService programService;
    private BaseEconomicStrategy strategy;
    private EconomicService economicService;
    private CommercialCurtailmentBean commercialCurtailment;
    private EconomicEventParticipant participant;
    
    public void setParticipant(EconomicEventParticipant participant) {
        this.participant = participant;
    }
    
    public String show() {
        return "notifEconDetail";
    }
    
    public String refresh() {
        return null;
    }
    
    
    // getters and setters
    
    public EconomicEventParticipant getParticipant() {
        return participant;
    }
    public CommercialCurtailmentBean getCommercialCurtailment() {
        return commercialCurtailment;
    }
    public void setCommercialCurtailment(CommercialCurtailmentBean commercialCurtailment) {
        this.commercialCurtailment = commercialCurtailment;
    }
    public EconomicService getEconomicService() {
        return economicService;
    }
    public void setEconomicService(EconomicService economicService) {
        this.economicService = economicService;
    }
    public ProgramService getProgramService() {
        return programService;
    }
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    public BaseEconomicStrategy getStrategy() {
        return strategy;
    }
    public void setStrategy(BaseEconomicStrategy strategy) {
        this.strategy = strategy;
    }

    public List<EconomicEventNotif> getNotifList() {
        return economicService.getNotifications(participant);
    }
    
        
}
