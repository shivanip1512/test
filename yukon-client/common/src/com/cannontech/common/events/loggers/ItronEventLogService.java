package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface ItronEventLogService {
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addHANDevice(@Arg(ArgEnum.deviceName) String displayName, 
                             @Arg(ArgEnum.macAddress) String macAddress,
                             @Arg(ArgEnum.username) String userName);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addHANDeviceToServicePoint(@Arg(ArgEnum.accountNumber) String accountNumber, 
                                           @Arg(ArgEnum.macAddress) String macAddress, 
                                           @Arg(ArgEnum.username) String userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void removeHANDeviceFromServicePoint(@Arg(ArgEnum.macAddress) String macAddress);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addServicePoint(@Arg(ArgEnum.accountNumber) String accountNumber,
                                @Arg(ArgEnum.username) String userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addGroup(@Arg(ArgEnum.loadGroupName) String loadGroupName, 
                         Long groupId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addProgram(@Arg(ArgEnum.programName) String programName, 
                           Long programId);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void setServicePointEnrollment(@Arg(ArgEnum.customerId) int customerId,
                                          String itronGroupIds);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addMacAddressToGroup(@Arg(ArgEnum.macAddress) int macAddress,
                                     @Arg(ArgEnum.loadGroupName) String groupName);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void getGroupIdFromItron(@Arg(ArgEnum.deviceName) String device,
                                    @Arg(ArgEnum.loadGroupName) String groupName);
    
    
}
