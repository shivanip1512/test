/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14537 */
ALTER TABLE InventoryConfigTask
ADD SendOutOfService CHAR(1);
GO

UPDATE InventoryConfigTask
SET SendOutOfService = 'N';

ALTER TABLE InventoryConfigTask
ALTER COLUMN SendOutOfService CHAR(1) NOT NULL;
GO
/* End YUK-14537 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.4', '28-AUG-2015', 'Latest Update', 5, GETDATE());