package com.cannontech.esub.web;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * State info stored in session for esubstation.com
 * @author alauinger
 */
public class SessionInfo {
	private LiteYukonUser user;

	/**
	 * Returns the user.
	 * @return LiteYukonUser
	 */
	public LiteYukonUser getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * @param user The user to set
	 */
	public void setUser(LiteYukonUser user) {
		this.user = user;
	}

}
