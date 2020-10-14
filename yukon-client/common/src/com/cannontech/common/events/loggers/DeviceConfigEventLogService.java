package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceConfigEventLogService {
    
    @YukonEventLog(category = "device.configuration")
    public void changeConfigOfDeviceCompleted(@Arg(ArgEnum.deviceConfig) String deviceConfig,
                                              @Arg(ArgEnum.deviceName) String deviceName, 
                                              @Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                              Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void removeConfigFromDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                                  @Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                  Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void uploadConfigInitiated(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.totalCount)Integer numDevices,
                                    @Arg(ArgEnum.username)LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void uploadConfigToDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void uploadConfigCompleted(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.status)String creStatus,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void uploadConfigCancelled(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void uploadConfigToDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                            Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void validateConfigInitiated(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.totalCount)Integer numDevices,
                                    @Arg(ArgEnum.username)LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void validateConfigCompleted(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.status)String creStatus,
                                    @Arg(ArgEnum.resultKey)String resultKey);

    @YukonEventLog(category = "device.configuration")
    public void validateConfigOnDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                              @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(category = "device.configuration")
    public void validateConfigOnDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                              Integer successOrFail);

    @YukonEventLog(category = "device.configuration")
    public void validateConfigCancelled(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigInitiated(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.totalCount)Integer numDevices,
                                      @Arg(ArgEnum.username)LiteYukonUser username,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigCompleted(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.statistics)String statistics,
                                      @Arg(ArgEnum.status)String creStatus,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigCancelled(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.statistics)String statistics,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigFromDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                                @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void verifyConfigFromDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                                Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void changeConfigInitiated(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.totalCount)Integer numDevices,
                                      @Arg(ArgEnum.username)LiteYukonUser username,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void changeConfigCompleted(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.statistics)String statistics,
                                      @Arg(ArgEnum.status)String creStatus,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void removeConfigInitiated(@Arg(ArgEnum.action)String action,
                                        @Arg(ArgEnum.input)String input,
                                        @Arg(ArgEnum.totalCount)Integer numDevices,
                                        @Arg(ArgEnum.username)LiteYukonUser username,
                                        @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void removeConfigCompleted(@Arg(ArgEnum.action)String action,
                                        @Arg(ArgEnum.input)String input,
                                        @Arg(ArgEnum.statistics)String statistics,
                                        @Arg(ArgEnum.status)String creStatus,
                                        @Arg(ArgEnum.resultKey)String resultKey);
    
}