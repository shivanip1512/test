package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommanderEventLogService {
    
    @YukonEventLog(category="commander")
    public void executeOnPao(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.commandRequestString) String command,
            @Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.paoId) int paoId);
    
    @YukonEventLog(category="commander")
    public void executeOnSerial(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.commandRequestString) String command,
            @Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.routeName) String routeName,
            @Arg(ArgEnum.routeId) int routeId);
    
    @YukonEventLog(category="commander")
    public void changeRoute(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.routeName) String oldRouteName,
            @Arg(ArgEnum.routeName) String newRouteName,
            @Arg(ArgEnum.paoId) int paoId,
            @Arg(ArgEnum.routeId) int oldRouteId,
            @Arg(ArgEnum.routeId) int newRouteId);
    
    @YukonEventLog(category="commander")
    public void groupCommandInitiated(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.totalCount)Integer numDevices,
                                      @Arg(ArgEnum.username)LiteYukonUser username,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category="commander")
    public void groupCommandCancelled(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.statistics)String statistics,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);

    @YukonEventLog(category="commander")
    public void groupCommandCompleted(@Arg(ArgEnum.action)String action,
                                      @Arg(ArgEnum.input)String input,
                                      @Arg(ArgEnum.statistics)String statistics,
                                      @Arg(ArgEnum.status)String creStatus,
                                      @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category="commander")
    public void attributeReadInitiated(@Arg(ArgEnum.action)String action,
                                       @Arg(ArgEnum.input)String input,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category="commander")
    public void attributeReadCompleted(@Arg(ArgEnum.action)String action,
                                       @Arg(ArgEnum.input)String input,
                                       @Arg(ArgEnum.statistics)String statistics,
                                       @Arg(ArgEnum.status)String creStatus,
                                       @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category="commander")
    public void attributeReadCancelled(@Arg(ArgEnum.action)String action,
                                       @Arg(ArgEnum.input)String input,
                                       @Arg(ArgEnum.statistics)String statistics,
                                       @Arg(ArgEnum.username) LiteYukonUser user,
                                       @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category="commander")
    public void locateRouteInitiated(@Arg(ArgEnum.action)String action,
                                     @Arg(ArgEnum.input)String input,
                                     @Arg(ArgEnum.totalCount)Integer numDevices,
                                     @Arg(ArgEnum.username)LiteYukonUser username,
                                     @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category="commander")
    public void locateRouteCompleted(@Arg(ArgEnum.action)String action,
                                     @Arg(ArgEnum.input)String input,
                                     @Arg(ArgEnum.statistics)String statistics,
                                     @Arg(ArgEnum.status)String creStatus,
                                     @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(category="commander")
    public void locateRouteCancelled(@Arg(ArgEnum.action)String action,
                                     @Arg(ArgEnum.input)String input,
                                     @Arg(ArgEnum.statistics)String statistics,
                                     @Arg(ArgEnum.username) LiteYukonUser user,
                                     @Arg(ArgEnum.resultKey) String resultKey);
    
}