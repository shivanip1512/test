/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-10000 */ 
UPDATE State 
SET Text = 'Connected'
WHERE StateGroupId = -13
  AND RawState = 0; 

UPDATE State 
SET ForegroundColor = 7 
WHERE StateGroupId = -13 
  AND RawState = 1;

INSERT INTO State VALUES(-13, 2, 'Disconnected', 1, 6, 0); 

DELETE FROM PointStatus 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 

DELETE FROM PointAlarming 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 

DELETE FROM DynamicPointDispatch 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 

DELETE FROM Display2WayData
WHERE PointId IN (SELECT P.PointId 
                   FROM Point P 
                     JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                   WHERE P.StateGroupId = -11 
                     AND (PAO.Type = 'ZigBee Utility Pro' OR 
                          PAO.Type = 'Digi Gateway'));

DELETE FROM DynamicPointAlarming
WHERE PointId IN (SELECT P.PointId 
                   FROM Point P 
                     JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                   WHERE P.StateGroupId = -11 
                     AND (PAO.Type = 'ZigBee Utility Pro' OR 
                          PAO.Type = 'Digi Gateway'));

DELETE FROM Point 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 
/* End YUK-10000 */

/* Start YUK-10062 */ 
CREATE INDEX Indx_DevCarSet_Address ON DeviceCarrierSettings (
   ADDRESS ASC
);
/* End YUK-10062 */

/* Start YUK-10067 */ 
CREATE INDEX Indx_DynVer_TimeArr_Code ON DynamicVerification (
   TimeArrival ASC,
   Code ASC
);
/* End YUK-10067 */

/* Start YUK-10090 */
/* @error warn-once */
/* @start-block */
DECLARE
    errorFlagCount INT;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount
    FROM YukonGroupRole YGR, YukonUserRole YUR
    WHERE (YGR.RolePropertyId IN (-20892, -40205)
           AND YGR.Value = 'true')
      OR (YUR.RolePropertyId IN (-20892, -40205)
          AND YUR.Value = 'true');

    IF 0 < errorFlagCount THEN
        RAISE_APPLICATION_ERROR(-20001, 'The database contains Thermostat Schedule 7 Day role properties that are about to be reset to the default value. This will change the current value from true to false. Please record all current values for Allow 5/1/1, and 7 day thermostat role properties before continuing. See YUK-10090 for more inforamtion.');
    END IF;
END;
/
/* @end-block */

/* @error warn-once */
/* @start-block */
DECLARE
    errorFlagCount INT;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount
    FROM YukonGroupRole YGR, YukonUserRole YUR
    WHERE (YGR.RolePropertyId IN (-40204, -20899)
           AND YGR.Value = 'true')
      OR (YUR.RolePropertyId IN (-40204, -20899)
          AND YUR.Value = 'true');

    IF 0 < errorFlagCount THEN
        RAISE_APPLICATION_ERROR(-20001, 'The database contains Thermostat Schedule 5-2 role properties that are about to be reset to the default value. This will change the current value from true to false. Please record all current values for Allow 5/1/1, and 7 day thermostat role properties before continuing. See YUK-10090 for more inforamtion.');
    END IF;
END;
/
/* @end-block */

INSERT INTO YukonRoleProperty VALUES(-1121, -2, 'Allow Single Day Thermostat Schedules', 'true', 'Allow the use of schedules where every day shares the same values for compatible thermostats.'); 
INSERT INTO YukonRoleProperty VALUES(-1122, -2, 'Allow 5/2 Thermostat Schedules', 'false', 'Allow the use of 5/2 day schedules for compatible thermostats. Weekday/Weekend.'); 
INSERT INTO YukonRoleProperty VALUES(-1123, -2, 'Allow 5/1/1 Thermostat Schedules', 'true', 'Allow the use of 5/1/1 schedules for compatible thermostats. Weekday/Saturday/Sunday.'); 
INSERT INTO YukonRoleProperty VALUES(-1124, -2, 'Allow 7 Day Thermostat Schedules', 'false', 'Allow the use of 7 day schedules (different schedule each day of the week) for compatible thermostats.'); 

ALTER TABLE AcctThermostatScheduleEntry
ADD TemporaryCoolTemp FLOAT;
ALTER TABLE AcctThermostatScheduleEntry
ADD TemporaryHeatTemp FLOAT;

UPDATE AcctThermostatScheduleEntry
SET TemporaryCoolTemp = CoolTemp;
UPDATE AcctThermostatScheduleEntry
SET TemporaryHeatTemp = HeatTemp;

ALTER TABLE AcctThermostatScheduleEntry
MODIFY CoolTemp NUMBER NULL;
ALTER TABLE AcctThermostatScheduleEntry
MODIFY HeatTemp NUMBER NULL;

UPDATE AcctThermostatScheduleEntry
SET CoolTemp = NULL;
UPDATE AcctThermostatScheduleEntry
SET HeatTemp = NULL;

ALTER TABLE AcctThermostatScheduleEntry 
MODIFY CoolTemp FLOAT; 
ALTER TABLE AcctThermostatScheduleEntry 
MODIFY HeatTemp FLOAT;

UPDATE AcctThermostatScheduleEntry
SET CoolTemp = TemporaryCoolTemp ;
UPDATE AcctThermostatScheduleEntry
SET HeatTemp = TemporaryHeatTemp;

ALTER TABLE AcctThermostatScheduleEntry
DROP COLUMN TemporaryCoolTemp;
ALTER TABLE AcctThermostatScheduleEntry
DROP COLUMN TemporaryHeatTemp;

ALTER TABLE AcctThermostatScheduleEntry 
MODIFY CoolTemp FLOAT NOT NULL; 
ALTER TABLE AcctThermostatScheduleEntry 
MODIFY HeatTemp FLOAT NOT NULL;

DELETE FROM YukonUserRole 
WHERE RolePropertyId IN (-20892, -20899, -40204, -40205); 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId IN (-20892, -20899, -40204, -40205); 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId IN (-20892, -20899, -40204, -40205); 
/* End YUK-10090 */

/* Start YUK-10120 */ 
CREATE INDEX Indx_SchGrpComReq_JobId ON ScheduledGrpCommandRequest (
   JobId ASC
);
/* End YUK-10120 */

/* Start YUK-10129 */ 
INSERT INTO YukonServices VALUES (17, 'DigiPollingService', 'classpath:com/cannontech/services/digiPollingService/digiPollingService.xml', 'ServiceManager');
/* End YUK-10129 */

/* Start YUK-10140 */ 
INSERT INTO YukonListEntry VALUES (1047, 1005, 0, 'LCR-6200(ZIGBEE)', 1320); 
INSERT INTO YukonListEntry VALUES (1048, 1005, 0, 'LCR-6200(EXPRESSCOM)', 1321); 
INSERT INTO YukonListEntry VALUES (1049, 1005, 0, 'LCR-6600(ZIGBEE)', 1322); 
INSERT INTO YukonListEntry VALUES (1050, 1005, 0, 'LCR-6600(EXPRESSCOM)', 1323);
/* End YUK-10140 */

/* Start YUK-10131 */ 
ALTER TABLE ArchiveDataAnalysis 
ADD AnalysisStatus VARCHAR2(60); 

UPDATE ArchiveDataAnalysis 
SET AnalysisStatus = 'COMPLETE'; 

ALTER TABLE ArchiveDataAnalysis 
MODIFY AnalysisStatus VARCHAR2(60) NOT NULL; 

ALTER TABLE ArchiveDataAnalysis 
ADD StatusId VARCHAR(60) NULL;
/* End YUK-10131 */

/* Start YUK-10130 */ 
UPDATE YukonPAObject 
SET Type = 'ZigBee Endpoint' 
WHERE Type = 'ZigBee Utility Pro';
/* End YUK-10130 */

/* Start YUK-10141 */ 
ALTER TABLE DynamicBillingField 
ADD Channel VARCHAR2(5); 
UPDATE DynamicBillingField 
SET Channel = 'ONE'; 
ALTER TABLE DynamicBillingField 
MODIFY Channel VARCHAR2(5) NOT NULL; 

UPDATE DynamicBillingField
SET ReadingType = 'DEVICE_DATA' 
WHERE FieldName = 'Plain Text' 
  OR FieldName = 'meterNumber' 
  OR FieldName = 'paoName' 
  OR FieldName = 'address';
/* End YUK-10141 */

/* Start YUK-10133 */
ALTER TABLE LMThermostatManualEvent
ADD PreviousTemperatureTemp FLOAT;

UPDATE LMThermostatManualEvent
SET PreviousTemperatureTemp = PreviousTemperature;

UPDATE LMThermostatManualEvent
SET PreviousTemperature = NULL;

ALTER TABLE LMThermostatManualEvent 
MODIFY PreviousTemperature FLOAT;

UPDATE LMThermostatManualEvent
SET PreviousTemperature = PreviousTemperatureTemp ;

ALTER TABLE LMThermostatManualEvent
DROP COLUMN PreviousTemperatureTemp;
/* End YUK-10133 */

/* Start YUK-10132 */
ALTER TABLE StaticPAOInfo 
MODIFY Value VARCHAR(128) NULL;
/* End YUK-10132 */

/* Start YUK-10146 */
ALTER TABLE ArchiveDataAnalysis 
ADD IntervalPeriod VARCHAR2(60) NULL; 

UPDATE ArchiveDataAnalysis 
SET IntervalPeriod = 'PT' || to_char(IntervalLengthInMillis/60000) || 'M'; 

ALTER TABLE ArchiveDataAnalysis 
MODIFY IntervalPeriod VARCHAR2(60) NOT NULL; 

ALTER TABLE ArchiveDataAnalysis 
DROP COLUMN IntervalLengthInMillis;
/* End YUK-10146 */

/* Start YUK-10150 */
ALTER TABLE ThermostatEventHistory 
ADD ManualTempTemp FLOAT;

UPDATE ThermostatEventHistory 
SET ManualTempTemp = ManualTemp;

UPDATE ThermostatEventHistory 
SET ManualTemp = NULL;

ALTER TABLE ThermostatEventHistory  
MODIFY ManualTemp FLOAT;

UPDATE ThermostatEventHistory 
SET ManualTemp = ManualTempTemp ;

ALTER TABLE ThermostatEventHistory 
DROP COLUMN ManualTempTemp;
/* End YUK-10150 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.3', 'Matt K', '16-Aug-2011', 'Latest Update', 3 );
