package com.cannontech.core.authentication.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * These values should match up with the ChangeLoginMessage enum until we can redo that page.
 */
public enum PasswordPolicyError implements DisplayableEnum {
    MIN_PASSWORD_LENGTH_NOT_MET, 
    MAX_PASSWORD_LENGTH_EXCEEDED, 
    MIN_PASSWORD_AGE_NOT_MET, 
    PASSWORD_USED_TOO_RECENTLY, 
    PASSWORD_DOES_NOT_MEET_POLICY_QUALITY;

    private final static String keyPrefix = "yukon.web.modules.passwordPolicyError.";

    @Override
    public String getFormatKey() {
        return keyPrefix+name();
    }
}