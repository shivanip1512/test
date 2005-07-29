/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

create unique index Indx_todsw_idoff on TOUDayRateSwitches (
   SwitchOffset ASC,
   TOUDayID ASC
);

/* @error ignore */
insert into YukonGroupRole values(-130,-100,-101,-10111,'(none)');











/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '29-JUL-2005', 'Manual version insert done', 10);