package com.cannontech.notif.outputs;

import java.util.List;

import com.cannontech.database.data.lite.LiteCICustomer;

public abstract class ContactableBase {
    public abstract LiteCICustomer getContactableCustomer() throws UnknownCustomerException;
    public abstract List getNotifications(NotificationTypeChecker checker);

}
