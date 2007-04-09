/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/
alter table dynamiccccapbank add twowaycbcstate numeric;
go
update dynamiccccapbank set twowaycbcstate = -1;
go
alter table dynamiccccapbank alter column twowaycbcstate numeric not null;
go
alter table dynamiccccapbank add twowaycbcstatetime datetime;
go
update dynamiccccapbank set twowaycbcstatetime = '01-JAN-1990';
go
alter table dynamiccccapbank alter column twowaycbcstatetime datetime not null;


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '04-Apr-2007', 'Latest Update', 14 );