package com.cannontech.common.events.service;

import com.cannontech.common.events.YukonEventLog;

public interface BadTestEventLogInterface {
    @YukonEventLog(category="amr")
    public void logStringWithTooManyArgs(String arg1, int arg2, String arg3, long arg4, String arg5);

}
