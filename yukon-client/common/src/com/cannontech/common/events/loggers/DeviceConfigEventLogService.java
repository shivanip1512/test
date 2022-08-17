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
    public void sendConfigInitiated(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.totalCount)Integer numDevices,
                                    @Arg(ArgEnum.username)LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigToDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigCompleted(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.status)String creStatus,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigCancelled(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void sendConfigToDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                            Integer successOrFail);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigInitiated(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.totalCount)Integer numDevices,
                                    @Arg(ArgEnum.username)LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void readConfigCompleted(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.status)String creStatus,
                                    @Arg(ArgEnum.resultKey)String resultKey);

    @YukonEventLog(category = "device.configuration")
    public void readConfigFromDeviceInitiated(@Arg(ArgEnum.deviceName) String deviceName,
                                              @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(category = "device.configuration")
    public void readConfigFromDeviceCompleted(@Arg(ArgEnum.deviceName) String deviceName,
                                              Integer successOrFail);

    @YukonEventLog(category = "device.configuration")
    public void readConfigCancelled(@Arg(ArgEnum.action)String action,
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
    public void assignConfigInitiated(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.totalCount)Integer numDevices,
                                      @Arg(ArgEnum.username)LiteYukonUser username,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void assignConfigCompleted(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.statistics)String statistics,
                                      @Arg(ArgEnum.status)String creStatus,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void unassignConfigInitiated(@Arg(ArgEnum.action)String action,
                                        @Arg(ArgEnum.input)String input,
                                        @Arg(ArgEnum.totalCount)Integer numDevices,
                                        @Arg(ArgEnum.username)LiteYukonUser username,
                                        @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "device.configuration")
    public void unassignConfigCompleted(@Arg(ArgEnum.action)String action,
                                        @Arg(ArgEnum.input)String input,
                                        @Arg(ArgEnum.statistics)String statistics,
                                        @Arg(ArgEnum.status)String creStatus,
                                        @Arg(ArgEnum.resultKey)String resultKey);
    
}