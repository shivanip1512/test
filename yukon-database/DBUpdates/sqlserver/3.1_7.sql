/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create   index Indx_lmcnt_paoid on LMControlHistory (
PAObjectID
);
go

update LMProgramConstraints set MaxHoursDaily=MaxHoursDaily*3600,MaxHoursMonthly=MaxHoursMonthly*3600,MaxHoursSeasonal=MaxHoursSeasonal*3600,MaxHoursAnnually=MaxHoursAnnually*3600;
go







/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */
