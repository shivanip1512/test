/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8627 */
UPDATE YukonRoleProperty
SET DefaultValue = 'false'
WHERE RolePropertyId = -20214;
/* End YUK-8627 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
