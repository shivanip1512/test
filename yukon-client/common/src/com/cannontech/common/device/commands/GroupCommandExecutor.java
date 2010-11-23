package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.util.ResultResultExpiredException;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to execute a command for a group of devices
 */
public interface GroupCommandExecutor {

	/**
	 * Execute a command on a collection of devices.
	 * Will send standard CommandRequestDevice requests.
	 * @param deviceCollection
	 * @param command
	 * @param commandRequestExecutionType defaults to CommandRequestExecutionType.GROUP_COMMAND if null
	 * @param callback custom code to be called on command completion
	 * @param user
	 * @return
	 */
    public String execute(DeviceCollection deviceCollection, String command, DeviceRequestType commandRequestExecutionType, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);
    
    /**
     * Execute a command on a collection of devices.
     * May take custom CommandRequestDevice requests as parameter.
     * @param deviceCollection
     * @param command
     * @param requests
     * @param commandRequestExecutionType defaults to CommandRequestExecutionType.GROUP_COMMAND if null
     * @param callback custom code to be called on command completion
     * @param user
     * @return
     */
    public String execute(DeviceCollection deviceCollection, String command, List<CommandRequestDevice> requests, DeviceRequestType commandRequestExecutionType, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);	
    
    public List<GroupCommandResult> getCompleted();
    public List<GroupCommandResult> getCompletedByType(DeviceRequestType type);

    public List<GroupCommandResult> getPending();
    public List<GroupCommandResult> getPendingByType(DeviceRequestType type);

    public GroupCommandResult getResult(String id) throws ResultResultExpiredException;
    
    public long cancelExecution(String resultId, LiteYukonUser user) throws ResultResultExpiredException;

}
