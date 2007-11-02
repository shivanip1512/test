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
   FieldFormat          varchar(50)          null
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
   add constraint PK_CapControlSpecialArea primary key nonclustered (AreaID)
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
update UserPaoPermission set Allow = 'ALLOW';
alter table groupPaoPermission add Allow varchar(5);
update GroupPaoPermission set Allow = 'ALLOW';

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */