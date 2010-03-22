package com.cannontech.web.menu.option.producer;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public abstract class FaqMenuOptionProducer extends DynamicMenuOptionProducer {
    protected RolePropertyDao rolePropertyDao;
    protected CustomerAccountDao customerAccountDao;
    protected ECMappingDao ecMappingDao;
    
    protected boolean isInherited(String link) {
        boolean result = ServletUtils.INHERITED_FAQ.equals(link);
        return result;
    }
    
    protected String getInheritedLink(LiteYukonUser user, String defaultLink) {
        final List<CustomerAccount> customerAccountList = customerAccountDao.getByUser(user);
        final CustomerAccount customerAccount = customerAccountList.get(0);

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);

        while (ServletUtils.isCustomerFAQInherited(energyCompany)) {
            energyCompany = energyCompany.getParent();
        }

        // The top most parent cannot inherit FAQ's
        if (energyCompany == null) return defaultLink;
        
        String link = ServletUtils.getCustomerFAQLink(energyCompany);
        if (StringUtils.isEmpty(link)) link = defaultLink;
        
        return link;
    }
    
    protected void buildMenuOption(final LiteYukonUser user, String link, String defaultLink,
                                     SimpleMenuOptionLink menuOption) {

        if (StringUtils.isEmpty(link)) {
            link = defaultLink;
        } else if (isInherited(link)) {
            link = getInheritedLink(user, defaultLink);

            // Custom link - open in new window
            menuOption.setNewWindow(true);
        } else {
            // Custom link - open in new window
            menuOption.setNewWindow(true);
        }
        
        menuOption.setLinkUrl(link);
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
