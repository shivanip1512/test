package com.cannontech.web.cc.methods;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.service.AccountingStrategy;
import com.cannontech.cc.service.builder.AccountingBuilder;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.web.util.JSFUtil;


public class CreateAccountingBean extends EventCreationBase {
    private AccountingBuilder builder;
    private DetailAccountingBean detailBean;
    
    @Override
    public String getStartPage() {
        return "acctStart";
    }
    
    @Override
    public void initialize() {
        setBuilder(getMyStrategy().createBuilder(getProgram(), JSFUtil.getYukonUserContext()));
    }

    public String doAfterParameterEntry() {
        getCustomerSelectionBean().setEventBean(this);
        return "groupSelection";
    }
    
    @Override
    public String doAfterCustomerPage() {
        // move parameters from getCustomerSelectionBean() to EventBuilderBase
        getBuilder().setCustomerList(getCustomerSelectionBean().getSelectedCustomers());
        return "acctConfirmation";
    }
    
    public String doCreateEvent() {
        AccountingEvent event;
        try {
            event = getMyStrategy().createEvent(getBuilder());
            return detailBean.showDetail(event);
        } catch (EventCreationException e) {
            JSFUtil.handleException("Unable to create event", e);
            return null;
        }
    }
    
    public AccountingStrategy getMyStrategy() {
        return (AccountingStrategy) getStrategy();
    }

    public AccountingBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(AccountingBuilder builder) {
        this.builder = builder;
    }

    public DetailAccountingBean getDetailBean() {
        return detailBean;
    }

    public void setDetailBean(DetailAccountingBean detailBean) {
        this.detailBean = detailBean;
    }


}
