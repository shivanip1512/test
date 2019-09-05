/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-19063-1 if YUK-19063 */
ALTER TABLE MeterProgramAssignment
DROP CONSTRAINT FK_MeterProgramAssignment_Device;

ALTER TABLE MeterProgramAssignment
DROP CONSTRAINT FK_MeterProgramAssignment_MeterProgram;

ALTER TABLE MeterProgramStatus
DROP CONSTRAINT FK_MeterProgramStatus_Device;

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

INSERT INTO DBUpdates VALUES ('YUK-19063-1', '7.4.0', SYSDATE);
/* @end YUK-19063-1 */

/* @start YUK-19063-1 */
CREATE TABLE MeterProgram  (
    Guid            VARCHAR2(40)        NOT NULL,
    Name            VARCHAR2(100)       NOT NULL,
    PaoType         VARCHAR2(30)        NOT NULL,
    Program         BLOB                NOT NULL,
    CONSTRAINT PK_MeterProgram PRIMARY KEY (Guid)
);

ALTER TABLE MeterProgram
    ADD CONSTRAINT AK_MeterProgram_Name UNIQUE (Name);


CREATE TABLE MeterProgramAssignment  (
    DeviceId        NUMBER              NOT NULL,
    Guid            VARCHAR2(40)        NOT NULL,
    CONSTRAINT PK_MeterProgramAssignment PRIMARY KEY (DeviceId)
);

ALTER TABLE MeterProgramAssignment
    ADD CONSTRAINT FK_MeterProgramAssignment_DeviceMG FOREIGN KEY (DeviceId)
    REFERENCES DEVICEMETERGROUP (DEVICEID)
    ON DELETE CASCADE;

ALTER TABLE MeterProgramAssignment
    ADD CONSTRAINT FK_MeterProgramAssignment_MeterProgram FOREIGN KEY (Guid)
    REFERENCES MeterProgram (Guid)
    ON DELETE CASCADE;

CREATE TABLE MeterProgramStatus  (
    DeviceId        NUMBER              NOT NULL,
    ReportedGuid    VARCHAR2(40)        NOT NULL,
    Source          VARCHAR2(1)         NOT NULL,
    Status          VARCHAR2(20)        NOT NULL,
    LastUpdate      DATE                NOT NULL,
    CONSTRAINT PK_MeterProgramStatus PRIMARY KEY (DeviceId)
);

ALTER TABLE MeterProgramStatus
    ADD CONSTRAINT FK_MeterProgramStatus_DeviceMG FOREIGN KEY (DeviceId)
    REFERENCES DEVICEMETERGROUP (DEVICEID)
    ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-19063-1', '7.4.0', SYSDATE);
/* @end YUK-19063-1 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.3', '13-AUG-2019', 'Latest Update', 1, SYSDATE);*/
