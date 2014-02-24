/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12914 */
/* @error ignore-begin */
ALTER TABLE RfnBroadcastEvent
RENAME COLUMN EventSendTime TO EventSentTime;
/* @error ignore-end */
/* End YUK-12914 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAR-2014', 'Latest Update', 0, SYSDATE);*/