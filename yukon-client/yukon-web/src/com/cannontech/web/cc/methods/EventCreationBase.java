package com.cannontech.web.cc.methods;

import java.util.List;

import javax.faces.component.UIComponent;

import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.StrategyBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.web.cc.CustomerSelectionBean;
import com.cannontech.web.util.JSFUtil;

public abstract class EventCreationBase {

    private CustomerSelectionBean customerSelectionBean;
    private Program program;
    private StrategyBase strategy;
    private boolean doClear = false;
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

    /**
     * This will cause the stored values in the form
     * to be cleared. To work, the JSF page must bind
     * its form element to the "form" property of this
     * bean.
     */
    protected void clearForm() {
        doClear = true;
    }
    
    public UIComponent getForm() {
        return form;
    }

    public void setForm(UIComponent form) {
        this.form = form;
        if (doClear) {
            JSFUtil.clearComponent(form);
            doClear = false;
        }
    }

    public abstract 
    List<VerifiedCustomer> getVerifiedCustomerList(List<Group> selectedGroupList);

}
