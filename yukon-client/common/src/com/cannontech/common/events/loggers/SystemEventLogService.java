package com.cannontech.common.events.loggers;

import org.joda.time.Instant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
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
    
    
    // Username service level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void usernameChanged(@Arg(ArgEnum.username) LiteYukonUser user, 
                                @Arg(ArgEnum.username) String oldUsername,
                                @Arg(ArgEnum.username) String newUsername);
    
/* Logging in */    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginWeb(@Arg(ArgEnum.username) LiteYukonUser user, 
                         @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginClient(@Arg(ArgEnum.username) LiteYukonUser user,
                            @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginInboundVoice(@Arg(ArgEnum.username) LiteYukonUser user,
                                  @Arg(ArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginOutboundVoice(@Arg(ArgEnum.username) LiteYukonUser user,
                                   @Arg(ArgEnum.remoteAddress) String remoteAddress);    
    
    /* System Admin */
    /* Maintenance */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void rphDeleteDuplicates(int rowsDeleted, Instant start, Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void rphDeleteDanglingEntries(int rowsDeleted, Instant start, Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void systemLogDeleteDanglingEntries(int rowsDeleted, Instant start, Instant finish);
    
    /* System Admin */
    /* Configuration */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.configuration")
    public void globalSettingChanged(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.globalSettingType) GlobalSettingType type, @Arg(ArgEnum.globalSettingValue) String value);
}
