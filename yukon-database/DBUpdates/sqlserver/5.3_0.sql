/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9319 */
ALTER TABLE YukonServices 
DROP COLUMN ParamNames; 

ALTER TABLE YukonServices 
DROP COLUMN ParamValues;
/* End YUK-9319 */

/* Start YUK-9390 */
DELETE FROM ECToGenericMapping 
WHERE MappingCategory = 'InterviewQuestion'; 

ALTER TABLE InterviewQuestion
DROP CONSTRAINT FK_IntQ_CsLsEn;

ALTER TABLE InterviewQuestion
DROP CONSTRAINT FK_IntQ_CsLsEn2;

DELETE FROM YukonListEntry 
WHERE EntryId IN (SELECT AnswerType 
                  FROM InterviewQuestion)
AND EntryId != 0;
DELETE FROM YukonListEntry 
WHERE EntryId IN (SELECT QuestionType 
                  FROM InterviewQuestion)
AND EntryId != 0;
DELETE FROM YukonListEntry 
WHERE EntryId IN (SELECT ExpectedAnswer 
                  FROM InterviewQuestion)
AND EntryId != 0;

DROP TABLE InterviewQuestion;
/* End YUK-9390 */

/* Start YUK-9504 */
/* Migrated energy company substation information to new ECToSubstationMapping Table */
CREATE TABLE ECToSubstationMapping  (
   EnergyCompanyId      NUMERIC                          NOT NULL,
   SubstationId         NUMERIC                          NOT NULL,
   CONSTRAINT PK_ECToSubMap PRIMARY KEY (EnergyCompanyId, SubstationId)
);

INSERT INTO ECToSubstationMapping
SELECT EnergyCompanyId, ItemId
FROM ECToGenericMapping
WHERE MappingCategory = 'Substation';

DELETE FROM ECToGenericMapping
WHERE MappingCategory = 'Substation';

ALTER TABLE ECToSubstationMapping
    ADD CONSTRAINT FK_ECToSubMap_Sub FOREIGN KEY (SubstationId)
        REFERENCES Substation (SubstationId)
            ON DELETE CASCADE;

ALTER TABLE ECToSubstationMapping
    ADD CONSTRAINT FK_ECToSubMap_EC FOREIGN KEY (EnergyCompanyId)
        REFERENCES EnergyCompany (EnergyCompanyId)
            ON DELETE CASCADE;

/* Migrated energy company route information to new ECToRouteMapping Table */
CREATE TABLE ECToRouteMapping  (
   EnergyCompanyId      NUMERIC                          NOT NULL,
   RouteId              NUMERIC                          NOT NULL,
   CONSTRAINT PK_ECToRouteMap PRIMARY KEY (EnergyCompanyId, RouteId)
);

INSERT INTO ECToRouteMapping
SELECT EnergyCompanyId, ItemId
FROM ECToGenericMapping
WHERE MappingCategory = 'Route';

DELETE FROM ECToGenericMapping
WHERE MappingCategory = 'Route';

ALTER TABLE ECToRouteMapping
    ADD CONSTRAINT FK_ECToRouteMap_Route FOREIGN KEY (RouteId)
        REFERENCES Route (RouteId)
            ON DELETE CASCADE;

ALTER TABLE ECToRouteMapping
    ADD CONSTRAINT FK_ECToRouteMap_EC FOREIGN KEY (EnergyCompanyId)
        REFERENCES EnergyCompany (EnergyCompanyId)
            ON DELETE CASCADE;

/* Removed lmRouteId from the substation table */
ALTER TABLE Substation
DROP CONSTRAINT FK_Sub_Rt;

ALTER TABLE Substation
DROP COLUMN LmRouteId;
/* End YUK-9504 */

/* Start YUK-9489 */
UPDATE YukonRoleProperty 
SET KeyName = 'Create/Delete Energy Company', 
    Description = 'Controls access to create and delete an energy company.' 
WHERE RolePropertyId = -20001;

DELETE FROM YukonUserRole 
WHERE RolePropertyId = -20002; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -20002; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -20002;
/* End YUK-9489 */

/* Start YUK-9436 */
INSERT INTO YukonRoleProperty
VALUES(-1119,-2,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment');

DELETE FROM YukonUserRole
WHERE RolePropertyId IN (-40052, -20154);
DELETE FROM YukonGroupRole
WHERE RolePropertyId IN (-40052, -20154);
DELETE FROM YukonRoleProperty
WHERE RolePropertyId IN (-40052, -20154);
/* End YUK-9436 */

/* Start YUK-9118 */
ALTER TABLE DeviceGroupMember
DROP CONSTRAINT FK_DevGrpMember_DeviceGroup;

ALTER TABLE DeviceGroupMember
    ADD CONSTRAINT FK_DevGrpMember_DeviceGroup FOREIGN KEY (DeviceGroupId)
        REFERENCES DeviceGroup (DeviceGroupId)
            ON DELETE CASCADE;
/* End YUK-9118 */

/* Start YUK-9493 */
UPDATE YukonRoleProperty
SET KeyName = 'Edit Energy Company', 
    Description = 'Controls access to edit the user''s energy company settings.'
WHERE RolePropertyId = -20000;
/* End YUK-9493 */

/* Start YUK-9445 */
ALTER TABLE LmControlAreaTrigger 
ADD ThresholdPointId NUMERIC; 
GO

UPDATE LmControlAreaTrigger 
SET ThresholdPointId = 0; 
GO

ALTER TABLE LmControlAreaTrigger 
ALTER COLUMN ThresholdPointId NUMERIC NOT NULL; 
/* End YUK-9445 */

/* Start YUK-9404 */
DELETE FROM ECToGenericMapping 
WHERE MappingCategory = 'CustomerFAQ'; 

ALTER TABLE CustomerFAQ
DROP CONSTRAINT FK_CsLsEn_CsF;

DELETE FROM YukonListEntry 
WHERE ListId IN (SELECT ListId 
                  FROM YukonSelectionList 
                  WHERE ListName IN ('CustomerFAQGroup', 'QuestionType', 'AnswerType')); 

DELETE FROM YukonSelectionList 
WHERE ListName IN ('CustomerFAQGroup', 'QuestionType', 'AnswerType'); 

DELETE FROM YukonListEntry 
WHERE EntryId IN (SELECT SubjectId 
                   FROM CustomerFAQ) 
AND EntryId != 0; 

DROP TABLE CustomerFAQ; 
/* End YUK-9404 */

/* Start YUK-9015 */
DELETE FROM YukonServices
WHERE ABS(ServiceId) = 4;
/* End YUK-9015 */

/* Start YUK-9482 */
INSERT INTO YukonRoleProperty
VALUES(-20019,-200,'Admin Super User','false','Allows full control of all energy companies and other administrator features.');

INSERT INTO YukonUserRole
VALUES(-1020, -100, -200, -20019, 'true');
/* End YUK-9482 */

/* Start YUK-9461 */
CREATE TABLE PorterResponseMonitor (
   MonitorId            NUMERIC              NOT NULL,
   Name                 VARCHAR(255)         NOT NULL,
   GroupName            VARCHAR(255)         NOT NULL,
   StateGroupId         NUMERIC              NOT NULL,
   Attribute            VARCHAR(255)         NOT NULL,
   EvaluatorStatus      VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_PortRespMonId PRIMARY KEY (MonitorId)
);
GO

CREATE UNIQUE INDEX Indx_PortRespMon_Name_UNQ ON PorterResponseMonitor (
   Name ASC
);

CREATE TABLE PorterResponseMonitorRule (
   RuleId               NUMERIC              NOT NULL,
   RuleOrder            NUMERIC              NOT NULL,
   MonitorId            NUMERIC              NOT NULL,
   Success              CHAR(1)              NOT NULL,
   MatchStyle           VARCHAR(40)          NOT NULL,
   State                VARCHAR(40)          NOT NULL,
   CONSTRAINT PK_PortRespMonRuleId PRIMARY KEY (RuleId)
);
GO

CREATE UNIQUE INDEX Indx_PortRespMonRule_RO_MI_UNQ ON PorterResponseMonitorRule (
   RuleOrder ASC,
   MonitorId ASC
);

CREATE TABLE PorterResponseMonitorErrorCode (
   ErrorCodeId          NUMERIC              NOT NULL,
   RuleId               NUMERIC              NOT NULL,
   ErrorCode            NUMERIC              NOT NULL,
   CONSTRAINT PK_PortRespMonErrorCodeId PRIMARY KEY (ErrorCodeId)
);
GO

CREATE UNIQUE INDEX Indx_PortRespMonErr_RI_EC_UNQ ON PorterResponseMonitorErrorCode (
   RuleId ASC,
   ErrorCode ASC
);

ALTER TABLE PorterResponseMonitor
    ADD CONSTRAINT FK_PortRespMon_StateGroup FOREIGN KEY (StateGroupId)
        REFERENCES StateGroup (StateGroupId);
GO

ALTER TABLE PorterResponseMonitorRule
    ADD CONSTRAINT FK_PortRespMonRule_PortRespMon FOREIGN KEY (MonitorId)
        REFERENCES PorterResponseMonitor (MonitorId)
            ON DELETE CASCADE;
GO

ALTER TABLE PorterResponseMonitorErrorCode
    ADD CONSTRAINT FK_PortRespMonErr_PortRespMonR FOREIGN KEY (RuleId)
        REFERENCES PorterResponseMonitorRule (RuleId)
            ON DELETE CASCADE;
GO

INSERT INTO YukonRoleProperty VALUES (-20218,-202,'Porter Response Monitor','false','Controls access to the Porter Response Monitor');

INSERT INTO YukonServices VALUES (-15, 'PorterResponseMonitor', 'classpath:com/cannontech/services/porterResponseMonitor/porterResponseMonitorContext.xml', 'ServiceManager');
/* End YUK-9461 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
