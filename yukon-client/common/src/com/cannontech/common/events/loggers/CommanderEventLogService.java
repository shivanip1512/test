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
    public void groupCommandInitiated(String action,
                                      String detail,
                                    @Arg(ArgEnum.totalCount)Integer numDevices,
                                    @Arg(ArgEnum.username)LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category="commander")
    public void groupCommandCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);

    @YukonEventLog(category="commander")
    public void groupCommandCompleted(String action,
            String detail,
            String creStatus,
            @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category="commander")
    public void attributeReadInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category="commander")
    public void attributeReadCompleted(String action,
            String detail,
            String creStatus,
            @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category="commander")
    public void attributeReadCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category="commander")
    public void locateRouteInitiated(String action,
                                       String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(category="commander")
    public void locateRouteCompleted(String action,
            String detail,
            String creStatus,
            @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(category="commander")
    public void locateRouteCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);
    
}