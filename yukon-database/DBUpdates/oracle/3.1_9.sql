/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table capbank modify maplocationid varchar2(64);
alter table capcontrolfeeder modify maplocationid varchar2(64);
alter table capcontrolsubstationbus modify maplocationid varchar2(64);











/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '5-JUL-2005', 'Manual version insert done', 9);