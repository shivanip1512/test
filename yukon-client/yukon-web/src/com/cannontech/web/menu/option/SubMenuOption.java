package com.cannontech.web.menu.option;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

/**
 * Represents a menu option that when selected should show a sub menu with
 * additional options to choose from.
 */
public class SubMenuOption extends BaseMenuOption implements MenuOptionProducer {
    private List<MenuOptionProducer> subOptions = new ArrayList<MenuOptionProducer>();

    public SubMenuOption(YukonMessageSourceResolvable menuText) {
        super(menuText);
    }

    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        List<MenuOption> menuOptions = expandProducers(subOptions, userContext);
        return menuOptions;
    }

    public void setSubOptions(List<MenuOptionProducer> subOptions) {
        this.subOptions = subOptions;
    }

    public static List<MenuOption> expandProducers(List<MenuOptionProducer> subOptions, YukonUserContext userContext) {
        List<MenuOption> result = new ArrayList<MenuOption>();
        
        for (MenuOptionProducer menuOptionProducer : subOptions) {
            List<MenuOption> menuOptions = menuOptionProducer.getMenuOptions(userContext);
            result.addAll(menuOptions);
        }
        
        return result;
    }
    
}
