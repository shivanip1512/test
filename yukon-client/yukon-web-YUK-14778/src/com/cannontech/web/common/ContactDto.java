package com.cannontech.web.common;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDto;
import com.cannontech.web.stars.dr.operator.model.ContactNotificationDtoSupplier;

public class ContactDto {
	
	private int contactId = 0;
	private String firstName;
	private String lastName;
	private String homePhone;
	private String workPhone;
	private String email;
	private List<ContactNotificationDto> otherNotifications = new LazyList<ContactNotificationDto>(new ArrayList<ContactNotificationDto>(), new ContactNotificationDtoSupplier());
	private boolean primary = false;
	
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getHomePhone() {
		return homePhone;
	}
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	public String getWorkPhone() {
		return workPhone;
	}
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<ContactNotificationDto> getOtherNotifications() {
		return otherNotifications;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
}