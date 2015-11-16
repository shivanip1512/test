package com.cannontech.web.menu.option.producer;

import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.google.common.collect.ImmutableList;

/**
 * Abstract base class for menu option producers
 */
public abstract class DynamicMenuOptionProducer implements MenuOptionProducer {
    private UserChecker userChecker = null;
    
    public void setUserChecker(UserChecker newUserChecker) {
        userChecker = newUserChecker;
    }

    @Override
    public final List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        if (userChecker.check(userContext.getYukonUser())) {
            return doGetMenuOptions(userContext);
        } else {
            return ImmutableList.of();
        }
    }

    /**
     * A helper method to get a list of menu options from a producer
     * 
     * @param userContext - UserContext to get menu options for
     * @return List of user-specific menu options
     */
    protected abstract List<MenuOption> doGetMenuOptions(YukonUserContext userContext);
}
