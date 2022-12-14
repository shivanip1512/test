/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

insert into point select max(PointID)+1,'Analog','Porter Work Count',0,'Default',0,'N','N','R',1500,'None',0 from point;
insert into pointalarming select max(pointID), '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1, 0  from point;
insert into pointanalog select max(pointID), 0, 'None', 1, 0 from point;
insert into pointunit select max(pointID), 9, 1, 1.0E+30, -1.0E+30 from point;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '15-MAR-2005', 'Manual version insert done', 20);
