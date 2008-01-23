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

/**************************************************************/
/* VERSION INFO                                               */
/**************************************************************/
insert into CTIDatabase values('4.1', 'David', '07-Dec-2007', 'Latest Update', 0 );