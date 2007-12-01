package com.cannontech.jobs.support;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonTask {
    public void start();
    public void stop() throws UnsupportedOperationException;
    public void setRunAsUser(LiteYukonUser user);
}
