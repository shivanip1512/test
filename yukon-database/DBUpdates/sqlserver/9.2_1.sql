/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-25311 */
DELETE FROM CTIDatabase
WHERE Version = 9.2
AND Build = 0;

INSERT INTO DBUpdates VALUES ('YUK-25311', '9.2.0', GETDATE());
/* @end YUK-25311 */

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.2', '18-OCT-2021', 'Latest Update', 1, GETDATE()); */