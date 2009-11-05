package com.cannontech.common.device.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.device.service.PointReadService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

public class PointReadServiceImpl implements PointReadService {
    
    private CommandRequestDeviceExecutor executor;
    private DeviceDefinitionDao deviceDefinitionDao;
    
    @Override
    public void backgroundReadPoint(PaoPointIdentifier paoPointIdentifier,
            CommandRequestExecutionType type, LiteYukonUser user) {
        
        Set<PointIdentifier> pointSet = ImmutableSet.of(paoPointIdentifier.getPointIdentifier());
        Set<CommandDefinition> commandsThatAffectPoints = deviceDefinitionDao.getCommandsThatAffectPoints(paoPointIdentifier.getPaoIdentifier().getPaoType(), pointSet);
        
        Ordering<CommandDefinition> ordering = Ordering.natural().onResultOf(new Function<CommandDefinition, Comparable<Integer>>() {
            public Comparable<Integer> apply(CommandDefinition arg0) {
                return arg0.getCommandStringList().size();
            };
        });
        
        CommandDefinition commandDefinition = ordering.min(commandsThatAffectPoints);
        List<String> commandStrings = commandDefinition.getCommandStringList();
        
        List<CommandRequestDevice> commandRequests = new ArrayList<CommandRequestDevice>();
        for (String commandStr : commandStrings) {
            CommandRequestDevice request = new CommandRequestDevice();
            request.setDevice(new SimpleDevice(paoPointIdentifier.getPaoIdentifier()));

            request.setCommand(commandStr);
            commandRequests.add(request);
        }
        CommandCompletionCallbackAdapter<Object> dummyCallback = new CommandCompletionCallbackAdapter<Object>();
        executor.execute(commandRequests, dummyCallback, type, user);
    }
    
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }
    
    @Autowired
    public void setCommandRequestDeviceExecutor(CommandRequestDeviceExecutor executor) {
        this.executor = executor;
    }

}
