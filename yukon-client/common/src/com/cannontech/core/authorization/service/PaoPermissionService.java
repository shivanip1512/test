package com.cannontech.core.authorization.service;

import java.util.Collection;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

/**
 * Service for user pao permissions
 */
public interface PaoPermissionService {

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
     * Method to get a list of pao ids for a given user and permission
     * @param user - User to get pao ids for
     * @param permission - Permission in question
     * @return All pao ids with the given permission for the user and all of the userGroups the user is in
     */
    public Set<Integer> getPaoIdsForUserPermission(LiteYukonUser user, Permission permission);

    /**
     * Method to remove all user and userGroup permissions for a given pao
     * @param paoId - Id of pao to remove permissions for
     */
    public void removeAllPaoPermissions(int paoId);
}