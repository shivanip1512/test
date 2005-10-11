/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

delete from yukonuserrole where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;
go
insert into YukonRoleProperty values(-70005,-700,'Mapping Interface','amfm','Optional interface to the AMFM mapping system');
insert into YukonRoleProperty values(-70006,-700,'CBC Creation Name','CBC %PAOName%','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-70007,-700,'PFactor Decimal Places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-70008,-700,'CBC Allow OVUV','false','Allows users to toggle OV/UV usage on capbanks');
go

update yukongrouprole set rolepropertyid = -70005, roleid = -700 where rolepropertyid = -10105
update yukongrouprole set rolepropertyid = -70006, roleid = -700 where rolepropertyid = -10106;
update yukongrouprole set rolepropertyid = -70007, roleid = -700 where rolepropertyid = -10109;
update yukongrouprole set rolepropertyid = -70008, roleid = -700 where rolepropertyid = -10112;
go

delete from yukonroleproperty where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;
go

update state set foregroundcolor = 10 where stategroupid = 3 and rawstate = 2;

insert into YukonRoleProperty values(-70009,-700,'CBC Refresh Rate','60','The rate, in seconds, all CBC clients reload data from the CBC server');
go

alter table yukonuser drop column logincount;
alter table yukonuser drop column lastlogin;
update contact set loginid = -9999 where loginid != -100 and loginid < 0;
insert into YukonListEntry values( 7, 1, 0, 'Voice PIN', 3 );
go

alter table CustomerAdditionalContact add Ordering smallint;
update CustomerAdditionalContact set Ordering = 0;
alter table CustomerAdditionalContact alter column Ordering smallint not null;
go

alter table ContactNotification add Ordering smallint;
update ContactNotification set Ordering = 0;
alter table ContactNotification alter column Ordering smallint not null;
go

create table ContactNotifGroupMap (
   ContactID            numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null
);
alter table ContactNotifGroupMap
   add constraint PK_CONTACTNOTIFGROUPMAP primary key  (ContactID, NotificationGroupID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go

create table CustomerNotifGroupMap (
   CustomerID           numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null
);
alter table CustomerNotifGroupMap
   add constraint PK_CUSTOMERNOTIFGROUPMAP primary key  (CustomerID, NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID);
go

alter table NotificationDestination add Attribs char(16);
go
update NotificationDestination set Attribs = '0000000000000000';
go
alter table NotificationDestination alter column Attribs char(16) not null;
go


alter table NotificationGroup drop column emailsubject;
go
alter table NotificationGroup drop column emailfromaddress;
go
alter table NotificationGroup drop column emailmessage;
go
alter table NotificationGroup drop column numericpagermessage;
go

alter table CapBank alter column OperationalState varchar(16) not null;
 
update YukonRoleProperty set Description = 'The number of seconds to wait for a confirmation from the time the call is initiated until the user listens to the notification.' where RolePropertyID = -1402;
update YukonRoleProperty set Description = 'The number of seconds to wait for a call to be connected.' where RolePropertyID = -1401;
go


sp_rename 'LMProgramDirect.NotifyOffset', 'NotifyActiveOffset', 'COLUMN';
go
alter table LMProgramDirect add NotifyInactiveOffset numeric;
go
update LMProgramDirect set NotifyInactiveOffset=0;
go
alter table LMProgramDirect alter column NotifyInactiveOffset numeric not null;
go

sp_rename 'DynamicLMProgramDirect.NotifyTime', 'NotifyActiveTime', 'COLUMN';
go
alter table DynamicLMProgramDirect add NotifyInactiveTime datetime;
go
update DynamicLMProgramDirect set NotifyInactiveTime = '01-JAN-1990';
go
alter table DynamicLMProgramDirect alter column NotifyInactiveTime datetime not null;
go


create table CICUSTOMERPOINTDATA (
   CustomerID           numeric              not null,
   PointID              numeric              not null,
   Type                 varchar(16)          not null,
   OptionalLabel        varchar(32)          not null
);
go
alter table CICUSTOMERPOINTDATA
   add constraint PK_CICUSTOMERPOINTDATA primary key  (CustomerID, PointID);
go
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID);
go

alter table Customer add CustomerNumber varchar(64);
go
update Customer set CustomerNumber = '(none)';
go
alter table Customer alter column CustomerNumber varchar(64) not null;
go


create table PAOSchedule (
   ScheduleID           numeric              not null,
   NextRunTime          datetime             not null,
   LastRunTime          datetime             not null,
   IntervalRate         numeric              not null,
   ScheduleName         varchar(64)          not null,
   Disabled             char(1)              not null
);
go
alter table PAOSchedule
   add constraint PK_PAOSCHEDULE primary key  (ScheduleID);
go

create table PAOScheduleAssignment (
   EventID              numeric              not null,
   ScheduleID           numeric              not null,
   PaoID                numeric              not null,
   Command              varchar(128)         not null
);
go
alter table PAOScheduleAssignment
   add constraint PK_PAOSCHEDULEASSIGNMENT primary key  (EventID, ScheduleID);
go
alter table PAOScheduleAssignment
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOSchedule (ScheduleID);
go
alter table PAOScheduleAssignment
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go

INSERT into point  values (-6, 'System', 'Notifcation', 0, 'Default', 0, 'N', 'N', 'S', 6, 'None', 0);
go

insert into YukonRole values (-800,'Outbound Calling','IVR','Settings for Interactive Voice Response module');
insert into YukonRoleProperty values(-80001,-800,'Number of Channels','1','The number of outgoing channels assigned to the specified voice application.');
insert into YukonRoleProperty values(-80002,-800,'Template Root','http://localhost:8080/template/','A URL base where the notification templates will be stored (file: or http: are okay).');
go

update YukonGroupRole set roleid = -800 where rolepropertyid = -1400;
update YukonRoleProperty set roleid = -800 where rolepropertyid = -1400;
insert into YukonRoleProperty values(-1403,-5,'Call Prefix','(none)','Any number or numbers that must be dialed before a call can be placed.');
go

alter table dynamiccccapbank add assumedstartverificationstatus numeric;
update dynamiccccapbank set assumedstartverificationstatus = 0;
alter table dynamiccccapbank alter column assumedstartverificationstatus numeric not null;
go

alter table dynamiccccapbank add prevverificationcontrolstatus numeric;
update dynamiccccapbank set prevverificationcontrolstatus = 0;
alter table dynamiccccapbank alter column prevverificationcontrolstatus numeric not null;
go

alter table dynamiccccapbank add verificationcontrolindex numeric;
update dynamiccccapbank set verificationcontrolindex = 0;
alter table dynamiccccapbank alter column verificationcontrolindex numeric not null;
gp

alter table dynamiccccapbank add AdditionalFlags varchar(32);
update dynamiccccapbank set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamiccccapbank alter column AdditionalFlags varchar(32) not null;
go

alter table dynamicccfeeder add AdditionalFlags varchar(32);
update dynamicccfeeder set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccfeeder alter column AdditionalFlags varchar(32) not null;
go

alter table dynamicccsubstationbus add AdditionalFlags varchar(32);
update dynamicccsubstationbus set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccsubstationbus alter column AdditionalFlags varchar(32) not null;
go

alter table dynamicccsubstationbus add currVerifyCBId numeric;
update dynamicccsubstationbus set currVerifyCBId = -1;
alter table dynamicccsubstationbus alter column currVerifyCBId numeric not null;
go

alter table dynamicccsubstationbus add currVerifyFeederId numeric;
update dynamicccsubstationbus set currVerifyFeederId = -1;
alter table dynamicccsubstationbus alter column currVerifyFeederId numeric not null;
go

alter table dynamicccsubstationbus add currVerifyCBOrigState numeric;
update dynamicccsubstationbus set currVerifyCBOrigState = 0;
alter table dynamicccsubstationbus alter column currVerifyCBOrigState numeric not null;
go

alter table dynamicccsubstationbus add verificationStrategy numeric;
update dynamicccsubstationbus set verificationStrategy = -1;
alter table dynamicccsubstationbus alter column verificationStrategy numeric not null;
go

alter table dynamicccsubstationbus add cbInactivityTime numeric;
update dynamicccsubstationbus set cbInactivityTime = -1;
alter table dynamicccsubstationbus alter column cbInactivityTime numeric not null;
go

insert into YukonSelectionList values (1065,'A','(none)','Customer account rate schedule selection','RateSchedule','Y');
go

insert into YukonListEntry values (1901,1065,0,'J',3601);
insert into YukonListEntry values (1902,1065,1,'PS',3602);
insert into YukonListEntry values (1903,1065,2,'Power Service Only',3603);
insert into YukonListEntry values (1904,1065,3,'Power & Lighting Service',3604);
insert into YukonListEntry values (1905, 1065, 4, 'PP', 3605);
insert into YukonListEntry values (1906, 1065, 5, 'PT', 3606);
go

alter table Customer add RateScheduleID numeric;
go
update Customer set RateScheduleID = 0;
go
alter table Customer alter column RateScheduleID numeric not null;
go
alter table Customer add constraint FK_Cust_YkLs foreign key (RateScheduleID) references YukonListEntry (EntryID);
go

insert into YukonRoleProperty values(-20846,-201,'Label Alt Tracking #','Alt Tracking #','Text of the alternate tracking number label on a customer account');
insert into yukongrouprole values (-2146,-303,-201,-20846,'(none)');
insert into yukongrouprole values (-846,-301,-201,-20846,'(none)');
go

alter table Customer add AltTrackNum varchar(64);
go
update Customer set AltTrackNum = '(none)';
go
alter table Customer alter column AltTrackNum varchar(64) not null;
go

update yukonservices set serviceclass = 'com.cannontech.notif.server.NotificationServer' where serviceid = -1 or serviceid = 1;


insert into YukonRoleProperty values(-20303,-203,'Direct Loadcontrol','true','Allows access to the Direct load management web interface');
go
insert into yukongrouprole values (-778,-301,-203,-20303,'(none)');
insert into YukonGroupRole values (-1278,-2,-203,-20303,'(none)');
go
insert into YukonUserRole values (-778,-1,-203,-20303,'(none)');


create table DeviceTNPPSettings (
   DeviceID             numeric              not null,
   Inertia              numeric              not null,
   DestinationAddress   numeric              not null,
   OriginAddress        numeric              not null,
   IdentifierFormat     char(1)              not null,
   Protocol             varchar(32)          not null,
   DataFormat           char(1)              not null,
   Channel              char(1)              not null,
   Zone                 char(1)              not null,
   FunctionCode         char(1)              not null,
   PagerID              numeric              not null
);
go
alter table DeviceTNPPSettings
   add constraint PK_DEVICETNPPSETTINGS primary key  (DeviceID);
go
alter table DeviceTNPPSettings
   add constraint FK_DevTNPP_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go

insert into YukonListEntry values(9,1,0,'Email to Cell', 1);
update YukonListEntry set YukonDefinitionID = 1 where EntryText = 'Pager Number';
update YukonListEntry set EntryText = 'Email to Pager'  where EntryText = 'Pager Number';
go

update YukonListEntry set YukonDefinitionID = 5 where EntryText = 'Fax Number';
insert into YukonListEntry values(8,1,0,'Cell Phone',2);
go


create table DevicePagingReceiverSettings (
   DeviceID             numeric              not null,
   CapCode1             numeric              not null,
   CapCode2             numeric              not null,
   CapCode3             numeric              not null,
   CapCode4             numeric              not null,
   CapCode5             numeric              not null,
   CapCode6             numeric              not null,
   CapCode7             numeric              not null,
   CapCode8             numeric              not null,
   CapCode9             numeric              not null,
   CapCode10            numeric              not null,
   CapCode11            numeric              not null,
   CapCode12            numeric              not null,
   CapCode13            numeric              not null,
   CapCode14            numeric              not null,
   CapCode15            numeric              not null,
   CapCode16            numeric              not null,
   Frequency            float                not null
);
go
alter table DevicePagingReceiverSettings
   add constraint PK_DEVICEPAGINGRECEIVERSETTING primary key  (DeviceID);
go
alter table DevicePagingReceiverSettings
   add constraint FK_DevPaRec_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go

insert into command values(-100, 'scan general', 'General Meter Scan', 'SENTINEL');
insert into command values(-101, 'scan general frozen', 'General Meter Scan Frozen', 'SENTINEL');
insert into command values(-102, 'scan general update', 'General Meter Scan and DB Update', 'SENTINEL');
insert into command values(-103, 'scan general frozen update', 'General Meter Scan Frozen and DB Update', 'SENTINEL');
insert into command values(-104, 'putvalue reset', 'Reset Demand', 'SENTINEL');

INSERT INTO DEVICETYPECOMMAND VALUES (-383, -100, 'SENTINEL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-384, -101, 'SENTINEL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-385, -102, 'SENTINEL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-386, -103, 'SENTINEL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-387, -104, 'SENTINEL', 5, 'Y');


insert into stategroup values(-7,'Thread Monitor','Status');
insert into state values(-7,0,'Normal',0,6,0);
insert into state values(-7,1,'NonCriticalFailure',1,6,0);
insert into state values(-7,2,'CriticalFailure',2,6,0);
insert into state values(-7,3,'Unresponsive',3,6,0);
go

insert into point values (-200,'Status','Porter Monitor',0,'Default',-7,'N','N','R',1000,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
go
insert into point values (-200,'Status','Dispatch Monitor',0,'Default',-7,'N','N','R',1001,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
go
insert into point values (-200,'Status','Scanner Monitor',0,'Default',-7,'N','N','R',1002,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
go
insert into point values (-200,'Status','Calc Monitor',0,'Default',-7,'N','N','R',1003,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
go
insert into point values (-200,'Status','Cap Control Monitor',0,'Default',-7,'N','N','R',1004,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
go
insert into point values (-200,'Status','FDR Monitor',0,'Default',-7,'N','N','R',1005,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
go
insert into point values (-200,'Status','Macs Monitor',0,'Default',-7,'N','N','R',1006,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
go


create table CapControlStrategy (
   StrategyID           numeric              not null,
   StrategyName         varchar(32)          not null,
   ControlMethod        varchar(32)          null,
   MAXDAILYOPERATION    numeric              not null,
   MaxOperationDisableFlag char(1)              not null,
   PEAKSTARTTIME        numeric              not null,
   PEAKSTOPTIME         numeric              not null,
   CONTROLINTERVAL      numeric              not null,
   MINRESPONSETIME      numeric              not null,
   MINCONFIRMPERCENT    numeric              not null,
   FAILUREPERCENT       numeric              not null,
   DAYSOFWEEK           char(8)              not null,
   ControlUnits         varchar(32)          not null,
   ControlDelayTime     numeric              not null,
   ControlSendRetries   numeric              not null,
   PeakLag              float                not null,
   PeakLead             float                not null,
   OffPkLag             float                not null,
   OffPkLead            float                not null
);
insert into CapControlStrategy values (0, '(none)', '(none)', 0, 'N', 0, 0, 0, 0, 0, 0, 'NYYYYYNN', '(none)', 0, 0, 0.0, 0.0, 0.0, 0.0);
alter table CapControlStrategy
   add constraint PK_CAPCONTROLSTRAT primary key  (StrategyID);
go
create unique  index Indx_CapCntrlStrat_name_UNQ on CapControlStrategy (
StrategyName
);
go

alter table CapControlFeeder add StrategyID numeric;
go
update CapControlFeeder set StrategyID = feederid;
go
alter table CapControlFeeder alter column StrategyID numeric not null;
go

insert into CapControlStrategy
select feederid, 'Fdr: '  paoname, 'IndividualFeeder',0,'N',0,0,0,0,0,0,'NYYYYYNN','(none)',0,0,
PEAKSETPOINT+UpperBandwidth, PEAKSETPOINT-LowerBandwidth, OFFPEAKSETPOINT+UpperBandwidth, OFFPEAKSETPOINT-LowerBandwidth
from capcontrolfeeder, yukonpaobject
where paobjectid = feederid;
go

update CapControlFeeder set StrategyID = 0
where PEAKSETPOINT = 0.0 and OFFPEAKSETPOINT = 0.0 and
UpperBandwidth = 0.0 and LowerBandwidth = 0.0;
go

delete from CapControlStrategy where strategyid not in
(select f.strategyid from CapControlFeeder f)
and strategyid > 0;
go

alter table CapControlSubstationBus add StrategyID numeric;
go
update CapControlSubstationBus set StrategyID = substationbusid;
go
alter table CapControlSubstationBus alter column StrategyID numeric not null;
go

insert into CapControlStrategy
select substationbusid, 'Sub: '  paoname, ControlMethod,MAXDAILYOPERATION,
MaxOperationDisableFlag,PEAKSTARTTIME,PEAKSTOPTIME,CONTROLINTERVAL,MINRESPONSETIME,
MINCONFIRMPERCENT,FAILUREPERCENT,DAYSOFWEEK,ControlUnits,ControlDelayTime,ControlSendRetries,
PEAKSETPOINT+UpperBandwidth, PEAKSETPOINT-LowerBandwidth, OFFPEAKSETPOINT+UpperBandwidth, OFFPEAKSETPOINT-LowerBandwidth
from capcontrolsubstationbus, yukonpaobject
where paobjectid = substationbusid;
go

alter table capcontrolfeeder drop column PEAKSETPOINT;
alter table capcontrolfeeder drop column OFFPEAKSETPOINT;
alter table capcontrolfeeder drop column UpperBandwidth;
alter table capcontrolfeeder drop column LowerBandwidth;
go

alter table CapControlSubstationBus drop column ControlMethod;
alter table CapControlSubstationBus drop column MAXDAILYOPERATION;
alter table CapControlSubstationBus drop column MaxOperationDisableFlag;
alter table CapControlSubstationBus drop column PEAKSETPOINT;
alter table CapControlSubstationBus drop column OFFPEAKSETPOINT;
alter table CapControlSubstationBus drop column PEAKSTARTTIME;
alter table CapControlSubstationBus drop column PEAKSTOPTIME;
alter table CapControlSubstationBus drop column UpperBandwidth;
alter table CapControlSubstationBus drop column CONTROLINTERVAL;
alter table CapControlSubstationBus drop column MINRESPONSETIME;
alter table CapControlSubstationBus drop column MINCONFIRMPERCENT;
alter table CapControlSubstationBus drop column FAILUREPERCENT;
alter table CapControlSubstationBus drop column DAYSOFWEEK;
alter table CapControlSubstationBus drop column LowerBandwidth;
alter table CapControlSubstationBus drop column ControlUnits;
alter table CapControlSubstationBus drop column ControlDelayTime;
alter table CapControlSubstationBus drop column ControlSendRetries;
go

alter table CAPCONTROLSUBSTATIONBUS add constraint FK_CCSUBB_CCSTR foreign key (StrategyID) references CapControlStrategy (StrategyID);
go
alter table CapControlFeeder add constraint FK_CCFDR_CCSTR foreign key (StrategyID) references CapControlStrategy (StrategyID);
go


alter table dynamicccsubstationbus add CurrentVoltPointValue numeric;
go
update dynamicccsubstationbus set CurrentVoltPointValue = 0;
go
alter table dynamicccsubstationbus alter column CurrentVoltPointValue numeric not null;
go

alter table dynamicccfeeder add CurrentVoltPointValue numeric;
go
update dynamicccfeeder set CurrentVoltPointValue = 0;
go
alter table dynamicccfeeder alter column CurrentVoltPointValue numeric not null;
go

alter table capcontrolsubstationbus add CurrentVoltLoadPointID numeric;
go
update capcontrolsubstationbus set CurrentVoltLoadPointID = 0;
go
alter table capcontrolsubstationbus alter column CurrentVoltLoadPointID numeric not null;
go

alter table capcontrolfeeder add CurrentVoltLoadPointID numeric;
go
update capcontrolfeeder set CurrentVoltLoadPointID = 0;
go
alter table capcontrolfeeder alter column CurrentVoltLoadPointID numeric not null;
go

insert into YukonRoleProperty values(-70010,-700,'Database Editing','false','Allows the user to view/modify the database set up for all CapControl items');
go

alter table CapBank add MaxDailyOps numeric;
go
update CapBank set MaxDailyOps = 0;
go
alter table CapBank alter column MaxDailyOps numeric not null;
go

alter table CapBank add MaxOpDisable char(1);
go
update CapBank set MaxOpDisable = 'N';
go
alter table CapBank alter column MaxOpDisable char(1) not null;
go

sp_rename 'dynamiccccapbank.currentdailyoperations', 'totaloperations', 'column';
go
alter table dynamiccccapbank add currentdailyoperations numeric;
go
update dynamiccccapbank set currentdailyoperations = 0;
go
alter table dynamiccccapbank alter column currentdailyoperations numeric not null;
go

















/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */
