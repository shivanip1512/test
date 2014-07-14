package com.cannontech.cc.model;

import java.util.Date;

import com.cannontech.database.data.notification.NotifType;
import com.cannontech.cc.service.NotificationState;

public interface EventNotif {

    public Date getNotificationTime();

    public NotifType getNotifType();

    public NotificationState getState();
    public void setState(NotificationState state);
    public CICustomerStub getCustomer();

}