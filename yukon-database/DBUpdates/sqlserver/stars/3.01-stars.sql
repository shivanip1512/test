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
