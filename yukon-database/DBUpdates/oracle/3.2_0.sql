/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/
/* @error ignore */
set define off;

/* @error ignore */
alter table command modify command VARCHAR2(256);
/* @error ignore */
alter table command modify label VARCHAR2(256);

delete from yukonuserrole where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;

insert into YukonRoleProperty values(-70005,-700,'Mapping Interface','amfm','Optional interface to the AMFM mapping system');
insert into YukonRoleProperty values(-70006,-700,'CBC Creation Name','CBC %PAOName%','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-70007,-700,'PFactor Decimal Places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-70008,-700,'CBC Allow OVUV','false','Allows users to toggle OV/UV usage on capbanks');

update yukongrouprole set rolepropertyid = -70005, roleid = -700 where rolepropertyid = -10105;
update yukongrouprole set rolepropertyid = -70006, roleid = -700 where rolepropertyid = -10106;
update yukongrouprole set rolepropertyid = -70007, roleid = -700 where rolepropertyid = -10109;
update yukongrouprole set rolepropertyid = -70008, roleid = -700 where rolepropertyid = -10112;

delete from yukonroleproperty where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;

update state set foregroundcolor = 10 where stategroupid = 3 and rawstate = 2;

insert into YukonRoleProperty values(-70009,-700,'CBC Refresh Rate','60','The rate, in seconds, all CBC clients reload data from the CBC server');

alter table yukonuser drop column logincount;
alter table yukonuser drop column lastlogin;
update contact set loginid = -9999 where loginid != -100 and loginid < 0;
insert into YukonListEntry values( 7, 1, 0, 'Voice PIN', 3 );

alter table CustomerAdditionalContact add Ordering smallint;
update CustomerAdditionalContact set Ordering = 0;
alter table CustomerAdditionalContact modify Ordering smallint not null;

alter table ContactNotification add Ordering smallint;
update ContactNotification set Ordering = 0;
alter table ContactNotification modify Ordering smallint not null;

create table ContactNotifGroupMap  (
   ContactID            NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              char(16)                        not null
);
alter table ContactNotifGroupMap
   add constraint PK_CONTACTNOTIFGROUPMAP primary key (ContactID, NotificationGroupID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

create table CustomerNotifGroupMap  (
   CustomerID           NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              char(16)                        not null
);
alter table CustomerNotifGroupMap
   add constraint PK_CUSTOMERNOTIFGROUPMAP primary key (CustomerID, NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID);

alter table NotificationDestination add Attribs char(16);
update NotificationDestination set Attribs = 'YNYNNNNNNNNNNNNN';
alter table NotificationDestination modify Attribs char(16) not null;

alter table NotificationGroup drop column emailsubject;
alter table NotificationGroup drop column emailfromaddress;
alter table NotificationGroup drop column emailmessage;
alter table NotificationGroup drop column numericpagermessage;

alter table CapBank modify OperationalState varchar2(16);

update YukonRoleProperty set Description = 'The number of seconds to wait for a confirmation from the time the call is initiated until the user listens to the notification.' where RolePropertyID = -1402;
update YukonRoleProperty set Description = 'The number of seconds to wait for a call to be connected.' where RolePropertyID = -1401;

alter table LMProgramDirect rename column NotifyOffset to NotifyActiveOffset;

alter table LMProgramDirect add NotifyInactiveOffset number;
update LMProgramDirect set NotifyInactiveOffset = 0;
alter table LMProgramDirect modify NotifyInactiveOffset number not null;

alter table DynamicLMProgramDirect rename column NotifyTime to NotifyActiveTime;

alter table DynamicLMProgramDirect add NotifyInactiveTime date;
update DynamicLMProgramDirect set NotifyInactiveTime = '01-JAN-1990';
alter table DynamicLMProgramDirect modify NotifyInactiveTime date not null;

create table CICUSTOMERPOINTDATA  (
   CustomerID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   Type                 VARCHAR2(16)                    not null,
   OptionalLabel        VARCHAR2(32)                    not null
);
alter table CICUSTOMERPOINTDATA
   add constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, PointID);
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID);
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID);

alter table Customer add CustomerNumber varchar2(64);
update Customer set CustomerNumber = '(none)';
alter table Customer modify CustomerNumber varchar2(64) not null;

create table PAOSchedule  (
   ScheduleID           NUMBER                          not null,
   NextRunTime          DATE                            not null,
   LastRunTime          DATE                            not null,
   IntervalRate         NUMBER                          not null,
   ScheduleName         VARCHAR2(64)                    not null,
   Disabled             CHAR(1)                         not null
);
alter table PAOSchedule
   add constraint PK_PAOSCHEDULE primary key (ScheduleID);

create table PAOScheduleAssignment  (
   EventID              NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Command              VARCHAR2(128)                   not null
);
alter table PAOScheduleAssignment
   add constraint PK_PAOSCHEDULEASSIGNMENT primary key (EventID);
alter table PAOScheduleAssignment
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOSchedule (ScheduleID);
alter table PAOScheduleAssignment
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

INSERT into point  values (-6, 'System', 'Notifcation', 0, 'Default', 0, 'N', 'N', 'S', 6, 'None', 0);

insert into YukonRole values (-800,'Outbound Calling','IVR','Settings for Interactive Voice Response module');
insert into YukonRoleProperty values(-80001,-800,'Number of Channels','1','The number of outgoing channels assigned to the specified voice application.');
insert into YukonRoleProperty values(-80002,-800,'Template Root','http://localhost:8080/template/','A URL base where the notification templates will be stored (file: or http: are okay).');

update YukonGroupRole set roleid = -800 where rolepropertyid = -1400;
update YukonRoleProperty set roleid = -800 where rolepropertyid = -1400;
insert into YukonRoleProperty values(-1403,-5,'Call Prefix','(none)','Any number or numbers that must be dialed before a call can be placed.');



alter table dynamiccccapbank add assumedstartverificationstatus number;
update dynamiccccapbank set assumedstartverificationstatus = 0;
alter table dynamiccccapbank modify assumedstartverificationstatus number not null;

alter table dynamiccccapbank add prevverificationcontrolstatus number;
update dynamiccccapbank set prevverificationcontrolstatus = 0;
alter table dynamiccccapbank modify prevverificationcontrolstatus number not null;

alter table dynamiccccapbank add verificationcontrolindex number;
update dynamiccccapbank set verificationcontrolindex = 0;
alter table dynamiccccapbank modify verificationcontrolindex number not null;

alter table dynamiccccapbank add AdditionalFlags varchar2(32);
update dynamiccccapbank set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamiccccapbank modify AdditionalFlags varchar2(32) not null;

alter table dynamicccfeeder add AdditionalFlags varchar2(32);
update dynamicccfeeder set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccfeeder modify AdditionalFlags varchar2(32) not null;

alter table dynamicccsubstationbus add AdditionalFlags varchar2(32);
update dynamicccsubstationbus set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccsubstationbus modify AdditionalFlags varchar2(32) not null;

alter table dynamicccsubstationbus add currVerifyCBId number;
update dynamicccsubstationbus set currVerifyCBId = -1;
alter table dynamicccsubstationbus modify currVerifyCBId number not null;

alter table dynamicccsubstationbus add currVerifyFeederId number;
update dynamicccsubstationbus set currVerifyFeederId = -1;
alter table dynamicccsubstationbus modify currVerifyFeederId number not null;

alter table dynamicccsubstationbus add currVerifyCBOrigState number;
update dynamicccsubstationbus set currVerifyCBOrigState = 0;
alter table dynamicccsubstationbus modify currVerifyCBOrigState number not null;

alter table dynamicccsubstationbus add verificationStrategy number;
update dynamicccsubstationbus set verificationStrategy = -1;
alter table dynamicccsubstationbus modify verificationStrategy number not null;

alter table dynamicccsubstationbus add cbInactivityTime number;
update dynamicccsubstationbus set cbInactivityTime = -1;
alter table dynamicccsubstationbus modify cbInactivityTime number not null;

insert into YukonSelectionList values (1065,'A','(none)','Customer account rate schedule selection','RateSchedule','Y');

insert into YukonListEntry values (1901,1065,0,'J',3601);
insert into YukonListEntry values (1902,1065,1,'PS',3602);
insert into YukonListEntry values (1903,1065,2,'Power Service Only',3603);
insert into YukonListEntry values (1904,1065,3,'Power & Lighting Service',3604);
insert into YukonListEntry values (1905, 1065, 4, 'PP', 3605);
insert into YukonListEntry values (1906, 1065, 5, 'PT', 3606);

alter table Customer add RateScheduleID number;
update Customer set RateScheduleID = 0;
alter table Customer modify RateScheduleID number not null;

alter table Customer add constraint FK_Cust_YkLs foreign key (RateScheduleID) references YukonListEntry (EntryID);

insert into YukonRoleProperty values(-20846,-201,'Label Alt Tracking #','Alt Tracking #','Text of the alternate tracking number label on a customer account');
insert into yukongrouprole values (-2146,-303,-201,-20846,'(none)');
insert into yukongrouprole values (-846,-301,-201,-20846,'(none)');

alter table Customer add AltTrackNum varchar2(64);
update Customer set AltTrackNum = '(none)';
alter table Customer modify AltTrackNum varchar2(64) not null;

update yukonservices set serviceclass = 'com.cannontech.notif.server.NotificationServer' where serviceid = -1 or serviceid = 1;

insert into YukonRoleProperty values(-20303,-203,'Direct Loadcontrol','true','Allows access to the Direct load management web interface');
insert into yukongrouprole values (-778,-301,-203,-20303,'(none)');
insert into YukonGroupRole values (-1278,-2,-203,-20303,'(none)');
insert into YukonUserRole values (-778,-1,-203,-20303,'(none)');


create table DeviceTNPPSettings  (
   DeviceID             NUMBER                          not null,
   Inertia              NUMBER                          not null,
   DestinationAddress   NUMBER                          not null,
   OriginAddress        NUMBER                          not null,
   IdentifierFormat     CHAR(1)                         not null,
   Protocol             VARCHAR2(32)                    not null,
   DataFormat           CHAR(1)                         not null,
   Channel              CHAR(1)                         not null,
   Zone                 CHAR(1)                         not null,
   FunctionCode         CHAR(1)                         not null,
   PagerID              NUMBER                          not null
);
alter table DeviceTNPPSettings
   add constraint PK_DEVICETNPPSETTINGS primary key (DeviceID);
alter table DeviceTNPPSettings
   add constraint FK_DevTNPP_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);

insert into YukonListEntry values(9,1,0,'Email to Cell', 1);
update YukonListEntry set YukonDefinitionID = 1 where EntryText = 'Pager Number';
update YukonListEntry set EntryText = 'Email to Pager'  where EntryText = 'Pager Number';

update YukonListEntry set YukonDefinitionID = 5 where EntryText = 'Fax Number';
insert into YukonListEntry values(8,1,0,'Cell Phone',2);

create table DevicePagingReceiverSettings  (
   DeviceID             NUMBER                          not null,
   CapCode1             NUMBER                          not null,
   CapCode2             NUMBER                          not null,
   CapCode3             NUMBER                          not null,
   CapCode4             NUMBER                          not null,
   CapCode5             NUMBER                          not null,
   CapCode6             NUMBER                          not null,
   CapCode7             NUMBER                          not null,
   CapCode8             NUMBER                          not null,
   CapCode9             NUMBER                          not null,
   CapCode10            NUMBER                          not null,
   CapCode11            NUMBER                          not null,
   CapCode12            NUMBER                          not null,
   CapCode13            NUMBER                          not null,
   CapCode14            NUMBER                          not null,
   CapCode15            NUMBER                          not null,
   CapCode16            NUMBER                          not null,
   Frequency            FLOAT                           not null
);
alter table DevicePagingReceiverSettings
   add constraint PK_DEVICEPAGINGRECEIVERSETTING primary key (DeviceID);
alter table DevicePagingReceiverSettings
   add constraint FK_DevPaRec_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);

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

insert into point values (-200,'Status','Porter Monitor',0,'Default',-7,'N','N','R',1000,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
insert into pointstatus select max(pointID), 0, 'none', 'N', 0, 0, 0, 'none','none',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;

insert into point values (-200,'Status','Dispatch Monitor',0,'Default',-7,'N','N','R',1001,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
insert into pointstatus select max(pointID), 0, 'none', 'N', 0, 0, 0, 'none','none',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;

insert into point values (-200,'Status','Scanner Monitor',0,'Default',-7,'N','N','R',1002,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
insert into pointstatus select max(pointID), 0, 'none', 'N', 0, 0, 0, 'none','none',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;

insert into point values (-200,'Status','Calc Monitor',0,'Default',-7,'N','N','R',1003,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
insert into pointstatus select max(pointID), 0, 'none', 'N', 0, 0, 0, 'none','none',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;

insert into point values (-200,'Status','Cap Control Monitor',0,'Default',-7,'N','N','R',1004,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
insert into pointstatus select max(pointID), 0, 'none', 'N', 0, 0, 0, 'none','none',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;

insert into point values (-200,'Status','FDR Monitor',0,'Default',-7,'N','N','R',1005,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
insert into pointstatus select max(pointID), 0, 'none', 'N', 0, 0, 0, 'none','none',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;

insert into point values (-200,'Status','Macs Monitor',0,'Default',-7,'N','N','R',1006,'None',0);
update point set pointID = (select max(pointID)+1 from point) where pointID = -200;
insert into pointstatus select max(pointID), 0, 'none', 'N', 0, 0, 0, 'none','none',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;


create table CapControlStrategy  (
   StrategyID           NUMBER                          not null,
   StrategyName         VARCHAR2(32)                    not null,
   ControlMethod        VARCHAR2(32),
   MAXDAILYOPERATION    NUMBER                          not null,
   MaxOperationDisableFlag CHAR(1)                         not null,
   PEAKSTARTTIME        NUMBER                          not null,
   PEAKSTOPTIME         NUMBER                          not null,
   CONTROLINTERVAL      NUMBER                          not null,
   MINRESPONSETIME      NUMBER                          not null,
   MINCONFIRMPERCENT    NUMBER                          not null,
   FAILUREPERCENT       NUMBER                          not null,
   DAYSOFWEEK           CHAR(8)                         not null,
   ControlUnits         VARCHAR2(32)                    not null,
   ControlDelayTime     NUMBER                          not null,
   ControlSendRetries   NUMBER                          not null,
   PeakLag              FLOAT                           not null,
   PeakLead             FLOAT                           not null,
   OffPkLag             FLOAT                           not null,
   OffPkLead            FLOAT                           not null
);
insert into CapControlStrategy values (0, '(none)', '(none)', 0, 'N', 0, 0, 0, 0, 0, 0, 'NYYYYYNN', '(none)', 0, 0, 0.0, 0.0, 0.0, 0.0);
alter table CapControlStrategy
   add constraint PK_CAPCONTROLSTRAT primary key (StrategyID);
create unique index Indx_CapCntrlStrat_name_UNQ on CapControlStrategy (
   StrategyName ASC
);


alter table CapControlFeeder add StrategyID number;
update CapControlFeeder set StrategyID = feederid;
alter table CapControlFeeder modify StrategyID number not null;


insert into CapControlStrategy
select feederid, 'Fdr: '  paoname, 'IndividualFeeder',0,'N',0,0,0,0,0,0,'NYYYYYNN','(none)',0,0,
PEAKSETPOINT+UpperBandwidth, PEAKSETPOINT-LowerBandwidth, OFFPEAKSETPOINT+UpperBandwidth, OFFPEAKSETPOINT-LowerBandwidth
from capcontrolfeeder, yukonpaobject
where paobjectid = feederid;

update CapControlFeeder set StrategyID = 0
where PEAKSETPOINT = 0.0 and OFFPEAKSETPOINT = 0.0 and
UpperBandwidth = 0.0 and LowerBandwidth = 0.0;

delete from CapControlStrategy where strategyid not in
(select f.strategyid from CapControlFeeder f)
and strategyid > 0;

alter table CapControlSubstationBus add StrategyID number;
update CapControlSubstationBus set StrategyID = substationbusid;
alter table CapControlSubstationBus modify StrategyID number not null;

insert into CapControlStrategy
select substationbusid, 'Sub: '  paoname, ControlMethod,MAXDAILYOPERATION,
MaxOperationDisableFlag,PEAKSTARTTIME,PEAKSTOPTIME,CONTROLINTERVAL,MINRESPONSETIME,
MINCONFIRMPERCENT,FAILUREPERCENT,DAYSOFWEEK,ControlUnits,ControlDelayTime,ControlSendRetries,
PEAKSETPOINT+UpperBandwidth, PEAKSETPOINT-LowerBandwidth, OFFPEAKSETPOINT+UpperBandwidth, OFFPEAKSETPOINT-LowerBandwidth
from capcontrolsubstationbus, yukonpaobject
where paobjectid = substationbusid;

alter table capcontrolfeeder drop column PEAKSETPOINT;
alter table capcontrolfeeder drop column OFFPEAKSETPOINT;
alter table capcontrolfeeder drop column UpperBandwidth;
alter table capcontrolfeeder drop column LowerBandwidth;

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

alter table CAPCONTROLSUBSTATIONBUS add constraint FK_CCSUBB_CCSTR foreign key (StrategyID) references CapControlStrategy (StrategyID);
alter table CapControlFeeder add constraint FK_CCFDR_CCSTR foreign key (StrategyID) references CapControlStrategy (StrategyID);

alter table dynamicccsubstationbus add CurrentVoltPointValue number;
update dynamicccsubstationbus set CurrentVoltPointValue = 0;
alter table dynamicccsubstationbus modify CurrentVoltPointValue number not null;

alter table dynamicccfeeder add CurrentVoltPointValue number;
update dynamicccfeeder set CurrentVoltPointValue = 0;
alter table dynamicccfeeder modify CurrentVoltPointValue number not null;

alter table capcontrolsubstationbus add CurrentVoltLoadPointID number;
update capcontrolsubstationbus set CurrentVoltLoadPointID = 0;
alter table capcontrolsubstationbus modify CurrentVoltLoadPointID number not null;

alter table capcontrolfeeder add CurrentVoltLoadPointID number;
update capcontrolfeeder set CurrentVoltLoadPointID = 0;
alter table capcontrolfeeder modify CurrentVoltLoadPointID number not null;

insert into YukonRoleProperty values(-70010,-700,'Database Editing','false','Allows the user to view/modify the database set up for all CapControl items');

alter table CapBank add MaxDailyOps number;
update CapBank set MaxDailyOps = 0;
alter table CapBank modify MaxDailyOps number not null;

alter table CapBank add MaxOpDisable char(1);
update CapBank set MaxOpDisable = 'N';
alter table CapBank modify MaxOpDisable char(1) not null;

alter table DynamicccCapBank rename column CurrentDailyOperations to TotalOperations;

alter table dynamiccccapbank add currentdailyoperations number;
update dynamiccccapbank set currentdailyoperations = 0;
alter table dynamiccccapbank modify currentdailyoperations number not null;

insert into YukonListEntry values(1138, 1011, 0, 'Company', 1609);

insert into YukonSelectionList values( 100, 'A', 'Calc Functions', 'DBEditor calc point functions', 'CalcFunctions', 'N' );

insert into YukonListEntry values (100, 100, 0, 'Addition', 0);
insert into YukonListEntry values (101, 100, 0, 'Subtraction', 0);
insert into YukonListEntry values (102, 100, 0, 'Multiplication', 0);
insert into YukonListEntry values (103, 100, 0, 'Division', 0);
insert into YukonListEntry values (104, 100, 0, 'Logical AND', 0);
insert into YukonListEntry values (105, 100, 0, 'Logical OR', 0);
insert into YukonListEntry values (106, 100, 0, 'Logical NOT', 0);
insert into YukonListEntry values (107, 100, 0, 'Logical XOR', 0);
insert into YukonListEntry values (108, 100, 0, 'Greater than', 0);
insert into YukonListEntry values (109, 100, 0, 'Geq than', 0);
insert into YukonListEntry values (110, 100, 0, 'Less than', 0);
insert into YukonListEntry values (111, 100, 0, 'Leq than', 0);
insert into YukonListEntry values (112, 100, 0, 'Min', 0);
insert into YukonListEntry values (113, 100, 0, 'Max', 0);
insert into YukonListEntry values (114, 100, 0, 'Baseline', 0);
insert into YukonListEntry values (115, 100, 0, 'Baseline Percent', 0);
insert into YukonListEntry values (116, 100, 0, 'DemandAvg15', 0);
insert into YukonListEntry values (117, 100, 0, 'DemandAvg30', 0);
insert into YukonListEntry values (118, 100, 0, 'DemandAvg60', 0);
insert into YukonListEntry values (119, 100, 0, 'P-Factor kW/kVAr', 0);
insert into YukonListEntry values (120, 100, 0, 'P-Factor kW/kQ', 0);
insert into YukonListEntry values (121, 100, 0, 'P-Factor kW/kVA', 0);
insert into YukonListEntry values (122, 100, 0, 'kVAr from kW/kQ', 0);
insert into YukonListEntry values (123, 100, 0, 'kVA from kW/kVAr', 0);
insert into YukonListEntry values (124, 100, 0, 'kVA from kW/kQ', 0);
insert into YukonListEntry values (125, 100, 0, 'COS from P/Q', 0);
insert into YukonListEntry values (126, 100, 0, 'Squared', 0);
insert into YukonListEntry values (127, 100, 0, 'Square Root', 0);
insert into YukonListEntry values (128, 100, 0, 'ArcTan', 0);
insert into YukonListEntry values (129, 100, 0, 'Max Difference', 0);
insert into YukonListEntry values (130, 100, 0, 'Absolute Value', 0);
insert into YukonListEntry values (131, 100, 0, 'kW from kVA/kVAr', 0);
insert into YukonListEntry values (132, 100, 0, 'Modulo Divide', 0);
insert into YukonListEntry values (133, 100, 0, 'State Timer', 0);

alter table DynamicTags modify UserName varchar2(64);
alter table DynamicPointAlarming modify UserName varchar2(64);
alter table TagLog modify UserName varchar2(64);
alter table SYSTEMLOG modify UserName varchar2(64);

create table ConfigurationName  (
   ConfigID             NUMBER                          not null,
   ConfigName           VARCHAR2(40)                    not null
);
alter table ConfigurationName
   add constraint PK_CONFIGURATIONNAME primary key (ConfigID);
create unique index AK_CONFNM_NAME on ConfigurationName (
   ConfigName ASC
);

create table DeviceConfiguration  (
   DeviceID             NUMBER                          not null,
   ConfigID             NUMBER                          not null
);
alter table DeviceConfiguration
   add constraint PK_DEVICECONFIGURATION primary key (DeviceID);
alter table DeviceConfiguration
   add constraint FK_DevConf_ConfName foreign key (ConfigID)
      references ConfigurationName (ConfigID);
alter table DeviceConfiguration
   add constraint FK_DevConf_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);

create table ConfigurationPartsName  (
   PartID               NUMBER                          not null,
   PartName             VARCHAR2(40)                    not null,
   PartType             VARCHAR2(40)                    not null
);
alter table ConfigurationPartsName
   add constraint PK_CONFIGURATIONPARTSNAME primary key (PartID);
create unique index AK_CONFPTNM_NAME on ConfigurationPartsName (
   PartName ASC
);

create table ConfigurationParts  (
   ConfigID             NUMBER                          not null,
   PartID               NUMBER                          not null,
   ConfigRowID          NUMBER                          not null
);
alter table ConfigurationParts
   add constraint PK_CONFIGURATIONPARTS primary key (ConfigRowID);
alter table ConfigurationParts
   add constraint FK_ConfPart_ConfName foreign key (ConfigID)
      references ConfigurationName (ConfigID);
alter table ConfigurationParts
   add constraint FK_ConfPart_ConfPartName foreign key (PartID)
      references ConfigurationPartsName (PartID);

create table ConfigurationValue  (
   PartID               NUMBER                          not null,
   ValueID              VARCHAR2(40)                    not null,
   Value                VARCHAR2(40)                    not null,
   ConfigRowID          NUMBER                          not null
);
alter table ConfigurationValue
   add constraint PK_CONFIGURATIONVALUE primary key (ConfigRowID);
alter table ConfigurationValue
   add constraint FK_ConfVal_ConfPart foreign key (PartID)
      references ConfigurationPartsName (PartID);


insert into billingfileformats values( -17, 'SEDC (yyyyMMdd)');
insert into billingfileformats values( -18, 'ATS');

insert into YukonRoleProperty values(-10305,-103,'Commands Group Name','Default Commands','The commands group name for the displayed commands.');
insert into YukonGroupRole values(-175,-100,-103,-10305,'(none)');
insert into YukonGroupRole values(-1075,-2,-103,-10305,'(none)');
insert into YukonUserRole values(-175,-1,-103,-10305,'(none)');

create table CommandGroup(
   CommandGroupID              NUMBER          not null,
   CommandGroupName            VARCHAR2(60)          not null
);
insert into CommandGroup values (-1, 'Default Commands');
alter table CommandGroup
   add constraint PK_COMMANDGROUP primary key (CommandGroupID);
alter table CommandGroup
   add constraint AK_KEY_CmdGrp_Name unique  (CommandGroupName);


alter table DeviceTypeCommand drop constraint PK_DEVICETYPECOMMAND;
/* @error ignore */
drop index PK_DEVICETYPECOMMAND;
alter table DeviceTypeCommand add CommandGroupID NUMBER;
update DeviceTypeCommand set CommandGroupID = -1;
alter table DeviceTypeCommand modify CommandGroupID not null;

alter table DeviceTypeCommand
   add constraint PK_DEVICETYPECOMMAND primary key  (CommandGroupID, DeviceCommandID);

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);

create index Indx_DevTypeCmd_GroupID on DeviceTypeCommand (
CommandGroupID ASC
);

insert into command values(-105, 'getvalue lp channel ?''Channel (1 or 4)'' ?''MM/DD/YYYY HH:mm''', 'Read block of data (six intevals) from start date/time specified', 'MCT-410IL');
insert into command values(-106, 'getvalue outage ?''Outage Log (1 - 6)''', 'Read two outages per read.  Specify 1 (returns 1&2), 3 (returns 3&4), 5 (returns 5&6)', 'MCT-410IL');
insert into command values(-107, 'getvalue peak frozen', 'Read frozen demand - kW and kWh', 'MCT-410IL');
insert into command values(-108, 'getvalue voltage frozen', 'Read frozen voltage - min, max', 'MCT-410IL');
insert into command values(-109, 'getvalue powerfail reset', 'Reset blink counter', 'MCT-410IL');
insert into command values(-111, 'getconfig intervals', 'Read rates for Last Interval Demand, Load Profile Demand, Voltage Last Interval Demand, Voltage Profile Demand', 'MCT-410IL');
insert into command values(-112, 'putconfig emetcon intervals', 'Write rate intervals from database to MCT', 'MCT-410IL');
insert into command values(-113, 'putstatus emetcon freeze ?''(one or two)''', 'Reset current peak demand, write current peak demand - kW and kWh to frozen register', 'MCT-410IL');
insert into command values(-114, 'putstatus emetcon freeze voltage ?''(one or two)''', 'Reset current min/max voltage, write current min/max voltage to frozen register', 'MCT-410IL');

INSERT INTO DEVICETYPECOMMAND VALUES (-388, -105, 'MCT-410IL', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-389, -106, 'MCT-410IL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-390, -107, 'MCT-410IL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-391, -108, 'MCT-410IL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-392, -109, 'MCT-410IL', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-393, -110, 'MCT-410IL', 21, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-394, -111, 'MCT-410IL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-395, -112, 'MCT-410IL', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-396, -113, 'MCT-410IL', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-397, -114, 'MCT-410IL', 25, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-398, -105, 'MCT-410CL', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-399, -106, 'MCT-410CL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-400, -107, 'MCT-410CL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-401, -108, 'MCT-410CL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-402, -109, 'MCT-410CL', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-403, -110, 'MCT-410CL', 21, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-404, -111, 'MCT-410CL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-405, -112, 'MCT-410CL', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-406, -113, 'MCT-410CL', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-407, -114, 'MCT-410CL', 25, 'Y', -1);














/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Ryan', '20-Oct-2005', 'Manual version insert done', 0 );
