/*==============================================================*/
/* DBMS name:      ORACLE Version 9i                            */
/* Created on:     2/3/2005 5:26:25 PM                          */
/*==============================================================*/


drop index CstSrvCstProp_FK;

drop index CstAcc_CstLdInfo_FK;

drop index CstLdTy_CstLdInf_FK;

drop index CstAccCstPro_FK;

drop index CstAccCstHrdB_FK;

drop index HrdInst_CstHrdBs_FK;

drop index CstLdIn_LMHrdCfg_FK;

drop index LmHrd_LmHrdCfg_FK;

drop table AccountSite cascade constraints;

drop table ApplianceAirConditioner cascade constraints;

drop table ApplianceBase cascade constraints;

drop table ApplianceCategory cascade constraints;

drop table ApplianceDualFuel cascade constraints;

drop table ApplianceGenerator cascade constraints;

drop table ApplianceGrainDryer cascade constraints;

drop table ApplianceHeatPump cascade constraints;

drop table ApplianceIrrigation cascade constraints;

drop table ApplianceStorageHeat cascade constraints;

drop table ApplianceWaterHeater cascade constraints;

drop table CallReportBase cascade constraints;

drop table CustomerAccount cascade constraints;

drop table CustomerFAQ cascade constraints;

drop table CustomerResidence cascade constraints;

drop table ECToAccountMapping cascade constraints;

drop table ECToCallReportMapping cascade constraints;

drop table ECToGenericMapping cascade constraints;

drop table ECToInventoryMapping cascade constraints;

drop table ECToLMCustomerEventMapping cascade constraints;

drop table ECToWorkOrderMapping cascade constraints;

drop table InterviewQuestion cascade constraints;

drop table InventoryBase cascade constraints;

drop table LMConfigurationBase cascade constraints;

drop table LMConfigurationExpressCom cascade constraints;

drop table LMConfigurationSA205 cascade constraints;

drop table LMConfigurationSA305 cascade constraints;

drop table LMConfigurationSASimple cascade constraints;

drop table LMConfigurationVersacom cascade constraints;

drop table LMCustomerEventBase cascade constraints;

drop table LMHardwareBase cascade constraints;

drop table LMHardwareConfiguration cascade constraints;

drop table LMHardwareEvent cascade constraints;

drop table LMProgramEvent cascade constraints;

drop table LMProgramWebPublishing cascade constraints;

drop table LMThermostatManualEvent cascade constraints;

drop table LMThermostatSchedule cascade constraints;

drop table LMThermostatSeason cascade constraints;

drop table LMThermostatSeasonEntry cascade constraints;

drop table ServiceCompany cascade constraints;

drop table SiteInformation cascade constraints;

drop table Substation cascade constraints;

drop table WorkOrderBase cascade constraints;

/*==============================================================*/
/* Table: AccountSite                                           */
/*==============================================================*/
create table AccountSite  (
   AccountSiteID        NUMBER                          not null,
   SiteInformationID    NUMBER,
   SiteNumber           VARCHAR2(40)                    not null,
   StreetAddressID      NUMBER,
   PropertyNotes        VARCHAR2(300)
);

INSERT INTO AccountSite VALUES (0,0,'(none)',0,'(none)');
alter table AccountSite
   add constraint PK_ACCOUNTSITE primary key (AccountSiteID);

/*==============================================================*/
/* Index: CstSrvCstProp_FK                                      */
/*==============================================================*/
create index CstSrvCstProp_FK on AccountSite (
   SiteInformationID ASC
);

/*==============================================================*/
/* Table: ApplianceAirConditioner                               */
/*==============================================================*/
create table ApplianceAirConditioner  (
   ApplianceID          NUMBER                          not null,
   TonnageID            NUMBER,
   TypeID               NUMBER
);

alter table ApplianceAirConditioner
   add constraint PK_APPLIANCEAIRCONDITIONER primary key (ApplianceID);

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
   KWCapacity           NUMBER,
   EfficiencyRating     NUMBER,
   Notes                VARCHAR2(500),
   ModelNumber          VARCHAR2(40)                    not null
);

alter table ApplianceBase
   add constraint PK_APPLIANCEBASE primary key (ApplianceID);

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
   WebConfigurationID   NUMBER
);

insert into ApplianceCategory values (0,'(none)',0,0);
alter table ApplianceCategory
   add constraint PK_APPLIANCECATEGORY primary key (ApplianceCategoryID);

/*==============================================================*/
/* Table: ApplianceDualFuel                                     */
/*==============================================================*/
create table ApplianceDualFuel  (
   ApplianceID          NUMBER                          not null,
   SwitchOverTypeID     NUMBER                          not null,
   SecondaryKWCapacity  NUMBER                          not null,
   SecondaryEnergySourceID NUMBER                          not null
);

alter table ApplianceDualFuel
   add constraint PK_APPLIANCEDUALFUEL primary key (ApplianceID);

/*==============================================================*/
/* Table: ApplianceGenerator                                    */
/*==============================================================*/
create table ApplianceGenerator  (
   ApplianceID          NUMBER                          not null,
   TransferSwitchTypeID NUMBER                          not null,
   TransferSwitchMfgID  NUMBER                          not null,
   PeakKWCapacity       NUMBER                          not null,
   FuelCapGallons       NUMBER                          not null,
   StartDelaySeconds    NUMBER                          not null
);

alter table ApplianceGenerator
   add constraint PK_APPLIANCEGENERATOR primary key (ApplianceID);

/*==============================================================*/
/* Table: ApplianceGrainDryer                                   */
/*==============================================================*/
create table ApplianceGrainDryer  (
   ApplianceID          NUMBER                          not null,
   DryerTypeID          NUMBER                          not null,
   BinSizeID            NUMBER                          not null,
   BlowerEnergySourceID NUMBER                          not null,
   BlowerHorsePowerID   NUMBER                          not null,
   BlowerHeatSourceID   NUMBER                          not null
);

alter table ApplianceGrainDryer
   add constraint PK_APPLIANCEGRAINDRYER primary key (ApplianceID);

/*==============================================================*/
/* Table: ApplianceHeatPump                                     */
/*==============================================================*/
create table ApplianceHeatPump  (
   ApplianceID          NUMBER                          not null,
   PumpTypeID           NUMBER                          not null,
   StandbySourceID      NUMBER                          not null,
   SecondsDelayToRestart NUMBER                          not null,
   PumpSizeID           NUMBER                          not null
);

alter table ApplianceHeatPump
   add constraint PK_APPLIANCEHEATPUMP primary key (ApplianceID);

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
   MeterVoltageID       NUMBER                          not null
);

alter table ApplianceIrrigation
   add constraint PK_APPLIANCEIRRIGATION primary key (ApplianceID);

/*==============================================================*/
/* Table: ApplianceStorageHeat                                  */
/*==============================================================*/
create table ApplianceStorageHeat  (
   ApplianceID          NUMBER                          not null,
   StorageTypeID        NUMBER                          not null,
   PeakKWCapacity       NUMBER                          not null,
   HoursToRecharge      NUMBER                          not null
);

alter table ApplianceStorageHeat
   add constraint PK_APPLIANCESTORAGEHEAT primary key (ApplianceID);

/*==============================================================*/
/* Table: ApplianceWaterHeater                                  */
/*==============================================================*/
create table ApplianceWaterHeater  (
   ApplianceID          NUMBER                          not null,
   NumberOfGallonsID    NUMBER,
   EnergySourceID       NUMBER                          not null,
   NumberOfElements     NUMBER                          not null
);

alter table ApplianceWaterHeater
   add constraint PK_APPLIANCEWATERHEATER primary key (ApplianceID);

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
   AccountID            NUMBER
);

alter table CallReportBase
   add constraint PK_CALLREPORTBASE primary key (CallID);

/*==============================================================*/
/* Table: CustomerAccount                                       */
/*==============================================================*/
create table CustomerAccount  (
   AccountID            NUMBER                          not null,
   AccountSiteID        NUMBER,
   AccountNumber        VARCHAR2(40),
   CustomerID           NUMBER                          not null,
   BillingAddressID     NUMBER,
   AccountNotes         VARCHAR2(200)
);

INSERT INTO CustomerAccount VALUES (0,0,'(none)',-1,0,'(none)');
alter table CustomerAccount
   add constraint PK_CUSTOMERACCOUNT primary key (AccountID);

/*==============================================================*/
/* Index: CstAccCstPro_FK                                       */
/*==============================================================*/
create index CstAccCstPro_FK on CustomerAccount (
   AccountSiteID ASC
);

/*==============================================================*/
/* Table: CustomerFAQ                                           */
/*==============================================================*/
create table CustomerFAQ  (
   QuestionID           NUMBER                          not null,
   SubjectID            NUMBER,
   Question             VARCHAR2(200),
   Answer               VARCHAR2(500)
);

insert into CustomerFAQ values(1,1231,'How long does it take for my program to become effective after adding or changing a program?','Immediately! You can take advantage of energy savings the moment you decide to. Just make your selection on the "Programs - Add/Change" page, click the submit button, and select Yes at the prompt.');
insert into CustomerFAQ values(2,1231,'How do I find out more about my program or other programs?','Go to the "Programs - Add/Change" page and click the Program Details button. You will find all of the information you need here regarding the program, amount of control, and savings.');
insert into CustomerFAQ values(3,1231,'Can I sign up for more than one program?','Certainly! The more programs you enroll in, the more energy savings you will receive.');
insert into CustomerFAQ values(4,1232,'Can I control my thermostat even if I do not know my current settings?','Yes. You may select the temperature change (up or down) in degrees without knowing the current temperature or simply set a new specific temperature. If pre-cooling, you may also select a new specific temperature or select the number of degress to decrease in temperature.');
insert into CustomerFAQ values(5,1232,'What does the Fan setting do?','The fan setting controls the operation of the fan. <br>Auto - the fan runs only as necessary to maintain the current temperature settings. <br>On - the fan runs continuously. <br>Off - the fan does not run.');
insert into CustomerFAQ values(6,1232,'Does the utility company have access to my thermostat?','The utility only has access to your thermostat for control based on the programs you have signed up for. When not being controlled, you have complete control of your thermostat.');
insert into CustomerFAQ values(7,1233,'How much credit do I receive if I opt out while controlling?','You will receive credit for the portion of time you were controlled.');
alter table CustomerFAQ
   add constraint PK_CUSTOMERFAQ primary key (QuestionID);

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
   Notes                VARCHAR2(300)
);

alter table CustomerResidence
   add constraint PK_CUSTOMERRESIDENCE primary key (AccountSiteID);

/*==============================================================*/
/* Table: ECToAccountMapping                                    */
/*==============================================================*/
create table ECToAccountMapping  (
   EnergyCompanyID      NUMBER                          not null,
   AccountID            NUMBER                          not null
);

alter table ECToAccountMapping
   add constraint PK_ECTOACCOUNTMAPPING primary key (EnergyCompanyID, AccountID);

/*==============================================================*/
/* Table: ECToCallReportMapping                                 */
/*==============================================================*/
create table ECToCallReportMapping  (
   EnergyCompanyID      NUMBER                          not null,
   CallReportID         NUMBER                          not null
);

alter table ECToCallReportMapping
   add constraint PK_ECTOCALLREPORTMAPPING primary key (EnergyCompanyID, CallReportID);

/*==============================================================*/
/* Table: ECToGenericMapping                                    */
/*==============================================================*/
create table ECToGenericMapping  (
   EnergyCompanyID      NUMBER                          not null,
   ItemID               NUMBER                          not null,
   MappingCategory      VARCHAR2(40)                    not null
);

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
   add constraint PK_ECTOGENERICMAPPING primary key (EnergyCompanyID, ItemID, MappingCategory);

/*==============================================================*/
/* Table: ECToInventoryMapping                                  */
/*==============================================================*/
create table ECToInventoryMapping  (
   EnergyCompanyID      NUMBER                          not null,
   InventoryID          NUMBER                          not null
);

alter table ECToInventoryMapping
   add constraint PK_ECTOINVENTORYMAPPING primary key (EnergyCompanyID, InventoryID);

/*==============================================================*/
/* Table: ECToLMCustomerEventMapping                            */
/*==============================================================*/
create table ECToLMCustomerEventMapping  (
   EnergyCompanyID      NUMBER                          not null,
   EventID              NUMBER                          not null
);

alter table ECToLMCustomerEventMapping
   add constraint PK_ECTOLMCUSTOMEREVENTMAPPING primary key (EnergyCompanyID, EventID);

/*==============================================================*/
/* Table: ECToWorkOrderMapping                                  */
/*==============================================================*/
create table ECToWorkOrderMapping  (
   EnergyCompanyID      NUMBER                          not null,
   WorkOrderID          NUMBER                          not null
);

alter table ECToWorkOrderMapping
   add constraint PK_ECTOWORKORDERMAPPING primary key (EnergyCompanyID, WorkOrderID);

/*==============================================================*/
/* Table: InterviewQuestion                                     */
/*==============================================================*/
create table InterviewQuestion  (
   QuestionID           NUMBER                          not null,
   QuestionType         NUMBER,
   Question             VARCHAR2(200),
   Mandatory            VARCHAR2(1),
   DisplayOrder         NUMBER,
   AnswerType           NUMBER,
   ExpectedAnswer       NUMBER
);

alter table InterviewQuestion
   add constraint PK_INTERVIEWQUESTION primary key (QuestionID);

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
   DeviceLabel          VARCHAR2(60)
);

INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0,'(none)');
alter table InventoryBase
   add constraint PK_INVENTORYBASE primary key (InventoryID);

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
/* Table: LMConfigurationBase                                   */
/*==============================================================*/
create table LMConfigurationBase  (
   ConfigurationID      NUMBER                          not null,
   ColdLoadPickup       VARCHAR2(128)                   not null,
   TamperDetect         VARCHAR2(128)                   not null
);

insert into LMConfigurationBase values (0, '(none)', '(none)');
alter table LMConfigurationBase
   add constraint PK_LMCONFIGURATIONBASE primary key (ConfigurationID);

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
   Splinter             VARCHAR2(80)                    not null
);

alter table LMConfigurationExpressCom
   add constraint PK_LMCONFIGURATIONEXPRESSCOM primary key (ConfigurationID);

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
   Slot6                NUMBER                          not null
);

alter table LMConfigurationSA205
   add constraint PK_LMCONFIGURATIONSA205 primary key (ConfigurationID);

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
   RateHierarchy        NUMBER                          not null
);

alter table LMConfigurationSA305
   add constraint PK_LMCONFIGURATIONSA305 primary key (ConfigurationID);

/*==============================================================*/
/* Table: LMConfigurationSASimple                               */
/*==============================================================*/
create table LMConfigurationSASimple  (
   ConfigurationID      NUMBER                          not null,
   OperationalAddress   NUMBER                          not null
);

alter table LMConfigurationSASimple
   add constraint PK_LMCONFIGURATIONSASIMPLE primary key (ConfigurationID);

/*==============================================================*/
/* Table: LMConfigurationVersacom                               */
/*==============================================================*/
create table LMConfigurationVersacom  (
   ConfigurationID      NUMBER                          not null,
   UtilityID            NUMBER                          not null,
   Section              NUMBER                          not null,
   ClassAddress         NUMBER                          not null,
   DivisionAddress      NUMBER                          not null
);

alter table LMConfigurationVersacom
   add constraint PK_LMCONFIGURATIONVERSACOM primary key (ConfigurationID);

/*==============================================================*/
/* Table: LMCustomerEventBase                                   */
/*==============================================================*/
create table LMCustomerEventBase  (
   EventID              NUMBER                          not null,
   EventTypeID          NUMBER                          not null,
   ActionID             NUMBER                          not null,
   EventDateTime        DATE,
   Notes                VARCHAR2(100),
   AuthorizedBy         VARCHAR2(40)
);

alter table LMCustomerEventBase
   add constraint PK_LMCUSTOMEREVENTBASE primary key (EventID);

/*==============================================================*/
/* Table: LMHardwareBase                                        */
/*==============================================================*/
create table LMHardwareBase  (
   InventoryID          NUMBER                          not null,
   ManufacturerSerialNumber VARCHAR2(30),
   LMHardwareTypeID     NUMBER                          not null,
   RouteID              NUMBER                          not null,
   ConfigurationID      NUMBER                          not null
);

alter table LMHardwareBase
   add constraint PK_LMHARDWAREBASE primary key (InventoryID);

/*==============================================================*/
/* Table: LMHardwareConfiguration                               */
/*==============================================================*/
create table LMHardwareConfiguration  (
   InventoryID          NUMBER                          not null,
   ApplianceID          NUMBER                          not null,
   AddressingGroupID    NUMBER,
   LoadNumber           NUMBER
);

alter table LMHardwareConfiguration
   add constraint PK_LMHARDWARECONFIGURATION primary key (InventoryID, ApplianceID);

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
/* Table: LMHardwareEvent                                       */
/*==============================================================*/
create table LMHardwareEvent  (
   EventID              NUMBER                          not null,
   InventoryID          NUMBER                          not null
);

alter table LMHardwareEvent
   add constraint PK_LMHARDWAREEVENT primary key (EventID);

/*==============================================================*/
/* Table: LMProgramEvent                                        */
/*==============================================================*/
create table LMProgramEvent  (
   EventID              NUMBER                          not null,
   AccountID            NUMBER                          not null,
   ProgramID            NUMBER
);

alter table LMProgramEvent
   add constraint PK_LMPROGRAMEVENT primary key (EventID);

/*==============================================================*/
/* Table: LMProgramWebPublishing                                */
/*==============================================================*/
create table LMProgramWebPublishing  (
   ApplianceCategoryID  NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   WebsettingsID        NUMBER,
   ChanceOfControlID    NUMBER,
   ProgramOrder         NUMBER,
   ProgramID            NUMBER                          not null
);

insert into LMProgramWebPublishing values (0,0,0,0,0,0);
alter table LMProgramWebPublishing
   add constraint PK_LMPROGRAMWEBPUBLISHING primary key (ProgramID);

/*==============================================================*/
/* Table: LMThermostatManualEvent                               */
/*==============================================================*/
create table LMThermostatManualEvent  (
   EventID              NUMBER                          not null,
   InventoryID          NUMBER                          not null,
   PreviousTemperature  NUMBER,
   HoldTemperature      VARCHAR2(1),
   OperationStateID     NUMBER,
   FanOperationID       NUMBER
);

alter table LMThermostatManualEvent
   add constraint PK_LMTHERMOSTATMANUALEVENT primary key (EventID);

/*==============================================================*/
/* Table: LMThermostatSchedule                                  */
/*==============================================================*/
create table LMThermostatSchedule  (
   ScheduleID           NUMBER                          not null,
   ScheduleName         VARCHAR2(60)                    not null,
   ThermostatTypeID     NUMBER                          not null,
   AccountID            NUMBER                          not null,
   InventoryID          NUMBER                          not null
);

INSERT INTO LMThermostatSchedule VALUES (-1,'(none)',0,0,0);
alter table LMThermostatSchedule
   add constraint PK_LMTHERMOSTATSCHEDULE primary key (ScheduleID);

/*==============================================================*/
/* Table: LMThermostatSeason                                    */
/*==============================================================*/
create table LMThermostatSeason  (
   SeasonID             NUMBER                          not null,
   ScheduleID           NUMBER,
   WebConfigurationID   NUMBER,
   StartDate            DATE,
   DisplayOrder         NUMBER
);

INSERT INTO LMThermostatSeason VALUES (-1,-1,-1,'01-JUN-00',1);
INSERT INTO LMThermostatSeason VALUES (-2,-1,-2,'15-OCT-00',2);
alter table LMThermostatSeason
   add constraint PK_LMTHERMOSTATSEASON primary key (SeasonID);

/*==============================================================*/
/* Table: LMThermostatSeasonEntry                               */
/*==============================================================*/
create table LMThermostatSeasonEntry  (
   EntryID              NUMBER                          not null,
   SeasonID             NUMBER                          not null,
   TimeOfWeekID         NUMBER                          not null,
   StartTime            NUMBER                          not null,
   Temperature          NUMBER
);

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
   add constraint PK_LMTHERMOSTATSEASONENTRY primary key (EntryID);

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
   HIType               VARCHAR2(40)
);

INSERT INTO ServiceCompany VALUES (0,'(none)',0,'(none)','(none)',0,'(none)');
alter table ServiceCompany
   add constraint PK_SERVICECOMPANY primary key (CompanyID);

/*==============================================================*/
/* Table: SiteInformation                                       */
/*==============================================================*/
create table SiteInformation  (
   SiteID               NUMBER                          not null,
   Feeder               VARCHAR2(20),
   Pole                 VARCHAR2(20),
   TransformerSize      VARCHAR2(20),
   ServiceVoltage       VARCHAR2(20),
   SubstationID         NUMBER
);

INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0);
alter table SiteInformation
   add constraint PK_SITEINFORMATION primary key (SiteID);

/*==============================================================*/
/* Table: Substation                                            */
/*==============================================================*/
create table Substation  (
   SubstationID         NUMBER                          not null,
   SubstationName       VARCHAR2(50),
   RouteID              NUMBER
);

INSERT INTO Substation VALUES (0,'(none)',0);
alter table Substation
   add constraint PK_SUBSTATION primary key (SubstationID);

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
   Description          VARCHAR2(200),
   DateScheduled        DATE,
   DateCompleted        DATE,
   ActionTaken          VARCHAR2(200),
   AccountID            NUMBER
);

alter table WorkOrderBase
   add constraint PK_WORKORDERBASE primary key (OrderID);

alter table AccountSite
   add constraint FK_CUS_CSTS_CUS2 foreign key (SiteInformationID)
      references SiteInformation (SiteID);

alter table AccountSite
   add constraint FK_AccS_CstAd foreign key (StreetAddressID)
      references Address (AddressID);

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

alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst1 foreign key (SecondaryEnergySourceID)
      references YukonListEntry (EntryID);

alter table ApplianceDualFuel
   add constraint FK_AppDuF_YkLst2 foreign key (SwitchOverTypeID)
      references YukonListEntry (EntryID);

alter table ApplianceDualFuel
   add constraint FK_AppDlF_AppB foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

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

alter table CallReportBase
   add constraint FK_CstAc_ClRpB foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table CallReportBase
   add constraint FK_CstELs_ClRB foreign key (CallTypeID)
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

alter table CustomerFAQ
   add constraint FK_CsLsEn_CsF foreign key (SubjectID)
      references YukonListEntry (EntryID);

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

alter table ECToAccountMapping
   add constraint FK_ECTAcc_CstAcc foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table ECToAccountMapping
   add constraint FK_ECTAcc_Enc foreign key (EnergyCompanyID)
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

alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc2 foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

alter table ECToWorkOrderMapping
   add constraint FK_ECTWrk_Enc foreign key (WorkOrderID)
      references WorkOrderBase (OrderID);

alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn foreign key (AnswerType)
      references YukonListEntry (EntryID);

alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn2 foreign key (QuestionType)
      references YukonListEntry (EntryID);

alter table InterviewQuestion
   add constraint FK_IntQ_CsLsEn3 foreign key (ExpectedAnswer)
      references YukonListEntry (EntryID);

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

alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE foreign key (EventTypeID)
      references YukonListEntry (EntryID);

alter table LMCustomerEventBase
   add constraint FK_CsLsE_LCstE_a foreign key (ActionID)
      references YukonListEntry (EntryID);

alter table LMHardwareBase
   add constraint FK_LMHARDWA_REF_CSTLS_YUKONLIS foreign key (LMHardwareTypeID)
      references YukonListEntry (EntryID);

alter table LMHardwareBase
   add constraint FK_LMHrdB_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMHardwareBase
   add constraint FK_LMHrdB_Rt foreign key (RouteID)
      references Route (RouteID);

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

alter table LMProgramEvent
   add constraint FK_CstAc_LMPrEv foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table LMProgramEvent
   add constraint FK_LMPrEv_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID);

alter table LMProgramEvent
   add constraint FK_LmCsEv_LmPrEv foreign key (EventID)
      references LMCustomerEventBase (EventID);

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

alter table LMThermostatSchedule
   add constraint FK_LMThSc_CsAc foreign key (AccountID)
      references CustomerAccount (AccountID);

alter table LMThermostatSchedule
   add constraint FK_LMThSc_InvB foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table LMThermostatSchedule
   add constraint FK_LMThSc_YkLs foreign key (ThermostatTypeID)
      references YukonListEntry (EntryID);

alter table LMThermostatSeason
   add constraint FK_ThSc_LThSs foreign key (ScheduleID)
      references LMThermostatSchedule (ScheduleID);

alter table LMThermostatSeason
   add constraint FK_YkWbC_LThSs foreign key (WebConfigurationID)
      references YukonWebConfiguration (ConfigurationID);

alter table LMThermostatSeasonEntry
   add constraint FK_CsLsE_LThSE foreign key (TimeOfWeekID)
      references YukonListEntry (EntryID);

alter table LMThermostatSeasonEntry
   add constraint FK_LThSe_LThSEn foreign key (SeasonID)
      references LMThermostatSeason (SeasonID);

alter table ServiceCompany
   add constraint FK_CstAdd_SrC foreign key (AddressID)
      references Address (AddressID);

alter table ServiceCompany
   add constraint FK_CstCnt_SrvC foreign key (PrimaryContactID)
      references Contact (ContactID);

alter table SiteInformation
   add constraint FK_Sub_Si foreign key (SubstationID)
      references Substation (SubstationID);

alter table Substation
   add constraint FK_Sub_Rt foreign key (RouteID)
      references Route (RouteID);

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

