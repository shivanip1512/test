/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/
alter table dynamiccccapbank add twowaycbcstate number;
update dynamiccccapbank set twowaycbcstate = -1;
alter table dynamiccccapbank modify twowaycbcstate number not null;

alter table dynamiccccapbank add twowaycbcstatetime date;
update dynamiccccapbank set twowaycbcstatetime = '01-JAN-1990';
alter table dynamiccccapbank modify twowaycbcstatetime date not null;


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '04-Apr-2007', 'Latest Update', 14 );