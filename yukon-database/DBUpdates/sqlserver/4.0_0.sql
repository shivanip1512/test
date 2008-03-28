/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-5287 */
alter table dynamicccarea add controlvalue numeric;
go
update dynamicccarea set controlvalue = 0;
go
alter table dynamicccarea alter column controlvalue numeric not null; 
go
/* End YUK-5287 */

create table DYNAMICCCSPECIALAREA (
   AreaID               numeric              not null,
   additionalflags      varchar(20)          not null,
   ControlValue         numeric              not null
);
go


create table DYNAMICBILLINGFIELD (
   id                   numeric              not null,
   FormatID             numeric              not null,
   FieldName            varchar(50)          not null,
   FieldOrder           numeric              not null,
   FieldFormat          varchar(50)          null,
   MaxLength            numeric              not null,
   constraint PK_DYNAMICBILLINGFIELD primary key (id)
);
go

create table DynamicBillingFormat (
	 FormatID numeric NOT NULL,
	 Delimiter varchar(20),
	 Header varchar(255),
	 Footer varchar(255),
	 constraint PK_DYNAMICBILLINGFORMAT primary key (FormatID)
);
go 

alter table BillingFileFormats ALTER COLUMN FormatType varchar(100);
go

/* Start YUK-4920 */
alter table BillingFileFormats ADD SystemFormat smallint;
go
/* End YUK-4920 */

update BillingFileFormats SET SystemFormat=1;
go
insert into sequenceNumber values (100, 'BillingFileFormats');
go
/* Start YUK-4744 */
insert into billingfileformats values( 31, 'STANDARD',1);
update billingFileFormats set formatID = -100 where formattype = 'INVALID'; 

insert into DynamicBillingFormat values( 31, ',' ,'H Meter kWh Time Date Peak PeakT PeakD', '');
insert into DynamicBillingFormat values( 21, ',' ,'' ,''); 

/* STANDARD */
insert into DynamicBillingField values(1, 31,'Plain Text', 0, 'M', 0);
insert into DynamicBillingField values(2, 31, 'meterNumber', 1, '', 0);
insert into DynamicBillingField values(3, 31, 'totalConsumption - reading',2,'#####', 0);
insert into DynamicBillingField values(4, 31, 'totalConsumption - timestamp', 3, 'HH:mm', 0);
insert into DynamicBillingField values(5, 31, 'totalConsumption - timestamp', 4, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(6, 31, 'totalPeakDemand - reading', 5, '##0.000', 0);
insert into DynamicBillingField values(7, 31, 'totalPeakDemand - timestamp', 6, 'HH:mm', 0);
insert into DynamicBillingField values(8, 31, 'totalPeakDemand - timestamp', 7, 'MM/dd/yyyy', 0);

/* SimpleTOU */
insert into DynamicBillingField values(10, 21, 'meterNumber', 0, '', 0);
insert into DynamicBillingField values(11, 21, 'totalConsumption - reading' ,1, '#####', 0);
insert into DynamicBillingField values(12, 21, 'totalConsumption - timestamp', 2, 'HH:mm', 0);
insert into DynamicBillingField values(13, 21, 'totalConsumption - timestamp', 3, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(14, 21, 'totalPeakDemand - reading', 4, '##0.000', 0);
insert into DynamicBillingField values(15, 21, 'totalPeakDemand - timestamp', 5, 'HH:mm', 0);
insert into DynamicBillingField values(16, 21, 'totalPeakDemand - timestamp', 6, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(17, 21, 'rateAConsumption - reading', 7, '#####', 0);
insert into DynamicBillingField values(18, 21, 'rateADemand - reading', 8, '##0.000', 0);
insert into DynamicBillingField values(19, 21, 'rateADemand - timestamp', 9, 'HH:mm', 0);
insert into DynamicBillingField values(20, 21, 'rateADemand - timestamp', 10, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(21, 21, 'rateBConsumption - reading', 11, '#####', 0);
insert into DynamicBillingField values(22, 21, 'rateBDemand - reading', 12, '##0.000', 0);
insert into DynamicBillingField values(23, 21, 'rateBDemand - timestamp', 13, 'HH:mm', 0);
insert into DynamicBillingField values(24, 21, 'rateBDemand - timestamp', 14, 'MM/dd/yyyy', 0); 
/* End YUK-4744 */

update yukonpaobject set type = 'MCT-430SL' where type = 'MCT-430SN' or type = 'MCT430SN';
update devicetypecommand set devicetype = 'MCT-430SL' where devicetype = 'MCT-430SN' or devicetype = 'MCT430SN';

insert into SeasonSchedule values (-1,'No Season');
insert into DateOfSeason values(-1, 'Default', 1,1,12,31);
go

create table CCSEASONSTRATEGYASSIGNMENT (
   paobjectid           numeric              not null,
   seasonscheduleid     numeric              not null,
   seasonname           varchar(20)          not null,
   strategyid           numeric              not null,
   constraint PK_CCSEASONSTRATEGYASSIGNMENT primary key (paobjectid,seasonscheduleid, seasonname)
);
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_PAOID foreign key (paobjectid)
      references YukonPAObject (PAObjectID);
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_SCHEDID foreign key (seasonscheduleid)
      references SeasonSchedule (ScheduleID);
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_ccssa_season foreign key (seasonscheduleid, seasonname)
      references DateOfSeason (SeasonScheduleID, SeasonName);
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSEASON_REFERENCE_CAPCONTR foreign key (strategyid)
      references CapControlStrategy (StrategyID);
go

/* Start YUK-4905 */
insert into ccseasonstrategyassignment
(paobjectid, seasonscheduleid, seasonname, strategyid)
select substationbusid, -1,'Default',strategyid from capcontrolsubstationbus;
go
insert into ccseasonstrategyassignment
(paobjectid, seasonscheduleid, seasonname, strategyid)
select feederid, -1, 'Default',strategyid from capcontrolfeeder;
go
insert into ccseasonstrategyassignment
(paobjectid, seasonscheduleid, seasonname, strategyid)
select areaid, -1, 'Default',strategyid from capcontrolarea; 
/* End YUK-4905 */

alter table CapControlArea  drop constraint FK_CAPCONTAREA_CAPCONTRSTRAT;
alter table CapControlArea drop column StrategyId;
alter table CapControlSubstationbus  drop constraint FK_CCSUBB_CCSTR;
alter table CapControlSubstationbus drop column StrategyId;
alter table CapControlFeeder  drop constraint FK_CCFDR_CCSTR;
alter table CapControlFeeder drop column StrategyId;
go

create table CAPCONTROLSPECIALAREA (
   AreaID               numeric              not null,
   constraint PK_CapControlSpecialArea primary key (AreaID)
)
go

create table CCSubSpecialAreaAssignment (

   AreaID numeric not null,
   SubstationBusID numeric not null,
   DisplayOrder numeric not null

);

alter table CCSubSpecialAreaAssignment
   add constraint PK_CCSubSpecialAreaAssignment primary key  (AreaId, SubstationBusId);
go


alter table DYNAMICCCAREA
   add constraint PK_DYNAMICCCAREA primary key  (AreaId);
go

insert into DynamicCCSpecialArea (AreaId, Additionalflags, ControlValue) select areaid, 'NNNNNNNNNNNNNNNNNNNN', 0 from CapControlSpecialArea;
go

alter table DynamicCCFeeder add PhaseAValue float;
go
update DynamicCCFeeder set PhaseAValue = 0;
go
alter table DynamicCCFeeder alter column PhaseAValue float not null;
go
alter table DynamicCCFeeder add PhaseBValue float;
go
update DynamicCCFeeder set PhaseBValue = 0;
go
alter table DynamicCCFeeder alter column PhaseBValue float not null;
go
alter table DynamicCCFeeder add PhaseCValue float;
go
update DynamicCCFeeder set PhaseCValue = 0;
go
alter table DynamicCCFeeder alter column PhaseCValue float not null;
go
alter table DynamicCCSubstationbus add PhaseAValue float;
go
update DynamicCCSubstationbus set PhaseAValue = 0;
go
alter table DynamicCCSubstationbus alter column PhaseAValue float not null;
go
alter table DynamicCCSubstationbus add PhaseBValue float;
go
update DynamicCCSubstationbus set PhaseBValue = 0;
go
alter table DynamicCCSubstationbus alter column PhaseBValue float not null;
go
alter table DynamicCCSubstationbus add PhaseCValue float;
go
update DynamicCCSubstationbus set PhaseCValue = 0;
go
alter table DynamicCCSubstationbus alter column PhaseCValue float not null;



alter table CapControlFeeder add UsePhaseData char(1);
go
update CapControlFeeder set UsePhaseData = 'N';
go
alter table CapControlFeeder alter column UsePhaseData char(1) not null;
go
alter table CapControlFeeder add PhaseB numeric;
go
update CapControlFeeder set PhaseB = 0;
go
alter table CapControlFeeder alter column PhaseB numeric not null;
go
alter table CapControlFeeder add PhaseC numeric;
go
update CapControlFeeder set PhaseC = 0;
go
alter table CapControlFeeder alter column PhaseC numeric not null;
go
alter table CapControlSubstationbus add UsePhaseData char(1);
go
update CapControlSubstationbus set UsePhaseData = 'N';
go
alter table CapControlSubstationbus alter column UsePhaseData char(1) not null;
go
alter table CapControlSubstationbus add PhaseB numeric;
go
update CapControlSubstationbus set PhaseB = 0;
go
alter table CapControlSubstationbus alter column PhaseB numeric not null;
go
alter table CapControlSubstationbus add PhaseC numeric;
go
update CapControlSubstationbus set PhaseC = 0;
go
alter table CapControlSubstationbus alter column PhaseC numeric not null;


delete from YukonlistEntry where YukonDefinitionID = 3100;
go


update State
set text = 'Normal'
where 
	stategroupid = -1 
	and text = 'AnalogText';
/* @error ignore-begin */
INSERT INTO State VALUES(-1, 1, 'Non-update', 1, 6 , 0);
INSERT INTO State VALUES(-1, 2, 'Rate of Change', 2, 6 , 0);
INSERT INTO State VALUES(-1, 3, 'Limit Set 1', 3, 6 , 0);
INSERT INTO State VALUES(-1, 4, 'Limit Set 2', 4, 6 , 0);
INSERT INTO State VALUES(-1, 5, 'High Reasonability', 5, 6 , 0);
INSERT INTO State VALUES(-1, 6, 'Low Reasonability', 6, 6 , 0);
INSERT INTO State VALUES(-1, 7, 'Low Limit 1', 7, 6 , 0);
INSERT INTO State VALUES(-1, 8, 'Low Limit 2', 8, 6 , 0);
INSERT INTO State VALUES(-1, 9, 'High Limit 1', 9, 6 , 0);
INSERT INTO State VALUES(-1, 10, 'High Limit 2', 10, 6 , 0);
/* @error ignore-end */

/* Start YUK-5209 */
ALTER TABLE CCFEEDERBANKLIST
 ADD ControlOrderTemp  NUMERIC(18,5);
go
ALTER TABLE CCFEEDERBANKLIST
 ADD CloseOrderTemp  NUMERIC(18,5);
go
ALTER TABLE CCFEEDERBANKLIST
 ADD TripOrderTemp  NUMERIC(18,5);
go

UPDATE CCFeederBankList
   SET ControlOrderTemp = ControlOrder
      ,CloseOrderTemp = CloseOrder
      ,TripOrderTemp = TripOrder;
go 
ALTER TABLE CCFEEDERBANKLIST DROP COLUMN CONTROLORDER;
go
ALTER TABLE CCFEEDERBANKLIST DROP COLUMN CLOSEORDER;
go
ALTER TABLE CCFEEDERBANKLIST DROP COLUMN TRIPORDER;
go
sp_rename 'ccfeederbanklist.ControlOrderTemp', 'ControlOrder', 'COLUMN';
go
sp_rename 'ccfeederbanklist.CLOSEORDERTEMP', 'CloseOrder', 'COLUMN';
go
sp_rename 'ccfeederbanklist.TripOrderTemp', 'TripOrder', 'COLUMN';
go

ALTER TABLE CCFEEDERBANKLIST
alter column ControlOrder NUMERIC(18,5) NOT NULL;
go
ALTER TABLE CCFEEDERBANKLIST
alter column CloseOrder NUMERIC(18,5) NOT NULL;
go
ALTER TABLE CCFEEDERBANKLIST
alter column TripOrder NUMERIC(18,5) NOT NULL;
go
/* End YUK-5209 */

update command set label = 'Turn Off Test Light' where commandid = -65;
update command set label = 'Clear Comm Loss Counter' where commandid = -67;


/* @error ignore-begin */
insert into yukonroleproperty values (-100011,-1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into yukonroleproperty values (-100012,-1000, 'Substation Last Update Timestamp', 'true', 'is last update timstamp shown for substations');
insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timstamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timstamp shown for capbanks');
insert into yukonroleproperty values (-100105,-1001, 'Target', 'true', 'is target stat displayed');
/* @error ignore-end */

update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100008;
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100007;

insert into YukonRoleProperty values(-1308,-4,'LDAP DN','dc=example,dc=com','LDAP Distinguished Name');
insert into YukonRoleProperty values(-1309,-4,'LDAP User Suffix','ou=users','LDAP User Suffix');
insert into YukonRoleProperty values(-1310,-4,'LDAP User Prefix','uid=','LDAP User Prefix');
insert into YukonRoleProperty values(-1311,-4,'LDAP Server Address','127.0.0.1','LDAP Server Address');
insert into YukonRoleProperty values(-1312,-4,'LDAP Server Port','389','LDAP Server Port');
insert into YukonRoleProperty values(-1313,-4,'LDAP Server Timeout','30','LDAP Server Timeout (in seconds)');

insert into YukonRoleProperty values(-1314,-4,'Active Directory Server Address','127.0.0.1','Active Directory Server Address');
insert into YukonRoleProperty values(-1315,-4,'Active Directory Server Port','389','Active Directory Server Port');
insert into YukonRoleProperty values(-1316,-4,'Active Directory Server Timeout','30','Active Directory Server Timeout (in seconds)');
insert into YukonRoleProperty values(-1317,-4,'Active Directory NT Domain Name','(none)','Active Directory NT DOMAIN NAME');

insert into YukonGroupRole values(-50,-1,-4,-1308,'(none)');
insert into YukonGroupRole values(-51,-1,-4,-1309,'(none)');
insert into YukonGroupRole values(-52,-1,-4,-1310,'(none)');
insert into YukonGroupRole values(-53,-1,-4,-1311,'(none)');
insert into YukonGroupRole values(-54,-1,-4,-1312,'(none)');
insert into YukonGroupRole values(-55,-1,-4,-1313,'(none)');

insert into YukonGroupRole values(-56,-1,-4,-1314,'(none)');
insert into YukonGroupRole values(-57,-1,-4,-1315,'(none)');
insert into YukonGroupRole values(-58,-1,-4,-1316,'(none)');
insert into YukonGroupRole values(-59,-1,-4,-1317,'(none)');
insert into YukonGroupRole values(-92,-1,-4,-1307,'(none)');

/* Start YUK-4982 */
create table TOUATTRIBUTEMAPPING (
   touid                numeric(6)           not null,
   displayname          varchar(50)          not null,
   peakAttribute        varchar(50)          not null,
   usageAttribute       varchar(50)          not null,
   constraint PK_TOUATTRIBUTEMAPPING primary key (touid)
);
go

INSERT INTO TouAttributeMapping VALUES (1, 'A', 'TOU_RATE_A_PEAK_DEMAND', 'TOU_RATE_A_USAGE');
INSERT INTO TouAttributeMapping VALUES (2, 'B', 'TOU_RATE_B_PEAK_DEMAND', 'TOU_RATE_B_USAGE');
INSERT INTO TouAttributeMapping VALUES (3, 'C', 'TOU_RATE_C_PEAK_DEMAND', 'TOU_RATE_C_USAGE');
INSERT INTO TouAttributeMapping VALUES (4, 'D', 'TOU_RATE_D_PEAK_DEMAND', 'TOU_RATE_D_USAGE');
/* Start YUK-4982 */

alter table cceventlog add actionId numeric;
go
update cceventlog set actionId = -1;
go
alter table cceventlog alter column actionId numeric not null;

 
/* Begin YUK-4785, YUK-5216 */
if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICECONFIGURATIONDEVICEMAP')
            and   type = 'U')
   drop table DEVICECONFIGURATIONDEVICEMAP;
go

create table DEVICECONFIGURATIONDEVICEMAP (
   DeviceID             numeric              not null,
   DeviceConfigurationId numeric              not null,
   constraint PK_DEVICECONFIGURATIONDEVICEMA primary key (DeviceID)
);
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICECONFIGURATION')
            and   type = 'U')
   drop table DEVICECONFIGURATION;
go

create table DEVICECONFIGURATION (
   DeviceConfigurationID numeric              not null,
   Name                 varchar(60)          not null,
   Type                 varchar(30)          not null,
   constraint PK_DEVICECONFIGURATION primary key (DeviceConfigurationID)
);
go

if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICECONFIGURATIONITEM')
            and   type = 'U')
   drop table DEVICECONFIGURATIONITEM;
go

create table DEVICECONFIGURATIONITEM (
   DEVICECONFIGURATIONITEMID numeric              not null,
   DeviceConfigurationID numeric              not null,
   FieldName            varchar(30)          not null,
   Value                varchar(30)          not null,
   constraint PK_DEVICECONFIGURATIONITEM primary key (DEVICECONFIGURATIONITEMID)
);
go

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_DEVICECO foreign key (DeviceConfigurationId)
      references DEVICECONFIGURATION (DeviceConfigurationID)
         on delete cascade;
go

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_YUKONPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
          on delete cascade;
go

alter table DEVICECONFIGURATIONITEM
   add constraint FK_DEVICECO_REF_DEVICEC2 foreign key (DeviceConfigurationID)
      references DEVICECONFIGURATION (DeviceConfigurationID)
          on delete cascade;
go


/* End YUK-4785, YUK-5216 */
insert into YukonRoleProperty values(-20013,-200,'Edit Device Config','false','Controls the ability to edit and create device configurations');
insert into YukonRoleProperty values(-20014,-200,'View Device Config','true','Controls the ability to view existing device configurations');
insert into YukonRoleProperty values(-20206,-202,'Enable Profile Request','true','Access to perform profile data request');

alter table userPaoPermission add Allow varchar(5);
go
update UserPaoPermission set Allow = 'ALLOW';
go
alter table groupPaoPermission add Allow varchar(5);
go
update GroupPaoPermission set Allow = 'ALLOW';
go

/* Start YUK-5048 */
ALTER TABLE GROUPPAOPERMISSION
alter column ALLOW varchar(5) NOT NULL;
/* End YUK-5048 */

/* Start YUK-5046 */
ALTER TABLE USERPAOPERMISSION
alter column ALLOW varchar(5) NOT NULL;
/* End YUK-5046 */

/* Start YUK-5095 */
insert into YukonRoleProperty values(-70013,-700,'Definition Available','Switched:Open,Switched:OpenQuestionable,Switched:OpenPending,Standalone:Open,Standalone:OpenQuestionable,Standalone:OpenPending','Capbank sized in these states will be added to the available sum.');
insert into YukonRoleProperty values(-70014,-700,'Definition Unavailable','Switched:Close,Switched:CloseQuestionable,Switched:CloseFail,Switched:ClosePending,Switched:OpenFail,Standalone:Close,Standalone:CloseQuestionable,Standalone:CloseFail,Standalone:ClosePending,Standalone:OpenFail,Fixed-Open,Disabled-Open','Capbank sized in these states will be added to the unavailable sum.');
insert into YukonRoleProperty values(-70015,-700,'Definition Tripped','Switched:Open,Switched:OpenFail,Switched:OpenPending,Switched:OpenQuestionable,Standalone:Open,Standalone:OpenFail,Standalone:OpenPending,Standalone:OpenQuestionable','Capbank sized in these states will be added to the tripped sum.');
insert into YukonRoleProperty values(-70016,-700,'Definition Closed','Switched:Close,Switched:CloseFail,Switched:CloseQuestionable,Switched:ClosePending,Standalone:Close,Standalone:CloseFail,Standalone:CloseQuestionable,Standalone:ClosePending,Fixed:Close,Fixed:CloseFail,Fixed:CloseQuestionable,Fixed:ClosePending,Disabled:Close,Disabled:CloseFail,Disabled:CloseQuestionable,Disabled:ClosePending','Capbank sized in these states will be added to the closed sum.');
/* End YUK-5095 */
insert into YukonRoleProperty VALUES(-100204, -1002, 'Daily/Total Operation Count', 'true', 'is Daily/Total Operation Count displayed');

/* Start YUK-4752 */
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'System',0,'Y','STATIC' FROM DeviceGroup WHERE DeviceGroupId<100;
go
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Routes',(SELECT MAX(DeviceGroupID) from DeviceGroup WHERE DeviceGroupId<100),'Y','ROUTE' FROM DeviceGroup WHERE DeviceGroupId<100;
go
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Device Types',(SELECT MAX(DeviceGroupID)-1 from DeviceGroup WHERE DeviceGroupId<100),'Y','DEVICETYPE' FROM DeviceGroup WHERE DeviceGroupId<100; 
go
/* End YUK-4752 */

alter table dynamiccccapbank add beforeVar varchar(32);
go
update dynamiccccapbank set beforeVar = '---';
go
alter table dynamiccccapbank alter column beforeVar varchar(32) not null;
go
alter table dynamiccccapbank add afterVar varchar(32);
go
update dynamiccccapbank set afterVar = '---';
go
alter table dynamiccccapbank alter column afterVar varchar(32) not null;
go
alter table dynamiccccapbank add changeVar varchar(32);
go
update dynamiccccapbank set changeVar = '---';
go
alter table dynamiccccapbank alter column changeVar varchar(32) not null;
go


alter table lmthermostatgear add RampRate float;
go
update lmthermostatgear set RampRate = 0;
go
alter table lmthermostatgear alter column RampRate float not null;
go


/* Start YUK-4707 */
create table CAPCONTROLSUBSTATION  (
   SubstationID         numeric                          not null,
   constraint PK_CAPCONTROLSUBSTATION primary key (SubstationID)
);

create table DYNAMICCCSUBSTATION  (
   SubStationID         numeric                          not null,
   AdditionalFlags      VARCHAR(20)                    not null,
   SAEnabledID          numeric                          not null,
   constraint PK_DYNAMICCCSUBSTATION primary key (SubStationID)
);

create table CCSUBSTATIONSUBBUSLIST  (
   SubStationID         numeric                          not null,
   SubStationBusID      numeric                          not null,
   DisplayOrder         numeric                          not null,
   constraint PK_CCSUBSTATIONSUBBUSLIST primary key (SubStationID, SubStationBusID)
);

/* @error ignore-begin */
create table CAPCONTROLAREA (
   AreaID               numeric              not null,
   constraint PK_CAPCONTROLAREA primary key (AreaID)
);
go

alter table CAPCONTROLAREA
   add constraint FK_CAPCONTR_REFERENCE_YUKONPAO foreign key (AreaID)
      references YukonPAObject (PAObjectID);

/* Start YUK-5021 */
alter table dynamicccarea
   add constraint FK_ccarea_Dynccarea foreign key (areaID)
      references Capcontrolarea (areaID);
go
/* End YUK-5021 */

alter table CCSUBAREAASSIGNMENT drop constraint FK_CCSUBARE_CAPCONTR;
go

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLAREA (AreaID);
/* @error ignore-end */

select
	paoname as SubBusName,
	paobjectid as subbusId,
	case
		when (paoname like '%- BK%')
			then substring(paoname,1,len(paoname) - 5)
		else
			case
				when (paoname like '%Bnk #%')
					then substring(paoname,1,len(paoname) - 7)
				else
					substring(paoname,1,len(paoname))
			end
		end as CCsubStationName
into
	 #mySubstation
from
	yukonpaobject
where
	type = 'CCSUBBUS';

/* @start-block */
declare @ccsubstationname varchar(60);

declare substation_curs cursor for (select distinct(CCsubStationName) from #mySubstation);
open substation_curs;
fetch substation_curs into @ccsubstationname;

while (@@fetch_status = 0)
	begin
		insert into yukonpaobject (paobjectid, category, paoclass, paoname, type, description, disableflag, paostatistics)
		select 
			Max(paobjectid) + 1,
			'CAPCONTROL',
			'CAPCONTROL',
			'S: ' + @ccsubstationname,
			'CCSUBSTATION',
			'(none)',
			'N',
			'-----' 
		from 
			yukonpaobject;
		insert into capcontrolsubstation (substationid)
		select
			Max(paobjectid)
		from 
			yukonpaobject;
		fetch substation_curs into @ccsubstationname;
	end
close substation_curs;
deallocate substation_curs;
/* @end-block */

select 
	s.*
	, yp.paobjectid as Substationid
	, yp1.paoname as areaname
	, yp1.paobjectid as areaId
into 
	#mySubstation2
from
	yukonpaobject yp
	, #mySubstation s
	, yukonpaobject yp1
	, ccsubareaassignment sa
where
	yp.paoname = 'S: ' + s.ccsubstationname
	and yp.type = 'CCSUBSTATION'
	and s.subbusid = sa.substationbusid
	and sa.areaid = yp1.paobjectid;

select 
	*
into
	#ccsubareaassignment_backup
from 
	ccsubareaassignment;

alter table ccsubareaassignment drop constraint FK_CCSUBARE_CAPSUBAREAASSGN;

update 
	ccsubareaassignment
set 
	ccsubareaassignment.substationbusid=#mySubstation2.substationid
from 
	ccsubareaassignment
	, #mySubstation2
where 
	ccsubareaassignment.substationbusid = #mySubstation2.subbusid;

/* @start-block */
declare @ccsubstationid numeric;
declare @lastsubstationid numeric;
declare @ccsubbusid numeric;
declare @index numeric;

declare substation_curs cursor for (select subbusid, substationid from #mySubstation2);
open substation_curs;
fetch substation_curs into @ccsubbusid, @ccsubstationid;
set @index = 1;
while (@@fetch_status = 0)
    begin
		insert into ccsubstationsubbuslist (substationid,substationbusid, displayorder)
		select 
			@ccsubstationid
			, @ccsubbusid
			, @index ;
		set @lastsubstationid = @ccsubstationid;
		fetch substation_curs into @ccsubbusid, @ccsubstationid;
		if (@lastsubstationid = @ccsubstationid)
			set @index = @index + 1;
		else
			set @index = 1;
	end

close substation_curs;
deallocate substation_curs;
/* @end-block */

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_CAPSUBAREAASSGN foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID);
         
drop table #mySubstation;
drop table #mySubstation2;
drop table #ccsubareaassignment_backup;

alter table DYNAMICCCSUBSTATION
   add constraint FK_DYNAMICC_REFERENCE_CAPCONTR foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID);

alter table CAPCONTROLSUBSTATION
   add constraint FK_CAPCONTR_REF_YUKONPA2 foreign key (SubstationID)
      references YukonPAObject (PAObjectID)
          on delete cascade;
go

alter table CAPCONTROLSPECIALAREA
   add constraint FK_CAPCONTR_YUKONPAO2 foreign key (AreaID)
      references YukonPAObject (PAObjectID);
go


alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSUBSTA_CAPCONTR foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID);
go

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSUBSTA_REFERENCE_CAPCONTR foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);
go
/* End YUK-4707 */

drop view CCINVENTORY_VIEW;
go

create view CCINVENTORY_VIEW (Region, SubName, FeederName, subId, substationid, fdrId, CBCName, cbcId, capbankname, bankId, CapBankSize, Sequence, ControlStatus, SWMfgr, SWType, ControlType, Protocol, IPADDRESS, SlaveAddress, LAT, LON, DriveDirection, OpCenter, TA) as
SELECT yp4.paoname AS Region, yp3.PAOName AS SubName, yp2.PAOName AS FeederName, yp3.PAObjectID AS subId, ssl.substationid AS substationid, yp2.PAObjectID AS fdrId, 
                      yp.PAOName AS CBCName, yp.PAObjectID AS cbcId, yp1.PAOName AS capBankName, yp1.PAObjectID AS bankId, cb.BANKSIZE AS CapBankSize, 
                      fb.ControlOrder AS Sequence, dcb.ControlStatus, cb.SwitchManufacture AS SWMfgr, cb.TypeOfSwitch AS SWType, 
                      cb.OPERATIONALSTATE AS ControlType, cb.ControllerType AS Protocol, pts.IPADDRESS, da.SlaveAddress, 
                                capa.latitude AS LAT, capa.longitude AS LON, capa.drivedirections AS DriveDirection, 
                                cb.maplocationid AS OpCenter, capa.maintenanceareaid AS TA
FROM CAPBANK cb INNER JOIN
                      YukonPAObject yp ON yp.PAObjectID = cb.CONTROLDEVICEID INNER JOIN
                      YukonPAObject yp1 ON yp1.PAObjectID = cb.DEVICEID INNER JOIN
                      DynamicCCCapBank dcb ON dcb.CapBankID = yp1.PAObjectID INNER JOIN
                      STATE s ON s.STATEGROUPID = 3 AND dcb.ControlStatus = s.RAWSTATE INNER JOIN
                      CCFeederBankList fb ON fb.DeviceID = cb.DEVICEID INNER JOIN
                      YukonPAObject yp2 ON yp2.PAObjectID = fb.FeederID INNER JOIN
                      CCFeederSubAssignment sf ON fb.FeederID = sf.FeederID INNER JOIN
                      YukonPAObject yp3 ON yp3.PAObjectID = sf.SubStationBusID INNER JOIN
                      ccsubstationsubbuslist ssl on ssl.substationbusid = yp3.paobjectid INNER JOIN
                      ccsubareaassignment sa on sa.substationbusid = ssl.substationid INNER JOIN
                      yukonpaobject yp4 on yp4.paobjectid = sa.areaid INNER JOIN
                      DeviceDirectCommSettings ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
                      PORTTERMINALSERVER pts ON pts.PORTID = ddcs.PORTID INNER JOIN
                      DeviceCBC cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      capbankadditional capa on capa.deviceid = cb.deviceid;
go

drop view CCOPERATIONS_VIEW;
go

create view CCOPERATIONS_VIEW (cbcName, capbankname, opTime, operation, confTime, confStatus, feederName, feederId, subName, subBusId, substationid, region, BANKSIZE, protocol, ipAddress, serialNum, SlaveAddress, kvarAfter, kvarChange, kvarBefore) as
SELECT 
      yp3.PAOName AS cbcName, yp.PAOName AS capbankname, el.DateTime AS opTime, el.Text AS operation, 
      el2.DateTime AS confTime, el2.Text AS confStatus, yp1.PAOName AS feederName, yp1.PAObjectID AS feederId, 
        yp2.PAOName AS subName, yp2.PAObjectID AS subBusId, ssl.substationid AS substationid, yp4.paoname AS region, cb.BANKSIZE, 
        cb.ControllerType AS protocol, p.Value AS ipAddress, cbc.SERIALNUMBER AS serialNum, da.SlaveAddress, 
        el2.kvarAfter, el2.kvarChange, el2.kvarBefore
FROM   
      (SELECT op.LogID AS oid, MIN(aaa.confid) AS cid FROM
              (SELECT LogID, PointID FROM CCEventLog 
        WHERE Text LIKE '%Close sent%' OR Text LIKE '%Open sent%') op
        LEFT OUTER JOIN 
        (SELECT el.LogID AS opid, MIN(el2.LogID) AS confid 
        FROM CCEventLog el INNER JOIN CCEventLog el2 ON el2.PointID = el.PointID AND el.LogID < el2.LogID 
        LEFT OUTER JOIN
        (SELECT a.LogID AS aid, MIN(b.LogID) AS next_aid FROM 
        CCEventLog a INNER JOIN CCEventLog b ON a.PointID = b.PointID AND a.LogID < b.LogID 
        WHERE (a.Text LIKE '%Close sent,%' OR a.Text LIKE '%Open sent,%') 
        AND (b.Text LIKE '%Close sent,%' OR b.Text LIKE '%Open sent,%')
        GROUP BY a.LogID) el3 ON el3.aid = el.LogID 
      WHERE (el.Text LIKE '%Close sent,%' OR el.Text LIKE '%Open sent,%') 
        AND (el2.Text LIKE 'Var: %') AND (el2.LogID < el3.next_aid) 
      OR (el.Text LIKE '%Close sent,%' OR el.Text LIKE '%Open sent,%') 
        AND (el2.Text LIKE 'Var: %') AND (el3.next_aid IS NULL)
        GROUP BY el.LogID)  aaa ON op.LogID = aaa.opid
GROUP BY op.LogID) OpConf INNER JOIN
        CCEventLog el ON el.LogID = OpConf.oid LEFT OUTER JOIN
        CCEventLog el2 ON el2.LogID = OpConf.cid INNER JOIN
        POINT ON POINT.POINTID = el.PointID INNER JOIN
        DynamicCCCapBank ON DynamicCCCapBank.CapBankID = POINT.PAObjectID INNER JOIN
        YukonPAObject yp ON yp.PAObjectID = DynamicCCCapBank.CapBankID INNER JOIN
        YukonPAObject yp1 ON yp1.PAObjectID = el.FeederID INNER JOIN
        YukonPAObject yp2 ON yp2.PAObjectID = el.SubID INNER JOIN
        CAPBANK cb ON cb.DEVICEID = DynamicCCCapBank.CapBankID LEFT OUTER JOIN
        DeviceDirectCommSettings ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
        YukonPAObject yp3 ON yp3.PAObjectID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        DeviceCBC cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        (SELECT EntryID, PAObjectID, Owner, InfoKey, Value, UpdateTime
        FROM DynamicPAOInfo WHERE (InfoKey LIKE '%udp ip%')) 
        p ON p.PAObjectID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        ccsubstationsubbuslist as ssl on ssl.substationbusid = el.subid  LEFT OUTER JOIN
        ccsubareaassignment as csa on csa.substationbusid = ssl.substationid left outer join 
        YukonPAObject AS yp4 ON yp4.paobjectid = csa.areaid;
go

/* Start YUK-4730 */
if exists (select 1
            from  sysobjects
           where  id = object_id('JOB')
            and   type = 'U')
   drop table JOB;
go

create table JOB (
   JobID                int                  not null,
   BeanName             varchar(250)         not null,
   Disabled             char(1)              not null,
   UserID               numeric              not null,
   constraint PK_JOB primary key (JobID)
);
go

alter table JOB
   add constraint FK_Job_YukonUser foreign key (UserID)
      references YukonUser (UserID);
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBPROPERTY')
            and   type = 'U')
   drop table JOBPROPERTY;
go

create table JOBPROPERTY (
   JobPropertyID          numeric              not null,
   JobID                int                  not null,
   name                 text                 not null,
   value                text                 not null,
   constraint PK_JOBPROPERTY primary key (JobPropertyID)
);
go

alter table JOBPROPERTY
   add constraint FK_JobProperty_Job foreign key (JobID)
      references JOB (JobID)
          on delete cascade;
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBSCHEDULEDONETIME')
            and   type = 'U')
   drop table JOBSCHEDULEDONETIME;
go

create table JOBSCHEDULEDONETIME (
   JobID                int                  not null,
   StartTime            datetime             not null,
   constraint PK_JOBSCHEDULEDONETIME primary key (JobID)
);
go

alter table JOBSCHEDULEDONETIME
   add constraint FK_JobScheduledOneTime_Job foreign key (JobID)
      references JOB (JobID)
         on delete cascade;
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBSCHEDULEDREPEATING')
            and   type = 'U')
   drop table JOBSCHEDULEDREPEATING;
go

create table JOBSCHEDULEDREPEATING (
   JobID                int                  not null,
   CronString           text                 not null,
   constraint PK_JobScheduledRepeating primary key (JobID)
);
go

alter table JOBSCHEDULEDREPEATING
   add constraint FK_JOBSCHED_REFERENCE_JOB foreign key (JobID)
      references JOB (JobID)
          on delete cascade;
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBSTATUS')
            and   type = 'U')
   drop table JOBSTATUS;
go

create table JOBSTATUS (
   JobStatusID          int                  not null,
   JobID                int                  not null,
   StartTime            datetime             not null,
   StopTime             datetime             null,
   JobState             varchar(50)          null,
   message              text                 null,
   constraint PK_JOBSTATUS primary key (JobStatusID)
);
go

alter table JOBSTATUS
   add constraint FK_JobStatus_Job foreign key (JobID)
      references JOB (JobID)
          on delete cascade;
go
/* End YUK-4730 */

/* Begin YUK-4771 (formerly YUK-4716) */

create table CCSTRATEGYTIMEOFDAY  (
   StrategyID           numeric                          not null,
   StartTimeSeconds     numeric                          not null,
   PercentClose         numeric                          not null,
   constraint PK_STRAT_TOD primary key (StrategyID, StartTimeSeconds)
);

alter table CCSTRATEGYTIMEOFDAY
   add constraint FK_STRAT_TOD_CCSTRAT foreign key (StrategyID)
      references CapControlStrategy (StrategyID);
/* End YUK-4716 */

/* Start YUK-4763 */
alter table DynamicCCFeeder add LastWattPointTime datetime;
go
update DynamicCCFeeder set LastWattPointTime = '1990-01-01 00:00:00';
go
alter table DynamicCCFeeder alter column LastWattPointTime datetime not null;
go
alter table DynamicCCFeeder add LastVoltPointTime datetime;
go
update DynamicCCFeeder set LastVoltPointTime = '1990-01-01 00:00:00';
go
alter table DynamicCCFeeder alter column LastVoltPointTime datetime not null;
go

alter table DynamicCCSubstationbus add LastWattPointTime datetime;
go
update DynamicCCSubstationbus set LastWattPointTime = '1990-01-01 00:00:00';
go
alter table DynamicCCSubstationbus alter column LastWattPointTime datetime not null;
go
alter table DynamicCCSubstationbus add LastVoltPointTime datetime;
go
update DynamicCCSubstationbus set LastVoltPointTime = '1990-01-01 00:00:00';
go
alter table DynamicCCSubstationbus alter column LastVoltPointTime datetime not null;
go 

alter table capcontrolstrategy add likeDayFallBack char(1);
go
update capcontrolstrategy set likeDayFallBack = 'N';
go
alter table capcontrolstrategy alter column likeDayFallBack char(1) not null;
go 
/* End YUK-4763*/

/* Start YUK-4759 */
alter table SubstationToRouteMapping drop constraint FK_Sub_Rte_Map_RteID;
go

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_RteID foreign key (RouteID)
      references Route (RouteID)
          on delete cascade;
go
/* End YUK-4759 */

/* Start YUK-4721 */
alter table DynamicPAOStatistics drop constraint FK_PASt_YkPA;
go

alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID)
          on delete cascade;
go
/* End YUK-4721 */

/* Start YUK-4638 */
update devicetypecommand set devicetype ='MCT-430S4' where devicetype = 'MCT-430S';
go
insert into command values(-136, 'putconfig emetcon timezone ?''Enter Timezone (et|ct|mt|pt OR #offset)''', 'Write Timezone to Meter', 'MCT-430A'); 
go

INSERT INTO DEVICETYPECOMMAND VALUES (-676, -111, 'MCT-430A', 26, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-677, -2, 'MCT-430A', 27, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-678, -83, 'MCT-430A', 28, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-679, -136, 'MCT-430A', 29, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-680, -111, 'MCT-430S4', 26, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-681, -2, 'MCT-430S4', 27, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-682, -83, 'MCT-430S4', 28, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-683, -136, 'MCT-430S4', 29, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-684, -111, 'MCT-430SL', 26, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-685, -2, 'MCT-430SL', 27, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-686, -83, 'MCT-430SL', 28, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-687, -136, 'MCT-430SL', 29, 'Y', -1);

insert into devicetypecommand values(-689, -136, 'MCT-410CL', 30, 'N', -1);
insert into devicetypecommand values(-690, -136, 'MCT-410FL', 30, 'N', -1);
insert into devicetypecommand values(-691, -136, 'MCT-410GL', 30, 'N', -1);
insert into devicetypecommand values(-692, -136, 'MCT-410IL', 30, 'N', -1);
insert into devicetypecommand values(-693, -136, 'MCT-470', 32, 'N', -1); 
/* End YUK-4638 */

/* @error ignore-begin */
/* Start YUK-4745 */
insert into YukonRole values(-211,'CI Curtailment','Operator','Operator access to C&I Curtailment');
insert into YukonRoleProperty values(-21100,-211,'CI Curtailment Label','CI Curtailment','The operator specific name for C&I Curtailment');
/* End YUK-4745 */

/* Start YUK-4906, YUK-5522 */
alter table MSPVendor add MaxReturnRecords int;
go
update MSPVendor set MaxReturnRecords = 10000 where MaxReturnRecords is null;
go
alter table MSPVendor alter column MaxReturnRecords int not null;
go

alter table MSPVendor add RequestMessageTimeout int;
go
update MSPVendor set RequestMessageTimeout = 120000 where RequestMessageTimeout is null;
go
alter table MSPVendor alter column RequestMessageTimeout int not null;
go

alter table MSPVendor add MaxInitiateRequestObjects int;
go
update MSPVendor set MaxInitiateRequestObjects = 15 where MaxInitiateRequestObjects is null;
go
alter table MSPVendor alter column MaxInitiateRequestObjects int not null;
go

alter table MSPVendor add TemplateNameDefault varchar(50);
go
update MSPVendor set TemplateNameDefault = '*Default Template' where TemplateNameDefault is null;
go
alter table MSPVendor alter column TemplateNameDefault varchar(50) not null;
go
/* End YUK-4906, YUK-5522 */
/* @error ignore-end */

/* Start YUK-4733 */
delete yukongrouprole where rolepropertyID = -10308;
delete yukonuserrole where rolepropertyID = -10308;
delete yukonroleproperty where rolepropertyID = -10308;
update yukonroleproperty set keyName = 'Control disconnect', description = 'Allow the ability to control a disconnect to a device' where RolePropertyID = -10309;
/* End YUK-4733 */

/* Start YUK-2924 */
update command set label = 'Configure LEDS (load, test, report)' where commandid = -49;
update command set label = 'Configure LEDS (load, test, report)' where commandid = -97;
/* End YUK-2924 */

/* Start YUK-4813 */
alter table cceventlog add capbankstateinfo varchar(20);
go
update cceventlog set capbankstateinfo = 'N/A';
go
alter table cceventlog alter column capbankstateinfo varchar(20) not null ;
go
/* End YUK-4813 */

/* Start YUK-4762, YUK-4969 */
create table CAPCONTROLCOMMENT  (
   CommentID            int                  not null,
   PaoID                numeric              not null,
   UserID               numeric              not null,
   Action               varchar(50)          not null,
   CommentTime          datetime             not null,
   CapComment              varchar(500)         not null,
   Altered              varchar(3)           not null,
   constraint PK_CAPCONTROLCOMMENT primary key (CommentID)
);
go

alter table CAPCONTROLCOMMENT 
   add constraint FK_CAPCONTR_REFERENCE_YUKONPA2 foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go

alter table CAPCONTROLCOMMENT 
   add constraint FK_CAPCONTR_REFERENCE_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);
go
/* End YUK 4762, YUK-4969 */

/* Start YUK-4810, YUK-5434 */
insert into YukonRoleProperty values(-20207,-202,'Enable Auto Archiving','true','Allows a user to setup automatic archiving on their yukon system pertaining to the move in/move out interface');

if exists (select 1
            from  sysobjects
           where  id = object_id('DEVICEEVENT')
            and   type = 'U')
   drop table DEVICEEVENT;
go

create table DEVICEEVENT (
   DeviceEventID        numeric              not null,
   DeviceID             numeric              not null,
   TimeStamp            datetime             not null,
   DeviceEventComment   varchar(50)          not null,
   constraint PK_DEVICEEVENT primary key (DeviceEventID)
);
go
/* End YUK-4810, YUK-5434 */

/* Begin YUK-4815 */
insert into YukonRoleProperty values (-100205, -1002, 'Capbank Fixed/Static Text', 'Fixed', 'The text to display for fixed/static capbanks');
/* End YUK-4815 */

/* Begin YUK-4846 */
insert into YukonRoleProperty values(-20909,-209,'Purchasing Access','false','Activates the purchasing section of the inventory module.'); 
/* End YUK-4846 */

/* Start YUK-4839 */
insert into YukonRoleProperty values(-20163,-201,'Allow Account Editing','true','Can be used to disable the ability to edit and delete customer account information.');
/* End YUK-4839 */

/* Start YUK-4837 */
insert into YukonRoleProperty values(-90008,-900,'Allow Gear Change for Stop','false','Activates the ability to change gears as part of manually stopping a load program'); 
/* End YUK-4837 */

/* Start YUK-4825 */
insert into command values(-137, 'putconfig emetcon outage ?''Enter number of cycles before outage is recorded''', 'Write Threshold (number of cycles before outage recorded) to Meter', 'MCT-410IL');
insert into devicetypecommand values(-694, -137, 'MCT-410CL', 31, 'N', -1);
insert into devicetypecommand values(-695, -137, 'MCT-410FL', 31, 'N', -1);
insert into devicetypecommand values(-696, -137, 'MCT-410GL', 31, 'N', -1);
insert into devicetypecommand values(-697, -137, 'MCT-410IL', 31, 'N', -1);
/* End YUK-4825 */

/* Start YUK-4810 */
insert into YukonRoleProperty values(-20208,-202,'Enable Move In Move Out Wizard','true','Allows a user to calculate a readings for a meter that is being moving in or out of service');
/* End YUK-4810 */

/* Start YUK-4858 */
create table PEAKREPORT (
   resultID             int                  not null,
   deviceID             numeric              not null,
   channel              int                  not null,
   peakType             varchar(50)          not null,
   runType              varchar(50)          not null,
   runDate              datetime             not null,
   resultString         varchar(1500)        not null,
   constraint PK_PEAKREPORT primary key (resultID)
);
go
/* End YUK-4858 */

/* Start YUK-4860 */
insert into command values(-138, 'getvalue peak channel 2', 'Read Peak (Channel 2)', 'MCT-410IL');
insert into command values(-139, 'getvalue peak channel 3', 'Read Peak (Channel 2)', 'MCT-410IL');

insert into devicetypecommand values(-698, -138, 'MCT-410CL', 32, 'N', -1);
insert into devicetypecommand values(-699, -138, 'MCT-410FL', 32, 'N', -1);
insert into devicetypecommand values(-700, -138, 'MCT-410GL', 32, 'N', -1);
insert into devicetypecommand values(-701, -138, 'MCT-410IL', 32, 'N', -1);

insert into devicetypecommand values(-702, -139, 'MCT-410CL', 32, 'N', -1);
insert into devicetypecommand values(-703, -139, 'MCT-410FL', 32, 'N', -1);
insert into devicetypecommand values(-704, -139, 'MCT-410GL', 32, 'N', -1);
insert into devicetypecommand values(-705, -139, 'MCT-410IL', 32, 'N', -1); 
/* End YUK-4860 */

/* Start YUK-4859, YUK-5197 */
IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[CCCAP_INVENTORY_VIEW]'))
DROP VIEW [CCCAP_INVENTORY_VIEW];
go
create view CCCAP_INVENTORY_VIEW as
SELECT
	yp4.paoname AS REGION
	, cb.maplocationid AS OP_CENTER
	, capa.maintenanceareaid AS TA_AREA
	, yp5.paoname as SUBSTATION_NAME
	, yp3.PAOName AS SUBSTATION_BANK_NAME
	, yp2.PAOName AS BREAKER_NUMBER
	, yp1.PAOName AS BANK_NAME
	, cb.BANKSIZE AS BANK_SIZE
	, cb.OPERATIONALSTATE AS CONTROL_TYPE
	, cb.SwitchManufacture AS SWITCH_MANUFACTURER
	, cb.TypeOfSwitch AS SWITCH_TYPE
	, yp.PAOName AS CBC_NAME
	, p.Value AS IP_ADDRESS
	, da.SlaveAddress AS DNP_ADDRESS
	, capa.driveDirections AS DRIVE_DIRECTION
	, capa.latitude AS LATITUDE
	, capa.longitude AS LONGITUDE
FROM
	CAPBANK cb
	INNER JOIN YukonPAObject yp1 ON yp1.PAObjectID = cb.DEVICEID
	LEFT OUTER JOIN YukonPAObject yp ON cb.CONTROLDEVICEID = yp.PAObjectID AND cb.CONTROLDEVICEID > 0
	LEFT OUTER JOIN CCFeederBankList fb ON fb.DeviceID = cb.DEVICEID
	LEFT OUTER JOIN YukonPAObject yp2 ON yp2.PAObjectID = fb.FeederID
	LEFT OUTER JOIN CCFeederSubAssignment sf ON fb.FeederID = sf.FeederID
	LEFT OUTER JOIN YukonPAObject yp3 ON yp3.PAObjectID = sf.SubStationBusID
	LEFT OUTER JOIN ccsubstationsubbuslist ss on ss.substationbusid = yp3.paobjectid
	LEFT OUTER JOIN YukonPAObject yp5 ON yp5.PAObjectID = ss.SubStationID
	left outer join ccsubareaassignment sa on ss.substationid = sa.substationbusid
	left outer join YukonPAObject yp4 on yp4.paobjectid = sa.areaid
	LEFT OUTER JOIN DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID
	LEFT OUTER JOIN (SELECT EntryID, PAObjectID, Owner, InfoKey, Value, UpdateTime
					  FROM DynamicPAOInfo
					  WHERE (InfoKey LIKE '%udp ip%')) p ON p.PAObjectID = yp.PAObjectID
	left outer join capbankadditional capa on capa.deviceid = cb.deviceid; 
go
/* End YUK-4859, YUK-5197 */

/* Start YUK-4862 */
insert into YukonRoleProperty values(-20205,-202,'Enable Device Group','true','Allows access to change device groups for a device');
/* End YUK-4862 */

/* Start YUK-4866 */
delete from
	DeviceGroup
where
	devicegroupid in (
	select
		a.devicegroupid
	from
		DeviceGroup a left outer join DeviceGroupMember b on a.devicegroupid = b.devicegroupid
	where
		a.devicegroupid in (5,6,7)
		and b.devicegroupid is null
		and a.devicegroupid not in (
			select distinct
				z.parentdevicegroupid 
			from 
				devicegroup z 
			where 
				z.parentdevicegroupid in (5,6,7))
	);
/* End YUK-4866 */

/* Start YUK-4876 */
/* @error ignore-begin */
INSERT INTO DEVICETYPECOMMAND VALUES (-706, -52, 'Repeater 902', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-707, -3, 'Repeater 902', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-708, -53, 'Repeater 902', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-709, -54, 'Repeater 902', 4, 'Y', -1); 
/* @error ignore-end */
/* End YUK-4876 */

/* Start YUK-4962 */
insert into YukonRoleProperty values(-20204,-202,'Enable TOU','true','Allows access to TOU(Time of use) data'); 
/* End YUK-4962 */

/* Start YUK-4977 */
update YukonRoleProperty
	set KeyName = replace(KeyName,'ardwares','ardware')
where
	(KeyName like '%Hardwares%'
	or KeyName like '%hardwares%')
	and RolePropertyID < 0;

update YukonRoleProperty
	set DefaultValue = replace(DefaultValue,'ardwares','ardware')
where
	(DefaultValue like '%Hardwares%'
	or DefaultValue like '%hardwares%')
	and RolePropertyID < 0;

update YukonRoleProperty
	set Description = replace(Description,'ardwares','ardware')
where
	(Description like '%Hardwares%'
	or Description like '%hardwares%')
	and RolePropertyID < 0;
/* End YUK-4977 */

/* Start YUK-4997 */
update capbank set operationalstate = 'StandAlone' where lower(operationalstate) = 'stand alone'; 
/* End YUK-4997 */

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID);
go

/* Start YUK-4746 */
alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_CAPCONTR2 foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID);
go
/* End YUK-4746 */

/* Start YUK-5020, YUK-5217 */
/* @error ignore-begin */
alter table DeviceTypeCommand
drop constraint "FK_DevCmd_Grp ";

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);
/* @error ignore-end */
/* End YUK-5020, YUK-5217 */

/* Start YUK-5022 */
alter table DYNAMICBILLINGFIELD
   add constraint FK_DBF_REF_BFF foreign key (FormatID)
      references BillingFileFormats (FormatID)
          on delete cascade;
go
alter table DYNAMICBILLINGFORMAT
   add constraint FK_DYNAMICB_REF_BILLI_BILLINGF foreign key (FormatID)
      references BillingFileFormats (FormatID);
go
/* End YUK-5022 */

/* Start YUK-5023 */
/* @error ignore-begin */
alter table EventWorkOrder
     drop constraint pk_eventwrkordr;
alter table EventWorkOrder
     add constraint PK_EVENTWORKORDER primary key (EventID);
/* @error ignore-end */
/* End YUK-5023 */

/* Start YUK-5026 */
create index INDX_PAOBJECTID_POFFSET on POINT (
	PAObjectID ASC,
	POINTOFFSET ASC
);
go
create index INDX_PAOBJECTID_POINTID on POINT (
	PAObjectID ASC,
	POINTID ASC
);
go
create index INDX_POFFSET_POINTTYPE on POINT (
	POINTOFFSET ASC,
	POINTTYPE ASC
);
go
/* End YUK-5025 */

/* Start YUK-5027 */
create index INDX_UOMID_POINTID on POINTUNIT (
	UOMID ASC,
	POINTID ASC
);
go
/* End YUK-5027 */

/* Start YUK-5028 */
/* @error ignore-begin */
alter table ScheduleTimePeriod
    drop constraint pk_schedtimeperiod;
alter table ScheduleTimePeriod
   add constraint PK_SCHEDULETIMEPERIOD primary key (TimePeriodID);
/* @error ignore-end */
/* End YUK-5028 */

/* Start YUK-5047 */
UPDATE YUKONROLEPROPERTY
SET    
       ROLEID         = -700,
       KEYNAME        = 'Allow OV/UV',
       DEFAULTVALUE   = 'false',
       DESCRIPTION    = 'Allows users to toggle OV/UV usage for capbanks, substations, subs, and feeders.'
WHERE  ROLEPROPERTYID = -70008
;
/* End YUK-5047 */

/* Start YUK-5086 */
insert into yukonroleproperty values(-70017,-700, 'Add Comments', 'false', 'Allows the user to Add comments to Cap Bank objects.');
insert into yukonroleproperty values(-70018,-700, 'Modify Comments', 'false', 'Allows the user to Modify comments on Cap Bank objects.');
/* End YUK-5086 */

/* Start YUK-4844, YUK-5114 */
update DeviceGroup set GroupName = ' ' where GroupName is null or GroupName = '' or DeviceGroupID = 0;
/* End YUK-4844, YUK-5114 */

/* Start YUK-5152 */
update YukonRoleProperty set keyName = 'Load Management Reports Group', Description = 'Access to Load Management group reports.' where rolepropertyid = -10906;
update YukonRoleProperty set keyName = 'Load Management Reports Group Label' where rolepropertyid = -10916;
/* End YUK-5152 */

/* Start YUK-5173 */
delete from dynamicpointalarming where tags=0;
/* End YUK-5173 */

/* Start YUK-5164 */
insert into yukonroleproperty values( -100206, -1002, 'Daily/Max/Total Operation Count', 'true', 'is Daily/Max/Total Operation Count displayed.');
update yukonroleproperty SET DefaultValue = 'false' where RolePropertyID = -100204;
/* End YUK-5164 */

/* Start YUK-5113 */
Update point set pointoffset = 100 where pointid = -100;
/* End YUK-5113 */

/* Start YUK-5213 */
insert into yukonroleproperty values(-70019,-700, 'System Wide Controls', 'false', 'Allow system wide controls');
/* End YUK-5213 */

/* Start YUK-5195 */
update yukonroleproperty SET defaultvalue = 'false' WHERE rolepropertyid = -100102;
update yukonroleproperty SET defaultvalue = 'false' WHERE rolepropertyid = -100104; 
insert into yukonroleproperty values ( -100107, -1001, 'Watt/Volt', 'true', 'display Watts/Volts');
insert into yukonroleproperty values ( -100108, -1001, 'Three Phase', 'false', 'display 3-phase data for feeder'); 
/* End YUK-5195 */

/* Start YUK-5025 */
/* @error ignore-begin */
alter table LMHardwareBase
   add constraint FK_LMHrdB_Rt foreign key (RouteID)
      references Route (RouteID);
/* @error ignore-end */
/* End YUK-5025 */

/* Start YUK-5248 */
update
	YukonPAObject
set 
	PAOName = substring(PAOName,0,len(PAOName)-3)
where
	PAObjectID in (
	select
		PAObjectID
	from
		YukonPAObject
	where
		PAOName like '%[_]REM'
		and DisableFlag = 'Y'
	);
/* End YUK-5248 */

/* Start YUK-5250 */
ALTER TABLE MSPVENDOR DROP COLUMN UNIQUEKEY;
ALTER TABLE MSPVENDOR DROP COLUMN TIMEOUT;

create unique index INDEX_1 on MSPVendor (
CompanyName ASC,
AppName ASC
);
/* End YUK-5250 */

/* Start YUK-5261 */
alter table cceventlog add aVar float;
go
update cceventlog set aVar = 0;
go
alter table cceventlog alter column aVar float not null;
go
alter table cceventlog add bVar float;
go
update cceventlog set bVar = 0;
go
alter table cceventlog alter column bVar float not null;
go
alter table cceventlog add cVar float;
go
update cceventlog set cVar = 0;
go
alter table cceventlog alter column cVar float not null;
/* End YUK-5261 */

/* Start YUK-5263 */
alter table capcontrolfeeder add controlFlag char(1);
go
update capcontrolfeeder set controlFlag = 'N';
go
alter table capcontrolfeeder alter column controlFlag char(1) not null;
go
alter table capcontrolsubstationbus add controlFlag char(1);
go
update capcontrolsubstationbus set controlFlag = 'N';
go
alter table capcontrolsubstationbus alter column controlFlag char(1) not null; 
/* End YUK-5263 */

/* Start YUK-5310 */
/* @error ignore-begin */
ALTER TABLE SUBSTATION
 DROP CONSTRAINT FK_SUBSTATI_FK_SUB_RT_ROUTE;

alter table Substation
   add constraint FK_Sub_Rt foreign key (LMRouteID)
      references Route (RouteID);
/* @error ignore-end */
/* End YUK-5310 */

/* Start YUK-5311 */
/* @error ignore-begin */
create table ApplianceDualStageAirCondTemp  (
   ApplianceID              numeric                not null,
   StageOneTonnageID          numeric,
   StageTwoTonnageID          numeric,
   TypeID            numeric
);
go
ALTER TABLE APPLIANCEDUALSTAGEAIRCOND
 DROP CONSTRAINT FK_DUALSTAGE_APPLNCBSE;
go
ALTER TABLE APPLIANCEDUALSTAGEAIRCOND
 DROP CONSTRAINT FK_DUALSTAGE_STGTWONTRY;
go
ALTER TABLE APPLIANCEDUALSTAGEAIRCOND
 DROP CONSTRAINT FK_DUALSTAGE_STNENTRY;
go
ALTER TABLE APPLIANCEDUALSTAGEAIRCOND
 DROP CONSTRAINT FK_DUALSTAGE_TYPENTRY;
go
INSERT INTO APPLIANCEDUALSTAGEAIRCONDTEMP (
   APPLIANCEID, 
   STAGEONETONNAGEID,
   STAGETWOTONNAGEID,  
   TYPEID) 
select
   ApplianceID,
   StageOneTonnageID,
   StateTwoTonnageID,
   TypeID   
from
   APPLIANCEDUALSTAGEAIRCOND
;
go
drop table ApplianceDualStageAirCond;
go
sp_rename 'ApplianceDualStageAirCondTemp','ApplianceDualStageAirCond';
go
alter table ApplianceDualStageAirCond
   add constraint PK_APPLIANCEDUALSTAGEAIRCOND primary key (ApplianceID);
go   
alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID);
go
alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STNENTRY foreign key (StageOneTonnageID)
      references YukonListEntry (EntryID);
go
alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STGTWONTRY foreign key (StageTwoTonnageID)
      references YukonListEntry (EntryID);
go
alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);
go
/* @error ignore-end */
/* End YUK-5311 */

/* Start YUK-5316 */
/* @error ignore-begin */
ALTER TABLE CCURTCURTAILMENTEVENT
 ADD CCURTPROGRAMTYPEIDTEMP  NUMERIC;
go
ALTER TABLE CCURTCURTAILMENTEVENT
 ADD IDENTIFIERTEMP  NUMERIC;
go

UPDATE CCURTCURTAILMENTEVENT
SET    
       CCURTPROGRAMTYPEIDTEMP      = CCURTPROGRAMTYPEID;
go
UPDATE CCURTCURTAILMENTEVENT
SET    
       IDENTIFIERTEMP      = IDENTIFIER;
go

ALTER TABLE CCURTCURTAILMENTEVENT DROP COLUMN CCURTPROGRAMTYPEID;
go
ALTER TABLE CCURTCURTAILMENTEVENT DROP COLUMN IDENTIFIER;
go

sp_rename 'CCURTCURTAILMENTEVENT.CCURTPROGRAMTYPEIDTEMP','CCURTPROGRAMTYPEID';
go
ALTER TABLE CCURTCURTAILMENTEVENT
    alter column CCURTPROGRAMTYPEID numeric NOT NULL;
go
sp_rename 'CCURTCURTAILMENTEVENT.IDENTIFIERTEMP','IDENTIFIER';
go
ALTER TABLE CCURTCURTAILMENTEVENT
    alter column IDENTIFIER numeric NOT NULL;
go
/* @error ignore-end */
/* End YUK-5316 */

/* Start YUK-5323 */
/* @error ignore-begin */
sp_rename 'PK_IMPPENDINGCOMM','PK_IMPORTPENDINGCOMM';
go
/* @error ignore-end */
/* End YUK-5323 */

/* Start YUK-5324 */
/* @error ignore-begin */
sp_rename 'PK_SUB_RTE_MAP','PK_SUBSTATIONTOROUTEMAPPING';
go
/* @error ignore-end */
/* End YUK-5324 */ 

/* Start YUK-5325 */
/* @error ignore-begin */
insert into YukonRoleProperty values(-20010,-200,'Auto Process Batch Configs','false','Automatically process batch configs using the DailyTimerTask.');
alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID); 
/* @error ignore-end */
/* End YUK-5325 */

/* Start YUK-5332 */
alter table DYNAMICPAOSTATISTICSHISTORY drop constraint FK_DYNPAOSTHIST_YKNPAO;
alter table DYNAMICPAOSTATISTICSHISTORY
   add constraint FK_DYNPAOSTHIST_YKNPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
         on delete cascade;
go

alter table MCTConfigMapping drop constraint FK_McCfgM_Dev;
alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID)
         on delete cascade;
go

alter table PROFILEPEAKRESULT drop constraint FK_PROFILEPKRSLT_DEVICE;
alter table PROFILEPEAKRESULT
   add constraint FK_PROFILEPKRSLT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade;
go
/* End YUK-5332 */

/* Start YUK-5312 */
DROP INDEX [INDX_CCRTEEPRTWIN_PWNID_PSID] ON [CCurtEEParticipantWindow];
go
DROP INDEX [INDX_CCURTEEPRWIN] ON [CCurtEEPricingWindow];
go
DROP INDEX [INDX_CCURTGROUP_ECID_GRPNM] ON [CCurtGroup];
go
DROP INDEX [INDX_CCRTGRPCSTNOTIF_GID_CID] ON [CCurtGroupCustomerNotif];
go
DROP INDEX [INDX_CCURTPRGGRP_GRPID_PRGID] ON [CCurtProgramGroup];
go

ALTER TABLE [ActivityLog]
ALTER COLUMN [Description] [varchar] (240) NULL;
GO

ALTER TABLE [BillingFileFormats]
ALTER COLUMN [FormatType] [varchar] (100) NOT NULL;
GO

ALTER TABLE [BillingFileFormats]
ALTER COLUMN [SystemFormat] [smallint] NOT NULL;
GO

ALTER TABLE [CCurtEEParticipantWindow]
ALTER COLUMN [CCurtEEParticipantSelectionID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [CCurtEEParticipantWindow]
ALTER COLUMN [CCurtEEPricingWindowID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [CCurtEEPricingWindow]
ALTER COLUMN [CCurtEEPricingID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [CCurtGroup]
ALTER COLUMN [EnergyCompanyID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [CCurtGroupCustomerNotif]
ALTER COLUMN [CCurtGroupID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [CCurtGroupCustomerNotif]
ALTER COLUMN [CustomerID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [CCurtProgramGroup]
ALTER COLUMN [CCurtGroupID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [CCurtProgramGroup]
ALTER COLUMN [CCurtProgramID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [MeterHardwareBase]
ALTER COLUMN [MeterTypeID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [Warehouse]
ALTER COLUMN [Notes] [varchar] (300) NULL;
GO

ALTER TABLE [WorkOrderBase]
ALTER COLUMN [AdditionalOrderNumber] [varchar] (24) NULL;
GO

CREATE UNIQUE NONCLUSTERED INDEX [INDX_CCRTEEPRTWIN_PWNID_PSID] ON [CCurtEEParticipantWindow] 
(
	[CCurtEEPricingWindowID] ASC,
	[CCurtEEParticipantSelectionID] ASC
);
go
CREATE UNIQUE NONCLUSTERED INDEX [INDX_CCURTEEPRWIN] ON [CCurtEEPricingWindow] 
(
	[Offset] ASC,
	[CCurtEEPricingID] ASC
);
go
CREATE UNIQUE NONCLUSTERED INDEX [INDX_CCURTGROUP_ECID_GRPNM] ON [CCurtGroup] 
(
	[EnergyCompanyID] ASC,
	[CCurtGroupName] ASC
);
go
CREATE UNIQUE NONCLUSTERED INDEX [INDX_CCRTGRPCSTNOTIF_GID_CID] ON [CCurtGroupCustomerNotif] 
(
	[CustomerID] ASC,
	[CCurtGroupID] ASC
);
go
CREATE UNIQUE NONCLUSTERED INDEX [INDX_CCURTPRGGRP_GRPID_PRGID] ON [dbo].[CCurtProgramGroup] 
(
	[CCurtProgramID] ASC,
	[CCurtGroupID] ASC
);
go
/* End YUK-5312 */

/* Start YUK-5330 */
DROP INDEX [Indx_RouteDevID] ON [Route];
go
CREATE UNIQUE NONCLUSTERED INDEX [Indx_RouteDevID] ON [Route] 
(
	[DeviceID] ASC,
	[RouteID] ASC
);
go

DROP INDEX [Indx_STATEGRP_Nme] ON [STATEGROUP];
go
CREATE UNIQUE NONCLUSTERED INDEX [Indx_STATEGRP_Nme] ON [STATEGROUP] 
(
	[NAME] ASC
);
go
/* End YUK-5330 */

/* Start YUK-5351 */
alter table capcontrolsubstationbus add voltReductionPointId numeric;
go
update capcontrolsubstationbus set voltReductionPointId = 0;
go
alter table capcontrolsubstationbus alter column voltReductionPointId numeric not null;
go
alter table capcontrolsubstation add voltReductionPointId numeric;
go
update capcontrolsubstation set voltReductionPointId = 0;
go
alter table capcontrolsubstation alter column voltReductionPointId numeric not null;
/* End YUK-5351 */

/* Start YUK-5363 */
create table CCHOLIDAYSTRATEGYASSIGNMENT  (
   PAObjectId           numeric                          not null,
   HolidayScheduleId    numeric                          not null,
   StrategyId           numeric                          not null,
   constraint PK_CCHOLIDAYSTRATEGYASSIGNMENT primary key (PAObjectId)
);

insert into HolidaySchedule values ( -1, 'No Holiday');
insert into dateOfHoliday values(-1, 'None', 1,1,1969);

insert into CCHolidayStrategyAssignment
( paobjectid, holidayscheduleid, strategyid )
select substationbusid, -1, 0 from capcontrolsubstationbus;

insert into CCHolidayStrategyAssignment
( paobjectid, holidayscheduleid, strategyid )
select feederid, -1, 0 from capcontrolfeeder;

insert into CCHolidayStrategyAssignment
( paobjectid, holidayscheduleid, strategyid )
select areaid, -1, 0 from capcontrolarea;

insert into CCHolidayStrategyAssignment
( paobjectid, holidayscheduleid, strategyid )
select areaid, -1, 0 from capcontrolspecialarea;

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHSA_PAOID foreign key (PAObjectId)
      references YukonPAObject (PAObjectID);

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHSA_SCHEDID foreign key (HolidayScheduleId)
      references HolidaySchedule (HolidayScheduleID);

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHOLIDAY_CAPCONTR foreign key (StrategyId)
      references CapControlStrategy (StrategyID);
/* End YUK-5363 */

/* Start YUK-5397 */
alter table cceventlog add stationId numeric;
go
update cceventlog set stationId = 0;
go
alter table cceventlog alter column stationId numeric not null;
go
alter table cceventlog add areaId numeric;
go
update cceventlog set areaId = 0;
go
alter table cceventlog alter column areaId numeric not null;
go
alter table cceventlog add spAreaId numeric;
go
update cceventlog set spAreaId = 0;
go
alter table cceventlog alter column spAreaId numeric not null;
go
/* End YUK-5397 */

/* Start YUK-5396 */
alter table capcontrolarea add voltReductionPointId numeric;
go
update capcontrolarea set voltReductionPointId = 0;
go
alter table capcontrolarea alter column voltReductionPointId numeric not null; 
go
alter table capcontrolspecialarea add voltReductionPointId numeric;
go
update capcontrolspecialarea set voltReductionPointId = 0;
go
alter table capcontrolspecialarea alter column voltReductionPointId numeric not null; 
go
/* End YUK-5396 */

/* Start YUK-5392 */
create table DYNAMICCCOPERATIONSTATISTICS (
   PAObjectID           numeric              not null,
   UserDefOpCount       numeric              not null,
   UserDefConfFail      numeric              not null,
   DailyOpCount         numeric              not null,
   DailyConfFail        numeric              not null,
   WeeklyOpCount        numeric              not null,
   WeeklyConfFail       numeric              not null,
   MonthlyOpCount       numeric              not null,
   MonthlyConfFail      numeric              not null,
   constraint PK_DYNAMICCCOPERATIONSTATISTIC primary key (PAObjectID)
);
go

alter table DYNAMICCCOPERATIONSTATISTICS
   add constraint FK_DYNAMICC_REFERENCE_YUKONPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go
/* End YUK-5392 */

/* Start YUK-5395 */
create table POINTPROPERTYVALUE (
   PointID              numeric              not null,
   PointPropertyCode    int                  not null,
   FloatValue           float                not null,
   constraint PK_POINTPROPERTYVALUE primary key nonclustered (PointID, PointPropertyCode)
);
go

alter table POINTPROPERTYVALUE
   add constraint FK_POINTPRO_REFERENCE_POINT foreign key (PointID)
      references POINT (POINTID);
go
/* End YUK-5395 */

/* Start YUK-5417 */
ALTER TABLE CAPBANKADDITIONAL
ALTER COLUMN PotentialTransformer varchar(100) NOT NULL;
GO
/* End YUK-5417 */

/* Start YUK-5422 */
DELETE FROM YukonGroupRole
      WHERE RolePropertyID = -70009;
go
DELETE FROM YukonUserRole
      WHERE RolePropertyID = -70009;
go
DELETE FROM YukonRoleProperty
      WHERE RolePropertyID = -70009;
go
/* End YUK-5422 */

/* Start YUK-5387 */
insert into YukonRoleProperty values(-10818, -108, 'View Alarms Alerts','false','Ability to receive point alarms as alerts');
insert into YukonRoleProperty values(-1701,-8, 'Alert Timeout Hours', '168', 'The number of hours that an alert should be held (zero = forever, decimal numbers are okay)'); 
/* End YUK-5387 */

/* Start YUK-5271 */
insert into YukonRoleProperty values(-1602,-7,'Msp BillingCyle DeviceGroup','/Meters/Billing/','Defines the Device Group parent group name for the MultiSpeak billingCycle element. Valid values are ''/Meters/Billing/'', ''/Meters/Collection'', ''/Meters/Alternate''');
/* End YUK-5271 */

/* Start YUK-5400 */
update
      dynamicpointalarming
set
      alarmcondition = alarmcondition + 1
from
      dynamicpointalarming a inner join point b on a.pointid = b.pointid
where
      a.alarmcondition > 3
      and b.pointtype in ('Status','CalcStatus','System','StatusOutput');

update
	pointalarming
set excludeNotifyStates = 
		(substring(excludeNotifyStates,1,4) 
		+ 'N' 
		+ substring(excludeNotifyStates,5,len(excludeNotifyStates) - 5)),
	alarmstates = 
		(substring(alarmstates,1,4) 
		+ '' 
		+ substring(alarmstates,5,len(alarmstates) - 5))
from
	pointalarming a inner join point b on a.pointid = b.pointid
where
	b.pointtype in ('Status','CalcStatus','System','StatusOutput');
/* End YUK-5400 */
/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('4.0', 'David', '07-Dec-2007', 'Latest Update', 0 );
