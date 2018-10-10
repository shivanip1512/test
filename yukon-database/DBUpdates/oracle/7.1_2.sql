/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18870 */
CREATE TABLE LMNestLoadShapingGear  (
    GearId               NUMBER                          NOT NULL,
    PreparationOption    VARCHAR2(20)                    NOT NULL,
    PeakOption           VARCHAR2(20)                    NOT NULL,
    PostPeakOption       VARCHAR2(20)                    NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);

ALTER TABLE LMNestLoadShapingGear
    ADD CONSTRAINT FK_NLSGear_LMProgramDirectGear FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearID)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18870', '7.1.2', SYSDATE);
/* @end YUK-18870 */

/* @start YUK-18868 */
CREATE TABLE NestSync  (
    SyncId               NUMBER                          NOT NULL,
    SyncStartTime        DATE                            NOT NULL,
    SyncStopTime         DATE,
    CONSTRAINT PK_NestSync PRIMARY KEY (SyncId)
);

CREATE TABLE NestSyncDetail  (
    SyncDetailId         NUMBER                          NOT NULL,
    SyncId               NUMBER                          NOT NULL,
    SyncType             VARCHAR2(60)                    NOT NULL,
    SyncReasonKey        VARCHAR2(100)                   NOT NULL,
    SyncReasonValue      VARCHAR2(100),
    SyncActionKey        VARCHAR2(100)                   NOT NULL,
    SyncActionValue      VARCHAR2(100),
    CONSTRAINT PK_NestSyncDetail PRIMARY KEY (SyncDetailId)
);

ALTER TABLE NestSyncDetail
    ADD CONSTRAINT FK_NestSync_NestSyncDetail FOREIGN KEY (SyncId)
        REFERENCES NestSync (SyncId)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18868', '7.1.2', SYSDATE);
/* @end YUK-18868 */

/* @start YUK-18897 */
CREATE TABLE LMNestControlEvent  (
    NestControlEventId      NUMBER          NOT NULL,
    NestGroup               VARCHAR2(20)    NOT NULL,
    NestKey                 VARCHAR2(20)    NOT NULL,
    StartTime               DATE            NOT NULL,
    StopTime                DATE,
    CancelRequestTime       DATE,
    CancelResponse          VARCHAR2(200),
    CONSTRAINT PK_LMNestControlEvent PRIMARY KEY (NestControlEventId)
);

INSERT INTO DBUpdates VALUES ('YUK-18897', '7.1.2', SYSDATE);
/* @end YUK-18897 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '07-SEP-2018', 'Latest Update', 2, SYSDATE);*/