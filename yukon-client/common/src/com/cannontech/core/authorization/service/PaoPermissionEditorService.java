package com.cannontech.core.authorization.service;

import java.util.List;

import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PaoPermissionEditorService<T> {

    /**
     * Method to get a list of paos that the given thing has the given
     * permission for
     * @param it - Thing to get paos for
     * @param permission - Permission the thing must have for the pao
     * @return A list of paos for the thing
     */
    public List<LiteYukonPAObject> getPaos(T it, Permission permission);

    /**
     * Method used to save the permission for each of the paos whose id is in
     * the idList for the given thing
     * @param it - Thing to save permissions for
     * @param idList - List of pao ids to save permissions for
     * @param permission - Permission to save for each pao
     * @return True if save successful
     */
    public boolean savePermissions(T it, List<Integer> idList, Permission permission);

}
