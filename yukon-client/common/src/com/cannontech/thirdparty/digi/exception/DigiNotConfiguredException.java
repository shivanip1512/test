package com.cannontech.thirdparty.digi.exception;

import javax.xml.ws.WebServiceException;

public class DigiNotConfiguredException extends WebServiceException {
    
    public DigiNotConfiguredException(String message) {
        super(message);
    }

    public DigiNotConfiguredException(Exception e) {
        super(e);
    }

    public DigiNotConfiguredException(String string, Throwable e) {
        super(string, e);
    }
    
}