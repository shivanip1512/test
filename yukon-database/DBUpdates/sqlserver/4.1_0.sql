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

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Scanning Meters',12,'Y','STATIC' FROM DeviceGroup WHERE DeviceGroupId<100; 
go
/* End YUK-5337 */

/**************************************************************/
/* VERSION INFO                                               */
/**************************************************************/
insert into CTIDatabase values('4.1', 'David', '07-Dec-2007', 'Latest Update', 0 );