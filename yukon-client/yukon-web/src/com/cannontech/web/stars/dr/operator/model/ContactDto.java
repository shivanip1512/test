package com.cannontech.web.stars.dr.operator.model;

import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class ContactDto {
	
	private int contactId = 0;
	private String firstName;
	private String lastName;
	private String homePhone;
	private String workPhone;
	private String email;
	private List<ContactNotificationDto> otherNotifications = LazyList.decorate(Lists.newArrayList(), FactoryUtils.instantiateFactory(ContactNotificationDto.class));
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
	public void setOtherNotifications(
			List<ContactNotificationDto> otherNotifications) {
		this.otherNotifications = otherNotifications;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
}