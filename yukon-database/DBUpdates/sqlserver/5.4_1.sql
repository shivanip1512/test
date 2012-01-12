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
ALTER COLUMN FirmwareVersion VARCHAR2(255) NULL;
GO
/* @error ignore-end */
/* End YUK-10536 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
