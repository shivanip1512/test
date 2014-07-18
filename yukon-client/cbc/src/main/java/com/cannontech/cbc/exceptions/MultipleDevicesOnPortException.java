package com.cannontech.cbc.exceptions;

import com.cannontech.database.TransactionException;

public class MultipleDevicesOnPortException extends TransactionException
 {

    public MultipleDevicesOnPortException(String message) {
        super(CBCExceptionMessages.MSG_MULTIPLE_DEVICES_ON_PORT[0] + message + CBCExceptionMessages.MSG_MULTIPLE_DEVICES_ON_PORT[1]);
    }

    public MultipleDevicesOnPortException() {
        super();
    }
}
