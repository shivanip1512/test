/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10982 */
UPDATE BillingFileFormats
SET FormatType = 'CMEP-MDM1'
WHERE FormatType = 'CMEP';
/* End YUK-10982 */

/* Start YUK-10978 */
UPDATE ArchiveValuesExportField 
SET FieldType = 'ADDRESS' 
WHERE FieldType = 'DLC_ADDRESS';
/* End YUK-10978 */

/* Start YUK-10963 */
/* @error ignore-begin */
ALTER TABLE MACSimpleSchedule
    DROP CONSTRAINT FK_MACSIMPLE_MACSCHED_ID;
ALTER TABLE MACSimpleSchedule
    ADD CONSTRAINT FK_MACSimpSch_MACSch FOREIGN KEY (ScheduleId)
        REFERENCES MACSchedule (ScheduleID);
GO
sp_rename @objname = 'MACSchedule.ScheduleID',
          @newname = 'ScheduleId',
          @objtype = 'COLUMN';
UPDATE MACSimpleSchedule
SET TargetPAObjectId = 0
WHERE TargetPAObjectId IS NULL;
ALTER TABLE MACSimpleSchedule
    ALTER COLUMN TargetPAObjectId NUMERIC NOT NULL;

ALTER TABLE PorterResponseMonitorErrorCode
    ALTER COLUMN RuleId NUMERIC NOT NULL;

ALTER TABLE OptOutTemporaryOverride
    ALTER COLUMN OptOutValue VARCHAR(25);
    
DECLARE constraint_cursor CURSOR FOR
        SELECT name from sys.default_constraints
        WHERE OBJECT_NAME(parent_object_id) = 'AcctThermostatSchedule'
          AND name like 'DF__AcctTherm__%';
OPEN constraint_cursor;
DECLARE @constraintName VARCHAR(100);
FETCH NEXT FROM constraint_cursor INTO @constraintName;
WHILE (@@FETCH_STATUS <> -1)
BEGIN
    EXECUTE('ALTER TABLE AcctThermostatSchedule DROP CONSTRAINT ' + @constraintName);
    FETCH NEXT FROM constraint_cursor INTO @constraintName;
END

UPDATE AcctThermostatScheduleEntry
SET CoolTemp = 72
WHERE CoolTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    ALTER COLUMN CoolTemp NUMERIC NOT NULL;

UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    ALTER COLUMN HeatTemp NUMERIC NOT NULL;
/* @error ignore-end */
/* End YUK-10963 */
    
/* Start YUK-10981 */
CREATE TABLE tempDynamicPaoStatistics (
   DynamicPAOStatisticsId NUMERIC              not null,
   PAObjectId             NUMERIC              not null,
   StatisticType          VARCHAR(16)          not null,
   StartDateTime          DATETIME             not null,
   Requests               NUMERIC              not null,
   Attempts               NUMERIC              not null,
   Completions            NUMERIC              not null,
   CommErrors             NUMERIC              not null,
   ProtocolErrors         NUMERIC              not null,
   SystemErrors           NUMERIC              not null,
);
GO
INSERT INTO tempDynamicPaoStatistics 
    SELECT 
        MIN(DynamicPaoStatisticsId), 
        PAObjectId, 
        'Lifetime',
        '2000-01-01 00:00',
        SUM(requests), 
        SUM(attempts), 
        SUM(completions), 
        SUM(CommErrors), 
        SUM(ProtocolErrors), 
        SUM(SystemErrors) 
    FROM DynamicPAOStatistics 
    WHERE statistictype = 'Lifetime' 
    GROUP BY PAObjectId;
GO
DELETE FROM DynamicPAOStatistics WHERE StatisticType = 'Lifetime';
GO
INSERT INTO DynamicPAOStatistics 
    SELECT * FROM tempDynamicPaoStatistics;
GO
DROP TABLE tempDynamicPaoStatistics;
GO
/* End YUK-10981 */

/* Start YUK- */

/* End YUK- */

/* Start YUK- */
/* End YUK- */    
/* Start YUK- */
/* End YUK- */    
/* Start YUK- */
/* End YUK- */  
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
