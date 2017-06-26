package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoIdentifier;

public interface InfrastructureEventLogService {

    @YukonEventLog(category = "infrastructure.warning")
    public void warningGenerated(@Arg(ArgEnum.paoId) PaoIdentifier paoIdentifier,
                                 String warningType,
                                 String severity,
                                 @Arg(ArgEnum.message) String message);
    
}
