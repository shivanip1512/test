/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

create table LMGroupSA305  (
   GroupID              NUMBER                           not null,
   RouteID              NUMBER                           not null,
   AddressUsage         VARCHAR2(8)                      not null,
   UtilityAddress       NUMBER                           not null,
   GroupAddress         NUMBER                           not null,
   DivisionAddress      NUMBER                           not null,
   SubstationAddress    NUMBER                           not null,
   IndividualAddress    VARCHAR2(16)                     not null,
   RateFamily           NUMBER                           not null,
   RateMember           NUMBER                           not null,
   RateHierarchy        NUMBER                           not null,
   LoadNumber           VARCHAR2(8)                      not null
);
alter table LMGroupSA305
   add constraint PK_LMGROUPSA305 primary key (GroupID);
alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID);
alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID);

create table LMGroupSA205105  (
   GroupID              NUMBER                           not null,
   RouteID              NUMBER                           not null,
   OperationalAddress   NUMBER                           not null,
   LoadNumber           VARCHAR2(64)                     not null
);
alter table LMGroupSA205105
   add constraint PK_LMGROUPSA205105 primary key (GroupID);
alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID);
alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID);


create table LMGroupSASimple  (
   GroupID              NUMBER                           not null,
   RouteID              NUMBER                           not null,
   OperationalAddress   VARCHAR2(8)                      not null,
   NominalTimeout       NUMBER                           not null,
   MarkIndex            NUMBER                           not null,
   SpaceIndex           NUMBER                           not null
);
alter table LMGroupSASimple
   add constraint PK_LMGROUPSASIMPLE primary key (GroupID);
alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID);
alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID);

create table LMControlScenarioProgram  (
   ScenarioID           NUMBER                           not null,
   ProgramID            NUMBER                           not null,
   StartDelay           NUMBER                           not null,
   StopOffset           NUMBER                           not null,
   StartGear            NUMBER                           not null
);
alter table LMControlScenarioProgram
   add constraint PK_LMCONTROLSCENARIOPROGRAM primary key (ScenarioID, ProgramID);
alter table LMControlScenarioProgram
   add constraint FK_LMC_REF__LMP foreign key (ProgramID)
      references LMPROGRAM (DeviceID);
alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID);


create table LMProgramConstraints  (
   ConstraintID         NUMBER                           not null,
   ConstraintName       VARCHAR2(60)                     not null,
   AvailableSeasons     VARCHAR2(4)                      not null,
   AvailableWeekDays    VARCHAR2(8)                      not null,
   MaxHoursDaily        NUMBER                           not null,
   MaxHoursMonthly      NUMBER                           not null,
   MaxHoursSeasonal     NUMBER                           not null,
   MaxHoursAnnually     NUMBER                           not null,
   MinActivateTime      NUMBER                           not null,
   MinRestartTime       NUMBER                           not null,
   MaxDailyOps          NUMBER                           not null,
   MaxActivateTime      NUMBER                           not null,
   HolidayScheduleID    NUMBER                           not null,
   SeasonScheduleID     NUMBER                           not null
);
insert into LMProgramConstraints values (0, 'Default Constraint', 'YYYY', 'YYYYYYYN', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
alter table LMProgramConstraints
   add constraint PK_PRGCONSTR primary key (ConstraintID);


alter table LMProgram add ConstraintID number;
update LMProgram set ConstraintID = 0;
alter table LMProgram modify ConstraintID number not null;
alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID);


create or replace view LMProgram_View (DeviceID, ControlType, ConstraintID , ConstraintName , AvailableSeasons , AvailableWeekDays , MaxHoursDaily , MaxHoursMonthly , MaxHoursSeasonal , MaxHoursAnnually , MinActivateTime , MinRestartTime , MaxDailyOps , MaxActivateTime , HolidayScheduleID , SeasonScheduleID ) as
select t.DEVICEID, t.CONTROLTYPE, u.ConstraintID, u.ConstraintName, u.AvailableSeasons, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID;


insert into LMProgramConstraints 
select deviceid, CONCAT('Constraint: ', LTRIM(deviceid) ), AvailableSeasons, AvailableWeekdays, MaxHoursDaily,
MaxHoursMonthly, MaxHoursSeasonal, MaxHoursAnnually, MinActivateTime, MinRestartTime,
0, 0, HolidayScheduleID, SeasonScheduleID from LMProgram;


update LMProgram set constraintid = deviceid;

/* @error ignore */
alter table LMProgram drop constraint FK_SesSch_LmPr;
/* @error ignore */
alter table LMProgram drop constraint FK_HlSc_LmPr;

alter table LMProgram drop column AvailableSeasons;
alter table LMProgram drop column AvailableWeekdays;
alter table LMProgram drop column MaxHoursDaily;
alter table LMProgram drop column MaxHoursMonthly;
alter table LMProgram drop column MaxHoursSeasonal;
alter table LMProgram drop column MaxHoursAnnually;
alter table LMProgram drop column MinActivateTime;
alter table LMProgram drop column MinRestartTime;
alter table LMProgram drop column HolidayScheduleID;
alter table LMProgram drop column SeasonScheduleID;


/* @error ignore */
drop table portstatistics;

alter table LMProgramDirect add NotifyInterval number;
update LMProgramDirect set NotifyInterval = 0;
alter table LMProgramDirect modify NotifyInterval numeric not null;

alter table LMProgramDirect add Heading varchar2(40);
update LMProgramDirect set Heading = '(none)';
alter table LMProgramDirect modify Heading varchar2(40) not null;

alter table LMProgramDirect add MessageHeader varchar2(160);
update LMProgramDirect set MessageHeader = '(none)';
alter table LMProgramDirect modify MessageHeader varchar2(160) not null;

alter table LMProgramDirect add MessageFooter varchar2(160);
update LMProgramDirect set MessageFooter = '(none)';
alter table LMProgramDirect modify MessageFooter varchar2(160) not null;

alter table LMProgramDirect add CanceledMsg varchar2(80);
update LMProgramDirect set CanceledMsg = '(none)';
alter table LMProgramDirect modify CanceledMsg varchar2(80) not null;

alter table LMProgramDirect add StoppedEarlyMsg varchar2(80);
update LMProgramDirect set StoppedEarlyMsg = '(none)';
alter table LMProgramDirect modify StoppedEarlyMsg varchar2(80) not null;


create table DeviceRTC  (
   DeviceID             NUMBER                           not null,
   RTCAddress           NUMBER                           not null,
   Response             VARCHAR2(1)                      not null,
   LBTMode              NUMBER                           not null,
   DisableVerifies      VARCHAR2(1)                      not null
);
alter table DeviceRTC
   add constraint PK_DEVICERTC primary key (DeviceID);
alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID);


insert into YukonRoleProperty values(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
insert into YukonRoleProperty values(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
insert into YukonRoleProperty values(-10303,-103,'DCU SA203 Serial','false','Show a DCU SA205 Serial Number SortBy display');
insert into YukonRoleProperty values(-10304,-103,'DCU SA305 Serial','false','Show a DCU SA305 Serial Number SortBy display');

insert into YukonGroupRole values(-171,-100,-103,-10301,'true');
insert into YukonGroupRole values(-172,-100,-103,-10302,'true');
insert into YukonGroupRole values(-173,-100,-103,-10303,'false');
insert into YukonGroupRole values(-174,-100,-103,-10304,'false');

insert into YukonGroupRole values(-1071,-2,-103,-10301,'true');
insert into YukonGroupRole values(-1072,-2,-103,-10302,'true');
insert into YukonGroupRole values(-1073,-2,-103,-10303,'false');
insert into YukonGroupRole values(-1074,-2,-103,-10304,'false');

insert into YukonUserRole values(-171,-1,-103,-10301,'true');
insert into YukonUserRole values(-172,-1,-103,-10302,'true');
insert into YukonUserRole values(-173,-1,-103,-10303,'false');
insert into YukonUserRole values(-174,-1,-103,-10304,'false');


rename DeviceDNP TO DeviceAddress;


create table LMDirectNotifGrpList  (
   ProgramID            NUMBER                           not null,
   NotificationGrpID    NUMBER                           not null
);
alter table LMDirectNotifGrpList
   add constraint PK_LMDIRECTNOTIFGRPLIST primary key (ProgramID, NotificationGrpID);
alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID);
alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID);


create table DeviceSeries5RTU  (
   DeviceID             NUMBER                           not null,
   TickTime             NUMBER                           not null,
   TransmitOffset       NUMBER                           not null,
   SaveHistory          CHAR(1)                          not null,   
   PowerValueHighLimit  NUMBER                           not null,
   PowerValueLowLimit   NUMBER                           not null,
   PowerValueMultiplier FLOAT                            not null,
   PowerValueOffset     FLOAT                            not null,
   StartCode            NUMBER                           not null,
   StopCode             NUMBER                           not null
);
alter table DeviceSeries5RTU
   add constraint PK_DEVICESERIES5RTU primary key (DeviceID);
alter table DeviceSeries5RTU
   add constraint FK_DvS5r_Dv2w foreign key (DeviceID)
      references DEVICE2WAYFLAGS (DEVICEID);


/* More role properties to customize text in the nav */
insert into YukonRoleProperty values(-20835,-201,'Label Account General','General','Text of the account general link');
insert into YukonRoleProperty values(-20836,-201,'Label Account Contacts','Contacts','Text of the account contacts link');
insert into YukonRoleProperty values(-20837,-201,'Label Account Residence','Residence','Text of the account residence link');
insert into YukonRoleProperty values(-20838,-201,'Label Call Tracking','Call Tracking','Text of the call tracking link');
insert into YukonRoleProperty values(-20839,-201,'Label Create Call','Create Call','Text of the create call link');
insert into YukonRoleProperty values(-20840,-201,'Label Service Request','Service Request','Text of the service request link');
insert into YukonRoleProperty values(-20841,-201,'Label Service History','Service History','Text of the service history link');
insert into YukonRoleProperty values(-20842,-201,'Label Change Login','Change Login','Text of the change login link');
insert into YukonRoleProperty values(-20843,-201,'Label FAQ','FAQ','Text of the FAQ link');
insert into YukonRoleProperty values(-20844,-201,'Label Interval Data','Interval Data','Text of the interval data link');
insert into YukonRoleProperty values(-20857,-201,'Title Call Tracking','ACCOUNT - CALL TRACKING','Title of the call tracking page');
insert into YukonRoleProperty values(-20858,-201,'Title Create Call','ACCOUNT - CREATE NEW CALL','Title of the create call page');
insert into YukonRoleProperty values(-20859,-201,'Title Service Request','WORK ORDERS - SERVICE REQUEST','Title of the service request page');
insert into YukonRoleProperty values(-20860,-201,'Title Service History','WORK ORDERS - SERVICE HISTORY','Title of the service history page');
insert into YukonRoleProperty values(-20861,-201,'Title Change Login','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
insert into YukonRoleProperty values(-20862,-201,'Title Create Trend','METERING - CREATE NEW TREND','Title of the create trend page');
insert into YukonRoleProperty values(-20880,-201,'Heading Account','Account','Heading of the account links');
insert into YukonRoleProperty values(-20881,-201,'Heading Metering','Metering','Heading of the metering links');
insert into YukonRoleProperty values(-20882,-201,'Heading Programs','Programs','Heading of the program links');
insert into YukonRoleProperty values(-20883,-201,'Heading Appliances','Appliances','Heading of the appliance links');
insert into YukonRoleProperty values(-20884,-201,'Heading Hardwares','Hardwares','Heading of the hardware links');
insert into YukonRoleProperty values(-20885,-201,'Heading Work Orders','Work Orders','Heading of the work order links');
insert into YukonRoleProperty values(-20886,-201,'Heading Administration','Administration','Heading of the administration links');
insert into YukonRoleProperty values(-20887,-201,'Sub-Heading Switches','Switches','Sub-heading of the switch links');
insert into YukonRoleProperty values(-20888,-201,'Sub-Heading Thermostats','Thermostats','Sub-heading of the thermostat links');
insert into YukonRoleProperty values(-20889,-201,'Sub-Heading Meters','Meters','Sub-heading of the meter links');

insert into YukonRoleProperty values(-40135,-400,'Label Account General','General','Text of the account general link');
insert into YukonRoleProperty values(-40136,-400,'Label Contact Us','Contact Us','Text of the contact us link');
insert into YukonRoleProperty values(-40137,-400,'Label FAQ','FAQ','Text of the FAQ link');
insert into YukonRoleProperty values(-40138,-400,'Label Change Login','Change Login','Text of the change login link');
insert into YukonRoleProperty values(-40190,-400,'Heading Account','Account','Heading of the account links');
insert into YukonRoleProperty values(-40191,-400,'Heading Thermostat','Thermostat','Heading of the thermostat links');
insert into YukonRoleProperty values(-40192,-400,'Heading Metering','Metering','Heading of the metering links');
insert into YukonRoleProperty values(-40193,-400,'Heading Programs','Programs','Heading of the program links');
insert into YukonRoleProperty values(-40194,-400,'Heading Trending','Trending','Heading of the trending links');
insert into YukonRoleProperty values(-40195,-400,'Heading Questions','Questions','Heading of the questions links');
insert into YukonRoleProperty values(-40196,-400,'Heading Administration','Administration','Heading of the administration links');

insert into yukongrouprole values (-635,-300,-400,-40135,'(none)');
insert into yukongrouprole values (-636,-300,-400,-40136,'(none)');
insert into yukongrouprole values (-637,-300,-400,-40137,'(none)');
insert into yukongrouprole values (-638,-300,-400,-40138,'(none)');
insert into yukongrouprole values (-690,-300,-400,-40190,'(none)');
insert into yukongrouprole values (-691,-300,-400,-40191,'(none)');
insert into yukongrouprole values (-692,-300,-400,-40192,'(none)');
insert into yukongrouprole values (-693,-300,-400,-40193,'(none)');
insert into yukongrouprole values (-694,-300,-400,-40194,'(none)');
insert into yukongrouprole values (-695,-300,-400,-40195,'(none)');
insert into yukongrouprole values (-696,-300,-400,-40196,'(none)');

insert into yukongrouprole values (-835,-301,-201,-20835,'(none)');
insert into yukongrouprole values (-836,-301,-201,-20836,'(none)');
insert into yukongrouprole values (-837,-301,-201,-20837,'(none)');
insert into yukongrouprole values (-838,-301,-201,-20838,'(none)');
insert into yukongrouprole values (-839,-301,-201,-20839,'(none)');
insert into yukongrouprole values (-840,-301,-201,-20840,'(none)');
insert into yukongrouprole values (-841,-301,-201,-20841,'(none)');
insert into yukongrouprole values (-842,-301,-201,-20842,'(none)');
insert into yukongrouprole values (-843,-301,-201,-20843,'(none)');
insert into yukongrouprole values (-844,-301,-201,-20844,'(none)');
insert into yukongrouprole values (-857,-301,-201,-20857,'(none)');
insert into yukongrouprole values (-858,-301,-201,-20858,'(none)');
insert into yukongrouprole values (-859,-301,-201,-20859,'(none)');
insert into yukongrouprole values (-860,-301,-201,-20860,'(none)');
insert into yukongrouprole values (-861,-301,-201,-20861,'(none)');
insert into yukongrouprole values (-862,-301,-201,-20862,'(none)');
insert into yukongrouprole values (-880,-301,-201,-20880,'(none)');
insert into yukongrouprole values (-881,-301,-201,-20881,'(none)');
insert into yukongrouprole values (-882,-301,-201,-20882,'(none)');
insert into yukongrouprole values (-883,-301,-201,-20883,'(none)');
insert into yukongrouprole values (-884,-301,-201,-20884,'(none)');
insert into yukongrouprole values (-885,-301,-201,-20885,'(none)');
insert into yukongrouprole values (-886,-301,-201,-20886,'(none)');
insert into yukongrouprole values (-887,-301,-201,-20887,'(none)');
insert into yukongrouprole values (-888,-301,-201,-20888,'(none)');
insert into yukongrouprole values (-889,-301,-201,-20889,'(none)');

/* Hardware addressing role property */
insert into YukonRoleProperty values(-1107,-2,'track_hardware_addressing','false','Controls whether to track the hardware addressing information.');

/* Authentication (safeword) Role/Properties */
insert into YukonRole values(-4,'Authentication','Yukon','Settings for using an authentication server to login instead of standard yukon login.');

insert into YukonRoleProperty values(-1300,-4,'server_address','127.0.0.1','Authentication server machine address');
insert into YukonRoleProperty values(-1301,-4,'auth_port','1812','Authentication port.');
insert into YukonRoleProperty values(-1302,-4,'acct_port','1813','Accounting port.');
insert into YukonRoleProperty values(-1303,-4,'secret_key','cti','Client machine secret key value, defined by the server.'); 
insert into YukonRoleProperty values(-1304,-4,'auth_method','(none)','Authentication method. Possible values are (none) | PAP, [chap, others to follow soon]');
insert into YukonRoleProperty values(-1305,-4,'authentication_mode','Yukon','Authentication mode to use.  Valid values are:   Yukon | Radius');

insert into YukonGroupRole values(-85,-1,-4,-1300,'(none)');
insert into YukonGroupRole values(-86,-1,-4,-1301,'(none)');
insert into YukonGroupRole values(-87,-1,-4,-1302,'(none)');
insert into YukonGroupRole values(-88,-1,-4,-1303,'(none)');
insert into YukonGroupRole values(-89,-1,-4,-1304,'(none)');
insert into YukonGroupRole values(-90,-1,-4,-1305,'(none)');

alter table DynamicLMProgramDirect add DailyOps number;
update DynamicLMProgramDirect set DailyOps = 0;
alter table DynamicLMProgramDirect modify DailyOps number not null;


alter table LMProgramDirectGear add RampInInterval number;
update LMProgramDirectGear set RampInInterval = 0;
alter table LMProgramDirectGear modify RampInInterval number not null;

alter table LMProgramDirectGear add RampInPercent number;
update LMProgramDirectGear set RampInPercent = 0;
alter table LMProgramDirectGear modify RampInPercent number not null;

alter table LMProgramDirectGear add RampOutInterval number;
update LMProgramDirectGear set RampOutInterval = 0;
alter table LMProgramDirectGear modify RampOutInterval number not null;

alter table LMProgramDirectGear add RampOutPercent number;
update LMProgramDirectGear set RampOutPercent = 0;
alter table LMProgramDirectGear modify RampOutPercent number not null;


alter table LMProgramDirect rename column NotifyInterval to NotifyOffset;
alter table LMProgramDirect drop column CanceledMsg;
alter table LMProgramDirect drop column StoppedEarlyMsg;


alter table DynamicLMProgramDirect ADD NotifyTime DATE;
UPDATE DynamicLMProgramDirect SET NotifyTime ='01-JAN-1990';
alter TABLE DynamicLMProgramDirect MODIFY NotifyTime NOT NULL;


alter table DeviceSeries5RTU add Retries number;
update DeviceSeries5RTU set Retries = 0;
alter table DeviceSeries5RTU modify Retries number not null;

alter table LMControlScenarioProgram rename column StartDelay to StartOffset;


update YukonListEntry set EntryText='LCR-5000(Xcom)' where EntryID=1051;
update YukonListEntry set YukonDefinitionID=1305 where EntryID=1052;
update YukonListEntry set YukonDefinitionID=1306 where EntryID=1053;
update YukonListEntry set YukonDefinitionID=1307 where EntryID=1054;
update YukonListEntry set YukonDefinitionID=1308 where EntryID=1055;
insert into YukonListEntry values (1060,1005,0,'SA-205',1309);
insert into YukonListEntry values (1061,1005,0,'SA-305',1310);
insert into YukonListEntry values (1062,1005,0,'LCR-5000(Vcom)',1311);

update yukonuserrole set value = 'true' where userroleid = -107;


alter table DynamicLMProgramDirect add StartedRampingOut date;
update DynamicLMProgramDirect set StartedRampingOut = '01-JAN-1990';
alter table DynamicLMProgramDirect modify StartedRampingOut date not null;

alter table DynamicLMGroup add NextControlTime date;
update DynamicLMGroup set NextControlTime = '01-JAN-1990';
alter table DynamicLMGroup modify NextControlTime date not null;

alter table DynamicLMGroup add InternalState number;
update DynamicLMGroup set InternalState = 0;
alter table DynamicLMGroup modify InternalState number not null;

update displaycolumns set typenum = 7 where title = 'Additional Info' and displaynum < 99;



/* @error ignore */
alter table dynamiclmgroup drop constraint FK_DyLmGr_LmPrDGr;
/* @error ignore */
alter table DynamicLMGroup drop constraint PK_DYNAMICLMGROUP;
delete from DynamicLMGroup;
alter table DynamicLMGroup add constraint PK_DYNAMICLMGROUP primary key (DeviceID);

alter table dynamiclmgroup DROP COLUMN lmprogramid;

alter table LMProgramConstraints drop column AvailableSeasons;

/*==============================================================*/
/* Table : DateOfSeason                                         */
/*==============================================================*/
create table DateOfSeason  (
   SeasonScheduleID     NUMBER                           not null,
   SeasonName           VARCHAR2(20)                     not null,
   SeasonStartMonth     NUMBER                           not null,
   SeasonStartDay       NUMBER                           not null,
   SeasonEndMonth       NUMBER                           not null,
   SeasonEndDay         NUMBER                           not null
);
alter table DateOfSeason
   add constraint PK_DATEOFSEASON primary key (SeasonScheduleID, SeasonName);
alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);

alter table SeasonSchedule drop column WinterDay;
alter table SeasonSchedule drop column WinterMonth;
alter table SeasonSchedule drop column FallDay;
alter table SeasonSchedule drop column FallMonth;
alter table SeasonSchedule drop column SummerDay;
alter table SeasonSchedule drop column SummerMonth;
alter table SeasonSchedule drop column SpringDay;
alter table SeasonSchedule drop column SpringMonth;

delete from SeasonSchedule where scheduleid > 0;



/* Billing AMR role */
insert into YukonRole values (-500,'Billing','AMR','Access to billing file generation.');

/* Billing AMR role properties */
insert into YukonRoleProperty values(-50000,-500,'Header Label','Billing','The header label for billing.');
insert into YukonRoleProperty values(-50001,-500,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
insert into YukonRoleProperty values(-50002,-500,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
insert into YukonRoleProperty values(-50003,-500,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
insert into YukonRoleProperty values(-50004,-500,'Append To File','false','Append to existing file.');
insert into YukonRoleProperty values(-50005,-500,'Remove Multiplier','false','Remove the multiplier value from the reading.');
insert into YukonRoleProperty values(-50006,-500,'Input File Location','c:\yukon\client\bin\BillingIn.txt','The NCDC format takes in an input file.');
insert into YukonRoleProperty values(-50007,-500,'Coop ID - CADP Only','(none)','CADP format requires a coop id number.');



/* Reporting Analysis role */
insert into YukonRole values (-600,'Reporting','Analysis','Access to reports generation.');

/* Reporting Analysis role properties */
insert into YukonRoleProperty values(-60000,-600,'Header Label','Reporting','The header label for reporting.');
insert into YukonRoleProperty values(-60001,-600,'Download Reports Enable','true','Access to download the report files..');
insert into YukonRoleProperty values(-60002,-600,'Download Reports Default Filename','report.txt','A default filename for the downloaded report.');
insert into YukonRoleProperty values(-60003,-600,'Admin Reports Group','true','Access to administrative group reports.');
insert into YukonRoleProperty values(-60004,-600,'AMR Reports Group','true','Access to AMR group reports.');
insert into YukonRoleProperty values(-60005,-600,'Statistical Reports Group','true','Access to statistical group reports.');
insert into YukonRoleProperty values(-60006,-600,'Load Managment Reports Group','false','Acces to Load Management group reports.');
insert into YukonRoleProperty values(-60007,-600,'Cap Control Reports Group','false','Access to Cap Control group reports.');
insert into YukonRoleProperty values(-60008,-600,'Database Reports Group','true','Access to Database group reports.');
insert into YukonRoleProperty values(-60009,-600,'Stars Reports Group','true','Access to Stars group reports.');
insert into YukonRoleProperty values(-60010,-600,'Other Reports Group','true','Access to Other group reports.');

insert into YukonRoleProperty values(-60013,-600,'Admin Reports Group Label','Administor','Label (header) for administrative group reports.');
insert into YukonRoleProperty values(-60014,-600,'AMR Reports Group Label','Metering','Label (header) for AMR group reports.');
insert into YukonRoleProperty values(-60015,-600,'Statistical Reports Group Label','Statistical','Label (header) for statistical group reports.');
insert into YukonRoleProperty values(-60016,-600,'Load Managment Reports Group Label','Load Management','Label (header) for Load Management group reports.');
insert into YukonRoleProperty values(-60017,-600,'Cap Control Reports Group Label','Cap Control','Label (header) for Cap Control group reports.');
insert into YukonRoleProperty values(-60018,-600,'Database Reports Group Label','Database','Label (header) for Database group reports.');
insert into YukonRoleProperty values(-60019,-600,'Stars Reports Group Label','Stars','Label (header) for Stars group reports.');
insert into YukonRoleProperty values(-60020,-600,'Other Reports Group Label','Other','Label (header) for Other group reports.');



/* Trending Analysis role */
insert into YukonRole values (-601,'Trending','Analysis','Access to trending functionality.');
                     
/* Trending Analysis role properties */
insert into YukonRoleProperty values(-60100, -601, 'Trending Disclaimer',' ','The disclaimer that appears with trends.');
insert into yukonroleproperty values(-60101, -601, 'Scan Now Enabled', 'false', 'Controls access to retrieve meter data on demand.');
insert into yukonroleproperty values(-60102, -601, 'Scan Now Label', 'Get Data Now', 'The label for the scan data now option.');
insert into yukonroleproperty values(-60103, -601, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events.');
insert into yukonroleproperty values(-60104, -601, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily.');
insert into yukonroleproperty values(-60105, -601, 'Reset Peaks Enabled', 'false', 'Allow access to reset the peak time period.');
insert into yukonroleproperty values(-60106, -601, 'Header Label', 'Trending', 'The header label for trends.');
insert into yukonroleproperty values(-60107, -601, 'Header Secondary Label', 'Interval Data', 'A secondary header label for grouping trends.');
insert into yukonroleproperty values(-60108, -601, 'Trend Assignment', 'false', 'Allow assignment of trends to users.');
insert into yukonroleproperty values(-60109, -601, 'Trend Create', 'false', 'Allow creation of new trends.');
insert into yukonroleproperty values(-60110, -601, 'Trend Delete', 'false', 'Allow deletion of old trends.');
insert into yukonroleproperty values(-60111, -601, 'Trend Edit', 'false', 'Allow ditting of existing trends.');
insert into yukonroleproperty values(-60112, -601, 'Options Button Enabled', 'true', 'Display the Options link to additional trending configuration properties.');
insert into yukonroleproperty values(-60113, -601, 'Export/Print Button Enabled', 'true', 'Display the Export/Print options button (drop down menu).');
insert into yukonroleproperty values(-60114, -601, 'View Button Enabled', 'true', 'Display the View options button (drop down menu).');
insert into yukonroleproperty values(-60115, -601, 'Export/Print Button Label', 'Trend', 'The label for the trend print/export button (drop down menu).');
insert into yukonroleproperty values(-60116, -601, 'View Button Label', 'View', 'The label for the trend view options button (drop down menu).');
insert into yukonroleproperty values(-60117, -601, 'Trending Usage', 'false', 'Allow access to trending time of use.');
insert into yukonroleproperty values(-60118, -601, 'Default Start Date Offset', '0', 'Offset the start date by this number.');
insert into yukonroleproperty values(-60119, -601, 'Default Time Period', '(none)', 'Default the time period.');

drop table FDRTelegyrGroup;
update FDRInterfaceOption set optionlabel = 'Interval (sec)', optiontype = 'Text', optionvalues = '(none)' where interfaceid = 11 and ordering = 2;






/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/