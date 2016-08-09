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

        SET @output = @output + N' : Time ' + CAST(DATEDIFF(millisecond, @start_time, CURRENT_TIMESTAMP) AS VARCHAR(20)) + ' millis';
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
CREATE TABLE BehaviorReport (
   BehaviorReportId     NUMERIC              not null,
   DeviceId             NUMERIC              not null,
   BehaviorType         VARCHAR(60)          not null,
   BehaviorStatus       VARCHAR(60)          not null,
   TimeStamp            DATETIME             not null,
   CONSTRAINT PK_BEHAVIORREPORT PRIMARY KEY (BehaviorReportId)
);
GO

CREATE TABLE BehaviorReportValue (
   BehaviorReportId     NUMERIC              not null,
   Name                 VARCHAR(60)          not null,
   Value                VARCHAR(100)         not null,
   CONSTRAINT PK_BEHAVIORREPORTVALUE PRIMARY KEY (BehaviorReportId, Name)
);
GO

CREATE TABLE DeviceBehaviorMap (
   BehaviorId           NUMERIC              not null,
   DeviceId             NUMERIC              not null,
   CONSTRAINT PK_DEVICEBEHAVIORMAP PRIMARY KEY (BehaviorId, DeviceId)
);
GO

CREATE TABLE Behavior (
   BehaviorId           NUMERIC              not null,
   BehaviorType         VARCHAR(60)          not null,
   CONSTRAINT PK_BEHAVIOR PRIMARY KEY (BehaviorId)
);
GO

CREATE TABLE BehaviorValue (
   BehaviorId           NUMERIC              not null,
   Name                 VARCHAR(60)          not null,
   Value                VARCHAR(100)         not null,
   CONSTRAINT PK_BEHAVIORVALUE PRIMARY KEY (BehaviorId, Name)
);
GO

ALTER TABLE BehaviorReport
   ADD CONSTRAINT FK_Device_BehaviorReport FOREIGN KEY (DeviceId)
      REFERENCES DEVICE (DEVICEID)
         ON DELETE CASCADE;
GO

ALTER TABLE BehaviorReportValue
   ADD CONSTRAINT FK_BehaviorRptVal_BehaviorRpt FOREIGN KEY (BehaviorReportId)
      REFERENCES BehaviorReport (BehaviorReportId)
         ON DELETE CASCADE;
GO

ALTER TABLE DeviceBehaviorMap
   ADD CONSTRAINT FK_Behavior_DeviceBehaviorMap FOREIGN KEY (BehaviorId)
      REFERENCES Behavior (BehaviorId)
         ON DELETE CASCADE;
GO

ALTER TABLE DeviceBehaviorMap
   ADD CONSTRAINT FK_Device_DeviceBehaviorMap FOREIGN KEY (DeviceId)
      REFERENCES DEVICE (DEVICEID)
         ON DELETE CASCADE;
GO

ALTER TABLE BehaviorValue
   ADD CONSTRAINT FK_BehaviorValue_Behavior FOREIGN KEY (BehaviorId)
      REFERENCES Behavior (BehaviorId)
         ON DELETE CASCADE;
GO
/* End YUK-15438 */

/* Start YUK-13440 */
IF (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'temp_DeviceConfigCategoryItem_Translate') IS NULL
BEGIN
    CREATE TABLE temp_DeviceConfigCategoryItem_Translate
    (
  	    [DeviceConfigCategoryItemId] [numeric](18, 0) NOT NULL,
	    [DeviceConfigCategoryId] [numeric](18, 0) NULL,
	    [ItemName] [varchar](60) NOT NULL,
	    [ItemValue] [varchar](60) NOT NULL,
    )

    INSERT INTO temp_DeviceConfigCategoryItem_Translate
           ([DeviceConfigCategoryItemId]
           ,[DeviceConfigCategoryId]
           ,[ItemName]
           ,[ItemValue])
           SELECT [DeviceConfigCategoryItemId]
              ,[DeviceConfigCategoryId]
              ,[ItemName]
              ,[ItemValue]
          FROM [dbo].[DeviceConfigCategoryItem]

    UPDATE DeviceConfigCategoryItem SET ItemValue = ItemValue + 1
    WHERE ItemName like 'channel%PhysicalChannel' 

    UPDATE DeviceConfigCategoryItem SET ItemValue = CONVERT(DECIMAL(4,2), cast(ItemValue as decimal(4,2)) / 4)
    WHERE ItemName like 'relay%Timer' 

    UPDATE DeviceConfigCategoryItem SET ItemValue = ItemValue * 15
    WHERE ItemName = 'tableReadInterval' 

    UPDATE DeviceConfigCategoryItem SET ItemValue = 
        CASE
            WHEN ItemName like 'displayItem%' AND ItemValue='0'  THEN 'SLOT_DISABLED' 
            WHEN ItemName like 'displayItem%' AND ItemValue='1'  THEN 'NO_SEGMENTS'
            WHEN ItemName like 'displayItem%' AND ItemValue='2'  THEN 'ALL_SEGMENTS'
            /* 3 is unused */
            WHEN ItemName like 'displayItem%' AND ItemValue='4'  THEN 'CURRENT_LOCAL_TIME'
            WHEN ItemName like 'displayItem%' AND ItemValue='5'  THEN 'CURRENT_LOCAL_DATE'
            WHEN ItemName like 'displayItem%' AND ItemValue='6'  THEN 'TOTAL_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='7'  THEN 'NET_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='8'  THEN 'DELIVERED_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='9'  THEN 'RECEIVED_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='10' THEN 'LAST_INTERVAL_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='11' THEN 'PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='12' THEN 'PEAK_KW_DATE'
            WHEN ItemName like 'displayItem%' AND ItemValue='13' THEN 'PEAK_KW_TIME'
            WHEN ItemName like 'displayItem%' AND ItemValue='14' THEN 'LAST_INTERVAL_VOLTAGE'
            WHEN ItemName like 'displayItem%' AND ItemValue='15' THEN 'PEAK_VOLTAGE'
            WHEN ItemName like 'displayItem%' AND ItemValue='16' THEN 'PEAK_VOLTAGE_DATE'
            WHEN ItemName like 'displayItem%' AND ItemValue='17' THEN 'PEAK_VOLTAGE_TIME'
            WHEN ItemName like 'displayItem%' AND ItemValue='18' THEN 'MINIMUM_VOLTAGE'
            WHEN ItemName like 'displayItem%' AND ItemValue='19' THEN 'MINIMUM_VOLTAGE_DATE'
            WHEN ItemName like 'displayItem%' AND ItemValue='20' THEN 'MINIMUM_VOLTAGE_TIME'
            WHEN ItemName like 'displayItem%' AND ItemValue='21' THEN 'TOU_RATE_A_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='22' THEN 'TOU_RATE_A_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='23' THEN 'TOU_RATE_A_DATE_OF_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='24' THEN 'TOU_RATE_A_TIME_OF_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='25' THEN 'TOU_RATE_B_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='26' THEN 'TOU_RATE_B_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='27' THEN 'TOU_RATE_B_DATE_OF_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='28' THEN 'TOU_RATE_B_TIME_OF_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='29' THEN 'TOU_RATE_C_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='30' THEN 'TOU_RATE_C_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='31' THEN 'TOU_RATE_C_DATE_OF_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='32' THEN 'TOU_RATE_C_TIME_OF_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='33' THEN 'TOU_RATE_D_KWH'
            WHEN ItemName like 'displayItem%' AND ItemValue='34' THEN 'TOU_RATE_D_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='35' THEN 'TOU_RATE_D_DATE_OF_PEAK_KW'
            WHEN ItemName like 'displayItem%' AND ItemValue='36' THEN 'TOU_RATE_D_TIME_OF_PEAK_KW'

            WHEN ItemName like 'channel%PeakKWResolution' AND ItemValue='1.0' THEN 'ZERO' 
            WHEN ItemName like 'channel%PeakKWResolution' AND ItemValue='10.0' THEN 'ONE' 

            WHEN ItemName like 'channel%ProfileResolution' AND ItemValue='.1' THEN 'MINUS_ONE' 
            WHEN ItemName like 'channel%ProfileResolution' AND ItemValue='1.0' THEN 'ZERO' 
            WHEN ItemName like 'channel%ProfileResolution' AND ItemValue='10.0' THEN 'ONE' 

            WHEN ItemName like 'channel%LastIntervalDemandResolution' AND ItemValue='.01' THEN 'MINUS_TWO' 
            WHEN ItemName like 'channel%LastIntervalDemandResolution' AND ItemValue='.1' THEN 'MINUS_ONE' 
            WHEN ItemName like 'channel%LastIntervalDemandResolution' AND ItemValue='1.0' THEN 'ZERO' 
            WHEN ItemName like 'channel%LastIntervalDemandResolution' AND ItemValue='10.0' THEN 'ONE' 

            WHEN ItemName = 'electronicMeter' AND ItemValue='0' THEN 'NONE'
            WHEN ItemName = 'electronicMeter' AND ItemValue='1' THEN 'S4'
            WHEN ItemName = 'electronicMeter' AND ItemValue='2' THEN 'ALPHA_A3'
            WHEN ItemName = 'electronicMeter' AND ItemValue='3' THEN 'ALPHA_P_PLUS'
            WHEN ItemName = 'electronicMeter' AND ItemValue='4' THEN 'GEKV'
            WHEN ItemName = 'electronicMeter' AND ItemValue='5' THEN 'GEKV2'
            WHEN ItemName = 'electronicMeter' AND ItemValue='6' THEN 'DNP'
            WHEN ItemName = 'electronicMeter' AND ItemValue='7' THEN 'SENTINEL'
            WHEN ItemName = 'electronicMeter' AND ItemValue='8' THEN 'GEKV2C'

            WHEN ItemName like 'channel%Type' AND ItemValue='0' THEN 'CHANNEL_NOT_USED'
            WHEN ItemName like 'channel%Type' AND ItemValue='1' THEN 'ELECTRONIC_METER'
            WHEN ItemName like 'channel%Type' AND ItemValue='2' THEN 'TWO_WIRE_KYZ_FORM_A'
            WHEN ItemName like 'channel%Type' AND ItemValue='3' THEN 'THREE_WIRE_KYZ_FORM_C'

            WHEN ItemName = 'sunday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'
            WHEN ItemName = 'monday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'
            WHEN ItemName = 'tuesday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'
            WHEN ItemName = 'wednesday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'
            WHEN ItemName = 'thursday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'
            WHEN ItemName = 'friday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'
            WHEN ItemName = 'saturday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'
            WHEN ItemName = 'holiday' AND ItemValue = 'Schedule 1' THEN 'SCHEDULE_1'

            WHEN ItemName = 'sunday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'
            WHEN ItemName = 'monday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'
            WHEN ItemName = 'tuesday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'
            WHEN ItemName = 'wednesday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'
            WHEN ItemName = 'thursday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'
            WHEN ItemName = 'friday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'
            WHEN ItemName = 'saturday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'
            WHEN ItemName = 'holiday' AND ItemValue = 'Schedule 2' THEN 'SCHEDULE_2'

            WHEN ItemName = 'sunday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'
            WHEN ItemName = 'monday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'
            WHEN ItemName = 'tuesday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'
            WHEN ItemName = 'wednesday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'
            WHEN ItemName = 'thursday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'
            WHEN ItemName = 'friday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'
            WHEN ItemName = 'saturday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'
            WHEN ItemName = 'holiday' AND ItemValue = 'Schedule 3' THEN 'SCHEDULE_3'

            WHEN ItemName = 'sunday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'
            WHEN ItemName = 'monday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'
            WHEN ItemName = 'tuesday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'
            WHEN ItemName = 'wednesday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'
            WHEN ItemName = 'thursday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'
            WHEN ItemName = 'friday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'
            WHEN ItemName = 'saturday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'
            WHEN ItemName = 'holiday' AND ItemValue = 'Schedule 4' THEN 'SCHEDULE_4'

            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-2' THEN 'NORONHA'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-3' THEN 'SAO_PAULO'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-4' THEN 'MANAUS'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-5' THEN 'NEW_YORK'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-6' THEN 'CHICAGO'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-7' THEN 'DENVER'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-8' THEN 'LOS_ANGELES'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-9' THEN 'ANCHORAGE'
            WHEN ItemName = 'timeZoneOffset' AND ItemValue = '-10' THEN 'HONOLULU'
            ELSE ItemValue
        END
END;

/* End YUK-13440 */

/* Start YUK-15428 */
DELETE FROM YukonServices WHERE ServiceId IN (6, -6);
/* End YUK-15428 */

/* Start YUK-15548 */
ALTER TABLE FDRTranslation 
ALTER COLUMN Destination VARCHAR(256) NOT NULL;

ALTER TABLE FDRTranslation 
DROP CONSTRAINT PK_FDRTrans;
GO

ALTER TABLE FDRTranslation 
ALTER COLUMN Translation VARCHAR(500) NOT NULL;
GO

ALTER TABLE FDRTranslation 
ADD CONSTRAINT PK_FDRTrans PRIMARY KEY (PointId, DirectionType, InterfaceType, Translation);
GO
/* End YUK-15548 */

/* Start YUK-15611 */
INSERT INTO StateGroup VALUES (-21, 'CBC Door Status', 'Status');
INSERT INTO StateGroup VALUES (-22, 'CBC-8 Hardware Type', 'Status');
INSERT INTO StateGroup VALUES (-23, 'Var Voltage Input', 'Status');
INSERT INTO StateGroup VALUES (-24, 'SCADA TripClose', 'Status');
INSERT INTO StateGroup VALUES (-25, 'YesNo', 'Status');
INSERT INTO StateGroup VALUES (-26, 'SCADA Override Type', 'Status');
GO

INSERT INTO State VALUES (-21, 0, 'Closed', 0, 6, 0);
INSERT INTO State VALUES (-21, 1, 'Open', 1, 6, 0);

INSERT INTO State VALUES (-22, 0, 'N/A', 0, 6, 0);
INSERT INTO State VALUES (-22, 1, 'Old Board/Old Box', 1, 6, 0);
INSERT INTO State VALUES (-22, 2, 'New Board/Old Box', 2, 6, 0);
INSERT INTO State VALUES (-22, 3, 'New Board/New Box', 3, 6, 0);

INSERT INTO State VALUES (-23, 0, 'CBC Line', 0, 6, 0);
INSERT INTO State VALUES (-23, 1, 'Phase A', 1, 6, 0);
INSERT INTO State VALUES (-23, 2, 'Phase B', 2, 6, 0);
INSERT INTO State VALUES (-23, 3, 'Phase C', 3, 6, 0);

INSERT INTO State VALUES (-24, 0, 'Trip', 0, 6, 0);
INSERT INTO State VALUES (-24, 1, 'Close', 1, 6, 0);

INSERT INTO State VALUES (-25, 0, 'No', 0, 6, 0);
INSERT INTO State VALUES (-25, 1, 'Yes', 1, 6, 0);

INSERT INTO State VALUES (-26, 0, 'Time of Day', 0, 6, 0);
INSERT INTO State VALUES (-26, 1, 'Countdown Timer', 1, 6, 0);
GO
/* End YUK-15611 */

/* Start YUK-15502 */
/* @error ignore-begin */
INSERT INTO DeviceTypeCommand VALUES (-1157, -193, 'RFN-510fL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1158, -194, 'RFN-510fL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1159, -195, 'RFN-510fL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1160, -196, 'RFN-510fL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1161, -197, 'RFN-510fL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1162, -198, 'RFN-510fL', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1163, -193, 'RFN-520fAX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1164, -194, 'RFN-520fAX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1165, -195, 'RFN-520fAX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1166, -196, 'RFN-520fAX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1167, -197, 'RFN-520fAX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1168, -198, 'RFN-520fAX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1169, -193, 'RFN-520fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1170, -194, 'RFN-520fRX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1171, -195, 'RFN-520fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1172, -196, 'RFN-520fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1173, -197, 'RFN-520fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1174, -198, 'RFN-520fRX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1175, -193, 'RFN-520fAXD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1176, -194, 'RFN-520fAXD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1177, -195, 'RFN-520fAXD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1178, -196, 'RFN-520fAXD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1179, -197, 'RFN-520fAXD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1180, -198, 'RFN-520fAXD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1181, -193, 'RFN-520fRXD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1182, -194, 'RFN-520fRXD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1183, -195, 'RFN-520fRXD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1184, -196, 'RFN-520fRXD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1185, -197, 'RFN-520fRXD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1186, -198, 'RFN-520fRXD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1187, -193, 'RFN-530fAX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1188, -194, 'RFN-530fAX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1189, -195, 'RFN-530fAX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1190, -196, 'RFN-530fAX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1191, -197, 'RFN-530fAX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1192, -198, 'RFN-530fAX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1193, -193, 'RFN-530fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1194, -194, 'RFN-530fRX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1195, -195, 'RFN-530fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1196, -196, 'RFN-530fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1197, -197, 'RFN-530fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1198, -198, 'RFN-530fRX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1199, -193, 'RFN-530S4x', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1200, -194, 'RFN-530S4x', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1201, -195, 'RFN-530S4x', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1202, -196, 'RFN-530S4x', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1203, -197, 'RFN-530S4x', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1204, -198, 'RFN-530S4x', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1205, -193, 'RFN-530S4eAD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1206, -194, 'RFN-530S4eAD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1207, -195, 'RFN-530S4eAD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1208, -196, 'RFN-530S4eAD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1209, -197, 'RFN-530S4eAD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1210, -198, 'RFN-530S4eAD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1211, -193, 'RFN-530S4eAT', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1212, -194, 'RFN-530S4eAT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1213, -195, 'RFN-530S4eAT', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1214, -196, 'RFN-530S4eAT', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1215, -197, 'RFN-530S4eAT', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1216, -198, 'RFN-530S4eAT', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1217, -193, 'RFN-530S4eRD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1218, -194, 'RFN-530S4eRD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1219, -195, 'RFN-530S4eRD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1220, -196, 'RFN-530S4eRD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1221, -197, 'RFN-530S4eRD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1222, -198, 'RFN-530S4eRD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1223, -193, 'RFN-530S4eRT', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1224, -194, 'RFN-530S4eRT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1225, -195, 'RFN-530S4eRT', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1226, -196, 'RFN-530S4eRT', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1227, -197, 'RFN-530S4eRT', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1228, -198, 'RFN-530S4eRT', 6, 'Y', -1);
/* @error ignore-end */
/* End YUK-15502 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-JUN-2016', 'Latest Update', 0, GETDATE());*/
