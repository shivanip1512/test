/******************************************/ 
/**** SQL Server DBupdates             ****/ 
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
INSERT INTO YukonRoleProperty VALUES(-1121, -2, 'Allow Single Day Thermostat Schedules', 'true', 'Allow the use of schedules where every day shares the same values for compatible thermostats.'); 
INSERT INTO YukonRoleProperty VALUES(-1122, -2, 'Allow 5/2 Thermostat Schedules', 'false', 'Allow the use of 5/2 day schedules for compatible thermostats. Weekday/Weekend.'); 
INSERT INTO YukonRoleProperty VALUES(-1123, -2, 'Allow 5/1/1 Thermostat Schedules', 'true', 'Allow the use of 5/1/1 schedules for compatible thermostats. Weekday/Saturday/Sunday.'); 
INSERT INTO YukonRoleProperty VALUES(-1124, -2, 'Allow 7 Day Thermostat Schedules', 'false', 'Allow the use of 7 day schedules (different schedule each day of the week) for compatible thermostats.'); 

ALTER TABLE AcctThermostatScheduleEntry 
ALTER COLUMN CoolTemp FLOAT; 

ALTER TABLE AcctThermostatScheduleEntry 
ALTER COLUMN HeatTemp FLOAT; 

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
ADD AnalysisStatus VARCHAR(60); 
GO 
UPDATE ArchiveDataAnalysis 
SET AnalysisStatus = 'COMPLETE'; 
GO 
ALTER TABLE ArchiveDataAnalysis 
ALTER COLUMN AnalysisStatus VARCHAR(60) NOT NULL; 
GO

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
ADD Channel VARCHAR(5); 
GO 
UPDATE DynamicBillingField 
SET Channel = 'ONE'; 
GO 
ALTER TABLE DynamicBillingField 
ALTER COLUMN Channel VARCHAR(5) NOT NULL; 
GO

UPDATE DynamicBillingField
SET ReadingType = 'DEVICE_DATA' 
WHERE FieldName = 'Plain Text' 
  OR FieldName = 'meterNumber' 
  OR FieldName = 'paoName' 
  OR FieldName = 'address';
/* End YUK-10141 */

/* Start YUK-10133 */ 
ALTER TABLE LMThermostatManualEvent 
ALTER COLUMN PreviousTemperature FLOAT;
/* End YUK-10133 */

/* Start YUK-10132 */
ALTER TABLE StaticPAOInfo 
ALTER COLUMN Value VARCHAR(128) NULL;
/* End YUK-10132 */

/* Start YUK-10146 */
ALTER TABLE ArchiveDataAnalysis 
ADD IntervalPeriod VARCHAR(60); 
GO 
UPDATE ArchiveDataAnalysis 
SET IntervalPeriod = 'PT' + CAST(CAST(IntervalLengthInMillis/60000 AS NUMERIC(20)) AS VARCHAR(60)) + 'M'; 
GO 
ALTER TABLE ArchiveDataAnalysis 
ALTER COLUMN IntervalPeriod VARCHAR(60) NOT NULL; 
GO 
ALTER TABLE ArchiveDataAnalysis 
DROP COLUMN IntervalLengthInMillis;
/* End YUK-10146 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.3', 'Matt K', '11-Aug-2011', 'Latest Update', 3 );
