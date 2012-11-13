/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-11288 */
sp_rename 'CTIDatabase.DateApplied', 'BuildDate', 'COLUMN';

ALTER TABLE CTIDatabase
DROP COLUMN CTIEmployeeName;

ALTER TABLE CTIDatabase
ADD InstallDate DATETIME;

UPDATE CTIDatabase
SET InstallDate = '2000-01-01';

ALTER TABLE CTIDatabase
ALTER COLUMN InstallDate DATETIME NOT NULL;
/* End YUK-11288 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('5.5', '05-DEC-2012', 'Latest Update', 5, GETDATE());*/