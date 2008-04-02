package com.cannontech.web.menu.option;

import java.util.List;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.MenuBase;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

/**
 * Represents a menu option that when selected should show a sub menu with
 * additional options to choose from.
 */
public class SubMenuOption extends BaseMenuOption implements MenuOptionProducer {
    private MenuBase menuBase = new MenuBase();

    public SubMenuOption(String id, YukonMessageSourceResolvable menuText) {
        super(id, menuText);
    }
    
    public SubMenuOption(String id, String menuTextKey) {
        super(id, menuTextKey);
    }

    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        List<MenuOption> menuOptions = menuBase.getMenuOptions(userContext);
        return menuOptions;
    }

    public void setSubOptions(List<MenuOptionProducer> subOptions) {
        this.menuBase = new MenuBase(subOptions);
    }
}
