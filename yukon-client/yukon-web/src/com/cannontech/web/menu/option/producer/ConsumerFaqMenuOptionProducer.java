package com.cannontech.web.menu.option.producer;

import java.util.Arrays;
import java.util.List;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public class ConsumerFaqMenuOptionProducer extends FaqMenuOptionProducer {
    private static final String defaultLink = "/spring/stars/consumer/faq";
    private static final String labelKey = "faq";
    private static final String menuTextKey = "yukon.web.menu.config.consumer.questions.faq";
    
    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        final LiteYukonUser user = userContext.getYukonUser();
        
        String link = 
            rolePropertyDao.getPropertyStringValue(YukonRoleProperty.RESIDENTIAL_WEB_LINK_FAQ, user);
        
        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(labelKey, menuTextKey);
        buildMenuOption(user, link, defaultLink, menuOption);
        
        return Arrays.<MenuOption>asList(menuOption);
    }

}
