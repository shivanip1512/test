package com.cannontech.dr.loadgroup.service;

import java.util.List;

import com.cannontech.common.dr.setup.ControlRawState;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.dr.setup.service.LMSetupService;

public interface LoadGroupSetupService extends LMSetupService<LoadGroupBase, LMCopy> {

    /**
     * Retrieve available load groups except Ecobee, Honeywell, Itron and Nest.
     */
    List<LMPaoDto> retrieveAvailableLoadGroup();

    /**
     * Retrieve available control start states with rawState of 0 or 1 for the specified pointId.
     */
    List<ControlRawState> getStartState(int pointId);
}
