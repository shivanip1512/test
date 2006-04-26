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

insert into YukonRoleProperty values(-80100,-801,'Template Root','http://localhost:8080/WebConfig/custom/notif_templates/','A URL base where the notification templates will be stored (file: or http: are okay).');

go
update YukonGroupRole set RoleId = -801, RolePropertyId = -80100 where RolePropertyId = -80002;
go
update YukonUserRole set RoleId = -801, RolePropertyId = -80100 where RolePropertyId = -80002;
go

delete from YukonRoleProperty where RolePropertyId = -80002;
go

insert into YukonRoleProperty values(-10809,-108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');
go

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
go


alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID);
go


alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
go

insert into yukonlistentry values (137, 100, 0, 'Mid Level Latch', 0);
go

insert into YukonRoleProperty values(-10810,-108, 'pop_up_appear_style','onmouseover', 'Style of the popups appearance when the user selects element in capcontrol.');
go

/*@error ignore-begin */

alter table RAWPOINTHISTORY
   add constraint SYS_C0013322 primary key (CHANGEID);
go

create index Index_PointID on RAWPOINTHISTORY (
   POINTID ASC
);
go

create index Indx_TimeStamp on RAWPOINTHISTORY (
   TIMESTAMP ASC
);
go

create index Indx_RwPtHisPtIDTst on RAWPOINTHISTORY (
   POINTID ASC,
   TIMESTAMP ASC
);
go

alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID);
go
/*@error ignore-end */

create unique index INDX_SYSLG_PTID_TS on SYSTEMLOG (
   LOGID ASC,
   POINTID ASC,
   DATETIME ASC
);
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
