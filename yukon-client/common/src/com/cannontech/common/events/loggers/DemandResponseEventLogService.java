package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResponseEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStarted(LiteYukonUser user,
                                   String controlAreaName, 
                                   Boolean manual,
                                   Date startDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStopped(LiteYukonUser user,
                                  String controlAreaName, 
                                  Boolean manual,
                                  Date stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTriggersChanged(LiteYukonUser user, String controlAreaName, 
                                           Double threshold1, Double offset1, 
                                           Double threshold2, Double offset2);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTimeWindowChanged(LiteYukonUser user, 
                                             String controlAreaName,
                                             int startTime,
                                             int stopTime);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaEnabled(LiteYukonUser user, String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaDisabled(LiteYukonUser user, String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaPeakReset(LiteYukonUser user, String controlAreaName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programScheduled(LiteYukonUser user, String programName,
            Boolean overrideConstraints, String gearName, Boolean manual,
            Date startDate, Boolean stopScheduled, Date stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopped(LiteYukonUser user, String programName,
            Boolean manual, Date stopDate, String gearName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopScheduled(LiteYukonUser user, String programName,
            Boolean manual, Date stopDate, String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programChangeGear(LiteYukonUser user, String programName, String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programEnabled(LiteYukonUser user, String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programDisabled(LiteYukonUser user, String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupShed(LiteYukonUser user, String loadGroupName, int shedSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupRestore(LiteYukonUser user, String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupEnabled(LiteYukonUser user, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupDisabled(LiteYukonUser user, String loadGroupName);
}
