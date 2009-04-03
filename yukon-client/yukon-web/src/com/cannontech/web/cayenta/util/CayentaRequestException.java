package com.cannontech.web.cayenta.util;

public class CayentaRequestException extends Exception {

	public CayentaRequestException(String message) {
        super(message);
    }

    public CayentaRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
