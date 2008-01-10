/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

create table DYNAMICCCSPECIALAREA  (
   AreaID               NUMBER                          not null,
   additionalflags      VARCHAR2(20)                    not null
);

create table DYNAMICBILLINGFORMAT  (
   FormatID             NUMBER                          not null,
   Delimiter            VARCHAR2(20),
   Header               VARCHAR2(255),
   Footer               VARCHAR2(255)
)
;

alter table DYNAMICBILLINGFORMAT
   add constraint PK_DYNAMICBILLINGFORMAT primary key (FormatID)
;

create table DYNAMICBILLINGFIELD  (
   id                   NUMBER                          not null,
   FormatID             NUMBER                          not null,
   FieldName            VARCHAR2(50)                    not null,
   FieldOrder           NUMBER                          not null,
   FieldFormat          VARCHAR2(50),
   MaxLength            NUMBER                          not null,
   constraint PK_DYNAMICBILLINGFIELD primary key (id)
)
;

alter table BillingFileFormats modify FormatType varchar2(100);
/* Start YUK-4920 */
alter table BillingFileFormats ADD SystemFormat smallint;
/* End YUK-4920 */
update BillingFileFormats SET SystemFormat=1;

insert into sequenceNumber values (100, 'BillingFileFormats');

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

create table CCSEASONSTRATEGYASSIGNMENT  (
   paobjectid           NUMBER                          not null,
   seasonscheduleid     NUMBER                          not null,
   seasonname           VARCHAR2(20)                    not null,
   strategyid           NUMBER                          not null,
   constraint PK_CCSEASONSTRATEGYASSIGNMENT primary key (paobjectid,seasonscheduleid, seasonname)
);

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_PAOID foreign key (paobjectid)
      references YukonPAObject (PAObjectID);

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_SCHEDID foreign key (seasonscheduleid)
      references SeasonSchedule (ScheduleID);

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_ccssa_season foreign key (seasonscheduleid, seasonname)
      references DateOfSeason (SeasonScheduleID, SeasonName);

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSEASON_REFERENCE_CAPCONTR foreign key (strategyid)
      references CapControlStrategy (StrategyID);

/* Start YUK-4905 */
insert into ccseasonstrategyassignment
(paobjectid, seasonscheduleid, seasonname, strategyid)
select substationbusid, -1,'Default',strategyid from capcontrolsubstationbus;

insert into ccseasonstrategyassignment
(paobjectid, seasonscheduleid, seasonname, strategyid)
select feederid, -1, 'Default',strategyid from capcontrolfeeder;

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

create table CapControlSpecialArea( AreaID number not null );

alter table CapControlSpecialArea
   add constraint PK_CapControlSpecialArea primary key (AreaID);

create table CCSubSpecialAreaAssignment (

   AreaID number not null,
   SubstationBusID number not null,
   DisplayOrder number not null
);

alter table CCSubSpecialAreaAssignment
   add constraint PK_CCSubSpecialAreaAssignment primary key (AreaId, SubstationBusId);


alter table DYNAMICCCAREA
   add constraint PK_DYNAMICCCAREA primary key (AreaId);


insert into DynamicCCSpecialArea (AreaId, Additionalflags) select areaid, 'NNNNNNNNNNNNNNNNNNNN' from CapControlSpecialArea;

alter table DynamicCCFeeder add PhaseAValue float;
update DynamicCCFeeder set PhaseAValue = 0;
alter table DynamicCCFeeder modify PhaseAValue float not null;

alter table DynamicCCFeeder add PhaseBValue float;
update DynamicCCFeeder set PhaseBValue = 0;
alter table DynamicCCFeeder modify PhaseBValue float not null;

alter table DynamicCCFeeder add PhaseCValue float;
update DynamicCCFeeder set PhaseCValue = 0;
alter table DynamicCCFeeder modify PhaseCValue float not null;

alter table DynamicCCSubstationbus add PhaseAValue float;
update DynamicCCSubstationbus set PhaseAValue = 0;
alter table DynamicCCSubstationbus modify PhaseAValue float not null;

alter table DynamicCCSubstationbus add PhaseBValue float;
update DynamicCCSubstationbus set PhaseBValue = 0;
alter table DynamicCCSubstationbus modify PhaseBValue float not null;

alter table DynamicCCSubstationbus add PhaseCValue float;
update DynamicCCSubstationbus set PhaseCValue = 0;
alter table DynamicCCSubstationbus modify PhaseCValue float not null;

alter table CapControlFeeder add UsePhaseData char(1);
update CapControlFeeder set UsePhaseData = 'N';
alter table CapControlFeeder modify UsePhaseData char(1) not null;

alter table CapControlFeeder add PhaseB number;
update CapControlFeeder set PhaseB = 0;
alter table CapControlFeeder modify PhaseB number not null;

alter table CapControlFeeder add PhaseC number;
update CapControlFeeder set PhaseC = 0;
alter table CapControlFeeder modify PhaseC number not null;

alter table CapControlSubstationbus add UsePhaseData char(1);
update CapControlSubstationbus set UsePhaseData = 'N';
alter table CapControlSubstationbus modify UsePhaseData char(1) not null;

alter table CapControlSubstationbus add PhaseB number;
update CapControlSubstationbus set PhaseB = 0;
alter table CapControlSubstationbus modify PhaseB number not null;

alter table CapControlSubstationbus add PhaseC number;
update CapControlSubstationbus set PhaseC = 0;
alter table CapControlSubstationbus modify PhaseC number not null;

delete from YukonlistEntry where YukonDefinitionID = 3100;



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

alter table ccfeederbanklist modify controlorder float;
alter table ccfeederbanklist modify closeorder float;
alter table ccfeederbanklist modify triporder float;

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
create table TOUATTRIBUTEMAPPING  (
   touID                NUMBER(6)                       not null,
   displayname          VARCHAR2(50)                    not null,
   peakAttribute        VARCHAR2(50)                    not null,
   energyAttribute      VARCHAR2(50)                    not null
);

INSERT INTO TouAttributeMapping VALUES (1, 'A', 'TOU_RATE_A_PEAK_DEMAND', 'TOU_RATE_A_ENERGY');
INSERT INTO TouAttributeMapping VALUES (2, 'B', 'TOU_RATE_B_PEAK_DEMAND', 'TOU_RATE_B_ENERGY');
INSERT INTO TouAttributeMapping VALUES (3, 'C', 'TOU_RATE_C_PEAK_DEMAND', 'TOU_RATE_C_ENERGY');
INSERT INTO TouAttributeMapping VALUES (4, 'D', 'TOU_RATE_D_PEAK_DEMAND', 'TOU_RATE_D_ENERGY');

alter table TOUATTRIBUTEMAPPING
   add constraint PK_TOUATTRIBUTEMAPPING primary key (touID);
/* End YUK-4982 */

alter table cceventlog add actionId number;
update cceventlog set actionId = -1;
alter table cceventlog modify actionId number not null;


/* Begin YUK-4785 */
create table DEVICECONFIGURATIONDEVICEMAP  (
   DeviceID             NUMBER                          not null,
   DeviceConfigurationId NUMBER                          not null,
   constraint PK_DEVICECONFIGURATIONDEVICEMA primary key (DeviceID)
)
;


create table DEVICECONFIGURATION  (
   DeviceConfigurationID NUMBER                          not null,
   Name                 VARCHAR2(30)                    not null,
   Type                 VARCHAR2(30)                    not null,
   constraint PK_DEVICECONFIGURATION primary key (DeviceConfigurationID)
)
;

create table DEVICECONFIGURATIONITEM  (
   DEVICECONFIGURATIONITEMID NUMBER                          not null,
   DeviceConfigurationID NUMBER                          not null,
   FieldName            VARCHAR2(30)                    not null,
   Value                VARCHAR2(30)                    not null,
   constraint PK_DEVICECONFIGURATIONITEM primary key (DEVICECONFIGURATIONITEMID)
)
;

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_DEVICECO foreign key (DeviceConfigurationId)
      references DEVICECONFIGURATION (DeviceConfigurationID)
;

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_YUKONPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
      on delete cascade
;

alter table DEVICECONFIGURATIONITEM
   add constraint FK_DEVICECO_REF_DEVICEC2 foreign key (DeviceConfigurationID)
      references DEVICECONFIGURATION (DeviceConfigurationID)
      on delete cascade
;

      
/* End YUK-4785 */
insert into YukonRoleProperty values(-20013,-200,'Edit Device Config','false','Controls the ability to edit and create device configurations');
insert into YukonRoleProperty values(-20014,-200,'View Device Config','true','Controls the ability to view existing device configurations');
insert into YukonRoleProperty values(-20206,-202,'Enable Profile Request','true','Access to perform profile data request');

alter table userPaoPermission add Allow varchar2(5);
update UserPaoPermission set Allow = 'ALLOW';
alter table groupPaoPermission add Allow varchar2(5);
update GroupPaoPermission set Allow = 'ALLOW';

/* Start YUK-5048 */
ALTER TABLE GROUPPAOPERMISSION
MODIFY(ALLOW  NOT NULL);
/* End YUK-5048 */

/* Start YUK-5046 */
ALTER TABLE USERPAOPERMISSION
MODIFY(ALLOW  NOT NULL);
/* End YUK-5046 */

insert into YukonRoleProperty values(-70013,-700,'Definition Available','Open,OpenQuestionable,OpenPending','Capbank sized in these states will be added to the available sum.');
insert into YukonRoleProperty values(-70014,-700,'Definition Unavailable','Close,CloseQuestionable,CloseFail,ClosePending,OpenFail','Capbank sized in these states will be added to the unavailable sum.');
insert into YukonRoleProperty values(-70015,-700,'Definition Tripped','Open,OpenFail,OpenPending,OpenQuestionable','Capbank sized in these states will be added to the tripped sum.');
insert into YukonRoleProperty values(-70016,-700,'Definition Closed','Close,CloseFail,CloseQuestionable,ClosePending','Capbank sized in these states will be added to the closed sum.');
insert into YukonRoleProperty VALUES(-100204, -1002, 'Daily/Total Operation Count', 'true', 'is Daily/Total Operation Count displayed');

/* Start YUK-4752, YUK-5006 */
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'System',0,'Y','STATIC' FROM DeviceGroup WHERE DeviceGroupId<100 group by 2,3,4,5;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Routes',(SELECT MAX(DeviceGroupID) from DeviceGroup WHERE DeviceGroupId<100),'Y','ROUTE' FROM DeviceGroup WHERE DeviceGroupId<100 group by 2,3,4,5;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Device Types',(SELECT MAX(DeviceGroupID) -1 from DeviceGroup WHERE DeviceGroupId<100),'Y','DEVICETYPE' FROM DeviceGroup WHERE DeviceGroupId<100 group by 2,3,4,5; 
/* End YUK-4752, YUK-5006 */

alter table dynamiccccapbank add beforeVar varchar2(32);
update dynamiccccapbank set beforeVar = '---';
alter table dynamiccccapbank modify beforeVar not null;
alter table dynamiccccapbank add afterVar varchar2(32);
update dynamiccccapbank set afterVar = '---';
alter table dynamiccccapbank modify afterVar varchar2(32) not null;
alter table dynamiccccapbank add changeVar varchar2(32);
update dynamiccccapbank set changeVar = '---';
alter table dynamiccccapbank modify changeVar varchar2(32) not null;

alter table lmthermostatgear add RampRate float;
update lmthermostatgear set RampRate = 0;
alter table lmthermostatgear modify RampRate not null;

/* Start YUK-4707 */
create table CAPCONTROLSUBSTATION  (
   SubstationID         NUMBER                          not null,
   constraint PK_CAPCONTROLSUBSTATION primary key (SubstationID)
);

create table DYNAMICCCSUBSTATION  (
   SubStationID         NUMBER                          not null,
   AdditionalFlags      VARCHAR2(20)                    not null,
   SAEnabledID          number                          not null,
   constraint PK_DYNAMICCCSUBSTATION primary key (SubStationID)
);

create table CCSUBSTATIONSUBBUSLIST  (
   SubStationID         NUMBER                          not null,
   SubStationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCSUBSTATIONSUBBUSLIST primary key (SubStationID, SubStationBusID)
);

/* @error ignore-begin */
create table CAPCONTROLAREA  (
   AreaID               number                          not null,
   constraint PK_CAPCONTROLAREA primary key (AreaID)
);

alter table CAPCONTROLAREA
   add constraint FK_CAPCONTR_REFERENCE_YUKONPAO foreign key (AreaID)
      references YukonPAObject (PAObjectID);

/* Start YUK-5021 */
alter table dynamicccarea
   add constraint FK_ccarea_Dynccarea foreign key (areaID)
      references Capcontrolarea (areaID)
;
/* End YUK-5021 */

alter table CCSUBAREAASSIGNMENT drop constraint FK_CCSUBARE_CAPCONTR;

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLAREA (AreaID);
/* @error ignore-end */

/* @start-block */
Create global temporary table mySubstation
(
	SubBusName varchar2(60),
	subbusId number,
	CCsubStationName varchar2(60)
);
insert into
	mySubstation
select
	paoname,
	paobjectid,
	paoname
from
	yukonpaobject
where
	type = 'CCSUBBUS';

declare 
v_paoid number(6);
v_ccsubstationname varchar2(60);

cursor substation_curs is select distinct(CCSubStationName) from mySubstation;
begin
select max(paobjectid) into v_paoid from yukonpaobject;
v_paoid := v_paoid + 1;
open substation_curs;
fetch substation_curs into v_ccsubstationname;

while (substation_curs%found)
	loop
		insert into yukonpaobject (paobjectid, category, paoclass, paoname, type, description, 

disableflag, paostatistics)
		select 
			v_paoid,
			'CAPCONTROL',
			'CAPCONTROL',
			concat('S: ',v_ccsubstationname),
			'CCSUBSTATION',
			'(none)',
			'N',
			'-----' 
		from 
			yukonpaobject;
		insert into capcontrolsubstation (substationid)
		select
			v_paoid
		from 
			yukonpaobject;
		v_paoid := v_paoid + 1;
		fetch substation_curs into v_ccsubstationname;
	end loop;
close substation_curs;
end;
/

Create global temporary table mySubstation2
(
	SubBusName varchar2(60),
	subbusId number,
	CCsubStationName varchar2(60),
	Substationid number,
	areaname varchar2(60),
	areaid number
);
insert into
	mySubstation2
select 
	s.*
	, yp.paobjectid
	, yp1.paoname
	, yp1.paobjectid
from
	yukonpaobject yp
	, mySubstation s
	, yukonpaobject yp1
	, ccsubareaassignment sa
where
	yp.paoname = concat('S: ',s.ccsubstationname)
	and yp.type = 'CCSUBSTATION'
	and s.subbusid = sa.substationbusid
	and sa.areaid = yp1.paobjectid;

Create global temporary table mySubstation3
(
	SubBusName varchar2(60),
	subbusId number,
	CCsubStationName varchar2(60),
	Substationid number,
	areaname varchar2(60),
	areaid number
);
insert into
	mySubstation3
select 
	s.*
	, yp.paobjectid as Substationid
	, yp1.paoname as areaname
	, yp1.paobjectid as areaId
from
	yukonpaobject yp
	, mySubstation s
	, yukonpaobject yp1
	, ccsubspecialareaassignment sa
where
	yp.paoname = concat('S: ',s.ccsubstationname)
	and yp.type = 'CCSUBSTATION'
	and s.subbusid = sa.substationbusid
	and sa.areaid = yp1.paobjectid;

create global temporary table ccsa_backup
(
   AreaID               NUMBER                          not null,
   SubstationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null
);

insert into 
	ccsa_backup
select 
	*
from 
	ccsubareaassignment;

create global temporary table ccssaa_backup  (
   AreaID               NUMBER                          not null,
   SubstationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null
);
insert into
	ccssaa_backup
select 
	*
from 
	ccsubspecialareaassignment;

alter table ccsubareaassignment drop constraint FK_CCSUBARE_CAPSUBAREAASSGN;

update 
	ccsubareaassignment a
set 
	a.substationbusid = (
	select
		mySubstation2.substationid
	from 
		mySubstation2
	where 
		a.substationbusid = mySubstation2.subbusid);

update 
	ccsubspecialareaassignment a
set 
	a.substationbusid = (
	select
		mySubstation3.substationid
	from 
		mySubstation3
	where 
		a.substationbusid = mySubstation3.subbusid);
/
declare 
	v_ccsubbusid		mysubstation2.subbusid%TYPE;
	v_lastsubstationid 	mysubstation2.substationid%TYPE;
	v_index 		number(6) := 1;
	v_ccsubstationid 	mysubstation2.substationid%TYPE;
cursor substation_curs2 is select subbusid, substationid from mySubstation2;
begin
open substation_curs2; 
fetch substation_curs2 into v_ccsubbusid, v_ccsubstationid;

while (substation_curs2%found)
	loop
		insert into ccsubstationsubbuslist (substationid,substationbusid, displayorder)
		select 
			v_ccsubstationid,
			v_ccsubbusid, 
			v_index 
		from
			dual;
		v_lastsubstationid := v_ccsubstationid;
		fetch substation_curs2 into v_ccsubbusid, v_ccsubstationid;
		if (v_lastsubstationid = v_ccsubstationid) then
			v_index := v_index + 1;
		else
			v_index := 1;
		end if;
	end loop;
close substation_curs2;
end;

/

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_CAPSUBAREAASSGN foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID);

drop table mySubstation;
drop table mySubstation2;
drop table mySubstation3;
drop table ccsa_backup;
drop table ccssaa_backup;
/* @end-block */

alter table DYNAMICCCSUBSTATION
   add constraint FK_DYNAMICC_REFERENCE_CAPCONTR foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID);

alter table CAPCONTROLSUBSTATION
   add constraint FK_CAPCONTR_REF_YUKONPA2 foreign key (SubstationID)
      references YukonPAObject (PAObjectID)
         on delete cascade;

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID);

/* Start YUK-4746 */
alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_CAPCONTR2 foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID);
/* End YUK-4746 */

alter table CAPCONTROLSPECIALAREA
   add constraint FK_CAPCONTR_YUKONPAO2 foreign key (AreaID)
      references YukonPAObject (PAObjectID);

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSUBSTA_CAPCONTR foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID);

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSUBSTA_REFERENCE_CAPCONTR foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);
/* End YUK-4707 */

create or replace view CCINVENTORY_VIEW(Region, SubName, FeederName, subId, substationid, fdrId, CBCName, cbcId, capbankname, bankId, CapBankSize, Sequence, ControlStatus, SWMfgr, SWType, ControlType, Protocol, IPADDRESS, SlaveAddress, LAT, LON, DriveDirection, OpCenter, TA) as
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


create or replace view CCOPERATIONS_VIEW(cbcName, capbankname, opTime, operation, confTime, confStatus, feederName, feederId, subName, subBusId, substationid, region, BANKSIZE, protocol, ipAddress, serialNum, SlaveAddress, kvarAfter, kvarChange, kvarBefore) as
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


/* Start YUK-4730 */
create table JOB  (
   JobID                INTEGER                         not null,
   BeanName             VARCHAR2(250)                   not null,
   Disabled             CHAR(1)                         not null,
   UserID               NUMBER                          not null,
   constraint PK_JOB primary key (JobID)
);

alter table JOB
   add constraint FK_Job_YukonUser foreign key (UserID)
      references YukonUser (UserID);

create table JOBPROPERTY  (
   JobPropertyID          NUMBER                          not null,
   JobID                INTEGER                         not null,
   name                 CLOB                            not null,
   value                CLOB                            not null,
   constraint PK_JOBPROPERTY primary key (JobPropertyID)
);

alter table JOBPROPERTY
   add constraint FK_JobProperty_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade;

create table JOBSCHEDULEDONETIME  (
   JobID                int                             not null,
   StartTime            DATE                            not null,
   constraint PK_JOBSCHEDULEDONETIME primary key (JobID)
);

alter table JOBSCHEDULEDONETIME
   add constraint FK_JobScheduledOneTime_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade;

create table JOBSCHEDULEDREPEATING  (
   JobID                INTEGER                         not null,
   CronString           CLOB                            not null,
   constraint PK_JobScheduledRepeating primary key (JobID)
);

alter table JOBSCHEDULEDREPEATING
   add constraint FK_JOBSCHED_REFERENCE_JOB foreign key (JobID)
      references JOB (JobID)
      on delete cascade;

create table JOBSTATUS  (
   JobStatusID          INTEGER                         not null,
   JobID                INTEGER                         not null,
   StartTime            DATE                            not null,
   StopTime             DATE,
   JobState             VARCHAR2(50),
   message              CLOB,
   constraint PK_JOBSTATUS primary key (JobStatusID)
);

alter table JOBSTATUS
   add constraint FK_JobStatus_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade;
/* End YUK-4730 */

/* Begin YUK-4771 (formerly YUK-4716) */
create table CCSTRATEGYTIMEOFDAY  (
   StrategyID           NUMBER                          not null,
   StartTimeSeconds     NUMBER                          not null,
   PercentClose         NUMBER                          not null,
   constraint PK_STRAT_TOD primary key (StrategyID, StartTimeSeconds)
);

alter table CCSTRATEGYTIMEOFDAY
   add constraint FK_STRAT_TOD_CCSTRAT foreign key (StrategyID)
      references CapControlStrategy (StrategyID);
/* End YUK-4716 */

/* Start YUK-4763 */
alter table DynamicCCFeeder add LastWattPointTime date;
update DynamicCCFeeder set LastWattPointTime = '1990-01-01 00:00:00';
alter table DynamicCCFeeder modify LastWattPointTime date not null;
alter table DynamicCCFeeder add LastVoltPointTime date;
update DynamicCCFeeder set LastVoltPointTime = '1990-01-01 00:00:00';
alter table DynamicCCFeeder modify LastVoltPointTime date not null;

alter table DynamicCCSubstationbus add LastWattPointTime date;
update DynamicCCSubstationbus set LastWattPointTime = '1990-01-01 00:00:00';
alter table DynamicCCSubstationbus modify LastWattPointTime date not null;
alter table DynamicCCSubstationbus add LastVoltPointTime date;
update DynamicCCSubstationbus set LastVoltPointTime = '1990-01-01 00:00:00';
alter table DynamicCCSubstationbus modify LastVoltPointTime date not null;

alter table capcontrolstrategy add likeDayFallBack char(1);
update capcontrolstrategy set likeDayFallBack = 'N';
alter table capcontrolstrategy modify likeDayFallBack char(1) not null;
/* End YUK-4763*/

/* Start YUK-4759 */
alter table SubstationToRouteMapping drop constraint FK_Sub_Rte_Map_RteID;

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_RteID foreign key (RouteID)
      references Route (RouteID)
      on delete cascade;
/* End YUK-4759 */

/* Start YUK-4721 */
alter table DynamicPAOStatistics drop constraint FK_PASt_YkPA;

alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID)
      on delete cascade;
/* End YUK-4721 */

/* Start YUK-4638 */
update devicetypecommand set devicetype ='MCT-430S4' where devicetype = 'MCT-430S';
insert into command values(-136, 'putconfig emetcon timezone ?''Enter Timezone (et|ct|mt|pt OR #offset)''', 'Write Timezone to Meter', 'MCT-430A'); 

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
alter table cceventlog add capbankstateinfo varchar2(20);
update cceventlog set capbankstateinfo = 'N/A';
alter table cceventlog modify capbankstateinfo varchar2(20) not null ;
/* End YUK-4813 */

/* Start YUK-4762, YUK-4969 */
create table CAPCONTROLCOMMENT  (
   CommentID            INTEGER                         not null,
   PaoID                NUMBER                         not null,
   UserID               NUMBER                         not null,
   Action               VARCHAR2(50)                    not null,
   CommentTime          DATE                            not null,
   "Comment"            VARCHAR2(500)                   not null,
   Altered              VARCHAR2(3)                     not null,
   constraint PK_CAPCONTROLCOMMENT  primary key (CommentID)
);

alter table CAPCONTROLCOMMENT 
   add constraint FK_CAPCONTR_REFERENCE_YUKONPA2 foreign key (PaoID)
      references YukonPAObject (PAObjectID);

alter table CAPCONTROLCOMMENT 
   add constraint FK_CAPCONTR_REFERENCE_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);
/* End YUK-4762, YUK-4969 */

/* Start YUK-4810 */
insert into YukonRoleProperty values(-20207,-202,'Enable Auto Archiving','true','Allows a user to setup automatic archiving on their yukon system pertaining to the move in/move out interface');

create table DEVICEEVENT  (
   DeviceID             NUMBER                          not null,
   TimeStamp            DATE                            not null,
   "Comment"            VARCHAR2(50)                    not null,
   constraint PK_DEVICEEVENT primary key (DeviceID)
);
/* End YUK-4810 */

/* Begin YUK-4815 */
insert into YukonRoleProperty values (-100205, -1002, 'Capbank Fixed/Static Text', 'Fixed', 'The text to display for fixed/static capbanks');
/* End YUK-4815 */

/* Start YUK-4846 */
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
create table PEAKREPORT  (
   resultID             INTEGER                         not null,
   deviceID             NUMBER                          not null,
   channel              INTEGER                         not null,
   peakType             VARCHAR2(50)                    not null,
   runType              VARCHAR2(50)                    not null,
   runDate              DATE                            not null,
   resultString         VARCHAR2(1500)                  not null,
   constraint PK_PEAKREPORT primary key (resultID)
);
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

/* Start YUK-4859 */
create or replace view CCCAP_INVENTORY_VIEW as
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
/* End YUK-4859 */

/* Start YUK-4862 */
insert into YukonRoleProperty values(-20205,-202,'Enable Device Group','true','Allows access to change device groups for a device');
/* End YUK-4862 */

/* Start YUK-4866 */
delete
from
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

/* @error ignore-begin */
/* Start YUK-4745 */
insert into YukonRole values(-211,'CI Curtailment','Operator','Operator access to C&I Curtailment');
insert into YukonRoleProperty values(-21100,-211,'CI Curtailment Label','CI Curtailment','The operator specific name for C&I Curtailment');
/* End YUK-4745 */

/* Start YUK-4906 */
alter table MSPVendor add MaxReturnRecords int;
update MSPVendor set MaxReturnRecords = 10000;
alter table MSPVendor modify MaxReturnRecords int not null;

alter table MSPVendor add RequestMessageTimeout int;
update MSPVendor set RequestMessageTimeout = 120000;
alter table MSPVendor modify RequestMessageTimeout int not null;

alter table MSPVendor add MaxInitiateRequestObjects int;
update MSPVendor set MaxInitiateRequestObjects = 15;
alter table MSPVendor modify MaxInitiateRequestObjects int not null;

alter table MSPVendor add TemplateNameDefault varchar2(50);
update MSPVendor set TemplateNameDefault = '*Default Template';
alter table MSPVendor modify TemplateNameDefault varchar2(50) not null;
/* End YUK-4906 */

/* @error ignore-end */

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

/* Start YUK-5020 */
alter table DeviceTypeCommand
drop constraint "FK_DevCmd_Grp ";

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);
/* End YUK-5020 */

/* Start YUK-5022 */
alter table DYNAMICBILLINGFIELD
   add constraint FK_DBF_REF_BFF foreign key (FormatID)
      references BillingFileFormats (FormatID)
      on delete cascade;
alter table DYNAMICBILLINGFORMAT
   add constraint FK_DYNAMICB_REF_BILLI_BILLINGF foreign key (FormatID)
      references BillingFileFormats (FormatID);
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
create index INDX_PAOBJECTID_POINTID on POINT (
	   PAObjectID ASC,
	   POINTID ASC
	);
create index INDX_POFFSET_POINTTYPE on POINT (
	   POINTOFFSET ASC,
	   POINTTYPE ASC
	);
/* End YUK-5026 */

/* Start YUK-5027 */
create index INDX_UOMID_POINTID on POINTUNIT (
	   UOMID ASC,
	   POINTID ASC
	);
/* End YUK-5027 */

/* Start YUK-5028 */
/* @error ignore-begin */
alter table ScheduleTimePeriod
    drop constraint pk_schedtimeperiod;
alter table ScheduleTimePeriod
   add constraint PK_SCHEDULETIMEPERIOD primary key (TimePeriodID);
/* @error ignore-end */
/* End YUK-5028 */

/* Start YUK-5036 */
/* @error ignore-begin */
ALTER TABLE DCCATEGORY
MODIFY(CATEGORYID NUMBER);

ALTER TABLE DCCATEGORY
MODIFY(CATEGORYTYPEID NUMBER);

ALTER TABLE DCCATEGORYITEM
MODIFY(CATEGORYID NUMBER);

ALTER TABLE DCCATEGORYITEM
MODIFY(ITEMTYPEID NUMBER);

ALTER TABLE DCCATEGORYITEMTYPE
MODIFY(CATEGORYTYPEID NUMBER);

ALTER TABLE DCCATEGORYITEMTYPE
MODIFY(ITEMTYPEID NUMBER);

ALTER TABLE DCCONFIGURATION
MODIFY(CONFIGID NUMBER);

ALTER TABLE DCCONFIGURATION
MODIFY(CONFIGTYPEID NUMBER);

ALTER TABLE DCCONFIGURATIONCATEGORY
MODIFY(CONFIGID NUMBER);

ALTER TABLE DCCONFIGURATIONCATEGORY
MODIFY(CATEGORYID NUMBER);

ALTER TABLE DCCONFIGURATIONCATEGORYTYPE
MODIFY(CONFIGTYPEID NUMBER);

ALTER TABLE DCCONFIGURATIONCATEGORYTYPE
MODIFY(CATEGORYTYPEID NUMBER);

ALTER TABLE DCCONFIGURATIONTYPE
MODIFY(CONFIGTYPEID NUMBER);

ALTER TABLE DCDEVICECONFIGURATION
MODIFY(DEVICEID NUMBER);

ALTER TABLE DCDEVICECONFIGURATION
MODIFY(CONFIGID NUMBER);

ALTER TABLE DCDEVICECONFIGURATIONTYPE
MODIFY(CONFIGTYPEID NUMBER);

ALTER TABLE DCITEMTYPE
MODIFY(ITEMTYPEID NUMBER);

ALTER TABLE DCITEMTYPE
MODIFY(MINVALUE NUMBER);

ALTER TABLE DCITEMTYPE
MODIFY(MAXVALUE NUMBER);

ALTER TABLE DCITEMVALUE
MODIFY(ITEMTYPEID NUMBER);

ALTER TABLE DCITEMVALUE
MODIFY(VALUEORDER NUMBER);
/* @error ignore-end */
/* End YUK-5036 */

/* Start YUK-5049 */
ALTER TABLE DYNAMICCCAREA
MODIFY(AREAID NUMBER);
/* End YUK-5049 */

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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */
insert into CTIDatabase values('4.0', 'David', '07-Dec-2007', 'Latest Update', 0 );
