package com.cannontech.common.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ContactNotificationType implements DisplayableEnum, DatabaseRepresentationSource {
	
	CALL_BACK_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_CALL_BACK_PHONE, ContactNotificationMethodType.PHONE), 
    CELL_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_CELL_PHONE, ContactNotificationMethodType.PHONE), 
    EMAIL(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL, ContactNotificationMethodType.EMAIL),
    EMAIL_TO_CELL(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_CELL, ContactNotificationMethodType.SHORT_EMAIL),
    EMAIL_TO_PAGER(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_PAGER, ContactNotificationMethodType.SHORT_EMAIL),
    FAX(YukonListEntryTypes.YUK_ENTRY_ID_FAX, ContactNotificationMethodType.FAX),
    HOME_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, ContactNotificationMethodType.PHONE),
    IVR_LOGIN(YukonListEntryTypes.YUK_ENTRY_ID_IVR_LOGIN, ContactNotificationMethodType.PIN),
    PHONE(YukonListEntryTypes.YUK_ENTRY_ID_PHONE, ContactNotificationMethodType.PHONE),
	VOICE_PIN(YukonListEntryTypes.YUK_ENTRY_ID_PIN, ContactNotificationMethodType.PIN),
	WORK_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE, ContactNotificationMethodType.PHONE);

    private int definitionId;
    private ContactNotificationMethodType contactNotificationMethodType;

    private ContactNotificationType(int definitionId, ContactNotificationMethodType contactNotificationMethodType) {
        this.definitionId = definitionId;
        this.contactNotificationMethodType = contactNotificationMethodType;
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
