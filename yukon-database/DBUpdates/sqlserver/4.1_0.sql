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
ALTER TABLE [dbo].[DeviceRoutes]
DROP CONSTRAINT [PK_DEVICEROUTES];
GO

ALTER TABLE [dbo].[DeviceRoutes]
ADD CONSTRAINT [PK_DEVICEROUTES] PRIMARY KEY CLUSTERED ([DEVICEID]);
GO
/* End YUK-5123 */

/**************************************************************/
/* VERSION INFO                                               */
/**************************************************************/
insert into CTIDatabase values('4.1', 'David', '07-Dec-2007', 'Latest Update', 0 );