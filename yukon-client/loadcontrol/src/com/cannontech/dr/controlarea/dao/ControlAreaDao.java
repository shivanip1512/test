package com.cannontech.dr.controlarea.dao;

import java.util.List;

import com.cannontech.common.device.model.DisplayableDevice;

public interface ControlAreaDao {
    public List<DisplayableDevice> getControlAreas();
    public DisplayableDevice getControlArea(int controlAreaId);
    public DisplayableDevice getControlAreaForProgram(int programId);
}
