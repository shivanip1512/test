/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table dynamiclmprogramdirect drop column dailyops;
go
alter table dynamiclmgroup add dailyops smallint;
go
update dynamiclmgroup set dailyops = 0;
go
alter table dynamiclmgroup modify dailyops smallint not null;
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
