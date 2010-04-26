/******************************************/ 
/**** Oracle DBupdates                 ****/ 
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
INSERT INTO CTIDatabase VALUES ('5.1', 'Matt K', '26-APR-2010', 'Latest Update', 5);
