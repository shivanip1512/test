package com.cannontech.database.cache.functions;

import java.util.Iterator;
import java.util.Map;

import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Handles authenticating and authorization.
 * @author alauinger
 */
public class AuthFuncs {
	
	/**
	 * Attempts to locate user with the given username and password.
	 * Returns null if there is no match.
	 * @param username
	 * @param password
	 * @return LiteYukonUser
	 */
	public static LiteYukonUser login(String username, String password) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			Iterator i = cache.getAllYukonUsers().iterator();
			while(i.hasNext()) {
				LiteYukonUser u = (LiteYukonUser) i.next();
				if(u.getUsername().equals(username) &&
				   u.getPassword().equals(password) ) {
				   	return u;  //success!
				   }
			}			
			return null; //failure
		}
	}
	
	/**
	 * Returns a Pair<LiteYukonRole,String> if the given user 
	 * has been granted the given role.  The second element in the Pair
	 * is the value of role for this user.
	 * @param user
	 * @param roleName
	 * @return Pair
	 */
	public static Pair checkRole(LiteYukonUser user, String roleName) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			Map lookupMap = cache.getAllYukonUserRoleLookupMap();
			
			Map m = (Map) lookupMap.get(user);
	    	if(m != null) {
	    		Pair p = (Pair) m.get(roleName);
	    		return p;	    				
	    	}
		}
		
		return null;		
	}
	
	/**
	 * Returns the value for a given user and role name.
	 * @param user
	 * @param roleName
	 * @return String
	 */
	public static String getRoleValue(LiteYukonUser user, String roleName) {
		return getRoleValue(user,roleName,null);
	}
	
	/**
	 * Returns the value for a given user and role name.
	 * If the user doesn't have this role then returns defaultValue for convenience.
	 * @param user
	 * @param roleName
	 * @param defaultValue
	 * @return String
	 */
	public static String getRoleValue(LiteYukonUser user, String roleName, String defaultValue) {
		Pair p = checkRole(user,roleName);
		return (p != null ? (String)p.second : defaultValue);
	}
	
	/**
	 * Dont let anyone instantiate me
	 * @see java.lang.Object#Object()
	 */
	private AuthFuncs() {
	}
	
	
}
