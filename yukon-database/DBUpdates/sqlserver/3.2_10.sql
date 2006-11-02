/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

alter table dynamiclmprogramdirect add additionalinfo varchar(80) not null default '(none)';

insert into fdrinterfaceoption values(12,'DrivePath',2,'Text','(none)');
insert into fdrinterfaceoption values(12,'Filename',3,'Text','(none)');

alter table lmprogramdirectgear add KWReduction float not null default 0.0;

alter table capcontrolstrategy add peakVARlag float;
go
update capcontrolstrategy set peakVARlag = 0;
go
alter table capcontrolstrategy alter column peakVARlag float not null;
go

alter table capcontrolstrategy add peakVARlead float;
go
update capcontrolstrategy set peakVARlead = 0;
go
alter table capcontrolstrategy alter column peakVARlead float not null;
go

alter table capcontrolstrategy add offPkVARlag float;
go
update capcontrolstrategy set offPkVARlag = 0;
go
alter table capcontrolstrategy alter column offPkVARlag float not null;
go

alter table capcontrolstrategy add offPkVARlead float;
go
update capcontrolstrategy set offPkVARlead = 0;
go
alter table capcontrolstrategy alter column offPkVARlead float not null;
go

alter table capcontrolstrategy add peakPFSetPoint float;
go
update capcontrolstrategy set peakPFSetPoint = 100;
go
alter table capcontrolstrategy alter column peakPFSetPoint float not null;
go

alter table capcontrolstrategy add offPkPFSetPoint float;
go
update capcontrolstrategy set offPkPFSetPoint = 100;
go
alter table capcontrolstrategy alter column offPkPFSetPoint float not null;
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
insert into CTIDatabase values('3.2', 'Jon', '30-OCT-2006', 'DB Update Script', 10 );
