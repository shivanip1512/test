/******************************************/
/**** SQL Server DBupdates             ****/
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
ALTER TABLE Address
    ALTER COLUMN LocationAddress1 VARCHAR(100) NOT NULL;

ALTER TABLE Address
    ALTER COLUMN LocationAddress2 VARCHAR(100) NOT NULL;
/* End YUK-13230 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAY-2014', 'Latest Update', 1, GETDATE());*/