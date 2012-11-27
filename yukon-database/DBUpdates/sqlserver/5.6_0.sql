/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-11288 */
sp_rename 'CTIDatabase.DateApplied', 'BuildDate', 'COLUMN';
GO

ALTER TABLE CTIDatabase
DROP COLUMN CTIEmployeeName;
GO

ALTER TABLE CTIDatabase
ADD InstallDate DATETIME DEFAULT GETDATE();
GO
/* End YUK-11288 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase (Version, BuildDate, Notes, Build) 
VALUES ('5.6', '05-DEC-2012', 'Latest Update', 0);*/