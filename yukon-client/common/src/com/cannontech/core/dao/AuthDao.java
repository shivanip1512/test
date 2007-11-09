package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface AuthDao {

    /**
     * Attempts to locate user with the given username and password.
     * Returns null if there is no match or the user is disabled.
     * Does not attempt Radius login if user is admin.
     * @param username
     * @param password
     * @return LiteYukonUser
     * @deprecated Please call AuthenticationService.login() directly
     */
    @Deprecated
    public LiteYukonUser login(String username, String password);

    /**
     * Returns LiteYukonRole if the given user 
     * has been granted the given role otherwise null.
     * @param LiteYukonUser
     * @param roleID
     * @return LiteYukonRole
     */
    public LiteYukonRole getRole(LiteYukonUser user, int roleID);


    /**
     * Returns true if the given user has the given role.
     * @param user
     * @param rolePropertyID
     * @return boolean
     */
    public boolean checkRole(LiteYukonUser user, int roleId);
    
    /**
     * Returns true if the given user has a true value for the given property
     * @param user
     * @param rolePropertyID
     * @return boolean
     */
    public boolean checkRoleProperty(LiteYukonUser user, int rolePropertyID);

    public boolean checkRoleProperty(int userID, int rolePropertyID);

    /**
     * Returns the value for a given user and role property.
     * @param user
     * @param rolePropertyID
     * @return String
     * @throws UnknownRolePropertyException If RoleProperty doesn't exist. 
     */
    public String getRolePropertyValueEx(LiteYukonUser user, int rolePropertyID)
            throws UnknownRolePropertyException;

    /**
     * Returns the value for a given user and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param user
     * @param roleProperty
     * @return String
     */
    public String getRolePropertyValue(LiteYukonUser user, int rolePropertyID);

    /**
     * Returns the value for a given userID and rolePropertyID.
     * If no value is found, then the default stored in the database is returned.
     * @param userID the userID on which the property is stored
     * @param rolePropertyID the rolePropertyID to retrieve
     * @return the value of the property
     * @throws IllegalArgumentException if a valid user cannot be found for userID
     */
    public String getRolePropertyValue(int userID, int rolePropertyID);

    /**
     * Returns the value for a given group and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param group
     * @param roleProperty
     * @return String
     */
    public String getRolePropValueGroup(LiteYukonGroup group_,
            int rolePropertyID, String defaultValue);

    /**
     * Returns the value for a given group and role property.
     * If no value is found then defaultValue is returned for convenience.
     * @param groupId
     * @param roleProperty
     * @return String
     */
    public String getRolePropValueGroup(int groupId,
                                        int rolePropertyId, String defaultValue);

    /**
     * Returns a list of roles that are in the given category.
     * @param category
     * @return List
     */
    public List getRoles(String category);

    /**
     * Return a particular lite yukon role given a role id.
     * @param roleid
     * @return
     */
    public LiteYukonRole getRole(int roleid);

    /**
     * Return a particular role property given a role property id.
     * @param propid
     * @return
     */
    public LiteYukonRoleProperty getRoleProperty(int propid);

    /**
     * Return a particular lite yukon group given the group name
     * @param groupName
     * @return LiteYukonGroup
     */
    public LiteYukonGroup getGroup(String groupName);

    /**
     * Return a particular lite yukon group given the group ID
     * @param groupName
     * @return LiteYukonGroup
     */
    public LiteYukonGroup getGroup(int grpID_);

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

    /**
     * Return true if the use has access to a set of PAOids, else return false.
     * 
     * @param LiteYukonUser
     * @return boolean
     */
    public boolean hasPAOAccess(LiteYukonUser user);
    
    public void verifyRole(LiteYukonUser user, int roleId) throws NotAuthorizedException;
    public void verifyTrueProperty(LiteYukonUser user, int rolePropertyId) throws NotAuthorizedException;
    public void verifyFalseProperty(LiteYukonUser user, int rolePropertyId) throws NotAuthorizedException;
    public void verifyAdmin(LiteYukonUser user) throws NotAuthorizedException;


}