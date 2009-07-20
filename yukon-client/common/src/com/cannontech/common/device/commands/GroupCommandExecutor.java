package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to execute a command for a group of devices
 */
public interface GroupCommandExecutor {

    public String execute(DeviceCollection deviceCollection, String command, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);
    
    public String execute(final DeviceCollection deviceCollection, final String command, List<CommandRequestDevice> requests, final SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);	
    
    public List<GroupCommandResult> getCompleted();

    public List<GroupCommandResult> getPending();

    public GroupCommandResult getResult(String id);
    
    public long cancelExecution(String resultId, LiteYukonUser user);

}
