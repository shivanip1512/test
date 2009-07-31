package com.cannontech.amr.deviceread.dao.impl;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.deviceread.model.CommandWrapper;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class MeterReadServiceImpl implements MeterReadService {
    private Logger log = YukonLogManager.getLogger(MeterReadServiceImpl.class);
    private CommandRequestDeviceExecutor commandExecutor;
    private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    
    public boolean isReadable(YukonDevice device, Set<? extends Attribute> attributes, LiteYukonUser user) {
    	log.debug("Validating Readability for" + attributes + " on device " + device + " for " + user);
    	
        Multimap<PaoIdentifier, LitePoint> pointsToRead = meterReadCommandGeneratorService.getPointsToRead(device.getPaoIdentifier(), attributes);
    	
    	try {
            meterReadCommandGeneratorService.getRequiredCommands(pointsToRead);
        } catch (UnreadableException e) {
            return false;
        }

    	return true;
    }

    public CommandResultHolder readMeter(YukonDevice device, Set<? extends Attribute> attributes, LiteYukonUser user) {
        log.info("Reading " + attributes + " on device " + device + " for " + user);
        
        // figure out which commands to send
        Multimap<PaoIdentifier, LitePoint> pointsToRead = meterReadCommandGeneratorService.getPointsToRead(device.getPaoIdentifier(), attributes);
        
        Multimap<PaoIdentifier, CommandWrapper> requiredCommands;
        try {
            requiredCommands = meterReadCommandGeneratorService.getRequiredCommands(pointsToRead);
        } catch (UnreadableException e) {
            throw new RuntimeException("It isn't possible to read " + attributes + " for  " + device, e);
        }
        return readMeterPoints(requiredCommands, user);
    }
    
    private CommandResultHolder readMeterPoints(Multimap<PaoIdentifier, CommandWrapper> requiredCommands, LiteYukonUser user) {
        List<CommandRequestDevice> allCommands = Lists.newArrayList();
        for (PaoIdentifier device : requiredCommands.keySet()) {
            // get command requests to send
            List<CommandRequestDevice> commands = meterReadCommandGeneratorService.getCommandRequests(device, requiredCommands.get(device));
            allCommands.addAll(commands);
        }

        // wait for results
        CommandResultHolder holder;
        try {
            holder = commandExecutor.execute(allCommands, CommandRequestExecutionType.DEVICE_ATTRIBUTE_READ, user);
        } catch (Exception e) {
            throw new MeterReadRequestException(e);
        }
        
        return holder;
    }
    
    @Autowired
    public void setCommandExecutor(CommandRequestDeviceExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
    
    @Autowired
    public void setMeterReadCommandGeneratorService(MeterReadCommandGeneratorService meterReadCommandGeneratorService) {
		this.meterReadCommandGeneratorService = meterReadCommandGeneratorService;
	}
    
}
