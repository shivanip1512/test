/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* @error ignore */
drop table TouRateOffset;
go

/* @error ignore */
drop table TOUDeviceMapping;
go

update LMProgramDirectGear set ControlMethod = 'ThermostatRamping' where ControlMethod = 'ThermostatSetback';
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
insert into CTIDatabase values('3.1', 'Ryan', '28-OCT-2005', 'Manual version insert done', 15);