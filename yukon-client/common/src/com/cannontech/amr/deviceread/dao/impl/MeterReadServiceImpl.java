package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MeterReadServiceImpl implements MeterReadService {
    private Logger log = YukonLogManager.getLogger(MeterReadServiceImpl.class);
    private AttributeService attributeService;
    private DeviceDao deviceDao;
    private DeviceDefinitionDao deviceDefinitionDao;
    private CommandRequestDeviceExecutor commandExecutor;
    
    private boolean isUpdate = true;
    private boolean isNoqueue = true;
    
    public boolean isReadable(Meter device, Set<? extends Attribute> attributes, LiteYukonUser user) {
    	log.debug("Validating Readability for" + attributes + " on device " + device + " for " + user);
    	
        Multimap<YukonDevice, LitePoint> pointsToRead = getPointsToRead(device, attributes);
    	
    	try {
            Multimap<YukonDevice, CommandWrapper> requiredCommands = getRequiredCommands(pointsToRead);
            // just let the above throw? or maybe we need a check?
//    	if (requiredCommands.isEmpty()) {
//    	    log.debug("Not Readable: No commands defined to read " + pointSet + " for device type " + device.getType());
//    	    return false;
//    	}
        } catch (UnreadableException e) {
            return false;
        }

    	return true;
    }

    private Multimap<YukonDevice, LitePoint> getPointsToRead(Meter device,
            Set<? extends Attribute> attributes) {
        // reduce number of commands
    	Multimap<YukonDevice, LitePoint> pointsToRead = HashMultimap.create();
    	for (Attribute attribute : attributes) {
    	    // consider wrapping in try/catch and returning false if this fails
            LitePoint pointForAttribute = attributeService.getPointForAttribute(device, attribute);
            YukonDevice deviceForPoint = null;
            if (pointForAttribute.getPaobjectID() == device.getDeviceId()) {
                // prevent DAO call for common case
                deviceForPoint = device;
            } else {
                deviceForPoint = deviceDao.getYukonDevice(pointForAttribute.getPaobjectID());
            }
            pointsToRead.put(deviceForPoint, pointForAttribute);
        }
        return pointsToRead;
    }

    private Multimap<YukonDevice, CommandWrapper> getRequiredCommands(
            Multimap<YukonDevice, LitePoint> pointsToRead) throws UnreadableException {
        Multimap<YukonDevice, CommandWrapper> requiredCommands = HashMultimap.create();
    	
    	for (YukonDevice deviceToRead : pointsToRead.keySet()) {
    	    Set<PointIdentifier> pointSet = convertToDevicePointIdentifiers(pointsToRead.get(deviceToRead));
    	    Set<CommandWrapper> minimalCommands = getMinimalCommandSet(deviceToRead, pointSet);
            if (minimalCommands == null) {
                throw new UnreadableException();
            }
            requiredCommands.putAll(deviceToRead, minimalCommands);
        }
        return requiredCommands;
    }
    
    private Set<PointIdentifier> convertToDevicePointIdentifiers(Iterable<LitePoint> collection) {
        Set<PointIdentifier> result = Sets.newHashSet();
        for (LitePoint point : collection) {
            PointIdentifier pointIdentifier = new PointIdentifier(point.getPointType(), point.getPointOffset());
            result.add(pointIdentifier);
        }
        return result;
    }

    public CommandResultHolder readMeter(Meter device, Set<? extends Attribute> attributes, LiteYukonUser user) {
        log.info("Reading " + attributes + " on device " + device + " for " + user);
        
        // figure out which commands to send
        Multimap<YukonDevice, LitePoint> pointsToRead = getPointsToRead(device, attributes);
        
        Multimap<YukonDevice, CommandWrapper> requiredCommands;
        try {
            requiredCommands = getRequiredCommands(pointsToRead);
        } catch (UnreadableException e) {
            throw new RuntimeException("It isn't possible to read " + attributes + " for  " + device, e);
        }
        return readMeterPoints(requiredCommands, user);
    }
    
    private CommandResultHolder readMeterPoints(Multimap<YukonDevice, CommandWrapper> requiredCommands, LiteYukonUser user) {
//        log.debug("Reading " + pointSet + " on device " + device + " for " + user);
        // reduce number of commands

        List<CommandRequestDevice> allCommands = Lists.newArrayList();
        for (YukonDevice device : requiredCommands.keySet()) {
            // get command requests to send
            List<CommandRequestDevice> commands = getCommandRequests(device, requiredCommands.get(device));
            allCommands.addAll(commands);
        }

        // wait for results
        CommandResultHolder holder;
        try {
            holder = commandExecutor.execute(allCommands, user);
        } catch (Exception e) {
            throw new MeterReadRequestException(e);
        }
        
        return holder;
    }
    
    private List<CommandRequestDevice> getCommandRequests(YukonDevice device, Iterable<CommandWrapper> commands) {
        List<CommandRequestDevice> commandRequests = new ArrayList<CommandRequestDevice>();
        for (CommandWrapper wrapper : commands) {
            List<String> commandStringList = wrapper.commandDefinition.getCommandStringList();
            for (String commandStr : commandStringList) {
                CommandRequestDevice request = new CommandRequestDevice();
                request.setDevice(device);
                commandStr += (isUpdate ? " update " : "");
                commandStr += (isNoqueue ? " noqueue " : "");

                request.setCommand(commandStr);
                commandRequests.add(request);
            }
        }
        return commandRequests;
    }
    
    private Set<CommandWrapper> getMinimalCommandSet(YukonDevice device, Set<PointIdentifier> pointSet) {
        Set<CommandDefinition> allPossibleCommands = deviceDefinitionDao.getCommandsThatAffectPoints(device.getDeviceType(), pointSet);
        
        Set<CommandWrapper> wrappedCommands = new HashSet<CommandWrapper>(allPossibleCommands.size());
        for (CommandDefinition definition : allPossibleCommands) {
            wrappedCommands.add(new CommandWrapper(definition));
        }
        
        // reduce number of commands
        Set<CommandWrapper> minimalCommands = SetCoveringSolver.getMinimalSet(wrappedCommands, pointSet);
        return minimalCommands;
    }
    
    private class CommandWrapper implements SetCoveringSolver.HasWeight<PointIdentifier> {

        private final CommandDefinition commandDefinition;

        public CommandWrapper(CommandDefinition commandDefinition) {
            this.commandDefinition = commandDefinition;
        }
        public Set<PointIdentifier> getAffected() {
            return commandDefinition.getAffectedPointList();
        }

        public float getWeight() {
            // according to Matt, this is a good estimate
            // but, there will be lots of ties
            return commandDefinition.getCommandStringList().size();
        }
        
        public CommandDefinition getCommandDefinition() {
            return commandDefinition;
        }
        
        @Override
        public String toString() {
            return commandDefinition.toString();
        }
        
    }
    
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setCommandExecutor(CommandRequestDeviceExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
    
}
