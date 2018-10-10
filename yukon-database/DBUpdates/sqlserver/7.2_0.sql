/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-18039 */
ALTER TABLE InfrastructureWarnings
ADD Timestamp DATETIME;

UPDATE InfrastructureWarnings
SET Timestamp = '01-JAN-1990';

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

/* @start YUK-18868 */
CREATE TABLE NestSync (
    SyncId              NUMERIC         NOT NULL,
    SyncStartTime       DATETIME        NOT NULL,
    SyncStopTime        DATETIME        NULL,
    CONSTRAINT PK_NestSync PRIMARY KEY (SyncId)
);

CREATE TABLE NestSyncDetail (
    SyncDetailId        NUMERIC         NOT NULL,
    SyncId              NUMERIC         NOT NULL,
    SyncType            VARCHAR(60)     NOT NULL,
    SyncReasonKey       VARCHAR(100)    NOT NULL,
    SyncReasonValue     VARCHAR(100)    NULL,
    SyncActionKey       VARCHAR(100)    NOT NULL,
    SyncActionValue     VARCHAR(100)    NULL,
    CONSTRAINT PK_NestSyncDetail PRIMARY KEY (SyncDetailId)
);
GO

ALTER TABLE NestSyncDetail
    ADD CONSTRAINT FK_NestSync_NestSyncDetail FOREIGN KEY (SyncId)
        REFERENCES NestSync (SyncId)
            ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-18868', '7.2.0', GETDATE());
/* @end YUK-18868 */

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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '13-AUG-2018', 'Latest Update', 0, GETDATE());*/
