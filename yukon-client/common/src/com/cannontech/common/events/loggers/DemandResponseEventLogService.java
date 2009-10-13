package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResponseEventLogService {
    
    // Scenario logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.scenario")
    public void threeTierScenarioStarted(LiteYukonUser yukonUser, String scenarioName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.scenario")
    public void threeTierScenarioStopped(LiteYukonUser yukonUser, String scenarioName);

    // Control Area logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStarted(String controlAreaName, 
                                   Date startDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStopped(String controlAreaName, 
                                   Date stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaTriggersChanged(LiteYukonUser yukonUser,
                                                    String name,
                                                    Double threshold1,
                                                    Double offset1,
                                                    Double threshold2,
                                                    Double offset2);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTriggersChanged(String controlAreaName, Double threshold1, 
                                           Double offset1, Double threshold2, Double offset2);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaTimeWindowChanged(LiteYukonUser yukonUser,
                                                      String name,
                                                      int startSeconds,
                                                      int stopSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTimeWindowChanged(String controlAreaName,
                                             int startTime,
                                             int stopTime);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaStarted(LiteYukonUser yukonUser, String controlAreaName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaStopped(LiteYukonUser yukonUser, String controlAreaName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaEnabled(LiteYukonUser yukonUser, String name);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaEnabled(String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaDisabled(LiteYukonUser yukonUser, String name);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaDisabled(String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaPeakReset(LiteYukonUser yukonUser, String name);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaPeakReset(String controlAreaName);

    // Program logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramScheduled(LiteYukonUser yukonUser, String name, Date startDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programScheduled(String programName,
            Boolean overrideConstraints, String gearName, Date startDate,
            Boolean stopScheduled, Date stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramStopped(LiteYukonUser user, String name, Date stopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopped(String programName, Date stopDate, String gearName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramStopScheduled(LiteYukonUser user, String name, Date stopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopScheduled(String programName, Date stopDate,
            String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramChangeGear(LiteYukonUser yukonUser, String name);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programChangeGear(String programName, String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramEnabled(LiteYukonUser yukonUser, String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programEnabled(String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramDisabled(LiteYukonUser yukonUser, String programName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programDisabled(String programName);

    // Load Group logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupShed(LiteYukonUser user, String loadGroupName, int shedSeconds);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupShed(String loadGroupName, int shedSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupRestore(LiteYukonUser yukonUser, String name);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupRestore(String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupEnabled(LiteYukonUser user, String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupEnabled(String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupDisabled(LiteYukonUser user, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupDisabled(String loadGroupName);

}
