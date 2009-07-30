package com.cannontech.amr.deviceread.service;

import java.util.List;
import java.util.Set;

import com.cannontech.amr.deviceread.dao.impl.UnreadableException;
import com.cannontech.amr.deviceread.model.CommandWrapper;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.Multimap;

public interface MeterReadCommandGeneratorService {

	public  Multimap<SimpleDevice, LitePoint> getPointsToRead(SimpleDevice device, Set<? extends Attribute> attributes);
	
	public Multimap<SimpleDevice, CommandWrapper> getRequiredCommands(Multimap<SimpleDevice, LitePoint> pointsToRead) throws UnreadableException;
	
	public List<CommandRequestDevice> getCommandRequests(SimpleDevice device, Iterable<CommandWrapper> commands);
}
