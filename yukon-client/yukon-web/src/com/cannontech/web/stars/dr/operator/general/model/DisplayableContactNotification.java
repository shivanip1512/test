package com.cannontech.web.stars.dr.operator.general.model;

import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.dr.general.model.OperatorContactNotificationType;

public class DisplayableContactNotification {

	private OperatorContactNotificationType operatorContactNotificationType;
	private LiteContactNotification notification;
	
	public DisplayableContactNotification(OperatorContactNotificationType operatorContactNotificationType, LiteContactNotification notification) {
		this.operatorContactNotificationType = operatorContactNotificationType;
		this.notification = notification;
	}
	
	public int getNotificationId() {
		return notification.getContactNotifID();
	}
	
	public OperatorContactNotificationType getOperatorContactNotificationType() {
		return operatorContactNotificationType;
	}
	
	public LiteContactNotification getNotification() {
		return notification;
	}
}
