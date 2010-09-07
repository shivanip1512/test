package com.cannontech.amr.rfn.service;

public class UnexpectedMeterReadReplyException extends RuntimeException 
{
    public UnexpectedMeterReadReplyException(String message) {
        super(message);
    }

    public UnexpectedMeterReadReplyException(String message, Throwable cause) {
        super(message, cause);
    }
}