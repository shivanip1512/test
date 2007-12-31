/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table dynamiclmprogramdirect add additionalinfo varchar2(80) default '(none)' not null;

insert into fdrinterfaceoption values(12,'DrivePath',2,'Text','(none)');
insert into fdrinterfaceoption values(12,'Filename',3,'Text','(none)');

alter table lmprogramdirectgear add KWReduction float default 0.0 not null;

alter table capcontrolstrategy add peakVARlag float;
update capcontrolstrategy set peakVARlag = 0;
alter table capcontrolstrategy modify peakVARlag float not null;

alter table capcontrolstrategy add peakVARlead float;
update capcontrolstrategy set peakVARlead = 0;
alter table capcontrolstrategy modify peakVARlead float not null;

alter table capcontrolstrategy add offPkVARlag float;
update capcontrolstrategy set offPkVARlag = 0;
alter table capcontrolstrategy modify offPkVARlag float not null;

alter table capcontrolstrategy add offPkVARlead float;
update capcontrolstrategy set offPkVARlead = 0;
alter table capcontrolstrategy modify offPkVARlead float not null;

alter table capcontrolstrategy add peakPFSetPoint float;
update capcontrolstrategy set peakPFSetPoint = 100;
alter table capcontrolstrategy modify peakPFSetPoint float not null;

alter table capcontrolstrategy add offPkPFSetPoint float;
update capcontrolstrategy set offPkPFSetPoint = 100;
alter table capcontrolstrategy modify offPkPFSetPoint float not null;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Jon', '30-OCT-2006', 'DB Update Script', 10 );
