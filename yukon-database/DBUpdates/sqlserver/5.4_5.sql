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
    ALTER COLUMN CoolTemp FLOAT NOT NULL;

UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    ALTER COLUMN HeatTemp FLOAT NOT NULL;
/* @error ignore-end */
/* End YUK-10963 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
