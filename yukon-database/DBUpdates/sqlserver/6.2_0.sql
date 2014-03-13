/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12914 */
/* @error ignore-begin */
sp_rename 'RfnBroadcastEvent.EventSendTime', 'EventSentTime', 'COLUMN';
/* @error ignore-end */
/* End YUK-12914 */

/* Start YUK-12988 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10911;

DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10911;
/* End YUK-12988 */

/* Start YUK-12523 */
/* @error ignore-begin */
DROP TABLE YukonUserGroup_old;
DROP TABLE YukonUserRole;
/* @error ignore-end */
/* End YUK-12523 */

/* Start YUK-13117 */
UPDATE DeviceConfigCategory
SET CategoryType = 'mctDisconnectConfiguration'
WHERE CategoryType = 'disconnectConfiguration';

UPDATE DeviceConfigCategoryItem 
SET ItemValue = 'ARM' 
WHERE ItemValue = 'true' AND ItemName = 'reconnectButton';

UPDATE DeviceConfigCategoryItem 
SET ItemValue = 'IMMEDIATE' 
WHERE ItemValue = 'false' AND ItemName = 'reconnectButton';

UPDATE DeviceConfigCategoryItem 
SET ItemName = 'reconnectParam' 
WHERE ItemName = 'reconnectButton';
/* End YUK-13117 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAR-2014', 'Latest Update', 0, GETDATE());*/