package com.cannontech.multispeak.util;

import java.io.IOException;
import java.util.Iterator;

import org.springframework.ws.transport.TransportInputStream;

public class SoapProtocolChooserImpl implements SoapProtocolChooser {

    private static final String CONTENT_TYPE_HEADER_NAME = "content-type";
    private static final String CONTENT_TYPE_HEADER_CONTENT_SOAP_11 = "text/xml";
    private static final String CONTENT_TYPE_HEADER_CONTENT_SOAP_12 = "application/soap+xml";

    @Override
    public boolean useSoap11(TransportInputStream transportInputStream) throws IOException {
        return useSoap(transportInputStream, CONTENT_TYPE_HEADER_CONTENT_SOAP_11);
    }

    @Override
    public boolean useSoap12(TransportInputStream transportInputStream) throws IOException {
        return useSoap(transportInputStream, CONTENT_TYPE_HEADER_CONTENT_SOAP_12);
    }

    /**
     * Method is used to compare content-type attribute of SOAP 1.2 & 1.1
     * version
     */
    private boolean useSoap(TransportInputStream transportInputStream, String contentType) throws IOException {

        for (Iterator headerNames = transportInputStream.getHeaderNames(); headerNames.hasNext();) {
            String headerName = (String) headerNames.next();
            if (headerName.toLowerCase().contains(CONTENT_TYPE_HEADER_NAME)) {
                for (Iterator headerValues = transportInputStream.getHeaders(headerName); headerValues.hasNext();) {
                    String headerValue = (String) headerValues.next();
                    if (headerValue.trim().toLowerCase().contains(contentType)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

}