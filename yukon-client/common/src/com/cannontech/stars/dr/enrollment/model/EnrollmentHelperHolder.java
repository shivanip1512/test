package com.cannontech.stars.dr.enrollment.model;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class EnrollmentHelperHolder {

    private final EnrollmentHelper enrollmentHelper;
    private final CustomerAccount customerAccount;
    private final LMHardwareBase lmHardwareBase;

    public EnrollmentHelperHolder(EnrollmentHelper enrollmentHelper, CustomerAccount customerAccount, LMHardwareBase lmHardwareBase) {
        super();
        this.enrollmentHelper = enrollmentHelper;
        this.customerAccount = customerAccount;
        this.lmHardwareBase = lmHardwareBase;
    }

    public EnrollmentHelper getEnrollmentHelper() {
        return enrollmentHelper;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public LMHardwareBase getLmHardwareBase() {
        return lmHardwareBase;
    }
}