/*==============================================================*/
/* Database name:  STARS                                        */
/* DBMS name:      CTI Oracle 8.1.5                             */
/* Created on:     3/18/2003 1:54:20 PM                         */
/*==============================================================*/


drop table ECToAccountMapping cascade constraints
/


drop table LMThermostatSeasonEntry cascade constraints
/


drop table LMHardwareConfiguration cascade constraints
/


drop table LMHardwareEvent cascade constraints
/


drop table ApplianceGenerator cascade constraints
/


drop table ApplianceHeatPump cascade constraints
/


drop table ECToInventoryMapping cascade constraints
/


drop table LMThermostatSeason cascade constraints
/


drop table ApplianceGrainDryer cascade constraints
/


drop table ECToCallReportMapping cascade constraints
/


drop table ApplianceStorageHeat cascade constraints
/


drop table LMThermostatManualEvent cascade constraints
/


drop table ApplianceIrrigation cascade constraints
/


drop table ApplianceDualFuel cascade constraints
/


drop table LMHardwareBase cascade constraints
/


drop table CallReportBase cascade constraints
/


drop table LMProgramEvent cascade constraints
/


drop table ApplianceAirConditioner cascade constraints
/


drop table ApplianceWaterHeater cascade constraints
/


drop table ApplianceBase cascade constraints
/


drop table InventoryBase cascade constraints
/


drop table CustomerAccount cascade constraints
/


drop table LMProgramWebPublishing cascade constraints
/


drop table AccountSite cascade constraints
/


drop table ECToWorkOrderMapping cascade constraints
/


drop table ECToLMCustomerEventMapping cascade constraints
/


drop table LMCustomerEventBase cascade constraints
/


drop table WorkOrderBase cascade constraints
/


drop table CustomerResidence cascade constraints
/


drop table CustomerFAQ cascade constraints
/


drop table ECToGenericMapping cascade constraints
/


drop table SiteInformation cascade constraints
/


drop table ServiceCompany cascade constraints
/


drop table InterviewQuestion cascade constraints
/


drop table ApplianceCategory cascade constraints
/


drop table Substation cascade constraints
/


/*==============================================================*/
/* Table : Substation                                           */
/*==============================================================*/


create table Substation  (
   SubstationID         NUMBER                           not null,
   SubstationName       VARCHAR2(50),
   RouteID              NUMBER,
   constraint PK_SUBSTATION primary key (SubstationID),
   constraint FK_Sub_Rt foreign key (RouteID)
         references
)
/


/*==============================================================*/
/* Table : ApplianceCategory                                    */
/*==============================================================*/


create table ApplianceCategory  (
   ApplianceCategoryID  NUMBER                           not null,
   Description          VARCHAR2(40),
   CategoryID           NUMBER,
   WebConfigurationID   NUMBER,
   constraint PK_APPLIANCECATEGORY primary key (ApplianceCategoryID),
   constraint FK_CstLs_ApCt foreign key (CategoryID)
         references YukonListEntry,
   constraint FK_YkWC_ApCt foreign key (WebConfigurationID)
         references
)
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
   ExpectedAnswer       NUMBER,
   constraint PK_INTERVIEWQUESTION primary key (QuestionID),
   constraint FK_IntQ_CsLsEn3 foreign key (ExpectedAnswer)
         references YukonListEntry,
   constraint FK_IntQ_CsLsEn foreign key (QuestionType)
         references YukonListEntry,
   constraint FK_IntQ_CsLsEn2 foreign key (AnswerType)
         references YukonListEntry
)
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
   HIType               VARCHAR2(40),
   constraint PK_SERVICECOMPANY primary key (CompanyID),
   constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
         references,
   constraint FK_CstAdd_SrC foreign key (AddressID)
         references
)
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
   SubstationID         NUMBER,
   constraint PK_SITEINFORMATION primary key (SiteID),
   constraint FK_Sub_Si foreign key (SubstationID)
         references Substation (SubstationID)
)
/


/*==============================================================*/
/* Table : ECToGenericMapping                                   */
/*==============================================================*/


create table ECToGenericMapping  (
   EnergyCompanyID      NUMBER                           not null,
   ItemID               NUMBER                           not null,
   MappingCategory      VARCHAR2(40)                     not null,
   constraint PK_ECTOGENERICMAPPING primary key (EnergyCompanyID, ItemID, MappingCategory),
   constraint FK_ECTGn_Enc foreign key (EnergyCompanyID)
         references
)
/


/*==============================================================*/
/* Table : CustomerFAQ                                          */
/*==============================================================*/


create table CustomerFAQ  (
   QuestionID           NUMBER                           not null,
   SubjectID            NUMBER,
   Question             VARCHAR2(200),
   Answer               VARCHAR2(500),
   constraint PK_CUSTOMERFAQ primary key (QuestionID),
   constraint FK_CsLsEn_CsF foreign key (SubjectID)
         references YukonListEntry
)
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
   Notes                VARCHAR2(200),
   constraint PK_CUSTOMERRESIDENCE primary key (AccountSiteID),
   constraint FK_CstRes_YkLst1 foreign key (ConstructionMaterialID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst2 foreign key (DecadeBuiltID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst4 foreign key (InsulationDepthID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst9 foreign key (MainFuelTypeID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst11 foreign key (ResidenceTypeID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst6 foreign key (MainHeatingSystemID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst8 foreign key (MainCoolingSystemID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst10 foreign key (OwnershipTypeID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst7 foreign key (SquareFeetID)
         references YukonListEntry,
   constraint FK_CstRes_YkLst3 foreign key (GeneralConditionID)
         references YukonListEntry,
   constraint FK_CUS_REF__YUK foreign key (NumberOfOccupantsID)
         references YukonListEntry
)
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
   AccountID            NUMBER,
   constraint PK_WORKORDERBASE primary key (OrderID),
   constraint FK_CsLsE_WkB_c foreign key (CurrentStateID)
         references YukonListEntry,
   constraint FK_CsLsE_WkB foreign key (WorkTypeID)
         references YukonListEntry
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
   AuthorizedBy         VARCHAR2(40),
   constraint PK_LMCUSTOMEREVENTBASE primary key (EventID),
   constraint FK_CsLsE_LCstE_a foreign key (ActionID)
         references YukonListEntry,
   constraint FK_CsLsE_LCstE foreign key (EventTypeID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : ECToLMCustomerEventMapping                           */
/*==============================================================*/


create table ECToLMCustomerEventMapping  (
   EnergyCompanyID      NUMBER                           not null,
   EventID              NUMBER                           not null,
   constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key (EnergyCompanyID, EventID),
   constraint FK_LCsEv_ECLmCs foreign key (EventID)
         references LMCustomerEventBase (EventID),
   constraint FK_EnCm_ECLmCs foreign key (EnergyCompanyID)
         references
)
/


/*==============================================================*/
/* Table : ECToWorkOrderMapping                                 */
/*==============================================================*/


create table ECToWorkOrderMapping  (
   EnergyCompanyID      NUMBER                           not null,
   WorkOrderID          NUMBER                           not null,
   constraint PK_ECTOWORKORDERMAPPING primary key (EnergyCompanyID, WorkOrderID),
   constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
         references WorkOrderBase (OrderID),
   constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
         references
)
/


/*==============================================================*/
/* Table : AccountSite                                          */
/*==============================================================*/


create table AccountSite  (
   AccountSiteID        NUMBER                           not null,
   SiteInformationID    NUMBER,
   SiteNumber           VARCHAR2(40)                     not null,
   StreetAddressID      NUMBER,
   PropertyNotes        VARCHAR2(200),
   constraint PK_ACCOUNTSITE primary key (AccountSiteID),
   constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
         references SiteInformation (SiteID),
   constraint FK_AccS_CstAd foreign key (StreetAddressID)
         references
)
/


/*==============================================================*/
/* Index: CstSrvCstProp_FK                                      */
/*==============================================================*/
create index CstSrvCstProp_FK on AccountSite (
   SiteInformationID ASC
)
/


/*==============================================================*/
/* Table : LMProgramWebPublishing                               */
/*==============================================================*/


create table LMProgramWebPublishing  (
   ApplianceCategoryID  NUMBER                           not null,
   LMProgramID          NUMBER                           not null,
   WebsettingsID        NUMBER,
   ChanceOfControlID    NUMBER,
   constraint PK_LMPROGRAMWEBPUBLISHING primary key (ApplianceCategoryID, LMProgramID),
   constraint FK_LMprApp_App foreign key (ApplianceCategoryID)
         references ApplianceCategory (ApplianceCategoryID),
   constraint FK_LMprApp_LMPrg foreign key (LMProgramID)
         references,
   constraint FK_YkWC_LMPrWPb foreign key (WebsettingsID)
         references,
   constraint FK_CsLEn_LPWbP foreign key (ChanceOfControlID)
         references YukonListEntry
)
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
   AccountNotes         VARCHAR2(200),
   LoginID              NUMBER                           not null,
   constraint PK_CUSTOMERACCOUNT primary key (AccountID),
   constraint FK_CUS_CSTA_CUS2 foreign key (AccountSiteID)
         references AccountSite (AccountSiteID),
   constraint FK_CstBs_CstAcc foreign key (CustomerID)
         references,
   constraint FK_YkUs_CstAcc foreign key (LoginID)
         references
)
/


/*==============================================================*/
/* Index: CstAccCstPro_FK                                       */
/*==============================================================*/
create index CstAccCstPro_FK on CustomerAccount (
   AccountSiteID ASC
)
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
   Notes                VARCHAR2(100),
   DEVICEID             NUMBER,
   constraint PK_INVENTORYBASE primary key (InventoryID),
   constraint FK_CUS_CSTA_CUS3 foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_CUS_HRDI_HAR2 foreign key (InstallationCompanyID)
         references ServiceCompany (CompanyID),
   constraint FK_Dev_InvB foreign key (DEVICEID)
         references,
   constraint FK_INV_REF__YUK foreign key (CategoryID)
         references YukonListEntry
)
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
   Notes                VARCHAR2(100),
   ModelNumber          VARCHAR2(40)                     not null,
   constraint PK_APPLIANCEBASE primary key (ApplianceID),
   constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_APP_CSTL_APP foreign key (ApplianceCategoryID)
         references ApplianceCategory (ApplianceCategoryID),
   constraint FK_AppBs_LMPr foreign key (LMProgramID)
         references,
   constraint FK_CsLsEn_ApB foreign key (ManufacturerID)
         references YukonListEntry,
   constraint FK_CsLsEn_ApB2 foreign key (LocationID)
         references YukonListEntry
)
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
/* Table : ApplianceWaterHeater                                 */
/*==============================================================*/


create table ApplianceWaterHeater  (
   ApplianceID          NUMBER                           not null,
   NumberOfGallons      NUMBER                           not null,
   EnergySourceID       NUMBER                           not null,
   NumberOfElements     NUMBER                           not null,
   constraint PK_APPLIANCEWATERHEATER primary key (ApplianceID),
   constraint FK_AppWtHt_AppB foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_AppWtHt_YkLst foreign key (EnergySourceID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : ApplianceAirConditioner                              */
/*==============================================================*/


create table ApplianceAirConditioner  (
   ApplianceID          NUMBER                           not null,
   TonnageID            NUMBER,
   TypeID               NUMBER,
   constraint PK_APPLIANCEAIRCONDITIONER primary key (ApplianceID),
   constraint FK_APP_ISA__APP foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_CsLsE_Ac foreign key (TonnageID)
         references YukonListEntry,
   constraint FK_CsLsE_Ac_ty foreign key (TypeID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : LMProgramEvent                                       */
/*==============================================================*/


create table LMProgramEvent  (
   EventID              NUMBER                           not null,
   AccountID            NUMBER                           not null,
   LMProgramID          NUMBER,
   constraint PK_LMPROGRAMEVENT primary key (EventID),
   constraint FK_CstAc_LMPrEv foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_LMPrg_LMPrEv foreign key (LMProgramID)
         references,
   constraint FK_LmCsEv_LmPrEv foreign key (EventID)
         references LMCustomerEventBase (EventID)
)
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
   AccountID            NUMBER,
   constraint PK_CALLREPORTBASE primary key (CallID),
   constraint FK_CstAc_ClRpB foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_CstELs_ClRB foreign key (CallTypeID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : LMHardwareBase                                       */
/*==============================================================*/


create table LMHardwareBase  (
   InventoryID          NUMBER                           not null,
   ManufacturerSerialNumber VARCHAR2(30),
   LMHardwareTypeID     NUMBER                           not null,
   constraint PK_LMHARDWAREBASE primary key (InventoryID),
   constraint FK_LMH_ISA__INV foreign key (InventoryID)
         references InventoryBase (InventoryID),
   constraint FK_LMH_REF__YUK foreign key (LMHardwareTypeID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : ApplianceDualFuel                                    */
/*==============================================================*/


create table ApplianceDualFuel  (
   ApplianceID          NUMBER                           not null,
   SwitchOverTypeID     NUMBER                           not null,
   SecondaryKWCapacity  NUMBER                           not null,
   SecondaryEnergySourceID NUMBER                           not null,
   constraint PK_APPLIANCEDUALFUEL primary key (ApplianceID),
   constraint FK_AppDlF_AppB foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_AppDuF_YkLst2 foreign key (SwitchOverTypeID)
         references YukonListEntry,
   constraint FK_AppDuF_YkLst1 foreign key (SecondaryEnergySourceID)
         references YukonListEntry
)
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
   MeterVoltageID       NUMBER                           not null,
   constraint PK_APPLIANCEIRRIGATION primary key (ApplianceID),
   constraint FK_AppIrr_AppB foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_AppIrr_YkLst1 foreign key (EnergySourceID)
         references YukonListEntry,
   constraint FK_AppIrr_YkLst6 foreign key (MeterLocationID)
         references YukonListEntry,
   constraint FK_AppIrr_YkLst5 foreign key (SoilTypeID)
         references YukonListEntry,
   constraint FK_AppIrr_YkLst3 foreign key (IrrigationTypeID)
         references YukonListEntry,
   constraint FK_AppIrr_YkLst2 foreign key (HorsePowerID)
         references YukonListEntry,
   constraint FK_AppIrr_YkLst4 foreign key (MeterVoltageID)
         references YukonListEntry
)
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
   FanOperationID       NUMBER,
   constraint PK_LMTHERMOSTATMANUALEVENT primary key (EventID),
   constraint FK_CsLsE_LThMnO2 foreign key (OperationStateID)
         references YukonListEntry,
   constraint FK_CsLsE_LThMnO1 foreign key (FanOperationID)
         references YukonListEntry,
   constraint FK_LmThrS_LmCstEv foreign key (EventID)
         references LMCustomerEventBase (EventID)
)
/


/*==============================================================*/
/* Table : ApplianceStorageHeat                                 */
/*==============================================================*/


create table ApplianceStorageHeat  (
   ApplianceID          NUMBER                           not null,
   StorageTypeID        NUMBER                           not null,
   PeakKWCapacity       NUMBER                           not null,
   HoursToRecharge      NUMBER                           not null,
   constraint PK_APPLIANCESTORAGEHEAT primary key (ApplianceID),
   constraint FK_AppStHt_AppB foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_AppStHt_YkLst foreign key (StorageTypeID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : ECToCallReportMapping                                */
/*==============================================================*/


create table ECToCallReportMapping  (
   EnergyCompanyID      NUMBER                           not null,
   CallReportID         NUMBER                           not null,
   constraint PK_ECTOCALLREPORTMAPPING primary key (EnergyCompanyID, CallReportID),
   constraint FK_ECTSrv_Enc foreign key (EnergyCompanyID)
         references,
   constraint FK_ECTSrv_Call foreign key (CallReportID)
         references CallReportBase (CallID)
)
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
   BlowerHeatSourceID   NUMBER                           not null,
   constraint PK_APPLIANCEGRAINDRYER primary key (ApplianceID),
   constraint FK_AppGrD_AppB foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_AppGrDr_YkLst5 foreign key (BlowerHorsePowerID)
         references YukonListEntry,
   constraint FK_AppGrDr_YkLst1 foreign key (BinSizeID)
         references YukonListEntry,
   constraint FK_AppGrDr_YkLst2 foreign key (BlowerEnergySourceID)
         references YukonListEntry,
   constraint FK_AppGrDr_YkLst6 foreign key (BlowerHeatSourceID)
         references YukonListEntry,
   constraint FK_AppGrDr_YkLst3 foreign key (DryerTypeID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : LMThermostatSeason                                   */
/*==============================================================*/


create table LMThermostatSeason  (
   SeasonID             NUMBER                           not null,
   InventoryID          NUMBER,
   WebConfigurationID   NUMBER,
   StartDate            DATE,
   DisplayOrder         NUMBER,
   constraint PK_LMTHERMOSTATSEASON primary key (SeasonID),
   constraint FK_YkWbC_LThSs foreign key (WebConfigurationID)
         references,
   constraint FK_InvB_LThSs foreign key (InventoryID)
         references InventoryBase (InventoryID)
)
/


/*==============================================================*/
/* Table : ECToInventoryMapping                                 */
/*==============================================================*/


create table ECToInventoryMapping  (
   EnergyCompanyID      NUMBER                           not null,
   InventoryID          NUMBER                           not null,
   constraint PK_ECTOINVENTORYMAPPING primary key (EnergyCompanyID, InventoryID),
   constraint FK_ECTInv_Enc foreign key (EnergyCompanyID)
         references,
   constraint FK_ECTInv_Enc2 foreign key (InventoryID)
         references InventoryBase (InventoryID)
)
/


/*==============================================================*/
/* Table : ApplianceHeatPump                                    */
/*==============================================================*/


create table ApplianceHeatPump  (
   ApplianceID          NUMBER                           not null,
   PumpTypeID           NUMBER                           not null,
   StandbySourceID      NUMBER                           not null,
   SecondsDelayToRestart NUMBER                           not null,
   constraint PK_APPLIANCEHEATPUMP primary key (ApplianceID),
   constraint FK_AppHtP_AppB foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_AppHtPm_YkLst2 foreign key (StandbySourceID)
         references YukonListEntry,
   constraint FK_AppHtPm_YkLst1 foreign key (PumpTypeID)
         references YukonListEntry
)
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
   StartDelaySeconds    NUMBER                           not null,
   constraint PK_APPLIANCEGENERATOR primary key (ApplianceID),
   constraint FK_AppGen_AppB foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_AppGn_YkLst2 foreign key (TransferSwitchTypeID)
         references YukonListEntry,
   constraint FK_AppGn_YkLst1 foreign key (TransferSwitchMfgID)
         references YukonListEntry
)
/


/*==============================================================*/
/* Table : LMHardwareEvent                                      */
/*==============================================================*/


create table LMHardwareEvent  (
   EventID              NUMBER                           not null,
   InventoryID          NUMBER                           not null,
   constraint PK_LMHARDWAREEVENT primary key (EventID),
   constraint FK_IvB_LMHrEv foreign key (InventoryID)
         references InventoryBase (InventoryID),
   constraint FK_LmHrEv_LmCsEv foreign key (EventID)
         references LMCustomerEventBase (EventID)
)
/


/*==============================================================*/
/* Table : LMHardwareConfiguration                              */
/*==============================================================*/


create table LMHardwareConfiguration  (
   InventoryID          NUMBER                           not null,
   ApplianceID          NUMBER                           not null,
   AddressingGroupID    NUMBER,
   constraint PK_LMHARDWARECONFIGURATION primary key (InventoryID, ApplianceID),
   constraint FK_LMH_LMHR_LMH foreign key (InventoryID)
         references LMHardwareBase (InventoryID),
   constraint FK_LMH_CSTL_CUS2 foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_LMHrd_LMGr foreign key (AddressingGroupID)
         references
)
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
/* Table : LMThermostatSeasonEntry                              */
/*==============================================================*/


create table LMThermostatSeasonEntry  (
   EntryID              NUMBER                           not null,
   SeasonID             NUMBER                           not null,
   TimeOfWeekID         NUMBER                           not null,
   StartTime            NUMBER                           not null,
   Temperature          NUMBER,
   constraint PK_LMTHERMOSTATSEASONENTRY primary key (EntryID),
   constraint FK_CsLsE_LThSE foreign key (TimeOfWeekID)
         references YukonListEntry,
   constraint FK_LThSe_LThSEn foreign key (SeasonID)
         references LMThermostatSeason (SeasonID)
)
/


/*==============================================================*/
/* Table : ECToAccountMapping                                   */
/*==============================================================*/


create table ECToAccountMapping  (
   EnergyCompanyID      NUMBER                           not null,
   AccountID            NUMBER                           not null,
   constraint PK_ECTOACCOUNTMAPPING primary key (EnergyCompanyID, AccountID),
   constraint FK_ECTAcc_Enc foreign key (EnergyCompanyID)
         references,
   constraint FK_ECTAcc_CstAcc foreign key (AccountID)
         references CustomerAccount (AccountID)
)
/


