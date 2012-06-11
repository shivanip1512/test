/******************************************/ 
/****     Oracle DBupdates             ****/ 
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
        REFERENCES MACSchedule (ScheduleId);
UPDATE MACSimpleSchedule
SET TargetPAObjectId = 0
WHERE TargetPAObjectId IS NULL;
ALTER TABLE MACSimpleSchedule
    MODIFY TargetPAObjectId NUMBER NOT NULL;

ALTER TABLE PorterResponseMonitorErrorCode
    MODIFY RuleId NUMBER NOT NULL;

ALTER TABLE OptOutTemporaryOverride
    MODIFY OptOutValue VARCHAR2(25);

UPDATE AcctThermostatScheduleEntry
SET CoolTemp = 72
WHERE CoolTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    MODIFY CoolTemp FLOAT NOT NULL;
    
UPDATE AcctThermostatScheduleEntry
SET HeatTemp = 72
WHERE HeatTemp IS NULL;
ALTER TABLE AcctThermostatScheduleEntry
    MODIFY HeatTemp FLOAT NOT NULL;
/* @error ignore-end */
/* End YUK-10963 */
    
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.4', 'Garrett D', '11-JUN-2012', 'Latest Update', 5 );