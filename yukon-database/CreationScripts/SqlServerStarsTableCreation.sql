/*==============================================================*/
/* DBMS name:      Microsoft SQL Server 2000                    */
/* Created on:     1/10/2005 2:41:33 PM                         */
/*==============================================================*/


if exists (select 1
            from  sysindexes
           where  id    = object_id('AccountSite')
            and   name  = 'CstSrvCstProp_FK'
            and   indid > 0
            and   indid < 255)
   drop index AccountSite.CstSrvCstProp_FK
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('ApplianceBase')
            and   name  = 'CstAcc_CstLdInfo_FK'
            and   indid > 0
            and   indid < 255)
   drop index ApplianceBase.CstAcc_CstLdInfo_FK
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('ApplianceBase')
            and   name  = 'CstLdTy_CstLdInf_FK'
            and   indid > 0
            and   indid < 255)
   drop index ApplianceBase.CstLdTy_CstLdInf_FK
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('CustomerAccount')
            and   name  = 'CstAccCstPro_FK'
            and   indid > 0
            and   indid < 255)
   drop index CustomerAccount.CstAccCstPro_FK
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('InventoryBase')
            and   name  = 'CstAccCstHrdB_FK'
            and   indid > 0
            and   indid < 255)
   drop index InventoryBase.CstAccCstHrdB_FK
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('InventoryBase')
            and   name  = 'HrdInst_CstHrdBs_FK'
            and   indid > 0
            and   indid < 255)
   drop index InventoryBase.HrdInst_CstHrdBs_FK
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('LMHardwareConfiguration')
            and   name  = 'CstLdIn_LMHrdCfg_FK'
            and   indid > 0
            and   indid < 255)
   drop index LMHardwareConfiguration.CstLdIn_LMHrdCfg_FK
go


if exists (select 1
            from  sysindexes
           where  id    = object_id('LMHardwareConfiguration')
            and   name  = 'LmHrd_LmHrdCfg_FK'
            and   indid > 0
            and   indid < 255)
   drop index LMHardwareConfiguration.LmHrd_LmHrdCfg_FK
go


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
           where  id = object_id('ApplianceDualFuel')
            and   type = 'U')
   drop table ApplianceDualFuel
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceGenerator')
            and   type = 'U')
   drop table ApplianceGenerator
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceGrainDryer')
            and   type = 'U')
   drop table ApplianceGrainDryer
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceHeatPump')
            and   type = 'U')
   drop table ApplianceHeatPump
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceIrrigation')
            and   type = 'U')
   drop table ApplianceIrrigation
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceStorageHeat')
            and   type = 'U')
   drop table ApplianceStorageHeat
go


if exists (select 1
            from  sysobjects
           where  id = object_id('ApplianceWaterHeater')
            and   type = 'U')
   drop table ApplianceWaterHeater
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CallReportBase')
            and   type = 'U')
   drop table CallReportBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerAccount')
            and   type = 'U')
   drop table CustomerAccount
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerFAQ')
            and   type = 'U')
   drop table CustomerFAQ
go


if exists (select 1
            from  sysobjects
           where  id = object_id('CustomerResidence')
            and   type = 'U')
   drop table CustomerResidence
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
           where  id = object_id('InterviewQuestion')
            and   type = 'U')
   drop table InterviewQuestion
go


if exists (select 1
            from  sysobjects
           where  id = object_id('InventoryBase')
            and   type = 'U')
   drop table InventoryBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMConfigurationBase')
            and   type = 'U')
   drop table LMConfigurationBase
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMConfigurationExpressCom')
            and   type = 'U')
   drop table LMConfigurationExpressCom
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMConfigurationSA205')
            and   type = 'U')
   drop table LMConfigurationSA205
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMConfigurationSA305')
            and   type = 'U')
   drop table LMConfigurationSA305
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMConfigurationVersacom')
            and   type = 'U')
   drop table LMConfigurationVersacom
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
           where  id = object_id('LMThermostatManualEvent')
            and   type = 'U')
   drop table LMThermostatManualEvent
go


if exists (select 1
            from  sysobjects
           where  id = object_id('LMThermostatSchedule')
            and   type = 'U')
   drop table LMThermostatSchedule
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
/* Table: AccountSite                                           */
/*==============================================================*/
create table AccountSite (
   AccountSiteID        numeric              not null,
   SiteInformationID    numeric              null,
   SiteNumber           varchar(40)          not null,
   StreetAddressID      numeric              null,
   PropertyNotes        varchar(300)         null
)
go


INSERT INTO AccountSite VALUES (0,0,'(none)',0,'(none)');
alter table AccountSite
   add constraint PK_ACCOUNTSITE primary key  (AccountSiteID)
go


/*==============================================================*/
/* Index: CstSrvCstProp_FK                                      */
/*==============================================================*/
create   index CstSrvCstProp_FK on AccountSite (
SiteInformationID
)
go


/*==============================================================*/
/* Table: ApplianceAirConditioner                               */
/*==============================================================*/
create table ApplianceAirConditioner (
   ApplianceID          numeric              not null,
   TonnageID            numeric              null,
   TypeID               numeric              null
)
go


alter table ApplianceAirConditioner
   add constraint PK_APPLIANCEAIRCONDITIONER primary key  (ApplianceID)
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
   KWCapacity           numeric              null,
   EfficiencyRating     numeric              null,
   Notes                varchar(500)         null,
   ModelNumber          varchar(40)          not null
)
go


alter table ApplianceBase
   add constraint PK_APPLIANCEBASE primary key  (ApplianceID)
go


/*==============================================================*/
/* Index: CstAcc_CstLdInfo_FK                                   */
/*==============================================================*/
create   index CstAcc_CstLdInfo_FK on ApplianceBase (
AccountID
)
go


/*==============================================================*/
/* Index: CstLdTy_CstLdInf_FK                                   */
/*==============================================================*/
create   index CstLdTy_CstLdInf_FK on ApplianceBase (
ApplianceCategoryID
)
go


/*==============================================================*/
/* Table: ApplianceCategory                                     */
/*==============================================================*/
create table ApplianceCategory (
   ApplianceCategoryID  numeric              not null,
   Description          varchar(40)          null,
   CategoryID           numeric              null,
   WebConfigurationID   numeric              null
)
go


insert into ApplianceCategory values (0,'(none)',0,0);
alter table ApplianceCategory
   add constraint PK_APPLIANCECATEGORY primary key  (ApplianceCategoryID)
go


/*==============================================================*/
/* Table: ApplianceDualFuel                                     */
/*==============================================================*/
create table ApplianceDualFuel (
   ApplianceID          numeric              not null,
   SwitchOverTypeID     numeric              not null,
   SecondaryKWCapacity  numeric              not null,
   SecondaryEnergySourceID numeric              not null
)
go


alter table ApplianceDualFuel
   add constraint PK_APPLIANCEDUALFUEL primary key  (ApplianceID)
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
   StartDelaySeconds    numeric              not null
)
go


alter table ApplianceGenerator
   add constraint PK_APPLIANCEGENERATOR primary key  (ApplianceID)
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
   BlowerHeatSourceID   numeric              not null
)
go


alter table ApplianceGrainDryer
   add constraint PK_APPLIANCEGRAINDRYER primary key  (ApplianceID)
go


/*==============================================================*/
/* Table: ApplianceHeatPump                                     */
/*==============================================================*/
create table ApplianceHeatPump (
   ApplianceID          numeric              not null,
   PumpTypeID           numeric              not null,
   StandbySourceID      numeric              not null,
   SecondsDelayToRestart numeric              not null,
   PumpSizeID           numeric              not null
)
go


alter table ApplianceHeatPump
   add constraint PK_APPLIANCEHEATPUMP primary key  (ApplianceID)
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
   MeterVoltageID       numeric              not null
)
go


alter table ApplianceIrrigation
   add constraint PK_APPLIANCEIRRIGATION primary key  (ApplianceID)
go


/*==============================================================*/
/* Table: ApplianceStorageHeat                                  */
/*==============================================================*/
create table ApplianceStorageHeat (
   ApplianceID          numeric              not null,
   StorageTypeID        numeric              not null,
   PeakKWCapacity       numeric              not null,
   HoursToRecharge      numeric              not null
)
go


alter table ApplianceStorageHeat
   add constraint PK_APPLIANCESTORAGEHEAT primary key  (ApplianceID)
go


/*==============================================================*/
/* Table: ApplianceWaterHeater                                  */
/*==============================================================*/
create table ApplianceWaterHeater (
   ApplianceID          numeric              not null,
   NumberOfGallonsID    numeric              null,
   EnergySourceID       numeric              not null,
   NumberOfElements     numeric              not null
)
go


alter table ApplianceWaterHeater
   add constraint PK_APPLIANCEWATERHEATER primary key  (ApplianceID)
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
   AccountID            numeric              null
)
go


alter table CallReportBase
   add constraint PK_CALLREPORTBASE primary key  (CallID)
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
   AccountNotes         varchar(200)         null
)
go


INSERT INTO CustomerAccount VALUES (0,0,'(none)',-1,0,'(none)');
alter table CustomerAccount
   add constraint PK_CUSTOMERACCOUNT primary key  (AccountID)
go


/*==============================================================*/
/* Index: CstAccCstPro_FK                                       */
/*==============================================================*/
create   index CstAccCstPro_FK on CustomerAccount (
AccountSiteID
)
go


/*==============================================================*/
/* Table: CustomerFAQ                                           */
/*==============================================================*/
create table CustomerFAQ (
   QuestionID           numeric              not null,
   SubjectID            numeric              null,
   Question             varchar(200)         null,
   Answer               varchar(500)         null
)
go


insert into CustomerFAQ values(1,1231,'How long does it take for my program to become effective after adding or changing a program?','Immediately! You can take advantage of energy savings the moment you decide to. Just make your selection on the "Programs - Add/Change" page, click the submit button, and select Yes at the prompt.');
insert into CustomerFAQ values(2,1231,'How do I find out more about my program or other programs?','Go to the "Programs - Add/Change" page and click the Program Details button. You will find all of the information you need here regarding the program, amount of control, and savings.');
insert into CustomerFAQ values(3,1231,'Can I sign up for more than one program?','Certainly! The more programs you enroll in, the more energy savings you will receive.');
insert into CustomerFAQ values(4,1232,'Can I control my thermostat even if I do not know my current settings?','Yes. You may select the temperature change (up or down) in degrees without knowing the current temperature or simply set a new specific temperature. If pre-cooling, you may also select a new specific temperature or select the number of degress to decrease in temperature.');
insert into CustomerFAQ values(5,1232,'What does the Fan setting do?','The fan setting controls the operation of the fan. <br>Auto - the fan runs only as necessary to maintain the current temperature settings. <br>On - the fan runs continuously. <br>Off - the fan does not run.');
insert into CustomerFAQ values(6,1232,'Does the utility company have access to my thermostat?','The utility only has access to your thermostat for control based on the programs you have signed up for. When not being controlled, you have complete control of your thermostat.');
insert into CustomerFAQ values(7,1233,'How much credit do I receive if I opt out while controlling?','You will receive credit for the portion of time you were controlled.');
alter table CustomerFAQ
   add constraint PK_CUSTOMERFAQ primary key  (QuestionID)
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
   Notes                varchar(300)         null
)
go


alter table CustomerResidence
   add constraint PK_CUSTOMERRESIDENCE primary key  (AccountSiteID)
go


/*==============================================================*/
/* Table: ECToAccountMapping                                    */
/*==============================================================*/
create table ECToAccountMapping (
   EnergyCompanyID      numeric              not null,
   AccountID            numeric              not null
)
go


alter table ECToAccountMapping
   add constraint PK_ECTOACCOUNTMAPPING primary key  (EnergyCompanyID, AccountID)
go


/*==============================================================*/
/* Table: ECToCallReportMapping                                 */
/*==============================================================*/
create table ECToCallReportMapping (
   EnergyCompanyID      numeric              not null,
   CallReportID         numeric              not null
)
go


alter table ECToCallReportMapping
   add constraint PK_ECTOCALLREPORTMAPPING primary key  (EnergyCompanyID, CallReportID)
go


/*==============================================================*/
/* Table: ECToGenericMapping                                    */
/*==============================================================*/
create table ECToGenericMapping (
   EnergyCompanyID      numeric              not null,
   ItemID               numeric              not null,
   MappingCategory      varchar(40)          not null
)
go


insert into ectogenericmapping values (-1,-1,'LMThermostatSchedule');
insert into ectogenericmapping values (-1, 1001, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1002, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1003, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1004, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1005, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1006, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1007, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1008, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1009, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1010, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1011, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1012, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1013, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1014, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1015, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1016, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1017, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1018, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1019, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1020, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1021, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1022, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1023, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1024, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1025, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1026, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1027, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1028, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1029, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1030, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1031, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1032, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1033, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1034, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1035, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1036, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1037, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1038, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1039, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1040, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1041, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1042, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1043, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1044, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1045, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1046, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1047, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1048, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1049, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1050, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1051, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1052, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1053, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1054, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1055, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1056, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1057, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1058, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1059, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1060, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1061, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1062, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1063, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1064, 'YukonSelectionList');
alter table ECToGenericMapping
   add constraint PK_ECTOGENERICMAPPING primary key  (EnergyCompanyID, ItemID, MappingCategory)
go


/*==============================================================*/
/* Table: ECToInventoryMapping                                  */
/*==============================================================*/
create table ECToInventoryMapping (
   EnergyCompanyID      numeric              not null,
   InventoryID          numeric              not null
)
go


alter table ECToInventoryMapping
   add constraint PK_ECTOINVENTORYMAPPING primary key  (EnergyCompanyID, InventoryID)
go


/*==============================================================*/
/* Table: ECToLMCustomerEventMapping                            */
/*==============================================================*/
create table ECToLMCustomerEventMapping (
   EnergyCompanyID      numeric              not null,
   EventID              numeric              not null
)
go


alter table ECToLMCustomerEventMapping
   add constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key  (EnergyCompanyID, EventID)
go


/*==============================================================*/
/* Table: ECToWorkOrderMapping                                  */
/*==============================================================*/
create table ECToWorkOrderMapping (
   EnergyCompanyID      numeric              not null,
   WorkOrderID          numeric              not null
)
go


alter table ECToWorkOrderMapping
   add constraint PK_ECTOWORKORDERMAPPING primary key  (EnergyCompanyID, WorkOrderID)
go


/*==============================================================*/
/* Table: InterviewQuestion                                     */
/*==============================================================*/
create table InterviewQuestion (
   QuestionID           numeric              not null,
   QuestionType         numeric              null,
   Question             varchar(200)         null,
   Mandatory            varchar(1)           null,
   DisplayOrder         numeric              null,
   AnswerType           numeric              null,
   ExpectedAnswer       numeric              null
)
go


alter table InterviewQuestion
   add constraint PK_INTERVIEWQUESTION primary key  (QuestionID)
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
   DeviceLabel          varchar(60)          null
)
go


INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0,'(none)');
alter table InventoryBase
   add constraint PK_INVENTORYBASE primary key  (InventoryID)
go


/*==============================================================*/
/* Index: CstAccCstHrdB_FK                                      */
/*==============================================================*/
create   index CstAccCstHrdB_FK on InventoryBase (
AccountID
)
go


/*==============================================================*/
/* Index: HrdInst_CstHrdBs_FK                                   */
/*==============================================================*/
create   index HrdInst_CstHrdBs_FK on InventoryBase (
InstallationCompanyID
)
go


/*==============================================================*/
/* Table: LMConfigurationBase                                   */
/*==============================================================*/
create table LMConfigurationBase (
   ConfigurationID      numeric              not null,
   ColdLoadPickup       varchar(128)         not null,
   TamperDetect         varchar(128)         not null
)
go


insert into LMConfigurationBase values (0, '(none)', '(none)');
alter table LMConfigurationBase
   add constraint PK_LMCONFIGURATIONBASE primary key  (ConfigurationID)
go


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
   Splinter             varchar(80)          not null
)
go


alter table LMConfigurationExpressCom
   add constraint PK_LMCONFIGURATIONEXPRESSCOM primary key  (ConfigurationID)
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
   Slot6                numeric              not null
)
go


alter table LMConfigurationSA205
   add constraint PK_LMCONFIGURATIONSA205 primary key  (ConfigurationID)
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
   RateHierarchy        numeric              not null
)
go


alter table LMConfigurationSA305
   add constraint PK_LMCONFIGURATIONSA305 primary key  (ConfigurationID)
go


/*==============================================================*/
/* Table: LMConfigurationVersacom                               */
/*==============================================================*/
create table LMConfigurationVersacom (
   ConfigurationID      numeric              not null,
   UtilityID            numeric              not null,
   Section              numeric              not null,
   ClassAddress         numeric              not null,
   DivisionAddress      numeric              not null
)
go


alter table LMConfigurationVersacom
   add constraint PK_LMCONFIGURATIONVERSACOM primary key  (ConfigurationID)
go


/*==============================================================*/
/* Table: LMCustomerEventBase                                   */
/*==============================================================*/
create table LMCustomerEventBase (
   EventID              numeric              not null,
   EventTypeID          numeric              not null,
   ActionID             numeric              not null,
   EventDateTime        datetime             null,
   Notes                varchar(100)         null,
   AuthorizedBy         varchar(40)          null
)
go


alter table LMCustomerEventBase
   add constraint PK_LMCUSTOMEREVENTBASE primary key  (EventID)
go


/*==============================================================*/
/* Table: LMHardwareBase                                        */
/*==============================================================*/
create table LMHardwareBase (
   InventoryID          numeric              not null,
   ManufacturerSerialNumber varchar(30)          null,
   LMHardwareTypeID     numeric              not null,
   RouteID              numeric              not null,
   ConfigurationID      numeric              not null
)
go


alter table LMHardwareBase
   add constraint PK_LMHARDWAREBASE primary key  (InventoryID)
go


/*==============================================================*/
/* Table: LMHardwareConfiguration                               */
/*==============================================================*/
create table LMHardwareConfiguration (
   InventoryID          numeric              not null,
   ApplianceID          numeric              not null,
   AddressingGroupID    numeric              null,
   LoadNumber           numeric              null
)
go


alter table LMHardwareConfiguration
   add constraint PK_LMHARDWARECONFIGURATION primary key  (InventoryID, ApplianceID)
go


/*==============================================================*/
/* Index: LmHrd_LmHrdCfg_FK                                     */
/*==============================================================*/
create   index LmHrd_LmHrdCfg_FK on LMHardwareConfiguration (
InventoryID
)
go


/*==============================================================*/
/* Index: CstLdIn_LMHrdCfg_FK                                   */
/*==============================================================*/
create   index CstLdIn_LMHrdCfg_FK on LMHardwareConfiguration (
ApplianceID
)
go


/*==============================================================*/
/* Table: LMHardwareEvent                                       */
/*==============================================================*/
create table LMHardwareEvent (
   EventID              numeric              not null,
   InventoryID          numeric              not null
)
go


alter table LMHardwareEvent
   add constraint PK_LMHARDWAREEVENT primary key  (EventID)
go


/*==============================================================*/
/* Table: LMProgramEvent                                        */
/*==============================================================*/
create table LMProgramEvent (
   EventID              numeric              not null,
   AccountID            numeric              not null,
   ProgramID            numeric              null
)
go


alter table LMProgramEvent
   add constraint PK_LMPROGRAMEVENT primary key  (EventID)
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
   ProgramID            numeric              not null
)
go


insert into LMProgramWebPublishing values (0,0,0,0,0,0);
alter table LMProgramWebPublishing
   add constraint PK_LMPROGRAMWEBPUBLISHING primary key  (ProgramID)
go


/*==============================================================*/
/* Table: LMThermostatManualEvent                               */
/*==============================================================*/
create table LMThermostatManualEvent (
   EventID              numeric              not null,
   InventoryID          numeric              not null,
   PreviousTemperature  numeric              null,
   HoldTemperature      varchar(1)           null,
   OperationStateID     numeric              null,
   FanOperationID       numeric              null
)
go


alter table LMThermostatManualEvent
   add constraint PK_LMTHERMOSTATMANUALEVENT primary key  (EventID)
go


/*==============================================================*/
/* Table: LMThermostatSchedule                                  */
/*==============================================================*/
create table LMThermostatSchedule (
   ScheduleID           numeric              not null,
   ScheduleName         varchar(60)          not null,
   ThermostatTypeID     numeric              not null,
   AccountID            numeric              not null,
   InventoryID          numeric              not null
)
go


INSERT INTO LMThermostatSchedule VALUES (-1,'(none)',0,0,0);
alter table LMThermostatSchedule
   add constraint PK_LMTHERMOSTATSCHEDULE primary key  (ScheduleID)
go


/*==============================================================*/
/* Table: LMThermostatSeason                                    */
/*==============================================================*/
create table LMThermostatSeason (
   SeasonID             numeric              not null,
   ScheduleID           numeric              null,
   WebConfigurationID   numeric              null,
   StartDate            datetime             null,
   DisplayOrder         numeric              null
)
go


INSERT INTO LMThermostatSeason VALUES (-1,-1,-1,'01-JUN-00',1);
INSERT INTO LMThermostatSeason VALUES (-2,-1,-2,'15-OCT-00',2);
alter table LMThermostatSeason
   add constraint PK_LMTHERMOSTATSEASON primary key  (SeasonID)
go


/*==============================================================*/
/* Table: LMThermostatSeasonEntry                               */
/*==============================================================*/
create table LMThermostatSeasonEntry (
   EntryID              numeric              not null,
   SeasonID             numeric              not null,
   TimeOfWeekID         numeric              not null,
   StartTime            numeric              not null,
   Temperature          numeric              null
)
go


INSERT INTO LMThermostatSeasonEntry VALUES (-24,-1,1171,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-23,-1,1171,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-22,-1,1171,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-21,-1,1171,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-20,-1,1173,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-19,-1,1173,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-18,-1,1173,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-17,-1,1173,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-16,-1,1174,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-15,-1,1174,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-14,-1,1174,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-13,-1,1174,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-12,-2,1171,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-11,-2,1171,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-10,-2,1171,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-9,-2,1171,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-8,-2,1173,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-7,-2,1173,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-6,-2,1173,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-5,-2,1173,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-4,-2,1174,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-3,-2,1174,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-2,-2,1174,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-1,-2,1174,75600,72);
alter table LMThermostatSeasonEntry
   add constraint PK_LMTHERMOSTATSEASONENTRY primary key  (EntryID)
go


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
   HIType               varchar(40)          null
)
go


INSERT INTO ServiceCompany VALUES (0,'(none)',0,'(none)','(none)',0,'(none)');
alter table ServiceCompany
   add constraint PK_SERVICECOMPANY primary key  (CompanyID)
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
   SubstationID         numeric              null
)
go


INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0);
alter table SiteInformation
   add constraint PK_SITEINFORMATION primary key  (SiteID)
go


/*==============================================================*/
/* Table: Substation                                            */
/*==============================================================*/
create table Substation (
   SubstationID         numeric              not null,
   SubstationName       varchar(50)          null,
   RouteID              numeric              null
)
go


INSERT INTO Substation VALUES (0,'(none)',0);
alter table Substation
   add constraint PK_SUBSTATION primary key  (SubstationID)
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
   Description          varchar(200)         null,
   DateScheduled        datetime             null,
   DateCompleted        datetime             null,
   ActionTaken          varchar(200)         null,
   AccountID            numeric              null
)
go


alter table WorkOrderBase
   add constraint PK_WORKORDERBASE primary key  (OrderID)
go


alter table AccountSite
   add constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
      references SiteInformation (SiteID)
go


alter table AccountSite
   add constraint FK_AccS_CstAd foreign key (StreetAddressID)
      references Address (AddressID)
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


alter table CallReportBase
   add constraint FK_CstAc_ClRpB foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table CallReportBase
   add constraint FK_CstELs_ClRB foreign key (CallTypeID)
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


alter table CustomerFAQ
   add constraint FK_CsLsEn_CsF foreign key (SubjectID)
      references YukonListEntry (EntryID)
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


alter table ECToAccountMapping
   add constraint FK_ECTAcc_CstAcc foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table ECToAccountMapping
   add constraint FK_ECTAcc_Enc foreign key (EnergyCompanyID)
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


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go


alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
      references WorkOrderBase (OrderID)
go


alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn foreign key (AnswerType)
      references YukonListEntry (EntryID)
go


alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn2 foreign key (QuestionType)
      references YukonListEntry (EntryID)
go


alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn3 foreign key (ExpectedAnswer)
      references YukonListEntry (EntryID)
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


alter table LMConfigurationVersacom
   add constraint FK_LMCfgVcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID)
go


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE foreign key (EventTypeID)
      references YukonListEntry (EntryID)
go


alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE_a foreign key (ActionID)
      references YukonListEntry (EntryID)
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
   add constraint FK_LMHrdB_Rt foreign key (RouteID)
      references Route (RouteID)
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


alter table LMThermostatSchedule
   add constraint FK_LMThSc_CsAc foreign key (AccountID)
      references CustomerAccount (AccountID)
go


alter table LMThermostatSchedule
   add constraint FK_LMThSc_InvB foreign key (InventoryID)
      references InventoryBase (InventoryID)
go


alter table LMThermostatSchedule
   add constraint FK_LMThSc_YkLs foreign key (ThermostatTypeID)
      references YukonListEntry (EntryID)
go


alter table LMThermostatSeason
   add constraint FK_ThSc_LThSs foreign key (ScheduleID)
      references LMThermostatSchedule (ScheduleID)
go


alter table LMThermostatSeason
   add constraint FK_YkWbC_LThSs foreign key (WebConfigurationID)
      references YukonWebConfiguration (ConfigurationID)
go


alter table LMThermostatSeasonEntry
   add constraint FK_CsLsE_LThSE foreign key (TimeOfWeekID)
      references YukonListEntry (EntryID)
go


alter table LMThermostatSeasonEntry
   add constraint FK_LThSe_LThSEn foreign key (SeasonID)
      references LMThermostatSeason (SeasonID)
go


alter table ServiceCompany
   add constraint FK_CstAdd_SrC foreign key (AddressID)
      references Address (AddressID)
go


alter table ServiceCompany
   add constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
      references Contact (ContactID)
go


alter table SiteInformation
   add constraint FK_Sub_Si foreign key (SubstationID)
      references Substation (SubstationID)
go


alter table Substation
   add constraint FK_Sub_Rt foreign key (RouteID)
      references Route (RouteID)
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


