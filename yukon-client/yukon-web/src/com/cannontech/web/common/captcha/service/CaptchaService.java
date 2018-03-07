package com.cannontech.web.common.captcha.service;

import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaResponse;

public interface CaptchaService {
    
    /**
     * This method checks the Captcha supplied and returns a CaptchaResponse instance that contains whether it was successful or not.
     */
    public CaptchaResponse checkCaptcha(Captcha captcha, boolean isCaptchasEnabled);

    /**
     * This method returns the Site key needed to create a Captcha.
     */
    public String getSiteKey();

}