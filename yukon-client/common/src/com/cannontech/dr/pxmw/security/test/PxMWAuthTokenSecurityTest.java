package com.cannontech.dr.pxmw.security.test;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.pxmw.authToken.message.PxMWAuthTokenRequest;
import com.cannontech.services.pxmw.authToken.message.PxMWAuthTokenResponse;

public class PxMWAuthTokenSecurityTest {
    private static final Logger log = YukonLogManager.getLogger(PxMWAuthTokenSecurityTest.class);

    @Autowired private ConfigurationSource configSource;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private RequestReplyTemplate<PxMWAuthTokenResponse> pXMWAuthTokenRequestTemplate;

    @PostConstruct
    public void init() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.PX_MW_AUTH_TOKEN);
        pXMWAuthTokenRequestTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.PX_MW_AUTH_TOKEN.getName(),
                configSource, jmsTemplate);
    }
    
    public String getAuthenticationToken() {
        String authToken = null;
        BlockingJmsReplyHandler<PxMWAuthTokenResponse> reply = new BlockingJmsReplyHandler<>(PxMWAuthTokenResponse.class);
        PxMWAuthTokenRequest request = new PxMWAuthTokenRequest();
        pXMWAuthTokenRequestTemplate.send(request, reply);
        try {
            PxMWAuthTokenResponse response = reply.waitForCompletion();
            authToken = response.getAuthToken();
            if (authToken != null) {
                log.debug("Successfully logged in");
            }
        } catch (Exception e) {
            log.debug("Error getting authToken", e);
        }
        return authToken;
    }

}