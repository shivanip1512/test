/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table DynamicLMControlHistory alter column ControlType varchar(128) not null;
go

alter table LMControlHistory alter column ControlType varchar(128) not null;
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
insert into CTIDatabase values('3.1', 'Ryan', '8-DEC-2005', 'Manual version insert done', 16);