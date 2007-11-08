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
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;

public class MeterReadServiceImpl implements MeterReadService {
    private Logger log = YukonLogManager.getLogger(MeterReadServiceImpl.class);
    private DeviceDefinitionDao deviceDefinitionDao;
    private CommandRequestExecutor commandExecutor;

    public CommandResultHolder readMeter(Meter device,
            Set<Attribute> attributes,
            LiteYukonUser user) {
        log.info("Reading " + attributes + " on device " + device + " for " + user);
        
        // figure out which commands to send
        Set<DevicePointIdentifier> pointSet = new HashSet<DevicePointIdentifier>(attributes.size());
        for (Attribute attribute : attributes) {
            PointTemplate pointTemplateForAttribute = deviceDefinitionDao.getPointTemplateForAttribute(device, attribute);
            pointSet.add(pointTemplateForAttribute.getDevicePointIdentifier());
        }
        
        return readMeterPoints(device, pointSet, user);
    }
    
    private CommandResultHolder readMeterPoints(Meter device, Set<DevicePointIdentifier> pointSet, LiteYukonUser user) {
        log.debug("Reading " + pointSet + " on device " + device + " for " + user);
        Set<CommandDefinition> allPossibleCommands = deviceDefinitionDao.getAffected(device, pointSet);
        
        Set<CommandWrapper> wrappedCommands = new HashSet<CommandWrapper>(allPossibleCommands.size());
        for (CommandDefinition definition : allPossibleCommands) {
            wrappedCommands.add(new CommandWrapper(definition));
        }
        
        // reduce number of commands
        Set<CommandWrapper> minimalCommands = SetCoveringSolver.getMinimalSet(wrappedCommands, pointSet);
        
        if (minimalCommands == null) {
            throw new RuntimeException("It isn't possible to read " + pointSet + " for device type " + device.getType());
        }
        log.debug("Using " + minimalCommands + " for read");
        
        
        // send commands
        List<CommandRequest> commands = new ArrayList<CommandRequest>(minimalCommands.size());
        for (CommandWrapper wrapper : minimalCommands) {
            List<String> commandStringList = wrapper.commandDefinition.getCommandStringList();
            for (String commandStr : commandStringList) {
                CommandRequest request = new CommandRequest();
                request.setDeviceId(device.getDeviceId());
                commandStr += " update";
                commandStr += " noqueue";
                request.setCommand(commandStr);
                commands.add(request);
            }
        }
        
        // wait for results
        CommandResultHolder holder;
        try {
            holder = commandExecutor.execute(commands, user);
        } catch (Exception e) {
            throw new RuntimeException("There was a Yukon error while reading the meter", e);
        }
        
        return holder;
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

}
