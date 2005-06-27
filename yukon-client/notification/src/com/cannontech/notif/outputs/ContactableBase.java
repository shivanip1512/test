package com.cannontech.notif.outputs;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.LiteCustomer;

public abstract class ContactableBase {
    public abstract LiteCustomer getContactableCustomer() throws UnknownCustomerException;
    public abstract List getNotifications(Set notifTypes);

}
