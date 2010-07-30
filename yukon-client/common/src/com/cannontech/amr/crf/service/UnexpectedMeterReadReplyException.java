package com.cannontech.amr.crf.service;

public class UnexpectedMeterReadReplyException extends RuntimeException 
{
    public UnexpectedMeterReadReplyException(String message) {
        super(message);
    }

    public UnexpectedMeterReadReplyException(String message, Throwable cause) {
        super(message, cause);
    }
}