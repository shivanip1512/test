/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table capbank alter column maplocationid varchar(64) not null;
alter table capcontrolfeeder alter column maplocationid varchar(64) not null;
alter table capcontrolsubstationbus alter column maplocationid varchar(64) not null;
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
insert into CTIDatabase values('3.1', 'Ryan', '5-JUL-2005', 'Manual version insert done', 9);