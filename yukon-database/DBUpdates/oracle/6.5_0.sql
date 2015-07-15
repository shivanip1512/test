/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14460 */
ALTER TABLE PaoLocation
ADD LastChangedDate DATE;

UPDATE PaoLocation
SET LastChangedDate = SYSDATE;

ALTER TABLE PaoLocation
MODIFY LastChangedDate NOT NULL;

ALTER TABLE PaoLocation
ADD LocationType VARCHAR2(64);

UPDATE PaoLocation
SET LocationType = 'MANUAL';

ALTER TABLE PaoLocation
MODIFY LocationType NOT NULL;
/* End YUK-14460 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JUL-2015', 'Latest Update', 0, SYSDATE);*/