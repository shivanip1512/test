/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9847 */
CREATE TABLE ArchiveDataAnalysis  (
   AnalysisId           NUMBER                          NOT NULL,
   Attribute            VARCHAR2(60)                    NOT NULL,
   IntervalLengthInMillis NUMBER                          NOT NULL,
   LastChangeId         NUMBER                          NOT NULL,
   RunDate              DATE                            NOT NULL,
   ExcludeBadPointQualities CHAR(1)                         NOT NULL,
   StartDate            DATE                            NOT NULL,
   StopDate             DATE                            NOT NULL,
   CONSTRAINT PK_ArcDataAnal PRIMARY KEY (AnalysisId)
);

CREATE TABLE ArchiveDataAnalysisSlotValue  (
   SlotValueId          NUMBER                          NOT NULL,
   DeviceId             NUMBER                          NOT NULL,
   SlotId               NUMBER                          NOT NULL,
   ChangeId             NUMBER                          NULL,
   CONSTRAINT PK_ArcDataAnalSlotValue PRIMARY KEY (SlotValueId)
);

CREATE TABLE ArchiveDataAnalysisSlot  (
   SlotId               NUMBER                          NOT NULL,
   AnalysisId           NUMBER                          NOT NULL,
   StartTime            DATE                            NOT NULL,
   CONSTRAINT PK_ArcDataAnalSlot PRIMARY KEY (SlotId)
);

ALTER TABLE ArchiveDataAnalysisSlotValue
    ADD CONSTRAINT FK_ArcDataAnalSlotVal_ArcData FOREIGN KEY (SlotId)
        REFERENCES ArchiveDataAnalysisSlot (SlotId)
            ON DELETE CASCADE;

ALTER TABLE ArchiveDataAnalysisSlotValue
    ADD CONSTRAINT FK_ArchDataAnalSlotVal_Device FOREIGN KEY (DeviceId)
        REFERENCES DEVICE (DeviceId)
            ON DELETE CASCADE;

ALTER TABLE ArchiveDataAnalysisSlot
    ADD CONSTRAINT FK_ArcDataAnalSlots_ArcDataAna FOREIGN KEY (AnalysisId)
        REFERENCES ArchiveDataAnalysis (AnalysisId)
            ON DELETE CASCADE;
/* End YUK-9847 */

/* Start YUK-9797 */
UPDATE YukonPAObject
SET PAOClass = 'CAPCONTROL'
WHERE PAOClass = 'VOLTAGEREGULATOR';

/* Regulator table informaiton */
CREATE TABLE Regulator  (
   RegulatorId          NUMBER                          not null,
   KeepAliveTimer       NUMBER                          not null,
   KeepAliveConfig      NUMBER                          not null,
   CONSTRAINT PK_REG PRIMARY KEY (RegulatorId)
);

ALTER TABLE Regulator
    ADD CONSTRAINT FK_Reg_PAO FOREIGN KEY (RegulatorId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;

INSERT INTO Regulator (RegulatorId, KeepAliveTimer, KeepAliveConfig)
SELECT PaobjectId, 0, 0
FROM YukonPAObject
WHERE Type = 'LTC';

/* RegulatorToZoneMapping table informaiton */
CREATE TABLE RegulatorToZoneMapping  (
   RegulatorId          NUMBER                          NOT NULL,
   ZoneId               NUMBER                          NOT NULL,
   Phase                CHAR,
   CONSTRAINT PK_RegToZoneMap PRIMARY KEY (RegulatorId)
);

ALTER TABLE RegulatorToZoneMapping
    ADD CONSTRAINT FK_ZoneReg_Reg FOREIGN KEY (RegulatorId)
        REFERENCES Regulator (RegulatorId)
            ON DELETE CASCADE;

ALTER TABLE RegulatorToZoneMapping
    ADD CONSTRAINT FK_ZoneReg_Zone FOREIGN KEY (ZoneId)
        REFERENCES Zone (ZoneId)
            ON DELETE CASCADE;

INSERT INTO RegulatorToZoneMapping (RegulatorId, ZoneId)
SELECT RegulatorId, ZoneId
FROM Zone;

/* Remove old regulatorId */
DROP INDEX Indx_Zone_RegId_UNQ;

ALTER TABLE Zone
DROP CONSTRAINT FK_Zone_PAO;

ALTER TABLE Zone
DROP COLUMN RegulatorId;

/* Adding Phase columns */
ALTER TABLE Zone
ADD ZoneType VARCHAR2(40);
UPDATE Zone
SET ZoneType = 'GANG_OPERATED';
ALTER TABLE Zone
MODIFY ZoneType VARCHAR2(40) NOT NULL;

ALTER TABLE PointToZoneMapping 
ADD Phase CHAR;
UPDATE PointToZoneMapping
SET Phase = 'A';
ALTER TABLE PointToZoneMapping
MODIFY Phase CHAR NOT NULL;

ALTER TABLE CCMonitorBankList
ADD Phase CHAR;
/* End YUK-9797 */

/* Start YUK-9855 */
ALTER TABLE DigiControlEventMapping
DROP CONSTRAINT FK_DigiContEventMap_LMContHist;
ALTER TABLE DigiControlEventMapping
DROP CONSTRAINT FK_DigiContEventMap_LMGroup;
ALTER TABLE ZBControlEvent
DROP CONSTRAINT FK_ZBContEvent_DigiContEventMa;
ALTER TABLE ZBControlEvent
DROP CONSTRAINT FK_ZBContEvent_ZBEndPoint;

DROP TABLE DigiControlEventMapping;
DROP TABLE ZBControlEvent;

CREATE TABLE ZBControlEvent  (
   ZBControlEventId     NUMBER                          NOT NULL,
   IntegrationType      VARCHAR2(50)                    NOT NULL,
   StartTime            DATE                            NOT NULL,
   GroupId              NUMBER                          NOT NULL,
   LMControlHistoryId   NUMBER,
   CONSTRAINT PK_ZBContEvent PRIMARY KEY (ZBControlEventId)
);

CREATE TABLE ZBControlEventDevice  (
   ZBControlEventId     NUMBER                          NOT NULL,
   DeviceId             NUMBER                          NOT NULL,
   DeviceAck            CHAR                            NOT NULL,
   StartTime            DATE							NULL,
   StopTime             DATE							NULL,
   Canceled             CHAR							NULL,
   CONSTRAINT PK_ZBContEventDev PRIMARY KEY (ZBControlEventId, DeviceId)
);

ALTER TABLE ZBControlEvent
    ADD CONSTRAINT FK_ZBContEvent_LMContHist FOREIGN KEY (LMControlHistoryId)
        REFERENCES LMControlHistory (LMCtrlHistId);

ALTER TABLE ZBControlEvent
    ADD CONSTRAINT FK_ZBContEvent_LMGroup FOREIGN KEY (GroupId)
        REFERENCES LMGroup (DeviceId)
            ON DELETE CASCADE;

ALTER TABLE ZBControlEventDevice
    ADD CONSTRAINT FK_ZBContEventDev_ZBContEvent FOREIGN KEY (ZBControlEventId)
        REFERENCES ZBControlEvent (ZBControlEventId)
            ON DELETE CASCADE;

ALTER TABLE ZBControlEventDevice
    ADD CONSTRAINT FK_ZBContEventDev_ZBEndPoint FOREIGN KEY (DeviceId)
        REFERENCES ZBEndPoint (DeviceId)
            ON DELETE CASCADE;
/* End YUK-9855 */

/* Start YUK-9886 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -1015; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -1015; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -1015;
/* End YUK-9886 */

/* Start YUK-9878 */
UPDATE Point 
SET PointName = 'Capacitor Bank State' 
WHERE PointName = 'Capacitor bank state'; 
UPDATE Point 
SET PointName = 'Total Op Count' 
WHERE PointName = 'Total op count'; 
UPDATE Point 
SET PointName = 'OV Op Count' 
WHERE PointName = 'OV op count'; 
UPDATE Point 
SET PointName = 'UV Op Count' 
WHERE PointName = 'UV op count'; 
UPDATE Point 
SET PointName = 'OV Threshold' 
WHERE PointName = 'Control OV Set Point'; 

UPDATE Point 
SET PointName = 'UV Threshold' 
WHERE PointName = 'Control UV Set Point'; 
UPDATE Point 
SET PointName = 'Emergency OV Threshold'
WHERE PointName = 'Emergency OV Set Point'; 
UPDATE Point 
SET PointName = 'Emergency UV Threshold' 
WHERE PointName = 'Emergency UV Set Point'; 
UPDATE Point 
SET PointName = 'Comms Loss Time' 
WHERE PointName = 'Com Loss Time';
UPDATE Point 
SET PointName = 'Comms Retry Delay Time' 
WHERE PointName = 'Com Retry Delay Time'; 

UPDATE Point 
SET PointName = 'Neutral Current Alarm Threshold' 
WHERE PointName = 'Neutral Current Alarm Set Point';
/* End YUK-9878 */

/* Start YUK-9871 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -20907; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -20907; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -20907;
/* End YUK-9871 */

/* Start YUK-9620 */
/* @start-block */
DECLARE
    errorFlagCount int;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount 
    FROM YukonPAObject PAO 
    WHERE PAO.Type  = 'INTEGRATION GROUP' 
      OR PAO.Type = 'INTEGRATION' 
      OR PAO.Category = 'Integration Route';
    IF 0 < errorFlagCount THEN
        RAISE_APPLICATION_ERROR(-20001,'The database update requires manual interaction to continue. Please refer to YUK-9620 for more information on removing existing, non-support Integration Groups, Integration Routes, and/or Integration Transmitters.');
    END IF;
END;
/
/* @end-block */

DROP INDEX INDX_LMGroupId_ParamName_UNQ;
ALTER TABLE LMGroupXMLParameter
    DROP CONSTRAINT FK_LMGroupXml_LMGroup;

DROP TABLE LMGroupXMLParameter;
/* End YUK-9620 */

/* Start YUK-9824 */
CREATE INDEX Indx_CmdReqExec_ContId ON CommandRequestExec (
   CommandRequestExecContextId ASC
);

CREATE INDEX Indx_CmdReqExecRes_ExecId_ErrC ON CommandRequestExecResult (
   CommandRequestExecId ASC,
   ErrorCode ASC
);
/* End YUK-9824 */

/* Start YUK-9901 */
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
SELECT DG1.DeviceGroupId, 'Attributes', DG2.ParentDeviceGroupId, 'NOEDIT_NOMOD', 'STATIC'
FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.DeviceGroupId < 100) DG1,
      (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.GroupName = 'System'
       AND DG.ParentDeviceGroupId = 0) DG2;

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
SELECT DG1.DeviceGroupId, 'Supported', DG2.ParentDeviceGroupId, 'NOEDIT_NOMOD', 'ATTRIBUTE_DEFINED'
FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.DeviceGroupId < 100) DG1,
      (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.GroupName = 'Attributes'
       AND DG.ParentDeviceGroupId = (SELECT DGSYS.DeviceGroupId
                                     FROM DeviceGroup DGSYS
                                     WHERE DGSYS.GroupName = 'System'
                                     AND DGSYS.ParentDeviceGroupId = 0)) DG2;

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type)
SELECT DG1.DeviceGroupId, 'Existing', DG2.ParentDeviceGroupId, 'NOEDIT_NOMOD', 'ATTRIBUTE_EXISTS'
FROM (SELECT MAX(DG.DeviceGroupID)+1 DeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.DeviceGroupId < 100) DG1,
      (SELECT MAX(DG.DeviceGroupId) ParentDeviceGroupId
       FROM DeviceGroup DG
       WHERE DG.GroupName = 'Attributes'
       AND DG.ParentDeviceGroupId = (SELECT DGSYS.DeviceGroupId
                                     FROM DeviceGroup DGSYS
                                     WHERE DGSYS.GroupName = 'System'
                                     AND DGSYS.ParentDeviceGroupId = 0)) DG2;

UPDATE ValidationMonitor
SET GroupName = '/System/Attributes/Existing/Usage Reading'
WHERE ValidationMonitorId = 1;
/* End YUK-9901 */

/* Start YUK-9900 */
UPDATE YukonRoleProperty
SET KeyName = 'Demand Response',
    Description = 'Allows access to the Demand Response control web application'
WHERE RolePropertyId = -90002;
/* End YUK-9900 */

/* Start YUK-9924 */
UPDATE YukonRoleProperty 
SET DefaultValue = ' ' 
WHERE DefaultValue = '(none)'; 
UPDATE YukonGroupRole 
SET Value = ' ' 
WHERE Value = '(none)'; 
UPDATE YukonUserRole 
SET Value = ' ' 
WHERE Value = '(none)';

UPDATE YukonRoleProperty
SET Description = 'Authentication method. Possible values are leaving the field empty | PAP, [chap, others to follow soon]');
WHERE RolePropertyId = -1304;
/* End YUK-9924 */

/* Start YUK-9928 */
UPDATE YukonRole
SET RoleName = 'Cap Control Settings'
WHERE RoleId = -700;
/* End YUK-9928 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
