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


create or replace view LMProgram_View (DeviceID, ControlType, ConstraintID , ConstraintName , AvailableWeekDays , MaxHoursDaily , MaxHoursMonthly , MaxHoursSeasonal , MaxHoursAnnually , MinActivateTime , MinRestartTime , MaxDailyOps , MaxActivateTime , HolidayScheduleID , SeasonScheduleID ) as
select t.DEVICEID, t.CONTROLTYPE, u.ConstraintID, u.ConstraintName, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID;


insert into LMProgramConstraints 
select deviceid, CONCAT('Constraint: ', LTRIM(deviceid) ), AvailableSeasons, AvailableWeekdays, MaxHoursDaily,
MaxHoursMonthly, MaxHoursSeasonal, MaxHoursAnnually, MinActivateTime, MinRestartTime,
0, 0, HolidayScheduleID, SeasonScheduleID from LMProgram where deviceid > 0;


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

alter table LMProgramDirect add NotifyOffset number;
update LMProgramDirect set NotifyOffset = 0;
alter table LMProgramDirect modify NotifyOffset number not null;

alter table LMProgramDirect add Heading varchar2(40);
update LMProgramDirect set Heading = '(none)';
alter table LMProgramDirect modify Heading varchar2(40) not null;

alter table LMProgramDirect add MessageHeader varchar2(160);
update LMProgramDirect set MessageHeader = '(none)';
alter table LMProgramDirect modify MessageHeader varchar2(160) not null;

alter table LMProgramDirect add MessageFooter varchar2(160);
update LMProgramDirect set MessageFooter = '(none)';
alter table LMProgramDirect modify MessageFooter varchar2(160) not null;

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
   StopCode             NUMBER                           not null,
   Retries              NUMBER                           not null
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

delete from YukonGroupRole where roleid = -3;
delete from YukonUserRole where roleid = -3;
delete from YukonRoleProperty where roleid = -3;
delete from YukonRole where roleid = -3;

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


/* New default groups for STARS operators and customers */
insert into yukongroup values (-303,'STARS Operators Grp','The default group for STARS operators');
insert into yukongroup values (-304,'STARS Residential Customers Grp','The default group for STARS residential customers');

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


alter table DynamicLMProgramDirect ADD NotifyTime DATE;
UPDATE DynamicLMProgramDirect SET NotifyTime ='01-JAN-1990';
alter TABLE DynamicLMProgramDirect MODIFY NotifyTime NOT NULL;


alter table DeviceSeries5RTU add Retries number;
update DeviceSeries5RTU set Retries = 0;
alter table DeviceSeries5RTU modify Retries number not null;

alter table LMControlScenarioProgram rename column StartDelay to StartOffset;


update YukonListEntry set EntryText='LCR-5000(EXPRESSCOM)' where EntryID=1051;
update YukonListEntry set YukonDefinitionID=1305, EntryOrder=-1 where EntryID=1052;
update YukonListEntry set YukonDefinitionID=1306 where EntryID=1053;
update YukonListEntry set YukonDefinitionID=1307, EntryOrder=-1 where EntryID=1054;
update YukonListEntry set YukonDefinitionID=1308, EntryOrder=-1 where EntryID=1055;

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

create table UserPAOowner  (
   UserID               NUMBER                           not null,
   PaoID                NUMBER                           not null
);
alter table UserPAOowner
   add constraint PK_USERPAOOWNER primary key (UserID, PaoID);
alter table UserPAOowner
   add constraint FK_UsPow_YkUsr foreign key (UserID)
      references YukonUser (UserID);
alter table UserPAOowner
   add constraint FK_UsPow_YkP foreign key (PaoID)
      references YukonPAObject (PAObjectID);

update lmcontrolareaprogram set userorder=defaultpriority;

alter table lmcontrolareaprogram rename column UserOrder to StartPriority;
alter table lmcontrolareaprogram rename column StopOrder to StopPriority;
alter table lmcontrolareaprogram drop column defaultpriority;

create table DeviceVerification  (
   ReceiverID           NUMBER                           not null,
   TransmitterID        NUMBER                           not null,
   ResendOnFail         CHAR(1)                          not null,
   Disable              CHAR(1)                          not null
);
alter table DeviceVerification
   add constraint PK_DEVICEVERIFICATION primary key (ReceiverID, TransmitterID);
alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);
alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);

create table DynamicVerification  (
   LogID                NUMBER                           not null,
   TimeArrival          DATE                             not null,
   ReceiverID           NUMBER                           not null,
   TransmitterID        NUMBER                           not null,
   Command              VARCHAR2(256)                    not null,
   Code                 VARCHAR2(128)                    not null,
   CodeSequence         NUMBER                           not null,
   Received             CHAR(1)                          not null,
   CodeStatus           VARCHAR2(32)                     not null
);
alter table DynamicVerification
   add constraint PK_DYNAMICVERIFICATION primary key (LogID);
alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);
alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);

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

insert into yukongrouprole values (-796,-301,-209,-20905,'(none)');
insert into yukongrouprole values (-2085,-303,-209,-20905,'(none)');

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

/* Remove unused role properties */
delete from yukongrouprole where rolepropertyid = -1103;
delete from yukonuserrole where rolepropertyid = -1103;
delete from yukonroleproperty where rolepropertyid = -1103;

delete from yukongrouprole where rolepropertyid = -1104;
delete from yukonuserrole where rolepropertyid = -1104;
delete from yukonroleproperty where rolepropertyid = -1104;

delete from yukongrouprole where rolepropertyid = -20100;
delete from yukonuserrole where rolepropertyid = -20100;
delete from yukonroleproperty where rolepropertyid = -20100;

delete from yukongrouprole where rolepropertyid = -20150;
delete from yukonuserrole where rolepropertyid = -20150;
delete from yukonroleproperty where rolepropertyid = -20150;

delete from yukongrouprole where rolepropertyid = -40000;
delete from yukonuserrole where rolepropertyid = -40000;
delete from yukonroleproperty where rolepropertyid = -40000;

delete from yukongrouprole where rolepropertyid = -40050;
delete from yukonuserrole where rolepropertyid = -40050;
delete from yukonroleproperty where rolepropertyid = -40050;

delete from yukongrouprole where rolepropertyid = -40101;
delete from yukonuserrole where rolepropertyid = -40101;
delete from yukonroleproperty where rolepropertyid = -40101;


/* @error ignore */
insert into YukonListEntry values (1136,1011,0,'Address',1606);
/* @error ignore */
insert into YukonListEntry values (1306,1051,0,'Address',2706);
/* @error ignore */
insert into YukonListEntry values (1336,1054,0,'Address',3306);

update lmprogramdirectgear set methodstoptype='RampOutRandom' where methodstoptype='RampOut';
update lmprogramdirectgear set methodstoptype='RampOutRandomRest' where methodstoptype='RampOutRestore';

insert into YukonRoleProperty values(-1015,-1,'voice_host','127.0.0.1','Name or IP address of the voice server');
insert into YukonRoleProperty values(-1016,-1,'notification_host','127.0.0.1','Name or IP address of the Yukon Notification service');
insert into YukonRoleProperty values(-1017,-1,'notification_port','1515','TCP/IP port of the Yukon Notification service');

/* Fix added after build */
delete from YukonGroupRole where grouproleid = -17 or grouproleid = -18;

insert into YukonGroupRole values(-16,-1,-1,-1015,'(none)');
insert into YukonGroupRole values(-17,-1,-1,-1016,'(none)');
insert into YukonGroupRole values(-18,-1,-1,-1017,'(none)');

insert into YukonRole values(-5,'Voice Server','Yukon','Inbound and outbound voice interface.');
insert into YukonRoleProperty values(-1400,-5,'voice_app','login','The voice server application that Yukon should use');
insert into YukonRoleProperty values(-1401,-5,'call_timeout','30','The time-out in seconds given to each outbound call');
insert into YukonRoleProperty values(-1402,-5,'call_response_timeout','240','The time-out in seconds given to each outbound call response');

insert into YukonGroupRole values(-70,-1,-5,-1400,'(none)');
insert into YukonGroupRole values(-71,-1,-5,-1401,'(none)');
insert into YukonGroupRole values(-72,-1,-5,-1402,'(none)');

insert into YukonRoleProperty values(-20004,-200,'View Batch Commands','false','Controls whether to allow monitoring all the batched switch commands');
insert into YukonRoleProperty values(-20005,-200,'View Opt Out Events','false','Controls whether to allow monitoring all the scheduled opt out events');


/* @error ignore */
create table TOUSchedule  (
   TOUScheduleID        NUMBER                           not null,
   TOUScheduleName      VARCHAR2(32)                     not null
);
/* @error ignore */
alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key (TOUScheduleID);

/* @error ignore */
create table TOUDeviceMapping  (
   TOUScheduleID        NUMBER                           not null,
   DeviceID             NUMBER                           not null
);
/* @error ignore */
alter table TOUDeviceMapping
   add constraint PK_TOUDEVICEMAPPING primary key (TOUScheduleID, DeviceID);
/* @error ignore */
alter table TOUDeviceMapping
   add constraint FK_TOU_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
/* @error ignore */
alter table TOUDeviceMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);

/* @error ignore */
create table TOURateOffset  (
   TOUScheduleID        NUMBER                           not null,
   SwitchRate           VARCHAR2(4)                      not null,
   SwitchOffset         NUMBER                           not null
);
/* @error ignore */
alter table TOURateOffset
   add constraint PK_TOURATEOFFSET primary key (TOUScheduleID, SwitchOffset);
/* @error ignore */
alter table TOURateOffset
   add constraint FK_TOUr_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);


create table Command  (
   CommandID            NUMBER                           not null,
   Command              VARCHAR2(256)                    not null,
   Label                VARCHAR2(256)                    not null,
   Category             VARCHAR2(32)                     not null
);
alter table Command
   add constraint PK_COMMAND primary key (CommandID);

create table DeviceTypeCommand  (
   DeviceCommandID      NUMBER                           not null,
   CommandID            NUMBER                           not null,
   DeviceType           VARCHAR2(32)                     not null,
   DisplayOrder         NUMBER                           not null,
   VisibleFlag          CHAR(1)                          not null
);
alter table DeviceTypeCommand
   add constraint PK_DEVICETYPECOMMAND primary key (DeviceCommandID);
alter table DeviceTypeCommand
   add constraint FK_DevCmd_Cmd foreign key (CommandID)
      references Command (CommandID);

update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-1101;

insert into lmgroup values( 0, 0 );

insert into UserPaoOwner select operatorloginid, scheduleid from LMMacsscheduleoperatorlist;
drop table LMMacsscheduleoperatorlist;

/* @error ignore */
alter table operatorlogin drop constraint FK_OpSGrp_OpLg;
insert into UserPaoOwner select loginid, lmgroupid from OperatorSerialGroup;
drop table OperatorSerialGroup;

insert into UserPaoOwner select OperatorLoginId, programid from LMDirectOperatorList;
drop table LMDirectOperatorList;

create index Indx_ContLstName on Contact (
   ContLastName ASC
);

create index Index_DYNVER_CS on DynamicVerification (
   CodeSequence ASC
);

insert into YukonRoleProperty values(-10010, -100,'z_optional_product_dev','00000000','This feature is for development purposes only');
insert into YukonGroupRole values(-1010,-2,-100,-10010,'00000000');

create index Indx_DYNV_TIME on DynamicVerification (
   TimeArrival ASC
);

insert into LMGroupExpressComAddress select distinct SplinterAddress + 1000, 'SPLINTER', SplinterAddress,'(none)' from LMGroupExpressCom;
update LMGroupExpressCom set SplinterAddress = SplinterAddress + 1000;
insert into LMGroupExpressComAddress select distinct UdAddress + 2000, 'USER', UdAddress,'(none)' from LMGroupExpressCom;
update LMGroupExpressCom set UdAddress = UdAddress + 2000;
insert into LMGroupExpressComAddress select distinct ZipCodeAddress + 3000, 'ZIP', ZipCodeAddress,'(none)' from LMGroupExpressCom;
update LMGroupExpressCom set ZipCodeAddress = ZipCodeAddress + 3000;


alter table LMGroupExpressCom rename column SplinterAddress to SplinterID;
alter table LMGroupExpressCom rename column UdAddress to UserID;
alter table LMGroupExpressCom rename column ZipCodeAddress to ZipID;

alter table LMGroupExpressCom
   add constraint FK_ExCS_LMExCm foreign key (SplinterID)
      references LMGroupExpressComAddress (AddressID);
alter table LMGroupExpressCom
   add constraint FK_ExCU_LMExCm foreign key (UserID)
      references LMGroupExpressComAddress (AddressID);
alter table LMGroupExpressCom
   add constraint FK_ExCZ_LMExCm foreign key (ZipID)
      references LMGroupExpressComAddress (AddressID);

update YukonRoleProperty set Description='Sender address of emails sent on behalf of energy company, e.g. control odds and opt out notification emails.' where RolePropertyID=-1100;

create or replace view "ExpressComAddress_View" as
select x.LMGroupID, x.RouteID, x.SerialNumber, s.Address as serviceaddress,
g.Address as geoaddress, b.Address as substationaddress, f.Address as feederaddress,
z.Address as ZipCodeAddress, us.Address as UDAddress, p.Address as programaddress, sp.Address as SplinterAddress, x.AddressUsage, x.RelayUsage
from LMGroupExpressCom x, LMGroupExpressComAddress s, 
LMGroupExpressComAddress g, LMGroupExpressComAddress b, LMGroupExpressComAddress f,
LMGroupExpressComAddress p,
LMGroupExpressComAddress sp, LMGroupExpressComAddress us, LMGroupExpressComAddress z
where ( x.ServiceProviderID = s.AddressID and ( s.AddressType = 'SERVICE' or s.AddressID = 0 ) )
and ( x.FeederID = f.AddressID and ( f.AddressType = 'FEEDER' or f.AddressID = 0 ) )
and ( x.GeoID = g.AddressID and ( g.AddressType = 'GEO' or g.AddressID = 0 ) )
and ( x.ProgramID = p.AddressID and ( p.AddressType = 'PROGRAM' or p.AddressID = 0 ) )
and ( x.SubstationID = b.AddressID and ( b.AddressType = 'SUBSTATION' or b.AddressID = 0 ) )
and ( x.SplinterID = sp.AddressID and ( sp.AddressType = 'SPLINTER' or sp.AddressID = 0 ) )
and ( x.UserID = us.AddressID and ( us.AddressType = 'USER' or us.AddressID = 0 ) )
and ( x.ZipID = z.AddressID and ( z.AddressType = 'ZIP' or z.AddressID = 0 ) );

/*N/A*/
insert into command values(-0, 'Not Available Yet', 'Not Available Yet', 'DEVICE');

/*MCT-BASE*/
insert into command values(-1, 'getvalue kWh', 'Read Energy', 'All MCTs');
insert into command values(-2, 'getvalue demand', 'Read Current Demand', 'All MCTs');
insert into command values(-3, 'getconfig model', 'Read Options', 'All MCTs');
insert into command values(-4, 'putvalue kyz 1 reset', 'Clear kWh Reading', 'All MCTs');
insert into command values(-5, 'getvalue powerfail', 'Read Powerfail', 'All MCTs');
insert into command values(-6, 'getstatus internal', 'Read General Info', 'All MCTs'); 
insert into command values(-7, 'getconfig mult kyz 1', 'Read MPKH ()', 'All MCTs');
insert into command values(-8, 'putconfig emetcon multiplier kyz1 ?Multiplier(x.xxx)', 'Write MPKH ()', 'All MCTs');
insert into command values(-9, 'getconfig interval ?LP/LI', 'Read Demand Interval (LP or LI)', 'All MCTs');
insert into command values(-10, 'putconfig emetcon interval ?LP/LI', 'Write Demand Interval (LP or LI)', 'All MCTs');
/*MCT-213, 310ID*/
insert into command values(-11, 'getstatus disconnect', 'Read Disconnect Status', 'All Disconnect Meters');
insert into command values(-12, 'control disconnect', 'Disconnect Meter', 'All Disconnect Meters');
insert into command values(-13, 'control connect', 'Connect Meter', 'All Disconnect Meters');
/*MCT-250, 318, 318L, 360, 370*/
insert into command values(-14, 'getstatus external', 'Read Status Points', 'All Status Input');
/*LS-BASE*/
insert into command values(-15, 'getstatus LP', 'Read LS Info', 'All LP Meters');
insert into command values(-16, 'Not Available Yet', 'Read LS Intervals 1 Thru 6', 'All LP Meters');
insert into command values(-17, 'Not Available Yet', 'Read  6 LS Intervals Starting at ?', 'All LP Meters');
insert into command values(-18, 'getconfig time', 'Read Date/Time', 'All LP Meters');
insert into command values(-19, 'getconfig time sync', 'Read Last TimeSync', 'All LP Meters');
/*IED-BASE*/
insert into command values(-20, 'getvalue ied demand', 'Read IED Last Interval Demands', 'All IED Meters');
insert into command values(-21, 'getvalue ied kwh', 'Read IED KWH/KW', 'All IED Meters');
insert into command values(-22, 'getconfig ied time', 'Read IED Date/Time', 'All IED Meters');
insert into command values(-23, 'Not Available', 'Read IED TOU Rate [A,B,C,or D]', 'All IED Meters');
insert into command values(-24, 'Not Available', 'Read IED Reset Count', 'All IED Meters');
insert into command values(-25, 'getconfig ied scan', 'Read IED Scan Info', 'All IED Meters');
insert into command values(-26, 'putvalue ied reset', 'Reset IED Demand', 'All IED Meters');
/*LoadGroup-BASE*/
insert into command values(-27, 'control shed 5m', 'Shed Group', 'All Load Group');
insert into command values(-28, 'control restore', 'Restore Group', 'All Load Group');
/*Alpha-BASE*/
insert into command values(-29, 'scan integrity', 'Force Scan', 'All Alpha Meters');
/*CBC-BASE*/
insert into command values(-30, 'control open', 'OPEN Cap Bank', 'All CBCs');
insert into command values(-31, 'control close', 'CLOSE Cap Bank', 'All CBCs');
insert into command values(-32, 'putstatus ovuv disable', 'Disable OVUV', 'All CBCs');
insert into command values(-33, 'putstatus ovuv enable', 'Enable OVUV', 'All CBCs');
/*CCU-BASE, RTU-BASE, LCU-BASE, TCU-BASE*/
insert into command values(-34, 'ping', 'Ping', 'All Ping-able');
insert into command values(-35, 'loop', '1 Loopback', 'All Ping-able');
insert into command values(-36, 'loop 5', '5 Loopbacks', 'All Ping-able');
/*CCU-711*/
insert into command values(-37, 'putstatus reset', 'CCU Reset', 'CCU-711');
/*Emetcon Group*/
insert into command values(-38, 'control shed 7.m', 'Shed 7.5min', 'Emetcon Group');
insert into command values(-39, 'control shed 15m', 'Shed 15-min', 'Emetcon Group');
insert into command values(-40, 'control shed 30m', 'Shed 30-min', 'Emetcon Group');
insert into command values(-41, 'control shed 60m', 'Shed 60-min', 'Emetcon Group');
insert into command values(-42, 'control shed ?''NumMins''m', 'Shed x-min', 'Emetcon Group');
insert into command values(-43, 'control restore', 'Restore', 'Emetcon Group');
/*Versacom Group*/
insert into command values(-44, 'control cycle 50 period 30 count 4', 'Cycle 50% / 30min', 'Versacom Group');
insert into command values(-45, 'control cycle terminate', 'Terminate Cycle', 'Versacom Group');
insert into command values(-46, 'putconfig service out', 'Set to Out-Of-Service', 'Versacom Group');
insert into command values(-47, 'putconfig service in', 'Set to In-Service', 'Versacom Group');
insert into command values(-48, 'putstatus reset r1 r2 r3 cl', 'Reset All Counters', 'Versacom Group');
insert into command values(-49, 'putconfig led yyy', 'Configure LEDS (report, test, load)', 'Versacom Group');
/*TCU-BASE*/
/*insert into command values(-50, 'loop', '1 TCU Loop', 'All TCUs');*/
/*insert into command values(-51, 'loop 5', '5 TCU Loops', 'All TCUs');*/
/*Repeater-BASE*/
insert into command values(-52, 'getconfig role 1', 'Read Roles', 'All Repeaters');
insert into command values(-53, 'getconfig emetcon install', 'Download All Roles', 'All Repeaters');
insert into command values(-54, 'loop locate', 'Locate Device', 'All Repeaters');
/*ION-BASE*/
insert into command values(-55, 'scan general', 'Scan Status Points', 'All ION Meters');
insert into command values(-56, 'scan integrity', 'Scan Power Meter and Status', 'All ION Meters');
insert into command values(-57, 'getstatus eventlog', 'Retrieve Event Log', 'All ION Meters');
/*IEDAlpha-BASE*/
insert into command values(-58, 'putconfig emetcon ied class 72 1', 'Set Current Period (Alpha)', 'DEVICE');
insert into command values(-59, 'putconfig emetcon ied class 72 2', 'Set Previous Period (Alpha)', 'DEVICE');
/*EDGEKV-BASE*/
insert into command values(-60, 'getvalue ied demand', 'Read IED Volts', 'DEVICE');
insert into command values(-61, 'putconfig emetcon ied class 0 0', 'Set Current Period (GEKV)', 'DEVICE');
insert into command values(-62, 'putconfig emetcon ied class 0 1', 'Set Previous Period (GEKV)', 'DEVICE');
/*ExpresscomSerial*/
insert into command values(-63, 'putconfig xcom raw 0x30 0x00 0x02 0x58', 'Cold Load Pickup (load, time x 0.5sec)', 'ExpresscomSerial');
insert into command values(-64, 'putstatus xcom prop inc', 'Increment Prop Counter', 'ExpresscomSerial');
insert into command values(-65, 'putconfig xcom raw 0x05 0x00', 'Reset CopCount', 'ExpresscomSerial');
insert into command values(-66, 'putconfig xcom main 0x01 0x40', 'Clear Prop Counter', 'ExpresscomSerial');
insert into command values(-67, 'putconfig xcom main 0x01 0x80', 'Clear Comm Loss COunter', 'ExpresscomSerial');
insert into command values(-68, 'putconfig xcom serv out temp 24', 'Temp Out-Of-Service (hours)', 'ExpresscomSerial');
insert into command values(-69, 'putconfig xcom serv out temp', 'In Service', 'ExpresscomSerial');
/*LCRSerial*/
insert into command values(-70, 'control shed 5m relay 1', 'Shed 5-min Relay 1', 'LCRSerial');
insert into command values(-71, 'control restore relay 1', 'Restore Relay 1', 'LCRSerial');
insert into command values(-72, 'control cycle 50 period 30 relay 1', 'Cycle 50 Period-30 Relay 1', 'LCRSerial');
insert into command values(-73, 'control cycle terminate relay 1', 'Terminate Cycle Relay 1', 'LCRSerial');
insert into command values(-74, 'putconfig template ''?LoadGroup''', 'Install Versacom Addressing', 'LCRSerial');
insert into command values(-75, 'putconfig cycle r1 50', 'Install Cycle Count', 'LCRSerial');
insert into command values(-76, 'putconfig cold_load r1 10', 'Install Versacom Cold Load (relay, minutes)', 'LCRSerial');
insert into command values(-77, 'putconfig raw 0x3a ff', 'Emetcon Cold Load (ON -ff / OFF -00', 'LCRSerial');
insert into command values(-78, 'putconfig xcom raw 40 ?''Weighting (1-8)''', 'Store a TrueCycle Day', 'LCRSerial');
insert into command values(-79, 'putconfig raw 36 1', 'Set Versacom Mode', 'LCRSerial');
insert into command values(-80, 'putconfig raw 35 0', 'Set Emetcon Mode', 'LCRSerial');
/* MCT410IL */
insert into command values(-81, 'getvalue demand', 'Read KW Demand, Current Voltage, Blink Count', 'MCT-410IL');
insert into command values(-82, 'getvalue voltage', 'Read Min / Max Voltage', 'MCT-410IL');
insert into command values(-83, 'putconfig emetcon timesync', 'Write Time/Date to Meter', 'MCT-410IL');
insert into command values(-84, 'getvalue peak', 'Read Current Peak', 'MCT-410IL');

INSERT INTO DEVICETYPECOMMAND VALUES (0, 0, 'System', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-1, 0, 'CICustomer', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-2, 0, 'Davis Weather', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-3, 0, 'Fulcrum', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-4, 0, 'Landis-Gyr S4', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-5, 0, 'MCT Broadcast', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-6, 0, 'Sixnet', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-7, 0, 'Tap Terminal', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-8, 0, 'VersacomSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-9, 0, 'SA205Serial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-10, 0, 'SA305Serial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-11, 0, 'CollectionGroup', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-12, 0, 'TestCollectionGroup', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-13, -1, 'LMT-2', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-14, -2, 'LMT-2', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-15, -3, 'LMT-2', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-16, -4, 'LMT-2', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-17, -5, 'LMT-2', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-18, -6, 'LMT-2', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-19, -7, 'LMT-2', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-20, -8, 'LMT-2', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-21, -9, 'LMT-2', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-22, -10, 'LMT-2', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-23, -27, 'Macro Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-24, -28, 'Macro Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-25, -29, 'Alpha A1', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-26, -29, 'Alpha Power Plus', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-27, -30, 'CBC 6510', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-28, -31, 'CBC 6510', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-29, -32, 'CBC 6510', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-30, -33, 'CBC 6510', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-31, -30, 'CBC FP-2800', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-32, -31, 'CBC FP-2800', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-33, -32, 'CBC FP-2800', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-34, -33, 'CBC FP-2800', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-35, -30, 'CBC Versacom', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-36, -31, 'CBC Versacom', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-37, -32, 'CBC Versacom', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-38, -33, 'CBC Versacom', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-39, -30, 'Cap Bank', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-40, -31, 'Cap Bank', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-41, -32, 'Cap Bank', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-42, -33, 'Cap Bank', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-43, -34, 'CCU-710A', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-44, -35, 'CCU-710A', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-45, -36, 'CCU-710A', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-46, -34, 'CCU-711', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-47, -35, 'CCU-711', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-48, -36, 'CCU-711', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-49, -37, 'CCU-711', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-50, -38, 'Emetcon Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-51, -39, 'Emetcon Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-52, -40, 'Emetcon Group', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-53, -41, 'Emetcon Group', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-54, -42, 'Emetcon Group', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-55, -43, 'Emetcon Group', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-56, -27, 'Expresscom Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-57, -28, 'Expresscom Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-58, -27, 'Golay Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-59, -28, 'Golay Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-60, -27, 'Macro Group', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-61, -28, 'Macro Group', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-62, -27, 'Ripple Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-63, -28, 'Ripple Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-64, -27, 'SA-205 Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-65, -28, 'SA-205 Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-66, -27, 'SA-305 Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-67, -28, 'SA-305 Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-68, -27, 'SA-Digital Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-69, -28, 'SA-Digital Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-70, -27, 'Versacom Group', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-71, -28, 'Versacom Group', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-72, -44, 'Versacom Group', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-73, -45, 'Versacom Group', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-74, -46, 'Versacom Group', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-75, -47, 'Versacom Group', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-76, -48, 'Versacom Group', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-77, -49, 'Versacom Group', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-78, -34, 'TCU-5000', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-79, -35, 'TCU-5000', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-80, -36, 'TCU-5000', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-81, -34, 'TCU-5500', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-82, -35, 'TCU-5500', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-83, -36, 'TCU-5500', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-84, -52, 'Repeater 800', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-85, -3, 'Repeater 800', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-86, -53, 'Repeater 800', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-87, -54, 'Repeater 800', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-88, -52, 'Repeater', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-89, -3, 'Repeater', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-90, -53, 'Repeater', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-91, -54, 'Repeater', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-92, -34, 'RTU-DART', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-93, -35, 'RTU-DART', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-94, -36, 'RTU-DART', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-95, -34, 'RTU-DNP', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-96, -35, 'RTU-DNP', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-97, -36, 'RTU-DNP', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-98, -34, 'RTU-ILEX', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-99, -35, 'RTU-ILEX', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-100, -36, 'RTU-ILEX', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-101, -34, 'RTU-WELCO', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-102, -35, 'RTU-WELCO', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-103, -36, 'RTU-WELCO', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-104, -55, 'ION-7330', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-105, -56, 'ION-7330', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-106, -57, 'ION-7330', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-107, -55, 'ION-7700', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-108, -56, 'ION-7700', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-109, -57, 'ION-7700', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-110, -55, 'ION-8300', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-111, -56, 'ION-8300', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-112, -57, 'ION-8300', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-113, -34, 'LCU-415', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-114, -35, 'LCU-415', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-115, -36, 'LCU-415', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-116, -34, 'LCU-EASTRIVER', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-117, -35, 'LCU-EASTRIVER', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-118, -36, 'LCU-EASTRIVER', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-119, -34, 'LCU-LG', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-120, -35, 'LCU-LG', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-121, -36, 'LCU-LG', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-122, -34, 'LCU-T3026', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-123, -35, 'LCU-T3026', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-124, -36, 'LCU-T3026', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-125, -63, 'ExpresscomSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-126, -64, 'ExpresscomSerial', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-127, -65, 'ExpresscomSerial', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-128, -66, 'ExpresscomSerial', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-129, -67, 'ExpresscomSerial', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-130, -68, 'ExpresscomSerial', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-131, -69, 'ExpresscomSerial', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-132, -70, 'LCRSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-133, -71, 'LCRSerial', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-134, -72, 'LCRSerial', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-135, -73, 'LCRSerial', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-136, -74, 'LCRSerial', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-137, -46, 'LCRSerial', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-138, -47, 'LCRSerial', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-139, -75, 'LCRSerial', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-140, -76, 'LCRSerial', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-141, -77, 'LCRSerial', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-142, -78, 'LCRSerial', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-143, -79, 'LCRSerial', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-144, -80, 'LCRSerial', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-145, -49, 'LCRSerial', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-146, -48, 'LCRSerial', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-147, -1, 'MCT-210', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-148, -2, 'MCT-210', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-149, -3, 'MCT-210', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-150, -4, 'MCT-210', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-151, -5, 'MCT-210', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-152, -6, 'MCT-210', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-153, -7, 'MCT-210', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-154, -8, 'MCT-210', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-155, -9, 'MCT-210', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-156, -10, 'MCT-210', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-157, -1, 'MCT-213', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-158, -2, 'MCT-213', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-159, -3, 'MCT-213', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-160, -4, 'MCT-213', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-161, -5, 'MCT-213', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-162, -6, 'MCT-213', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-163, -7, 'MCT-213', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-164, -8, 'MCT-213', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-165, -9, 'MCT-213', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-166, -10, 'MCT-213', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-167, -11, 'MCT-213', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-168, -12, 'MCT-213', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-169, -13, 'MCT-213', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-170, -1, 'MCT-240', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-171, -2, 'MCT-240', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-172, -3, 'MCT-240', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-173, -4, 'MCT-240', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-174, -5, 'MCT-240', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-175, -6, 'MCT-240', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-176, -7, 'MCT-240', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-177, -8, 'MCT-240', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-178, -9, 'MCT-240', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-179, -10, 'MCT-240', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-180, -15, 'MCT-240', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-181, -18, 'MCT-240', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-182, -19, 'MCT-240', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-183, -1, 'MCT-248', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-184, -2, 'MCT-248', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-185, -3, 'MCT-248', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-186, -4, 'MCT-248', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-187, -5, 'MCT-248', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-188, -6, 'MCT-248', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-189, -7, 'MCT-248', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-190, -8, 'MCT-248', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-191, -9, 'MCT-248', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-192, -10, 'MCT-248', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-193, -15, 'MCT-248', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-194, -18, 'MCT-248', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-195, -19, 'MCT-248', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-196, -1, 'MCT-250', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-197, -2, 'MCT-250', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-198, -3, 'MCT-250', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-199, -4, 'MCT-250', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-200, -5, 'MCT-250', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-201, -6, 'MCT-250', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-202, -7, 'MCT-250', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-203, -8, 'MCT-250', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-204, -9, 'MCT-250', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-205, -10, 'MCT-250', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-206, -14, 'MCT-250', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-207, -15, 'MCT-250', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-208, -18, 'MCT-250', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-209, -19, 'MCT-250', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-210, -1, 'MCT-310', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-211, -2, 'MCT-310', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-212, -3, 'MCT-310', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-213, -4, 'MCT-310', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-214, -5, 'MCT-310', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-215, -6, 'MCT-310', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-216, -7, 'MCT-310', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-217, -8, 'MCT-310', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-218, -9, 'MCT-310', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-219, -10, 'MCT-310', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-220, -1, 'MCT-310CT', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-221, -2, 'MCT-310CT', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-222, -3, 'MCT-310CT', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-223, -4, 'MCT-310CT', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-224, -5, 'MCT-310CT', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-225, -6, 'MCT-310CT', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-226, -7, 'MCT-310CT', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-227, -8, 'MCT-310CT', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-228, -9, 'MCT-310CT', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-229, -10, 'MCT-310CT', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-230, -1, 'MCT-310IDL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-231, -2, 'MCT-310IDL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-232, -3, 'MCT-310IDL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-233, -4, 'MCT-310IDL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-234, -5, 'MCT-310IDL', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-235, -6, 'MCT-310IDL', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-236, -7, 'MCT-310IDL', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-237, -8, 'MCT-310IDL', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-238, -9, 'MCT-310IDL', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-239, -10, 'MCT-310IDL', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-240, -1, 'MCT-310ID', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-241, -2, 'MCT-310ID', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-242, -3, 'MCT-310ID', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-243, -4, 'MCT-310ID', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-244, -5, 'MCT-310ID', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-245, -6, 'MCT-310ID', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-246, -7, 'MCT-310ID', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-247, -8, 'MCT-310ID', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-248, -9, 'MCT-310ID', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-249, -10, 'MCT-310ID', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-250, -11, 'MCT-310ID', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-251, -12, 'MCT-310ID', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-252, -13, 'MCT-310ID', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-253, -1, 'MCT-310IL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-254, -2, 'MCT-310IL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-255, -3, 'MCT-310IL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-256, -4, 'MCT-310IL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-257, -5, 'MCT-310IL', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-258, -6, 'MCT-310IL', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-259, -7, 'MCT-310IL', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-260, -8, 'MCT-310IL', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-261, -9, 'MCT-310IL', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-262, -10, 'MCT-310IL', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-263, -15, 'MCT-310IL', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-264, -18, 'MCT-310IL', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-265, -19, 'MCT-310IL', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-266, -1, 'MCT-318', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-267, -2, 'MCT-318', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-268, -3, 'MCT-318', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-269, -4, 'MCT-318', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-270, -5, 'MCT-318', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-271, -6, 'MCT-318', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-272, -7, 'MCT-318', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-273, -8, 'MCT-318', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-274, -9, 'MCT-318', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-275, -10, 'MCT-318', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-276, -14, 'MCT-318', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-277, -1, 'MCT-318L', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-278, -2, 'MCT-318L', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-279, -3, 'MCT-318L', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-280, -4, 'MCT-318L', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-281, -5, 'MCT-318L', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-282, -6, 'MCT-318L', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-283, -7, 'MCT-318L', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-284, -8, 'MCT-318L', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-285, -9, 'MCT-318L', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-286, -10, 'MCT-318L', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-287, -14, 'MCT-318L', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-288, -1, 'MCT-360', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-289, -2, 'MCT-360', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-290, -3, 'MCT-360', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-291, -4, 'MCT-360', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-292, -5, 'MCT-360', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-293, -6, 'MCT-360', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-294, -7, 'MCT-360', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-295, -8, 'MCT-360', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-296, -9, 'MCT-360', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-297, -10, 'MCT-360', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-298, -14, 'MCT-360', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-299, -26, 'MCT-360', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-300, -1, 'MCT-370', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-301, -2, 'MCT-370', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-302, -3, 'MCT-370', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-303, -4, 'MCT-370', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-304, -5, 'MCT-370', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-305, -6, 'MCT-370', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-306, -7, 'MCT-370', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-307, -8, 'MCT-370', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-308, -9, 'MCT-370', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-309, -10, 'MCT-370', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-310, -14, 'MCT-370', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-311, -20, 'MCT-370', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-312, -21, 'MCT-370', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-313, -22, 'MCT-370', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-314, -23, 'MCT-370', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-315, -24, 'MCT-370', 16, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-316, -25, 'MCT-370', 17, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-317, -26, 'MCT-370', 18, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-318, -1, 'MCT-410IL', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-319, -81, 'MCT-410IL', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-320, -3, 'MCT-410IL', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-321, -6, 'MCT-410IL', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-322, -34, 'MCT-410IL', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-323, -82, 'MCT-410IL', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-324, -18, 'MCT-410IL', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-325, -19, 'MCT-410IL', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-326, -83, 'MCT-410IL', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-327, -84, 'MCT-410IL', 10, 'Y');

alter table fdrtranslation drop constraint PK_FDRTrans;
alter table FDRTRANSLATION add constraint PK_FDRTrans primary key (POINTID, DIRECTIONTYPE, InterfaceType, TRANSLATION);

update YukonRoleProperty set KeyName='stars_preload_data', DefaultValue='true', Description='Controls whether the STARS application should preload data into the cache.' where RolePropertyID=-1013;

insert into YukonRoleProperty values(-10112,-101,'cbc_allow_ovuv','false','Allows users to toggle OV/UV usage on capbanks');
insert into YukonGroupRole values(-1031,-2,-101,-10112,'(none)');

insert into FDRInterface values( 19, 'XA21LM','Receive,Send', 't' );
insert into FDRInterfaceOption values(19, 'Translation', 1, 'Text', '(none)' );

update YukonRoleProperty set DefaultValue='yukon/DefaultHeader.gif' where RolePropertyID=-10805;







/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '10-DEC-2004', 'Manual version insert done', 0);