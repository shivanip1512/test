/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7299 */
DELETE FROM YukonGroupRole WHERE RolePropertyId = -20017;
DELETE FROM YukonUserRole WHERE RolePropertyId = -20017;
DELETE FROM YukonRoleProperty WHERE RolePropertyId = -20017;
/* End YUK-7299 */

/* Start YUK-7238 */
UPDATE YukonRole 
SET RoleName = 'Tabular Data Console', RoleDescription = 'Access to the Yukon Tabular Data Console (TDC) application'
WHERE RoleId = -101;
/* End YUK-7238 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
