/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9213 */
UPDATE CapControlStrategy 
SET ControlMethod = 'INDIVIDUAL_FEEDER' 
WHERE ControlMethod = 'IndividualFeeder'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'SUBSTATION_BUS' 
WHERE ControlMethod = 'SubstationBus'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'BUSOPTIMIZED_FEEDER' 
WHERE ControlMethod = 'BusOptimizedFeeder'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'MANUAL_ONLY' 
WHERE ControlMethod = 'ManualOnly'; 
UPDATE CapControlStrategy 
SET ControlMethod = 'TIME_OF_DAY' 
WHERE ControlMethod = 'TimeOfDay'; 

UPDATE CapControlStrategy 
SET ControlUnits = 'NONE' 
WHERE ControlUnits = '(none)'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'MULTI_VOLT' 
WHERE ControlUnits = 'Multi Volt'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'KVAR' 
WHERE ControlUnits = 'kVAr'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'MULTI_VOLT_VAR' 
WHERE ControlUnits = 'Multi Volt/VAR'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'PFACTOR_KW_KVAR' 
WHERE ControlUnits = 'P-Factor kW/kVAr'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'TIME_OF_DAY' 
WHERE ControlUnits = 'Time of Day'; 
UPDATE CapControlStrategy 
SET ControlUnits = 'INTEGRATED_VOLT_VAR' 
WHERE ControlUnits = 'Integrated Volt/Var';
/* End YUK-9213 */ 

/* Start YUK-9160 */
ALTER TABLE CCSubstationBusToLTC DROP CONSTRAINT FK_CCSubBusToLTC_CapContSubBus;
ALTER TABLE CCSubstationBusToLTC DROP CONSTRAINT FK_CCSubBusToLTC_YukonPAO;
DROP TABLE CCSubstationBusToLTC;
/* End YUK-9160 */ 

/* Start YUK-9143 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -10810;
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -10810;
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -10810;
/* End YUK-9143 */ 

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
