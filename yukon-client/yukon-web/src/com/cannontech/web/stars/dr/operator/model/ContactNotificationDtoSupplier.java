package com.cannontech.web.stars.dr.operator.model;

import com.google.common.base.Supplier;

public class ContactNotificationDtoSupplier implements Supplier<ContactNotificationDto> {
	
	@Override
	public ContactNotificationDto get() {
		return new ContactNotificationDto();
	}
}