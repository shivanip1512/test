package com.cannontech.web.menu;

import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SubMenuOption;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

public class MenuBase implements MenuOptionProducer {
    private List<MenuOptionProducer> topLevelOptions;

    public MenuBase(List<MenuOptionProducer> topLevelOptions) {
        this.topLevelOptions = topLevelOptions;
    }

    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        List<MenuOption> menuOptions = SubMenuOption.expandProducers(topLevelOptions, userContext);
        return menuOptions;
    }
}
