/*==============================================================*/
/* Database name:  STARS                                        */
/* DBMS name:      CTI SqlServer 2000                           */
/* Created on:     11/26/2002 3:49:51 PM                        */
/*==============================================================*/


if exists (select 1
            from  sysobjects
           where  id = object_id('AccountSite')
            and   type = 'U')
   drop table AccountSite
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceAirConditioner')
            and   type = 'U')
   drop table ApplianceAirConditioner
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceBase')
            and   type = 'U')
   drop table ApplianceBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceCategory')
            and   type = 'U')
   drop table ApplianceCategory
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CallReportBase')
            and   type = 'U')
   drop table CallReportBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ContactNotification')
            and   type = 'U')
   drop table ContactNotification
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAccount')
            and   type = 'U')
   drop table CustomerAccount
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAdditionalContact')
            and   type = 'U')
   drop table CustomerAdditionalContact
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerBase')
            and   type = 'U')
   drop table CustomerBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerFAQ')
            and   type = 'U')
   drop table CustomerFAQ
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerListEntry')
            and   type = 'U')
   drop table CustomerListEntry
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerSelectionList')
            and   type = 'U')
   drop table CustomerSelectionList
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerWebConfiguration')
            and   type = 'U')
   drop table CustomerWebConfiguration
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToAccountMapping')
            and   type = 'U')
   drop table ECToAccountMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToCallReportMapping')
            and   type = 'U')
   drop table ECToCallReportMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToGenericMapping')
            and   type = 'U')
   drop table ECToGenericMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToInventoryMapping')
            and   type = 'U')
   drop table ECToInventoryMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToLMCustomerEventMapping')
            and   type = 'U')
   drop table ECToLMCustomerEventMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ECToWorkOrderMapping')
            and   type = 'U')
   drop table ECToWorkOrderMapping
go


if exists (select 1
            from  sysobjects
           where  id = object_id('InventoryBase')
            and   type = 'U')
   drop table InventoryBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMCustomerEventBase')
            and   type = 'U')
   drop table LMCustomerEventBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMHardwareBase')
            and   type = 'U')
   drop table LMHardwareBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMHardwareConfiguration')
            and   type = 'U')
   drop table LMHardwareConfiguration
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMHardwareEvent')
            and   type = 'U')
   drop table LMHardwareEvent
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramEvent')
            and   type = 'U')
   drop table LMProgramEvent
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMProgramWebPublishing')
            and   type = 'U')
   drop table LMProgramWebPublishing
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMThermostatManualOption')
            and   type = 'U')
   drop table LMThermostatManualOption
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMThermostatSeason')
            and   type = 'U')
   drop table LMThermostatSeason
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMThermostatSeasonEntry')
            and   type = 'U')
   drop table LMThermostatSeasonEntry
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ServiceCompany')
            and   type = 'U')
   drop table ServiceCompany
go


if exists (select 1
            from  sysobjects
           where  id = object_id('SiteInformation')
            and   type = 'U')
   drop table SiteInformation
go


if exists (select 1
            from  sysobjects
           where  id = object_id('Substation')
            and   type = 'U')
   drop table Substation
go


if exists (select 1
            from  sysobjects
           where  id = object_id('WorkOrderBase')
            and   type = 'U')
   drop table WorkOrderBase
go


/*==============================================================*/
/* Table : AccountSite                                          */
/*==============================================================*/
create table AccountSite (
AccountSiteID        numeric              not null,
SiteInformationID    numeric              null,
SiteNumber           varchar(40)          not null,
StreetAddressID      numeric              null,
PropertyNotes        varchar(200)         null,
constraint PK_ACCOUNTSITE primary key  (AccountSiteID)
)
go


/*==============================================================*/
/* Table : ApplianceAirConditioner                              */
/*==============================================================*/
create table ApplianceAirConditioner (
ApplianceID          numeric              not null,
TonnageID            numeric              null,
TypeID               numeric              null,
constraint PK_APPLIANCEAIRCONDITIONER primary key  (ApplianceID)
)
go


/*==============================================================*/
/* Table : ApplianceBase                                        */
/*==============================================================*/
create table ApplianceBase (
ApplianceID          numeric              not null,
AccountID            numeric              not null,
ApplianceCategoryID  numeric              not null,
LMProgramID          numeric              null,
YearManufactured     numeric              null,
ManufacturerID       numeric              null,
LocationID           numeric              null,
KWCapacity           numeric              null,
EfficiencyRating     numeric              null,
Notes                varchar(100)         null,
constraint PK_APPLIANCEBASE primary key  (ApplianceID)
)
go


/*==============================================================*/
/* Table : ApplianceCategory                                    */
/*==============================================================*/
create table ApplianceCategory (
ApplianceCategoryID  numeric              not null,
Description          varchar(40)          null,
CategoryID           numeric              null,
WebConfigurationID   numeric              null,
constraint PK_APPLIANCECATEGORY primary key  (ApplianceCategoryID)
)
go


/*==============================================================*/
/* Table : CallReportBase                                       */
/*==============================================================*/
create table CallReportBase (
CallID               numeric              not null,
CallNumber           varchar(20)          null,
CallTypeID           numeric              null,
DateTaken            datetime             null,
TakenBy              varchar(30)          null,
Description          varchar(300)         null,
AccountID            numeric              null,
constraint PK_CALLREPORTBASE primary key  (CallID)
)
go


/*==============================================================*/
/* Table : ContactNotification                                  */
/*==============================================================*/
create table ContactNotification (
ContactID            numeric              not null,
NotificationLabelID  numeric              null,
NotificationCategoryID numeric              null,
Notification         varchar(100)         null,
constraint PK_CONTACTNOTIFICATION primary key  (ContactID)
)
go


/*==============================================================*/
/* Table : CustomerAccount                                      */
/*==============================================================*/
create table CustomerAccount (
AccountID            numeric              not null,
AccountSiteID        numeric              null,
AccountNumber        varchar(40)          null,
CustomerID           numeric              not null,
BillingAddressID     numeric              null,
AccountNotes         varchar(200)         null,
constraint PK_CUSTOMERACCOUNT primary key  (AccountID)
)
go


/*==============================================================*/
/* Table : CustomerAdditionalContact                            */
/*==============================================================*/
create table CustomerAdditionalContact (
CustomerID           numeric              not null,
ContactID            numeric              not null,
constraint PK_CUSTOMERADDITIONALCONTACT primary key  (CustomerID, ContactID)
)
go


/*==============================================================*/
/* Table : CustomerBase                                         */
/*==============================================================*/
create table CustomerBase (
CustomerID           numeric              not null,
PrimaryContactID     numeric              null,
CustomerTypeID       numeric              not null,
TimeZone             varchar(30)          null,
PaoID                numeric              null,
constraint PK_CUSTOMERBASE primary key  (CustomerID)
)
go


/*==============================================================*/
/* Table : CustomerFAQ                                          */
/*==============================================================*/
create table CustomerFAQ (
QuestionID           numeric              not null,
SubjectID            numeric              null,
Question             varchar(200)         null,
Answer               varchar(500)         null,
constraint PK_CUSTOMERFAQ primary key  (QuestionID)
)
go


/*==============================================================*/
/* Table : CustomerListEntry                                    */
/*==============================================================*/
create table CustomerListEntry (
EntryID              numeric              not null,
ListID               numeric              not null,
EntryOrder           numeric              null,
EntryText            varchar(50)          null,
YukonDefinition      varchar(20)          null,
constraint PK_CUSTOMERLISTENTRY primary key  (EntryID)
)
go


/*==============================================================*/
/* Table : CustomerSelectionList                                */
/*==============================================================*/
create table CustomerSelectionList (
ListID               numeric              not null,
Ordering             varchar(1)           null,
SelectionLabel       varchar(30)          null,
WhereIsList          varchar(100)         null,
ListName             varchar(40)          null,
UserUpdateAvailable  varchar(1)           null,
constraint PK_CUSTOMERSELECTIONLIST primary key  (ListID)
)
go


/*==============================================================*/
/* Table : CustomerWebConfiguration                             */
/*==============================================================*/
create table CustomerWebConfiguration (
ConfigurationID      numeric              not null,
LogoLocation         varchar(100)         null,
Description          varchar(500)         null,
AlternateDisplayName varchar(50)          null,
URL                  varchar(100)         null,
constraint PK_CUSTOMERWEBCONFIGURATION primary key  (ConfigurationID)
)
go


/*==============================================================*/
/* Table : ECToAccountMapping                                   */
/*==============================================================*/
create table ECToAccountMapping (
EnergyCompanyID      numeric              not null,
AccountID            numeric              not null,
constraint PK_ECTOACCOUNTMAPPING primary key  (EnergyCompanyID, AccountID)
)
go


/*==============================================================*/
/* Table : ECToCallReportMapping                                */
/*==============================================================*/
create table ECToCallReportMapping (
EnergyCompanyID      numeric              not null,
CallReportID         numeric              not null,
constraint PK_ECTOCALLREPORTMAPPING primary key  (EnergyCompanyID, CallReportID)
)
go


/*==============================================================*/
/* Table : ECToGenericMapping                                   */
/*==============================================================*/
create table ECToGenericMapping (
EnergyCompanyID      numeric              not null,
ItemID               numeric              not null,
MappingCategory      varchar(40)          not null,
constraint PK_ECTOGENERICMAPPING primary key  (EnergyCompanyID, ItemID, MappingCategory)
)
go


/*==============================================================*/
/* Table : ECToInventoryMapping                                 */
/*==============================================================*/
create table ECToInventoryMapping (
EnergyCompanyID      numeric              not null,
InventoryID          numeric              not null,
constraint PK_ECTOINVENTORYMAPPING primary key  (EnergyCompanyID, InventoryID)
)
go


/*==============================================================*/
/* Table : ECToLMCustomerEventMapping                           */
/*==============================================================*/
create table ECToLMCustomerEventMapping (
EnergyCompanyID      numeric              not null,
EventID              numeric              not null,
constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key  (EnergyCompanyID, EventID)
)
go


/*==============================================================*/
/* Table : ECToWorkOrderMapping                                 */
/*==============================================================*/
create table ECToWorkOrderMapping (
EnergyCompanyID      numeric              not null,
WorkOrderID          numeric              not null,
constraint PK_ECTOWORKORDERMAPPING primary key  (EnergyCompanyID, WorkOrderID)
)
go


/*==============================================================*/
/* Table : InventoryBase                                        */
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
Notes                varchar(100)         null,
DEVICEID             numeric              null,
constraint PK_INVENTORYBASE primary key  (InventoryID)
)
go


/*==============================================================*/
/* Table : LMCustomerEventBase                                  */
/*==============================================================*/
create table LMCustomerEventBase (
EventID              numeric              not null,
EventTypeID          numeric              not null,
ActionID             numeric              not null,
EventDateTime        datetime             null,
Notes                varchar(100)         null,
AuthorizedBy         varchar(40)          null,
constraint PK_LMCUSTOMEREVENTBASE primary key  (EventID)
)
go


/*==============================================================*/
/* Table : LMHardwareBase                                       */
/*==============================================================*/
create table LMHardwareBase (
InventoryID          numeric              not null,
ManufacturerSerialNumber varchar(30)          null,
LMHardwareTypeID     numeric              not null,
constraint PK_LMHARDWAREBASE primary key  (InventoryID)
)
go


/*==============================================================*/
/* Table : LMHardwareConfiguration                              */
/*==============================================================*/
create table LMHardwareConfiguration (
InventoryID          numeric              not null,
ApplianceID          numeric              not null,
AddressingGroupID    numeric              null,
constraint PK_LMHARDWARECONFIGURATION primary key  (InventoryID, ApplianceID)
)
go


/*==============================================================*/
/* Table : LMHardwareEvent                                      */
/*==============================================================*/
create table LMHardwareEvent (
EventID              numeric              not null,
InventoryID          numeric              not null,
constraint PK_LMHARDWAREEVENT primary key  (EventID)
)
go


/*==============================================================*/
/* Table : LMProgramEvent                                       */
/*==============================================================*/
create table LMProgramEvent (
EventID              numeric              not null,
AccountID            numeric              not null,
LMProgramID          numeric              null,
constraint PK_LMPROGRAMEVENT primary key  (EventID)
)
go


/*==============================================================*/
/* Table : LMProgramWebPublishing                               */
/*==============================================================*/
create table LMProgramWebPublishing (
ApplianceCategoryID  numeric              not null,
LMProgramID          numeric              not null,
WebsettingsID        numeric              null,
ChanceOfControlID    numeric              null,
constraint PK_LMPROGRAMWEBPUBLISHING primary key  (ApplianceCategoryID, LMProgramID)
)
go


/*==============================================================*/
/* Table : LMThermostatManualOption                             */
/*==============================================================*/
create table LMThermostatManualOption (
InventoryID          numeric              not null,
PerviousTemperature  numeric              null,
HoldTemperature      varchar(1)           null,
OperationalStateID   numeric              null,
FanOperationID       numeric              null,
constraint PK_LMTHERMOSTATMANUALOPTION primary key  (InventoryID)
)
go


/*==============================================================*/
/* Table : LMThermostatSeason                                   */
/*==============================================================*/
create table LMThermostatSeason (
SeasonID             numeric              not null,
InventoryID          numeric              null,
WebConfigurationID   numeric              null,
StartDate            datetime             null,
DisplayOrder         numeric              null,
constraint PK_LMTHERMOSTATSEASON primary key  (SeasonID)
)
go


/*==============================================================*/
/* Table : LMThermostatSeasonEntry                              */
/*==============================================================*/
create table LMThermostatSeasonEntry (
SeasonID             numeric              not null,
TimeOfWeekID         numeric              null,
StartTime            numeric              null,
Temperature          numeric              null,
constraint PK_LMTHERMOSTATSEASONENTRY primary key  (SeasonID)
)
go


/*==============================================================*/
/* Table : ServiceCompany                                       */
/*==============================================================*/
create table ServiceCompany (
CompanyID            numeric              not null,
CompanyName          varchar(40)          null,
AddressID            numeric              null,
MainPhoneNumber      varchar(14)          null,
MainFaxNumber        varchar(14)          null,
PrimaryContactID     numeric              null,
HIType               varchar(40)          null,
constraint PK_SERVICECOMPANY primary key  (CompanyID)
)
go


/*==============================================================*/
/* Table : SiteInformation                                      */
/*==============================================================*/
create table SiteInformation (
SiteID               numeric              not null,
Feeder               varchar(20)          null,
Pole                 varchar(20)          null,
TransformerSize      varchar(20)          null,
ServiceVoltage       varchar(20)          null,
SubstationID         numeric              null,
constraint PK_SITEINFORMATION primary key  (SiteID)
)
go


/*==============================================================*/
/* Table : Substation                                           */
/*==============================================================*/
create table Substation (
SubstationID         numeric              not null,
SubstationName       varchar(50)          null,
RouteID              numeric              null,
constraint PK_SUBSTATION primary key  (SubstationID)
)
go


/*==============================================================*/
/* Table : WorkOrderBase                                        */
/*==============================================================*/
create table WorkOrderBase (
OrderID              numeric              not null,
OrderNumber          varchar(20)          null,
WorkTypeID           numeric              not null,
CurrentStateID       numeric              not null,
ServiceCompanyID     numeric              null,
DateReported         datetime             null,
OrderedBy            varchar(30)          null,
Description          varchar(200)         null,
DateScheduled        datetime             null,
DateCompleted        datetime             null,
ActionTaken          varchar(200)         null,
AccountID            numeric              null,
constraint PK_WORKORDERBASE primary key  (OrderID)
)
go


alter table InventoryBase
   add constraint FK_CUS_CSTA_CUS3 foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table CustomerAccount
   add constraint FK_CUS_CSTA_CUS2 foreign key (AccountSiteID)
      references AccountSite (AccountSiteID)
go


alter table ApplianceBase
   add constraint FK_CUS_CSTA_CUS4 foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table LMHardwareConfiguration
   add constraint FK_LMH_CSTL_CUS2 foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go


alter table ApplianceBase
   add constraint FK_APP_CSTL_APP foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
go


alter table AccountSite
   add constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
      references SiteInformation (SiteID)
go


alter table InventoryBase
   add constraint FK_CUS_HRDI_HAR2 foreign key (InstallationCompanyID)
      references ServiceCompany (CompanyID)
go


alter table LMHardwareConfiguration
   add constraint FK_LMH_LMHR_LMH foreign key (InventoryID)
      references LMHardwareBase (InventoryID)
go


alter table AccountSite
   add constraint FK_AccS_CstAd foreign key (StreetAddressID)
      references CustomerAddress (AddressID)
go


alter table WorkOrderBase
   add constraint FK_AccS_WkB foreign key (SiteID)
      references AccountSite (AccountSiteID)
>>>>>>> 1.8
go


alter table ApplianceBase
   add constraint FK_AppBs_LMPr foreign key (LMProgramID)
      references LMPROGRAM (DEVICEID)
go


alter table ContactNotification
   add constraint FK_CntNotCsLs1 foreign key (NotificationCategoryID)
      references CustomerListEntry (EntryID)
go


alter table ContactNotification
   add constraint FK_CntNotCsLs2 foreign key (NotificationLabelID)
      references CustomerListEntry (EntryID)
go


alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references CustomerContact (ContactID)
go


alter table CustomerAdditionalContact
   add constraint FK_CsCnt_CsAdCn foreign key (ContactID)
      references CustomerContact (ContactID)
go


alter table LMProgramWebPublishing
   add constraint FK_CsLEn_LPWbP foreign key (ChanceOfControlID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac foreign key (TonnageID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceAirConditioner
   add constraint FK_CsLsE_Ac_ty foreign key (TypeID)
      references CustomerListEntry (EntryID)
go


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE foreign key (EventTypeID)
      references CustomerListEntry (EntryID)
go


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE_a foreign key (ActionID)
      references CustomerListEntry (EntryID)
go


alter table LMThermostatManualOption
   add constraint FK_CsLsE_LThMnO2 foreign key (OperationalStateID)
      references CustomerListEntry (EntryID)
go


alter table LMThermostatManualOption
   add constraint FK_CsLsE_LThMnO1 foreign key (FanOperationID)
      references CustomerListEntry (EntryID)
go


alter table LMThermostatSeasonEntry
   add constraint FK_CsLsE_LThSE foreign key (TimeOfWeekID)
      references CustomerListEntry (EntryID)
go


alter table WorkOrderBase
   add constraint FK_CsLsE_WkB foreign key (WorkTypeID)
      references CustomerListEntry (EntryID)
go


alter table WorkOrderBase
   add constraint FK_CsLsE_WkB_c foreign key (CurrentStateID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceBase
   add constraint FK_CsLsEn_ApB foreign key (ManufacturerID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceBase
   add constraint FK_CsLsEn_ApB2 foreign key (LocationID)
      references CustomerListEntry (EntryID)
go


alter table CustomerFAQ
   add constraint FK_CsLsEn_CsF foreign key (SubjectID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceCategory
   add constraint FK_CsWC_ApCt foreign key (WebConfigurationID)
      references CustomerWebConfiguration (ConfigurationID)
go


alter table LMThermostatSeason
   add constraint FK_CsWbC_LThSs foreign key (WebConfigurationID)
      references CustomerWebConfiguration (ConfigurationID)
go


alter table CallReportBase
   add constraint FK_CstAc_ClRpB foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table LMProgramEvent
   add constraint FK_CstAc_LMPrEv foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table ServiceCompany
   add constraint FK_CstAdd_SrC foreign key (AddressID)
      references CustomerAddress (AddressID)
go


alter table CallReportBase
   add constraint FK_CstB_CllR foreign key ()
      references CustomerBase (CustomerID)
go


alter table CustomerAccount
   add constraint FK_CstBs_CstAcc foreign key (CustomerID)
      references CustomerBase (CustomerID)
go


alter table CustomerBase
   add constraint FK_CstBs_CstCnt foreign key (PrimaryContactID)
      references CustomerContact (ContactID)
go


alter table ServiceCompany
   add constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
      references CustomerContact (ContactID)
go


alter table CallReportBase
   add constraint FK_CstELs_ClRB foreign key (CallTypeID)
      references CustomerListEntry (EntryID)
go


alter table ApplianceCategory
   add constraint FK_CstLs_ApCt foreign key (CategoryID)
      references CustomerListEntry (EntryID)
go


alter table InventoryBase
   add constraint FK_INV_REF__CUS foreign key (CategoryID)
      references CustomerListEntry (EntryID)
go


alter table LMHardwareBase
   add constraint FK_LMH_REF__CUS foreign key (LMHardwareTypeID)
      references CustomerListEntry (EntryID)
go


alter table InventoryBase
   add constraint FK_Dev_InvB foreign key (DEVICEID)
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


alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Call foreign key (CallReportID)
      references CallReportBase (CallID)
go


alter table ECToCallReportMapping
   add constraint FK_ECTSrv_Enc foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
      references WorkOrderBase (OrderID)
go


alter table ECToLMCustomerEventMapping
   add constraint FK_EnCm_ECLmCs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table LMThermostatSeason
   add constraint FK_InvB_LThSs foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table LMHardwareEvent
   add constraint FK_IvB_LMHrEv foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table ECToLMCustomerEventMapping
   add constraint FK_LCsEv_ECLmCs foreign key (EventID)
      references LMCustomerEventBase (EventID)
go


alter table LMHardwareConfiguration
   add constraint FK_LMHrd_LMGr foreign key (AddressingGroupID)
      references LMGroup (DeviceID)
go


alter table LMProgramWebPublishing
   add constraint FK_LMPrWPb_CsWC foreign key (WebsettingsID)
      references CustomerWebConfiguration (ConfigurationID)
go


alter table LMProgramEvent
   add constraint FK_LMPrg_LMPrEv foreign key (LMProgramID)
      references LMPROGRAM (DEVICEID)
go


alter table LMProgramWebPublishing
   add constraint FK_LMprApp_App foreign key (ApplianceCategoryID)
      references ApplianceCategory (ApplianceCategoryID)
go


alter table LMProgramWebPublishing
   add constraint FK_LMprApp_LMPrg foreign key (LMProgramID)
      references LMPROGRAM (DEVICEID)
go


alter table Substation
   add constraint FK_Sub_Rt foreign key (RouteID)
      references Route (RouteID)
go


alter table SiteInformation
   add constraint FK_Sub_Si foreign key (SubstationID)
      references Substation (SubstationID)
go


alter table LMHardwareBase
   add constraint FK_LMH_ISA__INV foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table ApplianceAirConditioner
   add constraint FK_APP_ISA__APP foreign key (ApplianceID)
      references ApplianceBase (ApplianceID)
go


