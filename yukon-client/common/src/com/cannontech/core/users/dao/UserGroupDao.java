package com.cannontech.core.users.dao;

import java.util.List;

import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.user.UserGroup;

public interface UserGroupDao {
    
    public void create(com.cannontech.database.db.user.UserGroup userGroup);
    public void update(com.cannontech.database.db.user.UserGroup userGroup);
    public void delete(int userGroupId);

    /**
     * This method returns the lite user group associated with the supplied user group id.
     */
    public com.cannontech.database.db.user.UserGroup getDBUserGroup(int userGroupId);
    
    /**
     * This method returns the lite user group associated with the supplied user group id.
     */
    public LiteUserGroup getLiteUserGroup(int userGroupId);
    
    /**
     * This method returns the lite user group associated with the supplied user id.
     */
    public LiteUserGroup getLiteUserGroupByUserId(int userId);

    /**
     * The method returns the number of user's apart of a user group.
     */
    public int getNumberOfUsers(int userGroupId);
    
    /**
     * This method returns the lite user group associated with the supplied user group name.
     */
    public LiteUserGroup getLiteUserGroupByUserGroupName(String userGroupName);

    /**
     * This method returns the lite user group associated with the supplied user group name.
     */
    public com.cannontech.database.db.user.UserGroup getDBUserGroupByUserGroupName(String userGroupName);

    /**
     * This method returns the lite user group associated with the supplied user group name.
     */
    public com.cannontech.database.db.user.UserGroup findDBUserGroupByUserGroupName(String userGroupName);

    /**
     * This method returns all of the lite user groups that have the supplied role group id.
     */
    public List<LiteUserGroup> getLiteUserGroupsByRoleGroupId(int roleGroupId);

    /**
     * This method returns the lite user group associated with the supplied user group name.
     * It will return null if there is not a user group associated with that user group id.
     */
    public LiteUserGroup findLiteUserGroupByUserGroupName(String userGroupName);

    /**
     * This method returns all the lite user groups in the system.
     */
    public List<LiteUserGroup> getAllLiteUserGroups();

    /**
     * This method returns the user group for the user group id supplied.
     */
    public UserGroup getUserGroup(int userGroupId);
    
    /**
     * This method returns all of the user groups that have the supplied role group id.
     */
    public List<UserGroup> getUserGroupsByRoleGroupId(int roleGroupId);

    /**
     * This method adds the mapping entry between a user group and a yukon group
     */
    public void createUserGroupToYukonGroupMappng(int userGroupId, int roleGroupId);

    /**
     * This method removes the mapping entry between a user group and a yukon group
     */
    public void deleteUserGroupToYukonGroupMappng(int userGroupId, int roleGroupId);
    
}