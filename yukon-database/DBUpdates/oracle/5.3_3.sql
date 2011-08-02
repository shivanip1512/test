/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-10000 */ 
UPDATE State 
SET Text = 'Connected'
WHERE StateGroupId = -13
  AND RawState = 0; 

INSERT INTO State VALUES(-13, 2, 'Disconnected', 2, 6, 0); 

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
INSERT INTO YukonRoleProperty VALUES(-1121, -2, 'Allow Single Day Thermostat Schedules', 'true', 'Allow the use of schedules where every day shares the same values for compatible thermostats.'); 
INSERT INTO YukonRoleProperty VALUES(-1122, -2, 'Allow 5/2 Thermostat Schedules', 'true', 'Allow the use of 5/2 day schedules (different schedule each day of the week) for compatible thermostats. Weekday/Weekend.'); 
INSERT INTO YukonRoleProperty VALUES(-1123, -2, 'Allow 5/1/1 Thermostat Schedules', 'false', 'Allow the use of 5/1/1 schedules for compatible thermostats. Weekday/Saturday/Sunday.'); 
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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
