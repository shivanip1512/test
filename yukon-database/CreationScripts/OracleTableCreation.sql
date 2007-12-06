/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      ORACLE Version 9i                            */
/* Created on:     12/6/2007 10:48:46 AM                        */
/*==============================================================*/


set define off;


drop procedure RenCol
;

drop view SubstationAddress_View
;

drop view ServiceAddress_View
;

drop view ProgramAddress_View
;

drop view PointHistory_View
;

drop view PointEventLog_View
;

drop view Peakpointhistory_View
;

drop view LMProgram_View
;

drop view LMCurtailCustomerActivity_View
;

drop view GeoAddress_View
;

drop view FullPointHistory_View
;

drop view FullEventLog_View
;

drop view FeederAddress_View
;

drop view ExpressComAddress_View
;

drop view DISPLAY2WAYDATA_VIEW
;

drop view CCOPERATIONS_VIEW
;

drop view CCINVENTORY_VIEW
;

drop table ActivityLog cascade constraints
;

drop table Address cascade constraints
;

drop table AlarmCategory cascade constraints
;

drop table BaseLine cascade constraints
;

drop table BillingFileFormats cascade constraints
;

drop table CALCBASE cascade constraints
;

drop table CALCCOMPONENT cascade constraints
;

drop table CAPBANK cascade constraints
;

drop table CAPBANKADDITIONAL cascade constraints
;

drop table CAPBANKCOMMENT cascade constraints
;

drop table CAPCONTROLAREA cascade constraints
;

drop table CAPCONTROLSPECIALAREA cascade constraints
;

drop table CAPCONTROLSUBSTATION cascade constraints
;

drop table CAPCONTROLSUBSTATIONBUS cascade constraints
;

drop table CCEventLog cascade constraints
;

drop table CCFeederBankList cascade constraints
;

drop table CCFeederSubAssignment cascade constraints
;

drop table CCMONITORBANKLIST cascade constraints
;

drop table CCSEASONSTRATEGYASSIGNMENT cascade constraints
;

drop table CCSTRATEGYTIMEOFDAY cascade constraints
;

drop table CCSUBAREAASSIGNMENT cascade constraints
;

drop table CCSUBSPECIALAREAASSIGNMENT cascade constraints
;

drop table CCSUBSTATIONSUBBUSLIST cascade constraints
;

drop table CCURTACCTEVENT cascade constraints
;

drop table CCURTACCTEVENTPARTICIPANT cascade constraints
;

drop table CCurtCENotif cascade constraints
;

drop table CCurtCEParticipant cascade constraints
;

drop table CCurtCurtailmentEvent cascade constraints
;

drop table CCurtEEParticipant cascade constraints
;

drop table CCurtEEParticipantSelection cascade constraints
;

drop table CCurtEEParticipantWindow cascade constraints
;

drop table CCurtEEPricing cascade constraints
;

drop table CCurtEEPricingWindow cascade constraints
;

drop table CCurtEconomicEvent cascade constraints
;

drop table CCurtEconomicEventNotif cascade constraints
;

drop table CCurtGroup cascade constraints
;

drop table CCurtGroupCustomerNotif cascade constraints
;

drop table CCurtProgram cascade constraints
;

drop table CCurtProgramGroup cascade constraints
;

drop table CCurtProgramNotifGroup cascade constraints
;

drop table CCurtProgramParameter cascade constraints
;

drop table CCurtProgramType cascade constraints
;

drop table CICUSTOMERPOINTDATA cascade constraints
;

drop table CICustomerBase cascade constraints
;

drop table COLUMNTYPE cascade constraints
;

drop table CTIDatabase cascade constraints
;

drop table CalcPointBaseline cascade constraints
;

drop table CapControlFeeder cascade constraints
;

drop table CapControlStrategy cascade constraints
;

drop table CarrierRoute cascade constraints
;

drop table CommErrorHistory cascade constraints
;

drop table CommPort cascade constraints
;

drop table Command cascade constraints
;

drop table CommandGroup cascade constraints
;

drop table Contact cascade constraints
;

drop table ContactNotifGroupMap cascade constraints
;

drop table ContactNotification cascade constraints
;

drop table Customer cascade constraints
;

drop table CustomerAdditionalContact cascade constraints
;

drop table CustomerBaseLinePoint cascade constraints
;

drop table CustomerLoginSerialGroup cascade constraints
;

drop table CustomerNotifGroupMap cascade constraints
;

drop table DCCategory cascade constraints
;

drop table DCCategoryItem cascade constraints
;

drop table DCCategoryItemType cascade constraints
;

drop table DCCategoryType cascade constraints
;

drop table DCConfiguration cascade constraints
;

drop table DCConfigurationCategory cascade constraints
;

drop table DCConfigurationCategoryType cascade constraints
;

drop table DCConfigurationType cascade constraints
;

drop table DCDeviceConfiguration cascade constraints
;

drop table DCDeviceConfigurationType cascade constraints
;

drop table DCItemType cascade constraints
;

drop table DCItemValue cascade constraints
;

drop table DEVICE cascade constraints
;

drop table DEVICE2WAYFLAGS cascade constraints
;

drop table DEVICECARRIERSETTINGS cascade constraints
;

drop table DEVICECONFIGURATION cascade constraints
;

drop table DEVICECONFIGURATIONDEVICEMAP cascade constraints
;

drop table DEVICECONFIGURATIONITEM cascade constraints
;

drop table DEVICEDIALUPSETTINGS cascade constraints
;

drop table DEVICEEVENT cascade constraints
;

drop table DEVICEGROUP cascade constraints
;

drop table DEVICEGROUPMEMBER cascade constraints
;

drop table DEVICEIDLCREMOTE cascade constraints
;

drop table DEVICEIED cascade constraints
;

drop table DEVICELOADPROFILE cascade constraints
;

drop table DEVICEMCTIEDPORT cascade constraints
;

drop table DEVICEMETERGROUP cascade constraints
;

drop table DEVICEREADJOBLOG cascade constraints
;

drop table DEVICEREADLOG cascade constraints
;

drop table DEVICEREADREQUESTLOG cascade constraints
;

drop table DEVICESCANRATE cascade constraints
;

drop table DEVICETAPPAGINGSETTINGS cascade constraints
;

drop table DISPLAY cascade constraints
;

drop table DISPLAY2WAYDATA cascade constraints
;

drop table DISPLAYCOLUMNS cascade constraints
;

drop table DYNAMICACCUMULATOR cascade constraints
;

drop table DYNAMICBILLINGFIELD cascade constraints
;

drop table DYNAMICBILLINGFORMAT cascade constraints
;

drop table DYNAMICCCAREA cascade constraints
;

drop table DYNAMICCCSPECIALAREA cascade constraints
;

drop table DYNAMICCCSUBSTATION cascade constraints
;

drop table DYNAMICCCTWOWAYCBC cascade constraints
;

drop table DYNAMICDEVICESCANDATA cascade constraints
;

drop table DYNAMICPAOSTATISTICSHISTORY cascade constraints
;

drop table DYNAMICPOINTDISPATCH cascade constraints
;

drop table DateOfHoliday cascade constraints
;

drop table DateOfSeason cascade constraints
;

drop table DeviceAddress cascade constraints
;

drop table DeviceCBC cascade constraints
;

drop table DeviceCustomerList cascade constraints
;

drop table DeviceDirectCommSettings cascade constraints
;

drop table DeviceMCT400Series cascade constraints
;

drop table DevicePagingReceiverSettings cascade constraints
;

drop table DeviceRTC cascade constraints
;

drop table DeviceRoutes cascade constraints
;

drop table DeviceSeries5RTU cascade constraints
;

drop table DeviceTNPPSettings cascade constraints
;

drop table DeviceTypeCommand cascade constraints
;

drop table DeviceVerification cascade constraints
;

drop table DeviceWindow cascade constraints
;

drop table DynamicCCCapBank cascade constraints
;

drop table DynamicCCFeeder cascade constraints
;

drop table DynamicCCMonitorBankHistory cascade constraints
;

drop table DynamicCCMonitorPointResponse cascade constraints
;

drop table DynamicCCSubstationBus cascade constraints
;

drop table DynamicCalcHistorical cascade constraints
;

drop table DynamicImportStatus cascade constraints
;

drop table DynamicLMControlArea cascade constraints
;

drop table DynamicLMControlAreaTrigger cascade constraints
;

drop table DynamicLMControlHistory cascade constraints
;

drop table DynamicLMGroup cascade constraints
;

drop table DynamicLMProgram cascade constraints
;

drop table DynamicLMProgramDirect cascade constraints
;

drop table DynamicPAOInfo cascade constraints
;

drop table DynamicPAOStatistics cascade constraints
;

drop table DynamicPointAlarming cascade constraints
;

drop table DynamicTags cascade constraints
;

drop table DynamicVerification cascade constraints
;

drop table EnergyCompany cascade constraints
;

drop table EnergyCompanyCustomerList cascade constraints
;

drop table EnergyCompanyOperatorLoginList cascade constraints
;

drop table EsubDisplayIndex cascade constraints
;

drop table FDRInterface cascade constraints
;

drop table FDRInterfaceOption cascade constraints
;

drop table FDRTRANSLATION cascade constraints
;

drop table GRAPHDATASERIES cascade constraints
;

drop table GRAPHDEFINITION cascade constraints
;

drop table GatewayEndDevice cascade constraints
;

drop table GenericMacro cascade constraints
;

drop table GraphCustomerList cascade constraints
;

drop table GroupPaoPermission cascade constraints
;

drop table HolidaySchedule cascade constraints
;

drop table ImportData cascade constraints
;

drop table ImportFail cascade constraints
;

drop table ImportPendingComm cascade constraints
;

drop table JOB cascade constraints
;

drop table JOBPROPERTY cascade constraints
;

drop table JOBSCHEDULEDONETIME cascade constraints
;

drop table JOBSCHEDULEDREPEATING cascade constraints
;

drop table JOBSTATUS cascade constraints
;

drop table LMCONTROLAREAPROGRAM cascade constraints
;

drop table LMCONTROLAREATRIGGER cascade constraints
;

drop table LMControlArea cascade constraints
;

drop table LMControlHistory cascade constraints
;

drop table LMControlScenarioProgram cascade constraints
;

drop table LMCurtailCustomerActivity cascade constraints
;

drop table LMCurtailProgramActivity cascade constraints
;

drop table LMDirectCustomerList cascade constraints
;

drop table LMDirectNotifGrpList cascade constraints
;

drop table LMEnergyExchangeCustomerList cascade constraints
;

drop table LMEnergyExchangeCustomerReply cascade constraints
;

drop table LMEnergyExchangeHourlyCustomer cascade constraints
;

drop table LMEnergyExchangeHourlyOffer cascade constraints
;

drop table LMEnergyExchangeOfferRevision cascade constraints
;

drop table LMEnergyExchangeProgramOffer cascade constraints
;

drop table LMGroup cascade constraints
;

drop table LMGroupEmetcon cascade constraints
;

drop table LMGroupExpressCom cascade constraints
;

drop table LMGroupExpressComAddress cascade constraints
;

drop table LMGroupMCT cascade constraints
;

drop table LMGroupPoint cascade constraints
;

drop table LMGroupRipple cascade constraints
;

drop table LMGroupSA205105 cascade constraints
;

drop table LMGroupSA305 cascade constraints
;

drop table LMGroupSASimple cascade constraints
;

drop table LMGroupVersacom cascade constraints
;

drop table LMMacsScheduleCustomerList cascade constraints
;

drop table LMPROGRAM cascade constraints
;

drop table LMProgramConstraints cascade constraints
;

drop table LMProgramControlWindow cascade constraints
;

drop table LMProgramCurtailCustomerList cascade constraints
;

drop table LMProgramCurtailment cascade constraints
;

drop table LMProgramDirect cascade constraints
;

drop table LMProgramDirectGear cascade constraints
;

drop table LMProgramDirectGroup cascade constraints
;

drop table LMProgramEnergyExchange cascade constraints
;

drop table LMThermoStatGear cascade constraints
;

drop table LOGIC cascade constraints
;

drop table MACROROUTE cascade constraints
;

drop table MACSchedule cascade constraints
;

drop table MACSimpleSchedule cascade constraints
;

drop table MCTBroadCastMapping cascade constraints
;

drop table MCTConfig cascade constraints
;

drop table MCTConfigMapping cascade constraints
;

drop table MSPInterface cascade constraints
;

drop table MSPVendor cascade constraints
;

drop table NotificationDestination cascade constraints
;

drop table NotificationGroup cascade constraints
;

drop table OperatorLoginGraphList cascade constraints
;

drop table PAOExclusion cascade constraints
;

drop table PAOSchedule cascade constraints
;

drop table PAOScheduleAssignment cascade constraints
;

drop table PAOowner cascade constraints
;

drop table PEAKREPORT cascade constraints
;

drop table POINT cascade constraints
;

drop table POINTACCUMULATOR cascade constraints
;

drop table POINTANALOG cascade constraints
;

drop table POINTLIMITS cascade constraints
;

drop table POINTSTATUS cascade constraints
;

drop table POINTTRIGGER cascade constraints
;

drop table POINTUNIT cascade constraints
;

drop table PORTDIALUPMODEM cascade constraints
;

drop table PORTLOCALSERIAL cascade constraints
;

drop table PORTRADIOSETTINGS cascade constraints
;

drop table PORTSETTINGS cascade constraints
;

drop table PORTTERMINALSERVER cascade constraints
;

drop table PROFILEPEAKRESULT cascade constraints
;

drop table PointAlarming cascade constraints
;

drop table PortTiming cascade constraints
;

drop table RAWPOINTHISTORY cascade constraints
;

drop table RepeaterRoute cascade constraints
;

drop table Route cascade constraints
;

drop table STATE cascade constraints
;

drop table STATEGROUP cascade constraints
;

drop table SYSTEMLOG cascade constraints
;

drop table SeasonSchedule cascade constraints
;

drop table SequenceNumber cascade constraints
;

drop table SettlementConfig cascade constraints
;

drop table TEMPLATE cascade constraints
;

drop table TEMPLATECOLUMNS cascade constraints
;

drop table TOUATTRIBUTEMAPPING cascade constraints
;

drop table TOUDay cascade constraints
;

drop table TOUDayMapping cascade constraints
;

drop table TOUDayRateSwitches cascade constraints
;

drop table TOUSchedule cascade constraints
;

drop table TagLog cascade constraints
;

drop table Tags cascade constraints
;

drop table TemplateDisplay cascade constraints
;

drop table UNITMEASURE cascade constraints
;

drop table UserPaoPermission cascade constraints
;

drop table VersacomRoute cascade constraints
;

drop table YukonGroup cascade constraints
;

drop table YukonGroupRole cascade constraints
;

drop table YukonImage cascade constraints
;

drop table YukonListEntry cascade constraints
;

drop table YukonPAObject cascade constraints
;

drop table YukonRole cascade constraints
;

drop table YukonRoleProperty cascade constraints
;

drop table YukonSelectionList cascade constraints
;

drop table YukonServices cascade constraints
;

drop table YukonUser cascade constraints
;

drop table YukonUserGroup cascade constraints
;

drop table YukonUserRole cascade constraints
;

drop table YukonWebConfiguration cascade constraints
;

drop user Yukon
;

/*==============================================================*/
/* User: Yukon                                                  */
/*==============================================================*/
create user Yukon identified by ''
;

/*==============================================================*/
/* Table: ActivityLog                                           */
/*==============================================================*/
create table ActivityLog  (
   ActivityLogID        NUMBER                          not null,
   TimeStamp            DATE                            not null,
   UserID               NUMBER,
   AccountID            NUMBER,
   EnergyCompanyID      NUMBER,
   CustomerID           NUMBER,
   PaoID                NUMBER,
   Action               VARCHAR2(80)                    not null,
   Description          VARCHAR2(240),
   constraint PK_ACTIVITYLOG primary key (ActivityLogID)
)
;

/*==============================================================*/
/* Table: Address                                               */
/*==============================================================*/
create table Address  (
   AddressID            NUMBER                          not null,
   LocationAddress1     VARCHAR2(40)                    not null,
   LocationAddress2     VARCHAR2(40)                    not null,
   CityName             VARCHAR2(32)                    not null,
   StateCode            CHAR(2)                         not null,
   ZipCode              VARCHAR2(12)                    not null,
   County               VARCHAR2(30)                    not null,
   constraint PK_ADDRESS primary key (AddressID)
)
;

insert into address values ( 0, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' );

/*==============================================================*/
/* Table: AlarmCategory                                         */
/*==============================================================*/
create table AlarmCategory  (
   AlarmCategoryID      NUMBER                          not null,
   CategoryName         VARCHAR2(40)                    not null,
   NotificationGroupID  NUMBER                          not null,
   constraint PK_ALARMCATEGORYID primary key (AlarmCategoryID)
)
;

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

insert into AlarmCategory values(33,'Category 33',1);
insert into AlarmCategory values(34,'Category 34',1);
insert into AlarmCategory values(35,'Category 35',1);
insert into AlarmCategory values(36,'Category 36',1);
insert into AlarmCategory values(37,'Category 37',1);
insert into AlarmCategory values(38,'Category 38',1);
insert into AlarmCategory values(39,'Category 39',1);
insert into AlarmCategory values(40,'Category 40',1);
insert into AlarmCategory values(41,'Category 41',1);
insert into AlarmCategory values(42,'Category 42',1);
insert into AlarmCategory values(43,'Category 43',1);
insert into AlarmCategory values(44,'Category 44',1);
insert into AlarmCategory values(45,'Category 45',1);
insert into AlarmCategory values(46,'Category 46',1);
insert into AlarmCategory values(47,'Category 47',1);
insert into AlarmCategory values(48,'Category 48',1);
insert into AlarmCategory values(49,'Category 49',1);
insert into AlarmCategory values(50,'Category 50',1);
insert into AlarmCategory values(51,'Category 51',1);
insert into AlarmCategory values(52,'Category 52',1);
insert into AlarmCategory values(53,'Category 53',1);
insert into AlarmCategory values(54,'Category 54',1);
insert into AlarmCategory values(55,'Category 55',1);
insert into AlarmCategory values(56,'Category 56',1);
insert into AlarmCategory values(57,'Category 57',1);
insert into AlarmCategory values(58,'Category 58',1);
insert into AlarmCategory values(59,'Category 59',1);
insert into AlarmCategory values(60,'Category 60',1);
insert into AlarmCategory values(61,'Category 61',1);
insert into AlarmCategory values(62,'Category 62',1);
insert into AlarmCategory values(63,'Category 63',1);
insert into AlarmCategory values(64,'Category 64',1);
insert into AlarmCategory values(65,'Category 65',1);
insert into AlarmCategory values(66,'Category 66',1);
insert into AlarmCategory values(67,'Category 67',1);
insert into AlarmCategory values(68,'Category 68',1);
insert into AlarmCategory values(69,'Category 69',1);
insert into AlarmCategory values(70,'Category 70',1);
insert into AlarmCategory values(71,'Category 71',1);
insert into AlarmCategory values(72,'Category 72',1);
insert into AlarmCategory values(73,'Category 73',1);
insert into AlarmCategory values(74,'Category 74',1);
insert into AlarmCategory values(75,'Category 75',1);
insert into AlarmCategory values(76,'Category 76',1);
insert into AlarmCategory values(77,'Category 77',1);
insert into AlarmCategory values(78,'Category 78',1);
insert into AlarmCategory values(79,'Category 79',1);
insert into AlarmCategory values(80,'Category 80',1);
insert into AlarmCategory values(81,'Category 81',1);
insert into AlarmCategory values(82,'Category 82',1);
insert into AlarmCategory values(83,'Category 83',1);
insert into AlarmCategory values(84,'Category 84',1);
insert into AlarmCategory values(85,'Category 85',1);
insert into AlarmCategory values(86,'Category 86',1);
insert into AlarmCategory values(87,'Category 87',1);
insert into AlarmCategory values(88,'Category 88',1);
insert into AlarmCategory values(89,'Category 89',1);
insert into AlarmCategory values(90,'Category 90',1);
insert into AlarmCategory values(91,'Category 91',1);
insert into AlarmCategory values(92,'Category 92',1);
insert into AlarmCategory values(93,'Category 93',1);
insert into AlarmCategory values(94,'Category 94',1);
insert into AlarmCategory values(95,'Category 95',1);
insert into AlarmCategory values(96,'Category 96',1);
insert into AlarmCategory values(97,'Category 97',1);
insert into AlarmCategory values(98,'Category 98',1);
insert into AlarmCategory values(99,'Category 99',1);
insert into AlarmCategory values(100,'Category 100',1);

/*==============================================================*/
/* Table: BaseLine                                              */
/*==============================================================*/
create table BaseLine  (
   BaselineID           NUMBER                          not null,
   BaselineName         VARCHAR2(30)                    not null,
   DaysUsed             NUMBER                          not null,
   PercentWindow        NUMBER                          not null,
   CalcDays             NUMBER                          not null,
   ExcludedWeekDays     CHAR(7)                         not null,
   HolidaysUsed         NUMBER                          not null,
   constraint PK_BASELINE primary key (BaselineID)
)
;

insert into baseline values (1, 'Default Baseline', 30, 75, 5, 'YNNNNNY', 0);

/*==============================================================*/
/* Table: BillingFileFormats                                    */
/*==============================================================*/
create table BillingFileFormats  (
   FormatID             NUMBER                          not null,
   FormatType           VARCHAR2(100)                   not null,
   SystemFormat         SMALLINT                       default 1,
   constraint PK_BILLINGFILEFORMATS primary key (FormatID)
)
;

insert into billingfileformats values(-11, 'MV_90 DATA Import',1);
insert into billingfileformats values(-100,'INVALID',1);
insert into billingfileformats values( 1,'CADP',1);
insert into billingfileformats values( 2,'CADPXL2',1);
insert into billingfileformats values( 3,'WLT-40',1);
insert into billingfileformats values( 4,'CTI-CSV',1);
insert into billingfileformats values( 5,'OPU',1);
insert into billingfileformats values( 9, 'CTI2',1);
insert into billingfileformats values( 15, 'NCDC-Handheld',1);
insert into billingfileformats values( 16, 'NISC TOU (kVarH)',1);
insert into billingfileformats values(-20, 'IVUE_BI_T65',1);
insert into billingfileformats values( 21, 'SIMPLE_TOU',1);
insert into billingfileformats values( 22, 'EXTENDED_TOU',1);
insert into billingfileformats values(-23, 'Big Rivers Elec Coop',1);
insert into billingfileformats values(-24, 'INCODE (Extended TOU)',1);
insert into billingfileformats values(-25, 'Itron Register Readings Export',1);
insert into billingfileformats values(-26, 'SIMPLE_TOU_DeviceName',1);
insert into billingfileformats values( 31, 'STANDARD',1); 

/*==============================================================*/
/* Table: CALCBASE                                              */
/*==============================================================*/
create table CALCBASE  (
   POINTID              NUMBER                          not null,
   UPDATETYPE           VARCHAR2(16)                    not null,
   PERIODICRATE         NUMBER                          not null,
   QualityFlag          CHAR(1)                         not null,
   constraint PK_CALCBASE primary key (POINTID)
)
;

/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create index Indx_ClcBaseUpdTyp on CALCBASE (
   UPDATETYPE ASC
)
;

/*==============================================================*/
/* Table: CALCCOMPONENT                                         */
/*==============================================================*/
create table CALCCOMPONENT  (
   PointID              NUMBER                          not null,
   COMPONENTORDER       NUMBER                          not null,
   COMPONENTTYPE        VARCHAR2(10)                    not null,
   COMPONENTPOINTID     NUMBER                          not null,
   OPERATION            VARCHAR2(10),
   CONSTANT             FLOAT                           not null,
   FUNCTIONNAME         VARCHAR2(20),
   constraint PK_CALCCOMPONENT primary key (PointID, COMPONENTORDER)
)
;

/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create index Indx_CalcCmpCmpType on CALCCOMPONENT (
   COMPONENTTYPE ASC
)
;

/*==============================================================*/
/* Table: CAPBANK                                               */
/*==============================================================*/
create table CAPBANK  (
   DEVICEID             NUMBER                          not null,
   OPERATIONALSTATE     VARCHAR2(16)                    not null,
   ControllerType       VARCHAR2(20)                    not null,
   CONTROLDEVICEID      NUMBER                          not null,
   CONTROLPOINTID       NUMBER                          not null,
   BANKSIZE             NUMBER                          not null,
   TypeOfSwitch         VARCHAR2(20)                    not null,
   SwitchManufacture    VARCHAR2(20)                    not null,
   MapLocationID        VARCHAR2(64)                    not null,
   RecloseDelay         NUMBER                          not null,
   MaxDailyOps          NUMBER                          not null,
   MaxOpDisable         CHAR(1)                         not null,
   constraint PK_CAPBANK primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: CAPBANKADDITIONAL                                     */
/*==============================================================*/
create table CAPBANKADDITIONAL  (
   DeviceID             NUMBER                          not null,
   MaintenanceAreaID    NUMBER                          not null,
   PoleNumber           NUMBER                          not null,
   DriveDirections      VARCHAR2(120)                   not null,
   Latitude             FLOAT                           not null,
   Longitude            FLOAT                           not null,
   CapBankConfig        VARCHAR2(10)                    not null,
   CommMedium           VARCHAR2(20)                    not null,
   CommStrength         NUMBER                          not null,
   ExtAntenna           CHAR(1)                         not null,
   AntennaType          VARCHAR2(10)                    not null,
   LastMaintVisit       DATE                            not null,
   LastInspVisit        DATE                            not null,
   OpCountResetDate     DATE                            not null,
   PotentialTransformer VARCHAR2(10)                    not null,
   MaintenanceReqPend   CHAR(1)                         not null,
   OtherComments        VARCHAR2(150)                   not null,
   OpTeamComments       VARCHAR2(150)                   not null,
   CBCBattInstallDate   DATE                            not null,
   constraint PK_CAPBANKADDITIONAL primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: CAPBANKCOMMENT                                        */
/*==============================================================*/
create table CAPBANKCOMMENT  (
   CommentID            INTEGER                         not null,
   PaoID                INTEGER                         not null,
   UserID               INTEGER                         not null,
   CommentTime          DATE                            not null,
   "Comment"            VARCHAR2(500)                   not null,
   Altered              VARCHAR2(3)                     not null,
   constraint PK_CAPBANKCOMMENT primary key (CommentID)
)
;

/*==============================================================*/
/* Table: CAPCONTROLAREA                                        */
/*==============================================================*/
create table CAPCONTROLAREA  (
   AreaID               number                          not null,
   constraint PK_CAPCONTROLAREA primary key (AreaID)
)
;

/*==============================================================*/
/* Table: CAPCONTROLSPECIALAREA                                 */
/*==============================================================*/
create table CAPCONTROLSPECIALAREA  (
   AreaID               NUMBER                          not null,
   constraint PK_CapControlSpecialArea primary key (AreaID)
)
;

/*==============================================================*/
/* Table: CAPCONTROLSUBSTATION                                  */
/*==============================================================*/
create table CAPCONTROLSUBSTATION  (
   SubstationID         NUMBER                          not null,
   constraint PK_CAPCONTROLSUBSTATION primary key (SubstationID)
)
;

/*==============================================================*/
/* Table: CAPCONTROLSUBSTATIONBUS                               */
/*==============================================================*/
create table CAPCONTROLSUBSTATIONBUS  (
   SubstationBusID      NUMBER                          not null,
   CurrentVarLoadPointID NUMBER                          not null,
   CurrentWattLoadPointID NUMBER                          not null,
   MapLocationID        VARCHAR2(64)                    not null,
   CurrentVoltLoadPointID NUMBER                          not null,
   AltSubID             NUMBER                          not null,
   SwitchPointID        NUMBER                          not null,
   DualBusEnabled       CHAR(1)                         not null,
   MultiMonitorControl  CHAR(1)                         not null,
   usephasedata         CHAR(1)                        default '0' not null,
   phaseb               NUMBER                         default 0 not null,
   phasec               NUMBER                         default 0 not null,
   constraint SYS_C0013476 primary key (SubstationBusID)
)
;

/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
   CurrentVarLoadPointID ASC
)
;

/*==============================================================*/
/* Table: CCEventLog                                            */
/*==============================================================*/
create table CCEventLog  (
   LogID                NUMBER                          not null,
   PointID              NUMBER                          not null,
   DateTime             DATE                            not null,
   SubID                NUMBER                          not null,
   FeederID             NUMBER                          not null,
   EventType            NUMBER                          not null,
   SeqID                NUMBER                          not null,
   Value                NUMBER                          not null,
   Text                 VARCHAR2(120)                   not null,
   UserName             VARCHAR2(64)                    not null,
   KVARBefore           FLOAT                           not null,
   KVARAfter            FLOAT                           not null,
   KVARChange           FLOAT                           not null,
   AdditionalInfo       VARCHAR2(20)                    not null,
   actionId             NUMBER                         default -1 not null,
   CapBankStateInfo     VARCHAR2(20)                   default 'N/A' not null,
   constraint PK_CCEventLog primary key (LogID)
)
;

/*==============================================================*/
/* Table: CCFeederBankList                                      */
/*==============================================================*/
create table CCFeederBankList  (
   FeederID             NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   ControlOrder         FLOAT                           not null,
   CloseOrder           FLOAT                           not null,
   TripOrder            FLOAT                           not null,
   constraint PK_CCFEEDERBANKLIST primary key (FeederID, DeviceID)
)
;

/*==============================================================*/
/* Table: CCFeederSubAssignment                                 */
/*==============================================================*/
create table CCFeederSubAssignment  (
   SubStationBusID      NUMBER                          not null,
   FeederID             NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCFEEDERSUBASSIGNMENT primary key (SubStationBusID, FeederID)
)
;

/*==============================================================*/
/* Table: CCMONITORBANKLIST                                     */
/*==============================================================*/
create table CCMONITORBANKLIST  (
   BankID               NUMBER                          not null,
   PointID              NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   Scannable            CHAR(1)                         not null,
   NINAvg               NUMBER                          not null,
   UpperBandwidth       FLOAT                           not null,
   LowerBandwidth       FLOAT                           not null,
   constraint PK_CCMONITORBANKLIST primary key (BankID, PointID)
)
;

/*==============================================================*/
/* Table: CCSEASONSTRATEGYASSIGNMENT                            */
/*==============================================================*/
create table CCSEASONSTRATEGYASSIGNMENT  (
   paobjectid           NUMBER                          not null,
   seasonscheduleid     NUMBER                          not null,
   seasonname           VARCHAR2(20)                    not null,
   strategyid           NUMBER                          not null,
   constraint PK_CCSEASONSTRATEGYASSIGNMENT primary key (paobjectid)
)
;

/*==============================================================*/
/* Table: CCSTRATEGYTIMEOFDAY                                   */
/*==============================================================*/
create table CCSTRATEGYTIMEOFDAY  (
   StrategyID           NUMBER                          not null,
   StartTimeSeconds     NUMBER                          not null,
   PercentClose         NUMBER                          not null,
   constraint PK_STRAT_TOD primary key (StrategyID, StartTimeSeconds)
)
;

/*==============================================================*/
/* Table: CCSUBAREAASSIGNMENT                                   */
/*==============================================================*/
create table CCSUBAREAASSIGNMENT  (
   AreaID               NUMBER                          not null,
   SubstationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCSUBAREAASSIGNMENT primary key (AreaID, SubstationBusID)
)
;

/*==============================================================*/
/* Table: CCSUBSPECIALAREAASSIGNMENT                            */
/*==============================================================*/
create table CCSUBSPECIALAREAASSIGNMENT  (
   AreaID               NUMBER                          not null,
   SubstationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null
)
;

/*==============================================================*/
/* Table: CCSUBSTATIONSUBBUSLIST                                */
/*==============================================================*/
create table CCSUBSTATIONSUBBUSLIST  (
   SubStationID         NUMBER                          not null,
   SubStationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCSUBSTATIONSUBBUSLIST primary key (SubStationID, SubStationBusID)
)
;

/*==============================================================*/
/* Table: CCURTACCTEVENT                                        */
/*==============================================================*/
create table CCURTACCTEVENT  (
   CCurtAcctEventID     NUMBER                          not null,
   CCurtProgramID       NUMBER                          not null,
   Duration             NUMBER                          not null,
   Reason               VARCHAR2(255)                   not null,
   StartTime            DATE                            not null,
   Identifier           NUMBER                          not null,
   constraint PK_CCURTACCTEVENT primary key (CCurtAcctEventID)
)
;

/*==============================================================*/
/* Table: CCURTACCTEVENTPARTICIPANT                             */
/*==============================================================*/
create table CCURTACCTEVENTPARTICIPANT  (
   CCurtAcctEventParticipantID NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CCurtAcctEventID     NUMBER                          not null,
   constraint PK_CCURTACCTEVENTPARTICIPANT primary key (CCurtAcctEventParticipantID)
)
;

/*==============================================================*/
/* Table: CCurtCENotif                                          */
/*==============================================================*/
create table CCurtCENotif  (
   CCurtCENotifID       NUMBER                          not null,
   NotificationTime     DATE,
   NotifTypeID          NUMBER                          not null,
   State                VARCHAR2(10)                    not null,
   Reason               VARCHAR2(10)                    not null,
   CCurtCEParticipantID NUMBER                          not null,
   constraint PK_CCURTCENOTIF primary key (CCurtCENotifID)
)
;

/*==============================================================*/
/* Table: CCurtCEParticipant                                    */
/*==============================================================*/
create table CCurtCEParticipant  (
   CCurtCEParticipantID NUMBER                          not null,
   NotifAttribs         VARCHAR2(256)                   not null,
   CustomerID           NUMBER                          not null,
   CCurtCurtailmentEventID NUMBER                          not null,
   constraint PK_CCURTCEPARTICIPANT primary key (CCurtCEParticipantID)
)
;

/*==============================================================*/
/* Index: INDX_CCURTCEPART_EVTID_CUSTID                         */
/*==============================================================*/
create unique index INDX_CCURTCEPART_EVTID_CUSTID on CCurtCEParticipant (
   CustomerID ASC,
   CCurtCurtailmentEventID ASC
)
;

/*==============================================================*/
/* Table: CCurtCurtailmentEvent                                 */
/*==============================================================*/
create table CCurtCurtailmentEvent  (
   CCurtCurtailmentEventID NUMBER                          not null,
   CCurtProgramID       NUMBER,
   NotificationTime     DATE                            not null,
   Duration             NUMBER                          not null,
   Message              VARCHAR2(255)                   not null,
   State                VARCHAR2(10)                    not null,
   StartTime            DATE                            not null,
   Identifier           NUMBER                          not null,
   CCurtProgramTypeID   NUMBER                          not null,
   constraint PK_CCURTCURTAILMENTEVENT primary key (CCurtCurtailmentEventID)
)
;

/*==============================================================*/
/* Table: CCurtEEParticipant                                    */
/*==============================================================*/
create table CCurtEEParticipant  (
   CCurtEEParticipantID NUMBER                          not null,
   NotifAttribs         VARCHAR2(255)                   not null,
   CustomerID           NUMBER                          not null,
   CCurtEconomicEventID NUMBER                          not null,
   constraint PK_CCURTEEPARTICIPANT primary key (CCurtEEParticipantID)
)
;

/*==============================================================*/
/* Table: CCurtEEParticipantSelection                           */
/*==============================================================*/
create table CCurtEEParticipantSelection  (
   CCurtEEParticipantSelectionID NUMBER                          not null,
   ConnectionAudit      VARCHAR2(255)                   not null,
   SubmitTime           DATE                            not null,
   State                VARCHAR2(255)                   not null,
   CCurtEEParticipantID NUMBER                          not null,
   CCurtEEPricingID     NUMBER                          not null,
   constraint PK_CCURTEEPARTICIPANTSELECTION primary key (CCurtEEParticipantSelectionID)
)
;

/*==============================================================*/
/* Index: INDX_CCURTEEPARTSEL_CCURTEEPR                         */
/*==============================================================*/
create unique index INDX_CCURTEEPARTSEL_CCURTEEPR on CCurtEEParticipantSelection (
   CCurtEEParticipantID ASC,
   CCurtEEPricingID ASC
)
;

/*==============================================================*/
/* Table: CCurtEEParticipantWindow                              */
/*==============================================================*/
create table CCurtEEParticipantWindow  (
   CCurtEEParticipantWindowID NUMBER                          not null,
   EnergyToBuy          NUMBER(19,2)                    not null,
   CCurtEEPricingWindowID NUMBER,
   CCurtEEParticipantSelectionID NUMBER,
   constraint PK_CCURTEEPARTICIPANTWINDOW primary key (CCurtEEParticipantWindowID)
)
;

/*==============================================================*/
/* Index: INDX_CCRTEEPRTWIN_PWNID_PSID                          */
/*==============================================================*/
create unique index INDX_CCRTEEPRTWIN_PWNID_PSID on CCurtEEParticipantWindow (
   CCurtEEPricingWindowID ASC,
   CCurtEEParticipantSelectionID ASC
)
;

/*==============================================================*/
/* Table: CCurtEEPricing                                        */
/*==============================================================*/
create table CCurtEEPricing  (
   CCurtEEPricingID     NUMBER                          not null,
   Revision             NUMBER                          not null,
   CreationTime         DATE                            not null,
   CCurtEconomicEventID NUMBER                          not null,
   constraint PK_CCURTEEPRICING primary key (CCurtEEPricingID)
)
;

/*==============================================================*/
/* Index: INDX_CCURTECONSVTID_REV                               */
/*==============================================================*/
create unique index INDX_CCURTECONSVTID_REV on CCurtEEPricing (
   Revision ASC,
   CCurtEconomicEventID ASC
)
;

/*==============================================================*/
/* Table: CCurtEEPricingWindow                                  */
/*==============================================================*/
create table CCurtEEPricingWindow  (
   CCurtEEPricingWindowID NUMBER                          not null,
   EnergyPrice          NUMBER(19,2)                    not null,
   Offset               NUMBER                          not null,
   CCurtEEPricingID     NUMBER,
   constraint PK_CCURTEEPRICINGWINDOW primary key (CCurtEEPricingWindowID)
)
;

/*==============================================================*/
/* Index: INDX_CCURTEEPRWIN                                     */
/*==============================================================*/
create unique index INDX_CCURTEEPRWIN on CCurtEEPricingWindow (
   Offset ASC,
   CCurtEEPricingID ASC
)
;

/*==============================================================*/
/* Table: CCurtEconomicEvent                                    */
/*==============================================================*/
create table CCurtEconomicEvent  (
   CCurtEconomicEventID NUMBER                          not null,
   NotificationTime     DATE,
   WindowLengthMinutes  NUMBER                          not null,
   State                VARCHAR2(10)                    not null,
   StartTime            DATE                            not null,
   CCurtProgramID       NUMBER                          not null,
   InitialEventID       NUMBER,
   Identifier           NUMBER                          not null,
   constraint PK_CCURTECONOMICEVENT primary key (CCurtEconomicEventID)
)
;

/*==============================================================*/
/* Table: CCurtEconomicEventNotif                               */
/*==============================================================*/
create table CCurtEconomicEventNotif  (
   CCurtEconomicEventNotifID NUMBER                          not null,
   NotificationTime     DATE,
   NotifTypeID          NUMBER                          not null,
   State                VARCHAR2(10)                    not null,
   Reason               VARCHAR2(10)                    not null,
   CCurtEEPricingID     NUMBER                          not null,
   CCurtEconomicParticipantID NUMBER                          not null,
   constraint PK_CCURTECONOMICEVENTNOTIF primary key (CCurtEconomicEventNotifID)
)
;

/*==============================================================*/
/* Table: CCurtGroup                                            */
/*==============================================================*/
create table CCurtGroup  (
   CCurtGroupID         NUMBER                          not null,
   EnergyCompanyID      NUMBER,
   CCurtGroupName       VARCHAR2(255)                   not null,
   constraint PK_CCURTGROUP primary key (CCurtGroupID)
)
;

/*==============================================================*/
/* Index: INDX_CCURTGROUP_ECID_GRPNM                            */
/*==============================================================*/
create unique index INDX_CCURTGROUP_ECID_GRPNM on CCurtGroup (
   EnergyCompanyID ASC,
   CCurtGroupName ASC
)
;

/*==============================================================*/
/* Table: CCurtGroupCustomerNotif                               */
/*==============================================================*/
create table CCurtGroupCustomerNotif  (
   CCurtGroupCustomerNotifID NUMBER                          not null,
   Attribs              VARCHAR2(255)                   not null,
   CustomerID           NUMBER,
   CCurtGroupID         NUMBER,
   constraint PK_CCURTGROUPCUSTOMERNOTIF primary key (CCurtGroupCustomerNotifID)
)
;

/*==============================================================*/
/* Index: INDX_CCRTGRPCSTNOTIF_GID_CID                          */
/*==============================================================*/
create unique index INDX_CCRTGRPCSTNOTIF_GID_CID on CCurtGroupCustomerNotif (
   CustomerID ASC,
   CCurtGroupID ASC
)
;

/*==============================================================*/
/* Table: CCurtProgram                                          */
/*==============================================================*/
create table CCurtProgram  (
   CCurtProgramID       NUMBER                          not null,
   CCurtProgramName     VARCHAR2(255)                   not null,
   CCurtProgramTypeID   NUMBER,
   LastIdentifier       NUMBER                          not null,
   IdentifierPrefix     VARCHAR2(32)                    not null,
   constraint PK_CCURTPROGRAM primary key (CCurtProgramID)
)
;

/*==============================================================*/
/* Index: INDX_CCURTPGM_PRGNM_PRGTYPEID                         */
/*==============================================================*/
create index INDX_CCURTPGM_PRGNM_PRGTYPEID on CCurtProgram (
   CCurtProgramName ASC,
   CCurtProgramTypeID ASC
)
;

/*==============================================================*/
/* Table: CCurtProgramGroup                                     */
/*==============================================================*/
create table CCurtProgramGroup  (
   CCurtProgramGroupID  NUMBER                          not null,
   CCurtProgramID       NUMBER,
   CCurtGroupID         NUMBER,
   constraint PK_CCURTPROGRAMGROUP primary key (CCurtProgramGroupID)
)
;

/*==============================================================*/
/* Index: INDX_CCURTPRGGRP_GRPID_PRGID                          */
/*==============================================================*/
create unique index INDX_CCURTPRGGRP_GRPID_PRGID on CCurtProgramGroup (
   CCurtProgramID ASC,
   CCurtGroupID ASC
)
;

/*==============================================================*/
/* Table: CCurtProgramNotifGroup                                */
/*==============================================================*/
create table CCurtProgramNotifGroup  (
   CCurtProgramID       NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   constraint PK_CCURTPROGRAMNOTIFGROUP primary key (CCurtProgramID, NotificationGroupID)
)
;

/*==============================================================*/
/* Index: INDX_CCURPNG_PRGNM_PRGTYPEID                          */
/*==============================================================*/
create index INDX_CCURPNG_PRGNM_PRGTYPEID on CCurtProgramNotifGroup (
   NotificationGroupID ASC
)
;

/*==============================================================*/
/* Table: CCurtProgramParameter                                 */
/*==============================================================*/
create table CCurtProgramParameter  (
   CCurtProgramParameterID NUMBER                          not null,
   ParameterValue       VARCHAR2(255)                   not null,
   ParameterKey         VARCHAR2(255)                   not null,
   CCurtProgramID       NUMBER,
   constraint PK_CCURTPROGRAMPARAMETER primary key (CCurtProgramParameterID)
)
;

/*==============================================================*/
/* Index: INDX_CCRTPRGPRM_PGID_PMKEY                            */
/*==============================================================*/
create index INDX_CCRTPRGPRM_PGID_PMKEY on CCurtProgramParameter (
   ParameterKey ASC,
   CCurtProgramID ASC
)
;

/*==============================================================*/
/* Table: CCurtProgramType                                      */
/*==============================================================*/
create table CCurtProgramType  (
   CCurtProgramTypeID   NUMBER                          not null,
   EnergyCompanyID      NUMBER,
   CCurtProgramTypeStrategy VARCHAR2(255),
   CCurtProgramTypeName VARCHAR2(255),
   constraint PK_CCURTPROGRAMTYPE primary key (CCurtProgramTypeID)
)
;

/*==============================================================*/
/* Table: CICUSTOMERPOINTDATA                                   */
/*==============================================================*/
create table CICUSTOMERPOINTDATA  (
   CustomerID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   Type                 VARCHAR2(16)                    not null,
   OptionalLabel        VARCHAR2(32)                    not null,
   constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, Type)
)
;

/*==============================================================*/
/* Table: CICustomerBase                                        */
/*==============================================================*/
create table CICustomerBase  (
   CustomerID           NUMBER                          not null,
   MainAddressID        NUMBER                          not null,
   CustomerDemandLevel  FLOAT                           not null,
   CurtailmentAgreement VARCHAR2(100)                   not null,
   CurtailAmount        FLOAT                           not null,
   CompanyName          VARCHAR2(80)                    not null,
   CiCustType           NUMBER                          not null,
   constraint PK_CICUSTOMERBASE primary key (CustomerID)
)
;

/*==============================================================*/
/* Table: COLUMNTYPE                                            */
/*==============================================================*/
create table COLUMNTYPE  (
   TYPENUM              NUMBER                          not null,
   NAME                 VARCHAR2(20)                    not null,
   constraint SYS_C0013414 primary key (TYPENUM)
)
;

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

/*==============================================================*/
/* Table: CTIDatabase                                           */
/*==============================================================*/
create table CTIDatabase  (
   Version              VARCHAR2(6)                     not null,
   CTIEmployeeName      VARCHAR2(30)                    not null,
   DateApplied          DATE,
   Notes                VARCHAR2(300),
   Build                NUMBER                          not null,
   constraint PK_CTIDATABASE primary key (Version, Build)
)
;

/* __YUKON_VERSION__ */

/*==============================================================*/
/* Table: CalcPointBaseline                                     */
/*==============================================================*/
create table CalcPointBaseline  (
   PointID              NUMBER                          not null,
   BaselineID           NUMBER                          not null,
   constraint PK_CalcBsPt primary key (PointID)
)
;

/*==============================================================*/
/* Table: CapControlFeeder                                      */
/*==============================================================*/
create table CapControlFeeder  (
   FeederID             NUMBER                          not null,
   CurrentVarLoadPointID NUMBER                          not null,
   CurrentWattLoadPointID NUMBER                          not null,
   MapLocationID        VARCHAR2(64)                    not null,
   CurrentVoltLoadPointID NUMBER                          not null,
   MultiMonitorControl  CHAR(1)                         not null,
   usephasedata         CHAR(1)                        default '0' not null,
   phaseb               NUMBER                         default 0 not null,
   phasec               NUMBER                         default 0 not null,
   constraint PK_CAPCONTROLFEEDER primary key (FeederID)
)
;

/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create index Indx_CPCNFDVARPT on CapControlFeeder (
   CurrentVarLoadPointID ASC
)
;

/*==============================================================*/
/* Table: CapControlStrategy                                    */
/*==============================================================*/
create table CapControlStrategy  (
   StrategyID           NUMBER                          not null,
   StrategyName         VARCHAR2(32)                    not null,
   ControlMethod        VARCHAR2(32),
   MAXDAILYOPERATION    NUMBER                          not null,
   MaxOperationDisableFlag CHAR(1)                         not null,
   PEAKSTARTTIME        NUMBER                          not null,
   PEAKSTOPTIME         NUMBER                          not null,
   CONTROLINTERVAL      NUMBER                          not null,
   MINRESPONSETIME      NUMBER                          not null,
   MINCONFIRMPERCENT    NUMBER                          not null,
   FAILUREPERCENT       NUMBER                          not null,
   DAYSOFWEEK           CHAR(8)                         not null,
   ControlUnits         VARCHAR2(32)                    not null,
   ControlDelayTime     NUMBER                          not null,
   ControlSendRetries   NUMBER                          not null,
   PeakLag              FLOAT                           not null,
   PeakLead             FLOAT                           not null,
   OffPkLag             FLOAT                           not null,
   OffPkLead            FLOAT                           not null,
   PeakVARLag           FLOAT                           not null,
   PeakVARLead          FLOAT                           not null,
   OffPkVARLag          FLOAT                           not null,
   OffPkVARLead         FLOAT                           not null,
   PeakPFSetPoint       FLOAT                           not null,
   OffPkPFSetPoint      FLOAT                           not null,
   IntegrateFlag        CHAR(1)                         not null,
   IntegratePeriod      NUMBER                          not null,
   LikeDayFallBack      CHAR(1)                         not null,
   constraint PK_CAPCONTROLSTRAT primary key (StrategyID)
)
;

insert into CapControlStrategy values (0, '(none)', '(none)', 0, 'N', 0, 0, 0, 0, 0, 0, 'NYYYYYNN', '(none)', 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 100.0, 100.0, 'N', 0, 'N'); 

/*==============================================================*/
/* Index: Indx_CapCntrlStrat_name_UNQ                           */
/*==============================================================*/
create unique index Indx_CapCntrlStrat_name_UNQ on CapControlStrategy (
   StrategyName ASC
)
;

/*==============================================================*/
/* Table: CarrierRoute                                          */
/*==============================================================*/
create table CarrierRoute  (
   ROUTEID              NUMBER                          not null,
   BUSNUMBER            NUMBER                          not null,
   CCUFIXBITS           NUMBER                          not null,
   CCUVARIABLEBITS      NUMBER                          not null,
   UserLocked           CHAR(1)                         not null,
   ResetRptSettings     CHAR(1)                         not null,
   constraint PK_CARRIERROUTE primary key (ROUTEID)
)
;

/*==============================================================*/
/* Table: CommErrorHistory                                      */
/*==============================================================*/
create table CommErrorHistory  (
   CommErrorID          NUMBER                          not null,
   PAObjectID           NUMBER                          not null,
   DateTime             DATE                            not null,
   SOE_Tag              NUMBER                          not null,
   ErrorType            NUMBER                          not null,
   ErrorNumber          NUMBER                          not null,
   Command              VARCHAR2(50)                    not null,
   OutMessage           VARCHAR2(160)                   not null,
   InMessage            VARCHAR2(160)                   not null,
   constraint PK_COMMERRORHISTORY primary key (CommErrorID)
)
;

/*==============================================================*/
/* Table: CommPort                                              */
/*==============================================================*/
create table CommPort  (
   PORTID               NUMBER                          not null,
   ALARMINHIBIT         VARCHAR2(1)                     not null,
   COMMONPROTOCOL       VARCHAR2(8)                     not null,
   PERFORMTHRESHOLD     NUMBER                          not null,
   PERFORMANCEALARM     VARCHAR2(1)                     not null,
   SharedPortType       VARCHAR2(20)                    not null,
   SharedSocketNumber   NUMBER                          not null,
   constraint SYS_C0013112 primary key (PORTID)
)
;

/*==============================================================*/
/* Table: Command                                               */
/*==============================================================*/
create table Command  (
   CommandID            NUMBER                          not null,
   Command              VARCHAR2(256)                   not null,
   Label                VARCHAR2(256)                   not null,
   Category             VARCHAR2(32)                    not null,
   constraint PK_COMMAND primary key (CommandID)
)
;

/* N-A */
insert into command values(-0, 'Not Available Yet', 'Not Available Yet', 'DEVICE');

/* MCT-BASE */
insert into command values(-1, 'getvalue kWh', 'Read Energy', 'All MCTs');
insert into command values(-2, 'getvalue demand', 'Read Current Demand', 'All MCTs');
insert into command values(-3, 'getconfig model', 'Read Meter Config', 'All MCTs');
insert into command values(-4, 'putvalue kyz 1 reset', 'Clear kWh Reading', 'All MCTs');
insert into command values(-5, 'getvalue powerfail', 'Read Powerfail', 'All MCTs');
insert into command values(-6, 'getstatus internal', 'Read General Info', 'All MCTs'); 
insert into command values(-7, 'getconfig mult kyz 1', 'Read MPKH ()', 'All MCTs');
insert into command values(-8, 'putconfig emetcon multiplier kyz1 ?Multiplier(x.xxx)', 'Write MPKH ()', 'All MCTs');
insert into command values(-9, 'getconfig interval ?LP/LI', 'Read Demand Interval (LP or LI)', 'All MCTs');
insert into command values(-10, 'putconfig emetcon interval ?LP/LI', 'Write Demand Interval (LP or LI)', 'All MCTs');
/* MCT-213, 310ID */
insert into command values(-11, 'getstatus disconnect', 'Read Disconnect Status', 'All Disconnect Meters');
insert into command values(-12, 'control disconnect', 'Disconnect Meter', 'All Disconnect Meters');
insert into command values(-13, 'control connect', 'Connect Meter', 'All Disconnect Meters');
/* MCT-250, 318, 318L, 360, 370 */
insert into command values(-14, 'getstatus external', 'Read Status Points', 'All Status Input');
/* LS-BASE */
insert into command values(-15, 'getstatus LP', 'Read LS Info', 'All LP Meters');
insert into command values(-16, 'Not Available Yet', 'Read LS Intervals 1 Thru 6', 'All LP Meters');
insert into command values(-17, 'Not Available Yet', 'Read  6 LS Intervals Starting at ?', 'All LP Meters');
insert into command values(-18, 'getconfig time', 'Read Date/Time', 'All LP Meters');
insert into command values(-19, 'getconfig time sync', 'Read Last TimeSync', 'All LP Meters');
/* IED-BASE */
insert into command values(-20, 'getvalue ied demand', 'Read IED Last Interval Demands', 'All IED Meters');
insert into command values(-21, 'getvalue ied kwht', 'Read IED KWH/KW', 'All IED Meters');
insert into command values(-22, 'getconfig ied time', 'Read IED Date/Time', 'All IED Meters');
insert into command values(-23, 'Not Available', 'Read IED TOU Rate [A,B,C,or D]', 'All IED Meters');
insert into command values(-24, 'Not Available', 'Read IED Reset Count', 'All IED Meters');
insert into command values(-25, 'getconfig ied scan', 'Read IED Scan Info', 'All IED Meters');
insert into command values(-26, 'putvalue ied reset', 'Reset IED Demand', 'All IED Meters');
/* LoadGroup-BASE */
insert into command values(-27, 'control shed 5m', 'Shed Group', 'All Load Group');
insert into command values(-28, 'control restore', 'Restore Group', 'All Load Group');
/* Alpha-BASE */
insert into command values(-29, 'scan integrity', 'Force Scan', 'All Alpha Meters');
/* CBC-BASE */
insert into command values(-30, 'control open', 'OPEN Cap Bank', 'All CBCs');
insert into command values(-31, 'control close', 'CLOSE Cap Bank', 'All CBCs');
insert into command values(-32, 'putstatus ovuv disable', 'Disable OVUV', 'All CBCs');
insert into command values(-33, 'putstatus ovuv enable', 'Enable OVUV', 'All CBCs');
/* CCU-BASE, RTU-BASE, LCU-BASE, TCU-BASE */
insert into command values(-34, 'ping', 'Ping', 'All Ping-able');
insert into command values(-35, 'loop', '1 Loopback', 'All Ping-able');
insert into command values(-36, 'loop 5', '5 Loopbacks', 'All Ping-able');
/* CCU-711 */
insert into command values(-37, 'putstatus reset', 'CCU Reset', 'CCU-711');
/* Emetcon Group */
insert into command values(-38, 'control shed 7.m', 'Shed 7.5min', 'Emetcon Group');
insert into command values(-39, 'control shed 15m', 'Shed 15-min', 'Emetcon Group');
insert into command values(-40, 'control shed 30m', 'Shed 30-min', 'Emetcon Group');
insert into command values(-41, 'control shed 60m', 'Shed 60-min', 'Emetcon Group');
insert into command values(-42, 'control shed ?''NumMins''m', 'Shed x-min', 'Emetcon Group');
insert into command values(-43, 'control restore', 'Restore', 'Emetcon Group');
/* Versacom Group */
insert into command values(-44, 'control cycle 50 period 30 count 4', 'Cycle 50% / 30min', 'Versacom Group');
insert into command values(-45, 'control cycle terminate', 'Terminate Cycle', 'Versacom Group');
insert into command values(-46, 'putconfig service out', 'Set to Out-Of-Service', 'Versacom Group');
insert into command values(-47, 'putconfig service in', 'Set to In-Service', 'Versacom Group');
insert into command values(-48, 'putstatus reset r1 r2 r3 cl', 'Reset All Counters', 'Versacom Group');
insert into command values(-49, 'putconfig led yyy', 'Configure LEDS (load, test, report)', 'Versacom Group');

/* TCU-BASE*/
/* insert into command values(-50, 'loop', '1 TCU Loop', 'All TCUs'); */
/* insert into command values(-51, 'loop 5', '5 TCU Loops', 'All TCUs'); */
/* Repeater-BASE */
insert into command values(-52, 'getconfig role 1', 'Read Roles', 'All Repeaters');
insert into command values(-53, 'putconfig emetcon install', 'Download All Roles', 'All Repeaters');
insert into command values(-54, 'loop locate', 'Locate Device', 'All Repeaters');
/* ION-BASE */
insert into command values(-55, 'scan general', 'Scan Status Points', 'All ION Meters');
insert into command values(-56, 'scan integrity', 'Scan Power Meter and Status', 'All ION Meters');
insert into command values(-57, 'getstatus eventlog', 'Retrieve Event Log', 'All ION Meters');
/* IEDAlpha-BASE */
insert into command values(-58, 'putconfig emetcon ied class 72 1', 'Set Current Period (Alpha)', 'DEVICE');
insert into command values(-59, 'putconfig emetcon ied class 72 2', 'Set Previous Period (Alpha)', 'DEVICE');
/* EDGEKV-BASE */
insert into command values(-60, 'getvalue ied demand', 'Read IED Volts', 'DEVICE');
insert into command values(-61, 'putconfig emetcon ied class 0 0', 'Set Current Period (GEKV)', 'DEVICE');
insert into command values(-62, 'putconfig emetcon ied class 0 1', 'Set Previous Period (GEKV)', 'DEVICE');
/* ExpresscomSerial */
insert into command values(-63, 'putconfig xcom raw 0x30 0x00 0x02 0x58', 'Cold Load Pickup (load, time x 0.5sec)', 'ExpresscomSerial');
insert into command values(-64, 'putstatus xcom prop inc', 'Increment Prop Counter', 'ExpresscomSerial');
insert into command values(-65, 'putconfig xcom raw 0x05 0x00', 'Turn Off Test Light', 'ExpresscomSerial');
insert into command values(-66, 'putconfig xcom main 0x01 0x40', 'Clear Prop Counter', 'ExpresscomSerial');
insert into command values(-67, 'putconfig xcom main 0x01 0x80', 'Clear Comm Loss Counter', 'ExpresscomSerial');
insert into command values(-68, 'putconfig xcom service out temp offhours 24', 'Temp Out-Of-Service (hours)', 'ExpresscomSerial');
insert into command values(-69, 'putconfig xcom service in', 'In-Service', 'ExpresscomSerial');

/* LCRSerial */
insert into command values(-70, 'putconfig cycle r1 50', 'Install Cycle Count', 'LCRSerial');

/* VersacomSerial */
insert into command values(-71, 'putconfig template ''?LoadGroup''', 'Install Versacom Addressing', 'VersacomSerial');
insert into command values(-72, 'putconfig cold_load r1 10', 'Install Versacom Cold Load (relay, minutes)', 'VersacomSerial');
insert into command values(-73, 'putconfig raw 0x3a ff', 'Emetcon Cold Load (ON -ff / OFF -00', 'VersacomSerial');
insert into command values(-74, 'putconfig raw 36 0', 'Set LCR 3000 to Emetcon Mode', 'VersacomSerial');
insert into command values(-75, 'putconfig raw 36 1', 'Set LCR 3000 to Versacom Mode', 'VersacomSerial');

/* MCT410IL */
insert into command values(-81, 'getvalue demand', 'Read KW Demand, Current Voltage, Blink Count', 'All MCT-4xx Series');
insert into command values(-82, 'getvalue voltage', 'Read Min / Max Voltage', 'MCT-410IL');
insert into command values(-83, 'putconfig emetcon timesync', 'Write Time/Date to Meter', 'MCT-410IL');
insert into command values(-84, 'getvalue peak', 'Read Current Peak', 'MCT-410IL');

/* All LCRs */
insert into command values(-85, 'control shed 5m relay 1', 'Shed 5-min Relay 1', 'All LCRs');
insert into command values(-86, 'control shed 5m relay 2', 'Shed 5-min Relay 2', 'All LCRs');
insert into command values(-87, 'control shed 5m relay 3', 'Shed 5-min Relay 3', 'All LCRs');
insert into command values(-88, 'control restore relay 1', 'Restore Relay 1', 'All LCRs');
insert into command values(-89, 'control restore relay 2', 'Restore Relay 2', 'All LCRs');
insert into command values(-90, 'control restore relay 3', 'Restore Relay 3', 'All LCRs');
insert into command values(-91, 'control cycle 50 period 30 relay 1', 'Cycle 50 Period-30 Relay 1', 'All LCRs');
insert into command values(-92, 'control cycle terminate relay 1', 'Terminate Cycle Relay 1', 'All LCRs');
insert into command values(-93, 'control cycle terminate relay 2', 'Terminate Cycle Relay 2', 'All LCRs');
insert into command values(-94, 'control cycle terminate relay 3', 'Terminate Cycle Relay 3', 'All LCRs');
insert into command values(-95, 'putconfig service out', 'Set to Out-of-Service', 'All LCRs');
insert into command values(-96, 'putconfig service in', 'Set to In-Service', 'All LCRs');
insert into command values(-97, 'putconfig led yyy', 'Configure LEDS (load, test, report)', 'All LCRs'); 

insert into command values(-98, 'putconfig emetcon disconnect', 'Upload Disconnect Address', 'MCT-410IL');
insert into command values(-99, 'getconfig disconnect', 'Read Disconnect Address/Status', 'MCT-410IL');
insert into command values(-100, 'scan general', 'General Meter Scan', 'SENTINEL');
insert into command values(-101, 'scan general frozen', 'General Meter Scan Frozen', 'SENTINEL');
insert into command values(-102, 'scan general update', 'General Meter Scan and DB Update', 'SENTINEL');
insert into command values(-103, 'scan general frozen update', 'General Meter Scan Frozen and DB Update', 'SENTINEL');
insert into command values(-104, 'putvalue reset', 'Reset Demand', 'SENTINEL');
insert into command values(-105, 'getvalue lp channel ?''Channel (1 or 4)'' ?''MM/DD/YYYY HH:mm''', 'Read block of data (six intervals) from start date/time specified', 'MCT-410IL');
insert into command values(-106, 'getvalue outage ?''Outage Log (1 - 6)''', 'Read two outages per read.  Specify 1(1&2), 3(3&4), 5(5&6)', 'MCT-410IL');
insert into command values(-107, 'getvalue peak frozen', 'Read frozen demand - kW and kWh', 'MCT-410IL');
insert into command values(-108, 'getvalue voltage frozen', 'Read frozen voltage - min, max', 'MCT-410IL');
insert into command values(-109, 'putvalue powerfail reset', 'Reset blink counter', 'MCT-410IL');
insert into command values(-111, 'getconfig intervals', 'Read rates for LI, LP, Volt LI, and Volt Profile Demand', 'All MCT-4xx Series');
insert into command values(-112, 'putconfig emetcon intervals', 'Write rate intervals from database to MCT', 'MCT-410IL');
insert into command values(-113, 'putstatus emetcon freeze ?''(one or two)''', 'Reset and Write current peak demand - kW and kWh to frozen register', 'MCT-410IL');
insert into command values(-114, 'putstatus emetcon freeze voltage ?''(one or two)''', 'Reset and Write current min/max voltage to frozen register', 'MCT-410IL');

insert into command values(-115, 'getvalue ied current kwha', 'Read Current Rate A kWh/Peak kW', 'MCT-470');
insert into command values(-116, 'getvalue ied current kwhb', 'Read Current Rate B kWh/Peak kW', 'MCT-470');
insert into command values(-117, 'getvalue ied current kwhc', 'Read Current Rate C kWh/Peak kW', 'MCT-470');
insert into command values(-118, 'getvalue ied current kwhd', 'Read Current Rate D kWh/Peak kW', 'MCT-470');
insert into command values(-119, 'getvalue ied frozen kwha', 'Read Frozen Rate A kWh/Peak kW', 'MCT-470');
insert into command values(-120, 'getvalue ied frozen kwhb', 'Read Frozen Rate B kWh/Peak kW', 'MCT-470');
insert into command values(-121, 'getvalue ied frozen kwhc', 'Read Frozen Rate C kWh/Peak kW', 'MCT-470');
insert into command values(-122, 'getvalue ied frozen kwhd', 'Read Frozen Rate D kWh/Peak kW', 'MCT-470');
insert into command values(-123, 'getconfig options', 'Read Options', 'MCT-470');

insert into command values(-124, 'putconfig raw 38 0', 'Install Emetcon Gold 1', 'VersacomSerial');
insert into command values(-125, 'putconfig raw 38 1', 'Install Emetcon Gold 2', 'VersacomSerial');
insert into command values(-126, 'putconfig raw 38 2', 'Install Emetcon Gold 3', 'VersacomSerial');

insert into command values(-127, 'putconfig raw 39 0', 'Install Emetcon Silver 1', 'VersacomSerial');
insert into command values(-128, 'putconfig raw 39 1', 'Install Emetcon Silver 2', 'VersacomSerial');
insert into command values(-129, 'putconfig raw 39 2', 'Install Emetcon Silver 3', 'VersacomSerial');

insert into command values(-130, 'getvalue lp channel ?''Channel #'' ?''Enter Start Date: xx/xx/xxxx'' ?''Enter End Date: xx/xx/xxxx''', 'Read LP Channel Data', 'ALL MCT-4xx Series');
insert into command values(-131, 'getvalue lp status', 'Read LP Channel Data Status', 'ALL MCT-4xx Series');
insert into command values(-132, 'getvalue lp cancel', 'Read LP Channel Data Cancel', 'ALL MCT-4xx Series');

insert into command values(-133, 'putconfig xcom utility usage ?''Channel'':?''Value''', 'Thermostat Register Download', 'ExpresscomSerial');
insert into command values(-134, 'putconfig xcom data ''?''Text Message'''' port ?''Port (0 is default)'' deletable priority 7 timeout 30 hour clear', 'Thermostat Text Message', 'ExpresscomSerial');
insert into command values(-135, 'control xcom backlight cycles 20 duty 30 bperiod 10', 'Thermostat Display Blink', 'ExpresscomSerial');

insert into command values(-136, 'putconfig emetcon timezone ?''Enter Timezone (et|ct|mt|pt OR #offset)''', 'Write Timezone to Meter', 'MCT-430A'); 
insert into command values(-137, 'putconfig emetcon outage ?''Enter number of cycles before outage is recorded''', 'Write Threshold (number of cycles before outage recorded) to Meter', 'MCT-410IL');

/*==============================================================*/
/* Table: CommandGroup                                          */
/*==============================================================*/
create table CommandGroup  (
   CommandGroupID       NUMBER                          not null,
   CommandGroupName     VARCHAR2(60)                    not null,
   constraint PK_COMMANDGROUP primary key (CommandGroupID)
)
;

insert into CommandGroup values (-1, 'Default Commands');

/*==============================================================*/
/* Index: AK_KEY_CmdGrp_Name                                    */
/*==============================================================*/
create unique index AK_KEY_CmdGrp_Name on CommandGroup (
   CommandGroupName ASC
)
;

/*==============================================================*/
/* Table: Contact                                               */
/*==============================================================*/
create table Contact  (
   ContactID            NUMBER                          not null,
   ContFirstName        VARCHAR2(20)                    not null,
   ContLastName         VARCHAR2(32)                    not null,
   LogInID              NUMBER                          not null,
   AddressID            NUMBER                          not null,
   constraint PK_CONTACT primary key (ContactID)
)
;

insert into contact values ( 0, '(none)', '(none)', -9999, 0 );

/*==============================================================*/
/* Index: Indx_ContLstName                                      */
/*==============================================================*/
create index Indx_ContLstName on Contact (
   ContLastName ASC
)
;

/*==============================================================*/
/* Index: INDX_CONTID_LNAME                                     */
/*==============================================================*/
create index INDX_CONTID_LNAME on Contact (
   ContactID ASC,
   ContLastName ASC
)
;

/*==============================================================*/
/* Index: INDX_CONTID_LNAME_FNAME                               */
/*==============================================================*/
create index INDX_CONTID_LNAME_FNAME on Contact (
   ContactID ASC,
   ContFirstName ASC,
   ContLastName ASC
)
;

/*==============================================================*/
/* Table: ContactNotifGroupMap                                  */
/*==============================================================*/
create table ContactNotifGroupMap  (
   ContactID            NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              CHAR(16)                        not null,
   constraint PK_CONTACTNOTIFGROUPMAP primary key (ContactID, NotificationGroupID)
)
;

/*==============================================================*/
/* Table: ContactNotification                                   */
/*==============================================================*/
create table ContactNotification  (
   ContactNotifID       NUMBER                          not null,
   ContactID            NUMBER                          not null,
   NotificationCategoryID NUMBER                          not null,
   DisableFlag          CHAR(1)                         not null,
   Notification         VARCHAR2(130)                   not null,
   Ordering             NUMBER                          not null,
   constraint PK_CONTACTNOTIFICATION primary key (ContactNotifID)
)
;

insert into ContactNotification values( 0, 0, 0, 'N', '(none)', 0 );

/*==============================================================*/
/* Index: Indx_CntNotif_CntId                                   */
/*==============================================================*/
create index Indx_CntNotif_CntId on ContactNotification (
   ContactID ASC
)
;

/*==============================================================*/
/* Table: Customer                                              */
/*==============================================================*/
create table Customer  (
   CustomerID           NUMBER                          not null,
   PrimaryContactID     NUMBER                          not null,
   CustomerTypeID       NUMBER                          not null,
   TimeZone             VARCHAR2(40)                    not null,
   CustomerNumber       VARCHAR2(64)                    not null,
   RateScheduleID       NUMBER                          not null,
   AltTrackNum          VARCHAR2(64)                    not null,
   TemperatureUnit      CHAR(1)                         not null,
   constraint PK_CUSTOMER primary key (CustomerID)
)
;

INSERT INTO Customer VALUES ( -1, 0, 0, '(none)', '(none)', 0, '(none)', 'F');

/*==============================================================*/
/* Index: Indx_Cstmr_PcId                                       */
/*==============================================================*/
create index Indx_Cstmr_PcId on Customer (
   PrimaryContactID ASC
)
;

/*==============================================================*/
/* Index: INDX_CUSTID_PCONTID                                   */
/*==============================================================*/
create index INDX_CUSTID_PCONTID on Customer (
   CustomerID ASC,
   PrimaryContactID ASC
)
;

/*==============================================================*/
/* Table: CustomerAdditionalContact                             */
/*==============================================================*/
create table CustomerAdditionalContact  (
   CustomerID           NUMBER                          not null,
   ContactID            NUMBER                          not null,
   Ordering             NUMBER                          not null,
   constraint PK_CUSTOMERADDITIONALCONTACT primary key (ContactID, CustomerID)
)
;

/*==============================================================*/
/* Table: CustomerBaseLinePoint                                 */
/*==============================================================*/
create table CustomerBaseLinePoint  (
   CustomerID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   constraint PK_CUSTOMERBASELINEPOINT primary key (CustomerID, PointID)
)
;

/*==============================================================*/
/* Table: CustomerLoginSerialGroup                              */
/*==============================================================*/
create table CustomerLoginSerialGroup  (
   LoginID              NUMBER                          not null,
   LMGroupID            NUMBER                          not null,
   constraint PK_CUSTOMERLOGINSERIALGROUP primary key (LoginID, LMGroupID)
)
;

/*==============================================================*/
/* Table: CustomerNotifGroupMap                                 */
/*==============================================================*/
create table CustomerNotifGroupMap  (
   CustomerID           NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              CHAR(16)                        not null,
   constraint PK_CUSTOMERNOTIFGROUPMAP primary key (CustomerID, NotificationGroupID)
)
;

/*==============================================================*/
/* Table: DCCategory                                            */
/*==============================================================*/
create table DCCategory  (
   CategoryID           NUMBER                          not null,
   CategoryTypeID       NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null,
   constraint PK_DCCATEGORY primary key (CategoryID)
)
;

/*==============================================================*/
/* Table: DCCategoryItem                                        */
/*==============================================================*/
create table DCCategoryItem  (
   CategoryID           NUMBER                          not null,
   ItemTypeID           NUMBER                          not null,
   Value                VARCHAR2(40)                    not null,
   constraint PK_DCCATEGORYITEM primary key (CategoryID, ItemTypeID)
)
;

/*==============================================================*/
/* Table: DCCategoryItemType                                    */
/*==============================================================*/
create table DCCategoryItemType  (
   CategoryTypeID       NUMBER                          not null,
   ItemTypeID           NUMBER                          not null,
   constraint PK_DCCATEGORYITEMTYPE primary key (CategoryTypeID, ItemTypeID)
)
;

/*==============================================================*/
/* Table: DCCategoryType                                        */
/*==============================================================*/
create table DCCategoryType  (
   CategoryTypeID       NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null,
   DisplayName          VARCHAR2(40)                    not null,
   CategoryGroup        VARCHAR2(40),
   CategoryTypeLevel    VARCHAR2(40)                    not null,
   Description          VARCHAR2(320),
   constraint PK_DCCATEGORYTYPE primary key (CategoryTypeID)
)
;

/*==============================================================*/
/* Table: DCConfiguration                                       */
/*==============================================================*/
create table DCConfiguration  (
   ConfigID             NUMBER                          not null,
   ConfigTypeID         NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null,
   constraint PK_DCCONFIGURATION primary key (ConfigID)
)
;

/*==============================================================*/
/* Table: DCConfigurationCategory                               */
/*==============================================================*/
create table DCConfigurationCategory  (
   ConfigID             NUMBER                          not null,
   CategoryID           NUMBER                          not null,
   constraint PK_DCCONFIGURATIONCATEGORY primary key (ConfigID, CategoryID)
)
;

/*==============================================================*/
/* Table: DCConfigurationCategoryType                           */
/*==============================================================*/
create table DCConfigurationCategoryType  (
   ConfigTypeID         NUMBER                          not null,
   CategoryTypeID       NUMBER                          not null,
   constraint PK_DCCONFIGURATIONCATEGORYTYPE primary key (ConfigTypeID, CategoryTypeID)
)
;

/*==============================================================*/
/* Table: DCConfigurationType                                   */
/*==============================================================*/
create table DCConfigurationType  (
   ConfigTypeID         NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null,
   DisplayName          VARCHAR2(40)                    not null,
   Description          VARCHAR2(320),
   constraint PK_DCCONFIGURATIONTYPE primary key (ConfigTypeID)
)
;

/*==============================================================*/
/* Table: DCDeviceConfiguration                                 */
/*==============================================================*/
create table DCDeviceConfiguration  (
   DeviceID             NUMBER                          not null,
   ConfigID             NUMBER                          not null,
   constraint PK_DCDEVICECONFIGURATION primary key (DeviceID, ConfigID)
)
;

/*==============================================================*/
/* Table: DCDeviceConfigurationType                             */
/*==============================================================*/
create table DCDeviceConfigurationType  (
   ConfigTypeID         NUMBER                          not null,
   DeviceType           VARCHAR2(30)                    not null,
   constraint PK_DCDEVICECONFIGURATIONTYPE primary key (ConfigTypeID, DeviceType)
)
;

/*==============================================================*/
/* Table: DCItemType                                            */
/*==============================================================*/
create table DCItemType  (
   ItemTypeID           NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null,
   DisplayName          VARCHAR2(40)                    not null,
   ValidationType       VARCHAR2(40),
   Required             CHAR(1)                         not null,
   MinValue             NUMBER                          not null,
   MaxValue             NUMBER                          not null,
   DefaultValue         VARCHAR2(40),
   Description          VARCHAR2(320),
   constraint PK_DCITEMTYPE primary key (ItemTypeID)
)
;

/*==============================================================*/
/* Table: DCItemValue                                           */
/*==============================================================*/
create table DCItemValue  (
   ItemTypeID           NUMBER                          not null,
   Value                VARCHAR2(40)                    not null,
   ValueOrder           NUMBER                          not null,
   constraint PK_DCITEMVALUE primary key (ItemTypeID, ValueOrder)
)
;

/*==============================================================*/
/* Table: DEVICE                                                */
/*==============================================================*/
create table DEVICE  (
   DEVICEID             NUMBER                          not null,
   ALARMINHIBIT         VARCHAR2(1)                     not null,
   CONTROLINHIBIT       VARCHAR2(1)                     not null,
   constraint PK_DEV_DEVICEID2 primary key (DEVICEID)
)
;

INSERT into device values (0, 'N', 'N');

/*==============================================================*/
/* Table: DEVICE2WAYFLAGS                                       */
/*==============================================================*/
create table DEVICE2WAYFLAGS  (
   DEVICEID             NUMBER                          not null,
   MONTHLYSTATS         VARCHAR2(1)                     not null,
   TWENTYFOURHOURSTATS  VARCHAR2(1)                     not null,
   HOURLYSTATS          VARCHAR2(1)                     not null,
   FAILUREALARM         VARCHAR2(1)                     not null,
   PERFORMANCETHRESHOLD NUMBER                          not null,
   PERFORMANCEALARM     VARCHAR2(1)                     not null,
   PERFORMANCETWENTYFOURALARM VARCHAR2(1)                     not null,
   constraint PK_DEVICE2WAYFLAGS primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICECARRIERSETTINGS                                 */
/*==============================================================*/
create table DEVICECARRIERSETTINGS  (
   DEVICEID             NUMBER                          not null,
   ADDRESS              NUMBER                          not null,
   constraint PK_DEVICECARRIERSETTINGS primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICECONFIGURATION                                   */
/*==============================================================*/
create table DEVICECONFIGURATION  (
   DeviceConfigurationID NUMBER                          not null,
   Name                 VARCHAR2(30)                    not null,
   Type                 VARCHAR2(30)                    not null,
   constraint PK_DEVICECONFIGURATION primary key (DeviceConfigurationID)
)
;

/*==============================================================*/
/* Table: DEVICECONFIGURATIONDEVICEMAP                          */
/*==============================================================*/
create table DEVICECONFIGURATIONDEVICEMAP  (
   DeviceID             NUMBER                          not null,
   DeviceConfigurationId NUMBER                          not null,
   constraint PK_DEVICECONFIGURATIONDEVICEMA primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DEVICECONFIGURATIONITEM                               */
/*==============================================================*/
create table DEVICECONFIGURATIONITEM  (
   DEVICECONFIGURATIONITEMID NUMBER                          not null,
   DeviceConfigurationID NUMBER                          not null,
   FieldName            VARCHAR2(30)                    not null,
   Value                VARCHAR2(30)                    not null,
   constraint PK_DEVICECONFIGURATIONITEM primary key (DEVICECONFIGURATIONITEMID)
)
;

/*==============================================================*/
/* Table: DEVICEDIALUPSETTINGS                                  */
/*==============================================================*/
create table DEVICEDIALUPSETTINGS  (
   DEVICEID             NUMBER                          not null,
   PHONENUMBER          VARCHAR2(40)                    not null,
   MINCONNECTTIME       NUMBER                          not null,
   MAXCONNECTTIME       NUMBER                          not null,
   LINESETTINGS         VARCHAR2(8)                     not null,
   BaudRate             NUMBER                          not null,
   constraint PK_DEVICEDIALUPSETTINGS primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICEEVENT                                           */
/*==============================================================*/
create table DEVICEEVENT  (
   DeviceID             NUMBER                          not null,
   TimeStamp            DATE                            not null,
   "Comment"            VARCHAR2(50)                    not null,
   constraint PK_DEVICEEVENT primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DEVICEGROUP                                           */
/*==============================================================*/
create table DEVICEGROUP  (
   DeviceGroupId        NUMBER(18,0)                    not null,
   GroupName            VARCHAR2(255)                   not null,
   ParentDeviceGroupId  NUMBER(18,0),
   SystemGroup          CHAR(1)                         not null,
   Type                 VARCHAR2(255)                   not null,
   constraint PK_DEVICEGROUP primary key (DeviceGroupId)
)
;

insert into DeviceGroup values (0,' ',null,'Y','STATIC');
insert into DeviceGroup values (1,'Meters',0,'Y','STATIC');
insert into DeviceGroup values (2,'Billing',1,'Y','STATIC');
insert into DeviceGroup values (3,'Collection',1,'Y','STATIC');
insert into DeviceGroup values (4,'Alternate',1,'Y','STATIC');
insert into DeviceGroup values (5,'CustomGroup1',1,'Y','STATIC');
insert into DeviceGroup values (6,'CustomGroup2',1,'Y','STATIC');
insert into DeviceGroup values (7,'CustomGroup3',1,'Y','STATIC');
insert into DeviceGroup values (8,'Flags',1,'Y','STATIC');
insert into DeviceGroup values (9,'Inventory',8,'Y','STATIC');
insert into DeviceGroup values (10,'DisconnectedStatus',8,'Y','STATIC');
insert into DeviceGroup values (11,'UsageMonitoring',8,'Y','STATIC');
INSERT INTO DeviceGroup values (12,'System',0,'Y','STATIC');
INSERT INTO DeviceGroup values (13,'Routes',12,'Y','ROUTE');
INSERT INTO DeviceGroup values (14,'Device Types',12,'Y','DEVICETYPE'); 

alter table DEVICEGROUP
   add constraint AK_DEVICEGR_PDG_GN unique (GroupName, ParentDeviceGroupId)
;

/*==============================================================*/
/* Table: DEVICEGROUPMEMBER                                     */
/*==============================================================*/
create table DEVICEGROUPMEMBER  (
   DeviceGroupID        NUMBER(18,0)                    not null,
   YukonPaoId           NUMBER(18,0)                    not null,
   constraint PK_DEVICEGROUPMEMBER primary key (DeviceGroupID, YukonPaoId)
)
;

/*==============================================================*/
/* Table: DEVICEIDLCREMOTE                                      */
/*==============================================================*/
create table DEVICEIDLCREMOTE  (
   DEVICEID             NUMBER                          not null,
   ADDRESS              NUMBER                          not null,
   POSTCOMMWAIT         NUMBER                          not null,
   CCUAmpUseType        VARCHAR2(20)                    not null,
   constraint PK_DEVICEIDLCREMOTE primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICEIED                                             */
/*==============================================================*/
create table DEVICEIED  (
   DEVICEID             NUMBER                          not null,
   PASSWORD             VARCHAR2(20)                    not null,
   SLAVEADDRESS         VARCHAR2(20)                    not null,
   constraint PK_DEVICEIED primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICELOADPROFILE                                     */
/*==============================================================*/
create table DEVICELOADPROFILE  (
   DEVICEID             NUMBER                          not null,
   LASTINTERVALDEMANDRATE NUMBER                          not null,
   LOADPROFILEDEMANDRATE NUMBER                          not null,
   LOADPROFILECOLLECTION VARCHAR2(4)                     not null,
   VoltageDmdInterval   NUMBER                          not null,
   VoltageDmdRate       NUMBER                          not null,
   constraint PK_DEVICELOADPROFILE primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICEMCTIEDPORT                                      */
/*==============================================================*/
create table DEVICEMCTIEDPORT  (
   DEVICEID             NUMBER                          not null,
   CONNECTEDIED         VARCHAR2(20)                    not null,
   IEDSCANRATE          NUMBER                          not null,
   DEFAULTDATACLASS     NUMBER                          not null,
   DEFAULTDATAOFFSET    NUMBER                          not null,
   PASSWORD             VARCHAR2(6)                     not null,
   REALTIMESCAN         VARCHAR2(1)                     not null,
   constraint PK_DEVICEMCTIEDPORT primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICEMETERGROUP                                      */
/*==============================================================*/
create table DEVICEMETERGROUP  (
   DEVICEID             NUMBER                          not null,
   METERNUMBER          VARCHAR2(50)                    not null,
   constraint PK_DEVICEMETERGROUP primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DEVICEREADJOBLOG                                      */
/*==============================================================*/
create table DEVICEREADJOBLOG  (
   DeviceReadJobLogID   NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   StartTime            DATE                            not null,
   StopTime             DATE                            not null,
   constraint PK_DEVICEREADJOBLOG primary key (DeviceReadJobLogID)
)
;

/*==============================================================*/
/* Table: DEVICEREADLOG                                         */
/*==============================================================*/
create table DEVICEREADLOG  (
   DeviceReadLogID      NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   Timestamp            DATE                            not null,
   StatusCode           SMALLINT                        not null,
   DeviceReadRequestLogID NUMBER                          not null,
   constraint PK_DEVICEREADLOG primary key (DeviceReadLogID)
)
;

/*==============================================================*/
/* Table: DEVICEREADREQUESTLOG                                  */
/*==============================================================*/
create table DEVICEREADREQUESTLOG  (
   DeviceReadRequestLogID NUMBER                          not null,
   RequestID            NUMBER                          not null,
   Command              VARCHAR2(128)                   not null,
   StartTime            DATE                            not null,
   StopTime             DATE                            not null,
   DeviceReadJobLogID   NUMBER                          not null,
   constraint PK_DEVICEREADREQUESTLOG primary key (DeviceReadRequestLogID)
)
;

/*==============================================================*/
/* Table: DEVICESCANRATE                                        */
/*==============================================================*/
create table DEVICESCANRATE  (
   DEVICEID             NUMBER                          not null,
   SCANTYPE             VARCHAR2(20)                    not null,
   INTERVALRATE         NUMBER                          not null,
   SCANGROUP            NUMBER                          not null,
   AlternateRate        NUMBER                          not null,
   constraint PK_DEVICESCANRATE primary key (DEVICEID, SCANTYPE)
)
;

/*==============================================================*/
/* Table: DEVICETAPPAGINGSETTINGS                               */
/*==============================================================*/
create table DEVICETAPPAGINGSETTINGS  (
   DEVICEID             NUMBER                          not null,
   PAGERNUMBER          VARCHAR2(20)                    not null,
   Sender               VARCHAR2(64)                    not null,
   SecurityCode         VARCHAR2(64)                    not null,
   POSTPath             VARCHAR2(64)                    not null,
   constraint PK_DEVICETAPPAGINGSETTINGS primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DISPLAY                                               */
/*==============================================================*/
create table DISPLAY  (
   DISPLAYNUM           NUMBER                          not null,
   NAME                 VARCHAR2(40)                    not null,
   TYPE                 VARCHAR2(40)                    not null,
   TITLE                VARCHAR2(30),
   DESCRIPTION          VARCHAR2(200),
   constraint SYS_C0013412 primary key (DISPLAYNUM)
)
;

insert into display values(-4, 'Yukon Server', 'Static Displays', 'Yukon Servers', 'com.cannontech.tdc.windows.WinServicePanel');
insert into display values(-1, 'All Categories', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');

/** insert into display values(-3, 'All Control Areas', 'Load Management Client', 'Load Management', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel'); **/
/** insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occured.'); **/
/** insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will recieve current raw point updates as they happen in the system.'); **/


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

/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique index Indx_DISPLAYNAME on DISPLAY (
   NAME ASC
)
;

/*==============================================================*/
/* Table: DISPLAY2WAYDATA                                       */
/*==============================================================*/
create table DISPLAY2WAYDATA  (
   DISPLAYNUM           NUMBER                          not null,
   ORDERING             NUMBER                          not null,
   POINTID              NUMBER                          not null,
   constraint PK_DISPLAY2WAYDATA primary key (DISPLAYNUM, ORDERING)
)
;

/*==============================================================*/
/* Table: DISPLAYCOLUMNS                                        */
/*==============================================================*/
create table DISPLAYCOLUMNS  (
   DISPLAYNUM           NUMBER                          not null,
   TITLE                VARCHAR2(50)                    not null,
   TYPENUM              NUMBER                          not null,
   ORDERING             NUMBER                          not null,
   WIDTH                NUMBER                          not null,
   constraint PK_DISPLAYCOLUMNS primary key (DISPLAYNUM, TITLE)
)
;

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

/*==============================================================*/
/* Table: DYNAMICACCUMULATOR                                    */
/*==============================================================*/
create table DYNAMICACCUMULATOR  (
   POINTID              NUMBER                          not null,
   PREVIOUSPULSES       NUMBER                          not null,
   PRESENTPULSES        NUMBER                          not null,
   constraint PK_DYNAMICACCUMULATOR primary key (POINTID)
)
;

/*==============================================================*/
/* Table: DYNAMICBILLINGFIELD                                   */
/*==============================================================*/
create table DYNAMICBILLINGFIELD  (
   id                   NUMBER                          not null,
   FormatID             NUMBER                          not null,
   FieldName            VARCHAR2(50)                    not null,
   FieldOrder           NUMBER                          not null,
   FieldFormat          VARCHAR2(50),
   MaxLength            NUMBER                          not null,
   constraint PK_DYNAMICBILLINGFIELD primary key (id)
)
;

/* STANDARD */
insert into DynamicBillingField values(1, 31,'Plain Text', 0, 'M', 0);
insert into DynamicBillingField values(2, 31, 'meterNumber', 1, '', 0);
insert into DynamicBillingField values(3, 31, 'totalConsumption - reading',2,'#####', 0);
insert into DynamicBillingField values(4, 31, 'totalConsumption - timestamp', 3, 'HH:mm', 0);
insert into DynamicBillingField values(5, 31, 'totalConsumption - timestamp', 4, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(6, 31, 'totalPeakDemand - reading', 5, '##0.000', 0);
insert into DynamicBillingField values(7, 31, 'totalPeakDemand - timestamp', 6, 'HH:mm', 0);
insert into DynamicBillingField values(8, 31, 'totalPeakDemand - timestamp', 7, 'MM/dd/yyyy', 0);

/* SimpleTOU */
insert into DynamicBillingField values(10, 21, 'meterNumber', 0, '', 0);
insert into DynamicBillingField values(11, 21, 'totalConsumption - reading' ,1, '#####', 0);
insert into DynamicBillingField values(12, 21, 'totalConsumption - timestamp', 2, 'HH:mm', 0);
insert into DynamicBillingField values(13, 21, 'totalConsumption - timestamp', 3, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(14, 21, 'totalPeakDemand - reading', 4, '##0.000', 0);
insert into DynamicBillingField values(15, 21, 'totalPeakDemand - timestamp', 5, 'HH:mm', 0);
insert into DynamicBillingField values(16, 21, 'totalPeakDemand - timestamp', 6, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(17, 21, 'rateAConsumption - reading', 7, '#####', 0);
insert into DynamicBillingField values(18, 21, 'rateADemand - reading', 8, '##0.000', 0);
insert into DynamicBillingField values(19, 21, 'rateADemand - timestamp', 9, 'HH:mm', 0);
insert into DynamicBillingField values(20, 21, 'rateADemand - timestamp', 10, 'MM/dd/yyyy', 0);
insert into DynamicBillingField values(21, 21, 'rateBConsumption - reading', 11, '#####', 0);
insert into DynamicBillingField values(22, 21, 'rateBDemand - reading', 12, '##0.000', 0);
insert into DynamicBillingField values(23, 21, 'rateBDemand - timestamp', 13, 'HH:mm', 0);
insert into DynamicBillingField values(24, 21, 'rateBDemand - timestamp', 14, 'MM/dd/yyyy', 0); 

/*==============================================================*/
/* Table: DYNAMICBILLINGFORMAT                                  */
/*==============================================================*/
create table DYNAMICBILLINGFORMAT  (
   FormatID             NUMBER                          not null,
   Delimiter            VARCHAR2(20),
   Header               VARCHAR2(255),
   Footer               VARCHAR2(255),
   constraint PK_DYNAMICBILLINGFORMAT primary key (FormatID)
)
;

insert into DynamicBillingFormat values( 31, ',' ,'H Meter kWh Time Date Peak PeakT PeakD', '');
insert into DynamicBillingFormat values( 21, ',' ,'' ,''); 

/*==============================================================*/
/* Table: DYNAMICCCAREA                                         */
/*==============================================================*/
create table DYNAMICCCAREA  (
   AreaID               NUMBER                          not null,
   additionalflags      VARCHAR2(20)                    not null,
   constraint PK_DYNAMICCCAREA primary key (AreaID)
)
;

/*==============================================================*/
/* Table: DYNAMICCCSPECIALAREA                                  */
/*==============================================================*/
create table DYNAMICCCSPECIALAREA  (
   AreaID               NUMBER                          not null,
   additionalflags      VARCHAR2(20)                    not null
)
;

/*==============================================================*/
/* Table: DYNAMICCCSUBSTATION                                   */
/*==============================================================*/
create table DYNAMICCCSUBSTATION  (
   SubStationID         NUMBER                          not null,
   AdditionalFlags      VARCHAR2(20)                    not null,
   SAEnabledID          number                          not null,
   constraint PK_DYNAMICCCSUBSTATION primary key (SubStationID)
)
;

/*==============================================================*/
/* Table: DYNAMICCCTWOWAYCBC                                    */
/*==============================================================*/
create table DYNAMICCCTWOWAYCBC  (
   DeviceID             NUMBER                          not null,
   RecloseBlocked       CHAR(1)                         not null,
   ControlMode          CHAR(1)                         not null,
   AutoVoltControl      CHAR(1)                         not null,
   LastControl          NUMBER                          not null,
   Condition            NUMBER                          not null,
   OpFailedNeutralCurrent CHAR(1)                         not null,
   NeutralCurrentFault  CHAR(1)                         not null,
   BadRelay             CHAR(1)                         not null,
   DailyMaxOps          CHAR(1)                         not null,
   VoltageDeltaAbnormal CHAR(1)                         not null,
   TempAlarm            CHAR(1)                         not null,
   DSTActive            CHAR(1)                         not null,
   NeutralLockout       CHAR(1)                         not null,
   IgnoredIndicator     CHAR(1)                         not null,
   Voltage              FLOAT                           not null,
   HighVoltage          FLOAT                           not null,
   LowVoltage           FLOAT                           not null,
   DeltaVoltage         FLOAT                           not null,
   AnalogInputOne       NUMBER                          not null,
   Temp                 FLOAT                           not null,
   RSSI                 NUMBER                          not null,
   IgnoredReason        NUMBER                          not null,
   TotalOpCount         NUMBER                          not null,
   UvOpCount            NUMBER                          not null,
   OvOpCount            NUMBER                          not null,
   OvUvCountResetDate   DATE                            not null,
   UvSetPoint           NUMBER                          not null,
   OvSetPoint           NUMBER                          not null,
   OvUvTrackTime        NUMBER                          not null,
   LastOvUvDateTime     DATE                            not null,
   NeutralCurrentSensor NUMBER                          not null,
   NeutralCurrentAlarmSetPoint NUMBER                          not null,
   IPAddress            NUMBER                          not null,
   UDPPort              NUMBER                          not null,
   constraint PK_DYNAMICCCTWOWAYCBC primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DYNAMICDEVICESCANDATA                                 */
/*==============================================================*/
create table DYNAMICDEVICESCANDATA  (
   DEVICEID             NUMBER                          not null,
   LASTFREEZETIME       DATE                            not null,
   PREVFREEZETIME       DATE                            not null,
   LASTLPTIME           DATE                            not null,
   LASTFREEZENUMBER     NUMBER                          not null,
   PREVFREEZENUMBER     NUMBER                          not null,
   NEXTSCAN0            DATE                            not null,
   NEXTSCAN1            DATE                            not null,
   NEXTSCAN2            DATE                            not null,
   NEXTSCAN3            DATE                            not null,
   constraint PK_DYNAMICDEVICESCANDATA primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DYNAMICPAOSTATISTICSHISTORY                           */
/*==============================================================*/
create table DYNAMICPAOSTATISTICSHISTORY  (
   PAObjectID           NUMBER(18,0)                    not null,
   DateOffset           NUMBER(18,0)                    not null,
   Requests             NUMBER(18,0)                    not null,
   Completions          NUMBER(18,0)                    not null,
   Attempts             NUMBER(18,0)                    not null,
   CommErrors           NUMBER(18,0)                    not null,
   ProtocolErrors       NUMBER(18,0)                    not null,
   SystemErrors         NUMBER(18,0)                    not null,
   constraint PK_DYNAMICPAOSTATISTICSHISTORY primary key (PAObjectID, DateOffset)
)
;

/*==============================================================*/
/* Table: DYNAMICPOINTDISPATCH                                  */
/*==============================================================*/
create table DYNAMICPOINTDISPATCH  (
   POINTID              NUMBER                          not null,
   TIMESTAMP            DATE                            not null,
   QUALITY              NUMBER                          not null,
   VALUE                FLOAT                           not null,
   TAGS                 NUMBER                          not null,
   NEXTARCHIVE          DATE                            not null,
   STALECOUNT           NUMBER                          not null,
   LastAlarmLogID       NUMBER                          not null,
   millis               SMALLINT                        not null,
   constraint PK_DYNAMICPOINTDISPATCH primary key (POINTID)
)
;

/*==============================================================*/
/* Table: DateOfHoliday                                         */
/*==============================================================*/
create table DateOfHoliday  (
   HolidayScheduleID    NUMBER                          not null,
   HolidayName          VARCHAR2(20)                    not null,
   HolidayMonth         NUMBER                          not null,
   HolidayDay           NUMBER                          not null,
   HolidayYear          NUMBER                          not null,
   constraint PK_DATEOFHOLIDAY primary key (HolidayScheduleID, HolidayName)
)
;

/*==============================================================*/
/* Table: DateOfSeason                                          */
/*==============================================================*/
create table DateOfSeason  (
   SeasonScheduleID     NUMBER                          not null,
   SeasonName           VARCHAR2(20)                    not null,
   SeasonStartMonth     NUMBER                          not null,
   SeasonStartDay       NUMBER                          not null,
   SeasonEndMonth       NUMBER                          not null,
   SeasonEndDay         NUMBER                          not null,
   constraint PK_DATEOFSEASON primary key (SeasonScheduleID, SeasonName)
)
;

insert into dateOfSeason values(-1, 'Default', 1,1,12,31);

/*==============================================================*/
/* Table: DeviceAddress                                         */
/*==============================================================*/
create table DeviceAddress  (
   DeviceID             NUMBER                          not null,
   MasterAddress        NUMBER                          not null,
   SlaveAddress         NUMBER                          not null,
   PostCommWait         NUMBER                          not null,
   constraint PK_DEVICEADDRESS primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DeviceCBC                                             */
/*==============================================================*/
create table DeviceCBC  (
   DEVICEID             NUMBER                          not null,
   SERIALNUMBER         NUMBER                          not null,
   ROUTEID              NUMBER                          not null,
   constraint PK_DEVICECBC primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DeviceCustomerList                                    */
/*==============================================================*/
create table DeviceCustomerList  (
   CustomerID           NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   constraint PK_DEVICECUSTOMERLIST primary key (DeviceID, CustomerID)
)
;

/*==============================================================*/
/* Table: DeviceDirectCommSettings                              */
/*==============================================================*/
create table DeviceDirectCommSettings  (
   DEVICEID             NUMBER                          not null,
   PORTID               NUMBER                          not null,
   constraint PK_DEVICEDIRECTCOMMSETTINGS primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: DeviceMCT400Series                                    */
/*==============================================================*/
create table DeviceMCT400Series  (
   DeviceID             NUMBER                          not null,
   DisconnectAddress    NUMBER                          not null,
   TOUScheduleID        NUMBER                          not null,
   constraint PK_DEV400S primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DevicePagingReceiverSettings                          */
/*==============================================================*/
create table DevicePagingReceiverSettings  (
   DeviceID             NUMBER                          not null,
   CapCode1             NUMBER                          not null,
   CapCode2             NUMBER                          not null,
   CapCode3             NUMBER                          not null,
   CapCode4             NUMBER                          not null,
   CapCode5             NUMBER                          not null,
   CapCode6             NUMBER                          not null,
   CapCode7             NUMBER                          not null,
   CapCode8             NUMBER                          not null,
   CapCode9             NUMBER                          not null,
   CapCode10            NUMBER                          not null,
   CapCode11            NUMBER                          not null,
   CapCode12            NUMBER                          not null,
   CapCode13            NUMBER                          not null,
   CapCode14            NUMBER                          not null,
   CapCode15            NUMBER                          not null,
   CapCode16            NUMBER                          not null,
   Frequency            FLOAT                           not null,
   constraint PK_DEVICEPAGINGRECEIVERSETTING primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DeviceRTC                                             */
/*==============================================================*/
create table DeviceRTC  (
   DeviceID             NUMBER                          not null,
   RTCAddress           NUMBER                          not null,
   Response             VARCHAR2(1)                     not null,
   LBTMode              NUMBER                          not null,
   DisableVerifies      VARCHAR2(1)                     not null,
   constraint PK_DEVICERTC primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DeviceRoutes                                          */
/*==============================================================*/
create table DeviceRoutes  (
   DEVICEID             NUMBER                          not null,
   ROUTEID              NUMBER                          not null,
   constraint PK_DEVICEROUTES primary key (DEVICEID, ROUTEID)
)
;

/*==============================================================*/
/* Table: DeviceSeries5RTU                                      */
/*==============================================================*/
create table DeviceSeries5RTU  (
   DeviceID             NUMBER                          not null,
   TickTime             NUMBER                          not null,
   TransmitOffset       NUMBER                          not null,
   SaveHistory          CHAR(1)                         not null,
   PowerValueHighLimit  NUMBER                          not null,
   PowerValueLowLimit   NUMBER                          not null,
   PowerValueMultiplier FLOAT                           not null,
   PowerValueOffset     FLOAT                           not null,
   StartCode            NUMBER                          not null,
   StopCode             NUMBER                          not null,
   Retries              NUMBER                          not null,
   constraint PK_DEVICESERIES5RTU primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DeviceTNPPSettings                                    */
/*==============================================================*/
create table DeviceTNPPSettings  (
   DeviceID             NUMBER                          not null,
   Inertia              NUMBER                          not null,
   DestinationAddress   NUMBER                          not null,
   OriginAddress        NUMBER                          not null,
   IdentifierFormat     CHAR(1)                         not null,
   Protocol             VARCHAR2(32)                    not null,
   DataFormat           CHAR(1)                         not null,
   Channel              CHAR(1)                         not null,
   Zone                 CHAR(1)                         not null,
   FunctionCode         CHAR(1)                         not null,
   PagerID              NUMBER                          not null,
   constraint PK_DEVICETNPPSETTINGS primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DeviceTypeCommand                                     */
/*==============================================================*/
create table DeviceTypeCommand  (
   DeviceCommandID      NUMBER                          not null,
   CommandID            NUMBER                          not null,
   DeviceType           VARCHAR2(32)                    not null,
   DisplayOrder         NUMBER                          not null,
   VisibleFlag          CHAR(1)                         not null,
   CommandGroupID       NUMBER                          not null,
   constraint PK_DEVICETYPECOMMAND primary key (DeviceCommandID, CommandGroupID)
)
;

INSERT INTO DEVICETYPECOMMAND VALUES (0, 0, 'System', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-1, 0, 'CICustomer', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-2, 0, 'Davis Weather', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-3, 0, 'Fulcrum', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-4, 0, 'Landis-Gyr S4', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-5, 0, 'MCT Broadcast', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-6, 0, 'Sixnet', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-7, 0, 'Tap Terminal', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-9, 0, 'SA205Serial', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-10, 0, 'SA305Serial', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-11, 0, 'CollectionGroup', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-12, 0, 'TestCollectionGroup', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-13, -1, 'LMT-2', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-14, -2, 'LMT-2', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-15, -3, 'LMT-2', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-16, -4, 'LMT-2', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-17, -5, 'LMT-2', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-18, -6, 'LMT-2', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-19, -7, 'LMT-2', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-20, -8, 'LMT-2', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-21, -9, 'LMT-2', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-22, -10, 'LMT-2', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-23, -27, 'Macro Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-24, -28, 'Macro Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-25, -29, 'Alpha A1', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-26, -29, 'Alpha Power Plus', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-27, -30, 'CBC 6510', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-28, -31, 'CBC 6510', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-29, -32, 'CBC 6510', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-30, -33, 'CBC 6510', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-31, -30, 'CBC FP-2800', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-32, -31, 'CBC FP-2800', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-33, -32, 'CBC FP-2800', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-34, -33, 'CBC FP-2800', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-35, -30, 'CBC Versacom', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-36, -31, 'CBC Versacom', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-37, -32, 'CBC Versacom', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-38, -33, 'CBC Versacom', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-39, -30, 'Cap Bank', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-40, -31, 'Cap Bank', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-41, -32, 'Cap Bank', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-42, -33, 'Cap Bank', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-43, -34, 'CCU-710A', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-44, -35, 'CCU-710A', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-45, -36, 'CCU-710A', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-46, -34, 'CCU-711', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-47, -35, 'CCU-711', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-48, -36, 'CCU-711', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-49, -37, 'CCU-711', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-50, -38, 'Emetcon Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-51, -39, 'Emetcon Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-52, -40, 'Emetcon Group', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-53, -41, 'Emetcon Group', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-54, -42, 'Emetcon Group', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-55, -43, 'Emetcon Group', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-56, -27, 'Expresscom Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-57, -28, 'Expresscom Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-58, -27, 'Golay Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-59, -28, 'Golay Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-60, -27, 'Macro Group', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-61, -28, 'Macro Group', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-62, -27, 'Ripple Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-63, -28, 'Ripple Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-64, -27, 'SA-205 Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-65, -28, 'SA-205 Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-66, -27, 'SA-305 Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-67, -28, 'SA-305 Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-68, -27, 'SA-Digital Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-69, -28, 'SA-Digital Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-70, -27, 'Versacom Group', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-71, -28, 'Versacom Group', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-72, -44, 'Versacom Group', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-73, -45, 'Versacom Group', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-74, -95, 'Versacom Group', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-75, -96, 'Versacom Group', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-76, -48, 'Versacom Group', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-77, -97, 'Versacom Group', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-78, -34, 'TCU-5000', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-79, -35, 'TCU-5000', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-80, -36, 'TCU-5000', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-81, -34, 'TCU-5500', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-82, -35, 'TCU-5500', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-83, -36, 'TCU-5500', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-84, -52, 'Repeater 800', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-85, -3, 'Repeater 800', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-86, -53, 'Repeater 800', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-87, -54, 'Repeater 800', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-88, -52, 'Repeater', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-89, -3, 'Repeater', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-90, -53, 'Repeater', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-91, -54, 'Repeater', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-92, -34, 'RTU-DART', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-93, -35, 'RTU-DART', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-94, -36, 'RTU-DART', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-95, -34, 'RTU-DNP', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-96, -35, 'RTU-DNP', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-97, -36, 'RTU-DNP', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-98, -34, 'RTU-ILEX', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-99, -35, 'RTU-ILEX', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-100, -36, 'RTU-ILEX', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-101, -34, 'RTU-WELCO', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-102, -35, 'RTU-WELCO', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-103, -36, 'RTU-WELCO', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-104, -55, 'ION-7330', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-105, -56, 'ION-7330', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-106, -57, 'ION-7330', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-107, -55, 'ION-7700', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-108, -56, 'ION-7700', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-109, -57, 'ION-7700', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-110, -55, 'ION-8300', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-111, -56, 'ION-8300', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-112, -57, 'ION-8300', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-113, -34, 'LCU-415', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-114, -35, 'LCU-415', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-115, -36, 'LCU-415', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-116, -34, 'LCU-EASTRIVER', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-117, -35, 'LCU-EASTRIVER', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-118, -36, 'LCU-EASTRIVER', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-119, -34, 'LCU-LG', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-120, -35, 'LCU-LG', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-121, -36, 'LCU-LG', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-122, -34, 'LCU-T3026', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-123, -35, 'LCU-T3026', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-124, -36, 'LCU-T3026', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-125, -63, 'ExpresscomSerial', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-126, -64, 'ExpresscomSerial', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-127, -65, 'ExpresscomSerial', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-128, -66, 'ExpresscomSerial', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-129, -67, 'ExpresscomSerial', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-130, -68, 'ExpresscomSerial', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-131, -69, 'ExpresscomSerial', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-132, -85, 'ExpresscomSerial', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-133, -86, 'ExpresscomSerial', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-134, -87, 'ExpresscomSerial', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-135, -88, 'ExpresscomSerial', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-136, -89, 'ExpresscomSerial', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-137, -90, 'ExpresscomSerial', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-138, -91, 'ExpresscomSerial', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-139, -92, 'ExpresscomSerial', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-140, -93, 'ExpresscomSerial', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-141, -94, 'ExpresscomSerial', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-142, -95, 'ExpresscomSerial', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-143, -96, 'ExpresscomSerial', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-144, -97, 'ExpresscomSerial', 20, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-147, -1, 'MCT-210', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-148, -2, 'MCT-210', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-149, -3, 'MCT-210', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-150, -4, 'MCT-210', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-151, -5, 'MCT-210', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-152, -6, 'MCT-210', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-153, -7, 'MCT-210', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-154, -8, 'MCT-210', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-155, -9, 'MCT-210', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-156, -10, 'MCT-210', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-157, -1, 'MCT-213', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-158, -2, 'MCT-213', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-159, -3, 'MCT-213', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-160, -4, 'MCT-213', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-161, -5, 'MCT-213', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-162, -6, 'MCT-213', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-163, -7, 'MCT-213', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-164, -8, 'MCT-213', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-165, -9, 'MCT-213', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-166, -10, 'MCT-213', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-167, -11, 'MCT-213', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-168, -12, 'MCT-213', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-169, -13, 'MCT-213', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-170, -1, 'MCT-240', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-171, -2, 'MCT-240', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-172, -3, 'MCT-240', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-173, -4, 'MCT-240', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-174, -5, 'MCT-240', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-175, -6, 'MCT-240', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-176, -7, 'MCT-240', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-177, -8, 'MCT-240', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-178, -9, 'MCT-240', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-179, -10, 'MCT-240', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-180, -15, 'MCT-240', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-181, -18, 'MCT-240', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-182, -19, 'MCT-240', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-183, -1, 'MCT-248', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-184, -2, 'MCT-248', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-185, -3, 'MCT-248', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-186, -4, 'MCT-248', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-187, -5, 'MCT-248', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-188, -6, 'MCT-248', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-189, -7, 'MCT-248', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-190, -8, 'MCT-248', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-191, -9, 'MCT-248', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-192, -10, 'MCT-248', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-193, -15, 'MCT-248', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-194, -18, 'MCT-248', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-195, -19, 'MCT-248', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-196, -1, 'MCT-250', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-197, -2, 'MCT-250', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-198, -3, 'MCT-250', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-199, -4, 'MCT-250', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-200, -5, 'MCT-250', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-201, -6, 'MCT-250', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-202, -7, 'MCT-250', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-203, -8, 'MCT-250', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-204, -9, 'MCT-250', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-205, -10, 'MCT-250', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-206, -14, 'MCT-250', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-207, -15, 'MCT-250', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-208, -18, 'MCT-250', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-209, -19, 'MCT-250', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-210, -1, 'MCT-310', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-211, -2, 'MCT-310', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-212, -3, 'MCT-310', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-213, -4, 'MCT-310', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-214, -5, 'MCT-310', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-215, -6, 'MCT-310', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-216, -7, 'MCT-310', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-217, -8, 'MCT-310', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-218, -9, 'MCT-310', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-219, -10, 'MCT-310', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-220, -1, 'MCT-310CT', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-221, -2, 'MCT-310CT', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-222, -3, 'MCT-310CT', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-223, -4, 'MCT-310CT', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-224, -5, 'MCT-310CT', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-225, -6, 'MCT-310CT', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-226, -7, 'MCT-310CT', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-227, -8, 'MCT-310CT', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-228, -9, 'MCT-310CT', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-229, -10, 'MCT-310CT', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-230, -1, 'MCT-310IDL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-231, -2, 'MCT-310IDL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-232, -3, 'MCT-310IDL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-233, -4, 'MCT-310IDL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-234, -5, 'MCT-310IDL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-235, -6, 'MCT-310IDL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-236, -7, 'MCT-310IDL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-237, -8, 'MCT-310IDL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-238, -9, 'MCT-310IDL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-239, -10, 'MCT-310IDL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-240, -1, 'MCT-310ID', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-241, -2, 'MCT-310ID', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-242, -3, 'MCT-310ID', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-243, -4, 'MCT-310ID', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-244, -5, 'MCT-310ID', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-245, -6, 'MCT-310ID', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-246, -7, 'MCT-310ID', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-247, -8, 'MCT-310ID', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-248, -9, 'MCT-310ID', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-249, -10, 'MCT-310ID', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-250, -11, 'MCT-310ID', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-251, -12, 'MCT-310ID', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-252, -13, 'MCT-310ID', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-253, -1, 'MCT-310IL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-254, -2, 'MCT-310IL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-255, -3, 'MCT-310IL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-256, -4, 'MCT-310IL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-257, -5, 'MCT-310IL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-258, -6, 'MCT-310IL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-259, -7, 'MCT-310IL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-260, -8, 'MCT-310IL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-261, -9, 'MCT-310IL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-262, -10, 'MCT-310IL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-263, -15, 'MCT-310IL', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-264, -18, 'MCT-310IL', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-265, -19, 'MCT-310IL', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-266, -1, 'MCT-318', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-267, -2, 'MCT-318', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-268, -3, 'MCT-318', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-269, -4, 'MCT-318', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-270, -5, 'MCT-318', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-271, -6, 'MCT-318', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-272, -7, 'MCT-318', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-273, -8, 'MCT-318', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-274, -9, 'MCT-318', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-275, -10, 'MCT-318', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-276, -14, 'MCT-318', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-277, -1, 'MCT-318L', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-278, -2, 'MCT-318L', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-279, -3, 'MCT-318L', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-280, -4, 'MCT-318L', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-281, -5, 'MCT-318L', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-282, -6, 'MCT-318L', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-283, -7, 'MCT-318L', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-284, -8, 'MCT-318L', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-285, -9, 'MCT-318L', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-286, -10, 'MCT-318L', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-287, -14, 'MCT-318L', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-288, -1, 'MCT-360', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-289, -2, 'MCT-360', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-290, -3, 'MCT-360', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-291, -4, 'MCT-360', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-292, -5, 'MCT-360', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-293, -6, 'MCT-360', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-294, -7, 'MCT-360', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-295, -8, 'MCT-360', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-296, -9, 'MCT-360', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-297, -10, 'MCT-360', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-298, -14, 'MCT-360', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-299, -26, 'MCT-360', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-300, -1, 'MCT-370', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-301, -2, 'MCT-370', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-302, -3, 'MCT-370', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-303, -4, 'MCT-370', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-304, -5, 'MCT-370', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-305, -6, 'MCT-370', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-306, -7, 'MCT-370', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-307, -8, 'MCT-370', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-308, -9, 'MCT-370', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-309, -10, 'MCT-370', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-310, -14, 'MCT-370', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-311, -20, 'MCT-370', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-312, -21, 'MCT-370', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-313, -22, 'MCT-370', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-314, -23, 'MCT-370', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-315, -24, 'MCT-370', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-316, -25, 'MCT-370', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-317, -26, 'MCT-370', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-318, -1, 'MCT-410IL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-319, -81, 'MCT-410IL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-320, -3, 'MCT-410IL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-321, -6, 'MCT-410IL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-322, -34, 'MCT-410IL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-323, -82, 'MCT-410IL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-324, -18, 'MCT-410IL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-325, -19, 'MCT-410IL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-326, -83, 'MCT-410IL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-327, -84, 'MCT-410IL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-328, -70, 'LCRSerial', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-329, -85, 'LCRSerial', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-330, -86, 'LCRSerial', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-331, -87, 'LCRSerial', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-332, -88, 'LCRSerial', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-333, -89, 'LCRSerial', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-334, -90, 'LCRSerial', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-335, -91, 'LCRSerial', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-336, -92, 'LCRSerial', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-337, -93, 'LCRSerial', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-338, -94, 'LCRSerial', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-339, -95, 'LCRSerial', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-340, -96, 'LCRSerial', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-341, -97, 'LCRSerial', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-342, -48, 'LCRSerial', 15, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-343, -71, 'VersacomSerial', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-344, -72, 'VersacomSerial', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-345, -73, 'VersacomSerial', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-346, -74, 'VersacomSerial', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-347, -75, 'VersacomSerial', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-348, -85, 'VersacomSerial', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-349, -86, 'VersacomSerial', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-350, -87, 'VersacomSerial', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-351, -88, 'VersacomSerial', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-352, -89, 'VersacomSerial', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-353, -90, 'VersacomSerial', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-354, -91, 'VersacomSerial', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-355, -92, 'VersacomSerial', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-356, -93, 'VersacomSerial', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-357, -94, 'VersacomSerial', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-358, -95, 'VersacomSerial', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-359, -96, 'VersacomSerial', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-360, -97, 'VersacomSerial', 18, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-361, -11, 'MCT-410IL', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-362, -12, 'MCT-410IL', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-363, -13, 'MCT-410IL', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-364, -1, 'MCT-410CL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-365, -81, 'MCT-410CL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-366, -3, 'MCT-410CL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-367, -6, 'MCT-410CL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-368, -34, 'MCT-410CL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-369, -82, 'MCT-410CL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-370, -18, 'MCT-410CL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-371, -19, 'MCT-410CL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-372, -83, 'MCT-410CL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-373, -84, 'MCT-410CL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-374, -11, 'MCT-410CL', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-375, -12, 'MCT-410CL', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-376, -13, 'MCT-410CL', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-377, -30, 'MCT-248', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-378, -31, 'MCT-248', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-379, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-380, -99, 'All MCT-4xx Series', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-381, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-382, -99, 'All MCT-4xx Series', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-383, -100, 'SENTINEL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-384, -101, 'SENTINEL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-385, -102, 'SENTINEL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-386, -103, 'SENTINEL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-387, -104, 'SENTINEL', 5, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-388, -105, 'All MCT-4xx Series', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-389, -106, 'MCT-410IL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-390, -107, 'MCT-410IL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-391, -108, 'MCT-410IL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-392, -109, 'All MCT-4xx Series', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-393, -108, 'MCT-410IL', 21, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-394, -111, 'MCT-410IL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-395, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-396, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-397, -114, 'MCT-410IL', 25, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-398, -105, 'All MCT-4xx Series', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-399, -106, 'MCT-410CL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-400, -107, 'MCT-410CL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-401, -108, 'MCT-410CL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-402, -109, 'All MCT-4xx Series', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-404, -111, 'MCT-410CL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-405, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-406, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-407, -114, 'MCT-410CL', 25, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-408, -1, 'MCT-470', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-409, -2, 'MCT-470', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-410, -3, 'MCT-470', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-411, -20, 'MCT-470', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-412, -21, 'MCT-470', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-413, -22, 'MCT-470', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-414, -115, 'MCT-470', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-415, -116, 'MCT-470', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-416, -117, 'MCT-470', 9, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-417, -118, 'MCT-470', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-418, -119, 'MCT-470', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-419, -120, 'MCT-470', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-420, -121, 'MCT-470', 13, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-421, -122, 'MCT-470', 14, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-422, -123, 'MCT-470', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-423, -111, 'MCT-470', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-424, -7, 'MCT-470', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-425, -8, 'MCT-470', 18, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-426, -3, 'MCT-430A', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-427, -20, 'MCT-430A', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-428, -21, 'MCT-430A', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-429, -22, 'MCT-430A', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-430, -115, 'MCT-430A', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-431, -116, 'MCT-430A', 6, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-432, -117, 'MCT-430A', 7, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-433, -118, 'MCT-430A', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-434, -119, 'MCT-430A', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-435, -120, 'MCT-430A', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-436, -121, 'MCT-430A', 11, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-437, -122, 'MCT-430A', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-448, -123, 'MCT-430A', 13, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-449, -3, 'MCT-430S4', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-450, -20, 'MCT-430S4', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-451, -21, 'MCT-430S4', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-452, -22, 'MCT-430S4', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-453, -115, 'MCT-430S4', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-454, -116, 'MCT-430S4', 6, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-455, -117, 'MCT-430S4', 7, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-456, -118, 'MCT-430S4', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-457, -119, 'MCT-430S4', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-458, -120, 'MCT-430S4', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-459, -121, 'MCT-430S4', 11, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-460, -122, 'MCT-430S4', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-461, -123, 'MCT-430S4', 13, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-462, -30, 'CBC Expresscom', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-463, -31, 'CBC Expresscom', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-464, -32, 'CBC Expresscom', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-465, -33, 'CBC Expresscom', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-466, -30, 'CBC 7010', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-467, -31, 'CBC 7010', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-468, -32, 'CBC 7010', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-469, -33, 'CBC 7010', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-470, -30, 'CBC 7011', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-471, -31, 'CBC 7011', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-472, -32, 'CBC 7011', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-473, -33, 'CBC 7011', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-474, -30, 'CBC 7011', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-475, -31, 'CBC 7011', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-476, -32, 'CBC 7011', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-477, -33, 'CBC 7011', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-478, -30, 'CBC 7012', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-479, -31, 'CBC 7012', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-480, -32, 'CBC 7012', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-481, -33, 'CBC 7012', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-482, -30, 'CBC 7020', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-483, -31, 'CBC 7020', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-484, -32, 'CBC 7020', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-485, -33, 'CBC 7020', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-486, -30, 'CBC 7022', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-487, -31, 'CBC 7022', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-488, -32, 'CBC 7022', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-489, -33, 'CBC 7022', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-490, -30, 'CBC 7023', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-491, -31, 'CBC 7023', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-492, -32, 'CBC 7023', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-493, -33, 'CBC 7023', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-494, -30, 'CBC 7024', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-495, -31, 'CBC 7024', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-496, -32, 'CBC 7024', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-497, -33, 'CBC 7024', 4, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-498, -124, 'VersacomSerial', 19, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-499, -125, 'VersacomSerial', 20, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-500, -126, 'VersacomSerial', 21, 'N', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-501, -34, 'CCU-721', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-502, -35, 'CCU-721', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-503, -36, 'CCU-721', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-504, -37, 'CCU-721', 4, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-505, -1, 'MCT-410FL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-506, -81, 'MCT-410FL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-507, -3, 'MCT-410FL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-508, -6, 'MCT-410FL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-509, -34, 'MCT-410FL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-510, -82, 'MCT-410FL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-511, -18, 'MCT-410FL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-512, -19, 'MCT-410FL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-513, -83, 'MCT-410FL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-514, -84, 'MCT-410FL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-515, -11, 'MCT-410FL', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-516, -12, 'MCT-410FL', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-517, -13, 'MCT-410FL', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-518, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-519, -99, 'All MCT-4xx Series', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-520, -105, 'All MCT-4xx Series', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-521, -106, 'MCT-410FL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-522, -107, 'MCT-410FL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-523, -108, 'MCT-410FL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-524, -109, 'All MCT-4xx Series', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-526, -111, 'MCT-410FL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-527, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-528, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-529, -114, 'MCT-410FL', 25, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-530, -1, 'MCT-410GL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-531, -81, 'MCT-410GL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-532, -3, 'MCT-410GL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-533, -6, 'MCT-410GL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-534, -34, 'MCT-410GL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-535, -82, 'MCT-410GL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-536, -18, 'MCT-410GL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-537, -19, 'MCT-410GL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-538, -83, 'MCT-410GL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-539, -84, 'MCT-410GL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-540, -11, 'MCT-410GL', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-541, -12, 'MCT-410GL', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-542, -13, 'MCT-410GL', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-543, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-544, -99, 'All MCT-4xx Series', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-545, -105, 'All MCT-4xx Series', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-546, -106, 'MCT-410GL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-547, -107, 'MCT-410GL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-548, -108, 'MCT-410GL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-549, -109, 'All MCT-4xx Series', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-551, -111, 'MCT-410GL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-552, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-553, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-554, -114, 'MCT-410GL', 25, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-555, -3, 'MCT-430SL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-556, -20, 'MCT-430SL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-557, -21, 'MCT-430SL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-558, -22, 'MCT-430SL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-559, -115, 'MCT-430SL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-560, -116, 'MCT-430SL', 6, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-561, -117, 'MCT-430SL', 7, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-562, -118, 'MCT-430SL', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-563, -119, 'MCT-430SL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-564, -120, 'MCT-430SL', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-565, -121, 'MCT-430SL', 11, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-566, -122, 'MCT-430SL', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-567, -123, 'MCT-430SL', 13, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-568, -52, 'Repeater 801', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-569, -3, 'Repeater 801', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-570, -53, 'Repeater 801', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-571, -54, 'Repeater 801', 4, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-572, -52, 'Repeater 921', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-573, -3, 'Repeater 921', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-574, -53, 'Repeater 921', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-575, -54, 'Repeater 921', 4, 'Y', -1);

insert into devicetypecommand values(-576, -127, 'VersacomSerial', 22, 'N', -1);
insert into devicetypecommand values(-577, -128, 'VersacomSerial', 23, 'N', -1);
insert into devicetypecommand values(-578, -129, 'VersacomSerial', 24, 'N', -1);

insert into devicetypecommand values (-581, -83, 'MCT-470', 23, 'Y', -1);
insert into devicetypecommand values (-582, -6, 'MCT-470', 24, 'Y', -1);
insert into devicetypecommand values (-583, -34, 'MCT-470', 25, 'Y', -1);

insert into devicetypecommand values (-584, -15, 'LMT-2', 11, 'Y', -1);
insert into devicetypecommand values (-585, -18, 'LMT-2', 12, 'Y', -1);
insert into devicetypecommand values (-586, -19, 'LMT-2', 13, 'Y', -1);

insert into devicetypecommand values (-587, -15, 'DCT-501', 1, 'Y', -1);
insert into devicetypecommand values (-588, -18, 'DCT-501', 2, 'Y', -1);
insert into devicetypecommand values (-589, -19, 'DCT-501', 3, 'Y', -1);

insert into devicetypecommand values (-590, -15, 'MCT-310IDL', 11, 'Y', -1);
insert into devicetypecommand values (-591, -18, 'MCT-310IDL', 12, 'Y', -1);
insert into devicetypecommand values (-592, -19, 'MCT-310IDL', 13, 'Y', -1);

insert into devicetypecommand values (-593, -15, 'MCT-310CT', 11, 'Y', -1);
insert into devicetypecommand values (-594, -18, 'MCT-310CT', 12, 'Y', -1);
insert into devicetypecommand values (-595, -19, 'MCT-310CT', 13, 'Y', -1);

insert into devicetypecommand values (-596, -15, 'MCT-310IM', 1, 'Y', -1);
insert into devicetypecommand values (-597, -18, 'MCT-310IM', 2, 'Y', -1);
insert into devicetypecommand values (-598, -19, 'MCT-310IM', 3, 'Y', -1);

insert into devicetypecommand values (-599, -15, 'MCT-318L', 12, 'Y', -1);
insert into devicetypecommand values (-600, -18, 'MCT-318L', 13, 'Y', -1);
insert into devicetypecommand values (-601, -19, 'MCT-318L', 14, 'Y', -1);

insert into devicetypecommand values (-602, -15, 'MCT-410CL', 26, 'Y', -1);

insert into devicetypecommand values (-603, -15, 'MCT-410FL', 26, 'Y', -1);

insert into devicetypecommand values (-604, -15, 'MCT-410GL', 26, 'Y', -1);

insert into devicetypecommand values (-605, -15, 'MCT-410IL', 26, 'Y', -1);

insert into devicetypecommand values (-606, -15, 'MCT-430A', 14, 'Y', -1);
insert into devicetypecommand values (-607, -18, 'MCT-430A', 15, 'Y', -1);
insert into devicetypecommand values (-608, -19, 'MCT-430A', 16, 'Y', -1);

insert into devicetypecommand values (-609, -15, 'MCT-430SL', 14, 'Y', -1);
insert into devicetypecommand values (-610, -18, 'MCT-430SL', 15, 'Y', -1);
insert into devicetypecommand values (-611, -19, 'MCT-430SL', 16, 'Y', -1);

insert into devicetypecommand values (-612, -15, 'MCT-430S4', 14, 'Y', -1);
insert into devicetypecommand values (-613, -18, 'MCT-430S4', 15, 'Y', -1);
insert into devicetypecommand values (-614, -19, 'MCT-430S4', 16, 'Y', -1);

insert into devicetypecommand values (-615, -15, 'MCT-470', 21, 'Y', -1);
insert into devicetypecommand values (-616, -18, 'MCT-470', 22, 'Y', -1);
insert into devicetypecommand values (-617, -19, 'MCT-470', 23, 'Y', -1);

insert into devicetypecommand values (-618, -6, 'MCT-430A', 17, 'Y', -1);
insert into devicetypecommand values (-619, -34, 'MCT-430A', 18, 'Y', -1);

insert into devicetypecommand values (-620, -6, 'MCT-430SL', 17, 'Y', -1);
insert into devicetypecommand values (-621, -34, 'MCT-430SL', 18, 'Y', -1);

insert into devicetypecommand values (-622, -6, 'MCT-430S4', 17, 'Y', -1);
insert into devicetypecommand values (-623, -34, 'MCT-430S4', 18, 'Y', -1);

insert into devicetypecommand values (-624, -105, 'MCT-430A', 19, 'Y', -1);
insert into devicetypecommand values (-625, -109, 'MCT-430A', 20, 'Y', -1);
insert into devicetypecommand values (-626, -112, 'MCT-430A', 21, 'Y', -1);
insert into devicetypecommand values (-627, -113, 'MCT-430A', 22, 'Y', -1);

insert into devicetypecommand values (-628, -105, 'MCT-430SL', 19, 'Y', -1);
insert into devicetypecommand values (-629, -109, 'MCT-430SL', 20, 'Y', -1);
insert into devicetypecommand values (-630, -112, 'MCT-430SL', 21, 'Y', -1);
insert into devicetypecommand values (-631, -113, 'MCT-430SL', 22, 'Y', -1);

insert into devicetypecommand values (-632, -105, 'MCT-430S4', 19, 'Y', -1);
insert into devicetypecommand values (-633, -109, 'MCT-430S4', 20, 'Y', -1);
insert into devicetypecommand values (-634, -112, 'MCT-430S4', 21, 'Y', -1);
insert into devicetypecommand values (-635, -113, 'MCT-430S4', 22, 'Y', -1);

insert into devicetypecommand values (-636, -105, 'MCT-470', 26, 'Y', -1);
insert into devicetypecommand values (-637, -109, 'MCT-470', 27, 'Y', -1);
insert into devicetypecommand values (-638, -112, 'MCT-470', 28, 'Y', -1);
insert into devicetypecommand values (-639, -113, 'MCT-470', 29, 'Y', -1);

insert into devicetypecommand values(-640, -130, 'MCT-410 kWh Only', 21, 'N', -1);
insert into devicetypecommand values(-641, -130, 'MCT-410CL', 27, 'N', -1);
insert into devicetypecommand values(-642, -130, 'MCT-410FL', 27, 'N', -1);
insert into devicetypecommand values(-643, -130, 'MCT-410GL', 27, 'N', -1);
insert into devicetypecommand values(-644, -130, 'MCT-410IL', 27, 'N', -1);
insert into devicetypecommand values(-645, -130, 'MCT-410iLE', 21, 'N', -1);
insert into devicetypecommand values(-646, -130, 'MCT-430A', 23, 'N', -1);
insert into devicetypecommand values(-647, -130, 'MCT-430S4', 23, 'N', -1);
insert into devicetypecommand values(-648, -130, 'MCT-430SL', 23, 'N', -1);
insert into devicetypecommand values(-649, -130, 'MCT-470', 29, 'N', -1);

insert into devicetypecommand values(-650, -131, 'MCT-410 kWh Only', 22, 'N', -1);
insert into devicetypecommand values(-651, -131, 'MCT-410CL', 28, 'N', -1);
insert into devicetypecommand values(-652, -131, 'MCT-410FL', 28, 'N', -1);
insert into devicetypecommand values(-653, -131, 'MCT-410GL', 28, 'N', -1);
insert into devicetypecommand values(-654, -131, 'MCT-410IL', 28, 'N', -1);
insert into devicetypecommand values(-655, -131, 'MCT-410iLE', 22, 'N', -1);
insert into devicetypecommand values(-656, -131, 'MCT-430A', 24, 'N', -1);
insert into devicetypecommand values(-657, -131, 'MCT-430S4', 24, 'N', -1);
insert into devicetypecommand values(-658, -131, 'MCT-430SL', 24, 'N', -1);
insert into devicetypecommand values(-659, -131, 'MCT-470', 30, 'N', -1);

insert into devicetypecommand values(-660, -132, 'MCT-410 kWh Only', 23, 'N', -1);
insert into devicetypecommand values(-661, -132, 'MCT-410CL', 29, 'N', -1);
insert into devicetypecommand values(-662, -132, 'MCT-410FL', 29, 'N', -1);
insert into devicetypecommand values(-663, -132, 'MCT-410GL', 29, 'N', -1);
insert into devicetypecommand values(-664, -132, 'MCT-410IL', 29, 'N', -1);
insert into devicetypecommand values(-665, -132, 'MCT-410iLE', 23, 'N', -1);
insert into devicetypecommand values(-666, -132, 'MCT-430A', 25, 'N', -1);
insert into devicetypecommand values(-667, -132, 'MCT-430S4', 25, 'N', -1);
insert into devicetypecommand values(-668, -132, 'MCT-430SL', 25, 'N', -1);
insert into devicetypecommand values(-669, -132, 'MCT-470', 31, 'N', -1);

insert into DeviceTypeCommand values (-670, -133, 'ExpresscomSerial', 21, 'Y', -1);
insert into DeviceTypeCommand values (-671, -134, 'ExpresscomSerial', 22, 'Y', -1);
insert into DeviceTypeCommand values (-672, -135, 'ExpresscomSerial', 23, 'Y', -1);
insert into DeviceTypeCommand values (-673, -133, 'Expresscom Group', 3, 'Y', -1);
insert into DeviceTypeCommand values (-674, -134, 'Expresscom Group', 4, 'Y', -1);
insert into DeviceTypeCommand values (-675, -135, 'Expresscom Group', 5, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-676, -111, 'MCT-430A', 26, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-677, -2, 'MCT-430A', 27, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-678, -83, 'MCT-430A', 28, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-679, -136, 'MCT-430A', 29, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-680, -111, 'MCT-430S4', 26, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-681, -2, 'MCT-430S4', 27, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-682, -83, 'MCT-430S4', 28, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-683, -136, 'MCT-430S4', 29, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-684, -111, 'MCT-430SL', 26, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-685, -2, 'MCT-430SL', 27, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-686, -83, 'MCT-430SL', 28, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-687, -136, 'MCT-430SL', 29, 'Y', -1); 

insert into devicetypecommand values(-689, -136, 'MCT-410CL', 30, 'N', -1);
insert into devicetypecommand values(-690, -136, 'MCT-410FL', 30, 'N', -1);
insert into devicetypecommand values(-691, -136, 'MCT-410GL', 30, 'N', -1);
insert into devicetypecommand values(-692, -136, 'MCT-410IL', 30, 'N', -1);
insert into devicetypecommand values(-693, -136, 'MCT-470', 32, 'N', -1); 
insert into devicetypecommand values(-694, -137, 'MCT-410CL', 31, 'N', -1);
insert into devicetypecommand values(-695, -137, 'MCT-410FL', 31, 'N', -1);
insert into devicetypecommand values(-696, -137, 'MCT-410GL', 31, 'N', -1);
insert into devicetypecommand values(-697, -137, 'MCT-410IL', 31, 'N', -1);

/*==============================================================*/
/* Index: Indx_DevTypeCmd_GroupID                               */
/*==============================================================*/
create index Indx_DevTypeCmd_GroupID on DeviceTypeCommand (
   CommandGroupID ASC
)
;

/*==============================================================*/
/* Table: DeviceVerification                                    */
/*==============================================================*/
create table DeviceVerification  (
   ReceiverID           NUMBER                          not null,
   TransmitterID        NUMBER                          not null,
   ResendOnFail         CHAR(1)                         not null,
   Disable              CHAR(1)                         not null,
   constraint PK_DEVICEVERIFICATION primary key (ReceiverID, TransmitterID)
)
;

/*==============================================================*/
/* Table: DeviceWindow                                          */
/*==============================================================*/
create table DeviceWindow  (
   DeviceID             NUMBER                          not null,
   Type                 VARCHAR2(20)                    not null,
   WinOpen              NUMBER                          not null,
   WinClose             NUMBER                          not null,
   AlternateOpen        NUMBER                          not null,
   AlternateClose       NUMBER                          not null,
   constraint PK_DEVICEWINDOW primary key (DeviceID, Type)
)
;

/*==============================================================*/
/* Table: DynamicCCCapBank                                      */
/*==============================================================*/
create table DynamicCCCapBank  (
   CapBankID            NUMBER                          not null,
   ControlStatus        NUMBER                          not null,
   TotalOperations      NUMBER                          not null,
   LastStatusChangeTime DATE                            not null,
   TagsControlStatus    NUMBER                          not null,
   CTITimeStamp         DATE                            not null,
   OriginalFeederID     NUMBER                          not null,
   OriginalSwitchingOrder NUMBER                          not null,
   AssumedStartVerificationStatus NUMBER                          not null,
   PrevVerificationControlStatus NUMBER                          not null,
   VerificationControlIndex NUMBER                          not null,
   AdditionalFlags      VARCHAR2(32)                    not null,
   CurrentDailyOperations NUMBER                          not null,
   TwoWayCBCState       NUMBER                          not null,
   TwoWayCBCStateTime   DATE                            not null,
   beforeVar            VARCHAR2(32)                    not null,
   afterVar             VARCHAR2(32)                    not null,
   changeVar            VARCHAR2(32)                    not null,
   constraint PK_DYNAMICCCCAPBANK primary key (CapBankID)
)
;

/*==============================================================*/
/* Table: DynamicCCFeeder                                       */
/*==============================================================*/
create table DynamicCCFeeder  (
   FeederID             NUMBER                          not null,
   CurrentVarPointValue FLOAT                           not null,
   CurrentWattPointValue FLOAT                           not null,
   NewPointDataReceivedFlag CHAR(1)                         not null,
   LastCurrentVarUpdateTime DATE                            not null,
   EstimatedVarPointValue FLOAT                           not null,
   CurrentDailyOperations NUMBER                          not null,
   RecentlyControlledFlag CHAR(1)                         not null,
   LastOperationTime    DATE                            not null,
   VarValueBeforeControl FLOAT                           not null,
   LastCapBankDeviceID  NUMBER                          not null,
   BusOptimizedVarCategory NUMBER                          not null,
   BusOptimizedVarOffset FLOAT                           not null,
   CTITimeStamp         DATE                            not null,
   PowerFactorValue     FLOAT                           not null,
   KvarSolution         FLOAT                           not null,
   EstimatedPFValue     FLOAT                           not null,
   CurrentVarPointQuality NUMBER                          not null,
   WaiveControlFlag     CHAR(1)                         not null,
   AdditionalFlags      VARCHAR2(32)                    not null,
   CurrentVoltPointValue FLOAT                           not null,
   EventSeq             NUMBER                          not null,
   CurrVerifyCBId       NUMBER                          not null,
   CurrVerifyCBOrigState NUMBER                          not null,
   CurrentWattPointQuality NUMBER                          not null,
   CurrentVoltPointQuality NUMBER                          not null,
   iVControlTot         FLOAT                           not null,
   iVCount              NUMBER                          not null,
   iWControlTot         FLOAT                           not null,
   iWCount              NUMBER                          not null,
   phaseavalue          FLOAT                          default 0 not null,
   phasebvalue          FLOAT                          default 0 not null,
   phasecvalue          FLOAT                          default 0 not null,
   LastWattPointTime    DATE                            not null,
   LastVoltPointTime    DATE                            not null,
   constraint PK_DYNAMICCCFEEDER primary key (FeederID)
)
;

/*==============================================================*/
/* Table: DynamicCCMonitorBankHistory                           */
/*==============================================================*/
create table DynamicCCMonitorBankHistory  (
   BankID               NUMBER                          not null,
   PointID              NUMBER                          not null,
   Value                FLOAT                           not null,
   DateTime             DATE                            not null,
   ScanInProgress       CHAR(1)                         not null,
   constraint PK_DYNAMICCCMONITORBANKHISTORY primary key (BankID, PointID)
)
;

/*==============================================================*/
/* Table: DynamicCCMonitorPointResponse                         */
/*==============================================================*/
create table DynamicCCMonitorPointResponse  (
   BankID               NUMBER                          not null,
   PointID              NUMBER                          not null,
   PreOpValue           FLOAT                           not null,
   Delta                FLOAT                           not null,
   constraint PK_DYNAMICCCMONITORPOINTRESPON primary key (BankID, PointID)
)
;

/*==============================================================*/
/* Table: DynamicCCSubstationBus                                */
/*==============================================================*/
create table DynamicCCSubstationBus  (
   SubstationBusID      NUMBER                          not null,
   CurrentVarPointValue FLOAT                           not null,
   CurrentWattPointValue FLOAT                           not null,
   NextCheckTime        DATE                            not null,
   NewPointDataReceivedFlag CHAR(1)                         not null,
   BusUpdatedFlag       CHAR(1)                         not null,
   LastCurrentVarUpdateTime DATE                            not null,
   EstimatedVarPointValue FLOAT                           not null,
   CurrentDailyOperations NUMBER                          not null,
   PeakTimeFlag         CHAR(1)                         not null,
   RecentlyControlledFlag CHAR(1)                         not null,
   LastOperationTime    DATE                            not null,
   VarValueBeforeControl FLOAT                           not null,
   LastFeederPAOid      NUMBER                          not null,
   LastFeederPosition   NUMBER                          not null,
   CTITimeStamp         DATE                            not null,
   PowerFactorValue     FLOAT                           not null,
   KvarSolution         FLOAT                           not null,
   EstimatedPFValue     FLOAT                           not null,
   CurrentVarPointQuality NUMBER                          not null,
   WaiveControlFlag     CHAR(1)                         not null,
   AdditionalFlags      VARCHAR2(32)                    not null,
   CurrVerifyCBId       NUMBER                          not null,
   CurrVerifyFeederId   NUMBER                          not null,
   CurrVerifyCBOrigState NUMBER                          not null,
   VerificationStrategy NUMBER                          not null,
   CbInactivityTime     NUMBER                          not null,
   CurrentVoltPointValue FLOAT                           not null,
   SwitchPointStatus    CHAR(1)                         not null,
   AltSubControlValue   FLOAT                           not null,
   EventSeq             NUMBER                          not null,
   CurrentWattPointQuality NUMBER                          not null,
   CurrentVoltPointQuality NUMBER                          not null,
   iVControlTot         FLOAT                           not null,
   iVCount              NUMBER                          not null,
   iWControlTot         FLOAT                           not null,
   iWCount              NUMBER                          not null,
   phaseavalue          FLOAT                          default 0 not null,
   phasebvalue          FLOAT                          default 0 not null,
   phasecvalue          FLOAT                          default 0 not null,
   LastWattPointTime    DATE                            not null,
   LastVoltPointTime    DATE                            not null,
   constraint PK_DYNAMICCCSUBSTATIONBUS primary key (SubstationBusID)
)
;

/*==============================================================*/
/* Table: DynamicCalcHistorical                                 */
/*==============================================================*/
create table DynamicCalcHistorical  (
   PointID              NUMBER                          not null,
   LastUpdate           DATE                            not null,
   constraint PK_DYNAMICCALCHISTORICAL primary key (PointID)
)
;

/*==============================================================*/
/* Table: DynamicImportStatus                                   */
/*==============================================================*/
create table DynamicImportStatus  (
   Entry                VARCHAR2(64)                    not null,
   LastImportTime       VARCHAR2(64)                    not null,
   NextImportTime       VARCHAR2(64)                    not null,
   TotalSuccesses       VARCHAR2(32)                    not null,
   TotalAttempts        VARCHAR2(32)                    not null,
   ForceImport          CHAR(1)                         not null,
   constraint PK_DYNAMICIMPORTSTATUS primary key (Entry)
)
;

insert into DynamicImportStatus values('SYSTEMVALUE', '------', '------', '--', '--', 'N');

/*==============================================================*/
/* Table: DynamicLMControlArea                                  */
/*==============================================================*/
create table DynamicLMControlArea  (
   DeviceID             NUMBER                          not null,
   NextCheckTime        DATE                            not null,
   NewPointDataReceivedFlag CHAR(1)                         not null,
   UpdatedFlag          CHAR(1)                         not null,
   ControlAreaState     NUMBER                          not null,
   CurrentPriority      NUMBER                          not null,
   TimeStamp            DATE                            not null,
   CurrentDailyStartTime NUMBER                          not null,
   CurrentDailyStopTime NUMBER                          not null,
   constraint PK_DYNAMICLMCONTROLAREA primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DynamicLMControlAreaTrigger                           */
/*==============================================================*/
create table DynamicLMControlAreaTrigger  (
   DeviceID             NUMBER                          not null,
   TriggerNumber        NUMBER                          not null,
   PointValue           FLOAT                           not null,
   LastPointValueTimeStamp DATE                            not null,
   PeakPointValue       FLOAT                           not null,
   LastPeakPointValueTimeStamp DATE                            not null,
   TriggerID            NUMBER                          not null,
   constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key (DeviceID, TriggerNumber)
)
;

/*==============================================================*/
/* Table: DynamicLMControlHistory                               */
/*==============================================================*/
create table DynamicLMControlHistory  (
   PAObjectID           NUMBER                          not null,
   LMCtrlHistID         NUMBER                          not null,
   StartDateTime        DATE                            not null,
   SOE_Tag              NUMBER                          not null,
   ControlDuration      NUMBER                          not null,
   ControlType          VARCHAR2(128)                   not null,
   CurrentDailyTime     NUMBER                          not null,
   CurrentMonthlyTime   NUMBER                          not null,
   CurrentSeasonalTime  NUMBER                          not null,
   CurrentAnnualTime    NUMBER                          not null,
   ActiveRestore        CHAR(1)                         not null,
   ReductionValue       FLOAT                           not null,
   StopDateTime         DATE                            not null,
   constraint PK_DYNLMCONTROLHISTORY primary key (PAObjectID)
)
;

/*==============================================================*/
/* Table: DynamicLMGroup                                        */
/*==============================================================*/
create table DynamicLMGroup  (
   DeviceID             NUMBER                          not null,
   GroupControlState    NUMBER                          not null,
   CurrentHoursDaily    NUMBER                          not null,
   CurrentHoursMonthly  NUMBER                          not null,
   CurrentHoursSeasonal NUMBER                          not null,
   CurrentHoursAnnually NUMBER                          not null,
   LastControlSent      DATE                            not null,
   TimeStamp            DATE                            not null,
   ControlStartTime     DATE                            not null,
   ControlCompleteTime  DATE                            not null,
   NextControlTime      DATE                            not null,
   InternalState        NUMBER                          not null,
   dailyops             SMALLINT                        not null,
   constraint PK_DYNAMICLMGROUP primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DynamicLMProgram                                      */
/*==============================================================*/
create table DynamicLMProgram  (
   DeviceID             NUMBER                          not null,
   ProgramState         NUMBER                          not null,
   ReductionTotal       FLOAT                           not null,
   StartedControlling   DATE                            not null,
   LastControlSent      DATE                            not null,
   ManualControlReceivedFlag CHAR(1)                         not null,
   TimeStamp            DATE                            not null,
   constraint PK_DYNAMICLMPROGRAM primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DynamicLMProgramDirect                                */
/*==============================================================*/
create table DynamicLMProgramDirect  (
   DeviceID             NUMBER                          not null,
   CurrentGearNumber    NUMBER                          not null,
   LastGroupControlled  NUMBER                          not null,
   StartTime            DATE                            not null,
   StopTime             DATE                            not null,
   TimeStamp            DATE                            not null,
   NotifyActiveTime     DATE                            not null,
   StartedRampingOut    DATE                            not null,
   NotifyInactiveTime   DATE                            not null,
   ConstraintOverride   CHAR(1)                         not null,
   AdditionalInfo       VARCHAR2(80)                    not null,
   constraint PK_DYNAMICLMPROGRAMDIRECT primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: DynamicPAOInfo                                        */
/*==============================================================*/
create table DynamicPAOInfo  (
   EntryID              NUMBER                          not null,
   PAObjectID           NUMBER                          not null,
   Owner                VARCHAR2(64)                    not null,
   InfoKey              VARCHAR2(128)                   not null,
   Value                VARCHAR2(128)                   not null,
   UpdateTime           DATE                            not null,
   constraint PK_DYNPAOINFO primary key (EntryID)
)
;

alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique (PAObjectID, Owner, InfoKey)
;

/*==============================================================*/
/* Table: DynamicPAOStatistics                                  */
/*==============================================================*/
create table DynamicPAOStatistics  (
   PAOBjectID           NUMBER                          not null,
   StatisticType        VARCHAR2(16)                    not null,
   Requests             NUMBER                          not null,
   Completions          NUMBER                          not null,
   Attempts             NUMBER                          not null,
   CommErrors           NUMBER                          not null,
   ProtocolErrors       NUMBER                          not null,
   SystemErrors         NUMBER                          not null,
   StartDateTime        DATE                            not null,
   StopDateTime         DATE                            not null,
   constraint PK_DYNAMICPAOSTATISTICS primary key (PAOBjectID, StatisticType)
)
;

/*==============================================================*/
/* Table: DynamicPointAlarming                                  */
/*==============================================================*/
create table DynamicPointAlarming  (
   PointID              NUMBER                          not null,
   AlarmCondition       NUMBER                          not null,
   CategoryID           NUMBER                          not null,
   AlarmTime            DATE                            not null,
   Action               VARCHAR2(60)                    not null,
   Description          VARCHAR2(120)                   not null,
   Tags                 NUMBER                          not null,
   LogID                NUMBER                          not null,
   SOE_TAG              NUMBER                          not null,
   Type                 NUMBER                          not null,
   UserName             VARCHAR2(64)                    not null,
   constraint PK_DYNAMICPOINTALARMING primary key (PointID, AlarmCondition)
)
;

/*==============================================================*/
/* Table: DynamicTags                                           */
/*==============================================================*/
create table DynamicTags  (
   InstanceID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   TagID                NUMBER                          not null,
   UserName             VARCHAR2(64)                    not null,
   Action               VARCHAR2(20)                    not null,
   Description          VARCHAR2(120)                   not null,
   TagTime              DATE                            not null,
   RefStr               VARCHAR2(60)                    not null,
   ForStr               VARCHAR2(60)                    not null,
   constraint PK_DYNAMICTAGS primary key (InstanceID)
)
;

/*==============================================================*/
/* Table: DynamicVerification                                   */
/*==============================================================*/
create table DynamicVerification  (
   LogID                NUMBER                          not null,
   TimeArrival          DATE                            not null,
   ReceiverID           NUMBER                          not null,
   TransmitterID        NUMBER                          not null,
   Command              VARCHAR2(256)                   not null,
   Code                 VARCHAR2(128)                   not null,
   CodeSequence         NUMBER                          not null,
   Received             CHAR(1)                         not null,
   CodeStatus           VARCHAR2(32)                    not null,
   constraint PK_DYNAMICVERIFICATION primary key (LogID)
)
;

/*==============================================================*/
/* Index: Index_DYNVER_CS                                       */
/*==============================================================*/
create index Index_DYNVER_CS on DynamicVerification (
   CodeSequence ASC
)
;

/*==============================================================*/
/* Index: Indx_DYNV_TIME                                        */
/*==============================================================*/
create index Indx_DYNV_TIME on DynamicVerification (
   TimeArrival ASC
)
;

/*==============================================================*/
/* Table: EnergyCompany                                         */
/*==============================================================*/
create table EnergyCompany  (
   EnergyCompanyID      NUMBER                          not null,
   Name                 VARCHAR2(60)                    not null,
   PrimaryContactID     NUMBER                          not null,
   UserID               NUMBER                          not null,
   constraint PK_ENERGYCOMPANY primary key (EnergyCompanyID)
)
;

insert into EnergyCompany VALUES (-1,'Default Energy Company',0,-100);

/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique index Indx_EnCmpName on EnergyCompany (
   Name ASC
)
;

/*==============================================================*/
/* Table: EnergyCompanyCustomerList                             */
/*==============================================================*/
create table EnergyCompanyCustomerList  (
   EnergyCompanyID      NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key (EnergyCompanyID, CustomerID)
)
;

/*==============================================================*/
/* Table: EnergyCompanyOperatorLoginList                        */
/*==============================================================*/
create table EnergyCompanyOperatorLoginList  (
   EnergyCompanyID      NUMBER                          not null,
   OperatorLoginID      NUMBER                          not null,
   constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key (EnergyCompanyID, OperatorLoginID)
)
;

INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-100);

/*==============================================================*/
/* Table: EsubDisplayIndex                                      */
/*==============================================================*/
create table EsubDisplayIndex  (
   SearchKey            VARCHAR2(500)                   not null,
   DisplayUrl           VARCHAR2(500)                   not null,
   constraint PK_ESUBDISPLAYINDEX primary key (SearchKey)
)
;

/*==============================================================*/
/* Table: FDRInterface                                          */
/*==============================================================*/
create table FDRInterface  (
   InterfaceID          NUMBER                          not null,
   InterfaceName        VARCHAR2(30)                    not null,
   PossibleDirections   VARCHAR2(100)                   not null,
   hasDestination       CHAR(1)                         not null,
   constraint PK_FDRINTERFACE primary key (InterfaceID)
)
;

insert into FDRInterface values ( 1, 'INET', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values ( 2, 'ACS', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 3, 'VALMET', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 4, 'CYGNET', 'Send,Send for control,Receive,Receive for control', 'f' );
insert into FDRInterface values ( 5, 'STEC', 'Receive', 'f' );
insert into FDRInterface values ( 6, 'RCCS', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values ( 7, 'TRISTATE', 'Receive', 'f' );
insert into FDRInterface values ( 8, 'RDEX', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values ( 9, 'SYSTEM','Link Status','f');
insert into FDRInterface values (10, 'DSM2IMPORT','Receive,Receive for control','f');
insert into FDRInterface values (11, 'TELEGYR','Receive,Receive for control','f');
insert into FDRInterface values (12, 'TEXTIMPORT','Receive,Receive for control','f');
insert into FDRInterface values (13, 'TEXTEXPORT','Send','f');

insert into fdrinterface values (16, 'LODESTAR_STD','Receive','f');
insert into fdrinterface values (17, 'LODESTAR_ENH','Receive','f');
insert into fdrinterface values (18, 'DSM2FILEIN', 'Receive,Receive for control', 'f');
insert into FDRInterface values (19, 'XA21LM','Receive,Send', 't' );
insert into fdrinterface values (20, 'BEPC','Send','f');
insert into FDRInterface values (21, 'PI','Receive', 't' );
insert into FDRInterface values (22, 'LIVEDATA','Receive', 'f' );
insert into FDRInterface values (23, 'ACSMULTI', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterface values (24, 'WABASH', 'Send', 'f' );
insert into FDRInterface values (25, 'TRISTATESUB', 'Receive', 'f' );

/*==============================================================*/
/* Table: FDRInterfaceOption                                    */
/*==============================================================*/
create table FDRInterfaceOption  (
   InterfaceID          NUMBER                          not null,
   OptionLabel          VARCHAR2(20)                    not null,
   Ordering             NUMBER                          not null,
   OptionType           VARCHAR2(8)                     not null,
   OptionValues         VARCHAR2(256)                   not null,
   constraint PK_FDRINTERFACEOPTION primary key (InterfaceID, Ordering)
)
;

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
insert into FDRInterfaceOption values(9, 'Client',1,'Text','(none)');
insert into FDRInterfaceOption values(10, 'Point',1,'Text','(none)');
insert into FDRInterfaceOption values(11, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(11, 'Interval (sec)', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(12, 'Point ID',1,'Text','(none)');
insert into fdrinterfaceoption values(12,'DrivePath',2,'Text','(none)');
insert into fdrinterfaceoption values(12,'Filename',3,'Text','(none)');
insert into FDRInterfaceOption values(13, 'Point ID',1,'Text','(none)');

insert into fdrinterfaceoption values(16, 'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values(16, 'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values(16, 'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values(16, 'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values(17, 'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values(17, 'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values(17, 'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values(17, 'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values(18, 'Option Number', 1, 'Combo', '1');
insert into fdrinterfaceoption values(18, 'Point ID', 2, 'Text', '(none)');
insert into FDRInterfaceOption values(19, 'Translation', 1, 'Text', '(none)' );
insert into fdrinterfaceoption values(20, 'Point', 1, 'Combo', 'TOTAL LOAD KW' );
insert into FDRInterfaceOption values(21, 'Tag Name', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(21, 'Period (sec)', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(22, 'Address', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(22, 'Data Type', 2, 'Combo', 'Data_RealExtended,Data_DiscreteExtended,Data_StateExtended,Data_RealQ,Data_DiscreteQ,Data_State,Data_Discrete,Data_Real,Data_RealQTimeTag,Data_StateQTimeTag,Data_DiscreteQTimeTag' );
insert into FDRInterfaceOption values(23, 'Category', 1, 'Combo', 'PSEUDO,REAL,CALCULATED' );
insert into FDRInterfaceOption values(23, 'Remote', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(23, 'Point', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(23, 'Destination/Source', 4, 'Text', '(none)' );
insert into FDRInterfaceOption values(24, 'SchedName', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(24, 'Path', 2, 'Text', 'c:\yukon\server\export\' );
insert into FDRInterfaceOption values(24, 'Filename', 3, 'Text', 'control.txt' );
insert into FDRInterfaceOption values(25, 'Point', 1, 'Combo', 'Nucla 115/69 Xfmr.,Happy Canyon 661Idarado,Cascade 115/69 (T2),Ames Generation,Dallas Creek MW,Dallas Creek MV' );

/*==============================================================*/
/* Table: FDRTRANSLATION                                        */
/*==============================================================*/
create table FDRTRANSLATION  (
   POINTID              NUMBER                          not null,
   DIRECTIONTYPE        VARCHAR2(20)                    not null,
   InterfaceType        VARCHAR2(20)                    not null,
   DESTINATION          VARCHAR2(20)                    not null,
   TRANSLATION          VARCHAR2(200)                   not null,
   constraint PK_FDRTrans primary key (POINTID, DIRECTIONTYPE, InterfaceType, TRANSLATION)
)
;

/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create index Indx_FdrTransIntTyp on FDRTRANSLATION (
   InterfaceType ASC
)
;

/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create index Indx_FdrTrnsIntTypDir on FDRTRANSLATION (
   DIRECTIONTYPE ASC,
   InterfaceType ASC
)
;

/*==============================================================*/
/* Table: GRAPHDATASERIES                                       */
/*==============================================================*/
create table GRAPHDATASERIES  (
   GRAPHDATASERIESID    NUMBER                          not null,
   GRAPHDEFINITIONID    NUMBER                          not null,
   POINTID              NUMBER                          not null,
   Label                VARCHAR2(40)                    not null,
   Axis                 CHAR(1)                         not null,
   Color                NUMBER                          not null,
   Type                 NUMBER                          not null,
   Multiplier           FLOAT                           not null,
   Renderer             SMALLINT                        not null,
   MoreData             VARCHAR2(100)                   not null,
   constraint SYS_GrphDserID primary key (GRAPHDATASERIESID)
)
;

/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create index Indx_GrpDSerPtID on GRAPHDATASERIES (
   POINTID ASC
)
;

/*==============================================================*/
/* Table: GRAPHDEFINITION                                       */
/*==============================================================*/
create table GRAPHDEFINITION  (
   GRAPHDEFINITIONID    NUMBER                          not null,
   NAME                 VARCHAR2(40)                    not null,
   AutoScaleTimeAxis    CHAR(1)                         not null,
   AutoScaleLeftAxis    CHAR(1)                         not null,
   AutoScaleRightAxis   CHAR(1)                         not null,
   STARTDATE            DATE                            not null,
   STOPDATE             DATE                            not null,
   LeftMin              FLOAT                           not null,
   LeftMax              FLOAT                           not null,
   RightMin             FLOAT                           not null,
   RightMax             FLOAT                           not null,
   Type                 CHAR(1)                         not null,
   constraint SYS_C0015109 primary key (GRAPHDEFINITIONID)
)
;

alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique (NAME)
;

/*==============================================================*/
/* Table: GatewayEndDevice                                      */
/*==============================================================*/
create table GatewayEndDevice  (
   SerialNumber         VARCHAR2(30)                    not null,
   HardwareType         NUMBER                          not null,
   DataType             NUMBER                          not null,
   DataValue            VARCHAR2(100),
   constraint PK_GATEWAYENDDEVICE primary key (SerialNumber, HardwareType, DataType)
)
;

/*==============================================================*/
/* Table: GenericMacro                                          */
/*==============================================================*/
create table GenericMacro  (
   OwnerID              NUMBER                          not null,
   ChildID              NUMBER                          not null,
   ChildOrder           NUMBER                          not null,
   MacroType            VARCHAR2(20)                    not null,
   constraint PK_GENERICMACRO primary key (OwnerID, ChildOrder, MacroType)
)
;

/*==============================================================*/
/* Table: GraphCustomerList                                     */
/*==============================================================*/
create table GraphCustomerList  (
   GraphDefinitionID    NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   constraint PK_GRAPHCUSTOMERLIST primary key (GraphDefinitionID, CustomerID)
)
;

/*==============================================================*/
/* Table: GroupPaoPermission                                    */
/*==============================================================*/
create table GroupPaoPermission  (
   GroupPaoPermissionID NUMBER                          not null,
   GroupID              NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Permission           VARCHAR2(50)                    not null,
   Allow                VARCHAR2(5)                    default 'Allow' not null,
   constraint PK_GROUPPAOPERMISSION primary key (GroupPaoPermissionID)
)
;

alter table GroupPaoPermission
   add constraint AK_GRPPAOPERM unique (GroupID, PaoID, Permission)
;

/*==============================================================*/
/* Table: HolidaySchedule                                       */
/*==============================================================*/
create table HolidaySchedule  (
   HolidayScheduleID    NUMBER                          not null,
   HolidayScheduleName  VARCHAR2(40)                    not null,
   constraint PK_HOLIDAYSCHEDULE primary key (HolidayScheduleID)
)
;

insert into HolidaySchedule values( 0, 'Empty Holiday Schedule' );

/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique index Indx_HolSchName on HolidaySchedule (
   HolidayScheduleName ASC
)
;

/*==============================================================*/
/* Table: ImportData                                            */
/*==============================================================*/
create table ImportData  (
   Address              VARCHAR2(64)                    not null,
   Name                 VARCHAR2(64)                    not null,
   RouteName            VARCHAR2(64)                    not null,
   MeterNumber          VARCHAR2(64)                    not null,
   CollectionGrp        VARCHAR2(64)                    not null,
   AltGrp               VARCHAR2(64)                    not null,
   TemplateName         VARCHAR2(64)                    not null,
   BillGrp              VARCHAR2(64)                    not null,
   SubstationName       VARCHAR2(64)                    not null,
   constraint PK_IMPORTDATA primary key (Address)
)
;

/*==============================================================*/
/* Table: ImportFail                                            */
/*==============================================================*/
create table ImportFail  (
   Address              VARCHAR2(64)                    not null,
   Name                 VARCHAR2(64)                    not null,
   RouteName            VARCHAR2(64)                    not null,
   MeterNumber          VARCHAR2(64)                    not null,
   CollectionGrp        VARCHAR2(64)                    not null,
   AltGrp               VARCHAR2(64)                    not null,
   TemplateName         VARCHAR2(64)                    not null,
   ErrorMsg             VARCHAR2(1024),
   DateTime             DATE,
   BillGrp              VARCHAR2(64)                    not null,
   SubstationName       VARCHAR2(64)                    not null,
   FailType             VARCHAR2(64)                    not null,
   constraint PK_IMPORTFAIL primary key (Address)
)
;

/*==============================================================*/
/* Table: ImportPendingComm                                     */
/*==============================================================*/
create table ImportPendingComm  (
   DeviceID             NUMBER                          not null,
   Address              VARCHAR2(64)                    not null,
   Name                 VARCHAR2(64)                    not null,
   RouteName            VARCHAR2(64)                    not null,
   MeterNumber          VARCHAR2(64)                    not null,
   CollectionGrp        VARCHAR2(64)                    not null,
   AltGrp               VARCHAR2(64)                    not null,
   TemplateName         VARCHAR2(64)                    not null,
   BillGrp              VARCHAR2(64)                    not null,
   SubstationName       VARCHAR2(64)                    not null,
   constraint PK_IMPORTPENDINGCOMM primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: JOB                                                   */
/*==============================================================*/
create table JOB  (
   JobID                INTEGER                         not null,
   BeanName             VARCHAR2(250)                   not null,
   Disabled             CHAR(1)                         not null,
   UserID               NUMBER                          not null,
   constraint PK_JOB primary key (JobID)
)
;

/*==============================================================*/
/* Table: JOBPROPERTY                                           */
/*==============================================================*/
create table JOBPROPERTY  (
   JobProperty          NUMBER                          not null,
   JobID                INTEGER                         not null,
   name                 CLOB                            not null,
   value                CLOB                            not null,
   constraint PK_JOBPROPERTY primary key (JobProperty)
)
;

/*==============================================================*/
/* Table: JOBSCHEDULEDONETIME                                   */
/*==============================================================*/
create table JOBSCHEDULEDONETIME  (
   JobID                INTEGER                         not null,
   StartTime            DATE                            not null,
   constraint PK_JOBSCHEDULEDONETIME primary key (JobID)
)
;

/*==============================================================*/
/* Table: JOBSCHEDULEDREPEATING                                 */
/*==============================================================*/
create table JOBSCHEDULEDREPEATING  (
   JobID                INTEGER                         not null,
   CronString           CLOB                            not null,
   constraint PK_JobScheduledRepeating primary key (JobID)
)
;

/*==============================================================*/
/* Table: JOBSTATUS                                             */
/*==============================================================*/
create table JOBSTATUS  (
   JobStatusID          INTEGER                         not null,
   JobID                INTEGER                         not null,
   StartTime            DATE                            not null,
   StopTime             DATE,
   JobState             VARCHAR2(50),
   message              CLOB,
   constraint PK_JOBSTATUS primary key (JobStatusID)
)
;

/*==============================================================*/
/* Table: LMCONTROLAREAPROGRAM                                  */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM  (
   DEVICEID             NUMBER                          not null,
   LMPROGRAMDEVICEID    NUMBER                          not null,
   StartPriority        NUMBER                          not null,
   StopPriority         NUMBER                          not null,
   constraint PK_LMCONTROLAREAPROGRAM primary key (DEVICEID, LMPROGRAMDEVICEID)
)
;

/*==============================================================*/
/* Table: LMCONTROLAREATRIGGER                                  */
/*==============================================================*/
create table LMCONTROLAREATRIGGER  (
   DEVICEID             NUMBER                          not null,
   TRIGGERNUMBER        NUMBER                          not null,
   TRIGGERTYPE          VARCHAR2(20)                    not null,
   POINTID              NUMBER                          not null,
   NORMALSTATE          NUMBER                          not null,
   THRESHOLD            FLOAT                           not null,
   PROJECTIONTYPE       VARCHAR2(14)                    not null,
   PROJECTIONPOINTS     NUMBER                          not null,
   PROJECTAHEADDURATION NUMBER                          not null,
   THRESHOLDKICKPERCENT NUMBER                          not null,
   MINRESTOREOFFSET     FLOAT                           not null,
   PEAKPOINTID          NUMBER                          not null,
   TriggerID            NUMBER                          not null,
   constraint PK_LMCONTROLAREATRIGGER primary key (DEVICEID, TRIGGERNUMBER)
)
;

/*==============================================================*/
/* Index: INDX_UNQ_LMCNTRTR_TRID                                */
/*==============================================================*/
create unique index INDX_UNQ_LMCNTRTR_TRID on LMCONTROLAREATRIGGER (
   TriggerID ASC
)
;

/*==============================================================*/
/* Table: LMControlArea                                         */
/*==============================================================*/
create table LMControlArea  (
   DEVICEID             NUMBER                          not null,
   DEFOPERATIONALSTATE  VARCHAR2(20)                    not null,
   CONTROLINTERVAL      NUMBER                          not null,
   MINRESPONSETIME      NUMBER                          not null,
   DEFDAILYSTARTTIME    NUMBER                          not null,
   DEFDAILYSTOPTIME     NUMBER                          not null,
   REQUIREALLTRIGGERSACTIVEFLAG VARCHAR2(1)                     not null,
   constraint PK_LMCONTROLAREA primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: LMControlHistory                                      */
/*==============================================================*/
create table LMControlHistory  (
   LMCtrlHistID         NUMBER                          not null,
   PAObjectID           NUMBER                          not null,
   StartDateTime        DATE                            not null,
   SOE_Tag              NUMBER                          not null,
   ControlDuration      NUMBER                          not null,
   ControlType          VARCHAR2(128)                   not null,
   CurrentDailyTime     NUMBER                          not null,
   CurrentMonthlyTime   NUMBER                          not null,
   CurrentSeasonalTime  NUMBER                          not null,
   CurrentAnnualTime    NUMBER                          not null,
   ActiveRestore        CHAR(1)                         not null,
   ReductionValue       FLOAT                           not null,
   StopDateTime         DATE                            not null,
   constraint PK_LMCONTROLHISTORY primary key (LMCtrlHistID)
)
;

/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create index Indx_Start on LMControlHistory (
   StartDateTime ASC
)
;

/*==============================================================*/
/* Table: LMControlScenarioProgram                              */
/*==============================================================*/
create table LMControlScenarioProgram  (
   ScenarioID           NUMBER                          not null,
   ProgramID            NUMBER                          not null,
   StartOffset          NUMBER                          not null,
   StopOffset           NUMBER                          not null,
   StartGear            NUMBER                          not null,
   constraint PK_LMCONTROLSCENARIOPROGRAM primary key (ScenarioID, ProgramID)
)
;

/*==============================================================*/
/* Table: LMCurtailCustomerActivity                             */
/*==============================================================*/
create table LMCurtailCustomerActivity  (
   CustomerID           NUMBER                          not null,
   CurtailReferenceID   NUMBER                          not null,
   AcknowledgeStatus    VARCHAR2(30)                    not null,
   AckDateTime          DATE                            not null,
   IPAddressOfAckUser   VARCHAR2(15)                    not null,
   UserIDName           VARCHAR2(40)                    not null,
   NameOfAckPerson      VARCHAR2(40)                    not null,
   CurtailmentNotes     VARCHAR2(120)                   not null,
   CurrentPDL           FLOAT                           not null,
   AckLateFlag          CHAR(1)                         not null,
   constraint PK_LMCURTAILCUSTOMERACTIVITY primary key (CustomerID, CurtailReferenceID)
)
;

/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create index Index_LMCrtCstActID on LMCurtailCustomerActivity (
   CustomerID ASC
)
;

/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
   AcknowledgeStatus ASC
)
;

/*==============================================================*/
/* Table: LMCurtailProgramActivity                              */
/*==============================================================*/
create table LMCurtailProgramActivity  (
   DeviceID             NUMBER                          not null,
   CurtailReferenceID   NUMBER                          not null,
   ActionDateTime       DATE                            not null,
   NotificationDateTime DATE                            not null,
   CurtailmentStartTime DATE                            not null,
   CurtailmentStopTime  DATE                            not null,
   RunStatus            VARCHAR2(20)                    not null,
   AdditionalInfo       VARCHAR2(100)                   not null,
   constraint PK_LMCURTAILPROGRAMACTIVITY primary key (CurtailReferenceID)
)
;

/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
   CurtailmentStartTime ASC
)
;

/*==============================================================*/
/* Table: LMDirectCustomerList                                  */
/*==============================================================*/
create table LMDirectCustomerList  (
   ProgramID            NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   constraint PK_LMDIRECTCUSTOMERLIST primary key (ProgramID, CustomerID)
)
;

/*==============================================================*/
/* Table: LMDirectNotifGrpList                                  */
/*==============================================================*/
create table LMDirectNotifGrpList  (
   ProgramID            NUMBER                          not null,
   NotificationGrpID    NUMBER                          not null,
   constraint PK_LMDIRECTNOTIFGRPLIST primary key (ProgramID, NotificationGrpID)
)
;

/*==============================================================*/
/* Table: LMEnergyExchangeCustomerList                          */
/*==============================================================*/
create table LMEnergyExchangeCustomerList  (
   ProgramID            NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key (ProgramID, CustomerID)
)
;

/*==============================================================*/
/* Table: LMEnergyExchangeCustomerReply                         */
/*==============================================================*/
create table LMEnergyExchangeCustomerReply  (
   CustomerID           NUMBER                          not null,
   OfferID              NUMBER                          not null,
   AcceptStatus         VARCHAR2(30)                    not null,
   AcceptDateTime       DATE                            not null,
   RevisionNumber       NUMBER                          not null,
   IPAddressOfAcceptUser VARCHAR2(15)                    not null,
   UserIDName           VARCHAR2(40)                    not null,
   NameOfAcceptPerson   VARCHAR2(40)                    not null,
   EnergyExchangeNotes  VARCHAR2(120)                   not null,
   constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key (CustomerID, OfferID, RevisionNumber)
)
;

/*==============================================================*/
/* Table: LMEnergyExchangeHourlyCustomer                        */
/*==============================================================*/
create table LMEnergyExchangeHourlyCustomer  (
   CustomerID           NUMBER                          not null,
   OfferID              NUMBER                          not null,
   RevisionNumber       NUMBER                          not null,
   Hour                 NUMBER                          not null,
   AmountCommitted      FLOAT                           not null,
   constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key (CustomerID, OfferID, RevisionNumber, Hour)
)
;

/*==============================================================*/
/* Table: LMEnergyExchangeHourlyOffer                           */
/*==============================================================*/
create table LMEnergyExchangeHourlyOffer  (
   OfferID              NUMBER                          not null,
   RevisionNumber       NUMBER                          not null,
   Hour                 NUMBER                          not null,
   Price                NUMBER                          not null,
   AmountRequested      FLOAT                           not null,
   constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key (OfferID, RevisionNumber, Hour)
)
;

/*==============================================================*/
/* Table: LMEnergyExchangeOfferRevision                         */
/*==============================================================*/
create table LMEnergyExchangeOfferRevision  (
   OfferID              NUMBER                          not null,
   RevisionNumber       NUMBER                          not null,
   ActionDateTime       DATE                            not null,
   NotificationDateTime DATE                            not null,
   OfferExpirationDateTime DATE                            not null,
   AdditionalInfo       VARCHAR2(100)                   not null,
   constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key (OfferID, RevisionNumber)
)
;

/*==============================================================*/
/* Table: LMEnergyExchangeProgramOffer                          */
/*==============================================================*/
create table LMEnergyExchangeProgramOffer  (
   DeviceID             NUMBER                          not null,
   OfferID              NUMBER                          not null,
   RunStatus            VARCHAR2(20)                    not null,
   OfferDate            DATE                            not null,
   constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key (OfferID)
)
;

/*==============================================================*/
/* Table: LMGroup                                               */
/*==============================================================*/
create table LMGroup  (
   DeviceID             NUMBER                          not null,
   KWCapacity           FLOAT                           not null,
   constraint PK_LMGROUP primary key (DeviceID)
)
;

insert into lmgroup values( 0, 0 );

/*==============================================================*/
/* Table: LMGroupEmetcon                                        */
/*==============================================================*/
create table LMGroupEmetcon  (
   DEVICEID             NUMBER                          not null,
   GOLDADDRESS          NUMBER                          not null,
   SILVERADDRESS        NUMBER                          not null,
   ADDRESSUSAGE         CHAR(1)                         not null,
   RELAYUSAGE           CHAR(1)                         not null,
   ROUTEID              NUMBER                          not null,
   constraint PK_LMGROUPEMETCON primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: LMGroupExpressCom                                     */
/*==============================================================*/
create table LMGroupExpressCom  (
   LMGroupID            NUMBER                          not null,
   RouteID              NUMBER                          not null,
   SerialNumber         VARCHAR2(10)                    not null,
   ServiceProviderID    NUMBER                          not null,
   GeoID                NUMBER                          not null,
   SubstationID         NUMBER                          not null,
   FeederID             NUMBER                          not null,
   ZipID                NUMBER                          not null,
   UserID               NUMBER                          not null,
   ProgramID            NUMBER                          not null,
   SplinterID           NUMBER                          not null,
   AddressUsage         VARCHAR2(10)                    not null,
   RelayUsage           CHAR(15)                        not null,
   constraint PK_LMGROUPEXPRESSCOM primary key (LMGroupID)
)
;

/*==============================================================*/
/* Table: LMGroupExpressComAddress                              */
/*==============================================================*/
create table LMGroupExpressComAddress  (
   AddressID            NUMBER                          not null,
   AddressType          VARCHAR2(20)                    not null,
   Address              NUMBER                          not null,
   AddressName          VARCHAR2(30)                    not null,
   constraint PK_LMGROUPEXPRESSCOMADDRESS primary key (AddressID)
)
;

insert into LMGroupExpressComAddress values( 0, '(none)', 0, '(none)' );

/*==============================================================*/
/* Table: LMGroupMCT                                            */
/*==============================================================*/
create table LMGroupMCT  (
   DeviceID             NUMBER                          not null,
   MCTAddress           NUMBER                          not null,
   MCTLevel             CHAR(1)                         not null,
   RelayUsage           CHAR(7)                         not null,
   RouteID              NUMBER                          not null,
   MCTDeviceID          NUMBER                          not null,
   constraint PK_LMGrpMCTPK primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: LMGroupPoint                                          */
/*==============================================================*/
create table LMGroupPoint  (
   DEVICEID             NUMBER                          not null,
   DeviceIDUsage        NUMBER                          not null,
   PointIDUsage         NUMBER                          not null,
   StartControlRawState NUMBER                          not null,
   constraint PK_LMGROUPPOINT primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: LMGroupRipple                                         */
/*==============================================================*/
create table LMGroupRipple  (
   DeviceID             NUMBER                          not null,
   RouteID              NUMBER                          not null,
   ShedTime             NUMBER                          not null,
   ControlValue         CHAR(50)                        not null,
   RestoreValue         CHAR(50)                        not null,
   constraint PK_LMGROUPRIPPLE primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: LMGroupSA205105                                       */
/*==============================================================*/
create table LMGroupSA205105  (
   GroupID              NUMBER                          not null,
   RouteID              NUMBER                          not null,
   OperationalAddress   NUMBER                          not null,
   LoadNumber           VARCHAR2(64)                    not null,
   constraint PK_LMGROUPSA205105 primary key (GroupID)
)
;

/*==============================================================*/
/* Table: LMGroupSA305                                          */
/*==============================================================*/
create table LMGroupSA305  (
   GroupID              NUMBER                          not null,
   RouteID              NUMBER                          not null,
   AddressUsage         VARCHAR2(8)                     not null,
   UtilityAddress       NUMBER                          not null,
   GroupAddress         NUMBER                          not null,
   DivisionAddress      NUMBER                          not null,
   SubstationAddress    NUMBER                          not null,
   IndividualAddress    VARCHAR2(16)                    not null,
   RateFamily           NUMBER                          not null,
   RateMember           NUMBER                          not null,
   RateHierarchy        NUMBER                          not null,
   LoadNumber           VARCHAR2(8)                     not null,
   constraint PK_LMGROUPSA305 primary key (GroupID)
)
;

/*==============================================================*/
/* Table: LMGroupSASimple                                       */
/*==============================================================*/
create table LMGroupSASimple  (
   GroupID              NUMBER                          not null,
   RouteID              NUMBER                          not null,
   OperationalAddress   VARCHAR2(8)                     not null,
   NominalTimeout       NUMBER                          not null,
   MarkIndex            NUMBER                          not null,
   SpaceIndex           NUMBER                          not null,
   constraint PK_LMGROUPSASIMPLE primary key (GroupID)
)
;

/*==============================================================*/
/* Table: LMGroupVersacom                                       */
/*==============================================================*/
create table LMGroupVersacom  (
   DEVICEID             NUMBER                          not null,
   ROUTEID              NUMBER                          not null,
   UTILITYADDRESS       NUMBER                          not null,
   SECTIONADDRESS       NUMBER                          not null,
   CLASSADDRESS         NUMBER                          not null,
   DIVISIONADDRESS      NUMBER                          not null,
   ADDRESSUSAGE         CHAR(4)                         not null,
   RELAYUSAGE           CHAR(7)                         not null,
   SerialAddress        VARCHAR2(15)                    not null,
   constraint PK_LMGROUPVERSACOM primary key (DEVICEID)
)
;

/*==============================================================*/
/* Table: LMMacsScheduleCustomerList                            */
/*==============================================================*/
create table LMMacsScheduleCustomerList  (
   ScheduleID           NUMBER                          not null,
   LMCustomerDeviceID   NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key (ScheduleID, LMCustomerDeviceID)
)
;

/*==============================================================*/
/* Table: LMPROGRAM                                             */
/*==============================================================*/
create table LMPROGRAM  (
   DeviceID             NUMBER                          not null,
   ControlType          VARCHAR2(20)                    not null,
   ConstraintID         NUMBER                          not null,
   constraint PK_LMPROGRAM primary key (DeviceID)
)
;

insert into LMProgram values(0, 'Automatic', 0);

/*==============================================================*/
/* Table: LMProgramConstraints                                  */
/*==============================================================*/
create table LMProgramConstraints  (
   ConstraintID         NUMBER                          not null,
   ConstraintName       VARCHAR2(60)                    not null,
   AvailableWeekDays    VARCHAR2(8)                     not null,
   MaxHoursDaily        NUMBER                          not null,
   MaxHoursMonthly      NUMBER                          not null,
   MaxHoursSeasonal     NUMBER                          not null,
   MaxHoursAnnually     NUMBER                          not null,
   MinActivateTime      NUMBER                          not null,
   MinRestartTime       NUMBER                          not null,
   MaxDailyOps          NUMBER                          not null,
   MaxActivateTime      NUMBER                          not null,
   HolidayScheduleID    NUMBER                          not null,
   SeasonScheduleID     NUMBER                          not null,
   constraint PK_PRGCONSTR primary key (ConstraintID)
)
;

insert into LMProgramConstraints values (0, 'Default Constraint', 'YYYYYYYN', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

/*==============================================================*/
/* Table: LMProgramControlWindow                                */
/*==============================================================*/
create table LMProgramControlWindow  (
   DeviceID             NUMBER                          not null,
   WindowNumber         NUMBER                          not null,
   AvailableStartTime   NUMBER                          not null,
   AvailableStopTime    NUMBER                          not null,
   constraint PK_LMPROGRAMCONTROLWINDOW primary key (DeviceID, WindowNumber)
)
;

/*==============================================================*/
/* Table: LMProgramCurtailCustomerList                          */
/*==============================================================*/
create table LMProgramCurtailCustomerList  (
   ProgramID            NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   RequireAck           CHAR(1)                         not null,
   constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key (CustomerID, ProgramID)
)
;

/*==============================================================*/
/* Table: LMProgramCurtailment                                  */
/*==============================================================*/
create table LMProgramCurtailment  (
   DeviceID             NUMBER                          not null,
   MinNotifyTime        NUMBER                          not null,
   Heading              VARCHAR2(40)                    not null,
   MessageHeader        VARCHAR2(160)                   not null,
   MessageFooter        VARCHAR2(160)                   not null,
   AckTimeLimit         NUMBER                          not null,
   CanceledMsg          VARCHAR2(80)                    not null,
   StoppedEarlyMsg      VARCHAR2(80)                    not null,
   constraint PK_LMPROGRAMCURTAILMENT primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: LMProgramDirect                                       */
/*==============================================================*/
create table LMProgramDirect  (
   DeviceID             NUMBER                          not null,
   NotifyActiveOffset   NUMBER                          not null,
   Heading              VARCHAR2(40)                    not null,
   MessageHeader        VARCHAR2(160)                   not null,
   MessageFooter        VARCHAR2(160)                   not null,
   TriggerOffset        FLOAT                           not null,
   RestoreOffset        FLOAT                           not null,
   NotifyInactiveOffset NUMBER                          not null,
   constraint PK_LMPROGRAMDIRECT primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: LMProgramDirectGear                                   */
/*==============================================================*/
create table LMProgramDirectGear  (
   DeviceID             NUMBER                          not null,
   GearName             VARCHAR2(30)                    not null,
   GearNumber           NUMBER                          not null,
   ControlMethod        VARCHAR2(30)                    not null,
   MethodRate           NUMBER                          not null,
   MethodPeriod         NUMBER                          not null,
   MethodRateCount      NUMBER                          not null,
   CycleRefreshRate     NUMBER                          not null,
   MethodStopType       VARCHAR2(20)                    not null,
   ChangeCondition      VARCHAR2(24)                    not null,
   ChangeDuration       NUMBER                          not null,
   ChangePriority       NUMBER                          not null,
   ChangeTriggerNumber  NUMBER                          not null,
   ChangeTriggerOffset  FLOAT                           not null,
   PercentReduction     NUMBER                          not null,
   GroupSelectionMethod VARCHAR2(30)                    not null,
   MethodOptionType     VARCHAR2(30)                    not null,
   MethodOptionMax      NUMBER                          not null,
   GearID               NUMBER                          not null,
   RampInInterval       NUMBER                          not null,
   RampInPercent        NUMBER                          not null,
   RampOutInterval      NUMBER                          not null,
   RampOutPercent       NUMBER                          not null,
   FrontRampOption      VARCHAR2(80)                    not null,
   FrontRampTime        NUMBER                          not null,
   BackRampOption       VARCHAR2(80)                    not null,
   BackRampTime         NUMBER                          not null,
   KWReduction          FLOAT                           not null,
   constraint PK_LMPROGRAMDIRECTGEAR primary key (GearID)
)
;

alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique (DeviceID, GearNumber)
;

/*==============================================================*/
/* Table: LMProgramDirectGroup                                  */
/*==============================================================*/
create table LMProgramDirectGroup  (
   DeviceID             NUMBER                          not null,
   LMGroupDeviceID      NUMBER                          not null,
   GroupOrder           NUMBER                          not null,
   constraint PK_LMPROGRAMDIRECTGROUP primary key (DeviceID, GroupOrder)
)
;

/*==============================================================*/
/* Table: LMProgramEnergyExchange                               */
/*==============================================================*/
create table LMProgramEnergyExchange  (
   DeviceID             NUMBER                          not null,
   MinNotifyTime        NUMBER                          not null,
   Heading              VARCHAR2(40)                    not null,
   MessageHeader        VARCHAR2(160)                   not null,
   MessageFooter        VARCHAR2(160)                   not null,
   CanceledMsg          VARCHAR2(80)                    not null,
   StoppedEarlyMsg      VARCHAR2(80)                    not null,
   constraint PK_LMPROGRAMENERGYEXCHANGE primary key (DeviceID)
)
;

/*==============================================================*/
/* Table: LMThermoStatGear                                      */
/*==============================================================*/
create table LMThermoStatGear  (
   GearID               NUMBER                          not null,
   Settings             VARCHAR2(10)                    not null,
   MinValue             NUMBER                          not null,
   MaxValue             NUMBER                          not null,
   ValueB               NUMBER                          not null,
   ValueD               NUMBER                          not null,
   ValueF               NUMBER                          not null,
   Random               NUMBER                          not null,
   ValueTa              NUMBER                          not null,
   ValueTb              NUMBER                          not null,
   ValueTc              NUMBER                          not null,
   ValueTd              NUMBER                          not null,
   ValueTe              NUMBER                          not null,
   ValueTf              NUMBER                          not null,
   RampRate             FLOAT                          default 0 not null,
   constraint PK_LMTHERMOSTATGEAR primary key (GearID)
)
;

/*==============================================================*/
/* Table: LOGIC                                                 */
/*==============================================================*/
create table LOGIC  (
   LOGICID              NUMBER                          not null,
   LOGICNAME            VARCHAR2(20)                    not null,
   PERIODICRATE         NUMBER                          not null,
   STATEFLAG            VARCHAR2(10)                    not null,
   SCRIPTNAME           VARCHAR2(20)                    not null,
   constraint SYS_C0013445 primary key (LOGICID)
)
;

/*==============================================================*/
/* Table: MACROROUTE                                            */
/*==============================================================*/
create table MACROROUTE  (
   ROUTEID              NUMBER                          not null,
   SINGLEROUTEID        NUMBER                          not null,
   ROUTEORDER           NUMBER                          not null,
   constraint PK_MACROROUTE primary key (ROUTEID, ROUTEORDER)
)
;

/*==============================================================*/
/* Table: MACSchedule                                           */
/*==============================================================*/
create table MACSchedule  (
   ScheduleID           NUMBER                          not null,
   CategoryName         VARCHAR2(50)                    not null,
   HolidayScheduleID    NUMBER,
   CommandFile          VARCHAR2(180),
   CurrentState         VARCHAR2(12)                    not null,
   StartPolicy          VARCHAR2(20)                    not null,
   StopPolicy           VARCHAR2(20)                    not null,
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
   ManualStopTime       DATE,
   Template             NUMBER,
   constraint PK_MACSCHEDULE primary key (ScheduleID)
)
;

/*==============================================================*/
/* Table: MACSimpleSchedule                                     */
/*==============================================================*/
create table MACSimpleSchedule  (
   ScheduleID           NUMBER                          not null,
   TargetSelect         VARCHAR2(40),
   StartCommand         VARCHAR2(120),
   StopCommand          VARCHAR2(120),
   RepeatInterval       NUMBER,
   constraint PK_MACSIMPLESCHEDULE primary key (ScheduleID)
)
;

/*==============================================================*/
/* Table: MCTBroadCastMapping                                   */
/*==============================================================*/
create table MCTBroadCastMapping  (
   MCTBroadCastID       NUMBER                          not null,
   MctID                NUMBER                          not null,
   Ordering             NUMBER                          not null,
   constraint PK_MCTBROADCASTMAPPING primary key (MCTBroadCastID, MctID)
)
;

/*==============================================================*/
/* Table: MCTConfig                                             */
/*==============================================================*/
create table MCTConfig  (
   ConfigID             NUMBER                          not null,
   ConfigName           VARCHAR2(30)                    not null,
   ConfigType           NUMBER                          not null,
   ConfigMode           VARCHAR2(30)                    not null,
   MCTWire1             NUMBER                          not null,
   Ke1                  FLOAT                           not null,
   MCTWire2             NUMBER                          not null,
   Ke2                  FLOAT                           not null,
   MCTWire3             NUMBER                          not null,
   Ke3                  FLOAT                           not null,
   DisplayDigits        NUMBER                          not null,
   constraint PK_MCTCONFIG primary key (ConfigID)
)
;

/*==============================================================*/
/* Table: MCTConfigMapping                                      */
/*==============================================================*/
create table MCTConfigMapping  (
   MctID                NUMBER                          not null,
   ConfigID             NUMBER                          not null,
   constraint PK_MCTCONFIGMAPPING primary key (MctID, ConfigID)
)
;

/*==============================================================*/
/* Table: MSPInterface                                          */
/*==============================================================*/
create table MSPInterface  (
   VendorID             NUMBER                          not null,
   Interface            VARCHAR2(12)                    not null,
   Endpoint             VARCHAR2(32)                    not null,
   constraint PK_MSPINTERFACE primary key (VendorID, Interface, Endpoint)
)
;


insert into MSPInterface values (1, 'MR_CB', 'MR_CBSoap');
insert into MSPInterface values (1, 'MR_EA', 'MR_EASoap');
insert into MSPInterface values (1, 'OD_OA', 'OD_OASoap');
insert into MSPInterface values (1, 'CD_CB', 'CD_CBSoap');


/*==============================================================*/
/* Table: MSPVendor                                             */
/*==============================================================*/
create table MSPVendor  (
   VendorID             NUMBER                          not null,
   CompanyName          VARCHAR2(64)                    not null,
   UserName             VARCHAR2(64)                    not null,
   Password             VARCHAR2(64)                    not null,
   UniqueKey            VARCHAR2(32)                    not null,
   Timeout              NUMBER                          not null,
   URL                  VARCHAR2(120)                   not null,
   AppName              VARCHAR2(64)                    not null,
   OutUserName          VARCHAR2(64)                    not null,
   OutPassword          VARCHAR2(64)                    not null,
   constraint PK_MSPVENDOR primary key (VendorID)
)
;

insert into MSPVendor values (1, 'Cannon', '(none)', '(none)', 'meterNumber', 0, 'http://127.0.0.1:8080/soap/', 'Yukon', '(none)', '(none)');

/*==============================================================*/
/* Table: NotificationDestination                               */
/*==============================================================*/
create table NotificationDestination  (
   NotificationGroupID  NUMBER                          not null,
   RecipientID          NUMBER                          not null,
   Attribs              CHAR(16)                        not null,
   constraint PKey_NotDestID primary key (NotificationGroupID, RecipientID)
)
;

/*==============================================================*/
/* Table: NotificationGroup                                     */
/*==============================================================*/
create table NotificationGroup  (
   NotificationGroupID  NUMBER                          not null,
   GroupName            VARCHAR2(40)                    not null,
   DisableFlag          CHAR(1)                         not null,
   constraint PK_NOTIFICATIONGROUP primary key (NotificationGroupID)
)
;

insert into notificationgroup values( 1, '(none)', 'N' );

/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique index Indx_NOTIFGRPNme on NotificationGroup (
   GroupName ASC
)
;

/*==============================================================*/
/* Table: OperatorLoginGraphList                                */
/*==============================================================*/
create table OperatorLoginGraphList  (
   OperatorLoginID      NUMBER                          not null,
   GraphDefinitionID    NUMBER                          not null,
   constraint PK_OPERATORLOGINGRAPHLIST primary key (OperatorLoginID, GraphDefinitionID)
)
;

/*==============================================================*/
/* Table: PAOExclusion                                          */
/*==============================================================*/
create table PAOExclusion  (
   ExclusionID          NUMBER                          not null,
   PaoID                NUMBER                          not null,
   ExcludedPaoID        NUMBER                          not null,
   PointID              NUMBER                          not null,
   Value                NUMBER                          not null,
   FunctionID           NUMBER                          not null,
   FuncName             VARCHAR2(100)                   not null,
   FuncRequeue          NUMBER                          not null,
   FuncParams           VARCHAR2(200)                   not null,
   constraint PK_PAOEXCLUSION primary key (ExclusionID)
)
;

/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique index Indx_PAOExclus on PAOExclusion (
   PaoID ASC,
   ExcludedPaoID ASC
)
;

/*==============================================================*/
/* Table: PAOSchedule                                           */
/*==============================================================*/
create table PAOSchedule  (
   ScheduleID           NUMBER                          not null,
   NextRunTime          DATE                            not null,
   LastRunTime          DATE                            not null,
   IntervalRate         NUMBER                          not null,
   ScheduleName         VARCHAR2(64)                    not null,
   Disabled             CHAR(1)                         not null,
   constraint PK_PAOSCHEDULE primary key (ScheduleID)
)
;

/*==============================================================*/
/* Index: Indx_SchedName                                        */
/*==============================================================*/
create unique index Indx_SchedName on PAOSchedule (
   ScheduleName ASC
)
;

/*==============================================================*/
/* Table: PAOScheduleAssignment                                 */
/*==============================================================*/
create table PAOScheduleAssignment  (
   EventID              NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Command              VARCHAR2(128)                   not null,
   constraint PK_PAOSCHEDULEASSIGNMENT primary key (EventID)
)
;

/*==============================================================*/
/* Table: PAOowner                                              */
/*==============================================================*/
create table PAOowner  (
   OwnerID              NUMBER                          not null,
   ChildID              NUMBER                          not null,
   constraint PK_PAOOWNER primary key (OwnerID, ChildID)
)
;

/*==============================================================*/
/* Table: PEAKREPORT                                            */
/*==============================================================*/
create table PEAKREPORT  (
   resultID             INTEGER                         not null,
   deviceID             NUMBER,
   channel              INTEGER,
   peakType             VARCHAR2(50),
   runType              VARCHAR2(50),
   runDate              DATE,
   resultString         VARCHAR2(1500),
   constraint PK_PEAKREPORT primary key (resultID)
)
;

/*==============================================================*/
/* Table: POINT                                                 */
/*==============================================================*/
create table POINT  (
   POINTID              NUMBER                          not null,
   POINTTYPE            VARCHAR2(20)                    not null,
   POINTNAME            VARCHAR2(60)                    not null,
   PAObjectID           NUMBER                          not null,
   LOGICALGROUP         VARCHAR2(14)                    not null,
   STATEGROUPID         NUMBER                          not null,
   SERVICEFLAG          VARCHAR2(1)                     not null,
   ALARMINHIBIT         VARCHAR2(1)                     not null,
   PSEUDOFLAG           VARCHAR2(1)                     not null,
   POINTOFFSET          NUMBER                          not null,
   ARCHIVETYPE          VARCHAR2(12)                    not null,
   ARCHIVEINTERVAL      NUMBER                          not null,
   constraint Key_PT_PTID primary key (POINTID)
)
;

insert into point values( 7, 'Status','Porter Monitor',0,'Default',-7,'N','N','R',1000,'None',0);
insert into point values( 6, 'Status','Dispatch Monitor',0,'Default',-7,'N','N','R',1001,'None',0);
insert into point values( 5, 'Status','Scanner Monitor',0,'Default',-7,'N','N','R',1002,'None',0);
insert into point values( 4, 'Status','Calc Monitor',0,'Default',-7,'N','N','R',1003,'None',0);
insert into point values( 3, 'Status','Cap Control Monitor',0,'Default',-7,'N','N','R',1004,'None',0);
insert into point values( 2, 'Status','FDR Monitor',0,'Default',-7,'N','N','R',1005,'None',0);
insert into point values( 1, 'Status','Macs Monitor',0,'Default',-7,'N','N','R',1006,'None',0);
INSERT into point values( 0,   'System', 'System Point', 0, 'Default', 0, 'N', 'N', 'S', 0  ,'None', 0);
INSERT into point values( -1,  'System', 'Porter', 0, 'Default', 0, 'N', 'N', 'S', 1  ,'None', 0);
INSERT into point values( -2,  'System', 'Scanner', 0, 'Default', 0, 'N', 'N', 'S', 2  ,'None', 0);
INSERT into point values( -3,  'System', 'Dispatch', 0, 'Default', 0, 'N', 'N', 'S', 3  ,'None', 0);
INSERT into point values( -4,  'System', 'Macs', 0, 'Default', 0, 'N', 'N', 'S', 4  ,'None', 0);
INSERT into point values( -5,  'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 'S', 5  ,'None', 0);
INSERT into point values( -6,  'System', 'Notifcation', 0, 'Default', 0, 'N', 'N', 'S', 6  ,'None', 0);
INSERT into point values( -10, 'System', 'Load Management' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);
INSERT into point values( -100, 'System', 'Threshold' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);
insert into point values( 100,'Analog','Porter Work Count',0,'Default',0,'N','N','R',1500,'None',0);
INSERT into point values( -110, 'System', 'Multispeak' , 0, 'Default', 0, 'N', 'N', 'S', 110 ,'None', 0);

alter table POINT
   add constraint AK_KEY_PTNM_YUKPAOID unique (POINTNAME, PAObjectID)
;

/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create index Indx_PointStGrpID on POINT (
   STATEGROUPID ASC
)
;

/*==============================================================*/
/* Index: INDX_PAOBJECTID                                       */
/*==============================================================*/
create index INDX_PAOBJECTID on POINT (
   PAObjectID ASC
)
;

/*==============================================================*/
/* Index: INDX_PAOBJECTID_POFFSET                               */
/*==============================================================*/
create index INDX_PAOBJECTID_POFFSET on POINT (
   PAObjectID ASC,
   POINTOFFSET ASC
)
;

/*==============================================================*/
/* Index: INDX_PAOBJECTID_POINTID                               */
/*==============================================================*/
create index INDX_PAOBJECTID_POINTID on POINT (
   PAObjectID ASC,
   POINTID ASC
)
;

/*==============================================================*/
/* Index: INDX_POFFSET_POINTTYPE                                */
/*==============================================================*/
create index INDX_POFFSET_POINTTYPE on POINT (
   POINTOFFSET ASC,
   POINTTYPE ASC
)
;

/*==============================================================*/
/* Table: POINTACCUMULATOR                                      */
/*==============================================================*/
create table POINTACCUMULATOR  (
   POINTID              NUMBER                          not null,
   MULTIPLIER           FLOAT                           not null,
   DATAOFFSET           FLOAT                           not null,
   constraint PK_POINTACCUMULATOR primary key (POINTID)
)
;

/*==============================================================*/
/* Table: POINTANALOG                                           */
/*==============================================================*/
create table POINTANALOG  (
   POINTID              NUMBER                          not null,
   DEADBAND             FLOAT                           not null,
   TRANSDUCERTYPE       VARCHAR2(14)                    not null,
   MULTIPLIER           FLOAT                           not null,
   DATAOFFSET           FLOAT                           not null,
   constraint PK_POINTANALOG primary key (POINTID)
)
;

insert into pointanalog values( 100, 0, 'None', 1, 0 );

/*==============================================================*/
/* Table: POINTLIMITS                                           */
/*==============================================================*/
create table POINTLIMITS  (
   POINTID              NUMBER                          not null,
   LIMITNUMBER          NUMBER                          not null,
   HIGHLIMIT            FLOAT                           not null,
   LOWLIMIT             FLOAT                           not null,
   LIMITDURATION        NUMBER                          not null,
   constraint PK_POINTLIMITS primary key (POINTID, LIMITNUMBER)
)
;

/*==============================================================*/
/* Table: POINTSTATUS                                           */
/*==============================================================*/
create table POINTSTATUS  (
   POINTID              NUMBER                          not null,
   INITIALSTATE         NUMBER                          not null,
   CONTROLTYPE          VARCHAR2(12)                    not null,
   CONTROLINHIBIT       VARCHAR2(1)                     not null,
   ControlOffset        NUMBER                          not null,
   CloseTime1           NUMBER                          not null,
   CloseTime2           NUMBER                          not null,
   StateZeroControl     VARCHAR2(100)                   not null,
   StateOneControl      VARCHAR2(100)                   not null,
   CommandTimeOut       NUMBER                          not null,
   constraint PK_PtStatus primary key (POINTID)
)
;

insert into pointstatus values( 7, 0, 'none', 'N', 0, 0, 0, 'none','none',0 );
insert into pointstatus values( 6, 0, 'none', 'N', 0, 0, 0, 'none','none',0 );
insert into pointstatus values( 5, 0, 'none', 'N', 0, 0, 0, 'none','none',0 );
insert into pointstatus values( 4, 0, 'none', 'N', 0, 0, 0, 'none','none',0 );
insert into pointstatus values( 3, 0, 'none', 'N', 0, 0, 0, 'none','none',0 );
insert into pointstatus values( 2, 0, 'none', 'N', 0, 0, 0, 'none','none',0 );
insert into pointstatus values( 1, 0, 'none', 'N', 0, 0, 0, 'none','none',0 );

/*==============================================================*/
/* Table: POINTTRIGGER                                          */
/*==============================================================*/
create table POINTTRIGGER  (
   PointID              NUMBER                          not null,
   TriggerID            NUMBER                          not null,
   TriggerDeadband      FLOAT                           not null,
   VerificationID       NUMBER                          not null,
   VerificationDeadband FLOAT                           not null,
   CommandTimeout       NUMBER                          not null,
   Parameters           VARCHAR2(40)                    not null,
   constraint PK_POINTTRIGGER primary key (PointID)
)
;

/*==============================================================*/
/* Table: POINTUNIT                                             */
/*==============================================================*/
create table POINTUNIT  (
   POINTID              NUMBER                          not null,
   UOMID                NUMBER                          not null,
   DECIMALPLACES        NUMBER                          not null,
   HighReasonabilityLimit FLOAT                           not null,
   LowReasonabilityLimit FLOAT                           not null,
   DecimalDigits        NUMBER                          not null,
   constraint PK_POINTUNITID primary key (POINTID)
)
;

insert into pointunit values( 100, 9, 1, 1.0E+30, -1.0E+30, 0);

/*==============================================================*/
/* Index: INDX_UOMID_POINTID                                    */
/*==============================================================*/
create index INDX_UOMID_POINTID on POINTUNIT (
   UOMID ASC,
   POINTID ASC
)
;

/*==============================================================*/
/* Table: PORTDIALUPMODEM                                       */
/*==============================================================*/
create table PORTDIALUPMODEM  (
   PORTID               NUMBER                          not null,
   MODEMTYPE            VARCHAR2(30)                    not null,
   INITIALIZATIONSTRING VARCHAR2(50)                    not null,
   PREFIXNUMBER         VARCHAR2(10)                    not null,
   SUFFIXNUMBER         VARCHAR2(10)                    not null,
   constraint PK_PORTDIALUPMODEM primary key (PORTID)
)
;

/*==============================================================*/
/* Table: PORTLOCALSERIAL                                       */
/*==============================================================*/
create table PORTLOCALSERIAL  (
   PORTID               NUMBER                          not null,
   PHYSICALPORT         VARCHAR2(8)                     not null,
   constraint PK_PORTLOCALSERIAL primary key (PORTID)
)
;

/*==============================================================*/
/* Table: PORTRADIOSETTINGS                                     */
/*==============================================================*/
create table PORTRADIOSETTINGS  (
   PORTID               NUMBER                          not null,
   RTSTOTXWAITSAMED     NUMBER                          not null,
   RTSTOTXWAITDIFFD     NUMBER                          not null,
   RADIOMASTERTAIL      NUMBER                          not null,
   REVERSERTS           NUMBER                          not null,
   constraint PK_PORTRADIOSETTINGS primary key (PORTID)
)
;

/*==============================================================*/
/* Table: PORTSETTINGS                                          */
/*==============================================================*/
create table PORTSETTINGS  (
   PORTID               NUMBER                          not null,
   BAUDRATE             NUMBER                          not null,
   CDWAIT               NUMBER                          not null,
   LINESETTINGS         VARCHAR2(8)                     not null,
   constraint PK_PORTSETTINGS primary key (PORTID)
)
;

/*==============================================================*/
/* Table: PORTTERMINALSERVER                                    */
/*==============================================================*/
create table PORTTERMINALSERVER  (
   PORTID               NUMBER                          not null,
   IPADDRESS            VARCHAR2(64)                    not null,
   SOCKETPORTNUMBER     NUMBER                          not null,
   constraint PK_PORTTERMINALSERVER primary key (PORTID)
)
;

/*==============================================================*/
/* Table: PROFILEPEAKRESULT                                     */
/*==============================================================*/
create table PROFILEPEAKRESULT  (
   ResultId             NUMBER                          not null,
   DeviceId             NUMBER                          not null,
   ResultFrom           VARCHAR2(30)                    not null,
   ResultTo             VARCHAR2(30)                    not null,
   RunDate              VARCHAR2(30)                    not null,
   PeakDay              VARCHAR2(30)                    not null,
   Usage                VARCHAR2(25)                    not null,
   Demand               VARCHAR2(25)                    not null,
   AverageDailyUsage    VARCHAR2(25)                    not null,
   TotalUsage           VARCHAR2(25)                    not null,
   ResultType           VARCHAR2(5)                     not null,
   Days                 NUMBER                          not null,
   constraint PK_PROFILEPEAKRESULT primary key (ResultId)
)
;

/*==============================================================*/
/* Table: PointAlarming                                         */
/*==============================================================*/
create table PointAlarming  (
   PointID              NUMBER                          not null,
   AlarmStates          VARCHAR2(32)                    not null,
   ExcludeNotifyStates  VARCHAR2(32)                    not null,
   NotifyOnAcknowledge  CHAR(1)                         not null,
   NotificationGroupID  NUMBER                          not null,
   RecipientID          NUMBER                          not null,
   constraint PK_POINTALARMING primary key (PointID)
)
;

insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;

/*==============================================================*/
/* Table: PortTiming                                            */
/*==============================================================*/
create table PortTiming  (
   PORTID               NUMBER                          not null,
   PRETXWAIT            NUMBER                          not null,
   RTSTOTXWAIT          NUMBER                          not null,
   POSTTXWAIT           NUMBER                          not null,
   RECEIVEDATAWAIT      NUMBER                          not null,
   EXTRATIMEOUT         NUMBER                          not null,
   constraint PK_PORTTIMING primary key (PORTID)
)
;

/*==============================================================*/
/* Table: RAWPOINTHISTORY                                       */
/*==============================================================*/
create table RAWPOINTHISTORY  (
   CHANGEID             NUMBER                          not null,
   POINTID              NUMBER                          not null,
   TIMESTAMP            DATE                            not null,
   QUALITY              NUMBER                          not null,
   VALUE                FLOAT                           not null,
   millis               SMALLINT                        not null,
   constraint SYS_C0013322 primary key (CHANGEID)
)
;

/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create index Index_PointID on RAWPOINTHISTORY (
   POINTID ASC
)
;

/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create index Indx_TimeStamp on RAWPOINTHISTORY (
   TIMESTAMP ASC
)
;

/*==============================================================*/
/* Index: Indx_RwPtHisPtIDTst                                   */
/*==============================================================*/
create index Indx_RwPtHisPtIDTst on RAWPOINTHISTORY (
   POINTID ASC,
   TIMESTAMP ASC
)
;

/*==============================================================*/
/* Table: RepeaterRoute                                         */
/*==============================================================*/
create table RepeaterRoute  (
   ROUTEID              NUMBER                          not null,
   DEVICEID             NUMBER                          not null,
   VARIABLEBITS         NUMBER                          not null,
   REPEATERORDER        NUMBER                          not null,
   constraint PK_REPEATERROUTE primary key (ROUTEID, DEVICEID)
)
;

/*==============================================================*/
/* Table: Route                                                 */
/*==============================================================*/
create table Route  (
   RouteID              NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   DefaultRoute         CHAR(1)                         not null,
   constraint SYS_RoutePK primary key (RouteID)
)
;

INSERT INTO Route VALUES (0,0,'N');

/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique index Indx_RouteDevID on Route (
   DeviceID DESC,
   RouteID ASC
)
;

/*==============================================================*/
/* Table: STATE                                                 */
/*==============================================================*/
create table STATE  (
   STATEGROUPID         NUMBER                          not null,
   RAWSTATE             NUMBER                          not null,
   TEXT                 VARCHAR2(32)                    not null,
   FOREGROUNDCOLOR      NUMBER                          not null,
   BACKGROUNDCOLOR      NUMBER                          not null,
   ImageID              NUMBER                          not null,
   constraint PK_STATE primary key (STATEGROUPID, RAWSTATE)
)
;

insert into state values(-7, 0, 'Normal',0,6,0);
insert into state values(-7, 1, 'NonCriticalFailure',1,6,0);
insert into state values(-7, 2, 'CriticalFailure',2,6,0);
insert into state values(-7, 3, 'Unresponsive',3,6,0);
insert into state values(-6, 0, 'Confirmed Disconnected', 1, 6, 0);
insert into state values(-6, 1, 'Connected', 0, 6, 0);
insert into state values(-6, 2, 'Unconfirmed Disconnected', 3, 6, 0);
insert into state values(-6, 3, 'Connect Armed', 5, 6, 0);
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
INSERT INTO State VALUES(-5,10, 'Priority 10', 9, 6, 0);
INSERT INTO State VALUES(-3, 0, 'CalculatedText', 0, 6 , 0);
INSERT INTO State VALUES(-2, 0, 'AccumulatorText', 0, 6 , 0);
INSERT INTO State VALUES(-1, 0, 'Normal', 0, 6 , 0);
INSERT INTO State VALUES(-1, 1, 'Non-update', 1, 6 , 0);
INSERT INTO State VALUES(-1, 2, 'Rate of Change', 2, 6 , 0);
INSERT INTO State VALUES(-1, 3, 'Limit Set 1', 3, 6 , 0);
INSERT INTO State VALUES(-1, 4, 'Limit Set 2', 4, 6 , 0);
INSERT INTO State VALUES(-1, 5, 'High Reasonability', 5, 6 , 0);
INSERT INTO State VALUES(-1, 6, 'Low Reasonability', 6, 6 , 0);
INSERT INTO State VALUES(-1, 7, 'Low Limit 1', 7, 6 , 0);
INSERT INTO State VALUES(-1, 8, 'Low Limit 2', 8, 6 , 0);
INSERT INTO State VALUES(-1, 9, 'High Limit 1', 9, 6 , 0);
INSERT INTO State VALUES(-1, 10, 'High Limit 2', 10, 6 , 0);
INSERT INTO State VALUES( 0, 0, 'SystemText', 0, 6 , 0);
INSERT INTO State VALUES( 1,-1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES( 1, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES( 1, 1, 'Closed', 1, 6 , 0);
INSERT INTO State VALUES( 2,-1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES( 2, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES( 2, 1, 'Closed', 1, 6 , 0);
INSERT INTO State VALUES( 2, 2, 'Unknown', 2, 6 , 0);
INSERT INTO State VALUES( 3,-1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES( 3, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES( 3, 1, 'Close', 1, 6 , 0);
INSERT INTO State VALUES( 3, 2, 'OpenQuestionable', 10, 6 , 0);
INSERT INTO State VALUES( 3, 3, 'CloseQuestionable', 3, 6 , 0);
INSERT INTO State VALUES( 3, 4, 'OpenFail', 4, 6 , 0);
INSERT INTO State VALUES( 3, 5, 'CloseFail', 5, 6 , 0);
INSERT INTO State VALUES( 3, 6, 'OpenPending', 7, 6 , 0);
INSERT INTO State VALUES( 3, 7, 'ClosePending', 8, 6 , 0);
INSERT INTO State VALUES( 4, 0, 'False', 0, 6, 0);
INSERT INTO State VALUES( 4, 1, 'True', 1, 6, 0);
INSERT INTO State VALUES( 5, 0, 'Remote', 0, 6, 0);
INSERT INTO State VALUES( 5, 1, 'Local', 1, 6, 0);
INSERT INTO State VALUES( 6, 0, 'Enable', 5, 6 , 0);
INSERT INTO State VALUES( 6, 1, 'Disable',9, 6 , 0);
INSERT INTO State VALUES( 6, 2, 'Pending',7, 6 , 0);
INSERT INTO State VALUES( 6, 3, 'Alt - Enabled', 2, 6 , 0);
INSERT INTO State VALUES( 7, 0, 'Verify All', 2, 6 , 0);
INSERT INTO State VALUES( 7, 1, 'Verify Stop', 6, 6 , 0);
INSERT INTO State VALUES(-8, 0, 'Inactive', 0, 6, 0);
INSERT INTO State VALUES(-8, 1, 'Active', 2, 6, 0);

/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create index Indx_StateRaw on STATE (
   RAWSTATE ASC
)
;

/*==============================================================*/
/* Table: STATEGROUP                                            */
/*==============================================================*/
create table STATEGROUP  (
   STATEGROUPID         NUMBER                          not null,
   NAME                 VARCHAR2(20)                    not null,
   GroupType            VARCHAR2(20)                    not null,
   constraint SYS_C0013128 primary key (STATEGROUPID)
)
;

insert into stategroup values(-7, 'Thread Monitor', 'Status');
insert into stategroup values(-6, '410 Disconnect', 'Status');
INSERT INTO StateGroup VALUES(-5, 'Event Priority', 'System' );
INSERT INTO StateGroup VALUES(-2, 'DefaultAccumulator', 'Accumulator' );
INSERT INTO StateGroup VALUES(-3, 'DefaultCalculated', 'Calculated' );
INSERT INTO StateGroup VALUES(-1, 'DefaultAnalog', 'Analog' );
INSERT INTO StateGroup VALUES( 0, 'SystemState', 'System' );
INSERT INTO StateGroup VALUES( 1, 'TwoStateStatus', 'Status' );
INSERT INTO StateGroup VALUES( 2, 'ThreeStateStatus', 'Status' );
INSERT INTO StateGroup VALUES( 3, 'CapBankStatus', 'Status' );
INSERT INTO StateGroup VALUES( 4, 'TrueFalse', 'Status' );
INSERT INTO stategroup VALUES( 5, 'RemoteLocal', 'Status' );
INSERT INTO StateGroup VALUES( 6, '1LNSUBSTATE', 'Status' );
INSERT INTO StateGroup VALUES( 7, '1LNVERIFY', 'Status' );
insert into StateGroup values (-8, 'TwoStateActive', 'Status');

/*==============================================================*/
/* Index: Indx_STATEGRP_Nme                                     */
/*==============================================================*/
create unique index Indx_STATEGRP_Nme on STATEGROUP (
   NAME DESC
)
;

/*==============================================================*/
/* Table: SYSTEMLOG                                             */
/*==============================================================*/
create table SYSTEMLOG  (
   LOGID                NUMBER                          not null,
   POINTID              NUMBER                          not null,
   DATETIME             DATE                            not null,
   SOE_TAG              NUMBER                          not null,
   TYPE                 NUMBER                          not null,
   PRIORITY             NUMBER                          not null,
   ACTION               VARCHAR2(60),
   DESCRIPTION          VARCHAR2(120),
   USERNAME             VARCHAR2(64),
   millis               SMALLINT                        not null,
   constraint SYS_C0013407 primary key (LOGID)
)
;

/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create index Indx_SYSLG_PtId on SYSTEMLOG (
   POINTID ASC
)
;

/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create index Indx_SYSLG_Date on SYSTEMLOG (
   DATETIME ASC
)
;

/*==============================================================*/
/* Index: INDX_SYSLG_PTID_TS                                    */
/*==============================================================*/
create unique index INDX_SYSLG_PTID_TS on SYSTEMLOG (
   LOGID ASC,
   POINTID ASC,
   DATETIME ASC
)
;

/*==============================================================*/
/* Table: SeasonSchedule                                        */
/*==============================================================*/
create table SeasonSchedule  (
   ScheduleID           NUMBER                          not null,
   ScheduleName         VARCHAR2(40)                    not null,
   constraint PK_SEASONSCHEDULE primary key (ScheduleID)
)
;

insert into SeasonSchedule values( 0, 'Empty Schedule' );
insert into seasonSchedule values (-1,'No Season');

/*==============================================================*/
/* Table: SequenceNumber                                        */
/*==============================================================*/
create table SequenceNumber  (
   LastValue            NUMBER                          not null,
   SequenceName         VARCHAR2(20)                    not null,
   constraint PK_SEQUENCENUMBER primary key (SequenceName)
)
;

insert into SequenceNumber values (1,'DeviceReadLog');
insert into SequenceNumber values (1,'DeviceReadRequestLog');
insert into SequenceNumber values (1,'DeviceReadJobLog');
insert into sequenceNumber values (100, 'BillingFileFormats');
insert into sequencenumber values (1, 'CapbankComment');

/*==============================================================*/
/* Table: SettlementConfig                                      */
/*==============================================================*/
create table SettlementConfig  (
   ConfigID             NUMBER                          not null,
   FieldName            VARCHAR2(64)                    not null,
   FieldValue           VARCHAR2(64)                    not null,
   CTISettlement        VARCHAR2(32)                    not null,
   YukonDefID           NUMBER                          not null,
   Description          VARCHAR2(128)                   not null,
   EntryID              NUMBER                          not null,
   RefEntryID           NUMBER                          not null,
   constraint PK_SETTLEMENTCONFIG primary key (ConfigID)
)
;

insert into SettlementConfig values (-1, 'CDI Rate', '0', 'HECO', '3651', 'Controlled Demand Incentive, Dollars per kW.', 0, 0);
insert into SettlementConfig values (-2, 'ERI Rate', '0', 'HECO', '3651', 'Energy Reduction Incentive, Dollars per kWh.', 0, 0);
insert into SettlementConfig values (-3, 'UF Delay', '0', 'HECO', '3651', 'Under frequency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-4, 'Dispatched Delay', '0', 'HECO', '3651', 'Dispatched Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-5, 'Emergency Delay', '0', 'HECO', '3651', 'Emergency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-6, 'Allowed Violations', '0', 'HECO', '3651', 'Max number of allowed violations, deviations.', 0, 0);
insert into SettlementConfig values (-7, 'Restore Duration', '0', 'HECO', '3651', 'Duration for event restoration to occur, in minutes.', 0, 0);
insert into SettlementConfig values (-8, 'Demand Charge', '0', 'HECO', '3651', 'Rate Schedule billing demand charge', 0, 0);

/*==============================================================*/
/* Table: TEMPLATE                                              */
/*==============================================================*/
create table TEMPLATE  (
   TEMPLATENUM          NUMBER                          not null,
   NAME                 VARCHAR2(40)                    not null,
   DESCRIPTION          VARCHAR2(200),
   constraint SYS_C0013425 primary key (TEMPLATENUM)
)
;

insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon  Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon  Template');

/*==============================================================*/
/* Table: TEMPLATECOLUMNS                                       */
/*==============================================================*/
create table TEMPLATECOLUMNS  (
   TEMPLATENUM          NUMBER                          not null,
   TITLE                VARCHAR2(50)                    not null,
   TYPENUM              NUMBER                          not null,
   ORDERING             NUMBER                          not null,
   WIDTH                NUMBER                          not null,
   constraint PK_TEMPLATECOLUMNS primary key (TEMPLATENUM, TITLE)
)
;

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

/*==============================================================*/
/* Table: TOUATTRIBUTEMAPPING                                   */
/*==============================================================*/
create table TOUATTRIBUTEMAPPING  (
   touid                NUMBER(6)                       not null,
   displayname          VARCHAR2(50)                    not null,
   peakAttribute        VARCHAR2(50)                    not null,
   usageAttribute       VARCHAR2(50)                    not null,
   constraint PK_TOUATTRIBUTEMAPPING primary key (touid)
)
;

INSERT INTO TouAttributeMapping VALUES (1, 'A', 'TOU_RATE_A_PEAK_DEMAND', 'TOU_RATE_A_USAGE');
INSERT INTO TouAttributeMapping VALUES (2, 'B', 'TOU_RATE_B_PEAK_DEMAND', 'TOU_RATE_B_USAGE');
INSERT INTO TouAttributeMapping VALUES (3, 'C', 'TOU_RATE_C_PEAK_DEMAND', 'TOU_RATE_C_USAGE');
INSERT INTO TouAttributeMapping VALUES (4, 'D', 'TOU_RATE_D_PEAK_DEMAND', 'TOU_RATE_D_USAGE');

/*==============================================================*/
/* Table: TOUDay                                                */
/*==============================================================*/
create table TOUDay  (
   TOUDayID             NUMBER                          not null,
   TOUDayName           VARCHAR2(32)                    not null,
   constraint PK_TOUDAY primary key (TOUDayID)
)
;

/*==============================================================*/
/* Table: TOUDayMapping                                         */
/*==============================================================*/
create table TOUDayMapping  (
   TOUScheduleID        NUMBER                          not null,
   TOUDayID             NUMBER                          not null,
   TOUDayOffset         NUMBER                          not null,
   constraint PK_TOUDAYMAPPING primary key (TOUScheduleID, TOUDayOffset)
)
;

/*==============================================================*/
/* Table: TOUDayRateSwitches                                    */
/*==============================================================*/
create table TOUDayRateSwitches  (
   TOURateSwitchID      NUMBER                          not null,
   SwitchRate           VARCHAR2(4)                     not null,
   SwitchOffset         NUMBER                          not null,
   TOUDayID             NUMBER                          not null,
   constraint PK_TOURATESWITCH primary key (TOURateSwitchID)
)
;

/*==============================================================*/
/* Index: Indx_todsw_idoff                                      */
/*==============================================================*/
create unique index Indx_todsw_idoff on TOUDayRateSwitches (
   SwitchOffset ASC,
   TOUDayID ASC
)
;

/*==============================================================*/
/* Table: TOUSchedule                                           */
/*==============================================================*/
create table TOUSchedule  (
   TOUScheduleID        NUMBER                          not null,
   TOUScheduleName      VARCHAR2(32)                    not null,
   TOUDefaultRate       VARCHAR2(4)                     not null,
   constraint PK_TOUSCHEDULE primary key (TOUScheduleID)
)
;

insert into TOUSchedule values (0, '(none)', 0);

/*==============================================================*/
/* Table: TagLog                                                */
/*==============================================================*/
create table TagLog  (
   LogID                NUMBER                          not null,
   InstanceID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   TagID                NUMBER                          not null,
   UserName             VARCHAR2(64)                    not null,
   Action               VARCHAR2(20)                    not null,
   Description          VARCHAR2(120)                   not null,
   TagTime              DATE                            not null,
   RefStr               VARCHAR2(60)                    not null,
   ForStr               VARCHAR2(60)                    not null,
   constraint PK_TAGLOG primary key (LogID)
)
;

/*==============================================================*/
/* Table: Tags                                                  */
/*==============================================================*/
create table Tags  (
   TagID                NUMBER                          not null,
   TagName              VARCHAR2(60)                    not null,
   TagLevel             NUMBER                          not null,
   Inhibit              CHAR(1)                         not null,
   ColorID              NUMBER                          not null,
   ImageID              NUMBER                          not null,
   constraint PK_TAGS primary key (TagID)
)
;

insert into tags values(-1, 'Out Of Service', 1, 'Y', 1, 0);
insert into tags values(-2, 'Info', 1, 'N', 6, 0);
insert into tags values (-3, 'Cap Bank Operational State', 1, 'N', 0, 0);
insert into tags values (-4, 'Enablement State', 1, 'N', 0, 0);
insert into tags values (-5, 'OVUV Enablement State', 1, 'N', 0, 0); 

/*==============================================================*/
/* Table: TemplateDisplay                                       */
/*==============================================================*/
create table TemplateDisplay  (
   DisplayNum           NUMBER                          not null,
   TemplateNum          NUMBER                          not null,
   constraint PK_TEMPLATEDISPLAY primary key (DisplayNum)
)
;

/*==============================================================*/
/* Table: UNITMEASURE                                           */
/*==============================================================*/
create table UNITMEASURE  (
   UOMID                NUMBER                          not null,
   UOMName              VARCHAR2(8)                     not null,
   CalcType             NUMBER                          not null,
   LongName             VARCHAR2(40)                    not null,
   Formula              VARCHAR2(80)                    not null,
   constraint SYS_C0013344 primary key (UOMID)
)
;

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


/*==============================================================*/
/* Table: UserPaoPermission                                     */
/*==============================================================*/
create table UserPaoPermission  (
   UserPaoPermissionID  NUMBER                          not null,
   UserID               NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Permission           VARCHAR2(50)                    not null,
   Allow                VARCHAR2(5)                    default 'Allow' not null,
   constraint PK_USERPAOPERMISSION primary key (UserPaoPermissionID)
)
;

alter table UserPaoPermission
   add constraint AK_USRPAOPERM unique (UserID, PaoID, Permission)
;

/*==============================================================*/
/* Table: VersacomRoute                                         */
/*==============================================================*/
create table VersacomRoute  (
   ROUTEID              NUMBER                          not null,
   UTILITYID            NUMBER                          not null,
   SECTIONADDRESS       NUMBER                          not null,
   CLASSADDRESS         NUMBER                          not null,
   DIVISIONADDRESS      NUMBER                          not null,
   BUSNUMBER            NUMBER                          not null,
   AMPCARDSET           NUMBER                          not null,
   constraint PK_VERSACOMROUTE primary key (ROUTEID)
)
;

/*==============================================================*/
/* Table: YukonGroup                                            */
/*==============================================================*/
create table YukonGroup  (
   GroupID              NUMBER                          not null,
   GroupName            VARCHAR2(120)                   not null,
   GroupDescription     VARCHAR2(200)                   not null,
   constraint PK_YUKONGROUP primary key (GroupID)
)
;

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

/*==============================================================*/
/* Table: YukonGroupRole                                        */
/*==============================================================*/
create table YukonGroupRole  (
   GroupRoleID          NUMBER                          not null,
   GroupID              NUMBER                          not null,
   RoleID               NUMBER                          not null,
   RolePropertyID       NUMBER                          not null,
   Value                VARCHAR2(1000)                  not null,
   constraint PK_YUKONGRPROLE primary key (GroupRoleID)
)
;

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
insert into YukonGroupRole values(-14,-1,-1,-1013,'(none)');
insert into YukonGroupRole values(-15,-1,-1,-1014,'CannonLogo.gif');
insert into YukonGroupRole values(-16,-1,-1,-1015,'(none)');
insert into YukonGroupRole values(-17,-1,-1,-1016,'(none)');
insert into YukonGroupRole values(-18,-1,-1,-1017,'(none)');
insert into YukonGroupRole values(-19,-1,-1,-1018,'(none)');

insert into YukonGroupRole values(-20,-1,-1,-1019,'(none)');
insert into YukonGroupRole values(-21,-1,-1,-1020,'(none)');

insert into YukonGroupRole values(-50,-1,-4,-1308,'(none)');
insert into YukonGroupRole values(-51,-1,-4,-1309,'(none)');
insert into YukonGroupRole values(-52,-1,-4,-1310,'(none)');
insert into YukonGroupRole values(-53,-1,-4,-1311,'(none)');
insert into YukonGroupRole values(-54,-1,-4,-1312,'(none)');
insert into YukonGroupRole values(-55,-1,-4,-1313,'(none)');
insert into YukonGroupRole values(-56,-1,-4,-1314,'(none)');
insert into YukonGroupRole values(-57,-1,-4,-1315,'(none)');
insert into YukonGroupRole values(-58,-1,-4,-1316,'(none)');
insert into YukonGroupRole values(-59,-1,-4,-1317,'(none)');

insert into YukonGroupRole values(-70,-1,-5,-1400,'(none)');
insert into YukonGroupRole values(-71,-1,-5,-1401,'(none)');
insert into YukonGroupRole values(-72,-1,-5,-1402,'(none)');

insert into YukonGroupRole values(-85,-1,-4,-1300,'(none)');
insert into YukonGroupRole values(-86,-1,-4,-1301,'(none)');
insert into YukonGroupRole values(-87,-1,-4,-1302,'(none)');
insert into YukonGroupRole values(-88,-1,-4,-1303,'(none)');
insert into YukonGroupRole values(-89,-1,-4,-1304,'(none)');
insert into YukonGroupRole values(-90,-1,-4,-1305,'(none)');
insert into YukonGroupRole values(-91,-1,-4,-1306,'(none)');
insert into YukonGroupRole values(-92,-1,-4,-1307,'(none)');

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
insert into YukonGroupRole values(-110,-100,-100,-10010,'(none)');
insert into YukonGroupRole values(-111,-100,-100,-10011,'(none)');

/* TDC */
insert into YukonGroupRole values(-120,-100,-101,-10100,'(none)');
insert into YukonGroupRole values(-121,-100,-101,-10101,'(none)');
insert into YukonGroupRole values(-122,-100,-101,-10102,'(none)');
insert into YukonGroupRole values(-123,-100,-101,-10103,'(none)');
insert into YukonGroupRole values(-124,-100,-101,-10104,'(none)');
insert into YukonGroupRole values(-127,-100,-101,-10107,'(none)');
insert into YukonGroupRole values(-128,-100,-101,-10108,'(none)');
insert into YukonGroupRole values(-130,-100,-101,-10111,'(none)');

/* Trending */
insert into YukonGroupRole values(-150,-100,-102,-10200,'(none)');

insert into YukonGroupRole values(-152,-100,-102,-10202,'(none)');
insert into YukonGroupRole values(-153,-100,-102,-10203,'(none)');
insert into YukonGroupRole values(-154,-100,-102,-10204,'(none)');
insert into YukonGroupRole values(-155,-100,-102,-10205,'(none)');
insert into YukonGroupRole values(-156,-100,-102,-10206,'(none)');
insert into YukonGroupRole values(-157,-100,-102,-10207,'(none)');
insert into YukonGroupRole values(-158,-100,-102,-10208,'(none)');
insert into YukonGroupRole values(-159,-100,-102,-10209,'(none)');
insert into YukonGroupRole values(-160,-100,-102,-10210,'(none)');
insert into YukonGroupRole values(-161,-100,-102,-10211,'(none)');
insert into YukonGroupRole values(-162,-100,-102,-10212,'(none)');
insert into YukonGroupRole values(-163,-100,-102,-10213,'(none)');
insert into YukonGroupRole values(-164,-100,-102,-10214,'(none)');
insert into YukonGroupRole values(-165,-100,-102,-10215,'(none)');
insert into YukonGroupRole values(-166,-100,-102,-10216,'(none)');
insert into YukonGroupRole values(-167,-100,-102,-10217,'(none)');
insert into YukonGroupRole values(-168,-100,-102,-10218,'(none)');
insert into YukonGroupRole values(-169,-100,-102,-10219,'(none)');
insert into YukonGroupRole values(-149,-100,-102,-10220,'(none)');
insert into YukonGroupRole values(-148,-100,-102,-10221,'(none)');

/* Commander */
insert into YukonGroupRole values(-170,-100,-103,-10300,'(none)');
insert into YukonGroupRole values(-171,-100,-103,-10301,'true');
insert into YukonGroupRole values(-172,-100,-103,-10302,'true');
insert into YukonGroupRole values(-173,-100,-103,-10303,'false');
insert into YukonGroupRole values(-174,-100,-103,-10304,'false');
insert into YukonGroupRole values(-175,-100,-103,-10305,'(none)');

insert into YukonGroupRole values(-180,-100,-106,-10600,'(none)');

/* Calc Historical for Yukon Gorup */
insert into YukonGroupRole values(-190,-1,-104,-10400,'(none)');
insert into YukonGroupRole values(-191,-1,-104,-10401,'(none)');
insert into YukonGroupRole values(-192,-1,-104,-10402,'(none)');

/* Web Graph for Yukon Gorup */
insert into YukonGroupRole values(-210,-1,-105,-10500,'(none)');
insert into YukonGroupRole values(-211,-1,-105,-10501,'(none)');

/* Billing for Yukon Gorup */
insert into YukonGroupRole values(-230,-1,-6,-1500,'(none)');
insert into YukonGroupRole values(-231,-1,-6,-1501,'(none)');

insert into YukonGroupRole values(-233,-1,-6,-1503,'(none)');
insert into YukonGroupRole values(-234,-1,-6,-1504,'(none)');
insert into YukonGroupRole values(-235,-1,-6,-1505,'(none)');
insert into YukonGroupRole values(-236,-1,-6,-1506,'(none)');
insert into YukonGroupRole values(-237,-1,-6,-1507,'(none)');

/*Multispeak*/
insert into YukonGroupRole values(-270,-1,-7,-1600,'0');

/*Configuration (Device)*/
insert into YukonGroupRole values(-280,-1,-8,-1700,'{name}');

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
insert into yukongrouprole values (-40700, -302, -108, -10807, '(none)');
insert into yukongrouprole values (-40701, -302, -108, -10808, '(none)');
insert into yukongrouprole values (-40703, -302, -108, -10810, '(none)');
insert into yukongrouprole values (-40704, -302, -108, -10811, '(none)');

/* Web Client Customers Direct Load Control role */
insert into yukongrouprole values (-407, -302, -300, -30000, '(none)');
insert into yukongrouprole values (-408, -302, -300, -30001, 'true');

/* Web Client Customers Commercial Metering role */
insert into yukongrouprole values (-413, -302, -102, -10202, '(none)');
insert into yukongrouprole values (-414, -302, -102, -10203, 'true');

/* Web Client Customers Administrator role */
insert into yukongrouprole values (-415, -302, -305, -30500, 'true');
insert into YukonGroupRole values (-416, -302, -102, -10205, '(none)');
insert into YukonGroupRole values (-417, -302, -102, -10206, '(none)');

insert into yukongrouprole values (-500,-300,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (-502,-300,-108,-10802,'(none)');
insert into yukongrouprole values (-503,-300,-108,-10803,'(none)');
insert into yukongrouprole values (-504,-300,-108,-10804,'(none)');
insert into yukongrouprole values (-505,-300,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-506,-300,-108,-10806,'(none)');
insert into yukongrouprole values (-507,-300,-108,-10807,'(none)');
insert into yukongrouprole values (-508,-300,-108,-10808,'(none)');
insert into yukongrouprole values (-510,-300,-108,-10810,'(none)');
insert into yukongrouprole values (-511,-300,-108,-10811,'(none)');

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
insert into yukongrouprole values (-710,-301,-108,-10810,'(none)');
insert into yukongrouprole values (-711,-301,-108,-10811,'(none)');

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
insert into yukongrouprole values (-759,-301,-201,-20159,'(none)');
insert into yukongrouprole values (-760,-301,-201,-20160,'(none)');
insert into yukongrouprole values (-761,-301,-201,-20161,'(none)');

insert into yukongrouprole values (-765,-301,-210,-21000,'(none)');
insert into yukongrouprole values (-766,-301,-210,-21001,'(none)');
insert into yukongrouprole values (-767,-301,-210,-21002,'(none)');

insert into yukongrouprole values (-775,-301,-900,-90000,'(none)');
insert into yukongrouprole values (-776,-301,-900,-90001,'(none)');
insert into yukongrouprole values (-777,-301,-900,-90002,'(none)');
insert into yukongrouprole values (-778,-301,-900,-90003,'(none)');
insert into yukongrouprole values (-779,-301,-900,-90004,'(none)');

insert into yukongrouprole values (-781,-301,-900,-90005,'(none)');
insert into yukongrouprole values (-782,-301,-900,-90006,'(none)');
insert into yukongrouprole values (-783,-301,-900,-90007,'(none)');

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
insert into yukongrouprole values (-846,-301,-201,-20846,'(none)');

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
insert into yukongrouprole values (-890,-301,-201,-20890,'(none)');
insert into yukongrouprole values (-891,-301,-201,-20891,'(none)');
insert into yukongrouprole values (-892,-301,-201,-20892,'(none)');
insert into yukongrouprole values (-893,-301,-201,-20893,'(none)');


/* Add the user-control properties to the Web Client Customers group */
insert into yukongrouprole values ( -985, -302, -306, -30600, '(none)');
insert into yukongrouprole values ( -986, -302, -306, -30601, 'true');
insert into yukongrouprole values ( -987, -302, -306, -30602, 'true');
insert into yukongrouprole values ( -988, -302, -306, -30603, 'true');


/* START the System Administrator role Group */
/*Database Editor*/
insert into YukonGroupRole values(-1000,-2,-100,-10000,'(none)');
insert into YukonGroupRole values(-1001,-2,-100,-10001,'(none)');
insert into YukonGroupRole values(-1002,-2,-100,-10002,'(none)');
insert into YukonGroupRole values(-1003,-2,-100,-10003,'(none)');
insert into YukonGroupRole values(-1004,-2,-100,-10004,'(none)');
insert into YukonGroupRole values(-1005,-2,-100,-10005,'(none)');
insert into YukonGroupRole values(-1007,-2,-100,-10007,'(none)');
insert into YukonGroupRole values(-1008,-2,-100,-10008,'(none)');
insert into YukonGroupRole values(-1009,-2,-100,-10009,'(none)');
insert into YukonGroupRole values(-1010,-2,-100,-10010,'00000000');
insert into YukonGroupRole values(-1011,-2,-100,-10011,'(none)');

/*TDC*/
insert into YukonGroupRole values(-1020,-2,-101,-10100,'(none)');
insert into YukonGroupRole values(-1021,-2,-101,-10101,'(none)');
insert into YukonGroupRole values(-1022,-2,-101,-10102,'(none)');
insert into YukonGroupRole values(-1023,-2,-101,-10103,'(none)');
insert into YukonGroupRole values(-1024,-2,-101,-10104,'(none)');
insert into YukonGroupRole values(-1027,-2,-101,-10107,'(none)');
insert into YukonGroupRole values(-1028,-2,-101,-10108,'(none)');
insert into YukonGroupRole values(-1031,-2,-101,-10111,'(none)');

/*Trending*/
insert into YukonGroupRole values(-1048,-2,-102,-10221,'(none)');
insert into YukonGroupRole values(-1049,-2,-102,-10220,'(none)');
insert into YukonGroupRole values(-1050,-2,-102,-10200,'(none)');
insert into YukonGroupRole values(-1052,-2,-102,-10202,'(none)');
insert into YukonGroupRole values(-1053,-2,-102,-10203,'(none)');
insert into YukonGroupRole values(-1054,-2,-102,-10204,'(none)');
insert into YukonGroupRole values(-1055,-2,-102,-10205,'(none)');
insert into YukonGroupRole values(-1056,-2,-102,-10206,'(none)');
insert into YukonGroupRole values(-1057,-2,-102,-10207,'(none)');
insert into YukonGroupRole values(-1058,-2,-102,-10208,'(none)');
insert into YukonGroupRole values(-1059,-2,-102,-10209,'(none)');
insert into YukonGroupRole values(-1060,-2,-102,-10210,'(none)');
insert into YukonGroupRole values(-1061,-2,-102,-10211,'(none)');
insert into YukonGroupRole values(-1062,-2,-102,-10212,'(none)');
insert into YukonGroupRole values(-1063,-2,-102,-10213,'(none)');
insert into YukonGroupRole values(-1064,-2,-102,-10214,'(none)');
insert into YukonGroupRole values(-1065,-2,-102,-10215,'(none)');
insert into YukonGroupRole values(-1066,-2,-102,-10216,'(none)');
insert into YukonGroupRole values(-1067,-2,-102,-10217,'(none)');
insert into YukonGroupRole values(-1068,-2,-102,-10218,'(none)');
insert into YukonGroupRole values(-1069,-2,-102,-10219,'(none)');

/*Commander*/
insert into YukonGroupRole values(-1070,-2,-103,-10300,'(none)');
insert into YukonGroupRole values(-1071,-2,-103,-10301,'true');
insert into YukonGroupRole values(-1072,-2,-103,-10302,'true');
insert into YukonGroupRole values(-1073,-2,-103,-10303,'false');
insert into YukonGroupRole values(-1074,-2,-103,-10304,'false');
insert into YukonGroupRole values(-1075,-2,-103,-10305,'(none)');

/*Billing*/
insert into YukonGroupRole values(-1390,-2,-106,-10600,'(none)');

/*Esubstation Editor*/
insert into YukonGroupRole values(-1080,-2,-107,-10700,'(none)');
insert into YukonGroupRole values(-1081,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1082,-2,-206,-20601,'(none)');
insert into YukonGroupRole values(-1083,-2,-206,-20602,'(none)');
insert into YukonGroupRole values(-1084,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1085,-2,-206,-20601,'true');
insert into YukonGroupRole values(-1086,-2,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into YukonGroupRole values (-1090,-2, -108, -10800, '/operator/Operations.jsp');
insert into YukonGroupRole values (-1091,-2, -108, -10802, '(none)');
insert into YukonGroupRole values (-1092,-2, -108, -10803, '(none)');
insert into YukonGroupRole values (-1093,-2, -108, -10804, '(none)');
insert into YukonGroupRole values (-1094,-2, -108, -10805, '(none)');
insert into YukonGroupRole values (-1095,-2, -108, -10806, '(none)');
insert into YukonGroupRole values (-1096,-2, -108, -10807, '(none)');
insert into YukonGroupRole values (-1097,-2, -108, -10808, '(none)');
insert into YukonGroupRole values (-10990,-2, -108, -10810, '(none)');
insert into YukonGroupRole values (-10991,-2, -108, -10811, '(none)');

/* Give yukon login access to View Logs, */
insert into YukonGroupRole values (-4000, -2, -200, -20009, '(none)');

/* Give yukon login access to view Reporting on main page */
insert into YukonGroupRole values (-5000, -2, -109, -10920, '(none)');


insert into yukongrouprole values (-2000,-303,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-2002,-303,-108,-10802,'(none)');
insert into yukongrouprole values (-2003,-303,-108,-10803,'(none)');
insert into yukongrouprole values (-2004,-303,-108,-10804,'(none)');
insert into yukongrouprole values (-2005,-303,-108,-10805,'(none)');
insert into yukongrouprole values (-2006,-303,-108,-10806,'(none)');
insert into yukongrouprole values (-2007,-303,-108,-10807,'(none)');
insert into yukongrouprole values (-2008,-303,-108,-10808,'(none)');
insert into yukongrouprole values (-2010,-303,-108,-10810,'(none)');
insert into yukongrouprole values (-2011,-303,-108,-10811,'(none)');

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
insert into yukongrouprole values (-2058,-303,-201,-20158,'(none)');
insert into yukongrouprole values (-2059,-303,-201,-20159,'(none)');
insert into yukongrouprole values (-2060,-303,-201,-20160,'(none)');
insert into yukongrouprole values (-2061,-303,-201,-20161,'(none)');

insert into yukongrouprole values (-2070,-303,-210,-21000,'(none)');
insert into yukongrouprole values (-2071,-303,-210,-21001,'(none)');
insert into yukongrouprole values (-2072,-303,-210,-21002,'(none)');

insert into yukongrouprole values (-2080,-303,-209,-20900,'(none)');
insert into yukongrouprole values (-2081,-303,-209,-20901,'(none)');
insert into yukongrouprole values (-2082,-303,-209,-20902,'(none)');
insert into yukongrouprole values (-2083,-303,-209,-20903,'(none)');
insert into yukongrouprole values (-2084,-303,-209,-20904,'(none)');
insert into yukongrouprole values (-2085,-303,-209,-20905,'(none)');
insert into yukongrouprole values (-2086,-303,-209,-20906,'(none)');
insert into yukongrouprole values (-2087,-303,-209,-20907,'(none)');
insert into yukongrouprole values (-2088,-303,-209,-20908,'(none)');

insert into yukongrouprole values (-2100,-303,-201,-20800,'(none)');
insert into yukongrouprole values (-2101,-303,-201,-20801,'(none)');
insert into yukongrouprole values (-2110,-303,-201,-20810,'(none)');
insert into yukongrouprole values (-2113,-303,-201,-20813,'(none)');
insert into yukongrouprole values (-2114,-303,-201,-20814,'(none)');
insert into yukongrouprole values (-2115,-303,-201,-20815,'(none)');
insert into yukongrouprole values (-2116,-303,-201,-20816,'(none)');
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
insert into yukongrouprole values (-2146,-303,-201,-20846,'(none)');

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
insert into yukongrouprole values (-2190,-303,-201,-20890,'(none)');
insert into yukongrouprole values (-2191,-303,-201,-20891,'(none)');
insert into yukongrouprole values (-2192,-303,-201,-20892,'(none)');
insert into yukongrouprole values (-2193,-303,-201,-20893,'(none)');

insert into yukongrouprole values (-2200,-304,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (-2202,-304,-108,-10802,'(none)');
insert into yukongrouprole values (-2203,-304,-108,-10803,'(none)');
insert into yukongrouprole values (-2204,-304,-108,-10804,'(none)');
insert into yukongrouprole values (-2205,-304,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-2206,-304,-108,-10806,'(none)');
insert into yukongrouprole values (-2207,-304,-108,-10807,'(none)');
insert into yukongrouprole values (-2208,-304,-108,-10808,'(none)');
insert into yukongrouprole values (-2210,-304,-108,-10810,'(none)');
insert into yukongrouprole values (-2211,-304,-108,-10811,'(none)');

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

/*==============================================================*/
/* Table: YukonImage                                            */
/*==============================================================*/
create table YukonImage  (
   ImageID              NUMBER                          not null,
   ImageCategory        VARCHAR2(20)                    not null,
   ImageName            VARCHAR2(80)                    not null,
   ImageValue           BLOB,
   constraint PK_YUKONIMAGE primary key (ImageID)
)
;

insert into YukonImage values( 0, '(none)', '(none)', null );

/*==============================================================*/
/* Table: YukonListEntry                                        */
/*==============================================================*/
create table YukonListEntry  (
   EntryID              NUMBER                          not null,
   ListID               NUMBER                          not null,
   EntryOrder           NUMBER                          not null,
   EntryText            VARCHAR2(50)                    not null,
   YukonDefinitionID    NUMBER                          not null,
   constraint PK_YUKONLISTENTRY primary key (EntryID)
)
;

insert into YukonListEntry values( 0, 0, 0, '(none)', 0 );
insert into YukonListEntry values( 1, 1, 0, 'Email', 1 );
insert into YukonListEntry values( 2, 1, 0, 'Phone Number', 2 );
insert into YukonListEntry values( 3, 1, 0, 'Email to Pager', 1 );
insert into YukonListEntry values( 4, 1, 0, 'Fax Number', 5 );
insert into YukonListEntry values( 5, 1, 0, 'Home Phone', 2 );
insert into YukonListEntry values( 6, 1, 0, 'Work Phone', 2 );
insert into YukonListEntry values( 7, 1, 0, 'Voice PIN', 3 );
insert into YukonListEntry values( 8, 1, 0, 'Cell Phone', 2 );
insert into YukonListEntry values( 9, 1, 0, 'Email to Cell', 1);
insert into YukonListEntry values( 10, 1, 0, 'Call Back Phone', 2);
insert into YukonListEntry values( 11, 1, 0, 'IVR Login', 3 );

insert into YukonListEntry values (100, 100, 0, 'Addition', 0);
insert into YukonListEntry values (101, 100, 0, 'Subtraction', 0);
insert into YukonListEntry values (102, 100, 0, 'Multiplication', 0);
insert into YukonListEntry values (103, 100, 0, 'Division', 0);
insert into YukonListEntry values (104, 100, 0, 'Logical AND', 0);
insert into YukonListEntry values (105, 100, 0, 'Logical OR', 0);
insert into YukonListEntry values (106, 100, 0, 'Logical NOT', 0);
insert into YukonListEntry values (107, 100, 0, 'Logical XOR', 0);
insert into YukonListEntry values (108, 100, 0, 'Greater than', 0);
insert into YukonListEntry values (109, 100, 0, 'Geq than', 0);
insert into YukonListEntry values (110, 100, 0, 'Less than', 0);
insert into YukonListEntry values (111, 100, 0, 'Leq than', 0);
insert into YukonListEntry values (112, 100, 0, 'Min', 0);
insert into YukonListEntry values (113, 100, 0, 'Max', 0);
insert into YukonListEntry values (114, 100, 0, 'Baseline', 0);
insert into YukonListEntry values (115, 100, 0, 'Baseline Percent', 0);
insert into YukonListEntry values (116, 100, 0, 'DemandAvg15', 0);
insert into YukonListEntry values (117, 100, 0, 'DemandAvg30', 0);
insert into YukonListEntry values (118, 100, 0, 'DemandAvg60', 0);
insert into YukonListEntry values (119, 100, 0, 'P-Factor kW/kVAr', 0);
insert into YukonListEntry values (120, 100, 0, 'P-Factor kW/kQ', 0);
insert into YukonListEntry values (121, 100, 0, 'P-Factor kW/kVA', 0);
insert into YukonListEntry values (122, 100, 0, 'kVAr from kW/kQ', 0);
insert into YukonListEntry values (123, 100, 0, 'kVA from kW/kVAr', 0);
insert into YukonListEntry values (124, 100, 0, 'kVA from kW/kQ', 0);
insert into YukonListEntry values (126, 100, 0, 'Squared', 0);
insert into YukonListEntry values (127, 100, 0, 'Square Root', 0);
insert into YukonListEntry values (128, 100, 0, 'ArcTan', 0);
insert into YukonListEntry values (129, 100, 0, 'Max Difference', 0);
insert into YukonListEntry values (130, 100, 0, 'Absolute Value', 0);
insert into YukonListEntry values (131, 100, 0, 'kW from kVA/kVAr', 0);
insert into YukonListEntry values (132, 100, 0, 'Modulo Divide', 0);
insert into YukonListEntry values (133, 100, 0, 'State Timer', 0);
insert into YukonListEntry values (134, 100, 0, 'True,False,Condition', 0); 
insert into YukonListEntry values (135, 100, 0, 'Regression', 0); 
insert into YukonListEntry values (136, 100, 0, 'Binary Encode', 0);
insert into yukonlistentry values (137, 100, 0, 'Mid Level Latch', 0);
insert into yukonlistentry values (138, 100, 0, 'Float From 16bit', 0);
insert into yukonlistentry values (139, 100, 0, 'Get Point Limit', 0);
insert into yukonlistentry values (140, 100, 0, 'Get Interval Minutes', 0);
insert into yukonlistentry values (141, 100, 0, 'Intervals To Value', 0);
insert into yukonlistentry values (142, 100, 0, 'Linear Slope', 0);

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
insert into YukonListEntry values (1034,1003,0,'Non Yukon Meter',1204);

insert into YukonListEntry values (1041,1004,0,' ',0);
insert into YukonListEntry values (1042,1004,0,'120/120',0);

insert into YukonListEntry values (1051,1005,0,'LCR-5000(EXPRESSCOM)',1302);
insert into YukonListEntry values (1052,1005,-1,'LCR-4000',1305);
insert into YukonListEntry values (1053,1005,0,'LCR-3000',1306);
insert into YukonListEntry values (1054,1005,-1,'LCR-2000',1307);
insert into YukonListEntry values (1055,1005,-1,'LCR-1000',1308);
insert into YukonListEntry values (1056,1005,-1,'ExpressStat',1301);
insert into YukonListEntry values (1057,1005,-1,'EnergyPro',3100);
insert into YukonListEntry values (1058,1005,-1,'MCT',1303);
insert into YukonListEntry values (1059,1005,-1,'Commercial ExpressStat',1304);
insert into YukonListEntry values (1060,1005,-1,'SA-205',1309);
insert into YukonListEntry values (1061,1005,-1,'SA-305',1310);
insert into YukonListEntry values (1062,1005,-1,'LCR-5000(VERSACOM)',1311);
insert into YukonListEntry values (1063,1005,-1,'SA Simple',1312);
insert into YukonListEntry values (1064,1005,-1,'ExpressStat Heat Pump',1313);

insert into YukonListEntry values (1071,1006,0,'Available',1701);
insert into YukonListEntry values (1072,1006,0,'Temp Unavail',1702);
insert into YukonListEntry values (1073,1006,0,'Unavailable',1703);
insert into YukonListEntry values (1074,1006,0,'Ordered',1704);
insert into YukonListEntry values (1075,1006,0,'Shipped',1705);
insert into YukonListEntry values (1076,1006,0,'Received',1706);
insert into YukonListEntry values (1077,1006,0,'Issued',1707);
insert into YukonListEntry values (1078,1006,0,'Installed',1708);
insert into YukonListEntry values (1079,1006,0,'Removed',1709);

insert into YukonListEntry values (1081,1007,0,'(Default)',1400);
insert into YukonListEntry values (1082,1007,0,'Air Conditioner',1401);
insert into YukonListEntry values (1083,1007,0,'Water Heater',1402);
insert into YukonListEntry values (1084,1007,0,'Storage Heat',1403);
insert into YukonListEntry values (1085,1007,0,'Heat Pump',1404);
insert into YukonListEntry values (1086,1007,0,'Dual Fuel',1405);
insert into YukonListEntry values (1087,1007,0,'Generator',1406);
insert into YukonListEntry values (1088,1007,0,'Grain Dryer',1407);
insert into YukonListEntry values (1089,1007,0,'Irrigation',1408);
insert into YukonListEntry values (1090, 1007, 0, 'Chiller', 1409);
insert into YukonListEntry values (1091, 1007, 0, 'Dual Stage', 1410);

insert into YukonListEntry values (1101,1008,0,'General',0);
insert into YukonListEntry values (1102,1008,0,'Credit',0);
insert into YukonListEntry values (1111,1009,0,'Service Call',1550);
insert into YukonListEntry values (1112,1009,0,'Install',1551);
insert into YukonListEntry values (1113,1009,0,'Activation',1552);
insert into YukonListEntry values (1114,1009,0,'Deactivation',1553);
insert into YukonListEntry values (1115,1009,0,'Removal',1554);
insert into YukonListEntry values (1116,1009,0,'Repair',1555);
insert into YukonListEntry values (1117,1009,0,'Other',1556);
insert into YukonListEntry values (1118,1009,0,'Maintenance',1557);

insert into YukonListEntry values (1121,1010,0,'Pending',1501);
insert into YukonListEntry values (1122,1010,0,'Scheduled',1502);
insert into YukonListEntry values (1123,1010,0,'Completed',1503);
insert into YukonListEntry values (1124,1010,0,'Cancelled',1504);
insert into YukonListEntry values (1125,1010,0,'Assigned',1505);
insert into YukonListEntry values (1126,1010,0,'Released',1506);
insert into YukonListEntry values (1127,1010,0,'Processed',1507);
insert into YukonListEntry values (1128,1010,0,'Hold',1508);

insert into YukonListEntry values (1131,1011,0,'Acct #',1601);
insert into YukonListEntry values (1132,1011,0,'Phone #',1602);
insert into YukonListEntry values (1133,1011,0,'Last name',1603);
insert into YukonListEntry values (1134,1011,0,'Serial #',1604);
insert into YukonListEntry values (1135,1011,0,'Map #',1605);
insert into YukonListEntry values (1136,1011,0,'Address',1606);
insert into YukonListEntry values (1137,1011,0,'Alt Track #',1607);
insert into YukonListEntry values (1138, 1011, 0, 'Company', 1609);

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

insert into YukonListEntry values (1241,1049,1,'1 Day',24);
insert into YukonListEntry values (1242,1049,2,'2 Days',48);
insert into YukonListEntry values (1243,1049,3,'3 Days',72);
insert into YukonListEntry values (1244,1049,4,'4 Days',96);
insert into YukonListEntry values (1245,1049,5,'5 Days',120);
insert into YukonListEntry values (1246,1049,6,'6 Days',144);
insert into YukonListEntry values (1247,1049,7,'7 Days',168);

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
insert into YukonListEntry values (1306,1051,0,'Address',2706);
insert into YukonListEntry values (1307,1051,0,'Alt Track #',2707);
insert into YukonListEntry values (1311,1052,0,'Serial #',2801);
insert into YukonListEntry values (1312,1052,0,'Install date',2802);
insert into YukonListEntry values (1321,1053,0,'Device type',2901);
insert into YukonListEntry values (1322,1053,0,'Service company',2902);
insert into YukonListEntry values (1323,1053,0,'Appliance Type',2903);
insert into YukonListEntry values (1325,1053,0,'Device status',2905);
insert into YukonListEntry values (1326,1053,0,'Member',2906);
insert into YukonListEntry values (1327,1053,0,'Warehouse',2907);
insert into YukonListEntry values (1328,1053,0,'Min Serial Number',2908);
insert into YukonListEntry values (1329,1053,0,'Max Serial Number',2909);
insert into YukonListEntry values (1330,1053,0,'Postal Code',2910);

insert into YukonListEntry values (1331,1054,0,'Order #',3301);
insert into YukonListEntry values (1332,1054,0,'Acct #',3302);
insert into YukonListEntry values (1333,1054,0,'Phone #',3303);
insert into YukonListEntry values (1334,1054,0,'Last Name',3304);
insert into YukonListEntry values (1335,1054,0,'Serial #',3305);
insert into YukonListEntry values (1336,1054,0,'Address',3306);
insert into YukonListEntry values (1341,1055,0,'Order #',3401);
insert into YukonListEntry values (1342,1055,0,'Date/Time',3402);
insert into YukonListEntry values (1343,1055,0,'Service Company',3403);
insert into YukonListEntry values (1344,1055,0,'Service Type',3404);
insert into YukonListEntry values (1345,1055,0,'Service Status',3405);
insert into YukonListEntry values (1346,1055,0,'Customer Type',3406);

insert into yukonlistentry values (1351, 1056, 0, 'Service Status', 3501);
insert into YukonListEntry values (1352,1056,0,'Service Type',3502);
insert into YukonListEntry values (1353,1056,0,'Service Company',3503);
insert into YukonListEntry values (1354,1056,0,'Postal Code',3504);
insert into YukonListEntry values (1355,1056,0,'Customer Type',3505);

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

insert into YukonListEntry values (1901,1065,0,'J',3601);
insert into YukonListEntry values (1902,1065,1,'PS',3602);
insert into YukonListEntry values (1903,1065,2,'Power Service Only',3603);
insert into YukonListEntry values (1904,1065,3,'Power & Lighting Service',3604);
insert into YukonListEntry values (1905,1065,4, 'PP', 3605);
insert into YukonListEntry values (1906,1065,5, 'PT', 3606);

insert into YukonListEntry values (1930,1071,0, 'Commercial', 0);
insert into YukonListEntry values (1931,1071,0, 'Industrial', 0);
insert into YukonListEntry values (1932,1071,0, 'Manufacturing', 0);
insert into YukonListEntry values (1933,1071,0, 'Municipal', 0);

insert into yukonlistentry values (10101, 1067, 0, 'CustomerAccount', 0);
insert into yukonlistentry values (10102, 1067, 0, 'Inventory', 0);
insert into yukonlistentry values (10103, 1067, 0, 'WorkOrder', 0);
insert into yukonlistentry values (10201, 1068, 0, 'Created', 0);
insert into yukonlistentry values (10202, 1068, 0, 'Updated', 0);

insert into YukonListEntry values (10431,1053,0,'Customer Type',2911);

insert into YukonListEntry values (20000,0,0,'Customer List Entry Base 2',0);


/*==============================================================*/
/* Index: Indx_YkLstDefID                                       */
/*==============================================================*/
create index Indx_YkLstDefID on YukonListEntry (
   YukonDefinitionID ASC
)
;

/*==============================================================*/
/* Table: YukonPAObject                                         */
/*==============================================================*/
create table YukonPAObject  (
   PAObjectID           NUMBER                          not null,
   Category             VARCHAR2(20)                    not null,
   PAOClass             VARCHAR2(20)                    not null,
   PAOName              VARCHAR2(60)                    not null,
   Type                 VARCHAR2(30)                    not null,
   Description          VARCHAR2(60)                    not null,
   DisableFlag          CHAR(1)                         not null,
   PAOStatistics        VARCHAR2(10)                    not null,
   constraint PK_YUKONPAOBJECT primary key (PAObjectID)
)
;

INSERT into YukonPAObject values (0, 'DEVICE', 'System', 'System Device', 'System', 'Reserved System Device', 'N', '-----');
update yukonpaobject set type = 'MCT-430SL' where type = 'MCT-430SN' or type = 'MCT430SN'

/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique index Indx_PAO on YukonPAObject (
   Category ASC,
   PAOName ASC,
   PAOClass ASC
)
;

/*==============================================================*/
/* Table: YukonRole                                             */
/*==============================================================*/
create table YukonRole  (
   RoleID               NUMBER                          not null,
   RoleName             VARCHAR2(120)                   not null,
   Category             VARCHAR2(60)                    not null,
   RoleDescription      VARCHAR2(200)                   not null,
   constraint PK_YUKONROLE primary key (RoleID)
)
;

/* Default role for all users - yukon category */
insert into YukonRole values(-1,'Yukon','Yukon','Default Yukon role. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-4,'Authentication','Yukon','Settings for using an authentication server to login instead of standard yukon login.');
insert into YukonRole values(-5,'Voice Server','Yukon','Inbound and outbound voice interface.');
insert into YukonRole values(-6,'Billing Configuration','Yukon','Billing. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-7,'MultiSpeak','Yukon','MultiSpeak web services interface.');
insert into YukonRole values(-8,'Configuration','Yukon','Miscellaneous Yukon configuration settings');
insert into YukonRole values(-104,'Calc Historical','Yukon','Calc Historical. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-105,'Web Graph','Yukon','Web Graph. Edit this role from the Yukon SetUp page.');

/* Application specific roles */
insert into YukonRole values(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
insert into YukonRole values(-101,'Tabular Display Console','Application','Access to the Yukon Tabular Display Console application');
insert into YukonRole values(-102,'Trending','Application','Access to the Yukon Trending application');
insert into YukonRole values(-103,'Commander','Application','Access to the Yukon Commander application');

insert into YukonRole values(-106,'Billing','Application','Billing. Edit this role from the Yukon SetUp page.');
insert into YukonRole values(-107,'Esubstation Editor','Application','Access to the Esubstation Drawing Editor application');
insert into YukonRole values(-108,'Web Client','Application','Access to the Yukon web application');
insert into YukonRole values(-109,'Reporting','Application','Access to reports generation.');

/* Web client operator roles */
insert into YukonRole values(-200,'Administrator','Operator','Access to Yukon administration');
insert into YukonRole values(-201,'Consumer Info','Operator','Operator access to consumer account information');
insert into YukonRole values(-202,'Metering','Operator','Operator access to metering');

/* Operator roles */
insert into YukonRole values(-206,'Esubstation Drawings','Operator','Operator access to esubstation drawings');
insert into YukonRole values(-207,'Odds For Control','Operator','Operator access to odds for control');
insert into YukonRole values(-2,'Energy Company','Operator','Energy company role');

/* Inventory Role */
insert into YukonRole values (-209,'Inventory','Operator','Operator Access to hardware inventory');

/* operator work order management role */
insert into YukonRole values (-210,'Work Order','Operator','Operator Access to work order management');

/*ISOC*/
insert into YukonRole values(-211,'CI Curtailment','Operator','Operator access to C&I Curtailment'); 

/* Scheduler Role */
insert into YukonRole values(-212,'Scheduler','Operator','Operator access to Scheduler'); 

/* CI customer roles */
insert into YukonRole values(-300,'Direct Loadcontrol','CICustomer','Customer access to commercial/industrial customer direct loadcontrol');
insert into YukonRole values(-305,'Administrator','CICustomer','Administrator privileges.');
insert into YukonRole values(-306,'User Control', 'CICustomer', 'Customer access to user control operations.');

/* Consumer roles */
insert into YukonRole values(-400,'Residential Customer','Consumer','Access to residential customer information');

/* Capacitor Control roles */
insert into YukonRole values (-700,'Administrator','Capacitor Control','Allows a user to change overall settings of the Cap Control system .');

/* IVR roles */
insert into YukonRole values (-800,'IVR','Notifications','Settings for Interactive Voice Response module');
insert into YukonRole values (-801, 'Configuration', 'Notifications', 'Configuration for Notification Server (voice and email)');

/* Load Control roles */
insert into YukonRole values(-900,'Direct Loadcontrol','Load Control','Access and usage of direct loadcontrol system');

/* Capacitor Control roles cont*/
insert into YukonRole values (-1000,'Substation Display','Capacitor Control','Change display settings for substation details.');
insert into YukonRole values (-1001,'Feeder Display','Capacitor Control','Change display settings for feeder details.');
insert into YukonRole values (-1002,'Cap Bank Display','Capacitor Control','Change display settings for cap bank details.');

/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create index Indx_YukRol_Nm on YukonRole (
   RoleName ASC
)
;

/*==============================================================*/
/* Table: YukonRoleProperty                                     */
/*==============================================================*/
create table YukonRoleProperty  (
   RolePropertyID       NUMBER                          not null,
   RoleID               NUMBER                          not null,
   KeyName              VARCHAR2(100)                   not null,
   DefaultValue         VARCHAR2(1000)                  not null,
   Description          VARCHAR2(1000)                  not null,
   constraint PK_YUKONROLEPROPERTY primary key (RolePropertyID)
)
;

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
insert into YukonRoleProperty values(-1013,-1,'stars_preload_data','true','Controls whether the STARS application should preload data into the cache.');
insert into YukonRoleProperty values(-1014,-1,'web_logo','CannonLogo.gif','The logo that is used for the yukon web applications');
insert into YukonRoleProperty values(-1015,-1,'voice_host','127.0.0.1','Name or IP address of the voice server');
insert into YukonRoleProperty values(-1016,-1,'notification_host','127.0.0.1','Name or IP address of the Yukon Notification service');
insert into YukonRoleProperty values(-1017,-1,'notification_port','1515','TCP/IP port of the Yukon Notification service');
insert into YukonRoleProperty values(-1018,-1,'export_file_directory','(none)','File location of all export operations');
insert into YukonRoleProperty values(-1019,-1,'batched_switch_command_timer','auto','Specifies whether the STARS application should automatically process batched switch commands');
insert into YukonRoleProperty values(-1020,-1,'stars_activation','false','Specifies whether STARS functionality should be allowed in this web deployment.');

/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of emails sent on behalf of energy company, e.g. control odds and opt out notification emails.');
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','(none)','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1105,-2,'customer_group_ids','-300','Group IDs of all the residential customer logins');
insert into YukonRoleProperty values(-1106,-2,'operator_group_ids','-301','Group IDs of all the web client operator logins');
insert into YukonRoleProperty values(-1107,-2,'track_hardware_addressing','false','Controls whether to track the hardware addressing information.');
insert into YukonRoleProperty values(-1108,-2,'single_energy_company','true','Indicates whether this is a single energy company system.');
insert into YukonRoleProperty values(-1109,-2,'z_optional_product_dev','00000000','This feature is for development purposes only');
insert into YukonRoleProperty values(-1110,-2,'Default Temperature Unit','F','Default temperature unit for an energy company, F(ahrenheit) or C(elsius)');
insert into YukonRoleProperty values(-1111,-2,'z_meter_mct_base_desig','yukon','Allow meters to be used general STARS entries versus Yukon MCTs');
insert into YukonRoleProperty values(-1112,-2,'applicable_point_type_key',' ','The name of the set of CICustomerPointData TYPES that should be set for customers.');
insert into YukonRoleProperty values(-1113,-2,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');
insert into YukonRoleProperty values(-1114,-2,'Inherit Parent App Cats','true','If part of a member structure, should appliance categories be inherited from the parent.');


insert into YukonRoleProperty values(-1300,-4,'server_address','127.0.0.1','Authentication server machine address');
insert into YukonRoleProperty values(-1301,-4,'auth_port','1812','Authentication port.');
insert into YukonRoleProperty values(-1302,-4,'acct_port','1813','Accounting port.');
insert into YukonRoleProperty values(-1303,-4,'secret_key','cti','Client machine secret key value, defined by the server.');
insert into YukonRoleProperty values(-1304,-4,'auth_method','(none)','Authentication method. Possible values are (none) | PAP, [chap, others to follow soon]');
insert into YukonRoleProperty values(-1305,-4,'authentication_mode','Yukon','Authentication mode to use.  Valid values are:   Yukon | Radius');
insert into YukonRoleProperty values(-1306,-4,'auth_timeout','30','Number of seconds before the authentication process times out');
insert into yukonroleproperty values(-1307,-4,'Default Authentication Type', 'PLAIN', 'Set the default authentication type to use {PLAIN,HASH_SHA,RADIUS,AD,LDAP,NONE}');
insert into YukonRoleProperty values(-1308,-4,'LDAP DN','dc=example,dc=com','LDAP Distinguished Name')
insert into YukonRoleProperty values(-1309,-4,'LDAP User Suffix','ou=users','LDAP User Suffix')
insert into YukonRoleProperty values(-1310,-4,'LDAP User Prefix','uid=','LDAP User Prefix')
insert into YukonRoleProperty values(-1311,-4,'LDAP Server Address','127.0.0.1','LDAP Server Address')
insert into YukonRoleProperty values(-1312,-4,'LDAP Server Port','389','LDAP Server Port')
insert into YukonRoleProperty values(-1313,-4,'LDAP Server Timeout','30','LDAP Server Timeout (in seconds)')
insert into YukonRoleProperty values(-1314,-4,'Active Directory Server Address','127.0.0.1','Active Directory Server Address')
insert into YukonRoleProperty values(-1315,-4,'Active Directory Server Port','389','Active Directory Server Port')
insert into YukonRoleProperty values(-1316,-4,'Active Directory Server Timeout','30','Active Directory Server Timeout (in seconds)')
insert into YukonRoleProperty values(-1317,-4,'Active Directory NT Domain Name','(none)','Active Directory NT DOMAIN NAME')


insert into YukonRoleProperty values(-1401,-5,'call_timeout','30','The time-out in seconds given to each outbound call');
insert into YukonRoleProperty values(-1402,-5,'call_response_timeout','240','The time-out in seconds given to each outbound call response');
insert into YukonRoleProperty values(-1403,-5,'Call Prefix','(none)','Any number or numbers that must be dialed before a call can be placed.');

/* Billing Role Properties */
insert into YukonRoleProperty values(-1500,-6,'wiz_activate','false','<description>');
insert into YukonRoleProperty values(-1501,-6,'input_file','c:yukonclientbinBillingIn.txt','<description>');

insert into YukonRoleProperty values(-1503,-6,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
insert into YukonRoleProperty values(-1504,-6,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
insert into YukonRoleProperty values(-1505,-6,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
insert into YukonRoleProperty values(-1506,-6,'Append To File','false','Append to existing file.');
insert into YukonRoleProperty values(-1507,-6,'Remove Multiplier','false','Remove the multiplier value from the reading.');
insert into YukonRoleProperty values(-1508,-6,'Coop ID - CADP Only','(none)','CADP format requires a coop id number.');

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
insert into YukonRoleProperty values(-10010,-100,'z_optional_product_dev','00000000','This feature is for development purposes only');
insert into YukonRoleProperty values(-10011,-100,'allow_member_programs','false','Allows member management of LM Direct Programs through the DBEditor');

/*MultiSpeak*/
insert into YukonRoleProperty values(-1600,-7,'PAOName Alias','0','Defines a Yukon Pao (Device) Name field alias. Valid values(0-4): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer]');
insert into YukonRoleProperty values(-1601,-7,'Primary CIS Vendor','0','Defines the primary CIS vendor for CB interfaces.');

/*Configuration*/
insert into YukonRoleProperty values(-1700,-8,'Device Display Template','{name}','Defines the format for displaying devices. Available placeholders: {name},{description},{meterNumber},{id},{address}');

/* TDC Role */
insert into YukonRoleProperty values(-10100,-101,'loadcontrol_edit','00000000','(No settings yet)');
insert into YukonRoleProperty values(-10101,-101,'macs_edit','00000CCC','The following settings are valid: CREATE_SCHEDULE(0x0000000C), ENABLE_SCHEDULE(0x000000C0), ABLE_TO_START_SCHEDULE(0x00000C00)');
insert into YukonRoleProperty values(-10102,-101,'tdc_express','ludicrous_speed','<description>');
insert into YukonRoleProperty values(-10103,-101,'tdc_max_rows','500','The number of rows shown before creating a new page of data');
insert into YukonRoleProperty values(-10104,-101,'tdc_rights','00000000','The following settings are valid: HIDE_MACS(0x00001000), HIDE_CAPCONTROL(0x00002000), HIDE_LOADCONTROL(0x00004000), HIDE_ALL_DISPLAYS(0x0000F000), CONTROL_YUKON_SERVICES(0x00010000), HIDE_ALARM_COLORS(0x80000000)');
insert into YukonRoleProperty values(-10107,-101,'tdc_alarm_count','3','Total number alarms that are displayed in the quick access list');
insert into YukonRoleProperty values(-10108,-101,'decimal_places','2','How many decimal places to show for real values');
insert into YukonRoleProperty values(-10111,-101,'lc_reduction_col','true','Tells TDC to show the LoadControl reduction column or not');


/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');

insert into YukonRoleProperty values(-10202, -102, 'Trending Disclaimer',' ','The disclaimer that appears with trends.');
insert into yukonroleproperty values(-10203, -102, 'Scan Now Enabled', 'false', 'Controls access to retrieve meter data on demand.');
insert into yukonroleproperty values(-10204, -102, 'Scan Now Label', 'Get Data Now', 'The label for the scan data now option.');
insert into yukonroleproperty values(-10205, -102, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events.');
insert into yukonroleproperty values(-10206, -102, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily.');
insert into yukonroleproperty values(-10207, -102, 'Reset Peaks Enabled', 'false', 'Allow access to reset the peak time period.');
insert into yukonroleproperty values(-10208, -102, 'Header Label', 'Trending', 'The header label for trends.');
insert into yukonroleproperty values(-10209, -102, 'Header Secondary Label', 'Interval Data', 'A secondary header label for grouping trends.');
insert into yukonroleproperty values(-10210, -102, 'Trend Assignment', 'false', 'Allow assignment of trends to users.');
insert into yukonroleproperty values(-10211, -102, 'Trend Create', 'false', 'Allow creation of new trends.');
insert into yukonroleproperty values(-10212, -102, 'Trend Delete', 'false', 'Allow deletion of old trends.');
insert into yukonroleproperty values(-10213, -102, 'Trend Edit', 'false', 'Allow ditting of existing trends.');
insert into yukonroleproperty values(-10214, -102, 'Options Button Enabled', 'true', 'Display the Options link to additional trending configuration properties.');
insert into yukonroleproperty values(-10215, -102, 'Export/Print Button Enabled', 'true', 'Display the Export/Print options button (drop down menu).');
insert into yukonroleproperty values(-10216, -102, 'View Button Enabled', 'true', 'Display the View options button (drop down menu).');
insert into yukonroleproperty values(-10217, -102, 'Export/Print Button Label', 'Trend', 'The label for the trend print/export button (drop down menu).');
insert into yukonroleproperty values(-10218, -102, 'View Button Label', 'View', 'The label for the trend view options button (drop down menu).');
insert into yukonroleproperty values(-10219, -102, 'Trending Usage', 'false', 'Allow access to trending time of use.');
insert into yukonroleproperty values(-10220, -102, 'Default Start Date Offset', '0', 'Offset the start date by this number.');
insert into yukonroleproperty values(-10221, -102, 'Default Time Period', '(none)', 'Default the time period.');


/* Commander Role Properties */ 
insert into YukonRoleProperty values(-10300,-103,'msg_priority','14','Tells commander what the outbound priority of messages are (low)1 - 14(high)');
insert into YukonRoleProperty values(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
insert into YukonRoleProperty values(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
insert into YukonRoleProperty values(-10303,-103,'DCU SA205 Serial','false','Show a DCU SA205 Serial Number SortBy display');
insert into YukonRoleProperty values(-10304,-103,'DCU SA305 Serial','false','Show a DCU SA305 Serial Number SortBy display');
insert into YukonRoleProperty values(-10305,-103,'Commands Group Name','Default Commands','The commands group name for the displayed commands.');
insert into yukonroleproperty values(-10306,-103,'Read device', 'true', 'Allow the ability to read values from a device');
insert into yukonroleproperty values(-10307,-103,'Write to device', 'true', 'Allow the ability to write values to a device');
insert into yukonroleproperty values(-10309,-103,'Control disconnect', 'true', 'Allow the ability to control a disconnect to a device');
insert into yukonroleproperty values(-10310,-103,'Read LM device', 'true', 'Allow the ability to read values from an LM device');
insert into yukonroleproperty values(-10311,-103,'Write to LM device', 'true', 'Allow the ability to write values to an LM device');
insert into yukonroleproperty values(-10312,-103,'Control LM device', 'true', 'Allow the ability to control an LM device');
insert into yukonroleproperty values(-10313,-103,'Read Cap Control device', 'true', 'Allow the ability to read values from a Cap Control device');
insert into yukonroleproperty values(-10314,-103,'Write to Cap Control device', 'true', 'Allow the ability to write values to a Cap Control device');
insert into yukonroleproperty values(-10315,-103,'Control Cap Control device', 'true', 'Allow the ability to control a Cap Control device');
insert into yukonroleproperty values(-10316,-103,'Execute Unknown Command', 'true', 'Allow the ability to execute commands which do not fall under another role property.');
insert into yukonroleproperty values(-10317,-103,'Execute Manual Command', 'true', 'Allow the ability to execute manual commands');


/* Calc Historical Role Properties */
insert into YukonRoleProperty values(-10400,-104,'interval','900','<description>');
insert into YukonRoleProperty values(-10401,-104,'baseline_calctime','4','<description>');
insert into YukonRoleProperty values(-10402,-104,'daysprevioustocollect','30','<description>');

/* Web Graph Role Properties */
insert into YukonRoleProperty values(-10500,-105,'home_directory','c:yukonclientwebgraphs','<description>');
insert into YukonRoleProperty values(-10501,-105,'run_interval','900','<description>');

insert into YukonRoleProperty values(-10600,-106,'Header Label','Billing','The header label for billing.');

/* Esubstation Editor Role Properties */
insert into YukonRoleProperty values(-10700,-107,'default','false','The default esub editor property');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10800,-108,'home_url','/operator/Operations.jsp','The url to take the user immediately after logging into the Yukon web application');
insert into YukonRoleProperty values(-10802,-108,'style_sheet','yukon/CannonStyle.css','The web client cascading style sheet.');
insert into YukonRoleProperty values(-10803,-108,'nav_bullet_selected','yukon/Bullet.gif','The bullet used when an item in the nav is selected.');
insert into YukonRoleProperty values(-10804,-108,'nav_bullet_expand','yukon/BulletExpand.gif','The bullet used when an item in the nav can be expanded to show submenu.');
insert into YukonRoleProperty values(-10805,-108,'header_logo','yukon/DefaultHeader.gif','The main header logo');
insert into YukonRoleProperty values(-10806,-108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','yukon/BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','yukon/MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');
insert into YukonRoleProperty values(-10810,-108, 'pop_up_appear_style','onmouseover', 'Style of the popups appearance when the user selects element in capcontrol.');
insert into YukonRoleProperty values(-10811,-108, 'inbound_voice_home_url', '/voice/inboundOptOut.jsp', 'Home URL for inbound voice logins');
insert into YukonRoleProperty values(-10812, -108,'Java Web Start Launcher Enabled', 'true', 'Allow access to the Java Web Start Launcher for client applications.');
insert into YukonRoleProperty values(-10814, -108,'Suppress Error Page Details', 'false', 'Disable stack traces for this user.');
insert into YukonRoleProperty values(-10815, -108,'Data Updater Delay (milliseconds)', '4000', 'The number of milliseconds between requests for the latest point values on pages that support the data updater.');
insert into YukonRoleProperty values(-10816, -108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');

/* Reporting Analysis role properties */
insert into YukonRoleProperty values(-10900,-109,'Header Label','Reporting','The header label for reporting.');
insert into YukonRoleProperty values(-10901,-109,'Download Reports Enable','true','Access to download the report files..');
insert into YukonRoleProperty values(-10902,-109,'Download Reports Default Filename','report.txt','A default filename for the downloaded report.');
insert into YukonRoleProperty values(-10903,-109,'Admin Reports Group','true','Access to administrative group reports.');
insert into YukonRoleProperty values(-10904,-109,'AMR Reports Group','true','Access to AMR group reports.');
insert into YukonRoleProperty values(-10905,-109,'Statistical Reports Group','true','Access to statistical group reports.');
insert into YukonRoleProperty values(-10906,-109,'Load Managment Reports Group','false','Acces to Load Management group reports.');
insert into YukonRoleProperty values(-10907,-109,'Cap Control Reports Group','false','Access to Cap Control group reports.');
insert into YukonRoleProperty values(-10908,-109,'Database Reports Group','true','Access to Database group reports.');
insert into YukonRoleProperty values(-10909,-109,'Stars Reports Group','true','Access to Stars group reports.');
insert into YukonRoleProperty values(-10910,-109,'Other Reports Group','true','Access to Other group reports.');

insert into YukonRoleProperty values(-10913,-109,'Admin Reports Group Label','Administrator','Label (header) for administrative group reports.');
insert into YukonRoleProperty values(-10914,-109,'AMR Reports Group Label','Metering','Label (header) for AMR group reports.');
insert into YukonRoleProperty values(-10915,-109,'Statistical Reports Group Label','Statistical','Label (header) for statistical group reports.');
insert into YukonRoleProperty values(-10916,-109,'Load Managment Reports Group Label','Load Management','Label (header) for Load Management group reports.');
insert into YukonRoleProperty values(-10917,-109,'Cap Control Reports Group Label','Cap Control','Label (header) for Cap Control group reports.');
insert into YukonRoleProperty values(-10918,-109,'Database Reports Group Label','Database','Label (header) for Database group reports.');
insert into YukonRoleProperty values(-10919,-109,'Stars Reports Group Label','Stars','Label (header) for Stars group reports.');
insert into YukonRoleProperty values(-10920,-109,'Other Reports Group Label','Other','Label (header) for Other group reports.');
insert into YukonRoleProperty values(-10922,-109,'C&I Curtailment Reports Group Label','Curtailment','Label (header) for C&I Curtailment group reports.');
insert into YukonRoleProperty values(-10923,-109,'C&I Curtailment Reports Group','false','Access to C&I Curtailment group reports');


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
insert into YukonRoleProperty values(-20158,-201,'Disable Switch Sending','false','Disables the ability to send configs and connects/disconnects to switches.');
insert into YukonRoleProperty values(-20159,-201,'Switches to Meter','(none)','Allow switches to be assigned under meters for an account.');
insert into YukonRoleProperty values(-20160,-201,'Create Login With Account','false','Require that a login is created with every new customer account.');
insert into YukonRoleProperty values(-20161,-201,'Account Number Length','(none)','Specifies the number of account number characters to consider for comparison purposes during the customer account import process.');
insert into YukonRoleProperty values(-20162,-201,'Rotation Digit Length','(none)','Specifies the number of rotation digit characters to ignore during the customer account import process.');
insert into YukonRoleProperty values(-20163,-201,'Allow Account Editing','true','Can be used to disable the ability to edit and delete customer account information.');

/* Operator Administrator Role Properties */
insert into YukonRoleProperty values(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
insert into YukonRoleProperty values(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
insert into YukonRoleProperty values(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');
insert into YukonRoleProperty values(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');
insert into YukonRoleProperty values(-20004,-200,'View Batch Commands','false','Controls whether to allow monitoring of all batched switch commands');
insert into YukonRoleProperty values(-20005,-200,'View Opt Out Events','false','Controls whether to allow monitoring of all scheduled opt out events');
insert into YukonRoleProperty values(-20006,-200,'Member Login Cntrl','false','Ignored if not a member company -- Controls whether operator logins are shown on the EC administration page.');
insert into YukonRoleProperty values(-20007,-200,'Member Route Select','false','Ignored if not a member company -- Controls whether routes are visible through the EC administration page.');
insert into YukonRoleProperty values(-20008,-200,'Allow Designation Codes','false','Toggles on or off the regional (usually zip) code option for service companies.');
insert into YukonRoleProperty values(-20009,-200,'Multiple Warehouses','false','Allows for multiple user-created warehouses instead of a single generic warehouse.');
insert into YukonRoleProperty values(-20011,-200,'MultiSpeak Setup','false','Controls access to configure the MultiSpeak Interfaces.');
insert into YukonRoleProperty values(-20012,-200,'LM User Assignment','false','Controls visibility of LM objects for 3-tier and direct control, based off assignment of users.');
insert into YukonRoleProperty values(-20013,-200,'Edit Device Config','false','Controls the ability to edit and create device configurations');
insert into YukonRoleProperty values(-20014,-200,'View Device Config','true','Controls the ability to view existing device configurations');

/* Operator Metering Role Properties*/
insert into YukonRoleProperty values(-20203,-202,'Enable Bulk Importer','true','Allows access to the Bulk Importer');
insert into YukonRoleProperty values(-20204,-202,'Enable Tou','true','Allows access to Tou(Time of use) data');  
insert into YukonRoleProperty values(-20205,-202,'Enable Device Group','true','Allows access to change device groups for a device');  
insert into YukonRoleProperty values(-20206,-202,'Enable Profile Request','true','Access to perform profile data request');
insert into YukonRoleProperty values(-20207,-202,'Enable Auto Archiving','true','Allows a user to setup automatic archiving on their yukon system pertaining to the move in/move out interface');

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
insert into YukonRoleProperty values(-20846,-201,'Label Alt Tracking #','Alt Tracking #','Text of the alternate tracking number label on a customer account');

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
insert into YukonRoleProperty values(-20890,-201,'Address State Label','State','Labelling for the address field which is usually state in the US or province in Canada');
insert into YukonRoleProperty values(-20891,-201,'Address County Label','County','Labelling for the address field which is usually county in the US or postal code in Canada');
insert into YukonRoleProperty values(-20892,-201,'Address PostalCode Label','Zip','Labelling for the address field which is usually zip code in the US or postal code in Canada');
insert into YukonRoleProperty values(-20893,-201,'Inventory Checking Create','true','Allow creation of inventory if not found during Inventory Checking');
insert into YukonRoleProperty values(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');

/* Operator Hardware Inventory Role Properties */
insert into YukonRoleProperty values(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
insert into YukonRoleProperty values(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardwares by serial number range');
insert into YukonRoleProperty values(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardwares by serial number range');
insert into YukonRoleProperty values(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardwares by serial number range');
insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');
insert into YukonRoleProperty values(-20905,-209,'Create Hardware','true','Controls whether to allow creating new hardware');
insert into YukonRoleProperty values(-20906,-209,'Expresscom Restore First','false','Controls whether an opt out command should also contain a restore');
insert into YukonRoleProperty values(-20907,-209,'Allow Designation Codes','false','Toggles on or off the ability utilize service company zip codes.');
insert into YukonRoleProperty values(-20908,-209,'Multiple Warehouses','false','Allows for inventory to be assigned to multiple user-created warehouses instead of a single generic warehouse.');
insert into YukonRoleProperty values(-20909,-209,'Purchasing Access','false','Activates the purchasing section of the inventory module.'); 

/* operator work order management role properties */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');
insert into YukonRoleProperty values(-21002,-210,'Work Order Report','true','Controls whether to allow reporting on work orders');
insert into YukonRoleProperty values(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');

insert into YukonRoleProperty values(-21100,-211,'CI Curtailment Label','CI Curtailment','The operator specific name for C&I Curtailment'); 

/* Scheduler Role properties */
insert into YukonRoleProperty values(-21200,-212,'Enable/Disable Schedule','true','Right to enable or disable a schedule'); 

/* CICustomer Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-30000,-300,'Direct Loadcontrol Label','Direct Control','The customer specific name for direct loadcontrol');
insert into YukonRoleProperty values(-30001,-300,'Individual Switch','false','Controls access to customer individual switch control');

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
insert into YukonRoleProperty values(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
insert into YukonRoleProperty values(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');

/* Capacitor Control role properties */
insert into YukonRoleProperty values(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
insert into YukonRoleProperty values(-70001,-700,'Allow Control','true','Enables or disables field and local controls for the given user');
insert into YukonRoleProperty values(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
insert into YukonRoleProperty values(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
insert into YukonRoleProperty values(-70004,-700,'Hide One-Lines','false','Sets the visibility of one-line displays.');
insert into YukonRoleProperty values(-70005,-700,'cap_control_interface','amfm','Optional interface to the AMFM mapping system');
insert into YukonRoleProperty values(-70006,-700,'cbc_creation_name','CBC <PAOName>','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-70007,-700,'pfactor_decimal_places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-70008,-700,'cbc_allow_ovuv','false','Allows users to toggle OV/UV usage on capbanks');
insert into YukonRoleProperty values(-70009,-700,'CBC Refresh Rate','60','The rate, in seconds, all CBC clients reload data from the CBC server');
insert into YukonRoleProperty values(-70010,-700,'Database Editing','false','Allows the user to view/modify the database set up for all CapControl items');
insert into YukonRoleProperty values(-70011,-700,'Show flip command', 'false', 'Show flip command for Cap Banks with 7010 type controller');
insert into YukonRoleProperty values(-70012,-700,'Show Cap Bank Add Info','false','Show Cap Bank Addititional Info tab');
insert into YukonRoleProperty values(-70013,-700,'Definition Available','Open,OpenQuestionable,OpenPending','Capbank sized in these states will be added to the available sum.');
insert into YukonRoleProperty values(-70014,-700,'Definition Unavailable','Close,CloseQuestionable,CloseFail,ClosePending,OpenFail','Capbank sized in these states will be added to the unavailable sum.');
insert into YukonRoleProperty values(-70015,-700,'Definition Tripped','Open,OpenFail,OpenPending,OpenQuestionable','Capbank sized in these states will be added to the tripped sum.');
insert into YukonRoleProperty values(-70016,-700,'Definition Closed','Close,CloseFail,CloseQuestionable,ClosePending','Capbank sized in these states will be added to the closed sum.');

/* Notification / IVR Role properties */
insert into YukonRoleProperty values(-1400,-800,'voice_app','login','The voice server application that Yukon should use');
insert into YukonRoleProperty values(-80001,-800,'Number of Channels','1','The number of outgoing channels assigned to the specified voice application.');
insert into YukonRoleProperty values(-80002,-800,'Intro Text','An important message from your energy provider','The text that is read after the phone is answered, but before the pin has been entered');

/* Notification / Configuration role properties */
insert into YukonRoleProperty values(-80100,-801,'Template Root','Server/web/webapps/ROOT/WebConfig/custom/notif_templates/','Either a URL base where the notification templates will be stored (file: or http:) or a directory relative to YUKON_BASE.');

/* Loadcontrol Role Properties */
insert into YukonRoleProperty values(-90000,-900,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
insert into YukonRoleProperty values(-90001,-900,'Individual Switch','true','Controls access to operator individual switch control');
insert into YukonRoleProperty values(-90002,-900,'3 Tier Direct Control','false','Allows access to the 3-tier load management web interface');
insert into YukonRoleProperty values(-90003,-900,'Direct Loadcontrol','true','Allows access to the Direct load management web interface');
insert into YukonRoleProperty values(-90004,-900,'Constraint Check','true','Allow load management program constraints to be CHECKED before starting');
insert into YukonRoleProperty values(-90005,-900,'Constraint Observe','true','Allow load management program constraints to be OBSERVED before starting');
insert into YukonRoleProperty values(-90006,-900,'Constraint Override','true','Allow load management program constraints to be OVERRIDDEN before starting');
insert into YukonRoleProperty values(-90007,-900,'Constraint Default','Check','The default program constraint selection prior to starting a program');
insert into YukonRoleProperty values(-90008,-900,'Allow Gear Change for Stop','false','Activates the ability to change gears as part of manually stopping a load program'); 

/* Capacitor Control role properties cont...*/
insert into YukonRoleProperty values(-100000, -1000, 'Target', 'true', 'display Target settings');
insert into YukonRoleProperty values(-100001, -1000, 'kVAR', 'true', 'display kVAR');
insert into YukonRoleProperty values(-100002, -1000, 'Estimated kVAR', 'true', 'display estimated kVAR');
insert into YukonRoleProperty values(-100003, -1000, 'Power Factor', 'true', 'display Power Factor');
insert into YukonRoleProperty values(-100004, -1000, 'Estimated Power Factor', 'true', 'display estimated Power Factor');
insert into YukonRoleProperty values(-100005, -1000, 'Watts', 'true', 'display Watts');
insert into YukonRoleProperty values(-100006, -1000, 'Volts', 'true', 'display Volts');
insert into YukonRoleProperty values(-100007, -1000, 'Daily Op Count', 'false', 'display Daily Operation Count');
insert into YukonRoleProperty values(-100008, -1000, 'Max Op Count', 'false', 'display Max Operation Count');

insert into YukonRoleProperty values(-100100, -1001, 'kVAR', 'true', 'display kVAR');
insert into YukonRoleProperty values(-100101, -1001, 'Power Factor', 'true', 'display Power Factor');
insert into YukonRoleProperty values(-100102, -1001, 'Watts', 'true', 'display Watts');
insert into YukonRoleProperty values(-100103, -1001, 'Daily Op Count', 'true', 'display Daily Operation Count');
insert into YukonRoleProperty values(-100104, -1001, 'Volts', 'true', 'display Volts');
insert into YukonRoleProperty values(-100105, -1001, 'Target', 'true', 'is target stat displayed');

insert into YukonRoleProperty values(-100200, -1002, 'Total Op Count', 'true', 'display Total Operation Count');
insert into YukonRoleProperty values(-100201, -1002, 'Bank Size', 'true', 'display Bank Size');
insert into YukonRoleProperty values(-100202, -1002, 'CBC Name', 'true', 'display CBC Name');
insert into YukonRoleProperty VALUES(-100204, -1002, 'Daily/Total Operation Count', 'true', 'is Daily/Total Operation Count displayed');


insert into yukonroleproperty values (-100011,-1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into yukonroleproperty values (-100012,-1000, 'Substation Last Update Timestamp', 'true', 'is last update timestamp shown for substations');
insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timestamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timestamp shown for capbanks');
insert into YukonRoleProperty values (-100205,-1002, 'Capbank Fixed/Static Text', 'Fixed', 'The text to display for fixed/static capbanks');

/*==============================================================*/
/* Table: YukonSelectionList                                    */
/*==============================================================*/
create table YukonSelectionList  (
   ListID               NUMBER                          not null,
   Ordering             VARCHAR2(1)                     not null,
   SelectionLabel       VARCHAR2(30)                    not null,
   WhereIsList          VARCHAR2(100)                   not null,
   ListName             VARCHAR2(40)                    not null,
   UserUpdateAvailable  VARCHAR2(1)                     not null,
   constraint PK_YUKONSELECTIONLIST primary key (ListID)
)
;

insert into YukonSelectionList values( 0, 'N', '(none)', '(none)', '(none)', 'N' );
insert into YukonSelectionList values( 1, 'A', 'Contact', 'DBEditor contact type list', 'ContactType', 'N' );

insert into YukonSelectionList values (100, 'A', 'Calc Functions', 'DBEditor calc point functions', 'CalcFunctions', 'N' );

insert into YukonSelectionList values (1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N');
insert into YukonSelectionList values (1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N');
insert into YukonSelectionList values (1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N');
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','Y');
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','Y');
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection','DeviceStatus','Y');
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
insert into YukonSelectionList values (1065,'A','(none)','Customer account rate schedule selection','RateSchedule','Y');
insert into YukonSelectionList values (1066,'A','(none)','Energy Company Settlement Types','Settlement','Y');
insert into yukonselectionlist values (1067, 'A', '(none)', 'System category types for Event Logging in STARS', 'EventSystemCategory', 'N');
insert into yukonselectionlist values (1068, 'A', '(none)', 'Action types for Customer Account events in STARS', 'EventAccountActions', 'N');
insert into YukonSelectionList values (1071,'A','(none)','Commercial Customer Types','CICustomerType','N');

insert into YukonSelectionList values (2000,'N','(none)','Customer Selection Base','(none)','N');

/*==============================================================*/
/* Table: YukonServices                                         */
/*==============================================================*/
create table YukonServices  (
   ServiceID            NUMBER                          not null,
   ServiceName          VARCHAR2(60)                    not null,
   ServiceClass         VARCHAR2(100)                   not null,
   ParamNames           VARCHAR2(300)                   not null,
   ParamValues          VARCHAR2(300)                   not null,
   constraint PK_YUKSER primary key (ServiceID)
)
;

insert into YukonServices values( -2, 'WebGraph', 'com.cannontech.jmx.services.DynamicWebGraph', '(none)', '(none)' );
insert into YukonServices values( -3, 'Calc_Historical', 'com.cannontech.jmx.services.DynamicCalcHist', '(none)', '(none)' );
insert into YukonServices values( -4, 'CBC_OneLine_Gen', 'com.cannontech.jmx.services.DynamicCBCOneLine', '(none)', '(none)');
insert into YukonServices values( -5, 'MCT410_BulkImporter', 'com.cannontech.jmx.services.DynamicImp', '(none)', '(none)' );
insert into YukonServices values( -6, 'Price_Server', 'com.cannontech.jmx.services.DynamicPriceServer', '(none)', '(none)' );

/*==============================================================*/
/* Table: YukonUser                                             */
/*==============================================================*/
create table YukonUser  (
   UserID               NUMBER                          not null,
   UserName             VARCHAR2(64)                    not null,
   Password             VARCHAR2(64)                    not null,
   Status               VARCHAR2(20)                    not null,
   AuthType             VARCHAR2(16)                    not null,
   constraint PK_YUKONUSER primary key (UserID)
)
;

insert into yukonuser values ( -9999, '(none)', '(none)', 'Disabled', 'PLAIN' );
insert into yukonuser values ( -100, 'DefaultCTI', '$cti_default', 'Enabled', 'PLAIN' );
insert into yukonuser values ( -2, 'yukon', 'yukon', 'Enabled', 'PLAIN' );
insert into yukonuser values ( -1, 'admin', 'admin', 'Enabled', 'PLAIN' );

/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique index Indx_YkUsIDNm on YukonUser (
   UserName ASC
)
;

/*==============================================================*/
/* Table: YukonUserGroup                                        */
/*==============================================================*/
create table YukonUserGroup  (
   UserID               NUMBER                          not null,
   GroupID              NUMBER                          not null,
   constraint PK_YUKONUSERGROUP primary key (UserID, GroupID)
)
;

insert into YukonUserGroup values(-1,-1);
insert into YukonUserGroup values(-2,-1);
insert into YukonUserGroup values(-2,-2);

/*==============================================================*/
/* Table: YukonUserRole                                         */
/*==============================================================*/
create table YukonUserRole  (
   UserRoleID           NUMBER                          not null,
   UserID               NUMBER                          not null,
   RoleID               NUMBER                          not null,
   RolePropertyID       NUMBER                          not null,
   Value                VARCHAR2(1000)                  not null,
   constraint PK_YKONUSRROLE primary key (UserRoleID)
)
;

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
insert into YukonUserRole values(-127,-1,-101,-10107,'(none)');
insert into YukonUserRole values(-128,-1,-101,-10108,'(none)');
insert into YukonUserRole values(-130,-1,-101,-10111,'(none)');


/* Trending */
insert into YukonUserRole values(-150,-1,-102,-10200,'(none)');

insert into YukonUserRole values(-152,-1,-102,-10202,'(none)');
insert into YukonUserRole values(-153,-1,-102,-10203,'(none)');
insert into YukonUserRole values(-154,-1,-102,-10204,'(none)');
insert into YukonUserRole values(-155,-1,-102,-10205,'(none)');
insert into YukonUserRole values(-156,-1,-102,-10206,'(none)');
insert into YukonUserRole values(-157,-1,-102,-10207,'(none)');
insert into YukonUserRole values(-158,-1,-102,-10208,'(none)');
insert into YukonUserRole values(-159,-1,-102,-10209,'(none)');
insert into YukonUserRole values(-160,-1,-102,-10210,'(none)');
insert into YukonUserRole values(-161,-1,-102,-10211,'(none)');
insert into YukonUserRole values(-162,-1,-102,-10212,'(none)');
insert into YukonUserRole values(-163,-1,-102,-10213,'(none)');
insert into YukonUserRole values(-164,-1,-102,-10214,'(none)');
insert into YukonUserRole values(-165,-1,-102,-10215,'(none)');
insert into YukonUserRole values(-166,-1,-102,-10216,'(none)');
insert into YukonUserRole values(-167,-1,-102,-10217,'(none)');
insert into YukonUserRole values(-168,-1,-102,-10218,'(none)');
insert into YukonUserRole values(-169,-1,-102,-10219,'(none)');
insert into YukonUserRole values(-149,-1,-102,-10220,'(none)');
insert into YukonUserRole values(-148,-1,-102,-10221,'(none)');

/* Commander */
insert into YukonUserRole values(-170,-1,-103,-10300,'(none)');
insert into YukonUserRole values(-171,-1,-103,-10301,'true');
insert into YukonUserRole values(-172,-1,-103,-10302,'true');
insert into YukonUserRole values(-173,-1,-103,-10303,'false');
insert into YukonUserRole values(-174,-1,-103,-10304,'false');
insert into YukonUserRole values(-175,-1,-103,-10305,'(none)');

insert into YukonUserRole values(-190,-1,-106,-10600,'(none)');

/* Esubstation Editor */
insert into YukonUserRole values(-250,-1,-107,-10700,'(none)');

/* Esub Drawings */
insert into YukonUserRole values(-300,-1,-206,-20600,'(none)');
insert into YukonUserRole values(-301,-1,-206,-20601,'(none)');
insert into YukonUserRole values(-302,-1,-206,-20602,'(none)');

insert into YukonUserRole values(-350,-1,-206,-20600,'(none)');
insert into YukonUserRole values(-351,-1,-206,-20601,'true');
insert into YukonUserRole values(-352,-1,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into YukonUserRole values (-400, -1, -108, -10800, '/operator/Operations.jsp');
insert into YukonUserRole values (-402, -1, -108, -10802, '(none)');
insert into YukonUserRole values (-403, -1, -108, -10803, '(none)');
insert into YukonUserRole values (-404, -1, -108, -10804, '(none)');
insert into YukonUserRole values (-405, -1, -108, -10805, '(none)');
insert into YukonUserRole values (-406, -1, -108, -10806, '(none)');
insert into YukonUserRole values (-407, -1, -108, -10807, '(none)');
insert into YukonUserRole values (-408, -1, -108, -10808, '(none)');
insert into YukonUserRole values (-41000,-1, -108, -10810, '(none)');
insert into YukonUserRole values (-41001,-1, -108, -10811, '(none)');

/* Give admin login access to View Logs, */
insert into YukonUserRole values (-2000, -1, -200, -20009, '(none)');

/* Give admin login access to view Reporting on main page */
insert into YukonUserRole values (-3000, -1, -109, -10920, '(none)');

insert into YukonUserRole values (-1000, -100, -108, -10800, '/operator/Operations.jsp');
insert into YukonUserRole values (-1002, -100, -108, -10802, '(none)');
insert into YukonUserRole values (-1003, -100, -108, -10803, '(none)');
insert into YukonUserRole values (-1004, -100, -108, -10804, '(none)');
insert into YukonUserRole values (-1005, -100, -108, -10805, '(none)');
insert into YukonUserRole values (-1006, -100, -108, -10806, '(none)');
insert into YukonUserRole values (-1007, -100, -108, -10811, '(none)');

insert into YukonUserRole values (-1010, -100, -200, -20000, '(none)');
insert into YukonUserRole values (-1011, -100, -200, -20001, 'true');
insert into YukonUserRole values (-1012, -100, -200, -20002, '(none)');
insert into YukonUserRole values (-1013, -100, -200, -20003, '(none)');
insert into YukonUserRole values (-1014, -100, -200, -20004, '(none)');
insert into YukonUserRole values (-1015, -100, -200, -20005, '(none)');
insert into YukonUserRole values (-1016, -100, -200, -20006, '(none)');
insert into YukonUserRole values (-1017, -100, -200, -20007, '(none)');
insert into YukonUserRole values (-1018, -100, -200, -20008, '(none)');
insert into YukonUserRole values (-1019, -100, -200, -20009, '(none)');




/*==============================================================*/
/* Table: YukonWebConfiguration                                 */
/*==============================================================*/
create table YukonWebConfiguration  (
   ConfigurationID      NUMBER                          not null,
   LogoLocation         VARCHAR2(100),
   Description          VARCHAR2(500),
   AlternateDisplayName VARCHAR2(100),
   URL                  VARCHAR2(100),
   constraint PK_YUKONWEBCONFIGURATION primary key (ConfigurationID)
)
;

INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Cooling','Cool');
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Heating','Heat');
insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');

/*==============================================================*/
/* View: CCINVENTORY_VIEW                                       */
/*==============================================================*/
create or replace view CCINVENTORY_VIEW(Region, SubName, FeederName, subId, substationid, fdrId, CBCName, cbcId, capbankname, bankId, CapBankSize, Sequence, ControlStatus, SWMfgr, SWType, ControlType, Protocol, IPADDRESS, SlaveAddress, LAT, LON, DriveDirection, OpCenter, TA) as
SELECT yp4.paoname AS Region, yp3.PAOName AS SubName, yp2.PAOName AS FeederName, yp3.PAObjectID AS subId, ssl.substationid AS substationid, yp2.PAObjectID AS fdrId, 
                      yp.PAOName AS CBCName, yp.PAObjectID AS cbcId, yp1.PAOName AS capBankName, yp1.PAObjectID AS bankId, cb.BANKSIZE AS CapBankSize, 
                      fb.ControlOrder AS Sequence, dcb.ControlStatus, cb.SwitchManufacture AS SWMfgr, cb.TypeOfSwitch AS SWType, 
                      cb.OPERATIONALSTATE AS ControlType, cb.ControllerType AS Protocol, pts.IPADDRESS, da.SlaveAddress, 
                                capa.latitude AS LAT, capa.longitude AS LON, capa.drivedirections AS DriveDirection, 
                                cb.maplocationid AS OpCenter, capa.maintenanceareaid AS TA
FROM CAPBANK cb INNER JOIN
                      YukonPAObject yp ON yp.PAObjectID = cb.CONTROLDEVICEID INNER JOIN
                      YukonPAObject yp1 ON yp1.PAObjectID = cb.DEVICEID INNER JOIN
                      DynamicCCCapBank dcb ON dcb.CapBankID = yp1.PAObjectID INNER JOIN
                      STATE s ON s.STATEGROUPID = 3 AND dcb.ControlStatus = s.RAWSTATE INNER JOIN
                      CCFeederBankList fb ON fb.DeviceID = cb.DEVICEID INNER JOIN
                      YukonPAObject yp2 ON yp2.PAObjectID = fb.FeederID INNER JOIN
                      CCFeederSubAssignment sf ON fb.FeederID = sf.FeederID INNER JOIN
                      YukonPAObject yp3 ON yp3.PAObjectID = sf.SubStationBusID INNER JOIN
                      ccsubstationsubbuslist ssl on ssl.substationbusid = yp3.paobjectid INNER JOIN
                      ccsubareaassignment sa on sa.substationbusid = ssl.substationid INNER JOIN
                      yukonpaobject yp4 on yp4.paobjectid = sa.areaid INNER JOIN
                      DeviceDirectCommSettings ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
                      PORTTERMINALSERVER pts ON pts.PORTID = ddcs.PORTID INNER JOIN
                      DeviceCBC cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      capbankadditional capa on capa.deviceid = cb.deviceid
;

/*==============================================================*/
/* View: CCOPERATIONS_VIEW                                      */
/*==============================================================*/
create or replace view CCOPERATIONS_VIEW(cbcName, capbankname, opTime, operation, confTime, confStatus, feederName, feederId, subName, subBusId, substationid, region, BANKSIZE, protocol, ipAddress, serialNum, SlaveAddress, kvarAfter, kvarChange, kvarBefore) as
SELECT 
      yp3.PAOName AS cbcName, yp.PAOName AS capbankname, el.DateTime AS opTime, el.Text AS operation, 
      el2.DateTime AS confTime, el2.Text AS confStatus, yp1.PAOName AS feederName, yp1.PAObjectID AS feederId, 
        yp2.PAOName AS subName, yp2.PAObjectID AS subBusId, ssl.substationid AS substationid, yp4.paoname AS region, cb.BANKSIZE, 
        cb.ControllerType AS protocol, p.Value AS ipAddress, cbc.SERIALNUMBER AS serialNum, da.SlaveAddress, 
        el2.kvarAfter, el2.kvarChange, el2.kvarBefore
FROM   
      (SELECT op.LogID AS oid, MIN(aaa.confid) AS cid FROM
              (SELECT LogID, PointID FROM CCEventLog 
        WHERE Text LIKE '%Close sent%' OR Text LIKE '%Open sent%') op
        LEFT OUTER JOIN 
        (SELECT el.LogID AS opid, MIN(el2.LogID) AS confid 
        FROM CCEventLog el INNER JOIN CCEventLog el2 ON el2.PointID = el.PointID AND el.LogID < el2.LogID 
        LEFT OUTER JOIN
        (SELECT a.LogID AS aid, MIN(b.LogID) AS next_aid FROM 
        CCEventLog a INNER JOIN CCEventLog b ON a.PointID = b.PointID AND a.LogID < b.LogID 
        WHERE (a.Text LIKE '%Close sent,%' OR a.Text LIKE '%Open sent,%') 
        AND (b.Text LIKE '%Close sent,%' OR b.Text LIKE '%Open sent,%')
        GROUP BY a.LogID) el3 ON el3.aid = el.LogID 
      WHERE (el.Text LIKE '%Close sent,%' OR el.Text LIKE '%Open sent,%') 
        AND (el2.Text LIKE 'Var: %') AND (el2.LogID < el3.next_aid) 
      OR (el.Text LIKE '%Close sent,%' OR el.Text LIKE '%Open sent,%') 
        AND (el2.Text LIKE 'Var: %') AND (el3.next_aid IS NULL)
        GROUP BY el.LogID)  aaa ON op.LogID = aaa.opid
GROUP BY op.LogID) OpConf INNER JOIN
        CCEventLog el ON el.LogID = OpConf.oid LEFT OUTER JOIN
        CCEventLog el2 ON el2.LogID = OpConf.cid INNER JOIN
        POINT ON POINT.POINTID = el.PointID INNER JOIN
        DynamicCCCapBank ON DynamicCCCapBank.CapBankID = POINT.PAObjectID INNER JOIN
        YukonPAObject yp ON yp.PAObjectID = DynamicCCCapBank.CapBankID INNER JOIN
        YukonPAObject yp1 ON yp1.PAObjectID = el.FeederID INNER JOIN
        YukonPAObject yp2 ON yp2.PAObjectID = el.SubID INNER JOIN
        CAPBANK cb ON cb.DEVICEID = DynamicCCCapBank.CapBankID LEFT OUTER JOIN
        DeviceDirectCommSettings ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
        YukonPAObject yp3 ON yp3.PAObjectID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        DeviceCBC cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        (SELECT EntryID, PAObjectID, Owner, InfoKey, Value, UpdateTime
        FROM DynamicPAOInfo WHERE (InfoKey LIKE '%udp ip%')) 
        p ON p.PAObjectID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        ccsubstationsubbuslist ssl on ssl.substationbusid = el.subid  LEFT OUTER JOIN
        ccsubareaassignment csa on csa.substationbusid = ssl.substationid left outer join 
        YukonPAObject yp4 ON yp4.paobjectid = csa.areaid
;

/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create or replace view DISPLAY2WAYDATA_VIEW as
select POINTID as PointID, POINTNAME as PointName, POINTTYPE as PointType, SERVICEFLAG as PointState, YukonPAObject.PAOName as DeviceName, YukonPAObject.Type as DeviceType, YukonPAObject.Description as DeviceCurrentState, YukonPAObject.PAObjectID as DeviceID, '**DYNAMIC**' as PointValue, '**DYNAMIC**' as PointQuality, '**DYNAMIC**' as PointTimeStamp, (select uomname from pointunit,unitmeasure where pointunit.pointid=point.pointid and pointunit.uomid=unitmeasure.uomid) as UofM, '**DYNAMIC**' as Tags
from YukonPAObject, POINT
where YukonPAObject.PAObjectID = POINT.PAObjectID
;

/*==============================================================*/
/* View: ExpressComAddress_View                                 */
/*==============================================================*/
create or replace view ExpressComAddress_View as
select x.LMGroupID, x.RouteID, x.SerialNumber, s.Address as serviceaddress,
g.Address as geoaddress, b.Address as substationaddress, f.Address as feederaddress,
z.Address as ZipCodeAddress, us.Address as UDAddress, p.Address as programaddress, sp.Address as SplinterAddress, x.AddressUsage, x.RelayUsage
from LMGroupExpressCom x, LMGroupExpressComAddress s, 
LMGroupExpressComAddress g, LMGroupExpressComAddress b, LMGroupExpressComAddress f,
LMGroupExpressComAddress p,
LMGroupExpressComAddress sp, LMGroupExpressComAddress us, LMGroupExpressComAddress z
where ( x.ServiceProviderID = s.AddressID and ( s.AddressType = 'SERVICE' or s.AddressID = 0 ) )
and ( x.FeederID = f.AddressID and ( f.AddressType = 'FEEDER' or f.AddressID = 0 ) )
and ( x.GeoID = g.AddressID and ( g.AddressType = 'GEO' or g.AddressID = 0 ) )
and ( x.ProgramID = p.AddressID and ( p.AddressType = 'PROGRAM' or p.AddressID = 0 ) )
and ( x.SubstationID = b.AddressID and ( b.AddressType = 'SUBSTATION' or b.AddressID = 0 ) )
and ( x.SplinterID = sp.AddressID and ( sp.AddressType = 'SPLINTER' or sp.AddressID = 0 ) )
and ( x.UserID = us.AddressID and ( us.AddressType = 'USER' or us.AddressID = 0 ) )
and ( x.ZipID = z.AddressID and ( z.AddressType = 'ZIP' or z.AddressID = 0 ) )
;

/*==============================================================*/
/* View: FeederAddress_View                                     */
/*==============================================================*/
create or replace view FeederAddress_View as
select x.LMGroupID, a.Address as FeederAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.FeederID = a.AddressID and ( a.AddressType = 'FEEDER' or a.AddressID = 0 ) )
;

/*==============================================================*/
/* View: FullEventLog_View                                      */
/*==============================================================*/
create or replace view FullEventLog_View(EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, DeviceName, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, y.PAOName, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from YukonPAObject y, POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
;

/*==============================================================*/
/* View: FullPointHistory_View                                  */
/*==============================================================*/
create or replace view FullPointHistory_View(PointID, DeviceName, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, y.PAOName, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from YukonPAObject y, POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
;

/*==============================================================*/
/* View: GeoAddress_View                                        */
/*==============================================================*/
create or replace view GeoAddress_View as
select x.LMGroupID, a.Address as GeoAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.GeoID = a.AddressID and ( a.AddressType = 'GEO' or a.AddressID = 0 ) )
;

/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create or replace view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID
;

/*==============================================================*/
/* View: LMProgram_View                                         */
/*==============================================================*/
create or replace view LMProgram_View as
select t.DeviceID, t.ControlType, u.ConstraintID, u.ConstraintName, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID
;

/*==============================================================*/
/* View: Peakpointhistory_View                                  */
/*==============================================================*/
create or replace view Peakpointhistory_View as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where VALUE in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by POINTID, VALUE
;

/*==============================================================*/
/* View: PointEventLog_View                                     */
/*==============================================================*/
create or replace view PointEventLog_View(EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID
;

/*==============================================================*/
/* View: PointHistory_View                                      */
/*==============================================================*/
create or replace view PointHistory_View(PointID, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID
;

/*==============================================================*/
/* View: ProgramAddress_View                                    */
/*==============================================================*/
create or replace view ProgramAddress_View as
select x.LMGroupID, a.Address as ProgramAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ProgramID = a.AddressID and ( a.AddressType = 'PROGRAM' or a.AddressID = 0 ) )
;

/*==============================================================*/
/* View: ServiceAddress_View                                    */
/*==============================================================*/
create or replace view ServiceAddress_View as
select x.LMGroupID, a.Address as ServiceAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ServiceProviderID = a.AddressID and ( a.AddressType = 'SERVICE' or a.AddressID = 0 ) )
;

/*==============================================================*/
/* View: SubstationAddress_View                                 */
/*==============================================================*/
create or replace view SubstationAddress_View as
select x.LMGroupID, a.Address as SubstationAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.SubstationID = a.AddressID and ( a.AddressType = 'SUBSTATION' or a.AddressID = 0 ) )
;

alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
;

alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID)
;

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
;

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID)
;

alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID)
;

alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID)
;

alter table CAPBANKADDITIONAL
   add constraint FK_CAPBANKA_CAPBANK foreign key (DeviceID)
      references CAPBANK (DEVICEID)
;

alter table CAPCONTROLAREA
   add constraint FK_CAPCONTR_REFERENCE_YUKONPAO foreign key (AreaID)
      references YukonPAObject (PAObjectID)
;

alter table CAPCONTROLAREA
   add constraint FK_CAPCONTR_REFERENCE_DYNAMICC foreign key (AreaID)
      references DYNAMICCCAREA (AreaID)
;

alter table CAPCONTROLSPECIALAREA
   add constraint FK_CAPCONTR_YUKONPAO2 foreign key (AreaID)
      references YukonPAObject (PAObjectID)
;

alter table CAPCONTROLSUBSTATION
   add constraint FK_CAPCONTR_REF_YUKONPA2 foreign key (SubstationID)
      references YukonPAObject (PAObjectID)
      on delete cascade
;

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_SWPTID foreign key (SwitchPointID)
      references POINT (POINTID)
;

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_CVOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID)
;

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID)
;

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
;

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
;

alter table CCFeederBankList
   add constraint FK_CB_CCFeedLst foreign key (DeviceID)
      references CAPBANK (DEVICEID)
;

alter table CCFeederBankList
   add constraint FK_CCFeed_CCBnk foreign key (FeederID)
      references CapControlFeeder (FeederID)
;

alter table CCFeederSubAssignment
   add constraint FK_CCFeed_CCFass foreign key (FeederID)
      references CapControlFeeder (FeederID)
;

alter table CCFeederSubAssignment
   add constraint FK_CCSub_CCFeed foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
;

alter table CCMONITORBANKLIST
   add constraint FK_CCMONBNKLIST_BNKID foreign key (BankID)
      references CAPBANK (DEVICEID)
;

alter table CCMONITORBANKLIST
   add constraint FK_CCMONBNKLST_PTID foreign key (PointID)
      references POINT (POINTID)
;

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_PAOID foreign key (paobjectid)
      references YukonPAObject (PAObjectID)
;

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_SCHEDID foreign key (seasonscheduleid)
      references SeasonSchedule (ScheduleID)
;

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_ccssa_season foreign key (seasonscheduleid, seasonname)
      references DateOfSeason (SeasonScheduleID, SeasonName)
;

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSEASON_REFERENCE_CAPCONTR foreign key (strategyid)
      references CapControlStrategy (StrategyID)
;

alter table CCSTRATEGYTIMEOFDAY
   add constraint FK_STRAT_TOD_CCSTRAT foreign key (StrategyID)
      references CapControlStrategy (StrategyID)
;

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_CAPSUBAREAASSGN foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID)
      on delete cascade
;

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSUBARE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLAREA (AreaID)
;

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_CAPCONTR2 foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID)
      on delete cascade
;

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID)
      on delete cascade
;

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSUBSTA_CAPCONTR foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID)
;

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSUBSTA_REFERENCE_CAPCONTR foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
;

alter table CCURTACCTEVENT
   add constraint FK_CCURTACC_CCURTPRO foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
;

alter table CCURTACCTEVENTPARTICIPANT
   add constraint FK_CCURTACCTEVENTID foreign key (CCurtAcctEventID)
      references CCURTACCTEVENT (CCurtAcctEventID)
;

alter table CCURTACCTEVENTPARTICIPANT
   add constraint FK_CCURTACC_CICUSTOM foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table CCurtCENotif
   add constraint FK_CCCURTCE_NOTIF_PART foreign key (CCurtCEParticipantID)
      references CCurtCEParticipant (CCurtCEParticipantID)
;

alter table CCurtCEParticipant
   add constraint FK_CCURTCE_PART_CURTEVT foreign key (CCurtCurtailmentEventID)
      references CCurtCurtailmentEvent (CCurtCurtailmentEventID)
;

alter table CCurtCEParticipant
   add constraint FK_CCURTCURTEVENTCICUST_CICUST foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table CCurtCurtailmentEvent
   add constraint FK_CCURTCURTEVT_CCURTPGM foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
;

alter table CCurtEEParticipant
   add constraint FK_CCURTEEPART_CCURTEE foreign key (CCurtEconomicEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID)
;

alter table CCurtEEParticipant
   add constraint FK_CCURTEEPART_CUST foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table CCurtEEParticipantSelection
   add constraint FK_CCURTEEPARTSEL_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID)
;

alter table CCurtEEParticipantSelection
   add constraint FK_CCURTEEPARTSEL_CCURTPART foreign key (CCurtEEParticipantID)
      references CCurtEEParticipant (CCurtEEParticipantID)
;

alter table CCurtEEParticipantWindow
   add constraint FK_CCRTEEPRTWIN_CCRTEEPRTSEL foreign key (CCurtEEParticipantSelectionID)
      references CCurtEEParticipantSelection (CCurtEEParticipantSelectionID)
;

alter table CCurtEEParticipantWindow
   add constraint FK_CCRTEEPRTWN_CCRTEEPRIWN foreign key (CCurtEEPricingWindowID)
      references CCurtEEPricingWindow (CCurtEEPricingWindowID)
;

alter table CCurtEEPricing
   add constraint FK_CCURTEEPR_CCURTECONEVT foreign key (CCurtEconomicEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID)
;

alter table CCurtEEPricingWindow
   add constraint FK_CCURTEEPRWIN_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID)
;

alter table CCurtEconomicEvent
   add constraint FK_CCURTEEVT_CCURTPGM foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
;

alter table CCurtEconomicEvent
   add constraint FK_CCURTINITEVT_CCURTECONEVT foreign key (InitialEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID)
;

alter table CCurtEconomicEventNotif
   add constraint FK_CCURTEENOTIF_CCURTEEPARTID foreign key (CCurtEconomicParticipantID)
      references CCurtEEParticipant (CCurtEEParticipantID)
;

alter table CCurtEconomicEventNotif
   add constraint FK_CCURTEENOTIF_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID)
;

alter table CCurtGroupCustomerNotif
   add constraint FK_CCURTGRO_FK_CCURTG_CCURTGRO foreign key (CCurtGroupID)
      references CCurtGroup (CCurtGroupID)
;

alter table CCurtGroupCustomerNotif
   add constraint FK_CCURTGRPCUSTNOTIF_CUST foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table CCurtProgram
   add constraint FK_CCURTPRG_CCURTPRGTYPE foreign key (CCurtProgramTypeID)
      references CCurtProgramType (CCurtProgramTypeID)
;

alter table CCurtProgramGroup
   add constraint FK_CCURTPRGGRP_CCURTGRP foreign key (CCurtGroupID)
      references CCurtGroup (CCurtGroupID)
;

alter table CCurtProgramGroup
   add constraint FK_CCURTPRGGRP_CCURTPRG foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
;

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_CCURTP foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
;

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_NG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
;

alter table CCurtProgramParameter
   add constraint FK_CCURTPRGPARAM_CCURTPRGID foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
;

alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID)
;

alter table CICustomerBase
   add constraint FK_CUSTTYPE_ENTRYID foreign key (CiCustType)
      references YukonListEntry (EntryID)
;

alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID)
;

alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID)
;

alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID)
;

alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
;

alter table CapControlFeeder
   add constraint FK_CAPCONTR_VARPTID foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
;

alter table CapControlFeeder
   add constraint FK_CAPCONTR_VOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID)
;

alter table CapControlFeeder
   add constraint FK_CAPCONTR_WATTPTID foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
;

alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID)
;

alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID)
;

alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
;

alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID)
;

alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID)
;

alter table Contact
   add constraint FK_CONTACT_REF_CNT_A_ADDRESS foreign key (AddressID)
      references Address (AddressID)
;

alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID)
;

alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
;

alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID)
;

alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID)
;

alter table Customer
   add constraint FK_Cust_YkLs foreign key (RateScheduleID)
      references YukonListEntry (EntryID)
;

alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
;

alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID)
;

alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID)
;

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID)
;

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID)
;

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
;

alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID)
;

alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
;

alter table DCCategory
   add constraint FK_DCCAT_DCCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID)
;

alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID)
;

alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCITEMTYPE foreign key (ItemTypeID)
      references DCItemType (ItemTypeID)
;

alter table DCCategoryItemType
   add constraint FK_DCCATITEMTYPE_DCCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID)
;

alter table DCCategoryItemType
   add constraint FK_DCITEMTY_DCCATITEMTY foreign key (ItemTypeID)
      references DCItemType (ItemTypeID)
;

alter table DCConfiguration
   add constraint FK_DCCONFIG_DCCONFIGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID)
;

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID)
;

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID)
;

alter table DCConfigurationCategoryType
   add constraint FK_DCCATTYPE_DCCFGCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID)
;

alter table DCConfigurationCategoryType
   add constraint FK_DCCFGTYPE_DCCFGCATTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID)
;

alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID)
;

alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_YKPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
;

alter table DCDeviceConfigurationType
   add constraint FK_DCCFGTYPE_DCCFGDVCFGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID)
;

alter table DCItemValue
   add constraint FK_DCIITEMVALUE_DCITEMTYPE foreign key (ItemTypeID)
      references DCItemType (ItemTypeID)
;

alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
;

alter table DEVICE2WAYFLAGS
   add constraint SYS_C0013208 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_DEVICECO foreign key (DeviceConfigurationId)
      references DEVICECONFIGURATION (DeviceConfigurationID)
;

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_YUKONPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
      on delete cascade
;

alter table DEVICECONFIGURATIONITEM
   add constraint FK_DEVICECO_REF_DEVICEC2 foreign key (DeviceConfigurationID)
      references DEVICECONFIGURATION (DeviceConfigurationID)
      on delete cascade
;

alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICEGROUP
   add constraint FK_DEVICEGROUP_DEVICEGROUP foreign key (ParentDeviceGroupId)
      references DEVICEGROUP (DeviceGroupId)
;

alter table DEVICEGROUPMEMBER
   add constraint FK_DevGrpMember_DeviceGroup foreign key (DeviceGroupID)
      references DEVICEGROUP (DeviceGroupId)
;

alter table DEVICEGROUPMEMBER
   add constraint FK_DeviceGroupMember_DEVICE foreign key (YukonPaoId)
      references DEVICE (DEVICEID)
      on delete cascade
;

alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICEREADLOG
   add constraint FK_DEVICERE_FK_DRLOGR_DEVICERE foreign key (DeviceReadRequestLogID)
      references DEVICEREADREQUESTLOG (DeviceReadRequestLogID)
;

alter table DEVICEREADREQUESTLOG
   add constraint FK_DEVICERE_FK_DRREQL_DEVICERE foreign key (DeviceReadJobLogID)
      references DEVICEREADJOBLOG (DeviceReadJobLogID)
;

alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID)
;

alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
;

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
;

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
;

alter table DYNAMICACCUMULATOR
   add constraint SYS_C0015129 foreign key (POINTID)
      references POINT (POINTID)
;

alter table DYNAMICBILLINGFIELD
   add constraint FK_DBF_REF_BFF foreign key (FormatID)
      references BillingFileFormats (FormatID)
      on delete cascade
;

alter table DYNAMICBILLINGFORMAT
   add constraint FK_DYNAMICB_REF_BILLI_BILLINGF foreign key (FormatID)
      references BillingFileFormats (FormatID)
;

alter table DYNAMICCCSUBSTATION
   add constraint FK_DYNAMICC_REFERENCE_CAPCONTR foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID)
;

alter table DYNAMICCCTWOWAYCBC
   add constraint FK_DYNAMICC_DEVICECB foreign key (DeviceID)
      references DeviceCBC (DEVICEID)
;

alter table DYNAMICDEVICESCANDATA
   add constraint SYS_C0015139 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DYNAMICPAOSTATISTICSHISTORY
   add constraint FK_DYNPAOSTHIST_YKNPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
      on delete cascade
;

alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID)
;

alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
;

alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
;

alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID)
;

alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID)
;

alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID)
;

alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID)
;

alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID)
;

alter table DevicePagingReceiverSettings
   add constraint FK_DevPaRec_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID)
;

alter table DeviceSeries5RTU
   add constraint FK_DvS5r_Dv2w foreign key (DeviceID)
      references DEVICE2WAYFLAGS (DEVICEID)
;

alter table DeviceTNPPSettings
   add constraint FK_DevTNPP_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Cmd foreign key (CommandID)
      references Command (CommandID)
;

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID)
;

alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
;

alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
;

alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table DynamicCCCapBank
   add constraint FK_CpBnk_DynCpBnk foreign key (CapBankID)
      references CAPBANK (DEVICEID)
;

alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID)
;

alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_BNKID foreign key (BankID)
      references CAPBANK (DEVICEID)
;

alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_PTID foreign key (PointID)
      references POINT (POINTID)
;

alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_BNKID foreign key (BankID)
      references DynamicCCCapBank (CapBankID)
;

alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_PTID foreign key (PointID)
      references POINT (POINTID)
;

alter table DynamicCCSubstationBus
   add constraint FK_CCSubBs_DySubBs foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
;

alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID)
;

alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID)
;

alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER)
;

alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
;

alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID)
;

alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
;

alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
;

alter table DynamicPAOInfo
   add constraint FK_DynPAOInfo_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
;

alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID)
      on delete cascade
;

alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID)
;

alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID)
;

alter table DynamicTags
   add constraint FK_DYNAMICT_REF_DYNTG_TAGS foreign key (TagID)
      references Tags (TagID)
;

alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
;

alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
;

alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
;

alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID)
;

alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
;

alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
;

alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
;

alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID)
;

alter table FDRTRANSLATION
   add constraint SYS_C0015066 foreign key (POINTID)
      references POINT (POINTID)
;

alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
;

alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID)
;

alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
;

alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID)
;

alter table GroupPaoPermission
   add constraint FK_GROUPPAO_REF_YKGRP_YUKONGRO foreign key (GroupID)
      references YukonGroup (GroupID)
;

alter table GroupPaoPermission
   add constraint FK_GROUPPAO_REF_YUKPA_YUKONPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
;

alter table ImportPendingComm
   add constraint FK_ImpPC_PAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
;

alter table JOB
   add constraint FK_Job_YukonUser foreign key (UserID)
      references YukonUser (UserID)
;

alter table JOBPROPERTY
   add constraint FK_JobProperty_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade
;

alter table JOBSCHEDULEDONETIME
   add constraint FK_JobScheduledOneTime_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade
;

alter table JOBSCHEDULEDREPEATING
   add constraint FK_JOBSCHED_REFERENCE_JOB foreign key (JobID)
      references JOB (JobID)
      on delete cascade
;

alter table JOBSTATUS
   add constraint FK_JobStatus_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade
;

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
;

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DeviceID)
;

alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
;

alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID)
;

alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID)
;

alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
;

alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
;

alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID)
;

alter table LMControlScenarioProgram
   add constraint FK_LMCONTRO_REF_LMSCP_LMPROGRA foreign key (ProgramID)
      references LMPROGRAM (DeviceID)
;

alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID)
;

alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
;

alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table LMDirectCustomerList
   add constraint FK_LMDIRECT_REFLMPDIR_LMPROGRA foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
;

alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
;

alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID)
;

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID)
;

alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table LMEnergyExchangeCustomerReply
   add constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
      on delete cascade
;

alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
      on delete cascade
;

alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
;

alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID)
;

alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
;

alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
;

alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID)
;

alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID)
;

alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID)
;

alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID)
;

alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID)
;

alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID)
;

alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
;

alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID)
;

alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID)
;

alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID)
;

alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID)
;

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID)
;

alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID)
;

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID)
;

alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID)
;

alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID)
;

alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
;

alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID)
;

alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID)
;

alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID)
;

alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
;

alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID)
;

alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID)
;

alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID)
;

alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
;

alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID)
;

alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID)
;

alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
;

alter table LMProgramConstraints
   add constraint FK_HlSc_LmPrC foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
;

alter table LMProgramConstraints
   add constraint FK_SesSch_LmPrC foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
;

alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
;

alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID)
;

alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID)
      on delete cascade
;

alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID)
;

alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
;

alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
;

alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
;

alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
;

alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
;

alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID)
;

alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID)
;

alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID)
;

alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID)
;

alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID)
;

alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID)
;

alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPMCT foreign key (MctID)
      references DEVICE (DEVICEID)
;

alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID)
      on delete cascade
;

alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID)
;

alter table MSPInterface
   add constraint FK_Intrfc_Vend foreign key (VendorID)
      references MSPVendor (VendorID)
;

alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
;

alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
;

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
;

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID)
;

alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID)
;

alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
;

alter table PAOExclusion
   add constraint FK_PAOEXCLU_REF_PAOEX_YUKONPAO foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID)
;

alter table PAOScheduleAssignment
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOSchedule (ScheduleID)
;

alter table PAOScheduleAssignment
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
;

alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID)
;

alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID)
;

alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
;

alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
;

alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID)
;

alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID)
;

alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID)
;

alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID)
;

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERTRIGGERPT_PT foreign key (TriggerID)
      references POINT (POINTID)
;

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERVERIF_PT foreign key (VerificationID)
      references POINT (POINTID)
;

alter table POINTTRIGGER
   add constraint FK_PTTRIGGER_PT foreign key (PointID)
      references POINT (POINTID)
;

alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID)
;

alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID)
;

alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references CommPort (PORTID)
;

alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID)
;

alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID)
;

alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID)
;

alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID)
;

alter table PROFILEPEAKRESULT
   add constraint FK_PROFILEPKRSLT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade
;

alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
;

alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
;

alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
;

alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID)
;

alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID)
;

alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
;

alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID)
;

alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID)
;

alter table STATE
   add constraint FK_YkIm_St foreign key (ImageID)
      references YukonImage (ImageID)
;

alter table STATE
   add constraint SYS_C0013342 foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID)
;

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM)
;

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
;

alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID)
;

alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID)
;

alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID)
;

alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID)
;

alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID)
;

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_DISPLAY foreign key (DisplayNum)
      references DISPLAY (DISPLAYNUM)
;

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_TEMPLATE foreign key (TemplateNum)
      references TEMPLATE (TEMPLATENUM)
;

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YKUSR_YUKONUSE foreign key (UserID)
      references YukonUser (UserID)
;

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YUKPA_YUKONPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
;

alter table VersacomRoute
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references Route (RouteID)
;

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID)
;

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
;

alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID)
;

alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID)
;

alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
;

alter table YukonUserGroup
   add constraint FK_YkUsGr_YkGr foreign key (GroupID)
      references YukonGroup (GroupID)
;

alter table YukonUserGroup
   add constraint FK_YUKONUSE_REF_YKUSG_YUKONUSE foreign key (UserID)
      references YukonUser (UserID)
;

alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID)
;

alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID)
;

alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID)
;


create or replace procedure RenCol(
  User in varchar2,       -- name of the schema. 
  Table_Name in varchar2, -- name of the table. 
  Old_Name in varchar2,   -- name of the column to be renamed. 
  New_Name in varchar2    -- new name of the column. 
) 
As
declare
  obj_id number; 
  col_id number; 
  cursor_name1 INTEGER; 
  cursor_name2 INTEGER; 
  ret1 INTEGER; 
  ret2 INTEGER; 

begin
  Select object_id 
  Into obj_id 
  From dba_objects 
  Where object_name=UPPER(table_name) 
  And owner=UPPER(user) 
  And object_type='TABLE'; 

  --DBMS_OutPut.put_line(obj_id); 

  Select col# 
  Into col_id 
  From col$ 
  Where obj#=obj_id 
  And name=UPPER(old_name); 

  --DBMS_OutPut.put_line(col_id); 

  Update col$ 
  Set name=UPPER(new_name) 
  Where obj#=obj_id 
  And col#=col_id; 

  Commit; 

  cursor_name1 := DBMS_Sql.Open_Cursor; 
  DBMS_Sql.Parse(cursor_name1, 'ALTER SYSTEM FLUSH SHARED_POOL',DBMS_Sql.Native); 
  ret1 := DBMS_Sql.Execute(cursor_name1); 
  DBMS_Sql.Close_Cursor(cursor_name1); 

  cursor_name2:= DBMS_Sql.Open_Cursor; 
  DBMS_Sql.Parse(cursor_name2, 'ALTER SYSTEM CHECKPOINT',DBMS_Sql.Native); 
  ret2:= DBMS_Sql.Execute(cursor_name2); 
  DBMS_Sql.Close_Cursor(cursor_name2); 
end;
/**************************************************************************************/
/* Example of use:                                                                    */
/*  SQL> Exec RenCol( 'username', 'tablename', 'old col name', 'new col name' );      */
/*                                                                                    */
/**************************************************************************************/
/

alter procedure RenCol compile
/
;

