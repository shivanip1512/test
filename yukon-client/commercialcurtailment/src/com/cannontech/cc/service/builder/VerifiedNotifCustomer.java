package com.cannontech.cc.service.builder;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.GroupCustomerNotif;

public class VerifiedNotifCustomer extends VerifiedCustomer {

    private GroupCustomerNotif customerNotif;

    public VerifiedNotifCustomer(GroupCustomerNotif customerNotif) {
        this.customerNotif = customerNotif;
        
    }

    public GroupCustomerNotif getCustomerNotif() {
        return customerNotif;
    }

    @Override
    public CICustomerStub getCustomer() {
        return customerNotif.getCustomer();
    }

}
