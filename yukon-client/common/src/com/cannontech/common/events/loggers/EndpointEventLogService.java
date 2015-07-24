package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EndpointEventLogService {

    @YukonEventLog(category = "endpoint.location")
    public void locationUpdated(@Arg(ArgEnum.paoId) PaoIdentifier paoIdentifier, PaoLocation location);

    @YukonEventLog(category = "endpoint.location")
    public void locationUpdatedByUser(@Arg(ArgEnum.paoId) PaoIdentifier paoIdentifier, PaoLocation location,
            LiteYukonUser user);

    @YukonEventLog(category = "endpoint.location")
    public void locationDeleted(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
}
