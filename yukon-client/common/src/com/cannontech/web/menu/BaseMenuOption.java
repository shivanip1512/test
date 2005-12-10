package com.cannontech.web.menu;

import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class BaseMenuOption {
    private OptionPropertyChecker propertyChecker = OptionPropertyChecker.createNullChecker();
    protected String linkName = "";

    public void setPropertyChecker(OptionPropertyChecker propertyChecker) {
        this.propertyChecker = propertyChecker;
    }

    public boolean isValidForUser(LiteYukonUser user) {
        return propertyChecker.check(user);
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

}
