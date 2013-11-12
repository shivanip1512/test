/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12728 */
ALTER TABLE FileExportHistory
ADD ArchiveFileExists CHAR(1);
 
UPDATE FileExportHistory
SET ArchiveFileExists = 1;

ALTER TABLE FileExportHistory
MODIFY ArchiveFileExists NOT NULL;
/* End YUK-12728 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.0', '12-NOV-2013', 'Latest Update', 4, SYSDATE);