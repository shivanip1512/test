package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.ExceptionStatus;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CommandResultHolder extends Completable, ExceptionStatus {

    /**
     * Returns a list of friendly error messages for errors
     * that were received as the last response to each request.
     */
    public List<SpecificDeviceErrorDescription> getErrors();

    /**
     * Returns all of the PointData messages returned by all of the
     * commands that were executed.
     */
    public List<PointValueHolder> getValues();
    
    /**
     * When multiple commands are submitted, this holds the last
     * result string that came back for each request that completed successfully.
     * @return a porter result string, straight out of the Return message.
     */
    public List<String> getResultStrings();
    
    public boolean isAnyErrorOrException();
    public boolean isErrorsExist();
    
    /**
     * A helper method that returns the last entry in the list
     * returned by getLastResultStrings().
     * @return
     */
    public String getLastResultString();
    
    /**
     * CommandRequestExecutionIdentifier for the CommandRequestExecution database table record where command results can be found.
     * @return
     */
    public CommandRequestExecutionIdentifier getCommandRequestExecutionIdentifier();

}