package com.cannontech.web.cc.methods;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.service.BaseNotificationStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.VerifiedNotifCustomer;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.web.util.JSFUtil;


public class CreateNotificationBean extends EventCreationBase {
    private ProgramService programService;
    private CurtailmentBuilder builder;
    private DetailNotificationBean detailBean;
    
    @Override
    public String getStartPage() {
        return "notifStart";
    }
    
    @Override
    public void initialize() {
        setBuilder(getMyStrategy().createBuilder(getProgram()));
    }

    public String doAfterParameterEntry() {
        try {
            // check values so far
            // bad use of exceptions?
            getMyStrategy().verifyTimes(getBuilder());
        } catch (EventCreationException e) {
            JSFUtil.handleException("Invalid times", e);
            return null;
        }
        getCustomerSelectionBean().setEventBean(this);
        return "groupSelection";
    }
    
    @Override
    public String doAfterCustomerPage() {
        // move parameters from getCustomerSelectionBean() to EventBuilderBase
        getBuilder().setCustomerList(getCustomerSelectionBean().getSelectedCustomers());
        return "notifConfirmation";
    }
    
    public String doCreateEvent() {
        CurtailmentEvent event;
        try {
            event = getMyStrategy().createEvent(getBuilder());
            return detailBean.showDetail(event);
        } catch (EventCreationException e) {
            JSFUtil.handleException("Unable to create event", e);
            return null;
        }
    }
    
    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    public BaseNotificationStrategy getMyStrategy() {
        return (BaseNotificationStrategy) getStrategy();
    }

    public CurtailmentBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(CurtailmentBuilder builder) {
        this.builder = builder;
    }

    @Override
    public List<VerifiedNotifCustomer> 
    getVerifiedCustomerList(List<Group> selectedGroupList) {
        return getStrategy().getVerifiedCustomerList(getBuilder(), selectedGroupList);
    }

    public DetailNotificationBean getDetailBean() {
        return detailBean;
    }

    public void setDetailBean(DetailNotificationBean detailBean) {
        this.detailBean = detailBean;
    }


}
