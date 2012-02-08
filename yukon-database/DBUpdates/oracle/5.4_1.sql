/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10547 */ 
ALTER TABLE LMProgramDirect ADD NotifyAdjust NUMBER;
UPDATE LMProgramDirect SET NotifyAdjust = -1;
ALTER TABLE LMProgramDirect MODIFY NotifyAdjust NUMBER NOT NULL;
/* End YUK-10547 */

/* Start YUK-10536 */
/* @error ignore-begin */
ALTER TABLE ZBGateway
MODIFY FirmwareVersion VARCHAR2(255) NULL;
/* @error ignore-end */
/* End YUK-10536 */

/* Start YUK-10565 */
INSERT INTO YukonRoleProperty VALUES (-20219, -202, 'Meter Events', 'false', 'Controls access to Meter Events.');
/* End YUK-10565 */

/* Start YUK-10462 */
INSERT INTO DeviceGroup VALUES (30, 'Device Configs', 15, 'NOEDIT_NOMOD', 'DEVICECONFIG');
/* End YUK-10462 */

/* Start YUK-10600 */
UPDATE YukonRoleProperty 
SET KeyName = 'Opt Out Force All Devices', Description = 'Controls access to select individual devices or forces all device selection when opting out. When true, individual device selection is unavailable and all devices are forced to opt out.' 
WHERE RolePropertyId = -40201;
/* End YUK-10600 */

/* Start YUK-10601 */
CREATE TABLE ArchiveValuesExportFormat (
    FormatId   NUMBER        NOT NULL,
    FormatName VARCHAR2(100) NOT NULL,
    Delimiter  VARCHAR2(20)  NOT NULL,
    Header     VARCHAR2(255) NULL,
    Footer     VARCHAR2(255) NULL,
    CONSTRAINT PK_ArchiveValuesExpFormat PRIMARY KEY (FormatId)
);

CREATE TABLE ArchiveValuesExportAttribute (
    AttributeId   NUMBER       NOT NULL,
    FormatId      NUMBER       NOT NULL,
    AttributeName VARCHAR2(50) NOT NULL,
    DataSelection VARCHAR2(12) NOT NULL,
    DaysPrevious  NUMBER       NOT NULL,
    CONSTRAINT PK_ArchiveValuesExpAttribute PRIMARY KEY (AttributeId)
);

CREATE INDEX Indx_ArchValExpAttr_FormatId ON ArchiveValuesExportAttribute (
    FormatId ASC
);

CREATE TABLE ArchiveValuesExportField (
    FieldId               NUMBER       NOT NULL,
    FormatId              NUMBER       NOT NULL,
    FieldType             VARCHAR2(50) NULL,
    AttributeId           NUMBER       NULL,
    AttributeField        VARCHAR2(50) NULL,
    Pattern               VARCHAR2(50) NULL,
    MaxLength             NUMBER       NULL,
    PadChar               CHAR(1)      NULL,
    PadSide               VARCHAR2(5)  NULL,
    RoundingMode          VARCHAR2(20) NULL,
    MissingAttribute      VARCHAR2(20) NULL,
    MissingAttributeValue VARCHAR2(20) NULL,
    CONSTRAINT PK_ArchiveValuesExpField PRIMARY KEY (FieldId)
);

CREATE INDEX Indx_ArchValExportFld_FormatId ON ArchiveValuesExportField(
    FormatId ASC
);

INSERT INTO YukonRoleProperty VALUES (-21313, -213, 'Archived Data Exporter', 'true', 'Controls access to Archived Data Exporter');
/* End YUK-10601 */

/* Start YUK-10605 */
/* @error ignore-begin */
/* Note: OptOutTemporaryOverride.OptOutValue is NOT NULL. Nullability is not affected in the below update statement */
ALTER TABLE OptOutTemporaryOverride
MODIFY OptOutValue VARCHAR2(18);
UPDATE OptOutTemporaryOverride SET OptOutType = 'OPT_OUTS' WHERE OptOutType = 'ENABLED';
UPDATE OptOutTemporaryOverride SET OptOutValue = 'COUNT' WHERE OptOutType = 'COUNTS' AND OptOutValue = '1';
UPDATE OptOutTemporaryOverride SET OptOutValue = 'DONT_COUNT' WHERE OptOutType = 'COUNTS' AND OptOutValue = '0';
UPDATE OptOutTemporaryOverride SET OptOutValue = 'ENABLED' WHERE OptOutType = 'OPT_OUTS' AND OptOutValue = '1';
UPDATE OptOutTemporaryOverride SET OptOutValue = 'DISABLED_WITH_COMM' WHERE OptOutType = 'OPT_OUTS' AND OptOutValue = '0';
/* @error ignore-end */
/* End YUK-10605 */

/* Start YUK-10610 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -1020; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -1020; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -1020;
/* End YUK-10610 */

/* Start YUK-10613 */
DELETE FROM FdrInterfaceOption WHERE InterfaceId = 29 AND Ordering = 2;
UPDATE FdrInterfaceOption SET Ordering = 2 WHERE InterfaceId = 29 AND Ordering = 3;
/* End YUK-10613 */

/* Start YUK-10255 */
UPDATE UnitMeasure 
SET UOMName = 'ft^3', LongName = 'Cubic Feet' 
WHERE UOMID = 38;
INSERT INTO UnitMeasure VALUES (55, 'm^3', 0, 'Cubic Meters', '(none)');
/* End YUK-10255 */

/* Start YUK-10619 */ 
INSERT INTO YukonRoleProperty 
VALUES(-40300,-400,'Auto Thermostat Mode Enabled','false','Enables auto mode functionality for the account.enrollment by individual device per program.');

ALTER TABLE LMThermostatManualEvent
ADD PreviousCoolTemperature FLOAT;

UPDATE LMThermostatManualEvent
SET PreviousCoolTemperature = PreviousTemperature;

ALTER TABLE LMThermostatManualEvent
ADD PreviousHeatTemperature FLOAT;

UPDATE LMThermostatManualEvent
SET PreviousHeatTemperature = PreviousTemperature;

ALTER TABLE LMThermostatManualEvent
DROP COLUMN PreviousTemperature;

ALTER TABLE ThermostatEventHistory
ADD ManualCoolTemp FLOAT;

UPDATE ThermostatEventHistory
SET ManualCoolTemp = ManualTemp;

ALTER TABLE ThermostatEventHistory
ADD ManualHeatTemp FLOAT;

UPDATE ThermostatEventHistory
SET ManualHeatTemp = ManualTemp;

ALTER TABLE ThermostatEventHistory
DROP COLUMN ManualTemp;
/* End YUK-10619 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/
