package com.cannontech.common.model;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.DisplayableEnum;

public enum ContactNotificationType implements DisplayableEnum {
	
	CALL_BACK_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_CALL_BACK_PHONE), 
    CELL_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_CELL_PHONE), 
    EMAIL(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL),
    EMAIL_TO_CELL(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_CELL),
    EMAIL_TO_PAGER(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_PAGER),
    FAX(YukonListEntryTypes.YUK_ENTRY_ID_FAX),
    HOME_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE),
    IVR_LOGIN(YukonListEntryTypes.YUK_ENTRY_ID_IVR_LOGIN),
    PHONE(YukonListEntryTypes.YUK_ENTRY_ID_PHONE),
	VOICE_PIN(YukonListEntryTypes.YUK_ENTRY_ID_PIN),
	WORK_PHONE(YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);

    private int definitionId;

    private ContactNotificationType(int definitionId) {
        this.definitionId = definitionId;
    }
    
    public int getDefinitionId() {
        return definitionId;
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
