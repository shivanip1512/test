/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-23591 */
ALTER TABLE ControlEvent
ADD ExternalEventId varchar(36);
GO

UPDATE ControlEvent
SET ExternalEventId = ControlEventId
WHERE ProgramId IN
    (SELECT DISTINCT PAObjectId from YukonPAObject
     WHERE Type IN ('HONEYWELL PROGRAM', 'ITRON PROGRAM'));

INSERT INTO DBUpdates VALUES ('YUK-23591', '9.1.0', GETDATE());
/* @end YUK-23591 */

/* @start YUK-23949 */
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'installOrientation',
       'FORWARD'
FROM DeviceConfigCategory 
WHERE CategoryType = 'regulatorCategory';

INSERT INTO DBUpdates VALUES ('YUK-23949', '9.1.0', GETDATE());
/* @end YUK-23949 */

/* @start YUK-23951 */
INSERT INTO StateGroup VALUES(-31, 'Eaton Regulator Control Mode', 'Status');
INSERT INTO State VALUES(-31, 0, 'Locked Forward', 0, 6, 0);
INSERT INTO State VALUES(-31, 1, 'Locked Reverse', 1, 6, 0);
INSERT INTO State VALUES(-31, 2, 'Reverse Idle', 2, 6, 0);
INSERT INTO State VALUES(-31, 3, 'Bidirectional', 3, 6, 0);
INSERT INTO State VALUES(-31, 4, 'Neutral Idle', 4, 6, 0);
INSERT INTO State VALUES(-31, 5, 'Cogeneration', 5, 6, 0);
INSERT INTO State VALUES(-31, 6, 'Reactive Bidirectional', 7, 6, 0);
INSERT INTO State VALUES(-31, 7, 'Bias Bidirectional', 8, 6, 0);
INSERT INTO State VALUES(-31, 8, 'Bias Cogeneration', 9, 6, 0);
INSERT INTO State VALUES(-31, 9, 'Reverse Cogeneration', 10, 6, 0);

INSERT INTO StateGroup VALUES(-32, 'Beckwith Regulator Control Mode', 'Status');
INSERT INTO State VALUES(-32, 0, 'Reverse Block', 0, 6, 0);
INSERT INTO State VALUES(-32, 1, 'Regulate Reverse', 1, 6, 0);
INSERT INTO State VALUES(-32, 2, 'Regulator Forward', 2, 6, 0);
INSERT INTO State VALUES(-32, 3, 'Return to Neutral', 3, 6, 0);
INSERT INTO State VALUES(-32, 4, 'Regulate in Reverse', 4, 6, 0);
INSERT INTO State VALUES(-32, 5, 'Distributed Generation', 5, 6, 0);
INSERT INTO State VALUES(-32, 7, 'Auto Determination', 7, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-23951', '9.1.0', GETDATE());
/* @end YUK-23951 */

/* @start YUK-23903 */
CREATE TABLE LMGroupZeusMapping (
   YukonGroupId         NUMERIC              NOT NULL,
   EcobeeGroupId        VARCHAR(32)          NOT NULL,
   EcobeeEventId        VARCHAR(50)          NULL,
   EcobeeGroupName      VARCHAR(255)         NULL,
   CONSTRAINT PK_LMGROUPZEUSMAPPING PRIMARY KEY (YukonGroupId, EcobeeGroupId)
);
GO

CREATE TABLE ZeusGroupInventoryMapping (
   InventoryID          NUMERIC              NOT NULL,
   EcobeeGroupId        VARCHAR(32)          NOT NULL,
   CONSTRAINT PK_ZEUSGROUPINVENTORYMAPPING PRIMARY KEY (InventoryID, EcobeeGroupId)
);
GO

ALTER TABLE LMGroupZeusMapping
   ADD CONSTRAINT FK_LMGroupZeusMapping_LMGroup FOREIGN KEY (YukonGroupId)
      REFERENCES LMGroup (DeviceID);
GO

ALTER TABLE ZeusGroupInventoryMapping
   ADD CONSTRAINT FK_ZeusGroupIM_IBase FOREIGN KEY (InventoryID)
      REFERENCES InventoryBase (InventoryID);
GO

INSERT INTO DBUpdates VALUES ('YUK-23903', '9.1.0', GETDATE());
/* @end YUK-23903 */

/* @start YUK-24286 */
INSERT INTO StateGroup VALUES(-33, 'OnOff', 'Status');
INSERT INTO State VALUES(-33, 0, 'Off', 1, 6, 0);
INSERT INTO State VALUES(-33, 1, 'On', 0, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-24286', '9.1.0', GETDATE());
/* @end YUK-24286 */

/* @start YUK-23668 */
INSERT INTO PortTiming (PORTID, PRETXWAIT, RTSTOTXWAIT, POSTTXWAIT, RECEIVEDATAWAIT, EXTRATIMEOUT, PostCommWait)
SELECT PAObjectID, 0, 0, 0, 0, 0, 0 
FROM YukonPAObject
WHERE Type = 'RFN-1200';

INSERT INTO DBUpdates VALUES ('YUK-23668', '9.1.0', GETDATE());
/* @end YUK-23668 */

/* @start YUK-24557 */
DELETE FROM Job WHERE BeanName = 'ecobeePointUpdateJobDefinition';

INSERT INTO DBUpdates VALUES ('YUK-24557', '9.1.0', GETDATE());
/* @end YUK-24557 */

/* @start YUK-24031 */
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'minTapPosition',
       '-16'
FROM DeviceConfigCategory 
WHERE CategoryType = 'regulatorCategory';

INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'maxTapPosition',
       '16'
FROM DeviceConfigCategory 
WHERE CategoryType = 'regulatorCategory';

INSERT INTO DBUpdates VALUES ('YUK-24031', '9.1.0', GETDATE());
/* @end YUK-24031 */

/* @start YUK-24437 */
CREATE TABLE SmartNotifEmailHistory (
   HistoryId            NUMERIC              NOT NULL,
   EventType            VARCHAR(50)          NOT NULL,
   Verbosity            VARCHAR(20)          NOT NULL,
   Media                VARCHAR(20)          NOT NULL,
   ProcessingType       VARCHAR(20)          NOT NULL,
   IntervalMinutes      NUMERIC              NOT NULL,
   TotalEvents          NUMERIC              NOT NULL,
   SendTime             DATETIME             NOT NULL,
   CONSTRAINT PK_SmartNotificationEmailHistory PRIMARY KEY (HistoryId)
);
GO

CREATE INDEX INDX_SmartNotifEmailHistory_EventType ON SmartNotifEmailHistory (
EventType ASC
);
GO

CREATE INDEX INDX_SmartNotifEmailHistory_SendTime ON SmartNotifEmailHistory (
SendTime ASC
);
GO

CREATE TABLE SmartNotifEventHistory (
   EventHistoryId       NUMERIC              NOT NULL,
   HistoryId            NUMERIC              NOT NULL,
   CONSTRAINT PK_SmartNotificationEventHistory PRIMARY KEY (EventHistoryId)
);
GO

CREATE TABLE SmartNotifEventParamHistory (
   EventHistoryId       NUMERIC              NOT NULL,
   Name                 VARCHAR(30)          NOT NULL,
   Value                VARCHAR(500)         NOT NULL,
   CONSTRAINT PK_SmartNotificationEventParamHistory PRIMARY KEY (EventHistoryId, Name, Value)
);
GO

CREATE TABLE SmartNotifRecipientHistory (
   HistoryId            NUMERIC              NOT NULL,
   Recipient            VARCHAR(254)         NOT NULL,
   CONSTRAINT PK_SmartNotificationRecipientHistory PRIMARY KEY (HistoryId, Recipient)
);
GO

CREATE INDEX INDX_SmartNotifRecipientHistory_Recipient ON SmartNotifRecipientHistory (
Recipient ASC
);
GO

ALTER TABLE SmartNotifEventHistory
   ADD CONSTRAINT FK_SmrtNotifEventHist_SmrtNotifEmailHist FOREIGN KEY (HistoryId)
      REFERENCES SmartNotifEmailHistory (HistoryId)
         ON DELETE CASCADE;
GO

ALTER TABLE SmartNotifEventParamHistory
   ADD CONSTRAINT FK_SmrtNotifEvntPHist_SmrtNotifEmailHist FOREIGN KEY (EventHistoryId)
      REFERENCES SmartNotifEventHistory (EventHistoryId)
         ON DELETE CASCADE;
GO

ALTER TABLE SmartNotifRecipientHistory
   ADD CONSTRAINT FK_SmrtNotifRecipHist_SmrtNotifEmailHist FOREIGN KEY (HistoryId)
      REFERENCES SmartNotifEmailHistory (HistoryId)
         ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-24437', '9.1.0', GETDATE());
/* @end YUK-24437 */

/* @start YUK-24460 */
CREATE TABLE YukonLogging (
   LoggerId             NUMERIC              NOT NULL,
   LoggerName           VARCHAR(200)         NOT NULL,
   LoggerLevel          VARCHAR(5)           NOT NULL,
   ExpirationDate       DATETIME             NULL,
   Notes                VARCHAR(300)         NULL,
   CONSTRAINT PK_YUKONLOGGING PRIMARY KEY (LoggerId)
)
GO

INSERT INTO DBUpdates VALUES ('YUK-24460', '9.1.0', GETDATE());
/* @end YUK-24460 */

/* @start YUK-24529 */
ALTER TABLE LMGroupZeusMapping ADD ProgramId NUMERIC;
GO
UPDATE LMGroupZeusMapping SET ProgramId = -1;
INSERT INTO DBUpdates VALUES ('YUK-24529', '9.1.0', GETDATE());
/* @end YUK-24529 */

/* @start YUK-24593 */
ALTER TABLE ControlEventDevice
ADD FailReason VARCHAR(100) NULL;
GO

ALTER TABLE ControlEventDevice
ADD RetryTime DATETIME NULL;

INSERT INTO DBUpdates VALUES ('YUK-24593', '9.1.0', GETDATE());
/* @end YUK-24593 */

/* @start YUK-23375 */
ALTER DATABASE CURRENT SET auto_create_statistics ON;
ALTER DATABASE CURRENT SET auto_update_statistics ON;

INSERT INTO DBUpdates VALUES ('YUK-23375', '9.1.0', GETDATE());
/* @end YUK-23375 */

/* @start YUK-24730 */
INSERT INTO UnitMeasure VALUES ( 59,'dB', 0, 'Decibels', '(none)');

INSERT INTO DBUpdates VALUES ('YUK-24730', '9.1.0', GETDATE());
/* @end YUK-24730 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.1', '09-SEP-2020', 'Latest Update', 0, GETDATE()); */