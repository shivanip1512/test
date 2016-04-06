/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-15182 */
/* @error ignore-begin */
INSERT INTO State VALUES(-20, 14, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 15, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 16, 'N/A', 9, 6, 0);
/* @error ignore-end */
/* End YUK-15182 */

/* Start YUK-15180 */
DELETE FROM UserPage WHERE Module = 'commercialcurtailment';

UPDATE YukonGroupRole SET Value = '/dr/cc/user/overview' 
WHERE RolePropertyId = -10800 
AND Value = '/cc/user/overview.jsf';

UPDATE YukonGroupRole SET Value = '/dr/cc/home' 
WHERE RolePropertyId = -10800 
AND Value = '/cc/ciSetup.jsf';
/* End YUK-15180 */

/* Start YUK-15216 */
UPDATE DeviceConfigCategoryItem 
SET ItemName = 'timeOffset', 
    ItemValue = CASE 
                  WHEN ItemValue='true' THEN 'LOCAL' 
                  ELSE 'UTC' 
                END
WHERE ItemName = 'localTime';
/* End YUK-15216 */

/* Start YUK-15201 */
/* @error ignore-begin */
CREATE TABLE StoredProcedureLog (
  EntryId           NUMERIC         NOT NULL,
  ProcedureName     VARCHAR(50)     NOT NULL,
  LogDate           DATETIME        NOT NULL,
  LogString         VARCHAR(500)    NOT NULL,
  CONSTRAINT PK_StoredProcedureLog PRIMARY KEY (EntryId)
);
/* @error ignore-end */

IF OBJECT_ID ('sp_SmartIndexMaintenance') IS NOT NULL
    DROP PROCEDURE sp_SmartIndexMaintenance;
GO

CREATE PROCEDURE sp_SmartIndexMaintenance AS
BEGIN TRY

    SET NOCOUNT ON;

    DECLARE @start_time     DATETIME;

    DECLARE @objectid       int;
    DECLARE @indexid        int;
    DECLARE @partitioncount bigint;
    DECLARE @schemaname     nvarchar(130);
    DECLARE @objectname     nvarchar(130);
    DECLARE @indexname      nvarchar(130);
    DECLARE @partitionnum   bigint;
    DECLARE @partitions     bigint;
    DECLARE @frag           float;
    DECLARE @pagecount      int;
    DECLARE @command        nvarchar(4000);
    DECLARE @output         nvarchar(4000);

    DECLARE @page_count_minimum   smallint
    DECLARE @frag_min_reorg       float
    DECLARE @frag_min_rebuild     float

    SET @page_count_minimum       = 50
    SET @frag_min_reorg           = 10.0
    SET @frag_min_rebuild         = 30.0

    /* Conditionally select tables and indexes from the sys.dm_db_index_physical_stats function and convert object and index IDs to names. */
    /* Store records in temp table work_to_do */
    SELECT object_id AS ObjectId, 
        index_id AS IndexId, 
        partition_number AS PartNum, 
        avg_fragmentation_in_percent AS AvgFrag, 
        page_count AS PageCount
        INTO #work_to_do
    FROM sys.dm_db_index_physical_stats (DB_ID(), NULL, NULL , NULL, 'LIMITED')
        WHERE avg_fragmentation_in_percent > @frag_min_reorg
        AND index_id > 0
        AND page_count > @page_count_minimum;

    /* Declare the cursor for the list of partitions (from temp table work_to_do) to be processed. */
    DECLARE partitions CURSOR FOR SELECT * FROM #work_to_do;

    /* Open the cursor. */
    OPEN partitions;

    /* Loop through the partitions. */
    FETCH NEXT FROM partitions INTO @objectid, @indexid, @partitionnum, @frag, @pagecount;
    WHILE @@FETCH_STATUS = 0
    BEGIN

        SELECT @objectname = QUOTENAME(o.name), @schemaname = QUOTENAME(s.name)
        FROM sys.objects AS o JOIN sys.schemas as s ON s.schema_id = o.schema_id
            WHERE o.object_id = @objectid;

        SELECT @indexname = QUOTENAME(name)
        FROM sys.indexes
            WHERE  object_id = @objectid AND index_id = @indexid;

        SELECT @partitioncount = count (*)
        FROM sys.partitions
            WHERE object_id = @objectid AND index_id = @indexid;

        SET @command = N'ALTER INDEX ' + @indexname + N' ON ' + @schemaname + N'.' + @objectname;

        IF @frag > @frag_min_rebuild
            SET @command = @command + N' REBUILD';
        ELSE IF @frag > @frag_min_reorg
            SET @command = @command + N' REORGANIZE';

        IF @partitioncount > 1
            SET @command = @command + N' PARTITION=' + CAST(@partitionnum AS nvarchar(10));

        SET @output = @command + N' : Fragmentation: ' + CAST(@frag AS varchar(15)) + ' : Page Count: ' + CAST(@pagecount AS varchar(15));

        SET @start_time = CURRENT_TIMESTAMP;
        /* Execute the REBUILD or REORGANIZE command on the table index */
        EXEC (@command);

        /* update the table index statistics, only do when reorganizing */
        IF @frag > @frag_min_reorg AND @frag <= @frag_min_rebuild
        BEGIN
            SET @output = @output + N' : Update Statistics';
            SET @command = N'UPDATE STATISTICS ' +  @schemaname + N'.' + @objectname + ' ' +  @indexname + ' WITH FULLSCAN';
            /* Execute the UPDATE STATISTICS command on the table index */
            EXEC (@command);
        END;

        SET @output = @output + N' : Time ' + CAST(DATEDIFF(millisecond, @start_time, CURRENT_TIMESTAMP) AS varchar(20)) + ' millis';
        INSERT INTO StoredProcedureLog VALUES (
            (SELECT ISNULL(MAX(EntryId) + 1, 1) FROM StoredProcedureLog), 
            'sp_SmartIndexMaintenance', 
            GETDATE(), @output);

        FETCH NEXT FROM partitions INTO @objectid, @indexid, @partitionnum, @frag, @pagecount;
    END;

    /* Close and deallocate the cursor. */
    CLOSE partitions;
    DEALLOCATE partitions;

    /* Drop the temporary table. */
    DROP TABLE #work_to_do;
    SET NOCOUNT OFF;

END TRY
BEGIN CATCH
    DECLARE @ErrorNumber INT = ERROR_NUMBER();
    DECLARE @ErrorLine INT = ERROR_LINE();
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();

    SET @output = N'ERROR: ' + CAST(@ErrorNumber AS VARCHAR(10)) + ' Message:' +  @ErrorMessage +
            ' ErrorLine:' + CAST(@ErrorLine AS VARCHAR(10)) + ' ErrorState:' + CAST(@ErrorState AS VARCHAR(10));
    INSERT INTO StoredProcedureLog VALUES (
            (SELECT ISNULL(MAX(EntryId) + 1, 1) FROM StoredProcedureLog), 
            'sp_SmartIndexMaintenance', 
            GETDATE(), @output);
    RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH;

INSERT INTO StoredProcedureLog VALUES (
            (SELECT ISNULL(MAX(EntryId) + 1, 1) FROM StoredProcedureLog), 
            'sp_SmartIndexMaintenance', 
            GETDATE(), 'Smart Index Maintenance Complete');
GO
/* End YUK-15201 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-JAN-2016', 'Latest Update', 0, GETDATE());*/