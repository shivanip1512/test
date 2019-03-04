/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-19587 if YUK-19531 */
/* errors are ignored for an edge case where TotalCount has been dropped already */
/* @error ignore-begin */
ALTER TABLE ScheduledDataImportHistory 
DROP COLUMN TotalCount;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19587', '7.2.1', SYSDATE);
/* @end YUK-19587 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '02-FEB-2019', 'Latest Update', 1, SYSDATE);*/
