package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * A collection of methods to handle authenticating, authorization, and the retrieval of role property values.
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
	 * Returns LiteYukonRole if the given user 
	 * has been granted the given role otherwise null.
	 * @param LiteYukonUser
	 * @param roleID
	 * @return LiteYukonRole
	 */
	public static LiteYukonRole checkRole(LiteYukonUser user, int roleID) {		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			Map lookupMap = cache.getYukonUserRoleIDLookupMap();
			Map roleMap = (Map) lookupMap.get(user);
			if(roleMap != null) {
				LiteYukonRole role= (LiteYukonRole) roleMap.get(new Integer(roleID));
				return role;
			}
		}			
		return null;		
	}
	
	/**
	 * Returns true if the given user has a true value for the given property
	 * @param user
	 * @param rolePropertyID
	 * @return boolean
	 */
	public static boolean checkRoleProperty(LiteYukonUser user, int rolePropertyID) {
		return CtiUtilities.isTrue(getRolePropertyValue(user, rolePropertyID, null));
	}	
	/**
	 * Returns the value for a given user and role property.
	 * @param user
	 * @param rolePropertyID
	 * @return String
	 */
	public static String getRolePropertyValue(LiteYukonUser user, int rolePropertyID) {
		return getRolePropertyValue(user,rolePropertyID,null);
	}
	
	/**
	 * Returns the value for a given user and role property.
	 * If no value is found then defaultValue is returned for convenience.
	 * @param user
	 * @param roleProperty
	 * @return String
	 */
	public static String getRolePropertyValue(LiteYukonUser user, int rolePropertyID, String defaultValue) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			Map lookupMap = cache.getYukonUserRolePropertyIDLookupMap();
			Map propMap = (Map) lookupMap.get(user);
			if(propMap != null) {
				Pair p = (Pair) propMap.get(new Integer(rolePropertyID));
				if(p != null) {
					return (String) p.second;
				}
			}	
		}
		return defaultValue;	
	}
	
	/**
	 * Returns a list of roles that are in the given category.
	 * @param category
	 * @return List
	 */
	public static List getRoles(String category) {
		List retList = new ArrayList(100);
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			Iterator i = cache.getAllYukonRoles().iterator();
			while(i.hasNext()) {
				LiteYukonRole r = (LiteYukonRole) i.next();
				if(r.getCategory().equals(category)) {
					retList.add(r);
				}
			}
		}		
		return retList;
	}
	
	public static LiteYukonRole getRole(int roleid) {		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			Iterator i = cache.getAllYukonRoles().iterator();
			while(i.hasNext()) {
				LiteYukonRole r = (LiteYukonRole) i.next();
				if(r.getRoleID() == roleid) {
					return r;
				}
			}
		}		
		return null;
	}
	
	/**
	 * Dont let anyone instantiate me
	 * @see java.lang.Object#Object()
	 */
	private AuthFuncs() {
	}
	
	
}
