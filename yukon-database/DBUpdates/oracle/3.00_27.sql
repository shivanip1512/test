/********************************************/
/* Oracle 9.2 DBupdates                     */
/*   Contains all 3.00 updates that         */
/*   occured after 3.0.13                   */
/********************************************/

/* @error ignore-remaining */

drop table TOUDeviceMapping;
drop table TOUDayMapping;
drop table TOURateOffset;
drop table TOUDay;
drop table TOUSchedule;


create table DynamicLMControlHistory  (
   PAObjectID           NUMBER                           not null,
   LMCtrlHistID         NUMBER                           not null,
   StartDateTime        DATE                             not null,
   SOE_Tag              NUMBER                           not null,
   ControlDuration      NUMBER                           not null,
   ControlType          VARCHAR2(32)                     not null,
   CurrentDailyTime     NUMBER                           not null,
   CurrentMonthlyTime   NUMBER                           not null,
   CurrentSeasonalTime  NUMBER                           not null,
   CurrentAnnualTime    NUMBER                           not null,
   ActiveRestore        CHAR(1)                          not null,
   ReductionValue       FLOAT                            not null,
   StopDateTime         DATE                             not null
);
alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key (PAObjectID);
alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);


create table TOUSchedule  (
   TOUScheduleID        NUMBER                           not null,
   TOUScheduleName      VARCHAR2(32)                     not null,
   TOUDefaultRate       VARCHAR2(4)                      not null
);
alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key (TOUScheduleID);

create table TOUDay  (
   TOUDayID             NUMBER                           not null,
   TOUDayName           VARCHAR2(32)                     not null
);
alter table TOUDay
   add constraint PK_TOUDAY primary key (TOUDayID);

create table TOUDayRateSwitches (
TOURateSwitchID	     NUMBER                           not null,
SwitchRate           varchar2(4)                      not null,
SwitchOffset         NUMBER                           not null,
TOUDayID             NUMBER                           not null
);
alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key  (TOURateSwitchID);
alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
create unique index Indx_TOUSWITCHRATE_PK on TOUDayRateSwitches (
TOUDayID, SwitchOffset
);

create table TOUDayMapping  (
   TOUScheduleID        NUMBER                           not null,
   TOUDayID             NUMBER                           not null,
   TOUDayOffset         NUMBER                           not null
);
alter table TOUDayMapping
   add constraint PK_TOUDAYMAPPING primary key (TOUScheduleID, TOUDayID);
alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);

alter table MCTConfig add DisplayDigits number;
update MCTConfig set DisplayDigits = 0;
alter table MCTConfig modify DisplayDigits not null;

create table DeviceMCT400Series  (
   DeviceID             NUMBER                           not null,
   DisconnectAddress    NUMBER                           not null,
   TOUScheduleID        NUMBER                           not null
);
alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key (DeviceID);
alter table DeviceMCT400Series
   add constraint FK_Dev4_Dev foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);
alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);

update yukonpaobject set type = 'MCT-410IL' where type like '%410%';

alter table ctidatabase drop constraint PK_CTIDATABASE;
alter table ctidatabase add constraint PK_CTIDATABASE primary key  (Version, Build);







/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '5-NOV-2004', 'Patch update to bring all other 3.00 up to date', 27 );
