/******************************************/
/**** Oracle DBupdates                 ****/
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

/* Start YUK-7236 */
DELETE FROM YukonGroupRole WHERE RolePropertyId = -1113;
DELETE FROM YukonUserRole WHERE RolePropertyId = -1113;
DELETE FROM YukonRoleProperty WHERE RolePropertyId = -1113;
/* End YUK-7236 */

/* Start YUK-7321 */
CREATE INDEX INDX_LMProgEvent_AcctId_ProgId ON LMProgramEvent (
    AccountId ASC,
    ProgramId ASC
); 
/* End YUK-7321 */

/* Start YUK-7320 */
CREATE INDEX INDX_LMHardBase_ManSerNum_FB ON LMHardwareBase (
    UPPER(ManufacturerSerialNumber)
);
/* End YUK-7320 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '17-APR-2009', 'Latest Update', 5);
