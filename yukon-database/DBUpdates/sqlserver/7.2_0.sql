/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-18039 */
ALTER TABLE InfrastructureWarnings
ADD Timestamp DATETIME;
GO

UPDATE InfrastructureWarnings
SET Timestamp = '01-JAN-1990';
GO

ALTER TABLE InfrastructureWarnings
ALTER COLUMN Timestamp DATETIME NOT NULL;

INSERT INTO DBUpdates VALUES ('YUK-18039', '7.2.0', GETDATE());
/* @end YUK-18039 */

/* @start YUK-18744 */
UPDATE SmartNotificationEventParam
SET Name = 'Status', Value = 'STOPPED'
WHERE Name = 'Argument0';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_WEB_APPLICATION_SERVICE'
WHERE Value = 'WEB_SERVER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_SERVICE_MANAGER'
WHERE Value = 'SERVICE_MANAGER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_NOTIFICATION_SERVER'
WHERE Value = 'NOTIFICATION_SERVER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_MESSAGE_BROKER'
WHERE Value = 'YUKON_MESSAGE_BROKER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_DISPATCH_SERVICE'
WHERE Value = 'DISPATCH_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_CAP_CONTROL_SERVICE'
WHERE Value = 'CAPCONTROL_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_LOAD_MANAGEMENT_SERVICE'
WHERE Value = 'LOADMANAGEMENT_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_PORT_CONTROL_SERVICE'
WHERE Value = 'PORTER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_MAC_SCHEDULER_SERVICE'
WHERE Value = 'MACS_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_FOREIGN_DATA_SERVICE'
WHERE Value = 'FDR_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_CALC_LOGIC_SERVICE'
WHERE Value = 'CALC_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_REAL_TIME_SCAN_SERVICE'
WHERE Value = 'SCANNER_SERVICE_STATUS'
AND Name = 'WarningType';

UPDATE SmartNotificationEventParam
SET Value = 'YUKON_NETWORK_MANAGER'
WHERE Value = 'NETWORK_MANAGER_STATUS'
AND Name = 'WarningType';

INSERT INTO DBUpdates VALUES ('YUK-18744', '7.2.0', GETDATE());
/* @end YUK-18744 */

/* @start YUK-18870 */
CREATE TABLE LMNestLoadShapingGear (
    GearId               NUMERIC              NOT NULL,
    PreparationOption    VARCHAR(20)          NOT NULL,
    PeakOption           VARCHAR(20)          NOT NULL,
    PostPeakOption       VARCHAR(20)          NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);
GO

ALTER TABLE LMNestLoadShapingGear
    ADD CONSTRAINT FK_NLSGear_LMProgramDirectGear FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearID)
            ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-18870', '7.2.0', GETDATE());
/* @end YUK-18870 */

/* @start YUK-18897 */
CREATE TABLE LMNestControlEvent (
    NestControlEventId      NUMERIC         NOT NULL,
    NestGroup               VARCHAR(20)     NOT NULL,
    NestKey                 VARCHAR(20)     NOT NULL,
    StartTime               DATETIME        NOT NULL,
    StopTime                DATETIME        NULL,
    CancelRequestTime       DATETIME        NULL,
    CancelResponse          VARCHAR(200)    NULL,
    CONSTRAINT PK_LMNestControlEvent PRIMARY KEY (NestControlEventId)
);

INSERT INTO DBUpdates VALUES ('YUK-18897', '7.2.0', GETDATE());
/* @end YUK-18897 */

/* @start YUK-18960 if YUK-18868 */
DELETE FROM NestSync;
DELETE FROM NestSyncDetail;

ALTER TABLE NestSyncDetail DROP COLUMN SyncReasonValue;
ALTER TABLE NestSyncDetail DROP COLUMN SyncActionValue;
GO

CREATE TABLE NestSyncValue (
    SyncValueId         NUMERIC             NOT NULL,
    SyncDetailId        NUMERIC             NOT NULL,
    SyncValue           VARCHAR(100)        NOT NULL,
    SyncValueType       VARCHAR(60)         NOT NULL,
    CONSTRAINT PK_NestSyncValue PRIMARY KEY (SyncValueId)
);
GO

ALTER TABLE NestSyncValue
    ADD CONSTRAINT FK_NestSDetail_NestSValue FOREIGN KEY (SyncDetailId)
        REFERENCES NestSyncDetail (SyncDetailId)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18960', '7.2.0', GETDATE());
/* @end YUK-18960 */

/* @start YUK-18960 */
CREATE TABLE NestSync (
    SyncId              NUMERIC             NOT NULL,
    SyncStartTime       DATETIME            NOT NULL,
    SyncStopTime        DATETIME            NULL,
    CONSTRAINT PK_NestSync PRIMARY KEY (SyncId)
);

CREATE TABLE NestSyncDetail (
    SyncDetailId        NUMERIC             NOT NULL,
    SyncId              NUMERIC             NOT NULL,
    SyncType            VARCHAR(60)         NOT NULL,
    SyncReasonKey       VARCHAR(100)        NOT NULL,
    SyncActionKey       VARCHAR(100)        NOT NULL,
    CONSTRAINT PK_NestSyncDetail PRIMARY KEY (SyncDetailId)
);

CREATE TABLE NestSyncValue (
    SyncValueId         NUMERIC             NOT NULL,
    SyncDetailId        NUMERIC             NOT NULL,
    SyncValue           VARCHAR(100)        NOT NULL,
    SyncValueType       VARCHAR(60)         NOT NULL,
    CONSTRAINT PK_NestSyncValue PRIMARY KEY (SyncValueId)
);
GO

ALTER TABLE NestSyncDetail
    ADD CONSTRAINT FK_NestSync_NestSyncDetail FOREIGN KEY (SyncId)
        REFERENCES NestSync (SyncId)
            ON DELETE CASCADE;

ALTER TABLE NestSyncValue
    ADD CONSTRAINT FK_NestSDetail_NestSValue FOREIGN KEY (SyncDetailId)
        REFERENCES NestSyncDetail (SyncDetailId)
            ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-18960', '7.2.0', GETDATE());
/* @end YUK-18960 */

/* @start YUK-19048 */
ALTER TABLE LMNestControlEvent
ADD CancelOrStop CHAR(1);
GO

UPDATE LMNestControlEvent
SET CancelOrStop = 'S';

ALTER TABLE LMNestControlEvent
ALTER COLUMN CancelOrStop CHAR(1) NOT NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-19048', '7.2.0', GETDATE());
/* @end YUK-19048 */

/* @start YUK-19102 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'Nest', 1338);

INSERT INTO DBUpdates VALUES ('YUK-19102', '7.2.0', GETDATE());
/* @end YUK-19102 */

/* @start YUK-19042 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6601S', 1339);

INSERT INTO DBUpdates VALUES ('YUK-19042', '7.2.0', GETDATE());
/* @end YUK-19042 */

/* @start YUK-19141 */
INSERT INTO YukonServices VALUES(25, 'NestMessageListener', 'classpath:com/cannontech/services/nestMessageListener/nestMessageListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');

ALTER TABLE LMNestControlEvent
ALTER COLUMN CancelOrStop CHAR(1) NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-19141', '7.2.0', GETDATE());
/* @end YUK-19141 */

/* @start YUK-19211 */
UPDATE YukonRoleProperty 
SET KeyName      = 'Area Commands and Actions', 
    DefaultValue = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS', 
    Description  = 'Allows access to Field Operation Commands, Non-Operational Commands and Yukon Actions for CapControl Areas.' 
WHERE RolePropertyID = '-70021';

UPDATE YukonGroupRole 
SET Value = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS' 
WHERE RolePropertyID = '-70021' 
AND   Value          = 'true';

UPDATE YukonGroupRole 
SET Value = 'NONE' 
WHERE RolePropertyID = '-70021' 
AND   Value          = 'false';


UPDATE YukonRoleProperty 
SET KeyName      = 'Substation Commands and Actions',  
    DefaultValue = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS', 
    Description  = 'Allows access to Field Operation Commands, Non-Operational Commands and Yukon Actions for CapControl Substations.' 
WHERE RolePropertyID = '-70022';

UPDATE YukonGroupRole 
SET Value = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS' 
WHERE RolePropertyID = '-70022' 
AND   Value          = 'true';

UPDATE YukonGroupRole 
SET Value = 'NONE' 
WHERE RolePropertyID = '-70022' 
AND   Value          = 'false';


UPDATE YukonRoleProperty 
SET KeyName      = 'SubBus Commands and Actions',
    DefaultValue = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS', 
    Description  = 'Allows access to Field Operation Commands, Non-Operational Commands and Yukon Actions for CapControl SubBuses.' 
WHERE RolePropertyID = '-70023';

UPDATE YukonGroupRole 
SET Value = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS' 
WHERE RolePropertyID = '-70023' 
AND   Value          = 'true';

UPDATE YukonGroupRole 
SET Value = 'NONE' 
WHERE RolePropertyID = '-70023' 
AND   Value          = 'false';


UPDATE YukonRoleProperty 
SET KeyName      = 'Feeder Commands and Actions', 
    DefaultValue = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS', 
    Description  = 'Allows access to Field Operation Commands, Non-Operational Commands and Yukon Actions for CapControl Feeders.' 
WHERE RolePropertyID = '-70024';

UPDATE YukonGroupRole 
SET Value = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS' 
WHERE RolePropertyID = '-70024' 
AND   Value          = 'true';

UPDATE YukonGroupRole 
SET Value = 'NONE' 
WHERE RolePropertyID = '-70024' 
AND   Value          = 'false';


UPDATE YukonRoleProperty 
SET KeyName      = 'Capbank/CBC Commands and Actions', 
    DefaultValue = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS', 
    Description  = 'Allows access to Field Operation Commands, Non-Operational Commands and Yukon Actions for CapControl CapBanks/CBCs.' 
WHERE RolePropertyID = '-70025';

UPDATE YukonGroupRole 
SET Value = 'ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS' 
WHERE RolePropertyID = '-70025' 
AND   Value          = 'true';

UPDATE YukonGroupRole 
SET Value = 'NONE' 
WHERE RolePropertyID = '-70025' 
AND   Value          = 'false';


UPDATE YukonRoleProperty 
SET KeyName      = 'Control Confirmation pop-ups', 
    DefaultValue = 'ALL_COMMANDS', 
    Description  = 'Controls whether to display confirmation pop-ups for all Cap Control commands, only Operational commands or for no commands.'
WHERE RolePropertyID = '-70026';

UPDATE YukonGroupRole 
SET Value = 'ALL_COMMANDS' 
WHERE RolePropertyID = '-70026' 
AND   Value          = 'true';

UPDATE YukonGroupRole 
SET Value = 'NONE' 
WHERE RolePropertyID = '-70026' 
AND   Value          = 'false';

INSERT INTO DBUpdates VALUES ('YUK-19211', '7.2.0', GETDATE());
/* @end YUK-19211 */

/* @start YUK-19241 */
ALTER TABLE LMNestControlEvent 
ADD Success CHAR(1);

INSERT INTO DBUpdates VALUES ('YUK-19241', '7.2.0', GETDATE());
/* @end YUK-19241 */

/* @start YUK-19162 */
CREATE TABLE ScheduledDataImportHistory (
   EntryId              NUMERIC              NOT NULL,
   FileName             VARCHAR(100)         NOT NULL,
   FileImportType       VARCHAR(50)          NOT NULL,
   ImportDate           DATETIME             NOT NULL,
   ArchiveFileName      VARCHAR(100)         NOT NULL,
   ArchiveFilePath      VARCHAR(300)         NOT NULL,
   ArchiveFileExists    CHAR(1)              NOT NULL,
   FailedFileName       VARCHAR(100)         NULL,
   FailedFilePath       VARCHAR(300)         NULL,
   SuccessCount         NUMERIC              NOT NULL,
   FailureCount         NUMERIC              NOT NULL,
   TotalCount           NUMERIC              NOT NULL,
   JobGroupId           INT                  NOT NULL,
   CONSTRAINT PK_ScheduledDataImportHistory PRIMARY KEY (EntryId)
);
GO

INSERT INTO DBUpdates VALUES ('YUK-19162', '7.2.0', GETDATE());
/* @end YUK-19162 */

/* @start YUK-19189 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6600S', 1340);

INSERT INTO DBUpdates VALUES ('YUK-19189', '7.2.0', GETDATE());
/* @end YUK-19189 */

/* @start YUK-19162-1 if YUK-19162 */
ALTER TABLE ScheduledDataImportHistory DROP COLUMN TotalCount;
GO

INSERT INTO DBUpdates VALUES ('YUK-19162-1', '7.2.0', GETDATE());
/* @end YUK-19162-1 */

/* @start YUK-19285 */
CREATE TABLE DeviceMacAddress (
    DeviceId             NUMERIC              NOT NULL,
    MacAddress           VARCHAR(255)         NOT NULL,
    CONSTRAINT PK_DeviceMacAddress PRIMARY KEY (DeviceId)
);
GO

ALTER TABLE DeviceMacAddress
ADD CONSTRAINT AK_DeviceMacAddress_MacAddress 
    UNIQUE (MacAddress);

ALTER TABLE DeviceMacAddress
ADD CONSTRAINT FK_DeviceMacAddress_Device 
    FOREIGN KEY (DeviceId)
    REFERENCES DEVICE (DEVICEID)
    ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-19285', '7.2.0', GETDATE());
/* @end YUK-19285 */

/* @start YUK-19190 */
UPDATE YukonRoleProperty 
SET DefaultValue = 'false' 
WHERE RolePropertyId = -10008;
GO

UPDATE YukonGroupRole 
SET Value = 'false' 
WHERE RolePropertyId = -10008;
GO

INSERT INTO DBUpdates VALUES ('YUK-19190', '7.2.0', GETDATE());
/* @end YUK-19190 */

/* @start YUK-19316 */
UPDATE StateGroup
SET NAME = 'RelayState'
WHERE STATEGROUPID = -28
AND NAME = 'ThermostatRelayState';

INSERT INTO DBUpdates VALUES ('YUK-19316', '7.2.0', GETDATE());
/* @end YUK-19316 */

/* @start YUK-19270 */
UPDATE POINT 
SET POINTNAME = 'Peak kVAr (Quadrants 1 4)'
WHERE POINTID IN (
    SELECT POINTID 
    FROM POINT P 
    JOIN YukonPAObject Y ON P.PAObjectID = Y.PAObjectID
    WHERE Y.Type IN ('RFN-430A3K', 'RFN-430A3R', 'RFN-430KV') 
    AND P.POINTTYPE = 'Analog'
    AND P.POINTOFFSET = 262
    AND P.POINTNAME = 'Max kVAr'
);

INSERT INTO DBUpdates VALUES ('YUK-19270', '7.2.0', GETDATE());
/* @end YUK-19270 */

/* @start YUK-19420 if YUK-19198 */
INSERT INTO YukonRoleProperty 
VALUES(-10812, -108, 'Java Web Start Launcher Enabled', 'false', 'Allow access to the Java Web Start Launcher for client applications.');

INSERT INTO DBUpdates VALUES ('YUK-19420', '7.2.0', GETDATE());
/* @end YUK-19420 */

/* @start YUK-19420 */
UPDATE YukonRoleProperty
SET DefaultValue = 'false'
WHERE RolePropertyID = -10812;

INSERT INTO DBUpdates VALUES ('YUK-19420', '7.2.0', GETDATE());
/* @end YUK-19420 */

/* @start YUK-19447 */
ALTER TABLE EncryptionKey
ALTER COLUMN PublicKey VARCHAR(3800);
GO

ALTER TABLE EncryptionKey
ADD Temp TEXT;
GO

UPDATE EncryptionKey
SET Temp = PrivateKey;
GO

ALTER TABLE EncryptionKey
ALTER COLUMN PrivateKey VARCHAR(1920) NULL;
GO

ALTER TABLE EncryptionKey
DROP COLUMN PrivateKey;
GO

EXEC sp_rename 'EncryptionKey.Temp', 'PrivateKey', 'COLUMN';
GO

ALTER TABLE EncryptionKey
ALTER COLUMN PrivateKey TEXT NOT NULL;
GO

ALTER TABLE EncryptionKey
ADD Timestamp DATETIME;
GO

INSERT INTO DBUpdates VALUES ('YUK-19447', '7.2.0', GETDATE());
/* @end YUK-19447 */

/* @start YUK-19471 */
UPDATE Widget
SET WidgetType = 'CSR_TREND'
WHERE WidgetType = 'TREND';

INSERT INTO DBUpdates VALUES ('YUK-19471', '7.2.0', GETDATE());
/* @end YUK-19471 */

/* @start YUK-19474-2 if YUK-19474 */
DROP TABLE LMGroupItronMapping;
DROP TABLE LMProgramItronMapping;

INSERT INTO DBUpdates VALUES ('YUK-19474-2', '7.2.0', GETDATE());
/* @end YUK-19474-2 */

/* @start YUK-19511 */
CREATE TABLE LMGroupItronMapping (
    YukonGroupId        NUMERIC     NOT NULL,
    ItronGroupId        NUMERIC     NOT NULL,
    CONSTRAINT PK_LMGroupItronMapping PRIMARY KEY (YukonGroupId)
);

CREATE TABLE LMProgramItronMapping (
    YukonProgramId      NUMERIC     NOT NULL,
    ItronProgramId      NUMERIC     NOT NULL,
    CONSTRAINT PK_LMProgramItronMapping PRIMARY KEY (YukonProgramId)
);
GO

ALTER TABLE LMGroupItronMapping
    ADD CONSTRAINT FK_LMGroupItronMapping_LMGroup FOREIGN KEY (YukonGroupId)
    REFERENCES LMGroup (DeviceID)
    ON DELETE CASCADE;

ALTER TABLE LMProgramItronMapping
    ADD CONSTRAINT FK_LMProgItronMapping_LMProg FOREIGN KEY (YukonProgramId)
    REFERENCES LMPROGRAM (DeviceID)
    ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-19511', '7.2.0', GETDATE());
/* @end YUK-19511 */

/* @start YUK-18984 */
DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 133
    AND YP.Type IN ('RFN-430A3R', 'RFN-430kV'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 133
    AND YP.Type IN ('RFN-430A3R', 'RFN-430kV'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 133
    AND YP.Type IN ('RFN-430A3R', 'RFN-430kV'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 133
    AND YP.Type IN ('RFN-430A3R', 'RFN-430kV'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 133
    AND YP.Type IN ('RFN-430A3R', 'RFN-430kV'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 133
    AND YP.Type IN ('RFN-430A3R', 'RFN-430kV'));

INSERT INTO DBUpdates VALUES ('YUK-18984', '7.2.0', GETDATE());
/* @end YUK-18984 */

/* @start YUK-19500 */
UPDATE YukonRoleProperty 
SET 
    KeyName = 'Java Web Start (Deprecated)', 
    Description = 'Java Web Start to access client applications is no longer supported.' 
WHERE RolePropertyId = -10812;

INSERT INTO DBUpdates VALUES ('YUK-19500', '7.2.0', GETDATE());
/* @end YUK-19500 */

/* @start YUK-19531 if YUK-19162 */
ALTER TABLE ScheduledDataImportHistory
DROP COLUMN ArchiveFilePath;
GO

INSERT INTO DBUpdates VALUES ('YUK-19531', '7.2.0', GETDATE());
/* @end YUK-19531 */

/* @start YUK-19531 */
CREATE TABLE ScheduledDataImportHistory (
   EntryId              NUMERIC              NOT NULL,
   FileName             VARCHAR(100)         NOT NULL,
   FileImportType       VARCHAR(50)          NOT NULL,
   ImportDate           DATETIME             NOT NULL,
   ArchiveFileName      VARCHAR(100)         NOT NULL,
   ArchiveFileExists    CHAR(1)              NOT NULL,
   FailedFileName       VARCHAR(100)         NULL,
   FailedFilePath       VARCHAR(300)         NULL,
   SuccessCount         NUMERIC              NOT NULL,
   FailureCount         NUMERIC              NOT NULL,
   TotalCount           NUMERIC              NOT NULL,
   JobGroupId           INT                  NOT NULL,
   CONSTRAINT PK_ScheduledDataImportHistory PRIMARY KEY (EntryId)
);
GO

INSERT INTO DBUpdates VALUES ('YUK-19531', '7.2.0', GETDATE());
/* @end YUK-19531 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '13-AUG-2018', 'Latest Update', 0, GETDATE());*/
