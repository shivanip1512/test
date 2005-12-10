package com.cannontech.web.menu;

import org.apache.commons.collections.Predicate;

import com.cannontech.database.data.lite.LiteYukonUser;

public class CheckUserPredicate implements Predicate {
    
    private final LiteYukonUser yukonUser;

    public CheckUserPredicate(LiteYukonUser user) {
        yukonUser = user;
    }

    public boolean evaluate(Object obj) {
        if (!(obj instanceof BaseMenuOption)) {
            return false;
        }
        BaseMenuOption menuOption = (BaseMenuOption) obj;
        return menuOption.isValidForUser(yukonUser);
    }

}
