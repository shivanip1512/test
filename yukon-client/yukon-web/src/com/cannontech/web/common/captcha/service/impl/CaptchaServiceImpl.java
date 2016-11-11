package com.cannontech.web.common.captcha.service.impl;

import javax.annotation.PostConstruct;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaErrorCode;
import com.cannontech.web.common.captcha.model.CaptchaResponse;
import com.cannontech.web.common.captcha.service.CaptchaService;

public class CaptchaServiceImpl implements CaptchaService{
    
    private Logger log = YukonLogManager.getLogger(CaptchaServiceImpl.class);
    private String RECAPTCHA_PUBLIC_KEY;
    private String RECAPTCHA_PRIVATE_KEY;
    
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource configurationSource;
    
    @PostConstruct 
    public void init() {
        RECAPTCHA_PUBLIC_KEY =
            configurationSource.getString(MasterConfigString.RECAPTCHA_PUBLIC_KEY,
                "6LcLps0SAAAAAH-sva1H7pRvCWiDddeA0yO_CbHB");
        RECAPTCHA_PRIVATE_KEY =
            configurationSource.getString(MasterConfigString.RECAPTCHA_PRIVATE_KEY,
                "6LcLps0SAAAAAM40wM_-kRx-FCYeEA72XVpQwGl8");
 
        String httpProxy = globalSettingDao.getString(GlobalSettingType.HTTP_PROXY);
        boolean isCaptchasEnabled = globalSettingDao.getBoolean(GlobalSettingType.ENABLE_CAPTCHAS);
        if (isCaptchasEnabled && !httpProxy.equals("none")) {
            String[] hostAndPort = httpProxy.split(":");
            if (hostAndPort.length != 2) {
                log.error("GlobalSettingType = HTTP_PROXY has an invalid value: " + httpProxy
                    + ". Unable to setup proxy settings for Captcha service");
            } else {
                System.setProperty("http.proxyHost", hostAndPort[0]);
                System.setProperty("http.proxyPort", hostAndPort[1]);
            }
        }
    }
    
    @Override
    public CaptchaResponse checkCaptcha(Captcha captcha) {
        
        // The captcha service is currently turned off.  Return no errors since it's not being used.
        boolean isCaptchasEnabled = globalSettingDao.getBoolean(GlobalSettingType.ENABLE_CAPTCHAS);
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