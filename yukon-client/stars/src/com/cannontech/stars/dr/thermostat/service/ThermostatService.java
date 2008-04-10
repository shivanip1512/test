package com.cannontech.stars.dr.thermostat.service;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.user.YukonUserContext;

/**
 * Service class for thermostat operations
 */
public interface ThermostatService {

    /**
     * Method used to perform a manual event on a thermostat
     * @param userContext - User context for manual event
     * @param account - Account for thermostat
     * @param event - Manual event to be performed
     * @return Status of manual event execution
     */
    public String executeManualEvent(YukonUserContext userContext,
            CustomerAccount account, ThermostatManualEvent event);

}
