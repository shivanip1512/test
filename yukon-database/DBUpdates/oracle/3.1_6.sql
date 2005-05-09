/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table dynamiclmprogramdirect drop column dailyops;
alter table dynamiclmgroup add dailyops smallint;
update dynamiclmgroup set dailyops = 0;
alter table dynamiclmgroup modify dailyops smallint not null;

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
