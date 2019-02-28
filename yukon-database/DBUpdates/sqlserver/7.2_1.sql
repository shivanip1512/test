/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-19587 if YUK-19531 */
ALTER TABLE ScheduledDataImportHistory 
DROP COLUMN TotalCount;

INSERT INTO DBUpdates VALUES ('YUK-19587', '7.2.1', GETDATE());
/* @end YUK-19587 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '02-FEB-2019', 'Latest Update', 1, GETDATE());*/
