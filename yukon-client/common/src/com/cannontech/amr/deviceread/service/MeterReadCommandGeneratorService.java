package com.cannontech.amr.deviceread.service;

import java.util.List;
import java.util.Set;

import com.cannontech.amr.deviceread.dao.impl.UnreadableException;
import com.cannontech.amr.deviceread.model.CommandWrapper;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.Multimap;

public interface MeterReadCommandGeneratorService {

	public  Multimap<YukonDevice, LitePoint> getPointsToRead(YukonDevice device, Set<? extends Attribute> attributes);
	
	public Multimap<YukonDevice, CommandWrapper> getRequiredCommands(Multimap<YukonDevice, LitePoint> pointsToRead) throws UnreadableException;
	
	public List<CommandRequestDevice> getCommandRequests(YukonDevice device, Iterable<CommandWrapper> commands);
}
