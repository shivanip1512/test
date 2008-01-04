alter table Invoice add AuthorizedNumber varchar(60);
update Invoice set AuthorizedNumber = '';
alter table Invoice alter column AuthorizedNumber varchar(60) not null;

/* Start YUK-5001 */
/* @error ignore-begin */
create table LMHardwareToMeterMapping
 (
   LMHardwareInventoryID numeric not null,
   MeterInventoryID numeric not null
);

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_LMHARDBASE foreign key (LMHardwareInventoryID)
      references LMHardwareBase (InventoryID);

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_METERHARDBASE foreign key (MeterInventoryID)
      references MeterHardwareBase (InventoryID);

alter table LMHardwareToMeterMapping
   add constraint PK_LMHARDWARETOMETERMAPPING primary key (LMHardwareInventoryID, MeterInventoryID);
/* @error ignore-end */

delete from LMThermostatSeasonEntry where SeasonID in (select SeasonID from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100)));
go
delete from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
go
delete from ECToGenericMapping where MappingCategory = 'LMThermostatSchedule' and ItemID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
go
delete from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
go
update LMHardwareBase set LMHardwareTypeID = 0 where LMHardwareTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
go

/* End YUK-5001 */