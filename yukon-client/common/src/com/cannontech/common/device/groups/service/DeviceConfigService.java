package com.cannontech.common.device.groups.service;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceConfigService {

    public String pushConfigs(DeviceCollection deviceCollection, String method, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);
    
    public VerifyConfigCommandResult verifyConfigs(Iterable<? extends YukonDevice> deviceCollection, LiteYukonUser user);

    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user);
    
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception;

    public CommandResultHolder pushConfig(YukonDevice device, LiteYukonUser user) throws Exception;
}
