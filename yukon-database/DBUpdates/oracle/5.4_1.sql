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
    CONSTRAINT PK_ArchiveValuesExportFormat PRIMARY KEY (FormatId)
);

CREATE TABLE ArchiveValuesExportAttribute (
    AttributeId   NUMBER       NOT NULL,
    FormatId      NUMBER       NOT NULL,
    AttributeName VARCHAR2(50) NOT NULL,
    DataSelection VARCHAR2(12) NOT NULL,
    DaysPrevious  NUMBER       NOT NULL,
    CONSTRAINT PK_ArchiveValuesExportAttrib PRIMARY KEY (AttributeId)
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
    CONSTRAINT PK_ArchiveValuesExportField PRIMARY KEY (FieldId)
);

CREATE INDEX Indx_ArchValExportFld_FormatId ON ArchiveValuesExportField(
    FormatId ASC
);

INSERT INTO YukonRoleProperty VALUES (-21313, -213, 'Archived Data Exporter', 'true', 'Controls access to Archived Data Exporter');
/* End YUK-10601 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/
