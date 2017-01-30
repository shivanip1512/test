package com.cannontech.multispeak.data.v5;

import com.cannontech.msp.beans.v5.commontypes.ErrorObject;

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
