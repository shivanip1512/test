/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/
update command set category = 'All MCT-4xx Series' where commandid = -81;
update command set category = 'All MCT-4xx Series' where commandid = -111;
update command set command = 'putvalue powerfail reset' where commandid = -109;

create table SettlementConfig (
   ConfigID             NUMBER                not null,
   FieldName            varchar2(64)          not null,
   FieldValue           varchar2(64)          not null,
   CTISettlement        varchar2(32)          not null
);
alter table SettlementConfig
   add constraint PK_SETTLEMENTCONFIG primary key (ConfigID);

insert into YukonRoleProperty values(-20890,-201,'Address State Label','State','Labelling for the address field which is usually state in the US or province in Canada.');
insert into YukonRoleProperty values(-20891,-201,'Address County Label','County','Labelling for the address field which is usually county in the US.');
insert into YukonRoleProperty values(-20892,-201,'Address PostalCode Label','Zip','Labelling for the address field which is usually zip code in the US or postal code in Canada.');
insert into YukonRoleProperty values(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');

insert into yukongrouprole values (-697,-300,-400,-40197,'false');
insert into yukongrouprole values (-890,-301,-201,-20890,'(none)');
insert into yukongrouprole values (-891,-301,-201,-20891,'(none)');
insert into yukongrouprole values (-892,-301,-201,-20892,'(none)');
insert into yukongrouprole values (-2190,-303,-201,-20890,'(none)');
insert into yukongrouprole values (-2191,-303,-201,-20891,'(none)');
insert into yukongrouprole values (-2192,-303,-201,-20892,'(none)');
insert into yukongrouprole values (-2397,-304,-400,-40197,'false');

update LMProgramDirectGear set ControlMethod = 'ThermostatRamping' where ControlMethod = 'ThermostatSetback';

insert into YukonRoleProperty values(-1110,-2,'Default Temperature Unit','F','Default temperature unit for an energy company, F(ahrenheit) or C(elsius)');

alter table Customer add TemperatureUnit char(1);
update Customer set TemperatureUnit = 'F';
alter table Customer modify TemperatureUnit not null;

insert into YukonListEntry values (134, 100, 0, 'True,False,Condition', 0);
insert into YukonListEntry values (135, 100, 0, 'Regression', 0);

/* @error ignore */
insert into YukonRoleProperty values(-10305,-103,'Commands Group Name','Default Commands','The commands group name for the displayed commands.');
/* @error ignore */
insert into YukonGroupRole values(-175,-100,-103,-10305,'(none)');
/* @error ignore */
insert into YukonGroupRole values(-1075,-2,-103,-10305,'(none)');
/* @error ignore */
insert into YukonUserRole values(-175,-1,-103,-10305,'(none)');










/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Ryan', '28-Oct-2005', 'Manual version insert done', 1 );