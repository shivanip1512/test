/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-18076 */
ALTER TABLE Display DROP COLUMN Title;
GO

INSERT INTO DBUpdates VALUES ('YUK-18076', '7.1.0', GETDATE());
/* @end YUK-18076 */

/* @start YUK-18086 if YUK-17960 */
/* YUK-17960 had previously created many names incorrectly.  This will simply re-name them if that YUK has been executed */
exec sp_rename @objname = 'PK_CollectionActionCommandRequest', @newname = 'PK_CACommandRequest';
exec sp_rename @objname = 'FK_CollectionActionCR_CollectionAction', @newname = 'FK_CACR_CollectionAction';
exec sp_rename @objname = 'FK_CollectionActionCR_CommandRequestExec', @newname = 'FK_CACR_CommandRequestExec';
exec sp_rename @objname = 'FK_CollectionActionI_CollectionAction', @newname = 'FK_CAInput_CollectionAction';
exec sp_rename @objname = 'FK_CollectionActionR_CollectionAction', @newname = 'FK_CARequest_CollectionAction';
exec sp_rename @objname = 'FK_CollectionActionR_YukonPAObject', @newname = 'FK_CARequest_YukonPAObject';

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', GETDATE());
/* @end YUK-18086 */

/* @start YUK-18086 */
/* If that YUK has not been executed, create the tables from scratch with the correct names already set */
CREATE TABLE CollectionAction (
    CollectionActionId  NUMERIC             NOT NULL,
    Action              VARCHAR(50)         NOT NULL,
    StartTime           DATETIME            NOT NULL,
    StopTime            DATETIME            NULL,
    Status              VARCHAR(50)         NOT NULL,
    UserName            VARCHAR(100)        NOT NULL,
    CONSTRAINT PK_CollectionAction PRIMARY KEY (CollectionActionId)
);

CREATE TABLE CollectionActionCommandRequest (
    CollectionActionId      NUMERIC         NOT NULL,
    CommandRequestExecId    NUMERIC         NOT NULL,
    CONSTRAINT PK_CACommandRequest PRIMARY KEY (CollectionActionId, CommandRequestExecId)
);

CREATE TABLE CollectionActionInput (
    CollectionActionId  NUMERIC             NOT NULL,
    InputOrder          NUMERIC             NOT NULL,
    Description         VARCHAR(50)         NOT NULL,
    Value               VARCHAR(1000)       NOT NULL,
    CONSTRAINT PK_CollectionActionInput PRIMARY KEY (CollectionActionId, InputOrder)
);

CREATE TABLE CollectionActionRequest (
    CollectionActionRequestId   NUMERIC     NOT NULL,
    CollectionActionId          NUMERIC     NOT NULL,
    PAObjectId                  NUMERIC     NOT NULL,
    Result                      VARCHAR(50) NOT NULL,
    CONSTRAINT PK_CollectionActionRequest PRIMARY KEY (CollectionActionRequestId)
);
GO

CREATE INDEX INDX_Car_CollectionActionId ON CollectionActionRequest (
    CollectionActionId ASC
);
GO

ALTER TABLE CollectionActionCommandRequest
    ADD CONSTRAINT FK_CACR_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionCommandRequest
    ADD CONSTRAINT FK_CACR_CommandRequestExec FOREIGN KEY (CommandRequestExecId)
        REFERENCES CommandRequestExec (CommandRequestExecId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionInput
    ADD CONSTRAINT FK_CAInput_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionRequest
    ADD CONSTRAINT FK_CARequest_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionRequest
    ADD CONSTRAINT FK_CARequest_YukonPAObject FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectID)
            ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', GETDATE());
/* @end YUK-18086 */

/* @start YUK-18122 */
UPDATE YukonRoleProperty SET Description = 'Controls access to view Control Area Trigger related information.'
    WHERE RolePropertyId = -90021;
GO

INSERT INTO DBUpdates VALUES ('YUK-18122', '7.1.0', GETDATE());
/* @end YUK-18122 */

/* @start YUK-18164 */
UPDATE RfnAddress SET Model='Sentinel-L0' WHERE Model='SENTINEL-L0';
UPDATE RfnAddress SET Model='Sentinel-L1' WHERE Model='SENTINEL-L1';
UPDATE RfnAddress SET Model='Sentinel-L2' WHERE Model='SENTINEL-L2';
UPDATE RfnAddress SET Model='Sentinel-L3' WHERE Model='SENTINEL-L3';
UPDATE RfnAddress SET Model='Sentinel-L4' WHERE Model='SENTINEL-L4';

UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L0' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L0';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L1' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L1';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L2' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L2';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L3' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L3';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L4' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L4';
GO

INSERT INTO DBUpdates VALUES ('YUK-18164', '7.1.0', GETDATE());
/* @end YUK-18164 */

/* @start YUK-18280 */
ALTER TABLE DeviceDataMonitorProcessor
    ADD ProcessorType VARCHAR(25) NULL,
        ProcessorValue FLOAT NULL,
        RangeMin FLOAT NULL,
        RangeMax FLOAT NULL;
GO

UPDATE DeviceDataMonitorProcessor
SET ProcessorValue = State;
GO

ALTER TABLE DeviceDataMonitorProcessor
DROP COLUMN State;
GO

UPDATE DeviceDataMonitorProcessor
SET ProcessorType = 'STATE';
GO

ALTER TABLE DeviceDataMonitorProcessor
    ALTER COLUMN ProcessorType VARCHAR(25) NOT NULL;
GO

ALTER TABLE DeviceDataMonitorProcessor
    ALTER COLUMN StateGroupId NUMERIC NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-18280', '7.1.0', GETDATE());
/* @end YUK-18280 */

/* @start YUK-18091 */
UPDATE UserPage
SET 
    PagePath = '/collectionActions/recentResults', 
    Module   = 'tools', 
    PageName = 'collectionActions.recentResults'
WHERE PagePath IN ('/common/commandRequestExecutionResults/list',
                   '/group/commander/resultList',
                   '/bulk/disconnect/recentResults',
                   '/group/groupMeterRead/resultsList',
                   '/bulk/demand-reset/recent-results');

UPDATE UserPage
SET 
    PagePath = '/collectionActions/home', 
    Module   = 'tools', 
    PageName = 'collectionActions.home'
WHERE PagePath IN ('/bulk/deviceSelection', 
                   '/bulk/deviceSelectionGetDevices');

DELETE FROM UserPage
WHERE PageName IN (
    'bulk.addPointsResults',
    'bulk.changeDeviceTypeResults',
    'bulk.connect.results',
    'bulk.disconnect.results',
    'bulk.arm.results',
    'bulk.massChangeResults',
    'bulk.massDeleteResults',
    'bulk.removePointsResults',
    'bulk.updatePointsResults',
    'bulk.assignConfigResults',
    'bulk.unassignConfigResults',
    'groupMeterRead.resultDetail',
    'groupCommand.details',
    'bulk.demandReset.results',
    'bulk.routeLocateResults',
    'bulk.home');

INSERT INTO DBUpdates VALUES ('YUK-18091', '7.1.0', GETDATE());
/* @end YUK-18091 */

/* @start YUK-18323 */
/* First delete any non-favorites where duplicate favorites exist */
DELETE P1
FROM UserPage AS P1
WHERE P1.UserPageId IN
(
    SELECT P1.UserPageId
    FROM UserPage P2
    WHERE P1.PagePath = P2.PagePath
    AND P1.UserId = P2.UserId
    AND P1.UserPageId != P2.UserPageId
    AND P1.Favorite = 0
    AND P2.Favorite = 1
);
GO

/* Then delete all remaining, older duplicates (or the lower UserPageId if LastAccess is equal) */
DELETE P1
FROM UserPage AS P1
WHERE P1.UserPageId IN
(
    SELECT P1.UserPageId
    FROM UserPage P2
    WHERE P1.PagePath = P2.PagePath
    AND P1.UserId = P2.UserId
    AND P1.UserPageId != P2.UserPageId
    AND
    (
        ( 
            P2.LastAccess > P1.LastAccess
        ) 
        OR
        (
            P2.LastAccess = P1.LastAccess
            AND P2.UserPageId > P1.UserPageId
        )
    )
);

INSERT INTO DBUpdates VALUES ('YUK-18323', '7.1.0', GETDATE());
/* @end YUK-18323 */

/* @start YUK-18349 */
CREATE INDEX INDX_DGMember_YukonPaoId ON DEVICEGROUPMEMBER ( YukonPaoId ASC );

INSERT INTO DBUpdates VALUES ('YUK-18349', '7.1.0', GETDATE());
/* @end YUK-18349 */

/* @start YUK-18333 */
UPDATE DmvTest
SET PollingInterval = 15
WHERE PollingInterval < 15;

INSERT INTO DBUpdates VALUES ('YUK-18333', '7.1.0', GETDATE());
/* @end YUK-18333 */

/* @start YUK-18391 */
EXEC sp_rename 'DmvTest.PollingInterval', 'DataArchivingInterval', 'COLUMN';
EXEC sp_rename 'DmvTest.DataGatheringDuration', 'IntervalDataGatheringDuration', 'COLUMN';

INSERT INTO DBUpdates VALUES ('YUK-18391', '7.1.0', GETDATE());
/* @end YUK-18391 */

/* @start YUK-18379 */
UPDATE EventLog 
SET String4 = String2, String2 = ' '
WHERE EventType = 'commander.groupCommandInitiated'
AND String4 IS NULL;

UPDATE EventLog 
SET String3 = String1, String1 = 'Send Command', String4 = 'Completed', String5 = String2, String2 = ' '
WHERE EventType = 'commander.groupCommandCompleted'
AND String3 IS NULL;

UPDATE EventLog 
SET String5 = String1, String4 = String2, String2 = ' ', String1 = 'Send Command', String3 = ' '
WHERE EventType = 'commander.groupCommandCancelled'
AND String3 IS NULL;

UPDATE EventLog 
SET EventType = 'amr.demand.reset.demandResetToDeviceInitiated' 
WHERE EventType = 'amr.demand.reset.demandResetInitiated'
AND Int7 IS NULL;

UPDATE EventLog 
SET EventType = 'amr.demand.reset.demandResetByApiCompleted' 
WHERE EventType = 'amr.demand.reset.demandResetCompleted'
AND String2 IS NULL;

UPDATE EventLog 
SET String4 = String2, String2 = ' '
WHERE EventType = 'device.configuration.sendConfigInitiated'
AND String4 IS NULL;

UPDATE EventLog 
SET String3 = String1, String1 = 'Send Config', String4 = 'Completed', String5 = String2, String2 = ' '
WHERE EventType = 'device.configuration.sendConfigCompleted'
AND String3 IS NULL;

UPDATE EventLog 
SET String4 = String1, String1 = 'Read Config', String3 = String2, String2 = ' '
WHERE EventType = 'device.configuration.readConfigInitiated'
AND String3 IS NULL;

UPDATE EventLog 
SET String3 = String1, String1 = 'Read Config', String4 = 'Completed', String5 = String2, String2 = ' '
WHERE EventType = 'device.configuration.readConfigCompleted'
AND String3 IS NULL;

UPDATE EventLog 
SET String4 = String1, String1 = 'Verify Config', String3 = String2, String2 = ' '
WHERE EventType = 'device.configuration.verifyConfigInitiated'
AND String3 IS NULL;

UPDATE EventLog 
SET String3 = String1, String1 = 'Verify Config', String4 = 'Completed', String5 = String2, String2 = ' '
WHERE EventType = 'device.configuration.verifyConfigCompleted'
AND String3 IS NULL;

UPDATE EventLog 
SET String2 = String1, String1 = 'Assign Config', String3 = String2, String4 = ' '
WHERE EventType = 'device.configuration.assignConfigInitiated'
AND String3 IS NULL;

UPDATE EventLog 
SET String3 = String1, String1 = 'Unassign Config', String2 = ' ', String4 = ' '
WHERE EventType = 'device.configuration.unassignConfigInitiated'
AND String3 IS NULL;

INSERT INTO DBUpdates VALUES ('YUK-18379', '7.1.0', GETDATE());
/* @end YUK-18379 */

/* @start YUK-18432 */
BEGIN
    DECLARE @MaxDeviceGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE DG.DeviceGroupId < 100)

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 1, 'All RFG Meters', (
SELECT DG.DeviceGroupId FROM DeviceGroup DG WHERE SystemGroupEnum = 'ALL_RFN_METERS'), 'NOEDIT_NOMOD', 'METERS_ALL_RFG_METERS', GETDATE(), 'ALL_RFG_METERS')
END;

INSERT INTO DBUpdates VALUES ('YUK-18432', '7.1.0', GETDATE());
/* @end YUK-18432 */

/* @start YUK-18371-1 if YUK-18371 */
EXEC sp_rename 'PaoNote.CreatorUserName', 'CreateUserName', 'COLUMN';
EXEC sp_rename 'PaoNote.CreationDate', 'CreateDate', 'COLUMN';
EXEC sp_rename 'PaoNote.EditorUserName', 'EditUserName', 'COLUMN';

INSERT INTO DBUpdates VALUES ('YUK-18371-1', '7.1.0', GETDATE());
/* @end YUK-18371-1 */

/* @start YUK-18371-1 */
CREATE TABLE PaoNote (
    NoteId              NUMERIC         NOT NULL,
    PaObjectId          NUMERIC         NOT NULL,
    NoteText            NVARCHAR(255)   NOT NULL,
    Status              CHAR(1)         NOT NULL,
    CreateUserName      NVARCHAR(64)    NOT NULL,
    CreateDate          DATETIME        NOT NULL,
    EditUserName        NVARCHAR(64)    NULL,
    EditDate            DATETIME        NULL,
    CONSTRAINT PK_PaoNote PRIMARY KEY (NoteId)
);
GO

CREATE INDEX INDX_PaObjectId_Status ON PaoNote (
    PaObjectId ASC,
    Status ASC
);

ALTER TABLE PaoNote
   ADD CONSTRAINT FK_PaoNote_YukonPAObject FOREIGN KEY (PaObjectId)
      REFERENCES YukonPAObject (PAObjectID)
         ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18371-1', '7.1.0', GETDATE());
/* @end YUK-18371-1 */

/* @start YUK-18487 */
/* Create some temporary stored procedures */
/* @start-block */
CREATE PROCEDURE sp_UpdatePointName
    @CurrentPointName nvarchar(200),
    @NewPointName nvarchar(200),
    @PointType nvarchar(200),
    @PointOffset numeric,
    @DeviceType nvarchar(200)
AS
    UPDATE POINT 
    SET POINTNAME = @NewPointName
    WHERE POINTID IN (
        SELECT POINTID 
        FROM POINT P 
        JOIN YukonPAObject Y ON P.PAObjectID = Y.PAObjectID
        WHERE Y.Type = @DeviceType
        AND P.POINTTYPE = @PointType
        AND P.POINTOFFSET = @PointOffset
        AND P.POINTNAME = @CurrentPointName
    );
GO
/* @end-block */

/* @start-block */
CREATE PROCEDURE sp_UpdateAttributeName(
    @CurrentAttributeName nvarchar(200),
    @NewAttributeName nvarchar(200),
    @DeviceType nvarchar(200) )
AS
    UPDATE BehaviorReportValue
    SET Value = @NewAttributeName
    FROM BehaviorReportValue BRV
    JOIN BehaviorReport BR 
        ON BRV.BehaviorReportId = BR.BehaviorReportId
    JOIN YukonPAObject Y 
        ON BR.DeviceId = Y.PAObjectID
    WHERE Y.Type = @DeviceType
    AND BRV.Value = @CurrentAttributeName;
    
    UPDATE BehaviorValue
    SET Value = @NewAttributeName
    FROM BehaviorValue BV 
    JOIN (
        SELECT DISTINCT BV.BehaviorId
        FROM BehaviorValue BV
        JOIN Behavior B 
            ON BV.BehaviorId = B.BehaviorId
        JOIN DeviceBehaviorMap DBM 
            ON B.BehaviorId = DBM.BehaviorId
        JOIN YukonPAObject Y 
            ON DBM.DeviceId = Y.PAObjectID
        WHERE B.BehaviorType = 'DATA_STREAMING'
        AND Y.Type = @DeviceType
    ) SentinelDS ON BV.BehaviorId = SentinelDS.BehaviorId
    WHERE BV.Value = @CurrentAttributeName;
    
    UPDATE DCCI
    SET ItemValue = @NewAttributeName
    FROM DeviceConfigCategoryItem DCCI
    JOIN (
        SELECT DISTINCT DCC.DeviceConfigCategoryId 
        FROM DeviceConfigCategory DCC
        JOIN DeviceConfigCategoryMap DCCM 
            ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId
        JOIN DeviceConfiguration DC 
            ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationID
        JOIN DeviceConfigDeviceTypes DCDT 
            ON DC.DeviceConfigurationID = DCDT.DeviceConfigurationId
        WHERE DCC.CategoryType='rfnChannelConfiguration'
        AND DCDT.PaoType = @DeviceType
    ) SentinelDC ON DCCI.DeviceConfigCategoryId = SentinelDC.DeviceConfigCategoryId
    WHERE DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue = @CurrentAttributeName;

    UPDATE ArchiveValuesExportAttribute
    SET AttributeName = @NewAttributeName
    WHERE AttributeName = @CurrentAttributeName;

    UPDATE DeviceDataMonitorProcessor
    SET Attribute = @NewAttributeName
    WHERE Attribute = @CurrentAttributeName;

    UPDATE JOBPROPERTY
    SET value = REPLACE(value, @CurrentAttributeName, @NewAttributeName)
    WHERE name = 'attributes'
    AND value LIKE CONCAT('%', @CurrentAttributeName, '%');
GO
/* @end-block */

/* Delivered Power Factor / DELIVERED_POWER_FACTOR -> Average Power Factor (Quadrants 1 2 4) / AVERAGE_POWER_FACTOR_Q124 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Delivered Power Factor',
    @NewPointName = 'Average Power Factor (Quadrants 1 2 4)',
    @PointType = 'Analog',
    @PointOffset = 172,
    @DeviceType = 'RFN-430A3R';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'DELIVERED_POWER_FACTOR',
    @NewAttributeName = 'AVERAGE_POWER_FACTOR_Q124',
    @DeviceType = 'RFN-430A3R';


/* Sum Power Factor / SUM_POWER_FACTOR -> Average Power Factor / AVERAGE_POWER_FACTOR */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Sum Power Factor',
    @NewPointName = 'Average Power Factor',
    @PointType = 'Analog',
    @PointOffset = 373,
    @DeviceType = 'RFN-430A3R';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'SUM_POWER_FACTOR',
    @NewAttributeName = 'AVERAGE_POWER_FACTOR',
    @DeviceType = 'RFN-430A3R';

EXEC sp_UpdatePointName
    @CurrentPointName = 'Sum Power Factor',
    @NewPointName = 'Average Power Factor',
    @PointType = 'Analog',
    @PointOffset = 373,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'SUM_POWER_FACTOR',
    @NewAttributeName = 'AVERAGE_POWER_FACTOR',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 1 Quadrant 3 kVAr / Q1_Q3_KVAR -> kVAr (Quadrants 1 3) / KVAR_Q13 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 1 Quadrant 3 kVAr',
    @NewPointName = 'kVAr (Quadrants 1 3)',
    @PointType = 'Analog',
    @PointOffset = 363,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q1_Q3_KVAR',
    @NewAttributeName = 'KVAR_Q13',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 2 Quadrant 4 kVAr / Q2_Q4_KVAR -> kVAr (Quadrants 2 4) / KVAR_Q24 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 2 Quadrant 4 kVAr',
    @NewPointName = 'kVAr (Quadrants 2 4)',
    @PointType = 'Analog',
    @PointOffset = 364,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q2_Q4_KVAR',
    @NewAttributeName = 'KVAR_Q24',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 1 Quadrant 4 kVAr / Q1_Q4_KVAR -> kVAr (Quadrants 1 4) / KVAR_Q14 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 1 Quadrant 4 kVAr',
    @NewPointName = 'kVAr (Quadrants 1 4)',
    @PointType = 'Analog',
    @PointOffset = 365,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q1_Q4_KVAR',
    @NewAttributeName = 'KVAR_Q14',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 2 Quadrant 3 kVAr / Q2_Q3_KVAR -> kVAr (Quadrants 2 3) / KVAR_Q23 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 2 Quadrant 3 kVAr',
    @NewPointName = 'kVAr (Quadrants 2 3)',
    @PointType = 'Analog',
    @PointOffset = 366,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q2_Q3_KVAR',
    @NewAttributeName = 'KVAR_Q23',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 1 Quadrant 2 kVA / Q1_Q2_KVA -> kVA (Quadrants 1 2) / KVA_Q12 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 1 Quadrant 2 kVA',
    @NewPointName = 'kVA (Quadrants 1 2)',
    @PointType = 'Analog',
    @PointOffset = 368,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q1_Q2_KVA',
    @NewAttributeName = 'KVA_Q12',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 3 Quadrant 4 kVA / Q3_Q4_KVA -> kVA (Quadrants 3 4) / KVA_Q34 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 3 Quadrant 4 kVA',
    @NewPointName = 'kVA (Quadrants 3 4)',
    @PointType = 'Analog',
    @PointOffset = 369,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q3_Q4_KVA',
    @NewAttributeName = 'KVA_Q34',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 1 Quadrant 3 kVA / Q1_Q3_KVA -> kVA (Quadrants 1 3) / KVA_Q13 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 1 Quadrant 3 kVA',
    @NewPointName = 'kVA (Quadrants 1 3)',
    @PointType = 'Analog',
    @PointOffset = 370,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q1_Q3_KVA',
    @NewAttributeName = 'KVA_Q13',
    @DeviceType = 'RFN-530S4x';

/* Quadrant 2 Quadrant 4 kVA / Q2_Q4_KVA -> kVA (Quadrants 2 4) / KVA_Q24 */
EXEC sp_UpdatePointName
    @CurrentPointName = 'Quadrant 2 Quadrant 4 kVA',
    @NewPointName = 'kVA (Quadrants 2 4)',
    @PointType = 'Analog',
    @PointOffset = 371,
    @DeviceType = 'RFN-530S4x';

EXEC sp_UpdateAttributeName
    @CurrentAttributeName = 'Q2_Q4_KVA',
    @NewAttributeName = 'KVA_Q24',
    @DeviceType = 'RFN-530S4x';
GO

DROP PROCEDURE sp_UpdatePointName;
DROP PROCEDURE sp_UpdateAttributeName;
GO

INSERT INTO DBUpdates VALUES ('YUK-18487', '7.1.0', GETDATE());
/* @end YUK-18487 */

/* @start YUK-18517 */
DELETE FROM PaoNote 
WHERE Status = 'D';

INSERT INTO DBUpdates VALUES ('YUK-18517', '7.1.0', GETDATE());
/* @end YUK-18517 */

/* @start YUK-18477 */
UPDATE YukonRoleProperty 
SET Description = 'Controls the ability to edit, delete, or manually add point data values.' 
WHERE RolePropertyID = -21405
AND RoleID = -214;

UPDATE YukonRoleProperty 
SET Description = 'Controls the ability to view, create, edit, or delete points.' 
WHERE RolePropertyID = -21406
AND RoleID = -214;

INSERT INTO YukonRoleProperty VALUES(-21407, -214, 'Manage Notes', 'OWNER', 'Controls the ability to view, create, edit, or delete notes.');

INSERT INTO DBUpdates VALUES ('YUK-18477', '7.1.0', GETDATE());
/* @end YUK-18477 */

/* @start YUK-18489 */
UPDATE PorterResponseMonitor 
SET Name = 'Default All PLC Meters', GroupName = '/System/Meters/All Meters/All MCT Meters' 
WHERE GroupName = '/' and MonitorId = 1;

UPDATE PorterResponseMonitor
SET GroupName = '/System/Meters/All Meters/All MCT Meters' 
WHERE GroupName = '/';
GO

INSERT INTO DBUpdates VALUES ('YUK-18489', '7.1.0', GETDATE());
/* @end YUK-18489 */

/* @start YUK-18551 */
/* Delete RFW-205 */
DELETE FROM DeviceAddress WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205');

DELETE FROM DeviceCBC WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205');

DELETE FROM DeviceDirectCommSettings WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205');

DELETE FROM DeviceWindow WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205');

DELETE FROM DEVICE WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205');

DELETE FROM POINTSTATUS WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205'));

DELETE FROM PointAlarming WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205'));

DELETE FROM POINTANALOG WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205'));

DELETE FROM POINTUNIT WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205'));

DELETE FROM POINT WHERE PAObjectID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFW-205');

DELETE FROM YukonPAObject WHERE TYPE = 'RFW-205';

DELETE FROM DeviceConfigDeviceTypes WHERE PaoType = 'RFW-205';

DELETE FROM DeviceTypeCommand WHERE DeviceType = 'RFW-205';


/* Delete RFG-205 */
DELETE FROM DeviceAddress WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205');

DELETE FROM DeviceCBC WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205');

DELETE FROM DeviceDirectCommSettings WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205');

DELETE FROM DeviceWindow WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205');

DELETE FROM DEVICE WHERE DEVICEID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205');

DELETE FROM POINTSTATUS WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205'));

DELETE FROM PointAlarming WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205'));

DELETE FROM POINTANALOG WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205'));

DELETE FROM POINTUNIT WHERE POINTID IN
    (SELECT POINTID FROM POINT WHERE PAObjectID IN
        (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205'));

DELETE FROM POINT WHERE PAObjectID IN
    (SELECT Y.PAObjectID FROM YukonPAObject Y WHERE Y.type = 'RFG-205');

DELETE FROM YukonPAObject WHERE TYPE = 'RFG-205';

DELETE FROM DeviceConfigDeviceTypes WHERE PaoType = 'RFG-205';

DELETE FROM DeviceTypeCommand WHERE DeviceType = 'RFG-205';


INSERT INTO DBUpdates VALUES ('YUK-18551', '7.1.0', GETDATE());
/* @end YUK-18551 */

/* @start YUK-18526 */
BEGIN
    DECLARE @MaxDeviceGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE DG.DeviceGroupId < 100)
    DECLARE @RootParentGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE SystemGroupEnum = 'METERS')

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 1, 'CIS DeviceClass', @RootParentGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'CIS_DEVICECLASS')
END;
GO

INSERT INTO DBUpdates VALUES ('YUK-18526', '7.1.0', GETDATE());
/* @end YUK-18526 */

/* @start YUK-18095 */
/* Remove invalid points */
DELETE FROM DynamicPointDispatch
WHERE PointId IN (
    SELECT PointId FROM Point p JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
    WHERE Type IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
    AND PointType = 'Analog'
    AND PointOffset = 151);
DELETE FROM PointUnit
WHERE PointId IN (
    SELECT PointId FROM Point p JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
    WHERE Type IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
    AND PointType = 'Analog'
    AND PointOffset = 151);
DELETE FROM Display2WayData
WHERE PointId IN (
    SELECT PointId FROM Point p JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
    WHERE Type IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
    AND PointType = 'Analog'
    AND PointOffset = 151);
DELETE FROM PointAnalog
WHERE PointId IN (
    SELECT PointId FROM Point p JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
    WHERE Type IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
    AND PointType = 'Analog'
    AND PointOffset = 151);
DELETE FROM PointStatus
WHERE PointId IN (
    SELECT PointId FROM Point p JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
    WHERE Type IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
    AND PointType = 'Analog'
    AND PointOffset = 151);
DELETE FROM PointAlarming
WHERE PointId IN (
    SELECT PointId FROM Point p JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
    WHERE Type IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
    AND PointType = 'Analog'
    AND PointOffset = 151);
DELETE FROM Point 
WHERE PointId IN (
    SELECT PointId FROM Point p JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
    WHERE Type IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4')
    AND PointType = 'Analog'
    AND PointOffset = 151);

/* Remove invalid configurations */
DELETE FROM DeviceConfigCategoryItem 
WHERE DeviceConfigCategoryId IN (
    SELECT DISTINCT DCC.DeviceConfigCategoryId 
    FROM DeviceConfigCategory DCC
    JOIN DeviceConfigCategoryMap DCCM 
        ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId
    JOIN DeviceConfiguration DC 
        ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationID
    JOIN DeviceConfigDeviceTypes DCDT 
        ON DC.DeviceConfigurationID = DCDT.DeviceConfigurationId
    WHERE DCC.CategoryType='rfnChannelConfiguration'
    AND DCDT.PaoType IN ('RFN-430SL0', 'RFN-430SL1', 'RFN-430SL2', 'RFN-430SL3', 'RFN-430SL4') )
AND DeviceConfigCategoryId NOT IN (
    SELECT DISTINCT DCC.DeviceConfigCategoryId 
    FROM DeviceConfigCategory DCC
    JOIN DeviceConfigCategoryMap DCCM 
        ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId
    JOIN DeviceConfiguration DC 
        ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationID
    JOIN DeviceConfigDeviceTypes DCDT 
        ON DC.DeviceConfigurationID = DCDT.DeviceConfigurationId
    WHERE DCC.CategoryType='rfnChannelConfiguration'
    AND DCDT.PaoType IN ('RFN-420cD', 'RFN-420cL', 'RFN-430A3K', 'RFN-430A3R', 'RFN-430KV', 'RFN-520fRX', 'RFN-520fRXD', 'RFN-530fRX', 'RFN-530S4x') )
AND ItemName LIKE 'enabledChannels%attribute'
AND ItemValue = 'RECEIVED_KVAH';

INSERT INTO DBUpdates VALUES ('YUK-18095', '7.1.0', GETDATE());
/* @end YUK-18095 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '26-FEB-2018', 'Latest Update', 0, GETDATE());*/
