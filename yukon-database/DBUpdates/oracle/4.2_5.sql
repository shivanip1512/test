/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7299 */
DELETE FROM YukonGroupRole WHERE RolePropertyId = -20017;
DELETE FROM YukonUserRole WHERE RolePropertyId = -20017;
DELETE FROM YukonRoleProperty WHERE RolePropertyId = -20017;
/* End YUK-7299 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
