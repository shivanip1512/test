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
import com.google.common.collect.ImmutableMap;

/**
 * This Class extends the Spring provided PayloadRootAnnotationMethodEndpointMapping class to provide customized 
 * mapping of {urlpart+localPart} as key to {Endpoint Method Handlers} as values. The getLookupKeyForMethod() enables 
 * creating this customized mappings and is invoked during server startup by specialized bean of spring framework.
 * The getLookupKeyForMessage() is used to retrieve the Endpoint mapped method corresponding to the {urlpart+localPart}
 * as key coming in the request url.
 */
public class YukonPayloadRootAnnotationMethodEndpointMapping extends PayloadRootAnnotationMethodEndpointMapping {

    private enum P2PMapping {
        MR_CBSoap("MR_CBSoap", "/soap/MR_ServerSoap"),

        OD_OASoap("OD_OASoap", "/soap/OD_ServerSoap"),

        CD_CBSoap("CD_CBSoap", "/soap/CD_ServerSoap"),

        MR_EASoap("MR_EASoap", "/soap/MR_ServerSoap"),

        MR_ServerSoap("MR_ServerSoap", "/soap/MR_ServerSoap"),

        CD_ServerSoap("CD_ServerSoap", "/soap/CD_ServerSoap"),

        SCADA_ServerSoap("SCADA_ServerSoap", "/soap/SCADA_ServerSoap"),

        OD_ServerSoap("OD_ServerSoap", "/soap/OD_ServerSoap"),

        LM_ServerSoap("LM_ServerSoap", "/soap/LM_ServerSoap");

        private static final ImmutableMap<String, String> endPointyMapping;
        private final String url;
        private final String endPointAddress;
        static {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for (P2PMapping p2PMapping : values()) {
                builder.put(p2PMapping.url, p2PMapping.endPointAddress);
            }
            endPointyMapping = builder.build();
        }

        private P2PMapping(String url, String endPointAddress) {
            this.url = url;
            this.endPointAddress = endPointAddress;
        }

        public static String getLocationForEndPoint(String url) {
            return endPointyMapping.get(url);
        }

    }

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
                String orginalUrlPart = requestURI.substring(contextPath.length());
                urlPart = P2PMapping.getLocationForEndPoint(orginalUrlPart.substring(orginalUrlPart.lastIndexOf("/")+1));
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
