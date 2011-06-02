/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9847 */
CREATE TABLE ArchiveDataAnalysis  (
   AnalysisId           NUMBER                          NOT NULL,
   Attribute            VARCHAR2(60)                    NOT NULL,
   IntervalLength       NUMBER                          NOT NULL,
   LastChangeId         NUMBER                          NOT NULL,
   RunDate              DATE                            NOT NULL,
   ExcludeBadPointQualities CHAR(1)                         NOT NULL,
   StartDate            DATE                            NOT NULL,
   StopDate             DATE                            NOT NULL,
   CONSTRAINT PK_ArcDataAnal PRIMARY KEY (AnalysisId)
);

CREATE TABLE ArchiveDataAnalysisSlotValues  (
   SlotValueId          NUMBER                          NOT NULL,
   DeviceId             NUMBER                          NOT NULL,
   SlotId               NUMBER                          NOT NULL,
   HasValue             CHAR(1)                         NOT NULL,
   ChangeId             NUMBER                          NULL,
   CONSTRAINT PK_ArcDataAnalSlotValues PRIMARY KEY (SlotValueId)
);

CREATE TABLE ArchiveDataAnalysisSlots  (
   SlotId               NUMBER                          NOT NULL,
   AnalysisId           NUMBER                          NOT NULL,
   Timestamp            DATE                            NOT NULL,
   CONSTRAINT PK_ArcDataAnalSlots PRIMARY KEY (SlotId)
);

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
WHERE Type = 'LTC' 
  OR Type = 'PO_REGULATOR' 
  OR Type = 'GO_REGULATOR';

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
   EventId              NUMBER                          NOT NULL,
   IntegrationType      VARCHAR2(50)                    NOT NULL,
   StartTime            DATE                            NOT NULL,
   GroupId              NUMBER                          NOT NULL,
   LMControlHistoryId   NUMBER,
   CONSTRAINT PK_ZBContEvent PRIMARY KEY (EventId)
);

CREATE TABLE ZBControlEventDevice  (
   EventId              NUMBER                          NOT NULL,
   DeviceId             NUMBER                          NOT NULL,
   DeviceAck            CHAR                            NOT NULL,
   StartTime            DATE							NULL,
   StopTime             DATE							NULL,
   Canceled             CHAR							NULL,
   CONSTRAINT PK_ZBContEventDev PRIMARY KEY (EventId, DeviceId)
);

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
/* End YUK-9855 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
