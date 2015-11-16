package com.cannontech.web.contextualMenu.model.menu;

import java.util.List;

import org.springframework.beans.factory.BeanNameAware;

import com.cannontech.web.contextualMenu.CollectionCategory;
import com.cannontech.web.contextualMenu.model.menuEntry.MenuEntry;

public interface Menu extends BeanNameAware {
    List<MenuEntry> getMenuEntries();
    CollectionCategory getCollectionCategory();

    /**
     * Gets the name of the Spring bean name (or id) as defined in the applicationContext.xml file
     */
    String getBeanName();
}
