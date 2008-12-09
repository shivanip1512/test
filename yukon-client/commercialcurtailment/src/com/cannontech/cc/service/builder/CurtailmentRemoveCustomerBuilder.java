package com.cannontech.cc.service.builder;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;

public class CurtailmentRemoveCustomerBuilder extends EventBuilderBase {
    private CurtailmentEvent originalEvent;
    private List<VerifiedPlainCustomer> newCustomerList;

    public CurtailmentRemoveCustomerBuilder(CurtailmentEvent originalEvent) {
        this.originalEvent = originalEvent;
    }
    
    public void setNewCustomerList(List<VerifiedPlainCustomer> newCustomerList) {
        this.newCustomerList = newCustomerList;
    }
    public List<VerifiedPlainCustomer> getNewCustomerList() {
        return newCustomerList;
    }
    
    public CurtailmentEvent getOriginalEvent() {
        return originalEvent;
    }
}
