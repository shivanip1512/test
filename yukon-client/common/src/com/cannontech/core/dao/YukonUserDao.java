package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.user.NewUser;
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

    void changeUsername(LiteYukonUser changingUser, int modifiedUserId, String newUsername);

    /**
     * Returns a LiteYukonUser by user id or null if none exists.
     * 
     * @param userId
     * @return the LiteYukonUser or null if no user with that id exists
     */
    LiteYukonUser getLiteYukonUser(int userId);

    /**
     * Get user authentication information for the given user id. This information is stored in the same
     * database table as the stuff in {@link LiteYukonUser} but needs to be updated via
     * {@link AuthenticationService#setAuthenticationCategory} or {@link AuthenticationService#setPassword}.
     */
    UserAuthenticationInfo getUserAuthenticationInfo(int userId);

    /**
     * Get user authentication information for the given user ids. This information is stored in the
     * same database table as the stuff in {@link LiteYukonUser} but needs to be updated via
     * {@link AuthenticationService#setAuthenticationCategory} or {@link AuthenticationService#setPassword}.
     * 
     * @return a map of userId -> UserAuthenticationInfo
     */
    Map<Integer, UserAuthenticationInfo> getUserAuthenticationInfo(Iterable<Integer> userIds);

    /**
     * Returns a LiteYukonUser by username or null if none exists.
     * 
     * @param userName
     * @return the LiteYukonUser or null if username doesn't exist
     */
    LiteYukonUser findUserByUsername(String userName);

    /**
     * Gets the cached contact for the given user id.
     * 
     * @param userId
     * @return
     */
    LiteContact getLiteContact(int userId);

    /**
     * This method sets all of the EventBase userIds of a given user to the default user. This
     * allows a user to be deleted.
     */
    void removeUserFromEventBase(int userId);

    /**
     * Deletes user from YukonUser by user id.
     * 
     * @param userId
     */
    void deleteUser(Integer userId);

    /**
     * Updates the YukonUser for a give LiteYukonUser.
     * 
     * @param user
     * @throws DataAccessException
     */
    void update(LiteYukonUser user) throws DataAccessException;

    /**
     * Updates the YukonUser's Status('Enabled' or 'Disabled') of a given user
     * 
     * @param user - The user we are updating
     * @param status - This value should be 'Enabled' or 'Disabled'
     */
    void setUserStatus(LiteYukonUser user, LoginStatusEnum loginStatusEnum);

    /**
     * Generates a unique username based on the given firstName and lastName.
     * 
     * @param firstName
     * @param lastName
     * @return newUsername
     */
    String generateUsername(String firstName, String lastName);

    int getAllYukonUserCount();

    /**
     * A Lists of users are given to the callback as all the users in the system are retrieved from the
     * database.
     */
    void callbackWithAllYukonUsers(SimpleCallback<LiteYukonUser> callback);

    /**
     * A Lists of users are given to the callback as they are retrieved in chunks from the role group.
     */
    void callbackWithYukonUsersInGroup(LiteYukonGroup liteGroup, SimpleCallback<LiteYukonUser> simpleCallback);

    /**
     * A Lists of users are given to the callback as they are retrieved in chunks from the user group.
     */
    void callbackWithYukonUsersInUserGroup(UserGroup userGroup, SimpleCallback<LiteYukonUser> simpleCallback);

    /**
     * This method removes the user from their current user group.
     */
    void removeUserFromUserGroup(int userId);

    /**
     * This method removes the users from the user group.
     */
    void removeUsersFromUserGroup(int userGroupId);

    /**
     * This method adds a user to the supplied groups and sends out the necessary db change messages
     * 
     * @param user
     * @param yukonGroups
     */
    void addUserToGroup(int userId, Integer... groupIds);

    /**
     * Creates a login for an additional contact on a stars account. This login will user the first and last
     * name in
     * the generation of the username. The login's auth type will be set to NONE but the login will be enable.
     * It
     * will have no password.
     * 
     */
    LiteYukonUser createLoginForAdditionalContact(String firstName, String lastName, LiteUserGroup userGroup);

    /**
     * Returns a SearchResult<LiteYukonUser> of the users that are members of the group provided
     */
    SearchResults<LiteYukonUser> getUsersForUserGroup(int userGroupId, PagingParameters paging);

    /**
     * This method returns all of the operators that are directly associated with the given energy
     * company ids.
     */
    List<LiteYukonUser> getOperatorLoginsByEnergyCompanyIds(Iterable<Integer> energyCompanyIds);

    /**
     * Inserts or updates a lite yukon user.
     * Requires userId = LiteYukonUser.CREATE_NEW_USER_ID for _new_ user to be created. (Not preferred!)
     * @deprecated Use {@link #create(NewUser, boolean)} instead for user creation.
     */
    void save(LiteYukonUser user);

    /**
     * Updates which user group a user is apart of
     */
    void updateUserGroupId(int userId, Integer userGroupId);

    /**
     * Changes all the forceReset values for a given role group (aka YukonGroup).
     */
    void updateForceResetByRoleGroupId(int roleGroupId, boolean forceReset);

    /** Create a new user */
    LiteYukonUser create(NewUser user, boolean forceReset, LiteYukonUser createdBy);
    
    List<Integer> getUserIdsForUserGroup(int userGroupId);
    
    /** Get count of active non residential users assigned to user group */
    int getNonResidentialUserCount();

    /** Get user group for non residential users */
    List<String> getNonResidentialUserGroups();
    
    /** Get user group with Device Action Yukon Role */
    List<String> getDeviceActionsRoleUserGroups();
}