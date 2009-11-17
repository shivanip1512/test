/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7966 */
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
/* End YUK-7966 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '29-OCT-2009', 'Latest Update', 18);
