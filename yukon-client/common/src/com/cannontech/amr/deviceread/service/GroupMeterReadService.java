package com.cannontech.amr.deviceread.service;

import java.util.List;
import java.util.Set;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface GroupMeterReadService {

	public String readDeviceCollection(DeviceCollection deviceCollection, 
    								   Set<? extends Attribute> attributes, 
    								   DeviceRequestType type, 
    								   SimpleCallback<GroupMeterReadResult> callback, 
    								   LiteYukonUser user);
	
	public CommandRequestExecutionContextId backgroundReadDeviceCollection(DeviceCollection deviceCollection, 
	                                                                    Set<? extends Attribute> attributes, 
	                                                                    DeviceRequestType type, 
	                                                                    CommandCompletionCallback<CommandRequestDevice> callback, 
	                                                                    LiteYukonUser user,
	                                                                    RetryParameters retryParameters);

	public List<GroupMeterReadResult> getCompleted();
	public List<GroupMeterReadResult> getCompletedByType(DeviceRequestType type);

	public List<GroupMeterReadResult> getPending();
    public List<GroupMeterReadResult> getPendingByType(DeviceRequestType type);

    public GroupMeterReadResult getResult(String id);
}
