/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/
update YukonRole set Category = 'Capacitor Control' where Category = 'CapBank Control';
update YukonRole set RoleName = 'Administrator' where RoleName = 'CBC Control';
update YukonRole set Category = 'Capacitor Control' where Category = 'CBC Oneline';
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
/* __YUKON_VERSION__ */
