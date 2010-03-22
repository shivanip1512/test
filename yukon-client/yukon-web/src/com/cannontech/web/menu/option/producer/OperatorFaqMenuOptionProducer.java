package com.cannontech.web.menu.option.producer;

import java.util.Arrays;
import java.util.List;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public class OperatorFaqMenuOptionProducer extends FaqMenuOptionProducer {
    private static final String defaultLink = "/spring/stars/operator/faq";
    private static final String labelKey = "operatorFaq";
    private static final String menuTextKey = "yukon.web.menu.config.consumer.questions.faq";
    
    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        final LiteYukonUser user = userContext.getYukonUser();
        
        String link = 
            rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_WEB_LINK_FAQ, user);
        
        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(labelKey, menuTextKey);
        buildMenuOption(user, link, defaultLink, menuOption);
                
        return Arrays.<MenuOption>asList(menuOption);
    }
    
}
