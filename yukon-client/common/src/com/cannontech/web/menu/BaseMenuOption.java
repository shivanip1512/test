package com.cannontech.web.menu;

import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class BaseMenuOption {
    private OptionPropertyChecker propertyChecker = OptionPropertyChecker.createNullChecker();
    private OptionNameFactory nameFactory = OptionNameFactory.createPlain("");
    
    public BaseMenuOption(OptionNameFactory nameFactory) {
        super();
        this.nameFactory = nameFactory;
    }

    public BaseMenuOption() {
        super();
    }

    public void setPropertyChecker(OptionPropertyChecker propertyChecker) {
        this.propertyChecker = propertyChecker;
    }

    public boolean isValidForUser(LiteYukonUser user) {
        return propertyChecker.check(user);
    }

    public String getLinkName(LiteYukonUser yukonUser) {
        return nameFactory.getName(yukonUser);
    }

    public void setLinkName(String linkName) {
        nameFactory = OptionNameFactory.createPlain(linkName);
    }

}
