package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface StatusPointMonitorEventLogService {
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.monitors")
    public void statusPointMonitorCreated(int statusPointMonitorId, 
                                    String statusPointMonitorName, 
                                    String groupName,
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.monitors")
    public void statusPointMonitorDeleted(int statusPointMonitorId, 
                                    String statusPointMonitorName, 
                                    String groupName,
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.monitors")
    public void statusPointMonitorUpdated(int statusPointMonitorId, 
                                    String statusPointMonitorName, 
                                    String groupName,
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.monitors")
    public void statusPointMonitorEnableDisable(int statusPointMonitorId, 
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
}