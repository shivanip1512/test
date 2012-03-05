package com.cannontech.web.common.captcha.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaErrorCode;
import com.cannontech.web.common.captcha.model.CaptchaResponse;
import com.cannontech.web.common.captcha.service.CaptchaService;

public class CaptchaServiceImpl implements CaptchaService{
    
    public static final String RECAPTCHA_PUBLIC_KEY = "6LcLps0SAAAAAH-sva1H7pRvCWiDddeA0yO_CbHB";
    private static final String RECAPTCHA_PRIVATE_KEY = "6LcLps0SAAAAAM40wM_-kRx-FCYeEA72XVpQwGl8";
    
    @Autowired private RolePropertyDao rolePropertyDao;
    
    @Override
    public CaptchaResponse checkCaptcha(Captcha captcha) {
        
        // The captcha service is corrently turned off.  Return no errors since it's not being used.
        boolean isCaptchasEnabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ENABLE_CAPTCHAS, null);
        if (!isCaptchasEnabled) {
            return new CaptchaResponse(CaptchaErrorCode.NO_ERRORS);
        }
        
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(RECAPTCHA_PRIVATE_KEY);

        ReCaptchaResponse reCaptchaResponse = 
                reCaptcha.checkAnswer(captcha.getRemoteAddr(), captcha.getChallenge(), captcha.getResponse());
        
        return new CaptchaResponse(reCaptchaResponse);
    }

    @Override
    public String getPublicKey() {
        return RECAPTCHA_PUBLIC_KEY;
    }
}