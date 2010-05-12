package com.cannontech.core.authorization.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
            YukonPao pao) {

        UserGroupPermissionList permissionList = new UserGroupPermissionList();

        // Get permissions for user
        permissionList.setUserPermissionList(userPaoPermissionDao.getPermissionsForPao(user, pao));

        // Get permissions for all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        permissionList.setGroupPermissionList(groupPaoPermissionDao.getPermissions(userGroups));

        return permissionList;
    }

    public void addPermission(LiteYukonUser user, YukonPao pao, Permission permission, boolean allow) {
        validatePermission(permission);
        userPaoPermissionDao.addPermission(user, pao.getPaoIdentifier().getPaoId(), permission, allow);
    }

    public void removePermission(LiteYukonUser user, YukonPao pao, Permission permission) {
        validatePermission(permission);
        userPaoPermissionDao.removePermission(user, pao, permission);
    }

    public void removeAllUserPermissions(int userId) {
        userPaoPermissionDao.removeAllPermissions(userId);
    }

    public AuthorizationResponse hasPermission(LiteYukonUser user, YukonPao pao, Permission permission) {
        if(!permission.isSettablePerPao()) {
            return AuthorizationResponse.UNKNOWN;
        }
        
        AuthorizationResponse ret = userPaoPermissionDao.hasPermissionForPao(user, pao, permission);        
        if( ret != AuthorizationResponse.UNKNOWN) {
            return ret;
        }

        // Get all groups that the user is in
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        ret = groupPaoPermissionDao.hasPermissionForPao(userGroups, pao, permission);
        
        return ret;
    }
    
    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               LiteYukonUser user,
                                                                               Permission permission) {
        if(!permission.isSettablePerPao()) {
            // Authorization unknown for all paos
            Multimap<AuthorizationResponse, PaoIdentifier> result = ArrayListMultimap.create();     
            result.putAll(AuthorizationResponse.UNKNOWN, paos);
            return result;
        }
        
        // Get user pao authorizations
        Multimap<AuthorizationResponse, PaoIdentifier> userPaoAuthorizations = 
            userPaoPermissionDao.getPaoAuthorizations(paos, user, permission);
        
        // Get group pao authorizations for the user
        List<LiteYukonGroup> userGroups = groupDao.getGroupsForUser(user);
        Collection<PaoIdentifier> unknownUserPaos = 
            userPaoAuthorizations.get(AuthorizationResponse.UNKNOWN);
        Multimap<AuthorizationResponse, PaoIdentifier> groupPaoAuthorizations = 
            groupPaoPermissionDao.getPaoAuthorizations(unknownUserPaos, userGroups, permission);
        
        // Add the authorized and unauthorized paos from the user results to the group results to 
        // get the final map of authorizations.  The unknown paos from the user results are not 
        // useful since the group authorizations hopefully shrunk that list.
        groupPaoAuthorizations.putAll(AuthorizationResponse.AUTHORIZED, 
                                      userPaoAuthorizations.get(AuthorizationResponse.AUTHORIZED));

        groupPaoAuthorizations.putAll(AuthorizationResponse.UNAUTHORIZED, 
                                      userPaoAuthorizations.get(AuthorizationResponse.UNAUTHORIZED));
        
        return groupPaoAuthorizations;
    }
    
    public void addGroupPermission(LiteYukonGroup group, YukonPao pao,
            Permission permission, boolean allow) {
        validatePermission(permission);
        groupPaoPermissionDao.addPermission(group, pao.getPaoIdentifier().getPaoId(), permission, allow);
    }

    public List<PaoPermission> getGroupPermissions(LiteYukonGroup group) {
        return groupPaoPermissionDao.getPermissions(group);
    }

    public List<PaoPermission> getGroupPermissions(List<LiteYukonGroup> groupList) {
        return groupPaoPermissionDao.getPermissions(groupList);
    }

    public List<PaoPermission> getGroupPermissionsForPao(LiteYukonGroup group, YukonPao pao) {
        return groupPaoPermissionDao.getPermissionsForPao(group, pao);
    }

    public AuthorizationResponse hasPermission(LiteYukonGroup group, YukonPao pao, Permission permission) {
    	if(permission.equals(Permission.ALLOWED_COMMAND)) {
    		// ALLOWED_COMMAND permission are always allowed
    		return AuthorizationResponse.AUTHORIZED;
    	}
    	
    	return groupPaoPermissionDao.hasPermissionForPao(group, pao, permission);
    }

    public AuthorizationResponse hasPermission(List<LiteYukonGroup> groupList, YukonPao pao,
            Permission permission) {
        
    	if(!permission.isSettablePerPao()) {
            return AuthorizationResponse.UNKNOWN;
    	}
    	
        return groupPaoPermissionDao.hasPermissionForPao(groupList, pao, permission);
    }

    public void removeGroupPermission(LiteYukonGroup group, YukonPao pao,
            Permission permission) {
        validatePermission(permission);
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

    private void validatePermission(Permission permission) {
        if(!permission.isSettablePerPao()) {
            throw new IllegalArgumentException("Permission not settable per pao.");
        }
    }
}

