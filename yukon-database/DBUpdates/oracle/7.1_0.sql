/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18076 */
ALTER TABLE Display DROP COLUMN Title;

INSERT INTO DBUpdates VALUES ('YUK-18076', '7.1.0', SYSDATE);
/* @end YUK-18076 */

/* @start YUK-18086 if YUK-17960 */
/* YUK-17960 had previously created the PK incorrectly.  This will simply re-name it if that YUK has been executed */
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT PK_CollectionActionCommandRequest TO PK_CACommandRequest;
ALTER INDEX PK_CollectionActionCommandRequest RENAME TO PK_CACommandRequest;

ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionCR_CollectionAction TO FK_CACR_CollectionAction;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionCR_CommandRequestExec TO FK_CACR_CommandRequestExec;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionI_CollectionAction TO FK_CAInput_CollectionAction;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionR_CollectionAction TO FK_CARequest_CollectionAction;
ALTER TABLE CollectionActionCommandRequest RENAME CONSTRAINT FK_CollectionActionR_YukonPAObject TO FK_CARequest_YukonPAObject;

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', GETDATE());
/* @end YUK-18086 */

/* @start YUK-18086 */
/* If that YUK has not been executed, create the tables from scratch with the correct PK name already set */
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
    CONSTRAINT PK_CACommandRequest PRIMARY KEY (CollectionActionId, CommandRequestExecId)
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
    ADD CONSTRAINT FK_CACR_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
        ON DELETE CASCADE;

ALTER TABLE CollectionActionCommandRequest
    ADD CONSTRAINT FK_CACR_CommandRequestExec FOREIGN KEY (CommandRequestExecId)
        REFERENCES CommandRequestExec (CommandRequestExecId)
        ON DELETE CASCADE;

ALTER TABLE CollectionActionInput
    ADD CONSTRAINT FK_CAInput_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
        ON DELETE CASCADE;

ALTER TABLE CollectionActionRequest
    ADD CONSTRAINT FK_CARequest_CollectionAction FOREIGN KEY (CollectionActionId)
        REFERENCES CollectionAction (CollectionActionId)
        ON DELETE CASCADE;

ALTER TABLE CollectionActionRequest
    ADD CONSTRAINT FK_CARequest_YukonPAObject FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectID)
        ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', SYSDATE);
/* @end YUK-18086 */

/* @start YUK-18122 */
UPDATE YukonRoleProperty SET Description = 'Controls access to view Control Area Trigger related information.' 
    WHERE RolePropertyId = -90021;

INSERT INTO DBUpdates VALUES ('YUK-18122', '7.1.0', SYSDATE);
/* @end YUK-18122 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '26-FEB-2018', 'Latest Update', 0, SYSDATE);*/
