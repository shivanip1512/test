/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14427 */
DELETE FROM ExtraPaoPointAssignment 
WHERE Attribute = 'KEEP_ALIVE_TIMER';
/* End YUK-14427 */

/* Start YUK-14460 */
ALTER TABLE PaoLocation
ADD LastChangedDate DATETIME;
GO

UPDATE PaoLocation
SET LastChangedDate = GETDATE();

ALTER TABLE PaoLocation
ALTER COLUMN LastChangedDate DATETIME NOT NULL;

ALTER TABLE PaoLocation
ADD Origin VARCHAR(64);
GO

UPDATE PaoLocation
SET Origin = 'MANUAL';

ALTER TABLE PaoLocation
ALTER COLUMN Origin VARCHAR(64) NOT NULL;
/* End YUK-14460 */

/* Start YUK-14377 */
DELETE FROM State 
WHERE StateGroupId = -17 
  AND RawState IN (16, 17, 18, 19, 20, 21);

UPDATE State
SET Text = 'Remote'
WHERE StateGroupId = -17
  AND RawState = 14;
/* End YUK-14377 */

/* Start YUK-14530 */
DELETE FROM YukonGroupRole 
WHERE RoleID IN (-107, -206);

DELETE FROM YukonRoleProperty 
WHERE RoleID IN (-107, -206);

DELETE FROM YukonRole 
WHERE RoleId IN (-107, -206);

DROP TABLE EsubDisplayIndex;
/* End YUK-14530 */

/* Start YUK-14537 */
/* @error ignore-begin */
ALTER TABLE InventoryConfigTask
ADD SendOutOfService CHAR(1);
GO

UPDATE InventoryConfigTask
SET SendOutOfService = 'N'
WHERE SendOutOfService NOT LIKE 'Y'
   OR SendOutOfService IS NULL;

ALTER TABLE InventoryConfigTask
ALTER COLUMN SendOutOfService CHAR(1) NOT NULL;
GO
/* @error ignore-end */
/* End YUK-14537 */

/* Start YUK-14474 */
DELETE FROM GlobalSetting 
WHERE Name = 'WEB_LOGO_URL';
/* End YUK-14474 */

/* Start YUK-14433 */
/* @error warn-once */
/* @start-block */
IF 0 < (SELECT COUNT(*)
        FROM YukonServices YS
        WHERE YS.ServiceID = 3)
BEGIN
    RAISERROR('Yukon Calc Historical Service is enabled. This service must be uninstalled and Yukon Calc-Logic Service used instead.', 16, 1);
END;
/* @end-block */
DELETE FROM YukonServices WHERE ServiceId = 3 OR ServiceId = -3;
/* End YUK-14433 */

/* Start YUK-14581 */
/* @error warn-once */
/* @start-block */
IF 0 < (SELECT COUNT(EnergyCompanyId)
        FROM EnergyCompany ec
        WHERE NOT EXISTS (
            SELECT 1
            FROM YukonUser yu
            JOIN UserGroup ug                        ON yu.UserGroupId = ug.UserGroupId
            JOIN UserGroupToYukonGroupMapping ugtygm ON ug.UserGroupId = ugtygm.UserGroupId
            JOIN YukonGroup yg                       ON ugtygm.GroupId = yg.GroupID
            JOIN YukonGroupRole ygr                  ON yg.GroupId = ygr.GroupID
            WHERE yu.UserID = ec.UserID
              AND ygr.RolePropertyID = -20164
        )
        AND ec.EnergyCompanyID > -1)
BEGIN
    DECLARE @NewLine CHAR(2) = CHAR(13) + CHAR(10);
    DECLARE @errorText VARCHAR(1024) = 'The role property ''Enroll Multiple Programs per Category'' has been changed to an Energy Company Setting.' + @NewLine + 'An attempt was made to use the current role property value for each Energy Company Admin Operator User, but no value could be found for one or more of the energy companies in the database.' + @NewLine + 'Attention should be paid to the new energy company setting ''Enroll Multiple Programs per Category'' after the upgrade is completed to ensure it is set to the desired value.' + @NewLine + 'See YUK-14581 for additional information.';
    RAISERROR(@errorText, 16, 1);
END;
/* @end-block */

/* @start-block */
DECLARE @MaxSetting NUMERIC = (SELECT ISNULL(MAX(EnergyCompanySettingId), 0) AS EnergyCompanySettingId FROM EnergyCompanySetting);
INSERT INTO EnergyCompanySetting
SELECT
    @MaxSetting + ROW_NUMBER() OVER (ORDER BY ec.EnergyCompanyId DESC) AS EnergyCompanySettingId,
    ec.EnergyCompanyId,
    'ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY' AS Name,
    ygr.Value as Value,
    'Y' as Enabled,
    NULL, NULL
FROM EnergyCompany ec
JOIN YukonUser yu                        ON ec.UserId = yu.UserId
JOIN UserGroup ug                        ON yu.UserGroupId = ug.UserGroupId
JOIN UserGroupToYukonGroupMapping ugtygm ON ug.UserGroupId = ugtygm.UserGroupId
JOIN YukonGroup yg                       ON ugtygm.GroupId = yg.GroupID
JOIN YukonGroupRole ygr                  ON yg.GroupId = ygr.GroupID
WHERE YGR.RolePropertyID  = -20164
  AND ec.EnergyCompanyId > -1;
/* @end-block */

DELETE FROM YukonGroupRole 
WHERE RoleID = -201 
  AND RolePropertyID = -20164;

DELETE FROM YukonRoleProperty 
WHERE RolePropertyID = -20164;
/* End YUK-14581 */

/* Start YUK-14616 */
UPDATE DeviceConfigCategory 
SET CategoryType = 'centron420DisplayItems' 
WHERE CategoryType = 'centronDisplayItems';
/* End YUK-14616 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JUL-2015', 'Latest Update', 0, GETDATE());*/