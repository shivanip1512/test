package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface SystemEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginWeb(@Arg(value="username") LiteYukonUser user, 
                         @Arg(value="remoteAddress") String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginClient(@Arg(value="username") LiteYukonUser user,
                            @Arg(value="remoteAddress") String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginInboundVoice(@Arg(value="username") LiteYukonUser user,
                                  @Arg(value="remoteAddress") String remoteAddress);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.login")
    public void loginOutboundVoice(@Arg(value="username") LiteYukonUser user,
                                   @Arg(value="remoteAddress") String remoteAddress);    
    

}
