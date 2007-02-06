/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/
/*

insert into FDRInterface values (24, 'WABASH', 'Send', 'f' );
insert into FDRInterfaceOption values(24, 'SchedName', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(24, 'Path', 2, 'Text', 'c:\\yukon\\server\\export\\' );
insert into FDRInterfaceOption values(24, 'Filename', 3, 'Text', 'control.txt' );

/**********************/


/*********************** BEGIN JULIE: cceventlog column additions **********************/
alter table cceventlog add kvarBefore float;
go
update cceventlog  set kvarBefore = 0;
go
alter table cceventlog alter column kvarBefore float not null;
go

alter table cceventlog add kvarAfter float;
go
update cceventlog  set kvarAfter = 0;
go
alter table cceventlog alter column kvarAfter float not null;
go

alter table cceventlog add kvarChange float;
go
update cceventlog  set kvarChange = 0;
go
alter table cceventlog alter column kvarChange float not null;
go

alter table cceventlog add additionalInfo varchar(20);
go
update cceventlog  set additionalInfo = '(none)';
go
alter table cceventlog alter column additionalInfo varchar(20) not null;
go
/*********************** END JULIE: cceventlog column additions **********************/
