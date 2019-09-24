/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-19063-1 if YUK-19063 */
ALTER TABLE MeterProgramAssignment
DROP CONSTRAINT FK_MeterProgramAssignment_Device;

ALTER TABLE MeterProgramAssignment
DROP CONSTRAINT FK_MeterProgramAssignment_MeterProgram;

ALTER TABLE MeterProgramStatus
DROP CONSTRAINT FK_MeterProgramStatus_Device;
GO

ALTER TABLE MeterProgramAssignment
    ADD CONSTRAINT FK_MeterProgramAssignment_DeviceMG FOREIGN KEY (DeviceId)
    REFERENCES DEVICEMETERGROUP (DEVICEID)
    ON DELETE CASCADE;

ALTER TABLE MeterProgramAssignment
    ADD CONSTRAINT FK_MeterProgramAssignment_MeterProgram FOREIGN KEY (Guid)
    REFERENCES MeterProgram (Guid)
    ON DELETE CASCADE;

ALTER TABLE MeterProgramStatus
    ADD CONSTRAINT FK_MeterProgramStatus_DeviceMG FOREIGN KEY (DeviceId)
    REFERENCES DEVICEMETERGROUP (DEVICEID)
    ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-19063-1', '7.3.1', GETDATE());
/* @end YUK-19063-1 */

/* @start YUK-19063-1 */
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

INSERT INTO DBUpdates VALUES ('YUK-19063-1', '7.3.1', GETDATE());
/* @end YUK-19063-1 */

/* @start YUK-20568-1 */
UPDATE YukonGroupRole
SET Value = 'NO_ACCESS'
WHERE Value = 'RESTRICTED'
AND RolePropertyID IN (-90049, -21406);
GO

UPDATE YukonGroupRole
SET Value = 'RESTRICTED'
WHERE Value = 'LIMITED'
AND RolePropertyID IN (-90049, -21406, -21405);

UPDATE YukonGroupRole
SET Value = 'UPDATE'
WHERE Value = 'CREATE'
AND RolePropertyID = -21405;
GO

INSERT INTO DBUpdates VALUES ('YUK-20568-1', '7.3.1', GETDATE());
/* @end YUK-20568-1 */

/*@start YUK-20568-2 */
UPDATE YukonRoleProperty
SET DefaultValue = 'RESTRICTED'
WHERE RolePropertyID IN (-90049);
GO

UPDATE YukonRoleProperty
SET DefaultValue = 'UPDATE'
WHERE RolePropertyID IN (-21200, -21404, -21405, -21406);
GO

INSERT INTO DBUpdates VALUES ('YUK-20568-2', '7.3.1', GETDATE());
/* @end YUK-20568-2 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.3', '13-AUG-2019', 'Latest Update', 1, GETDATE());*/
