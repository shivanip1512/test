/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8218 */
INSERT INTO YukonRoleProperty VALUES(-20910,-209,'Device Reconfiguration','false','Access to Device Reconfiguration Tool');
/* End YUK-8218 */

/* Start YUK-8219 */
ALTER TABLE OptOutTemporaryOverride
    ADD CONSTRAINT FK_OptOutTempOver_LMProgWebPub FOREIGN KEY (ProgramId)
        REFERENCES LMProgramWebPublishing (ProgramId)
            ON DELETE CASCADE;
/* End YUK-8219 */

/* Start YUK-8271 */
CREATE TABLE CCStrategyTargetSettings (
   StrategyId           NUMBER              NOT NULL,
   SettingName          VARCHAR2(255)         NOT NULL,
   SettingValue         VARCHAR2(255)         NOT NULL,
   SettingType          VARCHAR2(255)         NOT NULL,
   CONSTRAINT PK_CCStratTarSet PRIMARY KEY (StrategyId, SettingName, SettingType)
);

ALTER TABLE CCStrategyTargetSettings
   ADD CONSTRAINT FK_CCStratTarSet_CapContStrat FOREIGN KEY (StrategyId)
      REFERENCES CapControlStrategy (StrategyId)
          ON DELETE CASCADE;

/* Porting Time limits from CCStrategyTimeOfDay */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId,
CASE WHEN (StartTimeSeconds = 0) THEN '00:00'
WHEN (StartTimeSeconds = 1*3600) THEN '01:00'
WHEN (StartTimeSeconds = 2*3600) THEN '02:00'
WHEN (StartTimeSeconds = 3*3600) THEN '03:00'
WHEN (StartTimeSeconds = 4*3600) THEN '04:00'
WHEN (StartTimeSeconds = 5*3600) THEN '05:00'
WHEN (StartTimeSeconds = 6*3600) THEN '06:00'
WHEN (StartTimeSeconds = 7*3600) THEN '07:00'
WHEN (StartTimeSeconds = 8*3600) THEN '08:00'
WHEN (StartTimeSeconds = 9*3600) THEN '09:00'
WHEN (StartTimeSeconds = 10*3600) THEN '10:00'
WHEN (StartTimeSeconds = 11*3600) THEN '11:00'
WHEN (StartTimeSeconds = 12*3600) THEN '12:00'
WHEN (StartTimeSeconds = 13*3600) THEN '13:00'
WHEN (StartTimeSeconds = 14*3600) THEN '14:00'
WHEN (StartTimeSeconds = 15*3600) THEN '15:00'
WHEN (StartTimeSeconds = 16*3600) THEN '16:00'
WHEN (StartTimeSeconds = 17*3600) THEN '17:00'
WHEN (StartTimeSeconds = 18*3600) THEN '18:00'
WHEN (StartTimeSeconds = 19*3600) THEN '19:00'
WHEN (StartTimeSeconds = 20*3600) THEN '20:00'
WHEN (StartTimeSeconds = 21*3600) THEN '21:00'
WHEN (StartTimeSeconds = 22*3600) THEN '22:00'
WHEN (StartTimeSeconds = 23*3600) THEN '23:00'
WHEN (StartTimeSeconds = 24*3600) THEN '24:00'
ELSE '00:00'
END,
PercentClose,
'WEEKDAY'
FROM CCStrategyTimeOfDay;

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId,
CASE WHEN (StartTimeSeconds = 0) THEN '00:00'
WHEN (StartTimeSeconds = 1*3600) THEN '01:00'
WHEN (StartTimeSeconds = 2*3600) THEN '02:00'
WHEN (StartTimeSeconds = 3*3600) THEN '03:00'
WHEN (StartTimeSeconds = 4*3600) THEN '04:00'
WHEN (StartTimeSeconds = 5*3600) THEN '05:00'
WHEN (StartTimeSeconds = 6*3600) THEN '06:00'
WHEN (StartTimeSeconds = 7*3600) THEN '07:00'
WHEN (StartTimeSeconds = 8*3600) THEN '08:00'
WHEN (StartTimeSeconds = 9*3600) THEN '09:00'
WHEN (StartTimeSeconds = 10*3600) THEN '10:00'
WHEN (StartTimeSeconds = 11*3600) THEN '11:00'
WHEN (StartTimeSeconds = 12*3600) THEN '12:00'
WHEN (StartTimeSeconds = 13*3600) THEN '13:00'
WHEN (StartTimeSeconds = 14*3600) THEN '14:00'
WHEN (StartTimeSeconds = 15*3600) THEN '15:00'
WHEN (StartTimeSeconds = 16*3600) THEN '16:00'
WHEN (StartTimeSeconds = 17*3600) THEN '17:00'
WHEN (StartTimeSeconds = 18*3600) THEN '18:00'
WHEN (StartTimeSeconds = 19*3600) THEN '19:00'
WHEN (StartTimeSeconds = 20*3600) THEN '20:00'
WHEN (StartTimeSeconds = 21*3600) THEN '21:00'
WHEN (StartTimeSeconds = 22*3600) THEN '22:00'
WHEN (StartTimeSeconds = 23*3600) THEN '23:00'
WHEN (StartTimeSeconds = 24*3600) THEN '24:00'
ELSE '00:00'
END,
WkndPercentClose,
'WEEKEND'
FROM CCStrategyTimeOfDay;

DROP TABLE CCStrategyTimeOfDay;

UPDATE CapControlStrategy
SET ControlUnits = 'Time of Day'
WHERE ControlMethod = 'TimeOfDay';

/* Porting Volt limits from CapControlStrategy */
INSERT INTO CCStrategyTargetSettings (StrategyId , SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Upper Volt Limit', PeakLead, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND (ControlUnits = 'VOLTS'
     OR ControlUnits = 'Multi Volt'
     OR ControlUnits = 'Multi Volt/VAR');

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Upper Volt Limit', OffpkLead, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND (ControlUnits = 'VOLTS'
     OR ControlUnits = 'Multi Volt'
     OR ControlUnits = 'Multi Volt/VAR');

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Lower Volt Limit', PeakLag, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND (ControlUnits = 'VOLTS'
     OR ControlUnits = 'Multi Volt'
     OR ControlUnits = 'Multi Volt/VAR');

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Lower Volt Limit', OffpkLag, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND (ControlUnits = 'VOLTS'
     OR ControlUnits = 'Multi Volt'
     OR ControlUnits = 'Multi Volt/VAR');

/* Porting Multi Volt-VAR kVar limits from CapControlStrategy */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Leading', PeakVARlead, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'Multi Volt/VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Leading', OffPkVARlead, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'Multi Volt/VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Lagging', PeakVARlag, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'Multi Volt/VAR';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Lagging', OffPkVARlag, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'Multi Volt/VAR'; 

/* Porting regular kVar limits from CapControlStrategy */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Leading', PeakLead, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Leading', OffpkLead, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Lagging', PeakLag, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'KVAR Lagging', OffpkLag, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'kVAr'; 

/* Porting Power Factor from CapControlStrategy */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Target PF', PeakPFSetPoint, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'P-Factor kW/kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Target PF', offPkPFSetPoint, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'P-Factor kW/kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Min. of Bank Open', peaklead, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'P-Factor kW/kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Min. of Bank Open', offPkLead, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'P-Factor kW/kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Min. of Bank Close', peaklag, 'PEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'P-Factor kW/kVAr';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Min. of Bank Close', offPkLag, 'OFFPEAK'
FROM CapControlStrategy
WHERE StrategyId > 0
AND ControlUnits = 'P-Factor kW/kVAr'; 
COMMIT;

/* Removing ported columns from CapControlStrategy */
ALTER TABLE CapControlStrategy
DROP (PeakLag, PeakLead, OffPkLag, OffPkLead, PeakVARLag, PeakVARLead,
      OffPkVARlag, OffPkVARlead, PeakPFSetPoint, OffPkPFSetPoint);
/* End YUK-8271 */

/* Start YUK-8264 */
/* @error ignore-begin */
DROP INDEX INDX_PAO_PAOName_Type;
/* @error ignore-end */
/* End YUK-8264 */

/* Start YUK-8213 */
ALTER TABLE LMCustomerEventBase MODIFY Notes VARCHAR2(500);
/* End YUK-8213 */

/* Start YUK-8276 */
DELETE CCSeasonStrategyAssignment WHERE StrategyId = 0;
DELETE CCHolidayStrategyAssignment WHERE StrategyId = 0;
DELETE CapControlStrategy WHERE StrategyId = 0;
/* End YUK-8276 */

/* Start YUK-8250 */
ALTER TABLE DynamicCCOriginalParent
    DROP CONSTRAINT FK_DynCCOrigParent_YukonPAO;
ALTER TABLE DynamicCCOriginalParent
    ADD CONSTRAINT FK_DynCCOrigParent_YukonPAO FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
/* End YUK-8250 */

/* Start YUK-8301 */
CREATE TABLE CCSubstationBusToLTC  (
   LtcId                NUMBER                          NOT NULL,
   SubstationBusId      NUMBER                          NOT NULL,
   CONSTRAINT PK_CCSubBusToLtc PRIMARY KEY (LtcId)
);

ALTER TABLE CCSubstationBusToLTC
    ADD CONSTRAINT FK_CCSubBusToLTC_CapContSubBus FOREIGN KEY (SubstationBusId)
        REFERENCES CapControlSubstationBus (SubstationBusId)
            ON DELETE CASCADE;

ALTER TABLE CCSubstationBusToLTC
    ADD CONSTRAINT FK_CCSubBusToLTC_YukonPAO FOREIGN KEY (LtcId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
/* End YUK-8301 */


/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
