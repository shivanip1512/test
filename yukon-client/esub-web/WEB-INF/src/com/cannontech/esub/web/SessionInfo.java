package com.cannontech.esub.web;

import com.cannontech.database.data.web.User;
/**
 * @author alauinger
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SessionInfo {
	private User user;
	/**
	 * Returns the user.
	 * @return User
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * @param user The user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

}
