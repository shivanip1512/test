/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 
 
/* Start YUK-7940 */ 
UPDATE YukonRoleProperty 
SET DefaultValue = 'false' 
WHERE RolePropertyId = -21307; 
/* End YUK-7940 */ 

/* Start YUK-7966 */
/* @error ignore-begin */
ALTER TABLE DynamicCCFeeder
ADD PhaseAValueBeforeControl FLOAT;
GO
UPDATE DynamicCCFeeder
SET PhaseAValueBeforeControl = PhaseAValue
WHERE PhaseAValueBeforeControl IS NULL;
ALTER TABLE DynamicCCFeeder
ALTER COLUMN PhaseAValueBeforeControl FLOAT NOT NULL;
GO

ALTER TABLE DynamicCCFeeder
ADD PhaseBValueBeforeControl FLOAT;
GO
UPDATE DynamicCCFeeder
SET PhaseBValueBeforeControl = PhaseBValue
WHERE PhaseBValueBeforeControl IS NULL;
ALTER TABLE DynamicCCFeeder
ALTER COLUMN PhaseBValueBeforeControl FLOAT NOT NULL;
GO

ALTER TABLE DynamicCCFeeder
ADD PhaseCValueBeforeControl FLOAT;
GO
UPDATE DynamicCCFeeder
SET PhaseCValueBeforeControl = PhaseCValue
WHERE PhaseCValueBeforeControl IS NULL;
ALTER TABLE DynamicCCFeeder
ALTER COLUMN PhaseCValueBeforeControl FLOAT NOT NULL;
GO

ALTER TABLE DynamicCCSubstationBus
ADD PhaseAValueBeforeControl FLOAT;
GO
UPDATE DynamicCCSubstationBus
SET PhaseAValueBeforeControl = PhaseAValue
WHERE PhaseAValueBeforeControl IS NULL;
ALTER TABLE DynamicCCSubstationBus
ALTER COLUMN PhaseAValueBeforeControl FLOAT NOT NULL;
GO

ALTER TABLE DynamicCCSubstationBus
ADD PhaseBValueBeforeControl FLOAT;
GO
UPDATE DynamicCCSubstationBus
SET PhaseBValueBeforeControl = PhaseBValue
WHERE PhaseBValueBeforeControl IS NULL;
ALTER TABLE DynamicCCSubstationBus
ALTER COLUMN PhaseBValueBeforeControl FLOAT NOT NULL;
GO

ALTER TABLE DynamicCCSubstationBus
ADD PhaseCValueBeforeControl FLOAT;
GO
UPDATE DynamicCCSubstationBus
SET PhaseCValueBeforeControl = PhaseCValue
WHERE PhaseCValueBeforeControl IS NULL;
ALTER TABLE DynamicCCSubstationBus
ALTER COLUMN PhaseCValueBeforeControl FLOAT NOT NULL;
GO
/* @error ignore-end */
/* End YUK-7966 */ 

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
