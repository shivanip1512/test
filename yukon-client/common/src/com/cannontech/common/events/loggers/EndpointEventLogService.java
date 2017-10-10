package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EndpointEventLogService {

    @YukonEventLog(category = "endpoint.location")
    public void locationUpdated(@Arg(ArgEnum.deviceName) String deviceName, String latitude, String longitude,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(category = "endpoint.location")
    public void locationRemoved(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
}
