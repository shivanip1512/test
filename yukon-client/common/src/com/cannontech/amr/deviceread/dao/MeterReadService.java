package com.cannontech.amr.deviceread.dao;

import java.util.Set;

import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface MeterReadService {
    
	/**
     * This method will determine which commands need to be sent to read
     * the requested attributes and then use the CommandRequestExecutor
     * to actually execute the commands and returns a result.
     * Waits for command result.
     */
    public CommandResultHolder readMeter(YukonDevice device, Set<? extends Attribute> attribute, CommandRequestExecutionType type, LiteYukonUser user);

    /**
     * This method will determine which commands need to be sent to read
     * the requested attributes and then use the CommandRequestExecutor
     * to actually execute the commands and returns a CommandRequestExecutionIdentifier.
     * Does not wait for command result, uses callback.
     */
    public CommandRequestExecutionIdentifier readMeter(YukonDevice device, Set<? extends Attribute> attribute, CommandRequestExecutionType type, CommandCompletionCallback<Object> callback, LiteYukonUser user);

    /**
     * This method will verify which commands need to be sent to read the attributes.  If there
     * are no commands supported/available to read the attributes, then return false.
     * This method also validates the pao authorization of the user.  If the user does not have
     * rights (based on Commander role properties) to all commands that are to be issued, then return false.
     * Note: If one of the commands does not have access rights, all fail.
     */
    public boolean isReadable(YukonDevice device, Set<? extends Attribute> attributes, LiteYukonUser user);
    
}
