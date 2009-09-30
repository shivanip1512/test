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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('5.0', 'Matt K', '24-SEP-2009', 'Latest Update', 1);
