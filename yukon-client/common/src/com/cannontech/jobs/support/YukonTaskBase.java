package com.cannontech.jobs.support;

import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class YukonTaskBase implements YukonTask {
    private LiteYukonUser user;
    
    public void setRunAsUser(LiteYukonUser user) {
        this.user = user;
    }
    
    public LiteYukonUser getRunAsUser() {
        return user;
    }


}
