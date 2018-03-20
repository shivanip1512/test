package com.cannontech.common.device.config.service;

import java.util.List;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface DeviceConfigService {

    public int sendConfigs(DeviceCollection deviceCollection, String method, SimpleCallback<CollectionActionResult> callback, YukonUserContext context);
    
    public int readConfigs(DeviceCollection deviceCollection, SimpleCallback<CollectionActionResult> callback, YukonUserContext context);
    
    public VerifyConfigCommandResult verifyConfigs(Iterable<? extends YukonDevice> deviceCollection, LiteYukonUser user);

    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user);
    
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception;

    public CommandResultHolder sendConfig(YukonDevice device, LiteYukonUser user) throws Exception;

    public enum LogAction {
        READ, SEND, VERIFY
    }
    
    /**
     * Logs completion result (success or failure) for each device.
     * If isSuccessful is true, logs success otherwise logs failure.
     */
    void logCompleted(List<SimpleDevice> devices, LogAction action, boolean isSuccessful);

}
