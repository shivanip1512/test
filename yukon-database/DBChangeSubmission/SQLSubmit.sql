/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/********** JULIE: added 2way cbc state information to dynamic cap table ******/
alter table dynamiccccapbank add twowaycbcstate numeric;
go
update dynamiccccapbank set twowaycbcstate = -1;
go
alter table dynamiccccapbank alter column twowaycbcstate numeric not null;
go
alter table dynamiccccapbank add twowaycbcstatetime datetime;
go
update dynamiccccapbank set twowaycbcstatetime = '01-JAN-1990';
go
alter table dynamiccccapbank alter column twowaycbcstatetime datetime not null;

/********** END JULIE ********************************************************/



/** Stacey:  Changes to XXXReadLog tables ************************************/
/** First:  Remove the MeterReadLog table from head(3.4)**********************/
/** These deletion statements do not need to be in the update scripts since we haven't built 3.4 yet.  
    The old stuff can just be removed from the scripts and replaced with the new stuff below. **/
drop table MeterReadLog;	-- reference or head database use only
delete sequencenumber where sequencename = 'MeterReadLog';	-- reference or head database use only
/** Then:  Create new XXXLog tables as listed below **************************/

/*==============================================================*/
/* Table: DEVICEREADLOG                                         */
/*==============================================================*/
create table DEVICEREADLOG (
   DeviceReadLogID      numeric              not null,
   DeviceID             numeric              not null,
   TIMESTAMP            datetime             not null,
   STATUSCODE           numeric              not null,
   DeviceReadRequestLogID  numeric           not null
)
go

alter table DEVICEREADLOG
   add constraint PK_DEVICEREADLOG primary key  (DeviceReadLogID)
go


/*==============================================================*/
/* Table: DEVICEREADREQUESTLOG                                  */
/*==============================================================*/
create table DEVICEREADREQUESTLOG (
   DeviceReadRequestLogID  numeric           not null,
   RequestID            numeric              not null,
   Command              varchar(128)         not null,
   StartTime            datetime             not null,
   StopTime             datetime             not null,
   DeviceReadJobLogID   numeric              not null
)
go

alter table DEVICEREADREQUESTLOG
   add constraint PK_DEVICEREADREQUESTLOG primary key  (DeviceReadRequestLogID)
go

/*==============================================================*/
/* Table: DEVICEREADJOBLOG                                      */
/*==============================================================*/
create table DEVICEREADJOBLOG (
   DeviceReadJobLogID   numeric              not null,
   ScheduleID           numeric              not null,
   StartTime            datetime             not null,
   StopTime             datetime             not null
)
go

alter table DEVICEREADJOBLOG
   add constraint PK_DEVICEREADJOBLOG primary key  (DeviceReadJobLogID)
go

alter table DEVICEREADLOG
   add constraint FK_DRLogDevID_DevID foreign key (DeviceID)
      references Device (DeviceID)
go

alter table DEVICEREADLOG
   add constraint FK_DRLogReqLogID_DRReqLogID foreign key (DeviceReadRequestLogId)
      references DeviceReadRequestLog (DeviceReadRequestLogId)
go

alter table DEVICEREADREQUESTLOG
   add constraint FK_DRReqLogJobID_DRJobLogID foreign key (DeviceReadJobLogId)
      references DeviceReadJobLog (DeviceReadJobLogId)
go

alter table DEVICEREADJOBLOG
   add constraint FK_DRJobLogSchdID_MacSchdID foreign key (ScheduleID)
      references MacSchedule (ScheduleID)
go

insert into sequencenumber values (1,'DeviceReadLog');
insert into sequencenumber values (1,'DeviceReadRequestLog');
insert into sequencenumber values (1,'DeviceReadJobLog');