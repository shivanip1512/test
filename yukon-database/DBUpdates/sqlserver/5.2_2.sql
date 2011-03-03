/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9057 */
ALTER TABLE SurveyResult
ALTER COLUMN AccountId NUMERIC NULL;
/* End YUK-9057 */ 

/* Start YUK-9022 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-20165,-201,'Account Search','true','Enables you to use account searching.');
INSERT INTO YukonRoleProperty VALUES(-20911,-209,'Inventory Search','true','Enables you to use inventory searching.');
/* @error ignore-end */
/* End YUK-9022 */

/* Start YUK-9022 */
DROP INDEX StateGroup.Indx_STATEGRP_Nme;
ALTER TABLE StateGroup ALTER COLUMN Name VARCHAR(60) NOT NULL;
GO
CREATE UNIQUE INDEX Indx_StateGroup_Name_UNQ ON StateGroup (
    NAME ASC
);

/* Add 'RFN Disconnect Status' State and State Groups */
INSERT INTO StateGroup VALUES(-12, 'RFN Disconnect Status', 'Status');
GO

INSERT INTO State VALUES(-12, 0, 'Unknown', 3, 6, 0);
INSERT INTO State VALUES(-12, 1, 'Connected', 0, 6, 0);
INSERT INTO State VALUES(-12, 2, 'Disconnected', 1, 6, 0);
INSERT INTO State VALUES(-12, 3, 'Armed', 4, 6, 0);

/* Renaming CRFAddress table to RFNAddress */
DROP INDEX CRFAddress.Indx_CRFAdd_SerNum_Man_Mod_UNQ;
ALTER TABLE CRFAddress
DROP CONSTRAINT FK_CRFAdd_Device;
ALTER TABLE CRFAddress
DROP CONSTRAINT PK_CRFAdd;
GO

EXEC SP_RENAME 'CRFAddress', 'RFNAddress';
GO

ALTER TABLE RFNAddress
ADD CONSTRAINT PK_RFNAdd PRIMARY KEY (DeviceId);
ALTER TABLE RFNAddress
   ADD CONSTRAINT FK_RFNAdd_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
CREATE UNIQUE INDEX Indx_RFNAdd_SerNum_Man_Mod_UNQ ON RFNAddress (
   SerialNumber ASC,
   Manufacturer ASC,
   Model ASC
);
GO

UPDATE YukonPAObject SET Type = 'RFN-AL' WHERE Type = 'CRF-AL';
UPDATE YukonPAObject SET Type = 'RFN-AX' WHERE Type = 'CRF-AX';
/* End YUK-9022 */

/* Start YUK-8976 */
UPDATE YukonRoleProperty
SET DefaultValue = '120'
WHERE RolePropertyId = -10820;
/* End YUK-8976 */

/* Start YUK-9105 */
INSERT INTO YukonRoleProperty 
VALUES(-1605,-7,'PAOName Extension',' ','The extension name of the field appended to PaoName Alias. Leave this value blank to ignore the use of extensions.');

/* Insert a new entry for the PaoName Alias Extension IF the legacy option of Service Location with Position (value= 6) is found. */
INSERT INTO YukonGroupRole (GroupRoleId, GroupID, RoleID, RolePropertyID, Value)
SELECT (SELECT MAX(GroupRoleId) 
         FROM YukonGroupRole)+1, -1, -7, -1605, 'positionNumber' 
FROM YukonGroupRole 
WHERE RolePropertyId = -1600 
AND Value = '6';

/* Changing the PaoName Alias role property value from int to enum values. 
 Note: Only one of these updates should affect 1 row. The rest should affect 0 rows. */
UPDATE YukonGroupRole 
SET Value = 'METER_NUMBER' 
WHERE RolePropertyId = -1600 
AND Value = '0';

UPDATE YukonGroupRole 
SET Value = 'ACCOUNT_NUMBER' 
WHERE RolePropertyId = -1600 
AND Value = '1';

UPDATE YukonGroupRole 
SET Value = 'SERVICE_LOCATION' 
WHERE RolePropertyId = -1600 
AND Value = '2';

UPDATE YukonGroupRole 
SET Value = 'CUSTOMER_ID' 
WHERE RolePropertyId = -1600 
AND Value = '3';

UPDATE YukonGroupRole 
SET Value = 'EA_LOCATION' 
WHERE RolePropertyId = -1600 
AND Value = '4';

UPDATE YukonGroupRole 
SET Value = 'GRID_LOCATION' 
WHERE RolePropertyId = -1600 
AND Value = '5';

UPDATE YukonGroupRole 
SET Value = 'SERVICE_LOCATION' 
WHERE RolePropertyId = -1600
AND Value = '6';

UPDATE YukonGroupRole 
SET Value = 'POLE_NUMBER' 
WHERE RolePropertyId = -1600 
AND Value = '7';
/* End YUK-9105 */

/* Start YUK-9094 */
UPDATE ContactNotification
SET NotificationCategoryId = 5
WHERE NotificationCategoryId = 0
AND ContactNotifId != 0;
/* End YUK-9094 */

/* Start YUK-9086 */
CREATE TABLE StatusPointMonitor (
   StatusPointMonitorId   NUMERIC              NOT NULL,
   StatusPointMonitorName VARCHAR(255)         NOT NULL,
   GroupName              VARCHAR(255)         NOT NULL,
   Attribute              VARCHAR(255)         NOT NULL,
   StateGroupId           NUMERIC              NOT NULL,
   EvaluatorStatus        VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_StatPointMon PRIMARY KEY (StatusPointMonitorId)
);
GO

CREATE UNIQUE INDEX Indx_StatPointMon_MonName_UNQ ON StatusPointMonitor (
    StatusPointMonitorName ASC
);
GO

CREATE TABLE StatusPointMonitorProcessor (
   StatusPointMonitorProcessorId NUMERIC              NOT NULL,
   StatusPointMonitorId          NUMERIC              NOT NULL,
   PrevState                     VARCHAR(255)         NOT NULL,
   NextState                     VARCHAR(255)         NOT NULL,
   ActionType                    VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_StatPointMonProcId PRIMARY KEY (StatusPointMonitorProcessorId)
);
GO

/* @error ignore-begin */
ALTER TABLE STATEGROUP
   ADD CONSTRAINT SYS_C0013128 PRIMARY KEY (STATEGROUPID);
GO
/* @error ignore-end */
   
ALTER TABLE StatusPointMonitor
    ADD CONSTRAINT FK_StatPointMon_StateGroup FOREIGN KEY (StateGroupId)
        REFERENCES StateGroup (StateGroupId);

ALTER TABLE StatusPointMonitorProcessor
    ADD CONSTRAINT FK_StatPointMonProc_StatPointM FOREIGN KEY (StatusPointMonitorId)
        REFERENCES StatusPointMonitor (StatusPointMonitorId)
            ON DELETE CASCADE;
GO

INSERT INTO YukonRoleProperty VALUES(-20217,-202,'Status Point Monitor','false','Controls access to the Status Point Monitor');
/* End YUK-9086 */

/* Start YUK-9064 */
INSERT INTO YukonRoleProperty VALUES(-1115,-2,'Auto Create Login For Additional Contacts','true','Automatically create a default login for each additional contact created on a STARS account.');
/* End YUK-9064 */

/* Start YUK-9063 */
/* temporarily remove primary key so inserts can be made */
ALTER TABLE StaticPAOinfo DROP CONSTRAINT PK_StatPAOInfo;

/* move SiteAddress to StaticPaoInfo */
INSERT INTO StaticPAOInfo (StaticPAOInfoId, PAObjectId, InfoKey, Value)
(SELECT (1000), PAObjectId, 'RDS_TRANSMITTER_SITE_ADDRESS', SiteAddress
 FROM RDSTransmitter);

/* move encoderAddress to StaticPaoInfo */
INSERT INTO StaticPAOInfo (StaticPAOInfoId, PAObjectId, InfoKey, Value)
(SELECT (1000), PAObjectId, 'RDS_TRANSMITTER_ENCODER_ADDRESS', EncoderAddress
 FROM RDSTransmitter);

/* move TransmitSpeed to StaticPaoInfo */
INSERT INTO StaticPAOInfo (StaticPAOInfoId, PAObjectId, InfoKey, Value)
(SELECT (1000), PAObjectId, 'RDS_TRANSMITTER_TRANSMIT_SPEED', TransmitSpeed
 FROM RDSTransmitter);

/* move GroupType to StaticPaoInfo */
INSERT INTO StaticPAOInfo (StaticPAOInfoId, PAObjectId, InfoKey, Value)
(SELECT (1000), PAObjectId, 'RDS_TRANSMITTER_GROUP_TYPE', GroupType
 FROM RDSTransmitter);

/* update the primary keys to unique values */
DECLARE @incr NUMERIC SET @incr = 1 Update StaticPAOInfo SET @incr = StaticPaoInfoId = @incr + 1 
WHERE StaticPAOInfoId >= 1000;

/* add the primary key back */
ALTER TABLE StaticPAOInfo 
ADD CONSTRAINT PK_StatPAOInfo PRIMARY KEY (StaticPAOInfoId);

/* update existing infoKey values to enums */
UPDATE staticpaoinfo 
SET InfoKey = 'RDS_TRANSMITTER_IP_ADDRESS' 
WHERE InfoKey = 'ip address';

UPDATE StaticPAOInfo 
SET InfoKey = 'RDS_TRANSMITTER_IP_PORT' 
WHERE InfoKey = 'ip port';

/* Delete RDSTransmitter table */
DROP TABLE RDSTransmitter; 
/* End YUK-9063 */

/* Start YUK-9084 */
CREATE TABLE CapBankToZoneMapping (
   DeviceId             NUMERIC              NOT NULL,
   ZoneId               NUMERIC              NOT NULL,
   CONSTRAINT PK_CapBankZoneMap PRIMARY KEY (DeviceId)
);

CREATE TABLE PointToZoneMapping (
   PointId              NUMERIC              NOT NULL,
   ZoneId               NUMERIC              NOT NULL,
   CONSTRAINT PK_PointZoneMap PRIMARY KEY (PointId)
);

CREATE TABLE Zone (
   ZoneId               NUMERIC              NOT NULL,
   ZoneName             VARCHAR(255)         NOT NULL,
   RegulatorId          NUMERIC              NOT NULL,
   SubstationBusId      NUMERIC              NOT NULL,
   ParentId             NUMERIC              NULL,
   CONSTRAINT PK_Zone PRIMARY KEY (ZoneId)
);
GO

CREATE UNIQUE INDEX Indx_Zone_RegId_UNQ ON Zone (
    RegulatorId ASC
);
GO

ALTER TABLE CapBankToZoneMapping
    ADD CONSTRAINT FK_CapBankZoneMap_CapBank FOREIGN KEY (DeviceId)
        REFERENCES CAPBANK (DEVICEID)
            ON DELETE CASCADE;

ALTER TABLE CapBankToZoneMapping
    ADD CONSTRAINT FK_CapBankZoneMap_Zone FOREIGN KEY (ZoneId)
        REFERENCES Zone (ZoneId)
            ON DELETE CASCADE;

ALTER TABLE PointToZoneMapping
    ADD CONSTRAINT FK_PointZoneMap_Point FOREIGN KEY (PointId)
        REFERENCES POINT (POINTID)
            ON DELETE CASCADE;

ALTER TABLE PointToZoneMapping
    ADD CONSTRAINT FK_PointZoneMap_Zone FOREIGN KEY (ZoneId)
        REFERENCES Zone (ZoneId)
            ON DELETE CASCADE;

ALTER TABLE Zone
    ADD CONSTRAINT FK_ZONE_CapContSubBus FOREIGN KEY (SubstationBusId)
        REFERENCES CAPCONTROLSUBSTATIONBUS (SubstationBusID)
            ON DELETE CASCADE;

ALTER TABLE Zone
    ADD CONSTRAINT FK_Zone_PAO FOREIGN KEY (RegulatorId)
        REFERENCES YukonPAObject (PAObjectID);

ALTER TABLE Zone
    ADD CONSTRAINT FK_Zone_Zone FOREIGN KEY (ParentId)
        REFERENCES Zone (ZoneId);
GO
/* End YUK-9084 */

/* Start YUK-9114 */
UPDATE YukonPAObject
SET PAOClass = 'VOLTAGEREGULATOR'
WHERE Type = 'Load Tap Changer';
/* End YUK-9114 */

/* Start YUK-9085 */
INSERT INTO YukonRoleProperty VALUES(-20166,-201,'Survey Edit','false','Enables editing of surveys.');
INSERT INTO YukonRoleProperty VALUES(-20167,-201,'Opt Out Survey Edit','false','Enables editing of opt out surveys.'); 
/* End YUK-9085 */

/* Start YUK-9090 */
UPDATE YukonRoleProperty
SET DefaultValue = 'c:\yukon\client\webgraphs\',
    Description = 'Directory to write generated web graphs to.'
WHERE RolePropertyId = -10500;
/* End YUK-9090 */

/* Start YUK-9119 */
INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Max Consecutive CapBank Ops.', '2.0', 'PEAK'
FROM CapControlStrategy 
WHERE ControlUnits = 'Integrated Volt/Var';

INSERT INTO CCStrategyTargetSettings (StrategyId, SettingName, SettingValue, SettingType)
SELECT StrategyId, 'Max Consecutive CapBank Ops.', '2.0', 'OFFPEAK'
FROM CapControlStrategy
WHERE ControlUnits = 'Integrated Volt/Var';
/* End YUK-9119 */

/* Start YUK-7073 */
/* @error ignore-begin */
INSERT INTO YukonUserRole VALUES(-1200, -100, -801, -80100, '(none)'); 
/* @error ignore-end */
/* End YUK-7073 */

/* Start YUK-9115 */
INSERT INTO YukonRoleProperty VALUES(-10821, -108, 'CSRF Token Mode','OFF', 'Which mode to use for CSRF protection (OFF, TOKEN, PASSWORD).');
/* End YUK-9115 */


/* Start YUK-9077 */
EXEC SP_RENAME 'OptOutSurveyProgram.ProgramId', 'AssignedProgramId'
ALTER TABLE OptOutSurveyProgram ADD DeviceId NUMERIC;
GO

UPDATE OptOutSurveyProgram
SET DeviceId = (SELECT DeviceId
                 FROM LMProgramWebPublishing LMPWP
                 WHERE AssignedProgramId = LMPWP.ProgramId);

ALTER TABLE OptOutSurveyProgram DROP CONSTRAINT FK_OptOutSurvProg_LMProgWebPub;
ALTER TABLE OptOutSurveyProgram DROP CONSTRAINT PK_OptOutSurvProg;

DELETE FROM OptOutSurveyProgram WHERE DeviceId = 0;
ALTER TABLE OptOutSurveyProgram
   ALTER COLUMN DeviceId NUMERIC NOT NULL;

ALTER TABLE OptOutSurveyProgram
    ADD CONSTRAINT PK_OptOutSurvProg PRIMARY KEY (OptOutSurveyId, DeviceId);

ALTER TABLE OptOutSurveyProgram DROP COLUMN AssignedProgramId;

ALTER TABLE OptOutSurveyProgram
    ADD CONSTRAINT FK_OptOutSurvProg_LMProg FOREIGN KEY (DeviceId)
        REFERENCES LMProgram (DeviceId)
            ON DELETE CASCADE;
/* End YUK-9077 */

/* Start YUK-9081 */
ALTER TABLE SurveyResult DROP CONSTRAINT FK_SurvRes_CustAcct;
ALTER TABLE SurveyResult
    ADD CONSTRAINT FK_SurvRes_CustAcct FOREIGN KEY (AccountId)
        REFERENCES CustomerAccount (AccountID)
            ON DELETE SET NULL;
/* End YUK-9081 */

/* Start YUK-9090 */
UPDATE YukonRoleProperty
SET DefaultValue = 'c:\yukon\client\bin\BillingIn.txt',
    Description = 'Directory to read billing input file from. Used by ExtendedTOU_Incode, MVRS, WLT_40, and NCDC_Handhed formats.'
WHERE RolePropertyId = -1501;
/* End YUK-9090 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.2', 'Matt K', '03-OCT-2010', 'Latest Update', 2);