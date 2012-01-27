/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10547 */ 
ALTER TABLE LMProgramDirect ADD NotifyAdjust NUMERIC;
GO
UPDATE LMProgramDirect SET NotifyAdjust = -1;
GO
ALTER TABLE LMProgramDirect ALTER COLUMN NotifyAdjust NUMERIC NOT NULL;
GO
/* End YUK-10547 */

/* Start YUK-10536 */
/* @error ignore-begin */
ALTER TABLE ZBGateway
ALTER COLUMN FirmwareVersion VARCHAR(255) NULL;
GO
/* @error ignore-end */
/* End YUK-10536 */

/* Start YUK-10565 */
INSERT INTO YukonRoleProperty VALUES (-20219, -202, 'Meter Events', 'false', 'Controls access to Meter Events.');
/* End YUK-10565 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
