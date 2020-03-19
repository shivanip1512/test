package com.cannontech.multispeak.client;

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

/**
 * This class intercepts the response from the third party web service and
 * set the context in message holder in handleResponse.
 */
public class ClientMessageContextHolderInterceptor extends LoggingInterceptor implements ClientInterceptor {

    @Override
    public boolean handleFault(MessageContext arg0) throws WebServiceClientException {
        return false;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {

        logMessageSource("Request: ", getSource(messageContext.getRequest()));

        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {

        logMessageSource("Response: ", getSource(messageContext.getResponse()));

        MessageContextHolder.setMessageContext(messageContext);

        return false;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception arg1) throws WebServiceClientException {
        
    }

}
