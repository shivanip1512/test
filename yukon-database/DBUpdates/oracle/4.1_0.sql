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
ALTER TABLE DEVICEROUTES DROP CONSTRAINT PK_DEVICEROUTES;
commit;
/* @error ignore-begin */
drop index PK_DEVICEROUTES;
commit;
/* @error ignore-end */
ALTER TABLE DEVICEROUTES
 ADD CONSTRAINT PK_DEVICEROUTES
 PRIMARY KEY
 (DEVICEID);
commit;
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

/* Start YUK-5350 */
insert into YukonRoleProperty values(-1021,-1,'importer_communications_enabled','true','Specifies whether communications will be allowed by the bulk importer.'); 
/* End YUK-5350

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('4.1', 'David', '07-Dec-2007', 'Latest Update', 0 );
