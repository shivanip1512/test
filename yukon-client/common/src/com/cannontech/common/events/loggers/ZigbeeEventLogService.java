package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ZigbeeEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.config")
    public void zigbeeDeviceCommissionByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.config")
    public void zigbeeDeviceCommissioned(@Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.config")
    public void zigbeeDeviceDecommissionByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.config")
    public void zigbeeDeviceDecommissioned(@Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.status")
    public void zigbeeDeviceRefreshByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.status")
    public void zigbeeDeviceRefreshed(@Arg(ArgEnum.paoName) String paoName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.commands.actions")
    public void zigbeeSendTextByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.message)String message);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.commands.actions")
    public void zigbeeSentText(@Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.message)String message);
        
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.config")
    public void zigbeeDeviceAssignByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.config")
    public void zigbeeDeviceAssigned(@Arg(ArgEnum.paoName) String paoName,
                                                   @Arg(ArgEnum.gatewayName) String gatewayName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.config")
    public void zigbeeDeviceUnassignByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.config")
    public void zigbeeDeviceUnassigned(@Arg(ArgEnum.paoName) String paoName,
                                                      @Arg(ArgEnum.gatewayName) String gatewayName);
}
