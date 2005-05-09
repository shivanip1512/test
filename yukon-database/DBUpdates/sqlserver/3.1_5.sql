/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table lmprogramdirect add triggeroffset float;
go
update lmprogramdirect set triggeroffset=0.0;
go
alter table lmprogramdirect alter column triggeroffset float not null;
go


/* @error ignore */
update yukongrouprole set rolepropertyid = -30201 where grouproleid = -412;
/* @error ignore */
update yukongrouprole set rolepropertyid = -30201 where grouproleid = -1121;
go
/* @error ignore */
insert into yukongrouprole values (-767,-301,-210,-21002,'(none)');
/* @error ignore */
insert into YukonGroupRole values (-1267,-2,-210,-21002,'(none)');
/* @error ignore */
insert into yukongrouprole values (-2072,-303,-210,-21002,'(none)');
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