package com.cannontech.stars.dr.controlhistory.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;

public interface ControlHistoryDao {

    /**
     * @return - a Map of ProgramID's to List of ControlHistory Objects.
     */
    public Map<Integer, List<ControlHistory>> getControlHistory(CustomerAccount customerAccount, 
                                                                List<Appliance> appliances, 
                                                                YukonUserContext yukonUserContext, 
                                                                ControlPeriod controlPeriod);

    /**
     * This method gets a program to control history ListMultimap that holds all the control history 
     * since the inventory was added to the account and enrolled.
     * 
     */
    public ListMultimap<Program, ControlHistory> getControlHistoryByProgramList(int customerAccountId, 
                                                                                List<Program> programList,
                                                                                YukonUserContext yukonUserContext,
                                                                                ControlPeriod controlPeriod);


    
}
