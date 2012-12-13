package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ZigbeeEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.config")
    public void zigbeeDeviceCommission(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.config")
    public void zigbeeDeviceCommissioned(@Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.config")
    public void zigbeeDeviceDecommission(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.config")
    public void zigbeeDeviceDecommissioned(@Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.status")
    public void zigbeeDeviceRefresh(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.status")
    public void zigbeeDeviceRefreshed(@Arg(ArgEnum.paoName) String paoName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.actions")
    public void zigbeeSendText(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.message)String message,
                                                          @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.actions")
    public void zigbeeSentText(@Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.message)String message);
        
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.config")
    public void zigbeeDeviceAssign(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName,
                                                          @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.config")
    public void zigbeeDeviceAssigned(@Arg(ArgEnum.paoName) String paoName,
                                                   @Arg(ArgEnum.gatewayName) String gatewayName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.config")
    public void zigbeeDeviceUnassign(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName,
                                                          @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.config")
    public void zigbeeDeviceUnassigned(@Arg(ArgEnum.paoName) String paoName,
                                                      @Arg(ArgEnum.gatewayName) String gatewayName);
}
