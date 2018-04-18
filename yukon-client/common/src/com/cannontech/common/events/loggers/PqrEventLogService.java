package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PqrEventLogService {
    @YukonEventLog(category = "dr.powerQualityResponse")
    public void sendConfig(@Arg(ArgEnum.username) LiteYukonUser user,
                           Integer numberOfDevices,
                           String configuration);
}
