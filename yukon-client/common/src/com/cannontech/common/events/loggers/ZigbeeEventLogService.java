package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ZigbeeEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceCommissionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceCommissioned(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceDecommissionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceDecommissioned(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.action")
    public void zigbeeDeviceRefreshAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.action")
    public void zigbeeDeviceRefreshed(@Arg(ArgEnum.deviceName) String deviceName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.action")
    public void zigbeeSendTextAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.action")
    public void zigbeeSentText(@Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceAssignAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.deviceName) String deviceName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceAssigned(@Arg(ArgEnum.deviceName) String deviceName,
                                                   @Arg(ArgEnum.gatewayName) String gatewayName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.config")
    public void zigbeeDeviceUnassignAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                          @Arg(ArgEnum.deviceName) String deviceName,
                                                          @Arg(ArgEnum.gatewayName) String gatewayName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.config")
    public void zigbeeDeviceUnassigned(@Arg(ArgEnum.deviceName) String deviceName,
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
    public void zigbeeSendSEPControlAttempted(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeeSentSEPControl(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="zigbee.system")
    public void zigbeeSendSEPRestoreAttempted(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeeSentSEPRestore(@Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="zigbee.system")
    public void zigbeeDeviceLoadGroupAddressingSent(@Arg(ArgEnum.deviceName) String deviceName);
    
}
