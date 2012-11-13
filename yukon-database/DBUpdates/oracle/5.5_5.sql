/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11288 */
ALTER TABLE CTIDatabase RENAME COLUMN DateApplied TO BuildDate;

ALTER TABLE CTIDatabase
DROP COLUMN CTIEmployeeName;

ALTER TABLE CTIDatabase
ADD InstallDate DATE;

UPDATE CTIDatabase
SET InstallDate = '01-JAN-2000';

ALTER TABLE CTIDatabase
MODIFY InstallDate NOT NULL;
/* End YUK-11288 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('5.5', '05-DEC-2012', 'Latest Update', 5, GETDATE());*/ 