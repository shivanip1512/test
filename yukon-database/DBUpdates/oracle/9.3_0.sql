/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-25803 */
CREATE TABLE EventLogType (
   EventTypeId          Numeric              NOT NULL,
   EventType            VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_EVENTLOGTYPE PRIMARY KEY (EventTypeId));

INSERT INTO EventLogType (EventTypeId, EventType)
    (SELECT ROW_NUMBER() OVER (ORDER BY MIN(EventLogId) ASC), EventType
        from EventLog
        group by EventType);

DROP INDEX INDX_EventLog_EvntTime_EvntLogId_EvntType;

ALTER TABLE EventLogType
   ADD CONSTRAINT AK_EventLogType_EventType UNIQUE (EventType);
   
ALTER TABLE EventLog 
ADD (tempColumn NUMBER);

UPDATE 
(SELECT EventLog.tempColumn AS OLD, EventLogType.EventTypeId AS NEW
 FROM EventLog
 INNER JOIN EventLogType
 ON EventLog.EventType = EventLogType.EventType
) t
SET t.OLD = t.NEW;

ALTER TABLE EventLog
MODIFY (EventType NULL);

UPDATE EventLog
SET EventType = NULL;

ALTER TABLE EventLog 
MODIFY (EventType NUMBER);

AlTER TABLE EventLog
RENAME COLUMN EventType to EventTypeId;

UPDATE EventLog
SET EventTypeId = tempColumn;

ALTER TABLE EventLog
MODIFY (EventTypeId  NOT NULL);

ALTER TABLE EventLog
DROP COLUMN tempColumn;

CREATE INDEX INDX_EventLog_EventTypeId_EventTime on EventLog (
EventTypeId DESC,
EventTime DESC);

CREATE INDEX INDX_EventLog_EventTypeId_EventTime_EventLogId on EventLog (
EventTypeId ASC,
EventTime ASC,
EventLogId ASC);

ALTER TABLE EventLog
   ADD CONSTRAINT FK_EventLog_EventLogType FOREIGN KEY (EventTypeId)
      REFERENCES EventLogType (EventTypeId);

INSERT INTO DBUpdates VALUES ('YUK-25803', '9.3.0', SYSDATE);
/* @end YUK-25803 */

/* @start YUK-26160 */

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'http://127.0.0.1:8080/multispeak/v4/MR_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'http://127.0.0.1:8080/multispeak/v4/OD_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'http://127.0.0.1:8080/multispeak/v4/CD_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'DR_Server', 'http://127.0.0.1:8080/multispeak/v4/DR_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'SCADA_Server', 'http://127.0.0.1:8080/multispeak/v4/SCADA_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'NOT_Server', 'http://127.0.0.1:8080/multispeak/v4/NOT_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);

INSERT INTO DBUpdates VALUES ('YUK-26160', '9.3.0', SYSDATE);
/* @end YUK-26160 */

/* @start YUK-26097 */
ALTER TABLE PointToZoneMapping
ADD (FeederId NUMBER);

INSERT INTO DBUpdates VALUES ('YUK-26097', '9.3.0', SYSDATE);
/* @end YUK-26097 */

/* @start YUK-26210 */
INSERT INTO YukonRoleProperty VALUES (-90050,-900,'DER Edge Coordinator Permission','false','Allow access to DER Edge Coordinator features and APIs.');

INSERT INTO DBUpdates VALUES ('YUK-26210', '9.3.0', SYSDATE);
/* @end YUK-26210 */

/* @start YUK-26401 */
ALTER TABLE RegulatorToZoneMapping
ADD (FeederId NUMBER);

INSERT INTO DBUpdates VALUES ('YUK-26401', '9.3.0', SYSDATE);
/* @end YUK-26401 */

/* @start YUK-26847 */
ALTER TABLE MSPVendor ADD Attributes VARCHAR2(500);
UPDATE MSPVendor SET Attributes = 'Peak Demand , Usage';
ALTER TABLE MSPVendor MODIFY Attributes VARCHAR2(500) NOT NULL;

INSERT INTO DBUpdates VALUES ('YUK-26847', '9.3.0', SYSDATE);
/* @end YUK-26847 */

/* @start YUK-27139 */
UPDATE InfrastructureWarnings SET WarningType = 'INFRASTRUCTURE_OUTAGE' WHERE WarningType = 'RELAY_OUTAGE';
UPDATE SmartNotificationEventParam SET Value = 'INFRASTRUCTURE_OUTAGE' WHERE Name = 'WarningType' AND Value = 'RELAY_OUTAGE';

INSERT INTO DBUpdates VALUES ('YUK-27139', '9.3.0', SYSDATE);
/* @end YUK-27139 */

/* @start YUK-27043 */
UPDATE YukonRoleProperty
SET Description = 'Allow access to DER Edge Coordinator features and APIs. Warning: This setting should only be enabled for dedicated DER Edge API users. It will remove access to other Yukon features.'
WHERE RolePropertyID = -90050;

INSERT INTO DBUpdates VALUES ('YUK-27043', '9.3.0', SYSDATE);
/* @end YUK-27043 */

/* @start YUK-26089 */
INSERT INTO StateGroup VALUES( -34, 'InsertedRemoved', 'Status');

INSERT INTO State VALUES( -34, 0, 'Removed', 1, 6, 0);
INSERT INTO State VALUES( -34, 1, 'Inserted', 0, 6, 0);
INSERT INTO State VALUES( -34, 2, 'Unknown', 9, 6, 0);

UPDATE POINT SET STATEGROUPID = -34 WHERE PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE Type = 'CRLY-856') AND POINTOFFSET = 67 AND POINTTYPE = 'Status';
UPDATE POINT SET STATEGROUPID = -34 WHERE PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE Type IN ('CRL-520fAXe','CRL-520fRXe','CRL-520fAXeD','CRL-520fRXeD','CRL-530S4x')) AND POINTOFFSET = 106 AND POINTTYPE = 'Status';

UPDATE RAWPOINTHISTORY SET VALUE = 2 WHERE POINTID IN (SELECT POINTID FROM POINT WHERE PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE Type = 'CRLY-856') AND POINTOFFSET = 67 AND POINTTYPE = 'Status');
UPDATE RAWPOINTHISTORY SET VALUE = 2 WHERE POINTID IN (SELECT POINTID FROM POINT WHERE PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE Type IN ('CRL-520fAXe','CRL-520fRXe','CRL-520fAXeD','CRL-520fRXeD','CRL-530S4x')) AND POINTOFFSET = 106 AND POINTTYPE = 'Status');

UPDATE DYNAMICPOINTDISPATCH SET VALUE = 2 WHERE POINTID IN (SELECT POINTID FROM POINT WHERE PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE Type = 'CRLY-856') AND POINTOFFSET = 67 AND POINTTYPE = 'Status');
UPDATE DYNAMICPOINTDISPATCH SET VALUE = 2 WHERE POINTID IN (SELECT POINTID FROM POINT WHERE PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE Type IN ('CRL-520fAXe','CRL-520fRXe','CRL-520fAXeD','CRL-520fRXeD','CRL-530S4x')) AND POINTOFFSET = 106 AND POINTTYPE = 'Status');

INSERT INTO DBUpdates VALUES ('YUK-26089', '9.3.0', SYSDATE);
/* @end YUK-26089 */

/* @start YUK-26753 */
ALTER TABLE LMGroupZeusMapping DROP CONSTRAINT PK_LMGROUPZEUSMAPPING;
ALTER TABLE LMGroupZeusMapping MODIFY EcobeeGroupId VARCHAR2(40);
ALTER TABLE LMGroupZeusMapping ADD CONSTRAINT PK_LMGROUPZEUSMAPPING PRIMARY KEY (YukonGroupId, EcobeeGroupId);

ALTER TABLE ZeusGroupInventoryMapping DROP CONSTRAINT PK_ZEUSGROUPINVENTORYMAPPING;
ALTER TABLE ZeusGroupInventoryMapping MODIFY EcobeeGroupId VARCHAR2(40);
ALTER TABLE ZeusGroupInventoryMapping ADD CONSTRAINT PK_ZEUSGROUPINVENTORYMAPPING PRIMARY KEY (InventoryID, EcobeeGroupId);

INSERT INTO DBUpdates VALUES ('YUK-26753', '9.3.0', SYSDATE);
/* @end YUK-26753 */

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/*INSERT INTO CTIDatabase VALUES ('9.3', '29-MAR-2022', 'Latest Update', 0, SYSDATE);*/