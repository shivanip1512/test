package com.cannontech.common.device.groups.service;

import java.util.Map;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceConfigService {

    public String pushConfigs(DeviceCollection deviceCollection, boolean force, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);
    
    public Map<YukonDevice, VerifyResult> verifyConfigs(DeviceCollection deviceCollection, LiteYukonUser user);
}
