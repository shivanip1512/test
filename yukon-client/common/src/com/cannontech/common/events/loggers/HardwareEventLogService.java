package com.cannontech.common.events.loggers;

import org.springframework.stereotype.Service;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

@Service
public interface HardwareEventLogService {

    // Hardware
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareCreated(LiteYukonUser yukonUser, String deviceLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareAdded(LiteYukonUser yukonUser, String deviceLabel, String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareUpdated(LiteYukonUser yukonUser, String deviceLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareRemoved(LiteYukonUser yukonUser, String deviceLabel, String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareDeleted(LiteYukonUser yukonUser, String deviceLabel);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void serialNumberChanged(LiteYukonUser yukonUser, String oldSerialNumber, String newSerialNumber);
    
    
}
