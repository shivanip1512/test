package com.cannontech.multispeak.annotations;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

/**
 * This Class extends the Spring provided PayloadRootAnnotationMethodEndpointMapping class to provide customized 
 * mapping of {urlpart+localPart} as key to {Endpoint Method Handlers} as values. The getLookupKeyForMethod() enables 
 * creating this customized mappings and is invoked during server startup by specialized bean of spring framework.
 * The getLookupKeyForMessage() is used to retrieve the Endpoint mapped method corresponding to the {urlpart+localPart}
 * as key coming in the request url.
 */
public class YukonPayloadRootAnnotationMethodEndpointMapping extends PayloadRootAnnotationMethodEndpointMapping {
    
    @Override
    protected QName getLookupKeyForMessage(MessageContext messageContext) throws Exception {
        String urlPart = "";
        QName payloadRootPart = super.getLookupKeyForMessage(messageContext);

        TransportContext transportContext = TransportContextHolder.getTransportContext();
        if (transportContext != null) {
            WebServiceConnection connection = transportContext.getConnection();
            if (connection != null && connection instanceof HttpServletConnection) {
                String requestURI = ((HttpServletConnection) connection).getHttpServletRequest().getRequestURI();
                String contextPath = ((HttpServletConnection) connection).getHttpServletRequest().getContextPath();
                urlPart = requestURI.substring(contextPath.length());
            }
        }

        return new QName(payloadRootPart.getNamespaceURI(), urlPart + "/" + payloadRootPart.getLocalPart());
    }

    @Override
    protected QName getLookupKeyForMethod(Method method) {
        RequestMapping rm = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequestMapping.class);
        String urlPart = rm == null || rm.value().length != 1 ? "" : rm.value()[0];
        QName methodPart = super.getLookupKeyForMethod(method);
        return new QName(methodPart.getNamespaceURI(), urlPart + "/" + methodPart.getLocalPart());
    }
}
