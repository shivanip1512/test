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
      REFERENCES LMGroup (DeviceId); 
/* End YUK-6710 */

/* Start YUK-6742 */
CREATE TABLE LMProgramGearHistory (
   GearHistId           NUMERIC              NOT NULL,
   ProgramHistId        NUMERIC              NOT NULL,
   EventTime            DATETIME             NOT NULL,
   Action               VARCHAR(50)          NOT NULL,
   UserName             VARCHAR(50)          NOT NULL,
   GearName             VARCHAR(50)          NOT NULL,
   GearId               NUMERIC              NOT NULL,
   Reason               VARCHAR(50)          NOT NULL,
   CONSTRAINT PK_LMPROGRAMGEARHISTORY PRIMARY KEY NONCLUSTERED (GearHistId)
);

CREATE TABLE LMProgramHistory (
   ProgramHistId        NUMERIC              NOT NULL,
   ProgramName          VARCHAR(50)          NOT NULL,
   ProgramId            NUMERIC              NOT NULL,
   CONSTRAINT PK_LMPROGRAMHISTORY PRIMARY KEY NONCLUSTERED (ProgramHistId)
);

ALTER TABLE LMProgramGearHistory
   ADD CONSTRAINT FK_LMProgGearHist_LMProgHist FOREIGN KEY (ProgramHistId)
      REFERENCES LMProgramHistory (ProgramHistId);

ALTER TABLE DynamicLMProgramDirect 
ADD CurrentLogId NUMERIC NOT NULL DEFAULT 0;
/* End YUK-6742 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */