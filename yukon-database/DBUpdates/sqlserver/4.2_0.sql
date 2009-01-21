/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-6518 */
INSERT INTO YukonRoleProperty VALUES(-20015,-200,'Manage Indexes','false','Controls access to manually build Lucene indexes.'); 
INSERT INTO YukonRoleProperty VALUES(-20016,-200,'View Logs','false','Controls access to view or download log files.');
/* End YUK-6518 */

/* Start YUK-6736  */ 
UPDATE Command
SET label = 'Read LP Info'
WHERE commandId = -15;
/* End YUK-6736  */ 

/* Start YUK-6710 */ 
CREATE TABLE LMGroupXMLParameter ( 
   LMXmlParameterId NUMERIC NOT NULL, 
   LMGroupId NUMERIC NOT NULL, 
   ParameterName VARCHAR(50) NOT NULL, 
   ParameterValue VARCHAR(50) NOT NULL, 
   CONSTRAINT PK_LMGROUPXMLPARAMETER PRIMARY KEY (LMXmlParameterId) 
);

CREATE UNIQUE INDEX INDX_LMGroupId_ParamName_UNQ ON LMGroupXMLParameter ( 
   LMGroupId ASC, 
   ParameterName ASC 
); 

ALTER TABLE LMGroupXMLParameter 
   ADD CONSTRAINT FK_LMGroupXml_LMGroup FOREIGN KEY (LMGroupId) 
      REFERENCES LMGroup (DeviceId)
      ON DELETE CASCADE; 
/* End YUK-6710 */

/* Start YUK-6742 */
CREATE TABLE LMProgramGearHistory (
   LMProgramGearHistoryId  NUMERIC              NOT NULL,
   LMProgramHistoryId      NUMERIC              NOT NULL,
   EventTime               DATETIME             NOT NULL,
   Action                  VARCHAR(50)          NOT NULL,
   UserName                VARCHAR(64)          NOT NULL,
   GearName                VARCHAR(30)          NOT NULL,
   GearId                  NUMERIC              NOT NULL,
   Reason                  VARCHAR(50)          NOT NULL,
   CONSTRAINT PK_LMPROGRAMGEARHISTORY PRIMARY KEY NONCLUSTERED (LMProgramGearHistoryId)
);

CREATE TABLE LMProgramHistory (
   LMProgramHistoryId   NUMERIC              NOT NULL,
   ProgramName          VARCHAR(60)          NOT NULL,
   ProgramId            NUMERIC              NOT NULL,
   CONSTRAINT PK_LMPROGRAMHISTORY PRIMARY KEY NONCLUSTERED (LMProgramHistoryId)
);

ALTER TABLE LMProgramGearHistory
   ADD CONSTRAINT FK_LMProgGearHist_LMProgHist FOREIGN KEY (LMProgramHistoryId)
      REFERENCES LMProgramHistory (LMProgramHistoryId);

ALTER TABLE DynamicLMProgramDirect 
ADD CurrentLogId NUMERIC NOT NULL DEFAULT 0;
/* End YUK-6742 */

/* Start YUK-6743 */ 
UPDATE YukonRoleProperty 
SET KeyName='Admin Change Login Username',Description='Controls access to change a customer login username'
WHERE RolePropertyId = -20115;
INSERT INTO YukonRoleProperty VALUES(-20119,-201,'Admin Change Login Password','true','Controls access to change a customer login password'); 

UPDATE YukonRoleProperty 
SET KeyName='Change Login Username',Description='Controls access for customers to change their own login username'
WHERE RolePropertyId = -40009;
INSERT INTO YukonRoleProperty VALUES(-40011,-400,'Change Login Password','true','Controls access for customers to change their own login password');  
/* End YUK-6743 */

/* Start YUK-6726 */
CREATE TABLE MspLMInterfaceMapping (
   MspLMInterfaceMappingId  NUMERIC              NOT NULL,
   StrategyName             VARCHAR(100)         NOT NULL,
   SubstationName           VARCHAR(100)         NOT NULL,
   PAObjectId               NUMERIC              NOT NULL,
   CONSTRAINT PK_MSPLMINTERFACEMAPPING PRIMARY KEY (MspLMInterfaceMappingId)
);

CREATE UNIQUE INDEX INDX_StratName_SubName_UNQ ON MspLMInterfaceMapping (
   StrategyName ASC,
   SubstationName ASC
);

ALTER TABLE MspLMInterfaceMapping
   ADD CONSTRAINT FK_MspLMInterMap_YukonPAObj FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
      ON DELETE CASCADE;
/* End YUK-6726 */

/* Start YUK-6753 */
CREATE TABLE OptOutAdditional (
   InventoryId          NUMERIC              NOT NULL,
   CustomerAccountId    NUMERIC              NOT NULL,
   ExtraOptOutCount     NUMERIC              NOT NULL,
   CONSTRAINT PK_OPTOUTADDITIONAL PRIMARY KEY (InventoryId, CustomerAccountId)
);

CREATE TABLE OptOutEvent (
   OptOutEventId        NUMERIC              NOT NULL,
   InventoryId          NUMERIC              NOT NULL,
   CustomerAccountId    NUMERIC              NOT NULL,
   ScheduledDate        DATETIME             NOT NULL,
   StartDate            DATETIME             NOT NULL,
   StopDate             DATETIME             NOT NULL,
   EventCounts          VARCHAR(25)          NOT NULL,
   EventState           VARCHAR(25)          NOT NULL,
   CONSTRAINT PK_OPTOUTEVENT PRIMARY KEY (OptOutEventId)
);

CREATE TABLE OptOutEventLog (
   OptOutEventLogId     NUMERIC              NOT NULL,
   InventoryId          NUMERIC              NOT NULL,
   CustomerAccountId    NUMERIC              NOT NULL,
   EventAction          VARCHAR(25)          NOT NULL,
   LogDate              DATETIME             NOT NULL,
   EventStartDate       DATETIME             NULL,
   EventStopDate        DATETIME             NOT NULL,
   LogUserId            NUMERIC              NOT NULL,
   OptOutEventId        NUMERIC              NOT NULL,
   EventCounts          VARCHAR(25)          NULL,
   CONSTRAINT PK_OPTOUTEVENTLOG PRIMARY KEY (OptOutEventLogId)
);

CREATE TABLE OptOutTemporaryOverride (
   OptOutTemporaryOverrideId  NUMERIC              NOT NULL,
   UserId                     NUMERIC              NOT NULL,
   EnergyCompanyId            NUMERIC              NOT NULL,
   OptOutType                 VARCHAR(25)          NOT NULL,
   StartDate                  DATETIME             NOT NULL,
   StopDate                   DATETIME             NOT NULL,
   OptOutValue                VARCHAR(10)          NOT NULL,
   CONSTRAINT PK_OPTOUTTEMPORARYOVERRIDE PRIMARY KEY (OptOutTemporaryOverrideId)
);

ALTER TABLE OptOutEventLog
   ADD CONSTRAINT FK_OptOutEvent_OptOutEventLog FOREIGN KEY (OptOutEventId)
      REFERENCES OptOutEvent (OptOutEventId);

INSERT INTO YukonRoleProperty VALUES(-1704,-8,'Opt Outs Count', 'true', 'Determines whether new opt outs count against the opt out limits.'); 
INSERT INTO YukonRoleProperty VALUES(-20895,-201,'Opt Out Admin Status','true','Determines whether an operator can see current opt out status on the Opt Out Admin page.');
INSERT INTO YukonRoleProperty VALUES(-20896,-201,'Opt Out Admin Change Enabled','true','Determines whether an operator can enable or disable Opt Outs for the rest of the day.');
INSERT INTO YukonRoleProperty VALUES(-20897,-201,'Opt Out Admin Cancel Current','true','Determines whether an operator can cancel (reenable) ALL currently Opted Out devices.');
INSERT INTO YukonRoleProperty VALUES(-20898,-201,'Opt Out Admin Change Counts','true','Determines whether an operator can change from Opt Outs count against limits today to Opt Outs do not count.'); 
INSERT INTO YukonRoleProperty VALUES(-40056,-400,'Opt Out Limits',' ','Contains information on Opt Out limits.');
/* End YUK-6753 */

/* Start YUK-6776 */
INSERT INTO BillingFileFormats VALUES( -34, 'Curtailment Events - Itron', 1);
/* End YUK-6776 */

/* Start YUK-6596 */
ALTER TABLE PortTerminalServer
ADD EncodingKey VARCHAR(64);
go

UPDATE PortTerminalServer
SET EncodingKey='';
go

ALTER TABLE PortTerminalServer
ALTER COLUMN EncodingKey VARCHAR(64) NOT NULL;
go

ALTER TABLE PortTerminalServer
ADD EncodingType VARCHAR(50);
go

UPDATE PortTerminalServer
SET EncodingType='NONE';
go

ALTER TABLE PortTerminalServer
ALTER COLUMN EncodingType VARCHAR(50) NOT NULL;
go
/* End YUK-6596 */

/* Start YUK-6774 */
INSERT INTO YukonRoleProperty VALUES(-1318,-4,'Enable Password Recovery','true','Controls access to password recovery (Forgot your password?) feature.');
/* End YUK-6774 */

/* Start YUK-6854 */
INSERT INTO FDRInterface VALUES(27, 'MULTISPEAK_LM', 'Receive', 'f');
INSERT INTO FDRInterfaceOption VALUES(27, 'ObjectId', 1, 'Text', '(none)');
/* End YUK-6854 */

/* Start YUK-6845 */
INSERT INTO FDRInterface VALUES(28, 'DNPSLAVE', 'Send', 't');
INSERT INTO FDRInterfaceOption VALUES(28, 'MasterId', 1, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'SlaveId', 2, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'Offset', 3, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'Destination/Source', 4, 'Text', '(none)');
/* End YUK-YUK-6845 */

/* Start YUK-6850 */
INSERT INTO Command VALUES(-154, 'getvalue tou kwh', 'Read Current TOU kWh for rates A, B, C, D.', 'MCT-410IL');
INSERT INTO Command VALUES(-155, 'getvalue tou kwh frozen', 'Read Frozen TOU kWh for rates A, B, C, D.', 'MCT-410IL');

INSERT INTO DeviceTypeCommand VALUES(-737, -154, 'MCT-410CL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-738, -154, 'MCT-410FL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-739, -154, 'MCT-410GL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-740, -154, 'MCT-410IL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-741, -155, 'MCT-410CL', 37, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-742, -155, 'MCT-410FL', 37, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-743, -155, 'MCT-410GL', 37, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-744, -155, 'MCT-410IL', 37, 'Y', -1);
/* End YUK-6850 */

/* Start YUK-6846 */
DELETE YukonUserRole 
WHERE RolePropertyId = -10600;

UPDATE YukonRoleProperty 
SET KeyName = 'Dynamic Billing File Setup', DefaultValue = 'true', Description = 'Controls access to create, edit, and delete dynamic billing files.' 
WHERE RolePropertyId = -10600;
/* End YUK-6846 */

/* Start YUK-6610 */
/* @error ignore-begin */
CREATE UNIQUE INDEX INDX_IPAdd_SockPortNum_UNQ ON PortTerminalServer (
   IPAddress ASC,
   SocketPortNumber ASC
);
/* @error ignore-end */
/* End YUK-6610 */

/* Start YUK-6851 */
UPDATE Command 
SET Label = 'Read Peak (Channel 3)' 
WHERE CommandId = -139; 
/* End YUK-6851 */

/* Start YUK-6849 */
ALTER TABLE Contact 
ALTER COLUMN ContFirstName VARCHAR(120) NOT NULL;

ALTER TABLE Contact 
ALTER COLUMN ContLastName VARCHAR(120) NOT NULL;
/* End YUK-6849 */

/* Start YUK-2947 */
ALTER TABLE LMDirectCustomerList DROP CONSTRAINT FK_CICSTB_LMPRDI;
ALTER TABLE LMDirectCustomerList DROP CONSTRAINT FK_LMDIRECT_REFLMPDIR_LMPROGRA;
GO

ALTER TABLE LMDirectCustomerList
   ADD CONSTRAINT FK_LMDirCustList_CICustBase FOREIGN KEY (CustomerId)
      REFERENCES CICustomerBase (CustomerId)
      ON DELETE CASCADE;

ALTER TABLE LMDirectCustomerList
   ADD CONSTRAINT FK_LMDirCustList_LMProgDir FOREIGN KEY (ProgramId)
      REFERENCES LMProgramDirect (DeviceId)
      ON DELETE CASCADE;
GO
/* End YUK-2947 */

/* Start YUK-6858 */
ALTER TABLE DynamicCCCapBank ADD PartialPhaseInfo VARCHAR(20);
GO
UPDATE DynamicCCCapBank SET PartialPhaseInfo = '(none)';
GO
ALTER TABLE DynamicCCCapBank ALTER COLUMN PartialPhaseInfo VARCHAR(20) NOT NULL;
/* End YUK-6858 */

/* Start YUK-6558 */
UPDATE YukonRoleProperty
SET KeyName = 'Msp BillingCycle DeviceGroup'
WHERE RolePropertyId = -1602;
/* End YUK-6558 */

/* Start YUK-6860 */
INSERT INTO YukonRoleProperty VALUES(-20899,-201,'Thermostat Schedule 5-2','false','Allows a user to select Weekday/Weekend in addition to Weekday/Saturday/Sunday for thermostat schedule editing.');
INSERT INTO YukonRoleProperty VALUES(-40204,-400,'Thermostat Schedule 5-2','false','Allows a user to select Weekday/Weekend in addition to Weekday/Saturday/Sunday for thermostat schedule editing.');

ALTER TABLE LMThermostatSeasonEntry ADD HeatTemperature numeric(18);
EXEC sp_rename 'LMThermostatSeasonEntry.[Temperature]', 'CoolTemperature', 'COLUMN';

ALTER TABLE LMThermostatSeason ADD HeatStartDate datetime;
EXEC sp_rename 'LMThermostatSeason.[StartDate]', 'CoolStartDate', 'COLUMN';
ALTER TABLE LMThermostatSeason DROP COLUMN DisplayOrder;
/* End YUK-6860 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */