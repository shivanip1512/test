alter table LMHardwareBase add RouteID number;
update LMHardwareBase set RouteID = 0;
alter table LMHardwareBase
   add constraint FK_LMHrdB_Rt foreign key (RouteID)
      references Route (RouteID);


create table LMThermostatSchedule (
ScheduleID           NUMBER               not null,
ScheduleName         VARCHAR2(60)         not null,
ThermostatTypeID     NUMBER               not null,
AccountID            NUMBER               not null,
InventoryID          NUMBER               not null
);

alter table LMThermostatSchedule
   add constraint PK_LMTHERMOSTATSCHEDULE primary key (ScheduleID);
alter table LMThermostatSchedule
   add constraint FK_LMThSc_YkLs foreign key (ThermostatTypeID)
      references YukonListEntry (EntryID);
alter table LMThermostatSchedule
   add constraint FK_LMThSc_CsAc foreign key (AccountID)
      references CustomerAccount (AccountID);
alter table LMThermostatSchedule
   add constraint FK_LMThSc_InvB foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table LMThermostatSeason drop constraint FK_InvB_LThSs;

insert into LMThermostatSchedule
select distinct(inv.InventoryID), '(none)', hw.LMHardwareTypeID, inv.AccountID, inv.InventoryID
from LMThermostatSeason sn, InventoryBase inv, LMHardwareBase hw
where sn.InventoryID = inv.InventoryID and inv.InventoryID = hw.InventoryID and inv.InventoryID > 0;

insert into LMThermostatSchedule
select distinct(inv.InventoryID), '(none)', hw.LMHardwareTypeID, inv.AccountID, 0
from LMThermostatSeason sn, InventoryBase inv, LMHardwareBase hw
where sn.InventoryID = inv.InventoryID and inv.InventoryID = hw.InventoryID and inv.InventoryID < 0;

alter table LMThermostatSeason rename column InventoryID to ScheduleID;
alter table LMThermostatSeason
   add constraint FK_ThSc_LThSs foreign key (ScheduleID)
      references LMThermostatSchedule (ScheduleID);

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
alter table LMProgramWebPublishing rename column LMProgramID to DeviceID;

alter table LMProgramWebPublishing add ProgramID number;
update LMProgramWebPublishing set ProgramID = DeviceID;
alter table LMProgramWebPublishing modify ProgramID number not null;
alter table LMProgramWebPublishing
   add constraint PK_LMPROGRAMWEBPUBLISHING primary key (ProgramID);

insert into ApplianceCategory values (0,'(none)',0,0);
insert into LMProgramWebPublishing values (0,0,0,0,0,0);

alter table ApplianceBase drop constraint FK_AppBs_LMPr;
alter table ApplianceBase rename column LMProgramID to ProgramID;
alter table ApplianceBase
   add constraint FK_AppBs_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID);

alter table LMProgramEvent drop constraint FK_LMPrg_LMPrEv;
alter table LMProgramEvent rename column LMProgramID to ProgramID;
alter table LMProgramEvent
   add constraint FK_LMPrEv_LMPrPub foreign key (ProgramID)
      references LMProgramWebPublishing (ProgramID)

alter table LMHardwareConfiguration add LoadNumber number;
update LMHardwareConfiguration set LoadNumber=0;



create table LMConfigurationBase (
ConfigurationID      NUMBER               not null,
ColdLoadPickup       VARCHAR2(100)        not null,
TamperDetect         VARCHAR2(100)        not null
);

insert into LMConfigurationBase values (0, '(none)', '(none)');
alter table LMConfigurationBase
   add constraint PK_LMCONFIGURATIONBASE primary key (ConfigurationID);

create table LMConfigurationSA205 (
ConfigurationID      NUMBER               not null,
Slot1                NUMBER               not null,
Slot2                NUMBER               not null,
Slot3                NUMBER               not null,
Slot4                NUMBER               not null,
Slot5                NUMBER               not null,
Slot6                NUMBER               not null
);

alter table LMConfigurationSA205
   add constraint PK_LMCONFIGURATIONSA205 primary key (ConfigurationID);
alter table LMConfigurationSA205
   add constraint FK_LMCfg205_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

create table LMConfigurationSA305 (
ConfigurationID      NUMBER               not null,
Utility              NUMBER               not null,
GroupAddress         NUMBER               not null,
Division             NUMBER               not null,
Substation           NUMBER               not null,
RateFamily           NUMBER               not null,
RateMember           NUMBER               not null,
RateHierarchy        NUMBER               not null
);

alter table LMConfigurationSA305
   add constraint PK_LMCONFIGURATIONSA305 primary key (ConfigurationID);
alter table LMConfigurationSA305
   add constraint FK_LMCfg305_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

create table LMConfigurationExpressCom (
ConfigurationID      NUMBER               not null,
ServiceProvider      NUMBER               not null,
GEO                  NUMBER               not null,
Substation           NUMBER               not null,
Feeder               NUMBER               not null,
Zip                  NUMBER               not null,
UserAddress          NUMBER               not null,
Program              VARCHAR2(80)         not null,
Splinter             VARCHAR2(80)         not null
);

alter table LMConfigurationExpressCom
   add constraint PK_LMCONFIGURATIONEXPRESSCOM primary key (ConfigurationID);
alter table LMConfigurationExpressCom
   add constraint FK_LMCfgXcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

create table LMConfigurationVersaCom (
ConfigurationID      NUMBER               not null,
UtilityID            NUMBER               not null,
Section              NUMBER               not null,
ClassAddress         NUMBER               not null,
DivisionAddress      NUMBER               not null
);

alter table LMConfigurationVersaCom
   add constraint PK_LMCONFIGURATIONVERSACOM primary key (ConfigurationID);
alter table LMConfigurationVersaCom
   add constraint FK_LMCfgVcom_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);

alter table LMHardwareBase add ConfigurationID number;
update LMHardwareBase set ConfigurationID = 0;
alter table LMHardwareBase
   add constraint FK_LMHrdB_LMCfg foreign key (ConfigurationID)
      references LMConfigurationBase (ConfigurationID);
