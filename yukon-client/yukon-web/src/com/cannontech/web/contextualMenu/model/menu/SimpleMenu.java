package com.cannontech.web.contextualMenu.model.menu;

import java.util.List;

import com.cannontech.web.contextualMenu.model.menuEntry.MenuEntry;

public abstract class SimpleMenu implements Menu {

    private String beanName;

    @Override
    public abstract List<MenuEntry> getMenuEntries();

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

}
