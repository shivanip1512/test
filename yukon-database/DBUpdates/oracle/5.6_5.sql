/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12218 */
ALTER TABLE ArchiveValuesExportFormat
ADD TimeZoneFormat VARCHAR2(20);

UPDATE ArchiveValuesExportFormat
SET TimeZoneFormat = 'LOCALTZ';

ALTER TABLE ArchiveValuesExportFormat
MODIFY TimeZoneFormat VARCHAR2(20) NOT NULL;

ALTER TABLE ArchiveValuesExportFormat
MODIFY Delimiter VARCHAR2(20) NULL;
/* End YUK-12218 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('5.6', '30-JUN-2013', 'Latest Update', 5, SYSDATE);*/