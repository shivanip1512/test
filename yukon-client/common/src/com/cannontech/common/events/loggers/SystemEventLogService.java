package com.cannontech.common.events.loggers;

import org.joda.time.Instant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.DREncryption;
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
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.login")
    public void loginClientFailed(@Arg(ArgEnum.username) String user, @Arg(ArgEnum.remoteAddress) String remoteAddress,
            @Arg(ArgEnum.message) BadAuthenticationException.Type exceptionType);
    
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
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.logout")
    public void logoutWeb(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.remoteAddress) String remoteAddress, @Arg(ArgEnum.logoutReason) String logoutReason);
    
    /* System Admin */
    /* Maintenance */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void rphDeleteDuplicates(int rowsDeleted, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void rphDeleteDanglingEntries(int rowsDeleted, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void deletePointDataEntries(int rowsDeleted, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void systemLogDeleteDanglingEntries(int rowsDeleted, @Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void systemLogWeatherDataUpdate(@Arg(ArgEnum.name) String weatherLocationName, @Arg(ArgEnum.message) String errorMsg);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.maintenance")
    public void smartIndexMaintenance(@Arg(ArgEnum.startDate) Instant start, @Arg(ArgEnum.endDate) Instant finish);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance")
    public void maintenanceTaskEnabled(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.taskName) String taskName);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance")
    public void maintenanceTaskDisabled(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.taskName) String taskName);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance")
    public void maintenanceTaskSettingsUpdated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.taskName) String taskName);

    /* DR Reconciliation */
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance.reconciliation")
    public void outOfServiceMessageSent(@Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance.reconciliation")
    public void inServiceMessageSent(@Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance.reconciliation")
    public void messageSendingFailed(@Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.message) String error);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance.reconciliation")
    public void configMessageSent(@Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.maintenance.reconciliation")
    public void groupConflictLCRDetected(@Arg(ArgEnum.serialNumber) String serialNumber);

    /* System Admin */
    /* Configuration */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.configuration")
    public void globalSettingChanged(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.globalSettingType) GlobalSettingType type, @Arg(ArgEnum.globalSettingValue) String value);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.configuration")
    public void sensitiveGlobalSettingChanged(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.globalSettingType) GlobalSettingType type);
    
    /* Security */
    /* Honeywell Key Configuration */
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.configuration")
    public void importedKeyFile(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.drEncryption) DREncryption drEncryption);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.configuration")
    public void keyFileImportFailed(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.drEncryption) DREncryption drEncryption);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.configuration")
    public void newPublicKeyGenerated(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.drEncryption) DREncryption drEncryption);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.configuration")
    public void certificateGenerated(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.drEncryption) DREncryption drEncryption);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.configuration")
    public void certificateGenerationFailed(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.drEncryption) DREncryption drEncryption);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.bulkOperations")
    public void importStarted(@Arg(ArgEnum.username) LiteYukonUser user, String importType, String fileName);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.bulkOperations")
    public void importCompleted(String importType, String fileName, int successCount, int failureCount);

}
