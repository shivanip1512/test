package com.cannontech.web.menu;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;

public abstract class BaseMenuOption {
    private UserChecker propertyChecker = new NullUserChecker();
    private OptionNameFactory nameFactory = OptionNameFactory.createPlain("");
    private String id = null;
    
    public BaseMenuOption(OptionNameFactory nameFactory) {
        super();
        this.nameFactory = nameFactory;
    }

    public BaseMenuOption() {
        super();
    }

    public void setPropertyChecker(UserChecker propertyChecker) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
