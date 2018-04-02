/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-17960 */
CREATE TABLE CollectionAction  (
    CollectionActionId  NUMBER          NOT NULL,
    Action              VARCHAR2(50)    NOT NULL,
    StartTime           DATE            NOT NULL,
    StopTime            DATE,
    Status              VARCHAR2(50)    NOT NULL,
    UserName            VARCHAR2(100)   NOT NULL,
    CONSTRAINT PK_CollectionAction PRIMARY KEY (CollectionActionId)
);

CREATE TABLE CollectionActionCommandRequest  (
    CollectionActionId   NUMBER         NOT NULL,
    CommandRequestExecId NUMBER         NOT NULL,
    CONSTRAINT PK_CollectionActionCommandRequest PRIMARY KEY (CollectionActionId, CommandRequestExecId)
);

CREATE TABLE CollectionActionInput  (
    CollectionActionId   NUMBER         NOT NULL,
    InputOrder           NUMBER         NOT NULL,
    Description          VARCHAR2(50)   NOT NULL,
    Value                VARCHAR2(1000) NOT NULL,
    CONSTRAINT PK_CollectionActionInput PRIMARY KEY (CollectionActionId, InputOrder)
);

CREATE TABLE CollectionActionRequest  (
    CollectionActionRequestId NUMBER    NOT NULL,
    CollectionActionId   NUMBER         NOT NULL,
    PAObjectId           NUMBER         NOT NULL,
    Result               VARCHAR2(50)   NOT NULL,
    CONSTRAINT PK_CollectionActionRequest PRIMARY KEY (CollectionActionRequestId)
);

CREATE INDEX INDX_Car_CollectionActionId ON CollectionActionRequest (
    CollectionActionId ASC
);

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

INSERT INTO DBUpdates VALUES ('YUK-17960', '7.1.0', SYSDATE);
/* @end YUK-17960 */

/* @start YUK-18076 */
ALTER TABLE Display DROP COLUMN Title;

INSERT INTO DBUpdates VALUES ('YUK-18076', '7.1.0', SYSDATE);
/* @end YUK-18076 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '26-FEB-2018', 'Latest Update', 0, SYSDATE);*/
