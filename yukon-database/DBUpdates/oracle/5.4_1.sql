/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10547 */ 
ALTER TABLE LMProgramDirect ADD NotifyAdjust NUMBER;
UPDATE LMProgramDirect SET NotifyAdjust = -1;
ALTER TABLE LMProgramDirect MODIFY NotifyAdjust NUMBER NOT NULL;
/* End YUK-10547 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/
