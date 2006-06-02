/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

delete from YukonServices where serviceid=-1;
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
insert into CTIDatabase values('3.2', 'DBUPdater', '01-JUNE-2006', 'Manual version insert done', 6 );
