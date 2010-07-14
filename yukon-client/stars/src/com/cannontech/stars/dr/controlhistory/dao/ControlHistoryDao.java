package com.cannontech.stars.dr.controlhistory.dao;

import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;

public interface ControlHistoryDao {

    /**
     * @return - a Map of ProgramID's to List of ControlHistory Objects.
     */
    public ListMultimap<Integer, ControlHistory> getControlHistory(int accountId, YukonUserContext userContext, ControlPeriod controlPeriod, boolean past);

}