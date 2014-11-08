/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13472 */
INSERT INTO YukonRoleProperty VALUES (-21315, -213, 'Demand Reset', 'true', 'Controls access to Demand Reset collection action.');
/* End YUK-13472 */

/* Start YUK-13709 */
INSERT INTO YukonRoleProperty VALUES (-90047, -900, 'Allow DR Enable/Disable', 'true', 'Controls access to enable or disable control areas,load programs and load groups. Requires Allow DR Control.');
INSERT INTO YukonRoleProperty VALUES (-90048, -900, 'Allow Change Gears', 'true', 'Controls access to change gears for scenarios, control areas, and load programs. Requires Allow DR Control.');
/* End YUK-13709 */

/* Start YUK-13354 */
UPDATE CommandRequestExec 
SET    CommandRequestExecType = 'METER_CONNECT_DISCONNECT_WIDGET'
WHERE  CommandRequestExecType = 'CONTROL_CONNECT_DISCONNECT_COMAMND';
/* End YUK-13354 */

/* Start YUK-13801 */
UPDATE POINT
SET POINTOFFSET = 3
WHERE POINTOFFSET = 2 
  AND PointType = 'Analog'
  AND PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE PAOClass = 'LOADMANAGEMENT');
 
UPDATE POINT
SET POINTOFFSET = 2
WHERE POINTOFFSET = 1 
  AND PointType = 'Analog'
  AND PAObjectID IN (SELECT PAObjectID FROM YukonPAObject WHERE PAOClass = 'LOADMANAGEMENT');
/* End YUK-13801 */

/* Start YUK-13806 */
DELETE FROM GlobalSetting WHERE Name = 'SMTP_HOST' AND Value = '127.0.0.1';
/* End YUK-13806 */

/* Start YUK-13758 */
INSERT INTO YukonListEntry VALUES (2026, 1005, 0, 'ecobee3', 1330);
INSERT INTO YukonListEntry VALUES (2027, 1005, 0, 'ecobee Smart', 1331);
/* End YUK-13758 */

/* Start YUK-13552 */
CREATE TABLE GatewayCertificateUpdate (
    UpdateId        NUMERIC         NOT NULL,
    CertificateId   VARCHAR(100)    NOT NULL,
    SendDate        DATETIME        NOT NULL,
    FileName        VARCHAR(100)    NOT NULL,
    CONSTRAINT PK_GatewayCertificateUpdate PRIMARY KEY (UpdateId)
);
GO

CREATE TABLE GatewayCertificateUpdateEntry (
    EntryId         NUMERIC         NOT NULL,
    UpdateId        NUMERIC         NOT NULL,
    GatewayId       NUMERIC         NOT NULL,
    UpdateStatus    VARCHAR(40)     NOT NULL,
    CONSTRAINT PK_GatewayCertificateUpdEntry PRIMARY KEY (EntryId)
);
GO

ALTER TABLE GatewayCertificateUpdateEntry 
    ADD CONSTRAINT FK_GatewayCUEnt_GatewayCertUpd FOREIGN KEY (UpdateId)
        REFERENCES GatewayCertificateUpdate (UpdateId)
            ON DELETE CASCADE;
GO

ALTER TABLE GatewayCertificateUpdateEntry
    ADD CONSTRAINT FK_GatewayCertUpdateEnt_Device FOREIGN KEY (GatewayId)
        REFERENCES Device (DeviceId)
            ON DELETE CASCADE;
GO
/* End YUK-13552 */

/* Start YUK-13804 */
ALTER TABLE Job ADD JobGroupId INT;
GO
UPDATE Job SET JobGroupId = JobId;
GO
ALTER TABLE Job ALTER COLUMN JobGroupId INT NOT NULL;
GO

sp_rename 'FileExportHistory.Initiator', 'JobName', 'COLUMN';
GO

UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Billing Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Archived Data Export Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Data Export Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Water Leak Report Schedule: ', '');
UPDATE FileExportHistory SET JobName = REPLACE(JobName, 'Meter Events Report Schedule: ', '');

CREATE TABLE temp_JobFileExport (
   JobID                INT                  NOT NULL,
   BeanName             VARCHAR(250)         NOT NULL,
   Value                VARCHAR(4000)        NOT NULL
);
GO
 
INSERT INTO temp_JobFileExport (JobID, BeanName, Value)
    SELECT MAX(J.JobId), BeanName, Value FROM Job J JOIN JobProperty JP ON J.JobId = JP.JobId
    WHERE Name = 'name'
        AND (BeanName = 'scheduledBillingFileExportJobDefinition'
         OR  BeanName = 'scheduledArchivedDataFileExportJobDefinition'
         OR  BeanName = 'scheduledWaterLeakFileExportJobDefinition'
         OR  BeanName = 'scheduledMeterEventsFileExportJobDefinition')
    GROUP BY BeanName, Value;

ALTER TABLE FileExportHistory ADD JobGroupId INT;
GO

UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledBillingFileExportJobDefinition')
WHERE FileExportType = 'BILLING';
  
UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledArchivedDataFileExportJobDefinition')
WHERE FileExportType = 'ARCHIVED_DATA_EXPORT';
  
UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledWaterLeakFileExportJobDefinition')
WHERE FileExportType = 'WATER_LEAK';
  
UPDATE FileExportHistory SET FileExportHistory.JobGroupId =
    (SELECT DISTINCT T.JobId FROM temp_JobFileExport T WHERE T.value = JobName
        AND BeanName = 'scheduledMeterEventsFileExportJobDefinition')
WHERE FileExportType = 'METER_EVENTS';

ALTER TABLE FileExportHistory ALTER COLUMN JobGroupId INT NOT NULL;
GO
DROP TABLE temp_JobFileExport;
/* End YUK-13804 */

/* Start YUK-13738 */
/* @start-block */
IF OBJECT_ID ('SmartIndexMaintenance') IS NOT NULL
    DROP PROCEDURE SmartIndexMaintenance
GO

/* @start-block */
/* @end-block */
IF OBJECT_ID ('sp_SmartIndexMaintenance') IS NOT NULL
    DROP PROCEDURE sp_SmartIndexMaintenance
GO
/* @end-block */

/* @start-block */
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
        EXEC xp_logevent 201000, @output;  /* 201000 is an arbitrary number, means nothing, write to SQLServer Log */

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
    EXEC xp_logevent 201000, @output;  /* write to SQLServer Log */
    RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH
EXEC xp_logevent 201000, N'Smart Index Maintenance Complete';  /* write to SQLServer Log */
GO
/* @end-block */

INSERT INTO Job (Jobid, BeanName, Disabled, JobGroupId) VALUES (-3, 'spSmartIndexMaintanenceJobDefinition', 'N', -3);
INSERT INTO JobScheduledRepeating VALUES (-3, '0 0 22 ? * 7');
/* END YUK-13738 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.3', '11-NOV-2014', 'Latest Update', 0, GETDATE());