/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'DBAdmin', '29-MARCH-2006', 'Manual version insert done', 21);
