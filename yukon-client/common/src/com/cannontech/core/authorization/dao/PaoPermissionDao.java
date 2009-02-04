package com.cannontech.core.authorization.dao;

import java.util.List;

import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;

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
     * Method to get a list of all pao permissions for a list of things
     * @param itList - List of things to get permissions for
     * @return List of permissions
     */
    public List<PaoPermission> getPermissions(List<T> itList);

    /**
     * Method to get a list of pao permissions for a given thing and pao
     * @param it - Thing to get permissions for
     * @param pao - Pao to get permissions for
     * @return List of permissions
     */
    public List<PaoPermission> getPermissionsForPao(T it, LiteYukonPAObject pao);

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
    public AuthorizationResponse hasPermissionForPao(T it, LiteYukonPAObject pao, Permission permission);
    
    /**
     * Method to determine if a thing has permission for a pao
     * @param id 
     * @param paoId
     * @param permission
     * @return
     */
    public AuthorizationResponse hasPermissionForPao(int id, int paoId, Permission permission);

    /**
     * Method to determine if any of the things in the list have a given
     * permission for a pao
     * @param itList - List of things to check permission for
     * @param pao - Pao to determine permission for
     * @param permission - Permission in question
     * @return True if any of the things in the list have the permission for the
     *         pao
     */
    public AuthorizationResponse hasPermissionForPao(List<T> itList, LiteYukonPAObject pao, Permission permission);

    /**
     * Method to add a pao permission for a thing
     * @param it - Thing to get permission
     * @param pao - Pao for the permission
     * @param permission - Permission to add
     */
    public void addPermission(T it, LiteYukonPAObject pao, Permission permission, boolean allow);

    /**
     * Method to remove a pao permission for a thing
     * @param it - Thing to remove permission from
     * @param pao - Pao for the permission
     * @param permission - Permission to remove
     */
    public void removePermission(T it, LiteYukonPAObject pao, Permission permission);

    /**
     * Method to remove all permissions for a given thing. (Can be used when
     * deleting the thing)
     * @param it - Thing to remove permissions for
     */
    public void removeAllPermissions(T it);
    
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