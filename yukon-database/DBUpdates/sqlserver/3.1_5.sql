/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table lmprogramdirect add triggeroffset float;
go
update lmprogramdirect set triggeroffset=0.0;
go
alter table lmprogramdirect alter column triggeroffset float not null;
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
insert into CTIDatabase values('3.1', 'Ryan', '1-MAY-2005', 'Manual version insert done', 5);