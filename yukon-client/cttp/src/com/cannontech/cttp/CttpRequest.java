/*
 * Created on Nov 13, 2003
 */
package com.cannontech.cttp;

import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Encapsulates a cttp request.
 * @author aaron
 */
public class CttpRequest {
	private LiteYukonUser user;
	private cttp_OperationType cttpOperation;
	
	public CttpRequest(LiteYukonUser user, cttp_OperationType cttpOp) {
		setUser(user);
		setCttpOperation(cttpOp);
	}
	
	/**
	 * @return
	 */
	public cttp_OperationType getCttpOperation() {
		return cttpOperation;
	}

	/**
	 * @return
	 */
	public LiteYukonUser getUser() {
		return user;
	}

	/**
	 * @param type
	 */
	public void setCttpOperation(cttp_OperationType type) {
		cttpOperation = type;
	}

	/**
	 * @param user
	 */
	public void setUser(LiteYukonUser user) {
		this.user = user;
	}

}
