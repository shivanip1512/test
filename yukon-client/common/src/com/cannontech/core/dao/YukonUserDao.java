package com.cannontech.core.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.UserGroup;

public interface YukonUserDao {

    public void changeUsername(LiteYukonUser changingUser, int modifiedUserId, String newUsername);

    /**
     * Returns a LiteYukonUser by user id or null if none exists.
     * @param userId
     * @return the LiteYukonUser or null if no user with that id exists
     */
    public LiteYukonUser getLiteYukonUser(int userId);

    /**
     * Get user authentication information for the given user id.  This information is stored in the same
     * database table as the stuff in {@link LiteYukonUser} but needs to be updated via
     * {@link AuthenticationService#setAuthenticationCategory} or {@link AuthenticationService#setPassword}.
     */
    public UserAuthenticationInfo getUserAuthenticationInfo(int userId);

    /**
     * Returns a LiteYukonUser by username or null if none exists.
     * @param userName
     * @return the LiteYukonUser or null if username doesn't exist
     */
    public LiteYukonUser findUserByUsername(String userName);
    
    /**
     * Gets the cached contact for the given user id.
     * @param userId
     * @return
     */
    public LiteContact getLiteContact(int userId);

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

    /**
     * A Lists of users are given to the callback as all the users in the system are retrieved from the database.
     */
    public void callbackWithAllYukonUsers(SimpleCallback<LiteYukonUser> callback);

    /**
     * A Lists of users are given to the callback as they are retrieved in chunks from the role group.
     */
    public void callbackWithYukonUsersInGroup(LiteYukonGroup liteGroup, SimpleCallback<LiteYukonUser> simpleCallback);

    /**
     * A Lists of users are given to the callback as they are retrieved in chunks from the user group.
     */
    public void callbackWithYukonUsersInUserGroup(UserGroup userGroup, SimpleCallback<LiteYukonUser> simpleCallback);
    
    /**
     * This method removes the user from their current user group.
     */
    public void removeUserFromUserGroup(int userId);

    /**
     * This method adds a user to the supplied groups and sends out the necessary db change messages
     * 
     * @param user
     * @param yukonGroups
     */
    public void addUserToGroup(int userId, Integer... groupIds);

    /**
     * Creates a login for an additional contact on a stars account. This login will user the first and last name in
     * the generation of the username. The login's auth type will be set to NONE but the login will be enable.  It 
     * will have no password.
     * 
     */
    public LiteYukonUser createLoginForAdditionalContact(String firstName, String lastName, LiteUserGroup userGroup);

    /**
     * Returns a SearchResult<LiteYukonUser> of the users that are members of the group provided
     */
    public SearchResult<LiteYukonUser> getUsersForUserGroup(int userGroupId, final int start, final int count);

    /**
     * This method returns all of the operators that are directly associated with the given energy
     * company ids.
     */
    public List<LiteYukonUser> getOperatorLoginsByEnergyCompanyIds(Iterable<Integer> energyCompanyIds);

    /**
     * Inserts or updates a lite yukon user.
     */
    public void save(LiteYukonUser user);

    /**
     * This method returns all of the users associated with the supplied user group id 
     */
    public List<LiteYukonUser> getUsersByUserGroupId(int userGroupId);
    
    /**
     * Updates which user group a user is apart of 
     */
    public void updateUserGroupId(int userId, Integer userGroupId);
    
    /**
     * Changes all the forceReset values for a given group.
     */
    public void updateForceResetByGroupId(int groupId, boolean forceReset);

}