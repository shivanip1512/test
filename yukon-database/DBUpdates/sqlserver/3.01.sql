/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create table LMGroupSA305 (
GroupID              numeric              not null,
RouteID              numeric              not null,
AddressUsage         varchar(8)           not null,
UtilityAddress       numeric              not null,
GroupAddress         numeric              not null,
DivisionAddress      numeric              not null,
SubstationAddress    numeric              not null,
IndividualAddress    varchar(16)          not null,
RateFamily           numeric              not null,
RateMember           numeric              not null,
RateHierarchy        numeric              not null,
LoadNumber           varchar(8)           not null
);
go
alter table LMGroupSA305
   add constraint PK_LMGROUPSA305 primary key  (GroupID);
go
alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID);
go
alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID);
go

create table LMGroupSA205105 (
GroupID              numeric              not null,
RouteID              numeric              not null,
OperationalAddress   numeric              not null,
LoadNumber           varchar(64)          not null
);
go
alter table LMGroupSA205105
   add constraint PK_LMGROUPSA205105 primary key  (GroupID);
go
alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID);
go
alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID);
go

create table LMGroupSASimple (
GroupID              numeric              not null,
RouteID              numeric              not null,
OperationalAddress   varchar(8)           not null,
NominalTimeout       numeric              not null,
MarkIndex            numeric              not null,
SpaceIndex           numeric              not null
);
go
alter table LMGroupSASimple
   add constraint PK_LMGROUPSASIMPLE primary key  (GroupID);
go
alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID);
go
alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID);
go

create table LMControlScenarioProgram (
ScenarioID           numeric              not null,
ProgramID            numeric              not null,
StartDelay           numeric              not null,
StopOffset           numeric              not null,
StartGear            numeric              not null
);
go
alter table LMControlScenarioProgram
   add constraint PK_LMCONTROLSCENARIOPROGRAM primary key  (ScenarioID, ProgramID);
go
alter table LMControlScenarioProgram
   add constraint FK_LMC_REF__LMP foreign key (ProgramID)
      references LMPROGRAM (DeviceID);
go
alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID);
go

create table LMProgramConstraints (
ConstraintID         numeric              not null,
ConstraintName       varchar(60)          not null,
AvailableSeasons     varchar(4)           not null,
AvailableWeekDays    varchar(8)           not null,
MaxHoursDaily        numeric              not null,
MaxHoursMonthly      numeric              not null,
MaxHoursSeasonal     numeric              not null,
MaxHoursAnnually     numeric              not null,
MinActivateTime      numeric              not null,
MinRestartTime       numeric              not null,
MaxDailyOps          numeric              not null,
MaxActivateTime      numeric              not null,
HolidayScheduleID    numeric              not null,
SeasonScheduleID     numeric              not null
);
go
insert into LMProgramConstraints values (0, 'Default Constraint', 'YYYY', 'YYYYYYYN', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

alter table LMProgramConstraints
   add constraint PK_PRGCONSTR primary key  (ConstraintID);
go


alter table LMProgram add ConstraintID numeric;
go
update LMProgram set ConstraintID = 0;
go
alter table LMProgram alter column ConstraintID numeric not null;
go

alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID);
go


create view LMProgram_View (DeviceID, ControlType, ConstraintID , ConstraintName , AvailableWeekDays , MaxHoursDaily , MaxHoursMonthly , MaxHoursSeasonal , MaxHoursAnnually , MinActivateTime , MinRestartTime , MaxDailyOps , MaxActivateTime , HolidayScheduleID , SeasonScheduleID ) as
select t.DEVICEID, t.CONTROLTYPE, u.ConstraintID, u.ConstraintName, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID;
go


insert into LMProgramConstraints 
select deviceid, 'Constraint: ' + LTRIM(STR(deviceid)), AvailableSeasons, AvailableWeekdays, MaxHoursDaily,
MaxHoursMonthly, MaxHoursSeasonal, MaxHoursAnnually, MinActivateTime, MinRestartTime,
0, 0, HolidayScheduleID, SeasonScheduleID from LMProgram;


update LMProgram set constraintid = deviceid;
go

/* @error ignore */
alter table LMProgram drop constraint FK_SesSch_LmPr;
go
/* @error ignore */
alter table LMProgram drop constraint FK_HlSc_LmPr;
go

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
go


/* @error ignore */
drop table portstatistics;
go

alter table LMProgramDirect add NotifyInterval numeric;
go
update LMProgramDirect set NotifyInterval = 0;
go
alter table LMProgramDirect alter column NotifyInterval numeric not null;
go
alter table LMProgramDirect add Heading varchar(40);
go
update LMProgramDirect set Heading = '(none)';
go
alter table LMProgramDirect alter column Heading varchar(40) not null;
go
alter table LMProgramDirect add MessageHeader varchar(160);
go
update LMProgramDirect set MessageHeader = '(none)';
go
alter table LMProgramDirect alter column MessageHeader varchar(160) not null;
go
alter table LMProgramDirect add MessageFooter varchar(160);
go
update LMProgramDirect set MessageFooter = '(none)';
go
alter table LMProgramDirect alter column MessageFooter varchar(160) not null;
go
alter table LMProgramDirect add CanceledMsg varchar(80);
go
update LMProgramDirect set CanceledMsg = '(none)';
go
alter table LMProgramDirect alter column CanceledMsg varchar(80) not null;
go
alter table LMProgramDirect add StoppedEarlyMsg varchar(80);
go
update LMProgramDirect set StoppedEarlyMsg = '(none)';
go
alter table LMProgramDirect alter column StoppedEarlyMsg varchar(80) not null;
go


create table DeviceRTC (
DeviceID             numeric              not null,
RTCAddress           numeric              not null,
Response             varchar(1)           not null,
LBTMode              numeric              not null,
DisableVerifies      varchar(1)           not null
);
go
alter table DeviceRTC
   add constraint PK_DEVICERTC primary key  (DeviceID);
go
alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID);
go

/* Visibility of serial number tree models in commander. */
insert into YukonRoleProperty values(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
insert into YukonRoleProperty values(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
insert into YukonRoleProperty values(-10303,-103,'DCU SA205 Serial','false','Show a DCU SA205 Serial Number SortBy display');
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


sp_rename 'DeviceDNP', 'DeviceAddress';


create table LMDirectNotifGrpList (
ProgramID            numeric              not null,
NotificationGrpID    numeric              not null
);
go
alter table LMDirectNotifGrpList
   add constraint PK_LMDIRECTNOTIFGRPLIST primary key  (ProgramID, NotificationGrpID);
go
alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID);
go
alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID);
go


create table DeviceSeries5RTU (
DeviceID             numeric              not null,
TickTime             numeric              not null,
TransmitOffset       numeric              not null,
SaveHistory          CHAR(1)              not null,
PowerValueHighLimit  numeric              not null,
PowerValueLowLimit   numeric              not null,
PowerValueMultiplier float                not null,
PowerValueOffset     float                not null,
StartCode            numeric              not null,
StopCode             numeric              not null
);
go
alter table DeviceSeries5RTU
   add constraint PK_DEVICESERIES5RTU primary key  (DeviceID);
go
alter table DeviceSeries5RTU
   add constraint FK_DvS5r_Dv2w foreign key (DeviceID)
      references DEVICE2WAYFLAGS (DEVICEID);
go


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


/* New default group for STARS operators and customers */
insert into yukongroup values (-303,'STARS Operators Grp','The default group of STARS operators');
insert into yukongroup values (-304,'STARS Customers Grp','The default group of STARS customers');

insert into yukongrouprole values (-2000,-303,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-2002,-303,-108,-10802,'(none)');
insert into yukongrouprole values (-2003,-303,-108,-10803,'(none)');
insert into yukongrouprole values (-2004,-303,-108,-10804,'(none)');
insert into yukongrouprole values (-2005,-303,-108,-10805,'(none)');
insert into yukongrouprole values (-2006,-303,-108,-10806,'(none)');
insert into yukongrouprole values (-2007,-303,-108,-10807,'(none)');
insert into yukongrouprole values (-2008,-303,-108,-10808,'(none)');

insert into yukongrouprole values (-2020,-303,-201,-20100,'(none)');
insert into yukongrouprole values (-2021,-303,-201,-20101,'(none)');
insert into yukongrouprole values (-2022,-303,-201,-20102,'true');
insert into yukongrouprole values (-2023,-303,-201,-20103,'(none)');
insert into yukongrouprole values (-2024,-303,-201,-20104,'(none)');
insert into yukongrouprole values (-2025,-303,-201,-20105,'(none)');
insert into yukongrouprole values (-2026,-303,-201,-20106,'(none)');
insert into yukongrouprole values (-2027,-303,-201,-20107,'(none)');
insert into yukongrouprole values (-2028,-303,-201,-20108,'(none)');
insert into yukongrouprole values (-2029,-303,-201,-20109,'(none)');
insert into yukongrouprole values (-2030,-303,-201,-20110,'(none)');
insert into yukongrouprole values (-2031,-303,-201,-20111,'(none)');
insert into yukongrouprole values (-2032,-303,-201,-20112,'(none)');
insert into yukongrouprole values (-2033,-303,-201,-20113,'(none)');
insert into yukongrouprole values (-2034,-303,-201,-20114,'true');
insert into yukongrouprole values (-2035,-303,-201,-20115,'false');
insert into yukongrouprole values (-2036,-303,-201,-20116,'(none)');
insert into yukongrouprole values (-2037,-303,-201,-20117,'(none)');
insert into yukongrouprole values (-2038,-303,-201,-20118,'(none)');

insert into yukongrouprole values (-2050,-303,-201,-20150,'(none)');
insert into yukongrouprole values (-2051,-303,-201,-20151,'(none)');
insert into yukongrouprole values (-2052,-303,-201,-20152,'(none)');
insert into yukongrouprole values (-2053,-303,-201,-20153,'(none)');
insert into yukongrouprole values (-2054,-303,-201,-20154,'(none)');
insert into yukongrouprole values (-2055,-303,-201,-20155,'true');
insert into yukongrouprole values (-2056,-303,-201,-20156,'(none)');
insert into yukongrouprole values (-2057,-303,-201,-20157,'(none)');

insert into yukongrouprole values (-2070,-303,-210,-21000,'(none)');
insert into yukongrouprole values (-2071,-303,-210,-21001,'(none)');

insert into yukongrouprole values (-2080,-303,-209,-20900,'(none)');
insert into yukongrouprole values (-2081,-303,-209,-20901,'(none)');
insert into yukongrouprole values (-2082,-303,-209,-20902,'(none)');
insert into yukongrouprole values (-2083,-303,-209,-20903,'(none)');
insert into yukongrouprole values (-2084,-303,-209,-20904,'(none)');

insert into yukongrouprole values (-2100,-303,-201,-20800,'(none)');
insert into yukongrouprole values (-2101,-303,-201,-20801,'(none)');
insert into yukongrouprole values (-2110,-303,-201,-20810,'(none)');
insert into yukongrouprole values (-2113,-303,-201,-20813,'(none)');
insert into yukongrouprole values (-2114,-303,-201,-20814,'(none)');
insert into yukongrouprole values (-2115,-303,-201,-20815,'(none)');
insert into yukongrouprole values (-2116,-303,-201,-20816,'(none)');
insert into yukongrouprole values (-2119,-303,-201,-20819,'(none)');
insert into yukongrouprole values (-2120,-303,-201,-20820,'(none)');
insert into yukongrouprole values (-2130,-303,-201,-20830,'(none)');
insert into yukongrouprole values (-2131,-303,-201,-20831,'(none)');
insert into yukongrouprole values (-2132,-303,-201,-20832,'(none)');
insert into yukongrouprole values (-2133,-303,-201,-20833,'(none)');
insert into yukongrouprole values (-2134,-303,-201,-20834,'(none)');
insert into yukongrouprole values (-2135,-303,-201,-20835,'(none)');
insert into yukongrouprole values (-2136,-303,-201,-20836,'(none)');
insert into yukongrouprole values (-2137,-303,-201,-20837,'(none)');
insert into yukongrouprole values (-2138,-303,-201,-20838,'(none)');
insert into yukongrouprole values (-2139,-303,-201,-20839,'(none)');
insert into yukongrouprole values (-2140,-303,-201,-20840,'(none)');
insert into yukongrouprole values (-2141,-303,-201,-20841,'(none)');
insert into yukongrouprole values (-2142,-303,-201,-20842,'(none)');
insert into yukongrouprole values (-2143,-303,-201,-20843,'(none)');
insert into yukongrouprole values (-2144,-303,-201,-20844,'(none)');
insert into yukongrouprole values (-2150,-303,-201,-20850,'(none)');
insert into yukongrouprole values (-2151,-303,-201,-20851,'(none)');
insert into yukongrouprole values (-2152,-303,-201,-20852,'(none)');
insert into yukongrouprole values (-2153,-303,-201,-20853,'(none)');
insert into yukongrouprole values (-2154,-303,-201,-20854,'(none)');
insert into yukongrouprole values (-2155,-303,-201,-20855,'(none)');
insert into yukongrouprole values (-2156,-303,-201,-20856,'(none)');
insert into yukongrouprole values (-2157,-303,-201,-20857,'(none)');
insert into yukongrouprole values (-2158,-303,-201,-20858,'(none)');
insert into yukongrouprole values (-2159,-303,-201,-20859,'(none)');
insert into yukongrouprole values (-2160,-303,-201,-20860,'(none)');
insert into yukongrouprole values (-2161,-303,-201,-20861,'(none)');
insert into yukongrouprole values (-2162,-303,-201,-20862,'(none)');
insert into yukongrouprole values (-2170,-303,-201,-20870,'(none)');
insert into yukongrouprole values (-2180,-303,-201,-20880,'(none)');
insert into yukongrouprole values (-2181,-303,-201,-20881,'(none)');
insert into yukongrouprole values (-2182,-303,-201,-20882,'(none)');
insert into yukongrouprole values (-2183,-303,-201,-20883,'(none)');
insert into yukongrouprole values (-2184,-303,-201,-20884,'(none)');
insert into yukongrouprole values (-2185,-303,-201,-20885,'(none)');
insert into yukongrouprole values (-2186,-303,-201,-20886,'(none)');
insert into yukongrouprole values (-2187,-303,-201,-20887,'(none)');
insert into yukongrouprole values (-2188,-303,-201,-20888,'(none)');
insert into yukongrouprole values (-2189,-303,-201,-20889,'(none)');

insert into yukongrouprole values (-2200,-304,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (-2202,-304,-108,-10802,'(none)');
insert into yukongrouprole values (-2203,-304,-108,-10803,'(none)');
insert into yukongrouprole values (-2204,-304,-108,-10804,'(none)');
insert into yukongrouprole values (-2205,-304,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-2206,-304,-108,-10806,'(none)');
insert into yukongrouprole values (-2207,-304,-108,-10807,'(none)');
insert into yukongrouprole values (-2208,-304,-108,-10808,'(none)');

insert into yukongrouprole values (-2220,-304,-400,-40000,'(none)');
insert into yukongrouprole values (-2221,-304,-400,-40001,'(none)');
insert into yukongrouprole values (-2222,-304,-400,-40002,'(none)');
insert into yukongrouprole values (-2223,-304,-400,-40003,'(none)');
insert into yukongrouprole values (-2224,-304,-400,-40004,'(none)');
insert into yukongrouprole values (-2225,-304,-400,-40005,'(none)');
insert into yukongrouprole values (-2226,-304,-400,-40006,'(none)');
insert into yukongrouprole values (-2227,-304,-400,-40007,'(none)');
insert into yukongrouprole values (-2228,-304,-400,-40008,'(none)');
insert into yukongrouprole values (-2229,-304,-400,-40009,'(none)');
insert into yukongrouprole values (-2230,-304,-400,-40010,'(none)');

insert into yukongrouprole values (-2250,-304,-400,-40050,'(none)');
insert into yukongrouprole values (-2251,-304,-400,-40051,'(none)');
insert into yukongrouprole values (-2252,-304,-400,-40052,'(none)');
insert into yukongrouprole values (-2254,-304,-400,-40054,'(none)');
insert into yukongrouprole values (-2255,-304,-400,-40055,'(none)');

insert into yukongrouprole values (-2300,-304,-400,-40100,'(none)');
insert into yukongrouprole values (-2301,-304,-400,-40101,'(none)');
insert into yukongrouprole values (-2302,-304,-400,-40102,'(none)');
insert into yukongrouprole values (-2310,-304,-400,-40110,'(none)');
insert into yukongrouprole values (-2311,-304,-400,-40111,'(none)');
insert into yukongrouprole values (-2312,-304,-400,-40112,'(none)');
insert into yukongrouprole values (-2313,-304,-400,-40113,'(none)');
insert into yukongrouprole values (-2314,-304,-400,-40114,'(none)');
insert into yukongrouprole values (-2315,-304,-400,-40115,'(none)');
insert into yukongrouprole values (-2316,-304,-400,-40116,'(none)');
insert into yukongrouprole values (-2317,-304,-400,-40117,'(none)');
insert into yukongrouprole values (-2330,-304,-400,-40130,'(none)');
insert into yukongrouprole values (-2331,-304,-400,-40131,'(none)');
insert into yukongrouprole values (-2332,-304,-400,-40132,'(none)');
insert into yukongrouprole values (-2333,-304,-400,-40133,'(none)');
insert into yukongrouprole values (-2334,-304,-400,-40134,'(none)');
insert into yukongrouprole values (-2335,-304,-400,-40135,'(none)');
insert into yukongrouprole values (-2336,-304,-400,-40136,'(none)');
insert into yukongrouprole values (-2337,-304,-400,-40137,'(none)');
insert into yukongrouprole values (-2338,-304,-400,-40138,'(none)');
insert into yukongrouprole values (-2350,-304,-400,-40150,'(none)');
insert into yukongrouprole values (-2351,-304,-400,-40151,'(none)');
insert into yukongrouprole values (-2352,-304,-400,-40152,'(none)');
insert into yukongrouprole values (-2353,-304,-400,-40153,'(none)');
insert into yukongrouprole values (-2354,-304,-400,-40154,'(none)');
insert into yukongrouprole values (-2355,-304,-400,-40155,'(none)');
insert into yukongrouprole values (-2356,-304,-400,-40156,'(none)');
insert into yukongrouprole values (-2357,-304,-400,-40157,'(none)');
insert into yukongrouprole values (-2358,-304,-400,-40158,'(none)');
insert into yukongrouprole values (-2370,-304,-400,-40170,'(none)');
insert into yukongrouprole values (-2371,-304,-400,-40171,'(none)');
insert into yukongrouprole values (-2372,-304,-400,-40172,'(none)');
insert into yukongrouprole values (-2373,-304,-400,-40173,'(none)');
insert into yukongrouprole values (-2380,-304,-400,-40180,'(none)');
insert into yukongrouprole values (-2381,-304,-400,-40181,'(none)');
insert into yukongrouprole values (-2390,-304,-400,-40190,'(none)');
insert into yukongrouprole values (-2391,-304,-400,-40191,'(none)');
insert into yukongrouprole values (-2392,-304,-400,-40192,'(none)');
insert into yukongrouprole values (-2393,-304,-400,-40193,'(none)');
insert into yukongrouprole values (-2394,-304,-400,-40194,'(none)');
insert into yukongrouprole values (-2395,-304,-400,-40195,'(none)');
insert into yukongrouprole values (-2396,-304,-400,-40196,'(none)');


alter table DynamicLMProgramDirect add DailyOps numeric;
go
update DynamicLMProgramDirect set DailyOps = 0;
go
alter table DynamicLMProgramDirect alter column DailyOps numeric not null;
go

alter table LMProgramDirectGear add RampInInterval numeric;
go
update LMProgramDirectGear set RampInInterval = 0;
go
alter table LMProgramDirectGear alter column RampInInterval numeric not null;
go

alter table LMProgramDirectGear add RampInPercent numeric;
go
update LMProgramDirectGear set RampInPercent = 0;
go
alter table LMProgramDirectGear alter column RampInPercent numeric not null;
go

alter table LMProgramDirectGear add RampOutInterval numeric;
go
update LMProgramDirectGear set RampOutInterval = 0;
go
alter table LMProgramDirectGear alter column RampOutInterval numeric not null;
go

alter table LMProgramDirectGear add RampOutPercent numeric;
go
update LMProgramDirectGear set RampOutPercent = 0;
go
alter table LMProgramDirectGear alter column RampOutPercent numeric not null;
go


sp_rename 'LMProgramDirect.NotifyInterval', 'NotifyOffset', 'COLUMN';
go
alter table LMProgramDirect drop column CanceledMsg;
go
alter table LMProgramDirect drop column StoppedEarlyMsg;
go


alter table DynamicLMProgramDirect add NotifyTime datetime;
go
update DynamicLMProgramDirect set NotifyTime = '01-JAN-1990';
go
alter table DynamicLMProgramDirect alter column NotifyTime datetime not null;
go

alter table DeviceSeries5RTU add Retries numeric;
go
update DeviceSeries5RTU set Retries = 0;
go
alter table DeviceSeries5RTU alter column Retries numeric not null;
go

sp_rename 'LMControlScenarioProgram.StartDelay', 'StartOffset', 'COLUMN';


update YukonListEntry set EntryText='LCR-5000(Xcom)' where EntryID=1051;
update YukonListEntry set YukonDefinitionID=1305 where EntryID=1052;
update YukonListEntry set YukonDefinitionID=1306 where EntryID=1053;
update YukonListEntry set YukonDefinitionID=1307 where EntryID=1054;
update YukonListEntry set YukonDefinitionID=1308 where EntryID=1055;
insert into YukonListEntry values (1060,1005,0,'SA-205',1309);
insert into YukonListEntry values (1061,1005,0,'SA-305',1310);
insert into YukonListEntry values (1062,1005,0,'LCR-5000(Vcom)',1311);

update yukonuserrole set value = 'true' where userroleid = -107;


alter table DynamicLMProgramDirect add StartedRampingOut datetime;
go
update DynamicLMProgramDirect set StartedRampingOut = '01-JAN-1990';
go
alter table DynamicLMProgramDirect alter column StartedRampingOut datetime not null;
go

alter table DynamicLMGroup add NextControlTime datetime;
go
update DynamicLMGroup set NextControlTime = '01-JAN-1990';
go
alter table DynamicLMGroup alter column NextControlTime datetime not null;
go

alter table DynamicLMGroup add InternalState numeric;
go
update DynamicLMGroup set InternalState = 0;
go
alter table DynamicLMGroup alter column InternalState numeric not null;
go

update displaycolumns set typenum = 7 where title = 'Additional Info' and displaynum < 99;


/* @error ignore */
alter table dynamiclmgroup drop constraint FK_DyLmGr_LmPrDGr;
go
/* @error ignore */
alter table DynamicLMGroup drop constraint PK_DYNAMICLMGROUP;
go
delete from DynamicLMGroup;
go
alter table DynamicLMGroup add constraint PK_DYNAMICLMGROUP primary key (DeviceID);

alter table dynamiclmgroup DROP COLUMN lmprogramid;

alter table LMProgramConstraints drop column AvailableSeasons;

/*==============================================================*/
/* Table : DateOfSeason                                         */
/*==============================================================*/
create table DateOfSeason (
SeasonScheduleID     numeric              not null,
SeasonName           varchar(20)          not null,
SeasonStartMonth     numeric              not null,
SeasonStartDay       numeric              not null,
SeasonEndMonth       numeric              not null,
SeasonEndDay         numeric              not null
);
go


alter table DateOfSeason
   add constraint PK_DATEOFSEASON primary key  (SeasonScheduleID, SeasonName);
go

alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);
go

alter table SeasonSchedule drop column WinterDay;
go
alter table SeasonSchedule drop column WinterMonth;
go
alter table SeasonSchedule drop column FallDay;
go
alter table SeasonSchedule drop column FallMonth;
go
alter table SeasonSchedule drop column SummerDay;
go
alter table SeasonSchedule drop column SummerMonth;
go
alter table SeasonSchedule drop column SpringDay;
go
alter table SeasonSchedule drop column SpringMonth;
go

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
go
update FDRInterfaceOption set optionlabel = 'Interval (sec)', optiontype = 'Text', optionvalues = '(none)' where interfaceid = 11 and ordering = 2;

create table UserPAOowner (
UserID               numeric              not null,
PaoID                numeric              not null
);
go
alter table UserPAOowner
   add constraint PK_USERPAOOWNER primary key  (UserID, PaoID);
go
alter table UserPAOowner
   add constraint FK_UsPow_YkUsr foreign key (UserID)
      references YukonUser (UserID);
go
alter table UserPAOowner
   add constraint FK_UsPow_YkP foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go

update lmcontrolareaprogram set userorder=defaultpriority;
go
sp_rename 'LMControlAreaProgram.UserOrder', 'StartPriority', 'COLUMN';
go
sp_rename 'LMControlAreaProgram.StopOrder', 'StopPriority', 'COLUMN';
go

alter table LMControlAreaProgram drop column DefaultPriority;
go

delete from YukonGroupRole where roleid = -3;
delete from YukonUserRole where roleid = -3;
delete from YukonRoleProperty where roleid = -3;
delete from YukonRole where roleid = -3;

create table DeviceVerification (
ReceiverID           numeric              not null,
TransmitterID        numeric              not null,
ResendOnFail         char(1)              not null,
Disable              char(1)              not null,
constraint PK_DEVICEVERIFICATION primary key  (ReceiverID, TransmitterID)
);
go
alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);
go
alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);
go

create table DynamicVerification (
LogID                numeric              not null,
TimeArrival          datetime             not null,
ReceiverID           numeric              not null,
TransmitterID        numeric              not null,
Command              varchar(256)         not null,
Code                 varchar(128)         not null,
CodeSequence         numeric              not null,
Received             char(1)              not null,
CodeStatus           varchar(32)          not null
);
go
alter table DynamicVerification
   add constraint PK_DYNAMICVERIFICATION primary key  (LogID);
go
alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);
go
alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);
go

update YukonRoleProperty set description = '(No settings yet)' where rolepropertyid = -10100;
update YukonRoleProperty set description = 'The following settings are valid: CREATE_SCHEDULE(0x0000000C), ENABLE_SCHEDULE(0x000000C0), ABLE_TO_START_SCHEDULE(0x00000C00)' where rolepropertyid = -10101;

update YukonRoleProperty set description = 'The number of rows shown before creating a new page of data' where rolepropertyid = -10103;
update YukonRoleProperty set description = 'The following settings are valid: HIDE_MACS(0x00001000), HIDE_CAPCONTROL(0x00002000), HIDE_LOADCONTROL(0x00004000), HIDE_ALL_DISPLAYS(0x0000F000), CONTROL_YUKON_SERVICES(0x00010000), HIDE_ALARM_COLORS(0x80000000)' where rolepropertyid = -10104;

update YukonRoleProperty set description = 'What text will be added onto CBC names when they are created' where rolepropertyid = -10106;
update YukonRoleProperty set description = 'Total number alarms that are displayed in the quick access list' where rolepropertyid = -10107;
update YukonRoleProperty set description = 'How many decimal places to show for real values' where rolepropertyid = -10108;
update YukonRoleProperty set description = 'How many decimal places to show for real values for PowerFactor' where rolepropertyid = -10109;

update dateofseason set seasonstartmonth = seasonstartmonth + 1;
update dateofseason set seasonendmonth = seasonendmonth + 1;
update dateofholiday set holidaymonth = holidaymonth + 1;
update dateofholiday set holidayyear = 0 where holidayyear = -1;

/* Energy company role properties */
insert into YukonRoleProperty values(-1107,-2,'track_hardware_addressing','false','Controls whether to track the hardware addressing information.');
insert into YukonRoleProperty values(-1108,-2,'single_energy_company','true','Indicates whether this is a single energy company system.');

/* Description on the opt out page */
update YukonRoleProperty set DefaultValue='If you would like to temporarily opt out of all programs, select the time frame below, then click Submit.' where RolePropertyId=-20870;
update YukonRoleProperty set DefaultValue='If you would like to temporarily opt out of all programs, select the time frame below, then click Submit.' where RolePropertyId=-40171;

/* Inventory role properties */
insert into YukonRoleProperty values(-20905,-209,'Create Hardware','true','Controls whether to allow creating new hardware');
insert into YukonRoleProperty values(-20906,-209,'Create MCT','true','Controls whether to allow creating MCT devices');

insert into yukongrouprole values (-796,-301,-209,-20905,'(none)');
insert into yukongrouprole values (-797,-301,-209,-20906,'(none)');
insert into yukongrouprole values (-2085,-303,-209,-20905,'(none)');
insert into yukongrouprole values (-2086,-303,-209,-20906,'(none)');

/* Override individual hardware */
insert into YukonRoleProperty values(-20158,-201,'Override Hardware','false','Controls whether to allow overriding individual hardware');
insert into YukonRoleProperty values(-20817,-201,'Text Override','override','Term for override');

insert into yukongrouprole values (-758,-301,-201,-20158,'(none)');
insert into yukongrouprole values (-817,-301,-201,-20817,'(none)');
insert into yukongrouprole values (-2058,-303,-201,-20158,'true');
insert into yukongrouprole values (-2117,-303,-201,-20817,'(none)');

/* More labels and titles */
insert into YukonRoleProperty values(-20845,-201,'Label Thermostat Saved Schedules','Saved Schedules','Text of the thermostat saved schedules link');
insert into YukonRoleProperty values(-20863,-201,'Title Thermostat Saved Schedules','THERMOSTAT - SAVED SCHEDULES','Title of the thermostat saved schedules page');
insert into YukonRoleProperty values(-20864,-201,'Title Hardware Overriding','HARDWARE - OVERRIDING','Title of the hardware overriding page');
insert into YukonRoleProperty values(-40139,-400,'Label Thermostat Saved Schedules','Saved Schedules','Text of the thermostat saved schedules link');
insert into YukonRoleProperty values(-40159,-400,'Title Thermostat Saved Schedules','THERMOSTAT - SAVED SCHEDULES','Title of the thermostat saved schedules page');

insert into yukongrouprole values (-639,-300,-400,-40139,'(none)');
insert into yukongrouprole values (-659,-300,-400,-40159,'(none)');
insert into yukongrouprole values (-845,-301,-201,-20845,'(none)');
insert into yukongrouprole values (-863,-301,-201,-20863,'(none)');
insert into yukongrouprole values (-864,-301,-201,-20864,'(none)');
insert into yukongrouprole values (-2145,-303,-201,-20845,'(none)');
insert into yukongrouprole values (-2163,-303,-201,-20863,'(none)');
insert into yukongrouprole values (-2164,-303,-201,-20864,'(none)');
insert into yukongrouprole values (-2339,-304,-400,-40139,'(none)');
insert into yukongrouprole values (-2359,-304,-400,-40159,'(none)');










/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
