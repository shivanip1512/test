package com.cannontech.esub.web;

import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Handles logins specifically for esubstation
 * @author alauinger
 */
public class Authenticator {

	public static SessionInfo login(String username, String password) {
		LiteYukonUser user = AuthFuncs.login(username,password);
		if(user == null)
			return null;
			
		SessionInfo info = new SessionInfo();
		info.setUser(user);
		return info;		
	}
}
