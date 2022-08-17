package com.cannontech.dr.controlarea.dao;

import java.util.Set;

import com.cannontech.dr.controlarea.model.ControlArea;

public interface ControlAreaDao {

    public ControlArea getControlArea(int controlAreaId);
    
    public Set<Integer> getProgramIdsForControlArea(int controlAreaId);
    
}
