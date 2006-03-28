/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

/* @error ignore */
alter table CCMonitorBankList rename column UpperBandwith to UpperBandwidth;

/* @error ignore */
alter table CCMonitorBankList rename column LowerBandwith to LowerBandwidth;

update YukonRole set RoleName = 'IVR', Category = 'Notifications' where RoleID = -800;
insert into YukonRole values (-801, 'Configuration', 'Notifications', 'Configuration for Notification Server (voice and email)');

insert into YukonRoleProperty values(-80100,-801,'Template Root','http://localhost:8080/WebConfig/custom/notif_templates/','A URL base where the notification templates will be stored (file: or http: are okay).');

update YukonGroupRole set RoleId = -801, RolePropertyId = -80100 where RolePropertyId = -80002;
update YukonUserRole set RoleId = -801, RolePropertyId = -80100 where RolePropertyId = -80002;
 
delete from YukonRoleProperty where RolePropertyId = -80002;

insert into YukonRoleProperty values(-10809,-108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');

drop table dynamiclmprogramdirect;

create table DynamicLMProgramDirect (
   DeviceID             numeric              not null,
   CurrentGearNumber    numeric              not null,
   LastGroupControlled  numeric              not null,
   StartTime            datetime             not null,
   StopTime             datetime             not null,
   TimeStamp            datetime             not null,
   NotifyActiveTime     datetime             not null,
   StartedRampingOut    datetime             not null,
   NotifyInactiveTime   datetime             not null,
   ConstraintOverride   char(1)              not null
);

alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID);


alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
 
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
