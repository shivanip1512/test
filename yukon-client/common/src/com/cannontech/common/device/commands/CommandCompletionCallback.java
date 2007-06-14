/**
 * 
 */
package com.cannontech.common.device.commands;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CommandCompletionCallback {
    public void receivedError(DeviceErrorDescription error);
    public void receivedValue(PointValueHolder value);
    public void receivedResultString(String value);
    public void complete();
}