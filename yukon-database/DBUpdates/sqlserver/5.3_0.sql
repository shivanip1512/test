/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9319 */
ALTER TABLE YukonServices 
DROP COLUMN ParamNames; 

ALTER TABLE YukonServices 
DROP COLUMN ParamValues;
/* End YUK-9319 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
