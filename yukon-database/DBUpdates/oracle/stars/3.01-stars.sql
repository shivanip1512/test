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
