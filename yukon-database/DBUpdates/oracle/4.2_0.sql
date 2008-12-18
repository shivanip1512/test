/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/* Start YUK-6518 */
INSERT INTO YukonRoleProperty VALUES(-20015,-200,'Manage Indexes','false','Controls access to manually build Lucene indexes.'); 
INSERT INTO YukonRoleProperty VALUES(-20016,-200,'View Logs','false','Controls access to view or download log files.');
/* End YUK-6518 */

/* Start YUK-6736 */ 
UPDATE Command
SET label = 'Read LP Info'
WHERE commandId = -15;
/* End YUK-6736 */

/* Start YUK-6710 */ 
CREATE TABLE LMGroupXMLParameter ( 
   XmlParamId NUMBER NOT NULL, 
   LMGroupId NUMBER NOT NULL, 
   ParameterName VARCHAR2(50) NOT NULL, 
   ParameterValue VARCHAR2(50) NOT NULL, 
   CONSTRAINT PK_LMGROUPXMLPARAMETER PRIMARY KEY (XmlParamId) 
); 

CREATE UNIQUE INDEX INDX_LMGroupId_ParamName_UNQ ON LMGroupXMLParameter ( 
   LMGroupId ASC, 
   ParameterName ASC 
); 

ALTER TABLE LMGroupXMLParameter 
   ADD CONSTRAINT FK_LMGroupXml_LMGroup FOREIGN KEY (LMGroupId) 
      REFERENCES LMGroup (DeviceId); 
/* End YUK-6710 */

/* Start YUK-6742 */ 
CREATE TABLE LMProgramGearHistory  (
   GearHistId           NUMBER                          NOT NULL,
   ProgramHistId        NUMBER                          NOT NULL,
   EventTime            DATE                            NOT NULL,
   Action               VARCHAR2(50)                    NOT NULL,
   UserName             VARCHAR2(50)                    NOT NULL,
   GearName             VARCHAR2(50)                    NOT NULL,
   GearId               NUMBER                          NOT NULL,
   Reason               VARCHAR2(50)                    NOT NULL,
   CONSTRAINT PK_LMPROGRAMGEARHISTORY PRIMARY KEY (GearHistId)
);

CREATE TABLE LMProgramHistory  (
   ProgramHistId        NUMBER                          NOT NULL,
   ProgramName          VARCHAR2(50)                    NOT NULL,
   ProgramId            NUMBER                          NOT NULL,
   CONSTRAINT PK_LMPROGRAMHISTORY PRIMARY KEY (ProgramHistId)
);

ALTER TABLE LMProgramGearHistory
   ADD CONSTRAINT FK_LMProgGearHist_LMProgHist FOREIGN KEY (ProgramHistId)
      REFERENCES LMProgramHistory (ProgramHistId);

ALTER TABLE DynamicLMProgramDirect 
ADD CurrentLogId NUMBER NOT NULL DEFAULT 0;
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
CREATE TABLE MspLMInterfaceMapping  (
   MspLMInterfaceMappingId  NUMBER                          NOT NULL,
   StrategyName             VARCHAR2(100)                   NOT NULL,
   SubstationName           VARCHAR2(100)                   NOT NULL,
   PAObjectId               NUMBER                          NOT NULL,
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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */