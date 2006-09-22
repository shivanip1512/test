/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table dynamiclmprogramdirect add additionalinfo varchar(80) default '(none)' not null;

insert into fdrinterfaceoption values(12,'DrivePath',2,'Text','(none)');
insert into fdrinterfaceoption values(12,'Filename',3,'Text','(none)');

alter table lmprogramdirectgear add KWReduction float default 0.0 not null;

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
