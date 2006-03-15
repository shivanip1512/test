/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* @error ignore */
sp_rename 'CCMonitorBankList.UpperBandwith', 'UpperBandwidth', 'COLUMN';
go

/* @error ignore */
sp_rename 'CCMonitorBankList.LowerBandwith', 'LowerBandwidth', 'COLUMN';
go

update YukonRole set RoleName = 'IVR', Category = 'Notifications' where RoleID = -800;
go
insert into YukonRole values (-801, 'Configuration', 'Notifications', 'Configuration for Notification Server (voice and email)');
go

insert into YukonRoleProperty values(-80100,-801,'Template Root','http://localhost:8080/template/','A URL base where the notification templates will be stored (file: or http: are okay).');
go
update YukonGroupRole set RoleId = -801, RolePropertyId = -80100 where RolePropertyId = -80002;
go
update YukonUserRole set RoleId = -801, RolePropertyId = -80100 where RolePropertyId = -80002;
go

delete from YukonRoleProperty where RolePropertyId = -80002;
go

insert into YukonRoleProperty values(-10809,-108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');
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
/* __YUKON_VERSION__ */
