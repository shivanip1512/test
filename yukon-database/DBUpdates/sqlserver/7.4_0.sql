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
/* @error ignore-begin */
ALTER TABLE DeviceMacAddress
ADD SecondaryMacAddress varchar(255) null;
GO

INSERT INTO DBUpdates VALUES ('YUK-20819', '7.4.0', GETDATE());
/* @error ignore-end */
/* @end YUK-20819 */

/* @start YUK-20788 */
INSERT into YukonRoleProperty
VALUES (-10320, -103, 'Manage Custom Commands', 'VIEW', 'Controls access to the ability to manage custom commands in web commander');

INSERT INTO DBUpdates VALUES ('YUK-20788', '7.4.0', GETDATE());
/* @end YUK-20788 */

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

/* @start YUK-20919 */
/* @start-block */
BEGIN
    DECLARE @MaxDeviceGroupId NUMERIC = (SELECT MAX(DG.DeviceGroupId) FROM DeviceGroup DG WHERE DG.DeviceGroupId < 100)
    DECLARE @RootParentGroupId NUMERIC = (SELECT DG.DeviceGroupId FROM DeviceGroup DG WHERE SystemGroupEnum = 'SYSTEM_METERS')

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
    VALUES(@MaxDeviceGroupId + 1, 'Meter Programming', @RootParentGroupId, 'NOEDIT_NOMOD', 'METERS_METER_PROGRAMMING', GETDATE(), 'METER_PROGRAMMING')
END;
GO
/* @end-block */

INSERT INTO DBUpdates VALUES ('YUK-20919', '7.4.0', GETDATE());
/* @end YUK-20919 */

/* @start YUK-21004 */
UPDATE GlobalSetting 
SET Value = NULL
WHERE Name = 'HTTP_PROXY'
AND Value = 'none';

INSERT INTO DBUpdates VALUES ('YUK-21004', '7.4.0', GETDATE());
/* @end YUK-21004 */

/* @start YUK-21096 */
ALTER TABLE MSPInterface
DROP CONSTRAINT PK_MSPINTERFACE;
GO

ALTER TABLE MSPInterface
ALTER COLUMN Interface VARCHAR(20) NOT NULL;

ALTER TABLE MSPInterface
ADD CONSTRAINT PK_MSPINTERFACE PRIMARY KEY (VendorID, Interface, Version);

INSERT INTO DBUpdates VALUES ('YUK-21096', '7.4.0', GETDATE());
/* @end YUK-21096 */

/* @start YUK-21048 */
INSERT INTO Command VALUES (-215, 'putconfig template ''?''LoadGroup''''', 'Install ExpressCom Addressing', 'ExpresscomSerial');

INSERT INTO DeviceTypeCommand VALUES (-1273, -215, 'ExpresscomSerial', 31, 'Y', -1);

UPDATE Command SET Command = 'putconfig template ''?''LoadGroup''''' WHERE CommandId = -71;

INSERT INTO DBUpdates VALUES ('YUK-21048', '7.4.0', GETDATE());
/* @end YUK-21048 */

/* @start YUK-21132 */
UPDATE GlobalSetting
SET Value = 'HALF_EVEN'
WHERE Name = 'DEFAULT_ROUNDING_MODE'
AND Value = 'UNNECESSARY';

INSERT INTO DBUpdates VALUES('YUK-21132', '7.4.0', GETDATE());
/* @end YUK-21132 */

/* @start YUK-20859 */
UPDATE YukonPaobject SET Type = 'WRL-420cL' WHERE Type = 'RFN-420cLW';
UPDATE YukonPaobject SET Type = 'WRL-420cD' WHERE Type = 'RFN-420cDW';
INSERT INTO DBUpdates VALUES('YUK-20859', '7.4.0', GETDATE());
/* @end YUK-20859 */

/* @start YUK-21216 */
INSERT INTO UnitMeasure VALUES ( 57,'dBm', 0, 'Decibel-Milliwatts', '(none)');

UPDATE PointUnit
SET UomId = 57
WHERE UomId = 54
AND PointId IN
    (SELECT PointId FROM Point p
     JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId
     WHERE PointType = 'Analog' 
     AND PointOffset = 394
     AND Type in ('WRL-420cD', 'WRL-420cL'));

INSERT INTO DBUpdates VALUES('YUK-21216', '7.4.0', GETDATE());
/* @end YUK-21216 */

/* @start YUK-21149 */
DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAnalog WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointLimits WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset IN (12,13,15,16,17,50,51,52,53,54,55,57,58,59,60,61,62,64)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatusControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatus WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 1000
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

INSERT INTO DBUpdates VALUES ('YUK-21149', '7.4.0', GETDATE());
/* @end YUK-21149 */

/* @start YUK-21060 */
DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND (PointOffset = 226 OR PointOffset = 227 OR PointOffset = 228)
    AND YP.Type IN ('RFN-530FAX', 'RFN-530FRX'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND (PointOffset = 226 OR PointOffset = 227 OR PointOffset = 228)
    AND YP.Type IN ('RFN-530FAX', 'RFN-530FRX'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND (PointOffset = 226 OR PointOffset = 227 OR PointOffset = 228)
    AND YP.Type IN ('RFN-530FAX', 'RFN-530FRX'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND (PointOffset = 226 OR PointOffset = 227 OR PointOffset = 228)
    AND YP.Type IN ('RFN-530FAX', 'RFN-530FRX'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND (PointOffset = 226 OR PointOffset = 227 OR PointOffset = 228)
    AND YP.Type IN ('RFN-530FAX', 'RFN-530FRX'));

DELETE FROM POINTUNIT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 349
    AND YP.Type IN ('RFN-520FAX', 'RFN-520FRX', 'RFN-520FAXD', 'RFN-520FRXD'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 349
    AND YP.Type IN ('RFN-520FAX', 'RFN-520FRX', 'RFN-520FAXD', 'RFN-520FRXD'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 349
    AND YP.Type IN ('RFN-520FAX', 'RFN-520FRX', 'RFN-520FAXD', 'RFN-520FRXD'));

DELETE FROM POINTANALOG WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 349
    AND YP.Type IN ('RFN-520FAX', 'RFN-520FRX', 'RFN-520FAXD', 'RFN-520FRXD'));

DELETE FROM POINT WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Analog' AND PointOffset = 349
    AND YP.Type IN ('RFN-520FAX', 'RFN-520FRX', 'RFN-520FAXD', 'RFN-520FRXD'));

INSERT INTO DBUpdates VALUES ('YUK-21060', '7.4.0', GETDATE());
/* @end YUK-21060 */

/* @start YUK-21475 */
ALTER TABLE MeterProgramStatus
ALTER COLUMN Status VARCHAR(100) NOT NULL;

INSERT INTO DBUpdates VALUES ('YUK-21475', '7.4.0', GETDATE());
/* @end YUK-21475 */

/* @start YUK-21642 */
DROP INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN GatewayId NUMERIC NOT NULL;
GO

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
GatewayId ASC
)
GO

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMERIC NULL;
GO

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN DescendantCount NUMERIC NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTimeNew datetime NULL;
GO

UPDATE DynamicRfnDeviceData
SET LastTransferTimeNew = LastTransferTime;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN LastTransferTimeNew datetime NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;
GO

EXEC sp_rename 'DynamicRfnDeviceData.LastTransferTimeNew', 'LastTransferTime', 'COLUMN';
GO

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.4.0', GETDATE());
/* @end YUK-21642 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.4', '10-FEB-2020', 'Latest Update', 0, GETDATE());
