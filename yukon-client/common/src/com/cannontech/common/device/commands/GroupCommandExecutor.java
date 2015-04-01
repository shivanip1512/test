package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.util.ResultExpiredException;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to execute a command for a group of devices
 */
public interface GroupCommandExecutor {
	/**
	 * Execute a command on a collection of devices.
     * Will send standard {@link CommandRequestDevice} requests.
	 * @param callback custom code to be called on command completion
	 */
    public String execute(DeviceCollection deviceCollection, String command,
                          DeviceRequestType commandRequestExecutionType,
                          SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);

    /**
     * Execute a command on a collection of devices.
     * May take custom {@link CommandRequestDevice} requests as parameter.
     * @param callback custom code to be called on command completion
     */
    public String execute(DeviceCollection deviceCollection, String command,
                          List<CommandRequestDevice> requests,
                          DeviceRequestType commandRequestExecutionType,
                          SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);

    public List<GroupCommandResult> getCompleted();
    public List<GroupCommandResult> getCompletedByType(DeviceRequestType type);

    public List<GroupCommandResult> getPending();
    public List<GroupCommandResult> getPendingByType(DeviceRequestType type);

    public GroupCommandResult getResult(String id) throws ResultExpiredException;

    public long cancelExecution(String resultId, LiteYukonUser user) throws ResultExpiredException;
}
