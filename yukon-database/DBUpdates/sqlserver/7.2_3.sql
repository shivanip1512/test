/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-20294 */
UPDATE GlobalSetting 
SET Value = 'NONE'
WHERE Name IN ('LDAP_SSL_ENABLED', 'AD_SSL_ENABLED') 
AND Value = '0';

UPDATE GlobalSetting 
SET Value = 'TLS'
WHERE Name IN ('LDAP_SSL_ENABLED', 'AD_SSL_ENABLED') 
AND Value = '1';

INSERT INTO DBUpdates VALUES ('YUK-20294', '7.2.3', GETDATE());
/* @end YUK-20294 */

/* @start YUK-20257 */
UPDATE DCCI
SET ItemValue = 'DELIVERED_DEMAND'
FROM DeviceConfigCategoryItem DCCI
JOIN (
    SELECT DISTINCT DCC.DeviceConfigCategoryId 
    FROM DeviceConfigCategory DCC
    JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId
    JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationID
    JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID = DCDT.DeviceConfigurationId
    WHERE DCC.CategoryType = 'rfnChannelConfiguration'
    AND DCDT.PaoType IN ('RFN-410CL', 'RFN-420CL')
) RFNDC ON DCCI.DeviceConfigCategoryId = RFNDC.DeviceConfigCategoryId
WHERE DCCI.ItemName LIKE 'enabledChannels%attribute'
AND DCCI.ItemValue = 'DEMAND';

INSERT INTO DBUpdates VALUES ('YUK-20257', '7.2.3', GETDATE());
/* @end YUK-20257 */

/* @start YUK-20314 */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT
    TS.StrategyId,
    'Maximum Delta Voltage' AS SettingName,
    '10' AS SettingValue,
    'MAX_DELTA' AS SettingType
FROM CCStrategyTargetSettings TS
JOIN CapControlStrategy CS ON TS.StrategyId = CS.StrategyID
WHERE CS.ControlUnits IN ('MULTI_VOLT', 'MULTI_VOLT_VAR', 'INTEGRATED_VOLT_VAR')
AND NOT EXISTS (
    SELECT 1
    FROM CCStrategyTargetSettings 
    WHERE StrategyId = TS.StrategyId 
    AND SettingType = 'MAX_DELTA' )
GROUP BY TS.StrategyId;
GO

UPDATE CCStrategyTargetSettings
SET SettingValue = '10'
WHERE SettingType = 'MAX_DELTA'
AND SettingValue = '0';

INSERT INTO DBUpdates VALUES ('YUK-20314', '7.2.3', GETDATE());
/* @end YUK-20314 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.2', '26-JUN-2019', 'Latest Update', 3, GETDATE());
