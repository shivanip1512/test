/*
 * Created on Nov 13, 2003
 */
package com.cannontech.cttp;

import com.cannontech.cttp.schema.cttp_OperationType;

/**
 * @author aaron
 */
public class CttpResponse {
	private cttp_OperationType cttpOperation;
	
	public CttpResponse(cttp_OperationType cttpOp) {
		setCttpOperation(cttpOp);
	}
	
	/**
	 * @return
	 */
	public cttp_OperationType getCttpOperation() {
		return cttpOperation;
	}

	/**
	 * @param type
	 */
	public void setCttpOperation(cttp_OperationType type) {
		cttpOperation = type;
	}

}
