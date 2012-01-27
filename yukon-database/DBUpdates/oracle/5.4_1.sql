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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/
