/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11861 */
/* @error ignore-begin */
CREATE INDEX Indx_ADAS_AnalysisId_SlotId ON ArchiveDataAnalysisSlot (
    AnalysisId ASC, 
    SlotId ASC
);

CREATE INDEX Indx_ADASV_SlotId_DeviceId ON ArchiveDataAnalysisSlotValue (
    SlotId ASC, 
    DeviceId ASC
);
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
    EnergyCompanySettingId   NUMBER         NOT NULL,
    EnergyCompanyId          NUMBER         NOT NULL,
    Name                     VARCHAR2(100)  NOT NULL,
    Value                    VARCHAR2(1000) NULL,
    Enabled                  CHAR(1)        NULL,
    Comments                 VARCHAR2(1000) NULL,
    LastChangedDate          DATE           NULL,
    CONSTRAINT PK_EnergyCompanySetting PRIMARY KEY (EnergyCompanySettingId)
);

CREATE TABLE RolePropToSetting_Temp (
    RolePropertyId NUMBER       NOT NULL,
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
    v_energyCompanySettingId NUMBER; 
BEGIN
    SELECT NVL(MAX(EnergyCompanySettingId)+1,0) INTO v_energyCompanySettingId
    FROM EnergyCompanySetting ecs; 

    INSERT INTO EnergyCompanySetting (EnergyCompanySettingId, EnergyCompanyId, Name, Value, Enabled)
        SELECT 
            v_energyCompanySettingId + ROW_NUMBER() OVER (ORDER BY ygr.RoleId, ygr.RolePropertyId), 
            EnergyCompanyId,
            RoleName, 
            CASE
                WHEN value IS NULL THEN DefaultValue
                WHEN LTRIM(RTRIM(value)) IS NULL THEN DefaultValue
                WHEN value = '(none)' THEN DefaultValue
                ELSE value
            END,
            CASE
                WHEN rpts.Enabled = 'NA' THEN 'Y'
                WHEN LTRIM(RTRIM(value)) IS NULL THEN 'N'
                WHEN value = '(none)' THEN 'N'
                ELSE 'Y'
            END
        FROM EnergyCompany ec 
        JOIN YukonUser yu ON yu.UserID = ec.UserID 
        JOIN UserGroupToYukonGroupMapping ugyg ON ugyg.UserGroupId = yu.UserGroupId
        JOIN YukonGroupRole ygr ON ygr.GroupID = ugyg.GroupID
        JOIN YukonRoleProperty yrp ON ygr.RolePropertyID = yrp.RolePropertyID
        JOIN RolePropToSetting_Temp rpts ON rpts.RolePropertyId = ygr.RolePropertyId
        WHERE ygr.RoleID = -2;
END;
/
/* @end-block */

/* @start-block */
DECLARE
    v_count NUMBER;
BEGIN  
    SELECT COUNT(EnergycompanyId) INTO v_count
    FROM 
       (SELECT COUNT(EnergyCompanySettingId) AS DuplicateCount, EnergyCompanyId, Name
        FROM EnergyCompanySetting
        GROUP BY EnergyCompanyId, Name) t
    WHERE t.DuplicateCount > 1;
    
    IF 0 < v_count THEN
        RAISE_APPLICATION_ERROR(-20001, 'There was a problem converting energy company role properties for one or more energy companies. Multiple role groups contained the Energy Company role for an energy company''s primary operator user group, which is not a legal configuration. Please see YUK-11876 for instructions on how to resolve the problem.');
    END IF;
END;
/
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
    MonitorId               NUMBER               NOT NULL,
    Name                    VARCHAR2(255)        NOT NULL,
    GroupName               VARCHAR2(255)        NOT NULL,
    Enabled                 CHAR(1)              NOT NULL,
    CONSTRAINT PK_DeviceDataMonitor PRIMARY KEY (MonitorId)
);

CREATE UNIQUE INDEX Indx_DeviceDataMon_Name_UNQ
    ON DeviceDataMonitor (Name ASC);

CREATE TABLE DeviceDataMonitorProcessor (
    ProcessorId             NUMBER               NOT NULL,
    MonitorId               NUMBER               NOT NULL,
    Attribute               VARCHAR2(255)        NOT NULL,
    StateGroupId            NUMBER               NOT NULL,
    State                   NUMBER               NOT NULL,
    CONSTRAINT PK_DeviceDataMonitorProcessor PRIMARY KEY (ProcessorId)
);

ALTER TABLE DeviceDataMonitorProcessor
    ADD CONSTRAINT FK_DevDataMonProc_DevDataMon FOREIGN KEY (MonitorId)
        REFERENCES DeviceDataMonitor (MonitorId)
            ON DELETE CASCADE;

ALTER TABLE DeviceDataMonitorProcessor
    ADD CONSTRAINT FK_DevDataMonProc_StateGroup FOREIGN KEY (StateGroupId)
        REFERENCES StateGroup (StateGroupId);

INSERT INTO YukonRoleProperty VALUES (-20221, -202, 'Device Data Monitor', 'false', 'Controls access to the Device Data Monitor.');
/* End YUK-11906 */

/* Start YUK-11927 */
/* @start-block */
DECLARE 
    v_regulatorCommReporting VARCHAR2(60);
    v_capbankCommReporting VARCHAR2(60);
    v_monitorCommReporting VARCHAR2(60);
BEGIN
    /* @start-cparm CAP_CONTROL_IVVC_REGULATOR_REPORTING_RATIO */
    v_regulatorCommReporting := '100';
    /* @end-cparm */

    /* @start-cparm CAP_CONTROL_IVVC_BANKS_REPORTING_RATIO */
    v_capbankCommReporting := '100';
    /* @end-cparm */

    /* @start-cparm CAP_CONTROL_IVVC_VOLTAGEMONITOR_REPORTING_RATIO */
    v_monitorCommReporting := '100';
    /* @end-cparm */

    INSERT INTO CCStrategyTargetSettings
        SELECT StrategyID, 'Comm Reporting Percentage', v_regulatorCommReporting, 'REGULATOR'
        FROM CapControlStrategy
        WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

    INSERT INTO CCStrategyTargetSettings
        SELECT StrategyID, 'Comm Reporting Percentage', v_capbankCommReporting, 'CAPBANK'
        FROM CapControlStrategy
        WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

    INSERT INTO CCStrategyTargetSettings
        SELECT StrategyID, 'Comm Reporting Percentage', v_monitorCommReporting, 'VOLTAGE_MONITOR'
        FROM CapControlStrategy
        WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';
END;
/
/* @end-block */
/* End YUK-11927 */

/* Start YUK-11913 */
ALTER TABLE ArchiveValuesExportFormat
ADD FormatType VARCHAR2(40);

UPDATE ArchiveValuesExportFormat
SET FormatType = 'FIXED_ATTRIBUTE';

ALTER TABLE ArchiveValuesExportFormat
MODIFY FormatType VARCHAR2(40) NOT NULL;
/* End YUK-11913 */

/* Start YUK-11937 */
CREATE TABLE OpenAdrEvents  (
    EventId              VARCHAR2(64)                    NOT NULL,
    EventXml             XMLTYPE                         NOT NULL,
    StartOffset          NUMBER                          NOT NULL,
    EndDate              DATE                            NOT NULL,
    RequestId            VARCHAR2(64)                    NOT NULL,
    CONSTRAINT PK_OpenAdrEvents PRIMARY KEY (EventId)
);
/* End YUK-11937 */

/* Start YUK-11936 */
CREATE TABLE FileExportHistory  (
    EntryId              NUMBER                          NOT NULL,
    OriginalFileName     VARCHAR2(100)                   NOT NULL,
    FileName             VARCHAR2(100)                   NOT NULL,
    FileExportType       VARCHAR2(50)                    NOT NULL,
    Initiator            VARCHAR2(100)                   NOT NULL,
    ExportDate           DATE                            NOT NULL,
    ExportPath           VARCHAR2(300)                   NOT NULL,
    CONSTRAINT PK_FileExportHistory PRIMARY KEY (EntryId)
);
/* End YUK-11936 */

/* Start YUK-11951 */
CREATE TABLE RawPointHistoryDependentJob (
    JobId                INTEGER              NOT NULL,
    RawPointHistoryId    NUMBER               NOT NULL,
    CONSTRAINT PK_RawPointHistoryDependentJob PRIMARY KEY (JobId)
);

ALTER TABLE RawPointHistoryDependentJob
    ADD CONSTRAINT FK_RPHDependentJob_Job FOREIGN KEY (JobId)
        REFERENCES Job (JobId);
/* End YUK-11951 */

/* Start YUK-11953 */
CREATE TABLE temp_PointTableNames (
    tableName VARCHAR2(30)
);

INSERT ALL
    INTO temp_PointTableNames VALUES ('SystemLog')
    INTO temp_PointTableNames VALUES ('RawPointHistory')
    INTO temp_PointTableNames VALUES ('PointAnalog')
    INTO temp_PointTableNames VALUES ('PointUnit')
    INTO temp_PointTableNames VALUES ('PointLimits')
    INTO temp_PointTableNames VALUES ('PointPropertyValue')
    INTO temp_PointTableNames VALUES ('FDRTranslation')
    INTO temp_PointTableNames VALUES ('DynamicPointDispatch')
    INTO temp_PointTableNames VALUES ('DynamicAccumulator')
    INTO temp_PointTableNames VALUES ('GraphDataSeries')
    INTO temp_PointTableNames VALUES ('DynamicPointAlarming')
    INTO temp_PointTableNames VALUES ('CalcComponent')
    INTO temp_PointTableNames VALUES ('TagLog')
    INTO temp_PointTableNames VALUES ('DynamicTags')
    INTO temp_PointTableNames VALUES ('Display2WayData')
    INTO temp_PointTableNames VALUES ('CCEventLog')
    INTO temp_PointTableNames VALUES ('PointAlarming')
    INTO temp_PointTableNames VALUES ('Point')
SELECT 1 FROM DUAL;

/* @start-block */
DECLARE 
    v_pointOffsetsToDelete VARCHAR2(255);
    v_deleteCommand VARCHAR2(1000);
    v_currentTable VARCHAR2(30);
    CURSOR curs_tableNames IS (SELECT tableName 
                               FROM temp_PointTableNames);
BEGIN
    v_pointOffsetsToDelete := '(30, 102, 103, 104, 124, 172, 173, 174, 175)';
    OPEN curs_tableNames;
    LOOP
        FETCH curs_tableNames INTO v_currentTable;
        EXIT WHEN curs_tableNames%NOTFOUND;
        v_deleteCommand := CONCAT('DELETE FROM ', v_currentTable);
        v_deleteCommand := CONCAT(v_deleteCommand, ' WHERE PointId IN (');
        v_deleteCommand := CONCAT(v_deleteCommand, ' SELECT PointId FROM Point p ');
        v_deleteCommand := CONCAT(v_deleteCommand, ' JOIN YukonPAObject yp ON yp.PAObjectID = p.PAObjectID ');
        v_deleteCommand := CONCAT(v_deleteCommand, ' WHERE p.PointType = ''Analog'' ');
        v_deleteCommand := CONCAT(v_deleteCommand, ' AND p.PointOffset IN ');
        v_deleteCommand := CONCAT(v_deleteCommand, v_pointOffsetsToDelete);
        v_deleteCommand := CONCAT(v_deleteCommand, ' AND yp.Type LIKE ''RFN%'')');
        DBMS_OUTPUT.PUT_LINE(CONCAT('Executing: ', v_deleteCommand));
        EXECUTE IMMEDIATE v_deleteCommand;
     END LOOP;
    CLOSE curs_tableNames;
END;
/
/* @end-block */
DROP TABLE temp_PointTableNames;
/* End YUK-11953 */

/* Start YUK-11954 */
UPDATE YukonPAObject ypao
SET ypao.Type = 'RFN-430A3R'
WHERE ypao.PAOName like '%EE_A3R' 
   OR ypao.PAOBjectId IN (SELECT rfna.DeviceId
                          FROM RFNAddress rfna
                          WHERE Model = 'A3R');

UPDATE YukonPAObject ypao
SET ypao.Type = 'RFN-430A3D'
WHERE ypao.PAOName like '%EE_A3D'
   OR ypao.PAOBjectId IN (SELECT rfna.DeviceId
                          FROM RFNAddress rfna
                          WHERE Model = 'A3D');

UPDATE YukonPAObject ypao
SET ypao.Type = 'RFN-430A3T'
WHERE ypao.PAOName like '%EE_A3T'
   OR ypao.PAOBjectId IN (SELECT rfna.DeviceId
                          FROM RFNAddress rfna
                          WHERE Model = 'A3T');

UPDATE YukonPAObject ypao
SET ypao.Type = 'RFN-430A3K'
WHERE ypao.PAOName like '%EE_A3K'
   OR ypao.PAOBjectId IN (SELECT rfna.DeviceId
                          FROM RFNAddress rfna
                          WHERE Model = 'A3K');
/* End YUK-11954 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '11-MAR-2013', 'Latest Update', 2, SYSDATE);