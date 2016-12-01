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
                  WHEN ItemValue='true' 
                  THEN 'LOCAL' 
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

    DECLARE @objectid       INT;
    DECLARE @indexid        INT;
    DECLARE @partitioncount BIGINT;
    DECLARE @schemaname     NVARCHAR(130);
    DECLARE @objectname     NVARCHAR(130);
    DECLARE @indexname      NVARCHAR(130);
    DECLARE @partitionnum   BIGINT;
    DECLARE @partitions     BIGINT;
    DECLARE @frag           FLOAT;
    DECLARE @pagecount      INT;
    DECLARE @command        NVARCHAR(4000);
    DECLARE @output         NVARCHAR(4000);

    DECLARE @page_count_minimum   SMALLINT
    DECLARE @frag_min_reorg       FLOAT
    DECLARE @frag_min_rebuild     FLOAT

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
        FROM sys.objects AS o JOIN sys.schemas AS s ON s.schema_id = o.schema_id
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
            SET @command = @command + N' PARTITION=' + CAST(@partitionnum AS NVARCHAR(10));

        SET @output = @command + N' : Fragmentation: ' + CAST(@frag AS varchar(15)) + ' : Page Count: ' + CAST(@pagecount AS VARCHAR(15));

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
   UserId               NUMERIC              NOT NULL,
   SystemHealthMetricId VARCHAR(64)          NOT NULL,
   CONSTRAINT PK_UserSystemMetric PRIMARY KEY (UserId, SystemHealthMetricId)
);
GO

ALTER TABLE UserSystemMetric
   ADD CONSTRAINT FK_UserSystemMetric_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID)
      ON DELETE CASCADE;
GO
/* End YUK-15280 */

      
/* Start YUK-15374 */
CREATE TABLE RfnBroadcastEventSummary (
   RfnBroadcastEventId  NUMERIC        NOT NULL,
   Success              NUMERIC        NOT NULL,
   SuccessUnenrolled    NUMERIC        NOT NULL,
   Failure              NUMERIC        NOT NULL,
   Unknown              NUMERIC        NOT NULL,
   CONSTRAINT PK_RFNBROADCASTEVENTSUMMARY PRIMARY KEY (RfnBroadcastEventId)
);
GO

ALTER TABLE RfnBroadcastEventSummary
   ADD CONSTRAINT FK_RFNBROAD_REFERENCE_RFNBROAD FOREIGN KEY (RfnBroadcastEventId)
      REFERENCES RfnBroadcastEvent (RfnBroadcastEventId)
         ON DELETE CASCADE;
GO
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
GO
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
   BehaviorReportId     NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   BehaviorType         VARCHAR(60)          NOT NULL,
   BehaviorStatus       VARCHAR(60)          NOT NULL,
   TimeStamp            DATETIME             NOT NULL,
   CONSTRAINT PK_BEHAVIORREPORT PRIMARY KEY (BehaviorReportId)
);
GO

CREATE TABLE BehaviorReportValue (
   BehaviorReportId     NUMERIC              NOT NULL,
   Name                 VARCHAR(60)          NOT NULL,
   Value                VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_BEHAVIORREPORTVALUE PRIMARY KEY (BehaviorReportId, Name)
);
GO

CREATE TABLE DeviceBehaviorMap (
   BehaviorId           NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   CONSTRAINT PK_DEVICEBEHAVIORMAP PRIMARY KEY (BehaviorId, DeviceId)
);
GO

CREATE TABLE Behavior (
   BehaviorId           NUMERIC              NOT NULL,
   BehaviorType         VARCHAR(60)          NOT NULL,
   CONSTRAINT PK_BEHAVIOR PRIMARY KEY (BehaviorId)
);
GO

CREATE TABLE BehaviorValue (
   BehaviorId           NUMERIC              NOT NULL,
   Name                 VARCHAR(60)          NOT NULL,
   Value                VARCHAR(100)         NOT NULL,
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
    WHERE ItemName LIKE 'channel%PhysicalChannel' 

    UPDATE DeviceConfigCategoryItem SET ItemValue = CONVERT(DECIMAL(4,2), CAST(ItemValue AS DECIMAL(4,2)) / 4)
    WHERE ItemName LIKE 'relay%Timer' 

    UPDATE DeviceConfigCategoryItem SET ItemValue = ItemValue * 15
    WHERE ItemName = 'tableReadInterval' 

    UPDATE DeviceConfigCategoryItem SET ItemValue = 
        CASE
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='0'  THEN 'SLOT_DISABLED' 
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='1'  THEN 'NO_SEGMENTS'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='2'  THEN 'ALL_SEGMENTS'
            /* 3 is unused */
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='4'  THEN 'CURRENT_LOCAL_TIME'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='5'  THEN 'CURRENT_LOCAL_DATE'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='6'  THEN 'TOTAL_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='7'  THEN 'NET_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='8'  THEN 'DELIVERED_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='9'  THEN 'RECEIVED_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='10' THEN 'LAST_INTERVAL_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='11' THEN 'PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='12' THEN 'PEAK_KW_DATE'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='13' THEN 'PEAK_KW_TIME'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='14' THEN 'LAST_INTERVAL_VOLTAGE'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='15' THEN 'PEAK_VOLTAGE'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='16' THEN 'PEAK_VOLTAGE_DATE'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='17' THEN 'PEAK_VOLTAGE_TIME'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='18' THEN 'MINIMUM_VOLTAGE'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='19' THEN 'MINIMUM_VOLTAGE_DATE'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='20' THEN 'MINIMUM_VOLTAGE_TIME'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='21' THEN 'TOU_RATE_A_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='22' THEN 'TOU_RATE_A_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='23' THEN 'TOU_RATE_A_DATE_OF_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='24' THEN 'TOU_RATE_A_TIME_OF_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='25' THEN 'TOU_RATE_B_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='26' THEN 'TOU_RATE_B_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='27' THEN 'TOU_RATE_B_DATE_OF_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='28' THEN 'TOU_RATE_B_TIME_OF_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='29' THEN 'TOU_RATE_C_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='30' THEN 'TOU_RATE_C_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='31' THEN 'TOU_RATE_C_DATE_OF_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='32' THEN 'TOU_RATE_C_TIME_OF_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='33' THEN 'TOU_RATE_D_KWH'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='34' THEN 'TOU_RATE_D_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='35' THEN 'TOU_RATE_D_DATE_OF_PEAK_KW'
            WHEN ItemName LIKE 'displayItem%' AND ItemValue='36' THEN 'TOU_RATE_D_TIME_OF_PEAK_KW'

            WHEN ItemName LIKE 'channel%PeakKWResolution' AND ItemValue='1.0' THEN 'ZERO' 
            WHEN ItemName LIKE 'channel%PeakKWResolution' AND ItemValue='10.0' THEN 'ONE' 

            WHEN ItemName LIKE 'channel%ProfileResolution' AND ItemValue='.1' THEN 'MINUS_ONE' 
            WHEN ItemName LIKE 'channel%ProfileResolution' AND ItemValue='1.0' THEN 'ZERO' 
            WHEN ItemName LIKE 'channel%ProfileResolution' AND ItemValue='10.0' THEN 'ONE' 

            WHEN ItemName LIKE 'channel%LastIntervalDemandResolution' AND ItemValue='.01' THEN 'MINUS_TWO' 
            WHEN ItemName LIKE 'channel%LastIntervalDemandResolution' AND ItemValue='.1' THEN 'MINUS_ONE' 
            WHEN ItemName LIKE 'channel%LastIntervalDemandResolution' AND ItemValue='1.0' THEN 'ZERO' 
            WHEN ItemName LIKE 'channel%LastIntervalDemandResolution' AND ItemValue='10.0' THEN 'ONE' 

            WHEN ItemName = 'electronicMeter' AND ItemValue='0' THEN 'NONE'
            WHEN ItemName = 'electronicMeter' AND ItemValue='1' THEN 'S4'
            WHEN ItemName = 'electronicMeter' AND ItemValue='2' THEN 'ALPHA_A3'
            WHEN ItemName = 'electronicMeter' AND ItemValue='3' THEN 'ALPHA_P_PLUS'
            WHEN ItemName = 'electronicMeter' AND ItemValue='4' THEN 'GEKV'
            WHEN ItemName = 'electronicMeter' AND ItemValue='5' THEN 'GEKV2'
            WHEN ItemName = 'electronicMeter' AND ItemValue='6' THEN 'DNP'
            WHEN ItemName = 'electronicMeter' AND ItemValue='7' THEN 'SENTINEL'
            WHEN ItemName = 'electronicMeter' AND ItemValue='8' THEN 'GEKV2C'

            WHEN ItemName LIKE 'channel%Type' AND ItemValue='0' THEN 'CHANNEL_NOT_USED'
            WHEN ItemName LIKE 'channel%Type' AND ItemValue='1' THEN 'ELECTRONIC_METER'
            WHEN ItemName LIKE 'channel%Type' AND ItemValue='2' THEN 'TWO_WIRE_KYZ_FORM_A'
            WHEN ItemName LIKE 'channel%Type' AND ItemValue='3' THEN 'THREE_WIRE_KYZ_FORM_C'

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
        DROP TABLE temp_DeviceConfigCategoryItem_Translate
END;
GO
/* End YUK-13440 */

/* Start YUK-15428 */
DELETE FROM YukonServices WHERE ServiceId IN (6, -6);
/* End YUK-15428 */

/* Start YUK-15548 */
ALTER TABLE FDRTranslation 
ALTER COLUMN Destination VARCHAR(256) NOT NULL;
GO

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
INSERT INTO DeviceTypeCommand VALUES (-1165, -195, 'RFN-520fAX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1166, -196, 'RFN-520fAX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1167, -197, 'RFN-520fAX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1168, -198, 'RFN-520fAX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1169, -193, 'RFN-520fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1171, -195, 'RFN-520fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1172, -196, 'RFN-520fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1173, -197, 'RFN-520fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1174, -198, 'RFN-520fRX', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1175, -193, 'RFN-520fAXD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1177, -195, 'RFN-520fAXD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1178, -196, 'RFN-520fAXD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1179, -197, 'RFN-520fAXD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1180, -198, 'RFN-520fAXD', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1181, -193, 'RFN-520fRXD', 1, 'Y', -1);
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

INSERT INTO DeviceTypeCommand VALUES (-1211, -193, 'RFN-530S4eAT', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1217, -193, 'RFN-530S4eRD', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1223, -193, 'RFN-530S4eRT', 1, 'Y', -1);
/* @error ignore-end */
/* End YUK-15502 */

/* Start YUK-15671 */
INSERT INTO Command VALUES (-212, 'getconfig model', 'Read Meter Config', 'All Two Way LCR');
INSERT INTO Command VALUES (-213, 'ping', 'Ping', 'All Two Way LCR');

INSERT INTO DeviceTypeCommand VALUES (-1229, -212, 'LCR-3102', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1230, -213, 'LCR-3102', 17, 'Y', -1);
/* End YUK-15671 */

/* Start YUK-15633 */
ALTER TABLE LMControlHistory DROP CONSTRAINT FK_LmCtrlHis_YPAO;

ALTER TABLE LMControlHistory
   ADD CONSTRAINT FK_LmCtrlHis_YPAO FOREIGN KEY (PAObjectID)
      REFERENCES YukonPAObject (PAObjectID)
         ON DELETE CASCADE;
GO
/* End YUK-15633 */

/* Start YUK-15729 */
ALTER TABLE DYNAMICPOINTDISPATCH DROP COLUMN STALECOUNT, LastAlarmLogID;
GO
/* End YUK-15729 */

/* Start YUK-15720*/
INSERT INTO YukonRoleProperty VALUES (-21316, -213, 'RF Data Streaming', 'false', 'Controls access to RF data streaming configuration actions.');
/* End YUK-15720*/

/* Start YUK-15712 */
/* @error ignore-begin */
INSERT INTO Command VALUES (-214, 'putconfig install all', 'Send configuration', 'ALL RFNs');
/* @error ignore-end */
/* End YUK-15712 */

/* Start YUK-15711 */
/* @error ignore-begin */
INSERT INTO DeviceTypeCommand VALUES (-1234, -214, 'RFN-410fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1232, -214, 'RFN-410fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1238, -214, 'RFN-420fL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1241, -214, 'RFN-420fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1237, -214, 'RFN-420fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1240, -214, 'RFN-420fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1239, -214, 'RFN-420fRD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-1231, -214, 'RFN-410cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1236, -214, 'RFN-420cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1235, -214, 'RFN-420cD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1242, -214, 'RFN-430A3D', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1245, -214, 'RFN-430A3T', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1243, -214, 'RFN-430A3K', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1244, -214, 'RFN-430A3R', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1246, -214, 'RFN-430KV', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1247, -214, 'RFN-430SL0', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1248, -214, 'RFN-430SL1', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1249, -214, 'RFN-430SL2', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1250, -214, 'RFN-430SL3', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1251, -214, 'RFN-430SL4', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1255, -214, 'RFN-510fL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1256, -214, 'RFN-520fAX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1257, -214, 'RFN-520fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1258, -214, 'RFN-520fAXD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1259, -214, 'RFN-520fRXD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1260, -214, 'RFN-530fAX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1261, -214, 'RFN-530fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1262, -214, 'RFN-530S4x', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1263, -214, 'RFN-530S4eAD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1264, -214, 'RFN-530S4eAT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1265, -214, 'RFN-530S4eRD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1266, -214, 'RFN-530S4eRT', 2, 'Y', -1);

DELETE FROM DeviceTypeCommand WHERE DeviceCommandID IN (-1045, -1051, -1057, -1063, -1075, -1081, -1087, -1088, -1089,
    -1090, -1092, -1093, -1094, -1095, -1097, -1098, -1099, -1100, -1102, -1103, -1104, -1105, -1112, -1113, -1114,
    -1115, -1117, -1118, -1119, -1120, -1122, -1123, -1124, -1125, -1127, -1128, -1129, -1130, -1132, -1133, -1134,
    -1135, -1164, -1170, -1176, -1182);
/* @error ignore-end */
/* End YUK-15711 */

/* Start YUK-15746 */
INSERT INTO YukonListEntry VALUES (2030, 1005, 0, 'Honeywell Wi-Fi 9000', 1332);
INSERT INTO YukonListEntry VALUES (2031, 1005, 0, 'Honeywell Wi-Fi VisionPRO 8000', 1333);
INSERT INTO YukonListEntry VALUES (2032, 1005, 0, 'Honeywell Wi-Fi FocusPRO', 1334);
INSERT INTO YukonListEntry VALUES (2033, 1005, 0, 'Honeywell Wi-Fi Thermostat', 1335);
/* End YUK-15746 */

/* Start YUK-15836 */
INSERT INTO YukonServices VALUES (22, 'HoneywellWifiDataListener', 'classpath:com/cannontech/services/honeywellWifiListener/honeywellWifiMessageListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
/* End YUK-15836 */

/* Start YUK-15859 */
CREATE TABLE HoneywellWifiThermostat (
   DeviceId             NUMERIC              NOT NULL,
   MacAddress           VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_HONEYWELLWIFITHERMOSTAT PRIMARY KEY (DeviceId)
);
GO

ALTER TABLE HoneywellWifiThermostat
   ADD CONSTRAINT FK_HONEYWELLTHERMOSTAT_DEVICE FOREIGN KEY (DeviceId)
      REFERENCES DEVICE (DEVICEID);
GO
/* End YUK-15859 */

/* Start YUK-15908 */
CREATE TABLE LMGroupHoneywellWiFi  (
   DeviceId             NUMERIC              NOT NULL,
   HoneywellGroupId     NUMERIC              NOT NULL,
   CONSTRAINT PK_LMGroupHoneywellWiFi PRIMARY KEY (DeviceId)
);
GO

ALTER TABLE LMGroupHoneywellWiFi
   ADD CONSTRAINT AK_LMGroupHoneywellWiFi UNIQUE (HoneywellGroupId);
GO

ALTER TABLE LMGroupHoneywellWiFi
   ADD CONSTRAINT FK_LMGroupHoneywellWiFiLMGroup FOREIGN KEY (DeviceId)
      REFERENCES LMGroup (DeviceID)
      ON DELETE CASCADE;
GO
/* End YUK-15908 */

/* Start YUK-14794 */
DELETE FROM YukonGroupRole      WHERE RolePropertyID = -10000;
DELETE FROM YukonRoleProperty   WHERE RolePropertyID = -10000;
UPDATE YukonRoleProperty        SET KeyName = 'Load Management View Enabled'    WHERE RolePropertyID = -10002;
UPDATE YukonRoleProperty        SET KeyName = 'System View Enabled'             WHERE RolePropertyID = -10004;
UPDATE YukonRoleProperty        SET KeyName = 'Versacom Utility Range'          WHERE RolePropertyID = -10005;
UPDATE YukonRoleProperty        SET KeyName = 'Transmission Exclusion Enabled'  WHERE RolePropertyID = -10007;
UPDATE YukonRoleProperty        SET KeyName = 'Manage Users'                    WHERE RolePropertyID = -10008;
UPDATE YukonRoleProperty        SET KeyName = 'Optional Protocols'              WHERE RolePropertyID = -10010;
UPDATE YukonRoleProperty        SET KeyName = 'Program Member Management'       WHERE RolePropertyID = -10011;
/* End YUK-14794 */

/* Start YUK-15906 */
ALTER TABLE HoneywellWifiThermostat	
ADD UserId NUMERIC;
GO
/* End YUK-14794 */

/* Start YUK-15957 */
INSERT INTO StateGroup VALUES (-27, 'NoYes', 'Status');

INSERT INTO State VALUES (-27, 0, 'Yes', 0, 6, 0);
INSERT INTO State VALUES (-27, 1, 'No', 1, 6, 0);
/* End YUK-15957 */

/* Start YUK-15962 */
UPDATE YukonRole
SET RoleName = 'C&I Curtailment', RoleDescription = 'Controls user and operator access to C&I Curtailment.'
WHERE RoleId = -211;

UPDATE YukonRoleProperty 
SET KeyName = 'C&I Curtailment Operator', Description = 'Controls access to the C&I Curtailment operator functionality. When false, only the C&I Curtailment user pages are available.' 
WHERE RolePropertyId = -21100 AND RoleId = -211; 
/* End YUK-15962 */

/* Start YUK-15963 */
INSERT INTO Point VALUES(-34, 'Analog', 'Message Broker CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1030, 'None', 0);
INSERT INTO Point VALUES(-35, 'Analog', 'Message Broker Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1031, 'None', 0);

INSERT INTO pointanalog VALUES(-34, -1, 1, 0 );
INSERT INTO pointanalog VALUES(-35, -1, 1, 0 );

INSERT INTO pointunit VALUES(-34, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO pointunit VALUES(-35, 56, 0, 1.0E+30, -1.0E+30, 0);
/* End YUK-15963 */

/* Start YUK-15987 */
EXEC sp_rename 'EncryptionKey.Value', 'PrivateKey', 'COLUMN';
GO

ALTER TABLE EncryptionKey
ADD PublicKey VARCHAR(608);

ALTER TABLE EncryptionKey
ALTER COLUMN PrivateKey VARCHAR(1920) NOT NULL;
GO
/* End YUK-15987 */

/* Start YUK-15972 */
ALTER TABLE DynamicPAOInfo DROP CONSTRAINT FK_DynPAOInfo_YukPAO;
GO

ALTER TABLE DynamicPAOInfo
   ADD CONSTRAINT FK_DynPAOInfo_YukPAO FOREIGN KEY (PAObjectID)
      REFERENCES YukonPAObject (PAObjectID)
         ON DELETE CASCADE;
GO
/* End YUK-15972 */

/* Start YUK-16013 */
ALTER TABLE EncryptionKey
ADD EncryptionKeyType VARCHAR(128) NOT NULL
DEFAULT 'ExpresscomOneWay';
GO
/* End YUK-16013 */

/* Start YUK-16017 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-20222,-202,'Water Leak Report','true','Controls access to the Water Leak Report.');
/* @error ignore-end */
/* End YUK-16017 */

/* Start YUK-16029 */
INSERT INTO YukonServices VALUES (23, 'BrokerSystemMetricsListener','classpath:com/cannontech/services/BrokerSystemMetricsListener/brokerSystemMetricsListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
/* End YUK-16029 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-JUN-2016', 'Latest Update', 0, GETDATE());*/
