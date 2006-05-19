package com.cannontech.web.cc.methods;

import java.util.List;

import javax.faces.component.UIComponent;

import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.StrategyBase;
import com.cannontech.cc.service.builder.VerifiedNotifCustomer;
import com.cannontech.web.cc.CustomerSelectionBean;

public abstract class EventCreationBase {

    private CustomerSelectionBean customerSelectionBean;
    private Program program;
    private StrategyBase strategy;
    UIComponent form;

    public CustomerSelectionBean getCustomerSelectionBean() {
        return customerSelectionBean;
    }

    public void setCustomerSelectionBean(CustomerSelectionBean customerSelectionBean) {
        this.customerSelectionBean = customerSelectionBean;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public abstract String getStartPage();
    
    public abstract String doAfterCustomerPage();
    
    public abstract void initialize();
    
    public String cancel() {
        return "programSelect";
    }

    public void setStrategy(StrategyBase strategy) {
        this.strategy = strategy;
    }
    
    public StrategyBase getStrategy() {
        return strategy;
    }

    public abstract 
    List<VerifiedNotifCustomer> getVerifiedCustomerList(List<Group> selectedGroupList);

}
