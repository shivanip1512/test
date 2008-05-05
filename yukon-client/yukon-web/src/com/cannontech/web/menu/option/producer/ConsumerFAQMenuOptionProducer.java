package com.cannontech.web.menu.option.producer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public class ConsumerFAQMenuOptionProducer extends DynamicMenuOptionProducer {
    private static final String defaultLink = "/spring/stars/consumer/faq";
    private static final String labelKey = "faq";
    private static final String menuTextKey = "yukon.web.menu.config.consumer.questions.faq";
    private AuthDao authDao;
    
    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        final LiteYukonUser user = userContext.getYukonUser();
        
        String link = authDao.getRolePropertyValue(user, ResidentialCustomerRole.WEB_LINK_FAQ);
        if (isEmpty(link)) link = defaultLink;
        
        final SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(labelKey, menuTextKey);
        menuOption.setLinkUrl(link);
        
        return Arrays.<MenuOption>asList(menuOption);
    }
    
    private boolean isEmpty(String value) {
        boolean result = (StringUtils.isEmpty(value) || value.equals(CtiUtilities.STRING_NONE));
        return result;
    }
    
    @Autowired
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
