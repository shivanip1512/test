/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/



create table SOELog (
LogID                numeric              not null,
PointID              numeric              not null,
SOEDateTime          datetime             not null,
Millis               numeric              not null,
Description          varchar(60)          not null,
AdditionalInfo       varchar(120)         not null
);
go
alter table SOELog
   add constraint SYS_SOELog_PK primary key  (LogID);

create   index Indx_SYSLG_PtId on SOELog (
PointID
);
create   index Indx_SYSLG_Date on SOELog (
SOEDateTime
);
create unique  index Indx_SOELog_PK on SOELog (
LogID
);
alter table SOELog
   add constraint FK_Soe_Pt foreign key (PointID)
      references POINT (POINTID);
go


create table DynamicPointAlarming (
PointID              numeric              not null,
AlarmCondition       numeric              not null,
CategoryID           numeric              not null,
AlarmTime            datetime             not null,
Action               varchar(60)          not null,
Description          varchar(120)         not null,
Tags                 numeric              not null,
LogID                numeric              not null,
SOE_TAG              numeric              not null,
Type                 numeric              not null,
UserName             varchar(30)          not null
);
go

alter table DynamicPointAlarming
   add constraint PK_DYNAMICPOINTALARMING primary key  (PointID, AlarmCondition);
alter table DynamicPointAlarming
   add constraint FKf_DynPtAl_SysL foreign key (LogID)
      references SYSTEMLOG (LOGID);
alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID);
go



alter table LMGroupMCT drop constraint FK_LMGrMC_Dev;
go
alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);
go


update displaycolumns set typenum=7, width=110 where displaynum=1 and typenum=10;
go
update displaycolumns set typenum=7, width=110, title='Additional Info' where displaynum >=4 and displaynum <=20 and typenum=8;
go
update state set foregroundcolor=10 where stategroupid=-5 and rawstate=9;
go


insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'Priority 10 Alarm Viewer', 'This display will recieve all priority 10 alarm events as they happen in the system.');
go
insert into displaycolumns values(14, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(14, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(14, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(14, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(14, 'User Name', 8, 5, 50 );
go


/* CICustomer Administrator Role */
insert into YukonRole values(-305,'Administrator','CICustomer','Administrator privilages.');
insert into yukonroleproperty values(-30500, -305, 'Contact Information Editable', 'false', 'Contact information is editable by the customer');

/* Web Client Customers Group */
insert into yukongroup values (-302, 'Web Client Customers', 'The default group of web client customers');

/* Web Client Customers Web Client role */
insert into yukongrouprole values (400, -302, -108, -10800, '/user/CILC/user_trending.jsp');
insert into yukongrouprole values (401, -302, -108, -10801, '(none)');
insert into yukongrouprole values (402, -302, -108, -10802, '(none)');
insert into yukongrouprole values (403, -302, -108, -10803, '(none)');
insert into yukongrouprole values (404, -302, -108, -10804, '(none)');
insert into yukongrouprole values (405, -302, -108, -10805, '(none)');
insert into yukongrouprole values (406, -302, -108, -10806, '(none)');

/* Web Client Customers Direct Load Control role */
insert into yukongrouprole values (407, -302, -300, -30000, '(none)');
insert into yukongrouprole values (408, -302, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into yukongrouprole values (409, -302, -301, -30100, '(none)');
insert into yukongrouprole values (410, -302, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into yukongrouprole values (411, -302, -302, -30200, '(none)');
insert into yukongrouprole values (412, -302, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into yukongrouprole values (413, -302, -304, -30400, '(none)');
insert into yukongrouprole values (414, -302, -304, -30401, 'true');

/* Web Client Customers Administrator role */
insert into yukongrouprole values (415, -302, -305, -30500, 'true');


/* @error ignore */
alter table customerbaseline drop constraint fk_cicst_cstbslne;
go

drop table CustomerbaseLine;
go


create table BaseLine (
BaselineID           numeric              not null,
BaselineName         varchar(30)          not null,
DaysUsed             numeric              not null,
PercentWindow        numeric              not null,
CalcDays             numeric              not null,
ExcludedWeekDays     char(7)              not null,
HolidaysUsed         numeric              not null
);
go

insert into baseline values (1, 'Default Baseline', 30, 75, 5, 'YNNNNNY', 0);

alter table BaseLine
   add constraint PK_BASELINE primary key  (BaselineID);
go
create unique  index Indx_BASELINE_PK on BaseLine (
BaselineID
);
go

create table CalcPointBaseline (
PointID              numeric              not null,
BaselineID           numeric              not null
);
go
alter table CalcPointBaseline
   add constraint PK_CalcBsPt primary key  (PointID);
go
create unique  index Indx_CLCPTB_PK on CalcPointBaseline (
BaselineID
);
go
alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID);
go
alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);
go

insert into calcpointbaseline select pointid, '1' from calccomponent WHERE functionname = 'Baseline';


/* Web Client Groups */
insert into yukongroup values (-301,'Web Client Operators','The default group of web client operators');
insert into yukongroup values (-300,'Residential Customers','The default group of residential customers');

/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1106,-2,'operator_group_name','WebClient Operators','Group name of all the web client operator logins');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10806,-108,'log_off_url','(none)','The url to take the user after logging off the Yukon web application');

/* Operator Administrator Role Properties */
insert into YukonRoleProperty values(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
insert into YukonRoleProperty values(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
insert into YukonRoleProperty values(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');

insert into YukonRoleProperty values(-20800,-201,'Link FAQ','FAQ.jsp','The customized FAQ link');
insert into YukonRoleProperty values(-20810,-201,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-20813,-201,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-20814,-201,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-20815,-201,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-20816,-201,'Text Reenable','reenable','Term for reenable');
insert into YukonRoleProperty values(-20819,-201,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-20820,-201,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-20830,-201,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20831,-201,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20832,-201,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20833,-201,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-20834,-201,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20855,-201,'Title Thermostat Schedule','Schedule','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-20856,-201,'Title Thermostat Manual','Manual','Title of the thermostat manual page');
insert into YukonRoleProperty values(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');

/* Residential Customer Role Properties */
insert into YukonRoleProperty values(-40100,-400,'Link FAQ','FAQ.jsp','The customized FAQ link');
insert into YukonRoleProperty values(-40101,-400,'Link Utility Email','FAQ.jsp','The customized utility email');
insert into YukonRoleProperty values(-40110,-400,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-40111,-400,'Text Controlled','controlled','Past form of the term for control');
insert into YukonRoleProperty values(-40112,-400,'Text Controlling','controlling','Present form of the term for control');
insert into YukonRoleProperty values(-40113,-400,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-40114,-400,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-40115,-400,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-40116,-400,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-40117,-400,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-40130,-400,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-40131,-400,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-40132,-400,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-40133,-400,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-40134,-400,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-40150,-400,'Title General','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40151,-400,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-40152,-400,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40153,-400,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40154,-400,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40155,-400,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40156,-400,'Title Utility','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40157,-400,'Title Thermostat Schedule','Schedule','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-40158,-400,'Title Thermostat Manual','Manual','Title of the thermostat manual page');
insert into YukonRoleProperty values(-40170,-400,'Description General','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40171,-400,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');
insert into YukonRoleProperty values(-40180,-400,'Image Corner','Mom.jpg','Image at the upper-left corner');
insert into YukonRoleProperty values(-40181,-400,'Image General','Family.jpg','Image on the general page');


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.42', 'Ryan', '1-Aug-2003', ' ');
go