package com.cannontech.common.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.DisplayableEnum;

public enum ContactNotificationType implements DisplayableEnum {
	
	CALL_BACK_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_CALL_BACK_PHONE, true), 
    CELL_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_CELL_PHONE, true), 
    EMAIL(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL, false),
    EMAIL_TO_CELL(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_CELL, true),
    EMAIL_TO_PAGER(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_PAGER, true),
    FAX(YukonListEntryTypes.YUK_ENTRY_ID_FAX, true),
    HOME_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, true),
    IVR_LOGIN(YukonListEntryTypes.YUK_ENTRY_ID_IVR_LOGIN, false),
    PHONE(YukonListEntryTypes.YUK_ENTRY_ID_PHONE, true),
	VOICE_PIN(YukonListEntryTypes.YUK_ENTRY_ID_PIN, false),
	WORK_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE, true);

    private int definitionId;
    private boolean phoneType;

    private ContactNotificationType(int definitionId, boolean phoneType) {
        this.definitionId = definitionId;
        this.phoneType = phoneType;
    }
    
    public int getDefinitionId() {
        return definitionId;
    }
    public boolean isPhoneType() {
    	return this.phoneType;
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
}
