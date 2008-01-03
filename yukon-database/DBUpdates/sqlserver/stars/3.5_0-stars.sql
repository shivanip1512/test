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
/* End YUK-5001 */