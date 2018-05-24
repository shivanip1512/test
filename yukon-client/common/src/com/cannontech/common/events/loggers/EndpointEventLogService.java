package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EndpointEventLogService {

    @YukonEventLog(category = "endpoint.location")
    public void locationUpdated(@Arg(ArgEnum.paoName) String paoName, PaoLocation location,
            LiteYukonUser user);

    @YukonEventLog(category = "endpoint.location")
    public void locationRemoved(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeInitiated(String action,
                                String detail,
                                Integer devices,
                                @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeCompleted(String action,
                                String detail,
                                String creStatus,
                                String key);

    @YukonEventLog(category = "endpoint.device")
    public void changeInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeTypeInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeTypeCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeTypeCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void deleteInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category = "endpoint.device")
    public void deleteCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void deleteCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
}

