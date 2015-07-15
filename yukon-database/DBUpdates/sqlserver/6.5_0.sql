/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14460 */
ALTER TABLE PaoLocation
ADD LastChangedDate DATETIME;
GO

UPDATE PaoLocation
SET LastChangedDate = GETDATE();

ALTER TABLE PaoLocation
ALTER COLUMN LastChangedDate DATETIME NOT NULL;

ALTER TABLE PaoLocation
ADD LocationType VARCHAR(64);
GO

UPDATE PaoLocation
SET LocationType = 'MANUAL';

ALTER TABLE PaoLocation
ALTER COLUMN LocationType VARCHAR(64) NOT NULL;
/* End YUK-14460 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JUL-2015', 'Latest Update', 0, GETDATE());*/