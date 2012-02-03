/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10547 */ 
ALTER TABLE LMProgramDirect ADD NotifyAdjust NUMERIC;
GO
UPDATE LMProgramDirect SET NotifyAdjust = -1;
GO
ALTER TABLE LMProgramDirect ALTER COLUMN NotifyAdjust NUMERIC NOT NULL;
GO
/* End YUK-10547 */

/* Start YUK-10536 */
/* @error ignore-begin */
ALTER TABLE ZBGateway
ALTER COLUMN FirmwareVersion VARCHAR(255) NULL;
GO
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
    FormatId   NUMERIC      NOT NULL,
    FormatName VARCHAR(100) NOT NULL,
    Delimiter  VARCHAR(20)  NOT NULL,
    Header     VARCHAR(255) NULL,
    Footer     VARCHAR(255) NULL,
    CONSTRAINT PK_FormatId PRIMARY KEY (FormatId)
);

CREATE TABLE ArchiveValuesExportAttribute (
    AttributeId   NUMERIC     NOT NULL,
    FormatId      NUMERIC     NOT NULL,
    AttributeName VARCHAR(50) NOT NULL,
    DataSelection VARCHAR(12) NOT NULL,
    DaysPrevious  NUMERIC     NOT NULL,
    CONSTRAINT PK_AttributeId PRIMARY KEY (AttributeId)
);

CREATE INDEX Indx_ArchValExpAttr_FormatId ON ArchiveValuesExportAttribute (
    FormatId ASC
);

CREATE TABLE ArchiveValuesExportField (
    FieldId               NUMERIC     NOT NULL,
    FormatId              NUMERIC     NOT NULL,
    FieldType             VARCHAR(50) NULL,
    AttributeId           NUMERIC     NULL,
    AttributeField        VARCHAR(50) NULL,
    Pattern               VARCHAR(50) NULL,
    MaxLength             NUMERIC     NULL,
    PadChar               CHAR(1)     NULL,
    PadSide               VARCHAR(5)  NULL,
    RoundingMode          VARCHAR(20) NULL,
    MissingAttribute      VARCHAR(20) NULL,
    MissingAttributeValue VARCHAR(20) NULL,
    CONSTRAINT PK_FieldId PRIMARY KEY (FieldId)
);

CREATE INDEX Indx_ArchValExportFld_FormatId ON ArchiveValuesExportField (
    FormatId ASC
);

INSERT INTO YukonRoleProperty VALUES (-21313, -213, 'Archived Data Exporter', 'true', 'Controls access to Archived Data Exporter');
/* End YUK-10601 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
