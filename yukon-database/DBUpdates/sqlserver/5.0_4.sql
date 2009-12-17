/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8159 */ 
UPDATE YukonServices 
SET ServiceClass = 'classpath:com/cannontech/services/optout/optOutContext.xml' 
WHERE ServiceId = 10;
/* End YUK-8159 */ 

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
