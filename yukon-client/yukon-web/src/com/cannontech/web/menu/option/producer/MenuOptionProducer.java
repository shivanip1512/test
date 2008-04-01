package com.cannontech.web.menu.option.producer;

import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;

/**
 * Interface used to produce menu options
 */
public interface MenuOptionProducer {

    /**
     * Method to get a list of menu options from this producer
     * @param userContext - UserContext to get menu options for
     * @return List of user-specific menu options
     */
    public List<MenuOption> getMenuOptions(YukonUserContext userContext);
}
