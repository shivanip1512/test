package com.cannontech.cc.model;

import com.cannontech.database.data.notification.NotifMap;


public interface BaseParticipant {

    public CICustomerStub getCustomer();
    public BaseEvent getEvent();
    public NotifMap getNotifMap();

}