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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
