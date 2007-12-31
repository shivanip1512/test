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
   DeviceID             number              not null,
   CurrentGearNumber    number              not null,
   LastGroupControlled  number              not null,
   StartTime            date             not null,
   StopTime             date             not null,
   TimeStamp            date             not null,
   NotifyActiveTime     date             not null,
   StartedRampingOut    date             not null,
   NotifyInactiveTime   date             not null,
   ConstraintOverride   char(1)              not null
);

alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID);


alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID);

insert into yukonlistentry values (137, 100, 0, 'Mid Level Latch', 0);

insert into YukonRoleProperty values(-10810,-108, 'pop_up_appear_style','onmouseover', 'Style of the popups appearance when the user selects element in capcontrol.'); 

insert into YukonRoleProperty values(-10811,-108, 'inbound_voice_home_url', '/voice/inboundOptOut.jsp', 'Home URL for inbound voice logins');

/*@error ignore-begin */

alter table RAWPOINTHISTORY
   add constraint SYS_C0013322 primary key (CHANGEID);

create index Index_PointID on RAWPOINTHISTORY (
   POINTID ASC
);

create index Indx_TimeStamp on RAWPOINTHISTORY (
   TIMESTAMP ASC
);

create index Indx_RwPtHisPtIDTst on RAWPOINTHISTORY (
   POINTID ASC,
   TIMESTAMP ASC
);

alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID);

/*@error ignore-end */

create unique index INDX_SYSLG_PTID_TS on SYSTEMLOG (
   LOGID ASC,
   POINTID ASC,
   datetime ASC
);

create table EsubDisplayIndex (
   SearchKey            varchar2(500)         not null,
   DisplayUrl           varchar2(500)         not null
);


alter table EsubDisplayIndex
   add constraint PK_ESUBDISPLAYINDEX primary key  (SearchKey);

delete from display where displaynum = -2;

/*@error ignore-begin */
insert into YukonRoleProperty values(-20158,-201,'Disable Switch Sending','false','Disables the ability to send configs and connects/disconnects to switches.');
insert into yukongrouprole values (-758,-301,-201,-20158,'(none)');
insert into YukonGroupRole values (-1258,-2,-201,-20158,'(none)');
insert into yukongrouprole values (-2058,-303,-201,-20158,'(none)');
insert into YukonUserRole values (-758,-1,-201,-20158,'(none)');
/*@error ignore-end */

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'DBMaintainer', '10-MAY-2006', 'Manual version insert done', 4 );
