/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/
alter table FDRTranslation alter column Translation varchar(200) not null;
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
