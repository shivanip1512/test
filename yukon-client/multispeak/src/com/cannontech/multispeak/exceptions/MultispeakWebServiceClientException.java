package com.cannontech.multispeak.exceptions;

import java.io.Serializable;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * This is the parent class for every custom exception defined in this
 * application and should not be bypassed when declaring any new exception.
 * 
 * @version 1.0
 */
@SoapFault(faultCode = FaultCode.CLIENT, faultStringOrReason = "Can not process client.")
public class MultispeakWebServiceClientException extends Exception implements Serializable {

    public MultispeakWebServiceClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MultispeakWebServiceClientException(final String message) {
        super(message);
    }

    /**
     * @return the error message
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
