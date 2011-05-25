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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
