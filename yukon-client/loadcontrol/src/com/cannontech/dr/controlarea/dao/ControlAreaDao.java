package com.cannontech.dr.controlarea.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;

public interface ControlAreaDao {
    public List<DisplayablePao> getControlAreas();
    public DisplayablePao getControlArea(int controlAreaId);
    public DisplayablePao getControlAreaForProgram(int programId);
}
