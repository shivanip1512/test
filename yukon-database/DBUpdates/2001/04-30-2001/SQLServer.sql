if exists (select 1
            from  sysobjects
           where  id = object_id('CTIDatabase')
            and   type = 'U')
   drop table CTIDatabase
go
/*==============================================================*/
/* Table : CTIDatabase                                          */
/*==============================================================*/
create table CTIDatabase (
Version              varchar(6)           not null,
CTIEmployeeName      varchar(30)          not null,
DateApplied          datetime             null,
Notes                varchar(300)         null,
constraint PK_CTIDATABASE primary key  (Version)
)
go
insert into CTIDatabase values( '1.01', '(none)',  null, 'The initial database creation script created this.  The DateApplies is (null) because I can not find a way to set it in PowerBuilder!' );


/*==============================================================*/
/* -----------------  BEGIN MACS -------------------------------*/
/*==============================================================*/
if exists (select 1
            from  sysindexes
           where  id    = object_id('MACSchedule')
            and   name  = 'SchedNameIndx'
            and   indid > 0
            and   indid < 255)
   drop index MACSchedule.SchedNameIndx
go
if exists (select 1
            from  sysobjects
           where  id = object_id('MACSchedule')
            and   type = 'U')
   drop table MACSchedule
go
/*==============================================================*/
/* Table : MACSchedule                                          */
/*==============================================================*/
create table MACSchedule (
ScheduleID           numeric              not null,
ScheduleName         varchar(50)          not null,
CategoryName         varchar(50)          not null,
ScheduleType         varchar(12)          not null,
HolidayScheduleID    numeric              null,
CommandFile          varchar(180)         null,
CurrentState         varchar(12)          not null,
StartPolicy          varchar(20)          not null,
StopPolicy           varchar(20)          not null,
LastRunTime          datetime             null,
LastRunStatus        varchar(12)          null,
StartDay             numeric              null,
StartMonth           numeric              null,
StartYear            numeric              null,
StartTime            varchar(8)           null,
StopTime             varchar(8)           null,
ValidWeekDays        char(8)              null,
Duration             numeric              null,
ManualStartTime      datetime             null,
ManualStopTime       datetime             null,
constraint PK_MACSCHEDULE primary key  (ScheduleID)
)
go
/*==============================================================*/
/* Index: SchedNameIndx                                         */
/*==============================================================*/
create unique  index SchedNameIndx on MACSchedule (
ScheduleName
)
go



if exists (select 1
            from  sysobjects
           where  id = object_id('MACSimpleSchedule')
            and   type = 'U')
   drop table MACSimpleSchedule
go
/*==============================================================*/
/* Table : MACSimpleSchedule                                    */
/*==============================================================*/
create table MACSimpleSchedule (
ScheduleID           numeric              not null,
TargetSelect         varchar(40)          null,
StartCommand         varchar(120)         null,
StopCommand          varchar(120)         null,
RepeatInterval       numeric              null,
constraint PK_MACSIMPLESCHEDULE primary key  (ScheduleID)
)
go
alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go
/*==============================================================*/
/* -----------------  END MACS ---------------------------------*/
/*==============================================================*/



/*==============================================================*/
/* -----------------  BEGIN CURTAIL ----------------------------*/
/*==============================================================*/
if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAddress')
            and   type = 'U')
   drop table CustomerAddress
go
/*==============================================================*/
/* Table : CustomerAddress                                      */
/*==============================================================*/
create table CustomerAddress (
AddressID            numeric              not null,
LocationAddress1     varchar(40)          not null,
LocationAddress2     varchar(40)          not null,
CityName             varchar(32)          not null,
StateCode            char(2)              not null,
ZipCode              varchar(12)          not null,
constraint PK_CUSTOMERADDRESS primary key  (AddressID)
)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CICustomerBase')
            and   type = 'U')
   drop table CICustomerBase
go
/*==============================================================*/
/* Table : CICustomerBase                                       */
/*==============================================================*/
create table CICustomerBase (
DeviceID             numeric              not null,
AddressID            numeric              not null,
MainPhoneNumber      varchar(18)          not null,
MainFaxNumber        varchar(18)          not null,
CustPDL              float                not null,
PrimeContactID       numeric              not null,
CustTimeZone         varchar(6)           not null,
CurtailmentAgreement varchar(100)         not null,
constraint PK_CICUSTOMERBASE primary key  (DeviceID)
)
go
alter table CICustomerBase
   add constraint FK_Dev_CICustBase foreign key (DeviceID)
      references DEVICE (DEVICEID)
go
alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (AddressID)
      references CustomerAddress (AddressID)
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CustomerLogin')
            and   name  = 'Index_UserIDName'
            and   indid > 0
            and   indid < 255)
   drop index CustomerLogin.Index_UserIDName
go
if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerLogin')
            and   type = 'U')
   drop table CustomerLogin
go
/*==============================================================*/
/* Table : CustomerLogin                                        */
/*==============================================================*/
create table CustomerLogin (
LogInID              numeric              not null,
UserName             varchar(20)          not null,
Password             varchar(20)          not null,
LoginType            varchar(25)          not null,
LoginCount           numeric              not null,
LastLogin            datetime             not null,
Status               varchar(20)          not null,
constraint PK_CUSTOMERLOGIN primary key  (LogInID)
)
go
insert into CustomerLogin(LogInID,UserName,Password,LoginType,LoginCount,LastLogin,Status)
values (-1,'(none)','(none)','(none)',0,'01-JAN-1990', 'Disabled');
/*==============================================================*/
/* Index: Index_UserIDName                                      */
/*==============================================================*/
create unique  index Index_UserIDName on CustomerLogin (
UserName
)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerContact')
            and   type = 'U')
   drop table CustomerContact
go
/*==============================================================*/
/* Table : CustomerContact                                      */
/*==============================================================*/
create table CustomerContact (
ContactID            numeric              not null,
ContFirstName        varchar(20)          not null,
ContLastName         varchar(32)          not null,
ContPhone1           varchar(14)          not null,
ContPhone2           varchar(14)          not null,
LocationID           numeric              not null,
LogInID              numeric              not null,
constraint PK_CUSTOMERCONTACT primary key  (ContactID)
)
go
insert into CustomerContact(contactID, contFirstName, contLastName, contPhone1, contPhone2, locationID,loginID)
values (-1,'(none)','(none)','(none)','(none)',0,-1)
alter table CustomerContact
   add constraint FK_CstCont_GrpRecip foreign key (LocationID)
      references GroupRecipient (LocationID)
go
alter table CustomerContact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references CustomerLogin (LogInID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMPROGRAM')
            and   type = 'U')
   drop table LMPROGRAM
go
/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/
create table LMPROGRAM (
DEVICEID             numeric              not null,
CONTROLTYPE          varchar(20)          not null,
AVAILABLESEASONS     varchar(4)           not null,
AVAILABLEWEEKDAYS    varchar(8)           not null,
MAXHOURSDAILY        numeric              not null,
MAXHOURSMONTHLY      numeric              not null,
MAXHOURSSEASONAL     numeric              not null,
MAXHOURSANNUALLY     numeric              not null,
MINACTIVATETIME      numeric              not null,
MINRESTARTTIME       numeric              not null,
constraint PK_LMPROGRAM primary key  (DEVICEID)
)
go
alter table LMPROGRAM
   add constraint FK_Device_LMProgram foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramCurtailment')
            and   type = 'U')
   drop table LMProgramCurtailment
go
/*==============================================================*/
/* Table : LMProgramCurtailment                                 */
/*==============================================================*/
create table LMProgramCurtailment (
DeviceID             numeric              not null,
MinNotifyTime        numeric              not null,
Heading              varchar(40)          not null,
MessageHeader        varchar(160)         not null,
MessageFooter        varchar(160)         not null,
AckTimeLimit         numeric              not null,
CanceledMsg          varchar(80)          not null default 'THIS CURTAILMENT HAS BEEN CANCELED, PLEASE DISREGARD.',
StoppedEarlyMsg      varchar(80)          not null default 'THIS CURTAILMENT HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.',
constraint PK_LMPROGRAMCURTAILMENT primary key  (DeviceID)
)
go
alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailProgramActivity')
            and   type = 'U')
   drop table LMCurtailProgramActivity
go
/*==============================================================*/
/* Table : LMCurtailProgramActivity                             */
/*==============================================================*/
create table LMCurtailProgramActivity (
DeviceID             numeric              not null,
CurtailReferenceID   numeric              not null,
ActionDateTime       datetime             not null,
NotificationDateTime datetime             not null,
CurtailmentStartTime datetime             not null,
CurtailmentStopTime  datetime             not null,
RunStatus            varchar(20)          not null,
AdditionalInfo       varchar(100)         not null,
constraint PK_LMCURTAILPROGRAMACTIVITY primary key  (CurtailReferenceID)
)
go
alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailCustomerActivity')
            and   type = 'U')
   drop table LMCurtailCustomerActivity
go
/*==============================================================*/
/* Table : LMCurtailCustomerActivity                            */
/*==============================================================*/
create table LMCurtailCustomerActivity (
CustomerID           numeric              not null,
CurtailReferenceID   numeric              not null,
AcknowledgeStatus    varchar(30)          not null,
AckDateTime          datetime             not null,
IPAddressOfAckUser   varchar(15)          not null,
UserIDName           varchar(40)          not null,
NameOfAckPerson      varchar(40)          not null,
CurtailmentNotes     varchar(120)         not null,
CurrentPDL           float                not null,
AckLateFlag          char(1)              not null
)
go
alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go
alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CICustContact')
            and   type = 'U')
   drop table CICustContact
go
/*==============================================================*/
/* Table : CICustContact                                        */
/*==============================================================*/
create table CICustContact (
DeviceID             numeric              not null,
ContactID            numeric              not null,
constraint PK_CICUSTCONTACT primary key  (ContactID, DeviceID)
)
go
alter table CICustContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references CustomerContact (ContactID)
go
alter table CICustContact
   add constraint FK_CICstBase_CICstCont foreign key (DeviceID)
      references CICustomerBase (DeviceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramCurtailCustomerList')
            and   type = 'U')
   drop table LMProgramCurtailCustomerList
go
/*==============================================================*/
/* Table : LMProgramCurtailCustomerList                         */
/*==============================================================*/
create table LMProgramCurtailCustomerList (
DeviceID             numeric              not null,
LMCustomerDeviceID   numeric              not null,
CustomerOrder        numeric              not null,
RequireAck           char(1)              not null,
constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key  (LMCustomerDeviceID, DeviceID)
)
go
alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (LMCustomerDeviceID)
      references CICustomerBase (DeviceID)
go
alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMProgram')
            and   type = 'U')
   drop table DynamicLMProgram
go
/*==============================================================*/
/* Table : DynamicLMProgram                                     */
/*==============================================================*/
create table DynamicLMProgram (
DeviceID             numeric              not null,
ProgramState         numeric              not null,
ReductionTotal       float                not null,
StartedControlling   datetime             not null,
LastControlSent      datetime             not null,
ManualControlReceivedFlag char(1)              not null,
TimeStamp            datetime             not null,
constraint PK_DYNAMICLMPROGRAM primary key  (DeviceID)
)
go
alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramControlWindow')
            and   type = 'U')
   drop table LMProgramControlWindow
go
/*==============================================================*/
/* Table : LMProgramControlWindow                               */
/*==============================================================*/
create table LMProgramControlWindow (
DeviceID             numeric              not null,
WindowNumber         numeric              not null,
AvailableStartTime   numeric              not null,
AvailableStopTime    numeric              not null
)
go
alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirect')
            and   type = 'U')
   drop table LMProgramDirect
go
/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/
create table LMProgramDirect (
DeviceID             numeric              not null,
GroupSelectionMethod varchar(30)          not null,
constraint PK_LMPROGRAMDIRECT primary key  (DeviceID)
)
go
alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirectGear')
            and   type = 'U')
   drop table LMProgramDirectGear
go
/*==============================================================*/
/* Table : LMProgramDirectGear                                  */
/*==============================================================*/
create table LMProgramDirectGear (
DeviceID             numeric              not null,
GearName             varchar(30)          not null,
GearNumber           numeric              not null,
ControlMethod        varchar(30)          not null,
MethodRate           numeric              not null,
MethodPeriod         numeric              not null,
MethodRateCount      numeric              not null,
MethodCommand        varchar(40)          not null,
CycleRefreshRate     numeric              not null,
MethodStopType       varchar(20)          not null,
ChangeCondition      varchar(24)          not null,
ChangeDuration       numeric              not null,
ChangePriority       numeric              not null,
ChangeTriggerNumber  numeric              not null,
ChangeTriggerOffset  float                not null,
PercentReduction     numeric              not null,
constraint PK_LMPROGRAMDIRECTGEAR primary key  (DeviceID, GearNumber)
)
go
alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMGroup')
            and   type = 'U')
   drop table LMGroup
go
/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/
create table LMGroup (
DeviceID             numeric              not null,
KWCapacity           float                not null,
RecordControlHistoryFlag varchar(1)           not null,
constraint PK_LMGROUP primary key  (DeviceID)
)
go
alter table LMGroup
   add constraint FK_Device_LMGrpBase foreign key (DeviceID)
      references DEVICE (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramDirectGroup')
            and   type = 'U')
   drop table LMProgramDirectGroup
go
/*==============================================================*/
/* Table : LMProgramDirectGroup                                 */
/*==============================================================*/
create table LMProgramDirectGroup (
DeviceID             numeric              not null,
LMGroupDeviceID      numeric              not null,
GroupOrder           numeric              not null
)
go
alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go
alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREA')
            and   type = 'U')
   drop table LMCONTROLAREA
go
/*==============================================================*/
/* Table : LMCONTROLAREA                                        */
/*==============================================================*/
create table LMCONTROLAREA (
DEVICEID             numeric              not null,
DEFOPERATIONALSTATE  varchar(20)          not null,
CONTROLINTERVAL      numeric              not null,
MINRESPONSETIME      numeric              not null,
DEFDAILYSTARTTIME    numeric              not null,
DEFDAILYSTOPTIME     numeric              not null,
REQUIREALLTRIGGERSACTIVEFLAG varchar(1)           not null,
constraint PK_LMCONTROLAREA primary key  (DEVICEID)
)
go
alter table LMCONTROLAREA
   add constraint FK_Device_LMCctrlArea foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREAPROGRAM')
            and   type = 'U')
   drop table LMCONTROLAREAPROGRAM
go
/*==============================================================*/
/* Table : LMCONTROLAREAPROGRAM                                 */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM (
DEVICEID             numeric              not null,
LMPROGRAMDEVICEID    numeric              not null,
USERORDER            numeric              not null,
STOPORDER            numeric              not null,
DEFAULTPRIORITY      numeric              not null
)
go
alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMCONTROLAREA (DEVICEID)
go
alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCONTROLAREATRIGGER')
            and   type = 'U')
   drop table LMCONTROLAREATRIGGER
go
/*==============================================================*/
/* Table : LMCONTROLAREATRIGGER                                 */
/*==============================================================*/
create table LMCONTROLAREATRIGGER (
DEVICEID             numeric              not null,
TRIGGERNUMBER        numeric              not null,
TRIGGERTYPE          varchar(20)          not null,
POINTID              numeric              not null,
NORMALSTATE          numeric              not null,
THRESHOLD            float                not null,
PROJECTIONTYPE       varchar(14)          not null,
PROJECTIONPOINTS     numeric              not null,
PROJECTAHEADDURATION numeric              not null,
THRESHOLDKICKPERCENT numeric              not null,
MINRESTOREOFFSET     float                not null,
PEAKPOINTID          numeric              not null
)
go
alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID)
go
alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID)
go
alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMCONTROLAREA (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMGroup')
            and   type = 'U')
   drop table DynamicLMGroup
go
/*==============================================================*/
/* Table : DynamicLMGroup                                       */
/*==============================================================*/
create table DynamicLMGroup (
DeviceID             numeric              not null,
GroupControlState    numeric              not null,
CurrentHoursDaily    numeric              not null,
CurrentHoursMonthly  numeric              not null,
CurrentHoursSeasonal numeric              not null,
CurrentHoursAnnually numeric              not null,
LastControlSent      datetime             not null,
TimeStamp            datetime             not null,
constraint PK_DYNAMICLMGROUP primary key  (DeviceID)
)
go
alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMProgramDirect')
            and   type = 'U')
   drop table DynamicLMProgramDirect
go
/*==============================================================*/
/* Table : DynamicLMProgramDirect                               */
/*==============================================================*/
create table DynamicLMProgramDirect (
DeviceID             numeric              not null,
CurrentGearNumber    numeric              not null,
LastGroupControlled  numeric              not null,
StartTime            datetime             not null,
StopTime             datetime             not null,
TimeStamp            datetime             not null,
constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID)
)
go
alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('DynamicLMControlArea')
            and   type = 'U')
   drop table DynamicLMControlArea
go
/*==============================================================*/
/* Table : DynamicLMControlArea                                 */
/*==============================================================*/
create table DynamicLMControlArea (
DeviceID             numeric              not null,
NextCheckTime        datetime             not null,
NewPointDataReceivedFlag char(1)              not null,
UpdatedFlag          char(1)              not null,
ControlAreaState     numeric              not null,
CurrentPriority      numeric              not null,
TimeStamp            datetime             not null,
constraint PK_DYNAMICLMCONTROLAREA primary key  (DeviceID)
)
go
alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMCONTROLAREA (DEVICEID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCurtailCustomerActivity_View')
            and   type = 'V')
   drop view LMCurtailCustomerActivity_View
go
/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create view LMCurtailCustomerActivity_View as 
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
go
/*==============================================================*/
/* -----------------  END CURTAIL ------------------------------*/
/*==============================================================*/



/*==============================================================*/
/* -----------------  BEGIN GRAPH ------------------------------*/
/*==============================================================*/
if exists (select 1
            from  sysobjects
           where  id = object_id('LMMacsScheduleCustomerList')
            and   type = 'U')
   drop table LMMacsScheduleCustomerList
go
/*==============================================================*/
/* Table : LMMacsScheduleCustomerList                           */
/*==============================================================*/
create table LMMacsScheduleCustomerList (
ScheduleID           numeric              not null,
LMCustomerDeviceID   numeric              not null,
CustomerOrder        numeric              not null,
constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key  (ScheduleID, LMCustomerDeviceID)
)
go
alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (DeviceID)
go
alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('GraphCustomerList')
            and   type = 'U')
   drop table GraphCustomerList
go
/*==============================================================*/
/* Table : GraphCustomerList                                    */
/*==============================================================*/
create table GraphCustomerList (
GraphDefinitionID    numeric              not null,
CustomerID           numeric              not null,
CustomerOrder        numeric              not null,
constraint PK_GRAPHCUSTOMERLIST primary key  (GraphDefinitionID, CustomerID)
)
go
alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCS_CICUSTOM foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go
alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerWebSettings')
            and   type = 'U')
   drop table CustomerWebSettings
go
/*==============================================================*/
/* Table : CustomerWebSettings                                  */
/*==============================================================*/
create table CustomerWebSettings (
CustomerID           numeric              not null,
DatabaseAlias        varchar(40)          not null,
Logo                 varchar(60)          not null,
HomeURL              varchar(60)          not null,
constraint PK_CUSTOMERWEBSETTINGS primary key  (CustomerID)
)
go
alter table CustomerWebSettings
   add constraint FK_CustWebSet_CICstBse foreign key (CustomerID)
      references CICustomerBase (DeviceID)
go
/*==============================================================*/
/* -----------------  END GRAPH --------------------------------*/
/*==============================================================*/