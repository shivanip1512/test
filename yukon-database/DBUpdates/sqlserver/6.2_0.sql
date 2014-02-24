/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12914 */
sp_rename 'RfnBroadcastEvent.EventSendTime', 'EventSentTime', 'COLUMN';
/* End YUK-12914 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAR-2014', 'Latest Update', 0, GETDATE());*/