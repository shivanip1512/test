package com.cannontech.core.authorization.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.google.common.collect.Multimap;

/**
 * Dao for pao permissions
 */
public interface PaoPermissionDao<T> {

    /**
     * Method to get a list of pao permissions for a given thing
     * @param it - Thing to get permissions for
     * @return List of permissions
     */
    public List<PaoPermission> getPermissions(T it);

    /**
     * Method to get all paoids for which the thing has the given permission
     * @param it - Thing to get paoids for
     * @param permission - Permission in question
     * @return A list of paoids
     */
    public List<Integer> getPaosForPermission(T it, Permission permission);

    /**
     * Method to get all paoids for which the any of the things in the list has
     * the given permission
     * @param itList - List of things to check permission for
     * @param permission - Permission in question
     * @return A list of paoids
     */
    public List<Integer> getPaosForPermission(List<T> itList, Permission permission);

    /**
     * Method to determine if a thing has a permission for a pao
     * @param it - Thing to determine permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return True if the thing has the permission for the pao
     */
    public AuthorizationResponse hasPermissionForPao(T it, YukonPao pao, Permission permission);
    
    /**
     * Method to determine if a thing has permission for a pao
     * @param id 
     * @param paoId
     * @param permission
     * @return
     */
    public AuthorizationResponse hasPermissionForPao(int id, int paoId, Permission permission);

    /**
     * Method to get a list of authorizations for a collection of paos and a permission
     * @param paos - Collection of paos to get permissions for
     * @param it - Thing asking permission
     * @param permission - Permission in question
     * @return - Map of authorization to pao mappings
     */
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               T it, 
                                                                               Permission permission);
    
    /**
     * Method to get a list of authorizations for a collection of paos and a permission
     * @param paos - Collection of paos to get permissions for
     * @param it - List of things asking permission
     * @param permission - Permission in question
     * @return - Map of authorization to pao mappings
     */
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               List<T> it, 
                                                                               Permission permission);
    
    /**
     * Method to add a pao permission for a thing
     * @param it - Thing to get permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addPermission(T it, int paoId, Permission permission, boolean allow);

    /**
     * Method to remove a pao permission for a thing
     * @param it - Thing to remove permission from
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removePermission(T it, YukonPao pao, Permission permission);

    /**
     * Method to remove all matching permissions for a given thing.
     * @param it - Thing to remove permissions for
     */
    public void removeAllPermissions(T it, Permission permission);

    /**
     * Method to remove all permissions for a given thing. (Can be used when
     * deleting the thing)
     * @param id - Id of the thing to remove permissions for
     */
    public void removeAllPermissions(int id);

    /**
     * Method to remove all paoPermissions for a given pao related to the type
     * <T> of this dao
     * @param paoId - Id of pao to remove permissions for
     */
    public void removeAllPaoPermissions(int paoId);

}