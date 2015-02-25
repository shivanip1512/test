package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;

public interface EstimatedLoadEventLogService {
    
    @YukonEventLog(category="estimatedLoad")
    public void applianceCategoryNotFound(@Arg(ArgEnum.programName) String programName);
    
    @YukonEventLog(category="estimatedLoad")
    public void applianceCategoryInfoNotFound(@Arg(ArgEnum.programName) String programName,
                                              @Arg(ArgEnum.applianceCategoryName) String applianceCategoryName);
    
    @YukonEventLog(category="estimatedLoad")
    public void gearNotFound(@Arg(ArgEnum.programName) String programName,
                             Integer gearNumber);
    
    @YukonEventLog(category="estimatedLoad")
    public void inputOutOfRange(String formulaName,
                                String componentType,
                                String componentName,
                                String inputValue,
                                String validMin,
                                String validMax);
    
    @YukonEventLog(category="estimatedLoad")
    public void inputValueNotFound(String formulaName, 
                                   @Arg(ArgEnum.programName) String programName);
    
    @YukonEventLog(category="estimatedLoad")
    public void lmDataNotFound(@Arg(ArgEnum.programName) String programName);
    
    @YukonEventLog(category="estimatedLoad")
    public void notConnectedToLmServer();
    
    @YukonEventLog(category="estimatedLoad")
    public void noApplianceCategoryFormula(@Arg(ArgEnum.applianceCategoryName) String applianceCategoryName,
                                @Arg(ArgEnum.programName) String programName);
    
    @YukonEventLog(category="estimatedLoad")
    public void noGearFormula(@Arg(ArgEnum.gearName) String gearName,
                              @Arg(ArgEnum.programName) String programName);
    
    @YukonEventLog(category="estimatedLoad")
    public void unknownError(@Arg(ArgEnum.programName) String programName);
}
