/*==============================================================*/
/* Database name:  STARS                                        */
/* DBMS name:      CTI Oracle 8.1.5                             */
/* Created on:     11/13/2002 4:40:49 PM                        */
/*==============================================================*/


drop table AirConditioner cascade constraints
/


drop table ECToInventoryMapping cascade constraints
/


drop table LMHardwareConfiguration cascade constraints
/


drop table ECToCallReportMapping cascade constraints
/


drop table LMHardwareBase cascade constraints
/


drop table LMProgramWebPublishing cascade constraints
/


drop table ECToLMCustomerEventMapping cascade constraints
/


drop table ApplianceBase cascade constraints
/


drop table CallReportBase cascade constraints
/


drop table LMHardwareEvent cascade constraints
/


drop table LMProgramEvent cascade constraints
/


drop table ECToWorkOrderMapping cascade constraints
/


drop table InventoryBase cascade constraints
/


drop table ECToAccountMapping cascade constraints
/


drop table CustomerAccount cascade constraints
/


drop table LMCustomerEventBase cascade constraints
/


drop table WorkOrderBase cascade constraints
/


drop table AccountSite cascade constraints
/


drop table ApplianceCategory cascade constraints
/


drop table CustomerSelectionList cascade constraints
/


drop table CustomerListEntry cascade constraints
/


drop table CustomerWebConfiguration cascade constraints
/


drop table ECToGenericMapping cascade constraints
/


drop table SiteInformation cascade constraints
/


drop table CstBaseCstContactMap cascade constraints
/


drop table Substation cascade constraints
/


drop table ServiceCompany cascade constraints
/


drop table CustomerBase cascade constraints
/


/*==============================================================*/
/* Table : CustomerBase                                         */
/*==============================================================*/


create table CustomerBase  (
   CustomerID           NUMBER                           not null,
   ContactID            NUMBER,
   CustomerTypeID       NUMBER                           not null,
   TimeZone             VARCHAR2(30),
   PAObjectID           NUMBER,
   constraint PK_CUSTOMERBASE primary key (CustomerID),
   constraint FK_CstBs_CstCnt foreign key (ContactID)
         references CustomerContact (ContactID)
)
/


/*==============================================================*/
/* Table : ServiceCompany                                       */
/*==============================================================*/


create table ServiceCompany  (
   CompanyID            NUMBER                           not null,
   CompanyName          VARCHAR2(40),
   ContactID            NUMBER,
   AddressID            NUMBER,
   MainPhoneNumber      VARCHAR2(14),
   MainFaxNumber        VARCHAR2(14),
   HIType               VARCHAR2(40),
   constraint PK_SERVICECOMPANY primary key (CompanyID),
   constraint FK_CstCnt_SrvC foreign key (ContactID)
         references CustomerContact (ContactID),
   constraint FK_CstAdd_SrC foreign key (AddressID)
         references CustomerAddress (AddressID)
)
/


/*==============================================================*/
/* Table : Substation                                           */
/*==============================================================*/


create table Substation  (
   SubstationID         NUMBER                           not null,
   SubstationName       VARCHAR2(50),
   RouteID              NUMBER,
   constraint PK_SUBSTATION primary key (SubstationID)
)
/


/*==============================================================*/
/* Table : CstBaseCstContactMap                                 */
/*==============================================================*/


create table CstBaseCstContactMap  (
   CustomerID           NUMBER                           not null,
   CustomerContactID    NUMBER                           not null,
   ContactID            NUMBER,
   constraint PK_CSTBASECSTCONTACTMAP primary key (CustomerID, CustomerContactID),
   constraint FK_CstBs_CstMp foreign key (CustomerID)
         references CustomerBase (CustomerID),
   constraint FK_CstCnt_Csmap foreign key (ContactID)
         references CustomerContact (ContactID)
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
         references EnergyCompany (EnergyCompanyID)
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
/* Table : ApplianceCategory                                    */
/*==============================================================*/


create table ApplianceCategory  (
   ApplianceCategoryID  NUMBER                           not null,
   CategoryID           NUMBER,
   WebConfigurationID   NUMBER,
   Description          VARCHAR2(40),
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
   AddressID            NUMBER,
   SiteNumber           VARCHAR2(40)                     not null,
   PropertyNotes        VARCHAR2(200),
   constraint PK_ACCOUNTSITE primary key (AccountSiteID),
   constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
         references SiteInformation (SiteID),
   constraint FK_AccS_CstAd foreign key (AddressID)
         references CustomerAddress (AddressID)
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
   CustomerID           NUMBER                           not null,
   SiteID               NUMBER                           not null,
   ServiceCompanyID     NUMBER,
   DateReported         DATE,
   Description          VARCHAR2(200),
   DateScheduled        DATE,
   DateCompleted        DATE,
   ActionTaken          VARCHAR2(200),
   OrderedBy            VARCHAR2(30),
   constraint PK_WORKORDERBASE primary key (OrderID),
   constraint FK_CstB_WkB foreign key (CustomerID)
         references CustomerBase (CustomerID),
   constraint FK_CsLsE_WkB_c foreign key (CurrentStateID)
         references CustomerListEntry (EntryID),
   constraint FK_CsLsE_WkB foreign key (WorkTypeID)
         references CustomerListEntry (EntryID),
   constraint FK_AccS_WkB foreign key (SiteID)
         references AccountSite (AccountSiteID)
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
/* Table : InventoryBase                                        */
/*==============================================================*/


create table InventoryBase  (
   InventoryID          NUMBER                           not null,
   AccountID            NUMBER,
   InstallationCompanyID NUMBER,
   CategoryID           NUMBER                           not null,
   DEVICEID             NUMBER,
   ReceiveDate          DATE,
   InstallDate          DATE,
   RemoveDate           DATE,
   AlternateTrackingNumber VARCHAR2(40),
   VoltageID            NUMBER,
   Notes                VARCHAR2(100),
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
/* Table : LMProgramEvent                                       */
/*==============================================================*/


create table LMProgramEvent  (
   EventID              NUMBER                           not null,
   AccountID            NUMBER                           not null,
   DEVICEID             NUMBER,
   constraint PK_LMPROGRAMEVENT primary key (EventID),
   constraint FK_CstAc_LMPrEv foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_LMPrg_LMPrEv foreign key (DEVICEID)
         references LMPROGRAM (DEVICEID)
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
/* Table : CallReportBase                                       */
/*==============================================================*/


create table CallReportBase  (
   CallID               NUMBER                           not null,
   CallNumber           VARCHAR2(20),
   CallTypeID           NUMBER,
   DateTaken            DATE,
   Description          VARCHAR2(300),
   AccountID            NUMBER,
   CustomerID           NUMBER,
   TakenBy              VARCHAR2(30),
   constraint PK_CALLREPORTBASE primary key (CallID),
   constraint FK_CstB_CllR foreign key (CustomerID)
         references CustomerBase (CustomerID),
   constraint FK_CstAc_ClRpB foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_CstELs_ClRB foreign key (CallTypeID)
         references CustomerListEntry (EntryID)
)
/


/*==============================================================*/
/* Table : ApplianceBase                                        */
/*==============================================================*/


create table ApplianceBase  (
   ApplianceID          NUMBER                           not null,
   AccountID            NUMBER                           not null,
   ApplianceCategoryID  NUMBER                           not null,
   DEVICEID             NUMBER,
   Notes                VARCHAR2(100),
   constraint PK_APPLIANCEBASE primary key (ApplianceID),
   constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
         references CustomerAccount (AccountID),
   constraint FK_APP_CSTL_APP foreign key (ApplianceCategoryID)
         references ApplianceCategory (ApplianceCategoryID),
   constraint FK_AppBs_LMPr foreign key (DEVICEID)
         references LMPROGRAM (DEVICEID)
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
/* Table : LMProgramWebPublishing                               */
/*==============================================================*/


create table LMProgramWebPublishing  (
   ApplianceCategoryID  NUMBER                           not null,
   LMProgramID          NUMBER                           not null,
   WebsettingsID        NUMBER,
   DEVICEID             NUMBER,
   constraint PK_LMPROGRAMWEBPUBLISHING primary key (ApplianceCategoryID, LMProgramID),
   constraint FK_LMprApp_App foreign key (ApplianceCategoryID)
         references ApplianceCategory (ApplianceCategoryID),
   constraint FK_LMprApp_LMPrg foreign key (DEVICEID)
         references LMPROGRAM (DEVICEID),
   constraint FK_LMPrWPb_CsWC foreign key (WebsettingsID)
         references CustomerWebConfiguration (ConfigurationID)
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
/* Table : LMHardwareConfiguration                              */
/*==============================================================*/


create table LMHardwareConfiguration  (
   InventoryID          NUMBER                           not null,
   ApplianceID          NUMBER                           not null,
   DeviceID             NUMBER,
   constraint PK_LMHARDWARECONFIGURATION primary key (InventoryID, ApplianceID),
   constraint FK_LMH_LMHR_LMH foreign key (InventoryID)
         references LMHardwareBase (InventoryID),
   constraint FK_LMH_CSTL_CUS2 foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_LMHrd_LMGr foreign key (DeviceID)
         references LMGroup (DeviceID)
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
/* Table : AirConditioner                                       */
/*==============================================================*/


create table AirConditioner  (
   ApplianceID          NUMBER                           not null,
   TonageID             NUMBER,
   TypeID               NUMBER,
   constraint PK_AIRCONDITIONER primary key (ApplianceID),
   constraint FK_AIR_ISA__APP foreign key (ApplianceID)
         references ApplianceBase (ApplianceID),
   constraint FK_CsLsE_Ac foreign key (TonageID)
         references CustomerListEntry (EntryID),
   constraint FK_CsLsE_Ac_ty foreign key (TypeID)
         references CustomerListEntry (EntryID)
)
/


