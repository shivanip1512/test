/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/
alter table FDRTranslation alter column Translation varchar(200) not null;
go

alter table dynamicccsubstationbus add iVControlTot float;
go
update dynamicccsubstationbus set iVControlTot = 0;
go
alter table dynamicccsubstationbus alter column iVControlTot float not null;
go
alter table dynamicccsubstationbus add iVCount numeric;
go
update dynamicccsubstationbus set iVCount = 0;
go
alter table dynamicccsubstationbus alter column iVCount numeric not null;
go
alter table dynamicccsubstationbus add iWControlTot float;
go
update dynamicccsubstationbus set iWControlTot = 0;
go
alter table dynamicccsubstationbus alter column iWControlTot float not null;
go
alter table dynamicccsubstationbus add iWCount numeric;
go
update dynamicccsubstationbus set iWCount = 0;
go
alter table dynamicccsubstationbus alter column iWCount numeric not null;
go
alter table dynamicccfeeder add iVControlTot float;
go
update dynamicccfeeder set iVControlTot = 0;
go
alter table dynamicccfeeder alter column iVControlTot float not null;
go
alter table dynamicccfeeder add iVCount numeric;
go
update dynamicccfeeder set iVCount = 0;
go
alter table dynamicccfeeder alter column iVCount numeric not null;
go
alter table dynamicccfeeder add iWControlTot float;
go
update dynamicccfeeder set iWControlTot = 0;
go
alter table dynamicccfeeder alter column iWControlTot float not null;
go
alter table dynamicccfeeder add iWCount numeric;
go
update dynamicccfeeder set iWCount = 0;
go
alter table dynamicccfeeder alter column iWCount numeric not null;
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
insert into CTIDatabase values('3.4', 'Jon', '24-Apr-2007', 'Latest Update', 1 );
