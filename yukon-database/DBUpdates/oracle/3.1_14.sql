/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table DynamicLMProgramDirect add ConstraintOverride char(1);
update DynamicLMProgramDirect set ConstraintOverride = 'N';
alter table DynamicLMProgramDirect modify ConstraintOverride not null;







/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '14-OCT-2005', 'Manual version insert done', 14);