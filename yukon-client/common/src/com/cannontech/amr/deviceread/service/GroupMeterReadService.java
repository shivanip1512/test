package com.cannontech.amr.deviceread.service;

import java.util.List;
import java.util.Set;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface GroupMeterReadService {

	public String readDeviceCollection(DeviceCollection deviceCollection, 
																final Set<? extends Attribute> attributes, 
																CommandRequestExecutionType type, 
																SimpleCallback<GroupMeterReadResult> callback, 
																LiteYukonUser user);

	public List<GroupMeterReadResult> getCompleted();
	public List<GroupMeterReadResult> getCompletedByType(CommandRequestExecutionType type);

	public List<GroupMeterReadResult> getPending();
    public List<GroupMeterReadResult> getPendingByType(CommandRequestExecutionType type);

    public GroupMeterReadResult getResult(String id);
}
