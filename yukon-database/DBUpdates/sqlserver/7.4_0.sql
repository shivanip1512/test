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

INSERT INTO DBUpdates VALUES ('YUK-19063-1', '7.4.0', GETDATE());
/* @end YUK-19063-1 */

/* @start YUK-19063-1 */
/* errors are ignored for an edge case where the tables had been added already */
/* @error ignore-begin */
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
    ADD CONSTRAINT FK_MeterProgramAssignment_DeviceMG FOREIGN KEY (DeviceId)
    REFERENCES DEVICEMETERGROUP (DEVICEID)
    ON DELETE CASCADE;
GO

ALTER TABLE MeterProgramAssignment
    ADD CONSTRAINT FK_MeterProgramAssignment_MeterProgram FOREIGN KEY (Guid)
    REFERENCES MeterProgram (Guid)
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
    ADD CONSTRAINT FK_MeterProgramStatus_DeviceMG FOREIGN KEY (DeviceId)
    REFERENCES DEVICEMETERGROUP (DEVICEID)
    ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-19063-1', '7.4.0', GETDATE());
/* @error ignore-end */
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

INSERT INTO DBUpdates VALUES ('YUK-20568-1', '7.4.0', GETDATE());
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

INSERT INTO DBUpdates VALUES ('YUK-20568-2', '7.4.0', GETDATE());
/* @end YUK-20568-2 */

/* @start YUK-20645 */
DELETE FROM YukonGroupRole 
WHERE RolePropertyID = -20164;

DELETE FROM YukonRoleProperty 
WHERE RolePropertyID = -20164;

INSERT INTO DBUpdates VALUES ('YUK-20645', '7.4.0', GETDATE());
/* @end YUK-20645 */

/* @start YUK-20756 */
DELETE FROM EnergyCompanySetting
WHERE Name = 'METER_MCT_BASE_DESIGNATION';

INSERT INTO DBUpdates VALUES ('YUK-20756', '7.4.0', GETDATE());
/* @end YUK-20756 */

/* @start YUK-20689 */
UPDATE YukonGroupRole
SET Value = 'VIEW'
WHERE Value = 'RESTRICTED'
AND RolePropertyID IN (-21200, -21404, -21405, -21406, -90049);

UPDATE YukonGroupRole
SET Value = 'INTERACT'
WHERE Value = 'LIMITED'
AND RolePropertyID IN (-21200, -21404, -21405, -21406, -90049);

UPDATE YukonRoleProperty
SET DefaultValue = 'VIEW'
WHERE DefaultValue = 'RESTRICTED'
AND RolePropertyID IN (-21200, -21404, -21405, -21406, -90049);

UPDATE YukonRoleProperty
SET DefaultValue = 'INTERACT'
WHERE DefaultValue = 'LIMITED'
AND RolePropertyID IN (-21200, -21404, -21405, -21406, -90049);

INSERT INTO DBUpdates VALUES ('YUK-20689', '7.4.0', GETDATE());
/* @end YUK-20689 */

/* @start YUK-20819 */
ALTER TABLE DeviceMacAddress
ADD SecondaryMacAddress varchar(255) null;
GO

INSERT INTO DBUpdates VALUES ('YUK-20819', '7.3.2', GETDATE());
/* @end YUK-20819 */

/* @start YUK-20780 */
UPDATE CommandRequestUnsupported
SET Type = 'ALREADY_CONFIGURED'
WHERE CommandRequestExecId in 
    (SELECT DISTINCT CommandRequestExecId 
    FROM CollectionActionCommandRequest
    WHERE CollectionActionId in 
        (SELECT CollectionActionId
        FROM CollectionAction
        WHERE Action = 'REMOVE_DATA_STREAMING'))
AND Type = 'NOT_CONFIGURED';

INSERT INTO DBUpdates VALUES ('YUK-20780', '7.4.0', GETDATE());
/* @end YUK-20780 */

/* @start YUK-20801 */
ALTER TABLE MeterProgram
ADD Password VARCHAR(200);
GO

UPDATE MeterProgram
SET Password = '(none)';

ALTER TABLE MeterProgram
ALTER COLUMN Password VARCHAR(200) NOT NULL;
GO

INSERT INTO DBUpdates VALUES ('YUK-20801', '7.4.0', GETDATE());
/* @end YUK-20801 */

/* @start YUK-20788 */
INSERT into YukonRoleProperty
VALUES (-10320, -103, 'Manage Custom Commands', 'VIEW', 'Controls access to the ability to manage custom commands in web commander');

INSERT INTO DBUpdates VALUES ('YUK-20788', '7.4.0', GETDATE());
/* @end YUK-20788 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.4', '13-AUG-2019', 'Latest Update', 0, GETDATE());*/
