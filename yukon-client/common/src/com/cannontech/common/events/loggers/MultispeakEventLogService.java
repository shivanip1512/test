package com.cannontech.common.events.loggers;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface MultispeakEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void methodInvoked(@Arg(ArgEnum.mspMethod) String mspMethod, 
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void returnObjects(int numReturned, int objectsRemaining, String type, String lastSent,
            @Arg(ArgEnum.mspMethod) String mspMethod, 
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void returnObject(String type, 
            @Arg(ArgEnum.totalCount) int mspMeterCount,
            @Arg(ArgEnum.mspMethod) String mspMethod, 
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void returnObject(String type, 
            @Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.mspMethod) String mspMethod, 
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void meterCreated(@Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void meterFound(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void meterNotFound(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void meterNotFoundByPaoName(@Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void substationNotFound(@Arg(ArgEnum.substationName) String substationName,
            @Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void routeNotFound(@Arg(ArgEnum.substationName)String substationName,
            @Arg(ArgEnum.routeName) String routeName,
            @Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void objectNotFoundByVendor(String objectId,
            String requestedMethod,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void addMetersToGroup(int numberAdded, 
            @Arg(ArgEnum.deviceGroup) String deviceGroup,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void removeMetersFromGroup(int numberAdded, 
            @Arg(ArgEnum.deviceGroup) String deviceGroup,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void addMeterToGroup(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.deviceGroup) String deviceGroup,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void removeMeterFromGroup(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.deviceGroup) String deviceGroup,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateMeterReadRequest(int numberRequested,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateMeterRead(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            String transactionId,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateCDRequest(int numberRequested,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateCD(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            String loadActionCode,
            String transactionId,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateDemandResetRequest(int totalRequested, int totalSent, int totalInvalid, int totalUnsupported,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateDemandReset(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            String transactionId,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateODEventRequest(int numberRequested,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void initiateODEvent(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            String transactionId,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void routeUpdated(@Arg(ArgEnum.routeName) String routeName,
            @Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void routeUpdatedByDiscovery(@Arg(ArgEnum.routeName) String routeName,
            @Arg(ArgEnum.meterNumber) String meterNumber,
            String routeNameList,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void paoNameUpdated(@Arg(ArgEnum.paoName) String oldPaoName,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void serialNumberOrAddressUpdated(@Arg(ArgEnum.serialNumber) String oldSerialNumberOrAddress,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void meterNumberUpdated(@Arg(ArgEnum.meterNumber) String oldMeterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void deviceTypeUpdated(@Arg(ArgEnum.paoType) PaoType oldPaoType,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void enableDevice(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void disableDevice(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void removeDevice(@Arg(ArgEnum.meterNumber) String meterNumber,
            @Arg(ArgEnum.meterDescription) YukonMeter meter,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void notificationResponse(@Arg(ArgEnum.mspMethod) String mspMethod,
            String transactionID, 
            @Arg(ArgEnum.meterNumber) String meterNumber,
            String additionalInfo,
            Integer numberErrors,
            String responseUrl);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="multispeak")
    public void errorObject(@Arg(ArgEnum.message) String errorMessage,
            @Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.mspVendor) String mspVendor);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "multispeak")
    public void invalidSubstationName(@Arg(ArgEnum.mspMethod) String mspMethod,
            @Arg(ArgEnum.message) String errorMessage, 
            @Arg(ArgEnum.mspVendor) String mspVendor);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "multispeak")
    public void drNotificationReponse(@Arg(ArgEnum.mspMethod) String mspMethod, 
            @Arg(ArgEnum.mspVendor) String mspVendor, 
            @Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.mspTransactionId) String transactionID,
            String messageType, 
            Integer numberErrors,
            String responseUrl);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "multispeak")
    public void drProgramStatusNotificationReponse(@Arg(ArgEnum.mspMethod) String mspMethod, 
                                                   @Arg(ArgEnum.mspVendor) String mspVendor,
                                                   @Arg(ArgEnum.programName) String programName, 
                                                   @Arg(ArgEnum.mspTransactionId) String mspTransactionID, 
                                                   String messageType, 
                                                   Integer correlationId,
                                                   Integer numberErrors,
                                                   String responseUrl);
}
