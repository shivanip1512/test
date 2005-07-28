package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.radius.RadiusLogin;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.AuthenticationRole;
import com.cannontech.user.UserUtils;

/**
 * A collection of methods to handle authenticating, authorization, and the retrieval of role property values.
 * @author alauinger
 */
public class AuthFuncs {
	
	/**
	 * Attempts to locate user with the given username and password.
	 * Returns null if there is no match or the user is disabled.
	 * Does not attempt Radius login if user is admin.
	 * @param username
	 * @param password
	 * @return LiteYukonUser
	 */
	public static LiteYukonUser login(String username, String password) {
		String radiusMethod;
		//If admin user, skip the radius login attempt (the authentication mode is YUKON when skipped).
		if( isAdminUser(username))
			radiusMethod  = AuthenticationRole.YUKON_AUTH_STRING;
		else
			radiusMethod = RoleFuncs.getGlobalPropertyValue(AuthenticationRole.AUTHENTICATION_MODE);
			
		if(radiusMethod != null && radiusMethod.equalsIgnoreCase(AuthenticationRole.RADIUS_AUTH_STRING))
		{
			CTILogger.info("Attempting a RADIUS login");
			return RadiusLogin.login(username, password);
		}
		else
		{
			return yukonLogin(username, password);
		}
	}
	
	public static LiteYukonUser yukonLogin(String username, String password) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			Iterator i = cache.getAllYukonUsers().iterator();
			while(i.hasNext()) {
				LiteYukonUser u = (LiteYukonUser) i.next();
				if( !CtiUtilities.isDisabled(u.getStatus()) &&
					u.getUsername().equalsIgnoreCase(username) &&
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
		return !CtiUtilities.isFalse(getRolePropertyValue(user, rolePropertyID, null));
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
     * @param user
     * @param rolePropertyID
     * @return String
     * @throws UnknownRolePropertyException If RoleProperty doesn't exist. 
     */
    public static String getRolePropertyValueEx(LiteYukonUser user, int rolePropertyID) throws UnknownRolePropertyException {
        String value = getRolePropertyValue(user,rolePropertyID, null);
        if (value == null) {
            throw new UnknownRolePropertyException(user, rolePropertyID);
        }
        return value;
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
		//If the defaultValue is null, attempt to use the default value from the roleProperty
		// Returning a null value could cause some serious exceptions.
		// By returning the default value from the property, we should be able to continue on
		//  with life better when new properties have been added to a Role but may not have 
		//  successfully updated all existing login groups with the new properties. 
		if(defaultValue == null)
		{
			LiteYukonRoleProperty prop = getRoleProperty(rolePropertyID);
			CTILogger.warn("Unknown RoleProperty(" + rolePropertyID + ") '" + prop.getKeyName() + "' for user " + user + ".  Default value from DB will be used.");
			return prop == null ? defaultValue : prop.getDefaultValue();	
		}
		return defaultValue;	
	}

	/**
	 * Returns the value for a given group and role property.
	 * If no value is found then defaultValue is returned for convenience.
	 * @param group
	 * @param roleProperty
	 * @return String
	 */
	public static String getRolePropValueGroup(LiteYukonGroup group_, int rolePropertyID, String defaultValue) 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) 
		{
			Map lookupMap = cache.getYukonGroupRolePropertyMap();
			
//Set s = lookupMap.keySet();
//Iterator it = s.iterator();
//while( it.hasNext() )
//System.out.println("  " + it.next() );
//
//System.out.println("     Vals" );
//Collection c = lookupMap.values();
//it = c.iterator();
//while( it.hasNext() )
//System.out.println("  " + it.next() );


			Map roleMap = (Map) lookupMap.get( group_ );			
			
			if(roleMap != null) {			
				Iterator rIter = roleMap.entrySet().iterator(); 
				while(rIter.hasNext()) {
					Map propMap = (Map) ((Map.Entry) rIter.next()).getValue(); //Iter.next();
					String val = (String) propMap.get(getRoleProperty(rolePropertyID));
					if(val != null) return val;
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
				if(r.getCategory().equalsIgnoreCase(category)) {
					retList.add(r);
				}
			}
		}		
		return retList;
	}
	
	/**
	 * Return a particular lite yukon role given a role id.
	 * @param roleid
	 * @return
	 */
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
	 * Return a particular role property given a role property id.
	 * @param propid
	 * @return
	 */
	public static LiteYukonRoleProperty getRoleProperty(int propid) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			for(Iterator i = cache.getAllYukonRoleProperties().iterator(); i.hasNext();) {
				LiteYukonRoleProperty p = (LiteYukonRoleProperty) i.next();
				if(p.getRolePropertyID() == propid) {
					return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * Return a List<LiteYukonRoleProperty> for a given LiteYukonRole
	 * @param role
	 * @return
	 */
	public static List getRoleProperties(LiteYukonRole role) {
		ArrayList props = new ArrayList();
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			for(Iterator i = cache.getAllYukonRoleProperties().iterator(); i.hasNext();) {
				LiteYukonRoleProperty p = (LiteYukonRoleProperty) i.next();
				if(p.getRoleID() == role.getRoleID()) {
					props.add(p);
				}
			}
		}
		return props;
	}
	
	/**
	 * Return a particular lite yukon group given the group name
	 * @param groupName
	 * @return LiteYukonGroup
	 */
	public static LiteYukonGroup getGroup(String groupName) {
        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
        
        synchronized (cache) {
        	java.util.Iterator it = cache.getAllYukonGroups().iterator();
        	while (it.hasNext()) {
        		LiteYukonGroup group = (LiteYukonGroup) it.next();
        		if (group.getGroupName().equalsIgnoreCase( groupName ))
        			return group;
        	}
        }
        
        return null;
	}

	/**
	 * Return a particular lite yukon group given the group ID
	 * @param groupName
	 * @return LiteYukonGroup
	 */
	public static LiteYukonGroup getGroup(int grpID_) 
	{
		  DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
        
		  synchronized (cache) 
		  {
			java.util.Iterator it = cache.getAllYukonGroups().iterator();
			while (it.hasNext()) {
				LiteYukonGroup group = (LiteYukonGroup) it.next();
				if (group.getGroupID() == grpID_ )
					return group;
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

	/**
	 * Return true if username_ is the admin user.
	 * @param username_
	 * @return
	 */
	public static boolean isAdminUser(String username_)
	{
		LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser(username_);
		if ( liteUser != null && liteUser.getUserID() == UserUtils.USER_ADMIN_ID )
			return true;
		return false;
	}

    /**
     * Return true if the use has access to the given PAOid.
     * By default, the given user has access to ALL PAOS (backwards compatability)
     * 
     * @param LiteYukonUser, int
     * @return boolean
     */
    public static boolean userHasAccessPAO( LiteYukonUser user, int paoID )
    {
        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();            
        synchronized (cache) 
        {
            // Notice: The paoIDs array must be sorted for the BinarySearch to work
            int[] paoIDs = (int[])cache.getYukonUserPaoOwners().get( user );

            if( paoIDs != null )
            {
                int res = Arrays.binarySearch( paoIDs, paoID );                
                if( res >= 0 )
                    return true;
            }
            else //if the user is not found, we assume they have access to all PAOs
                return true;
        }

        //the user was found, but the ID was not in the set of
        //  given IDs, they are not permitted to see this given paoID
        return false;
    }
    
    /**
     * Return true if the use has access to the given PAOid, else return false.
     * 
     * @param LiteYukonUser, int
     * @return boolean
     */
    public static boolean hasExlusiveAccess( LiteYukonUser user, int paoID )
    {
        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();            
        synchronized (cache) 
        {
            // Notice: The paoIDs array must be sorted for the BinarySearch to work
            int[] paoIDs = (int[])cache.getYukonUserPaoOwners().get( user );

            if( paoIDs != null )
            {
                int res = Arrays.binarySearch( paoIDs, paoID );                
                if( res >= 0 )
                    return true;
            }
        }

        return false;
    }    


	/**
	 * Attempts to log a voice user into the system using the
	 * given ContactID and pin. If the PIN is matched to any PIN 
	 * the contact hast, then we are logged in. Returns null if
	 * we are not logged in. This method is CASE SENSITIVE for the
	 * PIN (however, most of the time the PIN will be numeric only!)
	 * 
	 */
	public static LiteYukonUser voiceLogin(int contactid, String pin) {
			
		LiteContact lContact = ContactFuncs.getContact( contactid );

		if( lContact != null )
		{
			CTILogger.info("Attempting a VOICE login with the following Contact info: " + lContact.toString());
			
			LiteYukonUser lYukUser = YukonUserFuncs.getLiteYukonUser( lContact.getLoginID() );
			if( lYukUser != null ) {
				if( lYukUser.getUserID() == UserUtils.USER_DEFAULT_ID )
					CTILogger.info("  Failed VOICE login because the YukonUser found was the (none) userid, Contact: " + lContact.toString());
				else if( UserUtils.isUserDisabled(lYukUser) )
					CTILogger.info("  Failed VOICE login because the YukonUser found is DISABLED, Contact: " + lContact.toString());			
				else {
					LiteContactNotification[] pins = ContactFuncs.getAllPINNotifDestinations( lContact.getContactID() );
					for( int i = 0; i < pins.length; i++ ) {
						LiteContactNotification pinNotif = pins[i];
						if( pinNotif.getNotification().equals(pin) ) {
							if( pinNotif.isDisabled() ) {
								CTILogger.info("  Failed VOICE login because the matching PIN found is DISABLED, Contact: " + lContact.toString());
								break;
							}
							else
								return lYukUser; //success
						}
					}

					CTILogger.info("  Failed VOICE login because an invalid PIN was entered or no PIN was found, Contact: " + lContact.toString());
				}
			}
			else
				CTILogger.info("  Failed VOICE login because no YukonUser was found for the Contact: " + lContact.toString());
		}
		else
			CTILogger.info("  Failed VOICE login since no Contact exists with a Contactid = " + contactid);


		return null;  //failure
	}

}
