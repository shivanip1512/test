/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13632 */
UPDATE YukonPaobject SET Type = 'RFN-1200' WHERE Type = 'RF-DA';
/* End YUK-13632 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.2', '28-AUG-2014', 'Latest Update', 3, GETDATE());