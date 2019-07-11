package com.cannontech.dr.area.service;

import java.util.List;

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.LMDto;

public interface ControlAreaSetupService {

    /**
     * Retrieve Control Area for area id.
     */
    ControlArea retrieve(int areaId);

    /**
     * Create the Control Area.
     */
    int create(ControlArea controlArea);

    /**
     * Update the Control Area.
     */
    int update(int areaId, ControlArea controlArea);

    /**
     * Delete the Control Area.
     */
    int delete(int areaId, String areaName);

    /**
     * Retrieve List of Unassigned Programs.
     */
    List<LMDto> retrieveUnassignedPrograms();

    /**
     * Retrieve Normal states for point Id.
     */
    List<LMDto> retrieveNormalState(int pointId);

}
