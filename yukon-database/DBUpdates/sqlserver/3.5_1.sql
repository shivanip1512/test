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
insert into CTIDatabase values('3.5', 'David', '30-Jan-2008', 'Latest Update', 1 );
