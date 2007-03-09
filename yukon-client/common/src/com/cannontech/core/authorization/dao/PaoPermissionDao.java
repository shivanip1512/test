package com.cannontech.core.authorization.dao;

import java.util.List;

import com.cannontech.core.authorization.model.GroupPaoPermission;
import com.cannontech.core.authorization.model.UserPaoPermission;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Dao for user pao permissions
 */
public interface PaoPermissionDao {

    /**
     * Method to get a list of user pao permissions for a given user
     * @param user - User to get permissions for
     * @return List of permissions
     */
    public List<UserPaoPermission> getUserPermissions(LiteYukonUser user);

    /**
     * Method to get a list of user pao permissions for a given user and pao
     * @param user - User to get permissions for
     * @param pao - Pao to get permissions for
     * @return List of permissions
     */
    public List<UserPaoPermission> getUserPermissionsForPao(LiteYukonUser user,
            LiteYukonPAObject pao);

    /**
     * Method to determine if a user has a permission for a pao
     * @param user - User to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return True if the user has the permission for the pao
     */
    public boolean isUserHasPermissionForPao(LiteYukonUser user, LiteYukonPAObject pao,
            Permission permission);

    /**
     * Method to add a user pao permission
     * @param user - User for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addUserPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission);

    /**
     * Method to remove a user pao permission
     * @param user - User for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removeUserPermission(LiteYukonUser user, LiteYukonPAObject pao,
            Permission permission);

    /**
     * Method to get a list of group pao permissions for a given group
     * @param group - Group to get permissions for
     * @return List of permissions
     */
    public List<GroupPaoPermission> getGroupPermissions(LiteYukonGroup group);

    /**
     * Method to get all of the group pao permissions for a list of groups
     * @param groupList - List of groups to get permissions for
     * @return List of permission
     */
    public List<GroupPaoPermission> getGroupPermissions(List<LiteYukonGroup> groupList);

    /**
     * Method to get a list of group pao permissions for a given group and pao
     * @param group - Group to get permissions for
     * @param pao - Pao to get permissions for
     * @return List of permissions
     */
    public List<GroupPaoPermission> getGroupPermissionsForPao(LiteYukonGroup group,
            LiteYukonPAObject pao);

    /**
     * Method to determine if a group has a permission for a pao
     * @param group - Group to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return True if the group has the permission for the pao
     */
    public boolean isGroupHasPermissionForPao(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission);

    /**
     * Method to determine if any group in the list has a permission for a pao
     * @param groupList - List of groups to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return True if any group in the list has the permission for the pao
     */
    public boolean isGroupHasPermissionForPao(List<LiteYukonGroup> groupList,
            LiteYukonPAObject pao, Permission permission);

    /**
     * Method to add a group pao permission
     * @param group - Group for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission);

    /**
     * Method to remove a group pao permission
     * @param group - Group for the permission
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removeGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission);

    /**
     * Method to remove all permissions for a given pao. (Can be used when
     * deleting a pao)
     * @param paoId - Id of pao to remove permissions for
     */
    public void removeAllPaoPermissions(int paoId);
}