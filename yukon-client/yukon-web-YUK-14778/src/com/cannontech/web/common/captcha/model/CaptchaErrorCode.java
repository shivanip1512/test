package com.cannontech.web.common.captcha.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CaptchaErrorCode implements DisplayableEnum{
    NO_ERRORS(true),
    INVALID_SITE_PRIVATE_KEY(false),
    INVALID_REQUEST_COOKIE(false),
    INCORRECT_CAPTCHA_SOL(false),
    RECAPTCH_NOT_RECHABLE(false);
    
    private boolean valid;
    private static final String displayKeyPrefix = "yukon.web.captcha.captchaErrorCode.";
    
    private CaptchaErrorCode(boolean valid) {
        this.valid = valid;
    }
    
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public String getFormatKey() {
        return displayKeyPrefix + name();
    }
    
    public static CaptchaErrorCode getByCaptchaResponse(String captchaResponse) {
        if (captchaResponse == null) {
            return NO_ERRORS;
        }
        
        String captchaResponseUpper = captchaResponse.toUpperCase().replaceAll("-", "_");
        return valueOf(captchaResponseUpper);
    }
}