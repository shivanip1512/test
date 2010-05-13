/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8649 */
DELETE FROM YukonGroupRole WHERE RolePropertyId = -20101;
DELETE FROM YukonUserRole WHERE RolePropertyId = -20101;
DELETE FROM YukonRoleProperty WHERE RolePropertyId = -20101;
/* End YUK-8649 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.1', 'Matt K', '13-MAY-2010', 'Latest Update', 6);
