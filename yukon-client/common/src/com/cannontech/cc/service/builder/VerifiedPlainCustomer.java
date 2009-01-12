package com.cannontech.cc.service.builder;

import com.cannontech.cc.model.CICustomerStub;

public class VerifiedPlainCustomer extends VerifiedCustomer {

    private final CICustomerStub customer;

    public VerifiedPlainCustomer(CICustomerStub customer) {
        this.customer = customer;
    }

    @Override
    public CICustomerStub getCustomer() {
        return customer;
    }

}
