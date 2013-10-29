package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanNameAware;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.contextualMenu.CollectionCategory;

/**
 * This interface represents the required methods for each individual MenuEntry
 */
public interface MenuAction extends MenuEntry, BeanNameAware {
    
    String getUrl(CollectionCategory collectionCategory, HttpServletRequest req);
    
    YukonRole getRequiredRole();
    
    Set<YukonRoleProperty> getRequiredRoleProperties();
    
    String getFormatKey();
}
