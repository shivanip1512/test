/*==============================================================*/
/* Table : MACSchedule                                          */
/*==============================================================*/

create table MACSchedule (
   ScheduleID           INTEGER                          not null,
   ScheduleName         VARCHAR2(50)                     not null,
   CategoryName         VARCHAR2(20)                     not null,
   ScheduleType         VARCHAR2(12)                     not null,
   HolidayScheduleID    INTEGER,
   CommandFile          VARCHAR2(180),
   CurrentState         VARCHAR2(12)                     not null,
   StartPolicy          VARCHAR2(20)                     not null,
   StopPolicy           VARCHAR2(20)                     not null,
   LastRunTime          DATE,
   LastRunStatus        VARCHAR2(12),
   StartDay             INTEGER,
   StartMonth           INTEGER,
   StartYear            INTEGER,
   StartTime            VARCHAR2(8),
   StopTime             VARCHAR2(8),
   ValidWeekDays        CHAR(8),
   Duration             INTEGER,
   ManualStartTime      DATE,
   ManualStopTime       DATE,
   constraint PK_MACSCHEDULE primary key (ScheduleID)
)
/
/*==============================================================*/
/* Index: SchedNameIndx                                         */
/*==============================================================*/
create unique index SchedNameIndx on MACSchedule (
   ScheduleName ASC
)
/


/*==============================================================*/
/* Table : MACSimpleSchedule                                    */
/*==============================================================*/

create table MACSimpleSchedule (
   ScheduleID           INTEGER                          not null,
   TargetSelect         VARCHAR2(40),
   StartCommand         VARCHAR2(120),
   StopCommand          VARCHAR2(120),
   RepeatInterval       INTEGER,
   constraint PK_MACSIMPLESCHEDULE primary key (ScheduleID),
   constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
         references MACSchedule (ScheduleID)
)
/