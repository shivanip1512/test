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
insert into CTIDatabase values('4.0', 'Matt K', '07-NOV-2008', 'Latest Update', 3 );
