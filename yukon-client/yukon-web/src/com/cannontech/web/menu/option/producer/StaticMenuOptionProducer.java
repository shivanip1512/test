package com.cannontech.web.menu.option.producer;

import java.util.Collections;
import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.menu.option.MenuOption;

/**
 * Menu option producer class used for all static menu options
 */
public class StaticMenuOptionProducer implements MenuOptionProducer {

    private MenuOption menuOption;
    private UserChecker userChecker;

    public StaticMenuOptionProducer(MenuOption menuOption,
            UserChecker userChecker) {
        super();
        this.menuOption = menuOption;
        this.userChecker = userChecker;
    }

    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        if (userChecker.check(userContext.getYukonUser())) {
            return Collections.singletonList(menuOption);
        } else {
            return Collections.emptyList();
        }
    }


}
