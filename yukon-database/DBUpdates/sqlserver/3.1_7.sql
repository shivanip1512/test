/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create   index Indx_lmcnt_paoid on LMControlHistory (
PAObjectID
);
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
insert into CTIDatabase values('3.1', 'Ryan', '25-MAY-2005', 'Manual version insert done', 7);