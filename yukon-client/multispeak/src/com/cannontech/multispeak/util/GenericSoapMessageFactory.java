package com.cannontech.multispeak.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.TransportInputStream;

/**
 * SOAP message factory that support both version of SOAP (1.1 and 1.2).This
 * class uses the SoapProtocolChooser to check which version of SOAP is used.
 */
public class GenericSoapMessageFactory implements SoapMessageFactory {
    private static final String REQUEST_CONTEXT_ATTRIBUTE = "GenericSoapMessageFactory";

    /**
     * Factory for SOAP 1.1 messages
     */
    @Autowired @Qualifier("soap11") private SaajSoapMessageFactory soap11;
    /**
     * Factory for SOAP 1.2 messages
     */
    @Autowired @Qualifier("soap12") private SaajSoapMessageFactory soap12;
    /**
     * Chooses the version of SOAP protocol
     */
    @Autowired private SoapProtocolChooser soapProtocolChooser;
    
    /**
     * Set the SaajSoapMessageFactory for the request scope attribute (REQUEST_CONTEXT_ATTRIBUTE)
     */
    
    private void setMessageFactoryForRequestContext(SaajSoapMessageFactory factory) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        attributes.setAttribute(REQUEST_CONTEXT_ATTRIBUTE, factory, RequestAttributes.SCOPE_REQUEST);
    }
    
    /**
     * Return the SaajSoapMessageFactory for the request scope attribute (REQUEST_CONTEXT_ATTRIBUTE)
     */

    private SaajSoapMessageFactory getMessageFactoryForRequestContext() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (SaajSoapMessageFactory) attributes.getAttribute(REQUEST_CONTEXT_ATTRIBUTE,
                                                                RequestAttributes.SCOPE_REQUEST);
    }

    @Override
    public void setSoapVersion(SoapVersion version) {
        // ignore this, it will be set automatically
    }

    @Override
    public SoapMessage createWebServiceMessage() {
        return getMessageFactoryForRequestContext().createWebServiceMessage();
    }

    @Override
    public SoapMessage createWebServiceMessage(InputStream inputStream) throws IOException {
        boolean isMessageFactorySet = false;
        if (inputStream instanceof TransportInputStream) {
            TransportInputStream transportInputStream = (TransportInputStream) inputStream;
            if (soapProtocolChooser.useSoap12(transportInputStream)) {
                setMessageFactoryForRequestContext(soap12);
                isMessageFactorySet = true;
            }
        }
        if (!isMessageFactorySet) {
            setMessageFactoryForRequestContext(soap11);
        }
        SaajSoapMessageFactory mf = getMessageFactoryForRequestContext();
        return mf.createWebServiceMessage(inputStream);
    }

}