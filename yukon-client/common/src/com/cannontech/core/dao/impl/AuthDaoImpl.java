package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.UnknownRolePropertyException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;


/**
 * A collection of methods to handle authenticating, authorization, and the retrieval of role property values.
 * @author alauinger
 */
public class AuthDaoImpl implements AuthDao {
	
    private RoleDao roleDao;
    private YukonUserDao yukonUserDao;
    private ContactDao contactDao;
    private IDatabaseCache databaseCache;
    private AuthenticationService authenticationService;
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#login(java.lang.String, java.lang.String)
     */
	public LiteYukonUser login(String username, String password) {
        try {
            return authenticationService.login(username, password);
        } catch (Exception e) {
            CTILogger.info(e);
            return null;
        }
//		String radiusMethod;
//		//If admin user, skip the radius login attempt (the authentication mode is YUKON when skipped).
//		if( isAdminUser(username))
//			radiusMethod  = AuthenticationRole.YUKON_AUTH_STRING;
//		else
//			radiusMethod = roleDao.getGlobalPropertyValue(AuthenticationRole.AUTHENTICATION_MODE);
//			
//		if(radiusMethod != null && radiusMethod.equalsIgnoreCase(AuthenticationRole.RADIUS_AUTH_STRING))
//		{
//			CTILogger.info("Attempting a RADIUS login");
//			return RadiusLogin.login(username, password);
//		}
//		else
//		{
//			return yukonLogin(username, password);
//		}
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#checkRole(com.cannontech.database.data.lite.LiteYukonUser, int)
     */
	/*This was changed to bypass the huge memory overhead in caching several
	 * complex map within map structures for every single user when all we really
	 * need is one.
	 */
	public LiteYukonRole getRole(LiteYukonUser user, int roleID) 
	{
		synchronized(databaseCache) 
		{
			return databaseCache.getARole(user, roleID);
		}
	}
    
    public boolean checkRole(LiteYukonUser user, int roleId) {
        return getRole(user, roleId) != null;
    }
	
	
	/*
	public LiteYukonRole checkRole(LiteYukonUser user, int roleID) {		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			Map lookupMap = cache.getYukonUserRoleIDLookupMap(user.getLiteID());
			Map roleMap = (Map) lookupMap.get(user);
			if(roleMap != null) {
				LiteYukonRole role= (LiteYukonRole) roleMap.get(new Integer(roleID));
				return role;
			}
		}			
		return null;		
	}
	*/
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#checkRoleProperty(com.cannontech.database.data.lite.LiteYukonUser, int)
     */
	public boolean checkRoleProperty(LiteYukonUser user, int rolePropertyID) {
		return !CtiUtilities.isFalse(getRolePropertyValue(user, rolePropertyID));
	}
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#checkRoleProperty(int, int)
     */
    public boolean checkRoleProperty(int userID, int rolePropertyID) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userID);
        return !CtiUtilities.isFalse(getRolePropertyValue(user, rolePropertyID));
    }   
    
   /* (non-Javadoc)
 * @see com.cannontech.core.dao.AuthDao#getRolePropertyValueEx(com.cannontech.database.data.lite.LiteYukonUser, int)
 */
    public String getRolePropertyValueEx(LiteYukonUser user, int rolePropertyID) throws UnknownRolePropertyException {
        String value = getRolePropertyValue(user,rolePropertyID);
        if (value == null) {
            throw new UnknownRolePropertyException(user, rolePropertyID);
        }
        return value;
    }
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getRolePropertyValue(com.cannontech.database.data.lite.LiteYukonUser, int)
     */
	/*This was changed to bypass the huge memory overhead in caching several
	 * complex map within map structures for every single user when all we really
	 * need is one return value straight from the db.
	 */
	public String getRolePropertyValue(LiteYukonUser user, int rolePropertyID) 
	{
		synchronized(databaseCache) 
		{
			return databaseCache.getARolePropertyValue(user, rolePropertyID);
		}
	}
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getRolePropertyValue(int, int)
     */
    public String getRolePropertyValue(int userID, int rolePropertyID) {
        LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(userID);
        Validate.notNull(liteYukonUser, "Could not find a valid LiteYukonUser for userID=" + userID);
        return getRolePropertyValue(liteYukonUser, rolePropertyID);
    }
	
	/*
	public String getRolePropertyValue(LiteYukonUser user, int rolePropertyID, String defaultValue) {
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
	*/
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getRolePropValueGroup(com.cannontech.database.data.lite.LiteYukonGroup, int, java.lang.String)
     */
	public String getRolePropValueGroup(LiteYukonGroup group_, int rolePropertyID, String defaultValue) 
	{
		synchronized(databaseCache) 
		{
			Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> lookupMap = 
                databaseCache.getYukonGroupRolePropertyMap();
			
			Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>> roleMap = lookupMap.get( group_ );			
			
			if(roleMap != null) {			
                Iterator<Entry<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> rIter = roleMap.entrySet().iterator();
				while(rIter.hasNext()) {
					Map<LiteYukonRoleProperty, String> propMap = rIter.next().getValue();
					String val = propMap.get(getRoleProperty(rolePropertyID));
					if(val != null) return val;
				}
			}
		}
		return defaultValue;		 	
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getRoles(java.lang.String)
     */
	public List<LiteYukonRole> getRoles(String category) {
		List<LiteYukonRole> retList = new ArrayList<LiteYukonRole>(100);
				
		synchronized(databaseCache) {
			Iterator i = databaseCache.getAllYukonRoles().iterator();
			while(i.hasNext()) {
				LiteYukonRole r = (LiteYukonRole) i.next();
				if(r.getCategory().equalsIgnoreCase(category)) {
					retList.add(r);
				}
			}
		}		
		return retList;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getRole(int)
     */
	public LiteYukonRole getRole(int roleid) {		
				
		synchronized(databaseCache) {
			Iterator i = databaseCache.getAllYukonRoles().iterator();
			while(i.hasNext()) {
				LiteYukonRole r = (LiteYukonRole) i.next();
				if(r.getRoleID() == roleid) {
					return r;
				}
			}
		}		
		return null;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getRoleProperty(int)
     */
	public LiteYukonRoleProperty getRoleProperty(int propid) {
		
		synchronized(databaseCache) {
			for(Iterator i = databaseCache.getAllYukonRoleProperties().iterator(); i.hasNext();) {
				LiteYukonRoleProperty p = (LiteYukonRoleProperty) i.next();
				if(p.getRolePropertyID() == propid) {
					return p;
				}
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getRoleProperties(com.cannontech.database.data.lite.LiteYukonRole)
     */
	public List<LiteYukonRoleProperty> getRoleProperties(LiteYukonRole role) {
		ArrayList<LiteYukonRoleProperty> props = new ArrayList<LiteYukonRoleProperty>();
		
        synchronized(databaseCache) {
			for(Iterator i = databaseCache.getAllYukonRoleProperties().iterator(); i.hasNext();) {
				LiteYukonRoleProperty p = (LiteYukonRoleProperty) i.next();
				if(p.getRoleID() == role.getRoleID()) {
					props.add(p);
				}
			}
		}
		return props;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getGroup(java.lang.String)
     */
	public LiteYukonGroup getGroup(String groupName) {
        
        synchronized (databaseCache) {
        	java.util.Iterator it = databaseCache.getAllYukonGroups().iterator();
        	while (it.hasNext()) {
        		LiteYukonGroup group = (LiteYukonGroup) it.next();
        		if (group.getGroupName().equalsIgnoreCase( groupName ))
        			return group;
        	}
        }
        
        return null;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#getGroup(int)
     */
	public LiteYukonGroup getGroup(int grpID_) 
	{
		  synchronized (databaseCache) 
		  {
			java.util.Iterator it = databaseCache.getAllYukonGroups().iterator();
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
	private AuthDaoImpl() 
	{
		super();
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#isAdminUser(java.lang.String)
     */
	public boolean isAdminUser(String username_)
	{
		LiteYukonUser liteUser = yukonUserDao.getLiteYukonUser(username_);
		return isAdminUser(liteUser);
	}

	/* (non-Javadoc)
	 * @see com.cannontech.core.dao.AuthDao#isAdminUser(java.lang.String)
	 */
	public boolean isAdminUser(LiteYukonUser user)
	{
	    if ( user != null && user.getUserID() == UserUtils.USER_ADMIN_ID )
	        return true;
	    return false;
	}
	
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#userHasAccessPAO(com.cannontech.database.data.lite.LiteYukonUser, int)
     */
    public boolean userHasAccessPAO( LiteYukonUser user, int paoID )
    {
        synchronized (databaseCache) 
        {
            // Notice: The paoIDs array must be sorted for the BinarySearch to work
            int[] paoIDs = (int[])databaseCache.getYukonUserPaoOwners().get( user );

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
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#hasExlusiveAccess(com.cannontech.database.data.lite.LiteYukonUser, int)
     */
    public boolean hasExlusiveAccess( LiteYukonUser user, int paoID )
    {
        synchronized (databaseCache) 
        {
            // Notice: The paoIDs array must be sorted for the BinarySearch to work
            int[] paoIDs = (int[])databaseCache.getYukonUserPaoOwners().get( user );

            if( paoIDs != null )
            {
                int res = Arrays.binarySearch( paoIDs, paoID );                
                if( res >= 0 )
                    return true;
            }
        }

        return false;
    }    

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#inboundVoiceLogin(java.lang.String, java.lang.String)
     */
    public LiteYukonUser inboundVoiceLogin(String phoneNumber, String pin){
        
        LiteYukonUser user = null;
        
        int[] phoneTypes = new int[]{YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE};
        
        LiteContact[] contacts = contactDao.getContactsByPhoneNo(phoneNumber, phoneTypes);
            
        // If no contact is found, try adding '-'s to the phone number
        if((contacts == null || contacts.length == 0) && phoneNumber.length() == 10) {

            String dashedPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);

            contacts = contactDao.getContactsByPhoneNo(dashedPhoneNumber, phoneTypes);

        } 
        
        // If still no contact is found, login failed - return null
        if(contacts == null || contacts.length == 0) {
            CTILogger.info("An INBOUND VOICE login with phone number: " + phoneNumber + " and pin: " + pin + " was unsuccessful - No contacts were found.");

            return user;
        
        }
        if(contacts.length > 1) {
            CTILogger.info("An INBOUND VOICE login with phone number: " + phoneNumber + " and pin: " + pin + " was unsuccessful - More than one contact was found.");
            
            return user;
            
        }
        
        LiteContact contact = contacts[0];

        user = voiceLogin(contact.getContactID(), pin);
        
        return user;
    }

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#voiceLogin(int, java.lang.String)
     */
	public LiteYukonUser voiceLogin(int contactid, String pin) {
			
		LiteContact lContact = contactDao.getContact( contactid );

		if( lContact != null )
		{
			CTILogger.info("Attempting a VOICE login with the following Contact info: " + lContact.toString());
			
			LiteYukonUser lYukUser = yukonUserDao.getLiteYukonUser( lContact.getLoginID() );
			if( lYukUser != null ) {
				if( lYukUser.getUserID() == UserUtils.USER_DEFAULT_ID )
					CTILogger.info("  Failed VOICE login because the YukonUser found was the (none) userid, Contact: " + lContact.toString());
				else if( UserUtils.isUserDisabled(lYukUser) )
					CTILogger.info("  Failed VOICE login because the YukonUser found is DISABLED, Contact: " + lContact.toString());			
				else {
					LiteContactNotification[] pins = contactDao.getAllPINNotifDestinations( lContact.getContactID() );
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

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.AuthDao#hasPAOAccess(com.cannontech.database.data.lite.LiteYukonUser)
     */
	public boolean hasPAOAccess( LiteYukonUser user )
	{
		synchronized (databaseCache) 
		{
			int[] paoIDs = (int[])databaseCache.getYukonUserPaoOwners().get( user );
			
			return paoIDs != null && paoIDs.length > 0;
		}
	}
    
	public void verifyFalseProperty(LiteYukonUser user, int rolePropertyId)
	throws NotAuthorizedException {
	    boolean b = checkRoleProperty(user, rolePropertyId);
	    if (b) {
	        throw NotAuthorizedException.falseProperty(user, rolePropertyId);
	    }
	}

	public void verifyRole(LiteYukonUser user, int roleId)
	throws NotAuthorizedException {
	    boolean b = checkRole(user, roleId);
	    if (!b) {
	        throw NotAuthorizedException.falseProperty(user, roleId);
	    }
	}

	public void verifyTrueProperty(LiteYukonUser user, int rolePropertyId)
	throws NotAuthorizedException {
	    boolean b = checkRoleProperty(user, rolePropertyId);
	    if (!b) {
	        throw NotAuthorizedException.trueProperty(user, rolePropertyId);
	    }
	}
    
    public void verifyAdmin(LiteYukonUser user) throws NotAuthorizedException {
        boolean b = isAdminUser(user);
        if (!b) {
            throw NotAuthorizedException.adminUser(user);
        }
    }


    @Required
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Required
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    @Required
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
