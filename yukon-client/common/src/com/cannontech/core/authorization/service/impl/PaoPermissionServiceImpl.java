package com.cannontech.core.authorization.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Implementation for PaoPermissionService
 */
public class PaoPermissionServiceImpl implements PaoPermissionService {
    @Autowired private @Qualifier("userGroup") PaoPermissionDao<LiteUserGroup> userGroupPaoPermissionDao;
    @Autowired private @Qualifier("user") PaoPermissionDao<LiteYukonUser> userPaoPermissionDao;

    @Autowired UserGroupDao userGroupDao;

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
    public AuthorizationResponse hasPermission(LiteYukonUser user, YukonPao pao, Permission permission) {
        // Get all groups that the user is in
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        if (!permission.isSettablePerPao() || userGroup == null) {
            return AuthorizationResponse.UNKNOWN;
        }

        AuthorizationResponse ret = userPaoPermissionDao.hasPermissionForPao(user, pao, permission);
        if (ret != AuthorizationResponse.UNKNOWN) {
            return ret;
        }

        ret = userGroupPaoPermissionDao.hasPermissionForPao(userGroup, pao, permission);

        return ret;
    }

    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
            LiteYukonUser user, Permission permission) {

        // Get group pao authorizations for the user
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        if (!permission.isSettablePerPao() || userGroup == null) {
            // Authorization unknown for all paos
            Multimap<AuthorizationResponse, PaoIdentifier> result = ArrayListMultimap.create();
            result.putAll(AuthorizationResponse.UNKNOWN, paos);
            return result;
        }

        // Get user pao authorizations
        Multimap<AuthorizationResponse, PaoIdentifier> userPaoAuthorizations =
            userPaoPermissionDao.getPaoAuthorizations(paos, user, permission);

        Collection<PaoIdentifier> unknownUserPaos = userPaoAuthorizations.get(AuthorizationResponse.UNKNOWN);
        Multimap<AuthorizationResponse, PaoIdentifier> groupPaoAuthorizations =
            userGroupPaoPermissionDao.getPaoAuthorizations(unknownUserPaos, userGroup, permission);

        // Add the authorized and unauthorized paos from the user results to the group results to
        // get the final map of authorizations. The unknown paos from the user results are not
        // useful since the group authorizations hopefully shrunk that list.
        groupPaoAuthorizations.putAll(AuthorizationResponse.AUTHORIZED,
            userPaoAuthorizations.get(AuthorizationResponse.AUTHORIZED));

        groupPaoAuthorizations.putAll(AuthorizationResponse.UNAUTHORIZED,
            userPaoAuthorizations.get(AuthorizationResponse.UNAUTHORIZED));

        return groupPaoAuthorizations;
    }

    @Override
    public Set<Integer> getPaoIdsForUserPermission(LiteYukonUser user, Permission permission) {

        // Get paos for user
        List<Integer> userPaoIdList = userPaoPermissionDao.getPaosForPermission(user, permission);
        Set<Integer> paoIdSet = Sets.newHashSet(userPaoIdList);

        // Get paos for user's groups
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(user.getUserID());
        if (userGroup != null) {
            List<Integer> groupPaoIdList = userGroupPaoPermissionDao.getPaosForPermission(userGroup, permission);
            // Combine the user's paos with all of the paos from the groups
            paoIdSet.addAll(groupPaoIdList);
        }
        return paoIdSet;
    }

    @Override
    public void removeAllPaoPermissions(int paoId) {
        userPaoPermissionDao.removeAllPaoPermissions(paoId);
        userGroupPaoPermissionDao.removeAllPaoPermissions(paoId);
    }

    private void validatePermission(Permission permission) {
        if (!permission.isSettablePerPao()) {
            throw new IllegalArgumentException("Permission not settable per pao.");
        }
    }
}
