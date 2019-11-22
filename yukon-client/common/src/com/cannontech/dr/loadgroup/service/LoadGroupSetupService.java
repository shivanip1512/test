package com.cannontech.dr.loadgroup.service;

import java.util.List;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.dr.setup.service.LMSetupService;

public interface LoadGroupSetupService extends LMSetupService<LoadGroupBase, LMCopy> {

    /**
     * Retrieve available load groups except Ecobee, Honeywell, Itron and Nest.
     */
    List<LMPaoDto> retrieveAvailableLoadGroup();

    /**
     * Retrieve available Control start states with (2 states:with liteId 0 and 1) with the specified pointId.
     */
    List<LMDto> getStartState(int pointId);
}
