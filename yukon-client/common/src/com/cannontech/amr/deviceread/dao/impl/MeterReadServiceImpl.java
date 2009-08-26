package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

public class MeterReadServiceImpl implements MeterReadService {
    private Logger log = YukonLogManager.getLogger(MeterReadServiceImpl.class);
    private CommandRequestDeviceExecutor commandExecutor;
    private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    
    public boolean isReadable(YukonDevice device, Set<? extends Attribute> attributes, LiteYukonUser user) {
    	log.debug("Validating Readability for" + attributes + " on device " + device + " for " + user);
    	
    	return meterReadCommandGeneratorService.isReadable(device, attributes);
    }

    public CommandResultHolder readMeter(YukonDevice device, Set<? extends Attribute> attributes, LiteYukonUser user) {
        log.info("Reading " + attributes + " on device " + device + " for " + user);
        
        CommandRequestExecutionType type = CommandRequestExecutionType.GROUP_ATTRIBUTE_READ;
        
        if (!meterReadCommandGeneratorService.isReadable(device, attributes)) {
            throw new RuntimeException("It isn't possible to read " + attributes + " for  " + device);
        }
        
        Multimap<YukonDevice, CommandRequestDevice> commandRequests = meterReadCommandGeneratorService.getCommandRequests(Collections.singletonList(device), attributes, type);
        
        return execute(new ArrayList<CommandRequestDevice>(commandRequests.values()), type, user);
    }
    
    private CommandResultHolder execute(List<CommandRequestDevice> commands, CommandRequestExecutionType type, LiteYukonUser user) {
        
        CommandResultHolder holder;
        try {
            holder = commandExecutor.execute(commands, type, user);
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
