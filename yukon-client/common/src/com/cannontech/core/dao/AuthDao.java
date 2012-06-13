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
     * Returns true if the given user has the given role.
     * @param user
     * @param rolePropertyID
     * @return boolean
     * @deprecated use RolePropertyDao.checkRole()
     */
    @Deprecated
    public boolean checkRole(LiteYukonUser user, int roleId);
    
    /**
     * Returns true if the given user has a true value for the given property
     * @param user
     * @param rolePropertyID
     * @return boolean
     * @deprecated use RolePropertyDao.checkProperty()
     */
    @Deprecated
    public boolean checkRoleProperty(LiteYukonUser user, int rolePropertyID);

    /**
     * @deprecated use RolePropertyDao.checkProperty()
     */
    @Deprecated
    public boolean checkRoleProperty(int userID, int rolePropertyID);

    /**
     * Returns the value for a given user and role property.
     * @param user
     * @param rolePropertyID
     * @return String
     * @throws UnknownRolePropertyException If RoleProperty doesn't exist. 
     * @deprecated use RolePropertyDao.getPropertyStringValue()
     */
    @Deprecated
    public String getRolePropertyValueEx(LiteYukonUser user, int rolePropertyID)
            throws UnknownRolePropertyException;

    /**
     * Returns the value for a given user and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param user
     * @param roleProperty
     * @return String
     * @deprecated use RolePropertyDao.getPropertyStringValue()
     */
    @Deprecated
    public String getRolePropertyValue(LiteYukonUser user, int rolePropertyID);

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
     * Attemps to login a voice user by phone number and voice pin.  The phone number will 
     * be matched against Home Phone and Work Phone.  
     * 
     * @param phoneNumber - User's phone number
     * @param pin - User's pin
     * 
     * @return Logged in user or null if login was unsuccessful
     */
    public LiteYukonUser inboundVoiceLogin(String phoneNumber, String pin);

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
     * @deprecated use RolePropertyDao.verifyRole()
     */
    @Deprecated
    public void verifyRole(LiteYukonUser user, int roleId) throws NotAuthorizedException;
    
    /**
     * Check that user has at least one of the role properties.
     * Works using OR semantics the same as the cti:checkMultiProperty
     * <requireRoleProperty> tags.
     * @param user
     * @param rolePropertyIds
     * @throws NotAuthorizedException
     * @deprecated use RolePropertyDao.verifyProperty()
     */
    @Deprecated public void verifyTrueProperty(LiteYukonUser user, int ... rolePropertyIds) throws NotAuthorizedException;
    
    /**
     * @param user
     * @param rolePropertyId
     * @throws NotAuthorizedException
     * @deprecated use RolePropertyDao.verifyFalseProperty()
     */
    @Deprecated public void verifyFalseProperty(LiteYukonUser user, int rolePropertyId) throws NotAuthorizedException;
    
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

    /**
     * @deprecated use RolePropertyDao.getPropertyEnumValue()
     */
    @Deprecated
    public <E extends Enum<E>> E getRolePropertyValue(Class<E> class1, LiteYukonUser user, int rolePropertyID);
}