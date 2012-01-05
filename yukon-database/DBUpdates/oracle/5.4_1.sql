/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10547 */ 
ALTER TABLE LMProgramDirect ADD NotifyAdjust NUMBER(18,0);
UPDATE LMProgramDirect SET NotifyAdjust = -1;
ALTER TABLE LMProgramDirect MODIFY NotifyAdjust NUMBER(18,0) NOT NULL;
/* End YUK-10547 */

/* Start YUK-10536 */ 
ALTER TABLE ZBGateway
MODIFY FirmwareVersion VARCHAR(255) NULL;
/* End YUK-10536 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/
