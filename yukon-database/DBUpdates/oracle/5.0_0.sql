/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7455 */
UPDATE YukonRoleProperty 
SET KeyName='Opt Out Period', Description='The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values; Ex. 1,3,4,5)'
WHERE RolePropertyId IN (-20157, -40055);

DELETE FROM YukonListEntry 
WHERE ListId IN (SELECT ListId 
                 FROM YukonSelectionList 
                 WHERE ListName = 'OptOutPeriod'
                 AND ListId IN (SELECT ItemId 
                                FROM ECToGenericMapping 
                                WHERE MappingCategory LIKE 'YukonSelectionList'));

DELETE FROM ECToGenericMapping 
WHERE ItemId IN (SELECT ListId 
                 FROM YukonSelectionList 
                 WHERE ListName = 'OptOutPeriod' 
                 AND ListId IN (SELECT ItemId 
                                FROM ECToGenericMapping 
                                WHERE MappingCategory LIKE 'YukonSelectionList')); 

DELETE FROM YukonSelectionList 
WHERE ListName = 'OptOutPeriod'; 
/* End YUK-7455 */

/* Start YUK-7618 */
UPDATE YukonRoleProperty 
SET Description = 'Defines a Yukon Pao (Device) Name field alias. Valid values(0-5): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer, 4=EA Location, 5=Grid Location, 6=Service Location [Position]]' 
WHERE RolePropertyId = -1600;
/* End YUK-7618 */

/* Start YUK-7461 */
INSERT INTO YukonRoleProperty VALUES (-21308,-213,'Add/Remove Points','false','Controls access to Add/Remove Points collection action.');
/* End YUK-7461 */

/* Start YUK-7594 */
ALTER TABLE CCStrategyTimeOfDay ADD WkndPercentClose NUMBER;
UPDATE CCStrategyTimeOfDay SET WkndPercentClose = 0;
ALTER TABLE CCStrategyTimeOfDay MODIFY WkndPercentClose NUMBER NOT NULL;
/* End YUK-7594 */

/* Start YUK-7498 */
ALTER TABLE PAOScheduleAssignment ADD DisableOvUv VARCHAR2(1);
UPDATE PAOScheduleAssignment SET DisableOvUv = 'N';
ALTER TABLE PAOScheduleAssignment MODIFY DisableOvUv VARCHAR2(1) NOT NULL; 
/* End YUK-7498 */

/* Start YUK-6623 */
UPDATE Command 
SET Command = 'putconfig xcom temp service enable', Label = 'Temp Out-Of-Service Cancel' 
WHERE CommandId = -69;
/* End YUK-6623 */

/* Start YUK-7587 */
ALTER TABLE MCTBroadCastMapping
   DROP CONSTRAINT FK_MCTB_MAPMCT;
ALTER TABLE MCTBroadCastMapping
   ADD CONSTRAINT FK_MCTBCM_Device_MCTId FOREIGN KEY (MCTId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
/* End YUK-7587 */

/* Start YUK-7404 */
ALTER TABLE DeviceSeries5RTU
    DROP Constraint FK_DvS5r_Dv2w;
    
ALTER TABLE DeviceSeries5RTU
   ADD CONSTRAINT FK_DeviceSer5RTU_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId);

DROP TABLE Device2WayFlags;
/* End YUK-7404 */

/* Start YUK-7384 */
INSERT INTO YukonRoleProperty VALUES(-20603,-206,'Esub Home URL','/esub/sublist.html','The url of the starting page for esubstation. Usually the sublist page.');
/* End YUK-7384 */

/* Start YUK-7662 */
DELETE FROM YukonGroupRole WHERE GroupRoleId IN (-1084, -1085, -1086);
DELETE FROM YukonUserRole WHERE UserRoleID IN (-350, -351, -352);
/* End YUK-7662 */

/* Start YUK-7637 */
ALTER TABLE LMHardwareControlGroup ADD ProgramId INTEGER;

UPDATE LMHardwareControlGroup
SET ProgramId = (SELECT LMPWP.ProgramID
                 FROM LMProgramDirectGroup LMPDG, LMProgramWebPublishing LMPWP
                 WHERE LMPDG.DeviceID =LMPWP.DeviceID
                 AND LMPDG.LMGroupDeviceID = LMHardwareControlGroup.LMGroupID);
                 
UPDATE LMHardwareControlGroup
SET ProgramId = 0
WHERE ProgramId IS NULL;

ALTER TABLE LMHardwareControlGroup MODIFY ProgramId NOT NULL;
/* End YUK-7637 */

/* Start YUK-7175 */
DELETE RawPointHistory WHERE PointId NOT IN (SELECT DISTINCT PointId FROM Point);
/* End YUK-7175 */

/* Start YUK-7731 */
ALTER TABLE DeviceConfigurationItem DROP CONSTRAINT FK_DEVICECO_REF_DEVICEC2; 

ALTER TABLE DeviceConfigurationItem RENAME TO DeviceConfigurationItemTemp;
ALTER TABLE DeviceConfigurationItemTemp DROP CONSTRAINT PK_DeviceConfigurationItem;
COMMIT;

CREATE TABLE DeviceConfigurationItem (
   DeviceConfigurationItemId NUMBER                          NOT NULL,
   DeviceConfigurationId NUMBER                          NOT NULL,
   FieldName            VARCHAR2(60)                    NOT NULL,
   Value                VARCHAR2(60)                    NOT NULL,
   CONSTRAINT PK_DeviceConfigurationItem PRIMARY KEY (DeviceConfigurationItemId)
);

INSERT INTO DeviceConfigurationItem
SELECT DeviceConfigurationItemId, DeviceConfigurationId, FieldName, Value
FROM DeviceConfigurationItemTemp;

ALTER TABLE DeviceConfigurationItem
   ADD CONSTRAINT FK_DevConfItem_DevConf FOREIGN KEY (DeviceConfigurationId)
      REFERENCES DeviceConfiguration (DeviceConfigurationId)
      ON DELETE CASCADE;

DROP TABLE DeviceConfigurationItemTemp;
/* End YUK-7731 */

/* Start YUK-7735 */
CREATE TABLE TamperFlagMonitor  (
   TamperFlagMonitorId  NUMBER                          NOT NULL,
   TamperFlagMonitorName VARCHAR2(255)                    NOT NULL,
   GroupName            VARCHAR2(255)                    NOT NULL,
   EvaluatorStatus      VARCHAR2(255)                    NOT NULL,
   CONSTRAINT PK_TamperFlagMonitor PRIMARY KEY (TamperFlagMonitorId)
);

CREATE UNIQUE INDEX INDX_TampFlagMonName_UNQ ON TamperFlagMonitor (
   TamperFlagMonitorName ASC
);
/* End YUK-7735 */

/* Start YUK-7719 */
ALTER TABLE DeviceTNPPSettings DROP CONSTRAINT FK_DevTNPP_Dev; 

ALTER TABLE DeviceTNPPSettings RENAME TO DeviceTNPPSettingsTemp;
ALTER TABLE DeviceTNPPSettingsTemp DROP CONSTRAINT PK_DEVICETNPPSETTINGS CASCADE;

CREATE TABLE DeviceTNPPSettings  (
   DeviceID             NUMBER                          not null,
   Inertia              NUMBER                          not null,
   DestinationAddress   NUMBER                          not null,
   OriginAddress        NUMBER                          not null,
   IdentifierFormat     CHAR(1)                         not null,
   Protocol             VARCHAR2(32)                    not null,
   DataFormat           CHAR(1)                         not null,
   Channel              CHAR(1)                         not null,
   Zone                 CHAR(1)                         not null,
   FunctionCode         CHAR(1)                         not null,
   PagerID              VARCHAR2(10)                    not null,
   CONSTRAINT PK_DEVICETNPPSETTINGS PRIMARY KEY (DeviceID)
);

INSERT INTO DeviceTNPPSettings
SELECT DeviceID, Inertia, DestinationAddress, OriginAddress, IdentifierFormat,
       Protocol, DataFormat, Channel, Zone, FunctionCode, PagerID
FROM DeviceTNPPSettingsTemp;

alter table DeviceTNPPSettings
   add constraint FK_DevTNPP_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);

DROP TABLE DeviceTNPPSettingsTemp;
/* End YUK-7719 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
