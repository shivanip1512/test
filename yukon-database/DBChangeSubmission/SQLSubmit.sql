/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!*/

<<<<<<< SQLSubmit.sql
/*Fixes YUK-4109, needs to be in creation and update scripts for 3.5 and HEAD*/
insert into YukonGroupRole values(-20,-1,-1,-1019,'(none)');
<<<<<<< SQLSubmit.sql
insert into YukonGroupRole values(-21,-1,-1,-1020,'(none)');

/************ YUK-3997 add ability to attach 3 phase var points to sub/feeder *********/ 
alter table capcontrolsubstationbus add usephasedata char(1);
go
update capcontrolsubstationbus set usephasedata = 0;
go
alter table capcontrolsubstationbus alter column usephasedata char(1) not null;
go 
alter table capcontrolsubstationbus add phaseb numeric;
go
update capcontrolsubstationbus set phaseb = 0;
go
alter table capcontrolsubstationbus alter column phaseb numeric not null;
go
alter table capcontrolsubstationbus add phasec numeric;
go
update capcontrolsubstationbus set phasec = 0;
go
alter table capcontrolsubstationbus alter column phasec numeric not null;
go
alter table capcontrolfeeder add usephasedata char(1);
go
update capcontrolfeeder set usephasedata = 0;
go
alter table capcontrolfeeder alter column usephasedata char(1) not null;
go
alter table capcontrolfeeder add phaseb numeric;
go
update capcontrolfeeder set phaseb = 0;
go
alter table capcontrolfeeder alter column phaseb numeric not null;
go
alter table capcontrolfeeder add phasec numeric;
go
update capcontrolfeeder set phasec = 0;
go
alter table capcontrolfeeder alter column phasec numeric not null;
go
alter table dynamicccfeeder add phaseavalue float;
go
update dynamicccfeeder set phaseavalue = 0;
go
alter table dynamicccfeeder alter column phaseavalue float not null;
go
alter table dynamicccfeeder add phasebvalue float;
go
update dynamicccfeeder set phasebvalue = 0;
go
alter table dynamicccfeeder alter column phasebvalue float not null;
go
alter table dynamicccfeeder add phasecvalue float;
go
update dynamicccfeeder set phasecvalue = 0;
go
alter table dynamicccfeeder alter column phasecvalue float not null;
go
alter table dynamicccsubstationbus add phaseavalue float;
go
update dynamicccsubstationbus set phaseavalue = 0;
go
alter table dynamicccsubstationbus alter column phaseavalue float not null;
go
alter table dynamicccsubstationbus add phasebvalue float;
go
update dynamicccsubstationbus set phasebvalue = 0;
go
alter table dynamicccsubstationbus alter column phasebvalue float not null;
go
alter table dynamicccsubstationbus add phasecvalue float;
go
update dynamicccsubstationbus set phasecvalue = 0;
go
alter table dynamicccsubstationbus alter column phasecvalue float not null;
/************ END YUK-3997 add ability to attach 3 phase var points to sub/feeder *********/ 

/******* YUK-4089 cap-control web editor add seasonal control strategies. **************/
go
insert into seasonSchedule values (-1,'No Season');
go
insert into dateOfSeason values(-1, 'Default', 1,1,12,31);
go
create table ccseasonstrategyassignment
(
paobjectid numeric not null,
seasonscheduleid numeric not null,
seasonname varchar(20) not null,
strategyid numeric not null
);
go
alter table ccseasonstrategyassignment
   add constraint PK_ccseasonstrategyassignment primary key  (paobjectid);
go
alter table ccseasonstrategyassignment
   add constraint FK_ccseasonstrategyassignment_PaoId foreign key (paobjectid)
      references yukonpaobject (paobjectid);
go
alter table ccseasonstrategyassignment
   add constraint FK_ccseasonstrategyassignment_schedid foreign key (seasonscheduleid)
      references seasonschedule (scheduleid);
go
alter table ccseasonstrategyassignment
   add constraint FK_ccseasonstrategyassignment_season foreign key (seasonscheduleid, seasonname)
      references dateofseason (seasonscheduleid, seasonname);
go
alter table ccseasonstrategyassignment
   add constraint FK_ccseasonstrategyassignment_strat foreign key (strategyid)
      references capcontrolstrategy (strategyid);
go
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
go
alter table capcontrolarea drop column strategyid;
go 
alter table capcontrolsubstationbus drop column strategyid;
go
alter table capcontrolfeeder drop column strategyid;

/******* END YUK-4089 cap-control web editor add seasonal control strategies. **************/
=======
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
<<<<<<< SQLSubmit.sql
>>>>>>> 1.114
=======

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

create table dynamicccSPECIALAREA ( AreaID numeric not null,
									additionalflags varchar(20) not null );

/*********************************************************************************************/

/* 3.5 and head  for tom */
insert into YukonRoleProperty values(-10815, -108,'Data Updater Delay (milliseconds)', '4000', 'The number of milliseconds between requests for the latest point values on pages that support the data updater.');
/* end */


/********* New billing file format for    3.4    3.5  and HEAD    SN 08/14/2007*/
insert into billingfileformats values(-26, 'SIMPLE_TOU_DeviceName');
/**********************/

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

/********* 430 name change (head only) - Jason 8/13/07    */
/* Note - In addition to these changes, please do a find  */
/* and replace in the creation scripts - change any 430SN */
/* inserts into 430SL  */

update yukonpaobject set type = 'MCT-430SL' where type = 'MCT-430SN' or type = 'MCT430SN'
update devicetypecommand set devicetype = 'MCT-430SL' where devicetype = 'MCT-430SN' or devicetype = 'MCT430SN'
/**********************************/

>>>>>>> 1.122
=======
/*New issue, check YUK-4334 for reference */
NEED TO CHECK TO SEE WHETHER THE USERPAOWNER TO USERPAOPERMISSION TRANSITION IN THE 3.4_0.sql file
is working correctly to move the default route group mapping for STARS energy companies.

If a default route is not loading correctly in STARS, then run the following query:
insert into UserPaoPermission (UserPaoPermissionID, UserID, PaoID, Permission) select 0, ec.userID, macro.OwnerID, 'allow LM visibility'
from EnergyCompany ec, LMGroupExpressCom exc, GenericMacro macro 
where macro.MacroType = 'GROUP' and macro.ChildID = exc.LMGroupID and exc.SerialNumber = '0' and ec.EnergyCompanyID = 0;

If an ID is returned, then the mapping failed and the default route is not correctly saved.

There are several questions we need to answer on this issue, so this can stay open for awhile.  
/*I'm marking this in here for reference until we figure out the best way to deal with this.  Jon*/

/********************** (HEAD only) - nmeverden 9/05/07 **********************/
update YukonRoleProperty SET Description='Set the default authentication type to use {PLAIN,HASH_SHA,RADIUS,AD,LDAP,NONE}' where RolePropertyID = -1307

insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1308,-4,'LDAP DN','dc=example,dc=com','LDAP Distinguished Name')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1309,-4,'LDAP User Suffix','ou=users','LDAP User Suffix')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1310,-4,'LDAP User Prefix','uid=','LDAP User Prefix')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1311,-4,'LDAP Server Address','127.0.0.1','LDAP Server Address')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1312,-4,'LDAP Server Port','389','LDAP Server Port')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1313,-4,'LDAP Server Timeout','30','LDAP Server Timeout (in seconds)')

insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1314,-4,'Active Directory Server Address','127.0.0.1','Active Directory Server Address')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1315,-4,'Active Directory Server Port','389','Active Directory Server Port')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1316,-4,'Active Directory Server Timeout','30','Active Directory Server Timeout (in seconds)')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1317,-4,'Active Directory NT Domain Name','(none)','Active Directory NT DOMAIN NAME')

insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-50,-1,-4,-1308,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-51,-1,-4,-1309,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-52,-1,-4,-1310,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-53,-1,-4,-1311,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-54,-1,-4,-1312,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-55,-1,-4,-1313,'(none)')

insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-56,-1,-4,-1314,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-57,-1,-4,-1315,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-58,-1,-4,-1316,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-59,-1,-4,-1317,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-92,-1,-4,-1307,'(none)')
<<<<<<< SQLSubmit.sql
/*****************************************************************************/
>>>>>>> 1.126
=======
/*****************************************************************************/


/** CHANGE CREATION SCRIPTS ONLY FOR THIS IN HEAD AND 3.5  - SN 20070918 
These insert statements already exist, but the formatType values are being changed, removing the "turtle" text */
insert into billingfileformats values( 13, 'NISC',1);
insert into billingfileformats values( -19, ' NISC No Limit kWh ',1);
/*** */


/** Head only capcontrol changes  -Thain 20070921 */
alter table ccfeederbanklist alter column controlorder float;
alter table ccfeederbanklist alter column closeorder float;
alter table ccfeederbanklist alter column triporder float;

/** Head only TOU Widget db updates */
USE [sw_cart40]
GO
/****** Object:  Table [dbo].[TouAttributeMapping]    Script Date: 09/24/2007 10:54:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[TouAttributeMapping](
	[touId] [int] IDENTITY(1,1) NOT NULL,
	[displayName] [varchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[peakAttribute] [varchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[usageAttribute] [varchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [PK_TouAttributeMapping] PRIMARY KEY CLUSTERED 
(
	[touId] ASC
)WITH (IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('Normal', 'PEAK_DEMAND', 'USAGE');

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('B', 'TOU_RATE_B_PEAK', 'TOU_RATE_B_USAGE');

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('C', 'TOU_RATE_C_PEAK', 'TOU_RATE_C_USAGE');

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('D', 'TOU_RATE_D_PEAK', 'TOU_RATE_D_USAGE');

insert into YukonRoleProperty values(-20204,-202,'Enable Tou','true','Allows access to Tou(Time of use) data');  
insert into YukonRoleProperty values(-20205,-202,'Enable Device Group','true','Allows access to change device groups for a device');  

>>>>>>> 1.130

/*****JULIE: YUK-4372 Revice ccevent table to allow easier joining...*****************/
alter table cceventlog add actionId numeric;
go
update cceventlog set actionId = -1;
go
alter table cceventlog alter column actionId numeric not null;
/***********************************************************************************/

/*****JULIE: updating capcontrol strategy to reflect minus values that the operator must enter. *****************/
update capcontrolstrategy set peaklead = -peaklead where peaklead > 0 and  controlunits = 'kVar';
update capcontrolstrategy set offpklead = -offpklead where offpklead > 0 and  controlunits = 'kVar';
/***********************************************************************************/


/********************* Device Configuration Additions (Head only) -Jason ******************************/
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

/*****************************************************************************/
/* nmeverden - YUK-4438 */

delete from YukonListEntry where EntryText = 'Configuration'

/*****************************************************************************/
