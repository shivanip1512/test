package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;

public interface InfrastructureEventLogService {

    @YukonEventLog(category = "infrastructure.warning")
    public void warningGenerated(@Arg(ArgEnum.paoName) String paoName,
                                 String warningType,
                                 String severity,
                                 @Arg(ArgEnum.message) String message);

    @YukonEventLog(category = "infrastructure.warning")
    public void warningCleared(@Arg(ArgEnum.paoName) String paoName,
                               String warningType);
}
