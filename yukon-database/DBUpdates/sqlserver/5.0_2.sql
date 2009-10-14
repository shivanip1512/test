/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7875 */ 
UPDATE DeviceGroup 
SET GroupName = 'CIS Substation' 
WHERE DeviceGroupId = 25 
AND GroupName = 'Substation' 
AND ParentDeviceGroupId = 1 
AND Permission = 'NOEDIT_MOD' 
AND Type = 'STATIC'; 
/* End YUK-7875 */ 

/* Start YUK-7882 */
INSERT INTO YukonRoleProperty VALUES (-90009,-900,'Ignore LM Pao Permissions','false','Allow access to all load management objects. Set to false to force the use of per pao permissions.');
/* End YUK-7882 */

/* Start YUK-7904 */
INSERT INTO YukonServices VALUES (7, 'ActiveMQ', 'classpath:com/cannontech/services/jms/activemq.xml', '(none)', '(none)', 'ServiceManager');
INSERT INTO YukonServices VALUES (8, 'PointInjector', 'classpath:com/cannontech/services/points/pointInjectionContext.xml', '(none)', '(none)', 'ServiceManager');
INSERT INTO YukonServices VALUES (9, 'Monitors', 'classpath:com/cannontech/services/monitors/monitorsContext.xml', '(none)', '(none)', 'ServiceManager');
/* End YUK-7904 */

/* Start YUK-7910 */
DELETE FROM DeviceTypeCommand
WHERE DeviceCommandId = -12;

UPDATE DeviceTypeCommand
SET DeviceType = 'Device Group'
WHERE DeviceType = 'CollectionGroup'
OR DeviceType = 'TestCollectionGroup';
/* End YUK-7910 */

/* Start YUK-7917 */
INSERT INTO YukonRoleProperty VALUES (-90010,-900,'Control Areas','true','Controls access to view Control Areas');
INSERT INTO YukonRoleProperty VALUES (-90011,-900,'Scenarios','true','Controls access to view Scenarios');

INSERT INTO YukonRoleProperty VALUES (-90020,-900,'Control Area State','true','Controls access to view Control Area State');
INSERT INTO YukonRoleProperty VALUES (-90021,-900,'Control Area Trigger Value/Threshold','true','Controls access to view Control Area Trigger Value/Threshold');
INSERT INTO YukonRoleProperty VALUES (-90022,-900,'Control Area Trigger Peak/Projection','true','Controls access to view Control Area Trigger Peak/Projection');
INSERT INTO YukonRoleProperty VALUES (-90023,-900,'Control Area Trigger ATKU','true','Controls access to view Control Area Trigger ATKU');
INSERT INTO YukonRoleProperty VALUES (-90024,-900,'Control Area Priority','true','Controls acces to view Control Area Priority');
INSERT INTO YukonRoleProperty VALUES (-90025,-900,'Control Area Time Window','true','Controls access to view Control Area Time Window');
INSERT INTO YukonRoleProperty VALUES (-90026,-900,'Control Area Load Capacity','true','Controls access to view Control Area Load Capacity');

INSERT INTO YukonRoleProperty VALUES (-90027,-900,'Program State','true','Controls access to view Program State');
INSERT INTO YukonRoleProperty VALUES (-90028,-900,'Program Start','true','Controls access to view Program Start');
INSERT INTO YukonRoleProperty VALUES (-90029,-900,'Program Stop','true','Controls access to view Program Stop');
INSERT INTO YukonRoleProperty VALUES (-90030,-900,'Program Current Gear','true','Controls access to view Program Current Gear');
INSERT INTO YukonRoleProperty VALUES (-90031,-900,'Program Priority','true','Controls access to view Program Priority');
INSERT INTO YukonRoleProperty VALUES (-90032,-900,'Program Reduction','true','Controls access to view Program Reduction');
INSERT INTO YukonRoleProperty VALUES (-90033,-900,'Program Load Capacity','true','Controls access to view Program Load Capacity');

INSERT INTO YukonRoleProperty VALUES (-90034,-900,'Load Group State','true', 'Controls access to view Load Group State');
INSERT INTO YukonRoleProperty VALUES (-90035,-900,'Load Group Last Action','true', 'Controls access to view Load Group Last Action');
INSERT INTO YukonRoleProperty VALUES (-90036,-900,'Load Group Control Statistics','true', 'Controls access to view Load Group Control Statistics');
INSERT INTO YukonRoleProperty VALUES (-90037,-900,'Load Group Reduction','true', 'Controls access to view Load Group Reduction');
INSERT INTO YukonRoleProperty VALUES (-90038,-900,'Load Group Load Capacity','true', 'Controls access to view Load Group Load Capacity'); 
/* End YUK-7917 */

/* Start YUK-7908 */
CREATE TABLE PAOFavorites (
   UserId               NUMERIC              NOT NULL,
   PAObjectId           NUMERIC              NOT NULL,
   CONSTRAINT PK_PAOFavorites PRIMARY KEY (UserId, PAObjectId)
);
GO

CREATE TABLE PAORecentViews (
   PAObjectId           NUMERIC              NOT NULL,
   WhenViewed           DATETIME             NOT NULL,
   CONSTRAINT PK_PAORecentViews PRIMARY KEY (PAObjectId)
);
GO

CREATE INDEX INDX_WhenViewed ON PAORecentViews (
	WhenViewed ASC
);
GO

ALTER TABLE PAOFavorites
   ADD CONSTRAINT FK_PAOFav_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId);
GO

ALTER TABLE PAOFavorites
   ADD CONSTRAINT FK_PAOFav_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserId);
GO

ALTER TABLE PAORecentViews
   ADD CONSTRAINT FK_PAORecentViews_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId);
GO
/* End YUK-7908 */

/* Start YUK-7922 */
CREATE TABLE PAOProperty (
   PAObjectId           NUMERIC              NOT NULL,
   PropertyName         VARCHAR(50)          NOT NULL,
   PropertyValue        VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_PAOProperty PRIMARY KEY (PAObjectId, PropertyName)
);
GO

ALTER TABLE PAOProperty
   ADD CONSTRAINT FK_PAOProp_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId);
GO
/* End YUK-7922 */

/* Start YUK-7826 */
CREATE TABLE EventLog (
   EventLogId           NUMERIC              NOT NULL,
   EventType            VARCHAR(250)         NOT NULL,
   EventTime            DATETIME             NULL,
   String1              VARCHAR(2000)        NULL,
   String2              VARCHAR(2000)        NULL,
   String3              VARCHAR(2000)        NULL,
   String4              VARCHAR(2000)        NULL,
   String5              VARCHAR(2000)        NULL,
   String6              VARCHAR(2000)        NULL,
   Int7                 NUMERIC              NULL,
   Int8                 NUMERIC              NULL,
   Int9                 NUMERIC              NULL,
   Int10                NUMERIC              NULL,
   Date11               DATETIME             NULL,
   Date12               DATETIME             NULL,
   CONSTRAINT PK_EventLog PRIMARY KEY (EventLogId)
);
GO

CREATE INDEX INDX_EventType ON EventLog (
	EventType ASC
);
/* End YUK-7826 */

/* Start YUK-7926 */
UPDATE YukonPAObject 
SET Type='UDP'
WHERE PAObjectId IN (SELECT PAObjectId
                     FROM YukonPAObject PAO, PortTerminalServer PTS
                     WHERE PAO.PAObjectId = PTS.PortId
                     AND PAO.Type = 'Terminal Server' 
                     AND PTS.IPAddress = 'UDP');
/* End YUK-7926 */

/* Start YUK-7840 */
UPDATE YukonPAObject
SET Type = 'INTEGRATION'
WHERE PAOClass = 'TRANSMITTER'
AND Type = 'Integration';
/* End YUK-7840 */

/* Start YUK-7932 */
DELETE FROM JobScheduledRepeating
WHERE JobId IN (Select JobId
                FROM JOB
                WHERE BeanName = 'optOutSchedulerJob');

DELETE FROM JOB
WHERE BeanName = 'optOutSchedulerJob';

INSERT INTO YukonServices VALUES (10, 'OptOut', 'classpath:com/cannontech/services/optOut/optOutContext.xml', '(none)', '(none)', 'ServiceManager');
/* Start YUK-7932 */

/* Start YUK-7903 */
UPDATE YukonRoleProperty
SET KeyName = 'Enable/Disable Scripts',
    Description = 'Controls access to enable or disable a script.'
WHERE RolePropertyId = -21200;

INSERT INTO YukonRoleProperty VALUES (-21201,-212,'Manage Schedules','true','Controls access to create, delete, or update scheduled reads. If false, access is view only.');
/* End YUK-7903 */

/* Start YUK-7902 */
IF 0 < (SELECT COUNT(*)
           FROM LMHardwareControlGroup
           WHERE ProgramId = -9999)
               RAISERROR('The database update has encountered a data integrity issue. Please refer to YUK-7902 for more information on how to manually update the data to resolve this issue.', 16, 1);
GO 
/* End YUK-7902 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
