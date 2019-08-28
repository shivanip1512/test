/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-19063 */
CREATE TABLE MeterProgram (
    Guid            VARCHAR(40)         NOT NULL,
    Name            VARCHAR(100)        NOT NULL,
    PaoType         VARCHAR(30)         NOT NULL,
    Program         VARBINARY(MAX)      NOT NULL,
    CONSTRAINT PK_MeterProgram PRIMARY KEY (Guid)
);
GO

ALTER TABLE MeterProgram
    ADD CONSTRAINT AK_MeterProgram_Name UNIQUE (Name);
GO

CREATE TABLE MeterProgramAssignment (
    DeviceId        NUMERIC             NOT NULL,
    Guid            VARCHAR(40)         NOT NULL,
    CONSTRAINT PK_MeterProgramAssignment PRIMARY KEY (DeviceId)
);
GO

ALTER TABLE MeterProgramAssignment
    ADD CONSTRAINT FK_MeterProgramAssignment_MeterProgram FOREIGN KEY (Guid)
    REFERENCES MeterProgram (Guid)
    ON DELETE CASCADE;
GO

ALTER TABLE MeterProgramAssignment
    ADD CONSTRAINT FK_MeterProgramAssignment_Device FOREIGN KEY (DeviceId)
    REFERENCES DEVICE (DEVICEID)
    ON DELETE CASCADE;
GO

CREATE TABLE MeterProgramStatus (
    DeviceId        NUMERIC              NOT NULL,
    ReportedGuid    VARCHAR(40)          NOT NULL,
    Source          VARCHAR(1)           NOT NULL,
    Status          VARCHAR(20)          NOT NULL,
    LastUpdate      DATETIME             NOT NULL,
    CONSTRAINT PK_MeterProgramStatus PRIMARY KEY (DeviceId)
);
GO

ALTER TABLE MeterProgramStatus
    ADD CONSTRAINT FK_MeterProgramStatus_Device FOREIGN KEY (DeviceId)
    REFERENCES DEVICE (DEVICEID)
    ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-19063', '7.3.1', GETDATE());
/* @end YUK-19063 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.3', '13-AUG-2019', 'Latest Update', 1, GETDATE());*/
