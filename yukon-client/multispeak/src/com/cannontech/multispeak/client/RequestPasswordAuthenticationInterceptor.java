package com.cannontech.multispeak.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.client.v5.UserDetailHolder;

/**
 * This class is used to intercept the request and validate the username/password.
 */
public class RequestPasswordAuthenticationInterceptor implements EndpointInterceptor {

    @Autowired MultispeakFuncs multispeakFuncs;

    @Override
    public void afterCompletion(MessageContext arg0, Object endpoint, Exception arg2) throws Exception {
        // Do nothhin

    }

    @Override
    public boolean handleFault(MessageContext arg0, Object endpoint) throws Exception {
        if (multispeakFuncs.getMSPVersion() == MultiSpeakVersion.V5) {
            UserDetailHolder.removeYukonUser();
        }
        return true;
    }

    @Override
    public boolean handleRequest(MessageContext arg0, Object endpoint) throws Exception {
        if (multispeakFuncs.getMSPVersion() == MultiSpeakVersion.V5) {
            LiteYukonUser user = multispeakFuncs.authenticateMsgHeader();
            UserDetailHolder.setYukonUser(user);
        }
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext arg0, Object endpoint) throws Exception {
        if (multispeakFuncs.getMSPVersion() == MultiSpeakVersion.V5) {
            UserDetailHolder.removeYukonUser();
        }
        return true;
    }

}
