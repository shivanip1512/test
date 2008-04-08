package com.cannontech.stars.dr.controlhistory.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.user.YukonUserContext;

public interface ControlHistoryDao {

    /**
     * @return - a Map of ProgramID's to List of ControlHistory Objects.
     */
    public Map<Integer, List<ControlHistory>> getControlHistory(CustomerAccount customerAccount, 
            List<Appliance> appliances, YukonUserContext yukonUserContext);
    
}
