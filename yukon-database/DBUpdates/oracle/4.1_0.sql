/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

set define off;

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
/* End YUK-5350 */

/* Start YUK-5337 */
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Scanning Meters',12,'Y','STATIC' FROM DeviceGroup WHERE DeviceGroupId<100; 

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Load Profile',0,'Y','SCANNING_LOAD_PROFILE' FROM DeviceGroup WHERE DeviceGroupId<100;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Voltage Profile',0,'Y','SCANNING_VOLTAGE_PROFILE' FROM DeviceGroup WHERE DeviceGroupId<100;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Integrity',0,'Y','SCANNING_INTEGRITY' FROM DeviceGroup WHERE DeviceGroupId<100;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Accumulator',0,'Y','SCANNING_ACCUMULATOR' FROM DeviceGroup WHERE DeviceGroupId<100;

UPDATE DeviceGroup
SET ParentDeviceGroupId = (SELECT DeviceGroupId FROM DeviceGroup WHERE GroupName='Scanning Meters')
WHERE Type IN ('SCANNING_LOAD_PROFILE','SCANNING_VOLTAGE_PROFILE','SCANNING_INTEGRITY','SCANNING_ACCUMULATOR');

ALTER TABLE DEVICEGROUP
RENAME COLUMN SYSTEMGROUP TO Permission;

ALTER TABLE DEVICEGROUP
MODIFY(Permission NVARCHAR2(50));

UPDATE DeviceGroup
SET Permission = 'NOEDIT_NOMOD'
WHERE Permission = 'Y'
AND Type = 'STATIC'
AND GroupName IN ('Scanning Meters');

UPDATE DeviceGroup
SET Permission = 'NOEDIT_MOD'
WHERE Permission = 'Y'
AND Type = 'STATIC';

UPDATE DeviceGroup
SET Permission = 'NOEDIT_NOMOD'
WHERE Permission = 'Y'
AND Type != 'STATIC';

UPDATE DeviceGroup
SET Permission = 'EDIT_MOD'
WHERE Permission = 'N';
/* End YUK-5337 */

/* Start YUK-5454 */
ALTER TABLE DYNAMICBILLINGFIELD ADD PadChar char(1);
UPDATE DYNAMICBILLINGFIELD SET PadChar = ' ';
ALTER TABLE DYNAMICBILLINGFIELD modify PadChar char(1) not null;

ALTER TABLE DYNAMICBILLINGFIELD ADD PadSide varchar(5);
UPDATE DYNAMICBILLINGFIELD SET PadSide = 'none';
ALTER TABLE DYNAMICBILLINGFIELD modify PadSide varchar(5) not null;

ALTER TABLE DYNAMICBILLINGFIELD ADD ReadingType varchar(12);
UPDATE DYNAMICBILLINGFIELD SET ReadingType = 'ELECTRIC';
ALTER TABLE DYNAMICBILLINGFIELD modify ReadingType varchar(12) not null;
/* End YUK-5454 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('4.1', 'David', '07-Dec-2007', 'Latest Update', 0 );
