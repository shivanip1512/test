package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface ItronEventLogService {
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addHANDevice(@Arg(ArgEnum.deviceName) String displayName, 
                             String macAddress,
                             @Arg(ArgEnum.username) String userName);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addHANDeviceToServicePoint(@Arg(ArgEnum.accountNumber) String accountNumber, 
                                           String macAddress, 
                                           @Arg(ArgEnum.username) String userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void removeHANDeviceFromServicePoint(String macAddress, 
                                                @Arg(ArgEnum.username) String userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addServicePoint(@Arg(ArgEnum.accountNumber) String accountNumber,
                                @Arg(ArgEnum.username) String userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addGroup(@Arg(ArgEnum.group) String groupName, 
                         Long groupId,
                         @Arg(ArgEnum.username) String userName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.itron")
    public void addProgram(@Arg(ArgEnum.programName) String programName, 
                           Long programId, 
                           @Arg(ArgEnum.username) String userName);
}
