package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResovable;

/**
 * Abstract base class for MenuOption
 */
public abstract class BaseMenuOption implements MenuOption {

    private String id = null;
    private YukonMessageSourceResovable menuText = null;

    public BaseMenuOption(YukonMessageSourceResovable menuText) {
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
    public YukonMessageSourceResovable getMenuText() {
        return menuText;
    }

}
