package com.cannontech.amr.disconnect.service;

import java.util.List;
import java.util.Set;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.StrategyService;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DisconnectStrategyService extends StrategyService {

    /**
     * Filter devices that will accept commands
     */
    FilteredDevices filter(List<SimpleDevice> meters);

    /**
     * Send connect, disconnect or arm command to the given devices.
     */
    public void execute(DisconnectCommand command, Set<SimpleDevice> meters, DisconnectCallback callback,
            CommandRequestExecution execution, LiteYukonUser user);

    /**
     * Returns true if the devices can be armed.
     */
    default boolean supportsArm(List<SimpleDevice> meters) {
        return false;
    }

    /**
     * Attempt to cancel the command
     */
    default void cancel(CollectionActionResult result, LiteYukonUser user) {
        return;
    }
}
