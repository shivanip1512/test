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

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', GETDATE());
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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '26-FEB-2018', 'Latest Update', 0, SYSDATE);*/
