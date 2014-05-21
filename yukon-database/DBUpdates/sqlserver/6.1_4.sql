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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.1', '21-MAY-2014', 'Latest Update', 4, GETDATE());