package com.cannontech.cc.service.builder;

import java.util.List;

import com.cannontech.cc.model.GroupCustomerNotif;



public abstract class EventBuilderBase {
    private List<GroupCustomerNotif> customerList;

    public List<GroupCustomerNotif> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<GroupCustomerNotif> customerList) {
        this.customerList = customerList;
    }

}
