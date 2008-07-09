package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;
import com.cannontech.web.widget.support.WidgetUtils;

/**
 * Dynamic MenuOptionProducer for thermostats
 */
public class ThermostatMenuOptionProducer extends DynamicMenuOptionProducer {

    private InventoryDao inventoryDao;
    private CustomerAccountDao customerAccountDao;

    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    @Override
    public List<MenuOption> getMenuOptions(YukonUserContext userContext) {

        // Generate one menu option for each of the thermostats associated with
        // the user

        List<MenuOption> optionList = new ArrayList<MenuOption>();

        // Get all thermostats for each of the user's accounts
        List<CustomerAccount> accounts = customerAccountDao.getByUser(userContext.getYukonUser());
        for (CustomerAccount account : accounts) {
            List<Thermostat> thermostats = inventoryDao.getThermostatsByAccount(account);

            for (Thermostat thermostat : thermostats) {
                String label = thermostat.getLabel();
                YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.thermostat.name",
                                                                                           label);

                String safeLabel = WidgetUtils.generateSafeJsString(label);
                SubMenuOption option = new SubMenuOption("thermostat_" + safeLabel, resolvable, false);
                List<MenuOptionProducer> subOptions = createSubMenu(thermostat);
                option.setSubOptions(subOptions);
                optionList.add(option);
            }
        }

        // Add an 'All' option if there is at least two thermostats
        if(optionList.size() > 1) {
	        String menuTextKey = "yukon.web.menu.config.consumer.thermostat.all";
	        SimpleMenuOptionLink option = new SimpleMenuOptionLink("allThermostats", menuTextKey);
	        option.setLinkUrl("/spring/stars/consumer/thermostat/view/all");
	        optionList.add(0, option);
        }
        
        return optionList;
    }

    public List<MenuOptionProducer> createSubMenu(Thermostat thermostat) {
        List<MenuOptionProducer> producerList = new ArrayList<MenuOptionProducer>(4);

        MenuOptionProducer producer;
        
        // Create schedule menu option
        producer = createLink("schedule", "/spring/stars/consumer/thermostat/schedule/view?thermostatIds=" + thermostat.getId());
        producerList.add(producer);

        // Create manual menu option
        producer = createLink("manual", "/spring/stars/consumer/thermostat/view?thermostatIds=" + thermostat.getId());
        producerList.add(producer);

        // Create saved schedules menu option
        producer = createLink("savedSchedules", "/spring/stars/consumer/thermostat/schedule/view/saved?thermostatIds=" + thermostat.getId());
        producerList.add(producer);

        return producerList;
    }

    private MenuOptionProducer createLink(String labelKey, String link) {
        String menuTextKey = "yukon.web.menu.config.consumer.thermostat." + labelKey;
        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(labelKey, menuTextKey);
        menuOption.setLinkUrl(link);
        StaticMenuOptionProducer menuOptionProducer = new StaticMenuOptionProducer(menuOption, new NullUserChecker());

        return menuOptionProducer;
    }

}
