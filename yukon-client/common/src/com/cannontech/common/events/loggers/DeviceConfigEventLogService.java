package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceConfigEventLogService {
    
    @YukonEventLog(category = "device.configuration")
    public void assignConfigToDeviceCompleted(@Arg(ArgEnum.deviceConfig) String deviceConfig,
                                              @Arg(ArgEnum.deviceName) String deviceName, 
                                              @Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                              Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void unassignConfigFromDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                                  @Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                  Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigInitiated(String action,
                                    String detail,
                                    @Arg(ArgEnum.totalCount) Integer numDevices,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigToDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigCancelled(String action,
                                    String detail,
                                    @Arg(ArgEnum.username) LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigToDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                            Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigInitiated(String action,
                                    String detail,
                                    @Arg(ArgEnum.totalCount) Integer numDevices,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);

    @YukonEventLog(category = "device.configuration")
    public void readConfigFromDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                              @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(category = "device.configuration")
    public void readConfigFromDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                              Integer successOrFail);

    @YukonEventLog(category = "device.configuration")
    public void readConfigCancelled(String action,
                                    String detail,
                                    @Arg(ArgEnum.username) LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigInitiated(String action,
                                    String detail,
                                    @Arg(ArgEnum.totalCount) Integer numDevices,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigCancelled(String action,
                                    String detail,
                                    @Arg(ArgEnum.username) LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigFromDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                                @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigFromDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                                Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void assignConfigInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category = "device.configuration")
    public void assignConfigCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void unassignConfigInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category = "device.configuration")
    public void unassignConfigCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
}