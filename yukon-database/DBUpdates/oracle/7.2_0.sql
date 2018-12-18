/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18039 */
ALTER TABLE InfrastructureWarnings
ADD Timestamp DATE;

UPDATE InfrastructureWarnings
SET Timestamp = '01-JAN-1990';

ALTER TABLE InfrastructureWarnings
MODIFY (Timestamp DATE NOT NULL);

INSERT INTO DBUpdates VALUES ('YUK-18039', '7.2.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-18744', '7.2.0', SYSDATE);
/* @end YUK-18744 */

/* @start YUK-18870 */
CREATE TABLE LMNestLoadShapingGear  (
    GearId               NUMBER                          NOT NULL,
    PreparationOption    VARCHAR2(20)                    NOT NULL,
    PeakOption           VARCHAR2(20)                    NOT NULL,
    PostPeakOption       VARCHAR2(20)                    NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);

ALTER TABLE LMNestLoadShapingGear
    ADD CONSTRAINT FK_NLSGear_LMProgramDirectGear FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearID)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18870', '7.2.0', SYSDATE);
/* @end YUK-18870 */

/* @start YUK-18897 */
CREATE TABLE LMNestControlEvent  (
    NestControlEventId      NUMBER          NOT NULL,
    NestGroup               VARCHAR2(20)    NOT NULL,
    NestKey                 VARCHAR2(20)    NOT NULL,
    StartTime               DATE            NOT NULL,
    StopTime                DATE,
    CancelRequestTime       DATE,
    CancelResponse          VARCHAR2(200),
    CONSTRAINT PK_LMNestControlEvent PRIMARY KEY (NestControlEventId)
);

INSERT INTO DBUpdates VALUES ('YUK-18897', '7.2.0', SYSDATE);
/* @end YUK-18897 */

/* @start YUK-18960 if YUK-18868 */
DELETE FROM NestSync;
DELETE FROM NestSyncDetail;

ALTER TABLE NestSyncDetail DROP COLUMN SyncReasonValue;
ALTER TABLE NestSyncDetail DROP COLUMN SyncActionValue;

CREATE TABLE NestSyncValue  (
    SyncValueId         NUMBER              NOT NULL,
    SyncDetailId        NUMBER              NOT NULL,
    SyncValue           VARCHAR2(100)       NOT NULL,
    SyncValueType       VARCHAR2(60)        NOT NULL,
    CONSTRAINT PK_NestSyncValue PRIMARY KEY (SyncValueId)
);

ALTER TABLE NestSyncValue
    ADD CONSTRAINT FK_NSDetail_NSValue FOREIGN KEY (SyncDetailId)
        REFERENCES NestSyncDetail (SyncDetailId)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18960', '7.2.0', SYSDATE);
/* @end YUK-18960 */

/* @start YUK-18960 */
CREATE TABLE NestSync  (
    SyncId              NUMBER              NOT NULL,
    SyncStartTime       DATE                NOT NULL,
    SyncStopTime        DATE,
    CONSTRAINT PK_NestSync PRIMARY KEY (SyncId)
);

CREATE TABLE NestSyncDetail (
    SyncDetailId        NUMBER              NOT NULL,
    SyncId              NUMBER              NOT NULL,
    SyncType            VARCHAR2(60)        NOT NULL,
    SyncReasonKey       VARCHAR2(100)       NOT NULL,
    SyncActionKey       VARCHAR2(100)       NOT NULL,
    CONSTRAINT PK_NestSyncDetail PRIMARY KEY (SyncDetailId)
);

CREATE TABLE NestSyncValue  (
    SyncValueId         NUMBER              NOT NULL,
    SyncDetailId        NUMBER              NOT NULL,
    SyncValue           VARCHAR2(100)       NOT NULL,
    SyncValueType       VARCHAR2(60)        NOT NULL,
    CONSTRAINT PK_NestSyncValue PRIMARY KEY (SyncValueId)
);

ALTER TABLE NestSyncDetail
    ADD CONSTRAINT FK_NestSync_NestSyncDetail FOREIGN KEY (SyncId)
        REFERENCES NestSync (SyncId)
            ON DELETE CASCADE;

ALTER TABLE NestSyncValue
    ADD CONSTRAINT FK_NestSDetail_NestSValue FOREIGN KEY (SyncDetailId)
        REFERENCES NestSyncDetail (SyncDetailId)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18960', '7.2.0', SYSDATE);
/* @end YUK-18960 */

/* @start YUK-19048 */
ALTER TABLE LMNestControlEvent
ADD CancelOrStop CHAR(1);

UPDATE LMNestControlEvent
SET CancelOrStop = 'S';

ALTER TABLE LMNestControlEvent
MODIFY CancelOrStop CHAR(1) NOT NULL;

INSERT INTO DBUpdates VALUES ('YUK-19048', '7.2.0', SYSDATE);
/* @end YUK-19048 */

/* @start YUK-19102 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'Nest', 1338);

INSERT INTO DBUpdates VALUES ('YUK-19102', '7.2.0', SYSDATE);
/* @end YUK-19102 */

/* @start YUK-19042 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6601S', 1339);

INSERT INTO DBUpdates VALUES ('YUK-19042', '7.2.0', SYSDATE);
/* @end YUK-19042 */

/* @start YUK-19044 if YUK-18280 */
/* error ignore is added here because this may have been fixed on the customer database manually. */

/* @error ignore-begin */
ALTER TABLE DeviceDataMonitorProcessor
MODIFY ( StateGroupId NUMBER NULL );
/* @error ignore-end */

INSERT INTO DBUpdates VALUES('YUK-19044', '7.2.0', SYSDATE);
/* @end YUK-19044 */

/* @start YUK-19141 */
INSERT INTO YukonServices VALUES(25, 'NestMessageListener', 'classpath:com/cannontech/services/nestMessageListener/nestMessageListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');

ALTER TABLE LMNestControlEvent
MODIFY CancelOrStop CHAR(1) NULL;

INSERT INTO DBUpdates VALUES ('YUK-19141', '7.2.0', SYSDATE);
/* @end YUK-19141 */

/* @start YUK-19198 */
DELETE FROM YukonGroupRole WHERE RolePropertyID = -10812;
DELETE FROM YukonRoleProperty WHERE RolePropertyID = -10812;

INSERT INTO DBUpdates VALUES ('YUK-19198', '7.2.0', SYSDATE);
/* @end YUK-19198 */

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

INSERT INTO DBUpdates VALUES ('YUK-19211', '7.2.0', SYSDATE);
/* @end YUK-19211 */

/* @start YUK-19241 */
ALTER TABLE LMNestControlEvent 
ADD Success CHAR(1);

INSERT INTO DBUpdates VALUES ('YUK-19241', '7.2.0', SYSDATE);
/* @end YUK-19241 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '13-AUG-2018', 'Latest Update', 0, SYSDATE);*/
