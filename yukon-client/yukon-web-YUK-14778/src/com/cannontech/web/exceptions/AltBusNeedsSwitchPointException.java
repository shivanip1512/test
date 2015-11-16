package com.cannontech.web.exceptions;

import com.cannontech.cbc.exceptions.CBCExceptionMessages;
import com.cannontech.database.TransactionException;

public class AltBusNeedsSwitchPointException extends TransactionException {

	public AltBusNeedsSwitchPointException() {
		super(CBCExceptionMessages.MSG_ALTSUB_NEEDS_SWITCH_PT);
	}

	public AltBusNeedsSwitchPointException(String s) {
		super(s);
	}

	public AltBusNeedsSwitchPointException(String s, Throwable t) {
		super(s, t);
	}

}
