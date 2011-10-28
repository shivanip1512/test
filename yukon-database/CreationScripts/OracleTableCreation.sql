/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      ORACLE Version 9i                            */
/* Created on:     10/28/2011 2:09:58 PM                        */
/*==============================================================*/


set define off;



/*==============================================================*/
/* Table: AccountSite                                           */
/*==============================================================*/
create table AccountSite  (
   AccountSiteID        NUMBER                          not null,
   SiteInformationID    NUMBER,
   SiteNumber           VARCHAR2(40)                    not null,
   StreetAddressID      NUMBER,
   PropertyNotes        VARCHAR2(300),
   CustomerStatus       VARCHAR2(1)                     not null,
   CustAtHome           VARCHAR2(1)                     not null,
   constraint PK_ACCOUNTSITE primary key (AccountSiteID)
);

INSERT INTO AccountSite VALUES (0,0,'(none)',0,'(none)',' ','N');

/*==============================================================*/
/* Index: CstSrvCstProp_FK                                      */
/*==============================================================*/
create index CstSrvCstProp_FK on AccountSite (
   SiteInformationID ASC
);

/*==============================================================*/
/* Index: Indx_AcctSite_SiteNum                                 */
/*==============================================================*/
create index Indx_AcctSite_SiteNum on AccountSite (
   SiteNumber ASC
);

/*==============================================================*/
/* Table: AcctThermostatSchedule                                */
/*==============================================================*/
create table AcctThermostatSchedule  (
   AcctThermostatScheduleId NUMBER                          not null,
   AccountId            NUMBER                          not null,
   ScheduleName         VARCHAR2(60)                    not null,
   ThermostatType       VARCHAR2(60)                    not null,
   ScheduleMode         VARCHAR2(60),
   constraint PK_AcctThermSch primary key (AcctThermostatScheduleId)
);

/*==============================================================*/
/* Table: AcctThermostatScheduleEntry                           */
/*==============================================================*/
create table AcctThermostatScheduleEntry  (
   AcctThermostatScheduleEntryId NUMBER                          not null,
   AcctThermostatScheduleId NUMBER                          not null,
   StartTime            NUMBER                          not null,
   TimeOfWeek           VARCHAR2(60)                    not null,
   CoolTemp             FLOAT                           not null,
   HeatTemp             FLOAT                           not null,
   constraint PK_AcctThermSchEntry primary key (AcctThermostatScheduleEntryId)
);

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
);

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
);

insert into address values ( 0, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' );

/*==============================================================*/
/* Index: Indx_Add_LocAdd                                       */
/*==============================================================*/
create index Indx_Add_LocAdd on Address (
   LocationAddress1 ASC
);

/*==============================================================*/
/* Table: AlarmCategory                                         */
/*==============================================================*/
create table AlarmCategory  (
   AlarmCategoryID      NUMBER                          not null,
   CategoryName         VARCHAR2(40)                    not null,
   NotificationGroupID  NUMBER                          not null,
   constraint PK_ALARMCATEGORYID primary key (AlarmCategoryID)
);

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
/* Table: ApplianceAirConditioner                               */
/*==============================================================*/
create table ApplianceAirConditioner  (
   ApplianceID          NUMBER                          not null,
   TonnageID            NUMBER,
   TypeID               NUMBER,
   constraint PK_APPLIANCEAIRCONDITIONER primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceBase                                         */
/*==============================================================*/
create table ApplianceBase  (
   ApplianceID          NUMBER                          not null,
   AccountID            NUMBER                          not null,
   ApplianceCategoryID  NUMBER                          not null,
   ProgramID            NUMBER,
   YearManufactured     NUMBER,
   ManufacturerID       NUMBER,
   LocationID           NUMBER,
   KWCapacity           FLOAT,
   EfficiencyRating     FLOAT,
   Notes                VARCHAR2(500),
   ModelNumber          VARCHAR2(40)                    not null,
   constraint PK_APPLIANCEBASE primary key (ApplianceID)
);

/*==============================================================*/
/* Index: CstAcc_CstLdInfo_FK                                   */
/*==============================================================*/
create index CstAcc_CstLdInfo_FK on ApplianceBase (
   AccountID ASC
);

/*==============================================================*/
/* Index: CstLdTy_CstLdInf_FK                                   */
/*==============================================================*/
create index CstLdTy_CstLdInf_FK on ApplianceBase (
   ApplianceCategoryID ASC
);

/*==============================================================*/
/* Table: ApplianceCategory                                     */
/*==============================================================*/
create table ApplianceCategory  (
   ApplianceCategoryID  NUMBER                          not null,
   Description          VARCHAR2(40),
   CategoryID           NUMBER,
   WebConfigurationID   NUMBER,
   ConsumerSelectable   CHAR(1)                         not null,
   constraint PK_APPLIANCECATEGORY primary key (ApplianceCategoryID)
);

insert into ApplianceCategory values (0,'(none)',0,0,'Y');

/*==============================================================*/
/* Table: ApplianceChiller                                      */
/*==============================================================*/
create table ApplianceChiller  (
   ApplianceID          NUMBER                          not null,
   TonnageID            NUMBER,
   TypeID               NUMBER,
   constraint PK_APPLIANCECHILLER primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceDualFuel                                     */
/*==============================================================*/
create table ApplianceDualFuel  (
   ApplianceID          NUMBER                          not null,
   SwitchOverTypeID     NUMBER                          not null,
   SecondaryKWCapacity  NUMBER                          not null,
   SecondaryEnergySourceID NUMBER                          not null,
   constraint PK_APPLIANCEDUALFUEL primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceDualStageAirCond                             */
/*==============================================================*/
create table ApplianceDualStageAirCond  (
   ApplianceID          NUMBER                          not null,
   StageOneTonnageID    NUMBER,
   StageTwoTonnageID    NUMBER,
   TypeID               NUMBER,
   constraint PK_APPLIANCEDUALSTAGEAIRCOND primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceGenerator                                    */
/*==============================================================*/
create table ApplianceGenerator  (
   ApplianceID          NUMBER                          not null,
   TransferSwitchTypeID NUMBER                          not null,
   TransferSwitchMfgID  NUMBER                          not null,
   PeakKWCapacity       NUMBER                          not null,
   FuelCapGallons       NUMBER                          not null,
   StartDelaySeconds    NUMBER                          not null,
   constraint PK_APPLIANCEGENERATOR primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceGrainDryer                                   */
/*==============================================================*/
create table ApplianceGrainDryer  (
   ApplianceID          NUMBER                          not null,
   DryerTypeID          NUMBER                          not null,
   BinSizeID            NUMBER                          not null,
   BlowerEnergySourceID NUMBER                          not null,
   BlowerHorsePowerID   NUMBER                          not null,
   BlowerHeatSourceID   NUMBER                          not null,
   constraint PK_APPLIANCEGRAINDRYER primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceHeatPump                                     */
/*==============================================================*/
create table ApplianceHeatPump  (
   ApplianceID          NUMBER                          not null,
   PumpTypeID           NUMBER                          not null,
   StandbySourceID      NUMBER                          not null,
   SecondsDelayToRestart NUMBER                          not null,
   PumpSizeID           NUMBER                          not null,
   constraint PK_APPLIANCEHEATPUMP primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceIrrigation                                   */
/*==============================================================*/
create table ApplianceIrrigation  (
   ApplianceID          NUMBER                          not null,
   IrrigationTypeID     NUMBER                          not null,
   HorsePowerID         NUMBER                          not null,
   EnergySourceID       NUMBER                          not null,
   SoilTypeID           NUMBER                          not null,
   MeterLocationID      NUMBER                          not null,
   MeterVoltageID       NUMBER                          not null,
   constraint PK_APPLIANCEIRRIGATION primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceStorageHeat                                  */
/*==============================================================*/
create table ApplianceStorageHeat  (
   ApplianceID          NUMBER                          not null,
   StorageTypeID        NUMBER                          not null,
   PeakKWCapacity       NUMBER                          not null,
   HoursToRecharge      NUMBER                          not null,
   constraint PK_APPLIANCESTORAGEHEAT primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ApplianceWaterHeater                                  */
/*==============================================================*/
create table ApplianceWaterHeater  (
   ApplianceID          NUMBER                          not null,
   NumberOfGallonsID    NUMBER,
   EnergySourceID       NUMBER                          not null,
   NumberOfElements     NUMBER                          not null,
   constraint PK_APPLIANCEWATERHEATER primary key (ApplianceID)
);

/*==============================================================*/
/* Table: ArchiveDataAnalysis                                   */
/*==============================================================*/
create table ArchiveDataAnalysis  (
   AnalysisId           NUMBER                          not null,
   Attribute            VARCHAR2(60)                    not null,
   LastChangeId         NUMBER                          not null,
   RunDate              DATE                            not null,
   ExcludeBadPointQualities CHAR(1)                         not null,
   StartDate            DATE                            not null,
   StopDate             DATE                            not null,
   AnalysisStatus       VARCHAR2(60)                    not null,
   StatusId             VARCHAR2(60),
   IntervalPeriod       VARCHAR2(60)                    not null,
   constraint PK_ArcDataAnal primary key (AnalysisId)
);

/*==============================================================*/
/* Table: ArchiveDataAnalysisSlot                               */
/*==============================================================*/
create table ArchiveDataAnalysisSlot  (
   SlotId               NUMBER                          not null,
   AnalysisId           NUMBER                          not null,
   StartTime            DATE                            not null,
   constraint PK_ArcDataAnalSlot primary key (SlotId)
);

/*==============================================================*/
/* Table: ArchiveDataAnalysisSlotValue                          */
/*==============================================================*/
create table ArchiveDataAnalysisSlotValue  (
   DeviceId             NUMBER                          not null,
   SlotId               NUMBER                          not null,
   ChangeId             NUMBER,
   constraint PK_ArcDataAnalSlotValue primary key (DeviceId, SlotId)
);

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
   HolidayScheduleId    NUMBER                          not null,
   constraint PK_BASELINE primary key (BaselineID)
);

insert into baseline values (1, 'Default Baseline', 30, 75, 5, 'YNNNNNY', 0);

/*==============================================================*/
/* Table: BillingFileFormats                                    */
/*==============================================================*/
create table BillingFileFormats  (
   FormatID             NUMBER                          not null,
   FormatType           VARCHAR2(100)                   not null,
   SystemFormat         SMALLINT                        not null,
   constraint PK_BILLINGFILEFORMATS primary key (FormatID)
);

INSERT INTO BillingFileFormats VALUES(-11, 'MV_90 DATA Import',1);
INSERT INTO BillingFileFormats VALUES(-100,'INVALID',1);
INSERT INTO BillingFileFormats VALUES( 1,'CADP',1);
INSERT INTO BillingFileFormats VALUES( 2,'CADPXL2',1);
INSERT INTO BillingFileFormats VALUES( 3,'WLT-40',1);
INSERT INTO BillingFileFormats VALUES( 4,'CTI-CSV',1);
INSERT INTO BillingFileFormats VALUES( 5,'OPU',1);
INSERT INTO BillingFileFormats VALUES( 9, 'CTI2',1);
INSERT INTO BillingFileFormats VALUES( 15, 'NCDC-Handheld',1);
INSERT INTO BillingFileFormats VALUES( 16, 'NISC TOU (kVarH)',1);

INSERT INTO BillingFileFormats VALUES(-20, 'IVUE_BI_T65',1);
INSERT INTO BillingFileFormats VALUES( 21, 'SIMPLE_TOU',1);
INSERT INTO BillingFileFormats VALUES( 22, 'EXTENDED_TOU',1);
INSERT INTO BillingFileFormats VALUES(-23, 'Big Rivers Elec Coop',1);
INSERT INTO BillingFileFormats VALUES(-24, 'INCODE (Extended TOU)',1);
INSERT INTO BillingFileFormats VALUES(-25, 'Itron Register Readings Export',1);
INSERT INTO BillingFileFormats VALUES(-26, 'SIMPLE_TOU_DeviceName',1);
INSERT INTO BillingFileFormats VALUES( 31, 'STANDARD',1);
INSERT INTO BillingFileFormats VALUES(-32, 'NISC TOU (kVarH) Rates Only',1);
INSERT INTO BillingFileFormats VALUES( 33, 'NISC Interval Readings', 1);
INSERT INTO BillingFileFormats VALUES( -34, 'Curtailment Events - Itron', 1);

/*==============================================================*/
/* Index: Indx_BillFile_FormType_UNQ                            */
/*==============================================================*/
create unique index Indx_BillFile_FormType_UNQ on BillingFileFormats (
   FormatType ASC
);

/*==============================================================*/
/* Table: CALCBASE                                              */
/*==============================================================*/
create table CALCBASE  (
   POINTID              NUMBER                          not null,
   UPDATETYPE           VARCHAR2(16)                    not null,
   PERIODICRATE         NUMBER                          not null,
   QualityFlag          CHAR(1)                         not null,
   constraint PK_CALCBASE primary key (POINTID)
);

/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create index Indx_ClcBaseUpdTyp on CALCBASE (
   UPDATETYPE ASC
);

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
);

/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create index Indx_CalcCmpCmpType on CALCCOMPONENT (
   COMPONENTTYPE ASC
);

/*==============================================================*/
/* Table: CAPBANK                                               */
/*==============================================================*/
create table CAPBANK  (
   DEVICEID             NUMBER                          not null,
   OPERATIONALSTATE     VARCHAR2(16)                    not null,
   ControllerType       VARCHAR2(64)                    not null,
   CONTROLDEVICEID      NUMBER                          not null,
   CONTROLPOINTID       NUMBER                          not null,
   BANKSIZE             NUMBER                          not null,
   TypeOfSwitch         VARCHAR2(64)                    not null,
   SwitchManufacture    VARCHAR2(64)                    not null,
   MapLocationID        VARCHAR2(64)                    not null,
   RecloseDelay         NUMBER                          not null,
   MaxDailyOps          NUMBER                          not null,
   MaxOpDisable         CHAR(1)                         not null,
   constraint PK_CAPBANK primary key (DEVICEID)
);

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
   PotentialTransformer VARCHAR2(100)                   not null,
   MaintenanceReqPend   CHAR(1)                         not null,
   OtherComments        VARCHAR2(150)                   not null,
   OpTeamComments       VARCHAR2(150)                   not null,
   CBCBattInstallDate   DATE                            not null,
   constraint PK_CAPBANKADDITIONAL primary key (DeviceID)
);

/*==============================================================*/
/* Table: CAPCONTROLAREA                                        */
/*==============================================================*/
create table CAPCONTROLAREA  (
   AreaID               NUMBER                          not null,
   VoltReductionPointID NUMBER                          not null,
   constraint PK_CAPCONTROLAREA primary key (AreaID)
);

/*==============================================================*/
/* Table: CAPCONTROLCOMMENT                                     */
/*==============================================================*/
create table CAPCONTROLCOMMENT  (
   CommentID            INTEGER                         not null,
   PaoID                NUMBER                          not null,
   UserID               NUMBER,
   Action               VARCHAR2(50)                    not null,
   CommentTime          DATE                            not null,
   CapComment           VARCHAR2(500)                   not null,
   Altered              VARCHAR2(3)                     not null,
   constraint PK_CAPCONTROLCOMMENT primary key (CommentID)
);

/*==============================================================*/
/* Table: CAPCONTROLSPECIALAREA                                 */
/*==============================================================*/
create table CAPCONTROLSPECIALAREA  (
   AreaID               NUMBER                          not null,
   VoltReductionPointID NUMBER                          not null,
   constraint PK_CapControlSpecialArea primary key (AreaID)
);

/*==============================================================*/
/* Table: CAPCONTROLSUBSTATION                                  */
/*==============================================================*/
create table CAPCONTROLSUBSTATION  (
   SubstationID         NUMBER                          not null,
   VoltReductionPointId NUMBER                          not null,
   MapLocationId        VARCHAR2(64)                    not null,
   constraint PK_CAPCONTROLSUBSTATION primary key (SubstationID)
);

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
   usephasedata         CHAR(1)                         not null,
   phaseb               NUMBER                          not null,
   phasec               NUMBER                          not null,
   ControlFlag          CHAR(1)                         not null,
   VoltReductionPointId NUMBER                          not null,
   DisableBusPointId    NUMBER                          not null,
   constraint SYS_C0013476 primary key (SubstationBusID)
);

/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
   CurrentVarLoadPointID ASC
);

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
   actionId             NUMBER                          not null,
   CapBankStateInfo     VARCHAR2(20)                    not null,
   aVar                 FLOAT                           not null,
   bVar                 FLOAT                           not null,
   cVar                 FLOAT                           not null,
   StationID            NUMBER                          not null,
   AreaID               NUMBER                          not null,
   SpAreaID             NUMBER                          not null,
   RegulatorId          NUMBER                          not null,
   constraint PK_CCEventLog primary key (LogID)
);

/*==============================================================*/
/* Index: INDX_CCEventLog_PointId_ActId                         */
/*==============================================================*/
create index INDX_CCEventLog_PointId_ActId on CCEventLog (
   PointID ASC,
   actionId ASC
);

/*==============================================================*/
/* Index: INDX_CCEventLog_ActId                                 */
/*==============================================================*/
create index INDX_CCEventLog_ActId on CCEventLog (
   actionId ASC
);

/*==============================================================*/
/* Index: INDX_CCEventLog_PointId                               */
/*==============================================================*/
create index INDX_CCEventLog_PointId on CCEventLog (
   PointID ASC
);

/*==============================================================*/
/* Index: INDX_CCEventLog_FeedId                                */
/*==============================================================*/
create index INDX_CCEventLog_FeedId on CCEventLog (
   FeederID ASC
);

/*==============================================================*/
/* Index: INDX_CCEventLog_SubId                                 */
/*==============================================================*/
create index INDX_CCEventLog_SubId on CCEventLog (
   SubID ASC
);

/*==============================================================*/
/* Index: INDX_CCEventLog_Text                                  */
/*==============================================================*/
create index INDX_CCEventLog_Text on CCEventLog (
   Text ASC
);

/*==============================================================*/
/* Table: CCFeederBankList                                      */
/*==============================================================*/
create table CCFeederBankList  (
   FeederID             NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   ControlOrder         NUMBER(18,5)                    not null,
   CloseOrder           NUMBER(18,5)                    not null,
   TripOrder            NUMBER(18,5)                    not null,
   constraint PK_CCFEEDERBANKLIST primary key (FeederID, DeviceID)
);

/*==============================================================*/
/* Table: CCFeederSubAssignment                                 */
/*==============================================================*/
create table CCFeederSubAssignment  (
   SubStationBusID      NUMBER                          not null,
   FeederID             NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCFEEDERSUBASSIGNMENT primary key (SubStationBusID, FeederID)
);

/*==============================================================*/
/* Table: CCHOLIDAYSTRATEGYASSIGNMENT                           */
/*==============================================================*/
create table CCHOLIDAYSTRATEGYASSIGNMENT  (
   PAObjectId           NUMBER                          not null,
   HolidayScheduleId    NUMBER                          not null,
   StrategyId           NUMBER                          not null,
   constraint PK_CCHOLIDAYSTRATEGYASSIGNMENT primary key (PAObjectId)
);

/*==============================================================*/
/* Table: CCMonitorBankList                                     */
/*==============================================================*/
create table CCMonitorBankList  (
   BankId               NUMBER                          not null,
   PointId              NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   Scannable            CHAR(1)                         not null,
   NINAvg               NUMBER                          not null,
   UpperBandwidth       FLOAT                           not null,
   LowerBandwidth       FLOAT                           not null,
   Phase                CHAR(1),
   constraint PK_CCMONITORBANKLIST primary key (BankId, PointId)
);

/*==============================================================*/
/* Table: CCOperationLogCache                                   */
/*==============================================================*/
create table CCOperationLogCache  (
   OperationLogId       NUMBER                          not null,
   ConfirmationLogId    NUMBER,
   constraint PK_CCOPERATIONLOGCACHE primary key (OperationLogId)
);

/*==============================================================*/
/* Table: CCSEASONSTRATEGYASSIGNMENT                            */
/*==============================================================*/
create table CCSEASONSTRATEGYASSIGNMENT  (
   paobjectid           NUMBER                          not null,
   seasonscheduleid     NUMBER                          not null,
   seasonname           VARCHAR2(20)                    not null,
   strategyid           NUMBER                          not null,
   constraint PK_CCSEASONSTRATEGYASSIGNMENT primary key (paobjectid, seasonscheduleid, seasonname)
);

/*==============================================================*/
/* Table: CCSUBAREAASSIGNMENT                                   */
/*==============================================================*/
create table CCSUBAREAASSIGNMENT  (
   AreaID               NUMBER                          not null,
   SubstationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCSUBAREAASSIGNMENT primary key (AreaID, SubstationBusID)
);

/*==============================================================*/
/* Table: CCSUBSPECIALAREAASSIGNMENT                            */
/*==============================================================*/
create table CCSUBSPECIALAREAASSIGNMENT  (
   AreaID               NUMBER                          not null,
   SubstationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCSUBSPECIALAREAASSIGNMENT primary key (AreaID, SubstationBusID)
);

/*==============================================================*/
/* Table: CCSUBSTATIONSUBBUSLIST                                */
/*==============================================================*/
create table CCSUBSTATIONSUBBUSLIST  (
   SubStationID         NUMBER                          not null,
   SubStationBusID      NUMBER                          not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_CCSUBSTATIONSUBBUSLIST primary key (SubStationID, SubStationBusID)
);

/*==============================================================*/
/* Table: CCStrategyTargetSettings                              */
/*==============================================================*/
create table CCStrategyTargetSettings  (
   StrategyId           NUMBER                          not null,
   SettingName          VARCHAR2(255)                   not null,
   SettingValue         VARCHAR2(255)                   not null,
   SettingType          VARCHAR2(255)                   not null,
   constraint PK_CCStratTarSet primary key (StrategyId, SettingName, SettingType)
);

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
);

/*==============================================================*/
/* Table: CCURTACCTEVENTPARTICIPANT                             */
/*==============================================================*/
create table CCURTACCTEVENTPARTICIPANT  (
   CCurtAcctEventParticipantID NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CCurtAcctEventID     NUMBER                          not null,
   constraint PK_CCURTACCTEVENTPARTICIPANT primary key (CCurtAcctEventParticipantID)
);

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
);

/*==============================================================*/
/* Table: CCurtCEParticipant                                    */
/*==============================================================*/
create table CCurtCEParticipant  (
   CCurtCEParticipantID NUMBER                          not null,
   NotifAttribs         VARCHAR2(256)                   not null,
   CustomerID           NUMBER                          not null,
   CCurtCurtailmentEventID NUMBER                          not null,
   constraint PK_CCURTCEPARTICIPANT primary key (CCurtCEParticipantID)
);

/*==============================================================*/
/* Index: INDX_CCURTCEPART_EVTID_CUSTID                         */
/*==============================================================*/
create unique index INDX_CCURTCEPART_EVTID_CUSTID on CCurtCEParticipant (
   CustomerID ASC,
   CCurtCurtailmentEventID ASC
);

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
   constraint PK_CCURTCURTAILMENTEVENT primary key (CCurtCurtailmentEventID)
);

/*==============================================================*/
/* Table: CCurtEEParticipant                                    */
/*==============================================================*/
create table CCurtEEParticipant  (
   CCurtEEParticipantID NUMBER                          not null,
   NotifAttribs         VARCHAR2(255)                   not null,
   CustomerID           NUMBER                          not null,
   CCurtEconomicEventID NUMBER                          not null,
   constraint PK_CCURTEEPARTICIPANT primary key (CCurtEEParticipantID)
);

/*==============================================================*/
/* Table: CCurtEEParticipantSelection                           */
/*==============================================================*/
create table CCurtEEParticipantSelection  (
   CCurtEEParticipantSelectionID NUMBER                          not null,
   ConnectionAudit      VARCHAR2(2550)                  not null,
   SubmitTime           DATE                            not null,
   State                VARCHAR2(255)                   not null,
   CCurtEEParticipantID NUMBER                          not null,
   CCurtEEPricingID     NUMBER                          not null,
   constraint PK_CCURTEEPARTICIPANTSELECTION primary key (CCurtEEParticipantSelectionID)
);

/*==============================================================*/
/* Index: INDX_CCURTEEPARTSEL_CCURTEEPR                         */
/*==============================================================*/
create unique index INDX_CCURTEEPARTSEL_CCURTEEPR on CCurtEEParticipantSelection (
   CCurtEEParticipantID ASC,
   CCurtEEPricingID ASC
);

/*==============================================================*/
/* Table: CCurtEEParticipantWindow                              */
/*==============================================================*/
create table CCurtEEParticipantWindow  (
   CCurtEEParticipantWindowID NUMBER                          not null,
   EnergyToBuy          NUMBER(19,2)                    not null,
   CCurtEEPricingWindowID NUMBER                          not null,
   CCurtEEParticipantSelectionID NUMBER                          not null,
   constraint PK_CCURTEEPARTICIPANTWINDOW primary key (CCurtEEParticipantWindowID)
);

/*==============================================================*/
/* Index: INDX_CCRTEEPRTWIN_PWNID_PSID                          */
/*==============================================================*/
create unique index INDX_CCRTEEPRTWIN_PWNID_PSID on CCurtEEParticipantWindow (
   CCurtEEPricingWindowID ASC,
   CCurtEEParticipantSelectionID ASC
);

/*==============================================================*/
/* Table: CCurtEEPricing                                        */
/*==============================================================*/
create table CCurtEEPricing  (
   CCurtEEPricingID     NUMBER                          not null,
   Revision             NUMBER                          not null,
   CreationTime         DATE                            not null,
   CCurtEconomicEventID NUMBER                          not null,
   constraint PK_CCURTEEPRICING primary key (CCurtEEPricingID)
);

/*==============================================================*/
/* Index: INDX_CCURTECONSVTID_REV                               */
/*==============================================================*/
create unique index INDX_CCURTECONSVTID_REV on CCurtEEPricing (
   Revision ASC,
   CCurtEconomicEventID ASC
);

/*==============================================================*/
/* Table: CCurtEEPricingWindow                                  */
/*==============================================================*/
create table CCurtEEPricingWindow  (
   CCurtEEPricingWindowID NUMBER                          not null,
   EnergyPrice          NUMBER(19,6)                    not null,
   Offset               NUMBER                          not null,
   CCurtEEPricingID     NUMBER                          not null,
   constraint PK_CCURTEEPRICINGWINDOW primary key (CCurtEEPricingWindowID)
);

/*==============================================================*/
/* Index: INDX_CCURTEEPRWIN                                     */
/*==============================================================*/
create unique index INDX_CCURTEEPRWIN on CCurtEEPricingWindow (
   Offset ASC,
   CCurtEEPricingID ASC
);

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
);

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
);

/*==============================================================*/
/* Table: CCurtGroup                                            */
/*==============================================================*/
create table CCurtGroup  (
   CCurtGroupID         NUMBER                          not null,
   EnergyCompanyID      NUMBER                          not null,
   CCurtGroupName       VARCHAR2(255)                   not null,
   constraint PK_CCURTGROUP primary key (CCurtGroupID)
);

/*==============================================================*/
/* Index: INDX_CCURTGROUP_ECID_GRPNM                            */
/*==============================================================*/
create unique index INDX_CCURTGROUP_ECID_GRPNM on CCurtGroup (
   EnergyCompanyID ASC,
   CCurtGroupName ASC
);

/*==============================================================*/
/* Table: CCurtGroupCustomerNotif                               */
/*==============================================================*/
create table CCurtGroupCustomerNotif  (
   CCurtGroupCustomerNotifID NUMBER                          not null,
   Attribs              VARCHAR2(255)                   not null,
   CustomerID           NUMBER                          not null,
   CCurtGroupID         NUMBER                          not null,
   constraint PK_CCURTGROUPCUSTOMERNOTIF primary key (CCurtGroupCustomerNotifID)
);

/*==============================================================*/
/* Index: INDX_CCRTGRPCSTNOTIF_GID_CID                          */
/*==============================================================*/
create unique index INDX_CCRTGRPCSTNOTIF_GID_CID on CCurtGroupCustomerNotif (
   CustomerID ASC,
   CCurtGroupID ASC
);

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
);

/*==============================================================*/
/* Index: INDX_CCURTPGM_PRGNM_PRGTYPEID                         */
/*==============================================================*/
create index INDX_CCURTPGM_PRGNM_PRGTYPEID on CCurtProgram (
   CCurtProgramName ASC,
   CCurtProgramTypeID ASC
);

/*==============================================================*/
/* Table: CCurtProgramGroup                                     */
/*==============================================================*/
create table CCurtProgramGroup  (
   CCurtProgramGroupID  NUMBER                          not null,
   CCurtProgramID       NUMBER                          not null,
   CCurtGroupID         NUMBER                          not null,
   constraint PK_CCURTPROGRAMGROUP primary key (CCurtProgramGroupID)
);

/*==============================================================*/
/* Index: INDX_CCURTPRGGRP_GRPID_PRGID                          */
/*==============================================================*/
create unique index INDX_CCURTPRGGRP_GRPID_PRGID on CCurtProgramGroup (
   CCurtProgramID ASC,
   CCurtGroupID ASC
);

/*==============================================================*/
/* Table: CCurtProgramNotifGroup                                */
/*==============================================================*/
create table CCurtProgramNotifGroup  (
   CCurtProgramID       NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   constraint PK_CCURTPROGRAMNOTIFGROUP primary key (CCurtProgramID, NotificationGroupID)
);

/*==============================================================*/
/* Index: INDX_CCURPNG_PRGNM_PRGTYPEID                          */
/*==============================================================*/
create index INDX_CCURPNG_PRGNM_PRGTYPEID on CCurtProgramNotifGroup (
   NotificationGroupID ASC
);

/*==============================================================*/
/* Table: CCurtProgramParameter                                 */
/*==============================================================*/
create table CCurtProgramParameter  (
   CCurtProgramParameterID NUMBER                          not null,
   ParameterValue       VARCHAR2(255)                   not null,
   ParameterKey         VARCHAR2(255)                   not null,
   CCurtProgramID       NUMBER,
   constraint PK_CCURTPROGRAMPARAMETER primary key (CCurtProgramParameterID)
);

/*==============================================================*/
/* Index: INDX_CCRTPRGPRM_PGID_PMKEY                            */
/*==============================================================*/
create index INDX_CCRTPRGPRM_PGID_PMKEY on CCurtProgramParameter (
   ParameterKey ASC,
   CCurtProgramID ASC
);

/*==============================================================*/
/* Table: CCurtProgramType                                      */
/*==============================================================*/
create table CCurtProgramType  (
   CCurtProgramTypeID   NUMBER                          not null,
   EnergyCompanyID      NUMBER,
   CCurtProgramTypeStrategy VARCHAR2(255),
   CCurtProgramTypeName VARCHAR2(255),
   constraint PK_CCURTPROGRAMTYPE primary key (CCurtProgramTypeID)
);

/*==============================================================*/
/* Table: CICUSTOMERPOINTDATA                                   */
/*==============================================================*/
create table CICUSTOMERPOINTDATA  (
   CustomerID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   Type                 VARCHAR2(16)                    not null,
   OptionalLabel        VARCHAR2(32)                    not null,
   constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, Type)
);

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
);

/*==============================================================*/
/* Index: Indx_CICustBase_CompName                              */
/*==============================================================*/
create index Indx_CICustBase_CompName on CICustomerBase (
   CompanyName ASC
);

/*==============================================================*/
/* Table: COLUMNTYPE                                            */
/*==============================================================*/
create table COLUMNTYPE  (
   TYPENUM              NUMBER                          not null,
   NAME                 VARCHAR2(20)                    not null,
   constraint SYS_C0013414 primary key (TYPENUM)
);

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
);

/* __YUKON_VERSION__ */

/*==============================================================*/
/* Table: CalcPointBaseline                                     */
/*==============================================================*/
create table CalcPointBaseline  (
   PointID              NUMBER                          not null,
   BaselineID           NUMBER                          not null,
   constraint PK_CalcBsPt primary key (PointID)
);

/*==============================================================*/
/* Table: CallReportBase                                        */
/*==============================================================*/
create table CallReportBase  (
   CallID               NUMBER                          not null,
   CallNumber           VARCHAR2(20),
   CallTypeID           NUMBER,
   DateTaken            DATE,
   TakenBy              VARCHAR2(30),
   Description          VARCHAR2(300),
   AccountID            NUMBER,
   constraint PK_CALLREPORTBASE primary key (CallID)
);

/*==============================================================*/
/* Table: CapBankToZoneMapping                                  */
/*==============================================================*/
create table CapBankToZoneMapping  (
   DeviceId             NUMBER                          not null,
   ZoneId               NUMBER                          not null,
   GraphPositionOffset  FLOAT,
   Distance             FLOAT,
   constraint PK_CapBankZoneMap primary key (DeviceId)
);

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
   usephasedata         CHAR(1)                         not null,
   phaseb               NUMBER                          not null,
   phasec               NUMBER                          not null,
   ControlFlag          CHAR(1)                         not null,
   constraint PK_CAPCONTROLFEEDER primary key (FeederID)
);

/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create index Indx_CPCNFDVARPT on CapControlFeeder (
   CurrentVarLoadPointID ASC
);

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
   IntegrateFlag        CHAR(1)                         not null,
   IntegratePeriod      NUMBER                          not null,
   LikeDayFallBack      CHAR(1)                         not null,
   EndDaySettings       VARCHAR2(20)                    not null,
   constraint PK_CAPCONTROLSTRAT primary key (StrategyID)
);

/*==============================================================*/
/* Index: Indx_CapCntrlStrat_name_UNQ                           */
/*==============================================================*/
create unique index Indx_CapCntrlStrat_name_UNQ on CapControlStrategy (
   StrategyName ASC
);

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
);

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
);

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
);

/*==============================================================*/
/* Table: Command                                               */
/*==============================================================*/
create table Command  (
   CommandID            NUMBER                          not null,
   Command              VARCHAR2(256)                   not null,
   Label                VARCHAR2(256)                   not null,
   Category             VARCHAR2(32)                    not null,
   constraint PK_COMMAND primary key (CommandID)
);

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
insert into command values(-15, 'getstatus LP', 'Read LP Info', 'All LP Meters');
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
INSERT INTO Command VALUES(-30, 'control open', 'OPEN Cap Bank', 'All CBCs');
INSERT INTO Command VALUES(-31, 'control close', 'CLOSE Cap Bank', 'All CBCs');
INSERT INTO Command VALUES(-32, 'putstatus ovuv disable', 'Disable OVUV', 'Oneway CBCs');
INSERT INTO Command VALUES(-33, 'putstatus ovuv enable', 'Enable OVUV', 'Oneway CBCs');
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
INSERT INTO Command VALUES(-44, 'control cycle 50 period 30 count 4', 'Cycle 50% / 30min', 'Versacom Group');
INSERT INTO Command VALUES(-45, 'control cycle terminate', 'Terminate Cycle', 'Versacom Group');
INSERT INTO Command VALUES(-46, 'putconfig service out', 'Set to Out-Of-Service', 'Versacom Group');
INSERT INTO Command VALUES(-47, 'putconfig service in', 'Set to In-Service', 'Versacom Group');
INSERT INTO Command VALUES(-48, 'putconfig reset r1 r2 r3 cl', 'Reset All Counters', 'Versacom Group');
INSERT INTO Command VALUES(-49, 'putconfig led yyy', 'Configure LEDS (load, test, report)', 'Versacom Group');

/* TCU-BASE*/
/* insert into command values(-50, 'loop', '1 TCU Loop', 'All TCUs'); */
/* insert into command values(-51, 'loop 5', '5 TCU Loops', 'All TCUs'); */
/* Repeater-BASE */
INSERT INTO Command VALUES(-52, 'getconfig role 1', 'Read Roles', 'All Repeaters');
INSERT INTO Command VALUES(-53, 'putconfig emetcon install', 'Download All Roles', 'All Repeaters');
INSERT INTO Command VALUES(-54, 'loop locate', 'Locate Device', 'All Repeaters');
/* ION-BASE */
INSERT INTO Command VALUES(-55, 'scan general', 'Scan Status Points', 'All ION Meters');
INSERT INTO Command VALUES(-56, 'scan integrity', 'Scan Power Meter and Status', 'All ION Meters');
INSERT INTO Command VALUES(-57, 'getstatus eventlog', 'Retrieve Event Log', 'All ION Meters');
/* IEDAlpha-BASE */
INSERT INTO Command VALUES(-58, 'putconfig emetcon ied class 72 1', 'Set Current Period (Alpha)', 'DEVICE');
INSERT INTO Command VALUES(-59, 'putconfig emetcon ied class 72 2', 'Set Previous Period (Alpha)', 'DEVICE');
/* EDGEKV-BASE */
INSERT INTO Command VALUES(-60, 'getvalue ied demand', 'Read IED Volts', 'DEVICE');
INSERT INTO Command VALUES(-61, 'putconfig emetcon ied class 0 0', 'Set Current Period (GEKV)', 'DEVICE');
INSERT INTO Command VALUES(-62, 'putconfig emetcon ied class 0 1', 'Set Previous Period (GEKV)', 'DEVICE');
/* ExpresscomSerial */
INSERT INTO Command VALUES(-63, 'putconfig xcom raw 0x30 0x00 0x02 0x58', 'Cold Load Pickup (load, time x 0.5sec)', 'ExpresscomSerial');
INSERT INTO Command VALUES(-64, 'putstatus xcom prop inc', 'Increment Prop Counter', 'ExpresscomSerial');
INSERT INTO Command VALUES(-65, 'putconfig xcom raw 0x05 0x00', 'Turn Off Test Light', 'ExpresscomSerial');
INSERT INTO Command VALUES(-66, 'putconfig xcom main 0x01 0x40', 'Clear Prop Counter', 'ExpresscomSerial');
INSERT INTO Command VALUES(-67, 'putconfig xcom main 0x01 0x80', 'Clear Comm Loss Counter', 'ExpresscomSerial');
INSERT INTO Command VALUES(-68, 'putconfig xcom service out temp offhours 24', 'Temp Out-Of-Service (hours)', 'ExpresscomSerial');
INSERT INTO Command VALUES(-69, 'putconfig xcom temp service enable', 'Temp Out-Of-Service Cancel', 'ExpresscomSerial');

/* LCRSerial */
INSERT INTO Command VALUES(-70, 'putconfig cycle r1 50', 'Install Cycle Count', 'LCRSerial');

/* VersacomSerial */
INSERT INTO Command VALUES(-71, 'putconfig template ''?LoadGroup''', 'Install Versacom Addressing', 'VersacomSerial');
INSERT INTO Command VALUES(-72, 'putconfig cold_load r1 10', 'Install Versacom Cold Load (relay, minutes)', 'VersacomSerial');
INSERT INTO Command VALUES(-73, 'putconfig raw 0x3a ff', 'Emetcon Cold Load (ON -ff / OFF -00', 'VersacomSerial');
INSERT INTO Command VALUES(-74, 'putconfig raw 36 0', 'Set LCR 3000 to Emetcon Mode', 'VersacomSerial');
INSERT INTO Command VALUES(-75, 'putconfig raw 36 1', 'Set LCR 3000 to Versacom Mode', 'VersacomSerial');

/* MCT410IL */
INSERT INTO Command VALUES(-81, 'getvalue demand', 'Read KW Demand, Current Voltage, Blink Count', 'All MCT-4xx Series');
INSERT INTO Command VALUES(-82, 'getvalue voltage', 'Read Min / Max Voltage', 'MCT-410IL');
INSERT INTO Command VALUES(-83, 'putconfig emetcon timesync', 'Write Time/Date to Meter', 'MCT-410IL');
INSERT INTO Command VALUES(-84, 'getvalue peak', 'Read Current Peak', 'MCT-410IL');

/* All LCRs */
INSERT INTO Command VALUES(-85, 'control shed 5m relay 1', 'Shed 5-min Relay 1', 'VersacomSerial');
INSERT INTO Command VALUES(-86, 'control shed 5m relay 2', 'Shed 5-min Relay 2', 'VersacomSerial');
INSERT INTO Command VALUES(-87, 'control shed 5m relay 3', 'Shed 5-min Relay 3', 'VersacomSerial');
INSERT INTO Command VALUES(-88, 'control restore relay 1', 'Restore Relay 1', 'VersacomSerial');
INSERT INTO Command VALUES(-89, 'control restore relay 2', 'Restore Relay 2', 'VersacomSerial');
INSERT INTO Command VALUES(-90, 'control restore relay 3', 'Restore Relay 3', 'VersacomSerial');
INSERT INTO Command VALUES(-91, 'control cycle 50 period 30 relay 1', 'Cycle 50 Period-30 Relay 1', 'VersacomSerial');
INSERT INTO Command VALUES(-92, 'control cycle terminate relay 1', 'Terminate Cycle Relay 1', 'VersacomSerial');
INSERT INTO Command VALUES(-93, 'control cycle terminate relay 2', 'Terminate Cycle Relay 2', 'VersacomSerial');
INSERT INTO Command VALUES(-94, 'control cycle terminate relay 3', 'Terminate Cycle Relay 3', 'VersacomSerial');

INSERT INTO Command VALUES(-95, 'putconfig service out', 'Set to Out-of-Service', 'VersacomSerial');
INSERT INTO Command VALUES(-96, 'putconfig service in', 'Set to In-Service', 'VersacomSerial');
INSERT INTO Command VALUES(-97, 'putconfig led yyy', 'Configure LEDS (load, test, report)', 'VersacomSerial'); 
INSERT INTO Command VALUES(-98, 'putconfig emetcon disconnect', 'Upload Disconnect Address', 'MCT-410IL');
INSERT INTO Command VALUES(-99, 'getconfig disconnect', 'Read Disconnect Address/Status', 'MCT-410IL');
INSERT INTO Command VALUES(-100, 'scan general', 'General Meter Scan', 'SENTINEL');
INSERT INTO Command VALUES(-101, 'scan general frozen', 'General Meter Scan Frozen', 'SENTINEL');
INSERT INTO Command VALUES(-102, 'scan general update', 'General Meter Scan and DB Update', 'SENTINEL');
INSERT INTO Command VALUES(-103, 'scan general frozen update', 'General Meter Scan Frozen and DB Update', 'SENTINEL');
INSERT INTO Command VALUES(-104, 'putvalue reset', 'Reset Demand', 'SENTINEL');

INSERT INTO Command VALUES(-105, 'getvalue lp channel ?''Channel (1 or 4)'' ?''MM/DD/YYYY HH:mm''', 'Read block of data (six intervals) from start date/time specified', 'MCT-410IL');
INSERT INTO Command VALUES(-106, 'getvalue outage ?''Outage Log (1 - 6)''', 'Read two outages per read.  Specify 1(1&2), 3(3&4), 5(5&6)', 'MCT-410IL');
INSERT INTO Command VALUES(-107, 'getvalue peak frozen', 'Read frozen demand - kW and kWh', 'MCT-410IL');
INSERT INTO Command VALUES(-108, 'getvalue voltage frozen', 'Read frozen voltage - min, max', 'MCT-410IL');
INSERT INTO Command VALUES(-109, 'putvalue powerfail reset', 'Reset blink counter', 'MCT-410IL');
INSERT INTO Command VALUES(-111, 'getconfig intervals', 'Read rates for LI, LP, Volt LI, and Volt Profile Demand', 'All MCT-4xx Series');
INSERT INTO Command VALUES(-112, 'putconfig emetcon intervals', 'Write rate intervals from database to MCT', 'MCT-410IL');
INSERT INTO Command VALUES(-113, 'putstatus emetcon freeze ?''(one or two)''', 'Reset and Write current peak demand - kW and kWh to frozen register', 'MCT-410IL');
INSERT INTO Command VALUES(-114, 'putstatus emetcon freeze voltage ?''(one or two)''', 'Reset and Write current min/max voltage to frozen register', 'MCT-410IL');

INSERT INTO Command VALUES(-115, 'getvalue ied current kwha', 'Read Current Rate A kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-116, 'getvalue ied current kwhb', 'Read Current Rate B kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-117, 'getvalue ied current kwhc', 'Read Current Rate C kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-118, 'getvalue ied current kwhd', 'Read Current Rate D kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-119, 'getvalue ied frozen kwha', 'Read Frozen Rate A kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-120, 'getvalue ied frozen kwhb', 'Read Frozen Rate B kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-121, 'getvalue ied frozen kwhc', 'Read Frozen Rate C kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-122, 'getvalue ied frozen kwhd', 'Read Frozen Rate D kWh/Peak kW', 'MCT-470');
INSERT INTO Command VALUES(-124, 'putconfig raw 38 0', 'Install Emetcon Gold 1', 'VersacomSerial');

INSERT INTO Command VALUES(-125, 'putconfig raw 38 1', 'Install Emetcon Gold 2', 'VersacomSerial');
INSERT INTO Command VALUES(-126, 'putconfig raw 38 2', 'Install Emetcon Gold 3', 'VersacomSerial');
INSERT INTO Command VALUES(-127, 'putconfig raw 39 0', 'Install Emetcon Silver 1', 'VersacomSerial');
INSERT INTO Command VALUES(-128, 'putconfig raw 39 1', 'Install Emetcon Silver 2', 'VersacomSerial');
INSERT INTO Command VALUES(-129, 'putconfig raw 39 2', 'Install Emetcon Silver 3', 'VersacomSerial');
INSERT INTO Command VALUES(-130, 'getvalue lp channel ?''Channel #'' ?''Enter Start Date: xx/xx/xxxx'' ?''Enter End Date: xx/xx/xxxx''', 'Read LP Channel Data', 'ALL MCT-4xx Series');
INSERT INTO Command VALUES(-131, 'getvalue lp status', 'Read LP Channel Data Status', 'ALL MCT-4xx Series');
INSERT INTO Command VALUES(-132, 'getvalue lp cancel', 'Read LP Channel Data Cancel', 'ALL MCT-4xx Series');
INSERT INTO Command VALUES(-133, 'putconfig xcom utility usage ?''Channel'':?''Value''', 'Thermostat Register Download', 'ExpresscomSerial');
INSERT INTO Command VALUES(-134, 'putconfig xcom data ''?''Text Message'''' port ?''Port (0 is default)'' deletable msgpriority 7 timeout 30 hour clear', 'Thermostat Text Message', 'ExpresscomSerial');

INSERT INTO Command VALUES(-135, 'control xcom backlight cycles 20 duty 30 bperiod 10', 'Thermostat Display Blink', 'ExpresscomSerial');
INSERT INTO Command VALUES(-136, 'putconfig emetcon timezone ?''Enter Timezone (et|ct|mt|pt OR #offset)''', 'Write Timezone to Meter', 'MCT-430A'); 
INSERT INTO Command VALUES(-137, 'putconfig emetcon outage ?''Enter number of cycles before outage is recorded''', 'Write Threshold (number of cycles before outage recorded) to Meter', 'MCT-410IL');
INSERT INTO Command VALUES(-138, 'getvalue peak channel 2', 'Read Peak (Channel 2)', 'MCT-410IL');
INSERT INTO Command VALUES(-139, 'getvalue peak channel 3', 'Read Peak (Channel 3)', 'MCT-410IL'); 
INSERT INTO Command VALUES(-140, 'getstatus freeze', 'Read the freeze timestamp, counter, and next freeze expected for demand and voltage.', 'MCT-410IL'); 
INSERT INTO Command VALUES(-141, 'putconfig emetcon freeze day ?''Day of month (0-31)''', 'Set meter to freeze on X day of month (use 0 for disable).', 'MCT-410IL'); 
INSERT INTO Command VALUES(-142, 'getconfig freeze', 'Read freeze config from meter and enable scheduled freeze processing in Yukon.', 'MCT-410IL'); 
INSERT INTO Command VALUES(-143, 'putconfig xcom extended tier ?''tier'' rate ?''rate'' cmd ?''cmd'' display 3 timeout 600 delay 5432', 'Thermostat Extended Tier Message', 'ExpresscomSerial');
INSERT INTO Command VALUES(-144, 'putconfig emetcon channel 1 ied input 1', 'Configure 430 kW LP Collection', 'MCT-430A'); 

/* Versacom */ 
INSERT INTO Command VALUES(-145, 'putconfig vcom lcrmode ?''Enter e|v (for Emetcon or Versacom)''', 'Set LCR3100 Versacom mode', 'VersacomSerial'); 
INSERT INTO Command VALUES(-146, 'putconfig vcom silver ?''Enter a value 1-60''', 'Set LCR3100 Versacom Silver Addressing', 'VersacomSerial'); 
INSERT INTO Command VALUES(-147, 'putconfig vcom gold ?''Enter a value 1-4''', 'Set LCR3100 Versacom Gold Addressing', 'VersacomSerial'); 

/* ExpressCom */ 
INSERT INTO Command VALUES(-148, 'putconfig xcom lcrmode ?''Enter Ex|Em|V|G (For example: ExEmVG or ExV)''', 'Set LCR3100 Expresscom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-149, 'putconfig xcom silver ?''Enter a value 1-60''', 'Set LCR3100 Expresscom Silver Addressing', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-150, 'putconfig xcom gold ?''Enter a value 1-4''', 'Set LCR3100 Expresscom Gold Addressing', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-151, 'putconfig xcom lcrmode Ex', 'Set LCR3100 Expresscom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-152, 'putconfig xcom lcrmode V', 'Set LCR3100 Versacom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-153, 'putconfig xcom lcrmode Em', 'Set LCR3100 Emetcon mode', 'ExpresscomSerial'); 

/* TOU */
INSERT INTO Command VALUES(-154, 'getvalue tou kwh', 'Read Current TOU kWh for rates A, B, C, D.', 'MCT-410IL');
INSERT INTO Command VALUES(-155, 'getvalue tou kwh frozen', 'Read Frozen TOU kWh for rates A, B, C, D.', 'MCT-410IL');
INSERT INTO Command VALUES(-156, 'getvalue daily read detail channel 1 ?''MM/DD/YYYY''', 'Read Daily kWh, Peak Demand, Min/Max Voltage (Channel 1).', 'MCT-410IL');
INSERT INTO Command VALUES(-157, 'getvalue daily read channel 1 ?''MM/DD/YYYY'' ?''MM/DD/YYYY''', 'Read Daily kWh for date range (Channel 1).', 'MCT-410IL');

INSERT INTO Command VALUES(-158, 'getvalue interval last', 'Last Interval kW', 'All Two Way LCR');
INSERT INTO Command VALUES(-159, 'getvalue runtime load 1 previous ?''12 , 24, or 36''', 'Runtime Load 1', 'All Two Way LCR');
INSERT INTO Command VALUES(-160, 'getvalue runtime load 2 previous ?''12 , 24, or 36''', 'Runtime Load 2', 'All Two Way LCR');
INSERT INTO Command VALUES(-161, 'getvalue runtime load 3 previous ?''12 , 24, or 36''', 'Runtime Load 3', 'All Two Way LCR');
INSERT INTO Command VALUES(-162, 'getvalue runtime load 4 previous ?''12 , 24, or 36''', 'Runtime Load 4', 'All Two Way LCR');
INSERT INTO Command VALUES(-163, 'getvalue shedtime relay 1 previous ?''12 , 24, or 36''', 'Relay 1 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-164, 'getvalue shedtime relay 2 previous ?''12 , 24, or 36''', 'Relay 2 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-165, 'getvalue shedtime relay 3 previous ?''12 , 24, or 36''', 'Relay 3 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-166, 'getvalue shedtime relay 4 previous ?''12 , 24, or 36''', 'Relay 4 Shed Time', 'All Two Way LCR');
INSERT INTO Command VALUES(-167, 'getvalue propcount', 'Prop Count', 'All Two Way LCR');

INSERT INTO Command VALUES(-168, 'putvalue emetcon ied reset a3', 'Reset IED Demand.', 'MCT-430A3'); 
INSERT INTO Command VALUES(-169, 'getvalue daily read ?''MM/DD/YYYY''', 'Read Daily kWh, Peak kW, and Outages (Channel 1 only, up to 8 days ago)', 'MCT-410IL');
INSERT INTO Command VALUES(-170, 'getvalue daily read detail channel ?''Channel 2|3'' ?''MM/DD/YYYY''', 'Read Daily kWh, Peak Demand, Outages (Channel 2 or 3).', 'MCT-410IL');

INSERT INTO Command VALUES(-171, 'putvalue ovuv analog 1 0', 'Disable OVUV', 'Twoway CBCs');
INSERT INTO Command VALUES(-172, 'putvalue ovuv analog 1 1', 'Enable OVUV', 'Twoway CBCs');
INSERT INTO Command VALUES(-173, 'putvalue analog ?''Enter point offset'' ?''Enter value''', 'Write Value', 'Twoway CBCs'); 

INSERT INTO Command VALUES(-174, 'control xcom shed 5m relay 1', 'Shed 5-min Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-175, 'control xcom shed 5m relay 2', 'Shed 5-min Relay 2', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-176, 'control xcom shed 5m relay 3', 'Shed 5-min Relay 3', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-177, 'control xcom restore relay 1', 'Restore Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-178, 'control xcom restore relay 2', 'Restore Relay 2', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-179, 'control xcom restore relay 3', 'Restore Relay 3', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-180, 'control xcom cycle 50 period 30 relay 1', 'Cycle 50 Period-30 Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-181, 'control xcom cycle terminate relay 1', 'Terminate Cycle Relay 1', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-182, 'control xcom cycle terminate relay 2', 'Terminate Cycle Relay 2', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-183, 'control xcom cycle terminate relay 3', 'Terminate Cycle Relay 3', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-184, 'putconfig xcom service out', 'Set to Out-of-Service', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-185, 'putconfig xcom service in', 'Set to In-Service', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-186, 'putconfig xcom led yyy', 'Configure LEDS (load, test, report)', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-187, 'getconfig ied dnp address', 'Read DNP address configuration', 'MCT-470');
INSERT INTO Command VALUES(-188, 'putconfig emetcon ied dnp address master ?''Master address'' outstation ?''Outstation address''', 'Write DNP address configuration', 'MCT-470');

/*==============================================================*/
/* Table: CommandGroup                                          */
/*==============================================================*/
create table CommandGroup  (
   CommandGroupID       NUMBER                          not null,
   CommandGroupName     VARCHAR2(60)                    not null,
   constraint PK_COMMANDGROUP primary key (CommandGroupID)
);

insert into CommandGroup values (-1, 'Default Commands');

/*==============================================================*/
/* Index: AK_KEY_CmdGrp_Name                                    */
/*==============================================================*/
create unique index AK_KEY_CmdGrp_Name on CommandGroup (
   CommandGroupName ASC
);

/*==============================================================*/
/* Table: CommandRequestExec                                    */
/*==============================================================*/
create table CommandRequestExec  (
   CommandRequestExecId NUMBER                          not null,
   StartTime            DATE                            not null,
   StopTime             DATE,
   RequestCount         NUMBER,
   CommandRequestExecType VARCHAR2(255)                   not null,
   UserName             VARCHAR2(64),
   CommandRequestType   VARCHAR2(100)                   not null,
   CommandRequestExecContextId NUMBER                          not null,
   ExecutionStatus      VARCHAR2(100)                   not null,
   constraint PK_CommandRequestExec primary key (CommandRequestExecId)
);

/*==============================================================*/
/* Index: Indx_CmdReqExec_ContId                                */
/*==============================================================*/
create index Indx_CmdReqExec_ContId on CommandRequestExec (
   CommandRequestExecContextId ASC
);

/*==============================================================*/
/* Table: CommandRequestExecResult                              */
/*==============================================================*/
create table CommandRequestExecResult  (
   CommandRequestExecResultId NUMBER                          not null,
   CommandRequestExecId NUMBER,
   Command              VARCHAR2(255)                   not null,
   ErrorCode            NUMBER,
   CompleteTime         DATE,
   DeviceId             NUMBER,
   RouteId              NUMBER,
   constraint PK_CommandRequestExecResult primary key (CommandRequestExecResultId)
);

/*==============================================================*/
/* Index: Indx_CmdReqExecRes_ExecId_ErrC                        */
/*==============================================================*/
create index Indx_CmdReqExecRes_ExecId_ErrC on CommandRequestExecResult (
   CommandRequestExecId ASC,
   ErrorCode ASC
);

/*==============================================================*/
/* Table: CommandSchedule                                       */
/*==============================================================*/
create table CommandSchedule  (
   CommandScheduleId    NUMBER                          not null,
   StartTimeCronString  VARCHAR2(64)                    not null,
   RunPeriod            VARCHAR2(32)                    not null,
   DelayPeriod          VARCHAR2(32)                    not null,
   Enabled              CHAR(1)                         not null,
   EnergyCompanyId      NUMBER                          not null,
   constraint PK_CommandSchedule primary key (CommandScheduleId)
);

/*==============================================================*/
/* Table: Contact                                               */
/*==============================================================*/
create table Contact  (
   ContactID            NUMBER                          not null,
   ContFirstName        VARCHAR2(120)                   not null,
   ContLastName         VARCHAR2(120)                   not null,
   LogInID              NUMBER                          not null,
   AddressID            NUMBER                          not null,
   constraint PK_CONTACT primary key (ContactID)
);

insert into contact values ( 0, '(none)', '(none)', -9999, 0 );

/*==============================================================*/
/* Index: Indx_ContLstName                                      */
/*==============================================================*/
create index Indx_ContLstName on Contact (
   ContLastName ASC
);

/*==============================================================*/
/* Index: INDX_CONTID_LNAME                                     */
/*==============================================================*/
create index INDX_CONTID_LNAME on Contact (
   ContactID ASC,
   ContLastName ASC
);

/*==============================================================*/
/* Index: INDX_CONTID_LNAME_FNAME                               */
/*==============================================================*/
create index INDX_CONTID_LNAME_FNAME on Contact (
   ContactID ASC,
   ContFirstName ASC,
   ContLastName ASC
);

/*==============================================================*/
/* Table: ContactNotifGroupMap                                  */
/*==============================================================*/
create table ContactNotifGroupMap  (
   ContactID            NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              CHAR(16)                        not null,
   constraint PK_CONTACTNOTIFGROUPMAP primary key (ContactID, NotificationGroupID)
);

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
);

insert into ContactNotification values( 0, 0, 0, 'N', '(none)', 0 );

/*==============================================================*/
/* Index: Indx_CntNotif_CntId                                   */
/*==============================================================*/
create index Indx_CntNotif_CntId on ContactNotification (
   ContactID ASC
);

/*==============================================================*/
/* Index: Indx_ContNot_Not                                      */
/*==============================================================*/
create index Indx_ContNot_Not on ContactNotification (
   Notification ASC
);

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
);

INSERT INTO Customer VALUES ( -1, 0, 0, '(none)', '(none)', 0, '(none)', 'F');

/*==============================================================*/
/* Index: INDX_CUSTID_PCONTID                                   */
/*==============================================================*/
create index INDX_CUSTID_PCONTID on Customer (
   CustomerID ASC,
   PrimaryContactID ASC
);

/*==============================================================*/
/* Index: Indx_Cstmr_PcId                                       */
/*==============================================================*/
create index Indx_Cstmr_PcId on Customer (
   PrimaryContactID ASC
);

/*==============================================================*/
/* Index: Indx_Cust_AltTrackNum                                 */
/*==============================================================*/
create index Indx_Cust_AltTrackNum on Customer (
   AltTrackNum ASC
);

/*==============================================================*/
/* Table: CustomerAccount                                       */
/*==============================================================*/
create table CustomerAccount  (
   AccountID            NUMBER                          not null,
   AccountSiteID        NUMBER,
   AccountNumber        VARCHAR2(40),
   CustomerID           NUMBER                          not null,
   BillingAddressID     NUMBER,
   AccountNotes         VARCHAR2(200),
   constraint PK_CUSTOMERACCOUNT primary key (AccountID)
);

INSERT INTO CustomerAccount VALUES (0,0,'(none)',-1,0,'(none)');

/*==============================================================*/
/* Index: CstAccCstPro_FK                                       */
/*==============================================================*/
create index CstAccCstPro_FK on CustomerAccount (
   AccountSiteID ASC
);

/*==============================================================*/
/* Index: Indx_CstAcc_CstId                                     */
/*==============================================================*/
create index Indx_CstAcc_CstId on CustomerAccount (
   CustomerID ASC
);

/*==============================================================*/
/* Index: Indx_acctid_custid                                    */
/*==============================================================*/
create index Indx_acctid_custid on CustomerAccount (
   AccountID ASC,
   CustomerID ASC
);

/*==============================================================*/
/* Index: INDX_CustAcct_AcctNum                                 */
/*==============================================================*/
create index INDX_CustAcct_AcctNum on CustomerAccount (
   AccountNumber ASC
);

/*==============================================================*/
/* Table: CustomerAdditionalContact                             */
/*==============================================================*/
create table CustomerAdditionalContact  (
   CustomerID           NUMBER                          not null,
   ContactID            NUMBER                          not null,
   Ordering             NUMBER                          not null,
   constraint PK_CUSTOMERADDITIONALCONTACT primary key (ContactID, CustomerID)
);

/*==============================================================*/
/* Table: CustomerBaseLinePoint                                 */
/*==============================================================*/
create table CustomerBaseLinePoint  (
   CustomerID           NUMBER                          not null,
   PointID              NUMBER                          not null,
   constraint PK_CUSTOMERBASELINEPOINT primary key (CustomerID, PointID)
);

/*==============================================================*/
/* Table: CustomerLoginSerialGroup                              */
/*==============================================================*/
create table CustomerLoginSerialGroup  (
   LoginID              NUMBER                          not null,
   LMGroupID            NUMBER                          not null,
   constraint PK_CUSTOMERLOGINSERIALGROUP primary key (LoginID, LMGroupID)
);

/*==============================================================*/
/* Table: CustomerNotifGroupMap                                 */
/*==============================================================*/
create table CustomerNotifGroupMap  (
   CustomerID           NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null,
   Attribs              CHAR(16)                        not null,
   constraint PK_CUSTOMERNOTIFGROUPMAP primary key (CustomerID, NotificationGroupID)
);

/*==============================================================*/
/* Table: CustomerResidence                                     */
/*==============================================================*/
create table CustomerResidence  (
   AccountSiteID        NUMBER                          not null,
   ResidenceTypeID      NUMBER                          not null,
   ConstructionMaterialID NUMBER                          not null,
   DecadeBuiltID        NUMBER                          not null,
   SquareFeetID         NUMBER                          not null,
   InsulationDepthID    NUMBER                          not null,
   GeneralConditionID   NUMBER                          not null,
   MainCoolingSystemID  NUMBER                          not null,
   MainHeatingSystemID  NUMBER                          not null,
   NumberOfOccupantsID  NUMBER                          not null,
   OwnershipTypeID      NUMBER                          not null,
   MainFuelTypeID       NUMBER                          not null,
   Notes                VARCHAR2(300),
   constraint PK_CUSTOMERRESIDENCE primary key (AccountSiteID)
);

/*==============================================================*/
/* Table: DEVICE                                                */
/*==============================================================*/
create table DEVICE  (
   DEVICEID             NUMBER                          not null,
   ALARMINHIBIT         VARCHAR2(1)                     not null,
   CONTROLINHIBIT       VARCHAR2(1)                     not null,
   constraint PK_DEV_DEVICEID2 primary key (DEVICEID)
);

INSERT into device values (0, 'N', 'N');

/*==============================================================*/
/* Table: DEVICECARRIERSETTINGS                                 */
/*==============================================================*/
create table DEVICECARRIERSETTINGS  (
   DEVICEID             NUMBER                          not null,
   ADDRESS              NUMBER                          not null,
   constraint PK_DEVICECARRIERSETTINGS primary key (DEVICEID)
);

/*==============================================================*/
/* Index: Indx_DevCarSet_Address                                */
/*==============================================================*/
create index Indx_DevCarSet_Address on DEVICECARRIERSETTINGS (
   ADDRESS ASC
);

/*==============================================================*/
/* Table: DEVICECONFIGURATION                                   */
/*==============================================================*/
create table DEVICECONFIGURATION  (
   DeviceConfigurationID NUMBER                          not null,
   Name                 VARCHAR2(60)                    not null,
   Type                 VARCHAR2(30)                    not null,
   constraint PK_DEVICECONFIGURATION primary key (DeviceConfigurationID)
);

/*==============================================================*/
/* Table: DEVICECONFIGURATIONDEVICEMAP                          */
/*==============================================================*/
create table DEVICECONFIGURATIONDEVICEMAP  (
   DeviceID             NUMBER                          not null,
   DeviceConfigurationId NUMBER                          not null,
   constraint PK_DEVICECONFIGURATIONDEVICEMA primary key (DeviceID)
);

/*==============================================================*/
/* Table: DEVICECONFIGURATIONITEM                               */
/*==============================================================*/
create table DEVICECONFIGURATIONITEM  (
   DeviceConfigurationItemId NUMBER                          not null,
   DeviceConfigurationId NUMBER                          not null,
   FieldName            VARCHAR2(60)                    not null,
   Value                VARCHAR2(60)                    not null,
   constraint PK_DEVICECONFIGURATIONITEM primary key (DeviceConfigurationItemId)
);

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
);

/*==============================================================*/
/* Table: DEVICEEVENT                                           */
/*==============================================================*/
create table DEVICEEVENT  (
   DeviceEventID        NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   TimeStamp            DATE                            not null,
   DeviceEventComment   VARCHAR2(50)                    not null,
   constraint PK_DEVICEEVENT primary key (DeviceEventID)
);

/*==============================================================*/
/* Table: DEVICEGROUP                                           */
/*==============================================================*/
create table DEVICEGROUP  (
   DeviceGroupId        NUMBER(18,0)                    not null,
   GroupName            VARCHAR2(255)                   not null,
   ParentDeviceGroupId  NUMBER(18,0),
   Permission           NVARCHAR2(50)                   not null,
   Type                 VARCHAR2(255)                   not null,
   constraint PK_DEVICEGROUP primary key (DeviceGroupId)
);

INSERT INTO DeviceGroup VALUES (0,' ',null,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (1,'Meters',0,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (2,'Billing',1,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (3,'Collection',1,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (4,'Alternate',1,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (8,'Flags',1,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (9,'Inventory',8,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (10,'DisconnectedStatus',8,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (11,'UsageMonitoring',8,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (12,'System',0,'NOEDIT_NOMOD','STATIC');
INSERT INTO DeviceGroup VALUES (13,'Routes',12,'NOEDIT_NOMOD','ROUTE');
INSERT INTO DeviceGroup VALUES (14,'Device Types',12,'NOEDIT_NOMOD','DEVICETYPE'); 
INSERT INTO DeviceGroup VALUES (15,'Meters',12,'NOEDIT_NOMOD','STATIC'); 
INSERT INTO DeviceGroup VALUES (16,'Scanning',15,'NOEDIT_NOMOD','STATIC'); 
INSERT INTO DeviceGroup VALUES (17,'Load Profile',16,'NOEDIT_NOMOD','METERS_SCANNING_LOAD_PROFILE'); 
INSERT INTO DeviceGroup VALUES (18,'Voltage Profile',16,'NOEDIT_NOMOD','METERS_SCANNING_VOLTAGE_PROFILE'); 
INSERT INTO DeviceGroup VALUES (19,'Integrity',16,'NOEDIT_NOMOD','METERS_SCANNING_INTEGRITY'); 
INSERT INTO DeviceGroup VALUES (20,'Accumulator',16,'NOEDIT_NOMOD','METERS_SCANNING_ACCUMULATOR'); 
INSERT INTO DeviceGroup VALUES (21,'Temporary',12,'HIDDEN','STATIC'); 
INSERT INTO DeviceGroup VALUES (22,'Disabled',15,'NOEDIT_NOMOD','METERS_DISABLED');
INSERT INTO DeviceGroup VALUES (23,'Disconnect',15,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (24,'Collars',23,'NOEDIT_MOD','METERS_DISCONNECT_COLLAR');
INSERT INTO DeviceGroup VALUES (25,'CIS Substation',1,'NOEDIT_MOD','STATIC');
INSERT INTO DeviceGroup VALUES (26,'Substations',12,'NOEDIT_NOMOD','SUBSTATION_TO_ROUTE');
INSERT INTO DeviceGroup VALUES (27,'Attributes',12,'NOEDIT_NOMOD','STATIC');
INSERT INTO DeviceGroup VALUES (28,'Supported',27,'NOEDIT_NOMOD','ATTRIBUTE_DEFINED');
INSERT INTO DeviceGroup VALUES (29,'Existing',27,'NOEDIT_NOMOD','ATTRIBUTE_EXISTS');

alter table DEVICEGROUP
   add constraint AK_DEVICEGR_PDG_GN unique (GroupName, ParentDeviceGroupId);

/*==============================================================*/
/* Table: DEVICEGROUPMEMBER                                     */
/*==============================================================*/
create table DEVICEGROUPMEMBER  (
   DeviceGroupID        NUMBER(18,0)                    not null,
   YukonPaoId           NUMBER(18,0)                    not null,
   constraint PK_DEVICEGROUPMEMBER primary key (DeviceGroupID, YukonPaoId)
);

/*==============================================================*/
/* Table: DEVICEIDLCREMOTE                                      */
/*==============================================================*/
create table DEVICEIDLCREMOTE  (
   DEVICEID             NUMBER                          not null,
   ADDRESS              NUMBER                          not null,
   POSTCOMMWAIT         NUMBER                          not null,
   CCUAmpUseType        VARCHAR2(20)                    not null,
   constraint PK_DEVICEIDLCREMOTE primary key (DEVICEID)
);

/*==============================================================*/
/* Table: DEVICEIED                                             */
/*==============================================================*/
create table DEVICEIED  (
   DEVICEID             NUMBER                          not null,
   PASSWORD             VARCHAR2(20)                    not null,
   SLAVEADDRESS         VARCHAR2(20)                    not null,
   constraint PK_DEVICEIED primary key (DEVICEID)
);

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
);

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
);

/*==============================================================*/
/* Table: DEVICEMETERGROUP                                      */
/*==============================================================*/
create table DEVICEMETERGROUP  (
   DEVICEID             NUMBER                          not null,
   METERNUMBER          VARCHAR2(50)                    not null,
   constraint PK_DEVICEMETERGROUP primary key (DEVICEID)
);

/*==============================================================*/
/* Table: DEVICEREADJOBLOG                                      */
/*==============================================================*/
create table DEVICEREADJOBLOG  (
   DeviceReadJobLogID   NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   StartTime            DATE                            not null,
   StopTime             DATE                            not null,
   constraint PK_DEVICEREADJOBLOG primary key (DeviceReadJobLogID)
);

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
);

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
);

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
);

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
);

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
);

insert into display values(-1, 'All Categories', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');

/** insert into display values(-3, 'All Control Areas', 'Load Management Client', 'Load Management', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel'); **/
/** insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occurred.'); **/
/** insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will receive current raw point updates as they happen in the system.'); **/


insert into display values(1, 'Event Viewer', 'Alarms and Events', 'Current Event Viewer', 'This display will receive current events as they happen in the system.');
insert into display values(4, 'All Alarms', 'Alarms and Events', 'Global Alarm Viewer', 'This display will receive all alarm events as they happen in the system.');
insert into display values(5, 'Priority 1 Alarms', 'Alarms and Events', 'Priority 1 Alarm Viewer', 'This display will receive all priority 1 alarm events as they happen in the system.');
insert into display values(6, 'Priority 2 Alarms', 'Alarms and Events', 'Priority 2 Alarm Viewer', 'This display will receive all priority 2 alarm events as they happen in the system.');
insert into display values(7, 'Priority 3 Alarms', 'Alarms and Events', 'Priority 3 Alarm Viewer', 'This display will receive all priority 3 alarm events as they happen in the system.');
insert into display values(8, 'Priority 4 Alarms', 'Alarms and Events', 'Priority 4 Alarm Viewer', 'This display will receive all priority 4 alarm events as they happen in the system.');
insert into display values(9, 'Priority 5 Alarms', 'Alarms and Events', 'Priority 5 Alarm Viewer', 'This display will receive all priority 5 alarm events as they happen in the system.');
insert into display values(10, 'Priority 6 Alarms', 'Alarms and Events', 'Priority 6 Alarm Viewer', 'This display will receive all priority 6 alarm events as they happen in the system.');
insert into display values(11, 'Priority 7 Alarms', 'Alarms and Events', 'Priority 7 Alarm Viewer', 'This display will receive all priority 7 alarm events as they happen in the system.');
insert into display values(12, 'Priority 8 Alarms', 'Alarms and Events', 'Priority 8 Alarm Viewer', 'This display will receive all priority 8 alarm events as they happen in the system.');
insert into display values(13, 'Priority 9 Alarms', 'Alarms and Events', 'Priority 9 Alarm Viewer', 'This display will receive all priority 9 alarm events as they happen in the system.');
insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'Priority 10 Alarm Viewer', 'This display will receive all priority 10 alarm events as they happen in the system.');
insert into display values(15, 'Priority 11 Alarms', 'Alarms and Events', 'Priority 11 Alarm Viewer', 'This display will receive all priority 11 alarm events as they happen in the system.');
insert into display values(16, 'Priority 12 Alarms', 'Alarms and Events', 'Priority 12 Alarm Viewer', 'This display will receive all priority 12 alarm events as they happen in the system.');
insert into display values(17, 'Priority 13 Alarms', 'Alarms and Events', 'Priority 13 Alarm Viewer', 'This display will receive all priority 13 alarm events as they happen in the system.');
insert into display values(18, 'Priority 14 Alarms', 'Alarms and Events', 'Priority 14 Alarm Viewer', 'This display will receive all priority 14 alarm events as they happen in the system.');
insert into display values(19, 'Priority 15 Alarms', 'Alarms and Events', 'Priority 15 Alarm Viewer', 'This display will receive all priority 15 alarm events as they happen in the system.');
insert into display values(20, 'Priority 16 Alarms', 'Alarms and Events', 'Priority 16 Alarm Viewer', 'This display will receive all priority 16 alarm events as they happen in the system.');
insert into display values(21, 'Priority 17 Alarms', 'Alarms and Events', 'Priority 17 Alarm Viewer', 'This display will receive all priority 17 alarm events as they happen in the system.');
insert into display values(22, 'Priority 18 Alarms', 'Alarms and Events', 'Priority 18 Alarm Viewer', 'This display will receive all priority 18 alarm events as they happen in the system.');
insert into display values(23, 'Priority 19 Alarms', 'Alarms and Events', 'Priority 19 Alarm Viewer', 'This display will receive all priority 19 alarm events as they happen in the system.');
insert into display values(24, 'Priority 20 Alarms', 'Alarms and Events', 'Priority 20 Alarm Viewer', 'This display will receive all priority 20 alarm events as they happen in the system.');
insert into display values(25, 'Priority 21 Alarms', 'Alarms and Events', 'Priority 21 Alarm Viewer', 'This display will receive all priority 21 alarm events as they happen in the system.');
insert into display values(26, 'Priority 22 Alarms', 'Alarms and Events', 'Priority 22 Alarm Viewer', 'This display will receive all priority 22 alarm events as they happen in the system.');
insert into display values(27, 'Priority 23 Alarms', 'Alarms and Events', 'Priority 23 Alarm Viewer', 'This display will receive all priority 23 alarm events as they happen in the system.');
insert into display values(28, 'Priority 24 Alarms', 'Alarms and Events', 'Priority 24 Alarm Viewer', 'This display will receive all priority 24 alarm events as they happen in the system.');
insert into display values(29, 'Priority 25 Alarms', 'Alarms and Events', 'Priority 25 Alarm Viewer', 'This display will receive all priority 25 alarm events as they happen in the system.');
insert into display values(30, 'Priority 26 Alarms', 'Alarms and Events', 'Priority 26 Alarm Viewer', 'This display will receive all priority 26 alarm events as they happen in the system.');
insert into display values(31, 'Priority 27 Alarms', 'Alarms and Events', 'Priority 27 Alarm Viewer', 'This display will receive all priority 27 alarm events as they happen in the system.');
insert into display values(32, 'Priority 28 Alarms', 'Alarms and Events', 'Priority 28 Alarm Viewer', 'This display will receive all priority 28 alarm events as they happen in the system.');
insert into display values(33, 'Priority 29 Alarms', 'Alarms and Events', 'Priority 29 Alarm Viewer', 'This display will receive all priority 29 alarm events as they happen in the system.');
insert into display values(34, 'Priority 30 Alarms', 'Alarms and Events', 'Priority 30 Alarm Viewer', 'This display will receive all priority 30 alarm events as they happen in the system.');
insert into display values(35, 'Priority 31 Alarms', 'Alarms and Events', 'Priority 31 Alarm Viewer', 'This display will receive all priority 31 alarm events as they happen in the system.');

insert into display values(50, 'SOE Log', 'Alarms and Events', 'SOE Log Viewer', 'This display shows all the SOE events in the SOE log table for a given day.');
insert into display values(51, 'TAG Log', 'Alarms and Events', 'TAG Log Viewer', 'This display shows all the TAG events in the TAG log table for a given day.');

insert into display values(99, 'Your Custom Display', 'Custom Displays', 'Edit This Display', 'This display is used to show what a user created display looks like. You may edit this display to fit your own needs.');

/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique index Indx_DISPLAYNAME on DISPLAY (
   NAME ASC
);

/*==============================================================*/
/* Table: DISPLAY2WAYDATA                                       */
/*==============================================================*/
create table DISPLAY2WAYDATA  (
   DISPLAYNUM           NUMBER                          not null,
   ORDERING             NUMBER                          not null,
   POINTID              NUMBER                          not null,
   constraint PK_DISPLAY2WAYDATA primary key (DISPLAYNUM, ORDERING)
);

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
);

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
);

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
   PadChar              CHAR(1)                         not null,
   PadSide              VARCHAR2(5)                     not null,
   ReadingType          VARCHAR2(12)                    not null,
   RoundingMode         VARCHAR2(20)                    not null,
   Channel              VARCHAR2(5)                     not null,
   constraint PK_DYNAMICBILLINGFIELD primary key (id)
);

/* STANDARD */
INSERT INTO DynamicBillingField VALUES(1, 31, 'Plain Text', 0, 'M', 0, ' ','none', 'DEVICE_DATA', 'HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(2, 31, 'meterNumber', 1, ' ', 0, ' ','none', 'DEVICE_DATA', 'HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(3, 31, 'totalConsumption - reading',2,'#####', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(4, 31, 'totalConsumption - timestamp', 3, 'HH:mm', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(5, 31, 'totalConsumption - timestamp', 4, 'MM/dd/yyyy', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(6, 31, 'totalPeakDemand - reading', 5, '##0.000', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(7, 31, 'totalPeakDemand - timestamp', 6, 'HH:mm', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(8, 31, 'totalPeakDemand - timestamp', 7, 'MM/dd/yyyy', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');

/* SimpleTOU */
INSERT INTO DynamicBillingField VALUES(10, 21, 'meterNumber', 0, ' ', 0, ' ','none','DEVICE_DATA','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(11, 21, 'totalConsumption - reading' ,1, '#####', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(12, 21, 'totalConsumption - timestamp', 2, 'HH:mm', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(13, 21, 'totalConsumption - timestamp', 3, 'MM/dd/yyyy', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(14, 21, 'totalPeakDemand - reading', 4, '##0.000', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(15, 21, 'totalPeakDemand - timestamp', 5, 'HH:mm', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(16, 21, 'totalPeakDemand - timestamp', 6, 'MM/dd/yyyy', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(17, 21, 'rateAConsumption - reading', 7, '#####', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(18, 21, 'rateADemand - reading', 8, '##0.000', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(19, 21, 'rateADemand - timestamp', 9, 'HH:mm', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(20, 21, 'rateADemand - timestamp', 10, 'MM/dd/yyyy', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(21, 21, 'rateBConsumption - reading', 11, '#####', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(22, 21, 'rateBDemand - reading', 12, '##0.000', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(23, 21, 'rateBDemand - timestamp', 13, 'HH:mm', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE');
INSERT INTO DynamicBillingField VALUES(24, 21, 'rateBDemand - timestamp', 14, 'MM/dd/yyyy', 0, ' ','none','ELECTRIC','HALF_EVEN', 'ONE'); 

/*==============================================================*/
/* Table: DYNAMICBILLINGFORMAT                                  */
/*==============================================================*/
create table DYNAMICBILLINGFORMAT  (
   FormatID             NUMBER                          not null,
   Delimiter            VARCHAR2(20),
   Header               VARCHAR2(255),
   Footer               VARCHAR2(255),
   constraint PK_DYNAMICBILLINGFORMAT primary key (FormatID)
);

insert into DynamicBillingFormat values( 31, ',' ,'H Meter kWh Time Date Peak PeakT PeakD', ' ');
insert into DynamicBillingFormat values( 21, ',' ,' ' ,' '); 

/*==============================================================*/
/* Table: DYNAMICCCAREA                                         */
/*==============================================================*/
create table DYNAMICCCAREA  (
   AreaID               NUMBER                          not null,
   additionalflags      VARCHAR2(20)                    not null,
   ControlValue         NUMBER                          not null,
   constraint PK_DYNAMICCCAREA primary key (AreaID)
);

/*==============================================================*/
/* Table: DYNAMICCCOPERATIONSTATISTICS                          */
/*==============================================================*/
create table DYNAMICCCOPERATIONSTATISTICS  (
   PAObjectID           NUMBER                          not null,
   UserDefOpCount       NUMBER                          not null,
   UserDefConfFail      NUMBER                          not null,
   DailyOpCount         NUMBER                          not null,
   DailyConfFail        NUMBER                          not null,
   WeeklyOpCount        NUMBER                          not null,
   WeeklyConfFail       NUMBER                          not null,
   MonthlyOpCount       NUMBER                          not null,
   MonthlyConfFail      NUMBER                          not null,
   constraint PK_DYNAMICCCOPERATIONSTATISTIC primary key (PAObjectID)
);

/*==============================================================*/
/* Table: DYNAMICCCSPECIALAREA                                  */
/*==============================================================*/
create table DYNAMICCCSPECIALAREA  (
   AreaID               NUMBER                          not null,
   additionalflags      VARCHAR2(20)                    not null,
   ControlValue         NUMBER                          not null
);

/*==============================================================*/
/* Table: DYNAMICCCSUBSTATION                                   */
/*==============================================================*/
create table DYNAMICCCSUBSTATION  (
   SubStationID         NUMBER                          not null,
   AdditionalFlags      VARCHAR2(20)                    not null,
   SAEnabledID          NUMBER                          not null,
   constraint PK_DYNAMICCCSUBSTATION primary key (SubStationID)
);

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
);

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
);

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
);

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
);

insert into dateOfHoliday values(-1, 'None', 1,1,1969);

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
);

insert into dateOfSeason values(-1, 'Default', 1,1,12,31);

/*==============================================================*/
/* Table: DeliverySchedule                                      */
/*==============================================================*/
create table DeliverySchedule  (
   ScheduleID           NUMBER                          not null,
   PurchasePlanID       NUMBER                          not null,
   ScheduleName         VARCHAR2(60)                    not null,
   ModelID              NUMBER                          not null,
   StyleNumber          VARCHAR2(60)                    not null,
   OrderNumber          VARCHAR2(60)                    not null,
   QuotedPricePerUnit   FLOAT                           not null,
   constraint PK_DELIVERYSCHEDULE primary key (ScheduleID)
);

/*==============================================================*/
/* Table: DeviceAddress                                         */
/*==============================================================*/
create table DeviceAddress  (
   DeviceID             NUMBER                          not null,
   MasterAddress        NUMBER                          not null,
   SlaveAddress         NUMBER                          not null,
   PostCommWait         NUMBER                          not null,
   constraint PK_DEVICEADDRESS primary key (DeviceID)
);

/*==============================================================*/
/* Table: DeviceCBC                                             */
/*==============================================================*/
create table DeviceCBC  (
   DEVICEID             NUMBER                          not null,
   SERIALNUMBER         NUMBER                          not null,
   ROUTEID              NUMBER                          not null,
   constraint PK_DEVICECBC primary key (DEVICEID)
);

/*==============================================================*/
/* Table: DeviceCustomerList                                    */
/*==============================================================*/
create table DeviceCustomerList  (
   CustomerID           NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   constraint PK_DEVICECUSTOMERLIST primary key (DeviceID, CustomerID)
);

/*==============================================================*/
/* Table: DeviceDirectCommSettings                              */
/*==============================================================*/
create table DeviceDirectCommSettings  (
   DEVICEID             NUMBER                          not null,
   PORTID               NUMBER                          not null,
   constraint PK_DEVICEDIRECTCOMMSETTINGS primary key (DEVICEID)
);

/*==============================================================*/
/* Table: DeviceGroupComposed                                   */
/*==============================================================*/
create table DeviceGroupComposed  (
   DeviceGroupComposedId NUMBER                          not null,
   DeviceGroupId        NUMBER                          not null,
   CompositionType      VARCHAR2(100),
   constraint PK_DevGroupComp primary key (DeviceGroupComposedId)
);

/*==============================================================*/
/* Table: DeviceGroupComposedGroup                              */
/*==============================================================*/
create table DeviceGroupComposedGroup  (
   DeviceGroupComposedGroupId NUMBER                          not null,
   DeviceGroupComposedId NUMBER                          not null,
   GroupName            VARCHAR2(255)                   not null,
   IsNot                CHAR(1)                         not null,
   constraint PK_DevGroupCompGroup primary key (DeviceGroupComposedGroupId)
);

/*==============================================================*/
/* Table: DeviceMCT400Series                                    */
/*==============================================================*/
create table DeviceMCT400Series  (
   DeviceID             NUMBER                          not null,
   DisconnectAddress    NUMBER                          not null,
   TOUScheduleID        NUMBER                          not null,
   constraint PK_DEV400S primary key (DeviceID)
);

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
);

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
);

/*==============================================================*/
/* Table: DeviceRoutes                                          */
/*==============================================================*/
create table DeviceRoutes  (
   DEVICEID             NUMBER                          not null,
   ROUTEID              NUMBER                          not null,
   constraint PK_DEVICEROUTES primary key (DEVICEID)
);

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
);

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
   PagerID              VARCHAR2(10)                    not null,
   constraint PK_DEVICETNPPSETTINGS primary key (DeviceID)
);

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
);

INSERT INTO DeviceTypeCommand VALUES (0, 0, 'System', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1, 0, 'CICustomer', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-2, 0, 'Davis Weather', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-3, 0, 'Fulcrum', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-4, 0, 'Landis-Gyr S4', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-5, 0, 'MCT Broadcast', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-6, 0, 'Sixnet', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-7, 0, 'Tap Terminal', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-9, 0, 'SA205Serial', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-10, 0, 'SA305Serial', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-11, 0, 'Device Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-13, -1, 'LMT-2', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-14, -2, 'LMT-2', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-15, -3, 'LMT-2', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-16, -4, 'LMT-2', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-17, -5, 'LMT-2', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-18, -6, 'LMT-2', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-19, -7, 'LMT-2', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-20, -8, 'LMT-2', 8, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-21, -9, 'LMT-2', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-22, -10, 'LMT-2', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-23, -27, 'Macro Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-24, -28, 'Macro Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-25, -29, 'Alpha A1', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-26, -29, 'Alpha Power Plus', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-27, -30, 'CBC 6510', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-28, -31, 'CBC 6510', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-29, -32, 'CBC 6510', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-30, -33, 'CBC 6510', 4, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-31, -30, 'CBC FP-2800', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-32, -31, 'CBC FP-2800', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-33, -32, 'CBC FP-2800', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-34, -33, 'CBC FP-2800', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-35, -30, 'CBC Versacom', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-36, -31, 'CBC Versacom', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-37, -32, 'CBC Versacom', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-38, -33, 'CBC Versacom', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-39, -30, 'Cap Bank', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-40, -31, 'Cap Bank', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-41, -32, 'Cap Bank', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-42, -33, 'Cap Bank', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-43, -34, 'CCU-710A', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-44, -35, 'CCU-710A', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-45, -36, 'CCU-710A', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-46, -34, 'CCU-711', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-47, -35, 'CCU-711', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-48, -36, 'CCU-711', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-49, -37, 'CCU-711', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-50, -38, 'Emetcon Group', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-51, -39, 'Emetcon Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-52, -40, 'Emetcon Group', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-53, -41, 'Emetcon Group', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-54, -42, 'Emetcon Group', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-55, -43, 'Emetcon Group', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-56, -27, 'Expresscom Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-57, -28, 'Expresscom Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-58, -27, 'Golay Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-59, -28, 'Golay Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-60, -27, 'Macro Group', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-61, -28, 'Macro Group', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-62, -27, 'Ripple Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-63, -28, 'Ripple Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-64, -27, 'SA-205 Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-65, -28, 'SA-205 Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-66, -27, 'SA-305 Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-67, -28, 'SA-305 Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-68, -27, 'SA-Digital Group', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-69, -28, 'SA-Digital Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-70, -27, 'Versacom Group', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-71, -28, 'Versacom Group', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-72, -44, 'Versacom Group', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-73, -45, 'Versacom Group', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-74, -95, 'Versacom Group', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-75, -96, 'Versacom Group', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-76, -48, 'Versacom Group', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-77, -97, 'Versacom Group', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-78, -34, 'TCU-5000', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-79, -35, 'TCU-5000', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-80, -36, 'TCU-5000', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-81, -34, 'TCU-5500', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-82, -35, 'TCU-5500', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-83, -36, 'TCU-5500', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-84, -52, 'Repeater 800', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-85, -3, 'Repeater 800', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-86, -53, 'Repeater 800', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-87, -54, 'Repeater 800', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-88, -52, 'Repeater', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-89, -3, 'Repeater', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-90, -53, 'Repeater', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-91, -54, 'Repeater', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-92, -34, 'RTU-DART', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-93, -35, 'RTU-DART', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-94, -36, 'RTU-DART', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-95, -34, 'RTU-DNP', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-96, -35, 'RTU-DNP', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-97, -36, 'RTU-DNP', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-98, -34, 'RTU-ILEX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-99, -35, 'RTU-ILEX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-100, -36, 'RTU-ILEX', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-101, -34, 'RTU-WELCO', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-102, -35, 'RTU-WELCO', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-103, -36, 'RTU-WELCO', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-104, -55, 'ION-7330', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-105, -56, 'ION-7330', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-106, -57, 'ION-7330', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-107, -55, 'ION-7700', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-108, -56, 'ION-7700', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-109, -57, 'ION-7700', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-110, -55, 'ION-8300', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-111, -56, 'ION-8300', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-112, -57, 'ION-8300', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-113, -34, 'LCU-415', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-114, -35, 'LCU-415', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-115, -36, 'LCU-415', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-116, -34, 'LCU-EASTRIVER', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-117, -35, 'LCU-EASTRIVER', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-118, -36, 'LCU-EASTRIVER', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-119, -34, 'LCU-LG', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-120, -35, 'LCU-LG', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-121, -36, 'LCU-LG', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-122, -34, 'LCU-T3026', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-123, -35, 'LCU-T3026', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-124, -36, 'LCU-T3026', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-125, -63, 'ExpresscomSerial', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-126, -64, 'ExpresscomSerial', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-127, -65, 'ExpresscomSerial', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-128, -66, 'ExpresscomSerial', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-129, -67, 'ExpresscomSerial', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-130, -68, 'ExpresscomSerial', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-131, -69, 'ExpresscomSerial', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-132, -174, 'ExpresscomSerial', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-133, -175, 'ExpresscomSerial', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-134, -176, 'ExpresscomSerial', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-135, -177, 'ExpresscomSerial', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-136, -178, 'ExpresscomSerial', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-137, -179, 'ExpresscomSerial', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-138, -180, 'ExpresscomSerial', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-139, -181, 'ExpresscomSerial', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-140, -182, 'ExpresscomSerial', 16, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-141, -183, 'ExpresscomSerial', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-142, -184, 'ExpresscomSerial', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-143, -185, 'ExpresscomSerial', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-144, -186, 'ExpresscomSerial', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-147, -1, 'MCT-210', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-148, -2, 'MCT-210', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-149, -3, 'MCT-210', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-150, -4, 'MCT-210', 4, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-151, -5, 'MCT-210', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-152, -6, 'MCT-210', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-153, -7, 'MCT-210', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-154, -8, 'MCT-210', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-155, -9, 'MCT-210', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-156, -10, 'MCT-210', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-157, -1, 'MCT-213', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-158, -2, 'MCT-213', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-159, -3, 'MCT-213', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-160, -4, 'MCT-213', 4, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-161, -5, 'MCT-213', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-162, -6, 'MCT-213', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-163, -7, 'MCT-213', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-164, -8, 'MCT-213', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-165, -9, 'MCT-213', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-166, -10, 'MCT-213', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-167, -11, 'MCT-213', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-168, -12, 'MCT-213', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-169, -13, 'MCT-213', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-170, -1, 'MCT-240', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-171, -2, 'MCT-240', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-172, -3, 'MCT-240', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-173, -4, 'MCT-240', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-174, -5, 'MCT-240', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-175, -6, 'MCT-240', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-176, -7, 'MCT-240', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-177, -8, 'MCT-240', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-178, -9, 'MCT-240', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-179, -10, 'MCT-240', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-180, -15, 'MCT-240', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-181, -18, 'MCT-240', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-182, -19, 'MCT-240', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-183, -1, 'MCT-248', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-184, -2, 'MCT-248', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-185, -3, 'MCT-248', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-186, -4, 'MCT-248', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-187, -5, 'MCT-248', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-188, -6, 'MCT-248', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-189, -7, 'MCT-248', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-190, -8, 'MCT-248', 8, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-191, -9, 'MCT-248', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-192, -10, 'MCT-248', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-193, -15, 'MCT-248', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-194, -18, 'MCT-248', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-195, -19, 'MCT-248', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-196, -1, 'MCT-250', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-197, -2, 'MCT-250', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-198, -3, 'MCT-250', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-199, -4, 'MCT-250', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-200, -5, 'MCT-250', 5, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-201, -6, 'MCT-250', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-202, -7, 'MCT-250', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-203, -8, 'MCT-250', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-204, -9, 'MCT-250', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-205, -10, 'MCT-250', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-206, -14, 'MCT-250', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-207, -15, 'MCT-250', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-208, -18, 'MCT-250', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-209, -19, 'MCT-250', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-210, -1, 'MCT-310', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-211, -2, 'MCT-310', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-212, -3, 'MCT-310', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-213, -4, 'MCT-310', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-214, -5, 'MCT-310', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-215, -6, 'MCT-310', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-216, -7, 'MCT-310', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-217, -8, 'MCT-310', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-218, -9, 'MCT-310', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-219, -10, 'MCT-310', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-220, -1, 'MCT-310CT', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-221, -2, 'MCT-310CT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-222, -3, 'MCT-310CT', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-223, -4, 'MCT-310CT', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-224, -5, 'MCT-310CT', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-225, -6, 'MCT-310CT', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-226, -7, 'MCT-310CT', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-227, -8, 'MCT-310CT', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-228, -9, 'MCT-310CT', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-229, -10, 'MCT-310CT', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-230, -1, 'MCT-310IDL', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-231, -2, 'MCT-310IDL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-232, -3, 'MCT-310IDL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-233, -4, 'MCT-310IDL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-234, -5, 'MCT-310IDL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-235, -6, 'MCT-310IDL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-236, -7, 'MCT-310IDL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-237, -8, 'MCT-310IDL', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-238, -9, 'MCT-310IDL', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-239, -10, 'MCT-310IDL', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-240, -1, 'MCT-310ID', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-241, -2, 'MCT-310ID', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-242, -3, 'MCT-310ID', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-243, -4, 'MCT-310ID', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-244, -5, 'MCT-310ID', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-245, -6, 'MCT-310ID', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-246, -7, 'MCT-310ID', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-247, -8, 'MCT-310ID', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-248, -9, 'MCT-310ID', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-249, -10, 'MCT-310ID', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-250, -11, 'MCT-310ID', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-251, -12, 'MCT-310ID', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-252, -13, 'MCT-310ID', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-253, -1, 'MCT-310IL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-254, -2, 'MCT-310IL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-255, -3, 'MCT-310IL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-256, -4, 'MCT-310IL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-257, -5, 'MCT-310IL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-258, -6, 'MCT-310IL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-259, -7, 'MCT-310IL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-260, -8, 'MCT-310IL', 8, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-261, -9, 'MCT-310IL', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-262, -10, 'MCT-310IL', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-263, -15, 'MCT-310IL', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-264, -18, 'MCT-310IL', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-265, -19, 'MCT-310IL', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-266, -1, 'MCT-318', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-267, -2, 'MCT-318', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-268, -3, 'MCT-318', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-269, -4, 'MCT-318', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-270, -5, 'MCT-318', 5, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-271, -6, 'MCT-318', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-272, -7, 'MCT-318', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-273, -8, 'MCT-318', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-274, -9, 'MCT-318', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-275, -10, 'MCT-318', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-276, -14, 'MCT-318', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-277, -1, 'MCT-318L', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-278, -2, 'MCT-318L', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-279, -3, 'MCT-318L', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-280, -4, 'MCT-318L', 4, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-281, -5, 'MCT-318L', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-282, -6, 'MCT-318L', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-283, -7, 'MCT-318L', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-284, -8, 'MCT-318L', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-285, -9, 'MCT-318L', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-286, -10, 'MCT-318L', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-287, -14, 'MCT-318L', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-288, -1, 'MCT-360', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-289, -2, 'MCT-360', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-290, -3, 'MCT-360', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-291, -4, 'MCT-360', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-292, -5, 'MCT-360', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-293, -6, 'MCT-360', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-294, -7, 'MCT-360', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-295, -8, 'MCT-360', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-296, -9, 'MCT-360', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-297, -10, 'MCT-360', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-298, -14, 'MCT-360', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-299, -26, 'MCT-360', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-300, -1, 'MCT-370', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-301, -2, 'MCT-370', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-302, -3, 'MCT-370', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-303, -4, 'MCT-370', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-304, -5, 'MCT-370', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-305, -6, 'MCT-370', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-306, -7, 'MCT-370', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-307, -8, 'MCT-370', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-308, -9, 'MCT-370', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-309, -10, 'MCT-370', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-310, -14, 'MCT-370', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-311, -20, 'MCT-370', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-312, -21, 'MCT-370', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-313, -22, 'MCT-370', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-314, -23, 'MCT-370', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-315, -24, 'MCT-370', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-316, -25, 'MCT-370', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-317, -26, 'MCT-370', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-318, -1, 'MCT-410IL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-319, -81, 'MCT-410IL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-320, -3, 'MCT-410IL', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-321, -6, 'MCT-410IL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-322, -34, 'MCT-410IL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-323, -82, 'MCT-410IL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-324, -18, 'MCT-410IL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-325, -19, 'MCT-410IL', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-326, -83, 'MCT-410IL', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-327, -84, 'MCT-410IL', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-328, -70, 'LCRSerial', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-329, -85, 'LCRSerial', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-330, -86, 'LCRSerial', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-331, -87, 'LCRSerial', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-332, -88, 'LCRSerial', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-333, -89, 'LCRSerial', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-334, -90, 'LCRSerial', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-335, -91, 'LCRSerial', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-336, -92, 'LCRSerial', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-337, -93, 'LCRSerial', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-338, -94, 'LCRSerial', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-339, -95, 'LCRSerial', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-340, -96, 'LCRSerial', 13, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-341, -97, 'LCRSerial', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-342, -48, 'LCRSerial', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-343, -71, 'VersacomSerial', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-344, -72, 'VersacomSerial', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-345, -73, 'VersacomSerial', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-346, -74, 'VersacomSerial', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-347, -75, 'VersacomSerial', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-348, -85, 'VersacomSerial', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-349, -86, 'VersacomSerial', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-350, -87, 'VersacomSerial', 8, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-351, -88, 'VersacomSerial', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-352, -89, 'VersacomSerial', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-353, -90, 'VersacomSerial', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-354, -91, 'VersacomSerial', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-355, -92, 'VersacomSerial', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-356, -93, 'VersacomSerial', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-357, -94, 'VersacomSerial', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-358, -95, 'VersacomSerial', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-359, -96, 'VersacomSerial', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-360, -97, 'VersacomSerial', 18, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-361, -11, 'MCT-410IL', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-362, -12, 'MCT-410IL', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-363, -13, 'MCT-410IL', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-364, -1, 'MCT-410CL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-365, -81, 'MCT-410CL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-366, -3, 'MCT-410CL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-367, -6, 'MCT-410CL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-368, -34, 'MCT-410CL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-369, -82, 'MCT-410CL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-370, -18, 'MCT-410CL', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-371, -19, 'MCT-410CL', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-372, -83, 'MCT-410CL', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-373, -84, 'MCT-410CL', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-374, -11, 'MCT-410CL', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-375, -12, 'MCT-410CL', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-376, -13, 'MCT-410CL', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-377, -30, 'MCT-248', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-378, -31, 'MCT-248', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-379, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-380, -99, 'All MCT-4xx Series', 15, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-381, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-382, -99, 'All MCT-4xx Series', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-383, -100, 'SENTINEL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-384, -101, 'SENTINEL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-385, -102, 'SENTINEL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-386, -103, 'SENTINEL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-387, -104, 'SENTINEL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-388, -105, 'All MCT-4xx Series', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-389, -106, 'MCT-410IL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-390, -107, 'MCT-410IL', 18, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-391, -108, 'MCT-410IL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-392, -109, 'All MCT-4xx Series', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-393, -108, 'MCT-410IL', 21, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-394, -111, 'MCT-410IL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-395, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-396, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-397, -114, 'MCT-410IL', 25, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-398, -105, 'All MCT-4xx Series', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-399, -106, 'MCT-410CL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-400, -107, 'MCT-410CL', 18, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-401, -108, 'MCT-410CL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-402, -109, 'All MCT-4xx Series', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-404, -111, 'MCT-410CL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-405, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-406, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-407, -114, 'MCT-410CL', 25, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-408, -1, 'MCT-470', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-409, -2, 'MCT-470', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-410, -3, 'MCT-470', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-411, -20, 'MCT-470', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-412, -21, 'MCT-470', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-413, -22, 'MCT-470', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-414, -115, 'MCT-470', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-415, -116, 'MCT-470', 8, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-416, -117, 'MCT-470', 9, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-417, -118, 'MCT-470', 10, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-418, -119, 'MCT-470', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-419, -120, 'MCT-470', 12, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-420, -121, 'MCT-470', 13, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-421, -122, 'MCT-470', 14, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-423, -111, 'MCT-470', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-424, -7, 'MCT-470', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-425, -8, 'MCT-470', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-426, -3, 'MCT-430A', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-427, -20, 'MCT-430A', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-428, -21, 'MCT-430A', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-429, -22, 'MCT-430A', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-430, -115, 'MCT-430A', 5, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-431, -116, 'MCT-430A', 6, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-432, -117, 'MCT-430A', 7, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-433, -118, 'MCT-430A', 8, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-434, -119, 'MCT-430A', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-435, -120, 'MCT-430A', 10, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-436, -121, 'MCT-430A', 11, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-437, -122, 'MCT-430A', 12, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-449, -3, 'MCT-430S4', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-450, -20, 'MCT-430S4', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-451, -21, 'MCT-430S4', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-452, -22, 'MCT-430S4', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-453, -115, 'MCT-430S4', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-454, -116, 'MCT-430S4', 6, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-455, -117, 'MCT-430S4', 7, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-456, -118, 'MCT-430S4', 8, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-457, -119, 'MCT-430S4', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-458, -120, 'MCT-430S4', 10, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-459, -121, 'MCT-430S4', 11, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-460, -122, 'MCT-430S4', 12, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-462, -30, 'CBC Expresscom', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-463, -31, 'CBC Expresscom', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-464, -32, 'CBC Expresscom', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-465, -33, 'CBC Expresscom', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-466, -30, 'CBC 7010', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-467, -31, 'CBC 7010', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-468, -32, 'CBC 7010', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-469, -33, 'CBC 7010', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-470, -30, 'CBC 7011', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-471, -31, 'CBC 7011', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-472, -32, 'CBC 7011', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-473, -33, 'CBC 7011', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-474, -30, 'CBC 7011', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-475, -31, 'CBC 7011', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-476, -32, 'CBC 7011', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-477, -33, 'CBC 7011', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-478, -30, 'CBC 7012', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-479, -31, 'CBC 7012', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-480, -32, 'CBC 7012', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-481, -33, 'CBC 7012', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-482, -30, 'CBC 7020', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-483, -31, 'CBC 7020', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-484, -171, 'CBC 7020', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-485, -172, 'CBC 7020', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-486, -30, 'CBC 7022', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-487, -31, 'CBC 7022', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-488, -171, 'CBC 7022', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-489, -172, 'CBC 7022', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-490, -30, 'CBC 7023', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-491, -31, 'CBC 7023', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-492, -171, 'CBC 7023', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-493, -172, 'CBC 7023', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-494, -30, 'CBC 7024', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-495, -31, 'CBC 7024', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-496, -171, 'CBC 7024', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-497, -172, 'CBC 7024', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-498, -124, 'VersacomSerial', 19, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-499, -125, 'VersacomSerial', 20, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-500, -126, 'VersacomSerial', 21, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-501, -34, 'CCU-721', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-502, -35, 'CCU-721', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-503, -36, 'CCU-721', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-504, -37, 'CCU-721', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-505, -1, 'MCT-410FL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-506, -81, 'MCT-410FL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-507, -3, 'MCT-410FL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-508, -6, 'MCT-410FL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-509, -34, 'MCT-410FL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-510, -82, 'MCT-410FL', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-511, -18, 'MCT-410FL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-512, -19, 'MCT-410FL', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-513, -83, 'MCT-410FL', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-514, -84, 'MCT-410FL', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-515, -11, 'MCT-410FL', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-516, -12, 'MCT-410FL', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-517, -13, 'MCT-410FL', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-518, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-519, -99, 'All MCT-4xx Series', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-520, -105, 'All MCT-4xx Series', 16, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-521, -106, 'MCT-410FL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-522, -107, 'MCT-410FL', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-523, -108, 'MCT-410FL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-524, -109, 'All MCT-4xx Series', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-526, -111, 'MCT-410FL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-527, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-528, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-529, -114, 'MCT-410FL', 25, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-530, -1, 'MCT-410GL', 1, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-531, -81, 'MCT-410GL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-532, -3, 'MCT-410GL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-533, -6, 'MCT-410GL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-534, -34, 'MCT-410GL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-535, -82, 'MCT-410GL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-536, -18, 'MCT-410GL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-537, -19, 'MCT-410GL', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-538, -83, 'MCT-410GL', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-539, -84, 'MCT-410GL', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-540, -11, 'MCT-410GL', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-541, -12, 'MCT-410GL', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-542, -13, 'MCT-410GL', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-543, -98, 'All MCT-4xx Series', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-544, -99, 'All MCT-4xx Series', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-545, -105, 'All MCT-4xx Series', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-546, -106, 'MCT-410GL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-547, -107, 'MCT-410GL', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-548, -108, 'MCT-410GL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-549, -109, 'All MCT-4xx Series', 20, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-551, -111, 'MCT-410GL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-552, -112, 'All MCT-4xx Series', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-553, -113, 'All MCT-4xx Series', 24, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-554, -114, 'MCT-410GL', 25, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-555, -3, 'MCT-430SL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-556, -20, 'MCT-430SL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-557, -21, 'MCT-430SL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-558, -22, 'MCT-430SL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-559, -115, 'MCT-430SL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-560, -116, 'MCT-430SL', 6, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-561, -117, 'MCT-430SL', 7, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-562, -118, 'MCT-430SL', 8, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-563, -119, 'MCT-430SL', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-564, -120, 'MCT-430SL', 10, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-565, -121, 'MCT-430SL', 11, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-566, -122, 'MCT-430SL', 12, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-568, -52, 'Repeater 801', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-569, -3, 'Repeater 801', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-570, -53, 'Repeater 801', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-571, -54, 'Repeater 801', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-572, -52, 'Repeater 921', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-573, -3, 'Repeater 921', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-574, -53, 'Repeater 921', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-575, -54, 'Repeater 921', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-576, -127, 'VersacomSerial', 22, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-577, -128, 'VersacomSerial', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-578, -129, 'VersacomSerial', 24, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-581, -83, 'MCT-470', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-582, -6, 'MCT-470', 24, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-583, -34, 'MCT-470', 25, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-584, -15, 'LMT-2', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-585, -18, 'LMT-2', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-586, -19, 'LMT-2', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-587, -15, 'DCT-501', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-588, -18, 'DCT-501', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-589, -19, 'DCT-501', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-590, -15, 'MCT-310IDL', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-591, -18, 'MCT-310IDL', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-592, -19, 'MCT-310IDL', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-593, -15, 'MCT-310CT', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-594, -18, 'MCT-310CT', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-595, -19, 'MCT-310CT', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-596, -15, 'MCT-310IM', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-597, -18, 'MCT-310IM', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-598, -19, 'MCT-310IM', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-599, -15, 'MCT-318L', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-600, -18, 'MCT-318L', 13, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-601, -19, 'MCT-318L', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-602, -15, 'MCT-410CL', 26, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-603, -15, 'MCT-410FL', 26, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-604, -15, 'MCT-410GL', 26, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-605, -15, 'MCT-410IL', 26, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-606, -15, 'MCT-430A', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-607, -18, 'MCT-430A', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-608, -19, 'MCT-430A', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-609, -15, 'MCT-430SL', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-610, -18, 'MCT-430SL', 15, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-611, -19, 'MCT-430SL', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-612, -15, 'MCT-430S4', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-613, -18, 'MCT-430S4', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-614, -19, 'MCT-430S4', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-615, -15, 'MCT-470', 21, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-616, -18, 'MCT-470', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-617, -19, 'MCT-470', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-618, -6, 'MCT-430A', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-619, -34, 'MCT-430A', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-620, -6, 'MCT-430SL', 17, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-621, -34, 'MCT-430SL', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-622, -6, 'MCT-430S4', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-623, -34, 'MCT-430S4', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-624, -105, 'MCT-430A', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-625, -109, 'MCT-430A', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-626, -112, 'MCT-430A', 21, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-627, -113, 'MCT-430A', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-628, -105, 'MCT-430SL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-629, -109, 'MCT-430SL', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-630, -112, 'MCT-430SL', 21, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-631, -113, 'MCT-430SL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-632, -105, 'MCT-430S4', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-633, -109, 'MCT-430S4', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-634, -112, 'MCT-430S4', 21, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-635, -113, 'MCT-430S4', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-636, -105, 'MCT-470', 26, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-637, -109, 'MCT-470', 27, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-638, -112, 'MCT-470', 28, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-639, -113, 'MCT-470', 29, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES(-640, -130, 'MCT-410 kWh Only', 21, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-641, -130, 'MCT-410CL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-642, -130, 'MCT-410FL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-643, -130, 'MCT-410GL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-644, -130, 'MCT-410IL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-645, -130, 'MCT-410iLE', 21, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-646, -130, 'MCT-430A', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-647, -130, 'MCT-430S4', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-648, -130, 'MCT-430SL', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-649, -130, 'MCT-470', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-650, -131, 'MCT-410 kWh Only', 22, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-651, -131, 'MCT-410CL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-652, -131, 'MCT-410FL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-653, -131, 'MCT-410GL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-654, -131, 'MCT-410IL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-655, -131, 'MCT-410iLE', 22, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-656, -131, 'MCT-430A', 24, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-657, -131, 'MCT-430S4', 24, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-658, -131, 'MCT-430SL', 24, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-659, -131, 'MCT-470', 30, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-660, -132, 'MCT-410 kWh Only', 23, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-661, -132, 'MCT-410CL', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-662, -132, 'MCT-410FL', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-663, -132, 'MCT-410GL', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-664, -132, 'MCT-410IL', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-665, -132, 'MCT-410iLE', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-666, -132, 'MCT-430A', 25, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-667, -132, 'MCT-430S4', 25, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-668, -132, 'MCT-430SL', 25, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-669, -132, 'MCT-470', 31, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-670, -133, 'ExpresscomSerial', 21, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-671, -134, 'ExpresscomSerial', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-672, -135, 'ExpresscomSerial', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-673, -133, 'Expresscom Group', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-674, -134, 'Expresscom Group', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-675, -135, 'Expresscom Group', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-676, -111, 'MCT-430A', 26, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-677, -2, 'MCT-430A', 27, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-678, -83, 'MCT-430A', 28, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-679, -136, 'MCT-430A', 29, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-680, -111, 'MCT-430S4', 26, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-681, -2, 'MCT-430S4', 27, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-682, -83, 'MCT-430S4', 28, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-683, -136, 'MCT-430S4', 29, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-684, -111, 'MCT-430SL', 26, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-685, -2, 'MCT-430SL', 27, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-686, -83, 'MCT-430SL', 28, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-687, -136, 'MCT-430SL', 29, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-689, -136, 'MCT-410CL', 30, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-690, -136, 'MCT-410FL', 30, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-691, -136, 'MCT-410GL', 30, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-692, -136, 'MCT-410IL', 30, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-693, -136, 'MCT-470', 32, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-694, -137, 'MCT-410CL', 31, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-695, -137, 'MCT-410FL', 31, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-696, -137, 'MCT-410GL', 31, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-697, -137, 'MCT-410IL', 31, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-698, -138, 'MCT-410CL', 32, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-699, -138, 'MCT-410FL', 32, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-700, -138, 'MCT-410GL', 32, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-701, -138, 'MCT-410IL', 32, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-702, -139, 'MCT-410CL', 32, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-703, -139, 'MCT-410FL', 32, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-704, -139, 'MCT-410GL', 32, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-705, -139, 'MCT-410IL', 32, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-706, -52, 'Repeater 902', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-707, -3, 'Repeater 902', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-708, -53, 'Repeater 902', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-709, -54, 'Repeater 902', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-710, -140, 'MCT-410CL', 33, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-711, -140, 'MCT-410FL', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-712, -140, 'MCT-410GL', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-713, -140, 'MCT-410IL', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-714, -141, 'MCT-410CL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-715, -141, 'MCT-410FL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-716, -141, 'MCT-410GL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-717, -141, 'MCT-410IL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-718, -142, 'MCT-410CL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-719, -142, 'MCT-410FL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-720, -142, 'MCT-410GL', 35, 'N', -1); 

INSERT INTO DeviceTypeCommand VALUES (-721, -142, 'MCT-410IL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-722, -140, 'MCT-470', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-723, -143, 'ExpresscomSerial', 24, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-724, -143, 'Expresscom Group', 24, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-725, -144, 'MCT-430A', 30, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-726, -144, 'MCT-430S4', 30, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-727, -144, 'MCT-430SL', 30, 'N', -1); 

INSERT INTO DeviceTypeCommand VALUES (-728, -148, 'ExpresscomSerial', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-729, -149, 'ExpresscomSerial', 26, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-730, -150, 'ExpresscomSerial', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-731, -151, 'ExpresscomSerial', 28, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-732, -152, 'ExpresscomSerial', 29, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-733, -153, 'ExpresscomSerial', 30, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-734, -145, 'VersacomSerial', 24, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-735, -146, 'VersacomSerial', 25, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-736, -147, 'VersacomSerial', 26, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-737, -154, 'MCT-410CL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-738, -154, 'MCT-410FL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-739, -154, 'MCT-410GL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-740, -154, 'MCT-410IL', 36, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-741, -155, 'MCT-410CL', 37, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-742, -155, 'MCT-410FL', 37, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-743, -155, 'MCT-410GL', 37, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-744, -155, 'MCT-410IL', 37, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-745, -156, 'MCT-410CL', 38, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-746, -156, 'MCT-410FL', 38, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-747, -156, 'MCT-410GL', 38, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-748, -156, 'MCT-410IL', 38, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-749, -157, 'MCT-410CL', 39, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-750, -157, 'MCT-410FL', 39, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-751, -157, 'MCT-410GL', 39, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-752, -157, 'MCT-410IL', 39, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-753, -158, 'LCR-3102', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-754, -159, 'LCR-3102', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-755, -160, 'LCR-3102', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-756, -161, 'LCR-3102', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-757, -162, 'LCR-3102', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-758, -163, 'LCR-3102', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-759, -164, 'LCR-3102', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-760, -165, 'LCR-3102', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-761, -166, 'LCR-3102', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-762, -167, 'LCR-3102', 10, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-763, -3, 'MCT-430A3', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-764, -20, 'MCT-430A3', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-765, -21, 'MCT-430A3', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-766, -22, 'MCT-430A3', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-767, -115, 'MCT-430A3', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-768, -116, 'MCT-430A3', 6, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-769, -117, 'MCT-430A3', 7, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-770, -118, 'MCT-430A3', 8, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-771, -119, 'MCT-430A3', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-772, -120, 'MCT-430A3', 10, 'N', -1); 

INSERT INTO DeviceTypeCommand VALUES (-773, -121, 'MCT-430A3', 11, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-774, -122, 'MCT-430A3', 12, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-776, -15, 'MCT-430A3', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-777, -18, 'MCT-430A3', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-778, -19, 'MCT-430A3', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-779, -6, 'MCT-430A3', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-780, -34, 'MCT-430A3', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-781, -105, 'MCT-430A3', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-782, -109, 'MCT-430A3', 20, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-783, -112, 'MCT-430A3', 21, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-784, -113, 'MCT-430A3', 22, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-785, -130, 'MCT-430A3', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-786, -131, 'MCT-430A3', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-787, -132, 'MCT-430A3', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-788, -111, 'MCT-430A3', 26, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-789, -2, 'MCT-430A3', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-790, -83, 'MCT-430A3', 28, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-791, -136, 'MCT-430A3', 29, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-792, -144, 'MCT-430A3', 30, 'N', -1);
 
INSERT INTO DeviceTypeCommand VALUES (-793, -168, 'MCT-430A3', 31, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-794, -169, 'MCT-410CL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-795, -169, 'MCT-410FL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-796, -169, 'MCT-410GL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-797, -169, 'MCT-410IL', 40, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-798, -170, 'MCT-410CL', 41, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-799, -170, 'MCT-410FL', 41, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-800, -170, 'MCT-410GL', 41, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-801, -170, 'MCT-410IL', 41, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-802, -173, 'CBC 7020', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-803, -173, 'CBC 7022', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-804, -173, 'CBC 7023', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-805, -173, 'CBC 7024', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-807, -30, 'CBC DNP', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-808, -31, 'CBC DNP', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-809, -173, 'CBC DNP', 3, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-810, -52, 'Repeater 850', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-811, -3, 'Repeater 850', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-812, -53, 'Repeater 850', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-813, -54, 'Repeater 850', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-814, -187, 'MCT-470', 34, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-815, -188, 'MCT-470', 35, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-820, -1, 'MCT-420CLD', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-821, -81, 'MCT-420CLD', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-822, -3, 'MCT-420CLD', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-823, -6, 'MCT-420CLD', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-824, -34, 'MCT-420CLD', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-825, -82, 'MCT-420CLD', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-826, -18, 'MCT-420CLD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-827, -19, 'MCT-420CLD', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-828, -83, 'MCT-420CLD', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-829, -84, 'MCT-420CLD', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-830, -11, 'MCT-420CLD', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-831, -12, 'MCT-420CLD', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-832, -13, 'MCT-420CLD', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-833, -106, 'MCT-420CLD', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-834, -107, 'MCT-420CLD', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-835, -108, 'MCT-420CLD', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-836, -111, 'MCT-420CLD', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-837, -114, 'MCT-420CLD', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-838, -15, 'MCT-420CLD', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-839, -130, 'MCT-420CLD', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-840, -131, 'MCT-420CLD', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-841, -132, 'MCT-420CLD', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-842, -136, 'MCT-420CLD', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-843, -137, 'MCT-420CLD', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-844, -138, 'MCT-420CLD', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-845, -139, 'MCT-420CLD', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-846, -140, 'MCT-420CLD', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-847, -141, 'MCT-420CLD', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-848, -142, 'MCT-420CLD', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-849, -154, 'MCT-420CLD', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-850, -155, 'MCT-420CLD', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-851, -156, 'MCT-420CLD', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-852, -157, 'MCT-420CLD', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-853, -169, 'MCT-420CLD', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-854, -170, 'MCT-420CLD', 35, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-860, -1, 'MCT-420CL', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-861, -81, 'MCT-420CL', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-862, -3, 'MCT-420CL', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-863, -6, 'MCT-420CL', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-864, -34, 'MCT-420CL', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-865, -82, 'MCT-420CL', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-866, -18, 'MCT-420CL', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-867, -19, 'MCT-420CL', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-868, -83, 'MCT-420CL', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-869, -84, 'MCT-420CL', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-870, -106, 'MCT-420CL', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-871, -107, 'MCT-420CL', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-872, -108, 'MCT-420CL', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-873, -111, 'MCT-420CL', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-874, -114, 'MCT-420CL', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-875, -15, 'MCT-420CL', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-876, -130, 'MCT-420CL', 17, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-877, -131, 'MCT-420CL', 18, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-878, -132, 'MCT-420CL', 19, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-879, -136, 'MCT-420CL', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-880, -137, 'MCT-420CL', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-881, -138, 'MCT-420CL', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-882, -139, 'MCT-420CL', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-883, -140, 'MCT-420CL', 24, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-884, -141, 'MCT-420CL', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-885, -142, 'MCT-420CL', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-886, -154, 'MCT-420CL', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-887, -155, 'MCT-420CL', 28, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-888, -156, 'MCT-420CL', 29, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-889, -157, 'MCT-420CL', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-890, -169, 'MCT-420CL', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-891, -170, 'MCT-420CL', 32, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-900, -1, 'MCT-420FLD', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-901, -81, 'MCT-420FLD', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-902, -3, 'MCT-420FLD', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-903, -6, 'MCT-420FLD', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-904, -34, 'MCT-420FLD', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-905, -82, 'MCT-420FLD', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-906, -18, 'MCT-420FLD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-907, -19, 'MCT-420FLD', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-908, -83, 'MCT-420FLD', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-909, -84, 'MCT-420FLD', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-910, -11, 'MCT-420FLD', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-911, -12, 'MCT-420FLD', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-912, -13, 'MCT-420FLD', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-913, -106, 'MCT-420FLD', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-914, -107, 'MCT-420FLD', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-915, -108, 'MCT-420FLD', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-916, -111, 'MCT-420FLD', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-917, -114, 'MCT-420FLD', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-918, -15, 'MCT-420FLD', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-919, -130, 'MCT-420FLD', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-920, -131, 'MCT-420FLD', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-921, -132, 'MCT-420FLD', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-922, -136, 'MCT-420FLD', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-923, -137, 'MCT-420FLD', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-924, -138, 'MCT-420FLD', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-925, -139, 'MCT-420FLD', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-926, -140, 'MCT-420FLD', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-927, -141, 'MCT-420FLD', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-928, -142, 'MCT-420FLD', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-929, -154, 'MCT-420FLD', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-930, -155, 'MCT-420FLD', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-931, -156, 'MCT-420FLD', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-932, -157, 'MCT-420FLD', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-933, -169, 'MCT-420FLD', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-934, -170, 'MCT-420FLD', 35, 'Y', -1); 

INSERT INTO DeviceTypeCommand VALUES (-940, -1, 'MCT-420FL', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-941, -81, 'MCT-420FL', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-942, -3, 'MCT-420FL', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-943, -6, 'MCT-420FL', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-944, -34, 'MCT-420FL', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-945, -82, 'MCT-420FL', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-946, -18, 'MCT-420FL', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-947, -19, 'MCT-420FL', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-948, -83, 'MCT-420FL', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-949, -84, 'MCT-420FL', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-950, -11, 'MCT-420FL', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-951, -12, 'MCT-420FL', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-952, -13, 'MCT-420FL', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-953, -106, 'MCT-420FL', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-954, -107, 'MCT-420FL', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-955, -108, 'MCT-420FL', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-956, -111, 'MCT-420FL', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-957, -114, 'MCT-420FL', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-958, -15, 'MCT-420FL', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-959, -130, 'MCT-420FL', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-960, -131, 'MCT-420FL', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-961, -132, 'MCT-420FL', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-962, -136, 'MCT-420FL', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-963, -137, 'MCT-420FL', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-964, -138, 'MCT-420FL', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-965, -139, 'MCT-420FL', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-966, -140, 'MCT-420FL', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-967, -141, 'MCT-420FL', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-968, -142, 'MCT-420FL', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-969, -154, 'MCT-420FL', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-970, -155, 'MCT-420FL', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-971, -156, 'MCT-420FL', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-972, -157, 'MCT-420FL', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-973, -169, 'MCT-420FL', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-974, -170, 'MCT-420FL', 35, 'Y', -1);

/*==============================================================*/
/* Index: Indx_DevTypeCmd_GroupID                               */
/*==============================================================*/
create index Indx_DevTypeCmd_GroupID on DeviceTypeCommand (
   CommandGroupID ASC
);

/*==============================================================*/
/* Table: DeviceVerification                                    */
/*==============================================================*/
create table DeviceVerification  (
   ReceiverID           NUMBER                          not null,
   TransmitterID        NUMBER                          not null,
   ResendOnFail         CHAR(1)                         not null,
   Disable              CHAR(1)                         not null,
   constraint PK_DEVICEVERIFICATION primary key (ReceiverID, TransmitterID)
);

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
);

/*==============================================================*/
/* Table: DigiGateway                                           */
/*==============================================================*/
create table DigiGateway  (
   DeviceId             NUMBER                          not null,
   DigiId               NUMBER                          not null,
   constraint PK_DigiGate primary key (DeviceId)
);

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
   AssumedStartVerificationStatus NUMBER                          not null,
   PrevVerificationControlStatus NUMBER                          not null,
   VerificationControlIndex NUMBER                          not null,
   AdditionalFlags      VARCHAR2(32)                    not null,
   CurrentDailyOperations NUMBER                          not null,
   TwoWayCBCState       NUMBER                          not null,
   TwoWayCBCStateTime   DATE                            not null,
   beforeVar            VARCHAR2(48)                    not null,
   afterVar             VARCHAR2(48)                    not null,
   changeVar            VARCHAR2(48)                    not null,
   twoWayCBCLastControl NUMBER                          not null,
   PartialPhaseInfo     VARCHAR2(20)                    not null,
   constraint PK_DYNAMICCCCAPBANK primary key (CapBankID)
);

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
   phaseavalue          FLOAT                           not null,
   phasebvalue          FLOAT                           not null,
   phasecvalue          FLOAT                           not null,
   LastWattPointTime    DATE                            not null,
   LastVoltPointTime    DATE                            not null,
   retryIndex           NUMBER                          not null,
   PhaseAValueBeforeControl FLOAT                           not null,
   PhaseBValueBeforeControl FLOAT                           not null,
   PhaseCValueBeforeControl FLOAT                           not null,
   constraint PK_DYNAMICCCFEEDER primary key (FeederID)
);

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
);

/*==============================================================*/
/* Table: DynamicCCMonitorPointResponse                         */
/*==============================================================*/
create table DynamicCCMonitorPointResponse  (
   BankID               NUMBER                          not null,
   PointID              NUMBER                          not null,
   PreOpValue           FLOAT                           not null,
   Delta                FLOAT                           not null,
   StaticDelta          CHAR(1)                         not null,
   constraint PK_DYNAMICCCMONITORPOINTRESPON primary key (BankID, PointID)
);

/*==============================================================*/
/* Table: DynamicCCOriginalParent                               */
/*==============================================================*/
create table DynamicCCOriginalParent  (
   PAObjectId           NUMBER                          not null,
   OriginalParentId     NUMBER                          not null,
   OriginalSwitchingOrder FLOAT                           not null,
   OriginalCloseOrder   FLOAT                           not null,
   OriginalTripOrder    FLOAT                           not null,
   constraint PK_DynCCOrigParent primary key (PAObjectId)
);

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
   phaseavalue          FLOAT                           not null,
   phasebvalue          FLOAT                           not null,
   phasecvalue          FLOAT                           not null,
   LastWattPointTime    DATE                            not null,
   LastVoltPointTime    DATE                            not null,
   PhaseAValueBeforeControl FLOAT                           not null,
   PhaseBValueBeforeControl FLOAT                           not null,
   PhaseCValueBeforeControl FLOAT                           not null,
   constraint PK_DYNAMICCCSUBSTATIONBUS primary key (SubstationBusID)
);

/*==============================================================*/
/* Table: DynamicCalcHistorical                                 */
/*==============================================================*/
create table DynamicCalcHistorical  (
   PointID              NUMBER                          not null,
   LastUpdate           DATE                            not null,
   constraint PK_DYNAMICCALCHISTORICAL primary key (PointID)
);

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
);

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
);

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
);

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
   ProtocolPriority     NUMBER(9,0)                     not null,
   constraint PK_DYNLMCONTROLHISTORY primary key (PAObjectID)
);

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
);

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
);

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
   CurrentLogId         NUMBER                         default 0 not null,
   constraint PK_DYNAMICLMPROGRAMDIRECT primary key (DeviceID)
);

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
);

alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique (PAObjectID, Owner, InfoKey);

/*==============================================================*/
/* Table: DynamicPAOStatistics                                  */
/*==============================================================*/
create table DynamicPAOStatistics  (
   DynamicPAOStatisticsId NUMBER                          not null,
   PAObjectId           NUMBER                          not null,
   StatisticType        VARCHAR2(16)                    not null,
   StartDateTime        DATE                            not null,
   Requests             NUMBER                          not null,
   Attempts             NUMBER                          not null,
   Completions          NUMBER                          not null,
   CommErrors           NUMBER                          not null,
   ProtocolErrors       NUMBER                          not null,
   SystemErrors         NUMBER                          not null,
   constraint PK_DynPAOStat primary key (DynamicPAOStatisticsId)
);

/*==============================================================*/
/* Index: Indx_DynPAOStat_PId_ST_SD_UNQ                         */
/*==============================================================*/
create unique index Indx_DynPAOStat_PId_ST_SD_UNQ on DynamicPAOStatistics (
   PAObjectId ASC,
   StatisticType ASC,
   StartDateTime ASC
);

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
);

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
);

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
);

/*==============================================================*/
/* Index: Index_DYNVER_CS                                       */
/*==============================================================*/
create index Index_DYNVER_CS on DynamicVerification (
   CodeSequence ASC
);

/*==============================================================*/
/* Index: Indx_DYNV_TIME                                        */
/*==============================================================*/
create index Indx_DYNV_TIME on DynamicVerification (
   TimeArrival ASC
);

/*==============================================================*/
/* Index: Indx_DynVer_TimeArr_Code                              */
/*==============================================================*/
create index Indx_DynVer_TimeArr_Code on DynamicVerification (
   TimeArrival ASC,
   Code ASC
);

/*==============================================================*/
/* Table: ECToAccountMapping                                    */
/*==============================================================*/
create table ECToAccountMapping  (
   EnergyCompanyID      NUMBER                          not null,
   AccountID            NUMBER                          not null,
   constraint PK_ECTOACCOUNTMAPPING primary key (EnergyCompanyID, AccountID)
);

/*==============================================================*/
/* Table: ECToAcctThermostatSchedule                            */
/*==============================================================*/
create table ECToAcctThermostatSchedule  (
   EnergyCompanyId      NUMBER                          not null,
   AcctThermostatScheduleId NUMBER                          not null,
   constraint PK_ECToAcctThermSch primary key (EnergyCompanyId, AcctThermostatScheduleId)
);

/*==============================================================*/
/* Table: ECToCallReportMapping                                 */
/*==============================================================*/
create table ECToCallReportMapping  (
   EnergyCompanyID      NUMBER                          not null,
   CallReportID         NUMBER                          not null,
   constraint PK_ECTOCALLREPORTMAPPING primary key (EnergyCompanyID, CallReportID)
);

/*==============================================================*/
/* Table: ECToGenericMapping                                    */
/*==============================================================*/
create table ECToGenericMapping  (
   EnergyCompanyID      NUMBER                          not null,
   ItemID               NUMBER                          not null,
   MappingCategory      VARCHAR2(40)                    not null,
   constraint PK_ECTOGENERICMAPPING primary key (EnergyCompanyID, ItemID, MappingCategory)
);

INSERT INTO ECToGenericMapping VALUES(-1,-1,'LMThermostatSchedule');

/*==============================================================*/
/* Table: ECToInventoryMapping                                  */
/*==============================================================*/
create table ECToInventoryMapping  (
   EnergyCompanyID      NUMBER                          not null,
   InventoryID          NUMBER                          not null,
   constraint PK_ECTOINVENTORYMAPPING primary key (EnergyCompanyID, InventoryID)
);

/*==============================================================*/
/* Index: INDX_ECToInvMap_InvId                                 */
/*==============================================================*/
create index INDX_ECToInvMap_InvId on ECToInventoryMapping (
   InventoryID ASC
);

/*==============================================================*/
/* Table: ECToLMCustomerEventMapping                            */
/*==============================================================*/
create table ECToLMCustomerEventMapping  (
   EnergyCompanyID      NUMBER                          not null,
   EventID              NUMBER                          not null,
   constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key (EnergyCompanyID, EventID)
);

/*==============================================================*/
/* Index: INDX_ECToCustEventMap_EventId                         */
/*==============================================================*/
create index INDX_ECToCustEventMap_EventId on ECToLMCustomerEventMapping (
   EventID ASC
);

/*==============================================================*/
/* Table: ECToOperatorGroupMapping                              */
/*==============================================================*/
create table ECToOperatorGroupMapping  (
   EnergyCompanyId      NUMBER                          not null,
   GroupId              NUMBER                          not null,
   constraint PK_ECToOpGroupMap primary key (EnergyCompanyId, GroupId)
);

/*==============================================================*/
/* Table: ECToResidentialGroupMapping                           */
/*==============================================================*/
create table ECToResidentialGroupMapping  (
   EnergyCompanyId      NUMBER                          not null,
   GroupId              NUMBER                          not null,
   constraint PK_ECToResGroupMap primary key (EnergyCompanyId, GroupId)
);

/*==============================================================*/
/* Table: ECToRouteMapping                                      */
/*==============================================================*/
create table ECToRouteMapping  (
   EnergyCompanyId      NUMBER                          not null,
   RouteId              NUMBER                          not null,
   constraint PK_ECToRouteMap primary key (EnergyCompanyId, RouteId)
);

/*==============================================================*/
/* Table: ECToSubstationMapping                                 */
/*==============================================================*/
create table ECToSubstationMapping  (
   EnergyCompanyId      NUMBER                          not null,
   SubstationId         NUMBER                          not null,
   constraint PK_ECToSubMap primary key (EnergyCompanyId, SubstationId)
);

/*==============================================================*/
/* Table: ECToWorkOrderMapping                                  */
/*==============================================================*/
create table ECToWorkOrderMapping  (
   EnergyCompanyID      NUMBER                          not null,
   WorkOrderID          NUMBER                          not null,
   constraint PK_ECTOWORKORDERMAPPING primary key (EnergyCompanyID, WorkOrderID)
);

/*==============================================================*/
/* Table: EnergyCompany                                         */
/*==============================================================*/
create table EnergyCompany  (
   EnergyCompanyID      NUMBER                          not null,
   Name                 VARCHAR2(60)                    not null,
   PrimaryContactID     NUMBER                          not null,
   UserID               NUMBER                          not null,
   constraint PK_ENERGYCOMPANY primary key (EnergyCompanyID)
);

insert into EnergyCompany VALUES (-1,'Default Energy Company',0,-100);

/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique index Indx_EnCmpName on EnergyCompany (
   Name ASC
);

/*==============================================================*/
/* Table: EnergyCompanyCustomerList                             */
/*==============================================================*/
create table EnergyCompanyCustomerList  (
   EnergyCompanyID      NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key (EnergyCompanyID, CustomerID)
);

/*==============================================================*/
/* Index: INDX_ECompCustList_CustId                             */
/*==============================================================*/
create index INDX_ECompCustList_CustId on EnergyCompanyCustomerList (
   CustomerID ASC
);

/*==============================================================*/
/* Table: EnergyCompanyOperatorLoginList                        */
/*==============================================================*/
create table EnergyCompanyOperatorLoginList  (
   EnergyCompanyID      NUMBER                          not null,
   OperatorLoginID      NUMBER                          not null,
   constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key (EnergyCompanyID, OperatorLoginID)
);

INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-100);

/*==============================================================*/
/* Table: EsubDisplayIndex                                      */
/*==============================================================*/
create table EsubDisplayIndex  (
   SearchKey            VARCHAR2(500)                   not null,
   DisplayUrl           VARCHAR2(500)                   not null,
   constraint PK_ESUBDISPLAYINDEX primary key (SearchKey)
);

/*==============================================================*/
/* Table: EventAccount                                          */
/*==============================================================*/
create table EventAccount  (
   EVENTID              NUMBER                          not null,
   AccountID            NUMBER                          not null,
   constraint PK_EVENTACCOUNT primary key (EVENTID)
);

/*==============================================================*/
/* Table: EventBase                                             */
/*==============================================================*/
create table EventBase  (
   EventID              NUMBER                          not null,
   UserID               NUMBER                          not null,
   SystemCategoryID     NUMBER                          not null,
   ActionID             NUMBER                          not null,
   EventTimestamp       DATE                            not null,
   constraint PK_EVENTBASE primary key (EventID)
);

insert into eventbase values (-1, -9999, 0, 0, '01-JAN-1970');

/*==============================================================*/
/* Table: EventInventory                                        */
/*==============================================================*/
create table EventInventory  (
   EventID              NUMBER                          not null,
   InventoryID          NUMBER                          not null,
   constraint PK_EVENTINVENTORY primary key (EventID)
);

/*==============================================================*/
/* Table: EventLog                                              */
/*==============================================================*/
create table EventLog  (
   EventLogId           NUMBER                          not null,
   EventType            VARCHAR2(250)                   not null,
   EventTime            DATE,
   String1              VARCHAR2(2000),
   String2              VARCHAR2(2000),
   String3              VARCHAR2(2000),
   String4              VARCHAR2(2000),
   String5              VARCHAR2(2000),
   String6              VARCHAR2(2000),
   Int7                 NUMBER,
   Int8                 NUMBER,
   Int9                 NUMBER,
   Int10                NUMBER,
   Date11               DATE,
   Date12               DATE,
   constraint PK_EventLog primary key (EventLogId)
);

/*==============================================================*/
/* Index: INDX_EventType                                        */
/*==============================================================*/
create index INDX_EventType on EventLog (
   EventType ASC
);

/*==============================================================*/
/* Table: EventWorkOrder                                        */
/*==============================================================*/
create table EventWorkOrder  (
   EventID              NUMBER                          not null,
   OrderID              NUMBER                          not null,
   constraint PK_EVENTWORKORDER primary key (EventID)
);

/*==============================================================*/
/* Table: ExtraPaoPointAssignment                               */
/*==============================================================*/
create table ExtraPaoPointAssignment  (
   PAObjectId           NUMBER                          not null,
   PointId              NUMBER                          not null,
   Attribute            VARCHAR2(255)                   not null,
   constraint PK_ExtraPAOPointAsgmt primary key (PAObjectId, Attribute)
);

/*==============================================================*/
/* Table: FDRInterface                                          */
/*==============================================================*/
create table FDRInterface  (
   InterfaceId          NUMBER                          not null,
   InterfaceName        VARCHAR2(30)                    not null,
   PossibleDirections   VARCHAR2(100)                   not null,
   hasDestination       CHAR(1)                         not null,
   constraint PK_FDRINTERFACE primary key (InterfaceId)
);

INSERT INTO FDRInterface VALUES ( 1, 'INET', 'Send,Send for control,Receive,Receive for control', 't' );
INSERT INTO FDRInterface VALUES ( 2, 'ACS', 'Send,Send for control,Receive,Receive for control', 'f' );
INSERT INTO FDRInterface VALUES ( 3, 'VALMET', 'Send,Send for control,Receive,Receive for control,Receive for Analog Output', 'f' );
INSERT INTO FDRInterface VALUES ( 4, 'CYGNET', 'Send,Send for control,Receive,Receive for control', 'f' );
INSERT INTO FDRInterface VALUES ( 5, 'STEC', 'Receive', 'f' );
INSERT INTO FDRInterface VALUES ( 6, 'RCCS', 'Send,Send for control,Receive,Receive for control', 't' );
INSERT INTO FDRInterface VALUES ( 7, 'TRISTATE', 'Receive', 'f' );
INSERT INTO FDRInterface VALUES ( 8, 'RDEX', 'Send,Send for control,Receive,Receive for control', 't' );
INSERT INTO FDRInterface VALUES ( 9, 'SYSTEM','Link Status','f');
INSERT INTO FDRInterface VALUES (10, 'DSM2IMPORT','Receive,Receive for control','f');
INSERT INTO FDRInterface VALUES (11, 'TELEGYR','Receive,Receive for control','f');
INSERT INTO FDRInterface VALUES (12, 'TEXTIMPORT','Receive,Receive for control,Receive for Analog Output','f');
INSERT INTO FDRInterface VALUES (13, 'TEXTEXPORT','Send','f');
INSERT INTO FDRInterface VALUES (16, 'LODESTAR_STD','Receive','f');
INSERT INTO FDRInterface VALUES (17, 'LODESTAR_ENH','Receive','f');
INSERT INTO FDRInterface VALUES (18, 'DSM2FILEIN', 'Receive,Receive for control', 'f');
INSERT INTO FDRInterface VALUES (19, 'XA21LM','Receive,Send', 't' );
INSERT INTO FDRInterface VALUES (20, 'BEPC','Send','f');
INSERT INTO FDRInterface VALUES (21, 'PI','Receive', 't' );
INSERT INTO FDRInterface VALUES (22, 'LIVEDATA','Receive', 'f' );
INSERT INTO FDRInterface VALUES (23, 'ACSMULTI', 'Send,Send for control,Receive,Receive for control', 't' );
INSERT INTO FDRInterface VALUES (24, 'WABASH', 'Send', 'f' );
INSERT INTO FDRInterface VALUES (25, 'TRISTATESUB', 'Receive,Send', 'f' );
INSERT INTO FDRInterface VALUES (26, 'OPC', 'Receive,Send', 'f');
INSERT INTO FDRInterface VALUES (27, 'MULTISPEAK_LM', 'Receive', 'f'); 
INSERT INTO FDRInterface VALUES (28, 'DNPSLAVE', 'Send', 't');

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
);

INSERT INTO FDRInterfaceOption VALUES(1, 'Device', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(1, 'Point', 2, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(1, 'Destination/Source', 3, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(2, 'Category', 1, 'Combo', 'PSEUDO,REAL,CALCULATED' );
INSERT INTO FDRInterfaceOption VALUES(2, 'Remote', 2, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(2, 'Point', 3, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(3, 'Point', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(4, 'PointID', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(5, 'Point', 1, 'Combo', 'SYSTEM LOAD,STEC LOAD' );
INSERT INTO FDRInterfaceOption VALUES(6, 'Device', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(6, 'Point', 2, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(6, 'Destination/Source', 3, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(7, 'Point', 1, 'Combo', 'SYSTEM LOAD,30 MINUTE AVG' );
INSERT INTO FDRInterfaceOption VALUES(8, 'Translation', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(8, 'Destination/Source', 2, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(9, 'Client',1,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(10, 'Point',1,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(11, 'Point', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(11, 'Interval (sec)', 2, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(12, 'Point ID',1,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(12,'DrivePath',2,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(12,'Filename',3,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(13, 'Point ID',1,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(16, 'Customer',1,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(16, 'Channel',2,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(16, 'DrivePath',3,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(16, 'Filename',4,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(17, 'Customer',1,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(17, 'Channel',2,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(17, 'DrivePath',3,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(17, 'Filename',4,'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(18, 'Option Number', 1, 'Combo', '1');
INSERT INTO FDRInterfaceOption VALUES(18, 'Point ID', 2, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(19, 'Translation', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption Values(20, 'Coop Id', 1, 'Text','(none)');
INSERT INTO FDRInterfaceOption Values(20, 'Filename', 2, 'Text','(none)');
INSERT INTO FDRInterfaceOption VALUES(21, 'Tag Name', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(21, 'Period (sec)', 2, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(22, 'Address', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(22, 'Data Type', 2, 'Combo', 'Data_RealExtended,Data_DiscreteExtended,Data_StateExtended,Data_RealQ,Data_DiscreteQ,Data_State,Data_Discrete,Data_Real,Data_RealQTimeTag,Data_StateQTimeTag,Data_DiscreteQTimeTag' );
INSERT INTO FDRInterfaceOption VALUES(23, 'Category', 1, 'Combo', 'PSEUDO,REAL,CALCULATED' );
INSERT INTO FDRInterfaceOption VALUES(23, 'Remote', 2, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(23, 'Point', 3, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(23, 'Destination/Source', 4, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(24, 'SchedName', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(24, 'Path', 2, 'Text', 'c:\yukon\server\export\' );
INSERT INTO FDRInterfaceOption VALUES(24, 'Filename', 3, 'Text', 'control.txt' );
INSERT INTO FDRInterfaceOption VALUES(25, 'Point', 1, 'Combo', 'Nucla 115/69 Xfmr.,Happy Canyon 661Idarado,Cascade 115/69 (T2),Ames Generation,Dallas Creek MW,Dallas Creek MV' );
INSERT INTO FDRInterfaceOption VALUES(26, 'Server Name', 1, 'Text', '(none)'); 
INSERT INTO FDRInterfaceOption VALUES(26, 'OPC Group', 2, 'Text', '(none)'); 
INSERT INTO FDRInterfaceOption VALUES(26, 'OPC Item', 3, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(27, 'ObjectId', 1, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'MasterId', 1, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'SlaveId', 2, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'Offset', 3, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'Destination/Source', 4, 'Text', '(none)');
INSERT INTO FDRInterfaceOption VALUES(28, 'Multiplier', 5, 'Text', '1.0');

/*==============================================================*/
/* Table: FDRTranslation                                        */
/*==============================================================*/
create table FDRTranslation  (
   PointId              NUMBER                          not null,
   DirectionType        VARCHAR2(30)                    not null,
   InterfaceType        VARCHAR2(20)                    not null,
   Destination          VARCHAR2(20)                    not null,
   Translation          VARCHAR2(200)                   not null,
   constraint PK_FDRTrans primary key (PointId, DirectionType, InterfaceType, Translation)
);

/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create index Indx_FdrTransIntTyp on FDRTranslation (
   InterfaceType ASC
);

/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create index Indx_FdrTrnsIntTypDir on FDRTranslation (
   DirectionType ASC,
   InterfaceType ASC
);

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
);

/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create index Indx_GrpDSerPtID on GRAPHDATASERIES (
   POINTID ASC
);

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
);

alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique (NAME);

/*==============================================================*/
/* Table: GenericMacro                                          */
/*==============================================================*/
create table GenericMacro  (
   OwnerID              NUMBER                          not null,
   ChildID              NUMBER                          not null,
   ChildOrder           NUMBER                          not null,
   MacroType            VARCHAR2(20)                    not null,
   constraint PK_GENERICMACRO primary key (OwnerID, ChildOrder, MacroType)
);

/*==============================================================*/
/* Table: GraphCustomerList                                     */
/*==============================================================*/
create table GraphCustomerList  (
   GraphDefinitionID    NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   constraint PK_GRAPHCUSTOMERLIST primary key (GraphDefinitionID, CustomerID)
);

/*==============================================================*/
/* Table: GroupPaoPermission                                    */
/*==============================================================*/
create table GroupPaoPermission  (
   GroupPaoPermissionID NUMBER                          not null,
   GroupID              NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Permission           VARCHAR2(50)                    not null,
   Allow                VARCHAR2(5)                     not null,
   constraint PK_GROUPPAOPERMISSION primary key (GroupPaoPermissionID)
);

alter table GroupPaoPermission
   add constraint AK_GRPPAOPERM unique (GroupID, PaoID, Permission);

/*==============================================================*/
/* Table: HolidaySchedule                                       */
/*==============================================================*/
create table HolidaySchedule  (
   HolidayScheduleID    NUMBER                          not null,
   HolidayScheduleName  VARCHAR2(40)                    not null,
   constraint PK_HOLIDAYSCHEDULE primary key (HolidayScheduleID)
);

insert into HolidaySchedule values( 0, 'Empty Holiday Schedule' );
insert into HolidaySchedule values(-1, 'No Holiday');

/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique index Indx_HolSchName on HolidaySchedule (
   HolidayScheduleName ASC
);

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
);

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
);

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
);

/*==============================================================*/
/* Table: InventoryBase                                         */
/*==============================================================*/
create table InventoryBase  (
   InventoryID          NUMBER                          not null,
   AccountID            NUMBER,
   InstallationCompanyID NUMBER,
   CategoryID           NUMBER                          not null,
   ReceiveDate          DATE,
   InstallDate          DATE,
   RemoveDate           DATE,
   AlternateTrackingNumber VARCHAR2(40),
   VoltageID            NUMBER,
   Notes                VARCHAR2(500),
   DeviceID             NUMBER,
   DeviceLabel          VARCHAR2(60),
   CurrentStateID       NUMBER                          not null,
   constraint PK_INVENTORYBASE primary key (InventoryID)
);

INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0,'(none)',0);

/*==============================================================*/
/* Index: CstAccCstHrdB_FK                                      */
/*==============================================================*/
create index CstAccCstHrdB_FK on InventoryBase (
   AccountID ASC
);

/*==============================================================*/
/* Index: HrdInst_CstHrdBs_FK                                   */
/*==============================================================*/
create index HrdInst_CstHrdBs_FK on InventoryBase (
   InstallationCompanyID ASC
);

/*==============================================================*/
/* Table: InventoryConfigTask                                   */
/*==============================================================*/
create table InventoryConfigTask  (
   InventoryConfigTaskId NUMBER                          not null,
   TaskName             VARCHAR2(255)                   not null,
   SendInService        CHAR(1)                         not null,
   NumberOfItems        NUMBER                          not null,
   NumberOfItemsProcessed NUMBER                          not null,
   EnergyCompanyId      NUMBER                          not null,
   UserId               NUMBER                          not null,
   constraint PK_InvConfTask primary key (InventoryConfigTaskId)
);

/*==============================================================*/
/* Index: Indx_InvConfTa_EC_TaskName_UNQ                        */
/*==============================================================*/
create unique index Indx_InvConfTa_EC_TaskName_UNQ on InventoryConfigTask (
   TaskName ASC,
   EnergyCompanyId ASC
);

/*==============================================================*/
/* Table: InventoryConfigTaskItem                               */
/*==============================================================*/
create table InventoryConfigTaskItem  (
   InventoryConfigTaskId NUMBER                          not null,
   InventoryId          NUMBER                          not null,
   Status               VARCHAR2(16)                    not null,
   constraint PK_InvConfTaskItem primary key (InventoryConfigTaskId, InventoryId)
);

/*==============================================================*/
/* Table: InventoryToAcctThermostatSch                          */
/*==============================================================*/
create table InventoryToAcctThermostatSch  (
   InventoryId          NUMBER                          not null,
   AcctThermostatScheduleId NUMBER                          not null,
   constraint PK_InvToAcctThermSch primary key (InventoryId)
);

/*==============================================================*/
/* Table: InventoryToWarehouseMapping                           */
/*==============================================================*/
create table InventoryToWarehouseMapping  (
   WarehouseID          NUMBER                          not null,
   InventoryID          NUMBER                          not null,
   constraint PK_INVENTORYTOWAREHOUSEMAPPING primary key (WarehouseID, InventoryID)
);

/*==============================================================*/
/* Table: Invoice                                               */
/*==============================================================*/
create table Invoice  (
   InvoiceID            NUMBER                          not null,
   PurchasePlanID       NUMBER                          not null,
   InvoiceDesignation   VARCHAR2(60)                    not null,
   DateSubmitted        DATE                            not null,
   Authorized           VARCHAR2(1)                     not null,
   AuthorizedBy         VARCHAR2(30)                    not null,
   HasPaid              VARCHAR2(1)                     not null,
   DatePaid             DATE                            not null,
   TotalQuantity        NUMBER                          not null,
   AuthorizedNumber     VARCHAR2(60)                    not null,
   constraint PK_INVOICE primary key (InvoiceID)
);

/*==============================================================*/
/* Table: InvoiceShipmentMapping                                */
/*==============================================================*/
create table InvoiceShipmentMapping  (
   InvoiceID            NUMBER                          not null,
   ShipmentID           NUMBER                          not null,
   constraint PK_INVOICESHIPMENTMAPPING primary key (InvoiceID, ShipmentID)
);

/*==============================================================*/
/* Table: JOB                                                   */
/*==============================================================*/
create table JOB  (
   JobID                INTEGER                         not null,
   BeanName             VARCHAR2(250)                   not null,
   Disabled             CHAR(1)                         not null,
   UserID               NUMBER                          not null,
   Locale               VARCHAR2(10)                    not null,
   TimeZone             VARCHAR2(40)                    not null,
   themeName            VARCHAR2(60)                    not null,
   constraint PK_JOB primary key (JobID)
);

/*==============================================================*/
/* Table: JOBPROPERTY                                           */
/*==============================================================*/
create table JOBPROPERTY  (
   JobPropertyID        NUMBER                          not null,
   JobID                INTEGER                         not null,
   name                 VARCHAR2(100)                   not null,
   value                VARCHAR2(1000)                  not null,
   constraint PK_JOBPROPERTY primary key (JobPropertyID)
);

/*==============================================================*/
/* Table: JOBSCHEDULEDONETIME                                   */
/*==============================================================*/
create table JOBSCHEDULEDONETIME  (
   JobID                INTEGER                         not null,
   StartTime            DATE                            not null,
   constraint PK_JOBSCHEDULEDONETIME primary key (JobID)
);

/*==============================================================*/
/* Table: JOBSCHEDULEDREPEATING                                 */
/*==============================================================*/
create table JOBSCHEDULEDREPEATING  (
   JobID                INTEGER                         not null,
   CronString           VARCHAR2(100)                   not null,
   constraint PK_JobScheduledRepeating primary key (JobID)
);

/*==============================================================*/
/* Table: JOBSTATUS                                             */
/*==============================================================*/
create table JOBSTATUS  (
   JobStatusID          INTEGER                         not null,
   JobID                INTEGER                         not null,
   StartTime            DATE                            not null,
   StopTime             DATE,
   JobState             VARCHAR2(50),
   Message              VARCHAR2(1000),
   constraint PK_JOBSTATUS primary key (JobStatusID)
);

/*==============================================================*/
/* Table: LMCONTROLAREAPROGRAM                                  */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM  (
   DEVICEID             NUMBER                          not null,
   LMPROGRAMDEVICEID    NUMBER                          not null,
   StartPriority        NUMBER                          not null,
   StopPriority         NUMBER                          not null,
   constraint PK_LMCONTROLAREAPROGRAM primary key (DEVICEID, LMPROGRAMDEVICEID)
);

/*==============================================================*/
/* Table: LMConfigurationBase                                   */
/*==============================================================*/
create table LMConfigurationBase  (
   ConfigurationID      NUMBER                          not null,
   ColdLoadPickup       VARCHAR2(128)                   not null,
   TamperDetect         VARCHAR2(128)                   not null,
   constraint PK_LMCONFIGURATIONBASE primary key (ConfigurationID)
);

insert into LMConfigurationBase values (0, '(none)', '(none)');

/*==============================================================*/
/* Table: LMConfigurationExpressCom                             */
/*==============================================================*/
create table LMConfigurationExpressCom  (
   ConfigurationID      NUMBER                          not null,
   ServiceProvider      NUMBER                          not null,
   GEO                  NUMBER                          not null,
   Substation           NUMBER                          not null,
   Feeder               NUMBER                          not null,
   Zip                  NUMBER                          not null,
   UserAddress          NUMBER                          not null,
   Program              VARCHAR2(80)                    not null,
   Splinter             VARCHAR2(80)                    not null,
   constraint PK_LMCONFIGURATIONEXPRESSCOM primary key (ConfigurationID)
);

/*==============================================================*/
/* Table: LMConfigurationSA205                                  */
/*==============================================================*/
create table LMConfigurationSA205  (
   ConfigurationID      NUMBER                          not null,
   Slot1                NUMBER                          not null,
   Slot2                NUMBER                          not null,
   Slot3                NUMBER                          not null,
   Slot4                NUMBER                          not null,
   Slot5                NUMBER                          not null,
   Slot6                NUMBER                          not null,
   constraint PK_LMCONFIGURATIONSA205 primary key (ConfigurationID)
);

/*==============================================================*/
/* Table: LMConfigurationSA305                                  */
/*==============================================================*/
create table LMConfigurationSA305  (
   ConfigurationID      NUMBER                          not null,
   Utility              NUMBER                          not null,
   GroupAddress         NUMBER                          not null,
   Division             NUMBER                          not null,
   Substation           NUMBER                          not null,
   RateFamily           NUMBER                          not null,
   RateMember           NUMBER                          not null,
   RateHierarchy        NUMBER                          not null,
   constraint PK_LMCONFIGURATIONSA305 primary key (ConfigurationID)
);

/*==============================================================*/
/* Table: LMConfigurationSASimple                               */
/*==============================================================*/
create table LMConfigurationSASimple  (
   ConfigurationID      NUMBER                          not null,
   OperationalAddress   NUMBER                          not null,
   constraint PK_LMCONFIGURATIONSASIMPLE primary key (ConfigurationID)
);

/*==============================================================*/
/* Table: LMConfigurationVersacom                               */
/*==============================================================*/
create table LMConfigurationVersacom  (
   ConfigurationID      NUMBER                          not null,
   UtilityID            NUMBER                          not null,
   Section              NUMBER                          not null,
   ClassAddress         NUMBER                          not null,
   DivisionAddress      NUMBER                          not null,
   constraint PK_LMCONFIGURATIONVERSACOM primary key (ConfigurationID)
);

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
);

/*==============================================================*/
/* Table: LMControlAreaTrigger                                  */
/*==============================================================*/
create table LMControlAreaTrigger  (
   DeviceId             NUMBER                          not null,
   TriggerNumber        NUMBER                          not null,
   TriggerType          VARCHAR2(20)                    not null,
   PointId              NUMBER                          not null,
   NormalState          NUMBER                          not null,
   Threshold            FLOAT                           not null,
   ProjectionType       VARCHAR2(14)                    not null,
   ProjectionPoints     NUMBER                          not null,
   ProjectAheadDuration NUMBER                          not null,
   ThresholdKickPercent NUMBER                          not null,
   MinRestoreOffset     FLOAT                           not null,
   PeakPointId          NUMBER                          not null,
   TriggerId            NUMBER                          not null,
   ThresholdPointId     NUMBER                          not null,
   constraint PK_LMCONTROLAREATRIGGER primary key (DeviceId, TriggerNumber)
);

/*==============================================================*/
/* Index: INDX_UNQ_LMCNTRTR_TRID                                */
/*==============================================================*/
create unique index INDX_UNQ_LMCNTRTR_TRID on LMControlAreaTrigger (
   TriggerId ASC
);

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
   ProtocolPriority     NUMBER(9,0)                     not null,
   constraint PK_LMCONTROLHISTORY primary key (LMCtrlHistID)
);

/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create index Indx_Start on LMControlHistory (
   StartDateTime ASC
);

/*==============================================================*/
/* Index: Indx_LMContHist_SOE_Tag                               */
/*==============================================================*/
create index Indx_LMContHist_SOE_Tag on LMControlHistory (
   SOE_Tag ASC
);

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
);

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
);

/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create index Index_LMCrtCstActID on LMCurtailCustomerActivity (
   CustomerID ASC
);

/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
   AcknowledgeStatus ASC
);

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
);

/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
   CurtailmentStartTime ASC
);

/*==============================================================*/
/* Table: LMCustomerEventBase                                   */
/*==============================================================*/
create table LMCustomerEventBase  (
   EventID              NUMBER                          not null,
   EventTypeID          NUMBER                          not null,
   ActionID             NUMBER                          not null,
   EventDateTime        DATE,
   Notes                VARCHAR2(500),
   AuthorizedBy         VARCHAR2(40),
   constraint PK_LMCUSTOMEREVENTBASE primary key (EventID)
);

/*==============================================================*/
/* Table: LMDirectCustomerList                                  */
/*==============================================================*/
create table LMDirectCustomerList  (
   ProgramID            NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   constraint PK_LMDIRECTCUSTOMERLIST primary key (ProgramID, CustomerID)
);

/*==============================================================*/
/* Table: LMDirectNotifGrpList                                  */
/*==============================================================*/
create table LMDirectNotifGrpList  (
   ProgramID            NUMBER                          not null,
   NotificationGrpID    NUMBER                          not null,
   constraint PK_LMDIRECTNOTIFGRPLIST primary key (ProgramID, NotificationGrpID)
);

/*==============================================================*/
/* Table: LMEnergyExchangeCustomerList                          */
/*==============================================================*/
create table LMEnergyExchangeCustomerList  (
   ProgramID            NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key (ProgramID, CustomerID)
);

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
);

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
);

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
);

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
);

/*==============================================================*/
/* Table: LMEnergyExchangeProgramOffer                          */
/*==============================================================*/
create table LMEnergyExchangeProgramOffer  (
   DeviceID             NUMBER                          not null,
   OfferID              NUMBER                          not null,
   RunStatus            VARCHAR2(20)                    not null,
   OfferDate            DATE                            not null,
   constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key (OfferID)
);

/*==============================================================*/
/* Table: LMGroup                                               */
/*==============================================================*/
create table LMGroup  (
   DeviceID             NUMBER                          not null,
   KWCapacity           FLOAT                           not null,
   constraint PK_LMGROUP primary key (DeviceID)
);

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
);

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
   ProtocolPriority     NUMBER(9,0)                     not null,
   constraint PK_LMGROUPEXPRESSCOM primary key (LMGroupID)
);

/*==============================================================*/
/* Table: LMGroupExpressComAddress                              */
/*==============================================================*/
create table LMGroupExpressComAddress  (
   AddressID            NUMBER                          not null,
   AddressType          VARCHAR2(20)                    not null,
   Address              NUMBER                          not null,
   AddressName          VARCHAR2(30)                    not null,
   constraint PK_LMGROUPEXPRESSCOMADDRESS primary key (AddressID)
);

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
);

/*==============================================================*/
/* Table: LMGroupPoint                                          */
/*==============================================================*/
create table LMGroupPoint  (
   DEVICEID             NUMBER                          not null,
   DeviceIDUsage        NUMBER                          not null,
   PointIDUsage         NUMBER                          not null,
   StartControlRawState NUMBER                          not null,
   constraint PK_LMGROUPPOINT primary key (DEVICEID)
);

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
);

/*==============================================================*/
/* Table: LMGroupSA205105                                       */
/*==============================================================*/
create table LMGroupSA205105  (
   GroupID              NUMBER                          not null,
   RouteID              NUMBER                          not null,
   OperationalAddress   NUMBER                          not null,
   LoadNumber           VARCHAR2(64)                    not null,
   constraint PK_LMGROUPSA205105 primary key (GroupID)
);

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
);

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
);

/*==============================================================*/
/* Table: LMGroupSep                                            */
/*==============================================================*/
create table LMGroupSep  (
   DeviceId             NUMBER                          not null,
   UtilityEnrollmentGroup NUMBER                          not null,
   constraint PK_LMGroupSep primary key (DeviceId)
);

/*==============================================================*/
/* Table: LMGroupSepDeviceClass                                 */
/*==============================================================*/
create table LMGroupSepDeviceClass  (
   DeviceId             NUMBER                          not null,
   SepDeviceClass       VARCHAR2(40)                    not null,
   constraint PK_LMGroupSepDeviceClass primary key (DeviceId, SepDeviceClass)
);

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
);

/*==============================================================*/
/* Table: LMHardwareBase                                        */
/*==============================================================*/
create table LMHardwareBase  (
   InventoryID          NUMBER                          not null,
   ManufacturerSerialNumber VARCHAR2(30),
   LMHardwareTypeID     NUMBER                          not null,
   RouteID              NUMBER                          not null,
   ConfigurationID      NUMBER                          not null,
   constraint PK_LMHARDWAREBASE primary key (InventoryID)
);

/*==============================================================*/
/* Index: Indx_LMHardBase_ManSerNum                             */
/*==============================================================*/
create index Indx_LMHardBase_ManSerNum on LMHardwareBase (
   ManufacturerSerialNumber ASC
);

/*==============================================================*/
/* Table: LMHardwareConfiguration                               */
/*==============================================================*/
create table LMHardwareConfiguration  (
   InventoryID          NUMBER                          not null,
   ApplianceID          NUMBER                          not null,
   AddressingGroupID    NUMBER,
   LoadNumber           NUMBER,
   constraint PK_LMHARDWARECONFIGURATION primary key (InventoryID, ApplianceID)
);

/*==============================================================*/
/* Index: LmHrd_LmHrdCfg_FK                                     */
/*==============================================================*/
create index LmHrd_LmHrdCfg_FK on LMHardwareConfiguration (
   InventoryID ASC
);

/*==============================================================*/
/* Index: CstLdIn_LMHrdCfg_FK                                   */
/*==============================================================*/
create index CstLdIn_LMHrdCfg_FK on LMHardwareConfiguration (
   ApplianceID ASC
);

/*==============================================================*/
/* Table: LMHardwareControlGroup                                */
/*==============================================================*/
create table LMHardwareControlGroup  (
   ControlEntryID       INTEGER                         not null,
   InventoryID          INTEGER                         not null,
   LMGroupID            INTEGER                         not null,
   AccountID            INTEGER                         not null,
   GroupEnrollStart     DATE,
   GroupEnrollStop      DATE,
   OptOutStart          DATE,
   OptOutStop           DATE,
   Type                 INTEGER                         not null,
   Relay                INTEGER                         not null,
   UserIDFirstAction    INTEGER                         not null,
   UserIDSecondAction   INTEGER                         not null,
   ProgramId            INTEGER                         not null,
   constraint PK_LMHARDWARECONTROLGROUP primary key (ControlEntryID)
);

/*==============================================================*/
/* Index: INDX_LMHardContGroup_AcctId                           */
/*==============================================================*/
create index INDX_LMHardContGroup_AcctId on LMHardwareControlGroup (
   AccountID ASC
);

/*==============================================================*/
/* Index: INDX_LMHardContGroup_InvId                            */
/*==============================================================*/
create index INDX_LMHardContGroup_InvId on LMHardwareControlGroup (
   InventoryID ASC
);

/*==============================================================*/
/* Table: LMHardwareEvent                                       */
/*==============================================================*/
create table LMHardwareEvent  (
   EventID              NUMBER                          not null,
   InventoryID          NUMBER                          not null,
   constraint PK_LMHARDWAREEVENT primary key (EventID)
);

/*==============================================================*/
/* Table: LMHardwareToMeterMapping                              */
/*==============================================================*/
create table LMHardwareToMeterMapping  (
   LMHardwareInventoryID NUMBER                          not null,
   MeterInventoryID     NUMBER                          not null,
   constraint PK_LMHARDWARETOMETERMAPPING primary key (LMHardwareInventoryID, MeterInventoryID)
);

/*==============================================================*/
/* Table: LMMacsScheduleCustomerList                            */
/*==============================================================*/
create table LMMacsScheduleCustomerList  (
   ScheduleID           NUMBER                          not null,
   LMCustomerDeviceID   NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key (ScheduleID, LMCustomerDeviceID)
);

/*==============================================================*/
/* Table: LMPROGRAM                                             */
/*==============================================================*/
create table LMPROGRAM  (
   DeviceID             NUMBER                          not null,
   ControlType          VARCHAR2(20)                    not null,
   ConstraintID         NUMBER                          not null,
   constraint PK_LMPROGRAM primary key (DeviceID)
);

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
);

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
);

/*==============================================================*/
/* Table: LMProgramCurtailCustomerList                          */
/*==============================================================*/
create table LMProgramCurtailCustomerList  (
   ProgramID            NUMBER                          not null,
   CustomerID           NUMBER                          not null,
   CustomerOrder        NUMBER                          not null,
   RequireAck           CHAR(1)                         not null,
   constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key (CustomerID, ProgramID)
);

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
);

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
);

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
);

alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique (DeviceID, GearNumber);

/*==============================================================*/
/* Table: LMProgramDirectGroup                                  */
/*==============================================================*/
create table LMProgramDirectGroup  (
   DeviceID             NUMBER                          not null,
   LMGroupDeviceID      NUMBER                          not null,
   GroupOrder           NUMBER                          not null,
   constraint PK_LMPROGRAMDIRECTGROUP primary key (DeviceID, GroupOrder)
);

/*==============================================================*/
/* Index: Indx_LMPDG_DevId_LMGrpDev_UNQ                         */
/*==============================================================*/
create unique index Indx_LMPDG_DevId_LMGrpDev_UNQ on LMProgramDirectGroup (
   DeviceID ASC,
   LMGroupDeviceID ASC
);

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
);

/*==============================================================*/
/* Table: LMProgramEvent                                        */
/*==============================================================*/
create table LMProgramEvent  (
   EventID              NUMBER                          not null,
   AccountID            NUMBER                          not null,
   ProgramID            NUMBER,
   constraint PK_LMPROGRAMEVENT primary key (EventID)
);

/*==============================================================*/
/* Index: INDX_LMProgEvent_AcctId_ProgId                        */
/*==============================================================*/
create index INDX_LMProgEvent_AcctId_ProgId on LMProgramEvent (
   AccountID ASC,
   ProgramID ASC
);

/*==============================================================*/
/* Table: LMProgramGearHistory                                  */
/*==============================================================*/
create table LMProgramGearHistory  (
   LMProgramGearHistoryId NUMBER                          not null,
   LMProgramHistoryId   NUMBER                          not null,
   EventTime            DATE                            not null,
   Action               VARCHAR2(50)                    not null,
   UserName             VARCHAR2(64)                    not null,
   GearName             VARCHAR2(30)                    not null,
   GearId               NUMBER                          not null,
   Reason               VARCHAR2(50)                    not null,
   constraint PK_LMPROGRAMGEARHISTORY primary key (LMProgramGearHistoryId)
);

/*==============================================================*/
/* Table: LMProgramHistory                                      */
/*==============================================================*/
create table LMProgramHistory  (
   LMProgramHistoryId   NUMBER                          not null,
   ProgramName          VARCHAR2(60)                    not null,
   ProgramId            NUMBER                          not null,
   constraint PK_LMPROGRAMHISTORY primary key (LMProgramHistoryId)
);

/*==============================================================*/
/* Table: LMProgramWebPublishing                                */
/*==============================================================*/
create table LMProgramWebPublishing  (
   ApplianceCategoryID  NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   WebsettingsID        NUMBER,
   ChanceOfControlID    NUMBER,
   ProgramOrder         NUMBER,
   ProgramID            NUMBER                          not null,
   constraint PK_LMPROGRAMWEBPUBLISHING primary key (ProgramID)
);

insert into LMProgramWebPublishing values (0,0,0,0,0,0);

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
   RampRate             FLOAT                           not null,
   constraint PK_LMTHERMOSTATGEAR primary key (GearID)
);

/*==============================================================*/
/* Table: LMThermostatManualEvent                               */
/*==============================================================*/
create table LMThermostatManualEvent  (
   EventID              NUMBER                          not null,
   InventoryID          NUMBER                          not null,
   PreviousTemperature  FLOAT,
   HoldTemperature      VARCHAR2(1),
   OperationStateID     NUMBER,
   FanOperationID       NUMBER,
   constraint PK_LMTHERMOSTATMANUALEVENT primary key (EventID)
);

/*==============================================================*/
/* Table: LMThermostatSchedule                                  */
/*==============================================================*/
create table LMThermostatSchedule  (
   ScheduleID           NUMBER                          not null,
   ScheduleName         VARCHAR2(60)                    not null,
   ThermostatTypeID     NUMBER                          not null,
   AccountID            NUMBER                          not null,
   InventoryID          NUMBER                          not null,
   constraint PK_LMTHERMOSTATSCHEDULE primary key (ScheduleID)
);

INSERT INTO LMThermostatSchedule VALUES (-1,'(none)',0,0,0);

/*==============================================================*/
/* Index: INDX_LMThermSch_InvId                                 */
/*==============================================================*/
create index INDX_LMThermSch_InvId on LMThermostatSchedule (
   InventoryID ASC
);

/*==============================================================*/
/* Index: INDX_LMThermSch_AcctId                                */
/*==============================================================*/
create index INDX_LMThermSch_AcctId on LMThermostatSchedule (
   AccountID ASC
);

/*==============================================================*/
/* Table: LMThermostatSeason                                    */
/*==============================================================*/
create table LMThermostatSeason  (
   SeasonID             NUMBER                          not null,
   ScheduleID           NUMBER,
   WebConfigurationID   NUMBER,
   CoolStartDate        DATE,
   HeatStartDate        DATE,
   constraint PK_LMTHERMOSTATSEASON primary key (SeasonID)
);

INSERT INTO LMThermostatSeason VALUES (-1,-1,-2,'01-JUN-00','15-OCT-00');

/*==============================================================*/
/* Index: INDX_LMThermSea_SchId                                 */
/*==============================================================*/
create index INDX_LMThermSea_SchId on LMThermostatSeason (
   ScheduleID ASC
);

/*==============================================================*/
/* Table: LMThermostatSeasonEntry                               */
/*==============================================================*/
create table LMThermostatSeasonEntry  (
   EntryID              NUMBER                          not null,
   SeasonID             NUMBER                          not null,
   TimeOfWeekID         NUMBER                          not null,
   StartTime            NUMBER                          not null,
   CoolTemperature      NUMBER,
   HeatTemperature      NUMBER,
   constraint PK_LMTHERMOSTATSEASONENTRY primary key (EntryID)
);

INSERT INTO LMThermostatSeasonEntry VALUES (-24,-1,1171,21600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-23,-1,1171,30600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-22,-1,1171,61200,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-21,-1,1171,75600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-20,-1,1173,21600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-19,-1,1173,30600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-18,-1,1173,61200,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-17,-1,1173,75600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-16,-1,1174,21600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-15,-1,1174,30600,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-14,-1,1174,61200,72,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-13,-1,1174,75600,72,72);

/*==============================================================*/
/* Index: INDX_LMThermSeaEntry_SeaId                            */
/*==============================================================*/
create index INDX_LMThermSeaEntry_SeaId on LMThermostatSeasonEntry (
   SeasonID ASC
);

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
);

/*==============================================================*/
/* Table: MACROROUTE                                            */
/*==============================================================*/
create table MACROROUTE  (
   ROUTEID              NUMBER                          not null,
   SINGLEROUTEID        NUMBER                          not null,
   ROUTEORDER           NUMBER                          not null,
   constraint PK_MACROROUTE primary key (ROUTEID, ROUTEORDER)
);

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
);

/*==============================================================*/
/* Table: MACSimpleSchedule                                     */
/*==============================================================*/
create table MACSimpleSchedule  (
   ScheduleID           NUMBER                          not null,
   TargetPAObjectId     NUMBER,
   StartCommand         VARCHAR2(120),
   StopCommand          VARCHAR2(120),
   RepeatInterval       NUMBER,
   constraint PK_MACSIMPLESCHEDULE primary key (ScheduleID)
);

/*==============================================================*/
/* Table: MCTBroadCastMapping                                   */
/*==============================================================*/
create table MCTBroadCastMapping  (
   MCTBroadCastID       NUMBER                          not null,
   MctID                NUMBER                          not null,
   Ordering             NUMBER                          not null,
   constraint PK_MCTBROADCASTMAPPING primary key (MCTBroadCastID, MctID)
);

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
);

/*==============================================================*/
/* Table: MCTConfigMapping                                      */
/*==============================================================*/
create table MCTConfigMapping  (
   MctID                NUMBER                          not null,
   ConfigID             NUMBER                          not null,
   constraint PK_MCTCONFIGMAPPING primary key (MctID, ConfigID)
);

/*==============================================================*/
/* Table: MSPInterface                                          */
/*==============================================================*/
create table MSPInterface  (
   VendorID             NUMBER                          not null,
   Interface            VARCHAR2(12)                    not null,
   Endpoint             VARCHAR2(32)                    not null,
   constraint PK_MSPINTERFACE primary key (VendorID, Interface, Endpoint)
);

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'MR_ServerSoap');
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'OD_ServerSoap');
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'CD_ServerSoap');

/*==============================================================*/
/* Table: MSPVendor                                             */
/*==============================================================*/
create table MSPVendor  (
   VendorID             NUMBER                          not null,
   CompanyName          VARCHAR2(64)                    not null,
   UserName             VARCHAR2(64)                    not null,
   Password             VARCHAR2(64)                    not null,
   URL                  VARCHAR2(120)                   not null,
   AppName              VARCHAR2(64)                    not null,
   OutUserName          VARCHAR2(64)                    not null,
   OutPassword          VARCHAR2(64)                    not null,
   MaxReturnRecords     INTEGER                         not null,
   RequestMessageTimeout INTEGER                         not null,
   MaxInitiateRequestObjects INTEGER                         not null,
   TemplateNameDefault  VARCHAR2(50)                    not null,
   constraint PK_MSPVENDOR primary key (VendorID)
);

insert into MSPVendor values (1, 'Cannon', '(none)', '(none)', 'http://127.0.0.1:8080/soap/', 'Yukon', ' ', ' ',10000,120000,15,'*Default Template');

/*==============================================================*/
/* Index: INDEX_1                                               */
/*==============================================================*/
create unique index INDEX_1 on MSPVendor (
   CompanyName ASC,
   AppName ASC
);

/*==============================================================*/
/* Table: MeterHardwareBase                                     */
/*==============================================================*/
create table MeterHardwareBase  (
   InventoryID          NUMBER                          not null,
   MeterNumber          VARCHAR2(30)                    not null,
   MeterTypeID          NUMBER                          not null,
   constraint PK_METERHARDWAREBASE primary key (InventoryID)
);

/*==============================================================*/
/* Table: MspLMInterfaceMapping                                 */
/*==============================================================*/
create table MspLMInterfaceMapping  (
   MspLMInterfaceMappingId NUMBER                          not null,
   StrategyName         VARCHAR2(100)                   not null,
   SubstationName       VARCHAR2(100)                   not null,
   PAObjectId           NUMBER                          not null,
   constraint PK_MSPLMINTERFACEMAPPING primary key (MspLMInterfaceMappingId)
);

/*==============================================================*/
/* Index: INDX_StratName_SubName_UNQ                            */
/*==============================================================*/
create unique index INDX_StratName_SubName_UNQ on MspLMInterfaceMapping (
   StrategyName ASC,
   SubstationName ASC
);

/*==============================================================*/
/* Table: NotificationDestination                               */
/*==============================================================*/
create table NotificationDestination  (
   NotificationGroupID  NUMBER                          not null,
   RecipientID          NUMBER                          not null,
   Attribs              CHAR(16)                        not null,
   constraint PKey_NotDestID primary key (NotificationGroupID, RecipientID)
);

/*==============================================================*/
/* Table: NotificationGroup                                     */
/*==============================================================*/
create table NotificationGroup  (
   NotificationGroupID  NUMBER                          not null,
   GroupName            VARCHAR2(40)                    not null,
   DisableFlag          CHAR(1)                         not null,
   constraint PK_NOTIFICATIONGROUP primary key (NotificationGroupID)
);

insert into notificationgroup values( 1, '(none)', 'N' );

/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique index Indx_NOTIFGRPNme on NotificationGroup (
   GroupName ASC
);

/*==============================================================*/
/* Table: OperatorLoginGraphList                                */
/*==============================================================*/
create table OperatorLoginGraphList  (
   OperatorLoginID      NUMBER                          not null,
   GraphDefinitionID    NUMBER                          not null,
   constraint PK_OPERATORLOGINGRAPHLIST primary key (OperatorLoginID, GraphDefinitionID)
);

/*==============================================================*/
/* Table: OptOutAdditional                                      */
/*==============================================================*/
create table OptOutAdditional  (
   InventoryId          NUMBER                          not null,
   CustomerAccountId    NUMBER                          not null,
   ExtraOptOutCount     NUMBER                          not null,
   constraint PK_OPTOUTADDITIONAL primary key (InventoryId, CustomerAccountId)
);

/*==============================================================*/
/* Table: OptOutEvent                                           */
/*==============================================================*/
create table OptOutEvent  (
   OptOutEventId        NUMBER                          not null,
   InventoryId          NUMBER                          not null,
   CustomerAccountId    NUMBER,
   ScheduledDate        DATE                            not null,
   StartDate            DATE                            not null,
   StopDate             DATE                            not null,
   EventCounts          VARCHAR2(25)                    not null,
   EventState           VARCHAR2(25)                    not null,
   constraint PK_OPTOUTEVENT primary key (OptOutEventId)
);

/*==============================================================*/
/* Table: OptOutEventLog                                        */
/*==============================================================*/
create table OptOutEventLog  (
   OptOutEventLogId     NUMBER                          not null,
   InventoryId          NUMBER                          not null,
   CustomerAccountId    NUMBER                          not null,
   EventAction          VARCHAR2(25)                    not null,
   LogDate              DATE                            not null,
   EventStartDate       DATE,
   EventStopDate        DATE                            not null,
   LogUserId            NUMBER                          not null,
   OptOutEventId        NUMBER                          not null,
   EventCounts          VARCHAR2(25),
   constraint PK_OPTOUTEVENTLOG primary key (OptOutEventLogId)
);

/*==============================================================*/
/* Table: OptOutSurvey                                          */
/*==============================================================*/
create table OptOutSurvey  (
   OptOutSurveyId       NUMBER                          not null,
   SurveyId             NUMBER                          not null,
   StartDate            DATE                            not null,
   StopDate             DATE,
   constraint PK_OptOutSurvey primary key (OptOutSurveyId)
);

/*==============================================================*/
/* Table: OptOutSurveyProgram                                   */
/*==============================================================*/
create table OptOutSurveyProgram  (
   OptOutSurveyId       NUMBER                          not null,
   DeviceId             NUMBER                          not null,
   constraint PK_OptOutSurvProg primary key (OptOutSurveyId, DeviceId)
);

/*==============================================================*/
/* Table: OptOutSurveyResult                                    */
/*==============================================================*/
create table OptOutSurveyResult  (
   SurveyResultId       NUMBER                          not null,
   OptOutEventLogId     NUMBER                          not null,
   constraint PK_OptOutSurvRes primary key (SurveyResultId, OptOutEventLogId)
);

/*==============================================================*/
/* Table: OptOutTemporaryOverride                               */
/*==============================================================*/
create table OptOutTemporaryOverride  (
   OptOutTemporaryOverrideId NUMBER                          not null,
   UserId               NUMBER                          not null,
   EnergyCompanyId      NUMBER                          not null,
   OptOutType           VARCHAR2(25)                    not null,
   StartDate            DATE                            not null,
   StopDate             DATE                            not null,
   OptOutValue          VARCHAR2(10)                    not null,
   ProgramId            NUMBER,
   constraint PK_OPTOUTTEMPORARYOVERRIDE primary key (OptOutTemporaryOverrideId)
);

/*==============================================================*/
/* Table: OutageMonitor                                         */
/*==============================================================*/
create table OutageMonitor  (
   OutageMonitorId      NUMBER                          not null,
   OutageMonitorName    VARCHAR2(255)                   not null,
   GroupName            VARCHAR2(255)                   not null,
   TimePeriod           NUMBER                          not null,
   NumberOfOutages      NUMBER                          not null,
   EvaluatorStatus      VARCHAR2(255)                   not null,
   constraint PK_OutageMonitor primary key (OutageMonitorId)
);

/*==============================================================*/
/* Index: INDX_OutMonName_UNQ                                   */
/*==============================================================*/
create unique index INDX_OutMonName_UNQ on OutageMonitor (
   OutageMonitorName ASC
);

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
);

/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique index Indx_PAOExclus on PAOExclusion (
   PaoID ASC,
   ExcludedPaoID ASC
);

/*==============================================================*/
/* Table: PAOFavorites                                          */
/*==============================================================*/
create table PAOFavorites  (
   UserId               NUMBER                          not null,
   PAObjectId           NUMBER                          not null,
   constraint PK_PAOFavorites primary key (UserId, PAObjectId)
);

/*==============================================================*/
/* Table: PAOProperty                                           */
/*==============================================================*/
create table PAOProperty  (
   PAObjectId           NUMBER                          not null,
   PropertyName         VARCHAR2(50)                    not null,
   PropertyValue        VARCHAR2(100)                   not null,
   constraint PK_PAOProperty primary key (PAObjectId, PropertyName)
);

/*==============================================================*/
/* Table: PAORecentViews                                        */
/*==============================================================*/
create table PAORecentViews  (
   PAObjectId           NUMBER                          not null,
   WhenViewed           DATE                            not null,
   constraint PK_PAORecentViews primary key (PAObjectId)
);

/*==============================================================*/
/* Index: INDX_WhenViewed                                       */
/*==============================================================*/
create index INDX_WhenViewed on PAORecentViews (
   WhenViewed ASC
);

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
);

/*==============================================================*/
/* Index: Indx_SchedName                                        */
/*==============================================================*/
create unique index Indx_SchedName on PAOSchedule (
   ScheduleName ASC
);

/*==============================================================*/
/* Table: PAOScheduleAssignment                                 */
/*==============================================================*/
create table PAOScheduleAssignment  (
   EventID              NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Command              VARCHAR2(128)                   not null,
   DisableOvUv          VARCHAR2(1)                     not null,
   constraint PK_PAOSCHEDULEASSIGNMENT primary key (EventID)
);

/*==============================================================*/
/* Index: INDX_SchId_PAOId_Com_UNQ                              */
/*==============================================================*/
create unique index INDX_SchId_PAOId_Com_UNQ on PAOScheduleAssignment (
   ScheduleID ASC,
   PaoID ASC,
   Command ASC
);

/*==============================================================*/
/* Table: PAOowner                                              */
/*==============================================================*/
create table PAOowner  (
   OwnerID              NUMBER                          not null,
   ChildID              NUMBER                          not null,
   constraint PK_PAOOWNER primary key (OwnerID, ChildID)
);

/*==============================================================*/
/* Table: PEAKREPORT                                            */
/*==============================================================*/
create table PEAKREPORT  (
   resultID             INTEGER                         not null,
   deviceID             NUMBER                          not null,
   channel              INTEGER                         not null,
   peakType             VARCHAR2(50)                    not null,
   runType              VARCHAR2(50)                    not null,
   runDate              DATE                            not null,
   resultString         VARCHAR2(1500)                  not null,
   constraint PK_PEAKREPORT primary key (resultID)
);

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
);

INSERT INTO Point VALUES( 7, 'Status','Porter Monitor',0,'Default',-7,'N','N','R',1000,'None',0);
INSERT INTO Point VALUES( 6, 'Status','Dispatch Monitor',0,'Default',-7,'N','N','R',1001,'None',0);
INSERT INTO Point VALUES( 5, 'Status','Scanner Monitor',0,'Default',-7,'N','N','R',1002,'None',0);
INSERT INTO Point VALUES( 4, 'Status','Calc Monitor',0,'Default',-7,'N','N','R',1003,'None',0);
INSERT INTO Point VALUES( 3, 'Status','Cap Control Monitor',0,'Default',-7,'N','N','R',1004,'None',0);
INSERT INTO Point VALUES( 2, 'Status','FDR Monitor',0,'Default',-7,'N','N','R',1005,'None',0);
INSERT INTO Point VALUES( 1, 'Status','Macs Monitor',0,'Default',-7,'N','N','R',1006,'None',0);
INSERT INTO Point VALUES( 0, 'System', 'System Point', 0, 'Default', 0, 'N', 'N', 'S', 0  ,'None', 0);
INSERT INTO Point VALUES(-1, 'System', 'Porter', 0, 'Default', 0, 'N', 'N', 'S', 1  ,'None', 0);
INSERT INTO Point VALUES(-2, 'System', 'Scanner', 0, 'Default', 0, 'N', 'N', 'S', 2  ,'None', 0);
INSERT INTO Point VALUES(-3, 'System', 'Dispatch', 0, 'Default', 0, 'N', 'N', 'S', 3  ,'None', 0);
INSERT INTO Point VALUES(-4, 'System', 'Macs', 0, 'Default', 0, 'N', 'N', 'S', 4  ,'None', 0);
INSERT INTO Point VALUES(-5, 'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 'S', 5  ,'None', 0);
INSERT INTO Point VALUES(-6, 'System', 'Notification', 0, 'Default', 0, 'N', 'N', 'S', 6  ,'None', 0);
INSERT INTO Point VALUES(-10,'System', 'Load Management' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);
INSERT INTO Point VALUES(-100, 'System', 'Threshold' , 0, 'Default', 0, 'N', 'N', 'S', 100 ,'None', 0);
INSERT INTO Point VALUES( 100, 'Analog','Porter Work Count',0,'Default',0,'N','N','R',1500,'None',0);
INSERT INTO Point VALUES(-110, 'System', 'MultiSpeak' , 0, 'Default', 0, 'N', 'N', 'S', 110 ,'None', 0);

alter table POINT
   add constraint AK_KEY_PTNM_YUKPAOID unique (POINTNAME, PAObjectID);

/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create index Indx_PointStGrpID on POINT (
   STATEGROUPID ASC
);

/*==============================================================*/
/* Index: INDX_PAOBJECTID                                       */
/*==============================================================*/
create index INDX_PAOBJECTID on POINT (
   PAObjectID ASC
);

/*==============================================================*/
/* Index: INDX_PAOBJECTID_POFFSET                               */
/*==============================================================*/
create index INDX_PAOBJECTID_POFFSET on POINT (
   PAObjectID ASC,
   POINTOFFSET ASC
);

/*==============================================================*/
/* Index: INDX_PAOBJECTID_POINTID                               */
/*==============================================================*/
create index INDX_PAOBJECTID_POINTID on POINT (
   PAObjectID ASC,
   POINTID ASC
);

/*==============================================================*/
/* Index: INDX_POFFSET_POINTTYPE                                */
/*==============================================================*/
create index INDX_POFFSET_POINTTYPE on POINT (
   POINTOFFSET ASC,
   POINTTYPE ASC
);

/*==============================================================*/
/* Table: POINTACCUMULATOR                                      */
/*==============================================================*/
create table POINTACCUMULATOR  (
   POINTID              NUMBER                          not null,
   MULTIPLIER           FLOAT                           not null,
   DATAOFFSET           FLOAT                           not null,
   constraint PK_POINTACCUMULATOR primary key (POINTID)
);

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
);

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
);

/*==============================================================*/
/* Table: POINTPROPERTYVALUE                                    */
/*==============================================================*/
create table POINTPROPERTYVALUE  (
   PointID              NUMBER                          not null,
   PointPropertyCode    INTEGER                         not null,
   FltValue             FLOAT                           not null,
   constraint PK_POINTPROPERTYVALUE primary key (PointID, PointPropertyCode)
);

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
);

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
);

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
);

insert into pointunit values( 100, 9, 1, 1.0E+30, -1.0E+30, 0);

/*==============================================================*/
/* Index: INDX_UOMID_POINTID                                    */
/*==============================================================*/
create index INDX_UOMID_POINTID on POINTUNIT (
   UOMID ASC,
   POINTID ASC
);

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
);

/*==============================================================*/
/* Table: PORTLOCALSERIAL                                       */
/*==============================================================*/
create table PORTLOCALSERIAL  (
   PORTID               NUMBER                          not null,
   PHYSICALPORT         VARCHAR2(8)                     not null,
   constraint PK_PORTLOCALSERIAL primary key (PORTID)
);

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
);

/*==============================================================*/
/* Table: PORTSETTINGS                                          */
/*==============================================================*/
create table PORTSETTINGS  (
   PORTID               NUMBER                          not null,
   BAUDRATE             NUMBER                          not null,
   CDWAIT               NUMBER                          not null,
   LINESETTINGS         VARCHAR2(8)                     not null,
   constraint PK_PORTSETTINGS primary key (PORTID)
);

/*==============================================================*/
/* Table: PORTTERMINALSERVER                                    */
/*==============================================================*/
create table PORTTERMINALSERVER  (
   PORTID               NUMBER                          not null,
   IPADDRESS            VARCHAR2(64)                    not null,
   SOCKETPORTNUMBER     NUMBER                          not null,
   EncodingKey          VARCHAR2(64)                    not null,
   EncodingType         VARCHAR2(50)                    not null,
   constraint PK_PORTTERMINALSERVER primary key (PORTID)
);

/*==============================================================*/
/* Index: INDX_IPAdd_SockPortNum_UNQ                            */
/*==============================================================*/
create unique index INDX_IPAdd_SockPortNum_UNQ on PORTTERMINALSERVER (
   IPADDRESS ASC,
   SOCKETPORTNUMBER ASC
);

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
);

/*==============================================================*/
/* Table: PersistedSystemValue                                  */
/*==============================================================*/
create table PersistedSystemValue  (
   Name                 VARCHAR2(50)                    not null,
   Value                CLOB                            not null,
   constraint PK_PerSysValue primary key (Name)
);

/*==============================================================*/
/* Table: PointAlarming                                         */
/*==============================================================*/
create table PointAlarming  (
   PointID              NUMBER                          not null,
   AlarmStates          VARCHAR2(32)                    not null,
   ExcludeNotifyStates  VARCHAR2(32)                    not null,
   NotifyOnAcknowledge  CHAR(1)                         not null,
   NotificationGroupID  NUMBER                          not null,
   constraint PK_POINTALARMING primary key (PointID)
);

INSERT INTO PointAlarming(PointId, AlarmStates, ExcludeNotifyStates, NotifyOnAcknowledge, NotificationGroupId)
SELECT PointId, '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1 
FROM Point;

/*==============================================================*/
/* Table: PointToZoneMapping                                    */
/*==============================================================*/
create table PointToZoneMapping  (
   PointId              NUMBER                          not null,
   ZoneId               NUMBER                          not null,
   GraphPositionOffset  FLOAT,
   Distance             FLOAT,
   Phase                CHAR(1)                         not null,
   constraint PK_PointZoneMap primary key (PointId)
);

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
);

/*==============================================================*/
/* Table: PorterResponseMonitor                                 */
/*==============================================================*/
create table PorterResponseMonitor  (
   MonitorId            NUMBER                          not null,
   Name                 VARCHAR2(255)                   not null,
   GroupName            VARCHAR2(255)                   not null,
   StateGroupId         NUMBER                          not null,
   Attribute            VARCHAR2(255)                   not null,
   EvaluatorStatus      VARCHAR2(255)                   not null,
   constraint PK_PortRespMonId primary key (MonitorId)
);

INSERT INTO PorterResponseMonitor VALUES (1, 'Default All Meters', '/', -14, 'OUTAGE_STATUS', 'DISABLED');

/*==============================================================*/
/* Index: Indx_PortRespMon_Name_UNQ                             */
/*==============================================================*/
create unique index Indx_PortRespMon_Name_UNQ on PorterResponseMonitor (
   Name ASC
);

/*==============================================================*/
/* Table: PorterResponseMonitorErrorCode                        */
/*==============================================================*/
create table PorterResponseMonitorErrorCode  (
   ErrorCodeId          NUMBER                          not null,
   RuleId               NUMBER,
   ErrorCode            NUMBER                          not null,
   constraint PK_PortRespMonErrorCodeId primary key (ErrorCodeId)
);

INSERT INTO PorterResponseMonitorErrorCode VALUES (1, 2, 93); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (2, 2, 261); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (3, 2, 262); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (4, 2, 263); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (5, 2, 264); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (6, 2, 265); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (7, 2, 267); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (8, 2, 268); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (9, 2, 269); 
INSERT INTO PorterResponseMonitorErrorCode VALUES (10, 2, 270); 

/*==============================================================*/
/* Index: Indx_PortRespMonErr_RI_EC_UNQ                         */
/*==============================================================*/
create unique index Indx_PortRespMonErr_RI_EC_UNQ on PorterResponseMonitorErrorCode (
   RuleId ASC,
   ErrorCode ASC
);

/*==============================================================*/
/* Table: PorterResponseMonitorRule                             */
/*==============================================================*/
create table PorterResponseMonitorRule  (
   RuleId               NUMBER                          not null,
   RuleOrder            NUMBER                          not null,
   MonitorId            NUMBER                          not null,
   Success              CHAR(1)                         not null,
   MatchStyle           VARCHAR2(40)                    not null,
   State                VARCHAR2(40)                    not null,
   constraint PK_PortRespMonRuleId primary key (RuleId)
);

INSERT INTO PorterResponseMonitorRule VALUES (1, 1, 1, 'Y', 'any', 0); 
INSERT INTO PorterResponseMonitorRule VALUES (2, 2, 1, 'N', 'any', 0); 
INSERT INTO PorterResponseMonitorRule VALUES (3, 3, 1, 'N', 'any', 1); 

/*==============================================================*/
/* Index: Indx_PortRespMonRule_RO_MI_UNQ                        */
/*==============================================================*/
create unique index Indx_PortRespMonRule_RO_MI_UNQ on PorterResponseMonitorRule (
   RuleOrder ASC,
   MonitorId ASC
);

/*==============================================================*/
/* Table: PurchasePlan                                          */
/*==============================================================*/
create table PurchasePlan  (
   PurchaseID           NUMBER                          not null,
   EnergyCompanyID      NUMBER                          not null,
   PlanName             VARCHAR2(60)                    not null,
   PODesignation        VARCHAR2(40)                    not null,
   AccountingCode       VARCHAR2(30)                    not null,
   TimePeriod           DATE                            not null,
   constraint PK_PURCHASEPLAN primary key (PurchaseID)
);

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
);

/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create index Index_PointID on RAWPOINTHISTORY (
   POINTID ASC
);

/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create index Indx_TimeStamp on RAWPOINTHISTORY (
   TIMESTAMP ASC
);

/*==============================================================*/
/* Index: Indx_RwPtHisPtIDTst                                   */
/*==============================================================*/
create index Indx_RwPtHisPtIDTst on RAWPOINTHISTORY (
   POINTID ASC,
   TIMESTAMP ASC
);

/*==============================================================*/
/* Table: RFNAddress                                            */
/*==============================================================*/
create table RFNAddress  (
   DeviceId             NUMBER                          not null,
   SerialNumber         VARCHAR2(30)                    not null,
   Manufacturer         VARCHAR2(80)                    not null,
   Model                VARCHAR2(80)                    not null,
   constraint PK_RFNAdd primary key (DeviceId)
);

/*==============================================================*/
/* Index: Indx_RFNAdd_SerNum_Man_Mod_UNQ                        */
/*==============================================================*/
create unique index Indx_RFNAdd_SerNum_Man_Mod_UNQ on RFNAddress (
   SerialNumber ASC,
   Manufacturer ASC,
   Model ASC
);

/*==============================================================*/
/* Table: RPHTag                                                */
/*==============================================================*/
create table RPHTag  (
   ChangeId             NUMBER                          not null,
   TagName              VARCHAR2(150)                   not null,
   constraint PK_RPHTag primary key (ChangeId, TagName)
);

/*==============================================================*/
/* Table: Regulator                                             */
/*==============================================================*/
create table Regulator  (
   RegulatorId          NUMBER                          not null,
   KeepAliveTimer       NUMBER                          not null,
   KeepAliveConfig      NUMBER                          not null,
   constraint PK_Reg primary key (RegulatorId)
);

/*==============================================================*/
/* Table: RegulatorToZoneMapping                                */
/*==============================================================*/
create table RegulatorToZoneMapping  (
   RegulatorId          NUMBER                          not null,
   ZoneId               NUMBER                          not null,
   Phase                CHAR(1),
   constraint PK_RegToZoneMap primary key (RegulatorId)
);

/*==============================================================*/
/* Table: RepeaterRoute                                         */
/*==============================================================*/
create table RepeaterRoute  (
   ROUTEID              NUMBER                          not null,
   DEVICEID             NUMBER                          not null,
   VARIABLEBITS         NUMBER                          not null,
   REPEATERORDER        NUMBER                          not null,
   constraint PK_REPEATERROUTE primary key (ROUTEID, DEVICEID)
);

/*==============================================================*/
/* Table: Route                                                 */
/*==============================================================*/
create table Route  (
   RouteID              NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   DefaultRoute         CHAR(1)                         not null,
   constraint SYS_RoutePK primary key (RouteID)
);

INSERT INTO Route VALUES (0,0,'N');

/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique index Indx_RouteDevID on Route (
   DeviceID ASC,
   RouteID ASC
);

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
);

/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create index Indx_SYSLG_PtId on SYSTEMLOG (
   POINTID ASC
);

/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create index Indx_SYSLG_Date on SYSTEMLOG (
   DATETIME ASC
);

/*==============================================================*/
/* Index: INDX_SYSLG_PTID_TS                                    */
/*==============================================================*/
create unique index INDX_SYSLG_PTID_TS on SYSTEMLOG (
   LOGID ASC,
   POINTID ASC,
   DATETIME ASC
);

/*==============================================================*/
/* Table: ScheduleShipmentMapping                               */
/*==============================================================*/
create table ScheduleShipmentMapping  (
   ScheduleID           NUMBER                          not null,
   ShipmentID           NUMBER                          not null,
   constraint PK_SCHEDULESHIPMENTMAPPING primary key (ScheduleID, ShipmentID)
);

/*==============================================================*/
/* Table: ScheduleTimePeriod                                    */
/*==============================================================*/
create table ScheduleTimePeriod  (
   TimePeriodID         NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   TimePeriodName       VARCHAR2(60)                    not null,
   Quantity             NUMBER                          not null,
   PredictedShipDate    DATE                            not null,
   constraint PK_SCHEDULETIMEPERIOD primary key (TimePeriodID)
);

/*==============================================================*/
/* Table: ScheduledGrpCommandRequest                            */
/*==============================================================*/
create table ScheduledGrpCommandRequest  (
   CommandRequestExecContextId NUMBER                          not null,
   JobId                INTEGER                         not null,
   constraint PK_ScheduledGrpCommandRequest primary key (CommandRequestExecContextId)
);

/*==============================================================*/
/* Index: Indx_SchGrpComReq_JobId                               */
/*==============================================================*/
create index Indx_SchGrpComReq_JobId on ScheduledGrpCommandRequest (
   JobId ASC
);

/*==============================================================*/
/* Table: SeasonSchedule                                        */
/*==============================================================*/
create table SeasonSchedule  (
   ScheduleID           NUMBER                          not null,
   ScheduleName         VARCHAR2(40)                    not null,
   constraint PK_SEASONSCHEDULE primary key (ScheduleID)
);

insert into SeasonSchedule values( 0, 'Empty Schedule' );
insert into seasonSchedule values (-1,'No Season');

/*==============================================================*/
/* Table: SequenceNumber                                        */
/*==============================================================*/
create table SequenceNumber  (
   LastValue            NUMBER                          not null,
   SequenceName         VARCHAR2(30)                    not null,
   constraint PK_SEQUENCENUMBER primary key (SequenceName)
);

INSERT INTO SequenceNumber VALUES (100, 'BillingFileFormats');
INSERT INTO SequenceNumber VALUES (100, 'DeviceGroup');

/*==============================================================*/
/* Table: ServiceCompany                                        */
/*==============================================================*/
create table ServiceCompany  (
   CompanyID            NUMBER                          not null,
   CompanyName          VARCHAR2(40),
   AddressID            NUMBER,
   MainPhoneNumber      VARCHAR2(14),
   MainFaxNumber        VARCHAR2(14),
   PrimaryContactID     NUMBER,
   HIType               VARCHAR2(40),
   constraint PK_SERVICECOMPANY primary key (CompanyID)
);

INSERT INTO ServiceCompany VALUES (0,'(none)',0,'(none)','(none)',0,'(none)');

/*==============================================================*/
/* Table: ServiceCompanyDesignationCode                         */
/*==============================================================*/
create table ServiceCompanyDesignationCode  (
   DesignationCodeID    NUMBER                          not null,
   DesignationCodeValue VARCHAR2(60)                    not null,
   ServiceCompanyID     NUMBER                          not null,
   constraint PK_SERVICECOMPANYDESIGNATIONCO primary key (DesignationCodeID)
);

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
);

insert into SettlementConfig values (-1, 'CDI Rate', '0', 'HECO', '3651', 'Controlled Demand Incentive, Dollars per kW.', 0, 0);
insert into SettlementConfig values (-2, 'ERI Rate', '0', 'HECO', '3651', 'Energy Reduction Incentive, Dollars per kWh.', 0, 0);
insert into SettlementConfig values (-3, 'UF Delay', '0', 'HECO', '3651', 'Under frequency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-4, 'Dispatched Delay', '0', 'HECO', '3651', 'Dispatched Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-5, 'Emergency Delay', '0', 'HECO', '3651', 'Emergency Delay, in minutes.', 0, 0);
insert into SettlementConfig values (-6, 'Allowed Violations', '0', 'HECO', '3651', 'Max number of allowed violations, deviations.', 0, 0);
insert into SettlementConfig values (-7, 'Restore Duration', '0', 'HECO', '3651', 'Duration for event restoration to occur, in minutes.', 0, 0);
insert into SettlementConfig values (-8, 'Demand Charge', '0', 'HECO', '3651', 'Rate Schedule billing demand charge', 0, 0);

/*==============================================================*/
/* Table: Shipment                                              */
/*==============================================================*/
create table Shipment  (
   ShipmentID           NUMBER                          not null,
   ShipmentNumber       VARCHAR2(60)                    not null,
   WarehouseID          NUMBER                          not null,
   SerialNumberStart    VARCHAR2(30)                    not null,
   SerialNumberEnd      VARCHAR2(30)                    not null,
   ShipDate             DATE                            not null,
   ActualPricePerUnit   FLOAT                           not null,
   SalesTotal           FLOAT                           not null,
   SalesTax             FLOAT                           not null,
   OtherCharges         FLOAT                           not null,
   ShippingCharges      FLOAT                           not null,
   AmountPaid           FLOAT                           not null,
   OrderedDate          DATE                            not null,
   ReceivedDate         DATE                            not null,
   constraint PK_SHIPMENT primary key (ShipmentID)
);

/*==============================================================*/
/* Table: SiteInformation                                       */
/*==============================================================*/
create table SiteInformation  (
   SiteID               NUMBER                          not null,
   Feeder               VARCHAR2(20),
   Pole                 VARCHAR2(20),
   TransformerSize      VARCHAR2(20),
   ServiceVoltage       VARCHAR2(20),
   SubstationID         NUMBER,
   constraint PK_SITEINFORMATION primary key (SiteID)
);

INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0);

/*==============================================================*/
/* Table: State                                                 */
/*==============================================================*/
create table State  (
   StateGroupId         NUMBER                          not null,
   RawState             NUMBER                          not null,
   Text                 VARCHAR2(32)                    not null,
   ForegroundColor      NUMBER                          not null,
   BackgroundColor      NUMBER                          not null,
   ImageId              NUMBER                          not null,
   constraint PK_STATE primary key (StateGroupId, RawState)
);

INSERT INTO State VALUES(-15, 0, 'No Signal', 0, 6, 0); 
INSERT INTO State VALUES(-15, 1, 'Very Poor', 1, 6, 0); 
INSERT INTO State VALUES(-15, 2, 'Ok', 10, 6, 0); 
INSERT INTO State VALUES(-15, 3, 'Good', 3, 6, 0); 
INSERT INTO State VALUES(-15, 4, 'Best', 4, 6, 0);
INSERT INTO State VALUES(-14, 0, 'Good', 0, 6, 0); 
INSERT INTO State VALUES(-14, 1, 'Questionable', 3, 6, 0); 
INSERT INTO State VALUES(-14, 2, 'Bad', 1, 6, 0); 
INSERT INTO State VALUES(-13, 0, 'Connected', 0, 6, 0);
INSERT INTO State VALUES(-13, 1, 'Decommissioned', 7, 6, 0);
INSERT INTO State VALUES(-13, 2, 'Disconnected', 1, 6, 0);
INSERT INTO State VALUES(-12, 0, 'Unknown', 3, 6, 0);
INSERT INTO State VALUES(-12, 1, 'Connected', 0, 6, 0);
INSERT INTO State VALUES(-12, 2, 'Disconnected', 1, 6, 0);
INSERT INTO State VALUES(-12, 3, 'Armed', 4, 6, 0);
INSERT INTO State VALUES(-11,-1, 'Any', 2, 6, 0);
INSERT INTO State VALUES(-11, 0, 'Connected', 0, 6, 0);
INSERT INTO State VALUES(-11, 1, 'Disconnected', 1, 6, 0);
INSERT INTO State VALUES(-10, 0, 'Unknown', 0, 6, 0);
INSERT INTO State VALUES(-10, 1, 'A', 1, 6, 0);
INSERT INTO State VALUES(-10, 2, 'B', 10, 6, 0);
INSERT INTO State VALUES(-10, 3, 'C', 3, 6, 0);
INSERT INTO State VALUES(-10, 4, 'AB', 4, 6, 0);
INSERT INTO State VALUES(-10, 5, 'AC', 5, 6, 0);
INSERT INTO State VALUES(-10, 6, 'BC', 7, 6, 0);
INSERT INTO State VALUES(-10, 7, 'ABC', 8, 6, 0);
INSERT INTO State VALUES(-9,-1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES(-9, 0, 'Open', 0, 6 , 0);
INSERT INTO State VALUES(-9, 1, 'Closed', 1, 6 , 0);
INSERT INTO State VALUES(-9, 2, 'Unknown', 2, 6 , 0);
INSERT INTO State VALUES(-8, 0, 'Inactive', 0, 6, 0);
INSERT INTO State VALUES(-8, 1, 'Active', 2, 6, 0);
INSERT INTO State VALUES(-7, 0, 'Normal',0,6,0);
INSERT INTO State VALUES(-7, 1, 'NonCriticalFailure',1,6,0);
INSERT INTO State VALUES(-7, 2, 'CriticalFailure',2,6,0);
INSERT INTO State VALUES(-7, 3, 'Unresponsive',3,6,0);
INSERT INTO State VALUES(-6, 0, 'Confirmed Disconnected', 1, 6, 0);
INSERT INTO State VALUES(-6, 1, 'Connected', 0, 6, 0);
INSERT INTO State VALUES(-6, 2, 'Unconfirmed Disconnected', 3, 6, 0);
INSERT INTO State VALUES(-6, 3, 'Connect Armed', 5, 6, 0);
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

/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create index Indx_StateRaw on State (
   RawState ASC
);

/*==============================================================*/
/* Table: StateGroup                                            */
/*==============================================================*/
create table StateGroup  (
   StateGroupId         NUMBER                          not null,
   Name                 VARCHAR2(60)                    not null,
   GroupType            VARCHAR2(20)                    not null,
   constraint SYS_C0013128 primary key (StateGroupId)
);

INSERT INTO StateGroup VALUES(-15, 'Signal Strength', 'Status'); 
INSERT INTO StateGroup VALUES(-14, 'Outage Status','Status'); 
INSERT INTO StateGroup VALUES(-13, 'Commissioned State','Status'); 
INSERT INTO StateGroup VALUES(-12, 'RFN Disconnect Status', 'Status'); 
INSERT INTO StateGroup VALUES(-11, 'Comm Status State', 'Status'); 
INSERT INTO StateGroup VALUES(-10, 'PhaseStatus', 'Status');
INSERT INTO StateGroup VALUES(-9, 'ThreeStateStatus', 'Status');
INSERT INTO StateGroup VALUES(-8, 'TwoStateActive', 'Status');
INSERT INTO stategroup VALUES(-7, 'Thread Monitor', 'Status');
INSERT INTO stategroup VALUES(-6, '410 Disconnect', 'Status');
INSERT INTO StateGroup VALUES(-5, 'Event Priority', 'System' );
INSERT INTO StateGroup VALUES(-2, 'DefaultAccumulator', 'Accumulator');
INSERT INTO StateGroup VALUES(-3, 'DefaultCalculated', 'Calculated');
INSERT INTO StateGroup VALUES(-1, 'DefaultAnalog', 'Analog');
INSERT INTO StateGroup VALUES( 0, 'SystemState', 'System');
INSERT INTO StateGroup VALUES( 1, 'TwoStateStatus', 'Status');
INSERT INTO StateGroup VALUES( 3, 'CapBankStatus', 'Status');
INSERT INTO StateGroup VALUES( 4, 'TrueFalse', 'Status');
INSERT INTO stategroup VALUES( 5, 'RemoteLocal', 'Status');
INSERT INTO StateGroup VALUES( 6, '1LNSUBSTATE', 'Status');
INSERT INTO StateGroup VALUES( 7, '1LNVERIFY', 'Status');

/*==============================================================*/
/* Index: Indx_StateGroup_Name_UNQ                              */
/*==============================================================*/
create unique index Indx_StateGroup_Name_UNQ on StateGroup (
   Name ASC
);

/*==============================================================*/
/* Table: StaticPAOInfo                                         */
/*==============================================================*/
create table StaticPAOInfo  (
   StaticPAOInfoId      NUMBER                          not null,
   PAObjectId           NUMBER                          not null,
   InfoKey              VARCHAR2(128)                   not null,
   Value                VARCHAR2(128),
   constraint PK_StatPAOInfo primary key (StaticPAOInfoId)
);

/*==============================================================*/
/* Index: Indx_PAObjId_InfoKey_UNQ                              */
/*==============================================================*/
create unique index Indx_PAObjId_InfoKey_UNQ on StaticPAOInfo (
   PAObjectId ASC,
   InfoKey ASC
);

/*==============================================================*/
/* Table: StatusPointMonitor                                    */
/*==============================================================*/
create table StatusPointMonitor  (
   StatusPointMonitorId NUMBER                          not null,
   StatusPointMonitorName VARCHAR2(255)                   not null,
   GroupName            VARCHAR2(255)                   not null,
   Attribute            VARCHAR2(255)                   not null,
   StateGroupId         NUMBER                          not null,
   EvaluatorStatus      VARCHAR2(255)                   not null,
   constraint PK_StatPointMon primary key (StatusPointMonitorId)
);

INSERT INTO StatusPointMonitor VALUES (1, 'Default All Meters', '/', 'OUTAGE_STATUS', -14, 'DISABLED');

/*==============================================================*/
/* Index: Indx_StatPointMon_MonName_UNQ                         */
/*==============================================================*/
create unique index Indx_StatPointMon_MonName_UNQ on StatusPointMonitor (
   StatusPointMonitorName ASC
);

/*==============================================================*/
/* Table: StatusPointMonitorProcessor                           */
/*==============================================================*/
create table StatusPointMonitorProcessor  (
   StatusPointMonitorProcessorId NUMBER                          not null,
   StatusPointMonitorId NUMBER                          not null,
   PrevState            VARCHAR2(255)                   not null,
   NextState            VARCHAR2(255)                   not null,
   ActionType           VARCHAR2(255)                   not null,
   constraint PK_StatPointMonProcId primary key (StatusPointMonitorProcessorId)
);

INSERT INTO StatusPointMonitorProcessor VALUES (1, 1, 'DIFFERENCE', 1, 'NoResponse');
INSERT INTO StatusPointMonitorProcessor VALUES (2, 1, 'DIFFERENCE', 0, 'Restoration');

/*==============================================================*/
/* Table: Substation                                            */
/*==============================================================*/
create table Substation  (
   SubstationID         NUMBER                          not null,
   SubstationName       VARCHAR2(50),
   constraint PK_SUBSTATION primary key (SubstationID)
);

INSERT INTO Substation VALUES (0,'(none)');

/*==============================================================*/
/* Table: SubstationToRouteMapping                              */
/*==============================================================*/
create table SubstationToRouteMapping  (
   SubstationID         NUMBER                          not null,
   RouteID              NUMBER                          not null,
   Ordering             NUMBER                          not null,
   constraint PK_SUBSTATIONTOROUTEMAPPING primary key (SubstationID, RouteID)
);

/*==============================================================*/
/* Table: Survey                                                */
/*==============================================================*/
create table Survey  (
   SurveyId             NUMBER                          not null,
   EnergyCompanyId      NUMBER                          not null,
   SurveyName           VARCHAR2(64)                    not null,
   SurveyKey            VARCHAR2(64)                    not null,
   constraint PK_Surv primary key (SurveyId)
);

/*==============================================================*/
/* Table: SurveyQuestion                                        */
/*==============================================================*/
create table SurveyQuestion  (
   SurveyQuestionId     NUMBER                          not null,
   SurveyId             NUMBER                          not null,
   QuestionKey          VARCHAR2(64)                    not null,
   AnswerRequired       CHAR(1)                         not null,
   QuestionType         VARCHAR2(32)                    not null,
   TextAnswerAllowed    CHAR(1)                         not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_SurvQuest primary key (SurveyQuestionId)
);

/*==============================================================*/
/* Index: Indx_SurvId_DispOrder_UNQ                             */
/*==============================================================*/
create unique index Indx_SurvId_DispOrder_UNQ on SurveyQuestion (
   SurveyId ASC,
   DisplayOrder ASC
);

/*==============================================================*/
/* Table: SurveyQuestionAnswer                                  */
/*==============================================================*/
create table SurveyQuestionAnswer  (
   SurveyQuestionAnswerId NUMBER                          not null,
   SurveyQuestionId     NUMBER                          not null,
   AnswerKey            VARCHAR2(64)                    not null,
   DisplayOrder         NUMBER                          not null,
   constraint PK_SurvQuestAns primary key (SurveyQuestionAnswerId)
);

/*==============================================================*/
/* Index: Indx_SurvQuestId_dispOrder_UNQ                        */
/*==============================================================*/
create unique index Indx_SurvQuestId_dispOrder_UNQ on SurveyQuestionAnswer (
   SurveyQuestionId ASC,
   DisplayOrder ASC
);

/*==============================================================*/
/* Table: SurveyResult                                          */
/*==============================================================*/
create table SurveyResult  (
   SurveyResultId       NUMBER                          not null,
   SurveyId             NUMBER                          not null,
   AccountId            NUMBER,
   AccountNumber        VARCHAR2(40),
   WhenTaken            DATE                            not null,
   constraint PK_SurvRes primary key (SurveyResultId)
);

/*==============================================================*/
/* Table: SurveyResultAnswer                                    */
/*==============================================================*/
create table SurveyResultAnswer  (
   SurveyResultAnswerId NUMBER                          not null,
   SurveyResultId       NUMBER                          not null,
   SurveyQuestionId     NUMBER                          not null,
   SurveyQuestionAnswerId NUMBER,
   TextAnswer           VARCHAR2(255),
   constraint PK_SurvResAns primary key (SurveyResultAnswerId)
);

/*==============================================================*/
/* Table: TEMPLATE                                              */
/*==============================================================*/
create table TEMPLATE  (
   TEMPLATENUM          NUMBER                          not null,
   NAME                 VARCHAR2(40)                    not null,
   DESCRIPTION          VARCHAR2(200),
   constraint SYS_C0013425 primary key (TEMPLATENUM)
);

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
);

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
/* Table: TOUDay                                                */
/*==============================================================*/
create table TOUDay  (
   TOUDayID             NUMBER                          not null,
   TOUDayName           VARCHAR2(32)                    not null,
   constraint PK_TOUDAY primary key (TOUDayID)
);

/*==============================================================*/
/* Table: TOUDayMapping                                         */
/*==============================================================*/
create table TOUDayMapping  (
   TOUScheduleID        NUMBER                          not null,
   TOUDayID             NUMBER                          not null,
   TOUDayOffset         NUMBER                          not null,
   constraint PK_TOUDAYMAPPING primary key (TOUScheduleID, TOUDayOffset)
);

/*==============================================================*/
/* Table: TOUDayRateSwitches                                    */
/*==============================================================*/
create table TOUDayRateSwitches  (
   TOURateSwitchID      NUMBER                          not null,
   SwitchRate           VARCHAR2(4)                     not null,
   SwitchOffset         NUMBER                          not null,
   TOUDayID             NUMBER                          not null,
   constraint PK_TOURATESWITCH primary key (TOURateSwitchID)
);

/*==============================================================*/
/* Index: Indx_todsw_idoff                                      */
/*==============================================================*/
create unique index Indx_todsw_idoff on TOUDayRateSwitches (
   SwitchOffset ASC,
   TOUDayID ASC
);

/*==============================================================*/
/* Table: TOUSchedule                                           */
/*==============================================================*/
create table TOUSchedule  (
   TOUScheduleID        NUMBER                          not null,
   TOUScheduleName      VARCHAR2(32)                    not null,
   TOUDefaultRate       VARCHAR2(4)                     not null,
   constraint PK_TOUSCHEDULE primary key (TOUScheduleID)
);

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
);

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
);

insert into tags values(-1, 'Out Of Service', 1, 'Y', 1, 0);
insert into tags values(-2, 'Info', 1, 'N', 6, 0);
insert into tags values (-3, 'Cap Bank Operational State', 1, 'N', 0, 0);
insert into tags values (-4, 'Enablement State', 1, 'N', 0, 0);
insert into tags values (-5, 'OVUV Enablement State', 1, 'N', 0, 0); 

/*==============================================================*/
/* Table: TamperFlagMonitor                                     */
/*==============================================================*/
create table TamperFlagMonitor  (
   TamperFlagMonitorId  NUMBER                          not null,
   TamperFlagMonitorName VARCHAR2(255)                   not null,
   GroupName            VARCHAR2(255)                   not null,
   EvaluatorStatus      VARCHAR2(255)                   not null,
   constraint PK_TAMPERFLAGMONITOR primary key (TamperFlagMonitorId)
);

/*==============================================================*/
/* Index: INDX_TampFlagMonName_UNQ                              */
/*==============================================================*/
create unique index INDX_TampFlagMonName_UNQ on TamperFlagMonitor (
   TamperFlagMonitorName ASC
);

/*==============================================================*/
/* Table: TemplateDisplay                                       */
/*==============================================================*/
create table TemplateDisplay  (
   DisplayNum           NUMBER                          not null,
   TemplateNum          NUMBER                          not null,
   constraint PK_TEMPLATEDISPLAY primary key (DisplayNum)
);

/*==============================================================*/
/* Table: ThermostatEventHistory                                */
/*==============================================================*/
create table ThermostatEventHistory  (
   EventId              NUMBER                          not null,
   EventType            VARCHAR2(64)                    not null,
   Username             VARCHAR2(64)                    not null,
   EventTime            DATE                            not null,
   ThermostatId         NUMBER                          not null,
   ManualTemp           FLOAT,
   ManualMode           VARCHAR2(64),
   ManualFan            VARCHAR2(64),
   ManualHold           CHAR(1),
   ScheduleId           NUMBER,
   ScheduleMode         VARCHAR2(64),
   constraint PK_ThermEventHist primary key (EventId)
);

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
);

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
INSERT INTO UnitMeasure VALUES ( 26,'Ops', 0,'Ops','(none)' );
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
/* Table: UserPaoPermission                                     */
/*==============================================================*/
create table UserPaoPermission  (
   UserPaoPermissionID  NUMBER                          not null,
   UserID               NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Permission           VARCHAR2(50)                    not null,
   Allow                VARCHAR2(5)                     not null,
   constraint PK_USERPAOPERMISSION primary key (UserPaoPermissionID)
);

alter table UserPaoPermission
   add constraint AK_USRPAOPERM unique (UserID, PaoID, Permission);

/*==============================================================*/
/* Table: ValidationMonitor                                     */
/*==============================================================*/
create table ValidationMonitor  (
   ValidationMonitorId  NUMBER                          not null,
   ValidationMonitorName VARCHAR2(255)                   not null,
   GroupName            VARCHAR2(255)                   not null,
   Threshold            FLOAT                           not null,
   ReRead               NUMBER                          not null,
   SlopeError           FLOAT                           not null,
   ReadingError         FLOAT                           not null,
   PeakHeightMinimum    FLOAT                           not null,
   QuestionableQuality  NUMBER                          not null,
   EvaluatorStatus      VARCHAR2(255)                   not null,
   constraint PK_ValidMon primary key (ValidationMonitorId)
);

INSERT INTO ValidationMonitor VALUES (1, 'Default All Meters', '/System/Attributes/Existing/Usage Reading', 400, 1, 4, .1000001, 15, 1, 'DISABLED');

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
);

/*==============================================================*/
/* Table: Warehouse                                             */
/*==============================================================*/
create table Warehouse  (
   WarehouseID          NUMBER                          not null,
   WarehouseName        VARCHAR2(60)                    not null,
   AddressID            NUMBER                          not null,
   Notes                VARCHAR2(300),
   EnergyCompanyID      NUMBER                          not null,
   constraint PK_WAREHOUSE primary key (WarehouseID)
);

/*==============================================================*/
/* Table: WorkOrderBase                                         */
/*==============================================================*/
create table WorkOrderBase  (
   OrderID              NUMBER                          not null,
   OrderNumber          VARCHAR2(20),
   WorkTypeID           NUMBER                          not null,
   CurrentStateID       NUMBER                          not null,
   ServiceCompanyID     NUMBER,
   DateReported         DATE,
   OrderedBy            VARCHAR2(30),
   Description          VARCHAR2(500),
   DateScheduled        DATE,
   DateCompleted        DATE,
   ActionTaken          VARCHAR2(200),
   AccountID            NUMBER,
   AdditionalOrderNumber VARCHAR2(24),
   constraint PK_WORKORDERBASE primary key (OrderID)
);

/*==============================================================*/
/* Table: YukonGroup                                            */
/*==============================================================*/
create table YukonGroup  (
   GroupID              NUMBER                          not null,
   GroupName            VARCHAR2(120)                   not null,
   GroupDescription     VARCHAR2(200)                   not null,
   constraint PK_YUKONGROUP primary key (GroupID)
);

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
/* Index: Indx_YukonGroup_groupName_UNQ                         */
/*==============================================================*/
create unique index Indx_YukonGroup_groupName_UNQ on YukonGroup (
   GroupName ASC
);

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
);

/* Assign the default Yukon role to the default Yukon group */
insert into YukonGroupRole values(-1,-1,-1,-1000,' ');
insert into YukonGroupRole values(-2,-1,-1,-1001,' ');
insert into YukonGroupRole values(-3,-1,-1,-1002,' ');
insert into YukonGroupRole values(-4,-1,-1,-1003,' ');
insert into YukonGroupRole values(-5,-1,-1,-1004,' ');
insert into YukonGroupRole values(-6,-1,-1,-1005,' ');
insert into YukonGroupRole values(-7,-1,-1,-1006,' ');
insert into YukonGroupRole values(-8,-1,-1,-1007,' ');
insert into YukonGroupRole values(-9,-1,-1,-1008,' ');
insert into YukonGroupRole values(-10,-1,-1,-1009,' ');
insert into YukonGroupRole values(-11,-1,-1,-1010,' ');
insert into YukonGroupRole values(-12,-1,-1,-1011,' ');
insert into YukonGroupRole values(-14,-1,-1,-1013,' ');
insert into YukonGroupRole values(-15,-1,-1,-1014,'CannonLogo.gif');
insert into YukonGroupRole values(-17,-1,-1,-1016,' ');
insert into YukonGroupRole values(-18,-1,-1,-1017,' ');
insert into YukonGroupRole values(-20,-1,-1,-1019,' ');
insert into YukonGroupRole values(-21,-1,-1,-1020,' ');
insert into YukonGroupRole values(-50,-1,-4,-1308,' ');
insert into YukonGroupRole values(-51,-1,-4,-1309,' ');
insert into YukonGroupRole values(-52,-1,-4,-1310,' ');
insert into YukonGroupRole values(-53,-1,-4,-1311,' ');
insert into YukonGroupRole values(-54,-1,-4,-1312,' ');
insert into YukonGroupRole values(-55,-1,-4,-1313,' ');
insert into YukonGroupRole values(-56,-1,-4,-1314,' ');
insert into YukonGroupRole values(-57,-1,-4,-1315,' ');
insert into YukonGroupRole values(-58,-1,-4,-1316,' ');
insert into YukonGroupRole values(-59,-1,-4,-1317,' ');
insert into YukonGroupRole values(-72,-1,-5,-1402,' ');
insert into YukonGroupRole values(-85,-1,-4,-1300,' ');
insert into YukonGroupRole values(-86,-1,-4,-1301,' ');
insert into YukonGroupRole values(-87,-1,-4,-1302,' ');
insert into YukonGroupRole values(-88,-1,-4,-1303,' ');
insert into YukonGroupRole values(-89,-1,-4,-1304,' ');
insert into YukonGroupRole values(-90,-1,-4,-1305,' ');
insert into YukonGroupRole values(-91,-1,-4,-1306,' ');
insert into YukonGroupRole values(-92,-1,-4,-1307,' ');

/* Assign roles to the default operator group to allow them to use all the main rich Yukon applications */
/* Database Editor */
insert into YukonGroupRole values(-100,-100,-100,-10000,' ');
insert into YukonGroupRole values(-102,-100,-100,-10002,' ');
insert into YukonGroupRole values(-104,-100,-100,-10004,' ');
insert into YukonGroupRole values(-105,-100,-100,-10005,' ');
insert into YukonGroupRole values(-107,-100,-100,-10007,' ');
insert into YukonGroupRole values(-108,-100,-100,-10008,' ');
insert into YukonGroupRole values(-109,-100,-100,-10009,' ');
insert into YukonGroupRole values(-110,-100,-100,-10010,' ');
insert into YukonGroupRole values(-111,-100,-100,-10011,' ');

/* TDC */
insert into YukonGroupRole values(-120,-100,-101,-10100,' ');
insert into YukonGroupRole values(-121,-100,-101,-10101,' ');
insert into YukonGroupRole values(-122,-100,-101,-10102,' ');
insert into YukonGroupRole values(-123,-100,-101,-10103,' ');
insert into YukonGroupRole values(-124,-100,-101,-10104,' ');
insert into YukonGroupRole values(-127,-100,-101,-10107,' ');
insert into YukonGroupRole values(-128,-100,-101,-10108,' ');
insert into YukonGroupRole values(-130,-100,-101,-10111,' ');

/* Trending */
insert into YukonGroupRole values(-150,-100,-102,-10200,' ');
insert into YukonGroupRole values(-152,-100,-102,-10202,' ');
insert into YukonGroupRole values(-153,-100,-102,-10203,' ');
insert into YukonGroupRole values(-155,-100,-102,-10205,' ');
insert into YukonGroupRole values(-156,-100,-102,-10206,' ');

/* Commander */
insert into YukonGroupRole values(-170,-100,-103,-10300,' ');
insert into YukonGroupRole values(-171,-100,-103,-10301,'true');
insert into YukonGroupRole values(-172,-100,-103,-10302,'true');
insert into YukonGroupRole values(-173,-100,-103,-10303,'false');
insert into YukonGroupRole values(-174,-100,-103,-10304,'false');
insert into YukonGroupRole values(-175,-100,-103,-10305,' ');

insert into YukonGroupRole values(-180,-100,-106,-10600,' ');

/* Calc Historical for Yukon Group */
insert into YukonGroupRole values(-190,-1,-104,-10400,' ');
insert into YukonGroupRole values(-191,-1,-104,-10401,' ');
insert into YukonGroupRole values(-192,-1,-104,-10402,' ');

/* Web Graph for Yukon Group */
insert into YukonGroupRole values(-210,-1,-105,-10500,' ');
insert into YukonGroupRole values(-211,-1,-105,-10501,' ');

/* Billing for Yukon Group */
INSERT INTO YukonGroupRole VALUES(-230,-1,-6,-1500,' ');
INSERT INTO YukonGroupRole VALUES(-231,-1,-6,-1501,' ');
INSERT INTO YukonGroupRole VALUES(-233,-1,-6,-1503,' ');
INSERT INTO YukonGroupRole VALUES(-234,-1,-6,-1504,' ');
INSERT INTO YukonGroupRole VALUES(-235,-1,-6,-1505,' ');
INSERT INTO YukonGroupRole VALUES(-236,-1,-6,-1506,' ');
INSERT INTO YukonGroupRole VALUES(-237,-1,-6,-1507,' ');
INSERT INTO YukonGroupRole VALUES(-239,-1,-6,-1509,' '); 

/* MultiSpeak */
INSERT INTO YukonGroupRole VALUES(-270,-1,-7,-1600,'METER_NUMBER');
INSERT INTO YukonGroupRole VALUES(-274,-1,-7,-1604,' ');

/* Configuration (Device) */
INSERT INTO YukonGroupRole VALUES(-280,-1,-8,-1700,' ');

/* Esubstation Editor */
INSERT INTO YukonGroupRole VALUES(-250,-100,-107,-10700,' ');

/* Assign roles to the default Esub Users */
insert into YukonGroupRole values(-300,-200,-206,-20600,' ');
insert into YukonGroupRole values(-301,-200,-206,-20601,' ');
insert into YukonGroupRole values(-302,-200,-206,-20602,' ');

/* Assign roles to the default Esub Operators */
INSERT INTO YukonGroupRole VALUES(-350,-201,-206,-20600,' ');
INSERT INTO YukonGroupRole VALUES(-351,-201,-206,-20601,'true');
INSERT INTO YukonGroupRole VALUES(-352,-201,-206,-20602,'true');

/* Web Client Customers Web Client role */
insert into yukongrouprole values (-400, -302, -108, -10800, '/user/CILC/user_trending.jsp');
insert into yukongrouprole values (-402, -302, -108, -10802, ' ');
insert into yukongrouprole values (-403, -302, -108, -10803, ' ');
insert into yukongrouprole values (-404, -302, -108, -10804, ' ');
insert into yukongrouprole values (-405, -302, -108, -10805, ' ');
insert into yukongrouprole values (-406, -302, -108, -10806, ' ');
insert into yukongrouprole values (-40700, -302, -108, -10807, ' ');
insert into yukongrouprole values (-40701, -302, -108, -10808, ' ');
insert into yukongrouprole values (-40704, -302, -108, -10811, ' ');

/* Web Client Customers Commercial Metering role */
insert into yukongrouprole values (-413, -302, -102, -10202, ' ');
insert into yukongrouprole values (-414, -302, -102, -10203, 'true');

/* Web Client Customers Administrator role */
insert into YukonGroupRole values (-416, -302, -102, -10205, ' ');
insert into YukonGroupRole values (-417, -302, -102, -10206, ' ');
insert into yukongrouprole values (-500,-300,-108,-10800,'/spring/stars/consumer/general');
insert into yukongrouprole values (-502,-300,-108,-10802,' ');
insert into yukongrouprole values (-503,-300,-108,-10803,' ');
insert into yukongrouprole values (-504,-300,-108,-10804,' ');
insert into yukongrouprole values (-505,-300,-108, -10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-506,-300,-108,-10806,' ');
insert into yukongrouprole values (-507,-300,-108,-10807,' ');
insert into yukongrouprole values (-508,-300,-108,-10808,' ');
insert into yukongrouprole values (-511,-300,-108,-10811,' ');

insert into yukongrouprole values (-521,-300,-400,-40001,'true');
insert into yukongrouprole values (-523,-300,-400,-40003,'true');
insert into yukongrouprole values (-524,-300,-400,-40004,'true');
insert into yukongrouprole values (-525,-300,-400,-40005,'true');
insert into yukongrouprole values (-526,-300,-400,-40006,'true');
insert into yukongrouprole values (-527,-300,-400,-40007,'true');
insert into yukongrouprole values (-528,-300,-400,-40008,'true');
insert into yukongrouprole values (-529,-300,-400,-40009,'true');
insert into yukongrouprole values (-530,-300,-400,-40010,'true');

insert into yukongrouprole values (-551,-300,-400,-40051,'false');
insert into yukongrouprole values (-555,-300,-400,-40055,' ');
insert into yukongrouprole values (-600,-300,-400,-40100,' ');
insert into yukongrouprole values (-602,-300,-400,-40102,' ');

insert into yukongrouprole values (-700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-702,-301,-108,-10802,' ');
insert into yukongrouprole values (-703,-301,-108,-10803,' ');
insert into yukongrouprole values (-704,-301,-108,-10804,' ');
insert into yukongrouprole values (-705,-301,-108,-10805,' ');
insert into yukongrouprole values (-706,-301,-108,-10806,' ');
insert into yukongrouprole values (-707,-301,-108,-10807,' ');
insert into yukongrouprole values (-708,-301,-108,-10808,' ');
insert into yukongrouprole values (-711,-301,-108,-10811,' ');

insert into yukongrouprole values (-722,-301,-201,-20102,'true');
insert into yukongrouprole values (-723,-301,-201,-20103,'true');
insert into yukongrouprole values (-724,-301,-201,-20104,'false');
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
insert into yukongrouprole values (-752,-301,-201,-20152,' ');
insert into yukongrouprole values (-753,-301,-201,-20153,' ');
insert into yukongrouprole values (-755,-301,-201,-20155,'true');
insert into yukongrouprole values (-756,-301,-201,-20156,'true');
insert into yukongrouprole values (-757,-301,-201,-20157,' ');
insert into yukongrouprole values (-758,-301,-201,-20158,' ');
insert into yukongrouprole values (-759,-301,-201,-20159,' ');
insert into yukongrouprole values (-760,-301,-201,-20160,' ');

insert into yukongrouprole values (-765,-301,-210,-21000,' ');
insert into yukongrouprole values (-766,-301,-210,-21001,' ');
insert into yukongrouprole values (-767,-301,-210,-21002,' ');
insert into yukongrouprole values (-776,-301,-900,-90001,' ');
insert into yukongrouprole values (-777,-301,-900,-90002,' ');
insert into yukongrouprole values (-778,-301,-900,-90003,' ');
insert into yukongrouprole values (-779,-301,-900,-90004,' ');
insert into yukongrouprole values (-781,-301,-900,-90005,' ');
insert into yukongrouprole values (-782,-301,-900,-90006,' ');
insert into yukongrouprole values (-783,-301,-900,-90007,' ');
insert into yukongrouprole values (-790,-301,-207,-20700,' ');
insert into yukongrouprole values (-791,-301,-209,-20900,' ');
insert into yukongrouprole values (-792,-301,-209,-20901,' ');
insert into yukongrouprole values (-793,-301,-209,-20902,' ');
insert into yukongrouprole values (-794,-301,-209,-20903,' ');
insert into yukongrouprole values (-795,-301,-209,-20904,' ');
insert into yukongrouprole values (-796,-301,-209,-20905,' ');
insert into yukongrouprole values (-797,-301,-209,-20906,' ');

insert into yukongrouprole values (-800,-301,-201,-20800,' ');
insert into yukongrouprole values (-801,-301,-201,-20801,' ');

insert into yukongrouprole values (-893,-301,-201,-20893,' ');

/* START the System Administrator role Group */
/* Database Editor */
insert into YukonGroupRole values(-1000,-2,-100,-10000,' ');
insert into YukonGroupRole values(-1002,-2,-100,-10002,' ');
insert into YukonGroupRole values(-1004,-2,-100,-10004,' ');
insert into YukonGroupRole values(-1005,-2,-100,-10005,' ');
insert into YukonGroupRole values(-1007,-2,-100,-10007,' ');
insert into YukonGroupRole values(-1008,-2,-100,-10008,' ');
insert into YukonGroupRole values(-1009,-2,-100,-10009,' ');
insert into YukonGroupRole values(-1010,-2,-100,-10010,'00000000');
insert into YukonGroupRole values(-1011,-2,-100,-10011,' ');

/* TDC */
insert into YukonGroupRole values(-1020,-2,-101,-10100,' ');
insert into YukonGroupRole values(-1021,-2,-101,-10101,' ');
insert into YukonGroupRole values(-1022,-2,-101,-10102,' ');
insert into YukonGroupRole values(-1023,-2,-101,-10103,' ');
insert into YukonGroupRole values(-1024,-2,-101,-10104,' ');
insert into YukonGroupRole values(-1027,-2,-101,-10107,' ');
insert into YukonGroupRole values(-1028,-2,-101,-10108,' ');
insert into YukonGroupRole values(-1031,-2,-101,-10111,' ');

/* Trending */
insert into YukonGroupRole values(-1050,-2,-102,-10200,' ');
insert into YukonGroupRole values(-1052,-2,-102,-10202,' ');
insert into YukonGroupRole values(-1053,-2,-102,-10203,' ');
insert into YukonGroupRole values(-1055,-2,-102,-10205,' ');
insert into YukonGroupRole values(-1056,-2,-102,-10206,' ');

/* Commander */
insert into YukonGroupRole values(-1070,-2,-103,-10300,' ');
insert into YukonGroupRole values(-1071,-2,-103,-10301,'true');
insert into YukonGroupRole values(-1072,-2,-103,-10302,'true');
insert into YukonGroupRole values(-1073,-2,-103,-10303,'false');
insert into YukonGroupRole values(-1074,-2,-103,-10304,'false');
insert into YukonGroupRole values(-1075,-2,-103,-10305,' ');

/* Billing */
insert into YukonGroupRole values(-1390,-2,-106,-10600,' ');

/* Esubstation Editor */
INSERT INTO YukonGroupRole VALUES(-1080,-2,-107,-10700,' ');
INSERT INTO YukonGroupRole VALUES(-1081,-2,-206,-20600,' ');
INSERT INTO YukonGroupRole VALUES(-1082,-2,-206,-20601,' ');
INSERT INTO YukonGroupRole VALUES(-1083,-2,-206,-20602,' ');

/* Web Client Customers Web Client role */
insert into YukonGroupRole values (-1090,-2, -108, -10800, '/operator/Operations.jsp');
insert into YukonGroupRole values (-1091,-2, -108, -10802, ' ');
insert into YukonGroupRole values (-1092,-2, -108, -10803, ' ');
insert into YukonGroupRole values (-1093,-2, -108, -10804, ' ');
insert into YukonGroupRole values (-1094,-2, -108, -10805, ' ');
insert into YukonGroupRole values (-1095,-2, -108, -10806, ' ');
insert into YukonGroupRole values (-1096,-2, -108, -10807, ' ');
insert into YukonGroupRole values (-1097,-2, -108, -10808, ' ');
insert into YukonGroupRole values (-10991,-2, -108, -10811, ' ');

/* Give yukon login access to View Logs, */
insert into YukonGroupRole values (-4000, -2, -200, -20009, ' ');
insert into yukongrouprole values (-2000,-303,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (-2002,-303,-108,-10802,' ');
insert into yukongrouprole values (-2003,-303,-108,-10803,' ');
insert into yukongrouprole values (-2004,-303,-108,-10804,' ');
insert into yukongrouprole values (-2005,-303,-108,-10805,' ');
insert into yukongrouprole values (-2006,-303,-108,-10806,' ');
insert into yukongrouprole values (-2007,-303,-108,-10807,' ');
insert into yukongrouprole values (-2008,-303,-108,-10808,' ');
insert into yukongrouprole values (-2011,-303,-108,-10811,' ');

insert into yukongrouprole values (-2022,-303,-201,-20102,'true');
insert into yukongrouprole values (-2023,-303,-201,-20103,' ');
insert into yukongrouprole values (-2024,-303,-201,-20104,' ');
insert into yukongrouprole values (-2026,-303,-201,-20106,' ');
insert into yukongrouprole values (-2027,-303,-201,-20107,' ');
insert into yukongrouprole values (-2028,-303,-201,-20108,' ');
insert into yukongrouprole values (-2029,-303,-201,-20109,' ');
insert into yukongrouprole values (-2030,-303,-201,-20110,' ');
insert into yukongrouprole values (-2031,-303,-201,-20111,' ');
insert into yukongrouprole values (-2032,-303,-201,-20112,' ');
insert into yukongrouprole values (-2033,-303,-201,-20113,' ');
insert into yukongrouprole values (-2034,-303,-201,-20114,'true');
insert into yukongrouprole values (-2035,-303,-201,-20115,'false');
insert into yukongrouprole values (-2036,-303,-201,-20116,' ');
insert into yukongrouprole values (-2037,-303,-201,-20117,' ');
insert into yukongrouprole values (-2038,-303,-201,-20118,' ');

insert into yukongrouprole values (-2051,-303,-201,-20151,' ');
insert into yukongrouprole values (-2052,-303,-201,-20152,' ');
insert into yukongrouprole values (-2053,-303,-201,-20153,' ');
insert into yukongrouprole values (-2055,-303,-201,-20155,'true');
insert into yukongrouprole values (-2056,-303,-201,-20156,' ');
insert into yukongrouprole values (-2057,-303,-201,-20157,' ');
insert into yukongrouprole values (-2058,-303,-201,-20158,' ');
insert into yukongrouprole values (-2059,-303,-201,-20159,' ');
insert into yukongrouprole values (-2060,-303,-201,-20160,' ');

insert into yukongrouprole values (-2070,-303,-210,-21000,' ');
insert into yukongrouprole values (-2071,-303,-210,-21001,' ');
insert into yukongrouprole values (-2072,-303,-210,-21002,' ');

insert into yukongrouprole values (-2080,-303,-209,-20900,' ');
insert into yukongrouprole values (-2081,-303,-209,-20901,' ');
insert into yukongrouprole values (-2082,-303,-209,-20902,' ');
insert into yukongrouprole values (-2083,-303,-209,-20903,' ');
insert into yukongrouprole values (-2084,-303,-209,-20904,' ');
insert into yukongrouprole values (-2085,-303,-209,-20905,' ');
insert into yukongrouprole values (-2086,-303,-209,-20906,' ');
insert into yukongrouprole values (-2088,-303,-209,-20908,' ');

insert into yukongrouprole values (-2100,-303,-201,-20800,' ');
insert into yukongrouprole values (-2101,-303,-201,-20801,' ');

insert into yukongrouprole values (-2193,-303,-201,-20893,' ');

insert into yukongrouprole values (-2200,-304,-108,-10800,'/spring/stars/consumer/general');
insert into yukongrouprole values (-2202,-304,-108,-10802,' ');
insert into yukongrouprole values (-2203,-304,-108,-10803,' ');
insert into yukongrouprole values (-2204,-304,-108,-10804,' ');
insert into yukongrouprole values (-2205,-304,-108,-10805,'yukon/DemoHeaderCES.gif');
insert into yukongrouprole values (-2206,-304,-108,-10806,' ');
insert into yukongrouprole values (-2207,-304,-108,-10807,' ');
insert into yukongrouprole values (-2208,-304,-108,-10808,' ');
insert into yukongrouprole values (-2211,-304,-108,-10811,' ');

insert into yukongrouprole values (-2221,-304,-400,-40001,' ');
insert into yukongrouprole values (-2223,-304,-400,-40003,' ');
insert into yukongrouprole values (-2224,-304,-400,-40004,' ');
insert into yukongrouprole values (-2225,-304,-400,-40005,' ');
insert into yukongrouprole values (-2226,-304,-400,-40006,' ');
insert into yukongrouprole values (-2227,-304,-400,-40007,' ');
insert into yukongrouprole values (-2228,-304,-400,-40008,' ');
insert into yukongrouprole values (-2229,-304,-400,-40009,' ');
insert into yukongrouprole values (-2230,-304,-400,-40010,' ');

insert into yukongrouprole values (-2251,-304,-400,-40051,' ');
insert into yukongrouprole values (-2255,-304,-400,-40055,' ');
insert into yukongrouprole values (-2300,-304,-400,-40100,' ');
insert into yukongrouprole values (-2302,-304,-400,-40102,' ');

/* Device Actions role */ 
INSERT INTO YukonGroupRole VALUES (-2500, -2, -213, -21300, ' '); 
INSERT INTO YukonGroupRole VALUES (-2501, -2, -213, -21301, ' '); 
INSERT INTO YukonGroupRole VALUES (-2502, -2, -213, -21302, ' '); 
INSERT INTO YukonGroupRole VALUES (-2503, -2, -213, -21303, ' '); 
INSERT INTO YukonGroupRole VALUES (-2504, -2, -213, -21304, ' '); 
INSERT INTO YukonGroupRole VALUES (-2505, -2, -213, -21305, ' '); 
INSERT INTO YukonGroupRole VALUES (-2506, -2, -213, -21306, ' '); 
INSERT INTO YukonGroupRole VALUES (-2507, -2, -213, -21307, ' '); 

/*==============================================================*/
/* Table: YukonImage                                            */
/*==============================================================*/
create table YukonImage  (
   ImageID              NUMBER                          not null,
   ImageCategory        VARCHAR2(20)                    not null,
   ImageName            VARCHAR2(80)                    not null,
   ImageValue           BLOB,
   constraint PK_YUKONIMAGE primary key (ImageID)
);

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
);

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

INSERT INTO YukonListEntry VALUES (1047, 1005, 0, 'LCR-6200(ZIGBEE)', 1320); 
INSERT INTO YukonListEntry VALUES (1048, 1005, 0, 'LCR-6200(EXPRESSCOM)', 1321); 
INSERT INTO YukonListEntry VALUES (1049, 1005, 0, 'LCR-6600(ZIGBEE)', 1322); 
INSERT INTO YukonListEntry VALUES (1050, 1005, 0, 'LCR-6600(EXPRESSCOM)', 1323);
INSERT INTO YukonListEntry VALUES (1051, 1005, 0, 'LCR-5000(EXPRESSCOM)', 1302);
INSERT INTO YukonListEntry VALUES (1052, 1005, 0, 'LCR-4000', 1305);
INSERT INTO YukonListEntry VALUES (1053, 1005, 0, 'LCR-3000', 1306);
INSERT INTO YukonListEntry VALUES (1054, 1005, 0, 'LCR-2000', 1307);
INSERT INTO YukonListEntry VALUES (1055, 1005, 0, 'LCR-1000', 1308);
INSERT INTO YukonListEntry VALUES (1056, 1005, 0, 'ExpressStat', 1301);

INSERT INTO YukonListEntry VALUES (1058, 1005, 0, 'MCT', 1303);
INSERT INTO YukonListEntry VALUES (1059, 1005, 0, 'Commercial ExpressStat', 1304);
INSERT INTO YukonListEntry VALUES (1062, 1005, 0, 'LCR-5000(VERSACOM)', 1311);
INSERT INTO YukonListEntry VALUES (1064, 1005, 0, 'ExpressStat Heat Pump', 1313);
INSERT INTO YukonListEntry VALUES (1065, 1005, 0, 'UtilityPRO', 1314);
INSERT INTO YukonListEntry VALUES (1066, 1005, 0, 'LCR-3102', 1315);
INSERT INTO YukonListEntry VALUES (1067, 1005, 0, 'UtilityPRO ZigBee', 1316);

INSERT INTO YukonListEntry VALUES (1068, 1005, 0, 'Digi Gateway', 1317);
INSERT INTO YukonListEntry VALUES (1069, 1005, 0, 'UtilityPRO G2', 1318); 
INSERT INTO YukonListEntry VALUES (1070, 1005, 0, 'UtilityPRO G3', 1319); 

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
insert into YukonListEntry values (1211,1018,0,'(Default)',2401);
insert into YukonListEntry values (1212,1018,0,'Cool',2402);
insert into YukonListEntry values (1213,1018,0,'Heat',2403);
insert into YukonListEntry values (1214,1018,0,'Off',2404);
insert into YukonListEntry values (1215,1018,0,'Auto',2405);
insert into YukonListEntry values (1216,1018,0,'Emergency Heat',2406);
insert into YukonListEntry values (1221,1019,0,'(Default)',2501);
insert into YukonListEntry values (1222,1019,0,'Auto',2502);
insert into YukonListEntry values (1223,1019,0,'On',2503);
INSERT INTO YukonListEntry VALUES (1224,1019,0,'Circulate',2504); 

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
insert into YukonListEntry values (1565,1046,0,'Ditch W/Siphon Tube',0);
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

INSERT INTO YukonListEntry VALUES (10500,1072,0,'(none)',0);
INSERT INTO YukonListEntry VALUES (10501,1072,1,'CTI DLC',0);
INSERT INTO YukonListEntry VALUES (10502,1072,2,'CTI Paging',0);
INSERT INTO YukonListEntry VALUES (10503,1072,3,'CTI FM',0);
INSERT INTO YukonListEntry VALUES (10504,1072,4,'FP Paging',0);
INSERT INTO YukonListEntry VALUES (10505,1072,5,'Telemetric',0);

INSERT INTO YukonListEntry VALUES (10520,1073,0,'(none)',0);
INSERT INTO YukonListEntry VALUES (10521,1073,1,'ABB',0);
INSERT INTO YukonListEntry VALUES (10522,1073,2,'Cannon Tech',0);
INSERT INTO YukonListEntry VALUES (10523,1073,3,'Cooper',0);
INSERT INTO YukonListEntry VALUES (10524,1073,4,'Trinetics',0);
INSERT INTO YukonListEntry VALUES (10525,1073,5,'Siemens',0);
INSERT INTO YukonListEntry VALUES (10526,1073,6,'Westinghouse',0);
INSERT INTO YukonListEntry VALUES (10527,1073,7,'Mix',0);

INSERT INTO YukonListEntry VALUES (10540,1074,0,'(none)',0);
INSERT INTO YukonListEntry VALUES (10541,1074,1,'Oil',0);
INSERT INTO YukonListEntry VALUES (10542,1074,2,'Vacuum',0);
INSERT INTO YukonListEntry VALUES (10543,1074,3,'Mix',0);
INSERT INTO YukonListEntry VALUES (10544,1074,4,'Hybrid',0);

insert into YukonListEntry values (20000,0,0,'Customer List Entry Base 2',0);


/*==============================================================*/
/* Index: Indx_YkLstDefID                                       */
/*==============================================================*/
create index Indx_YkLstDefID on YukonListEntry (
   YukonDefinitionID ASC
);

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
);

INSERT into YukonPAObject values (0, 'DEVICE', 'SYSTEM', 'System Device', 'SYSTEM', 'Reserved System Device', 'N', '-----'); 

/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique index Indx_PAO on YukonPAObject (
   Category ASC,
   PAOName ASC,
   PAOClass ASC
);

/*==============================================================*/
/* Table: YukonRole                                             */
/*==============================================================*/
create table YukonRole  (
   RoleID               NUMBER                          not null,
   RoleName             VARCHAR2(120)                   not null,
   Category             VARCHAR2(60)                    not null,
   RoleDescription      VARCHAR2(200)                   not null,
   constraint PK_YUKONROLE primary key (RoleID)
);

/* Default role for all users - yukon category */
INSERT INTO YukonRole VALUES(-1,'Yukon','Yukon','Default Yukon role. Edit this role from the Yukon SetUp page.');
INSERT INTO YukonRole VALUES(-4,'Authentication','Yukon','Settings for using an authentication server to login instead of standard yukon login.');
INSERT INTO YukonRole VALUES(-5,'Voice Server','Yukon','Inbound and outbound voice interface.');
INSERT INTO YukonRole VALUES(-6,'Billing Configuration','Yukon','Billing. Edit this role from the Yukon SetUp page.');
INSERT INTO YukonRole VALUES(-7,'MultiSpeak','Yukon','MultiSpeak web services interface.');
INSERT INTO YukonRole VALUES(-8,'Configuration','Yukon','Miscellaneous Yukon configuration settings');
INSERT INTO YukonRole VALUES(-104,'Calc Historical','Yukon','Calc Historical. Edit this role from the Yukon SetUp page.');
INSERT INTO YukonRole VALUES(-105,'Web Graph','Yukon','Web Graph. Edit this role from the Yukon SetUp page.');

/* Application specific roles */
INSERT INTO YukonRole VALUES(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
INSERT INTO YukonRole VALUES(-101,'Tabular Data Console','Application','Access to the Yukon Tabular Data Console (TDC) application');
INSERT INTO YukonRole VALUES(-102,'Trending','Application','Access to the Yukon Trending application');
INSERT INTO YukonRole VALUES(-103,'Commander','Application','Access to the Yukon Commander application');
INSERT INTO YukonRole VALUES(-106,'Billing','Application','Billing. Edit this role from the Yukon SetUp page.');
INSERT INTO YukonRole VALUES(-107,'Esubstation Editor','Application','Access to the Esubstation Drawing Editor application');
INSERT INTO YukonRole VALUES(-108,'Web Client','Application','Access to the Yukon web application');
INSERT INTO YukonRole VALUES(-109,'Reporting','Application','Access to reports generation.');

/* Web client operator roles */
INSERT INTO YukonRole VALUES(-200,'Administrator','Operator','Access to Yukon administration');
INSERT INTO YukonRole VALUES(-201,'Consumer Info','Operator','Operator access to consumer account information');
INSERT INTO YukonRole VALUES(-202,'Metering','Operator','Operator access to metering');

/* Operator roles */
INSERT INTO YukonRole VALUES(-206,'Esubstation Drawings','Operator','Operator access to esubstation drawings');
INSERT INTO YukonRole VALUES(-207,'Odds For Control','Operator','Operator access to odds for control');
INSERT INTO YukonRole VALUES(-2,'Energy Company','Operator','Energy company role');

/* Inventory Role */
INSERT INTO YukonRole VALUES(-209,'Inventory','Operator','Operator Access to hardware inventory');

/* operator work order management role */
INSERT INTO YukonRole VALUES(-210,'Work Order','Operator','Operator Access to work order management');

/* ISOC */
INSERT INTO YukonRole VALUES(-211,'CI Curtailment','Operator','Operator access to C&I Curtailment'); 

/* Scheduler Role */
INSERT INTO YukonRole VALUES(-212,'Scheduler','Operator','Operator access to Scheduler'); 

/* Device Actions Role */
INSERT INTO YukonRole VALUES(-213,'Device Actions','Operator','Operator access to device actions'); 

/* Consumer roles */
INSERT INTO YukonRole VALUES(-400,'Residential Customer','Consumer','Access to residential customer information');

/* Capacitor Control roles */
INSERT INTO YukonRole VALUES(-700,'Cap Control Settings','Capacitor Control','Allows a user to change overall settings of the Cap Control system .'); 

/* IVR roles */
INSERT INTO YukonRole VALUES(-800,'IVR','Notifications','Settings for Interactive Voice Response module');
INSERT INTO YukonRole VALUES(-801, 'Configuration', 'Notifications', 'Configuration for Notification Server (voice and email)');

/* Load Control roles */
INSERT INTO YukonRole VALUES(-900,'Direct Loadcontrol','Load Control','Access and usage of direct loadcontrol system');

/* Capacitor Control roles cont*/
INSERT INTO YukonRole VALUES(-1000,'Substation Display','Capacitor Control','Change display settings for substation details.');
INSERT INTO YukonRole VALUES(-1001,'Feeder Display','Capacitor Control','Change display settings for feeder details.');
INSERT INTO YukonRole VALUES(-1002,'Cap Bank Display','Capacitor Control','Change display settings for cap bank details.');

/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create index Indx_YukRol_Nm on YukonRole (
   RoleName ASC
);

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
);

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
insert into YukonRoleProperty values(-1016,-1,'notification_host','127.0.0.1','Name or IP address of the Yukon Notification service');
insert into YukonRoleProperty values(-1017,-1,'notification_port','1515','TCP/IP port of the Yukon Notification service');
insert into YukonRoleProperty values(-1019,-1,'batched_switch_command_timer','auto','Specifies whether the STARS application should automatically process batched switch commands');
insert into YukonRoleProperty values(-1020,-1,'stars_activation','false','Specifies whether STARS functionality should be allowed in this web deployment.');
insert into YukonRoleProperty values(-1021,-1,'importer_communications_enabled','true','Specifies whether communications will be allowed by the bulk importer.'); 

/* Energy Company Role Properties */
INSERT INTO YukonRoleProperty VALUES(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of emails sent on behalf of energy company, e.g. control odds and opt out notification emails.');
INSERT INTO YukonRoleProperty VALUES(-1101,-2,'optout_notification_recipients',' ','Recipients of the opt out notification email');
INSERT INTO YukonRoleProperty VALUES(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
INSERT INTO YukonRoleProperty VALUES(-1107,-2,'track_hardware_addressing','false','Controls whether to track the hardware addressing information.');
INSERT INTO YukonRoleProperty VALUES(-1108,-2,'single_energy_company','true','Indicates whether this is a single energy company system.');
INSERT INTO YukonRoleProperty VALUES(-1109,-2,'z_optional_product_dev','00000000','This feature is for development purposes only');
INSERT INTO YukonRoleProperty VALUES(-1110,-2,'Default Temperature Unit','F','Default temperature unit for an energy company, F(ahrenheit) or C(elsius)');
INSERT INTO YukonRoleProperty VALUES(-1111,-2,'z_meter_mct_base_desig','yukon','Allow meters to be used general STARS entries versus Yukon MCTs');
INSERT INTO YukonRoleProperty VALUES(-1112,-2,'applicable_point_type_key',' ','The name of the set of CICustomerPointData TYPES that should be set for customers.');
INSERT INTO YukonRoleProperty VALUES(-1114,-2,'Inherit Parent App Cats','true','If part of a member structure, should appliance categories be inherited from the parent.');
INSERT INTO YukonRoleProperty VALUES(-1115,-2,'Auto Create Login For Additional Contacts','true','Automatically create a default login for each additional contact created on a STARS account.');
INSERT INTO YukonRoleProperty VALUES(-1116,-2,'Account Number Length',' ','Specifies the number of account number characters to consider for comparison purposes during the customer account import process.'); 
INSERT INTO YukonRoleProperty VALUES(-1117,-2,'Rotation Digit Length',' ','Specifies the number of rotation digit characters to ignore during the customer account import process.'); 
INSERT INTO YukonRoleProperty VALUES(-1118,-2,'Serial Number Validation','NUMERIC','Treat serial numbers as numeric or alpha-numberic. Possible values (NUMERIC, ALPHANUMERIC)');
INSERT INTO YukonRoleProperty VALUES(-1119,-2,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment.');

INSERT INTO YukonRoleProperty VALUES(-1120, -2, 'Allow Designation Codes', 'false', 'Toggles on or off the regional (usually zip) code option for service companies.');
INSERT INTO YukonRoleProperty VALUES(-1121, -2, 'Allow Single Day Thermostat Schedules', 'true', 'Allow the use of schedules where every day shares the same values for compatible thermostats.'); 
INSERT INTO YukonRoleProperty VALUES(-1122, -2, 'Allow 5/2 Thermostat Schedules', 'false', 'Allow the use of 5/2 day schedules for compatible thermostats. Weekday/Weekend.'); 
INSERT INTO YukonRoleProperty VALUES(-1123, -2, 'Allow 5/1/1 Thermostat Schedules', 'true', 'Allow the use of 5/1/1 schedules for compatible thermostats. Weekday/Saturday/Sunday.'); 
INSERT INTO YukonRoleProperty VALUES(-1124, -2, 'Allow 7 Day Thermostat Schedules', 'false', 'Allow the use of 7 day schedules (different schedule each day of the week) for compatible thermostats.'); 

INSERT INTO YukonRoleProperty VALUES(-1300,-4,'server_address','127.0.0.1','Authentication server machine address');
INSERT INTO YukonRoleProperty VALUES(-1301,-4,'auth_port','1812','Authentication port.');
INSERT INTO YukonRoleProperty VALUES(-1302,-4,'acct_port','1813','Accounting port.');
INSERT INTO YukonRoleProperty VALUES(-1303,-4,'secret_key','cti','Client machine secret key value, defined by the server.');
INSERT INTO YukonRoleProperty VALUES(-1304,-4,'auth_method',' ','Authentication method. Possible values are leaving the field empty | PAP, [chap, others to follow soon]');
INSERT INTO YukonRoleProperty VALUES(-1305,-4,'authentication_mode','Yukon','Authentication mode to use.  Valid values are:   Yukon | Radius');
INSERT INTO YukonRoleProperty VALUES(-1306,-4,'auth_timeout','30','Number of seconds before the authentication process times out');
INSERT INTO YukonRoleProperty VALUES(-1307,-4,'Default Authentication Type', 'PLAIN', 'Set the default authentication type to use {PLAIN,HASH_SHA,RADIUS,AD,LDAP,NONE}');
INSERT INTO YukonRoleProperty VALUES(-1308,-4,'LDAP DN','dc=example,dc=com','LDAP Distinguished Name');
INSERT INTO YukonRoleProperty VALUES(-1309,-4,'LDAP User Suffix','ou=users','LDAP User Suffix');

INSERT INTO YukonRoleProperty VALUES(-1310,-4,'LDAP User Prefix','uid=','LDAP User Prefix');
INSERT INTO YukonRoleProperty VALUES(-1311,-4,'LDAP Server Address','127.0.0.1','LDAP Server Address');
INSERT INTO YukonRoleProperty VALUES(-1312,-4,'LDAP Server Port','389','LDAP Server Port');
INSERT INTO YukonRoleProperty VALUES(-1313,-4,'LDAP Server Timeout','30','LDAP Server Timeout (in seconds)');
INSERT INTO YukonRoleProperty VALUES(-1314,-4,'Active Directory Server Address','127.0.0.1','Active Directory Server Address');
INSERT INTO YukonRoleProperty VALUES(-1315,-4,'Active Directory Server Port','389','Active Directory Server Port');
INSERT INTO YukonRoleProperty VALUES(-1316,-4,'Active Directory Server Timeout','30','Active Directory Server Timeout (in seconds)');
INSERT INTO YukonRoleProperty VALUES(-1317,-4,'Active Directory NT Domain Name',' ','Active Directory NT DOMAIN NAME');
INSERT INTO YukonRoleProperty VALUES(-1318,-4,'Enable Password Recovery','true','Controls access to password recovery (Forgot your password?) feature.');

INSERT INTO YukonRoleProperty VALUES(-1402,-5,'call_response_timeout','240','The time-out in seconds given to each outbound call response');
INSERT INTO YukonRoleProperty VALUES(-1403,-5,'Call Prefix',' ','Any number or numbers that must be dialed before a call can be placed.');

/* Billing Role Properties */
INSERT INTO YukonRoleProperty VALUES(-1500,-6,'wiz_activate','false','<description>');
INSERT INTO YukonRoleProperty VALUES(-1501,-6,'input_file','c:\yukon\client\bin\BillingIn.txt','Directory to read billing input file from. Used by ExtendedTOU_Incode, MVRS, WLT_40, and NCDC_Handhed formats.');
INSERT INTO YukonRoleProperty VALUES(-1503,-6,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
INSERT INTO YukonRoleProperty VALUES(-1504,-6,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
INSERT INTO YukonRoleProperty VALUES(-1505,-6,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
INSERT INTO YukonRoleProperty VALUES(-1506,-6,'Append To File','false','Append to existing file.');
INSERT INTO YukonRoleProperty VALUES(-1507,-6,'Remove Multiplier','false','Remove the multiplier value from the reading.');
INSERT INTO YukonRoleProperty VALUES(-1508,-6,'Coop ID - CADP Only',' ','CADP format requires a coop id number.');
INSERT INTO YukonRoleProperty VALUES(-1509,-6,'Rounding Mode','HALF_EVEN','Rounding Mode used when formatting value data in billing formats. Available placeholders: HALF_EVEN, CEILING, FLOOR, UP, DOWN, HALF_DOWN, HALF_UP'); 

/* Database Editor Role */
INSERT INTO YukonRoleProperty VALUES(-10000,-100,'point_id_edit','false','Controls whether point ids can be edited');
INSERT INTO YukonRoleProperty VALUES(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
INSERT INTO YukonRoleProperty VALUES(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
INSERT INTO YukonRoleProperty VALUES(-10005,-100,'utility_id_range','1-254','<description>');
INSERT INTO YukonRoleProperty VALUES(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');
INSERT INTO YukonRoleProperty VALUES(-10008,-100,'permit_login_edit','true','Closes off all access to logins and login groups for non-administrators in the dbeditor');
INSERT INTO YukonRoleProperty VALUES(-10009,-100,'allow_user_roles','false','Allows the editor panel individual user roles to be shown');
INSERT INTO YukonRoleProperty VALUES(-10010,-100,'z_optional_product_dev','00000000','This feature is for development purposes only');
INSERT INTO YukonRoleProperty VALUES(-10011,-100,'allow_member_programs','false','Allows member management of LM Direct Programs through the DBEditor');

/* MultiSpeak */
INSERT INTO YukonRoleProperty VALUES(-1600,-7,'PAOName Alias','METER_NUMBER','Defines a Yukon Pao (Device) Name field alias. Valid values: METER_NUMBER, ACCOUNT_NUMBER, SERVICE_LOCATION, CUSTOMER_ID, GRID_LOCATION, POLE_NUMBER');
INSERT INTO YukonRoleProperty VALUES(-1601,-7,'Primary CIS Vendor','0','Defines the primary CIS vendor for CB interfaces.');
INSERT INTO YukonRoleProperty VALUES(-1602,-7,'Msp BillingCycle DeviceGroup','/Meters/Billing/','Defines the Device Group parent group name for the MultiSpeak billingCycle element. Valid values are ''/Meters/Billing/'', ''/Meters/Collection'', ''/Meters/Alternate''');
INSERT INTO YukonRoleProperty VALUES(-1603,-7,'Msp LM Interface Mapping Setup','false','Controls access to setup the MultiSpeak LM interface mappings.');
INSERT INTO YukonRoleProperty VALUES(-1604,-7,'Meter Lookup Field','AUTO_METER_NUMBER_FIRST','Defines the field used to lookup a meter by in Yukon. Valid values: AUTO_METER_NUMBER_FIRST, AUTO_DEVICE_NAME_FIRST, METER_NUMBER, DEVICE_NAME, or ADDRESS.'); 
INSERT INTO YukonRoleProperty VALUES(-1605,-7,'PAOName Extension',' ','The extension name of the field appended to PaoName Alias. Leave this value blank to ignore the use of extensions.');

/* Configuration */
INSERT INTO YukonRoleProperty VALUES(-1700,-8,'Device Display Template','DEVICE_NAME','Defines the format for displaying devices. Available placeholders: DEVICE_NAME, METER_NUMBER, ID, ADDRESS');
INSERT INTO YukonRoleProperty VALUES(-1701,-8,'Alert Timeout Hours', '168', 'The number of hours that an alert should be held (zero = forever, decimal numbers are okay)'); 
INSERT INTO YukonRoleProperty VALUES(-1702,-8,'Customer Info Importer File Location', ' ', 'File location of the automated consumer information import process.');
INSERT INTO YukonRoleProperty VALUES(-1703,-8,'System Default TimeZone', ' ', 'System Default TimeZone (e.g. America/Denver, America/Chicago, America/Los_Angeles, or America/New_York)'); 
INSERT INTO YukonRoleProperty VALUES(-1704,-8,'Opt Outs Count', 'true', 'Determines whether new opt outs count against the opt out limits.'); 
INSERT INTO YukonRoleProperty VALUES(-1705,-8,'Database Migration File Location','/Server/Export/','File location of the database migration export process.'); 

/* TDC Role */
INSERT INTO YukonRoleProperty VALUES(-10100,-101,'loadcontrol_edit','00000000','(No settings yet)');
INSERT INTO YukonRoleProperty VALUES(-10101,-101,'macs_edit','00000CCC','The following settings are valid: CREATE_SCHEDULE(0x0000000C), ENABLE_SCHEDULE(0x000000C0), ABLE_TO_START_SCHEDULE(0x00000C00)');
INSERT INTO YukonRoleProperty VALUES(-10102,-101,'tdc_express','ludicrous_speed','<description>');
INSERT INTO YukonRoleProperty VALUES(-10103,-101,'tdc_max_rows','500','The number of rows shown before creating a new page of data');
INSERT INTO YukonRoleProperty VALUES(-10104,-101,'tdc_rights','00000000','The following settings are valid: HIDE_MACS(0x00001000), HIDE_CAPCONTROL(0x00002000), HIDE_LOADCONTROL(0x00004000), HIDE_ALL_DISPLAYS(0x0000F000), HIDE_ALARM_COLORS(0x80000000)');
INSERT INTO YukonRoleProperty VALUES(-10107,-101,'tdc_alarm_count','3','Total number alarms that are displayed in the quick access list');
INSERT INTO YukonRoleProperty VALUES(-10108,-101,'decimal_places','2','How many decimal places to show for real values');
INSERT INTO YukonRoleProperty VALUES(-10111,-101,'lc_reduction_col','true','Tells TDC to show the LoadControl reduction column or not');

/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');
insert into YukonRoleProperty values(-10202, -102, 'Trending Disclaimer',' ','The disclaimer that appears with trends.');
insert into yukonroleproperty values(-10203, -102, 'Scan Now Enabled', 'false', 'Controls access to retrieve meter data on demand.');
insert into yukonroleproperty values(-10205, -102, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events.');
insert into yukonroleproperty values(-10206, -102, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily.');

/* Commander Role Properties */ 
INSERT INTO YukonRoleProperty VALUES(-10300,-103,'msg_priority','14','Tells commander what the outbound priority of messages are (low)1 - 14(high)');
INSERT INTO YukonRoleProperty VALUES(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
INSERT INTO YukonRoleProperty VALUES(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
INSERT INTO YukonRoleProperty VALUES(-10303,-103,'DCU SA205 Serial','false','Show a DCU SA205 Serial Number SortBy display');
INSERT INTO YukonRoleProperty VALUES(-10304,-103,'DCU SA305 Serial','false','Show a DCU SA305 Serial Number SortBy display');
INSERT INTO YukonRoleProperty VALUES(-10305,-103,'Commands Group Name','Default Commands','The commands group name for the displayed commands.');
INSERT INTO YukonRoleProperty VALUES(-10306,-103,'Read device', 'true', 'Allow the ability to read values from a device');
INSERT INTO YukonRoleProperty VALUES(-10307,-103,'Write to device', 'true', 'Allow the ability to write values to a device');
INSERT INTO YukonRoleProperty VALUES(-10309,-103,'Control disconnect', 'true', 'Allow the ability to control a disconnect to a device');
INSERT INTO YukonRoleProperty VALUES(-10310,-103,'Read LM device', 'true', 'Allow the ability to read values from an LM device');
INSERT INTO YukonRoleProperty VALUES(-10311,-103,'Write to LM device', 'true', 'Allow the ability to write values to an LM device');
INSERT INTO YukonRoleProperty VALUES(-10312,-103,'Control LM device', 'true', 'Allow the ability to control an LM device');
INSERT INTO YukonRoleProperty VALUES(-10313,-103,'Read Cap Control device', 'true', 'Allow the ability to read values from a Cap Control device');
INSERT INTO YukonRoleProperty VALUES(-10314,-103,'Write to Cap Control device', 'true', 'Allow the ability to write values to a Cap Control device');
INSERT INTO YukonRoleProperty VALUES(-10315,-103,'Control Cap Control device', 'true', 'Allow the ability to control a Cap Control device');
INSERT INTO YukonRoleProperty VALUES(-10316,-103,'Execute Unknown Command', 'true', 'Allow the ability to execute commands which do not fall under another role property.');
INSERT INTO YukonRoleProperty VALUES(-10317,-103,'Execute Manual Command', 'true', 'Allow the ability to execute manual commands');
INSERT INTO YukonRoleProperty VALUES(-10318,-103,'Enable Web Commander', 'true', 'Controls access to web commander applications');
INSERT INTO YukonRoleProperty VALUES(-10319,-103,'Enable Client Commander', 'true', 'Controls access to client commander application'); 

/* Calc Historical Role Properties */
INSERT INTO YukonRoleProperty VALUES(-10400,-104,'interval','900','<description>');
INSERT INTO YukonRoleProperty VALUES(-10401,-104,'baseline_calctime','4','<description>');
INSERT INTO YukonRoleProperty VALUES(-10402,-104,'daysprevioustocollect','30','<description>');

/* Web Graph Role Properties */
INSERT INTO YukonRoleProperty VALUES(-10500,-105,'home_directory','c:\yukon\client\webgraphs\','Directory to write generated web graphs to.');
INSERT INTO YukonRoleProperty VALUES(-10501,-105,'run_interval','900','<description>');
INSERT INTO YukonRoleProperty VALUES(-10600,-106,'Dynamic Billing File Setup','true','Controls access to create, edit, and delete dynamic billing files.'); 

/* Esubstation Editor Role Properties */
INSERT INTO YukonRoleProperty VALUES(-10700,-107,'default','false','The default esub editor property');

/* Web Client Role Properties */
INSERT INTO YukonRoleProperty VALUES(-10800,-108,'home_url','/operator/Operations.jsp','The url to take the user immediately after logging into the Yukon web application');
INSERT INTO YukonRoleProperty VALUES(-10802,-108,'style_sheet','yukon/CannonStyle.css','The web client cascading style sheet.');
INSERT INTO YukonRoleProperty VALUES(-10803,-108,'nav_bullet_selected','yukon/Bullet.gif','The bullet used when an item in the nav is selected.');
INSERT INTO YukonRoleProperty VALUES(-10804,-108,'nav_bullet_expand','yukon/BulletExpand.gif','The bullet used when an item in the nav can be expanded to show submenu.');
INSERT INTO YukonRoleProperty VALUES(-10805,-108,'header_logo','yukon/DefaultHeader.gif','The main header logo');
INSERT INTO YukonRoleProperty VALUES(-10806,-108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
INSERT INTO YukonRoleProperty VALUES(-10807,-108,'nav_connector_bottom','yukon/BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
INSERT INTO YukonRoleProperty VALUES(-10808,-108,'nav_connector_middle','yukon/MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');
INSERT INTO YukonRoleProperty VALUES(-10811,-108, 'inbound_voice_home_url', '/voice/inboundOptOut.jsp', 'Home URL for inbound voice logins');
INSERT INTO YukonRoleProperty VALUES(-10812, -108,'Java Web Start Launcher Enabled', 'true', 'Allow access to the Java Web Start Launcher for client applications.');
INSERT INTO YukonRoleProperty VALUES(-10814, -108,'Suppress Error Page Details', 'true', 'Disable stack traces for this user.');
INSERT INTO YukonRoleProperty VALUES(-10815, -108,'Data Updater Delay (milliseconds)', '4000', 'The number of milliseconds between requests for the latest point values on pages that support the data updater.');
INSERT INTO YukonRoleProperty VALUES(-10816, -108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');
INSERT INTO YukonRoleProperty VALUES(-10817, -108,'Theme Name',' ','The name of the theme to be applied to this group');
INSERT INTO YukonRoleProperty VALUES(-10818, -108, 'View Alarms Alerts','false','Ability to receive point alarms as alerts');
INSERT INTO YukonRoleProperty VALUES(-10819, -108, 'Default TimeZone',' ','Default TimeZone (e.g. America/Denver, America/Chicago, America/Los_Angeles, or America/New_York)');
INSERT INTO YukonRoleProperty VALUES(-10820, -108, 'Session Timeout (minutes)','120','The amount of idle time (in minutes) before a user''s session will expire.');
INSERT INTO YukonRoleProperty VALUES(-10821, -108, 'CSRF Token Mode','OFF', 'Which mode to use for CSRF protection (OFF, TOKEN, PASSWORD).');

/* Reporting Analysis role properties */
INSERT INTO YukonRoleProperty VALUES(-10903,-109,'Admin Reports Group','true','Access to administrative group reports.');
INSERT INTO YukonRoleProperty VALUES(-10904,-109,'AMR Reports Group','true','Access to AMR group reports.');
INSERT INTO YukonRoleProperty VALUES(-10905,-109,'Statistical Reports Group','true','Access to statistical group reports.');
INSERT INTO YukonRoleProperty VALUES(-10906,-109,'Load Management Reports Group','false','Access to Load Management group reports.');
INSERT INTO YukonRoleProperty VALUES(-10907,-109,'Cap Control Reports Group','false','Access to Cap Control group reports.');
INSERT INTO YukonRoleProperty VALUES(-10908,-109,'Database Reports Group','true','Access to Database group reports.');
INSERT INTO YukonRoleProperty VALUES(-10909,-109,'Stars Reports Group','true','Access to Stars group reports.');
/* YUK-6642 INSERT INTO YukonRoleProperty VALUES(-10911,-109,'Settlement Reports Group','false','Access to Settlement group reports.'); */ 
INSERT INTO YukonRoleProperty VALUES(-10923,-109,'C&I Curtailment Reports Group','false','Access to C&I Curtailment group reports');

/* Operator Consumer Info Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20102,-201,'Account Residence','false','Controls whether to show the customer residence information');
INSERT INTO YukonRoleProperty VALUES(-20103,-201,'Account Call Tracking','false','Controls whether to enable the call tracking feature');
INSERT INTO YukonRoleProperty VALUES(-20104,-201,'Metering Interval Data','false','Controls whether to show the metering interval data');
INSERT INTO YukonRoleProperty VALUES(-20106,-201,'Programs Control History','true','Controls whether to show the program control history');
INSERT INTO YukonRoleProperty VALUES(-20107,-201,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
INSERT INTO YukonRoleProperty VALUES(-20108,-201,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
INSERT INTO YukonRoleProperty VALUES(-20109,-201,'Appliances','true','Controls whether to show the appliance information');
INSERT INTO YukonRoleProperty VALUES(-20110,-201,'Appliances Create','true','Controls whether to enable the appliance creation feature');
INSERT INTO YukonRoleProperty VALUES(-20111,-201,'Hardware','true','Controls whether to show the hardware information');
INSERT INTO YukonRoleProperty VALUES(-20112,-201,'Hardware Create','true','Controls whether to enable the hardware creation feature');
INSERT INTO YukonRoleProperty VALUES(-20113,-201,'Hardware Thermostat','true','Controls whether to enable the thermostat programming feature');
INSERT INTO YukonRoleProperty VALUES(-20114,-201,'Work Orders','false','Controls whether to enable the service request feature');
INSERT INTO YukonRoleProperty VALUES(-20115,-201,'Admin Change Login Username','true','Controls access to change a customer login username'); 
INSERT INTO YukonRoleProperty VALUES(-20116,-201,'Admin FAQ','false','Controls whether to show customer FAQs');
INSERT INTO YukonRoleProperty VALUES(-20117,-201,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
INSERT INTO YukonRoleProperty VALUES(-20118,-201,'Create Trend','false','Controls whether to allow new trends to assigned to the customer');
INSERT INTO YukonRoleProperty VALUES(-20119,-201,'Admin Change Login Password','true','Controls access to change a customer login password'); 
INSERT INTO YukonRoleProperty VALUES(-20120,-201,'Web Service LM Data Access','true','Controls access to web services that retrieve LM data.');
INSERT INTO YukonRoleProperty VALUES(-20121,-201,'Web Service LM Control Access','true','Controls access to web services that perform LM control.');

/* Operator Consumer Info Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20151,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
INSERT INTO YukonRoleProperty VALUES(-20152,-201,'Import Customer Account',' ','Controls whether to enable the customer account importing feature');
INSERT INTO YukonRoleProperty VALUES(-20153,-201,'Inventory Checking','true','Controls whether to perform inventory checking while creating or updating hardware information');
INSERT INTO YukonRoleProperty VALUES(-20155,-201,'Order Number Auto Generation','false','Controls whether the order number is automatically generated or entered by user');
INSERT INTO YukonRoleProperty VALUES(-20156,-201,'Call Number Auto Generation','false','Controls whether the call number is automatically generated or entered by user');
INSERT INTO YukonRoleProperty VALUES(-20157,-201,'Opt Out Period',' ','The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)');
INSERT INTO YukonRoleProperty VALUES(-20158,-201,'Disable Switch Sending','false','Disables the ability to send configs and connects/disconnects to switches.');
INSERT INTO YukonRoleProperty VALUES(-20159,-201,'Switches to Meter',' ','Allow switches to be assigned under meters for an account.');
INSERT INTO YukonRoleProperty VALUES(-20160,-201,'Create Login With Account','false','Require that a login is created with every new customer account.');
INSERT INTO YukonRoleProperty VALUES(-20163,-201,'Allow Account Editing','true','Can be used to disable the ability to edit and delete customer account information.');
INSERT INTO YukonRoleProperty VALUES(-20164,-201,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.');
INSERT INTO YukonRoleProperty VALUES(-20165,-201,'Account Search','true','Enables you to use account searching.');
INSERT INTO YukonRoleProperty VALUES(-20166,-201,'Survey Edit','false','Enables editing of surveys.');
INSERT INTO YukonRoleProperty VALUES(-20167,-201,'Opt Out Survey Edit','false','Enables editing of opt out surveys.'); 

/* Operator Administrator Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20000,-200,'Edit Energy Company','false','Controls access to edit the user''s energy company settings.');
INSERT INTO YukonRoleProperty VALUES(-20001,-200,'Create/Delete Energy Company','false','Controls access to create and delete an energy company');
INSERT INTO YukonRoleProperty VALUES(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');
INSERT INTO YukonRoleProperty VALUES(-20004,-200,'View Batch Commands','false','Controls whether to allow monitoring of all batched switch commands');
INSERT INTO YukonRoleProperty VALUES(-20005,-200,'View Opt Out Events','false','Controls whether to allow monitoring of all scheduled opt out events');
INSERT INTO YukonRoleProperty VALUES(-20006,-200,'Member Login Cntrl','false','Ignored if not a member company -- Controls whether operator logins are shown on the EC administration page.');
INSERT INTO YukonRoleProperty VALUES(-20007,-200,'Member Route Select','false','Ignored if not a member company -- Controls whether routes are visible through the EC administration page.');
INSERT INTO YukonRoleProperty VALUES(-20009,-200,'Multiple Warehouses','false','Allows for multiple user-created warehouses instead of a single generic warehouse.');
INSERT INTO YukonRoleProperty VALUES(-20010,-200,'Auto Process Batch Configs','false','Automatically process batch configs using the DailyTimerTask.'); 
INSERT INTO YukonRoleProperty VALUES(-20011,-200,'MultiSpeak Setup','false','Controls access to configure the MultiSpeak Interfaces.');
INSERT INTO YukonRoleProperty VALUES(-20012,-200,'LM User Assignment','false','Controls visibility of LM objects for 3-tier and direct control, based off assignment of users.');
INSERT INTO YukonRoleProperty VALUES(-20013,-200,'Edit Device Config','false','Controls the ability to edit and create device configurations');
INSERT INTO YukonRoleProperty VALUES(-20014,-200,'View Device Config','true','Controls the ability to view existing device configurations');
INSERT INTO YukonRoleProperty VALUES(-20015,-200,'Manage Indexes','true','Controls access to manually build Lucene indexes.'); 
INSERT INTO YukonRoleProperty VALUES(-20016,-200,'View Logs','true','Controls access to view or download log files.');
INSERT INTO YukonRoleProperty VALUES(-20017,-200,'Database Migration','false','Controls access to database migration tool.');
INSERT INTO YukonRoleProperty VALUES(-20018,-200,'Event Logs','false','Controls access to event logs feature.');
INSERT INTO YukonRoleProperty VALUES(-20019,-200,'Admin Super User','false','Allows full control of all energy companies and other administrator features.'); 

/* Operator Metering Role Properties*/
INSERT INTO YukonRoleProperty VALUES(-20203,-202,'Enable Bulk Importer','true','Allows access to the Bulk Importer');
INSERT INTO YukonRoleProperty VALUES(-20206,-202,'Profile Collection','true','Controls access to submit a (past) profile collection request');
INSERT INTO YukonRoleProperty VALUES(-20207,-202,'Move In/Move Out Auto Archiving','true','Enables automatic archiving of move in/move out transactions');
INSERT INTO YukonRoleProperty VALUES(-20208,-202,'Move In/Move Out','true','Controls access to process a move in/move out');
INSERT INTO YukonRoleProperty VALUES(-20209,-202,'Profile Collection Scanning','true','Controls access to start/stop scanning of profile data'); 
INSERT INTO YukonRoleProperty VALUES(-20210,-202,'High Bill Complaint','true','Controls access to process a high bill complaint'); 
INSERT INTO YukonRoleProperty VALUES(-20211,-202,'CIS Info Widget Enabled','true','Controls access to view the CIS Information widget.');
INSERT INTO YukonRoleProperty VALUES(-20212,-202,'CIS Info Type','NONE','Defines the type of CIS Information widget to display. Available placeholders: NONE, MULTISPEAK, CAYENTA');
INSERT INTO YukonRoleProperty VALUES(-20213,-202,'Outage Processing','true','Controls access to Outage Processing');
INSERT INTO YukonRoleProperty VALUES(-20214,-202,'Tamper Flag Processing','false','Controls access to Tamper Flag Processing');
INSERT INTO YukonRoleProperty VALUES(-20215,-202,'Phase Detection','false','Controls access to Phase Detection.');
INSERT INTO YukonRoleProperty VALUES(-20216,-202,'Validation Engine','false','Controls access to Validation Processing');
INSERT INTO YukonRoleProperty VALUES(-20217,-202,'Status Point Monitor','false','Controls access to the Status Point Monitor');
INSERT INTO YukonRoleProperty VALUES(-20218,-202,'Porter Response Monitor','false','Controls access to the Porter Response Monitor');

/* Operator Esubstation Drawings Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20600,-206,'View Drawings','true','Controls viewing of Esubstations drawings');
INSERT INTO YukonRoleProperty VALUES(-20601,-206,'Edit Limits','false','Controls editing of point limits');
INSERT INTO YukonRoleProperty VALUES(-20602,-206,'Control','false','Controls control from Esubstation drawings');
INSERT INTO YukonRoleProperty VALUES(-20603,-206,'Esub Home URL','/esub/sublist.html','The url of the starting page for esubstation. Usually the sublist page.');

/* Odds For Control Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20700,-207,'Odds For Control Label','Odds for Control','The operator specific name for odds for control');

/* Operator Consumer Info Role Properties Part II */
INSERT INTO YukonRoleProperty VALUES(-20800,-201,'Link FAQ',' ','The customized FAQ link');
INSERT INTO YukonRoleProperty VALUES(-20801,-201,'Link Thermostat Instructions',' ','The customized thermostat instructions link');

INSERT INTO YukonRoleProperty VALUES(-20893,-201,'Inventory Checking Create','true','Allow creation of inventory if not found during Inventory Checking');
INSERT INTO YukonRoleProperty VALUES(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-20895,-201,'Opt Out Admin Status','true','Determines whether an operator can see current opt out status on the Opt Out Admin page.');
INSERT INTO YukonRoleProperty VALUES(-20896,-201,'Opt Out Admin Change Enabled','true','Determines whether an operator can enable or disable Opt Outs for the rest of the day.');
INSERT INTO YukonRoleProperty VALUES(-20897,-201,'Opt Out Admin Cancel Current','true','Determines whether an operator can cancel (reenable) ALL currently Opted Out devices.');
INSERT INTO YukonRoleProperty VALUES(-20898,-201,'Opt Out Admin Change Counts','true','Determines whether an operator can change from Opt Outs count against limits today to Opt Outs do not count.'); 

/* Operator Hardware Inventory Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
INSERT INTO YukonRoleProperty VALUES(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardware by serial number range');
INSERT INTO YukonRoleProperty VALUES(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardware by serial number range');
INSERT INTO YukonRoleProperty VALUES(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardware by serial number range');
INSERT INTO YukonRoleProperty VALUES(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardware by serial number range');
INSERT INTO YukonRoleProperty VALUES(-20905,-209,'Create Hardware','true','Controls whether to allow creating new hardware');
INSERT INTO YukonRoleProperty VALUES(-20906,-209,'Expresscom Restore First','false','Controls whether an opt out command should also contain a restore');
INSERT INTO YukonRoleProperty VALUES(-20908,-209,'Multiple Warehouses','false','Allows for inventory to be assigned to multiple user-created warehouses instead of a single generic warehouse.');
INSERT INTO YukonRoleProperty VALUES(-20909,-209,'Purchasing Access','false','Activates the purchasing section of the inventory module.'); 
INSERT INTO YukonRoleProperty VALUES(-20910,-209,'Inventory Configuration','false','Controls access to Inventory Configuration Tool');
INSERT INTO YukonRoleProperty VALUES(-20911,-209,'Inventory Search','true','Enables you to use inventory searching.'); 

/* operator work order management role properties */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');
insert into YukonRoleProperty values(-21002,-210,'Work Order Report','true','Controls whether to allow reporting on work orders');
insert into YukonRoleProperty values(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');

insert into YukonRoleProperty values(-21100,-211,'CI Curtailment Label','CI Curtailment','The operator specific name for C&I Curtailment'); 

/* Scheduler Role Properties */
INSERT INTO YukonRoleProperty VALUES (-21200,-212,'Enable/Disable Scripts','true','Controls access to enable or disable a script.'); 
INSERT INTO YukonRoleProperty VALUES (-21201,-212,'Manage Schedules','true','Controls access to create, delete, or update scheduled reads. If false, access is view only.');

/* Device Actions Role Properties */
INSERT INTO YukonRoleProperty VALUES (-21300,-213,'Bulk Import Operation','true','Controls access to bulk import operations'); 
INSERT INTO YukonRoleProperty VALUES (-21301,-213,'Bulk Update Operation','true','Controls access to bulk update operations'); 
INSERT INTO YukonRoleProperty VALUES (-21302,-213,'Device Group Edit','true','Controls editing of Device Groups (Add/Remove Group, update name, etc.)'); 
INSERT INTO YukonRoleProperty VALUES (-21303,-213,'Device Group Modify','true','Controls modifying contents of a Device Group (Add to/Remove from group, etc.)'); 
INSERT INTO YukonRoleProperty VALUES (-21304,-213,'Group Commander','true','Controls access to group command actions'); 
INSERT INTO YukonRoleProperty VALUES (-21305,-213,'Mass Change','true','Controls access to mass change collection actions. Includes all Mass Change actions.'); 
INSERT INTO YukonRoleProperty VALUES (-21306,-213,'Locate Route','true','Controls access to locate route action'); 
INSERT INTO YukonRoleProperty VALUES (-21307,-213,'Mass Delete','false','Controls access to mass delete devices action'); 
INSERT INTO YukonRoleProperty VALUES (-21308,-213,'Add/Update/Remove Points','false','Controls access to Add, Update and Remove Points collection actions.');
INSERT INTO YukonRoleProperty VALUES (-21309,-213,'Send/Read Configs','false','Controls access to Send Config and Read Config collection actions.');
INSERT INTO YukonRoleProperty VALUES (-21310,-213,'Assign Configs','false','Controls access to Assign Config collection action.');

/* Residential Customer Role Properties */
INSERT INTO YukonRoleProperty VALUES(-40001,-400,'Account General','true','Controls whether to show the general account information');
INSERT INTO YukonRoleProperty VALUES(-40003,-400,'Programs Control History','true','Controls whether to show the program control history');
INSERT INTO YukonRoleProperty VALUES(-40004,-400,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
INSERT INTO YukonRoleProperty VALUES(-40005,-400,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
INSERT INTO YukonRoleProperty VALUES(-40006,-400,'Hardware Thermostat','true','Controls whether to enable the thermostat programming feature');
INSERT INTO YukonRoleProperty VALUES(-40007,-400,'Questions Utility','true','Controls whether to show the contact information of the energy company');
INSERT INTO YukonRoleProperty VALUES(-40008,-400,'Questions FAQ','true','Controls whether to show customer FAQs');
INSERT INTO YukonRoleProperty VALUES(-40009,-400,'Change Login Username','true','Controls access for customers to change their own login username'); 
INSERT INTO YukonRoleProperty VALUES(-40010,-400,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
INSERT INTO YukonRoleProperty VALUES(-40011,-400,'Change Login Password','true','Controls access for customers to change their own login password'); 
INSERT INTO YukonRoleProperty VALUES(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
INSERT INTO YukonRoleProperty VALUES(-40055,-400,'Opt Out Period',' ','The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)');
INSERT INTO YukonRoleProperty VALUES(-40056,-400,'Opt Out Limits',' ','Contains information on Opt Out limits.');
INSERT INTO YukonRoleProperty VALUES(-40100,-400,'Link FAQ',' ','The customized FAQ link');
INSERT INTO YukonRoleProperty VALUES(-40102,-400,'Link Thermostat Instructions',' ','The customized thermostat instructions link');

INSERT INTO YukonRoleProperty VALUES(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
INSERT INTO YukonRoleProperty VALUES(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-40199,-400,'Sign Out Enabled','true','Allows end-users to see a sign-out link when accessing their account pages.'); 
INSERT INTO YukonRoleProperty VALUES(-40200,-400,'Create Login For Account','false','Allows a new login to be automatically created for each contact on a customer account.'); 
INSERT INTO YukonRoleProperty VALUES(-40201,-400,'Opt Out Device Selection','false','Displays a second web page that allows for specific device selection when performing an opt out.'); 
INSERT INTO YukonRoleProperty VALUES(-40202,-400,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.'); 
INSERT INTO YukonRoleProperty VALUES(-40203,-400,'Enrollment per Device','false','Displays a second web page that allows for enrollment by individual device per program.');

/* Capacitor Control role properties */
insert into YukonRoleProperty values(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
insert into YukonRoleProperty values(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
insert into YukonRoleProperty values(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
insert into YukonRoleProperty values(-70004,-700,'Hide One-Lines','false','Sets the visibility of one-line displays.');
insert into YukonRoleProperty values(-70005,-700,'cap_control_interface','amfm','Optional interface to the AMFM mapping system');
insert into YukonRoleProperty values(-70006,-700,'cbc_creation_name','CBC <PAOName>','What text will be added onto CBC names when they are created');
insert into YukonRoleProperty values(-70007,-700,'pfactor_decimal_places','1','How many decimal places to show for real values for PowerFactor');
insert into YukonRoleProperty values(-70008,-700,'Allow OV/UV','false','Allows users to toggle OV/UV usage for capbanks, substations, subs, and feeders.'); 
insert into YukonRoleProperty values(-70010,-700,'Database Editing','false','Allows the user to view/modify the database set up for all CapControl items');
insert into YukonRoleProperty values(-70011,-700,'Show flip command', 'false', 'Show flip command for Cap Banks with 7010 type controller');
insert into YukonRoleProperty values(-70012,-700,'Show Cap Bank Add Info','false','Show Cap Bank Addititional Info tab');
insert into YukonRoleProperty values(-70013,-700,'Definition Available','Switched:Open,Switched:OpenQuestionable,Switched:OpenPending,StandAlone:Open,StandAlone:OpenQuestionable,StandAlone:OpenPending','Capbank sized in these states will be added to the available sum.');
insert into YukonRoleProperty values(-70014,-700,'Definition Unavailable','Switched:Close,Switched:CloseQuestionable,Switched:CloseFail,Switched:ClosePending,Switched:OpenFail,StandAlone:Close,StandAlone:CloseQuestionable,StandAlone:CloseFail,StandAlone:ClosePending,StandAlone:OpenFail,Fixed:Open,Disabled:Open','Capbank sized in these states will be added to the unavailable sum.');
insert into YukonRoleProperty values(-70015,-700,'Definition Tripped','Switched:Open,Switched:OpenFail,Switched:OpenPending,Switched:OpenQuestionable,StandAlone:Open,StandAlone:OpenFail,StandAlone:OpenPending,StandAlone:OpenQuestionable','Capbank sized in these states will be added to the tripped sum.');
insert into YukonRoleProperty values(-70016,-700,'Definition Closed','Switched:Close,Switched:CloseFail,Switched:CloseQuestionable,Switched:ClosePending,StandAlone:Close,StandAlone:CloseFail,StandAlone:CloseQuestionable,StandAlone:ClosePending,Fixed:Close,Fixed:CloseFail,Fixed:CloseQuestionable,Fixed:ClosePending,Disabled:Close,Disabled:CloseFail,Disabled:CloseQuestionable,Disabled:ClosePending','Capbank sized in these states will be added to the closed sum.');
insert into yukonroleproperty values(-70017,-700,'Add Comments', 'false', 'Allows the user to Add comments to Cap Bank objects.');
insert into yukonroleproperty values(-70018,-700,'Modify Comments', 'false', 'Allows the user to Modify comments on Cap Bank objects.');
insert into yukonroleproperty values(-70019,-700,'System Wide Controls', 'false', 'Allow system wide controls');
INSERT INTO YukonRoleProperty VALUES(-70020,-700,'Force Default Comment', 'false', 'If the user does not provide a comment, a default comment will be stored.'); 
INSERT INTO YukonRoleProperty VALUES(-70021,-700,'Allow Area Control','true','Enables or disables field and local Area controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70022,-700,'Allow Substation Control','true','Enables or disables field and local Substation controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70023,-700,'Allow SubBus Control','true','Enables or disables field and local Substation Bus controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70024,-700,'Allow Feeder Control','true','Enables or disables field and local Feeder controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70025,-700,'Allow Capbank/CBC Control','true','Enables or disables field and local Capbank/CBC controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70026,-700,'Warn on control send.','true','If true the user will be asked if they are sure they want to send that command.');


/* Notification / IVR Role properties */
INSERT INTO YukonRoleProperty VALUES(-80001,-800,'Number of Channels','1','The number of outgoing channels assigned to the specified voice application.');
INSERT INTO YukonRoleProperty VALUES(-80004,-800,'IVR URL Dialer Template','http://127.0.0.1:9998/VoiceXML.start?tokenid=yukon-{MESSAGE_TYPE}&numbertodial={PHONE_NUMBER}','The URL used to initiate a call, see documentation for allowed variables'); 
INSERT INTO YukonRoleProperty VALUES(-80005,-800,'IVR URL Dialer Success Matcher','success','A Java Regular Expression that will be matched against the output of the URL to determine if the call was successful'); 

/* Notification / Configuration role properties */
insert into YukonRoleProperty values(-80100,-801,'Template Root','Server/web/webapps/ROOT/WebConfig/custom/notif_templates/','Either a URL base where the notification templates will be stored (file: or http:) or a directory relative to YUKON_BASE.');

/* Loadcontrol Role Properties */
INSERT INTO YukonRoleProperty VALUES (-90001,-900,'Individual Switch','true','Controls access to operator individual switch control');
INSERT INTO YukonRoleProperty VALUES (-90002,-900,'Demand Response','false','Allows access to the Demand Response control web application');
INSERT INTO YukonRoleProperty VALUES (-90003,-900,'Direct Loadcontrol','true','Allows access to the Direct load management web interface');
INSERT INTO YukonRoleProperty VALUES (-90004,-900,'Constraint Check','true','Allow load management program constraints to be CHECKED before starting');
INSERT INTO YukonRoleProperty VALUES (-90005,-900,'Constraint Observe','true','Allow load management program constraints to be OBSERVED before starting');
INSERT INTO YukonRoleProperty VALUES (-90006,-900,'Constraint Override','true','Allow load management program constraints to be OVERRIDDEN before starting');
INSERT INTO YukonRoleProperty VALUES (-90007,-900,'Constraint Default','Check','The default program constraint selection prior to starting a program');
INSERT INTO YukonRoleProperty VALUES (-90008,-900,'Allow Gear Change for Stop','false','Activates the ability to change gears as part of manually stopping a load program'); 
INSERT INTO YukonRoleProperty VALUES (-90009,-900,'Ignore LM Pao Permissions','false','Allow access to all load management objects. Set to false to force the use of per pao permissions.');
INSERT INTO YukonRoleProperty VALUES (-90010,-900,'Control Areas','true','Controls access to view Control Areas');
INSERT INTO YukonRoleProperty VALUES (-90011,-900,'Scenarios','true','Controls access to view Scenarios');

INSERT INTO YukonRoleProperty VALUES (-90020,-900,'Control Area State','true','Controls access to view Control Area State');
INSERT INTO YukonRoleProperty VALUES (-90021,-900,'Control Area Trigger Value/Threshold','true','Controls access to view Control Area Trigger Value/Threshold');
INSERT INTO YukonRoleProperty VALUES (-90022,-900,'Control Area Trigger Peak/Projection','true','Controls access to view Control Area Trigger Peak/Projection');
INSERT INTO YukonRoleProperty VALUES (-90023,-900,'Control Area Trigger ATKU','true','Controls access to view Control Area Trigger ATKU');
INSERT INTO YukonRoleProperty VALUES (-90024,-900,'Control Area Priority','true','Controls acces to view Control Area Priority');
INSERT INTO YukonRoleProperty VALUES (-90025,-900,'Control Area Time Window','true','Controls access to view Control Area Time Window');
INSERT INTO YukonRoleProperty VALUES (-90026,-900,'Control Area Load Capacity','true','Controls access to view Control Area Load Capacity');

INSERT INTO YukonRoleProperty VALUES (-90027,-900,'Program State','true','Controls access to view Program State');
INSERT INTO YukonRoleProperty VALUES (-90028,-900,'Program Start','true','Controls access to view Program Start');
INSERT INTO YukonRoleProperty VALUES (-90029,-900,'Program Stop','true','Controls access to view Program Stop');
INSERT INTO YukonRoleProperty VALUES (-90030,-900,'Program Current Gear','true','Controls access to view Program Current Gear');
INSERT INTO YukonRoleProperty VALUES (-90031,-900,'Program Priority','true','Controls access to view Program Priority');
INSERT INTO YukonRoleProperty VALUES (-90032,-900,'Program Reduction','true','Controls access to view Program Reduction');
INSERT INTO YukonRoleProperty VALUES (-90033,-900,'Program Load Capacity','true','Controls access to view Program Load Capacity');

INSERT INTO YukonRoleProperty VALUES (-90034,-900,'Load Group State','true', 'Controls access to view Load Group State');
INSERT INTO YukonRoleProperty VALUES (-90035,-900,'Load Group Last Action','true', 'Controls access to view Load Group Last Action');
INSERT INTO YukonRoleProperty VALUES (-90036,-900,'Load Group Control Statistics','true', 'Controls access to view Load Group Control Statistics');
INSERT INTO YukonRoleProperty VALUES (-90037,-900,'Load Group Reduction','true', 'Controls access to view Load Group Reduction');
INSERT INTO YukonRoleProperty VALUES (-90038,-900,'Load Group Load Capacity','true', 'Controls access to view Load Group Load Capacity'); 
INSERT INTO YukonRoleProperty VALUES (-90039,-900,'Start Now Checked By Default','true', 'Controls whether the start now checkbox is checked by default in demand response.');
INSERT INTO YukonRoleProperty VALUES (-90040,-900,'Control Duration Default','240', 'Specifies the default duration for a control event in minutes for demand response');
INSERT INTO YukonRoleProperty VALUES (-90041,-900,'Schedule Stop Checked By Default', 'true', 'Controls whether the schedule stop check box is checked by default in demand response.');
INSERT INTO YukonRoleProperty VALUES (-90042,-900,'Start Time Default',' ', 'Specifies the default start time for a control event in the format (hh:mm). It will use the current time if no time has been supplied');

/* Capacitor Control role properties cont...*/
insert into YukonRoleProperty values(-100000, -1000, 'Target', 'true', 'display Target settings');
insert into YukonRoleProperty values(-100001, -1000, 'kVAR', 'true', 'display kVAR');
insert into YukonRoleProperty values(-100002, -1000, 'Estimated kVAR', 'true', 'display estimated kVAR');
insert into YukonRoleProperty values(-100003, -1000, 'Power Factor', 'true', 'display Power Factor');
insert into YukonRoleProperty values(-100004, -1000, 'Estimated Power Factor', 'true', 'display estimated Power Factor');
insert into YukonRoleProperty values(-100005, -1000, 'Watts', 'true', 'display Watts');
insert into YukonRoleProperty values(-100006, -1000, 'Volts', 'true', 'display Volts');

insert into YukonRoleProperty values(-100100, -1001, 'kVAR', 'true', 'display kVAR');
insert into YukonRoleProperty values(-100101, -1001, 'Power Factor', 'true', 'display Power Factor');
insert into YukonRoleProperty values(-100102, -1001, 'Watts', 'false', 'display Watts');
insert into YukonRoleProperty values(-100103, -1001, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into YukonRoleProperty values(-100104, -1001, 'Volts', 'false', 'display Volts');
insert into YukonRoleProperty values(-100105, -1001, 'Target', 'true', 'is target stat displayed');

insert into yukonroleproperty values(-100107, -1001, 'Watt/Volt', 'true', 'display Watts/Volts');
insert into yukonroleproperty values(-100108, -1001, 'Three Phase', 'false', 'display 3-phase data for feeder'); 

insert into YukonRoleProperty values(-100201, -1002, 'Bank Size', 'true', 'display Bank Size');
insert into YukonRoleProperty values(-100202, -1002, 'CBC Name', 'true', 'display CBC Name');

INSERT INTO YukonRoleProperty VALUES (-100011, -1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
INSERT INTO YukonRoleProperty VALUES (-100012, -1000, 'Substation Last Update Timestamp', 'true', 'is last update timestamp shown for substations');
INSERT INTO YukonRoleProperty VALUES (-100013, -1000, 'Three Phase', 'false', 'display 3-phase data for sub bus');

insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timestamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timestamp shown for capbanks');
insert into YukonRoleProperty values (-100205,-1002, 'Capbank Fixed/Static Text', 'Fixed', 'The text to display for fixed/static capbanks');
insert into yukonroleproperty values (-100206,-1002, 'Daily/Max/Total Operation Count', 'true', 'is Daily/Max/Total Operation Count displayed.');

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
   EnergyCompanyId      NUMBER                          not null,
   constraint PK_YUKONSELECTIONLIST primary key (ListID)
);

INSERT INTO YukonSelectionList VALUES( 0,'N','(none)','(none)','(none)','N',-1);
INSERT INTO YukonSelectionList VALUES( 1,'A','Contact','DBEditor contact type list','ContactType','N',-1);

INSERT INTO YukonSelectionList VALUES(100,'A','Calc Functions','DBEditor calc point functions','CalcFunctions','N',-1);

INSERT INTO YukonSelectionList VALUES(1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N',-1);
INSERT INTO YukonSelectionList VALUES(1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N',-1);
INSERT INTO YukonSelectionList VALUES(1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N',-1);
INSERT INTO YukonSelectionList VALUES(1004,'A','(none)','Device voltage selection','DeviceVoltage','Y',-1);
INSERT INTO YukonSelectionList VALUES(1005,'A','(none)','Device type selection','DeviceType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1006,'N','(none)','Hardware status selection','DeviceStatus','Y',-1);
INSERT INTO YukonSelectionList VALUES(1007,'A','(none)','Appliance category','ApplianceCategory','N',-1);
INSERT INTO YukonSelectionList VALUES(1008,'A','(none)','Call type selection','CallType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1009,'A','(none)','Service type selection','ServiceType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1010,'N','(none)','Service request status','ServiceStatus','N',-1);
INSERT INTO YukonSelectionList VALUES(1011,'N','(none)','Search by selection','SearchBy','N',-1);
INSERT INTO YukonSelectionList VALUES(1012,'A','(none)','Appliance manufacturer selection','Manufacturer','Y',-1);
INSERT INTO YukonSelectionList VALUES(1013,'A','(none)','Appliance location selection','ApplianceLocation','Y',-1);
INSERT INTO YukonSelectionList VALUES(1014,'N','(none)','Chance of control selection','ChanceOfControl','Y',-1);
INSERT INTO YukonSelectionList VALUES(1015,'N','(none)','Thermostat settings time of week selection','TimeOfWeek','N',-1);
INSERT INTO YukonSelectionList VALUES(1018,'N','(none)','Thermostat mode selection','ThermostatMode','N',-1);
INSERT INTO YukonSelectionList VALUES(1019,'N','(none)','Thermostat fan state selection','ThermostatFanState','N',-1);
INSERT INTO YukonSelectionList VALUES(1021,'N','(none)','Residence type selection','ResidenceType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1022,'N','(none)','Construction material selection','ConstructionMaterial','Y',-1);
INSERT INTO YukonSelectionList VALUES(1023,'N','(none)','Decade built selection','DecadeBuilt','Y',-1);
INSERT INTO YukonSelectionList VALUES(1024,'N','(none)','Square feet selection','SquareFeet','Y',-1);
INSERT INTO YukonSelectionList VALUES(1025,'N','(none)','Insulation depth selection','InsulationDepth','Y',-1);
INSERT INTO YukonSelectionList VALUES(1026,'N','(none)','General condition selection','GeneralCondition','Y',-1);
INSERT INTO YukonSelectionList VALUES(1027,'N','(none)','Main cooling system selection','CoolingSystem','Y',-1);
INSERT INTO YukonSelectionList VALUES(1028,'N','(none)','Main heating system selection','HeatingSystem','Y',-1);
INSERT INTO YukonSelectionList VALUES(1029,'N','(none)','Number of occupants selection','NumberOfOccupants','Y',-1);
INSERT INTO YukonSelectionList VALUES(1030,'N','(none)','Ownership type selection','OwnershipType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1031,'N','(none)','Main fuel type selection','FuelType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1032,'N','(none)','AC Tonnage selection','ACTonnage','Y',-1);
INSERT INTO YukonSelectionList VALUES(1033,'N','(none)','AC type selection','ACType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1034,'N','(none)','Water heater number of gallons selection','WHNumberOfGallons','Y',-1);
INSERT INTO YukonSelectionList VALUES(1035,'N','(none)','Water heater energy source selection','WHEnergySource','Y',-1);
INSERT INTO YukonSelectionList VALUES(1036,'N','(none)','Dual fuel switch over type selection','DFSwitchOverType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1037,'N','(none)','Dual fuel secondary source selection','DFSecondarySource','Y',-1);
INSERT INTO YukonSelectionList VALUES(1038,'N','(none)','Grain dryer type selection','GrainDryerType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1039,'N','(none)','Grain dryer bin size selection','GDBinSize','Y',-1);
INSERT INTO YukonSelectionList VALUES(1040,'N','(none)','Grain dryer blower energy source selection','GDEnergySource','Y',-1);
INSERT INTO YukonSelectionList VALUES(1041,'N','(none)','Grain dryer blower horse power selection','GDHorsePower','Y',-1);
INSERT INTO YukonSelectionList VALUES(1042,'N','(none)','Grain dryer blower heat source selection','GDHeatSource','Y',-1);
INSERT INTO YukonSelectionList VALUES(1043,'N','(none)','Storage heat type selection','StorageHeatType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1044,'N','(none)','Heat pump type selection','HeatPumpType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1045,'N','(none)','Heat pump standby source selection','HPStandbySource','Y',-1);
INSERT INTO YukonSelectionList VALUES(1046,'N','(none)','Irrigation type selection','IrrigationType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1047,'N','(none)','Irrigation soil type selection','IRRSoilType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1048,'N','(none)','Device location selection','DeviceLocation','N',-1);
INSERT INTO YukonSelectionList VALUES(1051,'N','(none)','Hardware Inventory search by selection','InvSearchBy','N',-1);
INSERT INTO YukonSelectionList VALUES(1052,'N','(none)','Hardware Inventory sort by selection','InvSortBy','N',-1);
INSERT INTO YukonSelectionList VALUES(1053,'N','(none)','Hardware Inventory filter by selection','InvFilterBy','N',-1);
INSERT INTO YukonSelectionList VALUES(1054,'N','(none)','Service order search by selection','SOSearchBy','N',-1);
INSERT INTO YukonSelectionList VALUES(1055,'N','(none)','Service order sort by selection','SOSortBy','N',-1);
INSERT INTO YukonSelectionList VALUES(1056,'N','(none)','Service order filter by selection','SOFilterBy','N',-1);
INSERT INTO YukonSelectionList VALUES(1057,'N','(none)','Generator transfer switch type selection','GENTransferSwitchType','Y',-1);
INSERT INTO YukonSelectionList VALUES(1058,'N','(none)','Generator transfer switch manufacturer selection','GENTransferSwitchMfg','Y',-1);
INSERT INTO YukonSelectionList VALUES(1059,'N','(none)','Irrigation horse power selection','IRRHorsePower','Y',-1);
INSERT INTO YukonSelectionList VALUES(1060,'N','(none)','Irrigation energy source selection','IRREnergySource','Y',-1);
INSERT INTO YukonSelectionList VALUES(1061,'N','(none)','Irrigation meter location selection','IRRMeterLocation','Y',-1);
INSERT INTO YukonSelectionList VALUES(1062,'N','(none)','Irrigation meter voltage selection','IRRMeterVoltage','Y',-1);
INSERT INTO YukonSelectionList VALUES(1063,'N','(none)','Water heater location selection','WHLocation','Y',-1);
INSERT INTO YukonSelectionList VALUES(1064,'N','(none)','Heat pump size selection','HeatPumpSize','Y',-1);
INSERT INTO YukonSelectionList VALUES(1065,'A','(none)','Customer account rate schedule selection','RateSchedule','Y',-1);
INSERT INTO YukonSelectionList VALUES(1066,'A','(none)','Energy Company Settlement Types','Settlement','Y',-1);
INSERT INTO YukonSelectionList VALUES(1067,'A','(none)','System category types for Event Logging in STARS', 'EventSystemCategory', 'N',-1);
INSERT INTO YukonSelectionList VALUES(1068,'A','(none)','Action types for Customer Account events in STARS', 'EventAccountActions', 'N',-1);
INSERT INTO YukonSelectionList VALUES(1071,'A','(none)','Commercial Customer Types','CICustomerType','N',-1);
INSERT INTO YukonSelectionList VALUES(1072,'N','(none)','Cap Bank Editor','Controller Type','N',-1);
INSERT INTO YukonSelectionList VALUES(1073,'N','(none)','Cap Bank Editor','Switch Manufacturer','N',-1);
INSERT INTO YukonSelectionList VALUES(1074,'N','(none)','Cap Bank Editor','Type of Switch','N',-1);

/*==============================================================*/
/* Index: Indx_YSL_ListName_ECId_UNQ                            */
/*==============================================================*/
create unique index Indx_YSL_ListName_ECId_UNQ on YukonSelectionList (
   ListName ASC,
   EnergyCompanyId ASC
);

/*==============================================================*/
/* Table: YukonServices                                         */
/*==============================================================*/
create table YukonServices  (
   ServiceID            NUMBER                          not null,
   ServiceName          VARCHAR2(60)                    not null,
   ServiceClass         VARCHAR2(100)                   not null,
   AppName              VARCHAR2(100)                   not null,
   constraint PK_YUKSER primary key (ServiceID)
);

INSERT INTO YukonServices VALUES (-2, 'WebGraph', 'com.cannontech.jmx.services.DynamicWebGraph', 'ServiceManager');
INSERT INTO YukonServices VALUES (-3, 'Calc_Historical', 'com.cannontech.jmx.services.DynamicCalcHist', 'ServiceManager');
INSERT INTO YukonServices VALUES (-5, 'MCT410_BulkImporter', 'com.cannontech.jmx.services.DynamicImp', 'ServiceManager');
INSERT INTO YukonServices VALUES (-6, 'Price_Server', 'com.cannontech.jmx.services.DynamicPriceServer', 'ServiceManager');
INSERT INTO YukonServices VALUES (8, 'PointInjector', 'classpath:com/cannontech/services/points/pointInjectionContext.xml', 'ServiceManager');
INSERT INTO YukonServices VALUES (9, 'Monitors', 'classpath:com/cannontech/services/monitors/monitorsContext.xml', 'ServiceManager');
INSERT INTO YukonServices VALUES (10, 'OptOut', 'classpath:com/cannontech/services/optout/optOutContext.xml', 'ServiceManager');
INSERT INTO YukonServices VALUES (11, 'RawPointHistoryValidation', 'classpath:com/cannontech/services/validation/validationServerContext.xml', 'ServiceManager');
INSERT INTO YukonServices VALUES (13, 'Eka', 'classpath:com/cannontech/services/rfn/rfnMeteringContext.xml', 'ServiceManager');
INSERT INTO yukonServices VALUES (14, 'Inventory Management', 'classpath:com/cannontech/services/dr/inventoryContext.xml', 'ServiceManager'); 
INSERT INTO YukonServices VALUES (15, 'PorterResponseMonitor', 'classpath:com/cannontech/services/porterResponseMonitor/porterResponseMonitorContext.xml', 'ServiceManager');
INSERT INTO YukonServices VALUES (16, 'SepMessageListener', 'classpath:com/cannontech/services/sepMessageListener/sepMessageListenerContext.xml', 'ServiceManager'); 
INSERT INTO YukonServices VALUES (17, 'DigiPollingService', 'classpath:com/cannontech/services/digiPollingService/digiPollingService.xml', 'ServiceManager');
INSERT INTO YukonServices VALUES (18, 'CymDISTMessageListener', 'classpath:com/cannontech/services/cymDISTService/cymDISTServiceContext.xml', 'ServiceManager');

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
);

insert into yukonuser values ( -9999, '(none)', '(none)', 'Disabled', 'PLAIN' );
insert into yukonuser values ( -100, 'DefaultCTI', '$cti_default', 'Enabled', 'PLAIN' );
insert into yukonuser values ( -2, 'yukon', 'yukon', 'Enabled', 'PLAIN' );
insert into yukonuser values ( -1, 'admin', 'admin', 'Enabled', 'PLAIN' );

/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique index Indx_YkUsIDNm on YukonUser (
   UserName ASC
);

/*==============================================================*/
/* Table: YukonUserGroup                                        */
/*==============================================================*/
create table YukonUserGroup  (
   UserID               NUMBER                          not null,
   GroupID              NUMBER                          not null,
   constraint PK_YUKONUSERGROUP primary key (UserID, GroupID)
);

INSERT INTO YukonUserGroup VALUES(-1,-1);
INSERT INTO YukonUserGroup VALUES(-2,-1);
INSERT INTO YukonUserGroup VALUES(-2,-2);

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
);

/* Database Editor */
INSERT INTO YukonUserRole VALUES(-100,-1,-100,-10000,' ');
INSERT INTO YukonUserRole VALUES(-102,-1,-100,-10002,' ');
INSERT INTO YukonUserRole VALUES(-104,-1,-100,-10004,' ');
INSERT INTO YukonUserRole VALUES(-105,-1,-100,-10005,' ');
INSERT INTO YukonUserRole VALUES(-107,-1,-100,-10007,'true');
INSERT INTO YukonUserRole VALUES(-108,-1,-100,-10008,' ');
INSERT INTO YukonUserRole VALUES(-109,-1,-100,-10009,' ');

/* TDC */
INSERT INTO YukonUserRole VALUES(-120,-1,-101,-10100,' ');
INSERT INTO YukonUserRole VALUES(-121,-1,-101,-10101,' ');
INSERT INTO YukonUserRole VALUES(-122,-1,-101,-10102,' ');
INSERT INTO YukonUserRole VALUES(-123,-1,-101,-10103,' ');
INSERT INTO YukonUserRole VALUES(-124,-1,-101,-10104,' ');
INSERT INTO YukonUserRole VALUES(-127,-1,-101,-10107,' ');
INSERT INTO YukonUserRole VALUES(-128,-1,-101,-10108,' ');
INSERT INTO YukonUserRole VALUES(-130,-1,-101,-10111,' ');

/* Trending */
INSERT INTO YukonUserRole VALUES(-150,-1,-102,-10200,' ');
INSERT INTO YukonUserRole VALUES(-152,-1,-102,-10202,' ');
INSERT INTO YukonUserRole VALUES(-153,-1,-102,-10203,' ');
INSERT INTO YukonUserRole VALUES(-155,-1,-102,-10205,' ');
INSERT INTO YukonUserRole VALUES(-156,-1,-102,-10206,' ');

/* Commander */
INSERT INTO YukonUserRole VALUES(-170,-1,-103,-10300,' ');
INSERT INTO YukonUserRole VALUES(-171,-1,-103,-10301,'true');
INSERT INTO YukonUserRole VALUES(-172,-1,-103,-10302,'true');
INSERT INTO YukonUserRole VALUES(-173,-1,-103,-10303,'false');
INSERT INTO YukonUserRole VALUES(-174,-1,-103,-10304,'false');
INSERT INTO YukonUserRole VALUES(-175,-1,-103,-10305,' ');

/* Esubstation Editor */
INSERT INTO YukonUserRole VALUES(-250,-1,-107,-10700,' ');

/* Esub Drawings */
INSERT INTO YukonUserRole VALUES(-300,-1,-206,-20600,' ');
INSERT INTO YukonUserRole VALUES(-301,-1,-206,-20601,' ');
INSERT INTO YukonUserRole VALUES(-302,-1,-206,-20602,' ');

/* Web Client Customers Web Client role */
INSERT INTO YukonUserRole VALUES(-400, -1, -108, -10800, '/operator/Operations.jsp');
INSERT INTO YukonUserRole VALUES(-402, -1, -108, -10802, ' ');
INSERT INTO YukonUserRole VALUES(-403, -1, -108, -10803, ' ');
INSERT INTO YukonUserRole VALUES(-404, -1, -108, -10804, ' ');
INSERT INTO YukonUserRole VALUES(-405, -1, -108, -10805, ' ');
INSERT INTO YukonUserRole VALUES(-406, -1, -108, -10806, ' ');
INSERT INTO YukonUserRole VALUES(-407, -1, -108, -10807, ' ');
INSERT INTO YukonUserRole VALUES(-408, -1, -108, -10808, ' ');
INSERT INTO YukonUserRole VALUES(-41001,-1, -108, -10811, ' ');

/* Give admin login access to View Logs, */
INSERT INTO YukonUserRole VALUES(-2000, -1, -200, -20009, ' ');
INSERT INTO YukonUserRole VALUES(-2001, -1, -200, -20019, 'true');

INSERT INTO YukonUserRole VALUES(-1000, -100, -108, -10800, '/operator/Operations.jsp');
INSERT INTO YukonUserRole VALUES(-1002, -100, -108, -10802, ' ');
INSERT INTO YukonUserRole VALUES(-1003, -100, -108, -10803, ' ');
INSERT INTO YukonUserRole VALUES(-1004, -100, -108, -10804, ' ');
INSERT INTO YukonUserRole VALUES(-1005, -100, -108, -10805, ' ');
INSERT INTO YukonUserRole VALUES(-1006, -100, -108, -10806, ' ');
INSERT INTO YukonUserRole VALUES(-1007, -100, -108, -10811, ' ');
INSERT INTO YukonUserRole VALUES(-1010, -100, -200, -20000, ' ');
INSERT INTO YukonUserRole VALUES(-1011, -100, -200, -20001, 'true');

INSERT INTO YukonUserRole VALUES(-1013, -100, -200, -20003, ' ');
INSERT INTO YukonUserRole VALUES(-1014, -100, -200, -20004, ' ');
INSERT INTO YukonUserRole VALUES(-1015, -100, -200, -20005, ' ');
INSERT INTO YukonUserRole VALUES(-1016, -100, -200, -20006, ' ');
INSERT INTO YukonUserRole VALUES(-1017, -100, -200, -20007, ' ');
INSERT INTO YukonUserRole VALUES(-1019, -100, -200, -20009, ' ');
INSERT INTO YukonUserRole VALUES(-1020, -100, -200, -20019, 'true');

/* Adding the Energy Company Role to DefaultCTI */
INSERT INTO YukonUserRole VALUES(-1100, -100, -2, -1100, ' ');
INSERT INTO YukonUserRole VALUES(-1101, -100, -2, -1101, ' ');
INSERT INTO YukonUserRole VALUES(-1102, -100, -2, -1102, ' ');
INSERT INTO YukonUserRole VALUES(-1107, -100, -2, -1107, ' ');
INSERT INTO YukonUserRole VALUES(-1108, -100, -2, -1108, ' ');
INSERT INTO YukonUserRole VALUES(-1109, -100, -2, -1109, ' ');
INSERT INTO YukonUserRole VALUES(-1110, -100, -2, -1110, ' ');
INSERT INTO YukonUserRole VALUES(-1111, -100, -2, -1111, ' ');
INSERT INTO YukonUserRole VALUES(-1112, -100, -2, -1112, ' ');
INSERT INTO YukonUserRole VALUES(-1114, -100, -2, -1114, ' ');

/* Adding the IVR role to DefaultCTI */
INSERT INTO YukonUserRole VALUES(-1200, -100, -801, -80100, ' '); 

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
);

INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Cooling','Cool');
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Heating','Heat');
insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');

/*==============================================================*/
/* Table: ZBControlEvent                                        */
/*==============================================================*/
create table ZBControlEvent  (
   ZBControlEventId     NUMBER                          not null,
   IntegrationType      VARCHAR2(50)                    not null,
   StartTime            DATE                            not null,
   GroupId              NUMBER                          not null,
   LMControlHistoryId   NUMBER,
   constraint PK_ZBContEvent primary key (ZBControlEventId)
);

/*==============================================================*/
/* Table: ZBControlEventDevice                                  */
/*==============================================================*/
create table ZBControlEventDevice  (
   ZBControlEventId     NUMBER                          not null,
   DeviceId             NUMBER                          not null,
   DeviceAck            CHAR(1)                         not null,
   StartTime            DATE,
   StopTime             DATE,
   Canceled             CHAR(1),
   constraint PK_ZBContEventDev primary key (ZBControlEventId, DeviceId)
);

/*==============================================================*/
/* Table: ZBEndPoint                                            */
/*==============================================================*/
create table ZBEndPoint  (
   DeviceId             NUMBER                          not null,
   InstallCode          VARCHAR2(255)                   not null,
   MacAddress           VARCHAR2(255)                   not null,
   NodeId               NUMBER                          not null,
   DestinationEndPointId NUMBER                          not null,
   constraint PK_ZBEndPoint primary key (DeviceId)
);

/*==============================================================*/
/* Table: ZBGateway                                             */
/*==============================================================*/
create table ZBGateway  (
   DeviceId             NUMBER                          not null,
   FirmwareVersion      VARCHAR2(255)                   not null,
   MacAddress           VARCHAR2(255)                   not null,
   constraint PK_ZBGateway primary key (DeviceId)
);

/*==============================================================*/
/* Table: ZBGatewayToDeviceMapping                              */
/*==============================================================*/
create table ZBGatewayToDeviceMapping  (
   GatewayId            NUMBER                          not null,
   DeviceId             NUMBER                          not null,
   constraint PK_ZBGateToDeviceMap primary key (GatewayId, DeviceId)
);

/*==============================================================*/
/* Table: Zone                                                  */
/*==============================================================*/
create table Zone  (
   ZoneId               NUMBER                          not null,
   ZoneName             VARCHAR2(255)                   not null,
   SubstationBusId      NUMBER                          not null,
   ParentId             NUMBER,
   GraphStartPosition   FLOAT,
   ZoneType             VARCHAR2(40)                    not null,
   constraint PK_Zone primary key (ZoneId)
);

/*==============================================================*/
/* View: CBCConfiguration2_View                                 */
/*==============================================================*/
create or replace view CBCConfiguration2_View as
SELECT YP.PAOName AS CBCName, D.* 
FROM DynamicCCTwoWayCBC D, YukonPAObject YP
WHERE YP.PAObjectId = D.DeviceId;

/*==============================================================*/
/* View: CBCConfiguration_View                                  */
/*==============================================================*/
create or replace view CBCConfiguration_View as
SELECT YP.PAOName AS CBCName, YP.PAObjectId AS CBCId, P.PointName AS PointName, P.PointId AS PointId, 
       PD.Value AS PointValue, PD.Timestamp, UOM.UOMName AS UnitOfMeasure
FROM Point P
JOIN YukonPAObject YP ON YP.PAObjectId = P.PAObjectId AND YP.Type like 'CBC 702%'
LEFT OUTER JOIN DynamicPointDispatch PD ON PD.PointId = P.PointId
LEFT OUTER JOIN PointUnit PU ON PU.PointId = P.PointId
LEFT OUTER JOIN UnitMeasure UOM ON UOM.UOMId = PU.UOMId;

/*==============================================================*/
/* View: CCCBCCVMSState_View                                    */
/*==============================================================*/
create or replace view CCCBCCVMSState_View as
SELECT YP5.PAOName AS Region, YP4.PAOName AS Substation, CB.MapLocationId AS OpCenter, 
       YP3.PAOName AS SubName, YP2.PAOName AS FeederName, YP1.PAOName AS CapBankName, 
       YP.PAOName AS CBCName, S.Text AS CapBankStatus, S1.Text AS CBCStatus, 
       CASE WHEN S.Text = S1.Text THEN 'No' ELSE 'Yes' END AS IsMisMatch, 
       DCB.LastStatusChangeTime AS CapBankChangeTime, DCB.TwoWayCBCStateTime AS CBCChangeTime
FROM (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
      FROM YukonPAObject
      WHERE  (Type LIKE 'CBC 702%')) YP 
LEFT OUTER JOIN CapBank CB ON CB.ControlDeviceId = YP.PAObjectId AND CB.ControlDeviceId > 0 
INNER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
            FROM YukonPAObject YukonPAObject_3
            WHERE (Type LIKE 'CAP BANK')) YP1 ON YP1.PAObjectId = cb.DeviceId 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_2
                 WHERE (Type LIKE 'CCFEEDER')) YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_1
                 WHERE (Type LIKE 'CCSUBBUS')) YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SS ON SS.SubstationBusId = SF.SubStationBusId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_1
                 WHERE (Type LIKE 'CCSUBSTATION')) YP4 ON YP4.PAObjectId = SS.SubStationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SS.SubStationId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_1
                 WHERE (Type LIKE 'CCAREA')) YP5 ON YP5.PAObjectId = SA.AreaId 
INNER JOIN DynamicCCCapBank DCB ON DCB.CapBankId = CB.DeviceId 
INNER JOIN State S ON S.StateGroupId = 3 AND DCB.ControlStatus = S.RawState 
LEFT OUTER JOIN State S1 ON S1.StateGroupId = 3 AND DCB.TwoWayCBCState = S1.RawState;

/*==============================================================*/
/* View: CCCBCInventory_View                                    */
/*==============================================================*/
create or replace view CCCBCInventory_View(CBCNAME, IPADDRESS, SLAVEADDRESS, CONTROLLERTYPE, OPCENTER, REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, CAPBANKNAME, BANKSIZE, OPERATIONMETHOD, LAT, LON, DRIVEDIRECTION, CAPBANKADDRESS, TA, CAPBANKCONFIG, COMMMEDIUM, COMMSTRENGTH, EXTERNALANTENNA, OPERATIONSCOUNTERRESETDATE, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
SELECT YP.PAOName AS CBCName, DPI.Value AS IPAddress, DA.SlaveAddress, CB.ControllerType, 
       CB.MapLocationId AS OpCenter, YP5.PAOName AS Region, YP4.PAOName AS SubstationName, 
       YP3.PAOName AS SubBusName, YP2.PAOName AS FeederName, YP1.PAOName AS CapBankName, 
       CB.BankSize, CB.OperationalState AS OperationMethod, CAPA.Latitude AS Lat, 
       CAPA.Longitude AS Lon, CAPA.DriveDirections AS DriveDirection, 
       YP1.Description AS CapBankAddress, CAPA.MaintenanceAreaId AS TA, CAPA.CapBankConfig,
       CAPA.CommMedium, CAPA.CommStrength, CAPA.ExtAntenna AS ExternalAntenna, 
       CAPA.OpCountResetDate AS OperationsCounterResetDate, 
       DTWC.TotalOpCount AS OpsCounterSinceLastReset, 
       DTWC.TotalOpCount AS OperationsCounterToday, DTWC.UvOpCount AS UvOperationsCounter, 
       DTWC.OvOpCount AS OvOperationsCounter, DTWC.OvUvCountResetDate AS UvOvCounterResetDate, 
       DTWC.LastOvUvDateTime
FROM (SELECT PAObjectId, PAOName 
      FROM YukonPAObject 
      WHERE (Type LIKE '%CBC%')) YP 
LEFT OUTER JOIN CapBank CB ON CB.ControlDeviceId = YP.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP1 ON CB.DeviceId = YP1.PAObjectId 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = YP3.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SSL.SubstationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SSL.SubstationId 
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SA.AreaId 
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId;

/*==============================================================*/
/* View: CCCapInventory_View                                    */
/*==============================================================*/
create or replace view CCCapInventory_View as
SELECT YP4.PAOName AS Region, CB.MapLocationId AS OpCenter, YP5.PAOName AS SubstationName, 
       YP3.PAOName AS SubbusName, YP2.PAOName AS FeederName, YP1.PAOName AS CapBankName, 
       CB.BankSize, CAPA.Latitude AS Lat, CAPA.Longitude AS LON, 
       CAPA.DriveDirections AS DriveDirection, CB.OperationalState AS OperationMethod, 
       CB.SwitchManufacture AS SWMfgr, CB.TypeOfSwitch AS SWType, YP.PAOName AS CBCName, 
       DPI.Value AS IPAddress, DA.SlaveAddress, CAPA.MaintenanceAreaId AS TA, 
       CAPA.CapBankConfig, CAPA.CommMedium, CAPA.CommStrength
FROM CapBank CB 
INNER JOIN YukonPAObject YP1 ON YP1.PAObjectId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP ON CB.ControlDeviceId = YP.PAObjectId AND CB.ControlDeviceId > 0 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubStationSubbusList SS ON SS.SubstationBusId = YP3.PAObjectId
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SS.SubStationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SS.SubstationId = SA.SubstationBusId
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime 
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId;

/*==============================================================*/
/* View: CCInventory_View                                       */
/*==============================================================*/
create or replace view CCInventory_View(REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, AREAID, SUBID, SUBBUSID, FDRID, CBCNAME, CBCID, CAPBANKNAME, BANKID, CAPBANKSIZE, DISPLAYORDER, CONTROLSTATUS, CONTROLSTATUSNAME, SWMFGR, SWTYPE, OPERATIONMETHOD, CONTROLLERTYPE, IPADDRESS, SLAVEADDRESS, LAT, LON, DRIVEDIRECTION, OPCENTER, TA, CLOSESEQUENCE, OPENSEQUENCE, LASTOPERATIONTIME, LASTINSPECTIONDATE, LASTMAINTENANCEDATE, MAINTENANCEREQPEND, CAPDISABLED, POTENTIALTRANSFORMER, OTHERCOMMENTS, OPTEAMCOMMENTS, POLENUMBER, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
SELECT YP4.PAOName AS Region, YP5.PAOName AS SubstationName, YP3.PAOName AS SubBusName, 
       YP2.PAOName AS FeederName, YP4.PAObjectId AS AreaId, YP5.PAObjectId AS SubId, 
       YP3.PAObjectId AS SubBusId, YP2.PAObjectId AS FdrId, YP.PAOName AS CBCName, 
       YP.PAObjectId AS CBCId, YP1.PAOName AS CapBankName, YP1.PAObjectId AS BankId, 
       CB.BankSize AS CapBankSize, FB.ControlOrder AS DisplayOrder, DCB.ControlStatus, 
       S.Text AS ControlStatusName, CB.SwitchManufacture AS SWMfgr, CB.TypeOfSwitch AS SWType,
       CB.OperationalState AS OperationMethod, CB.ControllerType, DPI.Value AS IPAddress, 
       DA.SlaveAddress, CAPA.Latitude AS Lat, CAPA.Longitude AS Lon, CAPA.DriveDirections AS DriveDirection, 
       CB.MapLocationId AS OpCenter, CAPA.MaintenanceAreaId AS TA, FB.CloseOrder AS CloseSequence, 
       FB.TripOrder AS OpenSequence, DCB.LastStatusChangeTime AS LastOperationTime, 
       CAPA.LastInspVisit AS LastInspectionDate, CAPA.LastMaintVisit AS LastMaintenanceDate, 
       CAPA.MaintenanceReqPend, YP1.DisableFlag AS CapDisabled, CAPA.PotentialTransformer, 
       CAPA.OtherComments, CAPA.OpTeamComments, CAPA.PoleNumber, 
       DTWC.TotalOpCount AS OpsCounterSinceLastReset, DTWC.TotalOpCount AS OperationsCounterToday, 
       DTWC.UvOpCount AS UvOperationsCounter, DTWC.OvOpCount AS OvOperationsCounter, 
       DTWC.OvUvCountResetDate AS UvOvCounterResetDate, DTWC.LastOvUvDateTime
FROM (SELECT  PAObjectId, PAOName 
      FROM YukonPAObject 
      WHERE (Type LIKE '%CBC%')) YP 
LEFT OUTER JOIN CapBank CB ON YP.PAObjectId = CB.ControlDeviceId 
LEFT OUTER JOIN YukonPAObject YP1 ON YP1.PAObjectId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCCapBank DCB ON DCB.CapBankId = YP1.PAObjectId 
LEFT OUTER JOIN State S ON S.StateGroupId = 3 AND DCB.ControlStatus = S.RawState 
LEFT OUTER JOIN State SL ON SL.StateGroupId = 3 AND DCB.TwoWayCBCState = SL.RawState
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = YP3.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SSL.SubstationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SSL.SubstationId 
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId 
LEFT OUTER JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = YP.PAObjectId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId;

/*==============================================================*/
/* View: CCOperationsASent_View                                 */
/*==============================================================*/
create or replace view CCOperationsASent_View as
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM CCEventLog
WHERE EventType = 1
AND ActionId > -1;

/*==============================================================*/
/* View: CCOperationsBConfirmed_View                            */
/*==============================================================*/
create or replace view CCOperationsBConfirmed_View as
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM CCEventLog
WHERE EventType = 0
AND ActionId > -1;

/*==============================================================*/
/* View: CCOperationsCOrphanedConf_View                         */
/*==============================================================*/
create or replace view CCOperationsCOrphanedConf_View as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL3.NextAId IS NULL
GROUP BY EL.LogId;

/*==============================================================*/
/* View: CCOperationsDSentAndValid_View                         */
/*==============================================================*/
create or replace view CCOperationsDSentAndValid_View as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL2.LogId < EL3.NextAId OR EL3.NextAId IS NULL
GROUP BY EL.LogId;

/*==============================================================*/
/* View: CCOperationsESentAndAll_View                           */
/*==============================================================*/
create or replace view CCOperationsESentAndAll_View as
SELECT OP.LogId AS OId, MIN(aaa.confid) AS CId 
FROM CCOperationsASent_View OP
LEFT JOIN CCOperationsDSentAndValid_view AAA ON OP.LogId = AAA.OpId
GROUP BY OP.LogId;

INSERT INTO CCOperationLogCache
SELECT OpId, ConfId 
FROM CCOperationsDSentAndValid_view
WHERE OpId NOT IN (SELECT OperationLogId 
                   FROM CCOperationLogCache);

/*==============================================================*/
/* View: CCOperations_View                                      */
/*==============================================================*/
create or replace view CCOperations_View as
SELECT YP3.PAObjectId AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectId AS CapBankId, YP.PAOName AS CapBankName, 
       CCOAS.PointId, CCOAS.LogId AS OpLogId, CCOAS.ActionId, CCOAS.DateTime AS OpTime, CCOAS.Text AS Operation, 
       CCOBC.LogId AS ConfLogId, CCOBC.ActionId AS ActionId2, CCOBC.DateTime AS ConfTime, CCOBC.Text AS ConfStatus, 
       YP1.PAOName AS FeederName, YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
       YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, YP4.PAOName AS Region, YP4.PAObjectId AS AreaId,
       CB.BankSize, CB.ControllerType, CCOAS.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       CCOBC.KvarAfter, CCOBC.KvarChange, CCOBC.KvarBefore
FROM CCOperationsASent_View CCOAS
JOIN CCOperationsBConfirmed_view CCOBC ON CCOBC.ActionId = CCOAS.ActionId 
AND CCOBC.PointId = CCOAS.PointId 
AND CCOAS.ActionId >= 0
AND CCOBC.ActionId >= 0
JOIN Point ON Point.PointId = CCOAS.PointId        
JOIN DynamicCCCapBank ON DynamicCCCapBank.CapBankId = Point.PAObjectId        
JOIN YukonPAObject YP ON YP.PAObjectId = DynamicCCCapBank.CapBankId        
JOIN YukonPAObject YP1 ON YP1.PAObjectId = CCOAS.FeederId        
JOIN YukonPAObject YP2 ON YP2.PAObjectId = CCOAS.SubId        
JOIN CapBank CB ON CB.DeviceId = DynamicCCCapBank.CapBankId        
LEFT JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId        
LEFT JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId        
JOIN YukonPAObject YP3 ON YP3.PAObjectId = CB.ControlDeviceId        
LEFT JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId        
LEFT JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime                        
           FROM DynamicPAOInfo                         
           WHERE (InfoKey LIKE '%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId        
LEFT JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = CCOAS.SubId         
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId        
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId        
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId;

/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create or replace view DISPLAY2WAYDATA_VIEW as
SELECT P.POINTID AS PointID, P.POINTNAME AS PointName, P.POINTTYPE AS PointType, 
       P.SERVICEFLAG AS PointState, PAO.PAOName AS DeviceName, PAO.Type AS DeviceType, 
       PAO.Description AS DeviceCurrentState, PAO.PAObjectID AS DeviceID, '**DYNAMIC**' AS PointValue, 
       '**DYNAMIC**' AS PointQuality, '**DYNAMIC**' AS PointTimeStamp, 
       (SELECT uomname 
        FROM PointUnit PU, UnitMeasure UM 
        WHERE PU.pointId = P.pointId 
        AND PU.uomId = UM.uomId) AS UofM, '**DYNAMIC**' AS Tags
FROM YukonPAObject PAO, POINT P
WHERE PAO.PAObjectID = P.PAObjectID;

/*==============================================================*/
/* View: ExpressComAddress_View                                 */
/*==============================================================*/
create or replace view ExpressComAddress_View as
SELECT X.LMGroupId, X.RouteId, X.SerialNumber, S.Address AS ServiceAddress, G.Address AS GeoAddress, 
       B.Address AS SubstationAddress, F.Address AS FeederAddress, Z.Address AS ZipCodeAddress, 
       US.Address AS UDAddress, P.Address AS ProgramAddress, SP.Address AS SplinterAddress, 
       X.AddressUsage, X.RelayUsage, X.ProtocolPriority
FROM LMGroupExpressCom X, LMGroupExpressComAddress S, LMGroupExpressComAddress G, 
     LMGroupExpressComAddress B, LMGroupExpressComAddress F, LMGroupExpressComAddress P,
     LMGroupExpressComAddress SP, LMGroupExpressComAddress US, LMGroupExpressComAddress Z
WHERE (X.ServiceProviderId = S.AddressId AND 
      (S.AddressType = 'SERVICE' OR S.AddressId = 0)) AND 
      (X.FeederId = F.AddressId AND 
      (F.AddressType = 'FEEDER' OR F.AddressId = 0)) AND 
      (X.GeoId = G.AddressId AND 
      (G.AddressType = 'GEO' OR G.AddressId = 0 )) AND 
      (X.ProgramId = P.AddressId AND 
      (P.AddressType = 'PROGRAM' OR P.AddressId = 0)) AND 
      (X.SubstationId = B.AddressId AND 
      (B.AddressType = 'SUBSTATION' OR B.AddressId = 0)) AND 
      (X.SplinterId = SP.AddressId AND 
      (SP.AddressType = 'SPLINTER' OR SP.AddressId = 0)) AND 
      (X.UserId = US.AddressId AND 
      (US.AddressType = 'USER' OR US.AddressId = 0)) AND 
      (X.ZipId = Z.AddressId AND 
      (Z.AddressType = 'ZIP' OR Z.AddressId = 0));

/*==============================================================*/
/* View: FeederAddress_View                                     */
/*==============================================================*/
create or replace view FeederAddress_View as
select x.LMGroupID, a.Address as FeederAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.FeederID = a.AddressID and ( a.AddressType = 'FEEDER' or a.AddressID = 0 ) );

/*==============================================================*/
/* View: FullEventLog_View                                      */
/*==============================================================*/
create or replace view FullEventLog_View(EVENTID, POINTID, EVENTTIMESTAMP, EVENTSEQUENCE, EVENTTYPE, EVENTALARMID, DEVICENAME, POINTNAME, EVENTDESCRIPTION, ADDITIONALINFO, EVENTUSERNAME) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, y.PAOName, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from YukonPAObject y, POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID;

/*==============================================================*/
/* View: FullPointHistory_View                                  */
/*==============================================================*/
create or replace view FullPointHistory_View(POINTID, DEVICENAME, POINTNAME, DATAVALUE, DATATIMESTAMP, DATAQUALITY) as
select r.POINTID, y.PAOName, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from YukonPAObject y, POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID;

/*==============================================================*/
/* View: GeoAddress_View                                        */
/*==============================================================*/
create or replace view GeoAddress_View as
select x.LMGroupID, a.Address as GeoAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.GeoID = a.AddressID and ( a.AddressType = 'GEO' or a.AddressID = 0 ) );

/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create or replace view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID;

/*==============================================================*/
/* View: LMProgram_View                                         */
/*==============================================================*/
create or replace view LMProgram_View as
select t.DeviceID, t.ControlType, u.ConstraintID, u.ConstraintName, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID;

/*==============================================================*/
/* View: Peakpointhistory_View                                  */
/*==============================================================*/
create or replace view Peakpointhistory_View as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where VALUE in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by POINTID, VALUE;

/*==============================================================*/
/* View: PointEventLog_View                                     */
/*==============================================================*/
create or replace view PointEventLog_View(EVENTID, POINTID, EVENTTIMESTAMP, EVENTSEQUENCE, EVENTTYPE, EVENTALARMID, POINTNAME, EVENTDESCRIPTION, ADDITIONALINFO, EVENTUSERNAME) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID;

/*==============================================================*/
/* View: PointHistory_View                                      */
/*==============================================================*/
create or replace view PointHistory_View(POINTID, POINTNAME, DATAVALUE, DATATIMESTAMP, DATAQUALITY) as
select r.POINTID, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID;

/*==============================================================*/
/* View: ProgramAddress_View                                    */
/*==============================================================*/
create or replace view ProgramAddress_View as
select x.LMGroupID, a.Address as ProgramAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ProgramID = a.AddressID and ( a.AddressType = 'PROGRAM' or a.AddressID = 0 ) );

/*==============================================================*/
/* View: ServiceAddress_View                                    */
/*==============================================================*/
create or replace view ServiceAddress_View as
select x.LMGroupID, a.Address as ServiceAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ServiceProviderID = a.AddressID and ( a.AddressType = 'SERVICE' or a.AddressID = 0 ) );

/*==============================================================*/
/* View: SubstationAddress_View                                 */
/*==============================================================*/
create or replace view SubstationAddress_View as
select x.LMGroupID, a.Address as SubstationAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.SubstationID = a.AddressID and ( a.AddressType = 'SUBSTATION' or a.AddressID = 0 ) );

/*==============================================================*/
/* View: TempMovedCapBanks_View                                 */
/*==============================================================*/
create or replace view TempMovedCapBanks_View as
SELECT YPF.PAOName TempFeederName, YPF.PAObjectId TempFeederId, YPC.PAOName CapBankName, 
       YPC.PAObjectId CapBankId, FB.ControlOrder, FB.CloseOrder, FB.TripOrder, 
       YPOF.PAOName OriginalFeederName, YPOF.PAObjectId OriginalFeederId 
FROM CCFeederBankList FB, YukonPAObject YPF, YukonPAObject YPC, 
     YukonPAObject YPOF, DynamicCCOriginalParent DCCOP
WHERE FB.DeviceId = YPC.PAObjectId
AND YPC.PAObjectID = DCCOP.PAObjectId
AND FB.FeederId = YPF.PAObjectId 
AND YPOF.PAObjectId = DCCOP.OriginalParentId 
AND DCCOP.OriginalParentId <> 0;

alter table AccountSite
   add constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
      references SiteInformation (SiteID);

alter table AccountSite
   add constraint FK_AccS_CstAd foreign key (StreetAddressID)
      references Address (AddressID);

alter table AcctThermostatSchedule
   add constraint FK_AcctThermSch_CustAcct foreign key (AccountId)
      references CustomerAccount (AccountID);

alter table AcctThermostatScheduleEntry
   add constraint FK_AccThermSchEnt_AccThermSch foreign key (AcctThermostatScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId);

alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac_ty foreign key (TypeID)
      references YukonListEntry (EntryID);

alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac foreign key (TonnageID)
      references YukonListEntry (EntryID);

alter table ApplianceAirConditioner
   add constraint FK_APPLIANC_ISA_CSTLD_APPLIANC foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceBase
   add constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table ApplianceBase
   add constraint FK_APPLIANC_CSTLDTY_C_APPLIANC foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID);

alter table ApplianceBase
   add constraint FK_AppBs_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID);

alter table ApplianceBase
   add constraint FK_CsLsEn_ApB foreign key (ManufacturerID)
      references YukonListEntry (EntryID);

alter table ApplianceBase
   add constraint FK_CsLsEn_ApB2 foreign key (LocationID)
      references YukonListEntry (EntryID);

alter table ApplianceCategory
   add constraint FK_CstLs_ApCt foreign key (CategoryID)
      references YukonListEntry (EntryID);

alter table ApplianceCategory
   add constraint FK_YkWC_ApCt foreign key (WebConfigurationID)
      references YukonWebConfiguration (ConfigurationID);

alter table ApplianceChiller
   add constraint FK_APPLCHILL_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TONNTRY foreign key (TonnageID)
      references YukonListEntry (EntryID);

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID);

alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst1 foreign key (SecondaryEnergySourceID)
      references YukonListEntry (EntryID);

alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst2 foreign key (SwitchOverTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceDualFuel
   add constraint FK_AppDlF_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STGTWONTRY foreign key (StageTwoTonnageID)
      references YukonListEntry (EntryID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STNENTRY foreign key (StageOneTonnageID)
      references YukonListEntry (EntryID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID);

alter table ApplianceGenerator
   add constraint FK_AppGn_YkLst1 foreign key (TransferSwitchTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceGenerator
   add constraint FK_AppGn_YkLst2 foreign key (TransferSwitchMfgID)
      references YukonListEntry (EntryID);

alter table ApplianceGenerator
   add constraint FK_AppGen_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst1 foreign key (BinSizeID)
      references YukonListEntry (EntryID);

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst2 foreign key (BlowerHorsePowerID)
      references YukonListEntry (EntryID);

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst3 foreign key (BlowerEnergySourceID)
      references YukonListEntry (EntryID);

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst5 foreign key (DryerTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst6 foreign key (BlowerHeatSourceID)
      references YukonListEntry (EntryID);

alter table ApplianceGrainDryer
   add constraint FK_AppGrD_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst1 foreign key (StandbySourceID)
      references YukonListEntry (EntryID);

alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst2 foreign key (PumpTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceHeatPump
   add constraint FK_AppHtP_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst3 foreign key (PumpSizeID)
      references YukonListEntry (EntryID);

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst1 foreign key (MeterVoltageID)
      references YukonListEntry (EntryID);

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst2 foreign key (MeterLocationID)
      references YukonListEntry (EntryID);

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst3 foreign key (IrrigationTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst6 foreign key (HorsePowerID)
      references YukonListEntry (EntryID);

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst5 foreign key (SoilTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceIrrigation
   add constraint FK_AppIrr_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst4 foreign key (EnergySourceID)
      references YukonListEntry (EntryID);

alter table ApplianceStorageHeat
   add constraint FK_AppStHt_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ApplianceStorageHeat
   add constraint FK_AppStHt_YkLst foreign key (StorageTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceWaterHeater
   add constraint FK_AppWtHt_YkLst foreign key (NumberOfGallonsID)
      references YukonListEntry (EntryID);

alter table ApplianceWaterHeater
   add constraint FK_ApWtrHt_YkLsE foreign key (EnergySourceID)
      references YukonListEntry (EntryID);

alter table ApplianceWaterHeater
   add constraint FK_AppWtHt_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table ArchiveDataAnalysisSlot
   add constraint FK_ArcDataAnalSlots_ArcDataAna foreign key (AnalysisId)
      references ArchiveDataAnalysis (AnalysisId)
      on delete cascade;

alter table ArchiveDataAnalysisSlotValue
   add constraint FK_ArcDataAnalSlotVal_ArcData foreign key (SlotId)
      references ArchiveDataAnalysisSlot (SlotId)
      on delete cascade;

alter table ArchiveDataAnalysisSlotValue
   add constraint FK_ArchDataAnalSlotVal_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table BaseLine
   add constraint FK_BASELINE_HOLIDAYS foreign key (HolidayScheduleId)
      references HolidaySchedule (HolidayScheduleID);

alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID);

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID);

alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID);

alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID);

alter table CAPBANKADDITIONAL
   add constraint FK_CAPBANKA_CAPBANK foreign key (DeviceID)
      references CAPBANK (DEVICEID);

alter table CAPCONTROLAREA
   add constraint FK_CAPCONTR_REFERENCE_YUKONPAO foreign key (AreaID)
      references YukonPAObject (PAObjectID);

alter table CAPCONTROLCOMMENT
   add constraint FK_CapContCom_PAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table CAPCONTROLCOMMENT
   add constraint FK_CapContCom_YukonUser foreign key (UserID)
      references YukonUser (UserID)
      on delete set null;

alter table CAPCONTROLSPECIALAREA
   add constraint FK_CAPCONTR_YUKONPAO2 foreign key (AreaID)
      references YukonPAObject (PAObjectID);

alter table CAPCONTROLSUBSTATION
   add constraint FK_CAPCONTR_REF_YUKONPA2 foreign key (SubstationID)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_SWPTID foreign key (SwitchPointID)
      references POINT (POINTID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_CVOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID);

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID);

alter table CCEventLog
   add constraint FK_CCEventLog_Point foreign key (PointID)
      references POINT (POINTID)
      on delete cascade;

alter table CCFeederBankList
   add constraint FK_CCFeederBankList_CCFeeder foreign key (FeederID)
      references CapControlFeeder (FeederID)
      on delete cascade;

alter table CCFeederBankList
   add constraint FK_CCFeederBankList_CapBank foreign key (DeviceID)
      references CAPBANK (DEVICEID)
      on delete cascade;

alter table CCFeederSubAssignment
   add constraint FK_CCFeederSubAssign_CCFeeder foreign key (FeederID)
      references CapControlFeeder (FeederID)
      on delete cascade;

alter table CCFeederSubAssignment
   add constraint FK_CCFeederSubAssign_CCSubBus foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
      on delete cascade;

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHSA_PAOID foreign key (PAObjectId)
      references YukonPAObject (PAObjectID);

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHSA_SCHEDID foreign key (HolidayScheduleId)
      references HolidaySchedule (HolidayScheduleID);

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHOLIDAY_CAPCONTR foreign key (StrategyId)
      references CapControlStrategy (StrategyID);

alter table CCMonitorBankList
   add constraint FK_CCMonBankList_CapBank foreign key (BankId)
      references CAPBANK (DEVICEID)
      on delete cascade;

alter table CCMonitorBankList
   add constraint FK_CCMonBankList_Point foreign key (PointId)
      references POINT (POINTID)
      on delete cascade;

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_PAOID foreign key (paobjectid)
      references YukonPAObject (PAObjectID);

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_SCHEDID foreign key (seasonscheduleid)
      references SeasonSchedule (ScheduleID);

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_ccssa_season foreign key (seasonscheduleid, seasonname)
      references DateOfSeason (SeasonScheduleID, SeasonName);

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSEASON_REFERENCE_CAPCONTR foreign key (strategyid)
      references CapControlStrategy (StrategyID);

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSubAreaAssign_CCSubst foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID)
      on delete cascade;

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSubAreaAssignment_CCArea foreign key (AreaID)
      references CAPCONTROLAREA (AreaID)
      on delete cascade;

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_CAPCONTR2 foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID);

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID);

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSubstSubBusList_CCSub foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID)
      on delete cascade;

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSubstSubBusList_CCSubBus foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
      on delete cascade;

alter table CCStrategyTargetSettings
   add constraint FK_CCStratTarSet_CapContStrat foreign key (StrategyId)
      references CapControlStrategy (StrategyID)
      on delete cascade;

alter table CCURTACCTEVENT
   add constraint FK_CCURTACC_CCURTPRO foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID);

alter table CCURTACCTEVENTPARTICIPANT
   add constraint FK_CCURTACCTEVENTID foreign key (CCurtAcctEventID)
      references CCURTACCTEVENT (CCurtAcctEventID);

alter table CCURTACCTEVENTPARTICIPANT
   add constraint FK_CCURTACC_CICUSTOM foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table CCurtCENotif
   add constraint FK_CCCURTCE_NOTIF_PART foreign key (CCurtCEParticipantID)
      references CCurtCEParticipant (CCurtCEParticipantID);

alter table CCurtCEParticipant
   add constraint FK_CCURTCE_PART_CURTEVT foreign key (CCurtCurtailmentEventID)
      references CCurtCurtailmentEvent (CCurtCurtailmentEventID);

alter table CCurtCEParticipant
   add constraint FK_CCURTCURTEVENTCICUST_CICUST foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table CCurtCurtailmentEvent
   add constraint FK_CCURTCURTEVT_CCURTPGM foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID);

alter table CCurtEEParticipant
   add constraint FK_CCURTEEPART_CCURTEE foreign key (CCurtEconomicEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID);

alter table CCurtEEParticipant
   add constraint FK_CCURTEEPART_CUST foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table CCurtEEParticipantSelection
   add constraint FK_CCURTEEPARTSEL_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID);

alter table CCurtEEParticipantSelection
   add constraint FK_CCURTEEPARTSEL_CCURTPART foreign key (CCurtEEParticipantID)
      references CCurtEEParticipant (CCurtEEParticipantID)
      on delete cascade;

alter table CCurtEEParticipantWindow
   add constraint FK_CCRTEEPRTWIN_CCRTEEPRTSEL foreign key (CCurtEEParticipantSelectionID)
      references CCurtEEParticipantSelection (CCurtEEParticipantSelectionID)
      on delete cascade;

alter table CCurtEEParticipantWindow
   add constraint FK_CCRTEEPRTWN_CCRTEEPRIWN foreign key (CCurtEEPricingWindowID)
      references CCurtEEPricingWindow (CCurtEEPricingWindowID);

alter table CCurtEEPricing
   add constraint FK_CCURTEEPR_CCURTECONEVT foreign key (CCurtEconomicEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID)
      on delete cascade;

alter table CCurtEEPricingWindow
   add constraint FK_CCURTEEPRWIN_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID)
      on delete cascade;

alter table CCurtEconomicEvent
   add constraint FK_CCURTEEVT_CCURTPGM foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID);

alter table CCurtEconomicEvent
   add constraint FK_CCURTINITEVT_CCURTECONEVT foreign key (InitialEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID);

alter table CCurtEconomicEventNotif
   add constraint FK_CCURTEENOTIF_CCURTEEPARTID foreign key (CCurtEconomicParticipantID)
      references CCurtEEParticipant (CCurtEEParticipantID);

alter table CCurtEconomicEventNotif
   add constraint FK_CCURTEENOTIF_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID);

alter table CCurtGroupCustomerNotif
   add constraint FK_CCURTGRO_FK_CCURTG_CCURTGRO foreign key (CCurtGroupID)
      references CCurtGroup (CCurtGroupID);

alter table CCurtGroupCustomerNotif
   add constraint FK_CCURTGRPCUSTNOTIF_CUST foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table CCurtProgram
   add constraint FK_CCURTPRG_CCURTPRGTYPE foreign key (CCurtProgramTypeID)
      references CCurtProgramType (CCurtProgramTypeID);

alter table CCurtProgramGroup
   add constraint FK_CCURTPRGGRP_CCURTGRP foreign key (CCurtGroupID)
      references CCurtGroup (CCurtGroupID);

alter table CCurtProgramGroup
   add constraint FK_CCURTPRGGRP_CCURTPRG foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID);

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_CCURTP foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID);

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_NG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table CCurtProgramParameter
   add constraint FK_CCURTPRGPARAM_CCURTPRGID foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID);

alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID);

alter table CICustomerBase
   add constraint FK_CUSTTYPE_ENTRYID foreign key (CiCustType)
      references YukonListEntry (EntryID);

alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID);

alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID);

alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID);

alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);

alter table CallReportBase
   add constraint FK_CstAc_ClRpB foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table CallReportBase
   add constraint FK_CstELs_ClRB foreign key (CallTypeID)
      references YukonListEntry (EntryID);

alter table CapBankToZoneMapping
   add constraint FK_CapBankZoneMap_CapBank foreign key (DeviceId)
      references CAPBANK (DEVICEID)
      on delete cascade;

alter table CapBankToZoneMapping
   add constraint FK_CapBankZoneMap_Zone foreign key (ZoneId)
      references Zone (ZoneId)
      on delete cascade;

alter table CapControlFeeder
   add constraint FK_CAPCONTR_VARPTID foreign key (CurrentVarLoadPointID)
      references POINT (POINTID);

alter table CapControlFeeder
   add constraint FK_CAPCONTR_VOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID);

alter table CapControlFeeder
   add constraint FK_CAPCONTR_WATTPTID foreign key (CurrentWattLoadPointID)
      references POINT (POINTID);

alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID);

alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID);

alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID);

alter table CommandRequestExecResult
   add constraint FK_ComReqExecResult_ComReqExec foreign key (CommandRequestExecId)
      references CommandRequestExec (CommandRequestExecId)
      on delete cascade;

alter table CommandSchedule
   add constraint FK_CommSche_EnergyComp foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
      on delete cascade;

alter table Contact
   add constraint FK_CONTACT_REF_CNT_A_ADDRESS foreign key (AddressID)
      references Address (AddressID);

alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID);

alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID);

alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID);

alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID);

alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);

alter table Customer
   add constraint FK_Cust_YkLs foreign key (RateScheduleID)
      references YukonListEntry (EntryID);

alter table CustomerAccount
   add constraint FK_CUS_CSTA_CUS2 foreign key (AccountSiteID)
      references AccountSite (AccountSiteID);

alter table CustomerAccount
   add constraint FK_CstBs_CstAcc foreign key (CustomerID)
      references Customer (CustomerID);

alter table CustomerAccount
   add constraint FK_CustAcc_Add foreign key (BillingAddressID)
      references Address (AddressID);

alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID);

alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID);

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID);

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID);

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);

alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID);

alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table CustomerResidence
   add constraint FK_CstRes_AccSt foreign key (AccountSiteID)
      references AccountSite (AccountSiteID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst1 foreign key (ConstructionMaterialID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst10 foreign key (GeneralConditionID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst11 foreign key (SquareFeetID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst2 foreign key (NumberOfOccupantsID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst3 foreign key (InsulationDepthID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst4 foreign key (MainCoolingSystemID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CUSTOMER_REF_CSTRE_YUKONLIS foreign key (MainFuelTypeID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst6 foreign key (DecadeBuiltID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst7 foreign key (OwnershipTypeID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst8 foreign key (ResidenceTypeID)
      references YukonListEntry (EntryID);

alter table CustomerResidence
   add constraint FK_CstRes_YkLst9 foreign key (MainHeatingSystemID)
      references YukonListEntry (EntryID);

alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);

alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_DEVICECO foreign key (DeviceConfigurationId)
      references DEVICECONFIGURATION (DeviceConfigurationID)
      on delete cascade;

alter table DEVICECONFIGURATIONDEVICEMAP
   add constraint FK_DEVICECO_REFERENCE_YUKONPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table DEVICECONFIGURATIONITEM
   add constraint FK_DevConfItem_DevConf foreign key (DeviceConfigurationId)
      references DEVICECONFIGURATION (DeviceConfigurationID)
      on delete cascade;

alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEGROUP
   add constraint FK_DEVICEGROUP_DEVICEGROUP foreign key (ParentDeviceGroupId)
      references DEVICEGROUP (DeviceGroupId);

alter table DEVICEGROUPMEMBER
   add constraint FK_DeviceGroupMember_DEVICE foreign key (YukonPaoId)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table DEVICEGROUPMEMBER
   add constraint FK_DevGrpMember_DeviceGroup foreign key (DeviceGroupID)
      references DEVICEGROUP (DeviceGroupId)
      on delete cascade;

alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICEREADLOG
   add constraint FK_DEVICERE_FK_DRLOGR_DEVICERE foreign key (DeviceReadRequestLogID)
      references DEVICEREADREQUESTLOG (DeviceReadRequestLogID);

alter table DEVICEREADREQUESTLOG
   add constraint FK_DEVICERE_FK_DRREQL_DEVICERE foreign key (DeviceReadJobLogID)
      references DEVICEREADJOBLOG (DeviceReadJobLogID);

alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID);

alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);

alter table DYNAMICACCUMULATOR
   add constraint SYS_C0015129 foreign key (POINTID)
      references POINT (POINTID);

alter table DYNAMICBILLINGFIELD
   add constraint FK_DBF_REF_BFF foreign key (FormatID)
      references BillingFileFormats (FormatID)
      on delete cascade;

alter table DYNAMICBILLINGFORMAT
   add constraint FK_DYNAMICB_REF_BILLI_BILLINGF foreign key (FormatID)
      references BillingFileFormats (FormatID);

alter table DYNAMICCCAREA
   add constraint FK_DynCCArea_CCArea foreign key (AreaID)
      references CAPCONTROLAREA (AreaID)
      on delete cascade;

alter table DYNAMICCCOPERATIONSTATISTICS
   add constraint FK_DynCCOpStats_YukonPAObject foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table DYNAMICCCSPECIALAREA
   add constraint FK_DynCCSpecial_CCSpecialArea foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID)
      on delete cascade;

alter table DYNAMICCCSUBSTATION
   add constraint FK_DynCCSub_CCSub foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID)
      on delete cascade;

alter table DYNAMICCCTWOWAYCBC
   add constraint FK_DYNAMICC_DEVICECB foreign key (DeviceID)
      references DeviceCBC (DEVICEID);

alter table DYNAMICDEVICESCANDATA
   add constraint FK_DynDeviceScanData_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID);

alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);

alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);

alter table DeliverySchedule
   add constraint FK_DS_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID);

alter table DeliverySchedule
   add constraint FK_DS_REF_YKNLSTNTRY foreign key (ModelID)
      references YukonListEntry (EntryID);

alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID);

alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID);

alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID);

alter table DeviceGroupComposed
   add constraint FK_DevGroupComp_DevGroup foreign key (DeviceGroupId)
      references DEVICEGROUP (DeviceGroupId)
      on delete cascade;

alter table DeviceGroupComposedGroup
   add constraint FK_DevGrpCompGrp_DevGrpComp foreign key (DeviceGroupComposedId)
      references DeviceGroupComposed (DeviceGroupComposedId)
      on delete cascade;

alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);

alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);

alter table DevicePagingReceiverSettings
   add constraint FK_DevPaRec_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID);

alter table DeviceSeries5RTU
   add constraint FK_DeviceSer5RTU_Device foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceTNPPSettings
   add constraint FK_DevTNPP_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Cmd foreign key (CommandID)
      references Command (CommandID);

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);

alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);

alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);

alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table DigiGateway
   add constraint FK_DigiGate_ZBGate foreign key (DeviceId)
      references ZBGateway (DeviceId)
      on delete cascade;

alter table DynamicCCCapBank
   add constraint FK_DynCCCapBank_CapBank foreign key (CapBankID)
      references CAPBANK (DEVICEID)
      on delete cascade;

alter table DynamicCCFeeder
   add constraint FK_DynCCFeeder_CCFeeder foreign key (FeederID)
      references CapControlFeeder (FeederID)
      on delete cascade;

alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_BNKID foreign key (BankID)
      references CAPBANK (DEVICEID);

alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_PTID foreign key (PointID)
      references POINT (POINTID);

alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_BNKID foreign key (BankID)
      references DynamicCCCapBank (CapBankID);

alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_PTID foreign key (PointID)
      references POINT (POINTID);

alter table DynamicCCOriginalParent
   add constraint FK_DynCCOrigParent_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table DynamicCCSubstationBus
   add constraint FK_DynCCSubBus_CCSubBus foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
      on delete cascade;

alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID);

alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID);

alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMControlAreaTrigger (DeviceId, TriggerNumber);

alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID);

alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DeviceID);

alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID);

alter table DynamicPAOInfo
   add constraint FK_DynPAOInfo_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table DynamicPAOStatistics
   add constraint FK_DynPAOStat_PAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID);

alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID);

alter table DynamicTags
   add constraint FK_DYNAMICT_REF_DYNTG_TAGS foreign key (TagID)
      references Tags (TagID);

alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);

alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);

alter table ECToAccountMapping
   add constraint FK_ECTAcc_CstAcc foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table ECToAccountMapping
   add constraint FK_ECTAcc_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table ECToAcctThermostatSchedule
   add constraint FK_ECToAccThermSch_AccThermSch foreign key (AcctThermostatScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId);

alter table ECToAcctThermostatSchedule
   add constraint FK_ECToAccThermSch_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID);

alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Call foreign key (CallReportID)
      references CallReportBase (CallID);

alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table ECToGenericMapping
   add constraint FK_ECTGn_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc2 foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table ECToLMCustomerEventMapping
   add constraint FK_EnCm_ECLmCs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table ECToLMCustomerEventMapping
   add constraint FK_LCsEv_ECLmCs foreign key (EventID)
      references LMCustomerEventBase (EventID);

alter table ECToOperatorGroupMapping
   add constraint FK_ECToOpGroupMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
      on delete cascade;

alter table ECToOperatorGroupMapping
   add constraint FK_ECToOpGroupMap_YukonGroup foreign key (GroupId)
      references YukonGroup (GroupID)
      on delete cascade;

alter table ECToResidentialGroupMapping
   add constraint FK_ECToResGroupMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
      on delete cascade;

alter table ECToResidentialGroupMapping
   add constraint FK_ECToResGroupMap_YukonGroup foreign key (GroupId)
      references YukonGroup (GroupID)
      on delete cascade;

alter table ECToRouteMapping
   add constraint FK_ECToRouteMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
      on delete cascade;

alter table ECToRouteMapping
   add constraint FK_ECToRouteMap_Route foreign key (RouteId)
      references Route (RouteID)
      on delete cascade;

alter table ECToSubstationMapping
   add constraint FK_ECToSubMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
      on delete cascade;

alter table ECToSubstationMapping
   add constraint FK_ECToSubMap_Sub foreign key (SubstationId)
      references Substation (SubstationID)
      on delete cascade;

alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
      references WorkOrderBase (OrderID);

alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);

alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID);

alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);

alter table EventAccount
   add constraint FK_EVENTACCT_CUSTACCT foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table EventAccount
   add constraint FK_EVENTACCT_EVNTBSE foreign key (EVENTID)
      references EventBase (EventID);

alter table EventBase
   add constraint FK_EVNTBSE_ACTNTRY foreign key (ActionID)
      references YukonListEntry (EntryID);

alter table EventBase
   add constraint FK_EVNTBSE_SYSCATNTRY foreign key (SystemCategoryID)
      references YukonListEntry (EntryID);

alter table EventBase
   add constraint FK_EVNTBSE_YUKUSR foreign key (UserID)
      references YukonUser (UserID);

alter table EventInventory
   add constraint FK_EVENTINV_EVNTBSE foreign key (EventID)
      references EventBase (EventID);

alter table EventInventory
   add constraint FK_EVENTINV_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table EventWorkOrder
   add constraint FK_EVENTWO_EVNTBSE foreign key (EventID)
      references EventBase (EventID);

alter table EventWorkOrder
   add constraint FK_EVENTWO_WOBASE foreign key (OrderID)
      references WorkOrderBase (OrderID);

alter table ExtraPaoPointAssignment
   add constraint FK_ExtraPAOPointAsgmt_Point foreign key (PointId)
      references POINT (POINTID)
      on delete cascade;

alter table ExtraPaoPointAssignment
   add constraint FK_ExtraPAOPointAsgmt_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceId);

alter table FDRTranslation
   add constraint SYS_C0015066 foreign key (PointId)
      references POINT (POINTID);

alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);

alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID);

alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);

alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID);

alter table GroupPaoPermission
   add constraint FK_GROUPPAO_REF_YKGRP_YUKONGRO foreign key (GroupID)
      references YukonGroup (GroupID);

alter table GroupPaoPermission
   add constraint FK_GROUPPAO_REF_YUKPA_YUKONPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

alter table ImportPendingComm
   add constraint FK_ImpPC_PAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);

alter table InventoryBase
   add constraint FK_CUS_CSTA_CUS3 foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table InventoryBase
   add constraint FK_CUS_HRDI_HAR2 foreign key (InstallationCompanyID)
      references ServiceCompany (CompanyID);

alter table InventoryBase
   add constraint FK_INVENTOR_REF_CSTLS_YUKONLIS foreign key (CategoryID)
      references YukonListEntry (EntryID);

alter table InventoryBase
   add constraint FK_Dev_InvB foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table InventoryBase
   add constraint FK_InvB_YkLstEvlt foreign key (VoltageID)
      references YukonListEntry (EntryID);

alter table InventoryConfigTask
   add constraint FK_InvConfTask_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
      on delete cascade;

alter table InventoryConfigTask
   add constraint FK_InvConfTask_YukonUser foreign key (UserId)
      references YukonUser (UserID)
      on delete cascade;

alter table InventoryConfigTaskItem
   add constraint FK_InvConfTaskItem_InvBase foreign key (InventoryId)
      references InventoryBase (InventoryID)
      on delete cascade;

alter table InventoryConfigTaskItem
   add constraint FK_InvConfTaskItem_InvConfTask foreign key (InventoryConfigTaskId)
      references InventoryConfigTask (InventoryConfigTaskId)
      on delete cascade;

alter table InventoryToAcctThermostatSch
   add constraint FK_InvToAccThermSch_AccThermSc foreign key (AcctThermostatScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId);

alter table InventoryToAcctThermostatSch
   add constraint FK_InvToAcctThermSch_InvBase foreign key (InventoryId)
      references InventoryBase (InventoryID);

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_INVENBASE foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_WAREHOUSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);

alter table Invoice
   add constraint FK_INVC_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID);

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_INVC foreign key (InvoiceID)
      references Invoice (InvoiceID);

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);

alter table JOB
   add constraint FK_Job_YukonUser foreign key (UserID)
      references YukonUser (UserID);

alter table JOBPROPERTY
   add constraint FK_JobProperty_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade;

alter table JOBSCHEDULEDONETIME
   add constraint FK_JobScheduledOneTime_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade;

alter table JOBSCHEDULEDREPEATING
   add constraint FK_JOBSCHED_REFERENCE_JOB foreign key (JobID)
      references JOB (JobID)
      on delete cascade;

alter table JOBSTATUS
   add constraint FK_JobStatus_Job foreign key (JobID)
      references JOB (JobID)
      on delete cascade;

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID);

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DeviceID);

alter table LMConfigurationExpressCom
   add constraint FK_LMCfgXcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMConfigurationSA205
   add constraint FK_LmCf2_LmCBs foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMConfigurationSA305
   add constraint FK_LMCfg305_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMConfigurationSASimple
   add constraint FK_LMCfgS_LMCfgB foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMConfigurationVersacom
   add constraint FK_LMCfgVcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);

alter table LMControlAreaTrigger
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DeviceId)
      references LMControlArea (DEVICEID);

alter table LMControlAreaTrigger
   add constraint FK_Point_LMCntlArTrig foreign key (PointId)
      references POINT (POINTID);

alter table LMControlAreaTrigger
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PeakPointId)
      references POINT (POINTID);

alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID);

alter table LMControlScenarioProgram
   add constraint FK_LMCONTRO_REF_LMSCP_LMPROGRA foreign key (ProgramID)
      references LMPROGRAM (DeviceID);

alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID);

alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID);

alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE foreign key (EventTypeID)
      references YukonListEntry (EntryID);

alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE_a foreign key (ActionID)
      references YukonListEntry (EntryID);

alter table LMDirectCustomerList
   add constraint FK_LMDirCustList_CICustBase foreign key (CustomerID)
      references CICustomerBase (CustomerID)
      on delete cascade;

alter table LMDirectCustomerList
   add constraint FK_LMDirCustList_LMProgDir foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
      on delete cascade;

alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID);

alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID);

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID);

alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMEnergyExchangeCustomerReply
   add constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
      on delete cascade;

alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
      on delete cascade;

alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber);

alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID);

alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID);

alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID);

alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID);

alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID);

alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);

alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);

alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID);

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID);

alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID);

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID);

alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID);

alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID);

alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID);

alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID);

alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMGroupSep
   add constraint FK_LMGroupSep_LMGroup foreign key (DeviceId)
      references LMGroup (DeviceID)
      on delete cascade;

alter table LMGroupSepDeviceClass
   add constraint FK_LMGroupSepDevClass_LMGrpSep foreign key (DeviceId)
      references LMGroupSep (DeviceId)
      on delete cascade;

alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID);

alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID);

alter table LMHardwareBase
   add constraint FK_LMHrdB_Rt foreign key (RouteID)
      references Route (RouteID);

alter table LMHardwareBase
   add constraint FK_LMHARDWA_REF_CSTLS_YUKONLIS foreign key (LMHardwareTypeID)
      references YukonListEntry (EntryID);

alter table LMHardwareBase
   add constraint FK_LMHrdB_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMHardwareBase
   add constraint FK_LMHARDWA_ISA_CSTHR_INVENTOR foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table LMHardwareConfiguration
   add constraint FK_LMH_CSTL_CUS2 foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

alter table LMHardwareConfiguration
   add constraint FK_LMHARDWA_LMHRD_LMH_LMHARDWA foreign key (InventoryID)
      references LMHardwareBase (InventoryID);

alter table LMHardwareConfiguration
   add constraint FK_LMHrd_LMGr foreign key (AddressingGroupID)
      references LMGroup (DeviceID);

alter table LMHardwareEvent
   add constraint FK_IvB_LMHrEv foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table LMHardwareEvent
   add constraint FK_LmHrEv_LmCsEv foreign key (EventID)
      references LMCustomerEventBase (EventID);

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_LMHARDBASE foreign key (LMHardwareInventoryID)
      references LMHardwareBase (InventoryID);

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_METERHARDBASE foreign key (MeterInventoryID)
      references MeterHardwareBase (InventoryID);

alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID);

alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID);

alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID);

alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);

alter table LMProgramConstraints
   add constraint FK_HlSc_LmPrC foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);

alter table LMProgramConstraints
   add constraint FK_SesSch_LmPrC foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);

alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DeviceID);

alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID);

alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID)
      on delete cascade;

alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DeviceID);

alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DeviceID);

alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID);

alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID);

alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID);

alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DeviceID);

alter table LMProgramEvent
   add constraint FK_CstAc_LMPrEv foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table LMProgramEvent
   add constraint FK_LMPrEv_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID);

alter table LMProgramEvent
   add constraint FK_LmCsEv_LmPrEv foreign key (EventID)
      references LMCustomerEventBase (EventID);

alter table LMProgramGearHistory
   add constraint FK_LMProgGearHist_LMProgHist foreign key (LMProgramHistoryId)
      references LMProgramHistory (LMProgramHistoryId);

alter table LMProgramWebPublishing
   add constraint FK_CsLEn_LPWbP foreign key (ChanceOfControlID)
      references YukonListEntry (EntryID);

alter table LMProgramWebPublishing
   add constraint FK_LMPrgW_LMPr foreign key (DeviceID)
      references LMPROGRAM (DeviceID);

alter table LMProgramWebPublishing
   add constraint FK_LMprApp_App foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID);

alter table LMProgramWebPublishing
   add constraint FK_YkWC_LMPrWPb foreign key (WebsettingsID)
      references YukonWebConfiguration (ConfigurationID);

alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID);

alter table LMThermostatManualEvent
   add constraint FK_CsLsE_LThMnO2 foreign key (FanOperationID)
      references YukonListEntry (EntryID);

alter table LMThermostatManualEvent
   add constraint FK_CsLsE_LThMnO1 foreign key (OperationStateID)
      references YukonListEntry (EntryID);

alter table LMThermostatManualEvent
   add constraint FK_LMTh_InvB foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table LMThermostatManualEvent
   add constraint FK_LmThrS_LmCstEv foreign key (EventID)
      references LMCustomerEventBase (EventID);

alter table LMThermostatSeason
   add constraint FK_LMThermSea_LMThermSch foreign key (ScheduleID)
      references LMThermostatSchedule (ScheduleID)
      on delete cascade;

alter table LMThermostatSeasonEntry
   add constraint FK_LMThermSeaEntry_LMThermSea foreign key (SeasonID)
      references LMThermostatSeason (SeasonID)
      on delete cascade;

alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID);

alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID);

alter table MACSchedule
   add constraint FK_MACSCHED_HOLIDAYS foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);

alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID);

alter table MACSimpleSchedule
   add constraint FK_MACSimpSch_PAO foreign key (TargetPAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID);

alter table MCTBroadCastMapping
   add constraint FK_MCTBCM_Device_MCTId foreign key (MctID)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID);

alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID);

alter table MSPInterface
   add constraint FK_Intrfc_Vend foreign key (VendorID)
      references MSPVendor (VendorID);

alter table MeterHardwareBase
   add constraint FK_METERHARD_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table MeterHardwareBase
   add constraint FK_METERHARD_YUKONLSTNTRY foreign key (MeterTypeID)
      references YukonListEntry (EntryID);

alter table MspLMInterfaceMapping
   add constraint FK_MspLMInterMap_YukonPAObj foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID);

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID);

alter table OptOutEvent
   add constraint FK_OptOutEvent_CustAcct foreign key (CustomerAccountId)
      references CustomerAccount (AccountID)
      on delete set null;

alter table OptOutSurvey
   add constraint FK_OptOutSurv_Surv foreign key (SurveyId)
      references Survey (SurveyId)
      on delete cascade;

alter table OptOutSurveyProgram
   add constraint FK_OptOutSurvProg_LMProg foreign key (DeviceId)
      references LMPROGRAM (DeviceID)
      on delete cascade;

alter table OptOutSurveyProgram
   add constraint FK_OptOutSurvProg_OptOutSurv foreign key (OptOutSurveyId)
      references OptOutSurvey (OptOutSurveyId)
      on delete cascade;

alter table OptOutSurveyResult
   add constraint FK_OptOutSurvRes_OptOutEventLo foreign key (OptOutEventLogId)
      references OptOutEventLog (OptOutEventLogId)
      on delete cascade;

alter table OptOutSurveyResult
   add constraint FK_OptOutSurvRes_SurvRes foreign key (SurveyResultId)
      references SurveyResult (SurveyResultId)
      on delete cascade;

alter table OptOutTemporaryOverride
   add constraint FK_OptOutTempOver_LMProgWebPub foreign key (ProgramId)
      references LMProgramWebPublishing (ProgramID)
      on delete cascade;

alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID);

alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

alter table PAOExclusion
   add constraint FK_PAOEXCLU_REF_PAOEX_YUKONPAO foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID);

alter table PAOFavorites
   add constraint FK_PAOFav_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table PAOFavorites
   add constraint FK_PAOFav_YukonUser foreign key (UserId)
      references YukonUser (UserID)
      on delete cascade;

alter table PAOProperty
   add constraint FK_PAOProp_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table PAORecentViews
   add constraint FK_PAORecentViews_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table PAOScheduleAssignment
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOSchedule (ScheduleID);

alter table PAOScheduleAssignment
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID);

alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID);

alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);

alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references StateGroup (StateGroupId);

alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID);

alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID);

alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID);

alter table POINTPROPERTYVALUE
   add constraint FK_POINTPRO_REFERENCE_POINT foreign key (PointID)
      references POINT (POINTID);

alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID);

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERTRIGGERPT_PT foreign key (TriggerID)
      references POINT (POINTID);

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERVERIF_PT foreign key (VerificationID)
      references POINT (POINTID);

alter table POINTTRIGGER
   add constraint FK_PTTRIGGER_PT foreign key (PointID)
      references POINT (POINTID);

alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID);

alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID);

alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID);

alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID);

alter table PROFILEPEAKRESULT
   add constraint FK_PROFILEPKRSLT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID);

alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table PointToZoneMapping
   add constraint FK_PointZoneMap_Point foreign key (PointId)
      references POINT (POINTID)
      on delete cascade;

alter table PointToZoneMapping
   add constraint FK_PointZoneMap_Zone foreign key (ZoneId)
      references Zone (ZoneId)
      on delete cascade;

alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID);

alter table PorterResponseMonitor
   add constraint FK_PortRespMon_StateGroup foreign key (StateGroupId)
      references StateGroup (StateGroupId);

alter table PorterResponseMonitorErrorCode
   add constraint FK_PortRespMonErr_PortRespMonR foreign key (RuleId)
      references PorterResponseMonitorRule (RuleId)
      on delete cascade;

alter table PorterResponseMonitorRule
   add constraint FK_PortRespMonRule_PortRespMon foreign key (MonitorId)
      references PorterResponseMonitor (MonitorId)
      on delete cascade;

alter table PurchasePlan
   add constraint FK_PRCHSPL_REF_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table RFNAddress
   add constraint FK_RFNAdd_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;

alter table RPHTag
   add constraint FK_RPHTag_RPH foreign key (ChangeId)
      references RAWPOINTHISTORY (CHANGEID)
      on delete cascade;

alter table Regulator
   add constraint FK_Reg_PAO foreign key (RegulatorId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table RegulatorToZoneMapping
   add constraint FK_ZoneReg_Reg foreign key (RegulatorId)
      references Regulator (RegulatorId)
      on delete cascade;

alter table RegulatorToZoneMapping
   add constraint FK_ZoneReg_Zone foreign key (ZoneId)
      references Zone (ZoneId)
      on delete cascade;

alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID);

alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID);

alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID);

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);

alter table ScheduleTimePeriod
   add constraint FK_SCHDTMPRD_REF_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);

alter table ScheduledGrpCommandRequest
   add constraint FK_SchGrpComReq_Job foreign key (JobId)
      references JOB (JobID);

alter table ServiceCompany
   add constraint FK_CstAdd_SrC foreign key (AddressID)
      references Address (AddressID);

alter table ServiceCompany
   add constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
      references Contact (ContactID);

alter table ServiceCompanyDesignationCode
   add constraint FK_ServCompDesCode_ServComp foreign key (ServiceCompanyID)
      references ServiceCompany (CompanyID)
      on delete cascade;

alter table Shipment
   add constraint FK_SHPMNT_WRHSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);

alter table SiteInformation
   add constraint FK_Sub_Si foreign key (SubstationID)
      references Substation (SubstationID);

alter table State
   add constraint FK_YkIm_St foreign key (ImageId)
      references YukonImage (ImageID);

alter table State
   add constraint SYS_C0013342 foreign key (StateGroupId)
      references StateGroup (StateGroupId);

alter table StaticPAOInfo
   add constraint FK_StatPAOInfo foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
      on delete cascade;

alter table StatusPointMonitor
   add constraint FK_StatPointMon_StateGroup foreign key (StateGroupId)
      references StateGroup (StateGroupId);

alter table StatusPointMonitorProcessor
   add constraint FK_StatPointMonProc_StatPointM foreign key (StatusPointMonitorId)
      references StatusPointMonitor (StatusPointMonitorId)
      on delete cascade;

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_RteID foreign key (RouteID)
      references Route (RouteID)
      on delete cascade;

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_SubID foreign key (SubstationID)
      references Substation (SubstationID);

alter table Survey
   add constraint FK_Surv_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
      on delete cascade;

alter table SurveyQuestion
   add constraint FK_SurvQuest_Surv foreign key (SurveyId)
      references Survey (SurveyId)
      on delete cascade;

alter table SurveyQuestionAnswer
   add constraint FK_SurvQuestAns_SurvQuest foreign key (SurveyQuestionId)
      references SurveyQuestion (SurveyQuestionId)
      on delete cascade;

alter table SurveyResult
   add constraint FK_SurvRes_CustAcct foreign key (AccountId)
      references CustomerAccount (AccountID)
      on delete set null;

alter table SurveyResult
   add constraint FK_SurvRes_Surv foreign key (SurveyId)
      references Survey (SurveyId)
      on delete cascade;

alter table SurveyResultAnswer
   add constraint FK_SurvResAns_SurQuestAns foreign key (SurveyQuestionAnswerId)
      references SurveyQuestionAnswer (SurveyQuestionAnswerId);

alter table SurveyResultAnswer
   add constraint FK_SurvResAns_SurvQuest foreign key (SurveyQuestionId)
      references SurveyQuestion (SurveyQuestionId);

alter table SurveyResultAnswer
   add constraint FK_SurvResAns_SurvRes foreign key (SurveyResultId)
      references SurveyResult (SurveyResultId)
      on delete cascade;

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM);

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);

alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);

alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);

alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);

alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID);

alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID);

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_DISPLAY foreign key (DisplayNum)
      references DISPLAY (DISPLAYNUM);

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_TEMPLATE foreign key (TemplateNum)
      references TEMPLATE (TEMPLATENUM);

alter table ThermostatEventHistory
   add constraint FK_ThermEventHist_AcctThermSch foreign key (ScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId)
      on delete set null;

alter table ThermostatEventHistory
   add constraint FK_ThermEventHist_InvBase foreign key (ThermostatId)
      references InventoryBase (InventoryID)
      on delete cascade;

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YKUSR_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YUKPA_YUKONPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

alter table VersacomRoute
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references Route (RouteID);

alter table Warehouse
   add constraint FK_WAREHOUSE_ADDRESS foreign key (AddressID)
      references Address (AddressID);

alter table Warehouse
   add constraint FK_WAREHOUSE_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table WorkOrderBase
   add constraint FK_CsLsE_WkB_c foreign key (CurrentStateID)
      references YukonListEntry (EntryID);

alter table WorkOrderBase
   add constraint FK_CsLsE_WkB foreign key (WorkTypeID)
      references YukonListEntry (EntryID);

alter table WorkOrderBase
   add constraint Ref_WrkOB_CstAc foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table WorkOrderBase
   add constraint FK_WrkOr_SrvC foreign key (ServiceCompanyID)
      references ServiceCompany (CompanyID);

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID);

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID);

alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);

alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID);

alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID);

alter table YukonSelectionList
   add constraint FK_YukonSelList_EnergyComp foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID);

alter table YukonUserGroup
   add constraint FK_YkUsGr_YkGr foreign key (GroupID)
      references YukonGroup (GroupID);

alter table YukonUserGroup
   add constraint FK_YUKONUSE_REF_YKUSG_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);

alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);

alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID);

alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID);

alter table ZBControlEvent
   add constraint FK_ZBContEvent_LMContHist foreign key (LMControlHistoryId)
      references LMControlHistory (LMCtrlHistID);

alter table ZBControlEvent
   add constraint FK_ZBContEvent_LMGroup foreign key (GroupId)
      references LMGroup (DeviceID)
      on delete cascade;

alter table ZBControlEventDevice
   add constraint FK_ZBContEventDev_ZBContEvent foreign key (ZBControlEventId)
      references ZBControlEvent (ZBControlEventId)
      on delete cascade;

alter table ZBControlEventDevice
   add constraint FK_ZBContEventDev_ZBEndPoint foreign key (DeviceId)
      references ZBEndPoint (DeviceId)
      on delete cascade;

alter table ZBEndPoint
   add constraint FK_ZBEndPoint_Device foreign key (DeviceId)
      references DEVICE (DEVICEID);

alter table ZBGateway
   add constraint FK_ZBGate_Device foreign key (DeviceId)
      references DEVICE (DEVICEID);

alter table ZBGatewayToDeviceMapping
   add constraint FK_ZBGateDeviceMap_ZBEndPoint foreign key (DeviceId)
      references ZBEndPoint (DeviceId)
      on delete cascade;

alter table ZBGatewayToDeviceMapping
   add constraint FK_ZBGateDeviceMap_ZBGate foreign key (GatewayId)
      references ZBGateway (DeviceId)
      on delete cascade;

alter table Zone
   add constraint FK_ZONE_CapContSubBus foreign key (SubstationBusId)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
      on delete cascade;

alter table Zone
   add constraint FK_Zone_Zone foreign key (ParentId)
      references Zone (ZoneId);

