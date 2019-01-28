/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-19044 if YUK-18280 */
/* error ignore is added here because this may have been fixed on the customer database manually. */

/* @error ignore-begin */
ALTER TABLE DeviceDataMonitorProcessor
MODIFY ( StateGroupId NUMBER NULL );
/* @error ignore-end */

INSERT INTO DBUpdates VALUES('YUK-19044', '7.1.3', SYSDATE);
/* @end YUK-19044 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.1', '29-JAN-2019', 'Latest Update', 3, SYSDATE);