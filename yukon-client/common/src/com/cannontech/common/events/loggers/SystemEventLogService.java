package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface SystemEventLogService {

/* Usernames */
    // Username
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginPasswordChangeAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginUsernameChangeAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                                                       @Arg(EventLogArgEnum.username) String newUsername);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.login")
    public void loginChangeAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                                               @Arg(EventLogArgEnum.username) String username);
    
    
    // Username service level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void usernameChanged(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                                @Arg(EventLogArgEnum.username) String oldUsername,
                                @Arg(EventLogArgEnum.username) String newUsername);
    
/* Logging in */    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginWeb(@Arg(EventLogArgEnum.username) LiteYukonUser user, 
                         @Arg(EventLogArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginClient(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                            @Arg(EventLogArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginInboundVoice(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                  @Arg(EventLogArgEnum.remoteAddress) String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginOutboundVoice(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                   @Arg(EventLogArgEnum.remoteAddress) String remoteAddress);    
    

}
