drop table usergraph
go
drop table userweb
go
drop table graphdataseries
go
drop table graphdefinition
go
drop table userinfo
go


create table USERINFO (
USERID               numeric                        not null,
USERNAME             varchar(20)                    not null
	
	
	constraint SYS_C0015103 check ("USERNAME" IS NOT NULL),
PASSWORD             varchar(20)                    not null
	
	
	constraint SYS_C0015104 check ("PASSWORD" IS NOT NULL),
DATABASEALIAS        varchar(20)                    not null
	
	
	constraint SYS_C0015105 check ("DATABASEALIAS" IS NOT NULL),
LOGINCOUNT           numeric                        not null,
LASTLOGIN            numeric                        not null,
constraint SYS_C0015106 primary key  (USERID)
)
go


create table USERWEB (
USERID               numeric                        not null
	
	
	constraint SYS_C0015125 check ("USERID" IS NOT NULL),
HOMEURL              varchar(200)                   null,
LOGO                 varchar(40)                    null
)
go
alter table USERWEB
   add constraint SYS_C0015126 foreign key (USERID)
      references USERINFO (USERID)
go


create table GRAPHDEFINITION (
GRAPHDEFINITIONID    numeric                        not null,
NAME                 varchar(40)                    not null
	
	
	constraint SYS_C0015108 check ("NAME" IS NOT NULL),
AutoScaleTimeAxis    char(1)                        not null,
AutoScaleLeftAxis    char(1)                        not null,
AutoScaleRightAxis   char(1)                        not null,
STARTDATE            datetime                       not null,
STOPDATE             datetime                       not null,
LeftMin              float                          not null,
LeftMax              float                          not null,
RightMin             float                          not null,
RightMax             float                          not null,
Type                 char(1)                        not null,
constraint SYS_C0015109 primary key  (GRAPHDEFINITIONID)
)
go


create table GRAPHDATASERIES (
GRAPHDATASERIESID    numeric                        not null,
GRAPHDEFINITIONID    numeric                        not null
	
	
	constraint SYS_C0015111 check ("GRAPHDEFINITIONID" IS NOT NULL),
POINTID              numeric                        not null
	
	
	constraint SYS_C0015112 check ("POINTID" IS NOT NULL),
Label                varchar(40)                    not null,
Axis                 char(1)                        not null,
Color                numeric                        not null,
constraint SYS_C0015113 primary key  (GRAPHDATASERIESID)
)
go
alter table GRAPHDATASERIES
   add constraint SYS_C0015114 foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go
alter table GRAPHDATASERIES
   add constraint SYS_C0015115 foreign key (POINTID)
      references POINT (POINTID)
go


create table UserGraph (
UserID               numeric                        not null,
GraphDefinitionID    numeric                        not null
)
go
alter table UserGraph
   add constraint FK_USERGRAP_USERGRAPH_USERINFO foreign key (UserID)
      references USERINFO (USERID)
go
alter table UserGraph
   add constraint FK_USERGRAP_USERGRAPH_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go





/************************/
/* DBEditor Updates 	*/
/************************/
alter table pointunit add DECIMALPLACES NUMERIC not null default 3
go
alter table pointlimits add ALARMDURATION NUMERIC not null default 0
go


create table GroupRecipient (
LocationID           numeric                        not null,
LocationName         varchar(30)                    not null,
EmailAddress         varchar(60)                    not null,
EmailSendType        numeric                        not null,
PagerNumber          varchar(20)                    not null,
DisableFlag          char(1)                        not null,
constraint PK_GROUPRECIPIENT primary key  (LocationID)
)
go
insert into GroupRecipient values(0,'None','None',1,'None','N');


create table NotificationGroup (
NotificationGroupID  numeric                        not null,
GroupName            varchar(40)                    not null,
EmailSubject         varchar(30)                    not null,
EmailFromAddress     varchar(40)                    not null,
EmailMessage         varchar(60)                    not null,
NumericPagerMessage  varchar(14)                    not null,
DisableFlag          char(1)                        not null,
constraint PK_NOTIFICATIONGROUP primary key  (NotificationGroupID)
)
go
insert into notificationgroup values(1,'None','None','None','None','None','N');
insert into notificationgroup values(2,'Alarm Group 1','Alarm Group 1 Subject','Alarm Group 1 From Address','Alarm Group 1 Message','None','N');
insert into notificationgroup values(3,'Alarm Group 2','Alarm Group 2 Subject','Alarm Group 2 From Address','Alarm Group 2 Message','None','N');
insert into notificationgroup values(4,'Alarm Group 3','Alarm Group 3 Subject','Alarm Group 3 From Address','Alarm Group 3 Message','None','N');
insert into notificationgroup values(5,'Alarm Group 4','Alarm Group 4 Subject','Alarm Group 4 From Address','Alarm Group 4 Message','None','N');
insert into notificationgroup values(6,'Alarm Group 5','Alarm Group 5 Subject','Alarm Group 5 From Address','Alarm Group 5 Message','None','N');
insert into notificationgroup values(7,'Alarm Group 6','Alarm Group 6 Subject','Alarm Group 6 From Address','Alarm Group 6 Message','None','N');
insert into notificationgroup values(8,'Alarm Group 7','Alarm Group 7 Subject','Alarm Group 7 From Address','Alarm Group 7 Message','None','N');
insert into notificationgroup values(9,'Alarm Group 8','Alarm Group 8 Subject','Alarm Group 8 From Address','Alarm Group 8 Message','None','N');
insert into notificationgroup values(10,'Alarm Group 9','Alarm Group 9 Subject','Alarm Group 9 From Address','Alarm Group 9 Message','None','N');
insert into notificationgroup values(11,'Alarm Group 10','Alarm Group 10 Subject','Alarm Group 10 From Address','Alarm Group 10 Message','None','N');
create unique  index Unique_GroupName on NotificationGroup (GroupName)
go



create table PointAlarming (
PointID              numeric                        not null,
AlarmStates          varchar(32)                    not null,
ExcludeNotifyStates  varchar(32)                    not null,
NotifyOnAcknowledge  char(1)                        not null,
NotificationGroupID  numeric                        not null,
LocationID           numeric                        not null,
constraint PK_POINTALARMING primary key  (PointID)
)
go
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, locationid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;
alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
go
alter table PointAlarming
   add constraint FK_NOTIFICATIONGRPOUP_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go
alter table PointAlarming
   add constraint FK_POINTALA_POINTALAR_GROUPREC foreign key (LocationID)
      references GroupRecipient (LocationID)
go


create table NotificationDestination (
DestinationOrder     numeric                        not null,
NotificationGroupID  numeric                        not null,
LocationID           numeric                        not null,
constraint PK_NOTIFICATIONDESTINATION primary key  (NotificationGroupID, DestinationOrder)
)
go
alter table NotificationDestination
   add constraint FK_NOTIFICAGROUP_NOTIFICATDEST foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go
alter table NotificationDestination
   add constraint FK_NOTIFICA_REFERENCE_GROUPREC foreign key (LocationID)
      references GroupRecipient (LocationID)
go

/**************************/
/*  TDC updates  **********/
/**************************/
update display set type='Load Control Client' where displaynum=-3;
update display set type='Cap Control Client' where displaynum=-2;
update display set type='Scheduler Client' where displaynum=-1;
update display set type='Alarms and Events' where displaynum >= 1 and displaynum <=99;
update display set type='Custom Displays' where displaynum >= 100;
delete from displaycolumns where displaynum=99;
delete from display where displaynum = 99;
insert into display values(99, 'Basic User Created', 'Custom Displays', 'A Predefined Generic Display', 'This display is is used to show what a user created display looks like.');
insert into displaycolumns values(99, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(99, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(99, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(99, 'Value', 9, 4, 160 );
go