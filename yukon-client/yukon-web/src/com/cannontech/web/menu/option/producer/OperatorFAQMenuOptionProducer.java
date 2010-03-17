package com.cannontech.web.menu.option.producer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public class OperatorFAQMenuOptionProducer extends DynamicMenuOptionProducer {
    private static final String defaultLink = "/spring/stars/operator/faq";
    private static final String labelKey = "operatorFaq";
    private static final String menuTextKey = "yukon.web.menu.config.consumer.questions.faq";
    private RolePropertyDao rolePropertyDao;
    private CustomerAccountDao customerAccountDao;
    private ECMappingDao ecMappingDao;
    
    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {
        final LiteYukonUser user = userContext.getYukonUser();
        
        String link = 
            rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_WEB_LINK_FAQ, user);
        
        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(labelKey, menuTextKey);
        if (isEmpty(link)) {
            link = defaultLink;
        } else if (isInherited(link)) {
            link = getInheritedLink(user);

            // Custom link - open in new window
            menuOption.setNewWindow(true);
        } else {
            // Custom link - open in new window
            menuOption.setNewWindow(true);
        }
        
        menuOption.setLinkUrl(link);
        
        return Arrays.<MenuOption>asList(menuOption);
    }
    
    private boolean isEmpty(String value) {
        boolean result = (StringUtils.isEmpty(value) || value.equals(CtiUtilities.STRING_NONE));
        return result;
    }

    private boolean isInherited(String link) {
        boolean result = ServletUtils.INHERITED_FAQ.equals(link);
        return result;
    }
    
    private String getInheritedLink(LiteYukonUser user) {
        final List<CustomerAccount> customerAccountList = customerAccountDao.getByUser(user);
        final CustomerAccount customerAccount = customerAccountList.get(0);

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);

        while (ServletUtils.isCustomerFAQInherited(energyCompany)) {
            energyCompany = energyCompany.getParent();
        }

        // The top most parent cannot inherit FAQ's
        if (energyCompany == null) return defaultLink;
        
        String link = ServletUtils.getCustomerFAQLink(energyCompany);
        if (isEmpty(link)) link = defaultLink;
        
        return link;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
}
