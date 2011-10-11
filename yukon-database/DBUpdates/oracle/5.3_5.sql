/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10174 */ 
DELETE FROM YukonServices 
    WHERE ServiceID = 18 AND ServiceName = 'CymDISTMessageListener';
/* End YUK-10174 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
