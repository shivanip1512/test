/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/* Start YUK-5103 */
/* @error ignore-begin */
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select -110,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from dual;
/* @error ignore-end */ 
/* End YUK-5103*/

/* Start YUK-5123 */
ALTER TABLE DEVICEROUTES
 DROP PRIMARY KEY CASCADE;
ALTER TABLE DEVICEROUTES
 ADD CONSTRAINT PK_DEVICEROUTES
 PRIMARY KEY
 (DEVICEID);
/* End YUK-5123 */

/* Start YUK-5119 */
insert into YukonRoleProperty values(-10817, -108,'Theme Name',' ','The name of the theme to be applied to this group');
/* End YUK-5119 */

/* Start YUK-5204 */
alter table Job add Locale varchar2(10);
update Job set Locale = 'en_US';
alter table Job modify Locale varchar2(10) not null;

alter table Job add TimeZone varchar2(40);
update Job set TimeZone = ' ';
alter table Job modify TimeZone varchar2(40) not null;
/* End YUK-5204 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('4.1', 'David', '07-Dec-2007', 'Latest Update', 0 );
