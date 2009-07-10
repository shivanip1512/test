package com.cannontech.common.events.service;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface TestEventLogInterface {
    @YukonEventLog(category="amr")
    public void logUserWithLong(LiteYukonUser user, long longId);

    @YukonEventLog(category="amr")
    public void logStringIntStringLong(String arg1, int arg2, String arg3, long arg4);

    @YukonEventLog(category="amr")
    public void logIntegerDouble(Integer integer1, Double double2);

}
