/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12914 */
ALTER TABLE RfnBroadcastEvent
RENAME COLUMN EventSendTime TO EventSentTime;
/* End YUK-12914 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.1', '01-MAR-2014', 'Latest Update', 1, SYSDATE);*/