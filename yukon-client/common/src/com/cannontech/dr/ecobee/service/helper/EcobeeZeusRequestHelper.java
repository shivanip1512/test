package com.cannontech.dr.ecobee.service.helper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenResponse;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class EcobeeZeusRequestHelper {

    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusRequestHelper.class);

    @Autowired private ConfigurationSource configSource;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private RestTemplate restTemplate;
    private RequestReplyTemplate<ZeusEcobeeAuthTokenResponse> zeusEcobeeAuthTokenRequestTemplate;

    public EcobeeZeusRequestHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.ZEUS_ECOBEE_AUTH_TOKEN);
        zeusEcobeeAuthTokenRequestTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.ZEUS_ECOBEE_AUTH_TOKEN.getName(),
                configSource, jmsTemplate);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
            factory.setProxy(httpProxy.getJavaHttpProxy());
        });
        restTemplate.setRequestFactory(factory);
    }

    /**
     * Returns the ResponseEntity object after making appropriated call using the provided URL, HTTP method & Response type.
     */
    public ResponseEntity<? extends Object> callEcobeeAPIForObject(String url, HttpMethod method,
            Class<? extends Object> responseType, Object... requestObject) throws RestClientException, EcobeeAuthenticationException {
        HttpEntity<Object> requestEntity = getRequestEntity(requestObject);
        ResponseEntity<? extends Object> response = restTemplate.exchange(url, method, requestEntity, responseType);
        return response;
    }

    /**
     * Return HttpEntity after adding Authorization bearer token for Ecobee API communications.
     * @param requestObject 
     */
    private HttpEntity<Object> getRequestEntity(Object[] requestObject) throws EcobeeAuthenticationException {
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.set("Authorization", "Bearer " + getAuthenticationToken());
        HttpEntity<Object> requestEntity = null;
        if (requestObject.length == 1) {
            requestEntity = new HttpEntity<>(requestObject[0], newheaders);
        } else {
            requestEntity = new HttpEntity<>(newheaders);
        }
        return requestEntity;
    }

    /**
     * Get Authentication token from service manager over JMS Queue.
     */
    private String getAuthenticationToken() throws EcobeeAuthenticationException {
        String authToken = null;
        BlockingJmsReplyHandler<ZeusEcobeeAuthTokenResponse> reply = new BlockingJmsReplyHandler<>(
                ZeusEcobeeAuthTokenResponse.class);
        ZeusEcobeeAuthTokenRequest request = new ZeusEcobeeAuthTokenRequest();
        zeusEcobeeAuthTokenRequestTemplate.send(request, reply);
        try {
            ZeusEcobeeAuthTokenResponse response = reply.waitForCompletion();
            authToken = response.getAuthToken();
            if (authToken != null) {
                log.debug("Successfully logged in");
            }
        } catch (Exception e) {
            log.error("Error getting authToken", e);
            throw new EcobeeAuthenticationException("Unable to receive authToken message");
        }
        return authToken;
    }

}
