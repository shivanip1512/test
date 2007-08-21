/******************************************/
/**** Oracle DBupdates   		       ****/
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
insert into CTIDatabase values('3.4', 'Jon', '02-Aug-2007', 'Latest Update', 5 );
