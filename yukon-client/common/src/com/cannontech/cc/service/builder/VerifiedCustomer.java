package com.cannontech.cc.service.builder;

import com.cannontech.cc.model.CICustomerStub;

public abstract class VerifiedCustomer {
    public enum Status {ALLOW, EXCLUDE_OVERRIDABLE, EXCLUDE};
    //private GroupCustomerNotif customerNotif;
    private Status status;
    private StringBuilder reasonForExclusion;
    
    public VerifiedCustomer() {
        status = Status.ALLOW;
        reasonForExclusion = new StringBuilder();
    }
    public abstract CICustomerStub getCustomer();
    public String getReasonForExclusion() {
        return reasonForExclusion.toString();
    }
    
    public void addExclusion(Status status, String reason) {
        if (reasonForExclusion.length() > 0) {
            reasonForExclusion.append(", ");
        }
        reasonForExclusion.append(reason);
        if (status.ordinal() > this.status.ordinal()) {
            this.status = status;
        }
    }
    
    public Status getStatus() {
        return status;
    }
    
    public boolean isIncludable() {
        return !(status == Status.EXCLUDE);
    }

}
