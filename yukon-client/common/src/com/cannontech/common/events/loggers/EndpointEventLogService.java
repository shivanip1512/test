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
    public void changeCompleted(@Arg(ArgEnum.action)String action,
                                @Arg(ArgEnum.input)String input,
                                @Arg(ArgEnum.statistics)String statistics,
                                @Arg(ArgEnum.status)String creStatus,
                                @Arg(ArgEnum.resultKey)String resultKey);

    @YukonEventLog(category = "endpoint.device")
    public void changeInitiated(@Arg(ArgEnum.action)String action,
                                @Arg(ArgEnum.input)String input,
                                @Arg(ArgEnum.totalCount)Integer numDevices,
                                @Arg(ArgEnum.username)LiteYukonUser username,
                                @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeCancelled(@Arg(ArgEnum.action)String action,
                                @Arg(ArgEnum.input)String input,
                                @Arg(ArgEnum.statistics)String statistics,
                                @Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeTypeInitiated(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.totalCount)Integer numDevices,
                                    @Arg(ArgEnum.username)LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeTypeCompleted(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.status)String creStatus,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void changeTypeCancelled(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void deleteInitiated(@Arg(ArgEnum.action)String action,
                                @Arg(ArgEnum.input)String input,
                                @Arg(ArgEnum.totalCount)Integer numDevices,
                                @Arg(ArgEnum.username)LiteYukonUser username,
                                @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void deleteCompleted(@Arg(ArgEnum.action)String action,
                                @Arg(ArgEnum.input)String input,
                                @Arg(ArgEnum.statistics)String statistics,
                                @Arg(ArgEnum.status)String creStatus,
                                @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category = "endpoint.device")
    public void deleteCancelled(@Arg(ArgEnum.action)String action,
                                @Arg(ArgEnum.input)String input,
                                @Arg(ArgEnum.statistics)String statistics,
                                @Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.resultKey) String resultKey);
    
}

