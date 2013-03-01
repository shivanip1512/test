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
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry), 1005, 0, 'LCR-3100', 1326);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry), 1005, 0, 'LCR-4600', 1327);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry), 1005, 0, 'LCR-4700', 1328);
/* End YUK-11904 */

/* Start YUK-11878 */
UPDATE YukonUser 
SET Password = ' ' 
WHERE AuthType IN ('NONE', 'AD', 'LDAP', 'RADIUS');

UPDATE YukonUser 
SET AuthType = 'NONE', Password = ' ' 
WHERE UserId = -9999;
/* End YUK-11878 */

CREATE TABLE EnergyCompanySetting (
   EnergyCompanySettingId   NUMBER         NOT NULL,
   EnergyCompanyId          NUMBER         NOT NULL,
   Name                     VARCHAR2(100)  NOT NULL,
   Value                    VARCHAR2(1000) NULL,
   Status                   VARCHAR2(100)  NULL,
   Comments                 VARCHAR2(1000) NULL,
   LastChangedDate          DATE           NULL,
   CONSTRAINT PK_EnergyCompanySetting PRIMARY KEY (EnergyCompanySettingId)
);

CREATE TABLE RolePropToSetting_Temp(
    RolePropertyId NUMERIC      NOT NULL,
    RoleName       VARCHAR(100) NOT NULL,
    Status         VARCHAR(100) NOT NULL,
    CONSTRAINT PK_RolePropToSetting_Temp PRIMARY KEY (RolePropertyId)
);

INSERT INTO RolePropToSetting_Temp VALUES (-1100, 'ADMIN_EMAIL_ADDRESS', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1101, 'OPTOUT_NOTIFICATION_RECIPIENTS', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1102, 'ENERGY_COMPANY_DEFAULT_TIME_ZONE', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1107, 'TRACK_HARDWARE_ADDRESSING', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1108, 'SINGLE_ENERGY_COMPANY', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1109, 'OPTIONAL_PRODUCT_DEV', 'UNSET');
INSERT INTO RolePropToSetting_Temp VALUES (-1110, 'DEFAULT_TEMPERATURE_UNIT', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1111, 'METER_MCT_BASE_DESIGNATION', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1112, 'APPLICABLE_POINT_TYPE_KEY', 'UNSET');
INSERT INTO RolePropToSetting_Temp VALUES (-1114, 'INHERIT_PARENT_APP_CATS', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1115, 'AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1116, 'ACCOUNT_NUMBER_LENGTH', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1117, 'ROTATION_DIGIT_LENGTH', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1118, 'SERIAL_NUMBER_VALIDATION', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1119, 'AUTOMATIC_CONFIGURATION', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1120, 'ALLOW_DESIGNATION_CODES', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1121, 'ALLOW_THERMOSTAT_SCHEDULE_ALL', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1122, 'ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1123, 'ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1124, 'ALLOW_THERMOSTAT_SCHEDULE_7_DAY', 'ALWAYS_SET');
INSERT INTO RolePropToSetting_Temp VALUES (-1125, 'BROADCAST_OPT_OUT_CANCEL_SPID', 'UNSET');
INSERT INTO RolePropToSetting_Temp VALUES (-1126, 'ALTERNATE_PROGRAM_ENROLLMENT', 'ALWAYS_SET');

/* @start-block */
DECLARE
    v_energyCompanySettingId NUMBER; 
BEGIN
    SELECT NVL(MAX(EnergyCompanySettingId)+1,0) INTO v_energyCompanySettingId
    FROM EnergyCompanySetting ecs; 

    INSERT INTO EnergyCompanySetting (EnergyCompanySettingId, EnergyCompanyId, Name, Value, Status)
        SELECT 
            v_energyCompanySettingId + ROW_NUMBER() OVER (ORDER BY ygr.RoleId, ygr.RolePropertyId), 
            EnergyCompanyId,
            RoleName, 
            CASE
                WHEN value = null THEN DefaultValue
                WHEN LTRIM(RTRIM(value)) = '' THEN DefaultValue
                WHEN value = '(none)' THEN DefaultValue
                ELSE value
            END,
            CASE
                WHEN rpts.Status = 'ALWAYS_SET'THEN rpts.Status
                WHEN LTRIM(RTRIM(value)) = '' THEN 'UNSET'
                WHEN value = '(none)' THEN 'UNSET'
                ELSE 'SET'
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

DELETE FROM YukonGroupRole    WHERE RoleID = -2;
DELETE FROM YukonRoleProperty WHERE RoleID = -2;
DELETE FROM YukonRole         WHERE RoleID = -2;
/* End YUK-11876 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('5.6', '08-MAR-2013', 'Latest Update', 2, SYSDATE);*/