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
WHERE YGR.RolePropertyID  = -20164;
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

/* Start YUK-14668 */
DELETE FROM GlobalSetting 
WHERE Name IN ('INTERVAL', 'BASELINE_CALCTIME', 'DAYS_PREVIOUS_TO_COLLECT');
/* End YUK-14668 */

/* Start YUK-14667 */
UPDATE DeviceConfigCategory 
SET CategoryType = 'rfnVoltage' 
WHERE CategoryType = 'rfnOvUv';

INSERT INTO DeviceConfigCategoryItem
    SELECT
       (SELECT ISNULL(MAX(DeviceConfigCategoryItemId) + T.RowNum, 0) AS Id FROM DeviceConfigCategoryItem),
       T.DeviceConfigCategoryId,
       'voltageAveragingInterval',
       60
    FROM (
       SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryId) as RowNum, 
              DeviceConfigCategoryId
       FROM DeviceConfigCategory 
       WHERE CategoryType = 'rfnVoltage') T;

DELETE FROM DeviceConfigCategoryMap
WHERE DeviceConfigurationId IN (
    SELECT dc.DeviceConfigurationID
    FROM DeviceConfiguration dc
    WHERE NOT EXISTS (
       SELECT 1 FROM DeviceConfigDeviceTypes dcdt
       WHERE dcdt.DeviceConfigurationId = dc.DeviceConfigurationID
         AND dcdt.PaoType IN ('RFN-410cL', 'RFN-420cL', 'RFN-420fX', 'RFN-420fD', 'RFN-420fL', 'RFN-420fRX', 'RFN-420fRD', 'RFN-420cD')))
AND DeviceConfigCategoryId IN (
    SELECT DeviceConfigCategoryId
    FROM DeviceConfigCategory
    WHERE CategoryType = 'rfnVoltage');
/* End YUK-14667 */

/* Start YUK-14624 */
DELETE FROM DeviceConfigCategoryMap
WHERE DeviceConfigurationId IN (
    SELECT dc.DeviceConfigurationID
    FROM DeviceConfiguration dc
    WHERE EXISTS (
       SELECT 1 FROM DeviceConfigDeviceTypes dcdt
       WHERE dcdt.DeviceConfigurationId = dc.DeviceConfigurationID
         AND dcdt.PaoType LIKE 'RFN%')
      AND NOT EXISTS (
        SELECT 1 FROM DeviceConfigDeviceTypes dcdt
       WHERE dcdt.DeviceConfigurationId = dc.DeviceConfigurationID
         AND dcdt.PaoType LIKE 'MCT%'))
AND DeviceConfigCategoryId IN (
    SELECT DeviceConfigCategoryId
    FROM DeviceConfigCategory
    WHERE CategoryType = 'profile');
/* End YUK-14624 */

/* Start YUK-14684 */
INSERT INTO DeviceConfigCategoryItem
    SELECT
       (SELECT ISNULL(MAX(DeviceConfigCategoryItemId) + T.RowNum, 0) AS Id FROM DeviceConfigCategoryItem),
       T.DeviceConfigCategoryId,
       'voltageDataStreamingIntervalMinutes',
       5
    FROM (
       SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryId) as RowNum, 
              DeviceConfigCategoryId
       FROM DeviceConfigCategory 
       WHERE CategoryType = 'rfnChannelConfiguration') T;
/* End YUK-14684 */

/* Start YUK-14722 */
CREATE TABLE GatewayFirmwareUpdate (
   UpdateId             NUMERIC              NOT NULL,
   SendDate             DATETIME             NOT NULL,
   GatewayCount         NUMERIC              NOT NULL,
   UpdateServerCount    NUMERIC              NOT NULL,
   CONSTRAINT PK_GatewayFirmwareUpdate PRIMARY KEY (UpdateId)
);
GO

CREATE TABLE GatewayFirmwareUpdateEntry (
   EntryId              NUMERIC              NOT NULL,
   UpdateId             NUMERIC              NOT NULL,
   GatewayId            NUMERIC              NOT NULL,
   OriginalVersion      VARCHAR(100)         NOT NULL,
   NewVersion           VARCHAR(100)         NOT NULL,
   UpdateServerUrl      VARCHAR(2000)         NOT NULL,
   UpdateStatus         VARCHAR(40)          NOT NULL,
   CONSTRAINT PK_GatewayFirmwareUpdateEntry PRIMARY KEY (EntryId)
);
GO

ALTER TABLE GatewayFirmwareUpdateEntry
   ADD CONSTRAINT FK_GatewayFUEnt_GatewayFUUpd FOREIGN KEY (UpdateId)
      REFERENCES GatewayFirmwareUpdate (UpdateId)
         ON DELETE CASCADE;
GO

ALTER TABLE GatewayFirmwareUpdateEntry
   ADD CONSTRAINT FK_GatewayFirmUpdateEnt_Device FOREIGN KEY (GatewayId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
GO
/* End YUK-14722 */

/* Start YUK-14739 */
ALTER TABLE LMProgramDirect
ADD NotifySchedule NUMERIC;
GO

UPDATE LMProgramDirect
SET NotifySchedule = -1;

ALTER TABLE LMProgramDirect
ALTER COLUMN NotifySchedule NUMERIC NOT NULL;
GO
/* End YUK-14739 */

/* Start YUK-14754 */
/* @error ignore-begin */
DROP TABLE temp_FdrTranslation_Increment;
/* @error ignore-end */
/* End YUK-14754 */

/* Start YUK-14755 */
UPDATE GlobalSetting
SET Value = 'yukon@eaton.com'
WHERE Name = 'MAIL_FROM_ADDRESS'
AND Value = 'yukon@cannontech.com';

UPDATE EnergyCompanySetting
SET Value = 'yukon@eaton.com'
WHERE Name = 'ADMIN_EMAIL_ADDRESS'
AND Value = 'info@cannontech.com';
/* End YUK-14755 */

/* Start YUK-14784 */
INSERT INTO Point VALUES (-11, 'Analog', 'Porter CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1007, 'None', 0);
INSERT INTO Point VALUES (-12, 'Analog', 'Dispatch CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1008, 'None', 0);
INSERT INTO Point VALUES (-13, 'Analog', 'Scanner CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1009, 'None', 0);
INSERT INTO Point VALUES (-14, 'Analog', 'Calc CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1010, 'None', 0);
INSERT INTO Point VALUES (-15, 'Analog', 'CapControl CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1011, 'None', 0);
INSERT INTO Point VALUES (-16, 'Analog', 'FDR CPU Utilization', 0, 'Default', 0, 'N', 'N', 'R', 1012, 'None', 0);
INSERT INTO Point VALUES (-17, 'Analog', 'MACS CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1013, 'None', 0);
INSERT INTO Point VALUES (-18, 'Analog', 'Notification Server CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1014, 'None', 0);
INSERT INTO Point VALUES (-19, 'Analog', 'Service Manager CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1015, 'None', 0);
INSERT INTO Point VALUES (-20, 'Analog', 'Web Service CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1016, 'None', 0);
INSERT INTO Point VALUES (-21, 'Analog', 'Porter Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1017, 'None', 0);
INSERT INTO Point VALUES (-22, 'Analog', 'Dispatch Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1018, 'None', 0);
INSERT INTO Point VALUES (-23, 'Analog', 'Scanner Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1019, 'None', 0);
INSERT INTO Point VALUES (-24, 'Analog', 'Calc Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1020, 'None', 0);
INSERT INTO Point VALUES (-25, 'Analog', 'CapControl Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1021, 'None', 0);
INSERT INTO Point VALUES (-26, 'Analog', 'FDR Memory Utilization', 0, 'Default', 0, 'N', 'N', 'R', 1022, 'None', 0);
INSERT INTO Point VALUES (-27, 'Analog', 'MACS Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1023, 'None', 0);
INSERT INTO Point VALUES (-28, 'Analog', 'Notification Server Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1024, 'None', 0);
INSERT INTO Point VALUES (-29, 'Analog', 'Service Manager Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1025, 'None', 0);
INSERT INTO Point VALUES (-30, 'Analog', 'Web Service Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1026, 'None', 0);

INSERT INTO PointAnalog VALUES (-11, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-12, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-13, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-14, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-15, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-16, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-17, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-18, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-19, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-20, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-21, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-22, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-23, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-24, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-25, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-26, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-27, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-28, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-29, -1, 1, 0 );
INSERT INTO PointAnalog VALUES (-30, -1, 1, 0 );

INSERT INTO UnitMeasure VALUES ((SELECT MAX(UomId) + 1 FROM UnitMeasure), 'MB', 0, 'MegaBytes', '(none)');

INSERT INTO PointUnit VALUES (-11, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-12, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-13, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-14, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-15, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-16, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-17, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-18, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-19, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-20, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-21, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-22, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-23, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-24, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-25, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-26, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-27, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-28, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-29, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO PointUnit VALUES (-30, (SELECT UomId FROM UnitMeasure WHERE UomName = 'MB'), 0, 1.0E+30, -1.0E+30, 0);
/* End YUK-14784 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JUL-2015', 'Latest Update', 0, GETDATE());*/