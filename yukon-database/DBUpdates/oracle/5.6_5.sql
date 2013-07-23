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

/* Start YUK-12368 */
UPDATE CalcBase SET UpdateType = 'On First Change' WHERE UpdateType = 'ON_FIRST_CHANGE';
UPDATE CalcBase SET UpdateType = 'On All Change' WHERE UpdateType = 'ON_ALL_CHANGE';
UPDATE CalcBase SET UpdateType = 'On Timer' WHERE UpdateType = 'ON_TIMER';
UPDATE CalcBase SET UpdateType = 'On Timer And Change' WHERE UpdateType = 'ON_TIMER_CHANGE';
UPDATE CalcBase SET UpdateType = 'Constant' WHERE UpdateType = 'CONSTANT';
UPDATE CalcBase SET UpdateType = 'Historical' WHERE UpdateType = 'HISTORICAL';
/* End YUK-12368 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '25-JUL-2013', 'Latest Update', 5, SYSDATE);