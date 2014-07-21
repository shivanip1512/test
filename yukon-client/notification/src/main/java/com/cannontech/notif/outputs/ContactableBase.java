package com.cannontech.notif.outputs;

import java.util.List;

import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContactNotification;

public abstract class ContactableBase {
    public abstract LiteCICustomer getContactableCustomer() throws UnknownCustomerException;
    public abstract List<LiteContactNotification> getNotifications(NotificationTypeChecker checker);

}
