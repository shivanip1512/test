package com.cannontech.common.device.config.service;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceConfigService {

    public String sendConfigs(DeviceCollection deviceCollection, String method, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);
    
    public String readConfigs(DeviceCollection deviceCollection, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user);
    
    public VerifyConfigCommandResult verifyConfigs(Iterable<? extends YukonDevice> deviceCollection, LiteYukonUser user);

    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user);
    
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception;

    public CommandResultHolder sendConfig(YukonDevice device, LiteYukonUser user) throws Exception;
    
    /** Returns true when there is any available configuration supported by paoType */
    public boolean isDeviceConfigAvailable(PaoType paoType);
}
