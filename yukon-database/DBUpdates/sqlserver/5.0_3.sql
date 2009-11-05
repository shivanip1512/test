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
CREATE TABLE DynamicCCOriginalParent (
   PAObjectId           NUMERIC              NOT NULL,
   OriginalParentId     NUMERIC              NOT NULL,
   OriginalSwitchingOrder FLOAT                NOT NULL,
   OriginalCloseOrder   FLOAT                NOT NULL,
   OriginalTripOrder    FLOAT                NOT NULL,
   CONSTRAINT PK_DynCCOrigParent PRIMARY KEY (PAObjectId)
);
GO

ALTER TABLE DynamicCCOriginalParent
   ADD CONSTRAINT FK_DynCCOrigParent_YukonPAO FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId);
GO

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

/* Start YUK-7995 */
INSERT INTO YukonRoleProperty VALUES(-20216,-202,'Validation Engine','true','Controls access to Validation Processing');

CREATE TABLE ValidationMonitor (
   ValidationMonitorId  NUMERIC              NOT NULL,
   ValidationMonitorName VARCHAR(255)         NOT NULL,
   GroupName            VARCHAR(255)         NOT NULL,
   Threshold            FLOAT                NOT NULL,
   ReRead               NUMERIC              NOT NULL,
   SlopeError           FLOAT                NOT NULL,
   ReadingError         FLOAT                NOT NULL,
   PeakHeightMinimum    FLOAT                NOT NULL,
   QuestionableQuality  NUMERIC              NOT NULL,
   EvaluatorStatus      VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_ValidMon PRIMARY KEY (ValidationMonitorId)
);
GO
/* End YUK-7995 */

/* Start YUK-7994 */
DELETE FROM YukonServices WHERE ServiceId = 7;

INSERT INTO YukonServices VALUES (11, 'RawPointHistoryValidation', 'classpath:com/cannontech/services/validation/validationServerContext.xml', '(none)', '(none)', 'ServiceManager');
/* End YUK-7994 */

/* Start YUK-8003 */
CREATE TABLE PersistedSystemValue (
   Name                 VARCHAR(50)          NOT NULL,
   Value                TEXT                 NOT NULL,
   CONSTRAINT PK_PerSysValue PRIMARY KEY (Name)
);
GO

CREATE TABLE RPHTag (
   ChangeId             NUMERIC              NOT NULL,
   TagName              VARCHAR(150)         NOT NULL,
   CONSTRAINT PK_RPHTag PRIMARY KEY (ChangeId, TagName)
);
GO

ALTER TABLE RPHTag
    ADD CONSTRAINT FK_RPHTag_RPH FOREIGN KEY (ChangeId)
        REFERENCES RawPointHistory (ChangeId)
            ON DELETE CASCADE;
/* End YUK-8003 */

/* Start YUK-8009 */
INSERT INTO PersistedSystemValue VALUES ('VALIDATION_ENGINE_LAST_CHANGE_ID', (SELECT CAST(MAX(changeid) AS CHAR) FROM RawPointHistory));
INSERT INTO ValidationMonitor VALUES (1, 'Default All Meters', '/Meters', 400, 1, 4, .1, 15, 1, 'ENABLED');
/* End YUK-8009 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
