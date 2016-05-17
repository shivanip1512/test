/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-15173 */
/* @error warn-once */
/* @start-block */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'PKC_RawPointHistory')
BEGIN
    DECLARE @NewLine CHAR(2) = CHAR(13) + CHAR(10);
    DECLARE @ErrorText VARCHAR(1024) = 'Indexes on RawPointHistory are being modified to improve system performance.' + @NewLine
        + 'Setup has detected that these indexes have not yet been updated on this system.' + @NewLine
        + 'This can potentially be a long-running task so it is not included in the normal DBToolsFrame update process,' + @NewLine
        + 'and some downtime should be scheduled in order to complete this update with minimal system impact.' + @NewLine
        + 'More information can be found in YUK-15173.' + @NewLine
        + 'The SQL for the index update can be found in the file:' + @NewLine
        + '~\YukonMisc\YukonDatabase\DatabaseUpdates\SqlServer\RPH_Index_Modification.sql';
    RAISERROR(@ErrorText, 16, 1);
END;
/* @end-block */
/* End YUK-15173 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.5', '11-MAY-2016', 'Latest Update', 3, GETDATE());