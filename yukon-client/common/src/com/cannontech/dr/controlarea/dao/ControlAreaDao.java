package com.cannontech.dr.controlarea.dao;

import java.util.Set;

import com.cannontech.dr.controlarea.model.ControlArea;
import com.google.common.collect.Multimap;

public interface ControlAreaDao {

    public ControlArea getControlArea(int controlAreaId);
    
    public Set<Integer> getProgramIdsForControlArea(int controlAreaId);

    /**
     * Returns the map of controlAreaIds to groupIds
     */
    Multimap<Integer, Integer> getGroupIdsByControlAreaId(Set<Integer> controlAreaIds);
}
