/******************************************/ 
/**** Oracle DBupdates                 ****/ 
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
UPDATE DynamicCCFeeder 
SET PhaseAValueBeforeControl = PhaseAValue 
WHERE PhaseAValueBeforeControl IS NULL;
ALTER TABLE DynamicCCFeeder 
MODIFY PhaseAValueBeforeControl FLOAT NOT NULL;

ALTER TABLE DynamicCCFeeder 
ADD PhaseBValueBeforeControl FLOAT;
UPDATE DynamicCCFeeder SET PhaseBValueBeforeControl = PhaseBValue 
WHERE PhaseBValueBeforeControl IS NULL;
ALTER TABLE DynamicCCFeeder 
MODIFY PhaseBValueBeforeControl FLOAT NOT NULL;

ALTER TABLE DynamicCCFeeder 
ADD PhaseCValueBeforeControl FLOAT;
UPDATE DynamicCCFeeder 
SET PhaseCValueBeforeControl = PhaseCValue 
WHERE PhaseCValueBeforeControl IS NULL;
ALTER TABLE DynamicCCFeeder
MODIFY PhaseCValueBeforeControl FLOAT NOT NULL;

ALTER TABLE DynamicCCSubstationBus
ADD PhaseAValueBeforeControl FLOAT;
UPDATE DynamicCCSubstationBus
SET PhaseAValueBeforeControl = PhaseAValue
WHERE PhaseAValueBeforeControl IS NULL;
ALTER TABLE DynamicCCSubstationBus
MODIFY PhaseAValueBeforeControl FLOAT NOT NULL;

ALTER TABLE DynamicCCSubstationBus
ADD PhaseBValueBeforeControl FLOAT;
UPDATE DynamicCCSubstationBus
SET PhaseBValueBeforeControl = PhaseBValue
WHERE PhaseBValueBeforeControl IS NULL;
ALTER TABLE DynamicCCSubstationBus
MODIFY PhaseBValueBeforeControl FLOAT NOT NULL;

ALTER TABLE DynamicCCSubstationBus
ADD PhaseCValueBeforeControl FLOAT;
UPDATE DynamicCCSubstationBus
SET PhaseCValueBeforeControl = PhaseCValue
WHERE PhaseCValueBeforeControl IS NULL;
ALTER TABLE DynamicCCSubstationBus
MODIFY PhaseCValueBeforeControl FLOAT NOT NULL;
/* @error ignore-end */
/* End YUK-7966 */

/* Start YUK-7977 */
UPDATE YukonPAObject 
SET Type = 'INTEGRATION GROUP' 
WHERE Type = 'XML GROUP';
/* End YUK-7977 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
