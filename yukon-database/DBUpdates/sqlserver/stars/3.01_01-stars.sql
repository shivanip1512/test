alter table LMHardwareBase add RouteID numeric;
go
update LMHardwareBase set RouteID = 0;
alter table LMHardwareBase
   add constraint FK_LMHrdB_Rt foreign key (RouteID)
      references Route (RouteID);
go



create table LMThermostatSchedule (
ScheduleID           numeric              not null,
ScheduleName         varchar(60)          not null,
ThermostatTypeID     numeric              not null,
AccountID            numeric              not null,
InventoryID          numeric              not null
);
go
alter table LMThermostatSchedule
   add constraint PK_LMTHERMOSTATSCHEDULE primary key (ScheduleID);
go
alter table LMThermostatSchedule
   add constraint FK_LMThSc_YkLs foreign key (ThermostatTypeID)
      references YukonListEntry (EntryID);
go
alter table LMThermostatSchedule
   add constraint FK_LMThSc_CsAc foreign key (AccountID)
      references CustomerAccount (AccountID);
go
alter table LMThermostatSchedule
   add constraint FK_LMThSc_InvB foreign key (InventoryID)
      references InventoryBase (InventoryID);
go

alter table LMThermostatSeason drop constraint FK_InvB_LThSs;
go

insert into LMThermostatSchedule
select distinct(inv.InventoryID), '(none)', hw.LMHardwareTypeID, inv.AccountID, inv.InventoryID
from LMThermostatSeason sn, InventoryBase inv, LMHardwareBase hw
where sn.InventoryID = inv.InventoryID and inv.InventoryID = hw.InventoryID and inv.InventoryID > 0;

insert into LMThermostatSchedule
select distinct(inv.InventoryID), '(none)', hw.LMHardwareTypeID, inv.AccountID, 0
from LMThermostatSeason sn, InventoryBase inv, LMHardwareBase hw
where sn.InventoryID = inv.InventoryID and inv.InventoryID = hw.InventoryID and inv.InventoryID < 0;

sp_rename 'LMThermostatSeason.InventoryID', 'ScheduleID', 'COLUMN';
go
alter table LMThermostatSeason
   add constraint FK_ThSc_LThSs foreign key (ScheduleID)
      references LMThermostatSchedule (ScheduleID);
go

insert into ECToGenericMapping
select EnergyCompanyID, InventoryID, 'LMThermostatSchedule'
from ECToInventoryMapping where InventoryID < 0;

delete from LMThermostatManualEvent where InventoryID < 0;

delete from ECToLMCustomerEventMapping
where EventID not in (select EventID from LMHardwareEvent)
and EventID not in (select EventID from LMProgramEvent)
and EventID not in (select EventID from LMThermostatManualEvent);

delete from LMCustomerEventBase
where EventID not in (select EventID from LMHardwareEvent)
and EventID not in (select EventID from LMProgramEvent)
and EventID not in (select EventID from LMThermostatManualEvent);

delete from ECToInventoryMapping where InventoryID < 0;
delete from LMHardwareBase where InventoryID < 0;
delete from InventoryBase where InventoryID < 0;



alter table LMProgramWebPublishing drop constraint PK_LMPROGRAMWEBPUBLISHING;
go
sp_rename 'LMProgramWebPublishing.LMProgramID', 'DeviceID', 'COLUMN';
go
alter table LMProgramWebPublishing add ProgramID numeric;
go
update LMProgramWebPublishing set ProgramID = DeviceID;
go
alter table LMProgramWebPublishing alter column ProgramID numeric not null;
go
alter table LMProgramWebPublishing
   add constraint PK_LMPROGRAMWEBPUBLISHING primary key (ProgramID);
go

insert into ApplianceCategory values (0,'(none)',0,0);
go
insert into LMProgramWebPublishing values (0,0,0,0,0,0);
go

alter table ApplianceBase drop constraint FK_AppBs_LMPr;
go
sp_rename 'ApplianceBase.LMProgramID', 'ProgramID', 'COLUMN';
go
alter table ApplianceBase
   add constraint FK_AppBs_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID);
go

alter table LMProgramEvent drop constraint FK_LMPrg_LMPrEv;
go
sp_rename 'LMProgramEvent.LMProgramID', 'ProgramID', 'COLUMN';
go
alter table LMProgramEvent
   add constraint FK_LMPrEv_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID)
go

alter table LMHardwareConfiguration add LoadNumber numeric;
go
update LMHardwareConfiguration set LoadNumber=0;
go



create table LMConfigurationBase (
ConfigurationID      numeric              not null,
ColdLoadPickup       varchar(100)         not null,
TamperDetect         varchar(100)         not null
);
go
insert into LMConfigurationBase values (0, '(none)', '(none)');
go
alter table LMConfigurationBase
   add constraint PK_LMCONFIGURATIONBASE primary key (ConfigurationID);
go

create table LMConfigurationSA205 (
ConfigurationID      numeric              not null,
Slot1                numeric              not null,
Slot2                numeric              not null,
Slot3                numeric              not null,
Slot4                numeric              not null,
Slot5                numeric              not null,
Slot6                numeric              not null
);
go
alter table LMConfigurationSA205
   add constraint PK_LMCONFIGURATIONSA205 primary key (ConfigurationID);
go
alter table LMConfigurationSA205
   add constraint FK_LMCfg205_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
go

create table LMConfigurationSA305 (
ConfigurationID      numeric              not null,
Utility              numeric              not null,
GroupAddress         numeric              not null,
Division             numeric              not null,
Substation           numeric              not null,
RateFamily           numeric              not null,
RateMember           numeric              not null,
RateHierarchy        numeric              not null
);
go
alter table LMConfigurationSA305
   add constraint PK_LMCONFIGURATIONSA305 primary key (ConfigurationID);
go
alter table LMConfigurationSA305
   add constraint FK_LMCfg305_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
go

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
);
go
alter table LMConfigurationExpressCom
   add constraint PK_LMCONFIGURATIONEXPRESSCOM primary key (ConfigurationID);
go
alter table LMConfigurationExpressCom
   add constraint FK_LMCfgXcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
go

create table LMConfigurationVersaCom (
ConfigurationID      numeric              not null,
UtilityID            numeric              not null,
Section              numeric              not null,
ClassAddress         numeric              not null,
DivisionAddress      numeric              not null
);
go
alter table LMConfigurationVersaCom
   add constraint PK_LMCONFIGURATIONVERSACOM primary key (ConfigurationID);
go
alter table LMConfigurationVersaCom
   add constraint FK_LMCfgVcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
go

alter table LMHardwareBase add ConfigurationID numeric;
go
update LMHardwareBase set ConfigurationID = 0;
alter table LMHardwareBase
   add constraint FK_LMHrdB_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
go
