/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-16411 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'ecobee3 Lite', 1336);
/* End YUK-16411 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '20-MARCH-2017', 'Latest Update', 5, GETDATE());*/
