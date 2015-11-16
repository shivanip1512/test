package com.cannontech.web.contextualMenu.model.menu;

import java.util.List;

import com.cannontech.web.contextualMenu.model.menuEntry.MenuEntry;

public abstract class ContextualMenu extends SimpleMenu {

    protected List<MenuEntry> menuEntries;

    @Override
    public List<MenuEntry> getMenuEntries() {
        return menuEntries;
    }

    public void setMenuEntries(List<MenuEntry> menuEntries) {
        this.menuEntries = menuEntries;
    }
}
