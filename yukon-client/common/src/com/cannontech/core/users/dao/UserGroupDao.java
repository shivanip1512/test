package com.cannontech.core.users.dao;

import java.util.List;

import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.user.UserGroup;

public interface UserGroupDao {
    
    public void create(LiteUserGroup liteUserGroup);
    public void update(LiteUserGroup liteUserGroup);
    public void delete(LiteUserGroup liteUserGroup);
    public void delete(int userGroupId);

    /**
     * This method returns the lite user group associated with the supplied user group id.
     */
    public LiteUserGroup getLiteUserGroup(int userGroupId);
    
    /**
     * This method returns the lite user group associated with the supplied user id.
     */
    public LiteUserGroup getLiteUserGroupByUserId(int userId);

    /**
     * This method returns the lite user group associated with the supplied user group name.
     */
    public LiteUserGroup getLiteUserGroupByUserGroupName(String userGroupName);

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