package com.cannontech.common.events.loggers;

import org.joda.time.Instant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;

public interface SystemEventLogService {

/* Usernames */
    // Username
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginPasswordChangeAttempted(@Arg(ArgEnum.username) LiteYukonUser user, 
                                                       @Arg(ArgEnum.eventSource) EventSource eventSource);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginUsernameChangeAttempted(@Arg(ArgEnum.username) LiteYukonUser user, 
                                                       @Arg(ArgEnum.username) String newUsername, 
                                                       @Arg(ArgEnum.eventSource) EventSource eventSource);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginChangeAttempted(@Arg(ArgEnum.username) LiteYukonUser user, 
                                       @Arg(ArgEnum.username) String username, 
                                       @Arg(ArgEnum.eventSource) EventSource eventSource);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void passwordRequestAttempted(@Arg(ArgEnum.username) String username,
                                         @Arg(ArgEnum.email) String email,
                                         @Arg(ArgEnum.accountNumber) String accountNumber,
                                         @Arg(ArgEnum.eventSource) EventSource eventSource);
    
    
    // Username service level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void usernameChanged(@Arg(ArgEnum.username) LiteYukonUser user, 
                                @Arg(ArgEnum.username) String oldUsername,
                                @Arg(ArgEnum.username) String newUsername);
    
    /* Logging in */    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginWeb(@Arg(ArgEnum.username) LiteYukonUser user, 
                         @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.login")
    public void loginWebFailed(@Arg(ArgEnum.username) String user, @Arg(ArgEnum.remoteAddress) String remoteAddress,
            @Arg(ArgEnum.message)BadAuthenticationException.Type exceptionType);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginClientFailed(@Arg(ArgEnum.username) String user, 
                         @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginOutboundVoiceFailed(@Arg(ArgEnum.username) String user, 
                         @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginClient(@Arg(ArgEnum.username) LiteYukonUser user,
                            @Arg(ArgEnum.remoteAddress) String remoteAddress);
        
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginOutboundVoice(@Arg(ArgEnum.username) LiteYukonUser user,
                                   @Arg(ArgEnum.remoteAddress) String remoteAddress);    
        
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginConsumerAttempted(@Arg(ArgEnum.username) String username,
                                       @Arg(ArgEnum.eventSource) EventSource eventSource);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginConsumer(@Arg(ArgEnum.username) LiteYukonUser user,
                              @Arg(ArgEnum.eventSource) EventSource eventSource);
    
    /* System Admin */
    /* Maintenance */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void rphDeleteDuplicates(int rowsDeleted, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void rphDeleteDanglingEntries(int rowsDeleted, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void systemLogDeleteDanglingEntries(int rowsDeleted, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void systemLogWeatherDataUpdate(int weatherLocationsUpdated, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void smartIndexMaintenance(@Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);

    /* System Admin */
    /* Configuration */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.configuration")
    public void globalSettingChanged(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.globalSettingType) GlobalSettingType type, @Arg(ArgEnum.globalSettingValue) String value);
}
