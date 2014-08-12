package com.cannontech.amr.disconnect.service;

import java.util.Set;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface DisconnectPlcService {

    /**
     * Send connect, disconnect or arm command to the given devices. Returns command CommandCompletionCallback needed for cancellations.
     */
    public CommandCompletionCallback<CommandRequestDevice> execute(final DisconnectCommand command, Set<SimpleDevice> meters,
                                                                   DisconnectCallback callback, CommandRequestExecution execution, LiteYukonUser user);
    /**
     * Send cancel command to the given devices.
     */
    void cancel(DisconnectResult result, YukonUserContext userContext);
}
