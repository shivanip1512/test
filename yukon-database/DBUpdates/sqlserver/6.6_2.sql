/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-16106 */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2;
/* End YUK-16106 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '4-JAN-2017', 'Latest Update', 2, GETDATE());*/
