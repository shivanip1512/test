package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResponseEventLogService {
    
    // Scenario logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.scenario")
    public void threeTierScenarioStarted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                         @Arg(ArgEnum.scenarioName) String scenarioName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.scenario")
    public void threeTierScenarioStopped(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                         @Arg(ArgEnum.scenarioName) String scenarioName);

    // Control Area logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStarted(@Arg(ArgEnum.controlAreaName) String controlAreaName, 
                                   Date startDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaStopped(@Arg(ArgEnum.controlAreaName) String controlAreaName, 
                                   Date stopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaTriggersChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(ArgEnum.controlAreaName) String controlAreaName,
                                                    Double threshold1,
                                                    Double offset1,
                                                    Double threshold2,
                                                    Double offset2);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTriggersChanged(@Arg(ArgEnum.controlAreaName) String controlAreaName, 
                                           Double threshold1, Double offset1, 
                                           Double threshold2, Double offset2);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaTimeWindowChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                      @Arg(ArgEnum.controlAreaName) String controlAreaName,
                                                      int startSeconds,
                                                      int stopSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaTimeWindowChanged(@Arg(ArgEnum.controlAreaName) String controlAreaName,
                                             int startSeconds,
                                             int stopSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaStarted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                            @Arg(ArgEnum.controlAreaName) String controlAreaName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaStopped(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                            @Arg(ArgEnum.controlAreaName) String controlAreaName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaEnabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                            @Arg(ArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaEnabled(@Arg(ArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaDisabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                             @Arg(ArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaDisabled(@Arg(ArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void threeTierControlAreaPeakReset(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(ArgEnum.controlAreaName) String controlAreaName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.controlArea")
    public void controlAreaPeakReset(@Arg(ArgEnum.controlAreaName) String controlAreaName);

    // Program logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramScheduled(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                          @Arg(ArgEnum.programName) String programName, 
                                          Date startDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programScheduled(@Arg(ArgEnum.programName) String programName,
                                 Boolean overrideConstraints, 
                                 @Arg(ArgEnum.gearName) String gearName, 
                                 Date startDate,
                                 Boolean stopScheduled, 
                                 Date stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramStopped(@Arg(ArgEnum.username) LiteYukonUser user, 
                                        @Arg(ArgEnum.programName) String programName, 
                                        Date stopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopped(@Arg(ArgEnum.programName) String programName, 
                               Date stopDate, 
                               @Arg(ArgEnum.gearName) String gearName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramStopScheduled(@Arg(ArgEnum.username) LiteYukonUser user,
                                              @Arg(ArgEnum.programName) String programName,
                                              Date stopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programStopScheduled(@Arg(ArgEnum.programName) String programName,
                                     Date stopDate,
                                     @Arg(ArgEnum.gearName) String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramChangeGear(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                           @Arg(ArgEnum.programName) String programName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programChangeGear(@Arg(ArgEnum.programName) String programName, 
                                  @Arg(ArgEnum.gearName) String gearName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramEnabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                        @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programEnabled(@Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void threeTierProgramDisabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                         @Arg(ArgEnum.programName) String programName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.program")
    public void programDisabled(@Arg(ArgEnum.programName) String programName);

    // Load Group logging
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupShed(@Arg(ArgEnum.username) LiteYukonUser user, 
                                       @Arg(ArgEnum.loadGroupName) String loadGroupName, 
                                       @Arg(ArgEnum.shedDuration) int shedSeconds);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupShed(@Arg(ArgEnum.loadGroupName) String loadGroupName, 
                              @Arg(ArgEnum.shedDuration) int shedSeconds);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupRestore(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                          @Arg(ArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupRestore(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupEnabled(@Arg(ArgEnum.username) LiteYukonUser user, 
                                          @Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupEnabled(@Arg(ArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void threeTierLoadGroupDisabled(@Arg(ArgEnum.username) LiteYukonUser user, 
                                           @Arg(ArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.loadGroup")
    public void loadGroupDisabled(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr")
    public void seasonalControlHistoryReset(@Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.loadGroup")
    public void loadGroupCreated(@Arg(ArgEnum.loadGroupName) String loadGroupName, @Arg(ArgEnum.paoType) PaoType loadGroupType,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.loadGroup")
    public void loadGroupUpdated(@Arg(ArgEnum.loadGroupName) String loadGroupName, @Arg(ArgEnum.paoType) PaoType loadGroupType,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.loadGroup")
    public void loadGroupDeleted(@Arg(ArgEnum.loadGroupName) String loadGroupName, @Arg(ArgEnum.paoType) PaoType loadGroupType,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.scenario")
    public void scenarioCreated(@Arg(ArgEnum.scenarioName) String scenarioName,
            @Arg(ArgEnum.loadProgramNames) String loadProgramNames,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.scenario")
    public void scenarioUpdated(@Arg(ArgEnum.scenarioName) String scenarioName,
            @Arg(ArgEnum.loadProgramNames) String loadProgramNames,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.scenario")
    public void scenarioDeleted(@Arg(ArgEnum.scenarioName) String scenarioName,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.controlArea")
    public void controlAreaCreated(@Arg(ArgEnum.controlAreaName) String controlAreaName, @Arg(ArgEnum.triggerNames) String triggerNames,
            @Arg(ArgEnum.loadProgramNames) String loadProgramNames, @Arg(ArgEnum.startTime) String startTime, @Arg(ArgEnum.stopTime) String stopTime,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.controlArea")
    public void controlAreaUpdated(@Arg(ArgEnum.controlAreaName) String controlAreaName, @Arg(ArgEnum.triggerNames) String triggerNames,
            @Arg(ArgEnum.loadProgramNames) String loadProgramNames, @Arg(ArgEnum.startTime) String startTime, @Arg(ArgEnum.stopTime) String stopTime,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "dr.setup.controlArea")
    public void controlAreaDeleted(@Arg(ArgEnum.controlAreaName) String controlAreaName,
            @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.programConstraint")
    public void programConstraintCreated(@Arg(ArgEnum.programConstraintName) String programConstraintName,
                                         @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.programConstraint")
    public void programConstraintUpdated(@Arg(ArgEnum.programConstraintName) String programConstraintName,
                                         @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.programConstraint")
    public void programConstraintDeleted(@Arg(ArgEnum.programConstraintName) String programConstraintName,
                                         @Arg(ArgEnum.username) LiteYukonUser userName);
    

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.loadProgram")
    public void loadProgramCreated(@Arg(ArgEnum.programName) String programName,
                                   @Arg(ArgEnum.paoType) PaoType programType,
                                   @Arg(ArgEnum.programConstraintName) String programConstraintName,
                                   @Arg(ArgEnum.gearName) String gearNames,
                                   @Arg(ArgEnum.loadGroupName) String LoadGroupNames,
                                   @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.loadProgram")
    public void loadProgramUpdated(@Arg(ArgEnum.programName) String programName,
                                   @Arg(ArgEnum.paoType) PaoType programType,
                                   @Arg(ArgEnum.programConstraintName) String programConstraintName,
                                   @Arg(ArgEnum.gearName) String gearNames,
                                   @Arg(ArgEnum.loadGroupName) String LoadGroupNames,
                                   @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.loadProgram")
    public void loadProgramDeleted(@Arg(ArgEnum.programName) String programName,
                                   @Arg(ArgEnum.paoType) PaoType programType,
                                   @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.loadProgram")
    public void gearCreated(@Arg(ArgEnum.gearName) String gearName,
                            @Arg(ArgEnum.gearControlMethod) String gearControlMethod,
                            @Arg(ArgEnum.programName) String programName,
                            @Arg(ArgEnum.gearNumber) Integer gearNumber,
                            @Arg(ArgEnum.username) LiteYukonUser userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.setup.loadProgram")
    public void gearDeleted(@Arg(ArgEnum.gearName) String gearName,
                            @Arg(ArgEnum.gearControlMethod) String gearControlMethod,
                            @Arg(ArgEnum.programName) String programName,
                            @Arg(ArgEnum.gearNumber) Integer gearNumber,
                            @Arg(ArgEnum.username) LiteYukonUser userName);

}
