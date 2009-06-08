/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7257 */
/* @error ignore-begin */
DROP INDEX LMThermostatSeasonEntry.INDX_LMThermSeaEnt_SeaId;
DROP INDEX LMThermostatSeasonEntry.INDX_LMThermSeaEntry_SeaId;
/* @error ignore-end */
CREATE INDEX INDX_LMThermSeaEntry_SeaId ON LMThermostatSeasonEntry (
   SeasonId ASC
);
/* End YUK-7257 */

/* Start YUK-7503 */
UPDATE FDRInterface 
SET PossibleDirections = 'Receive,Send' 
WHERE InterfaceId = 26;
/* End YUK-7503 */

/* Start YUK-7518 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
/* @error ignore-end */
/* End YUK-7518 */

/* Start YUK-7529 */
DROP VIEW CCOperationsASent_View;
DROP VIEW CCOperationsBConfirmed_View;
/* @error ignore-begin */
/* We want to use schema binding if possible.  This will give us a performance boost. */
SET NUMERIC_ROUNDABORT OFF;
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT ON;
SET QUOTED_IDENTIFIER, ANSI_NULLS ON;
GO
CREATE VIEW [dbo].[CCOperationsASent_View] with schemabinding
AS
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM dbo.CCEventLog
WHERE EventType = 1
AND ActionId > -1;
GO

SET NUMERIC_ROUNDABORT OFF;
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT ON;
SET QUOTED_IDENTIFIER, ANSI_NULLS ON;
GO
CREATE VIEW [dbo].[CCOperationsBConfirmed_View] with schemabinding
AS
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM dbo.CCEventLog
WHERE EventType = 0
AND ActionId > -1;
GO

CREATE VIEW CCOperationsASent_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM CCEventLog
WHERE EventType = 1
AND ActionId > -1;
GO

CREATE VIEW CCOperationsBConfirmed_View
AS
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM CCEventLog
WHERE EventType = 0
AND ActionId > -1;
GO
/* @error ignore-end */
/* End YUK-7529 */

/* Start YUK-7537 */
DELETE FROM EnergyCompanyOperatorLoginList
WHERE OperatorLoginId IN (SELECT DISTINCT ECOLL.OperatorLoginId 
                          FROM EnergyCompanyOperatorLoginList ECOLL, YukonUserGroup YUG, YukonGroupRole YGR, YukonRole YR
                          WHERE ECOLL.OperatorLoginId = YUG.UserId
                          AND YUG.GroupId = YGR.GroupId
                          AND YGR.RoleId = YR.RoleId
                          AND YR.Category = 'Consumer'
                          AND YR.RoleName = 'Residential Customer');
/* End YUK-7537 */

/* Start YUK-7561 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
/* @error ignore-end */
/* End YUK-7561 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
