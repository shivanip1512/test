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
    
    public VerifyConfigCommandResult verifyConfigs(List<SimpleDevice> devices, LiteYukonUser user);

    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user);
    
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception;

    public CommandResultHolder sendConfig(YukonDevice device, LiteYukonUser user) throws Exception;

    public enum LogAction {
        READ, SEND, VERIFY
    }
    
    int verifyConfigs(DeviceCollection deviceCollection, YukonUserContext context);
}
