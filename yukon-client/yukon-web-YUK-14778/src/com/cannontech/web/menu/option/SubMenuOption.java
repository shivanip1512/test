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
    private String linkUrl = null;
    
    private boolean collapseIfEmpty = false;

    public SubMenuOption(String id, YukonMessageSourceResolvable menuText, boolean collapseIfEmpty) {
        super(id, menuText);
        this.collapseIfEmpty = collapseIfEmpty;
    }
    
    public SubMenuOption(String id, String menuTextKey, boolean collapseIfEmpty) {
        super(id, menuTextKey);
        this.collapseIfEmpty = collapseIfEmpty;
    }

    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        List<MenuOption> menuOptions = menuBase.getMenuOptions(userContext);
        return menuOptions;
    }

    public void setSubOptions(List<? extends MenuOptionProducer> subOptions) {
        this.menuBase = new MenuBase(subOptions);
    }
    
    public boolean isCollapseIfEmpty() {
		return collapseIfEmpty;
	}
    
    public boolean hasLink() {
        return linkUrl != null;
    }

    public String getUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
