/********************************************/
/* Oracle 9.2 DBupdates                     */
/********************************************/

insert into FDRInterface values (21, 'PI','Receive', 't' );
insert into FDRInterfaceOption values(21, 'Tag Name', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(21, 'Period (sec)', 1, 'Text', '(none)' );











/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/



/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '11-MAR-2005', 'Manual version insert done', 31);