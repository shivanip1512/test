/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      CTI Oracle 8.1.5                             */
/* Created on:     9/22/2004 2:59:10 PM                         */
/*==============================================================*/


/*==============================================================*/
/* Table : ActivityLog                                          */
/*==============================================================*/


create table ActivityLog  (
   ActivityLogID        NUMBER                           not null,
   TimeStamp            DATE                             not null,
   UserID               NUMBER,
   AccountID            NUMBER,
   EnergyCompanyID      NUMBER,
   CustomerID           NUMBER,
   PaoID                NUMBER,
   Action               VARCHAR2(80)                     not null,
   Description          VARCHAR2(120)                    not null
)
/


alter table ActivityLog
   add constraint PK_ACTIVITYLOG primary key (ActivityLogID)
/


/*==============================================================*/
/* Table : Address                                              */
/*==============================================================*/


create table Address  (
   AddressID            NUMBER                           not null,
   LocationAddress1     VARCHAR2(40)                     not null,
   LocationAddress2     VARCHAR2(40)                     not null,
   CityName             VARCHAR2(32)                     not null,
   StateCode            CHAR(2)                          not null,
   ZipCode              VARCHAR2(12)                     not null,
   County               VARCHAR2(30)                     not null
)
/


alter table Address
   add constraint PK_ADDRESS primary key (AddressID)
/


/*==============================================================*/
/* Table : AlarmCategory                                        */
/*==============================================================*/


create table AlarmCategory  (
   AlarmCategoryID      NUMBER                           not null,
   CategoryName         VARCHAR2(40)                     not null,
   NotificationGroupID  NUMBER                           not null
)
/


alter table AlarmCategory
   add constraint PK_ALARMCATEGORYID primary key (AlarmCategoryID)
/


/*==============================================================*/
/* Table : BaseLine                                             */
/*==============================================================*/


create table BaseLine  (
   BaselineID           NUMBER                           not null,
   BaselineName         VARCHAR2(30)                     not null,
   DaysUsed             NUMBER                           not null,
   PercentWindow        NUMBER                           not null,
   CalcDays             NUMBER                           not null,
   ExcludedWeekDays     CHAR(7)                          not null,
   HolidaysUsed         NUMBER                           not null
)
/


alter table BaseLine
   add constraint PK_BASELINE primary key (BaselineID)
/


/*==============================================================*/
/* Table : BillingFileFormats                                   */
/*==============================================================*/


create table BillingFileFormats  (
   FormatID             NUMBER                           not null,
   FormatType           VARCHAR2(30)                     not null
)
/


alter table BillingFileFormats
   add constraint PK_BILLINGFILEFORMATS primary key (FormatID)
/


/*==============================================================*/
/* Table : CALCBASE                                             */
/*==============================================================*/


create table CALCBASE  (
   POINTID              NUMBER                           not null,
   UPDATETYPE           VARCHAR2(16)                     not null,
   PERIODICRATE         NUMBER                           not null
)
/


alter table CALCBASE
   add constraint PK_CALCBASE primary key (POINTID)
/


/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create index Indx_ClcBaseUpdTyp on CALCBASE (
   UPDATETYPE ASC
)
/


/*==============================================================*/
/* Table : CALCCOMPONENT                                        */
/*==============================================================*/


create table CALCCOMPONENT  (
   PointID              NUMBER                           not null,
   COMPONENTORDER       NUMBER                           not null,
   COMPONENTTYPE        VARCHAR2(10)                     not null,
   COMPONENTPOINTID     NUMBER                           not null,
   OPERATION            VARCHAR2(10),
   CONSTANT             FLOAT                            not null,
   FUNCTIONNAME         VARCHAR2(20)
)
/


alter table CALCCOMPONENT
   add constraint PK_CALCCOMPONENT primary key (PointID, COMPONENTORDER)
/


/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create index Indx_CalcCmpCmpType on CALCCOMPONENT (
   COMPONENTTYPE ASC
)
/


/*==============================================================*/
/* Table : CAPBANK                                              */
/*==============================================================*/


create table CAPBANK  (
   DEVICEID             NUMBER                           not null,
   OPERATIONALSTATE     VARCHAR2(8)                      not null,
   ControllerType       VARCHAR2(20)                     not null,
   CONTROLDEVICEID      NUMBER                           not null,
   CONTROLPOINTID       NUMBER                           not null,
   BANKSIZE             NUMBER                           not null,
   TypeOfSwitch         VARCHAR2(20)                     not null,
   SwitchManufacture    VARCHAR2(20)                     not null,
   MapLocationID        NUMBER                           not null,
   RecloseDelay         NUMBER                           not null
)
/


alter table CAPBANK
   add constraint PK_CAPBANK primary key (DEVICEID)
/


/*==============================================================*/
/* Table : CAPCONTROLSUBSTATIONBUS                              */
/*==============================================================*/


create table CAPCONTROLSUBSTATIONBUS  (
   SubstationBusID      NUMBER                           not null,
   ControlMethod        VARCHAR2(20)                     not null,
   MAXDAILYOPERATION    NUMBER                           not null,
   MaxOperationDisableFlag CHAR(1)                          not null,
   PEAKSETPOINT         FLOAT                            not null,
   OFFPEAKSETPOINT      FLOAT                            not null,
   PEAKSTARTTIME        NUMBER                           not null,
   PEAKSTOPTIME         NUMBER                           not null,
   CurrentVarLoadPointID NUMBER                           not null,
   CurrentWattLoadPointID NUMBER                           not null,
   UpperBandwidth       FLOAT                            not null,
   CONTROLINTERVAL      NUMBER                           not null,
   MINRESPONSETIME      NUMBER                           not null,
   MINCONFIRMPERCENT    NUMBER                           not null,
   FAILUREPERCENT       NUMBER                           not null,
   DAYSOFWEEK           CHAR(8)                          not null,
   MapLocationID        NUMBER                           not null,
   LowerBandwidth       FLOAT                            not null,
   ControlUnits         VARCHAR2(20)                     not null,
   ControlDelayTime     NUMBER                           not null,
   ControlSendRetries   NUMBER                           not null
)
/


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013476 primary key (SubstationBusID)
/


/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
   CurrentVarLoadPointID ASC
)
/


/*==============================================================*/
/* Table : CCFeederBankList                                     */
/*==============================================================*/


create table CCFeederBankList  (
   FeederID             NUMBER                           not null,
   DeviceID             NUMBER                           not null,
   ControlOrder         NUMBER                           not null
)
/


alter table CCFeederBankList
   add constraint PK_CCFEEDERBANKLIST primary key (FeederID, DeviceID)
/


/*==============================================================*/
/* Table : CCFeederSubAssignment                                */
/*==============================================================*/


create table CCFeederSubAssignment  (
   SubStationBusID      NUMBER                           not null,
   FeederID             NUMBER                           not null,
   DisplayOrder         NUMBER                           not null
)
/


alter table CCFeederSubAssignment
   add constraint PK_CCFEEDERSUBASSIGNMENT primary key (SubStationBusID, FeederID)
/


/*==============================================================*/
/* Table : CICustomerBase                                       */
/*==============================================================*/


create table CICustomerBase  (
   CustomerID           NUMBER                           not null,
   MainAddressID        NUMBER                           not null,
   CustomerDemandLevel  FLOAT                            not null,
   CurtailmentAgreement VARCHAR2(100)                    not null,
   CurtailAmount        FLOAT                            not null,
   CompanyName          VARCHAR2(80)                     not null
)
/


alter table CICustomerBase
   add constraint PK_CICUSTOMERBASE primary key (CustomerID)
/


/*==============================================================*/
/* Table : COLUMNTYPE                                           */
/*==============================================================*/


create table COLUMNTYPE  (
   TYPENUM              NUMBER                           not null,
   NAME                 VARCHAR2(20)                     not null
)
/


alter table COLUMNTYPE
   add constraint SYS_C0013414 primary key (TYPENUM)
/


/*==============================================================*/
/* Table : CTIDatabase                                          */
/*==============================================================*/


create table CTIDatabase  (
   Version              VARCHAR2(6)                      not null,
   CTIEmployeeName      VARCHAR2(30)                     not null,
   DateApplied          DATE,
   Notes                VARCHAR2(300),
   Build                NUMBER                           not null
)
/


alter table CTIDatabase
   add constraint PK_CTIDATABASE primary key (Version)
/


/*==============================================================*/
/* Table : CalcPointBaseline                                    */
/*==============================================================*/


create table CalcPointBaseline  (
   PointID              NUMBER                           not null,
   BaselineID           NUMBER                           not null
)
/


alter table CalcPointBaseline
   add constraint PK_CalcBsPt primary key (PointID)
/


/*==============================================================*/
/* Table : CapControlFeeder                                     */
/*==============================================================*/


create table CapControlFeeder  (
   FeederID             NUMBER                           not null,
   PeakSetPoint         FLOAT                            not null,
   OffPeakSetPoint      FLOAT                            not null,
   UpperBandwidth       FLOAT                            not null,
   CurrentVarLoadPointID NUMBER                           not null,
   CurrentWattLoadPointID NUMBER                           not null,
   MapLocationID        NUMBER                           not null,
   LowerBandwidth       FLOAT                            not null
)
/


alter table CapControlFeeder
   add constraint PK_CAPCONTROLFEEDER primary key (FeederID)
/


/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create index Indx_CPCNFDVARPT on CapControlFeeder (
   CurrentVarLoadPointID ASC
)
/


/*==============================================================*/
/* Table : CarrierRoute                                         */
/*==============================================================*/


create table CarrierRoute  (
   ROUTEID              NUMBER                           not null,
   BUSNUMBER            NUMBER                           not null,
   CCUFIXBITS           NUMBER                           not null,
   CCUVARIABLEBITS      NUMBER                           not null,
   UserLocked           CHAR(1)                          not null,
   ResetRptSettings     CHAR(1)                          not null
)
/


alter table CarrierRoute
   add constraint PK_CARRIERROUTE primary key (ROUTEID)
/


/*==============================================================*/
/* Table : CommErrorHistory                                     */
/*==============================================================*/


create table CommErrorHistory  (
   CommErrorID          NUMBER                           not null,
   PAObjectID           NUMBER                           not null,
   DateTime             DATE                             not null,
   SOE_Tag              NUMBER                           not null,
   ErrorType            NUMBER                           not null,
   ErrorNumber          NUMBER                           not null,
   Command              VARCHAR2(50)                     not null,
   OutMessage           VARCHAR2(160)                    not null,
   InMessage            VARCHAR2(160)                    not null
)
/


alter table CommErrorHistory
   add constraint PK_COMMERRORHISTORY primary key (CommErrorID)
/


/*==============================================================*/
/* Table : CommPort                                             */
/*==============================================================*/


create table CommPort  (
   PORTID               NUMBER                           not null,
   ALARMINHIBIT         VARCHAR2(1)                      not null,
   COMMONPROTOCOL       VARCHAR2(8)                      not null,
   PERFORMTHRESHOLD     NUMBER                           not null,
   PERFORMANCEALARM     VARCHAR2(1)                      not null,
   SharedPortType       VARCHAR2(20)                     not null,
   SharedSocketNumber   NUMBER                           not null
)
/


alter table CommPort
   add constraint SYS_C0013112 primary key (PORTID)
/


/*==============================================================*/
/* Table : Contact                                              */
/*==============================================================*/


create table Contact  (
   ContactID            NUMBER                           not null,
   ContFirstName        VARCHAR2(20)                     not null,
   ContLastName         VARCHAR2(32)                     not null,
   LogInID              NUMBER                           not null,
   AddressID            NUMBER                           not null
)
/


alter table Contact
   add constraint PK_CONTACT primary key (ContactID)
/


/*==============================================================*/
/* Table : ContactNotification                                  */
/*==============================================================*/


create table ContactNotification  (
   ContactNotifID       NUMBER                           not null,
   ContactID            NUMBER                           not null,
   NotificationCategoryID NUMBER                           not null,
   DisableFlag          CHAR(1)                          not null,
   Notification         VARCHAR2(130)                    not null
)
/


alter table ContactNotification
   add constraint PK_CONTACTNOTIFICATION primary key (ContactNotifID)
/


/*==============================================================*/
/* Table : Customer                                             */
/*==============================================================*/


create table Customer  (
   CustomerID           NUMBER                           not null,
   PrimaryContactID     NUMBER                           not null,
   CustomerTypeID       NUMBER                           not null,
   TimeZone             VARCHAR2(40)                     not null
)
/


alter table Customer
   add constraint PK_CUSTOMER primary key (CustomerID)
/


/*==============================================================*/
/* Table : CustomerAdditionalContact                            */
/*==============================================================*/


create table CustomerAdditionalContact  (
   CustomerID           NUMBER                           not null,
   ContactID            NUMBER                           not null
)
/


alter table CustomerAdditionalContact
   add constraint PK_CUSTOMERADDITIONALCONTACT primary key (ContactID, CustomerID)
/


/*==============================================================*/
/* Table : CustomerBaseLinePoint                                */
/*==============================================================*/


create table CustomerBaseLinePoint  (
   CustomerID           NUMBER                           not null,
   PointID              NUMBER                           not null
)
/


alter table CustomerBaseLinePoint
   add constraint PK_CUSTOMERBASELINEPOINT primary key (CustomerID, PointID)
/


/*==============================================================*/
/* Table : CustomerLoginSerialGroup                             */
/*==============================================================*/


create table CustomerLoginSerialGroup  (
   LoginID              NUMBER                           not null,
   LMGroupID            NUMBER                           not null
)
/


alter table CustomerLoginSerialGroup
   add constraint PK_CUSTOMERLOGINSERIALGROUP primary key (LoginID, LMGroupID)
/


/*==============================================================*/
/* Table : DEVICE                                               */
/*==============================================================*/


create table DEVICE  (
   DEVICEID             NUMBER                           not null,
   ALARMINHIBIT         VARCHAR2(1)                      not null,
   CONTROLINHIBIT       VARCHAR2(1)                      not null
)
/


alter table DEVICE
   add constraint PK_DEV_DEVICEID2 primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICE2WAYFLAGS                                      */
/*==============================================================*/


create table DEVICE2WAYFLAGS  (
   DEVICEID             NUMBER                           not null,
   MONTHLYSTATS         VARCHAR2(1)                      not null,
   TWENTYFOURHOURSTATS  VARCHAR2(1)                      not null,
   HOURLYSTATS          VARCHAR2(1)                      not null,
   FAILUREALARM         VARCHAR2(1)                      not null,
   PERFORMANCETHRESHOLD NUMBER                           not null,
   PERFORMANCEALARM     VARCHAR2(1)                      not null,
   PERFORMANCETWENTYFOURALARM VARCHAR2(1)                      not null
)
/


alter table DEVICE2WAYFLAGS
   add constraint PK_DEVICE2WAYFLAGS primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICECARRIERSETTINGS                                */
/*==============================================================*/


create table DEVICECARRIERSETTINGS  (
   DEVICEID             NUMBER                           not null,
   ADDRESS              NUMBER                           not null
)
/


alter table DEVICECARRIERSETTINGS
   add constraint PK_DEVICECARRIERSETTINGS primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICEDIALUPSETTINGS                                 */
/*==============================================================*/


create table DEVICEDIALUPSETTINGS  (
   DEVICEID             NUMBER                           not null,
   PHONENUMBER          VARCHAR2(40)                     not null,
   MINCONNECTTIME       NUMBER                           not null,
   MAXCONNECTTIME       NUMBER                           not null,
   LINESETTINGS         VARCHAR2(8)                      not null,
   BaudRate             NUMBER                           not null
)
/


alter table DEVICEDIALUPSETTINGS
   add constraint PK_DEVICEDIALUPSETTINGS primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICEIDLCREMOTE                                     */
/*==============================================================*/


create table DEVICEIDLCREMOTE  (
   DEVICEID             NUMBER                           not null,
   ADDRESS              NUMBER                           not null,
   POSTCOMMWAIT         NUMBER                           not null,
   CCUAmpUseType        VARCHAR2(20)                     not null
)
/


alter table DEVICEIDLCREMOTE
   add constraint PK_DEVICEIDLCREMOTE primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICEIED                                            */
/*==============================================================*/


create table DEVICEIED  (
   DEVICEID             NUMBER                           not null,
   PASSWORD             VARCHAR2(20)                     not null,
   SLAVEADDRESS         VARCHAR2(20)                     not null
)
/


alter table DEVICEIED
   add constraint PK_DEVICEIED primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICELOADPROFILE                                    */
/*==============================================================*/


create table DEVICELOADPROFILE  (
   DEVICEID             NUMBER                           not null,
   LASTINTERVALDEMANDRATE NUMBER                           not null,
   LOADPROFILEDEMANDRATE NUMBER                           not null,
   LOADPROFILECOLLECTION VARCHAR2(4)                      not null,
   VoltageDmdInterval   NUMBER                           not null,
   VoltageDmdRate       NUMBER                           not null
)
/


alter table DEVICELOADPROFILE
   add constraint PK_DEVICELOADPROFILE primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICEMCTIEDPORT                                     */
/*==============================================================*/


create table DEVICEMCTIEDPORT  (
   DEVICEID             NUMBER                           not null,
   CONNECTEDIED         VARCHAR2(20)                     not null,
   IEDSCANRATE          NUMBER                           not null,
   DEFAULTDATACLASS     NUMBER                           not null,
   DEFAULTDATAOFFSET    NUMBER                           not null,
   PASSWORD             VARCHAR2(6)                      not null,
   REALTIMESCAN         VARCHAR2(1)                      not null
)
/


alter table DEVICEMCTIEDPORT
   add constraint PK_DEVICEMCTIEDPORT primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICEMETERGROUP                                     */
/*==============================================================*/


create table DEVICEMETERGROUP  (
   DEVICEID             NUMBER                           not null,
   CollectionGroup      VARCHAR2(20)                     not null,
   TestCollectionGroup  VARCHAR2(20)                     not null,
   METERNUMBER          VARCHAR2(15)                     not null,
   BillingGroup         VARCHAR2(20)                     not null
)
/


alter table DEVICEMETERGROUP
   add constraint PK_DEVICEMETERGROUP primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DEVICESCANRATE                                       */
/*==============================================================*/


create table DEVICESCANRATE  (
   DEVICEID             NUMBER                           not null,
   SCANTYPE             VARCHAR2(20)                     not null,
   INTERVALRATE         NUMBER                           not null,
   SCANGROUP            NUMBER                           not null,
   AlternateRate        NUMBER                           not null
)
/


alter table DEVICESCANRATE
   add constraint PK_DEVICESCANRATE primary key (DEVICEID, SCANTYPE)
/


/*==============================================================*/
/* Table : DEVICETAPPAGINGSETTINGS                              */
/*==============================================================*/


create table DEVICETAPPAGINGSETTINGS  (
   DEVICEID             NUMBER                           not null,
   PAGERNUMBER          VARCHAR2(20)                     not null
)
/


alter table DEVICETAPPAGINGSETTINGS
   add constraint PK_DEVICETAPPAGINGSETTINGS primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DISPLAY                                              */
/*==============================================================*/


create table DISPLAY  (
   DISPLAYNUM           NUMBER                           not null,
   NAME                 VARCHAR2(40)                     not null,
   TYPE                 VARCHAR2(40)                     not null,
   TITLE                VARCHAR2(30),
   DESCRIPTION          VARCHAR2(200)
)
/


alter table DISPLAY
   add constraint SYS_C0013412 primary key (DISPLAYNUM)
/


/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique index Indx_DISPLAYNAME on DISPLAY (
   NAME ASC
)
/


/*==============================================================*/
/* Table : DISPLAY2WAYDATA                                      */
/*==============================================================*/


create table DISPLAY2WAYDATA  (
   DISPLAYNUM           NUMBER                           not null,
   ORDERING             NUMBER                           not null,
   POINTID              NUMBER                           not null
)
/


alter table DISPLAY2WAYDATA
   add constraint PK_DISPLAY2WAYDATA primary key (DISPLAYNUM, ORDERING)
/


/*==============================================================*/
/* Table : DISPLAYCOLUMNS                                       */
/*==============================================================*/


create table DISPLAYCOLUMNS  (
   DISPLAYNUM           NUMBER                           not null,
   TITLE                VARCHAR2(50)                     not null,
   TYPENUM              NUMBER                           not null,
   ORDERING             NUMBER                           not null,
   WIDTH                NUMBER                           not null
)
/


alter table DISPLAYCOLUMNS
   add constraint PK_DISPLAYCOLUMNS primary key (DISPLAYNUM, TITLE)
/


/*==============================================================*/
/* Table : DYNAMICACCUMULATOR                                   */
/*==============================================================*/


create table DYNAMICACCUMULATOR  (
   POINTID              NUMBER                           not null,
   PREVIOUSPULSES       NUMBER                           not null,
   PRESENTPULSES        NUMBER                           not null
)
/


alter table DYNAMICACCUMULATOR
   add constraint PK_DYNAMICACCUMULATOR primary key (POINTID)
/


/*==============================================================*/
/* Table : DYNAMICDEVICESCANDATA                                */
/*==============================================================*/


create table DYNAMICDEVICESCANDATA  (
   DEVICEID             NUMBER                           not null,
   LASTFREEZETIME       DATE                             not null,
   PREVFREEZETIME       DATE                             not null,
   LASTLPTIME           DATE                             not null,
   LASTFREEZENUMBER     NUMBER                           not null,
   PREVFREEZENUMBER     NUMBER                           not null,
   NEXTSCAN0            DATE                             not null,
   NEXTSCAN1            DATE                             not null,
   NEXTSCAN2            DATE                             not null,
   NEXTSCAN3            DATE                             not null
)
/


alter table DYNAMICDEVICESCANDATA
   add constraint PK_DYNAMICDEVICESCANDATA primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DYNAMICPOINTDISPATCH                                 */
/*==============================================================*/


create table DYNAMICPOINTDISPATCH  (
   POINTID              NUMBER                           not null,
   TIMESTAMP            DATE                             not null,
   QUALITY              NUMBER                           not null,
   VALUE                FLOAT                            not null,
   TAGS                 NUMBER                           not null,
   NEXTARCHIVE          DATE                             not null,
   STALECOUNT           NUMBER                           not null,
   LastAlarmLogID       NUMBER                           not null,
   millis               SMALLINT                         not null
)
/


alter table DYNAMICPOINTDISPATCH
   add constraint PK_DYNAMICPOINTDISPATCH primary key (POINTID)
/


/*==============================================================*/
/* Table : DateOfHoliday                                        */
/*==============================================================*/


create table DateOfHoliday  (
   HolidayScheduleID    NUMBER                           not null,
   HolidayName          VARCHAR2(20)                     not null,
   HolidayMonth         NUMBER                           not null,
   HolidayDay           NUMBER                           not null,
   HolidayYear          NUMBER                           not null
)
/


alter table DateOfHoliday
   add constraint PK_DATEOFHOLIDAY primary key (HolidayScheduleID, HolidayName)
/


/*==============================================================*/
/* Table : DateOfSeason                                         */
/*==============================================================*/


create table DateOfSeason  (
   SeasonScheduleID     NUMBER                           not null,
   SeasonName           VARCHAR2(20)                     not null,
   SeasonStartMonth     NUMBER                           not null,
   SeasonStartDay       NUMBER                           not null,
   SeasonEndMonth       NUMBER                           not null,
   SeasonEndDay         NUMBER                           not null
)
/


alter table DateOfSeason
   add constraint PK_DATEOFSEASON primary key (SeasonScheduleID, SeasonName)
/


/*==============================================================*/
/* Table : DeviceAddress                                        */
/*==============================================================*/


create table DeviceAddress  (
   DeviceID             NUMBER                           not null,
   MasterAddress        NUMBER                           not null,
   SlaveAddress         NUMBER                           not null,
   PostCommWait         NUMBER                           not null
)
/


alter table DeviceAddress
   add constraint PK_DEVICEADDRESS primary key (DeviceID)
/


/*==============================================================*/
/* Table : DeviceCBC                                            */
/*==============================================================*/


create table DeviceCBC  (
   DEVICEID             NUMBER                           not null,
   SERIALNUMBER         NUMBER                           not null,
   ROUTEID              NUMBER                           not null
)
/


alter table DeviceCBC
   add constraint PK_DEVICECBC primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DeviceCustomerList                                   */
/*==============================================================*/


create table DeviceCustomerList  (
   CustomerID           NUMBER                           not null,
   DeviceID             NUMBER                           not null
)
/


alter table DeviceCustomerList
   add constraint PK_DEVICECUSTOMERLIST primary key (DeviceID, CustomerID)
/


/*==============================================================*/
/* Table : DeviceDirectCommSettings                             */
/*==============================================================*/


create table DeviceDirectCommSettings  (
   DEVICEID             NUMBER                           not null,
   PORTID               NUMBER                           not null
)
/


alter table DeviceDirectCommSettings
   add constraint PK_DEVICEDIRECTCOMMSETTINGS primary key (DEVICEID)
/


/*==============================================================*/
/* Table : DeviceRTC                                            */
/*==============================================================*/


create table DeviceRTC  (
   DeviceID             NUMBER                           not null,
   RTCAddress           NUMBER                           not null,
   Response             VARCHAR2(1)                      not null,
   LBTMode              NUMBER                           not null,
   DisableVerifies      VARCHAR2(1)                      not null
)
/


alter table DeviceRTC
   add constraint PK_DEVICERTC primary key (DeviceID)
/


/*==============================================================*/
/* Table : DeviceRoutes                                         */
/*==============================================================*/


create table DeviceRoutes  (
   DEVICEID             NUMBER                           not null,
   ROUTEID              NUMBER                           not null
)
/


alter table DeviceRoutes
   add constraint PK_DEVICEROUTES primary key (DEVICEID, ROUTEID)
/


/*==============================================================*/
/* Table : DeviceSeries5RTU                                     */
/*==============================================================*/


create table DeviceSeries5RTU  (
   DeviceID             NUMBER                           not null,
   TickTime             NUMBER                           not null,
   TransmitOffset       NUMBER                           not null,
   SaveHistory          CHAR(1)                          not null,
   PowerValueHighLimit  NUMBER                           not null,
   PowerValueLowLimit   NUMBER                           not null,
   PowerValueMultiplier FLOAT                            not null,
   PowerValueOffset     FLOAT                            not null,
   StartCode            NUMBER                           not null,
   StopCode             NUMBER                           not null,
   Retries              NUMBER                           not null
)
/


alter table DeviceSeries5RTU
   add constraint PK_DEVICESERIES5RTU primary key (DeviceID)
/


/*==============================================================*/
/* Table : DeviceVerification                                   */
/*==============================================================*/


create table DeviceVerification  (
   ReceiverID           NUMBER                           not null,
   TransmitterID        NUMBER                           not null,
   ResendOnFail         CHAR(1)                          not null,
   Disable              CHAR(1)                          not null
)
/


alter table DeviceVerification
   add constraint PK_DEVICEVERIFICATION primary key (ReceiverID, TransmitterID)
/


/*==============================================================*/
/* Table : DeviceWindow                                         */
/*==============================================================*/


create table DeviceWindow  (
   DeviceID             NUMBER                           not null,
   Type                 VARCHAR2(20)                     not null,
   WinOpen              NUMBER                           not null,
   WinClose             NUMBER                           not null,
   AlternateOpen        NUMBER                           not null,
   AlternateClose       NUMBER                           not null
)
/


alter table DeviceWindow
   add constraint PK_DEVICEWINDOW primary key (DeviceID, Type)
/


/*==============================================================*/
/* Table : DynamicCCCapBank                                     */
/*==============================================================*/


create table DynamicCCCapBank  (
   CapBankID            NUMBER                           not null,
   ControlStatus        NUMBER                           not null,
   CurrentDailyOperations NUMBER                           not null,
   LastStatusChangeTime DATE                             not null,
   TagsControlStatus    NUMBER                           not null,
   CTITimeStamp         DATE                             not null,
   OriginalFeederID     NUMBER                           not null,
   OriginalSwitchingOrder NUMBER                           not null
)
/


alter table DynamicCCCapBank
   add constraint PK_DYNAMICCCCAPBANK primary key (CapBankID)
/


/*==============================================================*/
/* Table : DynamicCCFeeder                                      */
/*==============================================================*/


create table DynamicCCFeeder  (
   FeederID             NUMBER                           not null,
   CurrentVarPointValue FLOAT                            not null,
   CurrentWattPointValue FLOAT                            not null,
   NewPointDataReceivedFlag CHAR(1)                          not null,
   LastCurrentVarUpdateTime DATE                             not null,
   EstimatedVarPointValue FLOAT                            not null,
   CurrentDailyOperations NUMBER                           not null,
   RecentlyControlledFlag CHAR(1)                          not null,
   LastOperationTime    DATE                             not null,
   VarValueBeforeControl FLOAT                            not null,
   LastCapBankDeviceID  NUMBER                           not null,
   BusOptimizedVarCategory NUMBER                           not null,
   BusOptimizedVarOffset FLOAT                            not null,
   CTITimeStamp         DATE                             not null,
   PowerFactorValue     FLOAT                            not null,
   KvarSolution         FLOAT                            not null,
   EstimatedPFValue     FLOAT                            not null,
   CurrentVarPointQuality NUMBER                           not null,
   WaiveControlFlag     CHAR(1)                          not null
)
/


alter table DynamicCCFeeder
   add constraint PK_DYNAMICCCFEEDER primary key (FeederID)
/


/*==============================================================*/
/* Table : DynamicCCSubstationBus                               */
/*==============================================================*/


create table DynamicCCSubstationBus  (
   SubstationBusID      NUMBER                           not null,
   CurrentVarPointValue FLOAT                            not null,
   CurrentWattPointValue FLOAT                            not null,
   NextCheckTime        DATE                             not null,
   NewPointDataReceivedFlag CHAR(1)                          not null,
   BusUpdatedFlag       CHAR(1)                          not null,
   LastCurrentVarUpdateTime DATE                             not null,
   EstimatedVarPointValue FLOAT                            not null,
   CurrentDailyOperations NUMBER                           not null,
   PeakTimeFlag         CHAR(1)                          not null,
   RecentlyControlledFlag CHAR(1)                          not null,
   LastOperationTime    DATE                             not null,
   VarValueBeforeControl FLOAT                            not null,
   LastFeederPAOid      NUMBER                           not null,
   LastFeederPosition   NUMBER                           not null,
   CTITimeStamp         DATE                             not null,
   PowerFactorValue     FLOAT                            not null,
   KvarSolution         FLOAT                            not null,
   EstimatedPFValue     FLOAT                            not null,
   CurrentVarPointQuality NUMBER                           not null,
   WaiveControlFlag     CHAR(1)                          not null
)
/


alter table DynamicCCSubstationBus
   add constraint PK_DYNAMICCCSUBSTATIONBUS primary key (SubstationBusID)
/


/*==============================================================*/
/* Table : DynamicCalcHistorical                                */
/*==============================================================*/


create table DynamicCalcHistorical  (
   PointID              NUMBER                           not null,
   LastUpdate           DATE                             not null
)
/


alter table DynamicCalcHistorical
   add constraint PK_DYNAMICCALCHISTORICAL primary key (PointID)
/


/*==============================================================*/
/* Table : DynamicLMControlArea                                 */
/*==============================================================*/


create table DynamicLMControlArea  (
   DeviceID             NUMBER                           not null,
   NextCheckTime        DATE                             not null,
   NewPointDataReceivedFlag CHAR(1)                          not null,
   UpdatedFlag          CHAR(1)                          not null,
   ControlAreaState     NUMBER                           not null,
   CurrentPriority      NUMBER                           not null,
   TimeStamp            DATE                             not null,
   CurrentDailyStartTime NUMBER                           not null,
   CurrentDailyStopTime NUMBER                           not null
)
/


alter table DynamicLMControlArea
   add constraint PK_DYNAMICLMCONTROLAREA primary key (DeviceID)
/


/*==============================================================*/
/* Table : DynamicLMControlAreaTrigger                          */
/*==============================================================*/


create table DynamicLMControlAreaTrigger  (
   DeviceID             NUMBER                           not null,
   TriggerNumber        NUMBER                           not null,
   PointValue           FLOAT                            not null,
   LastPointValueTimeStamp DATE                             not null,
   PeakPointValue       FLOAT                            not null,
   LastPeakPointValueTimeStamp DATE                             not null
)
/


alter table DynamicLMControlAreaTrigger
   add constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key (DeviceID, TriggerNumber)
/


/*==============================================================*/
/* Table : DynamicLMGroup                                       */
/*==============================================================*/


create table DynamicLMGroup  (
   DeviceID             NUMBER                           not null,
   GroupControlState    NUMBER                           not null,
   CurrentHoursDaily    NUMBER                           not null,
   CurrentHoursMonthly  NUMBER                           not null,
   CurrentHoursSeasonal NUMBER                           not null,
   CurrentHoursAnnually NUMBER                           not null,
   LastControlSent      DATE                             not null,
   TimeStamp            DATE                             not null,
   LMProgramID          NUMBER                           not null,
   ControlStartTime     DATE                             not null,
   ControlCompleteTime  DATE                             not null,
   NextControlTime      DATE                             not null,
   InternalState        NUMBER                           not null
)
/


alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key (DeviceID, LMProgramID)
/


/*==============================================================*/
/* Table : DynamicLMProgram                                     */
/*==============================================================*/


create table DynamicLMProgram  (
   DeviceID             NUMBER                           not null,
   ProgramState         NUMBER                           not null,
   ReductionTotal       FLOAT                            not null,
   StartedControlling   DATE                             not null,
   LastControlSent      DATE                             not null,
   ManualControlReceivedFlag CHAR(1)                          not null,
   TimeStamp            DATE                             not null
)
/


alter table DynamicLMProgram
   add constraint PK_DYNAMICLMPROGRAM primary key (DeviceID)
/


/*==============================================================*/
/* Table : DynamicLMProgramDirect                               */
/*==============================================================*/


create table DynamicLMProgramDirect  (
   DeviceID             NUMBER                           not null,
   CurrentGearNumber    NUMBER                           not null,
   LastGroupControlled  NUMBER                           not null,
   StartTime            DATE                             not null,
   StopTime             DATE                             not null,
   TimeStamp            DATE                             not null,
   DailyOps             NUMBER                           not null,
   NotifyTime           DATE                             not null,
   StartedRampingOut    DATE                             not null
)
/


alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key (DeviceID)
/


/*==============================================================*/
/* Table : DynamicPAOStatistics                                 */
/*==============================================================*/


create table DynamicPAOStatistics  (
   PAOBjectID           NUMBER                           not null,
   StatisticType        VARCHAR2(16)                     not null,
   Requests             NUMBER                           not null,
   Completions          NUMBER                           not null,
   Attempts             NUMBER                           not null,
   CommErrors           NUMBER                           not null,
   ProtocolErrors       NUMBER                           not null,
   SystemErrors         NUMBER                           not null,
   StartDateTime        DATE                             not null,
   StopDateTime         DATE                             not null
)
/


alter table DynamicPAOStatistics
   add constraint PK_DYNAMICPAOSTATISTICS primary key (PAOBjectID, StatisticType)
/


/*==============================================================*/
/* Table : DynamicPointAlarming                                 */
/*==============================================================*/


create table DynamicPointAlarming  (
   PointID              NUMBER                           not null,
   AlarmCondition       NUMBER                           not null,
   CategoryID           NUMBER                           not null,
   AlarmTime            DATE                             not null,
   Action               VARCHAR2(60)                     not null,
   Description          VARCHAR2(120)                    not null,
   Tags                 NUMBER                           not null,
   LogID                NUMBER                           not null,
   SOE_TAG              NUMBER                           not null,
   Type                 NUMBER                           not null,
   UserName             VARCHAR2(30)                     not null
)
/


alter table DynamicPointAlarming
   add constraint PK_DYNAMICPOINTALARMING primary key (PointID, AlarmCondition)
/


/*==============================================================*/
/* Table : DynamicTags                                          */
/*==============================================================*/


create table DynamicTags  (
   InstanceID           NUMBER                           not null,
   PointID              NUMBER                           not null,
   TagID                NUMBER                           not null,
   UserName             VARCHAR2(60)                     not null,
   Action               VARCHAR2(20)                     not null,
   Description          VARCHAR2(120)                    not null,
   TagTime              DATE                             not null,
   RefStr               VARCHAR2(60)                     not null,
   ForStr               VARCHAR2(60)                     not null
)
/


alter table DynamicTags
   add constraint PK_DYNAMICTAGS primary key (InstanceID)
/


/*==============================================================*/
/* Table : DynamicVerification                                  */
/*==============================================================*/


create table DynamicVerification  (
   LogID                NUMBER                           not null,
   TimeArrival          DATE                             not null,
   ReceiverID           NUMBER                           not null,
   TransmitterID        NUMBER                           not null,
   Command              VARCHAR2(256)                    not null,
   Code                 VARCHAR2(128)                    not null,
   CodeSequence         NUMBER                           not null,
   Received             CHAR(1)                          not null,
   CodeStatus           VARCHAR2(32)                     not null
)
/


alter table DynamicVerification
   add constraint PK_DYNAMICVERIFICATION primary key (LogID)
/


/*==============================================================*/
/* Table : EnergyCompany                                        */
/*==============================================================*/


create table EnergyCompany  (
   EnergyCompanyID      NUMBER                           not null,
   Name                 VARCHAR2(60)                     not null,
   PrimaryContactID     NUMBER                           not null,
   UserID               NUMBER                           not null
)
/


alter table EnergyCompany
   add constraint PK_ENERGYCOMPANY primary key (EnergyCompanyID)
/


/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique index Indx_EnCmpName on EnergyCompany (
   Name ASC
)
/


/*==============================================================*/
/* Table : EnergyCompanyCustomerList                            */
/*==============================================================*/


create table EnergyCompanyCustomerList  (
   EnergyCompanyID      NUMBER                           not null,
   CustomerID           NUMBER                           not null
)
/


alter table EnergyCompanyCustomerList
   add constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key (EnergyCompanyID, CustomerID)
/


/*==============================================================*/
/* Table : EnergyCompanyOperatorLoginList                       */
/*==============================================================*/


create table EnergyCompanyOperatorLoginList  (
   EnergyCompanyID      NUMBER                           not null,
   OperatorLoginID      NUMBER                           not null
)
/


alter table EnergyCompanyOperatorLoginList
   add constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key (EnergyCompanyID, OperatorLoginID)
/


/*==============================================================*/
/* Table : FDRInterface                                         */
/*==============================================================*/


create table FDRInterface  (
   InterfaceID          NUMBER                           not null,
   InterfaceName        VARCHAR2(30)                     not null,
   PossibleDirections   VARCHAR2(100)                    not null,
   hasDestination       CHAR(1)                          not null
)
/


alter table FDRInterface
   add constraint PK_FDRINTERFACE primary key (InterfaceID)
/


/*==============================================================*/
/* Table : FDRInterfaceOption                                   */
/*==============================================================*/


create table FDRInterfaceOption  (
   InterfaceID          NUMBER                           not null,
   OptionLabel          VARCHAR2(20)                     not null,
   Ordering             NUMBER                           not null,
   OptionType           VARCHAR2(8)                      not null,
   OptionValues         VARCHAR2(150)                    not null
)
/


alter table FDRInterfaceOption
   add constraint PK_FDRINTERFACEOPTION primary key (InterfaceID, Ordering)
/


/*==============================================================*/
/* Table : FDRTRANSLATION                                       */
/*==============================================================*/


create table FDRTRANSLATION  (
   POINTID              NUMBER                           not null,
   DIRECTIONTYPE        VARCHAR2(20)                     not null,
   InterfaceType        VARCHAR2(20)                     not null,
   DESTINATION          VARCHAR2(20)                     not null,
   TRANSLATION          VARCHAR2(100)                    not null
)
/


alter table FDRTRANSLATION
   add constraint PK_FDRTrans primary key (POINTID, InterfaceType, TRANSLATION)
/


/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create index Indx_FdrTransIntTyp on FDRTRANSLATION (
   InterfaceType ASC
)
/


/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create index Indx_FdrTrnsIntTypDir on FDRTRANSLATION (
   DIRECTIONTYPE ASC,
   InterfaceType ASC
)
/


/*==============================================================*/
/* Table : GRAPHDATASERIES                                      */
/*==============================================================*/


create table GRAPHDATASERIES  (
   GRAPHDATASERIESID    NUMBER                           not null,
   GRAPHDEFINITIONID    NUMBER                           not null,
   POINTID              NUMBER                           not null,
   Label                VARCHAR2(40)                     not null,
   Axis                 CHAR(1)                          not null,
   Color                NUMBER                           not null,
   Type                 NUMBER                           not null,
   Multiplier           FLOAT                            not null,
   Renderer             SMALLINT                         not null,
   MoreData             VARCHAR2(100)                    not null
)
/


alter table GRAPHDATASERIES
   add constraint SYS_GrphDserID primary key (GRAPHDATASERIESID)
/


/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create index Indx_GrpDSerPtID on GRAPHDATASERIES (
   POINTID ASC
)
/


/*==============================================================*/
/* Table : GRAPHDEFINITION                                      */
/*==============================================================*/


create table GRAPHDEFINITION  (
   GRAPHDEFINITIONID    NUMBER                           not null,
   NAME                 VARCHAR2(40)                     not null,
   AutoScaleTimeAxis    CHAR(1)                          not null,
   AutoScaleLeftAxis    CHAR(1)                          not null,
   AutoScaleRightAxis   CHAR(1)                          not null,
   STARTDATE            DATE                             not null,
   STOPDATE             DATE                             not null,
   LeftMin              FLOAT                            not null,
   LeftMax              FLOAT                            not null,
   RightMin             FLOAT                            not null,
   RightMax             FLOAT                            not null,
   Type                 CHAR(1)                          not null
)
/


alter table GRAPHDEFINITION
   add constraint SYS_C0015109 primary key (GRAPHDEFINITIONID)
/


alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique (NAME)
/


/*==============================================================*/
/* Table : GatewayEndDevice                                     */
/*==============================================================*/


create table GatewayEndDevice  (
   SerialNumber         VARCHAR2(30)                     not null,
   HardwareType         NUMBER                           not null,
   DataType             NUMBER                           not null,
   DataValue            VARCHAR2(100)
)
/


alter table GatewayEndDevice
   add constraint PK_GATEWAYENDDEVICE primary key (SerialNumber, HardwareType, DataType)
/


/*==============================================================*/
/* Table : GenericMacro                                         */
/*==============================================================*/


create table GenericMacro  (
   OwnerID              NUMBER                           not null,
   ChildID              NUMBER                           not null,
   ChildOrder           NUMBER                           not null,
   MacroType            VARCHAR2(20)                     not null
)
/


alter table GenericMacro
   add constraint PK_GENERICMACRO primary key (OwnerID, ChildOrder, MacroType)
/


/*==============================================================*/
/* Table : GraphCustomerList                                    */
/*==============================================================*/


create table GraphCustomerList  (
   GraphDefinitionID    NUMBER                           not null,
   CustomerID           NUMBER                           not null,
   CustomerOrder        NUMBER                           not null
)
/


alter table GraphCustomerList
   add constraint PK_GRAPHCUSTOMERLIST primary key (GraphDefinitionID, CustomerID)
/


/*==============================================================*/
/* Table : HolidaySchedule                                      */
/*==============================================================*/


create table HolidaySchedule  (
   HolidayScheduleID    NUMBER                           not null,
   HolidayScheduleName  VARCHAR2(40)                     not null
)
/


alter table HolidaySchedule
   add constraint PK_HOLIDAYSCHEDULE primary key (HolidayScheduleID)
/


/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique index Indx_HolSchName on HolidaySchedule (
   HolidayScheduleName ASC
)
/


/*==============================================================*/
/* Table : LMCONTROLAREAPROGRAM                                 */
/*==============================================================*/


create table LMCONTROLAREAPROGRAM  (
   DEVICEID             NUMBER                           not null,
   LMPROGRAMDEVICEID    NUMBER                           not null,
   StartPriority        NUMBER                           not null,
   StopPriority         NUMBER                           not null
)
/


alter table LMCONTROLAREAPROGRAM
   add constraint PK_LMCONTROLAREAPROGRAM primary key (DEVICEID, LMPROGRAMDEVICEID)
/


/*==============================================================*/
/* Table : LMCONTROLAREATRIGGER                                 */
/*==============================================================*/


create table LMCONTROLAREATRIGGER  (
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
   PEAKPOINTID          NUMBER                           not null
)
/


alter table LMCONTROLAREATRIGGER
   add constraint PK_LMCONTROLAREATRIGGER primary key (DEVICEID, TRIGGERNUMBER)
/


/*==============================================================*/
/* Table : LMControlArea                                        */
/*==============================================================*/


create table LMControlArea  (
   DEVICEID             NUMBER                           not null,
   DEFOPERATIONALSTATE  VARCHAR2(20)                     not null,
   CONTROLINTERVAL      NUMBER                           not null,
   MINRESPONSETIME      NUMBER                           not null,
   DEFDAILYSTARTTIME    NUMBER                           not null,
   DEFDAILYSTOPTIME     NUMBER                           not null,
   REQUIREALLTRIGGERSACTIVEFLAG VARCHAR2(1)                      not null
)
/


alter table LMControlArea
   add constraint PK_LMCONTROLAREA primary key (DEVICEID)
/


/*==============================================================*/
/* Table : LMControlHistory                                     */
/*==============================================================*/


create table LMControlHistory  (
   LMCtrlHistID         NUMBER                           not null,
   PAObjectID           NUMBER                           not null,
   StartDateTime        DATE                             not null,
   SOE_Tag              NUMBER                           not null,
   ControlDuration      NUMBER                           not null,
   ControlType          VARCHAR2(20)                     not null,
   CurrentDailyTime     NUMBER                           not null,
   CurrentMonthlyTime   NUMBER                           not null,
   CurrentSeasonalTime  NUMBER                           not null,
   CurrentAnnualTime    NUMBER                           not null,
   ActiveRestore        CHAR(1)                          not null,
   ReductionValue       FLOAT                            not null,
   StopDateTime         DATE                             not null
)
/


alter table LMControlHistory
   add constraint PK_LMCONTROLHISTORY primary key (LMCtrlHistID)
/


/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create index Indx_Start on LMControlHistory (
   StartDateTime ASC
)
/


/*==============================================================*/
/* Table : LMControlScenarioProgram                             */
/*==============================================================*/


create table LMControlScenarioProgram  (
   ScenarioID           NUMBER                           not null,
   ProgramID            NUMBER                           not null,
   StartOffset          NUMBER                           not null,
   StopOffset           NUMBER                           not null,
   StartGear            NUMBER                           not null
)
/


alter table LMControlScenarioProgram
   add constraint PK_LMCONTROLSCENARIOPROGRAM primary key (ScenarioID, ProgramID)
/


/*==============================================================*/
/* Table : LMCurtailCustomerActivity                            */
/*==============================================================*/


create table LMCurtailCustomerActivity  (
   CustomerID           NUMBER                           not null,
   CurtailReferenceID   NUMBER                           not null,
   AcknowledgeStatus    VARCHAR2(30)                     not null,
   AckDateTime          DATE                             not null,
   IPAddressOfAckUser   VARCHAR2(15)                     not null,
   UserIDName           VARCHAR2(40)                     not null,
   NameOfAckPerson      VARCHAR2(40)                     not null,
   CurtailmentNotes     VARCHAR2(120)                    not null,
   CurrentPDL           FLOAT                            not null,
   AckLateFlag          CHAR(1)                          not null
)
/


alter table LMCurtailCustomerActivity
   add constraint PK_LMCURTAILCUSTOMERACTIVITY primary key (CustomerID, CurtailReferenceID)
/


/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create index Index_LMCrtCstActID on LMCurtailCustomerActivity (
   CustomerID ASC
)
/


/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
   AcknowledgeStatus ASC
)
/


/*==============================================================*/
/* Table : LMCurtailProgramActivity                             */
/*==============================================================*/


create table LMCurtailProgramActivity  (
   DeviceID             NUMBER                           not null,
   CurtailReferenceID   NUMBER                           not null,
   ActionDateTime       DATE                             not null,
   NotificationDateTime DATE                             not null,
   CurtailmentStartTime DATE                             not null,
   CurtailmentStopTime  DATE                             not null,
   RunStatus            VARCHAR2(20)                     not null,
   AdditionalInfo       VARCHAR2(100)                    not null
)
/


alter table LMCurtailProgramActivity
   add constraint PK_LMCURTAILPROGRAMACTIVITY primary key (CurtailReferenceID)
/


/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
   CurtailmentStartTime ASC
)
/


/*==============================================================*/
/* Table : LMDirectCustomerList                                 */
/*==============================================================*/


create table LMDirectCustomerList  (
   ProgramID            NUMBER                           not null,
   CustomerID           NUMBER                           not null
)
/


alter table LMDirectCustomerList
   add constraint PK_LMDIRECTCUSTOMERLIST primary key (ProgramID, CustomerID)
/


/*==============================================================*/
/* Table : LMDirectNotifGrpList                                 */
/*==============================================================*/


create table LMDirectNotifGrpList  (
   ProgramID            NUMBER                           not null,
   NotificationGrpID    NUMBER                           not null
)
/


alter table LMDirectNotifGrpList
   add constraint PK_LMDIRECTNOTIFGRPLIST primary key (ProgramID, NotificationGrpID)
/


/*==============================================================*/
/* Table : LMDirectOperatorList                                 */
/*==============================================================*/


create table LMDirectOperatorList  (
   ProgramID            NUMBER                           not null,
   OperatorLoginID      NUMBER                           not null
)
/


alter table LMDirectOperatorList
   add constraint PK_LMDIRECTOPERATORLIST primary key (ProgramID, OperatorLoginID)
/


/*==============================================================*/
/* Table : LMEnergyExchangeCustomerList                         */
/*==============================================================*/


create table LMEnergyExchangeCustomerList  (
   ProgramID            NUMBER                           not null,
   CustomerID           NUMBER                           not null,
   CustomerOrder        NUMBER                           not null
)
/


alter table LMEnergyExchangeCustomerList
   add constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key (ProgramID, CustomerID)
/


/*==============================================================*/
/* Table : LMEnergyExchangeCustomerReply                        */
/*==============================================================*/


create table LMEnergyExchangeCustomerReply  (
   CustomerID           NUMBER                           not null,
   OfferID              NUMBER                           not null,
   AcceptStatus         VARCHAR2(30)                     not null,
   AcceptDateTime       DATE                             not null,
   RevisionNumber       NUMBER                           not null,
   IPAddressOfAcceptUser VARCHAR2(15)                     not null,
   UserIDName           VARCHAR2(40)                     not null,
   NameOfAcceptPerson   VARCHAR2(40)                     not null,
   EnergyExchangeNotes  VARCHAR2(120)                    not null
)
/


alter table LMEnergyExchangeCustomerReply
   add constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key (CustomerID, OfferID, RevisionNumber)
/


/*==============================================================*/
/* Table : LMEnergyExchangeHourlyCustomer                       */
/*==============================================================*/


create table LMEnergyExchangeHourlyCustomer  (
   CustomerID           NUMBER                           not null,
   OfferID              NUMBER                           not null,
   RevisionNumber       NUMBER                           not null,
   Hour                 NUMBER                           not null,
   AmountCommitted      FLOAT                            not null
)
/


alter table LMEnergyExchangeHourlyCustomer
   add constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key (CustomerID, OfferID, RevisionNumber, Hour)
/


/*==============================================================*/
/* Table : LMEnergyExchangeHourlyOffer                          */
/*==============================================================*/


create table LMEnergyExchangeHourlyOffer  (
   OfferID              NUMBER                           not null,
   RevisionNumber       NUMBER                           not null,
   Hour                 NUMBER                           not null,
   Price                NUMBER                           not null,
   AmountRequested      FLOAT                            not null
)
/


alter table LMEnergyExchangeHourlyOffer
   add constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key (OfferID, RevisionNumber, Hour)
/


/*==============================================================*/
/* Table : LMEnergyExchangeOfferRevision                        */
/*==============================================================*/


create table LMEnergyExchangeOfferRevision  (
   OfferID              NUMBER                           not null,
   RevisionNumber       NUMBER                           not null,
   ActionDateTime       DATE                             not null,
   NotificationDateTime DATE                             not null,
   OfferExpirationDateTime DATE                             not null,
   AdditionalInfo       VARCHAR2(100)                    not null
)
/


alter table LMEnergyExchangeOfferRevision
   add constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key (OfferID, RevisionNumber)
/


/*==============================================================*/
/* Table : LMEnergyExchangeProgramOffer                         */
/*==============================================================*/


create table LMEnergyExchangeProgramOffer  (
   DeviceID             NUMBER                           not null,
   OfferID              NUMBER                           not null,
   RunStatus            VARCHAR2(20)                     not null,
   OfferDate            DATE                             not null
)
/


alter table LMEnergyExchangeProgramOffer
   add constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key (OfferID)
/


/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/


create table LMGroup  (
   DeviceID             NUMBER                           not null,
   KWCapacity           FLOAT                            not null
)
/


alter table LMGroup
   add constraint PK_LMGROUP primary key (DeviceID)
/


/*==============================================================*/
/* Table : LMGroupEmetcon                                       */
/*==============================================================*/


create table LMGroupEmetcon  (
   DEVICEID             NUMBER                           not null,
   GOLDADDRESS          NUMBER                           not null,
   SILVERADDRESS        NUMBER                           not null,
   ADDRESSUSAGE         CHAR(1)                          not null,
   RELAYUSAGE           CHAR(1)                          not null,
   ROUTEID              NUMBER                           not null
)
/


alter table LMGroupEmetcon
   add constraint PK_LMGROUPEMETCON primary key (DEVICEID)
/


/*==============================================================*/
/* Table : LMGroupExpressCom                                    */
/*==============================================================*/


create table LMGroupExpressCom  (
   LMGroupID            NUMBER                           not null,
   RouteID              NUMBER                           not null,
   SerialNumber         VARCHAR2(10)                     not null,
   ServiceProviderID    NUMBER                           not null,
   GeoID                NUMBER                           not null,
   SubstationID         NUMBER                           not null,
   FeederID             NUMBER                           not null,
   ZipCodeAddress       NUMBER                           not null,
   UDAddress            NUMBER                           not null,
   ProgramID            NUMBER                           not null,
   SplinterAddress      NUMBER                           not null,
   AddressUsage         VARCHAR2(10)                     not null,
   RelayUsage           CHAR(15)                         not null
)
/


alter table LMGroupExpressCom
   add constraint PK_LMGROUPEXPRESSCOM primary key (LMGroupID)
/


/*==============================================================*/
/* Table : LMGroupExpressComAddress                             */
/*==============================================================*/


create table LMGroupExpressComAddress  (
   AddressID            NUMBER                           not null,
   AddressType          VARCHAR2(20)                     not null,
   Address              NUMBER                           not null,
   AddressName          VARCHAR2(30)                     not null
)
/


alter table LMGroupExpressComAddress
   add constraint PK_LMGROUPEXPRESSCOMADDRESS primary key (AddressID)
/


/*==============================================================*/
/* Table : LMGroupMCT                                           */
/*==============================================================*/


create table LMGroupMCT  (
   DeviceID             NUMBER                           not null,
   MCTAddress           NUMBER                           not null,
   MCTLevel             CHAR(1)                          not null,
   RelayUsage           CHAR(7)                          not null,
   RouteID              NUMBER                           not null,
   MCTDeviceID          NUMBER                           not null
)
/


alter table LMGroupMCT
   add constraint PK_LMGrpMCTPK primary key (DeviceID)
/


/*==============================================================*/
/* Table : LMGroupPoint                                         */
/*==============================================================*/


create table LMGroupPoint  (
   DEVICEID             NUMBER                           not null,
   DeviceIDUsage        NUMBER                           not null,
   PointIDUsage         NUMBER                           not null,
   StartControlRawState NUMBER                           not null
)
/


alter table LMGroupPoint
   add constraint PK_LMGROUPPOINT primary key (DEVICEID)
/


/*==============================================================*/
/* Table : LMGroupRipple                                        */
/*==============================================================*/


create table LMGroupRipple  (
   DeviceID             NUMBER                           not null,
   RouteID              NUMBER                           not null,
   ShedTime             NUMBER                           not null,
   ControlValue         CHAR(50)                         not null,
   RestoreValue         CHAR(50)                         not null
)
/


alter table LMGroupRipple
   add constraint PK_LMGROUPRIPPLE primary key (DeviceID)
/


/*==============================================================*/
/* Table : LMGroupSA205105                                      */
/*==============================================================*/


create table LMGroupSA205105  (
   GroupID              NUMBER                           not null,
   RouteID              NUMBER                           not null,
   OperationalAddress   NUMBER                           not null,
   LoadNumber           VARCHAR2(64)                     not null
)
/


alter table LMGroupSA205105
   add constraint PK_LMGROUPSA205105 primary key (GroupID)
/


/*==============================================================*/
/* Table : LMGroupSA305                                         */
/*==============================================================*/


create table LMGroupSA305  (
   GroupID              NUMBER                           not null,
   RouteID              NUMBER                           not null,
   AddressUsage         VARCHAR2(8)                      not null,
   UtilityAddress       NUMBER                           not null,
   GroupAddress         NUMBER                           not null,
   DivisionAddress      NUMBER                           not null,
   SubstationAddress    NUMBER                           not null,
   IndividualAddress    VARCHAR2(16)                     not null,
   RateFamily           NUMBER                           not null,
   RateMember           NUMBER                           not null,
   RateHierarchy        NUMBER                           not null,
   LoadNumber           VARCHAR2(8)                      not null
)
/


alter table LMGroupSA305
   add constraint PK_LMGROUPSA305 primary key (GroupID)
/


/*==============================================================*/
/* Table : LMGroupSASimple                                      */
/*==============================================================*/


create table LMGroupSASimple  (
   GroupID              NUMBER                           not null,
   RouteID              NUMBER                           not null,
   OperationalAddress   VARCHAR2(8)                      not null,
   NominalTimeout       NUMBER                           not null,
   MarkIndex            NUMBER                           not null,
   SpaceIndex           NUMBER                           not null
)
/


alter table LMGroupSASimple
   add constraint PK_LMGROUPSASIMPLE primary key (GroupID)
/


/*==============================================================*/
/* Table : LMGroupVersacom                                      */
/*==============================================================*/


create table LMGroupVersacom  (
   DEVICEID             NUMBER                           not null,
   ROUTEID              NUMBER                           not null,
   UTILITYADDRESS       NUMBER                           not null,
   SECTIONADDRESS       NUMBER                           not null,
   CLASSADDRESS         NUMBER                           not null,
   DIVISIONADDRESS      NUMBER                           not null,
   ADDRESSUSAGE         CHAR(4)                          not null,
   RELAYUSAGE           CHAR(7)                          not null,
   SerialAddress        VARCHAR2(15)                     not null
)
/


alter table LMGroupVersacom
   add constraint PK_LMGROUPVERSACOM primary key (DEVICEID)
/


/*==============================================================*/
/* Table : LMMACSScheduleOperatorList                           */
/*==============================================================*/


create table LMMACSScheduleOperatorList  (
   ScheduleID           NUMBER                           not null,
   OperatorLoginID      NUMBER                           not null
)
/


alter table LMMACSScheduleOperatorList
   add constraint PK_LMMACSSCHEDULEOPERATORLIST primary key (ScheduleID, OperatorLoginID)
/


/*==============================================================*/
/* Table : LMMacsScheduleCustomerList                           */
/*==============================================================*/


create table LMMacsScheduleCustomerList  (
   ScheduleID           NUMBER                           not null,
   LMCustomerDeviceID   NUMBER                           not null,
   CustomerOrder        NUMBER                           not null
)
/


alter table LMMacsScheduleCustomerList
   add constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key (ScheduleID, LMCustomerDeviceID)
/


/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/


create table LMPROGRAM  (
   DeviceID             NUMBER                           not null,
   ControlType          VARCHAR2(20)                     not null,
   ConstraintID         NUMBER                           not null
)
/


alter table LMPROGRAM
   add constraint PK_LMPROGRAM primary key (DeviceID)
/


/*==============================================================*/
/* Table : LMProgramConstraints                                 */
/*==============================================================*/


create table LMProgramConstraints  (
   ConstraintID         NUMBER                           not null,
   ConstraintName       VARCHAR2(60)                     not null,
   AvailableWeekDays    VARCHAR2(8)                      not null,
   MaxHoursDaily        NUMBER                           not null,
   MaxHoursMonthly      NUMBER                           not null,
   MaxHoursSeasonal     NUMBER                           not null,
   MaxHoursAnnually     NUMBER                           not null,
   MinActivateTime      NUMBER                           not null,
   MinRestartTime       NUMBER                           not null,
   MaxDailyOps          NUMBER                           not null,
   MaxActivateTime      NUMBER                           not null,
   HolidayScheduleID    NUMBER                           not null,
   SeasonScheduleID     NUMBER                           not null
)
/


alter table LMProgramConstraints
   add constraint PK_PRGCONSTR primary key (ConstraintID)
/


/*==============================================================*/
/* Table : LMProgramControlWindow                               */
/*==============================================================*/


create table LMProgramControlWindow  (
   DeviceID             NUMBER                           not null,
   WindowNumber         NUMBER                           not null,
   AvailableStartTime   NUMBER                           not null,
   AvailableStopTime    NUMBER                           not null
)
/


alter table LMProgramControlWindow
   add constraint PK_LMPROGRAMCONTROLWINDOW primary key (DeviceID, WindowNumber)
/


/*==============================================================*/
/* Table : LMProgramCurtailCustomerList                         */
/*==============================================================*/


create table LMProgramCurtailCustomerList  (
   ProgramID            NUMBER                           not null,
   CustomerID           NUMBER                           not null,
   CustomerOrder        NUMBER                           not null,
   RequireAck           CHAR(1)                          not null
)
/


alter table LMProgramCurtailCustomerList
   add constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key (CustomerID, ProgramID)
/


/*==============================================================*/
/* Table : LMProgramCurtailment                                 */
/*==============================================================*/


create table LMProgramCurtailment  (
   DeviceID             NUMBER                           not null,
   MinNotifyTime        NUMBER                           not null,
   Heading              VARCHAR2(40)                     not null,
   MessageHeader        VARCHAR2(160)                    not null,
   MessageFooter        VARCHAR2(160)                    not null,
   AckTimeLimit         NUMBER                           not null,
   CanceledMsg          VARCHAR2(80)                     not null,
   StoppedEarlyMsg      VARCHAR2(80)                     not null
)
/


alter table LMProgramCurtailment
   add constraint PK_LMPROGRAMCURTAILMENT primary key (DeviceID)
/


/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/


create table LMProgramDirect  (
   DeviceID             NUMBER                           not null,
   NotifyOffset         NUMBER                           not null,
   Heading              VARCHAR2(40)                     not null,
   MessageHeader        VARCHAR2(160)                    not null,
   MessageFooter        VARCHAR2(160)                    not null
)
/


alter table LMProgramDirect
   add constraint PK_LMPROGRAMDIRECT primary key (DeviceID)
/


/*==============================================================*/
/* Table : LMProgramDirectGear                                  */
/*==============================================================*/


create table LMProgramDirectGear  (
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
   GroupSelectionMethod VARCHAR2(30)                     not null,
   MethodOptionType     VARCHAR2(30)                     not null,
   MethodOptionMax      NUMBER                           not null,
   GearID               NUMBER                           not null,
   RampInInterval       NUMBER                           not null,
   RampInPercent        NUMBER                           not null,
   RampOutInterval      NUMBER                           not null,
   RampOutPercent       NUMBER                           not null
)
/


alter table LMProgramDirectGear
   add constraint PK_LMPROGRAMDIRECTGEAR primary key (GearID)
/


alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique (DeviceID, GearNumber)
/


/*==============================================================*/
/* Table : LMProgramDirectGroup                                 */
/*==============================================================*/


create table LMProgramDirectGroup  (
   DeviceID             NUMBER                           not null,
   LMGroupDeviceID      NUMBER                           not null,
   GroupOrder           NUMBER                           not null
)
/


alter table LMProgramDirectGroup
   add constraint PK_LMPROGRAMDIRECTGROUP primary key (DeviceID, GroupOrder)
/


/*==============================================================*/
/* Table : LMProgramEnergyExchange                              */
/*==============================================================*/


create table LMProgramEnergyExchange  (
   DeviceID             NUMBER                           not null,
   MinNotifyTime        NUMBER                           not null,
   Heading              VARCHAR2(40)                     not null,
   MessageHeader        VARCHAR2(160)                    not null,
   MessageFooter        VARCHAR2(160)                    not null,
   CanceledMsg          VARCHAR2(80)                     not null,
   StoppedEarlyMsg      VARCHAR2(80)                     not null
)
/


alter table LMProgramEnergyExchange
   add constraint PK_LMPROGRAMENERGYEXCHANGE primary key (DeviceID)
/


/*==============================================================*/
/* Table : LMThermoStatGear                                     */
/*==============================================================*/


create table LMThermoStatGear  (
   GearID               NUMBER                           not null,
   Settings             VARCHAR2(10)                     not null,
   MinValue             NUMBER                           not null,
   MaxValue             NUMBER                           not null,
   ValueB               NUMBER                           not null,
   ValueD               NUMBER                           not null,
   ValueF               NUMBER                           not null,
   Random               NUMBER                           not null,
   ValueTa              NUMBER                           not null,
   ValueTb              NUMBER                           not null,
   ValueTc              NUMBER                           not null,
   ValueTd              NUMBER                           not null,
   ValueTe              NUMBER                           not null,
   ValueTf              NUMBER                           not null
)
/


alter table LMThermoStatGear
   add constraint PK_LMTHERMOSTATGEAR primary key (GearID)
/


/*==============================================================*/
/* Table : LOGIC                                                */
/*==============================================================*/


create table LOGIC  (
   LOGICID              NUMBER                           not null,
   LOGICNAME            VARCHAR2(20)                     not null,
   PERIODICRATE         NUMBER                           not null,
   STATEFLAG            VARCHAR2(10)                     not null,
   SCRIPTNAME           VARCHAR2(20)                     not null
)
/


alter table LOGIC
   add constraint SYS_C0013445 primary key (LOGICID)
/


/*==============================================================*/
/* Table : MACROROUTE                                           */
/*==============================================================*/


create table MACROROUTE  (
   ROUTEID              NUMBER                           not null,
   SINGLEROUTEID        NUMBER                           not null,
   ROUTEORDER           NUMBER                           not null
)
/


alter table MACROROUTE
   add constraint PK_MACROROUTE primary key (ROUTEID, ROUTEORDER)
/


/*==============================================================*/
/* Table : MACSchedule                                          */
/*==============================================================*/


create table MACSchedule  (
   ScheduleID           NUMBER                           not null,
   CategoryName         VARCHAR2(50)                     not null,
   HolidayScheduleID    NUMBER,
   CommandFile          VARCHAR2(180),
   CurrentState         VARCHAR2(12)                     not null,
   StartPolicy          VARCHAR2(20)                     not null,
   StopPolicy           VARCHAR2(20)                     not null,
   LastRunTime          DATE,
   LastRunStatus        VARCHAR2(12),
   StartDay             NUMBER,
   StartMonth           NUMBER,
   StartYear            NUMBER,
   StartTime            VARCHAR2(8),
   StopTime             VARCHAR2(8),
   ValidWeekDays        CHAR(8),
   Duration             NUMBER,
   ManualStartTime      DATE,
   ManualStopTime       DATE
)
/


alter table MACSchedule
   add constraint PK_MACSCHEDULE primary key (ScheduleID)
/


/*==============================================================*/
/* Table : MACSimpleSchedule                                    */
/*==============================================================*/


create table MACSimpleSchedule  (
   ScheduleID           NUMBER                           not null,
   TargetSelect         VARCHAR2(40),
   StartCommand         VARCHAR2(120),
   StopCommand          VARCHAR2(120),
   RepeatInterval       NUMBER
)
/


alter table MACSimpleSchedule
   add constraint PK_MACSIMPLESCHEDULE primary key (ScheduleID)
/


/*==============================================================*/
/* Table : MCTBroadCastMapping                                  */
/*==============================================================*/


create table MCTBroadCastMapping  (
   MCTBroadCastID       NUMBER                           not null,
   MctID                NUMBER                           not null,
   Ordering             NUMBER                           not null
)
/


alter table MCTBroadCastMapping
   add constraint PK_MCTBROADCASTMAPPING primary key (MCTBroadCastID, MctID)
/


/*==============================================================*/
/* Table : MCTConfig                                            */
/*==============================================================*/


create table MCTConfig  (
   ConfigID             NUMBER                           not null,
   ConfigName           VARCHAR2(30)                     not null,
   ConfigType           NUMBER                           not null,
   ConfigMode           VARCHAR2(30)                     not null,
   MCTWire1             NUMBER                           not null,
   Ke1                  FLOAT                            not null,
   MCTWire2             NUMBER                           not null,
   Ke2                  FLOAT                            not null,
   MCTWire3             NUMBER                           not null,
   Ke3                  FLOAT                            not null
)
/


alter table MCTConfig
   add constraint PK_MCTCONFIG primary key (ConfigID)
/


/*==============================================================*/
/* Table : MCTConfigMapping                                     */
/*==============================================================*/


create table MCTConfigMapping  (
   MctID                NUMBER                           not null,
   ConfigID             NUMBER                           not null
)
/


alter table MCTConfigMapping
   add constraint PK_MCTCONFIGMAPPING primary key (MctID, ConfigID)
/


/*==============================================================*/
/* Table : NotificationDestination                              */
/*==============================================================*/


create table NotificationDestination  (
   DestinationOrder     NUMBER                           not null,
   NotificationGroupID  NUMBER                           not null,
   RecipientID          NUMBER                           not null
)
/


alter table NotificationDestination
   add constraint PKey_NotDestID primary key (NotificationGroupID, DestinationOrder)
/


/*==============================================================*/
/* Table : NotificationGroup                                    */
/*==============================================================*/


create table NotificationGroup  (
   NotificationGroupID  NUMBER                           not null,
   GroupName            VARCHAR2(40)                     not null,
   EmailSubject         VARCHAR2(60)                     not null,
   EmailFromAddress     VARCHAR2(100)                    not null,
   EmailMessage         VARCHAR2(160)                    not null,
   NumericPagerMessage  VARCHAR2(14)                     not null,
   DisableFlag          CHAR(1)                          not null
)
/


alter table NotificationGroup
   add constraint PK_NOTIFICATIONGROUP primary key (NotificationGroupID)
/


/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique index Indx_NOTIFGRPNme on NotificationGroup (
   GroupName ASC
)
/


/*==============================================================*/
/* Table : OperatorLoginGraphList                               */
/*==============================================================*/


create table OperatorLoginGraphList  (
   OperatorLoginID      NUMBER                           not null,
   GraphDefinitionID    NUMBER                           not null
)
/


alter table OperatorLoginGraphList
   add constraint PK_OPERATORLOGINGRAPHLIST primary key (OperatorLoginID, GraphDefinitionID)
/


/*==============================================================*/
/* Table : OperatorSerialGroup                                  */
/*==============================================================*/


create table OperatorSerialGroup  (
   LoginID              NUMBER                           not null,
   LMGroupID            NUMBER                           not null
)
/


alter table OperatorSerialGroup
   add constraint PK_OpSerGrp primary key (LoginID)
/


/*==============================================================*/
/* Table : PAOExclusion                                         */
/*==============================================================*/


create table PAOExclusion  (
   ExclusionID          NUMBER                           not null,
   PaoID                NUMBER                           not null,
   ExcludedPaoID        NUMBER                           not null,
   PointID              NUMBER                           not null,
   Value                NUMBER                           not null,
   FunctionID           NUMBER                           not null,
   FuncName             VARCHAR2(100)                    not null,
   FuncRequeue          NUMBER                           not null,
   FuncParams           VARCHAR2(200)                    not null
)
/


alter table PAOExclusion
   add constraint PK_PAOEXCLUSION primary key (ExclusionID)
/


/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique index Indx_PAOExclus on PAOExclusion (
   PaoID ASC,
   ExcludedPaoID ASC
)
/


/*==============================================================*/
/* Table : PAOowner                                             */
/*==============================================================*/


create table PAOowner  (
   OwnerID              NUMBER                           not null,
   ChildID              NUMBER                           not null
)
/


alter table PAOowner
   add constraint PK_PAOOWNER primary key (OwnerID, ChildID)
/


/*==============================================================*/
/* Table : POINT                                                */
/*==============================================================*/


create table POINT  (
   POINTID              NUMBER                           not null,
   POINTTYPE            VARCHAR2(20)                     not null,
   POINTNAME            VARCHAR2(60)                     not null,
   PAObjectID           NUMBER                           not null,
   LOGICALGROUP         VARCHAR2(14)                     not null,
   STATEGROUPID         NUMBER                           not null,
   SERVICEFLAG          VARCHAR2(1)                      not null,
   ALARMINHIBIT         VARCHAR2(1)                      not null,
   PSEUDOFLAG           VARCHAR2(1)                      not null,
   POINTOFFSET          NUMBER                           not null,
   ARCHIVETYPE          VARCHAR2(12)                     not null,
   ARCHIVEINTERVAL      NUMBER                           not null
)
/


alter table POINT
   add constraint Key_PT_PTID primary key (POINTID)
/


alter table POINT
   add constraint AK_KEY_PTNM_YUKPAOID unique (POINTNAME, PAObjectID)
/


/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create index Indx_PointStGrpID on POINT (
   STATEGROUPID ASC
)
/


/*==============================================================*/
/* Table : POINTACCUMULATOR                                     */
/*==============================================================*/


create table POINTACCUMULATOR  (
   POINTID              NUMBER                           not null,
   MULTIPLIER           FLOAT                            not null,
   DATAOFFSET           FLOAT                            not null
)
/


alter table POINTACCUMULATOR
   add constraint PK_POINTACCUMULATOR primary key (POINTID)
/


/*==============================================================*/
/* Table : POINTANALOG                                          */
/*==============================================================*/


create table POINTANALOG  (
   POINTID              NUMBER                           not null,
   DEADBAND             FLOAT                            not null,
   TRANSDUCERTYPE       VARCHAR2(14)                     not null,
   MULTIPLIER           FLOAT                            not null,
   DATAOFFSET           FLOAT                            not null
)
/


alter table POINTANALOG
   add constraint PK_POINTANALOG primary key (POINTID)
/


/*==============================================================*/
/* Table : POINTLIMITS                                          */
/*==============================================================*/


create table POINTLIMITS  (
   POINTID              NUMBER                           not null,
   LIMITNUMBER          NUMBER                           not null,
   HIGHLIMIT            FLOAT                            not null,
   LOWLIMIT             FLOAT                            not null,
   LIMITDURATION        NUMBER                           not null
)
/


alter table POINTLIMITS
   add constraint PK_POINTLIMITS primary key (POINTID, LIMITNUMBER)
/


/*==============================================================*/
/* Table : POINTSTATUS                                          */
/*==============================================================*/


create table POINTSTATUS  (
   POINTID              NUMBER                           not null,
   INITIALSTATE         NUMBER                           not null,
   CONTROLTYPE          VARCHAR2(12)                     not null,
   CONTROLINHIBIT       VARCHAR2(1)                      not null,
   ControlOffset        NUMBER                           not null,
   CloseTime1           NUMBER                           not null,
   CloseTime2           NUMBER                           not null,
   StateZeroControl     VARCHAR2(100)                    not null,
   StateOneControl      VARCHAR2(100)                    not null,
   CommandTimeOut       NUMBER                           not null
)
/


alter table POINTSTATUS
   add constraint PK_PtStatus primary key (POINTID)
/


/*==============================================================*/
/* Table : POINTUNIT                                            */
/*==============================================================*/


create table POINTUNIT  (
   POINTID              NUMBER                           not null,
   UOMID                NUMBER                           not null,
   DECIMALPLACES        NUMBER                           not null,
   HighReasonabilityLimit FLOAT                            not null,
   LowReasonabilityLimit FLOAT                            not null
)
/


alter table POINTUNIT
   add constraint PK_POINTUNITID primary key (POINTID)
/


/*==============================================================*/
/* Table : PORTDIALUPMODEM                                      */
/*==============================================================*/


create table PORTDIALUPMODEM  (
   PORTID               NUMBER                           not null,
   MODEMTYPE            VARCHAR2(30)                     not null,
   INITIALIZATIONSTRING VARCHAR2(50)                     not null,
   PREFIXNUMBER         VARCHAR2(10)                     not null,
   SUFFIXNUMBER         VARCHAR2(10)                     not null
)
/


alter table PORTDIALUPMODEM
   add constraint PK_PORTDIALUPMODEM primary key (PORTID)
/


/*==============================================================*/
/* Table : PORTLOCALSERIAL                                      */
/*==============================================================*/


create table PORTLOCALSERIAL  (
   PORTID               NUMBER                           not null,
   PHYSICALPORT         VARCHAR2(8)                      not null
)
/


alter table PORTLOCALSERIAL
   add constraint PK_PORTLOCALSERIAL primary key (PORTID)
/


/*==============================================================*/
/* Table : PORTRADIOSETTINGS                                    */
/*==============================================================*/


create table PORTRADIOSETTINGS  (
   PORTID               NUMBER                           not null,
   RTSTOTXWAITSAMED     NUMBER                           not null,
   RTSTOTXWAITDIFFD     NUMBER                           not null,
   RADIOMASTERTAIL      NUMBER                           not null,
   REVERSERTS           NUMBER                           not null
)
/


alter table PORTRADIOSETTINGS
   add constraint PK_PORTRADIOSETTINGS primary key (PORTID)
/


/*==============================================================*/
/* Table : PORTSETTINGS                                         */
/*==============================================================*/


create table PORTSETTINGS  (
   PORTID               NUMBER                           not null,
   BAUDRATE             NUMBER                           not null,
   CDWAIT               NUMBER                           not null,
   LINESETTINGS         VARCHAR2(8)                      not null
)
/


alter table PORTSETTINGS
   add constraint PK_PORTSETTINGS primary key (PORTID)
/


/*==============================================================*/
/* Table : PORTTERMINALSERVER                                   */
/*==============================================================*/


create table PORTTERMINALSERVER  (
   PORTID               NUMBER                           not null,
   IPADDRESS            VARCHAR2(64)                     not null,
   SOCKETPORTNUMBER     NUMBER                           not null
)
/


alter table PORTTERMINALSERVER
   add constraint PK_PORTTERMINALSERVER primary key (PORTID)
/


/*==============================================================*/
/* Table : PointAlarming                                        */
/*==============================================================*/


create table PointAlarming  (
   PointID              NUMBER                           not null,
   AlarmStates          VARCHAR2(32)                     not null,
   ExcludeNotifyStates  VARCHAR2(32)                     not null,
   NotifyOnAcknowledge  CHAR(1)                          not null,
   NotificationGroupID  NUMBER                           not null,
   RecipientID          NUMBER                           not null
)
/


alter table PointAlarming
   add constraint PK_POINTALARMING primary key (PointID)
/


/*==============================================================*/
/* Table : PortTiming                                           */
/*==============================================================*/


create table PortTiming  (
   PORTID               NUMBER                           not null,
   PRETXWAIT            NUMBER                           not null,
   RTSTOTXWAIT          NUMBER                           not null,
   POSTTXWAIT           NUMBER                           not null,
   RECEIVEDATAWAIT      NUMBER                           not null,
   EXTRATIMEOUT         NUMBER                           not null
)
/


alter table PortTiming
   add constraint PK_PORTTIMING primary key (PORTID)
/


/*==============================================================*/
/* Table : RAWPOINTHISTORY                                      */
/*==============================================================*/


create table RAWPOINTHISTORY  (
   CHANGEID             NUMBER                           not null,
   POINTID              NUMBER                           not null,
   TIMESTAMP            DATE                             not null,
   QUALITY              NUMBER                           not null,
   VALUE                FLOAT                            not null,
   millis               SMALLINT                         not null
)
/


alter table RAWPOINTHISTORY
   add constraint SYS_C0013322 primary key (CHANGEID)
/


/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create index Index_PointID on RAWPOINTHISTORY (
   POINTID ASC
)
/


/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create index Indx_TimeStamp on RAWPOINTHISTORY (
   TIMESTAMP ASC
)
/


/*==============================================================*/
/* Table : RepeaterRoute                                        */
/*==============================================================*/


create table RepeaterRoute  (
   ROUTEID              NUMBER                           not null,
   DEVICEID             NUMBER                           not null,
   VARIABLEBITS         NUMBER                           not null,
   REPEATERORDER        NUMBER                           not null
)
/


alter table RepeaterRoute
   add constraint PK_REPEATERROUTE primary key (ROUTEID, DEVICEID)
/


/*==============================================================*/
/* Table : Route                                                */
/*==============================================================*/


create table Route  (
   RouteID              NUMBER                           not null,
   DeviceID             NUMBER                           not null,
   DefaultRoute         CHAR(1)                          not null
)
/


alter table Route
   add constraint SYS_RoutePK primary key (RouteID)
/


/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique index Indx_RouteDevID on Route (
   DeviceID DESC,
   RouteID ASC
)
/


/*==============================================================*/
/* Table : STATE                                                */
/*==============================================================*/


create table STATE  (
   STATEGROUPID         NUMBER                           not null,
   RAWSTATE             NUMBER                           not null,
   TEXT                 VARCHAR2(20)                     not null,
   FOREGROUNDCOLOR      NUMBER                           not null,
   BACKGROUNDCOLOR      NUMBER                           not null,
   ImageID              NUMBER                           not null
)
/


alter table STATE
   add constraint PK_STATE primary key (STATEGROUPID, RAWSTATE)
/


/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create index Indx_StateRaw on STATE (
   RAWSTATE ASC
)
/


/*==============================================================*/
/* Table : STATEGROUP                                           */
/*==============================================================*/


create table STATEGROUP  (
   STATEGROUPID         NUMBER                           not null,
   NAME                 VARCHAR2(20)                     not null,
   GroupType            VARCHAR2(20)                     not null
)
/


alter table STATEGROUP
   add constraint SYS_C0013128 primary key (STATEGROUPID)
/


/*==============================================================*/
/* Index: Indx_STATEGRP_Nme                                     */
/*==============================================================*/
create unique index Indx_STATEGRP_Nme on STATEGROUP (
   NAME DESC
)
/


/*==============================================================*/
/* Table : SYSTEMLOG                                            */
/*==============================================================*/


create table SYSTEMLOG  (
   LOGID                NUMBER                           not null,
   POINTID              NUMBER                           not null,
   DATETIME             DATE                             not null,
   SOE_TAG              NUMBER                           not null,
   TYPE                 NUMBER                           not null,
   PRIORITY             NUMBER                           not null,
   ACTION               VARCHAR2(60),
   DESCRIPTION          VARCHAR2(120),
   USERNAME             VARCHAR2(30),
   millis               SMALLINT                         not null
)
/


alter table SYSTEMLOG
   add constraint SYS_C0013407 primary key (LOGID)
/


/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create index Indx_SYSLG_PtId on SYSTEMLOG (
   POINTID ASC
)
/


/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create index Indx_SYSLG_Date on SYSTEMLOG (
   DATETIME ASC
)
/


/*==============================================================*/
/* Table : SeasonSchedule                                       */
/*==============================================================*/


create table SeasonSchedule  (
   ScheduleID           NUMBER                           not null,
   ScheduleName         VARCHAR2(40)                     not null
)
/


alter table SeasonSchedule
   add constraint PK_SEASONSCHEDULE primary key (ScheduleID)
/


/*==============================================================*/
/* Table : TEMPLATE                                             */
/*==============================================================*/


create table TEMPLATE  (
   TEMPLATENUM          NUMBER                           not null,
   NAME                 VARCHAR2(40)                     not null,
   DESCRIPTION          VARCHAR2(200)
)
/


alter table TEMPLATE
   add constraint SYS_C0013425 primary key (TEMPLATENUM)
/


/*==============================================================*/
/* Table : TEMPLATECOLUMNS                                      */
/*==============================================================*/


create table TEMPLATECOLUMNS  (
   TEMPLATENUM          NUMBER                           not null,
   TITLE                VARCHAR2(50)                     not null,
   TYPENUM              NUMBER                           not null,
   ORDERING             NUMBER                           not null,
   WIDTH                NUMBER                           not null
)
/


alter table TEMPLATECOLUMNS
   add constraint PK_TEMPLATECOLUMNS primary key (TEMPLATENUM, TITLE)
/


/*==============================================================*/
/* Table : TagLog                                               */
/*==============================================================*/


create table TagLog  (
   LogID                NUMBER                           not null,
   InstanceID           NUMBER                           not null,
   PointID              NUMBER                           not null,
   TagID                NUMBER                           not null,
   UserName             VARCHAR2(60)                     not null,
   Action               VARCHAR2(20)                     not null,
   Description          VARCHAR2(120)                    not null,
   TagTime              DATE                             not null,
   RefStr               VARCHAR2(60)                     not null,
   ForStr               VARCHAR2(60)                     not null
)
/


alter table TagLog
   add constraint PK_TAGLOG primary key (LogID)
/


/*==============================================================*/
/* Table : Tags                                                 */
/*==============================================================*/


create table Tags  (
   TagID                NUMBER                           not null,
   TagName              VARCHAR2(60)                     not null,
   TagLevel             NUMBER                           not null,
   Inhibit              CHAR(1)                          not null,
   ColorID              NUMBER                           not null,
   ImageID              NUMBER                           not null
)
/


alter table Tags
   add constraint PK_TAGS primary key (TagID)
/


/*==============================================================*/
/* Table : UNITMEASURE                                          */
/*==============================================================*/


create table UNITMEASURE  (
   UOMID                NUMBER                           not null,
   UOMName              VARCHAR2(8)                      not null,
   CalcType             NUMBER                           not null,
   LongName             VARCHAR2(40)                     not null,
   Formula              VARCHAR2(80)                     not null
)
/


alter table UNITMEASURE
   add constraint SYS_C0013344 primary key (UOMID)
/


/*==============================================================*/
/* Table : UserPAOowner                                         */
/*==============================================================*/


create table UserPAOowner  (
   UserID               NUMBER                           not null,
   PaoID                NUMBER                           not null
)
/


alter table UserPAOowner
   add constraint PK_USERPAOOWNER primary key (UserID, PaoID)
/


/*==============================================================*/
/* Table : VersacomRoute                                        */
/*==============================================================*/


create table VersacomRoute  (
   ROUTEID              NUMBER                           not null,
   UTILITYID            NUMBER                           not null,
   SECTIONADDRESS       NUMBER                           not null,
   CLASSADDRESS         NUMBER                           not null,
   DIVISIONADDRESS      NUMBER                           not null,
   BUSNUMBER            NUMBER                           not null,
   AMPCARDSET           NUMBER                           not null
)
/


alter table VersacomRoute
   add constraint PK_VERSACOMROUTE primary key (ROUTEID)
/


/*==============================================================*/
/* Table : YukonGroup                                           */
/*==============================================================*/


create table YukonGroup  (
   GroupID              NUMBER                           not null,
   GroupName            VARCHAR2(120)                    not null,
   GroupDescription     VARCHAR2(200)                    not null
)
/


alter table YukonGroup
   add constraint PK_YUKONGROUP primary key (GroupID)
/


/*==============================================================*/
/* Table : YukonGroupRole                                       */
/*==============================================================*/


create table YukonGroupRole  (
   GroupRoleID          NUMBER                           not null,
   GroupID              NUMBER                           not null,
   RoleID               NUMBER                           not null,
   RolePropertyID       NUMBER                           not null,
   Value                VARCHAR2(1000)                   not null
)
/


alter table YukonGroupRole
   add constraint PK_YUKONGRPROLE primary key (GroupRoleID)
/


/*==============================================================*/
/* Table : YukonImage                                           */
/*==============================================================*/


create table YukonImage  (
   ImageID              NUMBER                           not null,
   ImageCategory        VARCHAR2(20)                     not null,
   ImageName            VARCHAR2(80)                     not null,
   ImageValue           LONG RAW
)
/


alter table YukonImage
   add constraint PK_YUKONIMAGE primary key (ImageID)
/


/*==============================================================*/
/* Table : YukonListEntry                                       */
/*==============================================================*/


create table YukonListEntry  (
   EntryID              NUMBER                           not null,
   ListID               NUMBER                           not null,
   EntryOrder           NUMBER                           not null,
   EntryText            VARCHAR2(50)                     not null,
   YukonDefinitionID    NUMBER                           not null
)
/


alter table YukonListEntry
   add constraint PK_YUKONLISTENTRY primary key (EntryID)
/


/*==============================================================*/
/* Index: Indx_YkLstDefID                                       */
/*==============================================================*/
create index Indx_YkLstDefID on YukonListEntry (
   YukonDefinitionID ASC
)
/


/*==============================================================*/
/* Table : YukonPAObject                                        */
/*==============================================================*/


create table YukonPAObject  (
   PAObjectID           NUMBER                           not null,
   Category             VARCHAR2(20)                     not null,
   PAOClass             VARCHAR2(20)                     not null,
   PAOName              VARCHAR2(60)                     not null,
   Type                 VARCHAR2(30)                     not null,
   Description          VARCHAR2(60)                     not null,
   DisableFlag          CHAR(1)                          not null,
   PAOStatistics        VARCHAR2(10)                     not null
)
/


alter table YukonPAObject
   add constraint PK_YUKONPAOBJECT primary key (PAObjectID)
/


/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique index Indx_PAO on YukonPAObject (
   Category ASC,
   PAOName ASC,
   PAOClass ASC,
   Type ASC
)
/


/*==============================================================*/
/* Table : YukonRole                                            */
/*==============================================================*/


create table YukonRole  (
   RoleID               NUMBER                           not null,
   RoleName             VARCHAR2(120)                    not null,
   Category             VARCHAR2(60)                     not null,
   RoleDescription      VARCHAR2(200)                    not null
)
/


alter table YukonRole
   add constraint PK_YUKONROLE primary key (RoleID)
/


/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create index Indx_YukRol_Nm on YukonRole (
   RoleName ASC
)
/


/*==============================================================*/
/* Table : YukonRoleProperty                                    */
/*==============================================================*/


create table YukonRoleProperty  (
   RolePropertyID       NUMBER                           not null,
   RoleID               NUMBER                           not null,
   KeyName              VARCHAR2(100)                    not null,
   DefaultValue         VARCHAR2(1000)                   not null,
   Description          VARCHAR2(1000)                   not null
)
/


alter table YukonRoleProperty
   add constraint PK_YUKONROLEPROPERTY primary key (RolePropertyID)
/


/*==============================================================*/
/* Table : YukonSelectionList                                   */
/*==============================================================*/


create table YukonSelectionList  (
   ListID               NUMBER                           not null,
   Ordering             VARCHAR2(1)                      not null,
   SelectionLabel       VARCHAR2(30)                     not null,
   WhereIsList          VARCHAR2(100)                    not null,
   ListName             VARCHAR2(40)                     not null,
   UserUpdateAvailable  VARCHAR2(1)                      not null
)
/


alter table YukonSelectionList
   add constraint PK_YUKONSELECTIONLIST primary key (ListID)
/


/*==============================================================*/
/* Table : YukonServices                                        */
/*==============================================================*/


create table YukonServices  (
   ServiceID            NUMBER                           not null,
   ServiceName          VARCHAR2(60)                     not null,
   ServiceClass         VARCHAR2(100)                    not null,
   ParamNames           VARCHAR2(300)                    not null,
   ParamValues          VARCHAR2(300)                    not null
)
/


alter table YukonServices
   add constraint PK_YUKSER primary key (ServiceID)
/


/*==============================================================*/
/* Table : YukonUser                                            */
/*==============================================================*/


create table YukonUser  (
   UserID               NUMBER                           not null,
   UserName             VARCHAR2(64)                     not null,
   Password             VARCHAR2(64)                     not null,
   LoginCount           NUMBER                           not null,
   LastLogin            DATE                             not null,
   Status               VARCHAR2(20)                     not null
)
/


alter table YukonUser
   add constraint PK_YUKONUSER primary key (UserID)
/


/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique index Indx_YkUsIDNm on YukonUser (
   UserName ASC
)
/


/*==============================================================*/
/* Table : YukonUserGroup                                       */
/*==============================================================*/


create table YukonUserGroup  (
   UserID               NUMBER                           not null,
   GroupID              NUMBER                           not null
)
/


alter table YukonUserGroup
   add constraint PK_YUKONUSERGROUP primary key (UserID, GroupID)
/


/*==============================================================*/
/* Table : YukonUserRole                                        */
/*==============================================================*/


create table YukonUserRole  (
   UserRoleID           NUMBER                           not null,
   UserID               NUMBER                           not null,
   RoleID               NUMBER                           not null,
   RolePropertyID       NUMBER                           not null,
   Value                VARCHAR2(1000)                   not null
)
/


alter table YukonUserRole
   add constraint PK_YKONUSRROLE primary key (UserRoleID)
/


/*==============================================================*/
/* Table : YukonWebConfiguration                                */
/*==============================================================*/


create table YukonWebConfiguration  (
   ConfigurationID      NUMBER                           not null,
   LogoLocation         VARCHAR2(100),
   Description          VARCHAR2(500),
   AlternateDisplayName VARCHAR2(100),
   URL                  VARCHAR2(100)
)
/


alter table YukonWebConfiguration
   add constraint PK_YUKONWEBCONFIGURATION primary key (ConfigurationID)
/


alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
/


alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
/


alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
/


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
/


alter table DynamicLMProgramDirect
   add constraint FK_DYN_LMPR_LMP foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
/


alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
/


alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
/


alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
/


alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
/


alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (CustomerID)
/


alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID)
/


alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID)
/


alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID)
/


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
/


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID)
/


alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID)
/


alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID)
/


alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID)
/


alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
/


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
/


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID)
/


alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
/


alter table LMEnergyExchangeCustomerReply
   add constraint FK_LME_REFE_LME foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
      on delete cascade
/


alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
      on delete cascade
/


alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
/


alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID)
/


alter table GraphCustomerList
   add constraint FK_GRA_REFG_GRA foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
/


alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER)
/


alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID)
/


alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
/


alter table LMCurtailCustomerActivity
   add constraint FK_LMC_REFL_LMC foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID)
/


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
/


alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
/


alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID)
/


alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID)
/


alter table LMDirectCustomerList
   add constraint FK_LMD_REFL_LMP foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
/


alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
/


alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID)
      on delete cascade
/


alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
/


alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
/


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DeviceID)
/


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
/


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
/


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
/


alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID)
/


alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID)
/


alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
/


alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID)
/


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID)
/


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID)
/


alter table CCFeederBankList
   add constraint FK_CB_CCFeedLst foreign key (DeviceID)
      references CAPBANK (DEVICEID)
/


alter table CCFeederBankList
   add constraint FK_CCFeed_CCBnk foreign key (FeederID)
      references CapControlFeeder (FeederID)
/


alter table CCFeederSubAssignment
   add constraint FK_CCFeed_CCFass foreign key (FeederID)
      references CapControlFeeder (FeederID)
/


alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID)
/


alter table DynamicCCSubstationBus
   add constraint FK_CCSubBs_DySubBs foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
/


alter table CCFeederSubAssignment
   add constraint FK_CCSub_CCFeed foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
/


alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
/


alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID)
/


alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
/


alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID)
/


alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
/


alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
/


alter table Contact
   add constraint FK_CON_REF__ADD foreign key (AddressID)
      references Address (AddressID)
/


alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID)
/


alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
/


alter table CommPort
   add constraint FK_COM_REF__YUK foreign key (PORTID)
      references YukonPAObject (PAObjectID)
/


alter table DynamicCCCapBank
   add constraint FK_CpBnk_DynCpBnk foreign key (CapBankID)
      references CAPBANK (DEVICEID)
/


alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID)
/


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID)
/


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
/


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID)
/


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID)
/


alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID)
/


alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
/


alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID)
/


alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
/


alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
/


alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
/


alter table DeviceSeries5RTU
   add constraint FK_DvS5r_Dv2w foreign key (DeviceID)
      references DEVICE2WAYFLAGS (DEVICEID)
/


alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID)
/


alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table DynamicLMGroup
   add constraint FK_DyLmGr_LmPrDGr foreign key (LMProgramID)
      references LMProgramDirect (DeviceID)
/


alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID)
/


alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID)
/


alter table DynamicPointAlarming
   add constraint FKf_DynPtAl_SysL foreign key (LogID)
      references SYSTEMLOG (LOGID)
/


alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID)
/


alter table DynamicTags
   add constraint FK_DYN_REF__TAG foreign key (TagID)
      references Tags (TagID)
/


alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
/


alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
/


alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
/


alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
/


alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
/


alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID)
/


alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID)
/


alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID)
/


alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID)
/


alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID)
/


alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID)
/


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
/


alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID)
/


alter table LMProgramConstraints
   add constraint FK_HlSc_LmPrC foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
/


alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
/


alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
/


alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID)
/


alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
/


alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID)
/


alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID)
/


alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID)
/


alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
/


alter table LMDirectOperatorList
   add constraint FK_LMDirOpLs_LMPrD foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
/


alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID)
/


alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID)
/


alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID)
/


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID)
/


alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID)
/


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID)
/


alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID)
/


alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID)
/


alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
/


alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
/


alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
/


alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID)
/


alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
/


alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
/


alter table LMControlScenarioProgram
   add constraint FK_LMC_REF__LMP foreign key (ProgramID)
      references LMPROGRAM (DeviceID)
/


alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID)
/


alter table LMMACSScheduleOperatorList
   add constraint FK_MCSchLMMcSchOpLs foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
/


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID)
/


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPMCT foreign key (MctID)
      references DEVICE (DEVICEID)
/


alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID)
/


alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID)
/


alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID)
/


alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
/


alter table LMMACSScheduleOperatorList
   add constraint FK_OpLgLMMcSchOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
/


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID)
/


alter table LMDirectOperatorList
   add constraint FK_OpLg_LMDOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
/


alter table OperatorSerialGroup
   add constraint FK_OpSGrp_LmGrp foreign key (LMGroupID)
      references LMGroup (DeviceID)
/


alter table OperatorSerialGroup
   add constraint FK_OpSGrp_OpLg foreign key (LoginID)
      references YukonUser (UserID)
/


alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID)
/


alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
/


alter table PAOExclusion
   add constraint FK_PAO_REF__YUK foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID)
/


alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID)
/


alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID)
/


alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID)
/


alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
/


alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID)
/


alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID)
/


alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
/


alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID)
/


alter table LMProgramConstraints
   add constraint FK_SesSch_LmPrC foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
/


alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID)
/


alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID)
/


alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID)
/


alter table UserPAOowner
   add constraint FK_UsPow_YkP foreign key (PaoID)
      references YukonPAObject (PAObjectID)
/


alter table UserPAOowner
   add constraint FK_UsPow_YkUsr foreign key (UserID)
      references YukonUser (UserID)
/


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID)
/


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
/


alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID)
/


alter table STATE
   add constraint FK_YkIm_St foreign key (ImageID)
      references YukonImage (ImageID)
/


alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
/


alter table YukonUserGroup
   add constraint FK_YkUsGr_YkGr foreign key (GroupID)
      references YukonGroup (GroupID)
/


alter table YukonUserGroup
   add constraint FK_YUK_REF__YUK foreign key (UserID)
      references YukonUser (UserID)
/


alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID)
/


alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID)
/


alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID)
/


alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID)
/


alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID)
/


alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID)
/


alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID)
/


alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID)
/


alter table DEVICE2WAYFLAGS
   add constraint SYS_C0013208 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID)
/


alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID)
/


alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID)
/


alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID)
/


alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
/


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
/


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
/


alter table DYNAMICACCUMULATOR
   add constraint SYS_C0015129 foreign key (POINTID)
      references POINT (POINTID)
/


alter table DYNAMICDEVICESCANDATA
   add constraint SYS_C0015139 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table FDRTRANSLATION
   add constraint SYS_C0015066 foreign key (POINTID)
      references POINT (POINTID)
/


alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
/


alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID)
/


alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID)
/


alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID)
/


alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID)
/


alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID)
/


alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID)
/


alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID)
/


alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID)
/


alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID)
/


alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID)
/


alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID)
/


alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references CommPort (PORTID)
/


alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID)
/


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
/


alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID)
/


alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID)
/


alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID)
/


alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID)
/


alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID)
/


alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
/


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
/


alter table STATE
   add constraint SYS_C0013342 foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
/


alter table SYSTEMLOG
   add constraint SYS_C0013408 foreign key (POINTID)
      references POINT (POINTID)
/


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM)
/


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
/


alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID)
/


alter table VersacomRoute
   add constraint FK_VER_ROUT_ROU foreign key (ROUTEID)
      references Route (RouteID)
/


