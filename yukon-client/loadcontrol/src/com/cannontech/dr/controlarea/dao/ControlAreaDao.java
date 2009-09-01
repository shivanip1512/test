package com.cannontech.dr.controlarea.dao;

import java.util.List;

import com.cannontech.dr.controlarea.model.ControlArea;

public interface ControlAreaDao {
    public List<ControlArea> getControlAreas();
    public ControlArea getControlArea(int controlAreaId);
    public ControlArea findControlAreaForProgram(int programId);
}
