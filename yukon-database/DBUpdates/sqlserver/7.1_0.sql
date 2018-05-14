/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-18076 */
ALTER TABLE Display DROP COLUMN Title;
GO

INSERT INTO DBUpdates VALUES ('YUK-18076', '7.1.0', GETDATE());
/* @end YUK-18076 */

/* @start YUK-18086 if YUK-17960 */
/* YUK-17960 had previously created many names incorrectly.  This will simply re-name them if that YUK has been executed */
exec sp_rename @objname = 'PK_CollectionActionCommandRequest', @newname = 'PK_CACommandRequest';
exec sp_rename @objname = 'FK_CollectionActionCR_CollectionAction', @newname = 'FK_CACR_CollectionAction';
exec sp_rename @objname = 'FK_CollectionActionCR_CommandRequestExec', @newname = 'FK_CACR_CommandRequestExec';
exec sp_rename @objname = 'FK_CollectionActionI_CollectionAction', @newname = 'FK_CAInput_CollectionAction';
exec sp_rename @objname = 'FK_CollectionActionR_CollectionAction', @newname = 'FK_CARequest_CollectionAction';
exec sp_rename @objname = 'FK_CollectionActionR_YukonPAObject', @newname = 'FK_CARequest_YukonPAObject';

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', GETDATE());
/* @end YUK-18086 */

/* @start YUK-18086 */
/* If that YUK has not been executed, create the tables from scratch with the correct names already set */
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
    CONSTRAINT PK_CACommandRequest PRIMARY KEY (CollectionActionId, CommandRequestExecId)
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
GO

INSERT INTO DBUpdates VALUES ('YUK-18086', '7.1.0', GETDATE());
/* @end YUK-18086 */

/* @start YUK-18122 */
UPDATE YukonRoleProperty SET Description = 'Controls access to view Control Area Trigger related information.'
    WHERE RolePropertyId = -90021;
GO

INSERT INTO DBUpdates VALUES ('YUK-18122', '7.1.0', GETDATE());
/* @end YUK-18122 */

/* @start YUK-18164 */
UPDATE RfnAddress SET Model='Sentinel-L0' WHERE Model='SENTINEL-L0';
UPDATE RfnAddress SET Model='Sentinel-L1' WHERE Model='SENTINEL-L1';
UPDATE RfnAddress SET Model='Sentinel-L2' WHERE Model='SENTINEL-L2';
UPDATE RfnAddress SET Model='Sentinel-L3' WHERE Model='SENTINEL-L3';
UPDATE RfnAddress SET Model='Sentinel-L4' WHERE Model='SENTINEL-L4';

UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L0' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L0';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L1' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L1';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L2' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L2';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L3' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L3';
UPDATE YukonPAObject SET PAOName='*RfnTemplate_SCH_Sentinel-L4' WHERE PAOName='*RfnTemplate_SCH_SENTINEL-L4';
GO

INSERT INTO DBUpdates VALUES ('YUK-18164', '7.1.0', GETDATE());
/* @end YUK-18164 */

/* @start YUK-18280 */
ALTER TABLE DeviceDataMonitorProcessor
    ADD ProcessorType VARCHAR(25) NULL,
        ProcessorValue FLOAT NULL,
        RangeMin FLOAT NULL,
        RangeMax FLOAT NULL;
GO

UPDATE DeviceDataMonitorProcessor
SET ProcessorValue = State;
GO

ALTER TABLE DeviceDataMonitorProcessor
DROP COLUMN State;
GO

UPDATE DeviceDataMonitorProcessor
SET ProcessorType = 'STATE';
GO

ALTER TABLE DeviceDataMonitorProcessor
    ALTER COLUMN ProcessorType VARCHAR(25) NOT NULL;
GO

ALTER TABLE DeviceDataMonitorProcessor
    ALTER COLUMN StateGroupId NUMERIC NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-18280', '7.1.0', GETDATE());
/* @end YUk-18280 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '26-FEB-2018', 'Latest Update', 0, GETDATE());*/