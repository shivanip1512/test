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
   VirtualTimeout       NUMBER
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
      references LMPROGRAM (DEVICEID);


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
go


create or replace view LMProgram_View (DeviceID, ControlType, ConstraintID , ConstraintName , AvailableSeasons , AvailableWeekDays , MaxHoursDaily , MaxHoursMonthly , MaxHoursSeasonal , MaxHoursAnnually , MinActivateTime , MinRestartTime , MaxDailyOps , MaxActivateTime , HolidayScheduleID , SeasonScheduleID ) as
select t.DEVICEID, t.CONTROLTYPE, u.ConstraintID, u.ConstraintName, u.AvailableSeasons, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID;
go


insert into LMProgramConstraints 
select deviceid, CONCAT('Constraint: ', LTRIM(deviceid) ), AvailableSeasons, AvailableWeekdays, MaxHoursDaily,
MaxHoursMonthly, MaxHoursSeasonal, MaxHoursAnnually, MinActivateTime, MinRestartTime,
0, 0, HolidayScheduleID, SeasonScheduleID from LMProgram;


update LMProgram set constraintid = deviceid;

alter table LMProgram drop constraint FK_SesSch_LmPr;
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
   LBTMode              NUMBER                           not null
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

insert into YukonGroupRole values(-170,-100,-103,-10301,'true');
insert into YukonGroupRole values(-170,-100,-103,-10302,'true');
insert into YukonGroupRole values(-170,-100,-103,-10303,'false');
insert into YukonGroupRole values(-170,-100,-103,-10304,'false');

insert into YukonGroupRole values(-1070,-2,-103,-10301,'true');
insert into YukonGroupRole values(-1070,-2,-103,-10302,'true');
insert into YukonGroupRole values(-1070,-2,-103,-10303,'false');
insert into YukonGroupRole values(-1070,-2,-103,-10304,'false');

insert into YukonUserRole values(-170,-1,-103,-10301,'true');
insert into YukonUserRole values(-170,-1,-103,-10302,'true');
insert into YukonUserRole values(-170,-1,-103,-10303,'false');
insert into YukonUserRole values(-170,-1,-103,-10304,'false');

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '5-MAR-2004', 'Many changes to a major version jump');
