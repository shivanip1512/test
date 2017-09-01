package com.cannontech.common.tdc.dao;

import java.util.List;

import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayType;
import com.cannontech.core.dao.DuplicateException;

public interface DisplayDao {

    /**
     * Returns a display for display type
     */
    List<Display> getDisplayByType(DisplayType type);

    /**
     * Returns display for display id if found. Else null.
     */
    Display getDisplayById(int displayId);
    
    /**
     * If display id is 0 creates display otherwise updates display, display columns are not updated.
     */
    Display updateDisplay(Display display) throws DuplicateException;

    /**
     * Returns display for display name if found. Else null.
     */
    Display findDisplayByName(String name);

    /**
     * Deletes display.
     */
    void deleteCustomDisplay(int displayId);
}
