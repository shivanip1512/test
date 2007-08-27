/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/* @error ignore-begin */
create table dynamicccarea ( AreaID numeric not null, additionalflags varchar(20) not null );

alter table dynamicccarea
   add constraint FK_ccarea_Dynccarea foreign key (areaID)
      references Capcontrolarea (areaID);

insert into dynamicccarea (areaid, additionalflags) select areaid, 'NNNNNNNNNNNNNNNNNNNN' from capcontrolarea; 

/*==============================================================*/
/* Table: DYNAMICBILLINGFORMAT                                  */
/*==============================================================*/
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

/*==============================================================*/
/* Table: DYNAMICBILLINGFIELD                                   */
/*==============================================================*/
create table DYNAMICBILLINGFIELD  (
   id                   NUMBER                          not null,
   FormatID             NUMBER                          not null,
   FieldName            VARCHAR2(50)                    not null,
   FieldOrder           NUMBER                          not null,
   FieldFormat          VARCHAR2(50)
)
;


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
;

insert into sequenceNumber values (100, 'BillingFileFormats');
update yukonpaobject set type = 'MCT-430SL' where type = 'MCT-430SN' or type = 'MCT430SN';
update devicetypecommand set devicetype = 'MCT-430SL' where devicetype = 'MCT-430SN' or devicetype = 'MCT430SN';

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

delete from LMThermostatSeasonEntry where SeasonID in (select SeasonID from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100)));
delete from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
delete from ECToGenericMapping where MappingCategory = 'LMThermostatSchedule' and ItemID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
delete from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
update LMHardwareBase set LMHardwareTypeID = 0 where LMHardwareTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
delete from YukonlistEntry where YukonDefinitionID = 3100;
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


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */