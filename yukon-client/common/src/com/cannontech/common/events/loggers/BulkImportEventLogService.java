package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface BulkImportEventLogService {
    
    @YukonEventLog(category = "hardware.import")
    public void importIntitated(@Arg(ArgEnum.username) LiteYukonUser user, String importType);

    @YukonEventLog(category = "hardware.import")
    public void locationUpdated(@Arg(ArgEnum.paoId) PaoIdentifier paoIdentifier, String latitude, String longitude,
            String origin);
}
