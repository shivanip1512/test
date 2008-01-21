package com.cannontech.web.menu;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;

public abstract class BaseMenuOption {
    private UserChecker propertyChecker = new NullUserChecker();
    private String id = null;
    private String linkKey = null;
    
    public BaseMenuOption() {
        super();
    }

    public void setPropertyChecker(UserChecker propertyChecker) {
        this.propertyChecker = propertyChecker;
    }

    public boolean isValidForUser(LiteYukonUser user) {
        return propertyChecker.check(user);
    }

    public String getLinkKey() {
        return this.linkKey;
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
