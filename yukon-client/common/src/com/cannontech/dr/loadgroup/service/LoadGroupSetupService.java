package com.cannontech.dr.loadgroup.service;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoIdentifier;

public interface LoadGroupSetupService {

    /**
     * Saves the load group.
     */
    PaoIdentifier save(LoadGroupBase loadGroup);

    /**
     * Retrieve load group for the loadGroupId.
     */
    LoadGroupBase retrieve(int loadGroupId);
}
