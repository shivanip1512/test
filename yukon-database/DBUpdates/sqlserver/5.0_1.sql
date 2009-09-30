/******************************************/
/**** SQLServer 2000 DBupdates         ****/
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
CREATE TABLE DeviceGroupComposed (
   DeviceGroupComposedId NUMERIC              NOT NULL,
   DeviceGroupId        NUMERIC              NOT NULL,
   CompositionType      VARCHAR(100)         NULL,
   CONSTRAINT PK_DevGroupComp PRIMARY KEY (DeviceGroupComposedId)
);
GO

ALTER TABLE DeviceGroupComposed
   ADD CONSTRAINT FK_DevGroupComp_DevGroup FOREIGN KEY (DeviceGroupId)
      REFERENCES DeviceGroup (DeviceGroupId)
         ON DELETE CASCADE;
GO

CREATE TABLE DeviceGroupComposedGroup (
   DeviceGroupComposedGroupId NUMERIC              NOT NULL,
   DeviceGroupComposedId NUMERIC              NOT NULL,
   GroupName            VARCHAR(255)         NOT NULL,
   IsNot                CHAR(1)              NOT NULL,
   CONSTRAINT PK_DevGroupCompGroup PRIMARY KEY (DeviceGroupComposedGroupId)
);
GO

ALTER TABLE DeviceGroupComposedGroup
   ADD CONSTRAINT FK_DevGrpCompGrp_DevGrpComp FOREIGN KEY (DeviceGroupComposedId)
      REFERENCES DeviceGroupComposed (DeviceGroupComposedId)
         ON DELETE CASCADE;
GO
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
SET Type = Type + ' Route'
WHERE Type IN ('RTC', 'SNPP Terminal', 'WCTP Terminal', 'TNPP Terminal')
AND PAOClass = 'ROUTE';
/* End YUK-7840 */

/* Start YUK-7855 */
ALTER TABLE ScheduledGrpCommandRequest
DROP CONSTRAINT FK_SchGrpComReq_ComReqExec;
GO

ALTER TABLE CommandRequestExec
ADD CommandRequestExecContextId NUMERIC;
GO
UPDATE CommandRequestExec
SET CommandRequestExecContextId = CommandRequestExecId;
GO
ALTER TABLE CommandRequestExec
ALTER COLUMN CommandRequestExecContextId NUMERIC NOT NULL;
GO

EXEC sp_rename
@objname= 'ScheduledGrpCommandRequest.CommandRequestExecId',
@newname = 'CommandRequestExecContextId',
@objtype = 'COLUMN';
/* End YUK-7855 */

/* Start YUK-7873 */
INSERT INTO YukonRoleProperty VALUES(-20215,-202,'Phase Detection','false','Controls access to Phase Detection.');
/* End YUK-7873 */

/* Start YUK-7872 */
UPDATE YukonRoleProperty
SET KeyName = 'Add/Update/Remove Points'
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
      AND DG.ParentDeviceGroupId = 0) DG2
WHERE DG1.DeviceGroupId < 100;

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
                                 AND ParentDeviceGroupId = 0)) DG2
WHERE DeviceGroupId < 100;
/* End YUK-7874 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('5.0', 'Matt K', '24-SEP-2009', 'Latest Update', 1);
