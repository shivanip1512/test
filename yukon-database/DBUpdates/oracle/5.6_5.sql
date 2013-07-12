/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12218 */
ALTER TABLE ArchiveValuesExportFormat
ADD TimeZoneFormat VARCHAR2(20);

UPDATE ArchiveValuesExportFormat
SET TimeZoneFormat = 'LOCAL';

ALTER TABLE ArchiveValuesExportFormat
MODIFY TimeZoneFormat VARCHAR2(20) NOT NULL;

ALTER TABLE ArchiveValuesExportFormat
MODIFY Delimiter VARCHAR2(20) NULL;
/* End YUK-12218 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '25-JUL-2013', 'Latest Update', 5, SYSDATE);