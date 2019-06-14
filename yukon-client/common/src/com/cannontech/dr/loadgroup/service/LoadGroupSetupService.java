package com.cannontech.dr.loadgroup.service;

import com.cannontech.common.dr.setup.LoadGroupBase;

public interface LoadGroupSetupService {

    /**
     * Saves the load group.
     */
    int save(LoadGroupBase loadGroup);

    /**
     * Retrieve load group for the loadGroupId.
     */
    LoadGroupBase retrieve(int loadGroupId);

    /**
     * Delete the load group.
     */
    int delete(int loadGroupId, String loadGroupName);

    /**
     * Copy the load group.
     */
    int copy(int loadGroupID, String loadGroupName);
}
