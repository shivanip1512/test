package com.cannontech.multispeak.exceptions;

import java.io.Serializable;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * This is the parent class for every custom exception defined in this
 * application and should not be bypassed when declaring any new exception.
 * <p>
 * It wraps the ErrorType object within it that is required while logging as per HP Open View layout.
 * </p>
 * 
 * @version 1.0
 */
@SoapFault(faultCode = FaultCode.SERVER, faultStringOrReason = "Can not process request.")
public class MultispeakWebServiceException extends Exception implements Serializable {
    public Throwable detail;

    public MultispeakWebServiceException(final String message, final Throwable cause) {
        super(message);
        detail = cause;

    }

    public MultispeakWebServiceException(final String message) {
        super(message);

    }

    /**
     * Returns the detail message, including the message from the cause, if any,
     * of this exception.
     * 
     * @return the detail message
     */
    @Override
    public String getMessage() {
        if (detail == null) {
            return super.getMessage();
        } else {
            return super.getMessage() + "; nested exception is: \n\t" + detail.toString();
        }
    }

    /**
     * Returns the cause of this exception.
     */
    @Override
    public Throwable getCause() {
        return detail;
    }
}
