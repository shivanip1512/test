package com.cannontech.web.menu.option.producer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.i18n.YukonMessageSourceResolvableImpl;
import com.cannontech.i18n.YukonMessageSourceResovable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.TopLevelOption;

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
                YukonMessageSourceResovable resolvable = new YukonMessageSourceResolvableImpl("yukon.web.menu.config.consumer.thermostat.name",
                                                                                              label);

                TopLevelOption option = new TopLevelOption(resolvable);
                option.setId("thermostat_" + label);
                optionList.add(option);
            }
        }

        return optionList;
    }

    @Override
    public boolean hasChildren(YukonUserContext userContext) {
        return true;
    }

    @Override
    public Iterator<MenuOptionProducer> getChildren(YukonUserContext userContext) {
        List<MenuOptionProducer> producerList = new ArrayList<MenuOptionProducer>();

        // Create change label menu option
        StaticMenuOptionProducer labelProducer = new StaticMenuOptionProducer();
        labelProducer.setPropertyChecker(new NullUserChecker());
        labelProducer.setId(this.getId() + "changeLabel");
        labelProducer.setKey("yukon.web.menu.config.consumer.thermostat.changeLabel");
        labelProducer.setLinkUrl("/user/ConsumerStat/stat/NewLabel.jsp?Item=0");
        producerList.add(labelProducer);

        // Create schedule menu option
        StaticMenuOptionProducer scheduleProducer = new StaticMenuOptionProducer();
        scheduleProducer.setPropertyChecker(new NullUserChecker());
        scheduleProducer.setId(this.getId() + "schedule");
        scheduleProducer.setKey("yukon.web.menu.config.consumer.thermostat.schedule");
        scheduleProducer.setLinkUrl("/user/ConsumerStat/stat/ThermSchedule.jsp?Item=0");
        producerList.add(scheduleProducer);

        // Create schedule menu option
        StaticMenuOptionProducer manualProducer = new StaticMenuOptionProducer();
        manualProducer.setPropertyChecker(new NullUserChecker());
        manualProducer.setId(this.getId() + "manual");
        manualProducer.setKey("yukon.web.menu.config.consumer.thermostat.manual");
        manualProducer.setLinkUrl("/user/ConsumerStat/stat/Thermostat.jsp?Item=0");
        producerList.add(manualProducer);

        // Create schedule menu option
        StaticMenuOptionProducer savedSchedulesProducer = new StaticMenuOptionProducer();
        savedSchedulesProducer.setPropertyChecker(new NullUserChecker());
        savedSchedulesProducer.setId(this.getId() + "savedSchedules");
        savedSchedulesProducer.setKey("yukon.web.menu.config.consumer.thermostat.savedSchedules");
        savedSchedulesProducer.setLinkUrl("/user/ConsumerStat/stat/SavedSchedules.jsp?Item=0");
        producerList.add(savedSchedulesProducer);

        return producerList.iterator();
    }

}
