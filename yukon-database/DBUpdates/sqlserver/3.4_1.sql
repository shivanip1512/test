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


insert into YukonRole values (-1000,'Substation Display','CBC Oneline','Change display settings for substation details.');
insert into YukonRole values (-1001,'Feeder Display','CBC Oneline','Change display settings for feeder details.');
insert into YukonRole values (-1002,'Cap Bank Display','CBC Oneline','Change display settings for cap bank details.');

insert into YukonRoleProperty values(-100000, -1000, 'Target', 'true', 'display Target settings');
insert into YukonRoleProperty values(-100001, -1000, 'kVAR', 'true', 'display kVAR');
insert into YukonRoleProperty values(-100002, -1000, 'Estimated kVAR', 'true', 'display estimated kVAR');
insert into YukonRoleProperty values(-100003, -1000, 'Power Factor', 'true', 'display Power Factor');
insert into YukonRoleProperty values(-100004, -1000, 'Estimated Power Factor', 'true', 'display estimated Power Factor');
insert into YukonRoleProperty values(-100005, -1000, 'Watts', 'true', 'display Watts');
insert into YukonRoleProperty values(-100006, -1000, 'Volts', 'true', 'display Volts');
insert into YukonRoleProperty values(-100007, -1000, 'Daily Op Count', 'true', 'display Daily Operation Count');
insert into YukonRoleProperty values(-100008, -1000, 'Max Op Count', 'true', 'display Max Operation Count');

insert into YukonRoleProperty values(-100100, -1001, 'kVAR', 'true', 'display kVAR');
insert into YukonRoleProperty values(-100101, -1001, 'Power Factor', 'true', 'display Power Factor');
insert into YukonRoleProperty values(-100102, -1001, 'Watts', 'true', 'display Watts');
insert into YukonRoleProperty values(-100103, -1001, 'Daily Op Count', 'true', 'display Daily Operation Count');
insert into YukonRoleProperty values(-100104, -1001, 'Volts', 'true', 'display Volts');

insert into YukonRoleProperty values(-100200, -1002, 'Total Op Count', 'true', 'display Total Operation Count');
insert into YukonRoleProperty values(-100201, -1002, 'Bank Size', 'true', 'display Bank Size');
go

/* Start YUK-5316 */
/* @error ignore-begin */
alter table CCurtCurtailmentEvent add CCurtProgramTypeID numeric;
go
update CCurtCurtailmentEvent set CCurtProgramTypeID = 0;
go
alter table CCurtCurtailmentEvent alter column CCurtProgramTypeID numeric not null;
go
/* @error ignore-end */
/* End YUK-5316 */

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
