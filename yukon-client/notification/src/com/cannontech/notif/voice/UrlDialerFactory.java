package com.cannontech.notif.voice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.Maps;


public class UrlDialerFactory implements DialerFactory {
    private static final Logger log = YukonLogManager.getLogger(UrlDialerFactory.class);

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;

    @Override
    public Dialer createDialer(EnergyCompany energyCompany) {
        final LiteYukonUser ecUser = energyCompany.getUser();

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
                
                final int callTimeoutSeconds = globalSettingDao.getInteger(GlobalSettingType.CALL_RESPONSE_TIMEOUT);
                log.debug("callTimeoutSeconds: " + callTimeoutSeconds);
                
                final String dialPrefix = globalSettingDao.getString(GlobalSettingType.CALL_PREFIX);
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

                CloseableHttpClient httpClient = null;
                try {
                	
                	httpClient = HttpClients.custom().build();                 
                	HttpGet method = new HttpGet(urlStr);
                	HttpResponse httpResponse =  httpClient.execute(method);
                    
                	InputStream inputStream = httpResponse.getEntity().getContent();
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
                    log.trace(httpResponse.getAllHeaders());
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
                finally{
                	try {
						httpClient.close();
					} catch (IOException e) {
					    log.error(e.getMessage());
					}
                }
            }

            @Override
            public String toString() {
                return "UrlDialer";
            }
            
        };
    }
}
