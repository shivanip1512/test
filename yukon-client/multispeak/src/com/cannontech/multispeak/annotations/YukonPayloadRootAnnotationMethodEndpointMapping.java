package com.cannontech.multispeak.annotations;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.mapping.AbstractAnnotationMethodEndpointMapping;
import org.springframework.ws.server.endpoint.support.PayloadRootUtils;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.google.common.collect.ImmutableMap;

/**
 * This Class extends the Spring provided AbstractAnnotationMethodEndpointMapping class to provide customized 
 * mapping of {urlpart+localPart} as key to {Endpoint Method Handlers} as values. The getLookupKeyForMethod() enables 
 * creating this customized mappings and is invoked during server startup by specialized bean of spring framework.
 * The getLookupKeyForMessage() is used to retrieve the Endpoint mapped method corresponding to the {urlpart+localPart}
 * as key coming in the request url.
 */
public class YukonPayloadRootAnnotationMethodEndpointMapping extends AbstractAnnotationMethodEndpointMapping<QName> {

    /**
     * The purpose of this enum to support the same endpoint for different url point to point or any other web service url) 
     * it is also building the map that contains the url and endPointAddress.
     */
    
    private static TransformerFactory transformerFactory;
    @Autowired private ConfigurationSource configurationSource;
    static {
        transformerFactory = TransformerFactory.newInstance();
    }

    private enum EndPointMapping {
        // MSP Version 3.0 support
        MR_CBSoap("MR_CBSoap", "/multispeak/v3/MR_Server"),
        OD_OASoap("OD_OASoap", "/multispeak/v3/OD_Server"),
        CD_CBSoap("CD_CBSoap", "/multispeak/v3/CD_Server"),
        MR_EASoap("MR_EASoap", "/multispeak/v3/MR_Server"),
        MR_ServerSoap("MR_ServerSoap", "/multispeak/v3/MR_Server"),
        CD_ServerSoap("CD_ServerSoap", "/multispeak/v3/CD_Server"),
        SCADA_ServerSoap("SCADA_ServerSoap", "/multispeak/v3/SCADA_Server"),
        OD_ServerSoap("OD_ServerSoap", "/multispeak/v3/OD_Server"),
        LM_ServerSoap("LM_ServerSoap", "/multispeak/v3/LM_Server"),
        
        // MSP Version 3.0 support to redirect new url to old url
        MR_ServerSoap_v3("v3/MR_Server", "/multispeak/v3/MR_Server"),
        CD_ServerSoap_v3("v3/CD_Server", "/multispeak/v3/CD_Server"),
        SCADA_ServerSoap_v3("v3/SCADA_Server", "/multispeak/v3/SCADA_Server"),
        OD_ServerSoap_v3("v3/OD_Server", "/multispeak/v3/OD_Server"),
        LM_ServerSoap_v3("v3/LM_Server", "/multispeak/v3/LM_Server"),
        
        // MSP Version 5.0 support
        CD_ServerSoap_v5("v5/CD_Server", "/multispeak/v5/CD_Server"),
        NOT_ServerSoap_v5("v5/NOT_Server", "/multispeak/v5/NOT_Server"),
        DR_ServerSoap_v5("v5/DR_Server", "/multispeak/v5/DR_Server"),
        SCADA_ServerSoap_v5("v5/SCADA_Server", "/multispeak/v5/SCADA_Server"),
        OD_ServerSoap_v5("v5/OD_Server", "/multispeak/v5/OD_Server"),
        MR_ServerSoap_v5("v5/MR_Server", "/multispeak/v5/MR_Server");
        
        private static final ImmutableMap<String, String> endPointMappingMap;
        private final String url;
        private final String endPointAddress;
        static {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for (EndPointMapping p2PMapping : values()) {
                builder.put(p2PMapping.url, p2PMapping.endPointAddress);
            }
            endPointMappingMap = builder.build();
        }

        private EndPointMapping(String url, String endPointAddress) {
            this.url = url;
            this.endPointAddress = endPointAddress;
        }

        public static String getLocationForEndPoint(String url) {
            return endPointMappingMap.get(url);
        }

    }

    @Override
    protected QName getLookupKeyForMessage(MessageContext messageContext) throws Exception {
        String urlPart = "";
        QName payloadRootPart = PayloadRootUtils.getPayloadRootQName(messageContext.getRequest().getPayloadSource(), transformerFactory);

        TransportContext transportContext = TransportContextHolder.getTransportContext();
        if (transportContext != null) {
            WebServiceConnection connection = transportContext.getConnection();
            if (connection != null && connection instanceof HttpServletConnection) {
                String requestURI = ((HttpServletConnection) connection).getHttpServletRequest().getRequestURI();
                String contextPath = ((HttpServletConnection) connection).getHttpServletRequest().getContextPath();
                String orginalUrlPart = requestURI.substring(contextPath.length() + 1);
                urlPart = EndPointMapping.getLocationForEndPoint(orginalUrlPart.substring(orginalUrlPart.indexOf("/") + 1));
            }
        }

        return new QName(payloadRootPart.getNamespaceURI(), urlPart + "/" + payloadRootPart.getLocalPart());
    }

    @Override
    protected QName getLookupKeyForMethod(Method method) {
        QName methodPart = null;
        RequestMapping rm = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequestMapping.class);
        String urlPart = rm == null || rm.value().length != 1 ? "" : rm.value()[0];
        PayloadRoot annotation = AnnotationUtils.findAnnotation(method, PayloadRoot.class);
        if (annotation != null) {

            if (StringUtils.hasLength(annotation.localPart()) && StringUtils.hasLength(annotation.namespace())) {
                methodPart = new QName(annotation.namespace(), urlPart + "/" + annotation.localPart());
            } else {
                methodPart = new QName(urlPart + "/" + annotation.localPart());
            }
        }
        return methodPart;
    }
    
    @Override
    protected void initApplicationContext() throws BeansException {

        if (!configurationSource.getBoolean(MasterConfigBoolean.MULTISPEAK_DISABLED, false)) {
            super.initApplicationContext();
        }
    }

}
