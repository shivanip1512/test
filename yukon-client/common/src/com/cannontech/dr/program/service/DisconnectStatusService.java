package com.cannontech.dr.program.service;

import java.util.Map;

import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteHardwarePAObject;
import com.cannontech.user.YukonUserContext;

public interface DisconnectStatusService {

    /**
     * 
     * Gets the latest Disconnect Status point values for all devices in the given program, filtered by the given disconnect status values.
     * 
     * @param programId The PAO id of the program to get the devices for.
     * @param disconnectStatus An array of disconnect status values to filter by.
     * @param userContext the YukonUserContext.
     * @return Returns a Map of LiteHardwarePAObject and PointValueHolder
     */
    public Map<LiteHardwarePAObject, PointValueHolder> getDisconnectStatuses(int programId, String[] disconnectStatus, YukonUserContext userContext);

}
