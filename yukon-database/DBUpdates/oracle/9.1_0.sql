/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-23591 */
ALTER TABLE ControlEvent
ADD ExternalEventId varchar2(36);

UPDATE ControlEvent
SET ExternalEventId = ControlEventId
WHERE ProgramId IN
    (SELECT DISTINCT PAObjectId from YukonPAObject
     WHERE Type IN ('HONEYWELL PROGRAM', 'ITRON PROGRAM'));

INSERT INTO DBUpdates VALUES ('YUK-23591', '9.1.0', SYSDATE);
/* @end YUK-23591 */

/* @start YUK-23949 */
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT NVL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'installOrientation',
       'FORWARD'
FROM DeviceConfigCategory 
WHERE CategoryType = 'regulatorCategory';

INSERT INTO DBUpdates VALUES ('YUK-23949', '9.1.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-23951', '9.1.0', SYSDATE);
/* @end YUK-23951 */

/* @start YUK-23903 */
CREATE TABLE LMGroupZeusMapping (
   YukonGroupId         NUMBER                          NOT NULL,
   EcobeeGroupId        VARCHAR2(32)                    NOT NULL,
   EcobeeEventId        VARCHAR2(50),
   EcobeeGroupName      VARCHAR2(255),
   CONSTRAINT PK_LMGROUPZEUSMAPPING PRIMARY KEY (YukonGroupId, EcobeeGroupId)
);

CREATE TABLE ZeusGroupInventoryMapping (
   InventoryID          NUMERIC                         NOT NULL,
   EcobeeGroupId        VARCHAR(32)                     NOT NULL,
   CONSTRAINT PK_ZEUSGROUPINVENTORYMAPPING PRIMARY KEY (InventoryID, EcobeeGroupId)
);
ALTER TABLE LMGroupZeusMapping
   ADD CONSTRAINT FK_LMGroupZeusMapping_LMGroup FOREIGN KEY (YukonGroupId)
      REFERENCES LMGroup (DeviceID);

ALTER TABLE ZeusGroupInventoryMapping
   ADD CONSTRAINT FK_ZeusGroupIM_IBase FOREIGN KEY (InventoryID)
      REFERENCES InventoryBase (InventoryID);

INSERT INTO DBUpdates VALUES ('YUK-23903', '9.1.0', SYSDATE);
/* @end YUK-23903 */

/* @start YUK-24286 */
INSERT INTO StateGroup VALUES(-33, 'OnOff', 'Status');
INSERT INTO State VALUES(-33, 0, 'Off', 1, 6, 0);
INSERT INTO State VALUES(-33, 1, 'On', 0, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-24286', '9.1.0', SYSDATE);
/* @end YUK-24286 */

/* @start YUK-23668 */
INSERT INTO PortTiming (PORTID, PRETXWAIT, RTSTOTXWAIT, POSTTXWAIT, RECEIVEDATAWAIT, EXTRATIMEOUT, PostCommWait)
SELECT PAObjectID, 0, 0, 0, 0, 0, 0 
FROM YukonPAObject
WHERE Type = 'RFN-1200';

INSERT INTO DBUpdates VALUES ('YUK-23668', '9.1.0', SYSDATE);
/* @end YUK-23668 */

/* @start YUK-24557 */
DELETE FROM Job WHERE BeanName = 'ecobeePointUpdateJobDefinition';

INSERT INTO DBUpdates VALUES ('YUK-24557', '9.1.0', SYSDATE);
/* @end YUK-24557 */

/* @start YUK-24031 */
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT NVL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'minTapPosition',
       '-16'
FROM DeviceConfigCategory 
WHERE CategoryType = 'regulatorCategory';

INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT NVL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'maxTapPosition',
       '16'
FROM DeviceConfigCategory 
WHERE CategoryType = 'regulatorCategory';

INSERT INTO DBUpdates VALUES ('YUK-24031', '9.1.0', SYSDATE);
/* @end YUK-24031 */

/* @start YUK-24437 */
CREATE TABLE SmartNotifEmailHistory  (
   HistoryId            NUMBER                          NOT NULL,
   EventType            VARCHAR2(50)                    NOT NULL,
   Verbosity            VARCHAR2(20)                    NOT NULL,
   Media                VARCHAR2(20)                    NOT NULL,
   ProcessingType       VARCHAR2(20)                    NOT NULL,
   IntervalMinutes      NUMBER                          NOT NULL,
   TotalEvents          NUMBER                          NOT NULL,
   SendTime             DATE                            NOT NULL,
   CONSTRAINT PK_SmartNotificationEmailHistory PRIMARY KEY (HistoryId)
);

CREATE INDEX INDX_SmartNotifEmailHistory_EventType ON SmartNotifEmailHistory (
   EventType ASC
);

CREATE INDEX INDX_SmartNotifEmailHistory_SendTime ON SmartNotifEmailHistory (
   SendTime ASC
);

CREATE TABLE SmartNotifEventHistory  (
   EventHistoryId       NUMBER                          NOT NULL,
   HistoryId            NUMBER                          NOT NULL,
   CONSTRAINT PK_SmartNotificationEventHistory PRIMARY KEY (EventHistoryId)
);

CREATE TABLE SmartNotifEventParamHistory  (
   EventHistoryId       NUMBER                          NOT NULL,
   Name                 VARCHAR2(30)                    NOT NULL,
   Value                VARCHAR2(500)                   NOT NULL,
   CONSTRAINT PK_SmartNotificationEventParamHistory PRIMARY KEY (EventHistoryId, Name, Value)
);

CREATE TABLE SmartNotifRecipientHistory  (
   HistoryId            NUMBER                          NOT NULL,
   Recipient            VARCHAR2(254)                   NOT NULL,
   CONSTRAINT PK_SmartNotificationRecipientHistory PRIMARY KEY (HistoryId, Recipient)
);

CREATE INDEX INDX_SmartNotifRecipientHistory_Recipient ON SmartNotifRecipientHistory (
   Recipient ASC
);

ALTER TABLE SmartNotifEventHistory
   ADD CONSTRAINT FK_SmrtNotifEventHist_SmrtNotifEmailHist FOREIGN KEY (HistoryId)
      REFERENCES SmartNotifEmailHistory (HistoryId)
      ON DELETE CASCADE;

ALTER TABLE SmartNotifEventParamHistory
   ADD CONSTRAINT FK_SmrtNotifEvntPHist_SmrtNotifEmailHist FOREIGN KEY (EventHistoryId)
      REFERENCES SmartNotifEventHistory (EventHistoryId)
      ON DELETE CASCADE;

ALTER TABLE SmartNotifRecipientHistory
   ADD CONSTRAINT FK_SmrtNotifRecipHist_SmrtNotifEmailHist FOREIGN KEY (HistoryId)
      REFERENCES SmartNotifEmailHistory (HistoryId)
      ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-24437', '9.1.0', SYSDATE);
/* @end YUK-24437 */

/* @start YUK-24460 */
CREATE TABLE YukonLogging  (
   LoggerId             NUMBER                          NOT NULL,
   LoggerName           VARCHAR2(200)                   NOT NULL,
   LoggerLevel          VARCHAR2(5)                     NOT NULL,
   ExpirationDate       DATE,
   Notes                VARCHAR2(300),
   CONSTRAINT PK_YUKONLOGGING PRIMARY KEY (LoggerId)
);

INSERT INTO DBUpdates VALUES ('YUK-24460', '9.1.0', SYSDATE);
/* @end YUK-24460 */

/* @start YUK-24529 */
ALTER TABLE LMGroupZeusMapping ADD ProgramId NUMBER;

UPDATE LMGroupZeusMapping SET ProgramId = -1;

INSERT INTO DBUpdates VALUES ('YUK-24529', '9.1.0', SYSDATE);
/* @end YUK-24529 */

/* @start YUK-24593 */
ALTER TABLE ControlEventDevice
ADD FailReason VARCHAR2(100);

ALTER TABLE ControlEventDevice
ADD RetryTime DATE;

INSERT INTO DBUpdates VALUES ('YUK-24593', '9.1.0', SYSDATE);
/* @end YUK-24593 */

/* @start YUK-24461 */
/* @error ignore-begin */
DROP TABLE YukonLogging;

CREATE TABLE YukonLogging  (
   LoggerId             NUMBER                          NOT NULL,
   LoggerName           VARCHAR2(200)                   NOT NULL,
   LoggerLevel          VARCHAR2(5)                     NOT NULL,
   ExpirationDate       DATE,
   Notes                VARCHAR2(300),
   CONSTRAINT PK_YUKONLOGGING PRIMARY KEY (LoggerId)
);

INSERT INTO DBUpdates VALUES ('YUK-24461', '9.1.0', SYSDATE);
/* @error ignore-end */
/* @end YUK-24461 */

/* @start YUK-24110 */
UPDATE GlobalSetting SET Name = 'DEVICE_CONNECTION_WARNING_MINUTES' WHERE Name = 'GATEWAY_CONNECTION_WARNING_MINUTES';

INSERT INTO DBUpdates VALUES ('YUK-24110', '9.1.0', SYSDATE);
/* @end YUK-24110

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.1', '09-SEP-2020', 'Latest Update', 0, SYSDATE); */