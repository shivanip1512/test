package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResponseEventLogService {
    
    // Scenario logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.scenario")
    public void threeTierScenarioStarted(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                         @Arg(EventLogArgEnum.scenarioName) String scenarioName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.scenario")
    public void threeTierScenarioStopped(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                         @Arg(EventLogArgEnum.scenarioName) String scenarioName);

    // Control Area logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStarted(@Arg(EventLogArgEnum.controlAreaName) String controlAreaName, 
                                   Date startDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStopped(@Arg(EventLogArgEnum.controlAreaName) String controlAreaName, 
                                   Date stopDate);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaTriggersChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(EventLogArgEnum.controlAreaName) String controlAreaName,
                                                    Double threshold1,
                                                    Double offset1,
                                                    Double threshold2,
                                                    Double offset2);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTriggersChanged(@Arg(EventLogArgEnum.controlAreaName) String controlAreaName, 
                                           Double threshold1, Double offset1, 
                                           Double threshold2, Double offset2);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaTimeWindowChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                      @Arg(EventLogArgEnum.controlAreaName) String controlAreaName,
                                                      int startSeconds,
                                                      int stopSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTimeWindowChanged(@Arg(EventLogArgEnum.controlAreaName) String controlAreaName,
                                             int startSeconds,
                                             int stopSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaStarted(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                            @Arg(EventLogArgEnum.controlAreaName) String controlAreaName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaStopped(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                            @Arg(EventLogArgEnum.controlAreaName) String controlAreaName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaEnabled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                            @Arg(EventLogArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaEnabled(@Arg(EventLogArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaDisabled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                             @Arg(EventLogArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaDisabled(@Arg(EventLogArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaPeakReset(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(EventLogArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaPeakReset(@Arg(EventLogArgEnum.controlAreaName) String controlAreaName);

    // Program logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramScheduled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                          @Arg(EventLogArgEnum.programName) String programName, 
                                          Date startDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programScheduled(@Arg(EventLogArgEnum.programName) String programName,
                                 Boolean overrideConstraints, 
                                 @Arg(EventLogArgEnum.gearName) String gearName, 
                                 Date startDate,
                                 Boolean stopScheduled, 
                                 Date stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramStopped(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                                        @Arg(EventLogArgEnum.programName) String programName, 
                                        Date stopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopped(@Arg(EventLogArgEnum.programName) String programName, 
                               Date stopDate, 
                               @Arg(EventLogArgEnum.gearName) String gearName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramStopScheduled(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                              @Arg(EventLogArgEnum.programName) String programName,
                                              Date stopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopScheduled(@Arg(EventLogArgEnum.programName) String programName,
                                     Date stopDate,
                                     @Arg(EventLogArgEnum.gearName) String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramChangeGear(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                           @Arg(EventLogArgEnum.programName) String programName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programChangeGear(@Arg(EventLogArgEnum.programName) String programName, 
                                  @Arg(EventLogArgEnum.gearName) String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramEnabled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                        @Arg(EventLogArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programEnabled(@Arg(EventLogArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramDisabled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                         @Arg(EventLogArgEnum.programName) String programName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programDisabled(@Arg(EventLogArgEnum.programName) String programName);

    // Load Group logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupShed(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                                       @Arg(EventLogArgEnum.loadGroupName) String loadGroupName, 
                                       @Arg(EventLogArgEnum.shedDuration) int shedSeconds);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupShed(@Arg(EventLogArgEnum.loadGroupName) String loadGroupName, 
                              @Arg(EventLogArgEnum.shedDuration) int shedSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupRestore(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                          @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupRestore(@Arg(EventLogArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupEnabled(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                                          @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupEnabled(@Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupDisabled(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                                           @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupDisabled(@Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

}
