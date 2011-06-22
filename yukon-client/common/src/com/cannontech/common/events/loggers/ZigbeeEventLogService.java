package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ZigbeeEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceCommissionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceCommissioned(@Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceDecommissionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceDecommissioned(@Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.action")
    public void zigbeeDeviceRefreshAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.action")
    public void zigbeeDeviceRefreshed(@Arg(ArgEnum.paoName) String paoName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.action")
    public void zigbeeSendTextAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.message)String message);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.action")
    public void zigbeeSentText(@Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.message)String message);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.action")
    public void zigbeeSentManualAdjustment(@Arg(ArgEnum.paoName) String paoName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceAssignAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceAssigned(@Arg(ArgEnum.paoName) String paoName,
                                                   @Arg(ArgEnum.gatewayName) String gatewayName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceUnassignAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.paoName) String paoName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceUnassigned(@Arg(ArgEnum.paoName) String paoName,
                                                      @Arg(ArgEnum.gatewayName) String gatewayName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.system")
    public void zigbeeRefreshAllGatewaysAttempted();
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeeRefreshedAllGateways();
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.system")
    public void zigbeePollAllGatewaysAttempted();
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeePolledGateway(@Arg(ArgEnum.gatewayName) String gatewayName);
        
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.system")
    public void zigbeeSendSepControlAttempted(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeeSentSepControl(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.system")
    public void zigbeeSendSepRestoreAttempted(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeeSentSepRestore(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeeDeviceLoadGroupAddressingSent(@Arg(ArgEnum.paoName) String paoName);
    
}
