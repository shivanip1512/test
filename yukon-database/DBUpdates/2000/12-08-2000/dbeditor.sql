/******************************************************/
/***** CHANGE ALL STATE RAWVALUES 		 ******/
/******************************************************/
update state set rawstate=(rawstate-1);
update pointstatus set normalstate=(normalstate-1), alarmstate=(alarmstate-1),initialstate=(initialstate-1);
insert into state(stategroupid, rawstate, text, foregroundcolor, backgroundcolor)
	select unique stategroupid,
	-1,
	'Any',
	6,
	2 from state where stategroupid > 0;

alter table pointunit add DECIMALPLACES NUMBER;
update pointunit set DECIMALPLACES=3;
alter table pointunit modify DECIMALPLACES not null;
/* SQL SERVER */
/*alter table pointunit add DECIMALPLACES NUMERIC not null default 3;*/

alter table pointlimits add LIMITDURATION NUMBER;
update pointlimits set LIMITDURATION=0;
alter table pointlimits modify LIMITDURATION not null;
/* SQL SERVER */
/*alter table pointlimits add LIMITDURATION NUMERIC not null default 0;*/

create table NotificationGroup (
   NotificationGroupID  NUMBER                           not null,
   GroupName            VARCHAR2(40)                     not null,
   EmailSubject         VARCHAR2(30)                     not null,
   EmailFromAddress     VARCHAR2(40)                     not null,
   EmailMessage         VARCHAR2(160)                     not null,
   NumericPagerMessage  VARCHAR2(14)                     not null,
   DisableFlag          CHAR(1)                          not null,
   constraint PK_NOTIFICATIONGROUP primary key (NotificationGroupID)
);
create unique index Unique_GroupName on NotificationGroup (
   GroupName ASC
);

create table GroupRecipient (
   LocationID           NUMBER                           not null,
   LocationName         VARCHAR(30)                      not null,
   EmailAddress         VARCHAR(60)                      not null,
   EmailSendType        NUMBER                           not null,
   PagerNumber          VARCHAR(20)                      not null,
   DisableFlag          CHAR(1)                          not null,
   constraint PK_GROUPRECIPIENT primary key (LocationID)
);

create table PointAlarming (
   PointID              NUMBER                           not null,
   AlarmStates          VARCHAR2(32)                     not null,
   ExcludeNotifyStates  VARCHAR2(32)                     not null,
   NotifyOnAcknowledge  CHAR(1)                          not null,
   NotificationGroupID  NUMBER                           not null,
   LocationID           NUMBER,
   constraint PK_POINTALARMING primary key (PointID),
   constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
         references POINT (POINTID),
   constraint FK_NOTIFICATIONGRP_POINTALARM foreign key (NotificationGroupID)
         references NotificationGroup (NotificationGroupID),
   constraint FK_POINTALA_POINTALAR_GROUPREC foreign key (LocationID)
         references GroupRecipient (LocationID)
);

create table NotificationDestination (
   DestinationOrder     NUMBER                           not null,
   NotificationGroupID  NUMBER                           not null,
   LocationID           NUMBER,
   constraint PK_NOTIFICATIONDESTINATION primary key (NotificationGroupID, DestinationOrder),
   constraint FK_NOTIFICAGROUP_NOTIFICATDEST foreign key (NotificationGroupID)
         references NotificationGroup (NotificationGroupID),
   constraint FK_NOTIFICA_REFERENCE_GROUPREC foreign key (LocationID)
         references GroupRecipient (LocationID)
);

create table AlarmState (
   AlarmStateID         NUMBER                           not null,
   AlarmStateName       VARCHAR2(40)                     not null,
   NotificationGroupID  NUMBER                           not null,
   constraint PK_ALARMSTATE primary key (AlarmStateID),
   constraint FK_ALARMSTA_ALARMSTAT_NOTIFICA foreign key (NotificationGroupID)
         references NotificationGroup (NotificationGroupID)
);

/******************************************************/
/***** INSERT ALL PREDEFINED NOTIFICATIONGROUPS  ******/
/******************************************************/
insert into notificationgroup values(1,'None','None','None','None','None','N');

/******************************************************/
/***** INSERT A GROUPRECIPIENT ROW 		 ******/
/******************************************************/
insert into GroupRecipient values(0,'None','None',1,'None','N');

/******************************************************/
/***** INSERT ALL ALARMSTATE ROW 		 ******/
/******************************************************/
insert into alarmstate values(1,'Event',1);
insert into alarmstate values(2,'Priority 1',1);
insert into alarmstate values(3,'Priority 2',1);
insert into alarmstate values(4,'Priority 3',1);
insert into alarmstate values(5,'Priority 4',1);
insert into alarmstate values(6,'Priority 5',1);
insert into alarmstate values(7,'Priority 6',1);
insert into alarmstate values(8,'Priority 7',1);
insert into alarmstate values(9,'Priority 8',1);
insert into alarmstate values(10,'Priority 9',1);
insert into alarmstate values(11,'Priority 10',1);

/******************************************************/
/***** INSERT A POINTALARMING ROW FOR EACH POINT ******/
/******************************************************/
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, locationid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;