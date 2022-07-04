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

/* @start YUK-26210-2 */
UPDATE YukonRoleProperty
SET KeyName = 'DER Edge Coordinator Permission'
WHERE RolePropertyID = -90050;

UPDATE YukonRoleProperty
SET Description = 'Allow access to DER Edge Coordinator features and APIs.'
WHERE RolePropertyID = -90050;

INSERT INTO DBUpdates VALUES ('YUK-26210-2', '9.3.0', SYSDATE);
/* @end YUK-26210-2 */

/* @start YUK-26401 */
ALTER TABLE RegulatorToZoneMapping
ADD (FeederId NUMBER);

INSERT INTO DBUpdates VALUES ('YUK-26401', '9.3.0', SYSDATE);
/* @end YUK-26401 */

/* @start YUK-26847 */
ALTER TABLE MSPVendor ADD Attributes VARCHAR2(500);
UPDATE MSPVendor SET Attributes = 'KWH , PEAK_DEMAND';
ALTER TABLE MSPVendor MODIFY Attributes VARCHAR2(500) NOT NULL;

INSERT INTO DBUpdates VALUES ('YUK-26847', '9.3.0', SYSDATE);
/* @end YUK-26847 */


/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/*INSERT INTO CTIDatabase VALUES ('9.3', '29-MAR-2022', 'Latest Update', 0, SYSDATE);*/