package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;

/**
 * Dynamic MenuOptionProducer for thermostats
 */
public class ThermostatMenuOptionProducer extends DynamicMenuOptionProducer {

    private InventoryDao inventoryDao;
    private CustomerAccountDao customerAccountDao;
    private RolePropertyDao rolePropertyDao;

    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}

    @Override
    public List<MenuOption> doGetMenuOptions(YukonUserContext userContext) {

        // Generate one menu option for each of the thermostats associated with
        // the user

        List<MenuOption> optionList = new ArrayList<MenuOption>();

        // Get all thermostats for each of the user's accounts
        LiteYukonUser yukonUser = userContext.getYukonUser();
		List<CustomerAccount> accounts = customerAccountDao.getByUser(yukonUser);
        for (CustomerAccount account : accounts) {
            List<HardwareSummary> thermSummaryList = inventoryDao.getThermostatSummaryByAccount(account);

            for (HardwareSummary thermSummary : thermSummaryList) {
                String label = thermSummary.getDisplayName();
                YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.thermostat.name",
                                                                                           label);

                int inventoryId = thermSummary.getInventoryId();
                SubMenuOption option = new SubMenuOption("thermostat_" + inventoryId, resolvable, false);
                List<MenuOptionProducer> subOptions = createSubMenu(thermSummary);
                option.setSubOptions(subOptions);
                optionList.add(option);
            }
        }

        // Add an 'All' option if there is at least two thermostats
        boolean showAllLink = rolePropertyDao.checkProperty(
        		YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_THERMOSTATS_ALL, yukonUser);
        if(showAllLink && optionList.size() > 1) {
	        String menuTextKey = "yukon.web.menu.config.consumer.thermostat.all";
	        SimpleMenuOptionLink option = new SimpleMenuOptionLink("allThermostats", menuTextKey);
	        option.setLinkUrl("/spring/stars/consumer/thermostat/view/all");
	        optionList.add(0, option);
        }
        
        return optionList;
    }

    public List<MenuOptionProducer> createSubMenu(HardwareSummary thermSummary) {
        List<MenuOptionProducer> producerList = new ArrayList<MenuOptionProducer>(4);

        MenuOptionProducer producer;

        // Create saved schedules menu option
        int inventoryId = thermSummary.getInventoryId();
        producer = createLink("savedSchedules", "/spring/stars/consumer/thermostat/schedule/view/saved?thermostatIds=" + inventoryId);
        producerList.add(producer);
        
        // Create manual menu option
        producer = createLink("manual", "/spring/stars/consumer/thermostat/view?thermostatIds=" + inventoryId);
        producerList.add(producer);
        
        // Create history menu option
        producer = createLink("history", "/spring/stars/consumer/thermostat/schedule/history?thermostatIds=" + inventoryId);
        producerList.add(producer);

        return producerList;
    }

    private MenuOptionProducer createLink(String labelKey, String link) {
        String menuTextKey = "yukon.web.menu.config.consumer.thermostat." + labelKey;
        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(labelKey, menuTextKey);
        menuOption.setLinkUrl(link);
        StaticMenuOptionProducer menuOptionProducer = new StaticMenuOptionProducer(menuOption, NullUserChecker.getInstance());

        return menuOptionProducer;
    }

}
