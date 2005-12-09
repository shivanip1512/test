/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

drop table TouRateOffset;
drop table TOUDeviceMapping;

update LMProgramDirectGear set ControlMethod = 'ThermostatRamping' where ControlMethod = 'ThermostatSetback';











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