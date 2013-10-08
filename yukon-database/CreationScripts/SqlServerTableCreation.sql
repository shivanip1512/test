/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      Microsoft SQL Server 2005                    */
/* Created on:     10/8/2013 5:33:15 PM                         */
/*==============================================================*/


/*==============================================================*/
/* Table: AccountSite                                           */
/*==============================================================*/
create table AccountSite (
   AccountSiteID        numeric              not null,
   SiteInformationID    numeric              null,
   SiteNumber           varchar(40)          not null,
   StreetAddressID      numeric              null,
   PropertyNotes        varchar(300)         null,
   CustomerStatus       varchar(1)           not null,
   CustAtHome           varchar(1)           not null,
   constraint PK_ACCOUNTSITE primary key (AccountSiteID)
)
go

INSERT INTO AccountSite VALUES (0,0,'(none)',0,'(none)',' ','N');

/*==============================================================*/
/* Index: CstSrvCstProp_FK                                      */
/*==============================================================*/
create index CstSrvCstProp_FK on AccountSite (
SiteInformationID ASC
)
go

/*==============================================================*/
/* Index: Indx_AcctSite_SiteNum                                 */
/*==============================================================*/
create index Indx_AcctSite_SiteNum on AccountSite (
SiteNumber ASC
)
go

/*==============================================================*/
/* Table: AcctThermostatSchedule                                */
/*==============================================================*/
create table AcctThermostatSchedule (
   AcctThermostatScheduleId numeric              not null,
   AccountId            numeric              not null,
   ScheduleName         varchar(60)          not null,
   ThermostatType       varchar(60)          not null,
   ScheduleMode         varchar(60)          null,
   Archived             char(1)              not null,
   constraint PK_AcctThermSch primary key nonclustered (AcctThermostatScheduleId)
)
go

/*==============================================================*/
/* Table: AcctThermostatScheduleEntry                           */
/*==============================================================*/
create table AcctThermostatScheduleEntry (
   AcctThermostatScheduleEntryId numeric              not null,
   AcctThermostatScheduleId numeric              not null,
   StartTime            numeric              not null,
   TimeOfWeek           varchar(60)          not null,
   CoolTemp             float                not null,
   HeatTemp             float                not null,
   constraint PK_AcctThermSchEntry primary key nonclustered (AcctThermostatScheduleEntryId)
)
go

/*==============================================================*/
/* Table: ActivityLog                                           */
/*==============================================================*/
create table ActivityLog (
   ActivityLogID        numeric              not null,
   TimeStamp            datetime             not null,
   UserID               numeric              null,
   AccountID            numeric              null,
   EnergyCompanyID      numeric              null,
   CustomerID           numeric              null,
   PaoID                numeric              null,
   Action               varchar(80)          not null,
   Description          varchar(240)         null,
   constraint PK_ACTIVITYLOG primary key (ActivityLogID)
)
go

/*==============================================================*/
/* Table: Address                                               */
/*==============================================================*/
create table Address (
   AddressID            numeric              not null,
   LocationAddress1     varchar(40)          not null,
   LocationAddress2     varchar(40)          not null,
   CityName             varchar(32)          not null,
   StateCode            char(2)              not null,
   ZipCode              varchar(12)          not null,
   County               varchar(30)          not null,
   constraint PK_ADDRESS primary key (AddressID)
)
go

insert into address values ( 0, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' );

/*==============================================================*/
/* Index: Indx_Add_LocAdd                                       */
/*==============================================================*/
create index Indx_Add_LocAdd on Address (
LocationAddress1 ASC
)
go

/*==============================================================*/
/* Table: AlarmCategory                                         */
/*==============================================================*/
create table AlarmCategory (
   AlarmCategoryID      numeric              not null,
   CategoryName         varchar(40)          not null,
   NotificationGroupID  numeric              not null,
   constraint PK_ALARMCATEGORYID primary key (AlarmCategoryID)
)
go

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
create table ApplianceAirConditioner (
   ApplianceID          numeric              not null,
   TonnageID            numeric              null,
   TypeID               numeric              null,
   constraint PK_APPLIANCEAIRCONDITIONER primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceBase                                         */
/*==============================================================*/
create table ApplianceBase (
   ApplianceID          numeric              not null,
   AccountID            numeric              not null,
   ApplianceCategoryID  numeric              not null,
   ProgramID            numeric              null,
   YearManufactured     numeric              null,
   ManufacturerID       numeric              null,
   LocationID           numeric              null,
   KWCapacity           float                null,
   EfficiencyRating     float                null,
   Notes                varchar(500)         null,
   ModelNumber          varchar(40)          not null,
   constraint PK_APPLIANCEBASE primary key (ApplianceID)
)
go

/*==============================================================*/
/* Index: CstAcc_CstLdInfo_FK                                   */
/*==============================================================*/
create index CstAcc_CstLdInfo_FK on ApplianceBase (
AccountID ASC
)
go

/*==============================================================*/
/* Index: CstLdTy_CstLdInf_FK                                   */
/*==============================================================*/
create index CstLdTy_CstLdInf_FK on ApplianceBase (
ApplianceCategoryID ASC
)
go

/*==============================================================*/
/* Table: ApplianceCategory                                     */
/*==============================================================*/
create table ApplianceCategory (
   ApplianceCategoryID  numeric              not null,
   Description          varchar(40)          null,
   CategoryID           numeric              null,
   WebConfigurationID   numeric              null,
   ConsumerSelectable   char(1)              not null,
   AverageKwLoad        varchar(32)          null,
   constraint PK_APPLIANCECATEGORY primary key (ApplianceCategoryID)
)
go

insert into ApplianceCategory values (0, '(none)', 0, 0, 'Y', null);

/*==============================================================*/
/* Table: ApplianceChiller                                      */
/*==============================================================*/
create table ApplianceChiller (
   ApplianceID          numeric              not null,
   TonnageID            numeric              null,
   TypeID               numeric              null,
   constraint PK_APPLIANCECHILLER primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceDualFuel                                     */
/*==============================================================*/
create table ApplianceDualFuel (
   ApplianceID          numeric              not null,
   SwitchOverTypeID     numeric              not null,
   SecondaryKWCapacity  numeric              not null,
   SecondaryEnergySourceID numeric              not null,
   constraint PK_APPLIANCEDUALFUEL primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceDualStageAirCond                             */
/*==============================================================*/
create table ApplianceDualStageAirCond (
   ApplianceID          numeric              not null,
   StageOneTonnageID    numeric              null,
   StageTwoTonnageID    numeric              null,
   TypeID               numeric              null,
   constraint PK_APPLIANCEDUALSTAGEAIRCOND primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceGenerator                                    */
/*==============================================================*/
create table ApplianceGenerator (
   ApplianceID          numeric              not null,
   TransferSwitchTypeID numeric              not null,
   TransferSwitchMfgID  numeric              not null,
   PeakKWCapacity       numeric              not null,
   FuelCapGallons       numeric              not null,
   StartDelaySeconds    numeric              not null,
   constraint PK_APPLIANCEGENERATOR primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceGrainDryer                                   */
/*==============================================================*/
create table ApplianceGrainDryer (
   ApplianceID          numeric              not null,
   DryerTypeID          numeric              not null,
   BinSizeID            numeric              not null,
   BlowerEnergySourceID numeric              not null,
   BlowerHorsePowerID   numeric              not null,
   BlowerHeatSourceID   numeric              not null,
   constraint PK_APPLIANCEGRAINDRYER primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceHeatPump                                     */
/*==============================================================*/
create table ApplianceHeatPump (
   ApplianceID          numeric              not null,
   PumpTypeID           numeric              not null,
   StandbySourceID      numeric              not null,
   SecondsDelayToRestart numeric              not null,
   PumpSizeID           numeric              not null,
   constraint PK_APPLIANCEHEATPUMP primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceIrrigation                                   */
/*==============================================================*/
create table ApplianceIrrigation (
   ApplianceID          numeric              not null,
   IrrigationTypeID     numeric              not null,
   HorsePowerID         numeric              not null,
   EnergySourceID       numeric              not null,
   SoilTypeID           numeric              not null,
   MeterLocationID      numeric              not null,
   MeterVoltageID       numeric              not null,
   constraint PK_APPLIANCEIRRIGATION primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceStorageHeat                                  */
/*==============================================================*/
create table ApplianceStorageHeat (
   ApplianceID          numeric              not null,
   StorageTypeID        numeric              not null,
   PeakKWCapacity       numeric              not null,
   HoursToRecharge      numeric              not null,
   constraint PK_APPLIANCESTORAGEHEAT primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ApplianceWaterHeater                                  */
/*==============================================================*/
create table ApplianceWaterHeater (
   ApplianceID          numeric              not null,
   NumberOfGallonsID    numeric              null,
   EnergySourceID       numeric              not null,
   NumberOfElements     numeric              not null,
   constraint PK_APPLIANCEWATERHEATER primary key (ApplianceID)
)
go

/*==============================================================*/
/* Table: ArchiveDataAnalysis                                   */
/*==============================================================*/
create table ArchiveDataAnalysis (
   AnalysisId           numeric              not null,
   Attribute            varchar(60)          not null,
   LastChangeId         numeric              not null,
   RunDate              datetime             not null,
   ExcludeBadPointQualities char(1)              not null,
   StartDate            datetime             not null,
   StopDate             datetime             not null,
   AnalysisStatus       varchar(60)          not null,
   StatusId             varchar(60)          null,
   IntervalPeriod       varchar(60)          not null,
   constraint PK_ArcDataAnal primary key (AnalysisId)
)
go

/*==============================================================*/
/* Table: ArchiveDataAnalysisSlot                               */
/*==============================================================*/
create table ArchiveDataAnalysisSlot (
   SlotId               numeric              not null,
   AnalysisId           numeric              not null,
   StartTime            datetime             not null,
   constraint PK_ArcDataAnalSlot primary key (SlotId)
)
go

/*==============================================================*/
/* Index: Indx_ADAS_AnalysisId_SlotId                           */
/*==============================================================*/
create index Indx_ADAS_AnalysisId_SlotId on ArchiveDataAnalysisSlot (
AnalysisId ASC,
SlotId ASC
)
go

/*==============================================================*/
/* Table: ArchiveDataAnalysisSlotValue                          */
/*==============================================================*/
create table ArchiveDataAnalysisSlotValue (
   DeviceId             numeric              not null,
   SlotId               numeric              not null,
   ChangeId             numeric              null,
   constraint PK_ArcDataAnalSlotValue primary key (DeviceId, SlotId)
)
go

/*==============================================================*/
/* Index: Indx_ADASV_SlotId_DeviceId                            */
/*==============================================================*/
create index Indx_ADASV_SlotId_DeviceId on ArchiveDataAnalysisSlotValue (
SlotId ASC,
DeviceId ASC
)
go

/*==============================================================*/
/* Table: ArchiveValuesExportAttribute                          */
/*==============================================================*/
create table ArchiveValuesExportAttribute (
   AttributeId          numeric              not null,
   FormatId             numeric              not null,
   AttributeName        varchar(50)          not null,
   DataSelection        varchar(12)          not null,
   DaysPrevious         numeric              not null,
   constraint PK_ArchiveValuesExpAttribute primary key (AttributeId)
)
go

/*==============================================================*/
/* Index: Indx_ArchValExpAttr_FormatId                          */
/*==============================================================*/
create index Indx_ArchValExpAttr_FormatId on ArchiveValuesExportAttribute (
FormatId ASC
)
go

/*==============================================================*/
/* Table: ArchiveValuesExportField                              */
/*==============================================================*/
create table ArchiveValuesExportField (
   FieldId              numeric              not null,
   FormatId             numeric              not null,
   FieldType            varchar(50)          null,
   AttributeId          numeric              null,
   AttributeField       varchar(50)          null,
   Pattern              varchar(50)          null,
   MaxLength            numeric              null,
   PadChar              char(1)              null,
   PadSide              varchar(5)           null,
   RoundingMode         varchar(20)          null,
   MissingAttribute     varchar(20)          null,
   MissingAttributeValue varchar(20)          null,
   constraint PK_ArchiveValuesExpField primary key (FieldId)
)
go

/*==============================================================*/
/* Index: Indx_ArchValExportFld_FormatId                        */
/*==============================================================*/
create index Indx_ArchValExportFld_FormatId on ArchiveValuesExportField (
FormatId ASC
)
go

/*==============================================================*/
/* Table: ArchiveValuesExportFormat                             */
/*==============================================================*/
create table ArchiveValuesExportFormat (
   FormatId             numeric              not null,
   FormatName           varchar(100)         not null,
   Delimiter            varchar(20)          null,
   Header               varchar(255)         null,
   Footer               varchar(255)         null,
   FormatType           varchar(40)          not null,
   TimeZoneFormat       varchar(20)          not null,
   constraint PK_ArchiveValuesExpFormat primary key (FormatId)
)
go

/*==============================================================*/
/* Table: BaseLine                                              */
/*==============================================================*/
create table BaseLine (
   BaselineID           numeric              not null,
   BaselineName         varchar(30)          not null,
   DaysUsed             numeric              not null,
   PercentWindow        numeric              not null,
   CalcDays             numeric              not null,
   ExcludedWeekDays     char(7)              not null,
   HolidayScheduleId    numeric              not null,
   constraint PK_BASELINE primary key (BaselineID)
)
go

insert into baseline values (1, 'Default Baseline', 30, 75, 5, 'YNNNNNY', 0);

/*==============================================================*/
/* Table: BillingFileFormats                                    */
/*==============================================================*/
create table BillingFileFormats (
   FormatID             numeric              not null,
   FormatType           varchar(100)         not null,
   SystemFormat         smallint             not null,
   constraint PK_BILLINGFILEFORMATS primary key (FormatID)
)
go

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
INSERT INTO BillingFileFormats VALUES(-27, 'CMEP-MDM1', 1);
INSERT INTO BillingFileFormats VALUES( 31, 'STANDARD',1);
INSERT INTO BillingFileFormats VALUES(-32, 'NISC TOU (kVarH) Rates Only',1);
INSERT INTO BillingFileFormats VALUES( 33, 'NISC Interval Readings', 1);
INSERT INTO BillingFileFormats VALUES(-34, 'Curtailment Events - Itron', 1);

/*==============================================================*/
/* Index: Indx_BillFile_FormType_UNQ                            */
/*==============================================================*/
create unique index Indx_BillFile_FormType_UNQ on BillingFileFormats (
FormatType ASC
)
go

/*==============================================================*/
/* Table: CALCBASE                                              */
/*==============================================================*/
create table CALCBASE (
   POINTID              numeric              not null,
   UPDATETYPE           varchar(16)          not null,
   PERIODICRATE         numeric              not null,
   QualityFlag          char(1)              not null,
   constraint PK_CALCBASE primary key (POINTID)
)
go

/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create index Indx_ClcBaseUpdTyp on CALCBASE (
UPDATETYPE ASC
)
go

/*==============================================================*/
/* Table: CALCCOMPONENT                                         */
/*==============================================================*/
create table CALCCOMPONENT (
   PointID              numeric              not null,
   COMPONENTORDER       numeric              not null,
   COMPONENTTYPE        varchar(10)          not null,
   COMPONENTPOINTID     numeric              not null,
   OPERATION            varchar(10)          null,
   CONSTANT             float                not null,
   FUNCTIONNAME         varchar(20)          null,
   constraint PK_CALCCOMPONENT primary key (PointID, COMPONENTORDER)
)
go

/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create index Indx_CalcCmpCmpType on CALCCOMPONENT (
COMPONENTTYPE ASC
)
go

/*==============================================================*/
/* Table: CAPBANK                                               */
/*==============================================================*/
create table CAPBANK (
   DEVICEID             numeric              not null,
   OPERATIONALSTATE     varchar(16)          not null,
   ControllerType       varchar(64)          not null,
   CONTROLDEVICEID      numeric              not null,
   CONTROLPOINTID       numeric              not null,
   BANKSIZE             numeric              not null,
   TypeOfSwitch         varchar(64)          not null,
   SwitchManufacture    varchar(64)          not null,
   MapLocationID        varchar(64)          not null,
   RecloseDelay         numeric              not null,
   MaxDailyOps          numeric              not null,
   MaxOpDisable         char(1)              not null,
   constraint PK_CAPBANK primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: CAPBANKADDITIONAL                                     */
/*==============================================================*/
create table CAPBANKADDITIONAL (
   DeviceID             numeric              not null,
   MaintenanceAreaID    numeric              not null,
   PoleNumber           numeric              not null,
   DriveDirections      varchar(120)         not null,
   Latitude             float                not null,
   Longitude            float                not null,
   CapBankConfig        varchar(10)          not null,
   CommMedium           varchar(20)          not null,
   CommStrength         numeric              not null,
   ExtAntenna           char(1)              not null,
   AntennaType          varchar(10)          not null,
   LastMaintVisit       datetime             not null,
   LastInspVisit        datetime             not null,
   OpCountResetDate     datetime             not null,
   PotentialTransformer varchar(100)         not null,
   MaintenanceReqPend   char(1)              not null,
   OtherComments        varchar(150)         not null,
   OpTeamComments       varchar(150)         not null,
   CBCBattInstallDate   datetime             not null,
   constraint PK_CAPBANKADDITIONAL primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: CAPCONTROLAREA                                        */
/*==============================================================*/
create table CAPCONTROLAREA (
   AreaID               numeric              not null,
   VoltReductionPointID numeric              not null,
   constraint PK_CAPCONTROLAREA primary key (AreaID)
)
go

/*==============================================================*/
/* Table: CAPCONTROLCOMMENT                                     */
/*==============================================================*/
create table CAPCONTROLCOMMENT (
   CommentID            int                  not null,
   PaoID                numeric              not null,
   UserID               numeric              null,
   Action               varchar(50)          not null,
   CommentTime          datetime             not null,
   CapComment           varchar(500)         not null,
   Altered              varchar(3)           not null,
   constraint PK_CAPCONTROLCOMMENT primary key (CommentID)
)
go

/*==============================================================*/
/* Table: CAPCONTROLSPECIALAREA                                 */
/*==============================================================*/
create table CAPCONTROLSPECIALAREA (
   AreaID               numeric              not null,
   VoltReductionPointID numeric              not null,
   constraint PK_CapControlSpecialArea primary key (AreaID)
)
go

/*==============================================================*/
/* Table: CAPCONTROLSUBSTATION                                  */
/*==============================================================*/
create table CAPCONTROLSUBSTATION (
   SubstationID         numeric              not null,
   VoltReductionPointId numeric              not null,
   MapLocationId        varchar(64)          not null,
   constraint PK_CAPCONTROLSUBSTATION primary key (SubstationID)
)
go

/*==============================================================*/
/* Table: CAPCONTROLSUBSTATIONBUS                               */
/*==============================================================*/
create table CAPCONTROLSUBSTATIONBUS (
   SubstationBusID      numeric              not null,
   CurrentVarLoadPointID numeric              not null,
   CurrentWattLoadPointID numeric              not null,
   MapLocationID        varchar(64)          not null,
   CurrentVoltLoadPointID numeric              not null,
   AltSubID             numeric              not null,
   SwitchPointID        numeric              not null,
   DualBusEnabled       char(1)              not null,
   MultiMonitorControl  char(1)              not null,
   usephasedata         char(1)              not null,
   phaseb               numeric              not null,
   phasec               numeric              not null,
   ControlFlag          char(1)              not null,
   VoltReductionPointId numeric              not null,
   DisableBusPointId    numeric              not null,
   constraint SYS_C0013476 primary key (SubstationBusID)
)
go

/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
CurrentVarLoadPointID ASC
)
go

/*==============================================================*/
/* Table: CCEventLog                                            */
/*==============================================================*/
create table CCEventLog (
   LogID                numeric              not null,
   PointID              numeric              not null,
   DateTime             datetime             not null,
   SubID                numeric              not null,
   FeederID             numeric              not null,
   EventType            numeric              not null,
   SeqID                numeric              not null,
   Value                numeric              not null,
   Text                 varchar(120)         not null,
   UserName             varchar(64)          not null,
   KVARBefore           float                not null,
   KVARAfter            float                not null,
   KVARChange           float                not null,
   AdditionalInfo       varchar(20)          not null,
   actionId             numeric              not null,
   CapBankStateInfo     varchar(20)          not null,
   aVar                 float                not null,
   bVar                 float                not null,
   cVar                 float                not null,
   StationID            numeric              not null,
   AreaID               numeric              not null,
   SpAreaID             numeric              not null,
   RegulatorId          numeric              not null,
   constraint PK_CCEventLog primary key (LogID)
)
go

/*==============================================================*/
/* Index: INDX_CCEventLog_PointId_ActId                         */
/*==============================================================*/
create index INDX_CCEventLog_PointId_ActId on CCEventLog (
PointID ASC,
actionId ASC
)
go

/*==============================================================*/
/* Index: INDX_CCEventLog_ActId                                 */
/*==============================================================*/
create index INDX_CCEventLog_ActId on CCEventLog (
actionId ASC
)
go

/*==============================================================*/
/* Index: INDX_CCEventLog_PointId                               */
/*==============================================================*/
create index INDX_CCEventLog_PointId on CCEventLog (
PointID ASC
)
go

/*==============================================================*/
/* Index: INDX_CCEventLog_FeedId                                */
/*==============================================================*/
create index INDX_CCEventLog_FeedId on CCEventLog (
FeederID ASC
)
go

/*==============================================================*/
/* Index: INDX_CCEventLog_SubId                                 */
/*==============================================================*/
create index INDX_CCEventLog_SubId on CCEventLog (
SubID ASC
)
go

/*==============================================================*/
/* Index: INDX_CCEventLog_Text                                  */
/*==============================================================*/
create index INDX_CCEventLog_Text on CCEventLog (
Text ASC
)
go

/*==============================================================*/
/* Table: CCFeederBankList                                      */
/*==============================================================*/
create table CCFeederBankList (
   FeederID             numeric              not null,
   DeviceID             numeric              not null,
   ControlOrder         numeric(18,5)        not null,
   CloseOrder           numeric(18,5)        not null,
   TripOrder            numeric(18,5)        not null,
   constraint PK_CCFEEDERBANKLIST primary key (FeederID, DeviceID)
)
go

/*==============================================================*/
/* Table: CCFeederSubAssignment                                 */
/*==============================================================*/
create table CCFeederSubAssignment (
   SubStationBusID      numeric              not null,
   FeederID             numeric              not null,
   DisplayOrder         numeric              not null,
   constraint PK_CCFEEDERSUBASSIGNMENT primary key (SubStationBusID, FeederID)
)
go

/*==============================================================*/
/* Table: CCHOLIDAYSTRATEGYASSIGNMENT                           */
/*==============================================================*/
create table CCHOLIDAYSTRATEGYASSIGNMENT (
   PAObjectId           numeric              not null,
   HolidayScheduleId    numeric              not null,
   StrategyId           numeric              not null,
   constraint PK_CCHOLIDAYSTRATEGYASSIGNMENT primary key (PAObjectId)
)
go

/*==============================================================*/
/* Table: CCMonitorBankList                                     */
/*==============================================================*/
create table CCMonitorBankList (
   DeviceId             numeric              not null,
   PointId              numeric              not null,
   DisplayOrder         numeric              not null,
   Scannable            char(1)              not null,
   NINAvg               numeric              not null,
   UpperBandwidth       float                not null,
   LowerBandwidth       float                not null,
   Phase                char(1)              null,
   OverrideStrategy     char(1)              not null,
   constraint PK_CCMONITORBANKLIST primary key nonclustered (DeviceId, PointId)
)
go

/*==============================================================*/
/* Table: CCOperationLogCache                                   */
/*==============================================================*/
create table CCOperationLogCache (
   OperationLogId       numeric              not null,
   ConfirmationLogId    numeric              null,
   constraint PK_CCOPERATIONLOGCACHE primary key (OperationLogId)
)
go

/*==============================================================*/
/* Table: CCSEASONSTRATEGYASSIGNMENT                            */
/*==============================================================*/
create table CCSEASONSTRATEGYASSIGNMENT (
   paobjectid           numeric              not null,
   seasonscheduleid     numeric              not null,
   seasonname           varchar(20)          not null,
   strategyid           numeric              not null,
   constraint PK_CCSEASONSTRATEGYASSIGNMENT primary key (paobjectid, seasonscheduleid, seasonname)
)
go

/*==============================================================*/
/* Table: CCSUBAREAASSIGNMENT                                   */
/*==============================================================*/
create table CCSUBAREAASSIGNMENT (
   AreaID               numeric              not null,
   SubstationBusID      numeric              not null,
   DisplayOrder         numeric              not null,
   constraint PK_CCSUBAREAASSIGNMENT primary key (AreaID, SubstationBusID)
)
go

/*==============================================================*/
/* Table: CCSUBSPECIALAREAASSIGNMENT                            */
/*==============================================================*/
create table CCSUBSPECIALAREAASSIGNMENT (
   AreaID               numeric              not null,
   SubstationBusID      numeric              not null,
   DisplayOrder         numeric              not null,
   constraint PK_CCSUBSPECIALAREAASSIGNMENT primary key (AreaID, SubstationBusID)
)
go

/*==============================================================*/
/* Table: CCSUBSTATIONSUBBUSLIST                                */
/*==============================================================*/
create table CCSUBSTATIONSUBBUSLIST (
   SubStationID         numeric              not null,
   SubStationBusID      numeric              not null,
   DisplayOrder         numeric              not null,
   constraint PK_CCSUBSTATIONSUBBUSLIST primary key (SubStationID, SubStationBusID)
)
go

/*==============================================================*/
/* Table: CCStrategyTargetSettings                              */
/*==============================================================*/
create table CCStrategyTargetSettings (
   StrategyId           numeric              not null,
   SettingName          varchar(255)         not null,
   SettingValue         varchar(255)         not null,
   SettingType          varchar(255)         not null,
   constraint PK_CCStratTarSet primary key (StrategyId, SettingName, SettingType)
)
go

/*==============================================================*/
/* Table: CCURTACCTEVENT                                        */
/*==============================================================*/
create table CCURTACCTEVENT (
   CCurtAcctEventID     numeric              not null,
   CCurtProgramID       numeric              not null,
   Duration             numeric              not null,
   Reason               varchar(255)         not null,
   StartTime            datetime             not null,
   Identifier           numeric              not null,
   constraint PK_CCURTACCTEVENT primary key (CCurtAcctEventID)
)
go

/*==============================================================*/
/* Table: CCURTACCTEVENTPARTICIPANT                             */
/*==============================================================*/
create table CCURTACCTEVENTPARTICIPANT (
   CCurtAcctEventParticipantID numeric              not null,
   CustomerID           numeric              not null,
   CCurtAcctEventID     numeric              not null,
   constraint PK_CCURTACCTEVENTPARTICIPANT primary key (CCurtAcctEventParticipantID)
)
go

/*==============================================================*/
/* Table: CCurtCENotif                                          */
/*==============================================================*/
create table CCurtCENotif (
   CCurtCENotifID       numeric              not null,
   NotificationTime     datetime             null,
   NotifTypeID          numeric              not null,
   State                varchar(10)          not null,
   Reason               varchar(10)          not null,
   CCurtCEParticipantID numeric              not null,
   constraint PK_CCURTCENOTIF primary key (CCurtCENotifID)
)
go

/*==============================================================*/
/* Table: CCurtCEParticipant                                    */
/*==============================================================*/
create table CCurtCEParticipant (
   CCurtCEParticipantID numeric              not null,
   NotifAttribs         varchar(256)         not null,
   CustomerID           numeric              not null,
   CCurtCurtailmentEventID numeric              not null,
   constraint PK_CCURTCEPARTICIPANT primary key (CCurtCEParticipantID)
)
go

/*==============================================================*/
/* Index: INDX_CCURTCEPART_EVTID_CUSTID                         */
/*==============================================================*/
create unique index INDX_CCURTCEPART_EVTID_CUSTID on CCurtCEParticipant (
CustomerID ASC,
CCurtCurtailmentEventID ASC
)
go

/*==============================================================*/
/* Table: CCurtCurtailmentEvent                                 */
/*==============================================================*/
create table CCurtCurtailmentEvent (
   CCurtCurtailmentEventID numeric              not null,
   CCurtProgramID       numeric              null,
   NotificationTime     datetime             not null,
   Duration             numeric              not null,
   Message              varchar(255)         not null,
   State                varchar(10)          not null,
   StartTime            datetime             not null,
   Identifier           numeric              not null,
   constraint PK_CCURTCURTAILMENTEVENT primary key (CCurtCurtailmentEventID)
)
go

/*==============================================================*/
/* Table: CCurtEEParticipant                                    */
/*==============================================================*/
create table CCurtEEParticipant (
   CCurtEEParticipantID numeric              not null,
   NotifAttribs         varchar(255)         not null,
   CustomerID           numeric              not null,
   CCurtEconomicEventID numeric              not null,
   constraint PK_CCURTEEPARTICIPANT primary key (CCurtEEParticipantID)
)
go

/*==============================================================*/
/* Table: CCurtEEParticipantSelection                           */
/*==============================================================*/
create table CCurtEEParticipantSelection (
   CCurtEEParticipantSelectionID numeric              not null,
   ConnectionAudit      varchar(2550)        not null,
   SubmitTime           datetime             not null,
   State                varchar(255)         not null,
   CCurtEEParticipantID numeric              not null,
   CCurtEEPricingID     numeric              not null,
   constraint PK_CCURTEEPARTICIPANTSELECTION primary key (CCurtEEParticipantSelectionID)
)
go

/*==============================================================*/
/* Index: INDX_CCURTEEPARTSEL_CCURTEEPR                         */
/*==============================================================*/
create unique index INDX_CCURTEEPARTSEL_CCURTEEPR on CCurtEEParticipantSelection (
CCurtEEParticipantID ASC,
CCurtEEPricingID ASC
)
go

/*==============================================================*/
/* Table: CCurtEEParticipantWindow                              */
/*==============================================================*/
create table CCurtEEParticipantWindow (
   CCurtEEParticipantWindowID numeric              not null,
   EnergyToBuy          numeric(19,2)        not null,
   CCurtEEPricingWindowID numeric              not null,
   CCurtEEParticipantSelectionID numeric              not null,
   constraint PK_CCURTEEPARTICIPANTWINDOW primary key (CCurtEEParticipantWindowID)
)
go

/*==============================================================*/
/* Index: INDX_CCRTEEPRTWIN_PWNID_PSID                          */
/*==============================================================*/
create unique index INDX_CCRTEEPRTWIN_PWNID_PSID on CCurtEEParticipantWindow (
CCurtEEPricingWindowID ASC,
CCurtEEParticipantSelectionID ASC
)
go

/*==============================================================*/
/* Table: CCurtEEPricing                                        */
/*==============================================================*/
create table CCurtEEPricing (
   CCurtEEPricingID     numeric              not null,
   Revision             numeric              not null,
   CreationTime         datetime             not null,
   CCurtEconomicEventID numeric              not null,
   constraint PK_CCURTEEPRICING primary key (CCurtEEPricingID)
)
go

/*==============================================================*/
/* Index: INDX_CCURTECONSVTID_REV                               */
/*==============================================================*/
create unique index INDX_CCURTECONSVTID_REV on CCurtEEPricing (
Revision ASC,
CCurtEconomicEventID ASC
)
go

/*==============================================================*/
/* Table: CCurtEEPricingWindow                                  */
/*==============================================================*/
create table CCurtEEPricingWindow (
   CCurtEEPricingWindowID numeric              not null,
   EnergyPrice          numeric(19,6)        not null,
   Offset               numeric              not null,
   CCurtEEPricingID     numeric              not null,
   constraint PK_CCURTEEPRICINGWINDOW primary key (CCurtEEPricingWindowID)
)
go

/*==============================================================*/
/* Index: INDX_CCURTEEPRWIN                                     */
/*==============================================================*/
create unique index INDX_CCURTEEPRWIN on CCurtEEPricingWindow (
Offset ASC,
CCurtEEPricingID ASC
)
go

/*==============================================================*/
/* Table: CCurtEconomicEvent                                    */
/*==============================================================*/
create table CCurtEconomicEvent (
   CCurtEconomicEventID numeric              not null,
   NotificationTime     datetime             null,
   WindowLengthMinutes  numeric              not null,
   State                varchar(10)          not null,
   StartTime            datetime             not null,
   CCurtProgramID       numeric              not null,
   InitialEventID       numeric              null,
   Identifier           numeric              not null,
   constraint PK_CCURTECONOMICEVENT primary key (CCurtEconomicEventID)
)
go

/*==============================================================*/
/* Table: CCurtEconomicEventNotif                               */
/*==============================================================*/
create table CCurtEconomicEventNotif (
   CCurtEconomicEventNotifID numeric              not null,
   NotificationTime     datetime             null,
   NotifTypeID          numeric              not null,
   State                varchar(10)          not null,
   Reason               varchar(10)          not null,
   CCurtEEPricingID     numeric              not null,
   CCurtEconomicParticipantID numeric              not null,
   constraint PK_CCURTECONOMICEVENTNOTIF primary key (CCurtEconomicEventNotifID)
)
go

/*==============================================================*/
/* Table: CCurtGroup                                            */
/*==============================================================*/
create table CCurtGroup (
   CCurtGroupID         numeric              not null,
   EnergyCompanyID      numeric              not null,
   CCurtGroupName       varchar(255)         not null,
   constraint PK_CCURTGROUP primary key (CCurtGroupID)
)
go

/*==============================================================*/
/* Index: INDX_CCURTGROUP_ECID_GRPNM                            */
/*==============================================================*/
create unique index INDX_CCURTGROUP_ECID_GRPNM on CCurtGroup (
EnergyCompanyID ASC,
CCurtGroupName ASC
)
go

/*==============================================================*/
/* Table: CCurtGroupCustomerNotif                               */
/*==============================================================*/
create table CCurtGroupCustomerNotif (
   CCurtGroupCustomerNotifID numeric              not null,
   Attribs              varchar(255)         not null,
   CustomerID           numeric              not null,
   CCurtGroupID         numeric              not null,
   constraint PK_CCURTGROUPCUSTOMERNOTIF primary key (CCurtGroupCustomerNotifID)
)
go

/*==============================================================*/
/* Index: INDX_CCRTGRPCSTNOTIF_GID_CID                          */
/*==============================================================*/
create unique index INDX_CCRTGRPCSTNOTIF_GID_CID on CCurtGroupCustomerNotif (
CustomerID ASC,
CCurtGroupID ASC
)
go

/*==============================================================*/
/* Table: CCurtProgram                                          */
/*==============================================================*/
create table CCurtProgram (
   CCurtProgramID       numeric              not null,
   CCurtProgramName     varchar(255)         not null,
   CCurtProgramTypeID   numeric              null,
   LastIdentifier       numeric              not null,
   IdentifierPrefix     varchar(32)          not null,
   constraint PK_CCURTPROGRAM primary key (CCurtProgramID)
)
go

/*==============================================================*/
/* Index: INDX_CCURTPGM_PRGNM_PRGTYPEID                         */
/*==============================================================*/
create index INDX_CCURTPGM_PRGNM_PRGTYPEID on CCurtProgram (
CCurtProgramName ASC,
CCurtProgramTypeID ASC
)
go

/*==============================================================*/
/* Table: CCurtProgramGroup                                     */
/*==============================================================*/
create table CCurtProgramGroup (
   CCurtProgramGroupID  numeric              not null,
   CCurtProgramID       numeric              not null,
   CCurtGroupID         numeric              not null,
   constraint PK_CCURTPROGRAMGROUP primary key (CCurtProgramGroupID)
)
go

/*==============================================================*/
/* Index: INDX_CCURTPRGGRP_GRPID_PRGID                          */
/*==============================================================*/
create unique index INDX_CCURTPRGGRP_GRPID_PRGID on CCurtProgramGroup (
CCurtProgramID ASC,
CCurtGroupID ASC
)
go

/*==============================================================*/
/* Table: CCurtProgramNotifGroup                                */
/*==============================================================*/
create table CCurtProgramNotifGroup (
   CCurtProgramID       numeric              not null,
   NotificationGroupID  numeric              not null,
   constraint PK_CCURTPROGRAMNOTIFGROUP primary key (CCurtProgramID, NotificationGroupID)
)
go

/*==============================================================*/
/* Index: INDX_CCURPNG_PRGNM_PRGTYPEID                          */
/*==============================================================*/
create index INDX_CCURPNG_PRGNM_PRGTYPEID on CCurtProgramNotifGroup (
NotificationGroupID ASC
)
go

/*==============================================================*/
/* Table: CCurtProgramParameter                                 */
/*==============================================================*/
create table CCurtProgramParameter (
   CCurtProgramParameterID numeric              not null,
   ParameterValue       varchar(255)         not null,
   ParameterKey         varchar(255)         not null,
   CCurtProgramID       numeric              null,
   constraint PK_CCURTPROGRAMPARAMETER primary key (CCurtProgramParameterID)
)
go

/*==============================================================*/
/* Index: INDX_CCRTPRGPRM_PGID_PMKEY                            */
/*==============================================================*/
create index INDX_CCRTPRGPRM_PGID_PMKEY on CCurtProgramParameter (
ParameterKey ASC,
CCurtProgramID ASC
)
go

/*==============================================================*/
/* Table: CCurtProgramType                                      */
/*==============================================================*/
create table CCurtProgramType (
   CCurtProgramTypeID   numeric              not null,
   EnergyCompanyID      numeric              null,
   CCurtProgramTypeStrategy varchar(255)         null,
   CCurtProgramTypeName varchar(255)         null,
   constraint PK_CCURTPROGRAMTYPE primary key (CCurtProgramTypeID)
)
go

/*==============================================================*/
/* Table: CICUSTOMERPOINTDATA                                   */
/*==============================================================*/
create table CICUSTOMERPOINTDATA (
   CustomerID           numeric              not null,
   PointID              numeric              not null,
   Type                 varchar(16)          not null,
   OptionalLabel        varchar(32)          not null,
   constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, Type)
)
go

/*==============================================================*/
/* Table: CICustomerBase                                        */
/*==============================================================*/
create table CICustomerBase (
   CustomerID           numeric              not null,
   MainAddressID        numeric              not null,
   CustomerDemandLevel  float                not null,
   CurtailmentAgreement varchar(100)         not null,
   CurtailAmount        float                not null,
   CompanyName          varchar(80)          not null,
   CiCustType           numeric              not null,
   constraint PK_CICUSTOMERBASE primary key (CustomerID)
)
go

/*==============================================================*/
/* Index: Indx_CICustBase_CompName                              */
/*==============================================================*/
create index Indx_CICustBase_CompName on CICustomerBase (
CompanyName ASC
)
go

/*==============================================================*/
/* Table: COLUMNTYPE                                            */
/*==============================================================*/
create table COLUMNTYPE (
   TYPENUM              numeric              not null,
   NAME                 varchar(20)          not null,
   constraint SYS_C0013414 primary key (TYPENUM)
)
go

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
create table CTIDatabase (
   Version              varchar(6)           not null,
   BuildDate            datetime             null,
   Notes                varchar(300)         null,
   Build                numeric              not null,
   InstallDate          datetime             null,
   constraint PK_CTIDATABASE primary key (Version, Build)
)
go

/* __YUKON_VERSION__ */

/*==============================================================*/
/* Table: CalcPointBaseline                                     */
/*==============================================================*/
create table CalcPointBaseline (
   PointID              numeric              not null,
   BaselineID           numeric              not null,
   constraint PK_CalcBsPt primary key (PointID)
)
go

/*==============================================================*/
/* Table: CallReportBase                                        */
/*==============================================================*/
create table CallReportBase (
   CallID               numeric              not null,
   CallNumber           varchar(20)          null,
   CallTypeID           numeric              null,
   DateTaken            datetime             null,
   TakenBy              varchar(30)          null,
   Description          varchar(300)         null,
   AccountID            numeric              null,
   constraint PK_CALLREPORTBASE primary key (CallID)
)
go

/*==============================================================*/
/* Table: CapBankToZoneMapping                                  */
/*==============================================================*/
create table CapBankToZoneMapping (
   DeviceId             numeric              not null,
   ZoneId               numeric              not null,
   GraphPositionOffset  float                null,
   Distance             float                null,
   constraint PK_CapBankZoneMap primary key (DeviceId)
)
go

/*==============================================================*/
/* Table: CapControlFeeder                                      */
/*==============================================================*/
create table CapControlFeeder (
   FeederID             numeric              not null,
   CurrentVarLoadPointID numeric              not null,
   CurrentWattLoadPointID numeric              not null,
   MapLocationID        varchar(64)          not null,
   CurrentVoltLoadPointID numeric              not null,
   MultiMonitorControl  char(1)              not null,
   usephasedata         char(1)              not null,
   phaseb               numeric              not null,
   phasec               numeric              not null,
   ControlFlag          char(1)              not null,
   constraint PK_CAPCONTROLFEEDER primary key (FeederID)
)
go

/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create index Indx_CPCNFDVARPT on CapControlFeeder (
CurrentVarLoadPointID ASC
)
go

/*==============================================================*/
/* Table: CapControlStrategy                                    */
/*==============================================================*/
create table CapControlStrategy (
   StrategyID           numeric              not null,
   StrategyName         varchar(32)          not null,
   ControlMethod        varchar(32)          null,
   MAXDAILYOPERATION    numeric              not null,
   MaxOperationDisableFlag char(1)              not null,
   PEAKSTARTTIME        numeric              not null,
   PEAKSTOPTIME         numeric              not null,
   CONTROLINTERVAL      numeric              not null,
   MINRESPONSETIME      numeric              not null,
   MINCONFIRMPERCENT    numeric              not null,
   FAILUREPERCENT       numeric              not null,
   DAYSOFWEEK           char(8)              not null,
   ControlUnits         varchar(32)          not null,
   ControlDelayTime     numeric              not null,
   ControlSendRetries   numeric              not null,
   IntegrateFlag        char(1)              not null,
   IntegratePeriod      numeric              not null,
   LikeDayFallBack      char(1)              not null,
   EndDaySettings       varchar(20)          not null,
   constraint PK_CAPCONTROLSTRAT primary key (StrategyID)
)
go

/*==============================================================*/
/* Index: Indx_CapCntrlStrat_name_UNQ                           */
/*==============================================================*/
create unique index Indx_CapCntrlStrat_name_UNQ on CapControlStrategy (
StrategyName ASC
)
go

/*==============================================================*/
/* Table: CarrierRoute                                          */
/*==============================================================*/
create table CarrierRoute (
   ROUTEID              numeric              not null,
   BUSNUMBER            numeric              not null,
   CCUFIXBITS           numeric              not null,
   CCUVARIABLEBITS      numeric              not null,
   UserLocked           char(1)              not null,
   ResetRptSettings     char(1)              not null,
   constraint PK_CARRIERROUTE primary key (ROUTEID)
)
go

/*==============================================================*/
/* Table: CommPort                                              */
/*==============================================================*/
create table CommPort (
   PORTID               numeric              not null,
   ALARMINHIBIT         varchar(1)           not null,
   COMMONPROTOCOL       varchar(8)           not null,
   PERFORMTHRESHOLD     numeric              not null,
   PERFORMANCEALARM     varchar(1)           not null,
   SharedPortType       varchar(20)          not null,
   SharedSocketNumber   numeric              not null,
   constraint SYS_C0013112 primary key (PORTID)
)
go

/*==============================================================*/
/* Table: Command                                               */
/*==============================================================*/
create table Command (
   CommandID            numeric              not null,
   Command              varchar(256)         not null,
   Label                varchar(256)         not null,
   Category             varchar(32)          not null,
   constraint PK_COMMAND primary key (CommandID)
)
go

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

INSERT INTO Command VALUES (-189, 'getvalue instant line data', 'Read instant line data', 'MCT-440-2131B');
INSERT INTO Command VALUES (-190, 'getvalue outage ?''Outage Log (1 - 10)''', 'Read two outages per read.  Specify 1(1&2), 3(3&4), 5(5&6), 7(7&8), 9(9&10)', 'MCT-440-2131B');
INSERT INTO Command VALUES (-191, 'putstatus reset alarms', 'Reset meter alarms', 'MCT-440-2131B');
INSERT INTO Command VALUES (-192, 'getstatus eventlog', 'Read event log', 'MCT-440-2131B');

/*==============================================================*/
/* Table: CommandGroup                                          */
/*==============================================================*/
create table CommandGroup (
   CommandGroupID       numeric              not null,
   CommandGroupName     varchar(60)          not null,
   constraint PK_COMMANDGROUP primary key (CommandGroupID)
)
go

insert into CommandGroup values (-1, 'Default Commands');

/*==============================================================*/
/* Index: AK_KEY_CmdGrp_Name                                    */
/*==============================================================*/
create unique index AK_KEY_CmdGrp_Name on CommandGroup (
CommandGroupName ASC
)
go

/*==============================================================*/
/* Table: CommandRequestExec                                    */
/*==============================================================*/
create table CommandRequestExec (
   CommandRequestExecId numeric              not null,
   StartTime            datetime             not null,
   StopTime             datetime             null,
   RequestCount         numeric              null,
   CommandRequestExecType varchar(255)         not null,
   UserName             varchar(64)          null,
   CommandRequestType   varchar(100)         not null,
   CommandRequestExecContextId numeric              not null,
   ExecutionStatus      varchar(100)         not null,
   constraint PK_CommandRequestExec primary key (CommandRequestExecId)
)
go

/*==============================================================*/
/* Index: Indx_CmdReqExec_ContId                                */
/*==============================================================*/
create index Indx_CmdReqExec_ContId on CommandRequestExec (
CommandRequestExecContextId ASC
)
go

/*==============================================================*/
/* Table: CommandRequestExecResult                              */
/*==============================================================*/
create table CommandRequestExecResult (
   CommandRequestExecResultId numeric              not null,
   CommandRequestExecId numeric              null,
   Command              varchar(255)         not null,
   ErrorCode            numeric              null,
   CompleteTime         datetime             null,
   DeviceId             numeric              null,
   RouteId              numeric              null,
   constraint PK_CommandRequestExecResult primary key (CommandRequestExecResultId)
)
go

/*==============================================================*/
/* Index: Indx_CmdReqExecRes_ExecId_ErrC                        */
/*==============================================================*/
create index Indx_CmdReqExecRes_ExecId_ErrC on CommandRequestExecResult (
CommandRequestExecId ASC,
ErrorCode ASC
)
go

/*==============================================================*/
/* Table: CommandRequestUnsupported                             */
/*==============================================================*/
create table CommandRequestUnsupported (
   CommandRequestUnsupportedId numeric              not null,
   CommandRequestExecId numeric              not null,
   DeviceId             numeric              not null,
   constraint PK_CommandRequestUnsupported primary key nonclustered (CommandRequestUnsupportedId)
)
go

/*==============================================================*/
/* Table: CommandSchedule                                       */
/*==============================================================*/
create table CommandSchedule (
   CommandScheduleId    numeric              not null,
   StartTimeCronString  varchar(64)          not null,
   RunPeriod            varchar(32)          not null,
   DelayPeriod          varchar(32)          not null,
   Enabled              char(1)              not null,
   EnergyCompanyId      numeric              not null,
   constraint PK_CommandSchedule primary key (CommandScheduleId)
)
go

/*==============================================================*/
/* Table: Contact                                               */
/*==============================================================*/
create table Contact (
   ContactID            numeric              not null,
   ContFirstName        varchar(120)         not null,
   ContLastName         varchar(120)         not null,
   LogInID              numeric              not null,
   AddressID            numeric              not null,
   constraint PK_CONTACT primary key (ContactID)
)
go

insert into contact values ( 0, '(none)', '(none)', -9999, 0 );

/*==============================================================*/
/* Index: Indx_ContLstName                                      */
/*==============================================================*/
create index Indx_ContLstName on Contact (
ContLastName ASC
)
go

/*==============================================================*/
/* Index: INDX_CONTID_LNAME                                     */
/*==============================================================*/
create index INDX_CONTID_LNAME on Contact (
ContactID ASC,
ContLastName ASC
)
go

/*==============================================================*/
/* Index: INDX_CONTID_LNAME_FNAME                               */
/*==============================================================*/
create index INDX_CONTID_LNAME_FNAME on Contact (
ContactID ASC,
ContFirstName ASC,
ContLastName ASC
)
go

/*==============================================================*/
/* Table: ContactNotifGroupMap                                  */
/*==============================================================*/
create table ContactNotifGroupMap (
   ContactID            numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null,
   constraint PK_CONTACTNOTIFGROUPMAP primary key (ContactID, NotificationGroupID)
)
go

/*==============================================================*/
/* Table: ContactNotification                                   */
/*==============================================================*/
create table ContactNotification (
   ContactNotifID       numeric              not null,
   ContactID            numeric              not null,
   NotificationCategoryID numeric              not null,
   DisableFlag          char(1)              not null,
   Notification         varchar(130)         not null,
   Ordering             numeric              not null,
   constraint PK_CONTACTNOTIFICATION primary key (ContactNotifID)
)
go

insert into ContactNotification values( 0, 0, 0, 'N', '(none)', 0 );

/*==============================================================*/
/* Index: Indx_CntNotif_CntId                                   */
/*==============================================================*/
create index Indx_CntNotif_CntId on ContactNotification (
ContactID ASC
)
go

/*==============================================================*/
/* Index: Indx_ContNot_Not                                      */
/*==============================================================*/
create index Indx_ContNot_Not on ContactNotification (
Notification ASC
)
go

/*==============================================================*/
/* Table: Customer                                              */
/*==============================================================*/
create table Customer (
   CustomerID           numeric              not null,
   PrimaryContactID     numeric              not null,
   CustomerTypeID       numeric              not null,
   TimeZone             varchar(40)          not null,
   CustomerNumber       varchar(64)          not null,
   RateScheduleID       numeric              not null,
   AltTrackNum          varchar(64)          not null,
   TemperatureUnit      char(1)              not null,
   constraint PK_CUSTOMER primary key (CustomerID)
)
go

INSERT INTO Customer VALUES ( -1, 0, 0, '(none)', '(none)', 0, '(none)', 'F');

/*==============================================================*/
/* Index: INDX_CUSTID_PCONTID                                   */
/*==============================================================*/
create index INDX_CUSTID_PCONTID on Customer (
CustomerID ASC,
PrimaryContactID ASC
)
go

/*==============================================================*/
/* Index: Indx_Cstmr_PcId                                       */
/*==============================================================*/
create index Indx_Cstmr_PcId on Customer (
PrimaryContactID ASC
)
go

/*==============================================================*/
/* Index: Indx_Cust_AltTrackNum                                 */
/*==============================================================*/
create index Indx_Cust_AltTrackNum on Customer (
AltTrackNum ASC
)
go

/*==============================================================*/
/* Table: CustomerAccount                                       */
/*==============================================================*/
create table CustomerAccount (
   AccountID            numeric              not null,
   AccountSiteID        numeric              null,
   AccountNumber        varchar(40)          null,
   CustomerID           numeric              not null,
   BillingAddressID     numeric              null,
   AccountNotes         varchar(200)         null,
   constraint PK_CUSTOMERACCOUNT primary key (AccountID)
)
go

INSERT INTO CustomerAccount VALUES (0,0,'(none)',-1,0,'(none)');

/*==============================================================*/
/* Index: CstAccCstPro_FK                                       */
/*==============================================================*/
create index CstAccCstPro_FK on CustomerAccount (
AccountSiteID ASC
)
go

/*==============================================================*/
/* Index: Indx_CstAcc_CstId                                     */
/*==============================================================*/
create index Indx_CstAcc_CstId on CustomerAccount (
CustomerID ASC
)
go

/*==============================================================*/
/* Index: Indx_acctid_custid                                    */
/*==============================================================*/
create index Indx_acctid_custid on CustomerAccount (
AccountID ASC,
CustomerID ASC
)
go

/*==============================================================*/
/* Index: INDX_CustAcct_AcctNum                                 */
/*==============================================================*/
create index INDX_CustAcct_AcctNum on CustomerAccount (
AccountNumber ASC
)
go

/*==============================================================*/
/* Table: CustomerAdditionalContact                             */
/*==============================================================*/
create table CustomerAdditionalContact (
   CustomerID           numeric              not null,
   ContactID            numeric              not null,
   Ordering             numeric              not null,
   constraint PK_CUSTOMERADDITIONALCONTACT primary key (ContactID, CustomerID)
)
go

/*==============================================================*/
/* Table: CustomerBaseLinePoint                                 */
/*==============================================================*/
create table CustomerBaseLinePoint (
   CustomerID           numeric              not null,
   PointID              numeric              not null,
   constraint PK_CUSTOMERBASELINEPOINT primary key (CustomerID, PointID)
)
go

/*==============================================================*/
/* Table: CustomerLoginSerialGroup                              */
/*==============================================================*/
create table CustomerLoginSerialGroup (
   LoginID              numeric              not null,
   LMGroupID            numeric              not null,
   constraint PK_CUSTOMERLOGINSERIALGROUP primary key (LoginID, LMGroupID)
)
go

/*==============================================================*/
/* Table: CustomerNotifGroupMap                                 */
/*==============================================================*/
create table CustomerNotifGroupMap (
   CustomerID           numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null,
   constraint PK_CUSTOMERNOTIFGROUPMAP primary key (CustomerID, NotificationGroupID)
)
go

/*==============================================================*/
/* Table: CustomerResidence                                     */
/*==============================================================*/
create table CustomerResidence (
   AccountSiteID        numeric              not null,
   ResidenceTypeID      numeric              not null,
   ConstructionMaterialID numeric              not null,
   DecadeBuiltID        numeric              not null,
   SquareFeetID         numeric              not null,
   InsulationDepthID    numeric              not null,
   GeneralConditionID   numeric              not null,
   MainCoolingSystemID  numeric              not null,
   MainHeatingSystemID  numeric              not null,
   NumberOfOccupantsID  numeric              not null,
   OwnershipTypeID      numeric              not null,
   MainFuelTypeID       numeric              not null,
   Notes                varchar(300)         null,
   constraint PK_CUSTOMERRESIDENCE primary key (AccountSiteID)
)
go

/*==============================================================*/
/* Table: DEVICE                                                */
/*==============================================================*/
create table DEVICE (
   DEVICEID             numeric              not null,
   ALARMINHIBIT         varchar(1)           not null,
   CONTROLINHIBIT       varchar(1)           not null,
   constraint PK_DEV_DEVICEID2 primary key (DEVICEID)
)
go

INSERT into device values (0, 'N', 'N');

/*==============================================================*/
/* Table: DEVICECARRIERSETTINGS                                 */
/*==============================================================*/
create table DEVICECARRIERSETTINGS (
   DEVICEID             numeric              not null,
   ADDRESS              numeric              not null,
   constraint PK_DEVICECARRIERSETTINGS primary key (DEVICEID)
)
go

/*==============================================================*/
/* Index: Indx_DevCarSet_Address                                */
/*==============================================================*/
create index Indx_DevCarSet_Address on DEVICECARRIERSETTINGS (
ADDRESS ASC
)
go

/*==============================================================*/
/* Table: DEVICEDIALUPSETTINGS                                  */
/*==============================================================*/
create table DEVICEDIALUPSETTINGS (
   DEVICEID             numeric              not null,
   PHONENUMBER          varchar(40)          not null,
   MINCONNECTTIME       numeric              not null,
   MAXCONNECTTIME       numeric              not null,
   LINESETTINGS         varchar(8)           not null,
   BaudRate             numeric              not null,
   constraint PK_DEVICEDIALUPSETTINGS primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DEVICEEVENT                                           */
/*==============================================================*/
create table DEVICEEVENT (
   DeviceEventID        numeric              not null,
   DeviceID             numeric              not null,
   TimeStamp            datetime             not null,
   DeviceEventComment   varchar(50)          not null,
   constraint PK_DEVICEEVENT primary key (DeviceEventID)
)
go

/*==============================================================*/
/* Table: DEVICEGROUPMEMBER                                     */
/*==============================================================*/
create table DEVICEGROUPMEMBER (
   DeviceGroupID        numeric(18,0)        not null,
   YukonPaoId           numeric(18,0)        not null,
   constraint PK_DEVICEGROUPMEMBER primary key (DeviceGroupID, YukonPaoId)
)
go

/*==============================================================*/
/* Table: DEVICEIDLCREMOTE                                      */
/*==============================================================*/
create table DEVICEIDLCREMOTE (
   DEVICEID             numeric              not null,
   ADDRESS              numeric              not null,
   POSTCOMMWAIT         numeric              not null,
   CCUAmpUseType        varchar(20)          not null,
   constraint PK_DEVICEIDLCREMOTE primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DEVICEIED                                             */
/*==============================================================*/
create table DEVICEIED (
   DEVICEID             numeric              not null,
   PASSWORD             varchar(20)          not null,
   SLAVEADDRESS         varchar(20)          not null,
   constraint PK_DEVICEIED primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DEVICELOADPROFILE                                     */
/*==============================================================*/
create table DEVICELOADPROFILE (
   DEVICEID             numeric              not null,
   LASTINTERVALDEMANDRATE numeric              not null,
   LOADPROFILEDEMANDRATE numeric              not null,
   LOADPROFILECOLLECTION varchar(4)           not null,
   VoltageDmdInterval   numeric              not null,
   VoltageDmdRate       numeric              not null,
   constraint PK_DEVICELOADPROFILE primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DEVICEMCTIEDPORT                                      */
/*==============================================================*/
create table DEVICEMCTIEDPORT (
   DEVICEID             numeric              not null,
   CONNECTEDIED         varchar(20)          not null,
   IEDSCANRATE          numeric              not null,
   DEFAULTDATACLASS     numeric              not null,
   DEFAULTDATAOFFSET    numeric              not null,
   PASSWORD             varchar(6)           not null,
   REALTIMESCAN         varchar(1)           not null,
   constraint PK_DEVICEMCTIEDPORT primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DEVICEMETERGROUP                                      */
/*==============================================================*/
create table DEVICEMETERGROUP (
   DEVICEID             numeric              not null,
   METERNUMBER          varchar(50)          not null,
   constraint PK_DEVICEMETERGROUP primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DEVICEREADJOBLOG                                      */
/*==============================================================*/
create table DEVICEREADJOBLOG (
   DeviceReadJobLogID   numeric              not null,
   ScheduleID           numeric              not null,
   StartTime            datetime             not null,
   StopTime             datetime             not null,
   constraint PK_DEVICEREADJOBLOG primary key (DeviceReadJobLogID)
)
go

/*==============================================================*/
/* Table: DEVICEREADLOG                                         */
/*==============================================================*/
create table DEVICEREADLOG (
   DeviceReadLogID      numeric              not null,
   DeviceID             numeric              not null,
   Timestamp            datetime             not null,
   StatusCode           smallint             not null,
   DeviceReadRequestLogID numeric              not null,
   constraint PK_DEVICEREADLOG primary key (DeviceReadLogID)
)
go

/*==============================================================*/
/* Table: DEVICEREADREQUESTLOG                                  */
/*==============================================================*/
create table DEVICEREADREQUESTLOG (
   DeviceReadRequestLogID numeric              not null,
   RequestID            numeric              not null,
   Command              varchar(128)         not null,
   StartTime            datetime             not null,
   StopTime             datetime             not null,
   DeviceReadJobLogID   numeric              not null,
   constraint PK_DEVICEREADREQUESTLOG primary key (DeviceReadRequestLogID)
)
go

/*==============================================================*/
/* Table: DEVICESCANRATE                                        */
/*==============================================================*/
create table DEVICESCANRATE (
   DEVICEID             numeric              not null,
   SCANTYPE             varchar(20)          not null,
   INTERVALRATE         numeric              not null,
   SCANGROUP            numeric              not null,
   AlternateRate        numeric              not null,
   constraint PK_DEVICESCANRATE primary key (DEVICEID, SCANTYPE)
)
go

/*==============================================================*/
/* Table: DEVICETAPPAGINGSETTINGS                               */
/*==============================================================*/
create table DEVICETAPPAGINGSETTINGS (
   DEVICEID             numeric              not null,
   PAGERNUMBER          varchar(20)          not null,
   Sender               varchar(64)          not null,
   SecurityCode         varchar(64)          not null,
   POSTPath             varchar(64)          not null,
   constraint PK_DEVICETAPPAGINGSETTINGS primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DISPLAY                                               */
/*==============================================================*/
create table DISPLAY (
   DISPLAYNUM           numeric              not null,
   NAME                 varchar(40)          not null,
   TYPE                 varchar(40)          not null,
   TITLE                varchar(30)          null,
   DESCRIPTION          varchar(200)         null,
   constraint SYS_C0013412 primary key (DISPLAYNUM)
)
go

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
)
go

/*==============================================================*/
/* Table: DISPLAY2WAYDATA                                       */
/*==============================================================*/
create table DISPLAY2WAYDATA (
   DISPLAYNUM           numeric              not null,
   ORDERING             numeric              not null,
   POINTID              numeric              not null,
   constraint PK_DISPLAY2WAYDATA primary key (DISPLAYNUM, ORDERING)
)
go

/*==============================================================*/
/* Table: DISPLAYCOLUMNS                                        */
/*==============================================================*/
create table DISPLAYCOLUMNS (
   DISPLAYNUM           numeric              not null,
   TITLE                varchar(50)          not null,
   TYPENUM              numeric              not null,
   ORDERING             numeric              not null,
   WIDTH                numeric              not null,
   constraint PK_DISPLAYCOLUMNS primary key (DISPLAYNUM, TITLE)
)
go

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
create table DYNAMICACCUMULATOR (
   POINTID              numeric              not null,
   PREVIOUSPULSES       numeric              not null,
   PRESENTPULSES        numeric              not null,
   constraint PK_DYNAMICACCUMULATOR primary key (POINTID)
)
go

/*==============================================================*/
/* Table: DYNAMICBILLINGFIELD                                   */
/*==============================================================*/
create table DYNAMICBILLINGFIELD (
   id                   numeric              not null,
   FormatID             numeric              not null,
   FieldName            varchar(50)          not null,
   FieldOrder           numeric              not null,
   FieldFormat          varchar(50)          null,
   MaxLength            numeric              not null,
   PadChar              char(1)              not null,
   PadSide              varchar(5)           not null,
   ReadingType          varchar(12)          not null,
   RoundingMode         varchar(20)          not null,
   Channel              varchar(5)           not null,
   constraint PK_DYNAMICBILLINGFIELD primary key (id)
)
go

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
create table DYNAMICBILLINGFORMAT (
   FormatID             numeric              not null,
   Delimiter            varchar(20)          null,
   Header               varchar(255)         null,
   Footer               varchar(255)         null,
   constraint PK_DYNAMICBILLINGFORMAT primary key (FormatID)
)
go

insert into DynamicBillingFormat values( 31, ',' ,'H Meter kWh Time Date Peak PeakT PeakD', ' ');
insert into DynamicBillingFormat values( 21, ',' ,' ' ,' '); 

/*==============================================================*/
/* Table: DYNAMICCCAREA                                         */
/*==============================================================*/
create table DYNAMICCCAREA (
   AreaID               numeric              not null,
   additionalflags      varchar(20)          not null,
   ControlValue         numeric              not null,
   constraint PK_DYNAMICCCAREA primary key (AreaID)
)
go

/*==============================================================*/
/* Table: DYNAMICCCOPERATIONSTATISTICS                          */
/*==============================================================*/
create table DYNAMICCCOPERATIONSTATISTICS (
   PAObjectID           numeric              not null,
   UserDefOpCount       numeric              not null,
   UserDefConfFail      numeric              not null,
   DailyOpCount         numeric              not null,
   DailyConfFail        numeric              not null,
   WeeklyOpCount        numeric              not null,
   WeeklyConfFail       numeric              not null,
   MonthlyOpCount       numeric              not null,
   MonthlyConfFail      numeric              not null,
   constraint PK_DYNAMICCCOPERATIONSTATISTIC primary key (PAObjectID)
)
go

/*==============================================================*/
/* Table: DYNAMICCCSPECIALAREA                                  */
/*==============================================================*/
create table DYNAMICCCSPECIALAREA (
   AreaID               numeric              not null,
   additionalflags      varchar(20)          not null,
   ControlValue         numeric              not null
)
go

/*==============================================================*/
/* Table: DYNAMICCCSUBSTATION                                   */
/*==============================================================*/
create table DYNAMICCCSUBSTATION (
   SubStationID         numeric              not null,
   AdditionalFlags      varchar(20)          not null,
   SAEnabledID          numeric              not null,
   constraint PK_DYNAMICCCSUBSTATION primary key (SubStationID)
)
go

/*==============================================================*/
/* Table: DYNAMICCCTWOWAYCBC                                    */
/*==============================================================*/
create table DYNAMICCCTWOWAYCBC (
   DeviceID             numeric              not null,
   RecloseBlocked       char(1)              not null,
   ControlMode          char(1)              not null,
   AutoVoltControl      char(1)              not null,
   LastControl          numeric              not null,
   Condition            numeric              not null,
   OpFailedNeutralCurrent char(1)              not null,
   NeutralCurrentFault  char(1)              not null,
   BadRelay             char(1)              not null,
   DailyMaxOps          char(1)              not null,
   VoltageDeltaAbnormal char(1)              not null,
   TempAlarm            char(1)              not null,
   DSTActive            char(1)              not null,
   NeutralLockout       char(1)              not null,
   IgnoredIndicator     char(1)              not null,
   Voltage              float                not null,
   HighVoltage          float                not null,
   LowVoltage           float                not null,
   DeltaVoltage         float                not null,
   AnalogInputOne       numeric              not null,
   Temp                 float                not null,
   RSSI                 numeric              not null,
   IgnoredReason        numeric              not null,
   TotalOpCount         numeric              not null,
   UvOpCount            numeric              not null,
   OvOpCount            numeric              not null,
   OvUvCountResetDate   datetime             not null,
   UvSetPoint           float                not null,
   OvSetPoint           float                not null,
   OvUvTrackTime        numeric              not null,
   LastOvUvDateTime     datetime             not null,
   NeutralCurrentSensor numeric              not null,
   NeutralCurrentAlarmSetPoint float                not null,
   IPAddress            numeric              not null,
   UDPPort              numeric              not null,
   constraint PK_DYNAMICCCTWOWAYCBC primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DYNAMICDEVICESCANDATA                                 */
/*==============================================================*/
create table DYNAMICDEVICESCANDATA (
   DEVICEID             numeric              not null,
   LASTFREEZETIME       datetime             not null,
   PREVFREEZETIME       datetime             not null,
   LASTLPTIME           datetime             not null,
   LASTFREEZENUMBER     numeric              not null,
   PREVFREEZENUMBER     numeric              not null,
   NEXTSCAN0            datetime             not null,
   NEXTSCAN1            datetime             not null,
   NEXTSCAN2            datetime             not null,
   NEXTSCAN3            datetime             not null,
   constraint PK_DYNAMICDEVICESCANDATA primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DYNAMICPOINTDISPATCH                                  */
/*==============================================================*/
create table DYNAMICPOINTDISPATCH (
   POINTID              numeric              not null,
   TIMESTAMP            datetime             not null,
   QUALITY              numeric              not null,
   VALUE                float                not null,
   TAGS                 numeric              not null,
   NEXTARCHIVE          datetime             not null,
   STALECOUNT           numeric              not null,
   LastAlarmLogID       numeric              not null,
   millis               smallint             not null,
   constraint PK_DYNAMICPOINTDISPATCH primary key (POINTID)
)
go

/*==============================================================*/
/* Table: DateOfHoliday                                         */
/*==============================================================*/
create table DateOfHoliday (
   HolidayScheduleID    numeric              not null,
   HolidayName          varchar(20)          not null,
   HolidayMonth         numeric              not null,
   HolidayDay           numeric              not null,
   HolidayYear          numeric              not null,
   constraint PK_DATEOFHOLIDAY primary key (HolidayScheduleID, HolidayName)
)
go

insert into dateOfHoliday values(-1, 'None', 1,1,1969);

/*==============================================================*/
/* Table: DateOfSeason                                          */
/*==============================================================*/
create table DateOfSeason (
   SeasonScheduleID     numeric              not null,
   SeasonName           varchar(20)          not null,
   SeasonStartMonth     numeric              not null,
   SeasonStartDay       numeric              not null,
   SeasonEndMonth       numeric              not null,
   SeasonEndDay         numeric              not null,
   constraint PK_DATEOFSEASON primary key (SeasonScheduleID, SeasonName)
)
go

insert into dateOfSeason values(-1, 'Default', 1,1,12,31);

/*==============================================================*/
/* Table: DeliverySchedule                                      */
/*==============================================================*/
create table DeliverySchedule (
   ScheduleID           numeric              not null,
   PurchasePlanID       numeric              not null,
   ScheduleName         varchar(60)          not null,
   ModelID              numeric              not null,
   StyleNumber          varchar(60)          not null,
   OrderNumber          varchar(60)          not null,
   QuotedPricePerUnit   float                not null,
   constraint PK_DELIVERYSCHEDULE primary key (ScheduleID)
)
go

/*==============================================================*/
/* Table: DeviceAddress                                         */
/*==============================================================*/
create table DeviceAddress (
   DeviceID             numeric              not null,
   MasterAddress        numeric              not null,
   SlaveAddress         numeric              not null,
   PostCommWait         numeric              not null,
   constraint PK_DEVICEADDRESS primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DeviceCBC                                             */
/*==============================================================*/
create table DeviceCBC (
   DEVICEID             numeric              not null,
   SERIALNUMBER         numeric              not null,
   ROUTEID              numeric              not null,
   constraint PK_DEVICECBC primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DeviceConfigCategory                                  */
/*==============================================================*/
create table DeviceConfigCategory (
   DeviceConfigCategoryId numeric              not null,
   CategoryType         varchar(60)          not null,
   Name                 varchar(100)         not null,
   Description          varchar(1024)        null,
   constraint PK_DeviceConfigCategory primary key (DeviceConfigCategoryId)
)
go

INSERT INTO DeviceConfigCategory VALUES (0, 'dnp', 'Default DNP Category', null);

alter table DeviceConfigCategory
   add constraint AK_DeviceConfigCategory_Name unique (Name)
go

/*==============================================================*/
/* Table: DeviceConfigCategoryItem                              */
/*==============================================================*/
create table DeviceConfigCategoryItem (
   DeviceConfigCategoryItemId numeric              not null,
   DeviceConfigCategoryId numeric              null,
   ItemName             varchar(60)          not null,
   ItemValue            varchar(60)          not null,
   constraint PK_DeviceConfigCategoryItem primary key (DeviceConfigCategoryItemId)
)
go

INSERT INTO DeviceConfigCategoryItem VALUES (0, 0, 'internalRetries', 2);
INSERT INTO DeviceConfigCategoryItem VALUES (1, 0, 'omitTimeRequest', 'false');
INSERT INTO DeviceConfigCategoryItem VALUES (2, 0, 'enableDnpTimesyncs', 'false');
INSERT INTO DeviceConfigCategoryItem VALUES (3, 0, 'localTime', 'false');
INSERT INTO DeviceConfigCategoryItem VALUES (4, 0, 'enableUnsolicitedMessages', 'true');

alter table DeviceConfigCategoryItem
   add constraint AK_DevConCatItem_CatIdItemName unique (DeviceConfigCategoryId, ItemName)
go

/*==============================================================*/
/* Table: DeviceConfigCategoryMap                               */
/*==============================================================*/
create table DeviceConfigCategoryMap (
   DeviceConfigurationId numeric              not null,
   DeviceConfigCategoryId numeric              not null,
   constraint PK_DeviceConfigCategoryMap primary key (DeviceConfigurationId, DeviceConfigCategoryId)
)
go

INSERT INTO DeviceConfigCategoryMap VALUES(-1, 0);

/*==============================================================*/
/* Table: DeviceConfigDeviceTypes                               */
/*==============================================================*/
create table DeviceConfigDeviceTypes (
   DeviceConfigDeviceTypeId numeric              not null,
   DeviceConfigurationId numeric              null,
   PaoType              varchar(30)          not null,
   constraint PK_DeviceConfigDeviceTypes primary key (DeviceConfigDeviceTypeId)
)
go

INSERT INTO DeviceConfigDeviceTypes VALUES (0, -1, 'CBC 6510');
INSERT INTO DeviceConfigDeviceTypes VALUES (1, -1, 'CBC 7020');
INSERT INTO DeviceConfigDeviceTypes VALUES (2, -1, 'CBC 7022');
INSERT INTO DeviceConfigDeviceTypes VALUES (3, -1, 'CBC 7023');
INSERT INTO DeviceConfigDeviceTypes VALUES (4, -1, 'CBC 7024');
INSERT INTO DeviceConfigDeviceTypes VALUES (5, -1, 'CBC 8020');
INSERT INTO DeviceConfigDeviceTypes VALUES (6, -1, 'CBC 8024');
INSERT INTO DeviceConfigDeviceTypes VALUES (7, -1, 'CBC DNP');
INSERT INTO DeviceConfigDeviceTypes VALUES (8, -1, 'RTU-DART');
INSERT INTO DeviceConfigDeviceTypes VALUES (9, -1, 'RTU-DNP');

alter table DeviceConfigDeviceTypes
   add constraint AK_DevConDevTypes_CatIdDevType unique (DeviceConfigurationId, PaoType)
go

/*==============================================================*/
/* Table: DeviceConfiguration                                   */
/*==============================================================*/
create table DeviceConfiguration (
   DeviceConfigurationID numeric              not null,
   Name                 varchar(60)          not null,
   Description          varchar(1024)        null,
   constraint PK_DeviceConfiguration primary key (DeviceConfigurationID)
)
go

INSERT INTO DeviceConfiguration VALUES (-1, 'Default DNP Configuration', null);

alter table DeviceConfiguration
   add constraint AK_DeviceConfig_Name unique (Name)
go

/*==============================================================*/
/* Table: DeviceConfigurationDeviceMap                          */
/*==============================================================*/
create table DeviceConfigurationDeviceMap (
   DeviceID             numeric              not null,
   DeviceConfigurationId numeric              not null,
   constraint PK_DeviceConfigDeviceMap primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DeviceCustomerList                                    */
/*==============================================================*/
create table DeviceCustomerList (
   CustomerID           numeric              not null,
   DeviceID             numeric              not null,
   constraint PK_DEVICECUSTOMERLIST primary key (DeviceID, CustomerID)
)
go

/*==============================================================*/
/* Table: DeviceDataMonitor                                     */
/*==============================================================*/
create table DeviceDataMonitor (
   MonitorId            numeric              not null,
   Name                 varchar(255)         not null,
   GroupName            varchar(255)         not null,
   Enabled              char(1)              not null,
   constraint PK_DeviceDataMonitor primary key (MonitorId)
)
go

/*==============================================================*/
/* Index: Indx_DeviceDataMon_Name_UNQ                           */
/*==============================================================*/
create unique index Indx_DeviceDataMon_Name_UNQ on DeviceDataMonitor (
Name ASC
)
go

/*==============================================================*/
/* Table: DeviceDataMonitorProcessor                            */
/*==============================================================*/
create table DeviceDataMonitorProcessor (
   ProcessorId          numeric              not null,
   MonitorId            numeric              not null,
   Attribute            varchar(255)         not null,
   StateGroupId         numeric              not null,
   State                numeric              not null,
   constraint PK_DeviceDataMonitorProcessor primary key (ProcessorId)
)
go

/*==============================================================*/
/* Table: DeviceDirectCommSettings                              */
/*==============================================================*/
create table DeviceDirectCommSettings (
   DEVICEID             numeric              not null,
   PORTID               numeric              not null,
   constraint PK_DEVICEDIRECTCOMMSETTINGS primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DeviceGroup                                           */
/*==============================================================*/
create table DeviceGroup (
   DeviceGroupId        numeric(18,0)        not null,
   GroupName            varchar(255)         not null,
   ParentDeviceGroupId  numeric(18,0)        null,
   Permission           nvarchar(50)         not null,
   Type                 varchar(255)         not null,
   CreatedDate          datetime             not null,
   SystemGroupEnum      varchar(255)         null,
   constraint PK_DeviceGroup primary key (DeviceGroupId)
)
go

INSERT INTO DeviceGroup VALUES (0, ' ', null, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'ROOT');
INSERT INTO DeviceGroup VALUES (1, 'Meters', 0, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'METERS');
INSERT INTO DeviceGroup VALUES (2, 'Billing', 1, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'BILLING');
INSERT INTO DeviceGroup VALUES (3, 'Collection', 1, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'COLLECTION');
INSERT INTO DeviceGroup VALUES (4, 'Alternate', 1, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'ALTERNATE');
INSERT INTO DeviceGroup VALUES (8, 'Flags', 1, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'FLAGS');
INSERT INTO DeviceGroup VALUES (9, 'Inventory', 8, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'INVENTORY');
INSERT INTO DeviceGroup VALUES (10, 'DisconnectedStatus', 8, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'DISCONNECTED_STATUS');
INSERT INTO DeviceGroup VALUES (11, 'UsageMonitoring', 8, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'USAGE_MONITORING');
INSERT INTO DeviceGroup VALUES (12, 'System', 0, 'NOEDIT_NOMOD', 'STATIC', '01-JAN-2013', 'SYSTEM');
INSERT INTO DeviceGroup VALUES (13, 'Routes', 12, 'NOEDIT_NOMOD', 'ROUTE', '01-JAN-2013', 'ROUTES');
INSERT INTO DeviceGroup VALUES (14, 'Device Types', 12, 'NOEDIT_NOMOD', 'DEVICETYPE', '01-JAN-2013', 'DEVICE_TYPES'); 
INSERT INTO DeviceGroup VALUES (15, 'Meters', 12, 'NOEDIT_NOMOD', 'STATIC', '01-JAN-2013', 'SYSTEM_METERS'); 
INSERT INTO DeviceGroup VALUES (16, 'Scanning', 15, 'NOEDIT_NOMOD', 'STATIC', '01-JAN-2013', 'SCANNING'); 
INSERT INTO DeviceGroup VALUES (17, 'Load Profile', 16, 'NOEDIT_NOMOD', 'METERS_SCANNING_LOAD_PROFILE', '01-JAN-2013', 'LOAD_PROFILE'); 
INSERT INTO DeviceGroup VALUES (18, 'Voltage Profile', 16, 'NOEDIT_NOMOD', 'METERS_SCANNING_VOLTAGE_PROFILE', '01-JAN-2013', 'VOLTAGE_PROFILE'); 
INSERT INTO DeviceGroup VALUES (19, 'Integrity', 16, 'NOEDIT_NOMOD', 'METERS_SCANNING_INTEGRITY', '01-JAN-2013', 'INTEGRITY'); 
INSERT INTO DeviceGroup VALUES (20, 'Accumulator', 16, 'NOEDIT_NOMOD', 'METERS_SCANNING_ACCUMULATOR', '01-JAN-2013', 'ACCUMULATOR'); 
INSERT INTO DeviceGroup VALUES (21, 'Temporary', 12, 'HIDDEN', 'STATIC', '01-JAN-2013', 'TEMPORARY'); 
INSERT INTO DeviceGroup VALUES (22, 'Disabled', 15, 'NOEDIT_NOMOD', 'METERS_DISABLED', '01-JAN-2013', 'DISABLED');
INSERT INTO DeviceGroup VALUES (23, 'Disconnect', 15, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'DISCONNECT');
INSERT INTO DeviceGroup VALUES (24, 'Collars', 23, 'NOEDIT_MOD', 'METERS_DISCONNECT_COLLAR', '01-JAN-2013', 'COLLARS');
INSERT INTO DeviceGroup VALUES (25, 'CIS Substation', 1, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'CIS_SUBSTATION');
INSERT INTO DeviceGroup VALUES (26, 'Substations', 12, 'NOEDIT_NOMOD', 'SUBSTATION_TO_ROUTE', '01-JAN-2013', 'SUBSTATIONS');
INSERT INTO DeviceGroup VALUES (27, 'Attributes', 12, 'NOEDIT_NOMOD', 'STATIC', '01-JAN-2013', 'ATTRIBUTES');
INSERT INTO DeviceGroup VALUES (28, 'Supported', 27, 'NOEDIT_NOMOD', 'ATTRIBUTE_DEFINED', '01-JAN-2013', 'SUPPORTED');
INSERT INTO DeviceGroup VALUES (29, 'Existing', 27, 'NOEDIT_NOMOD', 'ATTRIBUTE_EXISTS', '01-JAN-2013', 'EXISTING');
INSERT INTO DeviceGroup VALUES (30, 'Device Configs', 12, 'NOEDIT_NOMOD', 'DEVICECONFIG', '01-JAN-2013', 'DEVICE_CONFIGS');
INSERT INTO DeviceGroup VALUES (31, 'Auto', 12, 'HIDDEN', 'STATIC', '01-JAN-2013', 'AUTO');
INSERT INTO DeviceGroup VALUES (32, 'Monitors', 0, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'MONITORS');
INSERT INTO DeviceGroup VALUES (33, 'Outage', 32, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'OUTAGE');
INSERT INTO DeviceGroup VALUES (34, 'DeviceData', 32, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'DEVICE_DATA');
INSERT INTO DeviceGroup VALUES (35, 'Tamper Flag', 32, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'TAMPER_FLAG');
INSERT INTO DeviceGroup VALUES (36, 'Phase Detect', 15, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'PHASE_DETECT');
INSERT INTO DeviceGroup VALUES (37, 'Last Results', 36, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'LAST_RESULTS');
INSERT INTO DeviceGroup VALUES (38, 'A', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'A');
INSERT INTO DeviceGroup VALUES (39, 'B', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'B');
INSERT INTO DeviceGroup VALUES (40, 'C', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'C');
INSERT INTO DeviceGroup VALUES (41, 'AB', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'AB');
INSERT INTO DeviceGroup VALUES (42, 'AC', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'AC');
INSERT INTO DeviceGroup VALUES (43, 'BC', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'BC');
INSERT INTO DeviceGroup VALUES (44, 'ABC', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'ABC');
INSERT INTO DeviceGroup VALUES (45, 'UNKNOWN', 37, 'NOEDIT_MOD', 'STATIC', '01-JAN-2013', 'UNKNOWN');

alter table DeviceGroup
   add constraint AK_DeviceGroup_ParentDG_GrpNam unique (GroupName, ParentDeviceGroupId)
go

/*==============================================================*/
/* Table: DeviceGroupComposed                                   */
/*==============================================================*/
create table DeviceGroupComposed (
   DeviceGroupComposedId numeric              not null,
   DeviceGroupId        numeric              not null,
   CompositionType      varchar(100)         null,
   constraint PK_DevGroupComp primary key (DeviceGroupComposedId)
)
go

/*==============================================================*/
/* Table: DeviceGroupComposedGroup                              */
/*==============================================================*/
create table DeviceGroupComposedGroup (
   DeviceGroupComposedGroupId numeric              not null,
   DeviceGroupComposedId numeric              not null,
   GroupName            varchar(255)         not null,
   IsNot                char(1)              not null,
   constraint PK_DevGroupCompGroup primary key (DeviceGroupComposedGroupId)
)
go

/*==============================================================*/
/* Table: DeviceMCT400Series                                    */
/*==============================================================*/
create table DeviceMCT400Series (
   DeviceID             numeric              not null,
   DisconnectAddress    numeric              not null,
   TOUScheduleID        numeric              not null,
   constraint PK_DEV400S primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DevicePagingReceiverSettings                          */
/*==============================================================*/
create table DevicePagingReceiverSettings (
   DeviceID             numeric              not null,
   CapCode1             numeric              not null,
   CapCode2             numeric              not null,
   CapCode3             numeric              not null,
   CapCode4             numeric              not null,
   CapCode5             numeric              not null,
   CapCode6             numeric              not null,
   CapCode7             numeric              not null,
   CapCode8             numeric              not null,
   CapCode9             numeric              not null,
   CapCode10            numeric              not null,
   CapCode11            numeric              not null,
   CapCode12            numeric              not null,
   CapCode13            numeric              not null,
   CapCode14            numeric              not null,
   CapCode15            numeric              not null,
   CapCode16            numeric              not null,
   Frequency            float                not null,
   constraint PK_DEVICEPAGINGRECEIVERSETTING primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DeviceRTC                                             */
/*==============================================================*/
create table DeviceRTC (
   DeviceID             numeric              not null,
   RTCAddress           numeric              not null,
   Response             varchar(1)           not null,
   LBTMode              numeric              not null,
   DisableVerifies      varchar(1)           not null,
   constraint PK_DEVICERTC primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DeviceRoutes                                          */
/*==============================================================*/
create table DeviceRoutes (
   DEVICEID             numeric              not null,
   ROUTEID              numeric              not null,
   constraint PK_DEVICEROUTES primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: DeviceSeries5RTU                                      */
/*==============================================================*/
create table DeviceSeries5RTU (
   DeviceID             numeric              not null,
   TickTime             numeric              not null,
   TransmitOffset       numeric              not null,
   SaveHistory          char(1)              not null,
   PowerValueHighLimit  numeric              not null,
   PowerValueLowLimit   numeric              not null,
   PowerValueMultiplier float                not null,
   PowerValueOffset     float                not null,
   StartCode            numeric              not null,
   StopCode             numeric              not null,
   Retries              numeric              not null,
   constraint PK_DEVICESERIES5RTU primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DeviceTNPPSettings                                    */
/*==============================================================*/
create table DeviceTNPPSettings (
   DeviceID             numeric              not null,
   Inertia              numeric              not null,
   DestinationAddress   numeric              not null,
   OriginAddress        numeric              not null,
   IdentifierFormat     char(1)              not null,
   Protocol             varchar(32)          not null,
   DataFormat           char(1)              not null,
   Channel              char(1)              not null,
   Zone                 char(1)              not null,
   FunctionCode         char(1)              not null,
   PagerID              varchar(10)          not null,
   constraint PK_DEVICETNPPSETTINGS primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DeviceTypeCommand                                     */
/*==============================================================*/
create table DeviceTypeCommand (
   DeviceCommandID      numeric              not null,
   CommandID            numeric              not null,
   DeviceType           varchar(32)          not null,
   DisplayOrder         numeric              not null,
   VisibleFlag          char(1)              not null,
   CommandGroupID       numeric              not null,
   constraint PK_DEVICETYPECOMMAND primary key (DeviceCommandID, CommandGroupID)
)
go

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

INSERT INTO DeviceTypeCommand VALUES (-641, -130, 'MCT-410CL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-642, -130, 'MCT-410FL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-643, -130, 'MCT-410GL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-644, -130, 'MCT-410IL', 27, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-646, -130, 'MCT-430A', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-647, -130, 'MCT-430S4', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-648, -130, 'MCT-430SL', 23, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-649, -130, 'MCT-470', 29, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-651, -131, 'MCT-410CL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-652, -131, 'MCT-410FL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-653, -131, 'MCT-410GL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-654, -131, 'MCT-410IL', 28, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-656, -131, 'MCT-430A', 24, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-657, -131, 'MCT-430S4', 24, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-658, -131, 'MCT-430SL', 24, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-659, -131, 'MCT-470', 30, 'N', -1);

INSERT INTO DeviceTypeCommand VALUES (-661, -132, 'MCT-410CL', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-662, -132, 'MCT-410FL', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-663, -132, 'MCT-410GL', 29, 'N', -1);
INSERT INTO DeviceTypeCommand VALUES (-664, -132, 'MCT-410IL', 29, 'N', -1);
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

INSERT INTO DeviceTypeCommand VALUES (-820, -1, 'MCT-420CD', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-821, -81, 'MCT-420CD', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-822, -3, 'MCT-420CD', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-823, -6, 'MCT-420CD', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-824, -34, 'MCT-420CD', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-825, -82, 'MCT-420CD', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-826, -18, 'MCT-420CD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-827, -19, 'MCT-420CD', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-828, -83, 'MCT-420CD', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-829, -84, 'MCT-420CD', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-830, -11, 'MCT-420CD', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-831, -12, 'MCT-420CD', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-832, -13, 'MCT-420CD', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-833, -106, 'MCT-420CD', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-834, -107, 'MCT-420CD', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-835, -108, 'MCT-420CD', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-836, -111, 'MCT-420CD', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-837, -114, 'MCT-420CD', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-838, -15, 'MCT-420CD', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-839, -130, 'MCT-420CD', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-840, -131, 'MCT-420CD', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-841, -132, 'MCT-420CD', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-842, -136, 'MCT-420CD', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-843, -137, 'MCT-420CD', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-844, -138, 'MCT-420CD', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-845, -139, 'MCT-420CD', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-846, -140, 'MCT-420CD', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-847, -141, 'MCT-420CD', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-848, -142, 'MCT-420CD', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-849, -154, 'MCT-420CD', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-850, -155, 'MCT-420CD', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-851, -156, 'MCT-420CD', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-852, -157, 'MCT-420CD', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-853, -169, 'MCT-420CD', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-854, -170, 'MCT-420CD', 35, 'Y', -1); 

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

INSERT INTO DeviceTypeCommand VALUES (-900, -1, 'MCT-420FD', 1, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-901, -81, 'MCT-420FD', 2, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-902, -3, 'MCT-420FD', 3, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-903, -6, 'MCT-420FD', 4, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-904, -34, 'MCT-420FD', 5, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-905, -82, 'MCT-420FD', 6, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-906, -18, 'MCT-420FD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-907, -19, 'MCT-420FD', 8, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-908, -83, 'MCT-420FD', 9, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-909, -84, 'MCT-420FD', 10, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-910, -11, 'MCT-420FD', 11, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-911, -12, 'MCT-420FD', 12, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-912, -13, 'MCT-420FD', 13, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-913, -106, 'MCT-420FD', 14, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-914, -107, 'MCT-420FD', 15, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-915, -108, 'MCT-420FD', 16, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-916, -111, 'MCT-420FD', 17, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-917, -114, 'MCT-420FD', 18, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-918, -15, 'MCT-420FD', 19, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-919, -130, 'MCT-420FD', 20, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-920, -131, 'MCT-420FD', 21, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-921, -132, 'MCT-420FD', 22, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-922, -136, 'MCT-420FD', 23, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-923, -137, 'MCT-420FD', 24, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-924, -138, 'MCT-420FD', 25, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-925, -139, 'MCT-420FD', 26, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-926, -140, 'MCT-420FD', 27, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-927, -141, 'MCT-420FD', 28, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-928, -142, 'MCT-420FD', 29, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-929, -154, 'MCT-420FD', 30, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-930, -155, 'MCT-420FD', 31, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-931, -156, 'MCT-420FD', 32, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-932, -157, 'MCT-420FD', 33, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-933, -169, 'MCT-420FD', 34, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-934, -170, 'MCT-420FD', 35, 'Y', -1); 

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

INSERT INTO DeviceTypeCommand VALUES (-975, -27, 'RFN EXPRESSCOM GROUP', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-976, -28, 'RFN EXPRESSCOM GROUP', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-977, -133, 'RFN EXPRESSCOM GROUP', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-978, -134, 'RFN EXPRESSCOM GROUP', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-979, -135, 'RFN EXPRESSCOM GROUP', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-980, -143, 'RFN EXPRESSCOM GROUP', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-981, -13, 'MCT-440-2131B', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-982, -13, 'MCT-440-2132B', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-983, -13, 'MCT-440-2133B', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-984, -12, 'MCT-440-2131B', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-985, -12, 'MCT-440-2132B', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-986, -12, 'MCT-440-2133B', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-987, -18, 'MCT-440-2131B', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-988, -18, 'MCT-440-2132B', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-989, -18, 'MCT-440-2133B', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-990, -19, 'MCT-440-2131B', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-991, -19, 'MCT-440-2132B', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-992, -19, 'MCT-440-2133B', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-993, -11, 'MCT-440-2131B', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-994, -11, 'MCT-440-2132B', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-995, -11, 'MCT-440-2133B', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-996, -6, 'MCT-440-2131B', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-997, -6, 'MCT-440-2132B', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-998, -6, 'MCT-440-2133B', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-999, -189, 'MCT-440-2131B', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1000, -189, 'MCT-440-2132B', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1001, -189, 'MCT-440-2133B', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1002, -1, 'MCT-440-2131B', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1003, -1, 'MCT-440-2132B', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1004, -1, 'MCT-440-2133B', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1005, -190, 'MCT-440-2131B', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1006, -190, 'MCT-440-2132B', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1007, -190, 'MCT-440-2133B', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1008, -154, 'MCT-440-2131B', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1009, -154, 'MCT-440-2132B', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1010, -154, 'MCT-440-2133B', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1011, -83, 'MCT-440-2131B', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1012, -83, 'MCT-440-2132B', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1013, -83, 'MCT-440-2133B', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1014, -191, 'MCT-440-2131B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1015, -191, 'MCT-440-2132B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1016, -191, 'MCT-440-2133B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1017, -192, 'MCT-440-2131B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1018, -192, 'MCT-440-2132B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1019, -192, 'MCT-440-2133B', 12, 'Y', -1);

/*==============================================================*/
/* Index: Indx_DevTypeCmd_GroupID                               */
/*==============================================================*/
create index Indx_DevTypeCmd_GroupID on DeviceTypeCommand (
CommandGroupID ASC
)
go

/*==============================================================*/
/* Table: DeviceVerification                                    */
/*==============================================================*/
create table DeviceVerification (
   ReceiverID           numeric              not null,
   TransmitterID        numeric              not null,
   ResendOnFail         char(1)              not null,
   Disable              char(1)              not null,
   constraint PK_DEVICEVERIFICATION primary key (ReceiverID, TransmitterID)
)
go

/*==============================================================*/
/* Table: DeviceWindow                                          */
/*==============================================================*/
create table DeviceWindow (
   DeviceID             numeric              not null,
   Type                 varchar(20)          not null,
   WinOpen              numeric              not null,
   WinClose             numeric              not null,
   AlternateOpen        numeric              not null,
   AlternateClose       numeric              not null,
   constraint PK_DEVICEWINDOW primary key (DeviceID, Type)
)
go

/*==============================================================*/
/* Table: DigiGateway                                           */
/*==============================================================*/
create table DigiGateway (
   DeviceId             numeric              not null,
   DigiId               numeric              not null,
   constraint PK_DigiGate primary key (DeviceId)
)
go

/*==============================================================*/
/* Table: DynamicCCCapBank                                      */
/*==============================================================*/
create table DynamicCCCapBank (
   CapBankID            numeric              not null,
   ControlStatus        numeric              not null,
   TotalOperations      numeric              not null,
   LastStatusChangeTime datetime             not null,
   TagsControlStatus    numeric              not null,
   CTITimeStamp         datetime             not null,
   AssumedStartVerificationStatus numeric              not null,
   PrevVerificationControlStatus numeric              not null,
   VerificationControlIndex numeric              not null,
   AdditionalFlags      varchar(32)          not null,
   CurrentDailyOperations numeric              not null,
   TwoWayCBCState       numeric              not null,
   TwoWayCBCStateTime   datetime             not null,
   beforeVar            varchar(48)          not null,
   afterVar             varchar(48)          not null,
   changeVar            varchar(48)          not null,
   twoWayCBCLastControl numeric              not null,
   PartialPhaseInfo     varchar(20)          not null,
   constraint PK_DYNAMICCCCAPBANK primary key (CapBankID)
)
go

/*==============================================================*/
/* Table: DynamicCCFeeder                                       */
/*==============================================================*/
create table DynamicCCFeeder (
   FeederID             numeric              not null,
   CurrentVarPointValue float                not null,
   CurrentWattPointValue float                not null,
   NewPointDataReceivedFlag char(1)              not null,
   LastCurrentVarUpdateTime datetime             not null,
   EstimatedVarPointValue float                not null,
   CurrentDailyOperations numeric              not null,
   RecentlyControlledFlag char(1)              not null,
   LastOperationTime    datetime             not null,
   VarValueBeforeControl float                not null,
   LastCapBankDeviceID  numeric              not null,
   BusOptimizedVarCategory numeric              not null,
   BusOptimizedVarOffset float                not null,
   CTITimeStamp         datetime             not null,
   PowerFactorValue     float                not null,
   KvarSolution         float                not null,
   EstimatedPFValue     float                not null,
   CurrentVarPointQuality numeric              not null,
   WaiveControlFlag     char(1)              not null,
   AdditionalFlags      varchar(32)          not null,
   CurrentVoltPointValue float                not null,
   EventSeq             numeric              not null,
   CurrVerifyCBId       numeric              not null,
   CurrVerifyCBOrigState numeric              not null,
   CurrentWattPointQuality numeric              not null,
   CurrentVoltPointQuality numeric              not null,
   iVControlTot         float                not null,
   iVCount              numeric              not null,
   iWControlTot         float                not null,
   iWCount              numeric              not null,
   phaseavalue          float                not null,
   phasebvalue          float                not null,
   phasecvalue          float                not null,
   LastWattPointTime    datetime             not null,
   LastVoltPointTime    datetime             not null,
   retryIndex           numeric              not null,
   PhaseAValueBeforeControl float                not null,
   PhaseBValueBeforeControl float                not null,
   PhaseCValueBeforeControl float                not null,
   constraint PK_DYNAMICCCFEEDER primary key (FeederID)
)
go

/*==============================================================*/
/* Table: DynamicCCMonitorBankHistory                           */
/*==============================================================*/
create table DynamicCCMonitorBankHistory (
   DeviceId             numeric              not null,
   PointID              numeric              not null,
   Value                float                not null,
   DateTime             datetime             not null,
   ScanInProgress       char(1)              not null,
   constraint PK_DYNAMICCCMONITORBANKHISTORY primary key (DeviceId, PointID)
)
go

/*==============================================================*/
/* Table: DynamicCCMonitorPointResponse                         */
/*==============================================================*/
create table DynamicCCMonitorPointResponse (
   DeviceId             numeric              not null,
   PointID              numeric              not null,
   PreOpValue           float                not null,
   Delta                float                not null,
   StaticDelta          char(1)              not null,
   constraint PK_DYNAMICCCMONITORPOINTRESPON primary key (DeviceId, PointID)
)
go

/*==============================================================*/
/* Table: DynamicCCOriginalParent                               */
/*==============================================================*/
create table DynamicCCOriginalParent (
   PAObjectId           numeric              not null,
   OriginalParentId     numeric              not null,
   OriginalSwitchingOrder float                not null,
   OriginalCloseOrder   float                not null,
   OriginalTripOrder    float                not null,
   constraint PK_DynCCOrigParent primary key nonclustered (PAObjectId)
)
go

/*==============================================================*/
/* Table: DynamicCCSubstationBus                                */
/*==============================================================*/
create table DynamicCCSubstationBus (
   SubstationBusID      numeric              not null,
   CurrentVarPointValue float                not null,
   CurrentWattPointValue float                not null,
   NextCheckTime        datetime             not null,
   NewPointDataReceivedFlag char(1)              not null,
   BusUpdatedFlag       char(1)              not null,
   LastCurrentVarUpdateTime datetime             not null,
   EstimatedVarPointValue float                not null,
   CurrentDailyOperations numeric              not null,
   PeakTimeFlag         char(1)              not null,
   RecentlyControlledFlag char(1)              not null,
   LastOperationTime    datetime             not null,
   VarValueBeforeControl float                not null,
   LastFeederPAOid      numeric              not null,
   LastFeederPosition   numeric              not null,
   CTITimeStamp         datetime             not null,
   PowerFactorValue     float                not null,
   KvarSolution         float                not null,
   EstimatedPFValue     float                not null,
   CurrentVarPointQuality numeric              not null,
   WaiveControlFlag     char(1)              not null,
   AdditionalFlags      varchar(32)          not null,
   CurrVerifyCBId       numeric              not null,
   CurrVerifyFeederId   numeric              not null,
   CurrVerifyCBOrigState numeric              not null,
   VerificationStrategy numeric              not null,
   CbInactivityTime     numeric              not null,
   CurrentVoltPointValue float                not null,
   SwitchPointStatus    char(1)              not null,
   AltSubControlValue   float                not null,
   EventSeq             numeric              not null,
   CurrentWattPointQuality numeric              not null,
   CurrentVoltPointQuality numeric              not null,
   iVControlTot         float                not null,
   iVCount              numeric              not null,
   iWControlTot         float                not null,
   iWCount              numeric              not null,
   phaseavalue          float                not null,
   phasebvalue          float                not null,
   phasecvalue          float                not null,
   LastWattPointTime    datetime             not null,
   LastVoltPointTime    datetime             not null,
   PhaseAValueBeforeControl float                not null,
   PhaseBValueBeforeControl float                not null,
   PhaseCValueBeforeControl float                not null,
   constraint PK_DYNAMICCCSUBSTATIONBUS primary key (SubstationBusID)
)
go

/*==============================================================*/
/* Table: DynamicCalcHistorical                                 */
/*==============================================================*/
create table DynamicCalcHistorical (
   PointID              numeric              not null,
   LastUpdate           datetime             not null,
   constraint PK_DYNAMICCALCHISTORICAL primary key (PointID)
)
go

/*==============================================================*/
/* Table: DynamicImportStatus                                   */
/*==============================================================*/
create table DynamicImportStatus (
   Entry                varchar(64)          not null,
   LastImportTime       varchar(64)          not null,
   NextImportTime       varchar(64)          not null,
   TotalSuccesses       varchar(32)          not null,
   TotalAttempts        varchar(32)          not null,
   ForceImport          char(1)              not null,
   constraint PK_DYNAMICIMPORTSTATUS primary key (Entry)
)
go

insert into DynamicImportStatus values('SYSTEMVALUE', '------', '------', '--', '--', 'N');

/*==============================================================*/
/* Table: DynamicLMControlArea                                  */
/*==============================================================*/
create table DynamicLMControlArea (
   DeviceID             numeric              not null,
   NextCheckTime        datetime             not null,
   NewPointDataReceivedFlag char(1)              not null,
   UpdatedFlag          char(1)              not null,
   ControlAreaState     numeric              not null,
   CurrentPriority      numeric              not null,
   TimeStamp            datetime             not null,
   CurrentDailyStartTime numeric              not null,
   CurrentDailyStopTime numeric              not null,
   constraint PK_DYNAMICLMCONTROLAREA primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DynamicLMControlAreaTrigger                           */
/*==============================================================*/
create table DynamicLMControlAreaTrigger (
   DeviceID             numeric              not null,
   TriggerNumber        numeric              not null,
   PointValue           float                not null,
   LastPointValueTimeStamp datetime             not null,
   PeakPointValue       float                not null,
   LastPeakPointValueTimeStamp datetime             not null,
   TriggerID            numeric              not null,
   constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key (DeviceID, TriggerNumber)
)
go

/*==============================================================*/
/* Table: DynamicLMControlHistory                               */
/*==============================================================*/
create table DynamicLMControlHistory (
   PAObjectID           numeric              not null,
   LMCtrlHistID         numeric              not null,
   StartDateTime        datetime             not null,
   SOE_Tag              numeric              not null,
   ControlDuration      numeric              not null,
   ControlType          varchar(128)         not null,
   CurrentDailyTime     numeric              not null,
   CurrentMonthlyTime   numeric              not null,
   CurrentSeasonalTime  numeric              not null,
   CurrentAnnualTime    numeric              not null,
   ActiveRestore        char(1)              not null,
   ReductionValue       float                not null,
   StopDateTime         datetime             not null,
   ProtocolPriority     numeric(9,0)         not null,
   constraint PK_DYNLMCONTROLHISTORY primary key (PAObjectID)
)
go

/*==============================================================*/
/* Table: DynamicLMGroup                                        */
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
   ControlStartTime     datetime             not null,
   ControlCompleteTime  datetime             not null,
   NextControlTime      datetime             not null,
   InternalState        numeric              not null,
   dailyops             smallint             not null,
   LastStopTimeSent     datetime             not null,
   constraint PK_DYNAMICLMGROUP primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DynamicLMProgram                                      */
/*==============================================================*/
create table DynamicLMProgram (
   DeviceID             numeric              not null,
   ProgramState         numeric              not null,
   ReductionTotal       float                not null,
   StartedControlling   datetime             not null,
   LastControlSent      datetime             not null,
   ManualControlReceivedFlag char(1)              not null,
   TimeStamp            datetime             not null,
   constraint PK_DYNAMICLMPROGRAM primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DynamicLMProgramDirect                                */
/*==============================================================*/
create table DynamicLMProgramDirect (
   DeviceID             numeric              not null,
   CurrentGearNumber    numeric              not null,
   LastGroupControlled  numeric              not null,
   StartTime            datetime             not null,
   StopTime             datetime             not null,
   TimeStamp            datetime             not null,
   NotifyActiveTime     datetime             not null,
   StartedRampingOut    datetime             not null,
   NotifyInactiveTime   datetime             not null,
   ConstraintOverride   char(1)              not null,
   AdditionalInfo       varchar(80)          not null,
   CurrentLogId         numeric              not null default 0,
   constraint PK_DYNAMICLMPROGRAMDIRECT primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: DynamicPAOInfo                                        */
/*==============================================================*/
create table DynamicPAOInfo (
   EntryID              numeric              not null,
   PAObjectID           numeric              not null,
   Owner                varchar(64)          not null,
   InfoKey              varchar(128)         not null,
   Value                varchar(128)         not null,
   UpdateTime           datetime             not null,
   constraint PK_DYNPAOINFO primary key (EntryID)
)
go

alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique (PAObjectID, Owner, InfoKey)
go

/*==============================================================*/
/* Table: DynamicPAOStatistics                                  */
/*==============================================================*/
create table DynamicPAOStatistics (
   DynamicPAOStatisticsId numeric              not null,
   PAObjectId           numeric              not null,
   StatisticType        varchar(16)          not null,
   StartDateTime        datetime             not null,
   Requests             numeric              not null,
   Attempts             numeric              not null,
   Completions          numeric              not null,
   CommErrors           numeric              not null,
   ProtocolErrors       numeric              not null,
   SystemErrors         numeric              not null,
   constraint PK_DynPAOStat primary key (DynamicPAOStatisticsId)
)
go

/*==============================================================*/
/* Index: Indx_DynPAOStat_PId_ST_SD_UNQ                         */
/*==============================================================*/
create unique index Indx_DynPAOStat_PId_ST_SD_UNQ on DynamicPAOStatistics (
PAObjectId ASC,
StatisticType ASC,
StartDateTime ASC
)
go

/*==============================================================*/
/* Table: DynamicPointAlarming                                  */
/*==============================================================*/
create table DynamicPointAlarming (
   PointID              numeric              not null,
   AlarmCondition       numeric              not null,
   CategoryID           numeric              not null,
   AlarmTime            datetime             not null,
   Action               varchar(60)          not null,
   Description          varchar(120)         not null,
   Tags                 numeric              not null,
   LogID                numeric              not null,
   SOE_TAG              numeric              not null,
   Type                 numeric              not null,
   UserName             varchar(64)          not null,
   constraint PK_DYNAMICPOINTALARMING primary key (PointID, AlarmCondition)
)
go

/*==============================================================*/
/* Table: DynamicTags                                           */
/*==============================================================*/
create table DynamicTags (
   InstanceID           numeric              not null,
   PointID              numeric              not null,
   TagID                numeric              not null,
   UserName             varchar(64)          not null,
   Action               varchar(20)          not null,
   Description          varchar(120)         not null,
   TagTime              datetime             not null,
   RefStr               varchar(60)          not null,
   ForStr               varchar(60)          not null,
   constraint PK_DYNAMICTAGS primary key (InstanceID)
)
go

/*==============================================================*/
/* Table: DynamicVerification                                   */
/*==============================================================*/
create table DynamicVerification (
   LogID                numeric              not null,
   TimeArrival          datetime             not null,
   ReceiverID           numeric              not null,
   TransmitterID        numeric              not null,
   Command              varchar(256)         not null,
   Code                 varchar(128)         not null,
   CodeSequence         numeric              not null,
   Received             char(1)              not null,
   CodeStatus           varchar(32)          not null,
   constraint PK_DYNAMICVERIFICATION primary key (LogID)
)
go

/*==============================================================*/
/* Index: Index_DYNVER_CS                                       */
/*==============================================================*/
create index Index_DYNVER_CS on DynamicVerification (
CodeSequence ASC
)
go

/*==============================================================*/
/* Index: Indx_DYNV_TIME                                        */
/*==============================================================*/
create index Indx_DYNV_TIME on DynamicVerification (
TimeArrival ASC
)
go

/*==============================================================*/
/* Index: Indx_DynVer_TimeArr_Code                              */
/*==============================================================*/
create index Indx_DynVer_TimeArr_Code on DynamicVerification (
TimeArrival ASC,
Code ASC
)
go

/*==============================================================*/
/* Table: ECToAccountMapping                                    */
/*==============================================================*/
create table ECToAccountMapping (
   EnergyCompanyID      numeric              not null,
   AccountID            numeric              not null,
   constraint PK_ECTOACCOUNTMAPPING primary key (EnergyCompanyID, AccountID)
)
go

/*==============================================================*/
/* Table: ECToAcctThermostatSchedule                            */
/*==============================================================*/
create table ECToAcctThermostatSchedule (
   EnergyCompanyId      numeric              not null,
   AcctThermostatScheduleId numeric              not null,
   constraint PK_ECToAcctThermSch primary key nonclustered (EnergyCompanyId, AcctThermostatScheduleId)
)
go

/*==============================================================*/
/* Table: ECToCallReportMapping                                 */
/*==============================================================*/
create table ECToCallReportMapping (
   EnergyCompanyID      numeric              not null,
   CallReportID         numeric              not null,
   constraint PK_ECTOCALLREPORTMAPPING primary key (EnergyCompanyID, CallReportID)
)
go

/*==============================================================*/
/* Table: ECToGenericMapping                                    */
/*==============================================================*/
create table ECToGenericMapping (
   EnergyCompanyID      numeric              not null,
   ItemID               numeric              not null,
   MappingCategory      varchar(40)          not null,
   constraint PK_ECTOGENERICMAPPING primary key (EnergyCompanyID, ItemID, MappingCategory)
)
go

/*==============================================================*/
/* Table: ECToInventoryMapping                                  */
/*==============================================================*/
create table ECToInventoryMapping (
   EnergyCompanyID      numeric              not null,
   InventoryID          numeric              not null,
   constraint PK_ECTOINVENTORYMAPPING primary key (EnergyCompanyID, InventoryID)
)
go

/*==============================================================*/
/* Index: INDX_ECToInvMap_InvId                                 */
/*==============================================================*/
create index INDX_ECToInvMap_InvId on ECToInventoryMapping (
InventoryID ASC
)
go

/*==============================================================*/
/* Table: ECToLMCustomerEventMapping                            */
/*==============================================================*/
create table ECToLMCustomerEventMapping (
   EnergyCompanyID      numeric              not null,
   EventID              numeric              not null,
   constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key (EnergyCompanyID, EventID)
)
go

/*==============================================================*/
/* Index: INDX_ECToCustEventMap_EventId                         */
/*==============================================================*/
create index INDX_ECToCustEventMap_EventId on ECToLMCustomerEventMapping (
EventID ASC
)
go

/*==============================================================*/
/* Table: ECToOperatorGroupMapping                              */
/*==============================================================*/
create table ECToOperatorGroupMapping (
   EnergyCompanyId      numeric              not null,
   UserGroupId          numeric              not null,
   constraint PK_ECToOpGroupMap primary key nonclustered (EnergyCompanyId, UserGroupId)
)
go

/*==============================================================*/
/* Table: ECToResidentialGroupMapping                           */
/*==============================================================*/
create table ECToResidentialGroupMapping (
   EnergyCompanyId      numeric              not null,
   UserGroupId          numeric              not null,
   constraint PK_ECToResGroupMap primary key nonclustered (EnergyCompanyId, UserGroupId)
)
go

/*==============================================================*/
/* Table: ECToRouteMapping                                      */
/*==============================================================*/
create table ECToRouteMapping (
   EnergyCompanyId      numeric              not null,
   RouteId              numeric              not null,
   constraint PK_ECToRouteMap primary key nonclustered (EnergyCompanyId, RouteId)
)
go

/*==============================================================*/
/* Table: ECToSubstationMapping                                 */
/*==============================================================*/
create table ECToSubstationMapping (
   EnergyCompanyId      numeric              not null,
   SubstationId         numeric              not null,
   constraint PK_ECToSubMap primary key nonclustered (EnergyCompanyId, SubstationId)
)
go

/*==============================================================*/
/* Table: ECToWorkOrderMapping                                  */
/*==============================================================*/
create table ECToWorkOrderMapping (
   EnergyCompanyID      numeric              not null,
   WorkOrderID          numeric              not null,
   constraint PK_ECTOWORKORDERMAPPING primary key (EnergyCompanyID, WorkOrderID)
)
go

/*==============================================================*/
/* Table: EncryptionKey                                         */
/*==============================================================*/
create table EncryptionKey (
   EncryptionKeyId      numeric              not null,
   Name                 varchar(128)         not null,
   Value                varchar(512)         not null,
   constraint PK_EncryptionKey primary key (EncryptionKeyId)
)
go

/*==============================================================*/
/* Table: EnergyCompany                                         */
/*==============================================================*/
create table EnergyCompany (
   EnergyCompanyID      numeric              not null,
   Name                 varchar(60)          not null,
   PrimaryContactID     numeric              not null,
   UserID               numeric              not null,
   constraint PK_ENERGYCOMPANY primary key (EnergyCompanyID)
)
go

insert into EnergyCompany VALUES (-1,'Default Energy Company',0,-100);

/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique index Indx_EnCmpName on EnergyCompany (
Name ASC
)
go

/*==============================================================*/
/* Table: EnergyCompanyCustomerList                             */
/*==============================================================*/
create table EnergyCompanyCustomerList (
   EnergyCompanyID      numeric              not null,
   CustomerID           numeric              not null,
   constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key (EnergyCompanyID, CustomerID)
)
go

/*==============================================================*/
/* Index: INDX_ECompCustList_CustId                             */
/*==============================================================*/
create index INDX_ECompCustList_CustId on EnergyCompanyCustomerList (
CustomerID ASC
)
go

/*==============================================================*/
/* Table: EnergyCompanyOperatorLoginList                        */
/*==============================================================*/
create table EnergyCompanyOperatorLoginList (
   EnergyCompanyID      numeric              not null,
   OperatorLoginID      numeric              not null,
   constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key (EnergyCompanyID, OperatorLoginID)
)
go

INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-1);

/*==============================================================*/
/* Table: EnergyCompanySetting                                  */
/*==============================================================*/
create table EnergyCompanySetting (
   EnergyCompanySettingId numeric              not null,
   EnergyCompanyId      numeric              not null,
   Name                 varchar(100)         not null,
   Value                varchar(1000)        null,
   Enabled              char(1)              null,
   Comments             varchar(1000)        null,
   LastChangedDate      datetime             null,
   constraint PK_EnergyCompanySetting primary key (EnergyCompanySettingId)
)
go

alter table EnergyCompanySetting
   add constraint AK_ECSetting_ECId_Name unique (EnergyCompanyId, Name)
go

/*==============================================================*/
/* Table: EstimatedLoadFormula                                  */
/*==============================================================*/
create table EstimatedLoadFormula (
   EstimatedLoadFormulaId numeric              not null,
   Name                 varchar(32)          not null,
   FormulaType          varchar(32)          not null,
   CalculationType      varchar(32)          not null,
   FunctionIntercept    float                not null,
   constraint PK_EstimatedLoadFormula primary key (EstimatedLoadFormulaId)
)
go

/*==============================================================*/
/* Table: EstimatedLoadFormulaAssignment                        */
/*==============================================================*/
create table EstimatedLoadFormulaAssignment (
   FormulaAssignmentId  numeric              not null,
   EstimatedLoadFormulaId numeric              not null,
   GearId               numeric              null,
   ApplianceCategoryId  numeric              null,
   constraint PK_EstimatedLoadFormulaAssignm primary key (FormulaAssignmentId)
)
go

/*==============================================================*/
/* Table: EstimatedLoadFunction                                 */
/*==============================================================*/
create table EstimatedLoadFunction (
   EstimatedLoadFunctionId numeric              not null,
   EstimatedLoadFormulaId numeric              not null,
   Name                 varchar(32)          not null,
   InputType            varchar(32)          not null,
   InputMin             varchar(32)          not null,
   InputMax             varchar(32)          not null,
   InputPointId         numeric              null,
   Quadratic            float                not null,
   Linear               float                not null,
   constraint PK_EstimatedLoadFunction primary key (EstimatedLoadFunctionId)
)
go

/*==============================================================*/
/* Table: EstimatedLoadLookupTable                              */
/*==============================================================*/
create table EstimatedLoadLookupTable (
   EstimatedLoadLookupTableId numeric              not null,
   EstimatedLoadFormulaId numeric              not null,
   Name                 varchar(32)          not null,
   InputType            varchar(32)          not null,
   InputMin             varchar(32)          not null,
   InputMax             varchar(32)          not null,
   InputPointId         numeric              null,
   constraint PK_EstimatedLoadLookupTable primary key (EstimatedLoadLookupTableId)
)
go

/*==============================================================*/
/* Table: EstimatedLoadTableEntry                               */
/*==============================================================*/
create table EstimatedLoadTableEntry (
   EstimatedLoadTableEntryId numeric              not null,
   EstimatedLoadLookupTableId numeric              not null,
   EntryKey             varchar(32)          not null,
   EntryValue           float                not null,
   constraint PK_EstimatedLoadTableEntry primary key (EstimatedLoadTableEntryId)
)
go

/*==============================================================*/
/* Table: EsubDisplayIndex                                      */
/*==============================================================*/
create table EsubDisplayIndex (
   SearchKey            varchar(500)         not null,
   DisplayUrl           varchar(500)         not null,
   constraint PK_ESUBDISPLAYINDEX primary key (SearchKey)
)
go

/*==============================================================*/
/* Table: EventAccount                                          */
/*==============================================================*/
create table EventAccount (
   EVENTID              numeric              not null,
   AccountID            numeric              not null,
   constraint PK_EVENTACCOUNT primary key (EVENTID)
)
go

/*==============================================================*/
/* Table: EventBase                                             */
/*==============================================================*/
create table EventBase (
   EventID              numeric              not null,
   UserID               numeric              not null,
   SystemCategoryID     numeric              not null,
   ActionID             numeric              not null,
   EventTimestamp       datetime             not null,
   constraint PK_EVENTBASE primary key (EventID)
)
go

insert into eventbase values (-1, -9999, 0, 0, '01-JAN-1970');

/*==============================================================*/
/* Table: EventInventory                                        */
/*==============================================================*/
create table EventInventory (
   EventID              numeric              not null,
   InventoryID          numeric              not null,
   constraint PK_EVENTINVENTORY primary key (EventID)
)
go

/*==============================================================*/
/* Table: EventLog                                              */
/*==============================================================*/
create table EventLog (
   EventLogId           numeric              not null,
   EventType            varchar(250)         not null,
   EventTime            datetime             null,
   String1              varchar(2000)        null,
   String2              varchar(2000)        null,
   String3              varchar(2000)        null,
   String4              varchar(2000)        null,
   String5              varchar(2000)        null,
   String6              varchar(2000)        null,
   Int7                 numeric              null,
   Int8                 numeric              null,
   Int9                 numeric              null,
   Int10                numeric              null,
   Date11               datetime             null,
   Date12               datetime             null,
   constraint PK_EventLog primary key (EventLogId)
)
go

/*==============================================================*/
/* Index: INDX_EventType                                        */
/*==============================================================*/
create index INDX_EventType on EventLog (
EventType ASC
)
go

/*==============================================================*/
/* Table: EventWorkOrder                                        */
/*==============================================================*/
create table EventWorkOrder (
   EventID              numeric              not null,
   OrderID              numeric              not null,
   constraint PK_EVENTWORKORDER primary key (EventID)
)
go

/*==============================================================*/
/* Table: ExtToYukonMessageIdMapping                            */
/*==============================================================*/
create table ExtToYukonMessageIdMapping (
   ExternalMessageId    numeric              not null,
   YukonMessageId       numeric              not null,
   UserId               numeric              not null,
   MessageEndTime       datetime             not null,
   constraint PK_ExtToYukonMessageIdMapping primary key (ExternalMessageId)
)
go

/*==============================================================*/
/* Table: ExtraPaoPointAssignment                               */
/*==============================================================*/
create table ExtraPaoPointAssignment (
   PAObjectId           numeric              not null,
   PointId              numeric              not null,
   Attribute            varchar(255)         not null,
   constraint PK_ExtraPAOPointAsgmt primary key (PAObjectId, Attribute)
)
go

/*==============================================================*/
/* Table: FDRInterface                                          */
/*==============================================================*/
create table FDRInterface (
   InterfaceId          numeric              not null,
   InterfaceName        varchar(30)          not null,
   PossibleDirections   varchar(100)         not null,
   hasDestination       char(1)              not null,
   constraint PK_FDRINTERFACE primary key (InterfaceId)
)
go

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
INSERT INTO FDRInterface VALUES (29, 'VALMETMULTI', 'Send,Send for control,Receive,Receive for control,Receive for Analog Output', 't' );

/*==============================================================*/
/* Table: FDRInterfaceOption                                    */
/*==============================================================*/
create table FDRInterfaceOption (
   InterfaceID          numeric              not null,
   OptionLabel          varchar(20)          not null,
   Ordering             numeric              not null,
   OptionType           varchar(8)           not null,
   OptionValues         varchar(256)         not null,
   constraint PK_FDRINTERFACEOPTION primary key (InterfaceID, Ordering)
)
go

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
INSERT INTO FDRInterfaceOption VALUES(29, 'Point', 1, 'Text', '(none)' );
INSERT INTO FDRInterfaceOption VALUES(29, 'Port', 2, 'Text', '(none)');

/*==============================================================*/
/* Table: FDRTranslation                                        */
/*==============================================================*/
create table FDRTranslation (
   PointId              numeric              not null,
   DirectionType        varchar(30)          not null,
   InterfaceType        varchar(20)          not null,
   Destination          varchar(20)          not null,
   Translation          varchar(200)         not null,
   constraint PK_FDRTrans primary key (PointId, DirectionType, InterfaceType, Translation)
)
go

/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create index Indx_FdrTransIntTyp on FDRTranslation (
InterfaceType ASC
)
go

/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create index Indx_FdrTrnsIntTypDir on FDRTranslation (
DirectionType ASC,
InterfaceType ASC
)
go

/*==============================================================*/
/* Table: FileExportHistory                                     */
/*==============================================================*/
create table FileExportHistory (
   EntryId              numeric              not null,
   OriginalFileName     varchar(100)         not null,
   FileName             varchar(100)         not null,
   FileExportType       varchar(50)          not null,
   Initiator            varchar(100)         not null,
   ExportDate           datetime             not null,
   ExportPath           varchar(300)         null,
   constraint PK_FileExportHistory primary key (EntryId)
)
go

/*==============================================================*/
/* Table: GRAPHDATASERIES                                       */
/*==============================================================*/
create table GRAPHDATASERIES (
   GRAPHDATASERIESID    numeric              not null,
   GRAPHDEFINITIONID    numeric              not null,
   POINTID              numeric              not null,
   Label                varchar(40)          not null,
   Axis                 char(1)              not null,
   Color                numeric              not null,
   Type                 numeric              not null,
   Multiplier           float                not null,
   Renderer             smallint             not null,
   MoreData             varchar(100)         not null,
   constraint SYS_GrphDserID primary key (GRAPHDATASERIESID)
)
go

/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create index Indx_GrpDSerPtID on GRAPHDATASERIES (
POINTID ASC
)
go

/*==============================================================*/
/* Table: GRAPHDEFINITION                                       */
/*==============================================================*/
create table GRAPHDEFINITION (
   GRAPHDEFINITIONID    numeric              not null,
   NAME                 varchar(40)          not null,
   AutoScaleTimeAxis    char(1)              not null,
   AutoScaleLeftAxis    char(1)              not null,
   AutoScaleRightAxis   char(1)              not null,
   STARTDATE            datetime             not null,
   STOPDATE             datetime             not null,
   LeftMin              float                not null,
   LeftMax              float                not null,
   RightMin             float                not null,
   RightMax             float                not null,
   Type                 char(1)              not null,
   constraint SYS_C0015109 primary key (GRAPHDEFINITIONID)
)
go

alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique (NAME)
go

/*==============================================================*/
/* Table: GenericMacro                                          */
/*==============================================================*/
create table GenericMacro (
   OwnerID              numeric              not null,
   ChildID              numeric              not null,
   ChildOrder           numeric              not null,
   MacroType            varchar(20)          not null,
   constraint PK_GENERICMACRO primary key (OwnerID, ChildOrder, MacroType)
)
go

/*==============================================================*/
/* Table: GlobalSetting                                         */
/*==============================================================*/
create table GlobalSetting (
   GlobalSettingId      numeric              not null,
   Name                 varchar(100)         not null,
   Value                varchar(1000)        null,
   Comments             varchar(1000)        null,
   LastChangedDate      datetime             null,
   constraint PK_GlobalSetting primary key (GlobalSettingId)
)
go

/*==============================================================*/
/* Index: Indx_GlobalSetting_Name_UNQ                           */
/*==============================================================*/
create unique index Indx_GlobalSetting_Name_UNQ on GlobalSetting (
Name ASC
)
go

/*==============================================================*/
/* Table: GraphCustomerList                                     */
/*==============================================================*/
create table GraphCustomerList (
   GraphDefinitionID    numeric              not null,
   CustomerID           numeric              not null,
   CustomerOrder        numeric              not null,
   constraint PK_GRAPHCUSTOMERLIST primary key (GraphDefinitionID, CustomerID)
)
go

/*==============================================================*/
/* Table: GroupPaoPermission                                    */
/*==============================================================*/
create table GroupPaoPermission (
   GroupPaoPermissionID numeric              not null,
   UserGroupId          numeric              not null,
   PaoId                numeric              not null,
   Permission           varchar(50)          not null,
   Allow                varchar(5)           not null,
   constraint PK_GroupPaoPermission primary key (GroupPaoPermissionID)
)
go

/*==============================================================*/
/* Index: Indx_UserGrpId_PaoId_Perm_UNQ                         */
/*==============================================================*/
create unique index Indx_UserGrpId_PaoId_Perm_UNQ on GroupPaoPermission (
UserGroupId ASC,
PaoId ASC,
Permission ASC
)
go

/*==============================================================*/
/* Table: HolidaySchedule                                       */
/*==============================================================*/
create table HolidaySchedule (
   HolidayScheduleID    numeric              not null,
   HolidayScheduleName  varchar(40)          not null,
   constraint PK_HOLIDAYSCHEDULE primary key (HolidayScheduleID)
)
go

insert into HolidaySchedule values( 0, 'Empty Holiday Schedule' );
insert into HolidaySchedule values(-1, 'No Holiday');

/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique index Indx_HolSchName on HolidaySchedule (
HolidayScheduleName ASC
)
go

/*==============================================================*/
/* Table: ImportData                                            */
/*==============================================================*/
create table ImportData (
   Address              varchar(64)          not null,
   Name                 varchar(64)          not null,
   RouteName            varchar(64)          not null,
   MeterNumber          varchar(64)          not null,
   CollectionGrp        varchar(64)          not null,
   AltGrp               varchar(64)          not null,
   TemplateName         varchar(64)          not null,
   BillGrp              varchar(64)          not null,
   SubstationName       varchar(64)          not null,
   constraint PK_IMPORTDATA primary key (Address)
)
go

/*==============================================================*/
/* Table: ImportFail                                            */
/*==============================================================*/
create table ImportFail (
   Address              varchar(64)          not null,
   Name                 varchar(64)          not null,
   RouteName            varchar(64)          not null,
   MeterNumber          varchar(64)          not null,
   CollectionGrp        varchar(64)          not null,
   AltGrp               varchar(64)          not null,
   TemplateName         varchar(64)          not null,
   ErrorMsg             varchar(1024)        null,
   DateTime             datetime             null,
   BillGrp              varchar(64)          not null,
   SubstationName       varchar(64)          not null,
   FailType             varchar(64)          not null,
   constraint PK_IMPORTFAIL primary key (Address)
)
go

/*==============================================================*/
/* Table: ImportPendingComm                                     */
/*==============================================================*/
create table ImportPendingComm (
   DeviceID             numeric              not null,
   Address              varchar(64)          not null,
   Name                 varchar(64)          not null,
   RouteName            varchar(64)          not null,
   MeterNumber          varchar(64)          not null,
   CollectionGrp        varchar(64)          not null,
   AltGrp               varchar(64)          not null,
   TemplateName         varchar(64)          not null,
   BillGrp              varchar(64)          not null,
   SubstationName       varchar(64)          not null,
   constraint PK_IMPORTPENDINGCOMM primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: InventoryBase                                         */
/*==============================================================*/
create table InventoryBase (
   InventoryID          numeric              not null,
   AccountID            numeric              null,
   InstallationCompanyID numeric              null,
   CategoryID           numeric              not null,
   ReceiveDate          datetime             null,
   InstallDate          datetime             null,
   RemoveDate           datetime             null,
   AlternateTrackingNumber varchar(40)          null,
   VoltageID            numeric              null,
   Notes                varchar(500)         null,
   DeviceID             numeric              null,
   DeviceLabel          varchar(60)          null,
   CurrentStateID       numeric              not null,
   constraint PK_INVENTORYBASE primary key (InventoryID)
)
go

INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0,'(none)',0);

/*==============================================================*/
/* Index: CstAccCstHrdB_FK                                      */
/*==============================================================*/
create index CstAccCstHrdB_FK on InventoryBase (
AccountID ASC
)
go

/*==============================================================*/
/* Index: HrdInst_CstHrdBs_FK                                   */
/*==============================================================*/
create index HrdInst_CstHrdBs_FK on InventoryBase (
InstallationCompanyID ASC
)
go

/*==============================================================*/
/* Table: InventoryConfigTask                                   */
/*==============================================================*/
create table InventoryConfigTask (
   InventoryConfigTaskId numeric              not null,
   TaskName             varchar(255)         not null,
   SendInService        char(1)              not null,
   NumberOfItems        numeric              not null,
   NumberOfItemsProcessed numeric              not null,
   EnergyCompanyId      numeric              not null,
   UserId               numeric              not null,
   constraint PK_InvConfTask primary key (InventoryConfigTaskId)
)
go

/*==============================================================*/
/* Index: Indx_InvConfTa_EC_TaskName_UNQ                        */
/*==============================================================*/
create unique index Indx_InvConfTa_EC_TaskName_UNQ on InventoryConfigTask (
TaskName ASC,
EnergyCompanyId ASC
)
go

/*==============================================================*/
/* Table: InventoryConfigTaskItem                               */
/*==============================================================*/
create table InventoryConfigTaskItem (
   InventoryConfigTaskId numeric              not null,
   InventoryId          numeric              not null,
   Status               varchar(16)          not null,
   constraint PK_InvConfTaskItem primary key (InventoryConfigTaskId, InventoryId)
)
go

/*==============================================================*/
/* Table: InventoryToAcctThermostatSch                          */
/*==============================================================*/
create table InventoryToAcctThermostatSch (
   InventoryId          numeric              not null,
   AcctThermostatScheduleId numeric              not null,
   constraint PK_InvToAcctThermSch primary key nonclustered (InventoryId)
)
go

/*==============================================================*/
/* Table: InventoryToWarehouseMapping                           */
/*==============================================================*/
create table InventoryToWarehouseMapping (
   WarehouseID          numeric              not null,
   InventoryID          numeric              not null,
   constraint PK_INVENTORYTOWAREHOUSEMAPPING primary key (WarehouseID, InventoryID)
)
go

/*==============================================================*/
/* Table: Invoice                                               */
/*==============================================================*/
create table Invoice (
   InvoiceID            numeric              not null,
   PurchasePlanID       numeric              not null,
   InvoiceDesignation   varchar(60)          not null,
   DateSubmitted        datetime             not null,
   Authorized           varchar(1)           not null,
   AuthorizedBy         varchar(30)          not null,
   HasPaid              varchar(1)           not null,
   DatePaid             datetime             not null,
   TotalQuantity        numeric              not null,
   AuthorizedNumber     varchar(60)          not null,
   constraint PK_INVOICE primary key (InvoiceID)
)
go

/*==============================================================*/
/* Table: InvoiceShipmentMapping                                */
/*==============================================================*/
create table InvoiceShipmentMapping (
   InvoiceID            numeric              not null,
   ShipmentID           numeric              not null,
   constraint PK_INVOICESHIPMENTMAPPING primary key (InvoiceID, ShipmentID)
)
go

/*==============================================================*/
/* Table: JOB                                                   */
/*==============================================================*/
create table JOB (
   JobID                int                  not null,
   BeanName             varchar(250)         not null,
   Disabled             char(1)              not null,
   UserID               numeric              not null,
   Locale               varchar(10)          not null,
   TimeZone             varchar(40)          not null,
   themeName            varchar(60)          not null,
   constraint PK_JOB primary key (JobID)
)
go

/*==============================================================*/
/* Table: JOBPROPERTY                                           */
/*==============================================================*/
create table JOBPROPERTY (
   JobPropertyID        numeric              not null,
   JobID                int                  not null,
   name                 varchar(100)         not null,
   value                varchar(4000)        not null,
   constraint PK_JOBPROPERTY primary key (JobPropertyID)
)
go

/*==============================================================*/
/* Table: JOBSCHEDULEDONETIME                                   */
/*==============================================================*/
create table JOBSCHEDULEDONETIME (
   JobID                int                  not null,
   StartTime            datetime             not null,
   constraint PK_JOBSCHEDULEDONETIME primary key (JobID)
)
go

/*==============================================================*/
/* Table: JOBSCHEDULEDREPEATING                                 */
/*==============================================================*/
create table JOBSCHEDULEDREPEATING (
   JobID                int                  not null,
   CronString           varchar(100)         not null,
   constraint PK_JobScheduledRepeating primary key (JobID)
)
go

/*==============================================================*/
/* Table: JOBSTATUS                                             */
/*==============================================================*/
create table JOBSTATUS (
   JobStatusID          int                  not null,
   JobID                int                  not null,
   StartTime            datetime             not null,
   StopTime             datetime             null,
   JobState             varchar(50)          null,
   Message              varchar(1000)        null,
   constraint PK_JOBSTATUS primary key (JobStatusID)
)
go

/*==============================================================*/
/* Table: LMBeatThePeakGear                                     */
/*==============================================================*/
create table LMBeatThePeakGear (
   GearId               numeric              not null,
   AlertLevel           varchar(20)          not null,
   constraint PK_LMBeatThePeakGear primary key (GearId)
)
go

/*==============================================================*/
/* Table: LMCONTROLAREAPROGRAM                                  */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM (
   DEVICEID             numeric              not null,
   LMPROGRAMDEVICEID    numeric              not null,
   StartPriority        numeric              not null,
   StopPriority         numeric              not null,
   constraint PK_LMCONTROLAREAPROGRAM primary key (DEVICEID, LMPROGRAMDEVICEID)
)
go

/*==============================================================*/
/* Table: LMConfigurationBase                                   */
/*==============================================================*/
create table LMConfigurationBase (
   ConfigurationID      numeric              not null,
   ColdLoadPickup       varchar(128)         not null,
   TamperDetect         varchar(128)         not null,
   constraint PK_LMCONFIGURATIONBASE primary key (ConfigurationID)
)
go

insert into LMConfigurationBase values (0, '(none)', '(none)');

/*==============================================================*/
/* Table: LMConfigurationExpressCom                             */
/*==============================================================*/
create table LMConfigurationExpressCom (
   ConfigurationID      numeric              not null,
   ServiceProvider      numeric              not null,
   GEO                  numeric              not null,
   Substation           numeric              not null,
   Feeder               numeric              not null,
   Zip                  numeric              not null,
   UserAddress          numeric              not null,
   Program              varchar(80)          not null,
   Splinter             varchar(80)          not null,
   constraint PK_LMCONFIGURATIONEXPRESSCOM primary key (ConfigurationID)
)
go

/*==============================================================*/
/* Table: LMConfigurationSA205                                  */
/*==============================================================*/
create table LMConfigurationSA205 (
   ConfigurationID      numeric              not null,
   Slot1                numeric              not null,
   Slot2                numeric              not null,
   Slot3                numeric              not null,
   Slot4                numeric              not null,
   Slot5                numeric              not null,
   Slot6                numeric              not null,
   constraint PK_LMCONFIGURATIONSA205 primary key (ConfigurationID)
)
go

/*==============================================================*/
/* Table: LMConfigurationSA305                                  */
/*==============================================================*/
create table LMConfigurationSA305 (
   ConfigurationID      numeric              not null,
   Utility              numeric              not null,
   GroupAddress         numeric              not null,
   Division             numeric              not null,
   Substation           numeric              not null,
   RateFamily           numeric              not null,
   RateMember           numeric              not null,
   RateHierarchy        numeric              not null,
   constraint PK_LMCONFIGURATIONSA305 primary key (ConfigurationID)
)
go

/*==============================================================*/
/* Table: LMConfigurationSASimple                               */
/*==============================================================*/
create table LMConfigurationSASimple (
   ConfigurationID      numeric              not null,
   OperationalAddress   numeric              not null,
   constraint PK_LMCONFIGURATIONSASIMPLE primary key (ConfigurationID)
)
go

/*==============================================================*/
/* Table: LMConfigurationVersacom                               */
/*==============================================================*/
create table LMConfigurationVersacom (
   ConfigurationID      numeric              not null,
   UtilityID            numeric              not null,
   Section              numeric              not null,
   ClassAddress         numeric              not null,
   DivisionAddress      numeric              not null,
   constraint PK_LMCONFIGURATIONVERSACOM primary key (ConfigurationID)
)
go

/*==============================================================*/
/* Table: LMControlArea                                         */
/*==============================================================*/
create table LMControlArea (
   DEVICEID             numeric              not null,
   DEFOPERATIONALSTATE  varchar(20)          not null,
   CONTROLINTERVAL      numeric              not null,
   MINRESPONSETIME      numeric              not null,
   DEFDAILYSTARTTIME    numeric              not null,
   DEFDAILYSTOPTIME     numeric              not null,
   REQUIREALLTRIGGERSACTIVEFLAG varchar(1)           not null,
   constraint PK_LMCONTROLAREA primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: LMControlAreaTrigger                                  */
/*==============================================================*/
create table LMControlAreaTrigger (
   DeviceId             numeric              not null,
   TriggerNumber        numeric              not null,
   TriggerType          varchar(20)          not null,
   PointId              numeric              not null,
   NormalState          numeric              not null,
   Threshold            float                not null,
   ProjectionType       varchar(14)          not null,
   ProjectionPoints     numeric              not null,
   ProjectAheadDuration numeric              not null,
   ThresholdKickPercent numeric              not null,
   MinRestoreOffset     float                not null,
   PeakPointId          numeric              not null,
   TriggerId            numeric              not null,
   ThresholdPointId     numeric              not null,
   constraint PK_LMCONTROLAREATRIGGER primary key nonclustered (DeviceId, TriggerNumber)
)
go

/*==============================================================*/
/* Index: INDX_UNQ_LMCNTRTR_TRID                                */
/*==============================================================*/
create unique index INDX_UNQ_LMCNTRTR_TRID on LMControlAreaTrigger (
TriggerId ASC
)
go

/*==============================================================*/
/* Table: LMControlHistory                                      */
/*==============================================================*/
create table LMControlHistory (
   LMCtrlHistID         numeric              not null,
   PAObjectID           numeric              not null,
   StartDateTime        datetime             not null,
   SOE_Tag              numeric              not null,
   ControlDuration      numeric              not null,
   ControlType          varchar(128)         not null,
   CurrentDailyTime     numeric              not null,
   CurrentMonthlyTime   numeric              not null,
   CurrentSeasonalTime  numeric              not null,
   CurrentAnnualTime    numeric              not null,
   ActiveRestore        char(1)              not null,
   ReductionValue       float                not null,
   StopDateTime         datetime             not null,
   ProtocolPriority     numeric(9,0)         not null,
   constraint PK_LMCONTROLHISTORY primary key (LMCtrlHistID)
)
go

/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create index Indx_Start on LMControlHistory (
StartDateTime ASC
)
go

/*==============================================================*/
/* Index: Indx_LMContHist_SOE_Tag                               */
/*==============================================================*/
create index Indx_LMContHist_SOE_Tag on LMControlHistory (
SOE_Tag ASC
)
go

/*==============================================================*/
/* Table: LMControlScenarioProgram                              */
/*==============================================================*/
create table LMControlScenarioProgram (
   ScenarioID           numeric              not null,
   ProgramID            numeric              not null,
   StartOffset          numeric              not null,
   StopOffset           numeric              not null,
   StartGear            numeric              not null,
   constraint PK_LMCONTROLSCENARIOPROGRAM primary key (ScenarioID, ProgramID)
)
go

/*==============================================================*/
/* Table: LMCurtailCustomerActivity                             */
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
   AckLateFlag          char(1)              not null,
   constraint PK_LMCURTAILCUSTOMERACTIVITY primary key (CustomerID, CurtailReferenceID)
)
go

/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create index Index_LMCrtCstActID on LMCurtailCustomerActivity (
CustomerID ASC
)
go

/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
AcknowledgeStatus ASC
)
go

/*==============================================================*/
/* Table: LMCurtailProgramActivity                              */
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
   constraint PK_LMCURTAILPROGRAMACTIVITY primary key (CurtailReferenceID)
)
go

/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
CurtailmentStartTime ASC
)
go

/*==============================================================*/
/* Table: LMCustomerEventBase                                   */
/*==============================================================*/
create table LMCustomerEventBase (
   EventID              numeric              not null,
   EventTypeID          numeric              not null,
   ActionID             numeric              not null,
   EventDateTime        datetime             null,
   Notes                varchar(500)         null,
   AuthorizedBy         varchar(40)          null,
   constraint PK_LMCUSTOMEREVENTBASE primary key (EventID)
)
go

/*==============================================================*/
/* Table: LMDirectCustomerList                                  */
/*==============================================================*/
create table LMDirectCustomerList (
   ProgramID            numeric              not null,
   CustomerID           numeric              not null,
   constraint PK_LMDIRECTCUSTOMERLIST primary key (ProgramID, CustomerID)
)
go

/*==============================================================*/
/* Table: LMDirectNotifGrpList                                  */
/*==============================================================*/
create table LMDirectNotifGrpList (
   ProgramID            numeric              not null,
   NotificationGrpID    numeric              not null,
   constraint PK_LMDIRECTNOTIFGRPLIST primary key (ProgramID, NotificationGrpID)
)
go

/*==============================================================*/
/* Table: LMEnergyExchangeCustomerList                          */
/*==============================================================*/
create table LMEnergyExchangeCustomerList (
   ProgramID            numeric              not null,
   CustomerID           numeric              not null,
   CustomerOrder        numeric              not null,
   constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key (ProgramID, CustomerID)
)
go

/*==============================================================*/
/* Table: LMEnergyExchangeCustomerReply                         */
/*==============================================================*/
create table LMEnergyExchangeCustomerReply (
   CustomerID           numeric              not null,
   OfferID              numeric              not null,
   AcceptStatus         varchar(30)          not null,
   AcceptDateTime       datetime             not null,
   RevisionNumber       numeric              not null,
   IPAddressOfAcceptUser varchar(15)          not null,
   UserIDName           varchar(40)          not null,
   NameOfAcceptPerson   varchar(40)          not null,
   EnergyExchangeNotes  varchar(120)         not null,
   constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key (CustomerID, OfferID, RevisionNumber)
)
go

/*==============================================================*/
/* Table: LMEnergyExchangeHourlyCustomer                        */
/*==============================================================*/
create table LMEnergyExchangeHourlyCustomer (
   CustomerID           numeric              not null,
   OfferID              numeric              not null,
   RevisionNumber       numeric              not null,
   Hour                 numeric              not null,
   AmountCommitted      float                not null,
   constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key (CustomerID, OfferID, RevisionNumber, Hour)
)
go

/*==============================================================*/
/* Table: LMEnergyExchangeHourlyOffer                           */
/*==============================================================*/
create table LMEnergyExchangeHourlyOffer (
   OfferID              numeric              not null,
   RevisionNumber       numeric              not null,
   Hour                 numeric              not null,
   Price                numeric              not null,
   AmountRequested      float                not null,
   constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key (OfferID, RevisionNumber, Hour)
)
go

/*==============================================================*/
/* Table: LMEnergyExchangeOfferRevision                         */
/*==============================================================*/
create table LMEnergyExchangeOfferRevision (
   OfferID              numeric              not null,
   RevisionNumber       numeric              not null,
   ActionDateTime       datetime             not null,
   NotificationDateTime datetime             not null,
   OfferExpirationDateTime datetime             not null,
   AdditionalInfo       varchar(100)         not null,
   constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key (OfferID, RevisionNumber)
)
go

/*==============================================================*/
/* Table: LMEnergyExchangeProgramOffer                          */
/*==============================================================*/
create table LMEnergyExchangeProgramOffer (
   DeviceID             numeric              not null,
   OfferID              numeric              not null,
   RunStatus            varchar(20)          not null,
   OfferDate            datetime             not null,
   constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key (OfferID)
)
go

/*==============================================================*/
/* Table: LMGroup                                               */
/*==============================================================*/
create table LMGroup (
   DeviceID             numeric              not null,
   KWCapacity           float                not null,
   constraint PK_LMGROUP primary key (DeviceID)
)
go

insert into lmgroup values( 0, 0 );

/*==============================================================*/
/* Table: LMGroupEmetcon                                        */
/*==============================================================*/
create table LMGroupEmetcon (
   DEVICEID             numeric              not null,
   GOLDADDRESS          numeric              not null,
   SILVERADDRESS        numeric              not null,
   ADDRESSUSAGE         char(1)              not null,
   RELAYUSAGE           char(1)              not null,
   ROUTEID              numeric              not null,
   constraint PK_LMGROUPEMETCON primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: LMGroupExpressCom                                     */
/*==============================================================*/
create table LMGroupExpressCom (
   LMGroupID            numeric              not null,
   RouteID              numeric              not null,
   SerialNumber         varchar(10)          not null,
   ServiceProviderID    numeric              not null,
   GeoID                numeric              not null,
   SubstationID         numeric              not null,
   FeederID             numeric              not null,
   ZipID                numeric              not null,
   UserID               numeric              not null,
   ProgramID            numeric              not null,
   SplinterID           numeric              not null,
   AddressUsage         varchar(10)          not null,
   RelayUsage           char(15)             not null,
   ProtocolPriority     numeric(9,0)         not null,
   constraint PK_LMGROUPEXPRESSCOM primary key (LMGroupID)
)
go

/*==============================================================*/
/* Table: LMGroupExpressComAddress                              */
/*==============================================================*/
create table LMGroupExpressComAddress (
   AddressID            numeric              not null,
   AddressType          varchar(20)          not null,
   Address              numeric              not null,
   AddressName          varchar(30)          not null,
   constraint PK_LMGROUPEXPRESSCOMADDRESS primary key (AddressID)
)
go

insert into LMGroupExpressComAddress values( 0, '(none)', 0, '(none)' );

/*==============================================================*/
/* Table: LMGroupMCT                                            */
/*==============================================================*/
create table LMGroupMCT (
   DeviceID             numeric              not null,
   MCTAddress           numeric              not null,
   MCTLevel             char(1)              not null,
   RelayUsage           char(7)              not null,
   RouteID              numeric              not null,
   MCTDeviceID          numeric              not null,
   constraint PK_LMGrpMCTPK primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: LMGroupPoint                                          */
/*==============================================================*/
create table LMGroupPoint (
   DEVICEID             numeric              not null,
   DeviceIDUsage        numeric              not null,
   PointIDUsage         numeric              not null,
   StartControlRawState numeric              not null,
   constraint PK_LMGROUPPOINT primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: LMGroupRipple                                         */
/*==============================================================*/
create table LMGroupRipple (
   DeviceID             numeric              not null,
   RouteID              numeric              not null,
   ShedTime             numeric              not null,
   ControlValue         char(50)             not null,
   RestoreValue         char(50)             not null,
   constraint PK_LMGROUPRIPPLE primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: LMGroupSA205105                                       */
/*==============================================================*/
create table LMGroupSA205105 (
   GroupID              numeric              not null,
   RouteID              numeric              not null,
   OperationalAddress   numeric              not null,
   LoadNumber           varchar(64)          not null,
   constraint PK_LMGROUPSA205105 primary key (GroupID)
)
go

/*==============================================================*/
/* Table: LMGroupSA305                                          */
/*==============================================================*/
create table LMGroupSA305 (
   GroupID              numeric              not null,
   RouteID              numeric              not null,
   AddressUsage         varchar(8)           not null,
   UtilityAddress       numeric              not null,
   GroupAddress         numeric              not null,
   DivisionAddress      numeric              not null,
   SubstationAddress    numeric              not null,
   IndividualAddress    varchar(16)          not null,
   RateFamily           numeric              not null,
   RateMember           numeric              not null,
   RateHierarchy        numeric              not null,
   LoadNumber           varchar(8)           not null,
   constraint PK_LMGROUPSA305 primary key (GroupID)
)
go

/*==============================================================*/
/* Table: LMGroupSASimple                                       */
/*==============================================================*/
create table LMGroupSASimple (
   GroupID              numeric              not null,
   RouteID              numeric              not null,
   OperationalAddress   varchar(8)           not null,
   NominalTimeout       numeric              not null,
   MarkIndex            numeric              not null,
   SpaceIndex           numeric              not null,
   constraint PK_LMGROUPSASIMPLE primary key (GroupID)
)
go

/*==============================================================*/
/* Table: LMGroupSep                                            */
/*==============================================================*/
create table LMGroupSep (
   DeviceId             numeric              not null,
   UtilityEnrollmentGroup numeric              not null,
   RampIn               numeric              not null,
   RampOut              numeric              not null,
   constraint PK_LMGroupSep primary key (DeviceId)
)
go

/*==============================================================*/
/* Table: LMGroupSepDeviceClass                                 */
/*==============================================================*/
create table LMGroupSepDeviceClass (
   DeviceId             numeric              not null,
   SepDeviceClass       varchar(40)          not null,
   constraint PK_LMGroupSepDeviceClass primary key (DeviceId, SepDeviceClass)
)
go

/*==============================================================*/
/* Table: LMGroupVersacom                                       */
/*==============================================================*/
create table LMGroupVersacom (
   DEVICEID             numeric              not null,
   ROUTEID              numeric              not null,
   UTILITYADDRESS       numeric              not null,
   SECTIONADDRESS       numeric              not null,
   CLASSADDRESS         numeric              not null,
   DIVISIONADDRESS      numeric              not null,
   ADDRESSUSAGE         char(4)              not null,
   RELAYUSAGE           char(7)              not null,
   SerialAddress        varchar(15)          not null,
   constraint PK_LMGROUPVERSACOM primary key (DEVICEID)
)
go

/*==============================================================*/
/* Table: LMHardwareBase                                        */
/*==============================================================*/
create table LMHardwareBase (
   InventoryID          numeric              not null,
   ManufacturerSerialNumber varchar(30)          null,
   LMHardwareTypeID     numeric              not null,
   RouteID              numeric              not null,
   ConfigurationID      numeric              not null,
   constraint PK_LMHARDWAREBASE primary key (InventoryID)
)
go

/*==============================================================*/
/* Index: Indx_LMHardBase_ManSerNum                             */
/*==============================================================*/
create index Indx_LMHardBase_ManSerNum on LMHardwareBase (
ManufacturerSerialNumber ASC
)
go

/*==============================================================*/
/* Table: LMHardwareConfiguration                               */
/*==============================================================*/
create table LMHardwareConfiguration (
   InventoryID          numeric              not null,
   ApplianceID          numeric              not null,
   AddressingGroupID    numeric              null,
   LoadNumber           numeric              null,
   constraint PK_LMHARDWARECONFIGURATION primary key (InventoryID, ApplianceID)
)
go

/*==============================================================*/
/* Index: LmHrd_LmHrdCfg_FK                                     */
/*==============================================================*/
create index LmHrd_LmHrdCfg_FK on LMHardwareConfiguration (
InventoryID ASC
)
go

/*==============================================================*/
/* Index: CstLdIn_LMHrdCfg_FK                                   */
/*==============================================================*/
create index CstLdIn_LMHrdCfg_FK on LMHardwareConfiguration (
ApplianceID ASC
)
go

/*==============================================================*/
/* Table: LMHardwareControlGroup                                */
/*==============================================================*/
create table LMHardwareControlGroup (
   ControlEntryID       int                  not null,
   InventoryID          int                  not null,
   LMGroupID            int                  not null,
   AccountID            int                  not null,
   GroupEnrollStart     datetime             null,
   GroupEnrollStop      datetime             null,
   OptOutStart          datetime             null,
   OptOutStop           datetime             null,
   Type                 int                  not null,
   Relay                int                  not null,
   UserIDFirstAction    int                  not null,
   UserIDSecondAction   int                  not null,
   ProgramId            int                  not null,
   constraint PK_LMHARDWARECONTROLGROUP primary key (ControlEntryID)
)
go

/*==============================================================*/
/* Index: INDX_LMHardContGroup_AcctId                           */
/*==============================================================*/
create index INDX_LMHardContGroup_AcctId on LMHardwareControlGroup (
AccountID ASC
)
go

/*==============================================================*/
/* Index: INDX_LMHardContGroup_InvId                            */
/*==============================================================*/
create index INDX_LMHardContGroup_InvId on LMHardwareControlGroup (
InventoryID ASC
)
go

/*==============================================================*/
/* Table: LMHardwareEvent                                       */
/*==============================================================*/
create table LMHardwareEvent (
   EventID              numeric              not null,
   InventoryID          numeric              not null,
   constraint PK_LMHARDWAREEVENT primary key (EventID)
)
go

/*==============================================================*/
/* Table: LMHardwareToMeterMapping                              */
/*==============================================================*/
create table LMHardwareToMeterMapping (
   LMHardwareInventoryID numeric              not null,
   MeterInventoryID     numeric              not null,
   constraint PK_LMHARDWARETOMETERMAPPING primary key (LMHardwareInventoryID, MeterInventoryID)
)
go

/*==============================================================*/
/* Table: LMMacsScheduleCustomerList                            */
/*==============================================================*/
create table LMMacsScheduleCustomerList (
   ScheduleID           numeric              not null,
   LMCustomerDeviceID   numeric              not null,
   CustomerOrder        numeric              not null,
   constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key (ScheduleID, LMCustomerDeviceID)
)
go

/*==============================================================*/
/* Table: LMPROGRAM                                             */
/*==============================================================*/
create table LMPROGRAM (
   DeviceID             numeric              not null,
   ControlType          varchar(20)          not null,
   ConstraintID         numeric              not null,
   constraint PK_LMPROGRAM primary key (DeviceID)
)
go

insert into LMProgram values(0, 'Automatic', 0);

/*==============================================================*/
/* Table: LMProgramConstraints                                  */
/*==============================================================*/
create table LMProgramConstraints (
   ConstraintID         numeric              not null,
   ConstraintName       varchar(60)          not null,
   AvailableWeekDays    varchar(8)           not null,
   MaxHoursDaily        numeric              not null,
   MaxHoursMonthly      numeric              not null,
   MaxHoursSeasonal     numeric              not null,
   MaxHoursAnnually     numeric              not null,
   MinActivateTime      numeric              not null,
   MinRestartTime       numeric              not null,
   MaxDailyOps          numeric              not null,
   MaxActivateTime      numeric              not null,
   HolidayScheduleID    numeric              not null,
   SeasonScheduleID     numeric              not null,
   constraint PK_PRGCONSTR primary key (ConstraintID)
)
go

insert into LMProgramConstraints values (0, 'Default Constraint', 'YYYYYYYN', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

/*==============================================================*/
/* Table: LMProgramControlWindow                                */
/*==============================================================*/
create table LMProgramControlWindow (
   DeviceID             numeric              not null,
   WindowNumber         numeric              not null,
   AvailableStartTime   numeric              not null,
   AvailableStopTime    numeric              not null,
   constraint PK_LMPROGRAMCONTROLWINDOW primary key (DeviceID, WindowNumber)
)
go

/*==============================================================*/
/* Table: LMProgramCurtailCustomerList                          */
/*==============================================================*/
create table LMProgramCurtailCustomerList (
   ProgramID            numeric              not null,
   CustomerID           numeric              not null,
   CustomerOrder        numeric              not null,
   RequireAck           char(1)              not null,
   constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key (CustomerID, ProgramID)
)
go

/*==============================================================*/
/* Table: LMProgramCurtailment                                  */
/*==============================================================*/
create table LMProgramCurtailment (
   DeviceID             numeric              not null,
   MinNotifyTime        numeric              not null,
   Heading              varchar(40)          not null,
   MessageHeader        varchar(160)         not null,
   MessageFooter        varchar(160)         not null,
   AckTimeLimit         numeric              not null,
   CanceledMsg          varchar(80)          not null,
   StoppedEarlyMsg      varchar(80)          not null,
   constraint PK_LMPROGRAMCURTAILMENT primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: LMProgramDirect                                       */
/*==============================================================*/
create table LMProgramDirect (
   DeviceID             numeric              not null,
   NotifyActiveOffset   numeric              not null,
   Heading              varchar(40)          not null,
   MessageHeader        varchar(160)         not null,
   MessageFooter        varchar(160)         not null,
   TriggerOffset        float                not null,
   RestoreOffset        float                not null,
   NotifyInactiveOffset numeric              not null,
   NotifyAdjust         numeric              not null,
   constraint PK_LMPROGRAMDIRECT primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: LMProgramDirectGear                                   */
/*==============================================================*/
create table LMProgramDirectGear (
   DeviceID             numeric              not null,
   GearName             varchar(30)          not null,
   GearNumber           numeric              not null,
   ControlMethod        varchar(30)          not null,
   MethodRate           numeric              not null,
   MethodPeriod         numeric              not null,
   MethodRateCount      numeric              not null,
   CycleRefreshRate     numeric              not null,
   MethodStopType       varchar(20)          not null,
   ChangeCondition      varchar(24)          not null,
   ChangeDuration       numeric              not null,
   ChangePriority       numeric              not null,
   ChangeTriggerNumber  numeric              not null,
   ChangeTriggerOffset  float                not null,
   PercentReduction     numeric              not null,
   GroupSelectionMethod varchar(30)          not null,
   MethodOptionType     varchar(30)          not null,
   MethodOptionMax      numeric              not null,
   GearID               numeric              not null,
   RampInInterval       numeric              not null,
   RampInPercent        numeric              not null,
   RampOutInterval      numeric              not null,
   RampOutPercent       numeric              not null,
   FrontRampOption      varchar(80)          not null,
   FrontRampTime        numeric              not null,
   BackRampOption       varchar(80)          not null,
   BackRampTime         numeric              not null,
   KWReduction          float                not null,
   constraint PK_LMPROGRAMDIRECTGEAR primary key (GearID)
)
go

alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique (DeviceID, GearNumber)
go

/*==============================================================*/
/* Table: LMProgramDirectGroup                                  */
/*==============================================================*/
create table LMProgramDirectGroup (
   DeviceID             numeric              not null,
   LMGroupDeviceID      numeric              not null,
   GroupOrder           numeric              not null,
   constraint PK_LMPROGRAMDIRECTGROUP primary key (DeviceID, GroupOrder)
)
go

/*==============================================================*/
/* Index: Indx_LMPDG_DevId_LMGrpDev_UNQ                         */
/*==============================================================*/
create unique index Indx_LMPDG_DevId_LMGrpDev_UNQ on LMProgramDirectGroup (
DeviceID ASC,
LMGroupDeviceID ASC
)
go

/*==============================================================*/
/* Table: LMProgramEnergyExchange                               */
/*==============================================================*/
create table LMProgramEnergyExchange (
   DeviceID             numeric              not null,
   MinNotifyTime        numeric              not null,
   Heading              varchar(40)          not null,
   MessageHeader        varchar(160)         not null,
   MessageFooter        varchar(160)         not null,
   CanceledMsg          varchar(80)          not null,
   StoppedEarlyMsg      varchar(80)          not null,
   constraint PK_LMPROGRAMENERGYEXCHANGE primary key (DeviceID)
)
go

/*==============================================================*/
/* Table: LMProgramEvent                                        */
/*==============================================================*/
create table LMProgramEvent (
   EventID              numeric              not null,
   AccountID            numeric              not null,
   ProgramID            numeric              null,
   constraint PK_LMPROGRAMEVENT primary key (EventID)
)
go

/*==============================================================*/
/* Index: INDX_LMProgEvent_AcctId_ProgId                        */
/*==============================================================*/
create index INDX_LMProgEvent_AcctId_ProgId on LMProgramEvent (
AccountID ASC,
ProgramID ASC
)
go

/*==============================================================*/
/* Table: LMProgramGearHistory                                  */
/*==============================================================*/
create table LMProgramGearHistory (
   LMProgramGearHistoryId numeric              not null,
   LMProgramHistoryId   numeric              not null,
   EventTime            datetime             not null,
   Action               varchar(50)          not null,
   UserName             varchar(64)          not null,
   GearName             varchar(30)          not null,
   GearId               numeric              not null,
   Reason               varchar(50)          not null,
   constraint PK_LMPROGRAMGEARHISTORY primary key nonclustered (LMProgramGearHistoryId)
)
go

/*==============================================================*/
/* Table: LMProgramHistory                                      */
/*==============================================================*/
create table LMProgramHistory (
   LMProgramHistoryId   numeric              not null,
   ProgramName          varchar(60)          not null,
   ProgramId            numeric              not null,
   constraint PK_LMPROGRAMHISTORY primary key nonclustered (LMProgramHistoryId)
)
go

/*==============================================================*/
/* Table: LMProgramWebPublishing                                */
/*==============================================================*/
create table LMProgramWebPublishing (
   ApplianceCategoryID  numeric              not null,
   DeviceID             numeric              not null,
   WebsettingsID        numeric              null,
   ChanceOfControlID    numeric              null,
   ProgramOrder         numeric              null,
   ProgramID            numeric              not null,
   constraint PK_LMPROGRAMWEBPUBLISHING primary key (ProgramID)
)
go

insert into LMProgramWebPublishing values (0,0,0,0,0,0);

/*==============================================================*/
/* Table: LMThermoStatGear                                      */
/*==============================================================*/
create table LMThermoStatGear (
   GearID               numeric              not null,
   Settings             varchar(10)          not null,
   MinValue             numeric              not null,
   MaxValue             numeric              not null,
   ValueB               numeric              not null,
   ValueD               numeric              not null,
   ValueF               numeric              not null,
   Random               numeric              not null,
   ValueTa              numeric              not null,
   ValueTb              numeric              not null,
   ValueTc              numeric              not null,
   ValueTd              numeric              not null,
   ValueTe              numeric              not null,
   ValueTf              numeric              not null,
   RampRate             float                not null,
   constraint PK_LMTHERMOSTATGEAR primary key (GearID)
)
go

/*==============================================================*/
/* Table: LMThermostatManualEvent                               */
/*==============================================================*/
create table LMThermostatManualEvent (
   EventID              numeric              not null,
   InventoryID          numeric              not null,
   HoldTemperature      varchar(1)           null,
   OperationStateID     numeric              null,
   FanOperationID       numeric              null,
   PreviousCoolTemperature float                null,
   PreviousHeatTemperature float                null,
   constraint PK_LMTHERMOSTATMANUALEVENT primary key (EventID)
)
go

/*==============================================================*/
/* Table: LOGIC                                                 */
/*==============================================================*/
create table LOGIC (
   LOGICID              numeric              not null,
   LOGICNAME            varchar(20)          not null,
   PERIODICRATE         numeric              not null,
   STATEFLAG            varchar(10)          not null,
   SCRIPTNAME           varchar(20)          not null,
   constraint SYS_C0013445 primary key (LOGICID)
)
go

/*==============================================================*/
/* Table: MACROROUTE                                            */
/*==============================================================*/
create table MACROROUTE (
   ROUTEID              numeric              not null,
   SINGLEROUTEID        numeric              not null,
   ROUTEORDER           numeric              not null,
   constraint PK_MACROROUTE primary key (ROUTEID, ROUTEORDER)
)
go

/*==============================================================*/
/* Table: MACSchedule                                           */
/*==============================================================*/
create table MACSchedule (
   ScheduleId           numeric              not null,
   CategoryName         varchar(50)          not null,
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
   Template             numeric              null,
   constraint PK_MACSCHEDULE primary key (ScheduleId)
)
go

/*==============================================================*/
/* Table: MACSimpleSchedule                                     */
/*==============================================================*/
create table MACSimpleSchedule (
   ScheduleID           numeric              not null,
   TargetPAObjectId     numeric              not null,
   StartCommand         varchar(120)         null,
   StopCommand          varchar(120)         null,
   RepeatInterval       numeric              null,
   constraint PK_MACSIMPLESCHEDULE primary key (ScheduleID)
)
go

/*==============================================================*/
/* Table: MCTBroadCastMapping                                   */
/*==============================================================*/
create table MCTBroadCastMapping (
   MCTBroadCastID       numeric              not null,
   MctID                numeric              not null,
   Ordering             numeric              not null,
   constraint PK_MCTBROADCASTMAPPING primary key (MCTBroadCastID, MctID)
)
go

/*==============================================================*/
/* Table: MCTConfig                                             */
/*==============================================================*/
create table MCTConfig (
   ConfigID             numeric              not null,
   ConfigName           varchar(30)          not null,
   ConfigType           numeric              not null,
   ConfigMode           varchar(30)          not null,
   MCTWire1             numeric              not null,
   Ke1                  float                not null,
   MCTWire2             numeric              not null,
   Ke2                  float                not null,
   MCTWire3             numeric              not null,
   Ke3                  float                not null,
   DisplayDigits        numeric              not null,
   constraint PK_MCTCONFIG primary key (ConfigID)
)
go

/*==============================================================*/
/* Table: MCTConfigMapping                                      */
/*==============================================================*/
create table MCTConfigMapping (
   MctID                numeric              not null,
   ConfigID             numeric              not null,
   constraint PK_MCTCONFIGMAPPING primary key (MctID, ConfigID)
)
go

/*==============================================================*/
/* Table: MSPInterface                                          */
/*==============================================================*/
create table MSPInterface (
   VendorID             numeric              not null,
   Interface            varchar(12)          not null,
   Endpoint             varchar(32)          not null,
   constraint PK_MSPINTERFACE primary key (VendorID, Interface, Endpoint)
)
go

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'MR_ServerSoap');
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'OD_ServerSoap');
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'CD_ServerSoap');

/*==============================================================*/
/* Table: MSPVendor                                             */
/*==============================================================*/
create table MSPVendor (
   VendorID             numeric              not null,
   CompanyName          varchar(64)          not null,
   UserName             varchar(64)          not null,
   Password             varchar(64)          not null,
   URL                  varchar(120)         not null,
   AppName              varchar(64)          not null,
   OutUserName          varchar(64)          not null,
   OutPassword          varchar(64)          not null,
   MaxReturnRecords     int                  not null,
   RequestMessageTimeout int                  not null,
   MaxInitiateRequestObjects int                  not null,
   TemplateNameDefault  varchar(50)          not null,
   constraint PK_MSPVENDOR primary key (VendorID)
)
go

insert into MSPVendor values (1, 'Cannon', '(none)', '(none)', 'http://127.0.0.1:8080/soap/', 'Yukon', ' ', ' ',10000,120000,15,'*Default Template');

/*==============================================================*/
/* Index: INDEX_1                                               */
/*==============================================================*/
create unique index INDEX_1 on MSPVendor (
CompanyName ASC,
AppName ASC
)
go

/*==============================================================*/
/* Table: MeterHardwareBase                                     */
/*==============================================================*/
create table MeterHardwareBase (
   InventoryID          numeric              not null,
   MeterNumber          varchar(30)          not null,
   MeterTypeID          numeric              not null,
   constraint PK_METERHARDWAREBASE primary key (InventoryID)
)
go

/*==============================================================*/
/* Table: MspLMInterfaceMapping                                 */
/*==============================================================*/
create table MspLMInterfaceMapping (
   MspLMInterfaceMappingId numeric              not null,
   StrategyName         varchar(100)         not null,
   SubstationName       varchar(100)         not null,
   PAObjectId           numeric              not null,
   constraint PK_MSPLMINTERFACEMAPPING primary key (MspLMInterfaceMappingId)
)
go

/*==============================================================*/
/* Index: INDX_StratName_SubName_UNQ                            */
/*==============================================================*/
create unique index INDX_StratName_SubName_UNQ on MspLMInterfaceMapping (
StrategyName ASC,
SubstationName ASC
)
go

/*==============================================================*/
/* Table: NotificationDestination                               */
/*==============================================================*/
create table NotificationDestination (
   NotificationGroupID  numeric              not null,
   RecipientID          numeric              not null,
   Attribs              char(16)             not null,
   constraint PKey_NotDestID primary key (NotificationGroupID, RecipientID)
)
go

/*==============================================================*/
/* Table: NotificationGroup                                     */
/*==============================================================*/
create table NotificationGroup (
   NotificationGroupID  numeric              not null,
   GroupName            varchar(40)          not null,
   DisableFlag          char(1)              not null,
   constraint PK_NOTIFICATIONGROUP primary key (NotificationGroupID)
)
go

insert into notificationgroup values( 1, '(none)', 'N' );

/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique index Indx_NOTIFGRPNme on NotificationGroup (
GroupName ASC
)
go

/*==============================================================*/
/* Table: OpenAdrEvents                                         */
/*==============================================================*/
create table OpenAdrEvents (
   EventId              varchar(64)          not null,
   EventXml             xml                  not null,
   StartOffset          numeric              not null,
   EndDate              datetime             not null,
   RequestId            varchar(64)          not null,
   constraint PK_OpenAdrEvents primary key (EventId)
)
go

/*==============================================================*/
/* Table: OperatorLoginGraphList                                */
/*==============================================================*/
create table OperatorLoginGraphList (
   OperatorLoginID      numeric              not null,
   GraphDefinitionID    numeric              not null,
   constraint PK_OPERATORLOGINGRAPHLIST primary key (OperatorLoginID, GraphDefinitionID)
)
go

/*==============================================================*/
/* Table: OptOutAdditional                                      */
/*==============================================================*/
create table OptOutAdditional (
   InventoryId          numeric              not null,
   CustomerAccountId    numeric              not null,
   ExtraOptOutCount     numeric              not null,
   constraint PK_OPTOUTADDITIONAL primary key (InventoryId, CustomerAccountId)
)
go

/*==============================================================*/
/* Table: OptOutEvent                                           */
/*==============================================================*/
create table OptOutEvent (
   OptOutEventId        numeric              not null,
   InventoryId          numeric              not null,
   CustomerAccountId    numeric              null,
   ScheduledDate        datetime             not null,
   StartDate            datetime             not null,
   StopDate             datetime             not null,
   EventCounts          varchar(25)          not null,
   EventState           varchar(25)          not null,
   constraint PK_OPTOUTEVENT primary key (OptOutEventId)
)
go

/*==============================================================*/
/* Table: OptOutEventLog                                        */
/*==============================================================*/
create table OptOutEventLog (
   OptOutEventLogId     numeric              not null,
   InventoryId          numeric              not null,
   CustomerAccountId    numeric              not null,
   EventAction          varchar(25)          not null,
   LogDate              datetime             not null,
   EventStartDate       datetime             null,
   EventStopDate        datetime             not null,
   LogUserId            numeric              not null,
   OptOutEventId        numeric              not null,
   EventCounts          varchar(25)          null,
   constraint PK_OPTOUTEVENTLOG primary key (OptOutEventLogId)
)
go

/*==============================================================*/
/* Table: OptOutSurvey                                          */
/*==============================================================*/
create table OptOutSurvey (
   OptOutSurveyId       numeric              not null,
   SurveyId             numeric              not null,
   StartDate            datetime             not null,
   StopDate             datetime             null,
   constraint PK_OptOutSurvey primary key (OptOutSurveyId)
)
go

/*==============================================================*/
/* Table: OptOutSurveyProgram                                   */
/*==============================================================*/
create table OptOutSurveyProgram (
   OptOutSurveyId       numeric              not null,
   DeviceId             numeric              not null,
   constraint PK_OptOutSurvProg primary key (OptOutSurveyId, DeviceId)
)
go

/*==============================================================*/
/* Table: OptOutSurveyResult                                    */
/*==============================================================*/
create table OptOutSurveyResult (
   SurveyResultId       numeric              not null,
   OptOutEventLogId     numeric              not null,
   constraint PK_OptOutSurvRes primary key (SurveyResultId, OptOutEventLogId)
)
go

/*==============================================================*/
/* Table: OptOutTemporaryOverride                               */
/*==============================================================*/
create table OptOutTemporaryOverride (
   OptOutTemporaryOverrideId numeric              not null,
   UserId               numeric              not null,
   EnergyCompanyId      numeric              not null,
   OptOutType           varchar(25)          not null,
   StartDate            datetime             not null,
   StopDate             datetime             not null,
   OptOutValue          varchar(25)          not null,
   ProgramId            numeric              null,
   constraint PK_OPTOUTTEMPORARYOVERRIDE primary key (OptOutTemporaryOverrideId)
)
go

/*==============================================================*/
/* Table: OutageMonitor                                         */
/*==============================================================*/
create table OutageMonitor (
   OutageMonitorId      numeric              not null,
   OutageMonitorName    varchar(255)         not null,
   GroupName            varchar(255)         not null,
   TimePeriod           numeric              not null,
   NumberOfOutages      numeric              not null,
   EvaluatorStatus      varchar(255)         not null,
   constraint PK_OutageMonitor primary key (OutageMonitorId)
)
go

/*==============================================================*/
/* Index: INDX_OutMonName_UNQ                                   */
/*==============================================================*/
create unique index INDX_OutMonName_UNQ on OutageMonitor (
OutageMonitorName ASC
)
go

/*==============================================================*/
/* Table: PAOExclusion                                          */
/*==============================================================*/
create table PAOExclusion (
   ExclusionID          numeric              not null,
   PaoID                numeric              not null,
   ExcludedPaoID        numeric              not null,
   PointID              numeric              not null,
   Value                numeric              not null,
   FunctionID           numeric              not null,
   FuncName             varchar(100)         not null,
   FuncRequeue          numeric              not null,
   FuncParams           varchar(200)         not null,
   constraint PK_PAOEXCLUSION primary key (ExclusionID)
)
go

/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique index Indx_PAOExclus on PAOExclusion (
PaoID ASC,
ExcludedPaoID ASC
)
go

/*==============================================================*/
/* Table: PAOProperty                                           */
/*==============================================================*/
create table PAOProperty (
   PAObjectId           numeric              not null,
   PropertyName         varchar(50)          not null,
   PropertyValue        varchar(100)         not null,
   constraint PK_PAOProperty primary key (PAObjectId, PropertyName)
)
go

/*==============================================================*/
/* Table: PAOSchedule                                           */
/*==============================================================*/
create table PAOSchedule (
   ScheduleID           numeric              not null,
   NextRunTime          datetime             not null,
   LastRunTime          datetime             not null,
   IntervalRate         numeric              not null,
   ScheduleName         varchar(64)          not null,
   Disabled             char(1)              not null,
   constraint PK_PAOSCHEDULE primary key (ScheduleID)
)
go

/*==============================================================*/
/* Index: Indx_SchedName                                        */
/*==============================================================*/
create unique index Indx_SchedName on PAOSchedule (
ScheduleName ASC
)
go

/*==============================================================*/
/* Table: PAOScheduleAssignment                                 */
/*==============================================================*/
create table PAOScheduleAssignment (
   EventID              numeric              not null,
   ScheduleID           numeric              not null,
   PaoID                numeric              not null,
   Command              varchar(128)         not null,
   DisableOvUv          varchar(1)           not null,
   constraint PK_PAOSCHEDULEASSIGNMENT primary key (EventID)
)
go

/*==============================================================*/
/* Index: INDX_SchId_PAOId_Com_UNQ                              */
/*==============================================================*/
create unique index INDX_SchId_PAOId_Com_UNQ on PAOScheduleAssignment (
ScheduleID ASC,
PaoID ASC,
Command ASC
)
go

/*==============================================================*/
/* Table: PAOowner                                              */
/*==============================================================*/
create table PAOowner (
   OwnerID              numeric              not null,
   ChildID              numeric              not null,
   constraint PK_PAOOWNER primary key (OwnerID, ChildID)
)
go

/*==============================================================*/
/* Table: PEAKREPORT                                            */
/*==============================================================*/
create table PEAKREPORT (
   resultID             int                  not null,
   deviceID             numeric              not null,
   channel              int                  not null,
   peakType             varchar(50)          not null,
   runType              varchar(50)          not null,
   runDate              datetime             not null,
   resultString         varchar(1500)        not null,
   constraint PK_PEAKREPORT primary key (resultID)
)
go

/*==============================================================*/
/* Table: POINT                                                 */
/*==============================================================*/
create table POINT (
   POINTID              numeric              not null,
   POINTTYPE            varchar(20)          not null,
   POINTNAME            varchar(60)          not null,
   PAObjectID           numeric              not null,
   LOGICALGROUP         varchar(14)          not null,
   STATEGROUPID         numeric              not null,
   SERVICEFLAG          varchar(1)           not null,
   ALARMINHIBIT         varchar(1)           not null,
   PSEUDOFLAG           varchar(1)           not null,
   POINTOFFSET          numeric              not null,
   ARCHIVETYPE          varchar(12)          not null,
   ARCHIVEINTERVAL      numeric              not null,
   constraint Key_PT_PTID primary key (POINTID)
)
go

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
   add constraint AK_KEY_PTNM_YUKPAOID unique (POINTNAME, PAObjectID)
go

/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create index Indx_PointStGrpID on POINT (
STATEGROUPID ASC
)
go

/*==============================================================*/
/* Index: INDX_PAOBJECTID                                       */
/*==============================================================*/
create index INDX_PAOBJECTID on POINT (
PAObjectID ASC
)
go

/*==============================================================*/
/* Index: INDX_PAOBJECTID_POFFSET                               */
/*==============================================================*/
create index INDX_PAOBJECTID_POFFSET on POINT (
PAObjectID ASC,
POINTOFFSET ASC
)
go

/*==============================================================*/
/* Index: INDX_PAOBJECTID_POINTID                               */
/*==============================================================*/
create index INDX_PAOBJECTID_POINTID on POINT (
PAObjectID ASC,
POINTID ASC
)
go

/*==============================================================*/
/* Index: INDX_POFFSET_POINTTYPE                                */
/*==============================================================*/
create index INDX_POFFSET_POINTTYPE on POINT (
POINTOFFSET ASC,
POINTTYPE ASC
)
go

/*==============================================================*/
/* Table: POINTACCUMULATOR                                      */
/*==============================================================*/
create table POINTACCUMULATOR (
   POINTID              numeric              not null,
   MULTIPLIER           float                not null,
   DATAOFFSET           float                not null,
   constraint PK_POINTACCUMULATOR primary key (POINTID)
)
go

/*==============================================================*/
/* Table: POINTANALOG                                           */
/*==============================================================*/
create table POINTANALOG (
   POINTID              numeric              not null,
   DEADBAND             float                not null,
   MULTIPLIER           float                not null,
   DATAOFFSET           float                not null,
   constraint PK_POINTANALOG primary key (POINTID)
)
go

insert into pointanalog values( 100, 0, 1, 0 );

/*==============================================================*/
/* Table: POINTLIMITS                                           */
/*==============================================================*/
create table POINTLIMITS (
   POINTID              numeric              not null,
   LIMITNUMBER          numeric              not null,
   HIGHLIMIT            float                not null,
   LOWLIMIT             float                not null,
   LIMITDURATION        numeric              not null,
   constraint PK_POINTLIMITS primary key (POINTID, LIMITNUMBER)
)
go

/*==============================================================*/
/* Table: POINTPROPERTYVALUE                                    */
/*==============================================================*/
create table POINTPROPERTYVALUE (
   PointID              numeric              not null,
   PointPropertyCode    int                  not null,
   FltValue             float                not null,
   constraint PK_POINTPROPERTYVALUE primary key nonclustered (PointID, PointPropertyCode)
)
go

/*==============================================================*/
/* Table: POINTSTATUS                                           */
/*==============================================================*/
create table POINTSTATUS (
   POINTID              numeric              not null,
   INITIALSTATE         numeric              not null,
   constraint PK_PtStatus primary key (POINTID)
)
go

insert into pointstatus values( 7, 0);
insert into pointstatus values( 6, 0);
insert into pointstatus values( 5, 0);
insert into pointstatus values( 4, 0);
insert into pointstatus values( 3, 0);
insert into pointstatus values( 2, 0);
insert into pointstatus values( 1, 0);

/*==============================================================*/
/* Table: POINTTRIGGER                                          */
/*==============================================================*/
create table POINTTRIGGER (
   PointID              numeric              not null,
   TriggerID            numeric              not null,
   TriggerDeadband      float                not null,
   VerificationID       numeric              not null,
   VerificationDeadband float                not null,
   CommandTimeout       numeric              not null,
   Parameters           varchar(40)          not null,
   constraint PK_POINTTRIGGER primary key (PointID)
)
go

/*==============================================================*/
/* Table: POINTUNIT                                             */
/*==============================================================*/
create table POINTUNIT (
   POINTID              numeric              not null,
   UOMID                numeric              not null,
   DECIMALPLACES        numeric              not null,
   HighReasonabilityLimit float                not null,
   LowReasonabilityLimit float                not null,
   DecimalDigits        numeric              not null,
   constraint PK_POINTUNITID primary key (POINTID)
)
go

insert into pointunit values( 100, 9, 1, 1.0E+30, -1.0E+30, 0);

/*==============================================================*/
/* Index: INDX_UOMID_POINTID                                    */
/*==============================================================*/
create index INDX_UOMID_POINTID on POINTUNIT (
UOMID ASC,
POINTID ASC
)
go

/*==============================================================*/
/* Table: PORTDIALUPMODEM                                       */
/*==============================================================*/
create table PORTDIALUPMODEM (
   PORTID               numeric              not null,
   MODEMTYPE            varchar(30)          not null,
   INITIALIZATIONSTRING varchar(50)          not null,
   PREFIXNUMBER         varchar(10)          not null,
   SUFFIXNUMBER         varchar(10)          not null,
   constraint PK_PORTDIALUPMODEM primary key (PORTID)
)
go

/*==============================================================*/
/* Table: PORTLOCALSERIAL                                       */
/*==============================================================*/
create table PORTLOCALSERIAL (
   PORTID               numeric              not null,
   PHYSICALPORT         varchar(8)           not null,
   constraint PK_PORTLOCALSERIAL primary key (PORTID)
)
go

/*==============================================================*/
/* Table: PORTRADIOSETTINGS                                     */
/*==============================================================*/
create table PORTRADIOSETTINGS (
   PORTID               numeric              not null,
   RTSTOTXWAITSAMED     numeric              not null,
   RTSTOTXWAITDIFFD     numeric              not null,
   RADIOMASTERTAIL      numeric              not null,
   REVERSERTS           numeric              not null,
   constraint PK_PORTRADIOSETTINGS primary key (PORTID)
)
go

/*==============================================================*/
/* Table: PORTSETTINGS                                          */
/*==============================================================*/
create table PORTSETTINGS (
   PORTID               numeric              not null,
   BAUDRATE             numeric              not null,
   CDWAIT               numeric              not null,
   LINESETTINGS         varchar(8)           not null,
   constraint PK_PORTSETTINGS primary key (PORTID)
)
go

/*==============================================================*/
/* Table: PORTTERMINALSERVER                                    */
/*==============================================================*/
create table PORTTERMINALSERVER (
   PORTID               numeric              not null,
   IPADDRESS            varchar(64)          not null,
   SOCKETPORTNUMBER     numeric              not null,
   EncodingKey          varchar(64)          not null,
   EncodingType         varchar(50)          not null,
   constraint PK_PORTTERMINALSERVER primary key (PORTID)
)
go

/*==============================================================*/
/* Index: INDX_IPAdd_SockPortNum_UNQ                            */
/*==============================================================*/
create unique index INDX_IPAdd_SockPortNum_UNQ on PORTTERMINALSERVER (
IPADDRESS ASC,
SOCKETPORTNUMBER ASC
)
go

/*==============================================================*/
/* Table: PROFILEPEAKRESULT                                     */
/*==============================================================*/
create table PROFILEPEAKRESULT (
   ResultId             numeric              not null,
   DeviceId             numeric              not null,
   ResultFrom           varchar(30)          not null,
   ResultTo             varchar(30)          not null,
   RunDate              varchar(30)          not null,
   PeakDay              varchar(30)          not null,
   Usage                varchar(25)          not null,
   Demand               varchar(25)          not null,
   AverageDailyUsage    varchar(25)          not null,
   TotalUsage           varchar(25)          not null,
   ResultType           varchar(5)           not null,
   Days                 numeric              not null,
   constraint PK_PROFILEPEAKRESULT primary key (ResultId)
)
go

/*==============================================================*/
/* Table: PasswordHistory                                       */
/*==============================================================*/
create table PasswordHistory (
   PasswordHistoryId    numeric              not null,
   UserId               numeric              not null,
   Password             varchar(64)          not null,
   AuthType             varchar(16)          not null,
   PasswordChangedDate  datetime             not null,
   constraint PK_PasswordHistory primary key nonclustered (PasswordHistoryId)
)
go

/*==============================================================*/
/* Table: PersistedSystemValue                                  */
/*==============================================================*/
create table PersistedSystemValue (
   Name                 varchar(50)          not null,
   Value                text                 not null,
   constraint PK_PerSysValue primary key (Name)
)
go

/*==============================================================*/
/* Table: PointAlarming                                         */
/*==============================================================*/
create table PointAlarming (
   PointID              numeric              not null,
   AlarmStates          varchar(32)          not null,
   ExcludeNotifyStates  varchar(32)          not null,
   NotifyOnAcknowledge  char(1)              not null,
   NotificationGroupID  numeric              not null,
   constraint PK_POINTALARMING primary key (PointID)
)
go

INSERT INTO PointAlarming(PointId, AlarmStates, ExcludeNotifyStates, NotifyOnAcknowledge, NotificationGroupId)
SELECT PointId, '', 'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN', 'N', 1 
FROM Point;

/*==============================================================*/
/* Table: PointControl                                          */
/*==============================================================*/
create table PointControl (
   PointId              numeric              not null,
   ControlOffset        numeric              not null,
   ControlInhibit       varchar(1)           not null,
   constraint PK_PointControl primary key (PointId)
)
go

/*==============================================================*/
/* Table: PointStatusControl                                    */
/*==============================================================*/
create table PointStatusControl (
   PointId              numeric              not null,
   ControlType          varchar(12)          not null,
   CloseTime1           numeric              not null,
   CloseTime2           numeric              not null,
   StateZeroControl     varchar(100)         not null,
   StateOneControl      varchar(100)         not null,
   CommandTimeOut       numeric              not null,
   constraint PK_PointStatusControl primary key (PointId)
)
go

/*==============================================================*/
/* Table: PointToZoneMapping                                    */
/*==============================================================*/
create table PointToZoneMapping (
   PointId              numeric              not null,
   ZoneId               numeric              not null,
   GraphPositionOffset  float                null,
   Distance             float                null,
   constraint PK_PointZoneMap primary key (PointId)
)
go

/*==============================================================*/
/* Table: PortTiming                                            */
/*==============================================================*/
create table PortTiming (
   PORTID               numeric              not null,
   PRETXWAIT            numeric              not null,
   RTSTOTXWAIT          numeric              not null,
   POSTTXWAIT           numeric              not null,
   RECEIVEDATAWAIT      numeric              not null,
   EXTRATIMEOUT         numeric              not null,
   constraint PK_PORTTIMING primary key (PORTID)
)
go

/*==============================================================*/
/* Table: PorterResponseMonitor                                 */
/*==============================================================*/
create table PorterResponseMonitor (
   MonitorId            numeric              not null,
   Name                 varchar(255)         not null,
   GroupName            varchar(255)         not null,
   StateGroupId         numeric              not null,
   Attribute            varchar(255)         not null,
   EvaluatorStatus      varchar(255)         not null,
   constraint PK_PortRespMonId primary key nonclustered (MonitorId)
)
go

INSERT INTO PorterResponseMonitor VALUES (1, 'Default All Meters', '/', -14, 'OUTAGE_STATUS', 'DISABLED');

/*==============================================================*/
/* Index: Indx_PortRespMon_Name_UNQ                             */
/*==============================================================*/
create unique index Indx_PortRespMon_Name_UNQ on PorterResponseMonitor (
Name ASC
)
go

/*==============================================================*/
/* Table: PorterResponseMonitorErrorCode                        */
/*==============================================================*/
create table PorterResponseMonitorErrorCode (
   ErrorCodeId          numeric              not null,
   RuleId               numeric              not null,
   ErrorCode            numeric              not null,
   constraint PK_PortRespMonErrorCodeId primary key nonclustered (ErrorCodeId)
)
go

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
)
go

/*==============================================================*/
/* Table: PorterResponseMonitorRule                             */
/*==============================================================*/
create table PorterResponseMonitorRule (
   RuleId               numeric              not null,
   RuleOrder            numeric              not null,
   MonitorId            numeric              not null,
   Success              char(1)              not null,
   MatchStyle           varchar(40)          not null,
   State                varchar(40)          not null,
   constraint PK_PortRespMonRuleId primary key nonclustered (RuleId)
)
go

INSERT INTO PorterResponseMonitorRule VALUES (1, 1, 1, 'Y', 'any', 0); 
INSERT INTO PorterResponseMonitorRule VALUES (2, 2, 1, 'N', 'any', 0); 
INSERT INTO PorterResponseMonitorRule VALUES (3, 3, 1, 'N', 'any', 1); 

/*==============================================================*/
/* Index: Indx_PortRespMonRule_RO_MI_UNQ                        */
/*==============================================================*/
create unique index Indx_PortRespMonRule_RO_MI_UNQ on PorterResponseMonitorRule (
RuleOrder ASC,
MonitorId ASC
)
go

/*==============================================================*/
/* Table: ProgramToSeasonalProgram                              */
/*==============================================================*/
create table ProgramToSeasonalProgram (
   AssignedProgramId    numeric              not null,
   SeasonalProgramId    numeric              not null,
   constraint PK_ProgramToSeasonalProgram primary key (AssignedProgramId)
)
go

/*==============================================================*/
/* Table: PurchasePlan                                          */
/*==============================================================*/
create table PurchasePlan (
   PurchaseID           numeric              not null,
   EnergyCompanyID      numeric              not null,
   PlanName             varchar(60)          not null,
   PODesignation        varchar(40)          not null,
   AccountingCode       varchar(30)          not null,
   TimePeriod           datetime             not null,
   constraint PK_PURCHASEPLAN primary key (PurchaseID)
)
go

/*==============================================================*/
/* Table: RAWPOINTHISTORY                                       */
/*==============================================================*/
create table RAWPOINTHISTORY (
   CHANGEID             numeric              not null,
   POINTID              numeric              not null,
   TIMESTAMP            datetime             not null,
   QUALITY              numeric              not null,
   VALUE                float                not null,
   millis               smallint             not null,
   constraint SYS_C0013322 primary key nonclustered (CHANGEID)
)
go

/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create index Index_PointID on RAWPOINTHISTORY (
POINTID ASC
)
go

/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create index Indx_TimeStamp on RAWPOINTHISTORY (
TIMESTAMP ASC
)
go

/*==============================================================*/
/* Index: Indx_RwPtHisPtIDTst                                   */
/*==============================================================*/
create index Indx_RwPtHisPtIDTst on RAWPOINTHISTORY (
POINTID ASC,
TIMESTAMP ASC
)
go

/*==============================================================*/
/* Index: Indx_RwPtHisTstPtId                                   */
/*==============================================================*/
create index Indx_RwPtHisTstPtId on RAWPOINTHISTORY (
TIMESTAMP ASC,
POINTID ASC
)
go

/*==============================================================*/
/* Table: RFNAddress                                            */
/*==============================================================*/
create table RFNAddress (
   DeviceId             numeric              not null,
   SerialNumber         varchar(30)          not null,
   Manufacturer         varchar(80)          not null,
   Model                varchar(80)          not null,
   constraint PK_RFNAdd primary key (DeviceId)
)
go

/*==============================================================*/
/* Index: Indx_RFNAdd_SerNum_Man_Mod_UNQ                        */
/*==============================================================*/
create unique index Indx_RFNAdd_SerNum_Man_Mod_UNQ on RFNAddress (
SerialNumber ASC,
Manufacturer ASC,
Model ASC
)
go

/*==============================================================*/
/* Table: RPHTag                                                */
/*==============================================================*/
create table RPHTag (
   ChangeId             numeric              not null,
   TagName              varchar(150)         not null,
   constraint PK_RPHTag primary key (ChangeId, TagName)
)
go

/*==============================================================*/
/* Table: RawPointHistoryDependentJob                           */
/*==============================================================*/
create table RawPointHistoryDependentJob (
   JobId                int                  not null,
   RawPointHistoryId    numeric              not null,
   constraint PK_RawPointHistoryDependentJob primary key (JobId)
)
go

/*==============================================================*/
/* Table: Regulator                                             */
/*==============================================================*/
create table Regulator (
   RegulatorId          numeric              not null,
   KeepAliveTimer       numeric              not null,
   KeepAliveConfig      numeric              not null,
   VoltChangePerTap     float                not null,
   constraint PK_Reg primary key (RegulatorId)
)
go

/*==============================================================*/
/* Table: RegulatorToZoneMapping                                */
/*==============================================================*/
create table RegulatorToZoneMapping (
   RegulatorId          numeric              not null,
   ZoneId               numeric              not null,
   constraint PK_RegToZoneMap primary key (RegulatorId)
)
go

/*==============================================================*/
/* Table: RepeaterRoute                                         */
/*==============================================================*/
create table RepeaterRoute (
   ROUTEID              numeric              not null,
   DEVICEID             numeric              not null,
   VARIABLEBITS         numeric              not null,
   REPEATERORDER        numeric              not null,
   constraint PK_REPEATERROUTE primary key (ROUTEID, DEVICEID)
)
go

/*==============================================================*/
/* Table: ReportedAddressExpressCom                             */
/*==============================================================*/
create table ReportedAddressExpressCom (
   ChangeId             numeric              not null,
   DeviceId             numeric              not null,
   Timestamp            datetime             not null,
   SPID                 numeric              not null,
   GEO                  numeric              not null,
   Substation           numeric              not null,
   Feeder               numeric              not null,
   Zip                  numeric              not null,
   UDA                  numeric              not null,
   Required             numeric              not null,
   constraint PK_ReportedAddressExpressCom primary key (ChangeId)
)
go

/*==============================================================*/
/* Table: ReportedAddressRelayExpressCom                        */
/*==============================================================*/
create table ReportedAddressRelayExpressCom (
   ChangeId             numeric              not null,
   RelayNumber          numeric              not null,
   Program              numeric              not null,
   Splinter             numeric              not null,
   constraint PK_ReportedAddressRelayExpCom primary key (ChangeId, RelayNumber)
)
go

/*==============================================================*/
/* Table: ReportedAddressSep                                    */
/*==============================================================*/
create table ReportedAddressSep (
   ChangeId             numeric              not null,
   DeviceId             numeric              not null,
   Timestamp            datetime             not null,
   UtilityEnrollmentGroup numeric              not null,
   RandomStartTimeMinutes numeric              not null,
   RandomStopTimeMinutes numeric              not null,
   DeviceClass          numeric              not null,
   constraint PK_ReportedAddressSep primary key (ChangeId)
)
go

/*==============================================================*/
/* Table: Route                                                 */
/*==============================================================*/
create table Route (
   RouteID              numeric              not null,
   DeviceID             numeric              not null,
   DefaultRoute         char(1)              not null,
   constraint SYS_RoutePK primary key nonclustered (RouteID)
)
go

INSERT INTO Route VALUES (0,0,'N');

/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique index Indx_RouteDevID on Route (
DeviceID ASC,
RouteID ASC
)
go

/*==============================================================*/
/* Table: SYSTEMLOG                                             */
/*==============================================================*/
create table SYSTEMLOG (
   LOGID                numeric              not null,
   POINTID              numeric              not null,
   DATETIME             datetime             not null,
   SOE_TAG              numeric              not null,
   TYPE                 numeric              not null,
   PRIORITY             numeric              not null,
   ACTION               varchar(60)          null,
   DESCRIPTION          varchar(120)         null,
   USERNAME             varchar(64)          null,
   millis               smallint             not null,
   constraint SYS_C0013407 primary key (LOGID)
)
go

/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create index Indx_SYSLG_PtId on SYSTEMLOG (
POINTID ASC
)
go

/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create index Indx_SYSLG_Date on SYSTEMLOG (
DATETIME ASC
)
go

/*==============================================================*/
/* Index: INDX_SYSLG_PTID_TS                                    */
/*==============================================================*/
create unique index INDX_SYSLG_PTID_TS on SYSTEMLOG (
LOGID ASC,
POINTID ASC,
DATETIME ASC
)
go

/*==============================================================*/
/* Table: ScheduleShipmentMapping                               */
/*==============================================================*/
create table ScheduleShipmentMapping (
   ScheduleID           numeric              not null,
   ShipmentID           numeric              not null,
   constraint PK_SCHEDULESHIPMENTMAPPING primary key (ScheduleID, ShipmentID)
)
go

/*==============================================================*/
/* Table: ScheduleTimePeriod                                    */
/*==============================================================*/
create table ScheduleTimePeriod (
   TimePeriodID         numeric              not null,
   ScheduleID           numeric              not null,
   TimePeriodName       varchar(60)          not null,
   Quantity             numeric              not null,
   PredictedShipDate    datetime             not null,
   constraint PK_SCHEDULETIMEPERIOD primary key (TimePeriodID)
)
go

/*==============================================================*/
/* Table: ScheduledGrpCommandRequest                            */
/*==============================================================*/
create table ScheduledGrpCommandRequest (
   CommandRequestExecContextId numeric              not null,
   JobId                int                  not null,
   constraint PK_ScheduledGrpCommandRequest primary key (CommandRequestExecContextId)
)
go

/*==============================================================*/
/* Index: Indx_SchGrpComReq_JobId                               */
/*==============================================================*/
create index Indx_SchGrpComReq_JobId on ScheduledGrpCommandRequest (
JobId ASC
)
go

/*==============================================================*/
/* Table: SeasonSchedule                                        */
/*==============================================================*/
create table SeasonSchedule (
   ScheduleID           numeric              not null,
   ScheduleName         varchar(40)          not null,
   constraint PK_SEASONSCHEDULE primary key (ScheduleID)
)
go

insert into SeasonSchedule values( 0, 'Empty Schedule' );
insert into seasonSchedule values (-1,'No Season');

/*==============================================================*/
/* Table: SequenceNumber                                        */
/*==============================================================*/
create table SequenceNumber (
   LastValue            numeric              not null,
   SequenceName         varchar(30)          not null,
   constraint PK_SEQUENCENUMBER primary key (SequenceName)
)
go

INSERT INTO SequenceNumber VALUES (100, 'BillingFileFormats');
INSERT INTO SequenceNumber VALUES (100, 'DeviceGroup');

/*==============================================================*/
/* Table: ServiceCompany                                        */
/*==============================================================*/
create table ServiceCompany (
   CompanyID            numeric              not null,
   CompanyName          varchar(40)          null,
   AddressID            numeric              null,
   MainPhoneNumber      varchar(14)          null,
   MainFaxNumber        varchar(14)          null,
   PrimaryContactID     numeric              null,
   HIType               varchar(40)          null,
   constraint PK_SERVICECOMPANY primary key (CompanyID)
)
go

INSERT INTO ServiceCompany VALUES (0,'(none)',0,'(none)','(none)',0,'(none)');

/*==============================================================*/
/* Table: ServiceCompanyDesignationCode                         */
/*==============================================================*/
create table ServiceCompanyDesignationCode (
   DesignationCodeID    numeric              not null,
   DesignationCodeValue varchar(60)          not null,
   ServiceCompanyID     numeric              not null,
   constraint PK_SERVICECOMPANYDESIGNATIONCO primary key (DesignationCodeID)
)
go

/*==============================================================*/
/* Table: Shipment                                              */
/*==============================================================*/
create table Shipment (
   ShipmentID           numeric              not null,
   ShipmentNumber       varchar(60)          not null,
   WarehouseID          numeric              not null,
   SerialNumberStart    varchar(30)          not null,
   SerialNumberEnd      varchar(30)          not null,
   ShipDate             datetime             not null,
   ActualPricePerUnit   float                not null,
   SalesTotal           float                not null,
   SalesTax             float                not null,
   OtherCharges         float                not null,
   ShippingCharges      float                not null,
   AmountPaid           float                not null,
   OrderedDate          datetime             not null,
   ReceivedDate         datetime             not null,
   constraint PK_SHIPMENT primary key (ShipmentID)
)
go

/*==============================================================*/
/* Table: SiteInformation                                       */
/*==============================================================*/
create table SiteInformation (
   SiteID               numeric              not null,
   Feeder               varchar(20)          null,
   Pole                 varchar(20)          null,
   TransformerSize      varchar(20)          null,
   ServiceVoltage       varchar(20)          null,
   SubstationID         numeric              null,
   constraint PK_SITEINFORMATION primary key (SiteID)
)
go

INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0);

/*==============================================================*/
/* Table: State                                                 */
/*==============================================================*/
create table State (
   StateGroupId         numeric              not null,
   RawState             numeric              not null,
   Text                 varchar(32)          not null,
   ForegroundColor      numeric              not null,
   BackgroundColor      numeric              not null,
   ImageId              numeric              not null,
   constraint PK_STATE primary key (StateGroupId, RawState)
)
go

INSERT INTO State VALUES(-19, 0, 'Success', 0, 6, 0);
INSERT INTO State VALUES(-19, 1, 'Not Applicable', 9, 6, 0);
INSERT INTO State VALUES(-19, 2, 'Failure', 1, 6, 0);
INSERT INTO State VALUES(-19, 3, 'Unsupported', 7, 6, 0);
INSERT INTO State VALUES(-18, 0, 'Unknown', 9, 6, 0);
INSERT INTO State VALUES(-18, 1, 'In Service', 0, 6, 0);
INSERT INTO State VALUES(-18, 2, 'Out of Service', 1, 6, 0);
INSERT INTO State VALUES(-18, 3, 'Temporarily Out of Service', 7, 6, 0);
INSERT INTO State VALUES(-17, 0, 'Manual', 0, 6, 0);
INSERT INTO State VALUES(-17, 1, 'SCADA Override', 1, 6, 0);
INSERT INTO State VALUES(-17, 2, 'Fault Current', 2, 6, 0);
INSERT INTO State VALUES(-17, 3, 'Emergency Voltage', 3, 6, 0);
INSERT INTO State VALUES(-17, 4, 'Time ONOFF', 4, 6, 0);
INSERT INTO State VALUES(-17, 5, 'OVUV Control', 5, 6, 0);
INSERT INTO State VALUES(-17, 6, 'VAR', 7, 6, 0);
INSERT INTO State VALUES(-17, 7, 'Analog 1', 8, 6, 0);
INSERT INTO State VALUES(-17, 8, 'Analog 2', 1, 6, 0);
INSERT INTO State VALUES(-17, 9, 'Analog 3', 2, 6, 0);
INSERT INTO State VALUES(-17, 10, 'Analog 4', 3, 6, 0);
INSERT INTO State VALUES(-17, 11, 'Analog 5', 4, 6, 0);
INSERT INTO State VALUES(-17, 12, 'Analog 6', 5, 6, 0);
INSERT INTO State VALUES(-17, 13, 'Temp', 7, 6, 0);
INSERT INTO State VALUES(-17, 14, 'Remote', 8, 6, 0);
INSERT INTO State VALUES(-17, 15, 'Time', 1, 6, 0);
INSERT INTO State VALUES(-17, 16, 'N/A', 2, 6, 0);
INSERT INTO State VALUES(-16, 0, 'Cleared', 0, 6, 0);
INSERT INTO State VALUES(-16, 1, 'Active', 1, 6, 0);
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
)
go

/*==============================================================*/
/* Table: StateGroup                                            */
/*==============================================================*/
create table StateGroup (
   StateGroupId         numeric              not null,
   Name                 varchar(60)          not null,
   GroupType            varchar(20)          not null,
   constraint SYS_C0013128 primary key (StateGroupId)
)
go

INSERT INTO StateGroup VALUES(-19, 'RF Demand Reset', 'Status');
INSERT INTO StateGroup VALUES(-18, 'LCR Service Status', 'Status');
INSERT INTO StateGroup VALUES(-17, 'Last Control', 'Status');
INSERT INTO StateGroup VALUES(-16, 'Event Status', 'Status');
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
)
go

/*==============================================================*/
/* Table: StaticPAOInfo                                         */
/*==============================================================*/
create table StaticPAOInfo (
   StaticPAOInfoId      numeric              not null,
   PAObjectId           numeric              not null,
   InfoKey              varchar(128)         not null,
   Value                varchar(128)         null,
   constraint PK_StatPAOInfo primary key (StaticPAOInfoId)
)
go

/*==============================================================*/
/* Index: Indx_PAObjId_InfoKey_UNQ                              */
/*==============================================================*/
create unique index Indx_PAObjId_InfoKey_UNQ on StaticPAOInfo (
PAObjectId ASC,
InfoKey ASC
)
go

/*==============================================================*/
/* Table: StatusPointMonitor                                    */
/*==============================================================*/
create table StatusPointMonitor (
   StatusPointMonitorId numeric              not null,
   StatusPointMonitorName varchar(255)         not null,
   GroupName            varchar(255)         not null,
   Attribute            varchar(255)         not null,
   StateGroupId         numeric              not null,
   EvaluatorStatus      varchar(255)         not null,
   constraint PK_StatPointMon primary key (StatusPointMonitorId)
)
go

INSERT INTO StatusPointMonitor VALUES (1, 'Default All Meters', '/', 'OUTAGE_STATUS', -14, 'DISABLED');

/*==============================================================*/
/* Index: Indx_StatPointMon_MonName_UNQ                         */
/*==============================================================*/
create unique index Indx_StatPointMon_MonName_UNQ on StatusPointMonitor (
StatusPointMonitorName ASC
)
go

/*==============================================================*/
/* Table: StatusPointMonitorProcessor                           */
/*==============================================================*/
create table StatusPointMonitorProcessor (
   StatusPointMonitorProcessorId numeric              not null,
   StatusPointMonitorId numeric              not null,
   PrevState            varchar(255)         not null,
   NextState            varchar(255)         not null,
   ActionType           varchar(255)         not null,
   constraint PK_StatPointMonProcId primary key (StatusPointMonitorProcessorId)
)
go

INSERT INTO StatusPointMonitorProcessor VALUES (1, 1, 'DIFFERENCE', 1, 'NoResponse');
INSERT INTO StatusPointMonitorProcessor VALUES (2, 1, 'DIFFERENCE', 0, 'Restoration');

/*==============================================================*/
/* Table: Substation                                            */
/*==============================================================*/
create table Substation (
   SubstationID         numeric              not null,
   SubstationName       varchar(50)          null,
   constraint PK_SUBSTATION primary key (SubstationID)
)
go

INSERT INTO Substation VALUES (0,'(none)');

/*==============================================================*/
/* Table: SubstationToRouteMapping                              */
/*==============================================================*/
create table SubstationToRouteMapping (
   SubstationID         numeric              not null,
   RouteID              numeric              not null,
   Ordering             numeric              not null,
   constraint PK_SUBSTATIONTOROUTEMAPPING primary key (SubstationID, RouteID)
)
go

/*==============================================================*/
/* Table: Survey                                                */
/*==============================================================*/
create table Survey (
   SurveyId             numeric              not null,
   EnergyCompanyId      numeric              not null,
   SurveyName           varchar(64)          not null,
   SurveyKey            varchar(64)          not null,
   constraint PK_Surv primary key (SurveyId)
)
go

/*==============================================================*/
/* Table: SurveyQuestion                                        */
/*==============================================================*/
create table SurveyQuestion (
   SurveyQuestionId     numeric              not null,
   SurveyId             numeric              not null,
   QuestionKey          varchar(64)          not null,
   AnswerRequired       char(1)              not null,
   QuestionType         varchar(32)          not null,
   TextAnswerAllowed    char(1)              not null,
   DisplayOrder         numeric              not null,
   constraint PK_SurvQuest primary key (SurveyQuestionId)
)
go

/*==============================================================*/
/* Index: Indx_SurvId_DispOrder_UNQ                             */
/*==============================================================*/
create unique index Indx_SurvId_DispOrder_UNQ on SurveyQuestion (
SurveyId ASC,
DisplayOrder ASC
)
go

/*==============================================================*/
/* Table: SurveyQuestionAnswer                                  */
/*==============================================================*/
create table SurveyQuestionAnswer (
   SurveyQuestionAnswerId numeric              not null,
   SurveyQuestionId     numeric              not null,
   AnswerKey            varchar(64)          not null,
   DisplayOrder         numeric              not null,
   constraint PK_SurvQuestAns primary key (SurveyQuestionAnswerId)
)
go

/*==============================================================*/
/* Index: Indx_SurvQuestId_dispOrder_UNQ                        */
/*==============================================================*/
create unique index Indx_SurvQuestId_dispOrder_UNQ on SurveyQuestionAnswer (
SurveyQuestionId ASC,
DisplayOrder ASC
)
go

/*==============================================================*/
/* Table: SurveyResult                                          */
/*==============================================================*/
create table SurveyResult (
   SurveyResultId       numeric              not null,
   SurveyId             numeric              not null,
   AccountId            numeric              null,
   AccountNumber        varchar(40)          null,
   WhenTaken            datetime             not null,
   constraint PK_SurvRes primary key (SurveyResultId)
)
go

/*==============================================================*/
/* Table: SurveyResultAnswer                                    */
/*==============================================================*/
create table SurveyResultAnswer (
   SurveyResultAnswerId numeric              not null,
   SurveyResultId       numeric              not null,
   SurveyQuestionId     numeric              not null,
   SurveyQuestionAnswerId numeric              null,
   TextAnswer           varchar(255)         null,
   constraint PK_SurvResAns primary key (SurveyResultAnswerId)
)
go

/*==============================================================*/
/* Table: TEMPLATE                                              */
/*==============================================================*/
create table TEMPLATE (
   TEMPLATENUM          numeric              not null,
   NAME                 varchar(40)          not null,
   DESCRIPTION          varchar(200)         null,
   constraint SYS_C0013425 primary key (TEMPLATENUM)
)
go

insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon  Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon  Template');

/*==============================================================*/
/* Table: TEMPLATECOLUMNS                                       */
/*==============================================================*/
create table TEMPLATECOLUMNS (
   TEMPLATENUM          numeric              not null,
   TITLE                varchar(50)          not null,
   TYPENUM              numeric              not null,
   ORDERING             numeric              not null,
   WIDTH                numeric              not null,
   constraint PK_TEMPLATECOLUMNS primary key (TEMPLATENUM, TITLE)
)
go

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
create table TOUDay (
   TOUDayID             numeric              not null,
   TOUDayName           varchar(32)          not null,
   constraint PK_TOUDAY primary key (TOUDayID)
)
go

/*==============================================================*/
/* Table: TOUDayMapping                                         */
/*==============================================================*/
create table TOUDayMapping (
   TOUScheduleID        numeric              not null,
   TOUDayID             numeric              not null,
   TOUDayOffset         numeric              not null,
   constraint PK_TOUDAYMAPPING primary key (TOUScheduleID, TOUDayOffset)
)
go

/*==============================================================*/
/* Table: TOUDayRateSwitches                                    */
/*==============================================================*/
create table TOUDayRateSwitches (
   TOURateSwitchID      numeric              not null,
   SwitchRate           varchar(4)           not null,
   SwitchOffset         numeric              not null,
   TOUDayID             numeric              not null,
   constraint PK_TOURATESWITCH primary key (TOURateSwitchID)
)
go

/*==============================================================*/
/* Index: Indx_todsw_idoff                                      */
/*==============================================================*/
create unique index Indx_todsw_idoff on TOUDayRateSwitches (
SwitchOffset ASC,
TOUDayID ASC
)
go

/*==============================================================*/
/* Table: TOUSchedule                                           */
/*==============================================================*/
create table TOUSchedule (
   TOUScheduleID        numeric              not null,
   TOUScheduleName      varchar(32)          not null,
   TOUDefaultRate       varchar(4)           not null,
   constraint PK_TOUSCHEDULE primary key (TOUScheduleID)
)
go

insert into TOUSchedule values (0, '(none)', 0);

/*==============================================================*/
/* Table: TagLog                                                */
/*==============================================================*/
create table TagLog (
   LogID                numeric              not null,
   InstanceID           numeric              not null,
   PointID              numeric              not null,
   TagID                numeric              not null,
   UserName             varchar(64)          not null,
   Action               varchar(20)          not null,
   Description          varchar(120)         not null,
   TagTime              datetime             not null,
   RefStr               varchar(60)          not null,
   ForStr               varchar(60)          not null,
   constraint PK_TAGLOG primary key (LogID)
)
go

/*==============================================================*/
/* Table: Tags                                                  */
/*==============================================================*/
create table Tags (
   TagID                numeric              not null,
   TagName              varchar(60)          not null,
   TagLevel             numeric              not null,
   Inhibit              char(1)              not null,
   ColorID              numeric              not null,
   ImageID              numeric              not null,
   constraint PK_TAGS primary key (TagID)
)
go

insert into tags values(-1, 'Out Of Service', 1, 'Y', 1, 0);
insert into tags values(-2, 'Info', 1, 'N', 6, 0);
insert into tags values (-3, 'Cap Bank Operational State', 1, 'N', 0, 0);
insert into tags values (-4, 'Enablement State', 1, 'N', 0, 0);
insert into tags values (-5, 'OVUV Enablement State', 1, 'N', 0, 0); 

/*==============================================================*/
/* Table: TamperFlagMonitor                                     */
/*==============================================================*/
create table TamperFlagMonitor (
   TamperFlagMonitorId  numeric              not null,
   TamperFlagMonitorName varchar(255)         not null,
   GroupName            varchar(255)         not null,
   EvaluatorStatus      varchar(255)         not null,
   constraint PK_TAMPERFLAGMONITOR primary key (TamperFlagMonitorId)
)
go

/*==============================================================*/
/* Index: INDX_TampFlagMonName_UNQ                              */
/*==============================================================*/
create unique index INDX_TampFlagMonName_UNQ on TamperFlagMonitor (
TamperFlagMonitorName ASC
)
go

/*==============================================================*/
/* Table: TemplateDisplay                                       */
/*==============================================================*/
create table TemplateDisplay (
   DisplayNum           numeric              not null,
   TemplateNum          numeric              not null,
   constraint PK_TEMPLATEDISPLAY primary key (DisplayNum)
)
go

/*==============================================================*/
/* Table: Theme                                                 */
/*==============================================================*/
create table Theme (
   ThemeId              numeric              not null,
   Name                 varchar(255)         not null,
   IsCurrent            char(1)              not null,
   constraint PK_Theme primary key (ThemeId)
)
go

INSERT INTO Theme VALUES (-1, 'Yukon Gray', 1);

/*==============================================================*/
/* Table: ThemeProperty                                         */
/*==============================================================*/
create table ThemeProperty (
   ThemeId              numeric              not null,
   Property             varchar(255)         not null,
   Value                varchar(2048)        not null,
   constraint PK_ThemeProperty primary key (ThemeId, Property)
)
go

INSERT INTO ThemeProperty VALUES (-1, 'PAGE_BACKGROUND', '#6e6d71');
INSERT INTO ThemeProperty VALUES (-1, 'PAGE_BACKGROUND_FONT_COLOR', '#ffffff');
INSERT INTO ThemeProperty VALUES (-1, 'PAGE_BACKGROUND_SHADOW', '#5a595d');
INSERT INTO ThemeProperty VALUES (-1, 'PRIMARY_COLOR', '#0066cc');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_BACKGROUND', '2');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_FONT_COLOR', '#ffffff');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_FONT_SHADOW', 'rgba(0,0,0,0.5)');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_TAGLINE_MARGIN', '35');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO', '1');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_LEFT', '0');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_TOP', '17');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_WIDTH', '163');

/*==============================================================*/
/* Table: ThermostatEventHistory                                */
/*==============================================================*/
create table ThermostatEventHistory (
   EventId              numeric              not null,
   EventType            varchar(64)          not null,
   Username             varchar(64)          not null,
   EventTime            datetime             not null,
   ThermostatId         numeric              not null,
   ManualMode           varchar(64)          null,
   ManualFan            varchar(64)          null,
   ManualHold           char(1)              null,
   ScheduleId           numeric              null,
   ScheduleMode         varchar(64)          null,
   ManualCoolTemp       float                null,
   ManualHeatTemp       float                null,
   constraint PK_ThermEventHist primary key (EventId)
)
go

/*==============================================================*/
/* Table: UNITMEASURE                                           */
/*==============================================================*/
create table UNITMEASURE (
   UOMID                numeric              not null,
   UOMName              varchar(8)           not null,
   CalcType             numeric              not null,
   LongName             varchar(40)          not null,
   Formula              varchar(80)          not null,
   constraint SYS_C0013344 primary key (UOMID)
)
go

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
INSERT INTO UnitMeasure VALUES ( 38,'ft^3', 0,'Cubic Feet','(none)' );
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
INSERT INTO UnitMeasure VALUES ( 55,'m^3', 0, 'Cubic Meters', '(none)');

/*==============================================================*/
/* Table: UserGroup                                             */
/*==============================================================*/
create table UserGroup (
   UserGroupId          numeric              not null,
   Name                 varchar(1000)        not null,
   Description          varchar(200)         not null,
   constraint PK_UserGroup primary key (UserGroupId)
)
go

INSERT INTO UserGroup VALUES(-1, 'Admin User Group', 'A user group with basic admin rights for configuring Yukon.');

/*==============================================================*/
/* Index: Indx_UserGroup_Name_UNQ                               */
/*==============================================================*/
create unique index Indx_UserGroup_Name_UNQ on UserGroup (
Name ASC
)
go

/*==============================================================*/
/* Table: UserGroupToYukonGroupMapping                          */
/*==============================================================*/
create table UserGroupToYukonGroupMapping (
   UserGroupId          numeric              not null,
   GroupId              numeric              not null,
   constraint PK_UserGroupToYukonGroupMap primary key (UserGroupId, GroupId)
)
go

INSERT INTO UserGroupToYukonGroupMapping VALUES(-1, -2);

/*==============================================================*/
/* Table: UserPage                                              */
/*==============================================================*/
create table UserPage (
   UserPageId           numeric              not null,
   UserId               numeric              not null,
   PagePath             varchar(2048)        not null,
   Module               varchar(32)          not null,
   PageName             varchar(32)          not null,
   Favorite             char(1)              not null,
   LastAccess           datetime             null,
   constraint PK_UserPage primary key (UserPageId)
)
go

/*==============================================================*/
/* Table: UserPageParam                                         */
/*==============================================================*/
create table UserPageParam (
   UserPageParamId      numeric              not null,
   UserPageId           numeric              not null,
   ParamNumber          numeric              not null,
   Parameter            varchar(80)          not null,
   constraint PK_UserPageParam primary key (UserPageParamId)
)
go

/*==============================================================*/
/* Table: UserPaoPermission                                     */
/*==============================================================*/
create table UserPaoPermission (
   UserPaoPermissionID  numeric              not null,
   UserID               numeric              not null,
   PaoID                numeric              not null,
   Permission           varchar(50)          not null,
   Allow                varchar(5)           not null,
   constraint PK_USERPAOPERMISSION primary key (UserPaoPermissionID)
)
go

alter table UserPaoPermission
   add constraint AK_USRPAOPERM unique (UserID, PaoID, Permission)
go

/*==============================================================*/
/* Table: UserPreference                                        */
/*==============================================================*/
create table UserPreference (
   PreferenceId         numeric              not null,
   UserId               numeric              not null,
   Name                 varchar(255)         not null,
   Value                varchar(255)         not null,
   constraint PK_UserPreference primary key (PreferenceId)
)
go

/*==============================================================*/
/* Index: Indx_UserPref_UserId_Name_UNQ                         */
/*==============================================================*/
create unique index Indx_UserPref_UserId_Name_UNQ on UserPreference (
UserId ASC,
Name ASC
)
go

/*==============================================================*/
/* Table: UserSubscription                                      */
/*==============================================================*/
create table UserSubscription (
   UserSubscriptionId   numeric              not null,
   UserId               numeric              not null,
   SubscriptionType     varchar(64)          not null,
   RefId                numeric              not null,
   constraint PK_UserSubscription primary key (UserSubscriptionId)
)
go

/*==============================================================*/
/* Table: ValidationMonitor                                     */
/*==============================================================*/
create table ValidationMonitor (
   ValidationMonitorId  numeric              not null,
   ValidationMonitorName varchar(255)         not null,
   GroupName            varchar(255)         not null,
   Threshold            float                not null,
   ReRead               numeric              not null,
   SlopeError           float                not null,
   ReadingError         float                not null,
   PeakHeightMinimum    float                not null,
   QuestionableQuality  numeric              not null,
   EvaluatorStatus      varchar(255)         not null,
   constraint PK_ValidMon primary key (ValidationMonitorId)
)
go

INSERT INTO ValidationMonitor VALUES (1, 'Default All Meters', '/System/Attributes/Existing/Usage Reading', 400, 1, 4, .1000001, 15, 1, 'DISABLED');

/*==============================================================*/
/* Table: VersacomRoute                                         */
/*==============================================================*/
create table VersacomRoute (
   ROUTEID              numeric              not null,
   UTILITYID            numeric              not null,
   SECTIONADDRESS       numeric              not null,
   CLASSADDRESS         numeric              not null,
   DIVISIONADDRESS      numeric              not null,
   BUSNUMBER            numeric              not null,
   AMPCARDSET           numeric              not null,
   constraint PK_VERSACOMROUTE primary key (ROUTEID)
)
go

/*==============================================================*/
/* Table: Warehouse                                             */
/*==============================================================*/
create table Warehouse (
   WarehouseID          numeric              not null,
   WarehouseName        varchar(60)          not null,
   AddressID            numeric              not null,
   Notes                varchar(300)         null,
   EnergyCompanyID      numeric              not null,
   constraint PK_WAREHOUSE primary key (WarehouseID)
)
go

/*==============================================================*/
/* Table: WorkOrderBase                                         */
/*==============================================================*/
create table WorkOrderBase (
   OrderID              numeric              not null,
   OrderNumber          varchar(20)          null,
   WorkTypeID           numeric              not null,
   CurrentStateID       numeric              not null,
   ServiceCompanyID     numeric              null,
   DateReported         datetime             null,
   OrderedBy            varchar(30)          null,
   Description          varchar(500)         null,
   DateScheduled        datetime             null,
   DateCompleted        datetime             null,
   ActionTaken          varchar(200)         null,
   AccountID            numeric              null,
   AdditionalOrderNumber varchar(24)          null,
   constraint PK_WORKORDERBASE primary key (OrderID)
)
go

/*==============================================================*/
/* Table: YukonGroup                                            */
/*==============================================================*/
create table YukonGroup (
   GroupID              numeric              not null,
   GroupName            varchar(120)         not null,
   GroupDescription     varchar(200)         not null,
   constraint PK_YUKONGROUP primary key (GroupID)
)
go

INSERT INTO YukonGroup VALUES (-2,'System Administrator Grp','A set of roles that allow administrative access to the system.');

/*==============================================================*/
/* Index: Indx_YukonGroup_groupName_UNQ                         */
/*==============================================================*/
create unique index Indx_YukonGroup_groupName_UNQ on YukonGroup (
GroupName ASC
)
go

/*==============================================================*/
/* Table: YukonGroupRole                                        */
/*==============================================================*/
create table YukonGroupRole (
   GroupRoleID          numeric              not null,
   GroupID              numeric              not null,
   RoleID               numeric              not null,
   RolePropertyID       numeric              not null,
   Value                varchar(1000)        not null,
   constraint PK_YUKONGRPROLE primary key (GroupRoleID)
)
go

/* START the System Administrator role Group */
/* Database Editor */
insert into YukonGroupRole values(-1000,-2,-100,-10000,' ');
insert into YukonGroupRole values(-1002,-2,-100,-10002,' ');
insert into YukonGroupRole values(-1004,-2,-100,-10004,' ');
insert into YukonGroupRole values(-1005,-2,-100,-10005,' ');
insert into YukonGroupRole values(-1007,-2,-100,-10007,' ');
insert into YukonGroupRole values(-1008,-2,-100,-10008,' ');
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

/* Give admin super user access to system administrator group */
insert into YukonGroupRole values (-2000, -2, -200, -20019, 'true');

/* Device Actions role */ 
INSERT INTO YukonGroupRole VALUES (-2500, -2, -213, -21300, ' '); 
INSERT INTO YukonGroupRole VALUES (-2501, -2, -213, -21301, ' '); 
INSERT INTO YukonGroupRole VALUES (-2502, -2, -213, -21302, ' '); 
INSERT INTO YukonGroupRole VALUES (-2503, -2, -213, -21303, ' '); 
INSERT INTO YukonGroupRole VALUES (-2504, -2, -213, -21304, ' '); 
INSERT INTO YukonGroupRole VALUES (-2505, -2, -213, -21305, ' '); 
INSERT INTO YukonGroupRole VALUES (-2506, -2, -213, -21306, ' '); 
INSERT INTO YukonGroupRole VALUES (-2507, -2, -213, -21307, ' '); 

/* Add IVR role to system administrator group */
insert into YukonGroupRole values (-3000, -2, -801, -80100, ' ');

/*==============================================================*/
/* Table: YukonImage                                            */
/*==============================================================*/
create table YukonImage (
   ImageID              numeric              not null,
   ImageCategory        varchar(20)          not null,
   ImageName            varchar(80)          not null,
   ImageValue           image                null,
   constraint PK_YUKONIMAGE primary key (ImageID)
)
go

INSERT INTO YukonImage VALUES( 0, '(none)', '(none)', NULL );
INSERT INTO YukonImage VALUES (1, 'logos', 'eaton_logo.png',
0x89504E470D0A1A0A0000000D49484452000000890000002F0806000000D761E93A00000006624B474400FF00FF00FFA0BDA793000000097048597300000B1300000B1301009A9C180000000774494D4507DC0B1E1323062794A8340000001D69545874436F6D6D656E7400000000004372656174656420776974682047494D50642E650700000FCB4944415478DAED9C7990DD5595C73FF7BCAD97746725121B12811008045904C18C0843028904D422198C0EC3A0540922A255A235533A7D3D3828B855E930E34051800A4C6682808321814E0B110CC210C72943124D589366CBDEEB5BCF9D3FDE6D6C3ADDE9D749870E9D3E55BFAAF75EDFF5DCEF3DE77BCEBDBF765CB57A2589F4348A79865452D550EC7A811D5B2ED459EB4B9556F36BBF58CDC43FAD44529387664C019C2410A907B719977C8862E7BDDAD0BC6130ADF857CFBF97F49833C8B50F5C581C24AB21DFFE596D687EE2EDED5C7037E9DA33FB6C27530F85D6AFE994A60738882449E05812E9A91086B6E54C1D14BB560E062065D929C07124D213F77B4C2140AA1642090AF97FC3B2DFD586E52FEF636BEF23919E4E325559E9F418C8775E0A3C51713BC934E46C1C079924813CA53C940A43DB72A1031CBFD8C7C5DDFF3185D00DD4D7C9E7FF4E8F6C5AB9BF331AD498F26D4098EB1B1B9DDE7043A8A89D521E90D2C106123920AD26D2906F7F93ED1D4F0DCBACBA0152CABF4CB6FD23430090C14B310789D4717CE1E9137897CB810149AA1A90C774D6EA8E614008A4AAA094DF4D313B5FA7ADDA383C403548D7398AC579A320E94F4AA5651511C2ED0B8EF08D8D6EC8FA75122D59E7958325A7430F9422201F3D044012401265C557F2A46A20D75A44AA0634F1DE373AB2D97FE6AA35F5BD696FFF7DA4F63ED64C3D6477DFAF473EF68B61D76EA10B1C67F996D993DECD204956B433ADD406A5AECAF8480A823DA50DCB5E1BB0EC35BF9D46C87C1AD7FE2FC01A00C6EF0CE05A28158A6522B7C78012C0C4BEC79A847C670192FF705068D78A90A91F439EB38107462E4832F5D0B5DBB3B3E3B68A5A3CFA78705B2B4B70989CCBB886143B5EFAF05B20B9EDF42E3ED7349BE27807E93E0861713AC9B00649384269CFB033B7EB97DAB072E341A36127105830B2418203B3F6CA49E86AA8A998632EA05404172E007E04A0AA0156F79BB1F22DF376F5DF5E099CBBA3D2C9FBB5B3EB396CFC55C0F9401D227F269FFD993634350F1E0C09F6002D40310B81F3FCDAC684CEBAA1343241926B838434FA96B9D70C1CD5D440B1EB25766C59345012CD775E564B78ED5CB23B0177966F5950AF0DCB5A2B899DFA7573F9B66D84CE272A0248CB393339ACEE013275C751CA81952099390B495DEE5BE6DEA80D2BBF31189A4E2875E0A49E60BDFE928364E6280E5B75CA5BD672C481C48A90CC1C81248F1890E0568D87B6CEB515655977BE369B4CDD240A9D90AE9D40AEEB4CA069DF675205A5FCEFB56175FBC000993D16A9FE15A9EAA3E9DC5AB696504E004A126A0FFBBA7F6DCE269DD27C57A57684C0231016FDA5B11E399B740D74E6E6BD5B41327074E35C391358E81CE0E92A671943585259CF898B4864A215C88084FDCB27B804C0DACA0AD77E99EAF147472BD6CBB596CA6009F21DBF76766D65643D2DC02A600BD2C7BE2B15C085F97FD94D876A9E2491825CEB4EA81AD0DCFBED8B5384B0002B962392508260F3FDCCC6FD1B4F70AF0CCC436626702CA6D0B1C7A67F5BE89AA93B9CF1B567570812103641789A64551FBCA40B9C9CEE5B168CC585EC0824AE95B6540DA5FC6F2AE415106431D95D0E0874E51C148DA54B1DB3F6192120EC1EB0D8F8A327417EEA8067302E09D889C08A8AACAD5916640589D4A23D5D760932F5D564DBCFC542E7A10B1227E0DCAF2A29AA1397148067F7F843C33B6219054785195E19442658AA21FD08F9F63268421F5EC585050479B761A402A586503E8BA91A075563FB7E6A269543BDB4350DDF541C5805C7EC276EDD8AF13A32C0917F2801B26E109AACD686655B281536BEC5B57A87C2847371E148AC38C22C49320385CE27C977FE11D7CFCE4A661C85CE169DDCFCF2F04E27316D402BB6EB99A2970BEE2733E62B143ACBBBBEB7DB4AD54276F7AB9079BCE2AEED2D5D3693AC3AB60C8A9EE4350FB863201428E6461848D263A033FF136D68BAF7A09E8915C185F757563675139D3B3E49F58423CA114E0F8024AB2311CD5EA753960D9E3F946C39A17475FFAECE65FA7445EFFA3C094CF72DF38EEABB85ACA398749CD8F6B2EE7A66F8EC68290770AADF3E67BC4E6CDEB9576BD2B06C9B6F39671E85E4DD548F3FF52DB72A02F9AE9D746CBB7E9F0F088527C9B5B522A97AACB0A7957AD705C015655C5BC1D108D67706D2D53B5C7613CB8F3E990F3DB3F7F073DCE234EBB6DD4A223D8E52614F7525328E62578B36345F3B78901420533F966CEB5C60E9806EA761D53A7FCFF633997BD9C721319B60D538B796E4CE07F5C8D52DFBAA506D68DEE15F3D7F35A9AAF9E40A8C04A92CBA7192C095B355BD3706E95A28667FA31F5A32F0A1DE9FB69E41CD615794B3B8A1AF041B4802DF32EF47DAF0C83E1CD205C03E57094800F46F271660F97DC07D43AAD5C00A24399F112295C563C1CAB17EEF2794CAD7F44A3C581917908B0806D9DD650BD5FBE9DA59E60314E7EED36CF2ED90AA99EB5BE69C39BC6ACD37916B0B38398440D26F3091865CDB0E24FD646520B10B0764F6C1C0C9BEA5E8839533BF223F1C56ADDE76CE7AACB89E6466142424ABC0B9C7B56159DB4045FD1BF38E23919EB54768B8473EA10B4238DBB72CA8DBA75C49AE0DAAC6CFF69BE75C3F5C4A55BD21004DA320297315085651969522F3C9D4C91E47E97D12D03113207BD63E8F2BD70655B537FBCD73CE1F36CD3A594EA93822409284308EAAB1DD21E420E0952A2F06550366597D63A3E3EAA7AE2055D3F7C59CDEDCB37A02143A16D3E7D5816282CC5857BE2659ECBF8D54B5E0128FFA96390BB9EDC30F942F33ED97D45335B61C26F7964C3D74B6BDFD1A9D259F22DF96A37672A662DD66C6427E57E6200489FB39B9DDEFE9FB3EE9DE6AD64231F7A236ACDC3260D9ABD65423EE59DADF58D7EFC2BE4DC12580FEC2D05DE45BEFC1251DEC0570D95D90AA49914C5FCC75CF3E03CBB6EC97A602F7D3B57B1DC5F6BEB990D8A65EB99856DF72FE3729769D44BEBDD23E40D870B081C4A92A235DBCF76E082CC9212B87044846653889EBA88C82645446651424A3727080C47B5FEDBDAF3B406D27BDF76E1FEA89F7BEDE7B9F1A6C7FC340BAE540E9AE2F82DF4F087C4027B8185800BCA9AA93CCEC4BAABA6B88DAAE1591AF9BD9B780AE41D49BEC9CFBD710C28B22F25EEFFD0A55BDBB827A7344642670CB10EAE75AE005557DD87B9F70CE358510BEA9AABFF1DEA745E406E05B40473FF52788C87566A6DDD19BF7FEFDC05C55FDE15EFA3D47444E21BE10177FBB1CD8063CFC8E5A1211990BFCBBAA7E2584500F1C3F546DAB6A8799DDA8AA5D83ACFA57CEB9CDAAFA3533FB91736E5185FD359BD99D43ACA229F181F25B841F00C6C6EF97002555DDDB9B932702E37B85F7A70036C05C5699D9EDBDD6EAF4089277CE92C49D9054D5DF0238E77221842DAAFA55608299FD0158232233BCF70F7BEFCF1391A9DEFBBBBCF7270175409B885C0AD49AD9F780AC885C6966796003F0BAF77EA7885C6A66754087AA7ECF7B3F5944BE6C665960377087AAB64565CC34B3C7E230678510567BEF8F070E57D5C7BDF70B8147815345E442A0CDCC6E12918B81E5DEFB05C0D1227294993DACAA2BBDF71F022E14911A33FB01B05D44BE081C6166FF19E7795DFCBEB45B27405B8F053DD739777B08A1268EF3A366F655EFFDA745E404206F6637015345E493C09B40C6CC7EEFBD4F89C857CD2C2922C79BD943DEFB8B55F521EFFD692232DB7B7F8BF7FE7DC0CCD8E713DEFB6344E473402B301958E3BD3F1B9827221933BB4955B71F484B3225847082F7FE6A55BDD5CC9601979B599B99FDD039F7296036302782E87AA0FB0EC6E5C03411F9AC997DD7CC5E001602A787103E164DE2478009C0C7E3C47FE29C3BCF7B5F27226A66AB8095CEB9BFEF65AE4F002EF4DE7F4F44E6013F0616452521229F008E14916BCDECC6F8FBD4D8BF39E7BE0C6C30B34745E4D208C82B809BCDEC15E063C067CD6CBB99FD57B40E579BD956335B0A9CD3632C79A0C37B7F2AF05ADC3825EFFDA966B609982122E799D98F81E9C019C0FC10C26966B642448E049E01AE35B32DC01D218469C09B22F2B1389F6B43080BE3E7CF003522B210C888C83F9AD95DB16E00268BC8A7806F9BD956E0AF0FB4BB392584B01178DDCCEE54D59F8BC889C0ADC076E75C0BF08A99EDF0DE9FE49CDB6066CF78EFA7033BE2CE991441742CB019382984D0A8AA9B44E430E08F22728C997D07D8E69C7B3E9AEFA4AA3E02B439E77EADAAD64DA2430855C0CF81E6104206A81691F7004F7AEFA7023B812D218409C045DEFB2F517E057E1B50E39C7B51559B0031B335C0D9669601E6C671BE1AC77209F0A2AADE02E49C730B81E755F5DBBD402211E84B81DAB8987F03FC14B8D0CC6E57D53763BB1D22322D8470BDAA6E8E9BA44D4466A9EACF8056E7DCD3C0466057D4E5F61042B3F7FE08604C04D52EE02860BBAAAE8F6BD404CC35B3247001704C2C77E0402222A701F7A9EA83AAFA3BEFFDC9C0B6E83F8F8F0ADA28221960BE99DD03A444647154D84CE07F62B926A049448E05D67BEF6BE2D8DB817AE0C5E88BB701EF8DBB10E00366B6B9C7B06638E776A8EA6A555DE19C6B058E8B7EFA55E024337B43555B43085738E73EE3BD5F044C31B33F471074FFDF9553813FC5B9AC0172945FE47A4C55978610EE73CEDDEBBD1FABAAB78610EEEFFEDE633CBB45E42380C5FE0B227241E422AF44F03E17CBBE27BA9871C02BDEFBF7029DD12D77BF9476A299ED8E8B9E032E89EE2E075C162DDBF468996798D9FA1EDCE6251199DE632EFF0D3C79A02DC98CA8C46E6930B3EEFE3E6966BF07DE08217C02C8A9EA1AE7DC45409DAA6E72CE9D0E3C14C17126301E18A3AA6F00A799D98E68358AD1529C6966CF47E237C97B7FB873EE32DEFE92F607CD6CABF77E4C5CFC8EA8E031DEFB84887C1AF85F55F5AABA258470675C9C9381FF03CE32B38D71134C8F203F19F825D01C3F9FE1BDFFA2AAFED439F752B4465F50D5BBA295EAF97E715708E1CAB82000218470A5992DF1DE2743081355B5CD7B7F1EB03DD62DA96A29CEE5B568E532DE7B2722D744EB9577CE9D2B226355F559E7DCD9223243559F8E2EE4B9E842BBBCF7639C739F003684104EA07C95F30911991D3728072A06CF507E79BBE7C9E8532222AA7AB3886481FF885CE48FDD4A72CE6D32B325515B4B44E41B22F20333FB359000BA095F0DD00C8C35B3C7BB151E17F2611111E032CA2FFB3ED77351802922A222728E997D335AA120228DD1CDAD8AA6FA66119901FC2CD65B1777D8EF62EE629DAAB698D90322D22822DF37B32780B522F20155BDD1CCEE8BE0F9A0AADE1842B8275A8C6ED9EA9CBB4755FF10BFBFE99CBB4B559F034ACEB92755F5FB22B2C0CCBE1B09FFE33D22D3A781F5915FFC5324C2DD81C29FCDECFEF879B399DD1BC1BD2DD659292217015F70CEAD56D5574308CB44E4DB22F21D337BB4DB4DBFE3077CDEFB7A556DADB06C355050D58A6FEFC4D0F696487CBF64669FEF7D02DCD7A9B0F7BE4A55B3FB31CE7CDCE1DDBF8D51D5F6FEBE0FB5BEBCF7D5834D07C46462A91B0CDDF927A0B3A77E46DC29B0AA7E3EFAD84E33BB435537302AFB2523F2AA80F73E3918EB332A87204846658823D551158CCA40F2FF31E25F2206F68F190000000049454E44AE426082
);
INSERT INTO YukonImage VALUES (2, 'backgrounds', 'bg_mountain_1920_420_fade2.jpg',
0xFFD8FFE000104A46494600010101012B012B0000FFDB0043000302020302020303030304030304050805050404050A070706080C0A0C0C0B0A0B0B0D0E12100D0E110E0B0B1016101113141515150C0F171816141812141514FFDB00430103040405040509050509140D0B0D1414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414FFC200110801A4078003011100021101031101FFC4001C0000020301010101000000000000000000010200030405060708FFC4001A010101010101010100000000000000000000010203040506FFDA000C03010002100310000001FD05EDC2D045A5A08A894A8295144B16C02A2D28A90545A801520A415002C52442BB12CA778AEC5B145A08001CD229224AD2B675092A5C812C83E74602AA53AC57405B94AB71B7C69A696C888A960922A5568AB4DC0022A849454C40B46223ADB9D38A2D2A2D808312523E74C5B35A31ABF3ABF37467409546B345CA6A599A6561E5CBA82C853ACE7D652E61080AA359AECA7716C090234B208EA6221572D974E3A5B8D90904D6252EA5772F2D99B6E757E759FA62AD732D51BE77E76B72F2D3BCDB9D5B34F9D3CA45B1E6A0A8CAF0ED5773574E75EF02C29A39F4BF1D2129E582DCAD3CA3595B0AB4AF2D92B2D9164D5B8D5F9DDB9ACAF2B06A0894EA258421A811821530423042AD04369862C1A0A9A8120C10C1A5058A2D8A2A2D04505222582C5451682000A8B4002A2D8A0058A2D8A92582D95DCA5896258A259081CEAEC68E7405401960A90AF504448A82DCC505772A2CA2C03E74F9D19402015EB2A414A6C4143112BB16A2B4B108652AC3E74F1280B68640543266975969AB33AD18D5D2DB2DB9B7674655B2AD4A6E6AD660A45B652A0B62453A99378A758949602AB9AB5105B052D8659600ABC5B9AD2A58D2DD2DD35A71BD19D844B16C08BA95D8B720165D2DD2E6E9C895EA6AE5D5A51A5D8D69C6CA8B05896122BE6CA4B086C17356F14EF09ACB17675A39F4D3CFB3CA65962594F4E62A20B258C3CD5D8DDD9D599D3164D3CB7E3764ACA120C1A54AA9359694D1084329084230430CACAC120C38D0CB0240D108608B42C516C51516820144B16E42AD8A8B4A9255B1512858A48025804B215DCAD8A016C08962D2A282A4187CEAEE7D1B364B1550557729642BB9AB784B98AA921A684A002C450194C34D4140557156A02B432C9A540A56BB12E6434D41860CA46879402801623445142196ECDD38DD91617E76F9B0AB59AACA759516CAE96ACCDBA5B25BB3A529D670F4E79F78AB59A8AEC4D6452DB248AB640245284B734CB023458D68CEB6F3DB2E6D669D60A945AAAE5518BE6EC1E4BA6C0CAD9B766D8D3C26B3474C066BD65A6B672ED5D3AA5C3551BE79FAF14D64530D0F35A79F5D7C7B5B9D40DA9ACD5AC26F210592ACCE98796DC6EECEAC96DCE9E5B25B252AB65760B1C32A58B4E30D2CA6821B4C3441821561D583068855A182121288430694162829100100B55DCA8B6002020052AD656C516C4402D2A02097296011052A2D2A42426A494C41F1BBF1B6CEA4A655B145B90AA89A95DC26B356B346B0069A547CEDB34419A849422D0454AECAB595A520734E74A0201A46404795E688030C158A225908915A6A06196FCDBF3A609666BCA96016DAEE6AB16C50215B0B735A54AC7BC51AC53A946B356A25C822AA0A1681A4150321569484653234AF2D8155662AA2D8156C0316CD68CDB65D39DBCB092C41A8759ABA7323453BCE8E7D3672ECD350AF78CFD3963EDC13792302CB33AB71BD5CFAEAE5D5A696C165B3412BD62ADE6534A51869AB73AB33AB25B669E5B25794226A26A40C15151187959484830E1CD60846575686182158304212042418940545A14090002A2D95D95DC8A39A0142209655AC2589412BB00B6220A545B1408B4116C524A2C540A11A5B31BBB1BB71B69A90A0B24A04B20B734EB39F7CF3EF9A6A53AC2D921B3A0B667521F3A32D772B65765772B5202A8D2BE74B066E0A2A0525B9D1024A6CD688D4530494A8065695E2D96FCEAC964AB5120622805CA5259558488CB766A19B59CDBC252594EA5562A0B00B602028921962C64AC09062412500C1169522C20CAF1766DD35A25BF1A155CB05D66CB06F9DD3793AF095AB975DDC7BD99DAEB39BA71C5DF857AC9A8145D4B73BD7C7AECE5D9A6AAD620D34F2C16C5D6490232B4B64AF2BCB64B64D3C425258B6100EB10AB2BC3056047186948C186561D5E1949021084240D12431014B6282820960A805B16CAAC5B0E6C1415667522BB2AD62BD44B9AAC82A57A95DC8A5451505016E545B145B0007CDB31BBF1BBB9F4794CA05240A110CDD39E7DF2AB595B14AF594B1516C4B15012532C0CA9739F78AEC7CE8CAF9D195F3A596CCEC28148343CD2D8B63CD364F35252B0282E4B411E68C5916CD5F9D3CAA808AC3C28A322AD7657A82C442AD15D537346A5772B556B296282822D280212245921A802504845881620A8008620071E5B65BA6B46756E695A8AB5879AB4B475A3AF1AB7CF4F3E9D0E3E9B33AAF58C5DFCF9FA735B16C3659342CB73AD9C7BE8E7D5513593350845884946C794CAC343A9578794A909282420CAC1196C8652420C30F29084686571D5E0914A453442120C1881A02A2D2D8A0022D04512C5B1522C8040CA6582255ACAD8964956C4B33EF15EB2A805B11168228B62D8A040AD065BF9EEEC6ECCEECCEE4AD0A0208946F18FAF1A37CD2C5B12E4CA66A8D73AF59A77869A6C6B375E465B71B4B2BB982865D1CBABE77663415F356A4A960588E09A4B1F3AB33A79A6820B1359160469A794CAF16CD5F9D5D9D3445059114230F2A5959558EAA946A2595A2525CD7625A96258622A6B15D896048B100CA0082A0280A04164A522C66291A589032D8B7E6DD34F2964CD2D2A3CB6AB96CB29ACBF3ABB3B6945CD7BCE7DF3359F7CCD9666AD5B8DE8E7D4CA28812BA04A88C32BC32C1C695C832995861E228B205591D5A561C2A522C1861C21530468657561E0914D842A42418304940545A5B145414A044B1516822D4830032858816BB908079A52AD67374E494972882ABB94B0005A0812BB028B0468E7BD5CBAD99DBE756E34D35044AEC4B33EF19FA72CFBE75EB346F156B260CB0ACA3A7309773E99BA73156634A856481512DC74BB1AB31D0CD19545B94B135258D9AF9E865B71D2CCEAC8328B9AB517598045B2BD61E6B4E3A5D8DDDCF7DFE5DAB2A4BA5AB5151E5BE505565562A15067B9AF52AB154257A5573004476A9B9CDBC57625CC596496105B95A8440B5D8A8BA041425163054C1244ABB2D39E964AB625810CB64D3964B64B64B6CB6CAEA488D445B135141619486592AD8CA292E52C232AA4A62C94856C8796C01148C3164A4841958B25B256053C4A81183069A594608C32B43AB8D10944215608430494A0B145A54545A08B4A8A8B62D00248335002A020A1801952CCFD309735DCD7A922BB2060AD572B64336F09601E5D3CBA5FCFA3674D356E75666B4D2A55653BCE7DE33EF955739BAF2AB58AF52AD6202530A57A97F3E88CAEA3E682AB2BD65F3A8496FE7D6FE7D0E74895EA2DCD3BC26A4875BF974BF3D6FE7D2DCE9F2155EA0404B2BD67275E26B6F0ED5E37C7AEEF9BD5F5BF2F655C59AB6756391DB1CEB2F958529B2A4A351016516576536255765910AAABB16A9D6336B35D8B4488B616822B29A8120CD225565762D8000B2C948A048AE34B6CB64A9656856C1E5B25B2698796D96D9AB6084342C84228B24B080B1688B625902102CA28CA471E5786581186836BC34AC41CB25B6579585A71A51632BC42128511A08C32B43AB042418653042109280B628B62A28B4100962A2D8B4114034AA04828B600021A582595DCD7A8A808290915EB205B3274E756B1069AB71A7CE9F3AB71B2B76376E3462AD29D66AD6336F9E5E9CF3F4E2B555CD5BCC8B31B4B932D99DA905B264B653BC8417333BB31A7CF4BB9ECC2557ACA6B35EA2DCBCA0B33ADBC7D3A79F4BF1A089625828C0AAF78C7BE5771EDCBB7936D99BEFFCBE9FADF0EB8B9EAB9699716A756E6DDCC32885AC5ACD425516576295D95599EE56D72BB33DCD5655A956B35D8962D1834116845762D9088BA2AD5715A2D81420A6042581651920C15002C865B2561CB33AB669CB26AD95A0A9B2BB08E592B4AB4B4244D45A2B116C8ABA808156432B5343AB43046561A0AB8F2BC11D5E2C5788AE10AB439081254084230CA55A47521186530C120424A5022D2D8A8A2D040258A8B4194A5A09255B1490116C8A11F3A7CEA2AA2257ACA594EB29A8D91962A5CAD80AEE71F5E75EB203061F3ABB1B69AB71BBF1B055ACE6DF305B8DD5ACE1EDC152BD66BB969A0B4EB9AD9A3975929952A457A9474E546F9C2DC6ECE7D0E6D99D8058B655ACC08481977F0F4ECE3D5E55B2BD45A30F28B28B3C46A649A794596A7678F6FD21E1F51CDA79EF0E779759AACEA59D5EB8C78D29552C67B31EA64D4A2CAD336A5165929326F39F58AECAACAF52AD456450B20B4AC85AF5902DC99529156E6AB24058CD7A0022D01420B4C92C3341196DCAC69E1CBB3A7698686568656168A3164ACB22BA095EA2D9024B40B600DAD0C3055A465608CAD0EAC30F2B4AE3430C32B29479582AD0C10D34AC10842AC341561823055830C109024A5022D2D8A2A2D04822258B6045A4A0804414A8052166756674D9D34AA04AB59AB594B1529DE29D61F3AB71BAF5954697275E593AF3162A050599BA39F46CD319BA73AEC5B245B9DE8E5D6BB28DE2AD60012BB28DE2BD65F36FE5D8CA0AEE6BD673F4E6B72D9D5B9E8A4839A82D14B33BB2696C21826AE5DAFE7D6D95A16C4A85B9D04F0BD79F0B639AE450CF4F9F4FD2BF33DFD74C78E8BCF6778E667596CEDF4E7D5E99AF36BCE94CC5660B316B38B533EA20C029D672EB15595557A8960484AAF528D612E62AD80AF58504D2088B4962A3958A89494960258D29945CC58AC8F2D92DB2DD2D92D934421523CB6CAB62D84841296C228285902B16240DAD0C11861A5232B0F04756195A195A1861E5232B0D0C11958232BC11830CACA46182344A61A560842121280A8B4B628A829520A25896044B168228A8B6016C50C5B8DDB8DBCD195522A58A95EB29657714EB315F3AAB58A37CEAD4AB58AB512E40B4A8B6196CCD32E6DF34D4848697670ECD9D012C0AA94EB1474C57ACAA34D087CD92D7ACD7ACB4AD9D4CDCFD71246C6C282CCDB73BB33A5B1D5A68CB19D5CFB5F8D9925AD2D5AC5727CEBB6306A8664DE88AEA84B737EE1F3BDDF57C693368E7B49AC39BCF53674358EDF6E6458C99DE72830EA53667B33D95D8955D94D95D9119619EE5A9E5955DCD5488954EB34EB39EB3DC51A89625897308B558A8B6054B95B0AB4A5421229465B62DCEB44B666DD365592055A5B60555A91156AD652C94D01650B2120A9A2195861A18619A608D0CAC30CAC3432B43AB8F2B411861958234A69A18682119586561A0868855A1862042425016C516C5150504001112C5B16C02A2D2A2D2A280696FE7B7CEDA22C8828B65560B2400156B39BA72CDD3951BCA5C8B14857657ACA322C0AA2A2EA0902B469E5D6CCEDB341250222555BC2A49406696CAEE535890F9E8A946F98A19A669A5D1CFA599D34D0B9795A69A58BA31BB71A75804F2BD3978CEB9AC95516CA164B55C997E9FE1F5FE82F3764CDCD8DBE9838F44AC8986BA1AC7A1DE2CE924519D512E42944ACDA992CA352B4AE92C032C4A2C71A0A8B2B28D66BB12CCC9CFEB8C7AC55ACA599F795428AA962D416C5058061A5694CA10D3164B6CB74B64593564D3CB28A34AEB04B16AAB94AAF500484A905659158215691D5878769868608CAC157823CAC3AB4583CAF0CA4230CA608C11948421195861E092D3232B0462042425016C516C5450500201116C5B141628A8B4112C51F3A7CE9F3A69644010028A85640B2AB33EF9E1EFE7A378802BB9AB79516C56569500A8B60A50201F3BB71A6CDAB52DC69A53347352824964B56B09625956F9DD8E873AAEE72F5E3269F1D2166756634EA556E7463A5B8DBE750B26ACCD65E45E7E5BAE3CAF5CDA34A9650BAE5B2322B5CB4D7A3F3FB3F41FCCF4F7F2496DDE7273E9A779F3D9D26359B52F83ACFA9EBCB46A579B9A6B315C67D4C7ACE6D0202AA51D2A2AAB0201112ABB29B9C9ACF3BA6316F19F591655A94EB288008960B420400B4A3432DB9D422158A471D5E4B1AB21A6A23B464BA699420AA752AB2BB16882C694ACB0055830C3CACAE38F2D8AD0D0688CA619587861875B25B735C7504465782A6884694D10842343042A50AB0C10902121280B628B62A0168010088B62A2D2D8A0402D8882D7CD6CE9A532C1502C404942450956B34EB19F7CE9DE29DE6BB84B12C5B141497225162D8B22D00588900B20C45B33AB31BB31A036699A4B12C516CAEE52E62C4A759A779B79EDF1B0AC3E74F0F34F2DB9DD98DBE7442599D716E7E67E9E49555CD2B74B79A25BB36C5A6E50BB1BFB37C7FA9F42CB4E355CBA2CA33A95D1F470F31C3B73B58E6598F474B93D5CBE9B79CB3598CE94553ACC5A92AD1022257630655B104A4B28B306F1C8EBCB16F29722CA7594B052A02BA082C00588F2B8D2D8A25620B60420A88ED3C3AB45B3574D69C69D5462BB336F19F52BD1A086C8A564868CAC30F2B964D38F2B8F164A4944234AC3CAE30D2BABC3164A494D0EAC15211E0D10902305610608C1084240909405B145B1500B4100A258A8B62D8A2A0A5402D920CAD34F9B25516E56A088D2801233EF14EF9D1D3155C55B8962DCA82C545052A04516C5058054551721416E35662C95F3A2AF8D4956C02A55A95EB20AB596CD5A32B6689A44B26AEC6C97676D2B4AD2B4D48B65E749E0BB6306AF5379BEF3E0E7A5A397C669AEAAF76F2C3583975F5FF2FE97B7F377F6DCF5D1EFC125E7F2EBABA73F3B346393A9E7BB72E76F15D5F9B663A7D1267D76E532D32E54CB54D8948895002A4516542D8A298B78E4F4C73BA73C9BCA5C84AE92C882CAB4AD1505908A4B25BA579A90B6155B95A512C152CB20CAC3ADB9BA26AFCEADCE9D5E51664DE71EF15E848C968914A3CA465692C5796C5B22C96C95871A521588461E57571A575620F0CA46186523046821B4A40AB10291582304242048125016C516C545052A28B62A0B1691022D2A0058010658AD9A05B12C90D2A049280253ACD3BE797AF2AB594D6545B145B0228112C00B145058054516C04492C2CC69A6ABB97CEADCE84AA3E7522BB1680D2D98DBE7619554B0CAC34B667514CAF281E5CF1F35EB9CBA7A2D636DC7313893A74572D9D6CE96BD673CD3AC613CE73F47D17E37D9F67E7E9D8CCF55EBF359D7197875E270ED6F5E5E7F3D3A8C792EB9F35E8E1D93992E59726F3DF93E89CF7EB3532E3795321552A4A0282C41694528B326B3C8E9CF9FD319778CF73140B60B089667DE69B852014A58B64D3C34B152E6292BB94B2BD481085A832592DD2DF9D5D2BCD5B9A4CBD339B594B028A210A98718795D2C9AB16CCAD5B2578232D92B2B44A29148F0CB62D92B41A2108CB1195860842182A421A2408460C1A242102125282C516C545058A2A2D2D811691058A2A016C52009008012C90F9D424B0505946B9D1BC66E9CD3595B145B145B022A2D04516C02A0B145402D805A543106CD057A97634AA0D1CBA5B8DD5601A6ACCE9B3A6CD8A012ADCC0964DC95A57CEA14C9F35EDCF3DE9D167A9AE7694E6F3B57D5664B8AF3D1194D4F336F3F1DBABE5F57D27E5FD0F6B89D597B06CEFC2CE99E2F97D3CC8C32B6B3CBAF35EBF375230EB3E3FBE02F4B8EFB3C3AFD6CEC74E692E7CEB215A29555172B52055340C5672FA6397D3965DE68D612A0082D84AEB36B156B2A909630F28504469A172D34417294A0A600B44686B6CCAD9AB62D9ABA566A9D62AB12C4B56C8A50CD3215B15E2D8B66AC9592C96C95E5963CB64D38F2B8CA016018B25B0794842B2884608C408C19582408688421090240902125282C5A54545058A2A2D2A0B145B145B0228B628100B4121254B2410020AC8057ACE5E9CA8E9CD516945B15052A012C08A0A545414A804B022D805B16059140A86532C8B33ABF9F410D34F9D983299A7CD019A912C334D2995E6ACCDCE7CF3BF3E5EE5D8DBDCB6A6CCD29DE654E6093766B16D895C3C74FA97CAFA7D2F37A7173DFAFC4F5FBE7AB79B378C5CBAF9BE7D553916F07BF0E3F7E34CD65DE337A38FA8E5BE44D67E5D7E8DF3FD7F46F77935EB2B1931BA4AD3299B7940948959ACC5ACE2DE706F19F59AF5800A544A5144D66AB2BB2AD642148A55516A4105590F3610A2D4B20156C2AD129C695E5B0B65B6552BB1692A500842ACB645B9D5B164B64119A646571E5B65756872C956A1094D160F2908435148C442108EA432B106A304350306A102109024A5058B4A8A8B41145B145B9028116951692C08A040AD2AA26B296024258008D34D9A2C92E4EBC73F4E6A2D8B628A82C51516C02D2A2A0B1505AA8A0B15169590A11416286530032DDCF714CAF9B2532BE75663696599DBE752C7952C8B763A0C5F99F498FA4CE6A8ED6B1459BB3BD8CF08CB378D7A06ADE161A30E3A77BC5EDFBFF00CEF5F2674C7CB766B9FB3F470E7F1EDDFEDC79DC3AF9ECEE4BE73D1CBCDFA3CF856AB36F5E5EAF974E65538D64E1D7DF1F43EFCA15E6E7CEB2267ACBACD22595D549875316B355956A55734EA2D8B60B11116BB2BB9AB52B4AACAF50A45328B98B08B12438C3CAB62D0A002586594D0D515A1E571952A002152118796C96FCDBF3AB1484811860C58B62B4AC5AB08120425929A62054A1564810855861A0A9188312890840842120494A0B169515168228B62A2D80545A02D8A8B520007CE9B356CA358AB790044B12C0016E4065AB58A7594D408A2D8A8B600225805A545B0228291900A516E5402DC81524D44002249A08F9D183376669CD042CC6DE68CB767746F9D5ACB635E2F1DF8BAD591657A44EB3995DB8D79CB9E05EA0DA5E9BAE14E3CD60E7D7D5787DDF41F99EFEA667CF7DBE7E27AFCDF4BF9DECFADF5E59B9EF3F1EBC7C6F972F92F67973FA38BC19AEC5989306754F1EEB73D1E5D7EA1AE5E97D3CA4558B8B373565D4A2CAAA9B32D992E736A2D89657651ACA5041A95A255694D556556258B6041634D148A60CA694963C1B511351414520481580A230D0CAD28A2AC86560ABC3ADD9B7CD5D9B629522A11C84A23CAEA4B22C95A804842C5782A43442108421183055823128C1A2009023102409294162D2A2A2D04516C545A08A2D8A8B411523460CB0109653BC2D8A95EA222DC822D772973152E52C1628A8B4A82C515058A2D8A82D54565680A8295145B00116C0800D2B304B1423669CD69642D456CDB33B54B26B4F2EB46B193AF1F23C3D9979FA1F53627A067AD9C6730CDEACEB9FB9A24E7B5D04E3E8F489933AC18EBEA3C3EDFA4FCAFA1774E7F3DF77938FEAE1EB7C1ECF65E7EBF4ADF3E379BD1AB58F997A39727B73CFD31D89304D6B8D99D737976BF37CEFA7CFD7E3D7D4F1EBF4AEFC35F5C0CB3E359232599B529ACF54DCE5D29B9AB52AB9AECA2E6BD20A896556295599E911745416420023E742CB33A955EB04444D1591503691A229002C8AC120EA55E18695C7948F2DB2DD357E74E10A846188B2C811A57195CB2228B20C3CAE32908494424182108D2908434424204210902121294162D2A2A2D04516C545A08A2D8A8295168CB22020595595EB211292E56C4B0228B62D9212C4B16C02A2D805A54545B022D012C08A2DC855B00A8116D5655052A00509559162915A24B6E355DCAD494CA654D664B672ECD879B9DB9F9F45F2F6D8E949919E5CE99D5A6BA1734C72AEFACC75A4E06EEEB8E84CF9CCF5A7977F79F23EA7A9E3BB66BE4FF53E7CDE3E9FF1BEA70FAE7EC1CE6F3938DF91F579BCEFA39742CBF3525A4D7E5F571ACC3D79707D5E7EDF2DFADF2F7FA6F4C6FED89032CF358E3299359A2ABAAACA6CCFACE6D669D673EB22C02502B2BB28B94145A5A0882D91421554BA560155956B0B4B6118694AB2921112C2B1491591D6C95E1A571A561E6AE96D96D530C32908484A2A428CA461A19610356438CA461881054084211A521A2183448120483102121295052D2A2A2D04516C545B0005B11058AA1006508052BB94D45B14544D652E56C500B62A28B60B14545A5B9028112C0896455B1515052A2D045058A8A2D82058A002283412048D366B66D7AC2519642D02AE7BF2DE4FA4EDE9B9DF9CEC298E55D19ACD7233B86BB322F419EC1DD98E669765C86B071F4FD3FE67D0E173EDE8B8EFB3C7A793F7793C8FBBCBEDBE57BF85EBF37D43CBDBDC77E38B876F9AF4CF2BD1C7D0C2E6F2E5BF3AF4BE0F673B53E4DF57C19BD1C3B1E6EDD9E3D3EB0CD79D7A4EB8BE9737162E35C9AC829D4A2CAACCDA99B79A359CFACAD8115552BAACA6E56C54455A512C4B228922A86E5D4CB5D94EB02824AB2579A230D34500BA856210AB4AC592D934F2C423B574B6E4EACAD0E58A4202128046212987948C008EAE38488C1595031288C49484235120C409024188120494A0B169515168228B62A2D0402A25045A82A1964A12BD45451359544D6544B94D45402581168228B62A0A545B1516C02D8A2A0A5408B405656941628114160145B91029522AA42125545B042D289CF7E2FC7F53A1CFB3DC0D6322BCB65826BAD1A64E5EAEFB3433B23936EF4EA673E55D3566F25AF45E0FA1F62F8FF4316E6B85B9E07A7872BD1CBA3E7E9CAEB93E7F47B1E3AEEF6E561E7BD1C4473B1D329925FA8F93B79FEFCBE77EEF2ACD70373B7C3A7A0E7ADD99D497E85DB1BB532F3D639619ACCDA99759A6CAECCFA99779A6E69D64002B5DCA5A8956B2A9582AB2816C245082951E532AD956A2D8B72C599D38652B64D30034B64469A72C948CAF0CB025B2D92B964A461D6D95A250002C830C45840D418838CAC3049730240A9194C1521086884240902408C408484A5058B4A8A8B41144B022A0A0016C4402D803299552BB16E52CAF5950580AEE535048B4B62A2D80516C541628B604516E40A2D8B601516822D2A00228B6045A102C540AB600010010E74961956D4B8C9E7F4713CFEDE7E3B6A96DDF3C55B12ACEED3B0644EB17B1D2CAB2AB2BB70CBC76A8CEF22B4BABCFE8FAFF00CBF6F731AAF1D3CCF6E5E53E9FCFEB78BDBEBFC5E9CBD71573DF5F9EAEE99F5979F91F679EACDE6E3A21C2DCF7BE4EFB77CFCB77E58359C8D5B2BA70B78EA62FB9E7AF71D33D2D44CDC79D644CBA98379A35095D94D8962D2A42BB33EB35D96CD54956A5762550CD36290942CAE92196C852AB2BD408C59356CB14A5B34E40C2D2AB16964AF28258F2B2B0F2B0E3430EB64B6CB06A5220A8408E4594105108C329092C244161561A561A52158108689021204210902409294162D2A2A2D04516C545B000545A545A80882A2D8A044A4B981952CAB58AF595B1516C516C02A2D045B145B022A2D01516C02D8A8B6016C10B6016E42AA2D0022A000B628122AA363A723CDEBD9264CE86F3E678FAAE9A49AD7A9E92F1E7DD5335D2C6D758ED67356A678E3CE984D766FD630E752DC735766F21557A19BEABE7FBFEB9F2FDDCEAF1DE9E1C6F5F9ADF3F7FA8793B795ED9CB9D69D73E5F7E3F45F17A795EEF2D59D72B1BC567A1C6BD079BBDDD78F07AE326F3AB3B89E4BAE3A18BCFE98D12E98FA9E1E87731E778A4CBA9837983164B4D8B488949667D668D4546969D654A759A4A752BB144B9517444580B6205AC4B169E1DA7823ADD0EB21960AB10AB0C1096CAF1629182AE34B64B616CAB50091459081AB22045A5A8118210D4495125120C34ACA430548434481090210848109094A82969515168228B62A2D800894A8002D8A8B41148404016C7CE8156B146F09626B29604516E545A5B042DC8A500A8B72B6282822A2D8A8B4216C08B6055642AD8A0B95816050290AE4F3BE3FA6787A74D694C1BE7CBCEF5E74B5A75CED679F9E9D097A395FAE64A1794DD265D4E85CF753748ECF9CCF5E471F466E980B773DFD47E27D7F6DC3A26B3C5EBCB9BB9E13D9C3EB1F2BDDEB7A72E457C8BE878EEE7D2F4FA1F1DF2FA6786B7635D7E1D7AB8D6DB9E476E5C9EDCFD3F1E825D678FEB3CA7A3CFEBB874B572E6FD6235D664AEB3D8EB645667D4AECA2CAECAE952AD152BB12AAB2BB29B9A6C4D4AECA912E56D4B16503441556C0329871D6D5B21C595958205030C592B0C592B84832B964AE5D9D5B02ABA4B010951195C20022D1A81092A0420A306821182AC194D1821A21090210848120484A5058B488116C028B62A2D805B145414A8A829400402A45328908A2D95DCAD55AC55ACA6B2A8B6054B058A045B95022AADCA82C545B0005B1454162D01515052A00228A82D08A2D9C4F37AF8FE5FA1AF37D7635CAEBC7A733E7E6F9B755D9D6B8E6CD649ABA3AC2DCD32F40D364B375E55CDEFC16CBCC3E3F7F99D6B0FA3CF42EFF3F7F79F2FE8FBDF374A26BE3BF63E6FD3BE5FBFE7BEFF001C6FEBBF3BD1AB79F09E9E1E3FD9C126BE99F37D7D5F479FCCCD9C6F772E90F4FC7787BF2E177E1D9E7AEF675AA3A7D6786F472F9E74E7EEBC3EAF3ED7B0CCFA3FA78D18AAA4018ACCE9975336A215D90AEAAB29B2BB2AD444AAC4B12CA6CA372AB8AC42B1462C9A84029475795872C945815AC72DCE9A5840AD85B2CB1E5350030D2B8F2BCBA22C5A52AD024B624186509280B6120495288431281035060C152108C1251084240848120484A5058B4A8A8B41145B1516C02D8A2A0A545B140045A545B24194CA102AA2DCD7ACA555AC57AC844A02DCAD2A016C08A8B605545B9555B90016C545B00A82951400B1500A0169590B9B1AF2DE3FA9763A75B0F499BCDD73F2D7A21B3591739C5973CAD9DAA5F66DB2F2FB3BEE7C238F9EDAB58D39BDEF27B3A3F23EBE2F4797CF7D6F99866F7F93D3DCF37A3D9793D1D6C6BE6FF43C9EEFE77B3C37BFC7D7C6FD879BB68B86E99F29EFF1F97CEFBBE3F4F6AF3A773CD2FA2E1D32F4CF6B9EB6DCAF5CEAC6B5C9D6D4F43DF960E7BF9569E87C5E9F35DF99DF3F4BD39FD096BCDB752092E7973C516536574889555556225754EB35D53625CAD57739ECA3528B2B4502BC32B4592B2C1C85B29514B7265B0B96C883116162BC3AC004832DA592A862C2F956AAB12A1084A00B004840D8481A8A50CB2A5048108548421188109021A9068902409094A0B169515168228B62A0B145B14541628A8B6016C5A5024962000654B2BB9AF595B94B1116C14A8B4B60402A2D8A8A2D8116C025805B022D8A000A8B6016C540A11516D5B048B9DF0BCDEBC7C3D8634CD56994EC2F718B2E38DA9933D39F359EAD8AF4E8DC3C7A04D4E72CE1DDB4D69932F0F5FD97F3DF6795CBB7CBFEBFCEC7EDF164C75E947B8F9BEEF67E6EFEBBB79FC5677387A327B3C5CDEB9FA2F9FA34BE7FBF0F31A6EF3F7CF9D5E276E5E4BA67B1C77D6E7D1359EB46FD674CA53BDBCEDEB8F3FC77CBE7D5F59F27EAF2F83F6F9F1E3A7D83C5DFD0F3E9E87539F73CFDE7AB8D689AA8CE94EA5653A958B49656946A2253A95595589A996E68B2B12E63521A572C94974B62C0962A80163C58BA2698AB5994D2C2CCDB16C96BA88C30EB64592A92C51C840542004A5A5B085006D88C152120484A012540842418210902125486A812102425282C5A54545A08A2D8A82C545A515058A2A2D045B169500008A0B0CAA55AC25CAD044B022D8A2D805B1408960058A8A8B4116C0AACAD045B1402A2D045A08A04545A072BC9EDE271F5D6D5A6993B19D3CCD36F2359ECC99D9674EAE2F12CE46AE8B3D4B9566A4547A32B463B7373F47BFF0089F67DCF93AF97E7D7E35F77E57A9D638F5BFCFDFD0783D7DBC5F5B9556E6B9F9DFBFC7DBCEBE95CB5C93E6FEAE1E87C7EA446C6AEABB78F25DB194FAE79BB78EDCEAE2DBBE7D595474AFB73F2FBC5FBC794F472F39DF9E0A3CBA7A0F17AFBBE6F47B5BCE8EB8E17A387571AFA3F2E99F3AA4A4A6CA6C4B16A9AA6E68D4AD12ABB33EB39B59A4AEE5556A43C58B74D124699ABA69A2558B5A55A945C32DB2DF2DCB155058EB7674E110830C39060C1AA43632C4652295D95D2D828848914D1860D19624A012540903502420C1084812510C4A2420484A5058B4881168228B62A0B145B145414A8B628116C516822A0B14019552AD616C16258A045A545A08A829516C0220B14162A2D8A0B1454160554545058B62805B04AACE7E5DBCD79BE858D6C66FC3573D5935C6DC5AEE5C73EB4675D7E7AE1E99573EE6CD73E74BD9B9B19D718284AD9D51CBD1F62FCEFDCE0677ECF8F4E5F4E5F36FB7F33AD879EE99F41F3FDEBACAAFD4FC9D46F36EB3E2FD9E6DFC3AF4B974DECFCE7D7CBD778FD3977CEA2F9AEB5CF88F471E7CBF4DF3F5D3A9C5C6CEF9D9675F1A1B9E07DBE5F39E8F3F6112CE56DC4AE6F3E9B3CFDFD878FD5F48E1AEBF6E5E0BD7C72B3F44F3F4DF9DEDB2FDCAECC5358E2A33EB39F52A4AEA8D66AB33EB34595522148A57466EBC6EC9596E96C82AC0184B28D4CDBCA25B2DD3574B6C5B2CA83C315D2129CB62C5AEC03284AE82109288B55A252D0258C4204811881516409035024A24204215244212049502420484A5058B488116822A2D2A045B1694541628A8B6016C5B1408A0A5404015DC258B62A2D805B14162A2D2A0A5402D8A8A8B6016C5022D2A016E42ADCA805B150004AE2F9BD3CFE1EDE9D9AA67CF67AA67745BB18B17BDAE3AA67CBDD599EBB337AECDB279AED8E817B2A94CD5157A73B3D31E77F40F8BF63EA3F2FDDE7FD1C7AFC7AE6B8F907DBF999B53447BFF95F47CCFABCDDAE7DBBDC7A7B6DF1DBD39F9FAA79F439BD8DE797ACF76334B98EC5CD15E4ADF0BA9D8E7AFA0F0EBCEDE73A4D4D173D997675E7F36F4638BEAF22DCE4AE52F9DCEF2AF438F5FA2FCEF6FAE63D2FA39780EB8CF9BED7CDDCA766E7774CD9491518179F667D4AACA6CAECA6C4AA6E6BA78B25DB8DECC6AC96164B60B404B002C42BD29B0842B6C6987500B1A520A4483ADB041402252D04AE821A00A445A5A0120420A89065885482C2120436C0A4581092890212212109508121294162D23205A08A8B4A82C545A545058A2A0A5414A8B628A9092AD8A89ACA58B62A2D80545B1402DCAD2A0A545022D8B62A016C025C8554028B722951505AA8A80A25F3BE3FA764BD767CFE3B6D97759D399CB2CD6389665BAEDB9F139F7D12DDAC75138FB9B2E6D939ED740372B2E39A5E3DFDCFC3FB3EFF00C3EACDBC73B78F37E9F3F33D7C387B9ED7E7FB7CEF5C65F470FA17CEF6DDAC59D7975F337675826B31BE4E477C7A6F3F5741D3341D4CD967CB7B633CD7BCF3F4F47BC64C6B99D33CEDE3BF9B7AD72F13D7E7F29EBF362D73C6BE41D33CBD1E3D3BFE5EFE9F96FDBB3CCEFCF8FAC7ACF3F6E474CF639EB7D5F2F0759CFAC6F96F2C5A6A8D4AACAAC8218359A6BD6F9BB5CB030565805B215D554B511296C8418BE5B6580A4B1A580B0806182328B21512814EA04140940445A8125408015120548C4A8108481090950240D409024484A8408024A5058B48C816822A2D2A0B15169408B411408002D8962A00AA5CAA2D258972A8B62A2D041628B628B62A016C08960151696E40A2D8054162805B1516C50205A8F3FE3FA1979F5E89D88B63A79B953976716F4EBEF9732A9CEF2F2DCBAEBB3D4D72D4CD5ACD651A974D6D990576F131D3D5FC0FBF779BBFD0717276E3F33F7F92FE3DBD2F1EBF34FA1E3F63E3F4F17B73E577E5E8FCBDFB5CF5D2906B3DFE5BB73AE24DF27D5E7D367AFF003766B9B6E6B6B533E87AE3C6B5F2AEDCFE81E3EFEC479793ACE5D4B2CF4FAC8977F4C726BC07A7870BB70F31ABC7C6F5F2E9AF37D679BB75A6BA51E4BD3C7BDC3A794F472A758DDCBAF499E6F6E58BB72C9A9A737B5CFA5F156F39D2B509452D8B8DFD5FE7FB3B736890745D541488B62528280B4C18356C32816C5188A2C21404B48C016C50156A5560A80202951494481A8292890810842108C1A842101441460D40912109508420494A0B1691902D04516C5408B62D2A016C02A28295058A4151408B4A89A8972A8B62D2A045A08B628B62A01516C02D8A82C5A545414A2A2D801620002DCAC2D9C7E1E9E3F9BDF52497B9269AED62E2AE7D99753A379F0B3AE7E3BFA14EDB37398D4AB58E56AF5B22557164D096B3C673F57D4BF39F7B91DF9FB6E3D366B97CFF00D3CFE8DE3F466B8F90FD2F27B7F375E276E38BD18F7DF37DBCDDE79DBC6FC6FE85E6EBC5DE7C2FBFC8358FB2F8BD56CBE72364515E83374EA68D67E7BD279CC6FD672D7ADB304ABA8A896756CE86F317979BE7BB73F39DF8F81EF8E26F15C7538F5F51C3AFA6E7A5D4E76F18378E5F6E7D38C3D7972F6E7D135413B398CBA25E7AE4D65A5A759AB3BFB5FCAF777F1D2D52145A451640228B62DB10854A32B8694AC03D419168A4554348A494A8955595D8B60500B20B502420680084A2108C40842310240500540849510AC48459642108125282C5A4408B41151695022D8B4A805B145414A82C5140180258B642BB9AF595414A8B62D8A2B22951680A8B6045A565680A8B6282C58162D8A8B4050225C86959F3BE4F7E0E3EACF73A2ADC6B2CBA97D2B19F59BEE3CE2F2A6F6B5A19EADCF77389653661BAB332B5C72E56D65CD5470F4DBE6EED77F60F89F47B5D39E1D662DBAC7CA7E879B3EF9CDF3F65E1F67439EEDB1759EF73E9D297C9F6E5E33DBE4D9C3BFA3F277FAC2722CF2F374EB1F48E75F533F2EBCAEFCBE76BD7C5F59C3A757D1C79D8DA5259B13ADBCB573F379DACF2FAE399D39F9DEFC78FD79F12D0979AA4BCC9BCD5ACB4BD6CB4CD733539E7375749D2CE7747431D285E56F17C586AC74F65E0F677B876EC9754054145414A82824B49061D5AC8B111511A88D640A0B551141125A88A55A556258120B4050D409095084084210D1090231084A8282A51821A842245882A102121295052D23205A08A8B4A8116C5A545058A2D80545B0065545B00008B49735DCAD25CAA0A516C541628116C5A545402DC816C5B145022D8A8B5051516822D9971AE3F0F565E1EBC6B65CAE74D9DE1B9B5AEA33D5D72A2E7C9CEBB0BA4F43AC74339CF5C7D5A972CD24B714E3471BCF6E649CFA0D36F2DE8F3FA3ECFF23DFE7BAE7B767D135CFC0FA7CFE33D7C388D7AAF17ABA99CF23D7C3B7E7F47B8E1AE3DB83A72F97FD0F27BCF9DEDF27E8E7F54F3F4F71BCD967CD71BECE27B2DCCDCBA6CF472F0537CDE5AA39F4E933E925C7BC6D2D3359D4D4E759E73BF2F3DE9F3D3AC57ACF3F4F2BD33D193358E97896D818B93A98DE3B39DB96675A25A93A11A73A0A21C432E75F64F95F4965BACBACD364A02AAA0B4201496157B1C7A881409408868A41A80A54A5050115112ABB2BB052A4B5514944840D42102108421092988120294142A0468350810012542042425282C5A4408B4111052A045A5B15141628B601500122C85B2BD6540012E5512C4D656C02A2A2D829408B4AC8B422A258B601516C08A2D80545A02A2D03266F97F1FD339B7148F73831D2D9BA2CB5365C74F7CFCFCD235B64BF7CEDC9B3BD1BC2B391BDECC979D8D73F9FA344BBB3565E1D7638F7A72D3CFB7D4FE67B3CE7A787A2D67AFCB7CCF479F04E993789BE7C3F579FDC78FD5EE7CFDB9335D0B9C2BF28F7F8F0E37A2CD9C3B7D73C7DFD3FAFCFC9AF9B675F50E3AD45FD716EF3E7B974C3C3AE3B3B1156E39B5394ADACEAD678DD71E53D5E6CFD39258B592CF3DD79F21AE9A767065D064B30EA75F16D9A3289684C5BCF4336F9A694466D32599F1BF53E3F47D17C7EBBECD766BDC34B0962AA90505915AC6191AA5004016906A88C4A020B440014AE94AEABB152501282405409020A210848108481A2420294150210909508041508109024A5058B488116822A2D2A045A5B1408B4115145B00008B411481956C111519A778AB785B16C08A2D8A045058D9A0AB59AF595400A54545A1628A82E55422D8A71F87A39FE7F7658AE6ACDE1335B1D6DB26B18336FDE7A7AE5CBCF4AE1E5D77384D5A77AF1AD50CF37D7F3F6C2D69D6359EA3C5ECF33ABCAEDC68E3DDB3AE9F1DF6F9EF87F43C3D3F2FA3D8F9BB789F571F45E7E9577E5CCF579AB9A9E3F5FB4E1D3A5AC7B4AF1F9BE5BAF3F99FB7CDDFF1FB2BC6FD6797BFA7E9C7DD77E3826F99C77E8FA7312DBBC85F37E7EB54695D1ACEBD4A205505D6713AF3F27EAF362EBC96C5A5B2BAE0EF396CBE4DB2EBCD5B2F9A840CB42E1D45B9DF96BCEE4B499B538FBCF02DEB797D1F51F9DEBF63CF7AD7A5D316EF0AA8A0112C4955451B186B1A820558940AC81B08694505400A28B4A574A8952C2AA220A04460ACA01A88560520C4092A1080A0009035000A84404A03042409294162D88045A08A8B4A8000B14545B16C001516820002135020021CE8CA0A37CE8EBC92E545B15052A022C8915EB29ACA58A829516C540282C51516E40B54E35E7FC9F47363A4A8BA59E6677D15F41AE26E38DABD267892E09D33635A8A2CEC6F9F4D8B0E5986EB6677DDE738B35E93877F43CF7BB1B2B7434D56559D6F8C7B9CB9AF5DC378B79E4F4C71FD5E5B7976CB9BDBF3F7E866F90EFCBD979BBE9C5F29DB3CBF4F9FA7E7EFD8CEBD462E667C1F69F4AC5F53ACF07874EC8DD3225E072DD31D034E966B3AB58A5AF3773CEE9CF87E9E183AF24B1494B6254381BC65D423C6D4E973E842A6590A5365F2E64C5A9B33A16707AE7CD4D74BCFDEE97E81E3EDF5AF2FA36F4CDDBC9D458551029556152511A8D400B105B6B401A6422D28A4A02805A025282C50805B00289020A8405842B1194908909508025020484A82D049500108C420494A0B16C515168228B60040052A0402D8B60802D8B6014902842D8039D41529DF3A3A72AF594B00B604521010B55EB09628B722C500B72AAB7205402D2B3465E5BC7F572E3766A5A6CB39D8DEED2E6626BD62C671659B7726374636BA9D2D6335CD6D75AF2C3754C4CBCFAD39EBF41F27A3D979FB77538FDFCF6CB6E7774D6EE7BB66AFC85B7E6DA783F4F9BCE69D3E1DBC7FB3CFF43F07AF4273FA67A1E6EDD7C6BC2FAFCFB4C3BE7AB3BF4FE5F473ACA378EBE75F46CD6AE4F0EBD9DE7674E7CBCEA9E5BE5CBE8352BDE6E306B3E33D7E6E4F6E55EF1116C4A081568244C7A9C9D4D47471595A58AD2C8ACB612B32F3778A2AE304DF2E1F9F5B2E44D7D8FE67B3DF637AF79BBA6125AE5B35209082AC48AC93480851545562851EC82DAA040005000292812824002A0125401015084A2846524210360509080A84212A0282025008C121024A5058B628A8B41151695200545058A005825825CA5800024424D4802DCD5AC53D39D5ACA5CAD80541628650258972960B1516C545A08B4A8100B4A82C5381E4F772B8FA8D9DBE9CAD34C9973D2D99E04E958FBE79E5B6CD3A9CFE7AADABB58DFAE785AE8B385755CF3F37CF67AD648ED79FD3E97CFD68F5F972DC7AFF003FA7DA79BB75B3565AEAE4E276C7479EF6E7559F36EB9F27E8F3DF8E9E8B9EBB38D64ADDC7A2E75C6EDCB9FD71CFEDCBB5E5F47A5F3F75D73F0FF4BC3D2E7D3E93E5EFF40E5BE5F1EB8A5EBEF0BCF794E79D49795DB1DACBC97ABCFE6BD7E6AEE73D9752A2D0B000082953255765B0CB7E74D29960482C65D33599ACAB52FE5D37F9FD1E77AE14C69EF7C1EBFAAF1E9D8D2FE986B31E3744BBB786D4AB35216D28434055082AB541520C4B02805045A800028500D1804B002A1014120282453506084840A4A0A08909500420285045A8108C121024A5058B628A8B4115169502002805889155002C10B60960B6000653025AF59A77CE8E9CABD655058A2A2D8105A212C5B95B00A8B62A0A545A02DC815140249E5BC7F53A35BAE77E71C975D995B1C5B71AE7D65EE73E75A753531CA9B89A35930FACCAE573D72A6B5DBCB8896676B9DEF5D9AC65D4FAB787D9D1F27A355CDCCAEF1BA6B1EF146A591E878F5D39D7CD3BE3CDFA7867CEBB173E93C7E94C74D502CF2DEBF38CEBA9CBA7430F3DEEF26DDF3E66A3E3A7DC7E6FB7A9CFA6CDE7958D66E5D3ADD79F139ECEF18E5C3D79F93F7F8B3EB3CFD6716A7632B2969408105455B3255295D974BB71B70CA65AEC0B5425CEBCEB3EA60DE795B9EC7C1ECDBCBAF3B79E56A518DFD0FCDD3DC675A56CD6525E759A8D565DA24201611002D02D57005A84484B00B405A15004496C4369880A09280480A0041503442158442405401089094084A14A04144210848409294162D8A2A2D8054505014902C02A0502A0010545B16C92C80104AB651BE74F4E556B0960414882C5414A2D8A8B600097205B00A8B4A8114052BC8F3FAAFC7A3A932ACE68E367AB5BDB98E0D57AA58C135BF53AF31C46B1D6B4C9192DB6E344BC5CF4C4B5C5A14AB3D25C6B5EDEB2ACFBBF1FBBD6783D3C5DCC77397B72CE7A7E7BD4983B71E9E37EAFCDDFCE75C798EB8F13EAE1DCCDF73E0F677F1A5971EB3E67B73A8F3BD3187BF2E7EF1D24D3ACF4F96FE89F3BDFD5E5BE94BD3AE7675BB59CB9BC2DE78D4378F39EEF1F07AF2373BA69D352815258254B99698073F79BA34E76668C1008952D159B52ECDC5BC0B3A3C3B629AF79E5F4713533CB2DE8F3D7579DF479D2EA74F373686CD99B5D5C6D22A8084B008AA82D004950960145A141228A8148329202C800D421094A025042A4945210950804842542028029516A0C10842420494A0B16C51500B60000545058A400A00208828B4116C84952C905552AD66ADF3A77CD35954057ACAB205A08B4112E428B1612E60B628A0B152A9AE570F6558E9A26AF9113159C4CF4335D2BCC675B759AB784B39D2D85515CB8D54ED6F18D9E3E7A095139CD5C979D4BCFCF67A15D167A6D6513E81F3BE8FABF277E158759F29D79F3FA4EC79FBCB8ABBF1ED61EC3876C15E63B73F0DEBF35B8E9EAFC7EAEFB3EB38F4CB679BEB8F29EAE1E7BAE3326AAEBDC763138F37DFF001FB3D971E9DAC5ED675CE37272E6B4D9E73AE5D3CE7B3CBCFEBC799BC53A5F27631B1612BB20B412219A89164D122992196CC56E6D4A6CCEB5D9C65E8F0E9D45A53B1CBA2AFA5E5BEF63A601F3AC7BC6F4DAB0EDF2DE3DCE574C04EFF2E9D2C6EE584B20004002A58082D2D025005420C842B020A092A102420281095084A844892D84220092A0014A0A082A0C10848408494A0B16C515002C500A8A0058A400A4800150501516C92C12C10292E2ADE12E6BD65358544B052A2A0A54516C08B41145B022D2892579E98F9F6AF9F6AF3D33B2E64B76DCD363DCF333BC79BBEDF5339D7AE78ECE3DD3CD7273BAE4E55BD0B9DD719CCB35C9CEE1DCE9CF958DF4EE395357966B3DFB9DD979D75FAD7CDF76AE1D05B82E797A9E73BF3F59E5EFE7FA65F59F31DF8FBBF3F5328B3C97ABCF9B8FA3A5866AEF635EDB1A29F3BF7F8B95A9469DB674C71A5C2BD3E5D7BFC7AFA7E1D7D9F3D34BC7B157B78BE7FAE78DBC65F4709D78E5D66939DB9AE4E9CA66A257ACA840815A508668AC502A0971EA60D66BB3A58BC6DB2D9CDC743355277B1AD060B3D0F2DEECEDB3A697A7AC682C3B1CBA67D4E4F5E741915A6BD173D7679EBA39D5F352A10204942A00140950040A4B48484258095084251800A84254254484224B61121094000A00A092A042109081092941628B62811682011058A00008085A9004B022D8B600E752145D4465359AB7CD3594B94B16C02DCA811695052A2805B00B62A62E5DC63AE4C756CDCD2D129AEADCED62CB9C7660D1CE1E74CDFA998A2E2BD6433E6675C59D646AA45D649A4CB2516E48E859A9295496897D0F4E6F3381BF4D79E3C76F71E0F667968C74E76B1D29781DB9F6396F150D67CF74C9976635AE399BCBD9A65CB67A5C5F41CF5BA6B85D79F97F579ECD63761E7EEF1CD03A78D6797DE70EBEB39746CD12F99AE8CB7A70FAE32F7E1A77CD6CE769CDD675275334AB2295EB32051964A2E4CD19A8433D08C1B99ACBA295F3B6E58D18D34D50979E939EE9D655ADB97A2E7D3466F42CA759155C6F9AE474C71F53766E99AE46F3D0CD797D770EBDEE7BB54C32C4952A0015084A04212886254054404A84251880A84254254484212A112129400A04A01484A810848120480A5402D2D8A8002D821696C54081412554040096045B24B25902C513594B2BD62ADF3AF595B1595A5402DCA82C545A08A2D8A04E5F2EFCAF3FB744AC5664AEDA755CD9022E99EE79D542A1D5CAC66AB9AB539ABE6B3D70AA275759E745558E54CDBF59DD58B37412CD19BCCBAD3716D9B6CEFF003D71B3BED635ED3CDE8CA9E5AEA896BEDC7B7CB42B9763D2EF18F1D1251ACE7A64BF59DB274F1BC0B763597D3E7D7AC609AB25E76974AB0B9D7AE99EF79FD174B54B98C65F2F4378E5F7E365CCAA2CE6EA68935CA4161952E459164AB63E7508B25172650AA70BA66BB2C8A39F5F2FA5D8D7473AE3F4E77CBD1E5D4EA5C77F2AACF6FE6EDD36B93D39F177835D4C6A59CFDCE5D816EB125D166A8EAF2E9E978F5ECF3E8C44369020054A0421094484254250401A84012A1084A84254224212A1101014A1250051224092A048109080A5022D2D80100004500B62D8A02428000400200828A82C02D89657AC029DF3A77C96C5B00B72A053004B94B168273F1BAFCDF439C8B3559BEE7A379EE9002C545A44CB6D9238A99AA8D64272AEFCD63A509D4D4F473973B57CC4D639533A1AC756B1CD4973A8CDD3D39F512C97AFC7A6DC6BC7F6CEE97B9C7A7AAE3D39BACF9DB2AE93B7CEF32CC76D82EB1BB35D70D992DA6CEEDCE3E7D329B25D565DD39713A66CCDEE279BA695655CEBAF1E938F4366995C65C165FAC68D4172B6298359AECE84A2572AD61A692E4055B3B16482D06408D3499ACE06D5D2C77BCDE8F17D735477B1AE5F4C6797A98D69CEEFB9E674C7479EFD1F1EBEDB9EEDD4F2FDB9F1F792CF6F974BBA639BA712CD0A89DCCB268E59CF7EEFCDDBB19DDB2C212820A04A91281094401254058020A8425021084A2025448425422005401034095081484A84090242501408A0A080500100A00029500A00480942028414008960492C0CA0AB59CDDBCF9FA724B902D9773EBA7977914EF9E4EBC4CA65C2C79BE5ECD8BD167CFEAFA2CE3521022D2D8245A1186CCDABD2CC44C5A71ADAD3979D722EB43369759D9BCF8B35C19AA25B0EAEF1AA664E9CF97555F73CBCEDB3AEBF9FBFA5BCFC7F7C7A0CDC59BEBF875CD2F9DE9346B1DBE5D31251BCF3F45CD4B124B6B995B74B336CCEB9B5D9CB3EF3D2D72D1BC66B79C619AD839C6C6F7A7A0E7BEEE293A31956F39FBC748AB594D40CD7664B3542D5D9DADCD7AC9956A2884D66CC6E5865099EDA6CCABCCD286B9F9D75BCFD7151B939D6F8E4EF37CD6F96B416E9CBB5C7AF5F37D3E758FAE7C7F6E3CED67D2F9FBEDE9CE6B3C2E90E5ACE757A4E7BA8CF356635EFB96F767574D44940940042502109640A8202A591012A0080A84206A1016421095102CB000242542542242049502421FFC4002E1000020202010303020603010101000000010200030411120510131420212230062331405060153233244125FFDA0008010100010502FDCEFEC9EC7EEEBDFAEDB9BEC66A6BEC9EE7B9FB5A9A9A9AEC66FECEA0100822CDC06133946780CE503CF24F27C336E0844687DA7B9F66E6FDE04020135EC23B11B9AD760603D98C0D19BB0309EDC09212709A9C66BB6A6BB0EE7B13D841DB5009A9AFB1A9C6059AD41041EF30FF0039BF66FB1EC7DC040B38C2B38CE335DCFD9266FBEBDE7BEE13DB5FB2D4D4D7B04559C6010083B6E1309EDB84CDC1DB53535196377DCE7A85E729BF7EE03DB7D8180C0D14CD42219CA169B85A7285A6E2186CD4E5B9A83E481DB5F2122AEA71EFBEDAEE609B9CA1685A6E08229F699B9B9B9BF60EC3B01041DF737394DF63FD23500EC076D4D4D4D43DCC3B9A3DB94E7DF535353535EF26169BEFA9AEE7DBA9A9A9A83B6BDFA9A9A80451D841DCC3EC3D841076318C6866E7284CDFDBDFB4188D0342D0B4DCE5370984CDCDC53D809A9C2718166841D8B42DB9B84C43DF50CFFE13099BF608B17D844E335EC1DC41EC1ECDCDCDCDCDFF000FAFDA9FB3AF68F66BB6A719C66A159C7B110ACE3DB9407DC7BEE1309F71F66BBEA6A6BDFA9AF66A6A718AB3535D819CA6FB987BEA6A0EDB85A16861861FD98339CF24E7394DCE5394DCDCDC0629ECBDB7394E539CE7394109D4DEE2C59AEEEF397B845104DCDCDF73EF1D81EE26E6E1337DB737EFDFF07A847B35DF50ACD7ECB7DF7041076D7BB5350C2B18430C3DF94DCE739CE5DB73737099B9BF6EFB6BD9A83EC6A6BB6BDA04026BB6BDFA844D771370C6866E1866BB6BBEBEF6FBEE6E6E6FDA2033714F6337373704E3F31B7B1F12B317B184C6F70110403B6E6FB6E6FB6BDA3B883B6E6FB13FCC159C611089A9A844D7DF1045FB4C633CE5BEC44D42B35DB737DB94E70B4E50984CDC0672EC3EDEE0FBA20F6EA71844D4D4E30ACE338CD4D7630C226A6A113535353535353535353535FB31D8411477337041DCF6AC41D888C26A6BB859A80451D8CD77DCDCDFD9DCDCDCDCDCDFEDC7F03BF76BB110FDFDC53D81EFA9AEE618C26A185A6E6FB9EDBEDBD1DC0740B4335D841353535F675009A804D4D43EC1DB7370183B6A6A6A6BB6A719C26A113538C290ACD4226A158566A719C2709C2709C66A6A6A6A6A6A6A6A6A6A6BEF0820135019CA6FDC2718120583B9485270DC35CE1009C62AC1ED27D9B9BFE93AEDA8D0CD4D7BB5DB535ED583DBA847630C30C30CFD3B6E6E19C60ECC3E40EDA9AEDC66A080FBB70B6A7380F7D4026A6BB19A9C66BB728BD975BAFF0032D1D3EC40D4EA708B8CEF1AB2B0573C7057F1C678E1AE78E7085214852709C6718CB384E1384E338C65844D4D4D4E33538CD4D4D4D4D4D4D7D8D7B445EFB9BEC3B8EC20F71F66A6BDC4FDA1DF7F6C4D771FC86FD9B9B8611D8F6D4D4D4E138C2B3508F83DC0807B01F61863430C63D899CA6F70C11A058A21138F6D433E603DD7B99B84CDC3F3DB7045135009A9A866A6A6A18D110981352FB85339F39D3E9C8F26166F9D72B1980A6C786E02DE15DEB7F4E6A678A0AE78B73C319271844E3384290A4290ACE1384E10ACD763089A9A9C6719A9A9A9A9A9A9A9C6113535EDD7BB7034DCE537DC19B9B9B80C0603DF7DB737DF5F6F737DF7DF73737F6C41EC1DF5FC86FDA44226BB6A6BB1866A111877067281E729CA7280CDF6266E18C2343D888440B35353538F623B6BB1EE3B03DCF6DC3DB5D94C580763ECD430C3A59EA2A51939FC86CBCD013F0A585BA96450B6AD56B566EA486E68F326A131B238D76E10DF8B53C738C64F93499E18D5EA6A719C2148CB384E30C30F6D4221135353535ECD4D4D77D4335EC1EDDCDCDCDCDCDFB4770603394E537373707BC99BF6EE6E6E6FDBB9B9BFB9B9B9BEDBFE14FDF1DB5350884763373737D8F765DC235DF73940F394DC579CA17D42F0BC0FD9D6149C7DA4CDC07BEBB184CDCDC5EFB9B9B84F71DC0D4AFF0045F708633045BB24DEEB6EA7285CC5B767A76536365E2DDE7A6FAFC835F4BD3F53D7F2178CC6B4AC74E63C51AA9E210D7196355B86B86B87E23760B0AC658442B35DB53538CD7EDB7EF10771F6F737099B9B84CDCDCDFBC7ECB7373737ECDFF047F67A844D4D4E3089B80FB5FDC1B50303DB9C779E49CF7396A2BCDEE1861138CD46108860304DC266E18C2719A9A9CA6FB1EDB804E3353538F6ACFC6FD9A80763F133734E5B6F449DCE4658D15A63E478727A6DF5E560A924BE3E8ABEE5956E37D078323519C1FB11B9C2148544E32D4844749C2709AECCB0AC6138C293C6678E78E78A1ACC2B3535350AC226BB1F71F66FED8FB3A9AEFB9CA169B9B84F7DCDCDFF004E1EED4D4226A15852059A8DDB94630FBB739C2F0B76DCDC062B4DF63DDBD81E6FB99B9BEC7BEE1EDA807B4AC4FD477D4D76B2E4A573BA8B665A09309DC59CA794CDEA079F82335FD37EB0FC4651600DF1929F4A86D140F31F2784077D88962F661B8EB0AC6584423B1844E338C0B0AC22049C278E1A44F009E210D021A678A32EA18619BF61EE7ED88B04104026BB6BD87DBBEFBFEA204D7D833538CE338C318C261F69FB426E6E6E6FBEA159C6719C671EED35351446EE3B0107B04D76020F6E6F5A4A9ADB1ADB18EC0B19616DC7DA40791261F980CFC1DD528C4BC7E8CC14576FC9ABE59CAB5C9F0993B162B6F0FA878C7956060C0C2938474F9293C3F0D56A3ACD42271852709C21138CE335353508857BB18F0C30C3DF70F73EED76D4D4D41041DB707B77F637FB0D7DDD4D4D4D7B75FC681F7898E639F61EDBFB80CDCE53940D394DF7D773D8C317B3427D83B980C1ED1DB2BA9D186D9BD6064D2382335B5EBC9E306E2D13E5C63F908A3609316C83E674BE95939A7A35B653439DC42BADEA64EB4505E9938C6998A7CEB7639E58ECCA9D3B38AC4C8474844759A863465844E1B9C2113535089C66A719A9A9A9A8C23C768C7B987D87DDA9A9A9A9A9A9AF66E0337019CA72EE3BEA11350FEC077D4D4D7BB5DC76135F675DB535DB5FC19EC3DBB9B9B9B9BEDB9B85A1685A1EC4CDFEC3737DC0F71F61137098C77D8771DF53507B3504EB192F8D89A3C82ECFC2CD4AFF0045AB91F12CB1F62AE1C6CAD37E3027FB4E8BD49FA52D3954F51A90702BB0472048F214735316162354F59B7A8B580E6B23ADFC67F916ADF2FAB5B943A5F564B692370AC6138C22719A8C26A6A6A11353535DF508844B63C3D8C3EC3EF026A0135DB5EDDCDCDCDCE537018228EDA9A844687EFEBDBA9AF66BBEA6BB8FE82619B84FED44DCE5394DF6DF73D88EC7F59A9C2719AEC04026A01EE133BA85780999D44E5C167298586B933FC4229B1BD4DDE91961FF0050138FFF007170CE405C5A80B71D19B299712FCFD654E8C9E969A5B9855D076D04B3E4AADF2F2F8F62E5B70C85164B10C36181CBC6608B47C4E97D5E954FF68D5EE1AB51D2159A844D4E338CE1384E335353535088EDA9698D0CD4D4D4D4D4E3D8FB808166A6BEDEE03018A6069B9B8218D0FEC04D4D77D4D4D7B7535353535FCB6E6E1337EF30C6861FDAEE6E033737034DCDCE537D8F6D4D4026A113538FD9132F2970E8CCC9F557A6B6B8141AD3329D6566960B7301E1FA0D2DAC3A4BB53816358958A90BD75B3F50A94306CCCE7C66BA8C7698B90775B720CA1A59509A21ECB56D5F12D7658BE2AF371CAA15DCAF1CAD2CBC1AFBC56A2E190F85D672316DC3EB1659D996357192709C6709AF66A6A6A1F8963CB5E33463DF535350084430FB75076DFB8FBC406030180CDC06728E61FB626A6A01009AEE3BEA6A719AFE77737DF7EE30C261FDBEE6E069B9B81A1680C07B183B0F76BDC3B6464262D3D43A8B750B17C7C79D59469155D625C88F979D4D89834F8EC5A1678D58575D38C0F50A94BE6A320B8A4762EDD271CDF93859C98742262E6D0B8A88141481E1F999438CB48596372A6B7AED5EA56F3B059E395E526B332E802C25E200AD4659A0F4ECEB79617514C9EC563571966A6A1F619B9CA5865863C30CD4D7B4C6135DF5ECDCDCDF6DFB75F6040603076226A11DB5EF1041EC1DC7B3535089AEE3F9EDCE5394DF6DC68C3F77BEDBEE1A030F7DCDC07EDE45EB8D4E5E65D9E8D4AF318E6B8F72895D0FAA0E486BABB16D565AEBADD2F7B6FC7664F1B056C4DDAEBE1BABAACAEEA951FF0006E3D268CAC3C75C9C0B1703171F3C5898B78C8AF7C67EB18B4B50ACA943C7A0E3C7B0B35CFF003E5E32E04B2D5CDC74F0D41E3872AE7E5F5BE2BB15DACA218C26A18D1BB6E1686318CD18C6F9856719C66A6A11EC3350FDA1DF5F6C180C0D37099BF6EBD80402013535EE10771FCF719C61F7EE6FB6E3FF00020CE5394E53737019B9CA72EDBEC5A0680CB6E5A2ACDCE399759905949F19FAED953E3E397CAB77875DD65D918EE65BF44B2B6A4AD6CF62D2D1F18548F71316E2B2C056742CCB71332BBF13A8D7774BAAC415261E3E0754C7C7B52FABA9D1CEDC4C8D0B56E65A8DF5717178BEAEA186AA2C1F4900C7FACF4AA39E47C43D3E8F11C2F49899579F07E19EA04E3F6610AC68637B098ED09EC44226A6A6A6A6A6A11350F63EDD76D4D4D7703EE6E6E6E069BFB3A8040201353535EE1DF737FCDEA6BB187BEFDBB9CA728C7F80DCE537373737F00C2F39C0D394E73737F04CE7A872020CECEB3A858C38B6F993F23070343C2BA4C7536AAFC66E5930516E59F4EEB28A5A54AA5ACA92D86AA4635FA46076FD02C032C2DD654994CD6F5CCB77CDA895AFA275038F874DAB934F0A94B1F28B2B7ADADC84C7B2DCB66955E2E66C3E3074D364A52BA14522B7BF37E95FCFC1AFF3ABC63E21D1B397268EC4461088C3B18D0C78476221108EDA9A87B113508EDA9A9A9A9A9A9A9A9A9C6719A80773FB60201041DB5DF53535DC761F607F2E61EC7DC7F87DCDCDC2603019B9CA069CA131A65E535E687F1B6B94AB17CCF4E3D7E43F12DB0197752AD2579F5D92DF0F99F3E8A97D5364B5F92869A4FC585C555526C9975F8871D4E8BE5AB20F254D54075FE499BC99ABC24CBE9F3A1F58AD32FF002EF0B5A8762DACBFFBB05129A9ACBF319400F8F50E9C38CB954CF4C8C18D14E2B64FFF00A34DDEAACE8DCF06DC6B0D89D996308C218618446585615EC619A9AEC47730CD4D4D4D7D8D4D7627ED6BEC6A6A6A6A6A6A6A010403D9A826BBEBB8EC3DC3F973DCFD8DFF000DAEC7BEFB7286E5AE5D6AD29999DE5AF80229C6763E94AAA255446CD263F54682D25775D8D453581919A0554D62C618DFFAD310562BC76B60C3B38F87C096217B06B5F85AA4F1F87C558A775DECF75A89BBBA79F35094BF50BFA639C8C31F5CC967A2753C8A6C738AB7CC5C2F4E6CC75B4B61A31C8C7666C5C2B053653755665E421C5C3C8ABCB8D9D517C6F25B30C6A8EEEB1961486158567185634221108F66A6A11089A9C66A6A6A6A6A6BD8BDCFBF5353535DF5EDD4026A6BBEA0100807B35353535FDD419BF66E6E6E3DC948BADF5172967955C107F94FA29CA6637F50E0C72DB8F26C967A8A43F9879158BB68ABBB58ECE351779BFDA22789DB26B41979CCC70EFB2BC9CAE8EAF8F9786F838B8A990B8D90E69C5EB1D2959BFC75CF763DA68E966EB91BA36587AF8ADB2EA790EA18FAB28426E8DF03CAF6B5392BEA4D67D4659F157917B6433E2BA53D39B956D9069CFC1EB98F968322EC1C8AAE4B9634611E6A6A3087B30844221F66A18443DF53535DF5DB50C1DCFB35D80EDA9A9A9A9A9A9A9A9C6719A804E3353535008160135DF53535353535FD28FF002373782936B3B56ABAD29198A1627D53660E3C42F36C6B2AADF2DAA0BA9563D861C6B6F165146254A9F9B5AFE6D84635175AF64B335C2FC87E829BCB6618EA991FF9E9AACB00003E71537E25C95E475BD3D3D3C57977625694DF879766336075C6B6ECCABEA28019E524BDDF5537566FA2CAB22BEA38EB725F8C4DE31EDD58AF8F33F8DCEC4A262E70CAA9723C6D8996B9492C5DCF1C29A844610C30C6108856719A9A9C6719A852149A9A9A9A87DBAEFA9A9AEE26BD9A9A9A9C66A6A6A719C6719A9AEDA9A807B75D84D4D77D4D77D4D4D7F3C4F7D763FC6F1846A6A6564FA65C86C9706A70C8DCC3ED50836358CAB383B9F17CB54B4567C6F29E9E382538F53FA8AE1CA456C96E75D5F44B7A87117657A976B496666668994F5D1D0FABF8703A6B014D5CAABBADE52F04B5FD1F46A12FCBEACCE71B06CAD70382584DB58C7E96132327A81DE287D8B9BE853C65B52DB2CC5FAFA256C29CAE243E1AAB3A597750EABC69B72B0470C505ABC476C6C5AADF255D3ACF08C3CE5CB585638863C73DBF585773C73C73C73C73C70AC6102CE30AC2938C221138CE338CD4D771350CD4226A6A6A6BB013535353538CD403B0EE26A719A9A9A9AEFA804D76D4D4D4D7B353535FCD6FDFA85611FC5FC00334B5BFE469DE47541E36CDE3D3D5F53CEED3F59898AD92CFE9D6B21295F4EF9228E9063E19068C545AFD25496E5662F892DB3479572ACA5DD568E17DE582E0DB683B2D96F5856DB41B738F40B2745C04CACA282B4C74F0BFE21C54AD971EEBBA7F45B9D0E45BEA6AEA986695E8BBC71D57A8D6D4E133FA9C5C94CBC3DEE59685965EAB2B77738A9CDB16B6ADB32A280FE70AF212ACAEB382B939070FD4554D0F899D93470C2E9D882D99F5DA4E3751F0BE0F545BC98D1C471A847CF082A853538CE1DB5088442B02C226A1138C61089C66BDBAF66A6A6A6A0EE04D4D4D76D4D4D7B3500EFA9AEDA9A9A804026A6BDE3FA31844D7F10C4286B4E49A47351815DD3FC653C327A735602A72B1BCD2D5152E3D57150A714E4D8E6EC6CDE11FA8B1B1F3AC013A91E0FD4058A0D6D05DC0575B5B0122CC4C6252AC54A6589B4CF028562D61DF10BF11766747B6BC7473538765A0750CCC6BEAC17F074FC716FA97CEE29471CBC7C9E9A06466F017D9C57A3FE1BCA4A726C501F2696717E3F138C9C260BFE6A9557F2A355C9E8BF2B95EEE7CD527D0DD470533AACD4B2DC54BCD7555D50576660A732AAB26DBB17A7E41C9C52218CBB86A9E29E38D5CE10AC226A19C2159C611350CD46138CD42B35DB535DC76D4D4D4D4D4D4D40201DC4D4D4D4E338CD4D4E138C0B35353535DC41350ACD7BB5FD1CF623F87CEBC6D1BE4A147A2B5A7097005F1F8AD5D42FA9DAAB0045FCD76A2D8C86B2189258098C6A159DCDB33E350E5B5898B17793772140AB1D2CBDF26BA8D59297966131FA67F99BB2712CC4B3833285F84A495E9591563A66A7A9079ACE5C0746A3C7879D55DD372FA7FA816E227D3779063754C2AB36AE5916CC1B5F1B32DBF70AFC648E7713C674CFAA267119D5549CAF4D9CB426630A85D96AB11DAB9FE3ABB6BEA3F86108343D77782B1818AAD663F4DCF6C0B7E181AE78E78E709C6158442232CE338C227184422113508844E338C2B08844D7BB538CE33538CE338CD4D7B7538CE338C0B38CD4D4D4D76D4D4026A6A2C10C3089A9A9A9AFE907B187F85C97F1D1E1B1CE35BE9AEC8C95BC61294A7A8E61A51D9F2187D6FE952CB6EF1E25C1DCA9AD82A8D84E31ECDCC7D03456F65D6D8EB6A62DFCAAA8529D52DF0D9EB6CDAD16EF1DDE97E974FADEA1898BFE3EEFC43673BC500D62BD16FA70FA6620AB1EADDB8166033E065F4EE38F85478675DC5B1F33029669661588E9D45A9C7FF00158F94323A22A13D19F1850E6E0BD70E4577B22D56E40D741B8BCEA3538EA986FEA6DB7E8BADC31E45A456D53ED1317714ED6F51E1AFA5597D98F529AF2FC98F664E70A57A47E235B6C33C8040419A8618D3508F6187B110884423B9844687B6A6A6A6A6A0138CE338CD402159A9A9A816049C66A6A6A09A9A9A9A9A9A9A9A81671EC041043089A9A9A9AFE8BBEC7B6FF8604199B77363FEA5D78BAF2A6AEA17554BE45B953128394531CA59E3552F89529B69F1290552B4267A7646AEA606AC02F1B1ECA2CAE82D0285ED9786F9360C2F48848AB1B0F157332AFA71855D4552A9D4EE3939550364FA806C36AAACDC1ABD2E06826DBD2F53C545754AA91D53152E183859BE4396F557662D7973A85D562558AEB70BB1DECBBFC7B5796D8BC45F57993250573A0FEBD5AB60A8FE36DF174E59634EC7C83C9594285B8559795BA2AE5C2D0061F55EA9558F7BA1159746E8FF008949AFA7F585CD8A410F9871ABAFAD6E539299538C2B08EC61EDA84430F630F631A1EDA9A9A9A804D4D40B38CE3024E33538CD40B02C0B38C22086013502CD433538CD4D4026A719AF6EA6A6BB1FE8C61FE19B3C55940ECF2FA82317B70ACAD7A653E4191D36A437E37187CE828EA1E61939E59E86ACC54A4AE550B535569AECA53CAFE64ADACC8E1287F222D86D7F8685CF300CB519AC6A9B2CF47E95FE345F5557A75ABD29C40499878E555D5F03231B31EFBF334C70F3865E5D56157BDFCF2FBA86AB2736DB2CE999D988CB9CB91567DD978E8FD4C66DFD39478F3F23230AD4B9965B981AB27699DFEDD36D35661C5FF218CF8E68CAD21BB09EBF3AD85EDBABF9523795D6D112CEAB6644C7EB77AE35B6DF9365A8B5A8FA4FCCE9190B2A0D45D4BDF7AE6DF63D42DD1C3CB34DBFEE0830AEE15D4226A6A6A186376308863433535353538CE1024E102415C15CF1C09384E108D77101EDC770D7386A2ACE102C2B38CD4D4D4E335DC89AEFAF69FE8C7F86F508A6EA8F92BC73CA9C65A67ABA6883AE209FE4EB7A799B6CAF9D76EBD44C8ABC63A6D55CBB1938DB92509B9EE55C4163F92AF01BD88A31BC8B90FC183F067C83E4F54FB7B8A9C67B32723170ABC41D5FC9E86B1CD3ABE28C9C1C04470F9695B66652E4A745D2D7D731809F8732571ACF03B90D7D532707D4547A364556E462DAF560508D88338E4E3E3F4FF4FD52BA9A9CCBE8AF21295F1665B82F96BE9BFF003750C76E657C6DD0337C94757C6DE3A9F24C2C70653B28A3432F05724750C57C3014B4AC78EAD7CDE8C62D5F16FD33A7BAAE4A753AE92AF8C26462A6750CA51BD6AEFA57505BE9AF391E71DCFD6354AD1F14C35959A8CB0C22110C31A110F6D4E338C5AB70513C50533C13C738CE1384E30AC2919204812059C6059C6719C66A6A1138CE33502CD4E33535089A9AFEE5D41CF2A57469249C9BBC759BC876BC592BA5EC7F4FE294ADB904D08A82DDA675E725B12B0516C3131B9CB6B3C51855656AB6231A10DF61E0AEB5A1B3CB6DE0D152577DC2C1C1BF0BE27D0AAB78CCA16CA2AD575DAC0D38F631963130DA9E3C244A13ADE7F920C4395D3306DB686C77AAD975C116CAB31B2055652C2AAD4D14E32DF76517CE7CCA6BAEB389787AA9F59E9EBAEB0D563CEA587C05AB670E81715897F957231FC393727061F312E79E6E2B7574DF4F58FC3ED8D17E6111A6656063DFC27E931D071A2F47C6E8BD4383E7F4F5CF5CBE9EE83A7D76D0D661BDF2B7B71A55907825CAE7909CD4C34AB4B319A357A856110A4E10A4E11AB9E282B82826263414EA78A78A0AE719C6709C7B6A6A11384E338CE302C0B38CE33500EDA9A9A9A8076D4D4D4226A6A1FE93AEC7F85B6C15202D73555739581898EB6F26F4368B0F4B725315525B5E3F3AD541CBC906EE4CD02D74D7557543F4C5EA8C1B1DFCD2BB2DB0AD2C15B5487BFC96D8869A515435B905831DA8C37C84E915D98585505786A2723A975018529EA9EA72BD21AEDE0775E057FE3BA4E55A1B1F966E461B1C73977D7E5A338BD94E733BF918BB3B097D75BB0B762AEBF6C5B972516A162DF5B9BABE9EC236325AE31390EAD8ED8B774CC951958EED53E4D55B57E0161452899D63363A78E789957EA3665747DABD6417496D5CD2EC7290524CC6F0B55D3DAB5B013859B85D50EBAF65F89B07AD2F2C7EA98D7BE4F59A6BBB1F305B5559E8CD90FC51BA88AE7F9CA948EA7F03A957AF3D2E1B8C6138C65812305596665158C7CD4B6DAAB52BC2719A9C6719A9A9A844226A6A719C6719C6058166A6BB6A6BBEA6BDBAEC7B9FEDEEC117C8D956FC215CE293497AD35502C0750DF4CCACCACAD74F90DA874D8C9895A27A43E4F24F1879574DB0A8C2AFC6AAB5A33A5512E5B1B2686718B4F8ABFD7B322BCCEC8F1BF46E97E9E9C2CBFF00F2E84265ED525999CEEBA9E92ED5E657E34BB97A8E924F2EA58868AB06E38CCD95959630735B0F1382632E2B7946804E60AF917259AD3E0C6CCC916E2E61D2F0B035F611E1B026364B0953F35EBD806E9C0D4F8392B954629E6059E1B2DFA2E358B4262ED31EC5C9C7B84599B814E5CCEE9CF8CC5664D6698D7B0393E3AAE79897FABA69BBCB613566637A2A96CB83DF33FA62B57855F1946737A7C7FC40E31CF5BA88F3D778F5095CAB2834A7A907B0E532CB325F55755C930F52C842BD4AEB66458755B2F1AADBBCFD31C5A844D4D4D4D7B089C66A6A6A719C6719A804D4D76D76D4E3353535EF30C3FDBF3EE1C02E81B7945B095A32F55D2C5AC35F951F0F53D320652762D2897E71795B1629D2C38AA9AF194581BB3EF8AE3BB154541FA42A5E160B15C38B6D152E6DDE4BB0F21BD1E5752AB16745F2D945F8CECF460BB2E074F174BEE7E9F98C897DBD02D4C89D672F7938B8E38D2461E1629E45D9CF50E9454CB12B8F753863D63F9ACBC62BD3978D6236156D322BD6472349C6BAE4C86F82584FA58758E91E06C03E2EA54E4147B13C8CD5F38D5FA73E7F2D751AEB9A76C5A9C3A3096896E1D4D333A50619BD22EA99B1ED10BEEBC1BCD7726525D735EA5B2AB17D6F7AA85EA1E0AA9EA7E9B26ECD5CE2D86A65D89E3054E95B94A290EB559C1E9CB178F9212A025DF0CC9B25C0464B0D783956E2DDD1EEA9F145A09FB7A9AEFA9A9A9A87B6BB6BB6A6A6BDC7FA56BF87CAF9BC910B4AC8218A575F9595A9C865B2A76B45B8963C545C5AAEC93B6051EAC66B556BB1263E1BEC0D08C445BBC965D94953B65A02FD5408D948CADD4E7A96229F133E4665AF3087A9C9CFEA7FE371B17AAB3E1E2DCBD49703A5B2E2F52C0B2EE9D978F6632FA835B5FC83140713E5F068E4261E2BE664DFF87EFA93A7A79E7E22C0BA815D5707B28E54E0E53604F258F5F50E9DEA657D3723D2E3BBFA9A9B70B2244D0B3AC62B0AF180F23DE714DB76AC5B2BBEB41153E3C3CAAC66655A34D1CEA3BCB089658A25990B32AAAEF96E3256AB8858DD53516D77BACAB36C12C73C9320AB5BF2DC4EB19D995BF4E2EE327C348049357D0C16E594317ECEBB571A26A4E29435AC1555FA6633AE3523E10EC77D7B35EDD4D4D7B0F7D77D7B35ED30FF004623B6FF00857C8AEB366535B1F9C6139AA8F231963021BF55F894DEEB1731ECB72B3C2B03CDEBE5458B92C2CC25686E502ECAE03CAECECC50D56309607567FF009D75576AA7055B57E0A1101D2852CD4643E15B8CEF9398F69B717A6B1C0E9F8F99E9D69E093AE74FC5E18F8804BB74B74FC46C6AEBC9E792B8966559814594BA5C2D89D36AA2FEB86FB16E67F2F4F55AAB7F367E4FE1FCF7D55757735FF351C7C7B2CE8963E4A1118843F0CBD47A23A5ACADD4B1307C95E3E1E630B1BFD3FDCE3B976DF07AAD141E7E4AEC99579119B70F6CB467A48B6B992FB5C775207C32D2F2BE259F1D27215A51F45B3214B20C415CF4D513E92B23D3F16A89D4E20CE026A1329B3FF63D7B8A02C07711BE7535DF5EED4D4D4D7B3535EF3ED3DB50FF0049D7B35FBFC8B3C548FAD9BE989B686B762F4681531D0AA44FA8DB6545316938D55EFF0034D7B8E7C829C41336E9465263A1B999ADABF329FCE1E35C7AB0433CCEB59DC16940D3DF8C29990544B35302A569D4313C56F84256179E06265D61306C4BF3317255BA9F52BBD4CBE96C23918FAC2ABAC2B62B84A9BA05F56EDC3C3C995749BD6EBFA55AF5DFD243D9D47A6FA1C84BEDC88DB6EA1825AACB4B2CCFA283670EA9D293A85786B6F4BCEAF27CA82C164F1EC6A66F4FAC3AFE6E35D50D33F912CAB8143CDFFEA847E55166EC79915F2162F16EF7D42CAD94B575B112B5F28AAD2882DF292BB991168B8CAD382F6F859E4506C60256495EC4EA797EA36FC74FEA270D7A3FE232B60D58A20FAA09A9AF66A6BB6BB6BF6C66BB6BFA56A6A11F74FEC3A81FA12B50186BB562C69B2B07D1002D1B177344355FECCED6CC8FADDC030FE441635A58AA9C76F1CE26B508D920D243D256DCA4C7BF2E7F86B14FF8B5E356137A8BFA1D79F559F867244BFF000FE4D29898D7571B30F9A845BC9B2CC5A6DA508FC3D99C6EEB35D8F6E7E6DE5FA8D95F507B68505AAAEB8986D65FD0F26E56C7C83E4C5EAD65868BFD45795937519BD5517211B1EEA07496FA7A3AB0B68CA645C0EBA287AB26ABD72FA78C86AAEB7132762C5AD944FAB9318CABE606CD637E50BB55D96228150666B289E3D47D6AFB388BDC337B7270DC9AEB6AE028672640B6ED15416F659B6069B390C6FA680C166E7C5A2FA8196B985A74AFAB2BA5F56AAA34DCB726A57FA77D4D7EF0FB75EDD7F31AF76BDEDF6B5DCFDFB2C15ADA5AC26CF855369F0C0A74EFB98F4794AA522322A8C8C667298C81726DE57DABE2809D0A57C7EA1A538B5A05B69056F164CCBC82E8EF462D4F8AE734535B75FDCC4C9F535D15ED54D8B3CA67A9260BA32D3613878CF12AAD6AFF001E39E2F43F06620F001D331EE193874508FF00243B97C8EB6D86B43578E57296EB33F2F22ABFA5F51BBD2F58B4E463A38B25F73DF73642D4F5520DB81814BDDD432F193233464F4ABBA3F5C19AB6549728AADC39B0B17F3232FE6DA757D8A7C883C85A85B6549F5328DD9F12FBCD10F52E497641B0987B9EF915DACFBE3192C32936285FA1BDA4C29B6FD258EC543B13592B2EACF1CB1F41FD70189C8E4CB3F0BF55AE80AD14FBF5FB3DFBCFF4AD4D7B1BB1F76BD87EFDAFE347B19D8B1DA7E8ADC0AABD61BF5A3A7B5ADE9784162D531179B1B7EBCCCA0C31B68F77E6359F12C67BA578838DA054C973B9B6F18B05A84BE53727EA455722FB321C1FA3A0BD5662D1E7AD8582C16DFF003B2189E32BDBC5ADA01F253881D97E275BC8B2835F50B9ED7CCE78E14BDB5D95BCBEA08A9958F9C6CB2BA55C03566E5374F1837F96DBB35AA5AB2DB8E35F7529D650BC3D69F1727A5351664D391BB73F14DC94D9604C4BBC86CB021CFA4716FAEB7FAC63DBCC5CFA6AAF5B132B2CA2E4663593737F66FAC140D622D03990BA9BF9F605EC563008847D479F92BC9B0447F23DD5F138FF000C7F5AFE1BA1753B6E81A21DF63394FD7EE99BEDB9BFEB07B1EC7F739B6F2B3FFBC3C931B0B884A07ABB6A5C823153C97BB54AF636F7A95E5B4B2CD4AEAF20AFF52C58B29A6565B2235CC65F5177AEBAFA7D75916DFC142645CB888E35147C7C4E9F93E26BBA91AE579173597F5767BBA0667999C2642E32284B0F8A059987E12E35B57902C21A64509915E4F46B302CB8FCA9F164E2E4B66E45571B517A62629C8282AE9F7BB62759C27E3D3D529171AEC9D3775CAAA7BD2DF0AE11C6D9C0BD287A3300AD2EE58F7542C5C56B68BB2AC1E423933D7E057FD693E41D539F95112A4EA190961266E73D1FB07E0322BC142D70B79145ABAFD7DCDF23C5F2F8ECE5D6C03E597A6DF4D2733097329A71583DD8A6B8A34DD26CF1CC5BC58ABF139432C81E298611F689F66FFA7EBEC9861FDD1F88297BAC1D36554A2C3F48ADEC95B7C16B232B0B326F5771487878004F2715BDCF9279D8F51AADF4E49A8902CCDF0C47F046A0B4C8CB01BD4D912B528DFA83CE5BF3621E0D53E9AD7E6063EA7E1FE9B4BD5E32B064B6229CC36E362E49B6AF2F9A8082E889F1EAD84C6BD3223D48065E20AA353AC7C02A831CF8D6966B2AB31CF8EB2B5A5CC097C77A99F145160B0D71FA90C7B1B2972596B0AB6A8518F90F53F4BBFF00F027046E41A751A8D89897F3857E9A2A1E5BB2851651962EBB3FCA314B476D2FA982CDD884EBDE46E595859C6CD3058B6298BFA776B02C0FC83B9E349E6B1B80283E2DDEBA3794516622B3E66078CD98DE618F92699D3F24EA8C9162F92792168E746ABBE16DDCE5BF6EE6FB9FB7BFEA46187F72CE125F6F36AEAE67C42B9B62F95908B0E479CA7E5145F19C879556B58C801A0209AF1C13F946BB74329D7C972158F705A96AD14B06DACF2378AC7B2DFA5B73E38D75944E500265639BD186C4F9887FC2ADF9992A194B9B0642D930FA87A78FD5DF1ADE9792A3152F56AEC7DB61586A72FCD726A04E4D416AB5486C6DDAD5FE50C55DD67057CB760A8B725072C84D63D9D47C6BE5162F99830CEBD856AF6CDAE3A2DC8B4615BB536BD4E5F90A97C319F70300FD54E910B7933B99AF97C5879B7946DB22A414BA11F66DAB72BAF5EDDEA19E50459F35A5FC65996AB38F9ABAC2D35274E1D46537E4E064F9572F1DEDD45B15AD6E906D95D6F52E366BD65727CA8B9A742EE4B659B98E80D8ECBCDB27814CB5682C579B9B9B9B9BFEC06187F6CEDC16CBD8D846E574AEACC8F027ABD576DFA0CDCA283AAADE11F28DCD8D8AACB918EF65B6535A973A5C4B0E9ED588BCC645A44161B4D78DC866BF98AD2DB169457E4575C9B8209C18C4C97B45834797C27EB462DE6B7A131DBF0B5AADD2F7E5AF1D96E3C8363E6A9C59E9CF52CAB57D3E461E4378EFCF3CFA7F544BE5595585BF253C99EECCB98FF998EFAAE9CC02FE95D5545BEBAB5976756AB96C99499B93E52F67195BB6D00DD55A88BF033EE9D2FA8AB51F55730F27739CAADFAF9EE364B792D0B7D7E2F4F69AC96B57EAB525893C5C1D2CD59ECDFDC1FED77C44B78C63CA5D6F8C0B8AD6EFA9859FE98E75E974C6CF7C74B724B2D1D4AABDB0B3A8ADAD54BD4509545FA62E415B2BC96566CAE53CBB8CFE30CFA73710532190D3D40155BD5A6E6E6E6FFAF184F63FB327519F4BE6632CC72222ACB72006B3A87D34E35B6DB6D2CB3D3DA65B498D8DE00D679263E1BDD00E225B40B59D03E42568B2D4296D963564FF00BD3ADBE57925A4AAE4359580201CD874E7148A0F9B20F17BB46C166D2BC3F24A31ABC08321BC38D459919543D5838965CA00B6BB201CA9EAC96FA5E97435B93905AAAD54D73295585247A8ACF8E59670BB25894B510B570B2A262BE873651856F35CAC60E96FE54B1039F4CDBC6C50058CB5ADF94D63DA478FC365B89D37A9734AD9C0A2FE55E872CABD95EACC5A5865258BD4574D4BE985DCADB179A9AE6415495DC2A54C8E16F63DBF4866BEC16E300F97426C64B52237919D1514F82EB72F07C8C986E65F896D25412B5D16D532BA632A25BB7C7EA9E157EA82DAD32CB3DBB1621E43716CD4F3F952EB7E1AC86EE11F3794A332C515F567A651D569BD52F5B26E6E6E6FFAD9FDA35C77E4243AECB643145BA581DCE3E39B62605650508BDAD75526D632BBF94CB278E3630C860342EE5C110548EDC42DA121B8727B7F3159A6B9270225C7C539F1243942255578C1CDBAC1BF4E40E6C7FE9898E96264750555E9E7D55ACBAAB12DF4B7B712F573AEDCCA98CA89C7B329932F1F0ED649E4252DAD9A0FF7CCC22F65365F50B6E76955DC90E8B2629E5946AE22B554C77361C65152F9B433A84BD4E08E494B336F4322C1794E352D8471C3C8B2B7F29F5182CB8B39F18B94AD2CE1915D9CE87ABEA5B6C6C9C7B6B3484528FDB2519E5F4F16A2BE2DF677EDD726625E5943862D31DC72CCB7E2EF91E7702AB4F17720F90CAAF6514758B389F4B7D9E3A7742292F4ABA63549616A824F89C8436F89CFD4B76C3323BD83106ECF1D43CDF3511C68EA1652713AAA5F036FFA4EA6BF65A9A844D7EC72B23884B0ECB158969971F22EB530B1A050BECB1058A6C447FCDC81774F2E2AA569530C049994F1F8C6525054C0A60B5CAE3C6C7FE7B21A2B32C7C634B2F22D8E5CB3DBC63E45829E50BBDA063B59756E28B2CC73C6923D555D45E8CB358B16DFC98CEEE558D231345DB26A165B922A6F2F06F55E17F23DCF602A389837587B4BCAFF00325752AB8B855759D4C592CB5D65571DF9383BE4006CC86B25385E4A38AD65B8DCB5F2C7B95073A2DF511F306C38015FD2B3DFE7634940B95E32F7ABD69C94F671B97B1675D0AFB7E901DC3F6F5A87F4BE9D4AD424AEA626DC2E18EC250399F4D6323020D7A9E16D0A49B7C46B94E1F3556BF0A626AFA8AF2172F8E5977295FD2B45DB5203CCAA794E5E21BF954D82388D2BC0782D1D4CA361F504CAFEBBA9A9C232C23DFC49850CD76D4D4BEF15C5A7CA9755C6729CA63633395C2A97DC6574F8E15DF7B6CE102F22F6856B144B7818C38AE182F1982D6D8A2CBAD460DF0843CF29673758F5B63DA257C919C043C4BC18CC6B4229A93E898B7D143E5D444A42BB58FB9D17AA148C3CF1D952ECA5F9C07519194BE543FE8793116FCE459CEBABF357FE41DF4D6BD5A57265552D232F28E45BC0328C8D8C7C84E7918D5F17AACD547E2956B633D74DFE736BF23CF22D1BC0CA149B8F371CB223DDE4414FC5AF2C1B6C6FF52BECBFF4C5EE7E627EA7DBBF608D6049CC1975BC05B6B312FB2EE78626416C1BD0A5B57CB3536335D6BAB095DC56332315AD6C7AAE7C7B36F54C2EA1E1B7CBB6C8A79A5D4FCD9FED5DACB05BF046C598A82B267D48AB69598CE8AD76AB20115D3735176367A5E41DFF005E31843EC5410201D9A1816328990E6BA00E407E59602C08BB75C4A94FD8CAB0D75625ACE3B641E15EC86B5DBC8F61E3FAC2DC0D33CADA369E59007ADB3FDBF481B73A7D42DBB31C89914AD3959D5AADB81336D6F0D6C402BC0509AC7A2E67AEA40F7E30E37AD0A997D3ED6B317AC9F0E3567CAB8A359193585CCC8402F5B18337FD49F9C66269E477FF006B0FEB85F4D5FABD6BCA6B8BE3541CAFCBBE43F0ACED6D50B7E6DCFE6B870B31CEA5CA0AFF00BCA3FEB824AE473233335785947C56ADBBEC506C4FFB1FD3B98876F0C1FA43ED307B1C6DB8FE6E62705BCEAC7FD5C4E9D616A72FFDB1EA0D561E4BE24C97F5871501B6AB3E7371D05740DE4750AC57954655AB55EDC97A6B90B4DADE5CC40AF9078C0E4363AFE457F286328F544724AEB58AE45B722975FAE2D61661314CA4FD10EFF9AFFFC4002A11000202010402020104030101000000000001101102122021310330406041225051700413326171FFDA0008010301013F01FDBABD97F0DFAEB75FA68A95B6E6CB2FE3D4A5BAA1A2A5386CB1BDBA6CD2698A8AF45EC53457AA8AFABD1451515F056E7FBAD965FC5BDAE10F22CA8A8A348956CBDF66A2C6F62F835F55534543DF66AF6DC5FC0AF657C57ECAF4A8BDCF6228D251A4A2B6DC316C70D8D97B50B6D7A17D5D6FA28A28A86515165EE73637EE5FB2BF8F6597EB5BACB8B8422A7265EE42FA6515F1D42F5D0F75966A35165C5C3659736597BBAF557EED7EA4F7B50C462E58D8F75090945EFA9AFA1515F1D7A98F22F635365C59659636596265FABB9BFD8EBD15EB7F05297B9C62850C68A2A6A121086515F634E17A5A2A2CB2F6DCD8A1EC5E8B8B19424515BAE6FE156FA28A28AF9D7E9A3495B1A349A4789A4A2848AFAB38AF7AF531EFB8A97BAA16FB351AA2E16FA8B350A10959A0D269349451454514514545145151515EAAFDA68A28AFACD6DA28A28A2B72F5396E5CA8A10CA8A950B63DD62142DEC62C6C4AA1230C4E51DECAF5515EDA2A6A68A8AF75C5FDE9ECB2CB2CB2F7318E1C25B28A2A286B62DCCB1EC42F4B3F3B70622B657B2A28AFE93AF53D965965962C8B1E46A1E42CA324515B5C27B5C5C29B2CBF463EAA8B9F1BE46623D957EF7F02FE4AFB551457A32DC997163C8D45962C84E68A28654216C6C6514545EC45145145462FD3DEEC1D32EE165FC8D6C6A6FE051451456DA28AF92BED6E28A2A1C58FD1A8BDA84F739A139A1C5EDB9ADCD0956FBA2EF6B9C15E329D15150D7C2AD9451451A4D26928AF6DFDB6BD7454B65FC042737B28A28A2A5C55890E56DA9A95B2C6FF008F5789CB42CBF0CEF63457C7AD8FFA25B1FC2B2CB2CB2FD2E14385BAE16F7950DD9D4B12DA8F1E0FB28432BF93145E865D894B5657C87F2ECB8BFB53658DFC5BF6B45965DC2DCA16E6E58BD18A162A849EC5C192D4751D458D886BF3F158FF00615F6772FE2A2CB2CB117B5887142C4A2B657A1BA970B6F4362660F932E84B818D88A39C4ECA287B3132EE2FE0B1ECAD95F116D45ED7F58BF5B1FC6B8B94CB2CB2F63456CA2BD4D9DC3631216D71D9E3C792AD50CECCD509D18E5155D08ECCB1D9745DC257F05EFA863DF475EFBD9737F636FE45965970996596218A17A5454B745DC3F524247870D4C6F4A164CD5F81D645462D54A7C99B963627181960D7BDFADFB2FEDD65965CB1FEC084F6D965FA9B18A6C43F423C1D703C6DF26358A173C992A7677D457E67A329CA128C0B32E1FED0FECB457A6E2CCBF6042659659658A2CB8B8D46A132CEE50DC22C52E1C23C2DA1DA47E0B466D3C44F494B32DE3C0B95C097F251929625286F834DABF96F6D7DBDFA6CB1FEC365965C265966A2CD459659A8D62FFD2C50DC284ACA4A2CB9423C1D8D58D51E566A31FD4A98D3C59CFE44AB9458D8DD8C4F6E29256588CB1FCFCEADB45457F49B16CB2F6D962C86E3C7E34B99BA2E68A2E8EE59518C7F8F63CAB88F2B663C982687592A2B2C7B315C0C70F6558B14659522C4C464ABFA69FEC4B75C6A15B8C70E6DCB63715B2AC7C459658CC448FF001D71C8DF31E5ECF1F662B8386CCB249D1FF3CA1F28CAF7628D2CC8D42762667FD70FD08B9C716FA3C78D234AEF6D15B7A8BD89182B67FAACD1A552178CA3C98598AE4D7A7132CAFA386B9164F131C8CB65091D8CCF2B28C10CA2AFF6EA2B754D7F43DCE38DB30C6943DA86CA86E1BD94508FF197E4D4BF077C97C0F233CD18F2658F0618D8878A67FAFF00818E28A31ECB6BA327656C7D09DC385F0ABFAA349A4A30F1EA16094BE77D97B6AC4B6639E95C0B9424768F27086ED9E1C4C9702A484EC6C5921C29A868A3F23E23B84FD74514514515BABE2D7D6EFD1457ECFD6C4ACC7C3C1FEB68FF0058F0E7812A28A32628E85B2ACA287091A85B7C09BE5CF95702F1DAB3C5FC0D0D5982A466E9189A9318A2C5C96372F98E98FA311975FD1F457EC2FD1E3F12C50865ECB1C2432A284A7A9B8C792A2848F1AA465C467957062B830FFA1A2E8C599B767589E37F832422C4625C64A147632CB2ACC50D57C47F7BAF577F2EE3C387E447E47D4A8EA10A1CD9633B9A28FF001FC3A9599E34310B130748ED8DA1F662BF499AD2CC1BB3A3A32464FF00078FB1A284A311D5968CFB18A5E5FC0D098AD98E2CC97F5AE38EA742490A7B34C59D950A2B6B663D9514787C76F930C349E7C466284E91E3E159F81FE946811E6E598B2CC90D99636CC718A3A35183B32BB131F3CECB8ECC3012AECCB2FE0D745EA715FD634CF0F8DA76CA86509170E12845966A8B2E31E06EE3C3E379331C287C747972B34942E5992D347234AF8314D9D1E43138ECBA33CCC158B12E32EE30638CB6FE4A1703C8CBCA6AB10B92A1965FCFADD5FD0F878955B1247517170C68A2B638ADF860D9E3F1E942BB327486CA8F0AE6C7FCB13D5D1A05FA4E4F2656CC159A68E07CB31548C9B146518F6375DC3DBA9263F28FC8D8F26C622CF1B2878D1942F5BF654D145145456FA28AFAE3FD9B1F1B6630DD94219438BDB74770DECA28F1F8B5B3C7E358AE23A465CA1AE67C7D7079ACF13E06D43CAB834F2751A458FEA3FF009B32E678C909D8F65599623D8A3C6F91E45DF631AA85C945CDFC0A28A28AF457F447871BE5CA5650B658CC4BD9DECA850B93FC7F1AA1F12D99763315C8B84679734245232E278FC1636C56533F522D9A9B1F3D4346251D9D0F66585A1ADD8898CFFC1A319A1AA28A2E2B654D1451A4D251457F48A5AB84618E9551762E0B2CB10CC467E44A2871709517094638B7D1E24D625D8DF2659A35D992A62562C7148C72ECD3AF2287C0F92A6A3B117165F059662E316EC7D58F6E588F1A852998B2C6AC7C2132C7909CD9ACD46A2F7DA13BFE857F03BE0C30D0A2ACAA2E68E94267654363DADECC16A3C5E2D08BFC0D8DA464F5330F11E4C6D084F833E0C32A3537D14316CCBC9F8462AC6B650D1DA85CA1BAE47FA96D51566587F11621BA350B2B9AA65992B318D67FB0791A8B1666B35335B35335B2CB31EC5FD29E1C1BFD454B28A96316222CBF5F8953174659A4E32C6CC30FCB16690DDC62CF2642C6CFF942BEC6CC45C9965A47E56C5D94CD4D70F6D1CF518F70B851DCFE472F143C0A6865986459659A8D66BE47E4B8BDB7B98B83177FD29E1BD3BDC3650A5BD951468349A4A311E6CF13D59193490F0B122F818CD5476CFC1F82CE0C7243CD23C959722565E91594AF9171D9C09F233967E04EFB176595C1CF5B58A6CD43A7166A353350D972B7A96A28460BFA4962F2E8C3C35D943848A96868AB14B7BEE1723450A31CB498B7965C9F8A31E067FF004C84AC4A8C71A1B1DB662A862A3213E4490F2E4E18D508E8B50B93A65D942E843DF931BF732A6B7E3CB17DCABE7E2ADD0B855B50F664CC4B86CADCCC71851518636658D33154368D48D56E1F3C1C628665E4A46A678F1BE4E4B45A18D6966BBE0AE4F1AE79385C0EBF058914551AB98C5EEEB6343F459DC3285B2FD18BD26199DFDC68A1AF9BE15FA862950CBD98CB421CD88C30BECE86C5C8B146934B317A47958B933CF4A1E478C7C1F9199343463833C7D1D43519AB29A3C4A9187765192509FF0023E4AA1AB3816C5D0BBA1972CC9FAD7A12DFE3ECD490A1FDA6B7BF6BF7AC5E5D1861A14DC21B2E131B12970DD4A84CD4CE59A458D1C966A2CB8ECD28C550D88CD9662AF935D70286C5D1431BE446AA350D946984CEC7C09F1365D0DEC790F2BF43DEB656E518332ECC32FB6D15B1FB1FBD63A9D1862B152A122CBB87150A5B9EE6858C612F28E85BF3B31C6E172E1BA3177393A314659F346223246A1643E4C5C64B812B13FC4232514C7C0F31BF93662ACAA31188C61CD7DA9C37F2FC385730C6CB1172CA12945C770DD9D4A9F1B33743C8478D7E46AC52CB139AA1D9D09DC22C4790C58F9660A32CE8D562C84C5C9D710B67E21E46590FDB517E9A9C1A4CAD4AC486846335F6D63F95462B4A356CA2853CCA2E1890E12B34CA85C0DDC59E39628A123509DC3198F06A315C4B1F62545966795B94CC7A3B28C47B721C5EEBD95F011E32868A132FEDEC7F2526CF1F8EB9639A2872E6F6328B284B6A8B2C6E3C2E5C74286610CFC16216C67E0D548D4DEC463D7A58C717EB6216EB9B9C5596F11656A2E6CB2CBFB631FC658EAE118F892EC58D4D6DA1972C5343629AD95B1C78FAD94773962210CCA315674272C7917B518E45FA73EC63DF65FC2E87C9659A85917B2CD45CEA350B22FEC4CB87F0F962C3262F07F2638AC7AD9D17165C3E06C70A14B28A85172865D1AAE31463C22CB87D188D9665B1F2518F03662370C7165EDC729B8B2E73EC70E3AF86CA9BDC99A84C73658DCD966A3517F5F7F131F15AB3FD48AA2A2A2CB9A2850DC395EAAD9931627518962745963E4C62CB1898C5165C5965992BD894D88D45965C272E5FB56E7B6848AF868BA165F5DA2868AF83E3C2F9D950DEEEE6F6214B7BAF654350F931C46271D086CB2CB11908B8B9BDB945172A3B8B2CB2CB1E5B5ED43DB5B3ADE942862DBD0A1A943F42C84FEBD4691E256FD2CD2D15346186A6556C6CBF5B952D8CBA16F432E28C711CA72F6219517B6A58F7216F7B9ED5BAF7B948C46287DEEA1435EA43E2109FD7D8D6D58A162A321890D0BF81710E2FE2318B63142321295D0F628C85B18B6B10E5C3D8C50A5FA1FADCDCB8AD981998C3D8BB3295190E10F7310850BF7AFFC4002C110002020103030207010101010100000000010211101220210330314041042232505160701361427152FFDA0008010201013F01FB8DF66BEE2F6D668A2BEC4997B52284B6DD165FAFBFD32BB76596597FA7515EB189145EDB1B2FB745096C7FB25E1F768D3DFAFB0BF40BD332B65762CB2CB2CBCD15843DC848ADAC7B6CBFD79EFB2F165E6CBC515DAAFB257D9A8A2BD0A4561618F64515BDFE9B7E9ABBD62DD451A4D25668A28ADACAFD41EFBC2C4878585B9B1BFD3ECB2FD33ED2122B65979AC51A4A284514515F61BF4D7D8BF4ADF6561B1E10B179B2C63CDFEC8D61F65668ADD59A195B18FBAFB75E86CBDF65965965FADAECD9A8BD965966A2CB2C6CBFD59772F6BEE2DD422F2B75E18F7514692BBB43C363746B46A2CD45965978B2CB2F365965E2CBC597D8B2FED565FEB37BECB2CBDCFB4B2B08A2B0C458F17D87DA63ECA121B1B3CE26D78C782F0996596597B6F6DFF1E5B28A28A286B65610B378BC5E2F17D97B685B18C7D9B1B62CF550CBC588BADF7B2FF8DD6E4CBD9434514345145143584597B514359BCA2B0F6515D8976EB347523688B2485B13AFE49796B6ADCD158A12349450E3B6CB117863DA8BC595BECBC5E24BB296E9AB45517F91C3F05EC52FCEF5DBB2CB2CD4597FC32CB2F0B142EC51425B1A1ADCB635B16FACD8F6D8FB0A35BD9D5F9659ABDAA545DFA1BCD9659A8B351A8D45FF0006B2CBC2C2EFB2B1456CB2CB2CBCAC58C597D8BC5EE51BF3DAEBAF711684C71BE51E362746A45D8F6D97E8D7EF4FBE84BD13451450D145769E161F7D26C51AEDCE51F71BA7C0DD88BAF04F92B5A2A87C628FA4D4BD42F5B5FB356DA28AF494565F6EB0B0FB0F7455BEE3744DB72B4397E73C9E44F4976B83C94CA28D2509D79F4A85FC21657A578A28ADEB659A8BCBED25628D63C1AB7DE24AD10E1B44DDB1090CB3899584C58AC488FA45F62ADABF6342F4D450D6286B14515B2CBD965F6BC89562CA65668F0718E713748BA937984AC6AC9470A57E4788CBD859D3669A348DE9F40842ECAF5CBF63AF5545143450F287E83C8A35845E158CB2C726CA6567AD2A895A9F0354571624E2291C3249A797C1D35EE2C212258EA119A7C77D0BB4BF7BA28ACA17D81A1EDAEE79231ACA5650C8E3C679CB3E25F229704D3931F1C1077C1E3CE2DF8CF922A84511C367926B81448BB5FC36CBECD663F6168A28A28A1E28AD9451584AB67250D08795B19D78292135EE5F23E590524EC7F31CC0A52E50F86392C42579437C62C97225C8E5A655DC5FC02F2BB3450BEC145145659450E251456748E256562CBDF658C67C4782D2F223A105438A2771959192923E51BB7C8D093646221ED937657B92E484AB87E817A3BFE39586B34345147824EFB142C2E3650F1F135428DE3A155C12E0EA69914E1F31AA32F049F22FF8456161E1CA873126DD9A781C6897E4E9BB5FCF6F6D6286596566FB378E0B18CF8B93D484B82CE92A4751F04DD33E98D1183F63CF0C5C320F178BC751AB2D10F2389289441577D7F2D7B68AC4A543627E8BA9E05F114F93FD3FD25C929F25DB3A3D5D3C326D513E9B9C88F4FF00FD12F965C0E2A64A243365927C1644E9C28B47504B8357E4D5427787F72BFE1B290DDE1766BB3F14F8A34B624FC0D2B28E9C6F925C23A72B91D4952B24ED17F835BAE44C43C592F057B321C3148BF7386B1EE38D622EF0FBB7B6FF00946A2CB2732F3421E7CE6F287BA50D4F93A916A5C0C67453932923AF3A4749F24EDB1F025C92B427879B27E6C4CD47844791318F91C71AB6ADB6596597BAFF0071AEC597F6B93A1F50D46A66AD88E4B2C721165EEF250F6FC44D41706AB1F27424EE992EA53A3AF1BC47CF2759D9D28BD47512F069A62F03C512E07C9155983A2EB93CA13E49B235EE38D89D79DF7D9BFDFAFED2C94AC62659763DF4522B625865E1BD8CEB5C99119D38BB26F9A24969E45E4AE4926BC9D35C0F999D78F1642596C9F8C5D0B916632A1791C6C7114B48FC89ECB2FB37EA6FF87F525EC3C7B965FE0E709766C58BD9D6EBFF00990929AB10C723AA9B62E0A22BE53AB2F98E9B5D48938C6B08E9CF4F0460BC9D55711313B2F132BE5BC43C0890B11891EA7B31D50DD326D117479ED5FA7BFE27274B0C431776B650CEBCF4449F535F27C2B131B1F323ACEDE9435F31CCA47FA7B13E793E1EB4932B088752913EA263A3E93C947563474FE9A24889679C558BC1E09F529127F82314FC8BA76F834B80CBFB05FF0011D489CACBC21A2B178B2F34692B6F186757A9A2364BA8E4C8BF667423A516791B488C9CA4C6F91375C8E49176745FB133942E4846CEA3A351ED8888EB221F818B6265F06AB1F2C5121D36FC8A3435649682F08A7EBECBFE1AE79AC3E052CD619CE56595B65251F275BAFADD1A97820AE4450DE3ABE0836B8435A57CC6A2EFCE3A5D3A564E91AECA127144DDBA2314C788626AD104D9EC21EC4A52542E88BA5428450878EB2F72C8B443CE1A3CFA8B2F16597D9B2CBFE05299451E0B2B28F1865E2B678DD39E956757AAE6F922BE6B679641E965F18AA3ADCBE4F87A3AF1BE48ABC246AE0AFC92F25BF04A570C2E0626478C7939848786F2B864257B10F1D55C1A1E2122ECA1FCA6AFC9FF00C11457A0B2CB2CB2F7597FC23A8F086FB143C5D6C5787C89659F17D5E74884C7CBE0A21F48893E09BB7C9D1E9FB8E5F33B190CBC2A67B16BF05A7E4757C0D2152F25899D413785CECBA447A9CD3DF3FC21AA67D2EC5F94291D4ACD89D966A38C5ECB2CB2CB2CD46A2CB2CB2CB2FF0086B74377863C7059CE50D8DECADEC9492F27C43529523FE0BC10836F83FCB4A212B43744A72D44E0B8A2F44493E6C8ABDB63E0D3C66B15C8931724D71875A45E70B158E9F538E44EF6C89A2AD09D10F99938E146C7C6749A19A59A59CEFA63545FF146E894AF673B561B2F1C17B10F649D1F11D7D6E8D3CDE23CF074E3A5593EB23A52A63E513474DEA67521A90A0A3F512E5F0276358788F4BFF4C971C96F67288B3E967924B4BE04AF817CB2C2C2188D4E3E08752F2D11B6691C74B2857166AB451095704D0E27F91FE4C51A349A070341A11FE68D08D0848A24B81FF14EA4BDB759785878F0792B63DD3F04D7CE461299249314A894F8A1C48AF6447C1D4E0E8C7DC94E8937390FF025489E22B511E8AF71F83522B578DB670F9357B13FA6F1E5EDF610F0A6D11EA0A49946927034F06912341FE67F9DA1434E6B6556DA1138D934D3E7F8A753CF616DA3C762F164D91E923A9F2C48A7263F9493AF27B91FA8F27F9DF929451FFAE4F7CC933459D2F95D127C1E49AFC1F335C1586B8B225A47BF24A3EE3F1451EE71E5ED43C21214591B45E2ACD08D0848D22C3C7815BCF03D965D0D9D495BFE24E490E6F0B17B6B7D6CA1AC36265E671D6A8945423C1EFC935ADA34F38E9C873A35264E56F8145F922A2959D477872674FC8D2A25277446151E469C7C09A64BC0D8E3CF031F02E51E0FA592FAB82485B9114259450878B189E1E1165B3532C7BA7F4FF1293A5B1761EDF2565E1BC358B11D49B8909EAF249D94ECD0FD8D3C15C10E393EB62F347F8BD469E28EB3AE0B4559A5A13A233D68FF00351E4BE393AAEE348E48BFC9E09F28B2ECD3C5144962F679D91627D8ACA38DC87B28A270D4757A3EEBF73B13F5BD4DAF08AC259BD891FF000F199CF497658DF039D0A68D7126B50BA74897043A7ADDB12AE0EB223CF0C495510E191FC89929D1D5FC9E46A84F1D27A59C48EB72E89F0AB11B352251BF07312D3F226916EC931E5F0C7E04566288ADC9F6ED163CF81312C755F0383F395FB4DEF5EB1CA893B7B96CBC3DBC9596AF8349A46E8723C946928E71E0D4C9CAD509164392893AE05072E4962314F925E45E4428F1C8F91AB291142D2CD55E4E18E345D11E492E7625EC2434B0958A0463D8558E3BCB0CEA2E04B83AD0F7595FB5597B17AB6E91277DC43EE7538CA5965EEE9D13742F23E10C4AC92D38F720B5132304D592E09220EB8345AE092E391704D7BE20F9252A1A5E70C8BC5A1724605773C7774A24E8BB242244D24217EDEBD5CE578AEED145767A888C6C504693ABF2AA13A24F28A1C709D1AAFC8A923C8D69C49DE24B93A4F926AC5C23A8EF1185B12270B1C46E91E4AAE4973B12C28F164222EEDE2BB379EAA6FC1196974C9322EC64D66F62FDA97ABF3B5F7DEE7C89567AD2F6CA1E2E8B349254591626BC13566926F9CA22F81CAF81A34D908E9597127C33FE96485C32BDF1588558B14576F8DF597B7AB56591659288D1E37597FB3A17A96E894AF17EAD63AEB81622345592C2278427C8912259444F7346A1452C56192E5EF45084228498FB487BEB1451E3129E9E069491545146AC389469284878AFDA17A76E8732FB4B63F41E31F10B9D899F4A2F1124868441738932871E0AC448C375138F250D6CA1661E30BD221ADB2564781C6CD2388E2F651A4D25628A1C4712BF63AF4CE491FE83977EFB6B651FFC18F827F33B1ACC7C93F025C8C89EC3288F05921224243421628F1B3DC9C28A28A28A284510F1943457A1470F178B1AD89AF034389A4711668AA122B14691C2C70657EE6E746B2F6DFAD589728A1AB28A170C972562888D090D6522868A2883AE04F734346928ACB4210B285E893C312CB62CB4738795B10B179638D8E15FACDEDB2CBF43297B7A47DD7B5CB0E36B1E4A28AE0AC3F0446B75628A28895B5E2A8A28A28A34896D47BFA24F2E5CE1E10F6363A63C2796216F701C6BF5EB3509F62CD48BCD9295179A2BB8BD03166C94AC599C7DCF1843F02CC7722F6C73E36B176D0F63EEA5B27E443C2DBE516496132C4359BD8C5CE24AC6BF604F6B6393C210D898F6DF69FA3621E1EC7E0449722F24462CA1ED43D88ACAC21E5E10FBC87DB5B26409121703C21901AC4B11161F9163DB2FC0B0C97DF3FFC4004010000103030302040405020307030500000100021103122122314151611013327104425281237091A1B120623033C1144043607292D124E1F1505382A0F0FFDA0008010100063F02FF00F6F5C90DF75E5B25EEECB5363C70D256447E74F528EA974A0FA1A9C3E94297C436DABFCAC65BEC8B7CB6BDC3636A824D17386E15B521D2B196F5FCE597180BD48B698FBF86F95136E9FD5647E88B1C4B87F76EAFA31051F340946311C841B51D783CC2967E9F9C65CEC00AE71F66F8884791DD51783B1E135D3748DD0EA320AC7A791E1D8AECB0659FC7E7149C045808652079E7C63C595226DE152AF4E698A83644144B39CC2123750A3A2BA99FB287E977E704BDC1A3BA3B8A5F2B7C3BA95DD4A9F075173A59389E3C319F0EA3C3BA9E790AD71C71F9BDE5D08A8FFAB808D47BEF71EAB65D085DCA6CE0F4478FE87D2AED1F89B3CF0B08A1F49FD95CD28B0890B0321105A3ECB1B1EABCBAC082395BA91F9B363DD2EE8D4F602E6CF016A98EC8000FB943CB799E56AD47BAE8B07EE518CC78F453468BDC3EA1B2F26BCDE395DD46E14B7EE15D1908144A2D7A73796FEE9F39809D7642B81C7E6BFE1E1EF36CF4464C1EA51CAEBEEA7C3FB792AE5C47643AFF000B92A56DFA2A7F0EF834EDC2BC083DB8445D785FC294442DF1D1621E3A273E94DBD902443DBF3057B5DBEE14AC20D9B19D020DAAE870C5C7F3505D2E7BB6685ADB3F4F65A59908D470D3C205D5303B286371B34051E98EAADD8044BEE73A3EC81845C4C37AA8B41F756318553F2C6B0438AA3F1316798C9B5A5000EF0561755B6513B15836BFB23B8578F5F28D403DD7650BA95D4A3989D937E1EAD48A83627F348D477D87546A3B7D91B935FA8624C20C66004F6069822329B4DB89E8BFB94F0A1D1ECE4CFA4EE7A20D6EC11FAB72B9F6850D8739E602F83A6CC5983FA2824370A09D8C78E3053B36C2F2AB63A3D0C9C728BD84163FD4C41EDF49F031EB2AD3B84D8DD093040560AA4B062DDD53F329687BAD0F9FCCF7547FA5A848B18DD9AB534977065347942986EEE0535B27CB1B09DCA25D4B00C02D0AD6CCFB2BB9E254B9A095A608FE13722472ADCE3A04624A738B6E3BE7C06E00DDC152A75CCABE85588CE794DA85FA5C32B1B78E7947FE253233D9074FA7909A19522A0391D57960C00361E19C2DAF7765262E7745FDBD53BCB013E5C200B826B0E9AB131F99AEA8FD82D50DA7386A0DA6FB8AFC5FC369E55B4A5ADEFB95700E45948381DD035753FF0084D7436F2A01731C04FBAB439E23E61CAA9ACD9B09E51B98E6B8753BAB5B4AD6BB74006E7EA4D0C24F5555EF70AB273486ED5A64719E1073DDA6709AE05C58F500CC282A08C2737D6D5D940DD17FF08BA7C328150C76FF00BA6B1DB8E423E9354F0ECE15370370798C26D46B7D58FF00A531CFF5467F32DD51DE96E4A2EA8347CACE883623DBC2E719081897FB286CEFB297922DDCA73A5B93FA05A9AF93C9E545F970F94AB5A0B970215D2D7FFA202E108B3169533BAA56343C4E5AAEABA5E78E89CDF34794EC4DBCA632F06CC4F544F981D9D8144D276C84CDA50705A9B7755E77C3BA07D2B5375F5EA855A5E93D3853E19C27BF866077F0A94C69BB729CE71636930C83F3A743BD5110BCAA8EE7177E65493010A74E7CB1C7551BACCF6438055D547B046E60002BEDDF32B3BA2D18CEE1122EA90A082CEA485A016B397276AB5BDF945CC7401BC05EAD67E62160E3D94CA22353B021347987CE673DD54A72DD3833B1552491660046A4FF00EEA688875302E1C140FEA13AD7964F7D95AE709E1C1195A86FC0463D33B2B1C16812B59B42F2D9EE8BC373389555B3E812480BCDAA7CC716E65369E0B18763BA86FBAB6E92D313F9925BF270BDD6FF0064017B19D95BE5C96E6F8C4A956CC4A866FD561A48EBD536AEF2B49FFB42CE968CC754E6B70ED820313BCA8D3F62B1EA4D693A8EE3A2E138D36B5F8F577422DC83249E553B81152770773D554B865D9954991F6087E1901FB829D4082C0D9DF9570CA7380D3F4AD0039BCB4A74C81D15C1C9A3EE4A009227A22E6D47D47772AA38E2539FD3747518261D0A08DB947CA873599123844B191021C07CABCA77A7AACEFF0098ADB8C5C6022F7986856B01683CAD8C0DD0D0E42D0D6BEEE51920BB929D10D03945B0D774EC8030D9F98EF08B69B207D5CA25AE23BB93435A03872AE2569C2738E5CB1BAF4DAB409A8795F518E56CAB93065C04147CC6BA0BCDAE4EA9E65F64E98CA6DF2ECF2569165A7929EE25C4EF3D53DFE59A5F1035307D4DE898EF98614116B95EC3909953CA873FD417E1BADFED29C6E995943E568E89BE5868109E6E6BA55C05B2131F9F31B984FF003645F9B9AA5F4CCEC2B3707FF74F6DD366E3FD426E2247E626A3F646A7EC8309D3BE4AFF002839A0E5C7950C6E7BF09A6A3D4361C84E506D2A71EE77504646EA47863EF2B3942D11C616BD3CCF5F0713018A499EC11653D206E530B69F9AE9F49E50A9E4B65D9219C270A0E82E77CBC041B7B8B2320FF29CFA6DD4701A8FC451D25905F4D5CD692C7180554A41A5AE782D685F0868BC834B489FA95C6A06549D4DE0A0E067B84E47181B26CF86D2B7B58132E6E895D0422F77A404E034B4F0BCC886CAF29C4751286A6B1F4E0B083BA879F2EA6C41469B8F9D4DDA9BECAE63A47E60B9E78524E4AD58E8BAF6408D8F0D3B2ECBBA07950A0CCF07A20D61974EE54C028BBCBD5D216967979C9285DFB72AD9F6C2D125A34CCA81BEC100F3CE20214E223A8CA249CA351DFF000C7F3854DF639F4C89C72BCC3696BBFB76551804665BECB5CB5C303955698A84B891739319E6139C5A7654DC1F505A60DFC2F2ED8686C8774555AD73B4B0B49E87AA2D6BB66E3087C3D66EB3B3C22E891D16DE1BC046D30070817FA06E9953330A5A48939F650C8A665368561879992AD9DC4AA155FA5D10E8E55D485C2107D26BAF6649E887C4D0ABA9CE8730A91B8DC7E5FE353CF09A6A34DA7650E6D87BA3D02134FEF28349E55A05B0AEFDD6A7B610FAB7B821F5F2A6A820857484358CECA0955008716AB8B4472E50DC4FEC9B6B9D0DE577037524CF854A6CC071C952EB5AD736D01DC94E60730D0A9A882BFDA3E16A0F2B6B5362A115E75105557EF2772A990D05B319FE55FF000E240F548E1798E0D619B49E80AAF619A94F49EE1798FD262309B53FFB7FAA716EE167C4DB894C6E6DEA9CC24BAD41AEC766AA8498A8D221321E490139A5B15B8C2BFCC16DB394C3321ED3B1FDD547996C3B6EA986E6DE7A7F055D46A6A6FA9AF5F4BBA7E5CC93014800539E53819C2FC3667EA29B54FF0098E968FF00CAF519505CFB1BB4237E4F751F27251A431DD61A1C53AA3CD8D0A7CC6DA5398695D9F5A1A33DD5F11D93AC76A9D90376BE029272545B9FAD1D36BDDB92BCA04060E8A4CB1BDD36CA7637D2A9B59373443A429DD6328DC6D68E558E79786B651B6986DA23DD39CD04D3771D1798C69CFABA2AD519E8A7A607EE9C290FC56EA07EE9E2D680FD27884D0D6E1D0CB9BD13F1AE4E530D36DAF0F82D5E736B1675CE3EE9DE5D469237CF841F02674EC853243F9909FB024E32B55BB2B8892DE46708D434F238E89B54BEDD3BF44CA44873769013985D004B44AAEC76ED21E3A27BF0C6D4302382854F879D2350E5517BC96C37850F3177A279FCB724EC89234F014596CE6E52CAE1FECB35ADEC4A6EA75503681B29C81DD3594B1D94799791DB0B435DDE0AB9F0E7F4276449C1EC993968DD02C769E842F97DD439B0E5059FBA74FAB85F841C0FD4518331B943A8C06AD72A772880A95364EF74ACE5191E1D57C3D2968AC6E121165675B3BA0C20D8E3D51651735D225CAAD565AE0E63B109B665C8136873DB803794D6BDEDBC73DD35C68F94E392697A4A73432C2DDE554100173E308B1CC2EBC709C03AE5A37401214A0E6E4F29FBE4AB7D4E7616D048FD53433153625DC954C6EEE539BFA145CD6C5502E053BCC92E023BAB722D21EDCE1533566E236554D33613B3484C6BAA0967A090813970C1FCB6F2FF0054101A87BA73EAD2B1DB12390AFCD2A5C0EA83EB3AD6B786A69A6CB8733B271B60AE8D0B96E3847BA28068449315507B0DED1F294246AE88B658D9DF9214E6AB91701148663BA13CF0064A356D1F7505C25682B7CA743E0B463DD39AE1046EAE85DFA29E17C3B3D4FF005BBB2F85A8FB4B3B6E9E5E3F0D8C749E8AA4489C42735F96DB24F44585D6BF70E1C84CA8DCDC6DCA06E2C2EFDD39DE6543F51E9F65E751ABFF00AB1EAA6445C850B4C33E5E8A9B80C82998D6DF9BAA12A4284E0244EEAA5371813828E75009AFF9BF845EEC073FD5D15367FB4488C4377280A62EAB129B2116D5683289A476403C827B1551A345574DAE2B5B7F129BB2FEAAD77F92FCF753F968F70DF85CFB94D74031D5124087193D423A8BD872DB945BBF5571D43F609ACB61BFC28A63F751BC0FDD6313CA05B24A9D919049E21472B71EC51B62421A6D7ED6A1A7F556B5527000953CBB857BE99F64481683C21E6629E6E55A899C3D90E1EE9B71FC420B5C0749C2F8633CE4A7D46981ECAD2E8CE80AAD5AB52C1505ADFF0054F7B3679D13C2A94C492E6C972A6E69D37585DFEA9B48DAE616C17742BE21DBB69801ABE1E941BD99FF00557B9AEC090A5EDBBEB6CEEABB9AEC16CB78215426AF99CBB30AF783D4531BC2F26ABE2AB3601369D4A0246CEE47657444AC27716F441CD8CEC816838904F44325CC94EBC0F2AA1DC2149B4800E3EB705AB2F68DDBCAB81860CC9413C46F3253DE5DE9C880BF10DD9E4613FCB334DCEE07A551F31A1F6FA4B3E609941E006F0E9FCB339D9060D828C8EEA0379DD4088DD35AD74376D906B8DC426907F0D9FBA9D36F38521A0273EA9BDE502CD6EE7B2B81FB2EFD15AFD2E576C0730A5AE1F7521A5D9DC20FA9EB4639F01C0EA55CD7EAEA422D2F379DFA954E9E757AB2994A8B1B4EDF496A6D78F4E1FEC9EFF94954E9EE04ECBCBEFB2F35DD30395F06D6D42037516F44DD36B5DE91D17E243F880BF0A3CB301D4CF0AFC803E44EF2E5AD7B64CF54CDCB47D213057029826DD48CB05A772BCBF87765DEBEAAB32ABF1820C6426D46D42EA231070579B4C6826139EE073FCA82AD683F75546C770995DA3D3C21E53BCB7F64C33A6ABB03D82699DC618ADA936B364310D07D3D95461D9A76ECA5A240E14D4D2D00DCCEA9C0365EEDA1BC2356A45FD0AFC030E204CF08D8E2E277408E328B2B31CEB46ED4445AE0762B09DE67A86DDD6AA7FA2D1BF4FCAC208D0D11F744CE5428833D15F6BA15CE1A3F945DE658C27217E09606748CA9A64DA376F2B2433A4F2BCBA5FAA178BE7EA0896CA0E8D2FC809D06D5ABD32BCB8B709AE1058775777D946CDEAA760A0096C2CA6FD23950DBB6E153AD1F88EF54F44C2D363865542E174E985941C440890179CF6B4DDB764D71A5E686E6D684C7D57794DC177FE13BC8D745AD534DABF1037AC80858C0E319B94D39B5D86B5305DA59BAD6C6CEF9D93EAD819F0CDDE37556A196BA30D2BCF0DB6FDDBC8544B592C7725798D74B8995F8A4B5C4E9236531088B5361A5DD4225A037EE9AF3A5A46553DC899055A1F6D9D7DD07178026D3DD5D1E974427EE0EE558C37540331B22CB581436D1DC0557CDA9A7DD018713B16A377E8A766F52AAD32FF5705798C7E3A057D030F1C4A679822A7216C838291CACFE54E087142D3E617F445A58E2E46A55C91B04E7069AB79924610FC2202CD421DD829B36DA4E10BB0DEDCA06CB23945F201D82DBEEE5238E8B9681D15A757BA6C9B45B25596968E141C041D5303E94C6B468E615A4904755CBB980871D56660F52994E965C760B03FCC680F9E552A548E2F0D77543311B74556222251D32E69DCAB7756867A724F44D14ADA951F97FF6A6BCBE01C58784E696122EC95752F49F9BA2801B0D19EEAFA6053246E426963468393D538BE97E2F560DFDD0869152321C9F4DC2F0341010A4E389F546E9FE6362ED98835BA193E9990D29A2A7F94E1EBFA4AF2AD92E30D784D8CC693EE83DBF742A8D05B9951B9D961A48BBF44D886D56AAAEB831E1D36FD4A9BC69330E08DFC9DFA2A8D925D1A4B558441237EAAD182501D13BBA8635BF7552E7089F521994D9EBBA6ED33954EAD1A9655E9C2788B2A6EA1E2D23AAC31C63B22DC82CEA887E972969F0D9632B3F946CA63DCC2B88D2330A441795E58CBB9570461A1AAD032A63CC8DFA056DFF00A9424E472E524881CA635ADC4E9EEAE3536C16AD2F8139E81075475C7A27596894645D18909971C2D31FE8B7467F127308971EE8BA61CEFE117B7F0DBB6795975E7AAF3C3A2ADFFB22D26C7460A753208A80C7BA0C232AA5131169BA53A9371DD483BEE5368B19A4B85C7EA29CD661F4F0E1D53A9B2A5F4C676D97C31F8668613EB3CA0DF3311056AA627AA3D823E506F96721C513F14D36F041506BB739DF2B454879E9CA70143CC68DA46E9AF2D8F71B2BBCAB0B8AF2ED151AE1B4EC568D0BCBAD0DBF6EE9D66A69D8A734906DDA5380391957365D4CFA930B3D2E4E2047708387CDB845AEDDC23D9137C4C6506D502A3765E6D17CB1BC72177183E22060141ACE373D7C3CD3B07655E0DDC42346B3840F4BBAA0E698775446C7A85A9C49EA14B6A00F1D96AC0E6D409C8EC881B8EBE385D7F280B8A7139255AD6C9DC84E75489E01599408A7734E4426B9A430F210131EC3742EBBD918686B3B22D8B8ED051A7BE72B59D8230E33C4A19DBE5E14B98D0E4F7C1443667A28F987ECBD120755C927E5447CEECB8FFA264CB8B449536C13C9E8819CED0512C234E5DD91AA69496CDB9DC141C04D423AA69EDB2A4493EAC855E6032B7D678461A05E601ECBCA63AE6CA7B836E75313DE5567BDF88933CAA96DB0EF501B2A0CA78F9729CFA74DEE744BADCA6D8EB7D827BC82F60D02A2973DD071BE1115813C4A6904E320EC83FF00DA1AE8D842351CCB6983111C2BA25B1E9514DD8198E54F91BE2E6EE1436AC9E4176A080734DC3945991EE893833BAD6D10E100F4463D3F38099BDC069551A354B71EE9C5993C7643833EA56548C895ABA4C846FDBBEC4235BE1DB8764B3A2C88F02D53C2ECAA53AC4B672D701395F0EC7BCC3A4108D904460A68A9AA7A26B98443C202D974643795249A2EE6F5A6BB4898CA05858EEA02876977EC8A17116AFF003210D633B415A84ADE16E3FA72405EB9F65686124EC14FE4A171D822716F00AFA7B2218D0D9F9932E65E46D286868A9D176EABD615AC79BBB05EB93B65369347FF00922F12FAA700ADC5F127B2EC835975EB510D41AF6871EAAD6880B3A54333D548DFA75525A03CF8E44A0C6097C2F34E7CDA6438719543E1BFE3DE067B1573207B2B9EF6B27AAAA6A3A59970BB9E89B7386ADE5536DB25A1714C4F54FDCCBBD415314E72E7027AA3B8A6E19C22EA4C2D601F728D033E6561B2F3298F31D67A552B6996C1D6D09E6748CADCBF1BA752BC16ED8E0AFF0067A4348FDD50A0688000F5382A8C6B4003E50362AFF3003D5102AB8538F516E502E1ABEAEA11A7ABCC6F532B9FBA1196A3C15E55500556FCCDF982F29C7FE94EC6DC27B8603F31D139BC386426550F2C6306494D7032C29A3E46AECA5C21DD423F333EAF03C8403BD33B29600E63B508F955C2D30E041FB2A7525ADAACF97AA2D6819DA7828FC3BDA0381D27A157B5C5AE98CF088D2FACCE062E08D76480736B827369BCBDD181B221C5D277B93DB575D9DF288F28A267EC4269F9821F8C47DD1612E6946CA8E9FEE0892F215BE64F429D3524F0ADF327D96B2FC7211747EA531D4D9163B3D9178F9B31F9286972775288E1470A1B45A5C50C965DB9D95BEA1D6502034FD94D6DFA27BC71C05F8A61C7309D023A1F00E6D60B89EAB067C0C6EA1C74FD4772A1A20228660745930B195255CC10A892634CDA83000FA9B9B360535C5967464E55773D8CB0FCBB957B9C7D5E94DA86AB306036554A95D81ED2220AA860B29670ACF406E02732DCD2244CA7D5F596B6F0C3C2A55DCE87B84DBD53EA380926D92986EB6989706F54E2FA845F920F5952D78B6217E2BF6E27745949B6B1EE95480266761CA6B6D0604C42756A0D0DA91C72AFF2F7FDCA61B8B5B50E61069300EC51901D9C13C20E1D385770BCFA267FB53DB1A6230A9BDA1D138471970561F659CBE38403BD278553CB7596E5AD40CCBB75231E39641ECB4E4742A43096AD4D21065BAE53646D854EA31D9F9D8859C9E53DC0DA647DD7E260B76211F2CF9AC3C744DA8183A1C2FA4ADD48C2EEB5FEA13AE1A4754E14CC01C7558FBA1A9D08CE560614DF6FB2873C98C218B6766AD8BDDF334F210F24E0E63A28E7F24DC77C420D383D56D2882CDD430B9CEFAF85BA9946D241EABFCD90A1AD93D3A94E6BF538A19B8F6575D1D0156B1C4B8F0D5F8BB7F2B181E11742B41D9004E565DF6580BCC32E3D1686C226665383EEED6A6DD50BA0447654D9688714DA923CDD8325558A645A20B8E230A990F90C6FCC2211A25D37B8D463C6C8B9B26AD330E13C2B1DF3FEC837E1EF67FAA69AB9AB972F2C8B6ABC35B6F65469EE43E1D7AAD16D85D7774F87361FE971E216883D6C4FA2FAAD0F390C4E7810D9B43935B4E0388CBD31CC9F3841B9C83EA902AD468894D34D9889DD134DDA86AB3BA87F98CC44F4429D5FC4B700F4EEA2D3DE55B6C2C883FCAF3A9CE9CB9ABCC74CB8F1DD303E6CBB05A839AED31842BF4DC2782E963B651391D11E38211FA422E6BB07FA375BCAD912D6EBEA8B9A4083B2B854D614F07841A361C2D24B86EA67251063DC2D32A1FB8F089B5BD4286EA7F5E1644FB1423D4A606780A61C3DFC0C2188EEBCCF5C744DA95371C22EE53FE40EC80810B3F9219767A2806C6FF28BB1943958609EEB2878E0A0035BEC8D31F7774F015207646DC0EC8BC8C1C82BEA3D026E9C959B803C297144EEF3D15A72E19328071D8E708B66D7704A735ADB8FD482DA56D9F0BDB178D9537D525F2EDDCAAB3E5AA213DD970224C2A0D126800012ADA606A33EE11A84C10630BD307F78EA9D59A65BE99EA89AB8AAFC8CFE89DF0CF8F4426303EFFA431798F2D209B201E1795E6967248722E79D73A5DD53E95D79F58CE0201C6C27E61CAF8675C3D05CE714D781B69684FF00837550EAD4F23FF08C61E370774EE55502ABA8BC88CA8F3C38B3885E904A02D86F0BDD1ABF0FEF6AA8DB832BB366AB7E22E169B7FF000A952ABA6EC04FA7851B14DBB170513A4E160E90A79F08FE87061B4F54C7585C072AF7C4C275C6020369EA896E1ECE2375B61DC0E1626E9D95AEFD9100123EAF080AE2091D14DAE12B185397472B3FD38548897B5A65C004D70E7C60FE4739C37E11E49591BECA6723695B4CEF0B3BC48852813E1BC04D14D96B8729D50E5E78447792AF73AC05444F03C18D654F7851EA774573E9E634B4297E34C9E8AD22E68CA2EB357D2139EE6EFCAB1B903A20D95829B7899CC216C41E83C35738109A0665A0AA369DF33D102C9B9817C48A8EF2CBEA60147E1A9455C4CFFE154A4D30F680D6876158F6C56A75208FABA2687E0D60017F64C2C88F5C1565B7D6BFA6CAF7BAD6F4E4ABEDD00F2210B459CE908D405A6709A25AEB5D7421509996C7D958F04B3390A9FC3B736630ACA112DD2D73536A499A678DCA6D5691783F76A8AA35F51B15B0F306C51FC3D071A4A696073A77056DF640787FB4B45B547EEA5DAAEDFDD0D3A9A784D7E6E0AFE375B41A67641FB1032BEFC2899BBFAC85805D1C11B2338ECAD7027956BFEC51B70E0A7952D6DEEDA147A428DFFA0ADC00BAFF00410E308DBE9558ED7E159F14E2EA670D3D107B0CB4F877FC8E6B799944BB1D2173E101D017281FD164E9589FBA80AEE02127CAA68359A5A4728003EEB547B05B5ADE8B192382892DB9FC00364EAAFD4F5ACE8E806EA01B293794D8692C8D33C775558C36329889EA57F9823B2198775842DA9A3E90134F9A69B8236D4A753F6573A91A8EFED47F05C40DC442356A510E231047E89C6D68A912D6F42A9D0A750936EAA9BCA754F2DEE9C5AF4E26D6B890D02537E37E18071C5F1BA6D7989767AAF866799970FD9796F186880E9CA706436A38744DB83800D3901398197B6673CA7CD5A94DD3B0551F78B4632214EC535959C0B4B621A13435D7C0B6F41EE9A65C763CAA95226A89C46C9EFA861ED573643BA8E57955DF038B90731E1C0F44D2D21919D9398E17765074944037656D8508B2219D1163719C15E5BF56B45AEC522BCC68D511EEAA36508209188521B9DFFC136ECBCC820F528EE4AD0C0D1DD03D55DFD300230E59127A95ABC41E14F41B23A47B044FEC84E40CC27D279B1BB8283D8EB9A791F91D27F445E795845724A8951C205D6DBDCAD01BF6507F75A486B4F00650D2B1046C848D7DFC039C0DC5408942A5639DE163DF6511BAF2DBA401943E97709954C0737E557D58A73C286D1F624A07CB8EBA94B002B6F1D94BA9B67D903E5B7EC830345AAEBCFB708556BB474E8886F2A5EDFDD0B5B0E6EC54E579C4488264F2ADA6EBCDA0990854020DA0B9AEEEA5981B495599488686FEEA8B0EF1BF74E300550371D1369530EB59BFBAA356989653C0047EE8D7A33E79D0F75BA55EFB4752AEB011EE9946A5974C6C9CEA47F04990F6EC9ACAB8A91BF550E12B5BBCEA7C4EEB813CA041D94F109926780B4EC72B7DC2B4C9B5775D0947AA7193078FF08EF076014641E617255A418EEB4EC7FA842DF1E1A4C2CAB40C2C22460F846D221147E1EA9893A49FC8D2E52727C4FB21F292BAAD5A5AA7E5ECA4403FBA738EA7752889B5A14537CFB27DCD9C6E81772BA7643F728B9B1D8A680E9233283072ADDE064A06A4DC727B041C4405A18013C9C95ADF7908856381F31877051782D0DFA5CB712B0ADE565604AD947F492467E5421A02A8C710D7332329B24C13940B487F50E5758EA6D6BAEC6C9CD397CE31BAA0FA34F330E0A4E863F1726FC3B6A3834EA2E844870888013A8BAA7E1B0FA79715E8BF1B0CC2A6D73EEA76E2137E2035AE230650A35434D0232DDD7E196B581D32AD9D912C36BE1345470657D81EA8821094DA9F2B4ECB4C185737E51109BD617F7708170B6A0DC29FD91E3FC326328E70A5CE3ED3FE16325461117442C894F1539185B2968C8F10CA9A8460FE465BC37C34E4F40AEA9BFD28B8B65364E3B72A293723E63C2D3A07F2B2A6539ADF4F5E7C054A9A5A4E02BDC086031EE8C6670D0BF104CAB0618DE50A4D18EB08300D6893AEA142E1703929F765CEC4351686DD5DDB03F2A1BCEE654A08C180A91654D63F745C1E18D3928469A639552A567975A6075444EA4D1FDB0BB4A747AA642B9765037E9E05AF68737BA756A2669EFDC273AF7389DF0A9D46BB4B4CA79F900928D12EBA99C4EE9E751671AA21798D04B87050A27537384D7FADC311D15CFB83DA538E6E95521B318CA753DDDC11C2AADA955CD7DB10B46B27F656D49F56FC20FBB2531E04C84E7B1B3FDAA2A036C61519EAADC9B94FD8A6B80C3B8E89A469E61537B45CD69D51B85E69A9A4E56977881FE14E15E54B5D6AF50FF037C2994E7870FD119611DD3CBD9F2C829B5693627301386D0BA83E03B2EFF9167824CACD4D3EC8D360B7BF27C21A07F738A0182543435AB2F9EE85A102F758DEA85A34A3B26B9D36056CDA00C050CFFB9636EA5389E3002686E0A246AA8FFD96A790CE4A1E4B60010867657BAA0150FDCF80684703EC83B95A8E16331B05AE40E023F105CEBC8C0D93DC61D981D539ADCE30BF106A2261078DA13874E569E11EA105839E88C896F29E0B5D6E6DB4A0F1B4DA255735DF1774DD175DA385044A702C3EE8794DB4CC10516906D2D85A21E3BEE8D589BC0D3DD55ABE580D072139E23CBDF08B89864FA7950D56C4CA874BA370A9177445EDF9BC016E217531C298931909CC3B4C82BF601131BAC39B634EDE1BC7872B267FC1DBF453E6E3FB8217542E9E02B43174FE8EEA4290167F4F09C2A9A85AA0E1BD95B801BE957B980F54DF29D91F527BAC8A83382A3AA0BBFF004EFF009079FD15BC784933C42CE86FF2ACDFAAB06005EA3722F7BA6EF9510309A6A1CBB609999F659CF64D1C9DC7451C3539EE3F657DDA15E440F942349A20753CABCADE4F445B4A5C405106E3BF6513B745BADB3D5491BF84F0AD1CA33B7F29F02E3B4F455A9BDCEA8D73203F8056C6FE0AB1BAAD9692550ACD0F80208E4E11A207E1BE4B6785503FE9C37851F3BB54A0E6644E7C03BC328C709A0982065536E2C2365878C76D949C872B80D2770AF1B1C5AA0FCBA4A0C7BB50DBECAC6EA07757BCCF64D701B1460841C4EAE5C8B8FEAA95319C7A9169882872DE9E12D89415BCA63BA3936D3172219BB770BBA3721A62395BEEB41FF0A65DFD727C71FBA6CE67A27C1CA249BBD90AB4EAC3DBF2A32E6920E4152D3F708364D42DE61496C395D48CF2116136BF85AB8520E5656FE192B1851E183FF3ECFF00FC57468E0274482564280141027BA69333C228655DD3251FF457BB39C292406ED8DD645A17E1881C94ECEEA1BF74EBB0501B750BA0E89AE7C06AB298247308803650DDD66A03DBC01B901B4AF2DB1FF53CF847085B97292E2D9E13C5182E38C9401D25AF26EEAAA6753653CB843FA26EED830E4D7C5CD6BBD6A9536917464F64CB64336421D9929B70C9C4AB3D2E18CAB2ED944AD260755BDC14ED25301E42A94DCED13CF0BD53D82E50AC305A723EA51B740A1A328CE1BCAFA82D4EF603C376968F9536939D038432D8517483E0F1DF9F080B50C15BFB2710F049DC2262D9E1488EFE16E998C4A88CF5FF00717045ADC2CE415109A08D820F189C7BA2320AC4A6D5117F6E535B9027AA068DCDAA33EEA2AD375FD5A559711EEB39EE897BBEEB0EBBA42B5C23DD60F80EE9B2B28A9056BDD6FF00F3CDDB8ECB0020F7BA5CB257A02D2210B818DC92AA3B99C2B9C43506887DDBCAD473F48505D6B7A0437652FE540DBC24A6D218A60709C26004DCDD39309B8CF75946710852E10A2306511B1EA8950013ECBE5693BAB0A145BC6E57A6D0384E6B0069FACF456B412FDE785E654D5578013AB3C40E026526CB0BCE5CA95169D34B0085E6DD803842D8F7427E696CCF75123842DDDBF3709ECAB0E9DA15BC138474FA7959D94364B535DC6CB1E9EA8DC4F8664BB85DE5070A9152460A17817729C1BFA2B0B483D562491C044F1D15CEFB05310DEAA2938903840F2795485361C66E429D5C3C0852D4D53CAB5DA5BC382C1F35CBA4A0F9C6C42F5109ED993BA2104496A8EA70ACF50FF70BB928C2D27F5403A9EFD0A24ECB513BE02BB00F65BB54113CC85B6C9B311C6579CCFB84D0EA900F6565F77D943A9DC839BB21AAE53E3DD4784BB65A30146EB50B9BD54CC7BAC19FF9CCC0DB95D3D94C13EEAD3A47B7864C9E811876D985AD9ACEE843463B786785202F497152541C35BD3C219EA3CF4568CF752539F395D16912B18E5752B08091E6731C232017472B2A10707FE2745E5B3FEE085CDBDFEA957464993846F6957468E9D5114CC13F3287EA8174A2D6A6EA0F7B7A26490391EEB3E971C1E89C5A7F13720729CF8B5BCB3A2743B31BA0C65A1AFDD79721C846A23215AD37522112CFFB5796E96B546EA04B4046EFD9375B435DC94198FF00A804E376A9C2D70F36EF0A469957F446A34DF4CEF1C20F63E47452FDB81E193F86107FCC4E02B48D3BA2C63A196E2729C5ED0F2A903375412474F0DD41C858C90BCC2FCF4440CBD67659C83B1F119C04239424FF008F71381C216A99FBAF5150489E0AB3784D8DD44F860ADF0B0B500423734B0EF2D2A2F3DB08454B5FBC10BCD6C1EA16DFA2C6DE31BF4081584417EC86A90B744232EFD1434E3BA87693FF0038B9AD99EABB2DB2B51F0CAF31FBF08C089DFF00A2161B91B23680C61DD321DCE6543478ED0AC6E4F2A0ACC5BC218DD174DAE5DCA995EDFCA94091A7BF283836E2E1701D164EE9C1983B615A4DE53002DA7CE947324F250130DE004DA6EFFE1369D0D53B9EA8927CB1DD6B189F9545660A94262F40B4DCC3B10AD0ED5D4AAD682E74C994EF949E0EE131AE389BBEC9A2FD311B6EAE60CEC891A5AFCC2189C273A65A38E14B560233FA2F659127BACE4F409AD1D60AB1B8B54DC5C1DC156E6D3C231E9950A070AE2359DA5117DE472365EC5070C03841DFCA6F3C2B7EC803B9E888BA6ED97D2B49CA9E395D414E07D3C78C222DC0E502DFDFFDCC38EE360AFC4391C874999858500495367DB95B2CA1D10EEB7D8A73E9549B55F21CC3B808556B2C7FCCDF0CE1691F752753FAA82A79572C72813B2285A64A86373B92A7E6E7B2FED51B3BFE6E81EA5A4F72863272842885270D5E99F7FEB2772BA788E64C2B89FB2B79894E7C9B8F09D13E01A5D014813EC838B74EF08F64073CF85C7254C4CF328625A558349EABA9F07BE2D0DE4A1637F17A9E1319221DEA71DD32D02EFA88D8A73DEDBFEE9B261837463D1C4EE9BF0EE1FF00CACE0A73B671DD5DC93B942F9B6385A48C6D3D146EE1CABE0E10BB36E6135AD16B4642DA53ACCF64FF00329F70108690EE42D3EA2764D755F57D2BCC1A5BD15CD30426C6085F8B9F75E636EB4A043491E104E801445CC1C755E9B420DFDD46CD4D9C00B466ECCA1987B116B85AF0B3F68507F65B615BD3FA4E3847FC7CF863757394BF6ECB1515950434729CB188E531F45D79E4353811079F068896AFA0A7B43B31890A44823842A344B1D982B0DF74C73734DDCA9E8A7C254AEC9CA214158194D69C176E8B3A942371FBA6B81568DFFE6C791BA958FD5107809BDD7A7FC191BA3267C491BA904A89E114501DB9F0DF94D6F109C2246EA7C07650ED979630DBD36937D09A008557030242027051EEA9B86E539FC872707670837613C267F7BAD2BE21836199E5349399215273306F4EBB2987BA6304DAEE1399C0459BB7BA3CA2DE154CC7B21946EE7C2AD4F98602CE708B4F55013BB094E9E217DBC1B0384D17100E202C2694F7FCC84A68ECBCBF9530755239417DE1470BDB1FD44703FDC5A4268E23C5ED391E0F77214D33BA0F78171DE3C20B5AE04F2131C309813A151D5B1B5549CC6C98DF95C32106F1309C3C41939F123842511DBC2633129D29FCFBA1087FF5BFFFC4002A10010002020202020104030101010100000100111021314151612071813091A1B1C1D1E140F150F0FFDA0008010100013F218C72C63863872C70CA951F8965CAC32F17145C18E1972E5B2E2B1165B2EE1012BE0970A32EA18971702421F158A5DC60B8C570A89F3AC08208B8B0638AC240DC084D3884D271C931F922F71D251DCFB4BC246D344185E2EB1C63167297160C2936970619A9647BCCE2732B018C49D4A6030750E02CACD4ABDC526C97442CA513841243CA692A1813127582D4B25CE50B0232B91D4EE5EB17065C2D0C02420860421F1A97F1AFD621FA6C70C631F831C31C3F070E19718B82CB971858B270C4952C81292DF84A95838D4D45CAE62C373943171C241B84634C1CC6022448C257C0275035F06560C9906E0C05332352E708B8397058605B85A1AC749A60A8CB8CB44BA30C5DC7E0640C749780E04360251154A992861A31419526C8D7645CB05944DB02B462825985E060560563F0914580DC095122A9CA31C2317084750700BC0703E091BC5F8CFD1210812A54A84218AFD06386318FC18E18C4C54631C5CBC3879891632E5CB8B16318CAB9A4B6060AF886984F8056A6B28CBBC6D1C7A614CA952A545AC1B230C0C18ADC70A810958126012B0A8982540C36863D12AE0AC4224D314891830A3C009494139E2D6380C738B9717E1CC398525E08328706A83589B3819A92606983029017C423640A52592ACA552C606A3CE02E5565779CC32306A0639964659A890208426F810845065C58E131B17061FA660C54A950FD3631C32A260918C70CA95122470C72C65CBC9CB9AC121C64C254A8C560C30E2B0CBB091A272839A952B258ED36CB23AC5C205C12B09718A950823D2042108CABC952A06049804693B9560DE2C77041132309734860CBE3F9E77F1AC56F17097820CA99F6F8067EFF00227541DC719A430A88AA9BB5706F08FB31D8C5352E68D4579979B830DC69A4D2326F0C28BB812A1086E690700C1C34F855C302E0832E5E06087C084207EA1F063F0AC54701315734898DF032952A247098631C318E6E382E1B414E01854A952A2470880CA6062A22630520AA0C713785E2C6B368D31F39B63B8306A3B4A9B4A8CAB8520D47152A54A950C1585409502F1E984260470958A8E03158538471C4C636C18AC2A26F0A95B952A54A81F0BAE25CBC2E5C30B0C5EE5CB8316330C6E3D65778E76FC016B5831EA6B8B78335F0D1D47785A1A86270E3012B0211450612CDE018B2E5E2E10F81083F0210830FD43E2E2F172E5C58B18C197161089781C1B23061B0C70E2F0C62CB8B95514507E151C547894B289E49630222598A46937974CB41CB21631572ECA66D3AC3A9DCA94C4892A1CCEA2EB050838A958EF212A04104EA54A81093E470C5B136C39BE0E518DB1C2E43F292A54ACB389DE1972E5CBCB9188C5C4BB9ACDB210E21E6171448624D51063762CCA24D18D2A831DE15F15793041978106410FC4950843E043E64507E27C8F8B1CAE562C632F0C25E0B18BB8C4C12E084118C6247E2FC2E18F7808183152A70865F348AA04DE10F118A2EF9C79232379CC4B20B8692E186C84189352A3123019D4672F946D0972E5C52E1246C8EE042F1F584EA63784E84D716EC3D728E038B4E2DF0DA3EB187E0CFE82027E91B892B016E75336715B84210C5B30653063980CA589129C2826D8854B9CE1705CB97082D065CB843E172E107E0108102060F89F121084187E91F1631F831C3F0E6566F0912101191AAC1B4618C638B4A9B4591C2C3986081084AC848A3CE306E070AF70DF11ECCA5CAD4B701C88542E4B11512E84AB9C45A8B39C18306E1B6116C30704DB332344778ED9550325E771C66D759786F570F9C214E39A27207DA59878D1F58FA4597AE20FE14BF264C9F0D5C4FCEB981961254A8939952B00C87C2669378336C11418B3B972E0DCA4A892894A81C5CBC2E0B87C060CB831425E172E0CB832E5E0810615812B152A57C48420C21F221FA0C631CB1F838BC5C59785CBF8066D8958D46182F2935CFAA06E38B2550970D601C24380A9CB0DD2A55144242E684D8892826E9A4AC1C584ADCE778150971C701ACD908518AF019670728E03513D4761B20BA0A6EA2C2DAE0C7A8B6DA4E027AD9AFAFFA8F74577A5C01D0205CDAC23A2E0332704E47D4BD7D3FF6961E27AE32D4835C4484F546CC66E9460D336613AE26B1331CF72DC4E0322CB152A54A84564C185C638860E220F8890DCB845C7DA55F8AF372E5C70392E5E17061041172E0CB97060E0F9558732A1152A57C8841832FE060F9386318FC18C631C5CB972F0B165E17786260B2698A31A4AC03385CBE531C510C420832D7893191FA89231656537E0E0D0814CB256F04837B9A31C2A6A550752EB070170210937412E81514654AC043A8ED21F2B2BDEFE096055C2E1B0BA7B94A94F8751C3AC455EBD22FDA9BF284C9F2711E4F24B74B7D5CE9F1E6B72ADBDA2F30B8265F87DC52E2614F8A8CBAC563C097F180EB10C4CAE50C234C46F166F07E21D6544891C1A458EE30231512560C65CBF80E39060C19714183943E02EF1418463388B9172F5870E59065C18308305C1832E0C1830704186022F0B97065CBF910860C983E07C98C639726318E18B2E5C597912B072049ACD19BE0B2D8654C065E72867893ED05315DCC11974D9184B9509A850ACBC9DC4B9A63595477967048150A61F7C0CAD6172AF004692E3CC7170411BCA76B381C3EB26DD345F0CF5E1DC36CA0E65ABE862CE1B6B9430A817ADCED165038878FB7270FA82DFBFF00107B2E8497D0DB25101DA7707E27CCBF0928EA6EE253D4AF8237495C494977C4AB65935CAB30C247E0951C24AC7DCBD44B8EA32A54ADCA8952B3CC75818B9DE0628A1B841084DC1832E7260830837C2E70C8B8B165C32212F030841F81060C20CB860208183070B970F890C983F598C7E270E1C3865CBC0C0B810254A8E032DE24251C4D25F02C8EA5DCB2B04DE1C5E700D46911EE6BA8D90C495C10CD92BF088A9541A8D19660AE330A908291826E262B04CB30D66D2D1D4BE0DA1C4A8919A1812A140395689A7F13B57B82B2F6799747881553BBB894BDCBCA6FEA7152C7DA0A425F012187C7677175C365BB82C50697E187BBF79CA527944FD3E5EF2AFEE9E19CE72F97A2251D4A65ADC6394D4958A89645F888F52EEA5A6D180758348FC5154A12A24715A9CA318C72232B150E20C19BC0810206A54A87308691A62E386DC78CB317F0D7830608648421825C20C1830864618186084324327C087E831CB18FC18C631F8B87073040952A54A8DBE78094863EA58658C12A54632E6908065CCB84315CD136C0C3046E3B95510229C542DCE58013523B8590B41B86D86332B70350D4B8B9A40DC306D18ADC78545ED17B4B95FE5F738CDCED3B435B712A73BE91F4538B97D1A62D9182C1A54503D32C3B278735FBCD017D9DCDC42CF3388D469175E497766402CE322F31BC44EA3871043896751895A85A96CDBE15DB91F48AEA3CF51F1E56D31861A60B062C5170A3919A864864586D808AC021A225C13A8BA8BB8B508B9785E2E5CBC1084210958206484210842040952A040810204AC10F810843E47E8397E4C631C5448C6318E37C20F8387E01BC634C5431EE55CAC54AC70FC1972E3DC248D2570C43018D270CF553B9CA31BCE4C1D4BB2692ED9BA084A9DC1886B09812B0B51DF92A7FFB4ED20BFC228F0F1012BF12586F7BAE5155A26D8A7D253BEE5B84A52307087BC95A74891C32C4FB479519E21EA6AC4CBEA66C6E07DCC34CEE1E18437A55F89DF54E6C8412C61B30BB605B10C0404D70B3542C4D2699CA11AE4BA5118F10A3B8B0DCC2E5C614E7E248608AC0F8808562C12E0D60E12A5C58CBC2FE26484304254202064254095081020420C8A810256150204A952B0421F021F23E4C63F06318E18C4C319580CDE2E5CB8CACACA30A5C6F0C51630B1C3870E1C56C278415EBE2C9369C471DCD268E17586C812A04D20C1B8A6EC4256A043515C61D529C575F7383D3079FB6206FA409B4F45CADA28D5203772F288B50FE55353AC83A383E619FA9A366BD427E2E655A36A8D3F308788BEE0F6AF187B8BBD4ABB2C3963434369BE6001A6A99A31FB959AC384EA255CFDF2279EC8A7B8891E8EE162DBCF51AAC2118544DB1EC841A6596C46F99970382AA6C71B18BE049C2547092A16852127C2AA7508526DC29C48B84371204701B617F3324204A8104081F001025408108A81040830A950204A952A061592A1152A564F99972FC98C72B8BC381166E2E472AF17E0A9C5A9660B718C631CB87E17358C8C170054B861E60951E228F388A4ECC2ADC32A554172A10B62204A9C64E520498B7743BD96C5014AE1BA96834799C2596C50F1E22DA502D83FF008408158789DC8F01C45A967975007487B83344F502DD2FA8A948A8EB7CC2355E6D6AF0D4E432597D42C0BA18F4AF12E01AE23B36EF09D088AAB138E44E88E39BF32839794731D6D570011AEC1E65CE96D7E67D2300619B33AF2CED2591960CB7184A448DB2FA637B838089125411958A95A9580CD2422A262F02092482354DF0386D8C5663087C03210204084042102C9A6152A0420204300422832A540812B150254A952A57CC21FA2E5F92C70C631F971061858CB8CB97978C56A317458C6387E4E18FC8CA104E1B418C5CB9B4AC5C982B52F0772C75164DB1B073C150E30372EF1743F613DFD4B7BFECA3E911F16CD6F51D6AA50E2D830EED6A93EA8CBA8314F9A6F500AD57F72BD8286E8788F1F97AD4DE66E1DB0DDC9DEEC5A8374D704020DA1C7A2262D4AE03D84630A3CBE61F91DC5FEC604E29E260767DB058BE2AD4A74D343CC2F45C13A6105384A51CB086A697C54FA97B1745FA4E1E73829D6E502CA4F3F069AE3B6061825CA65971D10ED2FC4E0C38778D089394A952A540F8018ADCA8E18B17032EA1F111998750FC37065C32420408102041082025432AA040810820C83E07C0FD43F49CBF163963F17E03F00B4B972E5E2E2C162A3863872FC19C7C584D65B1B1731EF1C5B4AB20844B09195C9418095F0AB9C41371D54791E23B15554E83D4DA58A953B8E2960FCEE88328C2F6F703BDD5636B1AECBE64D16DB5510DF278E6881A6C39290EEA3A097AA14F64E7C9874EDC4A63BB5BBB3625B6EAD5AE2A8AFDE01184A95D637948CA69C1022D8BCFF31B6D1C3C47732DA469FB8E0D1E5A25C1F2C67A16E54F28975ED88D9A977FE239E94F52EC876692CE1AD288FB22592DC543150969854A8979DC6A25B2EC5CB2A3859D388C12A54A81081A821831C56A261D4A958B83160A7E22DD258EA0958AF810840821064045604081948D21084204A952A54AC90F91FAC7C1CBF06386318FCEE5FC0BB9708B9CE2E5E0B1BDC70C63972C72E5C31CB59E48C3BEB377B806C8F02086A771C54768C2180825447E83F2FA8B7E55DEFCC65DABC1A86B79DAF12EAABB7F712A6A80C97DC59B3D5D222EB4E0253295ECE22350DAE75F5878DC34BDC5A0A70E0948355D1AFE63812BB38E1B83E572F278834D346CBB2B738A3BD159A9AE4570CA9EC9C0770065BB256972A00718E5F996625D29BD4089B60B0477A0C7820804B6E076348BDD8F08FD9E06D44ADB77774DB0941B76F30B4FA65687CCA4AB5CCD3E69952E2699462C98B539C2A8CA21B716A3A9B439AA54A9D41123152A6D030B81245DC22F151358D4A952A108314AE5914431B8A9854A952A042082081087C62040841F012084081FA46097FA7793E0E5C318C7E0CA8C70E1CACBCB0F80DA2C60A928FB8E1C31F9387E0E5CB2E5E0B0A4BB65CDD896E109A665CBD43150358204226D477EDF443D71DC4E3EFCCD907EA894CDB84CBFDA3150C0FEC46A00EBDC44976A622C21B5A7D236483CEFEE69869575F69E61D2153CD4003C95C37643641FCF21A8F0353C26A8CDAEA2F2A8D4F13EE7D632EE86EA5359A3B799FC3CACFF005288145AA3CF0F88596D7992929E2BE62425EDAF501D81BBAF10F72CBAFF00E63EB65885398315A7D42C711DF7956C86812150258F06907750AE943BFC4EF116DDA120B00F83CE05CDF34F843518BA70F87C6D81C962889861DC6C41832E6F2EA118AC12A54A95A9C7C0A7E056563CE2B0A9503E0012454A87C8327152AA1084BF89F121FA27C4F93970C7E0E1C38638715738C5415F059782B2B2EE3A4DD8E18C631C318C70C70FC1FD1BB970D4AE7285612D6179B2342728083780EA532DC171A06889829D9A3FEE50CD7C3A8829CD7888F67ECBA8877C6AD8252AEC93C2540486CDA78977836DA5C15EB76341032978967E20A79E896EDE855C454CFEF00D8772E0A0AEEBDDCE346C854777545EEE2500BF65EBDCB505D6D2A29C01E3369A2F506F03E62036DCA54043B575D30D61BD8C5AEBF0ADB0519DFC522C759E11514AEFC9EE5769110FE668D8BEBD12C63BA8A5AD6E13EF56D2110EBB8EAA152C5DEB7B37FD436941096CF59B6553466B8B18F134C68D1DFC5C61861C060E0CEFE1B4AF82256F054A8FCBBC5CE5083019BDCBC18A95021F05A1860A9503045065C20F82B0307E47C8C10FD33E4E18E18C639631C39A95789F029861972EE31C8B2386318C70C70E1CB1F8397E7ACE10C4606A944B56336C4BC291CB8948036AF50583C5DDF965BEC0E58AA369C2A3528E95BDBEA2F79EEFEC9B0B21F32A0669061697775054B5A093EEB8B72ED02F80A9CB6E3672934001F3B837E9768F04721BB2E36748A0D951A291B7A309F7E7DFDBEC97B2B83C8ABD7861D1F4EAC33D3547B339B0045DAF988106CF2D3E2162EEA2A53864F1B4CE2C571AD30A3B85F25136D37E0E2173BD46E99F700EDB83982357EE7ED8DA07C95A3EA70236F0D7A27386C4B953FC40A8F01342B5149EEEA9E3CCDE2682ECF85BA5F2B8982D4139E0AA9667AB064544DE26D8930707186619FA6627CD52A54F7821097F0A812A040C81D412A1152A1060AC2C886E57C0C03152BF44C1FF0085CB86387E0C639610C5CB8A2C2D61632E2C5870C638631C3972C771CB863189388CBC5CB8CB972E149C65F174E0DA3B6135AC6B7E0820D1743BF6C54EB82C4DBDFA731D89E94BB4C825FB088DD7380D42BCFA5D351D4E282ADA7BB37E8AF513D22ED3B257AB8CADD228377D844E12DCBB7EE79502DD8FDCE726BC2CDF10D8F49C48ABE8EA6AF2A6961D25D772F600FF00ECAC2BDB520343EB23BEAE01765808BA30B34F715D652DC16F89744B78E2E3611796A5CE95E9C62942EEDB22C5B3D727E268A69A9C8416EAFBFEF0121D174B1D16AD9B107D4B47D43C36E2A5E49AE85BBE1A896A4DE01F07CCBC85437E0FD42014468EC7DCB5F13587E0F8835062DB86989F18AC4912086385C159A9580B9C231C60B8E4204A952B0A952B260C0418C830062A9590C2A5601082545089932254A952A57C4C183F4CC1972E18C63F1631C30CDC58A32E2C3963861C38631CB18E58C7E0E1C3970FCAE1B854DC5C07706B11DDC378E8BD40F73C9C045B0B16DA5204272C82E2709BAE2A2DCA56D592E891B814B9959B607EE24AFB460BB2F70447BED5B67E66A0F6FABD109E4D1E1055BDEEFBF72EA5B5D7D54A156285E887BA358EE83EE6EB5EA3C25090FE521354DFF312DA01FD928013D469DF99A942AE0E1D4726493990BE26C6E23C154CA3374BA6BC43DB4B9AE8A401DF29E2D201A406FEE2DE455E7D404567A7EC9BBB1F9606F1F586BC65661B4836B0B485159A4156957C7539C91C686689C4A3CFBF51D64FD80CDFF7487EFF0043EE3B62950B4BE8EE3A1B01479CA5E4C887925B89C2192D92A26A560E70CB0CB2C39218670C26A1A8C704095F10495804A9502180600D4A87C0284A810C260A952A54AC90D7C0842183152BE47E89F13E2E5CB87E2C63872E163125448918C631C31C38631C318C7F418E1C75863F1704EE50C748EF1C4A99DC3B39A72CB7876F6E09C0C9DB510FAC3C07A555DBC3C2A00077607895C35B6BFEA53343BA65182AD91296CD91B3AB5A9A873F53BE4BEC9D109C9292035AA72A69C11A0F670C666AE0FF0011CB3D1B9723CC1A59524EC1D92DB14E9EFE1E62F11DAF13B9A2557B0D9DA69C404797CC01FF00E8526F726873E7F694C9236EFCCA5ECFBC770D5C5ABCDB8886F0DD1EC6D9CA0F426D3D10225920A0F9EA21C7C81E637D9944EE2CFDA10A2AFF00084924A0BCC4FB94D7F30F7FA30C86EABA6D2FA8EC3EC0454C7ABAC0B255CC5A66EDC6980544BF8FDD448980C02544BC18612553856F011D4E63C62CAC10182A1060713067106299445B9122A0420F8A2A540952A543061810843179BFD33F44F8B8639631F93F130C70918B163F031C31C3863F270C631C318FE831CF5F1E232E3296785CD1E5EA36B9C9369BAA8573EE2009B746A3F42A39828ED4889D5443FCA541B7B0FF0088C64F6BE255821E70B799CDA273732C5691E39FE239BFC383F68404653B64DF4A3BE4BCCE6A8BA1C9874A6A097E596F36FC8B1D743838A1BB4F9E74AB32BBE6D97DF414F857FB443B5D85901A9AD1D9BBC4624AEF5CF87A9447C35A2BC9FF0062A2E038A3A8BC63BB43FF00B2E8EA51B55D30DAB579AF3F64FB28347A4D1681A7A7301DBE0227D41368C588AB3816371EF461A68E4D2DAF35E2740CAD735F53AFD0AF3E11855B4021F7B076B61A943B00E310552506A81035C03C6C9AC52B2DF9111F02F68799C1FD5E3729DAA10C7AB0591BB12CC378C30DF20E7B48254A9572A31550DE0C3854108095021836C18290820824AC22B031020408108083254AC8103F4104087C2E5CB83FFA1CB870FCDCBF0631C8E0EA2C387E0E1C318C72FC1CBF272C70E5C5EE5CEE2E2C5960004557AFDCEA725543F89EF62349C7687CCDCB37CE97F51252BC178886D9F3CDC5649BE53066838732975381AAC88A825B9177E2A1525356DDFB850EB356CB7F0CB8BC7CD15BB8407AE3A94BAA748F1F5051CB6EE179E055CDF981AAF989DFDCBC257B5798AD9BA3C4D681346DAE2293F20B43C4100081E7FA9BC7D18DAA69623748EE13DC2B9A19C9F71542FB15B41EB5FBCEAE37281DC6F48D4D50E7FC4B1680857AD237444005DFA9700D9012809C41DA8B52ADD149450D5EA7669DDB3C6F6F5C4E045708D3282322B790D706BCCD16BC75AF3292909BE3D4D0D821E5B9CAFE05F90FF32BA18AECB22C2C0E1023AA597D9B8593FB3D0FA08575E5BFF52AE073F0AB25A60D16B8C5DC7595E2D938E2D1F0B36F85612A241808E010410454D90C66738A18044826D0C249040408606B2A6C4722A54A86424AC9FA443FF33970E18FC58FC6E30B2E32A240C8A6387E0C7E0E1CBF165E18E1FD273C46160C5CACA4274F24F302E22BBAE7D4E495F1C246D51FEC91B4B6DE7FB9446BF7096D217F2D90002BF6645BE1C50B6FEE5E3A337BF71EB1968B6E2F89C085B1C887879659A6BE771D21D81A253051451CC2207368BFE20A95B54BA6729DCA437001AAB734F823159D948A6D5BBF08F47051F309EB05A37FFF005C7CBD39F538C28F12F0C05543AA15DD1E82140400399B77FBD79419B5F08B53AC5F75A8DD0A45BE9657D5C4BA1BD87E6A1C0D8EDE374FBB8CBA26A5B344D875AB4BCDBF52F6CBF9B5A22F27342DB82029504A2EE12F6CD29F27E611A3F83297D081CCAEE5EAE5D51E25EC220E431B80357D7E52BE876A5F2D7F6CB153E728EFF689B8401AABFF009055753685F13B245EBB3A96316A7344EFD935E08B66D3BFCCA85BB7D11EFDC5A8ED9B35146E5F82CEA00E230C5531DF8CDDD2B9AA31A6392B0CA4A8912304225C61C04084ABC0411A43718649AC1379440810922A10498B44E3854A952A5604A952A24A9503E4E2A56087EB983E2E5CB18FC5F8B2F17F049DE1830B0C70E5C3870E18C72C4CB8A8FC1C31C38AC281436B16184B668978850D9D115AD0EC12FA3BBD9268076BA0F30298C34769A500871EFCC47BE26B927079299B668B9F76533AD3BC14341BBE7AA94D3522C691C9C66A0792E5572AA35C08004A8D3A88957D3B520CDA8BF546010FC62A5A5D2DA2F99700B3B7A9DF0651A91ECDCDCF7E76DB34F67FB8AB61B620E2DE2583452F9DFF003FE20B5AEFA34764484DCF50BBBFA21E01A8DD04AD5E001A8563BAFC5CD0A024DE68A3536AFC197B32817F79D337A91B3DC7408423939FF1385C45BB9E4EAE2A04507B788EEE6AEA54B82F4311AAD9ACD31C8AEC94895C86AEBDFDCA5AD39F03CC167730703FEA6C11AC43295142EEE66BA11B9B80DD98D539A9CE208390F30DF76CA727570325E79A7D789635F54CF3105E5FEE242DCA736C006E1A7181DF32C0B8C5394D61C961C4613062A5609838483119254A83E1B9420920B42B80971101020DC22C30194952A5408454A891254A95F2A95FFADCB9631F83863863F2BF831C135812A318C631C31CB1C3863972C70E1F83F0631F50BFB4D3EBC47959E9501136B37D5FDC74693939F6834E9523BF5EE2CA07535F420E8DFF00B1358FF619C04B79F8FB8A4B68E2EE123CA530820F3BBB8E9BADF5EA5B5BA23ED7EA230FA4A95A2C550F31A9F657DCEF860F0F847468FF00BE2238A9AAE6FB944EF095AB1393C40D88A1751000194E2DD4B6B348DFB4BA3DC7FCB50ECD82BF3BA84343BA4BE5CB3B9D1E9BF1A87475C1DAAA0EC28062D2EB5CEE44854A9559DC376DD0F4D5A92C1B16D5D2A9ADE0706BEF0AB70AD53D27E220B34BD2ADEE1386B6DD454376FC4C42855974CAEE4E2E096DC1027B291389335F12D156C789797E3E5514101B4D5EA0D6C6BD1F67DCBF5D6C3B6575842B7FD40097F71F99A92DB57B274AFEEC0FA8EE50B4EA22E509FB71528AFB9FCA14C6C4B19BFE3652A86E06F1AB85B4A728E6AC3795E558751C0408DA6D892410412424AA84087C2B6401F06457C20C1CB1D883E20203E0CAC54ACD62A57FE832E5F83F16318C7E1718B9B97858B14B8B06318C631CB970C70C7E4CAF83863C470C5D401FBA5828976C1C1BAAA36418ABC6EDF52FA627290A9BC37A5AD31182AE95AF512ED577884A4D5F568791F13B864ECA58E6304BB5ADB0D6B6F35A6731C76712A947A41055252D7F536DEAB823515575E96730FF00DCAF2BE6763415F5C415F24BA77364494A0DC11373D98676529D7AD07B856E0CD06B84140849F4DB183A5FD9B88BB627E5D4036FADDA176CA5A7356D2F4FE27558806CDCFF0075E772EA8FB43FF51EEC8DEE8EE21F65BD543539856AC791E7C413D703A7DC3032949B096ED4146178016E777AF737505552A362FA953025FF00267140A0D53DBCCBE34AF88D659F7E62C9DD59E9E21A2CA91A85111D53C45440AAA91CBB1C4E07FF0093418258A2BCF5B81B04E0808F0069B12EE56B53C56AB9B9A451B42E5256B6AAE9354386B76DAFD41D12F6B93C91D912B987DCE3A9D812036366026AC11C748913012BE30892A1B94C1826430181B4311F0A192F2E9E9856729B4E30420825C83E0C494604A80F880C97E752A54AC54A952B15FF008CCB970C631F8318C6318E18C7E1785E0B18B8E1C31C3863863863963970FC92318E3421B53BE2580DC3676C47126AEF120672B73383E38555B29AD94EC435EE25CE90B83AB7D40F65569C965F2E76D1CCF1C82B55F5118A8583A7A87771EDA482A76683B6344782F89B0ACE82FDE1F6DBDB62BF106D134E7B826B7716E32BD8C6D94AB56DE284807AEE7E08E25B19BB3C43D72D27A1CC43AEA0E7EE397468A77D3F6656A381F539611C12E92CAFDC88AF14115C67DD2D0F2CBE351B41FB40502D36AAE507D6C7CEBDC72F4C278AF27B9DBF18B486283A7853BDB1810AC6EFC71C452D75525D799BC4E86E9D4B85AA0A6CE2029C299FB8731BD2DC2EAB5C44703EB5CBF984FC3183C92A88B6A95444E36ECA4BAD0375C11E460843BB4F7FE49A9E70656A15CAD878538659D3AF68A47D5ED7A43B203422DA8803AFDC639B2DC7D20E94A5AD21073703A2A71EE32885B96B23AAA25FEEAB8FB9B9AD79122BBEBB205ABDAD9D2FDE72FD5A76E6379E59544830AC8383060C75160971833AD84F2F8D9AC730A6CC4C31F09303482A6D2D09300D230D20423031715560C7F42A54A9584958AFFCEC70E58C7E0C70E18E18C7E172E0C51470C631CB18FC58E1F9397E2C70E1A828A1DCDAFB16FDCB506D67AECA36C6CAAEF45D7DC5E2F357DA7828A6EFEA71B83A347E7B85176583A1E62205685CA3774A375EE1BE41CDFCCE93DB6AAA5D51E252C2EFB2B997AA57E56C342803475055F2DCE6125DDDBD1948095DA39F50B44D7B3DC37722CF31CF2BF1E2519B0A6169537A3C0771AEFD0FE2819EB32B4C6415B4FED2A07A3C773F3C1C5F701DC0D3C98C4874D07DB08CA908EAAF501E1A7B4ADFDC1C043742CD86B7B41853E5A37FCCE534D9E3F117B75B63CFDC3C206DA3346B223ABD4ED589E9FF00B534A3654587553958D28D7D753F7BBBA7DC2F517CB9D57B95CF71752D4AC1CBE22592E8235A770316D158135118D2F065E9D43266E2BF9AE5988CBADA5F401B43C7985A7B878BA9CFEFA7CA71902D16CDF30CF349CC8928FEC4666C3DEF1006757AA7512DA2DC24678827654DE3DF65BB9E77568186446D7545A547B3738A7BD89D4D0B70D4E8298BC0B63722B0B8D81505E15060D672C5B473C823460B716B0349BE30E3921947187C3163099A2218893304B08C12B0A95122412A54254A952B27C6A566BFF331C386318FC1F8BFA660C6318C70FC1C3872E18E5CB1C386319719F8C48EBF2CB00D875EE07D529A22541B61B9556AE6A1EA10BA077B22E526EBC1EE275F7BFF003971926EEE068EFD37A67277535DFF00F22F45D390E5F534B82381CC426AA8156E11B59C52DB9A9252FF00A820A16DAEE7020AE26DE751EFDC74C3A954B461DFE5FEA551D4C3AB174BA1CC236EDECB83FABA0E3ED832FB77BD8BB8ADCDFAF841B5582DFD229AFEC89A2C468EA315B1CD4B5A5D93A846565AB42F85FAFEE6A7CD43F84454C20FE020968D4A2AD2AB5128D82F9DCFB40711C45E5370A37F30603857BCF513C22BB7E2E39DA282D1F506CD8D0EB52D64EC7FC0268F3592521EA35A3AC34E6D3A94FF0033C138821FBE12DE90D4A3B3740504EEABA51C25A110D3F714B4E661A9517DDEF4CDD17A13C0F3396844C47B41AE84B4A74ABCC6ABD8E2544EB9905E0B887D1C535D5F8201D19F31458745D42177B35BD5C2996A77FD8C1251DABCCB95E4021B212AF160A40B8D7538673C75103EC6A1A6C9E19F5398E3812466A825B28CC22304616C0DE3F495CD25AC293587ACDA148C8CBF0260AE1844A606159C720CBF18E127464AF831892A562A54AC57C6BE152A562B352BFF000318C70C631C386386318E5CBF16318C72C7E2E18C72E18E18E5D7C197388C5D8023E44B20D364F3E08F3882F8AFA80B6D5BBCFD46B72F441037E039FB9F8FFF0051628B9971210101C96010589DC2770BA4C43A88402ADE7E619A9642D455081B3F9638B5B60E208057A5E1215AF23A0970087C7F11AA1B74AF0826EB65947ED2AAD8157247D0AB43A8B8B9A439F48E59C6BD31CE1B34E20205C85E43D1F9A96401796FCCA1F405D691B65A68E63B6343E35CCB802E7F08A8AB93823CEDB205223F9AB9AA0F3B155C5CA529246DEE5ADD792DACA26BE813728660740A2229C7E532F444CA47214D2C730EED2BFF0005957F3A39BD197A8A0BAAB84AAAF4ED05238FB1D411667B2FF7F328D43E8627A2472AF100DD5A8EE1A7533DDCD6942F8F3C312BAD5FB23A077417B9B9B711E4715FB43513B3F1E49ABC85EA00DDF76E17FB946EEF191A60451668D1B8D54F67B420E135817FB41DEA23F98246DA77F830CC99AE892F2A3AF14E771AA375F73A78E5F07B25657A7620FD45D13343DA5CA1CA0A89441BAAF706E9F53AE53D44E444C3646026F2983C2CCA656E30D9824D4CCD2560DB0B687C254C21CA546182D09A2731F8B0C0952B0C638A952A54A952BE07E9566BE159AFD170C70C639631F831C5618E430705C318C631C3963F1631C3970E1F839E1A4E0F334FF79F07A8B55F007A9A533526F15EEEF98A71EA19DB4205950531B6F93F28C0ADE77D27B2315CB479422745B2A72C53A0FEA3B28B0BECFA94C6B3EC7EE11D6DD437F94F715B7B3C41E6B5CF572D2C8D83551C3AE0D7FF00CDCB443AEA3BF5006DE892AA768E08A69C2FA08C854FFCD2805A012C8DE1542F876C50B7D6D003FE2E33C909D3EA399BE25B8459B6E714E40F31D0758756857F11E55887F7917AA1749E63032BC383632D6943F6788ABFA487CBA86D2ED1D2A06A3DC25F9B858ACDA59A8E4F8D0A5FA82F4004E588832EB7AE6702428F094286EA76FB80E3C9AAF8D9EEE6A5306FBB8582703C194524F03EE89DABAED93CC67EA1B1FD4B30638FFB82674040FC40F56F641437FB0F98A4721F49B142DB771C742893A3DC55AA015F4DD4B8D4EB3DA4162F55F0C960BB3C72E83FD45BD1921BFA4742239196CB5B84941B5FB61775639A97D8F352BD27895C51C1FF00FCCBF5ED69AC483E26A5CCAE8D6A05DD20FE90B551A0A95370A914B73917F92542EABE57E65E06B5CC19296AC96D63ED094179C4AADEF30B62DF13C3FBD35EB335CFE63188ACFA6F0577280584C1CF5E26BF369F800E260F94F26A105227C0A8409583856A317C5224A958AF857C0CD4A952A54A952A57CEBF518C70C631CB1F8B970CA8462E18C63870C7E2E58E18E1CB18E5CB1E30E4D05ACA1A80D9E09EDD398AABB4D658EB4AB6160194F6F844D5E3DB552A5DF5D0DC65D0603A0F536A963741CB8866AF63CB16952AF623573CA5ABA4E8A829D15D3E65000DAB9B86A87D13608B4D63DF63A9736C6E159B5CB5B8872DCA37AE630274D6C865A1D0E2E1B6CD89E0A205E391F1D94FE08F5D51C294291DBB27979978E2A46C7F483FFB8C156045E58BC705429529D7A13A6D4D9CEAE688F51EE8FF00A8AADAC472A9704B1ADFE7D40396BD362F9F52C0DA4BE578AFEE51C3A41128765377A9DA87F04BFC80D17E097DB63C36A881903E3F24672D707E08C977D2FAF5F70AFCB700C760101EBE43C33B6F73B27E66B107F685D1AAEFB1892D83356E1EC78945C22FF097DD8D22D78DE793CCAC01F91F99610B09B43A94529A3B8375050A7AF1115AAF0A827EC4D94A0F502551A7B56CD4DE050A7564686AF95E47F31682A7A692B481527DD789034656793F4C7EA27F88818BDF6F738A84B6B7908DEF9E00EF7EA1EE768B6750B65556EC65492D20A521F152B6AC0386FD055423B2BDC0D49C21633BA470152D46AE47896166BB0B8207D80D4B14DF23A45DD02BF245687B0D93B29CDDBFF12F90BDBA0FACB278461254AC2E1D32180F84126902380C8A958638E117C57587262BE15F0AC562A54A952A57FE370C70C631CBF072E18B170E1631238631C318C70C70E18C70E1CBF070E59486EAFA43B6D5711FA4F90891ABE3E23AF6AB888DFBE6FCCD890BBEEC1B88737723A8ABB0F07E21CEBD34F2B2C6607D4404550DDCFDCEC9BE57B95A65745CD43DCF4CBBE2F64E65C4AA943E233D26AEBFC12A47C44EC7674461A8B74EE19603DCB83A798F5F47472C0B86A2BE5802C00FCE895D19D4AFB1E674CEB0D079975D25707DFF0012A44E9636977CCD1E3CE1A94C0B78E8F10AA7A86FF12B0AACA7CBCB09FD00385BBFB834839A007B9552E5DB4E25F6B455EE2D1D247558BDADA5ED151B5F11DC46980E3B6BA8B6D840E4216252BB195DA56EB37DD12E45694A3EE4DA81C39D49234507035A9685768F37D4D772A1530616D9B0BA963FA91DC31381A575EA5056A69C0D5D46265615DF704FAD88DD56FF000605BBFBA34105C1DEBB2542F0D0D53D7F1108D911EBC4061A6ABC4F145A44B236D3E4D2331DF068A5FB91BD5F948B700EAF6FA9759764DF33740D2DA6CEEE6C37224169046F8A73528AB6815A15E65468357EFDCB3ED6821959770854A05DAABC5C4E49D13A47CAA52041E21A604E555C5137DFA3EE73CB472E494258F984D8DF9B8BF69C71143DC5F85A2BB8C204F469FB670D8D2BEC2367933F8443D5830CA892A25612265A95093030204292B0AC1C495186E55461891C5E197FA950254AC54AF938BFD3BF8B863870FC18FC18FC1F831512751DE18C63970E1CB87E2E58C6386396329E81B42160B7B7F8802694E5F30C42697E251B276342170394A547DCD510EFA813769DD136106E9B854AEC9D7D7D43C0FA5DCD2185D050A25D3C5AA76C11800EA5C7EE173CCA8FCC72FB9F43C1FE6591AEB831BE69BD01FA8773C0079B772E7D0B6FC4B820A6DFC2234FD93A8BDB98EE170C3C97FC8EA029F6E45096A4852D0E96BEA04FBD4BB2DF3141E5B65397F79ABBA5A5EF78BE28A74F2AE6A6886E1E557CC5676E25D7278F7376A0895BAEFF00886B7010D507FF00E66BD1DEEA56DAFDE5AA92579BCC044850A61A431E46BF89C853CE6BD4F1F50745F1F7140416781E827599716FD4AFB35F20FB2041DD40DDBCCBA96405BD23937A3D12F97974420A3B11D42B456A9D9EA2C30BC9D180087328DA8E15CC48294D1E66B180EFD3F707E923D98F0A82FF00CE85D6BD9D545B028D665CE9CB889B347886D502EF5E65902BC0DCD59E2BCCD896C4081A3C9DA2D71D07A432BC9CDBAF529315D3E09B05AF33A86C366A2A2FBA73468C7BB54153B3724BB2FAAA15CDFC2C65F8F3102FEA1366F10420216E6D141CA80FB82BCB1044BE8CFD9EA06AB5CE5328B05F3E62870618A9A44C3C4B4A952B2209A952A084A8454AC2A2470A8F187012A5463FA75F031583E7783F41F9B872E18E58E58C63F0303AC2E318C798C631C3870C7E4FCDC394DC70C0207D98FA2F7F3F9474B29E24444B676C4DFBDE90423B1E203F0F6F994D6B707D6BA9585E1BAFF12B985D8EA737F5411603686AFD4D663C87AF718D5FF84FBB48DDC648F572CAAF9350B3B0F0E59A31BBAA6E9860DEA5F84A51C620ED9D934E38222968FED4DD54EBB6A5DA2AE79D4E6B8965748A91CB01B01A4975EE50E56D2EE5050515EB5FF207DD755D0E83F68C4026E91A1FEE585EDDAEE36C36EFC3B1CC4B5BAD79080F0650B7A4B94059B20943F79CE43ABABE65500695407B7B8C1001FC0CAAA74200179DFD01F2C616B50EA1FDCAE70DD9C3DB1B94513621C7D4E472962D63B9821DD730C1D56B085102AB8398B585515F9FE23DB42BB50D5CBB685DC4684E5E19DAD8389C20756BF137F385B51192D4D9E7CA76A1AAFE419A720F1E65FD5BEE129B20D774CE346CA782A6C7D1B3CC09708083097622A6DB8A38FAADE1ABB94CA222BF2956251BCF96E94C117B86F482B5B80D5B52DAB42969645AED1B2357DF16B9DC6E69F51BB739D61B38E875393AEEF4CB08F083B2375A7D9508A388BA597F72814147825670975436DDA254DA0089E2703116A35B932544C2A544952A10410454A8996A180C8D44952A3930238A958AC566BFF0AFC4F95FE8318C70C6318E5CB18C7E4C4C126EA24612A318E1F831CD46318FC18E1E7E2C4A26BCBCCBB1B74081A60ADA41D7B336EB3F02339B9FFA1298B5EAE71C13C300974B6B573432C37E7CEA09E86CEFF5035E8FDC4B805555DC02E1AEDE61EB7685EBBF247D868B8D6C3A284D902D06C8B46D2CDE918AB7695CBE208BFE6578959BBF3E7EA56DB1AF361CA05F52FF0093BA8E3EA501690D2512975A63456D7070AC9BF71A982D5DB4FF00B2A95B3B3C9CC61F726A138A66F15E0D06BB7A8C32E855F511D0F4810009E18FB878DCB5D75AD557354B887304B5BABEFF00995C1E82E87E27D26198F005CF04A9D7F63E09CC097C8A173403B8AD7E252540A1AD1EE5BA94EC2FB66CF68F26180F28D7DD14000E6211D1CA77EA1DA9BA9C152DB6E02AA126FBE51C310A2E9B8D8A6341179AD411391D6B5035BD04E56C2A71A9F72BAFB95FA9B0D903EDDC2B5162ABD71394B6D9214743FB432F658118C6227EE2CD79BA4A8E845F6525E3C346F981ED53921009F96334901E7385726D69FE4BCA1B2BCB8AB3D450B8D40B4E6BA87699DF309AA7B10C11B624007A3B63950ECC1002B35F12974A1F6BF449CC95A784171862A24AC58AC02101F1A8CADE4C317E0C489186309084892A57C2A54A952A54A95FA4E2BE378BF81F270C72C639639715F165CB8B0631958A71BF070C6562A3460FC58C70E5C9D277D1EA300D8D24D6E9E2E68ABDB01BDC9CC54D749CCC5D3687F9F11C7E7AE012B9B706E9F02102578509CFB95114007F660A059A477136D45DDFB9CE406AD06DBB1C0C2D92D6BA0E2D175C04188DA40201343AF2B38904E85E5EF534357D364A9EB55E901305BB36B3F3096274A87E62D2F5480904157C5DA89782037AF72FB6DD8A8CB80DE12A8E078964A6E8A1B764249350FD88B82F315BF30CB811A253E26A855D6A799A3D71CAF48BEDB9AE0E57FC443A0F61C3551FDBA0F27DCD08557F89B2444B87D0FB57C8FF00A8F4B728C9AE5854B8F6735133EF98F4C60093C0A1C4D4B60E486495229B03D7DC4C1C4D76F31C08F4A3A7B97C1AFD7F98E4FA854B945B79314605D7AF9B8EE06DD3C92D0FB2DE25D9A4FAAD9B1805785D239F4727A6011AE859EAD53FC439B0A08308A783A251A2363E63B0546C906FA0BDC24E18890A8EA2C71512185EE8E62C533AE29753BD1A215E67EAB01BE7A4A2B97AF89EE4EE2FA8EB88480EDDCA974E755E20441DCD037D22A04BA3DA1AB46B5031E95E50F042B4143FA0105070203E6E2BDE7312564A892A54307C98C1C187E0C58B18C184C5461254A95854A952A57FE0ACB9BF81F270E58C631CD448913291C2A54A8918AC84A890C6318C7E1593CFC4E5C3878C386DF8BB32903A55F040B9BFB841B68BB859D7A15182A1AE0E6A2A6EEF55E223B57873C93ADA395296EB68CEDDF212C4141C5427104015FD4D0A8B65BA88F0F6C0966DDBC91D351FE752FE9DCEA3A283CAA9286DAB6FAA97FDB267F0139884BF3FB8D36882DADF995316CB0DD8AD685FB87B9F854AB97C60E7A482D6840DB63CA814F9732D11E498F5121470D438C1683D4691D0DE840985ED572BD43B435A57B8298BA53C89B97944EEBC43CFB5DF31C697039F551132F8D63D93C0678235D1A8E939E2245275CEF3950ADB57EF95D2415E1698E6F0AFCC32F6F746F7FF00F3C40343DEA287AFE8D05BFEE79755B5FD4A0F9357AC7EFF00671E7C434CFAD147073FC91CA416ADE3F728D07EE0CE5D17FB9359AABF640656FDCF70E6DB20C74E05E3511E33DF70076D0D55F24D18EB3AD4745A1F2914BAFD943D4DEA30DEA2BC165E4CB985A951B4145A1B40ED07EAE5833908AAF72D682CBD90E3E3B3907B8C7F754B0CB400BA7CCDA177DB02E0A25EBEEE6CBAF4798A8129176ED1385D71394ABE04266884A89131518AC560F8385C0E097162E172E3F0584959A952A57C6FF5AA57E99F370FC18C7E2E1899A8FC2F151C55870706261CAB0E4E1F831F91BDD1C7963055E216E1F08560E69A830E85EFA250A9FB21D4762C56CB071DA00D5CD7626ECF448799357DB2C39BA7CA5D01A747F896DF9DC0E3EA3EC14698699746978252AFB1FBB2A60EDD17D7D416AC7CF7BEE0CECAEE3FA08C43B02B5019DB9AD75D13779B96DF7EA11B7B8CAD0F77B969047A0F10ACE1C95AF334D4F4752A46CC08ECEA2EDA7DCEDFF59D88A07263DAEBEE5DE180FF00C9CF28609AD352962390DB1DC4BEBF8817513D9FDA3FD620EA1A6003FBA5FE0503A57CCA5C7BB9E1A8C429D0EDB94821E4D3D42105D72289AA013F2BC4B775BBE86DFEA05BD06BDC4E9900EF9F32919FB34657E955B577D7F30F9C34B8ED742C845815F743A6251090D34BE185D56D3F69772E9E0967DAFF00F0633E9B72F5EE301AF64AD4851FB9A94E214D28C684FDF2EE2C65C5C5E0114F23997F01BF980DE23A1C0E8F825CE8E038950529E224D64295C9EFA8C59D8D6D20B5AA6965D6E57D9954B5D6E6ADCD9F88EAD633916E9EA5FE34BB3E12752A41212B0FC0F82CEE28C5C208B8B165CB97165CB8B17352A566A57FF90FC58FC1C32E3F063F3AC9639CE0E18CB9718B16397E0FC9C68AFF00BA586CD4345665707A4382537E862A3D1D8363C47EB7B2B22B88BA86F70BE5A5E21B10D3CD712D53BF2F94D607756D4448D079082EF1D638830167D97F12F9046D394FB81842ED4D0CB912948E52FDCBDF0471A86E8FE09E7F07DD402748EE9EF028D208E4A5B1C37E7D4D36EBA8E7576A8EE50B6C139FA3152073D6BB89D662D1DBEE582677F976916C40F104271C1DC2BAB0A2DE5134ED64B2F3E0C0FCAED2B698E491551513F4DD27B94774DE1FBCA09017AE60C80D85A0B951B1A0E137D9D2CFE4C2DCA7DD67889E53A2E46E3862CB0E8F11685E35C4D40FAF84F2C34056E77A39FF0030A3E25719668F877EA305F90BE52E5684470854B2D05F4771F56E525FF17BEC3E496B3A3F280AE82B986DA9BDBFD4A6176581DC74DA843C951AEC536110A556DEDE7EA3B26DFDDD47F6BEAB13142776CB8C7E371962D1EE05B6FDD4496543A9488FB92815BDC1A5C3E26D367D443675374CA4008D9A8E2A5DA42B1A15A71043376676415CBD442223BDDEAFC91C12B60AF11B64AEC6BB43230609A3057137732E39B8303F0B972FE02CB8421717162CB972E5CBF9BFA81FF81C3F13F41F8318C63F04952A27C551C3F11062C63F070B1C39EB2FC18C70C16F0453D24ADD17084FDA72883A68BBC058F83B86390DC1A9D7695771EA5036DCFDEC3B852A941BFEE5C176BFE88F5D7B77EE0A42BFC4A8036CE2E529A2BF74B58BA1F2FC47B32BB5EE0BE70E87DCA4F176AED866D2B6B0896A2FBAE70F11FA94D17D84624DE2F04BF6E612FCE94481D07194040AD876A2D669DA346E2A20E48B236CD3935F856AEA3FC131FF00C8FC28BDBCF897A01A87A9B387A2F513D96E4014AE9CC616EB612B9768F3080A3B29BC20D2537109E8DF4C5E475E0FB8F36AE393C44B0FD30036FA377076109BBD4400ECB75C92CB41CBA876E96AF5AC4C23A5E3FDE5DA6D7F0CDA9E1414CB97B4BF288B41373C6DAE57CCB9C1A7808F7E07F48118BE5BD435B4F98F459DDC6349E64071D367B95711FB02342B82843E52B5BF1092D5D4F3CCB9978D3ED2B68EF5AA8DC40BBB7CC4687B4597170B8B82692C895DAA13E0FE64B40EE0339415D336367D25E5FA77E043A4FDE5EEE1C0B51C1DDE6DD4746E5976724E4B97C3E65099E63B30B1295D3909F9F774EFB83644ACE952AE3F747E22E52FB7B845E47CF5395EBC900BCCF097137442783EA0EB7700234FDCAC5E4B9714638B972E5CB97163178B97FA15F0A952A564FF00C6E0C9F270FC5C57E8B8AC387158BCB5C530CA8E788C6396318E18C48E7DC3C3963EB51F7CC50AA7AF5000B214E90B6A782F716EEBC171BF02A0AD3C0222051C203411D0F31563B1757FE8805056DA5A3D4A35957B4D6DE41E04761B6A3729213AF086BCAF3D4B268356EFEE20B0A54DC695BBE0626DF4DC1EE3ADD0EF62F7103BCAEBF295E62D1A366D39EDACFC25F296AD91ABA82B5F684468F1584E14F07946B3358E7F6941E806FD38DA8B417B222E135BE3B816FD03B1E446BC22AE4BC4DB58D0397B83E89BAFDF01393EE1EC60AF5B95935CDC2BCE19D801C100B95907B9F4C244B31F2EDB014AADBAA5A74BEBA771088711294DEC2751C0F6B64F3F9BBE608C035DB60C788468FF009942C16D75030AFD47E8015E58A240395DB137486871716CA6E162DE97A1B84A74C774AFED369C3E237270B948D082FB8A0F607515A4AF3730BEEEDD4753687880DEC2D8F104695E046C5A7BCB870F12E2593B437C4F36CF64A3C42061AA553C10B107C54BD89C7128A035CBFEF0206C2EF84BA802D8CB9BFC7681F5B171F53D4E89A4423ECF611FC677821C6702C0D29E2FBEA6DEF587894A16E8A434D21349B3E615A59840F30134D465B8A35EDC32D7F300D0FC90B162FCAF0B2E39597FAAE6BF52FE6B2E5E0FD37F41F931F9A40F85C60B83065470E1CA472FC98C145B7A3CBC4A7EF514E59468576ADB5E26EEF4350DE8D9A3B8D1E3BA8DF23EDC4BFF00CEE1587CF0315E9E43BA811CFED09BDAF617A9734DA56E93A47F17CC1A340BD8CDBABE55C42ECF3156D3D9EFD442E19B0207D68FCBF316893980751D2E1AF2C471C5F305E8BEFA86CBABC63A075CCD82966E89E27DAEB889AAB5BADFB41A06F7C9384BF49A5257823C4882CF53A0F9595EE134DD4F7E48DD83805571B3F720E5B1BAF3281A683B22977D8AAE7A793F1FEF348B280BC56A0754F475F705FB344A768729D9F50556A3A949E0DCAD5873206E28DA402BDB84E6243C059C5FB9441D05B9434A8D8281D1D5D70C48DB0F53512AEB5B37FCCB942BEA7402C2ED763604ED2F0F70D2707820514DA9B6EE58EC5FF88D1B54F32C1E2D6DD3E270949C4AB6DFD4AF78EAAA8D5764ACE87F6657BD7FCE01E1256F38DBE13883D8C0435772A0CAA037DF6D8E0837186C8ACE98E20CBBF9F32CB4126FD0798D72D3395EB77365A7331A2A68237810F37CCE8C2EEE1A875A6DA1FEE09B9512143EDBFD255C3EA2E3AF00267481E04B010F2D4DA953B7625DA3EA053C1028F13721F0842577CB359D8BB8D06A5A48C33A7D25151F8E5CBF85E565FF00F855FA443F41CBF372FE83972C4CA8B17058C70E1CB96318C633B087DB01C123C75F7B825DBC17C12C6A2A5C40BF0CA485E8CDA90D06AA0AA8AD5D1734BF04DC66D9CDCF52D90568835C22FDD5DC61272B7CC19850D11DB0E2BAFE21D76509D145406AECEB91440BE13BFB86F9371F17573FE26B056AB51B31ECB7FDCA10D414F118B1E7B8C837C728117AFBD1D11FA4234DCD7AAA85AA8E81C15279879D10AB5B01154896B4BF07EA54C731ED26BDF045BE57F6840A8A8DBE62CF521D4D9BD4D69DF3185BDE9F2615CAD84F5E48BBCEED43DB0A033486A6F8D06DEAE525C85C802D2FE6A0D2F5ABBBFE65DF6B2D6F12CE6DFDAA25B00E0E584D9D7478895D4F0744D96AD66E5A31D906127C5BA96A3C5FBFA8C418E9AF310D74D46E377B2A5A772D6509A3C4ECD1CB9847E8A26B26CB54294C7C02CBD5CF1E5A9D323C73F72C0B6D72C536C3507955102E9BB4E63BABF03D31AC543FDC97CA87BF32C141C98363CC3BAFE61D6CD7351EB3545390F72F10A962752EE0D41395210BAEB0E1F8F58A974C3E0A0972287984B479A3A67339C18065F943A1743802695E05B989A1AB76F1040826CD93BA97B9BA47245A734878CD9EC84226A53B111D21C0E95EE02551BD30DC0F1752D06ADDF8958270944F30056D523D3B1DCDDCC0BB6A7E58C1747CBB653C3E37D4A535BBB713603E514CB972FF00FCABF99FAAFC18E5FD070B172C632E28C638631F8B96318C650B5E969CA3F825A7F609FB53E2828AD7980555EC8B58B2E8EFC4B9FDC389A6BEBE11207ADBB71A85A0D3A39B26F00A5E8A089D0ABEBB654AA16A39F5040341A08FA0D1FE48611ECAE57CC044A0EE5E16BB7FBCB452BC9F52A5A2EEA08E1C92F3134703A9683606AEFB836DA9A70F4970420837FF0091683F71CE0E4D389DDA39D114E0776DD11DDABA85A00AEDAD0EBF797600BFFF001EA5489EEBC44F758E6E6909D7D4B08E69FBA7F895D32DE8E135E1B8AB7FB25CF503FA245924DA764D35F738A2029E7C4A1E0D2AD9EE6F2505E38DCA9206FA0A8D1508AF5F70061BD7B32AC7B870FDC28BDAB9F1EE05E7683A974B3651CA13BADE28EE43AA90F97FE079879ECD544A272579880095CB70A5BBC0FB90CB0F9E0B2B180E1FF31173A09C230B4EDFA8F85EA1BA3DCA740B47B96662EC50FC42CC4D95A8E2769781E278388D5057C4B2ABABEE2587946B50BA57243DCA040E4756457555D9129DB0FF004C632F403A2593AB87CCD1A53D77819CC751E63BCDC2CCBCAD46AD6E18B5BA07772800EEFD23A1C9DDF109C610E32CF2F710A6BDAE5ADA4E59B2E06B7F78A89B287EC78D90E0545456BAD3B114237F2E1122A1E818694F5E28B2C23DA840690B48073358DD82D6529C4453B73EA6FCDE5A9C6BC8EE325D5AEA5A82D3BA788DE68DF84ED2D283A9EDE1842C6C970F938315FF00E0DC597FF818C70FC2A260C27E832A24A952B2918705A448C6396386396384134A08EA0B6B69C433A2EA33D928AB49FCC78FC3F314B1B5C5EBDC791ADA8738632D1FDE39DC3C2E5129FD2961A11DBFE1347C5DBEF054CD8BF44B4FA07A9708D077FE259AD5AF29C86341EA34A9D05F15118DA95F5E61A0558B07053D18B5394B80A704593DA8E3EF0765E4DC434A76D87FE45BAB955E206F1BB72FCC072BCDF2CB66E0ED47052C6963756453FBA7A93F7DDF7375E5D7CB55002258BABEE050D946996A2AE727E60BAF223657A87EF05FF112EAB72EFC202C6FB68F8409C25ADE65EC20D12FA72F870C0D40824783BADC3758F9AEA0DA0F1DA78EA877751AFCA6D25A8FFB45035CB891C857B6068672753D16DC935B6D036CAC90F6B2DC4AFE52F3A95DA2724452343D4607AE6EEA3A20952EEEC9C280F10F70DECB9D4131D3CFCC2C03545C296C250AA2E1BE25BD91D5E988562CFE11D56A710A05FF00099A3A58BB8D146EA4212BC6E0E0BE611376E509CA0E194B02BE0B12C80146A1B445E7CADC66832296B0A37BA65A28FA97E602B0B6EE909746C8D42A1695517FA8591AA597DC26B0A798D9BA7333C9AC9FE66A5C90D9718AFBC2A0D090EEAA4508F51D03C8625473B89D7435F50B992B9A8E95C3DA164D787DC2BF4770D74EF1E255F5A3FA959A3480F2AFB1106AF89EE0E6A561257FF889F2A95FA4C63863F17291224AFD0A95863948C6D1B758688C7292AE78901D466B71A45D5CDAD7FD6118BCD0F6CB685B15712FA0DF9EE5BD93474DEB966EF67B7C18C3620D4148DDADCABA6FE633B9C296D0238B38A85CAE05DA9B22C241BD77286F6A68F1EE0E5156C9A96F8ED0017B1DFB9C683A53D43F202961D6A5637417D4EB01BFE886BB82C7CC28A3B6DA2503B799600BDF31F3CDBA2E6E6397255954AC3BD69F0415E70E27A21AD00E5DC9BFA57E75E2069B4524E019B2FA394E813E9EA5E779475CC0B8E42A6E1BD85DF6944028DCC1396C16B8232A2345AD4528D0366A0BF6D076CD0EFE5EA2693CED088C35E84FB3D2D4D09BFCE4E6E7BA3708EB9EBF8B8D3EE14546AB4FB88633D86C842966C2E2D0839F128425A7F306167009725A3A42D20AB835514520D85FA2398E566FAEF09FE61B20141E50B5380FB96C0D297B968B5AB7D113C03D42327B7AA2723AF09744C3395B543AA6DBF39D0934A47B895830C0E182BB8D02E600B123691F681783A8202FB4518FC6867AAF1EE005517A496D8B6D08E58D4E1880ADA0910AF3AE20371BB60B692D6B6466ABDB45B1F3A14AFEA2877D01536820F0B942803F060562DDA3F26BC41C3C753592C35500BCD3A86C3B52E53D9EA57E08BC321F65ED2AD17749A0D392538AEA8B4F1CCDC42B74C38A958A952A54A952BF42BFF2BFA35F23E4C70E18E5FF00C4FC2A544820DC18A95B88DC1B8C3D4D984B81A430510D57373578AA1E50CD135432B7E5083B51DC00D051E256A3870E1269485C27D4757863E95E52E8044EE292B0DB72AD0DBFC220348D34B8910B76D40597A78409F916FB80504A43DCD92E5DC74D46DD5A3D41D734BE67A03512D68E54F7290C77A8CBDCE709EE5093682D3630162775F102C1D625081B1BFA884D967CA1542F94F1507A78168B6B2B3E88CFB8D7F8B8C57561826D71D00C5DF52A478E5EDDB55D2763ED7DC56815DD54A0152E2F2B4DDEAF68691CD798A87AD1EAE0739F6308F02DBEE6DF147B6F78A9E71B21F9945AABDCE6B85E253017B4A61B59A8800E953C91F2C41EC2831E44C4EE698A45B3C0F5008AA16C0DD58B61A85EF7B886B4A2FC23965A389D3D688AC31C3095534D87054EF2B89CE56B2B92F79031DD4A35EA13179E2E307AA5AFCCE425C72F71A3DF54FD4021A0BE8E26D901D4E2E375BDF12A09B51AB882CE279190485653D5EA6E36ACEE02DBF32B069487652C77915455C6A7298564B55BECF887435CC5B5798F01D4ECC728B6300935369BA9ED065EAF418E3D30CB3A3F283C8A35073E9F4844B5126CE6372CF58A95FF00E03F0AFD13E067FFDA000C03010002000300000010FDA84CE92D475B2F88F7078C6EAA9E144C17C914AC2BD4D23EF70C9A0C2B7B3AFCFEB29E5F1E5DAB7B01E532128436D9491A28F46E8D9B9DE08BF7FCA638866D12C388133972AFB1DB0B28F9CCA4EB2529243DC98FCD87E6BECFBC2A3C7C6C6F8EB764755285D09686F0E396F2D63DBB434F74113B360127FCD5AFC77247E1FA252385E375A42D76B3639BCBCF06ABB85129C40653ACFC2710CE3294A679741EAB6EC442C8B849B03AA009DB6E4F592D8B7DFA4FFCAA3D6D26A937378CE9ECBDDA3A1200DDFAE4D8EB2DA6CF5A3261FAF2AC9027A04FBD4A454D6F144D5D6EDD0DD9D3B211AA34B0EE78A84E192FABE31BCEC7E969EA6D1603FE4815BAECABFD83E96F02AB62BEDEE862267DFB6FBCE12362E89F21118FF2E469E5CAF9DC633338CC392D090FC2797FCA1DA363A7EEDFD2A0214F2A969A80E354565BED080D4CE57C100BDC75CB7A8662738C2B82946384198B8A8ED58CCB4915300BD2DB0084FF00FE1B36B37FF6FBADE1001CDE3E1B2AF3FCF4EA75938B90AAC3C629889850EC96D467CC7D2C198AC60DC0B9BACE6150DEB706424A4F2ABB46BC724EC10254E60AB01DA23F4A8611F0C21169C97E5FCA5A427F0FCCDF5D8D4086EA7EF365B35DF27BECE070EA9A37D215C1D38E16D512553261AD2595EBC51880EFF36EDF78329605B214DB2B1C9E92EEC3B1DC06AD69F67406C42CECF313B9C50550D267A6ACD2FC7752C90023289174841E8531599AC9C32180EDEFCC27BE929EDFADEA250500C2E2C0950A892A2E3CCF79C66882A1BCFE9193275B4757AAAB345475C4732998E620F60DEEA00407DE893624A4FADE093647C68C294A62C2F5DE84C37181FF002C0F6AD8611CC8D9B89EE1632C801B1D7D45F1281EDBA9EA38DE7CEF7FE6ABE4430F16BF25F86EED703FF8AEE92E85F83DA7E7EA9543EED2FB837F71098F1EAE67A5CBDC5A98A6EC7B5EFEABA4BA242C39082954AC797B5ADB32FB86D3C9175C7ED292DED5192417D51B63A09F5F125BBCE83EF13765FA03CD7FE81D7BF1905F56FE450447E18267F47A4E1E0057B94C1893CAFDAA7843BA27C453D472D88A1C26C2608CFF005F837080682AAF47A6159B32574AA84124FF00B350CB3A3EBDFB6FB9778DB7636C3518DCBEFBACE27C692709E222A899BBC7A546D1DDA12A96B9AD84707E4601EC47399E67500C923F5E2B8ED6BD9E8B1703454C09BC2CCE0879B60466703742F60777B3B185C397EC0F44D54D21D38C88A61192F0E0423FBBDBEC4EB663B7FBB9EA7C7E2CECB04DF82D8BDAC1B0887AC4FABAD157759627CDD9A4515E8030002DF59951036AF97D6E96E5DA270939AB13D3E55524B18D3B0834C0488C1A8051839DC1EE9FB37D1233F36720A41B6014277EE00991A4A187B6FF00B9EA3EEDAECC278EB446885D9826D7678B21959E5CDEBADA8FBCD4B802E04809CAD17EA0D34A4C741DA88912BF1ADBFB1CED37CD9A17A792D38D164837D254BA059DEC3293AF3C93ACBF23A4C737AB810D96C36FCB6514BDB7DF29EA38EF48AC5BF66BFE2E2331BA2B9B7F34407408DF9954204F0A91C5E776F33721CD4D5DB8DB77DF9D6BE002DCCA83D9DC0CEA90E452C9AA7D294C30F1D4196E0BD2D1A13D82927FE17E964D3CB33640AA22DB4D45C99FB7DFA8EA38657E896127B27984D1D5B4218CB0AE2B69E3514C7A1DF15079E16D37369C2EDD4FCB9CF0EDEE704CD26BB7060484CF8A923AD3BDE20146507237406AE4FB21FA6A87C2853DAC5EDC3054C713F498DDB593EB44E7FF00B7DFA8EA789536512C1BD5CF264AE99C92A38C1AE9530DD7EF4B97E6F33FFE8ED6785F1C6CCC22A56415D1B90325F0D1C2642F023AAFD3D1AB7F37E2F8F7592081DF771993275A324CD6E30BC91B25F88985D3505500133E9EEDBFDFA8E24C95B7E54D1F0280184EEA85D04F9E2E41CC6219529D7E5D1B46CED18692FBA982D1674CFD32EDA4BC605C1F11C40BDDF4B85D4B41AD08895C7013B5E5990635FD07AD0CFE5B4249BD37BC0E8FF71E69664996A596FEB7DFA8E24C6D4D6CC36BD06F307B7FEF3C9613802CE00E26DFD9FADF05081A9C900BA6275893AA06B95AFDE0E7D71A28821491EB8DF6605CDCCFA91303FCB36D0F7799842B906895F19E8EA477CFDF0E843D97C946C1B0C795ACBFFF00A8E27CFA6223DF13DCFC8E71FBEED147479EC5424ED97285D6EEBD87C6CB9384B6A5DBADAA21FD7C088E3EDBBD075E1D1868152A4E9635933EC2593C1556E09692F3623D09995E64C6B3084081F018169788045E4EDDBDE4B7FF00B8EA7BFE42319D5A07904D9E582599F747E3D6E3FF004A66DB84065EA37B7FFCE0BA60923E79CE994E9136512F2333CD2B8D65220E37F2B7719124E7A2C47EF2CE74F116B8214F1A62A4ABD7CE23AFABDD22182C752BC85DFDF7FF00B8C07BFD63AE8D3EDDA104470F8459C788A5D161164D5DCC4C715900C44F3B74648B70C090914C5F5212D9965D322B9947049302BD57DA5951E94CA11BE775AA1E2C392E68621D72F0C00975DB87BFD6761FBE2E7CBE6F6FB6FF00B880634153B9427EF046969B88D050B795AB939E2F0674F8993AE4292DF226C875FDD415224DA89517FF0071BC73EF2C4113706E61E90FC99FE5AADB1675BA06BA723A36DCE5B566AC2B93FD4C54FBEA1EBE7FF5B3FDF64DFF00F7FF00B8806341D3E80E50273D46964A41A3D3DCAB921C3F2AC145CFF3B472B771C4CFCB9998DBDB099563BB83DA741A3BE03B312B7A7FB58A1D128FA896CE0860A22B05BDF5C861AE2BCD8D3E4613E36DE1ED22496C3EFDDE4F7FBFFF00B8806341CBAD89C003D0319A9BC54DEB1DCB142797B764D9339BCCD7D32F83798775C8A2CC3F9E087AAD7090B36780337DE547072EBB4B67550A4452A78E9D0B2CF2FC4A51172A859FFC49CA1BAD38891403252B7FD25BFDBFDFB84263C1CBA70C0011CDAEA3E2A7E8F50DE30AD5448DEBAF7167F1AD851983C130A2E24769D2BF8C30C074EB43B5B637B55D145D67E086A6015E516DC988C940C5FEDBCB794F3D6FA57F60329B2284C90456E92A24F61BFF00BFDFBCC06BE1D28F29DA4DE4CC2DA8BDCCF29CB7C84FDC7CFF0039FBE29C5B41CDA60DD4FF0075522508431D7A9BF166974B77721D1D39E2445FAAF7FF00267DBD4E970AB67F4D355712F07B3394FC2EDED9265249DE914A76BC8E9BEFBFFF00B8806341CABE5000889A23FCA238D4E11F499E570358E780D79D3D1734D1AB1D8ED816E42709757A73F0274B03E23AF814A52FFC66B8335181D40B42E6007D7B1C21D4D04527900D0ACD7971292D20007BFB87B624C19E7FB7DFF8406B597010EADA6EEEEB7C682A95E6D79B56971191FAE634574DAF50476DDBB04317E804397AA7B8B616D1A89EDBFD51CEB551B1DF7E78E6BF032CEF87618612EE4425760A0FDA7C261EC70641224A9FC516D4D80C93EDBFFF00F8806341511AD021CE6BB4EB2C2BBC6343E6D54B14EDB0E42827BDC3EE7368A5BBB3297B575EA555D8CF236761800331DC96BEAB97211ED56089E89A2128C3F1BE9A794377FECBB5767D728FFEB6DB0B1DBCA47E480E9FFF00BFDFFC40634150664BF8FB3E902FC615FC9B981338147CD5D0DCE756B7F3089F2BC99D2EF00CEB9D761A21A8DF1E8163253ACAA66C6A5F6B9E882EA572504B5A37635FA99195E3F5A20A7614824267CD4A48A61B3C147ED0BDADB7DFF8086BC1D86717949AF277DF4F1FD4103AD2CBD44AE681CDAFEC359B23B5EC1A9F66F5D96A8F761DD7BA84A207AF8E2B5BC620599BAB9760FBFE14EDA89CE6B55D686DEAAA32155A25DF0BA9D55F6A49EC8206A7EDDA4DBDBFDFF800634954471AD4C2BEA7332157911292E84654C6AB87B96FF1B367D2F7F3D4D80498D79DE2F04AA532D4DABFD63071E86C86915A1E938127076108F9B2DD7955E7EF20E1DBEB6F2C5BDB94E4B37CC2B0B7D929A6D7FDEDFF00DFF8406343195629D56418DA22C66D088433F57D4F59957AAD4DC96BC62FC02947328F1F8FD7624068991D2E4854A121E98CA721D5265D58B4FF00C235C346FF005F315666A207686D97B137BBFF0068A7C372BFF9CC5B4DB49BDFE5BFDFF840634397563BC7E54AE24270C7FD023F837890E3356BA34DD3708E68F245C561E164854F9BCD9FE94FBE5A2D5A5F7AB03948C71A8F7451D3F755A22073350A7D391A896B59545DC518D76EAEF06CAEFF00B4F349B6937D2DBFDFF84863C39D661AE5741CFEBFE6C7B41C2231C4186E216C8757A746EF7EF725291882B1F665E346A971A48415C52EA1C1000625018E98828EBFEA43C37557964D8FF37804C6C1CB479FA86367D36B6DB57FE6BA6D240B4D26FF00DFF8486B633F663BD74D385E43F7D7F9778A05F198EF4D1AAF6E3594828ACC01B03BF142BE65B3E0268E1B53CCA4655961CB73C6D895E0FE7933E1E65381182A2A65E6F25DC36FF2FD8E312F36B994FD34FF007F80C6EE9B052FDFDFF84A63631F6E3B8E191EE5E83C97FA73839A3543675F03A5FCD3C6103E2C725AB141C19D8A8C0500EEE2F023E11D93659FE8F7F4B126DFB2967D20C96883638E41AD105C89E6955FAA12806FB34BCFACCD4DFEFDFB9A24B6FEFF00F84AE36B1E62893695FC13444BC65153BAE8FA8D2631152F60B5DC93080C5C0E3AF2E3076334525ED93C4A7D1D59862DB4FBFF0006ADC6EAA163891FC1BC0DAD6149A588B6A43E44AB6D92907EBE4FAF6DB6ED9BFF00A6F32DBFFF00FF00F84AE34B3F68ABF08807B5E47A651957BFDAE45E6F90EA0FE0C5EC1C8EA070E38C451009F050D6B141E3E6AC96B19CBA5BF62E933FB3425D619619EC3FE03EF0F9D2BFE19D3F9427AFB989B4FE4DD24CFB7DFF00FF00AFB24DBAB7FF00F848E36B3B68FB8E76CD781CE111B9F620EBA95C712648BC65308825AB1640C4C9A120966C8AA66C2E4EB376720BF11A9B45B6E1206957FCCF96E8D93FB6D31C0826C9339272E5E5F326CBE6BF6969BF6BFDFF005F77F36D96B7DFFC4AE3633E4F7A8FECEED70F168D314C99FF004CD94F8568CD3AAB7EC450876F473D1BE953C1B27E146FD84B7C691AEB18D347E2175111AE698A5BD9022FFCC258E2834AC50D34EF6CFF00B1E7F4FE492F92DBA7BF7D927B699CB7DFF84863E33C2E7A8F9D56E1AFDA85F9CC705EA3562277E9ADF7BE066F6565ED35F3E10F5E8FC868180296A552B9F99832A766D73AA563A436536C4CF7E42A7A66197D2DDE09E6D25BA64E5C6CB35DA49A5BFF00FE5D6DB66DB5F6FF00F8486363396589AFB38F40107D0851CB36BCD82D02C7B396D0086FB47FBAF0965D50D946CC2E3D5714811ECDC389DDAD10A5E99B2098944CB0BE65AE6B8D716919573DB789359B7F26394273778614434F77FE5F7CFE41EFF7FF00F858E3637462240E0C9A10D1183FF0F867C8400AF91BB08F2E8D1DA606632B686EE08ADC99A0251DF5603E045057917C7EFF00D00368A6B173AF794142E40C5D8B9B723DEFF567F36BA42B56FE2EE1568B04BEFF00DFFF00D7693DF7FF00F85063C16C3766FEC8A8737159FF00F3D4FCF6E423286E3751CB8963EF28C6323952DFE0E7883480CB2DBA2FA48C64BAA638362A950F3D301DEA626B04596340F418C9EF4CF96233C96469468BD6D2DA5B40188FFF00FF00D24F3FBFFF00F851676394F35CE16C9399B3E72ABBC845935E299B1C947B333DCEE38DC9514FE29F7E4F1A0DF5149EA11FE15BADA72DAB4F880C3D6C49F7FD5A97CFE345929DCA6FDED1FEBFD34BE160B14929D8B8412CCCCDFD7FFF000DFF00FF00FF00F81063E3F7B0C3A28E504076B652BF622F52BC2809196D679CE26D74B28C643FDB84089883FB94B4223861C040480BFB2D703E7111AC46335FE44BF0DA6ACA60DB9B7A64934DD6C3CC301CEB2437BC9A250A4FBFFF00931B7DFF00FF00F8106BFE0D9A723483F3767485647B42CE02AE0737CE50BAEFFDD0ECB66FE06D363EDB712737F1B64B8504144B587BA95AA0285EA731BC9B00166D0E2594E8B1026E2FF89804981A98F44D380586C3ED2C45AEFDECB26FFF00B6FF00785055AD34701A5EFC74A883810AA7F3C74179958D80C80F2F38236EEC9C13D6A3370CF8DE77B2006640A3B6A91FFE1CDE8DAED20CD37182954228522383C9D67EA6A5F7F1EEDF34B0B358B675B0D90D24B36FFDFCF8DF7DB7FF00781049F5E471313DC0551BA31E1746E9EDF2F189DE82D3049C88D2D878D4F72A5DC079F7CA15F1CD48BA956E6579CCAC55C676593CFF00024439EBAC0EC40231752D41F81EB9B49F12CE0379229656DB4C095D2FF96CB0CB6DB7FF00784B0CEB1FB60E271D237C3F25D9DC739BB440431B593A8DC60B7B8E61C53AF5289210BFD8EDCA68B76CBCDE689F7576F539B96196CE79C5348F50CC6B66F8F17F665B9E99B693CEDA465C3EDF12DB4C09582FCF67D07DEDB6FF006FA96955C99AABF6ABB672880E073278F87FCC12705B614DB6F6ADE395845831BC0BC6B5E5DC961DF31D954959359748E68494D455539A144D3E294690C69325EE8E8D4DBC1FF3FF00E9F6683017E6DA4D24DA67FBECB80F67BFDF6FE94248F4CDAEADFF003CCF5814CE9A3F16DC146419BCB176496036FF0020AD683171F07C903CEAA500A3010C3ABD4EC12807E2337BF9A69DC5BDE06A9040AA2A1B50E17E9A4FF29F7DFF00BEEA314D86DB41A49A47EF7CB32125FF00FF00AF6C9BE8BB70A6CFCE5A432A05E37BA80194D49BE84212E51672614B85B5F9594BB19CEF0AB39EBA0A4A50A495769832F0EFAADD992FAEF8A853F2B05A4FC47E7734E35F0EF79B619FA2B280E8329B6015DB668BEDB6697DDEFF00FFC40023110101010003000202030101010000000001001110213120413051406171506081FFDA0008010301013F10F8BC3F37F0F9F0DE5F8B753C67CBBE1B7B041C671F5C05B74B765D9B2C64DBC8360C8FEA6C966FB9C27338F1CE5BF0CE0EBA9FB458B6752E4BD474EEFEA7AE32CBCF2CB38020DE067C36750DB1C95D83249277E286593C74BC06DE5BC964CCE1160EECE726040DD24FD5B9EC0F0441D833AE0595D9EA12D85C03F762C2ED659243C6F539974976D8E0124238670F1B2EC5BC89991DF0707C59FE0167F15F83F27867879795CB6D86DB659785E5EF8D8360589D5D6098132CC5A5A4B6AF1B2C2DBC25A23B866DC320CBF58B785C2F4B226CBC864BD8B760BAC8F3E09277A58B641FBF80B06D91E4177CAD9F01CBFCB38CB249EB9631DF53A5BB17B671EF1E430DB6F06427D424070D8976DFA973CBB252B6EC252DFB5D8C0482D2C70185E4B63CB76C8927AF6FA9C4C782B6C32C876CDB3A927A966D963637781862CD881F0DB6DB7878DF8FBC13FC97F2E70CFE1F27E0DBC27191241B042CB3932E9C32EDDD8CA87120FEB80983936EE0323F574338F25983B2F77B74111EDD1C3C79756440CC1FAE033E1BF0CE010670472CDACFF7F02DE3A99792E5B2F077667B1DF2BB133ABD64EE3678CE43B1AB65DBD6DC8F782DDC30DB2D8888E0408C2596770BC1F71E773ED9C3ACB69383DC3C1085979326F53992C920B262EDC65911C6DB6DB6F1BC9FF0B38C93F0670CFC9B6D99E3CBD9F86704DAF0220927BB19D70C4D54C38748423BC1B4B4E338E96DB97AE0DD921B761BDE06F6EB2CB2ED87EEE8B3F51C92E5ED967191C6591C673BCBDD9926FC57634865972ED3273B13DC1126F0F19DCDBC0A5BC16F22A196DB78D8B63CE59C6E5B2CE2DAE4138876EB77E0F52D9985A6CDF2478DC8761B1812DC2DDB78678C9EB9082221E76D986DDF91C16FE5CFE2E492497965996726659C3CBF278782DB78DB6ED1C02CE338CE12C93EA4B091259D9EA3A850B67EB10AEA5E1B9C077BB32C37E9189ED18DD1C306DDC272F0F6F390B37D83275C610DDC44116719F24B1937E039CB3B6C9B6FD4F1965F73D71B67DDF7C047BCF6CB90E92EDBF0FE9C2DB6DBADD10EF1BC03BCAE4BB65DBA58E5B1BA5F5470AC27DDD3E45B66D918E63C985E5B6EC9270CB380591C1C6DBC6FE1F7F1EFF236D966781B6DE0E18C492CB24927497E6CDB6DBC0E432861DE0BDF8A4B0F2EF6C9F775B03E59797489B68BACD0A70315C0C3CE088C2CDF2CE1D3BB757D752F5C0781238D2597F5C6C72791F020B26C82C936C6C820EF87CE12D59966B364C790486C9CB2CD64E03B9E048475C671BC964E138DB13FD5B62FBC1B9D43B2EA576278CEA164877C32130DD9B45F51D427C931EE463978BB46B8672F3907C378DB6DE36F7F06FF000F7F87BF01E4B6D967878CB24C99E72DE3786FAE48EA1E0196C719CE709775D267809A648ECDB913EA547B9FD258772C360EBB9EBC838E4166477064B91B78CBFB8D5D8E1D9DF031670F45BD776E5A8E9B780EC30DA59659CE4123C85964CCE13F525967C131A9673ED9670F01C66CF5C31FDC1DCF397B25916D8C7574B7B872618E323492C08C41D5F5642CF6B0963C08EF8C997B796772DB3F8738F7F0670718FE6DF81C6707F2F784B20892F126704E1E339CE13958841064721270CAF522C9FBB4D9DDB6DBBCF73DA3A63B8771C65D4E7D46CDBC650AC7B06D812993289C5DA1DBC43AEC20DB19E136CCBB71898D634BB7B6FC2D938B509967BC84CDE0AB5C199C9D382070C89964C9DC99032416406492C8FEE7DE13E0F57B3C7D471B7B046DD4865E5AB7B1C65BDC707B30F5F06CD9E902CD8B38627B97238CDE7639EB9D8E778DF8859C87E4DFF84BC6E5B6F1B96CCC7A9B2C9E0C4C6112793B86436C436F2F1D27D9E2E999642B275749EF818777ED66C47A9FEA5872088725ACA04B85AFDCBB191896C3638364F2D827DE13439E5A5877276109333C926C3E59659649671964C7927C3272784DB3275638257091AEE3B593324BC64D89EECDF86670462DFDCF02F215B7BB6D86DF8E5B691C647C187E3B7BC7BF1DFC847C7DB2CFF86F0FC36DB78DE761DE524939EACB24D92C93609771D43C4A53809B6CB692EC5FAB576886C9C09677041C8C33A6C7CB5C0CF0B63A6186DB73C958DCBB79125A13FBBB444B6FC185900B1823A6F41964FDC857A7DB3267F4B7F76596596596596165D24E44CE5EEF64CE36DBDB385B6724CE3784CF26CDB66C8D2DB63A9761E36DBEF8EE1BFCB32D7616EA186DB6DB658CFBB6F7E0CB6F1BC36DBDDB1C6D8FC3782FBE37E7B6DBFCFF00AFC0FCDF83F020B2C926264DB6DA5B2F0C3644E36380FAF038050275C1DDDC2304D66591D5D6F7221CBAADEF8C98752E4EDEAD5F2D8CF71D4639176FEEF6390461D464B3C2F19D4F00F61CBD4B96ED89186772C6E8D864F088396FC12492CE178CB393C249C64DE4B96F739F5C2E4BC3DEC972195878F6F2DBDBCBD9EBE066F70773EDBC0D9F76D878F663AE1E0E3CBCB6D96DB61B7E3E44F71C6DBC6C59F0390FC271BCEFF3DF9BC6F2C31DC41CE493C0892D4860D9EADBAE0FC36C203093C4E93B8E3684CCCEFC04C4C970DC96D91EA1B198ED182DBC5F5233B698D4627F48D42F274B6B76EF94832E824CBD5B6EC697FB3FD5D8432499D91E6440D211EF8CB1F381CE1BCA59259659C24925925A9E36AA4B12C7F5633D27F4E0139F70585E4C0F09125B97D5DC1975647C0784FD43390DB20B23C9E378DB6DB79F6186EF9FEE78CE47838F2DE3BB79CE73E67FC367E6FC0820F8659642C98D286466E92DBBBEE79DB623D65252D965652F19131DE136E9D9212AB63C0671DA01EE5C258E9D80C8EC37F482CBEEC8C81B6411E7763059C226BD45F70CCAC86EE2EC835EE47FD4F6523BEB865AF6499C0C77C6496596739259659312CB39736244F03C0CF97D71B6ECB6C4ACF1D3D876EA3BE3DB3EADCF62FA887F716641BC67EB80CE1339F6F2D9E572F62DE33E5905FE4167C4F81F87393F1E7F159F8670D9C7BC1023E2F0D9C1993D453177878CD972526D487AB78587384700C39768766C90CF13FAC5C09E3915975789B77A8646C1DC16C4C8E0106F07BE5C5AF533ED96161FAB72FBE16197CD9D5EE0595EAC0803B11DDD97B6F6E44F61CB7E19259CE49659659641659C3C97833ECF7665BD5EF1EF1E5EDED9FA937A8F3838FECB2CDE7381CB7F7C66DB90F0DBC6B6FC5E3AB79DE33E19F8CEFE1967CF3E19CE7F2DE19F8B3104191F1DE738F2597389B379C32E5AF92F1FDCBB0CDDCB64C11099887089EDBF7365E4E649D5DB09EAE85A12097585BD44210FEA1B4C2EDDA2C821C8765C81649560ED9CAF38DEB9222D209FE457B2D318EA120D8EE5A1EDD32EC0250E7036F2CF196596739659670CC9329665DB6DFDD83C7B3A5B3DDE59305E41DD9259967047BC0F77ED1AF61387BC124176FB264F7CEF1D3C757DFC379CBBFAE0E338CE7383E0739670739FCE7E2F0F3BF121B6D96DB6DB6DB6D97859A4B2CBB2B6F0A36CFF0057D7064CDD3647B3EDB6A701DBD82EAEE097267B8BC9481B01C1848DD71EDDDFEC2CBC4791C8DD5D599F006584B0D936CB205C8C4588E0F5669B9924E267008EBDB15DF5399DB7F496381EB5B5E90FC32CE1F83F1F224938769B6519EBA9763AB787BB39CEAEE0CB20819C8C67AE1FD71BF5C6DBC7DC4438CB249EB80B26F25D8270E7FAB38DF987E0CF9E739F0CB2CB3F8AFE17F091C1C36FC9782DB2E139C9E77809EAD9BA8E331938E9C0ED1C0A45B6F57D702D2431B1CB525A257B770305EA0EFABB60CF221976208EAEB12C96F369B7B003C282D7853B41C3659875AC2B0EFF00A9D185F4BE5D762752DBAF1B0E4B7A8641B168F19C3F1CE72CE197066C989649B78B3385E32F22CD83EACCE34B02DD6DC87EEFB99E1E43B16CA26C3C309EB82D63CBDE326CBCB7EB83E616596739659059659F13F0E7F11F9EFC37E6C36F05B561B6F238D9659707CE1E5E7661B787B6F2FF0065FABBB65EA14E3BDEC4DB7841C104688EE63A813631040649B189AE1E59F77F71E43B69E105E458106B5B2C7AB4C9F70CF64212D2D25B6C0C641743F4B1F5678B549E27DEE7B25BDE07B64C7B30733C4908E32CE336CB39CE59652CF7C1CB03916F6038FBBA819182DE37EA4D8327B66F6FF6C8E7227DC36F7C0659ED2F033185BDF3B0EF19DD907059B632C1659C241C9C1C67E438CFE2BF95F9EDB6DB0C37BC6DB2CB04BACF7CB7D7C1E37EB8FA9F86F1EDFD70E3D8C91BF782BC0EA35114A56B76BFC8D9BDE1F66EEF1DDD7B10B2C1B1AEBC26C775B5E37EA5FABBD870DB56778026D4BAA9DDA7B1B544FA19FBC28CE9DADD123848BC98967BC2506C41BB1FAB6187E0DBF2653D4EBF1EADE1360D9FBF864752CC30DDF0EEDE024E7EAF62DE0E0762CEACCE18BBE722CD7E01C6705BC1C6739659F1F78F3FE0BF37E0FC5E5FC20D5B6F50D867E3CF82719672F51DDBDCF7796DA44397B6CB817630F2DB7AEA21FA701D8FEE7DB223DE0D63A802D3EC6DBBF5B199627ACB9E441ADE5B92AB76336D1FB668290174BAA58778E0F5EC173C313DDD46ACFB0F50DF2CD6C2D92233B3FA8181C0C72CFC965D9992C8FD499C1CB24D9F0DE32EE2376D8E987DC4BCEEF19671BC16E5BB6FCB238380B3E6C47CF3E3BFF09FE165BE0996F2BC8E4CDBC7C8CF199C36FC1879DE1E1E478FBE37EA5CEA5B61036213ADDA50E710DBBC059C42E07EF67A785C2E9ECCB174247AC85BA7CB3245A6C3B82ED2985D31E421D8EEEAB09BDCCB5F2C5D5A356BB992A6470DF64C790EA4DEE3AB27969D202FDB91B7787E1BC2CBC249B365E4DED964927038F66EAED1FA83BBBBC864C83647C9EACE3EEFBBCB782DB761CB61F8967C320882CB2CB2CF80FC0B7E5BF37E67F15F93F3DE736C8387E05E36F67AB6E9C56FC367E0F2F0DBC6F059C33C2701DDE4F0CB91D3BB5BC045BD70398710BFA4698C1DCFED758FA915366AD33D36265AB69BBCB4771400F263D219781B2E2F11E5B3FC9F612E40E100F5D1B64935E4413ACE46F21B6A17ACB05F5391E5E5E19F87DDDD9FB92CCE592CD9338CC918D59DC1965977BA49B189907B9E727AE72F2F78C8E3DE03E6111F06CB2CB2C8E0FC39FF0009F933F8F6D965E16FF65E36D9E3CE3C9F82F1E5BC6CF2CCA1197B27C5EEF38DB438D1E1DBDE358791761FA8F6DEAF50CB48366EC125E703BBDF6CDF21318F781599966B0CE0A12D8EAEC70EA418853B98EB0D4A773D1F6F0789481F1719A7B2BF7C020B21F8887706016D76D7D969D5BFAE7786CF826C924CB6AF1BC2592597BCA7764C397B0593D449244B93FB9FEADC98B38CDE42CF9166D991C1C0410707CBDB2CFC47E53F8CF0FE17E07C566D97E2B33F104F0F29BCEDD4F7C3DF1F5C3F07F517FBC6DBF56F52EF0DD787209C24CEE1FD43C071DBB31F1B35CB386874421DCAB6840B1FBD810752C460750E9ACCF1316C7A9631CF10C1A790D859BE98EDDB07632A3D0B14018DB3DC1645D5E4877753B8036EEAC3D9779F01E327E0D93316701C64D967396496161CEC36EF0CBDCDDC65EC906D90670F01677CE70041C9DD916596459C67FD37E4FE138DE19EE4B249278787E6BC3CF977379C36F2F5E4FC582CE3385FD706F48E0725EB6DD7655BC4025DEE1D9DE97AD20C7BB367481FB8A625EE3C8EE7DBC43BECFD0E736D7BB3024903DB7A9DD81FDCE484EACC4060B0A845D47D438177248B26F7C08BA9798B5613D9C3D581B2670729CBC3332F1EF3B333C6739C65E70F01333EF724D91D477C7B633C196596596AC8231241059041F1CF8659FC0DB7F9CF0F0F0FC73E2593CBC6DBC27E067978CBCE5EA3B978DEE6FA966F6F26FA8EADDE5B2C49DDB3A896F2C524C65BA5D06DECE0424044DF2F22276D1EC9B768B0E88648F2727B7A8396AE906AAC3F6971DAD0FBB03B611969F521A6D1D471DC84EC95B0757A823A7059DA41B186CED832747AB3EDBA6AF14E3D30CE15BC333CB249273965926CAFAB1BBF8E59F038492F2760DB20C936CCE32CB26C48EC820B2CB2CB2C83E271965964FF001B7F94FC9E1F936470F2BC659C2EF2CFC3EB939678D9F8367C36F6727808F6795CEEDDEDB77C9BFCB6DEAED66315F532C66CCBEA1C25A93A8EDBA385B3BD985DDB27DB614CDB20E1B0883EF1BA77F501DD64F2684DDB7EF76046EEB907F704F65EEF566903F73A607024EE40F566CCC7336346598DA4E27767DF03CB3CBAE0F23131B2CB24DE0F7273E4F1B7B60739259907EAC6CCB2CE071965965911C1DD905965967C02CB38CB2CE72CF83F8F3E07F21E1E1E1E1E1F96F0DDB65B26CB26389E1E7DE3CE7BDE167E3BF01C9978F27838F2F227DBC9CB3781DFB37B796176FB6686FB19986308EDC30324ADDAD901ECBDCBB6B0A002C3E70C80F641D12B1973CB6149DFA48335DBEFA8DE9EF18CFDA47ADEA61843DB6C9B35EFEA418411BBCA969B6B200776FE4DB8E97D996699678711FB8ABD4E18D9A6911C3659659259C24C926C1C3248CF53649C64F9F1F387BBFD8E378CE0E72C8F865E71DD967196416719F0C9FCF93C67E33F84FC9F83F0D9B7E393DDF7C324F03C79CA7C326EAEB85B38DE193AB3397B2C9BEADB34D9BBBA5B65DB7567EAFA8F708039DB74253D25061BFBE1DDEEFF0022F7811159C4BEC19C2907659EB7F777DD09089B44617EE8D7CB13376C9F10074CFD5DDAF8CCC43DF6ACCE593A84BBEA7502B9960C98F7233AE320E9230E705FBB0EA05CE6D9616596593CE49659649C89B279CB2F6DE3CE4B1B2CB23E592596596591F3239CF967FCC7F819F02D8F83364C499E5E773E24F83ECF1961C2C773CFD5EF09271E9979C37AD8BB647794E047717645658BB02C1D41F72FD5FED93832D7D5D3B6EE021C3ABB5DA3AB5E5D64CC9A4621EDA6B7EC7735BE6BC7BD293C2498EA57BC9F23C9FA650DF64190EE5D101FED8D6C2F3D5AFDC37D705EC93601D46D5B72D4EA1DB6DB7E0CD9671979671F724965924923C7DD9136591641659CE6719636C1BF8B2CE7383E19659FC5CE73F8EF0FC1E1E5E379DF86C5B6F1B6CB6CB3F1EE6CBD3E2DB6CBCE7070B79C06F1B1D32DEDEDB929F57FB3DF930A81C25FABEF65EACD7700903931752B631ECFB6D9B68796CC366494F08D79792B2205802FB7772E883162688EFF00D40935DFEE7861743620B2F9684A0097704E46E6DA56C577C00777B0B3ED9EB564F47083A9F5835DB0AEB7A3C4E431EE7631FAB53A7B6C719C65967C5E53867B92CD92EB82C8380B2CB2CE536CB2CB2CF9059659659641F13E79F8B38CFF0080FC9E1FC6F2DB6DBB33D5B2CFCFDE1EB8667878CE1D9E1D93A8EA5FD5BC3B777B6333C7F51979C23607774BB58BB317B946B61ED9B0CE2F59108928985DBB2AB35EA03FAE3F44DBD4F6BA10488006C41FA999F5754D9F4F2D2EB7ECF962713877226483A2C1D603D474E5D23D5F547BD8C7A801B13D70EE8C0B86C8DD3D3ED9C0744B5DC924F72713ED83BB31AE1F83CE70F0FC3EEF64B20CE436CE02CB2CE1967059C3248E02CB2C9E33F80FC98FF88F0FC9F9BF07E44CBF04B387BE72F3E0E4F396F1BBC7FB6FEADEAF38C9E9E12F258DB763AF647DC6200CE4118129C3596BC025EF87446C1B0032EE750F51DC859FAB18EE67A203589EDE5DFB6CC6BB9EEC7A3622900B6E0F5F600C1B7EFE4B95E36DAE99C7AF7237BC095844CBB18E776E93C87D55D6DBFAB7BE0973D949B478843B65F777781E6587A9A0C21930E5D719271967C19C33C671ED97B67D44C923E20CE09C10C3C76E60B2CB38CE32CB3FEABF17E0F0F0FC9FC27078F78797E093C331C37F93C7913ED8C32F1B31C37F76F577F52DB9D5D0C6D093C5AC8307323817F710D59EA31EEF261D51D7B17D4773D7506C769D5D4161C5BBD16E0B66B08F443AC72163641044E9F6C1EEED435A6DFAC2E6B677480C5D24CD81FAB3BD6FAA3DCBA24EDFD5E4865D117F9606B30B96B6DCB7B9EEEAD9BDC47683E985D3C433A7C955B9622CF23F69E9B5B1124967093336709659C098F8567C9266416647E30B2CB38CFC79CE7CCB3F2E5EFF001DFC6FC9B3859EF8DCF9336CCF0F737D4B92DB133C3D5BD71D4C79C665B1B265EB1A63709917513B75184BACA04FE92EAE9DB02C3D972D7259892AB1DF5742C335BD75745884DC31D48AEDD1EC1F53EF9762D011E8278DF3BB6759B8C63BEE4FB5823F4C86791DCBBEC9E0C29B08C64AD81BDB7DA45F191EC3E24E03AF642C7508363D907BB2221FA8323AEB0A7515327F6E3D168FB3921926AD5A3DBA66659670CB268F89087232CB2CB3E39659CE596591C65967E1C9FC3967E039CF867C33E596FE37E2FC1E1E1F9E4FC02CE12794E5EF978F27F7374D937DD924F09B7DFC1FEB876F2DBFD9E1675F643672ECC2388CC61D9EF2D9C3EDB0D8BD2C493A9EDB608CCD90DEF57B99D393DF96101B31A2D965D1B01EA21AC6B8719B2D13EC9EA169F51D7248CB4B5BA40419E5B0D6F7B8ECEDD0EEF5DDBDFB20608993938EAECB4F1B46691DA7F56F5D5A7D5A646BD7B3293380E719196338BFFA2DC6F1D3B808125B7A9CD8FBB0D8B0DD719C04E1EC946B20B2CB3E19CA59659F1C8B2CE33F33FF000FDB27F13F178787F1AF39CBC6CDE4F396F3BCF9CB3C32CE1384EE67A38D6725BD82790D623D3EE1E202E1B369BA85D76D5EACF104F25B025DCBB0E75236A3696F1ACEB22D72EE18B031BEE2E8093758F23D21933DAD9EA35DC969D7D865AF5BDEE3D907D97BA650E92E107DC83E5D8CC98761F4BB19F75E443D6CEB26E8971EAFB33BF7606367DCC4E0F18BB467BB043FB9C7AB3A5E32C5A4FEA1C3BBA68B3AFABB2D47F7BE8671E4FEE86FBBFBEFA365277065B169C649CE7C30E11E1B2CB3E793D41CEC59659F07F887F2DFC2FE2787E3B364CFC36796786F278CD9EA75E3FAB65E7366270E3273219E14FD27A590C2EE44C973A2F1DDEB0B2F6006F06ADECB75C39C1DC166F53C3EA6D823B854EEC6C1B1D2E364D197BB62ECC9A003969DAD1961E4B104630B6C45E988ABAC64F703E97E8DD1A1BECD3126F4CBEC8C7BBF62C37B9FEE7AC431EE1991EC6F91EC93B78BF6595B249649EC90EA31A26E92ADD3D94BFC8FEEFF0023BE36C12E7938C37ED3DE918D38ECE32CB2787F0672F0D9F3CE36DFE2E47F35FC2FC1F8BF138CB2786F27978F678CE3DB3878489E12CBC8E1ED9B367AE186DE080BF70B3C0EC365D252D23ED6BEE5271803A8578748E9AD8BE436D30CBFB465BAF65FB160ED80D98EEF977B9E58319133A2C12D6103B5B842D5160F614C5E991E8D9062D191F44F1ABEC8AD4BC4F1EDE2B7EEC1DC33ABCBCD3A6A7B6D87560737659F764F73EBBE4B20913848C12B78147DF0EBD2D6DD2D718B68745FDB6C8BDC179C20EB613DB0D6C4DB3E6CFCB2CF867C1B678D8B7E6FFC6CFE13F07E4F04493B6C3B39C6CF0FC727A892787819F79DB78DD9E49B3EE4D920BB610767DC03A2E84F769EDD08E99B42C634EAE865BF4407DC47441FBBDE8808C632D0F205DFB0663C1612ACCB48D4C0C249C97D4F70E12D5560D7D84709E7233FDC080F6203931146C6E4B44D93A7D5EA016A465DC39E36FC3DC8E1FA89426583BFB757B8FDDB93A49A49BD3664D8496BC3E4F76ECD8B6750FEE73CBCF6DC7A8B4DA3B6EA01BA469EF39CE832CA7533C313C6FEB96CE7DF86F1B6DDFCDE3CE1FF0092FE17F0BC3C3F179CE592C6CE326264CF2F0F7CBCA4CF0F19C336DBC3C245962210BFD996D5782EAF4E1DDBE921D4C0CEEED84BACFF00525E37416AB97499EE03EE45D3C54223DC7804188467DC93DB7D0F27B946742C06AC1D11B2CC209190FB966C31E908E23EAB14C06D475BE2D3D337DD81EFC84376D69B7BD95E9BA8E9D38EB87E9C336D1C7278CEE4DB6319EAD16FD24BB776A09EC43FBB72C4F71D90F3BFBB5FABD459BDC33386DB6FF233E0FE3F7F33FF003DF8BC3C3F85F879C36DB6CC1C181C3CB9F0EB809B67BF2F2F78EB86CBCBEB8D9B2C9DD65FA1747BB27761AD962E968CB21BB060EE3BEA0186CA5CEA33DE0232B3AE85A36D6166D98ED3FD655A96616048B333218EAEFB5DEC18DD984CF60989863BB61DC9F658F198022C35290FB0AC8C803B69E9DC9D92CC7F70FB87EEF5DB37B25BAFB93D920358D4B32081679D4E1324B6DFD5A26B01EE5C9EE2C76EDEA46C7C923AE3749EF8186F6B66484BABFADF76DBC6BC1C7B672FCFCFCEFC73F0E7F3DE1FC19659649659C6596596592704B780B248CCCDECFBCE719B793DC2CCE09EE4EAD9720EAD9E3BE35190C1DC786CC44CD6E96475299590F657C9BD61D720D65DF5275DDE22DD93EE1B745F617761100497C46219C0B8867F680EE27B6BC312D2EDE848589616ECDB844F578C27D65A009E40185A0D38D4F73D3485204EAC83CA7B774B33BBD8C4E1A7DDD1A42E77E479D4B9613C5E76784EAD3C66D91D3B7BF0671D9B2573A87F77F92752D8EAC9C964CFA784D9E3C87E0BBF2CF93FF04FE5BC3F9938CB24F86C59BC081337799C9279CB38D9652CCF04FE8BCB66C9ECE7326198A2DCB6ED29B85D124E04BA5EC6BB95F23917DD9B0ED806CFE91D1907D5AFA816CCF25B3D5A65DD846BD4ECD3031C6F3B36CD65032F4C3960DB5EC32F5D4DD088EE430B4DD3DB4EC9038DD9D4A7D0DFD5EC38CFEB87960691D583D792CCCDA5BC2E4B79776CD97F76E5A2F71FAB7BE4B792732F26D10C74CB0773D767DC9CE0F01C3321FCBF7C3C967FCC7F1BC3F178DE37E1B6EFC1E32CB2CB7E25E0D9972DE772199EAD9EF979F2F6EC97639F65886FEED917D4460CB63ADACB36094E88027AF27B861ADDCC9723B6B185E020C776EBA5DAF50EA5CB7AB084713EF5B4556686196C32C232C2F96C83EDD1D5A31934756EF4DD7CE1E3B869A4817D49C3BB48C33A6D9C39B015FB27B122979DC23E5B29D1B3EEEECC8BF77ED3CB974DE71EF0F7187760C80D8ECF6C43B0FC0E106F563EDFEDAA64609B7BB26F52EA1D9F26790F096DBF99B3F8B9F849FCDBF91E1E5E1E1E7384B2C92CE72C9F9A421796CBC1BC670B7B64F1965D5B6D9B7B75133DF0A7A2112412ADF700918E08225BD5B6DD958D752E4AD7B877D475E4C3580498CB7E964A34CFDC8899B381243A93EDBA5DA42D9189B2105365894B859E1B21DEA1D5D3041DA238ED93FA2CFD439C3D85A8C6D17EAC74B5DC9E8D976DEAF1265B36EBB3EF032ECC1BC678205987B75F6C3FD46E702BC2843A5F50F0A12DDA4E658B68E0137784E1EB8CB393E07CDFE4659C9F07E07F19E1E5E72CB3F1659364C1671BC25D783797B67193C7B6B2F077C3B29CE1325B7566CAE042FD194E59FB86B18F644A412FA21C63B9C5DAEC19065DAFED74EA55FB213EA5B07B90B07B09F53DA2650EF72263E4847779EA1765547AD67CEAF726DD8EC9918C19F5C12C7B98F8A7D132706582DB763A944CBCB549B7ABB175670FD480EA13EA6DDE32CCBBDD98708D63078105B96DBDF05D21CF383ECC676D1D41B5A40DB05DE0E4116DDE16D878F6CF91C6F1BF9DFC79F17F94F0F2FC5E767F03326C1C6F2CF7039792F0D9B6729B675F0DB24E1BBBC9910438D62185E40B3DE0EFB8C2725D605D7C807B2EF90BF70EB61F72659B74EEC2C2F729F526F96B832DCBB3B3D10E91D4861372615BC967ED0EB20EA4EBA9279D3294F0CB3BE1789572D96F3B3BD169E365859E30277C06DB6D86785866A1B0CEE67DBA9EB8EE674B63861FABEAF7E01DCFE8E3CE1C60237C4BD423A2EDE581EE1B0DB0CB9C59DADEAD9C42E0CDBF2DE1FC3EF19FF41E19F93CBF24F8336F29CA4952F0D965B7838C9656CD9EB864C9DCE333CBB30270EB200D5B0182CEBA81B36EA38948EF1BE88365346F50E497B856D4979259DF6F0F6E9EF1D1B7AD87587EF8662BA10642D90235337A9E446C011D2EC5B8C36C7138B22DCB3EDFA21D2EC5B185BA5DAF60CEE2FAB5EA1B786E44ED2DB3F12FAB3619EDE5B3778493BCB32EACE3360874F831FB9F6F1BB460B21EE3816FDC8A6C3290BC925A42CB188D9632FEE1FB6FEB0A330582DB6F3BFF000DFC8FF03397E1B6DBF1CE5E3658E5E3725B33364F0D979C6C96C496F0DF5313EA0FB83E0BD6D9B64BA098A9D6CBA4CC16E5A5A6C8F0E03A85BDCF73C659058CBD658F6FAC842FEC977247D9036BBB277637228D4E1BA2E8DAB388D37576D252C9CD9FACF4863B771C24349EAEE2FB380C89D1DBA67035F00F72E4F590FBB739663DC74DB1DB205ADECC6FB3D432BFDB61B35D977C83796F07702099964085D6F1B1D9D47724F1B93D93C65A4CB3D84BA81877FE4BFCA79CB2CB2CB3839CF8A59659CA4EA780C4E19B267BE309EAFEEF6D997EAEC956FC803AB242E845D56F2CB1A2E885F6752CB74C35BFB5ED89630ECF19B6E476B378CDEA23D4076DBD5A3D5BFB0FD4C3DDD2750C62DF597AEB83DAF5D705976B7AE1E4BD5D2156562D332162C4D6750ED8CBB97F56AB21C2274E2638865AEFF57B664F72EF27CD8CFBE3CEE5B2C61EEFB96C6312E49BDDF5C06387AF6DBDF2D8DE3B4FEAE8CB18ECB0D9C806357576364DB0BFCE16F509B3F517ACDB0D842C3B3C659FF85CB2CB2CE324E32CB3E7925A78E538939FB92CDF2DBEA7EA4E2C932FDA7EEF201C2FBB2DB12DDABC792CF725B841B37DF07C82E919E3C60435EB856965B87005B259BC48D9EDBBF567DC32DCBD76CBA9348E993B1FBB6619E5FD6213C0D663B6E8EDB065D267B09C74C797610587EE00765DF235EA5E365FAE124CB36CCEAD6C31EE4A6641D5EB27DBFCB23D8FEE60FEF8FEA0D7B9C2D259E70DA5E7238F0CD20C08DF27A2F6446E9DCBABDB424EE4871934DE32D8989BDC35134EF8077E19F9B27F9B9FC167E64FC7DFC1931659616408964E32CEE676C33CB2508763BD42032F6E96A16ADDE59BEAFA8BD648BEEFBB0C80B2C9720DE004DBCC59DCF778BDCB7796116CFB06670748F63DBEF24E4103386773EE47BC7CC96703C4CDDBEA57E10C5E792EA72BC07B9E9E079CBECCF26F7ABEEC87AE1F6CBEECBA7530752B6F48FDDD993BB04752750750EE06C23A6EDDB07713E711085EA0EB9838047A863C3EC5F717621D4380FE17DD967E04FE11F933F17FFFC40023110101010002020301010101010100000001001110212031304151406171508191FFDA0008010201013F10E4F0383E3DE7DF19C673967210478E5961361692593896DE3261C96CDE19045B6C3C2F90791E3D5BCEDBCAD90584792DB6CB2CF80B222593CB0CE088B3C0E087826CB2CE761B6D965B79D86DB0E1BB26C996702B25DBD491EB8C89E35B6DE36189E322082C9F532C3296C3CE41049671B3376659B7C883E2DF98F1783F908E0E0E72CB2CB3864164792CF018E2DDF1CB1B2CE3380D920972D9EFC1889E322F56CC3E4F83EF8F5C1C696CB1C332DB2CB9396590659C659C364B6EDF70C721C0C41B6596783C33E0C8CEDB1C641C0590590B5B011F49F5D4B6DBD4DD786EDDD9C2CC44D910E06124909EADB61D8820B20B3864926D9659F0CB2CE0F8B7F836397E67938DE0F80EB838CB2CF2F56CC595B6DB6DB1C0C465A59BCC99C6DB6DB6DA78841BC72C93C4E16DE76D2D24E379DFA9CE3D5D5B6CB3E43D447270CF0B910700B2C82CBD59C77C7BF07D719C6F0C366041661C3220E5B6DF0DF0736995608D462C8427DC65B17DC3901CB397243109B0DB2DB332F2CCF016596701FDBF7F16C3E25B6DB6F079E591C1E7B13E065E37C4D8E0186231EE732ACE779CB2C8819CE49E278FBB7279638DB6DF0DB65B794DE017AE0F0DE19B20820888F0382CE77E0CB393A719659C66F0936CDBC6590707F997A86D99C3A5B165ABDC0BAB62C9267366C167247AB72D9E1666796CB20B38CB3FF0CE76DB6DB61F0DB66D89B0DBC1F36596707A999B6DB7C36D8614A12F71960C848B19C18DC8390E0CB26486F07C7DCFB8E37ACE76DB6DE3785E3666DE37C8B61B6D9B27DC47190FD71B6DB6F3BC6DB6DB6F9659641659647833C89B2CB208723ADB6327B86721DC1DC7AE3781B7925E0278CE06DB7785B65F16CB380F81FE72CF339CE32CB208E12C8266DC88426C32C36DBE39E596709242493C5B781BC3D6C486D611C837864891199C422609E049E5E37C0F7DD9DD93D4CF8E59664F2CF83335865B6D86DB7C4B6DB6DF0DE1B6DB6DB6DBC36DE378EB39383278F7E396F03DC910786CAD89970BB98C3901E507044F663AE1BE7B6F8678659C1E6FF2BCE7C078E739C133C325964445B6DB0C31E59659C1CA49C5909E36DE3621965BB113A4966444133EE0D23ED0D621D4B67072CB6F0C796CAD96DB63BB20B27BB26249258DB6DBE1B96DBC6DAC28E0430DB6C780109BE1BE05BCEF3EB8CE5E76D996DEE6259659CEF0454C7B888CC532C71BA8CF1BC36DE0B3F84F2DFE5CE0F88F03C4E5F31E1658781DDB96F96F1BE0C99CB333C6C3C111C0623192C95C08EE70F0978DB6EE70DBB0D864CB97B88221B98E249EA596DE0363AB6DCB76CE2272C211ACB7A8D59CB0407D4F0307C4361887207937906186DB6DB7937C47C07825B67C19E36F7071EBC77CC6DB6DCB56AD8E483CB2CE5E73C33CD6DB78DF1DFE07867C8FE0CB2CB2CE32C889EE1B6D86DC97C48C1EE395494F0C967245DA2216C2ED38B780C98A1E0B6C7719259932E182080820C9B34864A596618B6187807DC416B1AA7A9C6A3071918AFD8D49063F7C1E46DB0DBC061B6DE1B6C36F0471B6DB6DB6F071B6C36C36DB6F1B6F92596739259364964F8117777C6DBC916783659E2F19659678678B3CEF3B6F81F067CE7CB9659678659673B0C32F1A96CB0DB6C392DE5366B58F2196416A0C9408EB820CCD88B2B65E1B6C30FE72CC24B1623874965E07807891D5D090C37DCE16EC0630D2D9D4E7B23123636CFABEBA96DB61B4E036DBC061B6DE48863CBEA3838F7071BC6F3B3DF27190596596709CA739259659C1F0059C670719659CA719C6786719E3925965967F71E670472F1965967832DB6C30F059332C8E708D59649C963C160D87171F53D4E38B6D9E05DAC9B2DCE4423A581C765936E96A0CF00E17783E39316FD17A99906F0D991E993DCB4B7BC86D962696673B0C30C3C16CB0C316F071B6C459CE41B67565EB9EBC37839C5E1E3278CE1E366DE367E0CB2CB20B2CE4F2C9F91F1CE339CB2CE33FA0FE0CF0C9EA596DB6D86DB72D43B66F02E47765DF13C75944F7CF7775BB702630C30F5C8A20EC36F6CB360B20C964D59D3C12623FD9025CB5C06C477C19C96CBB2C367DD97AF73904DA613BBBEE13D46F70ED8C99D5B6DF54E992CB395C0DB6DB10C36C31E0E40F0F7C6C30CB2EC2DB0F99C16E73BE39CADB36F1BC6C3C6719059644719F275FF86FF20C7C6CB2F1BC6C36C36C42B565C618645D78E6CD3899259C1DD6703CAE3D5BAC2D81385B22CEA49723B787BF5388E4B8DB6631D4F49978DB78057A8877EE4E1B2CBFEC2474FDD832A1D483FEC2E633FB2C36CFF200D2CDF5249C10DB6F3B16DB6DBC06DE4DDAB70E2F78DC36C59C9C64C739B2678EF0CC93DF0BE1BCEEF1F7CE719678EF2F0FF03CEDBE7BF03C9F29F09E45BE0B2CF9116E70380EE10432F5CEC6C1C167909896995671B0A3877C5796CE25B6387ABB70B2CF2BC1662CBE0D00061C659E0126CBE869233A90DB377F6F6195EACE1C127FCC1300750B39DE041B78DB6DB61B6DB6DE0618E0E07031CE44473B0CF0B6DB6DBC3CA71FF78C93C0E4B2CB3C09F1DBAF8F79DE56DB6DB6DF95FE03E3DF0DE3785C33E19672F010438BAE42CB2CE4F83491C319C9636DB2F07047AE41D8382DB26C92412132DB3C3C7A58D6B1DC9C04F45B97BB249EA274A26FA41DBEE61EFBB1DAAF6C8009EC97D19E8BD7BB042AD218DBACDB3C0878DCB6DB6DE36DB6DE378188411C1F02F2B6DBE03C6F192709BC33963AB6D862DF88F1DB6D96DB6DF819786DF1DE76DB6DB6DB7F88F03E1CB3C1999E32CB2CE196596442102CD832C833E53878124244BC3C9C040C2CEA0865BE15CB785CB783CECDECA30F5647E4CC7A959DB5235F52B30D9D51674EE1070748DFABA3B3DA77276E1D3B58DCF58D4AF4DAF226F3B0DB0F88F1B6F3B0C721C1C1E05B6F1B32DBC6C32DB1C8477659670CCBC0DB0C47F03E3BC6F3B6F1BE5BE7B6FF0031E0781C1F0BDCF0C59659E39C02C8411F1E5EBC9E09B3C0C46C6CB38D94E10E96E3624586DE14B0CBC3C259323C8AE3B7AF763F2776DB65B6F5EED4766B6D10118D527DE4992C3D461DBFF8368E9B51BB7BE0174B36012C3BBA6CFC9338DE078DB6DB6DE36DB780E0116DB6DB6C30C71B6F0F0B6DB776F07059C365924924CCB38381F2EF1BC2F1B6DB6DBCEDBCEDB6FF0007AFE03C4F9F24B2C82C93CC83C03E6F7C9CB769E066D8E14B5C19B90CC2262B6B0CC15EE7879DE16056164C3810FAB4759C1B66C0ECD4C5FABDB02B12176EE2FBBF5BD3A5932111E757AB167FFA90C7AF71BFEAF496CFC84A3193ECB4773671B6DB6F273B6F02108F0D865865E7B3DD9C671BC11C6F19CE496496496423E7DB795E76DB6DE5F9DE3797F80F88F3CE738CB2CB24939CB2208811F231E07966CF07FABE88A24921C19BDCF3EAD8E596DB6056161DB5B476CA0D954F776430B47AB7ED90EB8864B92C793F76F584B1EA7A85DC16138F7778BA0EA356C0B4D8BA960BEACA1B3B1EA205649671B6C71EAD8E4217688B6D3C465E616592709C1CEF3B0CF2F80C36C3F12F1B6F0F9EF3BC3E6FF59C1E0799E278878B67274B2C9632D8F8B626DF03C739CB2EA4DB2CDE010493B670492C9E3679660561045FA9FF00248E6243F27572545AFDC36FD2357B6EF7BB31A04F53FA112CB7FED29DDDF511A9D9A482FB1C7ABF364C8753A6EB087BBB74980A193D6FDCDA3C27811C67044111D430DB6F3BC90C3C3E470FAE46DE361F02F5E0964590786DBCECB6DB6DBFC59C1F2FAE7DF27CE7C47079C3BE19CA6F0CB3BE07070799E0D97AE7D72F96593EB90EA789C42CE0F0C92373598EE16682CD920F42ED1058F574E3EA16991B3C10AC27F57DC4D74FB80E221CF4C6DB6185EE247A872F4989BABCC1CCA677BAA7A61DEC9364F038EB911C0C436F0786DBC0DEF8DE372DE372DE365CE08EF93938CB2CB2CE0E0F0DB6D96596DB6DB6DB7CB3F9CFE23C0F23CDB72D9E0726C8E338C390459F11F067C0719C76F0896770BBD9DBF09E1E927705D8B13B82593D43650F574F4CBCC65833B8FF00642CCF7174E9B5BEA5B28B948EE4EC2C8D7B844702EC1F52A7072EB17FF783D2C2630FB86C396AFBB66E84BA4D76B471249C8F07247278B7838D878DB6D96DB65B786EDB92DB0F8EDBC6DBCECF7C7AF0DF0D967C37E179DF2DF98E1E73E3383C0F95F0C822F506F19CE4781FC9B0F190DEF9F7E5926D8700E04B392F788749D4B0492A1A5B1FB77BDCAB598D8C3562B0DEEFF563EA7BF76995817721EBEA3D1103597D90E3EB61DF6B5F496C2482F4B652F5907B49B68D7EB7B11D712F77D70927036F07036C36C476F80DB6F1B0DB6DB1CEF86C3B2591C9F26F1B6F1B6DBC2CCB6FC1BCBE470F96FC6C4FC870781F11CBCE7011641E24719FD273B04707824F53322492ED27166E5D0EA53D5A93BEE1253EAC33BB37D5B6DB3A31FEDA66DDBD599EF83AE9F56259600B00B387188722E8DD41F76A49DD91BF8B07963069172386F0FF00B624396D8D86581DCF6983C3249C0F1B1C0C44788B78DE4E36DB7C58383CB6D96DB62DB6DB6DE365B6DB795965B7C779DB7F8F7C8F03E5383C0F95F121B61E08E4F84F9BDF87DF8F73647390DB23AB2653C3221932DA7B972F761F506CB6EFBBFE73984125D077C2E99777FDBDAEE4CC2EAC6A3A84411D26559752FDA0290031340B4F728B2C865987EA7DE48F6FB82B85835BE893F56B0C1934FAEE01A590E47823818623DF03CE479EF811271F711E3B6DB6E5B0DB6DB0DBC365B6DB6DB6DB6DE76DB78DB7C77E4CE33FA4E0F03E678D8E060B2383E438DF808F3DF1DBEEF7E2F077C245A1184CA781DD931D492750B6403DB747B61570B2CEBBB75B673765D94CBEEEE6C07162AC7DC9FB3D47B2E824EE41D96C755A7D9774BA95BC03BC81B19EE2E9B0B89F6A14D08EF0DA614F57DAF7242382188E0861B6DE1B0DB09039DE76DF1791BDF1B6F1B6DB6DB6C36DB6DB6DB2E5B6DB2F9ADBCEF1B6DB6DBF367F41C1E07C4799C073BC1F29E25BF065EBC76F7E470D9685880C8CBE8275F76CFB8410E19B665DCEE507A9797762FAB4237FD5EBDCF57767064FD24609E32071B029D4140898BEECB0BEAD90C9BA10E9854C83AE1B9291D76447DC474CB446D8D1EAC5C803B63360DD8F51DB1B249E47239C0F031865B6D626DB6DB6DB6C786F1B1E1B6CB6DB6DB6DB6DB36F0DB6DB6F92DB6DBE7B6F1BF01E3BCBFC870781F3670CE0E065B621AFE3CF8FAF0F56F97BE06A0F777EE74E995992BC3FE403B654CE08111D484F7287DC3C831EE5DB6EE7591500F70B5DB2CD9C2EB1EDB5EF7467DF1809AB06FD474E933B44467A9414B068EED6CCF67583641EE41DD8F5C83A448A64BD674C4A6F3232FA251F4A4591E462DE1B6DB6C310C3306D8B586DE07CDBD71B6DBE4F0B96DB96F92F1BCEDB6CBC6F25BCECF816DB6F91C6DBFD070781FCAB3111363E23E12DEF8F5C16C72F51C9EFC961ACCA3AD87D4E5E8810645EDB00B4F44F18FB82246D98F5C237E923B2CF53F887BEEEAD861D9659DDB3F91FB25DCED3F56E57124B88F42E40040EF748B003EAD0EE4D8338BBE18CCBEC9E365D77263B26C9D7EAC872CDB27D4FD1B3DAD84E4739361B61B791E1B6DB6C3C0FEC3C1C6DAF1BE3B6CB0DBC17C76DB6DB6DB6DF856D86DFE4DF8C9FE03E63E1C9B7C0B61863E0F5F27BF03839CE4F8055C0374E99C307ED7ABEC9C99B61CB78E8EDB4FAEA18772EFA9C9976D9F6DB9174CDB3087031C91BE8865C6100B00161EE40B63A0EADD6C2E5BB7EA439B72CBA847531ED9F50E1D4FF002FA25D49E088ECC76CA415A2093265AB4B01A48C1E430F0DB78D86D86D86DE0621E16DB78D86DE1B6CDB6DB6F5C75C3D786F86F1B6DB3C6CF07036F3BF11F06F86F81E67C070781F091CFBE33839CB38CE02C823FA9F02F5E5DE12EF6DDC8675674C87DCC0C1FB0F5D4EF0CB7BE737D40FB67BB7A81601DEDF8B751EE4CABBF0F5CD87AAC89E811FF0103677BE9930D2DAB6483BEA7DC1865D211D751E990E21AF031DD086400EA306DAAD67AF50C6DE89C1D10805ABFA8C1F795A01C7BBF458231F5649E03E5B0DB6F27036DBE1B6DB2C3C365B6DE36DB6DB62F5C3C6DB6F86CB6DBE071BCEFCBBC6FF0011F11E072789E27C196499041659F1EF07C071D72796EC471A487DDBB098B768175BA4E077763215F77E0277D70F7C0560F760FAB43A39EC423D64BB8926F9B658E9D8C12EA056C44885FB136BA90D25757DD8EC0661EEEAC7D435D5A37EA0A97ABDE1B7ABD65B274CE1D9EB93D77C6C008DD365AE97AD77F539B0C7A8FA322C3B2D10C97A2CA447BF1238DE08E08E4E76DB6DB78DB6DB7906DB65B6DB619E16DB6DD8B6DB6DB6DB6DF1DB793E2DFE4383E23C0E4F88F832C9220F8BDFF166DEBE066D72560DE087ABA3FD91F643BEECB27705A943DC3B2F50DEE55EE35E9867574361CBEB81085D56D1F5207EA520B02FA61130BAE772D0ED65DBA30DC494F6687A20D16DD19C312126D9B78D6C30EE096A9063C7A8A7D4BAB2E8C3ABEA1B3635E858F7B0AE98D10BB7ABA1249A47A4AE9844B62CC6CEDBC0C36DB1C1C111C0CB0F0B6CB6F26F80DB61E5E7A71ED2DB6DB6DBC6DB6F23F09F29E27C85BF09C1E07C87C6C47C9EE383C4F80F1FBE5720C43937E929EA53F7623C0DBFEA5278750F7BB362058962F03BED9EFB830B02D08994BC50413494F635A8D585DA6E9E84BD32D84EC377F56FDB27A91A7B5D31259A97424FD97B584DBA24252DEAF4C97A1F70CD8F7DDE938C97FA414CBDBBC63EE72EC8F584FABD7FDB4ECF6465D5A6DB77D416609B169DC86D9EA7A861E06D8622DF2493E36DB6DB618845965B65B6DB6DE772DB6DB79D8E37CCE379DF837C7DFF0031C1E0727F29E19F16F0787DF3BC1E078BC6DDB83E0176EE0920FB61FCB3BB568B35BE8213EE3D752E3691D5A5D585EFABFECB093FCA7AF7016A02E961A1600D8A65DA745C83BCDCEA5D764BF519924BDF706059C3A41984FACC90FC219A74CF53EF65E86FD2CFBBD467A258892D4F5041DEECE23C16F733DCB087EC8008F6096088FCB3F2117A575B0FBB17ABB226DB0C720A8E2E26BE70379D86DB6DB65B78DE36DB6DE762DB782DF0DF03E7DF2DE37E2DF84F03E6DB63C16D88823C8F90F062F5C1F099D6DBB06DF821B61EB6D8CD9DE5DE75C31F56B0D9B2616BEE38C5F506B765896A2FB4B924451099A861DF775964D958FAB20167FDED129077EAC03A965BA82F4702B013A37F91D3B816D18B54413FA41F6470BEB238A77DDAF6C88EEC9D20FA43C3DDEA2EE923A90291F5EAEC24A12633082B234EEF6DA7D465D5870608585B61958DFA849C6CBB6DB78EEEEDB621B78DCB6DB6DB6DB6DB6DE76DF33E2DF23F8CFE03923F8B6780F1FBF8339CE7393C4F1CB3938C1AC8EDB65CF52E2F7030247BBB776FD584A605F7748FC4EBDB76D8E7763D32EFABA24872C1B2637D4BCEECD74823A41BA16816EAFA94088C5833744A4DFE0247965D13F9C053BC814AC843746FEC1FB7FF006D27D831A631E92E8777F8AB6F6BE84BAECB21D8758DF991987A846C0EADBB06DD5D5851B6F57504FDA5A09783186D5A81EF2D1EAD68840493BFD4E0F529DC17DC07593EFB7F4597707D4A8B431C71F02DBDF80F3B6DB6DB6FF1047CA5BFD27C2781C9F1E71967059CEC781C1F17ABDFC05D5BE5B91164DE12CDFB840EE70EF1DFC849F92FC9D6D3D897E96BEE202D092BC7470A96ED870D82EA33FC911EDBAA4E616BD6F7627D9043B156DB143A9203EAE8622FF4E0B8CFEB0018410407B2C9511D196F79661A4BEC7BBD34F70C4108C1D764FB3ED913A6F5D5ACF6EC7718908E9BEE2C7DDF6703EAF6DB63A9476B4C875097A86BDCAC5E30FF0090EF0CC973D49A773592EC771EAD2F440184D80F01CEDBE5BF09F06F070447FE07AF32DF80F0393F876DB7C0F94F3DF873C10DCD8D99DE3EAC245EEDFA936CCBB5D7A4F7191DBB3FAC7AB725BAF45BB3D324CEAD3B911D2C384D79621538E8BA8D8FC40EF0C32F7AF49C1A48BD9761ACA3617B5EC6134C1BEFD40B9443991BA498EE59C237327A491818074F5C436210FA5A8FB0846DB63A7ABBBD70F7064E4012F738F4C13C1F6433F487574FB8E3A71A76BEC4126CABA864C430BD48184991744BB1C1F06FCE739E24789E27F2E7C47C0799E47C0C3C6591E671BF3E7C4B7B4677A9759772E4C1EFC01621B7FB3EAED9E8CFBBEE3564B9EAC6ECD84A1767131ED8308F44BC3DC9BA80884A617EBDC9A16590C5845134621F5853DC83098859DBBA043B7DA637A9773E9845C0CBA3E907ECD9FE3A907FEC99C61ECFA8FB7ADB1A4FACE0F796E30E7643A777B46C041C607BB0C31C25F97BB1043093EE4D74BB1C6375BBF47161EEEC40FAE1B725E3A2961888F13C0F84F1CE5F33F91B7F8B3E13C0E4F99E086D2DBAB6DB638383C0F0DF03BF139DF1EFA55EE5E0133677C11DD8664A0C3B97630EEDEE461D30870E17D12E1C1DB25B96AD5EA260B1533FF2CA02251A6D3A852B0D604FF163A0B5C53BF508477249D44A3777FCA1C0B5BBF922640D692362D201EE7F18289D8C6C09D194991D757D5FF2FD43B11EEC2D88E0851D38C9EA74F5621DDB97E1B44BAF0F18B0FDBDA78F77A751FA911204FA71E063BB1E73E12CF1CE4F03CCB63FB36DFE13C0E4F9F2CB24E56D8E01C91E1B2DB1AC4799F02331B65B6CB1C05BB59C1EBDCBB3361FD92494F49056F5DCBD6DA0FF006076601F509A495665F6218E987ED74436EFE1901C5D3D4FB7D909CD3411C4AFB8075014FB49F89019EE47A60E91F77696F54D01EAC889244EBD4249DA0A6D873A5D68EDF4BD583842FA973A87A3D58C2455C271D7048CC11C1C866328F5767A9C7DB66392B99E0C67B887BF533EADE585A773D331D66C7471B677BF5659F09CBC75F01F3EFF00511F31E072786DB6DB6F1B6DB6DB6DB106CE16DB6711C1E3B6CF711E05BF117B376DC6596CC0BB876C17B9FC5EEE92C751AB6FD138B5EE3F5B362113F5202456BD320ACBC15749DFB85F4B6324A632A1A801900C4640AE58911A8E7593ED1C76C92AFAA5F6BB3190D49FB7DCA79E967B6D9607FD975C7D5D56F7166B968CECEED987B67DEF0196C60CF039C8BDC6241EC9ECEFC1BB88725D83F64271ED91BDC70D351317606196593C6799CFB98FE4639DF9CF88F03E33C0E4F0CF01E761F0CE37831783811C6F86DBC9CFBE37C0F2EF997DAF707577EA4C9EEC0F7756FD17A2FF006DBA1758EEC832DC8EE3BE172F77FC98AAD18D2EBD643DC63EAC1238DE586201050B23387768C94C8BEE0C83DA4FAB708901EE1EEB7A7B4A4172DAF4931EA6EADD418C63D5EEEC4838CF6DB7BF715C2466CE73AE33C0719059E985BBE0F3BC6AB1EA333653D587A8C36EDD430809DCE75C177C36C632679113F0EFC47CA7C47CD9F19E078E781659C3C1E1BE4410B323C724E0208F2F7F036CCB18560461DF1B86B28D9077772AC197A9EDE07DAFD59ACFF9C2CCBDDEF8D89D7700EE46612C012A957635E0EB61242C91751E983A13A52CDAD88E928EECEE03A5961B6757ACC0BA92407210F597B05A7527A2CF62EC04FA366BD489D59B26C23A5F6131DA432383BE3DF81C8A5B7AB31C6D7A9327C1E060109D1063D30E45A8334E17ADB36631D4559B65F5649F1ED9FC1B6FC67C2789E27CC781C9E7B6DBE1B2C783C8C3E03CB393CB27AF02385C27B6D85B86CBB32DBAF6F077664A641B1D59FB664112EB1DF5692B5BEAF77A960083804DB076CD86300B31884361969196033419203BBE85B3FB74F503A6438E212C2EB3EE557BB361F776630716186F7C0F4FD5EDC898EEDD0D875071E8707196786C2B6FE237E891F6B3E06A4CE98F774F5C1ADF93FB0EC21E65FADF9B4EF8FF000DB6F01B7AB576F877E4CF9CF98F8CF90F03C36DF8B6D8B7879783C42DF0CB3E4D8BDDB908EE7EA419DB76E8BA0B725D827D611DC197B9230EDB5F76C65A122FAB308496CDE3B2D91920920491813DDA9E8BDBBB53608BDB223D434CB319E5A083DDE8B76665E0BA87E909777A18206DD1D33DBB8C97A9760474B76C063C08377218756A4413DF99CA94BC6D9670C3DCFEE1B7B4AAE48B45AE96B1BF530C18C9DEF01DBBE0AE0E859B1AB13C77E1CF03C0B39CF1DF13F84FE33C0F3CE1F80E09E33878251C9E072781E1BC1C07B48BD4B6EE5DBEE7FC91BEEFABBB73AB1913C2BD5B1DB685DE596E36907DCD96F5DF38B61ED6A9B277D8B36CC6D7A8EC590ADFF65D4BEF80369A63FCB3327567A981747233B6C0F5C6599EA6EC58E89B871964EA51DD93D832B74D24EB7838386C9F0F7C9C3EB6EDDB0307DD9DD9B37D88FA45DA30F729EAF7A0464D93F634702382CD621D60651EA51F09E391E3BCBE39E473BFC67F11E470CF93E278044C8B39CF0DBDF1BC6F9687BE134FAB66DB76C60FB60DF501B26C99C6076CF71EA1CBACD9E96DBB1DC756FDDB2CC1AC843B965D8363F73EB20F44F2AC9798C1311BD332306CB1B360D8E92D771D2DADD91FA2FD6C1BA33D34B223F42FFB659F520F6B46931E2ED37A31983DA3AB6D3D122DD8DE76FADB62D9F3F73F92EA2330E9B4B90A1013208466419DDAC0EA17BBAC043809DCEDC4624DF57FB917D2812CB2CB38CF84FF00C13CBD7CDBE078659E1BE2721E259110471BC1F19C1AC25A4B976DC875BFEC63D5AC646FB2266F5DF01DF73DF0B93DF0475C3CFBB727BE1208EA5B5EEDD6CE53880461B290A18DB1F50773825A631FD717646859A6DD9B3A6CFD4A78AAD5AF4CB9EB8F5C77F503DB684EB783732CDE2190D8F5164C25BEE5EBABEB848F0784F1DC30833DC199641CDBEE9EAD93656CC1BD420B595B8E33D4B6378CD2E8D8CDBA0D986B0750EE1134C4F84E4FE13C73E2392DFE23C36DB6DB6DE5F2D86DB786F1B0C421A87E13CF3E92ADB6DB65997FDB3823A6EF32C0F7692EC71991CBDC19E1BDC1B25EB8196B11047FB19D11B91747BBB1C6C8E08E06A0DF7083600C8EAC82C832352097BD89EDB763B89EAD364FB861B07EC81B7EB8768CCEB83ED8611C10336F7879F72647C6BBC03EA7BEAD0EAF42DBAC390EA17DDB843B60E3019238C396967ECC325D4BEACDF7C7A8EE3AF726DA4DE0DB6DF90FFD4FA8F0DB6DE76DB6DB7E0D87874861D8F1D249F7C8D876C463094BAF3DAE9EEDF0381977AB71E3EA1B6E191DCBF5C6C7EC3AF7759C36771D5EE48E02CC2DEAEB3F4263D4DF569D21D4BA65DED742FF0057DCC25EE7C06CE0C21EB2F5EF8CD3A93B8C9B67731ECB27A977D5B4C867B9C2CCECE020FDBFD8638DE5CE0F77A47AB65E73C1605F5225A599C8CD921388E7B9F52D91ADB92118F57DBC64C6B812438E430ACE0B72C6CFB8858CFC1B6FC3BCEF81E5DFCFBE59F2BCFD470F0FC2F91C6DB6B0C3243B6DB6CA48B791324BB92F1EA0251C6F7CE75C1C1EE064C45BC2C7AE476099988963874BD2FA94FBB3EE3B2F74B4EE03A5DB10EEFBCBA859EFBBD67DDF5C671E9389EC9EB8036CBF6065F5C0709D44CD83AE3ED7D5B1EA23C1EBBE09E3D3C044C1C9D1B7D4F95C63F73DEA50D268EAF49BDAF592916C7B877C378339E9E058C6CB26EC593E07F37AF0DF84F208F17CFF00FFC400281001000202020202020203010101000000010011213141511061718191A1B1C120D1F0E1F130FFDA0008010100013F1075E4798C4791D7808CDA31CF86D120CC6F1A414F84ACC59A6659A8DA39470455103B967771729DB2E1B821A6588B5146573BA0BA9C2990B806F52CAB5EE7B57E65798F489133B9C2B2F78C148D5443F119D3329643797DE62A6986EBF8870FEA5E227333BF16D2036CCA183994306B7302B52B55C56CC9DCCA479ADF53BB85AFA86AA526B9605C118D5C79F819655430012A1E628C7732C78D3F9865B814FB9D0FD4132DCB5312F712F472A380C2E44BE31030CE7B312CDBFB8D7570A72AB9C0C413C25AC0B31345C632F30A1BD4D8A8854B8DF11DA5DD91D1C401803D232DA7F32F711DA82AED94A3898EE20A339DCCCE72F0C2C932CBB7EE0356EA50BD4AAE18C8FEE60099CE214205B12A9999EA706A1A2B5F32B77F52C01B612B544A85B11118C6E7DCBD37D855AB5EA0D0BB9405591117999411A05A8D429F770ADAAD860D42F694E9326F44F46221215CAD512CB44A2D7085B33AB50961CC2DCC2BE918C5A9C86E096E3E91B33825A6501CA5E371BBB9EE859A8CB13925AC2E198EB2B4F2060C428845A8B11462D60C1CF9328188420431084081502042078386A0780B81021086E3AF23AF0DA6939784896434F824DBC1F2751DC6388FCCD255CEDC472C60F0518BCCBA5E15E7C26CBF5A8BDE22CCCA38DCCA5862203736BF714649571038544055433CC671E22AE60620CC39D53308A23AD46D6E5A910967173222E8833E08DA0D3078994A574CE1BCF852D0554DEDFDC20FB88EA64DA54B99901A97E656E77067A82E194A814CAB7DCCB02C7CF52A80BA986C89DC11464967753935EA5D9E22564CC47A2E36C663B2ED62A0F096F113188A0C450468AA735952CB6C596E0C4B066A3195DC471F316C6E2ED2AD6A3BDBE6329A94EB9982EE1485BAFCCD4EE2F3A94665EDCA64E6E612ED46581D45BB8937A8604013BF899E51408D6A504E7512E1519076C703E006F3FF002404D46C7B8B625EC5CCB1412B4E60998AB8AB1772B1251A3430DB58D5412E145CB78B83A7302EEA20E0946A555351182ED83764C4CC4B372A6730E2EB12D5F10CDB195CDC4E1967E653506A5A98C4609630C652753EC46DDA6A828A60E5BC7125F8980D4B1E0CB987C46A5C6325511164A2C205C0C4DB32AD840B210204081E020CCB21049033387810F0AB8150DC370DC75E475E4D20A8918B515B3940861F024685C441E0C68C12314801F0160C25D154738E198D35364CD3298712C0F81586658CC7BA0C4A88DFC5F58D788D388ED06CE6545C2F5315045B6D4425FEE376F73520794457D47EA81510622F85CC33C42AF996081AF7100111D5CCC211BCCE06E2A658D5966673DC42EE182F50070BEE0C6BF5326D205E60AD9737EA1BE227516FD42CDA4B31C1029280D1530333F2CAE6072A69944713D31A17FD4F56266990C4A3A6A182070804D786E2A8A82D05932C307DC75A963A99B76E6564168D5C0D914661A14541FE28C6DDC556F31F0F538A2FD7B9636115E198B5E986C91C39FB993D4BAAE5814DC4AC660DBDCB8236C7E621977C4C1C910D4DC4A52FB86ED493203F897EBF112DE0E60F9C4B5D40E1F5E0C88CC3BA987D400EE242C252F88B854D062586C9498845648910F1B972882A8CB11F7DCBB33302E662187B9A750192166989DC0B231CB5519D5CA171896088867331A524BA658730D4632471CC298605CB7E207C407C4C72C50B01098848EA59E27EB2B997105DF89C3C459F0AB2078209061502E042041020810F0102043C079086E1B8EBC36F048199781B46206E0AF0ACC15328E32998A0CC49CBC12E38E626263E14E631476C7C18EE6DEA200666802D516CBA82B2E458710A1078941197D20BB20F5386A1705B1CB6987B25471B943704CB5006EEAA0959C44566A50BB2517B894CCA4A6980351C3B946D83FED2A4AC4FF683ED170AB9945E23A2F89F9114DC7A6A577005D59E0C7E216F8A961DCACCEA38655F130419A8335B8077167136471F99AEB102F129C40A31899CB42662393504334BD2E60441116F119BB87837F50234A251755117B857A983289EECC20BE23AA5FA898EE07B980D6E2B8DAE2E2EF3982D55732CBFB9A1388BD6BDCCD99A810BB8D444616F480DB079371677EA08A1D41B89987D4E52AA5F8A7B85CA6E366AE18571306196CFF69771717CEA34E6178A004B327C6EE62CE2776BB9466E29D4630B514F2DF104EB28CE4B0855DB12EF68EA93714D3708C371065863166270A965730BB2E2D1D10AF3090A6614D498B11AB72C2F519A11359CC52B30A23E92F6500F0C8D43A711C193F04BC23451A265CC62EA095BF1A399E92C60C76DDCCD30840AA286F1E2A10810780F004AB81021084D7826BC0DC7519B78328895184A225FF0102998A3445816C734AFC0EA339B33065592A8EA2A8AE28F5E0269137033988AAC6236A2FB8218A898EC7F31BD133A96401C479D46B326ABC689B26130E4C4319494730E6E22151761989270B360CBE6C9A8334202978F5D42F49C46FBF1AC49461C9CC6AB1504E4427AFAF0EFA3009528B863D614D42ED4455B27335000752B1BC4C51DC2FEA1FF0048F5CFB99C108E13E0C404F4DC74D431CEE3C05EA1194C182025B32CEA24C6659CCCA4680696666A3ED8BC4C92F5029B658C01448A0D4549633889A788726E592548713D65355136CB72FD47285A36B541EC965A08F6870AD4C91BD4A46D816FA8E8D216EE594D4C9B89ADCC77A836D4A6E7BE2DEE2F6CD4B5D470D4F712B088504BDB1E63D666D7DCA20F68D5DFD457F4966E58AA063FD469DEA362AB68822C54E5B32036C755C6EE0A8E62413825934C30263DCCCCD9285C778846ECCDCC45C0A626E533160A994C216A01325DCC92D656AFC3632885D86106FC33AF10949E1459CF8084B8B315C5E2372A05421E0F03E0DF91B8EA3A8EFC16632EE348989F2CCB8A6389F12CCA541DA15592CDC41C1147B8C7BF0D110859A47F3308B157147C13E07D3C2B71B940EE2B2FF10917188E49DA5A546DC4C13175592DE351AA1B918DE259807DC686788018499B663B94151C81B9517A9A0B88607EE10D7DCC8E6A2855AEA5FF00532333220D40BB0F1001752B9401CA153552CAAD457FF6655D93B543095846F5351F68891765B0A6DD54284308D5473D251A600BAB858C42FC620862A73C002D8811A7151B7C4CBDDC77E254EA54EA0DE2590C45B69142091025BA957B4E3A8DAA98B52F62C5798EF13322F5751B245388D78B96F8258D5592F5983C730A7C4CDD457570CF30AFCCC6265E481FF9062F983D3E26AD588DE122A7198DCDE25D3715C4B0370B22007315AF1073899AD906665C663F7C4C5DC35CCA06F89BAAA297A9935CC74E75188D9982E932DB2C0968D4C2544C44097A8F2965C0C0758EE3AC404F0EFB92E18954941387F885BDC29716B98E331267EA169706F1041B8CF0102E277784F782EE2457B961C4BB86E196082B8752DE49986E2CCDE10CC21060DC729964BF03E43E07FC06E3A8C7739782E25D7816E2CA2245142841B7C142A4292D22C4D462B96C31C4D249FDCE67DC399B40A44AB8D2317B8B181231671004C9B94E183B298CD44CB2186A6167388A9683A5DC24FB8B6C0572B625379B7318DA5CBA9811E619782FBCC5B6220B598D40E7DCEE0C44E616F734351A6A28DDE20C2CC4079FA9413040472C1E9840FF00536CC27FC181A422011CD2BDC1DE351AF51F485B8809551891B87D27D8401CC031CC67588F8555CC484EA0EC4EBFC23DEA0813F70C1552CAE3A4EA44DB1896C1E861A46F42A31C5C58CCB09668C4A17D46B6FEA58D4C9D66155D789EB9DE477C46AD19952EC96863D621CA7168966397A8A4A3528998E5A9EBA889C46EB798ABBE6565BC4BADEE2D7FA83BBC4AB205C14CEE724D20A38841DC7526209827D396B388D2096EE0187982C2A1496A265ABF12F64F981A71314719B8944DDCB9EE709189515D8E2140B25E0188DCA4C393F30846A513F0975324B26254577053DB2E5780A99992663B265A818841C42AD730BC76E6658B1B5F1EC86A59E4C2BC08952B302E6F061E2E28A38BC4BC6A5C1F0307C1A9A408EA31DFF8062C629CA3AF06306A5C1465D45F05C475B9F92241AA95665D565829A7C3AE61883132F883D5912DC468BA8F16365432E5863D4C971316232B2515588C6887D42FA8D99908C330466EB1337DCBF04C76E25571B89A2F32FF00FC821A1F32A72945E09C36E65626409539CC0B733CF32A6F709484ACB296CF9E9CC8F106B51AC399419FFC626DA842C3789693021CCCACF0316A26E358882B303B637251EA0B5C6708C156C122779D0507B89379A5323981151B0D41B81552CD6A6C7CA76BF115A4380544375880F9205921529A7C4447843F18864406B1102BF516D61EEA35E3F70AE351BF03F52CC041AE4D42AEA64C98838A22532430D43BD7EA62619E484F19A850D55CAFC1D405C738D46F5156713B32CAA44C3537BE16CA19A97C5CC7C92A99C4C36DDC3F8F330AE650DE49850BCCC445BB97B22C666133AF7015FD44D737DCBE8C559EEB618C5EA70E2A5DB818E060254A3D4B00FEA0821773003A816854A41ACEA58D24352018E353126F9978BD54C3B8A57F3E2AE2470E25E63D4AEB3509B204798D4A98433994A2117F90DC17E25EA15856732D8D66E1685C840425CBF05159E07141C4BF0307C86FC1A86A3A8C77FE0348C6298C7118B14B2A1941C7874C729846769753165180EA5769F521ACF12FB0CCB3894F129EA1C91A3D4A2EB30ABE0298DA24275550A854A95C4A3A8ED061D1373BA510AD913297CAB5061666164A25DC3086670B1D57F1111999B99DEC914D9FA89432D5896CAAA5D3BAEE24588292C5900A1C3DA1943247A3169DC3E510D44D45232B8B23017D4A738662CC97A980A33E1B9324170B26A7B331A04A9710A657296881018CBF30188B6347B8DDDCDB01F3D4CC82A4D0E07B63B01E4A98DAB86043340B07DD4B609C529EFD4CC7218817027E61B790507656CB8B563C86CF421D0A25620C5D402A901461D505C4D1944BAAA0D64182B0401A817A94604E4404C1101823AFC41CA003DCCD615B44E285788DA523C337AA9FBC42F11EF12F552CFA8B606A02959D242F44607137D6625CFEA17CF306B72F2F71F5E235A6577B82E603D4A9946DC4B94752828AA8DCC6276E660BC4C9865CEEE1773498B24C3866FB81A3061D12894EE531C8CDC5389BB8F388172CB51CB14BA8D1C471398E712B00A992F898EA653218690CA17CCADA9530BB94F31CF70BF8883C708FA94B83A82086A3A99455B8C6F2A94F1C232F30843C179130F0661BF00A821A867C1A8EBC1DFF008831895E1A78728A2D30A30C232E714F218EE3D60D4B6258EC6AD4A50D55101CC746702121482BC66681F64AF66FD44BCB7CC1A7B808ACCCA044BA65F9E25C7A9930CBD85A26A0544357198961A4B5C389624CC4215897AE670D7DC403C751A12B719205E37038CCB1508A96AE59A56228771CB7706CEDD4C9EE558DE25288B9B6B30292CB4AE11196BF31E755105DB986ECD452619418C3330848543FA431A8035CC57CD112BB8D3570C8771A5F84E5F2DD042A8251C8B9836E8AB5517C7508B62559B5808D28031F97339EA8529D3BAB65B9DE140B763DC3437855DE3F31B200A1CF03ED3D4682A6A010735EA5D4531E559C3F3154E41577D1EBEE5689917AFA8D86E1A44D3182C331478CC4055CA1B5452D545E11704B982ACC60E205ECDC3A2B711751275005542BDC17F10A0C55AE5AD401C4DA184A37786561CF998A12CB1954F5181103B732B7FB962F130E267C330EB1CCC28942F3F98F79C454FA6387BEC869994C3AA96A1832B3F52EEDF51CDB4FA82CCBCDEA57F50AA1577515771AE000EE511F961187700F24CCDCA04B3696FA8764A398AC987851552A85E38C6B0398A8F530977085FDE372CE99655CAE3FC14A1C42F35F0D0108C9E2BBC06595D4AA1460206194DE1681E35837372A040F01043C086BC09C21B810F2351D4771DCE5FE01DC77078336994C3C08A8891A43C7DA39E63624B8D66063E614ACC102A5BD1012020A6220FA9EC891A22FB63AEA2A7D45179A85398237048301D439DC07981577068C49B882A30DD6D2A5BB841A063A9A840F32B544DE73001AC4416AD1C100A0E62B87BF31471FB9425F248B78A8295A8562B5DC4D5FA8A6B4772F9597D534FA840B8836544BC9106954060B7D4B36E22FB62EABF70FF9223286A382E506620D4CED2CB0698DBDCC16E3C57065EB012F7EE54D778C696B9961A850D8DC4555F60AE600F21A3BCEF1151BD02A7F1710D00F757FB809842992B227093384B072BEBE12C8740E6219777A5EC815D830B56B1A844A03B840971A7D47185CB69EBD45A63694FE652EC7300D3F84139184FA750EE4096A9F51DB6533682504764B4C5F2199893A1A88EF88AF1083364C4E256388D6C6DC4708D258A8E8650F12DC11E1CC4A52BF12E9E91B0E599DDC36F706FF00B8B5880D621B0C11B91E9DC685B1DD5622F35A940F7102186A6ADC6CFE602FDCBC99959486E69B96106D70A3EA0541618B21B3E2186194F32D583EE5BD92A9CDFCC5CC4220199A4C33538EE3625529F886771B730C65A3B96C1117517FDC487715C70A419ECCC736F53820D55C18398BC186A28909A184E4FF0011520BC214197702040410204103C0D4210DC37E02BCBA8C771F01E0EE3B89067C1F05F2517315455C54588B44E6430C4EE8C65019531B32D648D3129588B5285315AE1BE655198841337E224A5CC6D98B1967508D87CC0C87F10855D314C610C16C47A081CB3386E647F5029E250B29F49617044AAD8AB75772D6A61D40433D04B0B74CC058E8E08ABFD474667A65194386A598BC9C42FCE619C30458F88B04CB8804A4E0AA9412E11330C07084D4B89DA16977B895A9C4C02578858C076B2D97069380938761F110D2E0AC83EA08008E096F0A4505B2FE293D47B257A18CC279288340715061915D038D71F64A65ACA2A0A6CFBB2346D8033E44D36BAF9D549C4C27499015330D6D6951D3D9A47E0764AA3C14527F5041DC1D89419F161D5B9238710D63ED143152115B235D2A56590F5091C13E24A75599B4DA2D437D46E5AFB95F105B0DC6E08237622DA245AD5CC1A66C62519AA9698D4B86AA627B8533B94A866D986AA101B33F15C4463AFC41759802EEE2D19DCB52031077925A163ACCDAD88CB52A3A98FF00B8C440FA841BCCBF32A829A6679869661B6598EE30CEE0D30ADDB14261532334C5243BA615CBA235984328A5C785E3A8CF983717EE0FA8D96BDC1D42F1E65141A8A1083E7C166BC0AE0C42E11A283E0505628B1E041F204083102A0435E40B81033E4DF8D3C1DC7717918B4F830F93223A8C48F827831D0269F11E0619211F48038990DC2B712CE229C625CCC32DE65A304C7CF2ED114469EE61C4553097788E1971EE09EE12546B292E62B324B43772916C4888399966BFF21BC5464C66202388328EF372A513B180B9771EB00AB304552E55D91DA8F54265DC6ADB0D8113963B8366A739B958300939AB101F84AD39F440A109AA521DC40675344B2DC410F71A5E3A7A24450E8DBF5012E36A8C711857EA26257D543B26B5D5563BB9EE630AEE06C1C18F1080CB8804334B759C93266D5CA3B86E26609E43E5E3D451CD86C625A28C22DC2688EAC0C761F631F1E8AAB85771DB3059DA6F34F0EFEA66BB98393EF99D2EC4993D5C06C28B1944B331D898800F0CD3AB65BA13210DCEC496B8402AA2F421915142C79A542019A8758269D4C5744A26BF510EC27C4B471F884701F51CD03E8836C961899E240BCA0DCC962A59646C2AE60DCFAA6BBFCCB5DD4A482E6F729A967DCABFFD83847F512BDC55F33299BBE62D8E3302F33486CCC15F5092F13160A554B0F099CC1310C7FD4E04A128D66A5DDE15BB8917A33B98D6232E1EE2B706CB21F84BD9FD42062BF88A7A79042035027801FB81040F80994106A669E88560E51E0596F84502E0D4D21021145E06040F01503C0D43C9BF0EBC1DC7C5631891C410C1E0998F89878077E4AB8AE92B83109751628C505BDCCB10B3984B500289DF045CC1C1923B8115F28A37285956D7510F64771C31351688AC7F33F28DBE62EA08061254BB7036B9732C750D364E0333809699572CC4D6085378866BFC457096F421374C33D7E62994B525414F89A14E61B7446E446D5FB977A9538998F702FF00B454850C663884C45BC4BB1CF731E6635352B37A32BAA9414B198FAB36FD4A3B9B61A3D38AAEA02536507F2C7DBE05E0C5DD8A27FA8C2C81C334F292A61DD0EF8096AC68031B805BF7B8A6C14D91AEC1191C567E9BDC6F131C2376770CC837830AC7245CC0AE7DF64C4D1E4B8F9258C30D3B3E2230EC4C2CEF10EB50B2C1E96588D0D8D56FA862706841C5FF00B880040A6413EA133D6496E4145495A4D4E8625CA35358CC2DC458D45DB129724B9B3513208993705C25596E55565B31D54AFF00A4484AF328546C82C82A1A57A80B869EBB9D717A83071023FEA61EFE614DC67E225115FF00C45BD4C9B0947B895A2238AD4D0865F728E3733E229229C434F538C76D732B6663C99943B975453899526E608730C6601D4C4C4AB58E2E2899B396282096F0BE17F13589798BFF00D9D8CC2E0AFCC570E7505405820C42D344531C4A3980DC06E60878040F529FF0A2C9F0A8650ACA3C0416879A104101040CC4830F1B791AF078DBC3A891DC77FE013C3E5505C482A2B89712A086A17850030831858C32C2B80B95989508836CCB2E295449DDCF980AC4AEE52D1771224DB11C3E2E6E6DB8E7116351C116BA86EF51A88EF3137B72CB5CA609479CC2F2F12C63A352B5884CC19B2520AD4E6731D49CC588851016EE1BD65894C45730C63292B26B1B8E09751272CE668D5429409C4100650BEA2AD4B5C51B1EBB62E017C19D1BCFA3D1570A16C4A8DF19BC04A031BB0471688A177806DB96ACCBD3532E867A41B5CAD52AEFE79C43FC19A8379D8F32A3482C59598055E834E1989962326976F144DD4A86CBCBC1A9D083B83D7D4B65C62D0D38E2E54866DB95C195FB0BE495A4453907D43F0E28CF1AFA94AB0713138881B01764B5BCE12FB07D54D257E145571C41986AE556BFDF301C90116574F512B2955B817A994BB956A3D1CCC69528B653A4B2C3B95895E02604A0DCDACB2F50A543932ACD478C54FA816B10C4DD3DF98EDE65738808D4CAF129BEA265E631AF2044BE6E53962BA82BA9689437DC4AD6230F4D3D4DFB66F703F08C95B7F70367F103B9B4CD2683100E6ED89654DC639891D8E2B5A97355057CF86366E569BC40FF00ECC5C03799558962010CF88B965661437125B2B97CC67C330F243D664D4BE5102F7728C780D83CC4331FF0499C24F04824820F10920F02081021E03C0F0EBC1DC66D1CC75E18BE06691DC5F02F82DF9156A2420F8291F05128CCB9A84A25230468885F505BE5809AAF99BF3BD51473E308B3E065026AE6EEE6D1E222276E6597A8E58D0C6E0F0B8B544552B8EA22AAE372D5C38C31C437B88547DA22BD441FFB102AD452B480FC7706A5CE031B0199DB89AB18807E6655E268434021A7389B5CB02224202162E012D730E8CCAA5C2A365EDA528AA7BA28EAEE04AF452FCA795EE5B417C00D6EA26154B1DDBE398219E2C156BFEA559EB8BED8ED96A12C5CD0F6CB7F86155BF3009B8302E5EA02D22CC2BDD46E233AB6CD5FBDC10A83CF5EE69D6106A31AC11E2A615EA2E9F7AF9A8D39A146A33C8AE6123544CDF43A9BCD1BEE0534AE590482AD8B60ECF889C3700194EC7B3F702105A9D9F111C70A161ECE420EB21428FA174911A59D663B498CC95FAB83C56EA0FF004853A9D06E18FAD32E2046993815E7DEA0B4112C488181888F0C6A1598F4DCADB64A62177116AA3731465FEA54432A21F044AB99DA94784F9ADC6A6C952D6A64B33331298ECE98D32CC31AEA297A833A881956B4CC3E629CB50B0F128B261BC446A081C6A65F5D4B28890A957FE4A6B189A7FB8D3E650EE7BB532E1C441CB1562A2E6039845300DC05DC79A371895109CAE0D5CD8CC99A7A8B0712B8F006E571E0C1E2E4944C70510E7A9491A27C20E11826297D4B7C04555154C92C2650F029840786484610F110102578081E010815E0F03E1D4D23B8CDA31D475162F8145994515F83162C58541BDCB22C4B3C0A63CC4D415C1456F705B8F28F71C57007C4B9DCDBC89F53A473118B9EA3EA22CC3517B88603113D44A23C63112A0EA019651B238175E01DA1C15948847A828E22C69312A6C94FFC5409B4BB485ED0B6F3330D54C0A59990A94BA84E22957B8A2BC4A0840AC350717CF8CD943135EA17F479F4C29E05C4CD018292A5A0F455B3152D3BA406779BF7175332383953D3D7A80E0D33B1BCAB17304E00875F9DFDCAE172237F15B9B32408CBDAB963D3285D1EDDCA0012DD3F0F5340C40DBD5F8FEA34F23D8FB7FF00250A054DF22AF5AFDC65A33E12B0F4D2B01317110F99ADDFE6066A4555605F78BABF504A87906EC99D55B253246BAE9E2AC4EE0AC5C398BE93A80C24D9858CD55B49B1F5060D7A5B3DD73EE2A3311687D7E621C8B58DFC30AF44D7A8EF08039808090474EF35DE227B35F694BBFF00B8802997B979EAD35994ACAC81B1258C012A5A667184A993C26B966C829A8D5925D9AF02B29351B833172A430859B18152D34D91519962B331622F7096A3662C15375B88BD305C36F89AD1896EEE3A62E2A662FC5C01FEE044C4BB06B99434CC0842C7151A11B12D3106FC58B55DCBDCCB651529F89FA3B9752CAD98884816C54DD1D62567041E25C6AE21328A1986E0B2F983C1DFE3BFCADBB25292B85C9663C863E2CA4BE51E0B63D61485081021140B8102084212ACF0102A04A810F00A8791F06FC3AF0771F0631D47C146314D22F86304A834C539CC3987318A62CEE168D8D468E20BC546524630819B44E667182FC1DF739462D9530F98B16E30662E917F114BA8DF5169991153EA5C55B0DD7F9817DC70C4B9772D696045C1D5D4A5B4C088E440ACC0640DCACB3306CE22389571982F1129A8A0D40A704C030CAEA1A86FFA820E0851D4BF9425B0A83D558AD2AD03F97D0C1E2349C2D038372E5A8E15C87C430AC159FCAEAFA23293A9340FABDC066A4E438ACE9A4611FB7E2054322AE5FCC73510A0D3F6C0A507922B93B4EA200AF44EC35FC4393EB07BF987AA0019AF12ECEDC147FD4C3B0DAA2C307AA082B39DA55957635FC4B221BA01798FCA2E4306888BA8419B38B9A067861C0474C31021B910D5E292D88766CBAB78B9563AEB92EA9DF7A8A4A7507613FEC40D81BED5EFEE58105435CC3EA5AD783ABF896AB25EDCEE580249BD62BF4BFC9162103154E2DE19A6080A8F447C63C1025F2E3DE21E6CC7B5115BB3117118F115D90BDA626A2624618D46847796918DBC4AEB71520C4E52CB6258B1A3EA531CAF9943897DA55C6194E5973A9F6F98DBFD9363F71549AECCC1A70C03DCA3B88E222EB312F8C46943C468CC455B8610D7A86366E7567E489F7347F3E2182C3525EF9A8B4660A867A04BDD472898A8ED0C5437DC31055771D7A9BBCB336A65312E99B11A0DC2615F89552C98EA52EA7C6671A5941F7063C4CC10C619C270211503C040842108148150DC3C540A8103C1FE4351D783B8F8318CDBC1FF00002318C5A22E25C6C5785866E1ED12464D65FF0030B9369808D630B188F9BC406C65AA3B63E18FB823F119B47DEA3DD453488473328B18A2ED992F7063133974CD3552DACB4CA09F1EA31684ABBB9B2AEC9436E2055418DED117B895789602EEE0FB4BED08A4488A8A060C625AC894CCABFB94911098AE333307C6B63807B58A00341896D3CAE3E2637228CC378ACC419E00ACEC4ACAC57E68AA6CDF700C5E239613503966E5472BBC8AF6B9C7A8B5A9B9769C63A7DCBC03B68F5051E04D280D83032CA38503DAEB8838A54B23E3FDC4C607778761C118966EAC5D5673571D761AC0623A588F46540F16E3E2E300D4CEA26065C9B870CAE8C6E669C238993A11A23CDF59ACC3125ECE48569787E618BF91E98EB781D5F3EA1944609A7B07515E30C41BE179EC841B826BEEBA49D044C1377FD4B13BBB1E340B54B1FC44D11D8D489DB325C6DCE3E5EA0A4224302F290E88B27766AB3E8A86B633580EC76B01B7F03057B1B31BCCA306AEE11737F12D5547907EA771041AA23260C5055C20B04C7100652614C52235F39971DC47D4B3998FD46BEE0BE264E67E92FB9A85C40D09DCC4715EE155388A583F12B33000C98F89C86AE0AA35C5A63BA62CEE1844F5B8BA968859BA9ED18BEB707BE7B859B711ABA657F711675394E25C5440F3296F9982E50627FF0058BB84ED08329743F89678F9E0FC415024550A485410CEE98205667A20BC4309550CD1054B2510A25408C08420C1CCC984DA1E0421E021BF06BC86DF1B78351F07719B47FC07A4631F06D04E5183E23E57512E7A78BA1CC76EE2A6E2496ADCB19583C48954C5312C0128B6C6F1CCDAC8EBC062A25EE3F310971BBD4DB71E65AEA2711DB5A8C151A8EB7167DC725C76BBF989AB8F68E1A8D36FD44BC5463BC46068E6583364B7172D9471994F89D4908B5333519CCC6AEE5235C475C66742556C807E25FD4218310F9DF8E5E3B158A25EB90AA856D02CB2D18366EE4B5C5771CCE8DAF17639F5029E23451DAABA5EB88C2D66B7FC6359B4482BB6CA21A57DB4AB5A6DFF0050F985780AE1DFF6CA0955EC3B0705C2573222C99E7B85AE4894A639E5D4A1CAD914C06F9C412FA36DA6743B750ED6820187BE3D4498DD4E0F01DC078277712D6B4BC0E98D935D9B10391D66F985C5A20E8AAAE1D6FB97E8B52AB4149F061C19D2735A855AA556ED29018C3FF3EE640C500D66F5E6D4F06FFD334B07B4795871009574AC36E9E1C67DC2E4BD438389548E1798719C4CE70ACC6C55ADF3051A1025F66CBA0963D9B5CB6DA172BE08A0AB2AECC1DB2A7924E819B1DACDF4C1E0D2270CDBEAD187F058284614FC4D4ADEE5C94A8852B999EA61F31ECB002E684064B303D40CD47458F9B510F88D7FCCB04C8D4CBA969A2A531370A2D7329501289EBAAEE0538CCD9C6A66CFE25D7B8971881DCCDFFECB0620B8E66495B9672CCD9CCC10ADEA06CD405469B665F30C61A0C4FC986788355C3286FF009F11BBD7103DD4B3E666A97188A1350490490A6310CFD4D72CF1E9C4BB73D10B62A1E92C6A0EA19CCE129440D4C62CC082046E425C638B108BB841F23C0C228AE0C32C2054086A04AF01A9707C6DE03C1A8F83B8CDA3AF231DC7C1D782D13223E0EC89705460F0097DB70045172FDCE5E6375512E386E5FB85846AF7022099E626CA833A89534F0DFC89369CA7E1308B30F0DC51E389DC7FE22BA8E6F88B88BEA3AF52D88DB534F6437EA341FCCC94E63B6252737F31B2FD4B5A8497C45CAECB9814CD4BC4E5BF31C5001994260EE5BB40C1DC214661D91F1B386DEA83B5C4419442F98C6D56596299A0A4E97FEE070506538800B802BD00AE25E279473E968B8DBB6A12AEE120A104DFC2FBE6F5515B079C02E9032B4182201BB4D2690E08B2C028A9CA4EA31A5746CAFE2082A90E64FA250880B4E45C217B350258C44015FDCC56DD49E8E7044CBC64AD430B5D6E1D52E21780D99B37F999FAF6C8437E87D4A288D950D634BECAD4CDD55964114725C61A8A5B12FDA93F71118088A3F7B3D9106909E4EB39EC894E1A38486DEAC960ECEFE1816496CBCFB0E4F88A89BD87039470CB1805D27681D671080426326A2578CA1FEE30F856D8388E52E33749D3F8CFDCA05F842F70ACE0DC4DD0BAF8F72A28BFA34C01845A5B3FDCA233698165FDFA4BED2CDC9C9DE6F706CF045C625BB2655C42C900B7550AA11A14B314D4B5DB0D61A8E3702EA52B64BEEA35B1FCC7854035B94BA99752FE3110C4A2F300BB9A96DE1949A61F702FF8898319899FDDC2DD4E7882BD4B2EA58E25ECA2E5B0A966E6051998BA86B8E639289D5E200E49C98FC4A38862CC184E50FC998E66F71172CFA099DA3F4F71CBBA96ACE4619A3F50E1FCC3182D625D3D172CAC4338942E7A615710308ED81A8A9F0DD98633312C2259087855463C45B0F043C0F010813086E6DE0437E0F06BFF00C0787C1DCE5E0FF883B8F9145C78318611CF0579843A8812B5089B946656D81A8EF330B992248A7FB96398DCAB97370E18CB45983C8C5EE6198E20DC7C1F7132CCBE67AEA25D4BDDC7136C44E63BB831771B0865898712DE332EDB8B98E91A83DAC8B7958E3BCC72E99670955E01693E9963CE21B656A7D4851DE62D118B43965ADB7EE05EF50E7BB820116AC632B945D1C0EB7130A341CBCD408884B6D8EAE07B8B9080EB972455A41557F300DD52819737711312AC31D15787EA179B252C25E20CE2DB0A9D3179620AACE899F780EFB43FECC0EBB0447B6F556689448852808E4A35CC032B2B5E06CB8CA0E2ACEF545BEA3CB58388B6B6F5F1114B69930315BCDAE25B3F10F010BB80568AE705FB85868FD5010AF05FE4831D515B1BBFBBDFB8B6080B7C203A253F984861665BCAFA94659CD9C6631E7355939075ECD4BA5AC309FBEA235511597CDC541D8580F22FF7A8DAB19C8278B3966D63E5847A7B852070B63F3B814DB8AD257731C3A55B65ED200EF5FE86345E710418EA99A300DE6EB30979A6290028340A89101B3BA5EEF2FCC4102EA76E0FF3F716EE01EA719E6AC25C4B989E65CEC843504314B86966A11A594545E6514B17519254CCCCEE553B9446F01618DC44DC26CE498EAA34DCB389ACB709719254CCA04C900FB946B11BF51B328BE6618D7316FF00B8BB950332B89D6666A04894F4865BFE224F055B9C6589D4E0817B85B534F705D4CB32CC908DCAC41618E258465AF8A823D27E1012649788AE3D4B0A84D57866D04EC81884101020540840810F037E041F03E2C8B0817E087837E1D783B8F83AF23328C7C6117831833E185CB1CC6918AF999B312D8E5097DCE389986A584B18DDD4C337172DC56CEDE0936B8B1348EA651896CDAE27D11498662B54A0C5A8E461C511C66695A8A76FD4E0B8F4C4445738834C6CE707B8ED2F0C3706A3C78F0A771061352E2862D53A96EBBCCB80BC410031C4186AB66A18BDCBECDF2315CD3EE2E139BEB88A864725B07E2240A2002E95E5BE21F3A55CCF58E632A262D7C9F9085978D5DCBA350C01746F31CD978656EC239586901B02EFF511A756019706B45F735213115775CFB8D39BC330D61E5CC7F22083602B9EBD4AF7381ED68CB531823F24C068FF0072895BAA75ADEBEA39CEE002D5FB624BE070669AB8486224122E8BCEFF0011DB1A222545BA36FD408BF536358AF699FC44E4EE50EB07A69C4A660684BB5A3EFF00DCA14CA0B01422C045F7296A22D58FC0B8F512E9C2E2BF47B894F68B29D85C6AAB616DF63D437D609631E2E528DB5BD7A9FED0F55291F633EDE2645ECA57ECC4BBA53522CA71E88CF01482B6AF4759FD406F04CB32AB2C0B98B36229F0DAD5E6E83D44D718B7460162EAB31279C28A40D3A1746E1B07B3A0E10E6CEF896E87ECB2CB70DC7F50E55D710D41632EB6A52352D2E54C65316DB9B222E38554A9732E6342821BBB8FA46858C569969732636CA4CFF10EBEA52EA2DDA47845BAA9738B661EA524ABB730167F31350C3D784DD463DC1B5C179D479AC43F116A5378DCC0932CBD77887AC2F9E23C6610B7550A571347B8B4C0C0C4E685A68E2160E614352FE09F14BEA6B9A56105225F88F838E331B30B5CCB035298451198F7E0A95920820410F01041010210F03736845073E06BFC0815FE63E0EE3E0EBFC06D1DC631D782F066DAF0AA9700C48CB1E730A4B98E483F319A638A2CA0AF1770372ABC28CA462E60E22CC1962F71C4BB3F28331D388C7B88AAFDCDC166625DC0648C73B983D916DCF13636FB88871144F513544A46A706A650D9AF8CC0B3F0865F1326C6A5D4BF19AE06299251EF70E41DBC4FF5EE581A034B55D01CAC4996DD066AB81C730ED030A1D7C5DF3058F4D81EF359821B36E255FDFC0710814948B17383825485B900D7C1B223EE063D1DC56AA2DBB001ABD7D43C81000465B5434712B24D5614D806326E578DB11CAEE87E2E36208C9CB698824494B828336FFD996BE7C9DC663B9CC359C7641003A46F3DFAC6A168ACA96767E0FDC12249688B67EDFB8833FDB6617657FB9C326F20151F9A853AEF09E97A2E75B6A2519893A1D457BF92235536F7C3D7516BC0BA4C0C7351B37908CD1D43D1ECB2FC8ACA539D658A780BC7611E662D7857D71303C345A1CA8FE14B9CBF82E1C5986828442A30700AFF00D8E21CBE93AA99D1C0C686F8261C2091022B37B954AB6D177F884B289CAD5DB8C750DB71100F5ECA225D0B005B3E9BD1D413A52E870002873D26153E1C2C63511D1AA883AF8146AB37FF00665F8119808B52825C388034454B5382A7AF32AE25655420C91DE2E346B72D86BBFC44254338801AD44623324AB35F8967150CC54BF24CFDCA56A648A913F115F08E1A85C2588ED6013FA9F2412FD90932865C54C1C4B56B1E1BAD54C330675999AD4DDE0A3E7D4C1A87ACC03883FD78971CCFAA7AA6084649690571082F1CD8410690D420818159512BC5C030408204A785339F0409B420C21E4F21AF2790F875E0EFC28EA3E0EFC18C6399AF869182E189B8A054AA664BB31CA67944BF137993E16BC4665C78306E24FD26D0E75127460815CB34A8998C4B8D943B894C7712A0B3EE02D8FEA64C6A3B3CCAFCFA82B3A833A7E63BC951AAFEA3A8E7EA65994817D5CDBF98B17571B2BB954BBEE2B690C0EF73323DA16F9829805327D5FDC21D21C818007F3F996536021C7B4F44CF3C44B63803D75320110C13800CD1CB2C115B6AA99FA845DB02DAB37D398065B6CB63A2FF00514F802F85DAB8FA855DB638E747E22A903435C71155E2B4A59398F155EA981EFE20A0292A7644602814FC5F7B86B9D40834383ABFF7148B2EA37F246E5B66538681E88B150BB293E3FB88C07826F21C81CC17E12CE4A3594AB88D954A2D13250322CE1E4E5C444DB5A6FE65B1471AA51A5B385AF8962F0C264DBDD2AFC4170349AB5ADDCAB9219F391162D2594D096291307541BC62D58519AC6C59DAD16805E22240F50A6882E8CDAD44516DE04F9372CF545E4C2471A211623F5293E45374E3B998C470682F1114C162F5E8BEE569EBCDF41F575715A8F1D456AB1F980771A15C0CFF00F215825A062D56FDEA68B63C5EEB7509548A8001452D8D83511D49B94A653C9755FA84CE822341575FC447A88EDAF0F35D415CAD6CAE9387C5A86E0BB5030665AC1328AC4A0D791093DC73899B509D42D8A9F820F046BD4CB11D455A8D595A372A2DEA2717335388EA4054C691CAE056A2691CAEE98F020B877174C4738658F713AD4B0B8F1AA97111DE23EB2C30542AE099752CAC4B7550B45E0947111752AF703D41972FDCB1A99AD9822F10654BD99752F35E1AEBC3F0984CBC27AF8E1E271E0104CD0E2083C1032FC2EE1084A812A1086BFC4783FC47F81C791D47FC0318E231CC1518C711C451208D5950A3AF05D0100479EA304E51C4CBB8B31D416C752D7348C48E2E3129BCD415E581E022459AAB63CC167A8C62511C5E22DDD440250DB052E20A0A73110D47710F94ABF51ABE5954FB96FBAB8B6DE2A2CC37DCE5CBC4E152C12C37A8FC0FB625E7FDD7FAF52EAFAC2AD0CABEABF32CA677810F9EE3F75CB1AC0542DA950B754E54FEA32A0B44519AEBE3FDCC008D8AAFA3FF0051906F02E8F135363095652EE32D44CAC9B57EB30286085F2E88C4188AC01FA7D9154D12E4E9FEA50A61487B8BFEEA6C2A80593AEB88D3A85A5BBFE51FA9FBFE0E77F7F5300BD12C0D07B8776A09AF4DD717B8102851E699498A321B41E7D19449E7E80C003C3BC310D5862B606D62B51DCED00148934E350B39E802D04387AE91ADBCCA29A76EF058752F19468A498E2EDEE26788F20555558D65F88E80AABEC38DA2D3FEA517E86D5D5039D398FE025D9CD1C8D2C1A2F61C55DABFA8D409517317DEFF005EA5DEE00B9653B691CC6002A2C55769CDCB4D720A398ED9A86D36A78316B166A33B80A5F06B05C001906397593EB88AB32A56028FCC40862AD94AFC888FC4C044C4C13EE2EC6D736054DE32545A77BA98DB5804FE2555482A62B0362D7C399637110E7FB8F7371AD511B8B82AF750CB2A50EB70D39615B442266566096D25978CC4EEA5AB241AA63D60953A973530DD545DB714239476C62304A89775732B752F88B8DE60965D54C7A8AE3A951552ABC7C4443E67322C265646C73311331516F1FE104CA5DC54B9D5CA4BA98304B5AAF1FB92BD5DCF478C7A8FC4AC96CE8F1D0780AA30E11F0532DCCA731F48520621B9C6206B12C9441065E268850F0DBC03084205F82108420F8210DF8310F06FC3AF07C728EBC19CA318C631FDF831629570289A269052C54E660C45B82E70C442658C67A460A8971C471348EEA08FC4FCA0CCFD20BB89D188FC4CA3A895CC6A55C44A77133E0E2E59D5C72E6211C666DDC4AD731D8CC16A2F71F598C0B98BEBE234E9CC4AD7528BF5DCEC7EA3A96CA5A4DB23592A2694ADEA523F8B5D7969AF8855F60D717C0D315301E9FF00308F9855DB03FDB5F4456EAA15057A06E0A951B5C79F5F894A374A6CABB7887E82B0CDEB1D4340228945E355304E06778CE07BC7C40F7CC42D5964993730C440D7647178E65BD8C20A797D3027AEA36FD710022892B2FAEA161217322CC3D2F532C47A74BAECDB151EB5175E0AADD17704034032F41A3A830002C32ED3B81FE53FCC13DFF2206427C28762FE65EC4AF404537916222102559554CA2F1F5081A0A25E115AC679959EEE25A68662CCD4BE029624BA6FAD45B64C80A05031641DFC219639205A697C367DC6B178F8261C2AC1580F5442A8EACFF445A1222D52D566B1B09B8475E005F0E73EA39A2181958C32914771DADD2A858116A570711F08085F0C3B84E4173A8E4FB890CF5816B96DF8203E1CE95E84E9B5FD4BCB732CA2DC4C895CD4562F518ABC8FBA7EE5736A94BDDAFEEFDC686486828CA9C69F9208B189753469E4FCCC932D799947CC7468042A561F78F89FF29CE22BA62000CB5B5974BD888570560CC02E5422025AA33144C610CC6225A32B6EA2AC971CAAA5F3A991CCA4A0A63ED98F21881AAFD426136C3310C5274222E54E2A04C54C3559953FB94355732F50C2564C8CCB18DCC9A8CD7F129235D93F3A620350A4C931351F5897284C4AE0B8359211326A12C1A8523D6A55B253E6DF823AC218F8097E0FA4F8C201E169E18670C4310CF81022621A8E09C4BCC2073145086E1E084B83E0DFF8040B7C1E0DF87C18CE51D783E46318C74CCA2C77197995A949896EE3699936EE56B505E259E22B6C639982C7998118C724DA2A9F71C311BEE3AA8EEB89A31CDC77A98463A8913D4151A6E3B8B399A54718985E22B1BB625C71888965C73F511B7A8D86BEA206B305B128B1DC4B7FF6392999925512948ED7410B65A14ADBC9C3D11420D258BBD20D877D8F9C72CCDE3200BB069E8FDD42BA2B38377B3EE29C035AE3B7CC6B7B52ABECBE5FF71025803C5FDB137BCA32974F4E3B81EE80B29776F778FA9B492B80AC2FA3B8ADE80B357DD12CD145719EEF1F04C3B0728EB8BB6B51D3CEE6133747E63C0396F6FD460CD1045280A6F31EBD009B7F9DCC34AD01A9968AC6798EEB5AE29903BD335CB8B0B5F9DAB363E370AE4D6EEBD730B90405DE7B7B5BD42D663ADB88DBBCC2CB437608544015A68218DC48560D076E235E9AED269F34B988604D4107668310A82985EC4C146A965B9F66EB2D0DF19B8FC56D2ECC8D682E636054183B9BCE0D9AA1237A3DEEF7770AA99A553813869E908021354B3061D987F130C640A6A887AE3EA04D166C13872B2B417C5C1F1722AB707F18EA31469A7D4BBF638E2595765E5FD4B4B3C07F03FB95AC78AA9F77A48480B1AA11474884ABF66A1D1F0B10174868B84FC532A959E2F4A16D55BB96DFA73A0160E701F88FA3982FC8FB524A46EB7B098BED8650273DDC0E74CB51AEE58181679D5457FA3F832E42192D7040501D06994B50F00AEBFD128509AE24145B711080765C25781394180B98666A685406D03F119CD5104EB33A552AA10575999804AB67E211CCBBDCA4D402D658E2638654B9CC2D7329F77C4C126BF50AD7374C8C2CD24A6FC29C42CFEE67D4AE60D44655000949ACCB5D4538851949F70315DA222E63AFBF1DA6A5109C92E25F3245582656A97364B0472CC6AC6D03528BF01077C79F9F9C12AE054A81040F1C38F0A283061083E081E03C040BF1A7F80DF875E0C6728EA3AF0E5E0C63199C48C62C6915158C331CCA97C0AAC09AB2EE2E648C0CC1F7044DF8253FD912FE20C44EE0EB246D6E6D1D4FC225CF9413E11A7989C78DC7044A896EA22A351A626198C6C958987198DF1317734F71CE6EA0B0FDC4A6346C8FF301816C3401B89F29AEDCAF2FC7312C700B01B3DD6FB8B060BD104C2DB777EB188221ECA0071F2C1ABD8D80B5F437C4C734D8CFE7AC4B42998E8AD36BC0E22D682042E594B4B9B1DDD9815D60AD1048769A16E8FCCA33E6C00259FA7F713DD7CBBD00F58FCB0825000F5AA196BA04423BA6F98814FD5F17DFA80C444C346ECEDA88E3E859B72F75A8647984EE0358FE65D6B085D7294EDFD4AE24FB85D97634DB6FA8B10A50CC3A20BA0A45575798B834B4F18CC1ED9B958D77135BAA80C6D87DD44A499178A84A92341D7CCDFEDA1652CFB487FE22C9542D560B0C5E74CA8821D06EFA192021D877360CEAD08C7D25F205225E5F9943A2815D16DD66AB31CD5E1CD2AAE6200312A515675E8A8A9ED2CB495CA5EF17D468518C3D2EB8F88BC7BB44B7335D20F68C3983C9C730F16A5BB07D3EC619D184E718F58C70FBCC6006706C3DD461488A1107007E2115B7BA65F57A811E6674767A51FB827B52BDAD538B960AF114D0DB5C829EA2DA0879147349F12AD4EA601450637832CAE0B2AD8191EC5A7E623786C285C9DC7C4A0201C56DB37A85406235BA93BC9F52AECAB27234CE0F2C03E3DB1AAE4FA8B47E5A06E5C6C884541C901B05C30E4CFF00E4456F11CC8940DC7324426906632E496AEAE61996F12959298AABA80999D44A7D4EA9562609ED00548C26BDCC0A65B71CA5AB1C305C54D532A20E8C472AE68CCCE73101A99383C394B9A8BEA2DC08BA6571D89B11144E4F0608781C738A053C02F2AA565D46255C45241B19ED9EA85667E0B3FC1840892AD80C609AAF1502108421020790BF01703C1E4D783C3AF07719CA3A8CD229B46318A2F918B315C7712211409F3E26A266E0FB995F8086C1E610EBC9680607B9947715456C6F31CE66134F51FD78336F073728CCD3FDC751AA665EA617BA8F3505BD475CD4B5D5625D1EE2DB75A88062601D473CB1D45B4C71C4B56210AD2526EE20BF1FB49701C16CCA7B5AB32BEBA6070E688C4B639D2FD4DB4058E2C8781BACCA9140083A173C704AFE39162B1D1F50C87B08BF4C5718383393FE6314C9A2DA87A38B81DD8A37876FB8AE99080275980C12A98A2CB291810457C1F6C2CCF529459AB93E61D3B2850BBF5A271129ACE47063DCB0841EA7814510B8AA55B4681AD5DCCC08A0426AE9B7E20982D0E60E178C90F54F61747F51232F585226DB485CFAC4B61F87416CF5C77772B0F806EA9A7DD6A5450B15695DD6D88B16AA08AD1CFAA83606AF424507E5FC0C43847BAC42AE8AAFA2541350B343339ABEF9A94C4C0428D47BB1CC6A78C8AC58B7F55F980E48C025A71D6F27550B153124E05709FDC46914B78649A6869EE62248A0B60AEFDFB96A6255AAB8B9C86ACAD44C7B8FE0F49FDA31330600156B8C2EE5942A42A38558906CA2CA0D0EABFDC7E567CC2CB220414E13201FEE554181A6DB880AF502B55FA9546D0053C2EF7886BA000C01D53CBAD4A93562EF840A5FB84A80E15369AE137ED25DF6318D6F970918BCCEA94684071775EB31EA3280617ED0AACE13069AA4A5C1E50505D74EAE0C4F566DDA0C7DC32402982CC0F0379A98F360352DECCFE2A3D3B43D85BA9F615D40CA0C0D23A63363F315C6656B5123550A1106A186CB8C38A803894BB9C3454D4E5666512F7516AA67D78461DEA7D29D5B25A609BF198C1443072D4CDF71F7EE0350A04352C5F8F5CB38CCC2471D786CC4C9F303F52EA971A863A96EA6BA82E44735F8863AC4F84295DCF4C65B3A9722B17A9A950D4206A0304B9C1336A11844578686204BA6092A98F1A8782AA06610134F04950204081E0206204081E03C05435E0F07F89D783B8F83A8C6728C631F0C1F26112E31DC616E648BEE0E61E237327828311BFC4B063B83F1024DAE692D28B06771CC4BF076C6ED265170CB7C4DB3172B0379E7C1FDC7231CC71A8F24177FDCB2B1AD47B980EBA8CCBB9764E845185CD4FDA3674C2F640AFCDC72459356BCABDC5C6D8B276433EC0280516D67389529EA9A1D8ADF3C7A60DACE10515695F899BBA0A819C9460A991096E096AB576D6E356DA8D2AE1578E7570E8A7300B4CAE746A3D4A5217D4BE09401239A5D6BF31549F4B239FB8E8586140BBAACC701B6536D744094A28A4EFA87B4979A139015AFA95B5AE9FE950EE302BB0451FC0A89F2864EF94BF1150CAE4DE83F2B0FD6DD05B2D85139210385D59DC3C8F66941DA71704605AEDB63359557C4E415A826CFB4A1F966300C26EA9EDFE88210B9A9C887AD1F99811580AB6EFECB82599ECD033E96F5B5F52A13AAF3A8072D0FB61A00E541027B13710254BBBB41B3D63043122906775B9AD2BD460C466380039537D620535399BA1A1EAD5FB98ABB1B157A5403F285D0D4ED006BAD1ADE61B783C56D517A69D7D4738055B130F14602FF00132864C8EB81450C42579CC7283E87FCC59804715874FA3788E884B71DB07C31738187AC86ED586A02DF08C5EBD9414536EEABF305D6D15AC395EF0A3F718D37C3B96DB6053EABEA2201F155A329D2352839C0808340F3A635171A5D84D52CE85C40CA4952F1A2E835B7EA18A20ABD9B31CDC70D1432AF1069C482E962E0EA58691434D979BAAC91338C480B25DF231997DF20DE89ED8E2306745B529AB54F7D12E23EF917416E25B217F11B29834A1FB95149C910E6A56622B3DC556A6EB8376C7D4CB28310DEA2BAC9134E2A53A25AB36DEE54E331F31A4ADA97312C869730B988750FCA5B0CE222230C33306252E4854B96393105E25042C132DCB1A9CA268A8E8CC60E2235D20DF73240B8697533E211B4B7885F73D32B572B7044375E0CC98065DA8519DA59897129D45CF82E1AA9502103C09C21017E0C1E00F008102A040B86FC053E021BF26FC079DBC3AF0771F075349A7918C60C7F80CBC8F8298477303169F0692A91D2F8D331BB997B8CD98C3881A8EE0B7D46C83F13F68E1997CCC48E0F2266AA223A81E0A822E3DC6F82E0C18891DE88C4B7EA68A8FF00EC4EDA621D4B931DCBB32E2B304C4B5E320B6E3E778F3863F03FB8FAAD166AFD4AA6762DAE22A16509606D758E20525DE8F36DF98BBA1502EB957FB850B4AD07653F2DC76BD53546BEB71749EC32DDB52D58EEA1E80EBDB0C69821B8C00DB0010C8272B66CFBE6212DB0562EA8DCAB66AE3185CBC3EA500A83281B2886AE0E1160CABE465821100AA29775CC02553A1F17EE5965AB1573552C621C8350AC1AA0E584A09BC10B85037E816BD5F50D46DECB333A2ABABBE60EA5336A23F2717BE62D6C08B6FCAB4A83F0B012065F2528FED8B02917634DA19C0FFB8E121F4B7831A9B3119286B358FEA582E2948141F7C7B616DDB5B8CD3B39DD41401562EF6F0D712B51A46277F2598592A572B3936ABE63297B4815017ECADC647A6B436BD9460AD1F7775097DCE1B1F72C3946E28307871099AC1948A6DC7D751C50B040B02F94398FB16E70721D0E211A51C157047BB5B944F92B3735B5D9D30E7D90AE0EA2D9348ED70547E63500E0C9FB8222136D9CBAE46206319B5054FCFEA24ADAA9060FD04FC4C3AC9C00358EB519A335F40B59E79EA6BE2296AFC2F6BDFA891A6B19508C19FC4AB90046EAC0FA37F1152B2D5FF40C59ED8CF485D1AC9638CBBE20752BB5C6AB4DD1EEAE5B1E41BBC101A0E6E244B3F05AE68346620F4D91846E00B6B03968B555CA188168D9ACD446A32A367B20A8B1A61C1F305026F7A3F4C691E43A1F51AD8A6003CA1B7172FBC4AFE616F117CC5452156A1A602B3FB9CB3E32E41B44CA6A5DB9CC8B5001EE009947CA5FC43FF2855289A8F0A973927592C3DC2BC625569201A9A129A977114D622CE4C418C12FCC510A96D621554B982E0F841388FD45253B80C5CC7128604370FB98264F80964B3145702C818F06A566040B85BC0804AF0115E010204A810840F01E03CD7835E4DF875E1B46728EA3AFF00C7719B4B4608CCBC1F0D3C1F997517171CA2A5C78AC7C1D418DE6613698475EA54107315398E6E3A89169CEE38AC8E750751BCC4A8B71BD711264D733D312BD459EA2071F71C463B9A7C478BADC6EBD458836F6CC237B0A46EFABC7D4CBAD7179195FCC69A775ACBEA310E500A1EABE1957855A17D1AA8986D345B4EBB0ACC11ABA3B2CD874CFA84F383950176DEFD081A98C0B1C4DDC054CB53A2D48A99283DB903C5438457CC4571965F503656D2631787E7895EC0A32B3623F989971B1E97AB95473E76FA5E521ACB2028573F010EC60BB0C628810BEEB2AE35EC982BD3598D53AAE5896430AEADDFC4B3934B33E952C65230F58FED8897584AB7BFB824412AFC84AE0F982444478D31C1E2DAB6A2933456B4A1FB96D95280B46CF4186E0C0E5BA6568CC295482650DDB9FF7040596A80B6FD99258B245B143D15C5E2F3C4C6D4157719B2DD3F87B80928918AAD166D6F1EA59310316D693F7317E70404D5A6CBE2511CAC028DB80DEE98D96E539028B7D985030F5038A2B90D4AC0FA2553562E9BE18AAA71B7465063756FBF9A78308482260AD2B4EB3BA80360F35DF73363662A3F2C8DFA9EDE733A314D6AE30E790E039702327D26745F512445B16A9B8CA07A9BE6301C64914AC3C8353B1488A7B70F7303A2585C8385E711A300720383D4406D85E8707EEAE359A46D52D0FCEE153C4B148A2FF0050936A4D61452F4FAEE2D5F355D439F7FA89DD00CB03455AF529A612CB038B386B71664A9130696F57EA398191C5FBCC1ABB6D9FC7C4A68522129D39BDFE235750584574FD107EE8B00D0A6716751F814A584E989067180CBB92A2AAED98187C2F4CEA6AAF6711F0F44451117491B72EE264E257C46F086A29DEE52E662861A7A40A35106A20B44B3B20B535C669AC4DCA8AAC11C31140AAA8ED6251D4E6173D533C71A8B77FA87998532CF73280A71645BA1331DC54C952809471E4D56A1E2A25C58E204BD850A9C12D611BC15E210C55375C458D1F01348518780205C12A051181708C254A85A540A957E0B86A0780A843C0793C9E1D791F07534FF00B18CC98C6318A318E63863598C7516318F350C34D7869369B78267FA8798D463B82E38E1F17D438FEE3923859831C5E63D93E51C0F11BAB5993D470E22F753260E7C613EE7E5F3051734E58E1EE697F981F582C0D1EF407EE5E0B5AD766D03AB9BD30403076C4D2EC60747D85CB3A0DA6FC2D84C54502A5628AD4D60A149D05FEA5DA4EF442BBAAB28C12FD456DC0DD17C5D4241E2CAA39A2B37EE0368E9859DFC967EA1AD8986423DBD9F89423815D17188BCB327BF65ACBCD40E31A720F7C131C00B9B073F2CD108EC1A00AD3EFDFCC1A4E81260EFD44C79200117B41A71BB3AD5969DC4B30834B36ABD043E48D704A4B41805DD6D7E6341F685CD0C7C198B232569AAB05FEA65EFBFC3A0FB6163F3426589C0DE8EA29BED46F537ED739E087E201DA141B7D4AA797390D97ECC46A4CB55A4595EEEFF52DB03F0BD7CCB83A2B03606B95C45782B556C3DC0621B60C4C28C67D02B9F7311F8D945E46EF732786943402DDD03518644AF0723B372E2A80B36E1C6B98D38098D4E0BA89CD5A0192B54DD15DC19025177B6E9F8AF88C6814A5664DD863F73108E8EB28A784FC532F6AF42A6D55C35F858CA53D8B9D9790C30C7F5A223908F704A88B51B0B19009F0B28B16AF28B9BF9E2007540474E2CFD44FCCE098FF00D876FAEB7A7099EF3130E4415A581BA6DF895109C20348EFB9BF489DC1C5DE0CEBB08657A4DE1E8E2FE6355CFB5CD7ED2A326EEB002CEBB88DCD7796A947E7671182A26C10374330D5AB7DC4F6F02BABEE66D96BE017B7D40551A8576E7B3503198DAC537C467C82D5688DFA462CE82384806B14991E2E0832A37034A6D63DC480501DD56C4391EC8AF2CA813EA1D55C9C3F24D1E0ED9A625DA282DB1C232BA2968ABF9803A1BE12E02D94F38CC810E9C32F451EC8ED8114CAC610D4A4E204625BBC44B688EB706F04152964B853151DC08B14BF12A172A2AAA7150AA8660860CCAEAE1D892DEA05CE3102DCCA968887FF0023E2E1D710256E25C5B19873E152AAC42BA45EA3A5CA1817E65B12527AA1789528C237CB31751A108372E8861CF89F10781621150821A970D46903C904115E152A107804AB8153702BC87F81E1D7F883AF22DF83B9B466D147FC418CDA2460C46311974C764DFC8A31DCC06651E711CDC71159844A83133CC5677168B8B5F72D5788EFC05C63E88BDCAFE632CA3DC77168F0D0B71128D300F3712BB9B69740FAC2C75056A3BD7D8A1059D8A14566DC0746E3304CDE019A4A188E46EB3A257F38969DAD4F12FD1DAD8076BC11D2436811D6EDFBA867E050F84D63821BDD6AC34DBEBD4BC40144C36FA85121DAE228758FE650ED786E6D1FFB641C0F5EA21DB9CC62B91195F2FEB514AD4E34BD19885BAB1455AFFBA97157A6850B39AE185F63D30B06C9D497805F7BFA9A0240D3A5EB353190BB282B0F65CAC965AE134F977086832D922D2F18862B7680E5CD5F178BE6662FBA855E955660A1580DD7762FE6147EA6A6AED7590D9022421AD103054C607268126DD52D91164B5B25E578FA8584C59A1E62376DB6C202E00BE2E3CB034325DA6029E7A984C017535164CDE081795497C03AEAACACC7C951B056951D737710E53195381D44101B0041ABE3DC10CCE358C89B1BC5FF00B83988A319B8286E87D750273EDCCEAEAD3DCC876AEB92E8A6AAAC8A14C0754BB7B8A626BC0B2AF226338A88B1615CAF0E2B7EE5561C28070D34251577B8E905D940381BAC9D6237BC9681E3B52F6E7E62A07994B3A6B493012C6E3D1EA3C9692A0B729C4AE91BDAE85A775FD43A1F52E176C1C8E9983723C576C79C0CC8039AC59053AA69EC8C5C698D0A69DE09FB61C4A64FB43F3348A03364ABB3211F99A533D85780E3BA9B28451A5ABFEE35BD4485220F6C7057F31365AA58B4DBF1D402CDE379875843512CBC3A69B98F2374215A5F075894AC4D1154D9938C38FCCCE10D195DFF00B8B0F748BF98B4A543B2356AEB75A80AF053A2B0E984BB55C111B780F6413CD60FE8772C5AC1CA74E764106CA7DC6A34B8188D9777956422A47CACFE2368A3EC994D4A47171EDC4C7EE5056271A8F6A220B462564CC600306965C2AC816C266291831FA9580572440B88CBB8D30C454E94C72C6A19C40705410971D495BA80CC301029C78D86A38E3F73952B7C26966055433C4D2011EB3D71C829A4733227282E279483C8602A137E152A530204204095E012A1152A07953E147F85621E1D783BF23A9A7918EE6D39415E18F912309E186231196CA1B9BE20C59182A2DBE1941E0A0A8BE0E398B3D33348E7A899639F98E578264CD8E22D4D73322CE23B4621982F78885FA87189A96CE523BAE238358970711B0D45E1FC464AB0594BE884A880A707C7A063EE5D8BC15BA617ACBA85D0BD2BCD5D072DFE0238B17A6E8ECAF6AFE632ED2C7E2BC4A9C6C492D87705B45162AB4ABC7A8F3A4E40315FF0C3FBE98CF0A717F98B915A0E4C0103D73993758071BFE21C154BC8264E8FC7708048046392F42FDC18A2C10147F22FF32EA9A05AF66871F333B44B66AEBC15BC4C7428705B39AF9949ACCDB4D6395E622CC148B7D3F32DA52C3A5E8200F0802C6CA15B6F8F53626AC2D2C03D5461C4226BAF4BEE1A9133B065420B709288992868CCCF718C209AE83744DC054AEE5A7A4CDD89966C0BF4D7DCAC25F826D1FCABF316CFB44580057616BEE32A2A107B0722667044941D29E5A8F7EF50724467220D7A8DB51B05CDA7557FD45352D2A045E5A6B1F71EEFB7AD62B836DD07FE4AD3F07494174C5FF100A4D3D96AB6F1B7EE627973723D738BF5B8077406DB9A2B25FCF100C32D9BBD1AEF1529793940BBC3B886876906808C1EF735CDA98CD6CF6440143543ABB4BBBC4BEA95EB12214EF9828BC17B29A2D81E995C5295FD01BE4984B41EECE72A0B8B7B2A88DAF20E3D425C5902F2521D90152BB1A288F24224D95605B17C76400C1D37B5D2FADC73C841D25DFAC8400EAB1420B06CC2FE20D8275A536575FF00B2F68AE38F22FF00734D20C0150DE7ACCA4E714A16E2F9FC1824288E45CFB1342C2148FB250D66171D56CCEB61A1C95D84525515E81C5B10568641657610CA56402DC23CD38F4CC7E228161AC37CE2AE1CEA11407203E3E34C6AB1E6F41DDF77C4BA92B2986A9318C417F9E8BE2FE62EE170179B7A966EB618F90FE25F7A3BFD1C35F0CAAE6581C7B8A3428D7F99411CBCA69FB86EF230AA55E8802E6D96DC5562F95C35145B81040A28A7A961C8E7B87A0CCE92BF13F4079165B314DEFAC732DFF0B504B77EA52BE976DAE9F72AF04A18A861A804D4A662498B0427A25AD93133305C6FC410F73F747985475B4824C0F0515CE889D2579844B985661C4B73324AB9803E61A264C21819857D451312F116A2292D95C2D2B2A60C09588F9040F02542DE041E13C47A4F84A815E02E0540BFF31161E1D47736F0E51D7F8931DF918C63A8063B8911133045C14E67A44388D6E3C3C928E260F836C1B895369844B8DC5CB1CC5447316611547882E2E8CC4C41E19ACC25022DC70C58EC8BBF88B132C7CA1F35C4B3470382264680060A52F977DC72530C46C4BDFA3511A60BE8AD75D6208A28BECAB86F188DCC818AA7355AD459B9056803AA82B17AD95AF5CCCE7A59997E54AF920F1CA5253DAEFF00EA8DE017B5ED5ED9B9490971147E5FA8EC1921B6CBA7DEA5905826829ABCFCDCAB48A9C6BE377B96C0D2B155A120DCB507C14EC082074F51DC1415B3552DA28CD03BB7E62B53EFA5B07F05108AD290A7A5FF00B500285325E7312052B04ADC52F9A37512FA5C017883E6A63994EDEC1BC55FDC538A909ECBE9A2BA8054B2D47DD7FA820B05490568036E315C4B323AA05A1D87113CDBA425027C0DE7E180EC988AF443BD245453B4A5CEECD76B1EF696C95365015EDF98A62C25323937430AF4C562C0A3B80E9BA82EAD4019B23CA9D4B2261C243BF00BFC4288B33A9E0D728F820BF529DCB94E29DC484B6360070041C695C80D7189444D2C0CD65C2BFC5C090AC4ADAF0B5C5EBDEE5C1786DB909AABFE6183946823BA6791AE25E34F35551ADF2D4634BA06ED653632EAC672EB5D8550D6E19805CB1CC1F0F6E48571B39354EE65AC41967CD2F224AD9631BA47B8E81044AA2E4F0DE18A4A2B62296C98F92570B9866AE2E67A58E01FABF9621C31BB9AE1D5915446C52552FD12B9D39E806332CD7A214571F86EBD4A708A5F124C0118BBF2F719B75D07A7A8F954CDE38B602F5144E01D669AF51AC1AD55E184C62D18D3631451BCE74C5D3B979B4B77C2BAB982FC26C85A38EA11EB22E072DE2F27E26441E847094FB2B30C01B850F25A66266DE546C0722C6A107F785CB51B1738D429BB0E30564DE1ACCBECEAA67C19CEC8DB5F00A1F512CE82E068EB9FCCBF18358187CF31385D69427A3880EC507A7DD6A26D68AA14C7A895C1661F4085D851503073CC3B693041EA20C944D35D23288AAF91D638F729C7AD2D5C432CE37C7575CAE2DE016EF45574D40CC642FB80B4F54EF8DF3339713D1123A8DD28DC52C33894EA75CA4999A88262CCE821A66028403C4A630DB89830AE61024364AA984619930D449CC6577358172A0950848999BF0A8102101E01082083C84FF00C7837FF00E6EA6D1DF8728EBC83C1DC7C1DC63E0C7DC598B7CC4CE0F05D999845A80F101DCB486B71CCCA7A4751CFF8839F0D63894FB9BF886C9838964EA2CC9A22C58EA2E6305C72CAAA8D5C19F52B35700913F510665CEA1D96C874110FBA8850B203FA8D3187472D7B80D60D3A41F7FB82B101AA13BA3FA8B9726B408C14B344B22E01614E5A8C854D96A7ACDACA40C2F504CE468F579986A22816E800F5F888AA20F1F1EFFDB098E50619C8F6CBF7BDEB45CB7CC698EAB273486ABFA80C4371087CF530E86C1823B3789923D51BB5618F02E306B68685B79E886559BA6B3EE652BD06A2E28B6DF47D448286D54580E846FC680B4F4436A940ABD9FC4D7D060102D3ACDFBC4593488337D3B57339CE949606CB5765C4DEC6E069A15EE917AE56A3086DBA0A8705DBCAD6D5E5BC57A8CEDA0D8669AF0895367F1774E13ED4BF4301059C631295CB547A255ECE997B856D525FDC55E84D25E577638949424295A29E0CE086CA50155BBA780A4263722758513DA880F519C4742A6A0672504068C5A4F6098DDD4033A286103799A6E55EDDFC62192C085A433EAEB31D0A81877196F39868E2A0630F45F17198AAA204B4A55EEEA55235948B05EB75F30F29F929E4387375CD4B643DD3234C0FB305A237667F30505A0A80A4CC4E9FA5BB7EC56A1AB293618FE56CC2D9E4942C63D3F3021430B841CDFD41974D60383FA4C9179A016C15032EFF00DC6B4483A8A46DEF31E489B0B66B7C2B3EE57827A05A45FC199928BD6C351D1B7D312018447491FD0394B3F88A4B65B3F4C4C3CD6BE1CEE2913E887D0F7EA54055CE1555BEF305F32CAD3385BF7B8A143394252A956439A97782B39C9B32BF4C6CAFF82005CB261EF71236804A6874FF0072D12C02AB5E0390792511A91021A2AA05EF40A6FA79861B4B3196F98D9D0C707E49915790264F644C119C8AD53FF5C4A2A26C32DD9B2A24750375FDC95DACAAB09F1F511F58D1AFACE1A80541AB14AE8B80182B41E1E54830800071EC94A3C859F8D5C728D730C6CFC42A96340DDB3FA8C64C865B2F2738D474A6962090468208959430BB73E8441B8372D7308A7103C92BD420BAAF53D10A0CB388BC4571004ADC31B8BBD42F3042ECA1123030D505FCCC1EE310CB08DBC3CCD22DC02540C423A8F909508A21E008410103CA78388650CC0AF1570FF1BAF237E1D4DA3B9CBC1D4D23308C58EFC328C751D4C26518EE305CA82DC4B98233445AA789C78647536F07719B66779F04EA0C3B8EA2F83EA60BCCB07788FA8C4A20DB71C6A32D05B4EA0CCB475971E187DC5E08B5C543B487FA75C06B2D7AB8648164951D57485780D8967BA85C0AED4C337F11A169C9BB0ACB09A16502DA40ABD46C53DE26F8507B3D8FDE256DDC8A6DE73FF5410D70AC556AE18AB6D5C0AC58E0917164A2C3D2B9A624090734B6F201F0FE23AB20B203B2F948EA0032FB56B687101A0D00A08D94372E80CE01223D98942FBC562A207A006615E7A57F72BC985C439B52340C9B7383BCEE61036AC00BB077ADC414127403F05110B8922C9A57EE043C6AD635867DEDFE6523E851A8C1457EE5AC0452946D71D02C5B366028A65CE501F9C73005564A58838BC7D4087DE887B05C98086A8403F055C13BB29A9C0DEB0601B2012E05B2F6E63B7691A716862D7F533D25FA3EB507C00772E5C4916E3E0ABF732052600BADB82EBF66A5DE15D0ACB672D2644CD276510146872FB98A15B154BB48E6DA35A96762ED00D6A340F916076F6C51077B3505F33A01CC2DFEB8581AEECFCE0977CABA6E6C5EC7E615A2AF00E65D382ADBEEA58AD05D778719D9ED8BACB09CA691BABCFC47BFC23C5413B05BDF708EA18437DDFB5979493021A62BBA02C86F75DCE5BD0959743E6A6C8B9054E53E31176457B62E2D0E2B72E8F950AD315EAC6349B304C3193D33322259D99179174C0265F7222D9EA5FC524F55591EE30C194B84ADFAA976845D4F64F8D0337AEE0AF482209F98152EC1632E5D5028E8FA869D69908E7E08A505487CD09419AD90B3227370DA32D8211DD2EB71B0C3BCB972C30519083AC87D74C08450D2B16F1AF98B00EE87E7FDCD7FCB494B86B7B89C0C3852F4C1B9B2BAB1948D950013A4EA020E32C52FE3710B21B5D7D351415708F9102A7DAE02F2BFA8641ED8ACC4B7C4C8949B6EBB8E416E4949EAA0AADC249D204026944003DC56AB8580C4211F845D5DF17BA950A8D2DB69DCAEA749348964C8941AF1A750DF80F78C632A5D7CB6A7834350920CC2ECD3D4784CF1530D13594C367B9A42933C2554370D4B388D21E01298D9025CE26A2F9A8520402104AF008204095351D45A8E528F00B815FF00E43C3A9B477E0F31F0664783B8EE0F063E1A40F83F98CA94B4DCCD64B4384AACA8F706255D626115661EFC8B06627DC05FB8EE0ACCB118FB8D4E5C470B1D11FF0098BF98BCF31AE639751A306261C45AF5368FC470E659625B328BC1708785A07F0132181B51748D7C108A4A695AA0A2B1A8106EDA647DC020B7BBE87FB8BB4BA515EE501745F7F8CC81BB0DE656BD15DA3C772BB1FF0068D695AE5936ACBB5ABE60E28C71DC4BC865334D7F32924B48A4E9C7A8D8B05A5F6B6FDF331F873605B4381370BF83942BE9997F4A9E6838FDC2EEF56DF83D1FA882D5D1538332C0638556BD1BB6A3BCCF74D62D58090B453E76A1CA8EF8971320CD50D87EA03C50A1859B0381DCB5360AA2B11432B7996BC67E63842A321BCCC90431F1DC644F185A561A5063C0AD95B628DE8F88E278A529CEBB729976DA535A75C014756C01A96D86677A1444674010E62A79B0B85908B9B5115D50EA635A21D8597CD17A82445913A5D7FDAA9586A450C0BC22CF5392FF002150B4B336D0ACC2953870D6CA1777EE73E83E06ABE7B5F502E3F0D0C25B759A21C2A8AAAC4A1DD9889E8EBE28DC73C23ACDC2168C9C01AA22F28A9F8A05BA2AFF00B8D542F3C7582F2F303C0D0C42A83D51F9B8FC03AAA7D239AF71458D1B5F12C5AD6211BDB64540DEFA348BAF31CE910B057CEC8C6BF0E7D7F704612915CF71031B5322EED1DFA89B0028652261CB755DCAB0E55C2143EC98F98221C1002FC921CF24259ACE6B03747CA542CA5C811C1BC2FE18272AB28C6BFB3300E2DC11B65F9EE5584914429593DCBB7829359950370BA3F6988D489F716EB51C6E4869BA367E222360AC4B8A7A2603328B487555B72C2651B3378A283DF12A85719D75931AE3DC61876F04EB3844EA1DB50416754FF12E430CA839CFD4C1262B1F25F3292BFA24F7D317954B4159D4198F9A2942FBFF00C942B0B0AC15AB814A301A122E3B9CB9EC771ADA70D87E4EE2B25812DFA8AC75D52E039380288E434AD2AEA71E168DB692C05BB30B2CC7105C0DE8840557A972D388C13E0D7896BA8E691C7598D75E044A78326A7AE006A3842DA211BCC43C4A0DD6202E0254B3E25449B1CCC90C234DC1454151B65D2A83310A85A35891B3299495712692AA540B95986205C084AF010204A9519926E266660FF8095E187FC0F0EA6D369CBC1F272F0DA31F063FE20C4B899F164AB3AE2A29104326BC28986B332F063AB8AB71633482C731FCC45B8B4B833982994F88A96263512C89E1EB17C1579EA3CE227B8930663C0A0EC71F8CBF5115EC0B957B60EB0AECDA1FD4AE8E7857404AE72174FA82A6DD1DD52FCE3D435270A6442506DA365E965D61DEDA95FA311FF1DCB3E2D03FB0E2502963202EBE4B96652EF6DE594BF97FEA84B800E95F5D7BF5182028296F69B9ACD9C8D21C856B56CA7900362D70DFA2A26D7C0553B576FC408065BC65C0BDBB8FAAF58BF27F11E81E9C3496E56580E8160200CBBFEA0A2AE5E45D53D0731A1E96AB68CABF98ACA2CAE2F6B33F1D115BF981132896C0AB1FCC1018D921D1DFCC7887955C3FEA23F42CCB9A546AEAA2835D7DD9C9F3767D4A8CD0B55695CFFA420400BD4402BCDE75DB2CC8EF04D83828FDC32CE82C56223B0B5EFE227195500669C22BF98C58AAD6723E77F9608B4434726FA3E256D588145AA872A9541EBB961A7450E8955F7F91D409630BB64764E1EA5265AC956B4BE60A2D149C056F2850B0C9918E72641F7BBEA3E44C350BD0C6BD4BA92E6D32EDFA1C5463555D06A6DD94BF8A8ED935298F2F995CA324532AEF59310FAF7BEDC0A38C3F3024C15CC112C534DEB5014CD32FD8727C31747EA4A03B49DF71E9C5E32556272463E7AA8639739DF511437048FC9F132D05619CA81A6273A5FEE0ECEE3E5A3C42F21F313004412380A35BBFA949880355DD9B86640B4E432CA81A9B10583D5532E3284EC15443F8971C07391B2FBDC2AA525A550DC6072F50C68389E836657EA0C4172E66A9145A3DCA010480B75BD90EC012B011FD33048795A7AE713330A02AC357FEE60B1178F5ACD41AC1A507E60A1882230F64CE9365ECFD1CCBA360A5C4B3DCA30828E65E450750533DA8C8EE665A98311BDE3DC41C263431AC0AC4BCB46A15025DFF00247A4D6CD2E662CA81572A0E4DEE2AB1E2FE0FAF711B4F71624485D7350A268E5EFD432213D33D31F5F0B9C51316A15DCA554CB2AE2649578892AA24A90C3440CCA9893730981B9571289B9B6A5D008299613F278724BFC183184950211DE168780C2104040817C4087963E445408152EA52E09E0B165DCD3C1A835E1D4DA3BF0D231FF04C7C368998D4D231B46318C5CC61772C650E6519B40B1078C04DE25151F9897BCCC2382728ABE23840B79814842A21E2A67A9A8E63A23F33B4F867C1D4164AFCCD713489EA245D3EC0FA25FE7FB880C53295ADAF10C62262737D31528A0AAD54A2CB81600F5ED8F4A8515CA71BFCCBA2B74A28C272C3D01AD0C73FB46454B99940CB7DDF1295C4D09407B6086F03CA1FCE638CB7AA472317EA76D1904BBB39731B1FBB3FE7F5120A8BB0E9BE2264272C0023BADFCB0B90051A45D7C47D9EC83D5D6B99C451B2CD55139EDF01D8DF12EAC5467B679E08784592B4DA8BB45BD4A4FA6E87A0ECA2F5DC6ACD9962F9CC26E9B40E8AE5EFDCA74A94C99B6DF305A965A1D89BAC4B739C57ED89FB9A451FDFCB96B8A88517B7C540AD5F0C49EDE73A0D802DF7DC7E890D056A3814BF8618DE36A09745C19B05855C2890F2619E36FD131146C1C0AA66AD0F8CFB34B558A1E06DB30FC46D65A0F05163658B517E5484A20B4703817EE0385E403817540BF32D4937C8DE53DDFB8D8E852045D8B8B7012963AE15DE06FAFF00B7091BE57631C1ED04BAF88926428BAD4AD66203912C4EF706B074366ED3AE5EAA52BA99B18D3D6A0329ED80A2F5C167E6513F0B64EF96567E6044CA4A1A57079F7075B51C90632A496D5915383D74CE99437805B7A963C520DF1B67DDCA4C5AABC028ED957F130152881EC258042DE57FA83961A03797DCB9C093D189CDA8A292EEBEFF00988C11417C6DBD56238860560131F51D1357906C2C7593F7051142E01B07F1FB940D63458D187B9708445E5957EBF88B66151AAAB3FB86A019FEE66C038B96D9FCC42A94622E3B8B87988B706A22F9B04F604BD9140AD179B2F04B2B912AF601EE3218DDB0DF130D2C81369C11DB0874B255C34062264A86BDC5D8872B431DD4D28512B7714015D37D1F52D22441FC128ACCC6A002D5788B8FCCBFD5CB12805F6FEA04A312B6DAFEE288B502DB7FF919B722AB41AF882B85ADBF57A85C22DD624B61427EE5052A253C4CA2B52A7112F10A36D4691E7B86A0D7862E1891C4188A9AF067EFC1731473E76993C199078129992054A977E1CBE0E70923E11F20809502040C40812A57863CCAB9440F03C996CB4BB8B0DF805FF83A9B477E0C75FE444891F016511231594172AB88F8132D94B1432F1371196A974C38853C81B966318999C21C268499A99EA253123A83EE26EA38984A9B460A86E08A98D4464BAD80BD10633A0DAAE22320AD715F040A2EC916E0CC1A2F76303BCF50489D94A43372A71507754E615E157F883E257D6D4214FAD5CCD3DF4A7D575128699EF4BEBF89615082038CDBEE6725ACA067F94A6158D660E17F13255176F246104D7E0B1F5BE66DB4B2B26DAF9827F107FA59B5BE2212C6E503DAD658DDFE15868E7EF11CAD816553F4B733690CED80DBD67F708E1238610CB058C58651295AE3DBA9875A83F65351DC4F2168DD732FD1AA995FEA5A0F75150F83636175121567435076F02137F3DC67A5EE14EB5A9A43646BD31B8FA0430A9DE5CC2AC63B6D1C22665AFE14B4723BE378B63EE08BDB963D111254C40D94FED83E178C4F2B3C467AD458438D67D63F12A92509C74138A5FE201BE401B601D98AC4B5D888668B6B76770892684EC58CA7BB8CAE62AF38CAD519CB34851937B6D0EB6532B634ADA8D3CE6EFD451C6045B5441E71FF00623A853FB6AD1CD633D40FFD5F5B6D98DD157F9D47D5CC961C60C58E594853AD91782AD5577006561DA4346B5D316808B5BD8072F52820167A4AF9B891F0969F81D8FC4B851D321B82DD17B811D0044387BE26CD38D6BDC7FD518610C1D0CFE6E02455C1F2A4054681E2B2B2E80B06D1A192F6D95F72D5D0A3AF4F789A4BA86192B7F8FA97E6B0DAC83710B0E286FEE25C8020B29792B50B39DC0A96C217115662BFCC0FB9BCC22DCB509F88D4C98115D2EA08801457755F9B9C094C595EA2819AD4F74FAE2232984A3E6AA250D56219A85E61BAE22819A88A51191D47CB25B3E19B66A8D431BC010ED174A528B87025F9EA30DB791B27AEAE3B6D001C2B39FCC7E88201796B4C55EFE62056CACE18FD4F101767A8106C86D023C46CCC118951806348999F942692EA2C66116628DED945CBE6F715786598230CBF68A3A8B1664655F8B68988918D63978046A57F82ABC04081FE4C4F207F8244B8CAEE27810D40F26A3A9B4773947718EBFC025C4A8C6296780310226E30798E22916E349B89615E215850892B295378034419891298224B4C122C78F71671165EB31A3E62E62663AA89FA8BD47F5116F9A8BB8E3DFA8E2A3FF54AAFCF49AC183ED80D82D7A3AF4420523C302EA1CEA5F4E6A2ABB256B9D455664B8579174ADCBD1DCC587A0DB5D7A6328A495E45D65756CB7D2383EE0EAE257C058BABBE3E25F233DBA36BF67E253C6D181478F9625780FDD09F219F531E9161A0E636DBACF677EA2A48867EAAF8843C30605D391EBE25D9179EB47A7F68250A30B4ADBD16B024027A504A376C05E010A79A5E5AC7ABF511CDAB96C503EAB731817C0385B559603E652B0AD960D5A1D54C2A5D16C8FA841B2E30E4B7B255758A2C1A101894F205B9A070A1A2BB8A71A7E50B450618A4365548B220CA5617A8EC9660085852B830980DBEA2A8BC730502F09171DB3956F97E2E19D7CB5A793F17F105CAEEDB7A01AC57A65813D8576DFC28FE2068292EBA65FB0E623E90AE55593C6ECF50DAC5E49A58AB2FF89B4C4423365AF7D40ED5F1923AFF00D8BE550199917B4DC6DBCC0BBA177F47E613C3CF59734319577EE334B49B60CDEB1C3984DBD4435967B3357F31A00B988A64E096E651C1B800E9236542EC4EF170D34FA8D32EA3763FFB08EA4320BC29EBFB8AAE0AD6E36382C9AC87B35C343C9983EA158F3D9F8652E01A542D8FAFE20C3B05B297772FFA88341778A77DC77A0C69554D7B090DC5990B0A38B881F208AD1A792E98A2C65939DC1F060A6EE61028F5A8AA5FD23B250C6DE3C2865DD978F04B21C11CEA8C0E6116E9DD5E2610AC158386A552E94D21550B70A17A8F105BC41C66004AC25452AB66E8EFE6001ADC6694B69DCCAA4D6587B0A3A7A4BA3EE403DC7DAB1383BF98C8B54D34B615F10CB9D2870C1817281CF68D8C1CE5BA9A804115ACF1280D85946B1BE6326195A9E252FF00B89C20CCC12C48C2229AF177E0E27CBC6773EFC437E92A26281199BC0D596BE055C61F11C231478062E621984D224AB8DA158988918FEA04D4B83702E062044A952A55FF00825CAFF175E1DCB22F8081FE04751DC773945B8C5C4750E3C162C58A3A8AA39C57165C508E5A24AA88B125CA3C137303C6F71C2E29811F0D2349FCE690C1CDCB563CC7319C9977AF99513306E3AAE22DE711BA6A225FEE39DE3132ACC4396A02584AA39AE57E35F98A0D431896CE6B162B1D268AA68E97BF880EDA6D75B93BAA7EE0913EF3E4383DC0EB5DAE37A396590DA20BF3667FE65B55F0AD5EF70000C068073EDF51D5C5C55FB388AC92C54ABEA19EC930D16BF1C4E9501929D7657F330A85182DB01FA81E7C8EE6A970E26577374F9E5A849FC5180358E8B97C2E58590EF82223B4562F5D07F30B11EF7438F4351A8A55569BB77D6060DD408C341A3B84059C0855E2BD4B59D1A4693FEE00177B36DC0268D156BFAFA81D994441714726370CA2A9450E83B0CC29C816B0A1F4D84BA77236AA21382A84EE2D5C09C5F49EE001025C01A7E6374955AF038945C6AEEC400FD4740168AEB1994F36657C31F5425782BDCC1917CB905A5F4F137C8496272760EE54489CDA1FC82FF712E9A44E4362726886A3DC85517B2DB632866C30360BFA806B7B7A87905B7C472EC3A72F07B25A18543A672FB59A481A8E51E73CEA5D3B441616AAB97E3A941A016BB193E6F107C79059D037A84FF7DB14DFFA96CE4B1B536AE3244805EDAFB18F91ABCA976BF235F884D13D9763AB8DD08A25D565819C87340271DBC73CEF156899BE22A60C0606AA92200B4B5615AA42616526DC5D68750551686A8563D2D7F1042122C0D398D65314D0C8E5731B310141BB2D1BFD7A97AE7C84F74C3B80A1DA475282EE28A9C726A2D32DE6366398B6DC708B841B5512B34A7036C520CC601F040E4D077FD338300A356C300D8EA2C7FB8660F7FB957F10698083676A60D60029CF1290DA96FDC1772AED5908709391E8E66E30866CD64C5E61A0A171DCFEA1BAB56D8068E7111917BCF6A953C8BAA8D9600E19E23E55F2DCDFBDC2D19943351CAEDEA13C2FCC731103C62212C4160E20E77186BCC545B62D12CBF034E62E62732DE65FB8D3C8358F9ED9FF000A893504A84AB9512544F3503C0426E5797FC195FE258A2C1F071FE4751DC773947DC63FE487C24B239C7C04A8C4B8DA15F0483C1A9545C4C4BA5032D6CC3E22BE208E331CC60A97316E2BC6226E5A5479895A458F388B98E4BB8DAE7712BFF936EFC3223F888D83237894629622C5CC9468EE5CD2B0F934055FCEC3FF006A5281BB2F519AAB58DFC7510B9653EFEE50B9B62C7D15C4180E381D39CB9EE591707030514ED8EDD15C8B0D072C78C821808C65DC634A8D8655D75708358A6A3343D43346E700DB35DE8FCC2A9D0A30BE7E0C0834CAEDC2DA23A18C72852DD03F6F72C92292FB7F9EBA8662F5C28F7162C38CAC97EF8AF514133161A5FF00F60D123518DC48009B50863EDCEDEA3B86EAAB0DCA9AC16F45D95FC45449AD168AC7EE28913DE7FEDC7EDA57C2878F9973D1F726EBE2D5CC64506954532FA0B82B47D32BF32CCBEE59D107053873EDFCCCF38CEC485A7FB818EA5DBB68DE8197054BC605DB321901983E23AB0A11DD5B002B201AB8AE021B6D3A97143DA3E3B996521722755345889025A3D15DEA056359AEE0B43E0DC1A81D0E51C0E2BD6E5072E09ADD85D9B01C408896C74CA5D77EE0BA55B0DB393E798566DDEC16CDF3DC3DA5B64BA6CF8E1EE50185518038E0BEFE6375BB1A13757D28FCC394B498C0C0739DDFB98A3E3084B6AB6E636CDAA6ED6666B2B4B32472AF0F0CADFF00EE27CC475E1B80C654B71935FA817548B6BC42325E1B0F52C7A546CE923863A29C8F770CD035AFB65801687B68669F66AFA9622843D06ECBEF173E41ACB661F5063416C5A0AFA6E33679CCD14051D204981C37B7DDCC3B1858074960D2A0C62B4C0DF31B1D4B2B24BA658D4BA88ADC6A6E4B31281B39B391E31CC6603D37847BE2591B5E23D6B52B2B300E9AE1CC4A6D267ACC083588E602650B0B35DCB4C43814CF9A0009EAE352AB4A57E71162A83382A85DFCC5EF85DD9DAFA35105A7496F5F0C7980356ABB1FBB8259034C4E9AAD61897985763697CEA35ECB877CB3CB4EA593BCB2A0541DDAC01FF28FB55A4DCA6506552C83374DC5BCA173036E0CA731E414717363CEB32E20FC18D6E5F83C22A3761425A4516373730F125B5C61F078099F32E3E0B970F171DC4F0A95123698C4315021042103C578B8FF0093E5DC58BC82A1B9B7F8BA9B477E0E7C3A8C1118DA278631892A3BF048E128F04984AB2611C4308E6099982E36E37328A0833863CC772CF68A5AF55111F2B7589B41BA898B9A414CC10187F88E0CF31C85661D9BC00BF8020F01F80BAFB7D6E6906162C0FB600588301BDC2C0CA0016D16C19AAE1281E98455BB61A17B53FEC42751685CB174B5D974A657DC705B845E5DCB561E0D950B7D1F702131FF001F18208215C236570635CC42528CC2385FDC601DB7928C493C6EE0D97EA98F365D8590701C4A44160D8E157FF663092B2286DCFF0051F818ECFCFF00129EC8696B272BCD4692C060395FFB1CDBB780BDAB04B422E0B95BDDC19D24D841184001FE1EF106AF40A1A2DCC3B68DF0660830640EADA8120C1375D9FDCA2FF298788F836C32335B8CAA560EFD625C358F54DD5D5B7AF5EE196540A030BF5EE0881B0285BB7D42B2542C7F907EC9603B2506DB5EBAFB8A7D752D1C24FF00B112CAF84297F4A020D4430765753F8C4D451D834ED8A609CC777155DBDE8B881553B0B3A03FB83D0BA2D0038F6E2550AA39804B1AEF302DD228FA056C837015A08BF7445590D9FEA1C718E64776EB98F7C43EC0EBEDBFC417EB400BB2CFD50C0046BF63FCF71C36FA5A00D50E251148ACD9E520E1E05D46D760F12E9601282F010C7B06CB3897C7BCB6A5B7588B1E189DFABFE22F15A9D3777108D847024AA980BC7CCCB75ECCE1B4ECBA3D4D9148E54C0B1BA8C7A3323201A053FE654351AD38A8D436963F58EA1000D515F4876EE02E8A01954B6FEE712E5A38203EFB8ADDCC827EA1854C077D40B0B82CD5544637228AAC3A1200DD05F52B4CB06E2E31570AF50E33DCB50045DF245AB6694A7E658BD74F3F2C61AB6710214E552D6FF3031CAE29AE6FBC4554E996A978B268DB4038F23E7996796194BE07F88C7CBF302644E659C532B11775BF9E209862BC3877D240829B2E4167E10CB2853A66E5E62E9F8F64AEAD8A1E3B25331650CCCA00A389677E103E0774F3EA6E795586C8009F7204B23F8470F7A0E48D48D18DE23C30F8324563BF032E65162E6778B98372E9AF065E0DC20626A3FE1CCACC082244F0AA879B8B2E5F8BF14977E562F8A230C0DCDBC0F81FF0751DC773944A8C6313C3B8AE3B8B17C3E19448912318F8288B16997E0C85B43351D35532F51A4F9E23E0905BEA244CEF3E21CF87C189F863EA3BF51C3A9FB4544383607BE01EE53B58641CD6E312DC0E1DF44BDB149A55DB337058BA15B1ED63D7496BB0E10F6E494EEE9A006761FDC6566E852B461A30F05C73DC14959A3FC9FEA3E0DB37ABDADE218355C5C0633DE60C732C1AD83A3E601688DE6C0E5E5D5C5C17B265AB0BEBD40D1548BBD035FF007A842AB0D28B755F0D430C60EAA8C523B80E5D43BF5FA8C0D0149A476FB351E81685DE6F8661530146D8FD18E637D1B156A4EBB8F41734B07B888F4B475ADFDEA5B118AA79A770E4580AA3D45295B0D80731ED258A01AAE07EE3D2828861CF1EA586F95D3B98CC105763C056D66355942DCB2BA826D8AB1E6A3AC6A296CA163A5D1D505FA8F4320D55AB0ECA33EE3AC1EE0CDA7B799918895723577C098B3A9838C00DBA31D98F9A8A4BB05D056ABD78F64622521DD62BE3DC5CBE482C65CBE752B6CBEB298A6EBEE1EB505DCFF4B98D2852BFC4697B30D40C6268292F1EE10551CC2D35B83719C44050D95ADFF3138D128D96B2B31AB852EF0E8782038806CB634AAC02C7043E18434016FBD4A294A19606AC794040E93340C6E54C97C9695F4730DA3A98718256615072EEFB89881C0180EA5CD6CCCBAB675B8470565618E4F1D40DD684774EF7FB8144BD6B03BB7300D46BD46C1D370273495AB6AC7CCD68910C1CBE986EE916CA7FD8447DD73D16EE32241DE4286BDD41BBAF6018FEA5542CD8788098406EAEF4CB3A5C60B1E3700040D1C159710458D9DCDAE557998965D46D476EEA0D0FCCB4BD4E575083FAEA083AA95F8850FB829F51F5CC40D35D5435F2061A43E206A21565CFE21549336F1F728A4E80C27CCB1171070AF170B397032B9DF50DE580311D54CF2CA8969EA7E381929AE06923DB659B83B13AB986216F83C8B4DF5072CAA905CACE186475148B17570734A0A727C31E5CB56593A5850E51B5FF006C529169DBF11BAFA3A7EA6C85569EE5DE8552BBBA8E31D0E4BADC0560547F18C016789EC998CB1C2F4FF69B57742D43E2C7F29F38DF98D631718B18B8B4781CBCB8B83D4A5814CDC370F1B8EFC11979F04DC7C262540F735FFE09736815E16BC2C5F0EE3E0BC10F043C691DC77E0EE3E18C638EE3FE27717C31F866517886FCAD45EA2910FF0073B5CC67DCB711457F11CC5F88E483EA537EE2D7B8989644A82E61055F7348A0A84049EC022900C1B0BF32AB33AB2965C01DAD79040D41A69968B7091842BCFF001A38980ACA32E7B95FB3624B5F3B57895D55817B17EDFF0070B12D8597AF41C42C2963E54B128EE33AC095E5BBB780DC1C1308DA7B3E7517E9A02FB0F9CE60CC080701008E1CD7C31E141403FA7CC4698A60AA96AFE09767516E16B2FBD041E27948AE8F7A980AC585BB5E2EB3EA19298F2D5DFA19934829735A1D689852764D377657AE219DC02D85BCA02406263860E09BFEAE4DFB674209E5EFF300CA502D972036DE5F70BE17CACBE43B8421800C2D29BCBF31072E652F97E587414C5A7A05BDCE7709E6EDE41E22F6311F06B83A5EE39621053681ED581122534528FA2D451F0FC91A72AB6FCCBED0A2CB2F971B73A9465584A83B1EB35BEE15E20AF50D31C22B9789462E6BA68B45ABC4132F12F458BD9C63E265C412354D3C23084574F09B9F7F1EA26B258B37C09C1C9EE35E545AA0BA73F119A7C5379D22615B498EB05DBF50E918D16C5E108BD1AA34D7833AF72A23564E97104CA06A01DBCBF119CB96704BB6E2D00380ECAFCEA5A492265FD5C7FD0225B0F317241459BBEF5102D7CB0E6F79C4B67881578E307F32DA6B306B85FCC2429ED6C739958C56E65F1DB7145B10BA6F211CC58B1C95E76B000B34145564E1D7CC3E4DC28CA0E854561437B17DC6CD1C53513E7AAED66918C0C015ABC97CB361164B3060DE807BB0424E3C0C83919AA24753C7D44420356710466E92EFA22042159ADD57719C3C308DABD4A59B3B6A78AEB70E4FDCA953630154C56182D4BA664A6D2955C41B26B986560732BF1EA0F03106E650802E3996D812EFAE087442AA552C7F4D38548DCBB2172D486ECEA126AA29937EA175F2C1F4AB8601B2C4A69530BD4BA508913F395581AFE3AC9CFCC3E8A902514C4DB1A264BF8771B219D871FF00E952DDE475818B3863485C5DF1B370B9474CB639D7E48E8BBB52717D6A5835F976FAF641B7527C6657D9968321B2F883169F83DC7B292A62CAB48DA05CA367A809719A9DE2A67294B38E72E064FAF72A11DE8D471AC55D9CCAD4AB0B773E5E2FD3C2E5C5BB897F3E52E540950843CB065F9B832E2F8DF8E7C3063A83E160FF0082D45A8C2DC62F84955E03E0C783CB1DC489120CF863394518B147118918F8598B17C0D337073E026371ACBA3A8E6712B98330712D7118957123BCCED51D460F0B1505CD3DF5165D6B3617D041E4B2CEEAFBD40453C5AD438A52982EAB53149396D535725D8380AE3D4B2D57383C13C5B70C98CA2F4AF898C30AC4BED75B8DDDFB838C2A9D98BEAF12DC11A2B22DFD47CF80A05F3FCC64C5B383835DC2881E8D76A93CE07F305206034068964964283077F495A02D1B536AF983EB2D5A860E889DA5903EAA2AE599115F4F9672A920E40CD59F11E26C3600E30BFF00620878652EEF76FD110E0BD583D096B630197849C914862A382F6F7CCB3AE8C5AA3F28C4B3C2F98C0A05837BAAF932C6A28271C0E6DE0ED82A4C1923FCB2E350725BB43FF66DAF82F778D2E88EAE794198655734789F0AA992C00F8D4616ED557602FD541282635DDCBF25DCA126D26FB0F2D03ED8DD3CD5C169F9E1EA023E41321A7A15FB86D6E390A3BA56C2A30CAB56837A1C1E71084458B9416D7A6D85107DA3BA3DEE22220A55798B7CBEBA856FCE9E401F9CC5387D09914A8EEEE052922F4F307BF5A8F5EE8CE2461E12E188D3AAA6D7BA8FC820EC01A1F985E1010E59CACACEE89A823911E7D418D8E33DCF273F30C9E29571B5CFD5404456A846F07B89D6BB0E1730BED33E1515A7134F60338813BED4D0C64AEE2133E5FBFCA33605CE09C6A798F57DA1D296A56D77ECC3BAAB18A750F9A6DB925F07771E9C9B4511E12ACAB829CAC9D82BD372AC1D71884A5443C933683686E842F4010B0AD1115F80935B4516808D0C1BBD61D5F7295040D90AFF0087C175B949A6B6E57BEE55DC02F145EEE2B0A2225E7BF009BB3359BD4B0BAEA0A0854BA3B617571A12E085B5DCE772F15C4016E097A1629D3EDF732E096D1233BAE128C6C6AD1E260511BA406EF4770C688FCD7FE4B37C56C118865DC58E6BA8743EC3CD45C1099B869BD0160B3034B2064A6184AC4D39E910E5627305116245B1DD3142E6CEC3FEE62B314A2CF6D772B0331A14EABA80C5060984ACC6A861F71CFABCFE997CEC8B86666AD9198B39268D53BEA2971B86DF8F51E6869597DB0762B1658125AB1C157043AE0EEB44405A0680EC86809C8C2D130BE63E2A57833DA546DE2AE56604AF17E5FF0007C11FF0BF15E2A6BC243CADC6288A605C713702BCD40A8790A87877FE01DC31DC4A891B41B8C60F03896F8631F02E36E2619BF992A2786FF1DDA8D6992728AAA617123E1B44989921C5844B237E0BEA22306A082C0DA7E313228A85B130D79A5ACAEE64AD12108B5B37D7B4734028E72BCCCEAABE23FDB88AA36CA95EE39CC38663C4763428D01494FF0058305697AB83A6CB280715C9FCCA4D36C942BA731C5EEC2DABB58E8D4301414B200AB3C26DF98A9902EABA463F7134CA2972D7FCFC455DE8155F5BFEA072CBB026E8C9C4C08830D818C7D3158D22F1B16877628D525D5FCDDC4121DA5768C3EEAE29A5175ED81D9A6C523AADA47294037F3DEBFEB83CAED5477A7AD47A8D21037CA98C175087FB6604E83798791A8529A05BBCC22E8802B41E17444A9256D01D006D958106194DDFE261F2E3BB8EDD067110ED16C00B67C05F7F889A028A8A2057D2978E22F531992B02F69773F1AB821925258B326AB85FD95151F804B1A389994E6707E06EF85E23CCBE82D4BA51F3706E1F024E1F1EE5308A0BA9CD7A855852EDCDD7DD4102D8E72EE5E7EA186FD5AC9CD5F2FF0051DA81760B51C03A95BC5397F7159A5D8148F7D41EE82DE1E5F7042E400AAADB949C8077AFC3331F00118319F9FF00C893B773704A6A7566ABBA8C6F0E4AFA28E62ADBEC25B8AF8A812E1BA547C4A965C89AA1CB32CF60505DA7E61552B09D91CB15D7903341AF89BD211236AD7AFEE22C1097EBE6527AFA02A0B2DB271A1AFA8A7F301CBB48768934C5FB950144AD68188576464BA78CC3968236F93E2502283908C14E75999E41EE2E62B1868028F50011837273FF007A8EB3B56E6DDACBE65A329CC45966265BCAC7D41DD6C554D12B6A5DBD91D9435284A5EFB8B9A8CBCCAA8ABBE60E4312B253A239A5B5442E9D600DC00865575C5C4A0F58DACB98D7C0114B9B0FE92C8590F30E5796B32FC145C49EB731099A6CA88801450157F71E7DF94AA735F1181545B3495BB8F1B860A697773BCDCA1427E47FDC4CD1AAA37B21EFF9885107E5095C3195C6A590FF002C2CABECF32A8DA54CAF88055760DD8BE9854B22C94AE483943B760F50EA294CB75CFDEE516BBCB6A11D50C1D87B664C2892C1C403B4006C2F348FAAEB70BAFBFEA151801B29ADC30077D43B25D2A07913C08B894433E119A9502A579A8C4952A544F371815FE35E2BCF11C45F226655CAA8903C81299503C0780AF1B791D781B89122544F062407FC149512244892A54691113C8B1C9E16AD45AD4B19841C1136FC437F30663AF25F41604DE5B2E1142D2A37A1B8CB2531581C421136287F365AD2EC0BFBFEA057C102A8356102D3AA86D1B1AB2B1C405B84AC3D1D1EE3A586DE44BF88005051C06A7647A8965AE2A824A1EA2ED268C1F11057C214B43AA83EE2F68152D73830B6FE22135D54AA6E701AC0613E7EA5400E1D3EFF00538DD8AF4CBF980CBAE91837CBE9C4B54B8CCD7FBCD7DC44E300D99562E5059DD46E68739CCAC50AC9552F1F998704193794EBD6259803369E198F2010686AA57155065C1D37503D5C3D42009EA8A73510054B29B883D2AFFDA390E19B3028772841C4E601E785FD7CC799EA855B7A0BACC43F74E46AD1DBC5B1D5A6D8617159B56AB1CC074B3C5D9A1CBC66261E46D4BB3D53FEE62154137900F4563F1068DDEB99A3814DC16B1A160831FA27B41E40633F7706D22BB2C5E3FEE2639897CA8AEB6772D490B88DD3D9165BA7565C810A2B116809B7D5B708CB180092DFD4AA8550F4B9FF00BDCE34C1155FC7C46E1C80A5B5B7D90C7AA2D056A003821393A7E221C3CE411B17E0FE61185104B5659BDD4169C5B197FEF72FC18ED34FB23E71C2005DE4DEB10FBAA2D1AA364B44094D87DB372030B0C442B801EBEA217AA36ABB33F3098CA8740AF1DC55A320BD0FE8861980029DEE5E92F8D5EE095A09A1BBC7DC5DA79968FF00B9453F030B17994B5B1B61BA84AAA9D8671CD433A07DD47EE257320D1E1D4554DE3DEE55588BF980AF88EEDC21504B24073BA7AF51734712CD6A54C335CCB21326E062EA0A50FDC0B2FBDC2C317D40B4B966371715BF7135439A94C14D72CA483A4BB11D8CB7DC5EDC7A83E53347152EB995C81EA030261A8ABD63D4C5F875259BBCEF729272B41204DCCADB54C263EBA5ECC6527798007CC140B6596BB2695D0C19764A200B5043E19C5B3804C0FCEA5E850D8C3947E66664516E9A7E862E18D975498DF103014A5CB70C701D2540575AF1A2E1E55BCA8E213757BECA60D70A2436EAE1201693B22022F2ABE81169A055739942B232ED620A43C32F11EF1A2302F12B01A28CBB688BA59AD722B8F88E589A670DE1198B2EC2C3DD32B0BA610455CD230AFF0004E32A54A952B32A54612A578A952BFF00D4FF0005E62C5B81E2A067C2BFC4783CBAF23AF07C8988CD23E1891331891331225113C54A8938890047986A55900EE240C41FD436F110AC8238898B886E231805AD7E610ADBB8856B51BF28219A91C5D663FA00897578B8E1B8B37BBDC685392180EEA13F283FBF98BB01251CC151A94352EB7EE080030028220B4C18CD062597E4A71E815F27B99A1216C5318F12B7ED02992F0BF31AF1129D672D91C112FB37AF8842216D5F21D106460BD61C4226D7A106F17ADC1301601ABFFAA056BA59C1F11BE04D4BB3EE6E300317B9962C94FDC4D9B5EE0D560041C64BEE668F19C9EBE2143E00DA15ACF1981EE0A4CADE5DC1D06BF25586B39945393F11A8B51C4B327EFF0088DD551458E1610644468B4A3A845705B485977ADCBDF4EADB808FCE65CB53785AF37EA2E0592C812143869730B36DB7A21D9DF31193EBDD9467EA2E6174723BBBC9171D069E251A095F4081F117317A8B735D40894AC2670F2F333B656434738F7050243819E86FF32B3969030D4685531555A8BC6346C69DCB4D540872E21D93434E4F71CF0AE4854DA7BC4A61F2D6CABDB0B8B181F4DC0306EE98F996F6AC9DC58C31161BCCC6EA383592462A16AC6B711680A561CCAC6BD8931C13282021BA6A222AA5B0BBA7170CDB50E00596B114B4BF515717C4E3AFE22B4E4E6B89A65499815EE5A3C28B65EAE5FC45325D59C4326C0A6FDA5B29C5750801804078F7DC64371E5F88998A9A8AB0118EDA1B02B3EE38AF130D7732BF33B186672C3F533DB1C2AE2446F2CD609138DCE2E08B44212CD288C8620CDAD5563F1321CD576ABFE23BA800344B552D30F52BA8B19686D0C61A89D72E23CAD74BDFA635440034B588C6F1B141C91B6CA59E98E061CB2AF63847E26055E8B53EF3FB972560C51CD4B211779DE7131A0630330A8263500232AC7B97EF9BC33C9D4AE43D0B5F69506AE10524E392329B0890D5A22D9C62316CB48EC4B1E20B24295ABAB8574AEC80CCD47DCAA12AE2CF692C6358AE40DCC436D2B166181B1C80E4D875F88ED32AA5EE295976B7D43115D44AFC4A95B440222E208952B04A898951256E1131E1F3B27518EA6FFCAB12A54751752BFC0EA3AFFF00057F82B3E76F3FFFD9
);  
/*==============================================================*/
/* Table: YukonListEntry                                        */
/*==============================================================*/
create table YukonListEntry (
   EntryID              numeric              not null,
   ListID               numeric              not null,
   EntryOrder           numeric              not null,
   EntryText            varchar(50)          not null,
   YukonDefinitionID    numeric              not null,
   constraint PK_YUKONLISTENTRY primary key (EntryID)
)
go

insert into YukonListEntry values( 0, 0, 0, '(none)', 0 );
insert into YukonListEntry values( 1, 1, 3, 'Email', 1 );
insert into YukonListEntry values( 2, 1, 9, 'Phone Number', 2 );
insert into YukonListEntry values( 3, 1, 5, 'Email to Pager', 1 );
insert into YukonListEntry values( 4, 1, 6, 'Fax Number', 5 );
insert into YukonListEntry values( 5, 1, 7, 'Home Phone', 2 );
insert into YukonListEntry values( 6, 1, 11, 'Work Phone', 2 );
insert into YukonListEntry values( 7, 1, 10, 'Voice PIN', 3 );
insert into YukonListEntry values( 8, 1, 2, 'Cell Phone', 2 );
insert into YukonListEntry values( 9, 1, 4, 'Email to Cell', 1);
insert into YukonListEntry values( 10, 1, 1, 'Call Back Phone', 2);
insert into YukonListEntry values( 11, 1, 8, 'IVR Login', 3 );

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
insert into YukonListEntry values (1722,1023,0,'1900',0);
insert into YukonListEntry values (1723,1023,0,'1910',0);
insert into YukonListEntry values (1724,1023,0,'1920',0);
insert into YukonListEntry values (1725,1023,0,'1930',0);
insert into YukonListEntry values (1726,1023,0,'1940',0);
insert into YukonListEntry values (1727,1023,0,'1950',0);
insert into YukonListEntry values (1728,1023,0,'1960',0);
insert into YukonListEntry values (1729,1023,0,'1970',0);
insert into YukonListEntry values (1730,1023,0,'1980',0);
insert into YukonListEntry values (1731,1023,0,'1990',0);
insert into YukonListEntry values (1732,1023,0,'2000',0);
insert into YukonListEntry values (1733,1023,0,'2010',0);
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

INSERT INTO YukonListEntry VALUES (2000, 1005, 0, 'LCR-6200(RFN)', 1324);
INSERT INTO YukonListEntry VALUES (2001, 1005, 0, 'LCR-6600(RFN)', 1325);
INSERT INTO YukonListEntry VALUES (2002, 1005, 0, 'LCR-6200(ZIGBEE)', 1320); 
INSERT INTO YukonListEntry VALUES (2003, 1005, 0, 'LCR-6200(EXPRESSCOM)', 1321); 
INSERT INTO YukonListEntry VALUES (2004, 1005, 0, 'LCR-6600(ZIGBEE)', 1322); 
INSERT INTO YukonListEntry VALUES (2005, 1005, 0, 'LCR-6600(EXPRESSCOM)', 1323);
INSERT INTO YukonListEntry VALUES (2006, 1005, 0, 'LCR-5000(EXPRESSCOM)', 1302);
INSERT INTO YukonListEntry VALUES (2007, 1005, 0, 'LCR-4000', 1305);
INSERT INTO YukonListEntry VALUES (2008, 1005, 0, 'LCR-4600', 1327);
INSERT INTO YukonListEntry VALUES (2009, 1005, 0, 'LCR-4700', 1328);
INSERT INTO YukonListEntry VALUES (2010, 1005, 0, 'LCR-3000', 1306);
INSERT INTO YukonListEntry VALUES (2011, 1005, 0, 'LCR-3100', 1326);
INSERT INTO YukonListEntry VALUES (2012, 1005, 0, 'LCR-2000', 1307);
INSERT INTO YukonListEntry VALUES (2013, 1005, 0, 'LCR-1000', 1308);
INSERT INTO YukonListEntry VALUES (2014, 1005, 0, 'ExpressStat', 1301);

INSERT INTO YukonListEntry VALUES (2015, 1005, 0, 'Meter', 1303);
INSERT INTO YukonListEntry VALUES (2016, 1005, 0, 'Commercial ExpressStat', 1304);
INSERT INTO YukonListEntry VALUES (2017, 1005, 0, 'LCR-5000(VERSACOM)', 1311);
INSERT INTO YukonListEntry VALUES (2018, 1005, 0, 'ExpressStat Heat Pump', 1313);
INSERT INTO YukonListEntry VALUES (2019, 1005, 0, 'UtilityPRO', 1314);
INSERT INTO YukonListEntry VALUES (2020, 1005, 0, 'LCR-3102', 1315);
INSERT INTO YukonListEntry VALUES (2021, 1005, 0, 'UtilityPRO ZigBee', 1316);

INSERT INTO YukonListEntry VALUES (2022, 1005, 0, 'Digi Gateway', 1317);
INSERT INTO YukonListEntry VALUES (2023, 1005, 0, 'UtilityPRO G2', 1318); 
INSERT INTO YukonListEntry VALUES (2024, 1005, 0, 'UtilityPRO G3', 1319); 

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
)
go

/*==============================================================*/
/* Table: YukonPAObject                                         */
/*==============================================================*/
create table YukonPAObject (
   PAObjectID           numeric              not null,
   Category             varchar(20)          not null,
   PAOClass             varchar(20)          not null,
   PAOName              varchar(60)          not null,
   Type                 varchar(30)          not null,
   Description          varchar(60)          not null,
   DisableFlag          char(1)              not null,
   PAOStatistics        varchar(10)          not null,
   constraint PK_YUKONPAOBJECT primary key (PAObjectID)
)
go

INSERT into YukonPAObject values (0, 'DEVICE', 'SYSTEM', 'System Device', 'SYSTEM', 'Reserved System Device', 'N', '-----'); 

/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique index Indx_PAO on YukonPAObject (
Category ASC,
PAOName ASC,
PAOClass ASC
)
go

/*==============================================================*/
/* Table: YukonPAObjectEncryptionKey                            */
/*==============================================================*/
create table YukonPAObjectEncryptionKey (
   PAObjectID           numeric              not null,
   EncryptionKeyId      numeric              not null,
   constraint PK_YukonPAObjectEncryptionKey primary key (PAObjectID)
)
go

/*==============================================================*/
/* Table: YukonRole                                             */
/*==============================================================*/
create table YukonRole (
   RoleID               numeric              not null,
   RoleName             varchar(120)         not null,
   Category             varchar(60)          not null,
   RoleDescription      varchar(200)         not null,
   constraint PK_YUKONROLE primary key (RoleID)
)
go

/* Application specific roles */
INSERT INTO YukonRole VALUES(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
INSERT INTO YukonRole VALUES(-101,'Tabular Data Console','Application','Access to the Yukon Tabular Data Console (TDC) application');
INSERT INTO YukonRole VALUES(-102,'Trending','Application','Access to the Yukon Trending application');
INSERT INTO YukonRole VALUES(-103,'Commander','Application','Access to the Yukon Commander application');
INSERT INTO YukonRole VALUES(-106,'Billing','Application','Billing. Edit this role from the Yukon SetUp page.');
INSERT INTO YukonRole VALUES(-107,'Esubstation Editor','Application','Access to the Esubstation Drawing Editor application');
INSERT INTO YukonRole VALUES(-108,'Web Client','Application','Access to the Yukon web application');
INSERT INTO YukonRole VALUES(-109,'Reporting','Application','Access to reports generation.');
INSERT INTO YukonRole VALUES(-110,'Password Policy','Application','Handles the password rules and restrictions for a given group.');

/* Web client operator roles */
INSERT INTO YukonRole VALUES(-200,'Administrator','Operator','Access to Yukon administration');
INSERT INTO YukonRole VALUES(-201,'Consumer Info','Operator','Operator access to consumer account information');
INSERT INTO YukonRole VALUES(-202,'Metering','Operator','Operator access to metering');

/* Operator roles */
INSERT INTO YukonRole VALUES(-206,'Esubstation Drawings','Operator','Operator access to esubstation drawings');
INSERT INTO YukonRole VALUES(-207,'Odds For Control','Operator','Operator access to odds for control');

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
)
go

/*==============================================================*/
/* Table: YukonRoleProperty                                     */
/*==============================================================*/
create table YukonRoleProperty (
   RolePropertyID       numeric              not null,
   RoleID               numeric              not null,
   KeyName              varchar(100)         not null,
   DefaultValue         varchar(1000)        not null,
   Description          varchar(1000)        not null,
   constraint PK_YUKONROLEPROPERTY primary key (RolePropertyID)
)
go

/* Database Editor Role */
INSERT INTO YukonRoleProperty VALUES(-10000,-100,'point_id_edit','false','Controls whether point ids can be edited');
INSERT INTO YukonRoleProperty VALUES(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
INSERT INTO YukonRoleProperty VALUES(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
INSERT INTO YukonRoleProperty VALUES(-10005,-100,'utility_id_range','1-254','<description>');
INSERT INTO YukonRoleProperty VALUES(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');
INSERT INTO YukonRoleProperty VALUES(-10008,-100,'permit_login_edit','true','Closes off all access to logins and login groups for non-administrators in the dbeditor');
INSERT INTO YukonRoleProperty VALUES(-10010,-100,'z_optional_product_dev','00000000','This feature is for development purposes only');
INSERT INTO YukonRoleProperty VALUES(-10011,-100,'allow_member_programs','false','Allows member management of LM Direct Programs through the DBEditor');

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

/* Dynamic Billing File Setup */
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

INSERT INTO YukonRoleProperty VALUES(-11001,-110,'Password History','5','The number of different passwords retained before a user can reuse a password.');
INSERT INTO YukonRoleProperty VALUES(-11002,-110,'Minimum Password Length','8','The minimum number of characters a password has to be before it passes.');
INSERT INTO YukonRoleProperty VALUES(-11003,-110,'Minimum Password Age','0','The number of hours a user has to wait before changing their password again.');
INSERT INTO YukonRoleProperty VALUES(-11004,-110,'Maximum Password Age','60','The number of days before a login is expired and needs to be changed.');
INSERT INTO YukonRoleProperty VALUES(-11005,-110,'Lockout Threshold','5','The number of login attempts before an account is locked out.');
INSERT INTO YukonRoleProperty VALUES(-11006,-110,'Lockout Duration','20','The number of minutes a login is disabled when an account is locked out.');

INSERT INTO YukonRoleProperty VALUES(-11050,-110,'Policy Quality Check','3','The number of policy rules that are required to be able to save a password.');
INSERT INTO YukonRoleProperty VALUES(-11051,-110,'Policy Rule - Uppercase Characters','true','Uppercase characters count toward the required policy quality check.  (A, B, C, ... Z)');
INSERT INTO YukonRoleProperty VALUES(-11052,-110,'Policy Rule - Lowercase Characters','true','Lowercase characters count toward the required policy quality check.  (a, b, c, ... z)');
INSERT INTO YukonRoleProperty VALUES(-11053,-110,'Policy Rule - Base 10 Digits','true','Base 10 digits count toward the required policy quality check.  (0, 1, 2, ... 9)');
INSERT INTO YukonRoleProperty VALUES(-11054,-110,'Policy Rule - Nonalphanumeric Characters','true','Nonalphanumic characters count toward the required password rules check.  (~, !, @, #, $, %, ^, &, *, _, -, +, =, `, |, (, ), {, }, [, ], :, ", '', <, >, ,, ., ?, /)');

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
INSERT INTO YukonRoleProperty VALUES(-20152,-201,'Import Customer Account','false','Controls whether to enable the customer account importing feature');
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
INSERT INTO YukonRoleProperty VALUES(-20020,-200,'Network Manager Access','false','Controls access to Network Manager.');

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
INSERT INTO YukonRoleProperty VALUES(-20219,-202,'Meter Events','false','Controls access to Meter Events.');
INSERT INTO YukonRoleProperty VALUES(-20220,-202,'Allow Disconnect Control','true','Controls access to Disconnect, Connect, and Arm operations.');
INSERT INTO YukonRoleProperty VALUES(-20221,-202,'Device Data Monitor','false','Controls access to the Device Data Monitor.');

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
INSERT INTO YukonRoleProperty VALUES (-21311,-213,'Archived Data Analysis','true','Controls access to Archived Data Analysis collection action.');
INSERT INTO YukonRoleProperty VALUES (-21312,-213,'Manage FDR Translations','false','Controls access to FDR Translation Manager bulk operation.');
INSERT INTO YukonRoleProperty VALUES (-21313,-213,'Archived Data Export','true','Controls access to Archived Data Export');

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
INSERT INTO YukonRoleProperty VALUES(-40012,-400,'Grouped Control History Display', 'true', 'Controls whether to enable grouped control history display');
INSERT INTO YukonRoleProperty VALUES(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
INSERT INTO YukonRoleProperty VALUES(-40055,-400,'Opt Out Period',' ','The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)');
INSERT INTO YukonRoleProperty VALUES(-40056,-400,'Opt Out Limits',' ','Contains information on Opt Out limits.');
INSERT INTO YukonRoleProperty VALUES(-40100,-400,'Link FAQ',' ','The customized FAQ link');
INSERT INTO YukonRoleProperty VALUES(-40102,-400,'Link Thermostat Instructions',' ','The customized thermostat instructions link');

INSERT INTO YukonRoleProperty VALUES(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
INSERT INTO YukonRoleProperty VALUES(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-40199,-400,'Sign Out Enabled','true','Allows end-users to see a sign-out link when accessing their account pages.'); 
INSERT INTO YukonRoleProperty VALUES(-40200,-400,'Create Login For Account','false','Allows a new login to be automatically created for each contact on a customer account.'); 
INSERT INTO YukonRoleProperty VALUES(-40201,-400,'Opt Out Force All Devices','false','Controls access to select individual devices or forces all device selection when opting out. When true, individual device selection is unavailable and all devices are forced to opt out.'); 
INSERT INTO YukonRoleProperty VALUES(-40202,-400,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.'); 
INSERT INTO YukonRoleProperty VALUES(-40203,-400,'Enrollment per Device','false','Displays a second web page that allows for enrollment by individual device per program.');
INSERT INTO YukonRoleProperty VALUES(-40300,-400,'Auto Thermostat Mode Enabled','false','Enables auto mode functionality for the account.enrollment by individual device per program.');

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
INSERT INTO YukonRoleProperty VALUES(-70027,-700,'Enable Importer','false','Allows access to the Cap Control importers');

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
INSERT INTO YukonRoleProperty VALUES (-90043,-900,'Allow DR Control','true','Allow control of demand response control areas, scenarios, programs and groups');
INSERT INTO YukonRoleProperty VALUES (-90044,-900,'Asset Availability','false','Controls access to view Asset Availability for Scenarios, Control Areas, Programs, and Load Groups.');

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
create table YukonSelectionList (
   ListID               numeric              not null,
   Ordering             varchar(1)           not null,
   SelectionLabel       varchar(30)          not null,
   WhereIsList          varchar(100)         not null,
   ListName             varchar(40)          not null,
   UserUpdateAvailable  varchar(1)           not null,
   EnergyCompanyId      numeric              not null,
   constraint PK_YUKONSELECTIONLIST primary key (ListID)
)
go

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
)
go

/*==============================================================*/
/* Table: YukonServices                                         */
/*==============================================================*/
create table YukonServices (
   ServiceID            numeric              not null,
   ServiceName          varchar(60)          not null,
   ServiceClass         varchar(100)         not null,
   AppName              varchar(100)         not null,
   constraint PK_YUKSER primary key (ServiceID)
)
go

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
INSERT INTO YukonServices VALUES (20, 'OpcService','classpath:com/cannontech/services/opc/opcService.xml','ServiceManager');

/*==============================================================*/
/* Table: YukonUser                                             */
/*==============================================================*/
create table YukonUser (
   UserID               numeric              not null,
   UserName             nvarchar(64)         not null,
   Password             nvarchar(64)         not null,
   Status               varchar(20)          not null,
   AuthType             varchar(16)          not null,
   LastChangedDate      datetime             not null,
   ForceReset           char(1)              not null,
   UserGroupId          numeric              null,
   constraint PK_YUKONUSER primary key (UserID)
)
go

INSERT INTO YukonUser VALUES (-9999, '(none)', ' ', 'Disabled', 'NONE', '01-JAN-2000', 'N', null);
INSERT INTO YukonUser VALUES (-100, 'DefaultCTI', '3EfsZdxGY7ne6qD6xxKlyqVFesQ8wB157s7OzMHreIkphNoqRAZwPbH58YoH1B+Z', 'Disabled', 'HASH_SHA_V2', '01-JAN-2000', 'Y', -1);
INSERT INTO YukonUser VALUES (-2, 'yukon', '2QDwdtssSTx++kF70rwT1Y4HUmUBZHukDX1VVb27e0ngSxzZOeM4FhFNwsTSVFRd', 'Disabled', 'HASH_SHA_V2', '01-JAN-2000', 'Y', -1);
INSERT INTO YukonUser VALUES (-1, 'admin', '4zMkmR0POZE5ZF7v1B3gVL7XWGs/cG5dQJvDkwltIRvE3XD4Y+rgfQDAcLA+rITO', 'Enabled', 'HASH_SHA_V2', '01-JAN-2000', 'Y', -1);

/*==============================================================*/
/* Index: Indx_YukonUser_Username                               */
/*==============================================================*/
create unique index Indx_YukonUser_Username on YukonUser (
UserName ASC
)
go

/*==============================================================*/
/* Table: YukonWebConfiguration                                 */
/*==============================================================*/
create table YukonWebConfiguration (
   ConfigurationID      numeric              not null,
   LogoLocation         varchar(100)         null,
   Description          varchar(500)         null,
   AlternateDisplayName varchar(100)         null,
   URL                  varchar(100)         null,
   constraint PK_YUKONWEBCONFIGURATION primary key (ConfigurationID)
)
go

INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Cooling','Cool');
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Heating','Heat');
insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');

/*==============================================================*/
/* Table: ZBControlEvent                                        */
/*==============================================================*/
create table ZBControlEvent (
   ZBControlEventId     numeric              not null,
   IntegrationType      varchar(50)          not null,
   StartTime            datetime             not null,
   GroupId              numeric              not null,
   LMControlHistoryId   numeric              null,
   constraint PK_ZBContEvent primary key (ZBControlEventId)
)
go

/*==============================================================*/
/* Table: ZBControlEventDevice                                  */
/*==============================================================*/
create table ZBControlEventDevice (
   ZBControlEventId     numeric              not null,
   DeviceId             numeric              not null,
   DeviceAck            char(1)              not null,
   StartTime            datetime             null,
   StopTime             datetime             null,
   Canceled             char(1)              null,
   constraint PK_ZBContEventDev primary key (ZBControlEventId, DeviceId)
)
go

/*==============================================================*/
/* Table: ZBEndPoint                                            */
/*==============================================================*/
create table ZBEndPoint (
   DeviceId             numeric              not null,
   InstallCode          varchar(255)         not null,
   MacAddress           varchar(255)         not null,
   NodeId               numeric              not null,
   DestinationEndPointId numeric              not null,
   constraint PK_ZBEndPoint primary key (DeviceId)
)
go

/*==============================================================*/
/* Table: ZBGateway                                             */
/*==============================================================*/
create table ZBGateway (
   DeviceId             numeric              not null,
   FirmwareVersion      varchar(255)         null,
   MacAddress           varchar(255)         not null,
   constraint PK_ZBGateway primary key (DeviceId)
)
go

/*==============================================================*/
/* Table: ZBGatewayToDeviceMapping                              */
/*==============================================================*/
create table ZBGatewayToDeviceMapping (
   GatewayId            numeric              not null,
   DeviceId             numeric              not null,
   constraint PK_ZBGateToDeviceMap primary key (GatewayId, DeviceId)
)
go

/*==============================================================*/
/* Table: Zone                                                  */
/*==============================================================*/
create table Zone (
   ZoneId               numeric              not null,
   ZoneName             varchar(255)         not null,
   SubstationBusId      numeric              not null,
   ParentId             numeric              null,
   GraphStartPosition   float                null,
   ZoneType             varchar(40)          not null,
   constraint PK_Zone primary key (ZoneId)
)
go

/*==============================================================*/
/* View: CBCConfiguration2_View                                 */
/*==============================================================*/
create view CBCConfiguration2_View as
SELECT YP.PAOName AS CBCName, D.* 
FROM DynamicCCTwoWayCBC D, YukonPAObject YP
WHERE YP.PAObjectId = D.DeviceId
go

/*==============================================================*/
/* View: CBCConfiguration_View                                  */
/*==============================================================*/
create view CBCConfiguration_View as
SELECT YP.PAOName AS CBCName, YP.PAObjectId AS CBCId, P.PointName AS PointName, P.PointId AS PointId, 
       PD.Value AS PointValue, PD.Timestamp, UOM.UOMName AS UnitOfMeasure
FROM Point P
JOIN YukonPAObject YP ON YP.PAObjectId = P.PAObjectId AND YP.Type like 'CBC 702%'
LEFT OUTER JOIN DynamicPointDispatch PD ON PD.PointId = P.PointId
LEFT OUTER JOIN PointUnit PU ON PU.PointId = P.PointId
LEFT OUTER JOIN UnitMeasure UOM ON UOM.UOMId = PU.UOMId
go

/*==============================================================*/
/* View: CCCBCCVMSState_View                                    */
/*==============================================================*/
create view CCCBCCVMSState_View as
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
LEFT OUTER JOIN State S1 ON S1.StateGroupId = 3 AND DCB.TwoWayCBCState = S1.RawState
go

/*==============================================================*/
/* View: CCCBCInventory_View                                    */
/*==============================================================*/
create view CCCBCInventory_View (CBCNAME, IPADDRESS, SLAVEADDRESS, CONTROLLERTYPE, OPCENTER, REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, CAPBANKNAME, BANKSIZE, OPERATIONMETHOD, LAT, LON, DRIVEDIRECTION, CAPBANKADDRESS, TA, CAPBANKCONFIG, COMMMEDIUM, COMMSTRENGTH, EXTERNALANTENNA, OPERATIONSCOUNTERRESETDATE, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
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
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId
go

/*==============================================================*/
/* View: CCCapInventory_View                                    */
/*==============================================================*/
create view CCCapInventory_View as
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
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId
go

/*==============================================================*/
/* View: CCInventory_View                                       */
/*==============================================================*/
create view CCInventory_View (REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, AREAID, SUBID, SUBBUSID, FDRID, CBCNAME, CBCID, CAPBANKNAME, BANKID, CAPBANKSIZE, DISPLAYORDER, CONTROLSTATUS, CONTROLSTATUSNAME, SWMFGR, SWTYPE, OPERATIONMETHOD, CONTROLLERTYPE, IPADDRESS, SLAVEADDRESS, LAT, LON, DRIVEDIRECTION, OPCENTER, TA, CLOSESEQUENCE, OPENSEQUENCE, LASTOPERATIONTIME, LASTINSPECTIONDATE, LASTMAINTENANCEDATE, MAINTENANCEREQPEND, CAPDISABLED, POTENTIALTRANSFORMER, OTHERCOMMENTS, OPTEAMCOMMENTS, POLENUMBER, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
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
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId
go

/*==============================================================*/
/* View: CCOperationsASent_View                                 */
/*==============================================================*/
create view CCOperationsASent_View as
SELECT LogId, PointId, ActionId, DateTime, Text, FeederId, SubId, AdditionalInfo
FROM CCEventLog
WHERE EventType = 1
AND ActionId > -1
go

/*==============================================================*/
/* View: CCOperationsBConfirmed_View                            */
/*==============================================================*/
create view CCOperationsBConfirmed_View as
SELECT LogId, PointId, ActionId, DateTime, Text, KvarBefore, KvarAfter, KvarChange, CapBankStateInfo
FROM CCEventLog
WHERE EventType = 0
AND ActionId > -1
go

/*==============================================================*/
/* View: CCOperationsCOrphanedConf_View                         */
/*==============================================================*/
create view CCOperationsCOrphanedConf_View as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL3.NextAId IS NULL
GROUP BY EL.LogId
go

/*==============================================================*/
/* View: CCOperationsDSentAndValid_View                         */
/*==============================================================*/
create view CCOperationsDSentAndValid_View as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL2.LogId < EL3.NextAId OR EL3.NextAId IS NULL
GROUP BY EL.LogId
go

/*==============================================================*/
/* View: CCOperationsESentAndAll_View                           */
/*==============================================================*/
create view CCOperationsESentAndAll_View as
SELECT OP.LogId AS OId, MIN(aaa.confid) AS CId 
FROM CCOperationsASent_View OP
LEFT JOIN CCOperationsDSentAndValid_view AAA ON OP.LogId = AAA.OpId
GROUP BY OP.LogId
go

INSERT INTO CCOperationLogCache
SELECT OpId, ConfId 
FROM CCOperationsDSentAndValid_view
WHERE OpId NOT IN (SELECT OperationLogId 
                   FROM CCOperationLogCache);

/*==============================================================*/
/* View: CCOperations_View                                      */
/*==============================================================*/
go
create view CCOperations_View as
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
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId
go

/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create view DISPLAY2WAYDATA_VIEW as
SELECT P.POINTID AS PointID, P.POINTNAME AS PointName, P.POINTTYPE AS PointType, 
       P.SERVICEFLAG AS PointState, PAO.PAOName AS DeviceName, PAO.Type AS DeviceType, 
       PAO.Description AS DeviceCurrentState, PAO.PAObjectID AS DeviceID, '**DYNAMIC**' AS PointValue, 
       '**DYNAMIC**' AS PointQuality, '**DYNAMIC**' AS PointTimeStamp, 
       (SELECT uomname 
        FROM PointUnit PU, UnitMeasure UM 
        WHERE PU.pointId = P.pointId 
        AND PU.uomId = UM.uomId) AS UofM, '**DYNAMIC**' AS Tags
FROM YukonPAObject PAO, POINT P
WHERE PAO.PAObjectID = P.PAObjectID
go

/*==============================================================*/
/* View: ExpressComAddress_View                                 */
/*==============================================================*/
create view ExpressComAddress_View as
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
      (Z.AddressType = 'ZIP' OR Z.AddressId = 0))
go

/*==============================================================*/
/* View: FeederAddress_View                                     */
/*==============================================================*/
create view FeederAddress_View as
select x.LMGroupID, a.Address as FeederAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.FeederID = a.AddressID and ( a.AddressType = 'FEEDER' or a.AddressID = 0 ) )
go

/*==============================================================*/
/* View: FullEventLog_View                                      */
/*==============================================================*/
create view FullEventLog_View (EVENTID, POINTID, EVENTTIMESTAMP, EVENTSEQUENCE, EVENTTYPE, EVENTALARMID, DEVICENAME, POINTNAME, EVENTDESCRIPTION, ADDITIONALINFO, EVENTUSERNAME) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, y.PAOName, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from YukonPAObject y, POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
go

/*==============================================================*/
/* View: FullPointHistory_View                                  */
/*==============================================================*/
create view FullPointHistory_View (POINTID, DEVICENAME, POINTNAME, DATAVALUE, DATATIMESTAMP, DATAQUALITY) as
select r.POINTID, y.PAOName, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from YukonPAObject y, POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID
go

/*==============================================================*/
/* View: GeoAddress_View                                        */
/*==============================================================*/
create view GeoAddress_View as
select x.LMGroupID, a.Address as GeoAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.GeoID = a.AddressID and ( a.AddressType = 'GEO' or a.AddressID = 0 ) )
go

/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID
go

/*==============================================================*/
/* View: LMProgram_View                                         */
/*==============================================================*/
create view LMProgram_View as
select t.DeviceID, t.ControlType, u.ConstraintID, u.ConstraintName, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID
go

/*==============================================================*/
/* View: Peakpointhistory_View                                  */
/*==============================================================*/
create view Peakpointhistory_View as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where VALUE in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by POINTID, VALUE
go

/*==============================================================*/
/* View: PointEventLog_View                                     */
/*==============================================================*/
create view PointEventLog_View (EVENTID, POINTID, EVENTTIMESTAMP, EVENTSEQUENCE, EVENTTYPE, EVENTALARMID, POINTNAME, EVENTDESCRIPTION, ADDITIONALINFO, EVENTUSERNAME) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID
go

/*==============================================================*/
/* View: PointHistory_View                                      */
/*==============================================================*/
create view PointHistory_View (POINTID, POINTNAME, DATAVALUE, DATATIMESTAMP, DATAQUALITY) as
select r.POINTID, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID
go

/*==============================================================*/
/* View: ProgramAddress_View                                    */
/*==============================================================*/
create view ProgramAddress_View as
select x.LMGroupID, a.Address as ProgramAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ProgramID = a.AddressID and ( a.AddressType = 'PROGRAM' or a.AddressID = 0 ) )
go

/*==============================================================*/
/* View: ServiceAddress_View                                    */
/*==============================================================*/
create view ServiceAddress_View as
select x.LMGroupID, a.Address as ServiceAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ServiceProviderID = a.AddressID and ( a.AddressType = 'SERVICE' or a.AddressID = 0 ) )
go

/*==============================================================*/
/* View: SubstationAddress_View                                 */
/*==============================================================*/
create view SubstationAddress_View as
select x.LMGroupID, a.Address as SubstationAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.SubstationID = a.AddressID and ( a.AddressType = 'SUBSTATION' or a.AddressID = 0 ) )
go

/*==============================================================*/
/* View: TempMovedCapBanks_View                                 */
/*==============================================================*/
create view TempMovedCapBanks_View as
SELECT YPF.PAOName TempFeederName, YPF.PAObjectId TempFeederId, YPC.PAOName CapBankName, 
       YPC.PAObjectId CapBankId, FB.ControlOrder, FB.CloseOrder, FB.TripOrder, 
       YPOF.PAOName OriginalFeederName, YPOF.PAObjectId OriginalFeederId 
FROM CCFeederBankList FB, YukonPAObject YPF, YukonPAObject YPC, 
     YukonPAObject YPOF, DynamicCCOriginalParent DCCOP
WHERE FB.DeviceId = YPC.PAObjectId
AND YPC.PAObjectID = DCCOP.PAObjectId
AND FB.FeederId = YPF.PAObjectId 
AND YPOF.PAObjectId = DCCOP.OriginalParentId 
AND DCCOP.OriginalParentId <> 0
go

alter table AccountSite
   add constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
      references SiteInformation (SiteID)
go

alter table AccountSite
   add constraint FK_AccS_CstAd foreign key (StreetAddressID)
      references Address (AddressID)
go

alter table AcctThermostatSchedule
   add constraint FK_AcctThermSch_CustAcct foreign key (AccountId)
      references CustomerAccount (AccountID)
go

alter table AcctThermostatScheduleEntry
   add constraint FK_AccThermSchEnt_AccThermSch foreign key (AcctThermostatScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId)
go

alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go

alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac_ty foreign key (TypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac foreign key (TonnageID)
      references YukonListEntry (EntryID)
go

alter table ApplianceAirConditioner
   add constraint FK_APPLIANC_ISA_CSTLD_APPLIANC foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceBase
   add constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
      references CustomerAccount (AccountID)
go

alter table ApplianceBase
   add constraint FK_APPLIANC_CSTLDTY_C_APPLIANC foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
go

alter table ApplianceBase
   add constraint FK_AppBs_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID)
go

alter table ApplianceBase
   add constraint FK_CsLsEn_ApB foreign key (ManufacturerID)
      references YukonListEntry (EntryID)
go

alter table ApplianceBase
   add constraint FK_CsLsEn_ApB2 foreign key (LocationID)
      references YukonListEntry (EntryID)
go

alter table ApplianceCategory
   add constraint FK_CstLs_ApCt foreign key (CategoryID)
      references YukonListEntry (EntryID)
go

alter table ApplianceCategory
   add constraint FK_YkWC_ApCt foreign key (WebConfigurationID)
      references YukonWebConfiguration (ConfigurationID)
go

alter table ApplianceChiller
   add constraint FK_APPLCHILL_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TONNTRY foreign key (TonnageID)
      references YukonListEntry (EntryID)
go

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst1 foreign key (SecondaryEnergySourceID)
      references YukonListEntry (EntryID)
go

alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst2 foreign key (SwitchOverTypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceDualFuel
   add constraint FK_AppDlF_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STGTWONTRY foreign key (StageTwoTonnageID)
      references YukonListEntry (EntryID)
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STNENTRY foreign key (StageOneTonnageID)
      references YukonListEntry (EntryID)
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGenerator
   add constraint FK_AppGn_YkLst1 foreign key (TransferSwitchTypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGenerator
   add constraint FK_AppGn_YkLst2 foreign key (TransferSwitchMfgID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGenerator
   add constraint FK_AppGen_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst1 foreign key (BinSizeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst2 foreign key (BlowerHorsePowerID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst3 foreign key (BlowerEnergySourceID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst5 foreign key (DryerTypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst6 foreign key (BlowerHeatSourceID)
      references YukonListEntry (EntryID)
go

alter table ApplianceGrainDryer
   add constraint FK_AppGrD_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst1 foreign key (StandbySourceID)
      references YukonListEntry (EntryID)
go

alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst2 foreign key (PumpTypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceHeatPump
   add constraint FK_AppHtP_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst3 foreign key (PumpSizeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst1 foreign key (MeterVoltageID)
      references YukonListEntry (EntryID)
go

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst2 foreign key (MeterLocationID)
      references YukonListEntry (EntryID)
go

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst3 foreign key (IrrigationTypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst6 foreign key (HorsePowerID)
      references YukonListEntry (EntryID)
go

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst5 foreign key (SoilTypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceIrrigation
   add constraint FK_AppIrr_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst4 foreign key (EnergySourceID)
      references YukonListEntry (EntryID)
go

alter table ApplianceStorageHeat
   add constraint FK_AppStHt_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ApplianceStorageHeat
   add constraint FK_AppStHt_YkLst foreign key (StorageTypeID)
      references YukonListEntry (EntryID)
go

alter table ApplianceWaterHeater
   add constraint FK_AppWtHt_YkLst foreign key (NumberOfGallonsID)
      references YukonListEntry (EntryID)
go

alter table ApplianceWaterHeater
   add constraint FK_ApWtrHt_YkLsE foreign key (EnergySourceID)
      references YukonListEntry (EntryID)
go

alter table ApplianceWaterHeater
   add constraint FK_AppWtHt_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table ArchiveDataAnalysisSlot
   add constraint FK_ArcDataAnalSlots_ArcDataAna foreign key (AnalysisId)
      references ArchiveDataAnalysis (AnalysisId)
         on delete cascade
go

alter table ArchiveDataAnalysisSlotValue
   add constraint FK_ArcDataAnalSlotVal_ArcData foreign key (SlotId)
      references ArchiveDataAnalysisSlot (SlotId)
         on delete cascade
go

alter table ArchiveDataAnalysisSlotValue
   add constraint FK_ArchDataAnalSlotVal_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table BaseLine
   add constraint FK_BASELINE_HOLIDAYS foreign key (HolidayScheduleId)
      references HolidaySchedule (HolidayScheduleID)
go

alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID)
go

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
go

alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID)
go

alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID)
go

alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID)
go

alter table CAPBANKADDITIONAL
   add constraint FK_CAPBANKA_CAPBANK foreign key (DeviceID)
      references CAPBANK (DEVICEID)
go

alter table CAPCONTROLAREA
   add constraint FK_CAPCONTR_REFERENCE_YUKONPAO foreign key (AreaID)
      references YukonPAObject (PAObjectID)
go

alter table CAPCONTROLCOMMENT
   add constraint FK_CapContCom_PAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table CAPCONTROLCOMMENT
   add constraint FK_CapContCom_YukonUser foreign key (UserID)
      references YukonUser (UserID)
         on delete set null
go

alter table CAPCONTROLSPECIALAREA
   add constraint FK_CAPCONTR_YUKONPAO2 foreign key (AreaID)
      references YukonPAObject (PAObjectID)
go

alter table CAPCONTROLSUBSTATION
   add constraint FK_CAPCONTR_REF_YUKONPA2 foreign key (SubstationID)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_SWPTID foreign key (SwitchPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_CVOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
go

alter table CCEventLog
   add constraint FK_CCEventLog_Point foreign key (PointID)
      references POINT (POINTID)
         on delete cascade
go

alter table CCFeederBankList
   add constraint FK_CCFeederBankList_CCFeeder foreign key (FeederID)
      references CapControlFeeder (FeederID)
         on delete cascade
go

alter table CCFeederBankList
   add constraint FK_CCFeederBankList_CapBank foreign key (DeviceID)
      references CAPBANK (DEVICEID)
         on delete cascade
go

alter table CCFeederSubAssignment
   add constraint FK_CCFeederSubAssign_CCFeeder foreign key (FeederID)
      references CapControlFeeder (FeederID)
         on delete cascade
go

alter table CCFeederSubAssignment
   add constraint FK_CCFeederSubAssign_CCSubBus foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
         on delete cascade
go

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHSA_SCHEDID foreign key (HolidayScheduleId)
      references HolidaySchedule (HolidayScheduleID)
go

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHolidayStratAssign_PAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table CCHOLIDAYSTRATEGYASSIGNMENT
   add constraint FK_CCHOLIDAY_CAPCONTR foreign key (StrategyId)
      references CapControlStrategy (StrategyID)
go

alter table CCMonitorBankList
   add constraint FK_CCMonBankList_Point foreign key (PointId)
      references POINT (POINTID)
         on delete cascade
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSSA_SCHEDID foreign key (seasonscheduleid)
      references SeasonSchedule (ScheduleID)
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSeasonStratAssign_PAO foreign key (paobjectid)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_ccssa_season foreign key (seasonscheduleid, seasonname)
      references DateOfSeason (SeasonScheduleID, SeasonName)
go

alter table CCSEASONSTRATEGYASSIGNMENT
   add constraint FK_CCSEASON_REFERENCE_CAPCONTR foreign key (strategyid)
      references CapControlStrategy (StrategyID)
go

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSubAreaAssign_CCSubst foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID)
         on delete cascade
go

alter table CCSUBAREAASSIGNMENT
   add constraint FK_CCSubAreaAssignment_CCArea foreign key (AreaID)
      references CAPCONTROLAREA (AreaID)
         on delete cascade
go

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_CAPCONTR2 foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATION (SubstationID)
go

alter table CCSUBSPECIALAREAASSIGNMENT
   add constraint FK_CCSUBSPE_REFERENCE_CAPCONTR foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID)
go

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSubstSubBusList_CCSub foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID)
         on delete cascade
go

alter table CCSUBSTATIONSUBBUSLIST
   add constraint FK_CCSubstSubBusList_CCSubBus foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
         on delete cascade
go

alter table CCStrategyTargetSettings
   add constraint FK_CCStratTarSet_CapContStrat foreign key (StrategyId)
      references CapControlStrategy (StrategyID)
         on delete cascade
go

alter table CCURTACCTEVENT
   add constraint FK_CCURTACC_CCURTPRO foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
go

alter table CCURTACCTEVENTPARTICIPANT
   add constraint FK_CCURTACCTEVENTID foreign key (CCurtAcctEventID)
      references CCURTACCTEVENT (CCurtAcctEventID)
go

alter table CCURTACCTEVENTPARTICIPANT
   add constraint FK_CCURTACC_CICUSTOM foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table CCurtCENotif
   add constraint FK_CCCURTCE_NOTIF_PART foreign key (CCurtCEParticipantID)
      references CCurtCEParticipant (CCurtCEParticipantID)
go

alter table CCurtCEParticipant
   add constraint FK_CCURTCE_PART_CURTEVT foreign key (CCurtCurtailmentEventID)
      references CCurtCurtailmentEvent (CCurtCurtailmentEventID)
go

alter table CCurtCEParticipant
   add constraint FK_CCURTCURTEVENTCICUST_CICUST foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table CCurtCurtailmentEvent
   add constraint FK_CCURTCURTEVT_CCURTPGM foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
go

alter table CCurtEEParticipant
   add constraint FK_CCURTEEPART_CCURTEE foreign key (CCurtEconomicEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID)
go

alter table CCurtEEParticipant
   add constraint FK_CCURTEEPART_CUST foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table CCurtEEParticipantSelection
   add constraint FK_CCURTEEPARTSEL_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID)
go

alter table CCurtEEParticipantSelection
   add constraint FK_CCURTEEPARTSEL_CCURTPART foreign key (CCurtEEParticipantID)
      references CCurtEEParticipant (CCurtEEParticipantID)
         on delete cascade
go

alter table CCurtEEParticipantWindow
   add constraint FK_CCRTEEPRTWIN_CCRTEEPRTSEL foreign key (CCurtEEParticipantSelectionID)
      references CCurtEEParticipantSelection (CCurtEEParticipantSelectionID)
         on delete cascade
go

alter table CCurtEEParticipantWindow
   add constraint FK_CCRTEEPRTWN_CCRTEEPRIWN foreign key (CCurtEEPricingWindowID)
      references CCurtEEPricingWindow (CCurtEEPricingWindowID)
go

alter table CCurtEEPricing
   add constraint FK_CCURTEEPR_CCURTECONEVT foreign key (CCurtEconomicEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID)
         on delete cascade
go

alter table CCurtEEPricingWindow
   add constraint FK_CCURTEEPRWIN_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID)
         on delete cascade
go

alter table CCurtEconomicEvent
   add constraint FK_CCURTEEVT_CCURTPGM foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
go

alter table CCurtEconomicEvent
   add constraint FK_CCURTINITEVT_CCURTECONEVT foreign key (InitialEventID)
      references CCurtEconomicEvent (CCurtEconomicEventID)
go

alter table CCurtEconomicEventNotif
   add constraint FK_CCURTEENOTIF_CCURTEEPARTID foreign key (CCurtEconomicParticipantID)
      references CCurtEEParticipant (CCurtEEParticipantID)
go

alter table CCurtEconomicEventNotif
   add constraint FK_CCURTEENOTIF_CCURTEEPR foreign key (CCurtEEPricingID)
      references CCurtEEPricing (CCurtEEPricingID)
go

alter table CCurtGroupCustomerNotif
   add constraint FK_CCURTGRO_FK_CCURTG_CCURTGRO foreign key (CCurtGroupID)
      references CCurtGroup (CCurtGroupID)
go

alter table CCurtGroupCustomerNotif
   add constraint FK_CCURTGRPCUSTNOTIF_CUST foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table CCurtProgram
   add constraint FK_CCURTPRG_CCURTPRGTYPE foreign key (CCurtProgramTypeID)
      references CCurtProgramType (CCurtProgramTypeID)
go

alter table CCurtProgramGroup
   add constraint FK_CCURTPRGGRP_CCURTGRP foreign key (CCurtGroupID)
      references CCurtGroup (CCurtGroupID)
go

alter table CCurtProgramGroup
   add constraint FK_CCURTPRGGRP_CCURTPRG foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
go

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_CCURTP foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
go

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_NG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go

alter table CCurtProgramParameter
   add constraint FK_CCURTPRGPARAM_CCURTPRGID foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
go

alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID)
go

alter table CICustomerBase
   add constraint FK_CUSTTYPE_ENTRYID foreign key (CiCustType)
      references YukonListEntry (EntryID)
go

alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID)
go

alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go

alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID)
go

alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID)
go

alter table CallReportBase
   add constraint FK_CstAc_ClRpB foreign key (AccountID)
      references CustomerAccount (AccountID)
go

alter table CallReportBase
   add constraint FK_CstELs_ClRB foreign key (CallTypeID)
      references YukonListEntry (EntryID)
go

alter table CapBankToZoneMapping
   add constraint FK_CapBankZoneMap_CapBank foreign key (DeviceId)
      references CAPBANK (DEVICEID)
         on delete cascade
go

alter table CapBankToZoneMapping
   add constraint FK_CapBankZoneMap_Zone foreign key (ZoneId)
      references Zone (ZoneId)
         on delete cascade
go

alter table CapControlFeeder
   add constraint FK_CAPCONTR_VARPTID foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_CAPCONTR_VOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_CAPCONTR_WATTPTID foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID)
go

alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID)
go

alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID)
go

alter table CommandRequestExecResult
   add constraint FK_ComReqExecResult_ComReqExec foreign key (CommandRequestExecId)
      references CommandRequestExec (CommandRequestExecId)
         on delete cascade
go

alter table CommandRequestUnsupported
   add constraint FK_ComReqUnsupp_ComReqExec foreign key (CommandRequestExecId)
      references CommandRequestExec (CommandRequestExecId)
         on delete cascade
go

alter table CommandSchedule
   add constraint FK_CommSche_EnergyComp foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table Contact
   add constraint FK_CONTACT_REF_CNT_A_ADDRESS foreign key (AddressID)
      references Address (AddressID)
go

alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID)
go

alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID)
go

alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go

alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID)
go

alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID)
go

alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
go

alter table Customer
   add constraint FK_Cust_YkLs foreign key (RateScheduleID)
      references YukonListEntry (EntryID)
go

alter table CustomerAccount
   add constraint FK_CUS_CSTA_CUS2 foreign key (AccountSiteID)
      references AccountSite (AccountSiteID)
go

alter table CustomerAccount
   add constraint FK_CstBs_CstAcc foreign key (CustomerID)
      references Customer (CustomerID)
go

alter table CustomerAccount
   add constraint FK_CustAcc_Add foreign key (BillingAddressID)
      references Address (AddressID)
go

alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID)
go

alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID)
go

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID)
go

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID)
go

alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
go

alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID)
go

alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go

alter table CustomerResidence
   add constraint FK_CstRes_AccSt foreign key (AccountSiteID)
      references AccountSite (AccountSiteID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst1 foreign key (ConstructionMaterialID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst10 foreign key (GeneralConditionID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst11 foreign key (SquareFeetID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst2 foreign key (NumberOfOccupantsID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst3 foreign key (InsulationDepthID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst4 foreign key (MainCoolingSystemID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CUSTOMER_REF_CSTRE_YUKONLIS foreign key (MainFuelTypeID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst6 foreign key (DecadeBuiltID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst7 foreign key (OwnershipTypeID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst8 foreign key (ResidenceTypeID)
      references YukonListEntry (EntryID)
go

alter table CustomerResidence
   add constraint FK_CstRes_YkLst9 foreign key (MainHeatingSystemID)
      references YukonListEntry (EntryID)
go

alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go

alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEGROUPMEMBER
   add constraint FK_DeviceGroupMember_DEVICE foreign key (YukonPaoId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table DEVICEGROUPMEMBER
   add constraint FK_DevGrpMember_DeviceGroup foreign key (DeviceGroupID)
      references DeviceGroup (DeviceGroupId)
         on delete cascade
go

alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEREADLOG
   add constraint FK_DEVICERE_FK_DRLOGR_DEVICERE foreign key (DeviceReadRequestLogID)
      references DEVICEREADREQUESTLOG (DeviceReadRequestLogID)
go

alter table DEVICEREADREQUESTLOG
   add constraint FK_DEVICERE_FK_DRREQL_DEVICERE foreign key (DeviceReadJobLogID)
      references DEVICEREADJOBLOG (DeviceReadJobLogID)
go

alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID)
go

alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
go

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
go

alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
go

alter table DYNAMICACCUMULATOR
   add constraint FK_DynamicAccumulator_Point foreign key (POINTID)
      references POINT (POINTID)
         on delete cascade
go

alter table DYNAMICBILLINGFIELD
   add constraint FK_DBF_REF_BFF foreign key (FormatID)
      references BillingFileFormats (FormatID)
         on delete cascade
go

alter table DYNAMICBILLINGFORMAT
   add constraint FK_DYNAMICB_REF_BILLI_BILLINGF foreign key (FormatID)
      references BillingFileFormats (FormatID)
go

alter table DYNAMICCCAREA
   add constraint FK_DynCCArea_CCArea foreign key (AreaID)
      references CAPCONTROLAREA (AreaID)
         on delete cascade
go

alter table DYNAMICCCOPERATIONSTATISTICS
   add constraint FK_DynCCOpStats_YukonPAObject foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table DYNAMICCCSPECIALAREA
   add constraint FK_DynCCSpecial_CCSpecialArea foreign key (AreaID)
      references CAPCONTROLSPECIALAREA (AreaID)
         on delete cascade
go

alter table DYNAMICCCSUBSTATION
   add constraint FK_DynCCSub_CCSub foreign key (SubStationID)
      references CAPCONTROLSUBSTATION (SubstationID)
         on delete cascade
go

alter table DYNAMICCCTWOWAYCBC
   add constraint FK_DynCCTwoWayCbc_DeviceCbc foreign key (DeviceID)
      references DeviceCBC (DEVICEID)
         on delete cascade
go

alter table DYNAMICDEVICESCANDATA
   add constraint FK_DynDeviceScanData_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table DYNAMICPOINTDISPATCH
   add constraint FK_DynamicPointDispatch_Point foreign key (POINTID)
      references POINT (POINTID)
         on delete cascade
go

alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go

alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
go

alter table DeliverySchedule
   add constraint FK_DS_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID)
go

alter table DeliverySchedule
   add constraint FK_DS_REF_YKNLSTNTRY foreign key (ModelID)
      references YukonListEntry (EntryID)
go

alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID)
go

alter table DeviceConfigCategoryItem
   add constraint FK_DevConfCatItem_DevConfCat foreign key (DeviceConfigCategoryId)
      references DeviceConfigCategory (DeviceConfigCategoryId)
         on delete cascade
go

alter table DeviceConfigCategoryMap
   add constraint FK_DevConCatMap_DevCon foreign key (DeviceConfigurationId)
      references DeviceConfiguration (DeviceConfigurationID)
         on delete cascade
go

alter table DeviceConfigCategoryMap
   add constraint FK_DevConfCatMap_DevConfCat foreign key (DeviceConfigCategoryId)
      references DeviceConfigCategory (DeviceConfigCategoryId)
         on delete cascade
go

alter table DeviceConfigDeviceTypes
   add constraint FK_DevConfDevTypes_DevConfig foreign key (DeviceConfigurationId)
      references DeviceConfiguration (DeviceConfigurationID)
         on delete cascade
go

alter table DeviceConfigurationDeviceMap
   add constraint FK_DevConfigDevMap_DevConfig foreign key (DeviceConfigurationId)
      references DeviceConfiguration (DeviceConfigurationID)
         on delete cascade
go

alter table DeviceConfigurationDeviceMap
   add constraint FK_DevConfigDevMap_YukonPao foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID)
go

alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DeviceDataMonitorProcessor
   add constraint FK_DevDataMonProc_DevDataMon foreign key (MonitorId)
      references DeviceDataMonitor (MonitorId)
         on delete cascade
go

alter table DeviceDataMonitorProcessor
   add constraint FK_DevDataMonProc_StateGroup foreign key (StateGroupId)
      references StateGroup (StateGroupId)
go

alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID)
go

alter table DeviceGroup
   add constraint FK_DeviceGroup_DeviceGroup foreign key (ParentDeviceGroupId)
      references DeviceGroup (DeviceGroupId)
go

alter table DeviceGroupComposed
   add constraint FK_DevGroupComp_DevGroup foreign key (DeviceGroupId)
      references DeviceGroup (DeviceGroupId)
         on delete cascade
go

alter table DeviceGroupComposedGroup
   add constraint FK_DevGrpCompGrp_DevGrpComp foreign key (DeviceGroupComposedId)
      references DeviceGroupComposed (DeviceGroupComposedId)
         on delete cascade
go

alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID)
go

alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID)
go

alter table DevicePagingReceiverSettings
   add constraint FK_DevPaRec_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID)
go

alter table DeviceSeries5RTU
   add constraint FK_DeviceSer5RTU_Device foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DeviceTNPPSettings
   add constraint FK_DevTNPP_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Cmd foreign key (CommandID)
      references Command (CommandID)
go

alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID)
go

alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
go

alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
go

alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DigiGateway
   add constraint FK_DigiGate_ZBGate foreign key (DeviceId)
      references ZBGateway (DeviceId)
         on delete cascade
go

alter table DynamicCCCapBank
   add constraint FK_DynCCCapBank_CapBank foreign key (CapBankID)
      references CAPBANK (DEVICEID)
         on delete cascade
go

alter table DynamicCCFeeder
   add constraint FK_DynCCFeeder_CCFeeder foreign key (FeederID)
      references CapControlFeeder (FeederID)
         on delete cascade
go

alter table DynamicCCMonitorBankHistory
   add constraint FK_DynCCMonBankHistory_Point foreign key (PointID)
      references POINT (POINTID)
         on delete cascade
go

alter table DynamicCCMonitorPointResponse
   add constraint FK_DynCCMonPointResp_Point foreign key (PointID)
      references POINT (POINTID)
         on delete cascade
go

alter table DynamicCCOriginalParent
   add constraint FK_DynCCOrigParent_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table DynamicCCSubstationBus
   add constraint FK_DynCCSubBus_CCSubBus foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
         on delete cascade
go

alter table DynamicCalcHistorical
   add constraint FK_DynamicCalcHist_CalcBase foreign key (PointID)
      references CALCBASE (POINTID)
         on delete cascade
go

alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID)
go

alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMControlAreaTrigger (DeviceId, TriggerNumber)
go

alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go

alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID)
go

alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go

alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go

alter table DynamicPAOInfo
   add constraint FK_DynPAOInfo_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go

alter table DynamicPAOStatistics
   add constraint FK_DynPAOStat_PAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table DynamicPointAlarming
   add constraint FK_DynamicPointAlarming_Point foreign key (PointID)
      references POINT (POINTID)
         on delete cascade
go

alter table DynamicTags
   add constraint FK_DynamicTags_Point foreign key (PointID)
      references POINT (POINTID)
         on delete cascade
go

alter table DynamicTags
   add constraint FK_DYNAMICT_REF_DYNTG_TAGS foreign key (TagID)
      references Tags (TagID)
go

alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID)
go

alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID)
go

alter table ECToAccountMapping
   add constraint FK_ECTAcc_CstAcc foreign key (AccountID)
      references CustomerAccount (AccountID)
go

alter table ECToAccountMapping
   add constraint FK_ECTAcc_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table ECToAcctThermostatSchedule
   add constraint FK_ECToAccThermSch_AccThermSch foreign key (AcctThermostatScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId)
         on delete cascade
go

alter table ECToAcctThermostatSchedule
   add constraint FK_ECToAccThermSch_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
go

alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Call foreign key (CallReportID)
      references CallReportBase (CallID)
go

alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table ECToGenericMapping
   add constraint FK_ECTGn_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc2 foreign key (InventoryID)
      references InventoryBase (InventoryID)
go

alter table ECToLMCustomerEventMapping
   add constraint FK_EnCm_ECLmCs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table ECToLMCustomerEventMapping
   add constraint FK_LCsEv_ECLmCs foreign key (EventID)
      references LMCustomerEventBase (EventID)
go

alter table ECToOperatorGroupMapping
   add constraint FK_ECToOpGroupMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table ECToOperatorGroupMapping
   add constraint FK_ECToOpGroupMap_UserGroup foreign key (UserGroupId)
      references UserGroup (UserGroupId)
         on delete cascade
go

alter table ECToResidentialGroupMapping
   add constraint FK_ECToResGroupMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table ECToResidentialGroupMapping
   add constraint FK_ECToResGroupMap_UserGroup foreign key (UserGroupId)
      references UserGroup (UserGroupId)
         on delete cascade
go

alter table ECToRouteMapping
   add constraint FK_ECToRouteMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table ECToRouteMapping
   add constraint FK_ECToRouteMap_Route foreign key (RouteId)
      references Route (RouteID)
         on delete cascade
go

alter table ECToSubstationMapping
   add constraint FK_ECToSubMap_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table ECToSubstationMapping
   add constraint FK_ECToSubMap_Sub foreign key (SubstationId)
      references Substation (SubstationID)
         on delete cascade
go

alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
      references WorkOrderBase (OrderID)
go

alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID)
go

alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID)
go

alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID)
go

alter table EnergyCompanySetting
   add constraint FK_EC_EnergyCompanySetting foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table EstimatedLoadFormulaAssignment
   add constraint FK_FormulaAssign_ApplianceCat foreign key (ApplianceCategoryId)
      references ApplianceCategory (ApplianceCategoryID)
         on delete cascade
go

alter table EstimatedLoadFormulaAssignment
   add constraint FK_FormulaAssign_LmProgramGear foreign key (GearId)
      references LMProgramDirectGear (GearID)
         on delete cascade
go

alter table EstimatedLoadFormulaAssignment
   add constraint FK_FormulaAssign_LoadFormula foreign key (EstimatedLoadFormulaId)
      references EstimatedLoadFormula (EstimatedLoadFormulaId)
         on delete cascade
go

alter table EstimatedLoadFunction
   add constraint FK_EstLoadFunction_LoadFormula foreign key (EstimatedLoadFormulaId)
      references EstimatedLoadFormula (EstimatedLoadFormulaId)
         on delete cascade
go

alter table EstimatedLoadFunction
   add constraint FK_EstLoadFunction_Point foreign key (InputPointId)
      references POINT (POINTID)
go

alter table EstimatedLoadLookupTable
   add constraint FK_EstLoadLookup_LoadFormula foreign key (EstimatedLoadFormulaId)
      references EstimatedLoadFormula (EstimatedLoadFormulaId)
         on delete cascade
go

alter table EstimatedLoadLookupTable
   add constraint FK_EstLoadLookup_Point foreign key (InputPointId)
      references POINT (POINTID)
go

alter table EstimatedLoadTableEntry
   add constraint FK_EstLoadTableEntry_LookupTab foreign key (EstimatedLoadLookupTableId)
      references EstimatedLoadLookupTable (EstimatedLoadLookupTableId)
         on delete cascade
go

alter table EventAccount
   add constraint FK_EVENTACCT_CUSTACCT foreign key (AccountID)
      references CustomerAccount (AccountID)
go

alter table EventAccount
   add constraint FK_EVENTACCT_EVNTBSE foreign key (EVENTID)
      references EventBase (EventID)
go

alter table EventBase
   add constraint FK_EVNTBSE_ACTNTRY foreign key (ActionID)
      references YukonListEntry (EntryID)
go

alter table EventBase
   add constraint FK_EVNTBSE_SYSCATNTRY foreign key (SystemCategoryID)
      references YukonListEntry (EntryID)
go

alter table EventBase
   add constraint FK_EVNTBSE_YUKUSR foreign key (UserID)
      references YukonUser (UserID)
go

alter table EventInventory
   add constraint FK_EVENTINV_EVNTBSE foreign key (EventID)
      references EventBase (EventID)
go

alter table EventInventory
   add constraint FK_EVENTINV_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID)
go

alter table EventWorkOrder
   add constraint FK_EVENTWO_EVNTBSE foreign key (EventID)
      references EventBase (EventID)
go

alter table EventWorkOrder
   add constraint FK_EVENTWO_WOBASE foreign key (OrderID)
      references WorkOrderBase (OrderID)
go

alter table ExtToYukonMessageIdMapping
   add constraint FK_ExtToYukMessIdMap_YukonUser foreign key (UserId)
      references YukonUser (UserID)
         on delete cascade
go

alter table ExtraPaoPointAssignment
   add constraint FK_ExtraPAOPointAsgmt_Point foreign key (PointId)
      references POINT (POINTID)
         on delete cascade
go

alter table ExtraPaoPointAssignment
   add constraint FK_ExtraPAOPointAsgmt_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceId)
go

alter table FDRTranslation
   add constraint SYS_C0015066 foreign key (PointId)
      references POINT (POINTID)
go

alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go

alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID)
go

alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go

alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go

alter table GroupPaoPermission
   add constraint FK_GroupPaoPerm_PAO foreign key (PaoId)
      references YukonPAObject (PAObjectID)
go

alter table GroupPaoPermission
   add constraint FK_GroupPaoPerm_UserGroup foreign key (UserGroupId)
      references UserGroup (UserGroupId)
go

alter table ImportPendingComm
   add constraint FK_ImpPC_PAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
go

alter table InventoryBase
   add constraint FK_CUS_CSTA_CUS3 foreign key (AccountID)
      references CustomerAccount (AccountID)
go

alter table InventoryBase
   add constraint FK_CUS_HRDI_HAR2 foreign key (InstallationCompanyID)
      references ServiceCompany (CompanyID)
go

alter table InventoryBase
   add constraint FK_INVENTOR_REF_CSTLS_YUKONLIS foreign key (CategoryID)
      references YukonListEntry (EntryID)
go

alter table InventoryBase
   add constraint FK_Dev_InvB foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table InventoryBase
   add constraint FK_InvB_YkLstEvlt foreign key (VoltageID)
      references YukonListEntry (EntryID)
go

alter table InventoryConfigTask
   add constraint FK_InvConfTask_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table InventoryConfigTask
   add constraint FK_InvConfTask_YukonUser foreign key (UserId)
      references YukonUser (UserID)
         on delete cascade
go

alter table InventoryConfigTaskItem
   add constraint FK_InvConfTaskItem_InvBase foreign key (InventoryId)
      references InventoryBase (InventoryID)
         on delete cascade
go

alter table InventoryConfigTaskItem
   add constraint FK_InvConfTaskItem_InvConfTask foreign key (InventoryConfigTaskId)
      references InventoryConfigTask (InventoryConfigTaskId)
         on delete cascade
go

alter table InventoryToAcctThermostatSch
   add constraint FK_InvToAccThermSch_AccThermSc foreign key (AcctThermostatScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId)
go

alter table InventoryToAcctThermostatSch
   add constraint FK_InvToAcctThermSch_InvBase foreign key (InventoryId)
      references InventoryBase (InventoryID)
go

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_INVENBASE foreign key (InventoryID)
      references InventoryBase (InventoryID)
go

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_WAREHOUSE foreign key (WarehouseID)
      references Warehouse (WarehouseID)
go

alter table Invoice
   add constraint FK_INVC_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID)
go

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_INVC foreign key (InvoiceID)
      references Invoice (InvoiceID)
go

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID)
go

alter table JOB
   add constraint FK_Job_YukonUser foreign key (UserID)
      references YukonUser (UserID)
go

alter table JOBPROPERTY
   add constraint FK_JobProperty_Job foreign key (JobID)
      references JOB (JobID)
         on delete cascade
go

alter table JOBSCHEDULEDONETIME
   add constraint FK_JobScheduledOneTime_Job foreign key (JobID)
      references JOB (JobID)
         on delete cascade
go

alter table JOBSCHEDULEDREPEATING
   add constraint FK_JOBSCHED_REFERENCE_JOB foreign key (JobID)
      references JOB (JobID)
         on delete cascade
go

alter table JOBSTATUS
   add constraint FK_JobStatus_Job foreign key (JobID)
      references JOB (JobID)
         on delete cascade
go

alter table LMBeatThePeakGear
   add constraint FK_BTPGear_LMProgramDirectGear foreign key (GearId)
      references LMProgramDirectGear (GearID)
         on delete cascade
go

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID)
go

alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DeviceID)
go

alter table LMConfigurationExpressCom
   add constraint FK_LMCfgXcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID)
go

alter table LMConfigurationSA205
   add constraint FK_LmCf2_LmCBs foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID)
go

alter table LMConfigurationSA305
   add constraint FK_LMCfg305_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID)
go

alter table LMConfigurationSASimple
   add constraint FK_LMCfgS_LMCfgB foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID)
go

alter table LMConfigurationVersacom
   add constraint FK_LMCfgVcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID)
go

alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID)
go

alter table LMControlAreaTrigger
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DeviceId)
      references LMControlArea (DEVICEID)
go

alter table LMControlAreaTrigger
   add constraint FK_Point_LMCntlArTrig foreign key (PointId)
      references POINT (POINTID)
go

alter table LMControlAreaTrigger
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PeakPointId)
      references POINT (POINTID)
go

alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go

alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID)
go

alter table LMControlScenarioProgram
   add constraint FK_LMCONTRO_REF_LMSCP_LMPROGRA foreign key (ProgramID)
      references LMPROGRAM (DeviceID)
go

alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID)
go

alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID)
go

alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE foreign key (EventTypeID)
      references YukonListEntry (EntryID)
go

alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE_a foreign key (ActionID)
      references YukonListEntry (EntryID)
go

alter table LMDirectCustomerList
   add constraint FK_LMDirCustList_CICustBase foreign key (CustomerID)
      references CICustomerBase (CustomerID)
         on delete cascade
go

alter table LMDirectCustomerList
   add constraint FK_LMDirCustList_LMProgDir foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
         on delete cascade
go

alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go

alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID)
go

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID)
go

alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table LMEnergyExchangeCustomerReply
   add constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
         on delete cascade
go

alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
         on delete cascade
go

alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
go

alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID)
go

alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID)
go

alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID)
go

alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID)
go

alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID)
go

alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID)
go

alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID)
go

alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID)
go

alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID)
go

alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID)
go

alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID)
go

alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID)
go

alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID)
go

alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID)
go

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID)
go

alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID)
go

alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID)
go

alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID)
go

alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID)
go

alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
go

alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID)
go

alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID)
go

alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID)
go

alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID)
go

alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID)
go

alter table LMGroupSep
   add constraint FK_LMGroupSep_LMGroup foreign key (DeviceId)
      references LMGroup (DeviceID)
         on delete cascade
go

alter table LMGroupSepDeviceClass
   add constraint FK_LMGroupSepDevClass_LMGrpSep foreign key (DeviceId)
      references LMGroupSep (DeviceId)
         on delete cascade
go

alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID)
go

alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID)
go

alter table LMHardwareBase
   add constraint FK_LMHrdB_Rt foreign key (RouteID)
      references Route (RouteID)
go

alter table LMHardwareBase
   add constraint FK_LMHARDWA_REF_CSTLS_YUKONLIS foreign key (LMHardwareTypeID)
      references YukonListEntry (EntryID)
go

alter table LMHardwareBase
   add constraint FK_LMHrdB_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID)
go

alter table LMHardwareBase
   add constraint FK_LMHARDWA_ISA_CSTHR_INVENTOR foreign key (InventoryID)
      references InventoryBase (InventoryID)
go

alter table LMHardwareConfiguration
   add constraint FK_LMH_CSTL_CUS2 foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go

alter table LMHardwareConfiguration
   add constraint FK_LMHARDWA_LMHRD_LMH_LMHARDWA foreign key (InventoryID)
      references LMHardwareBase (InventoryID)
go

alter table LMHardwareConfiguration
   add constraint FK_LMHrd_LMGr foreign key (AddressingGroupID)
      references LMGroup (DeviceID)
go

alter table LMHardwareEvent
   add constraint FK_IvB_LMHrEv foreign key (InventoryID)
      references InventoryBase (InventoryID)
go

alter table LMHardwareEvent
   add constraint FK_LmHrEv_LmCsEv foreign key (EventID)
      references LMCustomerEventBase (EventID)
go

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_LMHARDBASE foreign key (LMHardwareInventoryID)
      references LMHardwareBase (InventoryID)
go

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_METERHARDBASE foreign key (MeterInventoryID)
      references MeterHardwareBase (InventoryID)
go

alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleId)
go

alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID)
go

alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID)
go

alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
go

alter table LMProgramConstraints
   add constraint FK_HlSc_LmPrC foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go

alter table LMProgramConstraints
   add constraint FK_SesSch_LmPrC foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID)
go

alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go

alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go

alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID)
         on delete cascade
go

alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go

alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go

alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go

alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID)
go

alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID)
go

alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go

alter table LMProgramEvent
   add constraint FK_CstAc_LMPrEv foreign key (AccountID)
      references CustomerAccount (AccountID)
go

alter table LMProgramEvent
   add constraint FK_LMPrEv_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID)
go

alter table LMProgramEvent
   add constraint FK_LmCsEv_LmPrEv foreign key (EventID)
      references LMCustomerEventBase (EventID)
go

alter table LMProgramGearHistory
   add constraint FK_LMProgGearHist_LMProgHist foreign key (LMProgramHistoryId)
      references LMProgramHistory (LMProgramHistoryId)
go

alter table LMProgramWebPublishing
   add constraint FK_CsLEn_LPWbP foreign key (ChanceOfControlID)
      references YukonListEntry (EntryID)
go

alter table LMProgramWebPublishing
   add constraint FK_LMPrgW_LMPr foreign key (DeviceID)
      references LMPROGRAM (DeviceID)
go

alter table LMProgramWebPublishing
   add constraint FK_LMprApp_App foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
go

alter table LMProgramWebPublishing
   add constraint FK_YkWC_LMPrWPb foreign key (WebsettingsID)
      references YukonWebConfiguration (ConfigurationID)
go

alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID)
go

alter table LMThermostatManualEvent
   add constraint FK_CsLsE_LThMnO2 foreign key (FanOperationID)
      references YukonListEntry (EntryID)
go

alter table LMThermostatManualEvent
   add constraint FK_CsLsE_LThMnO1 foreign key (OperationStateID)
      references YukonListEntry (EntryID)
go

alter table LMThermostatManualEvent
   add constraint FK_LMTh_InvB foreign key (InventoryID)
      references InventoryBase (InventoryID)
go

alter table LMThermostatManualEvent
   add constraint FK_LmThrS_LmCstEv foreign key (EventID)
      references LMCustomerEventBase (EventID)
go

alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID)
go

alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID)
go

alter table MACSchedule
   add constraint FK_MACSCHED_HOLIDAYS foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID)
go

alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleId)
      references YukonPAObject (PAObjectID)
go

alter table MACSimpleSchedule
   add constraint FK_MACSimpSch_PAO foreign key (TargetPAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table MACSimpleSchedule
   add constraint FK_MACSimpSch_MACSch foreign key (ScheduleID)
      references MACSchedule (ScheduleId)
go

alter table MCTBroadCastMapping
   add constraint FK_MCTBCM_Device_MCTId foreign key (MctID)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID)
go

alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID)
go

alter table MSPInterface
   add constraint FK_Intrfc_Vend foreign key (VendorID)
      references MSPVendor (VendorID)
go

alter table MeterHardwareBase
   add constraint FK_METERHARD_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID)
go

alter table MeterHardwareBase
   add constraint FK_METERHARD_YUKONLSTNTRY foreign key (MeterTypeID)
      references YukonListEntry (EntryID)
go

alter table MspLMInterfaceMapping
   add constraint FK_MspLMInterMap_YukonPAObj foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go

alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
go

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID)
go

alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID)
go

alter table OptOutEvent
   add constraint FK_OptOutEvent_CustAcct foreign key (CustomerAccountId)
      references CustomerAccount (AccountID)
         on delete set null
go

alter table OptOutSurvey
   add constraint FK_OptOutSurv_Surv foreign key (SurveyId)
      references Survey (SurveyId)
         on delete cascade
go

alter table OptOutSurveyProgram
   add constraint FK_OptOutSurvProg_LMProg foreign key (DeviceId)
      references LMPROGRAM (DeviceID)
         on delete cascade
go

alter table OptOutSurveyProgram
   add constraint FK_OptOutSurvProg_OptOutSurv foreign key (OptOutSurveyId)
      references OptOutSurvey (OptOutSurveyId)
         on delete cascade
go

alter table OptOutSurveyResult
   add constraint FK_OptOutSurvRes_OptOutEventLo foreign key (OptOutEventLogId)
      references OptOutEventLog (OptOutEventLogId)
         on delete cascade
go

alter table OptOutSurveyResult
   add constraint FK_OptOutSurvRes_SurvRes foreign key (SurveyResultId)
      references SurveyResult (SurveyResultId)
         on delete cascade
go

alter table OptOutTemporaryOverride
   add constraint FK_OptOutTempOver_LMProgWebPub foreign key (ProgramId)
      references LMProgramWebPublishing (ProgramID)
         on delete cascade
go

alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID)
go

alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
go

alter table PAOExclusion
   add constraint FK_PAOEXCLU_REF_PAOEX_YUKONPAO foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID)
go

alter table PAOProperty
   add constraint FK_PAOProp_YukonPAO foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table PAOScheduleAssignment
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOSchedule (ScheduleID)
go

alter table PAOScheduleAssignment
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
go

alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID)
go

alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID)
go

alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
go

alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references StateGroup (StateGroupId)
go

alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID)
go

alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID)
go

alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID)
go

alter table POINTPROPERTYVALUE
   add constraint FK_POINTPRO_REFERENCE_POINT foreign key (PointID)
      references POINT (POINTID)
go

alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID)
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERTRIGGERPT_PT foreign key (TriggerID)
      references POINT (POINTID)
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERVERIF_PT foreign key (VerificationID)
      references POINT (POINTID)
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGER_PT foreign key (PointID)
      references POINT (POINTID)
go

alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID)
go

alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID)
go

alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PROFILEPEAKRESULT
   add constraint FK_PROFILEPKRSLT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table PasswordHistory
   add constraint FK_PassHist_YukonUser foreign key (UserId)
      references YukonUser (UserID)
         on delete cascade
go

alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID)
go

alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID)
go

alter table PointControl
   add constraint FK_PointCont_Point foreign key (PointId)
      references POINT (POINTID)
         on delete cascade
go

alter table PointStatusControl
   add constraint FK_PointStatusCont_PointCont foreign key (PointId)
      references PointControl (PointId)
         on delete cascade
go

alter table PointToZoneMapping
   add constraint FK_PointZoneMap_Point foreign key (PointId)
      references POINT (POINTID)
         on delete cascade
go

alter table PointToZoneMapping
   add constraint FK_PointZoneMap_Zone foreign key (ZoneId)
      references Zone (ZoneId)
         on delete cascade
go

alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PorterResponseMonitor
   add constraint FK_PortRespMon_StateGroup foreign key (StateGroupId)
      references StateGroup (StateGroupId)
go

alter table PorterResponseMonitorErrorCode
   add constraint FK_PortRespMonErr_PortRespMonR foreign key (RuleId)
      references PorterResponseMonitorRule (RuleId)
         on delete cascade
go

alter table PorterResponseMonitorRule
   add constraint FK_PortRespMonRule_PortRespMon foreign key (MonitorId)
      references PorterResponseMonitor (MonitorId)
         on delete cascade
go

alter table ProgramToSeasonalProgram
   add constraint FK_ProgSeaProg_LMProgWebPub_AP foreign key (AssignedProgramId)
      references LMProgramWebPublishing (ProgramID)
         on delete cascade
go

alter table ProgramToSeasonalProgram
   add constraint FK_ProgSeaProg_LMProgWebPub_SP foreign key (SeasonalProgramId)
      references LMProgramWebPublishing (ProgramID)
go

alter table PurchasePlan
   add constraint FK_PRCHSPL_REF_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table RFNAddress
   add constraint FK_RFNAdd_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table RPHTag
   add constraint FK_RPHTag_RPH foreign key (ChangeId)
      references RAWPOINTHISTORY (CHANGEID)
         on delete cascade
go

alter table RawPointHistoryDependentJob
   add constraint FK_RPHDependentJob_Job foreign key (JobId)
      references JOB (JobID)
go

alter table Regulator
   add constraint FK_Reg_PAO foreign key (RegulatorId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table RegulatorToZoneMapping
   add constraint FK_ZoneReg_Reg foreign key (RegulatorId)
      references Regulator (RegulatorId)
         on delete cascade
go

alter table RegulatorToZoneMapping
   add constraint FK_ZoneReg_Zone foreign key (ZoneId)
      references Zone (ZoneId)
         on delete cascade
go

alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID)
go

alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table ReportedAddressExpressCom
   add constraint FK_RepAddExpressCom_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table ReportedAddressRelayExpressCom
   add constraint FK_RepAddRelayExp_RepAddExpCom foreign key (ChangeId)
      references ReportedAddressExpressCom (ChangeId)
         on delete cascade
go

alter table ReportedAddressSep
   add constraint FK_ReportedAddressSep_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID)
go

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID)
go

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID)
go

alter table ScheduleTimePeriod
   add constraint FK_SCHDTMPRD_REF_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID)
go

alter table ScheduledGrpCommandRequest
   add constraint FK_SchGrpComReq_Job foreign key (JobId)
      references JOB (JobID)
go

alter table ServiceCompany
   add constraint FK_CstAdd_SrC foreign key (AddressID)
      references Address (AddressID)
go

alter table ServiceCompany
   add constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
      references Contact (ContactID)
go

alter table ServiceCompanyDesignationCode
   add constraint FK_ServCompDesCode_ServComp foreign key (ServiceCompanyID)
      references ServiceCompany (CompanyID)
         on delete cascade
go

alter table Shipment
   add constraint FK_SHPMNT_WRHSE foreign key (WarehouseID)
      references Warehouse (WarehouseID)
go

alter table SiteInformation
   add constraint FK_Sub_Si foreign key (SubstationID)
      references Substation (SubstationID)
go

alter table State
   add constraint FK_YkIm_St foreign key (ImageId)
      references YukonImage (ImageID)
go

alter table State
   add constraint SYS_C0013342 foreign key (StateGroupId)
      references StateGroup (StateGroupId)
go

alter table StaticPAOInfo
   add constraint FK_StatPAOInfo foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table StatusPointMonitor
   add constraint FK_StatPointMon_StateGroup foreign key (StateGroupId)
      references StateGroup (StateGroupId)
go

alter table StatusPointMonitorProcessor
   add constraint FK_StatPointMonProc_StatPointM foreign key (StatusPointMonitorId)
      references StatusPointMonitor (StatusPointMonitorId)
         on delete cascade
go

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_RteID foreign key (RouteID)
      references Route (RouteID)
         on delete cascade
go

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_SubID foreign key (SubstationID)
      references Substation (SubstationID)
go

alter table Survey
   add constraint FK_Surv_EC foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
         on delete cascade
go

alter table SurveyQuestion
   add constraint FK_SurvQuest_Surv foreign key (SurveyId)
      references Survey (SurveyId)
         on delete cascade
go

alter table SurveyQuestionAnswer
   add constraint FK_SurvQuestAns_SurvQuest foreign key (SurveyQuestionId)
      references SurveyQuestion (SurveyQuestionId)
         on delete cascade
go

alter table SurveyResult
   add constraint FK_SurvRes_CustAcct foreign key (AccountId)
      references CustomerAccount (AccountID)
         on delete set null
go

alter table SurveyResult
   add constraint FK_SurvRes_Surv foreign key (SurveyId)
      references Survey (SurveyId)
         on delete cascade
go

alter table SurveyResultAnswer
   add constraint FK_SurvResAns_SurQuestAns foreign key (SurveyQuestionAnswerId)
      references SurveyQuestionAnswer (SurveyQuestionAnswerId)
go

alter table SurveyResultAnswer
   add constraint FK_SurvResAns_SurvQuest foreign key (SurveyQuestionId)
      references SurveyQuestion (SurveyQuestionId)
go

alter table SurveyResultAnswer
   add constraint FK_SurvResAns_SurvRes foreign key (SurveyResultId)
      references SurveyResult (SurveyResultId)
         on delete cascade
go

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM)
go

alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
go

alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID)
go

alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID)
go

alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID)
go

alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID)
go

alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID)
go

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_DISPLAY foreign key (DisplayNum)
      references DISPLAY (DISPLAYNUM)
go

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_TEMPLATE foreign key (TemplateNum)
      references TEMPLATE (TEMPLATENUM)
go

alter table ThemeProperty
   add constraint FK_ThemeProperty_Theme foreign key (ThemeId)
      references Theme (ThemeId)
         on delete cascade
go

alter table ThermostatEventHistory
   add constraint FK_ThermEventHist_AcctThermSch foreign key (ScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId)
         on delete set null
go

alter table ThermostatEventHistory
   add constraint FK_ThermEventHist_InvBase foreign key (ThermostatId)
      references InventoryBase (InventoryID)
         on delete cascade
go

alter table UserGroupToYukonGroupMapping
   add constraint FK_UserGrpYukGrpMap_UserGroup foreign key (UserGroupId)
      references UserGroup (UserGroupId)
         on delete cascade
go

alter table UserGroupToYukonGroupMapping
   add constraint FK_UserGrpYukGrpMap_YukGrp foreign key (GroupId)
      references YukonGroup (GroupID)
         on delete cascade
go

alter table UserPage
   add constraint FK_UserPage_YukonUser foreign key (UserId)
      references YukonUser (UserID)
         on delete cascade
go

alter table UserPageParam
   add constraint FK_UserPageParam_UserPage foreign key (UserPageId)
      references UserPage (UserPageId)
         on delete cascade
go

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YKUSR_YUKONUSE foreign key (UserID)
      references YukonUser (UserID)
go

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YUKPA_YUKONPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID)
go

alter table UserPreference
   add constraint FK_UserPreference_YukonUser foreign key (UserId)
      references YukonUser (UserID)
         on delete cascade
go

alter table UserSubscription
   add constraint FK_UserSubscription_YukonUser foreign key (UserId)
      references YukonUser (UserID)
         on delete cascade
go

alter table VersacomRoute
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references Route (RouteID)
go

alter table Warehouse
   add constraint FK_WAREHOUSE_ADDRESS foreign key (AddressID)
      references Address (AddressID)
go

alter table Warehouse
   add constraint FK_WAREHOUSE_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

alter table WorkOrderBase
   add constraint FK_CsLsE_WkB_c foreign key (CurrentStateID)
      references YukonListEntry (EntryID)
go

alter table WorkOrderBase
   add constraint FK_CsLsE_WkB foreign key (WorkTypeID)
      references YukonListEntry (EntryID)
go

alter table WorkOrderBase
   add constraint Ref_WrkOB_CstAc foreign key (AccountID)
      references CustomerAccount (AccountID)
go

alter table WorkOrderBase
   add constraint FK_WrkOr_SrvC foreign key (ServiceCompanyID)
      references ServiceCompany (CompanyID)
go

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID)
go

alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
go

alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID)
go

alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID)
go

alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID)
go

alter table YukonSelectionList
   add constraint FK_YukonSelList_EnergyComp foreign key (EnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
go

alter table YukonUser
   add constraint FK_YukonUser_UserGroup foreign key (UserGroupId)
      references UserGroup (UserGroupId)
go

alter table ZBControlEvent
   add constraint FK_ZBContEvent_LMContHist foreign key (LMControlHistoryId)
      references LMControlHistory (LMCtrlHistID)
go

alter table ZBControlEvent
   add constraint FK_ZBContEvent_LMGroup foreign key (GroupId)
      references LMGroup (DeviceID)
         on delete cascade
go

alter table ZBControlEventDevice
   add constraint FK_ZBContEventDev_ZBContEvent foreign key (ZBControlEventId)
      references ZBControlEvent (ZBControlEventId)
         on delete cascade
go

alter table ZBControlEventDevice
   add constraint FK_ZBContEventDev_ZBEndPoint foreign key (DeviceId)
      references ZBEndPoint (DeviceId)
         on delete cascade
go

alter table ZBEndPoint
   add constraint FK_ZBEndPoint_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
go

alter table ZBGateway
   add constraint FK_ZBGate_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
go

alter table ZBGatewayToDeviceMapping
   add constraint FK_ZBGateDeviceMap_ZBEndPoint foreign key (DeviceId)
      references ZBEndPoint (DeviceId)
         on delete cascade
go

alter table ZBGatewayToDeviceMapping
   add constraint FK_ZBGateDeviceMap_ZBGate foreign key (GatewayId)
      references ZBGateway (DeviceId)
         on delete cascade
go

alter table Zone
   add constraint FK_ZONE_CapContSubBus foreign key (SubstationBusId)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID)
         on delete cascade
go

alter table Zone
   add constraint FK_Zone_Zone foreign key (ParentId)
      references Zone (ZoneId)
go

