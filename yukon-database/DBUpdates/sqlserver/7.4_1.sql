/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-21621 */
INSERT INTO StateGroup VALUES(-29, 'Meter Programming', 'Status');
INSERT INTO State VALUES(-29, 0, 'Success', 0, 6, 0);
INSERT INTO State VALUES(-29, 1, 'Failure', 1, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-21621', '7.4.1', GETDATE());
/* @end YUK-21621 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.4', '15-NOV-2019', 'Latest Update', 1, GETDATE());*/