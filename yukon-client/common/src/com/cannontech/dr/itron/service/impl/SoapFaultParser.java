package com.cannontech.dr.itron.service.impl;

import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.dr.itron.service.ItronCommunicationException;

interface SoapFaultParser {
   
    /**
     *   Parses Soap Fault
     *   If it only contains the codes that we are ignoring, ignores exception
     *   Otherwise throws an Communication exception with the code and description of the first not ignored error it found
     *   The full Fault is logged in WS log if debug is turned on
     *
     * @param e - Soap Fault
     * @param faultCodesToIgnore - error codes to ignore
     * @throws ItronCommunicationException - created from the first fault that is not ignored
     */
    void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) throws ItronCommunicationException;
    
    /**
     * Returns true if the manager can parse the fault
     */
    boolean isSupported(ItronEndpointManager manager);
    
    /**
     * Checks if error code is in faults to ignore list
     * 
     * @throws ItronCommunicationException - created from the first fault that is not ignored
     */
    default void checkIfErrorShouldBeIgnored(String errorCode, String errorMessage, Set<String> faultCodesToIgnore, Logger log)
            throws ItronCommunicationException {
        if (!faultCodesToIgnore.contains(errorCode)) {
            ItronCommunicationException exception =
                new ItronCommunicationException("Soap Fault: " + errorCode + ":" + errorMessage);
            log.error(exception);
            throw exception;
        }
        log.debug("Ignored soap fault: " + errorCode + ":" + errorMessage);
    }
}
