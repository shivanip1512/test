/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* @error ignore */
create table CommandGroup (
   CommandGroupID       numeric              not null,
   CommandGroupName     varchar(60)          not null
)
go

/* @error ignore */
insert into CommandGroup values (-1, 'Default Commands');

/* @error ignore */
alter table CommandGroup
   add constraint PK_COMMANDGROUP primary key  (CommandGroupID)
go

/* @error ignore */
alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID)
go

/* @error ignore */
insert into YukonRoleProperty values(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
go

/*@error ignore-begin */
/* This was done in the 3.2.1 update script, but the creation script failed reflect these changes, do it again */
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
/*@error ignore-end */

/* @error ignore-begin */
insert into YukonRoleProperty values(-1110,-2,'Default Temperature Unit','F','Default temperature unit for an energy company, F(ahrenheit) or C(elsius)');
go

alter table Customer add TemperatureUnit char(1);
go
update Customer set TemperatureUnit = 'F';
go
alter table Customer alter column TemperatureUnit char(1) not null;
go
/* @error ignore-end */

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
