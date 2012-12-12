package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;

public class AlternateEnrollmentMenuOptionProducer extends DynamicMenuOptionProducer {

    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private YukonEnergyCompanyService yecService;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;

    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {

        List<MenuOption> menuOptions = new ArrayList<MenuOption>();
        
        List<CustomerAccount> customers = customerAccountDao.getByUser(userContext.getYukonUser());
        YukonEnergyCompany yec = yecService.getEnergyCompanyByAccountId(customers.get(0).getAccountId());
        boolean altProgramEnrollment = ecRolePropertyDao.checkProperty(YukonRoleProperty.ALTERNATE_PROGRAM_ENROLLMENT, yec);
        
        // Generate a menu option if opt out is enabled
        if (altProgramEnrollment && optOutStatusService.getOptOutEnabled(userContext.getYukonUser()).isCommunicationEnabled()) {
            YukonMessageSourceResolvable resolvable = 
                new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.programs.alternateEnrollment");
            
            SimpleMenuOptionLink optionLink = new SimpleMenuOptionLink("alternateEnrollment", resolvable);
            optionLink.setLinkUrl("/stars/consumer/ae/view");
            
            menuOptions.add(optionLink);
        }
        
        return menuOptions;
    }

}