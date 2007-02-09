/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table cceventlog add kvarBefore float;
go
update cceventlog set kvarBefore = 0.0;
go
alter table cceventlog alter column kvarBefore float not null;
go

alter table cceventlog add kvarAfter float;
go
update cceventlog set kvarAfter = 0.0;
go
alter table cceventlog alter column kvarAfter float not null;
go

alter table cceventlog add kvarChange float;
go
update cceventlog set kvarChange = 0.0;
go
alter table cceventlog alter column kvarChange float not null;
go

alter table cceventlog add additionalInfo varchar(20);
go
update cceventlog set additionalInfo = '(none)';
go
alter table cceventlog alter column additionalInfo varchar(20) not null;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '06-Feb-2007', 'Latest Update', 13 );