package com.cannontech.web.common.captcha.model;

import net.tanesha.recaptcha.ReCaptchaResponse;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public class CaptchaResponse {
    
    private CaptchaErrorCode error;
    
    public CaptchaResponse(ReCaptchaResponse reCaptchaResponse) {
        this.error = CaptchaErrorCode.getByCaptchaResponse(reCaptchaResponse.getErrorMessage());
    }

    public CaptchaErrorCode getError() {
        return error;
    }
    
    public MessageSourceResolvable getErrorMessageSourceResolvable() {
        return new DefaultMessageSourceResolvable(error.getFormatKey());
    }

    public boolean isError() {
        return !error.isValid();
    }
}