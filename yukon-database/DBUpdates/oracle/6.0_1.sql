/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12368 */
UPDATE CALCBASE SET UPDATETYPE = 'On First Change' WHERE UPDATETYPE = 'ON_FIRST_CHANGE';
UPDATE CALCBASE SET UPDATETYPE = 'On All Change' WHERE UPDATETYPE = 'ON_ALL_CHANGE';
UPDATE CALCBASE SET UPDATETYPE = 'On Timer' WHERE UPDATETYPE = 'ON_TIMER';
UPDATE CALCBASE SET UPDATETYPE = 'On Timer And Change' WHERE UPDATETYPE = 'ON_TIMER_CHANGE';
UPDATE CALCBASE SET UPDATETYPE = 'Constant' WHERE UPDATETYPE = 'CONSTANT';
UPDATE CALCBASE SET UPDATETYPE = 'Historical' WHERE UPDATETYPE = 'HISTORICAL';
/* End YUK-12368 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '15-AUG-2013', 'Latest Update', 1, SYSDATE);*/