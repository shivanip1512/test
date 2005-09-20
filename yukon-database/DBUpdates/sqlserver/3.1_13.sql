/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

insert into FDRInterface values( 23, 'ACSMULTI', 'Send,Send for control,Receive,Receive for control', 't' );
go

insert into FDRInterfaceOption values(23, 'Category', 1, 'Combo', 'PSEUDO,REAL,CALCULATED' );
insert into FDRInterfaceOption values(23, 'Remote', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(23, 'Point', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(23, 'Destination/Source', 4, 'Text', '(none)' );
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
insert into CTIDatabase values('3.1', 'Ryan', '13-SEP-2005', 'Manual version insert done', 13);