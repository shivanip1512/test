package com.cannontech.cc.service.builder;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.GroupCustomerNotif;

public class VerifiedCustomer {
    public enum Status {ALLOW, EXCLUDE, EXCLUDE_OVERRIDABLE};
    private GroupCustomerNotif customerNotif;
    private Status status;
    private String reasonForExclusion;
    
    public VerifiedCustomer(GroupCustomerNotif customerNotif) {
        this.customerNotif = customerNotif;
        status = Status.ALLOW;
        reasonForExclusion = "";
    }
    public CICustomerStub getCustomer() {
        return customerNotif.getCustomer();
    }
    public String getReasonForExclusion() {
        return reasonForExclusion;
    }
    public void setReasonForExclusion(String reasonForExclusion) {
        this.reasonForExclusion = reasonForExclusion;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public GroupCustomerNotif getCustomerNotif() {
        return customerNotif;
    }

}
