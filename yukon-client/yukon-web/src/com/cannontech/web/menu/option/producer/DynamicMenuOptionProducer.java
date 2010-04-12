package com.cannontech.web.menu.option.producer;

import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.AggregateAndUserChecker;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.google.common.collect.ImmutableList;

/**
 * Abstract base class for menu option producers
 */
public abstract class DynamicMenuOptionProducer implements MenuOptionProducer {

    private UserChecker userChecker = null;
    
    public void addUserChecker(UserChecker newUserChecker) {
        if (userChecker == null) {
            userChecker = newUserChecker;
        } else {
            userChecker = new AggregateAndUserChecker(userChecker, newUserChecker);
        }
        
    }
    
    @Override
    public final List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        if (userChecker.check(userContext.getYukonUser())) {
            return doGetMenuOptions(userContext);
        } else {
            return ImmutableList.of();
        }
    }

    protected abstract List<MenuOption> doGetMenuOptions(YukonUserContext userContext);
    
}
