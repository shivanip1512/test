package com.cannontech.web.menu.option.producer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public abstract class FaqMenuOptionProducer extends DynamicMenuOptionProducer {
    protected RolePropertyDao rolePropertyDao;
    protected StarsDatabaseCache starsDatabaseCache;
    
    protected boolean isInherited(String link) {
        boolean result = ServletUtils.INHERITED_FAQ.equals(link);
        return result;
    }
    
    protected String getInheritedLink(LiteYukonUser user, String defaultLink) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user); 

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
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
}