/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!*/

/*Fixes YUK-4109, needs to be in creation and update scripts for 3.5 and HEAD*/
insert into YukonGroupRole values(-20,-1,-1,-1019,'(none)');
insert into YukonGroupRole values(-21,-1,-1,-1020,'(none)');

/* Needed in 3.4.6, 3.5, and Head  TS*/
insert into yukonroleproperty values (-100011,-1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into yukonroleproperty values (-100012,-1000, 'Substation Last Update Timestamp', 'true', 'is last update timstamp shown for substations');
insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timstamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timstamp shown for capbanks');
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100008;
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100007;


/* Needed in 3.4.6, 3.5, and Head  8-6-7*/
insert into yukonroleproperty values (-100105,-1001, 'Target', 'true', 'is target stat displayed');

/********* Needed for 3.5 and Head    SN 8-8-07 */
/* Scheduler Role */
insert into YukonRole values(-212,'Scheduler','Operator','Operator access to Scheduler'); 
/* Scheduler Role properties */
insert into YukonRoleProperty values(-21200,-212,'Enable/Disable Schedule','true','Right to enable or disable a schedule'); 
/**********************************/

/* MACS YUK-4118 fix 8-13-2007 in  3.4.6  3.5 and head*/
ALTER TABLE DeviceReadJobLog DROP CONSTRAINT FK_DEVICERE_FK_DRJOBL_MACSCHED
/************/

/* CapControlSpecialArea and CCSubSpecialAreaAssignment Tables added: Needed in Head  8-13-07*/

create table CAPCONTROLSPECIALAREA ( AreaID numeric not null );

create table CCSUBSPECIALAREAASSIGNMENT (

   AreaID numeric not null,
   SubstationBusID numeric not null,
   DisplayOrder numeric not null

);
/*********************************************************************************************/

/* 3.5 and head  for tom */
insert into YukonRoleProperty values(-10815, -108,'Data Updater Delay (milliseconds)', '4000', 'The number of milliseconds between requests for the latest point values on pages that support the data updater.');
/* end */


/*********** Michael/Jason's Dynamic billing generation additions (Head - 4.0 only)    ***********/

create table DynamicBillingField (
	 id numeric,
	 FormatID numeric NOT NULL,
	 FieldName varchar(50) NOT NULL,
	 FieldOrder numeric NOT NULL,
	 FieldFormat varchar(50),
	 primary key (id),
	 Foreign key (FormatID) REFERENCES BillingFileFormats (FormatID)
 )
 
 create table DynamicBillingFormat (
	 FormatID numeric NOT NULL,
	 Delimiter varchar(20),
	 Header varchar(255),
	 Footer varchar(255),
	 primary key (FormatID),
	 Foreign key (FormatID) REFERENCES BillingFileFormats (FormatID)
 )
 
alter table BillingFileFormats ALTER COLUMN FormatType varchar(100);

alter table BillingFileFormats ADD SystemFormat bit;
update BillingFileFormats SET SystemFormat=1;

insert into sequenceNumber values (100, 'BillingFileFormats');

/* ATS */
insert into DynamicBillingFormat VALUES( '-18',',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','');
insert into DynamicBillingField values(1,-18,'Plain Text',0,'M')
insert into DynamicBillingField values(2,-18, 'meterNumber',1,'')
insert into DynamicBillingField values(3,-18, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(4,-18, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(5,-18, 'totalConsumption - timestamp',4,'yy/MM/dd')
insert into DynamicBillingField values(6,-18,'totalPeakDemand - reading',5,'##0.000')
insert into DynamicBillingField values(7,-18,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(8,-18,'totalPeakDemand - timestamp',7,'yy/MM/dd')
insert into DynamicBillingField values(9,-18,'Plain Text',8,'')
insert into DynamicBillingField values(10,-18,'Plain Text',9,'')
insert into DynamicBillingField values(11,-18,'Plain Text',10,';')

/* DAFFRON */
insert into DynamicBillingFormat values(6,',','H    Meter    kWh   Time   Date   Peak   PeakT  PeakD  Stat Sig Freq Phase','')
insert into DynamicBillingField values(12,6,'Plain Text',0,'M')
insert into DynamicBillingField values(13,6, 'meterNumber',1,'')
insert into DynamicBillingField values(14,6, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(15,6, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(16,6, 'totalConsumption - timestamp',4,'yy/MM/dd')
insert into DynamicBillingField values(17,6,'totalPeakDemand - reading',5,'##0.000')
insert into DynamicBillingField values(18,6,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(19,6,'totalPeakDemand - timestamp',7,'yy/MM/dd')
insert into DynamicBillingField values(20,6,'Plain Text',8,'  6')
insert into DynamicBillingField values(21,6,'Plain Text',9,'')
insert into DynamicBillingField values(22,6,'Plain Text',10,';')

/* NCDC */
insert into DynamicBillingFormat values(7,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD','')
insert into DynamicBillingField values(23,7,'Plain Text',0,'M')
insert into DynamicBillingField values(24,7, 'meterNumber',1,'')
insert into DynamicBillingField values(25,7, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(26,7, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(27,7, 'totalConsumption - timestamp',4,'yyyy/MM/dd')
insert into DynamicBillingField values(28,7,'totalPeakDemand - reading',5,'##0.00')
insert into DynamicBillingField values(29,7,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(30,7,'totalPeakDemand - timestamp',7,'yyyy/MM/dd')

/* NISC_NCDC */
insert into DynamicBillingFormat values(14,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD','')
insert into DynamicBillingField values(31,14,'Plain Text',0,'M')
insert into DynamicBillingField values(32,14, 'meterNumber',1,'')
insert into DynamicBillingField values(33,14, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(34,14, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(35,14, 'totalConsumption - timestamp',4,'MM/dd/yyyy')
insert into DynamicBillingField values(36,14,'totalPeakDemand - reading',5,'##0.00')
insert into DynamicBillingField values(37,14,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(38,14,'totalPeakDemand - timestamp',7,'MM/dd/yyyy')

/* NISC_NoLimt_kWh */
insert into DynamicBillingFormat values(-19,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','')
insert into DynamicBillingField values(39,-19,'Plain Text',0,'M')
insert into DynamicBillingField values(40,-19, 'meterNumber',1,'')
insert into DynamicBillingField values(41,-19, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(42,-19, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(43,-19, 'totalConsumption - timestamp',4,'yy/MM/dd')
insert into DynamicBillingField values(44,-19,'totalPeakDemand - reading',5,'##0.00')
insert into DynamicBillingField values(45,-19,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(46,-19,'totalPeakDemand - timestamp',7,'yy/MM/dd')
insert into DynamicBillingField values(47,-19,'Plain Text',8,'')
insert into DynamicBillingField values(48,-19,'Plain Text',9,'')
insert into DynamicBillingField values(49,-19,'Plain Text',10,';')

/* NISC */
insert into DynamicBillingFormat values(13,',','H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','')
insert into DynamicBillingField values(50,13,'Plain Text',0,'M')
insert into DynamicBillingField values(51,13, 'meterNumber',1,'')
insert into DynamicBillingField values(52,13, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(53,13, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(54,13, 'totalConsumption - timestamp',4,'yy/MM/dd')
insert into DynamicBillingField values(55,13,'totalPeakDemand - reading',5,'##0.00')
insert into DynamicBillingField values(56,13,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(57,13,'totalPeakDemand - timestamp',7,'yy/MM/dd')
insert into DynamicBillingField values(58,13,'Plain Text',8,'')
insert into DynamicBillingField values(59,13,'Plain Text',9,'')
insert into DynamicBillingField values(60,13,'Plain Text',10,';')

/* SEDC_yyyyMMdd */
insert into DynamicBillingFormat values(-17,',', 'H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','')
insert into DynamicBillingField values(61,-17,'Plain Text',0,'M')
insert into DynamicBillingField values(62,-17, 'meterNumber',1,'')
insert into DynamicBillingField values(63,-17, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(64,-17, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(65,-17, 'totalConsumption - timestamp',4,'yyyy/MM/dd')
insert into DynamicBillingField values(66,-17,'totalPeakDemand - reading',5,'##0.000')
insert into DynamicBillingField values(67,-17,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(68,-17,'totalPeakDemand - timestamp',7,'yyyy/MM/dd')
insert into DynamicBillingField values(69,-17,'Plain Text',8,'')
insert into DynamicBillingField values(70,-17,'Plain Text',9,'')
insert into DynamicBillingField values(71,-17,'Plain Text',10,';')

/* SEDC54 */
insert into DynamicBillingFormat values(12,',', 'H    Meter    kWh   Time   Date','')
insert into DynamicBillingField values(72,12,'Plain Text',0,'M')
insert into DynamicBillingField values(73,12, 'meterNumber',1,'')
insert into DynamicBillingField values(74,12, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(75,12, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(76,12, 'totalConsumption - timestamp',4,'yy/MM/dd')
insert into DynamicBillingField values(77,12, 'Plain Text',5,'')

/* SEDC */
insert into DynamicBillingFormat values(0,',', 'H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase','')
insert into DynamicBillingField values(78,0,'Plain Text',0,'M')
insert into DynamicBillingField values(79,0, 'meterNumber',1,'')
insert into DynamicBillingField values(80,0, 'totalConsumption - reading',2,'#####')
insert into DynamicBillingField values(81,0, 'totalConsumption - timestamp',3,'HH:mm')
insert into DynamicBillingField values(82,0, 'totalConsumption - timestamp',4,'yy/MM/dd')
insert into DynamicBillingField values(83,0,'totalPeakDemand - reading',5,'##0.000')
insert into DynamicBillingField values(84,0,'totalPeakDemand - timestamp',6,'HH:mm')
insert into DynamicBillingField values(85,0,'totalPeakDemand - timestamp',7,'yy/MM/dd')
insert into DynamicBillingField values(86,0,'Plain Text',8,'')
insert into DynamicBillingField values(87,0,'Plain Text',9,'')
insert into DynamicBillingField values(88,0,'Plain Text',10,';')

/* SimpleTOU */
insert into DynamicBillingFormat values(21,',','','')
insert into DynamicBillingField values(89,21,'meterNumber',0,'')
insert into DynamicBillingField values(90,21,'totalConsumption - reading',1,'#####')
insert into DynamicBillingField values(91,21,'totalConsumption - timestamp',2,'HH:mm')
insert into DynamicBillingField values(92,21,'totalConsumption - timestamp',3,'MM/dd/yyyy')
insert into DynamicBillingField values(93,21,'totalPeakDemand - reading',4,'##0.000')
insert into DynamicBillingField values(94,21,'totalPeakDemand - timestamp',5,'HH:mm')
insert into DynamicBillingField values(95,21,'totalPeakDemand - timestamp',6,'MM/dd/yyyy')
insert into DynamicBillingField values(96,21,'rateAConsumption - reading',7,'#####')
insert into DynamicBillingField values(97,21,'rateADemand- reading',8,'##0.000')
insert into DynamicBillingField values(98,21,'rateADemand- timestamp',9,'HH:mm')
insert into DynamicBillingField values(99,21,'rateADemand- timestamp',10,'MM/dd/yyyy')
insert into DynamicBillingField values(100,21,'rateBConsumption - reading',11,'#####')
insert into DynamicBillingField values(101,21,'rateBDemand- reading',12,'##0.000')
insert into DynamicBillingField values(102,21,'rateBDemand- timestamp',13,'HH:mm')
insert into DynamicBillingField values(103,21,'rateBDemand- timestamp',14,'MM/dd/yyyy')

/*********** END END END Michael/Jason's Dynamic billing generation additions (Head - 4.0 only)    ***********/

