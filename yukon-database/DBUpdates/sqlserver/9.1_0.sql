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

/* @start YUK-24557 */
DELETE FROM Job WHERE BeanName = 'ecobeePointUpdateJobDefinition';

INSERT INTO DBUpdates VALUES ('YUK-24557', '9.1.0', GETDATE());
/* @end YUK-24557 */

/* @start YUK-24437 */
create table SmartNotificationEmailHistory (
   HistoryId            numeric              not null,
   EventType            varchar(50)          not null,
   Verbosity            varchar(10)          not null,
   Media                varchar(10)          not null,
   ProcessingType       varchar(10)          not null,
   IntervalMinutes      numeric              not null,
   TotalEvents          numeric              not null,
   SendTime             datetime             not null,
   constraint PK_SmartNotificationEmailHistory primary key (HistoryId)
);
go

create index INDX_SmartNotificationEmailHistory_EventType on SmartNotificationEmailHistory (
EventType ASC
);
go

create index INDX_SmartNotificationEmailHistory_SendTime on SmartNotificationEmailHistory (
SendTime ASC
);
go

create table SmartNotificationEventHistory (
   EventHistoryId       numeric              not null,
   HistoryId            numeric              not null,
   constraint PK_SmartNotificationEventHistory primary key (EventHistoryId)
);
go

create table SmartNotificationEventParamHistory (
   EventHistoryId       numeric              not null,
   Name                 varchar(30)          not null,
   Value                varchar(500)         not null,
   constraint PK_SmartNotificationEventParamHistory primary key (EventHistoryId, Name, Value)
);
go

create table SmartNotificationRecipientHistory (
   HistoryId            numeric              not null,
   Recipient            varchar(254)         not null,
   constraint PK_SmartNotificationRecipientHistory primary key (HistoryId, Recipient)
);
go

alter table SmartNotificationEventHistory
   add constraint FK_SmrtNotifEventHist_SmrtNotifEmailHist foreign key (HistoryId)
      references SmartNotificationEmailHistory (HistoryId)
         on delete cascade;
go

alter table SmartNotificationEventParamHistory
   add constraint FK_SmrtNotifEvntPHist_SmrtNotifEmailHist foreign key (EventHistoryId)
      references SmartNotificationEventHistory (EventHistoryId)
         on delete cascade;
go

alter table SmartNotificationRecipientHistory
   add constraint FK_SmrtNotifRecipHist_SmrtNotifEmailHist foreign key (HistoryId)
      references SmartNotificationEmailHistory (HistoryId)
         on delete cascade;
go

INSERT INTO DBUpdates VALUES ('YUK-24437', '9.1.0', GETDATE());
/* @end YUK-24437 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.1', '09-SEP-2020', 'Latest Update', 0, GETDATE()); */