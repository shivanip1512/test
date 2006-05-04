package com.cannontech.web.cc.util;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.service.builder.VerifiedCustomer;

public class SelectableCustomer {

    private final VerifiedCustomer customerDelegate;
    private boolean selected;

    public SelectableCustomer(VerifiedCustomer customer) {
        customerDelegate = customer;
        selected = !isExclude();
    }

    public CICustomerStub getCustomer() {
        return customerDelegate.getCustomer();
    }
    
    public GroupCustomerNotif getCustomerNotif() {
        return customerDelegate.getCustomerNotif();
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

    public VerifiedCustomer getCustomerDelegate() {
        return customerDelegate;
    }

}
