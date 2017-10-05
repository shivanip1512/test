package com.cannontech.common.user;

import com.cannontech.core.dao.impl.LoginStatusEnum;

public class NewUser extends User {
    
    private boolean enabled;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setLoginStatus(enabled ? LoginStatusEnum.ENABLED : LoginStatusEnum.DISABLED);
    }
}