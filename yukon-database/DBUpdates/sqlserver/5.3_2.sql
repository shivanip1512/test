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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
