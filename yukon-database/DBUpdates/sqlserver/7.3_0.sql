/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-19587 if YUK-19531 */
/* errors are ignored for an edge case where TotalCount has been dropped already */
/* @error ignore-begin */
ALTER TABLE ScheduledDataImportHistory 
DROP COLUMN TotalCount;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19587', '7.3.0', GETDATE());
/* @end YUK-19587 */

/* @start YUK-19601 */
/* If the 7.2 creation script was used, the table/FK would be added without running the YUK-19601 update */
/* @error ignore-begin */
CREATE TABLE LMItronCycleGear (
    GearId          numeric         NOT NULL,
    CycleOption     nvarchar(20)    NOT NULL,
    CONSTRAINT PK_LMItronCycleGear PRIMARY KEY (GearId)
);
GO

ALTER TABLE LMItronCycleGear
    ADD CONSTRAINT FK_LMItronCycleGear_LMPDirGear FOREIGN KEY (GearId)
    REFERENCES LMProgramDirectGear (GearID)
    ON DELETE CASCADE;
GO

INSERT INTO LMItronCycleGear
SELECT 
    PDG.GearId, 
    'STANDARD' AS CycleOption
FROM LMProgramDirectGear PDG
WHERE PDG.ControlMethod = 'ItronCycle';
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19601', '7.3.0', GETDATE());
/* @end YUK-19601 */

/* @start YUK-19324 */
UPDATE YukonListEntry 
SET EntryText = 'Excellent (12+)"' 
WHERE EntryText = 'Excellant (12+)"';
GO

INSERT INTO DBUpdates VALUES ('YUK-19324', '7.3.0', GETDATE());
/* @end YUK-19324 */

/* @start YUK-19624 */
/* If the 7.2 creation script was used, the row would already exist without running the YUK-19624 update */
/* @error ignore-begin */
INSERT INTO State VALUES(-28, 3, 'On', 0, 6, 0);
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19624', '7.3.0', GETDATE());
/* @end YUK-19624 */

/* @start YUK-19667 */
UPDATE POINT
SET POINTOFFSET = 387
WHERE POINTOFFSET = 362
AND POINTTYPE = 'Analog'
AND POINTNAME = 'Net kW';

UPDATE POINT
SET POINTOFFSET = 388
WHERE POINTOFFSET = 363
AND POINTTYPE = 'Analog'
AND POINTNAME = 'kVAr (Quadrants 1 3)';

INSERT INTO DBUpdates VALUES ('YUK-19667', '7.3.0', GETDATE());
/* @end YUK-19667 */

/* @start YUK-19697 */
DELETE FROM GlobalSetting
WHERE Name IN ('NEST_USERNAME', 'NEST_PASSWORD', 'NEST_SERVER_URL');

INSERT INTO DBUpdates VALUES ('YUK-19697', '7.3.0', GETDATE());
/* @end YUK-19697 */

/* @start YUK-19773 */
/* If the 7.2 creation script was used, the column would already exist without running the YUK-19773 update */
/* @error ignore-begin */
ALTER TABLE LMGroupItronMapping
ADD ItronEventId NUMERIC NULL;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19773', '7.3.0', GETDATE());
/* @end YUK-19773 */

/* @start YUK-19653 */
CREATE INDEX INDX_LMHardConf_AddGrpId ON LMHardwareConfiguration (
    AddressingGroupID ASC
);

INSERT INTO DBUpdates VALUES ('YUK-19653', '7.3.0', GETDATE());
/* @end YUK-19653 */

/* @start YUK-19758 */
UPDATE UNITMEASURE 
SET UOMName = 'ms' 
WHERE UomId = 45 
AND UOMName = 'Ms';

INSERT INTO DBUpdates VALUES ('YUK-19758', '7.3.0', GETDATE());
/* @end YUK-19758 */

/* @start YUK-19780 if YUK-19712 */
ALTER TABLE DeviceConfigState DROP CONSTRAINT FK_DeviceConfigState_CollAct;
GO

ALTER TABLE DeviceConfigState DROP COLUMN CollectionActionId;
GO

ALTER TABLE DeviceConfigState ADD CommandRequestExecId NUMERIC NULL;
GO

ALTER TABLE DeviceConfigState
    ADD CONSTRAINT FK_DevConfigState_CommReqExec FOREIGN KEY (CommandRequestExecId)
    REFERENCES CommandRequestExec (CommandRequestExecId);

INSERT INTO DBUpdates VALUES ('YUK-19780', '7.3.0', GETDATE());
/* @end YUK-19780 */

/* @start YUK-19780 */
CREATE TABLE DeviceConfigState  (
    PaObjectId              NUMERIC             NOT NULL,
    CurrentState            VARCHAR(50)         NOT NULL,
    LastAction              VARCHAR(20)         NOT NULL,
    LastActionStatus        VARCHAR(20)         NOT NULL,
    LastActionStart         DATETIME            NOT NULL,
    LastActionEnd           DATETIME            NULL,
    CommandRequestExecId    NUMERIC             NULL,
    CONSTRAINT PK_DeviceConfigState PRIMARY KEY (PaObjectId)
);

ALTER TABLE DeviceConfigState
    ADD CONSTRAINT FK_DevConfigState_CommReqExec FOREIGN KEY (CommandRequestExecId)
    REFERENCES CommandRequestExec (CommandRequestExecId)
    ON DELETE SET NULL;

ALTER TABLE DeviceConfigState
    ADD CONSTRAINT FK_DeviceConfigState_YukonPao FOREIGN KEY (PaObjectId)
    REFERENCES YukonPAObject (PAObjectID)
    ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-19780', '7.3.0', GETDATE());
/* @end YUK-19780 */

/* @start YUK-19858-1 */
/* @start-block */
IF OBJECT_ID('NmToRfnDeviceData', 'U') IS NOT NULL 
  DROP TABLE NmToRfnDeviceData; 
/* @end-block */

CREATE TABLE DynamicRfnDeviceData (
    DeviceId            NUMERIC         NOT NULL,
    GatewayId           NUMERIC         NULL,
    LastTransferTime    DATETIME        NOT NULL,
    CONSTRAINT PK_NmToRfnDeviceData PRIMARY KEY (DeviceId)
);
GO

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
    GatewayId ASC
);
GO

ALTER TABLE DynamicRfnDeviceData
    ADD CONSTRAINT FK_DynRfnDevData_RfnAddr_DevId FOREIGN KEY (DeviceId)
    REFERENCES RfnAddress (DeviceId)
    ON DELETE CASCADE;

ALTER TABLE DynamicRfnDeviceData
    ADD CONSTRAINT FK_DynRfnDevData_RfnAddr_GatId FOREIGN KEY (GatewayId)
    REFERENCES RfnAddress (DeviceId);
GO

INSERT INTO DBUpdates VALUES ('YUK-19858-1', '7.3.0', GETDATE());
/* @end YUK-19858-1 */

-- YOU ARE HERE

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.3', '02-FEB-2019', 'Latest Update', 0, GETDATE());*/
