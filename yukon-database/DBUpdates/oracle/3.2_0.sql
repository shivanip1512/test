/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

delete from yukonuserrole where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;

insert into YukonRoleProperty values(-70005,-700,'Mapping Interface','amfm','Optional interface to the AMFM mapping system');
insert into YukonRoleProperty values(-70006,-700,'CBC Creation Name','CBC %PAOName%','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-70007,-700,'PFactor Decimal Places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-70008,-700,'CBC Allow OVUV','false','Allows users to toggle OV/UV usage on capbanks');

update yukongrouprole set rolepropertyid = -70005, roleid = -700 where rolepropertyid = -10105
update yukongrouprole set rolepropertyid = -70006, roleid = -700 where rolepropertyid = -10106;
update yukongrouprole set rolepropertyid = -70007, roleid = -700 where rolepropertyid = -10109;
update yukongrouprole set rolepropertyid = -70008, roleid = -700 where rolepropertyid = -10112;

delete from yukonroleproperty where rolepropertyid = -10105 or rolepropertyid = -10106 or rolepropertyid = -10106 or rolepropertyid = -10112;

update state set foregroundcolor = 10 where stategroupid = 3 and rawstate = 2;

insert into YukonRoleProperty values(-70009,-700,'CBC Refresh Rate','60','The rate, in seconds, all CBC clients reload data from the CBC server');

alter table yukonuser drop column logincount;
alter table yukonuser drop column lastlogin;
update contact set loginid = -9999 where loginid != -100 and loginid < 0;
insert into YukonListEntry values( 7, 1, 0, 'Voice PIN', 3 );

alter table CustomerAdditionalContact add Ordering smallint;
update CustomerAdditionalContact set Ordering = 0;
alter table CustomerAdditionalContact modify Ordering smallint not null;

alter table ContactNotification add Ordering smallint;
update ContactNotification set Ordering = 0;
alter table ContactNotification modify Ordering smallint not null;

create table ContactNotifGroupMap  (
   ContactID            NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              char(16)                        not null
);
alter table ContactNotifGroupMap
   add constraint PK_CONTACTNOTIFGROUPMAP primary key (ContactID, NotificationGroupID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID);
alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

create table CustomerNotifGroupMap  (
   CustomerID           NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              char(16)                        not null
);
alter table CustomerNotifGroupMap
   add constraint PK_CUSTOMERNOTIFGROUPMAP primary key (CustomerID, NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID);

alter table NotificationDestination add Attribs char(16);
update NotificationDestination set Attribs = '0000000000000000';
alter table NotificationDestination modify Attribs char(16) not null;

alter table NotificationGroup drop column emailsubject;
alter table NotificationGroup drop column emailfromaddress;
alter table NotificationGroup drop column emailmessage;
alter table NotificationGroup drop column numericpagermessage;

alter table CapBank modify OperationalState varchar2(16) not null;

update YukonRoleProperty set Description = 'The number of seconds to wait for a confirmation from the time the call is initiated until the user listens to the notification.' where RolePropertyID = -1402;
update YukonRoleProperty set Description = 'The number of seconds to wait for a call to be connected.' where RolePropertyID = -1401;

alter table LMProgramDirect rename column NotifyOffset to NotifyActiveOffset;

alter table LMProgramDirect add NotifyInactiveOffset number;
update LMProgramDirect set NotifyInactiveOffset = 0;
alter table LMProgramDirect modify NotifyInactiveOffset number not null;

alter table DynamicLMProgramDirect rename column NotifyTime to NotifyActiveTime;

alter table DynamicLMProgramDirect add NotifyInactiveTime date;
update DynamicLMProgramDirect set NotifyInactiveTime = '01-JAN-1990';
alter table DynamicLMProgramDirect modify NotifyInactiveTime date not null;

create table CICUSTOMERPOINTDATA  (
   CustomerID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   Type                 VARCHAR2(16)                    not null,
   OptionalLabel        VARCHAR2(32)                    not null
);
alter table CICUSTOMERPOINTDATA
   add constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, PointID);
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID);
alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID);

alter table Customer add CustomerNumber varchar2(64);
update Customer set CustomerNumber = '(none)';
alter table Customer modify CustomerNumber varchar2(64) not null;

create table PAOScheduleTable  (
   ScheduleID           NUMBER                          not null,
   NextRunTime          DATE                            not null,
   LastRunTime          DATE                            not null,
   IntervalRate         NUMBER                          not null
);
alter table PAOScheduleTable
   add constraint PK_PAOSCHEDULETABLE primary key (ScheduleID);

create table PAOScheduleAssignmentTable  (
   EventID              NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Command              VARCHAR2(128)                   not null
);
alter table PAOScheduleAssignmentTable
   add constraint PK_PAOSCHEDULEASSIGNMENTTABLE primary key (EventID, ScheduleID);
alter table PAOScheduleAssignmentTable
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOScheduleTable (ScheduleID);
alter table PAOScheduleAssignmentTable
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

INSERT into point  values (-6, 'System', 'Notifcation', 0, 'Default', 0, 'N', 'N', 'S', 6, 'None', 0);

insert into YukonRole values (-800,'Outbound Calling','IVR','Settings for Interactive Voice Response module');
insert into YukonRoleProperty values(-80000,-800,'Number of Channels','1','The number of outgoing channels assigned to the specified voice application.');
insert into YukonRoleProperty values(-80001,-800,'Template Root','http://localhost:8080/template/','A URL base where the notification templates will be stored (file: or http: are okay).');

update YukonGroupRole set roleid = -800 where rolepropertyid = -1400;
update YukonRoleProperty set roleid = -800 where rolepropertyid = -1400;
insert into YukonRoleProperty values(-1403,-5,'Call Prefix','(none)','Any number or numbers that must be dialed before a call can be placed.');



alter table dynamiccccapbank add assumedstartverificationstatus number;
update dynamiccccapbank set assumedstartverificationstatus = 0;
alter table dynamiccccapbank modify assumedstartverificationstatus number not null;

alter table dynamiccccapbank add prevverificationcontrolstatus number;
update dynamiccccapbank set prevverificationcontrolstatus = 0;
alter table dynamiccccapbank modify prevverificationcontrolstatus number not null;

alter table dynamiccccapbank add verificationcontrolindex number;
update dynamiccccapbank set verificationcontrolindex = 0;
alter table dynamiccccapbank modify verificationcontrolindex number not null;

alter table dynamiccccapbank add AdditionalFlags varchar2(32);
update dynamiccccapbank set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamiccccapbank modify AdditionalFlags varchar2(32) not null;

alter table dynamicccfeeder add AdditionalFlags varchar2(32);
update dynamicccfeeder set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccfeeder modify AdditionalFlags varchar2(32) not null;

alter table dynamicccsubstationbus add AdditionalFlags varchar2(32);
update dynamicccsubstationbus set AdditionalFlags = 'NNNNNNNNNNNNNNNNNNNN';
alter table dynamicccsubstationbus modify AdditionalFlags varchar2(32) not null;

alter table dynamicccsubstationbus add currVerifyCBId number;
update dynamicccsubstationbus set currVerifyCBId = -1;
alter table dynamicccsubstationbus modify currVerifyCBId number not null;

alter table dynamicccsubstationbus add currVerifyFeederId number;
update dynamicccsubstationbus set currVerifyFeederId = -1;
alter table dynamicccsubstationbus modify currVerifyFeederId number not null;

alter table dynamicccsubstationbus add currVerifyCBOrigState number;
update dynamicccsubstationbus set currVerifyCBOrigState = 0;
alter table dynamicccsubstationbus modify currVerifyCBOrigState number not null;

alter table dynamicccsubstationbus add verificationStrategy number;
update dynamicccsubstationbus set verificationStrategy = -1;
alter table dynamicccsubstationbus modify verificationStrategy number not null;

alter table dynamicccsubstationbus add cbInactivityTime number;
update dynamicccsubstationbus set cbInactivityTime = -1;
alter table dynamicccsubstationbus modify cbInactivityTime number not null;















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
