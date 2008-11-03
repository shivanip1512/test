package com.cannontech.yukon.api.util;

import java.lang.reflect.Method;

import javax.xml.transform.Source;

import org.jdom.Element;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.adapter.AbstractMethodEndpointAdapter;

public class JdomMethodEndpointAdapter extends AbstractMethodEndpointAdapter {

    protected boolean supportsInternal(MethodEndpoint methodEndpoint) {
        Method method = methodEndpoint.getMethod();
        return (Void.TYPE.isAssignableFrom(method.getReturnType()) ||
                Element.class.isAssignableFrom(method.getReturnType())) && method.getParameterTypes().length == 1 &&
                Element.class.isAssignableFrom(method.getParameterTypes()[0]);

    }

    protected void invokeInternal(MessageContext messageContext, MethodEndpoint methodEndpoint) throws Exception {
        Source requestSource = messageContext.getRequest().getPayloadSource();
        Element requestElement = null;
        if (requestSource != null) {
            JDOMResult jdomResult = new JDOMResult();
            transform(requestSource, jdomResult);
            requestElement = jdomResult.getDocument().getRootElement();
        }
        Object result = methodEndpoint.invoke(new Object[]{requestElement});
        if (result != null) {
            Element responseElement = (Element) result;
            WebServiceMessage response = messageContext.getResponse();
            transform(new JDOMSource(responseElement), response.getPayloadResult());
        }
    }
}
