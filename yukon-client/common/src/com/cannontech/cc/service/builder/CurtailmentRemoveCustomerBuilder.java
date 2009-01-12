package com.cannontech.cc.service.builder;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;

public class CurtailmentRemoveCustomerBuilder extends EventBuilderBase {
    private CurtailmentEvent originalEvent;
    private List<VerifiedPlainCustomer> removeCustomerList;

    public CurtailmentRemoveCustomerBuilder(CurtailmentEvent originalEvent) {
        this.originalEvent = originalEvent;
    }
    
    public void setRemoveCustomerList(List<VerifiedPlainCustomer> removeCustomerList) {
        this.removeCustomerList = removeCustomerList;
    }
    public List<VerifiedPlainCustomer> getRemoveCustomerList() {
        return removeCustomerList;
    }
    
    public CurtailmentEvent getOriginalEvent() {
        return originalEvent;
    }
}
