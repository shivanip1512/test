/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      CTI Oracle 8.1.5                             */
/* Created on:     9/29/2004 4:10:14 PM                         */
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


insert into address values ( 0, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' );

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


insert into AlarmCategory values(1,'(none)',1);
insert into AlarmCategory values(2,'Category 1',1);
insert into AlarmCategory values(3,'Category 2',1);
insert into AlarmCategory values(4,'Category 3',1);
insert into AlarmCategory values(5,'Category 4',1);
insert into AlarmCategory values(6,'Category 5',1);
insert into AlarmCategory values(7,'Category 6',1);
insert into AlarmCategory values(8,'Category 7',1);
insert into AlarmCategory values(9,'Category 8',1);
insert into AlarmCategory values(10,'Category 9',1);
insert into AlarmCategory values(11,'Category 10',1);


insert into AlarmCategory values(12,'Category 11',1);
insert into AlarmCategory values(13,'Category 12',1);
insert into AlarmCategory values(14,'Category 13',1);
insert into AlarmCategory values(15,'Category 14',1);
insert into AlarmCategory values(16,'Category 15',1);
insert into AlarmCategory values(17,'Category 16',1);
insert into AlarmCategory values(18,'Category 17',1);
insert into AlarmCategory values(19,'Category 18',1);
insert into AlarmCategory values(20,'Category 19',1);
insert into AlarmCategory values(21,'Category 20',1);
insert into AlarmCategory values(22,'Category 21',1);
insert into AlarmCategory values(23,'Category 22',1);
insert into AlarmCategory values(24,'Category 23',1);
insert into AlarmCategory values(25,'Category 24',1);
insert into AlarmCategory values(26,'Category 25',1);
insert into AlarmCategory values(27,'Category 26',1);
insert into AlarmCategory values(28,'Category 27',1);
insert into AlarmCategory values(29,'Category 28',1);
insert into AlarmCategory values(30,'Category 29',1);
insert into AlarmCategory values(31,'Category 30',1);
insert into AlarmCategory values(32,'Category 31',1);

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


insert into baseline values (1, 'Default Baseline', 30, 75, 5, 'YNNNNNY', 0);

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


insert into billingfileformats values( -11, 'MV_90 DATA Import');
insert into BillingFileFormats values(-1,'INVALID');
insert into BillingFileFormats values(0,'SEDC');
insert into BillingFileFormats values(1,'CADP');
insert into BillingFileFormats values(2,'CADPXL2');
insert into BillingFileFormats values(3,'WLT-40');
insert into BillingFileFormats values(4,'CTI-CSV');
insert into BillingFileFormats values(5,'OPU');
insert into BillingFileFormats values(6,'DAFRON');
insert into BillingFileFormats values(7,'NCDC');
insert into billingFileformats values (9, 'CTI2');
insert into billingfileformats values( 12, 'SEDC 5.4');
insert into billingfileformats values( 13, 'NISC-Turtle');
insert into billingfileformats values( 14, 'NISC-NCDC');


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


insert into columntype values (1, 'PointID');
insert into columntype values (2, 'PointName');
insert into columntype values (3, 'PointType');
insert into columntype values (4, 'PointState');
insert into columntype values (5, 'DeviceName');
insert into columntype values (6, 'DeviceType');
insert into columntype values (7, 'DeviceCurrentState');
insert into columntype values (8, 'DeviceID');
insert into columntype values (9, 'PointValue');
insert into columntype values (10, 'PointQuality');
insert into columntype values (11, 'PointTimeStamp');
insert into columntype values (12, 'UofM');
insert into columntype values (13, 'State');
insert into columntype values (14, 'PointImage' );
insert into columntype values (15, 'QualityCount' );

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


insert into CTIDatabase values('3.01', 'Ryan', '10-MAY-2004', 'Added many load control and protocol changes', 1);

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
/* Table : Command                                              */
/*==============================================================*/


create table Command  (
   CommandID            NUMBER                           not null,
   Command              VARCHAR2(128)                    not null,
   Label                VARCHAR2(64)                     not null,
   Category             CHAR(32)                         not null
)
/


alter table Command
   add constraint PK_COMMAND primary key (CommandID)
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


insert into contact values ( 0, '(none)', '(none)', -1, 0 );

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


insert into ContactNotification values( 0, 0, 0, 'N', '(none)' );


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


INSERT INTO Customer VALUES (-1,0,0,'(none)');

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


INSERT into device values (0, 'N', 'N');

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


insert into display values(-4, 'Yukon Server', 'Static Displays', 'Yukon Servers', 'com.cannontech.tdc.windows.WinServicePanel');
insert into display values(-1, 'All Categories', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');

/**insert into display values(-2, 'All Areas', 'Cap Control Client', 'Cap Control', 'com.cannontech.cbc.gui.StrategyReceiver');**/
/**insert into display values(-3, 'All Control Areas', 'Load Management Client', 'Load Management', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel');**/
/**insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occured.');**/
/**insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will recieve current raw point updates as they happen in the system.');**/


insert into display values(1, 'Event Viewer', 'Alarms and Events', 'Current Event Viewer', 'This display will recieve current events as they happen in the system.');
insert into display values(4, 'All Alarms', 'Alarms and Events', 'Global Alarm Viewer', 'This display will recieve all alarm events as they happen in the system.');
insert into display values(5, 'Priority 1 Alarms', 'Alarms and Events', 'Priority 1 Alarm Viewer', 'This display will recieve all priority 1 alarm events as they happen in the system.');
insert into display values(6, 'Priority 2 Alarms', 'Alarms and Events', 'Priority 2 Alarm Viewer', 'This display will recieve all priority 2 alarm events as they happen in the system.');
insert into display values(7, 'Priority 3 Alarms', 'Alarms and Events', 'Priority 3 Alarm Viewer', 'This display will recieve all priority 3 alarm events as they happen in the system.');
insert into display values(8, 'Priority 4 Alarms', 'Alarms and Events', 'Priority 4 Alarm Viewer', 'This display will recieve all priority 4 alarm events as they happen in the system.');
insert into display values(9, 'Priority 5 Alarms', 'Alarms and Events', 'Priority 5 Alarm Viewer', 'This display will recieve all priority 5 alarm events as they happen in the system.');
insert into display values(10, 'Priority 6 Alarms', 'Alarms and Events', 'Priority 6 Alarm Viewer', 'This display will recieve all priority 6 alarm events as they happen in the system.');
insert into display values(11, 'Priority 7 Alarms', 'Alarms and Events', 'Priority 7 Alarm Viewer', 'This display will recieve all priority 7 alarm events as they happen in the system.');
insert into display values(12, 'Priority 8 Alarms', 'Alarms and Events', 'Priority 8 Alarm Viewer', 'This display will recieve all priority 8 alarm events as they happen in the system.');
insert into display values(13, 'Priority 9 Alarms', 'Alarms and Events', 'Priority 9 Alarm Viewer', 'This display will recieve all priority 9 alarm events as they happen in the system.');
insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'Priority 10 Alarm Viewer', 'This display will recieve all priority 10 alarm events as they happen in the system.');
insert into display values(15, 'Priority 11 Alarms', 'Alarms and Events', 'Priority 11 Alarm Viewer', 'This display will recieve all priority 11 alarm events as they happen in the system.');
insert into display values(16, 'Priority 12 Alarms', 'Alarms and Events', 'Priority 12 Alarm Viewer', 'This display will recieve all priority 12 alarm events as they happen in the system.');
insert into display values(17, 'Priority 13 Alarms', 'Alarms and Events', 'Priority 13 Alarm Viewer', 'This display will recieve all priority 13 alarm events as they happen in the system.');
insert into display values(18, 'Priority 14 Alarms', 'Alarms and Events', 'Priority 14 Alarm Viewer', 'This display will recieve all priority 14 alarm events as they happen in the system.');
insert into display values(19, 'Priority 15 Alarms', 'Alarms and Events', 'Priority 15 Alarm Viewer', 'This display will recieve all priority 15 alarm events as they happen in the system.');
insert into display values(20, 'Priority 16 Alarms', 'Alarms and Events', 'Priority 16 Alarm Viewer', 'This display will recieve all priority 16 alarm events as they happen in the system.');
insert into display values(21, 'Priority 17 Alarms', 'Alarms and Events', 'Priority 17 Alarm Viewer', 'This display will recieve all priority 17 alarm events as they happen in the system.');
insert into display values(22, 'Priority 18 Alarms', 'Alarms and Events', 'Priority 18 Alarm Viewer', 'This display will recieve all priority 18 alarm events as they happen in the system.');
insert into display values(23, 'Priority 19 Alarms', 'Alarms and Events', 'Priority 19 Alarm Viewer', 'This display will recieve all priority 19 alarm events as they happen in the system.');
insert into display values(24, 'Priority 20 Alarms', 'Alarms and Events', 'Priority 20 Alarm Viewer', 'This display will recieve all priority 20 alarm events as they happen in the system.');
insert into display values(25, 'Priority 21 Alarms', 'Alarms and Events', 'Priority 21 Alarm Viewer', 'This display will recieve all priority 21 alarm events as they happen in the system.');
insert into display values(26, 'Priority 22 Alarms', 'Alarms and Events', 'Priority 22 Alarm Viewer', 'This display will recieve all priority 22 alarm events as they happen in the system.');
insert into display values(27, 'Priority 23 Alarms', 'Alarms and Events', 'Priority 23 Alarm Viewer', 'This display will recieve all priority 23 alarm events as they happen in the system.');
insert into display values(28, 'Priority 24 Alarms', 'Alarms and Events', 'Priority 24 Alarm Viewer', 'This display will recieve all priority 24 alarm events as they happen in the system.');
insert into display values(29, 'Priority 25 Alarms', 'Alarms and Events', 'Priority 25 Alarm Viewer', 'This display will recieve all priority 25 alarm events as they happen in the system.');
insert into display values(30, 'Priority 26 Alarms', 'Alarms and Events', 'Priority 26 Alarm Viewer', 'This display will recieve all priority 26 alarm events as they happen in the system.');
insert into display values(31, 'Priority 27 Alarms', 'Alarms and Events', 'Priority 27 Alarm Viewer', 'This display will recieve all priority 27 alarm events as they happen in the system.');
insert into display values(32, 'Priority 28 Alarms', 'Alarms and Events', 'Priority 28 Alarm Viewer', 'This display will recieve all priority 28 alarm events as they happen in the system.');
insert into display values(33, 'Priority 29 Alarms', 'Alarms and Events', 'Priority 29 Alarm Viewer', 'This display will recieve all priority 29 alarm events as they happen in the system.');
insert into display values(34, 'Priority 30 Alarms', 'Alarms and Events', 'Priority 30 Alarm Viewer', 'This display will recieve all priority 30 alarm events as they happen in the system.');
insert into display values(35, 'Priority 31 Alarms', 'Alarms and Events', 'Priority 31 Alarm Viewer', 'This display will recieve all priority 31 alarm events as they happen in the system.');

insert into display values(50, 'SOE Log', 'Alarms and Events', 'SOE Log Viewer', 'This display shows all the SOE events in the SOE log table for a given day.');
insert into display values(51, 'TAG Log', 'Alarms and Events', 'TAG Log Viewer', 'This display shows all the TAG events in the TAG log table for a given day.');

insert into display values(99, 'Your Custom Display', 'Custom Displays', 'Edit This Display', 'This display is is used to show what a user created display looks like. You may edit this display to fit your own needs.');


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


insert into displaycolumns values(99, 'Device Name', 5, 1, 90 );
insert into displaycolumns values(99, 'Point Name', 2, 2, 90 );
insert into displaycolumns values(99, 'Value', 9, 3, 90 );
insert into displaycolumns values(99, 'Quality', 10, 4, 70 );
insert into displaycolumns values(99, 'Time Stamp', 11, 1, 140 );

insert into displaycolumns values(1, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(1, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(1, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(1, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(1, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(1, 'User Name', 8, 6, 35 );

/*********************************************************************************************************
This use to be for the Historical Data View and RawPointHistory view,  they have been deleted and no longer used as of 7-11-2001
*********************************************************************************************************
insert into displaycolumns values(2, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(2, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(2, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(2, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(2, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(2, 'User Name', 8, 6, 35 );
insert into displaycolumns values(3, 'Time Stamp', 11, 1, 70 );
insert into displaycolumns values(3, 'Device Name', 5, 2, 60 );
insert into displaycolumns values(3, 'Point Name', 2, 3, 60 );
insert into displaycolumns values(3, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(3, 'Additional Info', 7, 5, 200 );
insert into displaycolumns values(3, 'User Name', 8, 6, 35 );
*********************************************************************************************************/


insert into displaycolumns values(4, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(4, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(4, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(4, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(4, 'User Name', 8, 5, 50 );

insert into displaycolumns values(5, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(5, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(5, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(5, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(5, 'User Name', 8, 5, 50 );
insert into displaycolumns values(6, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(6, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(6, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(6, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(6, 'User Name', 8, 5, 50 );
insert into displaycolumns values(7, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(7, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(7, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(7, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(7, 'User Name', 8, 5, 50 );
insert into displaycolumns values(8, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(8, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(8, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(8, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(8, 'User Name', 8, 5, 50 );
insert into displaycolumns values(9, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(9, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(9, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(9, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(9, 'User Name', 8, 5, 50 );
insert into displaycolumns values(10, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(10, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(10, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(10, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(10, 'User Name', 8, 5, 50 );
insert into displaycolumns values(11, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(11, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(11, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(11, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(11, 'User Name', 8, 5, 50 );
insert into displaycolumns values(12, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(12, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(12, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(12, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(12, 'User Name', 8, 5, 50 );
insert into displaycolumns values(13, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(13, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(13, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(13, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(13, 'User Name', 8, 5, 50 );
insert into displaycolumns values(14, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(14, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(14, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(14, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(14, 'User Name', 8, 5, 50 );

insert into displaycolumns values(15, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(15, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(15, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(15, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(15, 'User Name', 8, 5, 50 );

insert into displaycolumns values(16, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(16, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(16, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(16, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(16, 'User Name', 8, 5, 50 );

insert into displaycolumns values(17, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(17, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(17, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(17, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(17, 'User Name', 8, 5, 50 );

insert into displaycolumns values(18, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(18, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(18, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(18, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(18, 'User Name', 8, 5, 50 );

insert into displaycolumns values(19, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(19, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(19, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(19, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(19, 'User Name', 8, 5, 50 );

insert into displaycolumns values(20, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(20, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(20, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(20, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(20, 'User Name', 8, 5, 50 );

insert into displaycolumns values(21, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(21, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(21, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(21, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(21, 'User Name', 8, 5, 50 );

insert into displaycolumns values(22, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(22, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(22, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(22, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(22, 'User Name', 8, 5, 50 );

insert into displaycolumns values(23, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(23, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(23, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(23, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(23, 'User Name', 8, 5, 50 );

insert into displaycolumns values(24, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(24, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(24, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(24, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(24, 'User Name', 8, 5, 50 );

insert into displaycolumns values(25, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(25, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(25, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(25, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(25, 'User Name', 8, 5, 50 );

insert into displaycolumns values(26, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(26, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(26, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(26, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(26, 'User Name', 8, 5, 50 );

insert into displaycolumns values(27, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(27, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(27, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(27, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(27, 'User Name', 8, 5, 50 );

insert into displaycolumns values(28, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(28, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(28, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(28, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(28, 'User Name', 8, 5, 50 );

insert into displaycolumns values(29, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(29, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(29, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(29, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(29, 'User Name', 8, 5, 50 );

insert into displaycolumns values(30, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(30, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(30, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(30, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(30, 'User Name', 8, 5, 50 );

insert into displaycolumns values(31, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(31, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(31, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(31, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(31, 'User Name', 8, 5, 50 );

insert into displaycolumns values(32, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(32, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(32, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(32, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(32, 'User Name', 8, 5, 50 );

insert into displaycolumns values(33, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(33, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(33, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(33, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(33, 'User Name', 8, 5, 50 );

insert into displaycolumns values(34, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(34, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(34, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(34, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(34, 'User Name', 8, 5, 50 );

insert into displaycolumns values(35, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(35, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(35, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(35, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(35, 'User Name', 8, 5, 50 );

insert into displaycolumns values(50, 'Device Name', 5, 1, 70 );
insert into displaycolumns values(50, 'Point Name', 2, 2, 70 );
insert into displaycolumns values(50, 'Time Stamp', 11, 3, 60 );
insert into displaycolumns values(50, 'Description', 12, 4, 180 );
insert into displaycolumns values(50, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(51, 'Device Name', 5, 1, 70 );
insert into displaycolumns values(51, 'Point Name', 2, 2, 70 );
insert into displaycolumns values(51, 'Time Stamp', 11, 3, 60 );
insert into displaycolumns values(51, 'Description', 12, 4, 180 );
insert into displaycolumns values(51, 'Additional Info', 7, 5, 180 );
insert into displaycolumns values(51, 'User Name', 8, 6, 40 );
insert into displaycolumns values(51, 'Tag', 13, 7, 60 );

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
/* Table : DeviceTypeCommand                                    */
/*==============================================================*/


create table DeviceTypeCommand  (
   DeviceCommandID      NUMBER                           not null,
   CommandID            NUMBER                           not null,
   DeviceType           VARCHAR2(32)                     not null,
   DisplayOrder         NUMBER                           not null,
   VisibleFlag          VARCHAR2(1)                      not null
)
/


alter table DeviceTypeCommand
   add constraint PK_DEVICETYPECOMMAND primary key (DeviceCommandID)
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


insert into EnergyCompany VALUES (-1,'Default Energy Company',0,-100);

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


INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-100);

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


insert into FDRInterface values ( 1, 'INET', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values ( 2, 'ACS', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 3, 'VALMET', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 4, 'CYGNET', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 5, 'STEC', 'Receive', 'f' );
insert into FDRInterface values ( 6, 'RCCS', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values ( 7, 'TRISTATE', 'Receive', 'f' );
insert into FDRInterface values ( 8, 'RDEX', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values (9,'SYSTEM','Link Status','f');
insert into FDRInterface values (10,'DSM2IMPORT','Receive,Receive for control','f');
insert into FDRInterface values (11,'TELEGYR','Receive,Receive for control','f');
insert into FDRInterface values (12,'TEXTIMPORT','Receive,Receive for control','f');
insert into FDRInterface values (13,'TEXTEXPORT','Send','f');

insert into fdrinterface values(16,'LODESTAR_STD','Receive','f');
insert into fdrinterface values(17,'LODESTAR_ENH','Receive','f');
insert into fdrinterface values (18, 'DSM2FILEIN', 'Receive,Receive for control', 'f');

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


insert into FDRInterfaceOption values(1, 'Device', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Point', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Destination/Source', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'Category', 1, 'Combo', 'PSEUDO,REAL,CALCULATED' );
insert into FDRInterfaceOption values(2, 'Remote', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'Point', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(3, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(4, 'PointID', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(5, 'Point', 1, 'Combo', 'SYSTEM LOAD,STEC LOAD' );
insert into FDRInterfaceOption values(6, 'Device', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(6, 'Point', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(6, 'Destination/Source', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(7, 'Point', 1, 'Combo', 'SYSTEM LOAD,30 MINUTE AVG' );
insert into FDRInterfaceOption values(8, 'Translation', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(8, 'Destination/Source', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(9,'Client',1,'Text','(none)');
insert into FDRInterfaceOption values(10,'Point',1,'Text','(none)');
insert into FDRInterfaceOption values(11, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(11, 'Interval (sec)', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(12,'Point ID',1,'Text','(none)');
insert into FDRInterfaceOption values(13,'Point ID',1,'Text','(none)');

insert into fdrinterfaceoption values (16,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values (16,'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values (16,'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values (16,'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values (17,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values (17,'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values (17,'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values (17,'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values(18, 'Option Number', 1, 'Combo', '1');
insert into fdrinterfaceoption values(18, 'Point ID', 2, 'Text', '(none)');


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


insert into HolidaySchedule values( 0, 'Empty Holiday Schedule' );

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


insert into LMGroupExpressComAddress values( 0, '(none)', 0, '(none)' );

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


insert into LMProgram values(0, 'Automatic', 0);

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


insert into LMProgramConstraints values (0, 'Default Constraint', 'YYYYYYYN', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

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


insert into notificationgroup values(1,'(none)','(none)','(none)','(none)','(none)','N');

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


INSERT into point  values (0,   'System', 'System Point', 0, 'Default', 0, 'N', 'N', 'S', 0  ,'None', 0);
INSERT into point  values (-1,  'System', 'Porter', 0, 'Default', 0, 'N', 'N', 'S', 1  ,'None', 0);
INSERT into point  values (-2,  'System', 'Scanner', 0, 'Default', 0, 'N', 'N', 'S', 2  ,'None', 0);
INSERT into point  values (-3,  'System', 'Dispatch', 0, 'Default', 0, 'N', 'N', 'S', 3  ,'None', 0);
INSERT into point  values (-4,  'System', 'Macs', 0, 'Default', 0, 'N', 'N', 'S', 4  ,'None', 0);
INSERT into point  values (-5,  'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 'S', 5  ,'None', 0);
INSERT into point  values (-10, 'System', 'Load Management' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);
INSERT into point  values (-100, 'System', 'Threshold' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);

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


insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;

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


INSERT INTO Route VALUES (0,0,'N');

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


INSERT INTO State VALUES ( -1, 0, 'AnalogText', 0, 6 , 0);
INSERT INTO State VALUES ( -2, 0, 'AccumulatorText', 0, 6 , 0);
INSERT INTO State VALUES ( -3, 0, 'CalculatedText', 0, 6 , 0);
INSERT INTO State VALUES ( 0, 0, 'SystemText', 0, 6 , 0);
INSERT INTO State VALUES ( 1, -1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES ( 1, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES ( 1, 1, 'Closed', 1, 6 , 0);
INSERT INTO State VALUES ( 2, -1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES ( 2, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES ( 2, 1, 'Closed', 1, 6 , 0);
INSERT INTO State VALUES ( 2, 2, 'Unknown', 2, 6 , 0);
INSERT INTO State VALUES ( 3, -1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES ( 3, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES ( 3, 1, 'Close', 1, 6 , 0);
INSERT INTO State VALUES ( 3, 2, 'OpenQuestionable', 2, 6 , 0);
INSERT INTO State VALUES ( 3, 3, 'CloseQuestionable', 3, 6 , 0);
INSERT INTO State VALUES ( 3, 4, 'OpenFail', 4, 6 , 0);
INSERT INTO State VALUES ( 3, 5, 'CloseFail', 5, 6 , 0);
INSERT INTO State VALUES ( 3, 6, 'OpenPending', 7, 6 , 0);
INSERT INTO State VALUES ( 3, 7, 'ClosePending', 8, 6 , 0);
INSERT INTO State VALUES(-5, 0, 'Events', 2, 6, 0);
INSERT INTO State VALUES(-5, 1, 'Priority 1', 1, 6, 0);
INSERT INTO State VALUES(-5, 2, 'Priority 2', 4, 6, 0);
INSERT INTO State VALUES(-5, 3, 'Priority 3', 0, 6, 0);
INSERT INTO State VALUES(-5, 4, 'Priority 4', 7, 6, 0);
INSERT INTO State VALUES(-5, 5, 'Priority 5', 8, 6, 0);
INSERT INTO State VALUES(-5, 6, 'Priority 6', 5, 6, 0);
INSERT INTO State VALUES(-5, 7, 'Priority 7', 3, 6, 0);
INSERT INTO State VALUES(-5, 8, 'Priority 8', 2, 6, 0);
INSERT INTO State VALUES(-5, 9, 'Priority 9', 10, 6, 0);
INSERT INTO State VALUES(-5, 10, 'Priority 10', 9, 6, 0);

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


INSERT INTO StateGroup VALUES ( -1, 'DefaultAnalog', 'Analog' );
INSERT INTO StateGroup VALUES ( -2, 'DefaultAccumulator', 'Accumulator' );
INSERT INTO StateGroup VALUES ( -3, 'DefaultCalculated', 'Calculated' );
INSERT INTO StateGroup VALUES (-5, 'Event Priority', 'System' );
INSERT INTO StateGroup VALUES ( 0, 'SystemState', 'System' );
INSERT INTO StateGroup VALUES ( 1, 'TwoStateStatus', 'Status' );
INSERT INTO StateGroup VALUES ( 2, 'ThreeStateStatus', 'Status' );
INSERT INTO StateGroup VALUES ( 3, 'CapBankStatus', 'Status' );

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


insert into SeasonSchedule values( 0, 'Empty Schedule' );

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


insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon  Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon  Template');


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


insert into templatecolumns values( 1, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 1, 'Point Name', 2, 2, 85 );
insert into templatecolumns values( 1, 'Value', 9, 3, 85 );
insert into templatecolumns values( 1, 'Quality', 10, 4, 80 );
insert into templatecolumns values( 1, 'Time', 11, 5, 135 );
insert into templatecolumns values( 1, 'State', 13, 6, 80 );

insert into templatecolumns values( 2, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 2, 'Value', 9, 2, 85 );
insert into templatecolumns values( 2, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 2, 'Time', 11, 4, 135 );
insert into templatecolumns values( 2, 'State', 13, 5, 80 );

insert into templatecolumns values( 3, 'Point Name', 2, 1, 85 );
insert into templatecolumns values( 3, 'Value', 9, 2, 85 );
insert into templatecolumns values( 3, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 3, 'Time', 11, 4, 135 );
insert into templatecolumns values( 3, 'State', 13, 5, 80 );


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


insert into tags values(-1, 'Out Of Service', 1, 'Y', 1, 0);
insert into tags values(-2, 'Info', 1, 'N', 6, 0);

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


INSERT INTO UnitMeasure VALUES ( 0,'kW', 0,'kW','(none)' );
INSERT INTO UnitMeasure VALUES ( 1,'kWH', 0,'kWH','usage' );
INSERT INTO UnitMeasure VALUES ( 2,'kVA', 0,'kVA','(none)' );
INSERT INTO UnitMeasure VALUES ( 3,'kVAr', 0,'kVAr','(none)' );
INSERT INTO UnitMeasure VALUES ( 4,'kVAh', 0,'kVAh','usage' );
INSERT INTO UnitMeasure VALUES ( 5,'kVArh', 0,'kVArh','usage' );
INSERT INTO UnitMeasure VALUES ( 6,'kVolts', 0,'kVolts','(none)' );
INSERT INTO UnitMeasure VALUES ( 7,'kQ', 0,'kQ','(none)' );
INSERT INTO UnitMeasure VALUES ( 8,'Amps', 0,'Amps','(none)' );
INSERT INTO UnitMeasure VALUES ( 9,'Counts', 0,'Counts','(none)' );
INSERT INTO UnitMeasure VALUES ( 10,'Degrees', 0,'Degrees','(none)' );
INSERT INTO UnitMeasure VALUES ( 11,'Dollars', 0,'Dollars','(none)' );
INSERT INTO UnitMeasure VALUES ( 12,'$', 0,'Dollar Char','(none)' );
INSERT INTO UnitMeasure VALUES ( 13,'Feet', 0,'Feet','(none)' );
INSERT INTO UnitMeasure VALUES ( 14,'Gallons', 0,'Gallons','(none)' );
INSERT INTO UnitMeasure VALUES ( 15,'Gal/PM', 0,'Gal/PM','(none)' );
INSERT INTO UnitMeasure VALUES ( 16,'GAS-CFT', 0,'GAS-CFT','(none)' );
INSERT INTO UnitMeasure VALUES ( 17,'Hours', 0,'Hours','(none)' );
INSERT INTO UnitMeasure VALUES ( 18,'Level', 0,'Level','(none)' );
INSERT INTO UnitMeasure VALUES ( 19,'Minutes', 0,'Minutes','(none)' );
INSERT INTO UnitMeasure VALUES ( 20,'MW', 0,'MW','(none)' );
INSERT INTO UnitMeasure VALUES ( 21,'MWh', 0,'MWh','usage' );
INSERT INTO UnitMeasure VALUES ( 22,'MVA', 0,'MVA','(none)' );
INSERT INTO UnitMeasure VALUES ( 23,'MVAr', 0,'MVAr','(none)' );
INSERT INTO UnitMeasure VALUES ( 24,'MVAh', 0,'MVAh','usage' );
INSERT INTO UnitMeasure VALUES ( 25,'MVArh', 0,'MVArh','usage' );
INSERT INTO UnitMeasure VALUES ( 26,'Ops.', 0,'Ops','(none)' );
INSERT INTO UnitMeasure VALUES ( 27,'PF', 0,'PF','(none)' );
INSERT INTO UnitMeasure VALUES ( 28,'Percent', 0,'Percent','(none)' );
INSERT INTO UnitMeasure VALUES ( 29,'%', 0,'Percent Char','(none)' );
INSERT INTO UnitMeasure VALUES ( 30,'PSI', 0,'PSI','(none)' );
INSERT INTO UnitMeasure VALUES ( 31,'Seconds', 0,'Seconds','(none)' );
INSERT INTO UnitMeasure VALUES ( 32,'Temp-F', 0,'Temp-F','(none)' );
INSERT INTO UnitMeasure VALUES ( 33,'Temp-C', 0,'Temp-C','(none)' );
INSERT INTO UnitMeasure VALUES ( 34,'Vars', 0,'Vars','(none)' );
INSERT INTO UnitMeasure VALUES ( 35,'Volts', 0,'Volts','(none)' );
INSERT INTO UnitMeasure VALUES ( 36,'VoltAmps', 0,'VoltAmps','(none)' );
INSERT INTO UnitMeasure VALUES ( 37,'VA', 0,'VA','(none)' );
INSERT INTO UnitMeasure VALUES ( 38,'Watr-CFT', 0,'Watr-CFT','(none)' );
INSERT INTO UnitMeasure VALUES ( 39,'Watts', 0,'Watts','(none)' );
INSERT INTO UnitMeasure VALUES ( 40,'Hz', 0,'Hertz','(none)' );
INSERT INTO UnitMeasure VALUES ( 41,'Volts', 1,'Volts from V2H','(none)' );
INSERT INTO UnitMeasure VALUES ( 42,'Amps', 1,'Amps from A2H','(none)' );
INSERT INTO UnitMeasure VALUES ( 43,'Tap', 0,'LTC Tap Position','(none)' );
INSERT INTO UnitMeasure VALUES ( 44,'Miles', 0,'Miles','(none)' );
INSERT INTO UnitMeasure VALUES ( 45,'Ms', 0,'Milliseconds','(none)' );
INSERT INTO UnitMeasure VALUES( 46,'PPM',0,'Parts Per Million','(none)');
INSERT INTO UnitMeasure VALUES( 47,'MPH',0,'Miles Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 48,'Inches',0,'Inches','(none)');
INSERT INTO UnitMeasure VALUES( 49,'KPH',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 50,'Milibars',0,'Milibars','(none)');
INSERT INTO UnitMeasure VALUES( 51,'km/h',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 52,'m/s',0,'Meters Per Second','(none)');
INSERT INTO UnitMeasure VALUES( 53,'KV', 0,'KVolts','(none)' );
INSERT INTO UnitMeasure VALUES( 54,'UNDEF', 0,'Undefined','(none)' );
INSERT INTO UnitMeasure VALUES( 55,'A', 0,'Amps (A)','(none)' );

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


insert into YukonGroup values(-1,'Yukon Grp','The default system user group that allows limited user interaction.');
insert into YukonGroup values(-2,'System Administrator Grp','A set of roles that allow administrative access to the system.');
insert into YukonGroup values(-100,'Operators Grp', 'The default group of yukon operators');
insert into yukongroup values(-200,'Esub Users Grp', 'The default group of esubstation users');
insert into yukongroup values(-201,'Esub Operators Grp', 'The default group of esubstation operators');
insert into yukongroup values (-301,'Web Client Operators Grp','The default group of web client operators');
insert into yukongroup values (-300,'Residential Customers Grp','The default group of residential customers');
insert into yukongroup values (-302, 'Web Client Customers Grp', 'The default group of web client customers');
insert into yukongroup values (-303,'STARS Operators Grp','The default group for STARS operators');
insert into yukongroup values (-304,'STARS Residential Customers Grp','The default group for STARS residential customers');

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


/* Assign the default Yukon role to the default Yukon group */
insert into YukonGroupRole values(-1,-1,-1,-1000,'(none)');
insert into YukonGroupRole values(-2,-1,-1,-1001,'(none)');
insert into YukonGroupRole values(-3,-1,-1,-1002,'(none)');
insert into YukonGroupRole values(-4,-1,-1,-1003,'(none)');
insert into YukonGroupRole values(-5,-1,-1,-1004,'(none)');
insert into YukonGroupRole values(-6,-1,-1,-1005,'(none)');
insert into YukonGroupRole values(-7,-1,-1,-1006,'(none)');
insert into YukonGroupRole values(-8,-1,-1,-1007,'(none)');
insert into YukonGroupRole values(-9,-1,-1,-1008,'(none)');
insert into YukonGroupRole values(-10,-1,-1,-1009,'(none)');
insert into YukonGroupRole values(-11,-1,-1,-1010,'(none)');
insert into YukonGroupRole values(-12,-1,-1,-1011,'(none)');
insert into YukonGroupRole values(-13,-1,-1,-1012,'(none)');
insert into YukonGroupRole values(-14,-1,-1,-1013,'(none)');
insert into YukonGroupRole values(-15,-1,-1,-1014,'CannonLogo.gif');
insert into YukonGroupRole values(-16,-1,-1,-1015,'(none)');
insert into YukonGroupRole values(-17,-1,-1,-1016,'(none)');
insert into YukonGroupRole values(-18,-1,-1,-1017,'(none)');

insert into YukonGroupRole values(-70,-1,-5,-1400,'(none)');
insert into YukonGroupRole values(-71,-1,-5,-1401,'(none)');
insert into YukonGroupRole values(-72,-1,-5,-1402,'(none)');

insert into YukonGroupRole values(-85,-1,-4,-1300,'(none)');
insert into YukonGroupRole values(-86,-1,-4,-1301,'(none)');
insert into YukonGroupRole values(-87,-1,-4,-1302,'(none)');
insert into YukonGroupRole values(-88,-1,-4,-1303,'(none)');
insert into YukonGroupRole values(-89,-1,-4,-1304,'(none)');
insert into YukonGroupRole values(-90,-1,-4,-1305,'(none)');


/* Assign roles to the default operator group to allow them to use all the main rich Yukon applications */
/* Database Editor */
insert into YukonGroupRole values(-100,-100,-100,-10000,'(none)');
insert into YukonGroupRole values(-101,-100,-100,-10001,'(none)');
insert into YukonGroupRole values(-102,-100,-100,-10002,'(none)');
insert into YukonGroupRole values(-103,-100,-100,-10003,'(none)');
insert into YukonGroupRole values(-104,-100,-100,-10004,'(none)');
insert into YukonGroupRole values(-105,-100,-100,-10005,'(none)');
insert into YukonGroupRole values(-107,-100,-100,-10007,'(none)');
insert into YukonGroupRole values(-108,-100,-100,-10008,'(none)');
insert into YukonGroupRole values(-109,-100,-100,-10009,'(none)');


/* TDC */
insert into YukonGroupRole values(-120,-100,-101,-10100,'(none)');
insert into YukonGroupRole values(-121,-100,-101,-10101,'(none)');
insert into YukonGroupRole values(-122,-100,-101,-10102,'(none)');
insert into YukonGroupRole values(-123,-100,-101,-10103,'(none)');
insert into YukonGroupRole values(-124,-100,-101,-10104,'(none)');
insert into YukonGroupRole values(-125,-100,-101,-10105,'(none)');
insert into YukonGroupRole values(-126,-100,-101,-10106,'(none)');
insert into YukonGroupRole values(-127,-100,-101,-10107,'(none)');
insert into YukonGroupRole values(-128,-100,-101,-10108,'(none)');
insert into YukonGroupRole values(-129,-100,-101,-10109,'(none)');

/* Trending */
insert into YukonGroupRole values(-150,-100,-102,-10200,'(none)');

/* Commander */
insert into YukonGroupRole values(-170,-100,-103,-10300,'(none)');
insert into YukonGroupRole values(-171,-100,-103,-10301,'true');
insert into YukonGroupRole values(-172,-100,-103,-10302,'true');
insert into YukonGroupRole values(-173,-100,-103,-10303,'false');
insert into YukonGroupRole values(-174,-100,-103,-10304,'false');

/* Calc Historical for Yukon Gorup */
insert into YukonGroupRole values(-190,-1,-104,-10400,'(none)');
insert into YukonGroupRole values(-191,-1,-104,-10401,'(none)');
insert into YukonGroupRole values(-192,-1,-104,-10402,'(none)');

/* Web Graph for Yukon Gorup */
insert into YukonGroupRole values(-210,-1,-105,-10500,'(none)');
insert into YukonGroupRole values(-211,-1,-105,-10501,'(none)');

/* Billing for Yukon Gorup */
insert into YukonGroupRole values(-230,-1,-106,-10600,'(none)');
insert into YukonGroupRole values(-231,-1,-106,-10601,'(none)');

/* Esubstation Editor */
insert into YukonGroupRole values(-250,-100,-107,-10700,'(none)');

/* Assign roles to the default Esub Users */
insert into YukonGroupRole values(-300,-200,-206,-20600,'(none)');
insert into YukonGroupRole values(-301,-200,-206,-20601,'(none)');
insert into YukonGroupRole values(-302,-200,-206,-20602,'(none)');

/* Assign roles to the default Esub Operators */
insert into YukonGroupRole values(-350,-201,-206,-20600,'(none)');
insert into YukonGroupRole values(-351,-201,-206,-20601,'true');
insert into YukonGroupRole values(-352,-201,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into yukongrouprole values (-400, -302, -108, -10800, '/user/CILC/user_trending.jsp');
insert into yukongrouprole values (-402, -302, -108, -10802, '(none)');
insert into yukongrouprole values (-403, -302, -108, -10803, '(none)');
insert into yukongrouprole values (-404, -302, -108, -10804, '(none)');
insert into yukongrouprole values (-405, -302, -108, -10805, '(none)');
insert into yukongrouprole values (-406, -302, -108, -10806, '(none)');

/* Web Client Customers Direct Load Control role */
insert into yukongrouprole values (-407, -302, -300, -30000, '(none)');
insert into yukongrouprole values (-408, -302, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into yukongrouprole values (-409, -302, -301, -30100, '(none)');
insert into yukongrouprole values (-410, -302, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into yukongrouprole values (-411, -302, -302, -30200, '(none)');
insert into yukongrouprole values (-412, -302, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into yukongrouprole values (-413, -302, -304, -30400, '(none)');
insert into yukongrouprole values (-414, -302, -304, -30401, 'true');

/* Web Client Customers Administrator role */
insert into yukongrouprole values (-415, -302, -305, -30500, 'true');
insert into YukonGroupRole values (-416, -302, -304, -30402, '(none)');
insert into YukonGroupRole values (-417, -302, -304, -30403, '(none)');

insert into yukongrouprole values (-500,-300,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (-502,-300,-108,-10802,'(none)');
insert into yukongrouprole values (-503,-300,-108,-10803,'(none)');
insert into yukongrouprole values (-504,-300,-108,-10804,'(none)');
insert into yukongrouprole values (-505,-300,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-506,-300,-108,-10806,'(none)');
insert into yukongrouprole values (-507,-300,-108,-10807,'(none)');
insert into yukongrouprole values (-508,-300,-108,-10808,'(none)');

insert into yukongrouprole values (-521,-300,-400,-40001,'true');
insert into yukongrouprole values (-522,-300,-400,-40002,'false');
insert into yukongrouprole values (-523,-300,-400,-40003,'true');
insert into yukongrouprole values (-524,-300,-400,-40004,'true');
insert into yukongrouprole values (-525,-300,-400,-40005,'true');
insert into yukongrouprole values (-526,-300,-400,-40006,'true');
insert into yukongrouprole values (-527,-300,-400,-40007,'true');
insert into yukongrouprole values (-528,-300,-400,-40008,'true');
insert into yukongrouprole values (-529,-300,-400,-40009,'true');
insert into yukongrouprole values (-530,-300,-400,-40010,'true');

insert into yukongrouprole values (-551,-300,-400,-40051,'false');
insert into yukongrouprole values (-552,-300,-400,-40052,'false');
insert into yukongrouprole values (-554,-300,-400,-40054,'false');
insert into yukongrouprole values (-555,-300,-400,-40055,'(none)');

insert into yukongrouprole values (-600,-300,-400,-40100,'(none)');
insert into yukongrouprole values (-602,-300,-400,-40102,'(none)');

insert into yukongrouprole values (-610,-300,-400,-40110,'(none)');
insert into yukongrouprole values (-611,-300,-400,-40111,'(none)');
insert into yukongrouprole values (-612,-300,-400,-40112,'(none)');
insert into yukongrouprole values (-613,-300,-400,-40113,'(none)');
insert into yukongrouprole values (-614,-300,-400,-40114,'(none)');
insert into yukongrouprole values (-615,-300,-400,-40115,'(none)');
insert into yukongrouprole values (-616,-300,-400,-40116,'(none)');
insert into yukongrouprole values (-617,-300,-400,-40117,'(none)');

insert into yukongrouprole values (-630,-300,-400,-40130,'(none)');
insert into yukongrouprole values (-631,-300,-400,-40131,'(none)');
insert into yukongrouprole values (-632,-300,-400,-40132,'(none)');
insert into yukongrouprole values (-633,-300,-400,-40133,'(none)');
insert into yukongrouprole values (-634,-300,-400,-40134,'(none)');
insert into yukongrouprole values (-635,-300,-400,-40135,'(none)');
insert into yukongrouprole values (-636,-300,-400,-40136,'(none)');
insert into yukongrouprole values (-637,-300,-400,-40137,'(none)');
insert into yukongrouprole values (-638,-300,-400,-40138,'(none)');
insert into yukongrouprole values (-639,-300,-400,-40139,'(none)');

insert into yukongrouprole values (-650,-300,-400,-40150,'(none)');
insert into yukongrouprole values (-651,-300,-400,-40151,'(none)');
insert into yukongrouprole values (-652,-300,-400,-40152,'(none)');
insert into yukongrouprole values (-653,-300,-400,-40153,'(none)');
insert into yukongrouprole values (-654,-300,-400,-40154,'(none)');
insert into yukongrouprole values (-655,-300,-400,-40155,'(none)');
insert into yukongrouprole values (-656,-300,-400,-40156,'(none)');
insert into yukongrouprole values (-657,-300,-400,-40157,'(none)');
insert into yukongrouprole values (-658,-300,-400,-40158,'(none)');
insert into yukongrouprole values (-659,-300,-400,-40159,'(none)');

insert into yukongrouprole values (-670,-300,-400,-40170,'(none)');
insert into yukongrouprole values (-671,-300,-400,-40171,'(none)');
insert into yukongrouprole values (-672,-300,-400,-40172,'(none)');
insert into yukongrouprole values (-673,-300,-400,-40173,'(none)');

insert into yukongrouprole values (-680,-300,-400,-40180,'(none)');
insert into yukongrouprole values (-681,-300,-400,-40181,'(none)');

insert into yukongrouprole values (-690,-300,-400,-40190,'(none)');
insert into yukongrouprole values (-691,-300,-400,-40191,'(none)');
insert into yukongrouprole values (-692,-300,-400,-40192,'(none)');
insert into yukongrouprole values (-693,-300,-400,-40193,'(none)');
insert into yukongrouprole values (-694,-300,-400,-40194,'(none)');
insert into yukongrouprole values (-695,-300,-400,-40195,'(none)');
insert into yukongrouprole values (-696,-300,-400,-40196,'(none)');

insert into yukongrouprole values (-700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-702,-301,-108,-10802,'(none)');
insert into yukongrouprole values (-703,-301,-108,-10803,'(none)');
insert into yukongrouprole values (-704,-301,-108,-10804,'(none)');
insert into yukongrouprole values (-705,-301,-108,-10805,'(none)');
insert into yukongrouprole values (-706,-301,-108,-10806,'(none)');
insert into yukongrouprole values (-707,-301,-108,-10807,'(none)');
insert into yukongrouprole values (-708,-301,-108,-10808,'(none)');

insert into yukongrouprole values (-721,-301,-201,-20101,'true');
insert into yukongrouprole values (-722,-301,-201,-20102,'true');
insert into yukongrouprole values (-723,-301,-201,-20103,'true');
insert into yukongrouprole values (-724,-301,-201,-20104,'false');
insert into yukongrouprole values (-725,-301,-201,-20105,'false');
insert into yukongrouprole values (-726,-301,-201,-20106,'true');
insert into yukongrouprole values (-727,-301,-201,-20107,'true');
insert into yukongrouprole values (-728,-301,-201,-20108,'true');
insert into yukongrouprole values (-729,-301,-201,-20109,'true');
insert into yukongrouprole values (-730,-301,-201,-20110,'true');
insert into yukongrouprole values (-731,-301,-201,-20111,'true');
insert into yukongrouprole values (-732,-301,-201,-20112,'true');
insert into yukongrouprole values (-733,-301,-201,-20113,'true');
insert into yukongrouprole values (-734,-301,-201,-20114,'true');
insert into yukongrouprole values (-735,-301,-201,-20115,'true');
insert into yukongrouprole values (-736,-301,-201,-20116,'true');
insert into yukongrouprole values (-737,-301,-201,-20117,'true');
insert into yukongrouprole values (-738,-301,-201,-20118,'true');

insert into yukongrouprole values (-751,-301,-201,-20151,'true');
insert into yukongrouprole values (-752,-301,-201,-20152,'(none)');
insert into yukongrouprole values (-753,-301,-201,-20153,'(none)');
insert into yukongrouprole values (-754,-301,-201,-20154,'false');
insert into yukongrouprole values (-755,-301,-201,-20155,'true');
insert into yukongrouprole values (-756,-301,-201,-20156,'true');
insert into yukongrouprole values (-757,-301,-201,-20157,'(none)');
insert into yukongrouprole values (-758,-301,-201,-20158,'(none)');

insert into yukongrouprole values (-765,-301,-210,-21000,'(none)');
insert into yukongrouprole values (-766,-301,-210,-21001,'(none)');

insert into yukongrouprole values (-770,-301,-202,-20200,'(none)');
insert into yukongrouprole values (-775,-301,-203,-20300,'(none)');
insert into yukongrouprole values (-776,-301,-203,-20301,'(none)');

insert into yukongrouprole values (-780,-301,-204,-20400,'(none)');
insert into yukongrouprole values (-785,-301,-205,-20500,'(none)');

insert into yukongrouprole values (-790,-301,-207,-20700,'(none)');
insert into yukongrouprole values (-791,-301,-209,-20900,'(none)');
insert into yukongrouprole values (-792,-301,-209,-20901,'(none)');
insert into yukongrouprole values (-793,-301,-209,-20902,'(none)');
insert into yukongrouprole values (-794,-301,-209,-20903,'(none)');
insert into yukongrouprole values (-795,-301,-209,-20904,'(none)');
insert into yukongrouprole values (-796,-301,-209,-20905,'(none)');
insert into yukongrouprole values (-797,-301,-209,-20906,'(none)');

insert into yukongrouprole values (-800,-301,-201,-20800,'(none)');
insert into yukongrouprole values (-801,-301,-201,-20801,'(none)');

insert into yukongrouprole values (-810,-301,-201,-20810,'(none)');
insert into yukongrouprole values (-813,-301,-201,-20813,'(none)');
insert into yukongrouprole values (-814,-301,-201,-20814,'(none)');
insert into yukongrouprole values (-815,-301,-201,-20815,'(none)');
insert into yukongrouprole values (-816,-301,-201,-20816,'(none)');
insert into yukongrouprole values (-817,-301,-201,-20817,'(none)');
insert into yukongrouprole values (-819,-301,-201,-20819,'(none)');
insert into yukongrouprole values (-820,-301,-201,-20820,'(none)');

insert into yukongrouprole values (-830,-301,-201,-20830,'(none)');
insert into yukongrouprole values (-831,-301,-201,-20831,'(none)');
insert into yukongrouprole values (-832,-301,-201,-20832,'(none)');
insert into yukongrouprole values (-833,-301,-201,-20833,'(none)');
insert into yukongrouprole values (-834,-301,-201,-20834,'(none)');
insert into yukongrouprole values (-835,-301,-201,-20835,'(none)');
insert into yukongrouprole values (-836,-301,-201,-20836,'(none)');
insert into yukongrouprole values (-837,-301,-201,-20837,'(none)');
insert into yukongrouprole values (-838,-301,-201,-20838,'(none)');
insert into yukongrouprole values (-839,-301,-201,-20839,'(none)');
insert into yukongrouprole values (-840,-301,-201,-20840,'(none)');
insert into yukongrouprole values (-841,-301,-201,-20841,'(none)');
insert into yukongrouprole values (-842,-301,-201,-20842,'(none)');
insert into yukongrouprole values (-843,-301,-201,-20843,'(none)');
insert into yukongrouprole values (-844,-301,-201,-20844,'(none)');
insert into yukongrouprole values (-845,-301,-201,-20845,'(none)');

insert into yukongrouprole values (-850,-301,-201,-20850,'(none)');
insert into yukongrouprole values (-851,-301,-201,-20851,'(none)');
insert into yukongrouprole values (-852,-301,-201,-20852,'(none)');
insert into yukongrouprole values (-853,-301,-201,-20853,'(none)');
insert into yukongrouprole values (-854,-301,-201,-20854,'(none)');
insert into yukongrouprole values (-855,-301,-201,-20855,'(none)');
insert into yukongrouprole values (-856,-301,-201,-20856,'(none)');
insert into yukongrouprole values (-857,-301,-201,-20857,'(none)');
insert into yukongrouprole values (-858,-301,-201,-20858,'(none)');
insert into yukongrouprole values (-859,-301,-201,-20859,'(none)');
insert into yukongrouprole values (-860,-301,-201,-20860,'(none)');
insert into yukongrouprole values (-861,-301,-201,-20861,'(none)');
insert into yukongrouprole values (-862,-301,-201,-20862,'(none)');
insert into yukongrouprole values (-863,-301,-201,-20863,'(none)');
insert into yukongrouprole values (-864,-301,-201,-20864,'(none)');

insert into yukongrouprole values (-870,-301,-201,-20870,'(none)');

insert into yukongrouprole values (-880,-301,-201,-20880,'(none)');
insert into yukongrouprole values (-881,-301,-201,-20881,'(none)');
insert into yukongrouprole values (-882,-301,-201,-20882,'(none)');
insert into yukongrouprole values (-883,-301,-201,-20883,'(none)');
insert into yukongrouprole values (-884,-301,-201,-20884,'(none)');
insert into yukongrouprole values (-885,-301,-201,-20885,'(none)');
insert into yukongrouprole values (-886,-301,-201,-20886,'(none)');
insert into yukongrouprole values (-887,-301,-201,-20887,'(none)');
insert into yukongrouprole values (-888,-301,-201,-20888,'(none)');
insert into yukongrouprole values (-889,-301,-201,-20889,'(none)');

/* Add the user-control properties to the Web Client Customers group */
insert into yukongrouprole values ( -985, -302, -306, -30600, '(none)');
insert into yukongrouprole values ( -986, -302, -306, -30601, 'true');
insert into yukongrouprole values ( -987, -302, -306, -30602, 'true');
insert into yukongrouprole values ( -988, -302, -306, -30603, 'true');


/* START the Admin role Group */
insert into YukonGroupRole values(-1000,-2,-100,-10000,'(none)');
insert into YukonGroupRole values(-1001,-2,-100,-10001,'(none)');
insert into YukonGroupRole values(-1002,-2,-100,-10002,'(none)');
insert into YukonGroupRole values(-1003,-2,-100,-10003,'(none)');
insert into YukonGroupRole values(-1004,-2,-100,-10004,'(none)');
insert into YukonGroupRole values(-1005,-2,-100,-10005,'(none)');
insert into YukonGroupRole values(-1007,-2,-100,-10007,'(none)');
insert into YukonGroupRole values(-1008,-2,-100,-10008,'(none)');
insert into YukonGroupRole values(-1009,-2,-100,-10009,'(none)');


insert into YukonGroupRole values(-1020,-2,-101,-10100,'(none)');
insert into YukonGroupRole values(-1021,-2,-101,-10101,'(none)');
insert into YukonGroupRole values(-1022,-2,-101,-10102,'(none)');
insert into YukonGroupRole values(-1023,-2,-101,-10103,'(none)');
insert into YukonGroupRole values(-1024,-2,-101,-10104,'(none)');
insert into YukonGroupRole values(-1025,-2,-101,-10105,'(none)');
insert into YukonGroupRole values(-1026,-2,-101,-10106,'(none)');
insert into YukonGroupRole values(-1027,-2,-101,-10107,'(none)');
insert into YukonGroupRole values(-1028,-2,-101,-10108,'(none)');
insert into YukonGroupRole values(-1029,-2,-101,-10109,'(none)');
insert into YukonGroupRole values(-1030,-2,-101,-10111,'(none)');

insert into YukonGroupRole values(-1050,-2,-102,-10200,'(none)');

insert into YukonGroupRole values(-1070,-2,-103,-10300,'(none)');
insert into YukonGroupRole values(-1071,-2,-103,-10301,'true');
insert into YukonGroupRole values(-1072,-2,-103,-10302,'true');
insert into YukonGroupRole values(-1073,-2,-103,-10303,'false');
insert into YukonGroupRole values(-1074,-2,-103,-10304,'false');

insert into YukonGroupRole values(-1080,-2,-107,-10700,'(none)');
insert into YukonGroupRole values(-1081,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1082,-2,-206,-20601,'(none)');
insert into YukonGroupRole values(-1083,-2,-206,-20602,'(none)');
insert into YukonGroupRole values(-1084,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1085,-2,-206,-20601,'true');
insert into YukonGroupRole values(-1086,-2,-206,-20602,'false');

insert into YukonGroupRole values (-1090,-2, -108, -10800, '/user/CILC/user_trending.jsp');
insert into YukonGroupRole values (-1091,-2, -108, -10802, '(none)');
insert into YukonGroupRole values (-1092,-2, -108, -10803, '(none)');
insert into YukonGroupRole values (-1093,-2, -108, -10804, '(none)');
insert into YukonGroupRole values (-1094,-2, -108, -10805, '(none)');
insert into YukonGroupRole values (-1095,-2, -108, -10806, '(none)');
insert into YukonGroupRole values (-1096,-2, -108, -10807, '(none)');
insert into YukonGroupRole values (-1097,-2, -108, -10808, '(none)');

insert into YukonGroupRole values (-1100,-2, -300, -30000, '(none)');
insert into YukonGroupRole values (-1101,-2, -300, -30001, 'true');

insert into YukonGroupRole values (-1110,-2, -301, -30100, '(none)');
insert into YukonGroupRole values (-1111,-2, -301, -30101, '(none)');

insert into YukonGroupRole values (-1120,-2, -302, -30200, '(none)');
insert into YukonGroupRole values (-1121,-2, -302, -30200, '(none)');

insert into YukonGroupRole values (-1130,-2, -304, -30400, '(none)');
insert into YukonGroupRole values (-1131,-2, -304, -30401, 'true');
insert into YukonGroupRole values (-1132,-2, -304, -30402, '(none)');
insert into YukonGroupRole values (-1133,-2, -304, -30403, '(none)');

insert into YukonGroupRole values (-1140,-2, -305, -30500, 'true');
insert into YukonGroupRole values (-1142,-2,-400,-40001,'(none)');
insert into YukonGroupRole values (-1143,-2,-400,-40002,'(none)');
insert into YukonGroupRole values (-1144,-2,-400,-40003,'(none)');
insert into YukonGroupRole values (-1145,-2,-400,-40004,'(none)');
insert into YukonGroupRole values (-1146,-2,-400,-40005,'(none)');
insert into YukonGroupRole values (-1147,-2,-400,-40006,'(none)');
insert into YukonGroupRole values (-1148,-2,-400,-40007,'(none)');
insert into YukonGroupRole values (-1149,-2,-400,-40008,'(none)');
insert into YukonGroupRole values (-1150,-2,-400,-40009,'(none)');
insert into YukonGroupRole values (-1151,-2,-400,-40010,'(none)');
insert into YukonGroupRole values (-1153,-2,-400,-40051,'(none)');
insert into YukonGroupRole values (-1154,-2,-400,-40052,'(none)');
insert into YukonGroupRole values (-1155,-2,-400,-40054,'(none)');
insert into YukonGroupRole values (-1156,-2,-400,-40055,'(none)');
insert into YukonGroupRole values (-1157,-2,-400,-40100,'(none)');
insert into YukonGroupRole values (-1159,-2,-400,-40110,'(none)');
insert into YukonGroupRole values (-1160,-2,-400,-40111,'(none)');
insert into YukonGroupRole values (-1161,-2,-400,-40112,'(none)');
insert into YukonGroupRole values (-1162,-2,-400,-40113,'(none)');
insert into YukonGroupRole values (-1163,-2,-400,-40114,'(none)');
insert into YukonGroupRole values (-1164,-2,-400,-40115,'(none)');
insert into YukonGroupRole values (-1165,-2,-400,-40116,'(none)');
insert into YukonGroupRole values (-1166,-2,-400,-40117,'(none)');
insert into YukonGroupRole values (-1167,-2,-400,-40130,'(none)');
insert into YukonGroupRole values (-1168,-2,-400,-40131,'(none)');
insert into YukonGroupRole values (-1169,-2,-400,-40132,'(none)');
insert into YukonGroupRole values (-1170,-2,-400,-40133,'(none)');
insert into YukonGroupRole values (-1171,-2,-400,-40134,'(none)');
insert into YukonGroupRole values (-1172,-2,-400,-40150,'(none)');
insert into YukonGroupRole values (-1173,-2,-400,-40151,'(none)');
insert into YukonGroupRole values (-1174,-2,-400,-40152,'(none)');
insert into YukonGroupRole values (-1175,-2,-400,-40153,'(none)');
insert into YukonGroupRole values (-1176,-2,-400,-40154,'(none)');
insert into YukonGroupRole values (-1177,-2,-400,-40155,'(none)');
insert into YukonGroupRole values (-1178,-2,-400,-40156,'(none)');
insert into YukonGroupRole values (-1179,-2,-400,-40157,'(none)');
insert into YukonGroupRole values (-1180,-2,-400,-40158,'(none)');
insert into YukonGroupRole values (-1181,-2,-400,-40170,'(none)');
insert into YukonGroupRole values (-1182,-2,-400,-40171,'(none)');
insert into YukonGroupRole values (-1183,-2,-400,-40172,'(none)');
insert into YukonGroupRole values (-1184,-2,-400,-40173,'(none)');
insert into YukonGroupRole values (-1185,-2,-400,-40180,'(none)');
insert into YukonGroupRole values (-1186,-2,-400,-40181,'(none)');
insert into YukonGroupRole values (-1188,-2,-201,-20101,'(none)');
insert into YukonGroupRole values (-1189,-2,-201,-20102,'(none)');
insert into YukonGroupRole values (-1190,-2,-201,-20103,'(none)');
insert into YukonGroupRole values (-1191,-2,-201,-20104,'(none)');
insert into YukonGroupRole values (-1192,-2,-201,-20105,'(none)');
insert into YukonGroupRole values (-1193,-2,-201,-20106,'(none)');
insert into YukonGroupRole values (-1194,-2,-201,-20107,'(none)');
insert into YukonGroupRole values (-1195,-2,-201,-20108,'(none)');
insert into YukonGroupRole values (-1196,-2,-201,-20109,'(none)');
insert into YukonGroupRole values (-1197,-2,-201,-20110,'(none)');
insert into YukonGroupRole values (-1198,-2,-201,-20111,'(none)');
insert into YukonGroupRole values (-1199,-2,-201,-20112,'(none)');
insert into YukonGroupRole values (-1200,-2,-201,-20113,'(none)');
insert into YukonGroupRole values (-1201,-2,-201,-20114,'(none)');
insert into YukonGroupRole values (-1202,-2,-201,-20115,'(none)');
insert into YukonGroupRole values (-1203,-2,-201,-20116,'(none)');
insert into YukonGroupRole values (-1204,-2,-201,-20117,'(none)');
insert into YukonGroupRole values (-1205,-2,-201,-20118,'true');

insert into YukonGroupRole values (-1251,-2,-201,-20151,'(none)');
insert into YukonGroupRole values (-1252,-2,-201,-20152,'(none)');
insert into YukonGroupRole values (-1253,-2,-201,-20153,'(none)');
insert into YukonGroupRole values (-1254,-2,-201,-20154,'(none)');
insert into YukonGroupRole values (-1255,-2,-201,-20155,'(none)');
insert into YukonGroupRole values (-1256,-2,-201,-20156,'(none)');
insert into YukonGroupRole values (-1257,-2,-201,-20157,'(none)');

insert into YukonGroupRole values (-1265,-2,-210,-21000,'(none)');
insert into YukonGroupRole values (-1266,-2,-210,-21001,'(none)');

insert into YukonGroupRole values (-1270,-2,-202,-20200,'(none)');
insert into YukonGroupRole values (-1275,-2,-203,-20300,'(none)');
insert into YukonGroupRole values (-1276,-2,-203,-20301,'(none)');

insert into YukonGroupRole values (-1280,-2,-204,-20400,'(none)');
insert into YukonGroupRole values (-1285,-2,-205,-20500,'(none)');

insert into YukonGroupRole values (-1290,-2,-207,-20700,'(none)');
insert into YukonGroupRole values (-1291,-2,-209,-20900,'(none)');
insert into YukonGroupRole values (-1292,-2,-209,-20901,'(none)');
insert into YukonGroupRole values (-1293,-2,-209,-20902,'(none)');
insert into YukonGroupRole values (-1294,-2,-209,-20903,'(none)');
insert into YukonGroupRole values (-1295,-2,-209,-20904,'(none)');

insert into YukonGroupRole values (-1300,-2,-201,-20800,'(none)');
insert into YukonGroupRole values (-1301,-2,-201,-20801,'(none)');

insert into YukonGroupRole values (-1310,-2,-201,-20810,'(none)');
insert into YukonGroupRole values (-1313,-2,-201,-20813,'(none)');
insert into YukonGroupRole values (-1314,-2,-201,-20814,'(none)');
insert into YukonGroupRole values (-1315,-2,-201,-20815,'(none)');
insert into YukonGroupRole values (-1316,-2,-201,-20816,'(none)');
insert into YukonGroupRole values (-1319,-2,-201,-20819,'(none)');
insert into YukonGroupRole values (-1320,-2,-201,-20820,'(none)');

insert into YukonGroupRole values (-1330,-2,-201,-20830,'(none)');
insert into YukonGroupRole values (-1331,-2,-201,-20831,'(none)');
insert into YukonGroupRole values (-1332,-2,-201,-20832,'(none)');
insert into YukonGroupRole values (-1333,-2,-201,-20833,'(none)');
insert into YukonGroupRole values (-1334,-2,-201,-20834,'(none)');

insert into YukonGroupRole values (-1350,-2,-201,-20850,'(none)');
insert into YukonGroupRole values (-1351,-2,-201,-20851,'(none)');
insert into YukonGroupRole values (-1352,-2,-201,-20852,'(none)');
insert into YukonGroupRole values (-1353,-2,-201,-20853,'(none)');
insert into YukonGroupRole values (-1354,-2,-201,-20854,'(none)');
insert into YukonGroupRole values (-1355,-2,-201,-20855,'(none)');
insert into YukonGroupRole values (-1356,-2,-201,-20856,'(none)');

insert into YukonGroupRole values (-1370,-2,-201,-20870,'(none)');

insert into yukongrouprole values (-2000,-303,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-2002,-303,-108,-10802,'(none)');
insert into yukongrouprole values (-2003,-303,-108,-10803,'(none)');
insert into yukongrouprole values (-2004,-303,-108,-10804,'(none)');
insert into yukongrouprole values (-2005,-303,-108,-10805,'(none)');
insert into yukongrouprole values (-2006,-303,-108,-10806,'(none)');
insert into yukongrouprole values (-2007,-303,-108,-10807,'(none)');
insert into yukongrouprole values (-2008,-303,-108,-10808,'(none)');

insert into yukongrouprole values (-2021,-303,-201,-20101,'(none)');
insert into yukongrouprole values (-2022,-303,-201,-20102,'true');
insert into yukongrouprole values (-2023,-303,-201,-20103,'(none)');
insert into yukongrouprole values (-2024,-303,-201,-20104,'(none)');
insert into yukongrouprole values (-2025,-303,-201,-20105,'(none)');
insert into yukongrouprole values (-2026,-303,-201,-20106,'(none)');
insert into yukongrouprole values (-2027,-303,-201,-20107,'(none)');
insert into yukongrouprole values (-2028,-303,-201,-20108,'(none)');
insert into yukongrouprole values (-2029,-303,-201,-20109,'(none)');
insert into yukongrouprole values (-2030,-303,-201,-20110,'(none)');
insert into yukongrouprole values (-2031,-303,-201,-20111,'(none)');
insert into yukongrouprole values (-2032,-303,-201,-20112,'(none)');
insert into yukongrouprole values (-2033,-303,-201,-20113,'(none)');
insert into yukongrouprole values (-2034,-303,-201,-20114,'true');
insert into yukongrouprole values (-2035,-303,-201,-20115,'false');
insert into yukongrouprole values (-2036,-303,-201,-20116,'(none)');
insert into yukongrouprole values (-2037,-303,-201,-20117,'(none)');
insert into yukongrouprole values (-2038,-303,-201,-20118,'(none)');

insert into yukongrouprole values (-2051,-303,-201,-20151,'(none)');
insert into yukongrouprole values (-2052,-303,-201,-20152,'(none)');
insert into yukongrouprole values (-2053,-303,-201,-20153,'(none)');
insert into yukongrouprole values (-2054,-303,-201,-20154,'(none)');
insert into yukongrouprole values (-2055,-303,-201,-20155,'true');
insert into yukongrouprole values (-2056,-303,-201,-20156,'(none)');
insert into yukongrouprole values (-2057,-303,-201,-20157,'(none)');
insert into yukongrouprole values (-2058,-303,-201,-20158,'true');

insert into yukongrouprole values (-2070,-303,-210,-21000,'(none)');
insert into yukongrouprole values (-2071,-303,-210,-21001,'(none)');

insert into yukongrouprole values (-2080,-303,-209,-20900,'(none)');
insert into yukongrouprole values (-2081,-303,-209,-20901,'(none)');
insert into yukongrouprole values (-2082,-303,-209,-20902,'(none)');
insert into yukongrouprole values (-2083,-303,-209,-20903,'(none)');
insert into yukongrouprole values (-2084,-303,-209,-20904,'(none)');
insert into yukongrouprole values (-2085,-303,-209,-20905,'(none)');
insert into yukongrouprole values (-2086,-303,-209,-20906,'(none)');

insert into yukongrouprole values (-2100,-303,-201,-20800,'(none)');
insert into yukongrouprole values (-2101,-303,-201,-20801,'(none)');
insert into yukongrouprole values (-2110,-303,-201,-20810,'(none)');
insert into yukongrouprole values (-2113,-303,-201,-20813,'(none)');
insert into yukongrouprole values (-2114,-303,-201,-20814,'(none)');
insert into yukongrouprole values (-2115,-303,-201,-20815,'(none)');
insert into yukongrouprole values (-2116,-303,-201,-20816,'(none)');
insert into yukongrouprole values (-2117,-303,-201,-20817,'(none)');
insert into yukongrouprole values (-2119,-303,-201,-20819,'(none)');
insert into yukongrouprole values (-2120,-303,-201,-20820,'(none)');

insert into yukongrouprole values (-2130,-303,-201,-20830,'(none)');
insert into yukongrouprole values (-2131,-303,-201,-20831,'(none)');
insert into yukongrouprole values (-2132,-303,-201,-20832,'(none)');
insert into yukongrouprole values (-2133,-303,-201,-20833,'(none)');
insert into yukongrouprole values (-2134,-303,-201,-20834,'(none)');
insert into yukongrouprole values (-2135,-303,-201,-20835,'(none)');
insert into yukongrouprole values (-2136,-303,-201,-20836,'(none)');
insert into yukongrouprole values (-2137,-303,-201,-20837,'(none)');
insert into yukongrouprole values (-2138,-303,-201,-20838,'(none)');
insert into yukongrouprole values (-2139,-303,-201,-20839,'(none)');
insert into yukongrouprole values (-2140,-303,-201,-20840,'(none)');
insert into yukongrouprole values (-2141,-303,-201,-20841,'(none)');
insert into yukongrouprole values (-2142,-303,-201,-20842,'(none)');
insert into yukongrouprole values (-2143,-303,-201,-20843,'(none)');
insert into yukongrouprole values (-2144,-303,-201,-20844,'(none)');
insert into yukongrouprole values (-2145,-303,-201,-20845,'(none)');

insert into yukongrouprole values (-2150,-303,-201,-20850,'(none)');
insert into yukongrouprole values (-2151,-303,-201,-20851,'(none)');
insert into yukongrouprole values (-2152,-303,-201,-20852,'(none)');
insert into yukongrouprole values (-2153,-303,-201,-20853,'(none)');
insert into yukongrouprole values (-2154,-303,-201,-20854,'(none)');
insert into yukongrouprole values (-2155,-303,-201,-20855,'(none)');
insert into yukongrouprole values (-2156,-303,-201,-20856,'(none)');
insert into yukongrouprole values (-2157,-303,-201,-20857,'(none)');
insert into yukongrouprole values (-2158,-303,-201,-20858,'(none)');
insert into yukongrouprole values (-2159,-303,-201,-20859,'(none)');
insert into yukongrouprole values (-2160,-303,-201,-20860,'(none)');
insert into yukongrouprole values (-2161,-303,-201,-20861,'(none)');
insert into yukongrouprole values (-2162,-303,-201,-20862,'(none)');
insert into yukongrouprole values (-2163,-303,-201,-20863,'(none)');
insert into yukongrouprole values (-2164,-303,-201,-20864,'(none)');

insert into yukongrouprole values (-2170,-303,-201,-20870,'(none)');
insert into yukongrouprole values (-2180,-303,-201,-20880,'(none)');
insert into yukongrouprole values (-2181,-303,-201,-20881,'(none)');
insert into yukongrouprole values (-2182,-303,-201,-20882,'(none)');
insert into yukongrouprole values (-2183,-303,-201,-20883,'(none)');
insert into yukongrouprole values (-2184,-303,-201,-20884,'(none)');
insert into yukongrouprole values (-2185,-303,-201,-20885,'(none)');
insert into yukongrouprole values (-2186,-303,-201,-20886,'(none)');
insert into yukongrouprole values (-2187,-303,-201,-20887,'(none)');
insert into yukongrouprole values (-2188,-303,-201,-20888,'(none)');
insert into yukongrouprole values (-2189,-303,-201,-20889,'(none)');

insert into yukongrouprole values (-2200,-304,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (-2202,-304,-108,-10802,'(none)');
insert into yukongrouprole values (-2203,-304,-108,-10803,'(none)');
insert into yukongrouprole values (-2204,-304,-108,-10804,'(none)');
insert into yukongrouprole values (-2205,-304,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-2206,-304,-108,-10806,'(none)');
insert into yukongrouprole values (-2207,-304,-108,-10807,'(none)');
insert into yukongrouprole values (-2208,-304,-108,-10808,'(none)');

insert into yukongrouprole values (-2221,-304,-400,-40001,'(none)');
insert into yukongrouprole values (-2222,-304,-400,-40002,'(none)');
insert into yukongrouprole values (-2223,-304,-400,-40003,'(none)');
insert into yukongrouprole values (-2224,-304,-400,-40004,'(none)');
insert into yukongrouprole values (-2225,-304,-400,-40005,'(none)');
insert into yukongrouprole values (-2226,-304,-400,-40006,'(none)');
insert into yukongrouprole values (-2227,-304,-400,-40007,'(none)');
insert into yukongrouprole values (-2228,-304,-400,-40008,'(none)');
insert into yukongrouprole values (-2229,-304,-400,-40009,'(none)');
insert into yukongrouprole values (-2230,-304,-400,-40010,'(none)');

insert into yukongrouprole values (-2251,-304,-400,-40051,'(none)');
insert into yukongrouprole values (-2252,-304,-400,-40052,'(none)');
insert into yukongrouprole values (-2254,-304,-400,-40054,'(none)');
insert into yukongrouprole values (-2255,-304,-400,-40055,'(none)');

insert into yukongrouprole values (-2300,-304,-400,-40100,'(none)');
insert into yukongrouprole values (-2302,-304,-400,-40102,'(none)');
insert into yukongrouprole values (-2310,-304,-400,-40110,'(none)');
insert into yukongrouprole values (-2311,-304,-400,-40111,'(none)');
insert into yukongrouprole values (-2312,-304,-400,-40112,'(none)');
insert into yukongrouprole values (-2313,-304,-400,-40113,'(none)');
insert into yukongrouprole values (-2314,-304,-400,-40114,'(none)');
insert into yukongrouprole values (-2315,-304,-400,-40115,'(none)');
insert into yukongrouprole values (-2316,-304,-400,-40116,'(none)');
insert into yukongrouprole values (-2317,-304,-400,-40117,'(none)');
insert into yukongrouprole values (-2330,-304,-400,-40130,'(none)');
insert into yukongrouprole values (-2331,-304,-400,-40131,'(none)');
insert into yukongrouprole values (-2332,-304,-400,-40132,'(none)');
insert into yukongrouprole values (-2333,-304,-400,-40133,'(none)');
insert into yukongrouprole values (-2334,-304,-400,-40134,'(none)');
insert into yukongrouprole values (-2335,-304,-400,-40135,'(none)');
insert into yukongrouprole values (-2336,-304,-400,-40136,'(none)');
insert into yukongrouprole values (-2337,-304,-400,-40137,'(none)');
insert into yukongrouprole values (-2338,-304,-400,-40138,'(none)');
insert into yukongrouprole values (-2339,-304,-400,-40139,'(none)');

insert into yukongrouprole values (-2350,-304,-400,-40150,'(none)');
insert into yukongrouprole values (-2351,-304,-400,-40151,'(none)');
insert into yukongrouprole values (-2352,-304,-400,-40152,'(none)');
insert into yukongrouprole values (-2353,-304,-400,-40153,'(none)');
insert into yukongrouprole values (-2354,-304,-400,-40154,'(none)');
insert into yukongrouprole values (-2355,-304,-400,-40155,'(none)');
insert into yukongrouprole values (-2356,-304,-400,-40156,'(none)');
insert into yukongrouprole values (-2357,-304,-400,-40157,'(none)');
insert into yukongrouprole values (-2358,-304,-400,-40158,'(none)');
insert into yukongrouprole values (-2359,-304,-400,-40159,'(none)');

insert into yukongrouprole values (-2370,-304,-400,-40170,'(none)');
insert into yukongrouprole values (-2371,-304,-400,-40171,'(none)');
insert into yukongrouprole values (-2372,-304,-400,-40172,'(none)');
insert into yukongrouprole values (-2373,-304,-400,-40173,'(none)');
insert into yukongrouprole values (-2380,-304,-400,-40180,'(none)');
insert into yukongrouprole values (-2381,-304,-400,-40181,'(none)');
insert into yukongrouprole values (-2390,-304,-400,-40190,'(none)');
insert into yukongrouprole values (-2391,-304,-400,-40191,'(none)');
insert into yukongrouprole values (-2392,-304,-400,-40192,'(none)');
insert into yukongrouprole values (-2393,-304,-400,-40193,'(none)');
insert into yukongrouprole values (-2394,-304,-400,-40194,'(none)');
insert into yukongrouprole values (-2395,-304,-400,-40195,'(none)');
insert into yukongrouprole values (-2396,-304,-400,-40196,'(none)');

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


insert into YukonImage values( 0, '(none)', '(none)', null );

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


insert into YukonListEntry values( 0, 0, 0, '(none)', 0 );
insert into YukonListEntry values( 1, 1, 0, 'Email', 1 );
insert into YukonListEntry values( 2, 1, 0, 'Phone Number', 2 );
insert into YukonListEntry values( 3, 1, 0, 'Pager Number', 2 );
insert into YukonListEntry values( 4, 1, 0, 'Fax Number', 2 );
insert into YukonListEntry values( 5, 1, 0, 'Home Phone', 2 );
insert into YukonListEntry values( 6, 1, 0, 'Work Phone', 2 );


insert into YukonListEntry values (1001,1001,0,'Program',1001);
insert into YukonListEntry values (1002,1001,0,'Hardware',1002);
insert into YukonListEntry values (1003,1001,0,'ThermostatManual',1003);
insert into YukonListEntry values (1011,1002,0,'Signup',1101);
insert into YukonListEntry values (1012,1002,0,'Activation Pending',1102);
insert into YukonListEntry values (1013,1002,0,'Activation Completed',1103);
insert into YukonListEntry values (1014,1002,0,'Termination',1104);
insert into YukonListEntry values (1015,1002,0,'Temp Opt Out',1105);
insert into YukonListEntry values (1016,1002,0,'Future Activation',1106);
insert into YukonListEntry values (1017,1002,0,'Install',1107);
insert into YukonListEntry values (1018,1002,0,'Configure',1108);
insert into YukonListEntry values (1019,1002,0,'Programming',1109);
insert into YukonListEntry values (1020,1002,0,'Manual Option',1110);
insert into YukonListEntry values (1021,1002,0,'Uninstall',1111);
insert into YukonListEntry values (1031,1003,0,'OneWayReceiver',1201);
insert into YukonListEntry values (1032,1003,0,'TwoWayReceiver',1202);
insert into YukonListEntry values (1033,1003,0,'MCT',1203);
insert into YukonListEntry values (1041,1004,0,' ',0);
insert into YukonListEntry values (1042,1004,0,'120/120',0);
insert into YukonListEntry values (1051,1005,0,'LCR-5000(Xcom)',1302);
insert into YukonListEntry values (1052,1005,0,'LCR-4000',1305);
insert into YukonListEntry values (1053,1005,0,'LCR-3000',1306);
insert into YukonListEntry values (1054,1005,0,'LCR-2000',1307);
insert into YukonListEntry values (1056,1005,-1,'ExpressStat',1301);
insert into YukonListEntry values (1057,1005,-1,'EnergyPro',3100);
insert into YukonListEntry values (1058,1005,-1,'MCT',1303);
insert into YukonListEntry values (1059,1005,-1,'Commercial ExpressStat',1304);
insert into YukonListEntry values (1060,1005,-1,'SA-205',1309);
insert into YukonListEntry values (1061,1005,-1,'SA-305',1310);
insert into YukonListEntry values (1062,1005,0,'LCR-5000(Vcom)',1311);
insert into YukonListEntry values (1071,1006,0,'Available',1701);
insert into YukonListEntry values (1072,1006,0,'Temp Unavail',1702);
insert into YukonListEntry values (1073,1006,0,'Unavailable',1703);
insert into YukonListEntry values (1081,1007,0,'(Default)',1400);
insert into YukonListEntry values (1082,1007,0,'Air Conditioner',1401);
insert into YukonListEntry values (1083,1007,0,'Water Heater',1402);
insert into YukonListEntry values (1084,1007,0,'Storage Heat',1403);
insert into YukonListEntry values (1085,1007,0,'Heat Pump',1404);
insert into YukonListEntry values (1086,1007,0,'Dual Fuel',1405);
insert into YukonListEntry values (1087,1007,0,'Generator',1406);
insert into YukonListEntry values (1088,1007,0,'Grain Dryer',1407);
insert into YukonListEntry values (1089,1007,0,'Irrigation',1408);
insert into YukonListEntry values (1101,1008,0,'General',0);
insert into YukonListEntry values (1102,1008,0,'Credit',0);
insert into YukonListEntry values (1111,1009,0,'Service Call',0);
insert into YukonListEntry values (1112,1009,0,'Install',0);
insert into YukonListEntry values (1121,1010,0,'Pending',1501);
insert into YukonListEntry values (1122,1010,0,'Scheduled',1502);
insert into YukonListEntry values (1123,1010,0,'Completed',1503);
insert into YukonListEntry values (1124,1010,0,'Cancelled',1504);
insert into YukonListEntry values (1131,1011,0,'Acct #',1601);
insert into YukonListEntry values (1132,1011,0,'Phone #',1602);
insert into YukonListEntry values (1133,1011,0,'Last name',1603);
insert into YukonListEntry values (1134,1011,0,'Serial #',1604);
insert into YukonListEntry values (1135,1011,0,'Map #',1605);
insert into YukonListEntry values (1141,1012,0,'(Unknown)',1801);
insert into YukonListEntry values (1142,1012,0,'Century',0);
insert into YukonListEntry values (1143,1012,0,'Universal',0);
insert into YukonListEntry values (1151,1013,0,'(Unknown)',1901);
insert into YukonListEntry values (1152,1013,0,'Basement',0);
insert into YukonListEntry values (1153,1013,0,'North Side',0);
insert into YukonListEntry values (1161,1014,0,'Likely',0);
insert into YukonListEntry values (1162,1014,0,'Unlikely',0);
insert into YukonListEntry values (1171,1015,0,'Weekday',2101);
insert into YukonListEntry values (1172,1015,0,'Weekend',2102);
insert into YukonListEntry values (1173,1015,0,'Saturday',2103);
insert into YukonListEntry values (1174,1015,0,'Sunday',2104);
insert into YukonListEntry values (1175,1015,0,'Monday',2105);
insert into YukonListEntry values (1176,1015,0,'Tuesday',2106);
insert into YukonListEntry values (1177,1015,0,'Wednesday',2107);
insert into YukonListEntry values (1178,1015,0,'Thursday',2108);
insert into YukonListEntry values (1179,1015,0,'Friday',2109);
insert into YukonListEntry values (1191,1016,0,'Signup',2201);
insert into YukonListEntry values (1192,1016,0,'Exit',2202);
insert into YukonListEntry values (1201,1017,0,'Selection',2301);
insert into YukonListEntry values (1202,1017,0,'Free Form',2302);
insert into YukonListEntry values (1211,1018,0,'(Default)',2401);
insert into YukonListEntry values (1212,1018,0,'Cool',2402);
insert into YukonListEntry values (1213,1018,0,'Heat',2403);
insert into YukonListEntry values (1214,1018,0,'Off',2404);
insert into YukonListEntry values (1215,1018,0,'Auto',2405);
insert into YukonListEntry values (1216,1018,0,'Emergency Heat',2406);
insert into YukonListEntry values (1221,1019,0,'(Default)',2501);
insert into YukonListEntry values (1222,1019,0,'Auto',2502);
insert into YukonListEntry values (1223,1019,0,'On',2503);
insert into YukonListEntry values (1231,1020,1,'PROGRAMS',0);
insert into YukonListEntry values (1232,1020,2,'THERMOSTAT CONTROL',0);
insert into YukonListEntry values (1233,1020,3,'SAVINGS',0);
insert into YukonListEntry values (1241,1049,1,'Tomorrow',2601);
insert into YukonListEntry values (1242,1049,2,'Today',2602);
insert into YukonListEntry values (1243,1049,99,'Repeat Last',2699);

insert into YukonListEntry values (1251,1050,0,'Last Updated Time',3201);
insert into YukonListEntry values (1252,1050,0,'Setpoint',3202);
insert into YukonListEntry values (1253,1050,0,'Fan',3203);
insert into YukonListEntry values (1254,1050,0,'System',3204);
insert into YukonListEntry values (1255,1050,0,'Room,Unit',3205);
insert into YukonListEntry values (1256,1050,0,'Outdoor',3234);
insert into YukonListEntry values (1257,1050,0,'Filter Remaining,Filter Restart',3236);
insert into YukonListEntry values (1258,1050,0,'Lower CoolSetpoint Limit,Upper HeatSetpoint Limit',3237);
insert into YukonListEntry values (1259,1050,0,'Information String',3299);
insert into YukonListEntry values (1260,1050,0,'Cool Runtime,Heat Runtime',3238);
insert into YukonListEntry values (1261,1050,0,'Battery',3239);

insert into YukonListEntry values (1301,1051,0,'Serial #',2701);
insert into YukonListEntry values (1302,1051,0,'Acct #',2702);
insert into YukonListEntry values (1303,1051,0,'Phone #',2703);
insert into YukonListEntry values (1304,1051,0,'Last name',2704);
insert into YukonListEntry values (1305,1051,0,'Order #',2705);
insert into YukonListEntry values (1311,1052,0,'Serial #',2801);
insert into YukonListEntry values (1312,1052,0,'Install date',2802);
insert into YukonListEntry values (1321,1053,0,'Device type',2901);
insert into YukonListEntry values (1322,1053,0,'Service company',2902);
insert into YukonListEntry values (1323,1053,0,'Location',2903);
insert into YukonListEntry values (1324,1053,0,'Configuration',2904);
insert into YukonListEntry values (1325,1053,0,'Device status',2905);
insert into YukonListEntry values (1331,1054,0,'Order #',3301);
insert into YukonListEntry values (1332,1054,0,'Acct #',3302);
insert into YukonListEntry values (1333,1054,0,'Phone #',3303);
insert into YukonListEntry values (1334,1054,0,'Last Name',3304);
insert into YukonListEntry values (1335,1054,0,'Serial #',3305);
insert into YukonListEntry values (1341,1055,0,'Order #',3401);
insert into YukonListEntry values (1342,1055,0,'Date/Time',3402);
insert into YukonListEntry values (1352,1056,0,'Service Type',3502);
insert into YukonListEntry values (1353,1056,0,'Service Company',3503);

insert into YukonListEntry values (1400,1032,0,' ',0);
insert into YukonListEntry values (1401,1032,0,'2',0);
insert into YukonListEntry values (1402,1032,0,'2.5',0);
insert into YukonListEntry values (1403,1032,0,'3',0);
insert into YukonListEntry values (1404,1032,0,'3.5',0);
insert into YukonListEntry values (1405,1032,0,'4',0);
insert into YukonListEntry values (1406,1032,0,'4.5',0);
insert into YukonListEntry values (1407,1032,0,'5',0);
insert into YukonListEntry values (1410,1033,0,' ',0);
insert into YukonListEntry values (1411,1033,0,'Central Air',0);
insert into YukonListEntry values (1420,1034,0,' ',0);
insert into YukonListEntry values (1421,1034,0,'30',0);
insert into YukonListEntry values (1422,1034,0,'40',0);
insert into YukonListEntry values (1423,1034,0,'52',0);
insert into YukonListEntry values (1424,1034,0,'80',0);
insert into YukonListEntry values (1425,1034,0,'120',0);
insert into YukonListEntry values (1430,1035,0,' ',0);
insert into YukonListEntry values (1431,1035,0,'Propane',0);
insert into YukonListEntry values (1432,1035,0,'Electric',0);
insert into YukonListEntry values (1433,1035,0,'Natural Gas',0);
insert into YukonListEntry values (1434,1035,0,'Oil',0);
insert into YukonListEntry values (1440,1036,0,' ',0);
insert into YukonListEntry values (1441,1036,0,'Automatic',0);
insert into YukonListEntry values (1442,1036,0,'Manual',0);
insert into YukonListEntry values (1450,1037,0,' ',0);
insert into YukonListEntry values (1451,1037,0,'Propane',0);
insert into YukonListEntry values (1452,1037,0,'Electric',0);
insert into YukonListEntry values (1453,1037,0,'Natural Gas',0);
insert into YukonListEntry values (1454,1037,0,'Oil',0);
insert into YukonListEntry values (1460,1038,0,' ',0);
insert into YukonListEntry values (1461,1038,0,'Batch Dry',0);
insert into YukonListEntry values (1462,1038,0,'Aerate',0);
insert into YukonListEntry values (1463,1038,0,'Low Temp Air',0);
insert into YukonListEntry values (1470,1039,0,' ',0);
insert into YukonListEntry values (1471,1039,0,'0-2500 Bushels',0);
insert into YukonListEntry values (1472,1039,0,'2500-5000 Bushels',0);
insert into YukonListEntry values (1473,1039,0,'5000-7500 Bushels',0);
insert into YukonListEntry values (1474,1039,0,'7500-10000 Bushels',0);
insert into YukonListEntry values (1475,1039,0,'10000-12500 Bushels',0);
insert into YukonListEntry values (1476,1039,0,'12500-15000 Bushels',0);
insert into YukonListEntry values (1477,1039,0,'15000-17500 Bushels',0);
insert into YukonListEntry values (1478,1039,0,'17500-20000 Bushels',0);
insert into YukonListEntry values (1479,1039,0,'20000-25000 Bushels',0);
insert into YukonListEntry values (1480,1039,0,'25000-30000 Bushels',0);
insert into YukonListEntry values (1481,1039,0,'30000+ Bushels',0);
insert into YukonListEntry values (1490,1040,0,' ',0);
insert into YukonListEntry values (1491,1040,0,'Electric',0);
insert into YukonListEntry values (1500,1041,0,' ',0);
insert into YukonListEntry values (1501,1041,0,'5',0);
insert into YukonListEntry values (1502,1041,0,'10',0);
insert into YukonListEntry values (1503,1041,0,'15',0);
insert into YukonListEntry values (1504,1041,0,'20',0);
insert into YukonListEntry values (1505,1041,0,'25',0);
insert into YukonListEntry values (1506,1041,0,'30',0);
insert into YukonListEntry values (1507,1041,0,'35',0);
insert into YukonListEntry values (1508,1041,0,'40',0);
insert into YukonListEntry values (1509,1041,0,'45',0);
insert into YukonListEntry values (1510,1041,0,'50',0);
insert into YukonListEntry values (1511,1041,0,'60',0);
insert into YukonListEntry values (1512,1041,0,'70',0);
insert into YukonListEntry values (1513,1041,0,'80',0);
insert into YukonListEntry values (1514,1041,0,'100',0);
insert into YukonListEntry values (1515,1041,0,'150',0);
insert into YukonListEntry values (1520,1042,0,' ',0);
insert into YukonListEntry values (1521,1042,0,'Propane',0);
insert into YukonListEntry values (1522,1042,0,'Electric',0);
insert into YukonListEntry values (1523,1042,0,'Natural Gas',0);
insert into YukonListEntry values (1530,1043,0,' ',0);
insert into YukonListEntry values (1531,1043,0,'Ceramic Brick',0);
insert into YukonListEntry values (1532,1043,0,'Concrete Slab',0);
insert into YukonListEntry values (1533,1043,0,'Water',0);
insert into YukonListEntry values (1534,1043,0,'Phase-Change Comp',0);
insert into YukonListEntry values (1535,1043,0,'Pond',0);
insert into YukonListEntry values (1536,1043,0,'Rock Box',0);
insert into YukonListEntry values (1540,1044,0,' ',0);
insert into YukonListEntry values (1541,1044,0,'Air Source',0);
insert into YukonListEntry values (1542,1044,0,'Water Heater Closed',0);
insert into YukonListEntry values (1543,1044,0,'Water Heater Open',0);
insert into YukonListEntry values (1544,1044,0,'Water Heater Direct',0);
insert into YukonListEntry values (1545,1044,0,'Dual-Fuel (Gas)',0);
insert into YukonListEntry values (1550,1045,0,' ',0);
insert into YukonListEntry values (1551,1045,0,'Propane',0);
insert into YukonListEntry values (1552,1045,0,'Electric',0);
insert into YukonListEntry values (1553,1045,0,'Natural Gas',0);
insert into YukonListEntry values (1554,1045,0,'Oil',0);
insert into YukonListEntry values (1560,1046,0,' ',0);
insert into YukonListEntry values (1561,1046,0,'Pivot',0);
insert into YukonListEntry values (1562,1046,0,'Gated Pipe',0);
insert into YukonListEntry values (1563,1046,0,'Pivot/Power Only',0);
insert into YukonListEntry values (1564,1046,0,'Reuse Pit Only',0);
insert into YukonListEntry values (1565,1046,0,'Ditch W/Syphon Tube',0);
insert into YukonListEntry values (1566,1046,0,'Flood',0);
insert into YukonListEntry values (1570,1047,0,' ',0);
insert into YukonListEntry values (1571,1047,0,'Heavy Loam',0);
insert into YukonListEntry values (1572,1047,0,'Loam',0);
insert into YukonListEntry values (1573,1047,0,'Medium',0);
insert into YukonListEntry values (1574,1047,0,'Sandy',0);
insert into YukonListEntry values (1580,1059,0,' ',0);
insert into YukonListEntry values (1581,1059,0,'5',0);
insert into YukonListEntry values (1582,1059,0,'10',0);
insert into YukonListEntry values (1583,1059,0,'15',0);
insert into YukonListEntry values (1584,1059,0,'20',0);
insert into YukonListEntry values (1585,1059,0,'25',0);
insert into YukonListEntry values (1586,1059,0,'30',0);
insert into YukonListEntry values (1587,1059,0,'35',0);
insert into YukonListEntry values (1588,1059,0,'40',0);
insert into YukonListEntry values (1589,1059,0,'45',0);
insert into YukonListEntry values (1590,1059,0,'50',0);
insert into YukonListEntry values (1591,1059,0,'60',0);
insert into YukonListEntry values (1592,1059,0,'70',0);
insert into YukonListEntry values (1593,1059,0,'80',0);
insert into YukonListEntry values (1594,1059,0,'100',0);
insert into YukonListEntry values (1595,1059,0,'150',0);
insert into YukonListEntry values (1596,1059,0,'200',0);
insert into YukonListEntry values (1597,1059,0,'250',0);
insert into YukonListEntry values (1598,1059,0,'300',0);
insert into YukonListEntry values (1600,1060,0,' ',0);
insert into YukonListEntry values (1601,1060,0,'Propane',0);
insert into YukonListEntry values (1602,1060,0,'Electric',0);
insert into YukonListEntry values (1603,1060,0,'Natural Gas',0);
insert into YukonListEntry values (1604,1060,0,'Oil',0);
insert into YukonListEntry values (1610,1061,0,' ',0);
insert into YukonListEntry values (1611,1061,0,'At Road',0);
insert into YukonListEntry values (1612,1061,0,'At Pump',0);
insert into YukonListEntry values (1613,1061,0,'At Pivot',0);
insert into YukonListEntry values (1620,1062,0,' ',0);
insert into YukonListEntry values (1621,1062,0,'120 (PT)',0);
insert into YukonListEntry values (1622,1062,0,'240',0);
insert into YukonListEntry values (1623,1062,0,'277/480',0);
insert into YukonListEntry values (1624,1062,0,'480',0);
insert into YukonListEntry values (1630,1063,0,' ',0);
insert into YukonListEntry values (1631,1063,0,'Basement',0);
insert into YukonListEntry values (1632,1063,0,'Crawl Space',0);
insert into YukonListEntry values (1633,1063,0,'Main Floor Closet',0);
insert into YukonListEntry values (1634,1063,0,'Second Floor Closet',0);
insert into YukonListEntry values (1635,1063,0,'Under Counter',0);
insert into YukonListEntry values (1636,1063,0,'Attic',0);
insert into YukonListEntry values (1640,1064,0,' ',0);
insert into YukonListEntry values (1641,1064,0,'2',0);
insert into YukonListEntry values (1642,1064,0,'2.5',0);
insert into YukonListEntry values (1643,1064,0,'3',0);
insert into YukonListEntry values (1644,1064,0,'3.5',0);
insert into YukonListEntry values (1645,1064,0,'4',0);
insert into YukonListEntry values (1646,1064,0,'4.5',0);
insert into YukonListEntry values (1647,1064,0,'5',0);

insert into YukonListEntry values (1700,1021,0,' ',0);
insert into YukonListEntry values (1701,1021,0,'One Story Unfinished Basement',0);
insert into YukonListEntry values (1702,1021,0,'One Story Finished Basement',0);
insert into YukonListEntry values (1703,1021,0,'Two Story',0);
insert into YukonListEntry values (1704,1021,0,'Manufactured Home',0);
insert into YukonListEntry values (1705,1021,0,'Apartment',0);
insert into YukonListEntry values (1706,1021,0,'Duplex',0);
insert into YukonListEntry values (1707,1021,0,'Townhome',0);
insert into YukonListEntry values (1710,1022,0,' ',0);
insert into YukonListEntry values (1711,1022,0,'Frame',0);
insert into YukonListEntry values (1712,1022,0,'Brick',0);
insert into YukonListEntry values (1720,1023,0,' ',0);
insert into YukonListEntry values (1721,1023,0,'pre-1900',0);
insert into YukonListEntry values (1722,1023,0,'1910',0);
insert into YukonListEntry values (1723,1023,0,'1920',0);
insert into YukonListEntry values (1724,1023,0,'1930',0);
insert into YukonListEntry values (1725,1023,0,'1940',0);
insert into YukonListEntry values (1726,1023,0,'1950',0);
insert into YukonListEntry values (1727,1023,0,'1960',0);
insert into YukonListEntry values (1728,1023,0,'1970',0);
insert into YukonListEntry values (1729,1023,0,'1980',0);
insert into YukonListEntry values (1730,1023,0,'1990',0);
insert into YukonListEntry values (1731,1023,0,'2000',0);
insert into YukonListEntry values (1740,1024,0,' ',0);
insert into YukonListEntry values (1741,1024,0,'Less Than 1000',0);
insert into YukonListEntry values (1742,1024,0,'1000-1499',0);
insert into YukonListEntry values (1743,1024,0,'1500-1999',0);
insert into YukonListEntry values (1744,1024,0,'2000-2499',0);
insert into YukonListEntry values (1745,1024,0,'2500-2999',0);
insert into YukonListEntry values (1746,1024,0,'3000-3499',0);
insert into YukonListEntry values (1747,1024,0,'3500-3999',0);
insert into YukonListEntry values (1748,1024,0,'4000+',0);
insert into YukonListEntry values (1751,1025,0,'Unknown',0);
insert into YukonListEntry values (1752,1025,0,'Poor (0-3)"',0);
insert into YukonListEntry values (1753,1025,0,'Fair (3-5)"',0);
insert into YukonListEntry values (1754,1025,0,'Average (6-8)"',0);
insert into YukonListEntry values (1755,1025,0,'Good (9-11)"',0);
insert into YukonListEntry values (1756,1025,0,'Excellant (12+)"',0);
insert into YukonListEntry values (1760,1026,0,' ',0);
insert into YukonListEntry values (1761,1026,0,'Poor',0);
insert into YukonListEntry values (1762,1026,0,'Fair',0);
insert into YukonListEntry values (1763,1026,0,'Good',0);
insert into YukonListEntry values (1764,1026,0,'Excellent',0);
insert into YukonListEntry values (1770,1027,0,' ',0);
insert into YukonListEntry values (1771,1027,0,'None',0);
insert into YukonListEntry values (1772,1027,0,'Central Air',0);
insert into YukonListEntry values (1773,1027,0,'GSHP',0);
insert into YukonListEntry values (1774,1027,0,'ASHP',0);
insert into YukonListEntry values (1775,1027,0,'Window Unit',0);
insert into YukonListEntry values (1776,1027,0,'Gas ASHP',0);
insert into YukonListEntry values (1777,1027,0,'Attic Fan',0);
insert into YukonListEntry values (1780,1028,0,' ',0);
insert into YukonListEntry values (1781,1028,0,'Electric Forced Air',0);
insert into YukonListEntry values (1782,1028,0,'GSHP',0);
insert into YukonListEntry values (1783,1028,0,'ASHP',0);
insert into YukonListEntry values (1784,1028,0,'Electric Baseboard',0);
insert into YukonListEntry values (1785,1028,0,'Ceiling Fan',0);
insert into YukonListEntry values (1786,1028,0,'ETS',0);
insert into YukonListEntry values (1787,1028,0,'Gas Forced Air',0);
insert into YukonListEntry values (1788,1028,0,'Gas Wall Unit',0);
insert into YukonListEntry values (1790,1029,0,' ',0);
insert into YukonListEntry values (1791,1029,0,'1 - 2',0);
insert into YukonListEntry values (1792,1029,0,'3 - 4',0);
insert into YukonListEntry values (1793,1029,0,'5 - 6',0);
insert into YukonListEntry values (1794,1029,0,'7 - 8',0);
insert into YukonListEntry values (1795,1029,0,'9+',0);
insert into YukonListEntry values (1800,1030,0,' ',0);
insert into YukonListEntry values (1801,1030,0,'Own',0);
insert into YukonListEntry values (1802,1030,0,'Rent',0);
insert into YukonListEntry values (1810,1031,0,' ',0);
insert into YukonListEntry values (1811,1031,0,'Propane',0);
insert into YukonListEntry values (1812,1031,0,'Electric',0);
insert into YukonListEntry values (1813,1031,0,'Natural Gas',0);
insert into YukonListEntry values (1814,1031,0,'Oil',0);

insert into YukonListEntry values (2000,0,0,'Customer List Entry Base',0);

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


INSERT into YukonPAObject values (0, 'DEVICE', 'System', 'System Device', 'System', 'Reserved System Device', 'N', '-----');

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


/* Default role for all users - yukon category */
insert into YukonRole values(-1,'Yukon','Yukon','Default Yukon role. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-4,'Authentication','Yukon','Settings for using an authentication server to login instead of standard yukon login.');
insert into YukonRole values(-5,'Voice Server','Yukon','Inbound and outbound voice interface.');
insert into YukonRole values(-104,'Calc Historical','Yukon','Calc Historical. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-105,'Web Graph','Yukon','Web Graph. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-106,'Billing','Yukon','Billing. Edit this role from the Yukon SetUp page.');

/* Application specific roles */
insert into YukonRole values(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
insert into YukonRole values(-101,'Tabular Display Console','Application','Access to the Yukon Tabular Display Console application');
insert into YukonRole values(-102,'Trending','Application','Access to the Yukon Trending application');
insert into YukonRole values(-103,'Commander','Application','Access to the Yukon Commander application');

insert into YukonRole values(-107,'Esubstation Editor','Application','Access to the Esubstation Drawing Editor application');
insert into YukonRole values(-108,'Web Client','Application','Access to the Yukon web application');

/* Web client operator roles */
insert into YukonRole values(-200,'Administrator','Operator','Access to Yukon administration');
insert into YukonRole values(-201,'Consumer Info','Operator','Operator access to consumer account information');
insert into YukonRole values(-202,'Commercial Metering','Operator','Operator access to commerical metering');
insert into YukonRole values(-203,'Direct Loadcontrol','Operator','Operator  access to direct loadcontrol');
insert into YukonRole values(-204,'Direct Curtailment','Operator','Operator access to direct curtailment');
insert into YukonRole values(-205,'Energy Buyback','Operator','Operator access to energy buyback');

/* Operator roles */
insert into YukonRole values(-206,'Esubstation Drawings','Operator','Operator access to esubstation drawings');
insert into YukonRole values(-207,'Odds For Control','Operator','Operator access to odds for control');
insert into YukonRole values(-2,'Energy Company','Operator','Energy company role');

/* Inventory Role */
insert into YukonRole values (-209,'Inventory','Operator','Operator Access to hardware inventory');

/* operator work order management role */
insert into YukonRole values (-210,'Work Order','Operator','Operator Access to work order management');

/* CI customer roles */
insert into YukonRole values(-300,'Direct Loadcontrol','CICustomer','Customer access to commercial/industrial customer direct loadcontrol');
insert into YukonRole values(-301,'Curtailment','CICustomer','Customer access to commercial/industrial customer direct curtailment');
insert into YukonRole values(-302,'Energy Buyback','CICustomer','Customer access to commercial/industrial customer energy buyback');
insert into YukonRole values(-304,'Commercial Metering','CICustomer','Customer access to commercial metering');
insert into YukonRole values(-305,'Administrator','CICustomer','Administrator privilages.');
insert into YukonRole values(-306, 'User Control', 'CICustomer', 'Customer access to user control operations.');

/* Consumer roles */
insert into YukonRole values(-400,'Residential Customer','Consumer','Access to residential customer information');

/* Billing AMR role */
insert into YukonRole values (-500,'Billing','AMR','Access to billing file generation.');

/* Reporting Analysis role */
insert into YukonRole values (-600,'Reporting','Analysis','Access to reports generation.');
insert into YukonRole values (-601,'Trending','Analysis','Access to trending functionality.');

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


/* Yukon Role */
insert into YukonRoleProperty values(-1000,-1,'dispatch_machine','127.0.0.1','Name or IP address of the Yukon Dispatch Service');
insert into YukonRoleProperty values(-1001,-1,'dispatch_port','1510','TCP/IP port of the Yukon Dispatch Service');
insert into YukonRoleProperty values(-1002,-1,'porter_machine','127.0.0.1','Name or IP address of the Yukon Port Control Service');
insert into YukonRoleProperty values(-1003,-1,'porter_port','1540','TCP/IP port of the Yukon Port Control Service');
insert into YukonRoleProperty values(-1004,-1,'macs_machine','127.0.0.1','Name or IP address of the Yukon Metering and Control Scheduler Service');
insert into YukonRoleProperty values(-1005,-1,'macs_port','1900','TCP/IP port of the Yukon Metering and Control Scheduler Service');
insert into YukonRoleProperty values(-1006,-1,'cap_control_machine','127.0.0.1','Name or IP address of the Yukon Capacitor Control Service');
insert into YukonRoleProperty values(-1007,-1,'cap_control_port','1910','TCP/IP port of the Yukon Capacitor Control Service');
insert into YukonRoleProperty values(-1008,-1,'loadcontrol_machine','127.0.0.1','Name or IP Address of the Yukon Load Management Service');
insert into YukonRoleProperty values(-1009,-1,'loadcontrol_port','1920','TCP/IP port of the Yukon Load Management Service');
insert into YukonRoleProperty values(-1010,-1,'smtp_host','127.0.0.1','Name or IP address of the mail server');
insert into YukonRoleProperty values(-1011,-1,'mail_from_address','yukon@cannontech.com','Name of the FROM email address the mail server will use');
insert into YukonRoleProperty values(-1012,-1,'print_insert_sql','(none)','File name of where to print all SQL insert statements');
insert into YukonRoleProperty values(-1013,-1,'stars_soap_server','(none)','Where the soap server is running, the default value is the local host');
insert into YukonRoleProperty values(-1014,-1,'web_logo','CannonLogo.gif','The logo that is used for the yukon web applications');
insert into YukonRoleProperty values(-1015,-1,'voice_host','127.0.0.1','Name or IP address of the voice server');
insert into YukonRoleProperty values(-1016,-1,'notification_host','127.0.0.1','Name or IP address of the Yukon Notification service');
insert into YukonRoleProperty values(-1017,-1,'notification_port','1515','TCP/IP port of the Yukon Notification service');

insert into YukonRoleProperty values(-1300,-4,'server_address','127.0.0.1','Authentication server machine address');
insert into YukonRoleProperty values(-1301,-4,'auth_port','1812','Authentication port.');
insert into YukonRoleProperty values(-1302,-4,'acct_port','1813','Accounting port.');
insert into YukonRoleProperty values(-1303,-4,'secret_key','cti','Client machine secret key value, defined by the server.');
insert into YukonRoleProperty values(-1304,-4,'auth_method','(none)','Authentication method. Possible values are (none) | PAP, [chap, others to follow soon]');
insert into YukonRoleProperty values(-1305,-4,'authentication_mode','Yukon','Authentication mode to use.  Valid values are:   Yukon | Radius');

insert into YukonRoleProperty values(-1400,-5,'voice_app','login','The voice server application that Yukon should use');
insert into YukonRoleProperty values(-1401,-5,'call_timeout','30','The time-out in seconds given to each outbound call');
insert into YukonRoleProperty values(-1402,-5,'call_response_timeout','240','The time-out in seconds given to each outbound call response');


/* Database Editor Role */
insert into YukonRoleProperty values(-10000,-100,'point_id_edit','false','Controls whether point ids can be edited');
insert into YukonRoleProperty values(-10001,-100,'dbeditor_core','true','Controls whether the Core menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10003,-100,'dbeditor_cap_control','true','Controls whether the Cap Control menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10005,-100,'utility_id_range','1-254','<description>');
insert into YukonRoleProperty values(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');
insert into YukonRoleProperty values(-10008,-100,'permit_login_edit','true','Closes off all access to logins and login groups for non-administrators in the dbeditor');
insert into YukonRoleProperty values(-10009,-100,'allow_user_roles','false','Allows the editor panel individual user roles to be shown');


/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','override@cannontech.com','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1105,-2,'customer_group_ids','-300','Group IDs of all the residential customer logins');
insert into YukonRoleProperty values(-1106,-2,'operator_group_ids','-301','Group IDs of all the web client operator logins');
insert into YukonRoleProperty values(-1107,-2,'track_hardware_addressing','false','Controls whether to track the hardware addressing information.');
insert into YukonRoleProperty values(-1108,-2,'single_energy_company','true','Indicates whether this is a single energy company system.');

/* TDC Role */
insert into YukonRoleProperty values(-10100,-101,'loadcontrol_edit','00000000','(No settings yet)');
insert into YukonRoleProperty values(-10101,-101,'macs_edit','00000CCC','The following settings are valid: CREATE_SCHEDULE(0x0000000C), ENABLE_SCHEDULE(0x000000C0), ABLE_TO_START_SCHEDULE(0x00000C00)');
insert into YukonRoleProperty values(-10102,-101,'tdc_express','ludicrous_speed','<description>');
insert into YukonRoleProperty values(-10103,-101,'tdc_max_rows','500','The number of rows shown before creating a new page of data');
insert into YukonRoleProperty values(-10104,-101,'tdc_rights','00000000','The following settings are valid: HIDE_MACS(0x00001000), HIDE_CAPCONTROL(0x00002000), HIDE_LOADCONTROL(0x00004000), HIDE_ALL_DISPLAYS(0x0000F000), CONTROL_YUKON_SERVICES(0x00010000), HIDE_ALARM_COLORS(0x80000000)');
insert into YukonRoleProperty values(-10105,-101,'CAP_CONTROL_INTERFACE','amfm','<description>');
insert into YukonRoleProperty values(-10106,-101,'cbc_creation_name','CBC %PAOName%','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-10107,-101,'tdc_alarm_count','3','Total number alarms that are displayed in the quick access list');
insert into YukonRoleProperty values(-10108,-101,'decimal_places','2','How many decimal places to show for real values');
insert into YukonRoleProperty values(-10109,-101,'pfactor_decimal_places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-10111,-101,'lc_reduction_col','true','Tells TDC to show the LoadControl reduction column or not');


/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');

/* Commander Role Properties */ 
insert into YukonRoleProperty values(-10300,-103,'msg_priority','14','Tells commander what the outbound priority of messages are (low)1 - 14(high)');
insert into YukonRoleProperty values(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
insert into YukonRoleProperty values(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
insert into YukonRoleProperty values(-10303,-103,'DCU SA205 Serial','false','Show a DCU SA205 Serial Number SortBy display');
insert into YukonRoleProperty values(-10304,-103,'DCU SA305 Serial','false','Show a DCU SA305 Serial Number SortBy display');

/* Calc Historical Role Properties */
insert into YukonRoleProperty values(-10400,-104,'interval','900','<description>');
insert into YukonRoleProperty values(-10401,-104,'baseline_calctime','4','<description>');
insert into YukonRoleProperty values(-10402,-104,'daysprevioustocollect','30','<description>');

/* Web Graph Role Properties */
insert into YukonRoleProperty values(-10500,-105,'home_directory','c:\yukon\client\webgraphs','<description>');
insert into YukonRoleProperty values(-10501,-105,'run_interval','900','<description>');

/* Billing Role Properties */
insert into YukonRoleProperty values(-10600,-106,'wiz_activate','false','<description>');
insert into YukonRoleProperty values(-10601,-106,'input_file','c:\yukon\client\bin\BillingIn.txt','<description>');

/* Esubstation Editor Role Properties */
insert into YukonRoleProperty values(-10700,-107,'default','false','The default esub editor property');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10800,-108,'home_url','/default.jsp','The url to take the user immediately after logging into the Yukon web applicatoin');
insert into YukonRoleProperty values(-10802,-108,'style_sheet','yukon/CannonStyle.css','The web client cascading style sheet.');
insert into YukonRoleProperty values(-10803,-108,'nav_bullet_selected','yukon/Bullet.gif','The bullet used when an item in the nav is selected.');
insert into YukonRoleProperty values(-10804,-108,'nav_bullet_expand','yukon/BulletExpand.gif','The bullet used when an item in the nav can be expanded to show submenu.');
insert into YukonRoleProperty values(-10805,-108,'header_logo','yukon/DemoHeader.gif','The main header logo');
insert into YukonRoleProperty values(-10806, -108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','yukon/BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','yukon/MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20101,-201,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-20102,-201,'Account Residence','false','Controls whether to show the customer residence information');
insert into YukonRoleProperty values(-20103,-201,'Account Call Tracking','false','Controls whether to enable the call tracking feature');
insert into YukonRoleProperty values(-20104,-201,'Metering Interval Data','false','Controls whether to show the metering interval data');
insert into YukonRoleProperty values(-20105,-201,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-20106,-201,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-20107,-201,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-20108,-201,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-20109,-201,'Appliances','true','Controls whether to show the appliance information');
insert into YukonRoleProperty values(-20110,-201,'Appliances Create','true','Controls whether to enable the appliance creation feature');
insert into YukonRoleProperty values(-20111,-201,'Hardwares','true','Controls whether to show the hardware information');
insert into YukonRoleProperty values(-20112,-201,'Hardwares Create','true','Controls whether to enable the hardware creation feature');
insert into YukonRoleProperty values(-20113,-201,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-20114,-201,'Work Orders','false','Controls whether to enable the service request feature');
insert into YukonRoleProperty values(-20115,-201,'Admin Change Login','true','Controls whether to enable the changing customer login feature');
insert into YukonRoleProperty values(-20116,-201,'Admin FAQ','false','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-20117,-201,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
insert into YukonRoleProperty values(-20118,-201,'Create Trend','false','Controls whether to allow new trends to assigned to the customer');

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20151,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
insert into YukonRoleProperty values(-20152,-201,'Import Customer Account','(none)','Controls whether to enable the customer account importing feature');
insert into YukonRoleProperty values(-20153,-201,'Inventory Checking','true','Controls when to perform inventory checking while creating or updating hardware information');
insert into YukonRoleProperty values(-20154,-201,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment');
insert into YukonRoleProperty values(-20155,-201,'Order Number Auto Generation','false','Controls whether the order number is automatically generated or entered by user');
insert into YukonRoleProperty values(-20156,-201,'Call Number Auto Generation','false','Controls whether the call number is automatically generated or entered by user');
insert into YukonRoleProperty values(-20157,-201,'Opt Out Rules','(none)','Defines the rules for opting out.');
insert into YukonRoleProperty values(-20158,-201,'Override Hardware','false','Controls whether to allow overriding individual hardware');

/* Operator Administrator Role Properties */
insert into YukonRoleProperty values(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
insert into YukonRoleProperty values(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
insert into YukonRoleProperty values(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');
insert into YukonRoleProperty values(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');

/* Operator Commercial Metering Role Properties*/
insert into YukonRoleProperty values(-20200,-202,'Trending Disclaimer',' ','The disclaimer that appears with trends');

/* Operator Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-20300,-203,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
insert into YukonRoleProperty values(-20301,-203,'Individual Switch','true','Controls access to operator individual switch control');

/* Operator Direct Curtailment Role Properties */
insert into YukonRoleProperty values(-20400,-204,'Direct Curtailment Label','Notification','The operator specific name for direct curtailment');

/* Operator Energy Exchange Role Properties */
insert into YukonRoleProperty values(-20500,-205,'Energy Buyback Label','Energy Buyback','The operator specific name for Energy Buyback');

/* Operator Esubstation Drawings Role Properties */
insert into YukonRoleProperty values(-20600,-206,'View Drawings','true','Controls viewing of Esubstations drawings');
insert into YukonRoleProperty values(-20601,-206,'Edit Limits','false','Controls editing of point limits');
insert into YukonRoleProperty values(-20602,-206,'Control','false','Controls control from Esubstation drawings');

/* Odds For Control Role Properties */
insert into YukonRoleProperty values(-20700,-207,'Odds For Control Label','Odds for Control','The operator specific name for odds for control');

/* Operator Consumer Info Role Properties Part II */
insert into YukonRoleProperty values(-20800,-201,'Link FAQ','(none)','The customized FAQ link');
insert into YukonRoleProperty values(-20801,-201,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');

insert into YukonRoleProperty values(-20810,-201,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-20813,-201,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-20814,-201,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-20815,-201,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-20816,-201,'Text Reenable','reenable','Term for reenable');
insert into YukonRoleProperty values(-20817,-201,'Text Override','override','Term for override');
insert into YukonRoleProperty values(-20819,-201,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-20820,-201,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');

insert into YukonRoleProperty values(-20830,-201,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20831,-201,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20832,-201,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20833,-201,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-20834,-201,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-20835,-201,'Label Account General','General','Text of the account general link');
insert into YukonRoleProperty values(-20836,-201,'Label Account Contacts','Contacts','Text of the account contacts link');
insert into YukonRoleProperty values(-20837,-201,'Label Account Residence','Residence','Text of the account residence link');
insert into YukonRoleProperty values(-20838,-201,'Label Call Tracking','Call Tracking','Text of the call tracking link');
insert into YukonRoleProperty values(-20839,-201,'Label Create Call','Create Call','Text of the create call link');
insert into YukonRoleProperty values(-20840,-201,'Label Service Request','Service Request','Text of the service request link');
insert into YukonRoleProperty values(-20841,-201,'Label Service History','Service History','Text of the service history link');
insert into YukonRoleProperty values(-20842,-201,'Label Change Login','Change Login','Text of the change login link');
insert into YukonRoleProperty values(-20843,-201,'Label FAQ','FAQ','Text of the FAQ link');
insert into YukonRoleProperty values(-20844,-201,'Label Interval Data','Interval Data','Text of the interval data link');
insert into YukonRoleProperty values(-20845,-201,'Label Thermostat Saved Schedules','Saved Schedules','Text of the thermostat saved schedules link');

insert into YukonRoleProperty values(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL HISTORY','Title of the programs control history page');
insert into YukonRoleProperty values(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20855,-201,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-20856,-201,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-20857,-201,'Title Call Tracking','ACCOUNT - CALL TRACKING','Title of the call tracking page');
insert into YukonRoleProperty values(-20858,-201,'Title Create Call','ACCOUNT - CREATE NEW CALL','Title of the create call page');
insert into YukonRoleProperty values(-20859,-201,'Title Service Request','WORK ORDERS - SERVICE REQUEST','Title of the service request page');
insert into YukonRoleProperty values(-20860,-201,'Title Service History','WORK ORDERS - SERVICE HISTORY','Title of the service history page');
insert into YukonRoleProperty values(-20861,-201,'Title Change Login','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
insert into YukonRoleProperty values(-20862,-201,'Title Create Trend','METERING - CREATE NEW TREND','Title of the create trend page');
insert into YukonRoleProperty values(-20863,-201,'Title Thermostat Saved Schedules','THERMOSTAT - SAVED SCHEDULES','Title of the thermostat saved schedules page');
insert into YukonRoleProperty values(-20864,-201,'Title Hardware Overriding','HARDWARE - OVERRIDING','Title of the hardware overriding page');

insert into YukonRoleProperty values(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame below, then click Submit.','Description on the programs opt out page');

insert into YukonRoleProperty values(-20880,-201,'Heading Account','Account','Heading of the account links');
insert into YukonRoleProperty values(-20881,-201,'Heading Metering','Metering','Heading of the metering links');
insert into YukonRoleProperty values(-20882,-201,'Heading Programs','Programs','Heading of the program links');
insert into YukonRoleProperty values(-20883,-201,'Heading Appliances','Appliances','Heading of the appliance links');
insert into YukonRoleProperty values(-20884,-201,'Heading Hardwares','Hardwares','Heading of the hardware links');
insert into YukonRoleProperty values(-20885,-201,'Heading Work Orders','Work Orders','Heading of the work order links');
insert into YukonRoleProperty values(-20886,-201,'Heading Administration','Administration','Heading of the administration links');
insert into YukonRoleProperty values(-20887,-201,'Sub-Heading Switches','Switches','Sub-heading of the switch links');
insert into YukonRoleProperty values(-20888,-201,'Sub-Heading Thermostats','Thermostats','Sub-heading of the thermostat links');
insert into YukonRoleProperty values(-20889,-201,'Sub-Heading Meters','Meters','Sub-heading of the meter links');

/* Operator Hardware Inventory Role Properties */
insert into YukonRoleProperty values(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
insert into YukonRoleProperty values(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardwares by serial number range');
insert into YukonRoleProperty values(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardwares by serial number range');
insert into YukonRoleProperty values(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardwares by serial number range');
insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');
insert into YukonRoleProperty values(-20905,-209,'Create Hardware','true','Controls whether to allow creating new hardware');
insert into YukonRoleProperty values(-20906,-209,'Create MCT','true','Controls whether to allow creating MCT devices');

/* operator work order management role properties */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');

/* CICustomer Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-30000,-300,'Direct Loadcontrol Label','Direct Control','The customer specific name for direct loadcontrol');
insert into YukonRoleProperty values(-30001,-300,'Individual Switch','false','Controls access to customer individual switch control');

/* CICustomer Curtailment Role Properties */
insert into YukonRoleProperty values(-30100,-301,'Direct Curtailment Label','Notification','The customer specific name for direct curtailment');
insert into YukonRoleProperty values(-30101,-301,'Direct Curtailment Provider','<curtailment provider>','This customers direct curtailment provider');

/* CICustomer Energy Buyback Role Properties */
insert into YukonRoleProperty values(-30200,-302,'Energy Buyback Label','Energy Buyback','The customer specific name for Energy Buyback');
insert into YukonRoleProperty values(-30201,-302,'Energy Buyback Phone Number','1-800-555-5555','The phone number to call if the customer has questions');

/* CICustomer Commercial Metering Role Properties */
insert into YukonRoleProperty values(-30400,-304,'Trending Disclaimer',' ','The disclaimer that appears with trends');
insert into yukonroleproperty values(-30401, -304, 'Trending Get Data Now Button', 'false', 'Controls access to retrieve meter data on demand');
insert into yukonroleproperty values(-30402, -304, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events');
insert into yukonroleproperty values(-30403, -304, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily');

/* CICustomer Administrator Role */
insert into yukonroleproperty values(-30500, -305, 'Contact Information Editable', 'false', 'Contact information is editable by the customer');

/* Add the CICustomer user-control properties */
insert into yukonroleproperty values(-30600, -306, 'User Control Label', 'User-Control', 'The customer specific name for user control');
insert into yukonroleproperty values(-30601, -306, 'Auto Control', 'true', 'Controls access to auto control.');
insert into yukonroleproperty values(-30602, -306, 'Time Based Control', 'true', 'Controls access to time based control');
insert into yukonroleproperty values(-30603, -306, 'Switch Command Control', 'true', 'Controls acces to switch commands');

/* Residential Customer Role Properties */
insert into YukonRoleProperty values(-40001,-400,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-40002,-400,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-40003,-400,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-40004,-400,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-40005,-400,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-40006,-400,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-40007,-400,'Questions Utility','true','Controls whether to show the contact information of the energy company');
insert into YukonRoleProperty values(-40008,-400,'Questions FAQ','true','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-40009,-400,'Admin Change Login','true','Controls whether to allow customers to change their own login');
insert into YukonRoleProperty values(-40010,-400,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');

insert into YukonRoleProperty values(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
insert into YukonRoleProperty values(-40052,-400,'Automatic Configuration','false','Controls whether to automatically send out config command when changing program enrollment');
insert into YukonRoleProperty values(-40054,-400,'Disable Program Signup','false','Controls whether to prevent the customers from enrolling in or out of the programs');
insert into YukonRoleProperty values(-40055,-400,'Opt Out Rules','(none)','Defines the rules for opting out.');

insert into YukonRoleProperty values(-40100,-400,'Link FAQ','(none)','The customized FAQ link');
insert into YukonRoleProperty values(-40102,-400,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');

insert into YukonRoleProperty values(-40110,-400,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-40111,-400,'Text Controlled','controlled','Past form of the term for control');
insert into YukonRoleProperty values(-40112,-400,'Text Controlling','controlling','Present form of the term for control');
insert into YukonRoleProperty values(-40113,-400,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-40114,-400,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-40115,-400,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-40116,-400,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-40117,-400,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');

insert into YukonRoleProperty values(-40130,-400,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-40131,-400,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-40132,-400,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-40133,-400,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-40134,-400,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-40135,-400,'Label Account General','General','Text of the account general link');
insert into YukonRoleProperty values(-40136,-400,'Label Contact Us','Contact Us','Text of the contact us link');
insert into YukonRoleProperty values(-40137,-400,'Label FAQ','FAQ','Text of the FAQ link');
insert into YukonRoleProperty values(-40138,-400,'Label Change Login','Change Login','Text of the change login link');
insert into YukonRoleProperty values(-40139,-400,'Label Thermostat Saved Schedules','Saved Schedules','Text of the thermostat saved schedules link');

insert into YukonRoleProperty values(-40150,-400,'Title General','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40151,-400,'Title Programs Control History','PROGRAMS - CONTROL HISTORY','Title of the programs control history page');
insert into YukonRoleProperty values(-40152,-400,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40153,-400,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40154,-400,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40155,-400,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40156,-400,'Title Utility','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40157,-400,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-40158,-400,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-40159,-400,'Title Thermostat Saved Schedules','THERMOSTAT - SAVED SCHEDULES','Title of the thermostat saved schedules page');

insert into YukonRoleProperty values(-40170,-400,'Description General','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40171,-400,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame below, then click Submit.','Description on the programs opt out page');
insert into YukonRoleProperty values(-40172,-400,'Description Enrollment','Select the check boxes and corresponding radio button of the programs you would like to be enrolled in.','Description on the program enrollment page');
insert into YukonRoleProperty values(-40173,-400,'Description Utility',' <<COMPANY_ADDRESS>><br><<PHONE_NUMBER>><<FAX_NUMBER>><<EMAIL>>','Description on the contact us page. The special fields will be replaced by real information when displayed on the web.');

insert into YukonRoleProperty values(-40180,-400,'Image Corner','yukon/Mom.jpg','Image at the upper-left corner');
insert into YukonRoleProperty values(-40181,-400,'Image General','yukon/Family.jpg','Image on the general page');

insert into YukonRoleProperty values(-40190,-400,'Heading Account','Account','Heading of the account links');
insert into YukonRoleProperty values(-40191,-400,'Heading Thermostat','Thermostat','Heading of the thermostat links');
insert into YukonRoleProperty values(-40192,-400,'Heading Metering','Metering','Heading of the metering links');
insert into YukonRoleProperty values(-40193,-400,'Heading Programs','Programs','Heading of the program links');
insert into YukonRoleProperty values(-40194,-400,'Heading Trending','Trending','Heading of the trending links');
insert into YukonRoleProperty values(-40195,-400,'Heading Questions','Questions','Heading of the questions links');
insert into YukonRoleProperty values(-40196,-400,'Heading Administration','Administration','Heading of the administration links');

/* Billing AMR role properties */
insert into YukonRoleProperty values(-50000,-500,'Header Label','Billing','The header label for billing.');
insert into YukonRoleProperty values(-50001,-500,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
insert into YukonRoleProperty values(-50002,-500,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
insert into YukonRoleProperty values(-50003,-500,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
insert into YukonRoleProperty values(-50004,-500,'Append To File','false','Append to existing file.');
insert into YukonRoleProperty values(-50005,-500,'Remove Multiplier','false','Remove the multiplier value from the reading.');
insert into YukonRoleProperty values(-50006,-500,'Input File Location','c:\yukon\client\bin\BillingIn.txt','The NCDC format takes in an input file.');
insert into YukonRoleProperty values(-50007,-500,'Coop ID - CADP Only','(none)','CADP format requires a coop id number.');

/* Reporting Analysis role properties */
insert into YukonRoleProperty values(-60000,-600,'Header Label','Reporting','The header label for reporting.');
insert into YukonRoleProperty values(-60001,-600,'Download Reports Enable','true','Access to download the report files..');
insert into YukonRoleProperty values(-60002,-600,'Download Reports Default Filename','report.txt','A default filename for the downloaded report.');
insert into YukonRoleProperty values(-60003,-600,'Admin Reports Group','true','Access to administrative group reports.');
insert into YukonRoleProperty values(-60004,-600,'AMR Reports Group','true','Access to AMR group reports.');
insert into YukonRoleProperty values(-60005,-600,'Statistical Reports Group','true','Access to statistical group reports.');
insert into YukonRoleProperty values(-60006,-600,'Load Managment Reports Group','false','Acces to Load Management group reports.');
insert into YukonRoleProperty values(-60007,-600,'Cap Control Reports Group','false','Access to Cap Control group reports.');
insert into YukonRoleProperty values(-60008,-600,'Database Reports Group','true','Access to Database group reports.');
insert into YukonRoleProperty values(-60009,-600,'Stars Reports Group','true','Access to Stars group reports.');
insert into YukonRoleProperty values(-60010,-600,'Other Reports Group','true','Access to Other group reports.');

insert into YukonRoleProperty values(-60013,-600,'Admin Reports Group Label','Administor','Label (header) for administrative group reports.');
insert into YukonRoleProperty values(-60014,-600,'AMR Reports Group Label','Metering','Label (header) for AMR group reports.');
insert into YukonRoleProperty values(-60015,-600,'Statistical Reports Group Label','Statistical','Label (header) for statistical group reports.');
insert into YukonRoleProperty values(-60016,-600,'Load Managment Reports Group Label','Load Management','Label (header) for Load Management group reports.');
insert into YukonRoleProperty values(-60017,-600,'Cap Control Reports Group Label','Cap Control','Label (header) for Cap Control group reports.');
insert into YukonRoleProperty values(-60018,-600,'Database Reports Group Label','Database','Label (header) for Database group reports.');
insert into YukonRoleProperty values(-60019,-600,'Stars Reports Group Label','Stars','Label (header) for Stars group reports.');
insert into YukonRoleProperty values(-60020,-600,'Other Reports Group Label','Other','Label (header) for Other group reports.');

/* Trending Analysis role properties */
insert into YukonRoleProperty values(-60100, -601, 'Trending Disclaimer',' ','The disclaimer that appears with trends.');
insert into yukonroleproperty values(-60101, -601, 'Scan Now Enabled', 'false', 'Controls access to retrieve meter data on demand.');
insert into yukonroleproperty values(-60102, -601, 'Scan Now Label', 'Get Data Now', 'The label for the scan data now option.');
insert into yukonroleproperty values(-60103, -601, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events.');
insert into yukonroleproperty values(-60104, -601, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily.');
insert into yukonroleproperty values(-60105, -601, 'Reset Peaks Enabled', 'false', 'Allow access to reset the peak time period.');
insert into yukonroleproperty values(-60106, -601, 'Header Label', 'Trending', 'The header label for trends.');
insert into yukonroleproperty values(-60107, -601, 'Header Secondary Label', 'Interval Data', 'A secondary header label for grouping trends.');
insert into yukonroleproperty values(-60108, -601, 'Trend Assignment', 'false', 'Allow assignment of trends to users.');
insert into yukonroleproperty values(-60109, -601, 'Trend Create', 'false', 'Allow creation of new trends.');
insert into yukonroleproperty values(-60110, -601, 'Trend Delete', 'false', 'Allow deletion of old trends.');
insert into yukonroleproperty values(-60111, -601, 'Trend Edit', 'false', 'Allow ditting of existing trends.');
insert into yukonroleproperty values(-60112, -601, 'Options Button Enabled', 'true', 'Display the Options link to additional trending configuration properties.');
insert into yukonroleproperty values(-60113, -601, 'Export/Print Button Enabled', 'true', 'Display the Export/Print options button (drop down menu).');
insert into yukonroleproperty values(-60114, -601, 'View Button Enabled', 'true', 'Display the View options button (drop down menu).');
insert into yukonroleproperty values(-60115, -601, 'Export/Print Button Label', 'Trend', 'The label for the trend print/export button (drop down menu).');
insert into yukonroleproperty values(-60116, -601, 'View Button Label', 'View', 'The label for the trend view options button (drop down menu).');
insert into yukonroleproperty values(-60117, -601, 'Trending Usage', 'false', 'Allow access to trending time of use.');
insert into yukonroleproperty values(-60118, -601, 'Default Start Date Offset', '0', 'Offset the start date by this number.');
insert into yukonroleproperty values(-60119, -601, 'Default Time Period', '(none)', 'Default the time period.');

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


insert into YukonSelectionList values( 0, 'N', '(none)', '(none)', '(none)', 'N' );
insert into YukonSelectionList values( 1, 'A', 'Contact', 'DBEditor contact type list', 'ContactType', 'N' );


insert into YukonSelectionList values (1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N');
insert into YukonSelectionList values (1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N');
insert into YukonSelectionList values (1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N');
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','Y');
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','Y');
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection','DeviceStatus','N');
insert into YukonSelectionList values (1007,'A','(none)','Appliance category','ApplianceCategory','N');
insert into YukonSelectionList values (1008,'A','(none)','Call type selection','CallType','Y');
insert into YukonSelectionList values (1009,'A','(none)','Service type selection','ServiceType','Y');
insert into YukonSelectionList values (1010,'N','(none)','Service request status','ServiceStatus','N');
insert into YukonSelectionList values (1011,'N','(none)','Search by selection','SearchBy','N');
insert into YukonSelectionList values (1012,'A','(none)','Appliance manufacturer selection','Manufacturer','Y');
insert into YukonSelectionList values (1013,'A','(none)','Appliance location selection','ApplianceLocation','Y');
insert into YukonSelectionList values (1014,'N','(none)','Chance of control selection','ChanceOfControl','Y');
insert into YukonSelectionList values (1015,'N','(none)','Thermostat settings time of week selection','TimeOfWeek','N');
insert into YukonSelectionList values (1016,'N','(none)','Question type selection','QuestionType','N');
insert into YukonSelectionList values (1017,'N','(none)','Answer type selection','AnswerType','N');
insert into YukonSelectionList values (1018,'N','(none)','Thermostat mode selection','ThermostatMode','N');
insert into YukonSelectionList values (1019,'N','(none)','Thermostat fan state selection','ThermostatFanState','N');
insert into YukonSelectionList values (1020,'O','(none)','Customer FAQ groups','CustomerFAQGroup','N');
insert into YukonSelectionList values (1021,'N','(none)','Residence type selection','ResidenceType','Y');
insert into YukonSelectionList values (1022,'N','(none)','Construction material selection','ConstructionMaterial','Y');
insert into YukonSelectionList values (1023,'N','(none)','Decade built selection','DecadeBuilt','Y');
insert into YukonSelectionList values (1024,'N','(none)','Square feet selection','SquareFeet','Y');
insert into YukonSelectionList values (1025,'N','(none)','Insulation depth selection','InsulationDepth','Y');
insert into YukonSelectionList values (1026,'N','(none)','General condition selection','GeneralCondition','Y');
insert into YukonSelectionList values (1027,'N','(none)','Main cooling system selection','CoolingSystem','Y');
insert into YukonSelectionList values (1028,'N','(none)','Main heating system selection','HeatingSystem','Y');
insert into YukonSelectionList values (1029,'N','(none)','Number of occupants selection','NumberOfOccupants','Y');
insert into YukonSelectionList values (1030,'N','(none)','Ownership type selection','OwnershipType','Y');
insert into YukonSelectionList values (1031,'N','(none)','Main fuel type selection','FuelType','Y');
insert into YukonSelectionList values (1032,'N','(none)','AC Tonnage selection','ACTonnage','Y');
insert into YukonSelectionList values (1033,'N','(none)','AC type selection','ACType','Y');
insert into YukonSelectionList values (1034,'N','(none)','Water heater number of gallons selection','WHNumberOfGallons','Y');
insert into YukonSelectionList values (1035,'N','(none)','Water heater energy source selection','WHEnergySource','Y');
insert into YukonSelectionList values (1036,'N','(none)','Dual fuel switch over type selection','DFSwitchOverType','Y');
insert into YukonSelectionList values (1037,'N','(none)','Dual fuel secondary source selection','DFSecondarySource','Y');
insert into YukonSelectionList values (1038,'N','(none)','Grain dryer type selection','GrainDryerType','Y');
insert into YukonSelectionList values (1039,'N','(none)','Grain dryer bin size selection','GDBinSize','Y');
insert into YukonSelectionList values (1040,'N','(none)','Grain dryer blower energy source selection','GDEnergySource','Y');
insert into YukonSelectionList values (1041,'N','(none)','Grain dryer blower horse power selection','GDHorsePower','Y');
insert into YukonSelectionList values (1042,'N','(none)','Grain dryer blower heat source selection','GDHeatSource','Y');
insert into YukonSelectionList values (1043,'N','(none)','Storage heat type selection','StorageHeatType','Y');
insert into YukonSelectionList values (1044,'N','(none)','Heat pump type selection','HeatPumpType','Y');
insert into YukonSelectionList values (1045,'N','(none)','Heat pump standby source selection','HPStandbySource','Y');
insert into YukonSelectionList values (1046,'N','(none)','Irrigation type selection','IrrigationType','Y');
insert into YukonSelectionList values (1047,'N','(none)','Irrigation soil type selection','IRRSoilType','Y');
insert into YukonSelectionList values (1048,'N','(none)','Device location selection','DeviceLocation','N');
insert into YukonSelectionList values (1049,'O','(none)','Opt out period selection','OptOutPeriod','Y');
insert into YukonSelectionList values (1050,'N','(none)','Gateway end device data description','GatewayEndDeviceDataDesc','N');
insert into YukonSelectionList values (1051,'N','(none)','Hardware Inventory search by selection','InvSearchBy','N');
insert into YukonSelectionList values (1052,'N','(none)','Hardware Inventory sort by selection','InvSortBy','N');
insert into YukonSelectionList values (1053,'N','(none)','Hardware Inventory filter by selection','InvFilterBy','N');
insert into YukonSelectionList values (1054,'N','(none)','Service order search by selection','SOSearchBy','N');
insert into YukonSelectionList values (1055,'N','(none)','Service order sort by selection','SOSortBy','N');
insert into YukonSelectionList values (1056,'N','(none)','Service order filter by selection','SOFilterBy','N');
insert into YukonSelectionList values (1057,'N','(none)','Generator transfer switch type selection','GENTransferSwitchType','Y');
insert into YukonSelectionList values (1058,'N','(none)','Generator transfer switch manufacturer selection','GENTransferSwitchMfg','Y');
insert into YukonSelectionList values (1059,'N','(none)','Irrigation horse power selection','IRRHorsePower','Y');
insert into YukonSelectionList values (1060,'N','(none)','Irrigation energy source selection','IRREnergySource','Y');
insert into YukonSelectionList values (1061,'N','(none)','Irrigation meter location selection','IRRMeterLocation','Y');
insert into YukonSelectionList values (1062,'N','(none)','Irrigation meter voltage selection','IRRMeterVoltage','Y');
insert into YukonSelectionList values (1063,'N','(none)','Water heater location selection','WHLocation','Y');
insert into YukonSelectionList values (1064,'N','(none)','Heat pump size selection','HeatPumpSize','Y');
insert into YukonSelectionList values (2000,'N','(none)','Customer Selection Base','(none)','N');

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


insert into YukonServices values( 1, 'Notification_Server', 'com.cannontech.jmx.services.DynamicNotifcationServer', '(none)', '(none)' );
/* insert into YukonServices values( 2, 'WebGraph', 'com.cannontech.jmx.services.DynamicWebGraph', '(none)', '(none)' ); */
/* insert into YukonServices values( 3, 'Calc_Historical', 'com.cannontech.jmx.services.DynamicCalcHist', '(none)', '(none)' ); */
/* insert into YukonServices values( 4, 'CBC_OneLine_Gen', 'com.cannontech.jmx.services.DynamicCBCOneLine', '(none)', '(none)'); */

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


insert into YukonUser values(-1,'admin','admin',0,'01-JAN-00','Enabled');
insert into YukonUser values(-2,'yukon','yukon',0,'01-JAN-00','Enabled');
insert into YukonUser values(-100,'DefaultCTI','$cti_default',0,'01-JAN-00','Enabled');
insert into YukonUser values(-9999,'(none)','(none)',0,'01-JAN-00','Disabled');

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


insert into YukonUserGroup values(-1,-1);
insert into YukonUserGroup values(-2,-1);
insert into YukonUserGroup values(-2,-2);

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


/* Database Editor */
insert into YukonUserRole values(-100,-1,-100,-10000,'(none)');
insert into YukonUserRole values(-101,-1,-100,-10001,'(none)');
insert into YukonUserRole values(-102,-1,-100,-10002,'(none)');
insert into YukonUserRole values(-103,-1,-100,-10003,'(none)');
insert into YukonUserRole values(-104,-1,-100,-10004,'(none)');
insert into YukonUserRole values(-105,-1,-100,-10005,'(none)');
insert into YukonUserRole values(-107,-1,-100,-10007,'true');
insert into YukonUserRole values(-108,-1,-100,-10008,'(none)');
insert into YukonUserRole values(-109,-1,-100,-10009,'(none)');

/* TDC */
insert into YukonUserRole values(-120,-1,-101,-10100,'(none)');
insert into YukonUserRole values(-121,-1,-101,-10101,'(none)');
insert into YukonUserRole values(-122,-1,-101,-10102,'(none)');
insert into YukonUserRole values(-123,-1,-101,-10103,'(none)');
insert into YukonUserRole values(-124,-1,-101,-10104,'(none)');
insert into YukonUserRole values(-125,-1,-101,-10105,'(none)');
insert into YukonUserRole values(-126,-1,-101,-10106,'(none)');
insert into YukonUserRole values(-127,-1,-101,-10107,'(none)');
insert into YukonUserRole values(-128,-1,-101,-10108,'(none)');
insert into YukonUserRole values(-129,-1,-101,-10109,'(none)');
insert into YukonUserRole values(-130,-1,-101,-10111,'(none)');


/* Trending */
insert into YukonUserRole values(-150,-1,-102,-10200,'(none)');

/* Commander */
insert into YukonUserRole values(-170,-1,-103,-10300,'(none)');
insert into YukonUserRole values(-171,-1,-103,-10301,'true');
insert into YukonUserRole values(-172,-1,-103,-10302,'true');
insert into YukonUserRole values(-173,-1,-103,-10303,'false');
insert into YukonUserRole values(-174,-1,-103,-10304,'false');

/* Esubstation Editor */
insert into YukonUserRole values(-250,-1,-107,-10700,'(none)');

insert into YukonUserRole values(-300,-1,-206,-20600,'(none)');
insert into YukonUserRole values(-301,-1,-206,-20601,'(none)');
insert into YukonUserRole values(-302,-1,-206,-20602,'(none)');

insert into YukonUserRole values(-350,-1,-206,-20600,'(none)');
insert into YukonUserRole values(-351,-1,-206,-20601,'true');
insert into YukonUserRole values(-352,-1,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into YukonUserRole values (-400, -1, -108, -10800, '/user/CILC/user_trending.jsp');
insert into YukonUserRole values (-402, -1, -108, -10802, '(none)');
insert into YukonUserRole values (-403, -1, -108, -10803, '(none)');
insert into YukonUserRole values (-404, -1, -108, -10804, '(none)');
insert into YukonUserRole values (-405, -1, -108, -10805, '(none)');
insert into YukonUserRole values (-406, -1, -108, -10806, '(none)');
insert into YukonUserRole values (-407, -1, -108, -10807, '(none)');
insert into YukonUserRole values (-408, -1, -108, -10808, '(none)');

/* Web Client Customers Direct Load Control role */
insert into YukonUserRole values (-410, -1, -300, -30000, '(none)');
insert into YukonUserRole values (-411, -1, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into YukonUserRole values (-420, -1, -301, -30100, '(none)');
insert into YukonUserRole values (-421, -1, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into YukonUserRole values (-430, -1, -302, -30200, '(none)');
insert into YukonUserRole values (-431, -1, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into YukonUserRole values (-440, -1, -304, -30400, '(none)');
insert into YukonUserRole values (-441, -1, -304, -30401, 'true');
insert into YukonUserRole values (-442, -1, -304, -30402, '(none)'); 
insert into YukonUserRole values (-443, -1, -304, -30403, '(none)');

/* Web Client Customers Administrator role */
insert into YukonUserRole values (-450, -1, -305, -30500, 'true');

insert into YukonUserRole values (-521,-1,-400,-40001,'(none)');
insert into YukonUserRole values (-522,-1,-400,-40002,'(none)');
insert into YukonUserRole values (-523,-1,-400,-40003,'(none)');
insert into YukonUserRole values (-524,-1,-400,-40004,'(none)');
insert into YukonUserRole values (-525,-1,-400,-40005,'(none)');
insert into YukonUserRole values (-526,-1,-400,-40006,'(none)');
insert into YukonUserRole values (-527,-1,-400,-40007,'(none)');
insert into YukonUserRole values (-528,-1,-400,-40008,'(none)');
insert into YukonUserRole values (-529,-1,-400,-40009,'(none)');
insert into YukonUserRole values (-530,-1,-400,-40010,'(none)');

insert into YukonUserRole values (-551,-1,-400,-40051,'(none)');
insert into YukonUserRole values (-552,-1,-400,-40052,'(none)');
insert into YukonUserRole values (-554,-1,-400,-40054,'(none)');
insert into YukonUserRole values (-555,-1,-400,-40055,'(none)');

insert into YukonUserRole values (-600,-1,-400,-40100,'(none)');
insert into YukonUserRole values (-610,-1,-400,-40110,'(none)');
insert into YukonUserRole values (-611,-1,-400,-40111,'(none)');
insert into YukonUserRole values (-612,-1,-400,-40112,'(none)');
insert into YukonUserRole values (-613,-1,-400,-40113,'(none)');
insert into YukonUserRole values (-614,-1,-400,-40114,'(none)');
insert into YukonUserRole values (-615,-1,-400,-40115,'(none)');
insert into YukonUserRole values (-616,-1,-400,-40116,'(none)');
insert into YukonUserRole values (-617,-1,-400,-40117,'(none)');
insert into YukonUserRole values (-630,-1,-400,-40130,'(none)');
insert into YukonUserRole values (-631,-1,-400,-40131,'(none)');
insert into YukonUserRole values (-632,-1,-400,-40132,'(none)');
insert into YukonUserRole values (-633,-1,-400,-40133,'(none)');
insert into YukonUserRole values (-634,-1,-400,-40134,'(none)');
insert into YukonUserRole values (-650,-1,-400,-40150,'(none)');
insert into YukonUserRole values (-651,-1,-400,-40151,'(none)');
insert into YukonUserRole values (-652,-1,-400,-40152,'(none)');
insert into YukonUserRole values (-653,-1,-400,-40153,'(none)');
insert into YukonUserRole values (-654,-1,-400,-40154,'(none)');
insert into YukonUserRole values (-655,-1,-400,-40155,'(none)');
insert into YukonUserRole values (-656,-1,-400,-40156,'(none)');
insert into YukonUserRole values (-657,-1,-400,-40157,'(none)');
insert into YukonUserRole values (-658,-1,-400,-40158,'(none)');
insert into YukonUserRole values (-670,-1,-400,-40170,'(none)');
insert into YukonUserRole values (-671,-1,-400,-40171,'(none)');
insert into YukonUserRole values (-672,-1,-400,-40172,'(none)');
insert into YukonUserRole values (-673,-1,-400,-40173,'(none)');
insert into YukonUserRole values (-680,-1,-400,-40180,'(none)');
insert into YukonUserRole values (-681,-1,-400,-40181,'(none)');

insert into YukonUserRole values (-721,-1,-201,-20101,'(none)');
insert into YukonUserRole values (-722,-1,-201,-20102,'(none)');
insert into YukonUserRole values (-723,-1,-201,-20103,'(none)');
insert into YukonUserRole values (-724,-1,-201,-20104,'(none)');
insert into YukonUserRole values (-725,-1,-201,-20105,'(none)');
insert into YukonUserRole values (-726,-1,-201,-20106,'(none)');
insert into YukonUserRole values (-727,-1,-201,-20107,'(none)');
insert into YukonUserRole values (-728,-1,-201,-20108,'(none)');
insert into YukonUserRole values (-729,-1,-201,-20109,'(none)');
insert into YukonUserRole values (-730,-1,-201,-20110,'(none)');
insert into YukonUserRole values (-731,-1,-201,-20111,'(none)');
insert into YukonUserRole values (-732,-1,-201,-20112,'(none)');
insert into YukonUserRole values (-733,-1,-201,-20113,'(none)');
insert into YukonUserRole values (-734,-1,-201,-20114,'(none)');
insert into YukonUserRole values (-735,-1,-201,-20115,'(none)');
insert into YukonUserRole values (-736,-1,-201,-20116,'(none)');
insert into YukonUserRole values (-737,-1,-201,-20117,'(none)');
insert into YukonUserRole values (-738,-1,-201,-20118,'true');

insert into YukonUserRole values (-751,-1,-201,-20151,'(none)');
insert into YukonUserRole values (-752,-1,-201,-20152,'(none)');
insert into YukonUserRole values (-753,-1,-201,-20153,'(none)');
insert into YukonUserRole values (-754,-1,-201,-20154,'(none)');
insert into YukonUserRole values (-755,-1,-201,-20155,'(none)');
insert into YukonUserRole values (-756,-1,-201,-20156,'(none)');
insert into YukonUserRole values (-757,-1,-201,-20157,'(none)');

insert into YukonUserRole values (-765,-1,-210,-21000,'(none)');
insert into YukonUserRole values (-766,-1,-210,-21001,'(none)');

insert into YukonUserRole values (-770,-1,-202,-20200,'(none)');
insert into YukonUserRole values (-775,-1,-203,-20300,'(none)');
insert into YukonUserRole values (-776,-1,-203,-20301,'(none)');
insert into YukonUserRole values (-780,-1,-204,-20400,'(none)');
insert into YukonUserRole values (-785,-1,-205,-20500,'(none)');
insert into YukonUserRole values (-790,-1,-207,-20700,'(none)');
insert into YukonUserRole values (-791,-1,-209,-20900,'(none)');
insert into YukonUserRole values (-792,-1,-209,-20901,'(none)');
insert into YukonUserRole values (-793,-1,-209,-20902,'(none)');
insert into YukonUserRole values (-794,-1,-209,-20903,'(none)');
insert into YukonUserRole values (-795,-1,-209,-20904,'(none)');

insert into YukonUserRole values (-800,-1,-201,-20800,'(none)');
insert into YukonUserRole values (-810,-1,-201,-20810,'(none)');
insert into YukonUserRole values (-813,-1,-201,-20813,'(none)');
insert into YukonUserRole values (-814,-1,-201,-20814,'(none)');
insert into YukonUserRole values (-815,-1,-201,-20815,'(none)');
insert into YukonUserRole values (-816,-1,-201,-20816,'(none)');
insert into YukonUserRole values (-819,-1,-201,-20819,'(none)');
insert into YukonUserRole values (-820,-1,-201,-20820,'(none)');
insert into YukonUserRole values (-830,-1,-201,-20830,'(none)');
insert into YukonUserRole values (-831,-1,-201,-20831,'(none)');
insert into YukonUserRole values (-832,-1,-201,-20832,'(none)');
insert into YukonUserRole values (-833,-1,-201,-20833,'(none)');
insert into YukonUserRole values (-834,-1,-201,-20834,'(none)');
insert into YukonUserRole values (-850,-1,-201,-20850,'(none)');
insert into YukonUserRole values (-851,-1,-201,-20851,'(none)');
insert into YukonUserRole values (-852,-1,-201,-20852,'(none)');
insert into YukonUserRole values (-853,-1,-201,-20853,'(none)');
insert into YukonUserRole values (-854,-1,-201,-20854,'(none)');
insert into YukonUserRole values (-855,-1,-201,-20855,'(none)');
insert into YukonUserRole values (-856,-1,-201,-20856,'(none)');
insert into YukonUserRole values (-870,-1,-201,-20870,'(none)');

insert into YukonUserRole values (-1000, -100, -108, -10800, '/operator/Operations.jsp');
insert into YukonUserRole values (-1002, -100, -108, -10802, '(none)');
insert into YukonUserRole values (-1003, -100, -108, -10803, '(none)');
insert into YukonUserRole values (-1004, -100, -108, -10804, '(none)');
insert into YukonUserRole values (-1005, -100, -108, -10805, '(none)');
insert into YukonUserRole values (-1006, -100, -108, -10806, '(none)');
insert into YukonUserRole values (-1010, -100, -200, -20000, '(none)');
insert into YukonUserRole values (-1011, -100, -200, -20001, 'true');
insert into YukonUserRole values (-1012, -100, -200, -20002, '(none)');
insert into YukonUserRole values (-1013, -100, -200, -20003, '(none)');

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


INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Cooling','Cool');
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Heating','Heat');
insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');

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


alter table DeviceTypeCommand
   add constraint FK_DevCmd_Cmd foreign key (CommandID)
      references Command (CommandID)
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


