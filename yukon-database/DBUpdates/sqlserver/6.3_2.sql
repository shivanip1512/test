/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13917 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10803;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10803;
 
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10804;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10804;
 
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10808;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10808;
 
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10807;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10807;
/* End YUK-13917 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.3', '14-DEC-2014', 'Latest Update', 2, GETDATE());*/