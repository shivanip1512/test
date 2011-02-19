package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface RfnMeteringEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rfn")
    public void createdNewMeterAutomatically(@Arg(ArgEnum.paoId) long paoId, String rfnIdentifier, String templateName, String meterNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rfn")
    public void receivedDataForUnkownMeterTemplate(String templateName);

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rfn")
    public void unableToCreateMeterFromTemplate(String templateName, String sensorManufacturer,
            String sensorModel, String sensorSerialNumber);
    

}
