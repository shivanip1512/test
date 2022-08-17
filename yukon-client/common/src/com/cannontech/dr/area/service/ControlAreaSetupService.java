package com.cannontech.dr.area.service;

import java.util.List;

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.dr.setup.service.LMSetupService;

public interface ControlAreaSetupService extends LMSetupService<ControlArea, LMCopy>{

    /**
     * Retrieve List of Unassigned Programs.
     */
    List<LMDto> retrieveUnassignedPrograms();

    /**
     * Retrieve Normal states for point Id.
     */
    List<LMDto> retrieveNormalState(int pointId);

}
