/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7845 */
/* @error ignore-begin */
ALTER TABLE CommandRequestExecResult
   DROP CONSTRAINT FK_ComReqExecResult_Device;

ALTER TABLE CommandRequestExecResult
   DROP CONSTRAINT FK_ComReqExecResult_Route;
/* @error ignore-end */
/* End YUK-7845 */

/* Start YUK-7850 */
CREATE TABLE DeviceGroupComposed  (
   DeviceGroupComposedId NUMBER                          NOT NULL,
   DeviceGroupId        NUMBER                          NOT NULL,
   CompositionType      VARCHAR2(100),
   CONSTRAINT PK_DevGroupComp PRIMARY KEY (DeviceGroupComposedId)
);

ALTER TABLE DeviceGroupComposed
   ADD CONSTRAINT FK_DevGroupComp_DevGroup FOREIGN KEY (DeviceGroupId)
      REFERENCES DeviceGroup (DeviceGroupId)
      ON DELETE CASCADE;

CREATE TABLE DeviceGroupComposedGroup  (
   DeviceGroupComposedGroupId NUMBER                          NOT NULL,
   DeviceGroupComposedId NUMBER                          NOT NULL,
   GroupName            VARCHAR2(255)                   NOT NULL,
   IsNot                CHAR(1)                         NOT NULL,
   CONSTRAINT PK_DevGroupCompGroup PRIMARY KEY (DeviceGroupComposedGroupId)
);

ALTER TABLE DeviceGroupComposedGroup
   ADD CONSTRAINT FK_DevGrpCompGrp_DevGrpComp FOREIGN KEY (DeviceGroupComposedId)
      REFERENCES DeviceGroupComposed (DeviceGroupComposedId)
      ON DELETE CASCADE;
/* End YUK-7850 */

/* Start YUK-7847 */
INSERT INTO StateGroup VALUES (-10, 'PhaseStatus', 'Status');

INSERT INTO State VALUES (-10, 0, 'Unknown', 0, 6, 0);
INSERT INTO State VALUES (-10, 1, 'A', 1, 6, 0);
INSERT INTO State VALUES (-10, 2, 'B', 10, 6, 0);
INSERT INTO State VALUES (-10, 3, 'C', 3, 6, 0);
INSERT INTO State VALUES (-10, 4, 'AB', 4, 6, 0);
INSERT INTO State VALUES (-10, 5, 'AC', 5, 6, 0);
INSERT INTO State VALUES (-10, 6, 'BC', 7, 6, 0);
INSERT INTO State VALUES (-10, 7, 'ABC', 8, 6, 0);
/* End YUK-7847 */

/* Start YUK-7840 */
UPDATE YukonPAObject
SET Type = 'Integration'
WHERE Type = 'XML'
AND PAOClass = 'TRANSMITTER';

UPDATE YukonPAObject
SET Type = 'Integration Route'
WHERE Type = 'XML'
AND PAOClass = 'ROUTE';

UPDATE YukonPAObject
SET Type = CONCAT(Type,' Route')
WHERE Type IN ('RTC', 'SNPP Terminal', 'WCTP Terminal', 'TNPP Terminal')
AND PAOClass = 'ROUTE';
/* End YUK-7840 */

/* Start YUK-7855 */
ALTER TABLE ScheduledGrpCommandRequest
DROP CONSTRAINT FK_SchGrpComReq_ComReqExec;

ALTER TABLE CommandRequestExec
ADD CommandRequestExecContextId NUMERIC;
UPDATE CommandRequestExec
SET CommandRequestExecContextId = CommandRequestExecId;
ALTER TABLE CommandRequestExec
MODIFY CommandRequestExecContextId NUMERIC NOT NULL;

ALTER TABLE ScheduledGrpCommandRequest
RENAME COLUMN CommandRequestExecId TO CommandRequestExecContextId;
/* End YUK-7855 */

/* Start YUK-7873 */
INSERT INTO YukonRoleProperty VALUES(-20215,-202,'Phase Detection','false','Controls access to Phase Detection.');
/* End YUK-7873 */

/* Start YUK-7872 */
UPDATE YukonRoleProperty
SET KeyName = 'Add/Update/Remove Points', 
    Description = 'Controls access to Add, Update and Remove Points collection actions.'
WHERE RolePropertyId = -21308;
/* End YUK-7872 */

/* Start YUK-7874 */
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
SELECT DG1.DeviceGroupId,
       'Disconnect',
       DG2.ParentDeviceGroupId,
       'NOEDIT_MOD',
       'STATIC'
FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
	 (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.GroupName = 'System'
      AND DG.ParentDeviceGroupId = 0) DG2;

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
SELECT DG1.DeviceGroupId,
       'Collars',
       DG2.ParentDeviceGroupId,
       'NOEDIT_MOD',
       'METERS_DISCONNECT_COLLAR'
FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DeviceGroupId) ParentDeviceGroupId
      FROM DeviceGroup
      WHERE GroupName = 'Disconnect'
      AND ParentDeviceGroupId = (SELECT MAX(DeviceGroupId)
                                 FROM DeviceGroup
                                 WHERE GroupName = 'System'
                                 AND ParentDeviceGroupId = 0)) DG2;
/* End YUK-7874 */

/* Start YUK-7398 */
UPDATE Command 
SET Category = 'VersacomSerial' 
WHERE Category = 'ALL LCRs';

INSERT INTO Command VALUES(-174, 'control xcom shed 5m relay 1', 'Shed 5-min Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-175, 'control xcom shed 5m relay 2', 'Shed 5-min Relay 2', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-176, 'control xcom shed 5m relay 3', 'Shed 5-min Relay 3', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-177, 'control xcom restore relay 1', 'Restore Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-178, 'control xcom restore relay 2', 'Restore Relay 2', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-179, 'control xcom restore relay 3', 'Restore Relay 3', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-180, 'control xcom cycle 50 period 30 relay 1', 'Cycle 50 Period-30 Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-181, 'control xcom cycle terminate relay 1', 'Terminate Cycle Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-182, 'control xcom cycle terminate relay 2', 'Terminate Cycle Relay 2', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-183, 'control xcom cycle terminate relay 3', 'Terminate Cycle Relay 3', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-184, 'putconfig xcom service out', 'Set to Out-of-Service', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-185, 'putconfig xcom service in', 'Set to In-Service', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-186, 'putconfig xcom led yyy', 'Configure LEDS (load, test, report)', 'ExpresscomSerial');

UPDATE DeviceTypeCommand 
SET CommandId = -174 
WHERE DeviceCommandId = -132; 
UPDATE DeviceTypeCommand 
SET CommandId = -175 
WHERE DeviceCommandId = -133; 
UPDATE DeviceTypeCommand 
SET CommandId = -176 
WHERE DeviceCommandId = -134; 
UPDATE DeviceTypeCommand 
SET CommandId = -177 
WHERE DeviceCommandId = -135; 
UPDATE DeviceTypeCommand 
SET CommandId = -178 
WHERE DeviceCommandId = -136; 
UPDATE DeviceTypeCommand 
SET CommandId = -179 
WHERE DeviceCommandId = -137; 
UPDATE DeviceTypeCommand 
SET CommandId = -180 
WHERE DeviceCommandId = -138; 
UPDATE DeviceTypeCommand 
SET CommandId = -181 
WHERE DeviceCommandId = -139; 
UPDATE DeviceTypeCommand 
SET CommandId = -182 
WHERE DeviceCommandId = -140; 
UPDATE DeviceTypeCommand 
SET CommandId = -183 
WHERE DeviceCommandId = -141; 
UPDATE DeviceTypeCommand 
SET CommandId = -184 
WHERE DeviceCommandId = -142; 
UPDATE DeviceTypeCommand 
SET CommandId = -185 
WHERE DeviceCommandId = -143; 
UPDATE DeviceTypeCommand 
SET CommandId = -186 
WHERE DeviceCommandId = -144; 
/* End YUK-7398 */

/* Start YUK-7875 */
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
SELECT DG1.DeviceGroupId,
       'Disconnect',
       DG2.ParentDeviceGroupId,
       'NOEDIT_MOD',
       'STATIC'
FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
	 (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.GroupName = 'Meters'
      AND DG.ParentDeviceGroupId = (SELECT MAX(DeviceGroupId)
                                    FROM DeviceGroup
                                    WHERE GroupName = 'System'
                                    AND ParentDeviceGroupId = 0)) DG2;
/* End YUK-7875 */

/* Start YUK-7881 */
/* @error ignore-begin */
INSERT INTO YukonSelectionList VALUES (1072,'N','(none)','Cap Bank Editor','Controller Type','N');
INSERT INTO YukonSelectionList VALUES (1073,'N','(none)','Cap Bank Editor','Switch Manufacturer','N');
INSERT INTO YukonSelectionList VALUES (1074,'N','(none)','Cap Bank Editor','Type of Switch','N');

INSERT INTO YukonListEntry VALUES (10500,1072,0,'(none)',0);
INSERT INTO YukonListEntry VALUES (10501,1072,1,'CTI DLC',0);
INSERT INTO YukonListEntry VALUES (10502,1072,2,'CTI Paging',0);
INSERT INTO YukonListEntry VALUES (10503,1072,3,'CTI FM',0);
INSERT INTO YukonListEntry VALUES (10504,1072,4,'FP Paging',0);
INSERT INTO YukonListEntry VALUES (10505,1072,5,'Telemetric',0);

INSERT INTO YukonListEntry VALUES (10520,1073,0,'(none)',0);
INSERT INTO YukonListEntry VALUES (10521,1073,1,'ABB',0);
INSERT INTO YukonListEntry VALUES (10522,1073,2,'Cannon Tech',0);
INSERT INTO YukonListEntry VALUES (10523,1073,3,'Cooper',0);
INSERT INTO YukonListEntry VALUES (10524,1073,4,'Trinetics',0);
INSERT INTO YukonListEntry VALUES (10525,1073,5,'Siemens',0);
INSERT INTO YukonListEntry VALUES (10526,1073,6,'Westinghouse',0);
INSERT INTO YukonListEntry VALUES (10527,1073,7,'Mix',0);

INSERT INTO YukonListEntry VALUES (10540,1074,0,'(none)',0);
INSERT INTO YukonListEntry VALUES (10541,1074,1,'Oil',0);
INSERT INTO YukonListEntry VALUES (10542,1074,2,'Vacuum',0);
INSERT INTO YukonListEntry VALUES (10543,1074,3,'Mix',0);
INSERT INTO YukonListEntry VALUES (10544,1074,4,'Hybrid',0);
/* @error ignore-end */
/* End YUK-7881 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('5.0', 'Matt K', '01-OCT-2009', 'Latest Update', 1);
