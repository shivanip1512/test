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

/**
 * Dynamic MenuOptionProducer for thermostats
 */
public class ThermostatMenuOptionProducer extends MenuOptionProducerBase {

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
            int accountId = account.getAccountId();
            List<Thermostat> thermostats = inventoryDao.getThermostatsByAccount(accountId);

            for (Thermostat thermostat : thermostats) {
                String label = thermostat.getLabel();
                YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.thermostat.name",
                                                                                              label);

                SubMenuOption option = new SubMenuOption(resolvable);
                option.setId("thermostat_" + label);
                List<MenuOptionProducer> subOptions = createSubMenu(thermostat);
                option.setSubOptions(subOptions);
                optionList.add(option);
            }
        }

        return optionList;
    }

    public List<MenuOptionProducer> createSubMenu(Thermostat thermostat) {
        List<MenuOptionProducer> producerList = new ArrayList<MenuOptionProducer>(4);

        MenuOptionProducer producer;
        // Create change label menu option
        producer = createLink("changeLabel", "/user/ConsumerStat/stat/NewLabel.jsp?Item=0");
        producerList.add(producer);

        // Create schedule menu option
        producer = createLink("schedule", "/user/ConsumerStat/stat/ThermSchedule.jsp?Item=0");
        producerList.add(producer);

        // Create schedule menu option
        producer = createLink("manual", "/user/ConsumerStat/stat/Thermostat.jsp?Item=0");
        producerList.add(producer);

        // Create schedule menu option
        producer = createLink("savedSchedules", "/user/ConsumerStat/stat/SavedSchedules.jsp?Item=0");
        producerList.add(producer);

        return producerList;
    }

    private MenuOptionProducer createLink(String labelKey, String link) {
        YukonMessageSourceResolvable menuText = new YukonMessageSourceResolvable("yukon.web.menu.config.consumer.thermostat." + labelKey);
        SimpleMenuOptionLink menuOption = new SimpleMenuOptionLink(menuText);
        menuOption.setLinkUrl(link);
        StaticMenuOptionProducer menuOptionProducer = new StaticMenuOptionProducer(menuOption, new NullUserChecker());

        return menuOptionProducer;
    }

}
