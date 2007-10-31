package com.cannontech.jobs.support;

import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class YukonTaskBase implements YukonTask {
    private LiteYukonUser user;
    
    public LiteYukonUser setRunAsUser(LiteYukonUser user) {
        this.user = user;
        return null;
    }
    
    public LiteYukonUser getRunAsUser() {
        return user;
    }


}
