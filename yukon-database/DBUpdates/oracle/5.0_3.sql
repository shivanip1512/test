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

/* Start YUK-6820 */
UPDATE YukonRoleProperty 
SET DefaultValue = 'true'
WHERE RolePropertyID = -10814;
/* End YUK-6820 */

/* Start YUK-7993 */
CREATE TABLE DynamicCCOriginalParent  (
   PAObjectId           NUMBER                          not null,
   OriginalParentId     NUMBER                          not null,
   OriginalSwitchingOrder FLOAT                           not null,
   OriginalCloseOrder   FLOAT                           not null,
   OriginalTripOrder    FLOAT                           not null,
   CONSTRAINT PK_DynCCOrigParent PRIMARY KEY (PAObjectId)
);

ALTER TABLE DynamicCCOriginalParent
   ADD CONSTRAINT FK_DynCCOrigParent_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId);
      
INSERT INTO DynamicCCOriginalParent
SELECT DeviceId, 0, 0, 0, 0
FROM CapBank;

INSERT INTO DynamicCCOriginalParent
SELECT FeederId, 0, 0, 0, 0
FROM CapControlFeeder;

UPDATE DynamicCCOriginalParent
SET OriginalParentId = DCCCB.OriginalFeederId,
    OriginalSwitchingOrder = DCCCB.OriginalSwitchingOrder
FROM DynamicCCOriginalparent DCCOP, DynamicCCCapBank DCCCB
WHERE DCCCB.CapBankId = DCCOP.PAObjectId;

ALTER TABLE DynamicCCCapBank DROP COLUMN OriginalFeederId;
ALTER TABLE DynamicCCCapBank DROP COLUMN OriginalSwitchingOrder;
/* End YUK-7993 */

/* Start YUK-8002 */
INSERT INTO YukonRoleProperty VALUES(-1604,-7,'Meter Lookup Field','Meter Number','Defines the field used to lookup a meter by in Yukon. Valid values: Meter Number, Device Name, or Address.');
/* End YUK-8002 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
