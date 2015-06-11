
package com.cannontech.common.util;

public class JsonReponseException extends RuntimeException {
    
    public JsonReponseException(Exception cause) {
        super(cause);
    }
    
    public JsonReponseException(String message, Exception cause) {
        super(message, cause);
    }
    
}