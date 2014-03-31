package com.cannontech.common.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

public enum ContactNotificationType implements DisplayableEnum, DatabaseRepresentationSource {

    CALL_BACK_PHONE(10, ContactNotificationMethodType.PHONE), 
    CELL_PHONE(8, ContactNotificationMethodType.PHONE), 
    EMAIL(1, ContactNotificationMethodType.EMAIL),
    EMAIL_TO_CELL(9, ContactNotificationMethodType.SHORT_EMAIL),
    EMAIL_TO_PAGER(3, ContactNotificationMethodType.SHORT_EMAIL),
    FAX(4, ContactNotificationMethodType.FAX),
    HOME_PHONE(5, ContactNotificationMethodType.PHONE),
    IVR_LOGIN(11, ContactNotificationMethodType.PIN),
    PHONE(2, ContactNotificationMethodType.PHONE),
	VOICE_PIN(7, ContactNotificationMethodType.PIN),
	WORK_PHONE(6, ContactNotificationMethodType.PHONE);

    private int definitionId;
    private ContactNotificationMethodType contactNotificationMethodType;

    private final static ImmutableMultimap<ContactNotificationMethodType, ContactNotificationType> lookup;
    
    static {
        Builder<ContactNotificationMethodType, ContactNotificationType> builder = ImmutableMultimap.builder();
        for (ContactNotificationType notificationType : ContactNotificationType.values()) {
            builder.put(notificationType.getContactNotificationMethodType(), notificationType);
        }
        lookup = builder.build();
    }
    
    private ContactNotificationType(int definitionId, ContactNotificationMethodType contactNotificationMethodType) {
        this.definitionId = definitionId;
        this.contactNotificationMethodType = contactNotificationMethodType;
    }
    
    public static ImmutableCollection<ContactNotificationType> getFor(ContactNotificationMethodType methodType) {
        return lookup.get(methodType);
    }
    
    public int getDefinitionId() {
        return definitionId;
    }
    public ContactNotificationMethodType getContactNotificationMethodType() {
		return contactNotificationMethodType;
	}
    public boolean isPhoneType() {
    	return this.getContactNotificationMethodType() == ContactNotificationMethodType.PHONE;
    }
    public boolean isFaxType() {
    	return this.getContactNotificationMethodType() == ContactNotificationMethodType.FAX;
	}
    public boolean isEmailType() {
    	return this.getContactNotificationMethodType() == ContactNotificationMethodType.EMAIL;
    }
    public boolean isShortEmailType() {
    	return this.getContactNotificationMethodType() == ContactNotificationMethodType.SHORT_EMAIL;
    }
    public boolean isPinType() {
    	return this.getContactNotificationMethodType() == ContactNotificationMethodType.PIN;
    }

    public static ContactNotificationType getTypeForNotificationCategoryId(int notificationCategoryId) {
    	
    	ContactNotificationType contactNotificationType = null;
    	
    	ContactNotificationType[] types = ContactNotificationType.values();
    	for (ContactNotificationType type : types) {
    		if (type.getDefinitionId() == notificationCategoryId) {
    			contactNotificationType =  type;
    		}
    	}
    	
    	if (contactNotificationType == null) {
    		throw new IllegalArgumentException("Invalid notificationCategoryId. No matching ContactNotificationType for notificationCategoryId (" + notificationCategoryId + ").");
    	}
    	
    	return contactNotificationType;
    }
    
    @Override
    public String getFormatKey() {
    	return "yukon.web.modules.operator.contactNotificationEnum." + this.toString();
    }
    
    @Override
    public Object getDatabaseRepresentation() {
    	return getDefinitionId();
    }
}
