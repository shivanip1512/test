/*==============================================================*/
/* Database name:  STARS                                        */
/* DBMS name:      CTI Oracle 8.1.5                             */
/* Created on:     1/7/2003 5:07:15 PM                          */
/*==============================================================*/


drop table LMThermostatSeasonEntry cascade constraints
/


drop table LMThermostatSeason cascade constraints
/


drop table LMHardwareEvent cascade constraints
/


drop table LMHardwareConfiguration cascade constraints
/


drop table LMHardwareBase cascade constraints
/


drop table ECToInventoryMapping cascade constraints
/


drop table InventoryBase cascade constraints
/


drop table ECToCallReportMapping cascade constraints
/


drop table LMProgramEvent cascade constraints
/


drop table ECToAccountMapping cascade constraints
/


drop table CallReportBase cascade constraints
/


drop table ECToWorkOrderMapping cascade constraints
/


drop table ApplianceAirConditioner cascade constraints
/


drop table ApplianceBase cascade constraints
/


drop table CustomerAccount cascade constraints
/


drop table ECToLMCustomerEventMapping cascade constraints
/


drop table LMProgramWebPublishing cascade constraints
/


drop table AccountSite cascade constraints
/


drop table ApplianceCategory cascade constraints
/


drop table LMCustomerEventBase cascade constraints
/


drop table SiteInformation cascade constraints
/


drop table WorkOrderBase cascade constraints
/


drop table CustomerSelectionList cascade constraints
/


drop table InterviewQuestion cascade constraints
/


drop table CustomerFAQ cascade constraints
/


drop table CustomerAdditionalContact cascade constraints
/


drop table LMThermostatManualOption cascade constraints
/


drop table ContactNotification cascade constraints
/


drop table CustomerListEntry cascade constraints
/


drop table CustomerWebConfiguration cascade constraints
/


drop table Substation cascade constraints
/


drop table ECToGenericMapping cascade constraints
/


drop table CustomerBase cascade constraints
/


drop table ServiceCompany cascade constraints
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
         references CustomerContact (ContactID),
   constraint FK_CstAdd_SrC foreign key (AddressID)
         references CustomerAddress (AddressID)
)
/


/*==============================================================*/
/* Table : CustomerBase                                         */
/*==============================================================*/


create table CustomerBase  (
   CustomerID           NUMBER                           not null,
   PrimaryContactID     NUMBER,
   CustomerTypeID       NUMBER                           not null,
   TimeZone             VARCHAR2(30),
   PaoID                NUMBER,
   constraint PK_CUSTOMERBASE primary key (CustomerID),
   constraint FK_CstBs_CstCnt foreign key (PrimaryContactID)
         references CustomerContact (ContactID)
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
         references EnergyCompany (EnergyCompanyID)
)
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
         references Route (RouteID)
)
/


/*==============================================================*/
/* Table : CustomerWebConfiguration                             */
/*==============================================================*/


create table CustomerWebConfiguration  (
   ConfigurationID      NUMBER                           not null,
   LogoLocation         VARCHAR2(100),
   Description          VARCHAR2(500),
   AlternateDisplayName VARCHAR2(50),
   URL                  VARCHAR2(100),
   constraint PK_CUSTOMERWEBCONFIGURATION primary key (ConfigurationID)
)
/


/*==============================================================*/
/* Table : CustomerListEntry                                    */
/*==============================================================*/


create table CustomerListEntry  (
   EntryID              NUMBER                           not null,
   ListID               NUMBER                           not null,
   EntryOrder           NUMBER,
   EntryText            VARCHAR2(50),
   YukonDefinition      VARCHAR2(20),
   constraint PK_CUSTOMERLISTENTRY primary key (EntryID)
)
/


/*==============================================================*/
/* Table : ContactNotification                                  */
/*==============================================================*/


create table ContactNotification  (
   ContactID            NUMBER                           not null,
   NotificationLabelID  NUMBER,
   NotificationCategoryID NUMBER,
   Notification         VARCHAR2(100),
   constraint PK_CONTACTNOTIFICATION primary key (ContactID),
   constraint FK_CntNotCsLs1 foreign key (NotificationCategoryID)
         references CustomerListEntry (EntryID),
   constraint FK_CntNotCsLs2 foreign key (NotificationLabelID)
         references CustomerListEntry (EntryID),
   constraint FK_Cnt_CntNot foreign key (ContactID)
         references CustomerContact (ContactID)
)
/


/*==============================================================*/
/* Table : LMThermostatManualOption                             */
/*==============================================================*/


create table LMThermostatManualOption  (
   InventoryID          NUMBER                           not null,
   PreviousTemperature  NUMBER,
   HoldTemperature      VARCHAR2(1),
   OperationStateID     NUMBER,
   FanOperationID       NUMBER,
   constraint PK_LMTHERMOSTATMANUALOPTION primary key (InventoryID),
   constraint FK_CsLsE_LThMnO2 foreign key (OperationStateID)
         references CustomerListEntry (EntryID),
   constraint FK_CsLsE_LThMnO1 foreign key (FanOperationID)
         references CustomerListEntry (EntryID)
)
/


/*==============================================================*/
/* Table : CustomerAdditionalContact                            */
/*==============================================================*/


create table CustomerAdditionalContact  (
   CustomerID           NUMBER                           not null,
   ContactID            NUMBER                           not null,
   constraint PK_CUSTOMERADDITIONALCONTACT primary key (CustomerID, ContactID),
   constraint FK_CsCnt_CsAdCn foreign key (ContactID)
         references CustomerContact (ContactID)
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
         references CustomerListEntry (EntryID)
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
         references CustomerListEntry (EntryID),
   constraint FK_IntQ_CsLsEn foreign key (QuestionType)
         references CustomerListEntry (EntryID),
   constraint FK_IntQ_CsLsEn2 foreign key (AnswerType)
         references CustomerListEntry (EntryID)
)
/


/*==============================================================*/
/* Table : CustomerSelectionList                                */
/*==============================================================*/


create table CustomerSelectionList  (
   ListID               NUMBER                           not null,
   Ordering             VARCHAR2(1),
   SelectionLabel       VARCHAR2(30),
   WhereIsList          VARCHAR2(100),
   ListName             VARCHAR2(40),
   UserUpdateAvailable  VARCHAR2(1),
   constraint PK_CUSTOMERSELECTIONLIST primary key (ListID)
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
         references CustomerListEntry (EntryID),
   constraint FK_CsLsE_WkB foreign key (WorkTypeID)
         references CustomerListEntry (EntryID)
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
         references CustomerListEntry (EntryID),
   constraint FK_CsLsE_LCstE foreign key (EventTypeID)
         references CustomerListEntry (EntryID)
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
         references CustomerListEntry (EntryID),
   constraint FK_CsWC_ApCt foreign key (WebConfigurationID)
         references CustomerWebConfiguration (ConfigurationID)
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
         references CustomerAddress (AddressID)
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
         references LMPROGRAM (DEVICEID),
   constraint FK_LMPrWPb_CsWC foreign key (WebsettingsID)
         references CustomerWebConfiguration (ConfigurationID),
   constraint FK_CsLEn_LPWbP foreign key (ChanceOfControlID)
         references CustomerListEntry (EntryID)
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
         references EnergyCompany (EnergyCompanyID)
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
   constraint PK_CUSTOMERACCOUNT primary key (AccountID),
   constraint FK_CUS_CSTA_CUS2 foreign key (AccountSiteID)
         references AccountSite (AccountSiteID),
   constraint FK_CstBs_CstAcc foreign key (CustomerID)
         references CustomerBase (CustomerID)
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
   constraint PK_APPLIANCEBASE primary key (ApplianceID),
   constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_APP_CSTL_APP foreign key (ApplianceCategoryID)
         references ApplianceCategory (ApplianceCategoryID),
   constraint FK_AppBs_LMPr foreign key (LMProgramID)
         references LMPROGRAM (DEVICEID),
   constraint FK_CsLsEn_ApB foreign key (ManufacturerID)
         references CustomerListEntry (EntryID),
   constraint FK_CsLsEn_ApB2 foreign key (LocationID)
         references CustomerListEntry (EntryID)
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
         references CustomerListEntry (EntryID),
   constraint FK_CsLsE_Ac_ty foreign key (TypeID)
         references CustomerListEntry (EntryID)
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
         references EnergyCompany (EnergyCompanyID)
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
   constraint FK_CstB_CllR foreign key ()
         references CustomerBase (CustomerID),
   constraint FK_CstAc_ClRpB foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_CstELs_ClRB foreign key (CallTypeID)
         references CustomerListEntry (EntryID)
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
         references EnergyCompany (EnergyCompanyID),
   constraint FK_ECTAcc_CstAcc foreign key (AccountID)
         references CustomerAccount (AccountID)
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
         references LMPROGRAM (DEVICEID)
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
         references EnergyCompany (EnergyCompanyID),
   constraint FK_ECTSrv_Call foreign key (CallReportID)
         references CallReportBase (CallID)
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
         references DEVICE (DEVICEID),
   constraint FK_INV_REF__CUS foreign key (CategoryID)
         references CustomerListEntry (EntryID)
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
/* Table : ECToInventoryMapping                                 */
/*==============================================================*/


create table ECToInventoryMapping  (
   EnergyCompanyID      NUMBER                           not null,
   InventoryID          NUMBER                           not null,
   constraint PK_ECTOINVENTORYMAPPING primary key (EnergyCompanyID, InventoryID),
   constraint FK_ECTInv_Enc foreign key (EnergyCompanyID)
         references EnergyCompany (EnergyCompanyID),
   constraint FK_ECTInv_Enc2 foreign key (InventoryID)
         references InventoryBase (InventoryID)
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
   constraint FK_LMH_REF__CUS foreign key (LMHardwareTypeID)
         references CustomerListEntry (EntryID)
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
         references LMGroup (DeviceID)
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
/* Table : LMHardwareEvent                                      */
/*==============================================================*/


create table LMHardwareEvent  (
   EventID              NUMBER                           not null,
   InventoryID          NUMBER                           not null,
   constraint PK_LMHARDWAREEVENT primary key (EventID),
   constraint FK_IvB_LMHrEv foreign key (InventoryID)
         references InventoryBase (InventoryID)
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
   constraint FK_CsWbC_LThSs foreign key (WebConfigurationID)
         references CustomerWebConfiguration (ConfigurationID),
   constraint FK_InvB_LThSs foreign key (InventoryID)
         references InventoryBase (InventoryID)
)
/


/*==============================================================*/
/* Table : LMThermostatSeasonEntry                              */
/*==============================================================*/


create table LMThermostatSeasonEntry  (
   SeasonID             NUMBER                           not null,
   TimeOfWeekID         NUMBER,
   StartTime            NUMBER,
   Temperature          NUMBER,
   constraint PK_LMTHERMOSTATSEASONENTRY primary key (SeasonID),
   constraint FK_CsLsE_LThSE foreign key (TimeOfWeekID)
         references CustomerListEntry (EntryID),
   constraint FK_LThSe_LThSEn foreign key (SeasonID)
         references LMThermostatSeason (SeasonID)
)
/


