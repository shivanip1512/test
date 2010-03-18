package com.cannontech.stars.dr.controlhistory.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;

public interface ControlHistoryDao {

    /**
     * @return - a Map of ProgramID's to List of ControlHistory Objects.
     */
    public ListMultimap<Integer, ControlHistory> getControlHistory(CustomerAccount customerAccount, 
                                                                List<Appliance> appliances, 
                                                                YukonUserContext yukonUserContext, 
                                                                ControlPeriod controlPeriod);

}
