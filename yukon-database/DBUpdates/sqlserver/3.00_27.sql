/********************************************/
/* SQLServer 2000 DBupdates                 */
/*   Contains all 3.00 updates that         */
/*   occured after 3.0.13                   */
/********************************************/

/* @error ignore-remaining */

drop table TOUDeviceMapping;
drop table TOUDayMapping;
drop table TOURateOffset;
drop table TOUDay;
drop table TOUSchedule;
go


create table DynamicLMControlHistory (
PAObjectID           numeric              not null,
LMCtrlHistID         numeric              not null,
StartDateTime        datetime             not null,
SOE_Tag              numeric              not null,
ControlDuration      numeric              not null,
ControlType          varchar(32)          not null,
CurrentDailyTime     numeric              not null,
CurrentMonthlyTime   numeric              not null,
CurrentSeasonalTime  numeric              not null,
CurrentAnnualTime    numeric              not null,
ActiveRestore        char(1)              not null,
ReductionValue       float                not null,
StopDateTime         datetime             not null
);
go
alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key  (PAObjectID);
go
alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


create table TOUSchedule (
TOUScheduleID        numeric              not null,
TOUScheduleName      varchar(32)          not null,
TOUDefaultRate       varchar(4)           not null
);
go
alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key  (TOUScheduleID);
go

create table TOUDay (
TOUDayID             numeric              not null,
TOUDayName           varchar(32)          not null
);
go
alter table TOUDay
   add constraint PK_TOUDAY primary key  (TOUDayID);
go

create table TOUDayRateSwitches (
TOURateSwitchID	     numeric		  not null,
SwitchRate           varchar(4)           not null,
SwitchOffset         numeric              not null,
TOUDayID             numeric              not null
);
go
alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key  (TOURateSwitchID);
go
alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go
create unique index Indx_TOUSWITCHRATE_PK on TOUDayRateSwitches (
TOUDayID, SwitchOffset
);
go

create table TOUDayMapping (
TOUScheduleID        numeric              not null,
TOUDayID             numeric              not null,
TOUDayOffset         numeric              not null
);
go
alter table TOUDayRateSwitches
   add constraint PK_TOUDAYMAPPING primary key  (TOUScheduleID, TOUDayOffset);
go
alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go
alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go


alter table MCTConfig add DisplayDigits numeric;
go
update MCTConfig set DisplayDigits = 0;
go
alter table MCTConfig alter column DisplayDigits numeric not null;
go

create table DeviceMCT400Series (
DeviceID             numeric              not null,
DisconnectAddress    numeric              not null,
TOUScheduleID        numeric              not null
);
go
alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key  (DeviceID);
go
alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);
go
alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go

update yukonpaobject set type = 'MCT-410IL' where type like '%410%';

alter table ctidatabase drop constraint PK_CTIDATABASE;
go
alter table ctidatabase add constraint PK_CTIDATABASE primary key  (Version, Build);
go











/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/



/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '5-NOV-2004', 'Patch update to bring all other 3.00 up to date', 27 );
go