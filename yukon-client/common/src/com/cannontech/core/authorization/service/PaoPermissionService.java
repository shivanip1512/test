package com.cannontech.core.authorization.service;

import java.util.List;
import java.util.Set;

import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service for user pao permissions
 */
public interface PaoPermissionService {

    /**
     * Method to get a list of user pao permissions for a given user and all of
     * the groups the user is in
     * @param user - User to get permissions for
     * @return All pao permissions for the user and all of the groups the user
     *         is in
     */
    public UserGroupPermissionList getUserPermissions(LiteYukonUser user);

    /**
     * Method to get a list of user pao permissions for a given user and pao
     * @param user - User to get permissions for
     * @param pao - Pao to get permissions for
     * @return All permissions for the given pao for the user and all of the
     *         groups the user is in
     */
    public UserGroupPermissionList getUserPermissionsForPao(LiteYukonUser user,
            LiteYukonPAObject pao);

    /**
     * Method to add a user pao permission
     * @param user - User for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission, boolean allow);

    /**
     * Method to remove a user pao permission
     * @param user - User for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removePermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission);

    /**
     * Method to remove all permissions for a given user.
     * @param userId - Id of user to remove permissions for
     */
    public void removeAllUserPermissions(int userId);

    /**
     * Method to determine if a user has a given permission
     * @param user - User for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission in question
     * @return AuthorizationResponse if the user OR any of the groups the user is in have the
     *         permission for the pao
     */
    public AuthorizationResponse hasPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission);

    /**
     * Method to determine if a group has a permission for a pao
     * @param group - Group to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return AuthorizationResponse for the permission for the pao
     */
    public AuthorizationResponse hasPermission(LiteYukonGroup group, LiteYukonPAObject pao, Permission permission);

    /**
     * Method to determine if any group in the list has a permission for a pao
     * @param groupList - List of groups to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return AuthorizationResponse for the permission for the pao
     */
    public AuthorizationResponse hasPermission(List<LiteYukonGroup> groupList, LiteYukonPAObject pao,
            Permission permission);

    /**
     * Method to get a list of group pao permissions for a given group
     * @param group - Group to get permissions for
     * @return List of permissions
     */
    public List<PaoPermission> getGroupPermissions(LiteYukonGroup group);

    /**
     * Method to get all of the group pao permissions for a list of groups
     * @param groupList - List of groups to get permissions for
     * @return List of permission
     */
    public List<PaoPermission> getGroupPermissions(List<LiteYukonGroup> groupList);

    /**
     * Method to get a list of group pao permissions for a given group and pao
     * @param group - Group to get permissions for
     * @param pao - Pao to get permissions for
     * @return List of permissions
     */
    public List<PaoPermission> getGroupPermissionsForPao(LiteYukonGroup group, LiteYukonPAObject pao);

    /**
     * Method to add a group pao permission
     * @param group - Group for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao, Permission permission, boolean allow);

    /**
     * Method to remove a group pao permission
     * @param group - Group for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removeGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission);

    /**
     * Method to remove all permissions for a given group.
     * @param group - Group to remove permissions for
     */
    public void removeAllGroupPermissions(LiteYukonGroup group);

    /**
     * Method to get a list of pao ids for a given user and permission
     * @param user - User to get pao ids for
     * @param permission - Permission in question
     * @return All pao ids with the given permission for the user and all of the
     *         groups the user is in
     */
    public Set<Integer> getPaoIdsForUserPermission(LiteYukonUser user, Permission permission);

    /**
     * Method to get a list of pao ids for a given user and permission
     * @param user - User to get pao ids for
     * @param permission - Permission in question
     * @return All pao ids with the given permission for the user and not the groups they are in.
     */
    public Set<Integer> getPaoIdsForUserPermissionNoGroup(LiteYukonUser user, Permission permission);    
    
    /**
     * Method to get a list of pao ids for a given login group and permission
     * @param group - Group to get pao ids for
     * @param permission - Permission in question
     * @return All pao ids with the given permission for the login group
     */
    public Set<Integer> getPaoIdsForGroupPermission(LiteYukonGroup group, Permission permission);

    /**
     * Method to remove all user and group permissions for a given pao
     * @param paoId - Id of pao to remove permissions for
     */
    public void removeAllPaoPermissions(int paoId);
}