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
   XmlParamId NUMERIC NOT NULL, 
   LMGroupId NUMERIC NOT NULL, 
   ParameterName VARCHAR(50) NOT NULL, 
   ParameterValue VARCHAR(50) NOT NULL, 
   CONSTRAINT PK_LMGROUPXMLPARAMETER PRIMARY KEY (XmlParamId) 
) 

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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */