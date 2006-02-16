/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

/* @error ignore */
alter table DynamicPointAlarming drop constraint FKf_DynPtAl_SysL;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '15-FEB-2005', 'Manual version insert done', 19);
