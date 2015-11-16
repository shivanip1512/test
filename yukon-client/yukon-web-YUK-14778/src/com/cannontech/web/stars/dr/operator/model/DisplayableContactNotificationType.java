package com.cannontech.web.stars.dr.operator.model;

import com.cannontech.common.model.ContactNotificationType;

public class DisplayableContactNotificationType {
	
	private ContactNotificationType contactNotificationType;
	private String displayName;
	
	public DisplayableContactNotificationType(ContactNotificationType contactNotificationType, String displayName) {
		this.contactNotificationType = contactNotificationType;
		this.displayName = displayName;
	}
	
	public ContactNotificationType getContactNotificationType() {
		return contactNotificationType;
	}
	public String getDisplayName() {
		return displayName;
	}
}