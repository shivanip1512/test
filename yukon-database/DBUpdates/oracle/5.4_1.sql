/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10547 */ 
ALTER TABLE LMProgramDirect ADD NotifyAdjust NUMBER;
UPDATE LMProgramDirect SET NotifyAdjust = -1;
ALTER TABLE LMProgramDirect MODIFY NotifyAdjust NUMBER NOT NULL;
/* End YUK-10547 */

/* Start YUK-10536 */
/* @error ignore-begin */
ALTER TABLE ZBGateway
MODIFY FirmwareVersion VARCHAR2(255) NULL;
/* @error ignore-end */
/* End YUK-10536 */

/* Start YUK-10565 */
INSERT INTO YukonRoleProperty VALUES (-20219, -202, 'Meter Events', 'false', 'Controls access to Meter Events.');
/* End YUK-10565 */

/* Start YUK-10462 */
INSERT INTO DeviceGroup VALUES (30, 'Device Configs', 15, 'NOEDIT_NOMOD', 'DEVICECONFIG');
/* End YUK-10462 */

/* Start YUK-10600 */
UPDATE YukonRoleProperty 
SET KeyName = 'Opt Out Force All Devices', Description = 'Controls access to select individual devices or forces all device selection when opting out. When true, individual device selection is unavailable and all devices are forced to opt out.' 
WHERE RolePropertyId = -40201;
/* End YUK-10600 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/
