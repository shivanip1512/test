/************************/
/* DBEditor Updates 	*/
/************************/
delete from notificationgroup where notificationgroupid<>1;
go
delete from alarm;
go
drop table alarm;
go

delete from NotificationDestination;
go
drop table NotificationDestination;
go

create table NotificationDestination (
DestinationOrder     numeric                        not null,
NotificationGroupID  numeric                        not null,
LocationID           numeric                        not null,
constraint PrimaryKey_NOTIFICATIONDESTINATION primary key  (NotificationGroupID, DestinationOrder)
)
go
alter table NotificationDestination
   add constraint ForeignK_NOTIFICAGROUP_NOTIFICATDEST foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go
alter table NotificationDestination
   add constraint ForeignK_NOTIFICA_REFERENCE_GROUPREC foreign key (LocationID)
      references GroupRecipient (LocationID)
go


create table AlarmState (
AlarmStateID         numeric                        not null,
AlarmStateName       varchar(40)                    not null,
NotificationGroupID  numeric                        not null,
constraint PKey_ALARMSTATE primary key  (AlarmStateID)
)
go
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

alter table AlarmState
   add constraint FKey_ALARMSTA_ALARMSTAT_NOTIFICA foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go