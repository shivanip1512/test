package com.cannontech.web.menu;

import org.apache.commons.collections.Predicate;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

public class CheckUserPredicate implements Predicate {

    private final YukonUserContext userContext;

    public CheckUserPredicate(YukonUserContext userContext) {
        this.userContext = userContext;
    }

    public boolean evaluate(Object obj) {
        if (!(obj instanceof MenuOptionProducer)) {
            return false;
        }
        MenuOptionProducer menuOptionProducer = (MenuOptionProducer) obj;
        return menuOptionProducer.isValidForUser(userContext);
    }

}
