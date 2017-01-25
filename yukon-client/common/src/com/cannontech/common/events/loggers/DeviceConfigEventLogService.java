package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceConfigEventLogService {
    
    @YukonEventLog(category = "device.configuration")
    public void assignConfigToDeviceInitiated(@Arg(ArgEnum.deviceConfig) String deviceConfig,
                                              @Arg(ArgEnum.deviceName) String deviceName,
                                              @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void assignConfigToDeviceSucceeded(@Arg(ArgEnum.deviceConfig) String deviceConfig,
                                              @Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(category = "device.configuration")
    public void assignConfigToDeviceFailed(@Arg(ArgEnum.deviceConfig) String deviceConfig,
                                              @Arg(ArgEnum.deviceName) String deviceName);
            
    @YukonEventLog(category = "device.configuration")
    public void unassignConfigFromDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                                  @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    
    @YukonEventLog(category = "device.configuration")
    public void unassignConfigFromDeviceSucceeded(@Arg(ArgEnum.deviceName) String deviceName,
                                                  @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(category = "device.configuration")
    public void unassignConfigFromDeviceFailed(@Arg(ArgEnum.deviceName) String deviceName,
                                               @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigInitiated(@Arg(ArgEnum.totalCount) Integer numDevices,
                                    @Arg(ArgEnum.commandRequestString) String method,
                                    @Arg(ArgEnum.resultKey) String resultKey,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigToDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigCompleted(@Arg(ArgEnum.successCount) Integer numSucces,
                                    @Arg(ArgEnum.failureCount) Integer numFailed,
                                    @Arg(ArgEnum.unsupportedCount) Integer numUnsupported,
                                    @Arg(ArgEnum.message) String exceptionMessage,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigToDeviceSucceeded(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigToDeviceFailed(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigInitiated(@Arg(ArgEnum.totalCount) Integer numDevices,
                                    @Arg(ArgEnum.resultKey) String resultKey,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigCompleted(@Arg(ArgEnum.successCount) Integer numSucces,
                                    @Arg(ArgEnum.failureCount) Integer numFailed,
                                    @Arg(ArgEnum.unsupportedCount) Integer numUnsupported,
                                    @Arg(ArgEnum.message) String exceptionMessage,
                                    @Arg(ArgEnum.resultKey) String resultKey);

    @YukonEventLog(category = "device.configuration")
    public void readConfigFromDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                              @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigFromDeviceSucceeded(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigFromDeviceFailed(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigInitiated(@Arg(ArgEnum.totalCount) Integer numDevices,
                                      @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigCompleted(@Arg(ArgEnum.successCount) Integer numSucces,
                                      @Arg(ArgEnum.failureCount) Integer numFailed,
                                      @Arg(ArgEnum.unsupportedCount) Integer numUnsupported,
                                      @Arg(ArgEnum.message) String exceptionMessage);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigFromDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                                @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigFromDeviceSucceeded(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigFromDeviceFailed(@Arg(ArgEnum.deviceName) String deviceName);
}
