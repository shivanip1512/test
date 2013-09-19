package com.cannontech.common.tdc.dao;

import java.util.List;

import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayTypeEnum;

public interface DisplayDao {

    /**
     * Returns a display for display type
     */
    public List<Display> getDisplayByType(DisplayTypeEnum type);

    /**
     * Returns a list of displays for display type
     */
    public Display getDisplayById(int displayId);
}
