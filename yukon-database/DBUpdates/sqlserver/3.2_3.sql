/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* @error ignore */
delete from DynamicLMProgramDirect;
go

/* @error ignore */
alter table DynamicLMProgramDirect alter column NotifyInactiveTime datetime not null;
go

/* @error ignore */
create table CommandGroup (
   CommandGroupID       numeric              not null,
   CommandGroupName     varchar(60)          not null
);
go

/* @error ignore */
insert into CommandGroup values (-1, 'Default Commands');

/* @error ignore */
alter table CommandGroup
   add constraint PK_COMMANDGROUP primary key  (CommandGroupID);
go

/* @error ignore */
alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);
go

/* @error ignore */
insert into YukonRoleProperty values(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
go

/*@error ignore-begin */
/* This was done in the 3.2.1 update script, but the creation script failed reflect these changes, do it again */
/* Steps for adding a new role with some new RoleRoperties and assigning it existing RoleProperties from an old role */
/* 1: insert the new role */
insert into YukonRole values(-900,'Direct Loadcontrol','Load Control','Access and usage of direct loadcontrol system');
go

/* 2: Update all refrences of the old roleID to the new role ID, then delete the old role */
update YukonRoleProperty set roleid = -900 where roleid = -203;
update yukongrouprole set roleid = -900 where roleid = -203;
update yukonuserrole set roleid = -900 where roleid = -203;
delete from YukonRole where roleid = -203;
go

/* 3: Add all RolePoperties to the new role, including the RoleProperties we are moving */
insert into YukonRoleProperty values(-90000,-900,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
insert into YukonRoleProperty values(-90001,-900,'Individual Switch','true','Controls access to operator individual switch control');
insert into YukonRoleProperty values(-90002,-900,'3 Tier Direct Control','false','Allows access to the 3-tier load management web interface');
insert into YukonRoleProperty values(-90003,-900,'Direct Loadcontrol','true','Allows access to the Direct load management web interface');
insert into YukonRoleProperty values(-90004,-900,'Constraint Check','true','Allow load management program constraints to be CHECKED before starting');
insert into YukonRoleProperty values(-90005,-900,'Constraint Observe','true','Allow load management program constraints to be OBSERVED before starting');
insert into YukonRoleProperty values(-90006,-900,'Constraint Override','true','Allow load management program constraints to be OVERRIDDEN before starting');
insert into YukonRoleProperty values(-90007,-900,'Constraint Default','Check','The default program constraint selection prior to starting a program');
go

/* 4: Update the old RoleProperties ID to the duplicate RoleProperties we have just made */
update yukongrouprole set rolepropertyid = -90000 where rolepropertyid = -20300;
update yukongrouprole set rolepropertyid = -90001 where rolepropertyid = -20301;
update yukongrouprole set rolepropertyid = -90002 where rolepropertyid = -20302;
update yukongrouprole set rolepropertyid = -90003 where rolepropertyid = -20303;
go

update yukonuserrole set rolepropertyid = -90000 where rolepropertyid = -20300;
update yukonuserrole set rolepropertyid = -90001 where rolepropertyid = -20301;
update yukonuserrole set rolepropertyid = -90002 where rolepropertyid = -20302;
update yukonuserrole set rolepropertyid = -90003 where rolepropertyid = -20303;
go

/* 5: Delete the old RolePropertie */
delete from YukonRoleProperty where rolepropertyid = -20300;
delete from YukonRoleProperty where rolepropertyid = -20301;
delete from YukonRoleProperty where rolepropertyid = -20302;
delete from YukonRoleProperty where rolepropertyid = -20303;
go

/* 6: Add only the new RoleProperties to the UserRole and GroupRole mapping */
insert into yukongrouprole values (-779,-301,-900,-90004,'(none)');
insert into yukongrouprole values (-781,-301,-900,-90005,'(none)');
insert into yukongrouprole values (-782,-301,-900,-90006,'(none)');
insert into yukongrouprole values (-783,-301,-900,-90007,'(none)');
insert into YukonGroupRole values (-1279,-2,-900,-90004,'(none)');
insert into YukonGroupRole values (-1281,-2,-900,-90005,'(none)');
insert into YukonGroupRole values (-1282,-2,-900,-90006,'(none)');
insert into YukonGroupRole values (-1283,-2,-900,-90007,'(none)');
go

insert into YukonUserRole values (-779,-1,-900,-90004,'(none)');
insert into YukonUserRole values (-781,-1,-900,-90005,'(none)');
insert into YukonUserRole values (-782,-1,-900,-90006,'(none)');
insert into YukonUserRole values (-783,-1,-900,-90007,'(none)');
go
/*@error ignore-end */

/* @error ignore-begin */
insert into YukonRoleProperty values(-1110,-2,'Default Temperature Unit','F','Default temperature unit for an energy company, F(ahrenheit) or C(elsius)');
go

alter table Customer add TemperatureUnit char(1);
go
update Customer set TemperatureUnit = 'F';
go
alter table Customer alter column TemperatureUnit char(1) not null;
go
/* @error ignore-end */

alter table CapControlSubstationBus add AltSubID numeric;
go
update CapControlSubstationBus set AltSubID = SubstationBusID;
go
alter table CapControlSubstationBus alter column AltSubID numeric not null;
go
 
alter table CapControlSubstationBus add SwitchPointID numeric;
go
update CapControlSubstationBus set SwitchPointID = 0;
go
alter table CapControlSubstationBus alter column SwitchPointID numeric not null;
go
alter table CapControlSubstationBus 
   add constraint FK_CAPCONTR_SWPTID foreign key (SwitchPointID)
      references Point (PointID);
go
 
alter table CapControlSubstationBus add DualBusEnabled char(1);
go
update CapControlSubstationBus set DualBusEnabled = 'N';
go
alter table CapControlSubstationBus alter column DualBusEnabled char(1) not null;
go

alter table dynamicCCSubstationBus add switchPointStatus char(1);
go
update dynamicCCSubstationBus set switchPointStatus  = 'N';
go
alter table dynamicCCSubstationBus alter column switchPointStatus char(1) not null;
go

alter table dynamicCCSubstationBus add altSubControlValue numeric;
go
update dynamicCCSubstationBus set altSubControlValue = 0;
go
alter table dynamicCCSubstationBus alter column altSubControlValue numeric not null;
go

/* @error ignore-begin */
insert into YukonListEntry values (134, 100, 0, 'True,False,Condition', 0); 
insert into YukonListEntry values (135, 100, 0, 'Regression', 0); 
insert into YukonListEntry values (136, 100, 0, 'Binary Encode', 0);
go
/* @error ignore-end */

alter table CALCBASE add QualityFlag char(1);
go
update CALCBASE set QualityFlag = 'N';
go
alter table CALCBASE alter column QualityFlag char(1) not null;
go

/* @error ignore */
alter table DynamicPointAlarming drop constraint FKf_DynPtAl_SysL;
go

alter table dynamicccfeeder add currVerifyCBId numeric;
go
update dynamicccfeeder  set currVerifyCBId = -1;
go
alter table dynamicccfeeder alter column currVerifyCBId numeric not null;
go

alter table dynamicccfeeder add currVerifyCBOrigState numeric;
go
update dynamicccfeeder  set currVerifyCBOrigState = 0;
go
alter table dynamicccfeeder alter column currVerifyCBOrigState numeric not null;
go

/*==============================================================*/
/* Table: CCMONITORBANKLIST                                     */
/*==============================================================*/
create table CCMONITORBANKLIST (
   BankID               numeric              not null,
   PointID              numeric              not null,
   DisplayOrder         numeric              not null,
   Scannable            char(1)              not null,
   NINAvg               numeric              not null,
   UpperBandwidth        float                not null,
   LowerBandwidth        float                not null
);
go


alter table CCMONITORBANKLIST
   add constraint PK_CCMONITORBANKLIST primary key  (BankID, PointID);
go


alter table CCMONITORBANKLIST
   add constraint FK_CCMONBNKLST_PTID foreign key (PointID)
      references POINT (POINTID);
go


alter table CCMONITORBANKLIST
   add constraint FK_CCMONBNKLIST_BNKID foreign key (BankID)
      references CAPBANK (DEVICEID);
go

/*==============================================================*/
/* Table: DynamicCCMonitorBankHistory                           */
/*==============================================================*/
create table DynamicCCMonitorBankHistory (
   BankID               numeric              not null,
   PointID              numeric              not null,
   Value                float                not null,
   DateTime             datetime             not null,
   ScanInProgress       char(1)              not null
);
go


alter table DynamicCCMonitorBankHistory
   add constraint PK_DYNAMICCCMONITORBANKHISTORY primary key  (BankID, PointID);
go


alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_PTID foreign key (PointID)
      references POINT (POINTID);
go


alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_BNKID foreign key (BankID)
      references CAPBANK (DEVICEID);
go

/*==============================================================*/
/* Table: DynamicCCMonitorPointResponse                         */
/*==============================================================*/
create table DynamicCCMonitorPointResponse (
   BankID               numeric              not null,
   PointID              numeric              not null,
   PreOpValue           float                not null,
   Delta                numeric              not null
);
go


alter table DynamicCCMonitorPointResponse
   add constraint PK_DYNAMICCCMONITORPOINTRESPON primary key  (BankID, PointID);
go


alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_BNKID foreign key (BankID)
      references DynamicCCCapBank (CapBankID);
go


alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_PTID foreign key (PointID)
      references POINT (POINTID);
go

alter table Capcontrolsubstationbus add multiMonitorControl char(1);
go
update  Capcontrolsubstationbus set  multiMonitorControl = 'N';
go
alter table   Capcontrolsubstationbus alter column  multiMonitorControl char(1) not null;
go

alter table Capcontrolfeeder add multiMonitorControl char(1);
go
update  Capcontrolfeeder set  multiMonitorControl = 'N';
go
alter table   Capcontrolfeeder alter column  multiMonitorControl char(1) not null;
go

alter table NotificationDestination drop constraint PKey_NotDestID;
go
alter table NotificationDestination drop column DestinationOrder;
go
alter table NotificationDestination add constraint PKey_NotDestID primary key (NotificationGroupID, RecipientID);
go

alter table dynamicccsubstationbus add eventSeq numeric;
go
update dynamicccsubstationbus  set eventSeq = 0;
go
alter table dynamicccsubstationbus alter column eventSeq numeric not null;
go

alter table dynamicccfeeder add eventSeq numeric;
go
update dynamicccfeeder  set eventSeq = 0;
go
alter table dynamicccfeeder alter column eventSeq numeric not null;
go

/* @error ignore-begin */
/* Below is an attempt to create every table and constraint
   that should exist in the database as of 3.2.3.
   If the target database is correct ALL of
   these statements should fail.  Don't sweat the results of this
   section.
*/

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
   Description          varchar(120)         not null
);
go


alter table ActivityLog
   add constraint PK_ACTIVITYLOG primary key  (ActivityLogID);
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
   County               varchar(30)          not null
);
go


alter table Address
   add constraint PK_ADDRESS primary key  (AddressID);
go


/*==============================================================*/
/* Table: AlarmCategory                                         */
/*==============================================================*/
create table AlarmCategory (
   AlarmCategoryID      numeric              not null,
   CategoryName         varchar(40)          not null,
   NotificationGroupID  numeric              not null
);
go


alter table AlarmCategory
   add constraint PK_ALARMCATEGORYID primary key  (AlarmCategoryID);
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
   HolidaysUsed         numeric              not null
);
go


alter table BaseLine
   add constraint PK_BASELINE primary key  (BaselineID);
go


/*==============================================================*/
/* Table: BillingFileFormats                                    */
/*==============================================================*/
create table BillingFileFormats (
   FormatID             numeric              not null,
   FormatType           varchar(30)          not null
);
go


alter table BillingFileFormats
   add constraint PK_BILLINGFILEFORMATS primary key  (FormatID);
go


/*==============================================================*/
/* Table: CALCBASE                                              */
/*==============================================================*/
create table CALCBASE (
   POINTID              numeric              not null,
   UPDATETYPE           varchar(16)          not null,
   PERIODICRATE         numeric              not null
);
go


alter table CALCBASE
   add constraint PK_CALCBASE primary key  (POINTID);
go


/*==============================================================*/
/* Index: Indx_ClcBaseUpdTyp                                    */
/*==============================================================*/
create   index Indx_ClcBaseUpdTyp on CALCBASE (
UPDATETYPE
);
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
   FUNCTIONNAME         varchar(20)          null
);
go


alter table CALCCOMPONENT
   add constraint PK_CALCCOMPONENT primary key  (PointID, COMPONENTORDER);
go


/*==============================================================*/
/* Index: Indx_CalcCmpCmpType                                   */
/*==============================================================*/
create   index Indx_CalcCmpCmpType on CALCCOMPONENT (
COMPONENTTYPE
);
go


/*==============================================================*/
/* Table: CAPBANK                                               */
/*==============================================================*/
create table CAPBANK (
   DEVICEID             numeric              not null,
   OPERATIONALSTATE     varchar(16)          not null,
   ControllerType       varchar(20)          not null,
   CONTROLDEVICEID      numeric              not null,
   CONTROLPOINTID       numeric              not null,
   BANKSIZE             numeric              not null,
   TypeOfSwitch         varchar(20)          not null,
   SwitchManufacture    varchar(20)          not null,
   MapLocationID        varchar(64)          not null,
   RecloseDelay         numeric              not null,
   MaxDailyOps          numeric              not null,
   MaxOpDisable         char(1)              not null
);
go


alter table CAPBANK
   add constraint PK_CAPBANK primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: CAPCONTROLSUBSTATIONBUS                               */
/*==============================================================*/
create table CAPCONTROLSUBSTATIONBUS (
   SubstationBusID      numeric              not null,
   CurrentVarLoadPointID numeric              not null,
   CurrentWattLoadPointID numeric              not null,
   MapLocationID        varchar(64)          not null,
   StrategyID           numeric              not null,
   CurrentVoltLoadPointID numeric            not null,
   AltSubID             numeric              not null,
   SwitchPointID        numeric              not null,
   DualBusEnabled       char(1)              not null,
   MultiMonitorControl  char(1)              not null
);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013476 primary key  (SubstationBusID);
go


/*==============================================================*/
/* Index: Indx_CSUBVPT                                          */
/*==============================================================*/
create   index Indx_CSUBVPT on CAPCONTROLSUBSTATIONBUS (
CurrentVarLoadPointID
);
go


/*==============================================================*/
/* Table: CCFeederBankList                                      */
/*==============================================================*/
create table CCFeederBankList (
   FeederID             numeric              not null,
   DeviceID             numeric              not null,
   ControlOrder         numeric              not null
);
go


alter table CCFeederBankList
   add constraint PK_CCFEEDERBANKLIST primary key  (FeederID, DeviceID);
go


/*==============================================================*/
/* Table: CCFeederSubAssignment                                 */
/*==============================================================*/
create table CCFeederSubAssignment (
   SubStationBusID      numeric              not null,
   FeederID             numeric              not null,
   DisplayOrder         numeric              not null
);
go


alter table CCFeederSubAssignment
   add constraint PK_CCFEEDERSUBASSIGNMENT primary key  (SubStationBusID, FeederID);
go


/*==============================================================*/
/* Table: CICUSTOMERPOINTDATA                                   */
/*==============================================================*/
create table CICUSTOMERPOINTDATA (
   CustomerID           numeric              not null,
   PointID              numeric              not null,
   Type                 varchar(16)          not null,
   OptionalLabel        varchar(32)          not null
);
go


alter table CICUSTOMERPOINTDATA
   add constraint PK_CICUSTOMERPOINTDATA primary key  (CustomerID, PointID);
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
   CompanyName          varchar(80)          not null
);
go


alter table CICustomerBase
   add constraint PK_CICUSTOMERBASE primary key  (CustomerID);
go


/*==============================================================*/
/* Table: COLUMNTYPE                                            */
/*==============================================================*/
create table COLUMNTYPE (
   TYPENUM              numeric              not null,
   NAME                 varchar(20)          not null
);
go


alter table COLUMNTYPE
   add constraint SYS_C0013414 primary key  (TYPENUM);
go


/*==============================================================*/
/* Table: CTIDatabase                                           */
/*==============================================================*/
create table CTIDatabase (
   Version              varchar(6)           not null,
   CTIEmployeeName      varchar(30)          not null,
   DateApplied          datetime             null,
   Notes                varchar(300)         null,
   Build                numeric              not null
);
go


alter table CTIDatabase
   add constraint PK_CTIDATABASE primary key  (Version, Build);
go


/*==============================================================*/
/* Table: CalcPointBaseline                                     */
/*==============================================================*/
create table CalcPointBaseline (
   PointID              numeric              not null,
   BaselineID           numeric              not null
);
go


alter table CalcPointBaseline
   add constraint PK_CalcBsPt primary key  (PointID);
go


/*==============================================================*/
/* Table: CapControlFeeder                                      */
/*==============================================================*/
create table CapControlFeeder (
   FeederID             numeric              not null,
   CurrentVarLoadPointID numeric              not null,
   CurrentWattLoadPointID numeric              not null,
   MapLocationID        varchar(64)          not null,
   StrategyID           numeric              not null,
   CurrentVoltLoadPointID numeric            not null,
   MultiMonitorControl char(1)               not null
);
go


alter table CapControlFeeder
   add constraint PK_CAPCONTROLFEEDER primary key  (FeederID);
go


/*==============================================================*/
/* Index: Indx_CPCNFDVARPT                                      */
/*==============================================================*/
create   index Indx_CPCNFDVARPT on CapControlFeeder (
CurrentVarLoadPointID
);
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
   PeakLag              float                not null,
   PeakLead             float                not null,
   OffPkLag             float                not null,
   OffPkLead            float                not null
);
go


alter table CapControlStrategy
   add constraint PK_CAPCONTROLSTRAT primary key  (StrategyID);
go


/*==============================================================*/
/* Index: Indx_CapCntrlStrat_name_UNQ                           */
/*==============================================================*/
create unique  index Indx_CapCntrlStrat_name_UNQ on CapControlStrategy (
StrategyName
);
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
   ResetRptSettings     char(1)              not null
);
go


alter table CarrierRoute
   add constraint PK_CARRIERROUTE primary key  (ROUTEID);
go


/*==============================================================*/
/* Table: CommErrorHistory                                      */
/*==============================================================*/
create table CommErrorHistory (
   CommErrorID          numeric              not null,
   PAObjectID           numeric              not null,
   DateTime             datetime             not null,
   SOE_Tag              numeric              not null,
   ErrorType            numeric              not null,
   ErrorNumber          numeric              not null,
   Command              varchar(50)          not null,
   OutMessage           varchar(160)         not null,
   InMessage            varchar(160)         not null
);
go


alter table CommErrorHistory
   add constraint PK_COMMERRORHISTORY primary key  (CommErrorID);
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
   SharedSocketNumber   numeric              not null
);
go


alter table CommPort
   add constraint SYS_C0013112 primary key  (PORTID);
go


/*==============================================================*/
/* Table: Command                                               */
/*==============================================================*/
create table Command (
   CommandID            numeric              not null,
   Command              varchar(256)         not null,
   Label                varchar(256)         not null,
   Category             varchar(32)          not null
);
go

alter table Command
   add constraint PK_COMMAND primary key  (CommandID);
go


/*==============================================================*/
/* Table: CommandGroup                                          */
/*==============================================================*/
create table CommandGroup (
   CommandGroupID       numeric              not null,
   CommandGroupName     varchar(60)          not null
);
go


alter table CommandGroup
   add constraint PK_COMMANDGROUP primary key  (CommandGroupID);
go


/*==============================================================*/
/* Index: AK_KEY_CmdGrp_Name                                    */
/*==============================================================*/
create unique  index AK_KEY_CmdGrp_Name on CommandGroup (
CommandGroupName
);
go


/*==============================================================*/
/* Table: ConfigurationName                                     */
/*==============================================================*/
create table ConfigurationName (
   ConfigID             numeric              not null,
   ConfigName           varchar(40)          not null
);
go


alter table ConfigurationName
   add constraint PK_CONFIGURATIONNAME primary key  (ConfigID);
go


/*==============================================================*/
/* Index: AK_CONFNM_NAME                                        */
/*==============================================================*/
create unique  index AK_CONFNM_NAME on ConfigurationName (
ConfigName
);
go


/*==============================================================*/
/* Table: ConfigurationParts                                    */
/*==============================================================*/
create table ConfigurationParts (
   ConfigID             numeric              not null,
   PartID               numeric              not null,
   ConfigRowID          numeric              not null
);
go


alter table ConfigurationParts
   add constraint PK_CONFIGURATIONPARTS primary key  (ConfigRowID);
go


/*==============================================================*/
/* Table: ConfigurationPartsName                                */
/*==============================================================*/
create table ConfigurationPartsName (
   PartID               numeric              not null,
   PartName             varchar(40)          not null,
   PartType             varchar(40)          not null
);
go


alter table ConfigurationPartsName
   add constraint PK_CONFIGURATIONPARTSNAME primary key  (PartID);
go


/*==============================================================*/
/* Index: AK_CONFPTNM_NAME                                      */
/*==============================================================*/
create unique  index AK_CONFPTNM_NAME on ConfigurationPartsName (
PartName
);
go


/*==============================================================*/
/* Table: ConfigurationValue                                    */
/*==============================================================*/
create table ConfigurationValue (
   PartID               numeric              not null,
   ValueID              varchar(40)          not null,
   Value                varchar(40)          not null,
   ConfigRowID          numeric              not null
);
go


alter table ConfigurationValue
   add constraint PK_CONFIGURATIONVALUE primary key  (ConfigRowID);
go


/*==============================================================*/
/* Table: Contact                                               */
/*==============================================================*/
create table Contact (
   ContactID            numeric              not null,
   ContFirstName        varchar(20)          not null,
   ContLastName         varchar(32)          not null,
   LogInID              numeric              not null,
   AddressID            numeric              not null
);
go


alter table Contact
   add constraint PK_CONTACT primary key  (ContactID);
go


/*==============================================================*/
/* Index: Indx_ContLstName                                      */
/*==============================================================*/
create   index Indx_ContLstName on Contact (
ContLastName
);
go


/*==============================================================*/
/* Table: ContactNotifGroupMap                                  */
/*==============================================================*/
create table ContactNotifGroupMap (
   ContactID            numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null
);
go


alter table ContactNotifGroupMap
   add constraint PK_CONTACTNOTIFGROUPMAP primary key  (ContactID, NotificationGroupID);
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
   Ordering             numeric              not null
);
go



alter table ContactNotification
   add constraint PK_CONTACTNOTIFICATION primary key  (ContactNotifID);
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
   TemperatureUnit      char(1)              not null
);
go


alter table Customer
   add constraint PK_CUSTOMER primary key  (CustomerID);
go


/*==============================================================*/
/* Table: CustomerAdditionalContact                             */
/*==============================================================*/
create table CustomerAdditionalContact (
   CustomerID           numeric              not null,
   ContactID            numeric              not null,
   Ordering             numeric              not null
);
go


alter table CustomerAdditionalContact
   add constraint PK_CUSTOMERADDITIONALCONTACT primary key  (ContactID, CustomerID);
go


/*==============================================================*/
/* Table: CustomerBaseLinePoint                                 */
/*==============================================================*/
create table CustomerBaseLinePoint (
   CustomerID           numeric              not null,
   PointID              numeric              not null
);
go


alter table CustomerBaseLinePoint
   add constraint PK_CUSTOMERBASELINEPOINT primary key  (CustomerID, PointID);
go


/*==============================================================*/
/* Table: CustomerLoginSerialGroup                              */
/*==============================================================*/
create table CustomerLoginSerialGroup (
   LoginID              numeric              not null,
   LMGroupID            numeric              not null
);
go


alter table CustomerLoginSerialGroup
   add constraint PK_CUSTOMERLOGINSERIALGROUP primary key  (LoginID, LMGroupID);
go


/*==============================================================*/
/* Table: CustomerNotifGroupMap                                 */
/*==============================================================*/
create table CustomerNotifGroupMap (
   CustomerID           numeric              not null,
   NotificationGroupID  numeric              not null,
   Attribs              char(16)             not null
);
go


alter table CustomerNotifGroupMap
   add constraint PK_CUSTOMERNOTIFGROUPMAP primary key  (CustomerID, NotificationGroupID);
go


/*==============================================================*/
/* Table: DEVICE                                                */
/*==============================================================*/
create table DEVICE (
   DEVICEID             numeric              not null,
   ALARMINHIBIT         varchar(1)           not null,
   CONTROLINHIBIT       varchar(1)           not null
);
go


alter table DEVICE
   add constraint PK_DEV_DEVICEID2 primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DEVICE2WAYFLAGS                                       */
/*==============================================================*/
create table DEVICE2WAYFLAGS (
   DEVICEID             numeric              not null,
   MONTHLYSTATS         varchar(1)           not null,
   TWENTYFOURHOURSTATS  varchar(1)           not null,
   HOURLYSTATS          varchar(1)           not null,
   FAILUREALARM         varchar(1)           not null,
   PERFORMANCETHRESHOLD numeric              not null,
   PERFORMANCEALARM     varchar(1)           not null,
   PERFORMANCETWENTYFOURALARM varchar(1)           not null
);
go


alter table DEVICE2WAYFLAGS
   add constraint PK_DEVICE2WAYFLAGS primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DEVICECARRIERSETTINGS                                 */
/*==============================================================*/
create table DEVICECARRIERSETTINGS (
   DEVICEID             numeric              not null,
   ADDRESS              numeric              not null
);
go


alter table DEVICECARRIERSETTINGS
   add constraint PK_DEVICECARRIERSETTINGS primary key  (DEVICEID);
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
   BaudRate             numeric              not null
);
go


alter table DEVICEDIALUPSETTINGS
   add constraint PK_DEVICEDIALUPSETTINGS primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DEVICEIDLCREMOTE                                      */
/*==============================================================*/
create table DEVICEIDLCREMOTE (
   DEVICEID             numeric              not null,
   ADDRESS              numeric              not null,
   POSTCOMMWAIT         numeric              not null,
   CCUAmpUseType        varchar(20)          not null
);
go


alter table DEVICEIDLCREMOTE
   add constraint PK_DEVICEIDLCREMOTE primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DEVICEIED                                             */
/*==============================================================*/
create table DEVICEIED (
   DEVICEID             numeric              not null,
   PASSWORD             varchar(20)          not null,
   SLAVEADDRESS         varchar(20)          not null
);
go


alter table DEVICEIED
   add constraint PK_DEVICEIED primary key  (DEVICEID);
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
   VoltageDmdRate       numeric              not null
);
go


alter table DEVICELOADPROFILE
   add constraint PK_DEVICELOADPROFILE primary key  (DEVICEID);
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
   REALTIMESCAN         varchar(1)           not null
);
go


alter table DEVICEMCTIEDPORT
   add constraint PK_DEVICEMCTIEDPORT primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DEVICEMETERGROUP                                      */
/*==============================================================*/
create table DEVICEMETERGROUP (
   DEVICEID             numeric              not null,
   CollectionGroup      varchar(20)          not null,
   TestCollectionGroup  varchar(20)          not null,
   METERNUMBER          varchar(15)          not null,
   BillingGroup         varchar(20)          not null
);
go


alter table DEVICEMETERGROUP
   add constraint PK_DEVICEMETERGROUP primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DEVICESCANRATE                                        */
/*==============================================================*/
create table DEVICESCANRATE (
   DEVICEID             numeric              not null,
   SCANTYPE             varchar(20)          not null,
   INTERVALRATE         numeric              not null,
   SCANGROUP            numeric              not null,
   AlternateRate        numeric              not null
);
go


alter table DEVICESCANRATE
   add constraint PK_DEVICESCANRATE primary key  (DEVICEID, SCANTYPE);
go


/*==============================================================*/
/* Table: DEVICETAPPAGINGSETTINGS                               */
/*==============================================================*/
create table DEVICETAPPAGINGSETTINGS (
   DEVICEID             numeric              not null,
   PAGERNUMBER          varchar(20)          not null,
   Sender               varchar(64)          not null,
   SecurityCode         varchar(64)          not null,
   POSTPath             varchar(64)          not null
);
go


alter table DEVICETAPPAGINGSETTINGS
   add constraint PK_DEVICETAPPAGINGSETTINGS primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DISPLAY                                               */
/*==============================================================*/
create table DISPLAY (
   DISPLAYNUM           numeric              not null,
   NAME                 varchar(40)          not null,
   TYPE                 varchar(40)          not null,
   TITLE                varchar(30)          null,
   DESCRIPTION          varchar(200)         null
);
go








alter table DISPLAY
   add constraint SYS_C0013412 primary key  (DISPLAYNUM);
go


/*==============================================================*/
/* Index: Indx_DISPLAYNAME                                      */
/*==============================================================*/
create unique  index Indx_DISPLAYNAME on DISPLAY (
NAME
);
go


/*==============================================================*/
/* Table: DISPLAY2WAYDATA                                       */
/*==============================================================*/
create table DISPLAY2WAYDATA (
   DISPLAYNUM           numeric              not null,
   ORDERING             numeric              not null,
   POINTID              numeric              not null
);
go


alter table DISPLAY2WAYDATA
   add constraint PK_DISPLAY2WAYDATA primary key  (DISPLAYNUM, ORDERING);
go


/*==============================================================*/
/* Table: DISPLAYCOLUMNS                                        */
/*==============================================================*/
create table DISPLAYCOLUMNS (
   DISPLAYNUM           numeric              not null,
   TITLE                varchar(50)          not null,
   TYPENUM              numeric              not null,
   ORDERING             numeric              not null,
   WIDTH                numeric              not null
);
go

alter table DISPLAYCOLUMNS
   add constraint PK_DISPLAYCOLUMNS primary key  (DISPLAYNUM, TITLE);
go


/*==============================================================*/
/* Table: DYNAMICACCUMULATOR                                    */
/*==============================================================*/
create table DYNAMICACCUMULATOR (
   POINTID              numeric              not null,
   PREVIOUSPULSES       numeric              not null,
   PRESENTPULSES        numeric              not null
);
go


alter table DYNAMICACCUMULATOR
   add constraint PK_DYNAMICACCUMULATOR primary key  (POINTID);
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
   NEXTSCAN3            datetime             not null
);
go


alter table DYNAMICDEVICESCANDATA
   add constraint PK_DYNAMICDEVICESCANDATA primary key  (DEVICEID);
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
   millis               smallint             not null
);
go


alter table DYNAMICPOINTDISPATCH
   add constraint PK_DYNAMICPOINTDISPATCH primary key  (POINTID);
go


/*==============================================================*/
/* Table: DateOfHoliday                                         */
/*==============================================================*/
create table DateOfHoliday (
   HolidayScheduleID    numeric              not null,
   HolidayName          varchar(20)          not null,
   HolidayMonth         numeric              not null,
   HolidayDay           numeric              not null,
   HolidayYear          numeric              not null
);
go


alter table DateOfHoliday
   add constraint PK_DATEOFHOLIDAY primary key  (HolidayScheduleID, HolidayName);
go


/*==============================================================*/
/* Table: DateOfSeason                                          */
/*==============================================================*/
create table DateOfSeason (
   SeasonScheduleID     numeric              not null,
   SeasonName           varchar(20)          not null,
   SeasonStartMonth     numeric              not null,
   SeasonStartDay       numeric              not null,
   SeasonEndMonth       numeric              not null,
   SeasonEndDay         numeric              not null
);
go


alter table DateOfSeason
   add constraint PK_DATEOFSEASON primary key  (SeasonScheduleID, SeasonName);
go


/*==============================================================*/
/* Table: DeviceAddress                                         */
/*==============================================================*/
create table DeviceAddress (
   DeviceID             numeric              not null,
   MasterAddress        numeric              not null,
   SlaveAddress         numeric              not null,
   PostCommWait         numeric              not null
);
go


alter table DeviceAddress
   add constraint PK_DEVICEADDRESS primary key  (DeviceID);
go


/*==============================================================*/
/* Table: DeviceCBC                                             */
/*==============================================================*/
create table DeviceCBC (
   DEVICEID             numeric              not null,
   SERIALNUMBER         numeric              not null,
   ROUTEID              numeric              not null
);
go


alter table DeviceCBC
   add constraint PK_DEVICECBC primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DeviceConfiguration                                   */
/*==============================================================*/
create table DeviceConfiguration (
   DeviceID             numeric              not null,
   ConfigID             numeric              not null
);
go


alter table DeviceConfiguration
   add constraint PK_DEVICECONFIGURATION primary key  (DeviceID);
go


/*==============================================================*/
/* Table: DeviceCustomerList                                    */
/*==============================================================*/
create table DeviceCustomerList (
   CustomerID           numeric              not null,
   DeviceID             numeric              not null
);
go


alter table DeviceCustomerList
   add constraint PK_DEVICECUSTOMERLIST primary key  (DeviceID, CustomerID);
go


/*==============================================================*/
/* Table: DeviceDirectCommSettings                              */
/*==============================================================*/
create table DeviceDirectCommSettings (
   DEVICEID             numeric              not null,
   PORTID               numeric              not null
);
go


alter table DeviceDirectCommSettings
   add constraint PK_DEVICEDIRECTCOMMSETTINGS primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: DeviceMCT400Series                                    */
/*==============================================================*/
create table DeviceMCT400Series (
   DeviceID             numeric              not null,
   DisconnectAddress    numeric              not null,
   TOUScheduleID        numeric              not null
);
go


alter table DeviceMCT400Series
   add constraint PK_DEV400S primary key  (DeviceID);
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
   Frequency            float                not null
);
go


alter table DevicePagingReceiverSettings
   add constraint PK_DEVICEPAGINGRECEIVERSETTING primary key  (DeviceID);
go


/*==============================================================*/
/* Table: DeviceRTC                                             */
/*==============================================================*/
create table DeviceRTC (
   DeviceID             numeric              not null,
   RTCAddress           numeric              not null,
   Response             varchar(1)           not null,
   LBTMode              numeric              not null,
   DisableVerifies      varchar(1)           not null
);
go


alter table DeviceRTC
   add constraint PK_DEVICERTC primary key  (DeviceID);
go


/*==============================================================*/
/* Table: DeviceRoutes                                          */
/*==============================================================*/
create table DeviceRoutes (
   DEVICEID             numeric              not null,
   ROUTEID              numeric              not null
);
go


alter table DeviceRoutes
   add constraint PK_DEVICEROUTES primary key  (DEVICEID, ROUTEID);
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
   Retries              numeric              not null
);
go


alter table DeviceSeries5RTU
   add constraint PK_DEVICESERIES5RTU primary key  (DeviceID);
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
   PagerID              numeric              not null
);
go


alter table DeviceTNPPSettings
   add constraint PK_DEVICETNPPSETTINGS primary key  (DeviceID);
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
   CommandGroupID       numeric              not null
);
go








alter table DeviceTypeCommand
   add constraint PK_DEVICETYPECOMMAND primary key  (DeviceCommandID, CommandGroupID);
go


/*==============================================================*/
/* Index: Indx_DevTypeCmd_GroupID                               */
/*==============================================================*/
create   index Indx_DevTypeCmd_GroupID on DeviceTypeCommand (
CommandGroupID
);
go


/*==============================================================*/
/* Table: DeviceVerification                                    */
/*==============================================================*/
create table DeviceVerification (
   ReceiverID           numeric              not null,
   TransmitterID        numeric              not null,
   ResendOnFail         char(1)              not null,
   Disable              char(1)              not null
);
go


alter table DeviceVerification
   add constraint PK_DEVICEVERIFICATION primary key  (ReceiverID, TransmitterID);
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
   AlternateClose       numeric              not null
);
go


alter table DeviceWindow
   add constraint PK_DEVICEWINDOW primary key  (DeviceID, Type);
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
   OriginalFeederID     numeric              not null,
   OriginalSwitchingOrder numeric              not null,
   AssumedStartVerificationStatus numeric              not null,
   PrevVerificationControlStatus numeric              not null,
   VerificationControlIndex numeric              not null,
   AdditionalFlags      varchar(32)          not null,
   CurrentDailyOperations numeric              not null
);
go


alter table DynamicCCCapBank
   add constraint PK_DYNAMICCCCAPBANK primary key  (CapBankID);
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
   CurrentVoltPointValue float               not null,
   EventSeq             numeric              not null,
   CurrVerifyCBId       numeric              not null,
   CurrVerifyCBOrigState numeric             not null
);
go


alter table DynamicCCFeeder
   add constraint PK_DYNAMICCCFEEDER primary key  (FeederID);
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
   CurrentVoltPointValue float               not null,
   SwitchPointStatus    char(1)              not null,
   AltSubControlValue   float                not null,
   EventSeq             numeric              not null
);
go


alter table DynamicCCSubstationBus
   add constraint PK_DYNAMICCCSUBSTATIONBUS primary key  (SubstationBusID);
go


/*==============================================================*/
/* Table: DynamicCalcHistorical                                 */
/*==============================================================*/
create table DynamicCalcHistorical (
   PointID              numeric              not null,
   LastUpdate           datetime             not null
);
go


alter table DynamicCalcHistorical
   add constraint PK_DYNAMICCALCHISTORICAL primary key  (PointID);
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
   ForceImport          char(1)              not null
);
go


alter table DynamicImportStatus
   add constraint PK_DYNAMICIMPORTSTATUS primary key  (Entry);
go


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
   CurrentDailyStopTime numeric              not null
);
go


alter table DynamicLMControlArea
   add constraint PK_DYNAMICLMCONTROLAREA primary key  (DeviceID);
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
   TriggerID            numeric              not null
);
go


alter table DynamicLMControlAreaTrigger
   add constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key  (DeviceID, TriggerNumber);
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
   StopDateTime         datetime             not null
);
go


alter table DynamicLMControlHistory
   add constraint PK_DYNLMCONTROLHISTORY primary key  (PAObjectID);
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
   dailyops             smallint             not null
);
go


alter table DynamicLMGroup
   add constraint PK_DYNAMICLMGROUP primary key  (DeviceID);
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
   TimeStamp            datetime             not null
);
go


alter table DynamicLMProgram
   add constraint PK_DYNAMICLMPROGRAM primary key  (DeviceID);
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
   NotifyInactiveTime   datetime              not null,
   ConstraintOverride   char(1)              not null
);
go


alter table DynamicLMProgramDirect
   add constraint PK_DYNAMICLMPROGRAMDIRECT primary key  (DeviceID);
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
   UpdateTime           datetime             not null
);
go


alter table DynamicPAOInfo
   add constraint PK_DYNPAOINFO primary key  (EntryID);
go


alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique  (PAObjectID, Owner, InfoKey);
go


/*==============================================================*/
/* Table: DynamicPAOStatistics                                  */
/*==============================================================*/
create table DynamicPAOStatistics (
   PAOBjectID           numeric              not null,
   StatisticType        varchar(16)          not null,
   Requests             numeric              not null,
   Completions          numeric              not null,
   Attempts             numeric              not null,
   CommErrors           numeric              not null,
   ProtocolErrors       numeric              not null,
   SystemErrors         numeric              not null,
   StartDateTime        datetime             not null,
   StopDateTime         datetime             not null
);
go


alter table DynamicPAOStatistics
   add constraint PK_DYNAMICPAOSTATISTICS primary key  (PAOBjectID, StatisticType);
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
   UserName             varchar(64)          not null
);
go


alter table DynamicPointAlarming
   add constraint PK_DYNAMICPOINTALARMING primary key  (PointID, AlarmCondition);
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
   ForStr               varchar(60)          not null
);
go


alter table DynamicTags
   add constraint PK_DYNAMICTAGS primary key  (InstanceID);
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
   CodeStatus           varchar(32)          not null
);
go


alter table DynamicVerification
   add constraint PK_DYNAMICVERIFICATION primary key  (LogID);
go


/*==============================================================*/
/* Index: Index_DYNVER_CS                                       */
/*==============================================================*/
create   index Index_DYNVER_CS on DynamicVerification (
CodeSequence
);
go


/*==============================================================*/
/* Index: Indx_DYNV_TIME                                        */
/*==============================================================*/
create   index Indx_DYNV_TIME on DynamicVerification (
TimeArrival
);
go


/*==============================================================*/
/* Table: EnergyCompany                                         */
/*==============================================================*/
create table EnergyCompany (
   EnergyCompanyID      numeric              not null,
   Name                 varchar(60)          not null,
   PrimaryContactID     numeric              not null,
   UserID               numeric              not null
);
go


alter table EnergyCompany
   add constraint PK_ENERGYCOMPANY primary key  (EnergyCompanyID);
go


/*==============================================================*/
/* Index: Indx_EnCmpName                                        */
/*==============================================================*/
create unique  index Indx_EnCmpName on EnergyCompany (
Name
);
go


/*==============================================================*/
/* Table: EnergyCompanyCustomerList                             */
/*==============================================================*/
create table EnergyCompanyCustomerList (
   EnergyCompanyID      numeric              not null,
   CustomerID           numeric              not null
);
go


alter table EnergyCompanyCustomerList
   add constraint PK_ENERGYCOMPANYCUSTOMERLIST primary key  (EnergyCompanyID, CustomerID);
go


/*==============================================================*/
/* Table: EnergyCompanyOperatorLoginList                        */
/*==============================================================*/
create table EnergyCompanyOperatorLoginList (
   EnergyCompanyID      numeric              not null,
   OperatorLoginID      numeric              not null
);
go


alter table EnergyCompanyOperatorLoginList
   add constraint PK_ENERGYCOMPANYOPERATORLOGINL primary key  (EnergyCompanyID, OperatorLoginID);
go


/*==============================================================*/
/* Table: FDRInterface                                          */
/*==============================================================*/
create table FDRInterface (
   InterfaceID          numeric              not null,
   InterfaceName        varchar(30)          not null,
   PossibleDirections   varchar(100)         not null,
   hasDestination       char(1)              not null
);
go




alter table FDRInterface
   add constraint PK_FDRINTERFACE primary key  (InterfaceID);
go


/*==============================================================*/
/* Table: FDRInterfaceOption                                    */
/*==============================================================*/
create table FDRInterfaceOption (
   InterfaceID          numeric              not null,
   OptionLabel          varchar(20)          not null,
   Ordering             numeric              not null,
   OptionType           varchar(8)           not null,
   OptionValues         varchar(256)         not null
);
go



alter table FDRInterfaceOption
   add constraint PK_FDRINTERFACEOPTION primary key  (InterfaceID, Ordering);
go


/*==============================================================*/
/* Table: FDRTRANSLATION                                        */
/*==============================================================*/
create table FDRTRANSLATION (
   POINTID              numeric              not null,
   DIRECTIONTYPE        varchar(20)          not null,
   InterfaceType        varchar(20)          not null,
   DESTINATION          varchar(20)          not null,
   TRANSLATION          varchar(100)         not null
);
go


alter table FDRTRANSLATION
   add constraint PK_FDRTrans primary key  (POINTID, DIRECTIONTYPE, InterfaceType, TRANSLATION);
go


/*==============================================================*/
/* Index: Indx_FdrTransIntTyp                                   */
/*==============================================================*/
create   index Indx_FdrTransIntTyp on FDRTRANSLATION (
InterfaceType
);
go


/*==============================================================*/
/* Index: Indx_FdrTrnsIntTypDir                                 */
/*==============================================================*/
create   index Indx_FdrTrnsIntTypDir on FDRTRANSLATION (
DIRECTIONTYPE,
InterfaceType
);
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
   MoreData             varchar(100)         not null
);
go


alter table GRAPHDATASERIES
   add constraint SYS_GrphDserID primary key  (GRAPHDATASERIESID);
go


/*==============================================================*/
/* Index: Indx_GrpDSerPtID                                      */
/*==============================================================*/
create   index Indx_GrpDSerPtID on GRAPHDATASERIES (
POINTID
);
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
   Type                 char(1)              not null
);
go


alter table GRAPHDEFINITION
   add constraint SYS_C0015109 primary key  (GRAPHDEFINITIONID);
go


alter table GRAPHDEFINITION
   add constraint AK_GRNMUQ_GRAPHDEF unique  (NAME);
go


/*==============================================================*/
/* Table: GatewayEndDevice                                      */
/*==============================================================*/
create table GatewayEndDevice (
   SerialNumber         varchar(30)          not null,
   HardwareType         numeric              not null,
   DataType             numeric              not null,
   DataValue            varchar(100)         null
);
go


alter table GatewayEndDevice
   add constraint PK_GATEWAYENDDEVICE primary key  (SerialNumber, HardwareType, DataType);
go


/*==============================================================*/
/* Table: GenericMacro                                          */
/*==============================================================*/
create table GenericMacro (
   OwnerID              numeric              not null,
   ChildID              numeric              not null,
   ChildOrder           numeric              not null,
   MacroType            varchar(20)          not null
);
go


alter table GenericMacro
   add constraint PK_GENERICMACRO primary key  (OwnerID, ChildOrder, MacroType);
go


/*==============================================================*/
/* Table: GraphCustomerList                                     */
/*==============================================================*/
create table GraphCustomerList (
   GraphDefinitionID    numeric              not null,
   CustomerID           numeric              not null,
   CustomerOrder        numeric              not null
);
go


alter table GraphCustomerList
   add constraint PK_GRAPHCUSTOMERLIST primary key  (GraphDefinitionID, CustomerID);
go


/*==============================================================*/
/* Table: HolidaySchedule                                       */
/*==============================================================*/
create table HolidaySchedule (
   HolidayScheduleID    numeric              not null,
   HolidayScheduleName  varchar(40)          not null
);
go


alter table HolidaySchedule
   add constraint PK_HOLIDAYSCHEDULE primary key  (HolidayScheduleID);
go


/*==============================================================*/
/* Index: Indx_HolSchName                                       */
/*==============================================================*/
create unique  index Indx_HolSchName on HolidaySchedule (
HolidayScheduleName
);
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
   TemplateName         varchar(64)          not null
);
go


alter table ImportData
   add constraint PK_IMPORTDATA primary key  (Address);
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
   DateTime             datetime             null
);
go


alter table ImportFail
   add constraint PK_IMPORTFAIL primary key  (Address);
go


/*==============================================================*/
/* Table: LMCONTROLAREAPROGRAM                                  */
/*==============================================================*/
create table LMCONTROLAREAPROGRAM (
   DEVICEID             numeric              not null,
   LMPROGRAMDEVICEID    numeric              not null,
   StartPriority        numeric              not null,
   StopPriority         numeric              not null
);
go


alter table LMCONTROLAREAPROGRAM
   add constraint PK_LMCONTROLAREAPROGRAM primary key  (DEVICEID, LMPROGRAMDEVICEID);
go


/*==============================================================*/
/* Table: LMCONTROLAREATRIGGER                                  */
/*==============================================================*/
create table LMCONTROLAREATRIGGER (
   DEVICEID             numeric              not null,
   TRIGGERNUMBER        numeric              not null,
   TRIGGERTYPE          varchar(20)          not null,
   POINTID              numeric              not null,
   NORMALSTATE          numeric              not null,
   THRESHOLD            float                not null,
   PROJECTIONTYPE       varchar(14)          not null,
   PROJECTIONPOINTS     numeric              not null,
   PROJECTAHEADDURATION numeric              not null,
   THRESHOLDKICKPERCENT numeric              not null,
   MINRESTOREOFFSET     float                not null,
   PEAKPOINTID          numeric              not null,
   TriggerID            numeric              not null
);
go


alter table LMCONTROLAREATRIGGER
   add constraint PK_LMCONTROLAREATRIGGER primary key  (DEVICEID, TRIGGERNUMBER);
go


/*==============================================================*/
/* Index: INDX_UNQ_LMCNTRTR_TRID                                */
/*==============================================================*/
create unique  index INDX_UNQ_LMCNTRTR_TRID on LMCONTROLAREATRIGGER (
TriggerID
);
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
   REQUIREALLTRIGGERSACTIVEFLAG varchar(1)           not null
);
go


alter table LMControlArea
   add constraint PK_LMCONTROLAREA primary key  (DEVICEID);
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
   StopDateTime         datetime             not null
);
go


alter table LMControlHistory
   add constraint PK_LMCONTROLHISTORY primary key  (LMCtrlHistID);
go


/*==============================================================*/
/* Index: Indx_Start                                            */
/*==============================================================*/
create   index Indx_Start on LMControlHistory (
StartDateTime
);
go


/*==============================================================*/
/* Table: LMControlScenarioProgram                              */
/*==============================================================*/
create table LMControlScenarioProgram (
   ScenarioID           numeric              not null,
   ProgramID            numeric              not null,
   StartOffset          numeric              not null,
   StopOffset           numeric              not null,
   StartGear            numeric              not null
);
go


alter table LMControlScenarioProgram
   add constraint PK_LMCONTROLSCENARIOPROGRAM primary key  (ScenarioID, ProgramID);
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
   AckLateFlag          char(1)              not null
);
go


alter table LMCurtailCustomerActivity
   add constraint PK_LMCURTAILCUSTOMERACTIVITY primary key  (CustomerID, CurtailReferenceID);
go


/*==============================================================*/
/* Index: Index_LMCrtCstActID                                   */
/*==============================================================*/
create   index Index_LMCrtCstActID on LMCurtailCustomerActivity (
CustomerID
);
go


/*==============================================================*/
/* Index: Index_LMCrtCstAckSt                                   */
/*==============================================================*/
create   index Index_LMCrtCstAckSt on LMCurtailCustomerActivity (
AcknowledgeStatus
);
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
   AdditionalInfo       varchar(100)         not null
);
go


alter table LMCurtailProgramActivity
   add constraint PK_LMCURTAILPROGRAMACTIVITY primary key  (CurtailReferenceID);
go


/*==============================================================*/
/* Index: Indx_LMCrtPrgActStTime                                */
/*==============================================================*/
create   index Indx_LMCrtPrgActStTime on LMCurtailProgramActivity (
CurtailmentStartTime
);
go


/*==============================================================*/
/* Table: LMDirectCustomerList                                  */
/*==============================================================*/
create table LMDirectCustomerList (
   ProgramID            numeric              not null,
   CustomerID           numeric              not null
);
go


alter table LMDirectCustomerList
   add constraint PK_LMDIRECTCUSTOMERLIST primary key  (ProgramID, CustomerID);
go


/*==============================================================*/
/* Table: LMDirectNotifGrpList                                  */
/*==============================================================*/
create table LMDirectNotifGrpList (
   ProgramID            numeric              not null,
   NotificationGrpID    numeric              not null
);
go


alter table LMDirectNotifGrpList
   add constraint PK_LMDIRECTNOTIFGRPLIST primary key  (ProgramID, NotificationGrpID);
go


/*==============================================================*/
/* Table: LMEnergyExchangeCustomerList                          */
/*==============================================================*/
create table LMEnergyExchangeCustomerList (
   ProgramID            numeric              not null,
   CustomerID           numeric              not null,
   CustomerOrder        numeric              not null
);
go


alter table LMEnergyExchangeCustomerList
   add constraint PK_LMENERGYEXCHANGECUSTOMERLIS primary key  (ProgramID, CustomerID);
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
   EnergyExchangeNotes  varchar(120)         not null
);
go


alter table LMEnergyExchangeCustomerReply
   add constraint PK_LMENERGYEXCHANGECUSTOMERREP primary key  (CustomerID, OfferID, RevisionNumber);
go


/*==============================================================*/
/* Table: LMEnergyExchangeHourlyCustomer                        */
/*==============================================================*/
create table LMEnergyExchangeHourlyCustomer (
   CustomerID           numeric              not null,
   OfferID              numeric              not null,
   RevisionNumber       numeric              not null,
   Hour                 numeric              not null,
   AmountCommitted      float                not null
);
go


alter table LMEnergyExchangeHourlyCustomer
   add constraint PK_LMENERGYEXCHANGEHOURLYCUSTO primary key  (CustomerID, OfferID, RevisionNumber, Hour);
go


/*==============================================================*/
/* Table: LMEnergyExchangeHourlyOffer                           */
/*==============================================================*/
create table LMEnergyExchangeHourlyOffer (
   OfferID              numeric              not null,
   RevisionNumber       numeric              not null,
   Hour                 numeric              not null,
   Price                numeric              not null,
   AmountRequested      float                not null
);
go


alter table LMEnergyExchangeHourlyOffer
   add constraint PK_LMENERGYEXCHANGEHOURLYOFFER primary key  (OfferID, RevisionNumber, Hour);
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
   AdditionalInfo       varchar(100)         not null
);
go


alter table LMEnergyExchangeOfferRevision
   add constraint PK_LMENERGYEXCHANGEOFFERREVISI primary key  (OfferID, RevisionNumber);
go


/*==============================================================*/
/* Table: LMEnergyExchangeProgramOffer                          */
/*==============================================================*/
create table LMEnergyExchangeProgramOffer (
   DeviceID             numeric              not null,
   OfferID              numeric              not null,
   RunStatus            varchar(20)          not null,
   OfferDate            datetime             not null
);
go


alter table LMEnergyExchangeProgramOffer
   add constraint PK_LMENERGYEXCHANGEPROGRAMOFFE primary key  (OfferID);
go


/*==============================================================*/
/* Table: LMGroup                                               */
/*==============================================================*/
create table LMGroup (
   DeviceID             numeric              not null,
   KWCapacity           float                not null
);
go


alter table LMGroup
   add constraint PK_LMGROUP primary key  (DeviceID);
go


/*==============================================================*/
/* Table: LMGroupEmetcon                                        */
/*==============================================================*/
create table LMGroupEmetcon (
   DEVICEID             numeric              not null,
   GOLDADDRESS          numeric              not null,
   SILVERADDRESS        numeric              not null,
   ADDRESSUSAGE         char(1)              not null,
   RELAYUSAGE           char(1)              not null,
   ROUTEID              numeric              not null
);
go


alter table LMGroupEmetcon
   add constraint PK_LMGROUPEMETCON primary key  (DEVICEID);
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
   RelayUsage           char(15)             not null
);
go


alter table LMGroupExpressCom
   add constraint PK_LMGROUPEXPRESSCOM primary key  (LMGroupID);
go


/*==============================================================*/
/* Table: LMGroupExpressComAddress                              */
/*==============================================================*/
create table LMGroupExpressComAddress (
   AddressID            numeric              not null,
   AddressType          varchar(20)          not null,
   Address              numeric              not null,
   AddressName          varchar(30)          not null
);
go


alter table LMGroupExpressComAddress
   add constraint PK_LMGROUPEXPRESSCOMADDRESS primary key  (AddressID);
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
   MCTDeviceID          numeric              not null
)
go


alter table LMGroupMCT
   add constraint PK_LMGrpMCTPK primary key  (DeviceID);
go


/*==============================================================*/
/* Table: LMGroupPoint                                          */
/*==============================================================*/
create table LMGroupPoint (
   DEVICEID             numeric              not null,
   DeviceIDUsage        numeric              not null,
   PointIDUsage         numeric              not null,
   StartControlRawState numeric              not null
)
go


alter table LMGroupPoint
   add constraint PK_LMGROUPPOINT primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: LMGroupRipple                                         */
/*==============================================================*/
create table LMGroupRipple (
   DeviceID             numeric              not null,
   RouteID              numeric              not null,
   ShedTime             numeric              not null,
   ControlValue         char(50)             not null,
   RestoreValue         char(50)             not null
)
go


alter table LMGroupRipple
   add constraint PK_LMGROUPRIPPLE primary key  (DeviceID);
go


/*==============================================================*/
/* Table: LMGroupSA205105                                       */
/*==============================================================*/
create table LMGroupSA205105 (
   GroupID              numeric              not null,
   RouteID              numeric              not null,
   OperationalAddress   numeric              not null,
   LoadNumber           varchar(64)          not null
)
go


alter table LMGroupSA205105
   add constraint PK_LMGROUPSA205105 primary key  (GroupID);
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
   LoadNumber           varchar(8)           not null
)
go


alter table LMGroupSA305
   add constraint PK_LMGROUPSA305 primary key  (GroupID);
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
   SpaceIndex           numeric              not null
);
go


alter table LMGroupSASimple
   add constraint PK_LMGROUPSASIMPLE primary key  (GroupID);
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
   SerialAddress        varchar(15)          not null
);
go


alter table LMGroupVersacom
   add constraint PK_LMGROUPVERSACOM primary key  (DEVICEID);
go


/*==============================================================*/
/* Table: LMMacsScheduleCustomerList                            */
/*==============================================================*/
create table LMMacsScheduleCustomerList (
   ScheduleID           numeric              not null,
   LMCustomerDeviceID   numeric              not null,
   CustomerOrder        numeric              not null
);
go


alter table LMMacsScheduleCustomerList
   add constraint PK_LMMACSSCHEDULECUSTOMERLIST primary key  (ScheduleID, LMCustomerDeviceID);
go


/*==============================================================*/
/* Table: LMPROGRAM                                             */
/*==============================================================*/
create table LMPROGRAM (
   DeviceID             numeric              not null,
   ControlType          varchar(20)          not null,
   ConstraintID         numeric              not null
)
go


alter table LMPROGRAM
   add constraint PK_LMPROGRAM primary key  (DeviceID);
go


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
   SeasonScheduleID     numeric              not null
);
go


alter table LMProgramConstraints
   add constraint PK_PRGCONSTR primary key  (ConstraintID);
go


/*==============================================================*/
/* Table: LMProgramControlWindow                                */
/*==============================================================*/
create table LMProgramControlWindow (
   DeviceID             numeric              not null,
   WindowNumber         numeric              not null,
   AvailableStartTime   numeric              not null,
   AvailableStopTime    numeric              not null
);
go


alter table LMProgramControlWindow
   add constraint PK_LMPROGRAMCONTROLWINDOW primary key  (DeviceID, WindowNumber);
go


/*==============================================================*/
/* Table: LMProgramCurtailCustomerList                          */
/*==============================================================*/
create table LMProgramCurtailCustomerList (
   ProgramID            numeric              not null,
   CustomerID           numeric              not null,
   CustomerOrder        numeric              not null,
   RequireAck           char(1)              not null
);
go


alter table LMProgramCurtailCustomerList
   add constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key  (CustomerID, ProgramID);
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
   StoppedEarlyMsg      varchar(80)          not null
);
go


alter table LMProgramCurtailment
   add constraint PK_LMPROGRAMCURTAILMENT primary key  (DeviceID);
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
   NotifyInactiveOffset numeric              not null
);
go


alter table LMProgramDirect
   add constraint PK_LMPROGRAMDIRECT primary key  (DeviceID);
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
   BackRampTime         numeric              not null
);
go


alter table LMProgramDirectGear
   add constraint PK_LMPROGRAMDIRECTGEAR primary key  (GearID);
go


alter table LMProgramDirectGear
   add constraint AK_AKEY_LMPRGDIRG_LMPROGRA unique  (DeviceID, GearNumber);
go


/*==============================================================*/
/* Table: LMProgramDirectGroup                                  */
/*==============================================================*/
create table LMProgramDirectGroup (
   DeviceID             numeric              not null,
   LMGroupDeviceID      numeric              not null,
   GroupOrder           numeric              not null
);
go


alter table LMProgramDirectGroup
   add constraint PK_LMPROGRAMDIRECTGROUP primary key  (DeviceID, GroupOrder);
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
   StoppedEarlyMsg      varchar(80)          not null
);
go


alter table LMProgramEnergyExchange
   add constraint PK_LMPROGRAMENERGYEXCHANGE primary key  (DeviceID);
go


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
   ValueTf              numeric              not null
);
go


alter table LMThermoStatGear
   add constraint PK_LMTHERMOSTATGEAR primary key  (GearID);
go


/*==============================================================*/
/* Table: LOGIC                                                 */
/*==============================================================*/
create table LOGIC (
   LOGICID              numeric              not null,
   LOGICNAME            varchar(20)          not null,
   PERIODICRATE         numeric              not null,
   STATEFLAG            varchar(10)          not null,
   SCRIPTNAME           varchar(20)          not null
);
go


alter table LOGIC
   add constraint SYS_C0013445 primary key  (LOGICID);
go


/*==============================================================*/
/* Table: MACROROUTE                                            */
/*==============================================================*/
create table MACROROUTE (
   ROUTEID              numeric              not null,
   SINGLEROUTEID        numeric              not null,
   ROUTEORDER           numeric              not null
);
go


alter table MACROROUTE
   add constraint PK_MACROROUTE primary key  (ROUTEID, ROUTEORDER);
go


/*==============================================================*/
/* Table: MACSchedule                                           */
/*==============================================================*/
create table MACSchedule (
   ScheduleID           numeric              not null,
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
   Template             numeric              null
);
go


alter table MACSchedule
   add constraint PK_MACSCHEDULE primary key  (ScheduleID);
go


/*==============================================================*/
/* Table: MACSimpleSchedule                                     */
/*==============================================================*/
create table MACSimpleSchedule (
   ScheduleID           numeric              not null,
   TargetSelect         varchar(40)          null,
   StartCommand         varchar(120)         null,
   StopCommand          varchar(120)         null,
   RepeatInterval       numeric              null
);
go


alter table MACSimpleSchedule
   add constraint PK_MACSIMPLESCHEDULE primary key  (ScheduleID);
go


/*==============================================================*/
/* Table: MCTBroadCastMapping                                   */
/*==============================================================*/
create table MCTBroadCastMapping (
   MCTBroadCastID       numeric              not null,
   MctID                numeric              not null,
   Ordering             numeric              not null
);
go


alter table MCTBroadCastMapping
   add constraint PK_MCTBROADCASTMAPPING primary key  (MCTBroadCastID, MctID);
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
   DisplayDigits        numeric              not null
);
go


alter table MCTConfig
   add constraint PK_MCTCONFIG primary key  (ConfigID);
go


/*==============================================================*/
/* Table: MCTConfigMapping                                      */
/*==============================================================*/
create table MCTConfigMapping (
   MctID                numeric              not null,
   ConfigID             numeric              not null
);
go


alter table MCTConfigMapping
   add constraint PK_MCTCONFIGMAPPING primary key  (MctID, ConfigID);
go


/*==============================================================*/
/* Table: NotificationDestination                               */
/*==============================================================*/
create table NotificationDestination (
   NotificationGroupID  numeric              not null,
   RecipientID          numeric              not null,
   Attribs              char(16)             not null
);
go


alter table NotificationDestination
   add constraint PKey_NotDestID primary key  (NotificationGroupID, RecipientID);
go


/*==============================================================*/
/* Table: NotificationGroup                                     */
/*==============================================================*/
create table NotificationGroup (
   NotificationGroupID  numeric              not null,
   GroupName            varchar(40)          not null,
   DisableFlag          char(1)              not null
);
go


alter table NotificationGroup
   add constraint PK_NOTIFICATIONGROUP primary key  (NotificationGroupID);
go


/*==============================================================*/
/* Index: Indx_NOTIFGRPNme                                      */
/*==============================================================*/
create unique  index Indx_NOTIFGRPNme on NotificationGroup (
GroupName
);
go


/*==============================================================*/
/* Table: OperatorLoginGraphList                                */
/*==============================================================*/
create table OperatorLoginGraphList (
   OperatorLoginID      numeric              not null,
   GraphDefinitionID    numeric              not null
);
go


alter table OperatorLoginGraphList
   add constraint PK_OPERATORLOGINGRAPHLIST primary key  (OperatorLoginID, GraphDefinitionID);
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
   FuncParams           varchar(200)         not null
);
go


alter table PAOExclusion
   add constraint PK_PAOEXCLUSION primary key  (ExclusionID);
go


/*==============================================================*/
/* Index: Indx_PAOExclus                                        */
/*==============================================================*/
create unique  index Indx_PAOExclus on PAOExclusion (
PaoID,
ExcludedPaoID
);
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
   Disabled             char(1)              not null
);
go


alter table PAOSchedule
   add constraint PK_PAOSCHEDULE primary key  (ScheduleID);
go


/*==============================================================*/
/* Table: PAOScheduleAssignment                                 */
/*==============================================================*/
create table PAOScheduleAssignment (
   EventID              numeric              not null,
   ScheduleID           numeric              not null,
   PaoID                numeric              not null,
   Command              varchar(128)         not null
);
go


alter table PAOScheduleAssignment
   add constraint PK_PAOSCHEDULEASSIGNMENT primary key  (EventID);
go


/*==============================================================*/
/* Table: PAOowner                                              */
/*==============================================================*/
create table PAOowner (
   OwnerID              numeric              not null,
   ChildID              numeric              not null
);
go


alter table PAOowner
   add constraint PK_PAOOWNER primary key  (OwnerID, ChildID);
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
   ARCHIVEINTERVAL      numeric              not null
);
go


alter table POINT
   add constraint Key_PT_PTID primary key  (POINTID);
go


alter table POINT
   add constraint AK_KEY_PTNM_YUKPAOID unique  (POINTNAME, PAObjectID);
go


/*==============================================================*/
/* Index: Indx_PointStGrpID                                     */
/*==============================================================*/
create   index Indx_PointStGrpID on POINT (
STATEGROUPID;
)
go


/*==============================================================*/
/* Table: POINTACCUMULATOR                                      */
/*==============================================================*/
create table POINTACCUMULATOR (
   POINTID              numeric              not null,
   MULTIPLIER           float                not null,
   DATAOFFSET           float                not null
);
go


alter table POINTACCUMULATOR
   add constraint PK_POINTACCUMULATOR primary key  (POINTID);
go


/*==============================================================*/
/* Table: POINTANALOG                                           */
/*==============================================================*/
create table POINTANALOG (
   POINTID              numeric              not null,
   DEADBAND             float                not null,
   TRANSDUCERTYPE       varchar(14)          not null,
   MULTIPLIER           float                not null,
   DATAOFFSET           float                not null
);
go


alter table POINTANALOG
   add constraint PK_POINTANALOG primary key  (POINTID);
go


/*==============================================================*/
/* Table: POINTLIMITS                                           */
/*==============================================================*/
create table POINTLIMITS (
   POINTID              numeric              not null,
   LIMITNUMBER          numeric              not null,
   HIGHLIMIT            float                not null,
   LOWLIMIT             float                not null,
   LIMITDURATION        numeric              not null
);
go


alter table POINTLIMITS
   add constraint PK_POINTLIMITS primary key  (POINTID, LIMITNUMBER);
go


/*==============================================================*/
/* Table: POINTSTATUS                                           */
/*==============================================================*/
create table POINTSTATUS (
   POINTID              numeric              not null,
   INITIALSTATE         numeric              not null,
   CONTROLTYPE          varchar(12)          not null,
   CONTROLINHIBIT       varchar(1)           not null,
   ControlOffset        numeric              not null,
   CloseTime1           numeric              not null,
   CloseTime2           numeric              not null,
   StateZeroControl     varchar(100)         not null,
   StateOneControl      varchar(100)         not null,
   CommandTimeOut       numeric              not null
);
go



alter table POINTSTATUS
   add constraint PK_PtStatus primary key  (POINTID);
go


/*==============================================================*/
/* Table: POINTUNIT                                             */
/*==============================================================*/
create table POINTUNIT (
   POINTID              numeric              not null,
   UOMID                numeric              not null,
   DECIMALPLACES        numeric              not null,
   HighReasonabilityLimit float                not null,
   LowReasonabilityLimit float                not null
);
go


alter table POINTUNIT
   add constraint PK_POINTUNITID primary key  (POINTID);
go


/*==============================================================*/
/* Table: PORTDIALUPMODEM                                       */
/*==============================================================*/
create table PORTDIALUPMODEM (
   PORTID               numeric              not null,
   MODEMTYPE            varchar(30)          not null,
   INITIALIZATIONSTRING varchar(50)          not null,
   PREFIXNUMBER         varchar(10)          not null,
   SUFFIXNUMBER         varchar(10)          not null
);
go


alter table PORTDIALUPMODEM
   add constraint PK_PORTDIALUPMODEM primary key  (PORTID);
go


/*==============================================================*/
/* Table: PORTLOCALSERIAL                                       */
/*==============================================================*/
create table PORTLOCALSERIAL (
   PORTID               numeric              not null,
   PHYSICALPORT         varchar(8)           not null
)
go


alter table PORTLOCALSERIAL
   add constraint PK_PORTLOCALSERIAL primary key  (PORTID);
go


/*==============================================================*/
/* Table: PORTRADIOSETTINGS                                     */
/*==============================================================*/
create table PORTRADIOSETTINGS (
   PORTID               numeric              not null,
   RTSTOTXWAITSAMED     numeric              not null,
   RTSTOTXWAITDIFFD     numeric              not null,
   RADIOMASTERTAIL      numeric              not null,
   REVERSERTS           numeric              not null
);
go


alter table PORTRADIOSETTINGS
   add constraint PK_PORTRADIOSETTINGS primary key  (PORTID);
go


/*==============================================================*/
/* Table: PORTSETTINGS                                          */
/*==============================================================*/
create table PORTSETTINGS (
   PORTID               numeric              not null,
   BAUDRATE             numeric              not null,
   CDWAIT               numeric              not null,
   LINESETTINGS         varchar(8)           not null
);
go


alter table PORTSETTINGS
   add constraint PK_PORTSETTINGS primary key  (PORTID);
go


/*==============================================================*/
/* Table: PORTTERMINALSERVER                                    */
/*==============================================================*/
create table PORTTERMINALSERVER (
   PORTID               numeric              not null,
   IPADDRESS            varchar(64)          not null,
   SOCKETPORTNUMBER     numeric              not null
);
go


alter table PORTTERMINALSERVER
   add constraint PK_PORTTERMINALSERVER primary key  (PORTID);
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
   RecipientID          numeric              not null
);
go

alter table PointAlarming
   add constraint PK_POINTALARMING primary key  (PointID);
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
   EXTRATIMEOUT         numeric              not null
);
go


alter table PortTiming
   add constraint PK_PORTTIMING primary key  (PORTID);
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
   millis               smallint             not null
);
go


alter table RAWPOINTHISTORY
   add constraint SYS_C0013322 primary key  (CHANGEID);
go


/*==============================================================*/
/* Index: Index_PointID                                         */
/*==============================================================*/
create   index Index_PointID on RAWPOINTHISTORY (
POINTID
);
go


/*==============================================================*/
/* Index: Indx_TimeStamp                                        */
/*==============================================================*/
create   index Indx_TimeStamp on RAWPOINTHISTORY (
TIMESTAMP
);
go


/*==============================================================*/
/* Table: RepeaterRoute                                         */
/*==============================================================*/
create table RepeaterRoute (
   ROUTEID              numeric              not null,
   DEVICEID             numeric              not null,
   VARIABLEBITS         numeric              not null,
   REPEATERORDER        numeric              not null
);
go


alter table RepeaterRoute
   add constraint PK_REPEATERROUTE primary key  (ROUTEID, DEVICEID);
go


/*==============================================================*/
/* Table: Route                                                 */
/*==============================================================*/
create table Route (
   RouteID              numeric              not null,
   DeviceID             numeric              not null,
   DefaultRoute         char(1)              not null
);
go


alter table Route
   add constraint SYS_RoutePK primary key  (RouteID);
go


/*==============================================================*/
/* Index: Indx_RouteDevID                                       */
/*==============================================================*/
create unique  index Indx_RouteDevID on Route (
DeviceID,
RouteID
);
go


/*==============================================================*/
/* Table: STATE                                                 */
/*==============================================================*/
create table STATE (
   STATEGROUPID         numeric              not null,
   RAWSTATE             numeric              not null,
   TEXT                 varchar(32)          not null,
   FOREGROUNDCOLOR      numeric              not null,
   BACKGROUNDCOLOR      numeric              not null,
   ImageID              numeric              not null
);
go



alter table STATE
   add constraint PK_STATE primary key  (STATEGROUPID, RAWSTATE);
go


/*==============================================================*/
/* Index: Indx_StateRaw                                         */
/*==============================================================*/
create   index Indx_StateRaw on STATE (
RAWSTATE
);
go


/*==============================================================*/
/* Table: STATEGROUP                                            */
/*==============================================================*/
create table STATEGROUP (
   STATEGROUPID         numeric              not null,
   NAME                 varchar(20)          not null,
   GroupType            varchar(20)          not null
);
go



alter table STATEGROUP
   add constraint SYS_C0013128 primary key  (STATEGROUPID);
go


/*==============================================================*/
/* Index: Indx_STATEGRP_Nme                                     */
/*==============================================================*/
create unique  index Indx_STATEGRP_Nme on STATEGROUP (
NAME
);
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
   millis               smallint             not null
);
go


alter table SYSTEMLOG
   add constraint SYS_C0013407 primary key  (LOGID);
go


/*==============================================================*/
/* Index: Indx_SYSLG_PtId                                       */
/*==============================================================*/
create   index Indx_SYSLG_PtId on SYSTEMLOG (
POINTID
);
go


/*==============================================================*/
/* Index: Indx_SYSLG_Date                                       */
/*==============================================================*/
create   index Indx_SYSLG_Date on SYSTEMLOG (
DATETIME
);
go


/*==============================================================*/
/* Table: SeasonSchedule                                        */
/*==============================================================*/
create table SeasonSchedule (
   ScheduleID           numeric              not null,
   ScheduleName         varchar(40)          not null
);
go


alter table SeasonSchedule
   add constraint PK_SEASONSCHEDULE primary key  (ScheduleID);
go


/*==============================================================*/
/* Table: TEMPLATE                                              */
/*==============================================================*/
create table TEMPLATE (
   TEMPLATENUM          numeric              not null,
   NAME                 varchar(40)          not null,
   DESCRIPTION          varchar(200)         null
);
go



alter table TEMPLATE
   add constraint SYS_C0013425 primary key  (TEMPLATENUM);
go


/*==============================================================*/
/* Table: TEMPLATECOLUMNS                                       */
/*==============================================================*/
create table TEMPLATECOLUMNS (
   TEMPLATENUM          numeric              not null,
   TITLE                varchar(50)          not null,
   TYPENUM              numeric              not null,
   ORDERING             numeric              not null,
   WIDTH                numeric              not null
);
go





alter table TEMPLATECOLUMNS
   add constraint PK_TEMPLATECOLUMNS primary key  (TEMPLATENUM, TITLE);
go


/*==============================================================*/
/* Table: TOUDay                                                */
/*==============================================================*/
create table TOUDay (
   TOUDayID             numeric              not null,
   TOUDayName           varchar(32)          not null
);
go


alter table TOUDay
   add constraint PK_TOUDAY primary key  (TOUDayID);
go


/*==============================================================*/
/* Table: TOUDayMapping                                         */
/*==============================================================*/
create table TOUDayMapping (
   TOUScheduleID        numeric              not null,
   TOUDayID             numeric              not null,
   TOUDayOffset         numeric              not null
);
go


alter table TOUDayMapping
   add constraint PK_TOUDAYMAPPING primary key  (TOUScheduleID, TOUDayOffset);
go


/*==============================================================*/
/* Table: TOUDayRateSwitches                                    */
/*==============================================================*/
create table TOUDayRateSwitches (
   TOURateSwitchID      numeric              not null,
   SwitchRate           varchar(4)           not null,
   SwitchOffset         numeric              not null,
   TOUDayID             numeric              not null
);
go


alter table TOUDayRateSwitches
   add constraint PK_TOURATESWITCH primary key  (TOURateSwitchID);
go


/*==============================================================*/
/* Index: Indx_todsw_idoff                                      */
/*==============================================================*/
create unique  index Indx_todsw_idoff on TOUDayRateSwitches (
SwitchOffset,
TOUDayID
);
go


/*==============================================================*/
/* Table: TOUSchedule                                           */
/*==============================================================*/
create table TOUSchedule (
   TOUScheduleID        numeric              not null,
   TOUScheduleName      varchar(32)          not null,
   TOUDefaultRate       varchar(4)           not null
);
go


alter table TOUSchedule
   add constraint PK_TOUSCHEDULE primary key  (TOUScheduleID);
go


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
   ForStr               varchar(60)          not null
);
go


alter table TagLog
   add constraint PK_TAGLOG primary key  (LogID);
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
   ImageID              numeric              not null
);
go


alter table Tags
   add constraint PK_TAGS primary key  (TagID);
go


/*==============================================================*/
/* Table: UNITMEASURE                                           */
/*==============================================================*/
create table UNITMEASURE (
   UOMID                numeric              not null,
   UOMName              varchar(8)           not null,
   CalcType             numeric              not null,
   LongName             varchar(40)          not null,
   Formula              varchar(80)          not null
);
go


alter table UNITMEASURE
   add constraint SYS_C0013344 primary key  (UOMID);
go


/*==============================================================*/
/* Table: UserPAOowner                                          */
/*==============================================================*/
create table UserPAOowner (
   UserID               numeric              not null,
   PaoID                numeric              not null
);
go


alter table UserPAOowner
   add constraint PK_USERPAOOWNER primary key  (UserID, PaoID);
go


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
   AMPCARDSET           numeric              not null
);
go


alter table VersacomRoute
   add constraint PK_VERSACOMROUTE primary key  (ROUTEID);
go


/*==============================================================*/
/* Table: YukonGroup                                            */
/*==============================================================*/
create table YukonGroup (
   GroupID              numeric              not null,
   GroupName            varchar(120)         not null,
   GroupDescription     varchar(200)         not null
);
go


alter table YukonGroup
   add constraint PK_YUKONGROUP primary key  (GroupID);
go


/*==============================================================*/
/* Table: YukonGroupRole                                        */
/*==============================================================*/
create table YukonGroupRole (
   GroupRoleID          numeric              not null,
   GroupID              numeric              not null,
   RoleID               numeric              not null,
   RolePropertyID       numeric              not null,
   Value                varchar(1000)        not null
);
go


alter table YukonGroupRole
   add constraint PK_YUKONGRPROLE primary key  (GroupRoleID);
go


/*==============================================================*/
/* Table: YukonImage                                            */
/*==============================================================*/
create table YukonImage (
   ImageID              numeric              not null,
   ImageCategory        varchar(20)          not null,
   ImageName            varchar(80)          not null,
   ImageValue           image                null
);
go


alter table YukonImage
   add constraint PK_YUKONIMAGE primary key  (ImageID);
go


/*==============================================================*/
/* Table: YukonListEntry                                        */
/*==============================================================*/
create table YukonListEntry (
   EntryID              numeric              not null,
   ListID               numeric              not null,
   EntryOrder           numeric              not null,
   EntryText            varchar(50)          not null,
   YukonDefinitionID    numeric              not null
);
go


alter table YukonListEntry
   add constraint PK_YUKONLISTENTRY primary key  (EntryID);
go


/*==============================================================*/
/* Index: Indx_YkLstDefID                                       */
/*==============================================================*/
create   index Indx_YkLstDefID on YukonListEntry (
YukonDefinitionID
);
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
   PAOStatistics        varchar(10)          not null
);
go


alter table YukonPAObject
   add constraint PK_YUKONPAOBJECT primary key  (PAObjectID);
go


/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique  index Indx_PAO on YukonPAObject (
Category,
PAOName,
PAOClass,
Type
);
go


/*==============================================================*/
/* Table: YukonRole                                             */
/*==============================================================*/
create table YukonRole (
   RoleID               numeric              not null,
   RoleName             varchar(120)         not null,
   Category             varchar(60)          not null,
   RoleDescription      varchar(200)         not null
);
go


alter table YukonRole
   add constraint PK_YUKONROLE primary key  (RoleID);
go


/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create   index Indx_YukRol_Nm on YukonRole (
RoleName
);
go


/*==============================================================*/
/* Table: YukonRoleProperty                                     */
/*==============================================================*/
create table YukonRoleProperty (
   RolePropertyID       numeric              not null,
   RoleID               numeric              not null,
   KeyName              varchar(100)         not null,
   DefaultValue         varchar(1000)        not null,
   Description          varchar(1000)        not null
);
go



alter table YukonRoleProperty
   add constraint PK_YUKONROLEPROPERTY primary key  (RolePropertyID);
go


/*==============================================================*/
/* Table: YukonSelectionList                                    */
/*==============================================================*/
create table YukonSelectionList (
   ListID               numeric              not null,
   Ordering             varchar(1)           not null,
   SelectionLabel       varchar(30)          not null,
   WhereIsList          varchar(100)         not null,
   ListName             varchar(40)          not null,
   UserUpdateAvailable  varchar(1)           not null
);
go

alter table YukonSelectionList
   add constraint PK_YUKONSELECTIONLIST primary key  (ListID);
go


/*==============================================================*/
/* Table: YukonServices                                         */
/*==============================================================*/
create table YukonServices (
   ServiceID            numeric              not null,
   ServiceName          varchar(60)          not null,
   ServiceClass         varchar(100)         not null,
   ParamNames           varchar(300)         not null,
   ParamValues          varchar(300)         not null
);
go


alter table YukonServices
   add constraint PK_YUKSER primary key  (ServiceID);
go


/*==============================================================*/
/* Table: YukonUser                                             */
/*==============================================================*/
create table YukonUser (
   UserID               numeric              not null,
   UserName             varchar(64)          not null,
   Password             varchar(64)          not null,
   Status               varchar(20)          not null
);
go


alter table YukonUser
   add constraint PK_YUKONUSER primary key  (UserID);
go


/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique  index Indx_YkUsIDNm on YukonUser (
UserName
);
go


/*==============================================================*/
/* Table: YukonUserGroup                                        */
/*==============================================================*/
create table YukonUserGroup (
   UserID               numeric              not null,
   GroupID              numeric              not null
);
go


alter table YukonUserGroup
   add constraint PK_YUKONUSERGROUP primary key  (UserID, GroupID);
go


/*==============================================================*/
/* Table: YukonUserRole                                         */
/*==============================================================*/
create table YukonUserRole (
   UserRoleID           numeric              not null,
   UserID               numeric              not null,
   RoleID               numeric              not null,
   RolePropertyID       numeric              not null,
   Value                varchar(1000)        not null
);
go


alter table YukonUserRole
   add constraint PK_YKONUSRROLE primary key  (UserRoleID);
go


/*==============================================================*/
/* Table: YukonWebConfiguration                                 */
/*==============================================================*/
create table YukonWebConfiguration (
   ConfigurationID      numeric              not null,
   LogoLocation         varchar(100)         null,
   Description          varchar(500)         null,
   AlternateDisplayName varchar(100)         null,
   URL                  varchar(100)         null
);
go


alter table YukonWebConfiguration
   add constraint PK_YUKONWEBCONFIGURATION primary key  (ConfigurationID);
go

/*==============================================================*/
/* Table: SettlementConfig                                      */
/*==============================================================*/
create table SettlementConfig (
   ConfigID			numeric				not null,
   FieldName		varchar(64)		not null,
   FieldValue		varchar(64)		not null,
   CTISettlement	varchar(32)		not null,
   YukonDefID		numeric				not null,
   Description		varchar(128)		not null,
   EntryID			numeric				not null,
   RefEntryID		numeric				not null
);
go


alter table SettlementConfig
   add constraint PK_SETTLEMENTCONFIG primary key  (ConfigID);
go

/*==============================================================*/
/* Table: CCMONITORBANKLIST                                     */
/*==============================================================*/
create table CCMONITORBANKLIST (
   BankID               numeric              not null,
   PointID              numeric              not null,
   DisplayOrder         numeric              not null,
   Scannable            char(1)              not null,
   NINAvg               numeric              not null,
   UpperBandwidth        float                not null,
   LowerBandwidth        float                not null
);
go


alter table CCMONITORBANKLIST
   add constraint PK_CCMONITORBANKLIST primary key  (BankID, PointID);
go


alter table CCMONITORBANKLIST
   add constraint FK_CCMONBNKLST_PTID foreign key (PointID)
      references POINT (POINTID);
go


alter table CCMONITORBANKLIST
   add constraint FK_CCMONBNKLIST_BNKID foreign key (BankID)
      references CAPBANK (DEVICEID);
go

/*==============================================================*/
/* Table: DynamicCCMonitorBankHistory                           */
/*==============================================================*/
create table DynamicCCMonitorBankHistory (
   BankID               numeric              not null,
   PointID              numeric              not null,
   Value                float                not null,
   DateTime             datetime             not null,
   ScanInProgress       char(1)              not null
);
go


alter table DynamicCCMonitorBankHistory
   add constraint PK_DYNAMICCCMONITORBANKHISTORY primary key  (BankID, PointID);
go


alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_PTID foreign key (PointID)
      references POINT (POINTID);
go


alter table DynamicCCMonitorBankHistory
   add constraint FK_DYN_CCMONBNKHIST_BNKID foreign key (BankID)
      references CAPBANK (DEVICEID);
go

/*==============================================================*/
/* Table: DynamicCCMonitorPointResponse                         */
/*==============================================================*/
create table DynamicCCMonitorPointResponse (
   BankID               numeric              not null,
   PointID              numeric              not null,
   PreOpValue           float                not null,
   Delta                numeric              not null
);
go


alter table DynamicCCMonitorPointResponse
   add constraint PK_DYNAMICCCMONITORPOINTRESPON primary key  (BankID, PointID);
go


alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_BNKID foreign key (BankID)
      references DynamicCCCapBank (CapBankID);
go


alter table DynamicCCMonitorPointResponse
   add constraint FK_DYN_CCMONPTRSP_PTID foreign key (PointID)
      references POINT (POINTID);
go

/*==============================================================*/
/* Table: CCEventLog                                            */
/*==============================================================*/
create table CCEventLog  (
   LogID                numeric                          not null,
   PointID              numeric                          not null,
   DateTime             datetime                          not null,
   SubID                numeric                          not null,
   FeederID             numeric                         not null,
   EventType            numeric                          not null,
   SeqID                numeric                         not null,
   Value                numeric                         not null,
   Text                 VARCHAR(120)                    not null,
   UserName             VARCHAR(64)                     not null
);
go

alter table CCEventLog
   add constraint PK_CCEventLog primary key (LogID);
go

/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create view DISPLAY2WAYDATA_VIEW as
select POINTID as PointID, POINTNAME as PointName, POINTTYPE as PointType, SERVICEFLAG as PointState, YukonPAObject.PAOName as DeviceName, YukonPAObject.Type as DeviceType, YukonPAObject.Description as DeviceCurrentState, YukonPAObject.PAObjectID as DeviceID, '**DYNAMIC**' as PointValue, '**DYNAMIC**' as PointQuality, '**DYNAMIC**' as PointTimeStamp, (select uomname from pointunit,unitmeasure where pointunit.pointid=point.pointid and pointunit.uomid=unitmeasure.uomid) as UofM, '**DYNAMIC**' as Tags
from YukonPAObject, POINT
where YukonPAObject.PAObjectID = POINT.PAObjectID;
go


/*==============================================================*/
/* View: ExpressComAddress_View                                 */
/*==============================================================*/
create view ExpressComAddress_View as
select x.LMGroupID, x.RouteID, x.SerialNumber, s.Address as serviceaddress,
g.Address as geoaddress, b.Address as substationaddress, f.Address as feederaddress,
z.Address as ZipCodeAddress, us.Address as UDAddress, p.Address as programaddress, sp.Address as SplinterAddress, x.AddressUsage, x.RelayUsage
from LMGroupExpressCom x, LMGroupExpressComAddress s, 
LMGroupExpressComAddress g, LMGroupExpressComAddress b, LMGroupExpressComAddress f,
LMGroupExpressComAddress p,
LMGroupExpressComAddress sp, LMGroupExpressComAddress us, LMGroupExpressComAddress z
where ( x.ServiceProviderID = s.AddressID and ( s.AddressType = 'SERVICE' or s.AddressID = 0 ) )
and ( x.FeederID = f.AddressID and ( f.AddressType = 'FEEDER' or f.AddressID = 0 ) )
and ( x.GeoID = g.AddressID and ( g.AddressType = 'GEO' or g.AddressID = 0 ) )
and ( x.ProgramID = p.AddressID and ( p.AddressType = 'PROGRAM' or p.AddressID = 0 ) )
and ( x.SubstationID = b.AddressID and ( b.AddressType = 'SUBSTATION' or b.AddressID = 0 ) )
and ( x.SplinterID = sp.AddressID and ( sp.AddressType = 'SPLINTER' or sp.AddressID = 0 ) )
and ( x.UserID = us.AddressID and ( us.AddressType = 'USER' or us.AddressID = 0 ) )
and ( x.ZipID = z.AddressID and ( z.AddressType = 'ZIP' or z.AddressID = 0 ) );
go


/*==============================================================*/
/* View: FeederAddress_View                                     */
/*==============================================================*/
create view FeederAddress_View as
select x.LMGroupID, a.Address as FeederAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.FeederID = a.AddressID and ( a.AddressType = 'FEEDER' or a.AddressID = 0 ) );
go


/*==============================================================*/
/* View: FullEventLog_View                                      */
/*==============================================================*/
create view FullEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, DeviceName, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, y.PAOName, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from YukonPAObject y, POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID;
go


/*==============================================================*/
/* View: FullPointHistory_View                                  */
/*==============================================================*/
create view FullPointHistory_View (PointID, DeviceName, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, y.PAOName, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from YukonPAObject y, POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID and p.PAObjectID = y.PAObjectID;
go


/*==============================================================*/
/* View: GeoAddress_View                                        */
/*==============================================================*/
create view GeoAddress_View as
select x.LMGroupID, a.Address as GeoAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.GeoID = a.AddressID and ( a.AddressType = 'GEO' or a.AddressID = 0 ) );
go


/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
where prog.CurtailReferenceID = cust.CurtailReferenceID;
go


/*==============================================================*/
/* View: LMProgram_View                                         */
/*==============================================================*/
create view LMProgram_View as
select t.DeviceID, t.ControlType, u.ConstraintID, u.ConstraintName, u.AvailableWeekDays, u.MaxHoursDaily, u.MaxHoursMonthly, u.MaxHoursSeasonal, u.MaxHoursAnnually, u.MinActivateTime, u.MinRestartTime, u.MaxDailyOps, u.MaxActivateTime, u.HolidayScheduleID, u.SeasonScheduleID
from LMPROGRAM t, LMProgramConstraints u
where u.ConstraintID = t.ConstraintID;
go


/*==============================================================*/
/* View: Peakpointhistory_View                                  */
/*==============================================================*/
create view Peakpointhistory_View as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where VALUE in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by POINTID, VALUE;
go


/*==============================================================*/
/* View: PointEventLog_View                                     */
/*==============================================================*/
create view PointEventLog_View (EventID, PointID, EventTimeStamp, EventSequence, EventType, EventAlarmID, PointName, EventDescription, AdditionalInfo, EventUserName) as
select s.LOGID, s.POINTID, s.DATETIME, s.SOE_TAG, s.TYPE, s.PRIORITY, p.POINTNAME, s.DESCRIPTION, s.ACTION, s.USERNAME
from POINT p, SYSTEMLOG s
where s.POINTID = p.POINTID;
go


/*==============================================================*/
/* View: PointHistory_View                                      */
/*==============================================================*/
create view PointHistory_View (PointID, PointName, DataValue, DataTimeStamp, DataQuality) as
select r.POINTID, p.POINTNAME, r.VALUE, r.TIMESTAMP, r.QUALITY
from POINT p, RAWPOINTHISTORY r
where r.POINTID = p.POINTID;
go


/*==============================================================*/
/* View: ProgramAddress_View                                    */
/*==============================================================*/
create view ProgramAddress_View as
select x.LMGroupID, a.Address as ProgramAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ProgramID = a.AddressID and ( a.AddressType = 'PROGRAM' or a.AddressID = 0 ) );
go


/*==============================================================*/
/* View: ServiceAddress_View                                    */
/*==============================================================*/
create view ServiceAddress_View as
select x.LMGroupID, a.Address as ServiceAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.ServiceProviderID = a.AddressID and ( a.AddressType = 'SERVICE' or a.AddressID = 0 ) );
go


/*==============================================================*/
/* View: SubstationAddress_View                                 */
/*==============================================================*/
create view SubstationAddress_View as
select x.LMGroupID, a.Address as SubstationAddress
from LMGroupExpressCom x, LMGroupExpressComAddress a
where ( x.SubstationID = a.AddressID and ( a.AddressType = 'SUBSTATION' or a.AddressID = 0 ) );
go


alter table AlarmCategory
   add constraint FK_ALRMCAT_NOTIFGRP foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table CALCBASE
   add constraint SYS_C0013434 foreign key (POINTID)
      references POINT (POINTID);
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID);
go


alter table CAPBANK
   add constraint SYS_C0013453 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table CAPBANK
   add constraint SYS_C0013454 foreign key (CONTROLPOINTID)
      references POINT (POINTID);
go


alter table CAPBANK
   add constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
      references DEVICE (DEVICEID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CCSUBB_CCSTR foreign key (StrategyID)
      references CapControlStrategy (StrategyID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CpSbBus_YPao foreign key (SubstationBusID)
      references YukonPAObject (PAObjectID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013478 foreign key (CurrentWattLoadPointID)
      references POINT (POINTID);
go


alter table CAPCONTROLSUBSTATIONBUS
   add constraint SYS_C0013479 foreign key (CurrentVarLoadPointID)
      references POINT (POINTID);
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_SWPTID foreign key (SwitchPointID)
      references POINT (POINTID);
go

alter table CAPCONTROLSUBSTATIONBUS
   add constraint FK_CAPCONTR_CVOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID);
go

alter table CCFeederBankList
   add constraint FK_CB_CCFeedLst foreign key (DeviceID)
      references CAPBANK (DEVICEID);
go


alter table CCFeederBankList
   add constraint FK_CCFeed_CCBnk foreign key (FeederID)
      references CapControlFeeder (FeederID);
go


alter table CCFeederSubAssignment
   add constraint FK_CCFeed_CCFass foreign key (FeederID)
      references CapControlFeeder (FeederID);
go


alter table CCFeederSubAssignment
   add constraint FK_CCSub_CCFeed foreign key (SubStationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);
go


alter table CICUSTOMERPOINTDATA
   add constraint FK_CICstPtD_CICst foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table CICUSTOMERPOINTDATA
   add constraint FK_CICUSTOM_REF_CICST_POINT foreign key (PointID)
      references POINT (POINTID);
go


alter table CICustomerBase
   add constraint FK_CICstBas_CstAddrs foreign key (MainAddressID)
      references Address (AddressID);
go


alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID);
go


alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table CapControlFeeder
   add constraint FK_PAObj_CCFeed foreign key (FeederID)
      references YukonPAObject (PAObjectID);
go

alter table CapControlFeeder
   add constraint FK_CCFDR_CCSTR foreign key (StrategyID)
      references CapControlStrategy (StrategyID);
go

alter table CapControlFeeder
   add constraint FK_CAPCONTR_VARPTID foreign key (CurrentVarLoadPointID)
      references POINT (POINTID);
go


alter table CapControlFeeder
   add constraint FK_CAPCONTR_VOLTPTID foreign key (CurrentVoltLoadPointID)
      references POINT (POINTID);
go


alter table CapControlFeeder
   add constraint FK_CAPCONTR_WATTPTID foreign key (CurrentWattLoadPointID)
      references POINT (POINTID);
go

alter table CarrierRoute
   add constraint SYS_C0013264 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table CommErrorHistory
   add constraint FK_ComErrHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table CommPort
   add constraint FK_COMMPORT_REF_COMPO_YUKONPAO foreign key (PORTID)
      references YukonPAObject (PAObjectID);
go


alter table ConfigurationParts
   add constraint FK_ConfPart_ConfName foreign key (ConfigID)
      references ConfigurationName (ConfigID);
go


alter table ConfigurationParts
   add constraint FK_ConfPart_ConfPartName foreign key (PartID)
      references ConfigurationPartsName (PartID)
go


alter table ConfigurationValue
   add constraint FK_ConfVal_ConfPart foreign key (PartID)
      references ConfigurationPartsName (PartID);
go


alter table Contact
   add constraint FK_RefCstLg_CustCont foreign key (LogInID)
      references YukonUser (UserID);
go


alter table Contact
   add constraint FK_CONTACT_REF_CNT_A_ADDRESS foreign key (AddressID)
      references Address (AddressID);
go


alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM foreign key (ContactID)
      references Contact (ContactID);
go


alter table ContactNotifGroupMap
   add constraint FK_CNTNOFGM_NTFG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID);
go


alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID);
go


alter table Customer
   add constraint FK_Cust_YkLs foreign key (RateScheduleID)
      references YukonListEntry (EntryID);
go


alter table Customer
   add constraint FK_Cst_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);
go


alter table CustomerAdditionalContact
   add constraint FK_CstCont_CICstCont foreign key (ContactID)
      references Contact (ContactID);
go


alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_CsL foreign key (LoginID)
      references YukonUser (UserID);
go


alter table CustomerLoginSerialGroup
   add constraint FK_CsLgSG_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);
go


alter table CustomerNotifGroupMap
   add constraint FK_CST_CSTNOFGM foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table CustomerNotifGroupMap
   add constraint FK_NTFG_CSTNOFGM foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);
go


alter table DEVICE2WAYFLAGS
   add constraint SYS_C0013208 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICECARRIERSETTINGS
   add constraint SYS_C0013216 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEDIALUPSETTINGS
   add constraint SYS_C0013193 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEIDLCREMOTE
   add constraint SYS_C0013241 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEIED
   add constraint SYS_C0013245 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICELOADPROFILE
   add constraint SYS_C0013234 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEMCTIEDPORT
   add constraint SYS_C0013253 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICEMETERGROUP
   add constraint SYS_C0013213 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DEVICETAPPAGINGSETTINGS
   add constraint SYS_C0013237 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DISPLAY2WAYDATA
   add constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
      references POINT (POINTID);
go


alter table DISPLAY2WAYDATA
   add constraint SYS_C0013422 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);
go


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013418 foreign key (DISPLAYNUM)
      references DISPLAY (DISPLAYNUM);
go


alter table DISPLAYCOLUMNS
   add constraint SYS_C0013419 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);
go


alter table DYNAMICACCUMULATOR
   add constraint SYS_C0015129 foreign key (POINTID)
      references POINT (POINTID);
go


alter table DYNAMICDEVICESCANDATA
   add constraint SYS_C0015139 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DYNAMICPOINTDISPATCH
   add constraint SYS_C0013331 foreign key (POINTID)
      references POINT (POINTID);
go


alter table DateOfHoliday
   add constraint FK_HolSchID foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);
go


alter table DateOfSeason
   add constraint FK_DaOfSe_SeSc foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);
go


alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DeviceCBC
   add constraint SYS_C0013460 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table DeviceConfiguration
   add constraint FK_DevConf_ConfName foreign key (ConfigID)
      references ConfigurationName (ConfigID);
go


alter table DeviceConfiguration
   add constraint FK_DevConf_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);
go


alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DeviceDirectCommSettings
   add constraint SYS_C0013187 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table DeviceMCT400Series
   add constraint FK_Dev4_DevC foreign key (DeviceID)
      references DEVICECARRIERSETTINGS (DEVICEID);
go


alter table DeviceMCT400Series
   add constraint FK_Dev4_TOU foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go


alter table DevicePagingReceiverSettings
   add constraint FK_DevPaRec_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DeviceRTC
   add constraint FK_Dev_DevRTC foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DeviceRoutes
   add constraint SYS_C0013219 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table DeviceRoutes
   add constraint SYS_C0013220 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table DeviceSeries5RTU
   add constraint FK_DvS5r_Dv2w foreign key (DeviceID)
      references DEVICE2WAYFLAGS (DEVICEID);
go


alter table DeviceTNPPSettings
   add constraint FK_DevTNPP_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DeviceTypeCommand
   add constraint FK_DevCmd_Cmd foreign key (CommandID)
      references Command (CommandID);
go


alter table DeviceTypeCommand
   add constraint "FK_DevCmd_Grp " foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);
go


alter table DeviceVerification
   add constraint FK_DevV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);
go


alter table DeviceVerification
   add constraint FK_DevV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);
go


alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table DynamicCCCapBank
   add constraint FK_CpBnk_DynCpBnk foreign key (CapBankID)
      references CAPBANK (DEVICEID);
go


alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID);
go


alter table DynamicCCSubstationBus
   add constraint FK_CCSubBs_DySubBs foreign key (SubstationBusID)
      references CAPCONTROLSUBSTATIONBUS (SubstationBusID);
go


alter table DynamicCalcHistorical
   add constraint FK_DynClc_ClcB foreign key (PointID)
      references CALCBASE (POINTID);
go


alter table DynamicLMControlArea
   add constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
      references LMControlArea (DEVICEID);
go


alter table DynamicLMControlAreaTrigger
   add constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
      references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER);
go


alter table DynamicLMControlHistory
   add constraint FK_DYNLMCNT_PAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table DynamicLMGroup
   add constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
      references LMGroup (DeviceID);
go


alter table DynamicLMProgram
   add constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
      references LMPROGRAM (DeviceID);
go


alter table DynamicLMProgramDirect
   add constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
go


alter table DynamicPAOInfo
   add constraint FK_DynPAOInfo_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table DynamicPAOStatistics
   add constraint FK_PASt_YkPA foreign key (PAOBjectID)
      references YukonPAObject (PAObjectID);
go


alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table DynamicPointAlarming
   add constraint FKf_DynPtAl_SysL foreign key (LogID)
      references SYSTEMLOG (LOGID);
go


alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table DynamicTags
   add constraint FK_DYNAMICT_REF_DYNTG_TAGS foreign key (TagID)
      references Tags (TagID);
go


alter table DynamicVerification
   add constraint FK_DynV_Dev1 foreign key (ReceiverID)
      references DEVICE (DEVICEID);
go


alter table DynamicVerification
   add constraint FK_DynV_Dev2 foreign key (TransmitterID)
      references DEVICE (DEVICEID);
go


alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);
go


alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID);
go


alter table EnergyCompanyCustomerList
   add constraint FK_CICstBsEnCmpCsLs foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table EnergyCompanyCustomerList
   add constraint FK_EnCmpEnCmpCsLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_EnCmpEnCmpOpLs foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);
go


alter table EnergyCompanyOperatorLoginList
   add constraint FK_OpLgEnCmpOpLs foreign key (OperatorLoginID)
      references YukonUser (UserID);
go


alter table FDRInterfaceOption
   add constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
      references FDRInterface (InterfaceID);
go


alter table FDRTRANSLATION
   add constraint SYS_C0015066 foreign key (POINTID)
      references POINT (POINTID);
go


alter table GRAPHDATASERIES
   add constraint GrphDSeri_GrphDefID foreign key (GRAPHDEFINITIONID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);
go


alter table GRAPHDATASERIES
   add constraint GrphDSeris_ptID foreign key (POINTID)
      references POINT (POINTID);
go


alter table GraphCustomerList
   add constraint FK_GRAPHCUS_REFGRPHCU_GRAPHDEF foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);
go


alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID);
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
      references LMControlArea (DEVICEID);
go


alter table LMCONTROLAREAPROGRAM
   add constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
      references LMPROGRAM (DeviceID);
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
      references LMControlArea (DEVICEID);
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCntlArTrig foreign key (POINTID)
      references POINT (POINTID);
go


alter table LMCONTROLAREATRIGGER
   add constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
      references POINT (POINTID);
go


alter table LMControlArea
   add constraint FK_LmCntAr_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);
go


alter table LMControlHistory
   add constraint FK_LmCtrlHis_YPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table LMControlScenarioProgram
   add constraint FK_LmCScP_YkPA foreign key (ScenarioID)
      references YukonPAObject (PAObjectID);
go


alter table LMControlScenarioProgram
   add constraint FK_LMCONTRO_REF_LMSCP_LMPROGRA foreign key (ProgramID)
      references LMPROGRAM (DeviceID);
go


alter table LMCurtailCustomerActivity
   add constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMCurtailCustomerActivity
   add constraint FK_LMCURTAI_REFLMCST__LMCURTAI foreign key (CurtailReferenceID)
      references LMCurtailProgramActivity (CurtailReferenceID);
go


alter table LMCurtailProgramActivity
   add constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
      references LMProgramCurtailment (DeviceID);
go


alter table LMDirectCustomerList
   add constraint FK_CICstB_LMPrDi foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMDirectCustomerList
   add constraint FK_LMDIRECT_REFLMPDIR_LMPROGRA foreign key (ProgramID)
      references LMProgramDirect (DeviceID);
go


alter table LMDirectNotifGrpList
   add constraint FK_LMDi_DNGrpL foreign key (ProgramID)
      references LMProgramDirect (DeviceID);
go


alter table LMDirectNotifGrpList
   add constraint FK_NtGr_DNGrpL foreign key (NotificationGrpID)
      references NotificationGroup (NotificationGroupID);
go


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMEnergyExchangeCustomerList
   add constraint FK_ExCsLs_PrEx foreign key (ProgramID)
      references LMProgramEnergyExchange (DeviceID);
go


alter table LMEnergyExchangeCustomerReply
   add constraint FK_ExCsRp_CstBs foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMEnergyExchangeCustomerReply
   add constraint FK_LMENERGY_REFEXCSTR_LMENERGY foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber)
      on delete cascade;
go


alter table LMEnergyExchangeHourlyCustomer
   add constraint FK_ExHrCs_ExCsRp foreign key (CustomerID, OfferID, RevisionNumber)
      references LMEnergyExchangeCustomerReply (CustomerID, OfferID, RevisionNumber)
      on delete cascade;
go


alter table LMEnergyExchangeHourlyOffer
   add constraint FK_ExHrOff_ExOffRv foreign key (OfferID, RevisionNumber)
      references LMEnergyExchangeOfferRevision (OfferID, RevisionNumber);
go


alter table LMEnergyExchangeOfferRevision
   add constraint FK_EExOffR_ExPrOff foreign key (OfferID)
      references LMEnergyExchangeProgramOffer (OfferID);
go


alter table LMEnergyExchangeProgramOffer
   add constraint FK_EnExOff_PrgEnEx foreign key (DeviceID)
      references LMProgramEnergyExchange (DeviceID);
go


alter table LMGroup
   add constraint FK_Device_LMGrpBase2 foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table LMGroupEmetcon
   add constraint SYS_C0013356 foreign key (DEVICEID)
      references LMGroup (DeviceID);
go


alter table LMGroupEmetcon
   add constraint SYS_C0013357 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCG_LMExCm foreign key (GeoID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCP_LMExCm foreign key (ProgramID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCSb_LMExCm foreign key (SubstationID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCSp_LMExCm foreign key (ServiceProviderID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_ExCad_LMExCm foreign key (FeederID)
      references LMGroupExpressComAddress (AddressID);
go


alter table LMGroupExpressCom
   add constraint FK_LGrEx_LMG foreign key (LMGroupID)
      references LMGroup (DeviceID);
go


alter table LMGroupExpressCom
   add constraint FK_LGrEx_Rt foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID);
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
      references DEVICE (DEVICEID);
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
      references LMGroup (DeviceID);
go


alter table LMGroupPoint
   add constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
      references POINT (POINTID);
go


alter table LMGroupRipple
   add constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
      references LMGroup (DeviceID);
go


alter table LMGroupRipple
   add constraint FK_LmGrpRip_Rout foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupSA205105
   add constraint FK_LGrS205_LmG foreign key (GroupID)
      references LMGroup (DeviceID);
go


alter table LMGroupSA205105
   add constraint FK_LGrS205_Rt foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupSA305
   add constraint FK_LGrS305_LmGrp foreign key (GroupID)
      references LMGroup (DeviceID);
go


alter table LMGroupSA305
   add constraint FK_LGrS305_Rt foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupSASimple
   add constraint FK_LmGrSa_LmG foreign key (GroupID)
      references LMGroup (DeviceID);
go


alter table LMGroupSASimple
   add constraint FK_LmGrSa_Rt foreign key (RouteID)
      references Route (RouteID);
go


alter table LMGroupVersacom
   add constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
      references LMGroup (DeviceID);
go


alter table LMGroupVersacom
   add constraint SYS_C0013367 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McSchCstLst_MCSched foreign key (ScheduleID)
      references MACSchedule (ScheduleID);
go


alter table LMMacsScheduleCustomerList
   add constraint FK_McsSchdCusLst_CICBs foreign key (LMCustomerDeviceID)
      references CICustomerBase (CustomerID);
go


alter table LMPROGRAM
   add constraint FK_LMPr_PrgCon foreign key (ConstraintID)
      references LMProgramConstraints (ConstraintID);
go


alter table LMPROGRAM
   add constraint FK_LmProg_YukPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);
go


alter table LMProgramConstraints
   add constraint FK_HlSc_LmPrC foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);
go


alter table LMProgramConstraints
   add constraint FK_SesSch_LmPrC foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);
go


alter table LMProgramControlWindow
   add constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
      references LMPROGRAM (DeviceID);
go


alter table LMProgramCurtailCustomerList
   add constraint FK_CICstBase_LMProgCList foreign key (CustomerID)
      references CICustomerBase (CustomerID);
go


alter table LMProgramCurtailCustomerList
   add constraint FK_LMPrgCrt_LMPrCstLst foreign key (ProgramID)
      references LMProgramCurtailment (DeviceID);
      on delete cascade
go


alter table LMProgramCurtailment
   add constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
      references LMPROGRAM (DEVICEID);
go


alter table LMProgramDirect
   add constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
      references LMPROGRAM (DeviceID);
go


alter table LMProgramDirectGear
   add constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
go


alter table LMProgramDirectGroup
   add constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
      references LMGroup (DeviceID);
go


alter table LMProgramDirectGroup
   add constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
      references LMProgramDirect (DeviceID);
go


alter table LMProgramEnergyExchange
   add constraint FK_LmPrg_LmPrEEx foreign key (DeviceID)
      references LMPROGRAM (DeviceID);
go


alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID);
go


alter table MACROROUTE
   add constraint SYS_C0013274 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table MACROROUTE
   add constraint SYS_C0013275 foreign key (SINGLEROUTEID)
      references Route (RouteID);
go


alter table MACSchedule
   add constraint FK_SchdID_PAOID foreign key (ScheduleID)
      references YukonPAObject (PAObjectID);
go


alter table MACSimpleSchedule
   add constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
      references MACSchedule (ScheduleID);
go


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID);
go


alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPMCT foreign key (MctID)
      references DEVICE (DEVICEID);
go


alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID);
go


alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID);
go


alter table NotificationDestination
   add constraint FK_NotifDest_NotifGrp foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID);
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs foreign key (GraphDefinitionID)
      references GRAPHDEFINITION (GRAPHDEFINITIONID);
go


alter table OperatorLoginGraphList
   add constraint FK_OpLgOpLgGrLs2 foreign key (OperatorLoginID)
      references YukonUser (UserID);
go


alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go


alter table PAOExclusion
   add constraint FK_PAOEXCLU_REF_PAOEX_YUKONPAO foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID);
go


alter table PAOScheduleAssignment
   add constraint FK_PAOSCHASS_PAOSCH foreign key (ScheduleID)
      references PAOSchedule (ScheduleID);
go


alter table PAOScheduleAssignment
   add constraint FK_PAOSch_YukPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go


alter table PAOowner
   add constraint FK_YukPAO_PAOOwn foreign key (ChildID)
      references YukonPAObject (PAObjectID);
go


alter table PAOowner
   add constraint FK_YukPAO_PAOid foreign key (OwnerID)
      references YukonPAObject (PAObjectID);
go


alter table POINT
   add constraint FK_Pt_YukPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go


alter table POINT
   add constraint Ref_STATGRP_PT foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID);
go


alter table POINTACCUMULATOR
   add constraint SYS_C0013317 foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTANALOG
   add constraint SYS_C0013300 foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTLIMITS
   add constraint SYS_C0013289 foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTSTATUS
   add constraint Ref_ptstatus_pt foreign key (POINTID)
      references POINT (POINTID);
go


alter table POINTUNIT
   add constraint FK_PtUnit_UoM foreign key (UOMID)
      references UNITMEASURE (UOMID);
go


alter table POINTUNIT
   add constraint Ref_ptunit_point foreign key (POINTID)
      references POINT (POINTID);
go


alter table PORTDIALUPMODEM
   add constraint SYS_C0013175 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTLOCALSERIAL
   add constraint SYS_C0013147 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTRADIOSETTINGS
   add constraint SYS_C0013169 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTSETTINGS
   add constraint SYS_C0013156 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PORTTERMINALSERVER
   add constraint SYS_C0013151 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table PointAlarming
   add constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
      references POINT (POINTID);
go


alter table PointAlarming
   add constraint FK_POINTALARMING foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);
go


alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID);
go


alter table PortTiming
   add constraint SYS_C0013163 foreign key (PORTID)
      references CommPort (PORTID);
go


alter table RAWPOINTHISTORY
   add constraint FK_RawPt_Point foreign key (POINTID)
      references POINT (POINTID);
go


alter table RepeaterRoute
   add constraint SYS_C0013269 foreign key (ROUTEID)
      references Route (RouteID);
go


alter table RepeaterRoute
   add constraint SYS_C0013270 foreign key (DEVICEID)
      references DEVICE (DEVICEID);
go


alter table Route
   add constraint FK_Route_DevID foreign key (DeviceID)
      references DEVICE (DEVICEID);
go


alter table Route
   add constraint FK_Route_YukPAO foreign key (RouteID)
      references YukonPAObject (PAObjectID);
go


alter table STATE
   add constraint FK_YkIm_St foreign key (ImageID)
      references YukonImage (ImageID);
go


alter table STATE
   add constraint SYS_C0013342 foreign key (STATEGROUPID)
      references STATEGROUP (STATEGROUPID);
go


alter table SYSTEMLOG
   add constraint SYS_C0013408 foreign key (POINTID)
      references POINT (POINTID);
go


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013429 foreign key (TEMPLATENUM)
      references TEMPLATE (TEMPLATENUM);
go


alter table TEMPLATECOLUMNS
   add constraint SYS_C0013430 foreign key (TYPENUM)
      references COLUMNTYPE (TYPENUM);
go


alter table TOUDayMapping
   add constraint FK_TOUd_TOUSc foreign key (TOUScheduleID)
      references TOUSchedule (TOUScheduleID);
go


alter table TOUDayMapping
   add constraint FK_TOUm_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go


alter table TOUDayRateSwitches
   add constraint FK_TOUdRS_TOUd foreign key (TOUDayID)
      references TOUDay (TOUDayID);
go


alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID);
go


alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID);
go


alter table UserPAOowner
   add constraint FK_UsPow_YkP foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go


alter table UserPAOowner
   add constraint FK_UsPow_YkUsr foreign key (UserID)
      references YukonUser (UserID);
go


alter table VersacomRoute
   add constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
      references Route (RouteID);
go


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID);
go


alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID);
go


alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);
go


alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID);
go


alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID);
go


alter table YukonUserGroup
   add constraint FK_YkUsGr_YkGr foreign key (GroupID)
      references YukonGroup (GroupID);
go


alter table YukonUserGroup
   add constraint FK_YUKONUSE_REF_YKUSG_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);
go


alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);
go


alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID);
go


alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID);
go

/* @error ignore-end */


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Ryan', '13-MARCH-2005', 'Manual version insert done', 3 );
