package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface RfnDeviceEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rfn")
    public void createdNewDeviceAutomatically(@Arg(ArgEnum.rfnId) RfnIdentifier rfnIdentifier, 
            String templateName,
            @Arg(ArgEnum.paoName) String paoName);

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rfn")
    public void receivedDataForUnkownDeviceTemplate(String templateName);

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rfn")
    public void unableToCreateDeviceFromTemplate(String templateName, String sensorManufacturer,
            String sensorModel, String sensorSerialNumber);    
}