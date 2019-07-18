/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-19587 if YUK-19531 */
/* errors are ignored for an edge case where TotalCount has been dropped already */
/* @error ignore-begin */
ALTER TABLE ScheduledDataImportHistory 
DROP COLUMN TotalCount;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19587', '7.3.0', SYSDATE);
/* @end YUK-19587 */

/* @start YUK-19601 */
/* If the 7.2 creation script was used, the table/FK would be added without running the YUK-19601 update */
/* @error ignore-begin */
CREATE TABLE LMItronCycleGear  (
    GearId          NUMBER              NOT NULL,
    CycleOption     NVARCHAR2(20)       NOT NULL,
    CONSTRAINT PK_LMItronCycleGear PRIMARY KEY (GearId)
);

ALTER TABLE LMItronCycleGear
    ADD CONSTRAINT FK_LMItronCycleGear_LMPDirGear FOREIGN KEY (GearId)
    REFERENCES LMProgramDirectGear (GearID)
    ON DELETE CASCADE;

INSERT INTO LMItronCycleGear
SELECT 
    PDG.GearId, 
    'STANDARD' AS CycleOption
FROM LMProgramDirectGear PDG
WHERE PDG.ControlMethod = 'ItronCycle';
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19601', '7.3.0', SYSDATE);
/* @end YUK-19601 */

/* @start YUK-19324 */
UPDATE YukonListEntry 
SET EntryText = 'Excellent (12+)"' 
WHERE EntryText = 'Excellant (12+)"';

INSERT INTO DBUpdates VALUES ('YUK-19324', '7.3.0', SYSDATE);
/* @end YUK-19324 */

/* @start YUK-19624 */
/* If the 7.2 creation script was used, the row would already exist without running the YUK-19624 update */
/* @error ignore-begin */
INSERT INTO State VALUES(-28, 3, 'On', 0, 6, 0);
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19624', '7.3.0', SYSDATE);
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

INSERT INTO DBUpdates VALUES ('YUK-19667', '7.3.0', SYSDATE);
/* @end YUK-19667 */

/* @start YUK-19697 */
DELETE FROM GlobalSetting
WHERE Name IN ('NEST_USERNAME', 'NEST_PASSWORD', 'NEST_SERVER_URL');

INSERT INTO DBUpdates VALUES ('YUK-19697', '7.3.0', SYSDATE);
/* @end YUK-19697 */

/* @start YUK-19773 */
/* If the 7.2 creation script was used, the column would already exist without running the YUK-19773 update */
/* @error ignore-begin */
ALTER TABLE LMGroupItronMapping
ADD ItronEventId NUMBER;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19773', '7.3.0', SYSDATE);
/* @end YUK-19773 */

/* @start YUK-19653 */
CREATE INDEX INDX_LMHardConf_AddGrpId ON LMHardwareConfiguration (
    AddressingGroupID ASC
);

INSERT INTO DBUpdates VALUES ('YUK-19653', '7.3.0', SYSDATE);
/* @end YUK-19653 */

/* @start YUK-19758 */
UPDATE UNITMEASURE 
SET UOMName = 'ms' 
WHERE UomId = 45 
AND UOMName = 'Ms';

INSERT INTO DBUpdates VALUES ('YUK-19758', '7.3.0', SYSDATE);
/* @end YUK-19758 */

/* @start YUK-19780 if YUK-19712 */
ALTER TABLE DeviceConfigState DROP CONSTRAINT FK_DeviceConfigState_CollAct;
ALTER TABLE DeviceConfigState DROP COLUMN CollectionActionId;

ALTER TABLE DeviceConfigState ADD CommandRequestExecId NUMBER;

ALTER TABLE DeviceConfigState
    ADD CONSTRAINT FK_DevConfigState_CommReqExec FOREIGN KEY (CommandRequestExecId)
    REFERENCES CommandRequestExec (CommandRequestExecId);

INSERT INTO DBUpdates VALUES ('YUK-19780', '7.3.0', SYSDATE);
/* @end YUK-19780 */

/* @start YUK-19780 */
CREATE TABLE DeviceConfigState  (
    PaObjectId              NUMBER              NOT NULL,
    CurrentState            VARCHAR2(50)        NOT NULL,
    LastAction              VARCHAR2(20)        NOT NULL,
    LastActionStatus        VARCHAR2(20)        NOT NULL,
    LastActionStart         DATE                NOT NULL,
    LastActionEnd           DATE,
    CommandRequestExecId    NUMBER,
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

INSERT INTO DBUpdates VALUES ('YUK-19780', '7.3.0', SYSDATE);
/* @end YUK-19780 */

/* @start YUK-19858-1 */
/* @start-block */
DECLARE
    table_exists INT;
BEGIN
    SELECT COUNT(*) INTO table_exists 
    FROM USER_TABLES
    WHERE table_name = 'NMTORFNDEVICEDATA';
    
    IF table_exists > 0 THEN
        EXECUTE IMMEDIATE 'DROP TABLE NmToRfnDeviceData';
    END IF;
END;
/
/* @end-block */

CREATE TABLE DynamicRfnDeviceData  (
    DeviceId            NUMBER          NOT NULL,
    GatewayId           NUMBER,
    LastTransferTime    DATE            NOT NULL,
    CONSTRAINT PK_NmToRfnDeviceData PRIMARY KEY (DeviceId)
);

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
   GatewayId ASC
);

ALTER TABLE DynamicRfnDeviceData
    ADD CONSTRAINT FK_DynRfnDevData_RfnAddr_DevId FOREIGN KEY (DeviceId)
    REFERENCES RfnAddress (DeviceId)
    ON DELETE CASCADE;

ALTER TABLE DynamicRfnDeviceData
    ADD CONSTRAINT FK_DynRfnDevData_RfnAddr_GatId FOREIGN KEY (GatewayId)
    REFERENCES RfnAddress (DeviceId);

INSERT INTO DBUpdates VALUES ('YUK-19858-1', '7.3.0', SYSDATE);
/* @end YUK-19858-1 */

/* @start YUK-19963 */
/* Will error via. already existing if 7.1.4 or later creation scripts were ran. */
/* @error ignore-begin */
CREATE INDEX INDX_CRE_StartDesc_ExecContId ON CommandRequestExec (
    StartTime DESC,
    CommandRequestExecContextId ASC
);
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19963', '7.3.0', SYSDATE);
/* @end YUK-19963 */

/* @start YUK-19489-1 */
UPDATE YukonWebConfiguration
SET AlternateDisplayName = ','
WHERE AlternateDisplayName IS NULL;

/* In the case where a column is already NULL or NOT NULL, oracle will error */
/* @error ignore-begin */
ALTER TABLE YukonWebConfiguration
MODIFY AlternateDisplayName VARCHAR2(200) NOT NULL;
/* @error ignore-end */

ALTER TABLE EncryptionKey
MODIFY EncryptionKeyType DEFAULT NULL;

ALTER TABLE CCEventLog
MODIFY EventSubtype NUMBER;

UPDATE HoneywellWifiThermostat
SET UserID = 0
WHERE UserID IS NULL;

/* In the case where a column is already NULL or NOT NULL, oracle will error */
/* @error ignore-begin */
ALTER TABLE HoneywellWifiThermostat
MODIFY UserID NUMERIC NOT NULL;

/* In the case where the FK is already named correctly, this will error because the incorrect name cannot be found */
ALTER TABLE NestSyncValue RENAME CONSTRAINT FK_NSDetail_NSValue TO FK_NestSDetail_NestSValue;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19489-1', '7.3.0', SYSDATE);
/* @end YUK-19489-1 */

/* @start YUK-20075 */
/* @error ignore-start */
ALTER TABLE NestSyncValue RENAME CONSTRAINT FK_NestSDetail_NestSValue TO FK_NSDetail_NSValue;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-20075', '7.3.0', SYSDATE);
/* @end YUK-20075 */

/* @start YUK-20229 */
UPDATE POINT
SET 
    POINTOFFSET = 100,
    POINTNAME = 'Outages'
WHERE POINTOFFSET = 24
AND POINTTYPE = 'Analog'
AND PAObjectID IN (
    SELECT YPO.PAObjectID
    FROM YukonPAObject YPO
    WHERE YPO.Type = 'RFN Relay'
);

INSERT INTO DBUpdates VALUES ('YUK-20229', '7.3.0', SYSDATE);
/* @end YUK-20229 */

/* @start YUK-20281 */
INSERT INTO YukonListEntry VALUES (
    (SELECT MAX(YLE.EntryID) + 1 FROM YukonListEntry YLE), 
    1005, 0, 'Electric Meter', 1341);

/* @start-block */
DECLARE
    v_maxEntryId NUMBER;
BEGIN
    SELECT MAX(YLE.EntryID) INTO v_maxEntryId FROM YukonListEntry YLE;

    INSERT INTO YukonListEntry (EntryID, ListID, EntryOrder, EntryText, YukonDefinitionID) 
    SELECT 
        v_maxEntryId + ROW_NUMBER() OVER (ORDER BY YLE.EntryID),
        YLE.ListID,
        YLE.EntryOrder + 1,
        'Electric Meter',
        1341
    FROM YukonSelectionList YSL
    JOIN YukonListEntry YLE
        ON YSL.ListID = YLE.ListID
    WHERE YSL.EnergyCompanyId != -1
    AND YSL.Ordering = 'O'
    AND YSL.ListName = 'DeviceType'
    AND YLE.YukonDefinitionID != 1341
    AND YLE.EntryOrder = (SELECT MAX(EntryOrder) FROM YukonListEntry WHERE ListID = YLE.ListID);
END;
/
/* @end-block */

INSERT INTO DBUpdates VALUES ('YUK-20281', '7.3.0', SYSDATE);
/* @end YUK-20281 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.3', '02-FEB-2019', 'Latest Update', 0, SYSDATE);*/
