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

/* Start YUK-9455 */
UPDATE EventLog 
SET EventType = 'stars.account.optOut.optOutCancelAttemptedByOperator'
WHERE EventType = 'stars.account.optOut.optOutCancelAtteptedByOperator';

UPDATE EventLog 
SET EventType = 'stars.account.optOut.optOutCancelAttemptedByConsumer'
WHERE EventType = 'stars.account.optOut.optOutCancelAtteptedByConsumer'; 

UPDATE EventLog
SET EventType = 'stars.' + EventType
WHERE EventType LIKE 'account.account%'
OR EventType LIKE 'account.enrollment%'
OR EventType LIKE 'account.optOut%'
OR EventType LIKE 'account.appliance%'
OR EventType LIKE 'account.contactInfo%'
OR EventType LIKE 'account.thermostat%';
/* End YUK-9455 */

/* Start YUK-9448 */
DROP TABLE GatewayEndDevice; 

DELETE FROM YukonListEntry
WHERE ListId = 1050; 

DELETE FROM YukonSelectionList
WHERE ListId = 1050; 

DELETE FROM ECToGenericMapping
WHERE ItemId = 1050 
AND MappingCategory = 'YukonSelectionList';
/* End YUK-9448 */

/* Start YUK-9429 */
/* @start-block */
CREATE TABLE ECToOperatorGroupMapping ( 
   EnergyCompanyId NUMERIC NOT NULL, 
   GroupId NUMERIC NOT NULL, 
   CONSTRAINT PK_ECToOpGroupMap PRIMARY KEY (EnergyCompanyId, GroupId) 
);
GO

ALTER TABLE ECToOperatorGroupMapping 
    ADD CONSTRAINT FK_ECToOpGroupMap_YukonGroup FOREIGN KEY (GroupId) 
        REFERENCES YukonGroup (GroupId) 
            ON DELETE CASCADE; 

ALTER TABLE ECToOperatorGroupMapping 
    ADD CONSTRAINT FK_ECToOpGroupMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
            ON DELETE CASCADE; 

CREATE TABLE ECToResidentialGroupMapping ( 
   EnergyCompanyId NUMERIC NOT NULL, 
   GroupId NUMERIC NOT NULL, 
   CONSTRAINT PK_ECToResGroupMap PRIMARY KEY (EnergyCompanyId, GroupId) 
); 
GO

ALTER TABLE ECToResidentialGroupMapping 
    ADD CONSTRAINT FK_ECToResGroupMap_YukonGroup FOREIGN KEY (GroupId) 
        REFERENCES YukonGroup (GroupId) 
            ON DELETE CASCADE; 

ALTER TABLE ECToResidentialGroupMapping 
    ADD CONSTRAINT FK_ECToResGroupMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
            ON DELETE CASCADE;
GO
/* @end-block */

/* @start-block */
CREATE FUNCTION dbo.fx_Split
(
    @RowData nvarchar(2000),
    @SplitOn nvarchar(5)
)  
RETURNS @RtnValue table 
(
    Id int identity(1,1),
    Data nvarchar(100)
) 
AS  
BEGIN 
    Declare @Cnt int
    Set @Cnt = 1

    While (Charindex(@SplitOn,@RowData)>0)
    Begin
        Insert Into @RtnValue (data)
        Select 
            Data = ltrim(rtrim(Substring(@RowData,1,Charindex(@SplitOn,@RowData)-1)))

        Set @RowData = Substring(@RowData,Charindex(@SplitOn,@RowData)+1,len(@RowData))
        Set @Cnt = @Cnt + 1
    End
    
    Insert Into @RtnValue (data)
    Select Data = ltrim(rtrim(@RowData))

    Return
END
GO
/* @end-block */

/* @start-block */
DECLARE @EnergyCompanyName VARCHAR(60);
DECLARE @EnergyCompanyId NUMERIC;
DECLARE @GroupRoleId NUMERIC;
DECLARE @GroupId NUMERIC;
DECLARE @RoleId NUMERIC;
DECLARE @RolePropertyId NUMERIC;
DECLARE @RolePropertyValue VARCHAR(1000);

/* ECToGroupMapper insert value holders */
DECLARE @id int;
DECLARE @yukonGroupId nvarchar(MAX);

/* Gets the information needed to migrate the operator groups role property to the new ECToOperatorGroupMapping table. */
DECLARE ecToOpLoginGroup_curs CURSOR FOR ( 
        SELECT EC.Name, EC.EnergyCompanyId, YGR.GroupRoleId, YGR.GroupId, YGR.RoleId, YGR.RolePropertyId, YGR.Value
        FROM EnergyCompany EC
        JOIN YukonUserGroup YUG ON YUG.UserId = EC.UserId
        JOIN YukonGroupRole YGR ON YGR.GroupId = YUG.GroupId
        WHERE YGR.RolePropertyId = -1106);

/* Gets the information needed to migrate the residential groups role property to the new ECToResidentialGroupMapping table. */
DECLARE ecToResLoginGroup_curs CURSOR FOR ( 
        SELECT EC.Name, EC.EnergyCompanyId, YGR.GroupRoleId, YGR.GroupId, YGR.RoleId, YGR.RolePropertyId, YGR.Value
        FROM EnergyCompany EC
        JOIN YukonUserGroup YUG ON YUG.UserId = EC.UserId
        JOIN YukonGroupRole YGR ON YGR.GroupId = YUG.GroupId
        WHERE YGR.RolePropertyId = -1105);
    
OPEN ecToOpLoginGroup_curs;
FETCH ecToOpLoginGroup_curs INTO @EnergyCompanyName, @EnergyCompanyId, @GroupRoleId, @GroupId, @RoleId, @RolePropertyId, @RolePropertyValue;
    BEGIN
    
        /* Getting the operator groups from the role property value */
        DECLARE ecOperatorRolePropertyMapping_curs CURSOR FOR ( 
            SELECT * FROM dbo.fx_Split(@RolePropertyValue, ',')
        );
        
        OPEN ecOperatorRolePropertyMapping_curs;
        FETCH ecOperatorRolePropertyMapping_curs INTO @id, @yukonGroupId;
            BEGIN
            
                /* Make sure there is a group, if there is add an entry to the ECToOperatorGroupMapping table */ 
                IF (@yukonGroupId != '')
                BEGIN 
                    INSERT INTO ECToOperatorGroupMapping(energyCompanyId, groupId)
                    VALUES ( @EnergyCompanyId, @yukonGroupId );
                END
            END
        CLOSE ecOperatorRolePropertyMapping_curs;
        DEALLOCATE ecOperatorRolePropertyMapping_curs;
        
    END
CLOSE ecToOpLoginGroup_curs;
DEALLOCATE ecToOpLoginGroup_curs;

OPEN ecToResLoginGroup_curs;
FETCH ecToResLoginGroup_curs INTO @EnergyCompanyName, @EnergyCompanyId, @GroupRoleId, @GroupId, @RoleId, @RolePropertyId, @RolePropertyValue;
    BEGIN

        /* Getting the operator groups from the role property value */
        DECLARE ecOperatorRolePropertyMapping_curs CURSOR FOR ( 
            SELECT * FROM dbo.fx_Split(@RolePropertyValue, ',')
        );
        
        OPEN ecOperatorRolePropertyMapping_curs;
        FETCH ecOperatorRolePropertyMapping_curs INTO @id, @yukonGroupId;
            BEGIN
            
                /* Make sure there is a group, if there is add an entry to the ECToResidentialGroupMapping table */ 
                IF (@yukonGroupId != '')
                BEGIN 
                    INSERT INTO ECToResidentialGroupMapping(energyCompanyId, groupId)
                    VALUES ( @EnergyCompanyId, @yukonGroupId );
                END
            END
        CLOSE ecOperatorRolePropertyMapping_curs;
        DEALLOCATE ecOperatorRolePropertyMapping_curs;
    END
CLOSE ecToResLoginGroup_curs;
DEALLOCATE ecToResLoginGroup_curs;
GO
/* @end-block */

DELETE FROM YukonUserRole 
WHERE RolePropertyId = -1106; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -1106; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -1106; 

DELETE FROM YukonUserRole 
WHERE RolePropertyId = -1105; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -1105; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -1105;
/* End YUK-9429 */

/* Start YUK-9564 */
UPDATE YukonWebConfiguration
SET LogoLocation = 'yukon/Icons/AC.png'
WHERE LogoLocation = 'yukon/Icons/AC.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/DualFuel.png' 
WHERE LogoLocation = 'yukon/Icons/DualFuel.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/Electric.png' 
WHERE LogoLocation = 'yukon/Icons/Electric.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/Generation.png' 
WHERE LogoLocation = 'yukon/Icons/Generation.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/GrainDryer.png' 
WHERE LogoLocation = 'yukon/Icons/GrainDryer.gif'; 

UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/HeatPump.png' 
WHERE LogoLocation = 'yukon/Icons/HeatPump.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/HotTub.png' 
WHERE LogoLocation = 'yukon/Icons/HotTub.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/Irrigation.png' 
WHERE LogoLocation = 'yukon/Icons/Irrigation.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/Load.png' 
WHERE LogoLocation = 'yukon/Icons/Load.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/Pool.png' 
WHERE LogoLocation = 'yukon/Icons/Pool.gif'; 

UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/Setback.png' 
WHERE LogoLocation = 'yukon/Icons/Setback.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/StorageHeat.png' 
WHERE LogoLocation = 'yukon/Icons/StorageHeat.gif'; 
UPDATE YukonWebConfiguration 
SET LogoLocation = 'yukon/Icons/WaterHeater.png' 
WHERE LogoLocation = 'yukon/Icons/WaterHeater.gif'; 
/* End YUK-9564 */

/* Start YUK-9563 */
CREATE TABLE DigiGateway  (
   DeviceId             NUMERIC                          NOT NULL,
   DigiId               NUMERIC                          NOT NULL,
   CONSTRAINT PK_DigiGate PRIMARY KEY (DeviceId)
);

CREATE TABLE ZBDevice  (
   DeviceId             NUMERIC                          NOT NULL,
   InstallCode          VARCHAR(255)                   NOT NULL,
   CONSTRAINT PK_ZBDevice PRIMARY KEY (DeviceId)
);

CREATE TABLE ZBGateway  (
   DeviceId             NUMERIC                          NOT NULL,
   FirmwareVersion      VARCHAR(255)                   NOT NULL,
   MacAddress           VARCHAR(255)                   NOT NULL,
   CONSTRAINT PK_ZBGateway PRIMARY KEY (DeviceId)
);

CREATE TABLE ZBGatewayToDeviceMapping  (
   GatewayId            NUMERIC                          NOT NULL,
   DeviceId             NUMERIC                          NOT NULL,
   CONSTRAINT PK_ZBGateToDeviceMap PRIMARY KEY (GatewayId, DeviceId)
);
GO

ALTER TABLE DigiGateway
    ADD CONSTRAINT FK_DigiGate_ZBGate FOREIGN KEY (DeviceId)
        REFERENCES ZBGateway (DeviceId)
            ON DELETE CASCADE;

ALTER TABLE ZBDevice
    ADD CONSTRAINT FK_ZBDevice_Device FOREIGN KEY (DeviceId)
        REFERENCES DEVICE (DeviceId);

ALTER TABLE ZBGateway
    ADD CONSTRAINT FK_ZBGate_Device FOREIGN KEY (DeviceId)
        REFERENCES DEVICE (DeviceId);

ALTER TABLE ZBGatewayToDeviceMapping
    ADD CONSTRAINT FK_ZBGateDeviceMap_ZBDevice FOREIGN KEY (DeviceId)
        REFERENCES ZBDevice (DeviceId)
            ON DELETE CASCADE;

ALTER TABLE ZBGatewayToDeviceMapping
    ADD CONSTRAINT FK_ZBGateDeviceMap_ZBGate FOREIGN KEY (GatewayId)
        REFERENCES ZBGateway (DeviceId)
            ON DELETE CASCADE;
GO

INSERT INTO StateGroup VALUES(-13, 'Commissioned State','Status'); 
INSERT INTO State VALUES(-13, 0, 'Commissioned', 0, 6, 0);
INSERT INTO State VALUES(-13, 1, 'Uncommissioned', 1, 6, 0); 

INSERT INTO YukonListEntry VALUES (1067,1005,0,'UtilityPRO Zigbee',1316);
INSERT INTO YukonListEntry VALUES (1068,1005,0,'Digi Gateway',1317);

UPDATE YukonListEntry
SET EntryOrder = 0
WHERE ListId = 1005;
/* End YUK-9563 */

/* Start YUK-9565 */
INSERT INTO StateGroup VALUES(-14, 'Outage Status','Status');

INSERT INTO State VALUES(-14, 0, 'Good', 0, 6, 0);
INSERT INTO State VALUES(-14, 1, 'Questionable', 1, 6, 0);
INSERT INTO State VALUES(-14, 2, 'Bad', 2, 6, 0);
/* End YUK-9565 */

/* Start YUK-9562 */
INSERT INTO StateGroup VALUES(-15, 'Signal Strength', 'Status'); 

INSERT INTO State VALUES(-15, 0, 'No Signal', 0, 6, 0); 
INSERT INTO State VALUES(-15, 1, 'Very Poor', 1, 6, 0); 
INSERT INTO State VALUES(-15, 2, 'Ok', 10, 6, 0); 
INSERT INTO State VALUES(-15, 3, 'Good', 3, 6, 0); 
INSERT INTO State VALUES(-15, 4, 'Best', 4, 6, 0);
/* End YUK-9562 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
