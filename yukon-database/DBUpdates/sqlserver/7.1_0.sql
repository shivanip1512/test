/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17960 */
CREATE TABLE CollectionAction (
    CollectionActionId  NUMERIC             NOT NULL,
    Action              VARCHAR(50)         NOT NULL,
    StartTime           DATETIME            NOT NULL,
    StopTime            DATETIME            NULL,
    Status              VARCHAR(50)         NOT NULL,
    UserName            VARCHAR(100)        NOT NULL,
    CONSTRAINT PK_CollectionAction PRIMARY KEY (CollectionActionId)
);

CREATE TABLE CollectionActionCommandRequest (
    CollectionActionId      NUMERIC         NOT NULL,
    CommandRequestExecId    NUMERIC         NOT NULL,
    CONSTRAINT PK_CollectionActionCommandRequest PRIMARY KEY (CollectionActionId, CommandRequestExecId)
);

CREATE TABLE CollectionActionInput (
    CollectionActionId  NUMERIC             NOT NULL,
    InputOrder          NUMERIC             NOT NULL,
    Description         VARCHAR(50)         NOT NULL,
    Value               VARCHAR(1000)       NOT NULL,
    CONSTRAINT PK_CollectionActionInput PRIMARY KEY (CollectionActionId, InputOrder)
);

CREATE TABLE CollectionActionRequest (
    CollectionActionRequestId   NUMERIC     NOT NULL,
    CollectionActionId          NUMERIC     NOT NULL,
    PAObjectId                  NUMERIC     NOT NULL,
    Result                      VARCHAR(50) NOT NULL,
    CONSTRAINT PK_CollectionActionRequest PRIMARY KEY (CollectionActionRequestId)
);
GO

CREATE INDEX INDX_Car_CollectionActionId ON CollectionActionRequest (
    CollectionActionId ASC
);
GO

ALTER TABLE CollectionActionCommandRequest
    ADD CONSTRAINT FK_CollectionActionCR_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionCommandRequest
    ADD CONSTRAINT FK_CollectionActionCR_CommandRequestExec FOREIGN KEY (CommandRequestExecId)
        REFERENCES CommandRequestExec (CommandRequestExecId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionInput
    ADD CONSTRAINT FK_CollectionActionI_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionRequest
    ADD CONSTRAINT FK_CollectionActionR_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
            ON DELETE CASCADE;

ALTER TABLE CollectionActionRequest
    ADD CONSTRAINT FK_CollectionActionR_YukonPAObject FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectID)
            ON DELETE CASCADE;
GO
/* End YUK-17960 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '26-FEB-2018', 'Latest Update', 0, GETDATE());*/