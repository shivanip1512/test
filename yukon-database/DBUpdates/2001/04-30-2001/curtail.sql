/*==============================================================*/
/* Created : 4-30-2001                           */
/*==============================================================*/


/****************************************************************/
/* START CURTAIL ************************************************/
/****************************************************************/
/*==============================================================*/
/* Table : CustomerAddress                                      */
/*==============================================================*/
create table CustomerAddress (
   AddressID            NUMBER                           not null,
   LocationAddress1     VARCHAR2(40)                     not null,
   LocationAddress2     VARCHAR2(40)                     not null,
   CityName             VARCHAR2(32)                     not null,
   StateCode            CHAR(2)                          not null,
   ZipCode              VARCHAR2(12)                     not null,
   constraint PK_CUSTOMERADDRESS primary key (AddressID)
)
/

/*==============================================================*/
/* Table : CICustomerBase                                       */
/*==============================================================*/

create table CICustomerBase (
   DeviceID             NUMBER                           not null,
   AddressID            NUMBER                           not null,
   MainPhoneNumber      VARCHAR2(18)                     not null,
   MainFaxNumber        VARCHAR2(18)                     not null,
   CustPDL              FLOAT                            not null,
   PrimeContactID       NUMBER                           not null,
   CustTimeZone         VARCHAR2(6)                      not null,
   CurtailmentAgreement VARCHAR2(100)                    not null,
   constraint PK_CICUSTOMERBASE primary key (DeviceID),
   constraint FK_Dev_CICustBase foreign key (DeviceID)
         references DEVICE (DEVICEID),
   constraint FK_CICstBas_CstAddrs foreign key (AddressID)
         references CustomerAddress (AddressID)
)
/

/*==============================================================*/
/* Table : CustomerLogin                                        */
/*==============================================================*/

create table CustomerLogin (
   LogInID              NUMBER                           not null,
   UserName             VARCHAR2(20)                     not null,
   Password             VARCHAR2(20)                     not null,
   LoginType            VARCHAR2(25)                     not null,
   LoginCount           NUMBER                           not null,
   LastLogin            DATE                             not null,
   Status               VARCHAR2(20)                     not null,
   constraint PK_CUSTOMERLOGIN primary key (LogInID)
)
/


insert into CustomerLogin(LogInID,UserName,Password,LoginType,LoginCount,LastLogin,Status)
values (-1,'(none)','(none)','(none)',0,'01-JAN-1990', 'Disabled');

/*==============================================================*/
/* Index: Index_UserIDName                                      */
/*==============================================================*/
create unique index Index_UserIDName on CustomerLogin (
   UserName ASC
)
/

/*==============================================================*/
/* Table : CustomerContact                                      */
/*==============================================================*/

create table CustomerContact (
   ContactID            NUMBER                           not null,
   ContFirstName        VARCHAR2(20)                     not null,
   ContLastName         VARCHAR2(32)                     not null,
   ContPhone1           VARCHAR2(14)                     not null,
   ContPhone2           VARCHAR2(14)                     not null,
   LocationID           NUMBER                           not null,
   LogInID              NUMBER                           not null,
   constraint PK_CUSTOMERCONTACT primary key (ContactID),
   constraint FK_CstCont_GrpRecip foreign key (LocationID)
         references GroupRecipient (LocationID),
   constraint FK_RefCstLg_CustCont foreign key (LogInID)
         references CustomerLogin (LogInID)
)
/
insert into CustomerContact(contactID, contFirstName, contLastName, contPhone1, contPhone2, locationID,loginID)
values (-1,'(none)','(none)','(none)','(none)',0,-1)


/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/

create table LMPROGRAM (
   DEVICEID             NUMBER                           not null,
   CONTROLTYPE          VARCHAR2(20)                     not null,
   AVAILABLESEASONS     VARCHAR2(4)                      not null,
   AVAILABLEWEEKDAYS    VARCHAR2(8)                      not null,
   MAXHOURSDAILY        NUMBER                           not null,
   MAXHOURSMONTHLY      NUMBER                           not null,
   MAXHOURSSEASONAL     NUMBER                           not null,
   MAXHOURSANNUALLY     NUMBER                           not null,
   MINACTIVATETIME      NUMBER                           not null,
   MINRESTARTTIME       NUMBER                           not null,
   constraint PK_LMPROGRAM primary key (DEVICEID),
   constraint FK_Device_LMProgram foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMProgramCurtailment                                 */
/*==============================================================*/

create table LMProgramCurtailment (
   DeviceID             NUMBER                           not null,
   MinNotifyTime        NUMBER                           not null,
   Heading              VARCHAR2(40)                     not null,
   MessageHeader        VARCHAR2(160)                    not null,
   MessageFooter        VARCHAR2(160)                    not null,
   AckTimeLimit         NUMBER                           not null,
   CanceledMsg          VARCHAR2(80)                   default 'THIS CURTAILMENT HAS BEEN CANCELED, PLEASE DISREGARD.'  not null,
   StoppedEarlyMsg      VARCHAR2(80)                   default 'THIS CURTAILMENT HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.'  not null,
   constraint PK_LMPROGRAMCURTAILMENT primary key (DeviceID),
   constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMCurtailProgramActivity                             */
/*==============================================================*/

create table LMCurtailProgramActivity (
   DeviceID             NUMBER                           not null,
   CurtailReferenceID   NUMBER                           not null,
   ActionDateTime       DATE                             not null,
   NotificationDateTime DATE                             not null,
   CurtailmentStartTime DATE                             not null,
   CurtailmentStopTime  DATE                             not null,
   RunStatus            VARCHAR2(20)                     not null,
   AdditionalInfo       VARCHAR2(100)                    not null,
   constraint PK_LMCURTAILPROGRAMACTIVITY primary key (CurtailReferenceID),
   constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
         references LMProgramCurtailment (DeviceID)
)
/

/*==============================================================*/
/* Table : LMCurtailCustomerActivity                            */
/*==============================================================*/

create table LMCurtailCustomerActivity (
   CustomerID           NUMBER                           not null,
   CurtailReferenceID   NUMBER                           not null,
   AcknowledgeStatus    VARCHAR2(30)                     not null,
   AckDateTime          DATE                             not null,
   IPAddressOfAckUser   VARCHAR2(15)                     not null,
   UserIDName           VARCHAR2(40)                     not null,
   NameOfAckPerson      VARCHAR2(40)                     not null,
   CurtailmentNotes     VARCHAR2(120)                    not null,
   CurrentPDL           FLOAT                            not null,
   AckLateFlag          CHAR(1)                          not null,
   constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
         references CICustomerBase (DeviceID),
   constraint FK_LMCURTAI_REFLMCUST_LMCURTAI foreign key (CurtailReferenceID)
         references LMCurtailProgramActivity (CurtailReferenceID)
)
/

/*==============================================================*/
/* Table : CICustContact                                        */
/*==============================================================*/

create table CICustContact (
   DeviceID             NUMBER                           not null,
   ContactID            NUMBER                           not null,
   constraint PK_CICUSTCONTACT primary key (ContactID, DeviceID),
   constraint FK_CstCont_CICstCont foreign key (ContactID)
         references CustomerContact (ContactID),
   constraint FK_CICstBase_CICstCont foreign key (DeviceID)
         references CICustomerBase (DeviceID)
)
/

/*==============================================================*/
/* Table : LMProgramCurtailCustomerList                         */
/*==============================================================*/

create table LMProgramCurtailCustomerList (
   DeviceID             NUMBER                           not null,
   LMCustomerDeviceID   NUMBER                           not null,
   CustomerOrder        NUMBER                           not null,
   RequireAck           CHAR(1)                          not null,
   constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key (LMCustomerDeviceID, DeviceID),
   constraint FK_CICstBase_LMProgCList foreign key (LMCustomerDeviceID)
         references CICustomerBase (DeviceID),
   constraint FK_LMPrgCrt_LMPrCstLst foreign key (DeviceID)
         references LMProgramCurtailment (DeviceID)
         on delete cascade
)
/

/*==============================================================*/
/* Table : DynamicLMProgram                                     */
/*==============================================================*/

create table DynamicLMProgram (
   DeviceID             NUMBER                           not null,
   ProgramState         NUMBER                           not null,
   ReductionTotal       FLOAT                            not null,
   StartedControlling   DATE                             not null,
   LastControlSent      DATE                             not null,
   ManualControlReceivedFlag CHAR(1)                          not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMPROGRAM primary key (DeviceID),
   constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMProgramControlWindow                               */
/*==============================================================*/

create table LMProgramControlWindow (
   DeviceID             NUMBER                           not null,
   WindowNumber         NUMBER                           not null,
   AvailableStartTime   NUMBER                           not null,
   AvailableStopTime    NUMBER                           not null,
   constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/

create table LMProgramDirect (
   DeviceID             NUMBER                           not null,
   GroupSelectionMethod VARCHAR2(30)                     not null,
   constraint PK_LMPROGRAMDIRECT primary key (DeviceID),
   constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMProgramDirectGear                                  */
/*==============================================================*/

create table LMProgramDirectGear (
   DeviceID             NUMBER                           not null,
   GearName             VARCHAR2(30)                     not null,
   GearNumber           NUMBER                           not null,
   ControlMethod        VARCHAR2(30)                     not null,
   MethodRate           NUMBER                           not null,
   MethodPeriod         NUMBER                           not null,
   MethodRateCount      NUMBER                           not null,
   CycleRefreshRate     NUMBER                           not null,
   MethodStopType       VARCHAR2(20)                     not null,
   ChangeCondition      VARCHAR2(24)                     not null,
   ChangeDuration       NUMBER                           not null,
   ChangePriority       NUMBER                           not null,
   ChangeTriggerNumber  NUMBER                           not null,
   ChangeTriggerOffset  FLOAT                            not null,
   PercentReduction     NUMBER                           not null,
   constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
         references LMProgramDirect (DeviceID)
)
/

/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/

create table LMGroup (
   DeviceID             NUMBER                           not null,
   KWCapacity           FLOAT                            not null,
   RecordControlHistoryFlag VARCHAR2(1)                      not null,
   constraint PK_LMGROUP primary key (DeviceID),
   constraint FK_Device_LMGrpBase foreign key (DeviceID)
         references DEVICE (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMProgramDirectGroup                                 */
/*==============================================================*/

create table LMProgramDirectGroup (
   DeviceID             NUMBER                           not null,
   LMGroupDeviceID      NUMBER                           not null,
   GroupOrder           NUMBER                           not null,
   constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
         references LMProgramDirect (DeviceID),
   constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
         references LMGroup (DeviceID)
)
/

/*==============================================================*/
/* Table : LMCONTROLAREA                                        */
/*==============================================================*/

create table LMCONTROLAREA (
   DEVICEID             NUMBER                           not null,
   DEFOPERATIONALSTATE  VARCHAR2(20)                     not null,
   CONTROLINTERVAL      NUMBER                           not null,
   MINRESPONSETIME      NUMBER                           not null,
   DEFDAILYSTARTTIME    NUMBER                           not null,
   DEFDAILYSTOPTIME     NUMBER                           not null,
   REQUIREALLTRIGGERSACTIVEFLAG VARCHAR2(1)                      not null,
   constraint PK_LMCONTROLAREA primary key (DEVICEID),
   constraint FK_Device_LMCctrlArea foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMCONTROLAREAPROGRAM                                 */
/*==============================================================*/

create table LMCONTROLAREAPROGRAM (
   DEVICEID             NUMBER                           not null,
   LMPROGRAMDEVICEID    NUMBER                           not null,
   USERORDER            NUMBER                           not null,
   STOPORDER            NUMBER                           not null,
   DEFAULTPRIORITY      NUMBER                           not null,
   constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
         references LMCONTROLAREA (DEVICEID),
   constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
         references LMPROGRAM (DEVICEID)
)
/

/*==============================================================*/
/* Table : LMCONTROLAREATRIGGER                                 */
/*==============================================================*/

create table LMCONTROLAREATRIGGER (
   DEVICEID             NUMBER                           not null,
   TRIGGERNUMBER        NUMBER                           not null,
   TRIGGERTYPE          VARCHAR2(20)                     not null,
   POINTID              NUMBER                           not null,
   NORMALSTATE          NUMBER                           not null,
   THRESHOLD            FLOAT                            not null,
   PROJECTIONTYPE       VARCHAR2(14)                     not null,
   PROJECTIONPOINTS     NUMBER                           not null,
   PROJECTAHEADDURATION NUMBER                           not null,
   THRESHOLDKICKPERCENT NUMBER                           not null,
   MINRESTOREOFFSET     FLOAT                            not null,
   PEAKPOINTID          NUMBER                           not null,
   constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
         references LMCONTROLAREA (DEVICEID),
   constraint FK_Point_LMCntlArTrig foreign key (POINTID)
         references POINT (POINTID),
   constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
         references POINT (POINTID)
)
/

/*==============================================================*/
/* Table : DynamicLMGroup                                       */
/*==============================================================*/

create table DynamicLMGroup (
   DeviceID             NUMBER                           not null,
   GroupControlState    NUMBER                           not null,
   CurrentHoursDaily    NUMBER                           not null,
   CurrentHoursMonthly  NUMBER                           not null,
   CurrentHoursSeasonal NUMBER                           not null,
   CurrentHoursAnnually NUMBER                           not null,
   LastControlSent      DATE                             not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMGROUP primary key (DeviceID),
   constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
         references LMGroup (DeviceID)
)
/

/*==============================================================*/
/* Table : DynamicLMProgramDirect                               */
/*==============================================================*/

create table DynamicLMProgramDirect (
   DeviceID             NUMBER                           not null,
   CurrentGearNumber    NUMBER                           not null,
   LastGroupControlled  NUMBER                           not null,
   StartTime            DATE                             not null,
   StopTime             DATE                             not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMPROGRAMDIRECT primary key (DeviceID),
   constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
         references LMProgramDirect (DeviceID)
)
/

/*==============================================================*/
/* Table : DynamicLMControlArea                                 */
/*==============================================================*/

create table DynamicLMControlArea (
   DeviceID             NUMBER                           not null,
   NextCheckTime        DATE                             not null,
   NewPointDataReceivedFlag CHAR(1)                          not null,
   UpdatedFlag          CHAR(1)                          not null,
   ControlAreaState     NUMBER                           not null,
   CurrentPriority      NUMBER                           not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMCONTROLAREA primary key (DeviceID),
   constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
         references LMCONTROLAREA (DEVICEID)
)
/

/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create or replace view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
/
/****************************************************************/
/* END CURTAIL **************************************************/
/****************************************************************/