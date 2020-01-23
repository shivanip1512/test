package com.cannontech.dr.controlarea.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;

public interface ControlAreaDao {
    public ControlArea getControlArea(int controlAreaId);
    
    public Set<Integer> getProgramIdsForControlArea(int controlAreaId);
    
    Map<Integer, List<ControlAreaTrigger>> getControlAreaTriggers(int controlAreaId);
}
