package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequest;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public class MeterReadServiceImpl implements MeterReadService {
    private Logger log = YukonLogManager.getLogger(MeterReadServiceImpl.class);
    private DeviceDefinitionDao deviceDefinitionDao;
    private CommandRequestExecutor commandExecutor;
    private PaoCommandAuthorizationService commandAuthorizationService;
    private PaoDao paoDao;
    
    private boolean isUpdate = true;
    private boolean isNoqueue = true;
    
    public boolean isReadable(Meter device, Set<? extends Attribute> attributes, LiteYukonUser user) {
    	log.debug("Validating Readability for" + attributes + " on device " + device + " for " + user);
    	
        // reduce number of commands
    	Set<DevicePointIdentifier> pointSet = deviceDefinitionDao.getDevicePointIdentifiersForAttributes(device, attributes);
        Set<CommandWrapper> minimalCommands = getMinimalCommandSet(device, pointSet);
        if (minimalCommands == null) {
        	log.info("Not Readable: No commands defined to read " + pointSet + " for device type " + device.getType());
            return false;
        }

        try {
        	LiteYukonPAObject litePaobject = paoDao.getLiteYukonPAO(device.getDeviceId());
        	
        	for (CommandWrapper wrapper : minimalCommands) {
                List<String> commandStringList = wrapper.commandDefinition.getCommandStringList();
                for (String commandStr : commandStringList) {
    	        	commandAuthorizationService.verifyAuthorized(user, commandStr, litePaobject);
                }
            }
        } catch (PaoAuthorizationException e) {
        	log.info("Not Readable: " + e.getMessage());
            return false;
        }
        
    	return true;
    }
    
    public CommandResultHolder readMeter(Meter device, Set<? extends Attribute> attributes, LiteYukonUser user) {
        log.info("Reading " + attributes + " on device " + device + " for " + user);
        
        // figure out which commands to send
        Set<DevicePointIdentifier> pointSet = deviceDefinitionDao.getDevicePointIdentifiersForAttributes(device, attributes);
        return readMeterPoints(device, pointSet, user);
    }
    
    private CommandResultHolder readMeterPoints(Meter device, Set<DevicePointIdentifier> pointSet, LiteYukonUser user) {
        log.debug("Reading " + pointSet + " on device " + device + " for " + user);
        // reduce number of commands
        Set<CommandWrapper> minimalCommands = getMinimalCommandSet(device, pointSet);
        
        if (minimalCommands == null) {
            throw new RuntimeException("It isn't possible to read " + pointSet + " for device type " + device.getType());
        }
        log.debug("Using " + minimalCommands + " for read");
        
        
        // get command requests to send
        List<CommandRequest> commands = getCommandRequests(device, minimalCommands);

        // wait for results
        CommandResultHolder holder;
        try {
            holder = commandExecutor.execute(commands, user);
        } catch (Exception e) {
            throw new MeterReadRequestException(e);
        }
        
        return holder;
    }
    
    private List<CommandRequest> getCommandRequests(Meter device, Set<CommandWrapper> commands) {
        List<CommandRequest> commandRequests = new ArrayList<CommandRequest>(commands.size());
        for (CommandWrapper wrapper : commands) {
            List<String> commandStringList = wrapper.commandDefinition.getCommandStringList();
            for (String commandStr : commandStringList) {
                CommandRequest request = new CommandRequest();
                request.setDeviceId(device.getDeviceId());
                commandStr += (isUpdate ? " update " : "");
                commandStr += (isNoqueue ? " noqueue " : "");

                request.setCommand(commandStr);
                commandRequests.add(request);
            }
        }
        return commandRequests;
    }
    
    private Set<CommandWrapper> getMinimalCommandSet(Meter device, Set<DevicePointIdentifier> pointSet) {
        Set<CommandDefinition> allPossibleCommands = deviceDefinitionDao.getAffected(device, pointSet);
        
        Set<CommandWrapper> wrappedCommands = new HashSet<CommandWrapper>(allPossibleCommands.size());
        for (CommandDefinition definition : allPossibleCommands) {
            wrappedCommands.add(new CommandWrapper(definition));
        }
        
        // reduce number of commands
        Set<CommandWrapper> minimalCommands = SetCoveringSolver.getMinimalSet(wrappedCommands, pointSet);
        return minimalCommands;
    }
    
    private class CommandWrapper implements SetCoveringSolver.HasWeight<DevicePointIdentifier> {

        private final CommandDefinition commandDefinition;

        public CommandWrapper(CommandDefinition commandDefinition) {
            this.commandDefinition = commandDefinition;
        }
        public Set<DevicePointIdentifier> getAffected() {
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
    
    @Required
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }
    
    @Required
    public void setCommandExecutor(CommandRequestExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Required
    public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}
