package com.cannontech.notif.voice;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Maps;


public class UrlDialerFactory implements DialerFactory {
    private @Autowired RolePropertyDao rolePropertyDao;
    private @Autowired YukonUserDao yukonUserDao;
    private @Autowired ConfigurationSource configurationSource;
    
    private static final Logger log = YukonLogManager.getLogger(UrlDialerFactory.class);

    @Override
    public Dialer createDialer(LiteEnergyCompany energyCompany) {
        final LiteYukonUser ecUser = yukonUserDao.getLiteYukonUser(energyCompany.getUserID());

        int retryCount = configurationSource.getInteger("IVR_URL_DIALER_RETRY_COUNT", 3);
        int retryDelayMs = configurationSource.getInteger("IVR_URL_DIALER_RETRY_DELAY_MS", 4000);
        int postCallSleepMs = configurationSource.getInteger("IVR_URL_DIALER_POST_CALL_SLEEP_MS", 4000);
        
        // increment retryCount by one to get the "try" count
        return new Dialer(retryDelayMs, retryCount+1, postCallSleepMs) {

            @Override
            protected void dialCall(Call call) {
                // get configuration from role properties
                final String urlTemplate = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.IVR_URL_DIALER_TEMPLATE, ecUser);
                log.debug("template: " + urlTemplate);
                
                final int callTimeoutSeconds = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.CALL_RESPONSE_TIMEOUT, null);
                log.debug("callTimeoutSeconds: " + callTimeoutSeconds);
                
                final String dialPrefix = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.CALL_PREFIX, null);
                log.debug("dialPrefix: " + dialPrefix);
                
                final String successMatcherStr = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.IVR_URL_DIALER_SUCCESS_MATCHER, ecUser);
                Pattern successPattern;
                try {
                    successPattern = Pattern.compile(successMatcherStr);
                } catch (Exception e1) {
                    successPattern = Pattern.compile(".*");
                }
                log.debug("success pattern: " + successPattern);

                call.newAttempt();
                Map<String,String> callParameters = Maps.newHashMap();
                callParameters.putAll(call.getCallParameters());
                String phoneNumber = dialPrefix + call.getNumber().getPhoneNumber();
                callParameters.put("PHONE_NUMBER", phoneNumber);
                
                SimpleTemplateProcessor templateProcessor = new SimpleTemplateProcessor();
                String urlStr = templateProcessor.process(urlTemplate, callParameters);
                log.debug("Initiating call: " + urlStr);
                try {
                    
                    HttpClient httpClient = new HttpClient();
                    HttpMethod method = new GetMethod(urlStr);
                    httpClient.executeMethod(method);
                    InputStream inputStream = method.getResponseBodyAsStream();
                    byte[] inputBuffer = new byte[2000];
                    int bytesRead = inputStream.read(inputBuffer);
                    String response;
                    if (bytesRead < 0) {
                        log.debug("encountered EOF reading URL response, treating as blank");
                        response = "";
                    } else {
                        response = new String(inputBuffer, 0, bytesRead, Charset.forName("UTF-8"));
                    }
                    
                    
                    log.debug("URL response: " + StringUtils.left(response, 200));
                    log.trace(method.getResponseHeaders());
                    log.trace(response);

                    method.releaseConnection();
                    
                    Matcher successMatcher = successPattern.matcher(response);
                    if (!successMatcher.matches()) {
                        call.handleConnectionFailed("URL did not match success: " + response);
                        return;
                    }
                    
                    // wait for external signal for line state
                    boolean normalDisconnect = call.waitForLineToClear(callTimeoutSeconds);
                    if (!normalDisconnect) {
                        log.info("call timed out before disconnect: " + this);
                    }
                    
                } catch (Exception e) {
                    log.debug("Unable to initiate call", e);
                    call.handleConnectionFailed("unknown dialing exception: " + e.getMessage());
                }
            }

            @Override
            public String toString() {
                return "UrlDialer";
            }
            
        };
    }
}
