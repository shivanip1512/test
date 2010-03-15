package com.cannontech.web.stars.dr.operator.model;

import com.cannontech.common.model.ContactNotificationType;

public class ContactNotificationDto {
	
	private int notificationId = 0;
	private ContactNotificationType contactNotificationType;
	private String notificationValue;
	
	public int getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}
	
	public ContactNotificationType getContactNotificationType() {
		return contactNotificationType;
	}
	public void setContactNotificationType(
			ContactNotificationType contactNotificationType) {
		this.contactNotificationType = contactNotificationType;
	}
	public String getNotificationValue() {
		return notificationValue;
	}
	public void setNotificationValue(String notificationValue) {
		this.notificationValue = notificationValue;
	}
}