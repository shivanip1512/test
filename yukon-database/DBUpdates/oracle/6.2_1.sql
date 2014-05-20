/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13277 */
ALTER TABLE InventoryToAcctThermostatSch
   DROP CONSTRAINT FK_InvToAcctThermSch_InvBase;

ALTER TABLE InventoryToAcctThermostatSch
   ADD CONSTRAINT FK_InvToAcctThermSch_InvBase FOREIGN KEY (InventoryId)
      REFERENCES InventoryBase (InventoryID)
      ON DELETE CASCADE;
/* End YUK-13277 */

/* Start YUK-13230 */
/* @error ignore-begin */
DROP INDEX Indx_Add_LocAdd;
DROP INDEX Indx_Add_LocAdd_FB;
/* @error ignore-end */

ALTER TABLE Address
    MODIFY LocationAddress1 VARCHAR2(100);

ALTER TABLE Address
    MODIFY LocationAddress2 VARCHAR2(100);

CREATE INDEX Indx_Add_LocAdd ON Address (
    LocationAddress1 ASC
);

CREATE INDEX Indx_Add_LocAdd_FB ON Address (
   UPPER(LocationAddress1) ASC
);
/* End YUK-13230 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAY-2014', 'Latest Update', 1, SYSDATE);*/