package com.cannontech.web.common.captcha.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaErrorCode;
import com.cannontech.web.common.captcha.model.CaptchaResponse;
import com.cannontech.web.common.captcha.model.ReCaptchaResponse;
import com.cannontech.web.common.captcha.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CaptchaServiceImpl implements CaptchaService{
    
    private String RECAPTCHA_SITE_KEY;
    private String RECAPTCHA_SECRET_KEY;
    public static final String url = "https://www.google.com/recaptcha/api/siteverify";
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource configurationSource;
    
    @PostConstruct 
    public void init() {
        RECAPTCHA_SITE_KEY =
            configurationSource.getString(MasterConfigString.RECAPTCHA_PUBLIC_KEY,
                "6LfIVEkUAAAAAEtqdQcwZU8V6cYGKi0d0Yuz-ISZ");
        RECAPTCHA_SECRET_KEY =
            configurationSource.getString(MasterConfigString.RECAPTCHA_PRIVATE_KEY,
                "6LfIVEkUAAAAAFVpFbteZJ1QBP4Kq4Uzbg2udIhB");
    }
    
    @Override
    public CaptchaResponse checkCaptcha(Captcha captcha, boolean isCaptchasEnabled) {
        // The captcha service is currently turned off.  Return no errors since it's not being used.
        if (!isCaptchasEnabled) {
            return new CaptchaResponse(CaptchaErrorCode.NO_ERRORS);
        }
        ReCaptchaResponse reCaptchaResponse = callReCaptchaSiteVerifyService(captcha);
        return new CaptchaResponse(reCaptchaResponse);
    }

    public ReCaptchaResponse callReCaptchaSiteVerifyService(Captcha captcha) {
        ReCaptchaResponse reCaptchaResponse = null;
        try {
            CloseableHttpClient httpclient = buildHTTPClient();
            String siteVerifyServiceUrl = configurationSource.getString(MasterConfigString.RECAPTCHA_VERIFY_URL, url);
            HttpPost httppost = new HttpPost(siteVerifyServiceUrl);
            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("secret", RECAPTCHA_SECRET_KEY));
            params.add(new BasicNameValuePair("response", captcha.getResponse()));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            // Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                reCaptchaResponse = new ReCaptchaResponse();
                reCaptchaResponse.getErrorCodes().add("bad-request");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                // Map JSON to Object using Jackson Object Mapper.
                reCaptchaResponse = objectMapper.readValue(response.getEntity().getContent(), ReCaptchaResponse.class);
                if (reCaptchaResponse != null && reCaptchaResponse.isSuccess()
                    && !StringUtils.equals(reCaptchaResponse.getHostname(), captcha.getRemoteAddr())) {
                    reCaptchaResponse.getErrorCodes().add("invalid-host");
                }

            }

        } catch (Exception e) {
            reCaptchaResponse = new ReCaptchaResponse();
            reCaptchaResponse.getErrorCodes().add("bad-request");
        }
        return reCaptchaResponse;
    }

    @Override
    public String getSiteKey() {
        return RECAPTCHA_SITE_KEY;
    }

    private CloseableHttpClient buildHTTPClient() {
        CloseableHttpClient client = null;
        Optional<YukonHttpProxy> httpProxy = YukonHttpProxy.fromGlobalSetting(globalSettingDao);
        if (httpProxy.isPresent()) {
            HttpHost proxy = new HttpHost(httpProxy.get().getHost(), httpProxy.get().getPort());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            client = HttpClients.custom().setRoutePlanner(routePlanner).build();
        } else {
            client = HttpClients.custom().build();
        }
        return client;
    }
}