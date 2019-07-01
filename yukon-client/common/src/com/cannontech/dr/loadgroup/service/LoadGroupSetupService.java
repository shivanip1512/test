package com.cannontech.dr.loadgroup.service;

import java.util.List;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMPaoDto;
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
     * Retrieve available load groups except Ecobee,Honeywell,Itron and Nest.
     */
    List<LMPaoDto> retrieveAvailableLoadGroup();

    /**
     * Delete the load group.
     */
    int delete(int loadGroupId, String loadGroupName);

    /**
     * Copy the load group.
     */
    int copy(int loadGroupId, LMCopy lmCopy);
}
