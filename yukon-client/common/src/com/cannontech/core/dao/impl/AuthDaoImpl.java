package com.cannontech.core.dao.impl;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;
import com.google.common.collect.Lists;


/**
 * A collection of methods to handle authenticating, authorization, and the retrieval of 
 * role property values.
 */
public class AuthDaoImpl implements AuthDao {
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private ContactDao contactDao;
    @Autowired private SystemDateFormattingService systemDateFormattingService;
    @Autowired private RolePropertyDao rolePropertyDao;
	
	@Override
    public boolean isAdminUser(String username)
	{
		LiteYukonUser liteUser = yukonUserDao.findUserByUsername(username);
		return isAdminUser(liteUser);
	}

	@Override
    public boolean isAdminUser(LiteYukonUser user)
	{
	    if ( user != null && user.getUserID() == UserUtils.USER_ADMIN_ID )
	        return true;
	    return false;
	}
	
    @Override
    public boolean userHasAccessPAO( LiteYukonUser user, int paoId )
    {
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        Set<Integer> permittedPaos = pService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
            
        if( permittedPaos != null && ! permittedPaos.isEmpty()) {
            if(permittedPaos.contains(paoId))
                return true;
            /*the user has permissions found, but the ID was not in the set of
            given IDs, they are not permitted to see this given paoID*/
            else
                return false;
        }
        return true;
    }
    
    @Override
    public boolean hasExlusiveAccess( LiteYukonUser user, int paoId )
    {
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        Set<Integer> permittedPaos = pService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
            
        if( permittedPaos != null && permittedPaos.contains(paoId)) 
            return true;
            
        return false;
    }    

	@Override
    public LiteYukonUser voiceLogin(int contactId, String pin) {
			
		LiteContact lContact = contactDao.getContact( contactId );

		if( lContact != null )
		{
			CTILogger.info("Attempting a VOICE login with the following Contact info: " + lContact.toString());
			
			LiteYukonUser lYukUser = yukonUserDao.getLiteYukonUser( lContact.getLoginID() );
			if( lYukUser != null ) {
				if (lYukUser.getUserID() == UserUtils.USER_NONE_ID) {
					CTILogger.info("  Failed VOICE login because the YukonUser found was the (none) userid, Contact: " + lContact.toString());
				} else if (lYukUser.getLoginStatus().isDisabled()) {
					CTILogger.info("  Failed VOICE login because the YukonUser found is DISABLED, Contact: " + lContact.toString());			
				} else {
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
			CTILogger.info("  Failed VOICE login since no Contact exists with a Contactid = " + contactId);


		return null;  //failure
	}
	
	@Override
    public String getFirstNotificationPin(LiteContact contact) {
	    Validate.notNull(contact);

	    List<String> result = Lists.newArrayListWithExpectedSize(1);

	    LiteContactNotification[] pins = contactDao.getAllPINNotifDestinations( contact.getContactID() );
	    for (LiteContactNotification liteContactNotification : pins) {
	        if( liteContactNotification.isDisabled() ) {
	            CTILogger.debug("Skipping PIN because it is DISABLED, Contact: " + contact.toString());
	        } else {
	            String pin = liteContactNotification.getNotification();
                result.add(pin);
	        }
	    }
	    CTILogger.debug("Found PINs for " + contact + " (returning first): " + result);
	    return result.get(0);
	}

	@Override
    public boolean hasPAOAccess( LiteYukonUser user ) 
	{
        PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
        Set<Integer> permittedPaos = pService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
            
        return permittedPaos != null && ! permittedPaos.isEmpty(); 
	}
    	
    @Override
    public void verifyAdmin(LiteYukonUser user) throws NotAuthorizedException {
        boolean b = isAdminUser(user);
        if (!b) {
            throw NotAuthorizedException.adminUser(user);
        }
    }

    @Override
    public TimeZone getUserTimeZone(LiteYukonUser user) {
        
        if (user == null)
            throw new IllegalArgumentException("User cannot be null.");

        TimeZone timeZone;
        String timeZoneStr = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_TIMEZONE, user);
        
        if (StringUtils.isNotBlank(timeZoneStr)) {
            try {
                timeZone = CtiUtilities.getValidTimeZone(timeZoneStr);
                CTILogger.debug("WebClient Role Default TimeZone found: " + timeZone.getDisplayName());
            } catch (BadConfigurationException e) {
                throw new BadConfigurationException (e.getMessage() + ".  Invalid value in YukonRoleProperty Default TimeZone property.");
            }
        } else {
            timeZone = systemDateFormattingService.getSystemTimeZone();
        }
        return timeZone;
    }
}
