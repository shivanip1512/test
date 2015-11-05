/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14427 */
DELETE FROM ExtraPaoPointAssignment 
WHERE Attribute = 'KEEP_ALIVE_TIMER';
/* End YUK-14427 */

/* Start YUK-14460 */
ALTER TABLE PaoLocation
ADD LastChangedDate DATE;

UPDATE PaoLocation
SET LastChangedDate = SYSDATE;

ALTER TABLE PaoLocation
MODIFY LastChangedDate NOT NULL;

ALTER TABLE PaoLocation
ADD Origin VARCHAR2(64);

UPDATE PaoLocation
SET Origin = 'MANUAL';

ALTER TABLE PaoLocation
MODIFY Origin NOT NULL;
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

UPDATE InventoryConfigTask
SET SendOutOfService = 'N'
WHERE SendOutOfService NOT LIKE 'Y'
   OR SendOutOfService IS NULL;

ALTER TABLE InventoryConfigTask
MODIFY SendOutOfService CHAR(1) NOT NULL;
/* @error ignore-end */
/* End YUK-14537 */

/* Start YUK-14474 */
DELETE FROM GlobalSetting 
WHERE Name = 'WEB_LOGO_URL';
/* End YUK-14474 */

/* Start YUK-14433 */
/* @error warn-once */
/* @start-block */
DECLARE
    errorFlagCount INT;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount
    FROM YukonServices YS
    WHERE YS.ServiceID = 3;
    
    IF 0 < errorFlagCount
    THEN 
        RAISE_APPLICATION_ERROR(-20001, 'Yukon Calc Historical Service is enabled. This service must be uninstalled and Yukon Calc-Logic Service used instead.');
    END IF;
END;
/
/* @end-block */
DELETE FROM YukonServices WHERE ServiceId = 3 OR ServiceId = -3;
/* End YUK-14433 */

/* Start YUK-14581 */
/* @error warn-once */
/* @start-block */
DECLARE 
    v_errorCount NUMBER;
    v_NewLine VARCHAR2(2) := CHR(13) || CHR(10);
    v_errorText VARCHAR2(1024) := 'The role property ''Enroll Multiple Programs per Category'' has been changed to an Energy Company Setting.' || v_NewLine || 'An attempt was made to use the current role property value for each Energy Company Admin Operator User, but no value could be found for one or more of the energy companies in the database.' || v_NewLine || 'Attention should be paid to the new energy company setting ''Enroll Multiple Programs per Category'' after the upgrade is completed to ensure it is set to the desired value.' || v_NewLine || 'See YUK-14581 for additional information.';
BEGIN
    SELECT COUNT(EnergyCompanyId) INTO v_errorCount
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
    AND ec.EnergyCompanyID > -1;
    
    IF 0 < v_errorCount
    THEN
        RAISE_APPLICATION_ERROR(-20001, v_errorText);
        v_errorCount := 100;
    END IF;
END;
/* @end-block */

/* @start-block */
DECLARE v_MaxSetting NUMBER; 
BEGIN
    SELECT NVL(MAX(EnergyCompanySettingId), 0) INTO v_MaxSetting FROM EnergyCompanySetting;
    INSERT INTO EnergyCompanySetting (
        SELECT
            v_MaxSetting + ROW_NUMBER() OVER (ORDER BY ec.EnergyCompanyId DESC) AS EnergyCompanySettingId,
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
        WHERE YGR.RolePropertyID  = -20164);
END;
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
    SELECT MaxDeviceConfigCategoryItemId + ROWNUM,
        DeviceConfigCategoryId,
        Name,
        Value
    FROM (
        SELECT
           (SELECT NVL(MAX(DeviceConfigCategoryItemId), 0) AS Id FROM DeviceConfigCategoryItem) AS MaxDeviceConfigCategoryItemId,
           ROWNUM,
           T.DeviceConfigCategoryId AS DeviceConfigCategoryId,
           'voltageAveragingInterval' AS Name,
           60 AS Value
        FROM (
           SELECT DeviceConfigCategoryId
           FROM DeviceConfigCategory 
           WHERE CategoryType = 'rfnVoltage') T
    ) U;

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
    SELECT MaxDeviceConfigCategoryItemId + ROWNUM,
        DeviceConfigCategoryId,
        Name,
        Value
    FROM (
        SELECT
           (SELECT NVL(MAX(DeviceConfigCategoryItemId), 0) AS Id FROM DeviceConfigCategoryItem) AS MaxDeviceConfigCategoryItemId,
           ROWNUM,
           T.DeviceConfigCategoryId AS DeviceConfigCategoryId,
           'voltageDataStreamingIntervalMinutes' AS Name,
           5 AS Value
        FROM (
           SELECT DeviceConfigCategoryId
           FROM DeviceConfigCategory 
           WHERE CategoryType = 'rfnChannelConfiguration') T
    ) U;
/* End YUK-14684 */

/* Start YUK-14722 */
CREATE TABLE GatewayFirmwareUpdate (
   UpdateId             NUMBER              NOT NULL,
   SendDate             DATE                NOT NULL,
   GatewayCount         NUMBER              NOT NULL,
   UpdateServerCount    NUMBER              NOT NULL,
   CONSTRAINT PK_GatewayFirmwareUpdate PRIMARY KEY (UpdateId)
);

CREATE TABLE GatewayFirmwareUpdateEntry (
   EntryId              NUMBER              NOT NULL,
   UpdateId             NUMBER              NOT NULL,
   GatewayId            NUMBER              NOT NULL,
   OriginalVersion      VARCHAR2(100)       NOT NULL,
   NewVersion           VARCHAR2(100)       NOT NULL,
   UpdateServerUrl      VARCHAR2(2000)       NOT NULL,
   UpdateStatus         VARCHAR2(40)        NOT NULL,
   CONSTRAINT PK_GatewayFirmwareUpdateEntry PRIMARY KEY (EntryId)
);

ALTER TABLE GatewayFirmwareUpdateEntry
   ADD CONSTRAINT FK_GatewayFUEnt_GatewayFUUpd FOREIGN KEY (UpdateId)
      REFERENCES GatewayFirmwareUpdate (UpdateId)
         ON DELETE CASCADE;

ALTER TABLE GatewayFirmwareUpdateEntry
   ADD CONSTRAINT FK_GatewayFirmUpdateEnt_Device FOREIGN KEY (GatewayId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
/* End YUK-14722 */

/* Start YUK-14739 */
ALTER TABLE LMProgramDirect
ADD NotifySchedule NUMBER;

UPDATE LMProgramDirect
SET NotifySchedule = -1;

ALTER TABLE LMProgramDirect
MODIFY NotifySchedule NUMBER NOT NULL;
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
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'Porter CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1007, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'Dispatch CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1008, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'Scanner CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1009, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'Calc CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1010, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'CapControl CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1011, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'FDR CPU Utilization', 0, 'Default', 0, 'N', 'N', 'R', 1012, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'MACS CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1013, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'Notification Server CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1014, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'Service Manager CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1015, 'None', 0);
INSERT INTO Point VALUES ((SELECT MAX(PointId) + 1 FROM Point), 'Analog', 'Web Service CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1016, 'None', 0);

INSERT INTO PointAnalog
    SELECT PointId, -1, 1, 0
    FROM Point
    WHERE PointName IN ('Porter CPU Utilization', 'Dispatch CPU Utilization', 
        'Scanner CPU Utilization', 'Calc CPU Utilization', 'CapControl CPU Utilization', 'FDR CPU Utilization', 
        'MACS CPU Utilization', 'Notification Server CPU Utilization', 'Service Manager CPU Utilization', 
        'Web Service CPU Utilization');

INSERT INTO PointUnit 
    SELECT PointId, 28, 2, 1.0E+30, -1.0E+30, 0
    FROM Point
    WHERE PointName IN ('Porter CPU Utilization', 'Dispatch CPU Utilization', 
        'Scanner CPU Utilization', 'Calc CPU Utilization', 'CapControl CPU Utilization', 'FDR CPU Utilization', 
        'MACS CPU Utilization', 'Notification Server CPU Utilization', 'Service Manager CPU Utilization', 
        'Web Service CPU Utilization');
/* End YUK-14784 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JUL-2015', 'Latest Update', 0, SYSDATE);*/