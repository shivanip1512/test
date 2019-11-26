package com.cannontech.amr.deviceread.dao;

import java.util.Set;

import com.cannontech.amr.deviceread.dao.impl.DeviceCommandServiceImpl.CompletionCallback;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceCommandService {

    /**
     * Attempts to send a command to a group of devices. This method sends commands to porter only.
     * If command is not provided looks up commands by attributes.
     * RF devices and devices that do not support COMMANDER_REQUESTS tag will be marked as unsupported.
     */
    CompletionCallback execute(Set<SimpleDevice> devices, Set<? extends Attribute> attributes, String command,
            DeviceRequestType type, LiteYukonUser user, RetryParameters retryParameters,
            CommandCompletionCallback<CommandRequestDevice> callback, String scheduleName, Integer jobId);
}
