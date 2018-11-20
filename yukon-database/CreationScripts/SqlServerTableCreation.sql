/*==============================================================*/
/* Database name:  YukonDatabase                                */
/* DBMS name:      Microsoft SQL Server 2005                    */
/* Created on:     11/20/2018 12:08:17 PM                       */
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
   LocationAddress1     varchar(100)         not null,
   LocationAddress2     varchar(100)         not null,
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
   ExcludeAbnormal      char(1)              not null,
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
/* Table: Behavior                                              */
/*==============================================================*/
create table Behavior (
   BehaviorId           numeric              not null,
   BehaviorType         varchar(60)          not null,
   constraint PK_BEHAVIOR primary key (BehaviorId)
)
go

/*==============================================================*/
/* Table: BehaviorReport                                        */
/*==============================================================*/
create table BehaviorReport (
   BehaviorReportId     numeric              not null,
   DeviceId             numeric              not null,
   BehaviorType         varchar(60)          not null,
   BehaviorStatus       varchar(60)          not null,
   TimeStamp            datetime             not null,
   constraint PK_BEHAVIORREPORT primary key (BehaviorReportId)
)
go

/*==============================================================*/
/* Table: BehaviorReportValue                                   */
/*==============================================================*/
create table BehaviorReportValue (
   BehaviorReportId     numeric              not null,
   Name                 varchar(60)          not null,
   Value                varchar(100)         not null,
   constraint PK_BEHAVIORREPORTVALUE primary key (BehaviorReportId, Name)
)
go

/*==============================================================*/
/* Table: BehaviorValue                                         */
/*==============================================================*/
create table BehaviorValue (
   BehaviorId           numeric              not null,
   Name                 varchar(60)          not null,
   Value                varchar(100)         not null,
   constraint PK_BEHAVIORVALUE primary key (BehaviorId, Name)
)
go

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
INSERT INTO BillingFileFormats VALUES(-35, 'Banner', 1);

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
   constraint PK_CapControlSubstationBus primary key (SubstationBusID)
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
   EventSubType         numeric              null,
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
/* Index: Indx_CCEventLog_Type_Date_Sub                         */
/*==============================================================*/
create index Indx_CCEventLog_Type_Date_Sub on CCEventLog (
EventType ASC,
DateTime ASC,
EventSubType ASC,
PointID ASC
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
   constraint PK_ColumnType primary key (TYPENUM)
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
/* Table: CollectionAction                                      */
/*==============================================================*/
create table CollectionAction (
   CollectionActionId   numeric              not null,
   Action               varchar(50)          not null,
   StartTime            datetime             not null,
   StopTime             datetime             null,
   Status               varchar(50)          not null,
   UserName             varchar(100)         not null,
   constraint PK_CollectionAction primary key (CollectionActionId)
)
go

/*==============================================================*/
/* Table: CollectionActionCommandRequest                        */
/*==============================================================*/
create table CollectionActionCommandRequest (
   CollectionActionId   numeric              not null,
   CommandRequestExecId numeric              not null,
   constraint PK_CACommandRequest primary key (CollectionActionId, CommandRequestExecId)
)
go

/*==============================================================*/
/* Table: CollectionActionInput                                 */
/*==============================================================*/
create table CollectionActionInput (
   CollectionActionId   numeric              not null,
   InputOrder           numeric              not null,
   Description          varchar(50)          not null,
   Value                varchar(1000)        not null,
   constraint PK_CollectionActionInput primary key (CollectionActionId, InputOrder)
)
go

/*==============================================================*/
/* Table: CollectionActionRequest                               */
/*==============================================================*/
create table CollectionActionRequest (
   CollectionActionRequestId numeric              not null,
   CollectionActionId   numeric              not null,
   PAObjectId           numeric              not null,
   Result               varchar(50)          not null,
   constraint PK_CollectionActionRequest primary key (CollectionActionRequestId)
)
go

/*==============================================================*/
/* Index: INDX_Car_CollectionActionId                           */
/*==============================================================*/
create index INDX_Car_CollectionActionId on CollectionActionRequest (
CollectionActionId ASC
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
   constraint PK_CommPort primary key (PORTID)
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

INSERT INTO Command VALUES (-193, 'getconfig install all', 'Read configuration', 'ALL RFNs');
INSERT INTO Command VALUES (-194, 'getconfig install display', 'Read display metrics', 'ALL RFNs');
INSERT INTO Command VALUES (-195, 'getconfig install freezeday', 'Read demand freeze day and last freeze timestamp', 'ALL RFNs');
INSERT INTO Command VALUES (-196, 'getconfig tou', 'Read TOU status', 'ALL RFNs');
INSERT INTO Command VALUES (-197, 'getconfig holidays', 'Read holiday schedule', 'ALL RFNs');
INSERT INTO Command VALUES (-198, 'getvalue voltage profile ?''MM/DD/YYYY'' ?''HH:mm'' ?''MM/DD/YYYY'' ?''HH:mm''', 'Read voltage profile data, start date required, rest of fields optional', 'ALL RFNs');

INSERT INTO Command VALUES (-199, 'getconfig install', 'Read configuration', 'All Two Way LCR');
INSERT INTO Command VALUES (-200, 'getconfig address', 'Read addressing', 'All Two Way LCR');
INSERT INTO Command VALUES (-201, 'getvalue temperature', 'Read temperature', 'All Two Way LCR');
INSERT INTO Command VALUES (-202, 'getconfig time', 'Read Date/Time', 'All Two Way LCR');
INSERT INTO Command VALUES (-203, 'getvalue power', 'Read transmit power', 'All Two Way LCR');

INSERT INTO Command VALUES (-204, 'putconfig temp enable', 'Enable Temp Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-205, 'putconfig temp disable', 'Disable Temp Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-206, 'putconfig var enable', 'Enable Var Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-207, 'putconfig var disable', 'Disable Var Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-208, 'putconfig time enable', 'Enable Time Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-209, 'putconfig time disable', 'Disable Time Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-210, 'putconfig ovuv enable', 'Enable OVUV', 'Twoway CBCs');
INSERT INTO Command VALUES (-211, 'putconfig ovuv disable', 'Disable OVUV', 'Twoway CBCs');

INSERT INTO Command VALUES (-212, 'getconfig model', 'Read Meter Config', 'All Two Way LCR');
INSERT INTO Command VALUES (-213, 'ping', 'Ping', 'All Two Way LCR');

INSERT INTO Command VALUES (-214, 'putconfig install all', 'Send configuration', 'ALL RFNs');

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
/* Index: INDX_CRE_ExType_ExId_ExStart                          */
/*==============================================================*/
create index INDX_CRE_ExType_ExId_ExStart on CommandRequestExec (
CommandRequestExecType ASC,
CommandRequestExecId ASC,
StartTime ASC
)
go

/*==============================================================*/
/* Table: CommandRequestExecRequest                             */
/*==============================================================*/
create table CommandRequestExecRequest (
   CommandRequestExecRequestId numeric              not null,
   CommandRequestExecId numeric              null,
   DeviceId             numeric              null,
   constraint PK_CommandRequestExecRequest primary key (CommandRequestExecRequestId)
)
go

/*==============================================================*/
/* Index: INDX_CREReq_ExecId_DevId                              */
/*==============================================================*/
create index INDX_CREReq_ExecId_DevId on CommandRequestExecRequest (
CommandRequestExecId ASC,
DeviceId ASC
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
/* Index: INDX_CRERes_ExecId_DevId                              */
/*==============================================================*/
create index INDX_CRERes_ExecId_DevId on CommandRequestExecResult (
CommandRequestExecId ASC,
DeviceId ASC,
ErrorCode ASC,
CompleteTime ASC
)
go

/*==============================================================*/
/* Table: CommandRequestUnsupported                             */
/*==============================================================*/
create table CommandRequestUnsupported (
   CommandRequestUnsupportedId numeric              not null,
   CommandRequestExecId numeric              not null,
   DeviceId             numeric              not null,
   Type                 varchar(50)          not null,
   constraint PK_CommandRequestUnsupported primary key nonclustered (CommandRequestUnsupportedId)
)
go

/*==============================================================*/
/* Index: Indx_CommandReqUnsupport_Type                         */
/*==============================================================*/
create index Indx_CommandReqUnsupport_Type on CommandRequestUnsupported (
Type ASC
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
/* Table: ControlEvent                                          */
/*==============================================================*/
create table ControlEvent (
   ControlEventId       numeric              not null,
   StartTime            datetime             not null,
   ScheduledStopTime    datetime             not null,
   GroupId              numeric              not null,
   LMControlHistoryId   numeric              null,
   ProgramId            numeric              not null default 0,
   constraint PK_ControlEvent primary key (ControlEventId)
)
go

/*==============================================================*/
/* Table: ControlEventDevice                                    */
/*==============================================================*/
create table ControlEventDevice (
   DeviceId             numeric              not null,
   ControlEventId       numeric              not null,
   OptOutEventId        numeric              null,
   Result               varchar(30)          not null,
   DeviceReceivedTime   datetime             null,
   constraint PK_ControlEventDevice primary key (DeviceId, ControlEventId)
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
/* Table: DBUpdates                                             */
/*==============================================================*/
create table DBUpdates (
   UpdateId             varchar(50)          not null,
   Version              varchar(10)          null,
   InstallDate          datetime             null,
   constraint PK_DBUPDATES primary key (UpdateId)
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
/* Index: INDX_DGMember_YukonPaoId                              */
/*==============================================================*/
create index INDX_DGMember_YukonPaoId on DEVICEGROUPMEMBER (
YukonPaoId ASC
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
   DESCRIPTION          varchar(200)         null,
   constraint PK_Display primary key (DISPLAYNUM)
)
go

insert into display values(-1, 'All Categories', 'Scheduler Client', 'com.cannontech.macs.gui.Scheduler');

/** insert into display values(-3, 'All Control Areas', 'Load Management Client', 'Load Management', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel'); **/
/** insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occurred.'); **/
/** insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will receive current raw point updates as they happen in the system.'); **/


insert into display values(1, 'Event Viewer', 'Alarms and Events', 'This display will receive current events as they happen in the system.');
insert into display values(4, 'All Alarms', 'Alarms and Events', 'This display will receive all alarm events as they happen in the system.');
insert into display values(5, 'Priority 1 Alarms', 'Alarms and Events', 'This display will receive all priority 1 alarm events as they happen in the system.');
insert into display values(6, 'Priority 2 Alarms', 'Alarms and Events', 'This display will receive all priority 2 alarm events as they happen in the system.');
insert into display values(7, 'Priority 3 Alarms', 'Alarms and Events', 'This display will receive all priority 3 alarm events as they happen in the system.');
insert into display values(8, 'Priority 4 Alarms', 'Alarms and Events', 'This display will receive all priority 4 alarm events as they happen in the system.');
insert into display values(9, 'Priority 5 Alarms', 'Alarms and Events', 'This display will receive all priority 5 alarm events as they happen in the system.');
insert into display values(10, 'Priority 6 Alarms', 'Alarms and Events', 'This display will receive all priority 6 alarm events as they happen in the system.');
insert into display values(11, 'Priority 7 Alarms', 'Alarms and Events', 'This display will receive all priority 7 alarm events as they happen in the system.');
insert into display values(12, 'Priority 8 Alarms', 'Alarms and Events', 'This display will receive all priority 8 alarm events as they happen in the system.');
insert into display values(13, 'Priority 9 Alarms', 'Alarms and Events', 'This display will receive all priority 9 alarm events as they happen in the system.');
insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'This display will receive all priority 10 alarm events as they happen in the system.');
insert into display values(15, 'Priority 11 Alarms', 'Alarms and Events', 'This display will receive all priority 11 alarm events as they happen in the system.');
insert into display values(16, 'Priority 12 Alarms', 'Alarms and Events', 'This display will receive all priority 12 alarm events as they happen in the system.');
insert into display values(17, 'Priority 13 Alarms', 'Alarms and Events', 'This display will receive all priority 13 alarm events as they happen in the system.');
insert into display values(18, 'Priority 14 Alarms', 'Alarms and Events', 'This display will receive all priority 14 alarm events as they happen in the system.');
insert into display values(19, 'Priority 15 Alarms', 'Alarms and Events', 'This display will receive all priority 15 alarm events as they happen in the system.');
insert into display values(20, 'Priority 16 Alarms', 'Alarms and Events', 'This display will receive all priority 16 alarm events as they happen in the system.');
insert into display values(21, 'Priority 17 Alarms', 'Alarms and Events', 'This display will receive all priority 17 alarm events as they happen in the system.');
insert into display values(22, 'Priority 18 Alarms', 'Alarms and Events', 'This display will receive all priority 18 alarm events as they happen in the system.');
insert into display values(23, 'Priority 19 Alarms', 'Alarms and Events', 'This display will receive all priority 19 alarm events as they happen in the system.');
insert into display values(24, 'Priority 20 Alarms', 'Alarms and Events', 'This display will receive all priority 20 alarm events as they happen in the system.');
insert into display values(25, 'Priority 21 Alarms', 'Alarms and Events', 'This display will receive all priority 21 alarm events as they happen in the system.');
insert into display values(26, 'Priority 22 Alarms', 'Alarms and Events', 'This display will receive all priority 22 alarm events as they happen in the system.');
insert into display values(27, 'Priority 23 Alarms', 'Alarms and Events', 'This display will receive all priority 23 alarm events as they happen in the system.');
insert into display values(28, 'Priority 24 Alarms', 'Alarms and Events', 'This display will receive all priority 24 alarm events as they happen in the system.');
insert into display values(29, 'Priority 25 Alarms', 'Alarms and Events', 'This display will receive all priority 25 alarm events as they happen in the system.');
insert into display values(30, 'Priority 26 Alarms', 'Alarms and Events', 'This display will receive all priority 26 alarm events as they happen in the system.');
insert into display values(31, 'Priority 27 Alarms', 'Alarms and Events', 'This display will receive all priority 27 alarm events as they happen in the system.');
insert into display values(32, 'Priority 28 Alarms', 'Alarms and Events', 'This display will receive all priority 28 alarm events as they happen in the system.');
insert into display values(33, 'Priority 29 Alarms', 'Alarms and Events', 'This display will receive all priority 29 alarm events as they happen in the system.');
insert into display values(34, 'Priority 30 Alarms', 'Alarms and Events', 'This display will receive all priority 30 alarm events as they happen in the system.');
insert into display values(35, 'Priority 31 Alarms', 'Alarms and Events', 'This display will receive all priority 31 alarm events as they happen in the system.');

insert into display values(50, 'SOE Log', 'Alarms and Events', 'This display shows all the SOE events in the SOE log table for a given day.');
insert into display values(51, 'TAG Log', 'Alarms and Events', 'This display shows all the TAG events in the TAG log table for a given day.');

insert into display values(99, 'Your Custom Display', 'Custom Displays', 'This display is used to show what a user created display looks like. You may edit this display to fit your own needs.');

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
   millis               smallint             not null,
   constraint PK_DYNAMICPOINTDISPATCH primary key (POINTID)
)
go

/*==============================================================*/
/* Table: Dashboard                                             */
/*==============================================================*/
create table Dashboard (
   DashboardId          numeric              not null,
   Name                 varchar(100)         not null,
   Description          varchar(500)         null,
   OwnerId              numeric              null,
   Visibility           varchar(20)          not null,
   constraint PK_Dashboard primary key (DashboardId)
)
go

INSERT INTO Dashboard VALUES (-1, 'Default Main Dashboard', 'Default Main Dashboard', -1, 'SYSTEM');
INSERT INTO Dashboard VALUES (-2, 'Default AMI Dashboard', 'Default AMI Dashboard', -1, 'SYSTEM');

alter table Dashboard
   add constraint AK_Dashboard_OwnerId_Name unique (OwnerId, Name)
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
/* Table: DeviceBehaviorMap                                     */
/*==============================================================*/
create table DeviceBehaviorMap (
   BehaviorId           numeric              not null,
   DeviceId             numeric              not null,
   constraint PK_DEVICEBEHAVIORMAP primary key (BehaviorId, DeviceId)
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
/* Table: DeviceCollection                                      */
/*==============================================================*/
create table DeviceCollection (
   CollectionId         numeric              not null,
   CollectionType       varchar(50)          not null,
   PersistenceType      varchar(50)          not null,
   constraint PK_DeviceCollect primary key (CollectionId)
)
go

/*==============================================================*/
/* Table: DeviceCollectionByField                               */
/*==============================================================*/
create table DeviceCollectionByField (
   CollectionId         numeric              not null,
   FieldName            varchar(50)          not null,
   FieldValue           varchar(100)         not null,
   constraint PK_DeviceCollectByField primary key (CollectionId, FieldName)
)
go

/*==============================================================*/
/* Table: DeviceCollectionById                                  */
/*==============================================================*/
create table DeviceCollectionById (
   CollectionId         numeric              not null,
   DeviceId             numeric              not null,
   constraint PK_DeviceCollectById primary key (CollectionId, DeviceId)
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
INSERT INTO DeviceConfigCategory VALUES (1, 'regulatorCategory', 'Default Regulator Category', null);
INSERT INTO DeviceConfigCategory VALUES (2, 'regulatorHeartbeat', 'Default Regulator Heartbeat Category', null);
INSERT INTO DeviceConfigCategory VALUES (3, 'cbcHeartbeat', 'Default CBC Heartbeat Category', null);

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
INSERT INTO DeviceConfigCategoryItem VALUES (3, 0, 'timeOffset', 'UTC');
INSERT INTO DeviceConfigCategoryItem VALUES (4, 0, 'enableUnsolicitedMessagesClass1', 'true');
INSERT INTO DeviceConfigCategoryItem VALUES (5, 0, 'enableUnsolicitedMessagesClass2', 'true');
INSERT INTO DeviceConfigCategoryItem VALUES (6, 0, 'enableUnsolicitedMessagesClass3', 'true');
INSERT INTO DeviceConfigCategoryItem VALUES (7, 1, 'voltageChangePerTap', '0.75');
INSERT INTO DeviceConfigCategoryItem VALUES (8, 1, 'voltageControlMode', 'DIRECT_TAP');
INSERT INTO DeviceConfigCategoryItem VALUES (9, 2, 'regulatorHeartbeatPeriod', '0');
INSERT INTO DeviceConfigCategoryItem VALUES (10, 2, 'regulatorHeartbeatValue', '0');
INSERT INTO DeviceConfigCategoryItem VALUES (11, 2, 'regulatorHeartbeatMode', 'NONE');
INSERT INTO DeviceConfigCategoryItem VALUES (12, 3, 'cbcHeartbeatPeriod', '0');
INSERT INTO DeviceConfigCategoryItem VALUES (13, 3, 'cbcHeartbeatValue', '0');
INSERT INTO DeviceConfigCategoryItem VALUES (14, 3, 'cbcHeartbeatMode', 'DISABLED');

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
INSERT INTO DeviceConfigCategoryMap VALUES(-1, 3);
INSERT INTO DeviceConfigCategoryMap VALUES(-2, 1);
INSERT INTO DeviceConfigCategoryMap VALUES(-2, 2);

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

INSERT INTO DeviceConfigDeviceTypes VALUES (1, -1, 'CBC 7020');
INSERT INTO DeviceConfigDeviceTypes VALUES (2, -1, 'CBC 7022');
INSERT INTO DeviceConfigDeviceTypes VALUES (3, -1, 'CBC 7023');
INSERT INTO DeviceConfigDeviceTypes VALUES (4, -1, 'CBC 7024');
INSERT INTO DeviceConfigDeviceTypes VALUES (5, -1, 'CBC 8020');
INSERT INTO DeviceConfigDeviceTypes VALUES (6, -1, 'CBC 8024');
INSERT INTO DeviceConfigDeviceTypes VALUES (7, -1, 'CBC DNP');
INSERT INTO DeviceConfigDeviceTypes VALUES (8, -1, 'RTU-DART');
INSERT INTO DeviceConfigDeviceTypes VALUES (9, -1, 'RTU-DNP');
INSERT INTO DeviceConfigDeviceTypes VALUES (10, -2, 'LTC');
INSERT INTO DeviceConfigDeviceTypes VALUES (11, -2, 'GO_REGULATOR');
INSERT INTO DeviceConfigDeviceTypes VALUES (12, -2, 'PO_REGULATOR');

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
INSERT INTO DeviceConfiguration VALUES (-2, 'Default Regulator Configuration', null);

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
   StateGroupId         numeric              null,
   ProcessorType        varchar(25)          not null,
   ProcessorValue       float                null,
   RangeMin             float                null,
   RangeMax             float                null,
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
INSERT INTO DeviceGroup VALUES (46, 'All Meters', 15, 'NOEDIT_NOMOD', 'STATIC', '02-JUN-2017', 'ALL_METERS');
INSERT INTO DeviceGroup VALUES (47, 'All MCT Meters', 46, 'NOEDIT_NOMOD', 'METERS_ALL_PLC_METERS', '02-JUN-2017', 'ALL_MCT_METERS');
INSERT INTO DeviceGroup VALUES (48, 'All RFN Meters', 46, 'NOEDIT_NOMOD', 'STATIC', '02-JUN-2017', 'ALL_RFN_METERS');
INSERT INTO DeviceGroup VALUES (49, 'All RF Electric Meters', 48, 'NOEDIT_NOMOD', 'METERS_ALL_RF_ELECTRIC_METERS', '02-JUN-2017', 'ALL_RF_ELECTRIC_METERS');
INSERT INTO DeviceGroup VALUES (50, 'All RFW Meters', 48, 'NOEDIT_NOMOD', 'METERS_ALL_RFW_METERS', '02-JUN-2017', 'ALL_RFW_METERS');
INSERT INTO DeviceGroup VALUES (51, 'Demand Response', 12, 'NOEDIT_NOMOD', 'STATIC', '01-Nov-2017', 'DEMAND_RESPONSE');
INSERT INTO DeviceGroup VALUES (52, 'Load Groups', 51, 'NOEDIT_NOMOD', 'LOAD_GROUPS', '01-Nov-2017', 'LOAD_GROUPS');
INSERT INTO DeviceGroup VALUES (53, 'Load Programs', 51, 'NOEDIT_NOMOD', 'LOAD_PROGRAMS', '01-Nov-2017', 'LOAD_PROGRAMS');
INSERT INTO DeviceGroup VALUES (54, 'All RFG Meters', 48, 'NOEDIT_NOMOD', 'METERS_ALL_RFG_METERS', '18-JUN-2018', 'ALL_RFG_METERS');
INSERT INTO DeviceGroup VALUES (55, 'CIS DeviceClass', 1, 'NOEDIT_MOD', 'STATIC', '08-JAN-2018', 'CIS_DEVICECLASS');

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
/* Table: DeviceParent                                          */
/*==============================================================*/
create table DeviceParent (
   DeviceID             numeric              not null,
   ParentID             numeric              not null,
   constraint PK_DeviceParent primary key (DeviceID, ParentID)
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
INSERT INTO DeviceTypeCommand VALUES (-379, -98, 'MCT-410IL', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-380, -99, 'MCT-410IL', 15, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-381, -98, 'MCT-410CL', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-382, -99, 'MCT-410CL', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-383, -100, 'SENTINEL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-384, -101, 'SENTINEL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-385, -102, 'SENTINEL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-386, -103, 'SENTINEL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-387, -104, 'SENTINEL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-388, -105, 'MCT-410IL', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-389, -106, 'MCT-410IL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-390, -107, 'MCT-410IL', 18, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-391, -108, 'MCT-410IL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-392, -109, 'MCT-410IL', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-394, -111, 'MCT-410IL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-395, -112, 'MCT-410IL', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-396, -113, 'MCT-410IL', 24, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-397, -114, 'MCT-410IL', 25, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-398, -105, 'MCT-410CL', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-399, -106, 'MCT-410CL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-400, -107, 'MCT-410CL', 18, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-401, -108, 'MCT-410CL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-402, -109, 'MCT-410CL', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-404, -111, 'MCT-410CL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-405, -112, 'MCT-410CL', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-406, -113, 'MCT-410CL', 24, 'Y', -1);
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
INSERT INTO DeviceTypeCommand VALUES (-518, -98, 'MCT-410FL', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-519, -99, 'MCT-410FL', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-520, -105, 'MCT-410FL', 16, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-521, -106, 'MCT-410FL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-522, -107, 'MCT-410FL', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-523, -108, 'MCT-410FL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-524, -109, 'MCT-410FL', 20, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-526, -111, 'MCT-410FL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-527, -112, 'MCT-410FL', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-528, -113, 'MCT-410FL', 24, 'Y', -1);
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
INSERT INTO DeviceTypeCommand VALUES (-543, -98, 'MCT-410GL', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-544, -99, 'MCT-410GL', 15, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-545, -105, 'MCT-410GL', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-546, -106, 'MCT-410GL', 17, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-547, -107, 'MCT-410GL', 18, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-548, -108, 'MCT-410GL', 19, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-549, -109, 'MCT-410GL', 20, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-551, -111, 'MCT-410GL', 22, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-552, -112, 'MCT-410GL', 23, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-553, -113, 'MCT-410GL', 24, 'Y', -1);
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

INSERT INTO DeviceTypeCommand VALUES (-1020, -193, 'RFN-410fL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1021, -194, 'RFN-410fL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1022, -195, 'RFN-410fL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1023, -196, 'RFN-410fL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1024, -197, 'RFN-410fL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1025, -198, 'RFN-410fL', 6, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1026, -193, 'RFN-410fX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1027, -194, 'RFN-410fX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1028, -195, 'RFN-410fX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1029, -196, 'RFN-410fX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1030, -197, 'RFN-410fX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1031, -198, 'RFN-410fX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1234, -214, 'RFN-410fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1032, -193, 'RFN-410fD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1033, -194, 'RFN-410fD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1034, -195, 'RFN-410fD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1035, -196, 'RFN-410fD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1036, -197, 'RFN-410fD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1037, -198, 'RFN-410fD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1232, -214, 'RFN-410fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1038, -193, 'RFN-420fL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1039, -194, 'RFN-420fL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1040, -195, 'RFN-420fL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1041, -196, 'RFN-420fL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1042, -197, 'RFN-420fL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1043, -198, 'RFN-420fL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1238, -214, 'RFN-420fL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1044, -193, 'RFN-420fX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1046, -195, 'RFN-420fX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1047, -196, 'RFN-420fX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1048, -197, 'RFN-420fX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1049, -198, 'RFN-420fX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1241, -214, 'RFN-420fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1050, -193, 'RFN-420fD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1052, -195, 'RFN-420fD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1053, -196, 'RFN-420fD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1054, -197, 'RFN-420fD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1055, -198, 'RFN-420fD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1237, -214, 'RFN-420fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1056, -193, 'RFN-420fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1058, -195, 'RFN-420fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1059, -196, 'RFN-420fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1060, -197, 'RFN-420fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1061, -198, 'RFN-420fRX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1240, -214, 'RFN-420fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1062, -193, 'RFN-420fRD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1064, -195, 'RFN-420fRD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1065, -196, 'RFN-420fRD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1066, -197, 'RFN-420fRD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1067, -198, 'RFN-420fRD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1239, -214, 'RFN-420fRD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-1068, -193, 'RFN-410cL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1069, -194, 'RFN-410cL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1070, -195, 'RFN-410cL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1071, -196, 'RFN-410cL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1072, -197, 'RFN-410cL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1073, -198, 'RFN-410cL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1231, -214, 'RFN-410cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1074, -193, 'RFN-420cL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1076, -195, 'RFN-420cL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1077, -196, 'RFN-420cL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1078, -197, 'RFN-420cL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1079, -198, 'RFN-420cL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1236, -214, 'RFN-420cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1080, -193, 'RFN-420cD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1082, -195, 'RFN-420cD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1083, -196, 'RFN-420cD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1084, -197, 'RFN-420cD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1085, -198, 'RFN-420cD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1235, -214, 'RFN-420cD', 7, 'Y', -1);
 
INSERT INTO DeviceTypeCommand VALUES (-1086, -193, 'RFN-430A3D', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1242, -214, 'RFN-430A3D', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1091, -193, 'RFN-430A3T', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1245, -214, 'RFN-430A3T', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1096, -193, 'RFN-430A3K', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1243, -214, 'RFN-430A3K', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1101, -193, 'RFN-430A3R', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1244, -214, 'RFN-430A3R', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1106, -193, 'RFN-430KV', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1107, -194, 'RFN-430KV', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1108, -195, 'RFN-430KV', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1109, -196, 'RFN-430KV', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1110, -197, 'RFN-430KV', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1246, -214, 'RFN-430KV', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1111, -193, 'RFN-430SL0', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1247, -214, 'RFN-430SL0', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1116, -193, 'RFN-430SL1', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1248, -214, 'RFN-430SL1', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1121, -193, 'RFN-430SL2', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1249, -214, 'RFN-430SL2', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1126, -193, 'RFN-430SL3', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1250, -214, 'RFN-430SL3', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1131, -193, 'RFN-430SL4', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1251, -214, 'RFN-430SL4', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1136, -199, 'LCR-3102', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1137, -200, 'LCR-3102', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1138, -201, 'LCR-3102', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1139, -202, 'LCR-3102', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1140, -203, 'LCR-3102', 15, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1141, -204, 'CBC 8020', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1142, -205, 'CBC 8020', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1143, -206, 'CBC 8020', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1144, -207, 'CBC 8020', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1145, -208, 'CBC 8020', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1146, -209, 'CBC 8020', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1147, -210, 'CBC 8020', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1148, -211, 'CBC 8020', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1267, -30, 'CBC 8020', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1268, -31, 'CBC 8020', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1269, -173, 'CBC 8020', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1149, -204, 'CBC 8024', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1150, -205, 'CBC 8024', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1151, -206, 'CBC 8024', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1152, -207, 'CBC 8024', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1153, -208, 'CBC 8024', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1154, -209, 'CBC 8024', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1155, -210, 'CBC 8024', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1156, -211, 'CBC 8024', 8, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1270, -30, 'CBC 8024', 9, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1271, -31, 'CBC 8024', 10, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1272, -173, 'CBC 8024', 11, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1157, -193, 'RFN-510fL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1158, -194, 'RFN-510fL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1159, -195, 'RFN-510fL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1160, -196, 'RFN-510fL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1161, -197, 'RFN-510fL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1162, -198, 'RFN-510fL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1255, -214, 'RFN-510fL', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1163, -193, 'RFN-520fAX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1165, -195, 'RFN-520fAX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1166, -196, 'RFN-520fAX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1167, -197, 'RFN-520fAX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1168, -198, 'RFN-520fAX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1256, -214, 'RFN-520fAX', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1169, -193, 'RFN-520fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1171, -195, 'RFN-520fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1172, -196, 'RFN-520fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1173, -197, 'RFN-520fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1174, -198, 'RFN-520fRX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1257, -214, 'RFN-520fRX', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1175, -193, 'RFN-520fAXD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1177, -195, 'RFN-520fAXD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1178, -196, 'RFN-520fAXD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1179, -197, 'RFN-520fAXD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1180, -198, 'RFN-520fAXD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1258, -214, 'RFN-520fAXD', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1181, -193, 'RFN-520fRXD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1183, -195, 'RFN-520fRXD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1184, -196, 'RFN-520fRXD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1185, -197, 'RFN-520fRXD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1186, -198, 'RFN-520fRXD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1259, -214, 'RFN-520fRXD', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1187, -193, 'RFN-530fAX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1188, -194, 'RFN-530fAX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1189, -195, 'RFN-530fAX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1190, -196, 'RFN-530fAX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1191, -197, 'RFN-530fAX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1192, -198, 'RFN-530fAX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1260, -214, 'RFN-530fAX', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1193, -193, 'RFN-530fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1194, -194, 'RFN-530fRX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1195, -195, 'RFN-530fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1196, -196, 'RFN-530fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1197, -197, 'RFN-530fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1198, -198, 'RFN-530fRX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1261, -214, 'RFN-530fRX', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1199, -193, 'RFN-530S4x', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1200, -194, 'RFN-530S4x', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1201, -195, 'RFN-530S4x', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1202, -196, 'RFN-530S4x', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1203, -197, 'RFN-530S4x', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1204, -198, 'RFN-530S4x', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1262, -214, 'RFN-530S4x', 7, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1205, -193, 'RFN-530S4eAX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1263, -214, 'RFN-530S4eAX', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1211, -193, 'RFN-530S4eAXR', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1264, -214, 'RFN-530S4eAXR', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1217, -193, 'RFN-530S4eRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1265, -214, 'RFN-530S4eRX', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1223, -193, 'RFN-530S4eRXR', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1266, -214, 'RFN-530S4eRXR', 2, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1229, -212, 'LCR-3102', 16, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1230, -213, 'LCR-3102', 17, 'Y', -1);

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
/* Table: DmvMeasurementData                                    */
/*==============================================================*/
create table DmvMeasurementData (
   ExecutionId          numeric              not null,
   PointId              numeric              not null,
   Timestamp            datetime             not null,
   Quality              numeric              not null,
   Value                float                not null,
   constraint PK_DmvMeasurementData primary key (ExecutionId, PointId, Timestamp)
)
go

/*==============================================================*/
/* Index: INDX_DmvMeasurementData_TStamp                        */
/*==============================================================*/
create index INDX_DmvMeasurementData_TStamp on DmvMeasurementData (
Timestamp DESC
)
go

/*==============================================================*/
/* Table: DmvTest                                               */
/*==============================================================*/
create table DmvTest (
   DmvTestId            numeric              not null,
   DmvTestName          varchar(100)         not null,
   DataArchivingInterval numeric              not null,
   IntervalDataGatheringDuration numeric              not null,
   StepSize             float                not null,
   CommSuccessPercentage numeric              not null,
   constraint PK_DmvTest primary key (DmvTestId)
)
go

alter table DmvTest
   add constraint AK_DmvTest_DmvTestName unique (DmvTestName)
go

/*==============================================================*/
/* Table: DmvTestExecution                                      */
/*==============================================================*/
create table DmvTestExecution (
   ExecutionId          numeric              not null,
   DmvTestId            numeric              not null,
   AreaId               numeric              not null,
   SubstationId         numeric              not null,
   BusId                numeric              not null,
   StartTime            datetime             not null,
   StopTime             datetime             null,
   TestStatus           varchar(30)          null,
   constraint PK_DmvTestExecution primary key (ExecutionId)
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
/* Table: DynamicLcrCommunications                              */
/*==============================================================*/
create table DynamicLcrCommunications (
   DeviceId             numeric              not null,
   LastCommunication    datetime             null,
   LastNonZeroRuntime   datetime             null,
   Relay1Runtime        datetime             null,
   Relay2Runtime        datetime             null,
   Relay3Runtime        datetime             null,
   Relay4Runtime        datetime             null,
   constraint PK_DynamicLcrCommunications primary key (DeviceId)
)
go

/*==============================================================*/
/* Table: DynamicPAOInfo                                        */
/*==============================================================*/
create table DynamicPAOInfo (
   PAObjectID           numeric              not null,
   Owner                varchar(64)          not null,
   InfoKey              varchar(128)         not null,
   Value                varchar(128)         not null,
   UpdateTime           datetime             not null,
   constraint PK_DynamicPaoInfo primary key (PAObjectID, Owner, InfoKey)
)
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
/* Table: EcobeeQueryStatistics                                 */
/*==============================================================*/
create table EcobeeQueryStatistics (
   MonthIndex           numeric              not null,
   YearIndex            numeric              not null,
   QueryType            varchar(40)          not null,
   QueryCount           numeric              not null,
   constraint PK_EcobeeQueryStatistics primary key (MonthIndex, YearIndex, QueryType)
)
go

/*==============================================================*/
/* Table: EcobeeReconReportError                                */
/*==============================================================*/
create table EcobeeReconReportError (
   EcobeeReconReportErrorId numeric              not null,
   EcobeeReconReportId  numeric              not null,
   ErrorType            varchar(40)          not null,
   SerialNumber         varchar(30)          null,
   CurrentLocation      varchar(100)         null,
   CorrectLocation      varchar(100)         null,
   constraint PK_EcobeeReconReportError primary key (EcobeeReconReportErrorId)
)
go

/*==============================================================*/
/* Table: EcobeeReconciliationReport                            */
/*==============================================================*/
create table EcobeeReconciliationReport (
   EcobeeReconReportId  numeric              not null,
   ReportDate           datetime             not null,
   constraint PK_EcobeeReconReport primary key (EcobeeReconReportId)
)
go

/*==============================================================*/
/* Table: EncryptionKey                                         */
/*==============================================================*/
create table EncryptionKey (
   EncryptionKeyId      numeric              not null,
   Name                 varchar(128)         not null,
   PrivateKey           varchar(1920)        not null,
   PublicKey            varchar(608)         null,
   EncryptionKeyType    varchar(128)         not null,
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
   ParentEnergyCompanyId numeric              null,
   constraint PK_ENERGYCOMPANY primary key (EnergyCompanyID)
)
go

insert into EnergyCompany VALUES (-1, 'Default Energy Company', 0, -100, NULL);

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

alter table EstimatedLoadFormula
   add constraint AK_EstimatedLoadFormula_Name unique (Name)
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
/* Index: INDX_EPPA_PointId                                     */
/*==============================================================*/
create index INDX_EPPA_PointId on ExtraPaoPointAssignment (
PointId ASC
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
INSERT INTO FDRInterface VALUES (28, 'DNPSLAVE', 'Send,Receive,Receive for control', 't');
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
   Destination          varchar(256)         not null,
   Translation          varchar(500)         not null,
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
   JobName              varchar(100)         not null,
   ExportDate           datetime             not null,
   ExportPath           varchar(300)         null,
   ArchiveFileExists    char(1)              not null,
   JobGroupId           int                  not null,
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
   constraint PK_GraphDataSeries primary key (GRAPHDATASERIESID)
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
   constraint PK_GraphDefinition primary key (GRAPHDEFINITIONID)
)
go

alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique (NAME)
go

/*==============================================================*/
/* Table: GatewayCertificateUpdate                              */
/*==============================================================*/
create table GatewayCertificateUpdate (
   UpdateId             numeric              not null,
   CertificateId        varchar(100)         not null,
   SendDate             datetime             not null,
   FileName             varchar(100)         not null,
   constraint PK_GatewayCertificateUpdate primary key (UpdateId)
)
go

/*==============================================================*/
/* Table: GatewayCertificateUpdateEntry                         */
/*==============================================================*/
create table GatewayCertificateUpdateEntry (
   EntryId              numeric              not null,
   UpdateId             numeric              not null,
   GatewayId            numeric              not null,
   UpdateStatus         varchar(40)          not null,
   constraint PK_GatewayCertificateUpdEntry primary key (EntryId)
)
go

/*==============================================================*/
/* Table: GatewayFirmwareUpdate                                 */
/*==============================================================*/
create table GatewayFirmwareUpdate (
   UpdateId             numeric              not null,
   SendDate             datetime             not null,
   constraint PK_GatewayFirmwareUpdate primary key (UpdateId)
)
go

/*==============================================================*/
/* Table: GatewayFirmwareUpdateEntry                            */
/*==============================================================*/
create table GatewayFirmwareUpdateEntry (
   EntryId              numeric              not null,
   UpdateId             numeric              not null,
   GatewayId            numeric              not null,
   OriginalVersion      varchar(100)         not null,
   NewVersion           varchar(100)         not null,
   UpdateServerUrl      varchar(2000)        not null,
   UpdateStatus         varchar(40)          not null,
   constraint PK_GatewayFirmwareUpdateEntry primary key (EntryId)
)
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
/* Table: HoneywellWifiThermostat                               */
/*==============================================================*/
create table HoneywellWifiThermostat (
   DeviceId             numeric              not null,
   MacAddress           varchar(255)         not null,
   UserId               numeric              not null,
   constraint PK_HONEYWELLWIFITHERMOSTAT primary key (DeviceId)
)
go

alter table HoneywellWifiThermostat
   add constraint AK_HONEYWELLWIFITHERMOSTAT_MAC unique (MacAddress)
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
/* Table: InfrastructureWarnings                                */
/*==============================================================*/
create table InfrastructureWarnings (
   PaoId                numeric              not null,
   WarningType          varchar(50)          not null,
   Severity             varchar(10)          not null,
   Argument1            varchar(50)          null,
   Argument2            varchar(50)          null,
   Argument3            varchar(50)          null,
   Timestamp            datetime             not null,
   constraint PK_InfrastructureWarnings primary key (PaoId, WarningType)
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
   SendOutOfService     char(1)              not null,
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
   UserID               numeric              null,
   Locale               varchar(10)          null,
   TimeZone             varchar(40)          null,
   themeName            varchar(60)          null,
   JobGroupId           int                  not null,
   constraint PK_JOB primary key (JobID)
)
go

INSERT INTO Job (Jobid, BeanName, Disabled, JobGroupId) VALUES (-4, 'deviceConfigVerificationJobDefinition', 'N', -4);

INSERT INTO Job (Jobid, BeanName, Disabled, JobGroupId) VALUES (-3, 'spSmartIndexMaintanenceJobDefinition', 'N', -3);
INSERT INTO Job (Jobid, BeanName, Disabled, JobGroupId) VALUES (-2, 'rfnPerformanceVerificationEmailJobDefinition', 'N', -2);
INSERT INTO Job (Jobid, BeanName, Disabled, JobGroupId) VALUES (-1, 'rfnPerformanceVerificationJobDefinition', 'N', -1);

alter table JOB
   add constraint AK_Job_JobId_JobGroupId unique (JobID, JobGroupId)
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

INSERT INTO JobScheduledRepeating VALUES (-4, '0 01 0 ? * *');
INSERT INTO JobScheduledRepeating VALUES (-3, '0 0 22 ? * 7');
INSERT INTO JobScheduledRepeating VALUES (-2, '0 0 6 ? * *');
INSERT INTO JobScheduledRepeating VALUES (-1, '0 15 0 ? * *'); 

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
/* Table: LMGroupHoneywellWiFi                                  */
/*==============================================================*/
create table LMGroupHoneywellWiFi (
   DeviceId             numeric              not null,
   HoneywellGroupId     numeric              not null,
   constraint PK_LMGroupHoneywellWiFi primary key (DeviceId)
)
go

alter table LMGroupHoneywellWiFi
   add constraint AK_LMGroupHoneywellWiFi unique (HoneywellGroupId)
go

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
/* Table: LMNestControlEvent                                    */
/*==============================================================*/
create table LMNestControlEvent (
   NestControlEventId   numeric              not null,
   NestGroup            varchar(20)          not null,
   NestKey              varchar(20)          not null,
   StartTime            datetime             not null,
   StopTime             datetime             null,
   CancelRequestTime    datetime             null,
   CancelResponse       varchar(200)         null,
   CancelOrStop         char(1)              not null,
   constraint PK_LMNestControlEvent primary key (NestControlEventId)
)
go

/*==============================================================*/
/* Table: LMNestLoadShapingGear                                 */
/*==============================================================*/
create table LMNestLoadShapingGear (
   GearId               numeric              not null,
   PreparationOption    varchar(20)          not null,
   PeakOption           varchar(20)          not null,
   PostPeakOption       varchar(20)          not null,
   constraint PK_LMNestLoadShapingGear primary key (GearId)
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
   NotifySchedule       numeric              not null,
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
   StopCommandRepeat    numeric              not null,
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
   constraint PK_Logic primary key (LOGICID)
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
   Endpoint             varchar(255)         not null,
   Version              varchar(12)          not null,
   constraint PK_MSPINTERFACE primary key (VendorID, Interface, Version)
)
go

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'http://127.0.0.1:8080/multispeak/v3/MR_Server', '3.0');
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'http://127.0.0.1:8080/multispeak/v3/OD_Server', '3.0');
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'http://127.0.0.1:8080/multispeak/v3/CD_Server', '3.0');

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'http://127.0.0.1:8080/multispeak/v5/MR_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'http://127.0.0.1:8080/multispeak/v5/OD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'http://127.0.0.1:8080/multispeak/v5/CD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'NOT_Server', 'http://127.0.0.1:8080/multispeak/v5/NOT_Server', '5.0');

/*==============================================================*/
/* Table: MSPVendor                                             */
/*==============================================================*/
create table MSPVendor (
   VendorID             numeric              not null,
   CompanyName          varchar(64)          not null,
   UserName             varchar(64)          not null,
   Password             varchar(64)          not null,
   AppName              varchar(64)          not null,
   OutUserName          varchar(64)          not null,
   OutPassword          varchar(64)          not null,
   MaxReturnRecords     int                  not null,
   RequestMessageTimeout int                  not null,
   MaxInitiateRequestObjects int                  not null,
   TemplateNameDefault  varchar(50)          not null,
   ValidateCertificate  char(1)              not null,
   constraint PK_MSPVENDOR primary key (VendorID)
)
go

INSERT INTO MSPVendor VALUES (1, 'Cannon', ' ', ' ', 'Yukon', ' ', ' ', 10000, 120000, 15, ' ', 1);

/*==============================================================*/
/* Index: INDEX_1                                               */
/*==============================================================*/
create unique index INDEX_1 on MSPVendor (
CompanyName ASC,
AppName ASC
)
go

/*==============================================================*/
/* Table: MaintenanceTaskSettings                               */
/*==============================================================*/
create table MaintenanceTaskSettings (
   SettingType          varchar(50)          not null,
   Value                varchar(50)          not null,
   constraint PK_MaintenanceTaskSettings primary key (SettingType)
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
/* Table: NestSync                                              */
/*==============================================================*/
create table NestSync (
   SyncId               numeric              not null,
   SyncStartTime        datetime             not null,
   SyncStopTime         datetime             null,
   constraint PK_NestSync primary key (SyncId)
)
go

/*==============================================================*/
/* Table: NestSyncDetail                                        */
/*==============================================================*/
create table NestSyncDetail (
   SyncDetailId         numeric              not null,
   SyncId               numeric              not null,
   SyncType             varchar(60)          not null,
   SyncReasonKey        varchar(100)         not null,
   SyncActionKey        varchar(100)         not null,
   constraint PK_NestSyncDetail primary key (SyncDetailId)
)
go

/*==============================================================*/
/* Table: NestSyncValue                                         */
/*==============================================================*/
create table NestSyncValue (
   SyncValueId          numeric              not null,
   SyncDetailId         numeric              not null,
   SyncValue            varchar(100)         not null,
   SyncValueType        varchar(60)          not null,
   constraint PK_NestSyncValue primary key (SyncValueId)
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
INSERT INTO Point VALUES(-11, 'Analog', 'Porter CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1007, 'None', 0);
INSERT INTO Point VALUES(-12, 'Analog', 'Dispatch CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1008, 'None', 0);
INSERT INTO Point VALUES(-13, 'Analog', 'Scanner CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1009, 'None', 0);
INSERT INTO Point VALUES(-14, 'Analog', 'Calc CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1010, 'None', 0);
INSERT INTO Point VALUES(-15, 'Analog', 'CapControl CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1011, 'None', 0);
INSERT INTO Point VALUES(-16, 'Analog', 'FDR CPU Utilization', 0, 'Default', 0, 'N', 'N', 'R', 1012, 'None', 0);
INSERT INTO Point VALUES(-17, 'Analog', 'MACS CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1013, 'None', 0);
INSERT INTO Point VALUES(-18, 'Analog', 'Notification Server CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1014, 'None', 0);
INSERT INTO Point VALUES(-19, 'Analog', 'Service Manager CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1015, 'None', 0);
INSERT INTO Point VALUES(-20, 'Analog', 'Web Service CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1016, 'None', 0);
INSERT INTO Point VALUES(-21, 'Analog', 'Porter Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1017, 'None', 0);
INSERT INTO Point VALUES(-22, 'Analog', 'Dispatch Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1018, 'None', 0);
INSERT INTO Point VALUES(-23, 'Analog', 'Scanner Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1019, 'None', 0);
INSERT INTO Point VALUES(-24, 'Analog', 'Calc Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1020, 'None', 0);
INSERT INTO Point VALUES(-25, 'Analog', 'CapControl Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1021, 'None', 0);
INSERT INTO Point VALUES(-26, 'Analog', 'FDR Memory Utilization', 0, 'Default', 0, 'N', 'N', 'R', 1022, 'None', 0);
INSERT INTO Point VALUES(-27, 'Analog', 'MACS Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1023, 'None', 0);
INSERT INTO Point VALUES(-28, 'Analog', 'Notification Server Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1024, 'None', 0);
INSERT INTO Point VALUES(-29, 'Analog', 'Service Manager Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1025, 'None', 0);
INSERT INTO Point VALUES(-30, 'Analog', 'Web Service Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1026, 'None', 0);
INSERT INTO Point VALUES(-31, 'Status', 'Load Management Monitor', 0, 'Default', -7, 'N', 'N', 'R', 1027, 'None', 0);
INSERT INTO Point VALUES(-32, 'Analog', 'Load Management CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1028, 'None', 0);
INSERT INTO Point VALUES(-33, 'Analog', 'Load Management Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1029, 'None', 0);
INSERT INTO Point VALUES(-34, 'Analog', 'Message Broker CPU Utilization', 0,'Default', 0, 'N', 'N', 'R', 1030, 'None', 0);
INSERT INTO Point VALUES(-35, 'Analog', 'Message Broker Memory Utilization', 0,'Default', 0, 'N', 'N', 'R', 1031, 'None', 0);
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

insert into pointanalog values(-11, -1, 1, 0 );
insert into pointanalog values(-12, -1, 1, 0 );
insert into pointanalog values(-13, -1, 1, 0 );
insert into pointanalog values(-14, -1, 1, 0 );
insert into pointanalog values(-15, -1, 1, 0 );
insert into pointanalog values(-16, -1, 1, 0 );
insert into pointanalog values(-17, -1, 1, 0 );
insert into pointanalog values(-18, -1, 1, 0 );
insert into pointanalog values(-19, -1, 1, 0 );
insert into pointanalog values(-20, -1, 1, 0 );
insert into pointanalog values(-21, -1, 1, 0 );
insert into pointanalog values(-22, -1, 1, 0 );
insert into pointanalog values(-23, -1, 1, 0 );
insert into pointanalog values(-24, -1, 1, 0 );
insert into pointanalog values(-25, -1, 1, 0 );
insert into pointanalog values(-26, -1, 1, 0 );
insert into pointanalog values(-27, -1, 1, 0 );
insert into pointanalog values(-28, -1, 1, 0 );
insert into pointanalog values(-29, -1, 1, 0 );
insert into pointanalog values(-30, -1, 1, 0 );
insert into pointanalog values(-32, -1, 1, 0 );
insert into pointanalog values(-33, -1, 1, 0 );
INSERT INTO pointanalog VALUES(-34, -1, 1, 0 );
INSERT INTO pointanalog VALUES(-35, -1, 1, 0 );
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
insert into pointstatus values(-31, 0);

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

insert into pointunit values(-11, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-12, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-13, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-14, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-15, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-16, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-17, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-18, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-19, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-20, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-21, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-22, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-23, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-24, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-25, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-26, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-27, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-28, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-29, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-30, 56, 0, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-32, 28, 2, 1.0E+30, -1.0E+30, 0);
insert into pointunit values(-33, 56, 0, 1.0E+30, -1.0E+30, 0);
INSERT INTO pointunit VALUES(-34, 28, 2, 1.0E+30, -1.0E+30, 0);
INSERT INTO pointunit VALUES(-35, 56, 0, 1.0E+30, -1.0E+30, 0);
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
/* Table: PaoLocation                                           */
/*==============================================================*/
create table PaoLocation (
   PAObjectId           numeric              not null,
   Latitude             numeric(9,6)         not null,
   Longitude            numeric(9,6)         not null,
   LastChangedDate      datetime             not null,
   Origin               varchar(64)          not null,
   constraint PK_PaoLocation primary key (PAObjectId)
)
go

/*==============================================================*/
/* Table: PaoNote                                               */
/*==============================================================*/
create table PaoNote (
   NoteId               numeric              not null,
   PaObjectId           numeric              not null,
   NoteText             nvarchar(255)        not null,
   Status               char(1)              not null,
   CreateUserName       nvarchar(64)         not null,
   CreateDate           datetime             not null,
   EditUserName         nvarchar(64)         null,
   EditDate             datetime             null,
   constraint PK_PaoNote primary key (NoteId)
)
go

/*==============================================================*/
/* Index: INDX_PaObjectId_Status                                */
/*==============================================================*/
create index INDX_PaObjectId_Status on PaoNote (
PaObjectId ASC,
Status ASC
)
go

/*==============================================================*/
/* Table: PasswordHistory                                       */
/*==============================================================*/
create table PasswordHistory (
   PasswordHistoryId    numeric              not null,
   UserId               numeric              not null,
   Password             nvarchar(128)        not null,
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
   Ignore               varchar(1)           not null,
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

INSERT INTO PorterResponseMonitor VALUES (1, 'Default All PLC Meters', '/System/Meters/All Meters/All MCT Meters', -14, 'OUTAGE_STATUS', 'DISABLED');

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
   constraint PKC_RawPointHistory primary key (CHANGEID)
)
go

/*==============================================================*/
/* Index: Indx_RawPointHistory_PtId_Ts                          */
/*==============================================================*/
create index Indx_RawPointHistory_PtId_Ts on RAWPOINTHISTORY (
POINTID ASC,
TIMESTAMP DESC
)
include (QUALITY,VALUE)
go

/*==============================================================*/
/* Table: RPHTag                                                */
/*==============================================================*/
create table RPHTag (
   ChangeId             numeric              not null,
   PeakUp               bit                  not null,
   PeakDown             bit                  not null,
   UnreasonableUp       bit                  not null,
   UnreasonableDown     bit                  not null,
   ChangeOut            bit                  not null,
   Accepted             bit                  not null,
   constraint PK_RPHTag primary key (ChangeId)
)
go

/*==============================================================*/
/* Table: RawPointHistoryDependentJob                           */
/*==============================================================*/
create table RawPointHistoryDependentJob (
   JobId                int                  not null,
   RawPointHistoryId    numeric              not null,
   JobGroupId           int                  not null,
   constraint PK_RawPointHistoryDependentJob primary key (JobId)
)
go

/*==============================================================*/
/* Table: RecentPointValue                                      */
/*==============================================================*/
create table RecentPointValue (
   PAObjectID           numeric              not null,
   PointID              numeric              not null,
   Timestamp            datetime             not null,
   Quality              numeric              not null,
   Value                float                not null,
   constraint PK_RecentPointValue primary key (PAObjectID)
)
go

/*==============================================================*/
/* Index: Indx_RPV_PointID                                      */
/*==============================================================*/
create index Indx_RPV_PointID on RecentPointValue (
PointID ASC
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
/* Table: RegulatorEvents                                       */
/*==============================================================*/
create table RegulatorEvents (
   RegulatorEventId     numeric              not null,
   EventType            varchar(64)          not null,
   RegulatorId          numeric              not null,
   TimeStamp            datetime             not null,
   UserName             varchar(64)          not null,
   SetPointValue        float                null,
   TapPosition          numeric              null,
   Phase                char(1)              null,
   constraint PK_RegulatorEvents primary key (RegulatorEventId)
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
/* Index: AK_RepAddExpressCom_DeviceId                          */
/*==============================================================*/
create index AK_RepAddExpressCom_DeviceId on ReportedAddressExpressCom (
DeviceId ASC
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
/* Table: RfnAddress                                            */
/*==============================================================*/
create table RfnAddress (
   DeviceId             numeric              not null,
   SerialNumber         varchar(30)          not null,
   Manufacturer         varchar(80)          not null,
   Model                varchar(80)          not null,
   constraint PK_RfnAddress primary key nonclustered (DeviceId)
)
go

/*==============================================================*/
/* Index: Indx_RfnAdd_SerNum_Man_Mod_UNQ                        */
/*==============================================================*/
create unique index Indx_RfnAdd_SerNum_Man_Mod_UNQ on RfnAddress (
SerialNumber ASC,
Manufacturer ASC,
Model ASC
)
go

/*==============================================================*/
/* Table: RfnBroadcastEvent                                     */
/*==============================================================*/
create table RfnBroadcastEvent (
   RfnBroadcastEventId  numeric              not null,
   EventSentTime        datetime             not null,
   constraint PK_RfnBroadcastEventId primary key (RfnBroadcastEventId)
)
go

/*==============================================================*/
/* Table: RfnBroadcastEventDeviceStatus                         */
/*==============================================================*/
create table RfnBroadcastEventDeviceStatus (
   DeviceId             numeric              not null,
   RfnBroadcastEventId  numeric              not null,
   Result               varchar(30)          not null,
   DeviceReceivedTime   datetime             null,
   constraint PK_RfnBroadcastEventDevStatus primary key (DeviceId, RfnBroadcastEventId)
)
go

/*==============================================================*/
/* Index: Indx_RfnBcstEvntDev_DevIdMsgId                        */
/*==============================================================*/
create index Indx_RfnBcstEvntDev_DevIdMsgId on RfnBroadcastEventDeviceStatus (
RfnBroadcastEventId ASC,
Result ASC
)
go

/*==============================================================*/
/* Table: RfnBroadcastEventSummary                              */
/*==============================================================*/
create table RfnBroadcastEventSummary (
   RfnBroadcastEventId  numeric              not null,
   Success              numeric              not null,
   SuccessUnenrolled    numeric              not null,
   Failure              numeric              not null,
   Unknown              numeric              not null,
   constraint PK_RFNBROADCASTEVENTSUMMARY primary key (RfnBroadcastEventId)
)
go

/*==============================================================*/
/* Table: Route                                                 */
/*==============================================================*/
create table Route (
   RouteID              numeric              not null,
   DeviceID             numeric              not null,
   DefaultRoute         char(1)              not null,
   constraint PK_Route primary key nonclustered (RouteID)
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
   constraint PK_SystemLog primary key (LOGID)
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
/* Table: SmartNotificationEvent                                */
/*==============================================================*/
create table SmartNotificationEvent (
   EventId              numeric              not null,
   Type                 varchar(30)          not null,
   Timestamp            datetime             not null,
   GroupProcessTime     datetime             null,
   ImmediateProcessTime datetime             null,
   constraint PK_SmartNotificationEvent primary key (EventId)
)
go

/*==============================================================*/
/* Index: INDX_SmartNotifiEvt_Timestamp                         */
/*==============================================================*/
create index INDX_SmartNotifiEvt_Timestamp on SmartNotificationEvent (
Timestamp DESC
)
go

/*==============================================================*/
/* Table: SmartNotificationEventParam                           */
/*==============================================================*/
create table SmartNotificationEventParam (
   EventId              numeric              not null,
   Name                 varchar(30)          not null,
   Value                varchar(100)         not null,
   constraint PK_SmartNotificationEventParam primary key (EventId, Name, Value)
)
go

/*==============================================================*/
/* Table: SmartNotificationSub                                  */
/*==============================================================*/
create table SmartNotificationSub (
   SubscriptionId       numeric              not null,
   UserId               numeric              not null,
   Type                 varchar(30)          not null,
   Media                varchar(30)          not null,
   Frequency            varchar(30)          not null,
   Verbosity            varchar(30)          not null,
   Recipient            varchar(100)         not null,
   constraint PK_SmartNotificationSub primary key (SubscriptionId)
)
go

/*==============================================================*/
/* Index: INDX_SmartNotifSub_UserId_Type                        */
/*==============================================================*/
create index INDX_SmartNotifSub_UserId_Type on SmartNotificationSub (
UserId ASC,
Type ASC
)
go

/*==============================================================*/
/* Table: SmartNotificationSubParam                             */
/*==============================================================*/
create table SmartNotificationSubParam (
   SubscriptionId       numeric              not null,
   Name                 varchar(30)          not null,
   Value                varchar(100)         not null,
   constraint PK_SmartNotificationSubParam primary key (SubscriptionId, Name, Value)
)
go

/*==============================================================*/
/* Table: State                                                 */
/*==============================================================*/
create table State (
   StateGroupId         numeric              not null,
   RawState             numeric              not null,
   Text                 varchar(50)          not null,
   ForegroundColor      numeric              not null,
   BackgroundColor      numeric              not null,
   ImageId              numeric              not null,
   constraint PK_STATE primary key (StateGroupId, RawState)
)
go

INSERT INTO State VALUES(-28, 0, 'Heat', 1, 6, 0);
INSERT INTO State VALUES(-28, 1, 'Cool', 4, 6, 0);
INSERT INTO State VALUES(-28, 2, 'Off', 9, 6, 0);
INSERT INTO State VALUES(-27, 0, 'Yes', 0, 6, 0);
INSERT INTO State VALUES(-27, 1, 'No', 1, 6, 0);
INSERT INTO State VALUES(-26, 0, 'Time of Day', 0, 6, 0);
INSERT INTO State VALUES(-26, 1, 'Countdown Timer', 1, 6, 0);
INSERT INTO State VALUES(-25, 0, 'No', 0, 6, 0);
INSERT INTO State VALUES(-25, 1, 'Yes', 1, 6, 0);
INSERT INTO State VALUES(-24, 0, 'Trip', 0, 6, 0);
INSERT INTO State VALUES(-24, 1, 'Close', 1, 6, 0);
INSERT INTO State VALUES(-23, 0, 'CBC Line', 0, 6, 0);
INSERT INTO State VALUES(-23, 1, 'Phase A', 1, 6, 0);
INSERT INTO State VALUES(-23, 2, 'Phase B', 2, 6, 0);
INSERT INTO State VALUES(-23, 3, 'Phase C', 3, 6, 0);
INSERT INTO State VALUES(-22, 0, 'N/A', 0, 6, 0);
INSERT INTO State VALUES(-22, 1, 'Old Board/Old Box', 1, 6, 0);
INSERT INTO State VALUES(-22, 2, 'New Board/Old Box', 2, 6, 0);
INSERT INTO State VALUES(-22, 3, 'New Board/New Box', 3, 6, 0);
INSERT INTO State VALUES(-21, 0, 'Closed', 0, 6, 0);
INSERT INTO State VALUES(-21, 1, 'Open', 1, 6, 0);
INSERT INTO State VALUES(-20, 0, 'Manual', 1, 6, 0);
INSERT INTO State VALUES(-20, 1, 'SCADA Override', 2, 6, 0);
INSERT INTO State VALUES(-20, 2, 'Fault Current', 3, 6, 0);
INSERT INTO State VALUES(-20, 3, 'Emergency Voltage', 4, 6, 0);
INSERT INTO State VALUES(-20, 4, 'Time ONOFF', 5, 6, 0);
INSERT INTO State VALUES(-20, 5, 'OVUV Control', 7, 6, 0);
INSERT INTO State VALUES(-20, 6, 'VAR', 8, 6, 0);
INSERT INTO State VALUES(-20, 7, 'Va', 9, 6, 0);
INSERT INTO State VALUES(-20, 8, 'Vb', 10, 6, 0);
INSERT INTO State VALUES(-20, 9, 'Vc', 1, 6, 0);
INSERT INTO State VALUES(-20, 10, 'Ia', 2, 6, 0);
INSERT INTO State VALUES(-20, 11, 'Ib', 3, 6, 0);
INSERT INTO State VALUES(-20, 12, 'Ic', 4, 6, 0);
INSERT INTO State VALUES(-20, 13, 'Temp', 5, 6, 0);
INSERT INTO State VALUES(-20, 14, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 15, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 16, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 17, 'Bad Active Relay', 8, 6, 0);
INSERT INTO State VALUES(-20, 18, 'NC Lockout', 9, 6, 0);
INSERT INTO State VALUES(-20, 19, 'Control Accepted', 7, 6, 0);
INSERT INTO State VALUES(-20, 20, 'Auto Mode', 10, 6, 0);
INSERT INTO State VALUES(-20, 21, 'Reclose Block', 1, 6, 0);
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
INSERT INTO State VALUES(-17, 7, 'Va', 8, 6, 0);
INSERT INTO State VALUES(-17, 8, 'Vb', 1, 6, 0);
INSERT INTO State VALUES(-17, 9, 'Vc', 2, 6, 0);
INSERT INTO State VALUES(-17, 10, 'Ia', 3, 6, 0);
INSERT INTO State VALUES(-17, 11, 'Ib', 4, 6, 0);
INSERT INTO State VALUES(-17, 12, 'Ic', 5, 6, 0);
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
INSERT INTO State VALUES(-12, 4, 'Disconnected Demand Threshold Active', 1, 6, 0);
INSERT INTO State VALUES(-12, 5, 'Connected Demand Threshold Active', 7, 6, 0);
INSERT INTO State VALUES(-12, 6, 'Disconnected Cycling Active', 1, 6, 0);
INSERT INTO State VALUES(-12, 7, 'Connected Cycling Active', 7, 6, 0);
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
   constraint PK_StateGroup primary key (StateGroupId)
)
go

INSERT INTO StateGroup VALUES(-28, 'ThermostatRelayState', 'Status');
INSERT INTO StateGroup VALUES(-27, 'NoYes', 'Status');
INSERT INTO StateGroup VALUES(-26, 'SCADA Override Type', 'Status');
INSERT INTO StateGroup VALUES(-25, 'YesNo', 'Status');
INSERT INTO StateGroup VALUES(-24, 'SCADA TripClose', 'Status');
INSERT INTO StateGroup VALUES(-23, 'Var Voltage Input', 'Status');
INSERT INTO StateGroup VALUES(-22, 'CBC-8 Hardware Type', 'Status');
INSERT INTO StateGroup VALUES(-21, 'CBC Door Status', 'Status');
INSERT INTO StateGroup VALUES(-20, 'Ignored Control', 'Status');
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
INSERT INTO StatusPointMonitorProcessor VALUES (3, 1, 'DIFFERENCE', 2, 'Outage');

/*==============================================================*/
/* Table: StoredProcedureLog                                    */
/*==============================================================*/
create table StoredProcedureLog (
   EntryId              numeric              not null,
   ProcedureName        varchar(50)          not null,
   LogDate              datetime             not null,
   LogString            varchar(500)         not null,
   constraint PK_StoredProcedureLog primary key (EntryId)
)
go

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
   constraint PK_Template primary key (TEMPLATENUM)
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
   IsCurrent            bit                  not null,
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
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_BACKGROUND', '-2');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_FONT_COLOR', '#ffffff');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_FONT_SHADOW', 'rgba(0,0,0,0.5)');
INSERT INTO ThemeProperty VALUES (-1, 'LOGIN_TAGLINE_MARGIN', '35');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO', '-1');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_LEFT', '0');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_TOP', '17');
INSERT INTO ThemeProperty VALUES (-1, 'LOGO_WIDTH', '163');
INSERT INTO ThemeProperty VALUES (-1, 'VISITED_COLOR', '#1c49a6');
INSERT INTO ThemeProperty VALUES (-1, 'BUTTON_COLOR', '#777');
INSERT INTO ThemeProperty VALUES (-1, 'BUTTON_COLOR_BORDER', '#666');
INSERT INTO ThemeProperty VALUES (-1, 'BUTTON_COLOR_HOVER', '#888');

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
   constraint PK_UnitMeasure primary key (UOMID)
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
INSERT INTO UnitMeasure VALUES ( 41,'VoltsV2H', 1,'Volts from V2H','(none)' );
INSERT INTO UnitMeasure VALUES ( 42,'AmpsA2H', 1,'Amps from A2H','(none)' );
INSERT INTO UnitMeasure VALUES ( 43,'Tap', 0,'LTC Tap Position','(none)' );
INSERT INTO UnitMeasure VALUES ( 44,'Miles', 0,'Miles','(none)' );
INSERT INTO UnitMeasure VALUES ( 45,'Ms', 0,'Milliseconds','(none)' );
INSERT INTO UnitMeasure VALUES ( 46,'PPM',0,'Parts Per Million','(none)');
INSERT INTO UnitMeasure VALUES ( 47,'MPH',0,'Miles Per Hour','(none)');
INSERT INTO UnitMeasure VALUES ( 48,'Inches',0,'Inches','(none)');
INSERT INTO UnitMeasure VALUES ( 49,'KPH',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES ( 50,'Milibars',0,'Milibars','(none)');
INSERT INTO UnitMeasure VALUES ( 51,'km/h',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES ( 52,'m/s',0,'Meters Per Second','(none)');
INSERT INTO UnitMeasure VALUES ( 53,'KV', 0,'KVolts','(none)' );
INSERT INTO UnitMeasure VALUES ( 54,'UNDEF', 0,'Undefined','(none)' );
INSERT INTO UnitMeasure VALUES ( 55,'m^3', 0, 'Cubic Meters', '(none)');
INSERT INTO UnitMeasure VALUES ( 56,'MB', 0, 'Megabytes', '(none)');

/*==============================================================*/
/* Table: UsageThresholdReport                                  */
/*==============================================================*/
create table UsageThresholdReport (
   UsageThresholdReportId numeric              not null,
   Attribute            varchar(60)          not null,
   StartDate            datetime             not null,
   EndDate              datetime             not null,
   RunTime              datetime             not null,
   DevicesDescription   varchar(255)         not null,
   constraint PK_UTReport primary key (UsageThresholdReportId)
)
go

/*==============================================================*/
/* Table: UsageThresholdReportRow                               */
/*==============================================================*/
create table UsageThresholdReportRow (
   UsageThresholdReportId numeric              not null,
   PaoId                numeric              not null,
   PointId              numeric              null,
   FirstTimestamp       datetime             null,
   FirstValue           float                null,
   LastTimestamp        datetime             null,
   LastValue            float                null,
   Delta                float                null,
   constraint PK_UTReportRow primary key (UsageThresholdReportId, PaoId)
)
go

/*==============================================================*/
/* Table: UserDashboard                                         */
/*==============================================================*/
create table UserDashboard (
   UserId               numeric              not null,
   DashboardId          numeric              not null,
   PageAssignment       varchar(50)          not null,
   constraint PK_UserDashboard primary key (UserId, DashboardId, PageAssignment)
)
go

INSERT INTO UserDashboard VALUES (-1, -1, 'MAIN'); 
INSERT INTO UserDashboard VALUES (-1, -2, 'AMI');

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
   Module               varchar(64)          not null,
   PageName             varchar(256)         not null,
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
   Parameter            varchar(2000)        not null,
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
   Value                varchar(1275)        not null,
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
/* Table: Widget                                                */
/*==============================================================*/
create table Widget (
   WidgetId             numeric              not null,
   WidgetType           varchar(50)          not null,
   DashboardId          numeric              not null,
   Ordering             numeric              not null,
   constraint PK_Widget primary key (WidgetId)
)
go

/* Default Main Dashboard widgets */
INSERT INTO Widget VALUES (-1, 'FAVORITES', -1, 100);
INSERT INTO Widget VALUES (-2, 'MONITOR_SUBSCRIPTIONS', -1, 200);

/* Default AMI Dashboard widgets */
INSERT INTO Widget VALUES (-3, 'MONITORS', -2, 100);
INSERT INTO Widget VALUES (-4, 'SCHEDULED_REQUESTS', -2, 101);
INSERT INTO Widget VALUES (-5, 'METER_SEARCH', -2, 200);
INSERT INTO Widget VALUES (-6, 'AMI_ACTIONS', -2, 201);

/*==============================================================*/
/* Table: WidgetSettings                                        */
/*==============================================================*/
create table WidgetSettings (
   SettingId            numeric              not null,
   WidgetId             numeric              not null,
   Name                 varchar(50)          not null,
   Value                varchar(500)         not null,
   constraint PK_WidgetSettings primary key (SettingId)
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
insert into YukonGroupRole values(-1002,-2,-100,-10002,' ');
insert into YukonGroupRole values(-1004,-2,-100,-10004,' ');
insert into YukonGroupRole values(-1005,-2,-100,-10005,' ');
insert into YukonGroupRole values(-1007,-2,-100,-10007,' ');
insert into YukonGroupRole values(-1008,-2,-100,-10008,' ');
insert into YukonGroupRole values(-1010,-2,-100,-10010,'00000000');
insert into YukonGroupRole values(-1011,-2,-100,-10011,' ');

/* TDC */
insert into YukonGroupRole values(-1021,-2,-101,-10101,' ');
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

insert into YukonGroupRole values(-1071,-2,-103,-10301,'true');
insert into YukonGroupRole values(-1072,-2,-103,-10302,'true');
insert into YukonGroupRole values(-1073,-2,-103,-10303,'false');
insert into YukonGroupRole values(-1074,-2,-103,-10304,'false');
insert into YukonGroupRole values(-1075,-2,-103,-10305,' ');

/* Billing */
insert into YukonGroupRole values(-1390,-2,-106,-10600,' ');

/* Web Client Customers Web Client role */
insert into YukonGroupRole values (-1090,-2, -108, -10800, '/dashboard');
insert into YukonGroupRole values (-1091,-2, -108, -10802, ' ');
insert into YukonGroupRole values (-1094,-2, -108, -10805, ' ');
insert into YukonGroupRole values (-1095,-2, -108, -10806, ' ');

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

INSERT INTO YukonListEntry VALUES (2000, 1005, 0, 'LCR-6200 (RFN)', 1324);
INSERT INTO YukonListEntry VALUES (2001, 1005, 0, 'LCR-6600 (RFN)', 1325);
INSERT INTO YukonListEntry VALUES (2034, 1005, 0, 'LCR-6700 (RFN)', 1337);
INSERT INTO YukonListEntry VALUES (2002, 1005, 0, 'LCR-6200 (ZigBee)', 1320); 
INSERT INTO YukonListEntry VALUES (2003, 1005, 0, 'LCR-6200 (ExpressCom)', 1321); 
INSERT INTO YukonListEntry VALUES (2004, 1005, 0, 'LCR-6600 (ZigBee)', 1322); 
INSERT INTO YukonListEntry VALUES (2005, 1005, 0, 'LCR-6600 (ExpressCom)', 1323);
INSERT INTO YukonListEntry VALUES (2006, 1005, 0, 'LCR-5000 (ExpressCom)', 1302);
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
INSERT INTO YukonListEntry VALUES (2017, 1005, 0, 'LCR-5000 (VERSACOM)', 1311);
INSERT INTO YukonListEntry VALUES (2018, 1005, 0, 'ExpressStat Heat Pump', 1313);
INSERT INTO YukonListEntry VALUES (2019, 1005, 0, 'UtilityPRO', 1314);
INSERT INTO YukonListEntry VALUES (2020, 1005, 0, 'LCR-3102', 1315);
INSERT INTO YukonListEntry VALUES (2021, 1005, 0, 'UtilityPRO ZigBee', 1316);

INSERT INTO YukonListEntry VALUES (2022, 1005, 0, 'Digi Gateway', 1317);
INSERT INTO YukonListEntry VALUES (2023, 1005, 0, 'UtilityPRO G2', 1318); 
INSERT INTO YukonListEntry VALUES (2024, 1005, 0, 'UtilityPRO G3', 1319); 
INSERT INTO YukonListEntry VALUES (2025, 1005, 0, 'ecobee Smart Si', 1329);
INSERT INTO YukonListEntry VALUES (2026, 1005, 0, 'ecobee3', 1330);
INSERT INTO YukonListEntry VALUES (2027, 1005, 0, 'ecobee Smart', 1331);
INSERT INTO YukonListEntry VALUES (2028, 1005, 0, 'ecobee3 Lite', 1336);

INSERT INTO YukonListEntry VALUES (2030, 1005, 0, 'Honeywell Wi-Fi 9000', 1332);
INSERT INTO YukonListEntry VALUES (2031, 1005, 0, 'Honeywell Wi-Fi VisionPRO 8000', 1333);
INSERT INTO YukonListEntry VALUES (2032, 1005, 0, 'Honeywell Wi-Fi FocusPRO', 1334);
INSERT INTO YukonListEntry VALUES (2033, 1005, 0, 'Honeywell Wi-Fi Thermostat', 1335);

INSERT INTO YukonListEntry VALUES (2035, 1005, 0, 'Nest', 1338);

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
INSERT INTO YukonRole VALUES(-108,'Web Client','Application','Access to the Yukon web application');
INSERT INTO YukonRole VALUES(-109,'Reporting','Application','Access to reports generation.');
INSERT INTO YukonRole VALUES(-110,'Password Policy','Application','Handles the password rules and restrictions for a given group.');

/* Web client operator roles */
INSERT INTO YukonRole VALUES(-200,'Administrator','Operator','Access to Yukon administration');
INSERT INTO YukonRole VALUES(-201,'Consumer Info','Operator','Operator access to consumer account information');
INSERT INTO YukonRole VALUES(-202,'Metering','Operator','Operator access to metering');

/* Operator roles */
INSERT INTO YukonRole VALUES(-207,'Odds For Control','Operator','Operator access to odds for control');

/* Inventory Role */
INSERT INTO YukonRole VALUES(-209,'Inventory','Operator','Operator Access to hardware inventory');

/* operator work order management role */
INSERT INTO YukonRole VALUES(-210,'Work Order','Operator','Operator Access to work order management');

/* ISOC */
INSERT INTO YukonRole VALUES(-211,'C&I Curtailment','Operator','Controls user and operator access to C&I Curtailment.');

/* Scheduler Role */
INSERT INTO YukonRole VALUES(-212,'Scheduler','Operator','Operator access to Scheduler'); 

/* Device Actions Role */
INSERT INTO YukonRole VALUES(-213,'Device Actions','Operator','Operator access to device actions'); 

/* Device Management Role */
INSERT INTO YukonRole VALUES(-214, 'Device Management', 'Operator', 'Permissions for creating, editing, and configuring devices.');

/* Consumer roles */
INSERT INTO YukonRole VALUES(-400,'Residential Customer','Consumer','Access to residential customer information');

/* Capacitor Control roles */
INSERT INTO YukonRole VALUES(-700,'Cap Control Settings','Capacitor Control','Allows a user to change overall settings of the Cap Control system .'); 

/* IVR roles */
INSERT INTO YukonRole VALUES(-800,'IVR','Notifications','Settings for Interactive Voice Response module');
INSERT INTO YukonRole VALUES(-801, 'Configuration', 'Notifications', 'Configuration for Notification Server (voice and email)');

/* Load Control roles */
INSERT INTO YukonRole VALUES(-900,'Demand Response','Demand Response','Operator access to Demand Response');

/* Capacitor Control roles cont*/
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
INSERT INTO YukonRoleProperty VALUES(-10002,-100,'Load Management View Enabled','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
INSERT INTO YukonRoleProperty VALUES(-10004,-100,'System View Enabled','true','Controls whether the System menu item in the View menu is displayed');
INSERT INTO YukonRoleProperty VALUES(-10005,-100,'Versacom Utility Range','1-254','<description>');
INSERT INTO YukonRoleProperty VALUES(-10007,-100,'Transmission Exclusion Enabled','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');
INSERT INTO YukonRoleProperty VALUES(-10008,-100,'Manage Users','true','Closes off all access to logins and login groups for non-administrators in the dbeditor');
INSERT INTO YukonRoleProperty VALUES(-10010,-100,'Optional Protocols','00000000','This feature is for development purposes only');
INSERT INTO YukonRoleProperty VALUES(-10011,-100,'Program Member Management','false','Allows member management of LM Direct Programs through the DBEditor');

/* TDC Role */
INSERT INTO YukonRoleProperty VALUES(-10101,-101,'macs_edit','00000CCC','The following settings are valid: CREATE_SCHEDULE(0x0000000C), ENABLE_SCHEDULE(0x000000C0), ABLE_TO_START_SCHEDULE(0x00000C00)');
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

/* Web Client Role Properties */
INSERT INTO YukonRoleProperty VALUES(-10800,-108,'home_url','/dashboard','The url to take the user immediately after logging into the Yukon web application');
INSERT INTO YukonRoleProperty VALUES(-10802,-108,'style_sheet','yukon/CannonStyle.css','The web client cascading style sheet.');
INSERT INTO YukonRoleProperty VALUES(-10805,-108,'header_logo','yukon/DefaultHeader.gif','The main header logo');
INSERT INTO YukonRoleProperty VALUES(-10806,-108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
INSERT INTO YukonRoleProperty VALUES(-10812, -108,'Java Web Start Launcher Enabled', 'true', 'Allow access to the Java Web Start Launcher for client applications.');
INSERT INTO YukonRoleProperty VALUES(-10814, -108,'Suppress Error Page Details', 'true', 'Disable stack traces for this user.');
INSERT INTO YukonRoleProperty VALUES(-10815, -108,'Data Updater Delay (milliseconds)', '4000', 'The number of milliseconds between requests for the latest point values on pages that support the data updater.');
INSERT INTO YukonRoleProperty VALUES(-10816, -108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');
INSERT INTO YukonRoleProperty VALUES(-10817, -108,'Theme Name',' ','The name of the theme to be applied to this group');
INSERT INTO YukonRoleProperty VALUES(-10818, -108, 'View Alarms Alerts','false','Ability to receive point alarms as alerts');
INSERT INTO YukonRoleProperty VALUES(-10819, -108, 'Default TimeZone',' ','Default TimeZone (e.g. America/Denver, America/Chicago, America/Los_Angeles, or America/New_York)');
INSERT INTO YukonRoleProperty VALUES(-10820, -108, 'Session Timeout (minutes)','120','The amount of idle time (in minutes) before a user''s session will expire.');

/* Reporting Analysis role properties */
INSERT INTO YukonRoleProperty VALUES(-10903,-109,'Admin Reports Group','true','Access to administrative group reports.');
INSERT INTO YukonRoleProperty VALUES(-10904,-109,'AMR Reports Group','true','Access to AMR group reports.');
INSERT INTO YukonRoleProperty VALUES(-10905,-109,'Statistical Reports Group','true','Access to statistical group reports.');
INSERT INTO YukonRoleProperty VALUES(-10906,-109,'Load Management Reports Group','false','Access to Load Management group reports.');
INSERT INTO YukonRoleProperty VALUES(-10907,-109,'Cap Control Reports Group','false','Access to Cap Control group reports.');
INSERT INTO YukonRoleProperty VALUES(-10908,-109,'Database Reports Group','true','Access to Database group reports.');
INSERT INTO YukonRoleProperty VALUES(-10909,-109,'Stars Reports Group','true','Access to Stars group reports.');
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
INSERT INTO YukonRoleProperty VALUES(-20157,-201,'Opt Out Period',' ','The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)');
INSERT INTO YukonRoleProperty VALUES(-20158,-201,'Disable Switch Sending','false','Disables the ability to send configs and connects/disconnects to switches.');
INSERT INTO YukonRoleProperty VALUES(-20163,-201,'Allow Account Editing','true','Can be used to disable the ability to edit and delete customer account information.');
INSERT INTO YukonRoleProperty VALUES(-20164,-201,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.');
INSERT INTO YukonRoleProperty VALUES(-20165,-201,'Account Search','true','Enables you to use account searching.');
INSERT INTO YukonRoleProperty VALUES(-20166,-201,'Survey Edit','false','Enables editing of surveys.');
INSERT INTO YukonRoleProperty VALUES(-20167,-201,'Opt Out Survey Edit','false','Enables editing of opt out surveys.'); 

/* Operator Administrator Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20000,-200,'Edit Energy Company','false','Controls access to edit the user''s energy company settings.');
INSERT INTO YukonRoleProperty VALUES(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');
INSERT INTO YukonRoleProperty VALUES(-20004,-200,'View Batch Commands','false','Controls whether to allow monitoring of all batched switch commands');
INSERT INTO YukonRoleProperty VALUES(-20005,-200,'View Opt Out Events','false','Controls whether to allow monitoring of all scheduled opt out events');
INSERT INTO YukonRoleProperty VALUES(-20006,-200,'Member Login Cntrl','false','Ignored if not a member company -- Controls whether operator logins are shown on the EC administration page.');
INSERT INTO YukonRoleProperty VALUES(-20007,-200,'Member Route Select','false','Ignored if not a member company -- Controls whether routes are visible through the EC administration page.');
INSERT INTO YukonRoleProperty VALUES(-20009,-200,'Multiple Warehouses','false','Allows for multiple user-created warehouses instead of a single generic warehouse.');
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
INSERT INTO YukonRoleProperty VALUES(-20021,-200,'Manage Dashboards','false','Controls access to manage all user defined dashboards.');

/* Operator Metering Role Properties*/
INSERT INTO YukonRoleProperty VALUES(-20203,-202,'Enable Bulk Importer','true','Allows access to the Bulk Importer');
INSERT INTO YukonRoleProperty VALUES(-20206,-202,'Profile Collection','true','Controls access to submit a (past) profile collection request');
INSERT INTO YukonRoleProperty VALUES(-20207,-202,'Move In/Move Out Auto Archiving','true','Enables automatic archiving of move in/move out transactions');
INSERT INTO YukonRoleProperty VALUES(-20208,-202,'Move In/Move Out','true','Controls access to process a move in/move out');
INSERT INTO YukonRoleProperty VALUES(-20209,-202,'Profile Collection Scanning','true','Controls access to start/stop scanning of profile data'); 
INSERT INTO YukonRoleProperty VALUES(-20210,-202,'High Bill Complaint','true','Controls access to process a high bill complaint'); 
INSERT INTO YukonRoleProperty VALUES(-20213,-202,'Outage Processing','true','Controls access to Outage Processing');
INSERT INTO YukonRoleProperty VALUES(-20214,-202,'Tamper Flag Processing','false','Controls access to Tamper Flag Processing');
INSERT INTO YukonRoleProperty VALUES(-20215,-202,'Phase Detection','false','Controls access to Phase Detection.');
INSERT INTO YukonRoleProperty VALUES(-20216,-202,'Validation Engine','false','Controls access to Validation Processing');
INSERT INTO YukonRoleProperty VALUES(-20217,-202,'Status Point Monitor','false','Controls access to the Status Point Monitor');
INSERT INTO YukonRoleProperty VALUES(-20218,-202,'Porter Response Monitor','false','Controls access to the Porter Response Monitor');
INSERT INTO YukonRoleProperty VALUES(-20219,-202,'Meter Events','false','Controls access to Meter Events.');
INSERT INTO YukonRoleProperty VALUES(-20220,-202,'Allow Disconnect Control','true','Controls access to Disconnect, Connect, and Arm operations.');
INSERT INTO YukonRoleProperty VALUES(-20221,-202,'Device Data Monitor','false','Controls access to the Device Data Monitor.');
INSERT INTO YukonRoleProperty VALUES(-20222,-202,'Water Leak Report','true','Controls access to the Water Leak Report.');
INSERT INTO YukonRoleProperty VALUES(-20223,-202,'Usage Threshold Report','true','Controls access to the Usage Threshold Report.');

/* Odds For Control Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20700,-207,'Odds For Control Exists','true','The default odds for control property.');

/* Operator Consumer Info Role Properties Part II */
INSERT INTO YukonRoleProperty VALUES(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-20895,-201,'Opt Out Admin Status','true','Determines whether an operator can see current opt out status on the Opt Out Admin page.');
INSERT INTO YukonRoleProperty VALUES(-20896,-201,'Opt Out Admin Change Enabled','true','Determines whether an operator can enable or disable Opt Outs for the rest of the day.');
INSERT INTO YukonRoleProperty VALUES(-20897,-201,'Opt Out Admin Cancel Current','true','Determines whether an operator can cancel (reenable) ALL currently Opted Out devices.');
INSERT INTO YukonRoleProperty VALUES(-20898,-201,'Opt Out Admin Change Counts','true','Determines whether an operator can change from Opt Outs count against limits today to Opt Outs do not count.'); 

/* Operator Hardware Inventory Role Properties */
INSERT INTO YukonRoleProperty VALUES(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardware by serial number range');
INSERT INTO YukonRoleProperty VALUES(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardware by serial number range');
INSERT INTO YukonRoleProperty VALUES(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardware by serial number range');
INSERT INTO YukonRoleProperty VALUES(-20905,-209,'Create Hardware','true','Controls whether to allow creating new hardware');
INSERT INTO YukonRoleProperty VALUES(-20906,-209,'Expresscom Restore First','false','Controls whether an opt out command should also contain a restore');
INSERT INTO YukonRoleProperty VALUES(-20909,-209,'Purchasing Access','false','Activates the purchasing section of the inventory module.'); 
INSERT INTO YukonRoleProperty VALUES(-20910,-209,'Inventory Configuration','false','Controls access to Inventory Configuration Tool');
INSERT INTO YukonRoleProperty VALUES(-20911,-209,'Inventory Search','true','Enables you to use inventory searching.'); 

/* operator work order management role properties */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');
insert into YukonRoleProperty values(-21002,-210,'Work Order Report','true','Controls whether to allow reporting on work orders');
insert into YukonRoleProperty values(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');

INSERT INTO YukonRoleProperty VALUES(-21100,-211,'C&I Curtailment Operator','true','Controls access to the C&I Curtailment operator functionality. When false, only the C&I Curtailment user pages are available.');

/* Scheduler Role Properties */
INSERT INTO YukonRoleProperty VALUES (-21200,-212,'MACS Scripts','UPDATE','Controls the ability to view, start/stop, enable/disable, edit, create, delete for MACS Script.'); 
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
INSERT INTO YukonRoleProperty VALUES (-21314,-213,'Connect/Disconnect','true','Controls access to Connect/Disconnect collection action.');
INSERT INTO YukonRoleProperty VALUES (-21315,-213,'Demand Reset','true','Controls access to Demand Reset collection action.');
INSERT INTO YukonRoleProperty VALUES (-21316, -213, 'RF Data Streaming', 'false', 'Controls access to RF data streaming configuration actions.');

/* Device Management Role Properties */
INSERT INTO YukonRoleProperty VALUES(-21400, -214, 'Infrastructure Create/Edit', 'false', 'Controls the ability to create and edit infrastructure devices. i.e. RF Gateways.');
INSERT INTO YukonRoleProperty VALUES(-21401, -214, 'Infrastructure Delete', 'false', 'Controls the ability to delete infrastructure devices. i.e. RF Gateways.');
INSERT INTO YukonRoleProperty VALUES(-21402, -214, 'Infrastructure Administration', 'false', 'Controls the ability to send configuration commands to infrastructure devices. i.e. RF Gateways.');
INSERT INTO YukonRoleProperty VALUES(-21403, -214, 'Infrastructure View', 'false', 'Controls the ability to view infrastructure devices. i.e. RF Gateways.');
INSERT INTO YukonRoleProperty VALUES(-21404, -214, 'Endpoint Permission', 'UPDATE', 'Controls the ability to create, edit, or delete endpoint devices. i.e Meters. Metering Role controls view access.');
INSERT INTO YukonRoleProperty VALUES(-21405, -214, 'Manage Point Data', 'UPDATE', 'Controls the ability to edit, delete, or manually add point data values.');
INSERT INTO YukonRoleProperty VALUES(-21406, -214, 'Manage Points', 'UPDATE', 'Controls the ability to view, create, edit, or delete points.');
INSERT INTO YukonRoleProperty VALUES(-21407, -214, 'Manage Notes', 'OWNER', 'Controls the ability to view, create, edit, or delete notes.');

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
INSERT INTO YukonRoleProperty VALUES(-40055,-400,'Opt Out Period',' ','The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)');
INSERT INTO YukonRoleProperty VALUES(-40056,-400,'Opt Out Limits',' ','Contains information on Opt Out limits.');
INSERT INTO YukonRoleProperty VALUES(-40100,-400,'Link FAQ',' ','The customized FAQ link');

INSERT INTO YukonRoleProperty VALUES(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
INSERT INTO YukonRoleProperty VALUES(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-40199,-400,'Sign Out Enabled','true','Allows end-users to see a sign-out link when accessing their account pages.'); 
INSERT INTO YukonRoleProperty VALUES(-40201,-400,'Opt Out Force All Devices','false','Controls access to select individual devices or forces all device selection when opting out. When true, individual device selection is unavailable and all devices are forced to opt out.'); 
INSERT INTO YukonRoleProperty VALUES(-40202,-400,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.'); 
INSERT INTO YukonRoleProperty VALUES(-40203,-400,'Enrollment per Device','false','Displays a second web page that allows for enrollment by individual device per program.');
INSERT INTO YukonRoleProperty VALUES(-40300,-400,'Auto Thermostat Mode Enabled','false','Enables auto mode functionality for the account.enrollment by individual device per program.');

/* Capacitor Control role properties */
insert into YukonRoleProperty values(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
insert into YukonRoleProperty values(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
insert into YukonRoleProperty values(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
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
INSERT INTO YukonRoleProperty VALUES (-90004,-900,'Constraint Check','true','Allow load management program constraints to be CHECKED before starting');
INSERT INTO YukonRoleProperty VALUES (-90005,-900,'Constraint Observe','true','Allow load management program constraints to be OBSERVED before starting');
INSERT INTO YukonRoleProperty VALUES (-90006,-900,'Constraint Override','true','Allow load management program constraints to be OVERRIDDEN before starting');
INSERT INTO YukonRoleProperty VALUES (-90007,-900,'Constraint Default','Check','The default program constraint selection prior to starting a program');
INSERT INTO YukonRoleProperty VALUES (-90008,-900,'Allow Gear Change for Stop','false','Activates the ability to change gears as part of manually stopping a load program'); 
INSERT INTO YukonRoleProperty VALUES (-90009,-900,'Use Pao Permissions','false','Allow access to all load management objects. Set to true to force the use of per pao permissions.');
INSERT INTO YukonRoleProperty VALUES (-90010,-900,'Control Areas','true','Controls access to view Control Areas');
INSERT INTO YukonRoleProperty VALUES (-90011,-900,'Scenarios','true','Controls access to view Scenarios');

INSERT INTO YukonRoleProperty VALUES (-90021,-900,'Control Area Trigger Info','true','Controls access to view Control Area Trigger related information.');
INSERT INTO YukonRoleProperty VALUES (-90024,-900,'Priority','true','Controls access to view Control Area, Program, and Group Priority.');
INSERT INTO YukonRoleProperty VALUES (-90032,-900,'Reduction','true','Controls access to view Program and Group Reduction.');
INSERT INTO YukonRoleProperty VALUES (-90039,-900,'Start Now Checked By Default','true', 'Controls whether the start now checkbox is checked by default in demand response.');
INSERT INTO YukonRoleProperty VALUES (-90040,-900,'Control Duration Default','240', 'Specifies the default duration for a control event in minutes for demand response');
INSERT INTO YukonRoleProperty VALUES (-90041,-900,'Schedule Stop Checked By Default', 'true', 'Controls whether the schedule stop check box is checked by default in demand response.');
INSERT INTO YukonRoleProperty VALUES (-90042,-900,'Start Time Default',' ', 'Specifies the default start time for a control event in the format (hh:mm). It will use the current time if no time has been supplied');
INSERT INTO YukonRoleProperty VALUES (-90043,-900,'Allow DR Control','true','Allow control of demand response control areas, scenarios, programs and groups');
INSERT INTO YukonRoleProperty VALUES (-90044,-900,'Asset Availability','false','Controls access to view Asset Availability for Scenarios, Control Areas, Programs, and Load Groups.');
INSERT INTO YukonRoleProperty VALUES (-90045,-900,'Allow Load Group Control','true','In addition to Allow DR Control, controls access to shed and restore at group level.');
INSERT INTO YukonRoleProperty VALUES (-90046,-900,'Enable ecobee','false','Controls access to actions and information related to ecobee devices.');
INSERT INTO YukonRoleProperty VALUES (-90047,-900,'Allow DR Enable/Disable','true','Controls access to enable or disable control areas,load programs and load groups. Requires Allow DR Control.');
INSERT INTO YukonRoleProperty VALUES (-90048,-900,'Allow Change Gears','true','Controls access to change gears for scenarios, control areas, and load programs. Requires Allow DR Control.');

/* Capacitor Control role properties cont...*/
insert into YukonRoleProperty values (-100205,-1002, 'Capbank Fixed/Static Text', 'Fixed', 'The text to display for fixed/static capbanks');


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
   ServiceType          varchar(60)          not null,
   constraint PK_YUKSER primary key (ServiceID)
)
go

INSERT INTO YukonServices VALUES (-2, 'WebGraph', 'com.cannontech.jmx.services.DynamicWebGraph', 'ServiceManager', 'CLASS_NAME_TYPE');
INSERT INTO YukonServices VALUES (-3, 'Calc_Historical', 'com.cannontech.jmx.services.DynamicCalcHist', 'ServiceManager', 'CLASS_NAME_TYPE');
INSERT INTO YukonServices VALUES (-5, 'MCT410_BulkImporter', 'com.cannontech.jmx.services.DynamicImp', 'ServiceManager', 'CLASS_NAME_TYPE');
INSERT INTO YukonServices VALUES (8, 'PointInjector', 'classpath:com/cannontech/services/points/pointInjectionContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (9, 'Monitors', 'classpath:com/cannontech/services/monitors/monitorsContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (10, 'OptOut', 'classpath:com/cannontech/services/optout/optOutContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (11, 'RawPointHistoryValidation', 'classpath:com/cannontech/services/validation/validationServerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (13, 'Eka', 'classpath:com/cannontech/services/rfn/rfnMeteringContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO yukonServices VALUES (14, 'Inventory Management', 'classpath:com/cannontech/services/dr/inventoryContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE'); 
INSERT INTO YukonServices VALUES (15, 'PorterResponseMonitor', 'classpath:com/cannontech/services/porterResponseMonitor/porterResponseMonitorContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (16, 'SepMessageListener', 'classpath:com/cannontech/services/sepMessageListener/sepMessageListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE'); 
INSERT INTO YukonServices VALUES (17, 'DigiPollingService', 'classpath:com/cannontech/services/digiPollingService/digiPollingService.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (18, 'CymDISTMessageListener', 'classpath:com/cannontech/services/cymDISTService/cymDISTServiceContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (20, 'OpcService','classpath:com/cannontech/services/opc/opcService.xml','ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (21, 'EcobeeMessageListener', 'classpath:com/cannontech/services/ecobeeMessageListener/ecobeeMessageListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (22, 'HoneywellWifiDataListener', 'classpath:com/cannontech/services/honeywellWifiListener/honeywellWifiMessageListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (23, 'BrokerSystemMetricsListener','classpath:com/cannontech/services/BrokerSystemMetricsListener/brokerSystemMetricsListenerContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');
INSERT INTO YukonServices VALUES (24, 'InfrastructureWarnings', 'classpath:com/cannontech/services/infrastructure/infrastructureWarningsContext.xml', 'ServiceManager', 'CONTEXT_FILE_TYPE');

/*==============================================================*/
/* Table: YukonUser                                             */
/*==============================================================*/
create table YukonUser (
   UserID               numeric              not null,
   UserName             nvarchar(64)         not null,
   Password             nvarchar(128)        not null,
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
/* Index: Indx_YukonUser_UserGroupId                            */
/*==============================================================*/
create index Indx_YukonUser_UserGroupId on YukonUser (
UserGroupId ASC
)
go

/*==============================================================*/
/* Table: YukonWebConfiguration                                 */
/*==============================================================*/
create table YukonWebConfiguration (
   ConfigurationID      numeric              not null,
   LogoLocation         varchar(100)         null,
   Description          varchar(500)         null,
   AlternateDisplayName varchar(200)         null,
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
LEFT OUTER JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime
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
LEFT OUTER JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime 
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
LEFT OUTER JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime
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
LEFT JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime                        
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
         on delete cascade
go

alter table AcctThermostatScheduleEntry
   add constraint FK_AccThermSchEnt_AccThermSch foreign key (AcctThermostatScheduleId)
      references AcctThermostatSchedule (AcctThermostatScheduleId)
         on delete cascade
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

alter table BehaviorReport
   add constraint FK_Device_BehaviorReport foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table BehaviorReportValue
   add constraint FK_BehaviorRptVal_BehaviorRpt foreign key (BehaviorReportId)
      references BehaviorReport (BehaviorReportId)
         on delete cascade
go

alter table BehaviorValue
   add constraint FK_BehaviorValue_Behavior foreign key (BehaviorId)
      references Behavior (BehaviorId)
         on delete cascade
go

alter table CALCBASE
   add constraint FK_CalcBase_Point foreign key (POINTID)
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
   add constraint FK_CapBank_Device_ControlDev foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID)
go

alter table CAPBANK
   add constraint FK_CapBank_Device_DeviceId foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table CAPBANK
   add constraint FK_CapBank_Point foreign key (CONTROLPOINTID)
      references POINT (POINTID)
go

alter table CAPBANKADDITIONAL
   add constraint FK_CAPBANKA_CAPBANK foreign key (DeviceID)
      references CAPBANK (DEVICEID)
go

alter table CAPCONTROLAREA
   add constraint FK_CCArea_Point_VoltReduction foreign key (VoltReductionPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLAREA
   add constraint FK_CCArea_YukonPAO foreign key (AreaID)
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
   add constraint FK_CCSpecArea_Point_VoltReduc foreign key (VoltReductionPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSPECIALAREA
   add constraint FK_CCSpecArea_YukonPAO foreign key (AreaID)
      references YukonPAObject (PAObjectID)
go

alter table CAPCONTROLSUBSTATION
   add constraint FK_CCSub_Point_VoltReduct foreign key (VoltReductionPointId)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATION
   add constraint FK_CCSubstation_YukonPAO foreign key (SubstationID)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_DisableBus foreign key (DisableBusPointId)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_PhaseB foreign key (phaseb)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_PhaseC foreign key (phasec)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_Switch foreign key (SwitchPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_VarLoad foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_VoltLoad foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_VoltReduct foreign key (VoltReductionPointId)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSubBus_Point_WattLoad foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CapContrSubBus_YukonPAO foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID)
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
   add constraint FK_CCFeeder_Point_PhaseB foreign key (phaseb)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_CCFeeder_Point_PhaseC foreign key (phasec)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_CCFeeder_Point_VarLoad foreign key (CurrentVarLoadPointID)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_CCFeeder_Point_VoltLoad foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_CCFeeder_Point_WattLoad foreign key (CurrentWattLoadPointID)
      references POINT (POINTID)
go

alter table CapControlFeeder
   add constraint FK_CCFeeder_YukonPAO foreign key (FeederID)
      references YukonPAObject (PAObjectID)
go

alter table CarrierRoute
   add constraint FK_CarrierRoute_Route foreign key (ROUTEID)
      references Route (RouteID)
go

alter table CollectionActionCommandRequest
   add constraint FK_CACR_CollectionAction foreign key (CollectionActionId)
      references CollectionAction (CollectionActionId)
         on delete cascade
go

alter table CollectionActionCommandRequest
   add constraint FK_CACR_CommandRequestExec foreign key (CommandRequestExecId)
      references CommandRequestExec (CommandRequestExecId)
         on delete cascade
go

alter table CollectionActionInput
   add constraint FK_CAInput_CollectionAction foreign key (CollectionActionId)
      references CollectionAction (CollectionActionId)
         on delete cascade
go

alter table CollectionActionRequest
   add constraint FK_CARequest_CollectionAction foreign key (CollectionActionId)
      references CollectionAction (CollectionActionId)
         on delete cascade
go

alter table CollectionActionRequest
   add constraint FK_CARequest_YukonPAObject foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID)
go

alter table CommandRequestExecRequest
   add constraint FK_ComReqExRequest_ComReqExec foreign key (CommandRequestExecId)
      references CommandRequestExec (CommandRequestExecId)
         on delete cascade
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

alter table ControlEvent
   add constraint FK_CONTROLEVENT_YUKONPAOBJECT foreign key (ProgramId)
      references YukonPAObject (PAObjectID)
go

alter table ControlEvent
   add constraint FK_ContEvent_LMContHist foreign key (LMControlHistoryId)
      references LMControlHistory (LMCtrlHistID)
go

alter table ControlEvent
   add constraint FK_ContEvent_LMGroup foreign key (GroupId)
      references LMGroup (DeviceID)
         on delete cascade
go

alter table ControlEventDevice
   add constraint FK_ContEventDev_ContEvent foreign key (ControlEventId)
      references ControlEvent (ControlEventId)
         on delete cascade
go

alter table ControlEventDevice
   add constraint FK_ContEventDev_OptOutEvent foreign key (OptOutEventId)
      references OptOutEvent (OptOutEventId)
         on delete cascade
go

alter table ControlEventDevice
   add constraint FK_ContEventDev_YukonPAObject foreign key (DeviceId)
      references YukonPAObject (PAObjectID)
         on delete cascade
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
   add constraint FK_DeviceCarrierSetting_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEDIALUPSETTINGS
   add constraint FK_DeviceDialupSettings_Device foreign key (DEVICEID)
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
   add constraint FK_DeviceIdlcRemote_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEIED
   add constraint FK_DeviceIed_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICELOADPROFILE
   add constraint FK_DeviceLoadProfile_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEMCTIEDPORT
   add constraint FK_DeviceMctIedPort_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICEMETERGROUP
   add constraint FK_DeviceMeterGroup_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
         on delete cascade
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
   add constraint FK_DeviceScanRate_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DEVICETAPPAGINGSETTINGS
   add constraint FK_DeviceTapPagingSet_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DISPLAY2WAYDATA
   add constraint FK_Display2WayData_Display foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
         on delete cascade
go

alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID)
go

alter table DISPLAYCOLUMNS
   add constraint FK_DisplayColumns_ColumnType foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
go

alter table DISPLAYCOLUMNS
   add constraint FK_DisplayColumns_Display foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM)
         on delete cascade
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

alter table DeviceBehaviorMap
   add constraint FK_Behavior_DeviceBehaviorMap foreign key (BehaviorId)
      references Behavior (BehaviorId)
         on delete cascade
go

alter table DeviceBehaviorMap
   add constraint FK_Device_DeviceBehaviorMap foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table DeviceCBC
   add constraint FK_DeviceCbc_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DeviceCBC
   add constraint FK_DeviceCbc_Route foreign key (ROUTEID)
      references Route (RouteID)
go

alter table DeviceCollectionByField
   add constraint FK_DevCollByField_DevColl foreign key (CollectionId)
      references DeviceCollection (CollectionId)
         on delete cascade
go

alter table DeviceCollectionById
   add constraint FK_DevCollById_DevColl foreign key (CollectionId)
      references DeviceCollection (CollectionId)
         on delete cascade
go

alter table DeviceCollectionById
   add constraint FK_DeviceCollectionById_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
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
   add constraint FK_DeviceDirectCommSet_CommPrt foreign key (PORTID)
      references CommPort (PORTID)
go

alter table DeviceDirectCommSettings
   add constraint FK_DeviceDirectCommSet_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
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

alter table DeviceParent
   add constraint FK_DeviceParent_Device foreign key (DeviceID)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table DeviceParent
   add constraint FK_DeviceParent_Parent foreign key (ParentID)
      references DEVICE (DEVICEID)
go

alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID)
go

alter table DeviceRoutes
   add constraint FK_DeviceRoutes_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table DeviceRoutes
   add constraint FK_DeviceRoutes_Route foreign key (ROUTEID)
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

alter table DmvMeasurementData
   add constraint FK_DmvTestExec_DmvMData foreign key (ExecutionId)
      references DmvTestExecution (ExecutionId)
         on delete cascade
go

alter table DmvTestExecution
   add constraint FK_DmvTestExec_DmvTest foreign key (DmvTestId)
      references DmvTest (DmvTestId)
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

alter table DynamicLcrCommunications
   add constraint FK_Device_DynamicLcrComms foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table DynamicPAOInfo
   add constraint FK_DynPAOInfo_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
         on delete cascade
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

alter table EcobeeReconReportError
   add constraint FK_EcobeeRecRepErr_EcobeeRecRp foreign key (EcobeeReconReportId)
      references EcobeeReconciliationReport (EcobeeReconReportId)
         on delete cascade
go

alter table EnergyCompany
   add constraint FK_EnergyComp_EnergyCompParent foreign key (ParentEnergyCompanyId)
      references EnergyCompany (EnergyCompanyID)
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
   add constraint FK_FdrTranslation_Point foreign key (PointId)
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

alter table GatewayCertificateUpdateEntry
   add constraint FK_GatewayCUEnt_GatewayCertUpd foreign key (UpdateId)
      references GatewayCertificateUpdate (UpdateId)
         on delete cascade
go

alter table GatewayCertificateUpdateEntry
   add constraint FK_GatewayCertUpdateEnt_Device foreign key (GatewayId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table GatewayFirmwareUpdateEntry
   add constraint FK_GatewayFUEnt_GatewayFUUpd foreign key (UpdateId)
      references GatewayFirmwareUpdate (UpdateId)
         on delete cascade
go

alter table GatewayFirmwareUpdateEntry
   add constraint FK_GatewayFirmUpdateEnt_Device foreign key (GatewayId)
      references DEVICE (DEVICEID)
         on delete cascade
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
         on delete cascade
go

alter table GroupPaoPermission
   add constraint FK_GroupPaoPerm_UserGroup foreign key (UserGroupId)
      references UserGroup (UserGroupId)
go

alter table HoneywellWifiThermostat
   add constraint FK_HONEYWELLTHERMOSTAT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID)
go

alter table ImportPendingComm
   add constraint FK_ImpPC_PAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID)
go

alter table InfrastructureWarnings
   add constraint FK_InfWarnings_YukonPAO foreign key (PaoId)
      references YukonPAObject (PAObjectID)
         on delete cascade
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
         on delete cascade
go

alter table InventoryToAcctThermostatSch
   add constraint FK_InvToAcctThermSch_InvBase foreign key (InventoryId)
      references InventoryBase (InventoryID)
         on delete cascade
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
         on delete cascade
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
   add constraint FK_LmGroupEmetcon_LMGroup foreign key (DEVICEID)
      references LMGroup (DeviceID)
go

alter table LMGroupEmetcon
   add constraint FK_LmGroupEmetcon_Route foreign key (ROUTEID)
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

alter table LMGroupHoneywellWiFi
   add constraint FK_LMGroupHoneywellWiFiLMGroup foreign key (DeviceId)
      references LMGroup (DeviceID)
         on delete cascade
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
   add constraint FK_LmGroupVersacom_Route foreign key (ROUTEID)
      references Route (RouteID)
go

alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID)
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

alter table LMNestLoadShapingGear
   add constraint FK_NLSGear_LMProgramDirectGear foreign key (GearId)
      references LMProgramDirectGear (GearID)
         on delete cascade
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
   add constraint FK_MacroRoute_Route_RouteId foreign key (ROUTEID)
      references Route (RouteID)
go

alter table MACROROUTE
   add constraint FK_MacroRoute_Route_SingleRtId foreign key (SINGLEROUTEID)
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

alter table NestSyncDetail
   add constraint FK_NestSync_NestSyncDetail foreign key (SyncId)
      references NestSync (SyncId)
         on delete cascade
go

alter table NestSyncValue
   add constraint FK_NSDetail_NSValue foreign key (SyncDetailId)
      references NestSyncDetail (SyncDetailId)
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
   add constraint FK_PointAccumulator_Point foreign key (POINTID)
      references POINT (POINTID)
go

alter table POINTANALOG
   add constraint FK_PointAnalog_Point foreign key (POINTID)
      references POINT (POINTID)
go

alter table POINTLIMITS
   add constraint FK_PointLimits_Point foreign key (POINTID)
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
   add constraint FK_PortDialupModem_CommPort foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTLOCALSERIAL
   add constraint FK_PortLocalSerial_CommPort foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTRADIOSETTINGS
   add constraint FK_PortRadioSettings_CommPort foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTSETTINGS
   add constraint FK_PortSettings_CommPort foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PORTTERMINALSERVER
   add constraint FK_PortTerminalServer_CommPort foreign key (PORTID)
      references CommPort (PORTID)
go

alter table PROFILEPEAKRESULT
   add constraint FK_PROFILEPKRSLT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table PaoLocation
   add constraint FK_PaoLocation_YukonPAObject foreign key (PAObjectId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table PaoNote
   add constraint FK_PaoNote_YukonPAObject foreign key (PaObjectId)
      references YukonPAObject (PAObjectID)
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
   add constraint FK_PortTiming_CommPort foreign key (PORTID)
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

alter table RPHTag
   add constraint FK_RPHTag_RPH foreign key (ChangeId)
      references RAWPOINTHISTORY (CHANGEID)
         on delete cascade
go

alter table RawPointHistoryDependentJob
   add constraint FK_RPHDependentJob_Job foreign key (JobId, JobGroupId)
      references JOB (JobID, JobGroupId)
         on delete cascade
go

alter table RecentPointValue
   add constraint FK_RPV_Point foreign key (PointID)
      references POINT (POINTID)
         on delete cascade
go

alter table RecentPointValue
   add constraint FK_RPV_YukonPAObject foreign key (PAObjectID)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table Regulator
   add constraint FK_Reg_PAO foreign key (RegulatorId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table RegulatorEvents
   add constraint FK_RegulatorEvents_Device foreign key (RegulatorId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table RegulatorToZoneMapping
   add constraint FK_RegulatorToZoneMap_Device foreign key (RegulatorId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table RegulatorToZoneMapping
   add constraint FK_ZoneReg_Zone foreign key (ZoneId)
      references Zone (ZoneId)
         on delete cascade
go

alter table RepeaterRoute
   add constraint FK_RepeaterRoute_Device foreign key (DEVICEID)
      references DEVICE (DEVICEID)
go

alter table RepeaterRoute
   add constraint FK_RepeaterRoute_Route foreign key (ROUTEID)
      references Route (RouteID)
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

alter table RfnAddress
   add constraint FK_RfnAddress_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table RfnBroadcastEventDeviceStatus
   add constraint FK_RfnBcstEvntDev_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

alter table RfnBroadcastEventDeviceStatus
   add constraint FK_RfnBcstEvntDev_RfnBcstEvnt foreign key (RfnBroadcastEventId)
      references RfnBroadcastEvent (RfnBroadcastEventId)
         on delete cascade
go

alter table RfnBroadcastEventSummary
   add constraint FK_RFNBROAD_REFERENCE_RFNBROAD foreign key (RfnBroadcastEventId)
      references RfnBroadcastEvent (RfnBroadcastEventId)
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

alter table SmartNotificationEventParam
   add constraint FK_SmartNotifEP_SmartNotifE foreign key (EventId)
      references SmartNotificationEvent (EventId)
         on delete cascade
go

alter table SmartNotificationSub
   add constraint FK_SmartNotifSub_YukonUser foreign key (UserId)
      references YukonUser (UserID)
         on delete cascade
go

alter table SmartNotificationSubParam
   add constraint FK_SmartNotifSP_SmartNotifS foreign key (SubscriptionId)
      references SmartNotificationSub (SubscriptionId)
         on delete cascade
go

alter table State
   add constraint FK_State_StateGroup foreign key (StateGroupId)
      references StateGroup (StateGroupId)
go

alter table State
   add constraint FK_YkIm_St foreign key (ImageId)
      references YukonImage (ImageID)
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
   add constraint FK_TemplateColumns_ColumnType foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM)
go

alter table TEMPLATECOLUMNS
   add constraint FK_TemplateColumns_Template foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM)
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
         on delete cascade
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
         on delete cascade
go

alter table ThermostatEventHistory
   add constraint FK_ThermEventHist_InvBase foreign key (ThermostatId)
      references InventoryBase (InventoryID)
         on delete cascade
go

alter table UsageThresholdReportRow
   add constraint FK_UTReportRow_Point foreign key (PointId)
      references POINT (POINTID)
         on delete cascade
go

alter table UsageThresholdReportRow
   add constraint FK_UTReportRow_UTReport foreign key (UsageThresholdReportId)
      references UsageThresholdReport (UsageThresholdReportId)
         on delete cascade
go

alter table UsageThresholdReportRow
   add constraint FK_UTReportRow_YukonPAObject foreign key (PaoId)
      references YukonPAObject (PAObjectID)
         on delete cascade
go

alter table UserDashboard
   add constraint FK_UserDashboard_Dashboard foreign key (DashboardId)
      references Dashboard (DashboardId)
         on delete cascade
go

alter table UserDashboard
   add constraint FK_UserDashboard_YukonUser foreign key (UserId)
      references YukonUser (UserID)
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
         on delete cascade
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

alter table Widget
   add constraint FK_Widget_Dashboard foreign key (DashboardId)
      references Dashboard (DashboardId)
         on delete cascade
go

alter table WidgetSettings
   add constraint FK_WidgetSettings_Widget foreign key (WidgetId)
      references Widget (WidgetId)
         on delete cascade
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



IF OBJECT_ID ('sp_SmartIndexMaintenance') IS NOT NULL
    DROP PROCEDURE sp_SmartIndexMaintenance;
go

CREATE PROCEDURE sp_SmartIndexMaintenance AS
BEGIN TRY

    SET NOCOUNT ON;

    DECLARE @start_time     DATETIME;

    DECLARE @objectid       int;
    DECLARE @indexid        int;
    DECLARE @partitioncount bigint;
    DECLARE @schemaname     nvarchar(130);
    DECLARE @objectname     nvarchar(130);
    DECLARE @indexname      nvarchar(130);
    DECLARE @partitionnum   bigint;
    DECLARE @partitions     bigint;
    DECLARE @frag           float;
    DECLARE @pagecount      int;
    DECLARE @command        nvarchar(4000);
    DECLARE @output         nvarchar(4000);

    DECLARE @page_count_minimum   smallint
    DECLARE @frag_min_reorg       float
    DECLARE @frag_min_rebuild     float

    SET @page_count_minimum       = 50
    SET @frag_min_reorg           = 10.0
    SET @frag_min_rebuild         = 30.0

    /* Conditionally select tables and indexes from the sys.dm_db_index_physical_stats function and convert object and index IDs to names. */
    /* Store records in temp table work_to_do */
    SELECT object_id AS ObjectId, 
        index_id AS IndexId, 
        partition_number AS PartNum, 
        avg_fragmentation_in_percent AS AvgFrag, 
        page_count AS PageCount
        INTO #work_to_do
    FROM sys.dm_db_index_physical_stats (DB_ID(), NULL, NULL , NULL, 'LIMITED')
        WHERE avg_fragmentation_in_percent > @frag_min_reorg
        AND index_id > 0
        AND page_count > @page_count_minimum;

    /* Declare the cursor for the list of partitions (from temp table work_to_do) to be processed. */
    DECLARE partitions CURSOR FOR SELECT * FROM #work_to_do;

    /* Open the cursor. */
    OPEN partitions;

    /* Loop through the partitions. */
    FETCH NEXT FROM partitions INTO @objectid, @indexid, @partitionnum, @frag, @pagecount;
    WHILE @@FETCH_STATUS = 0
    BEGIN

        SELECT @objectname = QUOTENAME(o.name), @schemaname = QUOTENAME(s.name)
        FROM sys.objects AS o JOIN sys.schemas as s ON s.schema_id = o.schema_id
            WHERE o.object_id = @objectid;

        SELECT @indexname = QUOTENAME(name)
        FROM sys.indexes
            WHERE  object_id = @objectid AND index_id = @indexid;

        SELECT @partitioncount = count (*)
        FROM sys.partitions
            WHERE object_id = @objectid AND index_id = @indexid;

        SET @command = N'ALTER INDEX ' + @indexname + N' ON ' + @schemaname + N'.' + @objectname;

        IF @frag > @frag_min_rebuild
            SET @command = @command + N' REBUILD';
        ELSE IF @frag > @frag_min_reorg
            SET @command = @command + N' REORGANIZE';

        IF @partitioncount > 1
            SET @command = @command + N' PARTITION=' + CAST(@partitionnum AS nvarchar(10));

        SET @output = @command + N' : Fragmentation: ' + CAST(@frag AS varchar(15)) + ' : Page Count: ' + CAST(@pagecount AS varchar(15));

        SET @start_time = CURRENT_TIMESTAMP;
        /* Execute the REBUILD or REORGANIZE command on the table index */
        EXEC (@command);

        /* update the table index statistics, only do when reorganizing */
        IF @frag > @frag_min_reorg AND @frag <= @frag_min_rebuild
        BEGIN
            SET @output = @output + N' : Update Statistics';
            SET @command = N'UPDATE STATISTICS ' +  @schemaname + N'.' + @objectname + ' ' +  @indexname + ' WITH FULLSCAN';
            /* Execute the UPDATE STATISTICS command on the table index */
            EXEC (@command);
        END;

        SET @output = @output + N' : Time ' + CAST(DATEDIFF(millisecond, @start_time, CURRENT_TIMESTAMP) AS varchar(20)) + ' millis';
        INSERT INTO StoredProcedureLog VALUES (
            (SELECT ISNULL(MAX(EntryId) + 1, 1) FROM StoredProcedureLog), 
            'sp_SmartIndexMaintenance', 
            GETDATE(), @output);

        FETCH NEXT FROM partitions INTO @objectid, @indexid, @partitionnum, @frag, @pagecount;
    END;

    /* Close and deallocate the cursor. */
    CLOSE partitions;
    DEALLOCATE partitions;

    /* Drop the temporary table. */
    DROP TABLE #work_to_do;
    SET NOCOUNT OFF;

END TRY
BEGIN CATCH
    DECLARE @ErrorNumber INT = ERROR_NUMBER();
    DECLARE @ErrorLine INT = ERROR_LINE();
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();

    SET @output = N'ERROR: ' + CAST(@ErrorNumber AS VARCHAR(10)) + ' Message:' +  @ErrorMessage +
            ' ErrorLine:' + CAST(@ErrorLine AS VARCHAR(10)) + ' ErrorState:' + CAST(@ErrorState AS VARCHAR(10));
    INSERT INTO StoredProcedureLog VALUES (
            (SELECT ISNULL(MAX(EntryId) + 1, 1) FROM StoredProcedureLog), 
            'sp_SmartIndexMaintenance', 
            GETDATE(), @output);
    RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH

INSERT INTO StoredProcedureLog VALUES (
            (SELECT ISNULL(MAX(EntryId) + 1, 1) FROM StoredProcedureLog), 
            'sp_SmartIndexMaintenance', 
            GETDATE(), 'Smart Index Maintenance Complete');
go


