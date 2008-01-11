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
/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('4.0', 'David', '07-Dec-2007', 'Latest Update', 0 );
