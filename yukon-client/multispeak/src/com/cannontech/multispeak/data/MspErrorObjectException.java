package com.cannontech.multispeak.data;

import com.cannontech.multispeak.deploy.service.ErrorObject;

public class MspErrorObjectException extends RuntimeException {
    
    private ErrorObject errorObject;
    
    public MspErrorObjectException(ErrorObject errorObject) {
        super();
        this.errorObject = errorObject;
    }
    
    public ErrorObject getErrorObject() {
        return errorObject;
    }
}
