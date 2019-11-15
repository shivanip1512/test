/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-20819 */
ALTER TABLE DeviceMacAddress
ADD SecondaryMacAddress VARCHAR2(255);

INSERT INTO DBUpdates VALUES ('YUK-20819', '7.3.2', SYSDATE);
/* @end YUK-20819 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.3', '15-NOV-2019', 'Latest Update', 2, SYSDATE);