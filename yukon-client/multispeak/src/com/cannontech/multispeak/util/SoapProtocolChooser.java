package com.cannontech.multispeak.util;

import java.io.IOException;

import org.springframework.ws.transport.TransportInputStream;

public interface SoapProtocolChooser {

    /**
     * Verify the SOAP 1.1 version based on content-type header (text/xml)
     * @param transportInputStream input stream from the SOAP client
     * @return true if SOAP 1.1 is used
     * @throws IOException in case an error occurs
     */

    boolean useSoap11(TransportInputStream transportInputStream) throws IOException;

    /**
     * Verify the SOAP 1.2 version based on content-type header (application/soap+xml)
     * @param transportInputStream input stream from the SOAP client
     * @return true if SOAP 1.2 is used
     * @throws IOException in case an error occurs
     */

    boolean useSoap12(TransportInputStream transportInputStream) throws IOException;

}