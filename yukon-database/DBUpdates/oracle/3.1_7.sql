/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

create index Indx_lmcnt_paoid on LMControlHistory (
   PAObjectID ASC
);


update LMProgramConstraints set MaxHoursDaily=MaxHoursDaily*3600,MaxHoursMonthly=MaxHoursMonthly*3600,MaxHoursSeasonal=MaxHoursSeasonal*3600,MaxHoursAnnually=MaxHoursAnnually*3600;

alter table LMControlAreaTrigger add TriggerID number;
alter table DynamicLMControlAreaTrigger add TriggerID number;

update LMControlAreaTrigger SET TriggerID = ROWNUM;
update dynamiclmcontrolareatrigger set triggerid =
(select lmcontrolareatrigger.triggerid
from lmcontrolareatrigger
where lmcontrolareatrigger.deviceid = dynamiclmcontrolareatrigger.deviceid
and lmcontrolareatrigger.triggernumber = dynamiclmcontrolareatrigger.triggernumber);

alter table LMControlAreaTrigger modify TriggerID not null;
alter table DynamicLMControlAreaTrigger modify TriggerID not null;

create unique index INDX_UNQ_LMCNTRTR_TRID on LMControlAreaTrigger (
	TriggerID ASC
);

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