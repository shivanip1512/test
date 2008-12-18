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
create table LMGroupXMLParameter ( 
   xmlParamId numeric not null, 
   lmGroupId numeric not null, 
   parameterName varchar(50) not null, 
   parameterValue varchar(50) not null, 
   constraint PK_LMGROUPXMLPARAMETER primary key (xmlParamId) 
) 

create unique index INDX_LMGroupId_ParamName_UNQ on LMGroupXMLParameter ( 
lmGroupId ASC, 
parameterName ASC 
); 

alter table LMGroupXMLParameter 
   add constraint FK_LMGroupXml_LMGroup foreign key (lmGroupId) 
      references LMGroup (DeviceID); 
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