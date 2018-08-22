/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18039 */
ALTER TABLE InfrastructureWarnings
ADD Timestamp DATE;

UPDATE InfrastructureWarnings
SET Timestamp = '1/1/1990';

ALTER TABLE InfrastructureWarnings
MODIFY (Timestamp DATE NOT NULL);

INSERT INTO DBUpdates VALUES ('YUK-18039', '7.2.0', SYSDATE);
/* @end YUK-18039 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '13-AUG-2018', 'Latest Update', 0, SYSDATE);*/
