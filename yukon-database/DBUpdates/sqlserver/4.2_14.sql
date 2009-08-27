/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7750 */
ALTER TABLE CapControlSubstation ADD MapLocationId VARCHAR(64); 
GO 
UPDATE CapControlSubstation SET MapLocationId = '0'; 
GO 
ALTER TABLE CapControlSubstation ALTER COLUMN MapLocationId VARCHAR(64) NOT NULL;
GO
/* End YUK-7750 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '27-AUG-2009', 'Latest Update', 14);