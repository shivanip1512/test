/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table dynamiclmprogramdirect drop column dailyops;
go
alter table dynamiclmgroup add dailyops smallint;
go
update dynamiclmgroup set dailyops = 0;
go
alter table dynamiclmgroup alter column dailyops smallint not null;
go


alter table state alter column text varchar(32) not null;
go
insert into stategroup values( -6, '410 Disconnect', 'Status');
insert into state values( -6, 0, 'Confirmed Disconnected', 1, 6, 0);
insert into state values( -6, 1, 'Connected', 0, 6, 0);
insert into state values( -6, 2, 'Unconfirmed Disconnected', 3, 6, 0);
insert into state values( -6, 3, 'Connect Armed', 5, 6, 0);
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
