package com.cannontech.multispeak.data.v4;

import com.cannontech.msp.beans.v4.ErrorObject;

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
