package com.cannontech.web.menu.option;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Abstract base class for MenuOption
 */
public abstract class BaseMenuOption implements MenuOption {

    private String id = null;
    private YukonMessageSourceResolvable menuText = null;
    private MessageSourceResolvable menuTooltip = null;

    public BaseMenuOption(String id, YukonMessageSourceResolvable menuText) {
        this.id = id;
        this.menuText = menuText;
    }
    
    public BaseMenuOption(String id, String menuTextKey) {
        this.id = id;
        this.menuText = new YukonMessageSourceResolvable(menuTextKey);
        this.menuTooltip = new YukonMessageSourceResolvable(menuTextKey + ".title");
    }

    public String getId() {
        return id;
    }

    public MessageSourceResolvable getMenuText() {
        return menuText;
    }
    
    @Override
    public MessageSourceResolvable getMenuTooltip() {
        return menuTooltip ;
    }
    
    public void setMenuTooltip(MessageSourceResolvable menuTooltip) {
        this.menuTooltip = menuTooltip;
    }

}
