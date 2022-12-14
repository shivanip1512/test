/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-11861 */
/* @error ignore-begin */
CREATE INDEX Indx_ADAS_AnalysisId_SlotId ON ArchiveDataAnalysisSlot (
    AnalysisId ASC, 
    SlotId ASC
);
GO

CREATE INDEX Indx_ADASV_SlotId_DeviceId ON ArchiveDataAnalysisSlotValue (
    SlotId ASC, 
    DeviceId ASC
);
GO
/* @error ignore-end */
/* End YUK-11861 */

/* Start YUK-11904 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-3100', 1326);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-4600', 1327);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-4700', 1328);
/* End YUK-11904 */

/* Start YUK-11878 */
UPDATE YukonUser 
SET Password = ' ' 
WHERE AuthType IN ('NONE', 'AD', 'LDAP', 'RADIUS');

UPDATE YukonUser 
SET AuthType = 'NONE', Password = ' ' 
WHERE UserId = -9999;
/* End YUK-11878 */

/* Start YUK-11876 */
CREATE TABLE EnergyCompanySetting (
    EnergyCompanySettingId   NUMERIC       NOT NULL,
    EnergyCompanyId          NUMERIC       NOT NULL,
    Name                     VARCHAR(100)  NOT NULL,
    Value                    VARCHAR(1000) NULL,
    Enabled                  CHAR(1)       NULL,
    Comments                 VARCHAR(1000) NULL,
    LastChangedDate          DATETIME      NULL,
    CONSTRAINT PK_EnergyCompanySetting PRIMARY KEY (EnergyCompanySettingId)
);
 
CREATE TABLE RolePropToSetting_Temp (
    RolePropertyId NUMERIC      NOT NULL,
    RoleName       VARCHAR(100) NOT NULL,
    Enabled        VARCHAR(100) NOT NULL,
    CONSTRAINT PK_RolePropToSetting_Temp PRIMARY KEY (RolePropertyId)
);

INSERT INTO RolePropToSetting_Temp VALUES (-1100, 'ADMIN_EMAIL_ADDRESS', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1101, 'OPTOUT_NOTIFICATION_RECIPIENTS', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1102, 'ENERGY_COMPANY_DEFAULT_TIME_ZONE', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1107, 'TRACK_HARDWARE_ADDRESSING', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1108, 'SINGLE_ENERGY_COMPANY', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1109, 'OPTIONAL_PRODUCT_DEV', 'N');
INSERT INTO RolePropToSetting_Temp VALUES (-1110, 'DEFAULT_TEMPERATURE_UNIT', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1111, 'METER_MCT_BASE_DESIGNATION', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1112, 'APPLICABLE_POINT_TYPE_KEY', 'N');
INSERT INTO RolePropToSetting_Temp VALUES (-1114, 'INHERIT_PARENT_APP_CATS', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1115, 'AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1116, 'ACCOUNT_NUMBER_LENGTH', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1117, 'ROTATION_DIGIT_LENGTH', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1118, 'SERIAL_NUMBER_VALIDATION', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1119, 'AUTOMATIC_CONFIGURATION', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1120, 'ALLOW_DESIGNATION_CODES', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1121, 'ALLOW_THERMOSTAT_SCHEDULE_ALL', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1122, 'ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1123, 'ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1124, 'ALLOW_THERMOSTAT_SCHEDULE_7_DAY', 'NA');
INSERT INTO RolePropToSetting_Temp VALUES (-1125, 'BROADCAST_OPT_OUT_CANCEL_SPID', 'N');
INSERT INTO RolePropToSetting_Temp VALUES (-1126, 'ALTERNATE_PROGRAM_ENROLLMENT', 'NA');

/* @start-block */
DECLARE
    @energyCompanySettingId NUMERIC; 
BEGIN
    SELECT @energyCompanySettingId = ISNULL(MAX(EnergyCompanySettingId)+1,0)
    FROM EnergyCompanySetting ECS; 

    INSERT INTO EnergyCompanySetting (EnergyCompanySettingId, EnergyCompanyId, Name, Value, Enabled)
        SELECT 
            @energyCompanySettingId + ROW_NUMBER() OVER (ORDER BY YGR.RoleId, YGR.RolePropertyId), 
            EnergyCompanyId,
            RoleName, 
            CASE
                WHEN value = null THEN DefaultValue
                WHEN LTRIM(RTRIM(value)) = '' THEN DefaultValue
                WHEN value = '(none)' THEN DefaultValue
                ELSE value
            END,
            CASE
                WHEN RPTS.Enabled = 'NA' THEN 'Y'
                WHEN LTRIM(RTRIM(value)) = '' THEN 'N'
                WHEN value = '(none)' THEN 'N'
                ELSE 'Y'
            END
        FROM EnergyCompany EC 
        JOIN YukonUser YU ON YU.UserID = EC.UserID 
        JOIN UserGroupToYukonGroupMapping UGYG ON UGYG.UserGroupId = YU.UserGroupId
        JOIN YukonGroupRole YGR ON YGR.GroupID = UGYG.GroupID
        JOIN YukonRoleProperty YRP ON YGR.RolePropertyID = YRP.RolePropertyID
        JOIN RolePropToSetting_Temp RPTS ON RPTS.RolePropertyId = YGR.RolePropertyId
        WHERE YGR.RoleID = -2;
END;
/* @end-block */

/* @start-block */
DECLARE
    @count NUMERIC;
BEGIN  
    SELECT @count = COUNT(EnergycompanyId)
    FROM 
       (SELECT COUNT(EnergyCompanySettingId) AS DuplicateCount, EnergyCompanyId, Name
        FROM EnergyCompanySetting
        GROUP BY EnergyCompanyId, Name) T
    WHERE T.DuplicateCount > 1;
    
    IF 0 < @count
    BEGIN
        RAISERROR('There was a problem converting energy company role properties for one or more energy companies. Multiple role groups contained the Energy Company role for an energy company''s primary operator user group, which is not a legal configuration. Please see YUK-11876 for instructions on how to resolve the problem.', 16, 1);
    END
END;
/* @end-block */

ALTER TABLE EnergyCompanySetting
    ADD CONSTRAINT AK_ECSetting_ECId_Name UNIQUE (EnergyCompanyId, Name);

ALTER TABLE EnergyCompanySetting
    ADD CONSTRAINT FK_EC_EnergyCompanySetting FOREIGN KEY (EnergyCompanyId)
        REFERENCES EnergyCompany (EnergyCompanyID)
            ON DELETE CASCADE;

DELETE FROM YukonGroupRole    WHERE RoleID = -2;
DELETE FROM YukonRoleProperty WHERE RoleID = -2;
DELETE FROM YukonRole         WHERE RoleID = -2;
DROP TABLE RolePropToSetting_Temp;
/* End YUK-11876 */

/* Start YUK-11906 */
CREATE TABLE DeviceDataMonitor (
    MonitorId               NUMERIC              NOT NULL,
    Name                    VARCHAR(255)         NOT NULL,
    GroupName               VARCHAR(255)         NOT NULL,
    Enabled                 CHAR(1)              NOT NULL,
    CONSTRAINT PK_DeviceDataMonitor PRIMARY KEY (MonitorId)
);
GO

CREATE UNIQUE INDEX Indx_DeviceDataMon_Name_UNQ
    ON DeviceDataMonitor (Name ASC);
GO

CREATE TABLE DeviceDataMonitorProcessor (
    ProcessorId             NUMERIC              NOT NULL,
    MonitorId               NUMERIC              NOT NULL,
    Attribute               VARCHAR(255)         NOT NULL,
    StateGroupId            NUMERIC              NOT NULL,
    State                   NUMERIC              NOT NULL,
    CONSTRAINT PK_DeviceDataMonitorProcessor PRIMARY KEY (ProcessorId)
);
GO

ALTER TABLE DeviceDataMonitorProcessor
    ADD CONSTRAINT FK_DevDataMonProc_DevDataMon FOREIGN KEY (MonitorId)
        REFERENCES DeviceDataMonitor (MonitorId)
            ON DELETE CASCADE;
GO

ALTER TABLE DeviceDataMonitorProcessor
    ADD CONSTRAINT FK_DevDataMonProc_StateGroup FOREIGN KEY (StateGroupId)
        REFERENCES StateGroup (StateGroupId);
GO

INSERT INTO YukonRoleProperty VALUES (-20221, -202, 'Device Data Monitor', 'false', 'Controls access to the Device Data Monitor.');
/* End YUK-11906 */

/* Start YUK-11927 */
INSERT INTO CCStrategyTargetSettings(StrategyId, SettingName, SettingValue, SettingType)
    SELECT StrategyID, 'Comm Reporting Percentage', '100', 'REGULATOR'
    FROM CapControlStrategy
    WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings(StrategyId, SettingName, SettingValue, SettingType)
    SELECT StrategyID, 'Comm Reporting Percentage', '100', 'CAPBANK'
    FROM CapControlStrategy
    WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings(StrategyId, SettingName, SettingValue, SettingType)
    SELECT StrategyID, 'Comm Reporting Percentage', '100', 'VOLTAGE_MONITOR'
    FROM CapControlStrategy
    WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';
/* End YUK-11927 */

/* Start YUK-11913 */
ALTER TABLE ArchiveValuesExportFormat
ADD FormatType VARCHAR(40);
GO

UPDATE ArchiveValuesExportFormat
SET FormatType = 'FIXED_ATTRIBUTE';

ALTER TABLE ArchiveValuesExportFormat
ALTER COLUMN FormatType VARCHAR(40) NOT NULL;
/* End YUK-11913 */

/* Start YUK-11937 */
CREATE TABLE OpenAdrEvents (
    EventId              VARCHAR(64)          NOT NULL,
    EventXml             XML                  NOT NULL,
    StartOffset          NUMERIC              NOT NULL,
    EndDate              DATETIME             NOT NULL,
    RequestId            VARCHAR(64)          NOT NULL,
    CONSTRAINT PK_OpenAdrEvents PRIMARY KEY (EventId)
);
GO
/* End YUK-11937 */

/* Start YUK-11936 */
CREATE TABLE FileExportHistory  (
    EntryId              NUMERIC                        NOT NULL,
    OriginalFileName     VARCHAR(100)                   NOT NULL,
    FileName             VARCHAR(100)                   NOT NULL,
    FileExportType       VARCHAR(50)                    NOT NULL,
    Initiator            VARCHAR(100)                   NOT NULL,
    ExportDate           DATETIME                       NOT NULL,
    ExportPath           VARCHAR(300)                   NOT NULL,
    CONSTRAINT PK_FileExportHistory PRIMARY KEY (EntryId)
);
GO
/* End YUK-11936 */

CREATE TABLE RawPointHistoryDependentJob (
    JobId                INT                  NOT NULL,
    RawPointHistoryId    NUMERIC              NOT NULL,
    CONSTRAINT PK_RawPointHistoryDependentJob PRIMARY KEY (JobId)
);
GO

ALTER TABLE RawPointHistoryDependentJob
    ADD CONSTRAINT FK_RPHDependentJob_Job FOREIGN KEY (JobId)
        REFERENCES Job (JobId);
GO
/* End YUK-11951 */

/* Start YUK-11953 */
CREATE TABLE temp_PointTableNames (
    tableName VARCHAR(30)
);

INSERT INTO temp_PointTableNames VALUES 
    ('SystemLog'), ('RawPointHistory'), ('PointAnalog'), ('PointUnit'), ('PointLimits'),
    ('PointPropertyValue'), ('FDRTranslation'), ('DynamicPointDispatch'), ('DynamicAccumulator'),
    ('GraphDataSeries'), ('DynamicPointAlarming'), ('CalcComponent'), ('TagLog'), ('DynamicTags'),
    ('Display2WayData'), ('CCEventLog'), ('PointAlarming'), ('Point');

/* @start-block */
DECLARE @pointOffsetsToDelete VARCHAR(255) = '(30, 102, 103, 104, 124, 172, 173, 174, 175)';

DECLARE @deleteCommand VARCHAR(MAX);
DECLARE @currentTable VARCHAR(30);
DECLARE tableNames CURSOR FOR (SELECT tableName 
                               FROM temp_PointTableNames);
OPEN tableNames;
FETCH NEXT FROM tableNames INTO @currentTable;
WHILE @@FETCH_STATUS = 0
BEGIN
    SET @deleteCommand = 
        'DELETE FROM ' + @currentTable + ' WHERE PointId IN (
             SELECT PointId FROM Point P
             JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
             WHERE P.PointType = ''Analog''
               AND P.PointOffset IN ' + @pointOffsetsToDelete
           + ' AND YP.Type LIKE ''RFN%'');';
    PRINT ('Executing: ' + @deleteCommand);
    EXECUTE (@deleteCommand);
    FETCH NEXT FROM tableNames INTO @currentTable;
END;

CLOSE tableNames;
DEALLOCATE tableNames;
/* @end-block */
DROP TABLE temp_PointTableNames;
/* End YUK-11953 */

/* Start YUK-11954 */
UPDATE YukonPAObject 
SET Type = 'RFN-430A3R' 
FROM YukonPAObject YPAO
LEFT JOIN RFNAddress RFNA ON RFNA.DeviceId = YPAO.PAObjectID
WHERE RFNA.Model = 'A3R'
   OR (YPAO.PAOName LIKE '%EE_A3R'
       AND YPAO.Type = 'RFN-430A3');

UPDATE YukonPAObject 
SET Type = 'RFN-430A3D' 
FROM YukonPAObject YPAO
LEFT JOIN RFNAddress RFNA ON RFNA.DeviceId = YPAO.PAObjectID
WHERE RFNA.Model = 'A3D'
   OR (YPAO.PAOName LIKE '%EE_A3D'
       AND YPAO.Type = 'RFN-430A3');

UPDATE YukonPAObject 
SET Type = 'RFN-430A3T' 
FROM YukonPAObject YPAO
LEFT JOIN RFNAddress RFNA ON RFNA.DeviceId = YPAO.PAObjectID
WHERE RFNA.Model = 'A3T'
   OR (YPAO.PAOName LIKE '%EE_A3T'
       AND YPAO.Type = 'RFN-430A3');

UPDATE YukonPAObject 
SET Type = 'RFN-430A3K' 
FROM YukonPAObject YPAO
LEFT JOIN RFNAddress RFNA ON RFNA.DeviceId = YPAO.PAObjectID
WHERE RFNA.Model = 'A3K'
   OR (YPAO.PAOName LIKE '%EE_A3K'
       AND YPAO.Type = 'RFN-430A3');
/* End YUK-11954 */

/* Start YUK-11975 */
DELETE FROM ECToGenericMapping 
WHERE EnergyCompanyID = -1
  AND ItemID = -1
  AND MappingCategory = 'LMThermostatSchedule';
/* End YUK-11975 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '11-MAR-2013', 'Latest Update', 2, GETDATE());