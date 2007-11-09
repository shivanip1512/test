package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.UnknownRolePropertyException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;


/**
 * A collection of methods to handle authenticating, authorization, and the retrieval of 
 * role property values.
 * @author alauinger
 */
public class AuthDaoImpl implements AuthDao {
	
    private YukonUserDao yukonUserDao;
    private ContactDao contactDao;
    private IDatabaseCache databaseCache;
    private AuthenticationService authenticationService;
    
	public LiteYukonUser login(String username, String password) {
        try {
            return authenticationService.login(username, password);
        } catch (Exception e) {
            CTILogger.info(e);
            return null;
        }
	}
	
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
	
	
	public boolean checkRoleProperty(LiteYukonUser user, int rolePropertyID) {
		return !CtiUtilities.isFalse(getRolePropertyValue(user, rolePropertyID));
	}
    
    public boolean checkRoleProperty(int userID, int rolePropertyID) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userID);
        return !CtiUtilities.isFalse(getRolePropertyValue(user, rolePropertyID));
    }   
    
    public String getRolePropertyValueEx(LiteYukonUser user, int rolePropertyID) throws UnknownRolePropertyException {
        String value = getRolePropertyValue(user,rolePropertyID);
        if (value == null) {
            throw new UnknownRolePropertyException(user, rolePropertyID);
        }
        return value;
    }
    
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
    
    public String getRolePropertyValue(int userID, int rolePropertyID) {
        LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(userID);
        Validate.notNull(liteYukonUser, "Could not find a valid LiteYukonUser for userID=" + userID);
        return getRolePropertyValue(liteYukonUser, rolePropertyID);
    }
	
	public String getRolePropValueGroup(LiteYukonGroup group,
                                        int rolePropertyID,
                                        String defaultValue) {
        synchronized (databaseCache) {
            LiteYukonRoleProperty roleProperty = getRoleProperty(rolePropertyID);

            Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> lookupMap =
                databaseCache.getYukonGroupRolePropertyMap();

            Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>> roleMap = lookupMap.get(group);

            if (roleMap != null) {
                for (Map<LiteYukonRoleProperty, String> propMap : roleMap.values()) {
                    String val = propMap.get(roleProperty);
                    if (val != null) {
                        return val;
                    }
                }
            }
        }
        // I'm not sure when this would happen as the loader seems to take care of the default
        return defaultValue;
    }

	public String getRolePropValueGroup(int groupId, int rolePropertyId, String defaultValue) {
        LiteYukonGroup liteYukonGroup = getGroup(groupId);
        Validate.notNull(liteYukonGroup, "Could not find a valid LiteYukonGroup for groupId=" + groupId);
        return getRolePropValueGroup(liteYukonGroup, rolePropertyId, defaultValue);
	}
	
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
	
	public boolean isAdminUser(String username_)
	{
		LiteYukonUser liteUser = yukonUserDao.getLiteYukonUser(username_);
		return isAdminUser(liteUser);
	}

	public boolean isAdminUser(LiteYukonUser user)
	{
	    if ( user != null && user.getUserID() == UserUtils.USER_ADMIN_ID )
	        return true;
	    return false;
	}
	
    public boolean userHasAccessPAO( LiteYukonUser user, int paoID )
    {
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        Set<Integer> permittedPaos = pService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
            
        if( permittedPaos != null && ! permittedPaos.isEmpty()) {
            if(permittedPaos.contains(new Integer(paoID)))
                return true;
            /*the user has permissions found, but the ID was not in the set of
            given IDs, they are not permitted to see this given paoID*/
            else
                return false;
        }
        return true;
    }
    
    public boolean hasExlusiveAccess( LiteYukonUser user, int paoID )
    {
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        Set<Integer> permittedPaos = pService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
            
        if( permittedPaos != null && permittedPaos.contains(new Integer(paoID))) 
            return true;
            
        return false;
    }    

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

	public boolean hasPAOAccess( LiteYukonUser user ) 
	{
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        Set<Integer> permittedPaos = pService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
            
        return permittedPaos != null && ! permittedPaos.isEmpty(); 
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
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
