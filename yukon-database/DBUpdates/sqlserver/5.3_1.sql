/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9557 */
UPDATE CapControlStrategy 
SET ControlUnits = 'MULTI_VOLT_VAR' 
WHERE ControlUnits = 'Multi Volt Var'; 
/* End YUK-9557 */

/* Start YUK-9565 */
UPDATE State 
SET ForegroundColor = 3 
WHERE StateGroupId = -14 
  AND RawState = 1; 
UPDATE State 
SET ForegroundColor = 1 
WHERE StateGroupId = -14 
  AND RawState = 2;
/* End YUK-9565 */

/* Start YUK-9575 */
ALTER TABLE ServiceCompanyDesignationCode 
    DROP CONSTRAINT FK_SRVCODSGNTNCODES_SRVCO;
GO

ALTER TABLE ServiceCompanyDesignationCode 
    ADD CONSTRAINT FK_ServCompDesCode_ServComp FOREIGN KEY (ServiceCompanyId) 
        REFERENCES ServiceCompany (CompanyId) 
            ON DELETE CASCADE;
GO
/* End YUK-9575 */

/* Start YUK-9603 */
INSERT INTO YukonRoleProperty VALUES (-1120, -2, 'Allow Designation Codes', 'false', 'Toggles on or off the regional (usually zip) code option for service companies.') 

DELETE FROM YukonUserRole 
WHERE RolePropertyId = -20008; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -20008; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -20008;
/* End YUK-9603 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
