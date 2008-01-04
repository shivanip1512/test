alter table Invoice add AuthorizedNumber varchar2(60);
update Invoice set AuthorizedNumber = '';
alter table Invoice modify AuthorizedNumber varchar2(60) not null;

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
delete from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
delete from ECToGenericMapping where MappingCategory = 'LMThermostatSchedule' and ItemID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
delete from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
update LMHardwareBase set LMHardwareTypeID = 0 where LMHardwareTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);

/* End YUK-5001 */
   