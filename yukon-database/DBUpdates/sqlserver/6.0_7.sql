/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12951 */
ALTER TABLE UserPage
ALTER COLUMN Module VARCHAR(64) NOT NULL;

ALTER TABLE UserPage
ALTER COLUMN PageName VARCHAR(255) NOT NULL;
/* End YUK-12951 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.0', '03-FEB-2014', 'Latest Update', 7, GETDATE());