/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18076 */
ALTER TABLE Display DROP COLUMN Title;

INSERT INTO DBUpdates VALUES ('YUK-18076', '7.1.0', SYSDATE);
/* @end YUK-18076 */

/* @start YUK-18086 if YUK-17960 */
/* YUK-17960 had previously created the PK incorrectly.  This will simply re-name it if that YUK has been executed */
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT PK_CollectionActionCommandRequest TO PK_CACommandRequest;
ALTER INDEX PK_CollectionActionCommandRequest RENAME TO PK_CACommandRequest;

ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionCR_CollectionAction TO FK_CACR_CollectionAction;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionCR_CommandRequestExec TO FK_CACR_CommandRequestExec;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionI_CollectionAction TO FK_CAInput_CollectionAction;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionR_CollectionAction TO FK_CARequest_CollectionAction;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionR_YukonPAObject TO FK_CARequest_YukonPAObject;

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', SYSDATE);
/* @end YUK-18086 */

/* @start YUK-18086 */
/* If that YUK has not been executed, create the tables from scratch with the correct PK name already set */
CREATE TABLE CollectionAction  (
    CollectionActionId  NUMBER          NOT NULL,
    Action              VARCHAR2(50)    NOT NULL,
    StartTime           DATE            NOT NULL,
    StopTime            DATE,
    Status              VARCHAR2(50)    NOT NULL,
    UserName            VARCHAR2(100)   NOT NULL,
    CONSTRAINT PK_CollectionAction PRIMARY KEY (CollectionActionId)
);

CREATE TABLE CollectionActionCommandRequest  (
    CollectionActionId   NUMBER         NOT NULL,
    CommandRequestExecId NUMBER         NOT NULL,
    CONSTRAINT PK_CACommandRequest PRIMARY KEY (CollectionActionId, CommandRequestExecId)
);

CREATE TABLE CollectionActionInput  (
    CollectionActionId   NUMBER         NOT NULL,
    InputOrder           NUMBER         NOT NULL,
    Description          VARCHAR2(50)   NOT NULL,
    Value                VARCHAR2(1000) NOT NULL,
    CONSTRAINT PK_CollectionActionInput PRIMARY KEY (CollectionActionId, InputOrder)
);

CREATE TABLE CollectionActionRequest  (
    CollectionActionRequestId NUMBER    NOT NULL,
    CollectionActionId   NUMBER         NOT NULL,
    PAObjectId           NUMBER         NOT NULL,
    Result               VARCHAR2(50)   NOT NULL,
    CONSTRAINT PK_CollectionActionRequest PRIMARY KEY (CollectionActionRequestId)
);

CREATE INDEX INDX_Car_CollectionActionId ON CollectionActionRequest (
    CollectionActionId ASC
);

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

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', SYSDATE);
/* @end YUK-18086 */

/* @start YUK-18122 */
UPDATE YukonRoleProperty SET Description = 'Controls access to view Control Area Trigger related information.'
    WHERE RolePropertyId = -90021;

INSERT INTO DBUpdates VALUES ('YUK-18122', '7.1.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-18164', '7.1.0', SYSDATE);
/* @end YUK-18164 */

/* @start YUK-18280 */
ALTER TABLE DeviceDataMonitorProcessor
    ADD ( ProcessorType VARCHAR(25),
          ProcessorValue FLOAT,
          RangeMin FLOAT,
          RangeMax FLOAT );

UPDATE DeviceDataMonitorProcessor
SET ProcessorValue = State;

ALTER TABLE DeviceDataMonitorProcessor
DROP COLUMN State;

UPDATE DeviceDataMonitorProcessor
SET ProcessorType = 'STATE';

ALTER TABLE DeviceDataMonitorProcessor
    MODIFY ( ProcessorType VARCHAR2(25) NOT NULL );

ALTER TABLE DeviceDataMonitorProcessor
    MODIFY ( StateGroupId NUMBER );

INSERT INTO DBUpdates VALUES ('YUK-18280', '7.1.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-18091', '7.1.0', SYSDATE);
/* @end YUK-18091 */

/* @start YUK-18323 */
/* First delete any non-favorites where duplicate favorites exist */
DELETE UserPage P1
WHERE P1.PagePath IN
(
    SELECT P2.PagePath
    FROM UserPage P2
    WHERE P1.PagePath = P2.PagePath
    AND P1.UserId = P2.UserId
    AND P1.UserPageId != P2.UserPageId
    AND P1.Favorite = 0
    AND P2.Favorite = 1
);

/* Then delete all remaining, older duplicates (or the lower UserPageId if LastAccess is equal) */
DELETE UserPage P1
WHERE P1.PagePath IN
(
    SELECT P2.PagePath
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

INSERT INTO DBUpdates VALUES ('YUK-18323', '7.1.0', SYSDATE);
/* @end YUK-18323 */

/* @start YUK-18349 */
CREATE INDEX INDX_DGMember_YukonPaoId ON DEVICEGROUPMEMBER ( YukonPaoId ASC );

INSERT INTO DBUpdates VALUES ('YUK-18349', '7.1.0', SYSDATE);
/* @end YUK-18349 */

/* @start YUK-18333 */
UPDATE DmvTest
SET PollingInterval = 15
WHERE PollingInterval < 15;

INSERT INTO DBUpdates VALUES ('YUK-18333', '7.1.0', SYSDATE);
/* @end YUK-18333 */

/* @start YUK-18391 */
ALTER TABLE DmvTest
RENAME COLUMN PollingInterval
TO DataArchivingInterval;

ALTER TABLE DmvTest
RENAME COLUMN DataGatheringDuration
TO IntervalDataGatheringDuration;

INSERT INTO DBUpdates VALUES ('YUK-18391', '7.1.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-18379', '7.1.0', SYSDATE);
/* @end YUK-18379 */

/* @start YUK-18432 */
/* @start-block */
DECLARE
    v_MaxDeviceGroupId NUMBER;
    v_AllRfnGroupId NUMBER;
BEGIN
    SELECT MAX(DG.DeviceGroupId) INTO v_MaxDeviceGroupId FROM DeviceGroup DG WHERE DG.DeviceGroupId < 100;
    SELECT DG.DeviceGroupId INTO v_AllRfnGroupId FROM DeviceGroup DG WHERE SystemGroupEnum = 'ALL_RFN_METERS';
    
    INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
        VALUES (v_MaxDeviceGroupId + 1, 'All RFG Meters', v_AllRfnGroupId, 'NOEDIT_NOMOD', 'METERS_ALL_RFG_METERS', SYSDATE, 'ALL_RFG_METERS');

END;
/
/* @end-block */

INSERT INTO DBUpdates VALUES ('YUK-18432', '7.1.0', SYSDATE);
/* @end YUK-18432 */

/* @start YUK-18371-1 if YUK-18371 */
ALTER TABLE PaoNote
RENAME COLUMN CreatorUserName
TO CreateUserName;

ALTER TABLE PaoNote
RENAME COLUMN CreationDate
TO CreateDate;

ALTER TABLE PaoNote
RENAME COLUMN EditorUserName
TO EditUserName;

INSERT INTO DBUpdates VALUES ('YUK-18371-1', '7.1.0', SYSDATE);
/* @end YUK-18371-1 */

/* @start YUK-18371-1 */
CREATE TABLE PaoNote  (
    NoteId              NUMBER              NOT NULL,
    PaObjectId          NUMBER              NOT NULL,
    NoteText            NVARCHAR2(255)      NOT NULL,
    Status              CHAR(1)             NOT NULL,
    CreateUserName      NVARCHAR2(64)       NOT NULL,
    CreateDate          DATE                NOT NULL,
    EditUserName        NVARCHAR2(64),
    EditDate            DATE,
    CONSTRAINT PK_PaoNote PRIMARY KEY (NoteId)
);

CREATE INDEX INDX_PaObjectId_Status ON PaoNote (
   PaObjectId ASC,
   Status ASC
);

ALTER TABLE PaoNote
    ADD CONSTRAINT FK_PaoNote_YukonPAObject FOREIGN KEY (PaObjectId)
        REFERENCES YukonPAObject (PAObjectID)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18371-1', '7.1.0', SYSDATE);
/* @end YUK-18371-1 */

/* @start YUK-18487 */
/* Create some temporary stored procedures */
/* @start-block */
CREATE OR REPLACE PROCEDURE sp_UpdatePointName( 
    v_CurrentPointName IN VARCHAR2,
    v_NewPointName IN VARCHAR2,
    v_PointType IN VARCHAR2,
    v_PointOffset IN NUMBER,
    v_DeviceType IN VARCHAR2) AS
BEGIN
    UPDATE POINT 
    SET POINTNAME = v_NewPointName
    WHERE POINTID IN (
        SELECT POINTID 
        FROM POINT P 
        JOIN YukonPAObject Y ON P.PAObjectID = Y.PAObjectID
        WHERE Y.Type = v_DeviceType
        AND P.POINTTYPE = v_PointType
        AND P.POINTOFFSET = v_PointOffset
        AND P.POINTNAME = v_CurrentPointName
    );
END sp_UpdatePointName;
/
/* @end-block */

/* @start-block */
CREATE OR REPLACE PROCEDURE sp_UpdateAttributeName(
    v_CurrentAttributeName IN VARCHAR2,
    v_NewAttributeName IN VARCHAR2,
    v_DeviceType IN VARCHAR2) AS
BEGIN
    UPDATE BehaviorReportValue BRV
    SET BRV.Value = v_NewAttributeName
    WHERE BRV.BehaviorReportId IN (
        SELECT BR.BehaviorReportId 
        FROM BehaviorReport BR 
        JOIN YukonPAObject Y 
            ON BR.DeviceID = Y.PAObjectID
        WHERE Y.Type = v_DeviceType 
    )
    AND BRV.Value = v_CurrentAttributeName;
    
    UPDATE BehaviorValue BV
    SET BV.Value = v_NewAttributeName
    WHERE BV.BehaviorId IN (
        SELECT DISTINCT BV.BehaviorId
        FROM BehaviorValue BV
        JOIN Behavior B 
            ON BV.BehaviorId = B.BehaviorId
        JOIN DeviceBehaviorMap DBM 
            ON B.BehaviorId = DBM.BehaviorId
        JOIN YukonPAObject Y 
            ON DBM.DeviceId = Y.PAObjectID
        WHERE B.BehaviorType = 'DATA_STREAMING'
        AND Y.Type = v_DeviceType
    )
    AND BV.Value = v_CurrentAttributeName;
    
    UPDATE DeviceConfigCategoryItem DCCI
    SET ItemValue = v_NewAttributeName
    WHERE DCCI.DeviceConfigCategoryId IN (
        SELECT DISTINCT DCC.DeviceConfigCategoryId 
        FROM DeviceConfigCategory DCC
        JOIN DeviceConfigCategoryMap DCCM 
            ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId
        JOIN DeviceConfiguration DC 
            ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationID
        JOIN DeviceConfigDeviceTypes DCDT 
            ON DC.DeviceConfigurationID = DCDT.DeviceConfigurationId
        WHERE DCC.CategoryType='rfnChannelConfiguration'
        AND DCDT.PaoType = v_DeviceType
    )
    AND DCCI.ItemName LIKE 'enabledChannels%attribute'
    AND DCCI.ItemValue = v_CurrentAttributeName;

    UPDATE ArchiveValuesExportAttribute
    SET AttributeName = v_NewAttributeName
    WHERE AttributeName = v_CurrentAttributeName;

    UPDATE DeviceDataMonitorProcessor
    SET Attribute = v_NewAttributeName
    WHERE Attribute = v_CurrentAttributeName;

    UPDATE JOBPROPERTY
    SET value = REPLACE(value, v_CurrentAttributeName, v_NewAttributeName)
    WHERE name = 'attributes'
    AND value LIKE CONCAT( CONCAT( '%', v_CurrentAttributeName ), '%' );
END sp_UpdateAttributeName;
/
/* @end-block */

/* @start-block */
BEGIN
/* Delivered Power Factor / DELIVERED_POWER_FACTOR -> Average Power Factor (Quadrants 1 2 4) / AVERAGE_POWER_FACTOR_Q124 */
sp_UpdatePointName(v_CurrentPointName=>'Delivered Power Factor',v_NewPointName=>'Average Power Factor (Quadrants 1 2 4)',v_PointType=>'Analog',v_PointOffset=>172,v_DeviceType=>'RFN-430A3R');
sp_UpdateAttributeName(v_CurrentAttributeName=>'DELIVERED_POWER_FACTOR',v_NewAttributeName=>'AVERAGE_POWER_FACTOR_Q124',v_DeviceType=>'RFN-430A3R');

/* Sum Power Factor / SUM_POWER_FACTOR -> Average Power Factor / AVERAGE_POWER_FACTOR */
sp_UpdatePointName(v_CurrentPointName=>'Sum Power Factor',v_NewPointName=>'Average Power Factor',v_PointType=>'Analog',v_PointOffset=>373,v_DeviceType=>'RFN-430A3R');
sp_UpdateAttributeName(v_CurrentAttributeName=>'SUM_POWER_FACTOR',v_NewAttributeName=>'AVERAGE_POWER_FACTOR',v_DeviceType=>'RFN-430A3R');

sp_UpdatePointName(v_CurrentPointName=>'Sum Power Factor',v_NewPointName=>'Average Power Factor',v_PointType=>'Analog',v_PointOffset=>373,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'SUM_POWER_FACTOR',v_NewAttributeName=>'AVERAGE_POWER_FACTOR',v_DeviceType=>'RFN-530S4x'); 

/* Quadrant 1 Quadrant 3 kVAr / Q1_Q3_KVAR -> kVAr (Quadrants 1 3) / KVAR_Q13 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 1 Quadrant 3 kVAr',v_NewPointName=>'kVAr (Quadrants 1 3)',v_PointType=>'Analog',v_PointOffset=>363,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q1_Q3_KVAR',v_NewAttributeName=>'KVAR_Q13',v_DeviceType=>'RFN-430A3R');

/* Quadrant 2 Quadrant 4 kVAr / Q2_Q4_KVAR -> kVAr (Quadrants 2 4) / KVAR_Q24 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 2 Quadrant 4 kVAr',v_NewPointName=>'kVAr (Quadrants 2 4)',v_PointType=>'Analog',v_PointOffset=>364,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q2_Q4_KVAR',v_NewAttributeName=>'KVAR_Q24',v_DeviceType=>'RFN-430A3R');

/* Quadrant 1 Quadrant 4 kVAr / Q1_Q4_KVAR -> kVAr (Quadrants 1 4) / KVAR_Q14 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 1 Quadrant 4 kVAr',v_NewPointName=>'kVAr (Quadrants 1 4)',v_PointType=>'Analog',v_PointOffset=>365,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q1_Q4_KVAR',v_NewAttributeName=>'KVAR_Q14',v_DeviceType=>'RFN-430A3R');

/* Quadrant 2 Quadrant 3 kVAr / Q2_Q3_KVAR -> kVAr (Quadrants 2 3) / KVAR_Q23 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 2 Quadrant 3 kVAr',v_NewPointName=>'kVAr (Quadrants 2 3)',v_PointType=>'Analog',v_PointOffset=>366,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q2_Q3_KVAR',v_NewAttributeName=>'KVAR_Q23',v_DeviceType=>'RFN-430A3R');

/* Quadrant 1 Quadrant 2 kVA / Q1_Q2_KVA -> kVA (Quadrants 1 2) / KVA_Q12 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 1 Quadrant 2 kVA',v_NewPointName=>'kVA (Quadrants 1 2)',v_PointType=>'Analog',v_PointOffset=>368,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q1_Q2_KVA',v_NewAttributeName=>'KVA_Q12',v_DeviceType=>'RFN-430A3R');

/* Quadrant 3 Quadrant 4 kVA / Q3_Q4_KVA -> kVA (Quadrants 3 4) / KVA_Q34 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 3 Quadrant 4 kVA',v_NewPointName=>'kVA (Quadrants 3 4)',v_PointType=>'Analog',v_PointOffset=>369,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q3_Q4_KVA',v_NewAttributeName=>'KVA_Q34',v_DeviceType=>'RFN-430A3R');

/* Quadrant 1 Quadrant 3 kVA / Q1_Q3_KVA -> kVA (Quadrants 1 3) / KVA_Q13 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 1 Quadrant 3 kVA',v_NewPointName=>'kVA (Quadrants 1 3)',v_PointType=>'Analog',v_PointOffset=>370,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q1_Q3_KVA',v_NewAttributeName=>'KVA_Q13',v_DeviceType=>'RFN-430A3R');

/* Quadrant 2 Quadrant 4 kVA / Q2_Q4_KVA -> kVA (Quadrants 2 4) / KVA_Q24 */
sp_UpdatePointName(v_CurrentPointName=>'Quadrant 2 Quadrant 4 kVA',v_NewPointName=>'kVA (Quadrants 2 4)',v_PointType=>'Analog',v_PointOffset=>371,v_DeviceType=>'RFN-530S4x');
sp_UpdateAttributeName(v_CurrentAttributeName=>'Q2_Q4_KVA',v_NewAttributeName=>'KVA_Q24',v_DeviceType=>'RFN-430A3R');
END;
/
/* @end-block */

DROP PROCEDURE sp_UpdatePointName;
DROP PROCEDURE sp_UpdateAttributeName;

INSERT INTO DBUpdates VALUES ('YUK-18487', '7.1.0', SYSDATE);
/* @end YUK-18487 */

/* @start YUK-18517 */
DELETE FROM PaoNote 
WHERE Status = 'D';

INSERT INTO DBUpdates VALUES ('YUK-18517', '7.1.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-18477', '7.1.0', SYSDATE);
/* @end YUK-18477 */

/* @start YUK-18489 */
UPDATE PorterResponseMonitor 
SET Name = 'Default All PLC Meters', GroupName = '/System/Meters/All Meters/All MCT Meters' 
WHERE GroupName = '/';

INSERT INTO DBUpdates VALUES ('YUK-18489', '7.1.0', SYSDATE);
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


INSERT INTO DBUpdates VALUES ('YUK-18551', '7.1.0', SYSDATE);
/* @end YUK-18551 */

/* @start YUK-18526 */
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'CIS DeviceClass', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'CIS_DEVICECLASS'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'METERS') DG2;

INSERT INTO DBUpdates VALUES ('YUK-18526', '7.1.0', SYSDATE);
/* @end YUK-18526 */
/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '26-FEB-2018', 'Latest Update', 0, SYSDATE);*/
