/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14537 */
ALTER TABLE InventoryConfigTask
ADD SendOutOfService CHAR(1);

UPDATE InventoryConfigTask
SET SendOutOfService = 'N';

ALTER TABLE InventoryConfigTask
MODIFY SendOutOfService CHAR(1) NOT NULL;
/* End YUK-14537 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.4', '28-AUG-2015', 'Latest Update', 5, SYSDATE);