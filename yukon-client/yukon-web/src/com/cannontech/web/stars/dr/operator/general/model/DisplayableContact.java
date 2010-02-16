package com.cannontech.web.stars.dr.operator.general.model;

import java.util.List;

import com.cannontech.database.data.lite.LiteContact;

public class DisplayableContact implements Comparable<DisplayableContact> {

	private LiteContact contact;
	private List<DisplayableContactNotification> otherContactNotifications;
	private DisplayableContactNotification firstHomePhoneNotification = null;
	private DisplayableContactNotification firstWorkPhoneNotification = null;
	private DisplayableContactNotification firstEmailNotification = null;
	private boolean primary;
	
	public DisplayableContact(LiteContact contact, 
							  DisplayableContactNotification firstHomePhoneNotification,
							  DisplayableContactNotification firstWorkPhoneNotification,
							  DisplayableContactNotification firstEmailNotification,
							  List<DisplayableContactNotification> otherContactNotifications, 
							  boolean isPrimary) {
		this.contact = contact;
		this.firstHomePhoneNotification = firstHomePhoneNotification;
		this.firstWorkPhoneNotification = firstWorkPhoneNotification;
		this.firstEmailNotification = firstEmailNotification;
		this.otherContactNotifications = otherContactNotifications;
		this.primary = isPrimary;
	}
	
	public LiteContact getContact() {
		return contact;
	}
	
	public String getFirstName() {
		return contact.getContFirstName();
	}

	public String getLastName() {
		return contact.getContLastName();
	}
	
	public DisplayableContactNotification getFirstHomePhoneNotification() {
		return firstHomePhoneNotification;
	}
	
	public DisplayableContactNotification getFirstWorkPhoneNotification() {
		return firstWorkPhoneNotification;
	}
	
	public DisplayableContactNotification getFirstEmailNotification() {
		return firstEmailNotification;
	}
	
	public List<DisplayableContactNotification> getOtherContactNotifications() {
		return otherContactNotifications;
	}
	
	public boolean isPrimary() {
		return primary;
	}
	
	public int getContactId() {
		return contact.getContactID();
	}
	
	@Override
	public int compareTo(DisplayableContact o) {
		
		return this.getFirstName().compareTo(o.getLastName());
	}
}
