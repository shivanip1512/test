package com.cannontech.web.menu.option.producer;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.google.common.collect.Lists;

public class FaqMenuOptionProducer extends DynamicMenuOptionProducer {
    private RolePropertyDao rolePropertyDao;

    private static final String defaultLink = "/stars/consumer/faq";
    private static final String labelKey = "faq";
    private static final String menuTextKey = "yukon.web.menu.config.consumer.questions.faq";
    
    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {
        List<MenuOption> menuOptions = Lists.newArrayList();

        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(labelKey, menuTextKey);
        
        // Get the role property faq link
        String link = 
            rolePropertyDao.getPropertyStringValue(YukonRoleProperty.RESIDENTIAL_WEB_LINK_FAQ, userContext.getYukonUser());
        
        // If the role property isn't set, use the xml based faq as a default.
        if (StringUtils.isBlank(link)) {
            link = defaultLink;
        } else {
            menuOption.setNewWindow(true);
        }
        menuOption.setLinkUrl(link);
        
        menuOptions.add(menuOption);
        return menuOptions;
    }
    
    // DI Setters
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}