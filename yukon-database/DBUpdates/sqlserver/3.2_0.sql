/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

delete from yukonuserrole where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;
go
insert into YukonRoleProperty values(-70005,-700,'Mapping Interface','amfm','Optional interface to the AMFM mapping system');
insert into YukonRoleProperty values(-70006,-700,'CBC Creation Name','CBC %PAOName%','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-70007,-700,'PFactor Decimal Places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-70008,-700,'CBC Allow OVUV','false','Allows users to toggle OV/UV usage on capbanks');
go

update yukongrouprole set rolepropertyid = -70005, roleid = -700 where rolepropertyid = -10105
update yukongrouprole set rolepropertyid = -70006, roleid = -700 where rolepropertyid = -10106;
update yukongrouprole set rolepropertyid = -70007, roleid = -700 where rolepropertyid = -10109;
update yukongrouprole set rolepropertyid = -70008, roleid = -700 where rolepropertyid = -10112;
go

delete from yukonroleproperty where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;
go

update state set foregroundcolor = 10 where stategroupid = 3 and rawstate = 2;

insert into YukonRoleProperty values(-70009,-700,'CBC Refresh Rate','60','The rate, in seconds, all CBC clients reload data from the CBC server');
go

alter table yukonuser drop column logincount;
alter table yukonuser drop column lastlogin;
update contact set loginid = -9999 where loginid != -100 and loginid < 0;
insert into YukonListEntry values( 7, 1, 0, 'Voice PIN', 3 );
go

alter table CustomerAdditionalContact add Ordering smallint;
update CustomerAdditionalContact set Ordering = 0;
alter table CustomerAdditionalContact alter column Ordering smallint not null;
go

alter table ContactNotification add Ordering smallint;
update ContactNotification set Ordering = 0;
alter table ContactNotification alter column Ordering smallint not null;
go

create table ContactNotifGroupMap (
   ContactID            numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null
);
alter table ContactNotifGroupMap
   add constraint PK_CONTACTNOTIFGROUPMAP primary key  (ContactID, NotificationGroupID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go

create table CustomerNotifGroupMap (
   CustomerID           numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null
);
alter table CustomerNotifGroupMap
   add constraint PK_CUSTOMERNOTIFGROUPMAP primary key  (CustomerID, NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID);
go

alter table NotificationDestination add Attribs char(16);
go
update NotificationDestination set Attribs = '0000000000000000';
go
alter table NotificationDestination alter column Attribs char(16) not null;
go


alter table NotificationGroup drop column emailsubject;
go
alter table NotificationGroup drop column emailfromaddress;
go
alter table NotificationGroup drop column emailmessage;
go
alter table NotificationGroup drop column numericpagermessage;
go

alter table CapBank alter column OperationalState varchar(16) not null;
 
update YukonRoleProperty set Description = 'The number of seconds to wait for a confirmation from the time the call is initiated until the user listens to the notification.' where RolePropertyID = -1402;
update YukonRoleProperty set Description = 'The number of seconds to wait for a call to be connected.' where RolePropertyID = -1401;
go


sp_rename 'LMProgramDirect.NotifyOffset', 'NotifyActiveOffset', 'COLUMN';
go
alter table LMProgramDirect add NotifyInactiveOffset numeric;
go
update LMProgramDirect set NotifyInactiveOffset=0;
go
alter table LMProgramDirect alter column NotifyInactiveOffset numeric not null;
go

sp_rename 'DynamicLMProgramDirect.NotifyTime', 'NotifyActiveTime', 'COLUMN';
go
alter table DynamicLMProgramDirect add NotifyInactiveTime datetime;
go
update DynamicLMProgramDirect set NotifyInactiveTime = '01-JAN-1990';
go
alter table DynamicLMProgramDirect alter column NotifyInactiveTime datetime not null;
go


create table CICUSTOMERPOINTDATA (
   CustomerID           numeric              not null,
   PointID              numeric              not null,
   Type                 varchar(16)          not null,
   OptionalLabel        varchar(32)          not null
);
go
alter table CICUSTOMERPOINTDATA
   add constraint PK_CICUSTOMERPOINTDATA primary key  (CustomerID, PointID);
go
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID);
go

alter table Customer add CustomerNumber varchar(64);
go
update Customer set CustomerNumber = '(none)';
go
alter table Customer alter column CustomerNumber varchar(64) not null;
go


create table PAOScheduleTable (
   ScheduleID           numeric              not null,
   NextRunTime          datetime             not null,
   LastRunTime          datetime             not null,
   IntervalRate         numeric              not null
);
go
alter table PAOScheduleTable
   add constraint PK_PAOSCHEDULETABLE primary key  (ScheduleID);
go

create table PAOScheduleAssignmentTable (
   EventID              numeric              not null,
   ScheduleID           numeric              not null,
   PaoID                numeric              not null,
   Command              varchar(128)         not null
);
go
alter table PAOScheduleAssignmentTable
   add constraint PK_PAOSCHEDULEASSIGNMENTTABLE primary key  (EventID, ScheduleID);
go
alter table PAOScheduleAssignmentTable
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOScheduleTable (ScheduleID);
go
alter table PAOScheduleAssignmentTable
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go

INSERT into point  values (-6, 'System', 'Notifcation', 0, 'Default', 0, 'N', 'N', 'S', 6, 'None', 0);
go

insert into YukonRole values (-800,'Outbound Calling','IVR','Settings for Interactive Voice Response module');
insert into YukonRoleProperty values(-80000,-800,'Number of Channels','1','The number of outgoing channels assigned to the specified voice application.');
insert into YukonRoleProperty values(-80001,-800,'Template Root','http://localhost:8080/template/','A URL base where the notification templates will be stored (file: or http: are okay).');
go

update YukonGroupRole set roleid = -800 where rolepropertyid = -1400;
update YukonRoleProperty set roleid = -800 where rolepropertyid = -1400;
insert into YukonRoleProperty values(-1403,-5,'Call Prefix','(none)','Any number or numbers that must be dialed before a call can be placed.');
go

alter table dynamiccccapbank add assumedstartverificationstatus numeric;
update dynamiccccapbank set assumedstartverificationstatus = 0;
alter table dynamiccccapbank alter column assumedstartverificationstatus numeric not null;
go

alter table dynamiccccapbank add prevverificationcontrolstatus numeric;
update dynamiccccapbank set prevverificationcontrolstatus = 0;
alter table dynamiccccapbank alter column prevverificationcontrolstatus numeric not null;
go

alter table dynamiccccapbank add verificationcontrolindex numeric;
update dynamiccccapbank set verificationcontrolindex = 0;
alter table dynamiccccapbank alter column verificationcontrolindex numeric not null;
gp

alter table dynamiccccapbank add AdditionalFlags varchar(32);
update dynamiccccapbank set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamiccccapbank alter column AdditionalFlags varchar(32) not null;
go

alter table dynamicccfeeder add AdditionalFlags varchar(32);
update dynamicccfeeder set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccfeeder alter column AdditionalFlags varchar(32) not null;
go

alter table dynamicccsubstationbus add AdditionalFlags varchar(32);
update dynamicccsubstationbus set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccsubstationbus alter column AdditionalFlags varchar(32) not null;
go

alter table dynamicccsubstationbus add currVerifyCBId numeric;
update dynamicccsubstationbus set currVerifyCBId = -1;
alter table dynamicccsubstationbus alter column currVerifyCBId numeric not null;
go

alter table dynamicccsubstationbus add currVerifyFeederId numeric;
update dynamicccsubstationbus set currVerifyFeederId = -1;
alter table dynamicccsubstationbus alter column currVerifyFeederId numeric not null;
go

alter table dynamicccsubstationbus add currVerifyCBOrigState numeric;
update dynamicccsubstationbus set currVerifyCBOrigState = 0;
alter table dynamicccsubstationbus alter column currVerifyCBOrigState numeric not null;
go

alter table dynamicccsubstationbus add verificationStrategy numeric;
update dynamicccsubstationbus set verificationStrategy = -1;
alter table dynamicccsubstationbus alter column verificationStrategy numeric not null;
go

alter table dynamicccsubstationbus add cbInactivityTime numeric;
update dynamicccsubstationbus set cbInactivityTime = -1;
alter table dynamicccsubstationbus alter column cbInactivityTime numeric not null;
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
