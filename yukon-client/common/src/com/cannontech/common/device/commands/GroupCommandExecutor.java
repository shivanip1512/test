package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.bulk.collection.DeviceCollection;
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
    public String execute(DeviceCollection deviceCollection, String command, CommandRequestExecutionType commandRequestExecutionType, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);
    
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
    public String execute(DeviceCollection deviceCollection, String command, List<CommandRequestDevice> requests, CommandRequestExecutionType commandRequestExecutionType, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);	
    
    public List<GroupCommandResult> getCompleted();
    public List<GroupCommandResult> getCompletedByType(CommandRequestExecutionType type);

    public List<GroupCommandResult> getPending();
    public List<GroupCommandResult> getPendingByType(CommandRequestExecutionType type);

    public GroupCommandResult getResult(String id);
    
    public long cancelExecution(String resultId, LiteYukonUser user);

}
