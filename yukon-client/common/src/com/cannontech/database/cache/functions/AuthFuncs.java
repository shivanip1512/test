package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonRole;
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
	public static Pair checkRole(LiteYukonUser user, int roleID) {		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			Map lookupMap = cache.getAllYukonUserRoleIDLookupMap();
			Map m = (Map) lookupMap.get(user);
			if(m != null) {
				Pair p = (Pair) m.get(new Integer(roleID));
				return p;
			}
		}			
		return null;		
	}
		
	/**
	 * Returns the value for a given user and role
	 * @param user
	 * @param roleID
	 * @return String
	 */
	public static String getRoleValue(LiteYukonUser user, int roleID) {
		return getRoleValue(user,roleID,null);
	}
	
	/**
	 * Returns the value for a given user and role.
	 * If no value is found then defaultValue is returned for convenience.
	 * @param user
	 * @param roleID
	 * @return String
	 */
	public static String getRoleValue(LiteYukonUser user, int roleID, String defaultValue) {
		Pair p = checkRole(user,roleID);
		return (p != null ? (String)p.second : defaultValue);
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
