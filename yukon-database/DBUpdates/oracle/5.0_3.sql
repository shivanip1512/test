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
   PAObjectId           NUMBER                          NOT NULL,
   OriginalParentId     NUMBER                          NOT NULL,
   OriginalSwitchingOrder FLOAT                           NOT NULL,
   OriginalCloseOrder   FLOAT                           NOT NULL,
   OriginalTripOrder    FLOAT                           NOT NULL,
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
SET OriginalParentId = (SELECT DCCCB.OriginalFeederId FROM DynamicCCCapBank DCCCB WHERE DCCCB.CAPBANKID = PAObjectId), 
    OriginalSwitchingOrder = (SELECT DCCCB.OriginalSwitchingOrder FROM DynamicCCCapBank DCCCB WHERE DCCCB.CAPBANKID = PAObjectId)
WHERE PAObjectId IN (SELECT CapBankId FROM DynamicCCCapBank);

CREATE OR REPLACE VIEW TempMovedCapBanks_View AS
SELECT YPF.PAOName TempFeederName, YPF.PAObjectId TempFeederId, YPC.PAOName CapBankName, 
       YPC.PAObjectId CapBankId, FB.ControlOrder, FB.CloseOrder, FB.TripOrder, 
       YPOF.PAOName OriginalFeederName, YPOF.PAObjectId OriginalFeederId 
FROM CCFeederBankList FB, YukonPAObject YPF, YukonPAObject YPC, 
     YukonPAObject YPOF, DynamicCCOriginalParent DCCOP
WHERE FB.DeviceId = YPC.PAObjectId
AND YPC.PAObjectID = DCCOP.PAObjectId
AND FB.FeederId = YPF.PAObjectId 
AND YPOF.PAObjectId = DCCOP.OriginalParentId 
AND DCCOP.OriginalParentId <> 0;    
    
ALTER TABLE DynamicCCCapBank DROP COLUMN OriginalFeederId;
ALTER TABLE DynamicCCCapBank DROP COLUMN OriginalSwitchingOrder;
/* End YUK-7993 */

/* Start YUK-8002 */
INSERT INTO YukonRoleProperty VALUES(-1604,-7,'Meter Lookup Field','METER_NUMBER','Defines the field used to lookup a meter by in Yukon. Valid values: METER_NUMBER, DEVICE_NAME, or ADDRESS.');
/* End YUK-8002 */

/* Start YUK-7995 */
INSERT INTO YukonRoleProperty VALUES(-20216,-202,'Validation Engine','true','Controls access to Validation Processing');

CREATE TABLE ValidationMonitor  (
   ValidationMonitorId  NUMBER                          NOT NULL,
   ValidationMonitorName VARCHAR2(255)                   NOT NULL,
   GroupName            VARCHAR2(255)                   NOT NULL,
   Threshold            FLOAT                           NOT NULL,
   ReRead               NUMBER                          NOT NULL,
   SlopeError           FLOAT                           NOT NULL,
   ReadingError         FLOAT                           NOT NULL,
   PeakHeightMinimum    FLOAT                           NOT NULL,
   QuestionableQuality  NUMBER                          NOT NULL,
   EvaluatorStatus      VARCHAR2(255)                   NOT NULL,
   CONSTRAINT PK_ValidMon PRIMARY KEY (ValidationMonitorId)
);
/* End YUK-7995 */

/* Start YUK-7994 */
DELETE FROM YukonServices WHERE ServiceId = 7;

INSERT INTO YukonServices VALUES (11, 'RawPointHistoryValidation', 'classpath:com/cannontech/services/validation/validationServerContext.xml', '(none)', '(none)', 'ServiceManager');
/* End YUK-7994 */

/* Start YUK-8003 */
CREATE TABLE PersistedSystemValue  (
   Name                 VARCHAR2(50)                    NOT NULL,
   Value                CLOB                            NOT NULL,
   CONSTRAINT PK_PerSysValue PRIMARY KEY (Name)
);

CREATE TABLE RPHTag  (
   ChangeId             NUMBER                          NOT NULL,
   TagName              VARCHAR2(150)                   NOT NULL,
   CONSTRAINT PK_RPHTag PRIMARY KEY (ChangeId, TagName)
);

ALTER TABLE RPHTag
    ADD CONSTRAINT FK_RPHTag_RPH FOREIGN KEY (ChangeId)
        REFERENCES RawPointHistory (ChangeId)
            ON DELETE CASCADE;
/* End YUK-8003 */

/* Start YUK-8009 */
/* @error ignore-begin */
INSERT INTO PersistedSystemValue VALUES ('VALIDATION_ENGINE_LAST_CHANGE_ID', (SELECT MAX(ChangeId) FROM RawPointHistory));
/* @error ignore-end */
INSERT INTO ValidationMonitor VALUES (1, 'Default All Meters', '/Meters', 400, 1, 4, .1, 15, 1, 'ENABLED');
/* End YUK-8009 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.0', 'Matt K', '09-NOV-2009', 'Latest Update', 3);
