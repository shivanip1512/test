/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-17578 */
/* Small update to BehaviorReportValue */
UPDATE BehaviorReportValue BRV
SET BRV.Value = 'INSTANTANEOUS_KW'
WHERE BRV.BehaviorReportId IN (
    SELECT BR.BehaviorReportId 
    FROM 
        BehaviorReport BR 
    JOIN 
        YukonPAObject Y ON BR.DeviceID = Y.PAObjectID
    WHERE Y.Type IN ('RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4') )
AND BRV.Value='DEMAND';

/* Update all RFN Data Streaming Device Behaviors with RFN-430SL1+ to use INSTANTANEOUS_KW instead of DEMAND */
UPDATE BehaviorValue BV
SET 
    BV.Value='INSTANTANEOUS_KW'
WHERE
    BV.BehaviorId IN (
        SELECT DISTINCT
            BV.BehaviorId
        FROM BehaviorValue BV
            JOIN Behavior B ON BV.BehaviorId=B.BehaviorId
            JOIN DeviceBehaviorMap DBM ON B.BehaviorId=DBM.BehaviorId
            JOIN YukonPAObject Y ON DBM.DeviceId=Y.PAObjectID
        WHERE 
            B.BehaviorType='DATA_STREAMING'
            AND Y.Type IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4') )
AND
    BV.Value='DEMAND';

/* Update all rfnChannelConfiguration categories assigned to RFN-430SL1+ to use INSTANTANEOUS_KW instead of DEMAND */
UPDATE DeviceConfigCategoryItem DCCI
SET ItemValue='INSTANTANEOUS_KW'
WHERE DCCI.DeviceConfigCategoryId IN (
        SELECT DISTINCT DCC.DeviceConfigCategoryId 
        FROM
            DeviceConfigCategory DCC
            JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId=DCCM.DeviceConfigCategoryId
            JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId=DC.DeviceConfigurationID
            JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID=DCDT.DeviceConfigurationId
        WHERE
            DCC.CategoryType='rfnChannelConfiguration'
            AND DCDT.PaoType IN ('RFN-430SL1','RFN-430SL2','RFN-430SL3','RFN-430SL4') )
    AND DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue='DEMAND';
/* End YUK-17578 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '28-NOV-2017', 'Latest Update', 4, SYSDATE);*/