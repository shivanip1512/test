package com.cannontech.amr.disconnect.service;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.user.YukonUserContext;

public interface DisconnectRfnService {
    
    /**
     * Send connect, disconnect or arm command to the given devices.
     */
    void execute(DisconnectCommand command, Iterable<SimpleDevice> meters, DisconnectCallback callback,
                 CommandRequestExecution execution,
                 YukonUserContext userContext);
    /**
     * Send cancel command to the given devices.
     */
    void cancel(DisconnectResult result, YukonUserContext userContext);
}
