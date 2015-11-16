package com.cannontech.web.cayenta.util;

public class CayentaMeterNotFoundException extends CayentaRequestException {

	public CayentaMeterNotFoundException(String message) {
        super(message);
    }

    public CayentaMeterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
