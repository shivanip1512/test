/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17045 */
IF( (SELECT COUNT(*) 
     FROM   YukonListEntry 
     WHERE  EntryText = 'LCR-6700(RFN)') = 0 ) 
  INSERT INTO YukonListEntry 
  VALUES      ((SELECT MAX(EntryId) + 1 
                FROM   YukonListEntry 
                WHERE  EntryId < 10000), 
               1005, 
               0, 
               'LCR-6700(RFN)', 
               1337); 
/* End YUK-17045 */

/* Start YUK-17051 */
ALTER TABLE DISPLAY2WAYDATA DROP CONSTRAINT FK_Display2WayData_Display;
ALTER TABLE DISPLAYCOLUMNS DROP CONSTRAINT FK_DisplayColumns_Display;
ALTER TABLE TemplateDisplay DROP CONSTRAINT FK_TemplateDisplay_DISPLAY;
GO

ALTER TABLE DISPLAY2WAYDATA
   ADD CONSTRAINT FK_Display2WayData_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
         ON DELETE CASCADE;

ALTER TABLE DISPLAYCOLUMNS
   ADD CONSTRAINT FK_DisplayColumns_Display FOREIGN KEY (DISPLAYNUM)
      REFERENCES DISPLAY (DISPLAYNUM)
         ON DELETE CASCADE;

ALTER TABLE TemplateDisplay
   ADD CONSTRAINT FK_TemplateDisplay_DISPLAY FOREIGN KEY (DisplayNum)
      REFERENCES DISPLAY (DISPLAYNUM)
         ON DELETE CASCADE;
GO
/* End YUK-17051 */

/* Start YUK-17096 */
DROP INDEX INDX_CCEventLog_PointId ON CCEventLog;
/* End YUK-17096 */

/* Start YUK-17113 */
CREATE TABLE DBUpdates (
   UpdateId             VARCHAR(50)          NOT NULL,
   Version              VARCHAR(10)           NULL,
   InstallDate          DATETIME             NULL,
   CONSTRAINT PK_DBUPDATES PRIMARY KEY (UpdateId)
);
GO
/* End YUK-17113 */

/* Start YUK-17066 */
UPDATE PU SET PU.DECIMALPLACES = 0 
FROM POINT P 
    JOIN YukonPAObject YPO ON P.PAObjectID = YPO.PAObjectID
	JOIN POINTUNIT PU ON PU.POINTID = P.POINTID 
WHERE YPO.Type IN ('RFW-201', 'RFW-205') AND P.POINTTYPE = 'Analog' 
    AND P.POINTOFFSET = 6 AND PU.DECIMALPLACES = 3;
GO
/* End YUK-17066 */

/* Start YUK-17159 */
UPDATE ExtraPaoPointAssignment
SET Attribute = 'VOLTAGE'
WHERE Attribute = 'VOLTAGE_Y';

UPDATE ExtraPaoPointAssignment
SET Attribute = 'SOURCE_VOLTAGE'
WHERE Attribute = 'VOLTAGE_X';

CREATE INDEX INDX_EPPA_PointId ON ExtraPaoPointAssignment (
    PointId ASC
);
GO
/* End YUK-17159 */

/* Start YUK-17166 */
UPDATE YukonGroupRole 
SET Value = 'CREATE' 
WHERE RolePropertyID = -21200;

UPDATE YukonRoleProperty
SET 
    KeyName = 'MACS Scripts', 
    DefaultValue = 'UPDATE', 
    Description = 'Controls the ability to view, start/stop, enable/disable, edit, create, delete for MACS Script.'
WHERE RolePropertyID = -21200;
/* End YUK-17166 */

/* Start YUK-17126 */
CREATE TABLE SmartNotificationSub (
   SubscriptionId       NUMERIC              NOT NULL,
   UserId               NUMERIC              NOT NULL,
   Type                 VARCHAR(30)          NOT NULL,
   Media                VARCHAR(30)          NOT NULL,
   Frequency            VARCHAR(30)          NOT NULL,
   Verbosity            VARCHAR(30)          NOT NULL,
   Recipient            VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_SmartNotificationSub PRIMARY KEY (SubscriptionId)
);

CREATE TABLE SmartNotificationSubParam (
   SubscriptionId       NUMERIC              NOT NULL,
   Name                 VARCHAR(30)          NOT NULL,
   Value                VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_SmartNotificationSubParam PRIMARY KEY (SubscriptionId, Name, Value)
);
GO

ALTER TABLE SmartNotificationSub
   ADD CONSTRAINT FK_SmartNotifSub_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
         ON DELETE CASCADE;

ALTER TABLE SmartNotificationSubParam
   ADD CONSTRAINT FK_SmartNotifSP_SmartNotifS FOREIGN KEY (SubscriptionId)
      REFERENCES SmartNotificationSub (SubscriptionId)
         ON DELETE CASCADE;
GO

CREATE INDEX INDX_SmartNotifSub_UserId_Type ON SmartNotificationSub (
   UserId ASC,
   Type ASC
);
GO
/* End YUK-17126 */

/* Start YUK-17120 */
CREATE TABLE SmartNotificationEvent (
    EventId               NUMERIC         NOT NULL,
    Type                  VARCHAR(30)     NOT NULL,
    Timestamp             DATETIME        NOT NULL,
    GroupProcessTime      DATETIME        NULL,
    ImmediateProcessTime  DATETIME        NULL,
    CONSTRAINT PK_SmartNotificationEvent PRIMARY KEY (EventId)
);
GO

CREATE TABLE SmartNotificationEventParam (
    EventId          NUMERIC         NOT NULL,
    Name             VARCHAR(30)     NOT NULL,
    Value            VARCHAR(100)    NOT NULL,
    CONSTRAINT PK_SmartNotificationEventParam PRIMARY KEY (EventId, Name, Value)
);
GO

ALTER TABLE SmartNotificationEventParam
    ADD CONSTRAINT FK_SmartNotifEP_SmartNotifE FOREIGN KEY (EventId)
        REFERENCES SmartNotificationEvent (EventId)
            ON DELETE CASCADE;
GO
/* End YUK-17120 */

/* Start YUK-17273 */
CREATE INDEX INDX_SmartNotifiEvt_Timestamp ON SmartNotificationEvent (
Timestamp DESC
);
GO
/* End YUK-17273 */

/* Start YUK-17233 */
INSERT INTO CCStrategyTargetSettings  
    SELECT
        C.StrategyID, 
        'Comm Reporting Percentage' AS SettingName, 
        '1' AS SettingValue, 
        'CONSIDER_PHASE' AS SettingType
    FROM CapControlStrategy C
    WHERE C.ControlUnits = 'INTEGRATED_VOLT_VAR';
GO
/* End YUK-17233 */

/* Start YUK-17234 */
ALTER TABLE PointToZoneMapping
    ADD Ignore VARCHAR(1);
GO

UPDATE PointToZoneMapping
    SET Ignore = '0';
GO

ALTER TABLE PointToZoneMapping
    ALTER COLUMN Ignore VARCHAR(1) NOT NULL;
GO
/* End YUK-17234 */

/* Start YUK-17309 */
CREATE TABLE CommandRequestExecRequest (
   CommandRequestExecRequestId  NUMERIC              NOT NULL,
   CommandRequestExecId         NUMERIC              NULL,
   DeviceId                     NUMERIC              NULL,
   CONSTRAINT PK_CommandRequestExecRequest PRIMARY KEY (CommandRequestExecRequestId)
);
GO

ALTER TABLE CommandRequestExecRequest
   ADD CONSTRAINT FK_ComReqExRequest_ComReqExec FOREIGN KEY (CommandRequestExecId)
      REFERENCES CommandRequestExec (CommandRequestExecId)
         ON DELETE CASCADE;
GO

CREATE INDEX INDX_CmdReqExReq_CmdReqExId ON CommandRequestExecRequest (
CommandRequestExecId ASC
);
GO
/* End YUK-17309 */

/* Start YUK-17237 */
UPDATE YukonListEntry SET EntryText = 'LCR-6200 (ExpressCom)' WHERE EntryText = 'LCR-6200(EXPRESSCOM)' AND YukonDefinitionID = 1321;
UPDATE YukonListEntry SET EntryText = 'LCR-6200 (RFN)' WHERE EntryText = 'LCR-6200(RFN)' AND YukonDefinitionID = 1324;
UPDATE YukonListEntry SET EntryText = 'LCR-6200 (ZigBee)' WHERE EntryText = 'LCR-6200(ZIGBEE)' AND YukonDefinitionID = 1320;
UPDATE YukonListEntry SET EntryText = 'LCR-6600 (ExpressCom)' WHERE EntryText = 'LCR-6600(EXPRESSCOM)' AND YukonDefinitionID = 1323;
UPDATE YukonListEntry SET EntryText = 'LCR-6600 (ZigBee)' WHERE EntryText = 'LCR-6600(ZIGBEE)' AND YukonDefinitionID = 1322;
UPDATE YukonListEntry SET EntryText = 'LCR-6600 (RFN)' WHERE EntryText = 'LCR-6600(RFN)' AND YukonDefinitionID = 1325;
UPDATE YukonListEntry SET EntryText = 'LCR-6700 (RFN)' WHERE EntryText = 'LCR-6700(RFN)' AND YukonDefinitionID = 1337;
UPDATE YukonListEntry SET EntryText = 'LCR-5000 (VERSACOM)' WHERE EntryText = 'LCR-5000(VERSACOM)' AND YukonDefinitionID = 1311;
UPDATE YukonListEntry SET EntryText = 'LCR-5000 (ExpressCom)' WHERE EntryText = 'LCR-5000(EXPRESSCOM)' AND YukonDefinitionID = 1302;
/* End YUK-17237 */

/* Start YUK-17370 */
INSERT INTO Job (Jobid, BeanName, Disabled, JobGroupId) VALUES (-4, 'deviceConfigVerificationJobDefinition', 'N', -4);
INSERT INTO JobScheduledRepeating VALUES (-4, '0 01 0 ? * *');
/* End YUK-17370 */

/* Start YUK-17426 */
/* @start-block */
BEGIN
    DECLARE @MaxDeviceGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE DG.DeviceGroupId < 98)
    DECLARE @RootParentGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE DG.SystemGroupEnum = 'SYSTEM')

    INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
         VALUES(@MaxDeviceGroupId + 1, 'Demand Response', @RootParentGroupId, 'NOEDIT_NOMOD', 'STATIC', GETDATE(), 'DEMAND_RESPONSE')

    INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
        VALUES(@MaxDeviceGroupId + 2, 'Load Groups', @MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'LOAD_GROUPS', GETDATE(), 'LOAD_GROUPS')

    INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
        VALUES(@MaxDeviceGroupId + 3, 'Load Programs', @MaxDeviceGroupId + 1, 'NOEDIT_NOMOD', 'LOAD_PROGRAMS', GETDATE(), 'LOAD_PROGRAMS')
END;
/* @end-block */
/* End YUK-17426 */

/* Start YUK-17002 */
DECLARE @MAX_PID INT = (SELECT MAX(POINTID) FROM POINT)

INSERT INTO POINT
SELECT
    (@MAX_PID) + ROW_NUMBER() OVER (ORDER BY PT.PointId),
    'Analog',
    'Port Queue Count',
    PAO.PAObjectID,
    'Default',
    0,
    'N',
    'N',
    'R',
    1,
    'On Update',
    0 
FROM YukonPAObject PAO
LEFT JOIN Point PT
    ON  PT.PAObjectID = PAO.PAObjectID
    AND PT.POINTTYPE = 'Analog'
    AND PT.POINTOFFSET = 1
WHERE PAO.PAOClass = 'PORT'
AND PT.PointID IS NULL;

INSERT INTO PointAnalog
    SELECT PT.POINTID, -1, 1, 1
    FROM Point PT
    WHERE PT.POINTNAME = 'Port Queue Count'
    AND NOT EXISTS(SELECT POINTID
                   FROM PointAnalog
                   WHERE PT.POINTID = POINTID
                   AND POINTOFFSET = 1);

INSERT INTO PointUnit 
    SELECT PT.POINTID, 9, 0, 1.0E+30, -1.0E+30, 0
    FROM Point PT
    WHERE PT.POINTNAME = 'Port Queue Count'
    AND NOT EXISTS(SELECT POINTID
                   FROM PointUnit
                   WHERE PT.POINTID = POINTID
                   AND POINTOFFSET = 1);
/* End YUK-17002 */

/* Start YUK-17348 */
CREATE TABLE MaintenanceTask (
   TaskId               NUMERIC(10)          NOT NULL,
   TaskName             VARCHAR(50)          NOT NULL,
   Disabled             CHAR(1)              NOT NULL,
   CONSTRAINT PK_MaintenanceTask PRIMARY KEY (TaskId)
);
GO

CREATE TABLE MaintenanceTaskSettings (
   TaskPropertyId       NUMERIC(10)          NOT NULL,
   TaskId               NUMERIC(10)          NOT NULL,
   Attribute            VARCHAR(50)          NOT NULL,
   Value                VARCHAR(50)          NOT NULL,
   CONSTRAINT PK_MaintenanceTaskSettings PRIMARY KEY (TaskPropertyId)
);
GO

ALTER TABLE MaintenanceTaskSettings
   ADD CONSTRAINT FK_MTaskSettings_MTask FOREIGN KEY (TaskId)
      REFERENCES MaintenanceTask (TaskId)
         ON DELETE CASCADE;
GO
/* End YUK-17348 */

/* Start YUK-17417 */
INSERT INTO DeviceTypeCommand VALUES (-1267, -30, 'CBC 8020', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1268, -31, 'CBC 8020', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1269, -173, 'CBC 8020', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1270, -30, 'CBC 8024', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1271, -31, 'CBC 8024', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1272, -173, 'CBC 8024', 11, 'Y', -1);
/* End YUK-17417 */

/* Start YUK-17498 */
DROP INDEX Indx_CmdReqExec_ContId ON CommandRequestExec;
DROP INDEX INDX_CmdReqExReq_CmdReqExId ON CommandRequestExecRequest;
DROP INDEX Indx_CmdReqExecRes_ExecId_ErrC ON CommandRequestExecResult;
GO
                  
                                                                                                   
CREATE INDEX INDX_CRE_ContId ON CommandRequestExec (
    CommandRequestExecContextId
);

CREATE INDEX INDX_CRE_ExType_ExId_ExStart ON CommandRequestExec 
(
    CommandRequestExecType, 
    CommandRequestExecId, 
    StartTime
); 

CREATE INDEX INDX_CREReq_ExecId_DevId ON CommandRequestExecRequest (
    CommandRequestExecId, 
    DeviceId
);

CREATE INDEX INDX_CRERes_ExecId_ErrC on CommandRequestExecResult (
    CommandRequestExecId,
    ErrorCode
);

CREATE INDEX INDX_CRERes_ExecId_DevId ON CommandRequestExecResult 
(
    CommandRequestExecId, 
    DeviceId,
    ErrorCode,
    CompleteTime
);
GO


DECLARE @maxId AS numeric( 18,0 ) = COALESCE((SELECT MAX(CommandRequestExecId) FROM CommandRequestExecRequest), 0)
INSERT INTO CommandRequestExecRequest ( CommandRequestExecRequestId, CommandRequestExecId, DeviceId )
SELECT @maxId + ROW_NUMBER() OVER ( ORDER BY cre.CommandRequestExecId ) AS CommandRequestExecRequestId, cre.CommandRequestExecId, res.DeviceId
FROM CommandRequestExec cre
JOIN CommandRequestExecResult res ON cre.CommandRequestExecId = res.CommandRequestExecId 
LEFT JOIN CommandRequestExecRequest req ON res.CommandRequestExecId = req.CommandRequestExecId AND res.DeviceId = req.DeviceId
WHERE cre.CommandRequestExecType IN ( 'GROUP_DEVICE_CONFIG_VERIFY', 'GROUP_DEVICE_CONFIG_SEND', 'GROUP_DEVICE_CONFIG_READ' )
AND req.CommandRequestExecId IS NULL
AND cre.CommandRequestExecId = ( SELECT MAX(cre2.CommandRequestExecId)
                                 FROM CommandRequestExec cre2
                                 JOIN CommandRequestExecResult res2
                                     ON cre2.CommandRequestExecId = res2.CommandRequestExecId
                                 WHERE res.DeviceId = res2.DeviceId
                                 AND cre2.CommandRequestExecType IN ( 'GROUP_DEVICE_CONFIG_VERIFY', 'GROUP_DEVICE_CONFIG_SEND', 'GROUP_DEVICE_CONFIG_READ' ) );
GO

DECLARE @maxId AS numeric( 18,0 ) = COALESCE((SELECT MAX(CommandRequestExecId) FROM CommandRequestExecRequest), 0)
INSERT INTO CommandRequestExecRequest ( CommandRequestExecRequestId, CommandRequestExecId, DeviceId )
SELECT @maxId + ROW_NUMBER() OVER ( ORDER BY cre.CommandRequestExecId ) AS CommandRequestExecRequestId, cre.CommandRequestExecId, res.DeviceId
FROM CommandRequestExec cre
JOIN CommandRequestExecResult res ON cre.CommandRequestExecId = res.CommandRequestExecId 
LEFT JOIN CommandRequestExecRequest req ON res.CommandRequestExecId = req.CommandRequestExecId AND res.DeviceId = req.DeviceId
WHERE cre.CommandRequestExecType = 'GROUP_DEVICE_CONFIG_VERIFY'
AND req.CommandRequestExecId IS NULL
AND cre.CommandRequestExecId = ( SELECT MAX( cre2.CommandRequestExecId )
                                 FROM CommandRequestExec cre2
                                 JOIN CommandRequestExecResult res2
                                     ON cre2.CommandRequestExecId = res2.CommandRequestExecId
                                 WHERE res.DeviceId = res2.DeviceId
                                 AND cre2.CommandRequestExecType = 'GROUP_DEVICE_CONFIG_VERIFY' );
GO
/* End YUK-17498 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, GETDATE());*/