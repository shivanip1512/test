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
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.TABLES 
    WHERE TABLE_NAME = N'StoredProcedureLog')
BEGIN
    CREATE TABLE StoredProcedureLog (
      EntryId           NUMERIC         NOT NULL,
      ProcedureName     VARCHAR(50)     NOT NULL,
      LogDate           DATETIME        NOT NULL,
      LogString         VARCHAR(500)    NOT NULL,
      CONSTRAINT PK_StoredProcedureLog PRIMARY KEY (EntryId)
    )
END;

IF OBJECT_ID ('sp_SmartIndexMaintenance') IS NOT NULL
    DROP PROCEDURE sp_SmartIndexMaintenance;
GO

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
            GETDATE(), 'Smart Index Maintenance Complete'); /* Log procedure completion */
/* @end-block */
GO
/* End YUK-15201 */

/* Start YUK-15280 */
CREATE TABLE UserSystemMetric  (
   UserId               numeric              not null,
   SystemHealthMetricId varchar(64)          not null,
   CONSTRAINT PK_UserSystemMetric PRIMARY KEY (UserId, SystemHealthMetricId)
);

ALTER TABLE UserSystemMetric
   ADD CONSTRAINT FK_UserSystemMetric_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
      ON DELETE CASCADE;
/* End YUK-15280 */

      
/* Start YUK-15374 */
create table RfnBroadcastEventSummary (
   RfnBroadcastEventId  numeric        not null,
   Success              numeric         not null,
   SuccessUnenrolled    numeric         not null,
   Failure              numeric         not null,
   Unknown              numeric         not null,
   constraint PK_RFNBROADCASTEVENTSUMMARY primary key (RfnBroadcastEventId)
)
go

alter table RfnBroadcastEventSummary
   add constraint FK_RFNBROAD_REFERENCE_RFNBROAD foreign key (RfnBroadcastEventId)
      references RfnBroadcastEvent (RfnBroadcastEventId)
         on delete cascade
go

/* End YUK-15374 */

/* Start YUK-15251 */
UPDATE UserPage
SET PagePath = '/capcontrol/substations/' + SUBSTRING(PagePath, 39, LEN(PagePath)-38)
WHERE PageName = 'substation';

UPDATE UserPage
SET PageName = 'substation.VIEW'
WHERE PageName = 'substation';
/* End YUK-15251 */

/* Start YUK-15268 */
IF NOT EXISTS (SELECT 1 FROM GlobalSetting WHERE Name = 'CIS_DETAIL_TYPE')
BEGIN
    INSERT INTO GlobalSetting (GlobalSettingId, Name, Value)
        SELECT DISTINCT (SELECT ISNULL(MAX(GlobalSettingId) + 1, 1) FROM GlobalSetting), 'CIS_DETAIL_TYPE', 'CAYENTA' 
        FROM YukonGroupRole 
        WHERE RolePropertyId = -20212 AND Value = 'CAYENTA'
END;

IF NOT EXISTS (SELECT 1 FROM GlobalSetting WHERE Name = 'CIS_DETAIL_TYPE')
BEGIN
    INSERT INTO GlobalSetting (GlobalSettingId, Name, Value)
        SELECT (SELECT ISNULL(MAX(GlobalSettingId) + 1, 1) FROM GlobalSetting), 'CIS_DETAIL_TYPE', 'MULTISPEAK' 
        FROM GlobalSetting 
        WHERE Name = 'MSP_PRIMARY_CB_VENDORID' AND Value > 1
END;

DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20212;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20212;

DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20211;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20211;
/* End YUK-15268 */

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

/* Start YUK-15217 */
UPDATE YukonImage 
SET ImageId = -1 
WHERE ImageId = 1
  AND ImageCategory = 'logos'
  AND ImageName = 'eaton_logo.png';

UPDATE YukonImage 
SET ImageId = -2 
WHERE ImageId = 2
  AND ImageCategory = 'backgrounds'
  AND ImageName = 'yukon_background.jpg';
  
UPDATE ThemeProperty 
SET Value = -2 
WHERE Value = 2 
  AND Property = 'LOGIN_BACKGROUND';

UPDATE ThemeProperty 
SET Value = -1 
WHERE Value = 1 
  AND Property = 'LOGO';
/* End YUK-15217 */

/* Start YUK-15352 */
UPDATE DynamicPointDispatch
SET Tags = CAST(dpd.Tags AS BIGINT) & CAST(0xFFFFFFEF AS BIGINT)
FROM DynamicPointDispatch dpd 
JOIN Point p ON p.PointId = dpd.PointId
JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
WHERE y.DisableFlag = 'N' 
  AND CAST(dpd.Tags AS BIGINT) & 0x10 != 0;


UPDATE DynamicPointDispatch
SET Tags = CAST(dpd.Tags AS BIGINT) & CAST(0xFFFFFFFE AS BIGINT)
FROM DynamicPointDispatch dpd 
JOIN Point p ON p.PointId = dpd.PointId
JOIN YukonPAObject y ON y.PAObjectID = p.PAObjectID
WHERE p.ServiceFlag = 'N' 
  AND CAST(dpd.Tags AS BIGINT) & 0x01 != 0;
/* End YUK-15352 */

/* Start YUK-15271 */
INSERT INTO PointAlarming(PointId, AlarmStates, ExcludeNotifyStates, NotifyOnAcknowledge, NotificationGroupId)
SELECT PointId, '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1 
FROM Point p
WHERE p.PAObjectID = 0
  AND p.PointType != 'System'
  AND p.PointId NOT IN (SELECT PointId FROM PointAlarming);
/* End YUK-15271 */

/* Start YUK-15438 */
create table BehaviorReport (
   BehaviorReportId     numeric              not null,
   DeviceId             numeric              not null,
   BehaviorType         varchar(60)          not null,
   BehaviorStatus       varchar(60)          not null,
   TimeStamp            datetime             not null,
   constraint PK_BEHAVIORREPORT primary key (BehaviorReportId)
)
go

create table BehaviorReportValue (
   BehaviorReportId     numeric              not null,
   Name                 varchar(60)          not null,
   Value                varchar(100)         not null,
   constraint PK_BEHAVIORREPORTVALUE primary key (BehaviorReportId, Name)
)
go

create table DeviceBehaviorMap (
   BehaviorId           numeric              not null,
   DeviceId             numeric              not null,
   constraint PK_DEVICEBEHAVIORMAP primary key (BehaviorId, DeviceId)
)
go

create table Behavior (
   BehaviorId           numeric              not null,
   BehaviorType         varchar(60)          not null,
   constraint PK_BEHAVIOR primary key (BehaviorId)
)
go

create table BehaviorValue (
   BehaviorId           numeric              not null,
   Name                 varchar(60)          not null,
   Value                varchar(100)         not null,
   constraint PK_BEHAVIORVALUE primary key (BehaviorId, Name)
)
go

alter table BehaviorReport
   add constraint FK_Device_BehaviorReport foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table BehaviorReportValue
   add constraint FK_BehaviorRptVal_BehaviorRpt foreign key (BehaviorReportId)
      references BehaviorReport (BehaviorReportId)
         on delete cascade
go

alter table DeviceBehaviorMap
   add constraint FK_Behavior_DeviceBehaviorMap foreign key (BehaviorId)
      references Behavior (BehaviorId)
         on delete cascade
go

alter table DeviceBehaviorMap
   add constraint FK_Device_DeviceBehaviorMap foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table BehaviorValue
   add constraint FK_BehaviorValue_Behavior foreign key (BehaviorId)
      references Behavior (BehaviorId)
         on delete cascade
go
/* End YUK-15438 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-JUN-2016', 'Latest Update', 0, GETDATE());*/