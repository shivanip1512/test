alter table pointunit add DECIMALPLACES NUMBER;
update pointunit set DECIMALPLACES=3;
alter table pointunit modify DECIMALPLACES not null;
/* SQL SERVER */
/*alter table pointunit add DECIMALPLACES NUMERIC not null default 3;*/

alter table pointlimits add ALARMDURATION NUMBER;
update pointlimits set ALARMDURATION=0;
alter table pointlimits modify ALARMDURATION not null;
/* SQL SERVER */
/*alter table pointlimits add ALARMDURATION NUMERIC not null default 0;*/

create table NotificationGroup (
   NotificationGroupID  NUMBER                           not null,
   GroupName            VARCHAR2(40)                     not null,
   EmailSubject         VARCHAR2(30)                     not null,
   EmailFromAddress     VARCHAR2(40)                     not null,
   EmailMessage         VARCHAR2(60)                     not null,
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

/******************************************************/
/***** INSERT ALL PREDEFINED NOTIFICATIONGROUPS  ******/
/******************************************************/
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

/******************************************************/
/***** INSERT A GROUPRECIPIENT ROW 		 ******/
/******************************************************/
insert into GroupRecipient values(0,'None','None',1,'None','N');

/******************************************************/
/***** INSERT A POINTALARMING ROW FOR EACH POINT ******/
/******************************************************/
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, locationid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;