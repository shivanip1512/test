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
VirtualTimeout       numeric              null
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
Duration             numeric              not null,
StartGear            numeric              not null
);
go
alter table LMControlScenarioProgram
   add constraint PK_LMCONTROLSCENARIOPROGRAM primary key  (ScenarioID);
go
alter table LMControlScenarioProgram
   add constraint FK_LMC_REF__LMP foreign key (ProgramID)
      references LMPROGRAM (DEVICEID);
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


create view LMProgram_View (DeviceID, ControlType, ConstraintID , ConstraintName , AvailableSeasons , AvailableWeekDays , MaxHoursDaily , MaxHoursMonthly , MaxHoursSeasonal , MaxHoursAnnually , MinActivateTime , MinRestartTime , MaxDailyOps , MaxActivateTime , HolidayScheduleID , SeasonScheduleID ) as
select t.DEVICEID, t.CONTROLTYPE, u.ConstraintID, u.ConstraintName, u.AvailableSeasons, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID;
go


insert into LMProgramConstraints 
select deviceid, 'Constraint: ' + LTRIM(STR(deviceid)), AvailableSeasons, AvailableWeekdays, MaxHoursDaily,
MaxHoursMonthly, MaxHoursSeasonal, MaxHoursAnnually, MinActivateTime, MinRestartTime,
0, 0, HolidayScheduleID, SeasonScheduleID from LMProgram;


update LMProgram set constraintid = deviceid;
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







/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.01', 'Ryan', '5-MAR-2004', 'Many changes to a major version jump');
go