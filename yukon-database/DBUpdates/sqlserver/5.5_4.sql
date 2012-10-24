/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11429 */
ALTER TABLE Regulator
ADD VoltChangePerTap FLOAT;

UPDATE Regulator
SET VoltChangePerTap = 0.75;

ALTER TABLE Regulator
ALTER COLUMN VoltChangePerTap FLOAT NOT NULL;
/* End YUK-11429 */

/* Start YUK-11466 */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Low Voltage Violation', '3.0', 'BANDWIDTH'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'High Voltage Violation', '1.0', 'BANDWIDTH'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Low Voltage Violation', '-10.0', 'COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'High Voltage Violation', '70.0', 'COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Low Voltage Violation', '-150.0', 'EMERGENCY_COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'High Voltage Violation', '300.0', 'EMERGENCY_COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';
/* End YUK-11466 */

/* Start YUK-11481 */
CREATE TABLE RolePropToSetting_Temp(
    RolePropertyId NUMERIC(18, 0) NOT NULL,
    RolePropertyEnum VARCHAR(100) NOT NULL,
    RoleEnum VARCHAR(50) NOT NULL,
    RoleCategory VARCHAR(50) NOT NULL,
 CONSTRAINT PK_RolePropToSetting_Temp PRIMARY KEY (RolePropertyId)
);

INSERT INTO RolePropToSetting_Temp VALUES (-10400, 'INTERVAL', 'CALC_HISTORICAL', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-10401, 'BASELINE_CALCTIME', 'CALC_HISTORICAL', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-10402, 'DAYS_PREVIOUS_TO_COLLECT', 'CALC_HISTORICAL', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-10500, 'HOME_DIRECTORY', 'WEB_GRAPH', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-10501, 'RUN_INTERVAL', 'WEB_GRAPH', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1300, 'SERVER_ADDRESS', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1301, 'AUTH_PORT', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1302, 'ACCT_PORT', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1303, 'SECRET_KEY', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1304, 'AUTH_METHOD', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1305, 'AUTHENTICATION_MODE', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1306, 'AUTH_TIMEOUT', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1307, 'DEFAULT_AUTH_TYPE', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1308, 'LDAP_DN', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1309, 'LDAP_USER_SUFFIX', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1310, 'LDAP_USER_PREFIX', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1311, 'LDAP_SERVER_ADDRESS', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1312, 'LDAP_SERVER_PORT', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1313, 'LDAP_SERVER_TIMEOUT', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1314, 'AD_SERVER_ADDRESS', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1315, 'AD_SERVER_PORT', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1316, 'AD_SERVER_TIMEOUT', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1317, 'AD_NTDOMAIN', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1318, 'ENABLE_PASSWORD_RECOVERY', 'AUTHENTICATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1500, 'WIZ_ACTIVATE', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1501, 'INPUT_FILE', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1503, 'DEFAULT_BILLING_FORMAT', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1504, 'DEMAND_DAYS_PREVIOUS', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1505, 'ENERGY_DAYS_PREVIOUS', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1506, 'APPEND_TO_FILE', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1507, 'REMOVE_MULTIPLIER', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1508, 'COOP_ID_CADP_ONLY', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1509, 'DEFAULT_ROUNDING_MODE', 'SYSTEM_BILLING', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1700, 'DEVICE_DISPLAY_TEMPLATE', 'SYSTEM_CONFIGURATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1701, 'ALERT_TIMEOUT_HOURS', 'SYSTEM_CONFIGURATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1702, 'CUSTOMER_INFO_IMPORTER_FILE_LOCATION', 'SYSTEM_CONFIGURATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1703, 'SYSTEM_TIMEZONE', 'SYSTEM_CONFIGURATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1704, 'OPT_OUTS_COUNT', 'SYSTEM_CONFIGURATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1705, 'DATABASE_MIGRATION_FILE_LOCATION', 'SYSTEM_CONFIGURATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1706, 'ENABLE_CAPTCHAS', 'SYSTEM_CONFIGURATION', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1600, 'MSP_PAONAME_ALIAS', 'MULTISPEAK', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1601, 'MSP_PRIMARY_CB_VENDORID', 'MULTISPEAK', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1602, 'MSP_BILLING_CYCLE_PARENT_DEVICEGROUP', 'MULTISPEAK', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1603, 'MSP_LM_MAPPING_SETUP', 'MULTISPEAK', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1604, 'MSP_METER_LOOKUP_FIELD', 'MULTISPEAK', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1605, 'MSP_PAONAME_EXTENSION', 'MULTISPEAK', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1000, 'DISPATCH_MACHINE', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1001, 'DISPATCH_PORT', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1002, 'PORTER_MACHINE', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1003, 'PORTER_PORT', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1004, 'MACS_MACHINE', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1005, 'MACS_PORT', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1006, 'CAP_CONTROL_MACHINE', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1007, 'CAP_CONTROL_PORT', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1008, 'LOADCONTROL_MACHINE', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1009, 'LOADCONTROL_PORT', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1010, 'SMTP_HOST', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1011, 'MAIL_FROM_ADDRESS', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1013, 'STARS_PRELOAD_DATA', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1014, 'WEB_LOGO_URL', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1016, 'NOTIFICATION_HOST', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1017, 'NOTIFICATION_PORT', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1019, 'BATCHED_SWITCH_COMMAND_TOGGLE', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1021, 'BULK_IMPORTER_COMMUNICATIONS_ENABLED', 'SYSTEM', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1402, 'CALL_RESPONSE_TIMEOUT', 'VOICE_SERVER', 'System');
INSERT INTO RolePropToSetting_Temp VALUES (-1403, 'CALL_PREFIX', 'VOICE_SERVER', 'System');
 
CREATE TABLE GlobalSetting (
    GlobalSettingId NUMERIC(18, 0) NOT NULL,
    Name VARCHAR(100) NOT NULL,
    Value VARCHAR(1000) NULL,
    Comments VARCHAR(1000) NULL,
    LastChangedDate DATETIME NULL,
    constraint PK_GlobalSetting primary key (GlobalSettingId)
);

CREATE UNIQUE INDEX Indx_GlobalSetting_Name_UNQ ON GlobalSetting (
    Name ASC
);
GO

/* Identify if we are going to have unique index violations in the GlobalSettings table */
/* @start-block */
DECLARE 
    @DistinctDuplicateSets NUMERIC;
BEGIN
    SELECT @DistinctDuplicateSets = COUNT(*)
    FROM (SELECT GroupId, RoleId, RolePropertyId, COUNT(RolePropertyId) AS DuplicateCount 
          FROM YukonGroupRole 
          WHERE GroupId=-1
          GROUP BY GroupId, RoleId, RolePropertyId) T
    WHERE T.DuplicateCount > 1;
    
    IF 0 < @DistinctDuplicateSets
    BEGIN
        RAISERROR('There are duplicate role property values for one or more role properties specified in the Yukon Grp role group. In order to continue, these duplicate conflicts must be resolved.  Please see YUK-11481 for an explanation of this process and for the queries needed to delete the duplicate values.', 16, 1);
    END
END;
/* @end-block */

/* @start-block */
BEGIN
    DECLARE 
        @maxId NUMERIC; 
    BEGIN
        SELECT @maxId = ISNULL(MAX(GlobalSettingId)+1,0)
        FROM GlobalSetting YS; 

        INSERT INTO GlobalSetting (GlobalSettingId, Name, Value)
           (SELECT @maxId + ROW_NUMBER() OVER (ORDER BY YGR.RoleId, YGR.RolePropertyId), 
            RolepropertyEnum, 
            CASE 
                WHEN Value IS NULL THEN DefaultValue
                WHEN LTRIM(RTRIM(Value)) = '' THEN DefaultValue
                WHEN Value = '(none)' THEN DefaultValue
                ELSE Value
            END 
            FROM YukonGroupRole YGR JOIN YukonRoleProperty YRP ON YGR.RolePropertyID = YRP.RolePropertyID
            JOIN RolePropToSetting_Temp T ON T.rolepropertyid = YRP.rolepropertyid
            WHERE GroupID = -1
            AND YRP.RoleID in (-4, -6, -104, -8, -7, -5, -105, -1));
    END;
END;
/* @end-block */

DROP TABLE RolePropToSetting_Temp;

DELETE FROM YukonGroupRole WHERE RoleID IN (-4, -6, -104, -8, -7, -5, -105, -1);

DELETE FROM YukonRoleProperty WHERE RoleID IN (-4, -6, -104, -8, -7, -5, -105, -1);

DELETE FROM YukonRole WHERE RoleID IN (-4, -6, -104, -8, -7, -5, -105, -1);

/* If there are no remaining role properties specified for the 'Yukon Grp' role group,
 * then 'Yukon Grp' should be removed from the YukonGroup table as it is no longer needed.
 *
 * If any role properties remain and the following DELETE statement is not executed, run 
 *     SELECT * FROM YukonGroupRole WHERE GroupId = -1;
 * to discover which role properties still exist.  They can be deleted using queries
 * provided in YUK-11481. 
 */
/* @start-block */
IF 0 = (SELECT COUNT(*) FROM YukonGroupRole where GroupID = -1)
BEGIN
    DELETE FROM YukonGroup WHERE GroupID = -1;
END;
/* @end-block */
/* End YUK-11481 */

/* Start YUK-11522 */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Power Factor Correction', '0.02', 'BANDWIDTH'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Power Factor Correction', '20.0', 'COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Power Factor Correction', '2.0', 'MAX_COST'
FROM CapControlStrategy 
WHERE ControlUnits = 'INTEGRATED_VOLT_VAR';
/* End YUK-11522 */

/* Start YUK-11532 */
INSERT INTO YukonRoleProperty VALUES (-20220, -202, 'Allow Disconnect Control', 'true', 'Controls access to Disconnect, Connect, and Arm operations.');
/* End YUK-11532 */

/* Start YUK-11550 */
INSERT INTO YukonRoleProperty VALUES (-90043, -900, 'Allow DR Control', 'true', 'Allow control of demand response control areas, scenarios, programs and groups');
/* End YUK-11550 */

/* Start YUK-11542 */
UPDATE YukonRoleProperty
SET DefaultValue = 'false'
WHERE RolePropertyID = -20152;

UPDATE YukonGroupRole
SET Value = 'false' 
WHERE LOWER(Value) != 'true' AND RolePropertyID = -20152;
/* End YUK-11542 */

/* Start YUK-11390 */
/* Identify if we are going to have unique index violations with User Group names. */
/* @start-block */
DECLARE 
    @NumberOfDuplicateUserGroups NUMERIC;
BEGIN
    SELECT @NumberOfDuplicateUserGroups = COUNT(*)
    FROM (SELECT Name, COUNT(Name) AS NumberOfDuplicateUserGroups 
          FROM UserGroup 
          GROUP BY Name) T
    WHERE T.NumberOfDuplicateUserGroups > 1;
    
    IF 0 < @NumberOfDuplicateUserGroups
    BEGIN
        RAISERROR('The database currently has multiple User Groups that have identical names.  Going forward this will no longer be possible.  Please see YUK-11390 for details on how to identify duplicate names and change them to remove the conflict(s).The database contains multiple User Groups that have identical names.  Going forward this will no longer be possible.  Please see YUK-11390 for details on how to identify duplicate names and change them to remove the conflict.', 16, 1);
    END
END;

CREATE UNIQUE INDEX Indx_UserGroup_Name_UNQ on UserGroup (
    Name ASC
);
/* End YUK-11390 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
