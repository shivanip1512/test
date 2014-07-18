package com.cannontech.cbc.exceptions;



public class SameMasterSlaveCombinationException extends FormWarningException {
    
    public SameMasterSlaveCombinationException(String message) {
        super(CBCExceptionMessages.MSG_SAME_MASTER_SLAVE_COMBINATION + message);

    }
    
    public SameMasterSlaveCombinationException() {
        super();
    }
}
