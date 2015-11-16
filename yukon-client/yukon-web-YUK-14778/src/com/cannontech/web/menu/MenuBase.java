package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

public class MenuBase implements MenuOptionProducer {
    private List<? extends MenuOptionProducer> menuOptionsProducers;

    public MenuBase(List<? extends MenuOptionProducer> topLevelOptions) {
        this.menuOptionsProducers = topLevelOptions;
    }

    public MenuBase() {
        this.menuOptionsProducers = Collections.emptyList();
    }
    
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        List<MenuOption> result = new ArrayList<MenuOption>();
        
        for (MenuOptionProducer menuOptionProducer : menuOptionsProducers) {
            List<MenuOption> menuOptions1 = menuOptionProducer.getMenuOptions(userContext);
            result.addAll(menuOptions1);
        }
        List<MenuOption> menuOptions = result;
        return menuOptions;
    }
}
