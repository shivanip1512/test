/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-18870 */
CREATE TABLE LMNestLoadShapingGear (
    GearId               NUMERIC              NOT NULL,
    PreparationOption    VARCHAR(20)          NOT NULL,
    PeakOption           VARCHAR(20)          NOT NULL,
    PostPeakOption       VARCHAR(20)          NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);
GO

ALTER TABLE LMNestLoadShapingGear
    ADD CONSTRAINT FK_NLSGear_LMProgramDirectGear FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearID)
            ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-18870', '7.1.2', GETDATE());
/* @end YUK-18870 */

/* @start YUK-18868 */
CREATE TABLE NestSync (
   SyncId               NUMERIC             NOT NULL,
   SyncStartTime        DATETIME            NOT NULL,
   SyncStopTime         DATETIME            NULL,
   CONSTRAINT PK_NestSync PRIMARY KEY (SyncId)
);

CREATE TABLE NestSyncDetail (
    SyncDetailId        NUMERIC             NOT NULL,
    SyncId              NUMERIC             NOT NULL,
    SyncType            VARCHAR(60)         NOT NULL,
    SyncReasonKey       VARCHAR(100)        NOT NULL,
    SyncReasonValue     VARCHAR(100)        NULL,
    SyncActionKey       VARCHAR(100)        NOT NULL,
    SyncActionValue     VARCHAR(100)        NULL,
    CONSTRAINT PK_NestSyncDetail PRIMARY KEY (SyncDetailId)
);
GO

ALTER TABLE NestSyncDetail
    ADD CONSTRAINT FK_NestSync_NestSyncDetail FOREIGN KEY (SyncId)
        REFERENCES NestSync (SyncId)
            ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-18868', '7.1.2', GETDATE());
/* @end YUK-18868 */

/* @start YUK-18897 */
CREATE TABLE LMNestControlEvent (
    NestControlEventId      NUMERIC         NOT NULL,
    NestGroup               VARCHAR(20)     NOT NULL,
    NestKey                 VARCHAR(20)     NOT NULL,
    StartTime               DATETIME        NOT NULL,
    StopTime                DATETIME        NULL,
    CancelRequestTime       DATETIME        NULL,
    CancelResponse          VARCHAR(200)   NULL,
    CONSTRAINT PK_LMNestControlEvent PRIMARY KEY (NestControlEventId)
);

INSERT INTO DBUpdates VALUES ('YUK-18897', '7.1.2', GETDATE());
/* @end YUK-18897 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '07-SEP-2018', 'Latest Update', 2, GETDATE());*/