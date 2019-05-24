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
}
