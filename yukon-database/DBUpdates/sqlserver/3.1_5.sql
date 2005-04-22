/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table lmprogramdirect add triggernum smallint;
go
update lmprogramdirect set triggernum=-1;
go
alter table lmprogramdirect alter column triggernum not null;
go
alter table lmprogramdirect add triggeroffset smallint;
go
update lmprogramdirect set triggeroffset=0;
go
alter table lmprogramdirect alter column triggeroffset not null;
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
