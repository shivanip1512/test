/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18001 */
DELETE FROM DeviceWindow
WHERE Type = 'SCAN'
AND WinOpen = 0
AND WinClose = 0 
AND AlternateOpen = 0
AND AlternateClose = 0;

INSERT INTO DBUpdates VALUES ('YUK-18001', '7.0.2', SYSDATE);
/* @end YUK-18001 */
    
/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.0', '23-APR-2018', 'Latest Update', 2, SYSDATE);
