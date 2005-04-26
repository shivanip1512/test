/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table lmprogramdirect add triggeroffset float;
update lmprogramdirect set triggeroffset=0.0;
alter table lmprogramdirect modify triggeroffset float not null;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */
