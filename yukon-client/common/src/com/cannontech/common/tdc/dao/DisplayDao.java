package com.cannontech.common.tdc.dao;

import java.util.List;

import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayTypeEnum;

public interface DisplayDao {

    /**
     * Method to a return a display for display type
     * @param type
     * @return 
     */
    public List<Display> getDisplayByType(DisplayTypeEnum type);

    /**
     *  Method to a return a list of displays for display type
     * @param displayId
     * @return 
     */
    public Display getDisplayById(int displayId);
}
