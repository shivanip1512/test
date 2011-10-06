package com.cannontech.common.device.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.device.service.PointReadService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

public class PointReadServiceImpl implements PointReadService {
    
    private CommandRequestDeviceExecutor executor;
    private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    public void backgroundReadPoint(PaoPointIdentifier paoPointIdentifier,
            DeviceRequestType type, LiteYukonUser user) {
        
        Set<PointIdentifier> pointSet = ImmutableSet.of(paoPointIdentifier.getPointIdentifier());
        Set<CommandDefinition> commandsThatAffectPoints = paoDefinitionDao.getCommandsThatAffectPoints(paoPointIdentifier.getPaoIdentifier().getPaoType(), pointSet);
        
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

            request.setCommandCallback(new CommandCallbackBase(commandStr));
            commandRequests.add(request);
        }
        CommandCompletionCallbackAdapter<Object> dummyCallback = new CommandCompletionCallbackAdapter<Object>();
        executor.execute(commandRequests, dummyCallback, type, user);
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setCommandRequestDeviceExecutor(CommandRequestDeviceExecutor executor) {
        this.executor = executor;
    }

}
