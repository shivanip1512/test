/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9847 */
CREATE TABLE ArchiveDataAnalysis (
   AnalysisId           NUMERIC              NOT NULL,
   Attribute            varCHAR(60)          NOT NULL,
   IntervalLength       NUMERIC              NOT NULL,
   LastChangeId         NUMERIC              NOT NULL,
   RunDate              DATETIME             NOT NULL,
   ExcludeBadPointQualities CHAR(1)          NOT NULL,
   StartDate            DATETIME             NOT NULL,
   StopDate             DATETIME             NOT NULL,
   CONSTRAINT PK_ArcDataAnal PRIMARY KEY (AnalysisId)
);

CREATE TABLE ArchiveDataAnalysisSlotValues (
   SlotValueId          NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   SlotId               NUMERIC              NOT NULL,
   HasValue             CHAR(1)              NOT NULL,
   ChangeId             NUMERIC              NULL,
   CONSTRAINT PK_ArcDataAnalSlotValues PRIMARY KEY (SlotValueId)
);

CREATE TABLE ArchiveDataAnalysisSlots (
   SlotId               NUMERIC              NOT NULL,
   AnalysisId           NUMERIC              NOT NULL,
   Timestamp            DATETIME             NOT NULL,
   CONSTRAINT PK_ArcDataAnalSlots PRIMARY KEY (SlotId)
);
GO

ALTER TABLE ArchiveDataAnalysisSlotValues
    ADD CONSTRAINT FK_ArcDataAnalSlotVal_ArcData FOREIGN KEY (SlotId)
        REFERENCES ArchiveDataAnalysisSlots (SlotId)
            ON DELETE CASCADE;

ALTER TABLE ArchiveDataAnalysisSlotValues
    ADD CONSTRAINT FK_ArchDataAnalSlotVal_Device FOREIGN KEY (DeviceId)
        REFERENCES DEVICE (DeviceId)
            ON DELETE CASCADE;

ALTER TABLE ArchiveDataAnalysisSlots
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
WHERE Type = 'LTC' 
  OR Type = 'PO_REGULATOR' 
  OR Type = 'GO_REGULATOR';

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

INSERT INTO ZoneRegulator (RegulatorId, ZoneId)
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
   EventId              NUMERIC              NOT NULL,
   IntegrationType      VARCHAR(50)          NOT NULL,
   StartTime            DATETIME             NOT NULL,
   GroupId              NUMERIC              NOT NULL,
   LMControlHistoryId   NUMERIC              null,
   CONSTRAINT PK_ZBContEvent PRIMARY KEY (EventId)
);

CREATE TABLE ZBControlEventDevice (
   EventId              NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   DeviceAck            CHAR                 NOT NULL,
   StartTime            DATETIME             NULL,
   StopTime             DATETIME             NULL,
   Canceled             CHAR                 NULL,
   CONSTRAINT PK_ZBContEventDev PRIMARY KEY (EventId, DeviceId)
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
    ADD CONSTRAINT FK_ZBContEventDev_ZBContEvent FOREIGN KEY (EventId)
        REFERENCES ZBControlEvent (EventId)
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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
