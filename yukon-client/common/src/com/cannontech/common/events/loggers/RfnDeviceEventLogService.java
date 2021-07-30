package com.cannontech.common.events.loggers;

import org.joda.time.Instant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.device.Rfn1200;

public interface RfnDeviceEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.rfn")
    public void createdNewDeviceAutomatically(@Arg(ArgEnum.rfnId) RfnIdentifier rfnIdentifier, 
            String templateName,
            @Arg(ArgEnum.paoName) String paoName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "system.rfn")
    public void receivedDataForUnkownDeviceTemplate(String templateName, @Arg(ArgEnum.serialNumber) String sensorSerialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rfn")
    public void unableToCreateDeviceFromTemplate(String templateName, String sensorManufacturer,
            String sensorModel, String sensorSerialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.ASYNCHRONOUS, category="system.rfn")
    public void outageEventReceived(String sensorSerialNumber, String eventType, String eventState,
            @Arg(ArgEnum.startDate) Instant eventStart, @Arg(ArgEnum.endDate) Instant eventEnd);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn")
    void rfn1200Created(@Arg(ArgEnum.rfnId) RfnIdentifier RfnIdentifier,
                 @Arg(ArgEnum.paoName) String paoName,
                 @Arg(ArgEnum.username) String username);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn")
    void rfn1200Updated(Rfn1200 oldRfn1200,
                 Rfn1200 newRfn1200,
                 @Arg(ArgEnum.username) String username);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn")
    void rfn1200Deleted(@Arg(ArgEnum.paoName) String paoName,
                 @Arg(ArgEnum.username) String username);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn")
    void apnChanged(@Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.rfnId) RfnIdentifier rfnIdentifier,  @Arg(ArgEnum.apn) String apn);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn")
    void modelUpdated(@Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.rfnId) RfnIdentifier rfnIdentifier, String oldModel, String newModel);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn")
    void modelAndPaoTypeUpdated(@Arg(ArgEnum.paoName) String paoName, @Arg(ArgEnum.rfnId) RfnIdentifier rfnIdentifier,
            String oldModel, PaoType oldType, String newModel, PaoType newType);
}