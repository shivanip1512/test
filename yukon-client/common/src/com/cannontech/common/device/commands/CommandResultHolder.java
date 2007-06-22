package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CommandResultHolder {

    /**
     * Returns a list of friendly error messages for errors
     * that were received while executing the request.
     */
    public List<DeviceErrorDescription> getErrors();

    /**
     * Returns all of the PointData messages returned by all of the
     * commands that were executed.
     */
    public List<PointValueHolder> getValues();
    
    /**
     * When multiple commands are submitted, this holds the last
     * result string that came back for each command.
     * @return a porter result string, straight out of the Return message.
     */
    public List<String> getLastResultStrings();
    
    public boolean isErrorsExist();
    
    /**
     * A helper method that returns the last entry in the list
     * returned by getLastResultStrings().
     * @return
     */
    public String getLastResultString();

}