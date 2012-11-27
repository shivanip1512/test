/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11288 */
ALTER TABLE CTIDatabase RENAME COLUMN DateApplied TO BuildDate;

ALTER TABLE CTIDatabase
DROP COLUMN CTIEmployeeName;

ALTER TABLE CTIDatabase
ADD InstallDate DATE DEFAULT SYSDATE;
/* End YUK-11288 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase (Version, BuildDate, Notes, Build)
VALUES ('5.6', '05-DEC-2012', 'Latest Update', 0);*/ 