package com.cannontech.core.authorization.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.authorization.model.GroupPaoPermission;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Implementation for PaoPermissionService
 */
public class PaoPermissionServiceImpl implements PaoPermissionService {

    PaoPermissionDao paoPermissionDao = null;
    YukonGroupDao groupDao = null;

    public void setPaoPermissionDao(PaoPermissionDao paoPermissionDao) {
        this.paoPermissionDao = paoPermissionDao;
    }

    public void setGroupDao(YukonGroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public UserGroupPermissionList getUserPermissions(LiteYukonUser user) {

        UserGroupPermissionList permissionList = new UserGroupPermissionList();

        // Get permissions for user
        permissionList.setUserPermissionList(paoPermissionDao.getUserPermissions(user));

        // Get permissions for all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        permissionList.setGroupPermissionList(paoPermissionDao.getGroupPermissions(userGroups));

        return permissionList;
    }

    public UserGroupPermissionList getUserPermissionsForPao(LiteYukonUser user,
            LiteYukonPAObject pao) {

        UserGroupPermissionList permissionList = new UserGroupPermissionList();

        // Get permissions for user
        permissionList.setUserPermissionList(paoPermissionDao.getUserPermissionsForPao(user, pao));

        // Get permissions for all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        permissionList.setGroupPermissionList(paoPermissionDao.getGroupPermissions(userGroups));

        return permissionList;
    }

    public void addPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {
        paoPermissionDao.addUserPermission(user, pao, permission);
    }

    public void removePermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {
        paoPermissionDao.removeUserPermission(user, pao, permission);
    }

    public boolean hasPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {

        // Get all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);

        return paoPermissionDao.isUserHasPermissionForPao(user, pao, permission)
                || paoPermissionDao.isGroupHasPermissionForPao(userGroups, pao, permission);
    }

    public void addGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission) {
        paoPermissionDao.addGroupPermission(group, pao, permission);
    }

    public List<GroupPaoPermission> getGroupPermissions(LiteYukonGroup group) {
        return paoPermissionDao.getGroupPermissions(group);
    }

    public List<GroupPaoPermission> getGroupPermissions(List<LiteYukonGroup> groupList) {
        return paoPermissionDao.getGroupPermissions(groupList);
    }

    public List<GroupPaoPermission> getGroupPermissionsForPao(LiteYukonGroup group,
            LiteYukonPAObject pao) {
        return paoPermissionDao.getGroupPermissionsForPao(group, pao);
    }

    public boolean hasPermission(LiteYukonGroup group, LiteYukonPAObject pao, Permission permission) {
        return paoPermissionDao.isGroupHasPermissionForPao(group, pao, permission);
    }

    public boolean hasPermission(List<LiteYukonGroup> groupList, LiteYukonPAObject pao,
            Permission permission) {
        return paoPermissionDao.isGroupHasPermissionForPao(groupList, pao, permission);
    }

    public void removeGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission) {
        paoPermissionDao.removeGroupPermission(group, pao, permission);
    }

    public Set<Integer> getPaoIdsForUserPermission(LiteYukonUser user, Permission permission) {

        // Get paos for user
        List<Integer> userPaoIdList = paoPermissionDao.getPaosForUserPermission(user, permission);
        Set<Integer> paoIdSet = new HashSet<Integer>(userPaoIdList);

        // Get paos for user's groups
        List<Integer> groupPaoIdList = paoPermissionDao.getPaosForGroupPermission(groupDao.getGroupsForUser(user.getUserID()),
                                                                                  permission);
        Set<Integer> groupPaoIdSet = new HashSet<Integer>(groupPaoIdList);

        // Combine the user's paos with all of the paos from the groups
        paoIdSet.addAll(groupPaoIdSet);

        return paoIdSet;
    }
}
