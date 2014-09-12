/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13686 */
UPDATE Point 
SET ArchiveType = 'On Update' 
WHERE PAObjectID IN (
    SELECT PAObjectID FROM YukonPAObject WHERE Type = 'WEATHER LOCATION');
/* End YUK-13686 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.2', '12-SEP-2014', 'Latest Update', 4, GETDATE());