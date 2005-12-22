/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

update command set category = 'All MCT-4xx Series' where commandid = -81;
update command set category = 'All MCT-4xx Series' where commandid = -111;
update command set command = 'putvalue powerfail reset' where commandid = -109;
go

INSERT INTO DEVICETYPECOMMAND VALUES (-423, -111, 'MCT-470', 16, 'Y', -1);
go

create table SettlementConfig (
   ConfigID             numeric              not null,
   FieldName            varchar(64)          not null,
   FieldValue           varchar(64)          not null,
   CTISettlement        varchar(32)          not null
);
go
alter table SettlementConfig
   add constraint PK_SETTLEMENTCONFIG primary key  (ConfigID);
go

insert into YukonRoleProperty values(-20890,-201,'Address State Label','State','Labelling for the address field which is usually state in the US or province in Canada.');
insert into YukonRoleProperty values(-20891,-201,'Address County Label','County','Labelling for the address field which is usually county in the US.');
insert into YukonRoleProperty values(-20892,-201,'Address PostalCode Label','Zip','Labelling for the address field which is usually zip code in the US or postal code in Canada.');
insert into YukonRoleProperty values(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
go

insert into yukongrouprole values (-697,-300,-400,-40197,'false');
insert into yukongrouprole values (-890,-301,-201,-20890,'(none)');
insert into yukongrouprole values (-891,-301,-201,-20891,'(none)');
insert into yukongrouprole values (-892,-301,-201,-20892,'(none)');
insert into yukongrouprole values (-2190,-303,-201,-20890,'(none)');
insert into yukongrouprole values (-2191,-303,-201,-20891,'(none)');
insert into yukongrouprole values (-2192,-303,-201,-20892,'(none)');
insert into yukongrouprole values (-2397,-304,-400,-40197,'false');
go

update LMProgramDirectGear set ControlMethod = 'ThermostatRamping' where ControlMethod = 'ThermostatSetback';
go


insert into YukonRoleProperty values(-1110,-2,'Default Temperature Unit','F','Default temperature unit for an energy company, F(ahrenheit) or C(elsius)');
go

alter table Customer add TemperatureUnit char(1);
go
update Customer set TemperatureUnit = 'F';
go
alter table Customer alter column TemperatureUnit char(1) not null;
go

insert into YukonListEntry values (134, 100, 0, 'True,False,Condition', 0);
insert into YukonListEntry values (135, 100, 0, 'Regression', 0);
go

/* @error ignore */
insert into YukonRoleProperty values(-10305,-103,'Commands Group Name','Default Commands','The commands group name for the displayed commands.');
/* @error ignore */
insert into YukonGroupRole values(-175,-100,-103,-10305,'(none)');
/* @error ignore */
insert into YukonGroupRole values(-1075,-2,-103,-10305,'(none)');
/* @error ignore */
insert into YukonUserRole values(-175,-1,-103,-10305,'(none)');


/* Steps for adding a new role with some new RoleRoperties and assigning it existing RoleProperties from an old role */
/* 1: insert the new role */
insert into YukonRole values(-900,'Direct Loadcontrol','Load Control','Access and usage of direct loadcontrol system');
go

/* 2: Update all refrences of the old roleID to the new role ID, then delete the old role */
update YukonRoleProperty set roleid = -900 where roleid = -203;
update yukongrouprole set roleid = -900 where roleid = -203;
update yukonuserrole set roleid = -900 where roleid = -203;
delete from YukonRole where roleid = -203;
go

/* 3: Add all RolePoperties to the new role, including the RoleProperties we are moving */
insert into YukonRoleProperty values(-90000,-900,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
insert into YukonRoleProperty values(-90001,-900,'Individual Switch','true','Controls access to operator individual switch control');
insert into YukonRoleProperty values(-90002,-900,'3 Tier Direct Control','false','Allows access to the 3-tier load management web interface');
insert into YukonRoleProperty values(-90003,-900,'Direct Loadcontrol','true','Allows access to the Direct load management web interface');
insert into YukonRoleProperty values(-90004,-900,'Constraint Check','true','Allow load management program constraints to be CHECKED before starting');
insert into YukonRoleProperty values(-90005,-900,'Constraint Observe','true','Allow load management program constraints to be OBSERVED before starting');
insert into YukonRoleProperty values(-90006,-900,'Constraint Override','true','Allow load management program constraints to be OVERRIDDEN before starting');
insert into YukonRoleProperty values(-90007,-900,'Constraint Default','Check','The default program constraint selection prior to starting a program');
go

/* 4: Update the old RoleProperties ID to the duplicate RoleProperties we have just made */
update yukongrouprole set rolepropertyid = -90000 where rolepropertyid = -20300;
update yukongrouprole set rolepropertyid = -90001 where rolepropertyid = -20301;
update yukongrouprole set rolepropertyid = -90002 where rolepropertyid = -20302;
update yukongrouprole set rolepropertyid = -90003 where rolepropertyid = -20303;
go

update yukonuserrole set rolepropertyid = -90000 where rolepropertyid = -20300;
update yukonuserrole set rolepropertyid = -90001 where rolepropertyid = -20301;
update yukonuserrole set rolepropertyid = -90002 where rolepropertyid = -20302;
update yukonuserrole set rolepropertyid = -90003 where rolepropertyid = -20303;
go

/* 5: Delete the old RolePropertie */
delete from YukonRoleProperty where rolepropertyid = -20300;
delete from YukonRoleProperty where rolepropertyid = -20301;
delete from YukonRoleProperty where rolepropertyid = -20302;
delete from YukonRoleProperty where rolepropertyid = -20303;
go

/* 6: Add only the new RoleProperties to the UserRole and GroupRole mapping */
insert into yukongrouprole values (-779,-301,-900,-90004,'(none)');
insert into yukongrouprole values (-781,-301,-900,-90005,'(none)');
insert into yukongrouprole values (-782,-301,-900,-90006,'(none)');
insert into yukongrouprole values (-783,-301,-900,-90007,'(none)');
insert into YukonGroupRole values (-1279,-2,-900,-90004,'(none)');
insert into YukonGroupRole values (-1281,-2,-900,-90005,'(none)');
insert into YukonGroupRole values (-1282,-2,-900,-90006,'(none)');
insert into YukonGroupRole values (-1283,-2,-900,-90007,'(none)');
go

insert into YukonUserRole values (-779,-1,-900,-90004,'(none)');
insert into YukonUserRole values (-781,-1,-900,-90005,'(none)');
insert into YukonUserRole values (-782,-1,-900,-90006,'(none)');
insert into YukonUserRole values (-783,-1,-900,-90007,'(none)');
go

insert into YukonRoleProperty values(-20158,-201,'Disable Switch Sending','false','Disables the ability to send configs and connects/disconnects to switches.');
go

alter table LMProgramDirectGear add FrontRampOption varchar(80);
go
update LMProgramDirectGear set FrontRampOption = '(none)';
go
alter table LMProgramDirectGear alter column FrontRampOption varchar(80) not null;
go

alter table LMProgramDirectGear add FrontRampTime numeric;
go
update LMProgramDirectGear set FrontRampTime = 0;
go
alter table LMProgramDirectGear alter column FrontRampTime numeric not null;
go

alter table LMProgramDirectGear add BackRampOption varchar(80);
go
update LMProgramDirectGear set BackRampOption = '(none)';
go
alter table LMProgramDirectGear alter column BackRampOption varchar(80) not null;
go

alter table LMProgramDirectGear add BackRampTime numeric;
go
update LMProgramDirectGear set BackRampTime = 0;
go
alter table LMProgramDirectGear alter column BackRampTime numeric not null;
go










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