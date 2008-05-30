package com.cannontech.web.cc.methods;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.service.EconomicStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.builder.EconomicBuilder;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.web.util.JSFUtil;


public class CreateEconomicBean extends EventCreationBase {
    private ProgramService programService;
    private EconomicBuilder builder;
    private DetailEconomicBean detailBean;
    
    @Override
    public String getStartPage() {
        return "econStart";
    }
    
    @Override
    public void initialize() {
        setBuilder(getMyStrategy().createBuilder(getProgram(), JSFUtil.getYukonUserContext()));
    }
    
    public String initExtension(EconomicEvent oldEvent) {
        setBuilder(getMyStrategy().createExtensionBuilder(oldEvent, JSFUtil.getYukonUserContext()));
        return getStartPage();
    }

    public String doAfterInitialEntry() {
        try {
            // check values so far
            // bad use of exceptions?
            getMyStrategy().verifyTimes(getBuilder());
            getMyStrategy().setupPriceList(getBuilder());
        } catch (EventCreationException e) {
            JSFUtil.handleException("Error with initial parameters", e);
            return null;
        }
        return "econPricing";
    }
    
    public String doAfterPricingEntry() {
        try {
            // check values so far
            // bad use of exceptions?
            getMyStrategy().verifyPrices(getBuilder());
        } catch (EventCreationException e) {
            JSFUtil.handleException("Error with pricing", e);
            return null;
        }
        if (getBuilder().getEvent().isEventExtension()) {
            getMyStrategy().setupExtensionCustomers(getBuilder());
            return "econConfirmation";
        } else {
            getCustomerSelectionBean().setEventBean(this);
            return "groupSelection";
        }
    }
    
    @Override
    public String doAfterCustomerPage() {
        // move parameters from getCustomerSelectionBean() to EventBuilderBase
        getBuilder().setCustomerList(getCustomerSelectionBean().getSelectedCustomers());
        getMyStrategy().setupCustomers(getBuilder());
        return "econConfirmation";
    }
    
    public String doCreateEvent() {
        EconomicEvent event;
        try {
            event = getMyStrategy().createEvent(getBuilder());
            return detailBean.showDetail(event);
        } catch (EventCreationException e) {
            JSFUtil.handleException("Unable to create event", e);
            return null;
        }
    }
    
    public int getWindowLength() {
        return getMyStrategy().getWindowLengthMinutes();
    }
    
    public int getEventHours() {
        int eventMinutes = getBuilder().getEvent().getWindowLengthMinutes() 
            * getBuilder().getNumberOfWindows();
        return eventMinutes / 60;
    }

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    public EconomicStrategy getMyStrategy() {
        return (EconomicStrategy) getStrategy();
    }

    public EconomicBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(EconomicBuilder builder) {
        this.builder = builder;
    }

    public void setDetailBean(DetailEconomicBean detailBean) {
        this.detailBean = detailBean;
        
        // return the favor
        detailBean.setCreateEconomicBean(this);
    }

}
