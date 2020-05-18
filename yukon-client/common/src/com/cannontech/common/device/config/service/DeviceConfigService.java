package com.cannontech.common.device.config.service;

import java.util.List;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.user.YukonUserContext;

public interface DeviceConfigService {

    int sendConfigs(DeviceCollection deviceCollection, String method, SimpleCallback<CollectionActionResult> callback,
            YukonUserContext context);

    int readConfigs(DeviceCollection deviceCollection, SimpleCallback<CollectionActionResult> callback, YukonUserContext context);

    VerifyConfigCommandResult verifyConfigs(List<SimpleDevice> devices, LiteYukonUser user);

    VerifyResult verifyConfig(SimpleDevice device, LiteYukonUser user);

    CommandResultHolder readConfig(SimpleDevice device, LiteYukonUser user) throws Exception;

    CommandResultHolder sendConfig(SimpleDevice device, LiteYukonUser user) throws Exception;

    enum LogAction {
        READ, SEND, VERIFY
    }

    int verifyConfigs(DeviceCollection deviceCollection, YukonUserContext context);

    /**
     * Updates device config state for success results for assign and unassign collection action
     */
    void updateConfigStateForAssignAndUnassign(CollectionActionResult result);

    DeviceConfigState assignConfigToDevice(SimpleDevice device, DeviceConfiguration configuration, LiteYukonUser user)
            throws InvalidDeviceTypeException;

    void unassignConfig(SimpleDevice device, LiteYukonUser user) throws InvalidDeviceTypeException;

    /**
     * Updates status to "In Progress", used by Commander
     */
    void processCommandRequest(List<Request> commandRequests);

    /**
     * Updates status based on return value from Porter, used by Commander
     */
    void processCommandReturn(Return response);
}
