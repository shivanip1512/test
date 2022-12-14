/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11429 */
ALTER TABLE Regulator
ADD VoltChangePerTap FLOAT;

UPDATE Regulator
SET VoltChangePerTap = 0.75;

ALTER TABLE Regulator
MODIFY VoltChangePerTap FLOAT NOT NULL;
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
    RolePropertyId        NUMBER         NOT NULL,
    RolePropertyEnum      VARCHAR2(100)  NOT NULL,
    RoleEnum              VARCHAR2(50)   NOT NULL,
    RoleCategory          VARCHAR2(50)   NOT NULL,
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

/* Creating GlobalSetting */
CREATE TABLE GlobalSetting (
    GlobalSettingId            NUMBER                 NOT NULL,
    Name                       VARCHAR2(100)          NOT NULL,
    Value                      VARCHAR2(1000)         NULL,
    Comments                   VARCHAR2(1000)         NULL,
    LastChangedDate            DATE                   NULL,
    CONSTRAINT PK_GlobalSetting primary key (GlobalSettingId)
);

CREATE UNIQUE INDEX Indx_GlobalSetting_Name_UNQ ON GlobalSetting (
   Name ASC
);

/* Identify if we are going to have unique index violations in the GlobalSettings table */
/* @start-block */
DECLARE 
    v_DistinctDuplicateSets NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_DistinctDuplicateSets
    FROM (SELECT GroupId, RoleId, RolePropertyId, COUNT(RolePropertyId) AS DuplicateCount 
          FROM YukonGroupRole 
          WHERE GroupId=-1
          GROUP BY  GroupId, RoleId, RolePropertyId
          ORDER BY COUNT(RolePropertyId) ASC) T
    WHERE T.DuplicateCount > 1;
    
    IF 0 < v_DistinctDuplicateSets THEN
      RAISE_APPLICATION_ERROR(-20001, 'There are duplicate role property values for one or more role properties specified in the Yukon Grp role group. In order to continue, these duplicate conflicts must be resolved.  Please see YUK-11481 for an explanation of this process and for the queries needed to delete the duplicate values.');
    END IF;
END;
/
/* @end-block */

/* Migrating data into the GlobalSetting table */
/* @start-block */
DECLARE
    v_maxId NUMBER;
BEGIN

    SELECT NVL(MAX(GlobalSettingId)+1,0) INTO v_maxId
    FROM GlobalSetting YS;
    
    INSERT INTO GlobalSetting (GlobalSettingId, Name, Value)
       SELECT v_maxId + ROW_NUMBER() OVER (ORDER BY YGR.RoleId, YGR.RolePropertyId), 
            T.RolePropertyEnum AS Name,
            CASE
                WHEN YGR.Value IS NULL THEN YRP.DefaultValue
                WHEN LTRIM(RTRIM(YGR.Value)) IS NULL THEN YRP.DefaultValue
                WHEN YGR.Value = '(none)' THEN YRP.DefaultValue
                ELSE YGR.Value
            END as Value 
        FROM YukonGroupRole YGR 
	        JOIN YukonRoleProperty YRP ON YRP.RolePropertyId = YGR.RolePropertyId
	        JOIN RolePropToSetting_Temp T ON T.RolePropertyId = YRP.RolePropertyId
        WHERE YGR.GroupId = -1
          AND YRP.RoleId IN (-4, -6, -104, -8, -7, -5, -105, -1);
END;
/
/* @end-block */

DROP TABLE RolePropToSetting_Temp;

DELETE FROM YukonGroupRole WHERE RoleId IN (-4, -6, -104, -8, -7, -5, -105, -1);

DELETE FROM YukonRoleProperty WHERE RoleId IN (-4, -6, -104, -8, -7, -5, -105, -1);

DELETE FROM YukonRole WHERE RoleId IN (-4, -6, -104, -8, -7, -5, -105, -1);
 
/* If there are no remaining role properties specified for the 'Yukon Grp' role group,
 * then 'Yukon Grp' should be removed from the YukonGroup table as it is no longer needed.
 *
 * If any role properties remain and the following DELETE statement is not executed, run 
 *     SELECT * FROM YukonGroupRole WHERE GroupId = -1;
 * to discover which role properties still exist.  They can be deleted using queries
 * provided in YUK-11481. 
 */
/* @start-block */
DECLARE
  v_RemainingYukonGrpRPs NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_RemainingYukonGrpRPs FROM YukonGroupRole WHERE GroupId = -1;
  IF 0 = v_RemainingYukonGrpRPs THEN 
      DELETE FROM YukonGroup WHERE GroupId = -1;
  END IF;
END;
/
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
/* Identify if we are going to have unique index violations in the GlobalSettings table */
/* @start-block */
DECLARE 
    v_NumberOfDuplicateUserGroups NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_NumberOfDuplicateUserGroups
    FROM (SELECT Name, COUNT(Name) AS NumberOfDuplicateUserGroups 
          FROM UserGroup 
          GROUP BY Name) T
    WHERE T.NumberOfDuplicateUserGroups > 1;
    
    IF 0 < v_NumberOfDuplicateUserGroups THEN
      RAISE_APPLICATION_ERROR(-20001, 'The database currently has multiple User Groups that have identical names.  Going forward this will no longer be possible.  Please see YUK-11390 for details on how to identify duplicate names and change them to remove the conflict(s).');
    END IF;
END;
/
/* @end-block */

CREATE UNIQUE INDEX Indx_UserGroup_Name_UNQ on UserGroup (
    Name ASC
);
/* End YUK-11390 */

/* Start YUK-11493 */
DELETE FROM YukonServices WHERE ServiceID = 19;
/* End YUK-11493 */

/* Start YUK-11567 */
CREATE TABLE ReportedAddressSep (
   ChangeId                  NUMBER              NOT NULL,
   DeviceId                  NUMBER              NOT NULL,
   Timestamp                 DATE                NOT NULL,
   UtilityEnrollmentGroup    NUMBER              NOT NULL,
   RandomStartTimeMinutes    NUMBER              NOT NULL,
   RandomStopTimeMinutes     NUMBER              NOT NULL,
   DeviceClass               NUMBER              NOT NULL,
   CONSTRAINT PK_ReportedAddressSep PRIMARY KEY (ChangeId)
);
 
ALTER TABLE ReportedAddressSep
   ADD CONSTRAINT FK_ReportedAddressSep_Device FOREIGN KEY (DeviceId)
      REFERENCES DEVICE (DEVICEID)
         ON DELETE CASCADE;
/* End YUK-11567 */

/* Start YUK-11553 */
UPDATE YukonRoleProperty SET DefaultValue = 'stars' WHERE RolePropertyId = -1111;

/* @error warn-once */
/* @start-block */
DECLARE
    starsMctPaoCount INT;
BEGIN
    SELECT COUNT(*) INTO starsMctPaoCount
    FROM InventoryBase 
    WHERE CategoryID = 1033;
 
    IF 0 < starsMctPaoCount THEN
        RAISE_APPLICATION_ERROR(-20001, 'Your system currently has Yukon meters within STARS. If you are using STARS to track Yukon meters, then after upgrade is complete, log in to Yukon and set EnergyCompany > z_meter_mct_base_desig role property to ''yukon''. Reference YUK-11553');
    END IF;
END;
/
/* @end-block */
/* End YUK-11553 */

/* Start YUK-11606 */
/* @error ignore-begin */
DROP INDEX Indx_YkUsIDNm;
DROP INDEX Indx_YukonUser_Username;

CREATE UNIQUE INDEX Indx_YukonUser_Username_UNQ on YukonUser (
   UserName ASC
);

CREATE INDEX Indx_YukonUser_Username_FB ON YukonUser (
    LOWER(Username)
);
/* @error ignore-end */
/* End YUK-11606 */

/* Start YUK-11588 */
ALTER TABLE PointControl
    DROP CONSTRAINT FK_PointCont_Point;
 
ALTER TABLE PointControl
    ADD CONSTRAINT FK_PointCont_Point FOREIGN KEY(PointId)
        REFERENCES Point(PointId)
            ON DELETE CASCADE;
/* End YUK-11588 */

/* Start YUK-11603 */
ALTER TABLE PointStatusControl
    DROP CONSTRAINT FK_PointStatusCont_PointCont;
 
ALTER TABLE PointStatusControl
    ADD CONSTRAINT FK_PointStatusCont_PointCont FOREIGN KEY (PointId)
        REFERENCES PointControl (PointId)
            ON DELETE CASCADE;
/* End YUK-11603 */
            
/* Start YUK-11587 */
CREATE OR REPLACE VIEW CBCConfiguration2_View AS
SELECT YP.PAOName AS CBCName, D.* 
FROM DynamicCCTwoWayCBC D, YukonPAObject YP
WHERE YP.PAObjectId = D.DeviceId;
/* End YUK-11587 */
            
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.5', 'Garrett D', '30-OCT-2012', 'Latest Update', 4 );