package com.cannontech.web.common.captcha.service.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaErrorCode;
import com.cannontech.web.common.captcha.model.CaptchaResponse;
import com.cannontech.web.common.captcha.model.ReCaptchaResponse;
import com.cannontech.web.common.captcha.service.CaptchaService;
import com.google.common.base.Strings;

public class CaptchaServiceImpl implements CaptchaService{
    
    private String RECAPTCHA_SITE_KEY;
    private String RECAPTCHA_SECRET_KEY;
    public static final String url = "https://www.google.com/recaptcha/api/siteverify";
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private @Qualifier("captcha") RestOperations restTemplate;
    private static final Logger log = YukonLogManager.getLogger(CaptchaServiceImpl.class);
    
    @PostConstruct 
    public void init() {
        RECAPTCHA_SITE_KEY =
            configurationSource.getString(MasterConfigString.RECAPTCHA_SITE_KEY,
                "6LfIVEkUAAAAAEtqdQcwZU8V6cYGKi0d0Yuz-ISZ");
        RECAPTCHA_SECRET_KEY =
            configurationSource.getString(MasterConfigString.RECAPTCHA_SECRET_KEY,
                "6LfIVEkUAAAAAFVpFbteZJ1QBP4Kq4Uzbg2udIhB");
    }
    
    @Override
    public CaptchaResponse checkCaptcha(Captcha captcha) {
        // The captcha service is currently turned off.  Return no errors since it's not being used.
        boolean isCaptchasEnabled = globalSettingDao.getBoolean(GlobalSettingType.ENABLE_CAPTCHAS);
        if (!isCaptchasEnabled) {
            return new CaptchaResponse(CaptchaErrorCode.NO_ERRORS);
        } else if (isCaptchasEnabled && Strings.isNullOrEmpty(captcha.getResponse())) {
            return new CaptchaResponse(CaptchaErrorCode.MISSING_INPUT_RESPONSE);
        }
        ReCaptchaResponse reCaptchaResponse = callReCaptchaSiteVerifyService(captcha);
        return new CaptchaResponse(reCaptchaResponse);
    }

    public ReCaptchaResponse callReCaptchaSiteVerifyService(Captcha captcha) {
        ReCaptchaResponse reCaptchaResponse = null;
        try {
            String siteVerifyServiceUrl = configurationSource.getString(MasterConfigString.RECAPTCHA_VERIFY_URL, url);
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
            paramMap.add("secret", RECAPTCHA_SECRET_KEY);
            paramMap.add("response", captcha.getResponse());

            HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<MultiValueMap<String, String>>(paramMap, headers);

            ResponseEntity<ReCaptchaResponse> responseEntity =
                restTemplate.postForEntity(siteVerifyServiceUrl, request, ReCaptchaResponse.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                reCaptchaResponse = responseEntity.getBody();
                if (CollectionUtils.isNotEmpty(reCaptchaResponse.getErrorCodes())) {
                    log.info("CaptchaErrorCode received from reCAPTCHA : " + reCaptchaResponse.getErrorCodes().get(0));
                }
                if (reCaptchaResponse != null && reCaptchaResponse.isSuccess()
                    && !StringUtils.equals(reCaptchaResponse.getHostname(), captcha.getRemoteAddr())) {
                    reCaptchaResponse.getErrorCodes().add("invalid-host");
                }
            } else {
                reCaptchaResponse = new ReCaptchaResponse();
                reCaptchaResponse.getErrorCodes().add("bad-request");
            }
        } catch (RestClientException e) {
            reCaptchaResponse = new ReCaptchaResponse();
            reCaptchaResponse.getErrorCodes().add("bad-request");
        }
        return reCaptchaResponse;
    }

    @Override
    public String getSiteKey() {
        return RECAPTCHA_SITE_KEY;
    }

}