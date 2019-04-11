/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-19773 */
ALTER TABLE LMGroupItronMapping
ADD ItronEventId NUMBER;

INSERT INTO DBUpdates VALUES ('YUK-19773', '7.2.2', SYSDATE);
/* @end YUK-19773 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '22-MAR-2019', 'Latest Update', 2, SYSDATE);*/
