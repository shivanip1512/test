/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

update command set command='putconfig raw 36 0' where commandid=-74;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'DBAdmin', '27-ARIL-2006', 'Manual version insert done', 23);
