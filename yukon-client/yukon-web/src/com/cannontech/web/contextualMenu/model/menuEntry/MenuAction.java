package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanNameAware;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.contextualMenu.CollectionCategory;

/**
 * This interface represents the required methods for each individual MenuEntry
 */
public interface MenuAction extends MenuEntry, BeanNameAware {
    String getUrl(CollectionCategory collectionCategory, Map<String, String> inputs);
    YukonRole getRequiredRole();
    Set<YukonRoleProperty> getRequiredRoleProperties();
    String getFormatKey();
}
