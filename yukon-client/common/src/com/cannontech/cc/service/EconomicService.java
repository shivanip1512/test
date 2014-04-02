package com.cannontech.cc.service;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteYukonUser;

public class EconomicService {
    private EconomicEventDao economicEventDao;
    private EconomicEventParticipantDao economicEventParticipantDao;
    private EconomicEventNotifDao economicEventNotifDao;
    private CustomerStubDao customerStubDao;
    private CustomerDao customerDao;
    private StrategyFactory strategyFactory;

    public EconomicService() {
        super();
    }
    
    public EconomicEvent getEvent(Integer eventId) {
        return economicEventDao.getForId(eventId);
    }
    
    public EconomicStrategy getEconomicStrategy(EconomicEvent event) {
        Validate.notNull(event, "EconomicEvent must not be null.");
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(event.getProgram());
        return (EconomicStrategy) strategy;
    }

    public List<EconomicEventParticipant> getParticipants(EconomicEvent event) {
        return economicEventParticipantDao.getForEvent(event);
    }
    
    public EconomicEventParticipant getParticipant(EconomicEvent event, LiteYukonUser yukonUser) {
        LiteCICustomer liteCICustomer = customerDao.getCICustomerForUser(yukonUser);
        CICustomerStub customerStub = customerStubDao.getForLite(liteCICustomer);
        CICustomerStub customer = customerStub;
        return economicEventParticipantDao.getForCustomerAndEvent(customer, event);
    }

    public 
    EconomicEventParticipantSelectionWindow 
    getCustomerSelectionWindow(EconomicEventPricing revision,
                         EconomicEventParticipant participant, 
                         Integer windowOffset) {
        EconomicEventPricingWindow window = getFallThroughWindow(revision, windowOffset);
        EconomicEventParticipantSelection selection = participant.getSelection(window.getPricingRevision());
        EconomicEventParticipantSelectionWindow selectionWindow = selection.getSelectionWindow(window);
        return selectionWindow;
    }

    public EconomicEventPricingWindow getFallThroughWindow(EconomicEventPricing startingRevision, Integer windowOffset) {
        EconomicEventPricingWindow window = startingRevision.getWindows().get(windowOffset);
        EconomicEventPricing revision = startingRevision;
        while (window == null) {
            // look for window in previous revision
            revision = revision.getPrevious();
            window = revision.getWindows().get(windowOffset);
        }
        return window;
    }
    
    public EconomicEventParticipantSelectionWindow 
    getFallThroughWindowSelection(EconomicEventParticipantSelection selection, 
                                  Integer windowOffset) {
        
        EconomicEventPricingWindow fallThroughWindow = getFallThroughWindow(selection.getPricingRevision(), windowOffset);
        EconomicEventPricing revThatHasWindow = fallThroughWindow.getPricingRevision();
        EconomicEventParticipantSelection selThatHasWindow = selection.getParticipant().getSelection(revThatHasWindow);
        return selThatHasWindow.getSelectionWindow(fallThroughWindow);
    }
    
    public List<EconomicEventNotif> getNotifications(EconomicEventParticipant participant) {
        return economicEventNotifDao.getForParticipant(participant);
    }

    
    //setters for dependency injection
    
    public void setEconomicEventDao(EconomicEventDao economicEventDao) {
        this.economicEventDao = economicEventDao;
    }

    public void setEconomicEventParticipantDao(EconomicEventParticipantDao economicEventParticipantDao) {
        this.economicEventParticipantDao = economicEventParticipantDao;
    }

    public void setCustomerStubDao(CustomerStubDao customerDao) {
        this.customerStubDao = customerDao;
    }

    public EconomicEventNotifDao getEconomicEventNotifDao() {
        return economicEventNotifDao;
    }

    public void setEconomicEventNotifDao(EconomicEventNotifDao economicEventNotifDao) {
        this.economicEventNotifDao = economicEventNotifDao;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }


}
