package com.cannontech.core.authorization.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

/**
 * Service for user pao permissions
 */
public interface PaoPermissionService {

    /**
     * Method to get a list of user pao permissions for a given user and all of
     * the groups the user is in
     * @param user - User to get permissions for
     * @return All pao permissions for the user and all of the groups the user is in
     */
    public UserGroupPermissionList getUserPermissions(LiteYukonUser user);

    /**
     * Method to get a list of user pao permissions for a given user and pao
     * @param user - User to get permissions for
     * @param pao - Pao to get permissions for
     * @return All permissions for the given pao for the user and all of the groups the user is in
     */
    public UserGroupPermissionList getUserPermissionsForPao(LiteYukonUser user, YukonPao pao);

    /**
     * Method to add a user pao permission
     * @param user - User for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addPermission(LiteYukonUser user, YukonPao pao, Permission permission, boolean allow);

    /**
     * Method to remove a user pao permission
     * @param user - User for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removePermission(LiteYukonUser user, YukonPao pao, Permission permission);

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
     * @return AuthorizationResponse if the user OR any of the userGroups the user is in have the
     *         permission for the pao
     */
    public AuthorizationResponse hasPermission(LiteYukonUser user, YukonPao pao, Permission permission);

    /**
     * Method to get a list of authorizations for a collection of paos, a user and a permission
     * @param paos - Collection of paos to get permissions for
     * @param user - User asking permission
     * @param permission - Permission in question
     * @return - Map of authorization to pao mappings
     */
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               LiteYukonUser user, 
                                                                               Permission permission);
    
    /**
     * Method to determine if a userGroup has a permission for a pao
     * @param userGroup - Group to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return AuthorizationResponse for the permission for the pao
     */
    public AuthorizationResponse hasPermission(LiteUserGroup userGroup, YukonPao pao, Permission permission);

    /**
     * Method to determine if any userGroup in the list has a permission for a pao
     * @param userGroupList - List of userGroups to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return AuthorizationResponse for the permission for the pao
     */
    public AuthorizationResponse hasPermission(List<LiteUserGroup> userGroupList, YukonPao pao,
            Permission permission);

    /**
     * Method to get a list of group pao permissions for a given userGroup
     * @param userGroup - Group to get permissions for
     * @return List of permissions
     */
    public List<PaoPermission> getGroupPermissions(LiteUserGroup userGroup);

    /**
     * Method to get all of the group pao permissions for a list of userGroups
     * @param userGroupList - List of userGroups to get permissions for
     * @return List of permission
     */
    public List<PaoPermission> getGroupPermissions(List<LiteUserGroup> userGroupList);

    /**
     * Method to get a list of group pao permissions for a given userGroup and pao
     * @param userGroup - Group to get permissions for
     * @param pao - Pao to get permissions for
     * @return List of permissions
     */
    public List<PaoPermission> getGroupPermissionsForPao(LiteUserGroup userGroup, YukonPao pao);

    /**
     * Method to add a group pao permission
     * @param userGroup - Group for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addGroupPermission(LiteUserGroup userGroup, YukonPao pao, Permission permission, boolean allow);

    /**
     * Method to remove a userGroup pao permission
     * @param userGroup - Group for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removeGroupPermission(LiteUserGroup userGroup, YukonPao pao,
            Permission permission);

    /**
     * Method to remove all permissions for a given userGroup.
     * @param userGroup - Group to remove permissions for
     */
    public void removeAllGroupPermissions(LiteUserGroup userGroup);

    /**
     * Method to get a list of pao ids for a given user and permission
     * @param user - User to get pao ids for
     * @param permission - Permission in question
     * @return All pao ids with the given permission for the user and all of the userGroups the user is in
     */
    public Set<Integer> getPaoIdsForUserPermission(LiteYukonUser user, Permission permission);

    /**
     * Method to get a list of pao ids for a given user and permission
     * @param user - User to get pao ids for
     * @param permission - Permission in question
     * @return All pao ids with the given permission for the user and not the userGroups they are in.
     */
    public Set<Integer> getPaoIdsForUserPermissionNoGroup(LiteYukonUser user, Permission permission);    
    
    /**
     * Method to get a list of pao ids for a given user group and permission
     * @param userGroup - Group to get pao ids for
     * @param permission - Permission in question
     * @return All pao ids with the given permission for the user group
     */
    public Set<Integer> getPaoIdsForGroupPermission(LiteUserGroup userGroup, Permission permission);

    /**
     * Method to remove all user and userGroup permissions for a given pao
     * @param paoId - Id of pao to remove permissions for
     */
    public void removeAllPaoPermissions(int paoId);
}