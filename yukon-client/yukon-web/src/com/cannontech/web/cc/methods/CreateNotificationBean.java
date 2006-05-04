package com.cannontech.web.cc.methods;

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.service.BaseNotificationStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.web.util.JSFUtil;


public class CreateNotificationBean extends EventCreationBase {
    private ProgramService programService;
    private CurtailmentBuilder builder;
    
    @Override
    public String getStartPage() {
        return "notifStart";
    }
    
    @Override
    public void initialize() {
        setBuilder(getMyStrategy().createBuilder(getProgram()));
        
        clearForm();
    }

    public String doAfterParameterEntry() {
        try {
            // check values so far
            // bad use of exceptions?
            getMyStrategy().verifyTimes(getBuilder());
        } catch (EventCreationException e) {
            JSFUtil.addNullMessage(e.getMessage());
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
        CurtailmentEvent event = getMyStrategy().createEvent(getBuilder());
        
        String url = "/cc/notif/detail.jsf?eventId=" + event.getId();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            JSFUtil.addNullMessage("Unable to redirect: " + e);
            return "error";
        }
        return null;
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
    public List<VerifiedCustomer> 
    getVerifiedCustomerList(List<Group> selectedGroupList) {
        return getStrategy().getVerifiedCustomerList(getBuilder(), selectedGroupList);
    }

}
