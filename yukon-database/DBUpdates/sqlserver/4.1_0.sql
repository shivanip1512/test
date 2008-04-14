/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-5107 */
if exists (select 1
            from  sysobjects
           where  id = object_id('MeterReadLog')
            and   type = 'U')
   drop table MeterReadLog;
go

delete from SequenceNumber where SequenceName = 'MeterReadLog';
/* End YUK-5107 */


/* Start YUK-5103 */
/* @error ignore-begin */
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select -110,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0;
/* @error ignore-end */ 
/* End YUK-5103*/

/* Start YUK-5123 */
ALTER TABLE [DeviceRoutes]
DROP CONSTRAINT [PK_DEVICEROUTES];
GO

ALTER TABLE [DeviceRoutes]
ADD CONSTRAINT [PK_DEVICEROUTES] PRIMARY KEY CLUSTERED ([DEVICEID]);
GO
/* End YUK-5123 */

/* Start YUK-5119 */
insert into YukonRoleProperty values(-10817, -108,'Theme Name',' ','The name of the theme to be applied to this group');
/* End YUK-5119 */

/* Start YUK-5204 */
alter table Job add Locale varchar(10);
go
update Job set Locale = 'en_US';
go
alter table Job alter column Locale varchar(10) not null;
go

alter table Job add TimeZone varchar(40);
go
update Job set TimeZone = ' ';
go
alter table Job alter column TimeZone varchar(40) not null;
go
/* End YUK-5204 */

/* Start YUK-5350 */
insert into YukonRoleProperty values(-1021,-1,'importer_communications_enabled','true','Specifies whether communications will be allowed by the bulk importer.'); 
/* End YUK-5350 */

/* Start YUK-5337 */
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Scanning Meters',12,'Y','STATIC' FROM DeviceGroup WHERE DeviceGroupId<100; 
go

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Load Profile',0,'Y','SCANNING_LOAD_PROFILE' FROM DeviceGroup WHERE DeviceGroupId<100;
go
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Voltage Profile',0,'Y','SCANNING_VOLTAGE_PROFILE' FROM DeviceGroup WHERE DeviceGroupId<100;
go
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Integrity',0,'Y','SCANNING_INTEGRITY' FROM DeviceGroup WHERE DeviceGroupId<100;
go
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Accumulator',0,'Y','SCANNING_ACCUMULATOR' FROM DeviceGroup WHERE DeviceGroupId<100;
go

UPDATE DeviceGroup
SET ParentDeviceGroupId = (SELECT DeviceGroupId FROM DeviceGroup WHERE GroupName='Scanning Meters')
WHERE Type IN ('SCANNING_LOAD_PROFILE','SCANNING_VOLTAGE_PROFILE','SCANNING_INTEGRITY','SCANNING_ACCUMULATOR');
go

ALTER TABLE DeviceGroup ALTER COLUMN SystemGroup nvarchar(50);
go
EXEC sp_rename 'DeviceGroup.SystemGroup', 'Permission', 'COLUMN';
go

UPDATE DeviceGroup
SET Permission = 'NOEDIT_NOMOD'
WHERE Permission = 'Y'
AND Type = 'STATIC'
AND GroupName IN ('Scanning Meters');
go

UPDATE DeviceGroup
SET Permission = 'NOEDIT_MOD'
WHERE Permission = 'Y'
AND Type = 'STATIC';
go

UPDATE DeviceGroup
SET Permission = 'NOEDIT_NOMOD'
WHERE Permission = 'Y'
AND Type != 'STATIC';
go

UPDATE DeviceGroup
SET Permission = 'EDIT_MOD'
WHERE Permission = 'N';
go

update devicegroup
set Permission = 'NOEDIT_NOMOD'
where
	GroupName = 'System'
	and ParentDeviceGroupID = 0
	and Type = 'STATIC';
go
/* End YUK-5337 */

/* Start YUK-5454 */
ALTER TABLE DYNAMICBILLINGFIELD ADD PadChar char(1);
go
UPDATE DYNAMICBILLINGFIELD SET PadChar = ' ';
go
ALTER TABLE DYNAMICBILLINGFIELD alter column PadChar char(1) not null;
go

ALTER TABLE DYNAMICBILLINGFIELD ADD PadSide varchar(5);
go
UPDATE DYNAMICBILLINGFIELD SET PadSide = 'none';
go
ALTER TABLE DYNAMICBILLINGFIELD alter column PadSide varchar(5) not null;
go

ALTER TABLE DYNAMICBILLINGFIELD ADD ReadingType varchar(12);
go
UPDATE DYNAMICBILLINGFIELD SET ReadingType = 'ELECTRIC';
go
ALTER TABLE DYNAMICBILLINGFIELD alter column ReadingType varchar(12) not null;
go
/* End YUK-5454 */

/* Start YUK-5519 */
alter table CAPBANK alter column MAPLOCATIONID varchar(64) not null;
go
alter table CapControlFeeder alter column MAPLOCATIONID varchar(64) not null;
go
alter table CapControlSubstationBus alter column MAPLOCATIONID varchar(64) not null;
go
/* End YUK-5519 */

/* Start YUK-5579 */
INSERT INTO BillingFileFormats VALUES(-32, 'NISC TOU (kVarH) Rates Only', 1);
/* End YUK-5579 */

/* Start YUK-5663 */ 
ALTER TABLE JOBSCHEDULEDREPEATING ALTER COLUMN CronString VARCHAR(25) not null; 

ALTER TABLE JOBPROPERTY ALTER COLUMN name VARCHAR(25) not null; 

ALTER TABLE JOBPROPERTY ALTER COLUMN value VARCHAR(100) not null; 

ALTER TABLE JOBSTATUS ALTER COLUMN Message VARCHAR(100) not null;
/* End YUK-5663 */

/* Start YUK-5673 */
/* @error ignore-begin */
INSERT INTO Command VALUES(-141, 'putconfig emetcon freeze ?''Day of month (0-31)''', 'Set meter to freeze on X day of month (use 0 for disable).', 'MCT-410IL'); 
INSERT INTO Command VALUES(-142, 'getconfig freeze', 'Read freeze config from meter and enable scheduled freeze procesing in Yukon.', 'MCT-410IL'); 

INSERT INTO DeviceTypeCommand VALUES (-714, -141, 'MCT-410CL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-715, -141, 'MCT-410FL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-716, -141, 'MCT-410GL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-717, -141, 'MCT-410IL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-718, -142, 'MCT-410CL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-719, -142, 'MCT-410FL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-720, -142, 'MCT-410GL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-721, -142, 'MCT-410IL', 35, 'N', -1); 
/* @error ignore-end */
/* End YUK-5673 */

/**************************************************************/
/* VERSION INFO                                               */
/**************************************************************/
insert into CTIDatabase values('4.1', 'David', '07-Dec-2007', 'Latest Update', 0 );