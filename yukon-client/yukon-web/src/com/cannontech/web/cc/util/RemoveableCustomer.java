package com.cannontech.web.cc.util;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.builder.VerifiedPlainCustomer;

public class RemoveableCustomer {

    private final VerifiedPlainCustomer customerDelegate;
    private boolean selected;

    public RemoveableCustomer(VerifiedPlainCustomer customer) {
        customerDelegate = customer;
        selected = !isExclude();
    }

    public CICustomerStub getCustomer() {
        return customerDelegate.getCustomer();
    }
    
    public String getReasonForExclusion() {
        return customerDelegate.getReasonForExclusion();
    }

    public boolean isExclude() {
        return customerDelegate.getStatus() != VerifiedCustomer.Status.ALLOW;
    }
    
    public boolean isAllowOverride() {
        return customerDelegate.getStatus() != VerifiedCustomer.Status.EXCLUDE;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public VerifiedPlainCustomer getCustomerDelegate() {
        return customerDelegate;
    }

}
