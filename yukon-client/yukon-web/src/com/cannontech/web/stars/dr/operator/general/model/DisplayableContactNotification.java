package com.cannontech.web.stars.dr.operator.general.model;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.data.lite.LiteContactNotification;

public class DisplayableContactNotification {

	private LiteContactNotification notification;
	
	public DisplayableContactNotification(LiteContactNotification notification) {
		this.notification = notification;
	}
	
	public int getNotificationId() {
		return notification.getContactNotifID();
	}
	
	public ContactNotificationType getOperatorContactNotificationType() {
		return notification.getContactNotificationType();
	}
	
	public LiteContactNotification getNotification() {
		return notification;
	}
}
