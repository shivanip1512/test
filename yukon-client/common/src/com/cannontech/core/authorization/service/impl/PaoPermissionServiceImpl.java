package com.cannontech.core.authorization.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Implementation for PaoPermissionService
 */
public class PaoPermissionServiceImpl implements PaoPermissionService {

    public PaoPermissionDao<LiteYukonGroup> groupPaoPermissionDao = null;
    public PaoPermissionDao<LiteYukonUser> userPaoPermissionDao = null;
    YukonGroupDao groupDao = null;

    public void setGroupPaoPermissionDao(PaoPermissionDao<LiteYukonGroup> groupPaoPermissionDao) {
        this.groupPaoPermissionDao = groupPaoPermissionDao;
    }

    public void setUserPaoPermissionDao(PaoPermissionDao<LiteYukonUser> userPaoPermissionDao) {
        this.userPaoPermissionDao = userPaoPermissionDao;
    }

    public void setGroupDao(YukonGroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public UserGroupPermissionList getUserPermissions(LiteYukonUser user) {

        UserGroupPermissionList permissionList = new UserGroupPermissionList();

        // Get permissions for user
        permissionList.setUserPermissionList(userPaoPermissionDao.getPermissions(user));

        // Get permissions for all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        permissionList.setGroupPermissionList(groupPaoPermissionDao.getPermissions(userGroups));

        return permissionList;
    }

    public UserGroupPermissionList getUserPermissionsForPao(LiteYukonUser user,
            LiteYukonPAObject pao) {

        UserGroupPermissionList permissionList = new UserGroupPermissionList();

        // Get permissions for user
        permissionList.setUserPermissionList(userPaoPermissionDao.getPermissionsForPao(user, pao));

        // Get permissions for all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        permissionList.setGroupPermissionList(groupPaoPermissionDao.getPermissions(userGroups));

        return permissionList;
    }

    public void addPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission, boolean allow) {
        userPaoPermissionDao.addPermission(user, pao, permission, allow);
    }

    public void removePermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {
        userPaoPermissionDao.removePermission(user, pao, permission);
    }

    public void removeAllUserPermissions(int userId) {
        userPaoPermissionDao.removeAllPermissions(userId);
    }

    public AuthorizationResponse hasPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {
        AuthorizationResponse ret = userPaoPermissionDao.hasPermissionForPao(user, pao, permission);        
        if( ret != AuthorizationResponse.UNKNOWN)
            return ret;

        // Get all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        ret = groupPaoPermissionDao.hasPermissionForPao(userGroups, pao, permission);
        
        return ret;
    }

    public void addGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission, boolean allow) {
        groupPaoPermissionDao.addPermission(group, pao, permission, allow);
    }

    public List<PaoPermission> getGroupPermissions(LiteYukonGroup group) {
        return groupPaoPermissionDao.getPermissions(group);
    }

    public List<PaoPermission> getGroupPermissions(List<LiteYukonGroup> groupList) {
        return groupPaoPermissionDao.getPermissions(groupList);
    }

    public List<PaoPermission> getGroupPermissionsForPao(LiteYukonGroup group, LiteYukonPAObject pao) {
        return groupPaoPermissionDao.getPermissionsForPao(group, pao);
    }

    public AuthorizationResponse hasPermission(LiteYukonGroup group, LiteYukonPAObject pao, Permission permission) {
    	if(permission.equals(Permission.ALLOWED_COMMAND)) {
    		// ALLOWED_COMMAND permission are always allowed
    		return AuthorizationResponse.AUTHORIZED;
    	}
    	
    	return groupPaoPermissionDao.hasPermissionForPao(group, pao, permission);
    }

    public AuthorizationResponse hasPermission(List<LiteYukonGroup> groupList, LiteYukonPAObject pao,
            Permission permission) {
    	if(permission.equals(Permission.ALLOWED_COMMAND)) {
    		// ALLOWED_COMMAND permission are always allowed
    		return AuthorizationResponse.AUTHORIZED;
    	}
    	
        return groupPaoPermissionDao.hasPermissionForPao(groupList, pao, permission);
    }

    public void removeGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission) {
        groupPaoPermissionDao.removePermission(group, pao, permission);
    }

    public void removeAllGroupPermissions(LiteYukonGroup group) {
        groupPaoPermissionDao.removeAllPermissions(group);
    }

    public Set<Integer> getPaoIdsForUserPermission(LiteYukonUser user, Permission permission) {

        // Get paos for user
        List<Integer> userPaoIdList = userPaoPermissionDao.getPaosForPermission(user, permission);
        Set<Integer> paoIdSet = new HashSet<Integer>(userPaoIdList);

        // Get paos for user's groups
        List<Integer> groupPaoIdList = groupPaoPermissionDao.getPaosForPermission(groupDao.getGroupsForUser(user.getUserID()),
                                                                                  permission);
        Set<Integer> groupPaoIdSet = new HashSet<Integer>(groupPaoIdList);

        // Combine the user's paos with all of the paos from the groups
        paoIdSet.addAll(groupPaoIdSet);

        return paoIdSet;
    }
    
    public Set<Integer> getPaoIdsForUserPermissionNoGroup(LiteYukonUser user, Permission permission) {

        // Get paos for user
        List<Integer> userPaoIdList = userPaoPermissionDao.getPaosForPermission(user, permission);
        Set<Integer> paoIdSet = new HashSet<Integer>(userPaoIdList);

        return paoIdSet;
    }
    
    public Set<Integer> getPaoIdsForGroupPermission(LiteYukonGroup group, Permission permission) {

        // Get paos for group
        List<LiteYukonGroup> groupList = new ArrayList<LiteYukonGroup>();
        groupList.add(group);
        List<Integer> groupPaoIdList = groupPaoPermissionDao.getPaosForPermission(groupList,
                                                                                  permission);
        Set<Integer> groupPaoIdSet = new HashSet<Integer>(groupPaoIdList);

        return groupPaoIdSet;
    }

    public void removeAllPaoPermissions(int paoId) {
        userPaoPermissionDao.removeAllPaoPermissions(paoId);
        groupPaoPermissionDao.removeAllPaoPermissions(paoId);
    }
}
