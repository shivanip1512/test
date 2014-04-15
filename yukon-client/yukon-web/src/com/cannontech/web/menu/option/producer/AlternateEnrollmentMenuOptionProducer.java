package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.Pair;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.service.AlternateEnrollmentService;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.google.common.collect.Maps;

public class AlternateEnrollmentMenuOptionProducer extends DynamicMenuOptionProducer {

    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private AlternateEnrollmentService aeService;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;

    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {

        List<MenuOption> menuOptions = new ArrayList<MenuOption>();
        
        CustomerAccount customer = customerAccountDao.getByUser(userContext.getYukonUser()).get(0);
        YukonEnergyCompany yec = ecDao.getEnergyCompanyByAccountId(customer.getAccountId());
        boolean altProgramEnrollment = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.ALTERNATE_PROGRAM_ENROLLMENT, yec.getEnergyCompanyId());
        
        // Generate a menu option if opt out is enabled and actual enrollment exists 
        if (altProgramEnrollment && optOutStatusService.getOptOutEnabled(userContext.getYukonUser()).isCommunicationEnabled()) {
            /* inventory to: set of normal programs, set of alternate programs */
            Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> available = Maps.newHashMap();
            /* inventory to: set of alternate programs, set of normal programs */
            Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> active = Maps.newHashMap();
            aeService.buildEnrollmentMaps(customer.getAccountId(), available, active);
            if (!available.isEmpty() || !active.isEmpty()){
                YukonMessageSourceResolvable resolvable = 
                        new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.programs.alternateEnrollment");
                
                SimpleMenuOptionLink optionLink = new SimpleMenuOptionLink("alternateEnrollment", resolvable);
                optionLink.setLinkUrl("/stars/consumer/ae/view");
                
                menuOptions.add(optionLink);
            }
        }
        
        return menuOptions;
    }
    
}