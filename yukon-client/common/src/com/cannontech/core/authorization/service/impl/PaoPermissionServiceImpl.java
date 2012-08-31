package com.cannontech.core.authorization.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Implementation for PaoPermissionService
 */
public class PaoPermissionServiceImpl implements PaoPermissionService {

    @Qualifier("userGroup")
    @Autowired private PaoPermissionDao<LiteUserGroup> userGroupPaoPermissionDao;

    @Qualifier("user")
    @Autowired private PaoPermissionDao<LiteYukonUser> userPaoPermissionDao;

    @Autowired UserGroupDao userGroupDao;

    @Override
    public UserGroupPermissionList getUserPermissions(LiteYukonUser user) {

        UserGroupPermissionList permissionList = new UserGroupPermissionList();

        // Get permissions for user
        permissionList.setUserPermissionList(userPaoPermissionDao.getPermissions(user));

        // Get permissions for all groups that the user is in
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        permissionList.setGroupPermissionList(userGroupPaoPermissionDao.getPermissions(userGroup));

        return permissionList;
    }

    @Override
    public UserGroupPermissionList getUserPermissionsForPao(LiteYukonUser user,
            YukonPao pao) {

        UserGroupPermissionList permissionList = new UserGroupPermissionList();

        // Get permissions for user
        permissionList.setUserPermissionList(userPaoPermissionDao.getPermissionsForPao(user, pao));

        // Get permissions for all groups that the user is in
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        permissionList.setGroupPermissionList(userGroupPaoPermissionDao.getPermissions(userGroup));

        return permissionList;
    }

    @Override
    public void addPermission(LiteYukonUser user, YukonPao pao, Permission permission, boolean allow) {
        validatePermission(permission);
        userPaoPermissionDao.addPermission(user, pao.getPaoIdentifier().getPaoId(), permission, allow);
    }

    @Override
    public void removePermission(LiteYukonUser user, YukonPao pao, Permission permission) {
        validatePermission(permission);
        userPaoPermissionDao.removePermission(user, pao, permission);
    }

    @Override
    public void removeAllUserPermissions(int userId) {
        userPaoPermissionDao.removeAllPermissions(userId);
    }

    @Override
    public AuthorizationResponse hasPermission(LiteYukonUser user, YukonPao pao, Permission permission) {
        if(!permission.isSettablePerPao()) {
            return AuthorizationResponse.UNKNOWN;
        }
        
        AuthorizationResponse ret = userPaoPermissionDao.hasPermissionForPao(user, pao, permission);        
        if( ret != AuthorizationResponse.UNKNOWN) {
            return ret;
        }

        // Get all groups that the user is in
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        ret = userGroupPaoPermissionDao.hasPermissionForPao(userGroup, pao, permission);
        
        return ret;
    }
    
    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               LiteYukonUser user, Permission permission) {

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
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        Collection<PaoIdentifier> unknownUserPaos = 
            userPaoAuthorizations.get(AuthorizationResponse.UNKNOWN);
        Multimap<AuthorizationResponse, PaoIdentifier> groupPaoAuthorizations = 
            userGroupPaoPermissionDao.getPaoAuthorizations(unknownUserPaos, userGroup, permission);
        
        // Add the authorized and unauthorized paos from the user results to the group results to 
        // get the final map of authorizations.  The unknown paos from the user results are not 
        // useful since the group authorizations hopefully shrunk that list.
        groupPaoAuthorizations.putAll(AuthorizationResponse.AUTHORIZED, 
                                      userPaoAuthorizations.get(AuthorizationResponse.AUTHORIZED));

        groupPaoAuthorizations.putAll(AuthorizationResponse.UNAUTHORIZED, 
                                      userPaoAuthorizations.get(AuthorizationResponse.UNAUTHORIZED));
        
        return groupPaoAuthorizations;
    }
    
    @Override
    public void addGroupPermission(LiteUserGroup userGroup, YukonPao pao, Permission permission, boolean allow) {
        validatePermission(permission);
        userGroupPaoPermissionDao.addPermission(userGroup, pao.getPaoIdentifier().getPaoId(), permission, allow);
    }

    @Override
    public List<PaoPermission> getGroupPermissions(LiteUserGroup userGroup) {
        return userGroupPaoPermissionDao.getPermissions(userGroup);
    }

    @Override
    public List<PaoPermission> getGroupPermissions(List<LiteUserGroup> userGroupList) {
        return userGroupPaoPermissionDao.getPermissions(userGroupList);
    }

    @Override
    public List<PaoPermission> getGroupPermissionsForPao(LiteUserGroup userGroup, YukonPao pao) {
        return userGroupPaoPermissionDao.getPermissionsForPao(userGroup, pao);
    }

    @Override
    public AuthorizationResponse hasPermission(LiteUserGroup userGroup, YukonPao pao, Permission permission) {
    	if(permission.equals(Permission.ALLOWED_COMMAND)) {
    		// ALLOWED_COMMAND permission are always allowed
    		return AuthorizationResponse.AUTHORIZED;
    	}
    	
    	return userGroupPaoPermissionDao.hasPermissionForPao(userGroup, pao, permission);
    }

    @Override
    public AuthorizationResponse hasPermission(List<LiteUserGroup> userGroups, YukonPao pao, Permission permission) {
        
    	if(!permission.isSettablePerPao()) {
            return AuthorizationResponse.UNKNOWN;
    	}
    	
        return userGroupPaoPermissionDao.hasPermissionForPao(userGroups, pao, permission);
    }

    @Override
    public void removeGroupPermission(LiteUserGroup userGroup, YukonPao pao, Permission permission) {

        validatePermission(permission);
        userGroupPaoPermissionDao.removePermission(userGroup, pao, permission);
    }

    @Override
    public void removeAllGroupPermissions(LiteUserGroup userGroup) {
        userGroupPaoPermissionDao.removeAllPermissions(userGroup);
    }

    @Override
    public Set<Integer> getPaoIdsForUserPermission(LiteYukonUser user, Permission permission) {

        // Get paos for user
        List<Integer> userPaoIdList = userPaoPermissionDao.getPaosForPermission(user, permission);
        Set<Integer> paoIdSet = Sets.newHashSet(userPaoIdList);

        // Get paos for user's groups
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        List<Integer> groupPaoIdList = userGroupPaoPermissionDao.getPaosForPermission(userGroup, permission);
        Set<Integer> groupPaoIdSet = Sets.newHashSet(groupPaoIdList);

        // Combine the user's paos with all of the paos from the groups
        paoIdSet.addAll(groupPaoIdSet);
        return paoIdSet;
    }
    
    @Override
    public Set<Integer> getPaoIdsForUserPermissionNoGroup(LiteYukonUser user, Permission permission) {

        // Get paos for user
        List<Integer> userPaoIdList = userPaoPermissionDao.getPaosForPermission(user, permission);
        Set<Integer> paoIdSet = Sets.newHashSet(userPaoIdList);

        return paoIdSet;
    }
    
    @Override
    public Set<Integer> getPaoIdsForGroupPermission(LiteUserGroup group, Permission permission) {

        // Get paos for group
        List<LiteUserGroup> groupList = Lists.newArrayList();
        groupList.add(group);
        List<Integer> groupPaoIdList = userGroupPaoPermissionDao.getPaosForPermission(groupList, permission);
        Set<Integer> groupPaoIdSet = Sets.newHashSet(groupPaoIdList);

        return groupPaoIdSet;
    }

    @Override
    public void removeAllPaoPermissions(int paoId) {
        userPaoPermissionDao.removeAllPaoPermissions(paoId);
        userGroupPaoPermissionDao.removeAllPaoPermissions(paoId);
    }

    private void validatePermission(Permission permission) {
        if(!permission.isSettablePerPao()) {
            throw new IllegalArgumentException("Permission not settable per pao.");
        }
    }
}

