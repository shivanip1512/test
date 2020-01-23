package com.cannontech.dr.controlarea.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;

public interface ControlAreaDao {

    /**
     * Get Control area and its associated triggers.
     */
    public ControlArea getControlArea(int controlAreaId);

    /**
     * Get List of programs Id assigned to a control area.
     */
    public Set<Integer> getProgramIdsForControlArea(int controlAreaId);

    /**
     * Get triggers associated with a control area.
     */
    Map<Integer, List<ControlAreaTrigger>> getControlAreaTriggers(int controlAreaId);
}
