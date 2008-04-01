package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Abstract base class for MenuOption
 */
public abstract class BaseMenuOption implements MenuOption {

    private String id = null;
    private YukonMessageSourceResolvable menuText = null;

    public BaseMenuOption(YukonMessageSourceResolvable menuText) {
        this.menuText = menuText;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public YukonMessageSourceResolvable getMenuText() {
        return menuText;
    }

}
