/******************************************/ 
/**** Oracle DBupdates                 ****/ 
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

DROP TABLE InterviewQuestion CASCADE CONSTRAINTS;
/* End YUK-9390 */

/* Start YUK-9504 */
/* Migrated energy company substation information to new ECToSubstationMapping Table */ 
CREATE TABLE ECToSubstationMapping ( 
   EnergyCompanyId      NUMBER          NOT NULL, 
   SubstationId         NUMBER          NOT NULL, 
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
CREATE TABLE ECToRouteMapping ( 
   EnergyCompanyId      NUMBER          NOT NULL, 
   RouteId              NUMBER          NOT NULL, 
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
/* @error warn-once */
/* @start-block */
DECLARE
    errorFlagCount INT;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount
    FROM YukonGroupRole YGR, YukonUserRole YUR
    WHERE (YGR.RolePropertyId IN (-20154, -40052)
           AND YGR.Value = 'true')
      OR (YUR.RolePropertyId IN (-20154, -40052)
          AND YUR.Value = 'true');
          
    IF 0 < errorFlagCount THEN
        RAISE_APPLICATION_ERROR(-20001, 'The database contains Automatic Configuration role properties that are about to be reset to the default value. This will change the current value from true to false. Please record uses of Residential > Automatic Configuration and Consumer Info > Automatic Configuration before continuing. See YUK-9436 for more information.');
    END IF;
END;
/
/* @end-block */

INSERT INTO YukonRoleProperty
VALUES(-1119,-2,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment.');

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
ADD ThresholdPointId NUMBER; 

UPDATE LmControlAreaTrigger 
SET ThresholdPointId = 0; 

ALTER TABLE LmControlAreaTrigger 
MODIFY ThresholdPointId NUMBER NOT NULL; 
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
   MonitorId            NUMBER              NOT NULL,
   Name                 VARCHAR2(255)         NOT NULL,
   GroupName            VARCHAR2(255)         NOT NULL,
   StateGroupId         NUMBER              NOT NULL,
   Attribute            VARCHAR2(255)         NOT NULL,
   EvaluatorStatus      VARCHAR2(255)         NOT NULL,
   CONSTRAINT PK_PortRespMonId PRIMARY KEY (MonitorId)
);

CREATE UNIQUE INDEX Indx_PortRespMon_Name_UNQ ON PorterResponseMonitor (
   Name ASC
); 

CREATE TABLE PorterResponseMonitorRule (
   RuleId               NUMBER              NOT NULL,
   RuleOrder            NUMBER              NOT NULL,
   MonitorId            NUMBER              NOT NULL,
   Success              CHAR(1)              NOT NULL,
   MatchStyle           VARCHAR2(40)          NOT NULL,
   State                VARCHAR2(40)          NOT NULL,
   CONSTRAINT PK_PortRespMonRuleId PRIMARY KEY (RuleId)
);


CREATE UNIQUE INDEX Indx_PortRespMonRule_RO_MI_UNQ ON PorterResponseMonitorRule (
   RuleOrder ASC,
   MonitorId ASC
); 

CREATE TABLE PorterResponseMonitorErrorCode (
   ErrorCodeId          NUMBER              NOT NULL,
   RuleId               NUMBER              NOT NULL,
   ErrorCode            NUMBER              NOT NULL,
   CONSTRAINT PK_PortRespMonErrorCodeId PRIMARY KEY (ErrorCodeId)
);

CREATE UNIQUE INDEX Indx_PortRespMonErr_RI_EC_UNQ ON PorterResponseMonitorErrorCode (
   RuleId ASC,
   ErrorCode ASC
); 

ALTER TABLE PorterResponseMonitor
    ADD CONSTRAINT FK_PortRespMon_StateGroup FOREIGN KEY (StateGroupId)
        REFERENCES StateGroup (StateGroupId);

ALTER TABLE PorterResponseMonitorRule
    ADD CONSTRAINT FK_PortRespMonRule_PortRespMon FOREIGN KEY (MonitorId)
        REFERENCES PorterResponseMonitor (MonitorId)
            ON DELETE CASCADE;

ALTER TABLE PorterResponseMonitorErrorCode
    ADD CONSTRAINT FK_PortRespMonErr_PortRespMonR FOREIGN KEY (RuleId)
        REFERENCES PorterResponseMonitorRule (RuleId)
            ON DELETE CASCADE;

INSERT INTO YukonRoleProperty VALUES (-20218,-202,'Porter Response Monitor','false','Controls access to the Porter Response Monitor');

INSERT INTO YukonServices VALUES (15, 'PorterResponseMonitor', 'classpath:com/cannontech/services/porterResponseMonitor/porterResponseMonitorContext.xml', 'ServiceManager');
/* End YUK-9461 */

/* Start YUK-9455 */
UPDATE EventLog 
SET EventType = 'stars.account.optOut.optOutCancelAttemptedByOperator'
WHERE EventType = 'stars.account.optOut.optOutCancelAtteptedByOperator';

UPDATE EventLog 
SET EventType = 'stars.account.optOut.optOutCancelAttemptedByConsumer'
WHERE EventType = 'stars.account.optOut.optOutCancelAtteptedByConsumer'; 

UPDATE EventLog
SET EventType = CONCAT('stars.', EventType)
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
CREATE TABLE ECToOperatorGroupMapping ( 
   EnergyCompanyId NUMBER NOT NULL, 
   GroupId NUMBER NOT NULL, 
   CONSTRAINT PK_ECToOpGroupMap PRIMARY KEY (EnergyCompanyId, GroupId) 
); 

ALTER TABLE ECToOperatorGroupMapping 
    ADD CONSTRAINT FK_ECToOpGroupMap_YukonGroup FOREIGN KEY (GroupId) 
        REFERENCES YukonGroup (GroupId) 
            ON DELETE CASCADE; 

ALTER TABLE ECToOperatorGroupMapping 
    ADD CONSTRAINT FK_ECToOpGroupMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
            ON DELETE CASCADE; 

CREATE TABLE ECToResidentialGroupMapping ( 
   EnergyCompanyId NUMBER NOT NULL, 
   GroupId NUMBER NOT NULL, 
   CONSTRAINT PK_ECToResGroupMap PRIMARY KEY (EnergyCompanyId, GroupId) 
); 

ALTER TABLE ECToResidentialGroupMapping 
    ADD CONSTRAINT FK_ECToResGroupMap_YukonGroup FOREIGN KEY (GroupId) 
        REFERENCES YukonGroup (GroupId) 
            ON DELETE CASCADE; 

ALTER TABLE ECToResidentialGroupMapping 
    ADD CONSTRAINT FK_ECToResGroupMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
            ON DELETE CASCADE; 

/* @start-block */
CREATE OR REPLACE PACKAGE STRING_FNC 
IS 

TYPE t_array IS TABLE OF VARCHAR2(50) 
   INDEX BY BINARY_INTEGER; 

FUNCTION SPLIT (p_in_string VARCHAR2, p_delim VARCHAR2) RETURN t_array; 

END; 
/
/* @end-block */

/* @start-block */
CREATE OR REPLACE PACKAGE BODY STRING_FNC 
IS 

   FUNCTION SPLIT (p_in_string VARCHAR2, p_delim VARCHAR2) RETURN t_array  
   IS 
    
      i       NUMBER :=0; 
      pos     NUMBER :=0; 
      lv_str  VARCHAR2(1000) := p_in_string; 
       
   strings t_array; 
    
   BEGIN 
    
      /* determine first chuck of string */   
      pos := INSTR(lv_str,p_delim, 1, 1); 

      /* add it to the array if the value existed but did not have the delimitor. */  
      IF pos = 0 AND lv_str != ' ' THEN 
          strings(i+1) := lv_str; 
      END IF;
   
      /* while there are chunks left, loop  */
      WHILE ( pos != 0) LOOP 
          
         /* increment counter  */
         i := i + 1; 
          
         /* create array element for chuck of string  */
         strings(i) := SUBSTR(lv_str,1,pos-1); 
          
         /* remove chunk from string  */
         lv_str := SUBSTR(lv_str,pos+1, LENGTH(lv_str)); 
          
         /* determine next chunk  */
         pos := INSTR(lv_str,p_delim,1,1); 
          
         /* no last chunk, add to array  */
         IF pos = 0 THEN 
            strings(i+1) := lv_str; 
         END IF; 
       
      END LOOP; 
    
      /* return array  */
      RETURN strings; 
       
   END SPLIT; 

END;
/
/* @end-block */

/* @start-block */
DECLARE 
    /* New Appliance Base Values */
    v_EnergyCompanyName VARCHAR2(60);
    v_EnergyCompanyId NUMBER;
    v_GroupRoleId NUMBER;
    v_GroupId NUMBER;
    v_RoleId NUMBER;
    v_RolePropertyId NUMBER;
    v_RolePropertyValue VARCHAR2(1000);

    /* Account Enrollment information Cursor */
    CURSOR ecToOpLoginGroup_curs IS 
        SELECT EC.Name, EC.EnergyCompanyId, YGR.GroupRoleId, YGR.GroupId, YGR.RoleId, YGR.RolePropertyId, YGR.Value
        FROM EnergyCompany EC
        JOIN YukonUserGroup YUG ON YUG.UserId = EC.UserId
        JOIN YukonGroupRole YGR ON YGR.GroupId = YUG.GroupId
        WHERE YGR.RolePropertyId = -1106
        AND YGR.Value != '(none)';

    /* Inventory Enrollment information based on the account enrollment Cursor */
    CURSOR ecToResLoginGroup_curs IS 
        SELECT EC.Name, EC.EnergyCompanyId, YGR.GroupRoleId, YGR.GroupId, YGR.RoleId, YGR.RolePropertyId, YGR.Value
        FROM EnergyCompany EC
        JOIN YukonUserGroup YUG ON YUG.UserId = EC.UserId
        JOIN YukonGroupRole YGR ON YGR.GroupId = YUG.GroupId
        WHERE YGR.RolePropertyId = -1105
        AND YGR.Value != '(none)';
    
    str string_fnc.t_array;

BEGIN

    OPEN ecToOpLoginGroup_curs;
    LOOP
        FETCH ecToOpLoginGroup_curs INTO v_EnergyCompanyName, v_EnergyCompanyId, v_GroupRoleId, v_GroupId, v_RoleId, v_RolePropertyId, v_RolePropertyValue;
        EXIT WHEN ecToOpLoginGroup_curs%NOTFOUND;
        
        /* Turn on logging, reset the original appliance id and inventory count. */
        dbms_output.enable(1000000);

        dbms_output.put(CONCAT('[ECName = ',v_EnergyCompanyName));
        dbms_output.put(CONCAT(', ECId = ',v_EnergyCompanyId));
        dbms_output.put(CONCAT(', GroupRoleId = ',v_GroupRoleId));
        dbms_output.put(CONCAT(', GroupId = ',v_GroupId));
        dbms_output.put(CONCAT(', RoleId = ',v_RoleId));
        dbms_output.put(CONCAT(', RolePropertyId = ',v_RolePropertyId));
        dbms_output.put(CONCAT(', RolePropertyValue = (',v_RolePropertyValue));
        dbms_output.put_line(')]');

        str := string_fnc.split(v_RolePropertyValue,',');

        /* Add the entries to the ECToOperatorGroupMapping table. */
        FOR i IN 1..str.count LOOP
            dbms_output.put(CONCAT('inserting ecToOp(ECId = ',v_EnergycompanyId));
            dbms_output.put(CONCAT(', RolePropertyValue = ',str(i)));
            dbms_output.put_line(')');
    
            INSERT INTO ECToOperatorGroupMapping(EnergyCompanyId, GroupId) VALUES (v_EnergyCompanyId, str(i));
    
        END LOOP;


    END LOOP;
    CLOSE ecToOpLoginGroup_curs;

    OPEN ecToResLoginGroup_curs;
    LOOP
        FETCH ecToResLoginGroup_curs INTO v_EnergyCompanyName, v_EnergyCompanyId, v_GroupRoleId, v_GroupId, v_RoleId, v_RolePropertyId, v_RolePropertyValue;
        EXIT WHEN ecToResLoginGroup_curs%NOTFOUND;
        
        /* Turn on logging, reset the original appliance id and inventory count. */
        dbms_output.enable(1000000);

        dbms_output.put(CONCAT('[ECName = ',v_EnergyCompanyName));
        dbms_output.put(CONCAT(', ECId = ',v_EnergyCompanyId));
        dbms_output.put(CONCAT(', GroupRoleId = ',v_GroupRoleId));
        dbms_output.put(CONCAT(', GroupId = ',v_GroupId));
        dbms_output.put(CONCAT(', RoleId = ',v_RoleId));
        dbms_output.put(CONCAT(', RolePropertyId = ',v_RolePropertyId));
        dbms_output.put(CONCAT(', RolePropertyValue = (',v_RolePropertyValue));
        dbms_output.put_line(')]');

        str := string_fnc.split(v_RolePropertyValue,',');

        /* Add the entries to the ECToResidentialGroupMapping table. */
        FOR i IN 1..str.count LOOP
            dbms_output.put(CONCAT('inserting ecToRes(ECId = ',v_EnergycompanyId));
            dbms_output.put(CONCAT(', RolePropertyValue = ',str(i)));
            dbms_output.put_line(')');
    
            INSERT INTO ECToResidentialGroupMapping(EnergyCompanyId, GroupId) VALUES (v_EnergyCompanyId, str(i));
    
        END LOOP;

    END LOOP;
    CLOSE ecToResLoginGroup_curs;

    COMMIT;

    dbms_output.put_line('Process Finished');
end;
/
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
   DeviceId             NUMBER                          NOT NULL,
   DigiId               NUMBER                          NOT NULL,
   CONSTRAINT PK_DigiGate PRIMARY KEY (DeviceId)
);

CREATE TABLE ZBDevice  (
   DeviceId             NUMBER                          NOT NULL,
   InstallCode          VARCHAR2(255)                   NOT NULL,
   CONSTRAINT PK_ZBDevice PRIMARY KEY (DeviceId)
);

CREATE TABLE ZBGateway  (
   DeviceId             NUMBER                          NOT NULL,
   FirmwareVersion      VARCHAR2(255)                   NOT NULL,
   MacAddress           VARCHAR2(255)                   NOT NULL,
   CONSTRAINT PK_ZBGateway PRIMARY KEY (DeviceId)
);

CREATE TABLE ZBGatewayToDeviceMapping  (
   GatewayId            NUMBER                          NOT NULL,
   DeviceId             NUMBER                          NOT NULL,
   CONSTRAINT PK_ZBGateToDeviceMap PRIMARY KEY (GatewayId, DeviceId)
);

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

INSERT INTO StateGroup VALUES(-13, 'Commissioned State','Status'); 
INSERT INTO State VALUES(-13, 0, 'Commissioned', 0, 6, 0);
INSERT INTO State VALUES(-13, 1, 'Uncommissioned', 1, 6, 0); 

INSERT INTO YukonListEntry VALUES (1067,1005,0,'UtilityPRO ZigBee',1316);
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

/* Start YUK-9561 */
ALTER TABLE FDRTranslation 
MODIFY DirectionType VARCHAR2(30);

UPDATE FDRInterface 
SET PossibleDirections = 'Send,Send for control,Receive,Receive for control,Receive for Analog Output' 
WHERE InterfaceName = 'VALMET';

UPDATE FDRInterface 
SET PossibleDirections = 'Receive,Receive for control,Receive for Analog Output' 
WHERE InterfaceName = 'TEXTIMPORT';
/* End YUK-9561 */

/* Start YUK-9548 */
CREATE TABLE LMGroupSep  (
   DeviceId             NUMBER                          NOT NULL,
   UtilityEnrollmentGroup NUMBER                        NOT NULL,
   CONSTRAINT PK_LMGroupSep PRIMARY KEY (DeviceId)
);

CREATE TABLE LMGroupSepDeviceClass  (
   DeviceId             NUMBER                          NOT NULL,
   SepDeviceClass       VARCHAR2(40)                    NOT NULL,
   CONSTRAINT PK_LMGroupSepDeviceClass PRIMARY KEY (DeviceId, SepDeviceClass)
);

ALTER TABLE LMGroupSep
    ADD CONSTRAINT FK_LMGroupSep_LMGroup FOREIGN KEY (DeviceId)
        REFERENCES LMGroup (DeviceId)
            ON DELETE CASCADE;

ALTER TABLE LMGroupSepDeviceClass
    ADD CONSTRAINT FK_LMGroupSepDevClass_LMGrpSep FOREIGN KEY (DeviceId)
        REFERENCES LMGroupSep (DeviceId)
            ON DELETE CASCADE;
/* End YUK-9548 */

/* Start YUK-9567 */
INSERT INTO PorterResponseMonitor VALUES (1, 'Default All Meters', '/', -14, 'OUTAGE_STATUS', 'DISABLED');

INSERT INTO PorterResponseMonitorRule VALUES (1, 1, 1, 'Y', 'any', 0); 
INSERT INTO PorterResponseMonitorRule VALUES (2, 2, 1, 'N', 'any', 0); 
INSERT INTO PorterResponseMonitorRule VALUES (3, 3, 1, 'N', 'any', 1); 

INSERT INTO PorterResponseMonitorErrorCode VALUES (1, 2, 93); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (2, 2, 261); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (3, 2, 262); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (4, 2, 263); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (5, 2, 264); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (6, 2, 265); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (7, 2, 267); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (8, 2, 268); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (9, 2, 269); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (10, 2, 270); 

INSERT INTO StatusPointMonitor (StatusPointMonitorId, StatusPointMonitorName, GroupName, Attribute, StateGroupId, EvaluatorStatus) 
SELECT NVL(MAX(StatusPointMonitorId) + 1, 1), 'Default All Meters', '/', 'OUTAGE_STATUS', -14, 'DISABLED' 
FROM StatusPointMonitor;

/* @start-block */
DECLARE
    v_StatusPointMonitorId NUMBER;

BEGIN
    SELECT SPM.StatusPointMonitorId INTO v_StatusPointMonitorId
    FROM StatusPointMonitor SPM
    WHERE SPM.StatusPointMonitorName = 'Default All Meters';
    
    INSERT INTO StatusPointMonitorProcessor (StatusPointMonitorProcessorId, StatusPointMonitorId, PrevState, NextState, ActionType) 
    SELECT NVL(MAX(SPMP.StatusPointMonitorProcessorId) + 1, 1), v_StatusPointMonitorId, 'DIFFERENCE', 1, 'NoResponse' 
    FROM StatusPointMonitorProcessor SPMP;

    INSERT INTO StatusPointMonitorProcessor (StatusPointMonitorProcessorId, StatusPointMonitorId, PrevState, NextState, ActionType) 
    SELECT NVL(MAX(SPMP.StatusPointMonitorProcessorId) + 1, 1), v_StatusPointMonitorId, 'DIFFERENCE', 0, 'Restoration' 
    FROM StatusPointMonitorProcessor SPMP;
END;
/
/* @end-block */
/* End YUK-9567 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.3', 'Matt K', '08-MAR-2011', 'Latest Update', 0);
