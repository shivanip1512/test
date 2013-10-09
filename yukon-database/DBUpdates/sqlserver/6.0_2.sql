/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12536*/
INSERT INTO YukonRoleProperty VALUES (-90044,-900,'Asset Availability','false','Controls access to view Asset Availability for Scenarios, Control Areas, Programs, and Load Groups.');
/* End YUK-12536*/

/* Start YUK-12484*/
INSERT INTO YukonRoleProperty VALUES(-20020,-200,'Network Manager Access','false','Controls access to Network Manager.');
/* End YUK-12484*/

/* Start YUK-12305 */
ALTER TABLE JobProperty
ALTER COLUMN Value VARCHAR(4000) NOT NULL;
/* End YUK-12305 */

/* Start YUK-12525 */
/* @start-block */
DECLARE @PAObjectId AS NUMERIC;
DECLARE @PaoName AS VARCHAR(60);
DECLARE @Type AS VARCHAR(32);
DECLARE @UserId AS NUMERIC;
DECLARE @PagePath AS VARCHAR(2048);
DECLARE @PageName AS VARCHAR(32);
DECLARE @UserPageId AS NUMERIC;
DECLARE @UserPageParamId AS NUMERIC;
 
DECLARE favorites_curs CURSOR FOR (
    SELECT YPO.PAObjectID, YPO.PaoName, YPO.Type, PF.UserId 
    FROM PAOFavorites PF
    LEFT OUTER JOIN YukonPAObject YPO on 
    YPO.PAObjectID = PF.PAObjectId);
 
OPEN favorites_curs;
FETCH NEXT FROM favorites_curs INTO @PAObjectId, @PaoName, @Type, @UserId;
WHILE @@FETCH_STATUS = 0
BEGIN
    IF @Type LIKE '%GROUP%' 
    BEGIN
        SET @PagePath = '/dr/loadGroup/detail?loadGroupId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'loadGroupDetail';
    END
    IF @Type LIKE '%PROGRAM%'
    BEGIN
        SET @PagePath = '/dr/program/detail?programId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'programDetail';
    END
    IF @Type LIKE '%SCENARIO%'
    BEGIN
        SET @PagePath = '/dr/scenario/detail?scenarioId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'scenarioDetail';
    END
    IF @Type LIKE '%AREA%'
    BEGIN
        SET @PagePath = '/dr/controlArea/detail?controlAreaId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'controlAreaDetail';
    END
 
    SET @UserPageId = (SELECT ISNULL(MAX(UserPageId) + 1, 1) FROM UserPage);
    SET @UserPageParamId = (SELECT ISNULL(MAX(UserPageParamId) + 1, 1) FROM UserPageParam);
 
    INSERT INTO UserPage
    VALUES (@UserPageId, @UserId, @PagePath, 'dr', @PageName, 1, GETDATE());
 
    INSERT INTO UserPageParam
    VALUES (@UserPageParamId, @UserPageId, 0, @PAOName);
 
    FETCH NEXT FROM favorites_curs INTO @PAObjectId, @PaoName, @Type, @UserId;
END
CLOSE favorites_curs;
DEALLOCATE favorites_curs;
/* @end-block */

DROP TABLE PAOFavorites;
DROP TABLE PAORecentViews;
/* End YUK-12525 */

/* Start YUK-12602 */
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE GroupName LIKE '%/Tou Rate A Peak';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE GroupName LIKE '%/Tou Rate B Peak';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE GroupName LIKE '%/Tou Rate C Peak';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE GroupName LIKE '%/Tou Rate D Peak';
 
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Usage', '/Usage Rate A') WHERE GroupName LIKE '%/Tou Rate A Usage';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Usage', '/Usage Rate B') WHERE GroupName LIKE '%/Tou Rate B Usage';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Usage', '/Usage Rate C') WHERE GroupName LIKE '%/Tou Rate C Usage';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Usage', '/Usage Rate D') WHERE GroupName LIKE '%/Tou Rate D Usage';
 
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Energy Generated', '/Received kWh') WHERE GroupName LIKE '%/Energy Generated';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE GroupName LIKE '%/Tou Rate A Energy Generated';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE GroupName LIKE '%/Tou Rate B Energy Generated';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE GroupName LIKE '%/Tou Rate C Energy Generated';
UPDATE DeviceDataMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE GroupName LIKE '%/Tou Rate D Energy Generated';
 
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE GroupName LIKE '%/Tou Rate A Peak';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE GroupName LIKE '%/Tou Rate B Peak';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE GroupName LIKE '%/Tou Rate C Peak';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE GroupName LIKE '%/Tou Rate D Peak';
 
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate A Usage', '/Usage Rate A') WHERE GroupName LIKE '%/Tou Rate A Usage';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate B Usage', '/Usage Rate B') WHERE GroupName LIKE '%/Tou Rate B Usage';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate C Usage', '/Usage Rate C') WHERE GroupName LIKE '%/Tou Rate C Usage';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate D Usage', '/Usage Rate D') WHERE GroupName LIKE '%/Tou Rate D Usage';
 
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Energy Generated', '/Received kWh') WHERE GroupName LIKE '%/Energy Generated';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE GroupName LIKE '%/Tou Rate A Energy Generated';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE GroupName LIKE '%/Tou Rate B Energy Generated';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE GroupName LIKE '%/Tou Rate C Energy Generated';
UPDATE DeviceGroupComposedGroup SET GroupName = REPLACE(GroupName, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE GroupName LIKE '%/Tou Rate D Energy Generated';
 
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE GroupName LIKE '%/Tou Rate A Peak';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE GroupName LIKE '%/Tou Rate B Peak';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE GroupName LIKE '%/Tou Rate C Peak';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE GroupName LIKE '%/Tou Rate D Peak';
 
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Usage', '/Usage Rate A') WHERE GroupName LIKE '%/Tou Rate A Usage';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Usage', '/Usage Rate B') WHERE GroupName LIKE '%/Tou Rate B Usage';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Usage', '/Usage Rate C') WHERE GroupName LIKE '%/Tou Rate C Usage';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Usage', '/Usage Rate D') WHERE GroupName LIKE '%/Tou Rate D Usage';
 
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Energy Generated', '/Received kWh') WHERE GroupName LIKE '%/Energy Generated';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE GroupName LIKE '%/Tou Rate A Energy Generated';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE GroupName LIKE '%/Tou Rate B Energy Generated';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE GroupName LIKE '%/Tou Rate C Energy Generated';
UPDATE OutageMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE GroupName LIKE '%/Tou Rate D Energy Generated';
 
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE GroupName LIKE '%/Tou Rate A Peak';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE GroupName LIKE '%/Tou Rate B Peak';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE GroupName LIKE '%/Tou Rate C Peak';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE GroupName LIKE '%/Tou Rate D Peak';
 
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Usage', '/Usage Rate A') WHERE GroupName LIKE '%/Tou Rate A Usage';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Usage', '/Usage Rate B') WHERE GroupName LIKE '%/Tou Rate B Usage';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Usage', '/Usage Rate C') WHERE GroupName LIKE '%/Tou Rate C Usage';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Usage', '/Usage Rate D') WHERE GroupName LIKE '%/Tou Rate D Usage';
 
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Energy Generated', '/Received kWh') WHERE GroupName LIKE '%/Energy Generated';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE GroupName LIKE '%/Tou Rate A Energy Generated';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE GroupName LIKE '%/Tou Rate B Energy Generated';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE GroupName LIKE '%/Tou Rate C Energy Generated';
UPDATE PorterResponseMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE GroupName LIKE '%/Tou Rate D Energy Generated';
 
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE GroupName LIKE '%/Tou Rate A Peak';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE GroupName LIKE '%/Tou Rate B Peak';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE GroupName LIKE '%/Tou Rate C Peak';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE GroupName LIKE '%/Tou Rate D Peak';
 
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Usage', '/Usage Rate A') WHERE GroupName LIKE '%/Tou Rate A Usage';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Usage', '/Usage Rate B') WHERE GroupName LIKE '%/Tou Rate B Usage';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Usage', '/Usage Rate C') WHERE GroupName LIKE '%/Tou Rate C Usage';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Usage', '/Usage Rate D') WHERE GroupName LIKE '%/Tou Rate D Usage';
 
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Energy Generated', '/Received kWh') WHERE GroupName LIKE '%/Energy Generated';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE GroupName LIKE '%/Tou Rate A Energy Generated';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE GroupName LIKE '%/Tou Rate B Energy Generated';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE GroupName LIKE '%/Tou Rate C Energy Generated';
UPDATE StatusPointMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE GroupName LIKE '%/Tou Rate D Energy Generated';
 
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE GroupName LIKE '%/Tou Rate A Peak';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE GroupName LIKE '%/Tou Rate B Peak';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE GroupName LIKE '%/Tou Rate C Peak';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE GroupName LIKE '%/Tou Rate D Peak';
 
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Usage', '/Usage Rate A') WHERE GroupName LIKE '%/Tou Rate A Usage';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Usage', '/Usage Rate B') WHERE GroupName LIKE '%/Tou Rate B Usage';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Usage', '/Usage Rate C') WHERE GroupName LIKE '%/Tou Rate C Usage';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Usage', '/Usage Rate D') WHERE GroupName LIKE '%/Tou Rate D Usage';
 
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Energy Generated', '/Received kWh') WHERE GroupName LIKE '%/Energy Generated';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE GroupName LIKE '%/Tou Rate A Energy Generated';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE GroupName LIKE '%/Tou Rate B Energy Generated';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE GroupName LIKE '%/Tou Rate C Energy Generated';
UPDATE TamperFlagMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE GroupName LIKE '%/Tou Rate D Energy Generated';
 
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE GroupName LIKE '%/Tou Rate A Peak';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE GroupName LIKE '%/Tou Rate B Peak';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE GroupName LIKE '%/Tou Rate C Peak';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE GroupName LIKE '%/Tou Rate D Peak';
 
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Usage', '/Usage Rate A') WHERE GroupName LIKE '%/Tou Rate A Usage';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Usage', '/Usage Rate B') WHERE GroupName LIKE '%/Tou Rate B Usage';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Usage', '/Usage Rate C') WHERE GroupName LIKE '%/Tou Rate C Usage';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Usage', '/Usage Rate D') WHERE GroupName LIKE '%/Tou Rate D Usage';
 
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Energy Generated', '/Received kWh') WHERE GroupName LIKE '%/Energy Generated';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE GroupName LIKE '%/Tou Rate A Energy Generated';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE GroupName LIKE '%/Tou Rate B Energy Generated';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE GroupName LIKE '%/Tou Rate C Energy Generated';
UPDATE ValidationMonitor SET GroupName = REPLACE(GroupName, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE GroupName LIKE '%/Tou Rate D Energy Generated';
 
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_A_PEAK_DEMAND', 'PEAK_DEMAND_RATE_A') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_A_PEAK_DEMAND%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate A Peak%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate A Peak', '/Peak Demand Rate A') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate A Peak%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_B_PEAK_DEMAND', 'PEAK_DEMAND_RATE_B') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_B_PEAK_DEMAND%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate B Peak%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate B Peak', '/Peak Demand Rate B') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate B Peak%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_C_PEAK_DEMAND', 'PEAK_DEMAND_RATE_C') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_C_PEAK_DEMAND%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate C Peak%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate C Peak', '/Peak Demand Rate C') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate C Peak%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_D_PEAK_DEMAND', 'PEAK_DEMAND_RATE_D') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_D_PEAK_DEMAND%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate D Peak%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate D Peak', '/Peak Demand Rate D') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate D Peak%';
 
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_A_USAGE', 'USAGE_RATE_A') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_A_USAGE%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate A Usage', '/Usage Rate A') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate A Usage%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate A Usage', '/Usage Rate A') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate A Usage%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_B_USAGE', 'USAGE_RATE_B') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_B_USAGE%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate B Usage', '/Usage Rate B') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate B Usage%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate B Usage', '/Usage Rate B') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate B Usage%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_C_USAGE', 'USAGE_RATE_C') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_C_USAGE%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate C Usage', '/Usage Rate C') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate C Usage%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate C Usage', '/Usage Rate C') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate C Usage%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_D_USAGE', 'USAGE_RATE_D') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_D_USAGE%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate D Usage', '/Usage Rate D') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate D Usage%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate D Usage', '/Usage Rate D') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate D Usage%';
 
UPDATE JobProperty SET Value = REPLACE(Value, 'ENERGY_GENERATED', 'RECEIVED_KWH') WHERE Name = 'attributes' AND Value LIKE '%ENERGY_GENERATED%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Energy Generated', '/Received kWh') WHERE Name = 'deviceGroup' AND Value LIKE '%/Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Energy Generated', '/Received kWh') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_A_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_A') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_A_ENERGY_GENERATED%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate A Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate A Energy Generated', '/Received kWh Rate A') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate A Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_B_ENERGY_GENERATED', 'RECEIVED_KWH_RATE_B') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_B_ENERGY_GENERATED%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate B Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate B Energy Generated', '/Received kWh Rate B') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate B Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_C_ENERGY_GENERATED', 'RECEIVED_KWH_C') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_C_ENERGY_GENERATED%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate C Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate C Energy Generated', '/Received kWh Rate C') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate C Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, 'TOU_RATE_D_ENERGY_GENERATED', 'RECEIVED_KWH_D') WHERE Name = 'attributes' AND Value LIKE '%TOU_RATE_D_ENERGY_GENERATED%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE Name = 'deviceGroup' AND Value LIKE '%/Tou Rate D Energy Generated%';
UPDATE JobProperty SET Value = REPLACE(Value, '/Tou Rate D Energy Generated', '/Received kWh Rate D') WHERE Name = 'deviceGroupNames' AND Value LIKE '%/Tou Rate D Energy Generated%';
/* End YUK-12602 */

/* Start YUK-12572 */
CREATE TABLE Theme (
   ThemeId              NUMERIC              NOT NULL,
   Name                 VARCHAR(255)         NOT NULL,
   IsCurrent            BIT                  NOT NULL,
   CONSTRAINT PK_Theme PRIMARY KEY (ThemeId)
)
GO

INSERT INTO Theme VALUES (-1, 'Yukon Gray', 1);

CREATE TABLE ThemeProperty (
   ThemeId              NUMERIC              NOT NULL,
   Property             VARCHAR(255)         NOT NULL,
   Value                VARCHAR(2048)        NOT NULL,
   CONSTRAINT PK_ThemeProperty PRIMARY KEY (ThemeId, Property)
)
GO

ALTER TABLE ThemeProperty
   ADD CONSTRAINT FK_ThemeProperty_Theme foreign key (ThemeId)
      REFERENCES Theme (ThemeId)
         ON DELETE CASCADE;
GO

INSERT INTO ThemeProperty VALUES (-1, 'PAGE_BACKGROUND', '#6e6d71');
INSERT INTO ThemeProperty VALUES (-1, 'PAGE_BACKGROUND_FONT_COLOR', '#ffffff');
INSERT INTO ThemeProperty VALUES (-1, 'PAGE_BACKGROUND_SHADOW', '#5a595d');
INSERT INTO ThemeProperty VALUES (-1, 'PRIMARY_COLOR', '#0066cc');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_FONT_COLOR', '#ffffff');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_FONT_SHADOW', 'rgba(0,0,0,0.5)');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_TAGLINE_MARGIN', '35');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_LEFT', '0');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_TOP', '17');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_WIDTH', '163');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO', 1);
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_BACKGROUND', 2);
/* End YUK-12572 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '16-OCT-2013', 'Latest Update', 2, GETDATE());*/