package com.cannontech.core.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserDao {

    public void changeUsername(LiteYukonUser changingUser, int modifiedUserId, String newUsername);

    public LiteYukonUser getLiteYukonUser(int userId);

    /**
     * Gets LiteYukonUser by username.
     * @param userName
     * @return the LiteYukonUser or null if userName doesn't exist
     */
    public LiteYukonUser getLiteYukonUser(String userName);
    
    /**
     * Gets the cached contact for the given user id.
     * @param userId
     * @return
     */
    public LiteContact getLiteContact(int userId);

    /**
     * Adds a LiteYukonUser to YukonUser with the given password, energy company, to the specified user groups.
     * @param user
     * @param password
     * @param energyCompanyId
     * @param groups
     * @throws DataAccessException
     */
    public void addLiteYukonUserWithPassword(LiteYukonUser user, String password, int energyCompanyId, List<LiteYukonGroup> groups) throws DataAccessException;

    /**
     * This method sets all of the EventBase userIds of a given user to the default user.  This
     * allows a user to be deleted.
     */
    public void removeUserFromEventBase(int userId);
    
    /**
     * Deletes user from YukonUser by user id.
     * @param userId
     */
    public void deleteUser(Integer userId);

    /**
     * Updates the YukonUser for a give LiteYukonUser.
     * @param user
     * @throws DataAccessException
     */
    public void update(LiteYukonUser user) throws DataAccessException;
    
    /**
     * Updates the YukonUser's Status('Enabled' or 'Disabled') of a given user
     * 
     * @param user - The user we are updating
     * @param status - This value should be 'Enabled' or 'Disabled'
     */
    public void setUserStatus(LiteYukonUser user, LoginStatusEnum loginStatusEnum);
    
    /**
     * Generates a unique username based on the given firstName and lastName.
     * 
     * @param firstName
     * @param lastName
     * @return newUsername
     */
    public String generateUsername(String firstName, String lastName); 
    
    public int getAllYukonUserCount();

    public void callbackWithAllYukonUsers(SimpleCallback<LiteYukonUser> callback);

    public void callbackWithYukonUsersInGroup(
            LiteYukonGroup liteGroup,
            SimpleCallback<LiteYukonUser> simpleCallback);

    
    /**
     * This method removes a user from the supplied groups and sends out the necessary db change messages
     * 
     * @param user
     * @param yukonGroups
     */
    public void removeUserFromGroup(LiteYukonUser user, LiteYukonGroup... yukonGroups);

    /**
     * This method adds a user to the supplied groups and sends out the necessary db change messages
     * 
     * @param user
     * @param yukonGroups
     */
    public void addUserToGroup(LiteYukonUser user, LiteYukonGroup... yukonGroups);

    /**
     * Creates a login for an additional contact on a stars account. This login will user the first and last name in
     * the generation of the username. The login's auth type will be set to NONE but the login will be enable.  It 
     * will have no password.
     * 
     */
    public LiteYukonUser createLoginForAdditionalContact(String firstName, String lastName, LiteYukonGroup group);

    /**
     * This method returns all of the operators that are directly associated with the given energy
     * company ids.
     */
    public List<LiteYukonUser> getOperatorLoginsByEnergyCompanyIds(Iterable<Integer> energyCompanyIds);
 
}