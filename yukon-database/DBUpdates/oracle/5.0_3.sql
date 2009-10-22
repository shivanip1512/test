/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7940 */
UPDATE YukonRoleProperty
SET DefaultValue = 'false'
WHERE RolePropertyId = -21307;
/* End YUK-7940 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
