package com.cannontech.web.login;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;

public enum ChangeLoginMessage implements DisplayableEnum {
    LOGIN_USERNAME_CHANGED(FlashScopeMessageType.SUCCESS),
    LOGIN_PASSWORD_CHANGED(FlashScopeMessageType.SUCCESS),
    NO_CHANGE(FlashScopeMessageType.WARNING),
    NO_USERNAME_CHANGE(FlashScopeMessageType.WARNING),
    NO_PASSWORD_CHANGE(FlashScopeMessageType.WARNING),
    NO_PASSWORDMATCH(FlashScopeMessageType.ERROR),
    REQUIRED_FIELDS_MISSING(FlashScopeMessageType.ERROR),
    INVALID_CREDENTIALS_USERNAME_CHANGE(FlashScopeMessageType.ERROR),
    INVALID_CREDENTIALS_PASSWORD_CHANGE(FlashScopeMessageType.ERROR),
    USER_EXISTS(FlashScopeMessageType.ERROR),
    PASSWORD_CHANGE_NOT_SUPPORTED(FlashScopeMessageType.ERROR),
    INVALID_PASSWORD_LENGTH(FlashScopeMessageType.ERROR),
    MIN_PASSWORD_AGE_NOT_MET(FlashScopeMessageType.ERROR), 
    PASSWORD_USED_TOO_RECENTLY(FlashScopeMessageType.ERROR), 
    PASSWORD_DOES_NOT_MEET_POLICY_QUALITY(FlashScopeMessageType.ERROR),
    ;

    private static final String keyPrefix = "yukon.web.changelogin.message.";
    
    FlashScopeMessageType flashScopeMessageType;
    
    private ChangeLoginMessage(FlashScopeMessageType flashScopeMessageType) {
    	this.flashScopeMessageType = flashScopeMessageType;
    }
    
    public FlashScopeMessageType getFlashScopeMessageType() {
		return flashScopeMessageType;
	}
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

}
