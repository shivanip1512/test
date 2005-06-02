/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create   index Indx_lmcnt_paoid on LMControlHistory (
PAObjectID
);
go

update LMProgramConstraints set MaxHoursDaily=MaxHoursDaily*3600,MaxHoursMonthly=MaxHoursMonthly*3600,MaxHoursSeasonal=MaxHoursSeasonal*3600,MaxHoursAnnually=MaxHoursAnnually*3600;
go

update LMProgramConstraints set MaxHoursDaily=MaxHoursDaily*3600,MaxHoursMonthly=MaxHoursMonthly*3600,MaxHoursSeasonal=MaxHoursSeasonal*3600,MaxHoursAnnually=MaxHoursAnnually*3600;
go

alter table LMControlAreaTrigger add TriggerID numeric;
go
alter table DynamicLMControlAreaTrigger add TriggerID numeric;
go

declare @cnt numeric set @cnt = 0 update LMControlAreaTrigger SET @cnt = TriggerID = @cnt + 1;
update dynamiclmcontrolareatrigger set triggerid =
(select lmcontrolareatrigger.triggerid
from lmcontrolareatrigger
where lmcontrolareatrigger.deviceid = dynamiclmcontrolareatrigger.deviceid
and lmcontrolareatrigger.triggernumber = dynamiclmcontrolareatrigger.triggernumber);
go

alter table LMControlAreaTrigger alter column TriggerID numeric not null;
go
alter table DynamicLMControlAreaTrigger alter column TriggerID numeric not null;
go

create unique index INDX_UNQ_LMCNTRTR_TRID on LMControlAreaTrigger (
TriggerID
);

insert into billingfileformats values (15, 'NCDC-Handheld');
insert into billingfileformats values (16, 'NISC TOU (kVarH)');






/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '1-JUN-2005', 'Manual version insert done', 7);