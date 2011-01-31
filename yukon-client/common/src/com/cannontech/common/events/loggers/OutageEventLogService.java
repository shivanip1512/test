package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface OutageEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.mspHandler")
    public void mspMessageSentToVendor(String source, 
                                       String eventType, 
                                       String objectId,
                                       String deviceType,
                                       String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents")
    public void outageEventGenerated(String eventType,
                                     Date eventDateTime,
                                     String deviceType,
                                     String objectId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.statusPointMonitor")
    public void statusPointMonitorCreated(int statusPointMonitorId, 
                                    String statusPointMonitorName, 
                                    String groupName,
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.statusPointMonitor")
    public void statusPointMonitorDeleted(int statusPointMonitorId, 
                                    String statusPointMonitorName, 
                                    String groupName,
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.statusPointMonitor")
    public void statusPointMonitorUpdated(int statusPointMonitorId, 
                                    String statusPointMonitorName, 
                                    String groupName,
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.statusPointMonitor")
    public void statusPointMonitorEnableDisable(int statusPointMonitorId, 
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
    //Porter Response Monitor
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.porterResponseMonitor")
    public void porterResponseMonitorCreated(int monitorId, 
                                    String monitorName, 
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.porterResponseMonitor")
    public void porterResponseMonitorDeleted(int monitorId, 
                                    String monitorName, 
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.porterResponseMonitor")
    public void porterResponseMonitorUpdated(int monitorId, 
                                    String monitorName, 
                                    String attribute,
                                    String stateGroup,
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvents.porterResponseMonitor")
    public void porterResponseMonitorEnableDisable(int monitorId, 
                                    String evaluatorStatus,
                                    @Arg(ArgEnum.username) LiteYukonUser yukonUser);
}