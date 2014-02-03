/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12951 */
ALTER TABLE UserPage
MODIFY Module VARCHAR2(64);

ALTER TABLE UserPage
MODIFY PageName VARCHAR2(255);
/* End YUK-12951 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.0', '03-FEB-2014', 'Latest Update', 7, SYSDATE);