/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9847 */
CREATE TABLE ArchiveDataAnalysis (
   AnalysisId           NUMERIC              NOT NULL,
   Attribute            VARCHAR(60)          NOT NULL,
   IntervalLengthInMillis NUMERIC              NOT NULL,
   LastChangeId         NUMERIC              NOT NULL,
   RunDate              DATETIME             NOT NULL,
   ExcludeBadPointQualities CHAR(1)          NOT NULL,
   StartDate            DATETIME             NOT NULL,
   StopDate             DATETIME             NOT NULL,
   CONSTRAINT PK_ArcDataAnal PRIMARY KEY (AnalysisId)
);

CREATE TABLE ArchiveDataAnalysisSlotValue (
   SlotValueId          NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   SlotId               NUMERIC              NOT NULL,
   ChangeId             NUMERIC              NULL,
   CONSTRAINT PK_ArcDataAnalSlotValue PRIMARY KEY (SlotValueId)
);

CREATE TABLE ArchiveDataAnalysisSlot (
   SlotId               NUMERIC              NOT NULL,
   AnalysisId           NUMERIC              NOT NULL,
   StartTime            DATETIME             NOT NULL,
   CONSTRAINT PK_ArcDataAnalSlot PRIMARY KEY (SlotId)
);
GO

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
GO
/* End YUK-9847 */

/* Start YUK-9797 */
UPDATE YukonPAObject
SET PAOClass = 'CAPCONTROL'
WHERE PAOClass = 'VOLTAGEREGULATOR';

/* Regulator table informaiton */
CREATE TABLE Regulator (
   RegulatorId          NUMERIC              NOT NULL,
   KeepAliveTimer       NUMERIC              NOT NULL,
   KeepAliveConfig      NUMERIC              NOT NULL,
   CONSTRAINT PK_Reg PRIMARY KEY (RegulatorId)
);
GO

ALTER TABLE Regulator
    ADD CONSTRAINT FK_Reg_PAO FOREIGN KEY (RegulatorId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;

INSERT INTO Regulator (RegulatorId, KeepAliveTimer, KeepAliveConfig)
SELECT PaobjectId, 0, 0
FROM YukonPAObject
WHERE Type = 'LTC';

/* RegulatorToZoneMapping table informaiton */
CREATE TABLE RegulatorToZoneMapping (
   RegulatorId          NUMERIC              NOT NULL,
   ZoneId               NUMERIC              NOT NULL,
   Phase                CHAR(1)              NULL,
   CONSTRAINT PK_RegToZoneMap PRIMARY KEY (RegulatorId)
);
GO

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
DROP INDEX Zone.Indx_Zone_RegId_UNQ;

ALTER TABLE Zone
DROP CONSTRAINT FK_Zone_PAO;

ALTER TABLE Zone
DROP COLUMN RegulatorId;

/* Adding Phase columns */
ALTER TABLE Zone
ADD ZoneType VARCHAR(40);
GO
UPDATE Zone
SET ZoneType = 'GANG_OPERATED';
GO
ALTER TABLE Zone
ALTER COLUMN ZoneType VARCHAR(40) NOT NULL;
GO

ALTER TABLE PointToZoneMapping 
ADD Phase CHAR;
GO
UPDATE PointToZoneMapping
SET Phase = 'A';
GO
ALTER TABLE PointToZoneMapping
ALTER COLUMN Phase CHAR NOT NULL;
GO

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
GO

DROP TABLE DigiControlEventMapping;
DROP TABLE ZBControlEvent;
GO

CREATE TABLE ZBControlEvent (
   ZBControlEventId     NUMERIC              NOT NULL,
   IntegrationType      VARCHAR(50)          NOT NULL,
   StartTime            DATETIME             NOT NULL,
   GroupId              NUMERIC              NOT NULL,
   LMControlHistoryId   NUMERIC              null,
   CONSTRAINT PK_ZBContEvent PRIMARY KEY (ZBControlEventId)
);

CREATE TABLE ZBControlEventDevice (
   ZBControlEventId     NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   DeviceAck            CHAR                 NOT NULL,
   StartTime            DATETIME             NULL,
   StopTime             DATETIME             NULL,
   Canceled             CHAR                 NULL,
   CONSTRAINT PK_ZBContEventDev PRIMARY KEY (ZBControlEventId, DeviceId)
);
GO

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
GO
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
IF 0 < (SELECT COUNT(*)
        FROM YukonPAObject PAO 
        WHERE PAO.Type = 'INTEGRATION GROUP' 
          OR PAO.Type = 'INTEGRATION' 
          OR PAO.Type = 'Integration Route')
            RAISERROR('The database update requires manual interaction to continue. Please refer to YUK-9620 for more information on removing existing, non-support Integration Groups, Integration Routes, and/or Integration Transmitters.', 16, 1);
GO

DROP INDEX LMGroupXMLParameter.INDX_LMGroupId_ParamName_UNQ;
ALTER TABLE LMGroupXMLParameter
    DROP CONSTRAINT FK_LMGroupXml_LMGroup;
GO

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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
