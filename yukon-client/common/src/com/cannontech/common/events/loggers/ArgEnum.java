package com.cannontech.common.events.loggers;

/**
 * 
 * Some arguments tend to be used frequently, such as username, account number,
 * or paoId. Arguments that are likely to be used in other event log methods 
 * should be added to ArgEnum.java. For arguments in this enum, you should use
 * the \@Arg annotation. You do not, and should not, add every argument type to
 * ArgEnum! Only the ones that are likely to be reused."
 *
 */
public enum ArgEnum {
    accountNumber,
    action,
    apn,
    applianceCategoryName,
    applianceType,
    attributeId,
    attributeName,
    auto,
    baudRate,
    cancelledCount,
    cancelTime,
    changeId,
    commandRequestExecutionContextId, 
    commandRequestExecutionId, 
    commandRequestExecutionType,
    commandRequestString,
    commandScheduleId,
    contactName,
    controlAreaName,
    cron,
    customerId,
    dashboardName,
    deviceConfig,
    deviceLabel,
    deviceGroup,
    deviceName, 
    deviceRequestType,
    drEncryption,
    duration,
    email,
    endDate,
    energyCompanyId,
    energyCompanyName,
    energyCompanySettingType,
    energyCompanySettingValue,
    error,
    eventSource,
    eventType,
    failureCount,
    fileName, 
    frequency,
    gatewayName, //Zigbee gateway, not RF gateway
    gearControlMethod,
    gearName,
    gearNumber,
    globalSettingType,
    globalSettingValue,
    group,
    hardwareType,
    input,
    inventoryId,
    ipAddress,
    jobId,
    key,
    level,
    loadGroupIds,
    loadGroupName,
    loadProgramNames,
    logoutReason,
    macAddress,
    manual,
    media,
    message,
    meterDescription,
    meterName,
    meterNumber,
    mspMethod,
    mspTransactionId,
    mspVendor,
    name,
    notAttemptedCount,
    /** @deprecated use paoName instead */ @Deprecated paoId,
    paoName,
    paoType, 
    pointDate, 
    pointId, 
    pointName,
    pointOffset, 
    pointType, 
    pointValue,
    programConstraintName,
    programName, 
    remoteAddress,
    resultKey,
    rfnId,
    routeId,
    routeName,
    role,
    roleGroup,
    roleProperty,
    scenarioName, 
    scheduleName, 
    serialNumber,
    shedDuration,
    startDate,
    startTime,
    state,
    statistics,
    status,
    stopTime,
    substationName,
    successCount,
    syncId,
    syncIssueType,
    tagSet,
    taskName,
    thermostatLabel,
    triggerNames,
    type,
    totalCount,
    unsupportedCount,
    username,
    userGroup,
    warehouseName,
    widgetType,
    workOrderNumber,
    yukonService,
    serverIdentifier,
    ;
}
