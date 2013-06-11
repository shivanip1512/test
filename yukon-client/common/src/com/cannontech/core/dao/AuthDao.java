package com.cannontech.core.dao;

import java.util.List;
import java.util.TimeZone;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface AuthDao {

    /**
     * Attempts to locate user with the given username and password.
     * Returns null if there is no match or the user is disabled.
     * Does not attempt Radius login if user is admin.
     * @param username
     * @param password
     * @return LiteYukonUser
     * @throws PasswordExpiredException 
     * @deprecated Please call AuthenticationService.login() directly
     */
    @Deprecated
    public LiteYukonUser login(String username, String password) throws PasswordExpiredException;

    /**
     * Returns LiteYukonRole if the given user 
     * has been granted the given role otherwise null.
     * @param LiteYukonUser
     * @param roleID
     * @return LiteYukonRole
     * @deprecated not equivalent, LiteYukonRole shouldn't be used
     */
    @Deprecated
    public LiteYukonRole getRole(LiteYukonUser user, int roleID);

    /**
     * Returns the value for a given userID and rolePropertyID.
     * If no value is found, then the default stored in the database is returned.
     * @param userID the userID on which the property is stored
     * @param rolePropertyID the rolePropertyID to retrieve
     * @return the value of the property
     * @throws IllegalArgumentException if a valid user cannot be found for userID
     * @deprecated use RolePropertyDao.getPropertyStringValue()
     */
    @Deprecated
    public String getRolePropertyValue(int userID, int rolePropertyID);

    /**
     * Returns a list of roles that are in the given category.
     * @param category
     * @return List
     * @deprecated use RolePropertyDao.getRolesForUser()
     */
    @Deprecated
    public List getRoles(String category);

    /**
     * Return a particular lite yukon role given a role id.
     * @param roleid
     * @return
     * @deprecated no equivalent, LiteYukonRoles should be used
     */
    @Deprecated
    public LiteYukonRole getRole(int roleid);

    /**
     * Return true if username_ is the admin user.
     * @param username_
     * @return
     */
    public boolean isAdminUser(String username_);

    /**
     * Return true if username_ is the admin user.
     * @param username_
     * @return
     */
    public boolean isAdminUser(LiteYukonUser user);

    /**
     * Return true if the use has access to the given PAOid.
     * By default, the given user has access to ALL PAOS (backwards compatability)
     * 
     * @param LiteYukonUser, int
     * @return boolean
     */
    public boolean userHasAccessPAO(LiteYukonUser user, int paoID);

    /**
     * Return true if the use has access to the given PAOid, else return false.
     * 
     * @param LiteYukonUser, int
     * @return boolean
     */
    public boolean hasExlusiveAccess(LiteYukonUser user, int paoID);

    /**
     * Attempts to log a voice user into the system using the
     * given ContactID and pin. If the PIN is matched to any PIN 
     * the contact hast, then we are logged in. Returns null if
     * we are not logged in. This method is CASE SENSITIVE for the
     * PIN (however, most of the time the PIN will be numeric only!)
     * 
     */
    public LiteYukonUser voiceLogin(int contactid, String pin);

    public String getFirstNotificationPin(LiteContact contact);
    
    /**
     * Return true if the use has access to a set of PAOids, else return false.
     * 
     * @param LiteYukonUser
     * @return boolean
     */
    public boolean hasPAOAccess(LiteYukonUser user);
        
    /**
     * Check that user has at least one of the role properties.
     * Works using OR semantics
     * <requireRoleProperty> tags.
     * @param user
     * @param rolePropertyIds
     * @throws NotAuthorizedException
     * @deprecated use RolePropertyDao.verifyProperty()
     */
    @Deprecated public void verifyTrueProperty(LiteYukonUser user, int ... rolePropertyIds) throws NotAuthorizedException;
    
    /**
     * @param user
     * @throws NotAuthorizedException
     */
    public void verifyAdmin(LiteYukonUser user) throws NotAuthorizedException;

    /**
     * This method returns the TimeZone for the given user.  
     * First, the WebClientRole.TIMEZONE role property is checked, if found return
     * else if isBlank, then the ConfigurationRole.DEFAULT_TIMEZONE role property is checked, if found return
     * else if isBlank, then return the server timezone.
     * Throws BadConfigurationException when timeZone string value is not valid.
     * Throws IllegalArgumentException when user is null.
     * @param user
     * @return
     */
    public TimeZone getUserTimeZone(LiteYukonUser user) throws BadConfigurationException, IllegalArgumentException;
}