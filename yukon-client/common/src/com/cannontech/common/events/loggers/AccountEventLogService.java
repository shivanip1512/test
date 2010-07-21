package com.cannontech.common.events.loggers;

import org.joda.time.Instant;
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
    public void enrollmentAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void enrollmentAttemptedByConsumer(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void enrollmentAttemptedThroughWebServices(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void enrollmentEditAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void unenrollmentAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void unenrollmentAttemptedByConsumer(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void unenrollmentAttemptedThroughWebServices(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);
    
    // Enrollment Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void deviceEnrolled(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.enrollment")
    public void deviceUnenrolled(LiteYukonUser yukonUser, String accountNumber, String deviceName, String programName, String loadGroupName);
    
    
    // Opt Outs
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.optOut")
    public void optOutLimitIncreased(LiteYukonUser yukonUser, String accountNumber, String deviceName, int optOutsAdded);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.optOut")
    public void optOutLimitReset(LiteYukonUser yukonUser, String accountNumber, String deviceName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.optOut")
    public void optOutResent(LiteYukonUser yukonUser, String accountNumber, String deviceName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.optOut")
    public void deviceOptedOut(LiteYukonUser yukonUser, String accountNumber, String deviceName, 
                               Instant startDate, Instant stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="account.optOut")
    public void optOutCanceled(LiteYukonUser yukonUser, String accountNumber, String deviceName);

    
    
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
