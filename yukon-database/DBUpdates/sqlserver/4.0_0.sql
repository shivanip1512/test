/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create table dynamicccarea ( AreaID numeric not null, additionalflags varchar(20) not null );

alter table dynamicccarea
   add constraint FK_ccarea_Dynccarea foreign key (areaID)
      references Capcontrolarea (areaID);

insert into dynamicccarea (areaid, additionalflags) select areaid, 'NNNNNNNNNNNNNNNNNNNN' from capcontrolarea; 

/*==============================================================*/
/* Table: DYNAMICBILLINGFIELD                                   */
/*==============================================================*/
create table DYNAMICBILLINGFIELD (
   id                   numeric              not null,
   FormatID             numeric              not null,
   FieldName            varchar(50)          not null,
   FieldOrder           numeric              not null,
   FieldFormat          varchar(50)          null,
   MaxLength            numeric              not null,
   constraint PK_DYNAMICBILLINGFIELD primary key (id)
)
go

 create table DynamicBillingFormat (
	 FormatID numeric NOT NULL,
	 Delimiter varchar(20),
	 Header varchar(255),
	 Footer varchar(255),
	 primary key (FormatID),
	 Foreign key (FormatID) REFERENCES BillingFileFormats (FormatID)
 )
go 
alter table BillingFileFormats ALTER COLUMN FormatType varchar(100);
go

alter table BillingFileFormats ADD SystemFormat bit;
go

update BillingFileFormats SET SystemFormat=1;

insert into sequenceNumber values (100, 'BillingFileFormats');

/* ATS */
insert into DynamicBillingFormat VALUES( '-18',',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','');
insert into DynamicBillingField values(1,-18,'Plain Text',0,'M');
insert into DynamicBillingField values(2,-18, 'meterNumber',1,'');
insert into DynamicBillingField values(3,-18, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(4,-18, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(5,-18, 'totalConsumption - timestamp',4,'yy/MM/dd');
insert into DynamicBillingField values(6,-18,'totalPeakDemand - reading',5,'##0.000');
insert into DynamicBillingField values(7,-18,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(8,-18,'totalPeakDemand - timestamp',7,'yy/MM/dd');
insert into DynamicBillingField values(9,-18,'Plain Text',8,'');
insert into DynamicBillingField values(10,-18,'Plain Text',9,'');
insert into DynamicBillingField values(11,-18,'Plain Text',10,';');

/* DAFFRON */
insert into DynamicBillingFormat values(6,',','H    Meter    kWh   Time   Date   Peak   PeakT  PeakD  Stat Sig Freq Phase','');
insert into DynamicBillingField values(12,6,'Plain Text',0,'M');
insert into DynamicBillingField values(13,6, 'meterNumber',1,'');
insert into DynamicBillingField values(14,6, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(15,6, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(16,6, 'totalConsumption - timestamp',4,'yy/MM/dd');
insert into DynamicBillingField values(17,6,'totalPeakDemand - reading',5,'##0.000');
insert into DynamicBillingField values(18,6,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(19,6,'totalPeakDemand - timestamp',7,'yy/MM/dd');
insert into DynamicBillingField values(20,6,'Plain Text',8,'  6');
insert into DynamicBillingField values(21,6,'Plain Text',9,'');
insert into DynamicBillingField values(22,6,'Plain Text',10,';');

/* NCDC */
insert into DynamicBillingFormat values(7,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD','');
insert into DynamicBillingField values(23,7,'Plain Text',0,'M');
insert into DynamicBillingField values(24,7, 'meterNumber',1,'');
insert into DynamicBillingField values(25,7, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(26,7, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(27,7, 'totalConsumption - timestamp',4,'yyyy/MM/dd');
insert into DynamicBillingField values(28,7,'totalPeakDemand - reading',5,'##0.00');
insert into DynamicBillingField values(29,7,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(30,7,'totalPeakDemand - timestamp',7,'yyyy/MM/dd');

/* NISC_NCDC */
insert into DynamicBillingFormat values(14,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD','');
insert into DynamicBillingField values(31,14,'Plain Text',0,'M');
insert into DynamicBillingField values(32,14, 'meterNumber',1,'');
insert into DynamicBillingField values(33,14, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(34,14, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(35,14, 'totalConsumption - timestamp',4,'MM/dd/yyyy');
insert into DynamicBillingField values(36,14,'totalPeakDemand - reading',5,'##0.00');
insert into DynamicBillingField values(37,14,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(38,14,'totalPeakDemand - timestamp',7,'MM/dd/yyyy');

/* NISC_NoLimt_kWh */
insert into DynamicBillingFormat values(-19,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','');
insert into DynamicBillingField values(39,-19,'Plain Text',0,'M');
insert into DynamicBillingField values(40,-19, 'meterNumber',1,'');
insert into DynamicBillingField values(41,-19, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(42,-19, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(43,-19, 'totalConsumption - timestamp',4,'yy/MM/dd');
insert into DynamicBillingField values(44,-19,'totalPeakDemand - reading',5,'##0.00');
insert into DynamicBillingField values(45,-19,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(46,-19,'totalPeakDemand - timestamp',7,'yy/MM/dd');
insert into DynamicBillingField values(47,-19,'Plain Text',8,'');
insert into DynamicBillingField values(48,-19,'Plain Text',9,'');
insert into DynamicBillingField values(49,-19,'Plain Text',10,';');

/* NISC */
insert into DynamicBillingFormat values(13,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','');
insert into DynamicBillingField values(50,13,'Plain Text',0,'M');
insert into DynamicBillingField values(51,13, 'meterNumber',1,'');
insert into DynamicBillingField values(52,13, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(53,13, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(54,13, 'totalConsumption - timestamp',4,'yy/MM/dd');
insert into DynamicBillingField values(55,13,'totalPeakDemand - reading',5,'##0.00');
insert into DynamicBillingField values(56,13,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(57,13,'totalPeakDemand - timestamp',7,'yy/MM/dd');
insert into DynamicBillingField values(58,13,'Plain Text',8,'');
insert into DynamicBillingField values(59,13,'Plain Text',9,'');
insert into DynamicBillingField values(60,13,'Plain Text',10,';');

/* SEDC_yyyyMMdd */
insert into DynamicBillingFormat values(-17,',', 'H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','');
insert into DynamicBillingField values(61,-17,'Plain Text',0,'M');
insert into DynamicBillingField values(62,-17, 'meterNumber',1,'');
insert into DynamicBillingField values(63,-17, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(64,-17, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(65,-17, 'totalConsumption - timestamp',4,'yyyy/MM/dd');
insert into DynamicBillingField values(66,-17,'totalPeakDemand - reading',5,'##0.000');
insert into DynamicBillingField values(67,-17,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(68,-17,'totalPeakDemand - timestamp',7,'yyyy/MM/dd');
insert into DynamicBillingField values(69,-17,'Plain Text',8,'');
insert into DynamicBillingField values(70,-17,'Plain Text',9,'');
insert into DynamicBillingField values(71,-17,'Plain Text',10,';');

/* SEDC54 */
insert into DynamicBillingFormat values(12,',', 'H    Meter    kWh   Time   Date','');
insert into DynamicBillingField values(72,12,'Plain Text',0,'M');
insert into DynamicBillingField values(73,12, 'meterNumber',1,'');
insert into DynamicBillingField values(74,12, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(75,12, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(76,12, 'totalConsumption - timestamp',4,'yy/MM/dd');
insert into DynamicBillingField values(77,12, 'Plain Text',5,'');

/* SEDC */
insert into DynamicBillingFormat values(0,',', 'H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','');
insert into DynamicBillingField values(78,0,'Plain Text',0,'M');
insert into DynamicBillingField values(79,0, 'meterNumber',1,'');
insert into DynamicBillingField values(80,0, 'totalConsumption - reading',2,'#####');
insert into DynamicBillingField values(81,0, 'totalConsumption - timestamp',3,'HH:mm');
insert into DynamicBillingField values(82,0, 'totalConsumption - timestamp',4,'yy/MM/dd');
insert into DynamicBillingField values(83,0,'totalPeakDemand - reading',5,'##0.000');
insert into DynamicBillingField values(84,0,'totalPeakDemand - timestamp',6,'HH:mm');
insert into DynamicBillingField values(85,0,'totalPeakDemand - timestamp',7,'yy/MM/dd');
insert into DynamicBillingField values(86,0,'Plain Text',8,'');
insert into DynamicBillingField values(87,0,'Plain Text',9,'');
insert into DynamicBillingField values(88,0,'Plain Text',10,';');

/* SimpleTOU */
insert into DynamicBillingFormat values(21,',','','');
insert into DynamicBillingField values(89,21,'meterNumber',0,'');
insert into DynamicBillingField values(90,21,'totalConsumption - reading',1,'#####');
insert into DynamicBillingField values(91,21,'totalConsumption - timestamp',2,'HH:mm');
insert into DynamicBillingField values(92,21,'totalConsumption - timestamp',3,'MM/dd/yyyy');
insert into DynamicBillingField values(93,21,'totalPeakDemand - reading',4,'##0.000');
insert into DynamicBillingField values(94,21,'totalPeakDemand - timestamp',5,'HH:mm');
insert into DynamicBillingField values(95,21,'totalPeakDemand - timestamp',6,'MM/dd/yyyy');
insert into DynamicBillingField values(96,21,'rateAConsumption - reading',7,'#####');
insert into DynamicBillingField values(97,21,'rateADemand- reading',8,'##0.000');
insert into DynamicBillingField values(98,21,'rateADemand- timestamp',9,'HH:mm');
insert into DynamicBillingField values(99,21,'rateADemand- timestamp',10,'MM/dd/yyyy');
insert into DynamicBillingField values(100,21,'rateBConsumption - reading',11,'#####');
insert into DynamicBillingField values(101,21,'rateBDemand- reading',12,'##0.000');
insert into DynamicBillingField values(102,21,'rateBDemand- timestamp',13,'HH:mm');
insert into DynamicBillingField values(103,21,'rateBDemand- timestamp',14,'MM/dd/yyyy');

alter table DYNAMICBILLINGFIELD
   add constraint PK_DYNAMICBILLINGFIELD primary key (id)
go

update yukonpaobject set type = 'MCT-430SL' where type = 'MCT-430SN' or type = 'MCT430SN';
update devicetypecommand set devicetype = 'MCT-430SL' where devicetype = 'MCT-430SN' or devicetype = 'MCT430SN';

/************************************* 
	START CAPCONTROL 4.0 CHANGES 
*************************************/
insert into SeasonSchedule values (-1,'No Season');
insert into DateOfSeason values(-1, 'Default', 1,1,12,31);
go

create table CCSeasonStrategyAssignment (
	PaobjectId numeric not null,
	SeasonScheduleId numeric not null,
	SeasonName varchar(20) not null,
	StrategyId numeric not null
);
go

alter table CapControlArea  drop constraint FK_CAPCONTAREA_CAPCONTRSTRAT;
alter table CapControlArea drop column StrategyId;
alter table CapControlSubstationbus  drop constraint FK_CCSUBB_CCSTR;
alter table CapControlSubstationbus drop column StrategyId;
alter table CapControlFeeder  drop constraint FK_CCFDR_CCSTR;
alter table CapControlFeeder drop column StrategyId;
go

create table CapControlSpecialArea( AreaID numeric not null );
go

alter table CapControlSpecialArea
   add constraint PK_CapControlSpecialArea primary key clustered (AreaID)
go

create table CCSubSpecialAreaAssignment (

   AreaID numeric not null,
   SubstationBusID numeric not null,
   DisplayOrder numeric not null

);

alter table CCSubSpecialAreaAssignment
   add constraint PK_CCSubSpecialAreaAssignment primary key nonclustered (AreaId, SubstationBusId)
go

alter table CCSubSpecialAreaAssignment
   add constraint FK_CCSubSpecialArea_CapContr foreign key (AreaID)
      references CapControlSpecialArea (AreaID)
go

alter table CCSubSpecialAreaAssignment
   add constraint FK_CCSubSpecialArea_CapSubAreaAssgn foreign key (SubstationBusId)
      references CapControlSubstationBus (SubstationBusId)
go

create table DynamicCCSpecialArea (
   AreaID               numeric              not null,
   Additionalflags      varchar(20)          not null
)
go

alter table DynamicCCSpecialArea
   add constraint FK_ccspecialarea_Dynccspecialarea foreign key (AreaID)
      references CapControlSpecialArea (AreaID)
go

insert into DynamicCCSpecialArea (AreaId, Additionalflags) select areaid, 'NNNNNNNNNNNNNNNNNNNN' from CapControlSpecialArea;
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

/************************************* 
	END CAPCONTROL 4.0 CHANGES 
*************************************/

delete from LMThermostatSeasonEntry where SeasonID in (select SeasonID from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100)));
go
delete from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
go
delete from ECToGenericMapping where MappingCategory = 'LMThermostatSchedule' and ItemID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
go
delete from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
go
update LMHardwareBase set LMHardwareTypeID = 0 where LMHardwareTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
go
delete from YukonlistEntry where YukonDefinitionID = 3100;
go

/* @error ignore-end */

update State
set text = 'Normal'
where 
	stategroupid = -1 
	and text = 'AnalogText';
-- the following inserts will be in the creation scripts starting with 4.0 but 
-- still needed for upgrading clients
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

alter table ccfeederbanklist alter column controlorder float;
alter table ccfeederbanklist alter column closeorder float;
alter table ccfeederbanklist alter column triporder float;

update command set label = 'Turn Off Test Light' where commandid = -65;
update command set label = 'Clear Comm Loss Counter' where commandid = -67;

/*==============================================================*/
/* Table: CAPCONTROLSPECIALAREA                                 */
/*==============================================================*/
create table CAPCONTROLSPECIALAREA  (
   AreaID               NUMBER                          not null
)
;
/*==============================================================*/
/* Table: CCSUBSPECIALAREAASSIGNMENT                            */
/*==============================================================*/
create table CCSUBSPECIALAREAASSIGNMENT  (
   AreaID               NUMBER                          not null,
   SubstationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null
)
;

insert into seasonSchedule values (-1,'No Season');
insert into dateOfSeason values(-1, 'Default', 1,1,12,31);

/* @error ignore-begin */
insert into yukonroleproperty values (-100011,-1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into yukonroleproperty values (-100012,-1000, 'Substation Last Update Timestamp', 'true', 'is last update timstamp shown for substations');
insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timstamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timstamp shown for capbanks');
insert into yukonroleproperty values (-100105,-1001, 'Target', 'true', 'is target stat displayed');
/* @error ignore-end */

update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100008;
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100007;

insert into YukonRoleProperty values(-1308,-4,'LDAP DN','dc=example,dc=com','LDAP Distinguished Name')
insert into YukonRoleProperty values(-1309,-4,'LDAP User Suffix','ou=users','LDAP User Suffix')
insert into YukonRoleProperty values(-1310,-4,'LDAP User Prefix','uid=','LDAP User Prefix')
insert into YukonRoleProperty values(-1311,-4,'LDAP Server Address','127.0.0.1','LDAP Server Address')
insert into YukonRoleProperty values(-1312,-4,'LDAP Server Port','389','LDAP Server Port')
insert into YukonRoleProperty values(-1313,-4,'LDAP Server Timeout','30','LDAP Server Timeout (in seconds)')

insert into YukonRoleProperty values(-1314,-4,'Active Directory Server Address','127.0.0.1','Active Directory Server Address')
insert into YukonRoleProperty values(-1315,-4,'Active Directory Server Port','389','Active Directory Server Port')
insert into YukonRoleProperty values(-1316,-4,'Active Directory Server Timeout','30','Active Directory Server Timeout (in seconds)')
insert into YukonRoleProperty values(-1317,-4,'Active Directory NT Domain Name','(none)','Active Directory NT DOMAIN NAME')

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

if exists (select 1
            from  sysobjects
           where  id = object_id('TOUATTRIBUTEMAPPING')
            and   type = 'U')
   drop table TOUATTRIBUTEMAPPING
go

/*==============================================================*/
/* Table: TOUATTRIBUTEMAPPING                                   */
/*==============================================================*/
create table TOUATTRIBUTEMAPPING (
   touID                numeric(6)           identity(1,1),
   displayname          varchar(50)          not null,
   peakAttribute        varchar(50)          not null,
   usageAttribute       varchar(50)          not null
)
go

INSERT INTO TouAttributeMapping (displayname, peakattribute, usageattribute) VALUES ('A', 'TOU_RATE_A_PEAK_DEMAND', 'TOU_RATE_A_USAGE');
INSERT INTO TouAttributeMapping (displayname, peakattribute, usageattribute) VALUES ('B', 'TOU_RATE_B_PEAK_DEMAND', 'TOU_RATE_B_USAGE');
INSERT INTO TouAttributeMapping (displayname, peakattribute, usageattribute) VALUES ('C', 'TOU_RATE_C_PEAK_DEMAND', 'TOU_RATE_C_USAGE');
INSERT INTO TouAttributeMapping (displayname, peakattribute, usageattribute) VALUES ('D', 'TOU_RATE_D_PEAK_DEMAND', 'TOU_RATE_D_USAGE');

alter table TOUATTRIBUTEMAPPING
   add constraint PK_TOUATTRIBUTEMAPPING primary key (touID)
go

alter table cceventlog add actionId numeric;
go
update cceventlog set actionId = -1;
go
alter table cceventlog alter column actionId numeric not null;

create table DeviceConfiguration (
	DeviceConfigurationId           numeric              not null,
	Name        varchar(30)          not null,
	Type		varchar(30) not null
)

create table DeviceConfigurationItem (
	DeviceConfigurationItemId	numeric             not null,
	DeviceConfigurationId		numeric             not null,
	FieldName					varchar(30)			not null,
	Value						varchar(30)         not null
)

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

insert into YukonRoleProperty values(-70013,-700,'Definition Available','Open,OpenQuestionable,OpenPending','Capbank sized in these states will be added to the available sum.');
insert into YukonRoleProperty values(-70014,-700,'Definition Unavailable','Close,CloseQuestionable,CloseFail,ClosePending,OpenFail','Capbank sized in these states will be added to the unavailable sum.');
insert into YukonRoleProperty values(-70015,-700,'Definition Tripped','Open,OpenFail,OpenPending,OpenQuestionable','Capbank sized in these states will be added to the tripped sum.');
insert into YukonRoleProperty values(-70016,-700,'Definition Closed','Close,CloseFail,CloseQuestionable,ClosePending','Capbank sized in these states will be added to the closed sum.');
insert into YukonRoleProperty VALUES(-100204, -1002, 'Daily/Total Operation Count', 'true', 'is Daily/Total Operation Count displayed');

INSERT INTO DeviceGroup 
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Routes',0,'Y','ROUTE' FROM DeviceGroup WHERE DeviceGroupId<100;

INSERT INTO DeviceGroup 
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Device Types',0,'Y','DEVICETYPE' FROM DeviceGroup WHERE DeviceGroupId<100;

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
create table capcontrolsubstation
(
	substationid numeric not null
);

create table dynamicccsubstation
(
	substationid numeric not null,
	additionalflags varchar(20) not null,
	saenabledid numeric not null
);


create table ccsubstationsubbuslist
(
	substationid numeric not null,
	substationbusid numeric not null,
	displayorder numeric not null
);

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
			@ccsubstationname,
			'CCSUBSTATION',
			'(none)',
			'N',
			'-----' 
		from 
			yukonpaobject;
		fetch substation_curs into @ccsubstationname;
	end
close substation_curs;
deallocate substation_curs;

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
	yp.paoname = s.ccsubstationname
	and yp.type = 'CCSUBSTATION'
	and s.subbusid = sa.substationbusid
	and sa.areaid = yp1.paobjectid;

select 
	s.*
	, yp.paobjectid as Substationid
	, yp1.paoname as areaname
	, yp1.paobjectid as areaId
into 
	#mySubstation3
from
	yukonpaobject yp
	, #mySubstation s
	, yukonpaobject yp1
	, ccsubspecialareaassignment sa
where
	yp.paoname = s.ccsubstationname
	and yp.type = 'CCSUBSTATION'
	and s.subbusid = sa.substationbusid
	and sa.areaid = yp1.paobjectid;

select 
	*
into
	#ccsubareaassignment_backup
from 
	ccsubareaassignment;

select 
	*
into
	#ccsubspecialareaassignment_backup
from 
	ccsubspecialareaassignment;

alter table ccsubareaassignment drop constraint FK_CCSUBARE_CAPSUBAREAASSGN;
alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_CAPSUBAREAASSGN foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID);

update 
	ccsubareaassignment
set 
	ccsubareaassignment.substationbusid=#mySubstation2.substationid
from 
	ccsubareaassignment
	, #mySubstation2
where 
	ccsubareaassignment.substationbusid = #mySubstation2.subbusid;

update 
	ccsubspecialareaassignment
set 
	ccsubspecialareaassignment.substationbusid = #mySubstation3.substationid
from 
	ccsubspecialareaassignment
	, #mySubstation3
where 
	ccsubspecialareaassignment.substationbusid = #mySubstation3.subbusid;


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

insert into capcontrolsubstation (substationid) select paobjectid from yukonpaobject where type = 'CCSUBSTATION';

drop table #mySubstation;
drop table #mySubstation2;
drop table #mySubstation3;
drop table #ccsubareaassignment_backup;
drop table #ccsubspecialareaassignment_backup;

alter table CAPCONTROLSUBSTATION
   add constraint FK_CAPCONTR_REFERENCE_YUKONPAO foreign key (SubstationID)
      references YukonPAObject (PAObjectID)
         on update cascade on delete cascade;
go

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID);
go

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_CAPSUBAREAASSGN foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID)
         on delete cascade;
go

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_CAPCONTR2 foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID)
         on update cascade on delete cascade;
go

alter table CAPCONTROLSPECIALAREA
   add constraint FK_CAPCONTR_YUKONPAO2 foreign key (AreaID)
      references YukonPAObject (PAObjectID);
go

ALTER TABLE CCSUBSTATIONSUBBUSLIST 
	ADD  CONSTRAINT PK_CCSUBSTATIONSUBBUSLIST PRIMARY KEY CLUSTERED 
(
	SubStationID ASC,
	SubStationBusID ASC
);

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
   drop table JOB
go

/*==============================================================*/
/* Table: JOB                                                   */
/*==============================================================*/
create table JOB (
   JobID                int                  not null,
   BeanName             varchar(250)         not null,
   Disabled             char(1)              not null,
   UserID               numeric              not null,
   constraint PK_JOB primary key nonclustered (JobID)
)
go

alter table JOB
   add constraint FK_Job_YukonUser foreign key (UserID)
      references YukonUser (UserID)
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBPROPERTY')
            and   type = 'U')
   drop table JOBPROPERTY
go

/*==============================================================*/
/* Table: JOBPROPERTY                                           */
/*==============================================================*/
create table JOBPROPERTY (
   JobProperty          numeric              not null,
   JobID                int                  not null,
   name                 text                 not null,
   value                text                 not null,
   constraint PK_JOBPROPERTY primary key (JobProperty)
)
go

alter table JOBPROPERTY
   add constraint FK_JobProperty_Job foreign key (JobID)
      references JOB (JobID)
         on update cascade on delete cascade
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBSCHEDULEDONETIME')
            and   type = 'U')
   drop table JOBSCHEDULEDONETIME
go

/*==============================================================*/
/* Table: JOBSCHEDULEDONETIME                                   */
/*==============================================================*/
create table JOBSCHEDULEDONETIME (
   JobID                int                  not null,
   StartTime            datetime             not null,
   constraint PK_JOBSCHEDULEDONETIME primary key nonclustered (JobID)
)
go

alter table JOBSCHEDULEDONETIME
   add constraint FK_JobScheduledOneTime_Job foreign key (JobID)
      references JOB (JobID)
         on delete cascade
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBSCHEDULEDREPEATING')
            and   type = 'U')
   drop table JOBSCHEDULEDREPEATING
go

/*==============================================================*/
/* Table: JOBSCHEDULEDREPEATING                                 */
/*==============================================================*/
create table JOBSCHEDULEDREPEATING (
   JobID                int                  not null,
   CronString           text                 not null,
   constraint PK_JobScheduledRepeating primary key (JobID)
)
go

alter table JOBSCHEDULEDREPEATING
   add constraint FK_JOBSCHED_REFERENCE_JOB foreign key (JobID)
      references JOB (JobID)
         on update cascade on delete cascade
go

if exists (select 1
            from  sysobjects
           where  id = object_id('JOBSTATUS')
            and   type = 'U')
   drop table JOBSTATUS
go

/*==============================================================*/
/* Table: JOBSTATUS                                             */
/*==============================================================*/
create table JOBSTATUS (
   JobStatusID          int                  not null,
   JobID                int                  not null,
   StartTime            datetime             not null,
   StopTime             datetime             null,
   JobState             varchar(50)          null,
   message              text                 null,
   constraint PK_JOBSTATUS primary key (JobStatusID)
)
go

alter table JOBSTATUS
   add constraint FK_JobStatus_Job foreign key (JobID)
      references JOB (JobID)
         on update cascade on delete cascade
go

/* End YUK-4730 */

/* Begin YUK-4716 */
if exists (select 1
            from  sysobjects
           where  id = object_id('CCSTRATEGYTIMEOFDAY')
            and   type = 'U')
   drop table CCSTRATEGYTIMEOFDAY
go

/*==============================================================*/
/* Table: CCSTRATEGYTIMEOFDAY                                   */
/*==============================================================*/
create table CCSTRATEGYTIMEOFDAY (
   StrategyID           numeric              not null,
   HourZero             numeric              not null,
   HourOne              numeric              not null,
   HourTwo              numeric              not null,
   HourThree            numeric              not null,
   HourFour             numeric              not null,
   HourFive             numeric              not null,
   HourSix              numeric              not null,
   HourSeven            numeric              not null,
   HourEight            numeric              not null,
   HourNine             numeric              not null,
   HourTen              numeric              not null,
   HourEleven           numeric              not null,
   HourTwelve           numeric              not null,
   HourThirteen         numeric              not null,
   HourFourteen         numeric              not null,
   HourFifteen          numeric              not null,
   HourSixteen          numeric              not null,
   HourSeventeen        numeric              not null,
   HourEighteen         numeric              not null,
   HourNineteen         numeric              not null,
   HourTwenty           numeric              not null,
   HourTwentyOne        numeric              not null,
   HourTwentyTwo        numeric              not null,
   HourTwentyThree      numeric              not null,
   constraint PK_STRAT_TOD primary key nonclustered (StrategyID)
)
go

alter table CCSTRATEGYTIMEOFDAY
   add constraint FK_STRAT_TOD_CCSTRAT foreign key (StrategyID)
      references CapControlStrategy (StrategyID)
go
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

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */