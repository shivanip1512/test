package com.cannontech.multispeak.client;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;

/**
 * This class is used to intercept the request and store its message context.
 */
public class MessageContextHolderInterceptor implements EndpointInterceptor {

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) {
        MessageContextHolder.setMessageContext(messageContext);
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) {
        MessageContextHolder.removeMessageContext();
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) {
        MessageContextHolder.removeMessageContext();
        return true;
    }

    @Override
    public void afterCompletion(MessageContext arg0, Object arg1, Exception arg2) throws Exception {
        // Do nothhin
    }

}
