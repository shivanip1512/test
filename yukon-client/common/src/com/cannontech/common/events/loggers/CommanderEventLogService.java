package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;

public interface CommanderEventLogService {
    
    @YukonEventLog(category="commander")
    public void executeOnPao(@Arg(ArgEnum.username) String user,
            @Arg(ArgEnum.commandRequestString) String command,
            @Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.paoId) int paoId);
    
    @YukonEventLog(category="commander")
    public void executeOnSerial(@Arg(ArgEnum.username) String user,
            @Arg(ArgEnum.commandRequestString) String command,
            @Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.routeName) String routeName,
            @Arg(ArgEnum.routeId) int routeId);
    
    @YukonEventLog(category="commander")
    public void changeRoute(@Arg(ArgEnum.username) String user,
            @Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.routeName) String oldRouteName,
            @Arg(ArgEnum.routeName) String newRouteName,
            @Arg(ArgEnum.paoId) int paoId,
            @Arg(ArgEnum.routeId) int oldRouteId,
            @Arg(ArgEnum.routeId) int newRouteId);
    
}