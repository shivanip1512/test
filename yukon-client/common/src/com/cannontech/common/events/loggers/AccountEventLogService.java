package com.cannontech.common.events.loggers;

import org.springframework.stereotype.Service;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

@Service
public interface AccountEventLogService {

    // Accounts
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account")
    public void accountAdded(LiteYukonUser yukonUser, String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account")
    public void accountUpdated(LiteYukonUser yukonUser, String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account")
    public void accountDeleted(LiteYukonUser yukonUser, String accountNumber);

    // Enrollment
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void enrollmentAdded(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void enrollmentRemoved(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);
    
    // Appliance
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.appliance")
    public void applianceAdded(LiteYukonUser yukonUser, String accountNumber, 
                               String applianceType, String deviceName, String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.appliance")
    public void applianceUpdated(LiteYukonUser yukonUser, String accountNumber, 
                                 String applianceType, String deviceName, String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.appliance")
    public void applianceDeleted(LiteYukonUser yukonUser, String accountNumber, 
                                 String applianceType, String deviceName, String programName);

    
    
    
}
