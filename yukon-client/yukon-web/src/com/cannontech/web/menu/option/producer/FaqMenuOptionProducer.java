package com.cannontech.web.menu.option.producer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public class FaqMenuOptionProducer extends DynamicMenuOptionProducer {
    private RolePropertyDao rolePropertyDao;

    private static final String defaultLink = "/spring/stars/consumer/faq";
    private static final String labelKey = "faq";
    private static final String menuTextKey = "yukon.web.menu.config.consumer.questions.faq";
    
    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {
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
        
        return Arrays.<MenuOption>asList(menuOption);
    }
    
    // DI Setters
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}