/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      CTI Oracle 8.1.5                             */
/* Created on:     9/19/2003 3:19:42 PM                         */
/*==============================================================*/


drop view DISPLAY2WAYDATA_VIEW
/


drop view ExpressComAddress_View
/


drop view FeederAddress_View
/


drop view FullEventLog_View
/


drop view FullPointHistory_View
/


drop view GeoAddress_View
/


drop view LMCurtailCustomerActivity_View
/


drop view LMGroupMacroExpander_View
/


drop view Peakpointhistory_View
/


drop view PointEventLog_View
/


drop view PointHistory_View
/


drop view ProgramAddress_View
/


drop view ServiceAddress_View
/


drop view SubstationAddress_View
/


drop table Address cascade constraints
/


drop table AlarmCategory cascade constraints
/


drop table BaseLine cascade constraints
/


drop table BillingFileFormats cascade constraints
/


drop table CALCBASE cascade constraints
/


drop table CALCCOMPONENT cascade constraints
/


drop table CAPBANK cascade constraints
/


drop table CAPCONTROLSUBSTATIONBUS cascade constraints
/


drop table CCFeederBankList cascade constraints
/


drop table CCFeederSubAssignment cascade constraints
/


drop table CICustomerBase cascade constraints
/


drop table COLUMNTYPE cascade constraints
/


drop table CTIDatabase cascade constraints
/


drop table CalcPointBaseline cascade constraints
/


drop table CapControlFeeder cascade constraints
/


drop table CarrierRoute cascade constraints
/


drop table CommErrorHistory cascade constraints
/


drop table CommPort cascade constraints
/


drop table Contact cascade constraints
/


drop table ContactNotification cascade constraints
/


drop table Customer cascade constraints
/


drop table CustomerAdditionalContact cascade constraints
/


drop table CustomerBaseLinePoint cascade constraints
/


drop table CustomerLoginSerialGroup cascade constraints
/


drop table DEVICE cascade constraints
/


drop table DEVICE2WAYFLAGS cascade constraints
/


drop table DEVICECARRIERSETTINGS cascade constraints
/


drop table DEVICEDIALUPSETTINGS cascade constraints
/


drop table DEVICEIDLCREMOTE cascade constraints
/


drop table DEVICEIED cascade constraints
/


drop table DEVICELOADPROFILE cascade constraints
/


drop table DEVICEMCTIEDPORT cascade constraints
/


drop table DEVICEMETERGROUP cascade constraints
/


drop table DEVICESCANRATE cascade constraints
/


drop table DEVICETAPPAGINGSETTINGS cascade constraints
/


drop table DISPLAY cascade constraints
/


drop table DISPLAY2WAYDATA cascade constraints
/


drop table DISPLAYCOLUMNS cascade constraints
/


drop table DYNAMICACCUMULATOR cascade constraints
/


drop table DYNAMICDEVICESCANDATA cascade constraints
/


drop table DYNAMICPOINTDISPATCH cascade constraints
/


drop table DateOfHoliday cascade constraints
/


drop table DeviceCBC cascade constraints
/


drop table DeviceCustomerList cascade constraints
/


drop table DeviceDNP cascade constraints
/


drop table DeviceDirectCommSettings cascade constraints
/


drop table DeviceRoutes cascade constraints
/


drop table DeviceWindow cascade constraints
/


drop table DynamicCCCapBank cascade constraints
/


drop table DynamicCCFeeder cascade constraints
/


drop table DynamicCCSubstationBus cascade constraints
/


drop table DynamicCalcHistorical cascade constraints
/


drop table DynamicLMControlArea cascade constraints
/


drop table DynamicLMControlAreaTrigger cascade constraints
/


drop table DynamicLMGroup cascade constraints
/


drop table DynamicLMProgram cascade constraints
/


drop table DynamicLMProgramDirect cascade constraints
/


drop table DynamicPAOStatistics cascade constraints
/


drop table DynamicPointAlarming cascade constraints
/


drop table DynamicTags cascade constraints
/


drop table EnergyCompany cascade constraints
/


drop table EnergyCompanyCustomerList cascade constraints
/


drop table EnergyCompanyOperatorLoginList cascade constraints
/


drop table FDRInterface cascade constraints
/


drop table FDRInterfaceOption cascade constraints
/


drop table FDRTRANSLATION cascade constraints
/


drop table FDRTelegyrGroup cascade constraints
/


drop table GRAPHDATASERIES cascade constraints
/


drop table GRAPHDEFINITION cascade constraints
/


drop table GatewayEndDevice cascade constraints
/


drop table GenericMacro cascade constraints
/


drop table GraphCustomerList cascade constraints
/


drop table HolidaySchedule cascade constraints
/


drop table LMCONTROLAREAPROGRAM cascade constraints
/


drop table LMCONTROLAREATRIGGER cascade constraints
/


drop table LMControlArea cascade constraints
/


drop table LMControlHistory cascade constraints
/


drop table LMCurtailCustomerActivity cascade constraints
/


drop table LMCurtailProgramActivity cascade constraints
/


drop table LMDirectCustomerList cascade constraints
/


drop table LMDirectOperatorList cascade constraints
/


drop table LMEnergyExchangeCustomerList cascade constraints
/


drop table LMEnergyExchangeCustomerReply cascade constraints
/


drop table LMEnergyExchangeHourlyCustomer cascade constraints
/


drop table LMEnergyExchangeHourlyOffer cascade constraints
/


drop table LMEnergyExchangeOfferRevision cascade constraints
/


drop table LMEnergyExchangeProgramOffer cascade constraints
/


drop table LMGroup cascade constraints
/


drop table LMGroupEmetcon cascade constraints
/


drop table LMGroupExpressCom cascade constraints
/


drop table LMGroupExpressComAddress cascade constraints
/


drop table LMGroupMCT cascade constraints
/


drop table LMGroupPoint cascade constraints
/


drop table LMGroupRipple cascade constraints
/


drop table LMGroupVersacom cascade constraints
/


drop table LMMACSScheduleOperatorList cascade constraints
/


drop table LMMacsScheduleCustomerList cascade constraints
/


drop table LMPROGRAM cascade constraints
/


drop table LMProgramControlWindow cascade constraints
/


drop table LMProgramCurtailCustomerList cascade constraints
/


drop table LMProgramCurtailment cascade constraints
/


drop table LMProgramDirect cascade constraints
/


drop table LMProgramDirectGear cascade constraints
/


drop table LMProgramDirectGroup cascade constraints
/


drop table LMProgramEnergyExchange cascade constraints
/


drop table LMThermoStatGear cascade constraints
/


drop table LOGIC cascade constraints
/


drop table MACROROUTE cascade constraints
/


drop table MACSchedule cascade constraints
/


drop table MACSimpleSchedule cascade constraints
/


drop table MCTBroadCastMapping cascade constraints
/


drop table NotificationDestination cascade constraints
/


drop table NotificationGroup cascade constraints
/


drop table OperatorLoginGraphList cascade constraints
/


drop table OperatorSerialGroup cascade constraints
/


drop table PAOExclusion cascade constraints
/


drop table PAOowner cascade constraints
/


drop table POINT cascade constraints
/


drop table POINTACCUMULATOR cascade constraints
/


drop table POINTANALOG cascade constraints
/


drop table POINTLIMITS cascade constraints
/


drop table POINTSTATUS cascade constraints
/


drop table POINTUNIT cascade constraints
/


drop table PORTDIALUPMODEM cascade constraints
/


drop table PORTLOCALSERIAL cascade constraints
/


drop table PORTRADIOSETTINGS cascade constraints
/


drop table PORTSETTINGS cascade constraints
/


drop table PORTTERMINALSERVER cascade constraints
/


drop table PointAlarming cascade constraints
/


drop table PortStatistics cascade constraints
/


drop table PortTiming cascade constraints
/


drop table RAWPOINTHISTORY cascade constraints
/


drop table RepeaterRoute cascade constraints
/


drop table Route cascade constraints
/


drop table SOELog cascade constraints
/


drop table STATE cascade constraints
/


drop table STATEGROUP cascade constraints
/


drop table SYSTEMLOG cascade constraints
/


drop table SeasonSchedule cascade constraints
/


drop table TEMPLATE cascade constraints
/


drop table TEMPLATECOLUMNS cascade constraints
/


drop table TagLog cascade constraints
/


drop table Tags cascade constraints
/


drop table UNITMEASURE cascade constraints
/


drop table VersacomRoute cascade constraints
/


drop table YukonGroup cascade constraints
/


drop table YukonGroupRole cascade constraints
/


drop table YukonImage cascade constraints
/


drop table YukonListEntry cascade constraints
/


drop table YukonPAObject cascade constraints
/


drop table YukonRole cascade constraints
/


drop table YukonRoleProperty cascade constraints
/


drop table YukonSelectionList cascade constraints
/


drop table YukonUser cascade constraints
/


drop table YukonUserGroup cascade constraints
/


drop table YukonUserRole cascade constraints
/


drop table YukonWebConfiguration cascade constraints
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

/*==============================================================*/
/* Index: Indx_CUSTOMERADDRESS_PK                               */
/*==============================================================*/
create unique index Indx_CUSTOMERADDRESS_PK on Address (
   AddressID ASC
)
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

/*==============================================================*/
/* Index: Indx_ALARMCATEGORY_PK                                 */
/*==============================================================*/
create unique index Indx_ALARMCATEGORY_PK on AlarmCategory (
   AlarmCategoryID ASC
)
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

/*==============================================================*/
/* Index: Indx_BASELINE_PK                                      */
/*==============================================================*/
create unique index Indx_BASELINE_PK on BaseLine (
   BaselineID ASC
)
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
insert into billingfileformats values( 12, 'SEDC 5.4');
insert into billingfileformats values( 13, 'NISC-Turtle');
insert into billingfileformats values( 14, 'NISC-NCDC');


/*==============================================================*/
/* Index: Indx_BILLINGFILEFORM_PK                               */
/*==============================================================*/
create unique index Indx_BILLINGFILEFORM_PK on BillingFileFormats (
   FormatID ASC
)
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


/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create index Indx_ClcBaseUpdTyp on CALCBASE (
   UPDATETYPE ASC
)
/


/*==============================================================*/
/* Index: Indx_CALCBASE_PK                                      */
/*==============================================================*/
create unique index Indx_CALCBASE_PK on CALCBASE (
   POINTID ASC
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


/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create index Indx_CalcCmpCmpType on CALCCOMPONENT (
   COMPONENTTYPE ASC
)
/


/*==============================================================*/
/* Index: Indx_CALCCOMPONENT_PK                                 */
/*==============================================================*/
create unique index Indx_CALCCOMPONENT_PK on CALCCOMPONENT (
   PointID ASC,
   COMPONENTORDER ASC
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
   MapLocationID        NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_CAPBANK_PK                                       */
/*==============================================================*/
create unique index Indx_CAPBANK_PK on CAPBANK (
   DEVICEID ASC
)
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
   ControlUnits         VARCHAR2(20)                     not null
)
/


/*==============================================================*/
/* Index: Indx_CAPCONTROLSUBST_PK                               */
/*==============================================================*/
create unique index Indx_CAPCONTROLSUBST_PK on CAPCONTROLSUBSTATIONBUS (
   SubstationBusID ASC
)
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


/*==============================================================*/
/* Index: Indx_CCFEEDERBANKLIS_PK                               */
/*==============================================================*/
create unique index Indx_CCFEEDERBANKLIS_PK on CCFeederBankList (
   FeederID ASC,
   DeviceID ASC
)
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


/*==============================================================*/
/* Index: Indx_CCFEEDERSUBASSI_PK                               */
/*==============================================================*/
create unique index Indx_CCFEEDERSUBASSI_PK on CCFeederSubAssignment (
   SubStationBusID ASC,
   FeederID ASC
)
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


/*==============================================================*/
/* Index: Indx_CICUSTOMERBASE_PK                                */
/*==============================================================*/
create unique index Indx_CICUSTOMERBASE_PK on CICustomerBase (
   CustomerID ASC
)
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
insert into columntype values (13, 'Tags');
insert into columntype values (14, 'PointImage' );
insert into columntype values (15, 'QualityCount' );

/*==============================================================*/
/* Index: Indx_COLUMNTYPE_PK                                    */
/*==============================================================*/
create unique index Indx_COLUMNTYPE_PK on COLUMNTYPE (
   TYPENUM ASC
)
/


/*==============================================================*/
/* Table : CTIDatabase                                          */
/*==============================================================*/


create table CTIDatabase  (
   Version              VARCHAR2(6)                      not null,
   CTIEmployeeName      VARCHAR2(30)                     not null,
   DateApplied          DATE,
   Notes                VARCHAR2(300)
)
/


insert into CTIDatabase values('2.42', 'Ryan', '11-SEP-2003', 'Added some more roles, tag tables, new alarms, soe tables');

/*==============================================================*/
/* Index: Indx_CTIDATABASE_PK                                   */
/*==============================================================*/
create unique index Indx_CTIDATABASE_PK on CTIDatabase (
   Version ASC
)
/


/*==============================================================*/
/* Table : CalcPointBaseline                                    */
/*==============================================================*/


create table CalcPointBaseline  (
   PointID              NUMBER                           not null,
   BaselineID           NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_CLCPTB_PK                                        */
/*==============================================================*/
create unique index Indx_CLCPTB_PK on CalcPointBaseline (
   BaselineID ASC
)
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


/*==============================================================*/
/* Index: Indx_CAPCONTROLFEEDE_PK                               */
/*==============================================================*/
create unique index Indx_CAPCONTROLFEEDE_PK on CapControlFeeder (
   FeederID ASC
)
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


/*==============================================================*/
/* Index: Indx_CARRIERROUTE_PK                                  */
/*==============================================================*/
create unique index Indx_CARRIERROUTE_PK on CarrierRoute (
   ROUTEID ASC
)
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


/*==============================================================*/
/* Index: Indx_COMMERRORHISTOR_PK                               */
/*==============================================================*/
create unique index Indx_COMMERRORHISTOR_PK on CommErrorHistory (
   CommErrorID ASC
)
/


/*==============================================================*/
/* Table : CommPort                                             */
/*==============================================================*/


create table CommPort  (
   PORTID               NUMBER                           not null,
   ALARMINHIBIT         VARCHAR2(1)                      not null
         constraint SYS_C0013108 check ("ALARMINHIBIT" IS NOT NULL),
   COMMONPROTOCOL       VARCHAR2(8)                      not null
         constraint SYS_C0013109 check ("COMMONPROTOCOL" IS NOT NULL),
   PERFORMTHRESHOLD     NUMBER                           not null
         constraint SYS_C0013110 check ("PERFORMTHRESHOLD" IS NOT NULL),
   PERFORMANCEALARM     VARCHAR2(1)                      not null
         constraint SYS_C0013111 check ("PERFORMANCEALARM" IS NOT NULL),
   SharedPortType       VARCHAR2(20)                     not null,
   SharedSocketNumber   NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_COMMPORT_PK                                      */
/*==============================================================*/
create unique index Indx_COMMPORT_PK on CommPort (
   PORTID ASC
)
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

/*==============================================================*/
/* Index: Indx_CUSTOMERCONTACT_PK                               */
/*==============================================================*/
create unique index Indx_CUSTOMERCONTACT_PK on Contact (
   ContactID ASC
)
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


/*==============================================================*/
/* Index: Indx_CNTNOTIF_PK                                      */
/*==============================================================*/
create unique index Indx_CNTNOTIF_PK on ContactNotification (
   ContactNotifID ASC
)
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


/*==============================================================*/
/* Index: Indx_CSTMR_PK                                         */
/*==============================================================*/
create unique index Indx_CSTMR_PK on Customer (
   CustomerID ASC
)
/


/*==============================================================*/
/* Table : CustomerAdditionalContact                            */
/*==============================================================*/


create table CustomerAdditionalContact  (
   CustomerID           NUMBER                           not null,
   ContactID            NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_CUSTADDCNT_PK                                    */
/*==============================================================*/
create unique index Indx_CUSTADDCNT_PK on CustomerAdditionalContact (
   ContactID ASC,
   CustomerID ASC
)
/


/*==============================================================*/
/* Table : CustomerBaseLinePoint                                */
/*==============================================================*/


create table CustomerBaseLinePoint  (
   CustomerID           NUMBER                           not null,
   PointID              NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_CUSTOMERBASELIN_PK                               */
/*==============================================================*/
create unique index Indx_CUSTOMERBASELIN_PK on CustomerBaseLinePoint (
   CustomerID ASC,
   PointID ASC
)
/


/*==============================================================*/
/* Table : CustomerLoginSerialGroup                             */
/*==============================================================*/


create table CustomerLoginSerialGroup  (
   LoginID              NUMBER                           not null,
   LMGroupID            NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_CUSTOMERLOGINSE_PK                               */
/*==============================================================*/
create unique index Indx_CUSTOMERLOGINSE_PK on CustomerLoginSerialGroup (
   LoginID ASC,
   LMGroupID ASC
)
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

/*==============================================================*/
/* Index: Indx_DEVICE_PK                                        */
/*==============================================================*/
create unique index Indx_DEVICE_PK on DEVICE (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DEVICE2WAYFLAGS                                      */
/*==============================================================*/


create table DEVICE2WAYFLAGS  (
   DEVICEID             NUMBER                           not null,
   MONTHLYSTATS         VARCHAR2(1)                      not null
         constraint SYS_C0013200 check ("MONTHLYSTATS" IS NOT NULL),
   TWENTYFOURHOURSTATS  VARCHAR2(1)                      not null
         constraint SYS_C0013201 check ("TWENTYFOURHOURSTATS" IS NOT NULL),
   HOURLYSTATS          VARCHAR2(1)                      not null
         constraint SYS_C0013202 check ("HOURLYSTATS" IS NOT NULL),
   FAILUREALARM         VARCHAR2(1)                      not null
         constraint SYS_C0013203 check ("FAILUREALARM" IS NOT NULL),
   PERFORMANCETHRESHOLD NUMBER                           not null
         constraint SYS_C0013204 check ("PERFORMANCETHRESHOLD" IS NOT NULL),
   PERFORMANCEALARM     VARCHAR2(1)                      not null
         constraint SYS_C0013205 check ("PERFORMANCEALARM" IS NOT NULL),
   PERFORMANCETWENTYFOURALARM VARCHAR2(1)                      not null
         constraint SYS_C0013206 check ("PERFORMANCETWENTYFOURALARM" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_DEVICE2WAYFLAGS_PK                               */
/*==============================================================*/
create unique index Indx_DEVICE2WAYFLAGS_PK on DEVICE2WAYFLAGS (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DEVICECARRIERSETTINGS                                */
/*==============================================================*/


create table DEVICECARRIERSETTINGS  (
   DEVICEID             NUMBER                           not null,
   ADDRESS              NUMBER                           not null
         constraint SYS_C0013215 check ("ADDRESS" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_DEVICECARRIERSE_PK                               */
/*==============================================================*/
create unique index Indx_DEVICECARRIERSE_PK on DEVICECARRIERSETTINGS (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DEVICEDIALUPSETTINGS                                 */
/*==============================================================*/


create table DEVICEDIALUPSETTINGS  (
   DEVICEID             NUMBER                           not null,
   PHONENUMBER          VARCHAR2(40)                     not null
         constraint SYS_C0013189 check ("PHONENUMBER" IS NOT NULL),
   MINCONNECTTIME       NUMBER                           not null
         constraint SYS_C0013190 check ("MINCONNECTTIME" IS NOT NULL),
   MAXCONNECTTIME       NUMBER                           not null
         constraint SYS_C0013191 check ("MAXCONNECTTIME" IS NOT NULL),
   LINESETTINGS         VARCHAR2(8)                      not null
         constraint SYS_C0013192 check ("LINESETTINGS" IS NOT NULL),
   BaudRate             NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICEDIALUPSET_PK                               */
/*==============================================================*/
create unique index Indx_DEVICEDIALUPSET_PK on DEVICEDIALUPSETTINGS (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DEVICEIDLCREMOTE                                     */
/*==============================================================*/


create table DEVICEIDLCREMOTE  (
   DEVICEID             NUMBER                           not null,
   ADDRESS              NUMBER                           not null
         constraint SYS_C0013239 check ("ADDRESS" IS NOT NULL),
   POSTCOMMWAIT         NUMBER                           not null
         constraint SYS_C0013240 check ("POSTCOMMWAIT" IS NOT NULL),
   CCUAmpUseType        VARCHAR2(20)                     not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICEIDLCREMOT_PK                               */
/*==============================================================*/
create unique index Indx_DEVICEIDLCREMOT_PK on DEVICEIDLCREMOTE (
   DEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_DEVICEIED_PK                                     */
/*==============================================================*/
create unique index Indx_DEVICEIED_PK on DEVICEIED (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DEVICELOADPROFILE                                    */
/*==============================================================*/


create table DEVICELOADPROFILE  (
   DEVICEID             NUMBER                           not null,
   LASTINTERVALDEMANDRATE NUMBER                           not null,
   LOADPROFILEDEMANDRATE NUMBER                           not null,
   LOADPROFILECOLLECTION VARCHAR2(4)                      not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICELOADPROFI_PK                               */
/*==============================================================*/
create unique index Indx_DEVICELOADPROFI_PK on DEVICELOADPROFILE (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DEVICEMCTIEDPORT                                     */
/*==============================================================*/


create table DEVICEMCTIEDPORT  (
   DEVICEID             NUMBER                           not null,
   CONNECTEDIED         VARCHAR2(20)                     not null
         constraint SYS_C0013247 check ("CONNECTEDIED" IS NOT NULL),
   IEDSCANRATE          NUMBER                           not null
         constraint SYS_C0013248 check ("IEDSCANRATE" IS NOT NULL),
   DEFAULTDATACLASS     NUMBER                           not null
         constraint SYS_C0013249 check ("DEFAULTDATACLASS" IS NOT NULL),
   DEFAULTDATAOFFSET    NUMBER                           not null
         constraint SYS_C0013250 check ("DEFAULTDATAOFFSET" IS NOT NULL),
   PASSWORD             VARCHAR2(6)                      not null
         constraint SYS_C0013251 check ("PASSWORD" IS NOT NULL),
   REALTIMESCAN         VARCHAR2(1)                      not null
         constraint SYS_C0013252 check ("REALTIMESCAN" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_DEVICEMCTIEDPOR_PK                               */
/*==============================================================*/
create unique index Indx_DEVICEMCTIEDPOR_PK on DEVICEMCTIEDPORT (
   DEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_DEVICEMETERGROU_PK                               */
/*==============================================================*/
create unique index Indx_DEVICEMETERGROU_PK on DEVICEMETERGROUP (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DEVICESCANRATE                                       */
/*==============================================================*/


create table DEVICESCANRATE  (
   DEVICEID             NUMBER                           not null,
   SCANTYPE             VARCHAR2(20)                     not null
         constraint SYS_C0013195 check ("SCANTYPE" IS NOT NULL),
   INTERVALRATE         NUMBER                           not null
         constraint SYS_C0013196 check ("INTERVALRATE" IS NOT NULL),
   SCANGROUP            NUMBER                           not null
         constraint SYS_C0013197 check ("SCANGROUP" IS NOT NULL),
   AlternateRate        NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICESCANRATE_PK                                */
/*==============================================================*/
create unique index Indx_DEVICESCANRATE_PK on DEVICESCANRATE (
   DEVICEID ASC,
   SCANTYPE ASC
)
/


/*==============================================================*/
/* Table : DEVICETAPPAGINGSETTINGS                              */
/*==============================================================*/


create table DEVICETAPPAGINGSETTINGS  (
   DEVICEID             NUMBER                           not null,
   PAGERNUMBER          VARCHAR2(20)                     not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICETAPPAGING_PK                               */
/*==============================================================*/
create unique index Indx_DEVICETAPPAGING_PK on DEVICETAPPAGINGSETTINGS (
   DEVICEID ASC
)
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
insert into display values(99, 'Your Custom Display', 'Custom Displays', 'Edit This Display', 'This display is is used to show what a user created display looks like. You may edit this display to fit your own needs.');


/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique index Indx_DISPLAYNAME on DISPLAY (
   NAME ASC
)
/


/*==============================================================*/
/* Index: Indx_DISPLAY_PK                                       */
/*==============================================================*/
create unique index Indx_DISPLAY_PK on DISPLAY (
   DISPLAYNUM ASC
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


/*==============================================================*/
/* Index: Indx_DISPLAY2WAYDATA_PK                               */
/*==============================================================*/
create unique index Indx_DISPLAY2WAYDATA_PK on DISPLAY2WAYDATA (
   DISPLAYNUM ASC,
   ORDERING ASC
)
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
insert into displaycolumns values(1, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(1, 'User Name', 8, 6, 35 );

/*********************************************************************************************************
This use to be for the Historical Data View and RawPointHistory view,  they have been deleted and no longer used as of 7-11-2001
*********************************************************************************************************
insert into displaycolumns values(2, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(2, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(2, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(2, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(2, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(2, 'User Name', 8, 6, 35 );
insert into displaycolumns values(3, 'Time Stamp', 11, 1, 70 );
insert into displaycolumns values(3, 'Device Name', 5, 2, 60 );
insert into displaycolumns values(3, 'Point Name', 2, 3, 60 );
insert into displaycolumns values(3, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(3, 'Additional Info', 10, 5, 200 );
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

/*==============================================================*/
/* Index: Indx_DISPLAYCOLUMNS_PK                                */
/*==============================================================*/
create unique index Indx_DISPLAYCOLUMNS_PK on DISPLAYCOLUMNS (
   DISPLAYNUM ASC,
   TITLE ASC
)
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


/*==============================================================*/
/* Index: Indx_DYNAMICACCUMULA_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICACCUMULA_PK on DYNAMICACCUMULATOR (
   POINTID ASC
)
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


/*==============================================================*/
/* Index: Indx_DYNAMICDEVICESC_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICDEVICESC_PK on DYNAMICDEVICESCANDATA (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DYNAMICPOINTDISPATCH                                 */
/*==============================================================*/


create table DYNAMICPOINTDISPATCH  (
   POINTID              NUMBER                           not null,
   TIMESTAMP            DATE                             not null
         constraint SYS_C0013325 check ("TIMESTAMP" IS NOT NULL),
   QUALITY              NUMBER                           not null
         constraint SYS_C0013326 check ("QUALITY" IS NOT NULL),
   VALUE                FLOAT                            not null
         constraint SYS_C0013327 check ("VALUE" IS NOT NULL),
   TAGS                 NUMBER                           not null
         constraint SYS_C0013328 check ("TAGS" IS NOT NULL),
   NEXTARCHIVE          DATE                             not null
         constraint SYS_C0013329 check ("NEXTARCHIVE" IS NOT NULL),
   STALECOUNT           NUMBER                           not null
         constraint SYS_C0013330 check ("STALECOUNT" IS NOT NULL),
   LastAlarmLogID       NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DYNAMICPOINTDIS_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICPOINTDIS_PK on DYNAMICPOINTDISPATCH (
   POINTID ASC
)
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


/*==============================================================*/
/* Index: Indx_DATEOFHOLIDAY_PK                                 */
/*==============================================================*/
create unique index Indx_DATEOFHOLIDAY_PK on DateOfHoliday (
   HolidayScheduleID ASC,
   HolidayName ASC
)
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


/*==============================================================*/
/* Index: Indx_DEVICECBC_PK                                     */
/*==============================================================*/
create unique index Indx_DEVICECBC_PK on DeviceCBC (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DeviceCustomerList                                   */
/*==============================================================*/


create table DeviceCustomerList  (
   CustomerID           NUMBER                           not null,
   DeviceID             NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DEVCSTLST_PK                                     */
/*==============================================================*/
create unique index Indx_DEVCSTLST_PK on DeviceCustomerList (
   DeviceID ASC,
   CustomerID ASC
)
/


/*==============================================================*/
/* Table : DeviceDNP                                            */
/*==============================================================*/


create table DeviceDNP  (
   DeviceID             NUMBER                           not null,
   MasterAddress        NUMBER                           not null,
   SlaveAddress         NUMBER                           not null,
   PostCommWait         NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICEDNP_PK                                     */
/*==============================================================*/
create unique index Indx_DEVICEDNP_PK on DeviceDNP (
   DeviceID ASC
)
/


/*==============================================================*/
/* Table : DeviceDirectCommSettings                             */
/*==============================================================*/


create table DeviceDirectCommSettings  (
   DEVICEID             NUMBER                           not null,
   PORTID               NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICEDIRECTCOM_PK                               */
/*==============================================================*/
create unique index Indx_DEVICEDIRECTCOM_PK on DeviceDirectCommSettings (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : DeviceRoutes                                         */
/*==============================================================*/


create table DeviceRoutes  (
   DEVICEID             NUMBER                           not null,
   ROUTEID              NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DEVICEROUTES_PK                                  */
/*==============================================================*/
create unique index Indx_DEVICEROUTES_PK on DeviceRoutes (
   DEVICEID ASC,
   ROUTEID ASC
)
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


/*==============================================================*/
/* Index: Indx_DEVICEWINDOW_PK                                  */
/*==============================================================*/
create unique index Indx_DEVICEWINDOW_PK on DeviceWindow (
   DeviceID ASC,
   Type ASC
)
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


/*==============================================================*/
/* Index: Indx_DYNAMICCCCAPBAN_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICCCCAPBAN_PK on DynamicCCCapBank (
   CapBankID ASC
)
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
   CurrentVarPointQuality NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DYNAMICCCFEEDER_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICCCFEEDER_PK on DynamicCCFeeder (
   FeederID ASC
)
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
   CurrentVarPointQuality NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_DYNAMICCCSUBSTA_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICCCSUBSTA_PK on DynamicCCSubstationBus (
   SubstationBusID ASC
)
/


/*==============================================================*/
/* Table : DynamicCalcHistorical                                */
/*==============================================================*/


create table DynamicCalcHistorical  (
   PointID              NUMBER                           not null,
   LastUpdate           DATE                             not null
)
/


/*==============================================================*/
/* Index: Indx_DYNAMICCALCHIST_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICCALCHIST_PK on DynamicCalcHistorical (
   PointID ASC
)
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


/*==============================================================*/
/* Index: Indx_DYNAMICLMCONTRO_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICLMCONTRO_PK on DynamicLMControlArea (
   DeviceID ASC
)
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


/*==============================================================*/
/* Index: Indx_DYNAMICLMCONTRO_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICLMCONTRO_PK on DynamicLMControlAreaTrigger (
   DeviceID ASC,
   TriggerNumber ASC
)
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
   ControlCompleteTime  DATE                             not null
)
/


/*==============================================================*/
/* Index: Indx_DYNAMICLMGROUP_PK                                */
/*==============================================================*/
create unique index Indx_DYNAMICLMGROUP_PK on DynamicLMGroup (
   DeviceID ASC,
   LMProgramID ASC
)
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


/*==============================================================*/
/* Index: Indx_DYNAMICLMPROGRA_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICLMPROGRA_PK on DynamicLMProgram (
   DeviceID ASC
)
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
   TimeStamp            DATE                             not null
)
/


/*==============================================================*/
/* Index: Indx_DYNAMICLMPROGRA_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICLMPROGRA_PK on DynamicLMProgramDirect (
   DeviceID ASC
)
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


/*==============================================================*/
/* Index: Indx_DYNAMICPAOSTATI_PK                               */
/*==============================================================*/
create unique index Indx_DYNAMICPAOSTATI_PK on DynamicPAOStatistics (
   PAOBjectID ASC,
   StatisticType ASC
)
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


/*==============================================================*/
/* Index: Indx_DynPtAlarm_PK                                    */
/*==============================================================*/
create unique index Indx_DynPtAlarm_PK on DynamicPointAlarming (
   PointID ASC,
   AlarmCondition ASC
)
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


/*==============================================================*/
/* Index: Indx_DynTgs_PK                                        */
/*==============================================================*/
create unique index Indx_DynTgs_PK on DynamicTags (
   InstanceID ASC
)
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


/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique index Indx_EnCmpName on EnergyCompany (
   Name ASC
)
/


/*==============================================================*/
/* Index: Indx_ENERGYCOMPANY_PK                                 */
/*==============================================================*/
create unique index Indx_ENERGYCOMPANY_PK on EnergyCompany (
   EnergyCompanyID ASC
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


/*==============================================================*/
/* Table : EnergyCompanyOperatorLoginList                       */
/*==============================================================*/


create table EnergyCompanyOperatorLoginList  (
   EnergyCompanyID      NUMBER                           not null,
   OperatorLoginID      NUMBER                           not null
)
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
insert into FDRInterface values (14, 'LODESTAR', 'Receive', 'f' );


/*==============================================================*/
/* Index: Indx_FDRINTERFACE_PK                                  */
/*==============================================================*/
create unique index Indx_FDRINTERFACE_PK on FDRInterface (
   InterfaceID ASC
)
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
insert into FDRInterfaceOption values(2, 'Category', 1, 'Combo', 'PSEUDO,REAL' );
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
insert into FDRInterfaceOption values(11, 'Group', 2, 'Query', 'select GroupName from FDRTelegyrGroup' );
insert into FDRInterfaceOption values(12,'Point ID',1,'Text','(none)');
insert into FDRInterfaceOption values(13,'Point ID',1,'Text','(none)');
insert into fdrinterfaceoption values(14,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values(14,'Channel',2,'Text','(none)');


/*==============================================================*/
/* Index: Indx_FDRINTERFACEOPT_PK                               */
/*==============================================================*/
create unique index Indx_FDRINTERFACEOPT_PK on FDRInterfaceOption (
   InterfaceID ASC,
   Ordering ASC
)
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
/* Table : FDRTelegyrGroup                                      */
/*==============================================================*/


create table FDRTelegyrGroup  (
   GroupID              NUMBER                           not null,
   GroupName            VARCHAR2(40)                     not null,
   CollectionInterval   NUMBER                           not null,
   GroupType            VARCHAR2(20)                     not null
)
/


/*==============================================================*/
/* Index: Indx_FDRTELEGYRGROUP_PK                               */
/*==============================================================*/
create unique index Indx_FDRTELEGYRGROUP_PK on FDRTelegyrGroup (
   GroupID ASC
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
   Multiplier           FLOAT                            not null
)
/


/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create index Indx_GrpDSerPtID on GRAPHDATASERIES (
   POINTID ASC
)
/


/*==============================================================*/
/* Index: Indx_GRAPHDATASERIES_PK                               */
/*==============================================================*/
create unique index Indx_GRAPHDATASERIES_PK on GRAPHDATASERIES (
   GRAPHDATASERIESID ASC
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
   add constraint AK_GRNMUQ_GRAPHDEF unique (NAME)
/


/*==============================================================*/
/* Index: Indx_GRAPHDEFINITION_PK                               */
/*==============================================================*/
create unique index Indx_GRAPHDEFINITION_PK on GRAPHDEFINITION (
   GRAPHDEFINITIONID ASC
)
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


/*==============================================================*/
/* Index: Indx_GENERICMACRO_PK                                  */
/*==============================================================*/
create unique index Indx_GENERICMACRO_PK on GenericMacro (
   OwnerID ASC,
   ChildOrder ASC,
   MacroType ASC
)
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


/*==============================================================*/
/* Index: Indx_GRAPHCUSTOMERLI_PK                               */
/*==============================================================*/
create unique index Indx_GRAPHCUSTOMERLI_PK on GraphCustomerList (
   GraphDefinitionID ASC,
   CustomerID ASC
)
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

/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique index Indx_HolSchName on HolidaySchedule (
   HolidayScheduleName ASC
)
/


/*==============================================================*/
/* Index: Indx_HOLIDAYSCHEDULE_PK                               */
/*==============================================================*/
create unique index Indx_HOLIDAYSCHEDULE_PK on HolidaySchedule (
   HolidayScheduleID ASC
)
/


/*==============================================================*/
/* Table : LMCONTROLAREAPROGRAM                                 */
/*==============================================================*/


create table LMCONTROLAREAPROGRAM  (
   DEVICEID             NUMBER                           not null,
   LMPROGRAMDEVICEID    NUMBER                           not null,
   USERORDER            NUMBER                           not null,
   STOPORDER            NUMBER                           not null,
   DEFAULTPRIORITY      NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_LMCONTROLAREAPR_PK                               */
/*==============================================================*/
create unique index Indx_LMCONTROLAREAPR_PK on LMCONTROLAREAPROGRAM (
   DEVICEID ASC,
   LMPROGRAMDEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMCONTROLAREATR_PK                               */
/*==============================================================*/
create unique index Indx_LMCONTROLAREATR_PK on LMCONTROLAREATRIGGER (
   DEVICEID ASC,
   TRIGGERNUMBER ASC
)
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


/*==============================================================*/
/* Index: Indx_LMCONTROLAREA_PK                                 */
/*==============================================================*/
create unique index Indx_LMCONTROLAREA_PK on LMControlArea (
   DEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create index Indx_Start on LMControlHistory (
   StartDateTime ASC
)
/


/*==============================================================*/
/* Index: Indx_LMCONTROLHISTOR_PK                               */
/*==============================================================*/
create unique index Indx_LMCONTROLHISTOR_PK on LMControlHistory (
   LMCtrlHistID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
   CurtailmentStartTime ASC
)
/


/*==============================================================*/
/* Index: Indx_LMCURTAILPROGRA_PK                               */
/*==============================================================*/
create unique index Indx_LMCURTAILPROGRA_PK on LMCurtailProgramActivity (
   CurtailReferenceID ASC
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


/*==============================================================*/
/* Index: Indx_LMDIRECTCUSTOME_PK                               */
/*==============================================================*/
create unique index Indx_LMDIRECTCUSTOME_PK on LMDirectCustomerList (
   ProgramID ASC,
   CustomerID ASC
)
/


/*==============================================================*/
/* Table : LMDirectOperatorList                                 */
/*==============================================================*/


create table LMDirectOperatorList  (
   ProgramID            NUMBER                           not null,
   OperatorLoginID      NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_LMDIRECTOPERATO_PK                               */
/*==============================================================*/
create unique index Indx_LMDIRECTOPERATO_PK on LMDirectOperatorList (
   ProgramID ASC,
   OperatorLoginID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMENERGYEXCHANG_PK                               */
/*==============================================================*/
create unique index Indx_LMENERGYEXCHANG_PK on LMEnergyExchangeCustomerList (
   ProgramID ASC,
   CustomerID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMENERGYEXCHANG_PK                               */
/*==============================================================*/
create unique index Indx_LMENERGYEXCHANG_PK on LMEnergyExchangeCustomerReply (
   CustomerID ASC,
   OfferID ASC,
   RevisionNumber ASC
)
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


/*==============================================================*/
/* Index: Indx_LMENERGYEXCHANG_PK                               */
/*==============================================================*/
create unique index Indx_LMENERGYEXCHANG_PK on LMEnergyExchangeHourlyCustomer (
   CustomerID ASC,
   OfferID ASC,
   RevisionNumber ASC,
   Hour ASC
)
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


/*==============================================================*/
/* Index: Indx_LMENERGYEXCHANG_PK                               */
/*==============================================================*/
create unique index Indx_LMENERGYEXCHANG_PK on LMEnergyExchangeHourlyOffer (
   OfferID ASC,
   RevisionNumber ASC,
   Hour ASC
)
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


/*==============================================================*/
/* Index: Indx_LMENERGYEXCHANG_PK                               */
/*==============================================================*/
create unique index Indx_LMENERGYEXCHANG_PK on LMEnergyExchangeOfferRevision (
   OfferID ASC,
   RevisionNumber ASC
)
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


/*==============================================================*/
/* Index: Indx_LMENERGYEXCHANG_PK                               */
/*==============================================================*/
create unique index Indx_LMENERGYEXCHANG_PK on LMEnergyExchangeProgramOffer (
   OfferID ASC
)
/


/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/


create table LMGroup  (
   DeviceID             NUMBER                           not null,
   KWCapacity           FLOAT                            not null
)
/


/*==============================================================*/
/* Index: Indx_LMGROUP_PK                                       */
/*==============================================================*/
create unique index Indx_LMGROUP_PK on LMGroup (
   DeviceID ASC
)
/


/*==============================================================*/
/* Table : LMGroupEmetcon                                       */
/*==============================================================*/


create table LMGroupEmetcon  (
   DEVICEID             NUMBER                           not null,
   GOLDADDRESS          NUMBER                           not null
         constraint SYS_C13351 check ("GOLDADDRESS" IS NOT NULL),
   SILVERADDRESS        NUMBER                           not null
         constraint SYS_C13352 check ("SILVERADDRESS" IS NOT NULL),
   ADDRESSUSAGE         CHAR(1)                          not null
         constraint SYS_C0013353 check ("ADDRESSUSAGE" IS NOT NULL),
   RELAYUSAGE           CHAR(1)                          not null
         constraint SYS_C0013354 check ("RELAYUSAGE" IS NOT NULL),
   ROUTEID              NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_LMGROUPEMETCON_PK                                */
/*==============================================================*/
create unique index Indx_LMGROUPEMETCON_PK on LMGroupEmetcon (
   DEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMGROUPEXPRESSC_PK                               */
/*==============================================================*/
create unique index Indx_LMGROUPEXPRESSC_PK on LMGroupExpressCom (
   LMGroupID ASC
)
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

/*==============================================================*/
/* Index: Indx_LMGROUPEXPRESSC_PK                               */
/*==============================================================*/
create unique index Indx_LMGROUPEXPRESSC_PK on LMGroupExpressComAddress (
   AddressID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMGrpMCT_PK                                      */
/*==============================================================*/
create unique index Indx_LMGrpMCT_PK on LMGroupMCT (
   DeviceID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMGROUPPOINT_PK                                  */
/*==============================================================*/
create unique index Indx_LMGROUPPOINT_PK on LMGroupPoint (
   DEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMGROUPRIPPLE_PK                                 */
/*==============================================================*/
create unique index Indx_LMGROUPRIPPLE_PK on LMGroupRipple (
   DeviceID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMGROUPVERSACOM_PK                               */
/*==============================================================*/
create unique index Indx_LMGROUPVERSACOM_PK on LMGroupVersacom (
   DEVICEID ASC
)
/


/*==============================================================*/
/* Table : LMMACSScheduleOperatorList                           */
/*==============================================================*/


create table LMMACSScheduleOperatorList  (
   ScheduleID           NUMBER                           not null,
   OperatorLoginID      NUMBER                           not null
)
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


/*==============================================================*/
/* Index: Indx_LMMACSSCHEDULEC_PK                               */
/*==============================================================*/
create unique index Indx_LMMACSSCHEDULEC_PK on LMMacsScheduleCustomerList (
   ScheduleID ASC,
   LMCustomerDeviceID ASC
)
/


/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/


create table LMPROGRAM  (
   DEVICEID             NUMBER                           not null,
   CONTROLTYPE          VARCHAR2(20)                     not null,
   AVAILABLESEASONS     VARCHAR2(4)                      not null,
   AvailableWeekDays    VARCHAR2(8)                      not null,
   MAXHOURSDAILY        NUMBER                           not null,
   MAXHOURSMONTHLY      NUMBER                           not null,
   MAXHOURSSEASONAL     NUMBER                           not null,
   MAXHOURSANNUALLY     NUMBER                           not null,
   MINACTIVATETIME      NUMBER                           not null,
   MINRESTARTTIME       NUMBER                           not null,
   HolidayScheduleID    NUMBER                           not null,
   SeasonScheduleID     NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_LMPROGRAM_PK                                     */
/*==============================================================*/
create unique index Indx_LMPROGRAM_PK on LMPROGRAM (
   DEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMPROGRAMCONTRO_PK                               */
/*==============================================================*/
create unique index Indx_LMPROGRAMCONTRO_PK on LMProgramControlWindow (
   DeviceID ASC,
   WindowNumber ASC
)
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


/*==============================================================*/
/* Index: Indx_LMPROGRAMCURTAI_PK                               */
/*==============================================================*/
create unique index Indx_LMPROGRAMCURTAI_PK on LMProgramCurtailCustomerList (
   CustomerID ASC,
   ProgramID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMPROGRAMCURTAI_PK                               */
/*==============================================================*/
create unique index Indx_LMPROGRAMCURTAI_PK on LMProgramCurtailment (
   DeviceID ASC
)
/


/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/


create table LMProgramDirect  (
   DeviceID             NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_LMPROGRAMDIRECT_PK                               */
/*==============================================================*/
create unique index Indx_LMPROGRAMDIRECT_PK on LMProgramDirect (
   DeviceID ASC
)
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
   GearID               NUMBER                           not null
)
/


alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique (DeviceID, GearNumber)
/


/*==============================================================*/
/* Index: Indx_LMPROGRAMDIRECT_PK                               */
/*==============================================================*/
create unique index Indx_LMPROGRAMDIRECT_PK on LMProgramDirectGear (
   GearID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMPROGRAMDIRECT_PK                               */
/*==============================================================*/
create unique index Indx_LMPROGRAMDIRECT_PK on LMProgramDirectGroup (
   DeviceID ASC,
   GroupOrder ASC
)
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


/*==============================================================*/
/* Index: Indx_LMPROGRAMENERGY_PK                               */
/*==============================================================*/
create unique index Indx_LMPROGRAMENERGY_PK on LMProgramEnergyExchange (
   DeviceID ASC
)
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


/*==============================================================*/
/* Index: Indx_LMTHERMOSTATGEA_PK                               */
/*==============================================================*/
create unique index Indx_LMTHERMOSTATGEA_PK on LMThermoStatGear (
   GearID ASC
)
/


/*==============================================================*/
/* Table : LOGIC                                                */
/*==============================================================*/


create table LOGIC  (
   LOGICID              NUMBER                           not null,
   LOGICNAME            VARCHAR2(20)                     not null
         constraint SYS_C0013441 check ("LOGICNAME" IS NOT NULL),
   PERIODICRATE         NUMBER                           not null
         constraint SYS_C0013442 check ("PERIODICRATE" IS NOT NULL),
   STATEFLAG            VARCHAR2(10)                     not null
         constraint SYS_C0013443 check ("STATEFLAG" IS NOT NULL),
   SCRIPTNAME           VARCHAR2(20)                     not null
         constraint SYS_C0013444 check ("SCRIPTNAME" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_LOGIC_PK                                         */
/*==============================================================*/
create unique index Indx_LOGIC_PK on LOGIC (
   LOGICID ASC
)
/


/*==============================================================*/
/* Table : MACROROUTE                                           */
/*==============================================================*/


create table MACROROUTE  (
   ROUTEID              NUMBER                           not null,
   SINGLEROUTEID        NUMBER                           not null,
   ROUTEORDER           NUMBER                           not null
         constraint SYS_C0013273 check ("ROUTEORDER" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_MACROROUTE_PK                                    */
/*==============================================================*/
create unique index Indx_MACROROUTE_PK on MACROROUTE (
   ROUTEID ASC,
   ROUTEORDER ASC
)
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


/*==============================================================*/
/* Index: Indx_MACSCHEDULE_PK                                   */
/*==============================================================*/
create unique index Indx_MACSCHEDULE_PK on MACSchedule (
   ScheduleID ASC
)
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


/*==============================================================*/
/* Index: Indx_MACSIMPLESCHEDU_PK                               */
/*==============================================================*/
create unique index Indx_MACSIMPLESCHEDU_PK on MACSimpleSchedule (
   ScheduleID ASC
)
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


/*==============================================================*/
/* Index: Indx_MCTBROADCASTMAP_PK                               */
/*==============================================================*/
create unique index Indx_MCTBROADCASTMAP_PK on MCTBroadCastMapping (
   MCTBroadCastID ASC,
   MctID ASC
)
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


/*==============================================================*/
/* Index: Indx_NOTIFICATIONDES_PK                               */
/*==============================================================*/
create unique index Indx_NOTIFICATIONDES_PK on NotificationDestination (
   NotificationGroupID ASC,
   DestinationOrder ASC
)
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

/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique index Indx_NOTIFGRPNme on NotificationGroup (
   GroupName ASC
)
/


/*==============================================================*/
/* Index: Indx_NOTIFICATIONGRO_PK                               */
/*==============================================================*/
create unique index Indx_NOTIFICATIONGRO_PK on NotificationGroup (
   NotificationGroupID ASC
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


/*==============================================================*/
/* Table : OperatorSerialGroup                                  */
/*==============================================================*/


create table OperatorSerialGroup  (
   LoginID              NUMBER                           not null,
   LMGroupID            NUMBER                           not null
)
/


/*==============================================================*/
/* Index: Indx_OPERATORSERIALG_PK                               */
/*==============================================================*/
create unique index Indx_OPERATORSERIALG_PK on OperatorSerialGroup (
   LoginID ASC
)
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
   FuncRequeue          NUMBER                           not null
)
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


/*==============================================================*/
/* Index: Indx_PAOOWNER_PK                                      */
/*==============================================================*/
create unique index Indx_PAOOWNER_PK on PAOowner (
   OwnerID ASC,
   ChildID ASC
)
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
/* Index: Indx_POINT_PK                                         */
/*==============================================================*/
create unique index Indx_POINT_PK on POINT (
   POINTID ASC
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


/*==============================================================*/
/* Index: Indx_POINTACCUMULATO_PK                               */
/*==============================================================*/
create unique index Indx_POINTACCUMULATO_PK on POINTACCUMULATOR (
   POINTID ASC
)
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


/*==============================================================*/
/* Index: Indx_POINTANALOG_PK                                   */
/*==============================================================*/
create unique index Indx_POINTANALOG_PK on POINTANALOG (
   POINTID ASC
)
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


/*==============================================================*/
/* Index: Indx_POINTLIMITS_PK                                   */
/*==============================================================*/
create unique index Indx_POINTLIMITS_PK on POINTLIMITS (
   POINTID ASC,
   LIMITNUMBER ASC
)
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


/*==============================================================*/
/* Index: Indx_POINTSTATUS_PK                                   */
/*==============================================================*/
create unique index Indx_POINTSTATUS_PK on POINTSTATUS (
   POINTID ASC
)
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


/*==============================================================*/
/* Index: Indx_POINTUNIT_PK                                     */
/*==============================================================*/
create unique index Indx_POINTUNIT_PK on POINTUNIT (
   POINTID ASC
)
/


/*==============================================================*/
/* Table : PORTDIALUPMODEM                                      */
/*==============================================================*/


create table PORTDIALUPMODEM  (
   PORTID               NUMBER                           not null,
   MODEMTYPE            VARCHAR2(30)                     not null
         constraint SYS_C13171 check ("MODEMTYPE" IS NOT NULL),
   INITIALIZATIONSTRING VARCHAR2(50)                     not null
         constraint SYS_C13172 check ("INITIALIZATIONSTRING" IS NOT NULL),
   PREFIXNUMBER         VARCHAR2(10)                     not null
         constraint SYS_C0013173 check ("PREFIXNUMBER" IS NOT NULL),
   SUFFIXNUMBER         VARCHAR2(10)                     not null
         constraint SYS_C0013174 check ("SUFFIXNUMBER" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_PORTDIALUPMODEM_PK                               */
/*==============================================================*/
create unique index Indx_PORTDIALUPMODEM_PK on PORTDIALUPMODEM (
   PORTID ASC
)
/


/*==============================================================*/
/* Table : PORTLOCALSERIAL                                      */
/*==============================================================*/


create table PORTLOCALSERIAL  (
   PORTID               NUMBER                           not null,
   PHYSICALPORT         VARCHAR2(8)                      not null
         constraint SYS_C0013146 check ("PHYSICALPORT" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_PORTLOCALSERIAL_PK                               */
/*==============================================================*/
create unique index Indx_PORTLOCALSERIAL_PK on PORTLOCALSERIAL (
   PORTID ASC
)
/


/*==============================================================*/
/* Table : PORTRADIOSETTINGS                                    */
/*==============================================================*/


create table PORTRADIOSETTINGS  (
   PORTID               NUMBER                           not null,
   RTSTOTXWAITSAMED     NUMBER                           not null
         constraint SYS_C0013165 check ("RTSTOTXWAITSAMED" IS NOT NULL),
   RTSTOTXWAITDIFFD     NUMBER                           not null
         constraint SYS_C0013166 check ("RTSTOTXWAITDIFFD" IS NOT NULL),
   RADIOMASTERTAIL      NUMBER                           not null
         constraint SYS_C0013167 check ("RADIOMASTERTAIL" IS NOT NULL),
   REVERSERTS           NUMBER                           not null
         constraint SYS_C0013168 check ("REVERSERTS" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_PORTRADIOSETTIN_PK                               */
/*==============================================================*/
create unique index Indx_PORTRADIOSETTIN_PK on PORTRADIOSETTINGS (
   PORTID ASC
)
/


/*==============================================================*/
/* Table : PORTSETTINGS                                         */
/*==============================================================*/


create table PORTSETTINGS  (
   PORTID               NUMBER                           not null,
   BAUDRATE             NUMBER                           not null
         constraint SYS_C0013153 check ("BAUDRATE" IS NOT NULL),
   CDWAIT               NUMBER                           not null
         constraint SYS_C0013154 check ("CDWAIT" IS NOT NULL),
   LINESETTINGS         VARCHAR2(8)                      not null
         constraint SYS_C0013155 check ("LINESETTINGS" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_PORTSETTINGS_PK                                  */
/*==============================================================*/
create unique index Indx_PORTSETTINGS_PK on PORTSETTINGS (
   PORTID ASC
)
/


/*==============================================================*/
/* Table : PORTTERMINALSERVER                                   */
/*==============================================================*/


create table PORTTERMINALSERVER  (
   PORTID               NUMBER                           not null,
   IPADDRESS            VARCHAR2(16)                     not null
         constraint SYS_C0013149 check ("IPADDRESS" IS NOT NULL),
   SOCKETPORTNUMBER     NUMBER                           not null
         constraint SYS_C0013150 check ("SOCKETPORTNUMBER" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_PORTTERMINALSER_PK                               */
/*==============================================================*/
create unique index Indx_PORTTERMINALSER_PK on PORTTERMINALSERVER (
   PORTID ASC
)
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

/*==============================================================*/
/* Index: Index_PtAlarmPK                                       */
/*==============================================================*/
create unique index Index_PtAlarmPK on PointAlarming (
   PointID ASC
)
/


/*==============================================================*/
/* Table : PortStatistics                                       */
/*==============================================================*/


create table PortStatistics  (
   PORTID               NUMBER                           not null,
   STATISTICTYPE        NUMBER                           not null
         constraint SYS_C0013177 check ("STATISTICTYPE" IS NOT NULL),
   ATTEMPTS             NUMBER                           not null
         constraint SYS_C0013178 check ("ATTEMPTS" IS NOT NULL),
   DATAERRORS           NUMBER                           not null
         constraint SYS_C0013179 check ("DATAERRORS" IS NOT NULL),
   SYSTEMERRORS         NUMBER                           not null
         constraint SYS_C0013180 check ("SYSTEMERRORS" IS NOT NULL),
   STARTDATETIME        DATE                             not null
         constraint SYS_C0013181 check ("STARTDATETIME" IS NOT NULL),
   STOPDATETIME         DATE                             not null
         constraint SYS_C0013182 check ("STOPDATETIME" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_PORTSTATISTICS_PK                                */
/*==============================================================*/
create unique index Indx_PORTSTATISTICS_PK on PortStatistics (
   PORTID ASC,
   STATISTICTYPE ASC
)
/


/*==============================================================*/
/* Table : PortTiming                                           */
/*==============================================================*/


create table PortTiming  (
   PORTID               NUMBER                           not null,
   PRETXWAIT            NUMBER                           not null
         constraint SYS_C0013158 check ("PRETXWAIT" IS NOT NULL),
   RTSTOTXWAIT          NUMBER                           not null
         constraint SYS_C0013159 check ("RTSTOTXWAIT" IS NOT NULL),
   POSTTXWAIT           NUMBER                           not null
         constraint SYS_C0013160 check ("POSTTXWAIT" IS NOT NULL),
   RECEIVEDATAWAIT      NUMBER                           not null
         constraint SYS_C0013161 check ("RECEIVEDATAWAIT" IS NOT NULL),
   EXTRATIMEOUT         NUMBER                           not null
         constraint SYS_C0013162 check ("EXTRATIMEOUT" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_PORTTIMING_PK                                    */
/*==============================================================*/
create unique index Indx_PORTTIMING_PK on PortTiming (
   PORTID ASC
)
/


/*==============================================================*/
/* Table : RAWPOINTHISTORY                                      */
/*==============================================================*/


create table RAWPOINTHISTORY  (
   CHANGEID             NUMBER                           not null,
   POINTID              NUMBER                           not null,
   TIMESTAMP            DATE                             not null,
   QUALITY              NUMBER                           not null,
   VALUE                FLOAT                            not null
)
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
/* Index: Indx_RAWPOINTHISTORY_PK                               */
/*==============================================================*/
create unique index Indx_RAWPOINTHISTORY_PK on RAWPOINTHISTORY (
   CHANGEID ASC
)
/


/*==============================================================*/
/* Table : RepeaterRoute                                        */
/*==============================================================*/


create table RepeaterRoute  (
   ROUTEID              NUMBER                           not null,
   DEVICEID             NUMBER                           not null,
   VARIABLEBITS         NUMBER                           not null
         constraint SYS_C0013267 check ("VARIABLEBITS" IS NOT NULL),
   REPEATERORDER        NUMBER                           not null
         constraint SYS_C0013268 check ("REPEATERORDER" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_REPEATERROUTE_PK                                 */
/*==============================================================*/
create unique index Indx_REPEATERROUTE_PK on RepeaterRoute (
   ROUTEID ASC,
   DEVICEID ASC
)
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


/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique index Indx_RouteDevID on Route (
   DeviceID DESC,
   RouteID ASC
)
/


/*==============================================================*/
/* Index: Indx_ROUTE_PK                                         */
/*==============================================================*/
create unique index Indx_ROUTE_PK on Route (
   RouteID ASC
)
/


/*==============================================================*/
/* Table : SOELog                                               */
/*==============================================================*/


create table SOELog  (
   LogID                NUMBER                           not null,
   PointID              NUMBER                           not null,
   SOEDateTime          DATE                             not null,
   Millis               NUMBER                           not null,
   Description          VARCHAR2(60)                     not null,
   AdditionalInfo       VARCHAR2(120)                    not null
)
/


/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create index Indx_SYSLG_PtId on SOELog (
   PointID ASC
)
/


/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create index Indx_SYSLG_Date on SOELog (
   SOEDateTime ASC
)
/


/*==============================================================*/
/* Index: Indx_SOELog_PK                                        */
/*==============================================================*/
create unique index Indx_SOELog_PK on SOELog (
   LogID ASC
)
/


/*==============================================================*/
/* Table : STATE                                                */
/*==============================================================*/


create table STATE  (
   STATEGROUPID         NUMBER                           not null,
   RAWSTATE             NUMBER                           not null
         constraint SYS_C0013338 check ("RAWSTATE" IS NOT NULL),
   TEXT                 VARCHAR2(20)                     not null
         constraint SYS_C0013339 check ("TEXT" IS NOT NULL),
   FOREGROUNDCOLOR      NUMBER                           not null
         constraint SYS_C0013340 check ("FOREGROUNDCOLOR" IS NOT NULL),
   BACKGROUNDCOLOR      NUMBER                           not null
         constraint SYS_C0013341 check ("BACKGROUNDCOLOR" IS NOT NULL),
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

/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create index Indx_StateRaw on STATE (
   RAWSTATE ASC
)
/


/*==============================================================*/
/* Index: Indx_STATE_PK                                         */
/*==============================================================*/
create unique index Indx_STATE_PK on STATE (
   STATEGROUPID ASC,
   RAWSTATE ASC
)
/


/*==============================================================*/
/* Table : STATEGROUP                                           */
/*==============================================================*/


create table STATEGROUP  (
   STATEGROUPID         NUMBER                           not null,
   NAME                 VARCHAR2(20)                     not null
         constraint SYS_C0013127 check ("NAME" IS NOT NULL),
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

/*==============================================================*/
/* Index: Indx_STATEGRP_Nme                                     */
/*==============================================================*/
create unique index Indx_STATEGRP_Nme on STATEGROUP (
   NAME DESC
)
/


/*==============================================================*/
/* Index: Indx_STATEGROUP_PK                                    */
/*==============================================================*/
create unique index Indx_STATEGROUP_PK on STATEGROUP (
   STATEGROUPID ASC
)
/


/*==============================================================*/
/* Table : SYSTEMLOG                                            */
/*==============================================================*/


create table SYSTEMLOG  (
   LOGID                NUMBER                           not null,
   POINTID              NUMBER                           not null,
   DATETIME             DATE                             not null
         constraint SYS_C0013403 check ("DATETIME" IS NOT NULL),
   SOE_TAG              NUMBER                           not null
         constraint SYS_C0013404 check ("SOE_TAG" IS NOT NULL),
   TYPE                 NUMBER                           not null
         constraint SYS_C0013405 check ("TYPE" IS NOT NULL),
   PRIORITY             NUMBER                           not null
         constraint SYS_C0013406 check ("PRIORITY" IS NOT NULL),
   ACTION               VARCHAR2(60),
   DESCRIPTION          VARCHAR2(120),
   USERNAME             VARCHAR2(30)
)
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
/* Index: Indx_SYSTEMLOG_PK                                     */
/*==============================================================*/
create unique index Indx_SYSTEMLOG_PK on SYSTEMLOG (
   LOGID ASC
)
/


/*==============================================================*/
/* Table : SeasonSchedule                                       */
/*==============================================================*/


create table SeasonSchedule  (
   ScheduleID           NUMBER                           not null,
   ScheduleName         VARCHAR2(40)                     not null,
   SpringMonth          NUMBER                           not null,
   SpringDay            NUMBER                           not null,
   SummerMonth          NUMBER                           not null,
   SummerDay            NUMBER                           not null,
   FallMonth            NUMBER                           not null,
   FallDay              NUMBER                           not null,
   WinterMonth          NUMBER                           not null,
   WinterDay            NUMBER                           not null
)
/


/* There should be one default season schedule, months range 0-11, days range
*  1-31.  It is implied that the start of Spring signals the end of Winter, start of
*  Fall signals the end of Summer, etc.
*/
insert into SeasonSchedule values(0,'Default Season Schedule',3,15,5,1,8,15,11,1);

/*==============================================================*/
/* Index: Indx_SeasSchd_PK                                      */
/*==============================================================*/
create unique index Indx_SeasSchd_PK on SeasonSchedule (
   ScheduleID ASC
)
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


/*==============================================================*/
/* Index: Indx_TEMPLATE_PK                                      */
/*==============================================================*/
create unique index Indx_TEMPLATE_PK on TEMPLATE (
   TEMPLATENUM ASC
)
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
insert into templatecolumns values( 1, 'Tags', 13, 6, 80 );

insert into templatecolumns values( 2, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 2, 'Value', 9, 2, 85 );
insert into templatecolumns values( 2, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 2, 'Time', 11, 4, 135 );
insert into templatecolumns values( 2, 'Tags', 13, 5, 80 );

insert into templatecolumns values( 3, 'Point Name', 2, 1, 85 );
insert into templatecolumns values( 3, 'Value', 9, 2, 85 );
insert into templatecolumns values( 3, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 3, 'Time', 11, 4, 135 );
insert into templatecolumns values( 3, 'Tags', 13, 5, 80 );


/*==============================================================*/
/* Index: Indx_TEMPLATECOLUMNS_PK                               */
/*==============================================================*/
create unique index Indx_TEMPLATECOLUMNS_PK on TEMPLATECOLUMNS (
   TEMPLATENUM ASC,
   TITLE ASC
)
/


/*==============================================================*/
/* Table : TagLog                                               */
/*==============================================================*/


create table TagLog  (
   LogID                NUMBER                           not null,
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


/*==============================================================*/
/* Index: Indx_TagLog_PK                                        */
/*==============================================================*/
create unique index Indx_TagLog_PK on TagLog (
   LogID ASC
)
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


/*==============================================================*/
/* Index: Indx_Tags_PK                                          */
/*==============================================================*/
create unique index Indx_Tags_PK on Tags (
   TagID ASC
)
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

/*==============================================================*/
/* Index: Indx_UNITMEASURE_PK                                   */
/*==============================================================*/
create unique index Indx_UNITMEASURE_PK on UNITMEASURE (
   UOMID ASC
)
/


/*==============================================================*/
/* Table : VersacomRoute                                        */
/*==============================================================*/


create table VersacomRoute  (
   ROUTEID              NUMBER                           not null,
   UTILITYID            NUMBER                           not null
         constraint SYS_C0013276 check ("UTILITYID" IS NOT NULL),
   SECTIONADDRESS       NUMBER                           not null
         constraint SYS_C0013277 check ("SECTIONADDRESS" IS NOT NULL),
   CLASSADDRESS         NUMBER                           not null
         constraint SYS_C0013278 check ("CLASSADDRESS" IS NOT NULL),
   DIVISIONADDRESS      NUMBER                           not null
         constraint SYS_C0013279 check ("DIVISIONADDRESS" IS NOT NULL),
   BUSNUMBER            NUMBER                           not null
         constraint SYS_C0013280 check ("BUSNUMBER" IS NOT NULL),
   AMPCARDSET           NUMBER                           not null
         constraint SYS_C0013281 check ("AMPCARDSET" IS NOT NULL)
)
/


/*==============================================================*/
/* Index: Indx_VERSACOMROUTE_PK                                 */
/*==============================================================*/
create unique index Indx_VERSACOMROUTE_PK on VersacomRoute (
   ROUTEID ASC
)
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


insert into YukonGroup values(-1,'yukon','The default system user group that allows limited user interaction.');
insert into YukonGroup values(-100,'operators', 'The default group of yukon operators');
insert into yukongroup values(-200,'Esub Users', 'The default group of esubstation users');
insert into yukongroup values(-201,'Esub Operators', 'The default group of esubstation operators');
insert into yukongroup values (-301,'Web Client Operators','The default group of web client operators');
insert into yukongroup values (-300,'Residential Customers','The default group of residential customers');
insert into yukongroup values (-302, 'Web Client Customers', 'The default group of web client customers');

/*==============================================================*/
/* Index: Indx_YukGrp_PK                                        */
/*==============================================================*/
create unique index Indx_YukGrp_PK on YukonGroup (
   GroupID ASC
)
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
insert into YukonGroupRole values(1,-1,-1,-1000,'(none)');
insert into YukonGroupRole values(2,-1,-1,-1001,'(none)');
insert into YukonGroupRole values(3,-1,-1,-1002,'(none)');
insert into YukonGroupRole values(4,-1,-1,-1003,'(none)');
insert into YukonGroupRole values(5,-1,-1,-1004,'(none)');
insert into YukonGroupRole values(6,-1,-1,-1005,'(none)');
insert into YukonGroupRole values(7,-1,-1,-1006,'(none)');
insert into YukonGroupRole values(8,-1,-1,-1007,'(none)');
insert into YukonGroupRole values(9,-1,-1,-1008,'(none)');
insert into YukonGroupRole values(10,-1,-1,-1009,'(none)');

/* Assign roles to the default operator group to allow them to use all the main rich Yukon applications */
/* Database Editor */
insert into YukonGroupRole values(100,-100,-100,-10000,'(none)');
insert into YukonGroupRole values(101,-100,-100,-10001,'(none)');
insert into YukonGroupRole values(102,-100,-100,-10002,'(none)');
insert into YukonGroupRole values(103,-100,-100,-10003,'(none)');
insert into YukonGroupRole values(104,-100,-100,-10004,'(none)');
insert into YukonGroupRole values(105,-100,-100,-10005,'(none)');
insert into YukonGroupRole values(106,-100,-100,-10006,'(none)');
insert into YukonGroupRole values(107,-100,-100,-10007,'(none)');

/* TDC */
insert into YukonGroupRole values(120,-100,-101,-10100,'(none)');
insert into YukonGroupRole values(121,-100,-101,-10101,'(none)');
insert into YukonGroupRole values(122,-100,-101,-10102,'(none)');
insert into YukonGroupRole values(123,-100,-101,-10103,'(none)');
insert into YukonGroupRole values(124,-100,-101,-10104,'(none)');
insert into YukonGroupRole values(125,-100,-101,-10105,'(none)');
insert into YukonGroupRole values(126,-100,-101,-10106,'(none)');
insert into YukonGroupRole values(127,-100,-101,-10107,'(none)');
insert into YukonGroupRole values(128,-100,-101,-10108,'(none)');
insert into YukonGroupRole values(129,-100,-101,-10109,'(none)');
insert into YukonGroupRole values(130,-100,-101,-10110,'(none)');

/* Trending */
insert into YukonGroupRole values(150,-100,-102,-10200,'(none)');
insert into YukonGroupRole values(151,-100,-102,-10201,'(none)');

/* Commander */
insert into YukonGroupRole values(170,-100,-103,-10300,'(none)');

/* Calc Historical */
insert into YukonGroupRole values(190,-100,-104,-10400,'(none)');
insert into YukonGroupRole values(191,-100,-104,-10401,'(none)');
insert into YukonGroupRole values(192,-100,-104,-10402,'(none)');
insert into YukonGroupRole values(193,-100,-104,-10403,'(none)');

/* Web Graph */
insert into YukonGroupRole values(210,-100,-105,-10500,'(none)');
insert into YukonGroupRole values(211,-100,-105,-10501,'(none)');
insert into YukonGroupRole values(212,-100,-105,-10502,'(none)');

/* Billing */
insert into YukonGroupRole values(230,-100,-106,-10600,'(none)');
insert into YukonGroupRole values(231,-100,-106,-10601,'(none)');
insert into YukonGroupRole values(232,-100,-106,-10602,'(none)');

/* Esubstation Editor */
insert into YukonGroupRole values(250,-100,-107,-10700,'(none)');

/* Assign roles to the default Esub Users */
insert into YukonGroupRole values(300,-200,-206,-20600,'(none)');
insert into YukonGroupRole values(301,-200,-206,-20601,'(none)');
insert into YukonGroupRole values(302,-200,-206,-20602,'(none)');

/* Assign roles to the default Esub Operators */
insert into YukonGroupRole values(350,-201,-206,-20600,'(none)');
insert into YukonGroupRole values(351,-201,-206,-20601,'true');
insert into YukonGroupRole values(352,-201,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into yukongrouprole values (400, -302, -108, -10800, '/user/CILC/user_trending.jsp');
insert into yukongrouprole values (401, -302, -108, -10801, '(none)');
insert into yukongrouprole values (402, -302, -108, -10802, '(none)');
insert into yukongrouprole values (403, -302, -108, -10803, '(none)');
insert into yukongrouprole values (404, -302, -108, -10804, '(none)');
insert into yukongrouprole values (405, -302, -108, -10805, '(none)');
insert into yukongrouprole values (406, -302, -108, -10806, '(none)');

/* Web Client Customers Direct Load Control role */
insert into yukongrouprole values (407, -302, -300, -30000, '(none)');
insert into yukongrouprole values (408, -302, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into yukongrouprole values (409, -302, -301, -30100, '(none)');
insert into yukongrouprole values (410, -302, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into yukongrouprole values (411, -302, -302, -30200, '(none)');
insert into yukongrouprole values (412, -302, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into yukongrouprole values (413, -302, -304, -30400, '(none)');
insert into yukongrouprole values (414, -302, -304, -30401, 'true');

/* Web Client Customers Administrator role */
insert into yukongrouprole values (415, -302, -305, -30500, 'true');

insert into yukongrouprole values (500,-300,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (501,-300,-108,-10801,'(none)');
insert into yukongrouprole values (502,-300,-108,-10802,'(none)');
insert into yukongrouprole values (503,-300,-108,-10803,'(none)');
insert into yukongrouprole values (504,-300,-108,-10804,'(none)');
insert into yukongrouprole values (505,-300,-108,-10805,'DemoHeaderCES.gif');
insert into yukongrouprole values (506,-300,-108,-10806,'(none)');
insert into yukongrouprole values (520,-300,-400,-40000,'true');
insert into yukongrouprole values (521,-300,-400,-40001,'true');
insert into yukongrouprole values (522,-300,-400,-40002,'false');
insert into yukongrouprole values (523,-300,-400,-40003,'true');
insert into yukongrouprole values (524,-300,-400,-40004,'true');
insert into yukongrouprole values (525,-300,-400,-40005,'true');
insert into yukongrouprole values (526,-300,-400,-40006,'true');
insert into yukongrouprole values (527,-300,-400,-40007,'true');
insert into yukongrouprole values (528,-300,-400,-40008,'true');
insert into yukongrouprole values (529,-300,-400,-40009,'true');
insert into yukongrouprole values (530,-300,-400,-40010,'true');
insert into yukongrouprole values (550,-300,-400,-40050,'false');
insert into yukongrouprole values (551,-300,-400,-40051,'false');
insert into yukongrouprole values (554,-300,-400,-40054,'false');
insert into yukongrouprole values (600,-300,-400,-40100,'(none)');
insert into yukongrouprole values (601,-300,-400,-40101,'(none)');
insert into yukongrouprole values (610,-300,-400,-40110,'(none)');
insert into yukongrouprole values (611,-300,-400,-40111,'(none)');
insert into yukongrouprole values (612,-300,-400,-40112,'(none)');
insert into yukongrouprole values (613,-300,-400,-40113,'(none)');
insert into yukongrouprole values (614,-300,-400,-40114,'(none)');
insert into yukongrouprole values (615,-300,-400,-40115,'(none)');
insert into yukongrouprole values (616,-300,-400,-40116,'(none)');
insert into yukongrouprole values (617,-300,-400,-40117,'(none)');
insert into yukongrouprole values (630,-300,-400,-40130,'(none)');
insert into yukongrouprole values (631,-300,-400,-40131,'(none)');
insert into yukongrouprole values (632,-300,-400,-40132,'(none)');
insert into yukongrouprole values (633,-300,-400,-40133,'(none)');
insert into yukongrouprole values (634,-300,-400,-40134,'(none)');
insert into yukongrouprole values (650,-300,-400,-40150,'(none)');
insert into yukongrouprole values (651,-300,-400,-40151,'(none)');
insert into yukongrouprole values (652,-300,-400,-40152,'(none)');
insert into yukongrouprole values (653,-300,-400,-40153,'(none)');
insert into yukongrouprole values (654,-300,-400,-40154,'(none)');
insert into yukongrouprole values (655,-300,-400,-40155,'(none)');
insert into yukongrouprole values (656,-300,-400,-40156,'(none)');
insert into yukongrouprole values (657,-300,-400,-40157,'(none)');
insert into yukongrouprole values (658,-300,-400,-40158,'(none)');
insert into yukongrouprole values (670,-300,-400,-40170,'(none)');
insert into yukongrouprole values (671,-300,-400,-40171,'(none)');
insert into yukongrouprole values (672,-300,-400,-40172,'(none)');
insert into yukongrouprole values (673,-300,-400,-40173,'(none)');
insert into yukongrouprole values (680,-300,-400,-40180,'(none)');
insert into yukongrouprole values (681,-300,-400,-40181,'(none)');

insert into yukongrouprole values (700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (701,-301,-108,-10801,'(none)');
insert into yukongrouprole values (702,-301,-108,-10802,'(none)');
insert into yukongrouprole values (703,-301,-108,-10803,'(none)');
insert into yukongrouprole values (704,-301,-108,-10804,'(none)');
insert into yukongrouprole values (705,-301,-108,-10805,'(none)');
insert into yukongrouprole values (706,-301,-108,-10806,'(none)');
insert into yukongrouprole values (720,-301,-201,-20100,'true');
insert into yukongrouprole values (721,-301,-201,-20101,'true');
insert into yukongrouprole values (722,-301,-201,-20102,'true');
insert into yukongrouprole values (723,-301,-201,-20103,'true');
insert into yukongrouprole values (724,-301,-201,-20104,'false');
insert into yukongrouprole values (725,-301,-201,-20105,'false');
insert into yukongrouprole values (726,-301,-201,-20106,'true');
insert into yukongrouprole values (727,-301,-201,-20107,'true');
insert into yukongrouprole values (728,-301,-201,-20108,'true');
insert into yukongrouprole values (729,-301,-201,-20109,'true');
insert into yukongrouprole values (730,-301,-201,-20110,'true');
insert into yukongrouprole values (731,-301,-201,-20111,'true');
insert into yukongrouprole values (732,-301,-201,-20112,'true');
insert into yukongrouprole values (733,-301,-201,-20113,'true');
insert into yukongrouprole values (734,-301,-201,-20114,'true');
insert into yukongrouprole values (735,-301,-201,-20115,'true');
insert into yukongrouprole values (736,-301,-201,-20116,'true');
insert into yukongrouprole values (737,-301,-201,-20117,'true');
insert into yukongrouprole values (750,-301,-201,-20150,'true');
insert into yukongrouprole values (751,-301,-201,-20151,'true');
insert into yukongrouprole values (752,-301,-201,-20152,'false');
insert into yukongrouprole values (770,-301,-202,-20200,'(none)');
insert into yukongrouprole values (775,-301,-203,-20300,'(none)');
insert into yukongrouprole values (776,-301,-203,-20301,'(none)');
insert into yukongrouprole values (780,-301,-204,-20400,'(none)');
insert into yukongrouprole values (785,-301,-205,-20500,'(none)');
insert into yukongrouprole values (790,-301,-207,-20700,'(none)');
insert into yukongrouprole values (800,-301,-201,-20800,'(none)');
insert into yukongrouprole values (810,-301,-201,-20810,'(none)');
insert into yukongrouprole values (813,-301,-201,-20813,'(none)');
insert into yukongrouprole values (814,-301,-201,-20814,'(none)');
insert into yukongrouprole values (815,-301,-201,-20815,'(none)');
insert into yukongrouprole values (816,-301,-201,-20816,'(none)');
insert into yukongrouprole values (819,-301,-201,-20819,'(none)');
insert into yukongrouprole values (820,-301,-201,-20820,'(none)');
insert into yukongrouprole values (830,-301,-201,-20830,'(none)');
insert into yukongrouprole values (831,-301,-201,-20831,'(none)');
insert into yukongrouprole values (832,-301,-201,-20832,'(none)');
insert into yukongrouprole values (833,-301,-201,-20833,'(none)');
insert into yukongrouprole values (834,-301,-201,-20834,'(none)');
insert into yukongrouprole values (850,-301,-201,-20850,'(none)');
insert into yukongrouprole values (851,-301,-201,-20851,'(none)');
insert into yukongrouprole values (852,-301,-201,-20852,'(none)');
insert into yukongrouprole values (853,-301,-201,-20853,'(none)');
insert into yukongrouprole values (854,-301,-201,-20854,'(none)');
insert into yukongrouprole values (855,-301,-201,-20855,'(none)');
insert into yukongrouprole values (856,-301,-201,-20856,'(none)');
insert into yukongrouprole values (870,-301,-201,-20870,'(none)');


/*==============================================================*/
/* Index: Indx_YukGrpRol_PK                                     */
/*==============================================================*/
create unique index Indx_YukGrpRol_PK on YukonGroupRole (
   GroupRoleID ASC
)
/


/*==============================================================*/
/* Table : YukonImage                                           */
/*==============================================================*/


create table YukonImage  (
   ImageID              NUMBER                           not null,
   ImageCategory        VARCHAR2(20),
   ImageName            VARCHAR2(80),
   ImageValue           LONG RAW
)
/


insert into YukonImage values( 0, '(none)', '(none)', null );

/*==============================================================*/
/* Index: Indx_YUKONIMAGE_PK                                    */
/*==============================================================*/
create unique index Indx_YUKONIMAGE_PK on YukonImage (
   ImageID ASC
)
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

/*==============================================================*/
/* Index: Indx_YukLstEntr_PK                                    */
/*==============================================================*/
create unique index Indx_YukLstEntr_PK on YukonListEntry (
   EntryID ASC
)
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
/* Index: Indx_YUKONPAOBJECT_PK                                 */
/*==============================================================*/
create unique index Indx_YUKONPAOBJECT_PK on YukonPAObject (
   PAObjectID ASC
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


/* Default role for all users */
insert into YukonRole values(-1,'Yukon','Yukon','Default Yukon role');
insert into YukonRole values(-2,'Energy Company','Yukon','Energy company role');

/* Application specific roles */
insert into YukonRole values(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
insert into YukonRole values(-101,'Tabular Display Console','Application','Access to the Yukon Tabular Display Console application');
insert into YukonRole values(-102,'Trending','Application','Access to the Yukon Trending application');
insert into YukonRole values(-103,'Commander','Application','Access to the Yukon Commander application');
insert into YukonRole values(-104,'Calc Historical','Application','Calc Historical');
insert into YukonRole values(-105,'Web Graph','Application','Web Graph');
insert into YukonRole values(-106,'Billing','Application','Billing');
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

/* Inventory Role */
insert into YukonRole values (-209,'Inventory','Operator','Operator Access to hardware inventory');

/* CI customer roles */
insert into YukonRole values(-300,'Direct Loadcontrol','CICustomer','Customer access to commercial/industrial customer direct loadcontrol');
insert into YukonRole values(-301,'Curtailment','CICustomer','Customer access to commercial/industrial customer direct curtailment');
insert into YukonRole values(-302,'Energy Buyback','CICustomer','Customer access to commercial/industrial customer energy buyback');
insert into YukonRole values(-303,'Esubstation Drawings','CICustomer','Customer access to esubstation drawings');
insert into YukonRole values(-304,'Commercial Metering','CICustomer','Customer access to commercial metering');
insert into YukonRole values(-305,'Administrator','CICustomer','Administrator privilages.');

/* Consumer roles */
insert into YukonRole values(-400,'Residential Customer','Consumer','Access to residential customer information');

/*==============================================================*/
/* Index: Indx_YukRol_PK                                        */
/*==============================================================*/
create unique index Indx_YukRol_PK on YukonRole (
   RoleID ASC
)
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

/* Database Editor Role */
insert into YukonRoleProperty values(-10000,-100,'point_id_edit','true','Controls whether point ids can be edited');
insert into YukonRoleProperty values(-10001,-100,'dbeditor_core','true','Controls whether the Core menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10003,-100,'dbeditor_cap_control','true','Controls whether the Cap Control menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10005,-100,'utility_id_range','1-254','<description>');
insert into YukonRoleProperty values(-10006,-100,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');

/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of the emails sent by the STARS server');
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','info@cannontech.com','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1103,-2,'switch_command_file','c:/yukon/switch_command/default_switch.txt','Location of the file to temporarily store the switch commands');
insert into YukonRoleProperty values(-1104,-2,'optout_command_file','c:/yukon/switch_command/default_optout.txt','Location of the file to temporarily store the opt out commands');
insert into YukonRoleProperty values(-1105,-2,'customer_group_name','Residential Customers','Group name of all the residential customer logins');
insert into YukonRoleProperty values(-1106,-2,'operator_group_name','WebClient Operators','Group name of all the web client operator logins');

/* TDC Role */
insert into YukonRoleProperty values(-10100,-101,'loadcontrol_edit','00000000','<description>');
insert into YukonRoleProperty values(-10101,-101,'macs_edit','00000CCC','<description>');
insert into YukonRoleProperty values(-10102,-101,'tdc_express','ludicrous_speed','<description>');
insert into YukonRoleProperty values(-10103,-101,'tdc_max_rows','500','<description>');
insert into YukonRoleProperty values(-10104,-101,'tdc_rights','00000000','<description>');
insert into YukonRoleProperty values(-10105,-101,'CAP_CONTROL_INTERFACE','amfm','<description>');
insert into YukonRoleProperty values(-10106,-101,'cbc_creation_name','CBC %PAOName%','<description>');
insert into YukonRoleProperty values(-10107,-101,'tdc_alarm_count','3','<description>');
insert into YukonRoleProperty values(-10108,-101,'decimal_places','2','<description>');
insert into YukonRoleProperty values(-10109,-101,'pfactor_decimal_places','1','<description>');
insert into YukonRoleProperty values(-10110,-101,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');
insert into YukonRoleProperty values(-10201,-102,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Commander Role Properties */ 
insert into YukonRoleProperty values(-10300,-103,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Calc Historical Role Properties */
insert into YukonRoleProperty values(-10400,-104,'interval','900','<description>');
insert into YukonRoleProperty values(-10401,-104,'baseline_calctime','4','<description>');
insert into YukonRoleProperty values(-10402,-104,'daysprevioustocollect','30','<description>');
insert into YukonRoleProperty values(-10403,-104,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Web Graph Role Properties */
insert into YukonRoleProperty values(-10500,-105,'home_directory','c:\yukon\client\webgraphs','<description>');
insert into YukonRoleProperty values(-10501,-105,'run_interval','900','<description>');
insert into YukonRoleProperty values(-10502,-105,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Billing Role Properties */
insert into YukonRoleProperty values(-10600,-106,'wiz_activate','false','<description>');
insert into YukonRoleProperty values(-10601,-106,'input_file','c:\yukon\client\bin\BillingIn.txt','<description>');
insert into YukonRoleProperty values(-10602,-106,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Esubstation Editor Role Properties */
insert into YukonRoleProperty values(-10700,-107,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10800,-108,'home_url','/default.jsp','The url to take the user immediately after logging into the Yukon web applicatoin');
insert into YukonRoleProperty values(-10801,-108,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into yukonroleproperty values (-10802, -108,'style_sheet','CannonStyle.css','The web client cascading style sheet.');
insert into yukonroleproperty values (-10803, -108,'nav_bullet_selected','Bullet.gif','The bullet used when an item in the nav is selected.');
insert into YukonRoleProperty values (-10804,-108,'nav_bullet_expand','BulletExpand.gif','The bullet used when an item in the nav can be expanded to show submenu.');
insert into yukonroleproperty values (-10805,-108,'header_logo','DemoHeader.gif','The main header logo');
insert into YukonRoleProperty values(-10806,-108,'log_off_url','(none)','The url to take the user after logging off the Yukon web application');

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20100,-201,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
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

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20150,-201,'Super Operator','false','Used for some testing functions (not recommended)');
insert into YukonRoleProperty values(-20151,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
insert into YukonRoleProperty values(-20152,-201,'Import Customer Account','false','Controls whether to enable the customer account importing feature');

/* Operator Administrator Role Properties */
insert into YukonRoleProperty values(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
insert into YukonRoleProperty values(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
insert into YukonRoleProperty values(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');

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
insert into YukonRoleProperty values(-20800,-201,'Link FAQ','FAQ.jsp','The customized FAQ link');
insert into YukonRoleProperty values(-20810,-201,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-20813,-201,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-20814,-201,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-20815,-201,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-20816,-201,'Text Reenable','reenable','Term for reenable');
insert into YukonRoleProperty values(-20819,-201,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-20820,-201,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-20830,-201,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20831,-201,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20832,-201,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20833,-201,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-20834,-201,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20855,-201,'Title Thermostat Schedule','Schedule','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-20856,-201,'Title Thermostat Manual','Manual','Title of the thermostat manual page');
insert into YukonRoleProperty values(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');

/* Operator Hardware Inventory Role Properties */
insert into YukonRoleProperty values(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
insert into YukonRoleProperty values(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardwares by serial number range');
insert into YukonRoleProperty values(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardwares by serial number range');
insert into YukonRoleProperty values(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardwares by serial number range');

/* CICustomer Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-30000,-300,'Direct Loadcontrol Label','Direct Control','The customer specific name for direct loadcontrol');
insert into YukonRoleProperty values(-30001,-300,'Individual Switch','false','Controls access to customer individual switch control');

/* CICustomer Curtailment Role Properties */
insert into YukonRoleProperty values(-30100,-301,'Direct Curtailment Label','Notification','The customer specific name for direct curtailment');
insert into YukonRoleProperty values(-30101,-301,'Direct Curtailment Provider','<curtailment provider>','This customers direct curtailment provider');

/* CICustomer Energy Buyback Role Properties */
insert into YukonRoleProperty values(-30200,-302,'Energy Buyback Label','Energy Buyback','The customer specific name for Energy Buyback');
insert into YukonRoleProperty values(-30201,-302,'Energy Buyback Phone Number','1-800-555-5555','The phone number to call if the customer has questions');

/* CICustomer Esubstation Drawings Role Properties */
insert into YukonRoleProperty values(-30300,-303,'View Drawings','true','Controls viewing of Esubstations drawings');
insert into YukonRoleProperty values(-30301,-303,'Edit Limits','false','Controls editing of point limits');
insert into YukonRoleProperty values(-30302,-303,'Control','false','Controls control from Esubstation drawings');

/* CICustomer Commercial Metering Role Properties */
insert into YukonRoleProperty values(-30400,-304,'Trending Disclaimer',' ','The disclaimer that appears with trends');
insert into yukonroleproperty values(-30401, -304, 'Trending Get Data Now Button', 'false', 'Controls access to retrieve meter data on demand');

/* CICustomer Administrator Role */
insert into yukonroleproperty values(-30500, -305, 'Contact Information Editable', 'false', 'Contact information is editable by the customer');

/* Residential Customer Role Properties */
insert into YukonRoleProperty values(-40000,-400,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
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
insert into YukonRoleProperty values(-40050,-400,'Notification on General Page','false','Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)');
insert into YukonRoleProperty values(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');

insert into YukonRoleProperty values(-40054,-400,'Disable Program Signup','false','Controls whether to prevent the customers from enrolling in or out of the programs');

insert into YukonRoleProperty values(-40100,-400,'Link FAQ','FAQ.jsp','The customized FAQ link');
insert into YukonRoleProperty values(-40101,-400,'Link Utility Email','FAQ.jsp','The customized utility email');
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
insert into YukonRoleProperty values(-40150,-400,'Title General','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40151,-400,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-40152,-400,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40153,-400,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40154,-400,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40155,-400,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40156,-400,'Title Utility','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40157,-400,'Title Thermostat Schedule','Schedule','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-40158,-400,'Title Thermostat Manual','Manual','Title of the thermostat manual page');
insert into YukonRoleProperty values(-40170,-400,'Description General','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40171,-400,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');
insert into YukonRoleProperty values(-40172,-400,'Description Program','(none)','Description on the programs details page. If not provided, the descriptions of the published programs will be used.');
insert into YukonRoleProperty values(-40173,-400,'Description Utility','<<COMPANY_ADDRESS>><br><<PHONE_NUMBER>><<FAX_NUMBER>><<EMAIL>>','Description on the contact us page. The special fields in the default value will be replaced by real information when showing on the web.');
insert into YukonRoleProperty values(-40180,-400,'Image Corner','Mom.jpg','Image at the upper-left corner');
insert into YukonRoleProperty values(-40181,-400,'Image General','Family.jpg','Image on the general page');

/*==============================================================*/
/* Index: Indx_YukRolPrp_PK                                     */
/*==============================================================*/
create unique index Indx_YukRolPrp_PK on YukonRoleProperty (
   RolePropertyID ASC
)
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


/*==============================================================*/
/* Index: Indx_YkSelLst_PK                                      */
/*==============================================================*/
create unique index Indx_YkSelLst_PK on YukonSelectionList (
   ListID ASC
)
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


insert into YukonUser values(-1,'yukon','yukon',0,'01-JAN-00','Enabled');

/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique index Indx_YkUsIDNm on YukonUser (
   UserName ASC
)
/


/*==============================================================*/
/* Index: Indx_YukUser_PK                                       */
/*==============================================================*/
create unique index Indx_YukUser_PK on YukonUser (
   UserID ASC
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
insert into YukonUserGroup values(-1,-100);

/*==============================================================*/
/* Index: Indx_YukUGr_PK                                        */
/*==============================================================*/
create unique index Indx_YukUGr_PK on YukonUserGroup (
   UserID ASC,
   GroupID ASC
)
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


/*==============================================================*/
/* Index: Indx_YukUsRol_PK                                      */
/*==============================================================*/
create unique index Indx_YukUsRol_PK on YukonUserRole (
   UserRoleID ASC
)
/


/*==============================================================*/
/* Table : YukonWebConfiguration                                */
/*==============================================================*/


create table YukonWebConfiguration  (
   ConfigurationID      NUMBER                           not null,
   LogoLocation         VARCHAR2(100),
   Description          VARCHAR2(500),
   AlternateDisplayName VARCHAR2(50),
   URL                  VARCHAR2(100)
)
/


insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');


/*==============================================================*/
/* Index: Indx_YukWbCfg_PK                                      */
/*==============================================================*/
create unique index Indx_YukWbCfg_PK on YukonWebConfiguration (
   ConfigurationID ASC
)
/


/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create or replace view DISPLAY2WAYDATA_VIEW (POINTID, POINTNAME , POINTTYPE , POINTSTATE , DEVICENAME, DEVICETYPE, DEVICECURRENTSTATE, DEVICEID, POINTVALUE, POINTQUALITY, POINTTIMESTAMP, UofM, TAGS) as
select POINTID, POINTNAME, POINTTYPE, SERVICEFLAG, YukonPAObject.PAOName, YukonPAObject.Type, YukonPAObject.Description, YukonPAObject.PAObjectID, '**DYNAMIC**', '**DYNAMIC**', '**DYNAMIC**', (select uomname from pointunit,unitmeasure where pointunit.pointid=point.pointid and pointunit.uomid=unitmeasure.uomid), '**DYNAMIC**'
from YukonPAObject, POINT
where YukonPAObject.PAObjectID = POINT.PAObjectID
/


/*==============================================================*/
/* View: ExpressComAddress_View                                 */
/*==============================================================*/
create or replace view ExpressComAddress_View as
select x.LMGroupID, x.RouteID, x.SerialNumber, s.Address as serviceaddress,
g.Address as geoaddress, b.Address as substationaddress, f.Address as feederaddress,
x.ZipCodeAddress, x.UDAddress, p.Address as programaddress, x.SplinterAddress, x.AddressUsage, x.RelayUsage
from LMGroupExpressCom x, LMGroupExpressComAddress s, 
LMGroupExpressComAddress g, LMGroupExpressComAddress b, LMGroupExpressComAddress f,
LMGroupExpressComAddress p
where ( x.ServiceProviderID = s.AddressID and ( s.AddressType = 'SERVICE' or s.AddressID = 0 ) )
and ( x.FeederID = f.AddressID and ( f.AddressType = 'FEEDER' or f.AddressID = 0 ) )
and ( x.GeoID = g.AddressID and ( g.AddressType = 'GEO' or g.AddressID = 0 ) )
and ( x.ProgramID = p.AddressID and ( p.AddressType = 'PROGRAM' or p.AddressID = 0 ) )
and ( x.SubstationID = b.AddressID and ( b.AddressType = 'SUBSTATION' or b.AddressID = 0 ) )
/


/*==============================================================*/
/* View: FeederAddress_View                                     */
/*==============================================================*/
create or replace view FeederAddress_View as
select x.LMGroupID, a.Address as FeederAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.FeederID = a.AddressID and ( a.AddressType = 'FEEDER' or a.AddressID = 0 ) )
/


/*==============================================================*/
/* View: FullEventLog_View                                      */
/*==============================================================*/
create or replace view FullEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, DeviceName, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, y.PAOName, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from YukonPAObject y, POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
/


/*==============================================================*/
/* View: FullPointHistory_View                                  */
/*==============================================================*/
create or replace view FullPointHistory_View (PointID, DeviceName, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, y.PAOName, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from YukonPAObject y, POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
/


/*==============================================================*/
/* View: GeoAddress_View                                        */
/*==============================================================*/
create or replace view GeoAddress_View as
select x.LMGroupID, a.Address as GeoAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.GeoID = a.AddressID and ( a.AddressType = 'GEO' or a.AddressID = 0 ) )
/


/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create or replace view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID
/


/*==============================================================*/
/* View: LMGroupMacroExpander_View                              */
/*==============================================================*/
create or replace view LMGroupMacroExpander_View as
select distinct PAObjectID, Category, PAOClass, PAOName, Type, Description, DisableFlag, 
ALARMINHIBIT, CONTROLINHIBIT, KWCapacity, dg.DeviceID, 
LMGroupDeviceID, GroupOrder, OwnerID, ChildID, ChildOrder
from YukonPAObject y, DEVICE d, LMGroup g,
LMProgramDirectGroup dg, GenericMacro m
where y.PAObjectID = d.DEVICEID 
and d.DEVICEID = g.DeviceID
and dg.lmgroupdeviceid = m.ownerid (+)
/


/*==============================================================*/
/* View: Peakpointhistory_View                                  */
/*==============================================================*/
create or replace view Peakpointhistory_View as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where value in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by pointid, value
/


/*==============================================================*/
/* View: PointEventLog_View                                     */
/*==============================================================*/
create or replace view PointEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID
/


/*==============================================================*/
/* View: PointHistory_View                                      */
/*==============================================================*/
create or replace view PointHistory_View (PointID, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID
/


/*==============================================================*/
/* View: ProgramAddress_View                                    */
/*==============================================================*/
create or replace view ProgramAddress_View as
select x.LMGroupID, a.Address as ProgramAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ProgramID = a.AddressID and ( a.AddressType = 'PROGRAM' or a.AddressID = 0 ) )
/


/*==============================================================*/
/* View: ServiceAddress_View                                    */
/*==============================================================*/
create or replace view ServiceAddress_View as
select x.LMGroupID, a.Address as ServiceAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ServiceProviderID = a.AddressID and ( a.AddressType = 'SERVICE' or a.AddressID = 0 ) )
/


/*==============================================================*/
/* View: SubstationAddress_View                                 */
/*==============================================================*/
create or replace view SubstationAddress_View as
select x.LMGroupID, a.Address as SubstationAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.SubstationID = a.AddressID and ( a.AddressType = 'SUBSTATION' or a.AddressID = 0 ) )
/


alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
/


alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
/


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
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
      references LMPROGRAM (DEVICEID)
/


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
/


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
/


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
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


alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table DeviceDNP
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID)
/


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
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


alter table LMPROGRAM
   add constraint FK_HlSc_LmPr foreign key (HolidayScheduleID)
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


alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
/


alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
/


alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
/


alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
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


alter table LMPROGRAM
   add constraint FK_SesSch_LmPr foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
/


alter table SOELog
   add constraint FK_Soe_Pt foreign key (PointID)
      references POINT (POINTID)
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


alter table PortStatistics
   add constraint SYS_C0013183 foreign key (PORTID)
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


