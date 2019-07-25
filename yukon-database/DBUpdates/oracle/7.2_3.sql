/******************************************/ 
/****     Oracle DBupdates             ****/ 
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

INSERT INTO DBUpdates VALUES ('YUK-20294', '7.2.3', SYSDATE);
/* @end YUK-20294 */

/* @start YUK-20257 */
UPDATE DeviceConfigCategoryItem DCCI
SET ItemValue = 'DELIVERED_DEMAND'
WHERE DCCI.DeviceConfigCategoryId IN (
    SELECT DISTINCT DCC.DeviceConfigCategoryId 
    FROM DeviceConfigCategory DCC
    JOIN DeviceConfigCategoryMap DCCM ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId
    JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationID
    JOIN DeviceConfigDeviceTypes DCDT ON DC.DeviceConfigurationID = DCDT.DeviceConfigurationId
    WHERE DCC.CategoryType = 'rfnChannelConfiguration'
    AND DCDT.PaoType IN ('RFN-410CL', 'RFN-420CL') )
AND DCCI.ItemName LIKE 'enabledChannels%attribute'
AND DCCI.ItemValue = 'DEMAND';

INSERT INTO DBUpdates VALUES ('YUK-20257', '7.2.3', SYSDATE);
/* @end YUK-20257 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '20-MAY-2019', 'Latest Update', 3, SYSDATE);*/
