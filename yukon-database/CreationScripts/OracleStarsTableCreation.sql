/*==============================================================*/
/* Database name:  STARS                                        */
/* DBMS name:      CTI Oracle 8.1.5                             */
/* Created on:     2/17/2004 10:05:16 AM                        */
/*==============================================================*/


drop table AccountSite cascade constraints
/


drop table ApplianceAirConditioner cascade constraints
/


drop table ApplianceBase cascade constraints
/


drop table ApplianceCategory cascade constraints
/


drop table ApplianceDualFuel cascade constraints
/


drop table ApplianceGenerator cascade constraints
/


drop table ApplianceGrainDryer cascade constraints
/


drop table ApplianceHeatPump cascade constraints
/


drop table ApplianceIrrigation cascade constraints
/


drop table ApplianceStorageHeat cascade constraints
/


drop table ApplianceWaterHeater cascade constraints
/


drop table CallReportBase cascade constraints
/


drop table CustomerAccount cascade constraints
/


drop table CustomerFAQ cascade constraints
/


drop table CustomerResidence cascade constraints
/


drop table ECToAccountMapping cascade constraints
/


drop table ECToCallReportMapping cascade constraints
/


drop table ECToGenericMapping cascade constraints
/


drop table ECToInventoryMapping cascade constraints
/


drop table ECToLMCustomerEventMapping cascade constraints
/


drop table ECToWorkOrderMapping cascade constraints
/


drop table InterviewQuestion cascade constraints
/


drop table InventoryBase cascade constraints
/


drop table LMCustomerEventBase cascade constraints
/


drop table LMHardwareBase cascade constraints
/


drop table LMHardwareConfiguration cascade constraints
/


drop table LMHardwareEvent cascade constraints
/


drop table LMProgramEvent cascade constraints
/


drop table LMProgramWebPublishing cascade constraints
/


drop table LMThermostatManualEvent cascade constraints
/


drop table LMThermostatSeason cascade constraints
/


drop table LMThermostatSeasonEntry cascade constraints
/


drop table ServiceCompany cascade constraints
/


drop table SiteInformation cascade constraints
/


drop table Substation cascade constraints
/


drop table WorkOrderBase cascade constraints
/


/*==============================================================*/
/* Table : AccountSite                                          */
/*==============================================================*/


create table AccountSite  (
   AccountSiteID        NUMBER                           not null,
   SiteInformationID    NUMBER,
   SiteNumber           VARCHAR2(40)                     not null,
   StreetAddressID      NUMBER,
   PropertyNotes        VARCHAR2(300)
)
/


alter table AccountSite
   add constraint PK_ACCOUNTSITE primary key (AccountSiteID)
/


/*==============================================================*/
/* Index: CstSrvCstProp_FK                                      */
/*==============================================================*/
create index CstSrvCstProp_FK on AccountSite (
   SiteInformationID ASC
)
/


/*==============================================================*/
/* Table : ApplianceAirConditioner                              */
/*==============================================================*/


create table ApplianceAirConditioner  (
   ApplianceID          NUMBER                           not null,
   TonnageID            NUMBER,
   TypeID               NUMBER
)
/


alter table ApplianceAirConditioner
   add constraint PK_APPLIANCEAIRCONDITIONER primary key (ApplianceID)
/


/*==============================================================*/
/* Table : ApplianceBase                                        */
/*==============================================================*/


create table ApplianceBase  (
   ApplianceID          NUMBER                           not null,
   AccountID            NUMBER                           not null,
   ApplianceCategoryID  NUMBER                           not null,
   LMProgramID          NUMBER,
   YearManufactured     NUMBER,
   ManufacturerID       NUMBER,
   LocationID           NUMBER,
   KWCapacity           NUMBER,
   EfficiencyRating     NUMBER,
   Notes                VARCHAR2(500),
   ModelNumber          VARCHAR2(40)                     not null
)
/


alter table ApplianceBase
   add constraint PK_APPLIANCEBASE primary key (ApplianceID)
/


/*==============================================================*/
/* Index: CstAcc_CstLdInfo_FK                                   */
/*==============================================================*/
create index CstAcc_CstLdInfo_FK on ApplianceBase (
   AccountID ASC
)
/


/*==============================================================*/
/* Index: CstLdTy_CstLdInf_FK                                   */
/*==============================================================*/
create index CstLdTy_CstLdInf_FK on ApplianceBase (
   ApplianceCategoryID ASC
)
/


/*==============================================================*/
/* Table : ApplianceCategory                                    */
/*==============================================================*/


create table ApplianceCategory  (
   ApplianceCategoryID  NUMBER                           not null,
   Description          VARCHAR2(40),
   CategoryID           NUMBER,
   WebConfigurationID   NUMBER
)
/


alter table ApplianceCategory
   add constraint PK_APPLIANCECATEGORY primary key (ApplianceCategoryID)
/


/*==============================================================*/
/* Table : ApplianceDualFuel                                    */
/*==============================================================*/


create table ApplianceDualFuel  (
   ApplianceID          NUMBER                           not null,
   SwitchOverTypeID     NUMBER                           not null,
   SecondaryKWCapacity  NUMBER                           not null,
   SecondaryEnergySourceID NUMBER                           not null
)
/


alter table ApplianceDualFuel
   add constraint PK_APPLIANCEDUALFUEL primary key (ApplianceID)
/


/*==============================================================*/
/* Table : ApplianceGenerator                                   */
/*==============================================================*/


create table ApplianceGenerator  (
   ApplianceID          NUMBER                           not null,
   TransferSwitchTypeID NUMBER                           not null,
   TransferSwitchMfgID  NUMBER                           not null,
   PeakKWCapacity       NUMBER                           not null,
   FuelCapGallons       NUMBER                           not null,
   StartDelaySeconds    NUMBER                           not null
)
/


alter table ApplianceGenerator
   add constraint PK_APPLIANCEGENERATOR primary key (ApplianceID)
/


/*==============================================================*/
/* Table : ApplianceGrainDryer                                  */
/*==============================================================*/


create table ApplianceGrainDryer  (
   ApplianceID          NUMBER                           not null,
   DryerTypeID          NUMBER                           not null,
   BinSizeID            NUMBER                           not null,
   BlowerEnergySourceID NUMBER                           not null,
   BlowerHorsePowerID   NUMBER                           not null,
   BlowerHeatSourceID   NUMBER                           not null
)
/


alter table ApplianceGrainDryer
   add constraint PK_APPLIANCEGRAINDRYER primary key (ApplianceID)
/


/*==============================================================*/
/* Table : ApplianceHeatPump                                    */
/*==============================================================*/


create table ApplianceHeatPump  (
   ApplianceID          NUMBER                           not null,
   PumpTypeID           NUMBER                           not null,
   StandbySourceID      NUMBER                           not null,
   SecondsDelayToRestart NUMBER                           not null,
   PumpSizeID           NUMBER                           not null
)
/


alter table ApplianceHeatPump
   add constraint PK_APPLIANCEHEATPUMP primary key (ApplianceID)
/


/*==============================================================*/
/* Table : ApplianceIrrigation                                  */
/*==============================================================*/


create table ApplianceIrrigation  (
   ApplianceID          NUMBER                           not null,
   IrrigationTypeID     NUMBER                           not null,
   HorsePowerID         NUMBER                           not null,
   EnergySourceID       NUMBER                           not null,
   SoilTypeID           NUMBER                           not null,
   MeterLocationID      NUMBER                           not null,
   MeterVoltageID       NUMBER                           not null
)
/


alter table ApplianceIrrigation
   add constraint PK_APPLIANCEIRRIGATION primary key (ApplianceID)
/


/*==============================================================*/
/* Table : ApplianceStorageHeat                                 */
/*==============================================================*/


create table ApplianceStorageHeat  (
   ApplianceID          NUMBER                           not null,
   StorageTypeID        NUMBER                           not null,
   PeakKWCapacity       NUMBER                           not null,
   HoursToRecharge      NUMBER                           not null
)
/


alter table ApplianceStorageHeat
   add constraint PK_APPLIANCESTORAGEHEAT primary key (ApplianceID)
/


/*==============================================================*/
/* Table : ApplianceWaterHeater                                 */
/*==============================================================*/


create table ApplianceWaterHeater  (
   ApplianceID          NUMBER                           not null,
   NumberOfGallonsID    NUMBER,
   EnergySourceID       NUMBER                           not null,
   NumberOfElements     NUMBER                           not null
)
/


alter table ApplianceWaterHeater
   add constraint PK_APPLIANCEWATERHEATER primary key (ApplianceID)
/


/*==============================================================*/
/* Table : CallReportBase                                       */
/*==============================================================*/


create table CallReportBase  (
   CallID               NUMBER                           not null,
   CallNumber           VARCHAR2(20),
   CallTypeID           NUMBER,
   DateTaken            DATE,
   TakenBy              VARCHAR2(30),
   Description          VARCHAR2(300),
   AccountID            NUMBER
)
/


alter table CallReportBase
   add constraint PK_CALLREPORTBASE primary key (CallID)
/


/*==============================================================*/
/* Table : CustomerAccount                                      */
/*==============================================================*/


create table CustomerAccount  (
   AccountID            NUMBER                           not null,
   AccountSiteID        NUMBER,
   AccountNumber        VARCHAR2(40),
   CustomerID           NUMBER                           not null,
   BillingAddressID     NUMBER,
   AccountNotes         VARCHAR2(200)
)
/


alter table CustomerAccount
   add constraint PK_CUSTOMERACCOUNT primary key (AccountID)
/


/*==============================================================*/
/* Index: CstAccCstPro_FK                                       */
/*==============================================================*/
create index CstAccCstPro_FK on CustomerAccount (
   AccountSiteID ASC
)
/


/*==============================================================*/
/* Table : CustomerFAQ                                          */
/*==============================================================*/


create table CustomerFAQ  (
   QuestionID           NUMBER                           not null,
   SubjectID            NUMBER,
   Question             VARCHAR2(200),
   Answer               VARCHAR2(500)
)
/


alter table CustomerFAQ
   add constraint PK_CUSTOMERFAQ primary key (QuestionID)
/


/*==============================================================*/
/* Table : CustomerResidence                                    */
/*==============================================================*/


create table CustomerResidence  (
   AccountSiteID        NUMBER                           not null,
   ResidenceTypeID      NUMBER                           not null,
   ConstructionMaterialID NUMBER                           not null,
   DecadeBuiltID        NUMBER                           not null,
   SquareFeetID         NUMBER                           not null,
   InsulationDepthID    NUMBER                           not null,
   GeneralConditionID   NUMBER                           not null,
   MainCoolingSystemID  NUMBER                           not null,
   MainHeatingSystemID  NUMBER                           not null,
   NumberOfOccupantsID  NUMBER                           not null,
   OwnershipTypeID      NUMBER                           not null,
   MainFuelTypeID       NUMBER                           not null,
   Notes                VARCHAR2(300)
)
/


alter table CustomerResidence
   add constraint PK_CUSTOMERRESIDENCE primary key (AccountSiteID)
/


/*==============================================================*/
/* Table : ECToAccountMapping                                   */
/*==============================================================*/


create table ECToAccountMapping  (
   EnergyCompanyID      NUMBER                           not null,
   AccountID            NUMBER                           not null
)
/


alter table ECToAccountMapping
   add constraint PK_ECTOACCOUNTMAPPING primary key (EnergyCompanyID, AccountID)
/


/*==============================================================*/
/* Table : ECToCallReportMapping                                */
/*==============================================================*/


create table ECToCallReportMapping  (
   EnergyCompanyID      NUMBER                           not null,
   CallReportID         NUMBER                           not null
)
/


alter table ECToCallReportMapping
   add constraint PK_ECTOCALLREPORTMAPPING primary key (EnergyCompanyID, CallReportID)
/


/*==============================================================*/
/* Table : ECToGenericMapping                                   */
/*==============================================================*/


create table ECToGenericMapping  (
   EnergyCompanyID      NUMBER                           not null,
   ItemID               NUMBER                           not null,
   MappingCategory      VARCHAR2(40)                     not null
)
/


alter table ECToGenericMapping
   add constraint PK_ECTOGENERICMAPPING primary key (EnergyCompanyID, ItemID, MappingCategory)
/


/*==============================================================*/
/* Table : ECToInventoryMapping                                 */
/*==============================================================*/


create table ECToInventoryMapping  (
   EnergyCompanyID      NUMBER                           not null,
   InventoryID          NUMBER                           not null
)
/


alter table ECToInventoryMapping
   add constraint PK_ECTOINVENTORYMAPPING primary key (EnergyCompanyID, InventoryID)
/


/*==============================================================*/
/* Table : ECToLMCustomerEventMapping                           */
/*==============================================================*/


create table ECToLMCustomerEventMapping  (
   EnergyCompanyID      NUMBER                           not null,
   EventID              NUMBER                           not null
)
/


alter table ECToLMCustomerEventMapping
   add constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key (EnergyCompanyID, EventID)
/


/*==============================================================*/
/* Table : ECToWorkOrderMapping                                 */
/*==============================================================*/


create table ECToWorkOrderMapping  (
   EnergyCompanyID      NUMBER                           not null,
   WorkOrderID          NUMBER                           not null
)
/


alter table ECToWorkOrderMapping
   add constraint PK_ECTOWORKORDERMAPPING primary key (EnergyCompanyID, WorkOrderID)
/


/*==============================================================*/
/* Table : InterviewQuestion                                    */
/*==============================================================*/


create table InterviewQuestion  (
   QuestionID           NUMBER                           not null,
   QuestionType         NUMBER,
   Question             VARCHAR2(200),
   Mandatory            VARCHAR2(1),
   DisplayOrder         NUMBER,
   AnswerType           NUMBER,
   ExpectedAnswer       NUMBER
)
/


alter table InterviewQuestion
   add constraint PK_INTERVIEWQUESTION primary key (QuestionID)
/


/*==============================================================*/
/* Table : InventoryBase                                        */
/*==============================================================*/


create table InventoryBase  (
   InventoryID          NUMBER                           not null,
   AccountID            NUMBER,
   InstallationCompanyID NUMBER,
   CategoryID           NUMBER                           not null,
   ReceiveDate          DATE,
   InstallDate          DATE,
   RemoveDate           DATE,
   AlternateTrackingNumber VARCHAR2(40),
   VoltageID            NUMBER,
   Notes                VARCHAR2(500),
   DeviceID             NUMBER,
   DeviceLabel          VARCHAR2(60)
)
/


alter table InventoryBase
   add constraint PK_INVENTORYBASE primary key (InventoryID)
/


/*==============================================================*/
/* Index: CstAccCstHrdB_FK                                      */
/*==============================================================*/
create index CstAccCstHrdB_FK on InventoryBase (
   AccountID ASC
)
/


/*==============================================================*/
/* Index: HrdInst_CstHrdBs_FK                                   */
/*==============================================================*/
create index HrdInst_CstHrdBs_FK on InventoryBase (
   InstallationCompanyID ASC
)
/


/*==============================================================*/
/* Table : LMCustomerEventBase                                  */
/*==============================================================*/


create table LMCustomerEventBase  (
   EventID              NUMBER                           not null,
   EventTypeID          NUMBER                           not null,
   ActionID             NUMBER                           not null,
   EventDateTime        DATE,
   Notes                VARCHAR2(100),
   AuthorizedBy         VARCHAR2(40)
)
/


alter table LMCustomerEventBase
   add constraint PK_LMCUSTOMEREVENTBASE primary key (EventID)
/


/*==============================================================*/
/* Table : LMHardwareBase                                       */
/*==============================================================*/


create table LMHardwareBase  (
   InventoryID          NUMBER                           not null,
   ManufacturerSerialNumber VARCHAR2(30),
   LMHardwareTypeID     NUMBER                           not null
)
/


alter table LMHardwareBase
   add constraint PK_LMHARDWAREBASE primary key (InventoryID)
/


/*==============================================================*/
/* Table : LMHardwareConfiguration                              */
/*==============================================================*/


create table LMHardwareConfiguration  (
   InventoryID          NUMBER                           not null,
   ApplianceID          NUMBER                           not null,
   AddressingGroupID    NUMBER
)
/


alter table LMHardwareConfiguration
   add constraint PK_LMHARDWARECONFIGURATION primary key (InventoryID, ApplianceID)
/


/*==============================================================*/
/* Index: LmHrd_LmHrdCfg_FK                                     */
/*==============================================================*/
create index LmHrd_LmHrdCfg_FK on LMHardwareConfiguration (
   InventoryID ASC
)
/


/*==============================================================*/
/* Index: CstLdIn_LMHrdCfg_FK                                   */
/*==============================================================*/
create index CstLdIn_LMHrdCfg_FK on LMHardwareConfiguration (
   ApplianceID ASC
)
/


/*==============================================================*/
/* Table : LMHardwareEvent                                      */
/*==============================================================*/


create table LMHardwareEvent  (
   EventID              NUMBER                           not null,
   InventoryID          NUMBER                           not null
)
/


alter table LMHardwareEvent
   add constraint PK_LMHARDWAREEVENT primary key (EventID)
/


/*==============================================================*/
/* Table : LMProgramEvent                                       */
/*==============================================================*/


create table LMProgramEvent  (
   EventID              NUMBER                           not null,
   AccountID            NUMBER                           not null,
   LMProgramID          NUMBER
)
/


alter table LMProgramEvent
   add constraint PK_LMPROGRAMEVENT primary key (EventID)
/


/*==============================================================*/
/* Table : LMProgramWebPublishing                               */
/*==============================================================*/


create table LMProgramWebPublishing  (
   ApplianceCategoryID  NUMBER                           not null,
   LMProgramID          NUMBER                           not null,
   WebsettingsID        NUMBER,
   ChanceOfControlID    NUMBER
)
/


alter table LMProgramWebPublishing
   add constraint PK_LMPROGRAMWEBPUBLISHING primary key (ApplianceCategoryID, LMProgramID)
/


/*==============================================================*/
/* Table : LMThermostatManualEvent                              */
/*==============================================================*/


create table LMThermostatManualEvent  (
   EventID              NUMBER                           not null,
   InventoryID          NUMBER                           not null,
   PreviousTemperature  NUMBER,
   HoldTemperature      VARCHAR2(1),
   OperationStateID     NUMBER,
   FanOperationID       NUMBER
)
/


alter table LMThermostatManualEvent
   add constraint PK_LMTHERMOSTATMANUALEVENT primary key (EventID)
/


/*==============================================================*/
/* Table : LMThermostatSeason                                   */
/*==============================================================*/


create table LMThermostatSeason  (
   SeasonID             NUMBER                           not null,
   InventoryID          NUMBER,
   WebConfigurationID   NUMBER,
   StartDate            DATE,
   DisplayOrder         NUMBER
)
/


alter table LMThermostatSeason
   add constraint PK_LMTHERMOSTATSEASON primary key (SeasonID)
/


/*==============================================================*/
/* Table : LMThermostatSeasonEntry                              */
/*==============================================================*/


create table LMThermostatSeasonEntry  (
   EntryID              NUMBER                           not null,
   SeasonID             NUMBER                           not null,
   TimeOfWeekID         NUMBER                           not null,
   StartTime            NUMBER                           not null,
   Temperature          NUMBER
)
/


alter table LMThermostatSeasonEntry
   add constraint PK_LMTHERMOSTATSEASONENTRY primary key (EntryID)
/


/*==============================================================*/
/* Table : ServiceCompany                                       */
/*==============================================================*/


create table ServiceCompany  (
   CompanyID            NUMBER                           not null,
   CompanyName          VARCHAR2(40),
   AddressID            NUMBER,
   MainPhoneNumber      VARCHAR2(14),
   MainFaxNumber        VARCHAR2(14),
   PrimaryContactID     NUMBER,
   HIType               VARCHAR2(40)
)
/


alter table ServiceCompany
   add constraint PK_SERVICECOMPANY primary key (CompanyID)
/


/*==============================================================*/
/* Table : SiteInformation                                      */
/*==============================================================*/


create table SiteInformation  (
   SiteID               NUMBER                           not null,
   Feeder               VARCHAR2(20),
   Pole                 VARCHAR2(20),
   TransformerSize      VARCHAR2(20),
   ServiceVoltage       VARCHAR2(20),
   SubstationID         NUMBER
)
/


alter table SiteInformation
   add constraint PK_SITEINFORMATION primary key (SiteID)
/


/*==============================================================*/
/* Table : Substation                                           */
/*==============================================================*/


create table Substation  (
   SubstationID         NUMBER                           not null,
   SubstationName       VARCHAR2(50),
   RouteID              NUMBER
)
/


alter table Substation
   add constraint PK_SUBSTATION primary key (SubstationID)
/


/*==============================================================*/
/* Table : WorkOrderBase                                        */
/*==============================================================*/


create table WorkOrderBase  (
   OrderID              NUMBER                           not null,
   OrderNumber          VARCHAR2(20),
   WorkTypeID           NUMBER                           not null,
   CurrentStateID       NUMBER                           not null,
   ServiceCompanyID     NUMBER,
   DateReported         DATE,
   OrderedBy            VARCHAR2(30),
   Description          VARCHAR2(200),
   DateScheduled        DATE,
   DateCompleted        DATE,
   ActionTaken          VARCHAR2(200),
   AccountID            NUMBER
)
/


alter table WorkOrderBase
   add constraint PK_WORKORDERBASE primary key (OrderID)
/


alter table InventoryBase
   add constraint FK_CUS_CSTA_CUS3 foreign key (AccountID)
      references CustomerAccount (AccountID)
/


alter table CustomerAccount
   add constraint FK_CUS_CSTA_CUS2 foreign key (AccountSiteID)
      references AccountSite (AccountSiteID)
/


alter table ApplianceBase
   add constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
      references CustomerAccount (AccountID)
/


alter table LMHardwareConfiguration
   add constraint FK_LMH_CSTL_CUS2 foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table ApplianceBase
   add constraint FK_APP_CSTL_APP foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
/


alter table AccountSite
   add constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
      references SiteInformation (SiteID)
/


alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst1 foreign key (SecondaryEnergySourceID)
      references YukonListEntry
/


alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst2 foreign key (SwitchOverTypeID)
      references YukonListEntry
/


alter table ApplianceGenerator
   add constraint FK_AppGn_YkLst1 foreign key (TransferSwitchMfgID)
      references YukonListEntry
/


alter table ApplianceGenerator
   add constraint FK_AppGn_YkLst2 foreign key (TransferSwitchTypeID)
      references YukonListEntry
/


alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst1 foreign key (BinSizeID)
      references YukonListEntry
/


alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst2 foreign key (BlowerEnergySourceID)
      references YukonListEntry
/


alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst3 foreign key (DryerTypeID)
      references YukonListEntry
/


alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst5 foreign key (BlowerHorsePowerID)
      references YukonListEntry
/


alter table ApplianceGrainDryer
   add constraint FK_AppGrDr_YkLst6 foreign key (BlowerHeatSourceID)
      references YukonListEntry
/


alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst1 foreign key (PumpTypeID)
      references YukonListEntry
/


alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst2 foreign key (StandbySourceID)
      references YukonListEntry
/


alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst1 foreign key (EnergySourceID)
      references YukonListEntry
/


alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst2 foreign key (HorsePowerID)
      references YukonListEntry
/


alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst3 foreign key (IrrigationTypeID)
      references YukonListEntry
/


alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst6 foreign key (MeterLocationID)
      references YukonListEntry
/


alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst5 foreign key (SoilTypeID)
      references YukonListEntry
/


alter table ApplianceWaterHeater
   add constraint FK_AppWtHt_YkLst foreign key (EnergySourceID)
      references YukonListEntry
/


alter table InventoryBase
   add constraint FK_CUS_HRDI_HAR2 foreign key (InstallationCompanyID)
      references ServiceCompany (CompanyID)
/


alter table LMHardwareConfiguration
   add constraint FK_LMH_LMHR_LMH foreign key (InventoryID)
      references LMHardwareBase (InventoryID)
/


alter table AccountSite
   add constraint FK_AccS_CstAd foreign key (StreetAddressID)
      references
/


alter table ApplianceWaterHeater
   add constraint FK_ApWtrHt_YkLsE foreign key (NumberOfGallonsID)
      references YukonListEntry
/


alter table ApplianceBase
   add constraint FK_AppBs_LMPr foreign key (LMProgramID)
      references
/


alter table ApplianceDualFuel
   add constraint FK_AppDlF_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table ApplianceGenerator
   add constraint FK_AppGen_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table ApplianceGrainDryer
   add constraint FK_AppGrD_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table ApplianceHeatPump
   add constraint FK_AppHtP_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst3 foreign key (PumpSizeID)
      references YukonListEntry
/


alter table ApplianceIrrigation
   add constraint FK_AppIrr_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table ApplianceIrrigation
   add constraint FK_AppIrr_YkLst4 foreign key (MeterVoltageID)
      references YukonListEntry
/


alter table ApplianceStorageHeat
   add constraint FK_AppStHt_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table ApplianceStorageHeat
   add constraint FK_AppStHt_YkLst foreign key (StorageTypeID)
      references YukonListEntry
/


alter table ApplianceWaterHeater
   add constraint FK_AppWtHt_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


alter table LMProgramWebPublishing
   add constraint FK_CsLEn_LPWbP foreign key (ChanceOfControlID)
      references YukonListEntry
/


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE foreign key (EventTypeID)
      references YukonListEntry
/


alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac_ty foreign key (TypeID)
      references YukonListEntry
/


alter table WorkOrderBase
   add constraint FK_CsLsE_WkB_c foreign key (CurrentStateID)
      references YukonListEntry
/


alter table LMThermostatSeasonEntry
   add constraint FK_CsLsE_LThSE foreign key (TimeOfWeekID)
      references YukonListEntry
/


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE_a foreign key (ActionID)
      references YukonListEntry
/


alter table LMThermostatManualEvent
   add constraint FK_CsLsE_LThMnO2 foreign key (OperationStateID)
      references YukonListEntry
/


alter table WorkOrderBase
   add constraint FK_CsLsE_WkB foreign key (WorkTypeID)
      references YukonListEntry
/


alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac foreign key (TonnageID)
      references YukonListEntry
/


alter table LMThermostatManualEvent
   add constraint FK_CsLsE_LThMnO1 foreign key (FanOperationID)
      references YukonListEntry
/


alter table CustomerFAQ
   add constraint FK_CsLsEn_CsF foreign key (SubjectID)
      references YukonListEntry
/


alter table ApplianceBase
   add constraint FK_CsLsEn_ApB foreign key (ManufacturerID)
      references YukonListEntry
/


alter table ApplianceBase
   add constraint FK_CsLsEn_ApB2 foreign key (LocationID)
      references YukonListEntry
/


alter table CallReportBase
   add constraint FK_CstAc_ClRpB foreign key (AccountID)
      references CustomerAccount (AccountID)
/


alter table LMProgramEvent
   add constraint FK_CstAc_LMPrEv foreign key (AccountID)
      references CustomerAccount (AccountID)
/


alter table ServiceCompany
   add constraint FK_CstAdd_SrC foreign key (AddressID)
      references
/


alter table CustomerAccount
   add constraint FK_CstBs_CstAcc foreign key (CustomerID)
      references
/


alter table ServiceCompany
   add constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
      references
/


alter table CallReportBase
   add constraint FK_CstELs_ClRB foreign key (CallTypeID)
      references YukonListEntry
/


alter table ApplianceCategory
   add constraint FK_CstLs_ApCt foreign key (CategoryID)
      references YukonListEntry
/


alter table InventoryBase
   add constraint FK_INV_REF__YUK foreign key (CategoryID)
      references YukonListEntry
/


alter table LMHardwareBase
   add constraint FK_LMH_REF__YUK foreign key (LMHardwareTypeID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_AccSt foreign key (AccountSiteID)
      references AccountSite (AccountSiteID)
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst1 foreign key (ConstructionMaterialID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst10 foreign key (OwnershipTypeID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst11 foreign key (ResidenceTypeID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst2 foreign key (DecadeBuiltID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst3 foreign key (GeneralConditionID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst4 foreign key (InsulationDepthID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CUS_REF__YUK foreign key (NumberOfOccupantsID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst6 foreign key (MainHeatingSystemID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst7 foreign key (SquareFeetID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst8 foreign key (MainCoolingSystemID)
      references YukonListEntry
/


alter table CustomerResidence
   add constraint FK_CstRes_YkLst9 foreign key (MainFuelTypeID)
      references YukonListEntry
/


alter table InventoryBase
   add constraint FK_Dev_InvB foreign key (DeviceID)
      references
/


alter table ECToAccountMapping
   add constraint FK_ECTAcc_CstAcc foreign key (AccountID)
      references CustomerAccount (AccountID)
/


alter table ECToAccountMapping
   add constraint FK_ECTAcc_Enc foreign key (EnergyCompanyID)
      references
/


alter table ECToGenericMapping
   add constraint FK_ECTGn_Enc foreign key (EnergyCompanyID)
      references
/


alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc foreign key (EnergyCompanyID)
      references
/


alter table ECToInventoryMapping
   add constraint FK_ECTInv_Enc2 foreign key (InventoryID)
      references InventoryBase (InventoryID)
/


alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Call foreign key (CallReportID)
      references CallReportBase (CallID)
/


alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Enc foreign key (EnergyCompanyID)
      references
/


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
      references
/


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
      references WorkOrderBase (OrderID)
/


alter table ECToLMCustomerEventMapping
   add constraint FK_EnCm_ECLmCs foreign key (EnergyCompanyID)
      references
/


alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn foreign key (QuestionType)
      references YukonListEntry
/


alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn2 foreign key (AnswerType)
      references YukonListEntry
/


alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn3 foreign key (ExpectedAnswer)
      references YukonListEntry
/


alter table LMThermostatSeason
   add constraint FK_InvB_LThSs foreign key (InventoryID)
      references InventoryBase (InventoryID)
/


alter table LMHardwareEvent
   add constraint FK_IvB_LMHrEv foreign key (InventoryID)
      references InventoryBase (InventoryID)
/


alter table ECToLMCustomerEventMapping
   add constraint FK_LCsEv_ECLmCs foreign key (EventID)
      references LMCustomerEventBase (EventID)
/


alter table LMHardwareConfiguration
   add constraint FK_LMHrd_LMGr foreign key (AddressingGroupID)
      references
/


alter table LMProgramEvent
   add constraint FK_LMPrg_LMPrEv foreign key (LMProgramID)
      references
/


alter table LMProgramWebPublishing
   add constraint FK_LMprApp_App foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
/


alter table LMProgramWebPublishing
   add constraint FK_LMprApp_LMPrg foreign key (LMProgramID)
      references
/


alter table LMThermostatSeasonEntry
   add constraint FK_LThSe_LThSEn foreign key (SeasonID)
      references LMThermostatSeason (SeasonID)
/


alter table LMProgramEvent
   add constraint FK_LmCsEv_LmPrEv foreign key (EventID)
      references LMCustomerEventBase (EventID)
/


alter table LMHardwareEvent
   add constraint FK_LmHrEv_LmCsEv foreign key (EventID)
      references LMCustomerEventBase (EventID)
/


alter table LMThermostatManualEvent
   add constraint FK_LmThrS_LmCstEv foreign key (EventID)
      references LMCustomerEventBase (EventID)
/


alter table Substation
   add constraint FK_Sub_Rt foreign key (RouteID)
      references
/


alter table SiteInformation
   add constraint FK_Sub_Si foreign key (SubstationID)
      references Substation (SubstationID)
/


alter table ApplianceCategory
   add constraint FK_YkWC_ApCt foreign key (WebConfigurationID)
      references
/


alter table LMProgramWebPublishing
   add constraint FK_YkWC_LMPrWPb foreign key (WebsettingsID)
      references
/


alter table LMThermostatSeason
   add constraint FK_YkWbC_LThSs foreign key (WebConfigurationID)
      references
/


alter table LMHardwareBase
   add constraint FK_LMH_ISA__INV foreign key (InventoryID)
      references InventoryBase (InventoryID)
/


alter table ApplianceAirConditioner
   add constraint FK_APP_ISA__APP foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
/


